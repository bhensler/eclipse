set websphereInstallBase="C:\IBM\WebSphere"
set username=wasadmin
set password=wasadmin

echo modifying login properties
call %websphereInstallBase%\AppServer\bin\wsadmin.bat -username %username% -password %password% -f modifyLoginProperties.py %username%
if NOT %ERRORLEVEL% == 0 (
	echo Problem modifying login properties, exiting ...
	exit /B 10
)
call restartServer.bat