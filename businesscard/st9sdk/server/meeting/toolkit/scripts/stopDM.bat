REM Set some variables
set WAS_PATH=C:\IBM\WebSphere\AppServer
set PROFILE_PATH=%WAS_PATH%\profiles\STMDMgrProfile
echo Using PROFILE_PATH=%PROFILE_PATH%

REM start the DMger
echo stoping the dm ...
call %PROFILE_PATH%\bin\stopServer.bat dmgr -username wasadmin -password wasadmin

echo done.
REM pause



