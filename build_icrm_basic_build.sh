
export OFBIZ_HOME=$(pwd)/../../source_artifacts/icrm_base
export RUNTIME_OFBIZ_HOME=$(pwd)
export GIT_BASE_BRANCH=icrm_base_release_10.1.2_200824
export GIT_B2B_BRANCH=icrm_b2b_release_10.1.2_200824
echo $OFBIZ_HOME$

echo 'Cleanup project start'
cd $RUNTIME_OFBIZ_HOME/hot-deploy
find $RUNTIME_OFBIZ_HOME/hot-deploy -mindepth 1 -maxdepth 1 -type d -exec rm -r {} +

cd $RUNTIME_OFBIZ_HOME/hot-deploy-base
find $RUNTIME_OFBIZ_HOME/hot-deploy-base -mindepth 1 -maxdepth 1 -type d -exec rm -r {} +

cd $RUNTIME_OFBIZ_HOME
rm -rf applications
rm -rf framework
rm -rf runtime
echo 'Cleanup project end'

echo 'update from git start'
cd $OFBIZ_HOME
git pull origin $GIT_BASE_BRANCH
cd $OFBIZ_HOME/hot-deploy
git pull origin $GIT_B2B_BRANCH
cd $RUNTIME_OFBIZ_HOME
echo 'update from git end'

echo 'copy applications'
cp -r $OFBIZ_HOME/applications $RUNTIME_OFBIZ_HOME/

echo 'copy framework'
cp -r $OFBIZ_HOME/framework $RUNTIME_OFBIZ_HOME/

echo 'copy runtime'
cp -r $OFBIZ_HOME/runtime $RUNTIME_OFBIZ_HOME/

echo 'copy themes'
cp -r $OFBIZ_HOME/themes/bootstrap $RUNTIME_OFBIZ_HOME/themes/
cp -r $OFBIZ_HOME/themes/metronic $RUNTIME_OFBIZ_HOME/themes/
cp -r $OFBIZ_HOME/themes/omstheme $RUNTIME_OFBIZ_HOME/themes/
cp -r $OFBIZ_HOME/themes/tomahawk $RUNTIME_OFBIZ_HOME/themes/

echo 'copy hot-deploy'
cp -r $OFBIZ_HOME/hot-deploy/account-portal $RUNTIME_OFBIZ_HOME/hot-deploy/
cp -r $OFBIZ_HOME/hot-deploy/account-service $RUNTIME_OFBIZ_HOME/hot-deploy/
cp -r $OFBIZ_HOME/hot-deploy/activity-portal $RUNTIME_OFBIZ_HOME/hot-deploy/
cp -r $OFBIZ_HOME/hot-deploy/contact-portal $RUNTIME_OFBIZ_HOME/hot-deploy/
cp -r $OFBIZ_HOME/hot-deploy/crm-portal $RUNTIME_OFBIZ_HOME/hot-deploy/
cp -r $OFBIZ_HOME/hot-deploy/crm-service $RUNTIME_OFBIZ_HOME/hot-deploy/
cp -r $OFBIZ_HOME/hot-deploy/customer-portal $RUNTIME_OFBIZ_HOME/hot-deploy/
cp -r $OFBIZ_HOME/hot-deploy/customer-service $RUNTIME_OFBIZ_HOME/hot-deploy/
cp -r $OFBIZ_HOME/hot-deploy/lead-portal $RUNTIME_OFBIZ_HOME/hot-deploy/
cp -r $OFBIZ_HOME/hot-deploy/lead-service $RUNTIME_OFBIZ_HOME/hot-deploy/
cp -r $OFBIZ_HOME/hot-deploy/sales-portal $RUNTIME_OFBIZ_HOME/hot-deploy/
cp -r $OFBIZ_HOME/hot-deploy/ticket-portal $RUNTIME_OFBIZ_HOME/hot-deploy/
cp -r $OFBIZ_HOME/hot-deploy/activity-portal $RUNTIME_OFBIZ_HOME/hot-deploy/
cp -r $OFBIZ_HOME/hot-deploy/sr-portal $RUNTIME_OFBIZ_HOME/hot-deploy/

echo 'copy hot-deploy-base'
cp -r $OFBIZ_HOME/hot-deploy-base/ab-ag-grid-support $RUNTIME_OFBIZ_HOME/hot-deploy-base/
cp -r $OFBIZ_HOME/hot-deploy-base/admin-portal $RUNTIME_OFBIZ_HOME/hot-deploy-base/
cp -r $OFBIZ_HOME/hot-deploy-base/appbar-portal $RUNTIME_OFBIZ_HOME/hot-deploy-base/
cp -r $OFBIZ_HOME/hot-deploy-base/common-portal $RUNTIME_OFBIZ_HOME/hot-deploy-base/
cp -r $OFBIZ_HOME/hot-deploy-base/dyna-screen $RUNTIME_OFBIZ_HOME/hot-deploy-base/
cp -r $OFBIZ_HOME/hot-deploy-base/Etl-Process $RUNTIME_OFBIZ_HOME/hot-deploy-base/
cp -r $OFBIZ_HOME/hot-deploy-base/fio-dataimport $RUNTIME_OFBIZ_HOME/hot-deploy-base/
cp -r $OFBIZ_HOME/hot-deploy-base/homeapps $RUNTIME_OFBIZ_HOME/hot-deploy-base/
cp -r $OFBIZ_HOME/hot-deploy-base/ofbiz-ag-grid $RUNTIME_OFBIZ_HOME/hot-deploy-base/
cp -r $OFBIZ_HOME/hot-deploy-base/custom-field $RUNTIME_OFBIZ_HOME/hot-deploy-base/
cp -r $OFBIZ_HOME/hot-deploy-base/customfield-service $RUNTIME_OFBIZ_HOME/hot-deploy-base/

echo 'copy support-files'
cp -r $RUNTIME_OFBIZ_HOME/support-files/ofbiz-component.xml $RUNTIME_OFBIZ_HOME/framework/catalina/
cp -r $RUNTIME_OFBIZ_HOME/support-files/entityengine.xml $RUNTIME_OFBIZ_HOME/framework/entity/config/
cp -r $RUNTIME_OFBIZ_HOME/support-files/start.properties $RUNTIME_OFBIZ_HOME/framework/start/src/org/ofbiz/base/start/
cp -r $RUNTIME_OFBIZ_HOME/support-files/general.properties $RUNTIME_OFBIZ_HOME/framework/common/config/general.properties

echo 'basic build end'
