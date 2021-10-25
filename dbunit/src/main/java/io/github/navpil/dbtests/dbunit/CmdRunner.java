package io.github.navpil.dbtests.dbunit;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class CmdRunner {

    public static void main(String[] args) throws Exception {
        Path path = Paths.get(".").resolve(args[0]);
        Properties properties = new Properties();
        properties.load(Files.newBufferedReader(path));

        final Credentials credentials = new Credentials(
                properties.getProperty("db.url"),
                properties.getProperty("db.username"),
                properties.getProperty("db.password")
        );

        final String command = args[1];
        final String schema = args[2];

        runCommand(credentials, command, schema);
    }

    public static void runCommand(Credentials credentials, String command, String schema) throws Exception {
        switch (command) {
            case "export":
            case "backup":
                withHelper(credentials, schema, helper -> helper.export(schema + "-tables.xml", new String[0]));
                break;
            case "import":
            case "restore":
                withHelper(credentials, schema, helper -> helper.setUp(schema + "-tables.xml"));
                break;
            case "clean":
                withHelper(credentials, schema, SqlServerDbHelper::cleanDb);
                break;
        }
    }

    private static void withHelper(Credentials credentials, String schema, ExceptionalConsumer<SqlServerDbHelper> consumer) throws Exception {
        try (Connection c = DriverManager.getConnection(credentials.getUrl(), credentials.getUsername(), credentials.getPassword());
             final SqlServerDbHelper helper = new SqlServerDbHelper(c, schema)) {
            consumer.consume(helper);
        }
    }

    public interface ExceptionalConsumer<T> {

        void consume(T t) throws Exception;
    }

}
