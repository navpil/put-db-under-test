package io.github.navpil.dbtests.recreate;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class HSQLRecreate implements RecreateDB {

    @Override
    public void recreateDb(Connection c, String dbName) throws SQLException {
        //Db name is ignored, because it is provided in the URL
        try (final Statement statement = c.createStatement()) {
            statement.execute("DROP SCHEMA PUBLIC CASCADE");
            c.commit();
        }
    }
}
