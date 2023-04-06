mkdir target\dist
move target\dbunithelper.jar target\dist
move target\dependency target\dist\dependency
copy src\main\cmd\dbunit.cmd target\dist
copy src\main\cmd\dbunit.properties target\dist