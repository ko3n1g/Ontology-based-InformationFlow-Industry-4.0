@echo off
setlocal EnableDelayedExpansion

SET THEARGS=%*
REM # Default runtime Stardog arguments providing the default memory settings.
IF NOT DEFINED STARDOG_JAVA_ARGS set STARDOG_JAVA_ARGS=-Xmx2g -Xms2g -XX:MaxDirectMemorySize=1g

CALL %~dp0\helpers.bat init

CALL %~dp0\helpers.bat is_appstart %*

CALL %~dp0\helpers.bat build_classpath admin

CALL %~dp0\helpers.bat is_foreground %*

CALL %~dp0\helpers.bat handle_zk %*

CALL %~dp0\helpers.bat check_log4j_config

CALL %~dp0\helpers.bat check_home %STARDOG_HOME%

SET CMD=%JAVA% %STARDOG_JAVA_ARGS% %STARDOG_PERF_JAVA_ARGS% %DEFAULT_JAVA_ARGS% -Dstardog.install.location="%STARDOG%" -server -classpath "%CLASSPATH%" com.complexible.stardog.cli.admin.CLI %THEARGS%

%CMD%
