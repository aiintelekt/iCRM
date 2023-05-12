
export OFBIZ_HOME=/home/ec2-user/freemium/icrm_base
export BASIC_OFBIZ_HOME=/home/ec2-user/freemium/icrm_basic
echo $OFBIZ_HOME$

echo 'Cleanup project start'
cd $BASIC_OFBIZ_HOME/hot-deploy
find $BASIC_OFBIZ_HOME/hot-deploy -mindepth 1 -maxdepth 1 -type d -exec rm -r {} +

cd $BASIC_OFBIZ_HOME/hot-deploy-base
find $BASIC_OFBIZ_HOME/hot-deploy-base -mindepth 1 -maxdepth 1 -type d -exec rm -r {} +

cd $BASIC_OFBIZ_HOME
rm -rf applications
rm -rf framework
rm -rf runtime
rm -rf themes
echo 'Cleanup project end'

echo 'update from git start'
cd $OFBIZ_HOME
git pull origin dev_release_18032022
cd $OFBIZ_HOME/hot-deploy
git pull origin dev_release_18032022
cd $BASIC_OFBIZ_HOME
echo 'update from git end'

echo 'copy applications'
cp -r $OFBIZ_HOME/applications $BASIC_OFBIZ_HOME/

echo 'copy framework'
cp -r $OFBIZ_HOME/framework $BASIC_OFBIZ_HOME/

echo 'copy runtime'
cp -r $OFBIZ_HOME/runtime $BASIC_OFBIZ_HOME/

echo 'copy themes'
cp -r $OFBIZ_HOME/themes/bootstrap $BASIC_OFBIZ_HOME/themes/
cp -r $OFBIZ_HOME/themes/metronic $BASIC_OFBIZ_HOME/themes/
cp -r $OFBIZ_HOME/themes/omstheme $BASIC_OFBIZ_HOME/themes/
cp -r $OFBIZ_HOME/themes/tomahawk $BASIC_OFBIZ_HOME/themes/

echo 'copy hot-deploy'
cp -r $OFBIZ_HOME/hot-deploy/account-portal $BASIC_OFBIZ_HOME/hot-deploy/
cp -r $OFBIZ_HOME/hot-deploy/account-service $BASIC_OFBIZ_HOME/hot-deploy/
cp -r $OFBIZ_HOME/hot-deploy/activity-portal $BASIC_OFBIZ_HOME/hot-deploy/
cp -r $OFBIZ_HOME/hot-deploy/contact-portal $BASIC_OFBIZ_HOME/hot-deploy/
cp -r $OFBIZ_HOME/hot-deploy/crm-portal $BASIC_OFBIZ_HOME/hot-deploy/
cp -r $OFBIZ_HOME/hot-deploy/crm-service $BASIC_OFBIZ_HOME/hot-deploy/
cp -r $OFBIZ_HOME/hot-deploy/customer-portal $BASIC_OFBIZ_HOME/hot-deploy/
cp -r $OFBIZ_HOME/hot-deploy/customer-service $BASIC_OFBIZ_HOME/hot-deploy/
cp -r $OFBIZ_HOME/hot-deploy/lead-portal $BASIC_OFBIZ_HOME/hot-deploy/
cp -r $OFBIZ_HOME/hot-deploy/lead-service $BASIC_OFBIZ_HOME/hot-deploy/
cp -r $OFBIZ_HOME/hot-deploy/sales-portal $BASIC_OFBIZ_HOME/hot-deploy/
cp -r $OFBIZ_HOME/hot-deploy/ticket-portal $BASIC_OFBIZ_HOME/hot-deploy/
cp -r $OFBIZ_HOME/hot-deploy/activity-portal $BASIC_OFBIZ_HOME/hot-deploy/
cp -r $OFBIZ_HOME/hot-deploy/sr-portal $BASIC_OFBIZ_HOME/hot-deploy/

echo 'copy hot-deploy-base'
cp -r $OFBIZ_HOME/hot-deploy-base/ab-ag-grid-support $BASIC_OFBIZ_HOME/hot-deploy-base/
cp -r $OFBIZ_HOME/hot-deploy-base/admin-portal $BASIC_OFBIZ_HOME/hot-deploy-base/
cp -r $OFBIZ_HOME/hot-deploy-base/appbar-portal $BASIC_OFBIZ_HOME/hot-deploy-base/
cp -r $OFBIZ_HOME/hot-deploy-base/common-portal $BASIC_OFBIZ_HOME/hot-deploy-base/
cp -r $OFBIZ_HOME/hot-deploy-base/dyna-screen $BASIC_OFBIZ_HOME/hot-deploy-base/
cp -r $OFBIZ_HOME/hot-deploy-base/Etl-Process $BASIC_OFBIZ_HOME/hot-deploy-base/
cp -r $OFBIZ_HOME/hot-deploy-base/fio-dataimport $BASIC_OFBIZ_HOME/hot-deploy-base/
cp -r $OFBIZ_HOME/hot-deploy-base/homeapps $BASIC_OFBIZ_HOME/hot-deploy-base/
cp -r $OFBIZ_HOME/hot-deploy-base/ofbiz-ag-grid $BASIC_OFBIZ_HOME/hot-deploy-base/
cp -r $OFBIZ_HOME/hot-deploy-base/custom-field $BASIC_OFBIZ_HOME/hot-deploy-base/
cp -r $OFBIZ_HOME/hot-deploy-base/customfield-service $BASIC_OFBIZ_HOME/hot-deploy-base/

echo 'copy support-files'
cp -r $BASIC_OFBIZ_HOME/support-files/ofbiz-component.xml $BASIC_OFBIZ_HOME/framework/catalina/
cp -r $BASIC_OFBIZ_HOME/support-files/entityengine.xml $BASIC_OFBIZ_HOME/framework/entity/config/
cp -r $BASIC_OFBIZ_HOME/support-files/start.properties $BASIC_OFBIZ_HOME/framework/start/src/org/ofbiz/base/start/

echo 'basic build end'
