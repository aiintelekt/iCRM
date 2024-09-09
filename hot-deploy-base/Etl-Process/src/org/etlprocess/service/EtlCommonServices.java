package org.etlprocess.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.etlprocess.util.EtlXmlUtil;
import org.json.JSONException;
import org.json.XML;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilXml;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javolution.util.FastList;

public class EtlCommonServices {
	
	public static String etlTableName = UtilProperties.getPropertyValue("Etl-Process", "CUSTOMER_TABLE");
	
	
	public static Map<String, Object>createEtlPreProcessor(DispatchContext dctx, Map<String, ? extends Object> context)
	{
		Delegator delegator = dctx.getDelegator();

		try {
			
			String batchId = (String) context.get("batchId");
			String modelId = (String) context.get("modelId");
			String accessType = (String) context.get("accessType");
			String etlTableName = (String) context.get("etlTableName");
			GenericValue userLogin =  (GenericValue) context.get("userLogin");
			String userLoginId = "";
			if(UtilValidate.isNotEmpty(userLogin))
			 userLoginId = userLogin.getString("userLoginId");
			
			GenericValue makeProcessor = delegator.makeValue("EtlPreProcessor");
			makeProcessor.put("batchId", batchId);
			makeProcessor.put("modelId", modelId);
			if(UtilValidate.isNotEmpty(modelId)){
				GenericValue getModel = EntityUtil.getFirst(delegator.findByAnd("EtlModel",UtilMisc.toMap("modelName",modelId),null,false));
				makeProcessor.put("modelId", getModel.getString("modelId"));
			}
			makeProcessor.put("accessType", accessType);
			makeProcessor.put("etlTableName", etlTableName);
			makeProcessor.put("statusId", "CREATED");
			makeProcessor.put("createdBy", userLoginId);
			TransactionUtil.begin();
			makeProcessor.create();
			TransactionUtil.commit();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			/*e.printStackTrace();*/
			return ServiceUtil.returnError(e.toString());
		}
		return ServiceUtil.returnSuccess();
	}
	public static Map<String, Object>EtlSelfServiceImpl(DispatchContext dctx, Map<String, ? extends Object> context)
	{
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher disp = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");

		try {
			 List<GenericValue> findJobs = delegator.findByAnd("EtlPreProcessor", UtilMisc.toMap("statusId","CREATED"), null, false);
			 Debug.log("++++++++++++++++++++EtlSelfServiceImpl+++++++++++++++++++"+findJobs);
			 if(UtilValidate.isNotEmpty(findJobs)){
				 for(GenericValue job : findJobs){
					 String etlTable = job.getString("etlTableName");
					 String batchId = job.getString("batchId");
					 if(etlTableName.equals(etlTable)){
						 Map<String,Object> inputNew = new HashMap<String,Object>();
				   		 inputNew.put("userLogin",userLogin);
				   		 inputNew.put("initialResponsiblePartyId","admin");
				   		 inputNew.put("organizationPartyId","Company");
				   		 inputNew.put("batchId",batchId);
						 Map<String, Object> result = disp.runSync("importCustomers", inputNew);
						 Debug.log("++++++++++++++++++++EtlSelfServiceImpl+++++++++++++++++success++"+result);
						 if(ServiceUtil.isSuccess(result)){
							 job.put("statusId", "FINISHED");
							 job.store();
							 Debug.log("++++++++++++++++++++EtlSelfServiceImpl+++++++++++++++++success++");
						 }
					 }
				 }
			 }
		} catch (Exception e) {
			Debug.log("++++++++++++++++++++EtlSelfServiceImpl+++++++++++++++++++"+e.toString());
			// TODO Auto-generated catch block
			/*e.printStackTrace();*/
			return ServiceUtil.returnError(e.toString());
		}
		return ServiceUtil.returnSuccess();
	}
	
	/**
	 * @author Mahendran T
	 * @since 2017-07-07
	 * */
	public static Map<String,Object> findModelByType(DispatchContext dctx,Map<String, ? extends Object> context)
	{
		LocalDispatcher dispatcher = (LocalDispatcher) dctx.getDispatcher();
		Delegator delegator = (Delegator) dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		StringBuilder xmlOutput = new StringBuilder();
		String xmlInput = (String) context.get("XmlInput");
		Map<String, Object> result = ServiceUtil.returnSuccess();
		List<Object> errorList = FastList.newInstance();
		Document xmlDocument = null;
		String modelType = null;
		
		String jsonInput = xmlInput;
		
		//Json to XML Conversion
		if(UtilValidate.isNotEmpty(xmlInput) && (EtlXmlUtil.isValidJson(xmlInput))){
			try {
				org.json.JSONObject json = new org.json.JSONObject(xmlInput);
				String xml = XML.toString(json);
				xmlInput = xml;
				//System.out.println("*****XML*****"+xmlInput);
				Debug.log("*****XML*****"+xmlInput);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				/*e.printStackTrace();*/
			}
		
		}

		Document responseXmlDoc = UtilXml.makeEmptyXmlDocument("RESPONSE");
		Element responseXmlElement = responseXmlDoc.getDocumentElement();
		
		String logId = delegator.getNextSeqId("OrderApiLog");
		GenericValue OrderApi = delegator.makeValue("OrderApiLog", UtilMisc.toMap("logId", logId));
		OrderApi.put("requestId", logId);
		OrderApi.put("responseId", logId);
		OrderApi.put("requestString", context.toString());
		OrderApi.put("requestDate", UtilDateTime.nowTimestamp());
		try {
			OrderApi.create();
		} catch (GenericEntityException e) {

		}
		UtilXml.addChildElementValue(responseXmlElement, "RESPONSE_DATE", UtilDateTime.nowTimestamp().toString(), responseXmlDoc);
		try{
			if (xmlInput == null || xmlInput.equals("")) {
				UtilXml.addChildElementValue(responseXmlElement, "RESPONSE_CODE", "E101", responseXmlDoc);
				result.put("XmlOutput", EtlXmlUtil.cunstructXml(responseXmlDoc,OrderApi, "getEtlModel" ,jsonInput));
				return result;

			}

			try {
				xmlDocument = UtilXml.readXmlDocument(xmlInput, false);
			} catch (SAXException se) {
				UtilXml.addChildElementValue(responseXmlElement, "RESPONSE_CODE", "E102", responseXmlDoc);
				result.put("XmlOutput", EtlXmlUtil.cunstructXml(responseXmlDoc, OrderApi, "getEtlModel",jsonInput));
				return result;
			} catch (ParserConfigurationException pce) {
				UtilXml.addChildElementValue(responseXmlElement, "RESPONSE_CODE", "E103", responseXmlDoc);
				result.put("XmlOutput", EtlXmlUtil.cunstructXml(responseXmlDoc, OrderApi, "getEtlModel",jsonInput));
				return result;
			} catch (IOException ioe) {
				UtilXml.addChildElementValue(responseXmlElement, "RESPONSE_CODE", "E104", responseXmlDoc);
				result.put("XmlOutput", EtlXmlUtil.cunstructXml(responseXmlDoc, OrderApi, "getEtlModel",jsonInput));
				return result;
			}

			Element rootElement = xmlDocument.getDocumentElement();
			EtlXmlUtil.handleErrors(rootElement, errorList);

			if (UtilValidate.isNotEmpty(errorList)) {
				return ServiceUtil.returnError(errorList);
			}

			List<? extends Element> elementList = UtilXml.childElementList(rootElement);

			for(Element subrootElement : elementList){
				if (subrootElement.getLocalName().equalsIgnoreCase("MODEL")) {
					List<? extends Element> reqElementList = UtilXml.childElementList(subrootElement);
					for(Element reqsubRootElement : reqElementList){
						String elementName = reqsubRootElement.getLocalName();
						String elemenValue = UtilXml.elementValue(reqsubRootElement);

						if("MODEL_TYPE".equalsIgnoreCase(elementName)){
							modelType = elemenValue;
						}
					}
				}
			}

			if(UtilValidate.isNotEmpty(modelType)){
				GenericValue etlDestination = EntityUtil.getFirst(delegator.findByAnd("EtlDestination", UtilMisc.toMap("tableTitle",modelType), null, false));
				String tableName = "";
				if(UtilValidate.isNotEmpty(etlDestination)){
					tableName = etlDestination.getString("tableName");
					
					if(UtilValidate.isNotEmpty(tableName)){
						String sqlQuery = "select list_Name from Etl_Source_Table where table_Name='"+tableName+"' group by list_Name";
						List<String> listNames = EtlXmlUtil.runSqlQuery(sqlQuery, delegator);
						if(listNames.size() > 0){
							for(String lisName : listNames){
								List<EntityCondition> conditionlist = FastList.newInstance();
								conditionlist.add(EntityCondition.makeCondition(EntityOperator.OR,
										EntityCondition.makeCondition("isExport",EntityOperator.EQUALS,null),
										EntityCondition.makeCondition("isExport",EntityOperator.EQUALS,"N"),
										EntityCondition.makeCondition("isExport",EntityOperator.EQUALS,"")
										));
								
								conditionlist.add(EntityCondition.makeCondition("modelName",EntityOperator.EQUALS,lisName));
								EntityCondition condition = EntityCondition.makeCondition(conditionlist,EntityOperator.AND);
								
								List<GenericValue> etlModelList = delegator.findList("EtlModel", condition, UtilMisc.toSet("modelId","modelName","createdTxStamp"), null, null, false);
								if(etlModelList.size() > 0){
									for(GenericValue gv : etlModelList){
										String modelId = gv.getString("modelId");
										String modelName = gv.getString("modelName");
										String createdDate = gv.getString("createdTxStamp");
										
										Element modelElement = UtilXml.addChildElement(responseXmlElement, "MODEL", responseXmlDoc);
										
										UtilXml.addChildElementValue(modelElement, "MODEL_ID", StringUtils.defaultIfEmpty(modelId, ""), responseXmlDoc);
										UtilXml.addChildElementValue(modelElement, "MODEL_NAME", StringUtils.defaultIfEmpty(modelName, ""), responseXmlDoc);
										UtilXml.addChildElementValue(modelElement, "CREATED_DATE", StringUtils.defaultIfEmpty(createdDate, ""), responseXmlDoc);
									}
								}
							}
						}
						else {
							UtilXml.addChildElementValue(responseXmlElement, "RESPONSE_CODE", "E108", responseXmlDoc);
							result.put("XmlOutput", EtlXmlUtil.cunstructXml(responseXmlDoc, OrderApi, "getEtlModel",jsonInput));
							return result;
						}
					} else{
						UtilXml.addChildElementValue(responseXmlElement, "RESPONSE_CODE", "E107", responseXmlDoc);
						result.put("XmlOutput", EtlXmlUtil.cunstructXml(responseXmlDoc, OrderApi, "getEtlModel",jsonInput));
						return result;
					}
				} else{
					UtilXml.addChildElementValue(responseXmlElement, "RESPONSE_CODE", "E106", responseXmlDoc);
					result.put("XmlOutput", EtlXmlUtil.cunstructXml(responseXmlDoc, OrderApi, "getEtlModel",jsonInput));
					return result;
				}
				
			} else{
				UtilXml.addChildElementValue(responseXmlElement, "RESPONSE_CODE", "E105", responseXmlDoc);
				result.put("XmlOutput", EtlXmlUtil.cunstructXml(responseXmlDoc, OrderApi, "getEtlModel",jsonInput));
				return result;
			}
		}catch(Exception e){
			/*e.printStackTrace();*/
		}

		result.put("XmlOutput", EtlXmlUtil.cunstructXml(responseXmlDoc, OrderApi, "getEtlModel",jsonInput));
		return result;
	}
	
	/**
	 * @author Mahendran T
	 * @since 2017-07-07
	 * */
	public static Map<String, Object> findModelByIdOrName(DispatchContext dctx,Map<String, ? extends Object> context)
	{
		LocalDispatcher dispatcher = (LocalDispatcher) dctx.getDispatcher();
		Delegator delegator = (Delegator) dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		StringBuilder xmlOutput = new StringBuilder();
		String xmlInput = (String) context.get("XmlInput");
		Map<String, Object> result = ServiceUtil.returnSuccess();
		List<Object> errorList = FastList.newInstance();
		Document xmlDocument = null;
		String modelId = null;
		String modelName = null;
		
		String jsonInput = xmlInput;
		
		//Json to XML Conversion
		if(UtilValidate.isNotEmpty(xmlInput) && (EtlXmlUtil.isValidJson(xmlInput))){
			try {
				org.json.JSONObject json = new org.json.JSONObject(xmlInput);
				String xml = XML.toString(json);
				xmlInput = xml;
				//System.out.println("*****XML*****"+xmlInput);
				Debug.log("*****XML*****"+xmlInput);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				/*e.printStackTrace();*/
			}
		
		}

		Document responseXmlDoc = UtilXml.makeEmptyXmlDocument("RESPONSE");
		Element responseXmlElement = responseXmlDoc.getDocumentElement();
		String logId = delegator.getNextSeqId("OrderApiLog");
		GenericValue OrderApi = delegator.makeValue("OrderApiLog", UtilMisc.toMap("logId", logId));
		OrderApi.put("requestId", logId);
		OrderApi.put("responseId", logId);
		OrderApi.put("requestString", context.toString());
		OrderApi.put("requestDate", UtilDateTime.nowTimestamp());
		try {
			OrderApi.create();
		} catch (GenericEntityException e) {

		}
		UtilXml.addChildElementValue(responseXmlElement, "RESPONSE_DATE", UtilDateTime.nowTimestamp().toString(), responseXmlDoc);
		try{
			if (xmlInput == null || xmlInput.equals("")) {
				UtilXml.addChildElementValue(responseXmlElement, "RESPONSE_CODE", "E101", responseXmlDoc);
				result.put("XmlOutput", EtlXmlUtil.cunstructXml(responseXmlDoc,OrderApi, "getEtlModel",jsonInput));
				return result;

			}

			try {
				xmlDocument = UtilXml.readXmlDocument(xmlInput, false);
			} catch (SAXException se) {
				UtilXml.addChildElementValue(responseXmlElement, "RESPONSE_CODE", "E102", responseXmlDoc);
				result.put("XmlOutput", EtlXmlUtil.cunstructXml(responseXmlDoc, OrderApi, "getEtlModel",jsonInput));
				return result;
			} catch (ParserConfigurationException pce) {
				UtilXml.addChildElementValue(responseXmlElement, "RESPONSE_CODE", "E103", responseXmlDoc);
				result.put("XmlOutput", EtlXmlUtil.cunstructXml(responseXmlDoc, OrderApi, "getEtlModel",jsonInput));
				return result;
			} catch (IOException ioe) {
				UtilXml.addChildElementValue(responseXmlElement, "RESPONSE_CODE", "E104", responseXmlDoc);
				result.put("XmlOutput", EtlXmlUtil.cunstructXml(responseXmlDoc, OrderApi, "getEtlModel",jsonInput));
				return result;
			}

			Element rootElement = xmlDocument.getDocumentElement();
			EtlXmlUtil.handleErrors(rootElement, errorList);

			if (UtilValidate.isNotEmpty(errorList)) {
				return ServiceUtil.returnError(errorList);
			}

			List<? extends Element> elementList = UtilXml.childElementList(rootElement);

			for(Element subrootElement : elementList){
				if (subrootElement.getLocalName().equalsIgnoreCase("MODEL")) {
					List<? extends Element> reqElementList = UtilXml.childElementList(subrootElement);
					for(Element reqsubRootElement : reqElementList){
						String elementName = reqsubRootElement.getLocalName();
						String elemenValue = UtilXml.elementValue(reqsubRootElement);

						if("MODEL_ID".equalsIgnoreCase(elementName)){
							modelId = elemenValue;
						}
						if("MODEL_NAME".equalsIgnoreCase(elementName)){
							modelName = elemenValue;
						}
					}
				}
			}

			if(UtilValidate.isNotEmpty(modelId) || UtilValidate.isNotEmpty(modelName)){
				
				List<EntityCondition> conditionlist = FastList.newInstance();
				conditionlist.add(EntityCondition.makeCondition(EntityOperator.OR,
						EntityCondition.makeCondition("isExport",EntityOperator.EQUALS,null),
						EntityCondition.makeCondition("isExport",EntityOperator.EQUALS,"N"),
						EntityCondition.makeCondition("isExport",EntityOperator.EQUALS,"")
						));
				if(UtilValidate.isNotEmpty(modelId)){
					conditionlist.add(EntityCondition.makeCondition("modelId",EntityOperator.EQUALS,modelId));
				}
				
				if(UtilValidate.isNotEmpty(modelName)){
					conditionlist.add(EntityCondition.makeCondition("modelName",EntityOperator.EQUALS,modelName));
				}
				
				
				EntityCondition condition = EntityCondition.makeCondition(conditionlist,EntityOperator.AND);
				
				GenericValue etlModelList = EntityUtil.getFirst(delegator.findList("EtlModel", condition, UtilMisc.toSet("modelId","modelName","createdTxStamp"), null, null, false));
				if(UtilValidate.isNotEmpty(etlModelList)){
						String mId = etlModelList.getString("modelId");
						String mName = etlModelList.getString("modelName");
						String createdDate = etlModelList.getString("createdTxStamp");
						
						List<GenericValue> etlSourceTable = delegator.findByAnd("EtlSourceTable",UtilMisc.toMap("listName", mName),UtilMisc.toList("tableColumnName ASC"),false);
						if(UtilValidate.isNotEmpty(etlSourceTable)){
							Element modelElement = UtilXml.addChildElement(responseXmlElement, "MODEL", responseXmlDoc);
							Element definitionElement = UtilXml.addChildElement(modelElement, "DEFINITION", responseXmlDoc);
							UtilXml.addChildElementValue(modelElement, "MODEL_ID", StringUtils.defaultIfEmpty(mId, ""), responseXmlDoc);
							UtilXml.addChildElementValue(modelElement, "MODEL_NAME", StringUtils.defaultIfEmpty(mName, ""), responseXmlDoc);
							UtilXml.addChildElementValue(modelElement, "CREATED_DATE", StringUtils.defaultIfEmpty(createdDate, ""), responseXmlDoc);
							
							for(GenericValue gv: etlSourceTable){
								if(UtilValidate.isNotEmpty(gv.getString("tableColumnName")))
								UtilXml.addChildElementValue(definitionElement, gv.getString("tableColumnName"), StringUtils.defaultIfEmpty(gv.getString("etlFieldName"), ""), responseXmlDoc);
							}
						}
						else {
							UtilXml.addChildElementValue(responseXmlElement, "RESPONSE_CODE", "E106", responseXmlDoc);
							result.put("XmlOutput", EtlXmlUtil.cunstructXml(responseXmlDoc, OrderApi, "getEtlModel",jsonInput));
							return result;
						}
						
				} else {
					UtilXml.addChildElementValue(responseXmlElement, "RESPONSE_CODE", "E107", responseXmlDoc);
					result.put("XmlOutput", EtlXmlUtil.cunstructXml(responseXmlDoc, OrderApi, "getEtlModel",jsonInput));
					return result;
				}
				
			} else{
				UtilXml.addChildElementValue(responseXmlElement, "RESPONSE_CODE", "E105", responseXmlDoc);
				result.put("XmlOutput", EtlXmlUtil.cunstructXml(responseXmlDoc, OrderApi, "getEtlModel",jsonInput));
				return result;
			}
		}catch(Exception e){
			/*e.printStackTrace();*/
		}
		result.put("XmlOutput", EtlXmlUtil.cunstructXml(responseXmlDoc, OrderApi, "getEtlModel",jsonInput));
		return result;
	}

}
