package io.github.navpil.dbtests.access;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

public class JooqDao {
    private final String url;
    private final String username;
    private final String password;

    public JooqDao(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public List<Car> getCars() throws SQLException {
        final ArrayList<Car> cars = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            DSLContext create = DSL.using(conn, SQLDialect.HSQLDB);
            Result<Record> result = create.select().from(table("car")).fetch();

            for (Record r : result) {
                String id = r.getValue(field("ID"), String.class);
                String brand = r.getValue(field("BRAND"), String.class);
                Integer maxSpeed = r.getValue(field("MAX_SPEED"), Integer.class);
                cars.add(new Car(id, brand, maxSpeed));
            }
        }
        return cars;
    }

    /**
     * This code uses jOOQ auto-generated classes.
     * jOOQ generates classes directly from Database.
     * So to have classes and to compile we first need database.
     * But to have the database we first need to run test.
     * What to do?
     * 1. Run the PrepareDatabaseTest#prepareHsqldbWithJdbcAndLiquibase()
     * 2. Populate the HSQLDB with data using ImportExportDataWithDbUnitTest#setupData3Cars()
     * 3. mvn package (to generate the jOOQ classes)
     * 4. Uncomment code JooqDao#listCarsWithGeneratedJooq and remove the exception thrown
     * 5. Run this method
     * @throws SQLException
     */
    public List<Car> listCarsWithGeneratedJooq() throws SQLException {
        throw new ReadJavaDocBeforeRunningTheTestException();
//        final ArrayList<Car> cars = new ArrayList<>();
//
//        try (Connection conn = DriverManager.getConnection(url, username, password)) {
//            DSLContext create = DSL.using(conn, SQLDialect.HSQLDB);
//            Result<Record> result = create.select().from(org.jooq.codegen.navpil.tables.Car.CAR).fetch();
//
//            for (Record r : result) {
//                String id = r.getValue(org.jooq.codegen.navpil.tables.Car.CAR.ID);
//                String brand = r.getValue(org.jooq.codegen.navpil.tables.Car.CAR.BRAND);
//                Integer maxSpeed = r.getValue(org.jooq.codegen.navpil.tables.Car.CAR.MAX_SPEED);
//                cars.add(new Car(id, brand, maxSpeed));
//            }
//        }
//        return cars;
    }

    public static class ReadJavaDocBeforeRunningTheTestException extends RuntimeException {
        public ReadJavaDocBeforeRunningTheTestException() {
            super("Read javadocs for the test before running the test. Most likely some code manipulation is needed");
        }
    }
}
