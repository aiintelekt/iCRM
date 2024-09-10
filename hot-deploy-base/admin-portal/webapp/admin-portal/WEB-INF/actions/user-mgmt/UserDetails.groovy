import java.nio.file.Files
import java.text.SimpleDateFormat

import org.fio.admin.portal.constant.AdminPortalConstant.DateTimeTypeConstant
import org.fio.admin.portal.util.DataUtil
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.entity.util.EntityUtil
import org.ofbiz.party.contact.ContactMechWorker
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc

activeTab = UtilValidate.isNotEmpty(request.getParameter("activeTab")) ? request.getParameter("activeTab") : request.getAttribute("activeTab");
context.put("activeTab", activeTab);
inputContext = new LinkedHashMap<String, Object>();
SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
String userLoginId = request.getParameter("userLoginId");
if(UtilValidate.isNotEmpty(userLoginId)) {
	Map<String,Object> data = new HashMap<String,Object>();
	String oneBankId = "";
	String statusId = "";
	String partyId = "";
	String state = ""; String country = "";
	GenericValue partyGv = EntityQuery.use(delegator).from("UserLoginPerson").where("userLoginId",userLoginId).queryOne();
	if(UtilValidate.isNotEmpty(partyGv)) {
		partyId = partyGv.getString("partyId");
		//userLoginId = partyGv.getString("userLoginId");
		statusId = partyGv.getString("enabled");
		bDate = partyGv.getString("birthDate");
		gender = partyGv.getString("gender");
		status = statusId == "N"?"Inactive":"Active";
		//data.put("userStatus", statusId == "N"?"Inactive":"Active");
		data.put("userLoginId", userLoginId);
		//data.put("partyId", partyId);
		birthDate = DataUtil.convertDateTimestamp(bDate, df, DateTimeTypeConstant.DATE, DateTimeTypeConstant.STRING);
		data.put("partyId", partyId);
		data.put("firstName", partyGv.getString("firstName"));
		data.put("lastName", partyGv.getString("lastName"));
		data.put("occupationalGroup", partyGv.getString("occupation"));
		if (UtilValidate.isNotEmpty(gender)) {
			genderName = EntityQuery.use(delegator).from("Enumeration").where("enumId", gender, "enumTypeId", "GENDER").queryOne();
			gender = UtilValidate.isNotEmpty(genderName) ?(UtilValidate.isNotEmpty(genderName.getString("description")) ?genderName.getString("description") : "Unknown") : "Unknown";
		}else {
			gender = "Unknown";
		}
		data.put("gender", gender);
		data.put("salutation", partyGv.getString("personalTitle"));
		data.put("bDate", bDate);
		busiUnit = partyGv.getString("businessUnit");
		String businessUnitName = "Unknown";
		if(UtilValidate.isNotEmpty(busiUnit)) {
			productStore = EntityQuery.use(delegator).from("ProductStoreGroup").where("productStoreGroupId",busiUnit).queryOne();
			if(UtilValidate.isNotEmpty(productStore) && UtilValidate.isNotEmpty(productStore.getString("productStoreGroupName"))) {
				businessUnitName=productStore.getString("productStoreGroupName");
				}
			}
		data.put("businessUnitName", businessUnitName);
		data.put("businessUnit", partyGv.getString("businessUnit"));
		data.put("jobTitle", partyGv.getString("jobTitle"));
		data.put("nationality", partyGv.getString("nationality"));
		data.put("isLdapUser", partyGv.getString("isLdapUser"));
		data.put("userStatusDetail", status);
		
		
		//profile photo
		List<GenericValue> partyContentList = EntityQuery.use(delegator).from("PartyContent").where("partyId", partyId, "partyContentTypeId","USER_PROFILE_IMAGE").filterByDate().queryList();
		List<String> contentIds = new LinkedList<String>();
		List<EntityCondition> conditions2 = new ArrayList<EntityCondition>();
		
		conditions2.add(EntityCondition.makeCondition("contentTypeId",EntityOperator.EQUALS,"PROFILE_IMG"));
		if(UtilValidate.isNotEmpty(partyContentList)) {
			contentIds = EntityUtil.getFieldListFromEntityList(partyContentList, "contentId", true);
			conditions2.add(EntityCondition.makeCondition("contentId",EntityOperator.IN,contentIds));
			
			List<GenericValue> contents = EntityQuery.use(delegator).select("contentId","dataResourceId").from("Content").where(EntityCondition.makeCondition(conditions2, EntityOperator.AND)).queryList();
			List<String> dataResourceIds = new LinkedList<String>();
			
			if(UtilValidate.isNotEmpty(contents)) {
				dataResourceIds = EntityUtil.getFieldListFromEntityList(contents, "dataResourceId", true);
			}
			if(UtilValidate.isNotEmpty(dataResourceIds)) {
				List<GenericValue> dataResources =  EntityQuery.use(delegator).from("DataResource").where(EntityCondition.makeCondition("dataResourceId", EntityOperator.IN, dataResourceIds)).queryList();
				
				if(UtilValidate.isNotEmpty(dataResources)) {
					for(GenericValue dataResource : dataResources) {
						Map<String, Object> imageMap = new HashMap<>();
						String filePath = dataResource.getString("objectInfo");
						String dataResourceId = dataResource.getString("dataResourceId");
						String createdByUserLoginId = dataResource.getString("createdByUserLogin");
						File image = new File(filePath);
						String fileName = image.isFile() ? image.getName() :"";
						
						if(image.exists()) {
							imageMap.putAll(org.fio.admin.portal.util.DataUtil.convertGenericValueToMap(delegator, dataResource));
							byte[] fileContent = Files.readAllBytes(image.toPath());
							String encodedImage = Base64.getEncoder().encodeToString(fileContent);
							data.put("userPhoto", "data:image/png;base64,"+encodedImage);
							data.put("dataResourceId", dataResourceId);
						}
					}
				}
			}
			
		}
		
		// end
		
		

	}
	context.put("userData", data);
	inputContext.put("userData", data);
	inputContext.put("userStatus", statusId);
	inputContext.put("location", org.fio.homeapps.util.DataUtil.getPartyAttrValue(delegator, partyId, "LOCATION"));
	inputContext.put("locationDesc", org.groupfio.common.portal.util.DataUtil.getProductStoreName(delegator, (String) inputContext.get("location")));
	partyContactMechValueMaps = ContactMechWorker.getPartyContactMechValueMaps(delegator, partyId, false);
	context.put("partyContactMechValueMaps", partyContactMechValueMaps);
	// User summary data
	String emailContactId="";
	String phoneContactId="";
	String postalContactId="";
	GenericValue postalAddress = null;
	if(UtilValidate.isNotEmpty(partyContactMechValueMaps)) {
		for(int i=0;i<partyContactMechValueMaps.size();i++){
			contactMech = partyContactMechValueMaps.get(i).get("contactMech");
			if("TELECOM_NUMBER".equals(contactMech.get("contactMechTypeId"))){
				phoneContactId=contactMech.get("contactMechId");
			}
			if("EMAIL_ADDRESS".equals(contactMech.get("contactMechTypeId"))){
				emailContactId=contactMech.get("contactMechId");
			}
			if("POSTAL_ADDRESS".equals(contactMech.get("contactMechTypeId"))){
				postalContactId=contactMech.get("contactMechId");
			}
		}
	}
	//get postal address
	Map<String,Object> postalData = new HashMap<String,Object>();
	Map<String,Object> telecomNumber = new HashMap<String,Object>();
	Map<String,Object> emailInfo = new HashMap<String,Object>();
	if(UtilValidate.isNotEmpty(postalContactId)) {
		postalAddress = delegator.findOne("PostalAddress", UtilMisc.toMap("contactMechId", postalContactId), false);
	}
	if(UtilValidate.isNotEmpty(postalAddress)) {
		inputContext.put("generalAttnName", postalAddress.getString("attnName"));
		inputContext.put("generalAddress1", postalAddress.getString("address1"));
		inputContext.put("generalAddress2", postalAddress.getString("address2"));
		inputContext.put("generalPostalCode", postalAddress.getString("postalCode"));
		inputContext.put("generalPostalCodeExt", postalAddress.getString("postalCodeExt"));
		inputContext.put("generalCity", postalAddress.getString("city"));
		inputContext.put("stateGeoId", postalAddress.getString("stateProvinceGeoId"));
		inputContext.put("generalCountryGeoId", postalAddress.getString("countryGeoId"));
		state = postalAddress.getString("stateProvinceGeoId");
		country = postalAddress.getString("countryGeoId");
		if (UtilValidate.isNotEmpty(state)) {
			stateName = org.fio.homeapps.util.DataUtil.getGeoName(delegator,state,"STATE");
			inputContext.put("state", stateName);
		}
		if (UtilValidate.isNotEmpty(country)) {
			country = org.fio.homeapps.util.DataUtil.getGeoName(delegator, country, "COUNTRY");
			inputContext.put("country", country);
		}
	}
	inputContext.put("postalData", postalData);
	inputContext.put("emailContactId", emailContactId);
	inputContext.put("phoneContactId", phoneContactId);
	inputContext.put("postalContactId", postalContactId);
	context.put("userStatus", status);

	if(UtilValidate.isNotEmpty(phoneContactId)) {
		telecomNumber = delegator.findOne("TelecomNumber", UtilMisc.toMap("contactMechId", phoneContactId), false);
		
		GenericValue partyContactMech = EntityQuery.use(delegator).from("PartyContactMech").where("contactMechId", phoneContactId).filterByDate().queryFirst();
		inputContext.put("extension", UtilValidate.isNotEmpty(partyContactMech) ? partyContactMech.getString("extension"): "");
	}
	if(UtilValidate.isNotEmpty(emailContactId)) {
		emailInfo = delegator.findOne("ContactMech", UtilMisc.toMap("contactMechId", emailContactId), false);
	}

	
	partySummary = EntityQuery.use(delegator).from("PartySummaryDetailsView").where("partyId", partyId).cache(false).queryOne();
	if (partySummary != null) {
		context.put("partySummary", partySummary);
		inputContext.putAll(partySummary.getAllFields());
	}
	if(UtilValidate.isNotEmpty(emailInfo)) {
	inputContext.put("primaryEmail",emailInfo.get("infoString"));
	}
	if(UtilValidate.isNotEmpty(telecomNumber)) {
	inputContext.put("primaryPhoneNumber", telecomNumber.get("contactNumber"));
	}
	inputContext.put("bDate", bDate);
	inputContext.put("occupationalGroup", partyGv.getString("occupation"));
	context.put("inputContext", inputContext);
}