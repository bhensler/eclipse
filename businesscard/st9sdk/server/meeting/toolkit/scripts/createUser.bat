set websphereInstallBase="C:\IBM\WebSphere"
set username=wasadmin
set password=wasadmin

echo creating user %1
call %websphereInstallBase%\AppServer\bin\wsadmin.bat -username %username% -password %password% -f createUser.py %1
if NOT %ERRORLEVEL% == 0 (
	echo Problem creating user(s), exiting ...
	exit /B 10
)