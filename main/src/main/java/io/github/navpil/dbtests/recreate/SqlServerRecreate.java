package io.github.navpil.dbtests.recreate;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class SqlServerRecreate implements RecreateDB {

    @Override
    public void recreateDb(Connection c, String dbName) throws SQLException {
        try (Statement s = c.createStatement()) {
            s.executeUpdate("DROP DATABASE " + dbName);
        }
        try (Statement s = c.createStatement()) {
            s.executeUpdate("CREATE DATABASE " + dbName);
        }
    }
}
