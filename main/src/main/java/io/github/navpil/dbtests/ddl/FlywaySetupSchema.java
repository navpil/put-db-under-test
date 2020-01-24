package io.github.navpil.dbtests.ddl;

import io.github.navpil.dbtests.Credentials;
import org.flywaydb.core.Flyway;

public class FlywaySetupSchema implements SetupSchema {

    @Override
    public void ddl(Credentials credentials, String location) {
        Flyway.configure()
                .dataSource(credentials.getUrl(), credentials.getUsername(), credentials.getPassword())
                .locations(location)
                .load()
                .migrate();
    }
}
