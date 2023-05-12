
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
