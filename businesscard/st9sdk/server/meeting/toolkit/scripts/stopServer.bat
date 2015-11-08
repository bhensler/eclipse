REM Set some variables
set WAS_PATH="C:\IBM\WebSphere\AppServer"
set PROFILE_PATH=%WAS_PATH%\profiles\STMAppProfile
echo Using PROFILE_PATH=%PROFILE_PATH%
REM stop the server
echo stopping the server...
call %PROFILE_PATH%\bin\stopServer.bat STMeetingServer -username wasadmin -password wasadmin
REM call %PROFILE_PATH%\bin\stopServer.bat STMeetingHttpProxy -username wasadmin -password wasadmin
call %PROFILE_PATH%\bin\stopServer.bat nodeagent -username wasadmin -password wasadmin
echo done.
REM pause