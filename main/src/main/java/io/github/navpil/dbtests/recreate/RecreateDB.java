package io.github.navpil.dbtests.recreate;

import io.github.navpil.dbtests.Credentials;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@FunctionalInterface
public interface RecreateDB {

    default void recreateDb(DataSource ds, String dbName) throws SQLException {
        try (final Connection c = ds.getConnection()){
            recreateDb(c, dbName);
        }
    }

    void recreateDb(Connection c, String dbName) throws SQLException;

    default void recreateDb(Credentials credentials, String dbName) throws SQLException {
        try (final Connection c = DriverManager.getConnection(credentials.getUrl(), credentials.getUsername(), credentials.getPassword())) {
            recreateDb(c, dbName);
        }
    }

}
