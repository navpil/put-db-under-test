set COMMAND=%1
java -cp "dependency/*;dbunithelper.jar" io.github.navpil.dbtests.dbunit.CmdRunner dbunit.properties %COMMAND% data
java -cp "dependency/*;dbunithelper.jar" io.github.navpil.dbtests.dbunit.CmdRunner dbunit.properties %COMMAND% dbo
