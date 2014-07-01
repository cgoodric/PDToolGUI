@echo off
SETLOCAL
:
: runs the PDToolGUI jetty instance
:
: (c) 2014 Cisco and/or its affiliates. All rights reserved.
:
set APPS_INSTALL_DIR="C:\PDTool62"

call %APPS_INSTALL_DIR%\bin\setVars.bat

"%JAVA_HOME%\bin\java" -Dapps.install.dir=%APPS_INSTALL_DIR% -jar %APPS_INSTALL_DIR%\gui\target\PDToolGUI-MAVEN_VERSION.jar server %APPS_INSTALL_DIR%\gui\PDToolGUI.yml
ENDLOCAL

