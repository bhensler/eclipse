echo off
echo %TIME%
set WAS_PATH=C:\IBM\WebSphere\AppServer
set PROFILE_PATH=%WAS_PATH%\profiles\STMAppProfile
call %PROFILE_PATH%\bin\stopServer.bat STMeetingServer -username wasadmin -password wasadmin
call %PROFILE_PATH%\bin\startServer.bat STMeetingServer -username wasadmin -password wasadmin
echo %TIME%