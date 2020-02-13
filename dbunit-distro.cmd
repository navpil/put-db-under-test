mkdir dbunit\target\dist || goto :ErrorHandling
mkdir dbunit\target\dist\dependency || goto :ErrorHandling
xcopy dbunit\src\main\cmd dbunit\target\dist  || goto :ErrorHandling
copy dbunit\target\dbunithelper.jar dbunit\target\dist  || goto :ErrorHandling
xcopy dbunit\target\dependency dbunit\target\dist\dependency  || goto :ErrorHandling

goto :End
:ErrorHandling
echo Something went wrong
exit /b 1

:End
