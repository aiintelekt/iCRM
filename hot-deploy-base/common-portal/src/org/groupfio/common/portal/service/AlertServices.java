package org.groupfio.common.portal.service;

import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.fio.homeapps.util.DataUtil;
import org.fio.homeapps.util.UtilDateTime;
import org.groupfio.common.portal.extractor.ExtractFacade;
import org.groupfio.common.portal.extractor.constants.ExtractorConstants.ExtractType;
import org.groupfio.common.portal.resolver.EscalationResolver;
import org.groupfio.common.portal.util.DataHelper;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

/**
 * @author Sharif
 *
 */
public class AlertServices {

	private static final String MODULE = AlertServices.class.getName();
	
	public static Map<String, Object> triggerSPFailedNotification(DispatchContext dctx, Map context) {
		
		LocalDispatcher dispatcher = dctx.getDispatcher();
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	Locale locale = (Locale) context.get("locale");
    	
    	SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		SimpleDateFormat df2 = new SimpleDateFormat("MM/dd/yyyy");
		NumberFormat nf = NumberFormat.getInstance(locale);
		
    	Map<String, Object> requestContext = (Map<String, Object>) context.get("requestContext");
    	
    	//String partyId = (String) requestContext.get("partyId");
				
		Map<String, Object> result = new HashMap<String, Object>();

		try {
			
			String defaultFromEmailId = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "FROM_EMAIL_ID");
			String toEmailId = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SP_FAILED_TO_EMAIL");
			String templateId = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SP_FAILED_TPL");
			String activeSpList = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "ACTIVE_SP_LIST");
			List<String> activeSPs = new ArrayList<>();
			
			if (UtilValidate.isNotEmpty(activeSpList)) {
				activeSPs = Arrays.asList(activeSpList.split(","));
			}
			
			DynamicViewEntity dynamicView = new DynamicViewEntity();
			
			dynamicView.addMemberEntity("LP", "LogProc");
			dynamicView.addAlias("LP", "seqId", null, null, null, true, null);
			dynamicView.addAlias("LP", "timeStamp");
			dynamicView.addAlias("LP", "processId");
			dynamicView.addAlias("LP", "status");
			dynamicView.addAlias("LP", "tableName");
			dynamicView.addAlias("LP", "id");
			dynamicView.addAlias("LP", "logMsg1");
			dynamicView.addAlias("LP", "logMsg2");
			dynamicView.addAlias("LP", "logMsg3");
			dynamicView.addAlias("LP", "isNotificationSend");
			
			String orderBy = "timeStamp DESC";
			
			Set<String> fieldsToSelect = new LinkedHashSet<String>();
			
			List<EntityCondition> conditionList = FastList.newInstance();
			
			conditionList.add(EntityCondition.makeCondition("timeStamp", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(UtilDateTime.nowTimestamp())));
			conditionList.add(EntityCondition.makeCondition("timeStamp", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(UtilDateTime.nowTimestamp())));
			
			conditionList.add(EntityCondition.makeCondition(EntityOperator.OR,
					EntityCondition.makeCondition("isNotificationSend", EntityOperator.EQUALS, null),
					EntityCondition.makeCondition("isNotificationSend", EntityOperator.EQUALS, "N")
					));
			
			conditionList.add(EntityCondition.makeCondition("status", EntityOperator.EQUALS, "ERROR"));
			
			if (UtilValidate.isNotEmpty(activeSPs)) {
				//conditionList.add(EntityCondition.makeCondition("processId", EntityOperator.IN, activeSPs));
			}
			
			EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			Debug.logInfo("triggerSPFailedNotification condition: "+condition, MODULE);
			
			EntityListIterator pli = EntityQuery.use(delegator)
            		//.select(fieldsToSelect)
                    .from(dynamicView)
                    .where(condition)
                    .orderBy(orderBy)
                    .cursorScrollInsensitive()
                    //.fetchSize(highIndex)
                    //.distinct()
                    //.cache(true)
                    .queryIterator();
            // get the partial list for this page
			List<GenericValue> resultList = pli.getCompleteList();
			Debug.logInfo("triggerSPFailedNotification resultList size: "+resultList.size(), MODULE);
			if (UtilValidate.isNotEmpty(resultList)) {
				
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
						} else {
						
						// prepare email content [start]
						Map<String, Object> extractContext = new LinkedHashMap<String, Object>();
						extractContext.put("delegator", delegator);
						extractContext.put("extractType", ExtractType.EXTRACT_EMAIL_DATA);
						extractContext.put("fromEmail", fromEmail);
						extractContext.put("toEmail", toEmail);
						extractContext.put("listOfFsp", resultList);
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

						Debug.log("===== SP FAILED NOTIFICATION EMAIL TRIGGER ===="+callCtxt);

						dispatcher.runAsync("common.sendEmail", callCtxt);
						
						// mark as notification sent
						
						for (GenericValue sp : resultList) {
							GenericValue log = EntityQuery.use(delegator).from("LogProc").where("seqId", sp.getInteger("seqId")).queryFirst();
							log.put("isNotificationSend", "Y");
							log.store();
						}
						
						}
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("errorMsg", e.getMessage());
			return result;
		}
		
		result.putAll(ServiceUtil.returnSuccess("Successfully send SP failed notification"));
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> triggerOppoEscalationEmail(DispatchContext dctx, Map<String, Object> context){
		LocalDispatcher dispatcher = dctx.getDispatcher();
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	Locale locale = (Locale) context.get("locale");
    	
    	SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		SimpleDateFormat df2 = new SimpleDateFormat("MM/dd/yyyy");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:00");
		NumberFormat nf = NumberFormat.getInstance(locale);
		
    	Map<String, Object> requestContext1 = (Map<String, Object>) context.get("requestContext");
    			
		Map<String, Object> result = new HashMap<String, Object>();

		try {
			
			String defaultFromEmailId = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "FROM_EMAIL_ID");
			//String toEmailId = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SP_FAILED_TO_EMAIL");
			
			Timestamp now = org.ofbiz.base.util.UtilDateTime.nowTimestamp();
			String currentDateTime = df.format(now);

			String toEmailAddressRole = DataUtil.getGlobalValue(delegator, "OPPO_ESC_TO_EMAIL_ROLE");
			String oppoFirstEscTemplateId = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "OPPO_FIRST_ESC_TEMPLATE");
			GenericValue oppoFirstEscTemplateData = delegator.findOne("TemplateMaster", UtilMisc.toMap("templateId",oppoFirstEscTemplateId), false);

			String oppoSecEscTemplateId = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "OPPO_SEC_ESC_TEMPLATE");
			GenericValue oppoSecEscTemplateData = delegator.findOne("TemplateMaster", UtilMisc.toMap("templateId",oppoSecEscTemplateId), false);
			
			String oppoThridEscTemplateId = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "OPPO_THIRD_ESC_TEMPLATE");			
			GenericValue oppoThridEscTemplateData = delegator.findOne("TemplateMaster", UtilMisc.toMap("templateId",oppoThridEscTemplateId), false);

			//first Escalation Emails
			List<EntityCondition> conditions = new ArrayList<EntityCondition>();
			conditions.add(EntityCondition.makeCondition("opportunityStatusId", EntityOperator.NOT_EQUAL, "OPPO_CLOSED"));
			conditions.add(EntityCondition.makeCondition("escalationDate1", EntityOperator.EQUALS, currentDateTime));
						
			EntityCondition firstEscCondition = EntityCondition.makeCondition(conditions, EntityOperator.AND);
			
			List<GenericValue> firstEscTriggerList = EntityQuery.use(delegator).from("SalesOpportunity").where(firstEscCondition).queryList();
			
			firstEscTriggerList.parallelStream().forEach(e -> {
				String salesOpportunityId = e.getString("salesOpportunityId");
    			String nto = DataHelper.getOppoRoleEmails(delegator, salesOpportunityId, toEmailAddressRole);
    			
    			String ownerId = UtilValidate.isNotEmpty(e) ? e.getString("ownerId") : "";
				
				if(UtilValidate.isNotEmpty(defaultFromEmailId) && UtilValidate.isNotEmpty(nto) ) {
        			
    				String subject = "";
    				try {
    					if(UtilValidate.isNotEmpty(oppoFirstEscTemplateId) && UtilValidate.isNotEmpty(oppoFirstEscTemplateData)) {

    						String emailContent = "";
    						String templateFormContent = oppoFirstEscTemplateData.getString("templateFormContent");
    						if (UtilValidate.isNotEmpty(templateFormContent)) {
    							if (org.apache.commons.codec.binary.Base64.isBase64(templateFormContent)) {
    								templateFormContent = org.ofbiz.base.util.Base64.base64Decode(templateFormContent);
    							}
    						}

    						if(UtilValidate.isEmpty(oppoFirstEscTemplateData.getString("subject"))) {
    							subject = "OP#["+salesOpportunityId+"] escalation";
    						} else {
    							subject =  oppoFirstEscTemplateData.getString("subject") + " OP#["+salesOpportunityId+"] ";
    						}

    						// prepare email content [start]
    						Map<String, Object> extractContext = new LinkedHashMap<String, Object>();
    						extractContext.put("delegator", delegator);
    						extractContext.put("extractType", ExtractType.EXTRACT_EMAIL_DATA);
    						extractContext.put("fromEmail", defaultFromEmailId);
    						extractContext.put("toEmail", nto);
    						extractContext.put("partyId", UtilValidate.isNotEmpty(e.getString("partyId")) ? e.getString("partyId") : "");
    						extractContext.put("salesOpportunityId", salesOpportunityId);

    						extractContext.put("emailContent", templateFormContent);
    						extractContext.put("templateId", oppoFirstEscTemplateId);

    						Map<String, Object> extractResultContext = ExtractFacade.extractData(extractContext);
    						emailContent = (String) extractResultContext.get("emailContent");
    						// prepare email content [end]

    						Map<String, Object> callCtxt1 = FastMap.newInstance();
    						Map<String, Object> callResult1 = FastMap.newInstance();
    						Map<String, Object> requestContext = FastMap.newInstance();

    						requestContext.put("nsender", defaultFromEmailId);
    						requestContext.put("nto", nto);
    						requestContext.put("subject", subject);
    						requestContext.put("emailContent", emailContent);
    						requestContext.put("templateId", oppoFirstEscTemplateId);
    						//requestContext.put("ccAddresses", ccAddresses);
    						//requestContext.put("nbcc", ccAddresses);

    						callCtxt1.put("requestContext", requestContext);
    						callCtxt1.put("userLogin", userLogin);

    						Debug.log("===== OPPORTUNITY 1ST ESCALATION EMAIL TRIGGER ===="+callCtxt1);

    						callResult1 = dispatcher.runSync("common.sendEmail", callCtxt1);
    						if (ServiceUtil.isError(callResult1)) {
    							String errMsg = "Email send failed: "+ServiceUtil.getErrorMessage(callResult1);
    							Debug.log("Exception in first escalation email service :"+errMsg);
    						}

    					} else {
    						Debug.log("Please configure template!");
    					}
    				} catch (Exception e2) {
						Debug.log("Exception in first escalation :"+e2.getMessage());
					}
    			} else {
    				Debug.log("From Email : "+ defaultFromEmailId +"----to email--->"+nto);
    			}
				
				
			});
			
			//second escalation Emails
			conditions = new ArrayList<EntityCondition>();
			conditions.add(EntityCondition.makeCondition("opportunityStatusId", EntityOperator.NOT_EQUAL, "OPPO_CLOSED"));
			conditions.add(EntityCondition.makeCondition("escalationDate2", EntityOperator.EQUALS, currentDateTime));
						
			EntityCondition secEscCondition = EntityCondition.makeCondition(conditions, EntityOperator.AND);
			
			List<GenericValue> secEscTriggerList = EntityQuery.use(delegator).from("SalesOpportunity").where(secEscCondition).queryList();
			
			secEscTriggerList.parallelStream().forEach(e -> {
				String salesOpportunityId = e.getString("salesOpportunityId");
    			String nto = DataHelper.getOppoRoleEmails(delegator, salesOpportunityId, toEmailAddressRole);
    			
    			String ownerId = UtilValidate.isNotEmpty(e) ? e.getString("ownerId") : "";
				
				if(UtilValidate.isNotEmpty(defaultFromEmailId) && UtilValidate.isNotEmpty(nto) ) {
        			
    				String subject = "";
    				try {
    					if(UtilValidate.isNotEmpty(oppoSecEscTemplateId) && UtilValidate.isNotEmpty(oppoSecEscTemplateData)) {

    						String emailContent = "";
    						String templateFormContent = oppoSecEscTemplateData.getString("templateFormContent");
    						if (UtilValidate.isNotEmpty(templateFormContent)) {
    							if (org.apache.commons.codec.binary.Base64.isBase64(templateFormContent)) {
    								templateFormContent = org.ofbiz.base.util.Base64.base64Decode(templateFormContent);
    							}
    						}

    						if(UtilValidate.isEmpty(oppoSecEscTemplateData.getString("subject"))) {
    							subject = "OP#["+salesOpportunityId+"] escalation";
    						} else {
    							subject =  oppoSecEscTemplateData.getString("subject") + " OP#["+salesOpportunityId+"] ";
    						}

    						// prepare email content [start]
    						Map<String, Object> extractContext = new LinkedHashMap<String, Object>();
    						extractContext.put("delegator", delegator);
    						extractContext.put("extractType", ExtractType.EXTRACT_EMAIL_DATA);
    						extractContext.put("fromEmail", defaultFromEmailId);
    						extractContext.put("toEmail", nto);
    						extractContext.put("partyId", UtilValidate.isNotEmpty(e.getString("partyId")) ? e.getString("partyId") : "");
    						extractContext.put("salesOpportunityId", salesOpportunityId);

    						extractContext.put("emailContent", templateFormContent);
    						extractContext.put("templateId", oppoSecEscTemplateId);

    						Map<String, Object> extractResultContext = ExtractFacade.extractData(extractContext);
    						emailContent = (String) extractResultContext.get("emailContent");
    						// prepare email content [end]

    						Map<String, Object> callCtxt1 = FastMap.newInstance();
    						Map<String, Object> callResult1 = FastMap.newInstance();
    						Map<String, Object> requestContext = FastMap.newInstance();
    						
    						requestContext.put("nsender", defaultFromEmailId);
    						requestContext.put("nto", nto);
    						requestContext.put("subject", subject);
    						requestContext.put("emailContent", emailContent);
    						requestContext.put("templateId", oppoSecEscTemplateId);
    						//requestContext.put("ccAddresses", ccAddresses);
    						//requestContext.put("nbcc", ccAddresses);

    						callCtxt1.put("requestContext", requestContext);
    						callCtxt1.put("userLogin", userLogin);

    						Debug.log("===== OPPORTUNITY 2ND ESCALATION EMAIL TRIGGER ===="+callCtxt1);

    						callResult1 = dispatcher.runSync("common.sendEmail", callCtxt1);
    						if (ServiceUtil.isError(callResult1)) {
    							String errMsg = "Email send failed: "+ServiceUtil.getErrorMessage(callResult1);
    							Debug.log("Exception in second escalation email service :"+errMsg);
    						}

    					} else {
    						Debug.log("Please configure template!");
    					}
    				} catch (Exception e2) {
						Debug.log("Exception in second escalation :"+e2.getMessage());
					}
    			} else {
    				Debug.log("From Email : "+ defaultFromEmailId +"----to email--->"+nto);
    			}
				
			});
			
			
			//third escalation Emails
			conditions = new ArrayList<EntityCondition>();
			conditions.add(EntityCondition.makeCondition("opportunityStatusId", EntityOperator.NOT_EQUAL, "OPPO_CLOSED"));
			conditions.add(EntityCondition.makeCondition("escalationDate3", EntityOperator.EQUALS, currentDateTime));
						
			EntityCondition thirdEscCondition = EntityCondition.makeCondition(conditions, EntityOperator.AND);
			
			List<GenericValue> thirdEscTriggerList = EntityQuery.use(delegator).from("SalesOpportunity").where(thirdEscCondition).queryList();
			
			thirdEscTriggerList.parallelStream().forEach(e -> {
				String salesOpportunityId = e.getString("salesOpportunityId");
    			String nto = DataHelper.getOppoRoleEmails(delegator, salesOpportunityId, toEmailAddressRole);
    			
    			String ownerId = UtilValidate.isNotEmpty(e) ? e.getString("ownerId") : "";
				
				if(UtilValidate.isNotEmpty(defaultFromEmailId) && UtilValidate.isNotEmpty(nto) ) {
        			
    				String subject = "";
    				try {
    					if(UtilValidate.isNotEmpty(oppoThridEscTemplateId) && UtilValidate.isNotEmpty(oppoThridEscTemplateData)) {

    						String emailContent = "";
    						String templateFormContent = oppoThridEscTemplateData.getString("templateFormContent");
    						if (UtilValidate.isNotEmpty(templateFormContent)) {
    							if (org.apache.commons.codec.binary.Base64.isBase64(templateFormContent)) {
    								templateFormContent = org.ofbiz.base.util.Base64.base64Decode(templateFormContent);
    							}
    						}

    						if(UtilValidate.isEmpty(oppoThridEscTemplateData.getString("subject"))) {
    							subject = "OP#["+salesOpportunityId+"] escalation";
    						} else {
    							subject =  oppoThridEscTemplateData.getString("subject") + " OP#["+salesOpportunityId+"] ";
    						}

    						// prepare email content [start]
    						Map<String, Object> extractContext = new LinkedHashMap<String, Object>();
    						extractContext.put("delegator", delegator);
    						extractContext.put("extractType", ExtractType.EXTRACT_EMAIL_DATA);
    						extractContext.put("fromEmail", defaultFromEmailId);
    						extractContext.put("toEmail", nto);
    						extractContext.put("partyId", UtilValidate.isNotEmpty(e.getString("partyId")) ? e.getString("partyId") : "");
    						extractContext.put("salesOpportunityId", salesOpportunityId);

    						extractContext.put("emailContent", templateFormContent);
    						extractContext.put("templateId", oppoThridEscTemplateId);

    						Map<String, Object> extractResultContext = ExtractFacade.extractData(extractContext);
    						emailContent = (String) extractResultContext.get("emailContent");
    						// prepare email content [end]

    						Map<String, Object> callCtxt1 = FastMap.newInstance();
    						Map<String, Object> callResult1 = FastMap.newInstance();
    						Map<String, Object> requestContext = FastMap.newInstance();

    						requestContext.put("nsender", defaultFromEmailId);
    						requestContext.put("nto", nto);
    						requestContext.put("subject", subject);
    						requestContext.put("emailContent", emailContent);
    						requestContext.put("templateId", oppoThridEscTemplateId);
    						//requestContext.put("ccAddresses", ccAddresses);
    						//requestContext.put("nbcc", ccAddresses);

    						callCtxt1.put("requestContext", requestContext);
    						callCtxt1.put("userLogin", userLogin);

    						Debug.log("===== OPPORTUNITY 3RD ESCALATION EMAIL TRIGGER ===="+callCtxt1);

    						callResult1 = dispatcher.runSync("common.sendEmail", callCtxt1);
    						if (ServiceUtil.isError(callResult1)) {
    							String errMsg = "Email send failed: "+ServiceUtil.getErrorMessage(callResult1);
    							Debug.log("Exception in third escalation email service :"+errMsg);
    						}

    					} else {
    						Debug.log("Please configure template!");
    					}
    				} catch (Exception e2) {
						Debug.log("Exception in third escalation :"+e2.getMessage());
					}
    			} else {
    				Debug.log("Third escalation From Email : "+ defaultFromEmailId +"----to email--->"+nto);
    			}
				
			});
			
		} catch (Exception e) {
			e.printStackTrace();
			result.put("errorMsg", e.getMessage());
			return result;
		}
		
		result.putAll(ServiceUtil.returnSuccess("Escalations has been sent successfully"));
		return result;
	}
    
}
