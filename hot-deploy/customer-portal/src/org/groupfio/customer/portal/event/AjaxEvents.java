package org.groupfio.customer.portal.event;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.fio.admin.portal.constant.AdminPortalConstant.DateTimeTypeConstant;
import org.fio.admin.portal.util.DataUtil;
import org.fio.homeapps.util.DataHelper;
import org.fio.homeapps.util.EnumUtil;
import org.fio.homeapps.util.PartyHelper;
import org.fio.homeapps.util.QueryUtil;
import org.fio.homeapps.util.SrDataHelper;
import org.fio.homeapps.util.UtilHttp;
import org.groupfio.common.portal.util.LoyaltyUtil;
import org.groupfio.common.portal.util.UtilCampaign;
import org.groupfio.customer.portal.constants.CustomerPortalConstants;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.jdbc.SQLProcessor;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.model.ModelUtil;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import java.sql.SQLException;

/**
 * 
 * @author Prabhakar
 * @since 28-Oct-2020
 *
 */

public class AjaxEvents {
	private static final String _ERROR_MESSAGE = "_ERROR_MESSAGE_";
	private Delegator delegator;
	private AjaxEvents() {}

	private static final String MODULE = AjaxEvents.class.getName();
	public static String doJSONResponse(HttpServletResponse response, JSONObject jsonObject) {
		return doJSONResponse(response, jsonObject.toString());
	}

	public static String doJSONResponse(HttpServletResponse response, Collection<?> collection) {
		return doJSONResponse(response, JSONArray.fromObject(collection).toString());
	}

	public static String doJSONResponse(HttpServletResponse response, Map map) {
		return doJSONResponse(response, JSONObject.fromObject(map));
	}

	public static String doJSONResponse(HttpServletResponse response, String jsonString) {
		String result = "success";

		response.setContentType("application/x-json");
		try {
			response.setContentLength(jsonString.getBytes("UTF-8").length);
		} catch (UnsupportedEncodingException e) {
			Debug.logWarning("Could not get the UTF-8 json string due to UnsupportedEncodingException: " + e.getMessage(), MODULE);
			response.setContentLength(jsonString.length());
		}

		Writer out;
		try {
			out = response.getWriter();
			out.write(jsonString);
			out.flush();
		} catch (IOException e) {
			Debug.logError(e, "Failed to get response writer", MODULE);
			result = "error";
		}
		return result;
	}

	public static GenericValue getUserLogin(HttpServletRequest request) {
		HttpSession session = request.getSession();
		return (GenericValue) session.getAttribute("userLogin");
	}	

	public static String updateloyaltyidEnabled(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		String partyid = request.getParameter("partyId");
		String loyaltyidstatus = request.getParameter("isLoyaltyEnabled");

		if(UtilValidate.isNotEmpty(loyaltyidstatus)&& UtilValidate.isNotEmpty(partyid)) {
			Map<String,Object> updateloyaltyIdEnabledResult = LoyaltyUtil.updateloyaltyidEnabled(delegator, partyid, loyaltyidstatus);
			if(UtilValidate.isNotEmpty(updateloyaltyIdEnabledResult.get(_ERROR_MESSAGE))) {
				request.setAttribute(_ERROR_MESSAGE, updateloyaltyIdEnabledResult.get(_ERROR_MESSAGE));
				return "error";
			}
		}

		return "success";
	}

	public static String replaceLoyalty(HttpServletRequest request, HttpServletResponse response) {

		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		String partyId = request.getParameter("partyId");

		if (UtilValidate.isNotEmpty(partyId)) {
			Map<String, Object> resutMessage = LoyaltyUtil.replaceLoyalty(delegator, partyId);
			if(resutMessage.containsKey(_ERROR_MESSAGE)) {
				request.setAttribute(_ERROR_MESSAGE, resutMessage.get(_ERROR_MESSAGE));
				return "error";
			}
		}else {
			request.setAttribute(_ERROR_MESSAGE, "please provide party id");
			return "error";
		}
		return "success";
	}

	public static String getAssignedOrNewLoyaltyNumber(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin=(GenericValue)session.getAttribute("userLogin");
		String partyId = request.getParameter("partyId");
		String updateToPartyAttribute=request.getParameter("updateToPartyAttribute");	

		if (UtilValidate.isNotEmpty(partyId)) {
			Map<String, Object> resutMessage = LoyaltyUtil.getAssignedOrNewLoyaltyNumber(delegator, userLogin, partyId, updateToPartyAttribute);
			if(resutMessage.containsKey(_ERROR_MESSAGE)) {
				request.setAttribute(_ERROR_MESSAGE, resutMessage.get(_ERROR_MESSAGE));
				return "error";
			}
		}else {
			request.setAttribute(_ERROR_MESSAGE, "please provide party id");
			return "error";
		}

		return "success";
	}

	public static String searchCoupon(HttpServletRequest request, HttpServletResponse response)
			throws GenericEntityException {
		Map<String, Object> results = new HashMap<String, Object>();
		Delegator delegator = (Delegator) request.getAttribute(CustomerPortalConstants.DELEGATOR);
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute(CustomerPortalConstants.DISPATCHER);
		Map < String, Object > context = UtilHttp.getCombinedMap(request);
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		String globalDateTimeFormat =DataHelper.getGlobalDateTimeFormat(delegator);

		String externalLoginKey = (String) request.getAttribute("externalLoginKey");

		try {
			String productPromoId = (String) context.get("productPromoId");
			String partyId = (String) context.get("partyId");
			String productPromoCodeGroupId =(String) context.get("productPromoCodeGroupId");
			String productPromoTypeId = (String) context.get(CustomerPortalConstants.PRODUCT_PROMO_TYPE_ID);
			String couponStatus = (String) context.get(CustomerPortalConstants.COUPON_STATUS);
			Map<String, Object> requestContext = new LinkedHashMap<>();
			Map<String, Object> callContext=FastMap.newInstance();
			requestContext.put("productPromoId",productPromoId);
			requestContext.put("productPromoCodeGroupId",productPromoCodeGroupId);
			requestContext.put(CustomerPortalConstants.PRODUCT_PROMO_TYPE_ID,productPromoTypeId);
			requestContext.put(CustomerPortalConstants.COUPON_STATUS,couponStatus);
			requestContext.put("customerId",partyId);

			callContext.put("requestContext", requestContext);

			Map<String, Object> updateProductMap = dispatcher.runSync("loyalty.findCoupon",callContext );
			if (ServiceUtil.isSuccess(updateProductMap) && UtilValidate.isNotEmpty(updateProductMap.get("couponLists"))) {
				@SuppressWarnings("unchecked")
				List<GenericValue> couponLists = (List<GenericValue>) updateProductMap.get("couponLists");		

				if(UtilValidate.isNotEmpty(couponLists)) {
					List<GenericValue> productPromoCodeGroupAttrList= EntityQuery.use(delegator).select("productPromoCodeGroupId", "attrName", "attrValue").from("ProductPromoCodeGroupAttr").where("attrName","EXTERNAL_PROMO_ID").queryList();
					Map<String, Object> extenalCouponIdMap = DataUtil.getMapFromGeneric(productPromoCodeGroupAttrList, "productPromoCodeGroupId", "attrValue", false);
					
					for(GenericValue couponValue : couponLists) {
						try {
							Map<String, Object> couponData = new HashMap<String, Object>();

							String groupId =couponValue.getString(CustomerPortalConstants.PRODUCT_PROMO_CODE_GROUP_ID);
							String productPromoCodeId =couponValue.getString(CustomerPortalConstants.PRODUCT_PROMO_CODE_ID);
							if(UtilValidate.isNotEmpty(productPromoCodeId)) {
								couponData.put(CustomerPortalConstants.PRODUCT_PROMO_CODE_ID, productPromoCodeId);
							}
							String promoName=couponValue.getString(CustomerPortalConstants.PROMO_NAME);
							if(UtilValidate.isNotEmpty(promoName)) {
								couponData.put(CustomerPortalConstants.PROMO_NAME,promoName+"("+couponValue.getString(CustomerPortalConstants.PRODUCT_PROMO_ID)+")");
							}
							String productPromoCodeDescription= couponValue.getString("productPromoCodeDescription");
							if(UtilValidate.isNotEmpty(productPromoCodeDescription)) {
								couponData.put(CustomerPortalConstants.DESCRIPTION,productPromoCodeDescription);
							}
							String createdDateStr = couponValue.getString(CustomerPortalConstants.CREATED_DATE);
							if(UtilValidate.isNotEmpty(createdDateStr)) {
								String createdDate= UtilDateTime.timeStampToString(couponValue.getTimestamp(CustomerPortalConstants.CREATED_DATE), globalDateTimeFormat, TimeZone.getDefault(), Locale.getDefault());
								couponData.put(CustomerPortalConstants.CREATED_DATE, createdDate);
							}
							String issueDateStr = couponValue.getString(CustomerPortalConstants.ISSUE_DATE);
							if(UtilValidate.isNotEmpty(issueDateStr)) {
								String issueDate= UtilDateTime.timeStampToString(couponValue.getTimestamp(CustomerPortalConstants.ISSUE_DATE), globalDateTimeFormat, TimeZone.getDefault(), Locale.getDefault());
								couponData.put(CustomerPortalConstants.ISSUE_DATE, issueDate);
							}

							String expiryDateStr =  couponValue.getString(CustomerPortalConstants.THRU_DATE);
							if(UtilValidate.isNotEmpty(expiryDateStr)) {
								String expiryDate= UtilDateTime.timeStampToString(couponValue.getTimestamp(CustomerPortalConstants.THRU_DATE), globalDateTimeFormat, TimeZone.getDefault(), Locale.getDefault());
								couponData.put(CustomerPortalConstants.THRU_DATE, expiryDate);
							}
							String auditDateStr = couponValue.getString(CustomerPortalConstants.REDEMPTION_DATE);
							if(UtilValidate.isNotEmpty(auditDateStr)) {
								String auditDate= UtilDateTime.timeStampToString(couponValue.getTimestamp(CustomerPortalConstants.REDEMPTION_DATE), globalDateTimeFormat, TimeZone.getDefault(), Locale.getDefault());
								couponData.put(CustomerPortalConstants.REDEMPTION_DATE, auditDate);
							}
							String redeemedDateStr = couponValue.getString(CustomerPortalConstants.SCANNED_DATE);
							if(UtilValidate.isNotEmpty(redeemedDateStr)) {
								String redemptionDate= UtilDateTime.timeStampToString(couponValue.getTimestamp(CustomerPortalConstants.SCANNED_DATE), globalDateTimeFormat, TimeZone.getDefault(), Locale.getDefault());
								couponData.put(CustomerPortalConstants.SCANNED_DATE, redemptionDate);
							}

							Timestamp couponThruDate = couponValue.getTimestamp(CustomerPortalConstants.THRU_DATE);
							String couponStatusValue="Available";
							if (UtilValidate.isNotEmpty(couponThruDate)) {
								if (UtilValidate.isNotEmpty(couponThruDate) && couponThruDate.compareTo(UtilDateTime.nowTimestamp())>0) {
									couponStatusValue = "Available";
								}
								if (UtilValidate.isNotEmpty(couponThruDate) && couponThruDate.compareTo(UtilDateTime.nowTimestamp())<=0) {
									couponStatusValue = "Expired";
								}
							}
							
							Timestamp couponScannedDate = couponValue.getTimestamp(CustomerPortalConstants.SCANNED_DATE);
							if (UtilValidate.isNotEmpty(couponScannedDate)) {
								couponStatusValue = "Redeemed";
							}
							
							Timestamp couponRedemptionDate = couponValue.getTimestamp(CustomerPortalConstants.REDEMPTION_DATE);
							if (UtilValidate.isNotEmpty(couponRedemptionDate)) {
								couponStatusValue = "Audited";
							}
							
							couponData.put(CustomerPortalConstants.COUPON_STATUS, couponStatusValue);
							String productPromoCodePurposeTypeId=couponValue.getString(CustomerPortalConstants.PRODUCT_PROMO_CODE_PURPOSE_TYPE_ID);
							if (UtilValidate.isNotEmpty(productPromoCodePurposeTypeId)) {
								couponData.put(CustomerPortalConstants.PRODUCT_PROMO_CODE_PURPOSE_TYPE_ID, productPromoCodePurposeTypeId);
							}

							String orderId =couponValue.getString(CustomerPortalConstants.ORDER_ID);
							if (UtilValidate.isNotEmpty(orderId)) {
								couponData.put(CustomerPortalConstants.ORDER_ID, orderId);
							}
							String redeemedCount =couponValue.getString("redeemedCount");
							if (UtilValidate.isNotEmpty(redeemedCount)){
								couponData.put(CustomerPortalConstants.REDEEM_COUNT,redeemedCount);
							}
							couponData.put("externalId", extenalCouponIdMap.get(groupId));
							
							couponData.put("customerId", UtilValidate.isNotEmpty(PartyHelper.getPartyName(delegator, couponValue.getString("partyId"), false))? PartyHelper.getPartyName(delegator, couponValue.getString("partyId"), false) : "");
							couponData.put("partyId", UtilValidate.isNotEmpty(couponValue.getString("partyId")) ? couponValue.getString("partyId"): "");
							result.add(couponData);
						}catch(Exception e) {
							e.printStackTrace();
							results.put("errorMessage", e.getMessage());
							results.put("responseMessage", "error");
							results.put("data", new ArrayList<Map<String, Object>>());
						}
					}
				}
			}
			results.put("data", result);
			results.put("externalLoginKey", externalLoginKey);
			results.put("responseMessage", "success");
			results.put("successMessage", "Data loaded successfully");
		}catch (Exception e) {
			e.printStackTrace(); 
			results.put("errorMessage", e.getMessage());
			results.put("responseMessage", "error");
			results.put("data", new ArrayList<Map<String, Object>>());
		}
		return doJSONResponse(response, results);
	}

	public static String promotionsList(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException, ParseException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		Map<String, Object> results = new HashMap<String, Object>();
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		String globalDateFormat = org.fio.homeapps.util.DataHelper.getGlobalDateFormat(delegator);
		Map<String, Object> callResult = FastMap.newInstance();
		List<Map<String, Object>> dataList = FastList.newInstance();
		Map<String, Object> callCtxt = FastMap.newInstance();
		Map<String, Object> requestContext = FastMap.newInstance();
		Map<String, Object> context = UtilHttp.getCombinedMap(request);	
		String promoKey = (String) context.get("searchPromoKey");
		String fromDate = (String) context.get(CustomerPortalConstants.FROM_DATE);
		String thruDate = (String) context.get(CustomerPortalConstants.THRU_DATE);
		String type = (String) context.get(CustomerPortalConstants.TYPE);
		String status = (String) context.get(CustomerPortalConstants.STATUS);
		requestContext.put("searchPromoKey", promoKey);
		callCtxt.put("userLogin", userLogin);
		requestContext.put(CustomerPortalConstants.STATUS, status);
		requestContext.put(CustomerPortalConstants.FROM_DATE, fromDate);
		requestContext.put(CustomerPortalConstants.THRU_DATE, thruDate);
		requestContext.put(CustomerPortalConstants.PRODUCT_PROMO_TYPE_ID, type);
		callCtxt.put("requestContext", requestContext);
		try {
			callResult = dispatcher.runSync("loyalty.findPromotionList", callCtxt);
			if (ServiceUtil.isSuccess(callResult) && UtilValidate.isNotEmpty(callResult.get("data"))) {
				@SuppressWarnings("unchecked")
				List<GenericValue> resultList = (List<GenericValue>) callResult.get("data");
				if (resultList != null && resultList.size() > 0) {
					for(GenericValue result : resultList)
					{
						Map<String, Object> data = new HashMap<String, Object>();
						String promoFromDate = result.getString(CustomerPortalConstants.FROM_DATE);
						String productPromoTypeId = result.getString(CustomerPortalConstants.PRODUCT_PROMO_TYPE_ID);
						GenericValue promoType = EntityQuery.use(delegator).select("description").from("ProductPromoType").where(CustomerPortalConstants.PRODUCT_PROMO_TYPE_ID, productPromoTypeId).orderBy("-lastUpdatedTxStamp").queryFirst();
						if (UtilValidate.isNotEmpty(promoType)) {
							String description = promoType.getString("description");
							data.put(CustomerPortalConstants.TYPE, description);
						}
						if (UtilValidate.isNotEmpty(promoFromDate)) {
							data.put(CustomerPortalConstants.FROM_DATE,UtilDateTime.timeStampToString(result.getTimestamp(CustomerPortalConstants.FROM_DATE),globalDateFormat, TimeZone.getDefault(), Locale.getDefault()));
						}
						Timestamp promoThruDate = result.getTimestamp(CustomerPortalConstants.THRU_DATE);
						String promoStatus= "Current";
						if(UtilValidate.isNotEmpty(promoThruDate)) {
							promoStatus="Current";
							if (UtilValidate.isNotEmpty(promoThruDate)) {
								if (UtilValidate.isNotEmpty(promoThruDate) && promoThruDate.compareTo(UtilDateTime.nowTimestamp()) <= 0) {
									promoStatus = "Expired";
								}
							}
						}
						data.put("status",promoStatus);
						if (UtilValidate.isNotEmpty(promoThruDate)) {
							data.put(CustomerPortalConstants.THRU_DATE,UtilDateTime.timeStampToString(result.getTimestamp(CustomerPortalConstants.THRU_DATE),globalDateFormat, TimeZone.getDefault(), Locale.getDefault()));
						}
						data.put(CustomerPortalConstants.PRODUCT_PROMO_ID,result.getString(CustomerPortalConstants.PRODUCT_PROMO_ID));
						String promoName = result.getString(CustomerPortalConstants.PROMO_NAME);
						if (UtilValidate.isNotEmpty(promoName)) {
							promoName = promoName.replaceAll("'", "`").replaceAll("\"", "``");
						}
						data.put(CustomerPortalConstants.PROMO_NAME,promoName);
						data.put(CustomerPortalConstants.REQUIRE_CODE, result.getString(CustomerPortalConstants.REQUIRE_CODE));
						dataList.add(data);
					}
				}
				results.put("responseMessage", "success");
				results.put("data", dataList);
			}
		} catch (GenericServiceException e) {
			e.printStackTrace();
			results.put("errorMessage", e.getMessage());
			results.put("responseMessage", "error");
			results.put("data", new ArrayList<Map<String, Object>>());
		}
		return doJSONResponse(response, results);
	}

	public static String getPromoCampaign(HttpServletRequest request, HttpServletResponse response) {
		String description = request.getParameter("description");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		List<Map<String, String>> dataList = new ArrayList<>();
		Map<String, Object> result = new HashMap<>();
		Locale locale = UtilHttp.getLocale(request);
		NumberFormat nf = NumberFormat.getInstance(locale);
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		List<GenericValue> productPromoCodeGroupList = null;
		long start = System.currentTimeMillis();

		try {
			String globalDateTimeFormat = org.groupfio.common.portal.util.DataHelper
					.getGlobalDateTimeFormat(delegator);

			EntityCondition entityCondition = null;
			if (UtilValidate.isNotEmpty(description))
				entityCondition = EntityCondition.makeCondition(
						EntityCondition.makeCondition("description", EntityOperator.LIKE, "%" + description + "%"),
						EntityOperator.OR, EntityCondition.makeCondition("productPromoCodeGroupId",
								EntityOperator.LIKE, "%" + description + "%"));

			// get the default general grid fetch limit
			GenericValue systemProperty = EntityQuery.use(delegator).select("systemPropertyValue")
					.from("SystemProperty")
					.where("systemResourceId", "general", "systemPropertyId", "fio.grid.fetch.limit").queryFirst();
			// set the page parameters
			int viewIndex = 0;
			try {
				viewIndex = Integer.parseInt((String) context.get("VIEW_INDEX"));
			} catch (Exception e) {
				viewIndex = 0;
			}
			result.put("viewIndex", Integer.valueOf(viewIndex));

			int fioGridFetch = UtilValidate.isNotEmpty(systemProperty)
					&& UtilValidate.isNotEmpty(systemProperty.getString("systemPropertyValue"))
					? Integer.parseInt((String) systemProperty.getString("systemPropertyValue"))
							: 1000;

					int viewSize = fioGridFetch;
					try {
						viewSize = Integer.parseInt((String) context.get("VIEW_SIZE"));
					} catch (Exception e) {
						viewSize = fioGridFetch;
					}
					result.put("viewSize", Integer.valueOf(viewSize));

					int highIndex = 0;
					int lowIndex = 0;
					int resultListSize = 0;
					try {
						// get the indexes for the partial list
						lowIndex = viewIndex * viewSize + 1;
						highIndex = (viewIndex + 1) * viewSize;

						// set distinct on so we only get one row per order
						// using list iterator
						EntityListIterator pli = EntityQuery.use(delegator).from("ProductPromoCodeGroup")
								.where(entityCondition).orderBy(CustomerPortalConstants.FROM_DATE + " DESC")
								.cursorScrollInsensitive().fetchSize(highIndex).distinct().cache(true).queryIterator();
						// get the partial list for this page
						productPromoCodeGroupList = pli.getPartialList(lowIndex, viewSize);

						// attempt to get the full size
						resultListSize = pli.getResultsSizeAfterPartialList();
						// close the list iterator
						pli.close();
					} catch (GenericEntityException e) {
						String errMsg = "Error: " + e.toString();
						Debug.logError(e, errMsg, MODULE);
					}

					if (UtilValidate.isNotEmpty(productPromoCodeGroupList)) {
						for (GenericValue productPromoCodeGroup : productPromoCodeGroupList) {
							Map<String, String> data = DataUtil.convertGenericToMap(productPromoCodeGroup);

							if (UtilValidate.isNotEmpty(productPromoCodeGroup.getString(CustomerPortalConstants.FROM_DATE))) {
								data.put(CustomerPortalConstants.FROM_DATE,
										org.fio.homeapps.util.UtilDateTime.timeStampToString(
												productPromoCodeGroup.getTimestamp(CustomerPortalConstants.FROM_DATE),
												globalDateTimeFormat, TimeZone.getDefault(), null));
							}
							if (UtilValidate.isNotEmpty(productPromoCodeGroup.getString(CustomerPortalConstants.THRU_DATE))) {
								data.put(CustomerPortalConstants.THRU_DATE,
										org.fio.homeapps.util.UtilDateTime.timeStampToString(
												productPromoCodeGroup.getTimestamp(CustomerPortalConstants.THRU_DATE),
												globalDateTimeFormat, TimeZone.getDefault(), null));
							}
							dataList.add(data);
						}
						result.put("highIndex", Integer.valueOf(highIndex));
						result.put("lowIndex", Integer.valueOf(lowIndex));
						result.put("responseMessage", "success");
						result.put("list", dataList);
					}
					result.put("chunks", (int) Math.ceil((double) resultListSize / (double) viewSize));
					result.put("totalRecords", nf.format(resultListSize));
					result.put("recordCount", resultListSize);
					result.put("chunkSize", viewSize);
		} catch (GenericEntityException e) {
			Debug.logError(e.getMessage(), MODULE);
			result.put("errorMessage", e.getMessage());
			result.put("responseMessage", "error");
			result.put("list", new ArrayList<Map<String, Object>>());
			return doJSONResponse(response, result);
		}
		long end = System.currentTimeMillis();
		Debug.logInfo("timeElapsed--->" + (end - start) / 1000f, MODULE);
		result.put("timeTaken", (end - start) / 1000f);
		result.put("list", dataList);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return doJSONResponse(response, result);
	}
	/**
	 * @author Nirmal Kumar P
	 * @since 14-01-2023
	 * @param request
	 * @param response
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String searchOrders(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		String partyId = (String) request.getParameter("partyId");
		String isOrderCompleted = request.getParameter("isOrderCompleted");
		String isInvoiceApproved = request.getParameter("isInvoiceApproved");
		String orderByColumn = request.getParameter("orderByColumn");
		String entityName = request.getParameter("entityName");
		boolean isRecentTransaction = UtilValidate.isNotEmpty(request.getParameter("isRecentTransaction"))&&"Y".equals(request.getParameter("isRecentTransaction")) ? true : false;
		String externalLoginKey = request.getParameter("externalLoginKey");
		
		String limitRows = UtilValidate.isNotEmpty(request.getParameter("limitRows"))? request.getParameter("limitRows") : "Y";
		String maxRows = UtilValidate.isNotEmpty(request.getParameter("maxRows")) ? request.getParameter("maxRows") : "5";

		Map<String, Object> result = FastMap.newInstance();
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		try {
			if (UtilValidate.isNotEmpty(partyId)) {
				String globalDateFormat = org.fio.homeapps.util.DataHelper.getGlobalDateFormat(delegator);
				String globalDateTimeFormat = org.fio.homeapps.util.DataHelper.getGlobalDateTimeFormat(delegator);
				if(UtilValidate.isEmpty(entityName)) {
					entityName = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "CUSTOMER_RTM_ITM");
				}
				/*
				if(isRecentTransaction)
					entityName = "InvoiceTransactionMaster";
				*/
				boolean isRTM = UtilValidate.isNotEmpty(entityName) && "RmsTransactionMaster".equals(entityName) ? true : false;
				boolean isITM = UtilValidate.isNotEmpty(entityName) && "InvoiceTransactionMaster".equals(entityName) ? true : false;
				Set<String> fieldsToSelect = new TreeSet<String>();
				if (isRTM) {
					fieldsToSelect.add("orderId");
					fieldsToSelect.add("orderDate");
					fieldsToSelect.add("shipDate");
					fieldsToSelect.add("orderStatus");
					//fieldsToSelect.add("unitRetail");
					fieldsToSelect.add("itemNetSales");
					//fieldsToSelect.add("unitCost");
				} else {
					fieldsToSelect.add("invoiceId");
					fieldsToSelect.add("invoiceDate");
					fieldsToSelect.add("shippedDate");
					fieldsToSelect.add("invoiceStatus");
					fieldsToSelect.add("unitRetail");
					//fieldsToSelect.add("unitCost");
				}
				fieldsToSelect.add("unitCost");
				fieldsToSelect.add("skuNumber");
				fieldsToSelect.add("smallImageUrl");
				fieldsToSelect.add("skuDescription");
				fieldsToSelect.add("totalSalesAmount");
				fieldsToSelect.add("quantitySold");
				fieldsToSelect.add("storeNumber");
				fieldsToSelect.add("transactionType");
				
				fieldsToSelect.add("totalTaxAmount");
				fieldsToSelect.add("discountAmount");
				fieldsToSelect.add("numberOfReturns");
				fieldsToSelect.add("transactionNumber");
				
				DynamicViewEntity dynamicViewEntity = new DynamicViewEntity();
				dynamicViewEntity.addMemberEntity("TM", entityName);
				dynamicViewEntity.addAlias("TM", "billToPartyId");
				if (isRTM) {
					dynamicViewEntity.addAlias("TM", "orderId", "orderId", null, null, true, null);
					dynamicViewEntity.addAlias("TM", "orderDate");
					dynamicViewEntity.addAlias("TM", "shipDate");
					dynamicViewEntity.addAlias("TM", "orderStatus");
					//dynamicViewEntity.addAlias("TM", "unitRetail");
					//dynamicViewEntity.addAlias("TM", "unitCost");
					dynamicViewEntity.addAlias("TM", "itemNetSales");
				} else {
					dynamicViewEntity.addAlias("TM", "invoiceId", "invoiceId", null, null, true, null);
					dynamicViewEntity.addAlias("TM", "invoiceDate");
					dynamicViewEntity.addAlias("TM", "shippedDate");
					dynamicViewEntity.addAlias("TM", "invoiceStatus");
					dynamicViewEntity.addAlias("TM", "unitRetail");
					//dynamicViewEntity.addAlias("TM", "unitCost");
				}
				if(isRecentTransaction)
					dynamicViewEntity.addAlias("TM", "skuNumber");
				else
					dynamicViewEntity.addAlias("TM", "skuNumber","skuNumber", null, null, true, null);
				dynamicViewEntity.addAlias("TM", "skuDescription");
				dynamicViewEntity.addAlias("TM", "totalSalesAmount");
				if(isRecentTransaction)
					dynamicViewEntity.addAlias("TM", "quantitySold", "quantitySold", null, null, null, "sum");
				else 
					dynamicViewEntity.addAlias("TM", "quantitySold");
				
				dynamicViewEntity.addAlias("TM", "unitCost");
				dynamicViewEntity.addAlias("TM", "storeNumber");
				dynamicViewEntity.addAlias("TM", "transactionType");
				dynamicViewEntity.addAlias("TM", "totalTaxAmount");
				dynamicViewEntity.addAlias("TM", "discountAmount");
				dynamicViewEntity.addAlias("TM", "numberOfReturns");
				dynamicViewEntity.addAlias("TM", "transactionNumber");
				
				dynamicViewEntity.addMemberEntity("PDT", "Product");
				dynamicViewEntity.addAlias("PDT", "productId");
				dynamicViewEntity.addAlias("PDT", "smallImageUrl");
				dynamicViewEntity.addViewLink("TM", "PDT", Boolean.TRUE,
						ModelKeyMap.makeKeyMapList("skuNumber", "productId"));

				Map<String, Object> storeNames = SrDataHelper.getProductStoreNames(delegator);
				List conditionList = FastList.newInstance();
				if (UtilValidate.isNotEmpty(partyId)) {
					conditionList.add(EntityCondition.makeCondition("billToPartyId", EntityOperator.EQUALS, partyId));
				}
				if (isRTM && UtilValidate.isNotEmpty(isOrderCompleted)) {
					if ("Y".equals(isOrderCompleted ) ) {
						conditionList.add(EntityCondition.makeCondition("orderStatus", EntityOperator.EQUALS, "ORDER_COMPLETED"));
					}else if("N".equals(isOrderCompleted)){
						conditionList.add(EntityCondition.makeCondition(EntityOperator.OR,
								EntityCondition.makeCondition("orderStatus", EntityOperator.EQUALS, "ORDER_CREATED"),
								EntityCondition.makeCondition("orderStatus", EntityOperator.EQUALS, "ORDER_APPROVED")
								));
					}
				}
				EntityCondition mainConditions = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				String orderBy = null;
				if (UtilValidate.isNotEmpty(orderByColumn)) {
					orderBy = orderByColumn;
				}
				EntityQuery query = EntityQuery.use(delegator).select(fieldsToSelect).from(dynamicViewEntity)
						.where(mainConditions);
				if (UtilValidate.isNotEmpty(orderBy)) {
					query.orderBy(orderBy);
				}
				if (UtilValidate.isNotEmpty(limitRows) && limitRows.equalsIgnoreCase("Y")) {
					query.maxRows(Integer.parseInt(maxRows));
				}
				List<GenericValue> orderList = query.queryList();
				int seqNo = 1;
				for (GenericValue orderGv : orderList) {
					Map<String, Object> data = new HashMap<String, Object>();
					data.put("partyId", partyId);
					if (isRTM) {
						data.put("orderId", orderGv.getString("orderId"));
						data.put("orderDate",
								UtilValidate.isNotEmpty(orderGv.get("orderDate")) ? UtilDateTime.timeStampToString(
										orderGv.getTimestamp("orderDate"), globalDateFormat, TimeZone.getDefault(), null)
										: "");
						data.put("shipDate",
								UtilValidate.isNotEmpty(orderGv.get("shipDate"))
								? UtilDateTime.timeStampToString(orderGv.getTimestamp("shipDate"), globalDateFormat,
										TimeZone.getDefault(), null)
										: "");
					} else {
						data.put("invoiceId", orderGv.getString("invoiceId"));
						data.put("invoiceDate",
								UtilValidate.isNotEmpty(orderGv.get("invoiceDate"))
								? UtilDateTime.timeStampToString(orderGv.getTimestamp("invoiceDate"),
										globalDateFormat, TimeZone.getDefault(), null)
										: "");
						data.put("shippedDate",
								UtilValidate.isNotEmpty(orderGv.get("shippedDate"))
								? UtilDateTime.timeStampToString(orderGv.getTimestamp("shippedDate"),
										globalDateFormat, TimeZone.getDefault(), null)
										: "");
					}
					data.put("productImage", orderGv.getString("smallImageUrl"));
					data.put("productName", orderGv.getString("skuDescription"));

					if (isRecentTransaction && isRTM) {
						data.put("orderDateTime", UtilValidate.isNotEmpty(orderGv.get("orderDate"))
								? UtilDateTime.timeStampToString(orderGv.getTimestamp("orderDate"), globalDateTimeFormat,
										TimeZone.getDefault(), null)
										: "");
					}
					if (isRecentTransaction && isITM) {
						data.put("invoiceDateTime",
								UtilValidate.isNotEmpty(orderGv.get("invoiceDate"))
								? UtilDateTime.timeStampToString(orderGv.getTimestamp("invoiceDate"),
										globalDateTimeFormat, TimeZone.getDefault(), null)
										: "");
					}
					
					String priceValue = "0.00";
					BigDecimal price = null;
			        DecimalFormat decimalFormat = new DecimalFormat("0.00");
					if(isRTM) {
						BigDecimal itemNetSales = orderGv.getBigDecimal("itemNetSales");
						if (UtilValidate.isNotEmpty(itemNetSales))
							price = itemNetSales.setScale(2, BigDecimal.ROUND_HALF_EVEN);
							priceValue = decimalFormat.format(price);
					}else {
						BigDecimal unitRetail = orderGv.getBigDecimal("unitRetail");
						if (UtilValidate.isNotEmpty(unitRetail))
							price = unitRetail.setScale(2, BigDecimal.ROUND_HALF_EVEN);
							priceValue = decimalFormat.format(price);
					}
					data.put("price", priceValue);

					BigDecimal actualQty = orderGv.getBigDecimal("quantitySold");
					if (UtilValidate.isEmpty(actualQty))
						actualQty = BigDecimal.ZERO;
					data.put("totalSalesAmount", orderGv.getString("totalSalesAmount"));
					data.put("quantity", actualQty.doubleValue());
					data.put("storeNumber", orderGv.getString("storeNumber"));
					data.put("storeName", storeNames.get(orderGv.getString("storeNumber")));
					data.put("transactionType", orderGv.getString("transactionType"));
					data.put("externalLoginKey", externalLoginKey);
					data.put("seqNo", seqNo+"");
					dataList.add(data);
					seqNo = seqNo+1;
				}
			}
			result.put("data", dataList);
		} catch (Exception e) {
			Debug.logError(e.getMessage(), MODULE);
		}
		return doJSONResponse(response, result);
	}
	public static String addEarnedValue(HttpServletRequest request, HttpServletResponse response)
			throws ParseException {
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute(CustomerPortalConstants.USER_LOGIN);
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute(CustomerPortalConstants.DISPATCHER);
		Map<String, Object> requestContext = new LinkedHashMap<>();
		Map<String, Object> callCtxt = FastMap.newInstance();

		String partyId=request.getParameter("partyId");

		requestContext.putAll(context);
		callCtxt.put(CustomerPortalConstants.USER_LOGIN, userLogin);
		callCtxt.put("requestContext", requestContext);
		try {
			Map<String, Object> addEarnedValue = dispatcher.runSync("loyalty.addEarnedValue",
					callCtxt);
			if (ServiceUtil.isError(addEarnedValue) || ServiceUtil.isFailure(addEarnedValue)) {
				request.setAttribute(CustomerPortalConstants.ERROR_MESSAGE,
						ServiceUtil.getErrorMessage(addEarnedValue));
				return CustomerPortalConstants.ERROR;
			}
			request.setAttribute(CustomerPortalConstants.EVENT_MESSAGE, "Earned Value Created Successfully");
			request.setAttribute(CustomerPortalConstants.PARTY_ID, partyId);
		} catch (GenericServiceException e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
			request.setAttribute(CustomerPortalConstants.ERROR_MESSAGE, e.getMessage());
			return CustomerPortalConstants.ERROR;
		}
		return CustomerPortalConstants.SUCCESS;
	}

	public static String searchPromoCampaignCoupons(HttpServletRequest request, HttpServletResponse response) {

		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Locale locale = UtilHttp.getLocale(request);
		NumberFormat nf = NumberFormat.getInstance(locale);

		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String requestUri = (String) context.get("requestUri");
		String productPromoCodeGroupId = (String) context.get("productPromoCodeGroupId");
		String productPromoId = (String) context.get("productPromoId");
		String jobId = (String) context.get("jobId");
		String externalLoginKey = (String) session.getAttribute("externalLoginKey");
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		Map<String, Object> result = new HashMap<String, Object>();
		long start1 = System.currentTimeMillis();
		Debug.logInfo("try start: "+UtilDateTime.nowTimestamp(), MODULE);
		List<Map<String, Object>> resultList = new ArrayList<Map<String,Object>>();
		try {
			String globalDateFormat = org.fio.homeapps.util.DataHelper.getGlobalDateFormat(delegator);
			String globalDateTimeFormat = DataUtil.getGlobalMysqlDateTimeFormat(delegator);
			int highIndex = 0;
			int lowIndex = 0;
			int resultListSize = 0;
			int viewSize = 0;
			List<EntityCondition> conditionList = FastList.newInstance();

			if(UtilValidate.isEmpty(productPromoId) && UtilValidate.isEmpty(jobId)) {
				result.put("list", new ArrayList<Map<String, Object>>());
				result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
				result.put(ModelService.ERROR_MESSAGE, "Required parameters missing!");
				return doJSONResponse(response, result);
			}

			GenericValue productPromo = delegator.findOne("ProductPromo", UtilMisc.toMap("productPromoId", productPromoId), false);
			if(UtilValidate.isEmpty(productPromo)) {
				result.put("list", new ArrayList<Map<String, Object>>());
				result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
				result.put(ModelService.ERROR_MESSAGE, "Promotion not found!");
				return doJSONResponse(response, result);
			}
			if(UtilValidate.isNotEmpty(productPromo) && UtilValidate.isNotEmpty(productPromo.getString("productPromoTypeId"))) {
				String productPromoTypeId = productPromo.getString("productPromoTypeId");
				GenericValue productPromoType = EntityQuery.use(delegator).from("ProductPromoType").where("productPromoTypeId", productPromoTypeId).cache(true).queryFirst();
				String promoTypeDesc = UtilValidate.isNotEmpty(productPromoType) ? productPromoType.getString("description") : "";
				String promoName = productPromo.getString("promoName");
				List<GenericValue> childThreadInfo = null;
				conditionList.add(EntityCondition.makeCondition("jobId", EntityOperator.EQUALS, jobId));
				conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "FINISHED"));

				EntityCondition mainConditons1 = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				if("LOYALTY".equals(productPromoTypeId)) {
					//dynamicView.addMemberEntity("CJS", "LoyaltyCouponCreateJobSandbox");
					//dynamicView.addMemberEntity("CJCT", "LoyaltyCouponCreateChildThread");
					childThreadInfo = EntityQuery.use(delegator).from("LoyaltyCouponCreateChildThread").where(mainConditons1).queryList();
				}else  {
					//dynamicView.addMemberEntity("CJS", "CouponCreateJobSandbox");
					//dynamicView.addMemberEntity("CJCT", "CouponCreateChildThread");
					childThreadInfo = EntityQuery.use(delegator).from("CouponCreateChildThread").where(mainConditons1).queryList();
				}
				List<String> childThreadList = UtilValidate.isNotEmpty(childThreadInfo) ? EntityUtil.getFieldListFromEntityList(childThreadInfo, "childThreadId", true) : new ArrayList<String>();
				if(UtilValidate.isNotEmpty(childThreadList)) {
					String _where_condition_ = "";
					String _sql_query_ = "SELECT PPC.PRODUCT_PROMO_ID, PP.PROMO_NAME, PPC.PRODUCT_PROMO_CODE_ID, IFNULL(DATE_FORMAT(PPC.FROM_DATE,'"+globalDateTimeFormat+"'),'') AS 'FROM_DATE', IFNULL(DATE_FORMAT(PPC.THRU_DATE,'"+globalDateTimeFormat+"'),'') AS 'THRU_DATE',"
							+ " IFNULL(DATE_FORMAT(PPC.CREATED_DATE,'"+globalDateTimeFormat+"'),'') AS 'CREATED_DATE', PPC.DESCRIPTION AS 'PRODUCT_PROMO_CODE_DESC', PPC.PRODUCT_PROMO_CODE_TYPE_ID, IFNULL(DATE_FORMAT(PPC.ISSUE_DATE,'"+globalDateTimeFormat+"'),'') AS 'ISSUE_DATE', "
							+ " PPC.REDEEMED_COUNT, IFNULL(DATE_FORMAT(PPC.SCANNED_DATE,'"+globalDateTimeFormat+"'),'') AS 'SCANNED_DATE', "
							+ " IFNULL(DATE_FORMAT(PPC.REDEMPTION_DATE,'"+globalDateTimeFormat+"'),'') AS 'REDEMPTION_DATE', "
							+ " PPC.PRODUCT_PROMO_CODE_PURPOSE_TYPE_ID, PPC.CHILD_THREAD_ID, PPC.PRODUCT_PROMO_CODE_GROUP_ID, PPCP.PARTY_ID, "
							+ " PPU.ORDER_ID, PPT.DESCRIPTION AS 'PRODUCT_PROMO_TYPE_DESCRIPTION', "
							+ " PPCT.DESCRIPTION AS 'PRODUCT_PROMO_CODE_TYPE_DESCRIPTION', "
							+ " (CASE WHEN PPC.SCANNED_DATE IS NOT NULL THEN 'Redeemed' "
							+ " WHEN PPC.REDEMPTION_DATE IS NOT NULL THEN 'Audited' "
							+ " WHEN PPC.THRU_DATE IS NOT NULL AND PPC.THRU_DATE <= NOW() THEN 'Expired' "
							+ " ELSE 'Available' END) AS 'COUPON_STATUS', "
							+ " CONCAT(PER.FIRST_NAME,IFNULL(PER.LAST_NAME,'')) AS 'PARTY_NAME', PPCP.PARTY_ID,"
							+ " PPCPT.DESCRIPTION AS 'PRODUCT_PROMO_CODE_PURPOSE_TYPE_DESCRIPTION',"
							+ " '" + externalLoginKey + "' AS externalLoginKey "
							+ " FROM PRODUCT_PROMO_CODE PPC "
							+ " LEFT OUTER JOIN product_promo PP ON PPC.PRODUCT_PROMO_ID = PP.PRODUCT_PROMO_ID "
							+ " LEFT OUTER JOIN product_promo_type PPT ON PP.PRODUCT_PROMO_TYPE_ID = PPT.PRODUCT_PROMO_TYPE_ID "
							+ " LEFT OUTER JOIN product_promo_code_purpose_type PPCPT ON PPC.PRODUCT_PROMO_CODE_PURPOSE_TYPE_ID = PPCPT.PRODUCT_PROMO_CODE_PURPOSE_TYPE_ID"
							+ " LEFT OUTER JOIN product_promo_code_type PPCT ON PPC.PRODUCT_PROMO_CODE_TYPE_ID = PPCT.PRODUCT_PROMO_CODE_TYPE_ID "
							+ " LEFT OUTER JOIN PRODUCT_PROMO_CODE_PARTY PPCP ON PPC.PRODUCT_PROMO_CODE_ID = PPCP.PRODUCT_PROMO_CODE_ID "
							+ " LEFT OUTER JOIN PERSON PER ON PPCP.PARTY_ID = PER.PARTY_ID "
							+ " LEFT OUTER JOIN PRODUCT_PROMO_USE PPU ON PPC.PRODUCT_PROMO_CODE_ID = PPU.PRODUCT_PROMO_CODE_ID";
					String _count_sql_query_ ="SELECT count(PPC.PRODUCT_PROMO_CODE_ID) AS 'totalRecord'" + 
							"FROM PRODUCT_PROMO_CODE PPC";

					_where_condition_ = _where_condition_ + (UtilValidate.isNotEmpty(_where_condition_) ? " AND " : "") + " PPC.CHILD_THREAD_ID IN ("+DataUtil.toList(childThreadList, "")+")";


					GenericValue systemProperty = EntityQuery.use(delegator)
							.select("systemPropertyValue")
							.from("SystemProperty")
							.where("systemResourceId","general","systemPropertyId","fio.grid.fetch.limit")
							.queryFirst();

					// set the page parameters
					int viewIndex = 0;
					try {
						viewIndex = Integer.parseInt((String) context.get("VIEW_INDEX"));
					} catch (Exception e) {
						viewIndex = 0;
					}
					result.put("viewIndex", Integer.valueOf(viewIndex));

					int fioGridFetch = UtilValidate.isNotEmpty(systemProperty) && UtilValidate.isNotEmpty(systemProperty.getString("systemPropertyValue")) ?  Integer.parseInt((String) systemProperty.getString("systemPropertyValue")) : 1000;

					viewSize = fioGridFetch;
					try {
						viewSize = Integer.parseInt((String) context.get("VIEW_SIZE"));
					} catch (Exception e) {
						viewSize = fioGridFetch;
					}
					result.put("viewSize", Integer.valueOf(viewSize));

					Debug.logInfo("mainConditons: "+_where_condition_, MODULE);

					// get the indexes for the partial list
					lowIndex = viewIndex * viewSize;

					highIndex = (viewIndex + 1) * viewSize;
					Debug.logInfo("query start: "+UtilDateTime.nowTimestamp(), MODULE);
					// set distinct on so we only get one row per 
					SQLProcessor sqlProcessor = new SQLProcessor(delegator, delegator.getGroupHelperInfo("org.ofbiz"));

					ResultSet rs = null;
					_count_sql_query_ = _count_sql_query_ + (UtilValidate.isNotEmpty(_where_condition_) ?  " WHERE "+_where_condition_ : "");
					// get the total count 
					rs = sqlProcessor.executeQuery(_count_sql_query_);
					if (rs != null) {
						while (rs.next()) {
							resultListSize = (int) rs.getLong("totalRecord");
						}
					}
					try {
						rs.close();
					} catch (Exception e) {
					}
					String _final_sql_script = _sql_query_+ (UtilValidate.isNotEmpty(_where_condition_) ? " WHERE "+_where_condition_ :"") + " LIMIT "+lowIndex+", "+viewSize;

					Debug.log("_count_sql_query_ ---->"+_count_sql_query_, MODULE);

					Debug.log("_final_sql_script ---->"+_final_sql_script, MODULE);
					rs = sqlProcessor.executeQuery(_final_sql_script);

					if (rs != null) {
						ResultSetMetaData rsMetaData = rs.getMetaData();
						List<String> columnList = new ArrayList<String>();
						//Retrieving the list of column names
						int count = rsMetaData.getColumnCount();

						long start2 = System.currentTimeMillis();
						while (rs.next()) {
							Map<String, Object> data = new HashMap<String, Object>();
							for(int i = 1; i<=count; i++) {
								data.put(ModelUtil.dbNameToVarName(rsMetaData.getColumnLabel(i)), rs.getString(i));
								data.put("customerId", org.groupfio.common.portal.util.DataUtil.combineValueKey(rs.getString(21), rs.getString(22)));
							}
							resultList.add(data);
						}
						long end2 = System.currentTimeMillis();
						Debug.logInfo("timeElapsed for construction --->"+(end2-start2) / 1000f, MODULE);
						result.put("highIndex", Integer.valueOf(highIndex));
						result.put("lowIndex", Integer.valueOf(lowIndex));
					}
					try {
						rs.close();
					} catch (Exception e) {
					}
					Debug.logInfo("query end: "+UtilDateTime.nowTimestamp(), MODULE);
				}
			}
			result.put("chunks", (int) Math.ceil((double) resultListSize / (double) viewSize));
			result.put("totalRecords", nf.format(resultListSize));
			result.put("recordCount", resultListSize);
			result.put("chunkSize", viewSize);   
			Debug.logInfo("data ready: "+UtilDateTime.nowTimestamp(), MODULE);
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
			result.put("list", new ArrayList<Map<String, Object>>());
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
			return doJSONResponse(response, result);
		}

		long end1 = System.currentTimeMillis();
		Debug.logInfo("timeElapsed--->"+(end1-start1) / 1000f, MODULE);
		result.put("timeTaken", (end1-start1) / 1000f);
		result.put("list", resultList);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return doJSONResponse(response, result);

	}
	public static String updateEreceiptEnabled(HttpServletRequest request, HttpServletResponse response) {
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute(CustomerPortalConstants.USER_LOGIN);
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		Map<String, Object> callCtxt = FastMap.newInstance();
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map<String, Object> result = new HashMap<String, Object>();
		String partyId=(String)context.get("partyId");
		String isEreceiptEnabled=(String)context.get("isEreceiptEnabled");
		String externalLoginKey=(String)context.get("externalLoginKey");
		try {
			GenericValue partyAttribute = EntityQuery.use(delegator).from("PartyAttribute").where("partyId", partyId, "attrName", "IS_ERCPT_ENABLED").queryFirst();
			if(UtilValidate.isNotEmpty(partyAttribute)) {
				partyAttribute.set("attrValue", isEreceiptEnabled);
				partyAttribute.store();
			} else {
				partyAttribute = delegator.makeValue("PartyAttribute");
				partyAttribute.setPKFields(context);
				partyAttribute.setNonPKFields(context);
				partyAttribute.create();
			}
			result.put("responseMessage", "success");
		} catch (Exception e) {
			Debug.logWarning(e, MODULE);
			result.put("_ERROR_MESSAGE_", e.getMessage());
			return doJSONResponse(response, result);
		}
		return doJSONResponse(response, result);
	}
	// Updating PersonResponsible
	public static String updatePersonResponsible(HttpServletRequest request, HttpServletResponse response) {
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute(CustomerPortalConstants.USER_LOGIN);
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String partyId = (String) context.get("partyId");
		String newPartyId = (String) context.get("accountPartyId");
		String marketingCampaignId = (String) context.get("marketingCampaignId");
		String contactListId = (String) context.get("contactListId");
		java.sql.Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		try {
			String roleTypeIdFrom = "CUSTOMER";
			String roleTypeIdTo = "";
			String securityGroupId ="";
			EntityConditionList<EntityCondition> roleCondition = EntityCondition.makeCondition(UtilMisc.toList(
					EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, newPartyId),
					EntityCondition.makeCondition(UtilMisc.toList(
							EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "CUST_SERVICE_REP"),
							EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "SALES_REP"),
							EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "ACCOUNT_MANAGER")),
							EntityOperator.OR)),
					EntityOperator.AND);
			List<GenericValue> partyRoleCondition = delegator.findList("PartyRole", roleCondition, null, null, null, false);
			if(partyRoleCondition.size()>1){
				roleTypeIdTo = "CUST_SERVICE_REP";
				securityGroupId = "CONTACT_OWNER";	
			}else if(UtilValidate.isNotEmpty(partyRoleCondition) && partyRoleCondition.size()>0){
				Debug.log("partyRoleCondition"+partyRoleCondition);
				for(GenericValue partyRolee : partyRoleCondition){
					roleTypeIdTo = partyRolee.getString("roleTypeId");
				}
				if("ACCOUNT_MANAGER".equals(roleTypeIdTo)){
					roleTypeIdTo = "ACCOUNT_MANAGER";
					securityGroupId = "ACCOUNT_OWNER";
				}
				if("CUST_SERVICE_REP".equals(roleTypeIdTo) || "SALES_REP".equals(roleTypeIdTo)){
					roleTypeIdTo = "CUST_SERVICE_REP";
					securityGroupId = "CONTACT_OWNER";	
				}	
			}else{
				String errMsg = "Invalid Party Id. Cannot reassign";
				request.setAttribute(CustomerPortalConstants.ERROR_MESSAGE, errMsg);
				return "error";
			}
			EntityCondition condition = EntityCondition.makeCondition(UtilMisc.toList(
					EntityCondition.makeCondition("partyIdFrom",partyId),
					EntityCondition.makeCondition("roleTypeIdFrom","CUSTOMER")),
					EntityOperator.AND);
			List<GenericValue> partyRelationships =delegator.findList("PartyRelationship", condition, null, null, null, false);
			if(UtilValidate.isNotEmpty(partyRelationships)){
				partyRelationships= EntityUtil.filterByDate(partyRelationships);
				if(UtilValidate.isNotEmpty(partyRelationships))
				{
					GenericValue partyRelCondition = EntityUtil.getFirst(partyRelationships);
					if(UtilValidate.isNotEmpty(partyRelCondition)){
						partyRelCondition.set("thruDate",nowTimestamp);
						partyRelCondition.store();
					} 
				}
			}
			try {
			GenericValue partyRelationshipcreate = delegator.makeValue("PartyRelationship");
			partyRelationshipcreate.set("partyIdFrom",partyId);
			partyRelationshipcreate.set("partyIdTo",newPartyId);
			partyRelationshipcreate.set("roleTypeIdFrom",roleTypeIdFrom);
			partyRelationshipcreate.set("roleTypeIdTo",roleTypeIdTo);
			partyRelationshipcreate.set("securityGroupId",securityGroupId);
			partyRelationshipcreate.set("fromDate",nowTimestamp);
			partyRelationshipcreate.set("partyRelationshipTypeId","RESPONSIBLE_FOR");
			partyRelationshipcreate.create();
			}catch(Exception e) {
					String errMsg = "Invalid Party Id. Cannot reassign Please Check Party Role";
					request.setAttribute(CustomerPortalConstants.ERROR_MESSAGE, errMsg);
					return "error";
			}
			String outBoundCall = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "OUTBOUND_CALL","Y");
			if(UtilValidate.isNotEmpty(outBoundCall)) {
				List < EntityCondition > callRecordMasterconditionsList = new ArrayList < EntityCondition > ();
				callRecordMasterconditionsList.add(EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId)));
				callRecordMasterconditionsList.add(EntityCondition.makeCondition(EntityOperator.OR,
						EntityCondition.makeCondition("callFinished", EntityOperator.EQUALS, null),
						EntityCondition.makeCondition("callFinished", EntityOperator.EQUALS, ""),
						EntityCondition.makeCondition("callFinished", EntityOperator.EQUALS, "N")));
				String accountManager= "N";
				EntityCondition callRecordMastercondition = EntityCondition.makeCondition(callRecordMasterconditionsList, EntityOperator.AND);
				List < GenericValue > callRecordMasterList = delegator.findList("CallRecordMaster", callRecordMastercondition, null, null, null, false);
				if(callRecordMasterList != null && callRecordMasterList.size() > 0) {
					if(UtilValidate.isNotEmpty(roleTypeIdTo)) {
						accountManager = "Y";
					}
					for(GenericValue callRecordMaster : callRecordMasterList) {
						callRecordMaster.set("accountManager", accountManager);
						callRecordMaster.store();
					}
				}
			}else {
				request.setAttribute(CustomerPortalConstants.ERROR_MESSAGE, "Out Bound Call Parameter value missing ");
				request.setAttribute(CustomerPortalConstants.PARTY_ID, partyId);
				if(UtilValidate.isNotEmpty(marketingCampaignId)) {
					request.setAttribute("marketingCampaignId", marketingCampaignId);			
				}
				if(UtilValidate.isNotEmpty(contactListId)) {
					request.setAttribute("contactListId", contactListId);			
				}
				return "error";
			}
		} catch (GenericEntityException e) {
			request.setAttribute(CustomerPortalConstants.ERROR_MESSAGE, e);
		}
		request.setAttribute(CustomerPortalConstants.EVENT_MESSAGE, "Responsible Person Changed Successfuly");
		request.setAttribute(CustomerPortalConstants.PARTY_ID, partyId);
		if(UtilValidate.isNotEmpty(marketingCampaignId)) {
			request.setAttribute("marketingCampaignId", marketingCampaignId);			
		}
		if(UtilValidate.isNotEmpty(contactListId)) {
			request.setAttribute("contactListId", contactListId);			
		}
		return "success";
	}
	public static String removePersonResponsible(HttpServletRequest request, HttpServletResponse response) {
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute(CustomerPortalConstants.USER_LOGIN);
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map<String, Object> result = new HashMap<String, Object>();
		String partyId = (String) context.get("responsiblePerson");
		String contactListId = (String) context.get("contactListId");
		String marketingCampaignId = (String) context.get("marketingCampaignId");
		String externalLoginKey=(String)context.get("externalLoginKey");
		String responsiblePartyId = "";
		try{
			EntityCondition condition = EntityCondition.makeCondition(UtilMisc.toList(
					EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, partyId),
					EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.IN, UtilMisc.toList("ACCOUNT_MANAGER", "CUST_SERVICE_REP","SALES_REP","EMPLOYEE")),
					EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "CUSTOMER"),
					EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "RESPONSIBLE_FOR"),
					EntityCondition.makeConditionDate("fromDate", "thruDate")),
					EntityOperator.AND);
			List<GenericValue> responsibleForList = delegator.findList("PartyRelationship", condition, null, null, null, false) ;
			if(UtilValidate.isNotEmpty(responsibleForList)){
				Timestamp now = UtilDateTime.nowTimestamp();
				for(GenericValue responsibleFor : responsibleForList) {
					Debug.logInfo("removing the responsible relation "+responsibleFor, MODULE);
					responsiblePartyId = responsibleFor.getString("partyIdTo");
					responsibleFor.set("thruDate", now);
					responsibleFor.store();
				}
			}else{
				request.setAttribute(CustomerPortalConstants.ERROR_MESSAGE, "No responsible person Found");
				request.setAttribute(CustomerPortalConstants.PARTY_ID, partyId);
				if(UtilValidate.isNotEmpty(marketingCampaignId)) {
					request.setAttribute("marketingCampaignId", marketingCampaignId);			
				}
				if(UtilValidate.isNotEmpty(contactListId)) {
					request.setAttribute("contactListId", contactListId);			
				}
				return "error";
			}
		}catch(Exception e){
			e.printStackTrace();
			result.put(CustomerPortalConstants.ERROR_MESSAGE, e.getMessage());
			return "error";
		}
		result.put("responseMessage", "success");
		request.setAttribute(CustomerPortalConstants.EVENT_MESSAGE, "Responsible Person Removed Successfully");
		request.setAttribute(CustomerPortalConstants.PARTY_ID, partyId);
		if(UtilValidate.isNotEmpty(marketingCampaignId)) {
			request.setAttribute("marketingCampaignId", marketingCampaignId);			
		}
		if(UtilValidate.isNotEmpty(contactListId)) {
			request.setAttribute("contactListId", contactListId);			
		}
		return "success";
	}
	@SuppressWarnings("unchecked")
	public static String getCallStatusHistory(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String callStatus="";
		String callStatusId="";
		String marketingCampaignId="";
		Timestamp createdStamp =null;
		String csrPartyId="";
		String partyId = request.getParameter("partyId");
		if(UtilValidate.isNotEmpty(partyId)) {
			partyId = (String)context.get("partyId");
		}
		List<GenericValue> resultList = new ArrayList<>();
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		String globalDateFormat = org.groupfio.common.portal.util.DataHelper.getGlobalDateFormat(delegator);
		try {
			List<EntityCondition> conditionList = FastList.newInstance();
			if (UtilValidate.isNotEmpty(partyId)) {
				conditionList.add(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,partyId));
			}
			String orderBy = "lastUpdatedTxStamp";
			String orderDirection = "DESC";
			try {
				DynamicViewEntity dynamicViewEntity = new DynamicViewEntity();
				dynamicViewEntity.addMemberEntity("CRD", "CallRecordDetails");
				dynamicViewEntity.addAlias("CRD", "csrPartyId");
				dynamicViewEntity.addAlias("CRD", "callStatusId");
				dynamicViewEntity.addAlias("CRD", "createdStamp");
				dynamicViewEntity.addAlias("CRD", "partyId");
				dynamicViewEntity.addAlias("CRD", "callRecordId");
				dynamicViewEntity.addAlias("CRD", "lastUpdatedTxStamp");

				dynamicViewEntity.addMemberEntity("CRM", "CallRecordMaster");
				dynamicViewEntity.addAlias("CRM", "marketingCampaignId");
				dynamicViewEntity.addViewLink("CRD", "CRM", Boolean.FALSE, ModelKeyMap.makeKeyMapList("callRecordId"));
				EntityListIterator pli = EntityQuery.use(delegator)
						.from(dynamicViewEntity)
						.where(EntityCondition.makeCondition(conditionList, EntityOperator.AND))
						.orderBy(UtilMisc.toList(orderBy+" "+orderDirection))
						.cursorScrollInsensitive()
						.cache(true)
						.queryIterator();
				// get the partial list for this page
				resultList = pli.getCompleteList();

				pli.close();
			}catch (GenericEntityException e) {
				String errMsg = "Error: " + e.toString();
				Debug.logError(e, errMsg, MODULE);
			}
			Debug.log("resultList===="+resultList);
			Debug.log("resultList===a="+resultList.size());
			if(resultList != null && resultList.size() > 0) {
				for(GenericValue callRecord : resultList) {
					Map<String, Object> data = new HashMap<String, Object>();
					marketingCampaignId = callRecord.getString("marketingCampaignId");
					callStatusId = callRecord.getString("callStatusId");
					createdStamp = callRecord.getTimestamp("createdStamp");
					csrPartyId = callRecord.getString("csrPartyId");
					String marketingCampaignIdName="";
					if(UtilValidate.isNotEmpty(marketingCampaignId)) {
						GenericValue marketingCampaignName = EntityQuery.use(delegator).select("campaignName").from("MarketingCampaign").where("marketingCampaignId", marketingCampaignId).queryOne();
						if (UtilValidate.isNotEmpty(marketingCampaignName)) {
							marketingCampaignIdName=marketingCampaignName.getString("campaignName");
						}
					}
					data.put("marketingCampaignId", UtilValidate.isNotEmpty(marketingCampaignId) ?marketingCampaignId:"");
					data.put("marketingCampaignIdName", UtilValidate.isNotEmpty(marketingCampaignIdName) ?marketingCampaignIdName:"");
					data.put("callStatusId",UtilValidate.isNotEmpty(callStatusId) ? callStatusId:"");
					data.put("csrPartyId",UtilValidate.isNotEmpty(csrPartyId) ? csrPartyId:"");
					data.put("csrName", UtilValidate.isNotEmpty(csrPartyId)?org.fio.homeapps.util.PartyHelper.getPartyName(delegator, csrPartyId, false):"");
					data.put("createdStamp", UtilValidate.isNotEmpty(createdStamp) ? UtilDateTime.timeStampToString(createdStamp, globalDateFormat, TimeZone.getDefault(), Locale.getDefault()) : "");
					callStatus =EnumUtil.getEnumDescription(delegator, callStatusId, "CALL_STATUS");
					data.put("callStatus", UtilValidate.isNotEmpty(callStatus)?callStatus:"");
					data.put("externalLoginKey", request.getParameter("externalLoginKey"));
					dataList.add(data);
				}
				result.put("responseMessage", "success");
			}
		}catch (Exception e) {
			Debug.log("------- ERROR----"+e);
			result.put("callHistory", dataList);
			result.putAll(ServiceUtil.returnError(e.getMessage()));
			return doJSONResponse(response, e.getMessage());
		}
		result.put("callHistory", dataList);
		return doJSONResponse(response, result);
	}
	public static String updateBirthDate(HttpServletRequest request, HttpServletResponse response){

		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String birthDateStr = (String) context.get("birthDayDate");
		String partyId = (String) context.get("partyId");
		String marketingCampaignId = (String) context.get("marketingCampaignId");
		String contactListId = (String) context.get("contactListId");
		request.setAttribute(CustomerPortalConstants.PARTY_ID, partyId);
		if(UtilValidate.isNotEmpty(marketingCampaignId)) {
			request.setAttribute("marketingCampaignId", marketingCampaignId);			
		}
		if(UtilValidate.isNotEmpty(contactListId)) {
			request.setAttribute("contactListId", contactListId);			
		}
		try{
			GenericValue person = delegator.findOne("Person", UtilMisc.toMap("partyId", partyId),false);
			if(UtilValidate.isNotEmpty(person)){
				if(UtilValidate.isNotEmpty(birthDateStr)){
					Date birthDate = DataUtil.convertDateTimestamp(birthDateStr, null, DateTimeTypeConstant.DATE, DateTimeTypeConstant.SQL_DATE);
					//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					//java.util.Date birthDatesdf = sdf.parse(birthDateStr);
					//person.put("birthDate", java.sql.Date.valueOf(sdf.format(birthDatesdf)));
					person.put("birthDate", birthDate);
					person.store();
					request.setAttribute(CustomerPortalConstants.EVENT_MESSAGE, "Birth Date Updated Successfully");
				}
			}else {
				request.setAttribute(CustomerPortalConstants.ERROR_MESSAGE, "Party doesn't exists");
				return "error";
			}
		} catch (Exception e) {
			Debug.logError(e, "");
			return "error";
		}
		return "success";
	}
	public static String removeBirthDate(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String partyId = (String) context.get("partyId");
		String contactListId = (String) context.get("contactListId");
		String marketingCampaignId = (String) context.get("marketingCampaignId");
		request.setAttribute(CustomerPortalConstants.PARTY_ID, partyId);
		if(UtilValidate.isNotEmpty(marketingCampaignId)) {
			request.setAttribute("marketingCampaignId", marketingCampaignId);			
		}
		if(UtilValidate.isNotEmpty(contactListId)) {
			request.setAttribute("contactListId", contactListId);			
		}
		try{
			GenericValue person = delegator.findOne("Person", UtilMisc.toMap("partyId", partyId),false);
			if(UtilValidate.isNotEmpty(person)){
				person.put("birthDate", null);
				person.store();
				request.setAttribute(CustomerPortalConstants.EVENT_MESSAGE, "Birth Date Removed Successully");
			}else {
				request.setAttribute(CustomerPortalConstants.ERROR_MESSAGE, "Party doesn't exists");
				return "error";
			}
		} catch (Exception e) {
			Debug.logError(e, "");
			return "error";
		}
		return "success";
	}
	public static String getDaysLastCallCountDropdown(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		List<Map<String, String>> dataList = new ArrayList<>();
		Map<String, Object> results = new HashMap<>();
		try {
			dataList = org.groupfio.common.portal.util.DataUtil.getDaysLastCallCountDropdown(delegator);
			results.put("responseMessage", "success");
			results.put("list", dataList);
		} catch (Exception e) {
			Debug.logError(e.getMessage(), MODULE);
			results.put("errorMessage", e.getMessage());
			results.put("responseMessage", "error");
			results.put("list", new ArrayList<Map<String, Object>>());
		}
		return doJSONResponse(response, results);
	}
	public static Map<String, Object> getNonPrimaryPhoneNumberCount(Delegator delegator, String partyId) {
		Map<String, Object> results = new HashMap<>();
		try {
			List<EntityCondition> conditionList = FastList.newInstance();
			if (UtilValidate.isNotEmpty(partyId)) {
				conditionList.add(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,partyId));
				try {
					conditionList.add(EntityCondition.makeCondition("contactMechTypeId",EntityOperator.EQUALS,"TELECOM_NUMBER"));
					conditionList.add(EntityCondition.makeCondition("contactMechPurposeTypeId",EntityOperator.NOT_EQUAL,"PRIMARY_PHONE"));
					conditionList.add(EntityCondition.makeConditionDate("fromDate","thruDate"));

					DynamicViewEntity dynamicViewEntity = new DynamicViewEntity();
					dynamicViewEntity.addMemberEntity("CMTP", "ContactMechTypePurpose");
					dynamicViewEntity.addAlias("CMTP", "contactMechTypeId");
					dynamicViewEntity.addAlias("CMTP", "contactMechPurposeTypeId");

					dynamicViewEntity.addMemberEntity("PCMP", "PartyContactMechPurpose");
					dynamicViewEntity.addAlias("PCMP", "partyId");
					dynamicViewEntity.addAlias("PCMP", "contactMechPurposeTypeId");
					dynamicViewEntity.addAlias("PCMP", "fromDate");
					dynamicViewEntity.addAlias("PCMP", "thruDate");
					dynamicViewEntity.addViewLink("CMTP", "PCMP", Boolean.FALSE, ModelKeyMap.makeKeyMapList("contactMechPurposeTypeId"));

					EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
					Debug.logInfo("mainConditons: "+mainConditons, MODULE);

					long count = org.fio.homeapps.util.QueryUtil.findCountByCondition(delegator,dynamicViewEntity,mainConditons, null, null,null);
					if(UtilValidate.isNotEmpty(count)) {
						results.put("count", count);
					}else {
						results.put("count", "0");	
					}
				}catch (Exception e) {
					String errMsg = "Error: " + e.toString();
					Debug.logError(e, errMsg, MODULE);
				}
			}
		} catch (Exception e) {
			Debug.logError(e.getMessage(), MODULE);
			results.put("errorMessage", e.getMessage());
			results.put("responseMessage", "error");
			results.put("list", new ArrayList<Map<String, Object>>());
		}
		return results;
	}
	
	@SuppressWarnings("unchecked")
	public static String getPersonResponsibleForReport(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		String responsiblePerson = (String) context.get("personResponsibleFor");
		Map result = FastMap.newInstance();
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		ResultSet rs = null;
		try {
			SQLProcessor sqlProcessor = new SQLProcessor(delegator, delegator.getGroupHelperInfo("org.ofbiz"));
			String reportQuery = "SELECT p.party_id, CONCAT_WS(' ',p.first_name,p.last_name) AS customer_name,"
					+ " p.first_name, p.last_name, pr.party_id_to, pr.party_id_from,"
					+ " cm.info_string, tn.contact_number, pa.address1, pa.city ,"
					+ " cg.abbreviation AS country, sg.abbreviation AS state,"
					+ " pa.postal_code, idn.id_value AS loyality_id, eco.attr_value AS ecometry_id,"
					+ " CONCAT_WS(' ',rp.first_name,rp.last_name) AS responsible_party_name"
					+ " FROM person p LEFT JOIN party_relationship pr ON p.party_id = pr.party_id_from"
					+ " LEFT JOIN person rp ON rp.party_id = pr.party_id_to"
					+ " LEFT JOIN party_supplemental_data psd ON p.party_id = psd.party_id"
					+ " LEFT JOIN postal_address pa ON pa.contact_mech_id = psd.primary_postal_address_id"
					+ " LEFT JOIN geo cg ON cg.geo_id = pa.country_geo_id"
					+ " LEFT JOIN geo sg ON sg.geo_id = pa.state_province_geo_id"
					+ " LEFT JOIN telecom_number tn ON tn.contact_mech_id = psd.primary_telecom_number_id"
					+ " LEFT JOIN contact_mech cm ON cm.contact_mech_id = psd.primary_email_id"
					+ " LEFT JOIN party_identification idn ON idn.party_id = p.party_id AND idn.party_identification_type_id = 'LOYALTY_ID'"
					+ " LEFT JOIN party_attribute eco ON eco.party_id = p.party_id AND eco.attr_name = 'ECOMETRY_ID'"
					+ " WHERE PR.PARTY_ID_TO = '" + responsiblePerson + "'"
					+ " AND (PR.FROM_DATE <= NOW() OR PR.FROM_DATE IS NULL)"
					+ " AND (PR.THRU_DATE >= NOW() OR PR.THRU_DATE IS NULL)" + " AND (ROLE_TYPE_ID_FROM = 'CUSTOMER')"
					+ " AND (ROLE_TYPE_ID_TO = 'CUST_SERVICE_REP')"
					+ " AND (PARTY_RELATIONSHIP_TYPE_ID = 'RESPONSIBLE_FOR')"
					+ " ORDER BY p.first_name,p.last_name ASC";
			rs = sqlProcessor.executeQuery(reportQuery);
			if (rs != null) {
				try {
					while (rs.next()) {
						Map<String, Object> data = new HashMap<String, Object>();
						String partyId = rs.getString("party_id");
						String customerName = rs.getString("customer_name");
						String address1 = rs.getString("address1");
						String city = rs.getString("city");
						String state = rs.getString("state");
						String zipCode = rs.getString("postal_code");
						String phoneNumber = rs.getString("contact_number");
						String emailAddress = rs.getString("info_string");
						String loyaltyId = rs.getString("loyality_id");
						String ecometryId = rs.getString("ecometry_id");
						String personResponsible = rs.getString("responsible_party_name");
						data.put("partyId", partyId);
						data.put("customerName", UtilValidate.isNotEmpty(customerName) ? customerName : "");
						data.put("address1", UtilValidate.isNotEmpty(address1) ? address1 : "");
						data.put("city", UtilValidate.isNotEmpty(city) ? city : "");
						data.put("state", UtilValidate.isNotEmpty(state) ? state : "");
						data.put("zipCode", UtilValidate.isNotEmpty(zipCode) ? zipCode : "");
						data.put("phoneNumber", UtilValidate.isNotEmpty(phoneNumber) ? phoneNumber : "");
						data.put("emailAddress", UtilValidate.isNotEmpty(emailAddress) ? emailAddress : "");
						data.put("loyaltyId", UtilValidate.isNotEmpty(loyaltyId) ? loyaltyId : "");
						data.put("ecometryId", UtilValidate.isNotEmpty(ecometryId) ? ecometryId : "");
						data.put("personResponsible", UtilValidate.isNotEmpty(personResponsible) ? personResponsible : "");
						dataList.add(data);
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (rs != null) {
						try {
							rs.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				}

			}
		} catch (Exception e) {
			result.put("data", new ArrayList<Map<String, Object>>());
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
			return doJSONResponse(response, result);
		}
		Debug.log("dataList----" + dataList);
		result.put("data", dataList);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		result.put(ModelService.SUCCESS_MESSAGE, "Success");
		return doJSONResponse(response, result);
	}
	
	@SuppressWarnings("unchecked")
	public static String getAccountManagerCountsReport(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		Map result = FastMap.newInstance();
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		ResultSet rs = null;
		try {
			SQLProcessor sqlProcessor = new SQLProcessor(delegator, delegator.getGroupHelperInfo("org.ofbiz"));
			String reportQuery ="SELECT P.party_id, CONCAT_WS(' ',p.first_name,p.last_name) AS csr_name, p.first_name, p.last_name,COUNT(pr.party_id_to) as customer_count, pr.party_id_to"
								+" FROM person p LEFT JOIN party_relationship pr ON p.party_id = pr.party_id_to"
								+" WHERE (PR.FROM_DATE <= NOW() OR PR.FROM_DATE IS NULL)"
								+" AND (PR.THRU_DATE >= NOW() OR PR.THRU_DATE IS NULL)"
								+" AND (ROLE_TYPE_ID_FROM = 'CUSTOMER')"
								+" AND (ROLE_TYPE_ID_TO = 'CUST_SERVICE_REP')"
								+" AND (PARTY_RELATIONSHIP_TYPE_ID = 'RESPONSIBLE_FOR')"
								+" GROUP BY p.party_id ORDER BY p.first_name, p.last_name ASC";
			rs = sqlProcessor.executeQuery(reportQuery);
			if (rs != null) {
				try {
					while (rs.next()) {
						Map<String, Object> data = new HashMap<String, Object>();
						String partyId = rs.getString("party_id");
						String customerName = rs.getString("csr_name");
						String customerCount = rs.getString("customer_count");
						data.put("partyId", partyId);
						data.put("customerName", UtilValidate.isNotEmpty(customerName) ? customerName : "");
						data.put("customerCount", UtilValidate.isNotEmpty(customerCount) ? customerCount : "");
						dataList.add(data);
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (rs != null) {
						try {
							rs.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}  catch (Exception e) {
			result.put("data", new ArrayList<Map<String, Object>>());
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
			return doJSONResponse(response, result);
		}
		Debug.log("dataList----" + dataList);
		result.put("data", dataList);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		result.put(ModelService.SUCCESS_MESSAGE, "Success");
		return doJSONResponse(response, result);
	}
	public static String getCsrDropdown(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		List<Map<String, String>> dataList = new ArrayList<>();
		Map<String, Object> results = new HashMap<>();
		try {
			dataList = UtilCampaign.getCsrDropdown(delegator, null, true);
			results.put("responseMessage", "success");
			results.put("list", dataList);
		} catch (Exception e) {
			Debug.logError(e.getMessage(), MODULE);
			results.put("errorMessage", e.getMessage());
			results.put("responseMessage", "error");
			results.put("list", new ArrayList<Map<String, Object>>());
		}
		return doJSONResponse(response, results);
	}
	public static String getDailySummaryReport(HttpServletRequest request, HttpServletResponse response) throws SQLException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		Map<String, Object> result = FastMap.newInstance();
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		String globalDateFormat = org.fio.homeapps.util.DataHelper.getGlobalDateFormat(delegator);
		String marketingCampaignId = request.getParameter("marketingCampaignId");
		String createdDate = request.getParameter("createdDate");
		String csrPartyId = request.getParameter("csrPartyId");
		String callNumber = request.getParameter("callNumber");
		ResultSet rs = null;
		List<Object> values = new ArrayList<>();
		try {
			SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd");
			if (UtilValidate.isNotEmpty(createdDate)) {
				createdDate = formatter1.format(new SimpleDateFormat(globalDateFormat).parse(createdDate));
			}
			int fioGridFetch = org.groupfio.common.portal.util.DataUtil.defaultFioGridfetchLimit(delegator);
			int viewIndex = 0;
			try {
				viewIndex = Integer.parseInt(request.getParameter("VIEW_INDEX"));
			} catch (NumberFormatException e) {
				viewIndex = 0;
			}
			result.put("viewIndex", viewIndex);

			int viewSize = fioGridFetch;
			try {
				viewSize = Integer.parseInt(request.getParameter("VIEW_SIZE"));
			} catch (NumberFormatException e) {
				viewSize = fioGridFetch;
			}
			result.put("viewSize", viewSize);

			int lowIndex = viewIndex * viewSize;
			int highIndex = (viewIndex + 1) * viewSize;

			String orderBy = "CAMPAIGN_NAME, CSR_NAME,CALL_NUMBER";
			SQLProcessor sqlProcessor = new SQLProcessor(delegator, delegator.getGroupHelperInfo("org.ofbiz"));
			String query = "SELECT mc.campaign_name AS CAMPAIGN_NAME, CONCAT(p.first_name, ' ', p.last_name) AS CSR_NAME, " +
					"(COUNT(CALL_STATUS_ID) / 8) AS CALLS_PER_HOUR, " +
					"crd.CALL_NUMBER as CALL_NUMBER,mc.marketing_Campaign_Id AS campaign_id, crd.CSR_PARTY_ID AS csr_party_id, " +
					"crd.CREATED_STAMP AS CREATED_STAMP, COUNT(CALL_STATUS_ID) AS CALL_COUNT " +
					"FROM call_record_details AS crd " +
					"INNER JOIN `call_record_master` AS crm ON crd.`call_record_id` = crm.`call_record_id` " +
					"LEFT JOIN `marketing_campaign` AS mc ON crm.`MARKETING_CAMPAIGN_ID` = mc.MARKETING_CAMPAIGN_ID " +
					"LEFT JOIN person AS p ON crd.`CSR_PARTY_ID` = p.party_id " +
					"WHERE DATE(crd.CREATED_STAMP) = ?";
			values.add(createdDate);
			if (UtilValidate.isNotEmpty(marketingCampaignId)) {
				query = query + " AND crm.MARKETING_CAMPAIGN_ID = ? ";
				values.add(marketingCampaignId);
			}
			if (UtilValidate.isNotEmpty(csrPartyId)) {
				query = query + " AND crd.CSR_PARTY_ID = ? ";
				values.add(csrPartyId);
			}
			if(UtilValidate.isNotEmpty(callNumber)) {
				query = query +" AND crd.CALL_NUMBER = ?";
				values.add(callNumber);
			}
			query = query + " GROUP BY CSR_PARTY_ID, CREATED_STAMP, crd.CALL_NUMBER,CAMPAIGN_NAME, crm.MARKETING_CAMPAIGN_ID ";
			query = query + " ORDER BY " + orderBy;
			query = query + " LIMIT " + lowIndex + "," + viewSize + "";
			rs = QueryUtil.getResultSet(query, values, delegator);
			if (rs != null) {
				while (rs.next()) {
					Map<String, Object> data = new HashMap<String, Object>();
					String campaignName = rs.getString("CAMPAIGN_NAME");
					String csrName = rs.getString("CSR_NAME");
					String callCount = rs.getString("CALL_COUNT");
					double callsPerHourDouble = rs.getDouble("CALLS_PER_HOUR");
					double callNumberVal = rs.getDouble("CALL_NUMBER");
					String callsPerHour = String.format("%.2f", callsPerHourDouble);
					data.put("callNumber", UtilValidate.isNotEmpty(callNumberVal) ? callNumberVal : "");
					data.put("campaignName", UtilValidate.isNotEmpty(campaignName) ? campaignName : "");
					data.put("csrName", UtilValidate.isNotEmpty(csrName) ? csrName : "");
					data.put("callCount", UtilValidate.isNotEmpty(callCount) ? callCount : "");
					data.put("callsPerHour", UtilValidate.isNotEmpty(callsPerHour) ? callsPerHour : "");
					dataList.add(data);
				}
			}else {
				result.put("data", new ArrayList<Map<String, Object>>());
				result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
				result.put(ModelService.ERROR_MESSAGE, "Error");
			}
			result.put("data", dataList);
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
			result.put(ModelService.SUCCESS_MESSAGE, "Success");
		} catch (Exception e) {
			result.put("data", new ArrayList<Map<String, Object>>());
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
		} finally {
			if(rs !=null) {
				rs.close();
			}
		}
		return doJSONResponse(response, result);
	}
	public static String getContactRatesReport(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		Map<String, Object> result = FastMap.newInstance();
		List<Object> contactRatesReportList = FastList.newInstance();
		String campaignName = request.getParameter("campaignName");
		String createdDateFrom = request.getParameter("fromDate");
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String createdDateTo = request.getParameter("thruDate");
		String csrName = request.getParameter("csrName");
		ResultSet rs = null;
		String globalDateFormat = org.fio.homeapps.util.DataHelper.getGlobalDateFormat(delegator);
		SQLProcessor sqlProcessor = null;
		try {
			int highIndex = 0;
			int lowIndex = 0;
			int resultListSize = 0;
			int viewSize = 0;
			int recordsTotal = 0;
			String orderBy = "CAMPAIGN_NAME, CSR_NAME";
			sqlProcessor = new SQLProcessor(delegator, delegator.getGroupHelperInfo("org.ofbiz"));

			SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat formatter2 = new SimpleDateFormat(globalDateFormat);
			if (UtilValidate.isNotEmpty(createdDateFrom)) {
				createdDateFrom = formatter1.format(new SimpleDateFormat(globalDateFormat).parse(createdDateFrom));
			}
			if (UtilValidate.isNotEmpty(createdDateTo)) {
				createdDateTo = formatter1.format(new SimpleDateFormat(globalDateFormat).parse(createdDateTo));
			}
			GenericValue systemProperty = EntityQuery.use(delegator).select("systemPropertyValue")
					.from("SystemProperty")
					.where("systemResourceId", "general", "systemPropertyId", "fio.grid.fetch.limit").queryFirst();

			// set the page parameters
			int viewIndex = 0;
			try {
				viewIndex = Integer.parseInt((String) context.get("VIEW_INDEX"));
			} catch (Exception e) {
				viewIndex = 0;
			}
			result.put("viewIndex", Integer.valueOf(viewIndex));
			int fioGridFetch = UtilValidate.isNotEmpty(systemProperty)
					&& UtilValidate.isNotEmpty(systemProperty.getString("systemPropertyValue"))
					? Integer.parseInt((String) systemProperty.getString("systemPropertyValue"))
							: 1000;
					viewSize = fioGridFetch;
					try {
						viewSize = Integer.parseInt((String) context.get("VIEW_SIZE"));
					} catch (Exception e) {
						viewSize = fioGridFetch;
					}
					result.put("viewSize", Integer.valueOf(viewSize));
					// get the indexes for the partial list
					lowIndex = viewIndex * viewSize;
					highIndex = (viewIndex + 1) * viewSize;
					// Query to fetch the contact rates report
					String query = " SELECT CAMPAIGN_NAME,CSR_NAME, SUM(CALL_COUNT) AS call_count, SUM(contact) AS contact, SUM(sales) AS sales, dt,"
							+ " ((SUM(contact) / SUM(CALL_COUNT)) * 100) AS CONTACT_MADE_PERC, ((SUM(sales) / SUM(CALL_COUNT)) * 100) AS SALES_MADE_PERC"
							+ " FROM (SELECT mc.campaign_name AS CAMPAIGN_NAME, CONCAT(p.first_name,' ', p.last_name) AS CSR_NAME,"
							+ " COUNT(CALL_STATUS_ID) AS CALL_COUNT, 0 AS contact, 0 AS sales, DATE(crd.CREATED_STAMP) AS dt"
							+ " FROM call_record_details AS crd INNER JOIN `call_record_master` AS crm ON crd.call_record_id = crm.call_record_id "
							+ " LEFT JOIN `marketing_campaign` AS mc ON crm.`MARKETING_CAMPAIGN_ID` = mc.MARKETING_CAMPAIGN_ID "
							+ " LEFT JOIN person AS p ON crd.`CSR_PARTY_ID` = p.party_id WHERE DATE(crd.CREATED_STAMP) >=  '"+ createdDateFrom + "'" + " AND DATE(crd.CREATED_STAMP) <=  '" + createdDateTo
							+ "' GROUP BY CAMPAIGN_NAME, CSR_NAME, dt"
							+ " UNION SELECT mc.campaign_name AS CAMPAIGN_NAME, CONCAT(p.first_name,' ', p.last_name) AS CSR_NAME, 0 AS CALL_COUNT,"
							+ " COUNT(CALL_STATUS_ID) AS contact, 0 AS sales, DATE(crd.CREATED_STAMP) AS dt"
							+ " FROM call_record_details AS crd INNER JOIN `call_record_master` AS crm ON crd.call_record_id = crm.call_record_id"
							+ " LEFT JOIN `marketing_campaign` AS mc ON crm.`MARKETING_CAMPAIGN_ID` = mc.MARKETING_CAMPAIGN_ID"
							+ " LEFT JOIN person AS p ON crd.`CSR_PARTY_ID` = p.party_id WHERE DATE(crd.CREATED_STAMP) >= '"+ createdDateFrom + "'" + " AND DATE(crd.CREATED_STAMP) <= '" + createdDateTo
							+ "' AND CALL_STATUS_ID IN ('1003', '1007', '1008', '1012', '1016','1017', '1021', '1025', '1026')"
							+ " GROUP BY CAMPAIGN_NAME, CSR_NAME, dt UNION SELECT mc.campaign_name AS CAMPAIGN_NAME, CONCAT(p.first_name, ' ', p.last_name) AS CSR_NAME,"
							+ " 0 AS CALL_COUNT, 0 AS contact, COUNT(CALL_STATUS_ID) AS sales, DATE(crd.CREATED_STAMP) AS dt FROM call_record_details AS crd"
							+ " INNER JOIN `call_record_master` AS crm ON crd.call_record_id = crm.call_record_id"
							+ " LEFT JOIN `marketing_campaign` AS mc ON crm.`MARKETING_CAMPAIGN_ID` = mc.MARKETING_CAMPAIGN_ID LEFT JOIN person AS p ON crd.`CSR_PARTY_ID` = p.party_id"
							+ " WHERE DATE(crd.CREATED_STAMP) >= '" + createdDateFrom + "' AND DATE(crd.CREATED_STAMP) <= '"
							+ createdDateTo + "'"
							+ " AND CALL_STATUS_ID IN ('1007', '1016', '1025') GROUP BY CAMPAIGN_NAME, CSR_NAME, dt) temp";
					if (UtilValidate.isNotEmpty(campaignName)) {
						query = query + " WHERE CAMPAIGN_NAME = '" + campaignName + "'";
					}
					if (UtilValidate.isNotEmpty(csrName) && UtilValidate.isNotEmpty(campaignName)) {
						query = query + " AND CSR_NAME = '" + csrName + "'";
					} else if (UtilValidate.isNotEmpty(csrName) && UtilValidate.isEmpty(campaignName)) {
						query = query + " WHERE CSR_NAME = '" + csrName + "'";
					}
					query = query + " GROUP BY CAMPAIGN_NAME, CSR_NAME, dt";
					query = query + " ORDER BY " + orderBy;
					query = query + " LIMIT " + lowIndex + ", " + viewSize;
					rs = sqlProcessor.executeQuery(query);
					if (rs != null) {
						while (rs.next()) {
							Map<String, Object> contactRatesReportMap = FastMap.newInstance();
							String date = null;
							if (UtilValidate.isNotEmpty(rs.getString("CAMPAIGN_NAME"))) {
								contactRatesReportMap.put("campaignName", UtilValidate.isNotEmpty(rs.getString("CAMPAIGN_NAME")) ? rs.getString("CAMPAIGN_NAME") : "");
								contactRatesReportMap.put("partyName", UtilValidate.isNotEmpty(rs.getString("CSR_NAME")) ? rs.getString("CSR_NAME") : "");
								if (UtilValidate.isNotEmpty(rs.getString("dt"))) {
									date = formatter2.format(new SimpleDateFormat("yyyy-MM-dd").parse(rs.getString("dt")));
								}
								contactRatesReportMap.put("date", UtilValidate.isNotEmpty(date) ? date : "");
								contactRatesReportMap.put("callsMade", UtilValidate.isNotEmpty(rs.getString("CALL_COUNT")) ? rs.getString("CALL_COUNT") : "");
								contactRatesReportMap.put("contactMade", UtilValidate.isNotEmpty(rs.getString("CONTACT")) ? rs.getString("CONTACT") : "");
								contactRatesReportMap.put("contactMadePerc",UtilValidate.isNotEmpty(rs.getDouble("CONTACT_MADE_PERC")) ? String.format("%.2f", rs.getDouble("CONTACT_MADE_PERC")): "");
								contactRatesReportMap.put("salesMade", UtilValidate.isNotEmpty(rs.getString("SALES")) ? rs.getString("SALES") : "");
								contactRatesReportMap.put("salesMadePerc", UtilValidate.isNotEmpty(rs.getDouble("SALES_MADE_PERC")) ? String.format("%.2f", rs.getDouble("SALES_MADE_PERC")) : "");
								contactRatesReportList.add(contactRatesReportMap);
							}
						}
					}else {
						result.put("data", new ArrayList<Map<String, Object>>());
						result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
						result.put(ModelService.ERROR_MESSAGE,"Error");
						return doJSONResponse(response, result);
					}
					result.put("data", contactRatesReportList);
		} catch (Exception e) {
			Debug.logError("Exception in getContactRatesReport" + e.getMessage(), MODULE);
			result.put("data", new ArrayList<Map<String, Object>>());
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
			return doJSONResponse(response, result);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				Debug.log("Error in close connection " + e.getMessage());
			}
		}
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		result.put(ModelService.SUCCESS_MESSAGE, "Success");
		return doJSONResponse(response, result);
	}
	@SuppressWarnings("unchecked")
	public static String getDetailsByDayList(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		Locale locale = (Locale) context.get("locale");
		String campaignName = request.getParameter("campaignName");
		String createdDate = request.getParameter("createdDate");
		String csrName = request.getParameter("csrName");
		String callNumber = request.getParameter("callNumber");
		Map result = FastMap.newInstance();
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		String globalDateFormat = org.groupfio.common.portal.util.DataHelper.getGlobalDateFormat(delegator);
		ResultSet rs = null;
		List<Object> values = new ArrayList<>();
		try {
			SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd");
			if (UtilValidate.isNotEmpty(createdDate)) {
				createdDate = formatter1.format(new SimpleDateFormat(globalDateFormat).parse(createdDate));
			}
			String orderBy = "CAMPAIGN_NAME, CSR_PARTY_NAME";
			String query = "SELECT CAMPAIGN_NAME AS MARKETNG_CAMPAIGN, TT.PARTY_ID AS CUSTOMER_NUMBER, CONCAT(FIRST_NAME, ' ', LAST_NAME) AS "
					+ "CUSTOMER_NAME, CALL_NUMBER, CSR_PARTY_NAME AS CSR_NAME, SUM(CALL_MADE) AS CALLS_MADE, SUM(CONTACT_MADE) AS CONTACT_MADE, "
					+ "SUM(EMAIL_SENT) AS EMAIL_SENT, SUM(VM) AS LEFT_VOICEMAIL, SUM(SM) AS SALES_MADE, SUM(CONCIERGE) AS CONCIERGE, SUM(NO_CONTACT) "
					+ "AS NO_CONTACT, SUM(DNC) AS DNC, SUM(DIRECT_CALL) AS DIRECT_CALL, SUM(CALL_FROM_Q) AS CALL_FROM_Q, SUM(BAD_NO) AS BAD_NO, "
					+ "SUM(MAILER) AS MAILER, TT.CREATED_STAMP FROM (SELECT CAMPAIGN_NAME, PARTY_ID, CSR_PARTY_NAME, CALL_NUMBER, COUNT(*) AS "
					+ "CALL_MADE, 0 AS CONTACT_MADE, 0 AS EMAIL_SENT, 0 AS VM, 0 AS SM, 0 AS CONCIERGE, 0 AS NO_CONTACT, 0 AS DNC, 0 AS DIRECT_CALL, "
					+ "0 AS CALL_FROM_Q, 0 AS BAD_NO, 0 AS MAILER,temp.CREATED_STAMP FROM (SELECT mc.CAMPAIGN_NAME AS CAMPAIGN_NAME, crd.PARTY_ID AS "
					+ "PARTY_ID, CONCAT(p.first_name, ' ', p.last_name) AS CSR_PARTY_NAME, crd.CALL_NUMBER, CALL_STATUS_ID AS CALL_MADE, "
					+ "DATE(crd.CREATED_STAMP) AS CREATED_STAMP FROM call_record_details AS crd LEFT JOIN `call_record_master` AS crm ON  crm.`CALL_RECORD_ID`=crd.CALL_RECORD_ID LEFT JOIN `marketing_campaign` AS mc ON crm.`MARKETING_CAMPAIGN_ID` = mc.MARKETING_CAMPAIGN_ID LEFT JOIN `person` p ON crd.`CSR_PARTY_ID` = p.party_id WHERE "
					+ "DATE(crd.CREATED_STAMP)= ? GROUP BY CAMPAIGN_NAME, CSR_PARTY_NAME, crd.CALL_NUMBER, DATE(crd.CREATED_STAMP), "
					+ "crd.PARTY_ID, CALL_STATUS_ID) temp GROUP BY CAMPAIGN_NAME, CSR_PARTY_NAME, CALL_NUMBER, PARTY_ID ,temp.CREATED_STAMP UNION SELECT CAMPAIGN_NAME, "
					+ "PARTY_ID, CSR_PARTY_NAME, CALL_NUMBER, 0 AS CALL_MADE, COUNT(*) AS CONTACT_MADE, 0 AS EMAIL_SENT, 0 AS VM, 0 AS SM, "
					+ "0 AS CONCIERGE, 0 AS NO_CONTACT, 0 AS DNC, 0 AS DIRECT_CALL, 0 AS CALL_FROM_Q, 0 AS BAD_NO, 0 AS MAILER, temp.CREATED_STAMP FROM "
					+ "( SELECT mc.CAMPAIGN_NAME AS CAMPAIGN_NAME, crd.PARTY_ID, CONCAT(p.first_name, ' ', p.last_name) AS CSR_PARTY_NAME, "
					+ "crd.CALL_NUMBER, CALL_STATUS_ID AS CONTACT_MADE, DATE(crd.CREATED_STAMP) AS CREATED_STAMP FROM call_record_details AS crd "
					+ "LEFT JOIN `call_record_master` AS crm ON  crm.`CALL_RECORD_ID`=crd.CALL_RECORD_ID LEFT JOIN `marketing_campaign` AS mc "
					+ "ON crm.`MARKETING_CAMPAIGN_ID` = mc.MARKETING_CAMPAIGN_ID LEFT JOIN `person` p ON crd.`CSR_PARTY_ID` = p.party_id WHERE "
					+ "CALL_STATUS_ID IN ('1003', '1007', '1008', '1012', '1016', '1017', '1021', '1025', '1026') AND "
					+ "DATE(crd.CREATED_STAMP)= ? GROUP BY CAMPAIGN_NAME, CSR_PARTY_NAME, crd.CALL_NUMBER, "
					+ "DATE(crd.CREATED_STAMP), crd.PARTY_ID, CALL_STATUS_ID ) temp GROUP BY CAMPAIGN_NAME, CSR_PARTY_NAME, temp.CREATED_STAMP , "
					+ "CALL_NUMBER, PARTY_ID UNION SELECT CAMPAIGN_NAME, PARTY_ID, CSR_PARTY_NAME, CALL_NUMBER, 0 AS CALL_MADE, "
					+ "0 AS CONTACT_MADE, COUNT(*) AS EMAIL_SENT, 0 AS VM, 0 AS SM, 0 AS CONCIERGE, 0 AS NO_CONTACT, 0 AS DNC, "
					+ "0 AS DIRECT_CALL, 0 AS CALL_FROM_Q, 0 AS BAD_NO, 0 AS MAILER, temp.CREATED_STAMP FROM "
					+ "(SELECT mc.CAMPAIGN_NAME AS CAMPAIGN_NAME, crd.PARTY_ID, CONCAT(p.first_name, ' ', p.last_name) AS CSR_PARTY_NAME, "
					+ "crd.CALL_NUMBER, CALL_STATUS_ID AS EMAIL_SENT, DATE(crd.CREATED_STAMP) AS CREATED_STAMP FROM call_record_details AS crd "
					+ "LEFT JOIN `call_record_master` AS crm ON  crm.`CALL_RECORD_ID`=crd.CALL_RECORD_ID LEFT JOIN `marketing_campaign` AS mc "
					+ "ON crm.`MARKETING_CAMPAIGN_ID` = mc.MARKETING_CAMPAIGN_ID LEFT JOIN `person` p ON crd.`CSR_PARTY_ID` = p.party_id "
					+ "WHERE CALL_STATUS_ID IN ('1004', '1013', '1022', '1029') AND DATE(crd.CREATED_STAMP)= ? "
					+ "GROUP BY CAMPAIGN_NAME, CSR_PARTY_NAME, crd.CALL_NUMBER, DATE(crd.CREATED_STAMP), crd.PARTY_ID, CALL_STATUS_ID ) "
					+ "temp GROUP BY CAMPAIGN_NAME, CSR_PARTY_NAME, CALL_NUMBER, PARTY_ID ,temp.CREATED_STAMP UNION SELECT CAMPAIGN_NAME, PARTY_ID, "
					+ "CSR_PARTY_NAME, CALL_NUMBER, 0 AS CALL_MADE, 0 AS CONTACT_MADE, 0 AS EMAIL_SENT, COUNT(*) AS VM, 0 AS SM, "
					+ "0 AS CONCIERGE, 0 AS NO_CONTACT, 0 AS DNC, 0 AS DIRECT_CALL, 0 AS CALL_FROM_Q, 0 AS BAD_NO, 0 AS MAILER, "
					+ "temp.CREATED_STAMP FROM (SELECT mc.CAMPAIGN_NAME AS CAMPAIGN_NAME, crd.PARTY_ID, CONCAT(p.first_name, ' ', p.last_name) "
					+ "AS CSR_PARTY_NAME, crd.CALL_NUMBER, CALL_STATUS_ID AS VM, DATE(crd.CREATED_STAMP) AS CREATED_STAMP FROM call_record_details AS "
					+ "crd LEFT JOIN `call_record_master` AS crm ON  crm.`CALL_RECORD_ID`=crd.CALL_RECORD_ID LEFT JOIN `marketing_campaign` AS mc "
					+ "ON crm.`MARKETING_CAMPAIGN_ID` = mc.MARKETING_CAMPAIGN_ID LEFT JOIN `person` p ON crd.`CSR_PARTY_ID` = p.party_id WHERE "
					+ "CALL_STATUS_ID IN ('1001', '1010', '1019', '1028') AND DATE(crd.CREATED_STAMP)= ? GROUP BY CAMPAIGN_NAME, "
					+ "CSR_PARTY_NAME, crd.CALL_NUMBER, DATE(crd.CREATED_STAMP), crd.PARTY_ID, CALL_STATUS_ID) temp GROUP BY CAMPAIGN_NAME, "
					+ "CSR_PARTY_NAME, CALL_NUMBER, PARTY_ID ,temp.CREATED_STAMP UNION SELECT CAMPAIGN_NAME, PARTY_ID, CSR_PARTY_NAME, CALL_NUMBER, 0 AS CALL_MADE, "
					+ "0 AS CONTACT_MADE, 0 AS EMAIL_SENT, 0 AS VM, COUNT(*) AS SM, 0 AS CONCIERGE, 0 AS NO_CONTACT, 0 AS DNC, 0 AS DIRECT_CALL, "
					+ "0 AS CALL_FROM_Q, 0 AS BAD_NO, 0 AS MAILER, temp.CREATED_STAMP FROM (SELECT mc.CAMPAIGN_NAME AS CAMPAIGN_NAME, crd.PARTY_ID, "
					+ "CONCAT(p.first_name, ' ', p.last_name) AS CSR_PARTY_NAME, crd.CALL_NUMBER, CALL_STATUS_ID AS SM, "
					+ "DATE(crd.CREATED_STAMP) AS CREATED_STAMP FROM call_record_details AS crd LEFT JOIN `call_record_master` AS crm ON "
					+ "crm.`CALL_RECORD_ID`=crd.CALL_RECORD_ID LEFT JOIN `marketing_campaign` AS mc ON "
					+ "crm.`MARKETING_CAMPAIGN_ID` = mc.MARKETING_CAMPAIGN_ID LEFT JOIN `person` p ON crd.`CSR_PARTY_ID` = p.party_id WHERE "
					+ "CALL_STATUS_ID IN ('1007', '1016', '1025') AND DATE(crd.CREATED_STAMP)= ? GROUP BY CAMPAIGN_NAME, "
					+ "CSR_PARTY_NAME, crd.CALL_NUMBER, crd.PARTY_ID, DATE(crd.CREATED_STAMP),CALL_STATUS_ID) temp GROUP BY CAMPAIGN_NAME, CSR_PARTY_NAME, CALL_NUMBER, "
					+ "PARTY_ID ,temp.CREATED_STAMP UNION SELECT CAMPAIGN_NAME, PARTY_ID, CSR_PARTY_NAME, CALL_NUMBER, 0 AS CALL_MADE, 0 AS CONTACT_MADE, "
					+ "0 AS EMAIL_SENT, 0 AS VM, 0 AS SM, COUNT(*) AS CONCIERGE, 0 AS NO_CONTACT, 0 AS DNC, 0 AS DIRECT_CALL, 0 AS CALL_FROM_Q, "
					+ "0 AS BAD_NO, 0 AS MAILER, temp.CREATED_STAMP FROM (SELECT mc.CAMPAIGN_NAME AS CAMPAIGN_NAME, crd.PARTY_ID, "
					+ "CONCAT(p.first_name, ' ', p.last_name) AS CSR_PARTY_NAME, crd.CALL_NUMBER, CALL_STATUS_ID AS CONCIERGE, "
					+ "DATE(crd.CREATED_STAMP) AS CREATED_STAMP FROM call_record_details AS crd LEFT JOIN `call_record_master` AS crm ON  "
					+ "crm.`CALL_RECORD_ID`=crd.CALL_RECORD_ID LEFT JOIN `marketing_campaign` AS mc ON "
					+ "crm.`MARKETING_CAMPAIGN_ID` = mc.MARKETING_CAMPAIGN_ID LEFT JOIN `person` p ON crd.`CSR_PARTY_ID` = p.party_id "
					+ "WHERE CALL_STATUS_ID IN ('1000', '1009', '1018') AND DATE(crd.CREATED_STAMP)= ? GROUP BY CAMPAIGN_NAME, "
					+ "CSR_PARTY_NAME, crd.CALL_NUMBER, DATE(crd.CREATED_STAMP), crd.PARTY_ID, CALL_STATUS_ID ) temp GROUP BY CAMPAIGN_NAME,temp.CREATED_STAMP, "
					+ "CSR_PARTY_NAME, CALL_NUMBER, PARTY_ID UNION SELECT CAMPAIGN_NAME, PARTY_ID, CSR_PARTY_NAME, CALL_NUMBER, 0 AS CALL_MADE, "
					+ "0 AS CONTACT_MADE, 0 AS EMAIL_SENT, 0 AS VM, 0 AS SM, 0 AS CONCIERGE, COUNT(*) AS NO_CONTACT, 0 AS DNC, 0 AS DIRECT_CALL, "
					+ "0 AS CALL_FROM_Q, 0 AS BAD_NO, 0 AS MAILER, temp.CREATED_STAMP FROM (SELECT mc.CAMPAIGN_NAME AS CAMPAIGN_NAME, crd.PARTY_ID, CONCAT(p.first_name, ' ', p.last_name) AS CSR_PARTY_NAME, crd.CALL_NUMBER, CALL_STATUS_ID AS NO_CONTACT, "
					+ "DATE(crd.CREATED_STAMP) AS CREATED_STAMP FROM call_record_details AS crd LEFT JOIN `call_record_master` AS crm ON  "
					+ "crm.`CALL_RECORD_ID`=crd.CALL_RECORD_ID LEFT JOIN `marketing_campaign` AS mc ON "
					+ "crm.`MARKETING_CAMPAIGN_ID` = mc.MARKETING_CAMPAIGN_ID LEFT JOIN `person` p ON crd.`CSR_PARTY_ID` = p.party_id "
					+ "WHERE CALL_STATUS_ID IN ('1002', '1011', '1020') AND DATE(crd.CREATED_STAMP)= ? GROUP BY CAMPAIGN_NAME, "
					+ "CSR_PARTY_NAME, crd.CALL_NUMBER, DATE(crd.CREATED_STAMP), crd.PARTY_ID, CALL_STATUS_ID) temp GROUP BY CAMPAIGN_NAME, "
					+ "CSR_PARTY_NAME, CALL_NUMBER, PARTY_ID,temp.CREATED_STAMP UNION SELECT CAMPAIGN_NAME, PARTY_ID, CSR_PARTY_NAME, CALL_NUMBER, 0 AS CALL_MADE, "
					+ "0 AS CONTACT_MADE, 0 AS EMAIL_SENT, 0 AS VM, 0 AS SM, 0 AS CONCIERGE, 0 AS NO_CONTACT, COUNT(*) AS DNC, 0 AS DIRECT_CALL, "
					+ "0 AS CALL_FROM_Q, 0 AS BAD_NO, 0 AS MAILER, temp.CREATED_STAMP FROM (SELECT mc.CAMPAIGN_NAME AS CAMPAIGN_NAME, crd.PARTY_ID, "
					+ "CONCAT(p.first_name, ' ', p.last_name) AS CSR_PARTY_NAME, crd.CALL_NUMBER, CALL_STATUS_ID AS DNC, DATE(crd.CREATED_STAMP) AS CREATED_STAMP FROM call_record_details AS crd LEFT JOIN `call_record_master` AS crm ON  "
					+ "crm.`CALL_RECORD_ID`=crd.CALL_RECORD_ID LEFT JOIN `marketing_campaign` AS mc ON "
					+ "crm.`MARKETING_CAMPAIGN_ID` = mc.MARKETING_CAMPAIGN_ID LEFT JOIN `person` p ON crd.`CSR_PARTY_ID` = p.party_id "
					+ "WHERE CALL_STATUS_ID IN ('1006', '1015', '1024') AND DATE(crd.CREATED_STAMP)= ? GROUP BY CAMPAIGN_NAME, "
					+ "CSR_PARTY_NAME, crd.CALL_NUMBER, DATE(crd.CREATED_STAMP), crd.PARTY_ID, CALL_STATUS_ID) temp GROUP BY "
					+ "CAMPAIGN_NAME, CSR_PARTY_NAME, CALL_NUMBER, PARTY_ID,temp.CREATED_STAMP UNION SELECT CAMPAIGN_NAME, PARTY_ID, CSR_PARTY_NAME, CALL_NUMBER, "
					+ "0 AS CALL_MADE, 0 AS CONTACT_MADE, 0 AS EMAIL_SENT, 0 AS VM, 0 AS SM, 0 AS CONCIERGE, 0 AS NO_CONTACT, 0 AS DNC, COUNT(*) "
					+ "AS DIRECT_CALL, 0 AS CALL_FROM_Q, 0 AS BAD_NO, 0 AS MAILER, temp.CREATED_STAMP FROM ( SELECT mc.CAMPAIGN_NAME AS CAMPAIGN_NAME, "
					+ "crd.PARTY_ID, CONCAT(p.first_name, ' ', p.last_name) AS CSR_PARTY_NAME, crd.CALL_NUMBER, CALL_STATUS_ID AS DIRECT_CALL, "
					+ "DATE(crd.CREATED_STAMP) AS CREATED_STAMP FROM call_record_details AS crd LEFT JOIN `call_record_master` AS crm ON  "
					+ "crm.`CALL_RECORD_ID`=crd.CALL_RECORD_ID LEFT JOIN `marketing_campaign` AS mc ON "
					+ "crm.`MARKETING_CAMPAIGN_ID` = mc.MARKETING_CAMPAIGN_ID LEFT JOIN `person` p ON crd.`CSR_PARTY_ID` = p.party_id WHERE "
					+ "CALL_STATUS_ID IN ('1027') AND DATE(crd.CREATED_STAMP)= ? GROUP BY CAMPAIGN_NAME, CSR_PARTY_NAME, "
					+ "crd.CALL_NUMBER, DATE(crd.CREATED_STAMP), crd.PARTY_ID, CALL_STATUS_ID) temp GROUP BY CAMPAIGN_NAME, CSR_PARTY_NAME, "
					+ "CALL_NUMBER, PARTY_ID,temp.CREATED_STAMP UNION SELECT CAMPAIGN_NAME, PARTY_ID, CSR_PARTY_NAME, CALL_NUMBER, 0 AS CALL_MADE, 0 AS CONTACT_MADE, "
					+ "0 AS EMAIL_SENT, 0 AS VM, 0 AS SM, 0 AS CONCIERGE, 0 AS NO_CONTACT, 0 AS DNC, 0 AS DIRECT_CALL, COUNT(*) AS CALL_FROM_Q, "
					+ "0 AS BAD_NO, 0 AS MAILER, temp.CREATED_STAMP FROM (SELECT mc.CAMPAIGN_NAME AS CAMPAIGN_NAME, crd.PARTY_ID, "
					+ "CONCAT(p.first_name, ' ', p.last_name) AS CSR_PARTY_NAME, crd.CALL_NUMBER, CALL_STATUS_ID AS CALL_FROM_Q, "
					+ "DATE(crd.CREATED_STAMP) AS CREATED_STAMP FROM call_record_details AS crd LEFT JOIN `call_record_master` AS "
					+ "crm ON  crm.`CALL_RECORD_ID`=crd.CALL_RECORD_ID LEFT JOIN `marketing_campaign` AS mc ON "
					+ "crm.`MARKETING_CAMPAIGN_ID` = mc.MARKETING_CAMPAIGN_ID LEFT JOIN `person` p ON crd.`CSR_PARTY_ID` = p.party_id "
					+ "WHERE CALL_STATUS_ID IN ('1031') AND DATE(crd.CREATED_STAMP)= ? GROUP BY CAMPAIGN_NAME, CSR_PARTY_NAME, "
					+ "crd.CALL_NUMBER, DATE(crd.CREATED_STAMP), crd.PARTY_ID, CALL_STATUS_ID) temp GROUP BY CAMPAIGN_NAME, CSR_PARTY_NAME, "
					+ "CALL_NUMBER, PARTY_ID,temp.CREATED_STAMP UNION SELECT CAMPAIGN_NAME, PARTY_ID, CSR_PARTY_NAME, CALL_NUMBER, 0 AS CALL_MADE, 0 AS CONTACT_MADE, "
					+ "0 AS EMAIL_SENT, 0 AS VM, 0 AS SM, 0 AS CONCIERGE, 0 AS NO_CONTACT, 0 AS DNC, 0 AS DIRECT_CALL, 0 AS CALL_FROM_Q, "
					+ "COUNT(*) AS BAD_NO, 0 AS MAILER,temp.CREATED_STAMP FROM (SELECT mc.CAMPAIGN_NAME AS CAMPAIGN_NAME, crd.PARTY_ID, "
					+ "CONCAT(p.first_name, ' ', p.last_name) AS CSR_PARTY_NAME, crd.CALL_NUMBER, CALL_STATUS_ID AS BAD_NO, "
					+ "DATE(crd.CREATED_STAMP) AS CREATED_STAMP FROM call_record_details AS crd LEFT JOIN `call_record_master` "
					+ "AS crm ON  crm.`CALL_RECORD_ID`=crd.CALL_RECORD_ID LEFT JOIN `marketing_campaign` AS mc ON "
					+ "crm.`MARKETING_CAMPAIGN_ID` = mc.MARKETING_CAMPAIGN_ID LEFT JOIN `person` p ON crd.`CSR_PARTY_ID` = p.party_id "
					+ "WHERE CALL_STATUS_ID IN ('1050') AND DATE(crd.CREATED_STAMP)= ? GROUP BY CAMPAIGN_NAME, "
					+ "CSR_PARTY_NAME, crd.CALL_NUMBER, DATE(crd.CREATED_STAMP), crd.PARTY_ID, CALL_STATUS_ID) temp GROUP BY CAMPAIGN_NAME, "
					+ "CSR_PARTY_NAME, CALL_NUMBER, PARTY_ID,temp.CREATED_STAMP UNION SELECT CAMPAIGN_NAME, PARTY_ID, CSR_PARTY_NAME, CALL_NUMBER, 0 AS CALL_MADE, "
					+ "0 AS CONTACT_MADE, 0 AS EMAIL_SENT, 0 AS VM, 0 AS SM, 0 AS CONCIERGE, 0 AS NO_CONTACT, 0 AS DNC, 0 AS DIRECT_CALL, "
					+ "0 AS CALL_FROM_Q, 0 AS BAD_NO, COUNT(*) AS MAILER,temp.CREATED_STAMP FROM (SELECT mc.CAMPAIGN_NAME AS CAMPAIGN_NAME, crd.PARTY_ID, CONCAT(p.first_name, ' ', p.last_name) AS CSR_PARTY_NAME, crd.CALL_NUMBER, CALL_STATUS_ID AS MAILER, "
					+ "DATE(crd.CREATED_STAMP) AS CREATED_STAMP FROM call_record_details AS crd LEFT JOIN `call_record_master` AS crm ON  "
					+ "crm.`CALL_RECORD_ID`=crd.CALL_RECORD_ID LEFT JOIN `marketing_campaign` AS mc ON "
					+ "crm.`MARKETING_CAMPAIGN_ID` = mc.MARKETING_CAMPAIGN_ID LEFT JOIN `person` p ON crd.`CSR_PARTY_ID` = p.party_id "
					+ "WHERE CALL_STATUS_ID IN ('1005', '1014', '1023', '1030') AND DATE(crd.CREATED_STAMP)= ? GROUP BY "
					+ "CAMPAIGN_NAME, crd.CALL_NUMBER, DATE(crd.CREATED_STAMP), PARTY_ID, `CALL_STATUS_ID`) temp GROUP BY CAMPAIGN_NAME, temp.CREATED_STAMP, "
					+ "CSR_PARTY_NAME, CALL_NUMBER, PARTY_ID) AS TT, PERSON P WHERE P.PARTY_ID = TT.PARTY_ID ";
			values.add(createdDate);
			values.add(createdDate);
			values.add(createdDate);
			values.add(createdDate);
			values.add(createdDate);
			values.add(createdDate);
			values.add(createdDate);
			values.add(createdDate);
			values.add(createdDate);
			values.add(createdDate);
			values.add(createdDate);
			values.add(createdDate);
			if(UtilValidate.isNotEmpty(campaignName)) {
				query = query +" AND CAMPAIGN_NAME = ?";
				values.add(campaignName);
			}
			if(UtilValidate.isNotEmpty(csrName)) {
				query = query +" AND CSR_PARTY_NAME = ?";
				values.add(csrName);
			}

			if(UtilValidate.isNotEmpty(callNumber)) {
				query = query +" AND CALL_NUMBER = ?";
				values.add(callNumber);
			}

			query = query +" GROUP BY CAMPAIGN_NAME, CSR_PARTY_NAME";
			query = query +" ORDER BY " + orderBy ;
			rs = QueryUtil.getResultSet(query, values, delegator);
			if (rs != null) {
				while (rs.next()) {
					Map<String, Object> data = new HashMap<String, Object>();
					String  marketingCampaignName = UtilValidate.isNotEmpty(rs.getString("MARKETNG_CAMPAIGN")) ? rs.getString("MARKETNG_CAMPAIGN") : "";
					String csrNameVal = UtilValidate.isNotEmpty(rs.getString("CSR_NAME")) ? rs.getString("CSR_NAME") : "";
					double callNumberVal = rs.getDouble("CALL_NUMBER");
					String callsMade = UtilValidate.isNotEmpty(rs.getString("CALLS_MADE")) ? rs.getString("CALLS_MADE") : "";
					String contactMade = UtilValidate.isNotEmpty(rs.getString("CONTACT_MADE")) ? rs.getString("CONTACT_MADE") : "";
					String emailSent = UtilValidate.isNotEmpty(rs.getString("EMAIL_SENT")) ? rs.getString("EMAIL_SENT") : "";
					String leftVoicemail = UtilValidate.isNotEmpty(rs.getString("LEFT_VOICEMAIL")) ? rs.getString("LEFT_VOICEMAIL") : "";
					String salesMade = UtilValidate.isNotEmpty(rs.getString("SALES_MADE")) ? rs.getString("SALES_MADE") : "";
					String concierge = UtilValidate.isNotEmpty(rs.getString("CONCIERGE")) ? rs.getString("CONCIERGE") : "";
					String noContact = UtilValidate.isNotEmpty(rs.getString("NO_CONTACT")) ? rs.getString("NO_CONTACT") : "";
					String dnc = UtilValidate.isNotEmpty(rs.getString("DNC")) ? rs.getString("DNC") : "";
					String directCall = UtilValidate.isNotEmpty(rs.getString("DIRECT_CALL")) ? rs.getString("DIRECT_CALL") : "";
					String callFromQueue = UtilValidate.isNotEmpty(rs.getString("CALL_FROM_Q")) ? rs.getString("CALL_FROM_Q") : "";
					String badNo = UtilValidate.isNotEmpty(rs.getString("BAD_NO")) ? rs.getString("BAD_NO") : "";
					String mailer = UtilValidate.isNotEmpty(rs.getString("MAILER")) ? rs.getString("MAILER") : "";

					data.put("marketingCampaign", marketingCampaignName);
					data.put("csrName", csrNameVal);
					data.put("callNumber", callNumberVal);
					data.put("callsMade", callsMade);
					data.put("contactMade", contactMade);
					data.put("emailSent", emailSent);
					data.put("leftVoicemail", leftVoicemail);
					data.put("salesMade", salesMade);
					data.put("concierge", concierge);
					data.put("noContact", noContact);
					data.put("dnc", dnc);
					data.put("directCall", directCall);
					data.put("callFromQueue", callFromQueue);
					data.put("badNo", badNo);
					data.put("mailer", mailer);
					dataList.add(data);
				}
			}else {
				result.put("data",  new ArrayList<Map<String, Object>>());
				result.put(ModelService.ERROR_MESSAGE, ModelService.RESPOND_ERROR);
				result.put(ModelService.ERROR_MESSAGE, "Error");
				return doJSONResponse(response, result);
			}
		} catch (Exception e) {
			Debug.logError("Exception in details by day report" + e.getMessage(), MODULE);
			result.put("data", new ArrayList<Map<String, Object>>());
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
			return doJSONResponse(response, result);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				Debug.log("Error in close connection " + e.getMessage());
			}
		}
		result.put("data", dataList);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		result.put(ModelService.SUCCESS_MESSAGE, "Success");
		return doJSONResponse(response, result);
	}
	
	
	public static String searchPosOrder(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException, ParseException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		Map<String, Object> results = new HashMap<String, Object>();
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		String globalDateFormat = org.fio.homeapps.util.DataHelper.getGlobalDateFormat(delegator);
		Map<String, Object> callResult = FastMap.newInstance();
		Locale locale = UtilHttp.getLocale(request);
		List<Map<String, Object>> dataList = FastList.newInstance();
		Map<String, Object> callCtxt = FastMap.newInstance();
		Map<String, Object> requestContext = FastMap.newInstance();
		Map<String, Object> context = UtilHttp.getCombinedMap(request);	
		long start = System.currentTimeMillis();
		
		try {
			String externalLoginKey = (String) context.get("externalLoginKey");
			callCtxt.put("userLogin", userLogin);
			String orderDataType = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "ORDER_DATA_TYPE", "RMS");
			String partyId = (String) context.get("partyId");
			String storeNumber = (String) context.get("storeNumber");
			String registerNumber = (String) context.get("registerNumber");
			String transactionNumber = (String) context.get("transactionNumber");
			String transactionDate_from = (String) context.get("transactionDate_from");
			String transactionDate_to = (String) context.get("transactionDate_to");
			requestContext.put("orderDataType", orderDataType);
			requestContext.put("partyId", partyId);
			requestContext.put("storeNumber", storeNumber);
			requestContext.put("registerNumber", registerNumber);
			requestContext.put("transactionNumber", transactionNumber);
			requestContext.put("transactionDate_from", transactionDate_from);
			requestContext.put("transactionDate_to", transactionDate_to);
			callCtxt.put("requestContext", requestContext);
			String entity = "RmsTransactionMaster";
			String field = "orderId";
			if(orderDataType.equals("ITM")) {
				entity ="InvoiceTransactionMaster";
				field = "invoiceId";
			}
			callResult = dispatcher.runSync("searchPosOrder", callCtxt);
			if (ServiceUtil.isSuccess(callResult) && UtilValidate.isNotEmpty(callResult.get("result"))) {
				@SuppressWarnings("unchecked")
				List<GenericValue> resultList = (List<GenericValue>) callResult.get("result");
				if (resultList != null && resultList.size() > 0) {
					for(GenericValue result : resultList)
					{
						Map<String, Object> data = new HashMap<String, Object>();
						transactionNumber = result.getString("transactionNumber");
						String billToPartyId = result.getString("billToPartyId");
						storeNumber = result.getString("storeNumber");
						registerNumber = result.getString("registerNumber");
						BigDecimal totalSalesAmount = result.getBigDecimal("totalSalesAmount");
						data.put("transactionNumber",UtilValidate.isNotEmpty(transactionNumber)?transactionNumber:"");
						data.put("partyId",UtilValidate.isNotEmpty(billToPartyId)?billToPartyId:"");
						data.put("storeNumber",UtilValidate.isNotEmpty(storeNumber)?storeNumber:"");
						data.put("totalSalesAmount",UtilValidate.isNotEmpty(totalSalesAmount)?totalSalesAmount.toString():"");
						data.put("registerNumber",UtilValidate.isNotEmpty(registerNumber)?registerNumber:"");
						data.put("partyName",PartyHelper.getPartyName(delegator, billToPartyId, false));
						String transactionDate ="";
						if(UtilValidate.isNotEmpty(orderDataType) && orderDataType.equals("ITM")) {
							Timestamp transactionDateTs = result.getTimestamp("transactionDate");
							transactionDate = UtilValidate.isNotEmpty(transactionDateTs)?UtilDateTime.timeStampToString(transactionDateTs, globalDateFormat, TimeZone.getDefault(), locale):"";
						}else {
							Timestamp orderDateTs = result.getTimestamp("orderDate");
							transactionDate = UtilValidate.isNotEmpty(orderDateTs)?UtilDateTime.timeStampToString(orderDateTs, globalDateFormat, TimeZone.getDefault(), locale):"";
						}
						data.put("transactionDate",transactionDate);
						data.put("orderDataType",orderDataType);
						data.put("externalLoginKey",externalLoginKey);

						String orderOrInvoiceId = result.getString(field);
						data.put("orderId",UtilValidate.isNotEmpty(orderOrInvoiceId)?orderOrInvoiceId:"");
						List<GenericValue> distinctItems = EntityQuery.use(delegator).select("sequenceNumber").from(entity).where(EntityCondition.makeCondition(field, EntityOperator.EQUALS, orderOrInvoiceId)).distinct().queryList();
						Long itemCount = Long.valueOf(distinctItems.size());
						data.put("itemCount", itemCount);
						dataList.add(data);
					}
				}
				results.put("viewIndex", callResult.get("viewIndex"));
				results.put("highIndex", callResult.get("highIndex"));
				results.put("lowIndex", callResult.get("lowIndex"));
				results.put("chunks", callResult.get("lowIndex"));
				results.put("totalRecords", callResult.get("totalRecords"));
				results.put("recordCount", callResult.get("recordCount"));
				results.put("chunkSize", callResult.get("viewSize"));
				results.put("responseMessage", "success");
				results.put("data", dataList);
			}else {
				results.put("data", new ArrayList<Map<String, Object>>());
				results.put("responseMessage", "error");
			}
		} catch (GenericServiceException e) {
			e.printStackTrace();
			results.put("errorMessage", e.getMessage());
			results.put("responseMessage", "error");
			results.put("data", new ArrayList<Map<String, Object>>());
		}
		long end = System.currentTimeMillis();
		Debug.logInfo("timeElapsed--->"+(end-start) / 1000f, MODULE);
		results.put("timeTaken", (end-start) / 1000f);
		return doJSONResponse(response, results);
	}

}