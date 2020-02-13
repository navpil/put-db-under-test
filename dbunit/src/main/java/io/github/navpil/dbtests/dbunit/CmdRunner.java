package io.github.navpil.dbtests.dbunit;

import java.sql.Connection;
import java.sql.DriverManager;

public class CmdRunner {

    public static void main(String[] args) throws Exception {
        String url = null;
        final String command = args[0];
        if (args.length > 1) {
            url = args[1];
        }
        switch (command) {
            case "export":
            case "backup":
                withHelper(url, "data", helper -> helper.export("data-tables.xml", new String[0]));
                withHelper(url, "dbo", helper -> helper.export("dbo-tables.xml", new String[0]));
                break;
            case "import":
            case "restore":
                withHelper(url, "dbo", helper -> {
                    helper.cleanDb();
                    helper.setUp("dbo-tables.xml");
                });
                withHelper(url, "data", helper -> {
                    helper.cleanDb();
                    helper.setUp("data-tables.xml");
                });
                break;
            case "clean":
                withHelper(url, "dbo", SqlServerDbHelper::cleanDb);
                withHelper(url, "data", SqlServerDbHelper::cleanDb);
                break;
        }
    }

    private static void withHelper(String url, String schema, ExceptionalConsumer<SqlServerDbHelper> consumer) throws Exception {
        try (Connection c = DriverManager.getConnection(url, "", "");
             final SqlServerDbHelper helper = new SqlServerDbHelper(c, schema)) {
            consumer.consume(helper);
        }
    }

    public interface ExceptionalConsumer<T> {

        void consume(T t) throws Exception;
    }

}
