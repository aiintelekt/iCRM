/**
 * 
 */
package org.fio.sr.portal.util;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;

import javolution.util.FastList;

/**
 * @author Sharif
 *
 */
public class DataUtil {

	private static String MODULE = DataUtil.class.getName();
	
	public static Map<String, Object> getSrOrderDetail(Delegator delegator, String custRequestId, String orderId, String externalId) {
		Map<String, Object> orderDetail = new LinkedHashMap<String, Object>();
		try {
			if (UtilValidate.isNotEmpty(custRequestId)) {
				double totalActualQty = 0;
				double totalAppliedQty = 0;
				double totalAvailQty = 0;
				
				List conditions = FastList.newInstance();
				
				conditions.add(EntityCondition.makeCondition("transactionNumber", EntityOperator.EQUALS, externalId));
				
				/*conditions.add(EntityCondition.makeCondition(EntityOperator.OR,
						EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId),
						EntityCondition.makeCondition("transactionNumber", EntityOperator.EQUALS, externalId)
						));*/
				
                EntityCondition mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);
                List<GenericValue> rmsOrderLineList = delegator.findList("RmsTransactionMaster", mainConditons, null, UtilMisc.toList("sequenceNumber"), null, false);
                if (UtilValidate.isNotEmpty(rmsOrderLineList)) {
                	for (GenericValue rmsOrderLine : rmsOrderLineList) {
                		if (UtilValidate.isNotEmpty(rmsOrderLine.get("quantitySold"))) {
                			totalActualQty += rmsOrderLine.getBigDecimal("quantitySold").doubleValue();
                		}
                	}
                	
                	if (UtilValidate.isEmpty(orderId) && UtilValidate.isNotEmpty(rmsOrderLineList)) {
                    	orderId = rmsOrderLineList.get(0).getString("orderId");
                    }
    				
    				conditions = FastList.newInstance();
    				
    				conditions.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
    				//conditions.add(EntityCondition.makeCondition("domainEntityId", EntityOperator.EQUALS, custRequestId));
    				//conditions.add(EntityCondition.makeCondition("domainEntityType", EntityOperator.EQUALS, "SERVICE_REQUEST"));
    				
                    mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);
                    List<GenericValue> orderLineList = delegator.findList("EntityOrderLineAssoc", mainConditons, null, UtilMisc.toList("lineItemNumber"), null, false);
                    if (UtilValidate.isNotEmpty(orderLineList)) {
                    	for (GenericValue orderLine : orderLineList) {
                    		if (UtilValidate.isNotEmpty(orderLine.get("lineItemAppliedQty"))) {
                    			totalAppliedQty += orderLine.getBigDecimal("lineItemAppliedQty").doubleValue();
                    		}
                    	}
                    }
                    
                    totalAvailQty = totalActualQty - totalAppliedQty;
                }
                
                orderDetail.put("totalActualQty", totalActualQty);
                orderDetail.put("totalAppliedQty", totalAppliedQty);
                orderDetail.put("totalAvailQty", totalAvailQty);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return orderDetail;
	}
	
	public static Map<String, Object> getSrOrderLineDetail(Delegator delegator, Map<String, Object> filter) {
		Map<String, Object> orderLineDetail = new LinkedHashMap<String, Object>();
		try {
			
			String custRequestId = (String) filter.get("custRequestId");  
			String orderId = (String) filter.get("orderId");  
			String sequenceNumber = (String) filter.get("sequenceNumber");  
			String location = (String) filter.get("location");  
			
			if (UtilValidate.isNotEmpty(custRequestId)) {
				double totalActualQty = 0;
				double totalAppliedQty = 0;
				double totalAvailQty = 0;
				
				List conditions = FastList.newInstance();
				
				//conditions.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
				conditions.add(EntityCondition.makeCondition(EntityOperator.OR, 
						EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId),
						EntityCondition.makeCondition("transactionNumber", EntityOperator.EQUALS, orderId)
						));
				conditions.add(EntityCondition.makeCondition("sequenceNumber", EntityOperator.EQUALS, sequenceNumber));
				if (UtilValidate.isNotEmpty(location)) {
					conditions.add(EntityCondition.makeCondition("storeNumber", EntityOperator.EQUALS, location));
				}
				
                EntityCondition mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);
                List<GenericValue> rmsOrderLineList = delegator.findList("RmsTransactionMaster", mainConditons, null, UtilMisc.toList("sequenceNumber"), null, false);
                if (UtilValidate.isNotEmpty(rmsOrderLineList)) {
                	for (GenericValue rmsOrderLine : rmsOrderLineList) {
                		if (UtilValidate.isNotEmpty(rmsOrderLine.get("quantitySold"))) {
                			totalActualQty += rmsOrderLine.getBigDecimal("quantitySold").doubleValue();
                		}
                	}
                }
				
				conditions = FastList.newInstance();
				
				//conditions.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
				conditions.add(EntityCondition.makeCondition(EntityOperator.OR, 
						EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId),
						EntityCondition.makeCondition("externalId", EntityOperator.EQUALS, orderId)
						));
				conditions.add(EntityCondition.makeCondition("lineItemNumber", EntityOperator.EQUALS, sequenceNumber));
				if (UtilValidate.isNotEmpty(location)) {
					conditions.add(EntityCondition.makeCondition("storeNumber", EntityOperator.EQUALS, location));
				}
				/*if (UtilValidate.isNotEmpty(custRequestId)) {
					conditions.add(EntityCondition.makeCondition("domainEntityId", EntityOperator.EQUALS, custRequestId));
					conditions.add(EntityCondition.makeCondition("domainEntityType", EntityOperator.EQUALS, "SERVICE_REQUEST"));
				}*/
				
                mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);
                
                DynamicViewEntity dynamicView = new DynamicViewEntity();
	            
	            dynamicView.addMemberEntity("EOLA", "EntityOrderLineAssoc");
				dynamicView.addAlias("EOLA", "lineItemIdentifier", null, null, null, true, null);
                
				dynamicView.addAlias("EOLA", "lineItemAppliedQty");
				dynamicView.addAlias("EOLA", "orderId");
				dynamicView.addAlias("EOLA", "externalId");
				dynamicView.addAlias("EOLA", "lineItemNumber");
				
				dynamicView.addMemberEntity("RTM", "RmsTransactionMaster");
				dynamicView.addAlias("RTM", "storeNumber", "storeNumber", null, null, null, null);
				dynamicView.addViewLink("EOLA", "RTM", Boolean.FALSE, ModelKeyMap.makeKeyMapList("orderId", "orderId", "lineItemNumber", "sequenceNumber"));
				
				List<GenericValue> orderLineList = EntityQuery.use(delegator).select("lineItemAppliedQty").from(dynamicView).where(mainConditons).queryList();
                
                //List<GenericValue> orderLineList = delegator.findList("EntityOrderLineAssoc", mainConditons, null, UtilMisc.toList("lineItemNumber"), null, false);
				
                if (UtilValidate.isNotEmpty(orderLineList)) {
                	for (GenericValue orderLine : orderLineList) {
                		if (UtilValidate.isNotEmpty(orderLine.get("lineItemAppliedQty"))) {
                			totalAppliedQty += orderLine.getBigDecimal("lineItemAppliedQty").doubleValue();
                		}
                	}
                }
                
                totalAvailQty = totalActualQty - totalAppliedQty;
                
                orderLineDetail.put("totalActualQty", totalActualQty);
                orderLineDetail.put("totalAppliedQty", totalAppliedQty);
                orderLineDetail.put("totalAvailQty", totalAvailQty);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return orderLineDetail;
	}
	
	public static List<String> syncSrOrderAssociation(Delegator delegator, String custRequestId) {
		try {
			if (UtilValidate.isNotEmpty(custRequestId)) {
				String purchaseOrder = "";
				List<String> orderIdList = null;
                List conditions = FastList.newInstance();
				
				conditions.add(EntityCondition.makeCondition("domainEntityType", EntityOperator.EQUALS, "SERVICE_REQUEST"));
				conditions.add(EntityCondition.makeCondition("domainEntityId", EntityOperator.EQUALS, custRequestId));
				
				EntityCondition mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);
                List<GenericValue> orderLineList = delegator.findList("EntityOrderLineAssoc", mainConditons, UtilMisc.toSet("orderId", "lineItemNumber"), null, null, false);
                if (UtilValidate.isNotEmpty(orderLineList)) {
                	orderIdList = EntityUtil.getFieldListFromEntityList(orderLineList, "orderId", true);
                	for (GenericValue orderLine : orderLineList) {
                		GenericValue rtm = EntityQuery.use(delegator).select("orderTypeId").from("RmsTransactionMaster").where("orderId", orderLine.getString("orderId"), "sequenceNumber", orderLine.getString("lineItemNumber")).queryFirst();
                		if (UtilValidate.isNotEmpty(rtm)) {
                			purchaseOrder += orderLine.getString("orderId") + "-" + rtm.getString("orderTypeId") + ",";
                		}
                	}
                	if (UtilValidate.isNotEmpty(purchaseOrder)) {
                		purchaseOrder = purchaseOrder.substring(0, purchaseOrder.length() - 1);
                	}
                	//purchaseOrder = StringUtil.join(orderIdList, ",");
                }
                
                GenericValue supplementory = EntityUtil.getFirst(delegator.findByAnd("CustRequestSupplementory",
						UtilMisc.toMap("custRequestId", custRequestId), null, false));
            	if (UtilValidate.isNotEmpty(supplementory)) {
            		supplementory.put("purchaseOrder", purchaseOrder);
            		TransactionUtil.begin();
            		supplementory.store();
            		Debug.logInfo("syncSrOrderAssociation done for SR#: "+custRequestId+", purchaseOrder: "+purchaseOrder, MODULE);
            		TransactionUtil.commit();
            		return orderIdList;
            	}
			}
		} catch (Exception e) {
			Debug.logError("syncSrOrderAssociation Error: "+e.getMessage(), MODULE);
			e.printStackTrace();
		}
		return null;
	}
	
	public static Map<String, Object> getPartyRate(Delegator delegator, Map<String, Object> context){
		Map<String, Object>  result = new HashMap<String, Object>();
		try {
			String partyId = (String) context.get("partyId");
			String rateTypeId = (String) context.get("rateTypeId");
			Timestamp timeEntryDate = UtilValidate.isNotEmpty(context.get("timeEntryDate")) ? (Timestamp) context.get("timeEntryDate") : UtilDateTime.nowTimestamp();
			LocalDateTime monthStart = timeEntryDate.toLocalDateTime().with(TemporalAdjusters.firstDayOfMonth());
			LocalDateTime monthEnd = timeEntryDate.toLocalDateTime().with(TemporalAdjusters.lastDayOfMonth());
			
			int monthInt = monthStart.getMonthValue(); 
			String month = ""; 
			if (monthInt < 10) month = "0"+String.valueOf(monthInt); 
			else month = String.valueOf(monthInt);
			
			String timeEntryDateStr = ""+monthStart.getYear()+"-"+month+"-";
 
			List<EntityCondition> conditions = new ArrayList<>();
			conditions.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
			conditions.add(EntityCondition.makeCondition("rateTypeId", EntityOperator.EQUALS, rateTypeId));
			conditions.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(Timestamp.valueOf(monthStart))));
			//conditions.add(EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(Timestamp.valueOf(monthEnd))));
			EntityCondition condtion = EntityCondition.makeCondition(conditions, EntityOperator.AND);
			
			List<GenericValue> partyRateList = EntityQuery.use(delegator).from("PartyRate").where(condtion).queryList();
			if(UtilValidate.isNotEmpty(partyRateList)) {
				for(GenericValue partyRate : partyRateList) {
					Timestamp fromDate = partyRate.getTimestamp("fromDate");
					Timestamp thruDate = partyRate.getTimestamp("thruDate");
					
					if(UtilValidate.isNotEmpty(fromDate) && (timeEntryDate.equals(fromDate) || timeEntryDate.after(fromDate)) && (UtilValidate.isNotEmpty(thruDate) && (timeEntryDate.before(thruDate) || timeEntryDate.equals(thruDate)))) {
						result.put("rate", partyRate.getDouble("rate"));
						result.put("fromDate", fromDate);
						result.put("thruDate", thruDate);
						break;
					} else {
						result.put("rate", partyRate.getDouble("rate"));
						result.put("fromDate", fromDate);
						result.put("thruDate", thruDate);
						break;
					}
				}
			} else {
				condtion = EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, "Company"),
						EntityCondition.makeCondition("rateTypeId", EntityOperator.EQUALS, rateTypeId),
						EntityCondition.makeCondition("defaultRate", EntityOperator.EQUALS,"Y")
						);
				GenericValue defaultRate = EntityQuery.use(delegator).from("PartyRate").where(condtion).filterByDate().queryFirst();
				if(UtilValidate.isNotEmpty(defaultRate)) {
					result.put("rate", defaultRate.getDouble("rate"));
					result.put("fromDate", defaultRate.getTimestamp("fromDate"));
					result.put("thruDate", defaultRate.getTimestamp("thruDate"));
				}
				
			}
			//List<GenericValue> partyRate 
		} catch (Exception e) {
			Debug.logError(e, e.getMessage(), MODULE);
		}
		
		return result;
	}
	
}
