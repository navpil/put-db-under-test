package io.github.navpil.dbtests.flywayjavamigrations;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

public class V3__ChangeCarNames extends BaseJavaMigration {

    public void migrate(Context context) throws Exception {
        context.getConnection().createStatement().executeUpdate("UPDATE car SET brand = 'J' + brand");
    }
}
