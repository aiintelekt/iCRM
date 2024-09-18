import java.sql.Timestamp

import org.fio.homeapps.util.DataUtil;
import org.fio.homeapps.util.PartyHelper;
import org.fio.sr.portal.DataHelper
import org.groupfio.common.portal.util.PartyPrimaryContactMechWorker;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.DynamicViewEntity
import org.ofbiz.entity.model.ModelKeyMap
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;

delegator = request.getAttribute("delegator");
userLogin = request.getAttribute("userLogin");
	
uiLabelMap = UtilProperties.getResourceBundleMap("SrPortalUiLabels", locale);

loggedUserPartyId = userLogin.getString("partyId");
defaultLocationId = org.fio.homeapps.util.DataUtil.getPartyAttrValue(delegator, loggedUserPartyId, "LOCATION");
context.put("defaultLocationId", defaultLocationId);

String locationCustomFieldId = org.ofbiz.entity.util.EntityUtilProperties.getPropertyValue("sr-portal.properties", "location.customFieldId", delegator);

inputContext = new LinkedHashMap<String, Object>();

context.put("domainEntityType", "SERVICE_REQUEST");

context.put("isActUspsAddrVal", org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "IS_USPS_ADDRACT", "N"));

context.put("inputContext", inputContext);

String srNumber = request.getParameter("srNumber");
Map<String, Object> actionBarContext = new LinkedHashMap<String, Object>();
if(srNumber) {
	long start = System.currentTimeMillis();
	String partyName= "";
	partyId = request.getParameter("partyId");
	String isView = context.get("isView");
	activeTab = request.getAttribute("activeTab");
	context.activeTab = activeTab;
	
	String tsmRoleTypeId = EntityUtilProperties.getPropertyValue("sr-portal.properties", "tsm.roleTypeId", delegator);
	boolean isTsmUserLoggedIn = UtilValidate.isNotEmpty(PartyHelper.getFirstValidRoleTypeId(userLogin.getString("partyId"), UtilMisc.toList(tsmRoleTypeId), delegator)) ? true : false;
	context.put("isTsmUserLoggedIn", isTsmUserLoggedIn);
	
	partySummary = from("PartySummaryDetailsView").where("partyId", partyId,"partyTypeId","PARTY_GROUP").queryOne();
	
	
	if(partySummary!=null && partySummary.size()>0){
		context.put("partySummary", partySummary);
		inputContext.putAll(partySummary.getAllFields());
		partyId_desc = org.fio.homeapps.util.PartyHelper.getPartyName(delegator, partyId, false);
		inputContext.put("partyId_desc",partyId_desc );
	}
	
	if(UtilValidate.isEmpty(srNumber)) {
		srNumber = request.getAttribute("srNumber");
		if(UtilValidate.isNotEmpty(request.getParameter("custRequestId"))){
			srNumber = request.getParameter("custRequestId");
		}
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
	
	custRequestAttList = EntityQuery.use(delegator).from("CustRequestAttribute").where("custRequestId", srNumber).queryList();
	
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
	
	
	
	if(UtilValidate.isNotEmpty(custRequestAttList)){
		for(int i=0 ; i < custRequestAttList.size() ; i++){
			custRequest=custRequestAttList.get(i);
			if(pfpCustomFieldId.equals(custRequest.get("attrName"))){
				inputContext.put("preFinishPlus", custRequest.get("attrValue"));
			}else if(vcCustomFieldId.equals(custRequest.get("attrName"))){
				context.put("vendorCode", custRequest.get("attrValue"));
				inputContext.put("vendorCode", custRequest.get("attrValue"));
			} /*else if("10144".equals(custRequest.get("attrName"))){
				inputContext.put("finishType", custRequest.get("attrValue"));
			} */else if(sfCustomFieldId.equals(custRequest.get("attrName"))){
				context.put("serviceFee", custRequest.get("attrValue"));
				inputContext.put("serviceFee", custRequest.get("attrValue"));
			}else if(locationCustomFieldId.equals(custRequest.get("attrName"))){
				inputContext.put("location", custRequest.get("attrValue"));
			}else if(somCustomFieldId.equals(custRequest.get("attrName"))){
				context.put("soMaterial", custRequest.get("attrValue"));
			}else if(mtCustomFieldId.equals(custRequest.get("attrName"))){
				context.put("materialType", custRequest.get("attrValue"));
			} else if(sblCustomFieldId.equals(custRequest.get("attrName"))){
				inputContext.put("soldByLocation", custRequest.get("attrValue"));
			} else if(ibCustomFieldId.equals(custRequest.get("attrName"))){
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
				if(UtilValidate.isNotEmpty(copyFlag) && copyFlag.equals("Y")){}
				else {
					String coordinatorDesc = custRequest.get("attrValue");
					if(UtilValidate.isNotEmpty(coordinatorDesc) && DataUtil.isBase64(coordinatorDesc)) {
						byte[] base64decodedBytes = Base64.getDecoder().decode(coordinatorDesc);
						coordinatorDesc = new String(base64decodedBytes, "utf-8");
					}
					context.put("coordinatorDesc", coordinatorDesc);
					inputContext.put("coordinatorDesc", custRequest.get("attrValue"));
				}
			}/*else if("10210".equals(custRequest.get("attrName"))){
				context.put("majorMaterial", custRequest.get("attrValue"));
			}else if("10200".equals(custRequest.get("attrName"))){
				context.put("subMaterialCategory", custRequest.get("attrValue"));
			} */ 
			else if("FSR_MATERIAL_CATEGROY".equals(custRequest.get("attrName"))){
				if(UtilValidate.isNotEmpty(copyFlag) && copyFlag.equals("Y")){}
				else {
					context.put("materialCategory", custRequest.get("attrValue"));
					inputContext.put("materialCategory", custRequest.get("attrValue"));
				}
			} else if("FSR_MATERIAL_SUB_CATEGROY".equals(custRequest.get("attrName"))){
				if(UtilValidate.isNotEmpty(copyFlag) && copyFlag.equals("Y")){}
				else {
					context.put("materialSubCategory", custRequest.get("attrValue"));
					inputContext.put("materialSubCategory", custRequest.get("attrValue"));
				}
			} else if("FSR_FINISH_TYPE".equals(custRequest.get("attrName"))){
				context.put("finishType", custRequest.get("attrValue"));
				inputContext.put("finishType", custRequest.get("attrValue"));
			} else if("FSR_FINISH_COLOR".equals(custRequest.get("attrName"))){
				context.put("finishColor", custRequest.get("attrValue"));
				inputContext.put("finishColor", custRequest.get("attrValue"));
			} else if("DEALER_REF_NO".equals(custRequest.get("attrName"))){
				inputContext.put("dealerRefNo", custRequest.get("attrValue"));
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
	
	if(UtilValidate.isNotEmpty(custRequest)){
		
		srReopenValue= from("PretailLoyaltyGlobalParameters").where("parameterId","SR_REOPEN_DAYS").queryOne();
		if(UtilValidate.isNotEmpty(srReopenValue)){
			closedDate=custRequest.getTimestamp("closedByDate");
			isAllowReopen="Y";
			if(UtilValidate.isNotEmpty(closedDate)){
				int days= UtilDateTime.getIntervalInDays(closedDate,UtilDateTime.nowTimestamp());
				int srConfigureDays=Integer.parseInt(srReopenValue.get("value"));
				if(days>srConfigureDays){
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
	
		/*if(UtilValidate.isNotEmpty(custReqDocumentNum)){
			inputContext.put("sourceDocumentId", custReqDocumentNum	);
			context.put("sourceDocumentId", custReqDocumentNum);
		}else{
			inputContext.put("sourceDocumentId", srNumber);
			context.put("sourceDocumentId", srNumber);
		}*/
		
		inputContext.put("sourceDocumentId", srNumber);
		context.put("sourceDocumentId", srNumber);
		
		inputContext.put("sourceComponent", "Service Request");
		context.put("sourceComponent", "Service Request");
	
		inputContext.put("externalId", custRequest.getString("externalId"));
		inputContext.put("customerPrimaryEmail", custRequest.getString("emailAddress"));
		if(UtilValidate.isNotEmpty(copyFlag) && copyFlag.equals("Y")){}
		else {
			inputContext.put("homeType", custRequest.getString("homeType"));
		}
		
		inputContext.put("programTemplateId", org.groupfio.common.portal.util.SrUtil.getCustRequestAttrValue(delegator, "PROG_TPL_ID", srNumber));
		inputContext.put("isProgramTemplate", org.groupfio.common.portal.util.SrUtil.getCustRequestAttrValue(delegator, "IS_PROG_TPL", srNumber));
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
		context.put("srPostalAddress", org.groupfio.common.portal.util.DataHelper.wrapSrPostalAddress(delegator, custRequestSupplementory));
		inputContext.put("homePhoneNumber", custRequestSupplementory.get("homePhoneNumber"));
		inputContext.put("offPhoneNumber", custRequestSupplementory.get("offPhoneNumber"));
		inputContext.put("mobilePhoneNumber", custRequestSupplementory.get("mobileNumber"));
		inputContext.put("contractorPrimaryEmail", custRequestSupplementory.get("contractorEmail"));
		inputContext.put("contractorOffNumber", custRequestSupplementory.get("contractorOffPhone"));
		inputContext.put("contractorMobileNumber", custRequestSupplementory.get("contractorMobilePhone"));
		inputContext.put("contractorHomeNumber", custRequestSupplementory.get("contractorHomePhone"));
	}
	
	if(custRequest!=null && custRequest.size()>0){
	
		if(UtilValidate.isNotEmpty(copyFlag) && copyFlag.equals("Y")){
			
		} else if (UtilValidate.isNotEmpty(custRequest.get("description")) || UtilValidate.isNotEmpty(custRequest.get("tsmDescription"))){
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
				if(UtilValidate.isNotEmpty(resolution) && org.fio.homeapps.util.DataUtil.isBase64(resolution)) {
					byte[] base64decodedBytes = Base64.getDecoder().decode(resolution);
					resolution = new String(base64decodedBytes, "utf-8");
				}
				context.put("resolution", resolution);
			}
		}
		if(UtilValidate.isNotEmpty(copyFlag) && copyFlag.equals("Y")){
			
		} else if (UtilValidate.isNotEmpty(custRequest.get("actualResolution"))){
			String actualResolution = custRequest.getString("actualResolution");
			if(UtilValidate.isNotEmpty(actualResolution) && org.fio.homeapps.util.DataUtil.isBase64(actualResolution)) {
				byte[] base64decodedBytes = Base64.getDecoder().decode(actualResolution);
				actualResolution = new String(base64decodedBytes, "utf-8");
			}
			context.put("actualResolution", actualResolution);
		}
	
		if (UtilValidate.isNotEmpty(custRequest.get("responsiblePerson"))){
			context.put("ownerUserLoginId", custRequest.get("responsiblePerson"));
		}
	
		if (UtilValidate.isNotEmpty(custRequest.get("custRequestTypeId"))){
			if(UtilValidate.isNotEmpty(copyFlag) && copyFlag.equals("Y")){}
			else {
				context.put("srTypeId", custRequest.get("custRequestTypeId"));
				inputContext.put("srTypeId", custRequest.get("custRequestTypeId"));
			}
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
			}else{
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
	
	
			}
	
		}
		if (UtilValidate.isNotEmpty(custRequest)){
	
			if (UtilValidate.isNotEmpty(custRequest.get("priority"))){
				if(UtilValidate.isNotEmpty(copyFlag) && copyFlag.equals("Y")){}
				else {
					inputContext.put("priority", custRequest.get("priority"));
				}
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
	
		custRequestContactDetails = EntityUtil.getFirst( delegator.findList("CustRequestContact", mainConditons, null, null, null, false) );
	
		if (UtilValidate.isNotEmpty(custRequestContactDetails) && UtilValidate.isNotEmpty(custRequestContactDetails.getString("partyId"))){
			primaryContactName = org.fio.homeapps.util.PartyHelper.getPartyName(delegator, custRequestContactDetails.getString("partyId"), false);
			inputContext.put("primaryContactDesc", primaryContactName);
			inputContext.put("primaryContactDesc_link","/contact-portal/control/viewContact?partyId="+custRequestContactDetails.getString("partyId")+"&externalLoginKey="+externalLoginKey);
	
		}
	
		if (UtilValidate.isNotEmpty(custRequest.get("fromPartyId"))){
			inputContext.put("cNo", custRequest.get("fromPartyId"));
		}
		
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
	
		inputContext.put("openDate", UtilValidate.isNotEmpty(custRequest.get("createdDate")) ? UtilDateTime.timeStampToString(custRequest.getTimestamp("createdDate"), "dd/MM/yyyy HH:mm", TimeZone.getDefault(), null) : "");
		if (UtilValidate.isNotEmpty(custRequestSupplementory)){
			inputContext.put("dueDate", UtilValidate.isNotEmpty(custRequestSupplementory.get("commitDate")) ? UtilDateTime.timeStampToString(custRequestSupplementory.getTimestamp("commitDate"), "dd/MM/yyyy HH:mm", TimeZone.getDefault(), null) : "");
		}
		inputContext.put("createdOn", UtilValidate.isNotEmpty(custRequest.get("createdDate")) ? UtilDateTime.timeStampToString(custRequest.getTimestamp("createdDate"), "dd/MM/yyyy HH:mm", TimeZone.getDefault(), null) : "");
		inputContext.put("modifiedOn", UtilValidate.isNotEmpty(custRequest.get("lastModifiedDate")) ? UtilDateTime.timeStampToString(custRequest.getTimestamp("lastModifiedDate"), "dd/MM/yyyy HH:mm", TimeZone.getDefault(), null) : "");
	
		inputContext.put("closedOn", UtilValidate.isNotEmpty(custRequest.get("closedByDate")) ? UtilDateTime.timeStampToString(custRequest.getTimestamp("closedByDate"), "dd/MM/yyyy HH:mm", TimeZone.getDefault(), null) : "");
	
		inputContext.put("reopenedDate", UtilValidate.isNotEmpty(custRequest.get("reopenedDate")) ? UtilDateTime.timeStampToString(custRequest.getTimestamp("reopenedDate"), "dd/MM/yyyy HH:mm", TimeZone.getDefault(), null) : "");
		inputContext.put("reopenedBy", UtilValidate.isNotEmpty(custRequest.get("reopenedBy")) ? custRequest.getString("reopenedBy") : "");
	
		if (UtilValidate.isNotEmpty(copyFlag) && "Y".equals(copyFlag)){
			inputContext.put("srStatusId", "SR_OPEN");
		}
		context.put("currentSrStatusId", custRequest.get("statusId"));
	
		String priPartyRoleId = org.fio.admin.portal.util.DataUtil.getPartySecurityRole(delegator, custRequest.get("fromPartyId"));
		String priRoleDesc = org.fio.homeapps.util.DataUtil.getRoleTypeDesc(delegator, priPartyRoleId);
		if("Account".equals(priRoleDesc)) priRoleDesc = "Dealer";
		inputContext.put("primaryPhone", org.fio.admin.portal.util.DataUtil.formatPhoneNumber(partySummaryDetails.get("primaryContactNumber")));
		context.put("primaryPhone", partySummaryDetails.get("primaryContactNumber"));
		
	
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
	
	context.put("custRequestId", srNumber);
	context.put("domainEntityId", srNumber);
	context.put("partyId", PartyId);
	context.put("partyIdVal",PartyId);
	context.put("domainEntityType", "SERVICE_REQUEST");
	context.put("requestURI", "updateServiceRequest");
	
	
	EntityCondition searchConditions = EntityCondition.makeCondition(EntityOperator.AND,
			EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, PartyId),
			EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, UtilMisc.toList("ACCOUNT", "LEAD")));
	
	GenericValue partyRoleData = EntityUtil.getFirst(delegator.findList("PartyRole", searchConditions,null, null, null, false));
	roleType = "";
	if(UtilValidate.isNotEmpty(partyRoleData)){
		roleType = partyRoleData.roleTypeId
		if(roleType !=null){
	
			if(roleType.equals("ACCOUNT")){
				inputContext.put("cNo_link","/account-portal/control/viewAccount?partyId="+partyRoleData.partyId+"&externalLoginKey="+externalLoginKey);
			}else if(roleType.equals("LEAD")){
				inputContext.put("cNo_link","/lead-portal/control/viewLead?partyId="+partyRoleData.partyId+"&externalLoginKey="+externalLoginKey);
			}
		}
	
	}
	
	GenericValue custAttGv1 = EntityQuery.use(delegator).from("CustRequestAttribute").where("custRequestId", srNumber,"attrName","RECOMM_ADDRESS").queryFirst();
	inputContext.put("selectedHomeOwnerAddress", UtilValidate.isNotEmpty(custAttGv1) ? custAttGv1.getString("attrValue") : "");
	
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
			PrimaryContactEmailInformation = org.groupfio.common.portal.util.PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator,userLogin.getString("partyId"),UtilMisc.toMap("isRetriveEmail", true),true);
			if(UtilValidate.isNotEmpty(PrimaryContactEmailInformation)) {
				if(UtilValidate.isNotEmpty(PrimaryContactEmailInformation.get("EmailAddress"))) {
					loggedInUserEmailId = PrimaryContactEmailInformation.get("EmailAddress")+",";
				}
			}
		}
		custRequestContactList = from("CustRequestContact").where("custRequestId", srNumber, "thruDate", null, "roleTypeId", "CONTACT" ).queryList();
		if(UtilValidate.isNotEmpty(custRequestContactList)){
			custRequestContactList.each { eachCustContact ->
				ContactPartyId = eachCustContact.partyId;
				if(UtilValidate.isNotEmpty(ContactPartyId)){
					PrimaryContactInformation = org.groupfio.common.portal.util.PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator,ContactPartyId,UtilMisc.toMap("isRetriveEmail", true),true);
					if(UtilValidate.isNotEmpty(PrimaryContactInformation)) {
						if(UtilValidate.isNotEmpty(PrimaryContactInformation.get("EmailAddress"))) {
							if(UtilValidate.isNotEmpty(loggedInUserEmailId) && loggedInUserEmailId !=  PrimaryContactInformation.get("EmailAddress")){
								relatedPartiesEmailIds += PrimaryContactInformation.get("EmailAddress")+",";
							}
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
		if(UtilValidate.isNotEmpty(copyFlag) && copyFlag.equals("Y")){}
		else {
			List<GenericValue> reasonCodeList = EntityQuery.use(delegator).from("CustRequestResolution").where("custRequestId", srNumber,"custRequestTypeId","REASON_CODE").queryList();
			if(UtilValidate.isNotEmpty(reasonCodeList)) {
				List<String> reasonIds = EntityUtil.getFieldListFromEntityList(reasonCodeList, "reasonId", true);
				List<String> reasonDescs = EntityUtil.getFieldListFromEntityList(reasonCodeList, "description", true);
				if (request.getRequestURI().contains("updateServiceRequest") || request.getRequestURI().contains("createServiceRequest")) {
					inputContext.put("reasonCode", UtilValidate.isNotEmpty(reasonIds) ? org.fio.admin.portal.util.DataUtil.listToString(reasonIds): "" );
				}
				if (request.getRequestURI().contains("viewServiceRequest")) {
					inputContext.put("reasonCode", UtilValidate.isNotEmpty(reasonDescs) ? org.fio.admin.portal.util.DataUtil.listToString(reasonDescs): "" );
				}
				inputContext.put("reasonIds", UtilValidate.isNotEmpty(reasonIds) ? org.fio.admin.portal.util.DataUtil.listToString(reasonIds): "" );
			}
		}
		if(UtilValidate.isNotEmpty(copyFlag) && copyFlag.equals("Y")){}
		else {
			List<GenericValue> causeCategoryList = EntityQuery.use(delegator).from("CustRequestResolution").where("custRequestId", srNumber,"custRequestTypeId","CAUSE_CATEGORY").queryList();
			if(UtilValidate.isNotEmpty(causeCategoryList)) {
				List<String> causeCategoryIds = EntityUtil.getFieldListFromEntityList(causeCategoryList, "causeCategoryId", true);
				List<String> causeCategoryDescs = EntityUtil.getFieldListFromEntityList(causeCategoryList, "description", true);

				if (request.getRequestURI().contains("updateServiceRequest") || request.getRequestURI().contains("createServiceRequest")) {
					inputContext.put("causeCategory", UtilValidate.isNotEmpty(causeCategoryIds) ? org.fio.admin.portal.util.DataUtil.listToString(causeCategoryIds): "" );
				}
				if (request.getRequestURI().contains("viewServiceRequest")) {
					inputContext.put("causeCategory", UtilValidate.isNotEmpty(causeCategoryDescs) ? org.fio.admin.portal.util.DataUtil.listToString(causeCategoryDescs): "" );
				}
				inputContext.put("causeCategoryIds", UtilValidate.isNotEmpty(causeCategoryIds) ? org.fio.admin.portal.util.DataUtil.listToString(causeCategoryIds): "" );
			}
		}

		GenericValue srHomeOwner = EntityQuery.use(delegator).from("CustRequestParty").where("custRequestId", srNumber, "roleTypeId", "CUSTOMER", "thruDate",null).filterByDate().queryFirst();
		if(UtilValidate.isNotEmpty(srHomeOwner)) {
			String customerId = srHomeOwner.getString("partyId");
			println ("afdsafasd-------==============>"+customerId);
			if (request.getRequestURI().contains("updateServiceRequest") || request.getRequestURI().contains("createServiceRequest")) {
				inputContext.put("customerId", UtilValidate.isNotEmpty(customerId) ? customerId: "" );
				GenericValue person = EntityQuery.use(delegator).from("Person").where("partyId", customerId).queryFirst();
				inputContext.put("customerId_desc", UtilValidate.isNotEmpty(person) ? person.getString("firstName") + (UtilValidate.isNotEmpty(person.getString("lastName")) ? " "+person.getString("lastName") :"") : "" );

			}
			if (request.getRequestURI().contains("viewServiceRequest") || request.getRequestURI().contains("createPhoneCallActivity")) {
				GenericValue person = EntityQuery.use(delegator).from("Person").where("partyId", customerId).queryFirst();
				inputContext.put("customerId", UtilValidate.isNotEmpty(person) ? person.getString("firstName") + (UtilValidate.isNotEmpty(person.getString("lastName")) ? " "+person.getString("lastName") :"") : "" );
				inputContext.put("customerId_link","/customer-portal/control/viewCustomer?partyId="+customerId+"&externalLoginKey="+externalLoginKey);
			}
		}
		GenericValue contractor = EntityQuery.use(delegator).from("CustRequestParty").where("custRequestId", srNumber, "roleTypeId", "CONTRACTOR", "thruDate",null).filterByDate().queryFirst();
		if(UtilValidate.isNotEmpty(contractor)) {
			String contractorId = contractor.getString("partyId");
			if (request.getRequestURI().contains("updateServiceRequest")|| request.getRequestURI().contains("createServiceRequest")) {
				inputContext.put("contractorId", UtilValidate.isNotEmpty(contractorId) ? contractorId: "" );
				GenericValue person = EntityQuery.use(delegator).from("Person").where("partyId", contractorId).queryFirst();
				inputContext.put("contractorId_desc", UtilValidate.isNotEmpty(person) ? person.getString("firstName") + (UtilValidate.isNotEmpty(person.getString("lastName")) ? " "+person.getString("lastName") :"") : "" );

			}
			if (request.getRequestURI().contains("viewServiceRequest") || request.getRequestURI().contains("createPhoneCallActivity")) {
				GenericValue person = EntityQuery.use(delegator).from("Person").where("partyId", contractorId).queryFirst();
				inputContext.put("contractorId", UtilValidate.isNotEmpty(person) ? person.getString("firstName") + (UtilValidate.isNotEmpty(person.getString("lastName")) ? " "+person.getString("lastName") :"") : "" );
				/*
				if(UtilValidate.isNotEmpty(primaryVal) && "CONTRACTOR".equals(primaryVal)){
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
					actionBarContext.put("name", (UtilValidate.isNotEmpty(person) ? person.getString("firstName") + (UtilValidate.isNotEmpty(person.getString("lastName")) ? " "+person.getString("lastName") :"") : "") + (UtilValidate.isNotEmpty(roleDesc) ? " ("+roleDesc+")":"") );

				}
				*/
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
			}

			if (request.getRequestURI().contains("updateServiceRequest") || request.getRequestURI().contains("createServiceRequest")) {
				inputContext.put("customerId", UtilValidate.isNotEmpty(customerId) ? customerId: "" );
				String customerName = org.fio.homeapps.util.PartyHelper.getPartyName(delegator, customerId, false);
				inputContext.put("customerId_desc", UtilValidate.isNotEmpty(customerName) ? customerName : "" );
			}
			if (request.getRequestURI().contains("viewServiceRequest")) {
				inputContext.put("customerId", UtilValidate.isNotEmpty(customerId) ? org.ofbiz.party.party.PartyHelper.getPartyName(delegator, customerId, false) : "" );
				inputContext.put("customerId_link","/customer-portal/control/viewCustomer?partyId="+customerId+"&externalLoginKey="+externalLoginKey);
			}
			/*
			 if (request.getRequestURI().contains("updateServiceRequest") || request.getRequestURI().contains("createServiceRequest")) {
			 inputContext.put("contractorId", UtilValidate.isNotEmpty(contractorId) ? contractorId: "" );
			 contractorName = org.fio.homeapps.util.PartyHelper.getPartyName(delegator, contractorId, false);
			 inputContext.put("contractorId_desc", UtilValidate.isNotEmpty(contractorName) ? contractorName : "" );
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
						dealerId = primaryContactId;
						context.put("partyIdVal",primaryContactId);
					}
					else {
						dealerId = accountId;
						context.put("partyIdVal",accountId);
					}
					
					actionBarContext.put("partyId",dealerId);
					String dealerName = org.fio.homeapps.util.PartyHelper.getPartyName(delegator, dealerId, false);
					primaryContactCustInformation = org.groupfio.common.portal.util.PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator,dealerId,UtilMisc.toMap("isRetrivePhone", true, "isRetriveEmail", true),true);
					
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
					primaryContactCustInformation = org.groupfio.common.portal.util.PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator,customerId,UtilMisc.toMap("isRetrivePhone", true, "isRetriveEmail", true),true);
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
					primaryContactCustInformation = org.groupfio.common.portal.util.PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator,contractorId,UtilMisc.toMap("isRetrivePhone", true, "isRetriveEmail", true),true);
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
	
		workEffortTimeEntries =  delegator.findList("WorkEffortAndTimeEntry", mainConditons, null, null, null, false);
		if(UtilValidate.isEmpty(workEffortTimeEntries)){
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
		conditionList.add(EntityCondition.makeCondition("currentStatusId", EntityOperator.NOT_IN, UtilMisc.toList("IA_COMPLETED", "IA_MCOMPLETED", "IA_CANCEL")));
	
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
	
	long end = System.currentTimeMillis();
	println ("timeElapsed--->"+(end-start) / 1000f);
} else {
	partyId = request.getParameter("partyId");
	String partyName = DataUtil.getPartyName(delegator, partyId);
	String partySecurityRole = org.fio.admin.portal.util.DataUtil.getPartySecurityRole(delegator, partyId);
	context.put("partyName", partyName);
	context.put("partySecurityRole", partySecurityRole);
	context.put("leadOrAccountPartyId", partyId);
	if(UtilValidate.isNotEmpty(partyId) && "CONTACT".equals(partySecurityRole)) {
		String loggedInPartyId = partyId;
		EntityCondition searchConditions = EntityCondition.makeCondition(EntityOperator.AND,
				EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, loggedInPartyId),
				EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "CONTACT"),
				EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "ACCOUNT"),
				EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "CONTACT_REL_INV"),
				EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null),
				EntityUtil.getFilterByDateExpr());
	
		GenericValue partyRelationship = EntityUtil.getFirst(delegator.findList("PartyRelationship", searchConditions, null, null, null, false));
	
		if(partyRelationship != null && partyRelationship.size() > 0) {
			String AccountPartyId = partyRelationship.getString("partyIdTo");
			String companyName = "";
			
			partyGroupGen = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", AccountPartyId), false);
			
			if (UtilValidate.isNotEmpty(partyGroupGen)) {
				companyName = partyGroupGen.getString("groupName");
	
			}
			
			context.put("AccountPartyId", AccountPartyId);
			context.put("companyName", companyName);
			context.put("loggedInContactPartyId", loggedInPartyId);

			context.put("leadOrAccountPartyId", AccountPartyId);
			
			inputContext.put("cNo", UtilValidate.isNotEmpty(AccountPartyId) ? AccountPartyId: "" );
			String accName = org.fio.homeapps.util.PartyHelper.getPartyName(delegator, AccountPartyId, false);
			inputContext.put("cNo_desc", UtilValidate.isNotEmpty(accName) ? accName : "" );
			
		}
		
		List<GenericValue> partyRelList = EntityQuery.use(delegator).from("PartyRelationship").where(searchConditions).queryList();
		if(UtilValidate.isNotEmpty(partyRelList) && partyRelList.size() > 1) {
			inputContext.put("cNo", "" );
			inputContext.put("cNo_desc", "" );
		}
	}
	
}
String workEffortId = request.getParameter("workEffortId");
if(UtilValidate.isNotEmpty(workEffortId)) {
	DynamicViewEntity dynamicViewEntity = new DynamicViewEntity();
	dynamicViewEntity.addMemberEntity("CEWE", "CommunicationEventWorkEff");
	dynamicViewEntity.addAlias("CEWE", "workEffortId");
	dynamicViewEntity.addAlias("CEWE", "communicationEventId");
	dynamicViewEntity.addMemberEntity("CE", "CommunicationEvent");
	dynamicViewEntity.addAlias("CE", "communicationEventTypeId");
	dynamicViewEntity.addAlias("CE", "subject");
	dynamicViewEntity.addAlias("CE", "content");
	dynamicViewEntity.addViewLink("CEWE", "CE", Boolean.FALSE, ModelKeyMap.makeKeyMapList("communicationEventId"));
	
	GenericValue commEvent = EntityQuery.use(delegator).from(dynamicViewEntity).where("workEffortId", workEffortId).queryFirst();
	if(UtilValidate.isNotEmpty(commEvent)) {
		List<GenericValue> commEventContentAssocList = EntityQuery.use(delegator).from("CommEventContentAssoc").where(EntityCondition.makeCondition("communicationEventId", EntityOperator.EQUALS, commEvent.getString("communicationEventId"))).queryList();
		
		boolean isAttach = false;
		if(UtilValidate.isNotEmpty(commEventContentAssocList)){
			isAttach = true;
		}
		
		String communicationEventTypeId = commEvent.getString("communicationEventTypeId");
		if(UtilValidate.isNotEmpty(communicationEventTypeId) && "GRAPH_EMAIL".equals(communicationEventTypeId)) {
			
			Map<String, Object> mailContent = org.fio.admin.portal.util.DataUtil.convertToMap(commEvent.getString("content"));

			Map<String, Object> msgBody = (Map<String, Object>) mailContent.get("body");
			String emailCont = UtilValidate.isNotEmpty(msgBody) && UtilValidate.isNotEmpty(msgBody.get("content")) ? (String) msgBody.get("content") :"";
			if(isAttach) {
				if(emailCont.contains("<img "))
					emailCont = emailCont.replaceAll("<p class=\"MsoNormal\">&nbsp;</p>"," ");
			
				emailCont = emailCont.replaceAll("<img .*?>","&nbsp;");
			}
			inputContext.put("resolution", emailCont);
			inputContext.put("srName", commEvent.getString("subject"));
			context.put("description", emailCont);
			
			println ("email cont---------->"+mailContent.get("bodyPreview"));
		}
		else {
			String emailCont = UtilValidate.isNotEmpty(commEvent.getString("content")) ? commEvent.getString("content") :"";
			if(isAttach) {
				if(emailCont.contains("<img "))
					emailCont = emailCont.replaceAll("<p class=\"MsoNormal\">&nbsp;</p>"," ");
			
				emailCont = emailCont.replaceAll("<img .*?>","&nbsp;");
			}
			inputContext.put("resolution", emailCont);
			inputContext.put("srName", commEvent.getString("subject"));
			context.put("description", emailCont);
		}
	}
	
}
context.put("inputContext", inputContext);