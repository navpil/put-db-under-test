package io.github.navpil.dbtests;

import org.apache.ibatis.migration.ConnectionProvider;
import org.apache.ibatis.migration.MigrationException;
import org.apache.ibatis.migration.commands.BaseCommand;
import org.apache.ibatis.migration.operations.UpOperation;
import org.apache.ibatis.migration.options.OptionsParser;
import org.apache.ibatis.migration.options.SelectedOptions;

import java.sql.DriverManager;

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
