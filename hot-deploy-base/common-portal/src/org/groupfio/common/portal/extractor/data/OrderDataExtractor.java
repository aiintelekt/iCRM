package org.groupfio.common.portal.extractor.data;

import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

import org.fio.homeapps.util.ParamUtil;
import org.fio.homeapps.util.UtilDateTime;
import org.groupfio.common.portal.extractor.constants.DataConstants;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Sharif
 *
 */
public class OrderDataExtractor extends DataExtractor {
	
	private static final Logger log = LoggerFactory.getLogger(OrderDataExtractor.class);
	
	public OrderDataExtractor(Data extractedData) {
		super(extractedData);
	}
	
	@Override
	public Map<String, Object> retrieve(Map<String, Object> context) {
		if (UtilValidate.isNotEmpty(extractedData)) {
			extractedData.retrieve(context);
		}
		return retrieveData(context);
	}

	public Map<String, Object> retrieveData(Map<String, Object> context) {
		
		Map<String, Object> response = new LinkedHashMap<String, Object>();
		try {
			if (UtilValidate.isNotEmpty(context)) {
				Delegator delegator = (Delegator) context.get("delegator"); 
				Map<String, Object> request = (Map<String, Object>) context.get("request");
				response = (Map<String, Object>) context.get("response");
				
				String globalDateFormat = org.groupfio.common.portal.util.DataHelper.getGlobalDateFormat(delegator);
				
				String orderId = ParamUtil.getString(request, "orderId");
				String isRmsOrder = ParamUtil.getString(request, "isRmsOrder");
				if (UtilValidate.isNotEmpty(orderId)) {
					
					Map<String, Object> orderData = new LinkedHashMap<String, Object>();
					GenericValue entity = null;
					String dateColumnName = "invoiceDate";
					if (UtilValidate.isEmpty(isRmsOrder) || isRmsOrder.equals("N")) {
						entity = EntityQuery.use(delegator).select("invoiceDate").from("InvoiceTransactionMaster").where("transactionNumber", orderId).cache(false).queryFirst();
					} else if (UtilValidate.isNotEmpty(isRmsOrder) && isRmsOrder.equals("Y")) {
						entity = EntityQuery.use(delegator).select("orderDate").from("RmsTransactionMaster").where("transactionNumber", orderId).cache(false).queryFirst();
						dateColumnName = "orderDate";
					}
					
					if (UtilValidate.isNotEmpty(entity)) {
						Timestamp orderDate = entity.getTimestamp(dateColumnName);
						String orderDateStr = null;
						if (UtilValidate.isNotEmpty(orderDate)) {
							orderDateStr = UtilDateTime.timeStampToString(orderDate, globalDateFormat, TimeZone.getDefault(), Locale.getDefault());
						}
						
						orderData.put(DataConstants.ORDER_TAG.get("ORDER_DATE"), Objects.toString(orderDateStr, ""));
					}
					
					response.put("orderData", orderData);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
		
		return response;
	}

}
