package org.fio.customer.service.service.update;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.fio.homeapps.util.DataUtil;
import org.fio.homeapps.util.ResponseUtils;
import org.fio.homeapps.writer.WriterUtil;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;

import org.json.JSONArray;
import org.json.JSONObject;


public class CustomerOnlineServices {

	private static final String MODULE = CustomerOnlineServices.class.getName();

	private static String channelAccessId;

	public static String getCustomerUpdateUrl(Delegator delegator) {

		String url ="";
		try {
			GenericValue channelDet= EntityUtil.getFirst(delegator.findByAnd("OfbizApiConfiguration", UtilMisc.toMap("attrName","updateCustomerOnlineUrl"), null, true));
			if (UtilValidate.isNotEmpty(channelDet)) {
				url = channelDet.getString("attrValue");
				channelAccessId = channelDet.getString("channelAccessId");
			}
		}catch (Exception e) {
			// TODO: handle exception
			Debug.log("getCustomerUpdateUrl--$$-"+e);
		}
		return url;

	}

	public static String getApikey(Delegator delegator) {

		String key ="";
		try {
			GenericValue channelDet= EntityUtil.getFirst(delegator.findByAnd("ChannelAccess", UtilMisc.toMap("channelAccessId",channelAccessId), null, true));
			if (UtilValidate.isNotEmpty(channelDet)) {
				key = channelDet.getString("userName");
			}
		}catch (Exception e) {
			// TODO: handle exception
		}
		return key;
	}
	public static String getExternalPartyId(Delegator delegator,String partyId) {

		String externalId ="";
		try {
			if(UtilValidate.isNotEmpty(channelAccessId) && channelAccessId.equalsIgnoreCase("INDICA_POS")) {
				GenericValue partyidentification= EntityUtil.getFirst(delegator.findByAnd("PartyIdentification", 
						UtilMisc.toMap("partyId",partyId,"partyIdentificationTypeId","PATIENT_ID"), null, true));

				if (UtilValidate.isNotEmpty(partyidentification)) {
					externalId = partyidentification.getString("idValue");
				}
			}
		}catch (Exception e) {
			// TODO: handle exception
		}
		return externalId;
	}
	@SuppressWarnings({ "unchecked", "unused" })
	public static Map<String, Object> updateCustomerToOnline(DispatchContext dctx, Map<String, Object> context) {
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = (Delegator) dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Locale locale = (Locale) context.get("locale");

		Map<String, Object> result = ServiceUtil.returnSuccess();
		Map<String, Object> responseContext = ServiceUtil.returnSuccess();
		Map<String, Object> requestContext = (Map<String, Object>) context.get("requestContext");
		String partyId = (String) requestContext.get("partyId");
		String email = (String) requestContext.get("email");
		String phone = (String) requestContext.get("phone");
		
		String enableUpdateOperation = DataUtil.getGlobalValue(delegator, "ENABLE_CUST_UPDATE_API_ONLINE", "N");
		if (UtilValidate.isNotEmpty(enableUpdateOperation) && enableUpdateOperation.equalsIgnoreCase("N")) {
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
			responseContext.put("apiResponseStatus", "Not Enabled");
			result.put("responseContext",responseContext);
			return result;
		}
		String customerUpdateUrl =getCustomerUpdateUrl(delegator);//"https://api.indicaonline.com/open_api/v2.1/customers/";

		String apiKey = getApikey(delegator);

		String externalId = getExternalPartyId(delegator, partyId);
		Timestamp requestedTime = UtilDateTime.nowTimestamp();
		Timestamp responsedTime = null;
		String responseStatus="";
		JSONObject requestBody = new JSONObject();
		String response="";
		try {

			if(UtilValidate.isEmpty(apiKey) || UtilValidate.isEmpty(customerUpdateUrl) || UtilValidate.isEmpty(externalId)) {
				result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
				responseContext.put("apiResponseStatus", "Required Field missing");
				result.put("responseContext",responseContext);
				return result;
			}
			
			Map<String,Object> getResult = getCustomerFromOnlineById(dctx, UtilMisc.toMap("requestContext",UtilMisc.toMap("externalId",externalId,"requestUrl",customerUpdateUrl,"apiKey",apiKey),"userLogin",userLogin));
			if (UtilValidate.isNotEmpty(getResult) && ServiceUtil.isSuccess(getResult)) {
				String getResponseBody = (String) getResult.get("responseBody");
				if (UtilValidate.isNotEmpty(getResponseBody) && org.groupfio.common.portal.util.DataUtil.isJSONValid(getResponseBody)) {
					JSONObject data = new JSONObject(getResponseBody);
					requestBody = data;
				}else {
					result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
				}
			}else {
				result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
				responseContext.put("apiResponseStatus", "FAILED");
				result.put("responseContext",responseContext);
				return result;
			}
			// Build the request body
			boolean hasChanges= false;
			if (UtilValidate.isNotEmpty(requestBody)) {
				String currEmail = (String) requestBody.get("email");
				String currPhone = (String) requestBody.get("phone");
				if (UtilValidate.isNotEmpty(currPhone)) {
					currPhone = currPhone.replaceAll("[^0-9]", "");
					requestBody.put("phone", currPhone);
				}
				
				if (UtilValidate.isNotEmpty(email) && (UtilValidate.isEmpty(currEmail)
						|| (UtilValidate.isNotEmpty(currEmail) && !currEmail.equalsIgnoreCase(email)))) {
					requestBody.put("email", email);
					hasChanges = true;
				}
				if (UtilValidate.isNotEmpty(phone) && (UtilValidate.isEmpty(currPhone)
						|| (UtilValidate.isNotEmpty(currPhone) && !currPhone.equalsIgnoreCase(phone)))) {
					phone = phone.replaceAll("[^0-9]", "");
					requestBody.put("phone", phone);
					hasChanges = true;
				}else if (UtilValidate.isEmpty(phone) && UtilValidate.isEmpty(currPhone)) {
					List condList = FastList.newInstance();
					condList.add(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,partyId));
					condList.add(EntityCondition.makeCondition("contactMechTypeId",EntityOperator.EQUALS,"TELECOM_NUMBER"));
					condList.add(EntityUtil.getFilterByDateExpr("fromDate", "thruDate"));
					EntityCondition cond = EntityCondition.makeCondition(condList,EntityOperator.AND);
					GenericValue partyAndMechs = EntityUtil.getFirst(delegator.findList("PartyAndContactMech", cond, UtilMisc.toSet("contactMechId"), null, null, false));
					if (UtilValidate.isNotEmpty(partyAndMechs)) {
						String contactMechId = partyAndMechs.getString("contactMechId");
						GenericValue telecomNumber = delegator.findOne("TelecomNumber", false, UtilMisc.toMap("contactMechId",contactMechId));
						if (UtilValidate.isNotEmpty(telecomNumber)) {
							String contactNumber = telecomNumber.getString("contactNumber");
							if (UtilValidate.isNotEmpty(contactNumber)) {
								contactNumber = contactNumber.replaceAll("[^0-9]", "");
								if (contactNumber.length()==10) {
									requestBody.put("phone", contactNumber);
									hasChanges = true;
								}else {
									String areaCode = telecomNumber.getString("areaCode");
									if (UtilValidate.isNotEmpty(areaCode)) {
										areaCode = areaCode.replaceAll("[^0-9]", "");
										contactNumber = areaCode+""+contactNumber;
										if (UtilValidate.isNotEmpty(contactNumber) && contactNumber.length()==10) {
											requestBody.put("phone", contactNumber);
											hasChanges = true;
										}
									}
									
								}
							}
						}
					}
				}
			}
			if(UtilValidate.isEmpty(requestBody) || !hasChanges) {
				result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
				responseContext.put("apiResponseStatus", "No Change");
				result.put("responseContext",responseContext);
				return result;
			}
			List requiredFields = FastList.newInstance();
			requiredFields.add("first_name");
			requiredFields.add("last_name");
			requiredFields.add("date_of_birth");
			requiredFields.add("phone");
			requiredFields.add("has_medical_recommendation");
			boolean reqFields = true;
			String fieledMissing = "";
			for (int i=0;i<requiredFields.size();i++) {
				String idVal = (String) requiredFields.get(i);
				Object val = (Object) requestBody.get(idVal);
				if(UtilValidate.isEmpty(val)) {
					fieledMissing = idVal;
					reqFields = false;
					break;
				}
			}
			if(!reqFields) {
				result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
				responseContext.put("apiResponseStatus", "ERROR");
				responseContext.put("responseStatusMsg", "Required Field Missing:"+fieledMissing);
				result.put("responseContext",responseContext);
				return result;
			}
			
			Map<String,Object> updateResult = sendUpdateRequest(dctx, UtilMisc.toMap("requestContext",UtilMisc.toMap("externalId",externalId,"requestUrl",customerUpdateUrl,"apiKey",apiKey,"requestBody",requestBody.toString()),"userLogin",userLogin));
			Debug.log("-updateResult----"+updateResult);
			if (UtilValidate.isNotEmpty(updateResult)) {
				responseStatus = (String) updateResult.get("responseStatus");
			}
			
			}catch (Exception e) {
			// TODO: handle exception
			Debug.log("updateCustomer #Error----------"+e);
			String msg = e.getMessage();
			responsedTime = UtilDateTime.nowTimestamp();
			//result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			responseStatus = "ERROR";
			WriterUtil.writeLog(delegator, "updateCustomerToOnline", "", userLogin.getString("userLoginId"), ""+requestBody, ""+msg, "", "ERROR", null, requestedTime, responsedTime, null, null, channelAccessId);
		}
		responseContext.put("apiResponseStatus",responseStatus);
		result.put("responseContext",responseContext);
		return result;
	}
	
	@SuppressWarnings({ "unchecked", "unused" })
	public static Map<String, Object> sendUpdateRequest(DispatchContext dctx, Map<String, Object> context) {
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = (Delegator) dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Locale locale = (Locale) context.get("locale");

		Map<String, Object> result = ServiceUtil.returnSuccess();
		Map<String, Object> requestContext = (Map<String, Object>) context.get("requestContext");
		String externalId = (String) requestContext.get("externalId");
		String url = (String) requestContext.get("requestUrl");
		String apiKey = (String) requestContext.get("apiKey");
		String requestBody = (String) requestContext.get("requestBody");
		Timestamp requestedTime = UtilDateTime.nowTimestamp();
		Timestamp responsedTime = null;
		String responseStatus="";
		String response="";
		try {
			// Build the request body

			URI uri = new URIBuilder(url+externalId).build();
			
			HttpPut httpPatch = new HttpPut(uri);

			CloseableHttpClient httpClient = HttpClients.custom().build();
			httpPatch.setHeader("X-Api-Key", apiKey);
			httpPatch.setHeader("Accept", "application/json");
			httpPatch.setHeader("Content-Type", "application/json");
			StringEntity httpEntity = new StringEntity(requestBody);
			httpPatch.setEntity(httpEntity);
			
			int statusCode = 0;
			CloseableHttpResponse patcResponse = httpClient.execute(httpPatch);
			response = new BasicResponseHandler().handleResponse(patcResponse);
			responsedTime = UtilDateTime.nowTimestamp();
			statusCode = patcResponse.getStatusLine().getStatusCode();

			if (patcResponse.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED) {
				result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
				responseStatus="SUCCESS";
			}else {
				result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
				responseStatus="FAILED";
			}
			WriterUtil.writeLog(delegator, "updateCustomerToOnline", "", userLogin.getString("userLoginId"), ""+requestBody, response, ""+statusCode, responseStatus, null, requestedTime, responsedTime, null, null, channelAccessId);
		}catch (Exception e) {
			// TODO: handle exception
			Debug.log("updateCustomer Get #Error----------"+e);
			String msg = e.getMessage();
			responsedTime = UtilDateTime.nowTimestamp();
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			WriterUtil.writeLog(delegator, "updateCustomerToOnline_GET", "", userLogin.getString("userLoginId"), externalId, ""+msg, "", "ERROR", null, requestedTime, responsedTime, null, null, channelAccessId);
		}
		result.put("responseStatus", responseStatus);
		return result;
	}
	
	@SuppressWarnings({ "unchecked", "unused" })
	public static Map<String, Object> getCustomerFromOnlineById(DispatchContext dctx, Map<String, Object> context) {
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = (Delegator) dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Locale locale = (Locale) context.get("locale");

		Map<String, Object> result = ServiceUtil.returnSuccess();
		Map<String, Object> requestContext = (Map<String, Object>) context.get("requestContext");
		String externalId = (String) requestContext.get("externalId");
		String url = (String) requestContext.get("requestUrl");
		String apiKey = (String) requestContext.get("apiKey");

		Timestamp requestedTime = UtilDateTime.nowTimestamp();
		Timestamp responsedTime = null;
		String responseStatus="";
		String response="";
		try {
			// Build the request body

			if(UtilValidate.isEmpty(apiKey) || UtilValidate.isEmpty(url) || UtilValidate.isEmpty(externalId)) {
				result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
				return result;
			}

			URI uri = new URIBuilder(url+externalId).build();
			Debug.log("uri----------"+uri);
			
			HttpGet httpPatch = new HttpGet(uri);
			CloseableHttpClient httpClient = HttpClients.custom().build();
			httpPatch.setHeader("X-Api-Key", apiKey);
			httpPatch.setHeader("Accept", "application/json");
			httpPatch.setHeader("Content-Type", "application/json");

			CloseableHttpResponse patcResponse = httpClient.execute(httpPatch);
			response = new BasicResponseHandler().handleResponse(patcResponse);
			responsedTime = UtilDateTime.nowTimestamp();
			int statusCode = patcResponse.getStatusLine().getStatusCode();

			//Debug.log("-response----"+response+"--patcResponse---"+patcResponse+"--statusCode--"+statusCode);
			
			if (patcResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
				responseStatus="SUCCESS";
			}else {
				result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
				responseStatus="FAILED";
			}

			WriterUtil.writeLog(delegator, "updateCustomerToOnline_GET", "", userLogin.getString("userLoginId"), uri, response, ""+statusCode, responseStatus, null, requestedTime, responsedTime, null, null, channelAccessId);
		}catch (Exception e) {
			// TODO: handle exception
			Debug.log("updateCustomer Get #Error----------"+e);
			String msg = e.getMessage();
			responsedTime = UtilDateTime.nowTimestamp();
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			WriterUtil.writeLog(delegator, "updateCustomerToOnline_GET", "", userLogin.getString("userLoginId"), externalId, ""+msg, "", "ERROR", null, requestedTime, responsedTime, null, null, channelAccessId);
		}
		result.put("responseBody", response);
		return result;
	}

}
