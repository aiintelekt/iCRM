
SET OFBIZ_HOME=%CD%\..\..\source_artifacts\icrm_base
SET RUNTIME_OFBIZ_HOME=%CD%
echo %OFBIZ_HOME%
echo %RUNTIME_OFBIZ_HOME%

echo 'Cleanup project start'
cd %RUNTIME_OFBIZ_HOME%\hot-deploy
for /d /r %%i in (*) do rmdir /s /q "%%i"
for %%i in (*.*) do if not "%%i" == "build.xml" if not "%%i" == "component-load.xml" del /q "%%i"

cd %RUNTIME_OFBIZ_HOME%\hot-deploy-base
for /d /r %%i in (*) do rmdir /s /q "%%i"
for %%i in (*.*) do if not "%%i" == "build.xml" if not "%%i" == "component-load.xml" del /q "%%i"

cd %RUNTIME_OFBIZ_HOME%
rmdir /s /q applications
rmdir /s /q framework
rmdir /s /q runtime
rmdir /s /q themes
echo 'Cleanup project end'
