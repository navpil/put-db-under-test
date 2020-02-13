package io.github.navpil.dbtests.dbunit;

public class TestCmdRunner {

    public static void main(String[] args) throws Exception {
        CmdRunner.main(new String[]{"export", "jdbc:sqlserver://localhost;databaseName=carrental;integratedSecurity=true;"});
    }
}
