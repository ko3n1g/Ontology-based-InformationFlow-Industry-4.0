@echo OFF

REM # We pass in the label / command to run
GOTO :%1

:INIT
REM # SET up environment variables
SET JAVA=java

IF EXIST "%JAVA_HOME%\bin\java.exe" SET JAVA="%JAVA_HOME%\bin\java"
REM # Path delimiter here is always ; because bat == windows
SET PATHDELIM=;

REM # If not SET, SET %STARDOG% to the parent of the folder in which this script is stored
REM SET SOURCE=%~n0
pushd %~dp0
pushd ..
IF NOT DEFINED STARDOG SET STARDOG=%CD%
popd
popd

REM # The "default" java arguments for Stardog.  These should not be edited.  For providing custom arguments to the JVM,
REM # STARDOG_JAVA_ARGS should be used.
SET DEFAULT_JAVA_ARGS=-Djavax.xml.datatype.DatatypeFactory=org.apache.xerces.jaxp.datatype.DatatypeFactoryImpl -Dapple.awt.UIElement=true -Dfile.encoding=UTF-8

REM # Performance related java arguments for things like GC tuning.  Default values are reasonable defaults for the system
REM # across platforms, but these can be tweaked for your current environment.
IF NOT DEFINED STARDOG_PERF_JAVA_ARGS (
    SET STARDOG_PERF_JAVA_ARGS=-XX:SoftRefLRUPolicyMSPerMB=1 -XX:+UseParallelOldGC -XX:+UseCompressedOops
)

REM # SET STARDOG_HOME, either from command or %STARDOG% above
SET FOUND=false
FOR %%A IN (%*) DO (
    IF /I "%%A"=="--home" (
        SET FOUND=true
    ) ELSE (
        IF "%FOUND%"=="true" (
            SET STARDOG_HOME=%%A
            GOTO :BREAK0
        )
    )
)

SET sh=-Dstardog.home

:BREAK0
IF "%FOUND%"=="false" (
    IF NOT "%STARDOG_JAVA_ARGS%"=="" (

        FOR %%A IN (%STARDOG_JAVA_ARGS%) DO (
            REM # Same as above because batch treats = as a delimiter
            IF "%%A"=="%sh%" (
                SET FOUND=true
            ) ELSE (
                IF "%FOUND%"=="true" (
                    SET STARDOG_HOME=%%A
                    GOTO :BREAK1
                )
            )
        )
    )
)
:BREAK1
IF "%FOUND%"=="false" (
    IF NOT DEFINED STARDOG_HOME SET STARDOG_HOME=%STARDOG%
)

SET STARDOG_JAVA_ARGS=-Dstardog.home=%STARDOG_HOME% %STARDOG_JAVA_ARGS%

EXIT /B 0

REM # Helper functions (from helpers.sh)
:build_classpath

SET dirs=\client\ext\ ^
         \client\api\ ^
         \client\cli\ ^
         \client\http\ ^
         \client\snarl\ ^
         \client\pack\

IF "%2"=="admin" (
    SET is_admin=true
    SET dirs=%dirs% ^
    \server\ext\ ^
    \server\dbms\ ^
    \server\http\ ^
    \server\snarl\ ^
    \server\pack\
)
SET CLASSPATH=
FOR %%D IN (%dirs%) DO (
    IF NOT DEFINED CLASSPATH (
        SET CLASSPATH=%STARDOG%%%D*
    ) ELSE (
        SET CLASSPATH=!CLASSPATH!%PATHDELIM%%STARDOG%%%D*
    )
)
REM # helpers.sh only does this when admin, but stardog.bat was doing it
REM # Always do it just to be safe
FOR %%F IN ("%HOMEDIR%\server\dbms\slf4j*jar") DO SET SLF4J_JARS=!SLF4J_JARS!;%%F
SET CLASSPATH=!CLASSPATH!%PATHDELIM%!SLF4J_JARS!

IF DEFINED APP_START (
    SET CLASSPATH=!CLASSPATH!%PATHDELIM%!STARDOG_EXT!
)

EXIT /B 0

:signal_handler_first_stage
IF NOT DEFINED HANDLER_EXECUTED (
    SET HANDLER_EXECUTED=1
    TASKKILL /PID %PID%
)
EXIT /B 0

:process_exists
wmic process where "ProcessID = %2" get processid 2> pexists.err > NUL
IF EXIST "pexists.err" (
    DEL pexists.err
    EXIT /B 1
)
EXIT /B 0

:is_foreground
SET FOREGROUND=
IF NOT DEFINED APP_START EXIT /B 0
FOR %%A IN (%*) DO (
    IF /I "%%A"=="--foreground" SET FOREGROUND=true
)
EXIT /B 0

:is_appstart
SET APP_START=
IF /I "%2"=="server" IF /I "%3"=="start" SET APP_START=true
IF /I "%2"=="cluster" IF /I "%3"=="zkstart" SET APP_START=true
EXIT /B 0

:handle_zk
SET IS_ZK=
IF /I "%2"=="cluster" (
    IF /I "%3"=="zkstart" (
        IF NOT DEFINED SD_ZOO_JAVA_ARGS (
            SET SD_ZOO_JAVA_ARGS=-Xmx1g -Xms1g -XX:MaxDirectMemorySize=128m -Dzookeeper.jmx.log4j.disable=true
        )
        SET STARDOG_JAVA_ARGS=%SD_ZOO_JAVA_ARGS%
        SET IS_ZK=true
    )
    IF /I "%3"=="zkstop" (
        REM # Why would jps be in a global path?
        where java | findstr /R /C:"jdk" > sdtemp
        SET /P JDKLOC=<sdtemp
        CALL :_shorten "%JDKLOC%"
        REM # Remove '\bin\java.exe'
        SET JDKLOC=%SHORTEN:~0,-12%

        REM # Alright, we know where jps lives now!
        %SHORTENED%\jps | FINDSTR /R /C:"zkstart" > sdtemp
        SET /P JPSPID=<sdtemp
        REM # Remove ' zkstart'
        SET JPSPID=%JPSPID:~0,-8%
        TASKKILL /PID %JPSPID%
        SET STARDOG_ARGS=%STARDOG_ARGS% --is-successful %ERRORLEVEL%
    )
)
EXIT /B 0

:_shorten
SET SHORTENED=%~s1
EXIT /B 0

:check_log4j_config
SET log4j_location=
IF DEFINED STARDOG_HOME (
    FOR %%E IN (yaml yml json jsn xml) DO (
        IF EXIST "%STARDOG_HOME%\log4j2.%%E" SET log4j_location=%STARDOG_HOME%\log4j2.%%E
    )
)
IF NOT DEFINED log4j_location SET log4j_location=%STARDOG%\server\dbms\log4j2.xml
SET DEFAULT_JAVA_ARGS=%DEFAULT_JAVA_ARGS% -Dlog4j.configurationFile=%log4j_location%
EXIT /B 0

:check_home
IF NOT EXIST "%2" (
    echo STARDOG_HOME directory "%2" does not exist
    EXIT /B 1
)
IF NOT EXIST "%2\*" (
    echo STARDOG_HOME "%2" is not a directory
    EXIT /B 1
)

REM # *nix has us check permissions on the dir, but windows
REM # really doesn't want that to be easy

EXIT /B 0
