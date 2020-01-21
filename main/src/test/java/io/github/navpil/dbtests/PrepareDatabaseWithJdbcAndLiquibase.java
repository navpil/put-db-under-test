package io.github.navpil.dbtests;

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

public class PrepareDatabaseWithJdbcAndLiquibase {

    public static void main(String [] args) throws SQLException, LiquibaseException {

        String dbName = "carrental";

        //Step 1: Create DB (by JDBC)
        recreateDatabase(dbName);

        //Step 2: DDL the DB (By Liquibase)
        String url = getUrl(dbName);
        Connection connection = DriverManager.getConnection(url, "", ""); //your openConnection logic here

        Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));

        //WARNING: The myChangeLog.xml is not the same as in dbmigrationtools/liquibase/,
        // the myChangeLog.xml here can't resolve relative 'sql' path in the includeAll property

        Liquibase liquibase = new liquibase.Liquibase("liquibase/myChangeLog.xml", new ClassLoaderResourceAccessor(), database);

        liquibase.update(new Contexts(), new LabelExpression());

    }

    public static String getUrl(String dbName) {
        return "jdbc:sqlserver://localhost;databaseName=" + dbName + ";integratedSecurity=true;";
    }

    private static void recreateDatabase(String dbName) throws SQLException {
        try(Connection connection = DriverManager.getConnection("jdbc:sqlserver://localhost;integratedSecurity=true;", "", "")) {
            try {
                connection.createStatement()
                        .executeUpdate("DROP DATABASE " + dbName);
            } catch (SQLException e) {
                System.out.println("Cannot drop db" + e);
            }
            connection.createStatement()
                    .executeUpdate("CREATE DATABASE " + dbName);
        }
    }

}
