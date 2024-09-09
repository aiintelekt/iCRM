package org.fio.admin.portal.service;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.fio.admin.portal.util.DataUtil;
import org.fio.homeapps.constants.GlobalConstants.DateTimeTypeConstant;
import org.fio.message.SandboxMessage;
import org.fio.util.SandboxMessageUtil;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.jdbc.SQLProcessor;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;

public class CommonServices {
	private static final String MODULE = CommonServices.class.getName();
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> createPartyCustomField(DispatchContext dctx, Map<String , Object> context){
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> requestContext = (Map<String, Object>) context.get("requestContext");
		try {
			if(UtilValidate.isNotEmpty(requestContext)) {
				String partyId = (String) requestContext.get("partyId");
				Set<String> keys = requestContext.keySet();
				for(String key : keys) {
					Object value = requestContext.get(key);
					String val = "";
					if(value instanceof List) {
						val =  (String)((List<String>) value).get(0);
					} else if(value instanceof String) {
						val = (String) value;
					}
					boolean isDate = DataUtil.isDate(val, "date");
					if(isDate) {
						String globalDateFormat = org.fio.homeapps.util.DataHelper.getGlobalDateFormat(delegator);
						try {
							val = DataUtil.convertDateTimestamp(val, new SimpleDateFormat(globalDateFormat), DateTimeTypeConstant.DATE, DateTimeTypeConstant.STRING);
						} catch (Exception e) {
							val = "";
						}
						
					} else {
						isDate = DataUtil.isDate(val, "timestamp");
						if(isDate) {
							String globalDateTimeFormat = org.fio.homeapps.util.DataHelper.getGlobalDateTimeFormat(delegator);
							try {
								val = DataUtil.convertDateTimestamp(val, new SimpleDateFormat(globalDateTimeFormat), DateTimeTypeConstant.TIMESTAMP, DateTimeTypeConstant.STRING);
							} catch (Exception e) {
								val = "";
							}
							
						}
					}
					
					GenericValue customField = EntityQuery.use(delegator).from("CustomField").where("customFieldId", key).queryFirst();
					if(UtilValidate.isEmpty(customField))
						continue;
					
					if(UtilValidate.isNotEmpty(customField) && UtilValidate.isNotEmpty(partyId)) {
						GenericValue customFieldValue = EntityQuery.use(delegator).from("CustomFieldValue").where("customFieldId", key, "partyId", partyId).filterByDate().queryFirst();
						if(UtilValidate.isEmpty(customFieldValue)) {
							customFieldValue = delegator.makeValue("CustomFieldValue");
							customFieldValue.set("customFieldId", key);
							customFieldValue.set("partyId", partyId);
							customFieldValue.set("fieldValue", UtilValidate.isNotEmpty(val) ? val : "");
							customFieldValue.create();
						} else {
							customFieldValue.set("fieldValue", UtilValidate.isNotEmpty(val) ? (String) val : "");
							customFieldValue.store();
						}
					}
					
				}
			}
			
			result = ServiceUtil.returnSuccess();
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> createNonPartyCustomField(DispatchContext dctx, Map<String , Object> context){
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> requestContext = (Map<String, Object>) context.get("requestContext");
		try {
			if(UtilValidate.isNotEmpty(requestContext)) {
				String partyId = (String) requestContext.get("partyId");
				String domainEntityType = (String) requestContext.get("domainEntityType");
				String domainEntityId = (String) requestContext.get("domainEntityId");
				Set<String> keys = requestContext.keySet();
				for(String key : keys) {
					Object value = requestContext.get(key);
					String val = "";
					if(value instanceof List) {
						val =  (String)((List<String>) value).get(0);
					} else if(value instanceof String) {
						val = (String) value;
					}
					// check the value is date or not, if it's date then we will try to convert to global format
					boolean isDate = DataUtil.isDate(val, "date");
					if(isDate) {
						String globalDateFormat = org.fio.homeapps.util.DataHelper.getGlobalDateFormat(delegator);
						try {
							val = DataUtil.convertDateTimestamp(val, new SimpleDateFormat(globalDateFormat), DateTimeTypeConstant.DATE, DateTimeTypeConstant.STRING);
						} catch (Exception e) {
							val = "";
						}
						
					} else {
						isDate = DataUtil.isDate(val, "timestamp");
						if(isDate) {
							String globalDateTimeFormat = org.fio.homeapps.util.DataHelper.getGlobalDateTimeFormat(delegator);
							try {
								val = DataUtil.convertDateTimestamp(val, new SimpleDateFormat(globalDateTimeFormat), DateTimeTypeConstant.TIMESTAMP, DateTimeTypeConstant.STRING);
							} catch (Exception e) {
								val = "";
							}
							
						}
					}
					
					GenericValue customField = EntityQuery.use(delegator).from("CustomField").where("customFieldId", key).queryFirst();
					if(UtilValidate.isEmpty(customField))
						continue;
					
					if(UtilValidate.isNotEmpty(customField) && UtilValidate.isNotEmpty(domainEntityType) && UtilValidate.isNotEmpty(domainEntityId)) {
						
						if("SERVICE_REQUEST".equals(domainEntityType)) {
							GenericValue customFieldValue = EntityQuery.use(delegator).from("CustRequestAttribute").where("custRequestId", domainEntityId, "attrName", key).queryFirst();
							if(UtilValidate.isEmpty(customFieldValue)) {
								customFieldValue = delegator.makeValue("CustRequestAttribute");
								customFieldValue.set("custRequestId", domainEntityId);
								customFieldValue.set("attrName", key);
								customFieldValue.set("attrValue", UtilValidate.isNotEmpty(val) ? val : "");
								customFieldValue.create();
							} else {
								customFieldValue.set("attrValue", UtilValidate.isNotEmpty(val) ? (String) val : "");
								customFieldValue.store();
							}
						}
						
					}
					
				}
			}
			
			result = ServiceUtil.returnSuccess();
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		
		return result;
	}
	
	public static Map<String, Object> activateDeactivateParty(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> result = new HashMap<String, Object>();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
		String partyId = (String) context.get("partyId");
		String enabled = (String) context.get("enabled");
		if(UtilValidate.isNotEmpty(partyId)) {
			// when to expire the account
	        Timestamp expireDate = (Timestamp) context.get("expireDate");
	        if (expireDate == null) {
	            expireDate = UtilDateTime.nowTimestamp();
	        }
			try {
				String statusId = "PARTY_ENABLED";
				if("N".equals(enabled))
					statusId = "PARTY_DISABLED";
				//check userlogin and deactivate
				EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition(EntityOperator.OR,
								EntityCondition.makeCondition("enabled", EntityOperator.EQUALS, "Y".equals(enabled) ? "N" : "Y" ),
								EntityCondition.makeCondition("enabled", EntityOperator.EQUALS, null)
								),
						EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId)
						);
				List<GenericValue> userLoginList = EntityQuery.use(delegator).from("UserLogin").where(condition).queryList();
				if(UtilValidate.isNotEmpty(userLoginList)) {
					List<GenericValue> toBeUpdate = new ArrayList<GenericValue>();
					for(GenericValue userLoginGv : userLoginList) {
						userLoginGv.set("enabled", enabled);
						userLoginGv.set("disabledDateTime", "N".equals(enabled) ? expireDate : null);
						toBeUpdate.add(userLoginGv);
					}
					delegator.storeAll(toBeUpdate);
				}
				
	            GenericValue party = delegator.findOne("Party", UtilMisc.toMap("partyId", partyId),false);
	            party.put("statusId", statusId);
	            party.store();
	            
	            if("N".equals(enabled)) 
	            	delegator.create("PartyDeactivation", UtilMisc.toMap("partyId", partyId, "deactivationTimestamp", expireDate));
	            	
	        } catch (GenericEntityException e) {
	            return ServiceUtil.returnError("Error occurred while doing the " + ("N".equals(enabled) ? "deactivated" : "activated") );
	        }
		}
		result = ServiceUtil.returnSuccess("Successfully "+ ("N".equals(enabled) ? "deactivated" : "activated") );
		result.put("partyId", partyId);
		return result; 
	}
		
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getNavTabConfiguration(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> result = new HashMap<String, Object>();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		HttpSession  session = (HttpSession) context.get("session");
		HttpServletRequest request = (HttpServletRequest) context.get("request");
		result = ServiceUtil.returnSuccess("Nav Tab has been loaded successfully!");
		try {
			String componentId = (String) context.get("componentId");
			String tabConfigId = (String) context.get("tabConfigId");
			String hideTabIds = (String) context.get("hideTabIds");
			
			String userLoginId = userLogin.getString("userLoginId");
			Map<String, Object> tabConfiguration = new HashMap<String, Object>();
			if(UtilValidate.isNotEmpty(componentId) && UtilValidate.isNotEmpty(tabConfigId)) {
				if(componentId.contains("-")) componentId = (componentId.replace("-", "_")).toUpperCase();
				List<GenericValue> navTabsConfigList = EntityQuery.use(delegator).from("NavTabsConfig").where("componentId", componentId, "tabConfigId", tabConfigId,"isEnabled","Y").orderBy("sequenceNo").queryList();
				if(UtilValidate.isNotEmpty(navTabsConfigList)) {
					List<String> groupIds = UtilValidate.isNotEmpty(session.getAttribute("userLoginSecurityGroups")) ? (List<String>) session.getAttribute("userLoginSecurityGroups") : UtilValidate.isNotEmpty(request.getAttribute("userLoginSecurityGroups")) ? (List<String>) request.getAttribute("userLoginSecurityGroups") : new LinkedList<>();
					if(UtilValidate.isEmpty(groupIds)) {
						 String userLoginPartyId = userLogin.getString("partyId");
						 Map<String, Object> userData =  org.fio.homeapps.util.DataHelper.getUserRoleGroup(delegator, userLoginPartyId);
						 groupIds = UtilValidate.isNotEmpty(userData) ? (List<String>) userData.get("userLoginSecurityGroups") : new LinkedList<>();
						 List<String> userLoginRoles = UtilValidate.isNotEmpty(userData) ? (List<String>) userData.get("userLoginRoles") : new LinkedList<>();
						 
						 session.setAttribute("userLoginSecurityGroups", groupIds);
						 request.setAttribute("userLoginSecurityGroups", groupIds);
						 session.setAttribute("userLoginRoles", userLoginRoles);
						 request.setAttribute("userLoginRoles", userLoginRoles);
					 }
					List<String> hideTabIdList = new ArrayList<String>();
					
					if(UtilValidate.isNotEmpty(hideTabIds)) {
						if(UtilValidate.isNotEmpty(hideTabIds) && hideTabIds.contains(",")) {
							hideTabIdList = org.fio.admin.portal.util.DataUtil.stringToList(hideTabIds, ",");
						} else
							hideTabIdList.add(hideTabIds);
					}
					
					List<Map<String, Object>> navTabList = new ArrayList<Map<String,Object>>();
					boolean hasFullAccess = org.fio.homeapps.util.DataUtil.hasFullPermission(delegator, userLoginId);
					Map<String, Object> tabContentMap = new HashMap<String, Object>();
					for(GenericValue navTabsConfig : navTabsConfigList) {
						Map<String, Object> tabMap = new LinkedHashMap<String, Object>();
						String tabId = navTabsConfig.getString("tabId");
						if(hideTabIdList.contains(tabId)) continue;
						String securityPermissionId = navTabsConfig.getString("permissionId");
						boolean hasPermission = true;
						if(!hasFullAccess && UtilValidate.isNotEmpty(securityPermissionId)) {
							hasPermission = org.fio.homeapps.util.DataUtil.validateSecurityPermission(delegator, groupIds, securityPermissionId);
						}
						if(hasPermission) {
							tabMap = DataUtil.convertGenericValueToMap(delegator, navTabsConfig);
							tabContentMap.put(tabId, navTabsConfig.getString("tabContent"));
							navTabList.add(tabMap);
						}
					}
					
					tabConfiguration.put("navTabList", navTabList);
					tabConfiguration.put("tabContentMap", tabContentMap);
					tabConfiguration.put("componentId", componentId);
					tabConfiguration.put("tabConfigId", tabConfigId);
				}
			}
			result.put("tabConfiguration", tabConfiguration);
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnSuccess("Following error occurred: "+e.getMessage());
			result.put("responseCode", "EA103");
			return result; 
		}
		result.put("responseCode", "S200");
		return result; 
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> sendSandboxSms(DispatchContext dctx, Map<String, Object> context) {
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> responseData = new HashMap<String, Object>();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		
		try {
			Map<String, Object> requestContext = UtilValidate.isNotEmpty(context.get("requestContext")) ? (HashMap<String, Object>) context.get("requestContext") : new HashMap<String, Object>();
			
			String sandboxUser = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SANDBOX_USERNAME");
			String sandboxPassword = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SANDBOX_PASSWORD");
			String baseURL = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SANDBOX_BASE_URL");
			
			String smsMonthlyLimit = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SMS_MONTHLY_LIMIT","0");
			long monthlyLimit = Long.parseLong(smsMonthlyLimit);
			
			String smsRemainPercent = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SMS_REMAINDER_PERCENT","90");
			long remainderPercent = Long.parseLong(smsRemainPercent);
			
	        LocalDate currentDate = LocalDate.now();
	        YearMonth currentYearMonth = YearMonth.from(currentDate);
	        
	        LocalDate firstDayOfMonth = currentYearMonth.atDay(1);
	        LocalDate lastDayOfMonth = currentYearMonth.atEndOfMonth();
	        
	        Timestamp monthStart = Timestamp.valueOf(firstDayOfMonth.atStartOfDay());
	        Timestamp monthEnd = Timestamp.valueOf(lastDayOfMonth.atTime(23, 59, 59));
	        
	        
			EntityCondition condition =  EntityCondition.makeCondition(EntityOperator.AND,
					EntityCondition.makeCondition("createdTxStamp", EntityOperator.GREATER_THAN_EQUAL_TO, monthStart),
					EntityCondition.makeCondition("createdTxStamp", EntityOperator.LESS_THAN_EQUAL_TO, monthEnd)
					);
			long smsCurrentMonthCount = EntityQuery.use(delegator).from("SmsAuditLog").where(condition).queryCount();
			
			int percent = (int) ((smsCurrentMonthCount *remainderPercent) / 100);
			
			
			if(smsCurrentMonthCount > monthlyLimit) {
				return ServiceUtil.returnSuccess("Maximum count reached!");
			}
			
			String isSmsRemainderSend = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "IS_SMS_REMAINDER_SEND");
			if(smsCurrentMonthCount >= percent && !("Y".equals(isSmsRemainderSend))) {
				//Trigger the email if sms send percentage match
				/*
				String templateId = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "_TPL");
				String defaultFromEmailId = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "FROM_EMAIL_ID");
				
				if(UtilValidate.isNotEmpty(templateId)) {

					GenericValue template = delegator.findOne("TemplateMaster", UtilMisc.toMap("templateId",templateId), false);

					String emailContent = "";
					String templateFormContent = template.getString("templateFormContent");
					if (UtilValidate.isNotEmpty(templateFormContent)) {
						if (org.apache.commons.codec.binary.Base64.isBase64(templateFormContent)) {
							templateFormContent = org.ofbiz.base.util.Base64.base64Decode(templateFormContent);
						}
					}
					
					
					List conditions = new ArrayList<>();
					
					//String fromEmail = org.groupfio.common.portal.util.UtilContactMech.getPartyEmail(delegator, partyId, null);
					String fromEmail = defaultFromEmailId;
					
					String toEmail = org.groupfio.common.portal.util.UtilContactMech.getPartyEmail(delegator, "", null);
					//String toEmail = "sislam131@gmail.com";
					
					if (UtilValidate.isEmpty(toEmail)) {
						Debug.logError("toEmail not found: custRequestId", MODULE);
						
					}
					
					// prepare email content [start]
					Map<String, Object> extractContext = new LinkedHashMap<String, Object>();
					extractContext.put("delegator", delegator);
					extractContext.put("extractType", ExtractType.EXTRACT_EMAIL_DATA);
					extractContext.put("fromEmail", fromEmail);
					extractContext.put("toEmail", toEmail);
					extractContext.put("emailContent", templateFormContent);

					Map<String, Object> extractResultContext = ExtractFacade.extractData(extractContext);
					emailContent = (String) extractResultContext.get("emailContent");
					// prepare email content [end]

					Map<String, Object> reqContext = FastMap.newInstance();

					reqContext.put("nsender", fromEmail);
					reqContext.put("nto", toEmail);
					reqContext.put("subject", template.getString("subject"));
					reqContext.put("emailContent", emailContent);
					Map<String, Object> callCtxt = FastMap.newInstance();
					callCtxt.put("requestContext", reqContext);
					callCtxt.put("userLogin", userLogin);

					Debug.log("===== ACTIVITY COMPLETE EMAIL TRIGGER ===="+callCtxt);

					dispatcher.runAsync("common.sendEmail", callCtxt);
				}
				*/
				
				/*
				GenericValue globalParameter = EntityQuery.use(delegator).from("PretailLoyaltyGlobalParameters").where("parameterId","IS_SMS_REMAINDER_SEND").queryFirst();
				if(UtilValidate.isNotEmpty(globalParameter)) {
					globalParameter.set("value", "Y");
					globalParameter.store();
				} else {
					globalParameter.set("parameterId","IS_SMS_REMAINDER_SEND");
					globalParameter.set("value", "Y");
					globalParameter.create();
				}
				*/
			}
			
			String toPhoneNumber = (String) requestContext.get("toPhoneNumber");
			String fromPhoneNumber = UtilValidate.isNotEmpty(requestContext.get("fromPhoneNumber")) ? (String) requestContext.get("fromPhoneNumber") : "";
			String smsBody = (String) requestContext.get("smsBody");
			String requestDataStr = (String) requestContext.get("requestData");
			
			String domainEntityType = (String) requestContext.get("domainEntityType");
			String domainEntityId = (String) requestContext.get("domainEntityId");
			
			if(UtilValidate.isEmpty(sandboxUser) && UtilValidate.isEmpty(sandboxPassword)) {
				return ServiceUtil.returnError("Please configure SMS account");
			}
			
			SandboxMessage sandboxMessage = new SandboxMessage.SandboxMessageBuilder(sandboxUser, sandboxPassword)
					.setRequestMethod("POST")
					.setUrl(baseURL)
					.setRequestData(requestDataStr)
					.build();
			Debug.logInfo("sandboxMessage Obj : "+ sandboxMessage.toString(), MODULE);
			
			String response = SandboxMessageUtil.sendMessage(sandboxMessage);
			
			String msgSendStatus = "ERROR";
			
			if("SUCCESS".contains(response.toUpperCase())) {
				//create communication events
				msgSendStatus = "SUCCESS";				
			}
			Map<String, Object> input = new HashMap<String, Object>();
			input.put("communicationEventTypeId", "SMS_COMMUNICATION");
			input.put("statusId", "COM_COMPLETE");
			input.put("contactMechTypeId", "TELECOM_NUMBER");
			input.put("entryDate", UtilDateTime.nowTimestamp());
			input.put("datetimeStarted", UtilDateTime.nowTimestamp());
			input.put("datetimeEnded", UtilDateTime.nowTimestamp());
			input.put("subject", "");
			input.put("content", smsBody);
			input.put("contentMimeTypeId", "text/plain");
			input.put("fromData", "");
			input.put("toData", toPhoneNumber);
			input.put("msgSendStatus", msgSendStatus);
			input.put("msgSentTime", UtilDateTime.nowTimestamp());
			
			String communicationEventId = "";
			try {
				Map<String, Object> serviceResults = dispatcher.runSync("createCommunicationEvent", input);
				communicationEventId = (String) serviceResults.get("communicationEventId");
			} catch (GenericServiceException e) {
				String errMsg = "Problem caling sms comm event create: " + e.toString();
				Debug.logError(e, errMsg, MODULE);
			}
			
			//Store audit logs
			String logId = delegator.getNextSeqId("SmsAuditLog");
			GenericValue smsAuditLog = delegator.makeValue("SmsAuditLog");
			smsAuditLog.set("logId", logId);
			smsAuditLog.set("typeId", "SMS");
			smsAuditLog.set("domainEntityType", domainEntityType);
			smsAuditLog.set("domainEntityId", domainEntityId);
			smsAuditLog.set("fromPhoneNumber", "");
			smsAuditLog.set("toPhoneNumber", toPhoneNumber);
			smsAuditLog.set("requestData", requestDataStr);
			smsAuditLog.set("responseData", response);
			smsAuditLog.set("status", msgSendStatus);
			smsAuditLog.set("comments", "");
			smsAuditLog.create();
			
			result.put("results", UtilMisc.toMap("response", response));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> customerSearchHistory(DispatchContext dctx, Map<String, Object> context) {
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> result = ServiceUtil.returnSuccess();
		
		try {
			String searchHistoryDuration = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SEARCH_CUST_HIS_DURATION", "30");
			String partyId = (String) context.get("partyId");
			String staffName = (String) context.get("staffName");
			String storeId = (String) context.get("storeId");
			String purposeTypeId = (String) context.get("purposeTypeId");
			
			EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId),
						EntityCondition.makeCondition("staffName", EntityOperator.EQUALS, staffName),
						EntityCondition.makeCondition("storeId", EntityOperator.EQUALS, storeId)
					);
			GenericValue searchHistory = EntityQuery.use(delegator).from("UserSearchCustomerHistory").where(condition).orderBy("createdTxStamp DESC").queryFirst();
			Timestamp lastAccessedDate = null;
			boolean isEligible = true;
			if(UtilValidate.isNotEmpty(searchHistory)) {
				lastAccessedDate = searchHistory.getTimestamp("lastAccessedDate");
				
				LocalDateTime nowDateTime = LocalDateTime.now();
				LocalDateTime lastAccDate = lastAccessedDate.toLocalDateTime();
				int searchCustDuration = UtilValidate.isNotEmpty(searchHistoryDuration) ? Integer.parseInt(searchHistoryDuration) : 30;
				Duration durationBw = Duration.between(lastAccDate, nowDateTime);
				if(durationBw.toMinutes() > searchCustDuration) {
					isEligible = true;
				} else if(durationBw.toMinutes() < searchCustDuration) {
					isEligible = false;
				}
			}
			
			if(isEligible) {
				String firstName = "";
				String lastName = "";
				String storeName = "";
				if(UtilValidate.isNotEmpty(partyId)) {
					GenericValue person = EntityQuery.use(delegator).from("Person").where("partyId", partyId).queryFirst();
					firstName = UtilValidate.isNotEmpty(person) ? person.getString("firstName"): "";
					lastName = UtilValidate.isNotEmpty(person) ? person.getString("lastName"): "";
				}
				if(UtilValidate.isNotEmpty(storeId)) {
					GenericValue productStore = EntityQuery.use(delegator).from("ProductStore").where("productStoreId", storeId).queryFirst();
					storeName = UtilValidate.isNotEmpty(productStore) ? productStore.getString("storeName") : "";
					
				}
				
				searchHistory = delegator.makeValue("UserSearchCustomerHistory");
				searchHistory.set("partyId", partyId);
				searchHistory.set("firstName", firstName);
				searchHistory.set("lastName", lastName);
				searchHistory.set("staffName", staffName);
				searchHistory.set("storeId", storeId);
				searchHistory.set("storeName", storeName);
				searchHistory.set("lastAccessedDate", UtilDateTime.nowTimestamp());
				searchHistory.set("purposeTypeId", purposeTypeId);
				searchHistory.set("userLoginId", UtilValidate.isNotEmpty(userLogin) ? userLogin.getString("userLoginId") : "");
				searchHistory.create();
			}
			result.put("result", UtilMisc.toMap("partyId", partyId, "staffName", staffName, "storeId", storeId));
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError("Error : "+e.getMessage());
		}
		return result;
	}
	@SuppressWarnings("unused")
	public static Map<String, Object> getSampleRequest(DispatchContext ctx, Map<?, ?> context) {
		LocalDispatcher dispatcher = (LocalDispatcher) ctx.getDispatcher();
		Delegator delegator = ctx.getDelegator();
		List<Object> errorList = FastList.newInstance();
		StringBuffer XmlOutput = new StringBuffer();
		Map<String, Object> result = ServiceUtil.returnSuccess();
		List<Object> OrderHeaderMainList=new LinkedList<Object>();
		String serviceName = (String) context.get("serviceName");
		String outXml = "";
		try{
			GenericValue testService = delegator.findOne("WebServiceTest", false, UtilMisc.toMap("serviceName", serviceName));
			if(testService!=null){
				if(UtilValidate.isNotEmpty(testService.getString("sampleRequest"))){
					outXml = testService.getString("sampleRequest");
				}
			}		
		}catch(Exception ex){
			ex.printStackTrace();
		}
		result.put("XmlOutput",outXml);
		return result;
	}
}
