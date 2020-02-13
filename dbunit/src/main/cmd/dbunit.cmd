set COMMAND=%1
java -cp "dependency/*;dbunithelper.jar" io.github.navpil.dbtests.dbunit.CmdRunner %COMMAND% jdbc:sqlserver://localhost;databaseName=carrental;integratedSecurity=true;