@echo off
setlocal EnableDelayedExpansion

CALL %~dp0\helpers.bat init

CALL %~dp0\helpers.bat build_classpath admin

if "%STARDOG_JAVA_ARGS%"=="" set STARDOG_JAVA_ARGS=-Xmx2g -Xms2g

CALL %~dp0\helpers.bat check_log4j_config

SET CMD=%JAVA% %DEFAULT_JAVA_ARGS% %STARDOG_JAVA_ARGS% -Dstardog.install.location=%STARDOG% -client -classpath "%CLASSPATH%" com.complexible.stardog.cli.CLI %*

%CMD%
