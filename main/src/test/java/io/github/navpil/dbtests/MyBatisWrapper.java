package io.github.navpil.dbtests;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import org.apache.ibatis.migration.ConnectionProvider;
import org.apache.ibatis.migration.DataSourceConnectionProvider;
import org.apache.ibatis.migration.FileMigrationLoader;
import org.apache.ibatis.migration.JdbcConnectionProvider;
import org.apache.ibatis.migration.MigrationException;
import org.apache.ibatis.migration.commands.BaseCommand;
import org.apache.ibatis.migration.operations.UpOperation;
import org.apache.ibatis.migration.options.OptionsParser;
import org.apache.ibatis.migration.options.SelectedOptions;
import org.apache.ibatis.migration.utils.Util;

import java.io.File;
import java.sql.DriverManager;
import java.util.Properties;

public class MyBatisWrapper {


    private final String url;
    private final String username;
    private final String password;

    public MyBatisWrapper(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public void up() {
        final SelectedOptions options = OptionsParser.parse(new String[]{"up", "--path=./src/test/mybatisrepo"});

        /*
        //Also possible to use operations directly, though they are better suited for Java based migrations
        new UpOperation().operate(

                () -> DriverManager.getConnection(url, username, password),
                new FileMigrationLoader(

                        Util.file(new File("./src/test/mybatisrepo"), "./scripts"),
                        "UTF-8",
                        new Properties()
                ), null, null
        );
        */

        final BaseCommand upCommand = new BaseCommand(options) {

            @Override
            public void execute(String... params) {
                int limit = this.getStepCountParameter(2147483647, params);
                UpOperation operation = new UpOperation(limit);
                operation.operate(this.getConnectionProvider(), this.getMigrationLoader(), this.getDatabaseOperationOption(), this.printStream, this.createUpHook());
            }

            @Override
            protected ConnectionProvider getConnectionProvider() {
                try {
                    return () -> DriverManager.getConnection(url, username, password);
                } catch (Exception var2) {
                    throw new MigrationException("Error creating ScriptRunner.  Cause: " + var2, var2);
                }
            }
        };

        upCommand.execute(options.getParams());
    }
}
