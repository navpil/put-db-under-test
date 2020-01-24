package io.github.navpil.dbtests.ddl;

import io.github.navpil.dbtests.Credentials;

import java.sql.SQLException;

public interface SetupSchema {

    void ddl(Credentials credentials, String schemaLocation) throws SQLException;
}
