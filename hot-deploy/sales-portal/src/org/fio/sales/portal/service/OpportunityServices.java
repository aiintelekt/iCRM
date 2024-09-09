package org.fio.sales.portal.service;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.fio.admin.portal.constant.AdminPortalConstant.DateTimeTypeConstant;
import org.fio.admin.portal.util.DataUtil;
import org.fio.homeapps.util.UtilGenerator;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntity;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.math.BigDecimal;

public class OpportunityServices {
	private OpportunityServices() {}

	private static final String MODULE = OpportunityServices.class.getName();
	public static final String RESOURCE = "SalesPortalUiLabels";

	public static Map<String, Object> createSalesOpportunityDetails(DispatchContext dctx, Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String opportunityName = (String) context.get("opportunityName");
		String typeEnumId = (String) context.get("typeEnumId");
		BigDecimal estimatedAmount = (BigDecimal) context.get("estimatedAmount");
		String remarks = (String) context.get("remarks");
		String dataSourceId = (String) context.get("dataSourceId");
		String marketingCampaignId = (String) context.get("marketingCampaignId");
		String dataSourceDataId = (String) context.get("dataSourceDataId");
		String product = (String) context.get("productId");
		String partyId = (String) context.get("partyId");
		String roleTypeId = (String) context.get("roleTypeId");
		String opportunityState = (String) context.get("opportunityState");
		String cNo = (String) context.get("cNo");
		
		String responseMessage = UtilProperties.getMessage(RESOURCE, "SalesOpportunitySuccessfullyCreated", locale);
	    Map < String, Object > results = ServiceUtil.returnSuccess(responseMessage);
	    Map outMap = FastMap.newInstance();
	    Map outRoleMap = FastMap.newInstance();
	    String salesOpportunityId = "";
		try {
			if(UtilValidate.isNotEmpty(typeEnumId)) {
				Map inputMap = FastMap.newInstance();
				String callOutCome = "";
				GenericValue enumerationList = EntityQuery.use(delegator).select("enumId").from("Enumeration").where("description", "NEW", "enumTypeId", "CALL_OUT_COME").queryFirst();
				if(UtilValidate.isNotEmpty(enumerationList)) {
					callOutCome = enumerationList.getString("enumId");
				}
				inputMap.put("callOutCome",callOutCome);
				inputMap.put("opportunityName",opportunityName);
				inputMap.put("estimatedAmount",estimatedAmount);
				inputMap.put("typeEnumId",typeEnumId);
				inputMap.put("remarks",remarks);
				inputMap.put("dataSourceId",dataSourceId);
				inputMap.put("marketingCampaignId",marketingCampaignId);
				inputMap.put("dataSourceDataId",dataSourceDataId);
				inputMap.put("userLogin",userLogin);
				inputMap.put("product",product);
				inputMap.put("opportunityState",opportunityState);
				inputMap.put("createdDate",UtilDateTime.nowTimestamp());
				inputMap.put("opportunityStageId", "SOSTG_OPEN");

				salesOpportunityId = UtilGenerator.getSalesOpportunityNumber(delegator, delegator.getNextSeqId("SalesOpportunity"));
                inputMap.put("salesOpportunityId", salesOpportunityId);
				outMap = dispatcher.runSync("createSalesOpportunity", inputMap);
				if(ServiceUtil.isError(outMap) || ServiceUtil.isFailure(outMap)){
                    responseMessage = UtilProperties.getMessage(RESOURCE, "SalesOpportunityCreationFailed", locale);
                    return results;
                }
				if(UtilValidate.isNotEmpty(salesOpportunityId)) {
					String externalId = salesOpportunityId;
					GenericValue salesOpp = EntityQuery.use(delegator).from("SalesOpportunity").where("salesOpportunityId", salesOpportunityId).queryOne();
					if (salesOpp != null && UtilValidate.isNotEmpty(externalId)) {
						salesOpp.put("externalId", externalId);
						salesOpp.store();
		            }
				}
				if(UtilValidate.isNotEmpty(cNo)) {
	    	        GenericValue partyIdentification = EntityUtil.getFirst(delegator.findList("PartyIdentification", EntityCondition.makeCondition("idValue", EntityOperator.EQUALS, cNo), null, UtilMisc.toList("-createdStamp"), null, false));
	    	        if (UtilValidate.isNotEmpty(partyIdentification)) {
	    	        	partyId = partyIdentification.getString("partyId");
	    	        }
				}
				GenericValue roleIdentification = EntityUtil
						.getFirst(delegator.findList("RoleTypeAndParty", EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId), null, null, null, false));
    	        if (UtilValidate.isNotEmpty(roleIdentification)) {
    	        	roleTypeId = roleIdentification.getString("roleTypeId");
    	        }
    	        
				if(UtilValidate.isNotEmpty(salesOpportunityId) && UtilValidate.isNotEmpty(partyId) && UtilValidate.isNotEmpty(roleTypeId)) {
					Map inputRoleMap = FastMap.newInstance();
					inputRoleMap.put("salesOpportunityId",salesOpportunityId);
					inputRoleMap.put("partyId",partyId);
					inputRoleMap.put("roleTypeId",roleTypeId);
					inputRoleMap.put("userLogin",userLogin);
					inputRoleMap.put("ownerId",userLogin.getString("partyId"));
					String owner = userLogin.getString("partyId");
					String ownerBu = "";
					GenericValue userLoginPerson = EntityQuery.use(delegator).from("UserLoginPerson").where("userLoginId", userLogin.getString("userLoginId")).queryOne();
					if (UtilValidate.isNotEmpty(userLoginPerson)) {
						ownerBu = userLoginPerson.getString("businessUnit");
	    	        }
					inputRoleMap.put("ownerBu",ownerBu);
					if(UtilValidate.isNotEmpty(owner)){
						GenericValue emplTeam = EntityUtil.getFirst(EntityQuery.use(delegator).select(UtilMisc.toSet("emplTeamId","partyId","businessUnit")).from("EmplTeam").where("partyId", owner).queryList());
				  		if (UtilValidate.isNotEmpty(emplTeam) && UtilValidate.isNotEmpty(emplTeam.getString("emplTeamId"))) {    
				  			String emplTeamId = emplTeam.getString("emplTeamId");
							String businessUnit = emplTeam.getString("businessUnit");
							if(UtilValidate.isNotEmpty(emplTeamId)) {
								inputRoleMap.put("emplTeamId", emplTeamId);
					    	}
							if(UtilValidate.isNotEmpty(businessUnit)) {
								inputRoleMap.put("ownerBu",businessUnit);
					    	}
				  		}
					}
					outRoleMap = dispatcher.runSync("createSalesOpportunityRole", inputRoleMap);
					if(ServiceUtil.isError(outRoleMap) || ServiceUtil.isFailure(outRoleMap)){
	                    responseMessage = UtilProperties.getMessage(RESOURCE, "SalesOpportunityCreationFailed", locale);
	                    return results;
	                }
				}
			}
		} catch (Exception e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);
			results = ServiceUtil.returnError("Error : "+e.getMessage());
		}
		results.put("salesOpportunityId", salesOpportunityId);
		return results;
	}
	
public static Map<String, Object> updateSalesOpportunityDetails(DispatchContext dctx, Map<String, Object> context) {
		
		
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");

		Locale locale = (Locale) context.get("locale");
        Map<String, Object> results =ServiceUtil.returnSuccess();
        String responseTypeId = (String) context.get("responseTypeId");
        String opportunityStatusId = (String) context.get("opportunityStatusId");
        String callOutCome = (String) context.get("callOutcome");
        String responseReasonId = (String) context.get("responseReasonId");
        String salesOpportunityId = (String) context.get("salesOpportunityId");
        SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy");
        String callBackDate = (String) context.get("callBackDate");
        String opportunityStageId = (String) context.get("opportunityStageId");
        Map<String, Object> result = FastMap.newInstance();
        String responseMessage = UtilProperties.getMessage(RESOURCE, "SalesActivityCreatedSuccessfully", locale);
		try {
			GenericValue callRecordMaster = null;
			Map inMap = FastMap.newInstance();
		    Map outMap = FastMap.newInstance();
			String partyId = "";
			String customerCIN = "";
			String marketingCampaignId = "";
			String workEffortTypeId = "";
			String workEffortServiceType = "";
			String ownerBu = "";
			java.sql.Date callBackDateSql = null;
			if (UtilValidate.isNotEmpty(callBackDate)) {
				try {
					callBackDateSql = new java.sql.Date(df2.parse(callBackDate).getTime());
				} catch (ParseException e) {
				}
			}
			GenericValue salesOpportunityList = EntityQuery.use(delegator).select("customerId","customerCin","marketingCampaignId").from("SalesOpportunitySummary").where("salesOpportunityId",salesOpportunityId).queryFirst();
        	GenericValue salesOpportunityData = delegator.findOne("SalesOpportunity",UtilMisc.toMap("salesOpportunityId",salesOpportunityId),false);
        	if (UtilValidate.isNotEmpty(salesOpportunityData) && UtilValidate.isNotEmpty(opportunityStageId)) {
        		salesOpportunityData.set("opportunityStageId", opportunityStageId);
        		salesOpportunityData.set("callOutCome", callOutCome);
        		salesOpportunityData.set("responseReasonId", responseReasonId);
        		salesOpportunityData.set("responseTypeId", responseTypeId);
        		salesOpportunityData.set("lastModifiedDate", UtilDateTime.nowTimestamp());
        		salesOpportunityData.set("lastModifiedByUserLogin", userLogin.getString("userLoginId"));
        		salesOpportunityData.store();
        	}
			
			if (UtilValidate.isNotEmpty(salesOpportunityList)) {
				partyId = salesOpportunityList.getString("customerId");
				customerCIN = salesOpportunityList.getString("customerCin");
				marketingCampaignId = salesOpportunityList.getString("marketingCampaignId");
			}
			GenericValue workTypeIdentification = EntityQuery.use(delegator).from("WorkEffortType").where("description","Phone Call").queryFirst();
			if (UtilValidate.isNotEmpty(workTypeIdentification)) {
				workEffortTypeId = workTypeIdentification.getString("workEffortTypeId");
			}
			EntityCondition condtn = EntityCondition.makeCondition(EntityOperator.AND,
    				EntityCondition.makeCondition("entityName", EntityOperator.EQUALS, "Activity"),
    				EntityCondition.makeCondition("type", EntityOperator.EQUALS, "Type"),
    				EntityCondition.makeCondition("value", EntityOperator.EQUALS, "Phone Call"),
    				EntityCondition.makeCondition("active", EntityOperator.EQUALS, "Y"));
    		
        	GenericValue workEffortAssocTripletList = EntityUtil
    				.getFirst(delegator.findList("WorkEffortAssocTriplet", condtn, null, null, null, false));
        	if (UtilValidate.isNotEmpty(workEffortAssocTripletList)) {
        		workEffortServiceType = workEffortAssocTripletList.getString("code");
        	}
        	
        	/*EntityCondition rolecondition = EntityCondition.makeCondition(EntityOperator.AND,
    				EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId),
    				EntityCondition.makeCondition("roleTypeId", EntityOperator.NOT_EQUAL, "_NA_"));

    		GenericValue roleIdentification = EntityUtil
    				.getFirst(delegator.findList("PartyRole", rolecondition, null, null, null, false));
    		String roleTypeId = null;
    		if (UtilValidate.isNotEmpty(roleIdentification)) {
    			roleTypeId = roleIdentification.getString("roleTypeId");
    		}*/
        	
        	GenericValue party = EntityUtil.getFirst( delegator.findByAnd("Party", UtilMisc.toMap("partyId", partyId), null, false) );
			String roleTypeId = party.getString("roleTypeId");
			
    		if (UtilValidate.isNotEmpty(roleTypeId)) {
    			if ("CUSTOMER".equalsIgnoreCase(roleTypeId)) {
    				inMap.put("roleTypeId", "CUSTOMER");
    			}else if ("PROSPECT".equalsIgnoreCase(roleTypeId)) {
    				inMap.put("roleTypeId", "02");
    			}else if ("NON_CRM".equalsIgnoreCase(roleTypeId)) {
    				inMap.put("roleTypeId", "07");
    			}
    		}
    		inMap.put("userLogin",userLogin);
    		inMap.put("workEffortServiceType",workEffortServiceType);
    		inMap.put("workEffortTypeId",workEffortTypeId);
    		inMap.put("estimatedStartDate", UtilDateTime.nowTimestamp());
    		if(UtilValidate.isNotEmpty(customerCIN)){
    			inMap.put("partyId", customerCIN);
    		}else if (UtilValidate.isNotEmpty(partyId)) {
    			inMap.put("partyId", partyId);
    		}
    		inMap.put("primOwnerId", userLogin.getString("partyId"));
    		GenericValue emplTeam = EntityUtil.getFirst(EntityQuery.use(delegator).select(UtilMisc.toSet("emplTeamId","partyId","businessUnit")).from("EmplTeam").where("partyId", userLogin.getString("partyId")).queryList());
    		if (UtilValidate.isNotEmpty(userLogin.getString("partyId"))&&UtilValidate.isNotEmpty(emplTeam)) { 
    			if (UtilValidate.isNotEmpty(emplTeam.getString("emplTeamId"))){
    				inMap.put("emplTeamId", emplTeam.getString("emplTeamId"));
    			}
    			if (UtilValidate.isNotEmpty(emplTeam.getString("businessUnit"))){
    				ownerBu = emplTeam.getString("businessUnit");
        			inMap.put("wftMsdbusinessunit", emplTeam.getString("businessUnit"));
        		}
    		}
    		outMap = dispatcher.runSync("crmPortal.createInteractiveActivity", inMap);
    		if(!ServiceUtil.isSuccess(outMap)) {
                responseMessage = UtilProperties.getMessage(RESOURCE,ServiceUtil.getErrorMessage(outMap), locale);
            }
            String workEffortId = (String) outMap.get("workEffortId");
            if (UtilValidate.isNotEmpty(workEffortId) && !(workEffortId.equals(null)) && UtilValidate.isNotEmpty(salesOpportunityId)) {
                GenericValue salesOpportunityWorkEffort = delegator.makeValue("SalesOpportunityWorkEffort");
                salesOpportunityWorkEffort.set("salesOpportunityId", salesOpportunityId);
                salesOpportunityWorkEffort.set("workEffortId", workEffortId);
                salesOpportunityWorkEffort.create();
            }
        	String entityReferenceTypeId = "";
        	GenericValue enumList = EntityQuery.use(delegator).select("enumId").from("Enumeration").where("enumTypeId", "CALL_RECORD_ENTITY_TYPE","enumCode","SALES_OPPURTUNITY_TYPE").queryFirst();
			if (UtilValidate.isNotEmpty(enumList)){
				entityReferenceTypeId = enumList.getString("enumId");
			}
			String firstName = "";
			String middleName = "";
			String lastName = "";
			GenericValue person = EntityQuery.use(delegator).select("firstName","middleName","lastName").from("Person").where("partyId", partyId).queryFirst();
			if (UtilValidate.isNotEmpty(person)) {
				firstName = person.getString("firstName");
				middleName = person.getString("middleName");
				lastName = person.getString("lastName");
			}
			
            if (UtilValidate.isNotEmpty(workEffortId) && !(workEffortId.equals(null))) {
            	GenericValue callRecordMasterList = delegator.findOne("CallRecordMaster",UtilMisc.toMap("entityReferenceId",salesOpportunityId, "entityReferenceTypeId",entityReferenceTypeId),false);
    			if (UtilValidate.isEmpty(callRecordMasterList)) {
    				callRecordMaster = delegator.makeValue("CallRecordMaster");
    	        	callRecordMaster.set("callRecordId", delegator.getNextSeqId("CallRecordMaster"));
    	        	callRecordMaster.set("entityReferenceId", salesOpportunityId);
    	        	callRecordMaster.set("entityReferenceTypeId", entityReferenceTypeId);
    	        	if(UtilValidate.isNotEmpty(workEffortId)){
    	        		callRecordMaster.set("workEffortId", workEffortId);
    	        	}
    	        	if(UtilValidate.isNotEmpty(workEffortTypeId)){
    	        		callRecordMaster.set("workEffortTypeId", workEffortTypeId);
    	        	}
    	        	if(UtilValidate.isNotEmpty(marketingCampaignId)){
    	        		callRecordMaster.set("marketingCampaignId", marketingCampaignId);
    	        	}
    	        	callRecordMaster.set("partyId", partyId);
    	        	if(UtilValidate.isNotEmpty(customerCIN)){
    	        		callRecordMaster.set("externalReferenceId", customerCIN);
    	        		callRecordMaster.set("externalReferenceTypeId", "CIF");
    	        	}
    	        	callRecordMaster.set("createdDate", UtilDateTime.nowTimestamp());
    	        	 if (UtilValidate.isNotEmpty(callBackDateSql)) {
                         callRecordMaster.set("callBackDate", callBackDateSql);
                     } else {
                         callRecordMaster.set("callBackDate", null);
                     }
    	        	callRecordMaster.set("callOutCome", callOutCome);
    	        	callRecordMaster.set("responseTypeId", responseTypeId);
    	        	callRecordMaster.set("responseReasonId", responseReasonId);
    	        	callRecordMaster.set("csr1PartyId", userLogin.getString("userLoginId"));
    	        	callRecordMaster.set("createdByUserLogin", userLogin.getString("userLoginId"));
    	        	callRecordMaster.set("firstName", firstName);
    	        	//callRecordMaster.set("middleName", middleName);
    	        	callRecordMaster.set("lastName", lastName);
    	        	callRecordMaster.set("ownerId", userLogin.getString("partyId"));
                	callRecordMaster.set("ownerBusinessUnit", ownerBu);
    	        	
    	        	callRecordMaster.create();
    	        	
    	        	GenericValue callRecordDetails = delegator.makeValue("CallRecordDetails");
            		callRecordDetails.set("callRecordId", callRecordMaster.getString("callRecordId"));
                    callRecordDetails.set("callRecordDetailSeqId", delegator.getNextSeqId("CallRecordDetails"));
                    if (UtilValidate.isNotEmpty(partyId)) {
                    	callRecordDetails.set("partyId", partyId);
                    }
                    if (UtilValidate.isNotEmpty(marketingCampaignId)) {
                    	callRecordDetails.set("marketingCampaignId", marketingCampaignId);
                    }
                    if (UtilValidate.isNotEmpty(callOutCome)) {
                    	 callRecordDetails.set("callOutCome", callOutCome);
                    }
                    if (UtilValidate.isNotEmpty(responseTypeId)) {
                    	callRecordDetails.set("responseTypeId", responseTypeId);
                    }
                    if (UtilValidate.isNotEmpty(responseReasonId)) {
                    	 callRecordDetails.set("responseReasonId", responseReasonId);
                    }
                    callRecordDetails.set("csrPartyId", userLogin.getString("userLoginId"));
                    callRecordDetails.set("callStatusId", opportunityStageId);
                    callRecordDetails.create();
    			}else{
    				
                	GenericValue callRecordMasterData = EntityQuery.use(delegator).from("CallRecordMaster").where("entityReferenceId",salesOpportunityId, "entityReferenceTypeId",entityReferenceTypeId).queryFirst();
                	if (UtilValidate.isNotEmpty(callRecordMasterData)) {
                		callRecordMasterData.set("workEffortId", workEffortId);
                		if (UtilValidate.isNotEmpty(callBackDateSql)) {
                			callRecordMasterData.set("callBackDate", callBackDateSql);
                		}else {
                			callRecordMasterData.set("callBackDate", null);
                		}
                		callRecordMasterData.set("ownerId", userLogin.getString("partyId"));
                		callRecordMasterData.set("ownerBusinessUnit", ownerBu);
                		callRecordMasterData.set("callOutCome", callOutCome);
                		callRecordMasterData.set("responseTypeId", responseTypeId);
                		callRecordMasterData.set("responseReasonId", responseReasonId);
                		callRecordMasterData.store();

                		GenericValue callRecordDetails = delegator.makeValue("CallRecordDetails");
                		callRecordDetails.set("callRecordId", callRecordMaster.getString("callRecordId"));
                		callRecordDetails.set("callRecordDetailSeqId", delegator.getNextSeqId("CallRecordDetails"));
                		if (UtilValidate.isNotEmpty(partyId)) {
                			callRecordDetails.set("partyId", partyId);
                		}
                		if (UtilValidate.isNotEmpty(marketingCampaignId)) {
                			callRecordDetails.set("marketingCampaignId", marketingCampaignId);
                		}
                		if (UtilValidate.isNotEmpty(callOutCome)) {
                			callRecordDetails.set("callOutCome", callOutCome);
                		}
                		if (UtilValidate.isNotEmpty(responseTypeId)) {
                			callRecordDetails.set("responseTypeId", responseTypeId);
                		}
                		if (UtilValidate.isNotEmpty(responseReasonId)) {
                			callRecordDetails.set("responseReasonId", responseReasonId);
                		}
                		callRecordDetails.set("csrPartyId", userLogin.getString("userLoginId"));
                		callRecordDetails.set("callStatusId", opportunityStageId);
                		callRecordDetails.create();
                	}
    			}
            }
		} catch (Exception e) {
			Debug.log("==error in updation===" + e.getMessage());
		}
		return results;
	}

	public static Map<String, Object> createCustomerAlert(DispatchContext dctx, Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String alertCategoryName = (String) context.get("alertCategoryName");
		String salesOpportunityId = (String) context.get("salesOpportunityId");
		String alertTypeId = (String) context.get("alertTypeId");
		String alertPriority = (String) context.get("alertPriority");
		String alertStatusId = (String) context.get("alertStatusId");
		String alertAutoClosure = (String) context.get("alertAutoClosure");
		String alertAutoClosureDuration = (String) context.get("alertAutoClosureDuration");
		String remarks = (String) context.get("remarks");
		String alertEntityName = (String) context.get("alertEntityName");
		String alertEntityReferenceId = (String) context.get("alertEntityReferenceId");
		String alertInfo = (String) context.get("alertInfo");
		String alertStartDate = (String) context.get("alertStartDate");
		String alertCategoryId = (String) context.get("alertCategoryId");
		String alertEndDate = (String) context.get("alertEndDate");
		SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy");
		String srNumber = (String) context.get("srNumber");
		Timestamp timeStampEndDate = null;
		Timestamp timeStampStartDate = null;		
		String responseMessage = UtilProperties.getMessage(RESOURCE, "CustomerAlertCreationSuccessful", locale);
		Map<String, Object> results = ServiceUtil.returnSuccess(responseMessage);
		String userLoginId = null;
		try {

			if (UtilValidate.isNotEmpty(alertStartDate)) {
				alertStartDate = df1.format(df2.parse(alertStartDate));
				timeStampStartDate = UtilDateTime.getDayStart(Timestamp.valueOf(alertStartDate));
			}
			if (UtilValidate.isNotEmpty(alertEndDate)) {
				alertEndDate = df1.format(df2.parse(alertEndDate));
				timeStampEndDate = UtilDateTime.getDayEnd(Timestamp.valueOf(alertEndDate));
			}

			GenericValue alertTrackingHistory = delegator.makeValue("AlertTrackingHistory");
			
			String alertTrackingId = delegator.getNextSeqId("AlertTrackingHistory");
			alertTrackingId = UtilGenerator.getAlertTrackingNumber(delegator, alertTrackingId);
			
			alertTrackingHistory.set("alertTrackingId",alertTrackingId);
			alertTrackingHistory.set("alertEntityName", alertEntityName);
			alertTrackingHistory.set("alertEntityReferenceId", alertEntityReferenceId);
			alertTrackingHistory.set("alertCategoryId", alertCategoryId);
			alertTrackingHistory.set("alertInfo", alertInfo);
			alertTrackingHistory.set("alertStartDate", timeStampStartDate);
			alertTrackingHistory.set("alertEndDate", timeStampEndDate);
			alertTrackingHistory.set("createdDate", UtilDateTime.nowTimestamp());
			userLoginId = userLogin.getString("userLoginId");
			alertTrackingHistory.put("createdByUserLogin", userLoginId);
			alertTrackingHistory.put("alertCreatedOn", UtilDateTime.nowTimestamp());
			alertTrackingHistory.put("alertCreatedBy", userLoginId);

			try {
				delegator.setNextSubSeqId(alertTrackingHistory, "alertTrackingSequence", 5, 1);
	            delegator.create(alertTrackingHistory);
	        } catch (GenericEntityException e) {
	            return ServiceUtil.returnError("Customer Alert Creation Failed");
	        }
			results.put("alertTrackingId", alertTrackingId);
			results.put("salesOpportunityId", salesOpportunityId);
			results.put("srNumber", srNumber);
		}catch (Exception e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);

			results = ServiceUtil.returnError("Error : "+e.getMessage());
		}
		return results;
	}
	

	 public static Map<String, Object> UpdateEmplTeam(DispatchContext dctx, Map<String, Object> context) {
		    Delegator delegator = dctx.getDelegator();
			LocalDispatcher dispatcher = dctx.getDispatcher();
			Locale locale = (Locale) context.get("locale");
			Map<String, Object> result = FastMap.newInstance();
	        String csrUserLoginId = (String) context.get("csrUserLoginId");
	        String salesOpportunityId = (String) context.get("salesOpportunityId");
	        String emplTeamId = (String) context.get("emplTeamId");
	        try {
	            List < EntityCondition > conditionlist = FastList.newInstance();
	            if (UtilValidate.isNotEmpty(csrUserLoginId)) {
	                Set < String > fieldsToSelect = new TreeSet < String > ();
		            fieldsToSelect.add("csrUserLoginId");
		            fieldsToSelect.add("salesOpportunityId");
		            
		            GenericValue updateConfigRecords = EntityUtil.getFirst(delegator.findByAnd("CallRecordMasterSummary",
							UtilMisc.toMap("salesOpportunityId", salesOpportunityId), null, false));
					if (UtilValidate.isNotEmpty(updateConfigRecords)) {
								updateConfigRecords.put("csrUserLoginId", csrUserLoginId);
								updateConfigRecords.store();
								result = ServiceUtil.returnSuccess(
										UtilProperties.getMessage(RESOURCE, "ReassignSuccess",locale));
																		}
					
	            }
	            
	            List < EntityCondition > conditionlistTeam = FastList.newInstance();
	            if (UtilValidate.isNotEmpty(emplTeamId)) {
	            	 Set < String > fieldsToSelect = new TreeSet < String > ();
	  	            fieldsToSelect.add("emplTeamId");
	  	          GenericValue updateConfigRecords = EntityUtil.getFirst(delegator.findByAnd("CallRecordMasterSummary",
							UtilMisc.toMap("salesOpportunityId", salesOpportunityId), null, false));
					if (UtilValidate.isNotEmpty(updateConfigRecords)) {
								updateConfigRecords.put("csrUserLoginId", "");
								updateConfigRecords.put("emplTeamId", emplTeamId);
								updateConfigRecords.store();
								result = ServiceUtil.returnSuccess(
										UtilProperties.getMessage(RESOURCE, "ReassignSuccess",locale));
														
							}
	            }
	            
	            
	          
	        } catch (Exception e) {
	           // e.printStackTrace();
	    		Debug.logError(e.getMessage(), MODULE);

				result = ServiceUtil.returnError("Error : "+e.getMessage());
	        }
	        return result;
	    }


	public static Map<String, Object> reassignOpportunity(DispatchContext dctx, Map<String, Object> context) {

		Locale locale = (Locale) context.get("locale");
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = dctx.getDelegator();
		String salesOpportunityId = (String) context.get("salesOpportunityId");
		String assigneeType = (String) context.get("assigneeType");
		String assigneeValue = (String) context.get("assigneeValue");

		try {
			GenericValue salesOpportunitySummary = EntityQuery.use(delegator).from("SalesOpportunitySummary").where("salesOpportunityId", salesOpportunityId).queryOne();
			if (UtilValidate.isNotEmpty(salesOpportunitySummary)) {
				if ("user".equalsIgnoreCase(assigneeType)) {
						salesOpportunitySummary.set("assignedUserLoginId", assigneeValue);
						salesOpportunitySummary.set("emplTeamId", salesOpportunitySummary.getString("emplTeamId"));
				} else if ("team".equalsIgnoreCase(assigneeType)) {
					salesOpportunitySummary.set("assignedUserLoginId", "");
					salesOpportunitySummary.set("emplTeamId", assigneeValue);
				}
				salesOpportunitySummary.store();
				result = ServiceUtil.returnSuccess(
						UtilProperties.getMessage(RESOURCE, "ReassignSuccess",locale));
			}
		} catch (GenericEntityException e) {
			Debug.logError("Unable to reassign the Opportunity", MODULE);
			return ServiceUtil.returnError("Unable to reassign the Opportunity");
		}
		return result;
	}
    public static Map<String, Object> closedServiceActivityDetails(DispatchContext dctx, Map<String, Object> context) {
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");

        Locale locale = (Locale) context.get("locale");
        Map<String, Object> results =ServiceUtil.returnSuccess();
        
        String workEffortId = (String) context.get("workEffortId");
        
        try {
              Set<String> fieldsToSelect = new TreeSet<String>();
              fieldsToSelect.add("workEffortId");
              GenericValue updateConfigRecords = EntityUtil.getFirst(delegator.findByAnd("WorkEffort",
                                           UtilMisc.toMap("workEffortId", workEffortId), null, false));
              if (UtilValidate.isNotEmpty(updateConfigRecords)) {
                 updateConfigRecords.put("workEffortId", workEffortId);
                 updateConfigRecords.put("currentStatusId","IA_MCOMPLETED");
                 updateConfigRecords.store();
                 String responseMessage = UtilProperties.getMessage(RESOURCE, "ServiceViewActivitySuccessfullyClosed", locale);
                 results = ServiceUtil.returnSuccess(responseMessage);
              }
                                                                                                             
        } catch (Exception e) {
                      Debug.log("==error in updation===" + e.getMessage());
        }
        return results;
}

public static Map<String, Object> updateServiceActivityDetails(DispatchContext dctx, Map<String, Object> context) {
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");

        Locale locale = (Locale) context.get("locale");
        Map<String, Object> results =ServiceUtil.returnSuccess();
        
        String workEffortId = (String) context.get("workEffortId");

        String currentStatusId = (String) context.get("currentStatusId");

        try {
                      Set<String> fieldsToSelect = new TreeSet<String>();
                      fieldsToSelect.add("workEffortId");
                      fieldsToSelect.add("currentStatusId");
                      GenericValue updateConfigRecords = EntityUtil.getFirst(delegator.findByAnd("WorkEffort",
                                                   UtilMisc.toMap("workEffortId", workEffortId), null, false));
                      if (UtilValidate.isNotEmpty(updateConfigRecords)) {
                                                                 updateConfigRecords.put("workEffortId", workEffortId);
                                                                 updateConfigRecords.put("currentStatusId", currentStatusId);
                                                                 updateConfigRecords.store();
                                                                 String responseMessage = UtilProperties.getMessage(RESOURCE, "ServiceViewActivitySuccessfullyUpdated", locale);
                                                                 results = ServiceUtil.returnSuccess(responseMessage);
                      }
                                                                                                             
        } catch (Exception e) {
                      Debug.log("==error in updation===" + e.getMessage());
        }
        return results;
}


	public static Map<String, Object> getUserOrTeam(DispatchContext dctx, Map<String, Object> context) {

		Delegator delegator = dctx.getDelegator();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		String parameterType = (String) context.get("parameterType");
		String salesOpportunityId = (String) context.get("salesOpportunityId");

		try {
			if ("user".equalsIgnoreCase(parameterType)) {
				GenericValue salesOpportunitySummary = EntityQuery.use(delegator).from("SalesOpportunitySummary").where("salesOpportunityId", salesOpportunityId).queryOne();
				List<GenericValue> emplPositionFulfillmentList = EntityQuery.use(delegator).select("partyId").from("EmplPositionFulfillment").where("emplTeamId", salesOpportunitySummary.getString("emplTeamId")).queryList();
				Map<String, String> resultMap = new HashMap<>();
				List<String> partyIds = new ArrayList<>();

				for (GenericValue employeePartyId : emplPositionFulfillmentList) {
					partyIds.add(employeePartyId.getString("partyId"));
				}

				List<GenericValue> partyNameView = EntityQuery.use(delegator).from("UserLoginAndPartyDetails").where(EntityCondition.makeCondition("partyId",EntityOperator.IN, partyIds)).queryList();
				for (GenericValue party : partyNameView) {
					resultMap.put(party.getString("userLoginId"), party.getString("firstName") + party.getString("lastName"));
				}
				result.put("resultMap", resultMap);
			} else if ("team".equalsIgnoreCase(parameterType)) {
				GenericValue salesOpportunitySummary = EntityQuery.use(delegator).from("SalesOpportunitySummary").where("salesOpportunityId", salesOpportunityId).queryOne();
				List<GenericValue> emplTeam = EntityQuery.use(delegator).select("emplTeamId", "teamName").from("EmplTeam").where("businessUnit", salesOpportunitySummary.getString("businessUnitId")).queryList();
				Map<String, String> resultMap = new HashMap<>();
				for (GenericValue empl : emplTeam) {
					resultMap.put((String) empl.get("emplTeamId"), (String) empl.get("teamName"));
				}
				result.put("resultMap", resultMap);
			}
		} catch(GenericEntityException e) {
			Debug.logError("Unable to get User or Team", MODULE);
			return ServiceUtil.returnError("Unable to get User or Team");
		}
		return result;
	}
}
