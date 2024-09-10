package org.fio.customer.service.constant;
/***
 * 
 * @author Mahendran T
 *
 */
public class CustomerConstants {
	
	public static final String DEFAULT_RESP_PER = "admin";
	
	public static class VerifyMode {
		private VerifyMode() {}
		public static String VERIFICATION = "VERIFICATION";
		public static String VALIDATION = "VALIDATION";
	}
	public static class UserAccountMode {
		private UserAccountMode() {}
		public static String FORGET_PASSWORD = "FORGET_PASSWORD";
		public static String UPDATE_PASSWORD = "UPDATE_PASSWORD";
		public static String CHANGE_USER_ID = "CHANGE_USER_ID";
		
	}
	public static class EmailVerifyStatus{
		private EmailVerifyStatus() {}
		
		public static String SENT="SENT";
		public static String VERIFIED="VERIFIED";
		
	}
	
	public static final String DELEGATOR="delegator";
	public static final String DISPATCHER="dispatcher";
	public static final String PRODUCT_PROMO_ID="productPromoId";
	public static final String PRODUCT_PROMO_CODE_GROUP_ID="productPromoCodeGroupId";
	public static final String PRODUCT_PROMO_TYPE_ID="productPromoTypeId";
	public static final String COUPON_STATUS="couponStatus";
	public static final String THRU_DATE="thruDate";
	public static final String SCANNED_DATE="scannedDate";
	public static final String PRODUCT_PROMO_CODE_ID="productPromoCodeId";
	public static final String DESCRIPTION="description";
	public static final String CREATED_DATE="createdDate";
	public static final String ISSUE_DATE="issueDate";
	public static final String REDEMPTION_DATE="redemptionDate";
	public static final String PRODUCT_PROMO_CODE_PURPOSE_TYPE_ID="productPromoCodePurposeTypeId";
	public static final String ORDER_ID="orderId";
	public static final String REDEEM_COUNT="redeemCount";
	public static final String USER_LOGIN="userLogin";
	public static final String PROMO_NAME="promoName";
	public static final String USE_LIMIT_PER_CODE="useLimitPerCode";
	public static final String USE_LIMIT_PER_CUSTOMER="useLimitPerCustomer";
	public static final String IS_COMBINED="isCombined";
	public static final String PARTY_ID="partyId";
	public static final String IS_EMPLOYEE_ELIGIBLE="isEmployeeEligible";
	public static final String FROM_DATE="fromDate";
	public static final String ERROR_MESSAGE="errorMessage";
	public static final String EVENT_MESSAGE="eventMessage";
	public static final String ERROR="error";
	public static final String SUCCESS="success";
	public static final String NOTE_INFO="noteInfo";
	public static final String NOTE_PARTY="noteParty";
	public static final String PROMOTION="promotion";
	public static final String PRODUCT_PROMO_CODE_STATUS_ID="productPromoCodeStatusId";
}
