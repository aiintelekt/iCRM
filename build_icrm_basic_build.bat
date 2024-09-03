
SET OFBIZ_HOME=%CD%\..\..\source_artifacts\icrm_base
SET RUNTIME_OFBIZ_HOME=%CD%
SET GIT_BASE_BRANCH=icrm_base_release_10.1.2_200824
SET GIT_B2B_BRANCH=icrm_b2b_release_10.1.2_200824
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

echo 'update from git start'
cd %OFBIZ_HOME%
git pull origin %GIT_BASE_BRANCH%
cd %OFBIZ_HOME%\hot-deploy
git pull origin %GIT_B2B_BRANCH%
cd %RUNTIME_OFBIZ_HOME%
echo 'update from git end'

echo 'copy applications'
xcopy %OFBIZ_HOME%\applications %RUNTIME_OFBIZ_HOME%\applications /s /y /e /i /q

echo 'copy framework'
xcopy %OFBIZ_HOME%\framework %RUNTIME_OFBIZ_HOME%\framework /s /y /e /i /q

echo 'copy runtime'
xcopy %OFBIZ_HOME%\runtime %RUNTIME_OFBIZ_HOME%\runtime /s /y /e /i /q

echo 'copy themes'
xcopy %OFBIZ_HOME%\themes\bootstrap %RUNTIME_OFBIZ_HOME%\themes\bootstrap /s /y /e /i /q
xcopy %OFBIZ_HOME%\themes\metronic %RUNTIME_OFBIZ_HOME%\themes\metronic /s /y /e /i /q
xcopy %OFBIZ_HOME%\themes\omstheme %RUNTIME_OFBIZ_HOME%\themes\omstheme /s /y /e /i /q
xcopy %OFBIZ_HOME%\themes\tomahawk %RUNTIME_OFBIZ_HOME%\themes\tomahawk /s /y /e /i /q

echo 'copy hot-deploy'
xcopy %OFBIZ_HOME%\hot-deploy\account-portal %RUNTIME_OFBIZ_HOME%\hot-deploy\account-portal /s /y /e /i /q
xcopy %OFBIZ_HOME%\hot-deploy\account-service %RUNTIME_OFBIZ_HOME%\hot-deploy\account-service /s /y /e /i /q
xcopy %OFBIZ_HOME%\hot-deploy\activity-portal %RUNTIME_OFBIZ_HOME%\hot-deploy\activity-portal /s /y /e /i /q
xcopy %OFBIZ_HOME%\hot-deploy\contact-portal %RUNTIME_OFBIZ_HOME%\hot-deploy\contact-portal /s /y /e /i /q
xcopy %OFBIZ_HOME%\hot-deploy\crm-portal %RUNTIME_OFBIZ_HOME%\hot-deploy\crm-portal /s /y /e /i /q
xcopy %OFBIZ_HOME%\hot-deploy\crm-service %RUNTIME_OFBIZ_HOME%\hot-deploy\crm-service /s /y /e /i /q
xcopy %OFBIZ_HOME%\hot-deploy\customer-portal %RUNTIME_OFBIZ_HOME%\hot-deploy\customer-portal /s /y /e /i /q
xcopy %OFBIZ_HOME%\hot-deploy\customer-service %RUNTIME_OFBIZ_HOME%\hot-deploy\customer-service /s /y /e /i /q
xcopy %OFBIZ_HOME%\hot-deploy\lead-portal %RUNTIME_OFBIZ_HOME%\hot-deploy\lead-portal /s /y /e /i /q
xcopy %OFBIZ_HOME%\hot-deploy\lead-service %RUNTIME_OFBIZ_HOME%\hot-deploy\lead-service /s /y /e /i /q
xcopy %OFBIZ_HOME%\hot-deploy\sales-portal %RUNTIME_OFBIZ_HOME%\hot-deploy\sales-portal /s /y /e /i /q
xcopy %OFBIZ_HOME%\hot-deploy\ticket-portal %RUNTIME_OFBIZ_HOME%\hot-deploy\ticket-portal /s /y /e /i /q
xcopy %OFBIZ_HOME%\hot-deploy\activity-portal %RUNTIME_OFBIZ_HOME%\hot-deploy\activity-portal /s /y /e /i /q
xcopy %OFBIZ_HOME%\hot-deploy\sr-portal %RUNTIME_OFBIZ_HOME%\hot-deploy\sr-portal /s /y /e /i /q

echo 'copy hot-deploy-base'
xcopy %OFBIZ_HOME%\hot-deploy-base\ab-ag-grid-support %RUNTIME_OFBIZ_HOME%\hot-deploy-base\ab-ag-grid-support /s /y /e /i /q
xcopy %OFBIZ_HOME%\hot-deploy-base\admin-portal %RUNTIME_OFBIZ_HOME%\hot-deploy-base\admin-portal /s /y /e /i /q
xcopy %OFBIZ_HOME%\hot-deploy-base\appbar-portal %RUNTIME_OFBIZ_HOME%\hot-deploy-base\appbar-portal /s /y /e /i /q
xcopy %OFBIZ_HOME%\hot-deploy-base\common-portal %RUNTIME_OFBIZ_HOME%\hot-deploy-base\common-portal /s /y /e /i /q
xcopy %OFBIZ_HOME%\hot-deploy-base\dyna-screen %RUNTIME_OFBIZ_HOME%\hot-deploy-base\dyna-screen /s /y /e /i /q
xcopy %OFBIZ_HOME%\hot-deploy-base\Etl-Process %RUNTIME_OFBIZ_HOME%\hot-deploy-base\Etl-Process /s /y /e /i /q
xcopy %OFBIZ_HOME%\hot-deploy-base\fio-dataimport %RUNTIME_OFBIZ_HOME%\hot-deploy-base\fio-dataimport /s /y /e /i /q
xcopy %OFBIZ_HOME%\hot-deploy-base\homeapps %RUNTIME_OFBIZ_HOME%\hot-deploy-base\homeapps /s /y /e /i /q
xcopy %OFBIZ_HOME%\hot-deploy-base\ofbiz-ag-grid %RUNTIME_OFBIZ_HOME%\hot-deploy-base\ofbiz-ag-grid /s /y /e /i /q
xcopy %OFBIZ_HOME%\hot-deploy-base\custom-field %RUNTIME_OFBIZ_HOME%\hot-deploy-base\custom-field /s /y /e /i /q
xcopy %OFBIZ_HOME%\hot-deploy-base\customfield-service %RUNTIME_OFBIZ_HOME%\hot-deploy-base\customfield-service /s /y /e /i /q

echo 'copy support-files'
xcopy %RUNTIME_OFBIZ_HOME%\support-files\ofbiz-component.xml %RUNTIME_OFBIZ_HOME%\framework\catalina\ofbiz-component.xml /s /y /e /i /q
xcopy %RUNTIME_OFBIZ_HOME%\support-files\entityengine.xml %RUNTIME_OFBIZ_HOME%\framework\entity\config\entityengine.xml /s /y /e /i /q
xcopy %RUNTIME_OFBIZ_HOME%\support-files\start.properties %RUNTIME_OFBIZ_HOME%\framework\start\src\org\ofbiz\base\start\start.properties /s /y /e /i /q
xcopy %RUNTIME_OFBIZ_HOME%\support-files\general.properties %RUNTIME_OFBIZ_HOME%\framework\common\config\general.properties /s /y /e /i /q

echo 'basic build end'
