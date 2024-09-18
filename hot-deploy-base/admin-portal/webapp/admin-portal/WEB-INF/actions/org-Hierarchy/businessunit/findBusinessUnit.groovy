import java.text.SimpleDateFormat
import java.util.function.Function
import java.util.stream.Collectors

import org.fio.admin.portal.constant.AdminPortalConstant.BusinessUnitConstant
import org.fio.admin.portal.constant.AdminPortalConstant.DateTimeTypeConstant
import org.fio.admin.portal.util.DataUtil
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.entity.util.EntityUtil
import org.fio.admin.portal.util.DataHelper;
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.condition.EntityFieldValue;
import org.ofbiz.entity.condition.EntityFunction;

List<GenericValue> buDetails =  delegator.findByAnd("ProductStoreGroup",  null, null, false);
context.put("buList", DataHelper.getDropDownOptions(buDetails, "productStoreGroupId", "productStoreGroupName"));

List<GenericValue> parentBuDetails =  delegator.findByAnd("ProductStoreGroup",  null, null, false);
context.put("parentBuList", DataHelper.getDropDownOptions(parentBuDetails, "productStoreGroupId", "productStoreGroupName"));

List<GenericValue> buStatusDetails = delegator.findByAnd("Enumeration",  UtilMisc.toMap("enumTypeId", BusinessUnitConstant.STATUS_ID), null, false);
context.put("statusList", DataHelper.getDropDownOptions(buStatusDetails, "enumId", "description"));

ArrayList<String> typeIdList =  ['PHYSICAL', 'LOGICAL'];
List < GenericValue > buTypeDetails = EntityQuery.use(delegator).from("ProductStoreGroupType").where(EntityCondition.makeCondition("isEnabled", EntityOperator.EQUALS, "Y")).orderBy("-lastUpdatedTxStamp")queryList();
context.put("buTypeList", DataHelper.getDropDownOptions(buTypeDetails, "productStoreGroupTypeId", "description"));

/*
searchCriteria = request.getParameter("searchCriteria");
println('==searchCriteria==='+searchCriteria);
if(UtilValidate.isEmpty(searchCriteria)) {
	List<Map<String, Object>> results = new ArrayList<Map<String,Object>>();
	Set<String> fieldsToSelect = new TreeSet<String>();
	
	
	fieldsToSelect.add("productStoreGroupId");
	fieldsToSelect.add("productStoreGroupName");
	fieldsToSelect.add("primaryParentGroupId");
	fieldsToSelect.add("externalId");
	fieldsToSelect.add("productStoreGroupTypeId");
	fieldsToSelect.add("seqNum");
	fieldsToSelect.add("status");
	fieldsToSelect.add("websiteId");
	fieldsToSelect.add("postalContactMechId");
	fieldsToSelect.add("createdByUserLogin");
	fieldsToSelect.add("createdOn");
	fieldsToSelect.add("modifiedByUserLogin");
	fieldsToSelect.add("modifiedOn");
	fieldsToSelect.add("lastUpdatedTxStamp");
	String productStoreGroupId=null;
	String productStoreGroupTypeId=null;
	String status=null;
	String postalContactMechId=null;
	String websiteId=null;
	String state=null;
	String country=null;
	String createdOn=null;
	String modifiedOn=null;
	SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
	List<GenericValue> productStoreGroups = EntityQuery.use(delegator).select(fieldsToSelect).from("ProductStoreGroup").orderBy("-lastUpdatedTxStamp").queryList();
	if(productStoreGroups != null && productStoreGroups.size() > 0)
	{
		for(GenericValue productStoreGroup : productStoreGroups) {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("businessUnitName", productStoreGroup.getString("productStoreGroupId"));
			data.put("buId", productStoreGroup.getString("externalId"));
			productStoreGroupId=productStoreGroup.getString("primaryParentGroupId");
			if(UtilValidate.isNotEmpty(productStoreGroupId)){
				GenericValue getParentBu = EntityQuery.use(delegator).from("ProductStoreGroup").where("productStoreGroupId", productStoreGroupId).queryOne();
				if(UtilValidate.isNotEmpty(getParentBu)){
					data.put("parentBu", getParentBu.getString("productStoreGroupId"));
				}
			}
			productStoreGroupTypeId=productStoreGroup.getString("productStoreGroupTypeId");
			if(UtilValidate.isNotEmpty(productStoreGroupTypeId)){
				GenericValue getBuType = EntityQuery.use(delegator).from("ProductStoreGroupType").where("productStoreGroupTypeId", productStoreGroupTypeId).queryOne();
				if (UtilValidate.isNotEmpty(getBuType)){
					data.put("buType",getBuType.getString("description"));
				}
			}
			status=productStoreGroup.getString("status");
			if(UtilValidate.isNotEmpty(status)){
				GenericValue getBuStatus = EntityUtil.getFirst(delegator.findByAnd("Enumeration", UtilMisc.toMap("enumId", status,"enumTypeId",BusinessUnitConstant.STATUS_ID), null, false));
				if (UtilValidate.isNotEmpty(getBuStatus)){
					data.put("buStatus",getBuStatus.getString("description"));
				}
			}
			postalContactMechId=productStoreGroup.getString("postalContactMechId");
			if(UtilValidate.isNotEmpty(postalContactMechId)){
				GenericValue getPostalAddress= EntityQuery.use(delegator).from("PostalAddress").where("contactMechId",postalContactMechId).queryOne();
				if (UtilValidate.isNotEmpty(getPostalAddress)){
					state=getPostalAddress.getString("stateProvinceGeoId");
					country=getPostalAddress.getString("countryGeoId");
					if(UtilValidate.isNotEmpty(state)){
						GenericValue getState= EntityQuery.use(delegator).from("Geo").where("geoId",state).queryOne();
						if(UtilValidate.isNotEmpty(getState)){
							data.put("state",getState.getString("geoName"));
						}
					}
					if(UtilValidate.isNotEmpty(country)){
						GenericValue getCountry= EntityQuery.use(delegator).from("Geo").where("geoId",country).queryOne();
						if(UtilValidate.isNotEmpty(getCountry)){
							data.put("country",getCountry.getString("geoName"));
						}
					}
					data.put("city",getPostalAddress.getString("city"));
				}
			}
			websiteId=productStoreGroup.getString("websiteId");
			if(UtilValidate.isNotEmpty(websiteId)) {
				GenericValue getWebsite= EntityQuery.use(delegator).from("ContactMech").where("contactMechId",websiteId).queryOne();
				if (UtilValidate.isNotEmpty(getWebsite)){
					data.put("website",getWebsite.getString("infoString"));
				}
			}
			data.put("createdBy", productStoreGroup.getString("createdByUserLogin"));
			createdOn = productStoreGroup.getString("createdOn");
			if(UtilValidate.isNotEmpty(createdOn))
				createdOn = DataUtil.convertDateTimestamp(createdOn, df, DateTimeTypeConstant.DATE, DateTimeTypeConstant.STRING);
			data.put("createdOn", createdOn);
			data.put("modifiedBy", productStoreGroup.getString("modifiedByUserLogin"));
			modifiedOn = productStoreGroup.getString("modifiedOn");
			if(UtilValidate.isNotEmpty(modifiedOn))
				modifiedOn = DataUtil.convertDateTimestamp(modifiedOn, df, DateTimeTypeConstant.DATE, DateTimeTypeConstant.STRING);
			data.put("modifiedOn", modifiedOn);
			data.put("productStoreGroupId", productStoreGroup.getString("productStoreGroupId"));
			results.add(data);
		}
	}
}
else {
	context.put("searchCriteria", searchCriteria);
}
*/

List<GenericValue> buStatusList = EntityQuery.use(delegator).select("enumId","description").from("Enumeration").where("enumTypeId", BusinessUnitConstant.STATUS_ID).orderBy("sequenceId").queryList();
if(UtilValidate.isNotEmpty(buStatusList)) {
	context.put("buStatus",DataUtil.convertToJson(DataUtil.getMapFromGeneric(buStatusList, "enumId", "description", false)));
}
