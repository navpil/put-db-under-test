package io.github.navpil.dbtests;

import org.dbunit.DatabaseUnitException;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.MSSQLServerContainer;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SqlServerDbTest {

    @Rule
    public MSSQLServerContainer mssqlserver = new MSSQLServerContainer();

    @Test
    public void someTestMethod() throws SQLException, DatabaseUnitException, IOException {
        //Put up a test container
        String url = mssqlserver.getJdbcUrl();
        System.out.println(url);
        final String username = mssqlserver.getUsername();
        final String password = mssqlserver.getPassword();

        System.out.println(username + ", " +password);

        Connection connection = DriverManager.getConnection(url, username, password);

        //Create database
        final Statement statement = connection.createStatement();
        statement.executeUpdate("CREATE DATABASE CARRENTAL");

        System.out.println("All went fine");

        //DDL
        new MyBatisWrapper(url+ ";databaseName=carrental", username, password).up();

        connection = DriverManager.getConnection(url + ";databaseName=carrental", username, password);

        //Data setup
        DbUnitHelper.handleSetUpOperation("full_carrental_3_cars.xml", connection);


        //JDBC query
        final ResultSet resultSet = DriverManager.getConnection(url + ";databaseName=carrental", username, password).prepareStatement("SELECT * FROM car").executeQuery();

        System.out.println(resultSet);

        while(resultSet.next()) {
            System.out.println(resultSet.getString(1) + ":" + resultSet.getString(2) + ":" + resultSet.getInt(3));
        }


    }
}
