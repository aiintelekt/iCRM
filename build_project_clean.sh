
SET OFBIZ_HOME=D:\work\Eclipse_FIO_Workspace\FIO_IUC_ICRM_Project\icrmiuc-icrm-base-repo
SET BASIC_OFBIZ_HOME=D:\work\Eclipse_FIO_Workspace\FIO_IUC_ICRM_Project\aws-freemium-repo
echo $OFBIZ_HOME$

echo 'Cleanup project start'
cd $BASIC_OFBIZ_HOME$\hot-deploy
for /d /r $$i in (*) do rmdir /s /q "$$i"
for $$i in (*.*) do if not "$$i" == "build.xml" if not "$$i" == "component-load.xml" del /q "$$i"

cd $BASIC_OFBIZ_HOME$\hot-deploy-base
for /d /r $$i in (*) do rmdir /s /q "$$i"
for $$i in (*.*) do if not "$$i" == "build.xml" if not "$$i" == "component-load.xml" del /q "$$i"

cd $BASIC_OFBIZ_HOME$
rmdir /s /q applications
rmdir /s /q framework
rmdir /s /q runtime
rmdir /s /q themes
echo 'Cleanup project end'
