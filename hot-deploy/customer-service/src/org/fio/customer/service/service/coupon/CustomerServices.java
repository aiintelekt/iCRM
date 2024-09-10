package org.fio.customer.service.service.coupon;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.fio.customer.service.constant.CustomerConstants;
import org.fio.homeapps.util.UtilDateTime;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
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
}
