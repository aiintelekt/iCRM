package org.fio.customer.service.service.coupon;

import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;

import org.fio.customer.service.constant.CustomerConstants;
import org.fio.homeapps.util.UtilDateTime;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastMap;

public class CustomerServices {
	@SuppressWarnings({ "rawtypes" })
	public static Map editCoupon(DispatchContext dctx, Map context) throws ParseException, GenericEntityException {
		Delegator delegator = (Delegator) dctx.getDelegator();
		String globalDateFormat = org.fio.homeapps.util.DataHelper.getGlobalDateFormat(delegator);
		Map requestContextValues=(Map) context.get("couponData");
		Timestamp thruDateTs = null;
		Timestamp curThruDate=null;
		Map<String, Object> requestContext = new LinkedHashMap<>();
		Map<String, Object> callCtxt = FastMap.newInstance();
		GenericValue userLogin = (GenericValue) context.get(CustomerConstants.USER_LOGIN);
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map requestContextValue;
		String useLimitCode = (String) requestContextValues.get(CustomerConstants.USE_LIMIT_PER_CODE);
		Long useLimitPerCode = null;
		if(UtilValidate.isNotEmpty(useLimitCode)) {
			useLimitPerCode = Long.valueOf(useLimitCode);
		}
		String useLimitCustomer = (String) requestContextValues.get(CustomerConstants.USE_LIMIT_PER_CUSTOMER);
		Long useLimitPerCustomer = null;
		if(UtilValidate.isNotEmpty(useLimitCustomer)) {
			useLimitPerCustomer = Long.valueOf(useLimitCustomer);
		}
		String domainEntityType =(String) requestContextValues.get("domainEntityType");
		String productPromoCodeId =(String) requestContextValues.get(CustomerConstants.PRODUCT_PROMO_CODE_ID);
		String description =(String) requestContextValues.get(CustomerConstants.DESCRIPTION);
		String productPromoCodePurposeTypeId =(String) requestContextValues.get(CustomerConstants.PRODUCT_PROMO_CODE_PURPOSE_TYPE_ID);
		String isCombined =(String) requestContextValues.get(CustomerConstants.IS_COMBINED);
		String isEmployeeEligible =(String) requestContextValues.get(CustomerConstants.IS_EMPLOYEE_ELIGIBLE);
		String thruDate =(String) requestContextValues.get(CustomerConstants.THRU_DATE);
		String scannedDate =(String) requestContextValues.get(CustomerConstants.SCANNED_DATE);
		String redemptionDate =(String) requestContextValues.get(CustomerConstants.REDEMPTION_DATE);
		String couponStatus = "CREATED";
		GenericValue productPromoCode = delegator.makeValue("ProductPromoCode");
		productPromoCode.put(CustomerConstants.PRODUCT_PROMO_CODE_ID, productPromoCodeId);
		productPromoCode.put(CustomerConstants.DESCRIPTION,description);
		productPromoCode.put(CustomerConstants.PRODUCT_PROMO_CODE_PURPOSE_TYPE_ID,productPromoCodePurposeTypeId);
		if(UtilValidate.isNotEmpty(useLimitPerCode)) {
			productPromoCode.put(CustomerConstants.USE_LIMIT_PER_CODE,useLimitPerCode);
		}
		else{
			productPromoCode.put(CustomerConstants.USE_LIMIT_PER_CODE, null);
		}
		if(UtilValidate.isNotEmpty(isCombined)) {
			productPromoCode.put(CustomerConstants.IS_COMBINED,isCombined);
		}else {
			productPromoCode.put(CustomerConstants.IS_COMBINED,"N");
		}
		if(UtilValidate.isNotEmpty(useLimitPerCustomer)) {
			productPromoCode.put(CustomerConstants.USE_LIMIT_PER_CUSTOMER,useLimitPerCustomer);
		}
		else{
			productPromoCode.put(CustomerConstants.USE_LIMIT_PER_CUSTOMER, null);
		}
		if(UtilValidate.isNotEmpty(isEmployeeEligible)) {
			productPromoCode.put(CustomerConstants.IS_EMPLOYEE_ELIGIBLE,isEmployeeEligible);
		}else {
			productPromoCode.put(CustomerConstants.IS_EMPLOYEE_ELIGIBLE,"N");
		}
		String noteString="";
		if(UtilValidate.isNotEmpty(thruDate)) {
			thruDateTs = UtilDateTime.stringToTimeStamp(thruDate, globalDateFormat, TimeZone.getDefault(), Locale.getDefault());
			Timestamp curThruDateTs=UtilDateTime.getDayEnd(thruDateTs);
			GenericValue productPromoCodeValue = delegator.findOne("ProductPromoCode", false, UtilMisc.toMap(CustomerConstants.PRODUCT_PROMO_CODE_ID, productPromoCodeId));
			if(UtilValidate.isNotEmpty(productPromoCodeValue)){
				if( UtilValidate.isNotEmpty(productPromoCodeValue.getTimestamp("thruDate"))){
					curThruDate = productPromoCodeValue.getTimestamp("thruDate");
					if(!curThruDateTs.equals(curThruDate)) {
						noteString = "Coupon number expire date has been changed from "+curThruDate+" to "+curThruDateTs;
						productPromoCode.put(CustomerConstants.THRU_DATE, curThruDateTs);
					}
				}else {
					if(UtilValidate.isEmpty(productPromoCodeValue.getTimestamp("thruDate"))){
						productPromoCode.put(CustomerConstants.THRU_DATE, curThruDateTs);
						noteString = "Coupon number expire date is "+curThruDateTs;	
					}
				}
			}else {
				productPromoCode.put(CustomerConstants.THRU_DATE, curThruDateTs);
				noteString = "Coupon number expire date is "+curThruDateTs;
			}
		}else{
			productPromoCode.put(CustomerConstants.THRU_DATE, null);
			noteString = "Coupon number expire date is empty";
		}          
		if(!noteString.equals("")) {
			requestContext.put("domainEntityId", productPromoCodeId);
			requestContext.put("note", noteString);
			requestContext.put("domainEntityType", domainEntityType);

			callCtxt.put("requestContext", requestContext);
			callCtxt.put("userLogin", userLogin);
			try {
				Map<String, Object>  callResult = dispatcher.runSync("common.createNoteData", callCtxt);
				Debug.log("callResult"+callResult);
				if (ServiceUtil.isError(callResult) || ServiceUtil.isFailure(callResult)) {
					return callResult;
				}
			}catch(Exception e) {
				e.printStackTrace();
				requestContextValue = ServiceUtil.returnError("error occur while calling note service");
				return requestContextValue;
			}
		}
		if(UtilValidate.isNotEmpty(scannedDate)) {
			Timestamp scannedDateTs = UtilDateTime.stringToTimeStamp(scannedDate, globalDateFormat, TimeZone.getDefault(), Locale.getDefault());
			productPromoCode.put(CustomerConstants.SCANNED_DATE, UtilDateTime.getDayEnd(scannedDateTs));
		}
		else{
			productPromoCode.put(CustomerConstants.SCANNED_DATE, null);
		}
		if(UtilValidate.isNotEmpty(redemptionDate)) {
			Timestamp redemptionDateTs = UtilDateTime.stringToTimeStamp(redemptionDate, globalDateFormat, TimeZone.getDefault(), Locale.getDefault());
			productPromoCode.put(CustomerConstants.REDEMPTION_DATE, UtilDateTime.getDayEnd(redemptionDateTs));
		}
		else{
			productPromoCode.put(CustomerConstants.REDEMPTION_DATE, null);
		}
		if(UtilValidate.isNotEmpty(scannedDate))
			couponStatus = "SCANNED";
		else if(UtilValidate.isNotEmpty(redemptionDate))
			couponStatus = "REDEEMED";
		else {
			if(UtilValidate.isNotEmpty(thruDate)&&thruDateTs.compareTo(UtilDateTime.nowTimestamp())<=0)
				couponStatus = "EXPIRED";
		}
		productPromoCode.put(CustomerConstants.PRODUCT_PROMO_CODE_STATUS_ID, couponStatus);
		delegator.createOrStore(productPromoCode);
		requestContextValue=ServiceUtil.returnSuccess("Coupon Updated Successfully");
		return requestContextValue;
	}
	@SuppressWarnings("rawtypes")
	public static Map searchPosOrder(DispatchContext dctx, Map context) throws ParseException {
		Map requestContext = (Map) context.get("requestContext");
		Delegator delegator = (Delegator) dctx.getDelegator();
		Map < String, Object > results = new HashMap < String, Object > ();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		List<EntityCondition> conditions = new ArrayList<EntityCondition>();
		Locale locale = (Locale) context.get("locale");
		NumberFormat nf = NumberFormat.getInstance(locale);
		String globalDateFormat = org.fio.homeapps.util.DataHelper.getGlobalDateFormat(delegator);

		String orderDataType = (String) requestContext.get("orderDataType");
		String partyId = (String) requestContext.get("partyId");
		String storeNumber = (String) requestContext.get("storeNumber");
		String registerNumber = (String) requestContext.get("registerNumber");
		String transactionNumber = (String) requestContext.get("transactionNumber");
		String transactionDate_from = (String) requestContext.get("transactionDate_from");
		String transactionDate_to = (String) requestContext.get("transactionDate_to");
		
		if(UtilValidate.isNotEmpty(registerNumber)) {
			conditions.add(EntityCondition.makeCondition("registerNumber", EntityOperator.LIKE,"%"+registerNumber + "%"));
		}
		
		if(UtilValidate.isNotEmpty(storeNumber)) {
			conditions.add(EntityCondition.makeCondition("storeNumber", EntityOperator.LIKE,"%"+storeNumber + "%"));
		}

		if(UtilValidate.isNotEmpty(transactionNumber)) {
			conditions.add(EntityCondition.makeCondition("transactionNumber", EntityOperator.LIKE,"%"+transactionNumber + "%"));
		}
		
		if(UtilValidate.isNotEmpty(partyId)) {
			conditions.add(EntityCondition.makeCondition("billToPartyId", EntityOperator.LIKE,"%"+partyId + "%"));
		}
		
		if (UtilValidate.isNotEmpty(orderDataType)) {
		    String dateField = null;
		    if (orderDataType.equals("ITM")) {
		        dateField = "transactionDate";
		    } else if (orderDataType.equals("RMS")) {
		        dateField = "orderDate";
		    }
		    if(UtilValidate.isNotEmpty(transactionDate_from)) {
				conditions.add(EntityCondition.makeCondition(dateField, EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.stringToTimeStamp(transactionDate_from, globalDateFormat, TimeZone.getDefault(), locale)));
			}
			if(UtilValidate.isNotEmpty(transactionDate_to)) {
				conditions.add(EntityCondition.makeCondition(dateField, EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.stringToTimeStamp(transactionDate_to, globalDateFormat, TimeZone.getDefault(), locale)));
			}
		}
		
		Set < String > fieldsToSelect = new TreeSet < String > ();
		fieldsToSelect.add("transactionNumber");
		fieldsToSelect.add("registerNumber");
		fieldsToSelect.add("sequenceNumber");
		fieldsToSelect.add("transactionType");
		fieldsToSelect.add("billToPartyId");
		fieldsToSelect.add("loyaltyId");
		fieldsToSelect.add("storeNumber");
		fieldsToSelect.add("totalSalesAmount");

		DynamicViewEntity dynamicViewEntity = new DynamicViewEntity();

		if(UtilValidate.isNotEmpty(orderDataType) && orderDataType.equals("ITM")) {
			dynamicViewEntity.addMemberEntity("TM", "InvoiceTransactionMaster");
			dynamicViewEntity.addAlias("TM", "invoiceId");
			dynamicViewEntity.addAlias("TM", "transactionDate");
			fieldsToSelect.add("invoiceId");
			fieldsToSelect.add("transactionDate");
		}else {
			fieldsToSelect.add("orderId");
			fieldsToSelect.add("orderDate");
			dynamicViewEntity.addMemberEntity("TM", "RmsTransactionMaster");
			dynamicViewEntity.addAlias("TM", "orderId");
			dynamicViewEntity.addAlias("TM", "orderDate");
		}

		dynamicViewEntity.addAlias("TM", "transactionNumber");
		dynamicViewEntity.addAlias("TM", "registerNumber");
		dynamicViewEntity.addAlias("TM", "sequenceNumber");
		dynamicViewEntity.addAlias("TM", "transactionType");
		dynamicViewEntity.addAlias("TM", "billToPartyId");
		dynamicViewEntity.addAlias("TM", "loyaltyId");
		dynamicViewEntity.addAlias("TM", "storeNumber");
		dynamicViewEntity.addAlias("TM", "totalSalesAmount");

		int viewIndex = 0;
		try {
			viewIndex = Integer.parseInt((String) context.get("VIEW_INDEX"));
		} catch (Exception e) {
			viewIndex = 0;
		}
		results.put("viewIndex", Integer.valueOf(viewIndex));
		int viewSize = 0;
		try {
			viewSize = Integer.parseInt((String) context.get("VIEW_SIZE"));
		}catch (Exception e) {
			try {
				viewSize = Integer.parseInt(org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "FIO_GRID_FETCH_LIMIT"));
			}catch(Exception ex) {
				viewSize=1000;
			}
		}
		results.put("viewSize", Integer.valueOf(viewSize));
		int highIndex = 0;
		int lowIndex = 0;
		int resultListSize = 0;
		lowIndex = viewIndex * viewSize + 1;
		highIndex = (viewIndex + 1) * viewSize;
		List < GenericValue > transactionMasterList = null;
		try {
			transactionMasterList = EntityQuery.use(delegator).select(fieldsToSelect).from(dynamicViewEntity).where(EntityCondition.makeCondition(conditions, EntityOperator.AND)).maxRows(highIndex).queryList();
			results.put("result", transactionMasterList);
			resultListSize = transactionMasterList.size();
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		results.put("highIndex", Integer.valueOf(highIndex));
		results.put("lowIndex", Integer.valueOf(lowIndex));
		results.put("chunks", (int) Math.ceil((double) resultListSize / (double) viewSize));
		results.put("totalRecords", nf.format(resultListSize));
		results.put("recordCount", resultListSize);
		results.put("viewIndex", viewIndex);
		results.put("chunkSize", viewSize);
		return results;
	}
	
}
