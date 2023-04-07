package io.github.navpil.dbtests;

import io.github.navpil.dbtests.access.JdbcDao;
import io.github.navpil.dbtests.access.JdbcTemplateDao;
import io.github.navpil.dbtests.access.HibernateNativeDao;
import io.github.navpil.dbtests.access.HibernateJpaDao;
import io.github.navpil.dbtests.access.MyBatisDao;
import io.github.navpil.dbtests.access.QueryDSLDao;
import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;

import java.sql.SQLException;

@Ignore
public class DataAccessTest {

    private static final Logger LOG = Logger.getLogger(DataAccessTest.class);
    
    private SQLConfig config = SQLConfig.HSQLDB;

    @Test
    public void testUsingJdbc() throws SQLException {
        final JdbcDao dao = create(JdbcDao::new);
        LOG.info(dao.list());
    }

    @Test
    public void testUsingJdbcTemplate() {
        final JdbcTemplateDao dao = create(JdbcTemplateDao::new);
        LOG.info(dao.getCars());
    }

    @Test
    public void testUsingHibernate() {
        final HibernateNativeDao dao = create(HibernateNativeDao::new);
        LOG.info(dao.getCars());
    }

    @Test
    public void testUsingJpa() {
        final HibernateJpaDao dao = create(HibernateJpaDao::new);
        LOG.info(dao.getNativeQueryCars());
        LOG.info(dao.getJpqlCars());
        LOG.info(dao.getCriteriaCars());
    }

    @Test
    public void testUsingMyBatis() {
        final MyBatisDao dao = create(MyBatisDao::new);
        LOG.info(dao.getCars());
    }

    @Test
    public void testUsingQueryDsl() {
        final QueryDSLDao dao = create(QueryDSLDao::new);
        LOG.info(dao.getCars());
    }

    public <Dao> Dao create(DaoConstructor<Dao> constructor) {
        return constructor.create(config.getUrl("file:" + System.getProperty("user.home") + "/temp/carrental"), config.username, config.password);
    }

    @FunctionalInterface
    public interface DaoConstructor<Dao> {
        Dao create(String url, String username, String password);
    }

}
