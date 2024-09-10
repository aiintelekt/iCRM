import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import org.fio.homeapps.util.DataUtil;
import org.fio.homeapps.util.PartyHelper;
import org.fio.sr.portal.DataHelper;
import org.groupfio.common.portal.util.PartyPrimaryContactMechWorker;
import org.groupfio.crm.service.resolver.SlaTatResolver
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.jdbc.SQLProcessor;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;

delegator = request.getAttribute("delegator");
GenericValue userLogin = request.getAttribute("userLogin");

uiLabelMap = UtilProperties.getResourceBundleMap("SrPortalUiLabels", locale);
String globalDateFormat = org.fio.homeapps.util.DataHelper.getGlobalDateFormat(delegator);
String globalDateTimeFormat = org.fio.homeapps.util.DataHelper.getGlobalDateTimeFormat(delegator);

loggedUserPartyId = userLogin.getString("partyId");
loggedUserName = org.fio.homeapps.util.PartyHelper.getPartyName(delegator, loggedUserPartyId, false);
context.put("loggedUserPartyName", loggedUserName);
context.put("loggedUserId", userLogin.getString("userLoginId"));
context.put("loggedUserPartyId", loggedUserPartyId);

context.put("isActUspsAddrVal", org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "IS_USPS_ADDRACT", "N"));

inputContext = new LinkedHashMap<String, Object>();
Map<String, Object> actionBarContext = new LinkedHashMap<String, Object>();
String partyName= "";
partyId = request.getParameter("partyId");
String isView = context.get("isView");
activeTab = request.getAttribute("activeTab");
context.activeTab = activeTab;

String locationCustomFieldId = org.ofbiz.entity.util.EntityUtilProperties.getPropertyValue("sr-portal.properties", "location.customFieldId", delegator);

String tsmRoleTypeId = EntityUtilProperties.getPropertyValue("sr-portal.properties", "tsm.roleTypeId", delegator);
boolean isTsmUserLoggedIn = UtilValidate.isNotEmpty(PartyHelper.getFirstValidRoleTypeId(userLogin.getString("partyId"), UtilMisc.toList(tsmRoleTypeId), delegator)) ? true : false;
context.put("isTsmUserLoggedIn", isTsmUserLoggedIn);

partySummary = from("PartySummaryDetailsView").where("partyId", partyId,"partyTypeId","PARTY_GROUP").queryOne();

Map<String, Object> appBarContext = new HashMap<String, Object>();
if(partyId!=null){
	primaryContactInformation = PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator,partyId);
	if(UtilValidate.isNotEmpty(primaryContactInformation)) {
		appBarContext.put("primaryEmail",primaryContactInformation.get("EmailAddress"));
		appBarContext.put("emailSolicitation",primaryContactInformation.get("emailSolicitation"));
		appBarContext.put("primaryPhone",org.fio.admin.portal.util.DataUtil.formatPhoneNumber(primaryContactInformation.get("PrimaryPhone")));
		appBarContext.put("phoneSolicitation",primaryContactInformation.get("phoneSolicitation"));
		//context.put("telePhoneLink","tel://"+primaryContactInformation.get("PrimaryPhone"));
		//context.put("telePhoneNumber",primaryContactInformation.get("PrimaryPhone"));
	}
}

if(partySummary!=null && partySummary.size()>0){
	context.put("partySummary", partySummary);
	inputContext.putAll(partySummary.getAllFields());
	partyId_desc = org.fio.homeapps.util.PartyHelper.getPartyName(delegator, partyId, false);
	inputContext.put("partyId_desc",partyId_desc );
	appBarContext.put("name", partySummary.get("groupName"));
}

String srNumber = request.getParameter("srNumber");
if(UtilValidate.isEmpty(srNumber)) {
	srNumber = request.getAttribute("srNumber");
	if(UtilValidate.isNotEmpty(request.getParameter("custRequestId"))){
		srNumber = request.getParameter("custRequestId");
	}
}
if(UtilValidate.isEmpty(srNumber)) {
	srNumber = request.getParameter("domainEntityId");
}
context.put("srNumber", srNumber);

GenericValue primaryData = EntityQuery.use(delegator).from("CustRequestAttribute").where("custRequestId", srNumber, "attrName", "PRIMARY").queryFirst();
primaryVal=null;
if(UtilValidate.isNotEmpty(primaryData)){
	primaryVal=primaryData.get("attrValue");
	inputContext.put("primary", primaryVal);
}
String copyFlag = request.getParameter("copy");

/*Attributes 10146-->special order material
 10149 --> material type
 10210 --> major material category
 10200 --> sub material category*/
String preFinishPlus = DataUtil.getGlobalValue(delegator, "PRO_FINISH_PLUS", "Pro Finish Plus");
String pfpCustomFieldId = org.fio.sr.portal.DataHelper.getCustomFieldId(delegator, "FINISH_GROUP", preFinishPlus);

String vendorCode = DataUtil.getGlobalValue(delegator, "VENDOR_CODE", "Vendor Code");
String vcCustomFieldId = org.fio.sr.portal.DataHelper.getCustomFieldId(delegator, "VENDOR_GROUP", vendorCode);

String serviceFee = DataUtil.getGlobalValue(delegator, "SERVICE_FEE", "Service for a Fee");
String sfCustomFieldId = org.fio.sr.portal.DataHelper.getCustomFieldId(delegator, "SERVICE_GROUP", serviceFee);

String soMaterial = DataUtil.getGlobalValue(delegator, "SPEC_ORDER_MATERIAL", "Special Order Material");
String somCustomFieldId = org.fio.sr.portal.DataHelper.getCustomFieldId(delegator, "MATERIAL_GROUP", soMaterial);

String materialType = DataUtil.getGlobalValue(delegator, "METERIAL_TYPE", "Material Type");
String mtCustomFieldId = org.fio.sr.portal.DataHelper.getCustomFieldId(delegator, "MATERIAL_GROUP", materialType);

String soldByLocation = DataUtil.getGlobalValue(delegator, "SOLD_BY_LOCATION", "Sold By Location");
String sblCustomFieldId = org.fio.sr.portal.DataHelper.getCustomFieldId(delegator, "CUSTOMER_GRP", soldByLocation);

String inspectedBy = DataUtil.getGlobalValue(delegator, "INSPECTED_BY", "Inspected By");
String ibCustomFieldId = org.fio.sr.portal.DataHelper.getCustomFieldId(delegator, "INSPECTION_BOX", inspectedBy);

String inspectionDate = DataUtil.getGlobalValue(delegator, "INSPECTION_DATE", "Inspection Date");
String idCustomFieldId = org.fio.sr.portal.DataHelper.getCustomFieldId(delegator, "INSPECTION_BOX", inspectionDate);

String installedSquare = DataUtil.getGlobalValue(delegator, "INSTALLED_SQUARE", "Installed Square, Level, and Plumb");
String isCustomFieldId = org.fio.sr.portal.DataHelper.getCustomFieldId(delegator, "INSPECTION_BOX", installedSquare);

String hasAlarmSystem = DataUtil.getGlobalValue(delegator, "HAS_ALARM_SYSTEM", "Has Alarm System");
String hasCustomFieldId = org.fio.sr.portal.DataHelper.getCustomFieldId(delegator, "INSPECTION_BOX", hasAlarmSystem);

String installed = DataUtil.getGlobalValue(delegator, "INSTALLED", "Installed");
String insCustomFieldId = org.fio.sr.portal.DataHelper.getCustomFieldId(delegator, "INSPECTION_BOX", installed);

String thirdPtyInvCustomFieldId = DataUtil.getGlobalValue(delegator, "THIRD_PTY_INV_CODE");

String thirdPtyInvPriceCustomFieldId = DataUtil.getGlobalValue(delegator, "THIRD_PTY_INV_AMT_CODE");

custRequestAttList = EntityQuery.use(delegator).from("CustRequestAttribute").where("custRequestId", srNumber).queryList();

if(UtilValidate.isNotEmpty(custRequestAttList)){
	for(int i=0 ; i < custRequestAttList.size() ; i++){
		custRequest=custRequestAttList.get(i);
		if(pfpCustomFieldId.equals(custRequest.get("attrName"))){
			inputContext.put("preFinishPlus", custRequest.get("attrValue"));
		}else if(vcCustomFieldId.equals(custRequest.get("attrName"))){
			inputContext.put("vendorCode", custRequest.get("attrValue"));
		} else if(sfCustomFieldId.equals(custRequest.get("attrName"))){
			inputContext.put("serviceFee", custRequest.get("attrValue"));
		}else if(locationCustomFieldId.equals(custRequest.get("attrName"))){
			inputContext.put("location", custRequest.get("attrValue"));
		} else if(sblCustomFieldId.equals(custRequest.get("attrName"))){
			inputContext.put("soldByLocation", custRequest.get("attrValue"));
		}  else if(ibCustomFieldId.equals(custRequest.get("attrName"))){
			inputContext.put("inspectedBy", custRequest.get("attrValue"));
		} else if(idCustomFieldId.equals(custRequest.get("attrName"))){
			inputContext.put("inspectionDate", custRequest.get("attrValue"));
		} else if(isCustomFieldId.equals(custRequest.get("attrName"))){
			inputContext.put("installedSquare", custRequest.get("attrValue"));
		} else if(hasCustomFieldId.equals(custRequest.get("attrName"))){
			inputContext.put("hasAlarmSystem", custRequest.get("attrValue"));
		} else if(insCustomFieldId.equals(custRequest.get("attrName"))){
			inputContext.put("installed", custRequest.get("attrValue"));
		} else if("CSR_DESC".equals(custRequest.get("attrName"))){
			String coordinatorDesc = custRequest.get("attrValue");
			if(UtilValidate.isNotEmpty(coordinatorDesc) && DataUtil.isBase64(coordinatorDesc)) {
				byte[] base64decodedBytes = Base64.getDecoder().decode(coordinatorDesc);
				coordinatorDesc = new String(base64decodedBytes, "utf-8");
			}
			context.put("coordinatorDesc", coordinatorDesc);
			inputContext.put("coordinatorDesc", custRequest.get("attrValue"));
		}else if(somCustomFieldId.equals(custRequest.get("attrName"))){
			context.put("soMaterial", custRequest.get("attrValue"));
		}else if(mtCustomFieldId.equals(custRequest.get("attrName"))){
			context.put("materialType", custRequest.get("attrValue"));
		} else if("FSR_MATERIAL_CATEGROY".equals(custRequest.get("attrName"))){
			context.put("materialCategoryId", custRequest.get("attrValue"));
			if(UtilValidate.isNotEmpty(custRequest.get("attrValue"))) {
				GenericValue customFieldGroup = EntityQuery.use(delegator).from("CustomFieldGroup").where("groupId", custRequest.get("attrValue"),"groupType","SEGMENTATION").queryFirst();
				inputContext.put("materialCategory", UtilValidate.isNotEmpty(customFieldGroup) ? customFieldGroup.getString("groupName") : "");
			}
		} else if("FSR_MATERIAL_SUB_CATEGROY".equals(custRequest.get("attrName"))){
			context.put("materialSubCategoryId", custRequest.get("attrValue"));
			if(UtilValidate.isNotEmpty(custRequest.get("attrValue"))) {
				GenericValue customField = EntityQuery.use(delegator).from("CustomField").where("customFieldId", custRequest.get("attrValue"),"groupType","SEGMENTATION").queryFirst();
				inputContext.put("materialSubCategory", UtilValidate.isNotEmpty(customField) ? customField.getString("customFieldName") : "");
			}
		} else if("FSR_FINISH_TYPE".equals(custRequest.get("attrName"))){
			context.put("finishTypeId", custRequest.get("attrValue"));
			if(UtilValidate.isNotEmpty(custRequest.get("attrValue"))) {
				GenericValue customFieldGroup = EntityQuery.use(delegator).from("CustomFieldGroup").where("groupId", custRequest.get("attrValue"),"groupType","SEGMENTATION").queryFirst();
				inputContext.put("finishType", UtilValidate.isNotEmpty(customFieldGroup) ? customFieldGroup.getString("groupName") : "");
			}
		} else if("FSR_FINISH_COLOR".equals(custRequest.get("attrName"))){
			context.put("finishColorId", custRequest.get("attrValue"));
			if(UtilValidate.isNotEmpty(custRequest.get("attrValue"))) {
				GenericValue customField = EntityQuery.use(delegator).from("CustomField").where("customFieldId", custRequest.get("attrValue"),"groupType","SEGMENTATION").queryFirst();
				inputContext.put("finishColor", UtilValidate.isNotEmpty(customField) ? customField.getString("customFieldName") : "");
			}
		} else if("DEALER_REF_NO".equals(custRequest.get("attrName"))){
			inputContext.put("dealerRefNo", custRequest.get("attrValue"));
		} else if("CONTRACTOR_NAME".equals(custRequest.get("attrName"))){
			contractName = custRequest.get("attrValue");
			//inputContext.put("contractorId_desc",UtilValidate.isNotEmpty(contractName) ? contractName : "");
		} else if("CUSTOMER_NAME".equals(custRequest.get("attrName"))){
			homeOwnName = custRequest.get("attrValue");
			//inputContext.put("customerId_desc",UtilValidate.isNotEmpty(homeOwnName) ? homeOwnName : "");
		}else if(UtilValidate.isNotEmpty(thirdPtyInvCustomFieldId) && thirdPtyInvCustomFieldId.equals(custRequest.get("attrName"))){
			inputContext.put("thirdPartyInvoiceNumber",custRequest.get("attrValue"));
		}else if(UtilValidate.isNotEmpty(thirdPtyInvPriceCustomFieldId) && thirdPtyInvPriceCustomFieldId.equals(custRequest.get("attrName"))){
			inputContext.put("thirdPartyInvoicePrice",custRequest.get("attrValue"));
		}
	}
}


custRequestSrSummary = from("CustRequestSrSummary").where("custRequestId", srNumber).queryOne();

PartyId ="";
custRequest = from("CustRequest").where("custRequestId", srNumber).queryOne();
if(UtilValidate.isNotEmpty(srNumber) && UtilValidate.isNotEmpty(custRequest)){
	String allowToCloseSR = org.fio.sr.portal.event.AjaxEvents.allowToCloseSR(delegator, userLogin.getString("partyId"), srNumber,custRequest.statusId);
	context.put("allowToCloseSR", allowToCloseSR);
}
GenericValue custRequestParty = EntityQuery.use(delegator).from("CustRequestParty").where("custRequestId", srNumber, "roleTypeId", "CUSTOMER", "thruDate",null).filterByDate().queryFirst();
if(UtilValidate.isNotEmpty(custRequestParty)) {
	String homeOwnerId = custRequestParty.getString("partyId");
	println("homeOwnerId===="+homeOwnerId);
	if(UtilValidate.isNotEmpty(homeOwnerId))
		org.fio.sr.portal.DataHelper.updateSrContactDetails(delegator,srNumber,homeOwnerId,false);
}

GenericValue contractorData = EntityQuery.use(delegator).from("CustRequestParty").where("custRequestId", srNumber, "roleTypeId", "CONTRACTOR", "thruDate",null).filterByDate().queryFirst();
if(UtilValidate.isNotEmpty(contractorData)) {
	String contractorPartyId = contractorData.getString("partyId");
	println("contractorPartyId===="+contractorPartyId);
	if(UtilValidate.isNotEmpty(contractorPartyId))
		org.fio.sr.portal.DataHelper.updateSrContactDetails(delegator,srNumber,contractorPartyId,true);
}

if(UtilValidate.isNotEmpty(custRequest)){
	
	srReopenValue= from("PretailLoyaltyGlobalParameters").where("parameterId","SR_REOPEN_DAYS").queryOne();
	if(UtilValidate.isNotEmpty(srReopenValue)){
		closedDate=custRequest.getTimestamp("closedByDate");
		isAllowReopen="Y";
		if(UtilValidate.isNotEmpty(closedDate)){
			int days= UtilDateTime.getIntervalInDays(closedDate,UtilDateTime.nowTimestamp());
			int srConfigureDays=Integer.parseInt(srReopenValue.get("value"));
			if(days>srConfigureDays){
				boolean hasTLAccess = DataUtil.validatePermission(delegator, userLogin.getString("userLoginId"), "FSR_TL");
				
				if(hasTLAccess)
					isAllowReopen="Y";
				else
					isAllowReopen="N";
			}
			context.put("srReopenDays", srConfigureDays);
			context.put("isAllowReopen", isAllowReopen);
		}
	}
	PartyId = custRequest.fromPartyId;

	context.put("mainAssocPartyId", PartyId);
	custReqDocumentNum = custRequest.custReqDocumentNum;

	srStatusId = custRequest.statusId;
	context.put("srStatusId", srStatusId)

	if(UtilValidate.isNotEmpty(custReqDocumentNum)){
		inputContext.put("sourceDocumentId", custReqDocumentNum	);
		inputContext.put("sourceDocumentId_link","/sr-portal/control/viewServiceRequest?srNumber="+custReqDocumentNum);
		context.put("sourceDocumentId", custReqDocumentNum);
		inputContext.put("sourceComponent", "Service Request");
	}/*else{
	 inputContext.put("sourceDocumentId", srNumber);
	 context.put("sourceDocumentId", srNumber);
	 }*/

	context.put("sourceComponent", "Service Request");

	inputContext.put("externalId", custRequest.getString("externalId"));
	inputContext.put("customerPrimaryEmail", custRequest.getString("emailAddress"));
	inputContext.put("homeType", custRequest.getString("homeType"));

	//inputContext.put("actualResolution", custRequest.getString("actualResolution"));
}

custRequestSupplementory = from("CustRequestSupplementory").where("custRequestId", srNumber).queryOne();
if (UtilValidate.isNotEmpty(custRequestSupplementory)){
	inputContext.put("generalAttnName", custRequestSupplementory.get("pstlAttnName"));
	inputContext.put("generalAddress1", custRequestSupplementory.get("pstlAddress1"));
	inputContext.put("generalAddress2", custRequestSupplementory.get("pstlAddress2"));
	inputContext.put("generalPostalCode", custRequestSupplementory.get("pstlPostalCode"));
	context.put("generalPostalCode", custRequestSupplementory.get("pstlPostalCode"));
	inputContext.put("generalPostalCodeExt", custRequestSupplementory.get("pstlPostalCodeExt"));
	inputContext.put("generalCity", custRequestSupplementory.get("pstlPostalCity"));
	inputContext.put("generalStateProvinceGeoId", custRequestSupplementory.get("pstlStateProvinceGeoId"));
	inputContext.put("generalCountryGeoId", custRequestSupplementory.get("pstlCountryGeoId"));
	inputContext.put("countyGeoId", custRequestSupplementory.get("pstlCountyGeoId"));

	context.put("isUspsAddrVerified", custRequestSupplementory.get("isUspsAddrVerified"));
	context.put("latitude", custRequestSupplementory.get("latitude"));
	context.put("longitude", custRequestSupplementory.get("longitude"));
	iscityLower = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "IS_CITY_LOWER", "N");
	if(iscityLower.equals("Y"))
		context.put("srPostalAddress", org.fio.sr.portal.DataHelper.wrapSrPostalAddress(delegator, custRequestSupplementory));
	else
		context.put("srPostalAddress", org.groupfio.common.portal.util.DataHelper.wrapSrPostalAddress(delegator, custRequestSupplementory));
	inputContext.put("homePhoneNumber", custRequestSupplementory.get("homePhoneNumber"));
	inputContext.put("offPhoneNumber", custRequestSupplementory.get("offPhoneNumber"));
	inputContext.put("mobilePhoneNumber", custRequestSupplementory.get("mobileNumber"));
	inputContext.put("contractorPrimaryEmail", custRequestSupplementory.get("contractorEmail"));
	inputContext.put("contractorOffNumber", custRequestSupplementory.get("contractorOffPhone"));
	inputContext.put("contractorMobileNumber", custRequestSupplementory.get("contractorMobilePhone"));
	inputContext.put("contractorHomeNumber", custRequestSupplementory.get("contractorHomePhone"));
	
	inputContext.put("isCopySr", custRequestSupplementory.get("isCopySr"));
	inputContext.put("domainEntityId", custRequestSupplementory.get("domainEntityId"));
	inputContext.put("domainEntityId_link", org.groupfio.common.portal.util.DataHelper.prepareLinkedFrom(custRequestSupplementory.getString("domainEntityId"), custRequestSupplementory.getString("domainEntityType"), externalLoginKey));
	inputContext.put("domainEntityType", org.groupfio.common.portal.util.DataHelper.convertToLabel(custRequestSupplementory.getString("domainEntityType")));
}

if(custRequest!=null && custRequest.size()>0){

	if (UtilValidate.isNotEmpty(custRequest.get("description")) || UtilValidate.isNotEmpty(custRequest.get("tsmDescription"))){
		if (isTsmUserLoggedIn && UtilValidate.isEmpty(custRequest.get("description"))) {
			description = custRequest.getString("tsmDescription");
			if(UtilValidate.isNotEmpty(description)) {
				byte[] base64decodedBytes = Base64.getDecoder().decode(description);
				description = new String(base64decodedBytes, "utf-8");
				context.put("description", description);
			}
		} else {
			String description = custRequest.getString("description");
			if(UtilValidate.isNotEmpty(description) && DataUtil.isBase64(description)) {
				byte[] base64decodedBytes = Base64.getDecoder().decode(description);
				description = new String(base64decodedBytes, "utf-8");
			}
			context.put("description", description);
		}

		description = custRequest.getString("tsmDescription");
		if(UtilValidate.isNotEmpty(description)) {
			byte[] base64decodedBytes = Base64.getDecoder().decode(description);
			description = new String(base64decodedBytes, "utf-8");
			context.put("tsmDescription", description);
		}
	}

	inputContext.put("description", custRequest.get("description"));

	if (UtilValidate.isNotEmpty(custRequest.get("resolution"))){

		if(UtilValidate.isNotEmpty(copyFlag) && copyFlag.equals("Y")){
			context.put("resolution", "");

		}else{
			String resolution = custRequest.getString("resolution");
			if(UtilValidate.isNotEmpty(resolution) && DataUtil.isBase64(resolution)) {
				byte[] base64decodedBytes = Base64.getDecoder().decode(resolution);
				resolution = new String(base64decodedBytes, "utf-8");
			}
			context.put("resolution", resolution);
		}
	}
	
	String actualResolution = custRequest.getString("actualResolution");
	if(UtilValidate.isNotEmpty(actualResolution) && DataUtil.isBase64(actualResolution)) {
		byte[] base64decodedBytes = Base64.getDecoder().decode(actualResolution);
		actualResolution = new String(base64decodedBytes, "utf-8");
	}
	context.put("actualResolution", actualResolution);
	inputContext.put("actualResolution", custRequest.getString("actualResolution"));

	if (UtilValidate.isNotEmpty(custRequest.get("responsiblePerson"))){
		context.put("ownerUserLoginId", custRequest.get("responsiblePerson"));
	}

	if (UtilValidate.isNotEmpty(custRequest.get("custRequestTypeId"))){
		context.put("srTypeId", custRequest.get("custRequestTypeId"));
		inputContext.put("srTypeId", custRequest.get("custRequestTypeId"));
	}

	if (UtilValidate.isNotEmpty(custRequest.get("custRequestCategoryId"))){
		context.put("srCategoryId", custRequest.get("custRequestCategoryId"));
		inputContext.put("srCategoryId", custRequest.get("custRequestCategoryId"));
	}

	if (UtilValidate.isNotEmpty(custRequest.get("custRequestSubCategoryId"))){
		context.put("srSubCategoryId", custRequest.get("custRequestSubCategoryId"));
		inputContext.put("srSubCategoryId", custRequest.get("custRequestSubCategoryId"));
	}

	if (UtilValidate.isNotEmpty(custRequest.getString("custReqSrSource"))){
		inputContext.put("srSource", custRequest.getString("custReqSrSource"));
	}

	if (UtilValidate.isNotEmpty(custRequest.get("custRequestOthCategoryId"))){
		inputContext.put("otherSrSubCategoryId", custRequest.get("custRequestOthCategoryId"));
	}

	if (UtilValidate.isNotEmpty(custRequest.get("responsiblePerson"))){
		ownerDesc = org.fio.homeapps.util.PartyHelper.getUserLoginName(delegator, custRequest.get("responsiblePerson"), false);
		if (UtilValidate.isNotEmpty(ownerDesc)){
			inputContext.put("ownerDesc", ownerDesc);
		} else {
			inputContext.put("ownerDesc", custRequest.get("responsiblePerson"));
		}

		if (UtilValidate.isNotEmpty(custRequest.get("responsiblePerson"))){
			responsiblePerson = custRequest.get("responsiblePerson");
			GenericValue userLoginLoginGv = delegator.findOne("UserLoginPerson",UtilMisc.toMap("userLoginId", responsiblePerson), false);
			if (UtilValidate.isNotEmpty(userLoginLoginGv)){
				businessUnitVal = userLoginLoginGv.get("businessUnit");

				GenericValue productStoreGroupGv = delegator.findOne("ProductStoreGroup",UtilMisc.toMap("productStoreGroupId", businessUnitVal), false);
				if (UtilValidate.isNotEmpty(productStoreGroupGv)){
					description = productStoreGroupGv.get("description");
					inputContext.put("ownerBu", description);
				}
			}
			
			String ownerPartyId = org.fio.homeapps.util.DataUtil.getPartyIdByUserLoginId(delegator, custRequest.getString("responsiblePerson"));
			String ownerEmail = org.groupfio.common.portal.util.UtilContactMech.getPartyEmail(delegator, ownerPartyId, null);
			context.put("fsrOwnerEmail", ownerEmail);
		}

	}
	if (UtilValidate.isNotEmpty(custRequest)){

		if (UtilValidate.isNotEmpty(custRequest.get("priority"))){
			inputContext.put("priority", custRequest.get("priority"));
		}

		if (request.getRequestURI().contains("createTaskActivity") || request.getRequestURI().contains("createAppointmentActivity")) {
			inputContext.put("statusId", "IA_MSCHEDULED");
		}

		/*if (UtilValidate.isNotEmpty(custRequestSrSummary.get("ownerBuName"))){
		 inputContext.put("ownerBu", custRequestSrSummary.get("ownerBuName"));
		 }*/
	}
	if (UtilValidate.isNotEmpty(custRequestSupplementory)&&UtilValidate.isNotEmpty(custRequestSupplementory.get("accountType"))){
		inputContext.put("accountType", custRequestSupplementory.get("accountType"));
	}

	if (UtilValidate.isNotEmpty(custRequest.get("createdByUserLogin"))){
		createdByUserLogin = org.fio.homeapps.util.PartyHelper.getUserLoginName(delegator, custRequest.get("createdByUserLogin"), false);
		if (UtilValidate.isNotEmpty(createdByUserLogin)){
			inputContext.put("createdByUserLoginDesc", createdByUserLogin);
		}else{
			inputContext.put("createdByUserLoginDesc", custRequest.get("createdByUserLogin"));
		}
	}

	if (UtilValidate.isNotEmpty(custRequest.get("lastModifiedByUserLogin"))){
		lastModifiedByUserLogin = org.fio.homeapps.util.PartyHelper.getUserLoginName(delegator, custRequest.get("lastModifiedByUserLogin"), false);
		if (UtilValidate.isNotEmpty(lastModifiedByUserLogin)){
			inputContext.put("modifiedByUserLoginDesc", lastModifiedByUserLogin);
		}else{
			inputContext.put("modifiedByUserLoginDesc", custRequest.get("lastModifiedByUserLogin"));
		}
	}

	if (UtilValidate.isNotEmpty(custRequest.get("closedByUserLogin"))){
		closedByUserLogin = org.fio.homeapps.util.PartyHelper.getUserLoginName(delegator, custRequest.get("closedByUserLogin"), false);
		if (UtilValidate.isNotEmpty(closedByUserLogin)){
			inputContext.put("closedByUserLoginDesc", closedByUserLogin);
		}else{
			inputContext.put("closedByUserLoginDesc", custRequest.get("closedByUserLogin"));
		}
	}
	
	partyName = org.fio.homeapps.util.PartyHelper.getPartyName(delegator, custRequest.get("fromPartyId"), false);
	partySummaryDetails = from("PartySummaryDetailsView").where("partyId", custRequest.get("fromPartyId")).queryOne();
	inputContext.put("cNo_desc", partyName);
	inputContext.put("partyId_desc", partyName);
	inputContext.put("partyName", partyName);
	inputContext.put("partyId", custRequest.get("fromPartyId"));
	
	
	conditionList = FastList.newInstance();
	conditionList.add(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, srNumber));
	conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "CONTACT"));
	conditionList.add(EntityCondition.makeCondition("isPrimary", EntityOperator.EQUALS, "Y"));
	conditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
	EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	
	String primaryContactName = "";
	String primContactPartyId = "";
	/*
	custRequestContactDetails = EntityUtil.getFirst( delegator.findList("CustRequestContact", mainConditons, null, null, null, false) );
	String primaryContactName = "";
	String primContactPartyId = "";
	if (UtilValidate.isNotEmpty(custRequestContactDetails) && UtilValidate.isNotEmpty(custRequestContactDetails.getString("partyId"))){
		primContactPartyId = custRequestContactDetails.getString("partyId");
		primaryContactName = org.fio.homeapps.util.PartyHelper.getPartyName(delegator, custRequestContactDetails.getString("partyId"), false);
		inputContext.put("primaryContactDesc", primaryContactName);
		inputContext.put("primaryContactDesc_link","/contact-portal/control/viewContact?partyId="+custRequestContactDetails.getString("partyId")+"&externalLoginKey="+externalLoginKey);
	}
	*/
	
	List<GenericValue> custReqContactList = EntityQuery.use(delegator).from("CustRequestContact").where(mainConditons).queryList();
	if(UtilValidate.isNotEmpty(custReqContactList)) {
		List<String> primContacDesctList = new LinkedList();
		List<String> primContactLinkList = new LinkedList();
		
		Map<String, Object> linkData = new LinkedHashMap<String, Object>();
		for(GenericValue custReqContact : custReqContactList) {
			primContactPartyId = custReqContact.getString("partyId");
			primaryContactName = org.fio.homeapps.util.PartyHelper.getPartyName(delegator, primContactPartyId, false);
			primContacDesctList.add(primaryContactName);
			String primLink ="/contact-portal/control/viewContact?partyId="+primContactPartyId+"&externalLoginKey="+externalLoginKey;
			linkData.put(primaryContactName, primLink);
		}
		
		inputContext.put("primaryContactDesc", UtilValidate.isNotEmpty(primContacDesctList) ? org.fio.admin.portal.util.DataUtil.listToString(primContacDesctList,", ") : "");
		inputContext.put("primaryContactDesc_link_data", UtilValidate.isNotEmpty(linkData) ? org.fio.admin.portal.util.DataUtil.convertToJsonStr(linkData) : "");
	}
	

	if (UtilValidate.isNotEmpty(custRequest.get("fromPartyId"))){
		inputContext.put("cNo", custRequest.get("fromPartyId"));
	}
	
	String programTemplateId = org.groupfio.common.portal.util.SrUtil.getCustRequestAttrValue(delegator, "PROG_TPL_ID", srNumber);
	inputContext.put("programTemplateId", programTemplateId);

	if(UtilValidate.isNotEmpty(isView) && "Y".equals(isView)){
		if (UtilValidate.isNotEmpty(custRequest.get("custRequestCategoryId"))){
			custRequestCategory = from("CustRequestCategory").where("custRequestCategoryId", custRequest.get("custRequestCategoryId")).queryOne();
			if (UtilValidate.isNotEmpty(custRequestCategory) && UtilValidate.isNotEmpty(custRequestCategory.get("description"))){
				inputContext.put("srCategoryId", custRequestCategory.get("description"));
			}
		}

		if (UtilValidate.isNotEmpty(custRequest) && UtilValidate.isNotEmpty(custRequest.get("custRequestSubCategoryId"))){
			custRequestCategory = from("CustRequestCategory").where("custRequestCategoryId", custRequest.get("custRequestSubCategoryId")).queryOne();
			if (UtilValidate.isNotEmpty(custRequestCategory) && UtilValidate.isNotEmpty(custRequestCategory.get("description"))){
				inputContext.put("srSubCategoryId", custRequestCategory.get("description"));
			}
		}

		if (UtilValidate.isNotEmpty(custRequest) && UtilValidate.isNotEmpty(custRequest.get("fromPartyId"))){
			inputContext.put("partyType", org.ofbiz.party.party.PartyHelper.getFirstPartyRoleTypeId(custRequest.getString("fromPartyId"), delegator));
			context.put("fromPartyId", custRequest.get("fromPartyId"));
		}
		if (UtilValidate.isNotEmpty(custRequest) && UtilValidate.isNotEmpty(custRequest.get("custReqOnceDone"))){
			inputContext.put("onceAndDone", custRequest.get("custReqOnceDone"));
		}

		if(UtilValidate.isNotEmpty(srNumber)){
			attendeesconditions = EntityCondition.makeCondition([
				EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, srNumber),
				EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null),
				EntityCondition.makeCondition("isPrimary", EntityOperator.EQUALS, "N"),
				EntityUtil.getFilterByDateExpr()
			],
			EntityOperator.AND);

			CustRequestContactPartyAssignmentList = delegator.findList("CustRequestContact", attendeesconditions, null, null, null, false);

			if(UtilValidate.isNotEmpty(CustRequestContactPartyAssignmentList)){
				optionalAttendeeParties = EntityUtil.getFieldListFromEntityList(CustRequestContactPartyAssignmentList, "partyId", false);
				if(UtilValidate.isNotEmpty(optionalAttendeeParties)){
					optionalAttendeesDesc = "";
					optionalAttendeeParties.each { eachOptId ->
						optAttndName = org.fio.homeapps.util.PartyHelper.getPartyName(delegator, eachOptId, false);
						optionalAttendeesDesc += optAttndName+",";
					}
					inputContext.put("optionalAttendees", optionalAttendeesDesc.substring(0, optionalAttendeesDesc.length()-1));

				}
			}
		}
		
		inputContext.put("programTemplateId", org.groupfio.common.portal.util.SrUtil.getCustRequestName(delegator, programTemplateId));
	}

	if (UtilValidate.isNotEmpty(custRequest.get("custRequestId"))){
		inputContext.put("custRequestId", custRequest.get("custRequestId"));
	}

	if (UtilValidate.isNotEmpty(custRequest.get("custOrderId"))){
		inputContext.put("orderId", custRequest.get("custOrderId"));
	}

	if (UtilValidate.isNotEmpty(custRequest.get("custRequestName"))){
		inputContext.put("srName", custRequest.get("custRequestName"));
		context.put("srName", custRequest.get("custRequestName"));
	}

	if (UtilValidate.isNotEmpty(custRequest.get("statusId"))){
		inputContext.put("srStatusId", custRequest.get("statusId"));
	}

	if (UtilValidate.isNotEmpty(custRequest.get("tatDays"))){
		context.put("tatDays", custRequest.get("tatDays"));
	} else {
		context.put("tatDays", "0");
	}

	if (UtilValidate.isNotEmpty(custRequest.get("tatHours"))){
		context.put("tatHrs", custRequest.get("tatHours"));
	} else {
		context.put("tatHrs", "0");
	}

	if (UtilValidate.isNotEmpty(custRequest.get("tatMins"))){
		context.put("tatMins", custRequest.get("tatMins"));
	} else {
		context.put("tatMins", "0");
	}

	inputContext.put("openDate", UtilValidate.isNotEmpty(custRequest.get("createdDate")) ? UtilDateTime.timeStampToString(custRequest.getTimestamp("createdDate"), globalDateTimeFormat, TimeZone.getDefault(), null) : "");
	if (UtilValidate.isNotEmpty(custRequestSupplementory)){
		inputContext.put("dueDate", UtilValidate.isNotEmpty(custRequestSupplementory.get("commitDate")) ? UtilDateTime.timeStampToString(custRequestSupplementory.getTimestamp("commitDate"), globalDateTimeFormat, TimeZone.getDefault(), null) : "");
	}
	inputContext.put("createdOn", UtilValidate.isNotEmpty(custRequest.get("createdDate")) ? UtilDateTime.timeStampToString(custRequest.getTimestamp("createdDate"), globalDateTimeFormat, TimeZone.getDefault(), null) : "");
	inputContext.put("modifiedOn", UtilValidate.isNotEmpty(custRequest.get("lastModifiedDate")) ? UtilDateTime.timeStampToString(custRequest.getTimestamp("lastModifiedDate"), globalDateTimeFormat, TimeZone.getDefault(), null) : "");

	inputContext.put("closedOn", UtilValidate.isNotEmpty(custRequest.get("closedByDate")) ? UtilDateTime.timeStampToString(custRequest.getTimestamp("closedByDate"), globalDateTimeFormat, TimeZone.getDefault(), null) : "");

	inputContext.put("reopenedDate", UtilValidate.isNotEmpty(custRequest.get("reopenedDate")) ? UtilDateTime.timeStampToString(custRequest.getTimestamp("reopenedDate"), globalDateTimeFormat, TimeZone.getDefault(), null) : "");
	inputContext.put("reopenedBy", UtilValidate.isNotEmpty(custRequest.get("reopenedBy")) ? custRequest.getString("reopenedBy") : "");

	if (UtilValidate.isNotEmpty(custRequest.get("reopenedBy"))){
		if (request.getRequestURI().contains("updateServiceRequest") || request.getRequestURI().contains("createServiceRequest")) {
			inputContext.put("reopenedBy", custRequest.get("reopenedBy"));
		}
		if (request.getRequestURI().contains("viewServiceRequest")) {
			reopenedByUserLogin = org.fio.homeapps.util.PartyHelper.getUserLoginName(delegator, custRequest.get("reopenedBy"), false);
			if (UtilValidate.isNotEmpty(reopenedByUserLogin)){
				inputContext.put("reopenedBy", reopenedByUserLogin);
			}
		}
	}
	
	if (UtilValidate.isNotEmpty(copyFlag) && "Y".equals(copyFlag)){
		inputContext.put("srStatusId", "SR_OPEN");
	}
	context.put("currentSrStatusId", custRequest.get("statusId"));

	String priPartyRoleId = org.fio.admin.portal.util.DataUtil.getPartySecurityRole(delegator, custRequest.get("fromPartyId"));
	String priRoleDesc = org.fio.homeapps.util.DataUtil.getRoleTypeDesc(delegator, priPartyRoleId);
	if("Account".equals(priRoleDesc)) priRoleDesc = "Dealer";
	actionBarContext.put("sr-id", srNumber);
	//actionBarContext.put("name", partyName + (UtilValidate.isNotEmpty(priRoleDesc) ? " ("+priRoleDesc+")":""));
	actionBarContext.put("name", primaryContactName + (UtilValidate.isNotEmpty(priRoleDesc) ? " ("+priRoleDesc+")":""));
	
	if(UtilValidate.isNotEmpty(primContactPartyId)) {
		context.put("partyIdVal",primContactPartyId);
		GenericValue partySummaryDetails1 = from("PartySummaryDetailsView").where("partyId", primContactPartyId).queryOne();
		if(UtilValidate.isNotEmpty(partySummaryDetails1)) {
			actionBarContext.put("primaryEmail",partySummaryDetails1.get("primaryEmail"));
			actionBarContext.put("primaryPhone", org.fio.admin.portal.util.DataUtil.formatPhoneNumber(partySummaryDetails1.get("primaryContactNumber")));
			appBarContext.put("primaryPhone",org.fio.admin.portal.util.DataUtil.formatPhoneNumber(partySummaryDetails1.get("primaryContactNumber")));
			inputContext.put("primaryPhone", org.fio.admin.portal.util.DataUtil.formatPhoneNumber(partySummaryDetails1.get("primaryContactNumber")));
			context.put("primaryPhone", partySummaryDetails1.get("primaryContactNumber"));
		}
	}
	else {
		context.put("partyIdVal",PartyId);
		actionBarContext.put("primaryEmail",partySummaryDetails.get("primaryEmail"));
		actionBarContext.put("primaryPhone", org.fio.admin.portal.util.DataUtil.formatPhoneNumber(partySummaryDetails.get("primaryContactNumber")));
		appBarContext.put("primaryPhone",org.fio.admin.portal.util.DataUtil.formatPhoneNumber(partySummaryDetails.get("primaryContactNumber")));
		inputContext.put("primaryPhone", org.fio.admin.portal.util.DataUtil.formatPhoneNumber(partySummaryDetails.get("primaryContactNumber")));
		context.put("primaryPhone", partySummaryDetails.get("primaryContactNumber"));
	}
	

	String statusId = custRequest.get("statusId");
	println("statusId-->"+statusId);
	String atRisk = "No";
	if(!UtilMisc.toList("SR_CLOSED","SR_CANCELLED").contains(statusId) && UtilValidate.isNotEmpty(custRequestSupplementory)){
		Timestamp dueDateTimeStamp = custRequestSupplementory.getTimestamp("commitDate");
		Timestamp preEscalationTimeStamp = custRequestSupplementory.getTimestamp("preEscalationDate");
		Timestamp now = UtilDateTime.nowTimestamp();
		if(UtilValidate.isNotEmpty(preEscalationTimeStamp) && UtilValidate.isNotEmpty(dueDateTimeStamp) && now.after(preEscalationTimeStamp) && now.before(dueDateTimeStamp)) {
			atRisk = "Yes";
		}
	}
	inputContext.put("slaRisk", atRisk);
	context.put("slaRisk", atRisk);
	println ("atRisk-->"+atRisk);


	String overDue = "No";
	if(!UtilMisc.toList("SR_CLOSED","SR_CANCELLED").contains(statusId) && UtilValidate.isNotEmpty(custRequestSupplementory)){
		Timestamp dueDateTimeStamp = custRequestSupplementory.getTimestamp("commitDate");
		Timestamp now = UtilDateTime.nowTimestamp();
		if(UtilValidate.isNotEmpty(dueDateTimeStamp) && now.after(dueDateTimeStamp)) {
			overDue = "Yes";
		}
	}
	inputContext.put("overDueFlag", overDue);
	context.put("overDueFlag", overDue);


}
inputContext.put("appBarContext", appBarContext);
inputContext.put("actionBarContext", actionBarContext);
//kpi bar data
ResultSet rs = null;
SQLProcessor sqlProcessor = new SQLProcessor(delegator, delegator.getGroupHelperInfo("org.ofbiz"));
Map<String, Object> kpiBarContext = new LinkedHashMap<String, Object>();

String srId = "";
String srName = "";
String srType = "";
String srCategory = "";
String srSubCategory = "";
String priority = "";
String srStatus = "";
String srOpenDate = "";
String srDueDate = "";
String srClosedDate = "";
String srClosedBy = "";
String srActTotalTEHours = "0";
String srActTotalTECost = "0";
String contactedDate = "";
String issueMaterialCost = "0";


if(UtilValidate.isNotEmpty(srNumber)) {

	String srSql = "SELECT CUST_REQUEST_NAME as 'srName', DATE_FORMAT(IFNULL(OPEN_DATE_TIME,created_date),'%Y-%m-%d %H:%i') as srOpenDate, DATE_FORMAT(CLOSED_BY_DATE,'%Y-%m-%d %H:%i') as 'srClosedDate', CLOSED_BY_USER_LOGIN as 'srClosedBy' FROM `cust_request` WHERE `CUST_REQUEST_ID`='"+srNumber+"'";
	rs = sqlProcessor.executeQuery(srSql);
	if (rs != null) {
		while (rs.next()) {
			srName = rs.getString("srName");
			srOpenDate = rs.getString("srOpenDate");
			srClosedDate = rs.getString("srClosedDate");
			srClosedBy = rs.getString("srClosedBy");
		}
	}

	String srTypeSql = "SELECT b.DESCRIPTION as 'srType' FROM `cust_request` a INNER JOIN cust_request_type b ON a.`CUST_REQUEST_TYPE_ID`=b.CUST_REQUEST_TYPE_ID WHERE a.`CUST_REQUEST_ID`='"+srNumber+"'";
	rs = sqlProcessor.executeQuery(srTypeSql);
	if (rs != null) {
		while (rs.next()) {
			srType = rs.getString("srType");
		}
	}

	String srCategorySql = "SELECT b.DESCRIPTION as 'srCategory' FROM `cust_request` a INNER JOIN `cust_request_category` b ON a.`CUST_REQUEST_CATEGORY_ID`=b.CUST_REQUEST_CATEGORY_ID WHERE a.`CUST_REQUEST_ID`='"+srNumber+"'";
	rs = sqlProcessor.executeQuery(srCategorySql);
	if (rs != null) {
		while (rs.next()) {
			srCategory = rs.getString("srCategory");
		}
	}

	String srSubCategorySql = "SELECT b.DESCRIPTION as 'srSubCategory' FROM `cust_request` a INNER JOIN `cust_request_category` b ON a.`CUST_REQUEST_SUB_CATEGORY_ID`=b.CUST_REQUEST_CATEGORY_ID WHERE a.`CUST_REQUEST_ID`='"+srNumber+"'";
	rs = sqlProcessor.executeQuery(srSubCategorySql);
	if (rs != null) {
		while (rs.next()) {
			srSubCategory = rs.getString("srSubCategory");
		}
	}

	String prioritySql = "SELECT b.DESCRIPTION as 'priority' FROM `cust_request` a INNER JOIN  enumeration b ON a.`PRIORITY`=b.ENUM_ID WHERE a.`CUST_REQUEST_ID`='"+srNumber+"'";
	rs = sqlProcessor.executeQuery(prioritySql);
	if (rs != null) {
		while (rs.next()) {
			priority = rs.getString("priority");
		}
	}

	String srStatusSql = "SELECT b.DESCRIPTION as 'srStatus' FROM `cust_request` a INNER JOIN  STATUS_ITEM b ON a.`STATUS_ID`=b.STATUS_ID WHERE a.`CUST_REQUEST_ID`='"+srNumber+"'";
	rs = sqlProcessor.executeQuery(srStatusSql);
	if (rs != null) {
		while (rs.next()) {
			srStatus = rs.getString("srStatus");
		}
	}

	String srDueDateSql = "SELECT DATE_FORMAT(COMMIT_DATE,'%Y-%m-%d %H:%i') as 'srDueDate' FROM `cust_request_supplementory` WHERE CUST_REQUEST_ID='"+srNumber+"'";
	rs = sqlProcessor.executeQuery(srDueDateSql);
	if (rs != null) {
		while (rs.next()) {
			srDueDate = rs.getString("srDueDate");
		}
	}

	String totalCostSql = "SELECT SUM(cost) as totalCost FROM time_entry WHERE work_effort_id IN ( SELECT work_effort_id FROM cust_request_work_effort WHERE cust_request_id = '"+srNumber+"')";
	rs = sqlProcessor.executeQuery(totalCostSql);
	if (rs != null) {
		while (rs.next()) {
			srActTotalTECost = rs.getString("totalCost");
		}
	}

	String totalHoursSql = "SELECT SUM(hours) as totalHours FROM time_entry WHERE work_effort_id IN ( SELECT work_effort_id FROM cust_request_work_effort WHERE cust_request_id = '"+srNumber+"')";
	rs = sqlProcessor.executeQuery(totalHoursSql);
	if (rs != null) {
		while (rs.next()) {
			srActTotalTEHours = rs.getString("totalHours");
		}
	}
	
	String contactedDateSql = "SELECT DATE_FORMAT(crwe.CREATED_TX_STAMP,'%Y-%m-%d %H:%i') as 'contactedDate' FROM cust_request_work_effort crwe  INNER JOIN work_effort we ON crwe.WORK_EFFORT_ID=we.WORK_EFFORT_ID INNER JOIN work_effort_type wet ON we.WORK_EFFORT_TYPE_ID = wet.WORK_EFFORT_TYPE_ID WHERE crwe.CUST_REQUEST_ID = '"+srNumber+"' AND wet.DESCRIPTION IN ('Phone Call','E-mail')  LIMIT 1";
	rs = sqlProcessor.executeQuery(contactedDateSql);
	if (rs != null) {
		while (rs.next()) {
			contactedDate = rs.getString("contactedDate");
		}
	}
	
	String issueMatCostSql = "SELECT SUM(cost) as issueMatCost FROM issue_material WHERE work_effort_id IN ( SELECT work_effort_id FROM cust_request_work_effort WHERE cust_request_id = '"+srNumber+"')";
	rs = sqlProcessor.executeQuery(issueMatCostSql);
	if (rs != null) {
		while (rs.next()) {
			issueMaterialCost = rs.getString("issueMatCost");
		}
	}

	
	sqlProcessor.close();
}

SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
SimpleDateFormat df1 = new SimpleDateFormat("MM/dd/yyyy");

kpiBarContext.put("sr-id", srNumber);
kpiBarContext.put("sr-name", srName);
kpiBarContext.put("sr-type", srType);
kpiBarContext.put("sr-category", srCategory);
kpiBarContext.put("sr-sub-category", srSubCategory);
kpiBarContext.put("priority", priority);
kpiBarContext.put("sr-status", srStatus);
if(UtilValidate.isNotEmpty(srOpenDate)){
	kpiBarContext.put("sr-open-date", df1.format(df.parse(srOpenDate)));
}else{
	kpiBarContext.put("sr-open-date", srOpenDate);
}
if(UtilValidate.isNotEmpty(srDueDate)){
	kpiBarContext.put("sr-due-date", df1.format(df.parse(srDueDate)));
}else{
	kpiBarContext.put("sr-due-date", srDueDate);
}
if(UtilValidate.isNotEmpty(srClosedDate)){
	kpiBarContext.put("sr-closed-date", df1.format(df.parse(srClosedDate)));
}else{
	kpiBarContext.put("sr-closed-date", srClosedDate);
}
if(UtilValidate.isNotEmpty(contactedDate)){
	kpiBarContext.put("sr-contacted-date", df1.format(df.parse(contactedDate)));
}else{
	kpiBarContext.put("sr-contacted-date", contactedDate);
}

kpiBarContext.put("sr-closed-by", org.fio.homeapps.util.PartyHelper.getPartyName(delegator, org.fio.homeapps.util.DataUtil.getPartyIdByUserLoginId(delegator, srClosedBy), false));
kpiBarContext.put("sr-act-total-hours", srActTotalTEHours);
kpiBarContext.put("sr-act-total-cost", srActTotalTECost);
kpiBarContext.put("sr-act-issue-mat-cost", issueMaterialCost);

String slaTatStopStatus = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SLA_TAT_STOP_STATUS");
List slaTatStopStatusList = DataUtil.stringToList(slaTatStopStatus, ",");

String slaTat = "";
srStatusId = custRequest.statusId;
println"--srStatusId---"+srStatusId;
if (UtilValidate.isNotEmpty(srStatusId) && slaTatStopStatusList.contains(srStatusId)) {
	GenericValue slaTatInfo = EntityQuery.use(delegator).from("CustRequestAttribute").where("custRequestId", srNumber, "attrName", "SLA_TAT").queryFirst();
	if (UtilValidate.isNotEmpty(slaTatInfo)) {
		slaTat = slaTatInfo.get("attrValue");
	}
} else {
	slaTat = org.groupfio.common.portal.util.DataHelper.getSrTatCount(delegator, srNumber);

	/*
	Map inpCxt = UtilMisc.toMap("delegator",delegator,"closedDate",org.fio.homeapps.util.UtilDateTime.nowTimestamp(),"custRequestId", srNumber,"srStatuId",custRequest.statusId);
	int tatDays = SlaTatResolver.prepareTatToDate(inpCxt);
	println"--Dynamic---"+tatDays;
	slaTat = ""+tatDays;
	*/
}
kpiBarContext.put("sr-sla-tat", slaTat);
context.put("kpiBarContext", kpiBarContext);

String srClosedDateStr = "";
if (UtilValidate.isNotEmpty(srClosedDate)) {
	srClosedDateStr = df1.format(df.parse(srClosedDate))
}
context.put("srClosedDate", srClosedDateStr);

context.put("custRequestId", srNumber);
context.put("domainEntityId", srNumber);
context.put("partyId", PartyId);

context.put("domainEntityType", "SERVICE_REQUEST");
context.put("requestURI", "viewServiceRequest");


EntityCondition searchConditions = EntityCondition.makeCondition(EntityOperator.AND,
		EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, PartyId),
		EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, UtilMisc.toList("ACCOUNT", "LEAD", "CUSTOMER")));

GenericValue partyRoleData = EntityUtil.getFirst(delegator.findList("PartyRole", searchConditions,null, null, null, false));
roleType = "";
if(UtilValidate.isNotEmpty(partyRoleData)){
	roleType = partyRoleData.roleTypeId
	if(roleType !=null){
		if(roleType.equals("ACCOUNT")){
			inputContext.put("cNo_link","/account-portal/control/viewAccount?partyId="+partyRoleData.partyId+"&externalLoginKey="+externalLoginKey);
		} else if(roleType.equals("LEAD")){
			inputContext.put("cNo_link","/lead-portal/control/viewLead?partyId="+partyRoleData.partyId+"&externalLoginKey="+externalLoginKey);
		} else if(roleType.equals("CUSTOMER")){
			inputContext.put("cNo_link","/customer-portal/control/viewCustomer?partyId="+partyRoleData.partyId+"&externalLoginKey="+externalLoginKey);
		}
	}
}

Map<String, Object> contactAcctMap = new HashMap<String, Object>();
contactAcctMap.put("partyIdTo", PartyId);
contactAcctMap.put("partyRoleTypeId", roleType);
context.put("srFromPartyId", PartyId);
context.put("partyId", PartyId);
context.put("partyRoleTypeId", roleType);

Map<String, Object> result = dispatcher.runSync("common.getContactAndPartyAssoc", contactAcctMap);
partyContactAssocList = [];
String relatedPartiesEmailIds = "";
if(ServiceUtil.isSuccess(result)){
	partyContactAssoc= result.partyContactAssoc

	primContactName = "";
	primContactId = "";
	List<String> primContactList = new LinkedList<String>();
	for(int i=0;i<partyContactAssoc.size();i++){
		Map < String, Object > partyContactMap = new HashMap < String, Object > ();
		partyContactMap = (Map<String, Object>) partyContactAssoc.get(i);
		contactPartyId = partyContactMap.get("partyId")
		custRequestContact = from("CustRequestContact").where("custRequestId", srNumber,"partyId",contactPartyId, "thruDate", null, "roleTypeId", "CONTACT", "isPrimary", "Y" ).queryList();
		/*primaryContactInformation = PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator,contactPartyId);
		 if(UtilValidate.isNotEmpty(primaryContactInformation)) {
		 if(UtilValidate.isNotEmpty(primaryContactInformation.get("EmailAddress"))) {
		 relatedPartiesEmailIds += primaryContactInformation.get("EmailAddress")+",";
		 }
		 }*/

		if(UtilValidate.isNotEmpty(custRequestContact)){
			isPrimaryContact = custRequestContact.get(0).get("isPrimary");
			if(UtilValidate.isNotEmpty(isPrimaryContact) && isPrimaryContact.equals("Y")){
				partyContactMap.put("statusId", "PARTY_DEFAULT")
				primContactName = partyContactMap.get("name")
				primContactId = partyContactMap.get("partyId")
				primContactList.add(primContactId);

			}else{
				partyContactMap.put("statusId", "")
			}
			partyContactAssocList.add(partyContactMap);

		}

	}
	context.partyContactAssocList = partyContactAssocList;
	context.primContactName = primContactName;
	context.primContactId = org.fio.admin.portal.util.DataUtil.listToString(primContactList);
	/*if(UtilValidate.isNotEmpty(relatedPartiesEmailIds)) {
	 relatedPartiesEmailIds = relatedPartiesEmailIds.substring(0,relatedPartiesEmailIds.length()-1);
	 }
	 context.relatedPartiesEmailIds = relatedPartiesEmailIds;*/
}

String loggedInUserEmailId = "";
if(UtilValidate.isNotEmpty(srNumber)){
	if(UtilValidate.isNotEmpty(userLogin) && UtilValidate.isNotEmpty(userLogin.getString("partyId"))) {
		String emailAddress = org.groupfio.common.portal.util.UtilContactMech.getPartyEmail(delegator, userLogin.getString("partyId"), null);
		if(UtilValidate.isNotEmpty(emailAddress)) {
			loggedInUserEmailId = emailAddress;
		}
	}
	custRequestContactList = from("CustRequestContact").where("custRequestId", srNumber, "thruDate", null, "roleTypeId", "CONTACT" ).queryList();
	if(UtilValidate.isNotEmpty(custRequestContactList)){
		custRequestContactList.each { eachCustContact ->
			ContactPartyId = eachCustContact.partyId;
			if(UtilValidate.isNotEmpty(ContactPartyId)){
				emailAddress = org.groupfio.common.portal.util.UtilContactMech.getPartyEmail(delegator, ContactPartyId, null);
				if(UtilValidate.isNotEmpty(emailAddress)) {
					if(UtilValidate.isNotEmpty(loggedInUserEmailId) && loggedInUserEmailId != emailAddress){
						relatedPartiesEmailIds += emailAddress+",";
					}
				}
			}
		}
	}

	supportEmailAddress = delegator.findOne("PretailLoyaltyGlobalParameters", UtilMisc.toMap("parameterId", "TO_EMAIL_ID"), false);
	if (UtilValidate.isNotEmpty(supportEmailAddress) && supportEmailAddress.getString("value")) {
		relatedPartiesEmailIds += supportEmailAddress.getString("value")+",";
	}
}
if(UtilValidate.isNotEmpty(relatedPartiesEmailIds)) {
	relatedPartiesEmailIds = relatedPartiesEmailIds.substring(0,relatedPartiesEmailIds.length()-1);
}
context.relatedPartiesEmailIds = relatedPartiesEmailIds;

if(UtilValidate.isNotEmpty(srNumber)) {
	List<GenericValue> reasonCodeList = EntityQuery.use(delegator).from("CustRequestResolution").where("custRequestId", srNumber,"custRequestTypeId","REASON_CODE").queryList();
	if(UtilValidate.isNotEmpty(reasonCodeList)) {
		List<String> reasonIds = EntityUtil.getFieldListFromEntityList(reasonCodeList, "reasonId", true);
		List<String> reasonDescs = EntityUtil.getFieldListFromEntityList(reasonCodeList, "description", true);
		if (request.getRequestURI().contains("updateServiceRequest") || request.getRequestURI().contains("createServiceRequest")) {
			inputContext.put("reasonCode", UtilValidate.isNotEmpty(reasonIds) ? org.fio.admin.portal.util.DataUtil.listToString(reasonIds): "" );
		}
		if (request.getRequestURI().contains("viewServiceRequest")) {
			inputContext.put("reasonCode", UtilValidate.isNotEmpty(reasonDescs) ? org.fio.admin.portal.util.DataUtil.listToString(reasonDescs): "" );
			inputContext.put("reasonCode_desc", UtilValidate.isNotEmpty(reasonDescs) ? org.fio.admin.portal.util.DataUtil.listToString(reasonDescs): "" );
		}
		inputContext.put("reasonIds", UtilValidate.isNotEmpty(reasonIds) ? org.fio.admin.portal.util.DataUtil.listToString(reasonIds): "" );
	}

	List<GenericValue> causeCategoryList = EntityQuery.use(delegator).from("CustRequestResolution").where("custRequestId", srNumber,"custRequestTypeId","CAUSE_CATEGORY").queryList();
	if(UtilValidate.isNotEmpty(causeCategoryList)) {
		List<String> causeCategoryIds = EntityUtil.getFieldListFromEntityList(causeCategoryList, "causeCategoryId", true);
		List<String> causeCategoryDescs = EntityUtil.getFieldListFromEntityList(causeCategoryList, "description", true);

		if (request.getRequestURI().contains("updateServiceRequest") || request.getRequestURI().contains("createServiceRequest")) {
			inputContext.put("causeCategory", UtilValidate.isNotEmpty(causeCategoryIds) ? org.fio.admin.portal.util.DataUtil.listToString(causeCategoryIds): "" );
		}
		if (request.getRequestURI().contains("viewServiceRequest")) {
			inputContext.put("causeCategory", UtilValidate.isNotEmpty(causeCategoryDescs) ? org.fio.admin.portal.util.DataUtil.listToString(causeCategoryDescs): "" );
			inputContext.put("causeCategory_desc", UtilValidate.isNotEmpty(causeCategoryDescs) ? org.fio.admin.portal.util.DataUtil.listToString(causeCategoryDescs): "" );
		}
		inputContext.put("causeCategoryIds", UtilValidate.isNotEmpty(causeCategoryIds) ? org.fio.admin.portal.util.DataUtil.listToString(causeCategoryIds): "" );
		
		GenericValue causeCategoryGv = EntityQuery.use(delegator).from("Enumeration").where("enumTypeId", "CAUSE_CATAGORY_TYPE", "description","Customer Dispute","isEnabled","Y").queryFirst();
		String customerDispute = UtilValidate.isNotEmpty(causeCategoryGv) && UtilValidate.isNotEmpty(causeCategoryGv.getString("enumId")) ? causeCategoryGv.getString("enumId"): "";
		if(causeCategoryIds.contains(customerDispute)) {
			inputContext.put("isCustDisputeExist", "Y");
		}
	}
	GenericValue srHomeOwner = EntityQuery.use(delegator).from("CustRequestParty").where("custRequestId", srNumber, "roleTypeId", "CUSTOMER", "thruDate",null).filterByDate().queryFirst();
	if(UtilValidate.isNotEmpty(srHomeOwner)) {
		String customerId = srHomeOwner.getString("partyId");
		if (request.getRequestURI().contains("updateServiceRequest") || request.getRequestURI().contains("createServiceRequest")) {
			inputContext.put("customerId", UtilValidate.isNotEmpty(customerId) ? customerId: "" );
			//GenericValue person = EntityQuery.use(delegator).from("Person").where("partyId", customerId).queryFirst();
			String customerName = org.fio.homeapps.util.PartyHelper.getPartyName(delegator, customerId, false);
			inputContext.put("customerId_desc", UtilValidate.isNotEmpty(customerName) ? customerName : "" );

		}
		if (request.getRequestURI().contains("viewServiceRequest") || request.getRequestURI().contains("createPhoneCallActivity") || request.getRequestURI().contains("viewAllSrNotes") || request.getRequestURI().contains("viewAllNotes")) {
			//GenericValue person = EntityQuery.use(delegator).from("Person").where("partyId", customerId).queryFirst();
			customerName = org.fio.homeapps.util.PartyHelper.getPartyName(delegator, customerId, false);
			inputContext.put("customerId", UtilValidate.isNotEmpty(customerName) ? customerName : "" );
			inputContext.put("customerId_link","/customer-portal/control/viewCustomer?partyId="+customerId+"&externalLoginKey="+externalLoginKey);
			/*
			if(UtilValidate.isNotEmpty(primaryVal) && "HOME".equals(primaryVal)){
				actionBarContext.put("primaryEmail","");
				actionBarContext.put("primaryPhone", "");
				primaryContactCustInformation = PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator,customerId);
				if(UtilValidate.isNotEmpty(primaryContactCustInformation)) {
					actionBarContext.put("primaryEmail",primaryContactCustInformation.get("EmailAddress"));
					actionBarContext.put("emailSolicitation",primaryContactCustInformation.get("emailSolicitation"));
					custPhoneNumber=primaryContactCustInformation.get("PrimaryPhone");
					if(UtilValidate.isEmpty(custPhoneNumber)){
						if(UtilValidate.isNotEmpty(custRequestSupplementory)){
							custPhoneNumber=custRequestSupplementory.get("mobileNumber");
							if(UtilValidate.isEmpty(custPhoneNumber)){
								custPhoneNumber=custRequestSupplementory.get("offPhoneNumber");
							}
							if(UtilValidate.isEmpty(custPhoneNumber)){
								custPhoneNumber=custRequestSupplementory.get("homePhoneNumber");
							}

						}
					}
					actionBarContext.put("primaryPhone", org.fio.admin.portal.util.DataUtil.formatPhoneNumber(custPhoneNumber));
					actionBarContext.put("phoneSolicitation",primaryContactCustInformation.get("phoneSolicitation"));
					if (request.getRequestURI().contains("createPhoneCallActivity")) {
						context.put("telePhoneLink","tel://"+custPhoneNumber);
						context.put("telePhoneNumber",custPhoneNumber);
					}
				}
				context.put("partyIdVal",customerId);
				String roleDesc = org.fio.homeapps.util.DataUtil.getRoleTypeDesc(delegator, "CUSTOMER");
				if("Customer".equals(roleDesc)) roleDesc = "Homeowner";
				actionBarContext.put("name", (UtilValidate.isNotEmpty(customerName) ? customerName : "") + (UtilValidate.isNotEmpty(roleDesc) ? " ("+roleDesc+")":"") );
			}
			*/
		}
	}
	
	GenericValue contractor = EntityQuery.use(delegator).from("CustRequestParty").where("custRequestId", srNumber, "roleTypeId", "CONTRACTOR", "thruDate",null).filterByDate().queryFirst();
	if(UtilValidate.isNotEmpty(contractor)) {
		String contractorId = contractor.getString("partyId");
		if (request.getRequestURI().contains("updateServiceRequest")|| request.getRequestURI().contains("createServiceRequest")) {
			inputContext.put("contractorId", UtilValidate.isNotEmpty(contractorId) ? contractorId: "" );
			//GenericValue person = EntityQuery.use(delegator).from("Person").where("partyId", contractorId).queryFirst();
			contractorName = org.fio.homeapps.util.PartyHelper.getPartyName(delegator, contractorId, false);
			inputContext.put("contractorId_desc", UtilValidate.isNotEmpty(contractorName) ? contractorName : "" );
		}
		if (request.getRequestURI().contains("viewServiceRequest") || request.getRequestURI().contains("createPhoneCallActivity") || request.getRequestURI().contains("viewAllSrNotes") || request.getRequestURI().contains("viewAllNotes")) {
			//GenericValue person = EntityQuery.use(delegator).from("Person").where("partyId", contractorId).queryFirst();
			contractorName = org.fio.homeapps.util.PartyHelper.getPartyName(delegator, contractorId, false);
			inputContext.put("contractorId", UtilValidate.isNotEmpty(contractorName) ? contractorName : "" );
			/*
			if(UtilValidate.isNotEmpty(primaryVal) && "CONTRACTOR".equals(primaryVal)){
				actionBarContext.put("primaryEmail","");
				actionBarContext.put("primaryPhone", "");
				primaryContactCustInformation = PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator,contractorId);
				if(UtilValidate.isNotEmpty(primaryContactCustInformation)) {
					actionBarContext.put("primaryEmail",primaryContactCustInformation.get("EmailAddress"));
					actionBarContext.put("emailSolicitation",primaryContactCustInformation.get("emailSolicitation"));
					actionBarContext.put("phoneSolicitation",primaryContactCustInformation.get("phoneSolicitation"));
					phoneNumber=primaryContactCustInformation.get("PrimaryPhone");
					if(UtilValidate.isEmpty(phoneNumber)){
						if(UtilValidate.isNotEmpty(custRequestSupplementory)){
							phoneNumber=custRequestSupplementory.get("contractorMobilePhone");
							if(UtilValidate.isEmpty(phoneNumber)){
								phoneNumber=custRequestSupplementory.get("contractorOffPhone");
							}
						}
					}
					actionBarContext.put("primaryPhone", org.fio.admin.portal.util.DataUtil.formatPhoneNumber(phoneNumber));
					if (request.getRequestURI().contains("createPhoneCallActivity")) {
						context.put("telePhoneLink","tel://"+phoneNumber);
						context.put("telePhoneNumber",phoneNumber);
					}
				}
				actionBarContext.put("partyId",contractorId);
				context.put("partyIdVal",contractorId);
				String roleDesc = org.fio.homeapps.util.DataUtil.getRoleTypeDesc(delegator, "CONTRACTOR");
				actionBarContext.put("name", (UtilValidate.isNotEmpty(contractorName) ? contractorName : "") + (UtilValidate.isNotEmpty(roleDesc) ? " ("+roleDesc+")":"") );

			}
			*/
			if(UtilValidate.isNotEmpty(contractorId)){
				inputContext.put("contractorId_link","/customer-portal/control/viewCustomer?partyId="+contractorId+"&externalLoginKey="+externalLoginKey);
			}
		}
	}
	
	//GenericValue dealerContact = EntityQuery.use(delegator).from("CustRequestContact").where("custRequestId", srNumber, "roleTypeId", "CONTACT", "thruDate",null).filterByDate().queryFirst();
	if(UtilValidate.isNotEmpty(primContactId)) {
		if (request.getRequestURI().contains("createPhoneCallActivity")) {
			if(UtilValidate.isNotEmpty(primaryVal) && "DEALER".equals(primaryVal)){
				primaryContactCustInformation = PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator,primContactId);
				if(UtilValidate.isNotEmpty(primaryContactCustInformation)) {
					actionBarContext.put("primaryEmail",primaryContactCustInformation.get("EmailAddress"));
					actionBarContext.put("emailSolicitation",primaryContactCustInformation.get("emailSolicitation"));
					actionBarContext.put("phoneSolicitation",primaryContactCustInformation.get("phoneSolicitation"));
					phoneNumber=primaryContactCustInformation.get("PrimaryPhone");
					if(UtilValidate.isNotEmpty(phoneNumber)){
						actionBarContext.put("primaryPhone", org.fio.admin.portal.util.DataUtil.formatPhoneNumber(phoneNumber));
						if (request.getRequestURI().contains("createPhoneCallActivity")) {
							context.put("telePhoneLink","tel://"+phoneNumber);
							context.put("telePhoneNumber",phoneNumber);
						}
					}
				}
				/*
				actionBarContext.put("partyId",contractorId);
				context.put("partyIdVal",contractorId);
				String roleDesc = org.fio.homeapps.util.DataUtil.getRoleTypeDesc(delegator, "CONTRACTOR");
				actionBarContext.put("name", (UtilValidate.isNotEmpty(contractorName) ? contractorName : "") + (UtilValidate.isNotEmpty(roleDesc) ? " ("+roleDesc+")":"") );
				*/
			}
		}
	}
	/*
	GenericValue salesRep = EntityQuery.use(delegator).from("CustRequestParty").where("custRequestId", srNumber, "roleTypeId", "SALES_REP", "thruDate",null).orderBy("lastUpdatedTxStamp DESC").queryFirst();
	if(UtilValidate.isNotEmpty(salesRep)){
		String salesRepPartyId = salesRep.getString("partyId");
		salesUserLogin = EntityUtil.getFirst( delegator.findList("UserLogin", EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, salesRepPartyId), null, null, null, false) );
		salePersonUserName = org.fio.homeapps.util.PartyHelper.getUserLoginName(delegator, salesUserLogin.get("userLoginId"), false);
		inputContext.put("salesPerson", salesRepPartyId);
		inputContext.put("salesPersonDesc", salePersonUserName);
	}
	*/
	
	Map<String, Object> anchorPartyMap = DataHelper.getCustRequestAnchorParties(delegator, srNumber);
	if(UtilValidate.isNotEmpty(anchorPartyMap)) {
		println ("anchorPartyMap----------->"+anchorPartyMap);
		String salesPersonId = anchorPartyMap.get("SALES_REP");
		String primaryTechnicianId = anchorPartyMap.get("TECHNICIAN");
		String accountId = anchorPartyMap.get("ACCOUNT");
		String primaryContactId = anchorPartyMap.get("CONTACT");
		String customerId = anchorPartyMap.get("CUSTOMER");
		String contractorId = anchorPartyMap.get("CONTRACTOR");
		
		if (request.getRequestURI().contains("updateServiceRequest") || request.getRequestURI().contains("createServiceRequest")) {
			inputContext.put("salesPerson", UtilValidate.isNotEmpty(salesPersonId) ? salesPersonId : "" );
		}
		if (request.getRequestURI().contains("viewServiceRequest")) {
			inputContext.put("salesPerson", UtilValidate.isNotEmpty(salesPersonId) ? org.ofbiz.party.party.PartyHelper.getPartyName(delegator, salesPersonId, false) : "" );
		}
		
		if (request.getRequestURI().contains("updateServiceRequest") || request.getRequestURI().contains("createServiceRequest")) {
			inputContext.put("primaryTechnician", UtilValidate.isNotEmpty(primaryTechnicianId) ? primaryTechnicianId : "" );
		}
		if (request.getRequestURI().contains("viewServiceRequest")) {
			inputContext.put("primaryTechnician", UtilValidate.isNotEmpty(primaryTechnicianId) ? org.ofbiz.party.party.PartyHelper.getPartyName(delegator, primaryTechnicianId, false) : "" );
		}
		
		if (request.getRequestURI().contains("updateServiceRequest") || request.getRequestURI().contains("createServiceRequest")) {
			inputContext.put("contractorId", UtilValidate.isNotEmpty(contractorId) ? contractorId: "" );
			contractorName = org.fio.homeapps.util.PartyHelper.getPartyName(delegator, contractorId, false);
			inputContext.put("contractorId_desc", UtilValidate.isNotEmpty(contractorName) ? contractorName : "" );
		}
		if (request.getRequestURI().contains("viewServiceRequest")) {
			inputContext.put("contractorId", UtilValidate.isNotEmpty(contractorId) ? org.ofbiz.party.party.PartyHelper.getPartyName(delegator, contractorId, false) : "" );
			inputContext.put("contractorId_link","/customer-portal/control/viewCustomer?partyId="+contractorId+"&externalLoginKey="+externalLoginKey);
			context.put("existContractorId", contractorId);
		}
		
		if (request.getRequestURI().contains("updateServiceRequest") || request.getRequestURI().contains("createServiceRequest")) {
			inputContext.put("customerId", UtilValidate.isNotEmpty(customerId) ? customerId: "" );
			String customerName = org.fio.homeapps.util.PartyHelper.getPartyName(delegator, customerId, false);
			inputContext.put("customerId_desc", UtilValidate.isNotEmpty(customerName) ? customerName : "" );
		}
		if (request.getRequestURI().contains("viewServiceRequest")) {
			inputContext.put("customerId", UtilValidate.isNotEmpty(customerId) ? org.ofbiz.party.party.PartyHelper.getPartyName(delegator, customerId, false) : "" );
			inputContext.put("customerId_link","/customer-portal/control/viewCustomer?partyId="+customerId+"&externalLoginKey="+externalLoginKey);
			context.put("existCustomerId", customerId);
		}
		/*
		if (request.getRequestURI().contains("updateServiceRequest") || request.getRequestURI().contains("createServiceRequest")) {
			inputContext.put("primaryContactDesc", UtilValidate.isNotEmpty(primaryContactId) ? primaryContactId: "" );
			String contactName = org.fio.homeapps.util.PartyHelper.getPartyName(delegator, primaryContactId, false);
			inputContext.put("primaryContactDesc", UtilValidate.isNotEmpty(contactName) ? contactName : "" );
			
			inputContext.put("primaryContactDesc", UtilValidate.isNotEmpty(primaryContactId) ? org.ofbiz.party.party.PartyHelper.getPartyName(delegator, primaryContactId, false) : "" );

		}
		if (request.getRequestURI().contains("viewServiceRequest")) {
			inputContext.put("primaryContactDesc", UtilValidate.isNotEmpty(primaryContactId) ? org.ofbiz.party.party.PartyHelper.getPartyName(delegator, primaryContactId, false) : "" );
			inputContext.put("primaryContactDesc_link","/contact-portal/control/viewContact?partyId="+primaryContactId+"&externalLoginKey="+externalLoginKey);
			
		}
		*/
		if (request.getRequestURI().contains("updateServiceRequest") || request.getRequestURI().contains("createServiceRequest")) {
			inputContext.put("cNo", UtilValidate.isNotEmpty(accountId) ? accountId: "" );
			String accName = org.fio.homeapps.util.PartyHelper.getPartyName(delegator, accountId, false);
			inputContext.put("cNo_desc", UtilValidate.isNotEmpty(accName) ? accName : "" );
		}
		if (request.getRequestURI().contains("viewServiceRequest")) {
			inputContext.put("cNo", UtilValidate.isNotEmpty(accountId) ? accountId: "" );
			inputContext.put("cNo_desc", UtilValidate.isNotEmpty(accountId) ? org.ofbiz.party.party.PartyHelper.getPartyName(delegator, accountId, false) : "" );
			inputContext.put("cNo_link","/account-portal/control/viewAccount?partyId="+accountId+"&externalLoginKey="+externalLoginKey);
		}
		
		if(UtilValidate.isNotEmpty(primaryVal)) {
			if("DEALER".equals(primaryVal)) {
				String priRoleDesc = "Dealer";
				actionBarContext.put("name","");
				actionBarContext.put("primaryEmail","");
				actionBarContext.put("primaryPhone", "");
				String dealerId = UtilValidate.isNotEmpty(accountId) ? accountId : "";
				if(UtilValidate.isNotEmpty(primaryContactId)) {
					String primContactId = "";
					List<String> primaryContactList = new LinkedList<String>();
					if(primaryContactId.contains(","))
						primaryContactList = org.fio.admin.portal.util.DataUtil.stringToList(primaryContactId, ",");
					else
						primaryContactList.add(primaryContactId);
						
					Map<String, Object> primaryContactDetails = DataUtil.getPrimaryContactInfo(delegator, dealerId, "ACCOUNT");
					if(UtilValidate.isNotEmpty(primaryContactDetails)) {
						String contactId = primaryContactDetails.get("contactId");
						if(primaryContactList.contains(contactId))
							primContactId = contactId;
						else
							primContactId = primaryContactList.get(0);
					}
					
					dealerId = primContactId;
					context.put("partyIdVal",primContactId);	
				}
				else {
					dealerId = accountId;
					context.put("partyIdVal",accountId);
				}
				
				actionBarContext.put("partyId",dealerId);
				String dealerName = org.fio.homeapps.util.PartyHelper.getPartyName(delegator, dealerId, false);
				primaryContactCustInformation = PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator,dealerId);
				
				if(UtilValidate.isNotEmpty(primaryContactCustInformation)) {
					actionBarContext.put("primaryEmail",primaryContactCustInformation.get("EmailAddress"));
					actionBarContext.put("emailSolicitation",primaryContactCustInformation.get("emailSolicitation"));
					custPhoneNumber=primaryContactCustInformation.get("PrimaryPhone");
					if(UtilValidate.isEmpty(custPhoneNumber)){
						if(UtilValidate.isNotEmpty(custRequestSupplementory)){
							custPhoneNumber=custRequestSupplementory.get("mobileNumber");
							if(UtilValidate.isEmpty(custPhoneNumber)){
								custPhoneNumber=custRequestSupplementory.get("offPhoneNumber");
							}
							if(UtilValidate.isEmpty(custPhoneNumber)){
								custPhoneNumber=custRequestSupplementory.get("homePhoneNumber");
							}

						}
					}
					actionBarContext.put("primaryPhone", org.fio.admin.portal.util.DataUtil.formatPhoneNumber(custPhoneNumber));
					actionBarContext.put("phoneSolicitation",primaryContactCustInformation.get("phoneSolicitation"));
					context.put("primaryPhone", org.fio.admin.portal.util.DataUtil.formatPhoneNumber(custPhoneNumber));
					if (request.getRequestURI().contains("createPhoneCallActivity")) {
						context.put("telePhoneLink","tel://"+custPhoneNumber);
						context.put("telePhoneNumber",custPhoneNumber);
					}
				}
				actionBarContext.put("name", (UtilValidate.isNotEmpty(dealerName) ? dealerName : "") + (UtilValidate.isNotEmpty(priRoleDesc) ? " ("+priRoleDesc+")":"") );
				
			} else if("HOME".equals(primaryVal)) {
				context.put("partyIdVal",customerId);
				actionBarContext.put("partyId",customerId);
				
				actionBarContext.put("name","");
				actionBarContext.put("primaryEmail","");
				actionBarContext.put("primaryPhone", "");
				String homeName = org.fio.homeapps.util.PartyHelper.getPartyName(delegator, customerId, false);
				primaryContactCustInformation = PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator,customerId);
				if(UtilValidate.isNotEmpty(primaryContactCustInformation)) {
					actionBarContext.put("primaryEmail",primaryContactCustInformation.get("EmailAddress"));
					actionBarContext.put("emailSolicitation",primaryContactCustInformation.get("emailSolicitation"));
					custPhoneNumber=primaryContactCustInformation.get("PrimaryPhone");
					if(UtilValidate.isEmpty(custPhoneNumber)){
						if(UtilValidate.isNotEmpty(custRequestSupplementory)){
							custPhoneNumber=custRequestSupplementory.get("mobileNumber");
							if(UtilValidate.isEmpty(custPhoneNumber)){
								custPhoneNumber=custRequestSupplementory.get("offPhoneNumber");
							}
							if(UtilValidate.isEmpty(custPhoneNumber)){
								custPhoneNumber=custRequestSupplementory.get("homePhoneNumber");
							}

						}
					}
					actionBarContext.put("primaryPhone", org.fio.admin.portal.util.DataUtil.formatPhoneNumber(custPhoneNumber));
					actionBarContext.put("phoneSolicitation",primaryContactCustInformation.get("phoneSolicitation"));
					if (request.getRequestURI().contains("createPhoneCallActivity")) {
						context.put("telePhoneLink","tel://"+custPhoneNumber);
						context.put("telePhoneNumber",custPhoneNumber);
					}
				}
				String roleDesc = org.fio.homeapps.util.DataUtil.getRoleTypeDesc(delegator, "CUSTOMER");
				if("Customer".equals(roleDesc)) roleDesc = "Homeowner";
				actionBarContext.put("name", (UtilValidate.isNotEmpty(homeName) ? homeName : "") + (UtilValidate.isNotEmpty(roleDesc) ? " ("+roleDesc+")":"") );
			} else if("CONTRACTOR".equals(primaryVal)) {
				context.put("partyIdVal",contractorId);
				actionBarContext.put("name","");
				actionBarContext.put("primaryPhone", "");
				actionBarContext.put("primaryPhone", "");
				String contractorName = org.fio.homeapps.util.PartyHelper.getPartyName(delegator, contractorId, false);
				primaryContactCustInformation = PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator,contractorId);
				if(UtilValidate.isNotEmpty(primaryContactCustInformation)) {
					actionBarContext.put("primaryEmail",primaryContactCustInformation.get("EmailAddress"));
					actionBarContext.put("emailSolicitation",primaryContactCustInformation.get("emailSolicitation"));
					actionBarContext.put("phoneSolicitation",primaryContactCustInformation.get("phoneSolicitation"));
					phoneNumber=primaryContactCustInformation.get("PrimaryPhone");
					if(UtilValidate.isEmpty(phoneNumber)){
						if(UtilValidate.isNotEmpty(custRequestSupplementory)){
							phoneNumber=custRequestSupplementory.get("contractorMobilePhone");
							if(UtilValidate.isEmpty(phoneNumber)){
								phoneNumber=custRequestSupplementory.get("contractorOffPhone");
							}
						}
					}
					actionBarContext.put("primaryPhone", org.fio.admin.portal.util.DataUtil.formatPhoneNumber(phoneNumber));
					if (request.getRequestURI().contains("createPhoneCallActivity")) {
						context.put("telePhoneLink","tel://"+phoneNumber);
						context.put("telePhoneNumber",phoneNumber);
					}
				}
				actionBarContext.put("partyId",contractorId);
				
				String roleDesc = org.fio.homeapps.util.DataUtil.getRoleTypeDesc(delegator, "CONTRACTOR");
				actionBarContext.put("name", (UtilValidate.isNotEmpty(contractorName) ? contractorName : "") + (UtilValidate.isNotEmpty(roleDesc) ? " ("+roleDesc+")":"") );

			}
		}
	}
}

context.put("actionBarContext", actionBarContext);

String workStartTime = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "WORK_START_TIME");
String workEndTime = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "WORK_END_TIME");
context.put("workStartTime", workStartTime);
context.put("workEndTime", workEndTime);

custRequestWorkEffortList = EntityQuery.use(delegator).from("CustRequestWorkEffort").where("custRequestId", srNumber).queryList();
workEffortIds = EntityUtil.getFieldListFromEntityList(custRequestWorkEffortList, "workEffortId", true);
isAllowToCloseSR="Y";
workEffortNameStr="";
/*
 if(UtilValidate.isNotEmpty(workEffortIds)){
 conditionList = FastList.newInstance();
 conditionList.add(EntityCondition.makeCondition("workEffortId", EntityOperator.IN, workEffortIds));
 conditionList.add(EntityCondition.makeCondition("currentStatusId", EntityOperator.EQUALS, "IA_MCOMPLETED"));
 conditionList.add(EntityCondition.makeCondition("rateTypeId", EntityOperator.IN, UtilMisc.toList("LABOR", "TRAVEL")));
 EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
 //workEffortTimeEntries =  delegator.findList("WorkEffortAndTimeEntry", mainConditons, null, null, null, false);	
 long count = delegator.findCountByCondition("WorkEffortAndTimeEntry", mainConditons, null, null);
 if (count > 0) {
 isAllowToCloseSR="N";
 /*partyIds = EntityUtil.getFieldListFromEntityList(workEffortTimeEntries, "partyId", true);	
 conditionList1 = FastList.newInstance();
 conditionList1.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, partyIds));
 conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "TECHNICIAN"));
 EntityCondition mainConditons1 = EntityCondition.makeCondition(conditionList1, EntityOperator.AND);
 partyRolesList =  delegator.findList("PartyRole", mainConditons1, null, null, null, false);
 if(UtilValidate.isNotEmpty(partyRolesList)){
 workEffortTimeEntries.each { timeEntry ->			   
 if((UtilValidate.isEmpty(timeEntry.get("hours")) || timeEntry.get("hours").equals(BigDecimal.ZERO)) || (UtilValidate.isEmpty(timeEntry.get("cost"))||timeEntry.get("cost").equals(BigDecimal.ZERO))){
 workEffortNameStr += timeEntry.get("workEffortName")+",";
 isAllowToCloseSR="N";
 }
 }
 }*
 }
 }
 */
if(UtilValidate.isNotEmpty(workEffortIds)){
	conditionList = FastList.newInstance();
	conditionList.add(EntityCondition.makeCondition("workEffortId", EntityOperator.IN, workEffortIds));
	conditionList.add(EntityCondition.makeCondition("currentStatusId", EntityOperator.NOT_IN, UtilMisc.toList("IA_COMPLETED", "IA_MCOMPLETED","IA_CANCEL")));

	long openActivityCount = EntityQuery.use(delegator).from("WorkEffort").where(EntityCondition.makeCondition(conditionList, EntityOperator.AND)).queryCount();
	if(openActivityCount > 0) context.put("isOpenActExists" ,"Y"); else context.put("isOpenActExists" ,"N");

	conditionList = FastList.newInstance();
	conditionList.add(EntityCondition.makeCondition("workEffortId", EntityOperator.IN, workEffortIds));
	conditionList.add(EntityCondition.makeCondition("currentStatusId", EntityOperator.EQUALS, "IA_MCOMPLETED"));
	conditionList.add(EntityCondition.makeCondition("rateTypeId", EntityOperator.IN, UtilMisc.toList("LABOR", "TRAVEL")));

	long count = EntityQuery.use(delegator).from("WorkEffortAndTimeEntry").where(EntityCondition.makeCondition(conditionList, EntityOperator.AND)).queryCount();

	if(count <= 0)  isAllowToCloseSR = "N";

}

context.put("isAllowToCloseSR",isAllowToCloseSR);
context.put("workEffortNameStr",workEffortNameStr);

//println("context>>>>"+context);

isOpenOrderAssocTab = session.getAttribute("isOpenOrderAssocTab");
if(UtilValidate.isNotEmpty(isOpenOrderAssocTab)){
	context.put("isOpenOrderAssocTab", isOpenOrderAssocTab);
	session.setAttribute("isOpenOrderAssocTab", null);
}

String isEnableRebateModule = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "ENABLE_REBATE_MODULE");
if(UtilValidate.isNotEmpty(isEnableRebateModule)){
	context.put("isEnableRebateModule", isEnableRebateModule);
}

conditionsList = [];
conditionsList.add(EntityCondition.makeCondition("statusTypeId", EntityOperator.EQUALS, "SR_STATUS_ID"));
conditionsList.add(EntityCondition.makeCondition("isEnabled", EntityOperator.EQUALS, "Y"));
mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);
statusList = delegator.findList("StatusItem", mainConditons, null, UtilMisc.toList("sequenceId"), null, false);
//context.put("srStatusList", org.fio.homeapps.util.DataHelper.getDropDownOptions(statusList, "statusId", "description"));
context.put("srStatusList", statusList);

if(UtilValidate.isNotEmpty(userLogin)) {
	List<String> srTabs = new ArrayList<String>(Arrays.asList("sr-details", "customFields", "sr-communication-history","sr-activities","sr-orders", "sr-notes", "sr-attachments", "sr-administration", "sr-history", "sr-time-entry", "sr-issue-material", "associated-parties", "approval", "survey", "thirdpty-attachment"));
	String userLoginPartyId = userLogin.getString("partyId");
	String userLoginRoleTypeId = org.fio.admin.portal.util.DataUtil.getPartySecurityRole(delegator, userLoginPartyId);
	
	String availableTabs = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, userLoginRoleTypeId+"_SR_TABS");
	println ("availableTabs---->"+availableTabs);
	Map<String, Object> srTabMap = new HashMap<String, Object>();
	if(UtilValidate.isNotEmpty(availableTabs)) {
		List<String> avalTabList = org.fio.admin.portal.util.DataUtil.stringToList(availableTabs, ",");
		for(String tabId : srTabs) {
			if(avalTabList.contains(tabId))
				srTabMap.put(tabId, "Y");
			else
				srTabMap.put(tabId, "N");	
		}
	} else {
		for(String tabId : srTabs) {
			srTabMap.put(tabId, "Y");
		}
	}
	println ("srTabMap---->"+srTabMap);
	context.put("srTabMap", srTabMap);
	
	List<String> roles = new ArrayList<>();
	String globalConfig = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SR_OWNER");
	if (UtilValidate.isNotEmpty(globalConfig) && globalConfig.contains(",")) {
		roles = org.fio.admin.portal.util.DataUtil.stringToList(globalConfig, ",");
	} else if (UtilValidate.isNotEmpty(globalConfig)) {
		roles.add(globalConfig);
	}
	
	List<GenericValue> reassignOwners = EntityQuery.use(delegator).select("roleTypeId","description").from("RoleType").where(EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, roles)).queryList();
	context.put("reassignOwnerList", UtilValidate.isNotEmpty(reassignOwners) ? org.fio.admin.portal.util.DataUtil.getMapFromGeneric(reassignOwners, "roleTypeId", "description", false) : new HashMap<String, Object>());
	
	String srCopyRoles = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "COPY_SR");
	String isCopySr = "N";
	if(UtilValidate.isNotEmpty(srCopyRoles)) {
		List<String> srCopyRoleList = org.fio.admin.portal.util.DataUtil.stringToList(srCopyRoles, ",");
		if(srCopyRoleList.contains(userLoginRoleTypeId)) {
			isCopySr="Y";
		}
	}
	context.put("isCopySr", isCopySr)
	
	List<String> childSrIds = org.groupfio.common.portal.util.SrUtil.getReferenceSrIds(delegator, srNumber, new ArrayList<>());
	if(UtilValidate.isNotEmpty(childSrIds)) {
		childSrIds.remove(srNumber);
		List<String> srcDocIds = new LinkedList<String>();
		for(String childSrId : childSrIds) {
			srcDocIds.add(childSrId);
		}
		if(UtilValidate.isNotEmpty(srcDocIds) && srcDocIds.size() > 1) {
			inputContext.put("childSr", org.fio.admin.portal.util.DataUtil.listToString(srcDocIds));
			inputContext.put("childSr_link", "/sr-portal/control/viewServiceRequest?srNumber=");
		} else {
			inputContext.put("childSr", org.fio.admin.portal.util.DataUtil.listToString(srcDocIds));
			inputContext.put("childSr_link", "/sr-portal/control/viewServiceRequest?srNumber="+org.fio.admin.portal.util.DataUtil.listToString(srcDocIds));
		}
	}
	
	boolean hasInvoiceTabAvailablePer = DataUtil.validatePermission(delegator, userLogin.getString("userLoginId"), "VIEW_INVOICE_TAB");
	Debug.log("isInvoiceTabAvailable==== "+hasInvoiceTabAvailablePer);
	if(hasInvoiceTabAvailablePer)
		isInvoiceTabAvailable="Y";
	else
		isInvoiceTabAvailable="N";
		
		context.put("isInvoiceTabAvailable", isInvoiceTabAvailable)
	
}

// 3PL approval [start]
isApprovalEnabled = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "3PLINV_APPROVAL_ENABLED");
context.put("isApprovalEnabled", isApprovalEnabled);
if (UtilValidate.isNotEmpty(isApprovalEnabled)) {
	context.put("approvalCategoryId", "APVL_CAT_3PL_INV");
	
	partyApproval = org.groupfio.approval.portal.util.DataUtil.getPartyApproval(delegator, org.ofbiz.base.util.UtilMisc.toMap("partyId", loggedUserPartyId, "approvalCategoryId", "APVL_CAT_3PL_INV", "domainEntityType", "SERVICE_REQUEST", "domainEntityId", srNumber));
	context.put("partyApproval", partyApproval);
	
	conditionsList = [];
	conditionsList.add(EntityCondition.makeCondition("statusTypeId", EntityOperator.EQUALS, "DECISION_STATUS"));
	conditionsList.add(EntityCondition.makeCondition("isEnabled", EntityOperator.EQUALS, "Y"));
	if (UtilValidate.isNotEmpty(partyApproval) && UtilValidate.isNotEmpty(partyApproval.accessLevel) && partyApproval.accessLevel=='ACCESS_L1') {
		conditionsList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN, UtilMisc.toList('DECISION_ENQUIRY')));
	}
	mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);
	statusList = delegator.findList("StatusItem", mainConditons, null, UtilMisc.toList("sequenceId DESC"), null, false);
	context.put("decisionStatusList", org.fio.homeapps.util.DataHelper.getDropDownOptions(statusList, "statusId", "description"));
		
	context.put("createdByUserLogin", custRequest.get("createdByUserLogin"));
	
	//approvalTemplateId=org.groupfio.rebate.service.util.AgreementUtil.getAgreementAttrValue(delegator, agreementId, "APV_TPL_ID");
	approvalTemplateId = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SR_APV_3PL_TPL");
	inputContext.put("approvalTemplateId", approvalTemplateId);
}

isEnableDomainEntity = "N";
context.put("isEnableDomainEntity", isEnableDomainEntity);
// 3PL approval [end]
	
// program activity [start]

context.put("isEnableProgramAct", org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "ENABLE_PROG_ACT", "N"));
inputContext.put("isProgramTemplate", org.groupfio.common.portal.util.SrUtil.getCustRequestAttrValue(delegator, "IS_PROG_TPL", srNumber));

// program activity [end]

context.put("inputContext", inputContext);
println("inputContext>>>>"+inputContext);

context.put("enablePaymentTab", org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "ENABLE_SR_PAYMENT_TAB", "N"));


String allowCloseDateEditStr = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SR_CLOSED_DATE_STATUS");
allowCloseDateEditStatus=[];
if (UtilValidate.isNotEmpty(allowCloseDateEditStr)) {
	allowCloseDateEditStatus = org.fio.admin.portal.util.DataUtil.stringToList(allowCloseDateEditStr, ",");
}
context.put("allowCloseDateEditStatus", allowCloseDateEditStatus);
context.put("globalDateFormat", globalDateFormat);
context.put("enableFsrInvoiceTab", org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "ENABLE_SR_INVOICE_TAB", "N"));
enableFsrpartsOnlyModal = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "FSR_PARTS_ONLY_VIEW", "N");
context.enableFsrpartsOnlyModal = enableFsrpartsOnlyModal;
conditionsList = [];
GenericValue partsOnlyAttr = null;
if (enableFsrpartsOnlyModal=="Y") {
	customField = EntityQuery.use(delegator).from("CustomField").where("groupId", "SERVICE_GROUP", "groupType", "CUSTOM_FIELD","customFieldName","Part Changes").queryFirst();
	customFieldId = UtilValidate.isNotEmpty(customField)?customField.getString("customFieldId"):"10260";
	partsOnlyAttr = EntityQuery.use(delegator).from("CustRequestAttribute").where("custRequestId", srNumber, "attrName", customFieldId).queryFirst();
}
context.partsOnlyAttr=partsOnlyAttr;

context.put("isEnableDashboardButton",org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "IS_VIEW_DASHBOARD_BTN_ENABLED", "Y"));
