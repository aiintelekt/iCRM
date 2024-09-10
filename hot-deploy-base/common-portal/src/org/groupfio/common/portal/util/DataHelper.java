/**
 * 
 */
package org.groupfio.common.portal.util;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.TimeZone;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.fio.homeapps.util.CacheUtil;
import org.fio.homeapps.util.DataUtil;
import org.fio.homeapps.util.ParamUtil;
import org.fio.homeapps.util.PartyHelper;
import org.fio.homeapps.util.UtilDateTime;
import org.groupfio.common.portal.CommonPortalConstants;
import org.groupfio.common.portal.CommonPortalConstants.DomainEntityType;
import org.groupfio.common.portal.CommonPortalConstants.SlaSetupConstants;
import org.groupfio.common.portal.resolver.OpportunityEscalationResolver;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.StringUtil.StringWrapper;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityFieldValue;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.party.contact.ContactMechWorker;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import javolution.util.FastList;
import javolution.util.FastMap;

/**
 * @author Sharif
 */
public class DataHelper {
	
	private static String MODULE = DataHelper.class.getName();
	private static final String ALPHANUMERIC_REGEX = "[a-zA-Z0-9]+";
	public static final ObjectMapper mapper = new ObjectMapper();
	
	public static String getOpportunityStageDescription(Delegator delegator, String value) {
		return getOpportunityStageDescription(delegator, value, null);
	}
	
	public static String getOpportunityStageDescription(Delegator delegator, String value, String geoId) {
		
		try {
			if (UtilValidate.isNotEmpty(value)) {
				List conditionList = FastList.newInstance();
				
				conditionList.add( EntityCondition.makeCondition(EntityOperator.OR,
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("opportunityStageId")), EntityOperator.EQUALS, value.toString().toUpperCase()),
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("description")), EntityOperator.EQUALS, value.toString().toUpperCase())
               			) );    
				
				if (UtilValidate.isNotEmpty(geoId)) {
					conditionList.add( EntityCondition.makeCondition("geoId", EntityOperator.EQUALS, geoId));       
				}
				
				EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				
				GenericValue stage = EntityUtil.getFirst( delegator.findList("SalesOpportunityStage", mainConditons, UtilMisc.toSet("description"), null, null, false) );
				if (UtilValidate.isNotEmpty(stage)) {
					return stage.getString("description");
				}
			}
		} catch (Exception e) {
		}
		
		return null;
	}
	
	public static long getMyOpportunityCount(Delegator delegator, String userLoginId) {
		
		try {
			if (UtilValidate.isNotEmpty(userLoginId)) {
				List conditionList = FastList.newInstance();
				
				conditionList.add(EntityCondition.makeCondition("opportunityStageId", EntityOperator.IN, UtilMisc.toList("SOSTG_WON", "SOSTG_LOST", "SOSTG_OPEN", "SOSTG_CLOSED")));
				//conditionList.add(EntityCondition.makeCondition("opportunityStageId", EntityOperator.IN, UtilMisc.toList("SOSTG_OPEN")));
				conditionList.add(EntityCondition.makeCondition("assignedUserLogin", EntityOperator.EQUALS, userLoginId));
				EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				return delegator.findCountByCondition("SalesOpportunitySummary", mainConditons, null, UtilMisc.toSet("salesOpportunityId"), null);
				
			}
		} catch (Exception e) {
		}
		
		return 0;
	}
	
	public static long getMyTeamOpportunityCount(Delegator delegator, String emplTeamId) {
		
		try {
			if (UtilValidate.isNotEmpty(emplTeamId)) {
				List conditionList = FastList.newInstance();
				
				conditionList.add(EntityCondition.makeCondition("opportunityStageId", EntityOperator.IN, UtilMisc.toList("SOSTG_WON", "SOSTG_LOST", "SOSTG_OPEN", "SOSTG_CLOSED")));
				//conditionList.add(EntityCondition.makeCondition("opportunityStageId", EntityOperator.IN, UtilMisc.toList("SOSTG_OPEN")));
				conditionList.add(EntityCondition.makeCondition("emplTeamId", EntityOperator.EQUALS, emplTeamId));
				EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				return delegator.findCountByCondition("SalesOpportunitySummary", mainConditons, null, UtilMisc.toSet("salesOpportunityId"), null);
				
			}
		} catch (Exception e) {
		}
		
		return 0;
	}
	
	public static long convertMBtoBytes(String value) {
		if (UtilValidate.isNotEmpty(value)) {
			return new Long(value) * 1024 * 1024;
		}
		return 0;
	}
	
	public static String convertBytestoMB(long value) {
		if (UtilValidate.isNotEmpty(value)) {
			return new DecimalFormat("#.##").format(((float)value / 1024 / 1024));
		}
		return null;
	}
	
	public static String convertToLabel(String value) {
		if (UtilValidate.isNotEmpty(value)) {
			return WordUtils.capitalize(value.toLowerCase().replace("_", " "));
		}
		return null;
	}
	
	public static void contentAssociate(Delegator delegator, Map<String, Object> assocContext) {
		try {
			String contentId = ParamUtil.getString(assocContext, "contentId");
			
			String partyId = ParamUtil.getString(assocContext, "partyId");
			String salesOpportunityId = ParamUtil.getString(assocContext, "salesOpportunityId");
			String custRequestId = ParamUtil.getString(assocContext, "custRequestId");
			String workEffortId = ParamUtil.getString(assocContext, "workEffortId");
			String publicOrPrivate = ParamUtil.getString(assocContext, "publicOrPrivate");
			
			String domainEntityId = ParamUtil.getString(assocContext, "domainEntityId");
			String domainEntityType = ParamUtil.getString(assocContext, "domainEntityType");
			
			if(UtilValidate.isNotEmpty(partyId)){
				GenericValue partyContent = delegator.makeValue("PartyContent");
				partyContent.set("contentId", contentId);
				partyContent.set("partyId", partyId);
				partyContent.set("partyContentTypeId", "PARTY_ATTACHMENT_DATA");
				partyContent.set("fromDate", UtilDateTime.nowTimestamp());
        		partyContent.create();
			}
			
			if(UtilValidate.isNotEmpty(salesOpportunityId)){
				GenericValue opporContent = delegator.makeValue("OpportunityContent");
				opporContent.set("contentId", contentId);
				opporContent.set("salesOpportunityId", salesOpportunityId);
				opporContent.set("contentTypeId", "PARTY_ATTACHMENT_DATA");
				opporContent.set("fromDate", UtilDateTime.nowTimestamp());
				opporContent.create();
			}
			
			if(UtilValidate.isNotEmpty(custRequestId)){
				GenericValue srContent = delegator.makeValue("CustRequestContent");
				srContent.set("contentId", contentId);
				srContent.set("custRequestId", custRequestId);
				srContent.set("contentTypeId", "SR_ATTACHMENT_DATA");
				srContent.set("fromDate", UtilDateTime.nowTimestamp());
				srContent.create();
			}
			
			if(UtilValidate.isNotEmpty(workEffortId)){
				GenericValue activityContent = delegator.makeValue("WorkEffortContent");
				activityContent.set("contentId", contentId);
				activityContent.set("workEffortId", workEffortId);
				activityContent.set("workEffortContentTypeId", "ACTIVITY_ATTACHMENT_DATA");
				activityContent.set("fromDate", UtilDateTime.nowTimestamp());
				activityContent.create();
			}
			
			if (UtilValidate.isNotEmpty(domainEntityId) && UtilValidate.isNotEmpty(domainEntityType) && CommonPortalConstants.COMMON_ATTACHMENT_ENTITY_TYPE.containsKey(domainEntityType)) {
				GenericValue commonContent = delegator.makeValue("CommonContent");
				commonContent.set("contentId", contentId);
				commonContent.set("domainEntityId", domainEntityId);
				commonContent.set("domainEntityType", domainEntityType);
				commonContent.set("contentTypeId", "ATTACHMENT");
				commonContent.set("fromDate", UtilDateTime.nowTimestamp());
				commonContent.create();
			}
			
			if(UtilValidate.isNotEmpty(publicOrPrivate)){
				UtilAttribute.storeContentAttrValue(delegator, contentId, "IS_PUBLIC", publicOrPrivate);
			}
				
		} catch (Exception e) {
			Debug.logError(e.getMessage(), MODULE);
		}
	}
	
	public static String prepareLinkedFrom(String domainEntityId, String domainEntityType, String externalLoginKey) {
		if (UtilValidate.isNotEmpty(domainEntityId) && UtilValidate.isNotEmpty(domainEntityType)) {
			if (domainEntityType.equals(DomainEntityType.ACCOUNT)) {
				return "/account-portal/control/viewAccount?partyId="+domainEntityId+"&externalLoginKey="+externalLoginKey;
			} else if (domainEntityType.equals(DomainEntityType.LEAD)) {
				return "/lead-portal/control/viewLead?partyId="+domainEntityId+"&externalLoginKey="+externalLoginKey;
			} else if (domainEntityType.equals(DomainEntityType.CONTACT)) {
				return "/contact-portal/control/viewContact?partyId="+domainEntityId+"&externalLoginKey="+externalLoginKey;
			} else if (domainEntityType.equals(DomainEntityType.OPPORTUNITY)) {
				return "/opportunity-portal/control/viewOpportunity?salesOpportunityId="+domainEntityId+"&externalLoginKey="+externalLoginKey;
			} else if (domainEntityType.equals(DomainEntityType.SERVICE_REQUEST)) {
				return "/sr-portal/control/viewServiceRequest?srNumber="+domainEntityId+"&externalLoginKey="+externalLoginKey;
			} else if (domainEntityType.equals(DomainEntityType.SERVICE)) {
				return "/service-portal/control/viewSr?srNumber="+domainEntityId+"&externalLoginKey="+externalLoginKey;
			} else if (domainEntityType.equals(DomainEntityType.CUSTOMER)) {
				return "/customer-portal/control/viewCustomer?partyId="+domainEntityId+"&externalLoginKey="+externalLoginKey;
			} else if (domainEntityType.equals(DomainEntityType.ACTIVITY)) {
				return "/sr-portal/control/viewActivity?workEffortId="+domainEntityId+"&externalLoginKey="+externalLoginKey;
			} else if (domainEntityType.equals(DomainEntityType.REBATE)) {
				return "/rebate-portal/control/viewRebate?agreementId="+domainEntityId+"&externalLoginKey="+externalLoginKey;
			} else if (domainEntityType.equals(DomainEntityType.APV_TPL)) {
				return "/approval-portal/control/viewTemplate?approvalId="+domainEntityId+"&externalLoginKey="+externalLoginKey;
			} else if (domainEntityType.equals(DomainEntityType.CAMPAIGN)) {
				return "/campaign/control/viewMarketingCampaign?marketingCampaignId="+domainEntityId+"&externalLoginKey="+externalLoginKey;
			}
		}
		return null;
	}
	
	public static String getDomainEntityName(Delegator delegator, String domainEntityId, String domainEntityType) {
		try {
			if (UtilValidate.isNotEmpty(domainEntityId) && UtilValidate.isNotEmpty(domainEntityType)) {
				if (CommonPortalConstants.PARTY_DOMAIN_ENTITY_TYPE.containsKey(domainEntityType)) {
					return PartyHelper.getPartyName(delegator, domainEntityId, false);
				} else if (domainEntityType.equals(DomainEntityType.OPPORTUNITY)) {
					GenericValue entity = EntityQuery.use(delegator).select("opportunityName").from("SalesOpportunity").where("salesOpportunityId", domainEntityId).cache(false).queryFirst();
					return UtilValidate.isNotEmpty(entity) ? entity.getString("opportunityName") : null;
				} else if (CommonPortalConstants.SERVICE_DOMAIN_ENTITY_TYPE.containsKey(domainEntityType)) {
					GenericValue entity = EntityQuery.use(delegator).select("custRequestName").from("CustRequest").where("custRequestId", domainEntityId).cache(false).queryFirst();
					return UtilValidate.isNotEmpty(entity) ? entity.getString("custRequestName") : null;
				} else if (domainEntityType.equals(DomainEntityType.ACTIVITY)) {
					GenericValue entity = EntityQuery.use(delegator).select("workEffortName").from("WorkEffort").where("workEffortId", domainEntityId).cache(false).queryFirst();
					return UtilValidate.isNotEmpty(entity) ? entity.getString("workEffortName") : null;
				} else if (domainEntityType.equals(DomainEntityType.REBATE)) {
					GenericValue entity = EntityQuery.use(delegator).select("description").from("Agreement").where("agreementId", domainEntityId).cache(false).queryFirst();
					return UtilValidate.isNotEmpty(entity) ? entity.getString("description") : null;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getFormattedNumValue(Delegator delegator, String value) {
		if (UtilValidate.isNotEmpty(value)) {
			return getFormattedNumValue(delegator, value, null);
		}
		return value;
	}
	
	public static String getFormattedNumValue(Delegator delegator, String value, String format) {
		if (UtilValidate.isNotEmpty(value)) {
			String defaultNumFormat = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "NUM_FORMAT", "###,###.##"); 
			if (UtilValidate.isNotEmpty(format)) {
				defaultNumFormat = format;
			}
			
			DecimalFormat myFormatter = new DecimalFormat(defaultNumFormat);
			try {
				value = myFormatter.format(new BigDecimal(value));
			} catch (Exception e) {
				e.printStackTrace();
			}
			return value;
		}
		return value;
	}
	
	public static StringWrapper getHelpUrl(Delegator delegator, String reqUri) {
		GenericValue pageReqUrl = null;
		String helpUrl = "";
		StringWrapper finalHelpUrl = null;

		try {
			if (UtilValidate.isNotEmpty(reqUri)) {
				String commonlink = DataUtil.getGlobalValue(delegator, "COMMON_HELP_URL");
				if(UtilValidate.isNotEmpty(commonlink)) {
					helpUrl = commonlink;
				} else {
					pageReqUrl = EntityUtil.getFirst(
							delegator.findByAnd("OfbizPageSecurity", UtilMisc.toMap("requestUri", reqUri), null, false));
					if (UtilValidate.isEmpty(pageReqUrl)) {
						pageReqUrl = EntityUtil.getFirst(delegator.findByAnd("OfbizTabSecurityShortcut",
								UtilMisc.toMap("requestUri", reqUri), null, false));
					}
					if (UtilValidate.isNotEmpty(pageReqUrl)) {
						helpUrl = (String) pageReqUrl.getString("helpUrl");
					}
				}
				String finalLinkUrl = "";
				if (UtilValidate.isNotEmpty(helpUrl)) {
					finalLinkUrl = "<a target=\"_blank\"  href=\"" + helpUrl
							+ "\" class=\"btn btn-xs btn-primary m5 \" ><i class=\"fa fa-question-circle\" aria-hidden=\"true\"></i> Help</a>";
					finalHelpUrl = StringUtil.wrapString(finalLinkUrl);
					return finalHelpUrl;
				} else {
					finalHelpUrl = StringUtil.wrapString(finalLinkUrl);
					return finalHelpUrl;
				}

			}
		} catch (GenericEntityException e) {
			Debug.logError(e.getMessage(), MODULE);
		}
		if (UtilValidate.isEmpty(helpUrl)) {
			helpUrl = "";
		}
		finalHelpUrl = StringUtil.wrapString(helpUrl);
		return finalHelpUrl;
	}
	
	public static StringWrapper getTabHelpUrl(Delegator delegator, String reqUri, String tab) {
		GenericValue pageReqUrl = null;
		String helpUrl = "";
		StringWrapper finalHelpUrl = null;

		try {
			String commonlink = DataUtil.getGlobalValue(delegator, "COMMON_HELP_URL");
			if(UtilValidate.isNotEmpty(commonlink)) {
				helpUrl = commonlink;
			} else {
				if (UtilValidate.isNotEmpty(reqUri) && UtilValidate.isNotEmpty(tab)) {
					pageReqUrl = EntityQuery.use(delegator).from("OfbizTabHelp").where("tabId", tab, "requestUri", reqUri).queryFirst();
				} else if (UtilValidate.isNotEmpty(reqUri) && UtilValidate.isEmpty(tab)) {
					pageReqUrl = EntityQuery.use(delegator).from("OfbizTabHelp").where("requestUri", reqUri).queryFirst();
				}
				if (UtilValidate.isNotEmpty(pageReqUrl)) {
					helpUrl = (String) pageReqUrl.getString("helpUrl");
				}
			}
			
			String finalLinkUrl = "";
			if (UtilValidate.isNotEmpty(helpUrl)) {
				finalLinkUrl = "<a target=\"_blank\"  href=\"" + helpUrl
						+ "\" class=\"btn btn-xs btn-primary m5 \" ><i class=\"fa fa-question-circle\" aria-hidden=\"true\"></i> Help</a>";
				finalHelpUrl = StringUtil.wrapString(finalLinkUrl);
				return finalHelpUrl;
			} else {
				finalHelpUrl = StringUtil.wrapString(finalLinkUrl);
				return finalHelpUrl;
			}
		} catch (GenericEntityException e) {
			Debug.logError(e.getMessage(), MODULE);
		}
		if (UtilValidate.isEmpty(helpUrl)) {
			helpUrl = "";
		}
		finalHelpUrl = StringUtil.wrapString(helpUrl);
		return finalHelpUrl;
	}
	public static String getHelpUrl(Delegator delegator, String reqUri, String tab) {
		GenericValue pageReqUrl = null;
		String helpUrl = "";
		try {
			String commonlink = DataUtil.getGlobalValue(delegator, "COMMON_HELP_URL");
			if(UtilValidate.isNotEmpty(commonlink)) {
				helpUrl = commonlink;
			} else {
				if (UtilValidate.isNotEmpty(reqUri) && UtilValidate.isNotEmpty(tab)) {
					pageReqUrl = EntityQuery.use(delegator).from("OfbizTabHelp").where("tabId", tab, "requestUri", reqUri).cache(true).queryFirst();
				} else if (UtilValidate.isNotEmpty(reqUri) && UtilValidate.isEmpty(tab)) {
					pageReqUrl = EntityQuery.use(delegator).from("OfbizTabHelp").where("requestUri", reqUri).cache(true).queryFirst();
				}
				if (UtilValidate.isNotEmpty(pageReqUrl)) {
					helpUrl = (String) pageReqUrl.getString("helpUrl");
				}
			}
		} catch (GenericEntityException e) {
			Debug.logError(e.getMessage(), MODULE);
		}
		return helpUrl;
	}
	
	public static String getSrPrimaryContact(Delegator delegator, String value) {

		try {
			if (UtilValidate.isNotEmpty(value)) {
				
				List conditionList = FastList.newInstance();
				
				conditionList.add(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, value));
				conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "CONTACT"));
				conditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
				EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);

				GenericValue primaryCustRequestContact = EntityUtil.getFirst( delegator.findList("CustRequestContact", mainConditons, null, null, null, false) );

				if (UtilValidate.isNotEmpty(primaryCustRequestContact)) {
					return primaryCustRequestContact.getString("partyId");
				}
			}
		} catch (Exception e) {
			Debug.logError(e.getMessage(), MODULE);
		}

		return null;
	}
	
	public static String getGlobalDateFormat(Delegator delegator) {
		try {
			String globalDateFormat = "MM/dd/yyyy";
			GenericValue defaultGlobalDateFormat = EntityQuery.use(delegator).from("PretailLoyaltyGlobalParameters").where("parameterId","DATE_FORMAT").queryFirst();
				
			if (UtilValidate.isNotEmpty(defaultGlobalDateFormat)) {
				globalDateFormat = defaultGlobalDateFormat.getString("value").trim();
			}
			return globalDateFormat;
		} catch (Exception e) {
			Debug.logError(e.getMessage(), MODULE);
		}
		return null;
	}
	
	public static String getGlobalDateTimeFormat(Delegator delegator) {
		try {
			String globalFormat = org.groupfio.common.portal.util.DataHelper.getGlobalDateFormat(delegator);
			globalFormat = globalFormat + " HH:mm";
			return globalFormat;
		} catch (Exception e) {
			Debug.logError(e.getMessage(), MODULE);
		}
		return null;
	}
	
	public static String getActivitySrType(Delegator delegator, String workEffortId) {
		String srType = null;
		try {
			if (UtilValidate.isNotEmpty(workEffortId)) {
				List conditionList = FastList.newInstance();
				conditionList.add( EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId));      
				EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				
				GenericValue entity = EntityUtil.getFirst( delegator.findList("CustRequestWorkEffort", mainConditons, UtilMisc.toSet("custRequestId"), null, null, false) );
				if (UtilValidate.isNotEmpty(entity)) {
					String custRequestId = entity.getString("custRequestId");
					conditionList = FastList.newInstance();
					conditionList.add( EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId));      
					mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
					GenericValue sr = EntityUtil.getFirst( delegator.findList("CustRequest", mainConditons, UtilMisc.toSet("custRequestTypeId"), null, null, false) );
					if (UtilValidate.isNotEmpty(sr)) {
						srType = DataUtil.getCustRequestTypeDesc(delegator, sr.getString("custRequestTypeId"));
					} 
				}
			}
		} catch (Exception e) {
		}
		return srType;
	}
	
	public static String wrapPostalAddress(Delegator delegator, GenericValue postal) {
		try {
			if (UtilValidate.isNotEmpty(postal)) {
				String wrapPostal = "";
				if (UtilValidate.isNotEmpty(postal.getString("toName"))) {
					wrapPostal += "Name: "+postal.getString("toName")+"</br>";
				}
				if (UtilValidate.isNotEmpty(postal.getString("attnName"))) {
					wrapPostal += "Attention Name: "+postal.getString("attnName")+"</br>";
				}
				if (UtilValidate.isNotEmpty(postal.getString("address1"))) {
					wrapPostal += "Address 1: "+postal.getString("address1")+"</br>";
				}
				if (UtilValidate.isNotEmpty(postal.getString("address2"))) {
					wrapPostal += "Address 2: "+postal.getString("address2")+"</br>";
				}
				if (UtilValidate.isNotEmpty(postal.getString("city"))) {
					wrapPostal += "City: "+postal.getString("city")+"</br>";
				}
				if (UtilValidate.isNotEmpty(postal.getString("stateProvinceGeoId"))) {
					wrapPostal += "State/Province: "+DataUtil.getGeoName(delegator, postal.getString("stateProvinceGeoId"), "STATE,PROVINCE")+"</br>";
				}
				if (UtilValidate.isNotEmpty(postal.getString("postalCode"))) {
					wrapPostal += "Zip/Postal Code: "+postal.getString("postalCode")+"</br>";
				}
				return wrapPostal;
			}
		} catch (Exception e) {
			Debug.logError(e.getMessage(), MODULE);
		}
		return null;
	}
	
	public static String wrapSrPostalAddress(Delegator delegator, GenericValue postal) {
		try {
			if (UtilValidate.isNotEmpty(postal)) {
				String wrapPostal = "";
				if (UtilValidate.isNotEmpty(postal.getString("pstlAttnName"))) {
					wrapPostal += postal.getString("pstlAttnName")+", ";
				}
				if (UtilValidate.isNotEmpty(postal.getString("pstlAddress1"))) {
					wrapPostal += postal.getString("pstlAddress1")+", ";
				}
				if (UtilValidate.isNotEmpty(postal.getString("pstlAddress2"))) {
					wrapPostal += postal.getString("pstlAddress2")+", ";
				}
				if (UtilValidate.isNotEmpty(postal.getString("pstlPostalCity"))) {
					wrapPostal += postal.getString("pstlPostalCity")+", ";
				}
				if (UtilValidate.isNotEmpty(postal.getString("pstlStateProvinceGeoId"))) {
					wrapPostal += DataUtil.getGeoName(delegator, postal.getString("pstlStateProvinceGeoId"), "STATE,PROVINCE")+", ";
				}
				if (UtilValidate.isNotEmpty(postal.getString("pstlPostalCode"))) {
					wrapPostal += postal.getString("pstlPostalCode");
					if (UtilValidate.isNotEmpty(postal.getString("pstlPostalCodeExt"))) {
						wrapPostal += "-"+postal.getString("pstlPostalCodeExt");
					}
				}
				if (UtilValidate.isNotEmpty(postal.getString("pstlCountyGeoId"))) {
					wrapPostal += " ("+postal.getString("pstlCountyGeoId")+")";
				}
				/*if (UtilValidate.isNotEmpty(wrapPostal)) {
					wrapPostal = wrapPostal.substring(0, wrapPostal.length()-2);
				}*/
				return wrapPostal;
			}
		} catch (Exception e) {
			Debug.logError(e.getMessage(), MODULE);
		}
		return null;
	}
	
	public static Map<String, Object> getGeoCoordinate(Delegator delegator, Map<String, Object> context) {
		Map<String, Object> corodinate = new LinkedHashMap<>();
		try {
			if (UtilValidate.isNotEmpty(context)) {
				String zip5 = (String) context.get("zip5");
				String zip4 = (String) context.get("zip4");
				String city = (String) context.get("city");
				String state = (String) context.get("state");
				String county = (String) context.get("county");
				
				List conditionList = FastList.newInstance();
				conditionList.add( EntityCondition.makeCondition("zip", EntityOperator.EQUALS, zip5));    
				
				if (UtilValidate.isNotEmpty(city)) {
					conditionList.add( EntityCondition.makeCondition("city", EntityOperator.EQUALS, city));    
				}
				if (UtilValidate.isNotEmpty(state)) {
					conditionList.add( EntityCondition.makeCondition("state", EntityOperator.EQUALS, state));    
				}
				if (UtilValidate.isNotEmpty(county)) {
					conditionList.add( EntityCondition.makeCondition("county", EntityOperator.EQUALS, county));    
				}
				
				EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				GenericValue entity = EntityUtil.getFirst( delegator.findList("ZipCodeAssoc", mainConditons, UtilMisc.toSet("latitude", "longitude"), null, null, false) );
				if (UtilValidate.isNotEmpty(entity)) {
					corodinate.put("latitude", entity.getString("latitude"));
					corodinate.put("longitude", entity.getString("longitude"));
				}
			}
		} catch (Exception e) {
		}
		return corodinate;
	}
	public static Map<String, Object> getGeoCoordinate(Delegator delegator, String zip5, String zip4) {
		Map<String, Object> corodinate = new LinkedHashMap<>();
		try {
			if (UtilValidate.isNotEmpty(zip5)) {
				List conditionList = FastList.newInstance();
				conditionList.add( EntityCondition.makeCondition("zip", EntityOperator.EQUALS, zip5));      
				EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				
				GenericValue entity = EntityUtil.getFirst( delegator.findList("ZipCodeAssoc", mainConditons, UtilMisc.toSet("latitude", "longitude"), null, null, false) );
				if (UtilValidate.isNotEmpty(entity)) {
					corodinate.put("latitude", entity.getString("latitude"));
					corodinate.put("longitude", entity.getString("longitude"));
				}
			}
		} catch (Exception e) {
		}
		return corodinate;
	}
	
	public static Map<String, Object> getGeoCoordinateByGoogleApi(Map<String, Object> context) {
		Map<String, Object> corodinate = new LinkedHashMap<>();
		try {
			if (UtilValidate.isNotEmpty(context)) {
				
				Delegator delegator = (Delegator) context.get("delegator");
				LocalDispatcher dispatcher = (LocalDispatcher) context.get("dispatcher");
				GenericValue userLogin = (GenericValue) context.get("userLogin");
				
				String zip5 = (String) context.get("zip5");
				String zip4 = (String) context.get("zip4");
				String city = (String) context.get("city");
				String state = (String) context.get("state");
				String county = (String) context.get("county");
				String country = (String) context.get("country");
				
				String address1 = (String) context.get("address1");
				String address2 = (String) context.get("address2");
				
				Map<String, Object> callCtxt = FastMap.newInstance();
				Map<String, Object> callResult = FastMap.newInstance();
				boolean isValidAddress = false;
				
				callCtxt.put("zip5", zip5);
				callCtxt.put("zip4", zip4);
				callCtxt.put("address1", address1);
				callCtxt.put("address2", address2);
				callCtxt.put("city", city);
				callCtxt.put("state", state);
				callCtxt.put("country", country);
				callCtxt.put("userLogin", userLogin);

				callResult = dispatcher.runSync("postalApp.googleGeocodeRetrive", callCtxt);
				if (ServiceUtil.isSuccess(callResult)) {
					isValidAddress = (boolean) callResult.get("isValidAddress");
					if (isValidAddress) {
						Map<String, Object> address = (Map<String, Object>) callResult.get("address");
						corodinate.put("latitude", address.get("lat"));
						corodinate.put("longitude", address.get("lan"));
					}
				}
				
				if (!isValidAddress) {
					Map<String, Object> address = DataHelper.getGeoCoordinate(delegator, context);
					corodinate.put("latitude", address.get("latitude"));
					corodinate.put("longitude", address.get("longitude"));
				}
			}
		} catch (Exception e) {
			Debug.logError("Error getGeoCoordinateByGoogleApi: "+e.getMessage(), MODULE);
		}
		return corodinate;
	}
	
	public static List jsonArrayStrToMapList(String jsonBodyStr) {
		TypeFactory factory = mapper.getTypeFactory();
		CollectionType listType = 
			    factory.constructCollectionType(List.class, Map.class);

		List<Map<String, Object>> result = null;
		try {
			result = mapper.readValue(jsonBodyStr, listType);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return result;
	}
	
	public static String preparePhoneNumber(Delegator delegator, String value) {
		try {
			if (UtilValidate.isNotEmpty(value)) {
				String phoneNumberFormat = (String) CacheUtil.getInstance().get("PHONE_NUMBER_FORMAT");
				if (UtilValidate.isEmpty(phoneNumberFormat)) {
					phoneNumberFormat = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "PHONE_NUMBER_FORMAT", "(\\d{3})(\\d{3})(\\d+)|($1)-$2-$3");
					CacheUtil.getInstance().put("PHONE_NUMBER_FORMAT", phoneNumberFormat);
				}
				
				String regex = phoneNumberFormat.substring(0, phoneNumberFormat.indexOf("|"));
				String replacement = phoneNumberFormat.substring(phoneNumberFormat.indexOf("|")+1, phoneNumberFormat.length());
				
				value  = value.replaceFirst(regex, replacement);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}
	
	public static Object formatDate(Delegator delegator, Object value, String format) {
		try {
			if (UtilValidate.isNotEmpty(value) && value instanceof Timestamp && UtilValidate.isNotEmpty(format)) {
				/*String dateFormat = (String) CacheUtil.getInstance().get("DATE_FORMAT");
				if (UtilValidate.isEmpty(dateFormat)) {
					dateFormat = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "DATE_FORMAT", UtilDateTime.DATE_FORMAT);
					CacheUtil.getInstance().put("DATE_FORMAT", dateFormat);
				}*/
				format = format.replace("YYYY", "yyyy").replace("DD", "dd");
				value = UtilDateTime.timeStampToString((Timestamp)value, format, TimeZone.getDefault(), Locale.getDefault());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}
	
	public static String getCustomFieldId(Delegator delegator, String groupId, String customFieldName) {
		String customFieldId = "";
		try {
			if(UtilValidate.isNotEmpty(customFieldName)) {
				List<EntityCondition> conditions = new ArrayList<EntityCondition>();
				if(UtilValidate.isNotEmpty(groupId)) {
					conditions.add(EntityCondition.makeCondition("groupId", EntityOperator.EQUALS, groupId));
				}
				conditions.add(EntityCondition.makeCondition("customFieldName", EntityOperator.EQUALS, customFieldName));
				
				GenericValue customField = EntityQuery.use(delegator).select("customFieldId").from("CustomField").where(EntityCondition.makeCondition(conditions, EntityOperator.AND)).queryFirst();
				customFieldId = UtilValidate.isNotEmpty(customField) ? customField.getString("customFieldId") : "";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return customFieldId;
	}
	
	public static String getRebateTypeSymbol(Delegator delegator, String programId) {
		String rebateTypeSymbol = "%";
		try {
			if(UtilValidate.isNotEmpty(programId)) {
				List<EntityCondition> conditions = new ArrayList<EntityCondition>();
				conditions.add(EntityCondition.makeCondition("agreementItemSeqId", EntityOperator.EQUALS, programId));
				GenericValue program = EntityQuery.use(delegator).select("amountType").from("AgreementItem").where(EntityCondition.makeCondition(conditions, EntityOperator.AND)).queryFirst();
				if (UtilValidate.isNotEmpty(program)) {
					if (UtilValidate.isNotEmpty(program.getString("amountType")) && program.getString("amountType").equals("PERCENTAGE")) {
						rebateTypeSymbol = "%";
					} else if (UtilValidate.isNotEmpty(program.getString("amountType")) && program.getString("amountType").equals("AMOUNT")) {
						rebateTypeSymbol = "$";
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rebateTypeSymbol;
	}
	
	public static Map<String, Object> getPayoutMetaData(Delegator delegator, Map<String, Object> context) {
		Map<String, Object> payoutMeta = new HashMap<>();
		try {
			String programId = (String) context.get("programId");
			Double tySales = (Double) context.get("actualTySales");
			if(UtilValidate.isNotEmpty(programId)) {
				String rebateTypeSymbol = "%";
				BigDecimal tyDirSaleAmount = new BigDecimal(0);
				BigDecimal totalTySales = UtilValidate.isNotEmpty(tySales) ? new BigDecimal(tySales) : new BigDecimal(0);
				
				List<EntityCondition> conditions = new ArrayList<EntityCondition>();
				conditions.add(EntityCondition.makeCondition("agreementItemSeqId", EntityOperator.EQUALS, programId));
				GenericValue program = EntityQuery.use(delegator).select("amountType", "tyDirSaleAmount").from("AgreementItem").where(EntityCondition.makeCondition(conditions, EntityOperator.AND)).queryFirst();
				if (UtilValidate.isNotEmpty(program)) {
					if (UtilValidate.isNotEmpty(program.getString("amountType")) && program.getString("amountType").equals("PERCENTAGE")) {
						rebateTypeSymbol = "%";
					} else if (UtilValidate.isNotEmpty(program.getString("amountType")) && program.getString("amountType").equals("AMOUNT")) {
						rebateTypeSymbol = "$";
					}
					tyDirSaleAmount = UtilValidate.isNotEmpty(program.getBigDecimal("tyDirSaleAmount")) ? program.getBigDecimal("tyDirSaleAmount").setScale(2, RoundingMode.HALF_DOWN) : new BigDecimal(0).setScale(2, RoundingMode.HALF_DOWN);
					
				}
				totalTySales = totalTySales.add(tyDirSaleAmount).setScale(2, RoundingMode.HALF_DOWN);
				
				payoutMeta.put("rebateTypeSymbol", rebateTypeSymbol);
				payoutMeta.put("totalTySales", totalTySales);
				payoutMeta.put("tyDirSaleAmount", tyDirSaleAmount);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return payoutMeta;
	}
	
	public static Map getRoofTopPostalAddress(Delegator delegator, Map < String, Object > context) {
	    //Delegator delegator = (Delegator) dctx.getDelegator();
	    Locale locale = (Locale) context.get("locale");
	    GenericValue userLogin = (GenericValue) context.get("userLogin");

	    String domainEntityId = (String) context.get("domainEntityId");

	    Timestamp nowTimestamp = UtilDateTime.nowTimestamp();

	    Map < String, Object > result = new HashMap < String, Object > ();

	    Map < String, Object > resultContext = FastMap.newInstance();
	    Map < String, Object > callCxt = FastMap.newInstance();
	    Map < String, Object > callResult = FastMap.newInstance();
	    List < GenericValue > agreementRoles = null;
	    try {
	    	String globalDateFormat = org.fio.homeapps.util.DataHelper.getGlobalDateFormat(delegator);
	    	String globalDateTimeFormat = org.fio.homeapps.util.DataHelper.getGlobalDateTimeFormat(delegator);
	    	
	        agreementRoles = EntityQuery.use(delegator).from("AgreementRole").where("agreementId", domainEntityId, "roleTypeId", "DEALER").cache(false).queryList();
	        if (UtilValidate.isNotEmpty(agreementRoles)) {
	            for (int i = 0; i < agreementRoles.size(); i++) {
	                Map < String, Object > partyDetails = FastMap.newInstance();
	                GenericValue agreementRole = agreementRoles.get(i);
	                
	                String partyId = agreementRole.getString("partyId");
	                String customerId = agreementRole.getString("partyId");
	                String custId = DataUtil.getPartyIdentificationValue(delegator, partyId, "ALT_DEAL_CUST_ID");
	                if (UtilValidate.isNotEmpty(custId)) {
	                	customerId = custId;
	                }
	                
	                String partyName = "";
	                partyName = PartyHelper.getPartyName(delegator, partyId, false);
	                String fullAddress = "";
	                String address1 = "";
	                String address2 = "";
	                String city = "";
	                String state = "";
	                String country = "";
	                String postalCodeExt = "";
	                String postalCode = "";
	                List partyContactMechValueMaps = ContactMechWorker.getPartyContactMechValueMaps(delegator, partyId, false);
	                if (UtilValidate.isNotEmpty(partyContactMechValueMaps)) {
	                    String phoneContactId = "";
	                    String emailContactId = "";
	                    String postalContactId = "";
	                    for (int j = 0; j < partyContactMechValueMaps.size(); j++) {
	                        Map < String, Object > contactMechDet = (Map < String, Object > ) partyContactMechValueMaps.get(j);
	                        GenericValue contactMech = (GenericValue) contactMechDet.get("contactMech");
	                        if ("TELECOM_NUMBER".equals(contactMech.get("contactMechTypeId"))) {
	                            phoneContactId = contactMech.getString("contactMechId");
	                        }
	                        if ("EMAIL_ADDRESS".equals(contactMech.get("contactMechTypeId"))) {
	                            emailContactId = contactMech.getString("contactMechId");
	                        }
	                        if ("POSTAL_ADDRESS".equals(contactMech.get("contactMechTypeId"))) {
	                            postalContactId = contactMech.getString("contactMechId");
	                        }
	                    }
	                    if (UtilValidate.isNotEmpty(postalContactId)) {
	                        //callResult = ContactMechWorker.getPartyPostalAddresses(request, partyId, postalContactId);
	                        GenericValue postalAddress = delegator.findOne("PostalAddress", UtilMisc.toMap("contactMechId", postalContactId), false);
	                        if (UtilValidate.isNotEmpty(postalAddress)) {
	                            //GenericValue postalAdddres = (GenericValue)callResult.get("postalAddress");
	                            if (UtilValidate.isNotEmpty(postalAddress)) {
	                                address1 = postalAddress.getString("address1");
	                                address2 = postalAddress.getString("address1");
	                                postalCode = postalAddress.getString("postalCode");
	                                postalCodeExt = postalAddress.getString("postalCodeExt");
	                                city = postalAddress.getString("city");
	                                state = postalAddress.getString("stateProvinceGeoId");
	                                country = postalAddress.getString("countyGeoId");
	                            }

	                        }
	                    }
	                    if (UtilValidate.isNotEmpty(address1)) {
	                        fullAddress = address1;
	                    }
	                    if (UtilValidate.isNotEmpty(city)) {
	                        fullAddress = fullAddress + "," + city;
	                    }
	                    if (UtilValidate.isNotEmpty(city)) {
	                        fullAddress = fullAddress + "," + city;
	                    }
	                    if (UtilValidate.isNotEmpty(state)) {
	                        fullAddress = fullAddress + "," + state;
	                    }
	                    if (UtilValidate.isNotEmpty(country)) {
	                        fullAddress = fullAddress + "," + country;
	                    }
	                    if (UtilValidate.isNotEmpty(postalCode)) {
	                        fullAddress = fullAddress + "," + postalCode;
	                        if (UtilValidate.isNotEmpty(postalCodeExt)) {
	                            fullAddress = fullAddress + "-" + postalCodeExt;
	                        }
	                    }
	                }
	                partyDetails.put("customerId", customerId);
	                partyDetails.put("partyId", partyId);
	                partyDetails.put("partyName", partyName);
	                partyDetails.put("fullAddress", fullAddress);
	                
	                String fromDate = "";
	                String thruDate = "";
	                if (UtilValidate.isNotEmpty(agreementRole.getTimestamp("fromDate"))) {
	                	fromDate = UtilDateTime.timeStampToString(agreementRole.getTimestamp("fromDate"), globalDateFormat, TimeZone.getDefault(), Locale.getDefault());
	                }
	                if (UtilValidate.isNotEmpty(agreementRole.getTimestamp("thruDate"))) {
	                	thruDate = UtilDateTime.timeStampToString(agreementRole.getTimestamp("thruDate"), globalDateFormat, TimeZone.getDefault(), Locale.getDefault());
	                }
	                partyDetails.put("fromDate", fromDate);
	                partyDetails.put("thruDate", thruDate);
	                
	                callCxt.put(partyId, partyDetails);
	            }
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        Debug.logError(e.getMessage(), MODULE);
	        result.putAll(ServiceUtil.returnError(e.getMessage()));
	        return result;
	    }
	    result.put("resultContext", callCxt);
	    result.putAll(ServiceUtil.returnSuccess("Successfull"));
	    return result;
	}
	public static String wrapRoofTopPostalAddress(Delegator delegator, Map<String,Object> context) {

		//Delegator delegator = (Delegator) dctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String domainEntityId = (String) context.get("domainEntityId");
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();

		Map<String, Object> result = new HashMap<String, Object>();

		Map<String, Object> callResult = FastMap.newInstance();
		Map<String, Object> addressDet = FastMap.newInstance();
		List<GenericValue> partyIds = null;
		StringBuilder addressBuilder = new StringBuilder();
		String addressInfo = "";
		try {
			
			addressBuilder.append("<tr>");
			addressBuilder.append("<td></td>");
			addressBuilder.append("</tr>");
			
			if (UtilValidate.isNotEmpty(domainEntityId)) {
				callResult=getRoofTopPostalAddress(delegator, UtilMisc.toMap("domainEntityId", domainEntityId));
				if (ServiceUtil.isSuccess(callResult)) {
					addressDet=(Map<String, Object>) callResult.get("resultContext");
					if (UtilValidate.isNotEmpty(addressDet)) {
						 Set<Entry<String, Object>> addressSet = addressDet.entrySet();
			                Iterator<Entry<String, Object>> addItr = addressSet.iterator();
			                int count = 0;
			                while (addItr.hasNext()) {
			                    Entry<String, Object> field = addItr.next();
			                    Map<String,Object> fieldDet = (Map<String, Object>) field.getValue();
			                    if (UtilValidate.isNotEmpty(fieldDet)) {
			                    	String partyName= (String)fieldDet.get("partyName");
			                    	String address= (String)fieldDet.get("fullAddress");
			                    	addressBuilder.append("<tr>");
			            			addressBuilder.append("<td>"+partyName+"</td>");
			            			addressBuilder.append("<td>' : '</td>");
			            			addressBuilder.append("<td>"+address+"</td>");
			            			addressBuilder.append("</tr>");
			            			addressBuilder.append("<tr>");
			            			addressBuilder.append("<td></td>");
			            			addressBuilder.append("<td></td>");
			            			addressBuilder.append("<td></td>");
			            			addressBuilder.append("</tr>");
			                    }
			                }
					}else {
						addressBuilder.append("<tr>");
            			addressBuilder.append("<td>No Delears Found .</td>");
            			addressBuilder.append("<td></td>");
            			addressBuilder.append("<td></td>");
            			addressBuilder.append("</tr>");
            			
					}
				}else {
						addressBuilder.append("<tr>");
						addressBuilder.append("<td>No Delears Found .</td>");
            			addressBuilder.append("<td></td>");
            			addressBuilder.append("<td></td>");
            			addressBuilder.append("</tr>");
            			
				}
			}
			addressInfo=addressBuilder.toString();
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
			return addressInfo;
		}
		return addressInfo;
	}
	
	public static String prepareAgreementTierData(Delegator delegator, Map<String, Object> context) {
		String dataContent = "";
		
		try {
			String agreementItemSeqId = (String) context.get("agreementItemSeqId");
			String agreementId = (String) context.get("agreementId");
			String agreementItemTypeId = (String) context.get("agreementItemTypeId");
			String amtType = (String) context.get("amountType");
			String contractText = (String) context.get("contractText");
			
			if (UtilValidate.isNotEmpty(agreementId) && UtilValidate.isNotEmpty(agreementItemSeqId)) {
				String programName = UtilAttribute.getAgreementItemAttrValue(delegator, agreementId, agreementItemSeqId, "ITEM_NAME");
				programName = UtilValidate.isNotEmpty(programName) ? programName : "";
				contractText = UtilValidate.isNotEmpty(contractText) ? contractText : "";
				
				List termconditions = FastList.newInstance();
				termconditions.add(EntityCondition.makeCondition("agreementId", EntityOperator.EQUALS, agreementId));
				termconditions.add(EntityCondition.makeCondition("agreementItemSeqId", EntityOperator.EQUALS, agreementItemSeqId));

				EntityCondition mainTermConditons = EntityCondition.makeCondition(termconditions, EntityOperator.AND);
				List<GenericValue> termList = EntityQuery.use(delegator).from("AgreementTerm").where(mainTermConditons).queryList();
				if (UtilValidate.isNotEmpty(termList)) {
					String header1Name = "";
					if (agreementItemTypeId.equals("CSR")) {
						header1Name = "Purchase Tier";
					} else if (agreementItemTypeId.equals("ADDITIONAL_REBATE")) {
						header1Name = "Additional Tier";
					} else if (agreementItemTypeId.equals("VOLUME_REBATE")) {
						header1Name = "Volume Tier";
					}
					
					String amountType = "%";
					if (UtilValidate.isNotEmpty(amtType) && amtType.equals("AMOUNT")) {
						amountType = "$";
					}
					
					dataContent += "<table align=\"left\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"deviceWidth mob-col\" width=\"100%\" style=\"border: 1px solid black;border-collapse: collapse;margin-right: 10px; margin-bottom: 10px;\">";
					dataContent += "<tr>";
					dataContent += "<th width='30%' style=\"border: 1px solid black;border-collapse: collapse;text-align: right;padding: 2px 5px 2px 5px;font-size: 14px;font-weight: normal;\">"+header1Name+"</th>";
					dataContent += "<th width='20%' style=\"border: 1px solid black;border-collapse: collapse;text-align: right;padding: 2px 5px 2px 5px;font-size: 14px;font-weight: normal;\">Rebate "+amountType+"</th>";
					if (UtilValidate.isNotEmpty(contractText)) {
						dataContent += "<th width='50%' style=\"border: 1px solid black;border-collapse: collapse;text-align: center;padding: 2px 5px 2px 5px;font-size: 14px;font-weight: normal;\">Contract Description</th>";
					}else {
						
					}
					dataContent += "</tr>";
					 int count = 0;
					for (GenericValue agmtTerm: termList) {
						count++;
						String minAmt = "";
						String maxAmt = "";
						String rebateAmount = "";
						if (UtilValidate.isNotEmpty(agmtTerm.get("minQuantity"))) {
							minAmt = org.groupfio.common.portal.util.DataHelper.getFormattedNumValue(delegator, new BigDecimal(agmtTerm.getDouble("minQuantity")).setScale(2, RoundingMode.HALF_DOWN).toString());
						}
						if (UtilValidate.isNotEmpty(agmtTerm.get("maxQuantity"))) {
							maxAmt = org.groupfio.common.portal.util.DataHelper.getFormattedNumValue(delegator, new BigDecimal(agmtTerm.getDouble("maxQuantity")).setScale(2, RoundingMode.HALF_DOWN).toString());
						}
						if (UtilValidate.isNotEmpty(maxAmt) && maxAmt.equalsIgnoreCase("99999999") || maxAmt.equalsIgnoreCase("99,999,999")) {
							maxAmt = " and >";
						}else {
							maxAmt = " - $"+maxAmt;
						}
						if (UtilValidate.isNotEmpty(agmtTerm.getString("rebateAmount"))) {
							rebateAmount = org.groupfio.common.portal.util.DataHelper.getFormattedNumValue(delegator, agmtTerm.getString("rebateAmount"), "###,##0.00");
						}
						
						dataContent += "<tr>";
						dataContent += " <td width='30%' style=\"border: 1px solid black;border-collapse: collapse; padding: 5px 5px 2px 5px; text-align: right;font-size: 14px;\">"+"$"+ minAmt  + "-"+ "$ "+ maxAmt +"</td>";
						dataContent += " <td width='20%' style=\"border: 1px solid black;border-collapse: collapse; padding: 5px 5px 2px 5px; text-align: right;font-size: 14px;\">"+rebateAmount+amountType+"</td>";
						if (UtilValidate.isNotEmpty(contractText) && count==1 ) {
							dataContent += "<th width='50%' rowspan='"+(termList.size())+"' style='vertical-align: top;padding-left: 5px;'>"+contractText+"</th>";
						}
						dataContent +=" </tr>";
					}
					dataContent +="</table>";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return dataContent;
	}
	
	public static List<String> getAllDealerByContact(Delegator delegator, String contactPartyId){
		List<String> partyIds = new ArrayList<String>();
		try {
			if(UtilValidate.isNotEmpty(contactPartyId)) {
				EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, contactPartyId),
						EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "CONTACT"),
						EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "ACCOUNT"),
						EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "CONTACT_REL_INV")
						);
				List<GenericValue> partyRelationshipList = EntityQuery.use(delegator).select("partyIdTo").from("PartyRelationship").where(condition).filterByDate().queryList();
				if(UtilValidate.isNotEmpty(partyRelationshipList))
					partyIds = EntityUtil.getFieldListFromEntityList(partyRelationshipList, "partyIdTo", true);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return partyIds;
	}
	
	public static String getPrimaryPerson(Delegator delegator, String custRequestId) {
		String primaryPartyId = "";
		try {
			GenericValue primaryData = EntityQuery.use(delegator).from("CustRequestAttribute").where("custRequestId", custRequestId, "attrName", "PRIMARY").queryFirst();
			if(UtilValidate.isNotEmpty(primaryData)){
				String primary =primaryData.getString("attrValue");
				
				Map<String, Object> anchorPartyMap = SrUtil.getCustRequestAnchorParties(delegator, custRequestId);
				if(UtilValidate.isNotEmpty(anchorPartyMap)) {
					if("DEALER".equals(primary)) {
						String accountId = (String) anchorPartyMap.get("ACCOUNT");
						String primaryContactId = (String) anchorPartyMap.get("CONTACT");
						if(UtilValidate.isNotEmpty(primaryContactId)) 
							primaryPartyId = primaryContactId;
						else 
							primaryPartyId = accountId;
						
					} else if("CONTRACTOR".equals(primary)) {
						primaryPartyId = (String) anchorPartyMap.get("CONTRACTOR");
					} else if("HOME".equals(primary)) {
						primaryPartyId = (String) anchorPartyMap.get("CUSTOMER");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return primaryPartyId;
	}
	
	public static Map<String, Object> prepareProgramConfifguration (Delegator delegator, Map<String, Object> filter) {
		Map<String, Object> detail = new LinkedHashMap<>();
		try {
			if (UtilValidate.isNotEmpty(filter)) {
				String globalDateFormat = org.fio.homeapps.util.DataHelper.getGlobalDateFormat(delegator);
				
				String tplProgConfig = (String) filter.get("tplProgConfig");
				Timestamp actualStartDate = (Timestamp) filter.get("actualStartDate");
				Integer daysRequired = (Integer) filter.get("daysRequired");
				
				List<Map> tplConfigDataList = org.fio.homeapps.util.ParamUtil.jsonToList(tplProgConfig);
				
				List<Map> configDataList = new ArrayList();
				for (Map tplConfigData : tplConfigDataList) {
					Map configData = new LinkedHashMap<>();
					configData.put("groupId", tplConfigData.get("groupId"));
					configData.put("groupName", tplConfigData.get("groupName"));
					
					List<Map> tplFieldDetailList = (List<Map>) tplConfigData.get("fieldDetail");
					List<Map> fieldDetailList = new ArrayList<>();
					
					Timestamp actFromDate = actualStartDate;
					int count = 0;
					while(count < daysRequired) {
						Map fieldDetail = new LinkedHashMap();
						List<Map> fields = new ArrayList<>();
						
						fieldDetail.put("activityDate", UtilDateTime.timeStampToString(actFromDate, globalDateFormat, TimeZone.getDefault(), Locale.getDefault()));
						
						if (UtilValidate.isNotEmpty(tplFieldDetailList) && tplFieldDetailList.size() > 0) {
							if (count < tplFieldDetailList.size()) {
								Map tplFieldDetail = tplFieldDetailList.get(count);
								fields = (List<Map>) tplFieldDetail.get("fields");
							} else {
								Map tplFieldDetail = tplFieldDetailList.get(0);
								fields = (List<Map>) tplFieldDetail.get("fields");
							}
						}
							
						fieldDetail.put("fields", fields);
						actFromDate = UtilDateTime.addDaysToTimestamp(actFromDate, 1);
						count++;
						fieldDetailList.add(fieldDetail);
					}
					configData.put("fieldDetail", fieldDetailList);
					
					configDataList.add(configData);
				}
				String programConfiguration = ParamUtil.toJson(configDataList);
				detail.put("programConfiguration", programConfiguration);
				System.out.println("programConfiguration> "+programConfiguration);
			}
			
		} catch (Exception e) {
			Debug.logError(e, MODULE);
		}
		return detail;
	}

	
	public static Map<String, Object> calculateDate(Map<String, Object> context){
		Delegator delegator = (Delegator) context.get("delegator");
		Timestamp businessStartDate = (Timestamp) context.get("businessStartDate"); 
		long inHours = context.get("inHours") != null ? (long) context.get("inHours") : 0l; 
		long inMinutes = context.get("inMinutes") != null ? (long) context.get("inMinutes") : 0l; 
		LocalTime businessStartTime = (LocalTime) context.get("startTime");
		LocalTime businessEndTime = (LocalTime) context.get("endTime"); 
		String action = (String) context.get("action");
    	Map<String, Object> result = new HashMap<String, Object>();
    	try {
    		@SuppressWarnings("unchecked")
			List<LocalDate> holidayList =  (List<LocalDate>) context.get("holidayList");
    		if(UtilValidate.isNotEmpty(businessStartDate) && (UtilValidate.isNotEmpty(inHours) || UtilValidate.isNotEmpty(inMinutes))) {
    			LocalDateTime startDateTime = businessStartDate.toLocalDateTime().truncatedTo(ChronoUnit.MINUTES);
    			if(UtilValidate.isEmpty(businessStartTime))
    	    		businessStartTime = LocalTime.of(9, 00);
    	    	if(UtilValidate.isEmpty(businessEndTime))
    	    		businessEndTime = LocalTime.of(18, 00);
    	    	LocalDateTime startOfDay = startDateTime.with(businessStartTime);
    	    	LocalDateTime endOfDay = startDateTime.with(businessEndTime);
    	    	
    	    	// calculate total working hours and minutes
        		LocalDateTime workingDay = LocalDateTime.from(startOfDay);
        		long totalWorkingHours = workingDay.until(endOfDay, ChronoUnit.HOURS);
        		workingDay = workingDay.plusHours(totalWorkingHours);
        		long totalWorkingMinutes = workingDay.until( endOfDay, ChronoUnit.MINUTES );
            	
        		boolean flag = true;
        		int slaHours = (int) inHours;
        		int slaMinutes = (int) inMinutes;
        		if(slaMinutes>0 && slaMinutes == SlaSetupConstants.TOTAL_MINUTES) {
        			slaHours = slaHours+1; slaMinutes=0;
        		}
        		String defaultLoopLimit = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "DEFAULT_LOOP_LIMIT");
        		int loopLimit = UtilValidate.isNotEmpty(defaultLoopLimit) && ParamUtil.isInteger(defaultLoopLimit) ? Integer.parseInt(defaultLoopLimit) : 100;
        		int count = 0;
    			while(flag) {
    				LocalDateTime tempDateTime = LocalDateTime.from(startDateTime);
    	            long hours = tempDateTime.until( endOfDay, ChronoUnit.HOURS );
    	            tempDateTime = tempDateTime.plusHours( hours );
    	            long minutes = tempDateTime.until( endOfDay, ChronoUnit.MINUTES );
                	if(hours > 0 || minutes > 0) {
                		if(slaHours >= hours)
                			slaHours = (int) (slaHours - hours);
                		
                 		if(slaHours>0 && minutes >0)
                 			slaMinutes = (int) (slaMinutes-minutes);
                 		
                 		if(slaMinutes < 0) {
                 			slaHours = slaHours -1;
                 			slaMinutes = SlaSetupConstants.TOTAL_MINUTES+slaMinutes;
                 		}
                 		
                 		if((inHours >= 0 && inHours <= hours && inMinutes <= minutes)){
                    		startDateTime = startDateTime.plusHours(inHours).plusMinutes(inMinutes);			
                    		flag = false;
                    	} else if(slaHours > totalWorkingHours || (slaHours == totalWorkingHours && slaMinutes > 0)) {
                			startDateTime = startDateTime.plusDays(1).with(businessStartTime);
                    	} else {
                    		startDateTime = startDateTime.plusDays(1).with(businessStartTime);
                    		businessStartTime = businessStartTime.plusHours(slaHours).plusMinutes(slaMinutes);
                    		flag = false;
                    	}
                 		Map<String, Object> validateMap = new HashMap<String, Object>();
                 		validateMap.put("startDateTime", startDateTime);
                 		validateMap.put("businessStartTime", businessStartTime);
                 		validateMap.put("businessEndTime", businessEndTime);
                 		validateMap.put("flag", flag);
                 		validateMap.put("holidayList", holidayList);
                 		//System.out.println("validateMap---->"+validateMap);
                		Map<String, Object>  businessDateResult = DataHelper.getBusinessDate(validateMap);
                        if(UtilValidate.isNotEmpty(businessDateResult)) {
                        	startDateTime =  (LocalDateTime) businessDateResult.get("startDateTime");
                        	startOfDay =  (LocalDateTime) businessDateResult.get("startOfDay");
                        	endOfDay =  (LocalDateTime) businessDateResult.get("endOfDay");
                        }
                	} else {
                		flag= false;
                	}
                	if(count > loopLimit) break;
                	count = count +1;
    			}
    			result.put("finalDate", startDateTime);
    		}
    	} catch (Exception e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);
		}
    	return result;
    }
    public static List<LocalDate> getHolidays(Map<String, Object> context){
    	Delegator delegator = (Delegator) context.get("delegator"); 
    	String businessUnit = (String) context.get("businessUnit");
    	List<LocalDate> holidays = new ArrayList<>();
    	try {
    		List<EntityCondition> conditionList = FastList.newInstance();
			conditionList.add(EntityCondition.makeCondition(EntityOperator.OR,
							EntityCondition.makeCondition("status", EntityOperator.EQUALS, null),
							EntityCondition.makeCondition("status", EntityOperator.EQUALS, "ACTIVE"))
	                	);
			
			//EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			List<GenericValue> holidayConfigList = EntityQuery.use(delegator).from("TechDataHolidayConfig").where(EntityCondition.makeCondition(conditionList, EntityOperator.AND)).distinct(true).cache(false).queryList();
	    	if(UtilValidate.isNotEmpty(holidayConfigList)) {
	    		for(GenericValue holidayConfig : holidayConfigList) {
	    			//Timestamp holidayDate = UtilDateTime.toTimestamp(holidayConfig.getDate("holidayDate"));
	    			java.sql.Date holidayDate = holidayConfig.getDate("holidayDate");
	    			holidays.add(holidayDate.toLocalDate());
	    		}
	    	}
    	} catch (Exception e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);
		}
		return holidays;
    }
    
    @SuppressWarnings("unchecked")
	public static Map<String, Object> getBusinessDate(Map<String, Object> context) {
	    Delegator delegator = (Delegator) context.get("delegator");
	    LocalDateTime startDateTime = (LocalDateTime) context.get("startDateTime");
	    LocalTime businessStartTime = (LocalTime) context.get("businessStartTime");
	    LocalTime businessEndTime = (LocalTime) context.get("businessEndTime");
	    String businessUnit = (String) context.get("businessUnit");
	    boolean flag = UtilValidate.isNotEmpty(context.get("flag")) ? (boolean) context.get("flag") : true;
	    List<LocalDate> holidays =  (List<LocalDate>) context.get("holidayList");
	    Optional<List<LocalDate>> holidayList = Optional.of(holidays);
    	Map<String, Object> result = new HashMap<String, Object>();
    	try {
    		if(startDateTime != null) {
    	    	if(businessStartTime == null)
    	    		businessStartTime = LocalTime.of(9, 00);
    	    	if(businessEndTime == null)
    	    		businessEndTime = LocalTime.of(18, 00);
    	    	LocalDateTime startOfDay = startDateTime.with(businessStartTime);
    	    	LocalDateTime endOfDay = startDateTime.with(businessEndTime);
    	    	
                //Optional<List<LocalDate>> holidayList = getHolidays(context);
                //Debug.log("holiday list--->"+holidayList, MODULE);
                //if we add hours we can calculate straight, if it's days then have to convert one day to hours and minutes
                Predicate<LocalDate> isHoliday = date -> holidayList.isPresent() ? holidayList.get().contains(date) : false;
                
                //Predicate<LocalDate> isWeekend = date -> date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY;

                String defaultLoopLimit = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "DEFAULT_LOOP_LIMIT");
        		int loopLimit = UtilValidate.isNotEmpty(defaultLoopLimit) && org.fio.admin.portal.util.DataUtil.isInteger(defaultLoopLimit) ? Integer.parseInt(defaultLoopLimit) : 100;
        		int count = 0;
                boolean validateCreateDate = true;
                while(validateCreateDate) {
                	long isItHoliday = Stream.iterate(startDateTime.toLocalDate(), date -> date.plusDays(1)).limit(1)
                            .filter(isHoliday).count();
                	
                	if(isItHoliday > 0 || startDateTime.isAfter(endOfDay) || (flag && startDateTime.isEqual(endOfDay))) {
                		startDateTime = startDateTime.plusDays(1).with(businessStartTime);
                    	startOfDay = startDateTime.with(businessStartTime);
                    	endOfDay = startDateTime.with(businessEndTime);
                    } else if(!flag && startDateTime.isEqual(endOfDay)) {
                    	validateCreateDate = false;
                    	if(startDateTime.isBefore(startOfDay))
                    		startDateTime = startDateTime.with(businessStartTime);
                    } else {
                    	validateCreateDate = false;
                    	if(startDateTime.isBefore(startOfDay))
                    		startDateTime = startDateTime.with(businessStartTime);
                    }
                	if(count > loopLimit) break;
                	count = count+1;
                }
                result.put("startDateTime", startDateTime);
                result.put("startOfDay", startOfDay);
                result.put("endOfDay", endOfDay);
    		}
    	} catch (Exception e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);

		}
    	return result;
    }
    
    public static long countBusinessDays(Map<String, Object> context) {
    	long businessDays = 0l;
    	LocalDateTime startDate = (LocalDateTime) context.get("createdDateTime");
    	LocalDateTime endDate = (LocalDateTime) context.get("closedDateTime");
		try {
			List<LocalDate> holidays =  (List<LocalDate>) getHolidays(context);
		    Optional<List<LocalDate>> holidayList = Optional.of(holidays);
	        
	        if (startDate == null || endDate == null) {
	            throw new IllegalArgumentException("Invalid method argument(s) to countBusinessDaysBetween(" + startDate
	                    + "," + endDate + ")");
	        }
	 
	        Predicate<LocalDate> isHoliday = date -> holidayList.isPresent() ? holidayList.get().contains(date) : false;
	        //Predicate<LocalDate> isWeekend = date -> date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY;
	 
	        long daysBetween = ChronoUnit.DAYS.between(startDate.toLocalDate(), endDate.toLocalDate());
	        
	        businessDays = Stream.iterate(startDate.toLocalDate(), date -> date.plusDays(1)).limit(daysBetween)
	                .filter(isHoliday.negate()).count();
		} catch (Exception e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);

		}
        return businessDays;
    }
    
    @SuppressWarnings("unchecked")
	public static Map<String, Object> getClosedWorkingDate(Map<String, Object> context) {
	    
    	Delegator delegator = (Delegator) context.get("delegator");
 	    LocalDateTime startDateTime = (LocalDateTime) context.get("startDateTime");
 	    LocalTime businessStartTime = (LocalTime) context.get("businessStartTime");
 	    LocalTime businessEndTime = (LocalTime) context.get("businessEndTime");
 	    String businessUnit = (String) context.get("businessUnit");
 	    boolean flag = UtilValidate.isNotEmpty(context.get("flag")) ? (boolean) context.get("flag") : true;
 	    List<LocalDate> holidays =  (List<LocalDate>) context.get("holidayList");
 	    Optional<List<LocalDate>> holidayList = Optional.of(holidays);
 	    int interval = (int) context.get("interval");
	    
    	Map<String, Object> result = new HashMap<String, Object>();
    	try {
    		if(startDateTime != null) {
    	    	if(businessStartTime == null)
    	    		businessStartTime = LocalTime.of(9, 00);
    	    	if(businessEndTime == null)
    	    		businessEndTime = LocalTime.of(18, 00);
    	    	LocalDateTime startOfDay = startDateTime.with(businessStartTime);
    	    	LocalDateTime endOfDay = startDateTime.with(businessEndTime);
    	    	
                //Optional<List<LocalDate>> holidayList = getHolidays(context);
                //Debug.log("holiday list--->"+holidayList, MODULE);
                //if we add hours we can calculate straight, if it's days then have to convert one day to hours and minutes
                Predicate<LocalDate> isHoliday = date -> holidayList.isPresent() ? holidayList.get().contains(date) : false;
                
                //Predicate<LocalDate> isWeekend = date -> date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY;

                
                String defaultLoopLimit = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "DEFAULT_LOOP_LIMIT");
        		int loopLimit = UtilValidate.isNotEmpty(defaultLoopLimit) && org.fio.admin.portal.util.DataUtil.isInteger(defaultLoopLimit) ? Integer.parseInt(defaultLoopLimit) : 100;
        		int count = 0;
                boolean validateCreateDate = true;
                while(validateCreateDate) {
					long isItHoliday = Stream.iterate(startDateTime.toLocalDate(), date -> date.plusDays(1)).limit(1).filter(isHoliday).count();

					if(isItHoliday > 0 || startDateTime.isBefore(startOfDay)) {
						startDateTime = startDateTime.plusDays(interval).with(businessEndTime);
						startOfDay = startDateTime.with(businessStartTime);
						endOfDay = startDateTime.with(businessEndTime);
					} else if(startDateTime.isEqual(endOfDay)) {
						validateCreateDate = false;
						//startDateTime = startDateTime.with(businessEndTime);
					} else {
						validateCreateDate = false;
						if(startDateTime.isAfter(endOfDay))
							startDateTime = startDateTime.with(businessEndTime);
					}
					if(count > loopLimit) break;
					count = count+1;
				}
                result.put("startDateTime", startDateTime);
                result.put("startOfDay", startOfDay);
                result.put("endOfDay", endOfDay);
    		}
    	} catch (Exception e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);

		}
    	return result;
    }
    
    @SuppressWarnings("unchecked")
	public static Map<String, Object> getPreEscalWorkingDate(Map<String, Object> context) {

    	Delegator delegator = (Delegator) context.get("delegator");
		LocalDateTime startDateTime = (LocalDateTime) context.get("startDateTime");
		LocalTime businessStartTime = (LocalTime) context.get("startTime");
		LocalTime businessEndTime = (LocalTime) context.get("endTime");
		LocalTime remainTime = (LocalTime) context.get("remainTime");
		String businessUnit = (String) context.get("businessUnit");
		boolean flag = context.get("flag") != null ? (boolean) context.get("flag") : true;
		int interval = (int) context.get("interval");
		List<LocalDate> holidays =  (List<LocalDate>) context.get("holidayList");
		Optional<List<LocalDate>> holidayList = Optional.of(holidays);

		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if(UtilValidate.isNotEmpty(startDateTime)) {
				if(businessStartTime == null)
					businessStartTime = LocalTime.of(9, 00);
				if(businessEndTime == null)
					businessEndTime = LocalTime.of(18, 00);
				LocalDateTime startOfDay = startDateTime.with(businessStartTime);
				LocalDateTime endOfDay = startDateTime.with(businessEndTime);

				Predicate<LocalDate> isHoliday = date -> holidayList.isPresent() ? holidayList.get().contains(date) : false;

				//Predicate<LocalDate> isWeekend = date -> date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY;

				String defaultLoopLimit = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "DEFAULT_LOOP_LIMIT");
        		int loopLimit = UtilValidate.isNotEmpty(defaultLoopLimit) && org.fio.admin.portal.util.DataUtil.isInteger(defaultLoopLimit) ? Integer.parseInt(defaultLoopLimit) : 100;
        		int count = 0;
				boolean validateCreateDate = true;
				while(validateCreateDate) {
					long isItHoliday = Stream.iterate(startDateTime.toLocalDate(), date -> date.plusDays(1)).limit(1)
							.filter(isHoliday).count();

					if(isItHoliday > 0 || (startDateTime.isBefore(startOfDay) || startDateTime.isAfter(endOfDay))) {
						startDateTime = startDateTime.plusDays(interval).with(remainTime);
						startOfDay = startDateTime.with(businessStartTime);
						endOfDay = startDateTime.with(businessEndTime);
					} else if(!flag && startDateTime.isEqual(endOfDay)) {
						validateCreateDate = false;
						if(startDateTime.isAfter(startOfDay))
							startDateTime = startDateTime.with(remainTime);
					} else {
						validateCreateDate = false;
					}
					if(count > loopLimit) break;
					count = count+1;
				}
				result.put("startDateTime", startDateTime);
				result.put("startOfDay", startOfDay);
				result.put("endOfDay", endOfDay);
			}
		} catch (Exception e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);

		}
		return result;
	}
	
    public static Map<String, Object> getTotalHours(String slaPeriod, String slaPeriodValue, long hours, long minutes) {
    	Map<String, Object> result = new HashMap<String, Object>();
    	long totalHours = 0l;
    	long remainMinutes = 0l;
    	try {
			if("DAYS".equalsIgnoreCase(slaPeriod)) {
				long slaHours = 0l;
				if(UtilValidate.isNotEmpty(slaPeriodValue) && org.fio.admin.portal.util.DataUtil.isInteger(slaPeriodValue)) {
					slaHours = Long.parseLong(slaPeriodValue);
				}
				totalHours = slaHours * hours;
				long totalMinutes = slaHours * minutes;
				
				int minuteHours = (int) (totalMinutes / 60);
				remainMinutes = (int) (totalMinutes % 60);
				totalHours = totalHours + minuteHours;
			} else if("HOURS".equalsIgnoreCase(slaPeriod)) {
				totalHours = UtilValidate.isNotEmpty(slaPeriodValue) && org.fio.admin.portal.util.DataUtil.isInteger(slaPeriodValue) ? Long.parseLong(slaPeriodValue) : 0l;
			}
    	} catch (Exception e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);

		}
    	result.put("totalHours", totalHours);
    	result.put("remainMinutes", remainMinutes);
    	return result;
    }
    
    
    public static String getOppoRoleEmails(Delegator delegator, String salesOpportunityId, String roles) {
    	String toAddress = "";
    	try {
    		if(UtilValidate.isNotEmpty(roles)) {
    			List< String > roleList = new ArrayList<String>();
				if(UtilValidate.isNotEmpty(roles) && roles.contains(",")) {
					roleList = org.fio.admin.portal.util.DataUtil.stringToList(roles, ",");
				} else
					roleList.add(roles);
				GenericValue opportunityGv = EntityQuery.use(delegator).from("SalesOpportunity").where("salesOpportunityId", salesOpportunityId).queryFirst();
				String ownerId = UtilValidate.isNotEmpty(opportunityGv) ? opportunityGv.getString("ownerId") : "";
				List<String> partyIdList = new LinkedList<String>();
				if(UtilValidate.isNotEmpty(ownerId)) {
					String ownerPartyId = org.fio.homeapps.util.DataUtil.getUserLoginPartyId(delegator, ownerId);
					if(roleList.contains("TEAM_LEAD")) {
						List<GenericValue> emplPositionFulfillmentList = null;
						Map<String, Object> buTeamData = org.fio.homeapps.util.DataUtil.getUserBuTeam(delegator, ownerId);
				  		if (UtilValidate.isNotEmpty(buTeamData) && UtilValidate.isNotEmpty(buTeamData.get("emplTeamId"))) {    
				  			String emplTeamId = (String) buTeamData.get("emplTeamId");
				  			String ownerBu = (String) buTeamData.get("businessUnit");
				  			emplPositionFulfillmentList = EntityQuery.use(delegator).from("EmplPositionFulfillment").where("emplTeamId", emplTeamId,"isTeamLead","Y").filterByDate().queryList();
				  		}
				  		
				  		if(UtilValidate.isNotEmpty(emplPositionFulfillmentList)) {
				  			for(GenericValue emplPositionFulfillment : emplPositionFulfillmentList ) {
				  				String partyId = emplPositionFulfillment.getString("partyId");
				  				
				  				partyIdList.add(partyId);
				  			}
				  		}
				  		
					}
					EntityCondition oppoRoleConditions = EntityCondition.makeCondition(EntityOperator.AND,
							EntityCondition.makeCondition("salesOpportunityId", EntityOperator.EQUALS, salesOpportunityId),
							EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, roleList)
							);
					List<GenericValue> salesOpportunityRole = EntityQuery.use(delegator).from("SalesOpportunityRole").where(oppoRoleConditions).queryList();
					if(UtilValidate.isNotEmpty(salesOpportunityRole)) {
						List<String> partyIds = EntityUtil.getFieldListFromEntityList(salesOpportunityRole, "partyId", true);
						if(UtilValidate.isNotEmpty(partyIds))
							partyIdList.addAll(partyIds);
					}
					
					if(roleList.contains("OWNER")) {
						partyIdList.add(ownerPartyId);
					} 
					
				}
				List<String> partyEmails = new LinkedList<String>();
				if(UtilValidate.isNotEmpty(partyIdList)) {
					for(String partyId : partyIdList) {
						String emailId = PartyHelper.getEmailAddress(delegator, partyId, "PRIMARY_EMAIL");
						if(UtilValidate.isNotEmpty(emailId))
							partyEmails.add(emailId);
					}
				}
				
				if(UtilValidate.isNotEmpty(partyEmails)){
					toAddress = partyEmails.stream().collect(Collectors.joining(","));
				}
			}
    	} catch (Exception e) {
			e.printStackTrace();
		}
    	return toAddress;
    }

	public static String getErrorMsg(Map<String,Object> responseMap, String responseStr, String defaultMsg) {
		String responseMsg = "";
		try {
			if(UtilValidate.isNotEmpty(responseMap) && responseMap.containsKey("response_code_desc") && UtilValidate.isNotEmpty(responseMap.get("response_code_desc"))) {
				responseMsg = (String) responseMap.get("response_code_desc");
				if(responseMap.containsKey("response_code") && UtilValidate.isNotEmpty(responseMap.get("response_code")) && responseMsg.contains((String)responseMap.get("response_code"))) {
					responseMsg = responseMsg.replace("["+responseMap.get("response_code")+"]", "");
				}
			}else if(UtilValidate.isNotEmpty(responseStr)){
				responseMsg = responseStr;
			}else {
				responseMsg = defaultMsg;
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return responseMsg;
	}

	public static long mod10(String data, int dataLength) {
		long returnValue = -1;
		String dataBuffer;
		int numLen;
		int sum;
		boolean doMult;
		int position;
		int tempSum;
		int tempDigit = -1;

		if (dataLength != 0) {
			dataBuffer = data.substring(0, dataLength);

			if (isNumeric(dataBuffer)) {
				numLen = dataBuffer.length();
				doMult = true;
				sum = 0;

				for (position = (numLen - 1) - 1; position >= 0; position--) {
					tempDigit = Integer.parseInt(dataBuffer.substring(position, position + 1));

					if (doMult) {
						tempSum = 2 * tempDigit;
						if (tempSum < 10) {
							sum += tempSum;
						} else {
							sum += 1;
							sum += tempSum % 10;
						}

						doMult = false;
					} else {
						sum += tempDigit;
						doMult = true;
					}
				}

				if (sum > 0) {
					tempDigit = (((int) (sum / 10)) * 10) + 10;
				}

				tempDigit = tempDigit - sum;

				if (tempDigit == 10) {
					tempDigit = 0;
				}

			}

			returnValue = tempDigit;
		}
		return returnValue;
	}

	public static boolean isNumeric(String input) {
		try {
			Long.parseLong(input);
			return true;
		} catch (Exception e) {
			return false;
		}

	}
	
	@SuppressWarnings("unchecked")
	public static int prepareTatToDate(Map<String, Object> context) {
		Map<String, Object> result = new HashMap<String, Object>();
		Timestamp createdDate =null;
		Timestamp closedDate = (Timestamp) context.get("closedDate");
		String custRequestId = (String) context.get("custRequestId");
		String statusId = (String) context.get("srStatuId");
		Delegator delegator = (Delegator) context.get("delegator");
		int totalDays =0;
		try {
			Map<String,Object> tatByDay = getSrTatCountByHst(context);
			Map<String,Object> tatDaysByHistory = new HashMap<String,Object>();
			if (UtilValidate.isNotEmpty(tatByDay)) {
				tatDaysByHistory = (Map<String, Object>) tatByDay.get("tatDaysByHistory");
			}
			
			if (UtilValidate.isNotEmpty(tatDaysByHistory)) {
				 for (Entry<String, Object> entry: tatDaysByHistory.entrySet()) {
					 int count = (int) entry.getValue();
					 if (UtilValidate.isNotEmpty(totalDays)) {
						 totalDays = totalDays+count;
					 }
				 }
			}
			if (UtilValidate.isNotEmpty(statusId)) {
				String slaTatPauseStatus = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SLA_TAT_PAUSE_STATUS");
				List slaTatPauseStatusList = DataUtil.stringToList(slaTatPauseStatus, ",");
				String slaTatStopStatus = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SLA_TAT_STOP_STATUS");
				List slaTatStopStatusList = DataUtil.stringToList(slaTatStopStatus, ",");
				List slaTatNoCountList = FastList.newInstance();
				if (UtilValidate.isNotEmpty(slaTatPauseStatusList)) {
					slaTatNoCountList.addAll(slaTatPauseStatusList);
				}
				if (UtilValidate.isNotEmpty(slaTatStopStatusList)) {
					slaTatNoCountList.addAll(slaTatStopStatusList);
				}
				if (UtilValidate.isEmpty(slaTatNoCountList) || (UtilValidate.isNotEmpty(slaTatNoCountList) && !slaTatNoCountList.contains(statusId))) {
					GenericValue custRequestHistory = EntityQuery.use(delegator).from("CustRequestHistory").where("custRequestId", custRequestId).orderBy("-createdStamp").queryFirst();
					if (UtilValidate.isNotEmpty(custRequestHistory)) {
						createdDate = custRequestHistory.getTimestamp("createdStamp");
					}
					long days = 0;
					if (UtilValidate.isNotEmpty(createdDate) && UtilValidate.isNotEmpty(closedDate)) {
						Map cxtMap = FastMap.newInstance();
						 cxtMap.put("delegator", delegator);
						 cxtMap.put("createdDateTime", UtilDateTime.getDayStart(createdDate).toLocalDateTime());
						 cxtMap.put("closedDateTime", UtilDateTime.getDayStart(closedDate).toLocalDateTime());
						 days = DataHelper.countBusinessDays(cxtMap);
					}
					if (UtilValidate.isNotEmpty(totalDays) && UtilValidate.isNotEmpty(days)) {
						totalDays = (int) (totalDays+days);
					}
				}
					
			}
		} catch (Exception e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);
		}
		return totalDays;
	}
	
	public static String getSrTatCount(Delegator delegator, String custRequestId) {
		return getSrTatCount(delegator, custRequestId, null);
	}

	public static String getSrTatCount(Delegator delegator, String custRequestId, List holidays) {
		Map<String, Object> pausedDates= (Map<String, Object>) org.groupfio.common.portal.util.DataHelper.getSrTatCountByHst(UtilMisc.toMap("custRequestId",custRequestId,"holidays",holidays,"delegator",delegator)).get("tatDaysByHistory");
		if (UtilValidate.isNotEmpty(pausedDates)) {
			int tatCount = pausedDates.entrySet().stream().filter(e->UtilValidate.isNotEmpty(e.getValue())).map(e->(int)e.getValue()).collect(Collectors.toList()).stream().reduce(0, Integer::sum);
			return ""+tatCount;
		}
		return "";
	}
	
	public static Map<String, Object> getSrTatCountByHst(Map<String, Object> context) {
		Delegator delegator = (Delegator) context.get("delegator");
		LocalDateTime startDateTime = (LocalDateTime) context.get("startDateTime");
		String custRequestId = (String) context.get("custRequestId");
		String isTatReComputeByClosedDate = (String) context.get("isTatReComputeByClosedDate");
		Debug.logInfo("isTatReComputeByClosedDate> "+isTatReComputeByClosedDate, MODULE);
		Map<String, Object> result = new HashMap<String, Object>();
		String slaTatPauseStatus = (String) context.get("slaTatPauseStatus");
		if (UtilValidate.isEmpty(slaTatPauseStatus)) {
			slaTatPauseStatus = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SLA_TAT_PAUSE_STATUS");
		}
		List slaTatPauseStatusList = org.fio.homeapps.util.DataUtil.stringToList(slaTatPauseStatus, ",");
		List<LocalDate> tatPauseDates = FastList.newInstance();
		Map tatDaysByHistory = FastMap.newInstance();
		try {
			List holidays = FastList.newInstance();
			List<EntityCondition> conditionList = FastList.newInstance();
			
			if (UtilValidate.isEmpty(context.get("holidays"))) {
				conditionList = FastList.newInstance();
				conditionList.add(EntityCondition.makeCondition(EntityOperator.OR,
								EntityCondition.makeCondition("status", EntityOperator.EQUALS, null),
								EntityCondition.makeCondition("status", EntityOperator.EQUALS, "ACTIVE"))
		                	);
				List<GenericValue> holidayConfigList = EntityQuery.use(delegator).from("TechDataHolidayConfig").where(EntityCondition.makeCondition(conditionList, EntityOperator.AND)).distinct(true).cache(false).queryList();
		    	if(UtilValidate.isNotEmpty(holidayConfigList)) {
		    		for(GenericValue holidayConfig : holidayConfigList) {
		    			java.sql.Date holidayDate = holidayConfig.getDate("holidayDate");
		    			holidays.add(new Timestamp(holidayDate.getTime()));
		    		}
		    	}
			} else {
				holidays = (List) context.get("holidays");
			}
			
	    	boolean includePrevStatus = false;
	    	
			if (UtilValidate.isNotEmpty(slaTatPauseStatusList)) {
				conditionList = FastList.newInstance();
				conditionList.add(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId));
				EntityCondition cond = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				Set<String> fieldToSelect = new HashSet<String>();
				fieldToSelect.add("custRequestId");
				fieldToSelect.add("statusId");
				fieldToSelect.add("createdDate");
				fieldToSelect.add("closedByDate");
				fieldToSelect.add("createdStamp");
				fieldToSelect.add("custRequestHistoryId");
				
				List<GenericValue> custRequestHistoryList = EntityQuery.use(delegator).from("CustRequestHistory")
						.where("custRequestId", custRequestId).orderBy("createdStamp").queryList();
				context.put("custRequestHistoryList", custRequestHistoryList);
				
				Timestamp prevStatusDate = null;
				String prevStatus="";
				int countTat=0;
				Timestamp reopenedDate = getSrReopenDate(delegator, custRequestId);
				if (UtilValidate.isNotEmpty(reopenedDate)) {
					reopenedDate = UtilDateTime.getDayStart(reopenedDate);
				}
				if (UtilValidate.isNotEmpty(custRequestHistoryList)) {
					Timestamp lastClosedByDate = custRequestHistoryList.get(custRequestHistoryList.size()-1).getTimestamp("closedByDate");
					for (GenericValue history : custRequestHistoryList) {
						String historyId = history.getString("custRequestHistoryId");
						String statusId = history.getString("statusId");
						//Timestamp createdDate = history.getTimestamp("createdDate");
						Timestamp closedByDate = history.getTimestamp("closedByDate");
						Timestamp createdStamp = history.getTimestamp("createdStamp");
						
						if (UtilValidate.isNotEmpty(createdStamp)) {
							createdStamp = UtilDateTime.getDayStart(createdStamp);
							if (UtilValidate.isNotEmpty(isTatReComputeByClosedDate) && isTatReComputeByClosedDate.equals("Y") && UtilValidate.isNotEmpty(closedByDate)) {
								createdStamp = UtilDateTime.getDayStart(closedByDate);
								if (closedByDate.after(lastClosedByDate)) {
									continue;
								}
							}
							
							countTat=0;
							if (UtilValidate.isNotEmpty(prevStatusDate)) {
								int intervalDays = UtilDateTime.getIntervalInDays(prevStatusDate, createdStamp);
								if (intervalDays > 0) {
								}else {
									prevStatusDate = createdStamp;
									tatDaysByHistory.put(historyId, countTat);
									prevStatus=statusId;
									continue;
								}
							}
							
							if (slaTatPauseStatusList.contains(statusId)) {
								countTat = 0;
								if (UtilValidate.isNotEmpty(prevStatusDate) && (UtilValidate.isNotEmpty(prevStatus) && !slaTatPauseStatusList.contains(prevStatus))) {
									int intervalDays = UtilDateTime.getIntervalInDays(prevStatusDate, createdStamp);
									if (intervalDays > 0) {
										countTat = intervalDays;
										for(int i=1;i<=intervalDays;i++) {
											Timestamp inBetweenDay = UtilDateTime.addDaysToTimestamp(prevStatusDate, i);
											if (UtilValidate.isNotEmpty(holidays) && holidays.contains(inBetweenDay)) {
												countTat--;
											}
										}
									}else {
										countTat = 0;
									}
								}
								prevStatusDate = createdStamp;
								prevStatus = statusId;
							}else {
								if (UtilValidate.isNotEmpty(prevStatusDate) && (UtilValidate.isNotEmpty(prevStatus) && !slaTatPauseStatusList.contains(prevStatus))) {
									int intervalDays = UtilDateTime.getIntervalInDays(prevStatusDate, createdStamp);
									if (intervalDays > 0) {
										countTat = intervalDays;
										for(int i=1;i<=intervalDays;i++) {
											Timestamp inBetweenDay = UtilDateTime.addDaysToTimestamp(prevStatusDate, i);
											if (UtilValidate.isNotEmpty(holidays) && holidays.contains(inBetweenDay)) {
												countTat--;
											}
										}
									}else {
										countTat = 0;
									}
								}else {
									countTat = 0;
								}
								prevStatus = statusId;
								prevStatusDate = createdStamp;
							}
							tatDaysByHistory.put(historyId,countTat);
						}
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		result.put("tatDaysByHistory", tatDaysByHistory);
		return result;
	}
	
    public static Timestamp getSrReopenDate(Delegator delegator,String custRequestId) {
    	Timestamp reopenedDate=null;
    	try {
    		GenericValue custRequest = delegator.findOne("CustRequest",UtilMisc.toMap("custRequestId", custRequestId), false);
    		if (UtilValidate.isNotEmpty(custRequest)) {
    			reopenedDate = custRequest.getTimestamp("reopenedDate");
    		}
    	}catch (Exception e) {
			// TODO: handle exception
		}
    	return reopenedDate;
    }
	public static String capitalizeFirstLetterOfWords(String input) {
		if(UtilValidate.isNotEmpty(input)) {
			Pattern pattern = Pattern.compile("\\b\\w");
			Matcher matcher = pattern.matcher(input.toLowerCase());
			StringBuffer result = new StringBuffer();
			while (matcher.find()) {
				matcher.appendReplacement(result, matcher.group().toUpperCase());
			}
			matcher.appendTail(result);
			return result.toString();
		}else {
			return "";
		}
	}

	public static boolean isAlphanumeric(String value) {
		if (UtilValidate.isEmpty(value)) {
			return false;
		}
		try {
			Pattern pattern = Pattern.compile(ALPHANUMERIC_REGEX, Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(value);
			return matcher.matches();
		} catch (Exception e) {
		}
		return false;
	}
}
