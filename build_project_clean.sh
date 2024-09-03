
export OFBIZ_HOME=$(pwd)/../../source_artifacts/icrm_base
export RUNTIME_OFBIZ_HOME=$(pwd)
echo $OFBIZ_HOME$
echo $RUNTIME_OFBIZ_HOME$

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
