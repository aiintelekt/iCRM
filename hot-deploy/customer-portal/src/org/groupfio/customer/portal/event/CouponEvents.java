package org.groupfio.customer.portal.event;

import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.groupfio.customer.portal.constants.CustomerPortalConstants;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastMap;

public class CouponEvents {
	private CouponEvents() {}
	private static final String MODULE = CouponEvents.class.getName();
	public static String updateCoupon(HttpServletRequest request, HttpServletResponse response) throws ParseException{
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute(CustomerPortalConstants.USER_LOGIN);
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute(CustomerPortalConstants.DISPATCHER);
		Map<String, Object> requestContext = new LinkedHashMap<>();
		Map<String, Object> couponValues = FastMap.newInstance();
		
		requestContext.put(CustomerPortalConstants.PRODUCT_PROMO_CODE_ID, context.get(CustomerPortalConstants.PRODUCT_PROMO_CODE_ID));
		requestContext.put(CustomerPortalConstants.DESCRIPTION,context.get(CustomerPortalConstants.DESCRIPTION));
		requestContext.put(CustomerPortalConstants.PRODUCT_PROMO_CODE_PURPOSE_TYPE_ID,context.get(CustomerPortalConstants.PRODUCT_PROMO_CODE_PURPOSE_TYPE_ID));
		requestContext.put(CustomerPortalConstants.USE_LIMIT_PER_CODE,context.get(CustomerPortalConstants.USE_LIMIT_PER_CODE));
		requestContext.put(CustomerPortalConstants.IS_COMBINED,context.get(CustomerPortalConstants.IS_COMBINED));
		requestContext.put(CustomerPortalConstants.USE_LIMIT_PER_CUSTOMER,context.get(CustomerPortalConstants.USE_LIMIT_PER_CUSTOMER));
		requestContext.put(CustomerPortalConstants.IS_EMPLOYEE_ELIGIBLE,context.get(CustomerPortalConstants.IS_EMPLOYEE_ELIGIBLE));
		requestContext.put(CustomerPortalConstants.THRU_DATE,context.get(CustomerPortalConstants.THRU_DATE));
		requestContext.put(CustomerPortalConstants.SCANNED_DATE, context.get(CustomerPortalConstants.SCANNED_DATE));
		requestContext.put(CustomerPortalConstants.REDEMPTION_DATE,context.get(CustomerPortalConstants.REDEMPTION_DATE));
		requestContext.put("domainEntityType",context.get("domainEntityType"));

		couponValues.put("couponData", requestContext);
		couponValues.put("userLogin", userLogin);
		try {
			Map<String, Object> updateCouponMap = dispatcher.runSync("customer.editCoupon", couponValues);
			
			if (ServiceUtil.isError(updateCouponMap) || ServiceUtil.isFailure(updateCouponMap)) {
				request.setAttribute(CustomerPortalConstants.ERROR_MESSAGE,
						ServiceUtil.getErrorMessage(updateCouponMap));
				return CustomerPortalConstants.ERROR;
			}
			request.setAttribute(CustomerPortalConstants.EVENT_MESSAGE, "Coupon Updated Successfully");
		}catch (GenericServiceException e) {
			Debug.logError(e.getMessage(), MODULE);
			request.setAttribute(CustomerPortalConstants.ERROR_MESSAGE, e.getMessage());
			return CustomerPortalConstants.ERROR;
		}
		return CustomerPortalConstants.SUCCESS;
	}
		
}
