@REM ***************************************************************** 
@REM                                                                   
@REM Licensed Materials - Property of IBM                              
@REM                                                                   
@REM 5724-J23                                                          
@REM                                                                   
@REM Copyright IBM Corp. 2014  All Rights Reserved.                    
@REM                                                                   
@REM US Government Users Restricted Rights - Use, duplication or       
@REM disclosure restricted by GSA ADP Schedule Contract with           
@REM IBM Corp.                                                         
@REM                                                                   
@REM ***************************************************************** 

@REM Set STJAVATK_JAR variable according to the location of the stcommsrvrtk.jar.   
@REM A copy of the jar can be found in the SDK package in the server/commserver/bin directory and copied into this directory.
set STJAVATK_JAR=stcommsrvrtk.jar
set CP="orgtree.svc.impl.jar;orgtree.svc.api.jar;%STJAVATK_JAR%"
@REM set JAVA_HOME to java6 JRE
@REM set JAVA_HOME=C:\java\ibm-java-sdk-60-win-i386\bin
%JAVA_HOME%\java -classpath %CP% com.ibm.collaboration.realtime.orgtree.OrgTreeServerApp
pause
