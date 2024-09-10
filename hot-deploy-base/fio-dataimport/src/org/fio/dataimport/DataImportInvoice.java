package org.fio.dataimport;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

/**
 * @author Group Fio
 *
 */
public class DataImportInvoice {
	public static Map<String, Object> DataImportInvoiceHeader(DispatchContext dctx,
			Map<String, ? extends Object> context) {

		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();

		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> result = null;
		try {

			List<GenericValue> importDatas = (List) context.get("importDatas");
			

			if (importDatas == null) {
				EntityCondition conditions = EntityCondition.makeCondition(EntityOperator.OR,
						EntityCondition.makeCondition("importStatusId", EntityOperator.EQUALS, "DATAIMP_NOT_PROC"),
						EntityCondition.makeCondition("importStatusId", EntityOperator.EQUALS, "DATAIMP_FAILED"),
						EntityCondition.makeCondition("importStatusId", EntityOperator.EQUALS, null));

				EntityCondition statusCond = null;
				if (UtilValidate.isNotEmpty(context.get("batchId"))) {
					String batchId = (String) context.get("batchId");
					statusCond = EntityCondition.makeCondition(EntityOperator.AND,
							EntityCondition.makeCondition("batchId", EntityOperator.EQUALS, batchId), conditions);

				}

				if (UtilValidate.isNotEmpty(context.get("batchId")))
					importDatas = delegator.findList("DataImportInvoiceHeader", statusCond, null, null, null, false);
				else
					importDatas = delegator.findList("DataImportInvoiceHeader", conditions, null, null, null, false);

			}

			Map<String, Object> callCtxt = FastMap.newInstance();

			for (GenericValue genericValue : importDatas) {
				String invoiceId = genericValue.getString("invoiceId");
				String invoiceTypeId = genericValue.getString("invoiceTypeId");
				Timestamp invoiceDate = genericValue.getTimestamp("invoiceDate");
				String currencyUomId = genericValue.getString("currencyUomId");
				String partyIdFrom = genericValue.getString("partyIdFrom");
				String partyId = genericValue.getString("partyId");
				String description = genericValue.getString("description");
				String referenceNumber = genericValue.getString("referenceNumber");
				BigDecimal adjustedAmount = genericValue.getBigDecimal("adjustedAmount");
				BigDecimal appliedAmount = genericValue.getBigDecimal("appliedAmount");
				BigDecimal invoiceTotal = genericValue.getBigDecimal("invoiceTotal");
				BigDecimal openAmount = genericValue.getBigDecimal("openAmount");
				Timestamp paidDate = genericValue.getTimestamp("paidDate");
				String statusId = "INVOICE_IN_PROCESS";

				callCtxt = FastMap.newInstance();

				callCtxt.put("invoiceId", invoiceId);
				callCtxt.put("invoiceTypeId", invoiceTypeId);
				callCtxt.put("statusId", statusId);
				callCtxt.put("partyIdFrom", partyIdFrom);
				callCtxt.put("partyId", partyId);
				callCtxt.put("currencyUomId", currencyUomId);
				callCtxt.put("description", description);
				callCtxt.put("invoiceDate", invoiceDate);
				callCtxt.put("adjustedAmount", adjustedAmount);
				callCtxt.put("appliedAmount", appliedAmount);
				callCtxt.put("adjustedAmount", adjustedAmount);
				callCtxt.put("invoiceTotal", invoiceTotal);
				callCtxt.put("openAmount", openAmount);
				callCtxt.put("paidDate", paidDate);

				callCtxt.put("referenceNumber", referenceNumber);

				callCtxt.put("userLogin", userLogin);

				Map<String, Object> results = dispatcher.runSync("createInvoice", callCtxt);
				Debug.log("genInvoicdId******" + results.get("invoiceId"));
				String genInvoiceId = (String) results.get("invoiceId");

				EntityCondition cond = EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceId);

				List<GenericValue> invoiceItems = delegator.findList("DataImportInvoiceItem", cond, null, null, null,
						false);
				Map<String, Object> invoiceItemMap = FastMap.newInstance();

				for (GenericValue genericValue2 : invoiceItems) {

					String invoiceItemSeqId1 = genericValue2.getString("invoiceItemSeqId");
					BigDecimal amount = genericValue2.getBigDecimal("amount");
					BigDecimal quantity = genericValue2.getBigDecimal("quantity");
					String invoiceItemTypeId = genericValue2.getString("invoiceItemTypeId");
					String descriptionItem = genericValue2.getString("description");
					String productId = genericValue2.getString("productId");

					invoiceItemMap.put("userLogin", userLogin);
					invoiceItemMap.put("invoiceId", genInvoiceId);
					invoiceItemMap.put("invoiceItemSeqId", invoiceItemSeqId1);
					invoiceItemMap.put("amount", amount);
					invoiceItemMap.put("invoiceItemTypeId", invoiceItemTypeId);
					invoiceItemMap.put("productId", productId);
					invoiceItemMap.put("quantity", quantity);
					invoiceItemMap.put("description", descriptionItem);

					Map<String, Object> results1 = dispatcher.runSync("createInvoiceItem", invoiceItemMap);
					if (ServiceUtil.isSuccess(results1)) {
						// delegator.removeValue(genericValue2);
						genericValue2.put("importStatusId", "DATAIMP_IMPORTED");
						genericValue2.store();
						TransactionUtil.commit();
					}
				}
				if (ServiceUtil.isSuccess(results)) {
					// delegator.removeValue(genericValue);
					genericValue.put("importStatusId", "DATAIMP_IMPORTED");
					genericValue.store();
					TransactionUtil.commit();
				}
			}
			result = ServiceUtil.returnSuccess();

		} catch (GenericEntityException | GenericServiceException e) {
			// TODO Auto-generated catch block
			Debug.log("Exception in data import invoice==="+e.getMessage());
		}
		return result;
	}

	public static Map<String, Object> DataImportInvoiceItem(DispatchContext dctx,
			Map<String, ? extends Object> context) {

		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();

		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> result = null;
		try {
			
			List<GenericValue> importDatas = (List) context.get("importDatas");;
	    	
	    	if (importDatas == null) {
	    		EntityCondition conditions = EntityCondition.makeCondition(EntityOperator.OR,
	                    EntityCondition.makeCondition("importStatusId", EntityOperator.EQUALS, "DATAIMP_NOT_PROC"),
	                    EntityCondition.makeCondition("importStatusId", EntityOperator.EQUALS, "DATAIMP_FAILED"),
	                    EntityCondition.makeCondition("importStatusId", EntityOperator.EQUALS, null));
	           
	            EntityCondition statusCond =  null;
	            if(UtilValidate.isNotEmpty(context.get("batchId"))){
	            	String batchId = (String) context.get("batchId");
	            	statusCond = EntityCondition.makeCondition(EntityOperator.AND,
	                         EntityCondition.makeCondition("batchId", EntityOperator.EQUALS, batchId),
	                         conditions );
	            	
	            }
	            
	            if(UtilValidate.isNotEmpty(context.get("batchId")))
	            	importDatas = delegator.findList("DataImportInvoiceItem", statusCond, null,null,null,false);
	            else
	            	importDatas = delegator.findList("DataImportInvoiceItem", conditions, null,null,null,false);
	            
	    	}

			Map<String, Object> invoiceItemMap = FastMap.newInstance();

			for (GenericValue genericValue2 : importDatas) {

				String genInvoiceId = genericValue2.getString("invoiceId");
				GenericValue checkInvoice = delegator.findOne("Invoice", UtilMisc.toMap("invoiceId", genInvoiceId),
						false);
				String invoiceItemSeqId1 = genericValue2.getString("invoiceItemSeqId");
				BigDecimal amount = genericValue2.getBigDecimal("amount");
				BigDecimal quantity = genericValue2.getBigDecimal("quantity");
				String invoiceItemTypeId = genericValue2.getString("invoiceItemTypeId");
				String descriptionItem = genericValue2.getString("description");
				String productId = genericValue2.getString("productId");

				invoiceItemMap = FastMap.newInstance();
				
				invoiceItemMap.put("userLogin", userLogin);
				invoiceItemMap.put("invoiceId", genInvoiceId);
				invoiceItemMap.put("invoiceItemSeqId", invoiceItemSeqId1);
				invoiceItemMap.put("amount", amount);
				invoiceItemMap.put("invoiceItemTypeId", invoiceItemTypeId);
				invoiceItemMap.put("productId", productId);
				invoiceItemMap.put("quantity", quantity);
				invoiceItemMap.put("description", descriptionItem);

				if (UtilValidate.isNotEmpty(checkInvoice)) {
					Map<String, Object> results1 = dispatcher.runSync("createInvoiceItem", invoiceItemMap);
					if (ServiceUtil.isSuccess(results1)) {
						// delegator.removeValue(genericValue2);
						genericValue2.put("importStatusId", "DATAIMP_IMPORTED");
						genericValue2.store();
						TransactionUtil.commit();
					}
				}
			}

			result = ServiceUtil.returnSuccess();

		} catch (GenericEntityException | GenericServiceException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			Debug.log("Exception in dataimportinvoice==="+e.getMessage());
		} 

		return result;
	}
}
