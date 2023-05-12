
SET OFBIZ_HOME=D:\work\Eclipse_FIO_Workspace\FIO_IUC_ICRM_Project\icrmiuc-icrm-base-repo
SET BASIC_OFBIZ_HOME=D:\work\Eclipse_FIO_Workspace\FIO_IUC_ICRM_Project\aws-freemium-repo
echo %OFBIZ_HOME%

echo 'Cleanup project start'
cd %BASIC_OFBIZ_HOME%\hot-deploy
for /d /r %%i in (*) do rmdir /s /q "%%i"
for %%i in (*.*) do if not "%%i" == "build.xml" if not "%%i" == "component-load.xml" del /q "%%i"

cd %BASIC_OFBIZ_HOME%\hot-deploy-base
for /d /r %%i in (*) do rmdir /s /q "%%i"
for %%i in (*.*) do if not "%%i" == "build.xml" if not "%%i" == "component-load.xml" del /q "%%i"

cd %BASIC_OFBIZ_HOME%
rmdir /s /q applications
rmdir /s /q framework
rmdir /s /q runtime
rmdir /s /q themes
echo 'Cleanup project end'

echo 'update from git start'
cd %OFBIZ_HOME%
git pull origin dev_release_18032022
cd %OFBIZ_HOME%\hot-deploy
git pull origin dev_release_18032022
cd %BASIC_OFBIZ_HOME%
echo 'update from git end'

echo 'copy applications'
xcopy %OFBIZ_HOME%\applications %BASIC_OFBIZ_HOME%\applications /s /y /e /i /q

echo 'copy framework'
xcopy %OFBIZ_HOME%\framework %BASIC_OFBIZ_HOME%\framework /s /y /e /i /q

echo 'copy runtime'
xcopy %OFBIZ_HOME%\runtime %BASIC_OFBIZ_HOME%\runtime /s /y /e /i /q

echo 'copy themes'
xcopy %OFBIZ_HOME%\themes\bootstrap %BASIC_OFBIZ_HOME%\themes\bootstrap /s /y /e /i /q
xcopy %OFBIZ_HOME%\themes\metronic %BASIC_OFBIZ_HOME%\themes\metronic /s /y /e /i /q
xcopy %OFBIZ_HOME%\themes\omstheme %BASIC_OFBIZ_HOME%\themes\omstheme /s /y /e /i /q
xcopy %OFBIZ_HOME%\themes\tomahawk %BASIC_OFBIZ_HOME%\themes\tomahawk /s /y /e /i /q

echo 'copy hot-deploy'
xcopy %OFBIZ_HOME%\hot-deploy\account-portal %BASIC_OFBIZ_HOME%\hot-deploy\account-portal /s /y /e /i /q
xcopy %OFBIZ_HOME%\hot-deploy\account-service %BASIC_OFBIZ_HOME%\hot-deploy\account-service /s /y /e /i /q
xcopy %OFBIZ_HOME%\hot-deploy\activity-portal %BASIC_OFBIZ_HOME%\hot-deploy\activity-portal /s /y /e /i /q
xcopy %OFBIZ_HOME%\hot-deploy\contact-portal %BASIC_OFBIZ_HOME%\hot-deploy\contact-portal /s /y /e /i /q
xcopy %OFBIZ_HOME%\hot-deploy\crm-portal %BASIC_OFBIZ_HOME%\hot-deploy\crm-portal /s /y /e /i /q
xcopy %OFBIZ_HOME%\hot-deploy\crm-service %BASIC_OFBIZ_HOME%\hot-deploy\crm-service /s /y /e /i /q
xcopy %OFBIZ_HOME%\hot-deploy\customer-portal %BASIC_OFBIZ_HOME%\hot-deploy\customer-portal /s /y /e /i /q
xcopy %OFBIZ_HOME%\hot-deploy\customer-service %BASIC_OFBIZ_HOME%\hot-deploy\customer-service /s /y /e /i /q
xcopy %OFBIZ_HOME%\hot-deploy\lead-portal %BASIC_OFBIZ_HOME%\hot-deploy\lead-portal /s /y /e /i /q
xcopy %OFBIZ_HOME%\hot-deploy\lead-service %BASIC_OFBIZ_HOME%\hot-deploy\lead-service /s /y /e /i /q
xcopy %OFBIZ_HOME%\hot-deploy\sales-portal %BASIC_OFBIZ_HOME%\hot-deploy\sales-portal /s /y /e /i /q
xcopy %OFBIZ_HOME%\hot-deploy\ticket-portal %BASIC_OFBIZ_HOME%\hot-deploy\ticket-portal /s /y /e /i /q
xcopy %OFBIZ_HOME%\hot-deploy\activity-portal %BASIC_OFBIZ_HOME%\hot-deploy\activity-portal /s /y /e /i /q
xcopy %OFBIZ_HOME%\hot-deploy\sr-portal %BASIC_OFBIZ_HOME%\hot-deploy\sr-portal /s /y /e /i /q

echo 'copy hot-deploy-base'
xcopy %OFBIZ_HOME%\hot-deploy-base\ab-ag-grid-support %BASIC_OFBIZ_HOME%\hot-deploy-base\ab-ag-grid-support /s /y /e /i /q
xcopy %OFBIZ_HOME%\hot-deploy-base\admin-portal %BASIC_OFBIZ_HOME%\hot-deploy-base\admin-portal /s /y /e /i /q
xcopy %OFBIZ_HOME%\hot-deploy-base\appbar-portal %BASIC_OFBIZ_HOME%\hot-deploy-base\appbar-portal /s /y /e /i /q
xcopy %OFBIZ_HOME%\hot-deploy-base\common-portal %BASIC_OFBIZ_HOME%\hot-deploy-base\common-portal /s /y /e /i /q
xcopy %OFBIZ_HOME%\hot-deploy-base\dyna-screen %BASIC_OFBIZ_HOME%\hot-deploy-base\dyna-screen /s /y /e /i /q
xcopy %OFBIZ_HOME%\hot-deploy-base\Etl-Process %BASIC_OFBIZ_HOME%\hot-deploy-base\Etl-Process /s /y /e /i /q
xcopy %OFBIZ_HOME%\hot-deploy-base\fio-dataimport %BASIC_OFBIZ_HOME%\hot-deploy-base\fio-dataimport /s /y /e /i /q
xcopy %OFBIZ_HOME%\hot-deploy-base\homeapps %BASIC_OFBIZ_HOME%\hot-deploy-base\homeapps /s /y /e /i /q
xcopy %OFBIZ_HOME%\hot-deploy-base\ofbiz-ag-grid %BASIC_OFBIZ_HOME%\hot-deploy-base\ofbiz-ag-grid /s /y /e /i /q
xcopy %OFBIZ_HOME%\hot-deploy-base\custom-field %BASIC_OFBIZ_HOME%\hot-deploy-base\custom-field /s /y /e /i /q
xcopy %OFBIZ_HOME%\hot-deploy-base\customfield-service %BASIC_OFBIZ_HOME%\hot-deploy-base\customfield-service /s /y /e /i /q

echo 'copy support-files'
xcopy %BASIC_OFBIZ_HOME%\support-files\ofbiz-component.xml %BASIC_OFBIZ_HOME%\framework\catalina\ofbiz-component.xml /s /y /e /i /q
xcopy %BASIC_OFBIZ_HOME%\support-files\entityengine.xml %BASIC_OFBIZ_HOME%\framework\entity\config\entityengine.xml /s /y /e /i /q
xcopy %BASIC_OFBIZ_HOME%\support-files\start.properties %BASIC_OFBIZ_HOME%\framework\start\src\org\ofbiz\base\start\start.properties /s /y /e /i /q

echo 'basic build end'
