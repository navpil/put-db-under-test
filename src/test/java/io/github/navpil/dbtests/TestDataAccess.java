package io.github.navpil.dbtests;

import io.github.navpil.dbtests.access.JdbcDao;
import io.github.navpil.dbtests.access.JdbcTemplateDao;
import io.github.navpil.dbtests.access.HibernateNativeDao;
import io.github.navpil.dbtests.access.HibernateJpaDao;
import io.github.navpil.dbtests.access.MyBatisDao;
import io.github.navpil.dbtests.access.QueryDSLDao;
import org.junit.Test;

import java.sql.SQLException;

public class TestDataAccess {

    @Test
    public void testUsingJdbc() throws SQLException {
        final JdbcDao jdbcTemplateDao = new JdbcDao(
                "jdbc:sqlserver://localhost;databaseName=carrental;integratedSecurity=true;",
                "",
                ""
        );
        System.out.println(jdbcTemplateDao.getCars());
    }

    @Test
    public void testUsingJdbcTemplate() {
        final JdbcTemplateDao jdbcTemplateDao = new JdbcTemplateDao(
                "jdbc:sqlserver://localhost;databaseName=carrental;integratedSecurity=true;",
                "",
                ""
        );
        System.out.println(jdbcTemplateDao.getCars());
    }

    @Test
    public void testUsingHibernate() {
        final HibernateNativeDao hibernateBased = new HibernateNativeDao(
                "jdbc:sqlserver://localhost;databaseName=carrental;integratedSecurity=true;",
                "",
                ""
        );
        System.out.println(hibernateBased.getCars());
    }

    @Test
    public void testUsingJpaNative() {
        final HibernateJpaDao hibernateBased = new HibernateJpaDao(
                "jdbc:sqlserver://localhost;databaseName=carrental;integratedSecurity=true;",
                "",
                ""
        );
        System.out.println(hibernateBased.getNativeQueryCars());
        System.out.println(hibernateBased.getJpqlCars());
        System.out.println(hibernateBased.getCriteriaCars());
    }

    @Test
    public void testUsingMyBatis() {
        final MyBatisDao dao = new MyBatisDao(
                "jdbc:sqlserver://localhost;databaseName=carrental;integratedSecurity=true;",
                "",
                ""
        );
        System.out.println(dao.getCars());
    }

    @Test
    public void testUsingQueryDsl() {
        final QueryDSLDao dao = new QueryDSLDao(
                "jdbc:sqlserver://localhost;databaseName=carrental;integratedSecurity=true;",
                "",
                ""
        );
        System.out.println(dao.getCars());
    }
}
