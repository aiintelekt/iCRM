/**
 * 
 */
package org.groupfio.common.portal.service;

import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.fio.homeapps.constants.GlobalConstants.AccessLevel;
import org.fio.homeapps.util.UtilDateTime;
import org.groupfio.common.portal.CommonPortalConstants;
import org.groupfio.common.portal.CommonPortalConstants.DomainEntityType;
import org.groupfio.common.portal.util.UtilCampaign;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

/**
 * @author Sharif
 *
 */
public class CampaignServices {

	private static final String MODULE = CampaignServices.class.getName();
	
	public static Map findCampaign(DispatchContext dctx, Map context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = (Delegator) dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        
        Map requestContext = (Map) context.get("requestContext");
        
        String partyId = (String) requestContext.get("partyId");
        
        String campaignId = (String) requestContext.get("campaignId");
        String campaignIds = (String) requestContext.get("campaignIds");
	    String campaignCode = (String) requestContext.get("campaignCode");
	    String campaignName = (String) requestContext.get("campaignName");
	    String statusId = (String) requestContext.get("statusId");
	    String campaignType = (String) requestContext.get("campaignType");
	    String channel = (String) requestContext.get("channel");
	    String fromDate = (String) requestContext.get("fromDate");
	    String thruDate = (String) requestContext.get("thruDate");
	    String parentCampaignId = (String) requestContext.get("parentCampaignId");
	    String masterCampaignId = (String) requestContext.get("masterCampaignId");
	    String createdBy = (String) requestContext.get("createdBy");
	    String ownerUserLoginId = (String) requestContext.get("ownerId");
	    String profilePartyId = (String) requestContext.get("profilePartyId");
	    String createdStartDate = (String) requestContext.get("createdStartDate");
	    String createdEndDate = (String) requestContext.get("createdEndDate");
	    String campaignHierarchyType = (String) requestContext.get("campaignHierarchyType");
	    String masterParentCampaignId = (String) requestContext.get("masterParentCampaignId");
	    String refreshType = (String) requestContext.get("refreshType");
	    
	    String orderByColumn = (String) requestContext.get("orderByColumn");
	    String isActivieRecord = (String) requestContext.get("isActivieRecord");
        
        String searchText = (String) requestContext.get("searchText");
        
        List<GenericValue> resultList = new ArrayList<>();
        Map<String, Object> result = new HashMap<String, Object>();
        
        try {
        	String userLoginId = userLogin.getString("userLoginId");
        	String userLoginPartyId = org.fio.homeapps.util.DataUtil.getUserLoginPartyId(delegator, userLoginId);
        	
        	String globalDateFormat = org.fio.homeapps.util.DataHelper.getGlobalDateFormat(delegator);
            
            Map<String, Object> callCtxt = FastMap.newInstance();
            Map<String, Object> callResult = FastMap.newInstance();
            
			String accessLevel = "Y";
			Map<String, Object> accessMatrixRes = new HashMap<String, Object>();
			
			/*String businessUnit = null;
			if (UtilValidate.isNotEmpty(userLoginId)) {
				String userLoginPartyId = org.fio.homeapps.util.DataUtil.getUserLoginPartyId(delegator, userLoginId);
				businessUnit = org.fio.homeapps.util.DataUtil.getBusinessUnitId(delegator, userLoginPartyId);
				Map<String, Object> accessMatrixMap = new LinkedHashMap<String, Object>();
				accessMatrixMap.put("delegator", delegator);
				accessMatrixMap.put("dispatcher", dispatcher);
				accessMatrixMap.put("businessUnit", businessUnit);
				accessMatrixMap.put("modeOfOp", "Read");
				accessMatrixMap.put("entityName", "WorkEffort");
				accessMatrixMap.put("userLoginId", userLoginId);
				accessMatrixRes = org.fio.homeapps.util.DataUtil.getAccessList(accessMatrixMap);
				if (UtilValidate.isNotEmpty(accessMatrixRes)) {
					accessLevel = (String) accessMatrixRes.get("accessLevel");
				} else {
					accessLevel = null;
				}
			}*/
            
            if (UtilValidate.isNotEmpty(accessLevel) && AccessLevel.YES.equals(accessLevel)) {
            	List<EntityCondition> conditionList = FastList.newInstance();
            	
            	if (UtilValidate.isNotEmpty(searchText)) {
            		EntityCondition searchCondition = EntityCondition.makeCondition(EntityOperator.OR,
            				EntityCondition.makeCondition("masterCampaignId", EntityOperator.LIKE,"" + searchText + "%"),
            				EntityCondition.makeCondition("campaignName", EntityOperator.LIKE,"" + searchText + "%"));
            		conditionList.add(searchCondition);
            	}
            	
            	if (UtilValidate.isNotEmpty(campaignIds)) {
	            	conditionList.add(EntityCondition.makeCondition("marketingCampaignId", EntityOperator.IN, Arrays.asList(campaignIds.split(","))));
	            }
            	if (UtilValidate.isNotEmpty(campaignId)) {
            		conditionList.add(EntityCondition.makeCondition("marketingCampaignId", EntityOperator.LIKE, "" + campaignId + "%"));
            	}
            	if (UtilValidate.isNotEmpty(campaignCode)) {
	                EntityCondition campaignHomeCondition = EntityCondition.makeCondition("campaignCode", EntityOperator.LIKE, "" + campaignCode + "%");
	                conditionList.add(campaignHomeCondition);
	            }
	            if (UtilValidate.isNotEmpty(campaignName)) {
	                EntityCondition campaignNameCondition = EntityCondition.makeCondition("campaignName", EntityOperator.LIKE, "" + campaignName + "%");
	                conditionList.add(campaignNameCondition);
	            }
	            if (UtilValidate.isNotEmpty(campaignType)) {
	                EntityCondition campaignTypeCondition = EntityCondition.makeCondition("campaignTypeId", EntityOperator.EQUALS, campaignType);
	                conditionList.add(campaignTypeCondition);
	            }
	            if (UtilValidate.isNotEmpty(channel)) {
	                EntityCondition channelTypeCondition = EntityCondition.makeCondition("channelTypeId", EntityOperator.EQUALS, channel);
	                conditionList.add(channelTypeCondition);
	            }
	            if (UtilValidate.isNotEmpty(statusId)) {
	                EntityCondition statusIdCondition = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, statusId);
	                conditionList.add(statusIdCondition);
	            }
	            if(UtilValidate.isNotEmpty(fromDate)) {
					Timestamp sd = UtilDateTime.stringToTimeStamp(fromDate, globalDateFormat, TimeZone.getDefault(), Locale.getDefault());
					conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(sd)));
				}
				if (UtilValidate.isNotEmpty(thruDate)) {
					Timestamp ed = UtilDateTime.stringToTimeStamp(thruDate, globalDateFormat, TimeZone.getDefault(), Locale.getDefault());
					conditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(ed)));
				}
				if (UtilValidate.isNotEmpty(parentCampaignId)) {
	                EntityCondition parentCampaignIdCondition = EntityCondition.makeCondition("parentCampaignId", EntityOperator.EQUALS, parentCampaignId);
	                conditionList.add(parentCampaignIdCondition);
	            }
	            if (UtilValidate.isNotEmpty(masterCampaignId)) {
	                EntityCondition masterCampaignIdCondition = EntityCondition.makeCondition("masterCampaignId", EntityOperator.EQUALS, masterCampaignId);
	                conditionList.add(masterCampaignIdCondition);
	            }
	            if (UtilValidate.isNotEmpty(createdBy)) {
	                EntityCondition createdByUserLoginCondition = EntityCondition.makeCondition("createdByUserLogin", EntityOperator.EQUALS, createdBy);
	                conditionList.add(createdByUserLoginCondition);
	            }
	            if (UtilValidate.isNotEmpty(ownerUserLoginId)) {
	                EntityCondition createdByUserLoginCondition = EntityCondition.makeCondition("ownerId", EntityOperator.EQUALS, ownerUserLoginId);
	                conditionList.add(createdByUserLoginCondition);
	            }
	            if (UtilValidate.isNotEmpty(refreshType)) {
	            	conditionList.add(EntityCondition.makeCondition("refreshType", EntityOperator.EQUALS, refreshType));
	            }
            	
	            if(UtilValidate.isNotEmpty(createdStartDate)) {
					Timestamp sd = UtilDateTime.stringToTimeStamp(createdStartDate, globalDateFormat, TimeZone.getDefault(), Locale.getDefault());
					conditionList.add(EntityCondition.makeCondition("createdDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(sd)));
				}
				if (UtilValidate.isNotEmpty(createdEndDate)) {
					Timestamp ed = UtilDateTime.stringToTimeStamp(createdEndDate, globalDateFormat, TimeZone.getDefault(), Locale.getDefault());
					conditionList.add(EntityCondition.makeCondition("createdDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(ed)));
				}
				
				if (UtilValidate.isNotEmpty(campaignHierarchyType)) {
	                if ("MASTER_CAMPAIGN".equals(campaignHierarchyType)) {
	                    List < String > lists = new LinkedList < String > ();
	                    if (UtilValidate.isNotEmpty(masterParentCampaignId)) {
	                        List < String > childCampList = UtilCampaign.getAllChildCampaignIds(delegator, masterParentCampaignId, UtilMisc.toList(masterParentCampaignId), lists);
	                        if (UtilValidate.isNotEmpty(campaignId)) {
	                        	conditionList.add(EntityCondition.makeCondition("parentCampaignId", EntityOperator.EQUALS, masterParentCampaignId));
	                        } else {
	                        	conditionList.add(EntityCondition.makeCondition("marketingCampaignId", EntityOperator.IN, childCampList));
	                        }
	                    } else {
	                        EntityCondition masterConditions = EntityCondition.makeCondition(EntityOperator.OR,
	                            EntityCondition.makeCondition("masterCampaignId", EntityOperator.NOT_EQUAL, null),
	                            EntityCondition.makeCondition("masterCampaignId", EntityOperator.NOT_EQUAL, ""));
	                        conditionList.add(masterConditions);
	                        EntityCondition parentConditions = EntityCondition.makeCondition(EntityOperator.OR,
	                            EntityCondition.makeCondition("parentCampaignId", EntityOperator.NOT_EQUAL, null),
	                            EntityCondition.makeCondition("parentCampaignId", EntityOperator.NOT_EQUAL, ""));
	                        conditionList.add(parentConditions);
	                    }
	                }
	                if ("PARENT_CAMPAIGN".equals(campaignHierarchyType)) {
	                    List < String > lists = new LinkedList < String > ();
	                    if (UtilValidate.isNotEmpty(masterParentCampaignId)) {
	                        List < String > childCampList = UtilCampaign.getAllChildCampaignIds(delegator, masterParentCampaignId, UtilMisc.toList(masterParentCampaignId), lists);
	                        if (UtilValidate.isNotEmpty(campaignId)) {
	                        	conditionList.add(EntityCondition.makeCondition("parentCampaignId", EntityOperator.EQUALS, masterParentCampaignId));
	                        } else {
	                        	conditionList.add(EntityCondition.makeCondition("marketingCampaignId", EntityOperator.IN, childCampList));
	                        }
	                    } else {
	                        EntityCondition parentConditions = EntityCondition.makeCondition(EntityOperator.OR,
	                            EntityCondition.makeCondition("parentCampaignId", EntityOperator.NOT_EQUAL, null),
	                            EntityCondition.makeCondition("parentCampaignId", EntityOperator.NOT_EQUAL, ""));
	                        conditionList.add(parentConditions);
	                    }
	                }
	            }
				
				if (UtilValidate.isNotEmpty(isActivieRecord) && isActivieRecord.equals("Y")) {
					conditionList.add(EntityUtil.getFilterByDateExpr("fromDate", "thruDate"));
				}
				
	            EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	            Debug.logInfo("findCampaign condition: "+mainConditons, MODULE);
	            
	            String orderBy = "lastUpdatedTxStamp DESC";
	            if (UtilValidate.isNotEmpty(orderByColumn)) {
	            	orderBy = orderByColumn;
	            }
	            
	            DynamicViewEntity dynamicView = new DynamicViewEntity();
	            
	            dynamicView.addMemberEntity("MC", "MarketingCampaign");
				dynamicView.addAlias("MC", "marketingCampaignId", null, null, null, true, null);
				
				dynamicView.addAlias("MC", "parentCampaignId");
				dynamicView.addAlias("MC", "statusId");
				dynamicView.addAlias("MC", "campaignName");
				dynamicView.addAlias("MC", "campaignSummary");
				dynamicView.addAlias("MC", "fromDate");
				dynamicView.addAlias("MC", "thruDate");
				dynamicView.addAlias("MC", "isActive");
				dynamicView.addAlias("MC", "numSent");
				dynamicView.addAlias("MC", "startDate");
				dynamicView.addAlias("MC", "createdByUserLogin");
				dynamicView.addAlias("MC", "lastModifiedByUserLogin");
				dynamicView.addAlias("MC", "ownerId");
				dynamicView.addAlias("MC", "geoId");
				dynamicView.addAlias("MC", "statusCodeDesc");
				dynamicView.addAlias("MC", "channelTypeId");
				dynamicView.addAlias("MC", "campaignPurposeId");
				dynamicView.addAlias("MC", "campaignPurposeDesc");
				dynamicView.addAlias("MC", "parentAltCampaignId");
				dynamicView.addAlias("MC", "parentCampaignCode");
				dynamicView.addAlias("MC", "parentCampaignName");
				dynamicView.addAlias("MC", "parentDurationDays");
				dynamicView.addAlias("MC", "parentObjectives");
				dynamicView.addAlias("MC", "externalTemplateId");
				dynamicView.addAlias("MC", "openEmmCampaignId");
				dynamicView.addAlias("MC", "campaignTheme");
				dynamicView.addAlias("MC", "campProduct");
				dynamicView.addAlias("MC", "campKeyword");
				dynamicView.addAlias("MC", "campSector");
				dynamicView.addAlias("MC", "consolidateTemp");
				dynamicView.addAlias("MC", "emplTeamId");
				dynamicView.addAlias("MC", "refreshType");
				dynamicView.addAlias("MC", "nextRefreshDate");
				dynamicView.addAlias("MC", "lastRefreshDate");
				dynamicView.addAlias("MC", "masterCampaignId");
				dynamicView.addAlias("MC", "classificationType");
				dynamicView.addAlias("MC", "campaignTypeId");
				dynamicView.addAlias("MC", "approvalDate");
				dynamicView.addAlias("MC", "executiveApprovalDate");
				dynamicView.addAlias("MC", "endDate");
				dynamicView.addAlias("MC", "isApproved");
				dynamicView.addAlias("MC", "campaignTemplateId");
				dynamicView.addAlias("MC", "campaignTemplateName");
				dynamicView.addAlias("MC", "templateImageUrl");
				dynamicView.addAlias("MC", "approvedByUserLogin");
				dynamicView.addAlias("MC", "alternateCampaignId");
				dynamicView.addAlias("MC", "campaignCode");
				dynamicView.addAlias("MC", "isRemainderCampaign");
				dynamicView.addAlias("MC", "subject");
				dynamicView.addAlias("MC", "campaignListCount");
				dynamicView.addAlias("MC", "dedupedListExclCount");
				dynamicView.addAlias("MC", "corpGovExclusionCount");
				dynamicView.addAlias("MC", "campaignTemplate");
				dynamicView.addAlias("MC", "remainder");
				dynamicView.addAlias("MC", "solicitation");
				dynamicView.addAlias("MC", "campaignList");
				dynamicView.addAlias("MC", "dedupedCount");
				dynamicView.addAlias("MC", "timeZoneId");
				dynamicView.addAlias("MC", "objectives");
				dynamicView.addAlias("MC", "product");
				dynamicView.addAlias("MC", "measurementOfSuccess");
				dynamicView.addAlias("MC", "offerCodeCount");
				dynamicView.addAlias("MC", "roleTypeId");
				dynamicView.addAlias("MC", "campaignEmailType");
				dynamicView.addAlias("MC", "salesTriggerType");
				dynamicView.addAlias("MC", "subProduct");
				dynamicView.addAlias("MC", "multiTemplate");
				dynamicView.addAlias("MC", "campTestStatus");
				dynamicView.addAlias("MC", "rmBccStatus");
				dynamicView.addAlias("MC", "ownerBusinessUnit");
				dynamicView.addAlias("MC", "createdDate");
				dynamicView.addAlias("MC", "lastModifiedDate");
				dynamicView.addAlias("MC", "dndRequired");
				dynamicView.addAlias("MC", "productCategoryId");
				dynamicView.addAlias("MC", "productSubCategoryId");
				dynamicView.addAlias("MC", "salesLiteratureName");
				dynamicView.addAlias("MC", "salesLiteratureUrl");
				dynamicView.addAlias("MC", "gracePeriod");
				dynamicView.addAlias("MC", "remainder");
				dynamicView.addAlias("MC", "lastUpdatedTxStamp");

	            int viewIndex = 0;
		        try {
		            viewIndex = Integer.parseInt((String) requestContext.get("VIEW_INDEX"));
		        } catch (Exception e) {
		            viewIndex = 0;
		        }
		        
		        int fioGridFetch = UtilValidate.isNotEmpty(requestContext.get("totalGridFetch")) ? Integer.parseInt((String) requestContext.get("totalGridFetch")) : 1000;
				int autoCompleteLimit=org.groupfio.common.portal.util.DataUtil.getDefaultAutoCompleteMaxRows(delegator);

		        int viewSize = fioGridFetch;
		        try {
		            viewSize = Integer.parseInt((String) requestContext.get("VIEW_SIZE"));
		        } catch (Exception e) {
		            viewSize = fioGridFetch;
		        }
		        
		        Debug.logInfo("findCampaign start: "+UtilDateTime.nowTimestamp(), MODULE);
				int highIndex = 0;
	            int lowIndex = 0;
	            int resultListSize = 0;
	            if (UtilValidate.isNotEmpty(searchText)) {
					highIndex = autoCompleteLimit;
				}
	            try {
	                // get the indexes for the partial list
	            	lowIndex = viewIndex * viewSize + 1;
	                highIndex = (viewIndex + 1) * viewSize;
	                
	                // set distinct on so we only get one row per order
	                // using list iterator
	                EntityListIterator pli=null;
	                
	                pli = EntityQuery.use(delegator)
	                		//.select(fieldsToSelect)
	                        .from(dynamicView)
	                        .where(mainConditons)
	                        .orderBy(UtilMisc.toList(orderBy))
	                        .cursorScrollInsensitive()
	                        .fetchSize(highIndex)
	                        //.distinct()
	                        .cache(false)
	                        .queryIterator();
	                
	                // get the partial list for this page
	                resultList = pli.getPartialList(lowIndex, viewSize);

	                // attempt to get the full size
	                resultListSize = pli.getResultsSizeAfterPartialList();
	                // close the list iterator
	                pli.close();
	                
	                result.put("viewIndex", Integer.valueOf(viewIndex));
	                result.put("highIndex", Integer.valueOf(highIndex));
			        result.put("lowIndex", Integer.valueOf(lowIndex));
			        result.put("viewSize", viewSize);
			        result.put("resultListSize", resultListSize);
	            } catch (GenericEntityException e) {
	                String errMsg = "Error: " + e.toString();
	                Debug.logError(e, errMsg, MODULE);
	            }
	            
	            Debug.logInfo("findCampaign end: "+UtilDateTime.nowTimestamp(), MODULE);
	            Debug.logInfo("findCampaign count: "+resultList.size(), MODULE);
	            
	            result.put("dataList", resultList);
            }
        } catch (Exception e) {
           // e.printStackTrace();
            Debug.logError(e.getMessage(), MODULE);
            result.putAll(ServiceUtil.returnError(e.getMessage()));
            return result;
        }
        result.putAll(ServiceUtil.returnSuccess("Successfully find campaign.."));
        return result;
    }
	
	public static Map findOutBoundCall(DispatchContext dctx, Map context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = (Delegator) dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        
        Map requestContext = (Map) context.get("requestContext");
        
        String partyId = (String) requestContext.get("partyId");
		String statusId = (String) requestContext.get("statusId");
		String callBackDate = (String) requestContext.get("callBackDate");
		String campaign = (String) requestContext.get("campaignId");
		String timeZoneId = (String) requestContext.get("timeZoneId");
		String noOfDaysSinceLastCall = (String) requestContext.get("noOfDaysSinceLastCall");
		
		String domainEntityType = (String) requestContext.get("domainEntityType");
		String domainEntityId = (String) requestContext.get("domainEntityId");
	    
	    String orderByColumn = (String) requestContext.get("orderByColumn");
	    String isActivieRecord = (String) requestContext.get("isActivieRecord");
        
        String searchText = (String) requestContext.get("searchText");
        
        List<GenericValue> resultList = new ArrayList<>();
        Map<String, Object> result = new HashMap<String, Object>();
        
        NumberFormat nf = NumberFormat.getInstance(Locale.getDefault());
		Timestamp callBackDateTs=null;
		SimpleDateFormat sqlformatter = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy");
        
        try {
        	String userLoginId = userLogin.getString("userLoginId");
        	String userLoginPartyId = org.fio.homeapps.util.DataUtil.getUserLoginPartyId(delegator, userLoginId);
        	
        	String globalDateFormat = org.fio.homeapps.util.DataHelper.getGlobalDateFormat(delegator);
            
            Map<String, Object> callCtxt = FastMap.newInstance();
            Map<String, Object> callResult = FastMap.newInstance();
            
			String accessLevel = "Y";
			Map<String, Object> accessMatrixRes = new HashMap<String, Object>();
			
			/*String businessUnit = null;
			if (UtilValidate.isNotEmpty(userLoginId)) {
				String userLoginPartyId = org.fio.homeapps.util.DataUtil.getUserLoginPartyId(delegator, userLoginId);
				businessUnit = org.fio.homeapps.util.DataUtil.getBusinessUnitId(delegator, userLoginPartyId);
				Map<String, Object> accessMatrixMap = new LinkedHashMap<String, Object>();
				accessMatrixMap.put("delegator", delegator);
				accessMatrixMap.put("dispatcher", dispatcher);
				accessMatrixMap.put("businessUnit", businessUnit);
				accessMatrixMap.put("modeOfOp", "Read");
				accessMatrixMap.put("entityName", "WorkEffort");
				accessMatrixMap.put("userLoginId", userLoginId);
				accessMatrixRes = org.fio.homeapps.util.DataUtil.getAccessList(accessMatrixMap);
				if (UtilValidate.isNotEmpty(accessMatrixRes)) {
					accessLevel = (String) accessMatrixRes.get("accessLevel");
				} else {
					accessLevel = null;
				}
			}*/
            
            if (UtilValidate.isNotEmpty(accessLevel) && AccessLevel.YES.equals(accessLevel)) {
            	List<EntityCondition> conditionList = FastList.newInstance();
    			conditionList.add(EntityCondition.makeCondition("campaignName",EntityOperator.NOT_LIKE,"Direct Call Campaign"));
    			conditionList.add(EntityCondition.makeCondition("campaignTypeId",EntityOperator.EQUALS,"PHONE_CALL"));
    			conditionList.add(EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"MKTG_CAMP_PUBLISHED"));

    			EntityCondition condition = EntityCondition.makeCondition(EntityOperator.OR,
    					EntityCondition.makeCondition("callFinished", EntityOperator.EQUALS,"N"),
    					EntityCondition.makeCondition("callFinished", EntityOperator.EQUALS,null),
    					EntityCondition.makeCondition("callFinished", EntityOperator.EQUALS,""));

    			conditionList.add(EntityCondition.makeCondition(EntityOperator.AND,condition));

    			EntityCondition searchCondition = EntityCondition.makeCondition(EntityOperator.OR,
    					EntityCondition.makeCondition("endDate", EntityOperator.EQUALS,null),
    					EntityCondition.makeCondition("endDate", EntityOperator.GREATER_THAN,UtilDateTime.nowTimestamp()));

    			conditionList.add(EntityCondition.makeCondition(EntityOperator.AND,searchCondition));
    			
    			if (UtilValidate.isNotEmpty(domainEntityType) && CommonPortalConstants.PARTY_DOMAIN_ENTITY_TYPE.containsKey(domainEntityType)) {
    				//conditionList.add(EntityCondition.makeCondition("csr1PartyId", EntityOperator.EQUALS, partyId));
    				conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, domainEntityId));
    			}
    			if (UtilValidate.isNotEmpty(domainEntityType) && domainEntityType.equals(DomainEntityType.CAMPAIGN)) {
    				//conditionList.add(EntityCondition.makeCondition("csr1PartyId", EntityOperator.EQUALS, partyId));
    				conditionList.add(EntityCondition.makeCondition("marketingCampaignId", EntityOperator.EQUALS, domainEntityId));
    			}
    			
    			if (UtilValidate.isNotEmpty(statusId)) {//enumtypeid - CALL_STATUS
    				conditionList.add(EntityCondition.makeCondition("lastCallStatusId",EntityOperator.EQUALS,statusId));
    			}
    			if (UtilValidate.isNotEmpty(callBackDate)) {
    				Date callBackDate1 = formatter.parse(callBackDate);
    				java.sql.Date callBackDateSql = java.sql.Date.valueOf(sqlformatter.format(callBackDate1));
    				conditionList.add(EntityCondition.makeCondition("callBackDate",EntityOperator.GREATER_THAN_EQUAL_TO,callBackDateSql));
    			}
    			if (UtilValidate.isNotEmpty(campaign)) {
    				conditionList.add(EntityCondition.makeCondition("crmMarketingCampaignId",EntityOperator.EQUALS,campaign));
    			}
    			if (UtilValidate.isNotEmpty(timeZoneId)) {
    				conditionList.add(EntityCondition.makeCondition("localTimeZone",EntityOperator.EQUALS,timeZoneId));
    			}
    			if (UtilValidate.isNotEmpty(noOfDaysSinceLastCall)) {
    				Integer i = Integer.valueOf(noOfDaysSinceLastCall);
    				Calendar cal = Calendar.getInstance();
    				cal.setTime(new Date());
    				cal.add(Calendar.DATE, -i);
    				String sinceCallDate = sqlformatter.format(cal.getTime());//date - 2023-08-19
    				Date sinceCallDate1 = formatter.parse(callBackDate);
    				java.sql.Date noOfDaysSinceLastCallSql = java.sql.Date.valueOf(sqlformatter.format(sinceCallDate1));
    				conditionList.add(EntityCondition.makeCondition("lastContactDate",EntityOperator.GREATER_THAN_EQUAL_TO,noOfDaysSinceLastCallSql));
    			}
				
				if (UtilValidate.isNotEmpty(isActivieRecord) && isActivieRecord.equals("Y")) {
					conditionList.add(EntityUtil.getFilterByDateExpr("fromDate", "thruDate"));
				}
				
	            EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	            Debug.logInfo("findCampaign condition: "+mainConditons, MODULE);
	            
	            String orderBy = "createdDate DESC";
	            if (UtilValidate.isNotEmpty(orderByColumn)) {
	            	orderBy = orderByColumn;
	            }
	            
	            DynamicViewEntity dynamicEntity = new DynamicViewEntity();
	            
				dynamicEntity.addMemberEntity("CRM", "CallRecordMaster");
				dynamicEntity.addAlias("CRM", "crmMarketingCampaignId", "marketingCampaignId", null, null, null, null);
				dynamicEntity.addAlias("CRM", "crmEndDate", "endDate", null, null, null, null);
				dynamicEntity.addAlias("CRM", "crmStartDate", "startDate", null, null, null, null);
				dynamicEntity.addAlias("CRM", "crmPartyId", "partyId", null, null, null, null);
				dynamicEntity.addAlias("CRM", "contactListId");
				dynamicEntity.addAlias("CRM", "callFinished");
				dynamicEntity.addAlias("CRM", "externalReferenceId");
				dynamicEntity.addAlias("CRM", "entityReferenceId");
				dynamicEntity.addAlias("CRM", "entityReferenceTypeId");
				dynamicEntity.addAlias("CRM", "firstName");
				dynamicEntity.addAlias("CRM", "lastName");
				dynamicEntity.addAlias("CRM", "createdDate");
				dynamicEntity.addAlias("CRM", "countryCode");
				dynamicEntity.addAlias("CRM", "phoneNumber");
				dynamicEntity.addAlias("CRM", "solicitationStatus");
				dynamicEntity.addAlias("CRM", "csr1PartyId");
				dynamicEntity.addAlias("CRM", "lastCallStatusId");
				dynamicEntity.addAlias("CRM", "lastCallDuration");
				dynamicEntity.addAlias("CRM", "totalCallsMakeContact");
				dynamicEntity.addAlias("CRM", "totalCallsByCamp");
				dynamicEntity.addAlias("CRM", "totalCallDuration");
				dynamicEntity.addAlias("CRM", "firstContactDate");
				dynamicEntity.addAlias("CRM", "lastContactDate");
				dynamicEntity.addAlias("CRM", "nextActionStatusId");
				dynamicEntity.addAlias("CRM", "callBackDate");
				dynamicEntity.addAlias("CRM", "localTimeZone");
				dynamicEntity.addAlias("CRM", "accountManager");
				dynamicEntity.addAlias("CRM", "callRecordId");

				dynamicEntity.addMemberEntity("MKTC", "MarketingCampaign");
				dynamicEntity.addAlias("MKTC", "marketingCampaignId");
				dynamicEntity.addAlias("MKTC", "campaignTypeId");
				dynamicEntity.addAlias("MKTC", "statusId");
				dynamicEntity.addAlias("MKTC", "campaignName");
				dynamicEntity.addAlias("MKTC", "startDate");
				dynamicEntity.addAlias("MKTC", "endDate");
				dynamicEntity.addViewLink("CRM", "MKTC", Boolean.TRUE, ModelKeyMap.makeKeyMapList("marketingCampaignId", "marketingCampaignId"));
				
	            int viewIndex = 0;
		        try {
		            viewIndex = Integer.parseInt((String) requestContext.get("VIEW_INDEX"));
		        } catch (Exception e) {
		            viewIndex = 0;
		        }
		        
		        int fioGridFetch = UtilValidate.isNotEmpty(requestContext.get("totalGridFetch")) ? Integer.parseInt((String) requestContext.get("totalGridFetch")) : 1000;
		        
		        int viewSize = fioGridFetch;
		        try {
		            viewSize = Integer.parseInt((String) requestContext.get("VIEW_SIZE"));
		        } catch (Exception e) {
		            viewSize = fioGridFetch;
		        }
		        
		        Debug.logInfo("findCampaign start: "+UtilDateTime.nowTimestamp(), MODULE);
				int highIndex = 0;
	            int lowIndex = 0;
	            int resultListSize = 0;
	            try {
	                // get the indexes for the partial list
	            	lowIndex = viewIndex * viewSize + 1;
	                highIndex = (viewIndex + 1) * viewSize;
	                
	                // set distinct on so we only get one row per order
	                // using list iterator
	                EntityListIterator pli=null;
	                
	                pli = EntityQuery.use(delegator)
	                		//.select(fieldsToSelect)
	                        .from(dynamicEntity)
	                        .where(mainConditons)
	                        .orderBy(UtilMisc.toList(orderBy))
	                        .cursorScrollInsensitive()
	                        .fetchSize(highIndex)
	                        //.distinct()
	                        .cache(false)
	                        .queryIterator();
	                
	                // get the partial list for this page
	                resultList = pli.getPartialList(lowIndex, viewSize);

	                // attempt to get the full size
	                resultListSize = pli.getResultsSizeAfterPartialList();
	                // close the list iterator
	                pli.close();
	                
	                result.put("viewIndex", Integer.valueOf(viewIndex));
	                result.put("highIndex", Integer.valueOf(highIndex));
			        result.put("lowIndex", Integer.valueOf(lowIndex));
			        result.put("viewSize", viewSize);
			        result.put("resultListSize", resultListSize);
	            } catch (GenericEntityException e) {
	                String errMsg = "Error: " + e.toString();
	                Debug.logError(e, errMsg, MODULE);
	            }
	            
	            Debug.logInfo("findCampaign end: "+UtilDateTime.nowTimestamp(), MODULE);
	            Debug.logInfo("findCampaign count: "+resultList.size(), MODULE);
	            
	            result.put("dataList", resultList);
            }
        } catch (Exception e) {
           // e.printStackTrace();
            Debug.logError(e.getMessage(), MODULE);
            result.putAll(ServiceUtil.returnError(e.getMessage()));
            return result;
        }
        result.putAll(ServiceUtil.returnSuccess("Successfully find OutBoundCall.."));
        return result;
    }
	
}
