/**
 * 
 */
package org.groupfio.common.portal.util;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.fio.homeapps.util.DataUtil;
import org.fio.homeapps.util.ParamUtil;
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
public class ResAvailUtil {

	private static String MODULE = ResAvailUtil.class.getName();
	
	public static Map getCalBookedData(Delegator delegator, String domainEntityType, String domainEntityId) {
		try {
			if (UtilValidate.isNotEmpty(domainEntityType) && UtilValidate.isNotEmpty(domainEntityId)) {
				List conditions = FastList.newInstance();
				
				conditions.add(EntityCondition.makeCondition("domainEntityType", EntityOperator.EQUALS, domainEntityType));
				conditions.add(EntityCondition.makeCondition("domainEntityId", EntityOperator.EQUALS, domainEntityId));
				
				conditions.add(EntityCondition.makeCondition("availabilityTypeId", EntityOperator.EQUALS, "RESA_TYP_NON_AVAIL"));
				
                EntityCondition mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);
                List<GenericValue> entryList = delegator.findList("ResourceAvailability", mainConditons, null, null, null, false);
    			if (UtilValidate.isNotEmpty(entryList)) {
    				Map<String, Object> selectedCalSlot = new LinkedHashMap<>();
    				for (GenericValue entry : entryList) {
    					Map<String, Object> calSlot = new LinkedHashMap<>();
    					calSlot.put("startTime", UtilDateTime.timeStampToString(entry.getTimestamp("fromDate"), "yyyy-MM-dd HH:mm", TimeZone.getDefault(), Locale.getDefault()));
    					calSlot.put("endTime", UtilDateTime.timeStampToString(entry.getTimestamp("thruDate"), "yyyy-MM-dd HH:mm", TimeZone.getDefault(), Locale.getDefault()));
    					
    					selectedCalSlot.put(DataUtil.getPartyUserLoginId(delegator, entry.getString("partyId")), calSlot);
    				}
    				return selectedCalSlot;
    			}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return null;
	}
	
	public static GenericValue getCalBooking(Delegator delegator, String partyId, String domainEntityId) {
        try {
        	List conditionList = FastList.newInstance();
        	
			conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
			conditionList.add(EntityCondition.makeCondition("availabilityTypeId", EntityOperator.EQUALS, "RESA_TYP_NON_AVAIL"));
			
			conditionList.add(EntityCondition.makeCondition("domainEntityType", EntityOperator.EQUALS, "ACTIVITY"));
			conditionList.add(EntityCondition.makeCondition("domainEntityId", EntityOperator.EQUALS, domainEntityId));
			
			EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			GenericValue calBooking = EntityUtil.getFirst(delegator.findList("ResourceAvailability", mainConditons, null, null, null, false));
    		return calBooking;
        } catch (Exception e) {
        	Debug.logError(e, MODULE);
        }
        return null;
    }
	
	public static void executeEscalationEmail(Map<String, Object> context) {
		try {
			if (UtilValidate.isNotEmpty(context)) {
				
				Map<String, Object> callCtxt = FastMap.newInstance();
				Map<String, Object> callResult = FastMap.newInstance();
				
				Delegator delegator = (Delegator) context.get("delegator");
				LocalDispatcher dispatcher = (LocalDispatcher) context.get("dispatcher");
				GenericValue userLogin = (GenericValue) context.get("userLogin");
				
				GenericValue workEffort = (GenericValue) context.get("workEffort");
				
				String partyId = (String) context.get("partyId");
				String custRequestId = (String) context.get("custRequestId");
				String workEffortId = workEffort.getString("workEffortId");
				
				Timestamp scheduleStartDate = workEffort.getTimestamp("estimatedStartDate"); 
				Timestamp scheduleEndDate = workEffort.getTimestamp("estimatedCompletionDate"); 
				
				Timestamp endDate = (Timestamp) context.get("endDate");
				
				String emailType = null;
				if (endDate.before(scheduleEndDate)) {
					emailType = "ACT_ERL_CMPL";
				} else if (endDate.after(scheduleEndDate)) {
					emailType = "ACT_LTE_CMPL";
				}
				
				String templateId = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, emailType+"_TPL");
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
					
					GenericValue csr = SrUtil.getSrAssocParty(delegator, custRequestId, "CUST_SERVICE_REP");
					
					if (UtilValidate.isEmpty(csr)) {
						Debug.logError("CSR not found: custRequestId:"+custRequestId+", emailType: "+emailType, MODULE);
						return;
					}
					
					//String fromEmail = org.groupfio.common.portal.util.UtilContactMech.getPartyEmail(delegator, partyId, null);
					String fromEmail = defaultFromEmailId;
					
					String toEmail = org.groupfio.common.portal.util.UtilContactMech.getPartyEmail(delegator, csr.getString("partyId"), null);
					//String toEmail = "sislam131@gmail.com";
					
					if (UtilValidate.isEmpty(toEmail)) {
						Debug.logError("toEmail not found: custRequestId:"+custRequestId+", emailType: "+emailType, MODULE);
						return;
					}
					
					// prepare email content [start]
					Map<String, Object> extractContext = new LinkedHashMap<String, Object>();
					extractContext.put("delegator", delegator);
					extractContext.put("extractType", ExtractType.EXTRACT_EMAIL_DATA);
					extractContext.put("fromEmail", fromEmail);
					extractContext.put("toEmail", toEmail);
					extractContext.put("custRequestId", custRequestId);
					extractContext.put("partyId", partyId);
					extractContext.put("workEffortId", workEffortId);
					extractContext.put("emailContent", templateFormContent);
					extractContext.put("templateId", templateId);

					Map<String, Object> extractResultContext = ExtractFacade.extractData(extractContext);
					emailContent = (String) extractResultContext.get("emailContent");
					// prepare email content [end]

					Map<String, Object> requestContext = FastMap.newInstance();

					requestContext.put("nsender", fromEmail);
					requestContext.put("nto", toEmail);
					requestContext.put("subject", template.getString("subject"));
					requestContext.put("emailContent", emailContent);
					requestContext.put("templateId", templateId);
					//requestContext.put("ccAddresses", ccAddresses);

					callCtxt.put("requestContext", requestContext);
					callCtxt.put("userLogin", userLogin);

					Debug.log("===== ACTIVITY ESCALATION EMAIL TRIGGER ===="+callCtxt);

					dispatcher.runAsync("common.sendEmail", callCtxt);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
	}
	
	public static void executeTaskCompleteEmail(Map<String, Object> context) {
		try {
			if (UtilValidate.isNotEmpty(context)) {
				
				Map<String, Object> callCtxt = FastMap.newInstance();
				Map<String, Object> callResult = FastMap.newInstance();
				
				Delegator delegator = (Delegator) context.get("delegator");
				LocalDispatcher dispatcher = (LocalDispatcher) context.get("dispatcher");
				GenericValue userLogin = (GenericValue) context.get("userLogin");
				
				GenericValue workEffort = (GenericValue) context.get("workEffort");
				
				String partyId = (String) context.get("partyId");
				String custRequestId = (String) context.get("custRequestId");
				String workEffortId = workEffort.getString("workEffortId");
				
				Timestamp scheduleStartDate = workEffort.getTimestamp("estimatedStartDate"); 
				Timestamp scheduleEndDate = workEffort.getTimestamp("estimatedCompletionDate"); 
				
				Timestamp endDate = (Timestamp) context.get("endDate");
				
				String emailType = "ACT_CMPL";
				
				String templateId = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, emailType+"_TPL");
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
					
					/*GenericValue csr = SrUtil.getSrAssocParty(delegator, custRequestId, "CUST_SERVICE_REP");
					
					if (UtilValidate.isEmpty(csr)) {
						Debug.logError("CSR not found: custRequestId:"+custRequestId+", emailType: "+emailType, MODULE);
						return;
					}*/
					
					List conditions = new ArrayList<>();
					
					conditions.add(EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId));
					conditions.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "CONTACT"));
					GenericValue contact = EntityQuery.use(delegator).select("partyId").from("WorkEffortContact").where(EntityCondition.makeCondition(conditions, EntityOperator.AND)).filterByDate().cache(false).queryFirst();
					
					if (UtilValidate.isEmpty(contact)) {
						Debug.logError("Contact not found: custRequestId:"+custRequestId+", emailType: "+emailType, MODULE);
						return;
					}
					//String fromEmail = org.groupfio.common.portal.util.UtilContactMech.getPartyEmail(delegator, partyId, null);
					String fromEmail = defaultFromEmailId;
					
					String toEmail = org.groupfio.common.portal.util.UtilContactMech.getPartyEmail(delegator, contact.getString("partyId"), null);
					//String toEmail = "sislam131@gmail.com";
					
					if (UtilValidate.isEmpty(toEmail)) {
						Debug.logError("toEmail not found: custRequestId:"+custRequestId+", emailType: "+emailType, MODULE);
						return;
					}
					String ccAdd="";
					GenericValue csr = EntityQuery.use(delegator).from("CustRequest").where("custRequestId", custRequestId).queryFirst();
					if(UtilValidate.isNotEmpty(csr)) {
						String responsiblePerson = csr.getString("responsiblePerson");
						String responsiblePersonId = DataUtil.getPartyIdByUserLoginId(delegator, responsiblePerson);
						String ownerEmail = org.groupfio.common.portal.util.UtilContactMech.getPartyEmail(delegator, responsiblePersonId, null);
						ccAdd = UtilValidate.isNotEmpty(ownerEmail) ? ownerEmail : "";
						if(UtilValidate.isNotEmpty(responsiblePersonId)) {
							Map<String, String> backupCoordinatorInfo = SrUtil.getBackupCoordinatorInfo(delegator, responsiblePersonId);
							if(UtilValidate.isNotEmpty(backupCoordinatorInfo)) {
								ccAdd = UtilValidate.isNotEmpty(backupCoordinatorInfo.get("EmailAddress")) ? ","+ backupCoordinatorInfo.get("EmailAddress") : "";
							}
						}
					}
					
					// prepare email content [start]
					Map<String, Object> extractContext = new LinkedHashMap<String, Object>();
					extractContext.put("delegator", delegator);
					extractContext.put("extractType", ExtractType.EXTRACT_EMAIL_DATA);
					extractContext.put("fromEmail", fromEmail);
					extractContext.put("toEmail", toEmail);
					extractContext.put("custRequestId", custRequestId);
					extractContext.put("partyId", partyId);
					extractContext.put("workEffortId", workEffortId);
					extractContext.put("emailContent", templateFormContent);
					extractContext.put("templateId", templateId);

					Map<String, Object> extractResultContext = ExtractFacade.extractData(extractContext);
					emailContent = (String) extractResultContext.get("emailContent");
					// prepare email content [end]

					Map<String, Object> requestContext = FastMap.newInstance();

					requestContext.put("nsender", fromEmail);
					requestContext.put("nto", toEmail);
					requestContext.put("subject", template.getString("subject"));
					requestContext.put("emailContent", emailContent);
					requestContext.put("templateId", templateId);
					requestContext.put("ccAddresses", ccAdd);

					callCtxt.put("requestContext", requestContext);
					callCtxt.put("userLogin", userLogin);

					Debug.log("===== ACTIVITY COMPLETE EMAIL TRIGGER ===="+callCtxt);

					dispatcher.runAsync("common.sendEmail", callCtxt);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void executeUnscheduledTaskCompleteEmail(Map<String, Object> context) {
		try {
			if (UtilValidate.isNotEmpty(context)) {
				
				Map<String, Object> callCtxt = FastMap.newInstance();
				Map<String, Object> callResult = FastMap.newInstance();
				
				Delegator delegator = (Delegator) context.get("delegator");
				LocalDispatcher dispatcher = (LocalDispatcher) context.get("dispatcher");
				GenericValue userLogin = (GenericValue) context.get("userLogin");
				
				GenericValue workEffort = (GenericValue) context.get("workEffort");
				
				String partyId = (String) context.get("partyId");
				String custRequestId = (String) context.get("custRequestId");
				String workEffortId = workEffort.getString("workEffortId");
				
				Timestamp scheduleStartDate = workEffort.getTimestamp("estimatedStartDate"); 
				Timestamp scheduleEndDate = workEffort.getTimestamp("estimatedCompletionDate"); 
				
				Timestamp endDate = (Timestamp) context.get("endDate");
				
				String emailType = "ACT_UN_SCH_CMPL";
				
				String templateId = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, emailType+"_TPL");
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
					
					//GenericValue csr = SrUtil.getSrAssocParty(delegator, custRequestId, "CUST_SERVICE_REP");
					/*if (UtilValidate.isEmpty(csr)) {
						Debug.logError("CSR not found: custRequestId:"+custRequestId+", emailType: "+emailType, MODULE);
						return;
					}*/
					List conditions = new ArrayList<>();
					
					conditions.add(EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId));
					conditions.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "CONTACT"));
					GenericValue contact = EntityQuery.use(delegator).select("partyId").from("WorkEffortContact").where(EntityCondition.makeCondition(conditions, EntityOperator.AND)).filterByDate().cache(false).queryFirst();
					
					if (UtilValidate.isEmpty(contact)) {
						Debug.logError("Contact not found: custRequestId:"+custRequestId+", emailType: "+emailType, MODULE);
						return;
					}
					
					//String fromEmail = org.groupfio.common.portal.util.UtilContactMech.getPartyEmail(delegator, partyId, null);
					String fromEmail = defaultFromEmailId;
					
					String toEmail = org.groupfio.common.portal.util.UtilContactMech.getPartyEmail(delegator, contact.getString("partyId"), null);
					//String toEmail = "sislam131@gmail.com";
					
					if (UtilValidate.isEmpty(toEmail)) {
						Debug.logError("toEmail not found: custRequestId:"+custRequestId+", emailType: "+emailType, MODULE);
						return;
					}
					
					// prepare email content [start]
					Map<String, Object> extractContext = new LinkedHashMap<String, Object>();
					extractContext.put("delegator", delegator);
					extractContext.put("extractType", ExtractType.EXTRACT_EMAIL_DATA);
					extractContext.put("fromEmail", fromEmail);
					extractContext.put("toEmail", toEmail);
					extractContext.put("custRequestId", custRequestId);
					extractContext.put("partyId", partyId);
					extractContext.put("workEffortId", workEffortId);
					extractContext.put("emailContent", templateFormContent);
					extractContext.put("templateId", templateId);

					Map<String, Object> extractResultContext = ExtractFacade.extractData(extractContext);
					emailContent = (String) extractResultContext.get("emailContent");
					// prepare email content [end]

					Map<String, Object> requestContext = FastMap.newInstance();

					requestContext.put("nsender", fromEmail);
					requestContext.put("nto", toEmail);
					requestContext.put("subject", template.getString("subject"));
					requestContext.put("emailContent", emailContent);
					requestContext.put("templateId", templateId);
					//requestContext.put("ccAddresses", ccAddresses);

					callCtxt.put("requestContext", requestContext);
					callCtxt.put("userLogin", userLogin);

					Debug.log("===== UNSCHEDULED ACTIVITY COMPLETE EMAIL TRIGGER ===="+callCtxt);

					dispatcher.runAsync("common.sendEmail", callCtxt);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
	}
	
	public static boolean isTechnicianStartedActivity(Delegator delegator, String workEffortId, String partyId) {
        try {
        	if (UtilValidate.isNotEmpty(partyId)) {
        		//validate to skip if third party actvitiy
        		String workEffortAttr = org.fio.homeapps.util.UtilActivity.getActivityAttrValue(delegator, workEffortId, "IS_3_PARTY_ACTIVITY");
        		if(UtilValidate.isNotEmpty(workEffortAttr) && "Y".equals(workEffortAttr))
        			return true;
        		
        		List conditionList = FastList.newInstance();
            	
        		conditionList.add(EntityCondition.makeCondition("domainEntityType", EntityOperator.EQUALS, "ACTIVITY"));
        		conditionList.add(EntityCondition.makeCondition("domainEntityId", EntityOperator.EQUALS, workEffortId));
        		conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
        		conditionList.add(EntityCondition.makeCondition("startDate", EntityOperator.NOT_EQUAL, null));

            	EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
    			long count = delegator.findCountByCondition("ResourceAvailability", mainConditons, null, null);
    			if (count > 0) {
        			return true;
        		}
        	}
        } catch (Exception e) {
        	Debug.logError(e, MODULE);
        }
        return false;
    }
	
}
