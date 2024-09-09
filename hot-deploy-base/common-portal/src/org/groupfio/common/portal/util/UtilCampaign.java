/**
 * 
 */
package org.groupfio.common.portal.util;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.fio.homeapps.util.PartyHelper;
import org.fio.homeapps.util.QueryUtil;
import org.fio.homeapps.util.UtilActivity;
import org.fio.homeapps.util.UtilDateTime;
import org.fio.homeapps.util.UtilMessage;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.jdbc.SQLProcessor;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastMap;

/**
 * @author Sharif
 *
 */
public class UtilCampaign {

	private static String MODULE = UtilCampaign.class.getName();
	
	public static String getCampaignName(Delegator delegator, String marketingCampaignId) {
		try {
			if (UtilValidate.isNotEmpty(marketingCampaignId)) {
				GenericValue camapign = EntityQuery.use(delegator).from("MarketingCampaign").select("campaignName").where("marketingCampaignId", marketingCampaignId).cache(false).queryOne();
				if (UtilValidate.isNotEmpty(camapign)) {
					return camapign.getString("campaignName");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return null;
	}
	
	public static String getTemplateName(Delegator delegator, String templateId) {
		try {
			if (UtilValidate.isNotEmpty(templateId)) {
				GenericValue template = EntityQuery.use(delegator).from("TemplateMaster").select("templateName").where("templateId", templateId).cache(false).queryOne();
				if (UtilValidate.isNotEmpty(template)) {
					return template.getString("templateName");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return null;
	}
	
	public static String getTemplateTagName(Delegator delegator, String tagId) {
		try {
			if (UtilValidate.isNotEmpty(tagId)) {
				GenericValue templateTag = EntityQuery.use(delegator).from("DataTagConfiguration").select("tagName").where("tagId", tagId).cache(false).queryOne();
				if (UtilValidate.isNotEmpty(templateTag)) {
					return templateTag.getString("tagName");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return null;
	}
	
	public static boolean isTemplateExists(Delegator delegator, String templateId) {
		try {
			if (UtilValidate.isNotEmpty(templateId)) {
				GenericValue template = EntityQuery.use(delegator).from("TemplateMaster").select("templateName").where("templateId", templateId).cache(false).queryOne();
				if (UtilValidate.isNotEmpty(template)) {
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return false;
	}
	
	public static long getDripCampaignCount(Delegator delegator, Map<String, Object> filter) {
		long totalCount = 0;
		try {
			if (UtilValidate.isNotEmpty(filter)) {
				String marketingCampaignId = (String) filter.get("marketingCampaignId");
				String customFieldName = (String) filter.get("customFieldName");
				String partyId = (String) filter.get("partyId");
				
				if (UtilValidate.isNotEmpty(marketingCampaignId) && UtilValidate.isNotEmpty(customFieldName)) {
					customFieldName = customFieldName.toLowerCase();
					String segmentedColName = "";
					if (customFieldName.equals("bounced")) {
						segmentedColName = "BOUNCED";
					} else if (customFieldName.equals("clicked")) {
						segmentedColName = "CLICKED";
					} else if (customFieldName.equals("converted")) {
						segmentedColName = "CONVERTED";
					} else if (customFieldName.equals("opened")) {
						segmentedColName = "OPENED";
					} else if (customFieldName.equals("sent")) {
						segmentedColName = "SENT";
					} else if (customFieldName.equals("unsubscribe")) {
						segmentedColName = "unsubscribed";
					} else if (customFieldName.equals("not_open")) {
						segmentedColName = "NOT_OPEN";
					}
					
					ResultSet rs = null;
					SQLProcessor sqlProcessor = new SQLProcessor(delegator, delegator.getGroupHelperInfo("org.ofbiz"));
					
					String totalCallSql = "SELECT COUNT(1) as 'total_count' FROM campaign_contact_list_party cclp INNER JOIN marketing_campaign_contact_list mccl ON mccl.CONTACT_LIST_ID=cclp.CONTACT_LIST_ID"; 
			        totalCallSql += " WHERE mccl.MARKETING_CAMPAIGN_ID='" + marketingCampaignId + "' AND mccl.CONTACT_PURPOSE_TYPE='LIVE' "
			        		+ " AND cclp."+segmentedColName+"='Y'";
			        
			        if (UtilValidate.isNotEmpty(partyId)) {
			        	totalCallSql += " AND cclp.party_id='"+partyId+"'";
			        }
			        //totalCallSql += " GROUP BY mccl.CONTACT_LIST_ID, party_id";
			        rs = sqlProcessor.executeQuery(totalCallSql);
			        if (rs != null) {
			            while (rs.next()) {
			            	totalCount = rs.getLong("total_count");
			            }
			        }
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return totalCount;
	}
	
	public static List<String> getAllChildCampaignIds(Delegator delegator, String masterParentCampaignId, List<String> campaignList, List<String> campaignIds) {
		try {
			if (UtilValidate.isNotEmpty(masterParentCampaignId)) {
				campaignIds.add(masterParentCampaignId);
			}
			if (UtilValidate.isNotEmpty(campaignList)) {
				EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("parentCampaignId", EntityOperator.IN, campaignList));
				List<GenericValue> campList = delegator.findList("MarketingCampaign", condition,
						UtilMisc.toSet("marketingCampaignId"), null, null, false);
				if (campList != null && campList.size() > 0) {
					campaignList = EntityUtil.getFieldListFromEntityList(campList, "marketingCampaignId", true);
					campaignIds.addAll(campaignList);
					getAllChildCampaignIds(delegator, "", campaignList, campaignIds);
				}
			}

		} catch (Exception e) {
			Debug.logError("Error : " + e.getMessage(), MODULE);
		}
		return campaignIds;
	}
	
	public static List<String> getCampaignIds(Delegator delegator, Map<String, Object> filter) {
		List<String> campaignIds = new ArrayList<>();
		try {
			if (UtilValidate.isNotEmpty(filter)) {
				String partyId = (String) filter.get("partyId");
				String contactPurposeType = (String) filter.get("contactPurposeType");
				
				if (UtilValidate.isNotEmpty(partyId)) {
					List<String> partyIds = new ArrayList<>();
					partyIds.add(partyId);
					
					String roleTypeId = null;
					GenericValue party = EntityQuery.use(delegator).select("roleTypeId").from("Party").where("partyId", partyId).queryFirst();
					if (UtilValidate.isNotEmpty(party)) {
						roleTypeId = party.getString("roleTypeId");
					}
					if (UtilValidate.isNotEmpty(roleTypeId) && (roleTypeId.equals("ACCOUNT") || roleTypeId.equals("LEAD"))) {
						Map<String, Object> primaryContact = org.groupfio.common.portal.util.DataUtil.getPrimaryContact(delegator, partyId, roleTypeId);
						if (UtilValidate.isNotEmpty(primaryContact)) {
							String primaryContactId = (String) primaryContact.get("contactId");
							if (UtilValidate.isNotEmpty(primaryContactId)) {
								partyIds.add(primaryContactId);
							}
						}
					}
					
					if (UtilValidate.isEmpty(contactPurposeType)) {
						contactPurposeType = "LIVE";
					}
					
					ResultSet rs = null;
					SQLProcessor sqlProcessor = new SQLProcessor(delegator, delegator.getGroupHelperInfo("org.ofbiz"));
					
					String sql = "SELECT distinct mccl.MARKETING_CAMPAIGN_ID FROM marketing_campaign_contact_list mccl INNER JOIN campaign_contact_list_party cclp ON mccl.CONTACT_LIST_ID=cclp.CONTACT_LIST_ID"
						+ " WHERE mccl.CONTACT_LIST_ID=cclp.CONTACT_LIST_ID AND mccl.CONTACT_PURPOSE_TYPE='"+contactPurposeType+"'"
						+ " AND cclp.party_id IN ("+StringUtil.join(StringUtil.quoteStrList(partyIds), ",")+")";
			        rs = sqlProcessor.executeQuery(sql);
			        if (rs != null) {
			            while (rs.next()) {
			            	campaignIds.add(rs.getString("MARKETING_CAMPAIGN_ID"));
			            }
			        }
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return campaignIds;
	}
	
	public static List<String> getStoreIds(Delegator delegator, Map<String, Object> filter) {
		List<String> storeIds = new ArrayList<>();
		try {
			if (UtilValidate.isNotEmpty(filter)) {
				String campaignId = (String) filter.get("campaignId");
				String productStoreType = (String) filter.get("productStoreType");
				
				if (UtilValidate.isNotEmpty(campaignId)) {
					List<EntityCondition> conditions = new ArrayList<EntityCondition>();
        			conditions.add(EntityCondition.makeCondition("campaignId", EntityOperator.EQUALS, campaignId));
        			if (UtilValidate.isNotEmpty(productStoreType)) {
        				conditions.add(EntityCondition.makeCondition("productStoreType", EntityOperator.EQUALS, productStoreType));
        			}
        			conditions.add(EntityUtil.getFilterByDateExpr());
                	EntityCondition mainConditon = EntityCondition.makeCondition(conditions, EntityOperator.AND);
                	List<GenericValue> storeAssocList = EntityQuery.use(delegator).select("productStoreId").from("CampaignStoreAssoc").where(mainConditon).queryList();
                	if (UtilValidate.isNotEmpty(storeAssocList)) {
                		storeIds = storeAssocList.stream().map(x->x.getString("productStoreId")).collect(Collectors.toList());
                	}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return storeIds;
	}
	
	public static Map<String, Object> getCampaignSummary(Delegator delegator, String marketingCampaignId) {
		Map<String, Object> responseContext = new LinkedHashMap<String, Object>();
		try {
			if (UtilValidate.isNotEmpty(marketingCampaignId)) {
				GenericValue marketingCampaign = EntityQuery.use(delegator).from("MarketingCampaign").select("campaignName").where("marketingCampaignId", marketingCampaignId).cache(false).queryOne();
				if (UtilValidate.isNotEmpty(marketingCampaign)) {
					String globalDateFormat = org.fio.homeapps.util.DataHelper.getGlobalDateFormat(delegator);
					
					String campaignTypeId = (String) marketingCampaign.get("campaignTypeId");
					responseContext.put("campaignTypeId", campaignTypeId);
					
					ResultSet rs = null;
					SQLProcessor sqlProcessor = new SQLProcessor(delegator, delegator.getGroupHelperInfo("org.ofbiz"));
					
					String campaignName = "";
					String campaignId = "";
					String campaignTemplateId = "";
					String product = "";
					String deliveryChannel = "";
					String campaignStatus = "";
					long totalEmailSent = 0;
					long notOpen = 0;
					long clicked = 0;
					long bounced = 0;
					long unsubscribed = 0;
					long converted = 0;
					long opened = 0;
					String validCustomerCount = "";
					String startDate = "";
					String endDate = "";
					String targetRevenue = "";
					String targetMargin = "";
					String openTarget = "";
					String clickTarget = "";
					String conversionTarget = "";
					String actualSpend = "";
					String actualRevenue = "";
					String actualMargin = "";
					String actualOpenTarget = "";
					String actualClickTarget = "";
					String actualConversionTarget = "";
					String owner = "";
					String salesTriggerType = "";
					String parentCampaign = "";
					String masterCampaign = "";
					String testCampaignStatus = "";
					
					String groupId = UtilAttribute.getAttrValue(delegator, "MarketingCampaignAttribute", "marketingCampaignId", marketingCampaignId, "GROUP_ID");
					
					//specific for phone campaign
					long totalCall = 0;
					long notStarted = 0;
					long attempt1 = 0;
					long attempt2 = 0;
					long attempt3 = 0;
					long callWon = 0;
					long callLost = 0;
					long openOpportunity = 0;
					long closedOpportunity = 0;
					
					if (UtilValidate.isNotEmpty(marketingCampaignId)) {
					    String campaignSql = "SELECT `CAMPAIGN_NAME` as 'campaign_name', `MARKETING_CAMPAIGN_ID` as 'campaign_id', `CAMPAIGN_TEMPLATE_ID` as 'campaign_template_id', PRODUCT as 'product', `CAMPAIGN_TYPE_ID` as 'delivery_channel', STATUS_ID as 'campaign_status', date(`START_DATE`) as 'start_date', date(`END_DATE`) as 'end_date', `OWNER_ID` as 'owner_id', `SALES_TRIGGER_TYPE` as 'sale_trigger_type', `PARENT_CAMPAIGN_ID` as 'parent_campaign_id', `MASTER_CAMPAIGN_ID` as 'master_campaign_id', `CAMP_TEST_STATUS` as 'camp_test_status'  FROM `marketing_campaign` WHERE `MARKETING_CAMPAIGN_ID`='" + marketingCampaignId + "'";
					    rs = sqlProcessor.executeQuery(campaignSql);
					    if (rs != null) {
					        String campaignStatusId = "";
					        while (rs.next()) {
					            campaignName = rs.getString("campaign_name");
					            campaignId = rs.getString("campaign_id");
					            campaignTemplateId = rs.getString("campaign_template_id");
					            product = rs.getString("product");
					            deliveryChannel = rs.getString("delivery_channel");
					            campaignStatusId = rs.getString("campaign_status");
					            startDate = rs.getString("start_date");
					            endDate = rs.getString("end_date");
					            owner = rs.getString("owner_id");
					            salesTriggerType = rs.getString("sale_trigger_type");
					            parentCampaign = rs.getString("parent_campaign_id");
					            masterCampaign = rs.getString("master_campaign_id");
					            testCampaignStatus = rs.getString("camp_test_status");
					        }
					        if (UtilValidate.isNotEmpty(campaignStatusId)) {
					            String statusItemSql = "SELECT description FROM status_item WHERE status_id = ?";
					            List<Object> values = new ArrayList<>();
					            values.add(campaignStatusId);
					            rs = QueryUtil.getResultSet(statusItemSql, values, delegator);
					            if (rs != null) {
					                while (rs.next()) {
					                    campaignStatus = rs.getString("description");
					                }
					            }
					        }
					    }
					    if (!"PHONE_CALL".equals(campaignTypeId)) {
					        
					        String totalCallSql = "SELECT COUNT(1) as 'total_count' FROM campaign_contact_list_party cclp INNER JOIN marketing_campaign_contact_list mccl ON mccl.CONTACT_LIST_ID=cclp.CONTACT_LIST_ID"; 
					        totalCallSql += " WHERE mccl.MARKETING_CAMPAIGN_ID='" + marketingCampaignId + "' AND mccl.CONTACT_PURPOSE_TYPE='LIVE' AND cclp.NOT_OPEN='Y'";
					        rs = sqlProcessor.executeQuery(totalCallSql);
					        if (rs != null) {
					            while (rs.next()) {
					                notOpen = rs.getLong("total_count");
					            }
					        }
					        
					        totalCallSql = "SELECT COUNT(1) as 'total_count' FROM campaign_contact_list_party cclp INNER JOIN marketing_campaign_contact_list mccl ON mccl.CONTACT_LIST_ID=cclp.CONTACT_LIST_ID"; 
					        totalCallSql += " WHERE mccl.MARKETING_CAMPAIGN_ID='" + marketingCampaignId + "' AND mccl.CONTACT_PURPOSE_TYPE='LIVE' AND cclp.OPENED='Y'";
					        rs = sqlProcessor.executeQuery(totalCallSql);
					        if (rs != null) {
					            while (rs.next()) {
					                opened = rs.getLong("total_count");
					            }
					        }
					        
					        totalCallSql = "SELECT COUNT(1) as 'total_count' FROM campaign_contact_list_party cclp INNER JOIN marketing_campaign_contact_list mccl ON mccl.CONTACT_LIST_ID=cclp.CONTACT_LIST_ID"; 
					        totalCallSql += " WHERE mccl.MARKETING_CAMPAIGN_ID='" + marketingCampaignId + "' AND mccl.CONTACT_PURPOSE_TYPE='LIVE' AND cclp.SENT='Y'";
					        rs = sqlProcessor.executeQuery(totalCallSql);
					        if (rs != null) {
					            while (rs.next()) {
					                totalEmailSent = rs.getLong("total_count");
					            }
					        }
					        
					        totalCallSql = "SELECT COUNT(1) as 'total_count' FROM campaign_contact_list_party cclp INNER JOIN marketing_campaign_contact_list mccl ON mccl.CONTACT_LIST_ID=cclp.CONTACT_LIST_ID"; 
					        totalCallSql += " WHERE mccl.MARKETING_CAMPAIGN_ID='" + marketingCampaignId + "' AND mccl.CONTACT_PURPOSE_TYPE='LIVE' AND cclp.CONVERTED='Y'";
					        rs = sqlProcessor.executeQuery(totalCallSql);
					        if (rs != null) {
					            while (rs.next()) {
					                converted = rs.getLong("total_count");
					            }
					        }
					        
					        totalCallSql = "SELECT COUNT(1) as 'total_count' FROM campaign_contact_list_party cclp INNER JOIN marketing_campaign_contact_list mccl ON mccl.CONTACT_LIST_ID=cclp.CONTACT_LIST_ID"; 
					        totalCallSql += " WHERE mccl.MARKETING_CAMPAIGN_ID='" + marketingCampaignId + "' AND mccl.CONTACT_PURPOSE_TYPE='LIVE' AND cclp.CLICKED='Y'";
					        rs = sqlProcessor.executeQuery(totalCallSql);
					        if (rs != null) {
					            while (rs.next()) {
					                clicked = rs.getLong("total_count");
					            }
					        }
					        
					        totalCallSql = "SELECT COUNT(1) as 'total_count' FROM campaign_contact_list_party cclp INNER JOIN marketing_campaign_contact_list mccl ON mccl.CONTACT_LIST_ID=cclp.CONTACT_LIST_ID"; 
					        totalCallSql += " WHERE mccl.MARKETING_CAMPAIGN_ID='" + marketingCampaignId + "' AND mccl.CONTACT_PURPOSE_TYPE='LIVE' AND cclp.BOUNCED='Y'";
					        rs = sqlProcessor.executeQuery(totalCallSql);
					        if (rs != null) {
					            while (rs.next()) {
					                bounced = rs.getLong("total_count");
					            }
					        }
					        
					        totalCallSql = "SELECT COUNT(1) as 'total_count' FROM campaign_contact_list_party cclp INNER JOIN marketing_campaign_contact_list mccl ON mccl.CONTACT_LIST_ID=cclp.CONTACT_LIST_ID"; 
					        totalCallSql += " WHERE mccl.MARKETING_CAMPAIGN_ID='" + marketingCampaignId + "' AND mccl.CONTACT_PURPOSE_TYPE='LIVE' AND cclp.unsubscribed='Y'";
					        rs = sqlProcessor.executeQuery(totalCallSql);
					        if (rs != null) {
					            while (rs.next()) {
					                unsubscribed = rs.getLong("total_count");
					            }
					        }
					        
					        /*
					        String campaignSummarySql = "SELECT `SENT_MAIL_COUNT` as 'email_sent_count', `NOT_OPENED_MAIL_COUNT` as 'not_open', `CLICKED_MAIL_COUNT` as 'clicked', `BOUNCED_MAIL_COUNT` as 'bounced', `UNSUBSCRIBE_COUNT` as 'unsubscribed' FROM `marketing_campaign` a, `marketing_campaign_analysis_summary` b WHERE a.`MARKETING_CAMPAIGN_ID`=b.`CAMPAIGN_ID` AND a.`CAMPAIGN_TYPE_ID` IN('EMAIL','SMS') AND  a.`MARKETING_CAMPAIGN_ID`='" + marketingCampaignId + "'";
					        rs = sqlProcessor.executeQuery(campaignSummarySql);
					        if (rs != null) {
					            while (rs.next()) {
					                totalEmailSent = rs.getString("email_sent_count");
					                notOpen = rs.getString("not_open");
					                clicked = rs.getString("clicked");
					                bounced = rs.getString("bounced");
					                unsubscribed = rs.getString("unsubscribed");
					            }
					        }
					        */
					    }
					    if ("PHONE_CALL".equals(campaignTypeId)) {
					        String totalCallSql = "SELECT COUNT(DISTINCT `SALES_OPPORTUNITY_ID`) as 'total_call' FROM MARKETING_CAMPAIGN a INNER JOIN `sales_opportunity` b ON a.MARKETING_CAMPAIGN_ID=b.`MARKETING_CAMPAIGN_ID` WHERE a.MARKETING_CAMPAIGN_ID='" + marketingCampaignId + "'";
					        rs = sqlProcessor.executeQuery(totalCallSql);
					        if (rs != null) {
					            while (rs.next()) {
					                totalCall = rs.getLong("total_call");
					            }
					        }
					        String phoneCampSummarySql = "SELECT `NOT_STARTED` as 'not_started', ATTEMPT_1 as 'attempt_1', ATTEMPT_2 as 'attempt_2', ATTEMPT_3 as 'attempt_3' FROM `marketing_campaign_phone_summary` WHERE `MARKETING_CAMPAIGN_ID`='" + marketingCampaignId + "'";
					        rs = sqlProcessor.executeQuery(phoneCampSummarySql);
					        if (rs != null) {
					            while (rs.next()) {
					                notStarted = rs.getLong("not_started");
					                attempt1 = rs.getLong("attempt_1");
					                attempt2 = rs.getLong("attempt_2");
					                attempt3 = rs.getLong("attempt_3");
					            }
					        }
					
					        String callWonSql = "SELECT COUNT(DISTINCT `SALES_OPPORTUNITY_ID`) as 'call_won' FROM MARKETING_CAMPAIGN a INNER JOIN `sales_opportunity` b ON a.MARKETING_CAMPAIGN_ID=b.`MARKETING_CAMPAIGN_ID` WHERE `OPPORTUNITY_STAGE_ID`='SOSTG_WON' AND `CAMPAIGN_TYPE_ID`='PHONE_CALL' AND a.MARKETING_CAMPAIGN_ID='" + marketingCampaignId + "'";
					        rs = sqlProcessor.executeQuery(callWonSql);
					        if (rs != null) {
					            while (rs.next()) {
					                callWon = rs.getLong("call_won");
					            }
					        }
					        String callLostSql = "SELECT COUNT(DISTINCT `SALES_OPPORTUNITY_ID`) as 'call_lost' FROM MARKETING_CAMPAIGN a INNER JOIN `sales_opportunity` b ON a.MARKETING_CAMPAIGN_ID=b.`MARKETING_CAMPAIGN_ID` WHERE `OPPORTUNITY_STAGE_ID`='SOSTG_LOST' AND `CAMPAIGN_TYPE_ID`='PHONE_CALL' AND a.MARKETING_CAMPAIGN_ID='" + marketingCampaignId + "'";
					        rs = sqlProcessor.executeQuery(callLostSql);
					        if (rs != null) {
					            while (rs.next()) {
					                callLost = rs.getLong("call_lost");
					            }
					        }
					        String totalOpenOppoSql = "SELECT COUNT(DISTINCT `SALES_OPPORTUNITY_ID`) as 'open_opportunity' FROM MARKETING_CAMPAIGN a INNER JOIN `sales_opportunity` b ON a.MARKETING_CAMPAIGN_ID=b.`MARKETING_CAMPAIGN_ID` WHERE `OPPORTUNITY_STATUS_ID`='OPPO_OPEN' AND `CAMPAIGN_TYPE_ID`='PHONE_CALL' AND a.MARKETING_CAMPAIGN_ID='" + marketingCampaignId + "'";
					        rs = sqlProcessor.executeQuery(totalOpenOppoSql);
					        if (rs != null) {
					            while (rs.next()) {
					                openOpportunity = rs.getLong("open_opportunity");
					            }
					        }
					        String totalClosedOppoSql = "SELECT COUNT(DISTINCT `SALES_OPPORTUNITY_ID`) as 'closed_opportunity' FROM MARKETING_CAMPAIGN a INNER JOIN `sales_opportunity` b ON a.MARKETING_CAMPAIGN_ID=b.`MARKETING_CAMPAIGN_ID` WHERE `OPPORTUNITY_STATUS_ID`='OPPO_CLOSED' AND `CAMPAIGN_TYPE_ID`='PHONE_CALL' AND a.MARKETING_CAMPAIGN_ID='" + marketingCampaignId + "'";
					        rs = sqlProcessor.executeQuery(totalClosedOppoSql);
					        if (rs != null) {
					            while (rs.next()) {
					                closedOpportunity = rs.getLong("closed_opportunity");
					            }
					        }
					    }
					
					    String totalCustomerSql = "SELECT COUNT(DISTINCT CCLP.PARTY_ID) as 'total_customer' FROM MARKETING_CAMPAIGN MC INNER JOIN MARKETING_CAMPAIGN_CONTACT_LIST MCCL ON MC.`MARKETING_CAMPAIGN_ID`=MCCL.`MARKETING_CAMPAIGN_ID` INNER JOIN CAMPAIGN_CONTACT_LIST_PARTY CCLP ON MCCL.`CONTACT_LIST_ID`=CCLP.`CONTACT_LIST_ID` WHERE MC.`MARKETING_CAMPAIGN_ID`='"+marketingCampaignId+"' AND MCCL.CONTACT_PURPOSE_TYPE='LIVE' AND CCLP.THRU_DATE IS NULL";
					    rs = sqlProcessor.executeQuery(totalCustomerSql);
					    if (rs != null) {
					        while (rs.next()) {
					            validCustomerCount = rs.getString("total_customer");
					        }
					    }
					
					    String campaignMetricsSql = "SELECT BUDGET_REVENUE_VALUE as 'target_revenue', BUDGET_MARGIN_VALUE as 'target_margin', OPEN_TARGET_PERC as 'open_target', CLICK_TARGET_PERC as 'click_target', CONVERSION_TARGET_PERC as 'conversion_target', ACTUAL_SPEND_VALUE as 'actual_spend', ACTUAL_BUDGET_REVENUE as 'actual_revenue', ACTUAL_BUDGET_MARGIN as 'actual_margin', ACTUAL_OPEN_TARGET as 'actual_open_target', ACTUAL_CLICK_TARGET as 'actual_click_target', ACTUAL_CONVERSION_TARGET as 'actual_conversion_target' FROM `marketing_campaign` a, `marketing_campaign_financial_metrics` b WHERE a.`MARKETING_CAMPAIGN_ID`=b.`MARKETING_CAMPAIGN_ID` AND a.`MARKETING_CAMPAIGN_ID`='" + marketingCampaignId + "'";
					
					    rs = sqlProcessor.executeQuery(campaignMetricsSql);
					    if (rs != null) {
					        while (rs.next()) {
					            targetRevenue = rs.getString("target_revenue");
					            targetMargin = rs.getString("target_margin");
					            openTarget = rs.getString("open_target");
					            clickTarget = rs.getString("click_target");
					            conversionTarget = rs.getString("conversion_target");
					            actualSpend = rs.getString("actual_spend");
					            actualRevenue = rs.getString("actual_revenue");
					            actualMargin = rs.getString("actual_margin");
					            actualOpenTarget = rs.getString("actual_open_target");
					            actualClickTarget = rs.getString("actual_click_target");
					            actualConversionTarget = rs.getString("actual_conversion_target");
					        }
					    }
					    sqlProcessor.close();
					}
					
					responseContext.put("campaign-name", campaignName);
					responseContext.put("campaign-id", marketingCampaignId);
					responseContext.put("group-id", groupId);
					responseContext.put("campaign-template-id", campaignTemplateId);
					responseContext.put("product", product);
					responseContext.put("delivery-channel", deliveryChannel);
					responseContext.put("campaign-status", campaignStatus);
					responseContext.put("valid-customers", validCustomerCount);
					//responseContext.put("start-date", startDate);
					//responseContext.put("end-date", endDate);
					responseContext.put("target-revenue", targetRevenue);
					responseContext.put("target-margin", targetMargin);
					responseContext.put("open-target", openTarget);
					responseContext.put("click-target", clickTarget);
					responseContext.put("conversion-target", conversionTarget);
					responseContext.put("actual-spend", actualSpend);
					responseContext.put("actual-revenue", actualRevenue);
					responseContext.put("actual-margin", actualMargin);
					responseContext.put("actual-open-target", actualOpenTarget);
					responseContext.put("actual-click-target", actualClickTarget);
					responseContext.put("actual-conversion-target", actualConversionTarget);
					responseContext.put("owner", owner);
					responseContext.put("sales-trigger-type", salesTriggerType);
					responseContext.put("parent-campaign", parentCampaign);
					responseContext.put("master-campaign", masterCampaign);
					responseContext.put("test-campaign-status", testCampaignStatus);
					
					Timestamp startDateTime = null;
					Timestamp endDateTime = null;
					if (UtilValidate.isNotEmpty(startDate)) {
						startDateTime = UtilDateTime.stringToTimeStamp(startDate, "yyyy-MM-dd", TimeZone.getDefault(), Locale.getDefault());
						startDate = UtilDateTime.timeStampToString(startDateTime, globalDateFormat, TimeZone.getDefault(), Locale.getDefault());
					}
					if (UtilValidate.isNotEmpty(endDate)) {
						endDateTime = UtilDateTime.stringToTimeStamp(endDate, "yyyy-MM-dd", TimeZone.getDefault(), Locale.getDefault());
						endDate = UtilDateTime.timeStampToString(endDateTime, globalDateFormat, TimeZone.getDefault(), Locale.getDefault());
					}
					
					responseContext.put("start-date", startDate);
					responseContext.put("end-date", endDate);
					
					String campignState = "";
					if (UtilValidate.isNotEmpty(startDateTime)) {
						if (startDateTime.getTime() < UtilDateTime.nowTimestamp().getTime()
								&& (UtilValidate.isEmpty(endDateTime) || endDateTime.getTime() > UtilDateTime.nowTimestamp().getTime())) {
							campignState = "cmp-active";
						} else if (startDateTime.getTime() > UtilDateTime.nowTimestamp().getTime()) {
							campignState = "cmp-future";
						} else if (startDateTime.getTime() < UtilDateTime.nowTimestamp().getTime()
								&& (UtilValidate.isNotEmpty(endDateTime) && endDateTime.getTime() < UtilDateTime.nowTimestamp().getTime())) {
							campignState = "cmp-expired";
						}
					}
					responseContext.put("campign-state", campignState);
					
					if (!"PHONE_CALL".equals(campaignTypeId)) {
					    responseContext.put("sent", totalEmailSent);
					    responseContext.put("not-open", notOpen);
					    responseContext.put("opened", opened);
					    responseContext.put("clicked", clicked);
					    responseContext.put("bounced", bounced);
					    responseContext.put("unsubscribe", unsubscribed);
					    responseContext.put("converted", converted);
					}
					if ("PHONE_CALL".equals(campaignTypeId)) {
					    responseContext.put("total-call", totalCall);
					    responseContext.put("not-started", notStarted);
					    responseContext.put("attempt-1", attempt1);
					    responseContext.put("attempt-2", attempt2);
					    responseContext.put("attempt-3", attempt3);
					    responseContext.put("call-won", callWon);
					    responseContext.put("call-lost", callLost);
					    responseContext.put("open-opportunity", openOpportunity);
					    responseContext.put("closed-opportunity", closedOpportunity);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return responseContext;
	}

	public static List<String> getCsrList(Delegator delegator, GenericValue userLogin, boolean isTeamSpecific) throws GenericEntityException{
		return getCsrList(delegator, userLogin, isTeamSpecific, false);
	}

	public static List<String> getCsrMembersExcludeLeadersList(Delegator delegator, GenericValue userLogin, boolean isTeamSpecific) throws GenericEntityException{
		return getCsrList(delegator, userLogin, isTeamSpecific, true);
	}

	public static List<String> getCsrList(Delegator delegator, GenericValue userLogin, boolean isTeamSpecific, boolean skipLeaders) throws GenericEntityException{
		Set<String> partyIds = new LinkedHashSet<>();
		boolean fullAdmin = false;
		if(UtilValidate.isNotEmpty(userLogin)) {
			EntityCondition condition = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("userLoginId",EntityOperator.EQUALS,userLogin.getString("userLoginId")),
					EntityCondition.makeCondition("groupId",EntityOperator.EQUALS,"FULLADMIN"),
					EntityUtil.getFilterByDateExpr()),EntityOperator.AND);
			long securityCount = EntityQuery.use(delegator).from("UserLoginSecurityGroup").where(condition).queryCount();
			if(securityCount > 0) {
				fullAdmin = true;
			}
		}
		if(isTeamSpecific) {
			List<EntityCondition> teamCondList = new ArrayList<>();
			if(!fullAdmin && UtilValidate.isNotEmpty(userLogin)) {
				teamCondList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, userLogin.getString("partyId")));
			}
			teamCondList.add(EntityCondition.makeConditionDate("fromDate","thruDate"));
			EntityCondition teamConditons = EntityCondition.makeCondition(teamCondList, EntityOperator.AND);
			List<GenericValue> teamList = delegator.findList("EmplPositionFulfillment", teamConditons, null, null, null, false);
			if(UtilValidate.isNotEmpty(teamList)){
				for(GenericValue emplPosition : teamList) {
					GenericValue team = delegator.findOne("EmplTeam",UtilMisc.toMap("emplTeamId",emplPosition.getString("emplTeamId")),false);
					if(UtilValidate.isEmpty(team) || (UtilValidate.isNotEmpty(team.getString("isActive")) && "N".equals(team.getString("isActive"))))
						continue;

					String isTeamLead=emplPosition.getString("isTeamLead");
					if(UtilValidate.isNotEmpty(isTeamLead) && "Y".equals(isTeamLead)){
						if(!skipLeaders) {
							if(UtilValidate.isNotEmpty(userLogin)) {
								partyIds.add(userLogin.getString("partyId"));
							}
						}
						List<EntityCondition> teamMemberCondList = new ArrayList<>();
						teamMemberCondList.add(EntityCondition.makeCondition("emplTeamId", EntityOperator.EQUALS, emplPosition.getString("emplTeamId")));
						teamMemberCondList.add(EntityCondition.makeConditionDate("fromDate","thruDate"));
						if(skipLeaders) {
							List<EntityExpr> cond = UtilMisc.toList(
									EntityCondition.makeCondition("isTeamLead", EntityOperator.EQUALS, "N"),
									EntityCondition.makeCondition("isTeamLead", EntityOperator.EQUALS, null)
									);
							teamMemberCondList.add(EntityCondition.makeCondition(cond, EntityOperator.OR));
						}
						EntityCondition teamMemberConditons = EntityCondition.makeCondition(teamMemberCondList, EntityOperator.AND);

						List<GenericValue> teamMemberList = delegator.findList("EmplPositionFulfillment", teamMemberConditons, null, null, null, false);

						partyIds.addAll(EntityUtil.getFieldListFromEntityList(teamMemberList, "partyId", true));
					}else if(UtilValidate.isNotEmpty(userLogin)) {
						partyIds.add(userLogin.getString("partyId"));
					}
				}
			}
			return UtilMisc.toList(partyIds);
		} else {
			EntityCondition csrCondition = EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "CUST_SERVICE_REP");
			return EntityUtil.getFieldListFromEntityList(delegator.findList("PartyRole", csrCondition, UtilMisc.toSet("partyId"), null, null, true),"partyId",true);
		}
	}
	
	public static List<Map<String,String>> getCsrDropdown(Delegator delegator, GenericValue userLogin, boolean isTeamSpecific) throws GenericEntityException{
		List<Map<String,String>> dropdownList = new ArrayList<>();
		List<String> csrs = getCsrList(delegator, userLogin, isTeamSpecific);
		for(String partyId : csrs) {
			dropdownList.add(UtilMisc.toMap("partyId", partyId, "name", PartyHelper.getPartyName(delegator, partyId, false)));
		}
		return dropdownList;
	}
	
	public static GenericValue getLatestCallRecordMaster(Delegator delegator, String marketingCampaignId, String contactListId, String partyId) {
		return getLatestCallRecordMaster(delegator, marketingCampaignId, contactListId, partyId, false);
	}
	
	public static GenericValue getLatestCallRecordMaster(Delegator delegator, String marketingCampaignId, String contactListId, String partyId, boolean skipFinished) {
		try {
			List < EntityCondition > callRecordMasterconditionsList = new ArrayList < EntityCondition > ();
			callRecordMasterconditionsList.add(EntityCondition.makeCondition(EntityOperator.AND,
					EntityCondition.makeCondition("marketingCampaignId", EntityOperator.EQUALS, marketingCampaignId),
					EntityCondition.makeCondition("contactListId", EntityOperator.EQUALS, contactListId),
					EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId)));
			if(skipFinished) {
				callRecordMasterconditionsList.add(EntityCondition.makeCondition(EntityOperator.OR,
						EntityCondition.makeCondition("callFinished", EntityOperator.EQUALS, null),
						EntityCondition.makeCondition("callFinished", EntityOperator.EQUALS, ""),
						EntityCondition.makeCondition("callFinished", EntityOperator.EQUALS, "N")));
			}
			return EntityQuery.use(delegator).from("CallRecordMaster").where(EntityCondition.makeCondition(callRecordMasterconditionsList, EntityOperator.AND)).orderBy("callRecordId DESC").queryFirst();
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Map<String,Object> getCallBackDateLastCallStatus(Delegator delegator, String marketingCampaignId, String contactListId, String partyId, boolean ignoreAPIStatus){
		Map<String,Object> result = new HashMap<String, Object>();
		List<EntityCondition> conditionList = UtilMisc.toList(
				EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,partyId),
				EntityCondition.makeCondition("marketingCampaignId",EntityOperator.EQUALS, marketingCampaignId),
				EntityCondition.makeCondition("contactListId",EntityOperator.EQUALS, contactListId));
		if(ignoreAPIStatus) {
			conditionList.add(EntityCondition.makeCondition("callStatusId",EntityOperator.NOT_IN, UtilMisc.toList("SUCCESS", "ERROR")));
		}
		EntityCondition condition = EntityCondition.makeCondition(conditionList,EntityOperator.AND);
		List<String> orderBy = UtilMisc.toList("callRecordId DESC","callRecordDetailSeqId DESC");
		Set<String> select = UtilMisc.toSet("callBackDate","callStatusId");
		try {
			DynamicViewEntity dynamicViewEntity = new DynamicViewEntity();
			dynamicViewEntity.addMemberEntity("CRD", "CallRecordDetails");
			dynamicViewEntity.addAlias("CRD", "callRecordId");
			dynamicViewEntity.addAlias("CRD", "callRecordDetailSeqId");
			dynamicViewEntity.addAlias("CRD", "callStatusId");
			dynamicViewEntity.addAlias("CRD", "partyId");

			dynamicViewEntity.addMemberEntity("CRM", "CallRecordMaster");
			dynamicViewEntity.addAlias("CRM", "marketingCampaignId");
			dynamicViewEntity.addAlias("CRM", "contactListId");
			dynamicViewEntity.addAlias("CRM", "callBackDate");
			dynamicViewEntity.addAlias("CRM", "callFinished");
			dynamicViewEntity.addViewLink("CRD", "CRM", Boolean.FALSE, ModelKeyMap.makeKeyMapList("callRecordId"));

			GenericValue callRecordMaster = EntityQuery.use(delegator).from(dynamicViewEntity).where(condition).select(select).orderBy(orderBy).queryFirst();
			if(UtilValidate.isNotEmpty(callRecordMaster)) {
				result.put("callBackDate", callRecordMaster.getDate("callBackDate"));
				result.put("callStatusId", callRecordMaster.getString("callStatusId"));
			}

		}catch (GenericEntityException e) {
			String errMsg = "Error: " + e.toString();
			Debug.logError(e, errMsg, MODULE);
		}
		return result;
	}
	
	public static Map<String, Object> getEmailStatistics(Delegator delegator, LocalDispatcher dispatcher, GenericValue userLogin, Map<String, Object> filter) {
		Map<String, Object> result = FastMap.newInstance();
		
		Map<String, Object> callCtxt = FastMap.newInstance();
		Map<String, Object> callResult = FastMap.newInstance();
		
		int blocks = 0;
		int bounces = 0;
		int clicks = 0;
		int deferred = 0;
		int delivered = 0;
		int invalidEmails = 0;
		int opens = 0;
		int processed = 0;
		int requests = 0;
		int spamReports = 0;
		int uniqueClicks = 0;
		int uniqueOpens = 0;
		int unsubscribes = 0;
		
		try {
			String defaultEmailEngine = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "DEFAULT_EMAIL_ENGINE");
    		if ( (UtilValidate.isNotEmpty(defaultEmailEngine) && defaultEmailEngine.equals("SENDGRID"))
    				) {
    			Map<String, Object> requestContext = new LinkedHashMap<>();
    			
    			String startDate = (String) filter.get("startDate");
    			if (UtilValidate.isEmpty(startDate)) {
    				startDate = UtilDateTime.timeStampToString(UtilDateTime.nowTimestamp(), "yyyy-MM-dd", TimeZone.getDefault(), Locale.getDefault());
    			}
    			callCtxt.put("startDate", startDate);
    			
    			String endDate = (String) filter.get("endDate");
    			requestContext.put("endDate", endDate);
    			
    			callCtxt.put("requestContext", requestContext);
    			callCtxt.put("userLogin", userLogin);
    			
    			callResult = dispatcher.runSync("sendgrid.getEmailStatistics", callCtxt);
    			if (ServiceUtil.isSuccess(callResult)) {
    				Map<String, Object> responseContext = (Map<String, Object>) callResult.get("responseContext");
    				if (UtilValidate.isNotEmpty(responseContext)) {
    					if (UtilValidate.isNotEmpty(responseContext.get("data"))) {
    						Map<String, Object> metrics = (Map<String, Object>) responseContext.get("data");
    						blocks = UtilValidate.isNotEmpty(metrics.get("blocks")) ? (int) metrics.get("blocks") : 0;
							bounces = UtilValidate.isNotEmpty(metrics.get("bounces")) ? (int) metrics.get("bounces") : 0;
							clicks = UtilValidate.isNotEmpty(metrics.get("clicks")) ? (int) metrics.get("clicks") : 0;
							deferred = UtilValidate.isNotEmpty(metrics.get("deferred")) ? (int) metrics.get("deferred") : 0;
							delivered = UtilValidate.isNotEmpty(metrics.get("delivered")) ? (int) metrics.get("delivered") : 0;
							invalidEmails = UtilValidate.isNotEmpty(metrics.get("invalidEmails")) ? (int) metrics.get("invalidEmails") : 0;
							opens = UtilValidate.isNotEmpty(metrics.get("opens")) ? (int) metrics.get("opens") : 0;
							processed = UtilValidate.isNotEmpty(metrics.get("processed")) ? (int) metrics.get("processed") : 0;
							requests = UtilValidate.isNotEmpty(metrics.get("requests")) ? (int) metrics.get("requests") : 0;
							spamReports = UtilValidate.isNotEmpty(metrics.get("spamReports")) ? (int) metrics.get("spamReports") : 0;
							uniqueClicks = UtilValidate.isNotEmpty(metrics.get("uniqueClicks")) ? (int) metrics.get("uniqueClicks") : 0;
							uniqueOpens = UtilValidate.isNotEmpty(metrics.get("uniqueOpens")) ? (int) metrics.get("uniqueOpens") : 0;
							unsubscribes = UtilValidate.isNotEmpty(metrics.get("unsubscribes")) ? (int) metrics.get("unsubscribes") : 0;
    					}
    				}
    			}
    		}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		result.put("blocks", blocks);
		result.put("bounces", bounces);
		result.put("clicks", clicks);
		result.put("deferred", deferred);
		result.put("delivered", delivered);
		result.put("invalidEmails", invalidEmails);
		result.put("opens", opens);
		result.put("processed", processed);
		result.put("requests", requests);
		result.put("spamReports", spamReports);
		result.put("uniqueClicks", uniqueClicks);
		result.put("uniqueOpens", uniqueOpens);
		result.put("unsubscribes", unsubscribes);
		return result;
	}
	
	public static Map<String, Object> prepareCallRecordNotification(Delegator delegator, LocalDispatcher dispatcher, Map<String, Object> context) {
		Map<String, Object> response = FastMap.newInstance();
		
		Map<String, Object> callCtxt = FastMap.newInstance();
		Map<String, Object> callResult = FastMap.newInstance();
		try {
			if (UtilValidate.isNotEmpty(context)) {
				GenericValue userLogin = (GenericValue) context.get("userLogin");
				
				String partyId = (String) context.get("partyId");
				String marketingCampaignId = (String) context.get("marketingCampaignId");
				String contactListId = (String) context.get("contactListId");
				String callRecordId = (String) context.get("callRecordId");
				String callRecordDetailSeqId = (String) context.get("callRecordDetailSeqId");
				String csrPartyId = (String) context.get("csrPartyId");
				String notifyOwnerId = (String) context.get("notifyOwnerId");
				Timestamp callBackDate = (Timestamp) context.get("callBackDate");
				
				Timestamp currentTime = UtilDateTime.nowTimestamp();
				List<String> ownerList = new ArrayList<>();
				
				String applicationUrl = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "APPLICATION_URL");
				
				if (UtilValidate.isNotEmpty(callRecordDetailSeqId)) {
					GenericValue callDetail = EntityQuery.use(delegator).from("CallRecordDetails").where("callRecordDetailSeqId", callRecordDetailSeqId).queryFirst();
                	if (UtilValidate.isNotEmpty(callDetail)) {
                		GenericValue callMaster = EntityQuery.use(delegator).from("CallRecordMaster").where("callRecordId", callRecordId).queryFirst();
                		
                		String workEffortId = callMaster.getString("workEffortId");
                		String activityNamePrefix = "Appointment";
                		String workEffortName = null;
                		boolean isCreateActivity = false;
                		if (UtilValidate.isNotEmpty(workEffortId)) {
                			GenericValue workEffort = EntityQuery.use(delegator).from("WorkEffort").where("workEffortId", workEffortId).queryFirst();
                        	if (UtilValidate.isNotEmpty(workEffort)
                        			&& UtilValidate.isNotEmpty(workEffort.getString("currentStatusId"))
                    				&& !workEffort.getString("currentStatusId").equals("IA_MCOMPLETED") && !workEffort.getString("currentStatusId").equals("IA_CLOSED")) {
                    			workEffort.put("estimatedStartDate", null);
                    			workEffort.put("estimatedCompletionDate", null);
                    			workEffort.put("actualStartDate", UtilDateTime.getDayStart(callBackDate));
                    			workEffort.put("actualCompletionDate", UtilDateTime.getDayEnd(callBackDate));
                    			workEffort.store();
                        	} else {
                        		isCreateActivity = true;
                        	}
                		} else {
                			isCreateActivity = true;
                		}
                		
                		String userLoginPartyName = PartyHelper.getPartyName(delegator, userLogin.getString("partyId"), false);
                		userLoginPartyName = UtilValidate.isNotEmpty(userLoginPartyName) ? userLoginPartyName : "";
                			
                		//String viewCustomerLink = "<a href='/customer-portal/control/viewCallListCustomer?marketingCampaignId="+marketingCampaignId+"&contactListId="+contactListId+"&partyId="+partyId+"'>"+PartyHelper.getPartyName(delegator, partyId, false)+" ("+partyId+")"+"</a>";
                		String viewCustomerLink = PartyHelper.getPartyName(delegator, partyId, false)+" ("+partyId+")";
                		workEffortName = userLoginPartyName+" updated for Call Back# "+viewCustomerLink;
                		String phone = UtilContactMech.getPartyPhone(delegator, partyId, null);
                		if (UtilValidate.isNotEmpty(phone)) {
                			workEffortName += ", Ph:"+phone;
                		}
                		
                		if (isCreateActivity) {
                    		callCtxt = FastMap.newInstance();
                			
                			callCtxt.put("workEffortTypeId", "APPOINTMENT");
                			callCtxt.put("currentStatusId", "IA_OPEN");
                			callCtxt.put("workEffortName", workEffortName);
                			callCtxt.put("actualStartDate", UtilDateTime.getDayStart(callBackDate));
                			callCtxt.put("actualCompletionDate", UtilDateTime.getDayEnd(callBackDate));
                			callCtxt.put("wftLocation", null);
                			callCtxt.put("workEffortPurposeTypeId", "CALL_BACK");
                			callCtxt.put("createdDate", currentTime);
                			
                			if (UtilValidate.isNotEmpty(workEffortName)) {
                				callCtxt.put("description", Base64.getEncoder().encodeToString(workEffortName.getBytes()));
                			}
                			
                			//callCtxt.put("wftMsdduration", duration);
                			//callCtxt.put("priority", Long.valueOf(priority));
                			/*
                			callCtxt.put("partyId", csrPartyId);
                			callCtxt.put("roleTypeId", org.fio.homeapps.util.PartyHelper.getPartyRoleTypeId(csrPartyId, delegator));
                			callCtxt.put("partyUserLoginId", org.fio.homeapps.util.DataUtil.getActiveUserLoginIdByPartyId(delegator, csrPartyId));
                			*/
                			callCtxt.put("nrequired", UtilMisc.toList(partyId));
                			//callCtxt.put("noptional", partyId);
                			
                			/*callCtxt.put("emplTeamId", emplTeamId);
                			callCtxt.put("businessUnitId", businessUnit);
                			callCtxt.put("wftMsdbusinessunit", businessUnit);
                			callCtxt.put("businessUnitId", businessUnit);*/
                			
                			if (UtilValidate.isNotEmpty(notifyOwnerId)) {
                				ownerList.add(notifyOwnerId);
                			}
                			callCtxt.put("ownerList", ownerList);	
                			
                			callCtxt.put("endPointType", "OFCRM");
                			callCtxt.put("domainEntityType", "CUSTOMER");
                			callCtxt.put("domainEntityId", partyId);
                			
                			callCtxt.put("userLogin", userLogin);
                			
                			callResult = dispatcher.runSync("crmPortal.createInteractiveActivity", callCtxt);
                			if (ServiceUtil.isSuccess(callResult)) {
                				workEffortId = (String) callResult.get("workEffortId");
                				String callBackUrl = "marketingCampaignId="+marketingCampaignId+"&contactListId="+contactListId+"&partyId="+partyId;
                				
                				UtilActivity.storeActivityAttribute(delegator, workEffortId, "CALL_BACK_URL", callBackUrl);
                			}
                		}
                		
                		if (UtilValidate.isNotEmpty(workEffortId)) {
                			callDetail.put("workEffortId", workEffortId);
                			callDetail.put("workEffortTypeId", "APPOINTMENT");
                			callDetail.store();
                			
                			callMaster.put("workEffortId", workEffortId);
                			callMaster.put("workEffortTypeId", "APPOINTMENT");
                			callMaster.store();
                			
                			//String viewUrl = applicationUrl + "/" + "activity-portal/control/updateApnt?workEffortId="+workEffortId;
                			String viewUrl = applicationUrl + "/customer-portal/control/viewCallListCustomer?marketingCampaignId="+marketingCampaignId+"&contactListId="+contactListId+"&partyId="+partyId;
            				
            				Map<String, Object> eventMap = new HashMap<String, Object>();
            				eventMap.put("entityId", workEffortId);
            				eventMap.put("entityName", "WorkEffort");
            				eventMap.put("eventType", "APPOINTMENT");

            				eventMap.put("eventDate", currentTime);
            				eventMap.put("eventName", activityNamePrefix);
            				eventMap.put("eventDescription", workEffortName);
            				eventMap.put("eventUrl", viewUrl);
            				eventMap.put("entityOwnerId", notifyOwnerId);
            				eventMap.put("domainEntityId", callRecordId);
            				eventMap.put("domainEntityType", "CALL_RECORD");
            				eventMap.put("userLogin", userLogin);
            				callResult = dispatcher.runSync("notificationEventRegister", eventMap);
            				if (ServiceUtil.isSuccess(callResult)) {
                				response.put("isNotificationCreated", "Y");
                			}
                		}
                	}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(UtilMessage.getPrintStackTrace(e), MODULE);
		}
		return response;
	}
	
}
