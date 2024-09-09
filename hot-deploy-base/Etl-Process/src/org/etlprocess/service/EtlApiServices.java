package org.etlprocess.service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.etlprocess.upload.ETLConstants;
import org.etlprocess.util.EtlXmlUtil;
import org.groupfio.etl.process.job.AccountImportJob;
import org.groupfio.etl.process.job.CategoryImportJob;
import org.groupfio.etl.process.job.CustomerImportJob;
import org.groupfio.etl.process.job.InvoiceHeaderImportJob;
import org.groupfio.etl.process.job.InvoiceItemImportJob;
import org.groupfio.etl.process.job.OrderImportJob;
import org.groupfio.etl.process.job.ProductImportJob;
import org.groupfio.etl.process.job.SupplierImportJob;
import org.json.JSONException;
import org.json.XML;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilXml;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javolution.util.FastList;
import javolution.util.FastMap;

/**
 * @author Mahendran T
 * @since 2017-07-13
 * */
public class EtlApiServices {
	public static String accessType = "WEB_API";
	public static int incrementValue = 1;
	public static Map<String, Object> executeEtlModelService(DispatchContext dctx,Map<String, ? extends Object> context)
	{
		LocalDispatcher dispatcher = (LocalDispatcher) dctx.getDispatcher();
		Delegator delegator = (Delegator) dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		
		if(UtilValidate.isEmpty(userLogin)){
			 try {
				userLogin =delegator.findOne("UserLogin",  UtilMisc.toMap("userLoginId","admin"), false);
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		StringBuilder xmlOutput = new StringBuilder();
		String xmlInput = (String) context.get("XmlInput");
		Map<String, Object> result = ServiceUtil.returnSuccess();
		List<Object> errorList = FastList.newInstance();
		Document xmlDocument = null;
		String modelId = null;
		String modelName = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHMMSS");

		String validateModelName = "";
		String validateModelDef = "";


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
				result.put("XmlOutput", EtlXmlUtil.cunstructXml(responseXmlDoc,OrderApi, "executeEtlModelService",jsonInput));
				return result;

			}

			try {
				xmlDocument = UtilXml.readXmlDocument(xmlInput, false);
			} catch (SAXException se) {
				UtilXml.addChildElementValue(responseXmlElement, "RESPONSE_CODE", "E102", responseXmlDoc);
				result.put("XmlOutput", EtlXmlUtil.cunstructXml(responseXmlDoc, OrderApi, "executeEtlModelService",jsonInput));
				return result;
			} catch (ParserConfigurationException pce) {
				UtilXml.addChildElementValue(responseXmlElement, "RESPONSE_CODE", "E103", responseXmlDoc);
				result.put("XmlOutput", EtlXmlUtil.cunstructXml(responseXmlDoc, OrderApi, "executeEtlModelService",jsonInput));
				return result;
			} catch (IOException ioe) {
				UtilXml.addChildElementValue(responseXmlElement, "RESPONSE_CODE", "E104", responseXmlDoc);
				result.put("XmlOutput", EtlXmlUtil.cunstructXml(responseXmlDoc, OrderApi, "executeEtlModelService",jsonInput));
				return result;
			}

			Element rootElement = xmlDocument.getDocumentElement();
			EtlXmlUtil.handleErrors(rootElement, errorList);

			if (UtilValidate.isNotEmpty(errorList)) {
				return ServiceUtil.returnError(errorList);
			}


			// validate the required data
			List<? extends Element> validateElementList = UtilXml.childElementList(rootElement);

			for(Element subrootElement : validateElementList){
				if (subrootElement.getLocalName().equalsIgnoreCase("MODEL")) {
					List<? extends Element> reqElementList = UtilXml.childElementList(subrootElement);
					for(Element reqsubRootElement : reqElementList){
						String elementName = reqsubRootElement.getLocalName();
						String elemenValue = UtilXml.elementValue(reqsubRootElement);
						if("MODEL_NAME".equalsIgnoreCase(elementName)){
							validateModelName = elemenValue;
						}
						else if("DEFINITIONS".equalsIgnoreCase(elementName)){
							List<? extends Element> rootDefinitionElements = UtilXml.childElementList(reqsubRootElement);
							for(Element rootDefinitionElement : rootDefinitionElements){
								String rootDefinitionName = rootDefinitionElement.getLocalName();
								String rootDefinitionValue = UtilXml.elementValue(rootDefinitionElement);
								if (rootDefinitionName.equalsIgnoreCase("DEFINITION")) {
									List<? extends Element> definitionElementList = UtilXml.childElementList(rootDefinitionElement);
									for(Element definitionElement : definitionElementList){
										validateModelDef = UtilXml.elementValue(definitionElement);
										break;

									}
								}
							}
						}
					}
				}
			}
			if(UtilValidate.isEmpty(validateModelName)){
				UtilXml.addChildElementValue(responseXmlElement, "RESPONSE_CODE", "E109", responseXmlDoc);
				result.put("XmlOutput", EtlXmlUtil.cunstructXml(responseXmlDoc, OrderApi, "executeEtlModelService",jsonInput));
				return result;
			}

			if(UtilValidate.isEmpty(validateModelDef)){
				UtilXml.addChildElementValue(responseXmlElement, "RESPONSE_CODE", "E108", responseXmlDoc);
				result.put("XmlOutput", EtlXmlUtil.cunstructXml(responseXmlDoc, OrderApi, "executeEtlModelService",jsonInput));
				return result;
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
						else if("MODEL_NAME".equalsIgnoreCase(elementName)){
							modelName = elemenValue;
						}
						else if("DEFINITIONS".equalsIgnoreCase(elementName)){

							// validate the model
							List<GenericValue> etlSourceTableList = null;
							String tableName = "";
							String serviceName = "";
							String batchId = "";
							List<String> tableFieldList = FastList.newInstance();

							if(UtilValidate.isEmpty(modelName)){
								modelName = validateModelName;
							}
							List<EntityCondition> conditionlist = FastList.newInstance();
							conditionlist.add(EntityCondition.makeCondition(EntityOperator.OR,
									EntityCondition.makeCondition("isExport",EntityOperator.EQUALS,null),
									EntityCondition.makeCondition("isExport",EntityOperator.EQUALS,"N"),
									EntityCondition.makeCondition("isExport",EntityOperator.EQUALS,"")
									));


							if(UtilValidate.isNotEmpty(modelName)){
								conditionlist.add(EntityCondition.makeCondition("modelName",EntityOperator.EQUALS,validateModelName));
							}


							EntityCondition condition = EntityCondition.makeCondition(conditionlist,EntityOperator.AND);

							GenericValue etlModelList = EntityUtil.getFirst(delegator.findList("EtlModel", condition, UtilMisc.toSet("modelId","modelName"), null, null, false));
							if(UtilValidate.isNotEmpty(etlModelList)){
								String mId = etlModelList.getString("modelId");
								String mName = etlModelList.getString("modelName");
								etlSourceTableList = delegator.findByAnd("EtlSourceTable",UtilMisc.toMap("listName", mName),UtilMisc.toList("tableColumnName ASC"),false);
								if(UtilValidate.isNotEmpty(etlSourceTableList)){
									tableFieldList = EntityUtil.getFieldListFromEntityList(etlSourceTableList, "tableColumnName", true);
									tableName = etlSourceTableList.get(0).getString("tableName");
								}

							} else{
								UtilXml.addChildElementValue(responseXmlElement, "RESPONSE_CODE", "E110", responseXmlDoc);
								result.put("XmlOutput", EtlXmlUtil.cunstructXml(responseXmlDoc, OrderApi, "executeEtlModelService",jsonInput));
								return result;
							}

							GenericValue checkProcess = EntityUtil.getFirst(delegator.findByAnd("EtlProcess",UtilMisc.toMap("modalName",modelName),null,false));
							serviceName = checkProcess.getString("serviceName");
							if(UtilValidate.isNotEmpty(tableName)){

								List<? extends Element> rootDefinitionElements = UtilXml.childElementList(reqsubRootElement);
								for(Element rootDefinitionElement : rootDefinitionElements){
									String rootDefinitionName = rootDefinitionElement.getLocalName();
									String rootDefinitionValue = UtilXml.elementValue(rootDefinitionElement);
									Date date = new Date();
									batchId = dateFormat.format(date);
									if (rootDefinitionName.equalsIgnoreCase("DEFINITION")) {
										List<? extends Element> definitionElementList = UtilXml.childElementList(rootDefinitionElement);
										String definitionName ="";
										String definitionValue ="";
										Map<String, Object> dataCtx = new HashMap<String, Object>();
										for(Element definitionElement : definitionElementList){
											definitionName = definitionElement.getLocalName();
											definitionValue = UtilXml.elementValue(definitionElement);
											if(tableFieldList.contains(definitionName)){
												dataCtx.put(definitionName, definitionValue);
											}
										}


										Map<String,Object> serResult = FastMap.newInstance();
										if("DataImportSupplier".equals(tableName)){
											if(!ETLConstants.ETL_SUPP_IMP_SER.equals(serviceName) && UtilValidate.isNotEmpty(serviceName)){
												UtilXml.addChildElementValue(responseXmlElement, "RESPONSE_CODE", "E111", responseXmlDoc);
												result.put("XmlOutput", EtlXmlUtil.cunstructXml(responseXmlDoc, OrderApi, "executeEtlModelService",jsonInput));
												return result;
											}
											//call service
											if(UtilValidate.isNotEmpty(dataCtx)){
												dataCtx.put("listId", modelName);
												dataCtx.put("batchId", batchId);
												//System.out.println("context------>"+context);
												Debug.log("context------>"+context);
												serResult = dispatcher.runSync("createEtlStagingSupplier", dataCtx);
												if(ServiceUtil.isError(result)){
													UtilXml.addChildElementValue(responseXmlElement, "RESPONSE_CODE", "E112", responseXmlDoc);
													result.put("XmlOutput", EtlXmlUtil.cunstructXml(responseXmlDoc, OrderApi, "executeEtlModelService",jsonInput));
													return result;
												}
											}
										} else if("DataImportCustomer".equals(tableName)){
											if(!ETLConstants.ETL_CUST_IMP_SER.equals(serviceName) && UtilValidate.isNotEmpty(serviceName)) {
												UtilXml.addChildElementValue(responseXmlElement, "RESPONSE_CODE", "E111", responseXmlDoc);
												result.put("XmlOutput", EtlXmlUtil.cunstructXml(responseXmlDoc, OrderApi, "executeEtlModelService",jsonInput));
												return result;
											}
											//call service
											if(UtilValidate.isNotEmpty(dataCtx)){
												dataCtx.put("listId", modelName);
												dataCtx.put("batchId", batchId);
												dataCtx.put("incrementValue", incrementValue);
												serResult = dispatcher.runSync("createEtlStagingCustomer", dataCtx);
												if(ServiceUtil.isError(result)){
													UtilXml.addChildElementValue(responseXmlElement, "RESPONSE_CODE", "E112", responseXmlDoc);
													result.put("XmlOutput", EtlXmlUtil.cunstructXml(responseXmlDoc, OrderApi, "executeEtlModelService",jsonInput));
													return result;
												}
											}
										} else if("DataImportContact".equals(tableName)){
											if(!ETLConstants.ETL_CONT_IMP_SER.equals(serviceName) && UtilValidate.isNotEmpty(serviceName)) {
												UtilXml.addChildElementValue(responseXmlElement, "RESPONSE_CODE", "E111", responseXmlDoc);
												result.put("XmlOutput", EtlXmlUtil.cunstructXml(responseXmlDoc, OrderApi, "executeEtlModelService",jsonInput));
												return result;
											}
											//call service
											if(UtilValidate.isNotEmpty(dataCtx)){
												dataCtx.put("listId", modelName);
												dataCtx.put("batchId", batchId);
												dataCtx.put("incrementValue", incrementValue);
												serResult = dispatcher.runSync("createEtlStagingContact", dataCtx);
												if(ServiceUtil.isError(result)){
													UtilXml.addChildElementValue(responseXmlElement, "RESPONSE_CODE", "E112", responseXmlDoc);
													result.put("XmlOutput", EtlXmlUtil.cunstructXml(responseXmlDoc, OrderApi, "executeEtlModelService",jsonInput));
													return result;
												}
											}
										} else if("DataImportAccount".equals(tableName)){
											if(!ETLConstants.ETL_ACC_IMP_SER.equals(serviceName) && UtilValidate.isNotEmpty(serviceName)){
												UtilXml.addChildElementValue(responseXmlElement, "RESPONSE_CODE", "E111", responseXmlDoc);
												result.put("XmlOutput", EtlXmlUtil.cunstructXml(responseXmlDoc, OrderApi, "executeEtlModelService",jsonInput));
												return result;
											}

											//call service
											if(UtilValidate.isNotEmpty(dataCtx)){
												dataCtx.put("listId", modelName);
												dataCtx.put("batchId", batchId);
												dataCtx.put("incrementValue", incrementValue);
												serResult = dispatcher.runSync("createEtlStagingAccount", dataCtx);
												if(ServiceUtil.isError(result)){
													UtilXml.addChildElementValue(responseXmlElement, "RESPONSE_CODE", "E112", responseXmlDoc);
													result.put("XmlOutput", EtlXmlUtil.cunstructXml(responseXmlDoc, OrderApi, "executeEtlModelService",jsonInput));
													return result;
												}
											}

										}else if("DataImportInvoiceHeader".equals(tableName)){
											if(!ETLConstants.ETL_INV_IMP_SER.equals(serviceName) && UtilValidate.isNotEmpty(serviceName)){
												UtilXml.addChildElementValue(responseXmlElement, "RESPONSE_CODE", "E111", responseXmlDoc);
												result.put("XmlOutput", EtlXmlUtil.cunstructXml(responseXmlDoc, OrderApi, "executeEtlModelService",jsonInput));
												return result;
											}
											//call service
											if(UtilValidate.isNotEmpty(dataCtx)){
												dataCtx.put("listId", modelName);
												dataCtx.put("batchId", batchId);
												dataCtx.put("incrementValue", incrementValue);
												serResult = dispatcher.runSync("createEtlStagingInvoiceHeader", dataCtx);
												if(ServiceUtil.isError(result)){
													UtilXml.addChildElementValue(responseXmlElement, "RESPONSE_CODE", "E112", responseXmlDoc);
													result.put("XmlOutput", EtlXmlUtil.cunstructXml(responseXmlDoc, OrderApi, "executeEtlModelService",jsonInput));
													return result;
												}
											}
										}else if("DataImportInvoiceItem".equals(tableName)){
											if(!ETLConstants.ETL_INVITM_IMP_SER.equals(serviceName) && UtilValidate.isNotEmpty(serviceName)){
												UtilXml.addChildElementValue(responseXmlElement, "RESPONSE_CODE", "E111", responseXmlDoc);
												result.put("XmlOutput", EtlXmlUtil.cunstructXml(responseXmlDoc, OrderApi, "executeEtlModelService",jsonInput));
												return result;
											}
											//call service
											if(UtilValidate.isNotEmpty(dataCtx)){
												dataCtx.put("listId", modelName);
												dataCtx.put("batchId", batchId);
												dataCtx.put("incrementValue", incrementValue);
												serResult = dispatcher.runSync("createEtlStagingInvoiceItem", dataCtx);
												if(ServiceUtil.isError(result)){
													UtilXml.addChildElementValue(responseXmlElement, "RESPONSE_CODE", "E112", responseXmlDoc);
													result.put("XmlOutput", EtlXmlUtil.cunstructXml(responseXmlDoc, OrderApi, "executeEtlModelService",jsonInput));
													return result;
												}
											}

										}else if("DataImportProduct".equals(tableName)){
											if(!ETLConstants.ETL_PRO_IMP_SER.equals(serviceName) && UtilValidate.isNotEmpty(serviceName)){
												UtilXml.addChildElementValue(responseXmlElement, "RESPONSE_CODE", "E111", responseXmlDoc);
												result.put("XmlOutput", EtlXmlUtil.cunstructXml(responseXmlDoc, OrderApi, "executeEtlModelService",jsonInput));
												return result;
											}

											//call service
											if(UtilValidate.isNotEmpty(dataCtx)){
												dataCtx.put("listId", modelName);
												dataCtx.put("batchId", batchId);
												dataCtx.put("incrementValue", incrementValue);
												serResult = dispatcher.runSync("createEtlStagingProduct", dataCtx);
												if(ServiceUtil.isError(result)){
													UtilXml.addChildElementValue(responseXmlElement, "RESPONSE_CODE", "E112", responseXmlDoc);
													result.put("XmlOutput", EtlXmlUtil.cunstructXml(responseXmlDoc, OrderApi, "executeEtlModelService",jsonInput));
													return result;
												}
											}

										}else if("DataImportCategory".equals(tableName)){
											if(!ETLConstants.ETL_CAT_IMP_SER.equals(serviceName) && UtilValidate.isNotEmpty(serviceName)){
												UtilXml.addChildElementValue(responseXmlElement, "RESPONSE_CODE", "E111", responseXmlDoc);
												result.put("XmlOutput", EtlXmlUtil.cunstructXml(responseXmlDoc, OrderApi, "executeEtlModelService",jsonInput));
												return result;
											}

											//call service
											if(UtilValidate.isNotEmpty(dataCtx)){
												dataCtx.put("listId", modelName);
												dataCtx.put("batchId", batchId);
												dataCtx.put("incrementValue", incrementValue);
												serResult = dispatcher.runSync("createEtlStagingCategory", dataCtx);
												if(ServiceUtil.isError(result)){
													UtilXml.addChildElementValue(responseXmlElement, "RESPONSE_CODE", "E112", responseXmlDoc);
													result.put("XmlOutput", EtlXmlUtil.cunstructXml(responseXmlDoc, OrderApi, "executeEtlModelService",jsonInput));
													return result;
												}
											}

										}					
										else if("EtlImportOrderFields".equals(tableName) && UtilValidate.isNotEmpty(serviceName)){
											if(!ETLConstants.ETL_ORD_IMP_SER.equals(serviceName)){
												UtilXml.addChildElementValue(responseXmlElement, "RESPONSE_CODE", "E111", responseXmlDoc);
												result.put("XmlOutput", EtlXmlUtil.cunstructXml(responseXmlDoc, OrderApi, "executeEtlModelService",jsonInput));
												return result;
											}

											//call service
											if(UtilValidate.isNotEmpty(dataCtx)){
					           					dataCtx.put("listId", modelName);
					           					dataCtx.put("batchId", batchId);
					           					dataCtx.put("incrementValue", incrementValue);
					           					serResult = dispatcher.runSync("createEtlStagingOrder", dataCtx);
					           					if(ServiceUtil.isError(result)){
					           						UtilXml.addChildElementValue(responseXmlElement, "RESPONSE_CODE", "E112", responseXmlDoc);
													result.put("XmlOutput", EtlXmlUtil.cunstructXml(responseXmlDoc, OrderApi, "executeEtlModelService",jsonInput));
													return result;
					           					}
					           				}

										}else if("EtlOrderFulfillment".equals(tableName)){
											if(!ETLConstants.ETL_ORDER_FULFILL_SER.equals(serviceName)){
												UtilXml.addChildElementValue(responseXmlElement, "RESPONSE_CODE", "E111", responseXmlDoc);
												result.put("XmlOutput", EtlXmlUtil.cunstructXml(responseXmlDoc, OrderApi, "executeEtlModelService",jsonInput));
												return result;
											}
											//call service
											/*if(UtilValidate.isNotEmpty(dataCtx)){
					           					dataCtx.put("listId", modelName);
					           					dataCtx.put("batchId", batchId);
					           					serResult = dispatcher.runSync("", dataCtx);
					           					if(ServiceUtil.isError(result)){
					           						UtilXml.addChildElementValue(responseXmlElement, "RESPONSE_CODE", "E112", responseXmlDoc);
													result.put("XmlOutput", EtlXmlUtil.cunstructXml(responseXmlDoc, OrderApi, "executeEtlModelService",jsonInput));
													return result;
					           					}
					           				}*/
										}



									}
								}

								Map<String,Object> inputNew = new HashMap<String,Object>();
								inputNew.put("userLogin",userLogin);
								inputNew.put("batchId",batchId);
								inputNew.put("modelId",modelName);
								inputNew.put("accessType",accessType);
								inputNew.put("etlTableName",tableName);
								Map<String,Object> Res = dispatcher.runSync("EtlSelfServiceProcessor", inputNew);

								if(ServiceUtil.isSuccess(Res)){
									
									if("DataImportSupplier".equals(tableName)){
										// Trigger Thread
										SupplierImportJob job = new SupplierImportJob();
										job.setDelegator(delegator);
										job.setDispatcher(dispatcher);
										job.setEtlModelId(modelName);
										job.setUserLogin(userLogin);
										job.start();
									} else if("DataImportCustomer".equals(tableName)){
										CustomerImportJob job = new CustomerImportJob();
							   			job.setDelegator(delegator);
							   			job.setDispatcher(dispatcher);
							   			job.setEtlModelId(modelName);
							   			job.setUserLogin(userLogin);
							   			job.start();
									}else if("DataImportAccount".equals(tableName)){
										// Trigger Thread
						   				AccountImportJob job = new AccountImportJob();
							   			job.setDelegator(delegator);
							   			job.setDispatcher(dispatcher);
							   			job.setEtlModelId(modelName);
							   			job.setUserLogin(userLogin);
							   			job.start();

									}else if("DataImportInvoiceHeader".equals(tableName)){
										// Trigger Thread
							   			InvoiceHeaderImportJob job = new InvoiceHeaderImportJob();
							   			job.setDelegator(delegator);
							   			job.setDispatcher(dispatcher);
							   			job.setEtlModelId(modelName);
							   			job.setUserLogin(userLogin);
							   			job.start();
									}else if("DataImportInvoiceItem".equals(tableName)){
										// Trigger Thread
							   			InvoiceItemImportJob job = new InvoiceItemImportJob();
							   			job.setDelegator(delegator);
							   			job.setDispatcher(dispatcher);
							   			job.setEtlModelId(modelName);
							   			job.setUserLogin(userLogin);
							   			job.start();

									}else if("DataImportProduct".equals(tableName)){
										// Trigger Thread
						   				ProductImportJob job = new ProductImportJob();
							   			job.setDelegator(delegator);
							   			job.setDispatcher(dispatcher);
							   			job.setEtlModelId(modelName);
							   			job.setUserLogin(userLogin);
							   			job.start();

									}else if("DataImportCategory".equals(tableName)){
										// Trigger Thread
						   				CategoryImportJob job = new CategoryImportJob();
							   			job.setDelegator(delegator);
							   			job.setDispatcher(dispatcher);
							   			job.setEtlModelId(modelName);
							   			job.setUserLogin(userLogin);
							   			job.start();

									}					
									else if("EtlImportOrderFields".equals(tableName) && UtilValidate.isNotEmpty(serviceName)){
										// Trigger Thread
						   				OrderImportJob job = new OrderImportJob();
							   			job.setDelegator(delegator);
							   			job.setDispatcher(dispatcher);
							   			job.setEtlModelId(modelName);
							   			job.setUserLogin(userLogin);
							   			job.start();

									}else if("EtlOrderFulfillment".equals(tableName)){
										
									}
									
								}	
							} else{
								UtilXml.addChildElementValue(responseXmlElement, "RESPONSE_CODE", "E107", responseXmlDoc);
								result.put("XmlOutput", EtlXmlUtil.cunstructXml(responseXmlDoc, OrderApi, "executeEtlModelService",jsonInput));
								return result;
							}
						}
					}
				}
			}
			UtilXml.addChildElementValue(responseXmlElement, "RESPONSE_CODE", "S101", responseXmlDoc);
			result.put("XmlOutput", EtlXmlUtil.cunstructXml(responseXmlDoc, OrderApi, "executeEtlModelService",jsonInput));
			return result;

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
	public static Map<String,Object> findModelByType(DispatchContext dctx,Map<String, ? extends Object> context)
	{
		LocalDispatcher dispatcher = (LocalDispatcher) dctx.getDispatcher();
		Delegator delegator = (Delegator) dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		StringBuilder xmlOutput = new StringBuilder();
		String xmlInput = (String) context.get("XmlInput");
		Map<String, Object> result = FastMap.newInstance();
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
					if(UtilValidate.isNotEmpty(mName)){
						Element modelElement = UtilXml.addChildElement(responseXmlElement, "MODEL", responseXmlDoc);
						UtilXml.addChildElementValue(modelElement, "MODEL_ID", StringUtils.defaultIfEmpty(mId, ""), responseXmlDoc);
						UtilXml.addChildElementValue(modelElement, "MODEL_NAME", StringUtils.defaultIfEmpty(mName, ""), responseXmlDoc);
						UtilXml.addChildElementValue(modelElement, "CREATED_DATE", StringUtils.defaultIfEmpty(createdDate, ""), responseXmlDoc);
						Element definitionsElement = UtilXml.addChildElement(modelElement, "DEFINITIONS", responseXmlDoc);
						if(UtilValidate.isNotEmpty(etlSourceTable)){
							Element definitionElement = UtilXml.addChildElement(definitionsElement, "DEFINITION", responseXmlDoc);
							for(GenericValue gv: etlSourceTable){
								if(UtilValidate.isNotEmpty(gv.getString("tableColumnName")))
									UtilXml.addChildElementValue(definitionElement, gv.getString("tableColumnName"), StringUtils.defaultIfEmpty(gv.getString("etlFieldName"), ""), responseXmlDoc);
							}
						}
						else {
							UtilXml.addChildElementValue(responseXmlElement, "RESPONSE_CODE", "E108", responseXmlDoc);
							result.put("XmlOutput", EtlXmlUtil.cunstructXml(responseXmlDoc, OrderApi, "getEtlModel",jsonInput));
							return result;
						}
						//UtilXml.addChildElement(definitionsElement, "DEFINITION", responseXmlDoc);
						//UtilXml.addChildElementValue(definitionsElement, "DEFINITION", "Please duplicate the above list for inserting more records ", responseXmlDoc);
					}else{
						UtilXml.addChildElementValue(responseXmlElement, "RESPONSE_CODE", "E109", responseXmlDoc);
						result.put("XmlOutput", EtlXmlUtil.cunstructXml(responseXmlDoc, OrderApi, "getEtlModel",jsonInput));
						return result;
					}


				} else {
					UtilXml.addChildElementValue(responseXmlElement, "RESPONSE_CODE", "E110", responseXmlDoc);
					result.put("XmlOutput", EtlXmlUtil.cunstructXml(responseXmlDoc, OrderApi, "getEtlModel",jsonInput));
					return result;
				}

			} else{
				UtilXml.addChildElementValue(responseXmlElement, "RESPONSE_CODE", "E109", responseXmlDoc);
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
