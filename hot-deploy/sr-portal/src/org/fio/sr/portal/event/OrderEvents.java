/**
 * 
 */
package org.fio.sr.portal.event;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.fio.homeapps.ResponseCodes;
import org.fio.homeapps.constants.GlobalConstants;
import org.fio.homeapps.util.PartyHelper;
import org.fio.homeapps.util.QueryUtil;
import org.fio.homeapps.util.SrDataHelper;
import org.fio.homeapps.util.UtilDateTime;
import org.fio.sr.portal.connector.Connector;
import org.fio.sr.portal.connector.ReebConnector;
import org.fio.sr.portal.util.DataUtil;
import org.groupfio.common.portal.event.AjaxEvents;
import org.groupfio.common.portal.util.SrUtil;
import org.groupfio.common.portal.util.UtilContactMech;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

/**
 * @author Sharif
 *
 */
public class OrderEvents {
	
	private static final String MODULE = OrderEvents.class.getName();
	
	public static String getOrderDetail(HttpServletRequest request, HttpServletResponse response) {
    	
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
		String orderId = request.getParameter("orderId");
		String externalId = request.getParameter("externalId");
		String srNumber = request.getParameter("srNumber");
		
		Map<String, Object> result = FastMap.newInstance();
		
		try {
			String globalDateFormat = org.fio.homeapps.util.DataHelper.getGlobalDateFormat(delegator);
			
			Map<String, Object> callCtxt = FastMap.newInstance();
			Map<String, Object> callResult = FastMap.newInstance();
			
			/*if (UtilValidate.isEmpty(orderId) && UtilValidate.isNotEmpty(externalId)) {
				orderId = externalId;
			}*/
			
			if (UtilValidate.isNotEmpty(srNumber)) {
				result.putAll(org.fio.sr.portal.util.DataUtil.getSrOrderDetail(delegator, srNumber, orderId, externalId));
    		}
			
			if (UtilValidate.isNotEmpty(orderId)) {
				List conditions = FastList.newInstance();
				conditions.add(EntityCondition.makeCondition(EntityOperator.OR, 
						EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId),
						EntityCondition.makeCondition("transactionNumber", EntityOperator.EQUALS, orderId)
						));
                EntityCondition mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);
                GenericValue rmsOrderLine = EntityUtil.getFirst( delegator.findList("RmsTransactionMaster", mainConditons, null, null, null, false) );
				//GenericValue rmsOrderLine = EntityUtil.getFirst( delegator.findByAnd("RmsTransactionMaster", UtilMisc.toMap("orderId", orderId), null, false) );
				if (UtilValidate.isNotEmpty(rmsOrderLine)) {
					result.put("orderDate", UtilDateTime.timeStampToString(rmsOrderLine.getTimestamp("orderDate"), globalDateFormat, TimeZone.getDefault(), Locale.getDefault()));
					result.put("locationId", rmsOrderLine.getString("storeNumber"));
				}
			}
			if(UtilValidate.isNotEmpty(orderId)) {
				GenericValue lineAssoc = EntityQuery.use(delegator).from("EntityOrderLineAssoc").where("orderId", orderId).queryFirst();
				String domainEntityId = UtilValidate.isNotEmpty(lineAssoc) ? lineAssoc.getString("domainEntityId") : "";
				String domainEntityType = UtilValidate.isNotEmpty(lineAssoc) ? lineAssoc.getString("domainEntityType") : "";
				if("SERVICE_REQUEST".equals(domainEntityType) && !(srNumber.equals(domainEntityId))) {
					result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.CONFLICT_CODE);
					result.put(GlobalConstants.RESPONSE_MESSAGE, "This order is already associated with SR ID# "+domainEntityId);
					return AjaxEvents.doJSONResponse(response, result);
				}
			}
			
			/*if (UtilValidate.isNotEmpty(orderId) && UtilValidate.isNotEmpty(srNumber)) {
				GenericValue lineAssoc = EntityUtil.getFirst( delegator.findByAnd("EntityOrderLineAssoc", UtilMisc.toMap("orderId", orderId, "domainEntityId", srNumber, "domainEntityType", "SERVICE_REQUEST"), null, false) );
	    		if (UtilValidate.isNotEmpty(lineAssoc)) {
	    			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.CONFLICT_CODE);
	    			result.put(GlobalConstants.RESPONSE_MESSAGE, "SR and Order Association with orderId# "+lineAssoc.getString("externalId")+", already exists!");
	    			return AjaxEvents.doJSONResponse(response, result);
	    		}
			}*/
			
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, MODULE);
			
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());
			return AjaxEvents.doJSONResponse(response, result);
		}
		return AjaxEvents.doJSONResponse(response, result);
    }
	
	public static String getOrderLines(HttpServletRequest request, HttpServletResponse response) {
    	
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
		String orderId = request.getParameter("orderId");
		String externalId = request.getParameter("externalId");
		String srNumber = request.getParameter("srNumber");
		String orderDate = request.getParameter("orderDate");
		String location = request.getParameter("location");
		String externalLoginKey = request.getParameter("externalLoginKey");
		
		Map<String, Object> result = FastMap.newInstance();
		
		try {
			String globalDateFormat = org.fio.homeapps.util.DataHelper.getGlobalDateFormat(delegator);
			String globalDateTimeFormat = org.fio.homeapps.util.DataHelper.getGlobalDateTimeFormat(delegator);
			
			Map<String, Object> callCtxt = FastMap.newInstance();
			Map<String, Object> callResult = FastMap.newInstance();
			
			/*if (UtilValidate.isEmpty(orderId) && UtilValidate.isNotEmpty(externalId)) {
				orderId = externalId;
			}*/
			
			if (UtilValidate.isNotEmpty(externalId)) {
				
				List<Map<String, Object>> orderLines = new ArrayList<Map<String, Object>>();
				
				//Timestamp od = UtilDateTime.stringToTimeStamp(orderDate, "MM/dd/yyyy", TimeZone.getDefault(), Locale.getDefault());
				
				List conditions = new ArrayList<>();
				
				/*if (UtilValidate.isNotEmpty(orderId)) {
					conditions.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
				}*/
				if (UtilValidate.isNotEmpty(externalId)) {
					conditions.add(EntityCondition.makeCondition("transactionNumber", EntityOperator.EQUALS, externalId));
				}
				
				if (UtilValidate.isNotEmpty(location)) {
					conditions.add(EntityCondition.makeCondition("storeNumber", EntityOperator.EQUALS, location));
				}
				
				if (UtilValidate.isNotEmpty(orderDate)) {
					Timestamp od = UtilDateTime.stringToTimeStamp(orderDate, "MM/dd/yyyy", TimeZone.getDefault(), Locale.getDefault());
					conditions.add(EntityCondition.makeCondition(EntityOperator.AND, 
							EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(od)),
							EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(od))
							));
				}
				
                EntityCondition mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);
                
                Debug.log("getOrderLines mainConditons: "+ mainConditons);
                
                List<GenericValue> rmsOrderLineList = delegator.findList("RmsTransactionMaster", mainConditons, null, UtilMisc.toList("sequenceNumber"), null, false);
                
                if (UtilValidate.isNotEmpty(rmsOrderLineList)) {
                	Map<String, Object> orderTypes = SrDataHelper.getOrderTypes(delegator);
                	Map<String, Object> storeNames = SrDataHelper.getProductStoreNames(delegator);
                	
                	for (GenericValue rmsOrderLine : rmsOrderLineList) {
    		        	
    		        	Map<String, Object> orderLine = new LinkedHashMap<String, Object>();
    		        	String productName = UtilValidate.isNotEmpty(rmsOrderLine.getString("skuDescription")) ? rmsOrderLine.getString("skuDescription").replace("\"", "") : "";
    		        	orderLine.put("productId", rmsOrderLine.getString("skuNumber"));
    		        	orderLine.put("productName", productName);
    		        	orderLine.put("sequenceNo", rmsOrderLine.getString("sequenceNumber"));
    		        	//orderLine.put("subSequenceNumber", rmsOrderLine.getString("subSequenceNumber"));
    		        	orderLine.put("subSequenceNumber", UtilValidate.isNotEmpty(rmsOrderLine.getString("subSequenceNumber")) ? Integer.parseInt(rmsOrderLine.getString("subSequenceNumber")) : "");
    		        	orderLine.put("orderDate", UtilValidate.isNotEmpty(rmsOrderLine.get("orderDate")) ? UtilDateTime.timeStampToString(rmsOrderLine.getTimestamp("orderDate"), globalDateTimeFormat, TimeZone.getDefault(), Locale.getDefault()) : "");
    		        	orderLine.put("shippedDate", UtilValidate.isNotEmpty(rmsOrderLine.get("estimatedDeliveryDate")) ? UtilDateTime.timeStampToString(rmsOrderLine.getTimestamp("estimatedDeliveryDate"), globalDateTimeFormat, TimeZone.getDefault(), Locale.getDefault()) : "");
    		        	orderLine.put("lineStatus", rmsOrderLine.getString("itemStatus"));
    		        	
    		        	double totalAppliedQty = 0;
    		        	double totalAvailQty = 0;
    					Map<String, Object> orderLineDetail = org.fio.sr.portal.util.DataUtil.getSrOrderLineDetail(delegator, UtilMisc.toMap("custRequestId", srNumber, "orderId", UtilValidate.isEmpty(orderId)?externalId:orderId, "sequenceNumber", rmsOrderLine.getString("sequenceNumber"), "location", location));
    					if (UtilValidate.isNotEmpty(orderLineDetail)) {
    						totalAppliedQty = (double) orderLineDetail.get("totalAppliedQty");
    						totalAvailQty = (double) orderLineDetail.get("totalAvailQty");
    					}
    					orderLine.put("totalAppliedQty", totalAppliedQty);
    					orderLine.put("totalAvailQty", new BigDecimal(totalAvailQty).setScale(2, BigDecimal.ROUND_HALF_EVEN));
    		        	orderLine.put("actualQty", rmsOrderLine.getBigDecimal("quantitySold").doubleValue());
    		        	orderLine.put("orderTypeId", orderTypes.get(rmsOrderLine.getString("orderTypeId")));
    		        	
    		        	double appliedQty = 0;
    		        	if (UtilValidate.isNotEmpty(srNumber)) {
    		        		GenericValue entityAssocLine = EntityUtil.getFirst( delegator.findByAnd("EntityOrderLineAssoc", UtilMisc.toMap("orderId", rmsOrderLine.getString("orderId"), "lineItemNumber", rmsOrderLine.getString("sequenceNumber"), "domainEntityId", srNumber, "domainEntityType", "SERVICE_REQUEST"), null, false) );
    		        		if (UtilValidate.isNotEmpty(entityAssocLine)) {
    		        			appliedQty = entityAssocLine.getBigDecimal("lineItemAppliedQty").doubleValue();
    		        		}
    		        	}
    		        	orderLine.put("externalId", UtilValidate.isNotEmpty(externalId) ? externalId : orderId );
    		        	orderLine.put("appliedQty", appliedQty);
    		        	orderLine.put("orderId", orderId);
    		        	orderLine.put("externalLoginKey", externalLoginKey);
    		        	
    		        	orderLine.put("storeNumber", rmsOrderLine.getString("storeNumber"));
    		        	orderLine.put("storeName", storeNames.get(rmsOrderLine.getString("storeNumber")));
    		        	
    		        	orderLines.add(orderLine);
    		        }
                }
                
		        result.put("orderLines", orderLines);
    		}
			
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, MODULE);
			
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());
			return AjaxEvents.doJSONResponse(response, result);
		}
		return AjaxEvents.doJSONResponse(response, result);
    }

	public static String createSrOrderAssocAction(HttpServletRequest request, HttpServletResponse response) {
    	
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
		String orderId = request.getParameter("orderId");
		String srNumber = request.getParameter("srNumber");
		String orderDate = request.getParameter("orderDate");
		String location = request.getParameter("locationId");
		
		String domainEntityType = request.getParameter("domainEntityType");
		String domainEntityId = request.getParameter("domainEntityId");
		
		String orderTypeIdList[] = request.getParameterValues("orderTypeId");
		String appliedQtyList[] = request.getParameterValues("appliedQty");
		String sequenceNoList[] = request.getParameterValues("sequenceNo");
		
		Timestamp currentTimestamp = UtilDateTime.nowTimestamp();
		
		Map<String, Object> result = FastMap.newInstance();
		
		String poNumber = "";
		try {
			String userLoginId = userLogin.getString("userLoginId");
			
			/*if (UtilValidate.isNotEmpty(orderId) && UtilValidate.isNotEmpty(srNumber)) {
				GenericValue lineAssoc = EntityUtil.getFirst( delegator.findByAnd("EntityOrderLineAssoc", UtilMisc.toMap("orderId", orderId, "domainEntityId", domainEntityId, "domainEntityType", domainEntityType), null, false) );
	    		if (UtilValidate.isNotEmpty(lineAssoc)) {
	    			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
	    			result.put(GlobalConstants.RESPONSE_MESSAGE, "SR and Order Association with orderId# "+orderId+", already exists!");
	    		}
			}*/
			
			if (UtilValidate.isEmpty(result.get(GlobalConstants.RESPONSE_CODE))) {
				
				result.put("srNumber", srNumber);
				
				//Timestamp od = UtilDateTime.stringToTimeStamp(orderDate, "MM/dd/yyyy", TimeZone.getDefault(), Locale.getDefault());
				String externalId = orderId;
				// store order lines [start]
				if (UtilValidate.isNotEmpty(appliedQtyList)) {
				
					for (int i = 0; i < appliedQtyList.length; i++) {
						
						String orderTypeId = orderTypeIdList[i];
						
						String appliedQty = appliedQtyList[i];
						if (UtilValidate.isEmpty(appliedQty)) {
							continue;
						}
						String sequenceNo = sequenceNoList[i];
						
						appliedQty = UtilValidate.isEmpty( appliedQty ) ? "0" : appliedQty;
						sequenceNo = UtilValidate.isEmpty( sequenceNo ) ? null : sequenceNo;
						
						List conditions = FastList.newInstance();
						//conditions.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
						conditions.add(EntityCondition.makeCondition(EntityOperator.OR, 
								EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId),
								EntityCondition.makeCondition("transactionNumber", EntityOperator.EQUALS, orderId)
								));
						conditions.add(EntityCondition.makeCondition("sequenceNumber", EntityOperator.EQUALS, sequenceNo));
						if (UtilValidate.isNotEmpty(location)) {
							conditions.add(EntityCondition.makeCondition("storeNumber", EntityOperator.EQUALS, location));
						}
						
		                EntityCondition mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);
		                GenericValue rmsOrderLine = EntityUtil.getFirst( delegator.findList("RmsTransactionMaster", mainConditons, null, null, null, false) );
						//GenericValue rmsOrderLine = EntityUtil.getFirst( delegator.findByAnd("RmsTransactionMaster", UtilMisc.toMap("orderId", orderId, "sequenceNumber", sequenceNo), null, false) );
		                orderId = rmsOrderLine.getString("orderId");
		                poNumber = rmsOrderLine.getString("purchaseOrder");
		                		
						String lineItemIdentifier = null;
						GenericValue lineAssoc = EntityUtil.getFirst( delegator.findByAnd("EntityOrderLineAssoc", UtilMisc.toMap("orderId", orderId, "lineItemNumber", rmsOrderLine.getString("sequenceNumber"), "domainEntityId", srNumber, "domainEntityType", "SERVICE_REQUEST"), null, false) );
		        		if (UtilValidate.isEmpty(lineAssoc)) {
		        			lineAssoc = delegator.makeValue("EntityOrderLineAssoc");
		        			lineItemIdentifier = delegator.getNextSeqId("EntityOrderLineAssoc");
		        		} else {
		        			lineItemIdentifier = lineAssoc.getString("lineItemIdentifier");
		        		}
						
						lineAssoc.put("lineItemIdentifier", lineItemIdentifier);
						lineAssoc.put("domainEntityId", domainEntityId);
						lineAssoc.put("domainEntityType", domainEntityType);
						lineAssoc.put("orderId", orderId);
						lineAssoc.put("externalId", rmsOrderLine.getString("transactionNumber"));
						lineAssoc.put("lineItemNumber", sequenceNo);
						lineAssoc.put("lineItemType", "SERIAL");
						lineAssoc.put("lineItemAppliedQty", new BigDecimal(appliedQty));
						lineAssoc.put("lineItemStatus", rmsOrderLine.getString("itemStatus"));
						lineAssoc.put("lineItemDescription", rmsOrderLine.getString("skuDescription"));
						lineAssoc.put("lineAssocCreatedDate", currentTimestamp);
						lineAssoc.put("lineItemShippedDate", rmsOrderLine.get("estimatedDeliveryDate"));
						lineAssoc.put("customerSerialNumber", null);
						lineAssoc.put("parentEntityRefId", null);

						delegator.createOrStore(lineAssoc);
						
						if (UtilValidate.isNotEmpty(orderTypeId)) {
							rmsOrderLine.put("orderTypeId", orderTypeId.toUpperCase());
							rmsOrderLine.store();
						}
						externalId = rmsOrderLine.getString("transactionNumber");
						DataUtil.syncSrOrderAssociation(delegator, srNumber);
					}
				}
				// store order lines [end]
				result.put("orderId", orderId);
				
				if (UtilValidate.isNotEmpty(domainEntityType) && "SERVICE_REQUEST".equals(domainEntityType) && UtilValidate.isNotEmpty(domainEntityId)) {
					String customFieldId = EntityUtilProperties.getPropertyValue("sr-portal.properties", "customerPo.customFieldId", delegator);
					EntityCondition cond1 = EntityCondition.makeCondition(EntityOperator.AND,
							EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, domainEntityId),
							EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, customFieldId)
							);
					
					GenericValue custRequestAttr = EntityQuery.use(delegator).from("CustRequestAttribute").where(cond1).queryFirst();
					if(UtilValidate.isNotEmpty(custRequestAttr)) {
						String attrValue =  custRequestAttr.getString("attrValue");
						List<String> custPOList = new LinkedList<String>();
						if(UtilValidate.isNotEmpty(attrValue) && attrValue.contains(",")) {
							custPOList.addAll(org.fio.admin.portal.util.DataUtil.stringToList(attrValue, ","));
						} else if(UtilValidate.isNotEmpty(attrValue)) custPOList.add(attrValue);
						if(UtilValidate.isNotEmpty(custPOList) && !(custPOList.contains(poNumber))) {
							custPOList.add(poNumber);
							custRequestAttr.set("attrValue", org.fio.admin.portal.util.DataUtil.listToString(custPOList,","));
						}
						 
					} else {
						List<GenericValue> entityOrderLineItems = EntityQuery.use(delegator).from("EntityOrderLineAssoc").where("domainEntityId", domainEntityId, "domainEntityType", domainEntityType).queryList();
						List<String> orderIds = UtilValidate.isNotEmpty(entityOrderLineItems) ? EntityUtil.getFieldListFromEntityList(entityOrderLineItems, "orderId", true) : new ArrayList<String>();
						List<String> poNumbers = new LinkedList<String>();
						if(UtilValidate.isNotEmpty(orderIds)) {
							for(String ordId : orderIds) {
								GenericValue rmsTransMaster = EntityQuery.use(delegator).from("RmsTransactionMaster").where("orderId", ordId).queryFirst();
								if(UtilValidate.isNotEmpty(rmsTransMaster) && UtilValidate.isNotEmpty(rmsTransMaster.getString("purchaseOrder")))
									poNumbers.add(rmsTransMaster.getString("purchaseOrder"));
							}
						}
						custRequestAttr = delegator.makeValue("CustRequestAttribute");
						custRequestAttr.set("custRequestId", domainEntityId);
						custRequestAttr.set("attrName", customFieldId);
						custRequestAttr.set("attrValue", org.fio.admin.portal.util.DataUtil.listToString(poNumbers, ","));
						custRequestAttr.create();
					}
					
					request.setAttribute("custRequestId", domainEntityId);
					Map<String, Object> historyInputMap = new HashMap<String, Object>();
					historyInputMap.put("custRequestId", domainEntityId);
					historyInputMap.put("contextMap", UtilMisc.toMap("orderId", externalId, "comment", "Order Added to FSR by User "+org.fio.homeapps.util.PartyHelper.getUserLoginName(delegator, userLoginId, false)));
					historyInputMap.put("userLogin", userLogin);
					
					
					Map<String, Object> historyOutMap = dispatcher.runSync("srPortal.createSrHistory", historyInputMap);
					//String serviceResult = createSrHistory(request,response);

					if(!ServiceUtil.isSuccess(historyOutMap)) {
						result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
						result.put(GlobalConstants.RESPONSE_MESSAGE, "Problem While Creating Service Request History");
						return AjaxEvents.doJSONResponse(response, result);
					}
				}
				
				result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
				result.put(GlobalConstants.RESPONSE_MESSAGE, "Successfully Associated SR and Order");
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, MODULE);
			
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());
			return AjaxEvents.doJSONResponse(response, result);
		}
		return AjaxEvents.doJSONResponse(response, result);
    }
    
    public static String searchOrders(HttpServletRequest request, HttpServletResponse response) {

		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

		String requestUri = request.getParameter("requestUri");
		String screenService = request.getParameter("screenService");
		String start = request.getParameter("start");
		String length = request.getParameter("length");

		String domainEntityType = request.getParameter("domainEntityType");
		String domainEntityId = request.getParameter("domainEntityId");
		String isEnabledEdit = request.getParameter("isEnabledEdit");
		
		String externalLoginKey = request.getParameter("externalLoginKey");
		
		boolean hasUpdateAccess = org.fio.homeapps.util.DataUtil.hasPermission(request, "UPDATE_OPERATION");
		boolean hasDeleteAccess = org.fio.homeapps.util.DataUtil.hasPermission(request, "DELETE_OPERATION");
		boolean hasCreateAccess = org.fio.homeapps.util.DataUtil.hasPermission(request, "CREATE_OPERATION");
		boolean hasViewAccess = org.fio.homeapps.util.DataUtil.hasPermission(request, "VIEW_OPERATION");
		
		Map<String, Object> result = FastMap.newInstance();
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();

		try {
			
			String globalDateFormat = org.fio.homeapps.util.DataHelper.getGlobalDateFormat(delegator);
			String globalDateTimeFormat = org.fio.homeapps.util.DataHelper.getGlobalDateTimeFormat(delegator);

			if (UtilValidate.isNotEmpty(domainEntityType) && UtilValidate.isNotEmpty(domainEntityId)) {
				List<String> domainEntityIds = new ArrayList<String>();
				domainEntityIds.add(domainEntityId);
				GenericValue custRequset = EntityQuery.use(delegator).from("CustRequest").where("custRequestId", domainEntityId).queryFirst();
				String parentSrId = UtilValidate.isNotEmpty(custRequset.getString("custReqDocumentNum")) ? custRequset.getString("custReqDocumentNum") : "";
				if(UtilValidate.isNotEmpty(parentSrId)) domainEntityIds.add(parentSrId);
				List<EntityCondition> conditionList = FastList.newInstance();

				conditionList.add(EntityCondition.makeCondition("domainEntityType", EntityOperator.EQUALS, domainEntityType));
				conditionList.add(EntityCondition.makeCondition("domainEntityId", EntityOperator.IN, domainEntityIds));

				EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				List<GenericValue> orderLineList = delegator.findList("EntityOrderLineAssoc", mainConditons, null, UtilMisc.toList("orderId"), null, false);
				if (UtilValidate.isNotEmpty(orderLineList)) {	
					
					Map<String, Object> storeNames = SrDataHelper.getProductStoreNames(delegator);
					Map<String, Object> orderTypes = SrDataHelper.getOrderTypes(delegator);
					
					List<GenericValue> enumeration = EntityQuery.use(delegator).select("enumId","description").from("Enumeration").where("enumTypeId","STKT_STATUS_ID").orderBy("sequenceId").queryList();
					Map<String, Object> inspectionStatusMap = new HashMap<>();
					if(UtilValidate.isNotEmpty(enumeration)) {
						inspectionStatusMap = org.fio.admin.portal.util.DataUtil.getMapFromGeneric(enumeration, "enumId", "description", false);
					}
					
					Map<String, Object> partyNames = new HashMap<>();
					PartyHelper.getPartyNameByUserLoginIds(delegator, partyNames, orderLineList, "proofedBy");
					
					Map<String, Object> inspectPartyNames = new HashMap<>();
					PartyHelper.getPartyNameByUserLoginIds(delegator, inspectPartyNames, orderLineList, "inspectionBy");
					
					for (GenericValue orderLine : orderLineList) {
						String orderId = orderLine.getString("orderId");
						String sequenceNo = orderLine.getString("lineItemNumber");
						GenericValue rmsOrderLine = EntityUtil.getFirst( delegator.findByAnd("RmsTransactionMaster", UtilMisc.toMap("orderId", orderId, "sequenceNumber", sequenceNo), null, false) );
						if (UtilValidate.isEmpty(rmsOrderLine)) {
							Debug.logError("OrderLine not found into RmsTransactionMaster: searchOrders", MODULE);
							continue;
						}
						
						Map<String, Object> data = new HashMap<String, Object>();
						
						data.put("lineItemIdentifier", orderLine.getString("lineItemIdentifier"));
						data.put("orderId", orderLine.getString("orderId"));
						data.put("externalId", UtilValidate.isNotEmpty(rmsOrderLine.getString("transactionNumber")) ? rmsOrderLine.getString("transactionNumber") : orderId );

						data.put("domainEntityId", orderLine.getString("domainEntityId"));
						data.put("domainEntityType", orderLine.getString("domainEntityType"));
						
						String skuDescription = rmsOrderLine.getString("skuDescription"); //UtilValidate.isNotEmpty(rmsOrderLine.getString("skuDescription")) ? rmsOrderLine.getString("skuDescription").replace("\"", "") : "";
						
						BigDecimal extendedCost = new BigDecimal(0);
						BigDecimal unitCost = rmsOrderLine.getBigDecimal("unitCost");
						if (UtilValidate.isNotEmpty(unitCost)) {
							unitCost = unitCost.setScale(2, BigDecimal.ROUND_HALF_EVEN);
							extendedCost = new BigDecimal(unitCost.doubleValue() * rmsOrderLine.getBigDecimal("quantitySold").doubleValue()).setScale(2, BigDecimal.ROUND_HALF_EVEN);
						}
						
						data.put("productId", rmsOrderLine.getString("skuNumber"));
						data.put("customerPo", rmsOrderLine.getString("purchaseOrder"));
						data.put("unitCost", unitCost);
						data.put("extendedCost", extendedCost);
						data.put("orderType", orderTypes.get(rmsOrderLine.getString("orderTypeId")));
						
						data.put("skuDescription", skuDescription);
						data.put("sequenceNo", orderLine.getString("lineItemNumber"));
						data.put("subSequenceNumber", UtilValidate.isNotEmpty(rmsOrderLine.getString("subSequenceNumber")) ? Integer.parseInt(rmsOrderLine.getString("subSequenceNumber")) : "");
						data.put("orderDate", UtilValidate.isNotEmpty(rmsOrderLine.get("orderDate")) ? UtilDateTime.timeStampToString(rmsOrderLine.getTimestamp("orderDate"), globalDateTimeFormat, TimeZone.getDefault(), Locale.getDefault()) : "");
						data.put("shippedDate", UtilValidate.isNotEmpty(rmsOrderLine.get("estimatedDeliveryDate")) ? UtilDateTime.timeStampToString(rmsOrderLine.getTimestamp("estimatedDeliveryDate"), globalDateTimeFormat, TimeZone.getDefault(), Locale.getDefault()) : "");
						data.put("lineStatus", orderLine.getString("lineItemStatus"));
						
						data.put("actualQty", rmsOrderLine.getBigDecimal("quantitySold").doubleValue());
						double totalAppliedQty = 0;
						double totalAvailQty = 0;
						Map<String, Object> orderLineDetail = org.fio.sr.portal.util.DataUtil.getSrOrderLineDetail(delegator, UtilMisc.toMap("custRequestId", domainEntityId, "orderId", orderId, "sequenceNumber", sequenceNo, "location", rmsOrderLine.getString("storeNumber")));
						if (UtilValidate.isNotEmpty(orderLineDetail)) {
							totalAppliedQty = (double) orderLineDetail.get("totalAppliedQty");
							totalAvailQty = (double) orderLineDetail.get("totalAvailQty");
						}
						data.put("totalAppliedQty", totalAppliedQty);
						data.put("totalAvailQty", totalAvailQty);
						data.put("appliedQty", orderLine.getBigDecimal("lineItemAppliedQty").doubleValue());
						
						data.put("proofedBy", partyNames.get(orderLine.get("proofedBy")));
						data.put("proofedDate", UtilValidate.isNotEmpty(orderLine.get("proofedDate")) ? UtilDateTime.timeStampToString(orderLine.getTimestamp("proofedDate"), globalDateTimeFormat, TimeZone.getDefault(), Locale.getDefault()) : "");
						
						data.put("inspectionBy", UtilValidate.isNotEmpty(orderLine.get("inspectionBy")) ? inspectPartyNames.get(orderLine.get("inspectionBy")) : "");
						data.put("inspectionDate", UtilValidate.isNotEmpty(orderLine.get("inspectionDate")) ? UtilDateTime.timeStampToString(orderLine.getTimestamp("inspectionDate"), globalDateTimeFormat, TimeZone.getDefault(), Locale.getDefault()) : "");
						data.put("inspectionStatus", UtilValidate.isNotEmpty(orderLine.get("inspectionStatus")) ? inspectionStatusMap.get(orderLine.get("inspectionStatus")) : "");
						
						
						data.put("storeNumber", rmsOrderLine.getString("storeNumber"));
						data.put("storeName", storeNames.get(rmsOrderLine.getString("storeNumber")));
						
						if (UtilValidate.isNotEmpty(isEnabledEdit)) {
							hasUpdateAccess = isEnabledEdit.equals("N") ? false : true;
						}
						
						data.put("hasViewAccess", hasViewAccess ?"Y":"N");
						data.put("hasUpdateAccess", hasUpdateAccess ?"Y":"N");
						
						data.put("externalLoginKey", externalLoginKey);
						
						//data.put("isIssueMaterial", UtilValidate.isNotEmpty(orderLine.getString("isIssueMaterial")) ? orderLine.getString("isIssueMaterial") : "N");
						data.put("gridId", orderId+"_"+sequenceNo);
						dataList.add(data);
					}
				}
			}

			result.put("data", dataList);
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, MODULE);
			result.put("data", dataList);
			return AjaxEvents.doJSONResponse(response, result);
		}
		return AjaxEvents.doJSONResponse(response, result);
	}
    
    public static String removeOrderLineAssocData(HttpServletRequest request, HttpServletResponse response) {
		
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

		String selectedEntryIds = request.getParameter("selectedEntryIds");
		String srNumber = request.getParameter("srNumber");
		
		Map<String, Object> result = FastMap.newInstance();

		try {
			if (UtilValidate.isNotEmpty(selectedEntryIds)) {
				List<GenericValue> issueMaterials = EntityQuery.use(delegator).from("IssueMaterial").where("custRequestId", srNumber, "issuedType", "SO").queryList();
				Map<String, Object> products = SrDataHelper.getIssueMaterialProducts(delegator, issueMaterials);
				Map<String, Object> notRemoveItem = new HashMap<>();
				int removeCount = 0;
				Set<String> orderIds = new HashSet<String>();
				Set<String> poNumbers = new HashSet<String>();
				Map<String, Object> poNumberMap = new HashMap<String, Object>();
				for(String entryId : selectedEntryIds.split(",")) {
					GenericValue orderLine = EntityQuery.use(delegator).from("EntityOrderLineAssoc").where("lineItemIdentifier", entryId).queryFirst();
					if (UtilValidate.isNotEmpty(orderLine)) {
						String orderId = orderLine.getString("orderId");
						String externalId = orderId;
						String sequenceNo = orderLine.getString("lineItemNumber");
						String lineItemDescription = orderLine.getString("lineItemDescription");
						
						GenericValue rmsOrderLine = EntityUtil.getFirst( delegator.findByAnd("RmsTransactionMaster", UtilMisc.toMap("orderId", orderId, "sequenceNumber", sequenceNo), null, false) );
						if (UtilValidate.isEmpty(rmsOrderLine)) {
							Debug.logError("OrderLine not found into RmsTransactionMaster: removeOrderLineAssocData", MODULE);
							continue;
						}
						
						String skuNumber = rmsOrderLine.getString("skuNumber");
						String purchaseOrder = rmsOrderLine.getString("purchaseOrder");
						externalId = rmsOrderLine.getString("transactionNumber");
						
						if(!orderIds.contains(externalId))
							orderIds.add(externalId);
						if(!(poNumberMap.containsKey(orderId)))
							poNumberMap.put(orderId, purchaseOrder);
						
						if ( (UtilValidate.isNotEmpty(skuNumber) && products.containsKey(skuNumber)) 
								|| (UtilValidate.isNotEmpty(lineItemDescription) && products.containsValue(lineItemDescription)) ) {
							notRemoveItem.put(skuNumber, lineItemDescription);
						} else {
							delegator.removeByAnd("EntityOrderLineAssoc", UtilMisc.toMap("lineItemIdentifier", entryId));
							removeCount++;
						}
					}
				}
				
				String successMessage = removeCount+" SR and Order association removed!";
				if (UtilValidate.isNotEmpty(notRemoveItem)) {
					successMessage += " But";
					for (String productId : notRemoveItem.keySet()) {
						successMessage += " " + notRemoveItem.get(productId) + "("+productId+"), ";
					}
					successMessage = successMessage.substring(0, successMessage.length()-2);
					successMessage += " Already issued material";
				}
				
				String userLoginId = userLogin.getString("userLoginId");
				for(String orId: orderIds) {
					if (UtilValidate.isNotEmpty(srNumber)) {
						request.setAttribute("custRequestId", srNumber);
						Map<String, Object> historyInputMap = new HashMap<String, Object>();
						historyInputMap.put("custRequestId", srNumber);
						historyInputMap.put("contextMap", UtilMisc.toMap("orderId", orId, "comment", "Order Removed by User "+org.fio.homeapps.util.PartyHelper.getUserLoginName(delegator, userLoginId, false)));
						historyInputMap.put("userLogin", userLogin);
						
						
						Map<String, Object> historyOutMap = dispatcher.runSync("srPortal.createSrHistory", historyInputMap);
						//String serviceResult = createSrHistory(request,response);

						if(!ServiceUtil.isSuccess(historyOutMap)) {
							result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
							result.put(GlobalConstants.RESPONSE_MESSAGE, "Problem While Creating Service Request History");
							return AjaxEvents.doJSONResponse(response, result);
						}
					}
				}
				
				String customFieldId = EntityUtilProperties.getPropertyValue("sr-portal.properties", "customerPo.customFieldId", delegator);
				EntityCondition cond1 = EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, srNumber),
						EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, customFieldId)
						);
				GenericValue custRequestAttr = EntityQuery.use(delegator).from("CustRequestAttribute").where(cond1).queryFirst();
				
				if(UtilValidate.isNotEmpty(custRequestAttr) && UtilValidate.isNotEmpty(poNumberMap)) {
					List<String> customerPoList = new LinkedList<String>();
					String attrValue = custRequestAttr.getString("attrValue");
					customerPoList.addAll(org.fio.admin.portal.util.DataUtil.stringToList(attrValue, ","));

					for(String key : poNumberMap.keySet()) {
						GenericValue entityOrderLineAssoc1 = EntityQuery.use(delegator).from("EntityOrderLineAssoc").where("orderId", key).queryFirst();
						if(UtilValidate.isEmpty(entityOrderLineAssoc1)) {
							customerPoList.remove(poNumberMap.get(key));
						}
					}
					custRequestAttr.set("attrValue", org.fio.admin.portal.util.DataUtil.listToString(customerPoList, ","));
					custRequestAttr.store();
				}
				
				DataUtil.syncSrOrderAssociation(delegator, srNumber);
				result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
				result.put(GlobalConstants.RESPONSE_MESSAGE, successMessage);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, MODULE);
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());
			return AjaxEvents.doJSONResponse(response, result);
		}
		return AjaxEvents.doJSONResponse(response, result);
	}
    
    public static String validateOrderDetails(HttpServletRequest request, HttpServletResponse response) {
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
    	String orderId = request.getParameter("orderId");
    	String externalId = request.getParameter("externalId");
    	String location = request.getParameter("location");
    	String odDate = request.getParameter("orderDate");
		
		Map<String, Object> result = FastMap.newInstance();
		try {
			result.put("requiredJustification", "N");
			
			List conditions = new ArrayList<>();
			
			if (UtilValidate.isNotEmpty(orderId)) {
				conditions.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
			}
			if (UtilValidate.isNotEmpty(externalId)) {
				conditions.add(EntityCondition.makeCondition("transactionNumber", EntityOperator.EQUALS, externalId));
			}
			if (UtilValidate.isNotEmpty(location)) {
				conditions.add(EntityCondition.makeCondition("storeNumber", EntityOperator.EQUALS, location));
			}
			
			if (UtilValidate.isNotEmpty(odDate)) {
				Timestamp od = UtilDateTime.stringToTimeStamp(odDate, "MM/dd/yyyy", TimeZone.getDefault(), Locale.getDefault());
				conditions.add(EntityCondition.makeCondition(EntityOperator.AND, 
						EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(od)),
						EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(od))
						));
			}
			
            EntityCondition mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);
            Debug.log("getOrderLines mainConditons: "+ mainConditons);
            GenericValue rmsTransactionMaster = EntityUtil.getFirst( delegator.findList("RmsTransactionMaster", mainConditons, null, null, null, false) );
			
            if(UtilValidate.isNotEmpty(rmsTransactionMaster)) {
				Timestamp orderDate = rmsTransactionMaster.getTimestamp("orderDate");
				if(UtilValidate.isNotEmpty(orderDate)) {
					int ordJustfDiff = Integer.parseInt(org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "ORD_JUSTF_DIFF", "4"));
					
					LocalDateTime orderTimestamp = orderDate.toLocalDateTime();
					LocalDateTime nowTimestamp = LocalDateTime.now();
					long months = ChronoUnit.MONTHS.between(orderTimestamp, nowTimestamp);
					if(months > ordJustfDiff ) {
						result.put("requiredJustification", "Y");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("requiredJustification", "N");
			return AjaxEvents.doJSONResponse(response, result);
			
		}
    	return AjaxEvents.doJSONResponse(response, result);
    }
    
    public static String getProofData(HttpServletRequest request, HttpServletResponse response) {
		
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

		String selectedEntryIds = request.getParameter("selectedEntryIds");
		String srNumber = request.getParameter("srNumber");
		
		Map<String, Object> result = FastMap.newInstance();

		try {
			if (UtilValidate.isNotEmpty(selectedEntryIds)) {
				Map<String, Object> proofData = FastMap.newInstance();
				List<GenericValue> orderLines = EntityQuery.use(delegator).select("lineItemIdentifier", "lineItemNumber", "lineItemDescription", "externalId").from("EntityOrderLineAssoc")
						.where(EntityCondition.makeCondition("lineItemIdentifier", EntityOperator.IN, Arrays.asList(selectedEntryIds.split(",")))).queryList();
				
				if (UtilValidate.isNotEmpty(orderLines)) {
					int count = 1;
					for (GenericValue orderLine : orderLines) {
						proofData.put("itemNumber_"+count, orderLine.getString("lineItemNumber"));
						proofData.put("itemDesc_"+count, orderLine.getString("lineItemDescription"));
						proofData.put("itemIdentifier_"+count, orderLine.getString("lineItemIdentifier"));
						proofData.put("externalId_"+count, orderLine.getString("externalId"));
						count++;
					}
				}
				
				String partChanges = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "PRO_FINISH_PLUS", "Part Changes");
				String pcCustomFieldId = org.fio.sr.portal.DataHelper.getCustomFieldId(delegator, "SERVICE_GROUP", partChanges);
				if (UtilValidate.isNotEmpty(pcCustomFieldId)) {
					proofData.put("partChanges", SrUtil.getCustRequestAttrValue(delegator, pcCustomFieldId, srNumber));
				}
				
				result.put("proofData", proofData);
				result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, MODULE);
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());
			return AjaxEvents.doJSONResponse(response, result);
		}
		return AjaxEvents.doJSONResponse(response, result);
	}
    
    public static String performProof(HttpServletRequest request, HttpServletResponse response) {
		
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

		String selectedEntryIds = request.getParameter("selectedEntryIds");
		String srNumber = request.getParameter("srNumber");
		
		Map<String, Object> result = FastMap.newInstance();

		try {
			if (UtilValidate.isNotEmpty(selectedEntryIds)) {
				Map<String, Object> proofData = FastMap.newInstance();
				List<GenericValue> orderLines = EntityQuery.use(delegator)
						.from("EntityOrderLineAssoc")
						.where(EntityCondition.makeCondition("lineItemIdentifier", EntityOperator.IN, Arrays.asList(selectedEntryIds.split(",")))).queryList();
				
				if (UtilValidate.isNotEmpty(orderLines)) {
					for (GenericValue orderLine : orderLines) {
						orderLine.put("proofedBy", userLogin.getString("userLoginId"));
						orderLine.put("proofedDate", UtilDateTime.nowTimestamp());
						orderLine.store();
					}
				}
				
				result.put("proofData", proofData);
				result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, MODULE);
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());
			return AjaxEvents.doJSONResponse(response, result);
		}
		return AjaxEvents.doJSONResponse(response, result);
	}
    
    //public static Connector connector;
    public static String syncReebOrder(HttpServletRequest request, HttpServletResponse response) {
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
		//String orderId = request.getParameter("orderId");
		String externalId = request.getParameter("externalId");
		String orderDate = request.getParameter("orderDate");
		String location = request.getParameter("location");
		
		String srNumber = request.getParameter("srNumber");
		
		Map<String, Object> result = FastMap.newInstance();
		Connector connector = null;
		try {
			String globalDateFormat = org.fio.homeapps.util.DataHelper.getGlobalDateFormat(delegator);
			String globalDateTimeFormat = org.fio.homeapps.util.DataHelper.getGlobalDateTimeFormat(delegator);
			
			String wwDateFormat = "yyyy-MM-dd";
			
			Map<String, Object> callCtxt = FastMap.newInstance();
			Map<String, Object> callResult = FastMap.newInstance();
			
			if (UtilValidate.isEmpty(externalId) || UtilValidate.isEmpty(orderDate) || UtilValidate.isEmpty(location)) {
				result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
				result.put(GlobalConstants.RESPONSE_MESSAGE, "Order ID, Order Date and Location is Required field!");
				return AjaxEvents.doJSONResponse(response, result);
			}	
			
			Timestamp od = UtilDateTime.stringToTimeStamp(orderDate, "MM/dd/yyyy", TimeZone.getDefault(), Locale.getDefault());
			
			List conditions = new ArrayList<>();
			conditions.add(EntityCondition.makeCondition("transactionNumber", EntityOperator.EQUALS, externalId));
			conditions.add(EntityCondition.makeCondition("storeNumber", EntityOperator.EQUALS, location));
			conditions.add(EntityCondition.makeCondition(EntityOperator.AND, 
					EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(od)),
					EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(od))
					));
			
			GenericValue rtm = EntityQuery.use(delegator).select("orderId", "transactionNumber", "storeNumber", "orderDate").from("RmsTransactionMaster").where(EntityCondition.makeCondition(conditions, EntityOperator.AND)).queryFirst();
			if (UtilValidate.isNotEmpty(rtm)) {
				result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
				result.put(GlobalConstants.RESPONSE_MESSAGE, "Order already exists, Order# "+externalId);
				return AjaxEvents.doJSONResponse(response, result);
			}
			
			GenericValue productStore = EntityQuery.use(delegator).select("productStoreId", "wwLocationId").from("ProductStore").where("productStoreId", location).queryFirst();
			String wwLocationId = productStore.getString("wwLocationId");
			String wwOrderDate = UtilDateTime.timeStampToString(od, "yyyy-MM-dd", TimeZone.getDefault(), Locale.getDefault());
			
			if (UtilValidate.isEmpty(connector)) {
				connector = new ReebConnector(delegator);
			}
			
			Connection con = connector.getConnectionByLocation(delegator, wwLocationId);
			if (UtilValidate.isNotEmpty(con)) {
				List<GenericValue> dataList = new ArrayList<>();
				int resultSize = 0;
				try {
					Statement stmt = con.createStatement();
					String query = "SELECT "
							+ "'N' AS \"Invoiced\"," 
							+ "1 AS \"Location ID\","
							+ "cmcust AS \"Customer Number\","
							+ "cmbdat AS \"Creation Date\","
							+ "cmname AS \"Customer Name\","
							+ "cmadd1 AS \"Address Line 1\","
							+ "cmadd2 AS \"Address Line 2\","
							+ "cmcity AS \"City\","
							+ "cmstat AS \"State\","
							+ "cmzipc AS \"ZIP Code\","
							+ "cmphon AS \"Phone Number\","
							+ "odnumb AS \"Order Number\","
							+ "ommst.ompono AS \"Cust PO Number\","
							+ "ommst.omordt AS \"Order Date\","
							+ "ommst.omshdt AS \"Ship Date\","
							+ "odsnum AS \"Line Number\","
							+ "odqord AS \"Qty Ordered\","
							+ "{fn CONCAT(oddsc1,COALESCE(odddesc,''))} AS \"Description\","
							+ "odmcst AS \"Merchandise Cost\","
							+ "odbcst AS \"Burden Cost\","
							+ "odlcst AS \"Labor Cost\","
							+ "odnet AS \"Price\","
							+ "NULL AS \"Production Date\","
							+ "'2000-01-01 00:00:00' AS \"Last Updated\","
							+ "NULL AS \"Operation Work Center\","
							+ "NULL AS \"Worker\","
							+ "ommst.omuser As \"User\","
							+ "NULL AS \"Change Indicator\" "
							+ "FROM ommst "
							+ "LEFT JOIN oddtl ON ommst.omnumb = oddtl.odnumb "
							+ "LEFT JOIN cmmst ON ommst.omcust = cmmst.cmcust "
							+ "LEFT JOIN (SELECT list(odddesc) as \"odddesc\", oddsnum, oddnumb "
							+ "FROM oddfl WHERE oddnumb = ? GROUP BY oddnumb,oddsnum) a ON ommst.omnumb = a.oddnumb "
							+ "WHERE omnumb = ? and ommst.omordt=?";
					
					Debug.logInfo("reeb jdbc query: "+query, MODULE);
					Debug.logInfo("reeb jdbc start execute query: "+UtilDateTime.nowTimestamp(), MODULE);
					List<Object> values = new ArrayList<>();
					values.add(externalId);
					values.add(externalId);
					values.add(wwOrderDate);
					ResultSet rs = QueryUtil.getResultSet(query, values, delegator);
					//int resultSize = connector.getSize(rs);
					Debug.logInfo("reeb jdbc end execute query: "+UtilDateTime.nowTimestamp(), MODULE);
					
					String orderId = null;
					while (rs.next()) {
						Debug.logInfo("importing order# "+rs.getString("Order Number"), MODULE);
						
						rtm = delegator.makeValue("RmsTransactionMaster");
						
						if (resultSize == 0) {
							orderId = delegator.getNextSeqId("RmsTransactionMaster");
							Debug.logInfo("reeb jdbc new orderId: "+orderId, MODULE);
						}
						
						String billToPartyId = null;
						if (UtilValidate.isNotEmpty(rs.getString("Customer Number"))) {
							billToPartyId = org.fio.homeapps.util.DataUtil.getPartyIdentificationPartyId(delegator, rs.getString("Customer Number"), "ALT_DEAL_CUST_ID");
							if (UtilValidate.isEmpty(billToPartyId)) {
								callCtxt = FastMap.newInstance();
								callCtxt.put("accountName", rs.getString("Customer Name"));
								callCtxt.put("generalAddress1", rs.getString("Address Line 1"));
								callCtxt.put("generalAddress2", rs.getString("Address Line 2"));
								callCtxt.put("generalCity", rs.getString("City"));
								callCtxt.put("generalStateProvinceGeoId", rs.getString("State"));
								callCtxt.put("generalPostalCode", rs.getString("ZIP Code"));
								callCtxt.put("primaryPhoneNumber", rs.getString("Phone Number"));
								
								callCtxt.put("dataSourceId", org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "DL_DS_ID", "10150"));
								
								callResult = dispatcher.runSync("crmsfa.createAccount", callCtxt);
								if (ServiceUtil.isSuccess(callResult)) {
									String customerNumber = rs.getString("Customer Number");
									if (UtilValidate.isNotEmpty(customerNumber)) {
										GenericValue pi = delegator.makeValue("PartyIdentification");
										pi.put("partyId", callResult.get("partyId"));
										pi.put("idValue", customerNumber);
										pi.put("partyIdentificationTypeId", "ALT_DEAL_CUST_ID");
										pi.create();
									}
								}
							} else {
								String contactMechId = UtilContactMech.evalutePartyPostal(UtilMisc.toMap("delegator", delegator, "dispatcher", dispatcher, "userLogin", userLogin
										, "partyId", billToPartyId
										, "address1", rs.getString("Address Line 1"), "address2", rs.getString("Address Line 2")
										, "countryGeoId", "USA", "stateGeoId", rs.getString("State")
										, "city", rs.getString("City")
										, "zip5", rs.getString("ZIP Code")
										));
								if (UtilValidate.isNotEmpty(contactMechId)) {
									Debug.logInfo("Create primary address, Party# "+billToPartyId, MODULE);
								}
							}
						}
						
						rtm.put("orderId", orderId);
						rtm.put("transactionNumber", rs.getString("Order Number"));
						rtm.put("orderTypeId", SrUtil.getOrderType(delegator, rs.getString("Order Number")));
						
						rtm.put("invoice", rs.getString("Invoiced"));
						//rtm.put("storeNumber", SrUtil.getProductStoreIdFromWWLocation(delegator, wwLocationId));
						rtm.put("storeNumber", location);
						rtm.put("billToPartyId", billToPartyId);
						
						//rtm.put("", rs.getString("CUSTOMER_NAME"));
						rtm.put("shipmentAddress1", rs.getString("Address Line 1"));
						rtm.put("shipmentAddress2", rs.getString("Address Line 2"));
						rtm.put("shipmentCity", rs.getString("City"));
						rtm.put("shipmentStateProvince", rs.getString("State"));
						rtm.put("shipmentPostalCode", rs.getString("ZIP Code"));
						rtm.put("shipmentPhone", rs.getString("Phone Number"));
						rtm.put("purchaseOrder", rs.getString("Cust PO Number"));
						rtm.put("sequenceNumber", rs.getString("Line Number"));
						rtm.put("subSequenceNumber", rs.getString("Line Number"));
						rtm.put("quantitySold", UtilValidate.isNotEmpty(rs.getString("Qty Ordered")) ? new BigDecimal(rs.getString("Qty Ordered")) : null);
						rtm.put("skuDescription", rs.getString("Description"));
						rtm.put("unitRetail", UtilValidate.isNotEmpty(rs.getString("Price")) ? new BigDecimal(rs.getString("Price")) : null);
						//rtm.put("", rs.getString("BURDEN_COST"));
						//rtm.put("", rs.getString("LABOR_COST"));
						//rtm.put("", rs.getString("PRODUCTION_DATE"));
						
						BigDecimal unitPrice = new BigDecimal(0);
						unitPrice = UtilValidate.isNotEmpty(rs.getString("Merchandise Cost")) ? new BigDecimal(rs.getString("Merchandise Cost")).add(unitPrice) : unitPrice;
						unitPrice = UtilValidate.isNotEmpty(rs.getString("Burden Cost")) ? new BigDecimal(rs.getString("Burden Cost")).add(unitPrice) : unitPrice;
						unitPrice = UtilValidate.isNotEmpty(rs.getString("Labor Cost")) ? new BigDecimal(rs.getString("Labor Cost")).add(unitPrice) : unitPrice;
						rtm.put("unitCost", unitPrice);
						
						BigDecimal totalSalesAmount = null;
						if (UtilValidate.isNotEmpty(rtm.getBigDecimal("quantitySold")) && UtilValidate.isNotEmpty(rtm.getBigDecimal("unitRetail"))) {
							totalSalesAmount = new BigDecimal(rtm.getBigDecimal("unitRetail").doubleValue() * rtm.getBigDecimal("quantitySold").doubleValue()).setScale(2, BigDecimal.ROUND_HALF_EVEN);
						}
						rtm.put("totalSalesAmount", totalSalesAmount);
						
						try {
							rtm.put("createdStamp", UtilValidate.isNotEmpty(rs.getString("Creation Date")) ? UtilDateTime.stringToTimeStamp(rs.getString("Creation Date"), wwDateFormat, TimeZone.getDefault(), Locale.getDefault()) : null);
							rtm.put("orderDate", UtilValidate.isNotEmpty(rs.getString("Order Date")) ? UtilDateTime.stringToTimeStamp(rs.getString("Order Date"), wwDateFormat, TimeZone.getDefault(), Locale.getDefault()) : null);
							rtm.put("estimatedDeliveryDate", UtilValidate.isNotEmpty(rs.getString("Ship Date")) ? UtilDateTime.stringToTimeStamp(rs.getString("Ship Date"), wwDateFormat, TimeZone.getDefault(), Locale.getDefault()) : null);
							rtm.put("lastUpdatedStamp", UtilValidate.isNotEmpty(rs.getString("Last Updated")) ? UtilDateTime.stringToTimeStamp(rs.getString("Last Updated"), wwDateFormat, TimeZone.getDefault(), Locale.getDefault()) : null);
						} catch (Exception e) {
							Debug.logError(e, MODULE);
						}
						
						dataList.add(rtm);
						
						resultSize++;
					}
					Debug.logInfo("reeb jdbc result size: "+resultSize, MODULE);
					
					if (resultSize==0) {
						Debug.logInfo("ORDER NOT FOUND CHECK ORDER AVAILABLE IN ANY OTHER DATES, START: "+UtilDateTime.nowTimestamp(), MODULE);
						query = "SELECT "
								+ "'N' AS \"Invoiced\"," 
								+ "1 AS \"Location ID\","
								+ "cmcust AS \"Customer Number\","
								+ "cmbdat AS \"Creation Date\","
								+ "cmname AS \"Customer Name\","
								+ "cmadd1 AS \"Address Line 1\","
								+ "cmadd2 AS \"Address Line 2\","
								+ "cmcity AS \"City\","
								+ "cmstat AS \"State\","
								+ "cmzipc AS \"ZIP Code\","
								+ "cmphon AS \"Phone Number\","
								+ "odnumb AS \"Order Number\","
								+ "ommst.ompono AS \"Cust PO Number\","
								+ "ommst.omordt AS \"Order Date\","
								+ "ommst.omshdt AS \"Ship Date\","
								+ "odsnum AS \"Line Number\","
								+ "odqord AS \"Qty Ordered\","
								+ "{fn CONCAT(oddsc1,COALESCE(odddesc,''))} AS \"Description\","
								+ "odmcst AS \"Merchandise Cost\","
								+ "odbcst AS \"Burden Cost\","
								+ "odlcst AS \"Labor Cost\","
								+ "odnet AS \"Price\","
								+ "NULL AS \"Production Date\","
								+ "'2000-01-01 00:00:00' AS \"Last Updated\","
								+ "NULL AS \"Operation Work Center\","
								+ "NULL AS \"Worker\","
								+ "ommst.omuser As \"User\","
								+ "NULL AS \"Change Indicator\" "
								+ "FROM ommst "
								+ "LEFT JOIN oddtl ON ommst.omnumb = oddtl.odnumb "
								+ "LEFT JOIN cmmst ON ommst.omcust = cmmst.cmcust "
								+ "LEFT JOIN (SELECT list(odddesc) as \"odddesc\", oddsnum, oddnumb "
								+ "FROM oddfl WHERE oddnumb = ? GROUP BY oddnumb,oddsnum) a ON ommst.omnumb = a.oddnumb "
								+ "WHERE omnumb = ?";
						
						Debug.logInfo("reeb jdbc query: "+query, MODULE);
						Debug.logInfo("reeb jdbc start execute query: "+UtilDateTime.nowTimestamp(), MODULE);
						values = new ArrayList<>();
						values.add(externalId);
						values.add(externalId);
						rs = QueryUtil.getResultSet(query, values, delegator);
						//int resultSize = connector.getSize(rs);
						Debug.logInfo("reeb jdbc end execute query: "+UtilDateTime.nowTimestamp(), MODULE);
						
						Set<String> orderDates = new HashSet<String>();
						while (rs.next()) {
							String otherOrderDate = rs.getString("Order Date");
							if (UtilValidate.isNotEmpty(otherOrderDate)) {
								Timestamp othOrderDate =  UtilDateTime.stringToTimeStamp(otherOrderDate, wwDateFormat, TimeZone.getDefault(), Locale.getDefault());
								if (UtilValidate.isNotEmpty(othOrderDate)) {
									orderDates.add(UtilDateTime.timeStampToString(othOrderDate, globalDateFormat, TimeZone.getDefault(), Locale.getDefault()));
								}
							}
						}
						Debug.logInfo("reeb jdbc OTHER orderDates: "+StringUtil.join(orderDates, ","), MODULE);
						Debug.logInfo("ORDER NOT FOUND CHECK ORDER AVAILABLE IN ANY OTHER DATES, END: "+UtilDateTime.nowTimestamp(), MODULE);
						
						if (UtilValidate.isNotEmpty(orderDates)) {
							String errorMessage = "Order#"+externalId+" not found for given date. Order found for dates: "+StringUtil.join(orderDates, ",")+". Please update the date and re-sync";
							result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
							result.put(GlobalConstants.RESPONSE_MESSAGE, errorMessage);
							return AjaxEvents.doJSONResponse(response, result);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					Debug.logError(e, MODULE);
					result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
					result.put(GlobalConstants.RESPONSE_MESSAGE, "Error:  "+e.getMessage());
					return AjaxEvents.doJSONResponse(response, result);
				}
				connector.closeConnection(con);
				
				if (resultSize < 1) {
					Debug.logError("No result found, Order# "+externalId, MODULE);
					result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
					result.put(GlobalConstants.RESPONSE_MESSAGE, "Order not found, Order# "+externalId);
					return AjaxEvents.doJSONResponse(response, result);
				}
				
				Debug.logInfo("reeb jdbc start to store: "+UtilDateTime.nowTimestamp(), MODULE);
				TransactionUtil.begin();
				delegator.storeAll(dataList);
				TransactionUtil.commit();
				Debug.logInfo("reeb jdbc end to store: "+UtilDateTime.nowTimestamp(), MODULE);
				
				result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
			} else {
				result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
				result.put(GlobalConstants.RESPONSE_MESSAGE, "Unable to Connect!");
				return AjaxEvents.doJSONResponse(response, result);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, MODULE);
			
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());
			return AjaxEvents.doJSONResponse(response, result);
		}
		return AjaxEvents.doJSONResponse(response, result);
    }
    
    @Deprecated
    public static String syncReebOrderOld(HttpServletRequest request, HttpServletResponse response) {
    	
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
		//String orderId = request.getParameter("orderId");
		String externalId = request.getParameter("externalId");
		String orderDate = request.getParameter("orderDate");
		String location = request.getParameter("location");
		
		String srNumber = request.getParameter("srNumber");
		
		Map<String, Object> result = FastMap.newInstance();
		Connector connector = null;
		try {
			
			//String globalDateFormat = org.fio.homeapps.util.DataHelper.getGlobalDateFormat(delegator);
			String globalDateTimeFormat = org.fio.homeapps.util.DataHelper.getGlobalDateTimeFormat(delegator);
			
			String wwDateFormat = "yyyy-MM-dd";
			
			Map<String, Object> callCtxt = FastMap.newInstance();
			Map<String, Object> callResult = FastMap.newInstance();
			
			if (UtilValidate.isEmpty(externalId) || UtilValidate.isEmpty(orderDate) || UtilValidate.isEmpty(location)) {
				result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
				result.put(GlobalConstants.RESPONSE_MESSAGE, "Order ID, Order Date and Location is Required field!");
				return AjaxEvents.doJSONResponse(response, result);
			}	
			
			Timestamp od = UtilDateTime.stringToTimeStamp(orderDate, "MM/dd/yyyy", TimeZone.getDefault(), Locale.getDefault());
			
			List conditions = new ArrayList<>();
			conditions.add(EntityCondition.makeCondition("transactionNumber", EntityOperator.EQUALS, externalId));
			conditions.add(EntityCondition.makeCondition("storeNumber", EntityOperator.EQUALS, location));
			conditions.add(EntityCondition.makeCondition(EntityOperator.AND, 
					EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(od)),
					EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(od))
					));
			
			GenericValue rtm = EntityQuery.use(delegator).select("orderId", "transactionNumber", "storeNumber", "orderDate").from("RmsTransactionMaster").where(EntityCondition.makeCondition(conditions, EntityOperator.AND)).queryFirst();
			if (UtilValidate.isNotEmpty(rtm)) {
				result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
				result.put(GlobalConstants.RESPONSE_MESSAGE, "Order already exists, Order# "+externalId);
				return AjaxEvents.doJSONResponse(response, result);
			}
			
			GenericValue productStore = EntityQuery.use(delegator).select("productStoreId", "wwLocationId").from("ProductStore").where("productStoreId", location).queryFirst();
			String wwLocationId = productStore.getString("wwLocationId");
			String wwOrderDate = UtilDateTime.timeStampToString(od, "yyyy-MM-dd", TimeZone.getDefault(), Locale.getDefault());
			
			if (UtilValidate.isEmpty(connector)) {
				connector = new ReebConnector(delegator);
			}
			
			Connection con = connector.getConnection();
			if (UtilValidate.isNotEmpty(con)) {
				String orderTransViewName = connector.getOrderTransViewName();
				Statement stmt = con.createStatement();
				
				String query = "select * from "+orderTransViewName+" where \"Order Number\"=? and \"Location ID\"=? and \"Order Date\"=?";
				Debug.logInfo("reeb jdbc query: "+query, MODULE);
				Debug.logInfo("reeb jdbc start execute query: "+UtilDateTime.nowTimestamp(), MODULE);
				List<Object> values = new ArrayList<>();
				values.add(externalId);
				values.add(wwLocationId);
				values.add(wwOrderDate);
				ResultSet rs = QueryUtil.getResultSet(query, values, delegator);
				//int resultSize = connector.getSize(rs);
				Debug.logInfo("reeb jdbc end execute query: "+UtilDateTime.nowTimestamp(), MODULE);
				
				List<GenericValue> dataList = new ArrayList<>();
				String orderId = null;
				int resultSize = 0;
				while (rs.next()) {
					Debug.logInfo("importing order# "+rs.getString("Order Number"), MODULE);
					
					rtm = delegator.makeValue("RmsTransactionMaster");
					
					if (resultSize == 0) {
						orderId = delegator.getNextSeqId("RmsTransactionMaster");
						Debug.logInfo("reeb jdbc new orderId: "+orderId, MODULE);
					}
					
					String billToPartyId = null;
					if (UtilValidate.isNotEmpty(rs.getString("Customer Number"))) {
						billToPartyId = org.fio.homeapps.util.DataUtil.getPartyIdentificationPartyId(delegator, rs.getString("Customer Number"), "ALT_DEAL_CUST_ID");
						if (UtilValidate.isEmpty(billToPartyId)) {
							callCtxt = FastMap.newInstance();
							callCtxt.put("accountName", rs.getString("Customer Name"));
							callCtxt.put("generalAddress1", rs.getString("Address Line 1"));
							callCtxt.put("generalAddress2", rs.getString("Address Line 2"));
							callCtxt.put("generalCity", rs.getString("City"));
							callCtxt.put("generalStateProvinceGeoId", rs.getString("State"));
							callCtxt.put("generalPostalCode", rs.getString("ZIP Code"));
							callCtxt.put("primaryPhoneNumber", rs.getString("Phone Number"));
							
							callCtxt.put("dataSourceId", org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "DL_DS_ID", "10150"));
							
							callResult = dispatcher.runSync("crmsfa.createAccount", callCtxt);
							if (ServiceUtil.isSuccess(callResult)) {
								String customerNumber = rs.getString("Customer Number");
								if (UtilValidate.isNotEmpty(customerNumber)) {
									GenericValue pi = delegator.makeValue("PartyIdentification");
									pi.put("partyId", callResult.get("partyId"));
									pi.put("idValue", customerNumber);
									pi.put("partyIdentificationTypeId", "ALT_DEAL_CUST_ID");
									pi.create();
								}
							}
						} else {
							String contactMechId = UtilContactMech.evalutePartyPostal(UtilMisc.toMap("delegator", delegator, "dispatcher", dispatcher, "userLogin", userLogin
									, "partyId", billToPartyId
									, "address1", rs.getString("Address Line 1"), "address2", rs.getString("Address Line 2")
									, "countryGeoId", "USA", "stateGeoId", rs.getString("State")
									, "city", rs.getString("City")
									, "zip5", rs.getString("ZIP Code")
									));
							if (UtilValidate.isNotEmpty(contactMechId)) {
								Debug.logInfo("Create primary address, Party# "+billToPartyId, MODULE);
							}
						}
					}
					
					rtm.put("orderId", orderId);
					rtm.put("transactionNumber", rs.getString("Order Number"));
					rtm.put("orderTypeId", SrUtil.getOrderType(delegator, rs.getString("Order Number")));
					
					rtm.put("invoice", rs.getString("Invoiced"));
					rtm.put("storeNumber", SrUtil.getProductStoreIdFromWWLocation(delegator, rs.getString("Location ID")));
					rtm.put("billToPartyId", billToPartyId);
					
					//rtm.put("", rs.getString("CUSTOMER_NAME"));
					rtm.put("shipmentAddress1", rs.getString("Address Line 1"));
					rtm.put("shipmentAddress2", rs.getString("Address Line 2"));
					rtm.put("shipmentCity", rs.getString("City"));
					rtm.put("shipmentStateProvince", rs.getString("State"));
					rtm.put("shipmentPostalCode", rs.getString("ZIP Code"));
					rtm.put("shipmentPhone", rs.getString("Phone Number"));
					rtm.put("purchaseOrder", rs.getString("Cust PO Number"));
					rtm.put("sequenceNumber", rs.getString("Line Number"));
					rtm.put("quantitySold", UtilValidate.isNotEmpty(rs.getString("Qty Ordered")) ? new BigDecimal(rs.getString("Qty Ordered")) : null);
					rtm.put("skuDescription", rs.getString("Description"));
					rtm.put("unitRetail", UtilValidate.isNotEmpty(rs.getString("Price")) ? new BigDecimal(rs.getString("Price")) : null);
					//rtm.put("", rs.getString("BURDEN_COST"));
					//rtm.put("", rs.getString("LABOR_COST"));
					//rtm.put("", rs.getString("PRODUCTION_DATE"));
					
					BigDecimal unitPrice = new BigDecimal(0);
					unitPrice = UtilValidate.isNotEmpty(rs.getString("Merchandise Cost")) ? new BigDecimal(rs.getString("Merchandise Cost")).add(unitPrice) : unitPrice;
					unitPrice = UtilValidate.isNotEmpty(rs.getString("Burden Cost")) ? new BigDecimal(rs.getString("Burden Cost")).add(unitPrice) : unitPrice;
					unitPrice = UtilValidate.isNotEmpty(rs.getString("Labor Cost")) ? new BigDecimal(rs.getString("Labor Cost")).add(unitPrice) : unitPrice;
					rtm.put("unitCost", unitPrice);
					
					try {
						rtm.put("createdStamp", UtilValidate.isNotEmpty(rs.getString("Creation Date")) ? UtilDateTime.stringToTimeStamp(rs.getString("Creation Date"), wwDateFormat, TimeZone.getDefault(), Locale.getDefault()) : null);
						rtm.put("orderDate", UtilValidate.isNotEmpty(rs.getString("Order Date")) ? UtilDateTime.stringToTimeStamp(rs.getString("Order Date"), wwDateFormat, TimeZone.getDefault(), Locale.getDefault()) : null);
						rtm.put("estimatedDeliveryDate", UtilValidate.isNotEmpty(rs.getString("Ship Date")) ? UtilDateTime.stringToTimeStamp(rs.getString("Ship Date"), wwDateFormat, TimeZone.getDefault(), Locale.getDefault()) : null);
						rtm.put("lastUpdatedStamp", UtilValidate.isNotEmpty(rs.getString("Last Updated")) ? UtilDateTime.stringToTimeStamp(rs.getString("Last Updated"), wwDateFormat, TimeZone.getDefault(), Locale.getDefault()) : null);
					} catch (Exception e) {
						Debug.logError(e, MODULE);
					}
					
					//rtm.put("", rs.getString("OPERATION_WORK_CENTER"));
					
					//rtm.create();
					
					dataList.add(rtm);
					
					resultSize++;
				}
				
				Debug.logInfo("reeb jdbc result size: "+resultSize, MODULE);
				if (resultSize < 1) {
					Debug.logError("No result found, Order# "+externalId, MODULE);
					result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
					result.put(GlobalConstants.RESPONSE_MESSAGE, "Order not found, Order# "+externalId);
					return AjaxEvents.doJSONResponse(response, result);
				}
				
				Debug.logInfo("reeb jdbc start to store: "+UtilDateTime.nowTimestamp(), MODULE);
				TransactionUtil.begin();
				delegator.storeAll(dataList);
				TransactionUtil.commit();
				Debug.logInfo("reeb jdbc end to store: "+UtilDateTime.nowTimestamp(), MODULE);
				
				result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
			} else {
				result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
				result.put(GlobalConstants.RESPONSE_MESSAGE, "Unable to Connect!");
				return AjaxEvents.doJSONResponse(response, result);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, MODULE);
			
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());
			return AjaxEvents.doJSONResponse(response, result);
		}
		connector.closeConnection();
		return AjaxEvents.doJSONResponse(response, result);
    }
    
    public static String approveIssueMaterial(HttpServletRequest request, HttpServletResponse response) {
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map<String, Object> result = new HashMap<String, Object>();
		//String requestData = org.fio.admin.portal.util.DataUtil.getJsonStrBody(request);
		String selectedRows = request.getParameter("selectedRows");
		String activeTab = request.getParameter("activeTab");
		try {
			result.put("activeTab", activeTab);
			if(UtilValidate.isNotEmpty(selectedRows)) {
				List<Map<String, Object>> requestMapList = new ArrayList<Map<String, Object>>();
				if(UtilValidate.isNotEmpty(selectedRows))
					requestMapList = org.fio.admin.portal.util.DataUtil.convertToListMap(selectedRows);
				if(UtilValidate.isNotEmpty(requestMapList)) {
					for(Map<String, Object> requestMap : requestMapList) {
						String lineItemIdentifier = (String) requestMap.get("lineItemIdentifier");
						String domainEntityType = (String) requestMap.get("domainEntityType");
						String domainEntityId = (String) requestMap.get("domainEntityId");
						
						GenericValue entityOrderLineAssoc = EntityQuery.use(delegator).from("EntityOrderLineAssoc").where("lineItemIdentifier", lineItemIdentifier).queryFirst();
						if(UtilValidate.isNotEmpty(entityOrderLineAssoc)) {
							entityOrderLineAssoc.set("isIssueMaterial", "Y");
							entityOrderLineAssoc.store();
						}
						
					}
				}
			}
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
		} catch (Exception e) {
			e.printStackTrace();
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());
			return AjaxEvents.doJSONResponse(response, result);
		}
		return AjaxEvents.doJSONResponse(response, result);
    }
	
}
