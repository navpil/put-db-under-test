package io.github.navpil.dbtests;

import org.flywaydb.core.Flyway;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PrepareDatabaseWithJdbcAndFlyway {

    public static void main(String [] args) throws SQLException {

        String dbName = "carrental";

        //Step 1: Create DB (by JDBC)
        recreateDatabase(dbName);

        //Step 2: DDL the DB (By Flyway)
        String url = getUrl(dbName);
        Flyway.configure()
                .dataSource(url, "", "")
                .locations("classpath:flyway")
                .load()
                .migrate();
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
