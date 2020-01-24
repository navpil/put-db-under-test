package io.github.navpil.dbtests.ddl;

import io.github.navpil.dbtests.Credentials;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class LiquibaseSetupSchema implements SetupSchema {

    @Override
    public void ddl(Credentials credentials, String location) throws SQLException {
        try(Connection connection = DriverManager.getConnection(credentials.getUrl(), credentials.getUsername(), credentials.getPassword())) {
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));

            //WARNING: The myChangeLog.xml is not the same as in dbmigrationtools/liquibase/,
            // the myChangeLog.xml here can't resolve relative 'sql' path in the includeAll property
            Liquibase liquibase = new liquibase.Liquibase(location, new ClassLoaderResourceAccessor(), database);

            liquibase.update(new Contexts(), new LabelExpression());
        } catch (LiquibaseException e) {
            throw new SQLException(e);
        }
        ;
    }

}
