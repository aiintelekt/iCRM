import org.groupfio.customer.portal.constants.CustomerPortalConstants;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.GenericValue;
import org.fio.homeapps.constants.GlobalConstants.*;
import org.fio.homeapps.util.UtilDateTime
import java.sql.Timestamp;

delegator = request.getAttribute("delegator");
inputContext = new LinkedHashMap<String, Object>();
String globalDateFormat = org.fio.homeapps.util.DataHelper.getGlobalDateFormat(delegator);
context.put("globalDateFormat", globalDateFormat);
String globalDateTimeFormat = org.fio.homeapps.util.DataHelper.getGlobalDateTimeFormat(delegator);
context.put("globalDateTimeFormat", globalDateTimeFormat);
String productPromoCodeId= request.getParameter(CustomerPortalConstants.PRODUCT_PROMO_CODE_ID);
context.put("productPromoCodeId",productPromoCodeId);
inputContext.put(CustomerPortalConstants.PRODUCT_PROMO_CODE_ID, productPromoCodeId);
productPromoCodeValues = delegator.findOne("ProductPromoCode", false,
	UtilMisc.toMap(CustomerPortalConstants.PRODUCT_PROMO_CODE_ID, productPromoCodeId));
if (UtilValidate.isNotEmpty(productPromoCodeValues)) {
	String description = productPromoCodeValues.getString(CustomerPortalConstants.DESCRIPTION);
	if (UtilValidate.isNotEmpty(description)) {
	inputContext.put(CustomerPortalConstants.DESCRIPTION, description);
	}
	String productPromoCodePurposeTypeId = productPromoCodeValues.getString(CustomerPortalConstants.PRODUCT_PROMO_CODE_PURPOSE_TYPE_ID);
	if (UtilValidate.isNotEmpty(productPromoCodePurposeTypeId)) {
		inputContext.put("productPromoCodePurposeTypeId", productPromoCodePurposeTypeId);
	}
	String useLimitPerCode = productPromoCodeValues.getString(CustomerPortalConstants.USE_LIMIT_PER_CODE);
	if (UtilValidate.isNotEmpty(useLimitPerCode)) {
		inputContext.put(CustomerPortalConstants.USE_LIMIT_PER_CODE, useLimitPerCode);
	}
	String isCombined = productPromoCodeValues.getString(CustomerPortalConstants.IS_COMBINED);
	if (UtilValidate.isNotEmpty(isCombined)) {
		inputContext.put(CustomerPortalConstants.IS_COMBINED, isCombined);
	}else {
		inputContext.put(CustomerPortalConstants.IS_COMBINED, "N");
	}
	String useLimitPerCustomer = productPromoCodeValues.getString(CustomerPortalConstants.USE_LIMIT_PER_CUSTOMER);
	if (UtilValidate.isNotEmpty(useLimitPerCustomer)) {
		inputContext.put(CustomerPortalConstants.USE_LIMIT_PER_CUSTOMER, useLimitPerCustomer);
	}
	
	String couponScannedDateStr = productPromoCodeValues.getString(CustomerPortalConstants.SCANNED_DATE);
	if (UtilValidate.isNotEmpty(couponScannedDateStr)) {
		Timestamp couponScannedDateTs = Timestamp.valueOf(couponScannedDateStr);
		if (UtilValidate.isNotEmpty(couponScannedDateTs)) {
		couponStatusValue = "Coupon Redeemed";
		context.put(CustomerPortalConstants.COUPON_STATUS, couponStatusValue);
		inputContext.put(CustomerPortalConstants.COUPON_STATUS, couponStatusValue);
		}
	}else {
		String couponThruDate = productPromoCodeValues.getString(CustomerPortalConstants.THRU_DATE);
		couponStatusValue = "Coupon Available";
		if (UtilValidate.isNotEmpty(couponThruDate)) {
			Timestamp thruDateTimestamp = Timestamp.valueOf(couponThruDate);
			 if (UtilValidate.isNotEmpty(thruDateTimestamp) && thruDateTimestamp.compareTo(UtilDateTime.nowTimestamp())>0) {
				 couponStatusValue = "Coupon Available";
			 }
			 if (UtilValidate.isNotEmpty(thruDateTimestamp) && thruDateTimestamp.compareTo(UtilDateTime.nowTimestamp())<=0) {
				 couponStatusValue = "Coupon Expired";
			 }
		}
		context.put(CustomerPortalConstants.COUPON_STATUS, couponStatusValue);
		 inputContext.put(CustomerPortalConstants.COUPON_STATUS, couponStatusValue);
	}
	String productPromoCodeStatusId = productPromoCodeValues.getString("productPromoCodeStatusId");
	if (UtilValidate.isNotEmpty(productPromoCodeStatusId)&&productPromoCodeStatusId.equals("REDEEMED")) {
		couponStatusValue = "Coupon Audited";
		context.put(CustomerPortalConstants.COUPON_STATUS, couponStatusValue);
		inputContext.put(CustomerPortalConstants.COUPON_STATUS, couponStatusValue);
	}
	String isEmployeeEligible = productPromoCodeValues.getString(CustomerPortalConstants.IS_EMPLOYEE_ELIGIBLE);
	if (UtilValidate.isNotEmpty(isEmployeeEligible)) {
		inputContext.put(CustomerPortalConstants.IS_EMPLOYEE_ELIGIBLE, isEmployeeEligible);
	}else {
		inputContext.put(CustomerPortalConstants.IS_EMPLOYEE_ELIGIBLE, "N");
	}
	String fromDate = productPromoCodeValues.getString(CustomerPortalConstants.FROM_DATE);
	if (UtilValidate.isNotEmpty(fromDate)) {
		Timestamp fromDateTimestamp = Timestamp.valueOf(fromDate);
		if(UtilValidate.isNotEmpty(fromDateTimestamp)) {
			couponFromDate = UtilDateTime.timeStampToString(fromDateTimestamp, globalDateTimeFormat, TimeZone.getDefault(), Locale.getDefault());
			if(UtilValidate.isNotEmpty(couponFromDate)) {
		context.put(CustomerPortalConstants.FROM_DATE,couponFromDate)
		inputContext.put(CustomerPortalConstants.FROM_DATE, couponFromDate);
		
			}
		}
	}
	else {
		inputContext.put(CustomerPortalConstants.FROM_DATE, "N/A");
	}
	String issueDate = productPromoCodeValues.getString(CustomerPortalConstants.ISSUE_DATE);
	if (UtilValidate.isNotEmpty(issueDate)) {
		Timestamp issueDateTimestamp = Timestamp.valueOf(issueDate);
		if(UtilValidate.isNotEmpty(issueDateTimestamp)) {
			couponIssueDate = UtilDateTime.timeStampToString(issueDateTimestamp, globalDateTimeFormat, TimeZone.getDefault(), Locale.getDefault());
			if(UtilValidate.isNotEmpty(couponIssueDate)) {
		inputContext.put(CustomerPortalConstants.ISSUE_DATE, couponIssueDate);
		
			}
		}
	}else {
		inputContext.put(CustomerPortalConstants.ISSUE_DATE, "N/A");
	}
	String scannedDate = productPromoCodeValues.getString(CustomerPortalConstants.SCANNED_DATE);
	if (UtilValidate.isNotEmpty(scannedDate)) {
		Timestamp scannedDateTimestamp = Timestamp.valueOf(scannedDate);
		if(UtilValidate.isNotEmpty(scannedDateTimestamp)) {
			couponScannedDate = UtilDateTime.timeStampToString(scannedDateTimestamp, globalDateFormat, TimeZone.getDefault(), Locale.getDefault());
			if(UtilValidate.isNotEmpty(couponScannedDate)) {
		inputContext.put(CustomerPortalConstants.SCANNED_DATE, couponScannedDate);
		
			}
		}
	}
	String thruDate = productPromoCodeValues.getString(CustomerPortalConstants.THRU_DATE);
	if(UtilValidate.isNotEmpty(thruDate)) {
		Timestamp thruDateTimestamp = Timestamp.valueOf(thruDate);
		if(UtilValidate.isNotEmpty(thruDateTimestamp)) {
			couponThruDate = UtilDateTime.timeStampToString(thruDateTimestamp, globalDateFormat, TimeZone.getDefault(), Locale.getDefault());
			if(UtilValidate.isNotEmpty(couponThruDate)) {
		context.put("thruDate",couponThruDate)
		inputContext.put(CustomerPortalConstants.THRU_DATE, couponThruDate);
		
			}
		}
		}
	
	
	String redemptionDate = productPromoCodeValues.getString(CustomerPortalConstants.REDEMPTION_DATE);
	if (UtilValidate.isNotEmpty(redemptionDate)) {
		Timestamp redemptionDateTimestamp = Timestamp.valueOf(redemptionDate);
		if(UtilValidate.isNotEmpty(redemptionDateTimestamp)) {
			couponRedemptionDate = UtilDateTime.timeStampToString(redemptionDateTimestamp, globalDateFormat, TimeZone.getDefault(), Locale.getDefault());
			if(UtilValidate.isNotEmpty(couponRedemptionDate)) {
		inputContext.put(CustomerPortalConstants.REDEMPTION_DATE, couponRedemptionDate);
		
			}
		}
	}
}
partyValues = delegator.findByAnd("ProductPromoCodeParty",
	UtilMisc.toMap(CustomerPortalConstants.PRODUCT_PROMO_CODE_ID, productPromoCodeId),null,false);
if (UtilValidate.isNotEmpty(partyValues)) {
for(GenericValue partyValue: partyValues) {
	partyId=partyValue.getString(CustomerPortalConstants.PARTY_ID);
	inputContext.put(CustomerPortalConstants.PARTY_ID, partyId);
	context.put(CustomerPortalConstants.PARTY_ID, partyId);
	inputContext.put("partyId_link", "/customer-portal/control/viewCustomer?partyId="+partyId);
}
}
else {
	partyId= "N/A";
	context.put(CustomerPortalConstants.PARTY_ID, partyId);
	inputContext.put(CustomerPortalConstants.PARTY_ID, partyId);
}
String customerPortal = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "PRODUCT_PROMO_CODE");
context.put("domainEntityType", customerPortal);
inputContext.put("domainEntityType", customerPortal);
inputContext.put("domainEntityId", productPromoCodeId);

context.put("inputContext", inputContext);

String isPhoneCampaignEnabled=org.fio.homeapps.util.DataUtil.isPhoneCampaignEnabled(delegator);
context.put("isPhoneCampaignEnabled", isPhoneCampaignEnabled);