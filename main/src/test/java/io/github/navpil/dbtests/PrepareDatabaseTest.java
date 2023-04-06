package io.github.navpil.dbtests;

import io.github.navpil.dbtests.ddl.FlywaySetupSchema;
import io.github.navpil.dbtests.ddl.LiquibaseSetupSchema;
import io.github.navpil.dbtests.ddl.MyBatisSetupSchema;
import io.github.navpil.dbtests.recreate.HSQLRecreate;
import io.github.navpil.dbtests.recreate.SqlServerRecreate;
import org.junit.Ignore;
import org.junit.Test;

import java.sql.SQLException;

@Ignore
public class PrepareDatabaseTest {

    @Test
    public void prepareSqlServerWithJdbcAndFlyway() throws SQLException {
        String dbName = "carrental";

        //Step 1: Create DB (by JDBC)
        final SqlServerRecreate sqlServerRecreate = new SqlServerRecreate();
        sqlServerRecreate.recreateDb(new Credentials(
                "jdbc:sqlserver://localhost;integratedSecurity=true;",
                "",
                ""
        ), dbName);

        //Step 2: DDL the DB (By Flyway)
        new FlywaySetupSchema().ddl(new Credentials(
                SQLConfig.SQL_SERVER.getUrl(dbName),
                SQLConfig.SQL_SERVER.username,
                SQLConfig.SQL_SERVER.password
        ), "classpath:flyway");
    }

    @Test
    public void prepareSqlServerWithJdbcAndLiquibase() throws SQLException {
        final String dbName = "carrental";

        //Step 1: Create DB (by JDBC)
        new SqlServerRecreate().recreateDb(new Credentials(
                "jdbc:sqlserver://localhost;integratedSecurity=true;",
                "",
                ""
        ), dbName);

        //Step 2: DDL the DB (By Liquibase)
        new LiquibaseSetupSchema().ddl(
                new Credentials(SQLConfig.SQL_SERVER.getUrl(dbName),
                        SQLConfig.SQL_SERVER.username,
                        SQLConfig.SQL_SERVER.password),
                "liquibase/myChangeLog.xml"
        );
    }


    @Test
    public void prepareHsqldbWithJdbcAndLiquibase() throws SQLException {
        final SQLConfig hsqldb = SQLConfig.HSQLDB;
        final Credentials credentials = new Credentials(hsqldb.getUrl("file:" + System.getProperty("user.home") + "/temp/carrental"), hsqldb.username, hsqldb.password);

        //Step 1: Create DB
        //Database is created automatically, but we need to reset it with HSQL
        new HSQLRecreate().recreateDb(credentials, null);

        //Step 2: DDL the DB (By Liquibase)
        new LiquibaseSetupSchema().ddl(credentials, "liquibase/myChangeLog.xml");
    }

    @Test
    public void prepareSqlWithMyBatis() throws SQLException {
        final String dbName = "carrental";

        //Step 1: Create DB (by JDBC)
        new SqlServerRecreate().recreateDb(new Credentials(
                "jdbc:sqlserver://localhost;integratedSecurity=true;",
                "",
                ""
        ), dbName);

        new MyBatisSetupSchema().ddl(new Credentials(SQLConfig.SQL_SERVER.getUrl(dbName),
                        SQLConfig.SQL_SERVER.username,
                        SQLConfig.SQL_SERVER.password),
                "./src/test/mybatisrepo"
        );
    }
}
