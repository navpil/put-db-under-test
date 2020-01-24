package io.github.navpil.dbtests.services;

import io.github.navpil.dbtests.Credentials;
import io.github.navpil.dbtests.DbUnitHelper;
import io.github.navpil.dbtests.SQLConfig;
import io.github.navpil.dbtests.access.Car;
import io.github.navpil.dbtests.access.JdbcDao;
import org.assertj.core.api.Assertions;
import org.dbunit.DatabaseUnitException;
import org.easymock.EasyMock;
import org.junit.Ignore;
import org.junit.Test;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Locale;
import java.util.UUID;

@Ignore
public class CarServiceTest {

    @Test
    public void testCarsAreSavedUsingMock() throws SQLException {
        /*
        Mocking dao is OK if we don't quite care what it returns,
        or we specifically want to check how the CarService reacts on errors or edge cases.
        But in this case we miss all the real errors.
        Moreover we assume the internal implementation. For this simple case it is unlikely that
        Service will call other dao methods. But what if Service logic was a bit more complicated
        and called 5 methods, and later on some developer refactored how service interacts with
        dao. Then the test will of no use, since it doesn't catch the refactoring errors.
        */
        final Car car = new Car("my-fancy-id", null, 0);

        final IJdbcDao daoMock = EasyMock.mock(IJdbcDao.class);
        daoMock.save(car);
        EasyMock.expectLastCall();
        EasyMock.expect(daoMock.list()).andReturn(Collections.singletonList(car));
        EasyMock.replay(daoMock);

        final CarService carService = new CarService(daoMock);
        carService.save(car);
        Assertions.assertThat(carService.list()).containsExactly(car);

        EasyMock.verify(daoMock);
        //Just now we tested the EasyMock framework and confirmed that we know how to use it
    }

    @Test
    public void testCarsAreSavedUsingRealDao() throws SQLException, DatabaseUnitException {
        final SQLConfig sqlServer = SQLConfig.SQL_SERVER;
        final Credentials creds = new Credentials(sqlServer.getUrl("carrental"), sqlServer.username, sqlServer.password);
        DbUnitHelper.cleanupData(DriverManager.getConnection(creds.getUrl(), creds.getUsername(), creds.getPassword()));
        //Three problems: id should be uniqueidentifier, brand can't be null, car does not implement equals and hashCode
//        final Car car = new Car("some-brand-id", null, 0);
        final Car car = new Car(UUID.randomUUID().toString().toUpperCase(Locale.ROOT), "Skoda", 0);


        final CarService carService = new CarService(new JdbcDao(creds.getUrl(), creds.getUsername(), creds.getPassword()));
        carService.save(car);
        Assertions.assertThat(carService.list()).containsExactly(car);
    }
}
