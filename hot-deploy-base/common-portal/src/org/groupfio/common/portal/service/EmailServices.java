package org.groupfio.common.portal.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.fio.homeapps.ResponseCodes;
import org.fio.homeapps.constants.GlobalConstants;
import org.fio.homeapps.util.ParamUtil;
import org.groupfio.common.portal.extractor.ExtractFacade;
import org.groupfio.common.portal.extractor.constants.ExtractorConstants.ExtractType;
import org.groupfio.common.portal.util.UtilCommon;
import org.groupfio.common.portal.util.UtilContactMech;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.ByteWrapper;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastMap;

/**
 * @author Sharif
 *
 */
public class EmailServices {

	private static final String MODULE = EmailServices.class.getName();
	private static final String RESOURCE = org.ofbiz.common.email.EmailServices.resource;
	
	/**
     * Common Email Service, if sendgrid configured then use sendgrid to send email OR use default ofbiz engine 
     * @param ctx The DispatchContext that this service is operating in
     * @param context Map containing the input parameters
     * @return Map with the result of the service, the output parameters
     */
    public static Map<String, Object> sendMail(DispatchContext dctx, Map<String, ? extends Object> context) {
    	LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        Locale locale = (Locale) context.get("locale");
        Map<String, Object> sendMailResult;
        
    	try {
    		String emailEngine = (String) context.get("emailEngine");
    		String defaultEmailEngine = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "DEFAULT_EMAIL_ENGINE");
    		if (UtilValidate.isEmpty(emailEngine) && UtilValidate.isNotEmpty(defaultEmailEngine)) {
    			emailEngine = defaultEmailEngine;
    		}
    		
    		if ( (UtilValidate.isNotEmpty(emailEngine) && emailEngine.equals("SENDGRID"))
    				) {
    			sendMailResult = dispatcher.runSync("sendgrid.sendMail", context);
    		} else if ( (UtilValidate.isNotEmpty(emailEngine) && emailEngine.equals("POSTALSERVER"))
    				) {
    			sendMailResult = dispatcher.runSync("postalserver.sendMail", context);
    		} else {
    			sendMailResult = org.ofbiz.common.email.EmailServices.sendMail(dctx, context);
    		}
    	} catch (Exception e) {
            return ServiceUtil.returnError(UtilProperties.getMessage(RESOURCE, "CommonEmailSendError", UtilMisc.<String, Object>toMap("errorString", e.toString()), locale));
        }
    	return sendMailResult;
    }
    
    public static Map<String, Object> sendMailFromScreen(DispatchContext dctx, Map<String, ? extends Object> context) {
    	LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        Locale locale = (Locale) context.get("locale");
        Map<String, Object> sendMailResult;
        
    	try {
    		String emailEngine = (String) context.get("emailEngine");
    		String defaultEmailEngine = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "DEFAULT_EMAIL_ENGINE");
    		if (UtilValidate.isEmpty(emailEngine) && UtilValidate.isNotEmpty(defaultEmailEngine)) {
    			emailEngine = defaultEmailEngine;
    		}
    		
    		if ( (UtilValidate.isNotEmpty(emailEngine) && emailEngine.equals("SENDGRID"))
    				) {
    			sendMailResult = dispatcher.runSync("sendgrid.sendMailFromScreen", context);
    		} else if ( (UtilValidate.isNotEmpty(emailEngine) && emailEngine.equals("POSTALSERVER"))
    				) {
    			sendMailResult = dispatcher.runSync("postalserver.sendMailFromScreen", context);
    		} else {
    			sendMailResult = org.ofbiz.common.email.EmailServices.sendMailFromScreen(dctx, context);
    		}
    	} catch (Exception e) {
            return ServiceUtil.returnError(UtilProperties.getMessage(RESOURCE, "CommonEmailSendError", UtilMisc.<String, Object>toMap("errorString", e.toString()), locale));
        }
    	return sendMailResult;
    }
    
    /**
     * Common Email Service, if sendgrid configured then use sendgrid to send email OR use default ofbiz engine 
     * @param ctx The DispatchContext that this service is operating in
     * @param context Map containing the input parameters
     * @return Map with the result of the service, the output parameters
     */
    public static Map<String, Object> sendMailMultiPart(DispatchContext dctx, Map<String, ? extends Object> context) {
    	LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        Locale locale = (Locale) context.get("locale");
        Map<String, Object> sendMailResult;
        
    	try {
    		String emailEngine = (String) context.get("emailEngine");
    		String defaultEmailEngine = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "DEFAULT_EMAIL_ENGINE");
    		if (UtilValidate.isEmpty(emailEngine) && UtilValidate.isNotEmpty(defaultEmailEngine)) {
    			emailEngine = defaultEmailEngine;
    		}
    		
    		if ( (UtilValidate.isNotEmpty(emailEngine) && emailEngine.equals("SENDGRID"))
    				) {
    			sendMailResult = dispatcher.runSync("sendgrid.sendMailMultiPart", context);
    		} else if ( (UtilValidate.isNotEmpty(emailEngine) && emailEngine.equals("POSTALSERVER"))
    				) {
    			sendMailResult = dispatcher.runSync("postalserver.sendMailMultiPart", context);
    		} else {
    			sendMailResult = org.ofbiz.common.email.EmailServices.sendMail(dctx, context);
    		}
    	} catch (Exception e) {
            return ServiceUtil.returnError(UtilProperties.getMessage(RESOURCE, "CommonEmailSendError", UtilMisc.<String, Object>toMap("errorString", e.toString()), locale));
        }
    	return sendMailResult;
    }
	
	public static Map<String, Object> sendEmail(DispatchContext dctx, Map context) {
		LocalDispatcher dispatcher = dctx.getDispatcher();
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	Locale locale = (Locale) context.get("locale");

		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		Map requestContext = (Map) context.get("requestContext");
		
		String communicationEventId = (String) requestContext.get("communicationEventId");
		String emailContent = (String) requestContext.get("emailContent");
		String emplTeamId = (String) requestContext.get("emplTeamId");
		String ccAddresses = (String) requestContext.get("ccAddresses");
		
		String senderName = (String) requestContext.get("senderName");
		String emailEngine = (String) requestContext.get("emailEngine");
		String personalizationTags = (String) requestContext.get("personalizationTags");
		String referenceId = (String) requestContext.get("referenceId");
		String referenceType = (String) requestContext.get("referenceType");
		String templateId = (String) requestContext.get("templateId");
		String extTplId = (String) requestContext.get("extTplId");
		String clientName = (String) requestContext.get("clientName");
		String intContactListId = (String) requestContext.get("intContactListId");
		String emailPurposeTypeId = (String) requestContext.get("emailPurposeTypeId");
		String partyId = (String) requestContext.get("partyId");
		
		Map<String, Object> callCtxt = FastMap.newInstance();
		Map<String, Object> callResult = FastMap.newInstance();

		try {
			
			// attachment upload [start]
			
			callCtxt = FastMap.newInstance();
			
			callCtxt.put("requestContext", requestContext);
			callCtxt.put("userLogin", userLogin);
			
			callResult = dispatcher.runSync("common.uploadFile", callCtxt);
			if (ServiceUtil.isError(callResult)) {
				return callResult; 
			}
			
			// attachment upload [end]
			
			// send email [start]
			
			if (UtilValidate.isNotEmpty(templateId)) {
				GenericValue template = delegator.findOne("TemplateMaster", UtilMisc.toMap("templateId", templateId), false);
				if (UtilValidate.isNotEmpty(template)) {
					if (UtilValidate.isEmpty(emailEngine) && UtilValidate.isNotEmpty(template.getString("emailEngine"))) {
						emailEngine = template.getString("emailEngine").replace("_ENGINE", "");
					}
				}
			}
			
			Map<String, Object> inContext = new HashMap<String, Object>();
			
			if (UtilValidate.isEmpty(partyId) && UtilValidate.isNotEmpty(emplTeamId)) {
				partyId = emplTeamId;
			}
			
			inContext.put("partyId", partyId);
			inContext.put("body", emailContent);
			inContext.put("sendTo", requestContext.get("nto"));
			inContext.put("contentType", requestContext.get("contentType"));
			inContext.put("sendFrom", requestContext.get("nsender"));
			inContext.put("sendCc", ccAddresses);
			inContext.put("sendBcc", requestContext.get("nbcc"));
			inContext.put("subject", requestContext.get("subject"));
			inContext.put("senderName", senderName);
			
			inContext.put("emailEngine", emailEngine);
			inContext.put("personalizationTags", personalizationTags);
			inContext.put("referenceId", referenceId);
			inContext.put("referenceType", referenceType);
			inContext.put("templateId", templateId);
			inContext.put("extTplId", extTplId);
			inContext.put("intContactListId", intContactListId);
			inContext.put("clientName", clientName);
			inContext.put("emailPurposeTypeId", emailPurposeTypeId);
			
			// Assemble the body and the attachments into a list of body parts
            List < Map < String, Object >> attachments = new ArrayList < Map < String, Object >> ();
            List < GenericValue > commEventContentDataResources = delegator.findByAnd("CommEventContentDataResource", UtilMisc.toMap("communicationEventId", communicationEventId), null, false);
            //commEventContentDataResources = EntityUtil.filterByDate(commEventContentDataResources);
            Iterator < GenericValue > cecait = commEventContentDataResources.iterator();
            while (cecait.hasNext()) {
                GenericValue commEventContentDataResource = cecait.next();
                String dataResourceId = commEventContentDataResource.getString("dataResourceId");
                String mimeTypeId = commEventContentDataResource.getString("drMimeTypeId");
                String fileName = commEventContentDataResource.getString("drDataResourceName");
                Map < String, Object > attachment = UtilMisc. < String, Object > toMap("type", mimeTypeId, "filename", fileName);
                try {
                    ByteWrapper byteWrapper = UtilCommon.getContentAsByteWrapper(delegator, dataResourceId, null, null, locale, null);
                    attachment.put("content", byteWrapper.getBytes());
                } catch (Exception e) {
                	String errMsg = "Send Email Unable To Get Data Resource: "+e.getMessage();
					Debug.logError(errMsg, MODULE);
                }
                attachments.add(attachment);
            }
            Debug.logInfo("attachments size: "+attachments.size(), MODULE);
            
            String emailServiceName = "sendMail";
            if (UtilValidate.isNotEmpty(attachments)) {
            	inContext.remove("body");
                // Construct the list of parts so that the message body is first, just in case some email clients break
                List < Map < String, Object >> bodyParts = UtilMisc.toList(UtilMisc.toMap("content", emailContent, "type", "text/html"));
                bodyParts.addAll(attachments);
                inContext.put("bodyParts", bodyParts);
                emailServiceName = "sendMailMultiPart";
            }
			
            inContext.put("userLogin", userLogin);
            
			try {
				callResult = dispatcher.runSync(emailServiceName, inContext);
			} catch (GenericServiceException e) {
				e.printStackTrace();
				String errMsg = "Problem sending mail: " + e.toString();
				result.putAll(ServiceUtil.returnError(errMsg));
    			return result;
			}
			
			// send email [end]
			
		} catch (Exception e) {
			e.printStackTrace();
			result.put("errorMsg", e.getMessage());
			result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
		result.put("resultMap", resultMap);
		result.putAll(ServiceUtil.returnSuccess("Successfully send email"));
		return result;
	}
	
	public static Map<String, Object> sendInvoicePayment(DispatchContext dctx, Map context) {
		LocalDispatcher dispatcher = dctx.getDispatcher();
    	Delegator delegator = (Delegator) dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	Locale locale = (Locale) context.get("locale");

		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> responseContext = new HashMap<String, Object>();
		
		Map requestContext = (Map) context.get("requestContext");
		
		String invoiceId = (String) requestContext.get("invoiceId");
    	String workEffortId = (String) requestContext.get("workEffortId");
    	String domainEntityId = (String) requestContext.get("domainEntityId");
    	String sendCc = (String) requestContext.get("sendCc");
		
		Map<String, Object> callCtxt = FastMap.newInstance();
		Map<String, Object> callResult = FastMap.newInstance();

		GenericValue inv = null;
		try {
			String xslfoAttachScreenLocation = "component://accounting/widget/AccountingPrintScreens.xml#InvoicePDF";
			
			if(UtilValidate.isNotEmpty(invoiceId)) {
    			inv = EntityQuery.use(delegator).from("Invoice")
    					.where("invoiceId", invoiceId)
    					.queryFirst();
    		}
			
    		String templateId = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "INV_PYMT_EMAIL_TPL");
    		
    		if(UtilValidate.isNotEmpty(templateId)) {
    			String hostedInvoiceUrl = org.groupfio.common.portal.util.UtilAttribute.getAttrValue(delegator, "InvoiceAttribute", "invoiceId", invoiceId, "hosted_invoice_url");
        		
        		GenericValue tpl = delegator.findOne("TemplateMaster", UtilMisc.toMap("templateId", templateId), false);
        		
    			//String nsender = tpl.getString("senderEmail");
        		String nsender = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "FROM_EMAIL_ID");
    			String nto = UtilContactMech.getPartyEmail(delegator, inv.getString("partyId"), null);
    			String subject = "Invoice# "+invoiceId+" - "+"Pay Online";
    			
    			//nto = "sislam131@gmail.com";
    			
    			if (UtilValidate.isEmpty(nto)) {
    				String errMsg = "Primary email address not found for party# "+inv.getString("partyId");
    				Debug.logError(errMsg, MODULE);
    				responseContext.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
    				responseContext.put(GlobalConstants.RESPONSE_MESSAGE, errMsg);
    				result.put("responseContext", responseContext);
    				result.putAll(ServiceUtil.returnSuccess());
    				return result;
    			}
    			
    			String emailContent = "";
    			String templateFormContent = tpl.getString("templateFormContent");
    			if (UtilValidate.isNotEmpty(templateFormContent)) {
    				if (org.apache.commons.codec.binary.Base64.isBase64(templateFormContent)) {
    					templateFormContent = org.ofbiz.base.util.Base64.base64Decode(templateFormContent);
    				}
    			}
    			
    			List<Object> personalizedTagList = new ArrayList<>();
    			Map<String, Object> tag = new LinkedHashMap<>();
        		tag.put("TAG_NAME", "INVOICE_ID");
        		tag.put("TAG_VALUE", invoiceId);
        		//personalizedTagList.add(tag);
        		
        		tag = new LinkedHashMap<>();
        		tag.put("TAG_NAME", "RECEIPT_URL");
        		tag.put("TAG_VALUE", hostedInvoiceUrl);
        		//personalizedTagList.add(tag);
    			
    			String attachmentName = invoiceId;
    			String personalizationTags = ParamUtil.toJson(personalizedTagList);
    		
    			// prepare email content [start]
    			Map<String, Object> extractContext = new LinkedHashMap<String, Object>();
    			extractContext.put("delegator", delegator);
    			extractContext.put("extractType", ExtractType.EXTRACT_EMAIL_DATA);
    			extractContext.put("fromEmail", nsender);
    			extractContext.put("toEmail", nto);
    			extractContext.put("partyId", inv.getString("partyId"));
    			extractContext.put("emailContent", templateFormContent);
    			extractContext.put("personalizationTags", personalizationTags);
    			extractContext.put("invoiceId", invoiceId);
    			extractContext.put("receiptUrl", hostedInvoiceUrl);

    			Map<String, Object> extractResultContext = ExtractFacade.extractData(extractContext);
    			personalizationTags = (String) extractResultContext.get("personalizationTags");
    			emailContent = (String) extractResultContext.get("emailContent");
    			// prepare email content [end]

    			callCtxt = FastMap.newInstance();
    			callResult = FastMap.newInstance();
    			//Map<String, Object> requestContext = FastMap.newInstance();
    			
    			Map<String, Object> paramMap = new LinkedHashMap<>();
    			paramMap.put("locale", locale);
        		paramMap.put("userLogin", userLogin);
        		paramMap.put("invoiceId", invoiceId);
        		//paramMap.put("workEffortId", arg1);
        		//paramMap.put("domainEntityId", arg1);

    			callCtxt.put("bodyParameters", paramMap);
    			callCtxt.put("sendFrom", nsender);
    			callCtxt.put("sendTo", nto);
    			callCtxt.put("sendCc", sendCc);
    			callCtxt.put("subject", subject);
    			callCtxt.put("bodyText", emailContent);
    			callCtxt.put("partyId", inv.getString("partyId"));
    			callCtxt.put("templateId", templateId);
    			callCtxt.put("personalizationTags", personalizationTags);
    			
    			//callCtxt.put("requestContext", requestContext);
    			callCtxt.put("contentType", "ATTACHMENT");
    			callCtxt.put("attachmentName", attachmentName+".pdf");
    			callCtxt.put("xslfoAttachScreenLocation", xslfoAttachScreenLocation);
    			callCtxt.put("userLogin", userLogin);

    			//Debug.log("===== Payment Success EMAIL TRIGGER ===="+callCtxt);
    			//dispatcher.runAsync("sendMailFromScreen", callCtxt);

    			callResult = dispatcher.runSync("sendMailFromScreen", callCtxt);
    			if (ServiceUtil.isError(callResult)) {
    				String errMsg = "Email send failed: "+ServiceUtil.getErrorMessage(callResult);
    				Debug.logError(errMsg, MODULE);
    			}
    		}
    		
		} catch (Exception e) {
			e.printStackTrace();
			result.put("errorMsg", e.getMessage());
			result.putAll(ServiceUtil.returnError(e.getMessage()));
			return result;
		}
		result.put("responseContext", responseContext);
		result.putAll(ServiceUtil.returnSuccess("Successfully send invoice payment email"));
		return result;
	}
    
}
