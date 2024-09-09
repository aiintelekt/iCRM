package org.groupfio.common.portal.service;

import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.fio.admin.portal.constant.AdminPortalConstant.GlobalParameter;
import org.fio.homeapps.util.SrDataHelper;
import org.groupfio.common.portal.CommonPortalConstants;
import org.groupfio.common.portal.CommonPortalConstants.DomainEntityType;
import org.groupfio.common.portal.extractor.ExtractFacade;
import org.groupfio.common.portal.extractor.constants.ExtractorConstants.ExtractType;
import org.groupfio.common.portal.util.DataHelper;
import org.groupfio.common.portal.util.PartyPrimaryContactMechWorker;
import org.groupfio.common.portal.util.SrUtil;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

/**
 * @author Sharif
 *
 */
public class ServiceRequestServices {

	private static final String MODULE = ServiceRequestServices.class.getName();

	@SuppressWarnings("unchecked")
	public static Map<String, Object> findServiceRequest(DispatchContext dctx, Map context) {

		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = (Delegator) dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Locale locale = (Locale) context.get("locale");

		String globalDateFormat = org.groupfio.common.portal.util.DataHelper.getGlobalDateFormat(delegator);
		String globalDateTimeFormat = org.fio.homeapps.util.DataHelper.getGlobalDateTimeFormat(delegator);

		SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat df2 = new SimpleDateFormat(globalDateFormat);
		NumberFormat nf = NumberFormat.getInstance(locale);

		Map<String, Object> requestContext = (Map<String, Object>) context.get("requestContext");

		String partyId = (String) requestContext.get("partyId");

		String cin = (String) requestContext.get("cin");
		String srNo = (String) requestContext.get("srNo");
		String externalId = (String) requestContext.get("externalId");
		String srIds = (String) requestContext.get("srIds");
		String srSource = (String) requestContext.get("srSource");
		String email = (String) requestContext.get("email");
		String phone = (String) requestContext.get("phone");

		String srArea = UtilValidate.isNotEmpty(requestContext.get("srArea")) ? (String) requestContext.get("srArea") : (String) requestContext.get("srCategoryId");
		String srSubArea = UtilValidate.isNotEmpty(requestContext.get("srSubArea")) ? (String) requestContext.get("srSubArea") : (String) requestContext.get("srSubCategoryId");


		String srSubStatus = (String) requestContext.get("srSubStatus");
		String createdBy = (String) requestContext.get("createdBy");
		String open = (String) requestContext.get("open");
		String closed = (String) requestContext.get("closed");
		String pending = (String) requestContext.get("pending");
		String slaAtRisk = (String) requestContext.get("slaAtRisk");
		String hold = (String) requestContext.get("hold");
		String slaExpired = (String) requestContext.get("slaExpired");
		String unAssigned = (String) requestContext.get("unAssigned");
		String salesPerson = (String) requestContext.get("salesPerson");

		String startDate = (String) requestContext.get("srDateRange_from");
		String endDate = (String) requestContext.get("srDateRange_to");

		String dateRangeType = (String) requestContext.get("dateRangeType");
		String dateRangeFrom = (String) requestContext.get("dateRange_from");
		String dateRangeTo = (String) requestContext.get("dateRange_to");

		String startDueDate = (String) requestContext.get("srDueDate_from");
		String endDueDate = (String) requestContext.get("srDueDate_to");

		String orderId = (String) requestContext.get("orderId");
		String srName = (String) requestContext.get("srName");
		String srPrimaryContactId = (String) requestContext.get("srPrimaryContactId");
		String countryGeoId = (String) requestContext.get("countryGeoId");
		String zipCodeExt = (String) requestContext.get("zipCodeExt");
		String address = (String) requestContext.get("address");
		String customerId = (String) requestContext.get("customerId");

		String primaryEmail = (String) requestContext.get("primaryEmail");
		String primaryPhoneNumber = (String) requestContext.get("primaryPhoneNumber");
		String homePhoneNumber = (String) requestContext.get("homePhoneNumber");
		String offPhoneNumber = (String) requestContext.get("offPhoneNumber");
		String mobileNumber = (String) requestContext.get("mobileNumber");

		String contractorId = (String) requestContext.get("contractorId");
		String contractorEmail = (String) requestContext.get("contractorEmail");
		String contractorOffPhone = (String) requestContext.get("contractorOffPhone");
		String contractorMobilePhone = (String) requestContext.get("contractorMobilePhone");

		String fromDate = (String) requestContext.get("fromDate");
		String thruDate = (String) requestContext.get("thruDate");

		String domainEntityType = (String) requestContext.get("domainEntityType");
		String domainEntityId = (String) requestContext.get("domainEntityId");
		String externalKey = (String) requestContext.get("externalLoginKey");
		String custRequestDomainType = (String) requestContext.get("custRequestDomainType");

		Object owner = requestContext.get("owner");
		Object srStatus = requestContext.get("srStatus");
		Object srType = UtilValidate.isNotEmpty(requestContext.get("srType")) ? requestContext.get("srType") : requestContext.get("srTypeId");
		Object priority = requestContext.get("priority");
		Object stateProvinceGeoId = requestContext.get("stateProvinceGeoId");
		Object city = requestContext.get("city");
		Object county = requestContext.get("countyGeoId");
		Object zipCode = requestContext.get("zipCode");
		Object location = requestContext.get("location");
		Object finishType = requestContext.get("finishType");

		List<String> ownerIds = (List<String>) requestContext.get("ownerIds");
		List<String> emplTeamIds = (List<String>) requestContext.get("emplTeamIds");
		List statusNotInIds = (List) requestContext.get("statusNotInIds");
		Boolean isCSR = (Boolean) requestContext.get("isCSR");

		String customerPo = (String) requestContext.get("customerPo");
		//String location = (String) requestContext.get("location");
		String ticketNumber = (String) requestContext.get("ticketNumber");

		String searchType = (String) requestContext.get("searchType");
		String isPostalCodeRequired = (String) requestContext.get("isPostalCodeRequired");

		String description = (String) requestContext.get("description");
		String resolution = (String) requestContext.get("resolution");

		Object activityOwner = requestContext.get("activityOwner");
		String scheduledStartDate = (String) requestContext.get("scheduledStartDate");
		String scheduledEndDate = (String) requestContext.get("scheduledEndDate");

		String primaryTechnicain = (String) requestContext.get("primaryTechnicain");

		String isFilterByEarliestDueDate = (String) requestContext.get("isFilterByEarliestDueDate");
		Object activityStatus = requestContext.get("activityStatus");

		String dealerRefNo = (String) requestContext.get("dealerRefNo");
		String dealerFilterType = (String) requestContext.get("dealerFilterType");
		String isProgramTemplate = (String) requestContext.get("isProgramTemplate");
		String programTemplateId = (String) requestContext.get("programTemplateId");

		String isSurveyCompleted = (String) requestContext.get("isSurveyCompleted");

		Map<String, Object> result = new HashMap<String, Object>();
		ArrayList<String> statuses = new ArrayList<String>();
		ArrayList<String> activitystatuses = new ArrayList<String>();
		List<GenericValue> resultList = new ArrayList<>();

		try {
			Debug.logImportant("findServiceRequest start: "+UtilDateTime.nowTimestamp(), MODULE);
			String userLoginId = userLogin.getString("userLoginId");
			String isSecurityEnable = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, GlobalParameter.IS_SECURITY_MATRIX_ENABLE,"N");
			GenericValue isAltSrActive = org.fio.homeapps.util.DataUtil.pretailLoyaltyGlobalParameters(delegator,CommonPortalConstants.SR_ALT);

			List<EntityCondition> conditionList = FastList.newInstance();

			//check with ownerId
			if(UtilValidate.isNotEmpty(ownerIds)) {
				conditionList.add(EntityCondition.makeCondition("responsiblePerson", EntityOperator.IN, ownerIds));
			}

			//check with emplTeamId
			if(UtilValidate.isNotEmpty(emplTeamIds)) {
				conditionList.add(EntityCondition.makeCondition("emplTeamId", EntityOperator.IN, emplTeamIds));
			}

			if (UtilValidate.isNotEmpty(partyId) && UtilValidate.isEmpty(dealerFilterType)) {
				conditionList.add(EntityCondition.makeCondition("fromPartyId", EntityOperator.EQUALS, partyId));
			}

			if(UtilValidate.isNotEmpty(startDate)) {
				startDate = df1.format(df2.parse(startDate));
				conditionList.add(EntityCondition.makeCondition("createdDate",EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(Timestamp.valueOf(startDate))));
			}
			if (UtilValidate.isNotEmpty(endDate)) {
				endDate = df1.format(df2.parse(endDate));
				conditionList.add(EntityCondition.makeCondition("createdDate",EntityOperator.LESS_THAN_EQUAL_TO,UtilDateTime.getDayEnd(Timestamp.valueOf(endDate))));
			}

			// date range filter [start]

			String dateRangeFieldId = "createdDate";
			if (UtilValidate.isNotEmpty(dateRangeType)) {
				if (dateRangeType.equals("DUE")) {
					dateRangeFieldId = "commitDate";
				} else if (dateRangeType.equals("CLOSE")) {
					dateRangeFieldId = "closedByDate";
				} else if (dateRangeType.equals("SCHEDULED")) {
					dateRangeFieldId = "estimatedStartDate";
				}
			}
			boolean isSearchTypeActivityOrSR = (UtilValidate.isNotEmpty(searchType) && ((searchType.equals("ACTIVITY")) ||
                    (searchType.equals("SR") && UtilValidate.isNotEmpty(dateRangeType) && dateRangeType.equals("SCHEDULED") && 
                    (UtilValidate.isNotEmpty(dateRangeFrom) || UtilValidate.isNotEmpty(dateRangeTo)))));

			if(UtilValidate.isNotEmpty(dateRangeFrom)) {
				dateRangeFrom = df1.format(df2.parse(dateRangeFrom));
				conditionList.add(EntityCondition.makeCondition(dateRangeFieldId,EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(Timestamp.valueOf(dateRangeFrom))));
			}
			if (UtilValidate.isNotEmpty(dateRangeTo)) {
				dateRangeTo = df1.format(df2.parse(dateRangeTo));
				conditionList.add(EntityCondition.makeCondition(dateRangeFieldId,EntityOperator.LESS_THAN_EQUAL_TO,UtilDateTime.getDayEnd(Timestamp.valueOf(dateRangeTo))));
			}

			// date range filter [end]

			if(UtilValidate.isNotEmpty(startDueDate)) {
				startDueDate = df1.format(df2.parse(startDueDate));
				conditionList.add(EntityCondition.makeCondition("commitDate",EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(Timestamp.valueOf(startDueDate))));
			}
			if (UtilValidate.isNotEmpty(endDueDate)) {
				endDueDate = df1.format(df2.parse(endDueDate));
				conditionList.add(EntityCondition.makeCondition("commitDate",EntityOperator.LESS_THAN_EQUAL_TO,UtilDateTime.getDayEnd(Timestamp.valueOf(endDueDate))));
			}

			if(UtilValidate.isNotEmpty(fromDate)) {
				fromDate = df1.format(df2.parse(fromDate));
				conditionList.add(EntityCondition.makeCondition("actualStartDate",EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(Timestamp.valueOf(fromDate))));
			}
			if (UtilValidate.isNotEmpty(thruDate)) {
				thruDate = df1.format(df2.parse(thruDate));
				conditionList.add(EntityCondition.makeCondition("actualEndDate",EntityOperator.LESS_THAN_EQUAL_TO,UtilDateTime.getDayEnd(Timestamp.valueOf(thruDate))));
			}
			if (UtilValidate.isNotEmpty(srSource)) {
				conditionList.add(EntityCondition.makeCondition("custReqSrSource", EntityOperator.LIKE,""+srSource + "%"));
			}
			if (UtilValidate.isNotEmpty(srNo)) {
				conditionList.add(EntityCondition.makeCondition("custRequestId", EntityOperator.LIKE,""+srNo + "%"));
			}
			if (UtilValidate.isNotEmpty(externalId)) {
				conditionList.add(EntityCondition.makeCondition("externalId", EntityOperator.LIKE,""+externalId + "%"));
			}

			if (UtilValidate.isNotEmpty(srIds)) {
				conditionList.add(EntityCondition.makeCondition("custRequestId", EntityOperator.IN, Arrays.asList(srIds.split(","))));
			}
			if (UtilValidate.isNotEmpty(customerPo)) {
				List rmsCondList = FastList.newInstance();
				rmsCondList.add(EntityCondition.makeCondition("purchaseOrder",EntityOperator.EQUALS,customerPo));
				List<GenericValue> rmsorders = delegator.findList("RmsTransactionMaster", EntityCondition.makeCondition(rmsCondList,EntityOperator.AND), UtilMisc.toSet("orderId"), null, null, false); 
				List orderIds = EntityUtil.getFieldListFromEntityList(rmsorders, "orderId", true);
				rmsCondList.clear();
				List domainEntityIds = FastList.newInstance();
				if (UtilValidate.isNotEmpty(orderIds)) {
					rmsCondList.add(EntityCondition.makeCondition("orderId",EntityOperator.IN,orderIds));
					List<GenericValue> orderAssoc = delegator.findList("EntityOrderLineAssoc", EntityCondition.makeCondition(rmsCondList,EntityOperator.AND), UtilMisc.toSet("domainEntityId"), null, null, false); 
					domainEntityIds = EntityUtil.getFieldListFromEntityList(orderAssoc, "domainEntityId", true);
				}
				conditionList.add(EntityCondition.makeCondition("custRequestId", EntityOperator.IN,domainEntityIds));
			}
			if (UtilValidate.isNotEmpty(srType)) {
				if (!(srType instanceof List)) srType = UtilMisc.toList(""+srType);
				conditionList.add(EntityCondition.makeCondition("custRequestTypeId", EntityOperator.IN, srType));
			}
			if (UtilValidate.isNotEmpty(srArea)) {
				conditionList.add(EntityCondition.makeCondition("custRequestCategoryId", EntityOperator.EQUALS, srArea));
			}
			String srValue="";
			if(UtilValidate.isNotEmpty(requestContext.get("srType"))){
				srValue= (String)requestContext.get("srType");
			}else{
				srValue= (String)requestContext.get("srTypeId");
			}
			if(UtilValidate.isNotEmpty(srValue) && UtilValidate.isNotEmpty(srArea)){
				String deliveryOrExpressSRType =org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SR_TYPE_TOTAL_AMOUNT");
				String orderPlacedSRCategory =org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SR_CATEGORY_TOTAL_AMOUNT");
				if(UtilValidate.isNotEmpty(orderPlacedSRCategory)){
					List<String> srCategoryList =UtilMisc.toList(orderPlacedSRCategory);
					if(orderPlacedSRCategory.contains(",")){
						srCategoryList =org.fio.admin.portal.util.DataUtil.stringToList(orderPlacedSRCategory, ",");
					}
					if(UtilValidate.isNotEmpty(deliveryOrExpressSRType)){
						List<String> srTypeList =UtilMisc.toList(deliveryOrExpressSRType);
						if(deliveryOrExpressSRType.contains(",")){
							srTypeList =org.fio.admin.portal.util.DataUtil.stringToList(deliveryOrExpressSRType, ",");
						}
						if(srTypeList.contains(srValue)){
							if(srCategoryList.contains(srArea)){
								result.put("typeAndCategory", "Y");
							}else{
								result.put("typeAndCategory", "N");
							}
						}
					}
				}
			}
			if (UtilValidate.isNotEmpty(srSubArea)) {
				conditionList.add(EntityCondition.makeCondition("custRequestSubCategoryId", EntityOperator.EQUALS, srSubArea));
			}

			if (UtilValidate.isNotEmpty(owner)) {
				if (!(owner instanceof List)) owner = UtilMisc.toList(""+owner);
				conditionList.add(EntityCondition.makeCondition("responsiblePerson", EntityOperator.IN, owner));
			}

			if (UtilValidate.isNotEmpty(createdBy)) {
				conditionList.add(EntityCondition.makeCondition("createdByUserLogin", EntityOperator.LIKE,""+createdBy + "%"));
			}
			if (UtilValidate.isNotEmpty(priority)) {
				List priorityList = new ArrayList<>();
				if (!(priority instanceof List)) {
					priorityList = UtilMisc.toList(String.valueOf(""+priority));
				} else {
					for (Object p : ((List) priority)) {
						priorityList.add(String.valueOf(""+p));
					}
				}
				conditionList.add(EntityCondition.makeCondition("priority", EntityOperator.IN, priorityList));
			}

			if (UtilValidate.isNotEmpty(srName)) {
				conditionList.add(EntityCondition.makeCondition("custRequestName", EntityOperator.LIKE,"%"+srName + "%"));
			}
			if (UtilValidate.isNotEmpty(description)) {
				conditionList.add(EntityCondition.makeCondition("descriptionRawTxt", EntityOperator.LIKE,"%"+description + "%"));
			}
			if (UtilValidate.isNotEmpty(resolution)) {
				conditionList.add(EntityCondition.makeCondition("resolutionRawTxt", EntityOperator.LIKE,"%"+resolution + "%"));
			}

			if (UtilValidate.isNotEmpty(orderId)) {
				conditionList.add(EntityCondition.makeCondition("custOrderId", EntityOperator.EQUALS, orderId));
			}
			if (UtilValidate.isNotEmpty(ticketNumber)) {
				conditionList.add(EntityCondition.makeCondition("purchaseOrder", EntityOperator.LIKE,"%"+ticketNumber + "%"));
			}

			/*
			if (UtilValidate.isNotEmpty(srPrimaryContactId)) {
				conditionlist.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, srPrimaryContactId));
			}
			 */
			if (UtilValidate.isNotEmpty(countryGeoId)) {
				conditionList.add(EntityCondition.makeCondition("pstlCountryGeoId", EntityOperator.EQUALS, countryGeoId));
			}
			if (UtilValidate.isNotEmpty(stateProvinceGeoId)) {
				if (!(stateProvinceGeoId instanceof List)) stateProvinceGeoId = UtilMisc.toList(""+stateProvinceGeoId);
				conditionList.add(EntityCondition.makeCondition("pstlStateProvinceGeoId", EntityOperator.IN, stateProvinceGeoId));
			}
			if (UtilValidate.isNotEmpty(zipCode)) {
				if (!(zipCode instanceof List)) zipCode = UtilMisc.toList(""+zipCode);
				conditionList.add(EntityCondition.makeCondition("pstlPostalCode", EntityOperator.IN, zipCode));
			}
			if (UtilValidate.isNotEmpty(zipCodeExt)) {
				conditionList.add(EntityCondition.makeCondition("pstlPostalCodeExt", EntityOperator.EQUALS, zipCodeExt));
			}
			if (UtilValidate.isNotEmpty(city)) {
				if (!(city instanceof List)) city = UtilMisc.toList(""+city);
				conditionList.add(EntityCondition.makeCondition("pstlPostalCity", EntityOperator.IN, city));
			}
			if (UtilValidate.isNotEmpty(county)) {
				if (!(county instanceof List)) county = UtilMisc.toList(""+county);
				conditionList.add(EntityCondition.makeCondition("pstlCountyGeoId", EntityOperator.IN, county));
			}
			if (UtilValidate.isNotEmpty(address)) {
				conditionList.add(EntityCondition.makeCondition(UtilMisc.toList(                       
						EntityCondition.makeCondition("pstlAddress1", EntityOperator.LIKE,"%"+address + "%"),
						EntityCondition.makeCondition("pstlAddress2", EntityOperator.LIKE,"%"+address + "%")), EntityOperator.OR));
			}

			if (UtilValidate.isNotEmpty(pending)) {
				statuses.add(CommonPortalConstants.srOpenStatuses.SR_PENDING);
			}
			if (UtilValidate.isNotEmpty(hold)) {
				statuses.add("SR_HOLD");
			}

			if(UtilValidate.isNotEmpty(searchType) && (searchType.equals("SR") && UtilValidate.isNotEmpty(dateRangeType) && dateRangeType.equals("SCHEDULED") && 
                    (UtilValidate.isNotEmpty(dateRangeFrom) || UtilValidate.isNotEmpty(dateRangeTo)))) {
				activitystatuses.add("IA_MSCHEDULED");
				conditionList.add(EntityCondition.makeCondition("currentStatusId", EntityOperator.IN, activitystatuses));
				statuses.clear();
				statuses.add(CommonPortalConstants.srOpenStatuses.SR_ASSIGNED);
			}else if (UtilValidate.isNotEmpty(open) && UtilValidate.isEmpty(closed)) {
				statuses.add(CommonPortalConstants.srOpenStatuses.SR_ASSIGNED);
				statuses.add(CommonPortalConstants.srOpenStatuses.SR_OPEN);
				statuses.add(CommonPortalConstants.srOpenStatuses.SR_IN_PROGRESS);
				statuses.add(CommonPortalConstants.srOpenStatuses.SR_PENDING);
				statuses.add(CommonPortalConstants.srOpenStatuses.SR_FEED_PROVIDED);
			} else if (UtilValidate.isNotEmpty(open) && UtilValidate.isNotEmpty(closed)) {
				statuses.add(CommonPortalConstants.srOpenStatuses.SR_ASSIGNED);
				statuses.add(CommonPortalConstants.srOpenStatuses.SR_OPEN);
				statuses.add(CommonPortalConstants.srOpenStatuses.SR_IN_PROGRESS);
				statuses.add(CommonPortalConstants.srOpenStatuses.SR_FEED_PROVIDED);

				statuses.add(CommonPortalConstants.srClosedStatuses.SR_CLOSED);
				statuses.add(CommonPortalConstants.srClosedStatuses.SR_CANCELLED);
				statuses.add("SR_WRK_COMPL");
			} else {
				if (UtilValidate.isNotEmpty(open)) {
					conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN, UtilMisc.toList("SR_CLOSED","SR_CANCELLED","SR_WRK_COMPL","SR_PENDING")));
				}
				if (UtilValidate.isNotEmpty(closed)) {
					statuses.add(CommonPortalConstants.srClosedStatuses.SR_CLOSED);
					statuses.add(CommonPortalConstants.srClosedStatuses.SR_CANCELLED);
					statuses.add("SR_WRK_COMPL");
				}
			}

			if (UtilValidate.isNotEmpty(srStatus)) {
				if (srStatus instanceof List) statuses.addAll((List)srStatus);
				else statuses.add(""+srStatus);
			}

			if(UtilValidate.isNotEmpty(unAssigned) || UtilValidate.isNotEmpty(searchType) && searchType.equals(CommonPortalConstants.SrSearchType.UN_ASSIGNED_SRS)){

				conditionList.add(EntityCondition.makeCondition(UtilMisc.toList(                       
						EntityCondition.makeCondition("responsiblePerson", EntityOperator.EQUALS, null),
						EntityCondition.makeCondition("responsiblePerson", EntityOperator.EQUALS, "")), EntityOperator.OR));

			}
			if (UtilValidate.isNotEmpty(searchType) && searchType.equals(CommonPortalConstants.SrSearchType.MY_SRS)) {
				conditionList.add(EntityCondition.makeCondition("responsiblePerson", EntityOperator.EQUALS, userLoginId));
			}
			if (UtilValidate.isNotEmpty(searchType) && searchType.equals(CommonPortalConstants.SrSearchType.MY_OPEN_SRS)) {
				conditionList.add(EntityCondition.makeCondition("responsiblePerson", EntityOperator.EQUALS, userLoginId));
				statuses.add(CommonPortalConstants.srOpenStatuses.SR_ASSIGNED);
				statuses.add(CommonPortalConstants.srOpenStatuses.SR_OPEN);
				statuses.add(CommonPortalConstants.srOpenStatuses.SR_IN_PROGRESS);
				statuses.add(CommonPortalConstants.srOpenStatuses.SR_PENDING);
			}
			if (UtilValidate.isNotEmpty(searchType) && searchType.equals(CommonPortalConstants.SrSearchType.MY_CLOSED_SRS)) {
				conditionList.add(EntityCondition.makeCondition("responsiblePerson", EntityOperator.EQUALS, userLoginId));
				statuses.add(CommonPortalConstants.srClosedStatuses.SR_CLOSED);
				statuses.add(CommonPortalConstants.srClosedStatuses.SR_CANCELLED);
			}

			List<EntityCondition> flagConditions = new ArrayList<EntityCondition>();
			if(UtilValidate.isNotEmpty(slaAtRisk) && "Y".equalsIgnoreCase(slaAtRisk)) {
				Timestamp now = UtilDateTime.nowTimestamp();
				flagConditions.add(EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("preEscalationDate", EntityOperator.LESS_THAN, now),
						EntityCondition.makeCondition("commitDate", EntityOperator.GREATER_THAN, now),
						EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN, UtilMisc.toList(CommonPortalConstants.srClosedStatuses.SR_CLOSED,CommonPortalConstants.srClosedStatuses.SR_CANCELLED))
						));
			}
			if(UtilValidate.isNotEmpty(slaExpired) && "Y".equalsIgnoreCase(slaExpired)) {
				Timestamp now = UtilDateTime.nowTimestamp();
				flagConditions.add(EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("commitDate", EntityOperator.LESS_THAN, now),
						EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN, UtilMisc.toList(CommonPortalConstants.srClosedStatuses.SR_CLOSED,CommonPortalConstants.srClosedStatuses.SR_CANCELLED))
						));
			}

			if (UtilValidate.isNotEmpty(statuses)) {
				flagConditions.add(EntityCondition.makeCondition("statusId", EntityOperator.IN, statuses));
			}
			if (UtilValidate.isNotEmpty(statusNotInIds)) {
				conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN, statusNotInIds));
			}

			if(UtilValidate.isNotEmpty(flagConditions)) {
				EntityCondition conditions = EntityCondition.makeCondition(flagConditions,EntityOperator.OR);
				conditionList.add(conditions);
			}

			if (UtilValidate.isNotEmpty(isPostalCodeRequired) && isPostalCodeRequired.equals("Y")) {
				conditionList.add(EntityCondition.makeCondition("pstlPostalCode", EntityOperator.NOT_EQUAL, null));
			}

			if (UtilValidate.isNotEmpty(homePhoneNumber)) {
				conditionList.add(EntityCondition.makeCondition("homePhoneNumber", EntityOperator.LIKE,"%"+homePhoneNumber + "%"));
			}
			if (UtilValidate.isNotEmpty(offPhoneNumber)) {
				conditionList.add(EntityCondition.makeCondition("offPhoneNumber", EntityOperator.LIKE,"%"+offPhoneNumber + "%"));
			}
			if (UtilValidate.isNotEmpty(mobileNumber)) {
				conditionList.add(EntityCondition.makeCondition("mobileNumber", EntityOperator.LIKE,"%"+mobileNumber + "%"));
			}
			if (UtilValidate.isNotEmpty(contractorOffPhone)) {
				conditionList.add(EntityCondition.makeCondition("contractorOffPhone", EntityOperator.LIKE,"%"+contractorOffPhone + "%"));
			}
			if (UtilValidate.isNotEmpty(contractorMobilePhone)) {
				conditionList.add(EntityCondition.makeCondition("contractorMobilePhone", EntityOperator.LIKE,"%"+contractorMobilePhone + "%"));
			}
			if (UtilValidate.isNotEmpty(phone)) {
				conditionList.add(EntityCondition.makeCondition(EntityOperator.OR,
						EntityCondition.makeCondition("homePhoneNumber", EntityOperator.LIKE,"%"+phone + "%"),
						EntityCondition.makeCondition("offPhoneNumber", EntityOperator.LIKE,"%"+phone + "%"),
						EntityCondition.makeCondition("mobileNumber", EntityOperator.LIKE,"%"+phone + "%"),
						EntityCondition.makeCondition("contractorOffPhone", EntityOperator.LIKE,"%"+phone + "%"),
						EntityCondition.makeCondition("contractorMobilePhone", EntityOperator.LIKE,"%"+phone + "%"),
						EntityCondition.makeCondition("contractorHomePhone", EntityOperator.LIKE,"%"+phone + "%")			
						));
			}

			if (UtilValidate.isNotEmpty(contractorEmail)) {
				conditionList.add(EntityCondition.makeCondition("contractorEmail", EntityOperator.LIKE,"%"+contractorEmail + "%"));
			}
			if (UtilValidate.isNotEmpty(email)) {
				conditionList.add(EntityCondition.makeCondition(EntityOperator.OR,
						EntityCondition.makeCondition("contractorEmail", EntityOperator.LIKE,"%"+email + "%"),
						EntityCondition.makeCondition("emailAddress", EntityOperator.LIKE,"%"+email + "%")
						));
			}

			if (UtilValidate.isNotEmpty(domainEntityType)) {
				conditionList.add(EntityCondition.makeCondition("domainEntityType", EntityOperator.EQUALS, domainEntityType));
			}
			if (UtilValidate.isNotEmpty(custRequestDomainType)) {
				conditionList.add(EntityCondition.makeCondition("custRequestDomainType", EntityOperator.EQUALS, custRequestDomainType));
			}

			//if (UtilValidate.isNotEmpty(searchType) && searchType.equals("ATTRIBUTE")) {
			/*if (UtilValidate.isNotEmpty(customerPo)) {
				String customFieldId = EntityUtilProperties.getPropertyValue("sr-portal.properties", "customerPo.customFieldId", delegator);
				conditionList.add(EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("attrName1", EntityOperator.EQUALS, customFieldId),
						EntityCondition.makeCondition("attrValue1", EntityOperator.LIKE,"%"+customerPo + "%")
						));
			}*/
			if (UtilValidate.isNotEmpty(location)) {
				List<String> locationList = new ArrayList<String>();
				if (UtilValidate.isNotEmpty(location)) {
					if (location instanceof List) locationList.addAll((List<String>)location);
					else locationList.add(""+location);
				}
				String customFieldId = (String) requestContext.get("locationCustomFieldId");
				if (UtilValidate.isEmpty(customFieldId)) {
					customFieldId = EntityUtilProperties.getPropertyValue("sr-portal.properties", "location.customFieldId", delegator);
				}
				conditionList.add(EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, customFieldId),
						EntityCondition.makeCondition("attrValue", EntityOperator.IN, locationList)
						));
			}

			if (UtilValidate.isNotEmpty(finishType)) {
				List<String> finishTypeList = new ArrayList<String>();
				if (finishType instanceof List) finishTypeList.addAll((List<String>)finishType);
				else finishTypeList.add(""+finishType);
				conditionList.add(EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "FSR_FINISH_TYPE"),
						EntityCondition.makeCondition("attrValue", EntityOperator.IN, finishTypeList)
						));
			}

			//}

			// Activity related search [start]
			if (UtilValidate.isNotEmpty(searchType) && searchType.equals("ACTIVITY")) {
				if (UtilValidate.isNotEmpty(activityOwner)) {
					if (!(activityOwner instanceof List)) activityOwner = UtilMisc.toList(""+activityOwner);
					conditionList.add(EntityCondition.makeCondition(EntityOperator.OR,
							EntityCondition.makeCondition("primOwnerId", EntityOperator.IN, activityOwner),
							EntityCondition.makeCondition("ownerId", EntityOperator.IN, activityOwner)));
				} 
				if(UtilValidate.isNotEmpty(scheduledStartDate)) {
					scheduledStartDate = df1.format(df2.parse(scheduledStartDate));
					conditionList.add(EntityCondition.makeCondition("estimatedStartDate",EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(Timestamp.valueOf(scheduledStartDate))));
				}
				if (UtilValidate.isNotEmpty(scheduledEndDate)) {
					scheduledEndDate = df1.format(df2.parse(scheduledEndDate));
					conditionList.add(EntityCondition.makeCondition("estimatedCompletionDate",EntityOperator.LESS_THAN_EQUAL_TO,UtilDateTime.getDayEnd(Timestamp.valueOf(scheduledEndDate))));
				}


				if (UtilValidate.isNotEmpty(activityStatus)) {
					if (!(activityStatus instanceof List)) activityStatus = UtilMisc.toList(""+activityStatus);
					conditionList.add(EntityCondition.makeCondition(EntityOperator.AND,
							EntityCondition.makeCondition("currentStatusId", EntityOperator.IN, activityStatus),
							EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr("ownerFromDate", "ownerThruDate"))));
				}
			}
			// Activity related search [end]

			Set<String> fieldsToSelect = new LinkedHashSet<String>();

			fieldsToSelect.add("custRequestId");fieldsToSelect.add("custRequestName");fieldsToSelect.add("fromPartyId");fieldsToSelect.add("statusId");fieldsToSelect.add("custRequestTypeId");
			fieldsToSelect.add("custRequestCategoryId");fieldsToSelect.add("custRequestSubCategoryId");fieldsToSelect.add("createdDate");fieldsToSelect.add("responsiblePerson");fieldsToSelect.add("emplTeamId");fieldsToSelect.add("openDateTime");
			fieldsToSelect.add("priority");fieldsToSelect.add("ownerBu");fieldsToSelect.add("custOrderId");fieldsToSelect.add("custReqSrSource");fieldsToSelect.add("custReqOnceDone");
			fieldsToSelect.add("externalId");fieldsToSelect.add("createdByUserLogin");fieldsToSelect.add("lastModifiedDate");fieldsToSelect.add("lastModifiedByUserLogin");
			fieldsToSelect.add("closedByDate");fieldsToSelect.add("closedByUserLogin");fieldsToSelect.add("commitDate");fieldsToSelect.add("preEscalationDate");fieldsToSelect.add("purchaseOrder");

			fieldsToSelect.add("homePhoneNumber");fieldsToSelect.add("offPhoneNumber");fieldsToSelect.add("mobileNumber");
			fieldsToSelect.add("contractorEmail");fieldsToSelect.add("contractorHomePhone");fieldsToSelect.add("contractorOffPhone");fieldsToSelect.add("contractorMobilePhone");
			fieldsToSelect.add("custReqDocumentNum");fieldsToSelect.add("domainEntityType");fieldsToSelect.add("actualStartDate");fieldsToSelect.add("actualEndDate");
			fieldsToSelect.add("pstlPostalCity");fieldsToSelect.add("pstlStateProvinceGeoId");fieldsToSelect.add("pstlCountryGeoId");fieldsToSelect.add("pstlPostalCode");fieldsToSelect.add("pstlAddress1");fieldsToSelect.add("pstlAddress2");

			EntityFindOptions efo = new EntityFindOptions();
			efo.setOffset(0);
			efo.setLimit(1000);

			DynamicViewEntity dynamicView = new DynamicViewEntity();

			if (isSearchTypeActivityOrSR) {
				dynamicView.addMemberEntity("CRWE", "CustRequestWorkEffort");
				dynamicView.addAlias("CRWE", "custRequestId");
			}

			dynamicView.addMemberEntity("CR", "CustRequest");
			dynamicView.addAlias("CR", "custRequestId", null, null, null, true, null);
			//dynamicView.addAlias("CR", "custRequestId");
			dynamicView.addAlias("CR", "custRequestName");
			dynamicView.addAlias("CR", "fromPartyId");
			dynamicView.addAlias("CR", "statusId");
			dynamicView.addAlias("CR", "custRequestTypeId");
			dynamicView.addAlias("CR", "custRequestCategoryId");
			dynamicView.addAlias("CR", "custRequestSubCategoryId");
			dynamicView.addAlias("CR", "openDateTime");
			dynamicView.addAlias("CR", "createdDate");
			dynamicView.addAlias("CR", "responsiblePerson");
			dynamicView.addAlias("CR", "emplTeamId");
			dynamicView.addAlias("CR", "priority");
			dynamicView.addAlias("CR", "ownerBu");
			dynamicView.addAlias("CR", "custOrderId");
			dynamicView.addAlias("CR", "custReqSrSource");
			dynamicView.addAlias("CR", "custReqOnceDone");
			dynamicView.addAlias("CR", "externalId");
			dynamicView.addAlias("CR", "createdByUserLogin");
			dynamicView.addAlias("CR", "lastModifiedDate");
			dynamicView.addAlias("CR", "lastModifiedByUserLogin");
			dynamicView.addAlias("CR", "closedByDate");
			dynamicView.addAlias("CR", "closedByUserLogin");
			dynamicView.addAlias("CR", "createdStamp");
			dynamicView.addAlias("CR", "lastUpdatedTxStamp");
			dynamicView.addAlias("CR", "emailAddress");
			dynamicView.addAlias("CR", "custReqDocumentNum");
			dynamicView.addAlias("CR", "actualStartDate");
			dynamicView.addAlias("CR", "actualEndDate");
			dynamicView.addAlias("CR", "custRequestDomainType");

			if (isSearchTypeActivityOrSR) {
				dynamicView.addViewLink("CRWE", "CR", Boolean.TRUE, ModelKeyMap.makeKeyMapList("custRequestId"));
			}

			if(UtilValidate.isNotEmpty(isAltSrActive) && UtilValidate.isNotEmpty(isAltSrActive.getString("isActive")) &&
					isAltSrActive.getString("isActive").equalsIgnoreCase("Y") && UtilValidate.isNotEmpty(isAltSrActive.getString("parameterId"))){

				List conditions = FastList.newInstance();
				List<String> custRequestIdVal =null;
				conditions.add(EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, isAltSrActive.getString("parameterId")));
				conditions.add(EntityCondition.makeCondition("attrValue", EntityOperator.NOT_EQUAL, null));
				EntityCondition mainConditons1 = EntityCondition.makeCondition(conditions,EntityOperator.AND);

				List<GenericValue> custRequestListList1 = delegator.findList("CustRequestAttribute", mainConditons1, UtilMisc.toSet("custRequestId"), null, null, false);
				if (UtilValidate.isNotEmpty(custRequestListList1)) {
					custRequestIdVal = EntityUtil.getFieldListFromEntityList(custRequestListList1,"custRequestId", true);
					if (UtilValidate.isNotEmpty(srNo)) {
						conditionList.add(EntityCondition.makeCondition(EntityOperator.AND,
								EntityCondition.makeCondition("custRequestId", EntityOperator.NOT_IN, custRequestIdVal),
								EntityCondition.makeCondition("custRequestId", EntityOperator.LIKE,"%"+srNo + "%")));		
					}else {
						conditionList.add(EntityCondition.makeCondition("custRequestId", EntityOperator.NOT_IN, custRequestIdVal));
					}
				}

			}

			dynamicView.addMemberEntity("CRS", "CustRequestSupplementory");
			dynamicView.addAlias("CRS", "commitDate");
			dynamicView.addAlias("CRS", "preEscalationDate");
			dynamicView.addAlias("CRS", "pstlAddress1");
			dynamicView.addAlias("CRS", "pstlAddress2");
			dynamicView.addAlias("CRS", "pstlPostalCode");
			dynamicView.addAlias("CRS", "pstlPostalCodeExt");
			dynamicView.addAlias("CRS", "pstlPostalCity");
			dynamicView.addAlias("CRS", "pstlStateProvinceGeoId");
			dynamicView.addAlias("CRS", "pstlCountryGeoId");
			dynamicView.addAlias("CRS", "pstlCountyGeoId");
			//dynamicView.addAlias("CRS", "primaryEmail");
			//dynamicView.addAlias("CRS", "primaryPhoneNumber");
			dynamicView.addAlias("CRS", "homePhoneNumber");
			dynamicView.addAlias("CRS", "offPhoneNumber");
			dynamicView.addAlias("CRS", "mobileNumber");
			dynamicView.addAlias("CRS", "contractorEmail");
			dynamicView.addAlias("CRS", "contractorHomePhone");
			dynamicView.addAlias("CRS", "contractorOffPhone");
			dynamicView.addAlias("CRS", "contractorMobilePhone");

			dynamicView.addAlias("CRS", "descriptionRawTxt");
			dynamicView.addAlias("CRS", "resolutionRawTxt");
			dynamicView.addAlias("CRS", "purchaseOrder");
			dynamicView.addAlias("CRS", "domainEntityType");

			dynamicView.addViewLink("CR", "CRS", Boolean.TRUE, ModelKeyMap.makeKeyMapList("custRequestId"));

			//if (UtilValidate.isNotEmpty(searchType) && searchType.equals("ATTRIBUTE")) {
			if (UtilValidate.isNotEmpty(location) || UtilValidate.isNotEmpty(finishType)) {
				dynamicView.addMemberEntity("ATTR", "CustRequestAttribute");
				dynamicView.addAlias("ATTR", "attrName");
				dynamicView.addAlias("ATTR", "attrValue");

				dynamicView.addViewLink("CR", "ATTR", Boolean.TRUE, ModelKeyMap.makeKeyMapList("custRequestId"));
			}
			/*if (UtilValidate.isNotEmpty(customerPo)) {
				dynamicView.addMemberEntity("ATTR1", "CustRequestAttribute");
				dynamicView.addAlias("ATTR1", "attrName1","attrName",null, false, false, null);
				dynamicView.addAlias("ATTR1", "attrValue1","attrValue",null, false, false, null);

				dynamicView.addViewLink("CR", "ATTR1", Boolean.TRUE, ModelKeyMap.makeKeyMapList("custRequestId"));
			}*/
			if(UtilValidate.isNotEmpty(primaryTechnicain)) {
				dynamicView.addMemberEntity("ATTR1", "CustRequestAttribute");
				dynamicView.addAlias("ATTR1", "primAttrName","attrName",null, false, false, null);
				dynamicView.addAlias("ATTR1", "primAttrValue","attrValue",null, false, false, null);

				dynamicView.addViewLink("CR", "ATTR1", Boolean.TRUE, ModelKeyMap.makeKeyMapList("custRequestId"));

				String customFieldId = DataHelper.getCustomFieldId(delegator, "ANCHOR_ROLES", "ANR_TECHNICIAN");
				conditionList.add(EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("primAttrName", EntityOperator.EQUALS, customFieldId),
						EntityCondition.makeCondition("primAttrValue", EntityOperator.EQUALS, primaryTechnicain)
						));
			}

			/*
			dynamicView.addMemberEntity("CRC", "CustRequestContact");
			dynamicView.addAlias("CRC", "partyId");
			dynamicView.addViewLink("CR", "CRC", Boolean.TRUE, ModelKeyMap.makeKeyMapList("custRequestId"));
			 */
			if(UtilValidate.isNotEmpty(srPrimaryContactId)) {
				fieldsToSelect.add("partyId");fieldsToSelect.add("fromDate");fieldsToSelect.add("thruDate");
				fieldsToSelect.add("isPrimary");
				dynamicView.addMemberEntity("CRC", "CustRequestContact");
				dynamicView.addAlias("CRC", "partyId");
				dynamicView.addAlias("CRC", "fromDate");
				dynamicView.addAlias("CRC", "thruDate");
				dynamicView.addAlias("CRC", "isPrimary");

				dynamicView.addViewLink("CR", "CRC", Boolean.TRUE, ModelKeyMap.makeKeyMapList("custRequestId"));

				conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, srPrimaryContactId));
				//conditionlist.add(EntityCondition.makeCondition("isPrimary", EntityOperator.EQUALS, "Y"));
			}
			if("Y".equals(isSurveyCompleted)) {
				dynamicView.addMemberEntity("CRA4", "CustRequestAttribute");
				dynamicView.addAlias("CRA4", "attrName4","attrName",null, false,false,null);
				dynamicView.addAlias("CRA4", "attrValue4","attrValue",null, false,false,null);
				dynamicView.addViewLink("CR", "CRA4", Boolean.TRUE, ModelKeyMap.makeKeyMapList("custRequestId"));

				conditionList.add(EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("attrName4", EntityOperator.EQUALS, "SVY_FSR_ID")));

				conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN, UtilMisc.toList("SR_CLOSED","SR_CANCELLED","SR_WRK_COMPL")));
			}

			if( (UtilValidate.isNotEmpty(isCSR) && !isCSR && "Y".equals(isSecurityEnable))
					|| UtilValidate.isNotEmpty(customerId)
					|| UtilValidate.isNotEmpty(salesPerson)
					|| UtilValidate.isNotEmpty(contractorId)
					) {
				fieldsToSelect.add("crpPartyId");
				fieldsToSelect.add("roleTypeId");
				fieldsToSelect.add("crpThruDate");
				dynamicView.addMemberEntity("CRP", "CustRequestParty");
				dynamicView.addAlias("CRP", "crpPartyId","partyId",null,Boolean.FALSE,Boolean.FALSE,null);
				dynamicView.addAlias("CRP", "roleTypeId");
				dynamicView.addAlias("CRP", "crpFromDate","fromDate",null,Boolean.FALSE,Boolean.FALSE,null);
				dynamicView.addAlias("CRP", "crpThruDate","thruDate",null,Boolean.FALSE,Boolean.FALSE,null);
				dynamicView.addViewLink("CR", "CRP", Boolean.TRUE, ModelKeyMap.makeKeyMapList("custRequestId"));

				List<String> crpPartyIds = new ArrayList<>();
				List<String> roleTypeIds = new ArrayList<>();
				String crpPartyId = null;
				String roleTypeId = null;

				if ( (UtilValidate.isNotEmpty(isCSR) && !isCSR && "Y".equals(isSecurityEnable))
						) {
					if(UtilValidate.isNotEmpty(dealerFilterType) && "MY_COMPANY_REQUEST".equals(dealerFilterType)) {
						crpPartyId = userLogin.getString("partyId");
						List<String> allDealerIds = DataHelper.getAllDealerByContact(delegator, crpPartyId);
						//roleTypeId = org.fio.admin.portal.util.DataUtil.getPartySecurityRole(delegator, crpPartyId);
						if (UtilValidate.isNotEmpty(allDealerIds)) {
							crpPartyIds.addAll(allDealerIds);
							roleTypeIds.add("ACCOUNT");
						}
					} else {
						crpPartyId = userLogin.getString("partyId");
						roleTypeId = org.fio.admin.portal.util.DataUtil.getPartySecurityRole(delegator, crpPartyId);
						if (UtilValidate.isNotEmpty(crpPartyId) && UtilValidate.isNotEmpty(roleTypeId)) {
							crpPartyIds.add(crpPartyId);
							roleTypeIds.add(roleTypeId);
						}
					}

				} 

				if (UtilValidate.isNotEmpty(customerId)) {
					crpPartyId = customerId;
					roleTypeId = "CUSTOMER";
					crpPartyIds.add(crpPartyId);
					roleTypeIds.add(roleTypeId);
				} 
				if (UtilValidate.isNotEmpty(salesPerson)) {
					crpPartyId = salesPerson;
					roleTypeId = "SALES_REP";
					crpPartyIds.add(crpPartyId);
					roleTypeIds.add(roleTypeId);
				}
				if (UtilValidate.isNotEmpty(contractorId)) {
					crpPartyId = contractorId;
					roleTypeId = "CONTRACTOR";
					crpPartyIds.add(crpPartyId);
					roleTypeIds.add(roleTypeId);
				}

				conditionList.add(EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("crpPartyId", EntityOperator.IN, crpPartyIds),
						EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, roleTypeIds),
						EntityCondition.makeCondition("crpThruDate", EntityOperator.EQUALS, null)
						));

			}

			if (isSearchTypeActivityOrSR) {
				dynamicView.addMemberEntity("WE", "WorkEffort");
				dynamicView.addAlias("WE", "workEffortId");
				dynamicView.addAlias("WE", "estimatedStartDate");
				dynamicView.addAlias("WE", "estimatedCompletionDate");
				dynamicView.addAlias("WE", "currentStatusId");
				dynamicView.addAlias("WE", "primOwnerId");
				dynamicView.addViewLink("CRWE", "WE", Boolean.TRUE, ModelKeyMap.makeKeyMapList("workEffortId"));

				dynamicView.addMemberEntity("WEPA", "WorkEffortPartyAssignment");
				dynamicView.addAlias("WEPA", "ownerId");
				dynamicView.addAlias("WEPA", "techPartyId", "partyId", "", null, null, "");
				dynamicView.addAlias("WEPA", "techRoleTypeId", "roleTypeId", "", null, null, "");
				dynamicView.addAlias("WEPA", "ownerFromDate", "fromDate", "", null, null, "");
				dynamicView.addAlias("WEPA", "ownerThruDate", "thruDate", "", null, null, "");
				dynamicView.addViewLink("WE", "WEPA", Boolean.TRUE, ModelKeyMap.makeKeyMapList("workEffortId"));

			}

			if(UtilValidate.isNotEmpty(dealerRefNo)) {
				dynamicView.addMemberEntity("CRA1", "CustRequestAttribute");
				dynamicView.addAlias("CRA1", "attrName1","attrName",null, false,false,null);
				dynamicView.addAlias("CRA1", "attrValue1","attrValue",null, false,false,null);
				dynamicView.addViewLink("CR", "CRA1", Boolean.TRUE, ModelKeyMap.makeKeyMapList("custRequestId"));

				conditionList.add(EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("attrName1", EntityOperator.EQUALS, "DEALER_REF_NO"),
						EntityCondition.makeCondition("attrValue1", EntityOperator.EQUALS, dealerRefNo)));
			}

			if(UtilValidate.isNotEmpty(isProgramTemplate)) {
				dynamicView.addMemberEntity("CRA1", "CustRequestAttribute");
				dynamicView.addAlias("CRA1", "attrName2","attrName",null, false,false,null);
				dynamicView.addAlias("CRA1", "attrValue2","attrValue",null, false,false,null);
				dynamicView.addViewLink("CR", "CRA1", Boolean.TRUE, ModelKeyMap.makeKeyMapList("custRequestId"));

				if (isProgramTemplate.equals("Y")) {
					conditionList.add(EntityCondition.makeCondition(EntityOperator.AND,
							EntityCondition.makeCondition("attrName2", EntityOperator.EQUALS, "IS_PROG_TPL"),
							EntityCondition.makeCondition("attrValue2", EntityOperator.EQUALS, isProgramTemplate)));
				} else {
					conditionList.add(EntityCondition.makeCondition(EntityOperator.AND,
							EntityCondition.makeCondition("attrName2", EntityOperator.EQUALS, "IS_PROG_TPL"),
							EntityCondition.makeCondition(EntityOperator.OR,
									EntityCondition.makeCondition("attrValue2", EntityOperator.EQUALS, isProgramTemplate),
									EntityCondition.makeCondition("attrValue2", EntityOperator.EQUALS, null),
									EntityCondition.makeCondition("attrValue2", EntityOperator.EQUALS, "")
									)
							));
				}

			}
			if(UtilValidate.isNotEmpty(programTemplateId)) {
				dynamicView.addMemberEntity("CRA1", "CustRequestAttribute");
				dynamicView.addAlias("CRA1", "attrName3","attrName",null, false,false,null);
				dynamicView.addAlias("CRA1", "attrValue3","attrValue",null, false,false,null);
				dynamicView.addViewLink("CR", "CRA1", Boolean.TRUE, ModelKeyMap.makeKeyMapList("custRequestId"));

				conditionList.add(EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("attrName3", EntityOperator.EQUALS, "PROG_TPL_ID"),
						EntityCondition.makeCondition("attrValue3", EntityOperator.EQUALS, programTemplateId)));
			}

			EntityCondition condition = null;
			if (UtilValidate.isNotEmpty(domainEntityType)&& domainEntityType.equals(DomainEntityType.CONTACT)) {
				List conditionsList = FastList.newInstance();

				conditionsList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
				conditionsList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "CONTACT"));
				conditionsList.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
				EntityCondition mainConditons = EntityCondition.makeCondition(conditionsList,EntityOperator.AND);

				List<GenericValue> custRequestListList = delegator.findList("CustRequestContact", mainConditons, UtilMisc.toSet("custRequestId"), null, null, false);
				if (UtilValidate.isNotEmpty(custRequestListList)) {
					List<String> custRequestIds = EntityUtil.getFieldListFromEntityList(custRequestListList,"custRequestId", true);							
					conditionList.clear();
					conditionList.add(EntityCondition.makeCondition("custRequestId", EntityOperator.IN, custRequestIds));
					condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				}
			} else {
				condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			}
			Debug.log("findServiceRequest condition======="+condition);

			int viewIndex = 0;
			try {
				viewIndex = Integer.parseInt((String) requestContext.get("VIEW_INDEX"));
			} catch (Exception e) {
				viewIndex = 0;
			}

			int fioGridFetch = UtilValidate.isNotEmpty(requestContext.get("totalGridFetch")) ? Integer.parseInt((String) requestContext.get("totalGridFetch")) : 1000;

			int viewSize = fioGridFetch;
			try {
				viewSize = Integer.parseInt((String) requestContext.get("VIEW_SIZE"));
			} catch (Exception e) {
				viewSize = fioGridFetch;
			}

			String orderBy = "createdStamp DESC";
			if (UtilValidate.isNotEmpty(isFilterByEarliestDueDate)) {
				orderBy = "commitDate ASC";
			}

			int highIndex = 0;
			int lowIndex = 0;
			int resultListSize = 0;
			try {
				// get the indexes for the partial list
				lowIndex = viewIndex * viewSize + 1;
				highIndex = (viewIndex + 1) * viewSize;

				// set distinct on so we only get one row per order
				// using list iterator
				EntityListIterator pli = EntityQuery.use(delegator)
						.select(fieldsToSelect)
						.from(dynamicView)
						.where(condition)
						.orderBy(orderBy)
						.cursorScrollInsensitive()
						.fetchSize(highIndex)
						//.distinct()
						.cache(true)
						.queryIterator();
				// get the partial list for this page
				resultList = pli.getPartialList(lowIndex, viewSize);

				// attempt to get the full size
				resultListSize = pli.getResultsSizeAfterPartialList();
				// close the list iterator
				pli.close();

				result.put("viewIndex", Integer.valueOf(viewIndex));
				result.put("highIndex", Integer.valueOf(highIndex));
				result.put("lowIndex", Integer.valueOf(lowIndex));
				result.put("viewSize", viewSize);
				result.put("resultListSize", resultListSize);

			} catch (GenericEntityException e) {
				String errMsg = "Error: " + e.toString();
				Debug.logError(e, errMsg, MODULE);
			}
			Debug.log("findServiceRequest resultListSize======="+resultListSize);
			Debug.logImportant("findServiceRequest end: "+UtilDateTime.nowTimestamp(), MODULE);
		}catch (Exception e) {
			e.printStackTrace();
			result.put("errorMsg", e.getMessage());
			return result;
		}

		result.put("srList", resultList);
		result.putAll(ServiceUtil.returnSuccess("Successfully find sr"));
		return result;
	}

	public static Map<String, Object> triggerSrStatusEmail(DispatchContext dctx, Map context) {

		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = (Delegator) dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Locale locale = (Locale) context.get("locale");

		SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		SimpleDateFormat df2 = new SimpleDateFormat("MM/dd/yyyy");
		NumberFormat nf = NumberFormat.getInstance(locale);

		Map<String, Object> requestContext = (Map<String, Object>) context.get("requestContext");

		//String partyId = (String) requestContext.get("partyId");

		Map<String, Object> result = new HashMap<String, Object>();

		try {

			List<GenericValue> emailConfigList = EntityQuery.use(delegator).from("CustRequestEmailConfig").orderBy("sequenceId").queryList();
			if (UtilValidate.isNotEmpty(emailConfigList)) {
				Map<String, Object> emailTemplates = SrDataHelper.getSrStatusEmailTemplates(delegator, emailConfigList);

				DynamicViewEntity dynamicView = new DynamicViewEntity();

				dynamicView.addMemberEntity("CR", "CustRequest");
				dynamicView.addAlias("CR", "custRequestId", null, null, null, true, null);
				//dynamicView.addAlias("CR", "custRequestId");
				dynamicView.addAlias("CR", "custRequestName");
				dynamicView.addAlias("CR", "statusId");

				dynamicView.addMemberEntity("CRS", "CustRequestSupplementory");
				dynamicView.addAlias("CRS", "statusEscTime");
				dynamicView.addAlias("CRS", "statusClosedEscTime");

				dynamicView.addViewLink("CR", "CRS", Boolean.TRUE, ModelKeyMap.makeKeyMapList("custRequestId"));

				String orderBy = "statusEscTime DESC";

				Set<String> fieldsToSelect = new LinkedHashSet<String>();

				fieldsToSelect.add("custRequestId");fieldsToSelect.add("custRequestName");fieldsToSelect.add("statusId");
				fieldsToSelect.add("statusEscTime");fieldsToSelect.add("statusClosedEscTime");

				// trigger email for other status [start]

				List<EntityCondition> conditionList = FastList.newInstance();

				conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN, UtilMisc.toList("SR_CLOSED","SR_CANCELLED","SR_WRK_COMPL")));
				conditionList.add(EntityCondition.makeCondition("statusEscTime", EntityOperator.LESS_THAN_EQUAL_TO, org.fio.homeapps.util.UtilDateTime.nowTimestamp()));

				EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				Debug.logInfo("triggerSrStatusEmail condition: "+condition, MODULE);

				EntityListIterator pli = EntityQuery.use(delegator)
						.select(fieldsToSelect)
						.from(dynamicView)
						.where(condition)
						.orderBy(orderBy)
						.cursorScrollInsensitive()
						//.fetchSize(highIndex)
						//.distinct()
						//.cache(true)
						.queryIterator();
				// get the partial list for this page
				List<GenericValue> resultList = pli.getCompleteList();
				Debug.logInfo("triggerSrStatusEmail resultList size: "+resultList.size(), MODULE);
				if (UtilValidate.isNotEmpty(resultList)) {
					List<GenericValue> ecl = emailConfigList.stream().filter(x->!x.getString("statusId").equals("SR_CLOSED")).collect(Collectors.toList());
					SrUtil.triggerSrStatusEmail(delegator, dispatcher, userLogin, resultList, ecl, emailTemplates);
				}

				// trigger email for other status [end]

				// trigger email for closed status [start]

				conditionList = FastList.newInstance();

				conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN, UtilMisc.toList("SR_CLOSED","SR_CANCELLED","SR_WRK_COMPL")));
				conditionList.add(EntityCondition.makeCondition("statusClosedEscTime", EntityOperator.LESS_THAN_EQUAL_TO, org.fio.homeapps.util.UtilDateTime.nowTimestamp()));

				condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				Debug.logInfo("triggerSrStatusEmail condition: "+condition, MODULE);
				pli = EntityQuery.use(delegator)
						.select(fieldsToSelect)
						.from(dynamicView)
						.where(condition)
						.orderBy(orderBy)
						.cursorScrollInsensitive()
						//.fetchSize(highIndex)
						//.distinct()
						//.cache(true)
						.queryIterator();
				// get the partial list for this page
				resultList = pli.getCompleteList();
				Debug.logInfo("triggerSrStatusEmail resultList size: "+resultList.size(), MODULE);
				if (UtilValidate.isNotEmpty(resultList)) {
					List<GenericValue> ecl = emailConfigList.stream().filter(x->x.getString("statusId").equals("SR_CLOSED")).collect(Collectors.toList());
					SrUtil.triggerSrStatusEmail(delegator, dispatcher, userLogin, resultList, ecl, emailTemplates);
				}

				// trigger email for closed status [end]

			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("errorMsg", e.getMessage());
			return result;
		}

		result.putAll(ServiceUtil.returnSuccess("Successfully trigger sr status email"));
		return result;
	}
	
	public static Map<String, Object> triggerSrSMSReceivedEmail(DispatchContext dctx, Map context) {
		Map<String, Object> result = new HashMap<String, Object>();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = (Delegator) dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String custRequestId = (String) context.get("custRequestId");
		String fromEmailId = (String) context.get("fromEmailId");
		try {
			String nsender = "";
			String nto = "";
			String subject = "";
			GenericValue sytemProperty = EntityQuery.use(delegator).from("SystemProperty").where("systemResourceId", "NOTIFICATION", "systemPropertyId", "from").queryOne();
			GenericValue custRequest = EntityQuery.use(delegator).from("CustRequest").where("custRequestId", custRequestId).queryFirst();
			if(UtilValidate.isNotEmpty(custRequest)) {
				String custRequestName = custRequest.getString("custRequestName");
				String ownerPartyId = custRequest.getString("responsiblePerson");
				Debug.log("===== SR Primary tech EMAIL TRIGGER sytemProperty ===="+sytemProperty);

				if(UtilValidate.isNotEmpty(fromEmailId)){
					nsender = fromEmailId;
				}else if(UtilValidate.isNotEmpty(sytemProperty)) {
					nsender = sytemProperty.getString("systemPropertyValue");
				}else {
					result.put("errorMsg", "From email is not given");
					return result;
				}

				Map<String, String> ntoContactInformation = PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator,ownerPartyId);
				nto = ntoContactInformation.get("EmailAddress");

				String srSMSEmailNotify = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SR_SMS_EMAIL_NOTIFIY", "Y");

				if (UtilValidate.isNotEmpty(nsender) && UtilValidate.isNotEmpty(nto) && "Y".equals(srSMSEmailNotify)){

					String templateId = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "SR_SMS_EMAIL_NOTIFIY_TMPLT");

					if(UtilValidate.isNotEmpty(templateId)) {

						GenericValue emailTemlateData = delegator.findOne("TemplateMaster", UtilMisc.toMap("templateId",templateId), false);

						String emailContent = "";
						String templateFormContent = emailTemlateData.getString("templateFormContent");
						if (UtilValidate.isNotEmpty(templateFormContent)) {
							if (org.apache.commons.codec.binary.Base64.isBase64(templateFormContent)) {
								templateFormContent = org.ofbiz.base.util.Base64.base64Decode(templateFormContent);
							}
						}

						if(UtilValidate.isNotEmpty(emailTemlateData.getString("subject"))) {
							subject = "FSR# "+custRequestId+" ("+custRequestName+") - "+emailTemlateData.getString("subject");
						}

						// prepare email content [start]
						Map<String, Object> extractContext = new LinkedHashMap<String, Object>();
						extractContext.put("delegator", delegator);
						extractContext.put("extractType", ExtractType.EXTRACT_EMAIL_DATA);
						extractContext.put("fromEmail", nsender);
						extractContext.put("toEmail", nto);
						extractContext.put("partyId", ownerPartyId);
						extractContext.put("custRequestId", custRequestId);
						extractContext.put("emailContent", templateFormContent);
						extractContext.put("templateId", templateId);

						Map<String, Object> extractResultContext = ExtractFacade.extractData(extractContext);
						emailContent = (String) extractResultContext.get("emailContent");
						// prepare email content [end]

						Map<String, Object> callCtxt = FastMap.newInstance();
						Map<String, Object> callResult = FastMap.newInstance();
						Map<String, Object> requestContext = FastMap.newInstance();

						requestContext.put("nsender", nsender);
						requestContext.put("nto", nto);
						requestContext.put("subject", subject);
						requestContext.put("emailContent", emailContent);
						requestContext.put("templateId", templateId);

						callCtxt.put("requestContext", requestContext);
						callCtxt.put("userLogin", userLogin);

						Debug.log("===== SR Primary tech EMAIL TRIGGER ===="+callCtxt);

						callResult = dispatcher.runSync("common.sendEmail", callCtxt);
						if (ServiceUtil.isError(callResult)) {
							Debug.logError("Email send failed: "+ServiceUtil.getErrorMessage(callResult),MODULE);
							return callResult;
						}

					}
				}
			}
		
		}catch(Exception e) {
			e.printStackTrace();
			result.put("errorMsg", e.getMessage());
			return result;
		}
		result.putAll(ServiceUtil.returnSuccess("Successfully trigger sr sms received email"));
		return result;
	}

}
