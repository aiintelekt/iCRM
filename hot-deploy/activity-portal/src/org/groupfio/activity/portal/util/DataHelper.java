/**
 * 
 */
package org.groupfio.activity.portal.util;

import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.groupfio.common.portal.CommonPortalConstants;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityUtilProperties;

import javolution.util.FastList;
import javolution.util.FastMap;

/**
 * @author Sharif
 */
public class DataHelper {
	
	private static String MODULE = DataHelper.class.getName();
	
	public static List prepareSrCondition(Delegator delegator, Map<String, Object> requestContext) {
		List<EntityCondition> conditionList = FastList.newInstance();
		
		try {
			if (UtilValidate.isNotEmpty(requestContext)) {

		    	SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy");
				
				String srNo = (String) requestContext.get("srNo");
				String srName = (String) requestContext.get("srName");
				String customerId = (String) requestContext.get("customerId");
				String partyId = (String) requestContext.get("partyId");
				String srPrimaryContactId = (String) requestContext.get("srPrimaryContactId");
				String srArea = (String) requestContext.get("srArea");
				String srSubArea = (String) requestContext.get("srSubArea");
				Object priority = requestContext.get("priority");
				Object srType = requestContext.get("srType");
				Object srStatus = requestContext.get("srStatus");
				String startDueDate = (String) requestContext.get("srDueDate_from");
				String endDueDate = (String) requestContext.get("srDueDate_to");
				
				String primaryEmail = (String) requestContext.get("primaryEmail");
				String primaryPhoneNumber = (String) requestContext.get("primaryPhoneNumber");
				String homePhoneNumber = (String) requestContext.get("homePhoneNumber");
				String offPhoneNumber = (String) requestContext.get("offPhoneNumber");
				String mobileNumber = (String) requestContext.get("mobileNumber");
				
				String open = (String) requestContext.get("open");
				String slaAtRisk = (String) requestContext.get("slaAtRisk");
				String slaExpired = (String) requestContext.get("slaExpired");
				String unAssigned = (String) requestContext.get("unAssigned");
				String closed = (String) requestContext.get("closed");
				String salesPerson = (String) requestContext.get("salesPerson");
				
				String contractorId = (String) requestContext.get("contractorId");
				String contractorEmail = (String) requestContext.get("contractorEmail");
				String contractorOffPhone = (String) requestContext.get("contractorOffPhone");
				String contractorMobilePhone = (String) requestContext.get("contractorMobilePhone");
				
				Object owner = requestContext.get("owner");
				String countryGeoId = (String) requestContext.get("countryGeoId");
				Object stateProvinceGeoId = requestContext.get("stateProvinceGeoId");
				Object city = requestContext.get("city");
				Object county = requestContext.get("countyGeoId");
				Object zipCode = requestContext.get("zipCode");
				String zipCodeExt = (String) requestContext.get("zipCodeExt");
				
				String searchType = (String) requestContext.get("searchType");
				String isPostalCodeRequired = (String) requestContext.get("isPostalCodeRequired");
				
				String customerPo = (String) requestContext.get("customerPo");
				String location = (String) requestContext.get("location");
				String ticketNumber = (String) requestContext.get("ticketNumber");
				
				ArrayList<String> statuses = new ArrayList<String>();
				
				if (UtilValidate.isNotEmpty(partyId)) {
					conditionList.add(EntityCondition.makeCondition("fromPartyId", EntityOperator.EQUALS, partyId));
				}
				
				// date range filter [start]
				
				/*String dateRangeFieldId = "createdDate";
				if (UtilValidate.isNotEmpty(dateRangeType)) {
					if (dateRangeType.equals("DUE")) {
						dateRangeFieldId = "commitDate";
					} else if (dateRangeType.equals("CLOSE")) {
						dateRangeFieldId = "closedByDate";
					}
				}
				
				if(UtilValidate.isNotEmpty(dateRangeFrom)) {
					dateRangeFrom = df1.format(df2.parse(dateRangeFrom));
					conditionlist.add(EntityCondition.makeCondition(dateRangeFieldId,EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(Timestamp.valueOf(dateRangeFrom))));
				}
				if (UtilValidate.isNotEmpty(dateRangeTo)) {
					dateRangeTo = df1.format(df2.parse(dateRangeTo));
					conditionlist.add(EntityCondition.makeCondition(dateRangeFieldId,EntityOperator.LESS_THAN_EQUAL_TO,UtilDateTime.getDayEnd(Timestamp.valueOf(dateRangeTo))));
				}*/
				
				// date range filter [end]
				
				if(UtilValidate.isNotEmpty(startDueDate)) {
					startDueDate = df1.format(df2.parse(startDueDate));
					conditionList.add(EntityCondition.makeCondition("commitDate",EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(Timestamp.valueOf(startDueDate))));
				}
				if (UtilValidate.isNotEmpty(endDueDate)) {
					endDueDate = df1.format(df2.parse(endDueDate));
					conditionList.add(EntityCondition.makeCondition("commitDate",EntityOperator.LESS_THAN_EQUAL_TO,UtilDateTime.getDayEnd(Timestamp.valueOf(endDueDate))));
				}

				if (UtilValidate.isNotEmpty(srNo)) {
					conditionList.add(EntityCondition.makeCondition("custRequestId", EntityOperator.LIKE,"%"+srNo + "%"));
				}
				/*if (UtilValidate.isNotEmpty(externalId)) {
					conditionlist.add(EntityCondition.makeCondition("externalId", EntityOperator.LIKE,"%"+externalId + "%"));
				}*/
				
				if (UtilValidate.isNotEmpty(srType)) {
					if (!(srType instanceof List)) srType = UtilMisc.toList(""+srType);
					conditionList.add(EntityCondition.makeCondition("custRequestTypeId", EntityOperator.IN, srType));
				}
				if (UtilValidate.isNotEmpty(srArea)) {
					conditionList.add(EntityCondition.makeCondition("custRequestCategoryId", EntityOperator.EQUALS, srArea));
				}
				if (UtilValidate.isNotEmpty(srSubArea)) {
					conditionList.add(EntityCondition.makeCondition("custRequestSubCategoryId", EntityOperator.EQUALS, srSubArea));
				}
				
				if (UtilValidate.isNotEmpty(owner)) {
					if (!(owner instanceof List)) owner = UtilMisc.toList(""+owner);
					conditionList.add(EntityCondition.makeCondition("responsiblePerson", EntityOperator.IN, owner));
				}
				
				if (UtilValidate.isNotEmpty(priority)) {
					List priorityList = new ArrayList<>();
					if (!(priority instanceof List)) {
						priorityList = UtilMisc.toList(Long.parseLong(""+priority));
					} else {
						for (Object p : ((List) priority)) {
							priorityList.add(Long.parseLong(""+p));
						}
					}
					conditionList.add(EntityCondition.makeCondition("priority", EntityOperator.IN, priorityList));
				}
				
				if (UtilValidate.isNotEmpty(srName)) {
					conditionList.add(EntityCondition.makeCondition("custRequestName", EntityOperator.LIKE,"%"+srName + "%"));
				}
				/*if (UtilValidate.isNotEmpty(description)) {
					conditionlist.add(EntityCondition.makeCondition("descriptionRawTxt", EntityOperator.LIKE,"%"+description + "%"));
				}
				if (UtilValidate.isNotEmpty(resolution)) {
					conditionlist.add(EntityCondition.makeCondition("resolutionRawTxt", EntityOperator.LIKE,"%"+resolution + "%"));
				}*/
				
				if (UtilValidate.isNotEmpty(ticketNumber)) {
					conditionList.add(EntityCondition.makeCondition("purchaseOrder", EntityOperator.LIKE,"%"+ticketNumber + "%"));
				}
				/*
				if (UtilValidate.isNotEmpty(srPrimaryContactId)) {
					conditionlist.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, srPrimaryContactId));
				}
				*/
				if (UtilValidate.isNotEmpty(countryGeoId)) {
					conditionList.add(EntityCondition.makeCondition("pstlCountryGeoId", EntityOperator.EQUALS, countryGeoId));
				}
				if (UtilValidate.isNotEmpty(stateProvinceGeoId)) {
					if (!(stateProvinceGeoId instanceof List)) stateProvinceGeoId = UtilMisc.toList(""+stateProvinceGeoId);
					conditionList.add(EntityCondition.makeCondition("pstlStateProvinceGeoId", EntityOperator.IN, stateProvinceGeoId));
				}
				if (UtilValidate.isNotEmpty(zipCode)) {
					if (!(zipCode instanceof List)) zipCode = UtilMisc.toList(""+zipCode);
					conditionList.add(EntityCondition.makeCondition("pstlPostalCode", EntityOperator.IN, zipCode));
				}
				if (UtilValidate.isNotEmpty(zipCodeExt)) {
					conditionList.add(EntityCondition.makeCondition("pstlPostalCodeExt", EntityOperator.EQUALS, zipCodeExt));
				}
				if (UtilValidate.isNotEmpty(city)) {
					if (!(city instanceof List)) city = UtilMisc.toList(""+city);
					conditionList.add(EntityCondition.makeCondition("pstlPostalCity", EntityOperator.IN, city));
				}
				if (UtilValidate.isNotEmpty(county)) {
					if (!(county instanceof List)) county = UtilMisc.toList(""+county);
					conditionList.add(EntityCondition.makeCondition("pstlCountyGeoId", EntityOperator.IN, county));
				}
				
				if (UtilValidate.isNotEmpty(open)) {
					statuses.add(CommonPortalConstants.srOpenStatuses.SR_ASSIGNED);
					statuses.add(CommonPortalConstants.srOpenStatuses.SR_OPEN);
					statuses.add(CommonPortalConstants.srOpenStatuses.SR_IN_PROGRESS);
					statuses.add(CommonPortalConstants.srOpenStatuses.SR_PENDING);
				}
				if (UtilValidate.isNotEmpty(closed)) {
					statuses.add(CommonPortalConstants.srClosedStatuses.SR_CLOSED);
					statuses.add(CommonPortalConstants.srClosedStatuses.SR_CANCELLED);
				}
				if (UtilValidate.isNotEmpty(srStatus)) {
					if (srStatus instanceof List) statuses.addAll((List)srStatus);
					else statuses.add(""+srStatus);
				}
				
				if(UtilValidate.isNotEmpty(unAssigned) || UtilValidate.isNotEmpty(searchType) && searchType.equals(CommonPortalConstants.SrSearchType.UN_ASSIGNED_SRS)){
					
					conditionList.add(EntityCondition.makeCondition(UtilMisc.toList(                       
	                        EntityCondition.makeCondition("responsiblePerson", EntityOperator.EQUALS, null),
	                        EntityCondition.makeCondition("responsiblePerson", EntityOperator.EQUALS, "")), EntityOperator.OR));
					
				}
				
				List<EntityCondition> flagConditions = new ArrayList<EntityCondition>();
				if(UtilValidate.isNotEmpty(slaAtRisk) && "Y".equalsIgnoreCase(slaAtRisk)) {
					Timestamp now = UtilDateTime.nowTimestamp();
					flagConditions.add(EntityCondition.makeCondition(EntityOperator.AND,
							EntityCondition.makeCondition("preEscalationDate", EntityOperator.LESS_THAN, now),
							EntityCondition.makeCondition("commitDate", EntityOperator.GREATER_THAN, now),
							EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN, UtilMisc.toList(CommonPortalConstants.srClosedStatuses.SR_CLOSED,CommonPortalConstants.srClosedStatuses.SR_CANCELLED))
							));
				}
				if(UtilValidate.isNotEmpty(slaExpired) && "Y".equalsIgnoreCase(slaExpired)) {
					Timestamp now = UtilDateTime.nowTimestamp();
					flagConditions.add(EntityCondition.makeCondition(EntityOperator.AND,
							EntityCondition.makeCondition("commitDate", EntityOperator.LESS_THAN, now),
							EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN, UtilMisc.toList(CommonPortalConstants.srClosedStatuses.SR_CLOSED,CommonPortalConstants.srClosedStatuses.SR_CANCELLED))
							));
				}

				if (UtilValidate.isNotEmpty(statuses)) {
					flagConditions.add(EntityCondition.makeCondition("statusId", EntityOperator.IN, statuses));
				}
				if(UtilValidate.isNotEmpty(flagConditions)) {
					EntityCondition conditions = EntityCondition.makeCondition(flagConditions,EntityOperator.OR);
					conditionList.add(conditions);
				}
				
				if (UtilValidate.isNotEmpty(isPostalCodeRequired) && isPostalCodeRequired.equals("Y")) {
					conditionList.add(EntityCondition.makeCondition("pstlPostalCode", EntityOperator.NOT_EQUAL, null));
				}
				
				if (UtilValidate.isNotEmpty(homePhoneNumber)) {
					conditionList.add(EntityCondition.makeCondition("homePhoneNumber", EntityOperator.LIKE,"%"+homePhoneNumber + "%"));
				}
				if (UtilValidate.isNotEmpty(offPhoneNumber)) {
					conditionList.add(EntityCondition.makeCondition("offPhoneNumber", EntityOperator.LIKE,"%"+offPhoneNumber + "%"));
				}
				if (UtilValidate.isNotEmpty(mobileNumber)) {
					conditionList.add(EntityCondition.makeCondition("mobileNumber", EntityOperator.LIKE,"%"+mobileNumber + "%"));
				}
				
				if (UtilValidate.isNotEmpty(contractorEmail)) {
					conditionList.add(EntityCondition.makeCondition("contractorEmail", EntityOperator.LIKE,"%"+contractorEmail + "%"));
				}
				if (UtilValidate.isNotEmpty(contractorOffPhone)) {
					conditionList.add(EntityCondition.makeCondition("contractorOffPhone", EntityOperator.LIKE,"%"+contractorOffPhone + "%"));
				}
				if (UtilValidate.isNotEmpty(contractorMobilePhone)) {
					conditionList.add(EntityCondition.makeCondition("contractorMobilePhone", EntityOperator.LIKE,"%"+contractorMobilePhone + "%"));
				}
				
				if (UtilValidate.isNotEmpty(searchType) && searchType.equals("ATTRIBUTE")) {
					if (UtilValidate.isNotEmpty(customerPo)) {
						String customFieldId = EntityUtilProperties.getPropertyValue("sr-portal.properties", "customerPo.customFieldId", delegator);
						conditionList.add(EntityCondition.makeCondition(EntityOperator.AND,
								EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, customFieldId),
								EntityCondition.makeCondition("attrValue", EntityOperator.LIKE,"%"+customerPo + "%")
								));
					}
					if (UtilValidate.isNotEmpty(location)) {
						String customFieldId = EntityUtilProperties.getPropertyValue("sr-portal.properties", "location.customFieldId", delegator);
						conditionList.add(EntityCondition.makeCondition(EntityOperator.AND,
								EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, customFieldId),
								EntityCondition.makeCondition("attrValue", EntityOperator.EQUALS, location)
								));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return conditionList;
	}
	public static void reAssignOwnerCustRequestParty (Delegator delegator, GenericValue userLogin, String custRequestId, List<String> ownerList) {
		reAssignOwnerCustRequestParty(delegator, userLogin, custRequestId, ownerList, "");
	}
	public static void reAssignOwnerCustRequestParty (Delegator delegator, GenericValue userLogin, String custRequestId, List<String> ownerList, String workEffortId) {
		try {
			Map<String, Object> callCtxt = FastMap.newInstance();
			Map<String, Object> callResult = FastMap.newInstance();
			
			List<String> ownerRoles = new ArrayList<>();
			String activityOwnerRole = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "ACT_OWNER", "TECHNICIAN");
			if(UtilValidate.isNotEmpty(activityOwnerRole)) {
				if(UtilValidate.isNotEmpty(activityOwnerRole) && activityOwnerRole.contains(",")) {
					ownerRoles = org.fio.admin.portal.util.DataUtil.stringToList(activityOwnerRole, ",");
				} else
					ownerRoles.add(activityOwnerRole);
			}
			if(UtilValidate.isEmpty(ownerRoles)) ownerRoles.add("CAL_OWNER");
			List<String> ownerPartyIds = new ArrayList<String>();
			for(String owner : ownerList) {
				ownerPartyIds.add(org.fio.homeapps.util.DataUtil.getUserLoginPartyId(delegator, owner));
			}
			List<String> actParties = getCustRequestActivityParties(delegator, custRequestId, workEffortId);
			if(UtilValidate.isNotEmpty(actParties)) {
				ownerPartyIds.addAll(actParties);
			}
			
			Map<String, Object> anchorPartyMap = getCustRequestAnchorParties(delegator, custRequestId);
			if(UtilValidate.isNotEmpty(anchorPartyMap)) {
				List<String> anchorPartyIds = anchorPartyMap
												.values()
												.stream()
												.map(String::valueOf)
												.collect(Collectors.toList());
				
				if(UtilValidate.isNotEmpty(anchorPartyIds)) {
					ownerPartyIds.addAll(anchorPartyIds);
				}
			}
			List<String> distinctPartyIds = ownerPartyIds.stream()
                    .distinct()
                    .collect(Collectors.toList());
			
			List<EntityCondition> conditionList = FastList.newInstance();
			conditionList.add(EntityCondition.makeCondition(EntityOperator.AND,
					EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId),
					EntityCondition.makeCondition("partyId", EntityOperator.NOT_IN, distinctPartyIds),
					EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, ownerRoles),
					EntityUtil.getFilterByDateExpr()
	                ));
			
			EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			List<GenericValue> partyAssignmentList = EntityQuery.use(delegator).from("CustRequestParty").where(mainConditons).queryList();
			if (UtilValidate.isNotEmpty(partyAssignmentList)) {
				for (GenericValue partyAssignment : partyAssignmentList) {
					partyAssignment.put("thruDate", UtilDateTime.nowTimestamp());
					partyAssignment.store();
				}
			}
			
			for(String owner : ownerList) {
        		String ownerPartyId1 = org.fio.homeapps.util.DataUtil.getUserLoginPartyId(delegator, owner);
        		String ownerRoleTypeId = org.fio.admin.portal.util.DataUtil.getPartySecurityRole(delegator, ownerPartyId1);
        		
        		conditionList = FastList.newInstance();
    			conditionList.add(EntityCondition.makeCondition(EntityOperator.AND,
    					EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId),
    					EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, ownerPartyId1),
    					EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, ownerRoleTypeId)
    	                ));
    			
    			mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
    			GenericValue partyAssignment = EntityQuery.use(delegator).from("CustRequestParty").where(mainConditons).filterByDate().queryFirst();
    					//EntityUtil.getFirst( delegator.findList("CustRequestParty", mainConditons, UtilMisc.toSet("partyId"), null, null, false) );
    			if (UtilValidate.isEmpty(partyAssignment)) {
    				partyAssignment = delegator.makeValue("CustRequestParty");
    				partyAssignment.set("custRequestId", custRequestId);
    				partyAssignment.set("partyId", ownerPartyId1);
    				partyAssignment.set("roleTypeId", ownerRoleTypeId);
    				partyAssignment.set("fromDate", UtilDateTime.nowTimestamp());
    				partyAssignment.create();
    			}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static List<String> getCustRequestActivityParties(Delegator delegator, String custRequestId, String workEffortId){
		List<String> partyIds = new ArrayList<String>();
		try {
			List<GenericValue> custRequestWorkEffortList = EntityQuery.use(delegator).from("CustRequestWorkEffort").where("custRequestId", custRequestId).queryList();
			List<String> workEffortIds = UtilValidate.isNotEmpty(custRequestWorkEffortList) ? EntityUtil.getFieldListFromEntityList(custRequestWorkEffortList, "workEffortId", true) : new ArrayList<String>();
			if(UtilValidate.isNotEmpty(workEffortIds)) {
				workEffortIds.remove(workEffortId);
				EntityCondition workEffCondition = EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("workEffortId", EntityOperator.IN, workEffortIds),
						EntityCondition.makeCondition("currentStatusId", EntityOperator.NOT_IN, UtilMisc.toList("IA_COMPLETED", "IA_MCOMPLETED"))
						);
				List<GenericValue> workEffortList = EntityQuery.use(delegator).from("WorkEffort").where(workEffCondition).queryList();
				List<String> workEffIds = UtilValidate.isNotEmpty(workEffortList) ? EntityUtil.getFieldListFromEntityList(workEffortList, "workEffortId", true) : new ArrayList<String>();
				if(UtilValidate.isNotEmpty(workEffIds)) {
					
					List<EntityCondition> conditionList = FastList.newInstance();
					conditionList.add(EntityCondition.makeCondition(EntityOperator.AND,
							EntityCondition.makeCondition("workEffortId", EntityOperator.IN, workEffIds),
							EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PRTYASGN_ASSIGNED"),
							EntityUtil.getFilterByDateExpr()
			                ));
					
					EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
					List<GenericValue> workEffortPartyList = EntityQuery.use(delegator).from("WorkEffortPartyAssignment").where(mainConditons).queryList();
					partyIds = UtilValidate.isNotEmpty(workEffortPartyList) ? EntityUtil.getFieldListFromEntityList(workEffortPartyList, "partyId", true) : new ArrayList<String>();
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return partyIds;
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
}
