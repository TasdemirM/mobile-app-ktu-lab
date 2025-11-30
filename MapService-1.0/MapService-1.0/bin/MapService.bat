@rem
@rem Copyright 2015 the original author or authors.
@rem
@rem Licensed under the Apache License, Version 2.0 (the "License");
@rem you may not use this file except in compliance with the License.
@rem You may obtain a copy of the License at
@rem
@rem      https://www.apache.org/licenses/LICENSE-2.0
@rem
@rem Unless required by applicable law or agreed to in writing, software
@rem distributed under the License is distributed on an "AS IS" BASIS,
@rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@rem See the License for the specific language governing permissions and
@rem limitations under the License.
@rem
@rem SPDX-License-Identifier: Apache-2.0
@rem

@if "%DEBUG%"=="" @echo off
@rem ##########################################################################
@rem
@rem  MapService startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%"=="" set DIRNAME=.
@rem This is normally unused
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Resolve any "." and ".." in APP_HOME to make it shorter.
for %%i in ("%APP_HOME%") do set APP_HOME=%%~fi

@rem Add default JVM options here. You can also use JAVA_OPTS and MAP_SERVICE_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if %ERRORLEVEL% equ 0 goto execute

echo. 1>&2
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH. 1>&2
echo. 1>&2
echo Please set the JAVA_HOME variable in your environment to match the 1>&2
echo location of your Java installation. 1>&2

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto execute

echo. 1>&2
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME% 1>&2
echo. 1>&2
echo Please set the JAVA_HOME variable in your environment to match the 1>&2
echo location of your Java installation. 1>&2

goto fail

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\MapService-1.0.jar;%APP_HOME%\lib\ktorm-core-4.1.1.jar;%APP_HOME%\lib\http4k-config-6.17.0.0.jar;%APP_HOME%\lib\http4k-format-moshi-6.17.0.0.jar;%APP_HOME%\lib\http4k-format-jackson-yaml-6.17.0.0.jar;%APP_HOME%\lib\http4k-api-jsonschema-6.17.0.0.jar;%APP_HOME%\lib\http4k-format-jackson-6.17.0.0.jar;%APP_HOME%\lib\http4k-format-kondor-json-6.17.0.0.jar;%APP_HOME%\lib\http4k-format-core-6.17.0.0.jar;%APP_HOME%\lib\http4k-realtime-core-6.17.0.0.jar;%APP_HOME%\lib\http4k-security-core-6.17.0.0.jar;%APP_HOME%\lib\http4k-core-6.17.0.0.jar;%APP_HOME%\lib\result4k-2.22.4.0.jar;%APP_HOME%\lib\data4k-2.22.4.0.jar;%APP_HOME%\lib\values4k-2.22.4.0.jar;%APP_HOME%\lib\moshi-kotlin-1.15.2.jar;%APP_HOME%\lib\jackson-dataformat-yaml-2.20.0.jar;%APP_HOME%\lib\cloudevents-json-jackson-4.0.1.jar;%APP_HOME%\lib\jackson-databind-2.20.0.jar;%APP_HOME%\lib\jackson-core-2.20.0.jar;%APP_HOME%\lib\jackson-module-kotlin-2.20.0.jar;%APP_HOME%\lib\kotlin-reflect-2.2.10.jar;%APP_HOME%\lib\moshi-1.15.2.jar;%APP_HOME%\lib\okio-jvm-3.7.0.jar;%APP_HOME%\lib\kondor-core-3.5.2.jar;%APP_HOME%\lib\kondor-outcome-3.5.2.jar;%APP_HOME%\lib\kotlin-stdlib-jdk8-2.1.0.jar;%APP_HOME%\lib\kotlin-stdlib-jdk7-2.1.0.jar;%APP_HOME%\lib\kotlin-stdlib-2.2.20.jar;%APP_HOME%\lib\mariadb-java-client-3.3.3.jar;%APP_HOME%\lib\annotations-13.0.jar;%APP_HOME%\lib\waffle-jna-3.3.0.jar;%APP_HOME%\lib\jna-platform-5.13.0.jar;%APP_HOME%\lib\jna-5.13.0.jar;%APP_HOME%\lib\jcl-over-slf4j-2.0.7.jar;%APP_HOME%\lib\slf4j-api-2.0.7.jar;%APP_HOME%\lib\caffeine-2.9.3.jar;%APP_HOME%\lib\checker-qual-3.32.0.jar;%APP_HOME%\lib\error_prone_annotations-2.10.0.jar;%APP_HOME%\lib\snakeyaml-2.4.jar;%APP_HOME%\lib\cloudevents-core-4.0.1.jar;%APP_HOME%\lib\jackson-annotations-2.20.jar;%APP_HOME%\lib\cloudevents-api-4.0.1.jar


@rem Execute MapService
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %MAP_SERVICE_OPTS%  -classpath "%CLASSPATH%" edu.ktu.mis.MapServerKt %*

:end
@rem End local scope for the variables with windows NT shell
if %ERRORLEVEL% equ 0 goto mainEnd

:fail
rem Set variable MAP_SERVICE_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
set EXIT_CODE=%ERRORLEVEL%
if %EXIT_CODE% equ 0 set EXIT_CODE=1
if not ""=="%MAP_SERVICE_EXIT_CONSOLE%" exit %EXIT_CODE%
exit /b %EXIT_CODE%

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
