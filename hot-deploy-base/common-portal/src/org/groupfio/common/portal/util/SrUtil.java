/**
 * 
 */
package org.groupfio.common.portal.util;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.fio.admin.portal.util.DataUtil;
import org.fio.homeapps.ResponseCodes;
import org.fio.homeapps.constants.GlobalConstants;
import org.fio.homeapps.util.PartyHelper;
import org.fio.homeapps.util.UtilDateTime;
import org.groupfio.common.portal.extractor.ExtractFacade;
import org.groupfio.common.portal.extractor.constants.ExtractorConstants.ExtractType;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

/**
 * @author Sharif
 *
 */
public class SrUtil {
	
	private static String MODULE = SrUtil.class.getName();
	
	public static GenericValue getSrAssocParty(Delegator delegator, String custRequestId, String roleTypeId) {
		try {
			if (UtilValidate.isNotEmpty(custRequestId)) {
				List conditions = FastList.newInstance();
				
				conditions.add(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId));
				conditions.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, roleTypeId));
				
				conditions.add(EntityUtil.getFilterByDateExpr());
				
                EntityCondition mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);
                GenericValue srParty = EntityQuery.use(delegator).select("partyId").from("CustRequestParty").where(mainConditons).queryFirst();
    			return srParty;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return null;
	}
	
	public static List<String> getSrAssocPartyEmailIds(Delegator delegator, String custRequestId, String statusId, String primary){
		List<String> srAssocPartyEmailIds = new ArrayList<>();
		try{
			if(UtilValidate.isNotEmpty(custRequestId)) {
				List<String> srAssocPartiesRolesList = new ArrayList<>();
				String srAssocPartiesRoles = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, statusId+"_ROLES");
				if(UtilValidate.isNotEmpty(srAssocPartiesRoles)) {
					if(UtilValidate.isNotEmpty(srAssocPartiesRoles) && srAssocPartiesRoles.contains(",")) {
						srAssocPartiesRolesList = org.fio.admin.portal.util.DataUtil.stringToList(srAssocPartiesRoles, ",");
					} else
						srAssocPartiesRolesList.add(srAssocPartiesRoles);
				}
				
				if(UtilValidate.isEmpty(srAssocPartiesRolesList)) srAssocPartiesRolesList.add("SALES_REP");
				/*
				if (UtilValidate.isNotEmpty(primary) 
						&& (primary.equals("CONTRACTOR") || primary.equals("HOME"))
						) {
					srAssocPartiesRolesList.remove("CONTACT");
				}
				*/
				List<EntityCondition> conditionList = FastList.newInstance();
				conditionList.add(EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId),
						EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null),
						EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, srAssocPartiesRolesList),
						EntityCondition.makeCondition(EntityOperator.OR,
								EntityCondition.makeCondition("isEnable", EntityOperator.EQUALS, null),
								EntityCondition.makeCondition("isEnable", EntityOperator.EQUALS, ""),
								EntityCondition.makeCondition("isEnable", EntityOperator.EQUALS, "Y")
							),
						EntityUtil.getFilterByDateExpr()
						));
				EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				List<GenericValue> custRequestPartyList = EntityQuery.use(delegator).from("CustRequestParty").where(mainConditons).queryList();
				
				List<String> srAssocPartyIds = UtilValidate.isNotEmpty(custRequestPartyList) ? EntityUtil.getFieldListFromEntityList(custRequestPartyList, "partyId", true) : new ArrayList<>();
				
				if(UtilValidate.isNotEmpty(srAssocPartiesRolesList) && srAssocPartiesRolesList.contains("CONTACT")) {
					/*
					String srPrimaryPerson = org.fio.homeapps.util.DataUtil.getSrPrimaryPerson(delegator, custRequestId);
					if(UtilValidate.isNotEmpty(srPrimaryPerson) && "DEALER".equals(srPrimaryPerson)){
						String srPrimaryContactId = org.fio.homeapps.util.DataUtil.getSrPrimaryContact(delegator, custRequestId);
						if(UtilValidate.isNotEmpty(srPrimaryContactId)){
							srAssocPartyIds.add(srPrimaryContactId);
						}
					} */
					/*
					String srPrimaryContactId = org.fio.homeapps.util.DataUtil.getSrPrimaryContact(delegator, custRequestId);
					if(UtilValidate.isNotEmpty(srPrimaryContactId)){
						srAssocPartyIds.add(srPrimaryContactId);
					}
					*/
					List<String> srPrimContactList = org.fio.homeapps.util.DataUtil.getSrPrimaryContactList(delegator, custRequestId);
					if(UtilValidate.isNotEmpty(srPrimContactList)){
						srAssocPartyIds.addAll(srPrimContactList);
					}
				}
				
				if(UtilValidate.isNotEmpty(srAssocPartyIds)) {
					
					if("SR_SNT_TO_SRTECH_FOR_SCHLD".equals(statusId)){
						
						List<EntityCondition> partyRelationshipConditionList = FastList.newInstance();
						
						partyRelationshipConditionList.add(EntityCondition.makeCondition(EntityOperator.AND,
								EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "TECHNICIAN"),
								EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "CONTRACT_TYPE"),
								EntityCondition.makeCondition("partyIdTo", EntityOperator.IN, srAssocPartyIds),
								EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null)
								));
						
						EntityCondition partyRelationshipCondition = EntityCondition.makeCondition(partyRelationshipConditionList, EntityOperator.AND);
						
						Set<String> fieldToSelect = new LinkedHashSet<>();
						fieldToSelect.add("partyIdTo");
						
						List<GenericValue> partyRelationshipList = delegator.findList("PartyRelationship", partyRelationshipCondition, fieldToSelect, null, null, false);
						
						List<String> thirdPartyTechnicianPartyIds = UtilValidate.isNotEmpty(partyRelationshipList) ? EntityUtil.getFieldListFromEntityList(partyRelationshipList, "partyIdTo", true) : new ArrayList<>();
						
						if(UtilValidate.isNotEmpty(thirdPartyTechnicianPartyIds)) {
							srAssocPartyIds.clear();
							srAssocPartyIds.addAll(thirdPartyTechnicianPartyIds);
						}
					}
					conditionList = FastList.newInstance();
					conditionList.add(EntityCondition.makeCondition(EntityOperator.AND,
							EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId),
							EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null),
							EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, srAssocPartiesRolesList),
							EntityCondition.makeCondition("isEnable", EntityOperator.EQUALS, "N"),
							EntityUtil.getFilterByDateExpr()
							));
					EntityCondition mainConditons1 = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
					List<GenericValue> custRequestNotPartyList = EntityQuery.use(delegator).from("CustRequestParty").where(mainConditons1).queryList();
					List<String> removePartyIdList = UtilValidate.isNotEmpty(custRequestNotPartyList) ? EntityUtil.getFieldListFromEntityList(custRequestNotPartyList, "partyId", true) : new ArrayList<String>();
					if(UtilValidate.isNotEmpty(removePartyIdList))
						srAssocPartyIds.removeAll(removePartyIdList);
					
					for(String partyId : srAssocPartyIds) {
						Map<String, String> partyContactInformation = PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator,partyId);
						if(UtilValidate.isNotEmpty(partyContactInformation.get("EmailAddress"))) {
							srAssocPartyEmailIds.add(partyContactInformation.get("EmailAddress"));
						}
					}
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return srAssocPartyEmailIds;
	}
	
	public static Map<String, Object> getSrAssocPartyDetails(Delegator delegator, String custRequestId, String statusId, String primary){
		List<String> srAssocPartyEmailIds = new ArrayList<>();
		Map<String, Object> result = new HashMap<String, Object>();
		try{
			if(UtilValidate.isNotEmpty(custRequestId)) {
				List<String> srAssocPartiesRolesList = new ArrayList<>();
				String srAssocPartiesRoles = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, statusId+"_ROLES");
				if(UtilValidate.isNotEmpty(srAssocPartiesRoles)) {
					if(UtilValidate.isNotEmpty(srAssocPartiesRoles) && srAssocPartiesRoles.contains(",")) {
						srAssocPartiesRolesList = org.fio.admin.portal.util.DataUtil.stringToList(srAssocPartiesRoles, ",");
					} else
						srAssocPartiesRolesList.add(srAssocPartiesRoles);
				}
				
				if(UtilValidate.isEmpty(srAssocPartiesRolesList)) srAssocPartiesRolesList.add("SALES_REP");
				/*
				if (UtilValidate.isNotEmpty(primary) 
						&& (primary.equals("CONTRACTOR") || primary.equals("HOME"))
						) {
					srAssocPartiesRolesList.remove("CONTACT");
				}
				*/
				List<EntityCondition> conditionList = FastList.newInstance();
				conditionList.add(EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId),
						EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null),
						EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, srAssocPartiesRolesList),
						EntityUtil.getFilterByDateExpr()
						));
				EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				List<GenericValue> custRequestPartyList = EntityQuery.use(delegator).from("CustRequestParty").where(mainConditons).queryList();
				
				List<String> srAssocPartyIds = UtilValidate.isNotEmpty(custRequestPartyList) ? EntityUtil.getFieldListFromEntityList(custRequestPartyList, "partyId", true) : new ArrayList<>();
				
				if(UtilValidate.isNotEmpty(srAssocPartiesRolesList) && srAssocPartiesRolesList.contains("CONTACT")) {
					/*
					String srPrimaryPerson = org.fio.homeapps.util.DataUtil.getSrPrimaryPerson(delegator, custRequestId);
					if(UtilValidate.isNotEmpty(srPrimaryPerson) && "DEALER".equals(srPrimaryPerson)){
						String srPrimaryContactId = org.fio.homeapps.util.DataUtil.getSrPrimaryContact(delegator, custRequestId);
						if(UtilValidate.isNotEmpty(srPrimaryContactId)){
							srAssocPartyIds.add(srPrimaryContactId);
						}
					} */
					/*
					String srPrimaryContactId = org.fio.homeapps.util.DataUtil.getSrPrimaryContact(delegator, custRequestId);
					if(UtilValidate.isNotEmpty(srPrimaryContactId)){
						srAssocPartyIds.add(srPrimaryContactId);
					}
					*/
					List<String> srPrimContactList = org.fio.homeapps.util.DataUtil.getSrPrimaryContactList(delegator, custRequestId);
					if(UtilValidate.isNotEmpty(srPrimContactList)){
						srAssocPartyIds.addAll(srPrimContactList);
					}
				}
				
				if(UtilValidate.isNotEmpty(srAssocPartyIds)) {
					
					if("SR_SNT_TO_SRTECH_FOR_SCHLD".equals(statusId)){
						
						List<EntityCondition> partyRelationshipConditionList = FastList.newInstance();
						
						partyRelationshipConditionList.add(EntityCondition.makeCondition(EntityOperator.AND,
								EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "TECHNICIAN"),
								EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "CONTRACT_TYPE"),
								EntityCondition.makeCondition("partyIdTo", EntityOperator.IN, srAssocPartyIds),
								EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null)
								));
						
						EntityCondition partyRelationshipCondition = EntityCondition.makeCondition(partyRelationshipConditionList, EntityOperator.AND);
						
						Set<String> fieldToSelect = new LinkedHashSet<>();
						fieldToSelect.add("partyIdTo");
						
						List<GenericValue> partyRelationshipList = delegator.findList("PartyRelationship", partyRelationshipCondition, fieldToSelect, null, null, false);
						
						List<String> thirdPartyTechnicianPartyIds = UtilValidate.isNotEmpty(partyRelationshipList) ? EntityUtil.getFieldListFromEntityList(partyRelationshipList, "partyIdTo", true) : new ArrayList<>();
						
						if(UtilValidate.isNotEmpty(thirdPartyTechnicianPartyIds)) {
							srAssocPartyIds.clear();
							srAssocPartyIds.addAll(thirdPartyTechnicianPartyIds);
						}
					}
					
					for(String partyId : srAssocPartyIds) {
						Map<String, String> partyContactInformation = PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator,partyId);
						if(UtilValidate.isNotEmpty(partyContactInformation.get("EmailAddress"))) {
							srAssocPartyEmailIds.add(partyContactInformation.get("EmailAddress"));
						}
					}
					result.put("emailPartyIds", srAssocPartyIds);
					result.put("emailIds", srAssocPartyEmailIds);
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static List<String> getSrScheduledStatusList(Delegator delegator, String custRequestId){
		List<String> srScheduledStatusesList = new ArrayList<>();
		String srScheduledStatuses = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SR_SCHEDULED_STATUSES");
		if(UtilValidate.isNotEmpty(srScheduledStatuses)) {
			if(UtilValidate.isNotEmpty(srScheduledStatuses) && srScheduledStatuses.contains(",")) {
				srScheduledStatusesList = org.fio.admin.portal.util.DataUtil.stringToList(srScheduledStatuses, ",");
			} else
				srScheduledStatusesList.add(srScheduledStatuses);
		}
		return srScheduledStatusesList;
	}
	
	public static String getSrPrimaryPerson(Delegator delegator, String custRequestId) {
		String attrValue="";
		try {
			if (UtilValidate.isNotEmpty(custRequestId)) {
				GenericValue custRequestAttribute = EntityQuery.use(delegator).select("attrValue").from("CustRequestAttribute").where("custRequestId", custRequestId, "attrName", "PRIMARY").queryFirst();
				if (UtilValidate.isNotEmpty(custRequestAttribute)) {
					attrValue = custRequestAttribute.getString("attrValue");
					return attrValue;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return attrValue;
	}
	
	public static List<Map<String, Object>> getSrAssocParties(Delegator delegator, String custRequestId) {
		List < Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		try {
			if (UtilValidate.isNotEmpty(custRequestId)) {
				String globalDateTimeFormat = org.groupfio.common.portal.util.DataHelper.getGlobalDateTimeFormat(delegator);
				
				List<EntityCondition> conditions = new ArrayList<EntityCondition>();
				conditions.add(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId));
				
				EntityCondition condition = EntityCondition.makeCondition(conditions, EntityOperator.AND);
				
				DynamicViewEntity dynamicViewEntity = new DynamicViewEntity();
				dynamicViewEntity.addMemberEntity("CR", "CustRequest");
				dynamicViewEntity.addAlias("CR", "custRequestId");
				dynamicViewEntity.addAlias("CR", "custRequestName");
				dynamicViewEntity.addAlias("CR", "description");
				
				dynamicViewEntity.addMemberEntity("CRP", "CustRequestParty");
				dynamicViewEntity.addAlias("CRP", "partyId");
				dynamicViewEntity.addAlias("CRP", "roleTypeId");
				dynamicViewEntity.addAlias("CRP", "fromDate");
				dynamicViewEntity.addAlias("CRP", "thruDate");
				dynamicViewEntity.addAlias("CRP", "lastUpdatedTxStamp");
				dynamicViewEntity.addAlias("CRP", "isEnable");
				dynamicViewEntity.addViewLink("CR", "CRP", Boolean.FALSE, ModelKeyMap.makeKeyMapList("custRequestId"));
				
				List<GenericValue> custRequestPartyList = EntityQuery.use(delegator).from(dynamicViewEntity).where(condition).filterByDate().queryList();
				if(UtilValidate.isNotEmpty(custRequestPartyList)) {
					String existPrimaryTechnicianId = "";
					Map<String, Object> anchorPartyMap = getCustRequestAnchorParties(delegator, custRequestId);
					if(UtilValidate.isNotEmpty(anchorPartyMap)) {
						existPrimaryTechnicianId = UtilValidate.isNotEmpty(anchorPartyMap.get("TECHNICIAN")) ? (String) anchorPartyMap.get("TECHNICIAN") : "";
					}
					for(GenericValue custRequestParty : custRequestPartyList) {
						Map<String, Object> data = new HashMap<>();
						String partyId = custRequestParty.getString("partyId");
						String customerName = PartyHelper.getPartyName(delegator, partyId, false);
						
						Map<String,String> partyContactInfo = PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator, partyId);
						String phoneNumber = UtilValidate.isNotEmpty(partyContactInfo) ? partyContactInfo.get("PrimaryPhone") : "";
						String infoString = UtilValidate.isNotEmpty(partyContactInfo) ? partyContactInfo.get("EmailAddress") : "";
						
						String phoneSolicitation = UtilValidate.isNotEmpty(partyContactInfo) ? partyContactInfo.get("phoneSolicitation") : "";
						String emailSolicitation = UtilValidate.isNotEmpty(partyContactInfo) ? partyContactInfo.get("emailSolicitation") : "";
						
						data.putAll(DataUtil.convertGenericValueToMap(delegator, custRequestParty));
						data.put("phoneNumber", DataUtil.formatPhoneNumber(phoneNumber) );
						data.put("infoString", infoString );
						data.put("name", customerName);
						data.put("phoneSolicitation", phoneSolicitation );
						data.put("emailSolicitation", emailSolicitation );
						data.put("roleTypeDesc", org.groupfio.common.portal.util.DataUtil.getRoleTypeDescription(delegator, custRequestParty.getString("roleTypeId")));
						
						if(UtilValidate.isNotEmpty(existPrimaryTechnicianId) && partyId.equals(existPrimaryTechnicianId)) {
							data.put("isPrimaryTech", "Y" );
						} else 
							data.put("isPrimaryTech", "N" );
						
						data.put("fromDate", UtilDateTime.timeStampToString(custRequestParty.getTimestamp("fromDate"), globalDateTimeFormat, TimeZone.getDefault(), Locale.getDefault()));
						data.put("lastUpdatedTxStamp", UtilDateTime.timeStampToString(custRequestParty.getTimestamp("lastUpdatedTxStamp"), globalDateTimeFormat, TimeZone.getDefault(), Locale.getDefault()));
						
						dataList.add(data);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return dataList;
	}
	
	public static String getCustRequestAttrValue(Delegator delegator, String attrName, String custRequestId) {
    	String idValue = null;
		try {
			if (UtilValidate.isNotEmpty(attrName) && UtilValidate.isNotEmpty(custRequestId)) {
				EntityCondition mainCondition = EntityCondition.makeCondition(EntityOperator.AND,
	                    EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, attrName),
	                    EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId)
	                    );
				GenericValue custReqAttribute = EntityQuery.use(delegator).select("attrValue").from("CustRequestAttribute").where(mainCondition).cache(true).queryFirst();
				if(UtilValidate.isNotEmpty(custReqAttribute)){
					idValue = custReqAttribute.getString("attrValue");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return idValue;
	}
	
	public static String getCustRequestName(Delegator delegator, String custRequestId) {
		try {
			if (UtilValidate.isNotEmpty(custRequestId)) {
				EntityCondition mainCondition = EntityCondition.makeCondition(EntityOperator.AND,
	                    EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId)
	                    );
				GenericValue custReqAttribute = EntityQuery.use(delegator).select("custRequestName").from("CustRequest").where(mainCondition).queryFirst();
				if(UtilValidate.isNotEmpty(custReqAttribute)){
					return custReqAttribute.getString("custRequestName");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return null;
	}
	
	public static String getSrOrderIds(Delegator delegator, String purchaseOrder, String orderTypeId) {
		String ids = "";
		try {
			if (UtilValidate.isNotEmpty(purchaseOrder)) {
				List<String> orderIds = Arrays.asList( purchaseOrder.split(",") );
				for (String orderId : orderIds) {
					if (orderId.contains(orderTypeId)) {
						orderId = orderId.substring(0, orderId.lastIndexOf("-"));
						ids += orderId+",";
					}
				}
				
				if (UtilValidate.isNotEmpty(ids)) {
					ids = ids.substring(0, ids.length()-1);
					orderIds = Arrays.asList( ids.split(",") );
					
					List conditions = FastList.newInstance();
					
					conditions.add(EntityCondition.makeCondition("orderId", EntityOperator.IN, orderIds));
					
					//conditions.add(EntityCondition.makeCondition("domainEntityId", EntityOperator.EQUALS, custRequestId));
					//conditions.add(EntityCondition.makeCondition("domainEntityType", EntityOperator.EQUALS, "SERVICE_REQUEST"));
					
		            EntityCondition mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);
		            //List<GenericValue> entityList = delegator.findList("EntityOrderLineAssoc", mainConditons, UtilMisc.toSet("externalId"), null, null, false);
		            List<GenericValue> entityList = EntityQuery.use(delegator).select("externalId").from("EntityOrderLineAssoc").where(mainConditons).cache(true).queryList();
		            if (UtilValidate.isNotEmpty(entityList)) {
						return entityList.stream().filter(x->UtilValidate.isNotEmpty(x.getString("externalId"))).map(x->x.getString("externalId")).distinct().collect(Collectors.joining(","));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return ids;
	}
	
	public static String getSrOrderIds(Delegator delegator, String custRequestId) {
		try {
			if (UtilValidate.isNotEmpty(custRequestId)) {
				List conditions = FastList.newInstance();
				
				conditions.add(EntityCondition.makeCondition("domainEntityId", EntityOperator.EQUALS, custRequestId));
				conditions.add(EntityCondition.makeCondition("domainEntityType", EntityOperator.EQUALS, "SERVICE_REQUEST"));
				
                EntityCondition mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);
                //List<GenericValue> entityList = delegator.findList("EntityOrderLineAssoc", mainConditons, UtilMisc.toSet("externalId"), null, null, false);
                List<GenericValue> entityList = EntityQuery.use(delegator).select("externalId").from("EntityOrderLineAssoc").where(mainConditons).cache(true).queryList();
                if (UtilValidate.isNotEmpty(entityList)) {
    				return entityList.stream().filter(x->UtilValidate.isNotEmpty(x.getString("externalId"))).map(x->x.getString("externalId")).distinct().collect(Collectors.joining(","));
    			}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return null;
	}
	
	public static String getSrNumber(Delegator delegator, String orderId, String lineItemNumber) {
		try {
			if (UtilValidate.isNotEmpty(orderId)) {
				List conditions = FastList.newInstance();
				String viewServiceRequest = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "VIEW_SR_URL", "/sr-portal/control/viewServiceRequest?srNumber");
				
				conditions.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
				conditions.add(EntityCondition.makeCondition("domainEntityType", EntityOperator.EQUALS, "SERVICE_REQUEST"));
				
				if (UtilValidate.isNotEmpty(lineItemNumber)) {
					conditions.add(EntityCondition.makeCondition("lineItemNumber", EntityOperator.EQUALS, lineItemNumber));
				}
				
                EntityCondition mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);
                List<GenericValue> orderLineList = delegator.findList("EntityOrderLineAssoc", mainConditons, UtilMisc.toSet("domainEntityId"), null, null, false);
    			if (UtilValidate.isNotEmpty(orderLineList)) {
    				String srNumber = orderLineList.stream()
    				.filter(x->{
    					if (UtilValidate.isNotEmpty(x.getString("domainEntityId"))) {
    						return true;
    					}
    					return false;
    				})
    				.map(x->{
    					String srId = x.getString("domainEntityId");
    					
    					return "<a target='_blank' href='"+viewServiceRequest+"="+srId+"'>"+srId+"</a>";
    				}).collect(Collectors.joining(",<br/>"));
    				return srNumber;
    			}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return null;
	}
	
	public static String getSrNumberNonLink(Delegator delegator, String orderId, String lineItemNumber) {
		try {
			if (UtilValidate.isNotEmpty(orderId)) {
				List conditions = FastList.newInstance();
				String viewServiceRequest = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "VIEW_SR_URL", "/sr-portal/control/viewServiceRequest?srNumber");
				
				conditions.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
				conditions.add(EntityCondition.makeCondition("domainEntityType", EntityOperator.EQUALS, "SERVICE_REQUEST"));
				
				if (UtilValidate.isNotEmpty(lineItemNumber)) {
					conditions.add(EntityCondition.makeCondition("lineItemNumber", EntityOperator.EQUALS, lineItemNumber));
				}
				
                EntityCondition mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);
                List<GenericValue> orderLineList = delegator.findList("EntityOrderLineAssoc", mainConditons, UtilMisc.toSet("domainEntityId"), null, null, false);
    			if (UtilValidate.isNotEmpty(orderLineList)) {
    				String srNumber = orderLineList.stream()
    				.filter(x->{
    					if (UtilValidate.isNotEmpty(x.getString("domainEntityId"))) {
    						return true;
    					}
    					return false;
    				})
    				.map(x-> x.getString("domainEntityId")).collect(Collectors.joining(", "));
    				return srNumber;
    			}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return null;
	}
	
	public static void triggerSrStatusEmail(Delegator delegator, LocalDispatcher dispatcher, GenericValue userLogin, List<GenericValue> resultList, List<GenericValue> emailConfigList, Map<String, Object> emailTemplates) {
		try {
			if (UtilValidate.isNotEmpty(resultList)) {
				String defaultFromEmailId = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "FROM_EMAIL_ID");
				String toEmailId = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SR_STS_ESC_TO_EMAIL");
				Map<String, List<GenericValue>> srListByStatus = new LinkedHashMap<String, List<GenericValue>>();
				
				for (GenericValue emailConfig : emailConfigList) {
					String statusId = emailConfig.getString("statusId");
					List<GenericValue> srList = resultList;
					if (!statusId.equals("SR_CLOSED")) {
						srList = resultList.stream().filter(x->x.getString("statusId").equals(statusId)).collect(Collectors.toList());
					}
					if (UtilValidate.isNotEmpty(srList)) {
						srListByStatus.put(statusId, srList);
					}
				}
				
				srListByStatus.entrySet().stream()
			      .forEach(e -> {
			    	  
			    	  	String statusId = e.getKey();
			    		List<GenericValue> srList = e.getValue();	  
			    		
			    		if (UtilValidate.isNotEmpty(srList)) {
			    			String templateId = (String) emailTemplates.get(statusId);
			    			
			    			try {
								if(UtilValidate.isNotEmpty(templateId)) {
									
									Map<String, Object> callCtxt = FastMap.newInstance();
									GenericValue template = delegator.findOne("TemplateMaster", UtilMisc.toMap("templateId",templateId), false);

									String emailContent = "";
									String templateFormContent = template.getString("templateFormContent");
									if (UtilValidate.isNotEmpty(templateFormContent)) {
										if (org.apache.commons.codec.binary.Base64.isBase64(templateFormContent)) {
											templateFormContent = org.ofbiz.base.util.Base64.base64Decode(templateFormContent);
										}
									}
									
									String fromEmail = defaultFromEmailId;
									
									String toEmail = toEmailId;
									//String toEmail = "sislam131@gmail.com";
									
									if (UtilValidate.isEmpty(toEmail)) {
										Debug.logError("toEmail not found", MODULE);
										return;
									}
									
									// prepare email content [start]
									Map<String, Object> extractContext = new LinkedHashMap<String, Object>();
									extractContext.put("delegator", delegator);
									extractContext.put("extractType", ExtractType.EXTRACT_EMAIL_DATA);
									extractContext.put("fromEmail", fromEmail);
									extractContext.put("toEmail", toEmail);
									extractContext.put("listOfFsr", srList);
									extractContext.put("emailContent", templateFormContent);
									extractContext.put("templateId", templateId);

									Map<String, Object> extractResultContext = ExtractFacade.extractData(extractContext);
									emailContent = (String) extractResultContext.get("emailContent");
									// prepare email content [end]

									Map<String, Object> rc = FastMap.newInstance();

									rc.put("nsender", fromEmail);
									rc.put("nto", toEmail);
									rc.put("subject", template.getString("subject"));
									rc.put("emailContent", emailContent);
									rc.put("templateId", templateId);
									//requestContext.put("ccAddresses", ccAddresses);

									callCtxt.put("requestContext", rc);
									callCtxt.put("userLogin", userLogin);

									Debug.log("===== FSR STATUS ESC EMAIL TRIGGER ===="+callCtxt);

									dispatcher.runAsync("common.sendEmail", callCtxt);
								}
							} catch (Exception e1) {
								e1.printStackTrace();
							}
			    		}
			      });
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
	}
	
	public static long getIssuedMaterialQty(Delegator delegator, String custRequestId, String workEffortId, String partyId) {
		try {
			if (UtilValidate.isNotEmpty(custRequestId)) {
				List conditions = FastList.newInstance();
					
				conditions.add(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId));
				if (UtilValidate.isNotEmpty(workEffortId)) {
					conditions.add(EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId));
				}
				if (UtilValidate.isNotEmpty(partyId)) {
					conditions.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
				}
				
				EntityCondition mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);
            	
            	List<GenericValue> issueMaterialList = EntityQuery.use(delegator).from("IssueMaterial").where(mainConditons).queryList();
            	long totalIssuedMaterialQty = 0;
            	for(GenericValue issueMaterial : issueMaterialList) {
        			if (UtilValidate.isNotEmpty(issueMaterial.getString("quantity"))) {
        				BigDecimal quantity = new BigDecimal(issueMaterial.getString("quantity"));
        				totalIssuedMaterialQty = totalIssuedMaterialQty + quantity.longValue();
        			}
            	}
            	return totalIssuedMaterialQty;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return 0L;
	}
	
	public static String getOrderType(Delegator delegator, String orderNumber) {
		try {
			if (UtilValidate.isNotEmpty(orderNumber)) {
				String letter = orderNumber.substring(orderNumber.length()-2, orderNumber.length()-1);
				if (UtilValidate.isNotEmpty(letter)) {
					List conditions = FastList.newInstance();
					
					conditions.add(EntityCondition.makeCondition("letter", EntityOperator.EQUALS, letter));
					EntityCondition mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);
					
					GenericValue orderType = EntityQuery.use(delegator).from("EntityOrderType").where(mainConditons).queryFirst();
					if (UtilValidate.isNotEmpty(orderType)) {
						return orderType.getString("orderTypeId");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return null;
	}
	
	public static String getProductStoreIdFromWWLocation(Delegator delegator, String locationId) {
		try {
			if (UtilValidate.isNotEmpty(locationId)) {
				List conditions = FastList.newInstance();
				
				conditions.add(EntityCondition.makeCondition("wwLocationId", EntityOperator.EQUALS, locationId));
				EntityCondition mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);
				
				GenericValue entity = EntityQuery.use(delegator).from("ProductStore").where(mainConditons).queryFirst();
				if (UtilValidate.isNotEmpty(entity)) {
					return entity.getString("productStoreId");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return null;
	}
	
	public static void createCustRequestAnchorParty(Delegator delegator, String custRequestId, String partyId, String roleTypeId) {
		try {
			String customFieldName = "ANR_"+roleTypeId;
			String customFieldId = DataHelper.getCustomFieldId(delegator, "ANCHOR_ROLES", customFieldName);
			if(UtilValidate.isNotEmpty(customFieldId)) {
				GenericValue custRequestAttr = EntityQuery.use(delegator).from("CustRequestAttribute").where("custRequestId", custRequestId, "attrName", customFieldId).queryFirst();
				if(UtilValidate.isNotEmpty(custRequestAttr)) {
					custRequestAttr.set("attrValue", partyId);
					custRequestAttr.store();
				} else {
					custRequestAttr = delegator.makeValue("CustRequestAttribute");
					custRequestAttr.set("custRequestId", custRequestId);
					custRequestAttr.set("attrName", customFieldId);
					custRequestAttr.set("attrValue", partyId);
					custRequestAttr.set("channelId", "ANCHOR_ROLES");
					custRequestAttr.create();
				}
			} else {
				Debug.logInfo("Please configure attribute field with following customFieldId : "+customFieldName, MODULE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String getCustRequestAnchorParty(Delegator delegator, String custRequestId, String roleTypeId) {
		String attrValue = "";
		try {
			if(UtilValidate.isNotEmpty(custRequestId)) {
				Map<String, Object> result = getCustRequestAnchorParties(delegator, custRequestId);
				attrValue = UtilValidate.isNotEmpty(result) ? (String) result.get(roleTypeId) : "";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return attrValue;
	}
	
	public static Map<String, Object> getCustRequestAnchorParties(Delegator delegator, String custRequestId){
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			List<GenericValue> custRequestAttrList = EntityQuery.use(delegator).from("CustRequestAttribute").where("custRequestId", custRequestId,  "channelId", "ANCHOR_ROLES").queryList();
			if(UtilValidate.isNotEmpty(custRequestAttrList)) {
				for(GenericValue custRequestAttr : custRequestAttrList) {
					String customFieldId = custRequestAttr.getString("attrName");
					GenericValue customField = EntityQuery.use(delegator).from("CustomField").where("customFieldId", customFieldId, "groupId", "ANCHOR_ROLES").queryFirst();
					if(UtilValidate.isNotEmpty(customField)) {
						String customFieldName = customField.getString("customFieldName");
						customFieldName = UtilValidate.isNotEmpty(customFieldName) ? customFieldName.substring(customFieldName.indexOf("_")+1) : "";
						result.put(customFieldName, custRequestAttr.getString("attrValue"));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static Map<String, Object> srStatusChanged(Delegator delegator, LocalDispatcher dispatcher, GenericValue userLogin, String custRequestId, String statusId) {
		Map<String, Object> result = new LinkedHashMap<>();
		try {
			if (UtilValidate.isNotEmpty(custRequestId)) {
				GenericValue sr = EntityQuery.use(delegator).from("CustRequest").where("custRequestId", custRequestId).queryFirst();
				if (UtilValidate.isNotEmpty(sr)) {
					
					String previousStatusId = sr.getString("statusId");
					
					sr.put("statusId", statusId);
					sr.store();
					
					String custRequestName = sr.getString("custRequestName");
					String fromPartyId = sr.getString("fromPartyId");
					String owner = sr.getString("responsiblePerson");
					custRequestId = sr.getString("custRequestId");
					
					String primary = SrUtil.getCustRequestAttrValue(delegator, "PRIMARY", custRequestId);
					String nsender = null;
					String nto = null;
					
					GenericValue sytemProperty = EntityQuery.use(delegator).from("SystemProperty").where("systemResourceId", "NOTIFICATION", "systemPropertyId", "from").queryOne();
					if(UtilValidate.isNotEmpty(sytemProperty)){
						nsender = sytemProperty.getString("systemPropertyValue");
					}
					nto = org.fio.homeapps.util.PartyHelper.getEmailAddress(delegator, org.fio.homeapps.util.DataUtil.getPartyIdByUserLoginId(delegator, owner), "PRIMARY_EMAIL");
					String ccAddresses = null;
					
					List<String> srAssocPartiesEmailIds = SrUtil.getSrAssocPartyEmailIds(delegator, custRequestId, statusId, primary);
					if (UtilValidate.isNotEmpty(srAssocPartiesEmailIds)) {
						for (String eachAssocEmailId : srAssocPartiesEmailIds) {
							ccAddresses = ccAddresses + eachAssocEmailId + ",";
						}
						ccAddresses = ccAddresses.substring(0, ccAddresses.length() - 1);
					}
					String srEmailNotify = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SR_EMAIL_NOTIFIY", "Y");

					if (UtilValidate.isNotEmpty(nsender) && UtilValidate.isNotEmpty(nto) && "Y".equals(srEmailNotify)){
						String templateId = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, statusId+"_TEMPLATE");
						if(UtilValidate.isNotEmpty(templateId)) {
							GenericValue emailTemlateData = delegator.findOne("TemplateMaster", UtilMisc.toMap("templateId",templateId), false);
							String emailContent = "";
							String subject = "";
							//String templateFormContent = emailTemlateData.getString("textContent");
							String templateFormContent = emailTemlateData.getString("templateFormContent");
							if (UtilValidate.isNotEmpty(templateFormContent)) {
								if (org.apache.commons.codec.binary.Base64.isBase64(templateFormContent)) {
									templateFormContent = org.ofbiz.base.util.Base64.base64Decode(templateFormContent);
								}
							}
							if(UtilValidate.isNotEmpty(emailTemlateData.getString("subject"))) {
								subject = "FSR# "+custRequestId+" ("+custRequestName+") - "+emailTemlateData.getString("subject");
							}

							// prepare email content [start]
							Map<String, Object> extractContext = new LinkedHashMap<String, Object>();
							extractContext.put("delegator", delegator);
							extractContext.put("extractType", ExtractType.EXTRACT_EMAIL_DATA);
							extractContext.put("fromEmail", nsender);
							extractContext.put("toEmail", nto);
							extractContext.put("partyId", fromPartyId);
							extractContext.put("custRequestId", custRequestId);
							extractContext.put("emailContent", templateFormContent);
							extractContext.put("templateId", templateId);

							Map<String, Object> extractResultContext = ExtractFacade.extractData(extractContext);
							emailContent = (String) extractResultContext.get("emailContent");

							Map<String, Object> callCtxt = FastMap.newInstance();
							Map<String, Object> callResult = FastMap.newInstance();
							Map<String, Object> requestContext = FastMap.newInstance();

							requestContext.put("nsender", nsender);
							requestContext.put("nto", nto);
							requestContext.put("subject", subject);
							requestContext.put("emailContent", emailContent);
							requestContext.put("templateId", templateId);
							//requestContext.put("ccAddresses", ccAddresses);
							requestContext.put("nbcc", ccAddresses);

							callCtxt.put("requestContext", requestContext);
							callCtxt.put("userLogin", userLogin);

							Debug.log("==== Change SR Status sendEmail ===="+callCtxt);

							callResult = dispatcher.runSync("common.sendEmail", callCtxt);
							if (ServiceUtil.isError(callResult)) {
								String errMsg = "Email send failed: "+ServiceUtil.getErrorMessage(callResult);
								result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
								result.put(GlobalConstants.RESPONSE_MESSAGE, errMsg);
								Debug.logError(errMsg, MODULE);
							} else {
								String successMsg = "Successfully changed SR Status: "+statusId;
								result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
								result.put(GlobalConstants.RESPONSE_MESSAGE, successMsg);
								Debug.logInfo(successMsg, MODULE);
							}
						}
					}
					
					//send sms by twilio
					String twilioSetup = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "TWILIO_SETUP", "N");
					if(UtilValidate.isNotEmpty(twilioSetup) && "Y".equals(twilioSetup)) {
						String srSMSStatusStr = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SR_TWILIO_SMS_NOTIFIY_STATUS");
						List<String> srSMSStatusList = UtilValidate.isNotEmpty(srSMSStatusStr) ? org.fio.admin.portal.util.DataUtil.stringToList(srSMSStatusStr, ",") : new ArrayList<String>();
						
						String twilioSenderNumber = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "twilio-sender-phone-number");
						Map<String, Object> srPartyPhoneNumMap = new HashMap<String, Object>();
						if(UtilValidate.isNotEmpty(srSMSStatusList) && UtilValidate.isNotEmpty(statusId) && !(previousStatusId.equals(statusId)) && srSMSStatusList.contains(statusId)){							
							srPartyPhoneNumMap = SrUtil.getSrAssocPartyPhones(delegator, custRequestId, statusId, primary);
						}
						if(UtilValidate.isNotEmpty(srPartyPhoneNumMap)) {
							String applicationUrl = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "APPLICATION_URL");
							String smsBody = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, statusId+"_SMS_CONTENT");
							Map<String, Object> contentInfo = new HashMap<String, Object>();
							contentInfo.put("content", smsBody);
							contentInfo.put("SR_NUMBER", custRequestId);
							if(UtilValidate.isNotEmpty(applicationUrl))
								contentInfo.put("SR_SHORTURL", applicationUrl+"/sr-portal/control/viewServiceRequest?srNumber="+custRequestId);
							contentInfo.put("SR_NAME", custRequestName);
							
							smsBody = org.fio.admin.portal.util.DataUtil.extractContentWithTag(contentInfo);
							
							for(String srPartyId : srPartyPhoneNumMap.keySet()) {
								Map<String, Object> ctx = new HashMap<String, Object>();
								String phoneNumber = (String) srPartyPhoneNumMap.get(srPartyId);
								String requestData = "";
								String responseData = "";
								Debug.logInfo("smsBody----->"+smsBody, MODULE);
								boolean isSmsSend = false;
								String responseMsg = "";
								if(UtilValidate.isNotEmpty(phoneNumber) && UtilValidate.isNotEmpty(smsBody)) {
									
									Map<String, Object> ctx1 = new HashMap<String, Object>();
									ctx1.put("toPhoneNumber", UtilMisc.toList(phoneNumber));
									Map<String, Object> smsResult1 =  dispatcher.runSync("twilio.validateToNumber", ctx1);
									List<String> wrongPhoneList = new ArrayList<String>();
									if(UtilValidate.isNotEmpty(smsResult1) && UtilValidate.isNotEmpty(smsResult1.get("successMessage")) && ((String) smsResult1.get("successMessage")).contains("Error : ")) {
										smsResult1 = (Map<String, Object>) smsResult1.get("result");
									  	wrongPhoneList = (List<String>) smsResult1.get("wrongPhoneNumber");
										responseMsg = "The following to phone number is wrong : "+org.fio.admin.portal.util.DataUtil.listToString(wrongPhoneList);
										responseData = "{\"errorMessage\":\""+responseMsg+"\"}";
									}  else {
										ctx.put("toPhoneNumber", phoneNumber);
										ctx.put("fromPhoneNumber", twilioSenderNumber);
										ctx.put("subject", smsBody);
										
										requestData = org.fio.admin.portal.util.DataUtil.convertToJsonStr(ctx);
										
										Map<String, Object> smsResult =  dispatcher.runSync("twilio.sendSms", ctx);
										if( UtilValidate.isNotEmpty(smsResult)  && UtilValidate.isNotEmpty(smsResult.get("successMessage")) && ((String) smsResult.get("successMessage")).contains("Error : ")) {
											Debug.logInfo("Exception in sendSms :"+ServiceUtil.getErrorMessage(smsResult), MODULE);
											isSmsSend = false;
											smsResult = (Map<String, Object>) smsResult.get("result");
											responseData = UtilValidate.isNotEmpty(smsResult) ? (String) smsResult.get("response") : "";
											responseMsg = UtilValidate.isNotEmpty(smsResult) ? (String) smsResult.get("response") : "Error while sending sms to "+phoneNumber;
											Debug.logError("sms response :" +responseMsg, MODULE);
										} else {
											if(UtilValidate.isNotEmpty(smsResult)) {
												smsResult = (Map<String, Object>) smsResult.get("result");
												responseData = UtilValidate.isNotEmpty(smsResult) ? (String) smsResult.get("response") : "";
											}
											isSmsSend = true;
											responseMsg = "SMS has been sent successfully";
										}
									}
									
									
									
								} else {
									if(UtilValidate.isEmpty(phoneNumber))
										responseMsg = UtilValidate.isNotEmpty(responseMsg) ? " Phone number is not Exist" : "Phone number is not Exist";
									if(UtilValidate.isEmpty(smsBody))
										responseMsg = UtilValidate.isNotEmpty(responseMsg) ? " SMS content is not Exist" : "SMS content is not Exist";
								}
								
								//Store audit logs
								String twilioLogId = delegator.getNextSeqId("TwilioAuditLog");
								GenericValue twilioAuditLog = delegator.makeValue("TwilioAuditLog");
								twilioAuditLog.set("twilioLogId", twilioLogId);
								twilioAuditLog.set("typeId", "SMS");
								twilioAuditLog.set("domainEntityType", "SERVICE_REQUEST");
								twilioAuditLog.set("domainEntityId", custRequestId);
								twilioAuditLog.set("fromPhoneNumber", twilioSenderNumber);
								twilioAuditLog.set("toPhoneNumber", phoneNumber);
								twilioAuditLog.set("requestData", requestData);
								twilioAuditLog.set("responseData", responseData);
								twilioAuditLog.set("status", isSmsSend ? "SUCCESS" : "ERROR");
								twilioAuditLog.set("comments", responseMsg);
								twilioAuditLog.create();
							}
						}
					} else {
						String isEnabledsandboxMessage = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SANDBOX_MESSAGE_SETUP", "N");
						if("Y".equals(isEnabledsandboxMessage)) {
							String srSMSStatusStr = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SR_SMS_NOTIFIY_STATUS");
							List<String> srSMSStatusList = UtilValidate.isNotEmpty(srSMSStatusStr) ? org.fio.admin.portal.util.DataUtil.stringToList(srSMSStatusStr, ",") : new ArrayList<String>();
							
							String senderNumber = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "sender-phone-number");
							Map<String, Object> srPartyPhoneNumMap = new HashMap<String, Object>();
							if(UtilValidate.isNotEmpty(srSMSStatusList) && UtilValidate.isNotEmpty(statusId) && !(previousStatusId.equals(statusId)) && srSMSStatusList.contains(statusId)){							
								srPartyPhoneNumMap = SrUtil.getSrAssocPartyPhones(delegator, custRequestId, statusId, primary);
							}
							if(UtilValidate.isNotEmpty(srPartyPhoneNumMap)) {
								String applicationUrl = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "APPLICATION_URL");
								String smsBody = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, statusId+"_SMS_CONTENT");
								
								String merchantNumber = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SANDBOX_SMS_MERCHANT_NUMBER");
								String storeNumber = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SANDBOX_SMS_STORE_NUMNER");
								Map<String, Object> contentInfo = new HashMap<String, Object>();
								contentInfo.put("content", smsBody);
								contentInfo.put("SR_NUMBER", custRequestId);
								
								String srTrackerUrl = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SR_TRACKER_URL");
								if(UtilValidate.isNotEmpty(srTrackerUrl)){
									String customFieldName = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "FSRID_CUSTOM_FLD_NAME","FSRID");
									GenericValue customField = EntityQuery.use(delegator).from("CustomField").where("customFieldName",customFieldName).queryFirst();
									
									if(UtilValidate.isNotEmpty(customField)) {
										String channelId = UtilValidate.isNotEmpty(customField) && UtilValidate.isNotEmpty(customField.getString("groupId")) ? customField.getString("groupId") : "EXTERNAL_INFO";
	    								String customFieldId = UtilValidate.isNotEmpty(customField) && UtilValidate.isNotEmpty(customField.getString("customFieldId")) ? customField.getString("customFieldId") : "";
	    								GenericValue custRequestAttribute = EntityQuery.use(delegator).from("CustRequestAttribute").where("custRequestId", custRequestId, "attrName", customFieldId, "channelId", channelId).queryFirst();
	    								if(UtilValidate.isNotEmpty(custRequestAttribute)) {
	    									String hashValue = custRequestAttribute.getString("attrValue");
	    									if(UtilValidate.isNotEmpty(hashValue)) {
	    										srTrackerUrl = srTrackerUrl+"#"+hashValue;
	    									}
	    								} else {
	    									String encodedCustReqId = Base64.getEncoder().encodeToString(custRequestId.getBytes("utf-8"));
	    									if(UtilValidate.isNotEmpty(encodedCustReqId)) {
	    										srTrackerUrl = srTrackerUrl+"#"+encodedCustReqId;
	    									}
	    								}
									} else {
										String encodedCustReqId = Base64.getEncoder().encodeToString(custRequestId.getBytes("utf-8"));
										if(UtilValidate.isNotEmpty(encodedCustReqId)) {
											srTrackerUrl = srTrackerUrl+"#"+encodedCustReqId;
										}
									}
									
								}
								srTrackerUrl = Objects.toString(srTrackerUrl, "");
								if(UtilValidate.isNotEmpty(srTrackerUrl)) {
									contentInfo.put("SR_SHORTURL", srTrackerUrl);
									contentInfo.put("SR_TRACKER_URL", srTrackerUrl);
								}
								/*
								if(UtilValidate.isNotEmpty(applicationUrl))
									contentInfo.put("SR_SHORTURL", applicationUrl+"/sr-portal/control/viewServiceRequest?srNumber="+custRequestId);
								*/
								contentInfo.put("SR_NAME", custRequestName);
								
								smsBody = org.fio.admin.portal.util.DataUtil.extractContentWithTag(contentInfo);
								
								for(String srPartyId : srPartyPhoneNumMap.keySet()) {
									String phoneNumber = (String) srPartyPhoneNumMap.get(srPartyId);
									if(UtilValidate.isNotEmpty(phoneNumber) && UtilValidate.isNotEmpty(smsBody)) {
										Map<String, Object> msgMap = new HashMap<String, Object>();
										msgMap.put("TTMMVersion", "v1.0.0");
										msgMap.put("DateTime", UtilDateTime.nowTimestamp()+"");
										msgMap.put("MessageID",LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))+":12345:001:001:001:"+ LocalTime.now().format(DateTimeFormatter.ofPattern("HHmmss")));
										
										Map<String, Object> fromMap = new HashMap<String, Object>();
										fromMap.put("MerchantNumber", merchantNumber);
										fromMap.put("StoreNumber", storeNumber);
										fromMap.put("ApplicationID", "v1.0.0");
										fromMap.put("Associate", UtilMisc.toMap("IDType","1","Id","123"));
										msgMap.put("From", fromMap);
										
										Map<String, Object> toMap = new HashMap<String, Object>();
										toMap.put("MessageDeliveryMethod", "sms");
										Map<String, Object> personMap = new HashMap<String, Object>();
										personMap.put("PersonType", "Customer");
										personMap.put("Phone", UtilMisc.toMap("PhoneType","CELL","PhoneNumber", phoneNumber));
										toMap.put("Person", personMap);
										
										msgMap.put("To", toMap);
										msgMap.put("Content", UtilMisc.toMap("Type", "TEXT","Text", smsBody));
										
										String requestData = org.fio.admin.portal.util.DataUtil.convertToJsonStr(UtilMisc.toMap("Message", msgMap));
										
										Map<String, Object> ctx = new HashMap<String, Object>();
										ctx.put("domainEntityType", "SERVICE_REQUEST");
										ctx.put("domainEntityId", custRequestId);
										ctx.put("requestData", requestData);
										ctx.put("toPhoneNumber", phoneNumber);
										ctx.put("smsBody", smsBody);
										
										Map<String, Object> smsResult =  dispatcher.runSync("ap.sendSandboxSms", UtilMisc.toMap("requestContext",ctx));
										Map<String, Object> resultData = (Map<String, Object>) smsResult.get("results");
										Debug.logInfo(" SMS Response : "+ (UtilValidate.isNotEmpty(resultData) ? (String) resultData.get("response") : ""), MODULE);
									}
								}
							}
						}
						
						String isEnabledTelnyxMessage = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "TELNYX_MESSAGE_SETUP", "N");
						if("Y".equals(isEnabledTelnyxMessage)) {
							String srSMSStatusStr = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SR_SMS_NOTIFIY_STATUS");
							List<String> srSMSStatusList = UtilValidate.isNotEmpty(srSMSStatusStr) ? org.fio.admin.portal.util.DataUtil.stringToList(srSMSStatusStr, ",") : new ArrayList<String>();
								
							Map<String, Object> srPartyPhoneNumMap = new HashMap<String, Object>();
							if(UtilValidate.isNotEmpty(srSMSStatusList) && UtilValidate.isNotEmpty(statusId) && !(previousStatusId.equals(statusId)) && srSMSStatusList.contains(statusId)){							
								srPartyPhoneNumMap = SrUtil.getSrAssocPartyPhones(delegator, custRequestId, statusId, primary);
							}
							if(UtilValidate.isNotEmpty(srPartyPhoneNumMap)) {
								String applicationUrl = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "APPLICATION_URL");
								String smsBody = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, statusId+"_SMS_CONTENT");
								
								Map<String, Object> contentInfo = new HashMap<String, Object>();
								contentInfo.put("content", smsBody);
								contentInfo.put("SR_NUMBER", custRequestId);
								
								String srTrackerUrl = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SR_TRACKER_URL");
								if(UtilValidate.isNotEmpty(srTrackerUrl)){
									String customFieldName = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "FSRID_CUSTOM_FLD_NAME","FSRID");
									GenericValue customField = EntityQuery.use(delegator).from("CustomField").where("customFieldName",customFieldName).queryFirst();
									
									if(UtilValidate.isNotEmpty(customField)) {
										String channelId = UtilValidate.isNotEmpty(customField) && UtilValidate.isNotEmpty(customField.getString("groupId")) ? customField.getString("groupId") : "EXTERNAL_INFO";
	    								String customFieldId = UtilValidate.isNotEmpty(customField) && UtilValidate.isNotEmpty(customField.getString("customFieldId")) ? customField.getString("customFieldId") : "";
	    								GenericValue custRequestAttribute = EntityQuery.use(delegator).from("CustRequestAttribute").where("custRequestId", custRequestId, "attrName", customFieldId, "channelId", channelId).queryFirst();
	    								if(UtilValidate.isNotEmpty(custRequestAttribute)) {
	    									String hashValue = custRequestAttribute.getString("attrValue");
	    									if(UtilValidate.isNotEmpty(hashValue)) {
	    										srTrackerUrl = srTrackerUrl+"#"+hashValue;
	    									}
	    								} else {
	    									String encodedCustReqId = Base64.getEncoder().encodeToString(custRequestId.getBytes("utf-8"));
	    									if(UtilValidate.isNotEmpty(encodedCustReqId)) {
	    										srTrackerUrl = srTrackerUrl+"#"+encodedCustReqId;
	    									}
	    								}
									} else {
										String encodedCustReqId = Base64.getEncoder().encodeToString(custRequestId.getBytes("utf-8"));
										if(UtilValidate.isNotEmpty(encodedCustReqId)) {
											srTrackerUrl = srTrackerUrl+"#"+encodedCustReqId;
										}
									}
									
								}
								srTrackerUrl = Objects.toString(srTrackerUrl, "");
								if(UtilValidate.isNotEmpty(srTrackerUrl)) {
									contentInfo.put("SR_SHORTURL", srTrackerUrl);
									contentInfo.put("SR_TRACKER_URL", srTrackerUrl);
								}
								contentInfo.put("SR_NAME", custRequestName);
								
								smsBody = org.fio.admin.portal.util.DataUtil.extractContentWithTag(contentInfo);
								
								List<String> toPhoneNumberList = new LinkedList<String>();
								
								for(String srPartyId : srPartyPhoneNumMap.keySet()) {
									String phoneNumber = (String) srPartyPhoneNumMap.get(srPartyId);
									toPhoneNumberList.add(phoneNumber);
								}
								Map<String, Object> requestContext = new HashMap<String, Object>();
								requestContext.put("domainEntityType", "SERVICE_REQUEST");
								requestContext.put("domainEntityId", custRequestId);
								
								if(UtilValidate.isNotEmpty(toPhoneNumberList)) {
									Map<String, Object> ctx = new HashMap<String, Object>();
									ctx.put("toPhoneNumber", toPhoneNumberList);
									ctx.put("smsBody", smsBody);
									ctx.put("telnyxApiType", "SEND_MESSAGE");
									ctx.put("requestContext", requestContext);
									ctx.put("userLogin", userLogin);
									Map<String, Object> smsResult =  dispatcher.runSync("msg.sendTelnyxSms", ctx);
									Map<String, Object> resultData = (Map<String, Object>) smsResult.get("results");
								}
							}
						}
						// telnyx sms integration end
						
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());
		}
		return result;
	}
	
	public static void createCustRequestParty( Delegator delegator, String custRequestId, String partyId, String roleTypeId) {
    	try {
    		GenericValue custRequestParty = EntityQuery.use(delegator).from("CustRequestParty").where("custRequestId",custRequestId,"roleTypeId", roleTypeId,"partyId",partyId).queryFirst();
    		if(UtilValidate.isEmpty(custRequestParty)) {
    			if(UtilValidate.isNotEmpty(custRequestId) && UtilValidate.isNotEmpty(partyId) && UtilValidate.isNotEmpty(roleTypeId)) {
    				custRequestParty = delegator.makeValue("CustRequestParty");
    				custRequestParty.set("custRequestId", custRequestId);
    				custRequestParty.set("partyId", partyId);
    				custRequestParty.set("roleTypeId", roleTypeId);
    				custRequestParty.set("fromDate", UtilDateTime.nowTimestamp());
    				custRequestParty.create();
    			}
    		} else {
    			custRequestParty.set("lastUpdatedTxStamp", UtilDateTime.nowTimestamp());
    			custRequestParty.set("thruDate", null);
				custRequestParty.store();
    		}
    	} catch (Exception e) {
			
		}
    }
	
	public static GenericValue getSr( Delegator delegator, Map<String, Object> filter) {
    	try {
    		if (UtilValidate.isNotEmpty(filter)) {
    			String partyId = (String) filter.get("partyId");
    			String isProgramTemplate = (String) filter.get("isProgramTemplate");
    			String isActiveRecord = (String) filter.get("isActiveRecord");
    			Timestamp fromDateTime = (Timestamp) filter.get("fromDateTime");
    			Timestamp thruDateTime = (Timestamp) filter.get("thruDateTime");
    			String notInSrIds = (String) filter.get("notInSrIds");
    			
    			Timestamp moment = UtilDateTime.nowTimestamp();
    			
    			List<EntityCondition> conditionList = FastList.newInstance();
				
				conditionList.add(EntityCondition.makeCondition("fromPartyId", EntityOperator.EQUALS, partyId));
				conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN, UtilMisc.toList("SR_CANCELLED")));
				
				if (UtilValidate.isNotEmpty(isActiveRecord) && isActiveRecord.equals("Y")) {
					conditionList.add(EntityCondition.makeCondition(EntityOperator.OR,
							EntityCondition.makeCondition(
						            EntityCondition.makeCondition(
						                EntityCondition.makeCondition("actualEndDate", EntityOperator.EQUALS, null),
						                EntityOperator.OR,
						                EntityCondition.makeCondition("actualEndDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDateTime)
						           ),
						            EntityOperator.AND,
						            EntityCondition.makeCondition(
						                EntityCondition.makeCondition("actualStartDate", EntityOperator.EQUALS, null),
						                EntityOperator.OR,
						                EntityCondition.makeCondition("actualStartDate", EntityOperator.LESS_THAN_EQUAL_TO, fromDateTime)
						           )
						      ),
							EntityCondition.makeCondition(
						            EntityCondition.makeCondition(
						                EntityCondition.makeCondition("actualEndDate", EntityOperator.EQUALS, null),
						                EntityOperator.OR,
						                EntityCondition.makeCondition("actualEndDate", EntityOperator.GREATER_THAN_EQUAL_TO, thruDateTime)
						           ),
						            EntityOperator.AND,
						            EntityCondition.makeCondition(
						                EntityCondition.makeCondition("actualStartDate", EntityOperator.EQUALS, null),
						                EntityOperator.OR,
						                EntityCondition.makeCondition("actualStartDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDateTime)
						           )
						      )
	    					));
					
					/*conditionList.add(EntityCondition.makeCondition(EntityOperator.OR,
	    					EntityUtil.getFilterByDateExpr(fromDateTime, "actualStartDate", "actualEndDate"),
	    					EntityUtil.getFilterByDateExpr(thruDateTime, "actualStartDate", "actualEndDate")
	    					));*/
				}
				
				if (UtilValidate.isNotEmpty(notInSrIds)) {
					conditionList.add(EntityCondition.makeCondition("custRequestId", EntityOperator.NOT_IN, UtilMisc.toList(notInSrIds.split(","))));
				}
				
				DynamicViewEntity dynamicView = new DynamicViewEntity();
				
				dynamicView.addMemberEntity("CR", "CustRequest");
				dynamicView.addAlias("CR", "custRequestId","custRequestId", null, Boolean.FALSE, Boolean.TRUE, null);
				dynamicView.addAlias("CR", "custRequestName");
				dynamicView.addAlias("CR", "fromPartyId");
				dynamicView.addAlias("CR", "statusId");
				dynamicView.addAlias("CR", "actualStartDate");
				dynamicView.addAlias("CR", "actualEndDate");
				
				if(UtilValidate.isNotEmpty(isProgramTemplate)) {
					dynamicView.addMemberEntity("CRA1", "CustRequestAttribute");
					dynamicView.addAlias("CRA1", "attrName2","attrName",null, false,false,null);
					dynamicView.addAlias("CRA1", "attrValue2","attrValue",null, false,false,null);
					dynamicView.addViewLink("CR", "CRA1", Boolean.TRUE, ModelKeyMap.makeKeyMapList("custRequestId"));
					
					if (isProgramTemplate.equals("Y")) {
						conditionList.add(EntityCondition.makeCondition(EntityOperator.AND,
								EntityCondition.makeCondition("attrName2", EntityOperator.EQUALS, "IS_PROG_TPL"),
								EntityCondition.makeCondition("attrValue2", EntityOperator.EQUALS, isProgramTemplate)));
					} else {
						conditionList.add(EntityCondition.makeCondition(EntityOperator.AND,
								EntityCondition.makeCondition("attrName2", EntityOperator.EQUALS, "IS_PROG_TPL"),
								EntityCondition.makeCondition(EntityOperator.OR,
										EntityCondition.makeCondition("attrValue2", EntityOperator.EQUALS, isProgramTemplate),
										EntityCondition.makeCondition("attrValue2", EntityOperator.EQUALS, null),
										EntityCondition.makeCondition("attrValue2", EntityOperator.EQUALS, "")
										)
								));
					}
				}
				
				EntityCondition mainConditon = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				GenericValue sr = EntityQuery.use(delegator).from(dynamicView).where(mainConditon).queryFirst();
				return sr;
    		}
    	} catch (Exception e) {
    		e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
    	return null;
    }
	
	public static List<GenericValue> getSrActivities( Delegator delegator, Map<String, Object> filter) {
    	try {
    		if (UtilValidate.isNotEmpty(filter)) {
    			String domainEntityId = (String) filter.get("domainEntityId");
    			String domainEntityType = (String) filter.get("domainEntityType");
    			String isChecklistActivity = (String) filter.get("isChecklistActivity");
    			
    			List<EntityCondition> conditionList = FastList.newInstance();
				
    			conditionList.add(EntityCondition.makeCondition("domainEntityType", EntityOperator.EQUALS, domainEntityType));
				conditionList.add(EntityCondition.makeCondition("domainEntityId", EntityOperator.EQUALS, domainEntityId));
				
				DynamicViewEntity dynamicView = new DynamicViewEntity();
				
				dynamicView.addMemberEntity("WE", "WorkEffort");
				dynamicView.addAlias("WE", "workEffortId","workEffortId", null, Boolean.FALSE, Boolean.TRUE, null);
				dynamicView.addAlias("WE", "workEffortName");
				dynamicView.addAlias("WE", "workEffortTypeId");
				dynamicView.addAlias("WE", "createdByUserLogin");
				dynamicView.addAlias("WE", "workEffortName");
				dynamicView.addAlias("WE", "description");
				dynamicView.addAlias("WE", "currentStatusId");
				dynamicView.addAlias("WE", "externalId");
				dynamicView.addAlias("WE", "estimatedStartDate");
				dynamicView.addAlias("WE", "estimatedCompletionDate");
				dynamicView.addAlias("WE", "actualStartDate");
				dynamicView.addAlias("WE", "actualCompletionDate");
				dynamicView.addAlias("WE", "duration");
				dynamicView.addAlias("WE", "channelId");
				dynamicView.addAlias("WE", "primOwnerId");
				dynamicView.addAlias("WE", "createdDate");
				dynamicView.addAlias("WE", "lastModifiedDate");
				dynamicView.addAlias("WE", "sourceReferenceId");
				dynamicView.addAlias("WE", "cif");
				dynamicView.addAlias("WE", "businessUnitName");
				dynamicView.addAlias("WE", "priority");
				dynamicView.addAlias("WE", "direction");
				dynamicView.addAlias("WE", "ownerPartyId");
				dynamicView.addAlias("WE", "locationDesc");
				dynamicView.addAlias("WE", "completedBy");
				dynamicView.addAlias("WE", "closedDateTime");
				dynamicView.addAlias("WE", "closedByUserLogin");
				dynamicView.addAlias("WE", "domainEntityType");
				dynamicView.addAlias("WE", "domainEntityId");
				dynamicView.addAlias("WE", "createdStamp");
				dynamicView.addAlias("WE", "lastUpdatedStamp");
				
				if(UtilValidate.isNotEmpty(isChecklistActivity)) {
					dynamicView.addMemberEntity("WEA1", "WorkEffortAttribute");
					dynamicView.addAlias("WEA1", "attrName2","attrName",null, false,false,null);
					dynamicView.addAlias("WEA1", "attrValue2","attrValue",null, false,false,null);
					dynamicView.addViewLink("WE", "WEA1", Boolean.TRUE, ModelKeyMap.makeKeyMapList("workEffortId"));
					
					conditionList.add(EntityCondition.makeCondition(EntityOperator.AND,
							EntityCondition.makeCondition("attrName2", EntityOperator.EQUALS, "IS_PROG_ACT"),
							EntityCondition.makeCondition("attrValue2", EntityOperator.EQUALS, "Y")));
				}
				
				EntityCondition mainConditon = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				List<GenericValue> activityList = EntityQuery.use(delegator).from(dynamicView).where(mainConditon).queryList();
				return activityList;
    		}
    	} catch (Exception e) {
    		e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
    	return null;
    }
	
	/**
	 * @author Mahendran T
	 * @param delegator
	 * @param custRequestId
	 * @param statusId
	 * @param primary
	 * @return map value
	 */
	public static Map<String, Object> getSrAssocPartyPhones(Delegator delegator, String custRequestId, String statusId, String primary){
		Map<String, Object> srPartyPhoneNums = new HashMap<String, Object>();
		try{
			if(UtilValidate.isNotEmpty(custRequestId)) {
				List<String> srAssocPartiesRolesList = new ArrayList<>();
				String srAssocPartiesRoles = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, statusId+"_SMS_ROLES");
				if(UtilValidate.isNotEmpty(srAssocPartiesRoles)) {
					if(UtilValidate.isNotEmpty(srAssocPartiesRoles) && srAssocPartiesRoles.contains(",")) {
						srAssocPartiesRolesList = org.fio.admin.portal.util.DataUtil.stringToList(srAssocPartiesRoles, ",");
					} else
						srAssocPartiesRolesList.add(srAssocPartiesRoles);
				}
				
				if(UtilValidate.isEmpty(srAssocPartiesRolesList)) srAssocPartiesRolesList.add("SALES_REP");
				
				List<EntityCondition> conditionList = FastList.newInstance();
				conditionList.add(EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId),
						EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null),
						EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, srAssocPartiesRolesList),
						EntityUtil.getFilterByDateExpr()
						));
				EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				List<GenericValue> custRequestPartyList = EntityQuery.use(delegator).from("CustRequestParty").where(mainConditons).queryList();
				
				List<String> srAssocPartyIds = UtilValidate.isNotEmpty(custRequestPartyList) ? EntityUtil.getFieldListFromEntityList(custRequestPartyList, "partyId", true) : new ArrayList<>();
				
				if(UtilValidate.isNotEmpty(srAssocPartiesRolesList) && srAssocPartiesRolesList.contains("CONTACT")) {
					String srPrimaryContactId = org.fio.homeapps.util.DataUtil.getSrPrimaryContact(delegator, custRequestId);
					if(UtilValidate.isNotEmpty(srPrimaryContactId)){
						srAssocPartyIds.add(srPrimaryContactId);
					}
				}
				
				if(UtilValidate.isNotEmpty(srAssocPartyIds)) {
					
					if("SR_SNT_TO_SRTECH_FOR_SCHLD".equals(statusId)){
						
						List<EntityCondition> partyRelationshipConditionList = FastList.newInstance();
						
						partyRelationshipConditionList.add(EntityCondition.makeCondition(EntityOperator.AND,
								EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "TECHNICIAN"),
								EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "CONTRACT_TYPE"),
								EntityCondition.makeCondition("partyIdTo", EntityOperator.IN, srAssocPartyIds),
								EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null)
								));
						
						EntityCondition partyRelationshipCondition = EntityCondition.makeCondition(partyRelationshipConditionList, EntityOperator.AND);
						
						Set<String> fieldToSelect = new LinkedHashSet<>();
						fieldToSelect.add("partyIdTo");
						
						List<GenericValue> partyRelationshipList = delegator.findList("PartyRelationship", partyRelationshipCondition, fieldToSelect, null, null, false);
						
						List<String> thirdPartyTechnicianPartyIds = UtilValidate.isNotEmpty(partyRelationshipList) ? EntityUtil.getFieldListFromEntityList(partyRelationshipList, "partyIdTo", true) : new ArrayList<>();
						
						if(UtilValidate.isNotEmpty(thirdPartyTechnicianPartyIds)) {
							srAssocPartyIds.clear();
							srAssocPartyIds.addAll(thirdPartyTechnicianPartyIds);
						}
					}
					String includeCountryCode = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "INCLUDE_COUNTRY_CODE", "Y"); 
					
					for(String partyId : srAssocPartyIds) {
						String primaryPhone = PartyHelper.getContactNumber(delegator, partyId, "PRIMARY_PHONE", includeCountryCode);
						if(UtilValidate.isEmpty(primaryPhone))
							primaryPhone = PartyHelper.getContactNumber(delegator, partyId, "PRIMARY_MOBILE", includeCountryCode);
						
						srPartyPhoneNums.put(partyId, primaryPhone);
					}
					
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return srPartyPhoneNums;
	}
	
	public static String getServicePortalName(Delegator delegator) {
		String portalName = "sr-portal";
		try {
			String isFsrEnabled = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "FSR_ENABLED");
			if (UtilValidate.isNotEmpty(isFsrEnabled) && isFsrEnabled.equals("N")) {
				portalName = "ticket-portal";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		Debug.logInfo("getServicePortalName > "+portalName, MODULE);
		return portalName;
	}
	public static Map<String, String> getBackupCoordinatorInfo(Delegator delegator, String ownerPartyId){
		Map<String, String> backupCoordInformation = new HashMap<String, String>();
		try {
			GenericValue partyAttribute = EntityQuery.use(delegator).from("PartyAttribute").where("partyId", ownerPartyId, "attrName","BACKUP_COORDINATOR").queryFirst();
			if(UtilValidate.isNotEmpty(partyAttribute) && UtilValidate.isNotEmpty(partyAttribute.getString("attrValue"))) {
				String backupCoordinatorPartyId = partyAttribute.getString("attrValue");
				backupCoordInformation = PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator,backupCoordinatorPartyId);
				backupCoordInformation.put("partyId", backupCoordinatorPartyId);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return backupCoordInformation;
	}
	
	public static List<String> getReferenceSrIds(Delegator delegator, String srId, List<String> srIds){
		//List<String> srIds = new ArrayList<>();
		try {
			if (UtilValidate.isNotEmpty(srId)) {
				if (UtilValidate.isEmpty(srIds)) {
					srIds = new ArrayList<>();
				}
				
				GenericValue crs = EntityQuery.use(delegator).from("CustRequestSupplementory").where("custRequestId", srId).queryFirst();
				if (UtilValidate.isNotEmpty(crs)) {
					if (UtilValidate.isEmpty(crs.getString("isCopySr"))) {
						List<GenericValue> crsList = EntityQuery.use(delegator).from("CustRequestSupplementory").where("domainEntityId", srId, "domainEntityType", "SERVICE_REQUEST").queryList();
						if (UtilValidate.isNotEmpty(crsList)) {
							for (GenericValue crrs : crsList) {
								if (UtilValidate.isNotEmpty(crrs.getString("isCopySr")) && crrs.getString("isCopySr").equals("Y")) {
									srIds.add(crrs.getString("custRequestId"));
								} else {
									srIds.add(crrs.getString("domainEntityId"));
									srIds = getReferenceSrIds(delegator, crrs.getString("domainEntityId"), srIds);
								}
							}
						}
					} else if (crs.getString("isCopySr").equals("Y")) {
						srIds.add(crs.getString("domainEntityId"));
						srIds = getReferenceSrIds(delegator, crs.getString("domainEntityId"), srIds);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return srIds;
	}
	
	public static List<String> getStoreLocationIds(Delegator delegator, String storeGroupId){
		List<String> locationIdList = new ArrayList<>();
		try {
			if (UtilValidate.isNotEmpty(storeGroupId)) {
				List<EntityCondition> condList = new ArrayList<EntityCondition>();
				condList.add(EntityCondition.makeCondition(EntityOperator.OR,
						EntityCondition.makeCondition("isDemoStore",EntityOperator.EQUALS,"N"),
						EntityCondition.makeCondition("isDemoStore",EntityOperator.EQUALS,null))
						);
				condList.add(EntityCondition.makeCondition("primaryStoreGroupId", EntityOperator.EQUALS, storeGroupId));
				EntityCondition condition = EntityCondition.makeCondition(condList, EntityOperator.AND);
				List<GenericValue> locationList = EntityQuery.use(delegator).select("productStoreId","storeName").from("ProductStore").where(condition).queryList();
				locationIdList = locationList.stream().distinct().map(e-> e.getString("productStoreId")).collect(Collectors.toList());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return locationIdList;
	}
	
}
