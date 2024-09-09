package org.fio.sr.portal.service.impl;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;

import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;
import org.fio.homeapps.constants.GlobalConstants.AccessLevel;
import org.fio.homeapps.util.DataUtil;
import org.fio.homeapps.util.ParamUtil;
import org.fio.homeapps.util.UtilDateTime;
import org.groupfio.common.portal.util.PartyPrimaryContactMechWorker;
import org.groupfio.common.portal.util.DataHelper;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.webapp.stats.VisitHandler;

import javolution.util.FastList;
import javolution.util.FastMap;

public class SRServiceImpl {

	private static final String MODULE = SRServiceImpl.class.getName();
	public static  Map<String,Object> getCustRequestSrSummary(DispatchContext dctx, Map context) {

		Delegator delegator = (Delegator) dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> result = new HashMap<>();
		List < Map<String, Object>> results = new ArrayList<>();
		String userLoginPartyId = userLogin.getString("partyId");
		String externalId = (String) context.get("externalId");
		String start = (String) context.get("start");
		String length = (String) context.get("length");
		String customerName = (String) context.get("customerName");
		String cinNumber = (String) context.get("cinNumber");
		String srTypeId = (String) context.get("srTypeId");
		String srStatus = (String) context.get("srStatusId");
		String buPartyId = (String) context.get("buPartyId");
		String dueDate = (String) context.get("dueDate");
		String emailId = (String) context.get("emailId");
		String ownerUserLoginId = (String) context.get("ownerUserLoginId");
		String srCategoryId = (String) context.get("srCategoryId");
		String srSubStatus = (String) context.get("srSubStatus");
		String phone = (String) context.get("phone");
		String open = (String) context.get("open");
		String closed = (String) context.get("closed");
		String slaAtRisk = (String) context.get("slaAtRisk");
		String slaExpired = (String) context.get("slaExpired");
		String createdBy = (String) context.get("createdBy");
		String searchCriteria=(String) context.get("searchCriteria");
		String srSubCategoryId = (String) context.get("srSubCategoryId");
		String createdOn = (String) context.get("createdOn");
		String MyTeamServiceRequests = (String) context.get("MyTeamServiceRequestStr");
		String systemViewFilter = (String) context.get("systemViewFilter");
		String orderByField = "";

		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy");
		EntityFindOptions efo = new EntityFindOptions();
		efo.setDistinct(true);
		int startInx = UtilValidate.isNotEmpty(start) ? Integer.parseInt(start) : 0;
		int endInx = UtilValidate.isNotEmpty(length) ? Integer.parseInt(length) : 1000;
		efo.setOffset(startInx);
		efo.setLimit(endInx);

		String lastMonthsDateStr = (String) context.get("lastMonthsDate");
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		java.sql.Timestamp lastMonthsDate = null;
		if(UtilValidate.isNotEmpty(lastMonthsDateStr)) {
			try {
				lastMonthsDate = new java.sql.Timestamp(sdf.parse(lastMonthsDateStr).getTime());
			} catch (Exception e) {
				Debug.logError(e, "Cannot parse date string: " +  e.getMessage(), MODULE);
				// fromDate = UtilDateTime.nowTimestamp();
			}
			lastMonthsDate = UtilDateTime.getDayStart(lastMonthsDate);
		}
		String dateStr = "";
		String businessUnit="";
		String srPeriodUnit="";
		String slaPeriodLvl="";
		int nDays=3;
		try {
			List<EntityCondition> conditionDet = FastList.newInstance();
			if(UtilValidate.isNotEmpty(buPartyId)) {
				conditionDet.add(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,buPartyId));
				EntityCondition condition=EntityCondition.makeCondition(conditionDet,EntityOperator.AND);
				List<GenericValue> EmplTeamList = EntityQuery.use(delegator).from("EmplTeam").
						where(condition).orderBy("-createdOn").maxRows(endInx).queryList();
				if(UtilValidate.isNotEmpty(EmplTeamList)) {
					GenericValue emplTeam=EntityUtil.getFirst(EmplTeamList);
					businessUnit=emplTeam.getString("businessUnit");
				}
			}
		} catch (Exception e) {
			Debug.logError(e, "Exception: " + e.getMessage(), MODULE);
			result.put( "_EVENT_MESSAGE",e.getMessage());
			return result;
		}
		try {

			List<EntityCondition> conditions = FastList.newInstance();
			if(UtilValidate.isNotEmpty(externalId)) {
				conditions.add(EntityCondition.makeCondition("custRequestId",EntityOperator.EQUALS,externalId));
			}
			if(UtilValidate.isNotEmpty(customerName)) {
				conditions.add(EntityCondition.makeCondition("customerName",EntityOperator.EQUALS,customerName));
			}
			if(UtilValidate.isNotEmpty(cinNumber)) {
				conditions.add(EntityCondition.makeCondition("cinNumber",EntityOperator.EQUALS,cinNumber));
			}
			if(UtilValidate.isNotEmpty(srTypeId)) {
				conditions.add(EntityCondition.makeCondition("srTypeId",EntityOperator.EQUALS,srTypeId));
			}
			if(UtilValidate.isNotEmpty(srStatus)) {
				conditions.add(EntityCondition.makeCondition("srStatusId",EntityOperator.EQUALS,srStatus));
			}
			if(UtilValidate.isNotEmpty(emailId)) {
				conditions.add(EntityCondition.makeCondition("emailId",EntityOperator.EQUALS,emailId));
			}
			if(UtilValidate.isNotEmpty(srSubStatus)) {
				conditions.add(EntityCondition.makeCondition("srSubStatusId",EntityOperator.EQUALS,srSubStatus));
			}
			if(UtilValidate.isNotEmpty(phone)) {
				conditions.add(EntityCondition.makeCondition("phone",EntityOperator.EQUALS,phone));
			}
			if(UtilValidate.isNotEmpty(createdBy)) {
				conditions.add(EntityCondition.makeCondition("createdByUserLoginId",EntityOperator.EQUALS,createdBy));
			}
			if(UtilValidate.isNotEmpty(srSubCategoryId)) {
				conditions.add(EntityCondition.makeCondition("srSubCategoryId",EntityOperator.LIKE,srSubCategoryId));
			}
			if(UtilValidate.isNotEmpty(createdOn)) {
				createdOn = df1.format(df2.parse(createdOn));
				conditions.add(EntityCondition.makeCondition("createdOn",EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(Timestamp.valueOf(createdOn))));
			}
			if(UtilValidate.isNotEmpty(dueDate)) {
				dueDate = df1.format(df2.parse(dueDate));
				conditions.add(EntityCondition.makeCondition("createdOn",EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(Timestamp.valueOf(dueDate))));
			}
			if(UtilValidate.isNotEmpty(lastMonthsDate) && UtilValidate.isNotEmpty(lastMonthsDateStr)) {
				conditions.add(EntityCondition.makeCondition("createdOn",EntityOperator.LESS_THAN_EQUAL_TO, lastMonthsDate));
			}
			if(UtilValidate.isNotEmpty(srCategoryId)) {
				conditions.add(EntityCondition.makeCondition("srCategoryId",EntityOperator.EQUALS,srCategoryId));
			}
			if(UtilValidate.isNotEmpty(ownerUserLoginId)) {
				conditions.add(EntityCondition.makeCondition("ownerUserLoginId",EntityOperator.LIKE,ownerUserLoginId+"%"));
			}
			if(UtilValidate.isNotEmpty(MyTeamServiceRequests)) {
				conditions.add(EntityCondition.makeCondition("empTeamId",EntityOperator.EQUALS,"MANAGEMENT"));
			}
			if(UtilValidate.isNotEmpty(businessUnit)) {
				conditions.add(EntityCondition.makeCondition("ownerUserLoginId",EntityOperator.EQUALS,businessUnit));
			}
			List<EntityCondition> checkBoxconditions = FastList.newInstance();
			if(UtilValidate.isNotEmpty(open)) {
				checkBoxconditions.add(EntityCondition.makeCondition("srStatusId",EntityOperator.NOT_IN,UtilMisc.toList("SR_CLOSED","SR_CANCELLED")));
			}
			if(UtilValidate.isNotEmpty(closed)) {
				checkBoxconditions.add(EntityCondition.makeCondition("srStatusId",EntityOperator.IN,UtilMisc.toList("SR_CLOSED","SR_CANCELLED")));
			}
			/*List< Map<String, Object>> slaResult=getSlaForSR(request, response);
    		if(UtilValidate.isNotEmpty(slaResult)) {
    			srPeriodUnit=(String) slaResult.get(0).get("srPeriodUnit");
    			slaPeriodLvl=(String) slaResult.get(0).get("slaPeriodLvl");
    		}*/
			if(UtilValidate.isNotEmpty(slaAtRisk)) {
				/*if(UtilValidate.isNotEmpty(srPeriodUnit) && slaPeriodLvl.equals("Days")) {
    				nDays=Integer.parseInt(srPeriodUnit);
    			}else{
    				nDays=3;
    			}
    			Timestamp sysDate=UtilDateTime.addDaysToTimestamp(UtilDateTime.nowTimestamp(),-nDays);
    			checkBoxconditions.add(EntityCondition.makeCondition(EntityCondition.makeCondition("dueDate", EntityOperator.LESS_THAN, sysDate),EntityOperator.OR,EntityCondition.makeCondition("srStatusId",EntityOperator.NOT_IN,UtilMisc.toList("SR_CLOSED","SR_CANCELLED"))));*/
				checkBoxconditions.add(EntityCondition.makeCondition("slaRisk",EntityOperator.IN,UtilMisc.toList("Yes","Y","yes")));
			}
			if(UtilValidate.isNotEmpty(slaExpired)) {
				//checkBoxconditions.add(EntityCondition.makeCondition(EntityCondition.makeCondition("dueDate", EntityOperator.LESS_THAN, UtilDateTime.nowTimestamp()),EntityOperator.OR,EntityCondition.makeCondition("srStatusId",EntityOperator.NOT_IN,UtilMisc.toList("SR_CLOSED","SR_CANCELLED"))));
				checkBoxconditions.add(EntityCondition.makeCondition("overDueFlag",EntityOperator.IN,UtilMisc.toList("Yes","Y","yes")));
			}
			if(UtilValidate.isNotEmpty(systemViewFilter)) {
				conditions.clear();
				if("loggedInUserServiceRequests".equals(systemViewFilter)){
					if(UtilValidate.isNotEmpty(ownerUserLoginId)) {
						conditions.add(EntityCondition.makeCondition("ownerUserLoginId",EntityOperator.EQUALS,ownerUserLoginId));
						if(UtilValidate.isNotEmpty(lastMonthsDate) && UtilValidate.isNotEmpty(lastMonthsDateStr)) {
							conditions.add(EntityCondition.makeCondition("createdOn",EntityOperator.GREATER_THAN_EQUAL_TO, lastMonthsDate));
							conditions.add(EntityCondition.makeCondition("createdOn",EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.nowTimestamp()));
						}
					}
				}
				if("loggedInUserOpenServiceRequests".equals(systemViewFilter)){
					if(UtilValidate.isNotEmpty(ownerUserLoginId)) {
						conditions.add(EntityCondition.makeCondition("ownerUserLoginId",EntityOperator.EQUALS,ownerUserLoginId));
						conditions.add(EntityCondition.makeCondition("srStatusId",EntityOperator.NOT_IN,UtilMisc.toList("SR_CANCELLED","SR_CLOSED")));
					}
				}
				if("loggedInUserClosedServiceRequests".equals(systemViewFilter)){
					if(UtilValidate.isNotEmpty(ownerUserLoginId)) {
						conditions.add(EntityCondition.makeCondition("ownerUserLoginId",EntityOperator.EQUALS,ownerUserLoginId));
						conditions.add(EntityCondition.makeCondition("srStatusId",EntityOperator.EQUALS,"SR_CLOSED"));
					}
				}
				if("loggedInUserDelegatedServiceRequests".equals(systemViewFilter)){
					if(UtilValidate.isNotEmpty(ownerUserLoginId)) {
						conditions.add(EntityCondition.makeCondition("createdByUserLoginId",EntityOperator.EQUALS,ownerUserLoginId));
						conditions.add(EntityCondition.makeCondition("ownerUserLoginId",EntityOperator.NOT_EQUAL,ownerUserLoginId));
					}
				}
				if("loggedInUserTeamServiceRequests".equals(systemViewFilter)){
					GenericValue emplTeam = EntityUtil.getFirst(EntityQuery.use(delegator).select(UtilMisc.toSet("emplTeamId","partyId","businessUnit")).from("EmplTeam").where("partyId", userLoginPartyId).cache(true).queryList());
					if (UtilValidate.isNotEmpty(userLoginPartyId)&&UtilValidate.isNotEmpty(emplTeam)) {    
						String emplTeamId = emplTeam.getString("emplTeamId");
						if(UtilValidate.isNotEmpty(emplTeamId)) {
							conditions.add(EntityCondition.makeCondition("empTeamId",EntityOperator.EQUALS,emplTeamId));
						}
					}
				}
				if("loggedInUserTeamOpenServiceRequests".equals(systemViewFilter)){
					GenericValue emplTeam = EntityUtil.getFirst(EntityQuery.use(delegator).select(UtilMisc.toSet("emplTeamId","partyId","businessUnit")).from("EmplTeam").where("partyId", userLoginPartyId).cache(true).queryList());
					if (UtilValidate.isNotEmpty(userLoginPartyId)&&UtilValidate.isNotEmpty(emplTeam)) {    
						String emplTeamId = emplTeam.getString("emplTeamId");
						if(UtilValidate.isNotEmpty(emplTeamId)) {
							conditions.add(EntityCondition.makeCondition("empTeamId",EntityOperator.EQUALS,emplTeamId));
							if(UtilValidate.isNotEmpty(lastMonthsDate) && UtilValidate.isNotEmpty(lastMonthsDateStr)) {
								conditions.add(EntityCondition.makeCondition("createdOn",EntityOperator.GREATER_THAN_EQUAL_TO, lastMonthsDate));
								conditions.add(EntityCondition.makeCondition("createdOn",EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.nowTimestamp()));
							}
							conditions.add(EntityCondition.makeCondition("srStatusId",EntityOperator.NOT_IN,UtilMisc.toList("SR_CANCELLED","SR_CLOSED")));
						}
					}
				}
				if("loggedInUserTeamClosedServiceRequests".equals(systemViewFilter)){
					GenericValue emplTeam = EntityUtil.getFirst(EntityQuery.use(delegator).select(UtilMisc.toSet("emplTeamId","partyId","businessUnit")).from("EmplTeam").where("partyId", userLoginPartyId).cache(true).queryList());
					if (UtilValidate.isNotEmpty(userLoginPartyId)&&UtilValidate.isNotEmpty(emplTeam)) {    
						String emplTeamId = emplTeam.getString("emplTeamId");
						if(UtilValidate.isNotEmpty(emplTeamId)) {
							conditions.add(EntityCondition.makeCondition("empTeamId",EntityOperator.EQUALS,emplTeamId));
							if(UtilValidate.isNotEmpty(lastMonthsDate) && UtilValidate.isNotEmpty(lastMonthsDateStr)) {
								conditions.add(EntityCondition.makeCondition("createdOn",EntityOperator.GREATER_THAN_EQUAL_TO, lastMonthsDate));
								conditions.add(EntityCondition.makeCondition("createdOn",EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.nowTimestamp()));
							}
							conditions.add(EntityCondition.makeCondition("srStatusId",EntityOperator.EQUALS,"SR_CLOSED"));
						}
					}
				}
				if("loggedInUserBUOpenServiceRequests".equals(systemViewFilter)){
					GenericValue emplTeam = EntityUtil.getFirst(EntityQuery.use(delegator).select("emplTeamId","partyId","businessUnit").from("EmplTeam").where("partyId", userLoginPartyId).queryList());
					if (UtilValidate.isNotEmpty(emplTeam)&&UtilValidate.isNotEmpty(emplTeam.getString("businessUnit"))) {    
						String businessUnitId = emplTeam.getString("businessUnit");
						if(UtilValidate.isNotEmpty(businessUnitId)) {
							List<GenericValue> businessunitPartyIdsList  = EntityQuery.use(delegator).from("EmplTeam").where("businessUnit", businessUnitId).queryList();
							List<String> businessunitPartyIds = EntityUtil.getFieldListFromEntityList(businessunitPartyIdsList, "partyId", true);
							if (UtilValidate.isNotEmpty(businessunitPartyIds)) {
								conditions.add(EntityCondition.makeCondition("ownerUserLoginId", EntityOperator.IN, businessunitPartyIds));
								if(UtilValidate.isNotEmpty(lastMonthsDate) && UtilValidate.isNotEmpty(lastMonthsDateStr)) {
									conditions.add(EntityCondition.makeCondition("createdOn",EntityOperator.GREATER_THAN_EQUAL_TO, lastMonthsDate));
									conditions.add(EntityCondition.makeCondition("createdOn",EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.nowTimestamp()));
								}
								conditions.add(EntityCondition.makeCondition("srStatusId",EntityOperator.NOT_IN,UtilMisc.toList("SR_CANCELLED","SR_CLOSED")));
							}
						}
					}
				}
				if("loggedInUserBUClosedServiceRequests".equals(systemViewFilter)){
					GenericValue emplTeam = EntityUtil.getFirst(EntityQuery.use(delegator).select("emplTeamId","partyId","businessUnit").from("EmplTeam").where("partyId", userLoginPartyId).queryList());
					if (UtilValidate.isNotEmpty(emplTeam)&&UtilValidate.isNotEmpty(emplTeam.getString("businessUnit"))) {    
						String businessUnitId = emplTeam.getString("businessUnit");
						if(UtilValidate.isNotEmpty(businessUnitId)) {
							List<GenericValue> businessunitPartyIdsList  = EntityQuery.use(delegator).from("EmplTeam").where("businessUnit", businessUnitId).queryList();
							List<String> businessunitPartyIds = EntityUtil.getFieldListFromEntityList(businessunitPartyIdsList, "partyId", true);
							if (UtilValidate.isNotEmpty(businessunitPartyIds)) {
								conditions.add(EntityCondition.makeCondition("ownerUserLoginId", EntityOperator.IN, businessunitPartyIds));
								if(UtilValidate.isNotEmpty(lastMonthsDate) && UtilValidate.isNotEmpty(lastMonthsDateStr)) {
									conditions.add(EntityCondition.makeCondition("createdOn",EntityOperator.GREATER_THAN_EQUAL_TO, lastMonthsDate));
								}
								conditions.add(EntityCondition.makeCondition("srStatusId",EntityOperator.EQUALS,"SR_CLOSED"));
							}
						}
					}
				}
				if("loggedInUserBUOverdueServiceRequests".equals(systemViewFilter)){
					GenericValue emplTeam = EntityUtil.getFirst(EntityQuery.use(delegator).select("emplTeamId","partyId","businessUnit").from("EmplTeam").where("partyId", userLoginPartyId).queryList());
					if (UtilValidate.isNotEmpty(emplTeam)&&UtilValidate.isNotEmpty(emplTeam.getString("businessUnit"))) {    
						String businessUnitId = emplTeam.getString("businessUnit");
						if(UtilValidate.isNotEmpty(businessUnitId)) {
							List<GenericValue> businessunitPartyIdsList  = EntityQuery.use(delegator).from("EmplTeam").where("businessUnit", businessUnitId).queryList();
							List<String> businessunitPartyIds = EntityUtil.getFieldListFromEntityList(businessunitPartyIdsList, "partyId", true);
							if (UtilValidate.isNotEmpty(businessunitPartyIds)) {
								conditions.add(EntityCondition.makeCondition("ownerUserLoginId", EntityOperator.IN, businessunitPartyIds));
								Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();
								if(UtilValidate.isNotEmpty(nowTimeStamp)) {
									conditions.add(EntityCondition.makeCondition("dueDate",EntityOperator.LESS_THAN,nowTimeStamp));
								}
								if(UtilValidate.isNotEmpty(lastMonthsDate) && UtilValidate.isNotEmpty(lastMonthsDateStr)) {
									conditions.add(EntityCondition.makeCondition("createdOn",EntityOperator.GREATER_THAN_EQUAL_TO, lastMonthsDate));
									conditions.add(EntityCondition.makeCondition("createdOn",EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.nowTimestamp()));
								}
								conditions.add(EntityCondition.makeCondition("srStatusId",EntityOperator.NOT_IN,UtilMisc.toList("SR_CANCELLED","SR_CLOSED")));
							}
						}
					}
				}
				orderByField="-createdOn";
			}else{
				orderByField="dueDate";
			}
			EntityCondition condition=null;
			if(UtilValidate.isNotEmpty(checkBoxconditions)) {
				condition=EntityCondition.makeCondition(EntityCondition.makeCondition(conditions,EntityOperator.AND),EntityOperator.AND,(EntityCondition.makeCondition(checkBoxconditions,EntityOperator.OR)));
			}else {
				condition=EntityCondition.makeCondition(conditions,EntityOperator.AND);
			}
			if(UtilValidate.isNotEmpty(condition)) {
				List<GenericValue> custRequestSrSummaryList = EntityQuery.use(delegator).from("CustRequestSrSummary").
						where(condition).orderBy(orderByField).maxRows(endInx).queryList();
				if (UtilValidate.isNotEmpty(custRequestSrSummaryList)) {
					for (GenericValue Entry: custRequestSrSummaryList) {
						Map<String, Object> data = new HashMap<>();
						data.put("custRequestId", Entry.getString("custRequestId"));
						data.put("externalId", Entry.getString("externalId"));
						data.put("customerName", Entry.getString("customerName"));
						data.put("customerType", Entry.getString("customerTypeId"));
						data.put("cinNumber", Entry.getString("cinNumber"));
						data.put("prospectName", Entry.getString("prospectName"));
						data.put("prospectId", Entry.getString("prospectId"));
						data.put("isNonCrm", "No");
						if("Y".equals(Entry.getString("isNonCrm")))
							data.put("isNonCrm", "Yes");
						data.put("vPlusId", Entry.getString("vPlusId"));
						data.put("nationalId", Entry.getString("nationalId"));
						data.put("srTypeName", Entry.getString("srTypeName"));
						data.put("srCategoryName", Entry.getString("srCategoryName"));
						data.put("srSubCategoryName", Entry.getString("srSubCategoryName"));
						data.put("srTypeId", Entry.getString("srTypeId"));
						data.put("srCategoryId", Entry.getString("srCategoryId"));
						data.put("srSubCategoryId", Entry.getString("srSubCategoryId"));
						data.put("otherSrSubCategory", Entry.getString("otherSrSubCategory"));
						data.put("priority", Entry.getString("priority"));
						data.put("srStatusId", Entry.getString("srStatusId"));
						data.put("srStatus", Entry.getString("srStatus"));
						data.put("srSubStatusId", Entry.getString("srSubStatusId"));
						data.put("srSubStatus", Entry.getString("srSubStatus"));
						data.put("description", Entry.getString("description"));
						data.put("resolution", Entry.getString("resolution"));
						if(UtilValidate.isNotEmpty(Entry.getTimestamp("openDate"))){
							dateStr = UtilDateTime.toDateString(Entry.getTimestamp("openDate"),"dd/MM/yyyy hh:mm");
							data.put("openDate", dateStr);
							dateStr="";
						}
						if(UtilValidate.isNotEmpty(Entry.getString("durationDays")))
							data.put("durationDays", Entry.getString("durationDays").replace(".000000","")+" Days");
						if(UtilValidate.isNotEmpty(Entry.getTimestamp("dueDate"))){
							dateStr = UtilDateTime.toDateString(Entry.getTimestamp("dueDate"),"dd/MM/yyyy hh:mm");
							data.put("dueDate", dateStr);
							dateStr="";
						}
						data.put("urgencyState", Entry.getString("urgencyState"));
						data.put("overDueFlag", "No");
						if(UtilValidate.isNotEmpty(Entry.getString("overDueFlag"))&& "Y".equals(Entry.getString("overDueFlag")))
							data.put("overDueFlag", "Yes");
						data.put("ownerUserLoginId", Entry.getString("ownerUserLoginId"));
						data.put("ownerUserLoginDescription", Entry.getString("ownerUserLoginDescription"));
						data.put("ownerBuName", Entry.getString("ownerBuName"));
						data.put("ownerBuId", Entry.getString("ownerBu"));
						data.put("linkedFrom", Entry.getString("linkedFrom"));
						data.put("linkedTo", Entry.getString("linkedTo"));
						data.put("salesOpportunityId", Entry.getString("salesOpportunityId"));
						data.put("workEffortId", Entry.getString("workEffortId"));
						data.put("accountTypeDescription", Entry.getString("accountTypeDescription"));
						data.put("accountNumber", Entry.getString("accountNumber"));
						data.put("onceDone", Entry.getString("onceDone"));
						data.put("empTeamId", Entry.getString("empTeamId"));
						data.put("teamDescription", Entry.getString("teamDescription"));
						data.put("slaRisk", Entry.getString("slaRisk"));
						data.put("slaFixed", Entry.getString("slaFixed"));
						if(UtilValidate.isNotEmpty(Entry.getTimestamp("createdOn"))){
							dateStr = UtilDateTime.toDateString(Entry.getTimestamp("createdOn"),"dd/MM/yyyy hh:mm");
							data.put("createdOn", dateStr);
							dateStr="";
						}
						data.put("createdByUserLoginId", Entry.getString("createdByUserLoginId"));
						data.put("modifiedByUserLoginId", Entry.getString("modifiedByUserLoginId"));
						if(UtilValidate.isNotEmpty(Entry.getTimestamp("modifiedOn"))){
							dateStr = UtilDateTime.toDateString(Entry.getTimestamp("modifiedOn"),"dd/MM/yyyy hh:mm");
							data.put("modifiedOn", dateStr);
							dateStr = "";
						}
						if(UtilValidate.isNotEmpty(Entry.getTimestamp("closedOn"))){
							dateStr = UtilDateTime.toDateString(Entry.getTimestamp("closedOn"),"dd/MM/yyyy hh:mm");
							data.put("closedOn", dateStr);
							dateStr = "";
						}
						data.put("closedBy", Entry.getString("closedBy"));
						data.put("partyId", Entry.getString("fromPartyId"));
						results.add(data);
					}
					result.put("results", results);
				}
			}
		} catch (Exception e) {
			Debug.logError(e, "Exception: " + e.getMessage(), MODULE);
			result.put("_ERROR_MESSAGE_", e.getMessage());
			return result;
		}
		return result;
	}

	public static Map<String,Object> getServiceHomeData(DispatchContext dctx, Map context)  {
		Delegator delegator = (Delegator) dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> result = new HashMap<>();
		List < Map<String, Object>> results = new ArrayList<>();
		String start = (String)context.get("start");
		String length = (String)context.get("length");
		long myOpenSr=0;
		long myTeamOpenSr=0;
		long myOpenSrAtRisk=0;
		long myTeamOpenSrAtRisk=0;
		String emplTeamId = null;
		EntityFindOptions efo = new EntityFindOptions();
		efo.setDistinct(true);
		int startInx = UtilValidate.isNotEmpty(start) ? Integer.parseInt(start) : 0;
		int endInx = UtilValidate.isNotEmpty(length) ? Integer.parseInt(length) : 1000;
		efo.setOffset(startInx);
		efo.setLimit(endInx);
		EntityListIterator eli = null;
		try{
			List<EntityExpr> exprs = UtilMisc.toList(EntityCondition.makeCondition("ownerUserLoginId", EntityOperator.EQUALS, userLogin.getString("partyId")),
					EntityCondition.makeCondition("srStatusId", EntityOperator.NOT_IN, UtilMisc.toList("SR_CANCELLED","SR_CLOSED")));
			EntityCondition condition = EntityCondition.makeCondition(exprs, EntityOperator.AND);
			eli = EntityQuery.use(delegator).from("CustRequestSrSummary").where(condition).orderBy("-createdOn").queryIterator();
			if(eli!=null){
				myOpenSr=eli.getResultsSizeAfterPartialList();
				eli.close();
			}
			exprs.clear();
			GenericValue emplTeam = EntityUtil.getFirst(EntityQuery.use(delegator).select(UtilMisc.toSet("emplTeamId","partyId")).from("EmplTeam").where("partyId", userLogin.getString("userLoginId")).cache(true).queryList());
			if (UtilValidate.isNotEmpty(userLogin.getString("partyId"))&&UtilValidate.isNotEmpty(emplTeam)) {    
				emplTeamId = emplTeam.getString("emplTeamId");
				if(UtilValidate.isNotEmpty(emplTeamId)) {
					exprs = UtilMisc.toList(EntityCondition.makeCondition("empTeamId", EntityOperator.EQUALS, emplTeamId),
							EntityCondition.makeCondition("srStatusId", EntityOperator.NOT_IN, UtilMisc.toList("SR_CANCELLED","SR_CLOSED")));
					condition = EntityCondition.makeCondition(exprs, EntityOperator.AND);
					eli = EntityQuery.use(delegator).from("CustRequestSrSummary").where(condition).orderBy("-createdOn").queryIterator();
					if(eli!=null){
						myTeamOpenSr=eli.getResultsSizeAfterPartialList();
						eli.close();
					}
				}
			}
			exprs.clear();
			if(UtilValidate.isNotEmpty(userLogin.getString("partyId"))){
				exprs = UtilMisc.toList(EntityCondition.makeCondition("ownerUserLoginId", EntityOperator.EQUALS, userLogin.getString("partyId")),
						EntityCondition.makeCondition("slaRisk", EntityOperator.EQUALS, "YES"),
						EntityCondition.makeCondition("srStatusId", EntityOperator.NOT_IN, UtilMisc.toList("SR_CANCELLED","SR_CLOSED")));
				condition = EntityCondition.makeCondition(exprs, EntityOperator.AND);
				eli = EntityQuery.use(delegator).from("CustRequestSrSummary").where(condition).orderBy("-createdOn").queryIterator();
				if(eli!=null){
					myOpenSrAtRisk=eli.getResultsSizeAfterPartialList();
					eli.close();
				}
			}
			exprs.clear();
			int nDays=3;
			Timestamp sysDate=UtilDateTime.addDaysToTimestamp(UtilDateTime.nowTimestamp(),-nDays);
			if(UtilValidate.isNotEmpty(emplTeamId)) {
				exprs = UtilMisc.toList(EntityCondition.makeCondition("empTeamId", EntityOperator.EQUALS, emplTeamId),
						EntityCondition.makeCondition("overDueFlag", EntityOperator.EQUALS, "Y"),
						EntityCondition.makeCondition("srStatusId", EntityOperator.NOT_IN, UtilMisc.toList("SR_CANCELLED","SR_CLOSED")));
				condition = EntityCondition.makeCondition(exprs, EntityOperator.AND);
				eli = EntityQuery.use(delegator).from("CustRequestSrSummary").where(condition).orderBy("-createdOn").queryIterator();
				if(eli!=null){
					myTeamOpenSrAtRisk=eli.getResultsSizeAfterPartialList();
					eli.close();
				}
			}
			Map<String, Object> data = new HashMap<>();
			data.put("myOpenSr", myOpenSr);
			data.put("myTeamOpenSr", myTeamOpenSr);
			data.put("myOpenSrAtRisk", myOpenSrAtRisk);
			data.put("myTeamOpenSrAtRisk",myTeamOpenSrAtRisk);
			results.add(data);
			result.put("results", results);
		} catch (Exception e) {
			Debug.logError(e, "Exception: " + e.getMessage(), MODULE);
			result.putAll( ServiceUtil.returnError(e.getMessage()));
		}finally {
			if (eli != null) {
				try {
					eli.close();
				} catch (GenericEntityException e) {
					//e.printStackTrace();
					Debug.logError(e, "Exception: " + e.getMessage(), MODULE);
					result.putAll( ServiceUtil.returnError(e.getMessage()));
				}
			}
		}
		return result;
	}
	public static Map<String,Object> getActivityHomeData(DispatchContext dctx, Map context) {
		Delegator delegator = (Delegator) dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> result = new HashMap<>();
		List < Map<String, Object>> results = new ArrayList<>();
		String start = (String) context.get("start");
		String length = (String) context.get("length");
		String ownerUserLoginId = (String) context.get("ownerUserLoginId");
		String systemViewFilter = (String) context.get("systemViewFilter");
		String userLoginPartyId = userLogin.getString("partyId");
		String lastMonthsDateStr = (String) context.get("lastMonthsDate");
		String isRequestFromViewCalendar = (String) context.get("isRequestFromViewCalendar");
		String workEffortId = (String) context.get("workEffortId");
		String businessUnitName = (String) context.get("businessUnitName");
		String createdByUserLogin = (String) context.get("createdByUserLogin");
		String workEffortServiceType = (String) context.get("workEffortServiceType");
		String workEffortSubServiceType = (String) context.get("workEffortSubServiceType");
		String currentStatusId = (String) context.get("currentStatusId");
		String actualStartDateStr = (String) context.get("actualStartDate");
		String actualEndDateStr = (String) context.get("actualEndDate");
		String statusopen = (String) context.get("statusopen");
		String statuscompleted = (String) context.get("statuscompleted");
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		java.sql.Timestamp lastMonthsDate = null;
		EntityFindOptions efo = new EntityFindOptions();
		efo.setDistinct(true);
		int startInx = UtilValidate.isNotEmpty(start) ? Integer.parseInt(start) : 0;
		int endInx = UtilValidate.isNotEmpty(length) ? Integer.parseInt(length) : 1000;
		efo.setOffset(startInx);
		efo.setLimit(endInx);
		Timestamp date = null;
		if(UtilValidate.isNotEmpty(lastMonthsDateStr)) {
			try {
				lastMonthsDate = new java.sql.Timestamp(sdf.parse(lastMonthsDateStr).getTime());
			} catch (Exception e) {
				Debug.logError(e, "Cannot parse date string: " +  e.getMessage(), MODULE);
			}
			lastMonthsDate = UtilDateTime.getDayStart(lastMonthsDate);
		}

		try {
			List<EntityCondition> conditions = FastList.newInstance();
			EntityCondition condition=null;
			if(UtilValidate.isNotEmpty(systemViewFilter)) {
				conditions.clear();
				if("loggedInUserActivities".equals(systemViewFilter)){
					if(UtilValidate.isNotEmpty(ownerUserLoginId)) {
						conditions.add(EntityCondition.makeCondition("primOwnerId",EntityOperator.EQUALS,ownerUserLoginId));
					}
					if(UtilValidate.isNotEmpty(lastMonthsDate) && UtilValidate.isNotEmpty(lastMonthsDateStr)) {
						conditions.add(EntityCondition.makeCondition("createdDate",EntityOperator.GREATER_THAN, lastMonthsDate));
						conditions.add(EntityCondition.makeCondition("createdDate",EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.nowTimestamp()));
					}
				}
				if("loggedInUserOpenActivities".equals(systemViewFilter)){
					if(UtilValidate.isNotEmpty(ownerUserLoginId)) {
						conditions.add(EntityCondition.makeCondition("primOwnerId",EntityOperator.EQUALS,ownerUserLoginId));
					}
					conditions.add(EntityCondition.makeCondition("currentStatusId",EntityOperator.NOT_IN,UtilMisc.toList("IA_CANCELLED","IA_CLOSED","IA_MCOMPLETED")));
				}
				if("loggedInUserClosedActivities".equals(systemViewFilter)){
					if(UtilValidate.isNotEmpty(ownerUserLoginId)) {
						conditions.add(EntityCondition.makeCondition("primOwnerId",EntityOperator.EQUALS,ownerUserLoginId));
					}
					conditions.add(EntityCondition.makeCondition("currentStatusId",EntityOperator.IN,UtilMisc.toList("IA_CLOSED","IA_MCOMPLETED")));
				}
				if("loggedInUserTeamActivities".equals(systemViewFilter)){
					GenericValue emplTeam = EntityUtil.getFirst(EntityQuery.use(delegator).select(UtilMisc.toSet("emplTeamId","partyId","businessUnit")).from("EmplTeam").where("partyId", userLoginPartyId).queryList());
					if (UtilValidate.isNotEmpty(userLoginPartyId)&&UtilValidate.isNotEmpty(emplTeam)) {    
						String emplTeamId = emplTeam.getString("emplTeamId");
						if(UtilValidate.isNotEmpty(emplTeamId)) {
							conditions.add(EntityCondition.makeCondition("emplTeamId",EntityOperator.EQUALS,emplTeamId));
						}
					}
				}
				if("loggedInUserTeamOpenActivities".equals(systemViewFilter)){
					GenericValue emplTeam = EntityUtil.getFirst(EntityQuery.use(delegator).select(UtilMisc.toSet("emplTeamId","partyId","businessUnit")).from("EmplTeam").where("partyId", userLoginPartyId).queryList());
					if (UtilValidate.isNotEmpty(userLoginPartyId)&&UtilValidate.isNotEmpty(emplTeam)) {    
						String emplTeamId = emplTeam.getString("emplTeamId");
						if(UtilValidate.isNotEmpty(emplTeamId)) {
							conditions.add(EntityCondition.makeCondition("emplTeamId",EntityOperator.EQUALS,emplTeamId));
						}
					}
					conditions.add(EntityCondition.makeCondition("currentStatusId",EntityOperator.NOT_IN,UtilMisc.toList("IA_CANCELLED","IA_CLOSED","IA_MCOMPLETED")));
				}
				if("loggedInUserTeamClosedActivities".equals(systemViewFilter)){
					GenericValue emplTeam = EntityUtil.getFirst(EntityQuery.use(delegator).select(UtilMisc.toSet("emplTeamId","partyId","businessUnit")).from("EmplTeam").where("partyId", userLoginPartyId).queryList());
					if (UtilValidate.isNotEmpty(userLoginPartyId)&&UtilValidate.isNotEmpty(emplTeam)) {    
						String emplTeamId = emplTeam.getString("emplTeamId");
						if(UtilValidate.isNotEmpty(emplTeamId)) {
							conditions.add(EntityCondition.makeCondition("emplTeamId",EntityOperator.EQUALS,emplTeamId));
						}
					}
					if(UtilValidate.isNotEmpty(lastMonthsDate) && UtilValidate.isNotEmpty(lastMonthsDateStr)) {
						conditions.add(EntityCondition.makeCondition("createdDate",EntityOperator.LESS_THAN, lastMonthsDate));
					}
					conditions.add(EntityCondition.makeCondition("currentStatusId",EntityOperator.EQUALS,"IA_MCOMPLETED"));
				}
				condition=EntityCondition.makeCondition(conditions,EntityOperator.AND);
			}else{
				if (UtilValidate.isNotEmpty(workEffortId))
					conditions.add(EntityCondition.makeCondition("workEffortId",EntityOperator.EQUALS,workEffortId));
				if (UtilValidate.isNotEmpty(businessUnitName))
					conditions.add(EntityCondition.makeCondition("businessUnitName",EntityOperator.LIKE,"%"+businessUnitName+"%"));
				if (UtilValidate.isNotEmpty(createdByUserLogin))
					conditions.add(EntityCondition.makeCondition("createdByUserLogin",EntityOperator.EQUALS,createdByUserLogin));
				if (UtilValidate.isNotEmpty(workEffortServiceType))
					conditions.add(EntityCondition.makeCondition("workEffortServiceType",EntityOperator.EQUALS,workEffortServiceType));
				if (UtilValidate.isNotEmpty(workEffortSubServiceType))
					conditions.add(EntityCondition.makeCondition("workEffortSubServiceType",EntityOperator.EQUALS,workEffortSubServiceType));
				if (UtilValidate.isNotEmpty(currentStatusId))
					conditions.add(EntityCondition.makeCondition("currentStatusId",EntityOperator.EQUALS,currentStatusId));

				if (UtilValidate.isNotEmpty(actualStartDateStr)) {
					date = new Timestamp(sdf.parse(actualStartDateStr).getTime());
					conditions.add(EntityCondition.makeCondition(EntityCondition.makeCondition("actualStartDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(date))));
				}
				if (UtilValidate.isNotEmpty(actualEndDateStr)) {
					date = new Timestamp(sdf.parse(actualEndDateStr).getTime());
					conditions.add(EntityCondition.makeCondition(EntityCondition.makeCondition("actualEndDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(date))));
				}
				List<EntityCondition> checkBoxconditions = FastList.newInstance();
				if(UtilValidate.isNotEmpty(statusopen)) {
					checkBoxconditions.add(EntityCondition.makeCondition("currentStatusId",EntityOperator.NOT_IN,UtilMisc.toList("IA_MCOMPLETED","IA_CANCELLED")));
				}
				if(UtilValidate.isNotEmpty(statuscompleted)) {
					checkBoxconditions.add(EntityCondition.makeCondition("currentStatusId",EntityOperator.EQUALS,"IA_MCOMPLETED"));
				}
				if(UtilValidate.isNotEmpty(checkBoxconditions) && (UtilValidate.isNotEmpty(conditions))) {
					condition=EntityCondition.makeCondition(EntityCondition.makeCondition(conditions,EntityOperator.AND),EntityOperator.AND,(EntityCondition.makeCondition(checkBoxconditions,EntityOperator.OR)));
				}else if(UtilValidate.isNotEmpty(checkBoxconditions) && (UtilValidate.isEmpty(conditions))) {
					condition=EntityCondition.makeCondition(checkBoxconditions,EntityOperator.OR);
				}else  {
					condition=EntityCondition.makeCondition(conditions,EntityOperator.AND);
				}
			}
			if(null != isRequestFromViewCalendar && "true".equalsIgnoreCase(isRequestFromViewCalendar)) {
				conditions.add(EntityCondition.makeCondition("workEffortTypeId",EntityOperator.EQUALS,"62823"));
				condition=EntityCondition.makeCondition(conditions,EntityOperator.AND);
			}
			if(UtilValidate.isNotEmpty(condition)) {
				List<GenericValue> getActivityHome =EntityQuery.use(delegator).from("WorkEffortCallSummary").where(condition).maxRows(endInx).queryList(); 
				List<String> primOwnerIds = EntityUtil.getFieldListFromEntityList(getActivityHome, "primOwnerId", true);
				List<String> emplTeamIds = EntityUtil.getFieldListFromEntityList(getActivityHome, "emplTeamId", true);
				List<EntityCondition> primOwnerConditionList = FastList.newInstance();
				primOwnerConditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, primOwnerIds));
				EntityCondition primOwnerCondition = EntityCondition.makeCondition(primOwnerConditionList, EntityOperator.AND);
				List<GenericValue> personList = EntityQuery.use(delegator).select("partyId","firstName").from("Person").where(primOwnerCondition).queryList();
				Map<String, String> WorkEffortOwnerMap = new HashMap<String, String>();
				if (UtilValidate.isNotEmpty(personList)) {
					for (GenericValue eachEntry: personList) {
						WorkEffortOwnerMap.put(eachEntry.getString("partyId"),eachEntry.getString("firstName"));
					}
				}
				primOwnerConditionList.clear();
				primOwnerConditionList.add(EntityCondition.makeCondition("emplTeamId", EntityOperator.IN, emplTeamIds));
				EntityCondition emplTeamCondition = EntityCondition.makeCondition(primOwnerConditionList, EntityOperator.AND);
				List<GenericValue> emplTeamList = EntityQuery.use(delegator).select("emplTeamId","teamName").from("EmplTeam").where(emplTeamCondition).queryList();
				Map<String, String> WorkEffortTeamMap = new HashMap<String, String>();
				if (UtilValidate.isNotEmpty(emplTeamList)) {
					for (GenericValue eachEntry: emplTeamList) {
						WorkEffortTeamMap.put(eachEntry.getString("emplTeamId"),eachEntry.getString("teamName"));
					}
				}    			
				if (UtilValidate.isNotEmpty(getActivityHome)) {
					for (GenericValue Entry: getActivityHome) {	    	       
						Map<String, Object> data = new HashMap<>();
						data.put("workEffortId", Entry.getString("workEffortId"));
						data.put("businessUnitId", Entry.getString("businessUnitId"));
						data.put("businessUnitName", Entry.getString("businessUnitName"));
						data.put("emplTeamId", Entry.getString("emplTeamId"));
						data.put("customerName", Entry.getString("customerName"));
						data.put("customerType", Entry.getString("customerType"));
						data.put("CIFID", Entry.getString("cinNumber"));
						data.put("prospectId", Entry.getString("prospectId"));
						if("Y".equals(Entry.getString("isNonCrm")))
							data.put("isNonCrm", "Yes");
						data.put("wfVplusId", Entry.getString("wfVplusId"));
						data.put("wfNationalId", Entry.getString("wfNationalId"));
						data.put("parentActivity", Entry.getString("workEffortParentDescription"));
						data.put("activityType", Entry.getString("workEffortServiceTypeValue"));
						data.put("activitySubType", Entry.getString("workEffortSubServiceTypeValue"));
						data.put("subject", Entry.getString("description"));
						data.put("accountNumber", Entry.getString("accountNumber"));
						data.put("businessUnitName", Entry.getString("businessUnitName"));
						if(UtilValidate.isNotEmpty(Entry.getTimestamp("estimatedStartDate"))){
							String dateStr = UtilDateTime.toDateString(Entry.getTimestamp("estimatedStartDate"),"dd/MM/yyyy hh:mm");
							data.put("plannedStartDate", dateStr);
						}
						data.put("campaignCode", Entry.getString("campaignCode"));
						data.put("productName", Entry.getString("productName"));
						data.put("type", Entry.getString("workEffortTypeDescription"));
						//data.put("subType", Entry.getString("subType"));
						data.put("source", Entry.getString("lastUpdatedSource"));
						data.put("comments", Entry.getString("comments"));
						data.put("plannedDuration", Entry.getString("plannedDuration"));
						if(UtilValidate.isNotEmpty(Entry.getTimestamp("estimatedCompletionDate"))){
							String dateStr = UtilDateTime.toDateString(Entry.getTimestamp("estimatedCompletionDate"),"dd/MM/yyyy hh:mm");
							data.put("plannedDueDate", dateStr);
						}
						if(UtilValidate.isNotEmpty(Entry.getTimestamp("actualCompletionDate"))){
							String dateStr = UtilDateTime.toDateString(Entry.getTimestamp("actualCompletionDate"),"dd/MM/yyyy hh:mm");
							data.put("actualCompletionDate", dateStr);
							data.put("actualCompletionDateCal", UtilDateTime.toDateString(Entry.getTimestamp("actualCompletionDate"),"yyyy-MM-dd"));
						}
						data.put("wfOnceDone", Entry.getString("wfOnceDone"));
						data.put("currentStatusId", Entry.getString("currentStatusDescription"));
						data.put("primOwnerId", Entry.getString("primOwnerId"));
						//data.put("createdByFromIserve", Entry.getString("createdByFromIserve"));
						//data.put("lastUpdatedByFromIserve", Entry.getString("lastUpdatedByFromIserve"));
						data.put("overDue", Entry.getString("overDue"));
						data.put("resolution", Entry.getString("resolution"));
						if(UtilValidate.isNotEmpty(Entry.getTimestamp("actualStartDate"))){
							String dateStr = UtilDateTime.toDateString(Entry.getTimestamp("actualStartDate"),"dd/MM/yyyy hh:mm");
							data.put("actualStartDate", dateStr);
							data.put("actualStartDateCal", UtilDateTime.toDateString(Entry.getTimestamp("actualStartDate"),"yyyy-MM-dd"));
						}
						data.put("actualDuration", Entry.getString("actualDuration"));
						if(UtilValidate.isNotEmpty(Entry.getTimestamp("createdDate"))){
							String dateStr = UtilDateTime.toDateString(Entry.getTimestamp("createdDate"),"dd/MM/yyyy hh:mm");
							data.put("createdDate", dateStr);
						}
						data.put("createdByUserLogin", Entry.getString("createdByUserLogin"));
						if(UtilValidate.isNotEmpty(Entry.getTimestamp("lastModifiedDate"))){
							String dateStr = UtilDateTime.toDateString(Entry.getTimestamp("lastModifiedDate"),"dd/MM/yyyy hh:mm");
							data.put("lastModifiedDate", dateStr);
						}
						data.put("lastModifiedByUserLogin", Entry.getString("lastModifiedByUserLogin"));
						if (UtilValidate.isNotEmpty(Entry.getString("primOwnerId"))) {
							if (UtilValidate.isNotEmpty(WorkEffortOwnerMap.get(Entry.getString("primOwnerId")))) {
								data.put("primOwnerName", WorkEffortOwnerMap.get(Entry.getString("primOwnerId")));
							}
						}
						if (UtilValidate.isNotEmpty(Entry.getString("emplTeamId"))) {
							if (UtilValidate.isNotEmpty(WorkEffortTeamMap.get(Entry.getString("emplTeamId")))) {
								data.put("emplTeamName", WorkEffortTeamMap.get(Entry.getString("emplTeamId")));
							}
						}
						results.add(data);
					}
					result.put("results", results);
				}
			}
		} catch (Exception e) {
			Debug.logError(e, "Exception: " + e.getMessage(), MODULE);
			result.putAll( ServiceUtil.returnError(e.getMessage()));
			return result;
		}
		return result;
	}
	public static Map<String,Object> getSrCategory(DispatchContext dctx, Map context) {
		Delegator delegator = (Delegator) dctx.getDelegator();
		Map<String, Object> result = new HashMap<>();
		List < Map<String, Object>> results = new ArrayList<>();
		try {
			List < GenericValue > srCategorires = EntityQuery.use(delegator).select("custRequestCategoryId","description").from("CustRequestCategory").where("parentCustRequestCategoryId", null).orderBy("seqNum").queryList();
			if (UtilValidate.isNotEmpty(srCategorires)) {
				for (GenericValue srCategory: srCategorires) {
					Map < String, Object > data = new HashMap < String, Object > ();
					data.put("srCategoryId",srCategory.getString("custRequestCategoryId"));
					data.put("srCategoryDesc",srCategory.getString("description"));
					results.add(data);
				}
				result.put("results", results);
			}
		} catch (Exception e) {
			Debug.logError(e, "Exception: " + e.getMessage(), MODULE);
			result.put( "_EVENT_MESSAGE_",e.getMessage());
			return result;
		}
		return result;
	}
	public static Map<String,Object> getSrSubCategory(DispatchContext dctx, Map context) {
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = (Delegator) dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> result = new HashMap<>();
		List < Map < String, Object >> results = new ArrayList < Map < String, Object >> ();
		String srCategoryId = (String) context.get("srCategoryId");
		try {
			if(UtilValidate.isNotEmpty(srCategoryId)) {
				List < GenericValue > srSubCategorires = EntityQuery.use(delegator).select("custRequestCategoryId","description").from("CustRequestCategory").where("parentCustRequestCategoryId", srCategoryId).orderBy("seqNum").queryList();
				if (UtilValidate.isNotEmpty(srSubCategorires)) {
					for (GenericValue srSubCategoriry: srSubCategorires) {
						Map < String, Object > data = new HashMap < String, Object > ();
						data.put("srSubCategoryId",srSubCategoriry.getString("custRequestCategoryId"));
						data.put("srSubCategoryDesc",srSubCategoriry.getString("description"));

						results.add(data);
					}
					result.put("results", results);
				}
			}
		} catch (Exception e) {
			Debug.logError(e, "Exception: " + e.getMessage(), MODULE);
			result.putAll( ServiceUtil.returnError(e.getMessage()));
			return result;
		}
		return result;
	}

	public static Map<String, Object> getActivityCounts(DispatchContext dctx, Map context) {
		Delegator delegator = (Delegator) dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> result = new HashMap<>();
		String userLoginPartyId = userLogin.getString("partyId");
		Map<String, Object> data = new HashMap<>();

		if(UtilValidate.isEmpty(userLogin)){
			data.put("error", "No user login details found!");
			result.putAll( data);
			return result;
		}
		try{
			List<EntityCondition> conditions = new ArrayList<EntityCondition>();
			conditions.add(EntityCondition.makeCondition("primOwnerId",EntityOperator.EQUALS, userLogin.getString("partyId")));
			conditions.add(EntityCondition.makeCondition("currentStatusId",EntityOperator.NOT_IN,UtilMisc.toList("IA_CANCELLED","IA_CLOSED")));
			long myActivities = delegator.findCountByCondition("WorkEffortCallSummary", EntityCondition.makeCondition(conditions, EntityOperator.AND), null,null);
			data.put("myActivities", String.valueOf(myActivities));
			conditions.clear();
			GenericValue emplTeam = EntityUtil.getFirst(EntityQuery.use(delegator).select(UtilMisc.toSet("emplTeamId","partyId","businessUnit")).from("EmplTeam").where("partyId", userLoginPartyId).cache(true).queryList());
			if (UtilValidate.isNotEmpty(userLoginPartyId)&&UtilValidate.isNotEmpty(emplTeam)) {    
				String emplTeamId = emplTeam.getString("emplTeamId");
				if(UtilValidate.isNotEmpty(emplTeamId)) {
					conditions.add(EntityCondition.makeCondition("emplTeamId",EntityOperator.EQUALS,emplTeamId));
				}
			}
			conditions.add(EntityCondition.makeCondition("currentStatusId",EntityOperator.NOT_IN,UtilMisc.toList("IA_CANCELLED","IA_CLOSED")));
			long myTeamActivities = delegator.findCountByCondition("WorkEffortCallSummary", EntityCondition.makeCondition(conditions, EntityOperator.AND), null,null);
			data.put("myTeamActivities", String.valueOf(myTeamActivities));

			conditions.clear();
			conditions.add(EntityCondition.makeCondition("primOwnerId",EntityOperator.EQUALS, userLogin.getString("partyId")));
			conditions.add(EntityCondition.makeCondition("currentStatusId",EntityOperator.IN, UtilMisc.toList("IA_COMPLETED","IA_MCOMPLETED")));
			long completedActivities = delegator.findCountByCondition("WorkEffortCallSummary", EntityCondition.makeCondition(conditions, EntityOperator.AND), null,null);
			data.put("completedActivities", String.valueOf(completedActivities));

			conditions.clear();
			conditions.add(EntityCondition.makeCondition("estimatedCompletionDate",EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.nowTimestamp()));
			long overDueActivities = delegator.findCountByCondition("WorkEffortCallSummary", EntityCondition.makeCondition(conditions, EntityOperator.AND), null,null);
			data.put("overDueActivities", String.valueOf(overDueActivities));
			result.put("results", data);
		}
		catch (Exception e) {
			Debug.logError(e, "Exception: " + e.getMessage(), MODULE);
			data.put("errorMessage", e.getMessage());
			data.put("errorResult", new ArrayList<>());
			result.putAll(data);
			return result;
		}
		return result;
	}
	public static Map<String, Object> getSrOverDueSummary(DispatchContext dctx, Map context) {
		Delegator delegator = (Delegator) dctx.getDelegator();
		Map<String, Object> result = new HashMap<>();
		List < Map < String, Object >> results = new ArrayList < Map < String, Object >> ();
		String start = (String) context.get("start");
		String length = (String) context.get("length");
		String businessUnit = (String) context.get("businessUnit");
		EntityFindOptions efo = new EntityFindOptions();
		efo.setDistinct(true);
		int startInx = UtilValidate.isNotEmpty(start) ? Integer.parseInt(start) : 0;
		int endInx = UtilValidate.isNotEmpty(length) ? Integer.parseInt(length) : 1000;
		efo.setOffset(startInx);
		efo.setLimit(endInx);
		EntityListIterator eli = null;
		List<GenericValue> OverDueSrList=null;
		try{

			List<EntityCondition>	condList= FastList.newInstance();
			condList.add(EntityCondition.makeCondition("srStatusId", EntityOperator.NOT_IN, UtilMisc.toList("SR_CANCELLED","SR_CLOSED")));
			if(UtilValidate.isNotEmpty(businessUnit)) {
				condList.add(EntityCondition.makeCondition("ownerBuName", EntityOperator.EQUALS, businessUnit));
			}
			EntityCondition condition = EntityCondition.makeCondition(condList, EntityOperator.AND);
			Set < String > fieldsToSelect = new TreeSet < String > ();
			fieldsToSelect.add("ownerBuName");
			fieldsToSelect.add("createdOn");
			fieldsToSelect.add("dueDate");
			fieldsToSelect.add("closedOn");
			fieldsToSelect.add("custRequestId");
			fieldsToSelect.add("srStatusId");
			eli = EntityQuery.use(delegator).from("CustRequestSrSummary").select(fieldsToSelect).where(condition).orderBy("-createdOn").queryIterator();
			if(eli!=null){
				OverDueSrList=eli.getCompleteList();
			}
			if(UtilValidate.isNotEmpty(OverDueSrList)) {
				int nDays=-3;
				Timestamp sysDate3=UtilDateTime.addDaysToTimestamp(UtilDateTime.nowTimestamp(),nDays);
				List<GenericValue> s3DaysList = EntityUtil.filterByCondition(OverDueSrList, EntityCondition.makeCondition(EntityCondition.makeCondition("dueDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.nowTimestamp()),EntityOperator.AND,EntityCondition.makeCondition("dueDate", EntityOperator.GREATER_THAN, sysDate3)));
				nDays=-10;
				Timestamp sysDate10=UtilDateTime.addDaysToTimestamp(UtilDateTime.nowTimestamp(),nDays);
				List<GenericValue> s10DaysList = EntityUtil.filterByCondition(OverDueSrList, EntityCondition.makeCondition(EntityCondition.makeCondition("dueDate", EntityOperator.GREATER_THAN, sysDate3),EntityOperator.AND,EntityCondition.makeCondition("dueDate", EntityOperator.LESS_THAN_EQUAL_TO, sysDate10)));
				nDays=-15;
				Timestamp sysDate15=UtilDateTime.addDaysToTimestamp(UtilDateTime.nowTimestamp(),nDays);
				List<GenericValue> s15DaysList = EntityUtil.filterByCondition(OverDueSrList, EntityCondition.makeCondition(EntityCondition.makeCondition("dueDate", EntityOperator.GREATER_THAN, sysDate10),EntityOperator.AND,EntityCondition.makeCondition("dueDate", EntityOperator.LESS_THAN_EQUAL_TO, sysDate15)));
				nDays=-20;
				Timestamp sysDate20=UtilDateTime.addDaysToTimestamp(UtilDateTime.nowTimestamp(),nDays);
				List<GenericValue> s20DaysList = EntityUtil.filterByCondition(OverDueSrList, EntityCondition.makeCondition(EntityCondition.makeCondition("dueDate", EntityOperator.GREATER_THAN, sysDate15),EntityOperator.AND,EntityCondition.makeCondition("dueDate", EntityOperator.LESS_THAN_EQUAL_TO, sysDate20)));
				nDays=-30;
				Timestamp sysDate30=UtilDateTime.addDaysToTimestamp(UtilDateTime.nowTimestamp(),nDays);
				List<GenericValue> s30DaysList = EntityUtil.filterByCondition(OverDueSrList, EntityCondition.makeCondition(EntityCondition.makeCondition("dueDate", EntityOperator.GREATER_THAN, sysDate20),EntityOperator.AND,EntityCondition.makeCondition("dueDate", EntityOperator.LESS_THAN_EQUAL_TO, sysDate30)));
				nDays=-50;
				Timestamp sysDate50=UtilDateTime.addDaysToTimestamp(UtilDateTime.nowTimestamp(),nDays);
				List<GenericValue> s50DaysList = EntityUtil.filterByCondition(OverDueSrList, EntityCondition.makeCondition(EntityCondition.makeCondition("dueDate", EntityOperator.GREATER_THAN, sysDate30),EntityOperator.AND,EntityCondition.makeCondition("dueDate", EntityOperator.LESS_THAN_EQUAL_TO, sysDate50)));
				List<GenericValue> s50pDaysList = EntityUtil.filterByCondition(OverDueSrList, EntityCondition.makeCondition("dueDate", EntityOperator.GREATER_THAN,  sysDate50));
				Map<String, Object> data = new HashMap<>();
				data.put("s3Days", UtilValidate.isNotEmpty(s3DaysList)?s3DaysList.size():0);
				data.put("sysDate3", UtilDateTime.toDateString(sysDate3,"dd/MM/yyyy"));
				data.put("sysDate", UtilDateTime.toDateString(UtilDateTime.nowTimestamp(),"dd/MM/yyyy"));
				data.put("s10Days", UtilValidate.isNotEmpty(s10DaysList)?s10DaysList.size():0);
				data.put("sysDate10", UtilDateTime.toDateString(sysDate10,"dd/MM/yyyy"));
				data.put("s11Days", UtilValidate.isNotEmpty(s15DaysList)?s15DaysList.size():0);
				data.put("sysDate15", UtilDateTime.toDateString(sysDate15,"dd/MM/yyyy"));
				data.put("s16Days", UtilValidate.isNotEmpty(s20DaysList)?s20DaysList.size():0);
				data.put("sysDate20", UtilDateTime.toDateString(sysDate20,"dd/MM/yyyy"));
				data.put("s21Days", UtilValidate.isNotEmpty(s30DaysList)?s30DaysList.size():0);
				data.put("sysDate30", UtilDateTime.toDateString(sysDate30,"dd/MM/yyyy"));
				data.put("s31Days", UtilValidate.isNotEmpty(s50DaysList)?s50DaysList.size():0);
				data.put("sysDate50", UtilDateTime.toDateString(sysDate50,"dd/MM/yyyy"));
				data.put("s51Days", UtilValidate.isNotEmpty(s50pDaysList)?s50pDaysList.size():0);
				long total= (long)((UtilValidate.isNotEmpty(s3DaysList)?s3DaysList.size():0)+(UtilValidate.isNotEmpty(s10DaysList)?s10DaysList.size():0)+(UtilValidate.isNotEmpty(s15DaysList)?s15DaysList.size():0)+(UtilValidate.isNotEmpty(s20DaysList)?s20DaysList.size():0)+(UtilValidate.isNotEmpty(s30DaysList)?s30DaysList.size():0)+(UtilValidate.isNotEmpty(s50DaysList)?s50DaysList.size():0)+(UtilValidate.isNotEmpty(s50pDaysList)?s50pDaysList.size():0));
				data.put("total", total);
				results.add(data);

			}else {
				int nDays=3;
				Timestamp sysDate3=UtilDateTime.addDaysToTimestamp(UtilDateTime.nowTimestamp(),-nDays);
				nDays=10;
				Timestamp sysDate10=UtilDateTime.addDaysToTimestamp(UtilDateTime.nowTimestamp(),-nDays);
				nDays=15;
				Timestamp sysDate15=UtilDateTime.addDaysToTimestamp(UtilDateTime.nowTimestamp(),-nDays);
				nDays=20;
				Timestamp sysDate20=UtilDateTime.addDaysToTimestamp(UtilDateTime.nowTimestamp(),-nDays);
				nDays=30;
				Timestamp sysDate30=UtilDateTime.addDaysToTimestamp(UtilDateTime.nowTimestamp(),-nDays);
				nDays=50;
				Timestamp sysDate50=UtilDateTime.addDaysToTimestamp(UtilDateTime.nowTimestamp(),-nDays);
				Map<String, Object> data = new HashMap<>();
				data.put("s3Days", 0);
				data.put("sysDate", UtilDateTime.toDateString(UtilDateTime.nowTimestamp(),"dd/MM/yyyy"));
				data.put("sysDate3", UtilDateTime.toDateString(sysDate3,"dd/MM/yyyy"));
				data.put("s10Days", 0);
				data.put("sysDate10", UtilDateTime.toDateString(sysDate10,"dd/MM/yyyy"));
				data.put("s11Days", 0);
				data.put("sysDate15", UtilDateTime.toDateString(sysDate15,"dd/MM/yyyy"));
				data.put("s16Days", 0);
				data.put("sysDate20", UtilDateTime.toDateString(sysDate20,"dd/MM/yyyy"));
				data.put("s21Days", 0);
				data.put("sysDate30", UtilDateTime.toDateString(sysDate30,"dd/MM/yyyy"));
				data.put("s31Days", 0);
				data.put("sysDate50", UtilDateTime.toDateString(sysDate50,"dd/MM/yyyy"));
				data.put("s51Days", 0);
				long total=0;
				data.put("total", total);
				results.add(data);
			}
			result.put("results",results);
		} catch (Exception e) {
			Debug.logError(e, "Exception: " + e.getMessage(), MODULE);
			result.putAll(ServiceUtil.returnError(e.getMessage()));
		}finally {
			if (eli != null) {
				try {
					eli.close();
				} catch (GenericEntityException e) {
					Debug.logError(e, "Exception: " + e.getMessage(), MODULE);
					result.putAll(ServiceUtil.returnError(e.getMessage()));
				}
			}
		}
		return result;
	}
	public static Map<String, Object> reassignSr(DispatchContext dctx, Map context) {
		Delegator delegator = (Delegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> result = new HashMap<>();
		List<String> results = new ArrayList<String>();
		String ownerId = (String) context.get("accountPartyId");
		String custRequestId = (String) context.get("srNumber");
		String externalLoginKey = (String) context.get("externalLoginKey");
		String responsiblePerson = "";
		try {
			String userLoginId = userLogin.getString("userLoginId");
			String accessLevel = "Y";
			String businessUnit = null;
			String teamId = "";

			List < EntityCondition > conditions = new ArrayList<EntityCondition>();
			if (UtilValidate.isNotEmpty(ownerId)) {
				conditions.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, ownerId));
			}
			conditions.add(EntityCondition.makeCondition("enabled", EntityOperator.EQUALS, "Y"));
			EntityCondition condition = EntityCondition.makeCondition(conditions, EntityOperator.AND);

			List <GenericValue> userLogins = EntityQuery.use(delegator).from("UserLogin").where(condition).queryList();

			if (UtilValidate.isNotEmpty(userLogins)) {
				for (GenericValue userLoginDetails: userLogins) {
					responsiblePerson = userLoginDetails.getString("userLoginId");
				}
			}

			Map<String, Object> custReqData = DataUtil.getCustRequestDetail(delegator, custRequestId);
			if(UtilValidate.isNotEmpty(custReqData)) {
				businessUnit = UtilValidate.isNotEmpty(custReqData.get("businessUnit")) ? (String) custReqData.get("businessUnit") : "";
				teamId = UtilValidate.isNotEmpty(custReqData.get("teamId")) ? (String) custReqData.get("teamId") : "";
			}
			Map<String, Object> accessMatrixRes = new HashMap<>();
			if (UtilValidate.isNotEmpty(userLoginId)) {
				String userLoginPartyId = DataUtil.getUserLoginPartyId(delegator, userLoginId);
				if(UtilValidate.isEmpty(businessUnit))
					businessUnit = DataUtil.getBusinessUnitId(delegator, userLoginPartyId);
				Map<String, Object> accessMatrixMap = new LinkedHashMap<>();
				accessMatrixMap.put("delegator", delegator);
				accessMatrixMap.put("dispatcher", dispatcher);
				accessMatrixMap.put("teamId", teamId);
				accessMatrixMap.put("businessUnit", businessUnit);
				accessMatrixMap.put("modeOfOp", "ASSIGN");
				accessMatrixMap.put("entityName", "CustRequest");
				accessMatrixMap.put("userLoginId", userLoginId);
				accessMatrixRes = org.fio.homeapps.util.DataUtil.getAccessList(accessMatrixMap);
				if (UtilValidate.isNotEmpty(accessMatrixRes)) {
					accessLevel = (String) accessMatrixRes.get("accessLevel");
				} else {
					accessLevel = null;
				}
				//validate the common team and access for the assignment
				String currentPartyId = userLoginPartyId;
				if(UtilValidate.isEmpty(responsiblePerson)) {
					responsiblePerson = teamId;
				} else {
					currentPartyId = DataUtil.getUserLoginPartyId(delegator, responsiblePerson);
					Map<String, Object> buTeamData = DataUtil.getUserBuTeam(delegator, currentPartyId);
					businessUnit = (String) buTeamData.get("businessUnit");
					teamId = (String) buTeamData.get("emplTeamId");
				}
				List<EntityCondition> conditionList = new ArrayList<EntityCondition>();
				//change the access in the create 
				//check with ownerId
				if(UtilValidate.isNotEmpty(accessMatrixRes) && accessMatrixRes.containsKey("ownerId")) {
					@SuppressWarnings("unchecked")
					List<String> ownerIds = (List<String>) accessMatrixRes.get("ownerId");
					if(!ownerIds.contains(teamId)) accessLevel = null;
					conditionList.add(EntityCondition.makeCondition("responsiblePerson", EntityOperator.IN, ownerIds));
					//custRequestContext.put("ownerIds", ownerIds);
				}

				//check with emplTeamId
				if(UtilValidate.isNotEmpty(accessMatrixRes) && accessMatrixRes.containsKey("emplTeamId")) {
					@SuppressWarnings("unchecked")
					List<String> emplTeamIds = (List<String>) accessMatrixRes.get("emplTeamId");
					if(!emplTeamIds.contains(teamId)) accessLevel = null;
					conditionList.add(EntityCondition.makeCondition("emplTeamId", EntityOperator.IN, emplTeamIds));
					//custRequestContext.put("emplTeamIds", emplTeamIds);
				}

				if (UtilValidate.isNotEmpty(custRequestId)) {
					conditionList.add(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId));
				}

				EntityCondition mainCondition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);

				GenericValue custRequest1 = EntityUtil
						.getFirst(delegator.findList("CustRequest", mainCondition, null, null, null, false));

				if(UtilValidate.isEmpty(custRequest1)) accessLevel=null;

			}
			if(UtilValidate.isNotEmpty(accessLevel) && AccessLevel.YES.equals(accessLevel)) {
				GenericValue updateCustRequest = EntityUtil.getFirst(delegator.findByAnd("CustRequest",
						UtilMisc.toMap("custRequestId", custRequestId), null, false));
				if (UtilValidate.isNotEmpty(updateCustRequest)) {

					String cNo = updateCustRequest.getString("fromPartyId");
					String statusId = updateCustRequest.getString("statusId");
					String previousOwnerId = updateCustRequest.getString("responsiblePerson");
					String custRequestName = updateCustRequest.getString("custRequestName");
					String description = updateCustRequest.getString("description");

					/*GenericValue emplTeam = EntityUtil.getFirst(EntityQuery.use(delegator).select(UtilMisc.toSet("emplTeamId","partyId","businessUnit")).from("EmplTeam").where("partyId", ownerUserLoginId).queryList());
				if (UtilValidate.isNotEmpty(emplTeam)) {    
					String primOwnerTeamId = emplTeam.getString("emplTeamId");
					if (UtilValidate.isNotEmpty(primOwnerTeamId)) {   
						updateCustRequest.put("emplTeamId", primOwnerTeamId);
					}
					String ownerBusinessUnitId = emplTeam.getString("businessUnit");
					if (UtilValidate.isNotEmpty(ownerBusinessUnitId)) {   
						updateCustRequest.put("ownerBu", ownerBusinessUnitId);
					}
				}*/
					String responsiblePersonExists = updateCustRequest.getString("responsiblePerson"); 

					if(UtilValidate.isNotEmpty(responsiblePersonExists) && responsiblePersonExists.equals(responsiblePerson)){

						String data = "";
						data= "This "+custRequestId+"  is already assigned to the selected Owner";
						results.add(data);
						result.put("_EVENT_MESSAGE_", data);
						result.put("results", results);
					}else if(UtilValidate.isNotEmpty(responsiblePerson)){

						updateCustRequest.put("responsiblePerson", responsiblePerson);
						updateCustRequest.put("emplTeamId", teamId);
						updateCustRequest.put("ownerBu", businessUnit);
						/*if(UtilValidate.isNotEmpty(ownerUserLoginId))
					updateCustRequest.put("responsiblePerson", ownerUserLoginId);
				if(UtilValidate.isNotEmpty(emplTeamId))
					updateCustRequest.put("emplTeamId", emplTeamId);*/

						updateCustRequest.store();
						
						if(UtilValidate.isNotEmpty(responsiblePerson)) {
							String userLoginPartyId = DataUtil.getUserLoginPartyId(delegator, responsiblePerson);
							String userLoginRole = org.fio.admin.portal.util.DataUtil.getPartySecurityRole(delegator, userLoginPartyId);
							org.fio.sr.portal.DataHelper.createCustRequestParty(delegator, custRequestId, userLoginPartyId, userLoginRole);
						}

						// Reassign Notification Email

						Debug.log("== REASSIGN SR userLoginId =="+userLoginId+"== previousOwnerId =="+previousOwnerId);

						if (UtilValidate.isNotEmpty(custRequestId) && UtilValidate.isNotEmpty(userLoginId) && UtilValidate.isNotEmpty(previousOwnerId) && !previousOwnerId.equals(userLoginId)) {

							String partyDesc = org.fio.homeapps.util.PartyHelper.getPartyName(delegator, cNo, false);
							String ownerDesc = org.fio.homeapps.util.PartyHelper.getUserLoginName(delegator, userLoginId, false);
							String srStatusDesc = org.fio.homeapps.util.DataUtil.getStatusDescription(delegator, statusId);

							String subject = "Assigned SR ID "+custRequestId+" - "+custRequestName;

							/*Map<String, String> loggedInUserContactInformation = PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator,userLogin.getString("partyId"));
					String nsender = loggedInUserContactInformation.get("EmailAddress");*/

							String nsender = "";
							GenericValue sytemProperty = EntityQuery.use(delegator).from("SystemProperty").where("systemResourceId", "NOTIFICATION", "systemPropertyId", "from").queryOne();
							if(UtilValidate.isNotEmpty(sytemProperty)){
								nsender = sytemProperty.getString("systemPropertyValue");
							}

							Map<String, String> ntoContactInformation = PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator, ownerId);
							String nto = ntoContactInformation.get("EmailAddress");

							Map<String, String> previousOwnerContactInformation = PartyPrimaryContactMechWorker.getPartyPrimaryContactMechValueMaps(delegator,previousOwnerId);
							String ccAddresses = previousOwnerContactInformation.get("EmailAddress");

							if (UtilValidate.isNotEmpty(nsender) && UtilValidate.isNotEmpty(nto)){

								String appUrl = "";
								GenericValue systemProperty = EntityQuery.use(delegator).from("SystemProperty").where("systemResourceId", "EMAIL_NOTIFICATION", "systemPropertyId", "url").queryOne();
								if(UtilValidate.isNotEmpty(systemProperty)){
									appUrl = systemProperty.getString("systemPropertyValue");
								}

								String strVar="";
								strVar += "<html>";
								strVar += "<head>";
								strVar += " <title></title>";
								strVar += "</head>";
								strVar += "";
								strVar += "<body>";
								strVar += "";
								strVar += "<table style=\"font-family:Verdana; font-size:12px;\">";
								strVar += "<tr>";
								strVar += "<td colspan=\"3\">Dear "+ownerDesc+"</td>";
								strVar += "</tr>";
								strVar += "<tr>";
								strVar += "<td>&nbsp;</td>";
								strVar += "<td>&nbsp;</td>";
								strVar += "</tr>";
								strVar += "<tr>";
								strVar += "<td colspan=\"3\">An update has been made to the support SR:</td>";
								strVar += "</tr>";
								strVar += "<tr>";
								strVar += "<td>&nbsp;</td>";
								strVar += "<td>&nbsp;</td>";
								strVar += "</tr>";
								strVar += "<tr>";
								strVar += "<td width=\"15%\"><b>Customer</b></td>";
								strVar += "<td>:</td>";
								strVar += "<td style=\"font-family:Verdana;text-align:left;padding-left: 5px;\">"+partyDesc+"</td>";
								strVar += "</tr>";
								strVar += "<tr>";
								strVar += "<td><b>SR ID</b></td>";
								strVar += "<td>:</td>";
								strVar += "<td style=\"font-family:Verdana;text-align:left;padding-left: 5px;\"><a target=\"_blank\" href="+appUrl+"/sr-portal/control/viewServiceRequest?srNumber="+custRequestId+"&externalLoginKey="+externalLoginKey+">"+custRequestId+"</a></td>";
								strVar += "</tr>";
								strVar += "<tr>";
								strVar += "<td><b>SR Status</b></td>";
								strVar += "<td>:</td>";
								strVar += "<td style=\"font-family:Verdana;text-align:left;padding-left: 5px;\">"+srStatusDesc+"</td>";
								strVar += "</tr>";
								strVar += "<tr>";
								strVar += "<td style=\"vertical-align: top;\"><b>SR Name</b></td>";
								strVar += "<td style=\"vertical-align: top;\">:</td>";
								strVar += "<td style=\"font-family:Verdana;text-align:left;padding-left: 5px;\">"+custRequestName+"</td>";
								strVar += "</tr>";
								strVar += "<tr>";
								strVar += "<td style=\"vertical-align: top;\"><b>Description</b></td>";
								strVar += "<td style=\"vertical-align: top;\">:</td>";
								strVar += "<td style=\"font-family:Verdana;text-align:left;padding-left: 5px;\">"+description+"</td>";
								strVar += "</tr>";
								strVar += "";
								strVar += "<tr>";
								strVar += "<td colspan=\"2\">&nbsp;</td>";
								strVar += "</tr>";
								strVar += "<tr>";
								strVar += "<td colspan=\"3\">";
								strVar += "Thanks,<br>";
								strVar += "CRM Administrator.";
								strVar += "</td>";
								strVar += "</tr>";
								strVar += "</table>";
								strVar += "";
								strVar += "</body>";
								strVar += "</html>";
								strVar += "";

								Map<String, Object> callCtxt = FastMap.newInstance();
								Map<String, Object> callResult = FastMap.newInstance();
								Map<String, Object> requestContext = FastMap.newInstance();

								requestContext.put("nsender", nsender);
								requestContext.put("nto", nto);
								requestContext.put("subject", subject);
								requestContext.put("emailContent", strVar);
								requestContext.put("ccAddresses", ccAddresses);

								callCtxt.put("requestContext", requestContext);
								callCtxt.put("userLogin", userLogin);

								Debug.log("==== sendEmail ===="+callCtxt);

								callResult = dispatcher.runSync("common.sendEmail", callCtxt);
								if (ServiceUtil.isError(callResult)) {
									String errMsg = "Email send failed: "+ServiceUtil.getErrorMessage(callResult);
									results.add(errMsg);
									result.put("_EVENT_MESSAGE_", "Error While Sending Notification Email");
									result.put("results", results);
									return result;
								}
							}
						}

						String data = "";
						data= "Reassigned Successfully";
						results.add(data);
						result.put("_EVENT_MESSAGE_", "Reassigned Successfully");
						result.put("results", results);
					}
				} else {
					result.put("_ERROR_MESSAGE_", "SR not found");
					return result;
				}
			} else {
				String errMsg = "";
				if(UtilValidate.isNotEmpty(accessMatrixRes) && accessMatrixRes.containsKey("errorMessage")) {
					errMsg = accessMatrixRes.get("errorMessage").toString();
				} else {
					errMsg = "Access Denied";
				}
				result.put("_ERROR_MESSAGE_", errMsg);
				Debug.log("error==" + errMsg);
				return result;
			}


		} catch (Exception e) {
			String errMsg = "" + e.toString();
			Debug.logError(e, errMsg, MODULE);
			String data = "";
			data= errMsg;
			results.add(data);
			result.put("_EVENT_MESSAGE_", "Error in Reassign");
			result.put("results", results);
			return result;
		}
		return result;
	}

	public static Map<String, Object> addContactToSr(DispatchContext dctx, Map context) {
		Delegator delegator = (Delegator) dctx.getDelegator();
		Map<String, Object> result = new HashMap<>();
		List<String> results = new ArrayList<>();
		String contactPartyId = (String) context.get("contactPartyId");
		String srNumber = (String) context.get("srNumber");		

		try {

			if (UtilValidate.isNotEmpty(contactPartyId) && UtilValidate.isNotEmpty(srNumber) ) {


				EntityCondition searchConditions = EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, srNumber),
						EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, contactPartyId),
						EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "CONTACT"),
						EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));

				//EntityUtil.getFilterByDateExpr());
				List<GenericValue> existingRelationships = delegator.findList("CustRequestContact", searchConditions,null, null, null, false);

				if (UtilValidate.isNotEmpty(existingRelationships)) {
					GenericValue personGv = delegator.findOne("Person",UtilMisc.toMap("partyId", contactPartyId), false);

					String fullName = "";

					if(UtilValidate.isNotEmpty(personGv)){
						fullName = personGv.getString("firstName") + "  " +personGv.getString("lastName");

					}
					String data = "";
					data= fullName + "-" +contactPartyId+" already associated with this Service Request";

					results.add(data);
					result.put("_EVENT_MESSAGE_", data);
					result.put("results", results);
					result.put("srNumber", srNumber);
					result.put("activeTab", "contacts");
				}else{
					GenericValue custRequestContactCreate = delegator.makeValue("CustRequestContact");
					custRequestContactCreate.set("custRequestId", srNumber);
					custRequestContactCreate.set("partyId", contactPartyId);
					custRequestContactCreate.set("roleTypeId", "CONTACT");
					custRequestContactCreate.set("fromDate", UtilDateTime.nowTimestamp());

					custRequestContactCreate.create();

					String data = "";
					data= "Contact Successfully added to Service Request";
					results.add(data);
					result.put("_EVENT_MESSAGE_", "Contact Successfully added to Service Request");
					result.put("results", results);
					result.put("srNumber", srNumber);
					result.put("activeTab", "contacts");
				}


			}


		} catch (Exception e) {
			String errMsg = "" + e.toString();
			Debug.logError(e, errMsg, MODULE);
			String data = "";
			data= errMsg;
			results.add(data);
			result.put("_EVENT_MESSAGE_", "Error in adding caontact to service request");
			result.put("results", results);
			result.put("srNumber", srNumber);
			result.put("activeTab", "contacts");
			return result;
		}
		return result;
	}
	public static Map<String, Object> removeContactFromSr(DispatchContext dctx, Map context) {
		Delegator delegator = (Delegator) dctx.getDelegator();
		Map<String, Object> result = new HashMap<>();
		List<String> results = new ArrayList<>();
		String contactPartyId = (String) context.get("partyIdFrom");
		String srNumber = (String) context.get("srNumber");		


		try {

			if (UtilValidate.isNotEmpty(contactPartyId) && UtilValidate.isNotEmpty(srNumber) ) {

				EntityCondition searchConditions = EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, srNumber),
						EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, contactPartyId),
						EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "CONTACT"),
						EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));

				//EntityUtil.getFilterByDateExpr());
				GenericValue existingRelationships = EntityUtil.getFirst(delegator.findList("CustRequestContact", searchConditions,null, null, null, false));

				if (UtilValidate.isNotEmpty(existingRelationships)) {
					existingRelationships.set("thruDate", UtilDateTime.nowTimestamp());
					existingRelationships.store();

					result = ServiceUtil.returnSuccess("Contact removed successfully");
					result.put("srNumber", srNumber);
					result.put("activeTab", "contacts");
				}else{
					String data = "";
					data= "Contact not associated to service request";
					results.add(data);
					result.put("_EVENT_MESSAGE_", "Contact not associated to service request");
					result.put("results", results);
					result.put("srNumber", srNumber);
					result.put("activeTab", "contacts");			
				}


			}

		} catch (Exception e) {
			String errMsg = "" + e.toString();
			Debug.logError(e, errMsg, MODULE);
			String data = "";
			data= errMsg;
			results.add(data);
			result.put("_EVENT_MESSAGE_", "Error in remvoing contact to service request");
			result.put("results", results);
			result.put("srNumber", srNumber);
			result.put("activeTab", "contacts");
			return result;
		}
		return result;
	}




	@SuppressWarnings("unchecked")
	public static Map<String, Object> resolveServiceRequest(DispatchContext dctx, Map context) {
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = (Delegator) dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> result = new HashMap<>();
		List<String> results = new ArrayList<>();
		String srSubStatusId = (String) context.get("srSubStatusId");
		//String externalId = (String) context.get("externalId");
		String custRequestId = (String) context.get("custRequestId");
		List<String> externalIds = new LinkedList<String>();
		String srStatusId = (String) context.get("srStatusId");
		String srSubStatus = (String) context.get("srSubStatus");
		String statusId = (String) context.get("statusId");
		String subStatusId = (String) context.get("subStatusId");
		String description = (String) context.get("description");
		String resolution = (String) context.get("resolution");
		
		List<GenericValue> custRequestsList= new LinkedList<>();
		try {
			/*
			if(UtilValidate.isNotEmpty(context.get("externalIds"))){
				externalIds=(List)context.get("externalIds");
			}
			if(UtilValidate.isNotEmpty(externalId)&&UtilValidate.isEmpty(externalIds)){
				externalIds.clear();
				externalIds.add(externalId);
			}
			
			if(UtilValidate.isNotEmpty(externalIds)){
				custRequestsList=delegator.findList("CustRequest",
						EntityCondition.makeCondition("externalId", EntityOperator.IN ,externalIds),null,null, null, false);
			}
			for(String custRequestId:externalIds){
				GenericValue updateCustRequest  = EntityUtil.getFirst(EntityUtil.filterByCondition(custRequestsList, EntityCondition.makeCondition("externalId", EntityOperator.EQUALS, custRequestId)));
				if (UtilValidate.isNotEmpty(updateCustRequest)) {
					updateCustRequest.put("statusId", "SR_CLOSED");
					updateCustRequest.put("subStatusId", "SR_RESOLVED");
					if (UtilValidate.isNotEmpty(userLogin.getString("partyId"))) {
						updateCustRequest.put("closedByUserLogin", userLogin.getString("partyId"));
					}
					if (UtilValidate.isNotEmpty(srStatusId)) {
						updateCustRequest.put("statusId", srStatusId);
					}
					/*if (UtilValidate.isNotEmpty(srSubStatusId)) {
	    				updateCustRequest.put("subStatusId", srSubStatusId);
	    			}*
					if (UtilValidate.isNotEmpty(statusId)) {
						updateCustRequest.put("statusId", statusId);
					}
					/*if (UtilValidate.isNotEmpty(subStatusId)) {
	    				updateCustRequest.put("subStatusId", subStatusId);
	    			}*/
					/*if (UtilValidate.isNotEmpty(srSubStatus)) {
	    				updateCustRequest.put("subStatusId", srSubStatus);
	    			}*
					if (UtilValidate.isNotEmpty(description)) {
						updateCustRequest.put("description", description);
					}if (UtilValidate.isNotEmpty(resolution)) {
						updateCustRequest.put("resolution", resolution);
					}



					Timestamp closedByDate = UtilDateTime.nowTimestamp();
					updateCustRequest.put("closedByDate",closedByDate);


					updateCustRequest.store();
					result.put("_EVENT_MESSAGE_", "Resolved Successfully");
					String data = "";
					data= "Resolved Successfully";
					results.add(data);
					result.put("results",results);
				}
				if (UtilValidate.isNotEmpty(updateCustRequest)) {
					Map<String, Object> custRequestContext = new HashMap<>();
					Map<String, Object> supplementoryContext = new HashMap<>();

					if(UtilValidate.isNotEmpty(custRequestId)){
						custRequestContext.put("externalId", custRequestId);
						custRequestContext.put("statusId", "SR_CLOSED");
						custRequestContext.put("isAttachment", "N");
						if (UtilValidate.isNotEmpty(description)) {
							custRequestContext.put("description", description);
						}if (UtilValidate.isNotEmpty(resolution)) {
							custRequestContext.put("resolution", resolution);
						}
					}
					Map<String, Object> historyInputMap = new HashMap<String, Object>();
					historyInputMap.put("custRequestId", custRequestId);
					historyInputMap.put("userLogin", userLogin);
					
					Map<String, Object> historyOutMap = dispatcher.runSync("srPortal.createSrHistory", historyInputMap);
					
					if(!ServiceUtil.isSuccess(historyOutMap)) {
						result.put("_ERROR_MESSAGE_", "Problem While Creating Service Request History");
						return result;
					}
					
					Map<String, Object> inputMap = new HashMap<>();
					inputMap.put("custRequestContext", custRequestContext);
					inputMap.put("supplementoryContext", supplementoryContext);
					inputMap.put("userLogin", userLogin);
					Map<String, Object> outMap = dispatcher.runSync("crmPortal.updateServiceRequest", inputMap);
					if(!ServiceUtil.isSuccess(outMap)) {
						result.put("_ERROR_MESSAGE_", "Problem While Updating Service Request");
						result.put("_EVENT_MESSAGE_", "Error in SR Resolve");
						return result;
					}
				}
			}
			*/
			
			GenericValue updateCustRequest  = EntityQuery.use(delegator).from("CustRequest").where("custRequestId", custRequestId).queryFirst();
			if (UtilValidate.isNotEmpty(updateCustRequest)) {
				String externalId  = updateCustRequest.getString("externalId");
				updateCustRequest.put("statusId", "SR_CLOSED");
				updateCustRequest.put("subStatusId", "SR_RESOLVED");
				if (UtilValidate.isNotEmpty(userLogin.getString("partyId"))) {
					updateCustRequest.put("closedByUserLogin", userLogin.getString("partyId"));
				}
				if (UtilValidate.isNotEmpty(srStatusId)) {
					updateCustRequest.put("statusId", srStatusId);
				}
				/*if (UtilValidate.isNotEmpty(srSubStatusId)) {
    				updateCustRequest.put("subStatusId", srSubStatusId);
    			}*/
				if (UtilValidate.isNotEmpty(statusId)) {
					updateCustRequest.put("statusId", statusId);
				}
				/*if (UtilValidate.isNotEmpty(subStatusId)) {
    				updateCustRequest.put("subStatusId", subStatusId);
    			}*/
				/*if (UtilValidate.isNotEmpty(srSubStatus)) {
    				updateCustRequest.put("subStatusId", srSubStatus);
    			}*/
				if (UtilValidate.isNotEmpty(description)) {
					updateCustRequest.put("description", description);
				}if (UtilValidate.isNotEmpty(resolution)) {
					updateCustRequest.put("resolution", resolution);
				}



				Timestamp closedByDate = UtilDateTime.nowTimestamp();
				updateCustRequest.put("closedByDate",closedByDate);


				updateCustRequest.store();
				result.put("_EVENT_MESSAGE_", "Resolved Successfully");
				String data = "";
				data= "Resolved Successfully";
				results.add(data);
				result.put("results",results);
			
				Map<String, Object> custRequestContext = new HashMap<>();
				Map<String, Object> supplementoryContext = new HashMap<>();

				if(UtilValidate.isNotEmpty(custRequestId)){
					custRequestContext.put("externalId", externalId);
					custRequestContext.put("statusId", "SR_CLOSED");
					custRequestContext.put("isAttachment", "N");
					if (UtilValidate.isNotEmpty(description)) {
						custRequestContext.put("description", description);
					}if (UtilValidate.isNotEmpty(resolution)) {
						custRequestContext.put("resolution", resolution);
					}
				}
				Map<String, Object> historyInputMap = new HashMap<String, Object>();
				historyInputMap.put("custRequestId", custRequestId);
				historyInputMap.put("userLogin", userLogin);
				
				Map<String, Object> historyOutMap = dispatcher.runSync("srPortal.createSrHistory", historyInputMap);
				
				if(!ServiceUtil.isSuccess(historyOutMap)) {
					result.put("_ERROR_MESSAGE_", "Problem While Creating Service Request History");
					return result;
				}
				
				Map<String, Object> inputMap = new HashMap<>();
				inputMap.put("custRequestContext", custRequestContext);
				inputMap.put("supplementoryContext", supplementoryContext);
				inputMap.put("userLogin", userLogin);
				Map<String, Object> outMap = dispatcher.runSync("crmPortal.updateServiceRequest", inputMap);
				if(!ServiceUtil.isSuccess(outMap)) {
					result.put("_ERROR_MESSAGE_", "Problem While Updating Service Request");
					result.put("_EVENT_MESSAGE_", "Error in SR Resolve");
					return result;
				}
			} else {
				result.put("_ERROR_MESSAGE_", "FSR not found!");
				return result;
			}
			
		} catch (Exception e) {
			String errMsg = "" + e.toString();
			Debug.logError(e, errMsg, MODULE);
			String data = "";
			data= errMsg;
			results.add(data);
			result.put("_EVENT_MESSAGE_", "Error in SR Resolve");
			result.put("results", results);
			return result;
		}
		return result;
	}

	public static Map<String, Object> saveServiceRequest(DispatchContext dctx, Map context) {
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = (Delegator) dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> result = new HashMap<>();
		List<String> results = new ArrayList<>();
		String srSubStatusId = (String) context.get("srSubStatusId");
		String externalId = (String) context.get("externalId");
		List<String> externalIds = new ArrayList<String>();
		String srStatusId = (String) context.get("srStatusId");
		String statusId = (String) context.get("statusId");
		String subStatusId = (String) context.get("subStatusId");
		String srStatus = (String) context.get("srStatus");
		List<GenericValue> custRequestsList= new LinkedList<>();

		try {
			if(UtilValidate.isNotEmpty(context.get("externalIds"))){
				externalIds=(List)context.get("externalIds");
			}
			if(UtilValidate.isNotEmpty(externalId)&&UtilValidate.isEmpty(externalIds)){
				externalIds.clear();
				externalIds.add(externalId);
			}
			if(UtilValidate.isNotEmpty(externalIds)){
				custRequestsList=delegator.findList("CustRequest",EntityCondition.makeCondition("externalId", EntityOperator.IN ,externalIds),null,null, null, false);
			}
			for(String custRequestId:externalIds){
				GenericValue updateCustRequest  = EntityUtil.getFirst(EntityUtil.filterByCondition(custRequestsList, EntityCondition.makeCondition("externalId", EntityOperator.EQUALS, custRequestId)));
				if (UtilValidate.isNotEmpty(updateCustRequest)) {
					if(UtilValidate.isNotEmpty(srStatusId))
						updateCustRequest.put("statusId", srStatusId);
					if(UtilValidate.isNotEmpty(srSubStatusId))
						updateCustRequest.put("subStatusId", srSubStatusId);
					if (UtilValidate.isNotEmpty(statusId)) {
						updateCustRequest.put("statusId",statusId);
					}
					if (UtilValidate.isNotEmpty(subStatusId)) {
						updateCustRequest.put("subStatusId",subStatusId);
					}
					if (UtilValidate.isNotEmpty(srStatus)) {
						updateCustRequest.put("statusId",srStatus);
					}
					updateCustRequest.store();
				}
			}
			String data = "Saved Successfully";
			results.add(data);
			result.put("_EVENT_MESSAGE_", "Saved Successfully");
			result.put("results", results);
		} catch (Exception e) {
			String errMsg = "" + e.toString();
			Debug.logError(e, errMsg, MODULE);
			String data = "";
			data= errMsg;
			results.add(data);
			result.put("results", results);
			return result;
		}
		return result;
	}
	public static Map<String, Object> getServiceDetails(DispatchContext dctx, Map context) {
		Delegator delegator = (Delegator) dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> result = new HashMap<>();
		List<Map<String, Object>> results = new ArrayList<>();
		String start = (String)context.get("start");
		String length = (String)context.get("length");
		String externalId = (String)context.get("externalId");
		EntityFindOptions efo = new EntityFindOptions();
		efo.setDistinct(true);
		int startInx = UtilValidate.isNotEmpty(start) ? Integer.parseInt(start) : 0;
		int endInx = UtilValidate.isNotEmpty(length) ? Integer.parseInt(length) : 1000;
		efo.setOffset(startInx);
		efo.setLimit(endInx);
		try {
			if (UtilValidate.isNotEmpty(externalId)) {
				String userLoginId = userLogin.getString("userLoginId");
				List<String> rmList = new ArrayList<String>();
				String accessLevel = "Y";
				String opLevel="L6";
				String businessUnit = null;
				String emplTeamId = null;
				String userLoginPartyId = userLogin.getString("partyId");
				Map<String, Object> accessMatrixIN = new LinkedHashMap<>();
				Map<String, Object> accessMatrixRes = new LinkedHashMap<>();
				GenericValue emplTeam = EntityUtil.getFirst(
						EntityQuery.use(delegator).select(UtilMisc.toSet("emplTeamId", "partyId", "businessUnit"))
						.from("EmplTeam").where("partyId", userLoginPartyId).cache(true).queryList());
				//commenting Temporarily as security configs are not available
				/*if (UtilValidate.isNotEmpty(userLoginId) && UtilValidate.isNotEmpty(emplTeam)) {
					if (UtilValidate.isNotEmpty(emplTeam.getString("businessUnit"))&&UtilValidate.isNotEmpty(emplTeam.getString("emplTeamId"))) {
						businessUnit = UtilValidate.isNotEmpty(emplTeam.get("businessUnit"))? emplTeam.getString("businessUnit"): "";
						emplTeamId = UtilValidate.isNotEmpty(emplTeam.get("emplTeamId")) ? emplTeam.getString("emplTeamId"): "";
						accessMatrixIN.put("businessUnit", businessUnit);
						accessMatrixIN.put("modeOfOp", "Create");
						accessMatrixIN.put("entityName", "CustRequest");
						accessMatrixIN.put("teamId", emplTeamId);
						accessMatrixIN.put("userLoginId", userLoginId);
						try {
							accessMatrixRes = dispatcher.runSync("ap.getAccessMatrixInfo", accessMatrixIN);
							if (ServiceUtil.isSuccess(accessMatrixRes)) {
								JSONObject accessMatrixObj = JSONObject
										.fromObject(accessMatrixRes.get("securityLevelInfo").toString());
								accessLevel = (String) accessMatrixObj.get("access_level");
								opLevel= (String) accessMatrixObj.get("opLevel");
							} else {
								accessLevel = null;
								opLevel= null;
							}
						} catch (Exception e) {
							Debug.logInfo(e.getMessage(), MODULE);
						}
					}
				}*/
				GenericValue CustRequestSrSummary = delegator.findOne("CustRequestSrSummary",
						UtilMisc.toMap("custRequestId", externalId), false);
				GenericValue custRequest = delegator.findOne("CustRequest", UtilMisc.toMap("custRequestId",externalId),false);
				String custReqSrSource="";
				if (UtilValidate.isNotEmpty(custRequest) && UtilValidate.isNotEmpty(custRequest.getString("custReqSrSource"))) {
					GenericValue enumeration = EntityQuery.use(delegator).select("description").from("Enumeration").where("enumId", custRequest.getString("custReqSrSource")).queryOne();
					if (UtilValidate.isNotEmpty(enumeration)  && UtilValidate.isNotEmpty(enumeration.getString("description"))) {
						custReqSrSource = enumeration.getString("description");
					}
				}
				if (CustRequestSrSummary != null) {
					String dateStr = null;
					Map<String, Object> data = new HashMap<>();
					data.put("custRequestId", CustRequestSrSummary.getString("custRequestId"));
					data.put("externalId", CustRequestSrSummary.getString("externalId"));
					data.put("customerName", CustRequestSrSummary.getString("customerName"));
					data.put("cinNumber", CustRequestSrSummary.getString("cinNumber"));
					data.put("prospectName", CustRequestSrSummary.getString("prospectName"));
					data.put("prospectId", CustRequestSrSummary.getString("prospectId"));
					data.put("isNonCrm", "No");
					if ("Y".equals(CustRequestSrSummary.getString("isNonCrm")))
						data.put("isNonCrm", "Yes");
					data.put("vPlusId", CustRequestSrSummary.getString("vPlusId"));
					data.put("nationalId", CustRequestSrSummary.getString("nationalId"));
					data.put("srTypeId", CustRequestSrSummary.getString("srTypeId"));
					data.put("srTypeName", CustRequestSrSummary.getString("srTypeName"));
					data.put("srCategoryName", CustRequestSrSummary.getString("srCategoryName"));
					data.put("srCategoryId", CustRequestSrSummary.getString("srCategoryId"));
					data.put("srSubCategoryId", CustRequestSrSummary.getString("srSubCategoryId"));
					data.put("srSubCategoryName", CustRequestSrSummary.getString("srSubCategoryName"));
					data.put("otherSrSubCategory", CustRequestSrSummary.getString("otherSrSubCategory"));
					data.put("priority", CustRequestSrSummary.getString("priority"));
					data.put("priorityId", CustRequestSrSummary.getString("priorityId"));
					data.put("srStatusId", CustRequestSrSummary.getString("srStatus"));
					data.put("srSubStatusId", CustRequestSrSummary.getString("srSubStatus"));
					data.put("srStatus", CustRequestSrSummary.getString("srStatusId"));
					data.put("srSubStatus", CustRequestSrSummary.getString("srSubStatusId"));
					data.put("description", CustRequestSrSummary.getString("description"));
					data.put("resolution", CustRequestSrSummary.getString("resolution"));
					if (UtilValidate.isNotEmpty(CustRequestSrSummary.getTimestamp("openDate"))){
						dateStr = UtilDateTime.toDateString(CustRequestSrSummary.getTimestamp("openDate"),
								"yyyy/MM/dd hh:mm");
						data.put("openDate", dateStr);
						dateStr = "";
					}
					if (UtilValidate.isNotEmpty(CustRequestSrSummary.getString("durationDays"))) {
						data.put("durationDays",
								CustRequestSrSummary.getString("durationDays").replace(".000000", "") + " Days");
					}
					if (UtilValidate.isNotEmpty(CustRequestSrSummary.getTimestamp("dueDate"))){
						dateStr = UtilDateTime.toDateString(CustRequestSrSummary.getTimestamp("dueDate"),
								"yyyy/MM/dd hh:mm");
						data.put("dueDate", dateStr);
						dateStr = "";
					}
					data.put("urgencyState", CustRequestSrSummary.getString("urgencyState"));
					data.put("overDueFlag", "No");
					if ("Y".equals(CustRequestSrSummary.getString("overDueFlag")))
						data.put("overDueFlag", "Yes");
					data.put("ownerUserLoginId", CustRequestSrSummary.getString("ownerUserLoginId"));
					data.put("ownerUserLoginDescription", CustRequestSrSummary.getString("ownerUserLoginDescription"));
					data.put("ownerBu", CustRequestSrSummary.getString("ownerBu"));
					data.put("ownerBuName", CustRequestSrSummary.getString("ownerBuName"));
					data.put("linkedFrom", CustRequestSrSummary.getString("linkedFrom"));
					data.put("linkedTo", CustRequestSrSummary.getString("linkedTo"));
					data.put("salesOpportunityId", CustRequestSrSummary.getString("salesOpportunityId"));
					data.put("workEffortId", CustRequestSrSummary.getString("workEffortId"));
					data.put("accountType", CustRequestSrSummary.getString("accountType"));
					data.put("accountTypeDescription", CustRequestSrSummary.getString("accountTypeDescription"));
					data.put("accountNumber", CustRequestSrSummary.getString("accountNumber"));
					data.put("onceDone", CustRequestSrSummary.getString("onceDone"));
					data.put("empTeamId", CustRequestSrSummary.getString("empTeamId"));
					data.put("slaRisk", CustRequestSrSummary.getString("slaRisk"));
					data.put("slaFixed", CustRequestSrSummary.getString("slaFixed"));
					if (UtilValidate.isNotEmpty(CustRequestSrSummary.getTimestamp("createdOn"))){
						dateStr = UtilDateTime.toDateString(CustRequestSrSummary.getTimestamp("createdOn"),
								"dd/MM/yyyy hh:mm");
						data.put("createdOn", dateStr);
						dateStr = "";
					}
					data.put("createdByUserLoginId", CustRequestSrSummary.getString("createdByUserLoginId"));
					data.put("modifiedByUserLoginId", CustRequestSrSummary.getString("modifiedByUserLoginId"));
					if (UtilValidate.isNotEmpty(CustRequestSrSummary.getTimestamp("modifiedOn"))){
						dateStr = UtilDateTime.toDateString(CustRequestSrSummary.getTimestamp("modifiedOn"),
								"dd/MM/yyyy hh:mm");
						data.put("modifiedOn", dateStr);
						dateStr = "";
					}
					if (UtilValidate.isNotEmpty(CustRequestSrSummary.getTimestamp("closedOn"))){
						dateStr = UtilDateTime.toDateString(CustRequestSrSummary.getTimestamp("closedOn"),
								"dd/MM/yyyy hh:mm");
						data.put("closedOn", dateStr);
						dateStr = "";
					}
					data.put("closedBy", CustRequestSrSummary.getString("closedBy"));
					// context.put("responseObj", data);
					data.put("accessLevel", accessLevel);
					data.put("opLevel", opLevel);
					data.put("srSource",custReqSrSource);
					results.add(data);
					result.put( "results",results);
				}
			}
		} catch (Exception e) {
			Debug.logError(e, "Exception: " + e.getMessage(), MODULE);
			result.putAll( ServiceUtil.returnError(e.getMessage()));
			return result;
		}
		return result;
	}
	public static Map<String, Object> getCustomerCommunicationInfo(DispatchContext dctx, Map context) {
		Delegator delegator = (Delegator) dctx.getDelegator();
		Map<String, Object> result = new HashMap<>();
		List < Map<String, Object>> results = new ArrayList<>();
		String start = (String)context.get("start");
		String length = (String)context.get("length");
		String externalId = (String)context.get("externalId");
		String phoneNumber="";
		String emailAddr="";
		String Address="";
		String emailSolicitation="";
		String phoneSolicitation="";
		String addressSolicitation="";
		Map<String, Object> data = new HashMap<> ();
		try {
			if (UtilValidate.isNotEmpty(externalId)) {
				GenericValue CustRequestSrSummary = delegator.findOne("CustRequestSrSummary",UtilMisc.toMap("custRequestId", externalId), false);
				if (CustRequestSrSummary != null) {
					String cinNumber = CustRequestSrSummary.getString("cinNumber");
					if (cinNumber != null) {
						EntityCondition condition = EntityCondition.makeCondition(EntityOperator.OR,
								EntityCondition.makeCondition("idValue", EntityOperator.EQUALS, cinNumber));

						GenericValue partyIdentification = EntityUtil.getFirst(delegator.findList("PartyIdentification", condition, null, UtilMisc.toList("-createdStamp"), null, false));
						if (UtilValidate.isNotEmpty(partyIdentification)) {
							String partyId = partyIdentification.getString("partyId");
							if (UtilValidate.isNotEmpty(partyId)) {
								List<GenericValue> partyContactMechList = EntityQuery.use(delegator).from("PartyContactMech").where("partyId", partyId).queryList();
								List<String> contactMechIds = EntityUtil.getFieldListFromEntityList(partyContactMechList, "contactMechId", true);
								List conditionsList = FastList.newInstance();
								if (UtilValidate.isNotEmpty(contactMechIds)) {
									conditionsList.add(EntityCondition.makeCondition("contactMechId", EntityOperator.IN, contactMechIds));
								}
								EntityCondition mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);
								List < GenericValue > ContactMechList = delegator.findList("ContactMech", mainConditons, null, null, null, false);
								for (GenericValue eachContactMech: ContactMechList) {
									String contactMechTypeId = eachContactMech.getString("contactMechTypeId");
									if (contactMechTypeId.equals("EMAIL_ADDRESS")) {
										emailAddr = eachContactMech.getString("infoString");
										GenericValue partyEmailMech = EntityUtil.getFirst(delegator.findByAnd("PartyContactMech", UtilMisc.toMap("partyId",partyId,"contactMechId",eachContactMech.getString("contactMechId")), null, false));
										if(partyEmailMech != null && partyEmailMech.size() > 0) {
											emailSolicitation = UtilValidate.isNotEmpty(partyEmailMech.getString("allowSolicitation"))? partyEmailMech.getString("allowSolicitation") : "N";
										}
									}
									if (contactMechTypeId.equals("TELECOM_NUMBER")) {
										phoneNumber = eachContactMech.getString("infoString");
										GenericValue partyPhoneMech = EntityUtil.getFirst(delegator.findByAnd("PartyContactMech", UtilMisc.toMap("partyId",partyId,"contactMechId",eachContactMech.getString("contactMechId")), null, false));
										if(partyPhoneMech != null && partyPhoneMech.size() >0) {
											phoneSolicitation = UtilValidate.isNotEmpty(partyPhoneMech.getString("allowSolicitation"))? partyPhoneMech.getString("allowSolicitation") : "N";
										}
									}
									if (contactMechTypeId.equals("POSTAL_ADDRESS")) {
										Address = eachContactMech.getString("infoString");
										GenericValue partyAddressMech = EntityUtil.getFirst(delegator.findByAnd("PartyContactMech", UtilMisc.toMap("partyId",partyId,"contactMechId",eachContactMech.getString("contactMechId")), null, false));
										if(partyAddressMech != null && partyAddressMech.size() >0) {
											addressSolicitation = UtilValidate.isNotEmpty(partyAddressMech.getString("allowSolicitation"))? partyAddressMech.getString("allowSolicitation") : "N";
										}
									}
								}
								data.put("phoneNumber", phoneNumber);
								data.put("emailAddr", emailAddr);
								data.put("Address", Address);
								data.put("phoneSolicitation", phoneSolicitation);
								data.put("emailSolicitation", emailSolicitation);
								data.put("addressSolicitation", addressSolicitation);
								results.add(data);
								result.put("results", results);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			Debug.logError(e, "Exception: " + e.getMessage(), MODULE);
			result.putAll( ServiceUtil.returnError("No user login details found!"));
			return result;
		}
		return result;
	}

	public static Map<String, Object> getNoteData(DispatchContext dctx, Map context) {
		Delegator delegator = (Delegator) dctx.getDelegator();
		Map<String, Object> result = new HashMap<>();
		List<Map<String, Object>> results = new ArrayList<>();
		String noteId = (String) context.get("noteId");
		try {
			if(UtilValidate.isNotEmpty(noteId)) {
				Map<String, Object> data = new HashMap<>();
				GenericValue noteDetails = delegator.findOne("NoteData", UtilMisc.toMap("noteId",noteId),false);
				if(UtilValidate.isNotEmpty(noteDetails)) {
					data.put("noteId", noteDetails.getString("noteId"));
					data.put("noteName", noteDetails.getString("noteName"));
					data.put("noteInfo", noteDetails.getString("noteInfo"));
					data.put("moreInfoItemId",noteDetails.getString("moreInfoItemId"));
					data.put("moreInfoItemName", noteDetails.getString("moreInfoItemName"));
					data.put("moreInfoUrl", noteDetails.getString("moreInfoUrl"));
					data.put("noteParty", noteDetails.getString("noteParty"));
					data.put("createdBy", noteDetails.getString("createdByUserLogin"));
					String notePartyName="";
					String createdByName="";
					if(UtilValidate.isNotEmpty(noteDetails.getString("noteParty"))) {
						GenericValue partyNameView = delegator.findOne("Person", UtilMisc.toMap("partyId", noteDetails.getString("noteParty")), false);
						if (partyNameView != null && partyNameView.size() > 0) {
							notePartyName = partyNameView.getString("firstName")+" "+partyNameView.getString("lastName");
							data.put("notePartyName",notePartyName);
						}
					}
					if(UtilValidate.isNotEmpty(noteDetails.getString("createdByUserLogin"))) {
						GenericValue userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", noteDetails.getString("createdByUserLogin")), false);
						GenericValue partyNameView = delegator.findOne("Person", UtilMisc.toMap("partyId", userLogin.getString("partyId")), false);	
						if (partyNameView != null && partyNameView.size() > 0) {
							createdByName = partyNameView.getString("firstName")+" "+partyNameView.getString("lastName");
							data.put("createdByName",createdByName);
						}
					}
					GenericValue custRequestNote = EntityQuery.use(delegator).from("CustRequestNote").where("noteId", noteDetails.getString("noteId")).queryFirst();
					if(UtilValidate.isNotEmpty(custRequestNote)) {
						data.put("domainEntityId", UtilValidate.isNotEmpty(custRequestNote.getString("domainEntityId")) ? custRequestNote.getString("domainEntityId") : "");
						data.put("domainEntityType", UtilValidate.isNotEmpty(custRequestNote.getString("domainEntityType")) ? custRequestNote.getString("domainEntityType")  : "");
						data.put("domainEntityTypeDesc", UtilValidate.isNotEmpty(custRequestNote.getString("domainEntityType")) ? DataHelper.convertToLabel(custRequestNote.getString("domainEntityType")) : "");
						data.put("domainEntityLink",DataHelper.prepareLinkedFrom(custRequestNote.getString("domainEntityId"), custRequestNote.getString("domainEntityType"), (String) context.get("externalLoginKey")));
					}
					data.put("createdStamp", UtilValidate.isNotEmpty(noteDetails.get("createdStamp")) ? UtilDateTime.timeStampToString(noteDetails.getTimestamp("createdStamp"), "MM/dd/yyyy", TimeZone.getDefault(), null) : "");
					data.put("noteDateTime", UtilValidate.isNotEmpty(noteDetails.get("noteDateTime")) ? UtilDateTime.timeStampToString(noteDetails.getTimestamp("noteDateTime"), "MM/dd/yyyy", TimeZone.getDefault(), null) : "");
					data.put("notePartyName",notePartyName);
					data.put("createdByName",createdByName);
					results.add(data);
					result.put("results", results);
				}
			}
		} catch (Exception e) {
			Debug.logError(e, "Exception: " + e.getMessage(), MODULE);
			result.putAll( ServiceUtil.returnError(e.getMessage()));
			return result;
		}
		return result;
	}
	public static Map<String, Object> getContactDetails(DispatchContext dctx, Map context) {
		Delegator delegator = (Delegator) dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> result = new HashMap<>();
		List<Map<String, Object>> results = new ArrayList<>();
		String externalId = (String) context.get("externalId");
		String cinNumber=null;
		String partyId=null;
		String contactMechTypeId="";
		List<GenericValue> partyContactMechs = null;
		GenericValue partyIdentification = null;
		Map<String, Object> data = new HashMap<>();
		String emailAddr="";
		String phoneNumber="";
		String Address="";		
		try {
			if (UtilValidate.isNotEmpty(externalId)) {
				GenericValue CustRequestSrSummary = delegator.findOne("CustRequestSrSummary",
						UtilMisc.toMap("custRequestId",externalId), false);
				if (UtilValidate.isNotEmpty(CustRequestSrSummary)) {
					cinNumber=CustRequestSrSummary.getString("cinNumber");
					EntityCondition condition = EntityCondition.makeCondition(EntityOperator.OR,
							EntityCondition.makeCondition("idValue", EntityOperator.EQUALS, cinNumber)
							);      
					partyIdentification = EntityUtil.getFirst( delegator.findList("PartyIdentification", condition, null, UtilMisc.toList("-createdStamp"), null, false) );
					if(UtilValidate.isNotEmpty(partyIdentification)) {
						partyId = partyIdentification.getString("partyId");
					}
					if (UtilValidate.isNotEmpty(partyId)) {
						partyContactMechs = EntityQuery.use(delegator).from("PartyContactMech").where("partyId", partyId).queryList();
						List<String> contactMechIdIds = EntityUtil.getFieldListFromEntityList(partyContactMechs, "contactMechId", true);
						List conditionsList = FastList.newInstance();
						if (UtilValidate.isNotEmpty(contactMechIdIds)) {
							conditionsList.add(EntityCondition.makeCondition("contactMechId", EntityOperator.IN, contactMechIdIds));
						}
						EntityCondition mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);
						List<GenericValue> ContactMechDetails = delegator.findList("ContactMech", mainConditons, null, null, null, false);

						for (GenericValue eachContactMech : ContactMechDetails) {
							contactMechTypeId=eachContactMech.getString("contactMechTypeId");
							if(contactMechTypeId.equals("EMAIL_ADDRESS")){					
								emailAddr=eachContactMech.getString("infoString");
							}
							if(contactMechTypeId.equals("TELECOM_NUMBER")){					
								phoneNumber=eachContactMech.getString("infoString");
							}
							if(contactMechTypeId.equals("POSTAL_ADDRESS")){					
								Address=eachContactMech.getString("infoString");
							}
						}
						data.put("phoneNumber",phoneNumber);
						data.put("emailAddr",emailAddr);
						data.put("Address",Address);
						results.add(data);
						result.put("results",results);
						return result;
					}
				}
			}
		} catch (Exception e) {
			Debug.logError(e, "Exception: " + e.getMessage(), MODULE);
			result.putAll( ServiceUtil.returnError(e.getMessage()));
			return result;
		}
		return result;
	}
	public static Map<String, Object> pwebRelatedDetailsResult(DispatchContext dctx, Map context) {    		
		Delegator delegator = (Delegator) dctx.getDelegator();
		Map<String, Object> result = new HashMap<>();
		Map<String,Map<String, Object>> results = new HashMap<>();
		String externalId = (String) context.get("externalId");

		EntityCondition partyCond=null;
		EntityCondition supplCondition=null;
		EntityCondition channelCondition=null;
		String[] afterSplitcustRequestId = null;
		GenericValue CustRequestSrSummaryDetails = null;
		String finalCustRequestId=null;
		String custRequestId=null;
		String dateStr = "";

		try {
			if(UtilValidate.isNotEmpty(externalId)) {    				
				CustRequestSrSummaryDetails = delegator.findOne("CustRequestSrSummary", UtilMisc.toMap("custRequestId",externalId),false);
			}   			
			if(UtilValidate.isNotEmpty(CustRequestSrSummaryDetails)) {
				custRequestId=CustRequestSrSummaryDetails.get("custRequestId").toString();
				/*afterSplitcustRequestId=custRequestId.split("-");
				finalCustRequestId=afterSplitcustRequestId[4];	
				 */
			}
			if(UtilValidate.isNotEmpty(custRequestId)) {
				List condList = UtilMisc.toList(
						EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS,custRequestId));
				partyCond = EntityCondition.makeCondition(condList);    		
				List<GenericValue> getActivityHome =delegator.findList("CustRequest", partyCond, null, null, null, false);

				for (GenericValue Entry : getActivityHome) {
					Map<String, Object> data = new HashMap<>();   		     
					data.put("priority", Entry.get("priority"));
					data.put("topic", Entry.get("topic"));    		
					data.put("salesChannelEnumId", Entry.get("salesChannelEnumId")); 


					if(UtilValidate.isNotEmpty(Entry.getTimestamp("openDateTime")))
						dateStr = UtilDateTime.toDateString(Entry.getTimestamp("openDateTime"),"dd/MM/yyyy hh:mm");
					data.put("openDateTime", dateStr);
					if(UtilValidate.isNotEmpty(Entry.getTimestamp("closedDateTime")))
						dateStr = UtilDateTime.toDateString(Entry.getTimestamp("closedDateTime"),"dd/MM/yyyy hh:mm");
					data.put("closedDateTime", dateStr);
					results.put("CustRequest", data);
				}    		
				List condList2 = UtilMisc.toList(
						EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS,custRequestId));
				supplCondition=EntityCondition.makeCondition(condList2);
				List<GenericValue> getCustSupp =delegator.findList("CustRequestSupplementory", supplCondition, null, null, null, false);
				for (GenericValue Entry : getCustSupp) {
					Map<String, Object> data = new HashMap<>();   		     
					data.put("callOutcome", Entry.get("callOutcome"));
					data.put("opportunityId", Entry.get("opportunityId"));        		

					results.put("CustRequestSupplementory", data);
				}
				List condList3 = UtilMisc.toList(
						EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS,custRequestId));
				channelCondition=EntityCondition.makeCondition(condList3);
				List<GenericValue> getCustChannel =delegator.findList("CustRequestChannelPweb", channelCondition, null, null, null, false);

				for (GenericValue Entry : getCustChannel) {
					Map<String, Object> data = new HashMap<>();   		     
					data.put("custReqSegment", Entry.get("custReqSegment"));
					data.put("custPwebFormName", Entry.get("custPwebFormName"));
					data.put("custPwebCampaign", Entry.get("custPwebCampaign"));
					data.put("custPwebAccountType", Entry.get("custPwebAccountType"));
					data.put("custPwebConsent", Entry.get("custPwebConsent"));
					data.put("custPwebWantTo", Entry.get("custPwebWantTo"));
					data.put("custPwebWantToDetails", Entry.get("custPwebWantToDetails"));
					data.put("custPwebDisclChked", Entry.get("custPwebDisclChked"));
					data.put("custPwebInvstRange", Entry.get("custPwebInvstRange"));
					data.put("custIsExisting", Entry.get("custIsExisting"));  
					data.put("custPwebAddrLocal", Entry.get("custPwebAddrLocal"));
					data.put("custPwebPrefBranch", Entry.get("custPwebPrefBranch"));
					data.put("custPwebPrefContactMethod", Entry.get("custPwebPrefContactMethod")); 
					data.put("custPwebMktId", Entry.get("custPwebMktId")); 
					data.put("custPwebRefNo", Entry.get("custPwebRefNo")); 
					data.put("custCampaignQueue", Entry.get("custCampaignQueue"));
					if(UtilValidate.isNotEmpty(Entry.getTimestamp("custPwebSubmDate")))
						dateStr = UtilDateTime.toDateString(Entry.getTimestamp("custPwebSubmDate"),"dd/MM/yyyy hh:mm");
					data.put("closedDateTime", dateStr);
					data.put("custPwebSubmDate", dateStr); 
					data.put("custReqSegmentPweb", Entry.get("custReqSegmentPweb"));       		
					results.put("CustRequestChannelPweb", data);  
					result.put("results",results);
				}  

			}} catch (Exception e) {
				Debug.logError(e, "Exception: " + e.getMessage(), MODULE);
				result.putAll( ServiceUtil.returnError(e.getMessage()));
				return result;
			}

		return result;
	} 

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getNotesAttachments(DispatchContext dctx, Map context) {
		Delegator delegator = (Delegator) dctx.getDelegator();
		Map<String, Object> result = new HashMap<>();
		List<Map<String, Object>> results = new ArrayList<>();
		String externalId = (String) context.get("externalId");
		EntityCondition partyCond=null;
		GenericValue CustRequestSrSummaryDetails = null;
		String finalCustRequestId=null;
		String dateStr=null;

		try {

			if(UtilValidate.isNotEmpty(externalId)) {    				
				CustRequestSrSummaryDetails = delegator.findOne("CustRequestSrSummary", UtilMisc.toMap("custRequestId",externalId),false);
				if(UtilValidate.isNotEmpty(CustRequestSrSummaryDetails)) {
					finalCustRequestId=CustRequestSrSummaryDetails.get("custRequestId").toString();
				}
				if(UtilValidate.isNotEmpty(finalCustRequestId)) {
					List condtnList = UtilMisc.toList(
							EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS,finalCustRequestId));
					partyCond = EntityCondition.makeCondition(condtnList);    		
					List<GenericValue> custRequestNoteDedtails =delegator.findList("CustRequestNote", partyCond, null, UtilMisc.toList("-createdStamp"), null, false);
					List<String> noteIds = EntityUtil.getFieldListFromEntityList(custRequestNoteDedtails, "noteId", true);
					Map<String,Map<String,Object>> noteDataMap= new HashMap<String,Map<String,Object>>();
					if(UtilValidate.isNotEmpty(noteIds)){
						List<EntityExpr> exprs = UtilMisc.toList(EntityCondition.makeCondition("noteId", EntityOperator.IN,noteIds));
						EntityCondition condition = EntityCondition.makeCondition(exprs, EntityOperator.AND);
						List<GenericValue> noteData = EntityQuery.use(delegator).select("noteId","noteName","noteInfo","moreInfoItemId","moreInfoItemName","moreInfoUrl","noteType","createdStamp","noteParty","createdByUserLogin").from("NoteData").where(condition).queryList();
						if(UtilValidate.isNotEmpty(noteData)){
							for (GenericValue noteDataRow : noteData) {
								Map<String,Object> eachDataMap= new HashMap<String,Object>();
								eachDataMap.put("noteId", noteDataRow.getString("noteId"));
								eachDataMap.put("noteName", noteDataRow.getString("noteName"));
								eachDataMap.put("noteInfo", noteDataRow.getString("noteInfo"));
								eachDataMap.put("moreInfoItemId",noteDataRow.getString("moreInfoItemId"));
								eachDataMap.put("moreInfoItemName", noteDataRow.getString("moreInfoItemName"));
								eachDataMap.put("moreInfoUrl", noteDataRow.getString("moreInfoUrl"));
								eachDataMap.put("noteType", noteDataRow.getString("noteType"));
								dateStr = UtilDateTime.toDateString(noteDataRow.getTimestamp("createdStamp"),"dd/MM/yyyy hh:mm");
								eachDataMap.put("createdStamp",dateStr);
								eachDataMap.put("noteParty", noteDataRow.getString("noteParty"));
								eachDataMap.put("createdBy", noteDataRow.getString("createdByUserLogin"));
								noteDataMap.put(noteDataRow.getString("noteId"), eachDataMap);
							}
						}
					}
					for (GenericValue eachCustRequestNote : custRequestNoteDedtails) {
						String noteId=eachCustRequestNote.getString("noteId");
						Map<String, Object> data = new HashMap<>();
						if(UtilValidate.isNotEmpty(noteId)) {
							Map<String, Object> noteDetails = new HashMap<>();
							noteDetails = noteDataMap.get(noteId);
							if(UtilValidate.isNotEmpty(noteDetails)) {
								data.put("noteId",(String) noteDetails.get("noteId"));
								data.put("noteName", (String)noteDetails.get("noteName"));
								data.put("noteInfo", (String)noteDetails.get("noteInfo"));
								data.put("moreInfoItemId",(String)noteDetails.get("moreInfoItemId"));
								data.put("moreInfoItemName", (String)noteDetails.get("moreInfoItemName"));
								data.put("moreInfoUrl", (String)noteDetails.get("moreInfoUrl"));
								data.put("noteType",(String) noteDetails.get("noteType"));
								data.put("createdStamp",(String)noteDetails.get("createdStamp"));
								data.put("noteParty", (String)noteDetails.get("noteParty"));
								data.put("createdBy", (String)noteDetails.get("createdByUserLogin"));
								results.add(data);
							}
						}
					}
					result.put("results",results);
				}
			}
		} catch (Exception e) {
			Debug.logError(e, "Exception: " + e.getMessage(), MODULE);
			result.putAll( ServiceUtil.returnError(e.getMessage()));
			return result;
		}
		return result;
	}

	public static Map<String, Object> getActivityData(DispatchContext dctx, Map context) {
		Delegator delegator = (Delegator) dctx.getDelegator();
		Map<String, Object> result = new HashMap<>();
		List<Map<String, Object>> results = new ArrayList<>();
		String salesOpportunityId =(String) context.get("salesOpportunityId");
		String externalId = (String) context.get("externalId");
		List<String> workEffortIdList=null;
		try{
			if (UtilValidate.isNotEmpty(externalId)) {
				List < EntityCondition > conditions = new ArrayList<>();
				conditions.add(EntityCondition.makeCondition("externalId", EntityOperator.EQUALS, externalId));
				EntityCondition condition = EntityCondition.makeCondition(conditions, EntityOperator.AND);
				Set < String > fieldToSelect = new TreeSet < String > ();
				fieldToSelect.add("workEffortId");
				fieldToSelect.add("externalId");
				fieldToSelect.add("custRequestId");
				List < GenericValue > CustRequestSummaryList = EntityQuery.use(delegator).select(fieldToSelect).from("CustRequestSrSummary").where(condition).maxRows(100).queryList();
				workEffortIdList = EntityUtil.getFieldListFromEntityList(CustRequestSummaryList, "workEffortId", true);
			}

			if (UtilValidate.isNotEmpty(workEffortIdList)) {
				List<EntityCondition> workEffortConditionList = FastList.newInstance();
				workEffortConditionList.add(EntityCondition.makeCondition("workEffortId", EntityOperator.IN,workEffortIdList));
				EntityCondition wecondition = EntityCondition.makeCondition(workEffortConditionList, EntityOperator.AND);
				List <GenericValue> workEffortEntityData = EntityQuery.use(delegator).select("workEffortId","workEffortServiceType","workEffortSubServiceType","primOwnerId","createdStamp","estimatedStartDate","currentStatusId")
						.from("WorkEffort").orderBy("-createdStamp").where(wecondition).queryList();

				Map<String, Map<String, Object>> workEffortDetailsMap=new HashMap<>();
				for (GenericValue wf: workEffortEntityData) {
					Map<String, Object> workEffortData = new HashMap<>();
					workEffortData.put("activity", wf.getString("workEffortId"));
					workEffortData.put("activityType", wf.getString("workEffortServiceType"));
					workEffortData.put("ativitySubType", wf.getString("workEffortSubServiceType"));
					workEffortData.put("owner", wf.getString("primOwnerId"));
					workEffortData.put("createdDate", wf.getString("createdStamp"));
					workEffortData.put("plannedDate", wf.getString("estimatedStartDate"));
					workEffortData.put("status", wf.getString("currentStatusId"));
					workEffortDetailsMap.put(wf.getString("workEffortId"), workEffortData);
				}
				List<EntityCondition> callRecordMasterconditionlist = FastList.newInstance();
				callRecordMasterconditionlist.add(EntityCondition.makeCondition("workEffortId", EntityOperator.IN,workEffortIdList));
				EntityCondition callRecordMastercondition = EntityCondition.makeCondition(callRecordMasterconditionlist, EntityOperator.AND);
				List<GenericValue> callRecordMasterEntityData = EntityQuery.use(delegator).select("regardingId","workEffortId","partyId","externalReferenceId","lastCallStatusId")
						.from("CallRecordMaster").where(callRecordMastercondition).queryList();
				Map <String,Object> partyDataMap=new HashMap<String,Object>();
				Map <String,Object> personDataMap=new HashMap<String,Object>();
				List<String> partyIds = EntityUtil.getFieldListFromEntityList(callRecordMasterEntityData, "partyId", true);
				if(UtilValidate.isNotEmpty(partyIds)){
					List<EntityExpr> exprs = UtilMisc.toList(EntityCondition.makeCondition("partyId", EntityOperator.IN,partyIds));
					EntityCondition condition = EntityCondition.makeCondition(exprs, EntityOperator.AND);
					List<GenericValue> partyGroupData = EntityQuery.use(delegator).select("groupName","partyId").from("PartyGroup").where(condition).queryList();
					if(UtilValidate.isNotEmpty(partyGroupData)){
						for (GenericValue partyGroupDataRow : partyGroupData) {
							partyDataMap.put(partyGroupDataRow.getString("partyId"), partyGroupDataRow.getString("groupName"));
						}
					}else{
						List<GenericValue> personData = EntityQuery.use(delegator).select("firstName","partyId").from("Person").where(condition).queryList();
						if(UtilValidate.isNotEmpty(personData)){
							for (GenericValue personDataRow : personData) {
								personDataMap.put(personDataRow.getString("partyId"), personDataRow.getString("firstName"));
							}
						}
					}
				}

				for (GenericValue callRecordMasterEntityRow: callRecordMasterEntityData) {
					Map<String, Object> data = new HashMap<>();
					data.put("workEffortId",callRecordMasterEntityRow.getString("workEffortId") );
					data.put("regardingId", callRecordMasterEntityRow.getString("regardingId"));
					if (UtilValidate.isNotEmpty(partyDataMap)) {
						data.put("customerName",(String)partyDataMap.get(callRecordMasterEntityRow.getString("partyId")) );
					}else {
						if (UtilValidate.isNotEmpty(personDataMap)) {
							data.put("customerName",(String)personDataMap.get(callRecordMasterEntityRow.getString("partyId")));
						}else {
							data.put("customerName","");
						}
					}
					data.put("customerCIN",  callRecordMasterEntityRow.getString("externalReferenceId"));
					if(null !=workEffortDetailsMap.get(callRecordMasterEntityRow.getString("workEffortId"))) {
						Map<String,Object> tempMap=(Map<String,Object>)workEffortDetailsMap.get(callRecordMasterEntityRow.getString("workEffortId"));
						data.put("activity", tempMap.get("activity"));
						data.put("activityType",tempMap.get("activityType"));
						data.put("activitySubType",tempMap.get("ativitySubType"));
						data.put("owner",tempMap.get("owner"));
						data.put("createdDate",tempMap.get("createdDate"));
						data.put("plannedDate",tempMap.get("plannedDate"));
						data.put("status",tempMap.get("status"));
					}
					results.add(data);
				}
				result.put("results",results);
			}
		}
		catch (Exception e) {
			Debug.logError(e, "Exception: " + e.getMessage(), MODULE);
			result.putAll( ServiceUtil.returnError(e.getMessage()));
			return result;
		}
		return result;
	}

	public static Map<String, Object> getSrActivityData(DispatchContext dctx, Map context) {
		Delegator delegator = (Delegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> result = new HashMap<>();
		List<Map<String, Object>> results = new ArrayList<>();
		String custRequestId = (String) context.get("custRequestId");

		List<String> workEffortIdList=null;
		try{
			List<GenericValue> custRequestWorkEffort = EntityQuery.use(delegator).from("CustRequestWorkEffort").where("custRequestId", custRequestId).orderBy("-createdStamp").queryList();
			if (UtilValidate.isNotEmpty(custRequestWorkEffort)) {
				workEffortIdList = EntityUtil.getFieldListFromEntityList(custRequestWorkEffort, "workEffortId", true);
			}

			if (UtilValidate.isNotEmpty(workEffortIdList)) {
				List<EntityCondition> workEffortConditionList = FastList.newInstance();
				workEffortConditionList.add(EntityCondition.makeCondition("workEffortId", EntityOperator.IN,workEffortIdList));
				EntityCondition wecondition = EntityCondition.makeCondition(workEffortConditionList, EntityOperator.AND);
				List <GenericValue> workEffortEntityData = EntityQuery.use(delegator).select("workEffortId","workEffortServiceType","workEffortSubServiceType","primOwnerId","createdStamp","estimatedStartDate","currentStatusId")
						.from("WorkEffort").where(wecondition).orderBy("-createdStamp").queryList();

				Map<String, Map<String, Object>> workEffortDetailsMap=new HashMap<>();
				for (GenericValue wf: workEffortEntityData) {
					Map<String, Object> workEffortData = new HashMap<>();
					workEffortData.put("activity", wf.getString("workEffortId"));
					workEffortData.put("activityType", wf.getString("workEffortServiceType"));
					workEffortData.put("activitySubType", wf.getString("workEffortSubServiceType"));
					workEffortData.put("owner", wf.getString("primOwnerId"));
					workEffortData.put("createdDate", wf.getString("createdStamp"));
					workEffortData.put("plannedDate", wf.getString("estimatedStartDate"));
					workEffortData.put("status", wf.getString("currentStatusId"));
					workEffortDetailsMap.put(wf.getString("workEffortId"), workEffortData);
				}

				List<EntityCondition> callRecordMasterconditionlist = new ArrayList<>();
				callRecordMasterconditionlist.add(EntityCondition.makeCondition("workEffortId", EntityOperator.IN,workEffortIdList));
				EntityCondition callRecordMastercondition = EntityCondition.makeCondition(callRecordMasterconditionlist, EntityOperator.AND);
				List<GenericValue> callRecordMasterEntityData = EntityQuery.use(delegator).select("regardingId","workEffortId","partyId","externalReferenceId","lastCallStatusId")
						.from("CallRecordMaster").where(callRecordMastercondition).queryList();

				String customerName = "";
				String customerCIN = "";
				if (UtilValidate.isNotEmpty(callRecordMasterEntityData)) {
					Map <String,Object> partyDataMap=new HashMap<String,Object>();
					Map <String,Object> personDataMap=new HashMap<String,Object>();
					List<String> partyIds = EntityUtil.getFieldListFromEntityList(callRecordMasterEntityData, "partyId", true);
					if(UtilValidate.isNotEmpty(partyIds)){
						List<EntityExpr> exprs = UtilMisc.toList(EntityCondition.makeCondition("partyId", EntityOperator.IN,partyIds));
						EntityCondition condition = EntityCondition.makeCondition(exprs, EntityOperator.AND);
						List<GenericValue> partyGroupData = EntityQuery.use(delegator).select("groupName","partyId").from("PartyGroup").where(condition).queryList();
						if(UtilValidate.isNotEmpty(partyGroupData)){
							for (GenericValue partyGroupDataRow : partyGroupData) {
								partyDataMap.put(partyGroupDataRow.getString("partyId"), partyGroupDataRow.getString("groupName"));
							}
						}else{
							List<GenericValue> personData = EntityQuery.use(delegator).select("firstName","partyId").from("Person").where(condition).queryList();
							if(UtilValidate.isNotEmpty(personData)){
								for (GenericValue personDataRow : personData) {
									personDataMap.put(personDataRow.getString("partyId"), personDataRow.getString("firstName"));
								}
							}
						}
					}
					for (GenericValue callRecordMasterEntityRow : callRecordMasterEntityData) {

						if (UtilValidate.isNotEmpty(partyDataMap)) {
							customerName =(String) partyDataMap.get(callRecordMasterEntityRow.getString("partyId"));
						} else {
							if (UtilValidate.isNotEmpty(personDataMap)) {
								customerName = (String) personDataMap.get(callRecordMasterEntityRow.getString("partyId"));
							}
						}

						customerCIN = callRecordMasterEntityRow.getString("externalReferenceId");
					}
				} else {
					GenericValue custRequest = EntityQuery.use(delegator).select("fromPartyId").from("CustRequest").where("custRequestId", custRequestId).queryOne();
					GenericValue partyGroup = EntityQuery.use(delegator).from("PartyGroup").where("partyId", custRequest.getString("fromPartyId")).queryOne();
					if (UtilValidate.isNotEmpty(partyGroup)) {
						if (UtilValidate.isNotEmpty(partyGroup)) {
							customerName = partyGroup.getString("groupName");
						} else {
							GenericValue person = EntityQuery.use(delegator).from("Person").where("partyId", custRequest.getString("fromPartyId")).queryOne();
							if (UtilValidate.isNotEmpty(person)) {
								customerName = person.getString("firstName");
							}
						}
					}
					try {
						Map<String, Object> resultMap = dispatcher.runSync("crmPortal.getCINFromParty", UtilMisc.toMap("partyId", custRequest.getString("fromPartyId"), "userLogin", userLogin));
						customerCIN = (String) resultMap.get("CIN");
					} catch (GenericServiceException e) {
						Debug.logError(e, "Unable to get CIN : " + e.getMessage(), MODULE);
					}
				}
				for (String workEffortId : workEffortIdList) {
					if (UtilValidate.isNotEmpty(workEffortDetailsMap.get(workEffortId))) {
						Map<String, Object> tempMap = (Map<String, Object>) workEffortDetailsMap.get(workEffortId);
						tempMap.put("customerName", customerName);
						tempMap.put("customerCIN", customerCIN);
						tempMap.put("workEffortId", workEffortId);
						results.add(tempMap);
					}
				}
			}
			result.put("results", results);
		} catch (Exception e) {
			Debug.logError(e, "Exception: " + e.getMessage(), MODULE);
			result.putAll( ServiceUtil.returnError(e.getMessage()));
			return result;
		}
		return result;
	}
	public static Map<String, Object> saveSrReview(DispatchContext dctx, Map context) {

		Delegator delegator = (Delegator) dctx.getDelegator();
		Map<String, Object> result = new HashMap<>();
		String custRequestId = (String) context.get("custRequestId");
		String srTypeId = (String) context.get("srTypeId");
		String srCategoryId = (String) context.get("srCategoryId");
		String SRSubCategory = (String) context.get("SRSubCategory");
		String priorityStr = (String) context.get("priority");

		try {
			if(UtilValidate.isNotEmpty(custRequestId) && (UtilValidate.isNotEmpty(srTypeId) || UtilValidate.isNotEmpty(srCategoryId) || UtilValidate.isNotEmpty(SRSubCategory))) {    				
				GenericValue custRequest = delegator.findOne("CustRequest", UtilMisc.toMap("custRequestId",custRequestId),false);
				if(UtilValidate.isNotEmpty(custRequest)) {
					if(UtilValidate.isNotEmpty(srTypeId)) {
						custRequest.set("custRequestTypeId", srTypeId);
					}
					if(UtilValidate.isNotEmpty(srCategoryId)) {
						custRequest.set("custRequestCategoryId", srCategoryId);

					}
					if(UtilValidate.isNotEmpty(SRSubCategory)) {
						custRequest.set("custRequestSubCategoryId", SRSubCategory);
					}
					if(UtilValidate.isNotEmpty(priorityStr)){
						//long  priority = (Long.parseLong(priorityStr));
						custRequest.put("priority", priorityStr);
					}
					custRequest.store();
					result.put("results", "Successfully Saved SR Review Details");
				}
			}
		} catch (Exception e) {
			String errMsg = "Problem While Saving SR Review Details " + e.toString();
			Debug.logError(e, errMsg, MODULE);
			result.putAll( ServiceUtil.returnError(e.getMessage()));
			return result;
		}
		return result;
	}
	public static Map<String, Object> setLoginHistory(DispatchContext dctx, Map context) {
		Delegator delegator = (Delegator) dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> result = new HashMap<>();
		HttpSession session = (HttpSession) context.get("session");
		String seqId = (String) context.get("seqId");
		String entity = (String) context.get("entity");

		try {
			if(UtilValidate.isNotEmpty(seqId) && UtilValidate.isNotEmpty(entity)) {    				

				List<EntityCondition> conditionList = FastList.newInstance();
				if (UtilValidate.isNotEmpty(userLogin)) {
					conditionList.add(EntityCondition.makeCondition("userLoginId", EntityOperator.EQUALS, userLogin.getString("userLoginId")));
				}
				if (UtilValidate.isNotEmpty(seqId)) {
					conditionList.add( EntityCondition.makeCondition("seqId", EntityOperator.EQUALS, seqId) );
				}
				String visitId = VisitHandler.getVisitId(session);
				if (UtilValidate.isNotEmpty(visitId)) {
					conditionList.add( EntityCondition.makeCondition("visitId", EntityOperator.EQUALS, visitId) );
				}
				if (UtilValidate.isNotEmpty(entity)) {
					conditionList.add( EntityCondition.makeCondition("entity", EntityOperator.EQUALS, entity) );
				}
				EntityCondition condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
				List<GenericValue> UserLoginHistoryList = EntityQuery.use(delegator).from("UserLoginHistory").
						where(condition).orderBy("-fromDate").maxRows(5).queryList();
				if(UtilValidate.isEmpty(UserLoginHistoryList)) {
					Map<String, Object> ulhCreateMap = UtilMisc.toMap("userLoginId", userLogin.getString("userLoginId"), "visitId", visitId,
							"fromDate", UtilDateTime.nowTimestamp(), "successfulLogin", "Y","entity",entity,"seqId",seqId);

					ModelEntity modelUserLogin = userLogin.getModelEntity();
					if (modelUserLogin.isField("partyId")) {
						ulhCreateMap.put("partyId", userLogin.get("partyId"));
					}
					delegator.create("UserLoginHistory", ulhCreateMap);
				}else{

				}
				result.put("results", "Successfully Saved Login History Details");
			}
		} catch (Exception e) {
			String errMsg = "Problem While Saving Login History Details " + e.toString();
			Debug.logError(e, errMsg, MODULE);
			result.put("_ERROR_MESSAGE_", errMsg);
			return result;
		}
		return result;
	}
	public static Map<String, Object> findSRCustomers(DispatchContext dctx, Map context) {
		Delegator delegator = (Delegator) dctx.getDelegator();
		Map<String, Object> result = new HashMap<>();
		List<Map<String, Object>> results = new ArrayList<>();
		String start = (String) context.get("start");
		String length = (String) context.get("length");
		String roleTypeId = (String) context.get("roleTypeId");
		String cinNumber = (String) context.get("cinNumber");
		String name = (String) context.get("name");
		String dob = (String) context.get("dob");
		String uid = (String) context.get("uid");
		String cName = (String) context.get("cName");
		String email = (String) context.get("email");
		String account = (String) context.get("account");
		String apNo = (String) context.get("apNo");
		String phone = (String) context.get("phone");

		EntityFindOptions efo = new EntityFindOptions();
		efo.setDistinct(true);
		int startInx = UtilValidate.isNotEmpty(start) ? Integer.parseInt(start) : 0;
		int endInx = UtilValidate.isNotEmpty(length) ? Integer.parseInt(length) : 2000;
		efo.setOffset(startInx);
		efo.setMaxRows(endInx);

		if(UtilValidate.isEmpty(roleTypeId)){
			roleTypeId="CUSTOMER";
		}
		try{

			List<EntityCondition> conditions = FastList.newInstance();
			if(UtilValidate.isNotEmpty(cinNumber)) {
				conditions.add(EntityCondition.makeCondition("idValue",EntityOperator.EQUALS,cinNumber));
			}
			if(UtilValidate.isNotEmpty(roleTypeId) && "ALL".equals(roleTypeId)) {
				conditions.add(EntityCondition.makeCondition("roleTypeId",EntityOperator.IN,UtilMisc.toList("CUSTOMER","PROSPECT","NON_CRM")));
			}else{
				conditions.add(EntityCondition.makeCondition("roleTypeId",EntityOperator.EQUALS,roleTypeId));
			}
			if(UtilValidate.isNotEmpty(name)) {
				conditions.add(EntityCondition.makeCondition("firstName",EntityOperator.EQUALS,name));
			}
			if(UtilValidate.isNotEmpty(uid)) {
				conditions.add(EntityCondition.makeCondition("uniqueIDNumber",EntityOperator.EQUALS,uid));
			}
			if(UtilValidate.isNotEmpty(cName)) {
				//conditions.add(EntityCondition.makeCondition("chineseName",EntityOperator.EQUALS,cName));
			}
			if(UtilValidate.isNotEmpty(apNo)) {
				//conditions.add(EntityCondition.makeCondition("portfolioNumber",EntityOperator.EQUALS,apNo));
			}
			if(UtilValidate.isNotEmpty(email)) {
				//conditions.add(EntityCondition.makeCondition("emailId",EntityOperator.EQUALS,email));
			}
			if(UtilValidate.isNotEmpty(phone)) {
				//conditions.add(EntityCondition.makeCondition("phoneNumber",EntityOperator.EQUALS,phone));
			}
			if(UtilValidate.isNotEmpty(account)) {
				//conditions.add(EntityCondition.makeCondition("accountNumber",EntityOperator.EQUALS,account));
			}
			if(UtilValidate.isNotEmpty(dob)) {
				SimpleDateFormat df1 = new SimpleDateFormat("dd/MM/yyyy");
				SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
				dob = df2.format(df1.parse(dob));
				conditions.add(EntityCondition.makeCondition("birthDate", EntityOperator.EQUALS, java.sql.Date.valueOf(dob)));
			}
			conditions.add(EntityCondition.makeCondition("partyIdentificationTypeId",EntityOperator.EQUALS,"CIF"));
			EntityCondition condition = EntityCondition.makeCondition(conditions,EntityOperator.AND);;
			if(UtilValidate.isNotEmpty(condition)) {
				List<GenericValue> PartyRolePersonAndPartyIdendificationList = EntityQuery.use(delegator).from("PartyRolePersonAndPartyIdentification").
						where(condition).maxRows(endInx).queryList();

				if (UtilValidate.isNotEmpty(PartyRolePersonAndPartyIdendificationList)) {
					for (GenericValue Entry: PartyRolePersonAndPartyIdendificationList) {
						Map<String, Object> data = new HashMap<>();
						data.put("cin", Entry.getString("idValue"));
						data.put("suffix", Entry.getString("suffix"));
						data.put("name", Entry.getString("firstName"));
						data.put("customerType", Entry.getString("roleTypeId"));
						data.put("uid", Entry.getString("uniqueIDNumber"));
						//data.put("phone", "phone");
						//data.put("email", Entry.getString("email"));
						if(UtilValidate.isNotEmpty(Entry.getDate("birthDate"))){
							data.put("dob",UtilDateTime.toDateString(Entry.getDate("birthDate"),"dd/MM/yyyy"));
						}
						results.add(data);
					}
					result.put("results", results);
				}
			}

		}
		catch (Exception e) {
			Debug.logError(e, "Exception: " + e.getMessage(), MODULE);
			result.put("_ERROR_MESSAGE_", e.toString());
		}
		return result;
	}
	public static Map<String, Object> UpdateReasignActivity(DispatchContext dctx, Map context) {

		Delegator delegator = (Delegator) dctx.getDelegator();
		Map<String, Object> result = new HashMap<>();
		List<String> results = new ArrayList<String>();
		String primOwnerId = (String) context.get("primOwnerId");
		String workEffortId = (String) context.get("workEffortId");
		String emplTeamId = (String) context.get("emplTeamId");

		try {

			GenericValue callRecordMaster = EntityUtil.getFirst(delegator.findList("CallRecordMaster", EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId), null, UtilMisc.toList("-createdStamp"), null, false));
			if (UtilValidate.isNotEmpty(primOwnerId)) {
				Set<String> fieldsToSelect = new TreeSet<String>();
				fieldsToSelect.add("primOwnerId");
				fieldsToSelect.add("workEffortId");
				GenericValue updateConfigRecords = EntityUtil.getFirst(delegator.findByAnd("WorkEffort", UtilMisc.toMap("workEffortId", workEffortId), null, false));
				if (UtilValidate.isNotEmpty(updateConfigRecords)) {
					GenericValue emplTeam = EntityUtil.getFirst(EntityQuery.use(delegator).select(UtilMisc.toSet("emplTeamId","partyId","businessUnit")).from("EmplTeam").where("partyId", primOwnerId).queryList());
					if (UtilValidate.isNotEmpty(emplTeam)) {    
						String primOwnerTeamId = emplTeam.getString("emplTeamId");
						if (UtilValidate.isNotEmpty(primOwnerTeamId)) {   
							updateConfigRecords.put("emplTeamId", primOwnerTeamId);
						}
						String primOwnerBusinessUnitId = emplTeam.getString("businessUnit");
						if (UtilValidate.isNotEmpty(primOwnerBusinessUnitId)) {   
							updateConfigRecords.put("businessUnitId", primOwnerBusinessUnitId);
							if (UtilValidate.isNotEmpty(callRecordMaster)) {
								callRecordMaster.put("ownerBusinessUnit", primOwnerBusinessUnitId);
							}
						}
					}
					updateConfigRecords.put("primOwnerId", primOwnerId);
					updateConfigRecords.store();
					if (UtilValidate.isNotEmpty(callRecordMaster)) {
						callRecordMaster.put("ownerId", primOwnerId);
						callRecordMaster.store();
					}
					result.put("_EVENT_MESSAGE_", "Data updated succesfully with user");
					String data = null;;
					data= "Data updated succesfully with user";
					results.add(data);
					result.put("results",results);
				}
			}

			if (UtilValidate.isNotEmpty(emplTeamId)) {
				Set<String> fieldsToSelect = new TreeSet<String>();
				fieldsToSelect.add("emplTeamId");
				GenericValue updateConfigRecords = EntityUtil.getFirst(delegator.findByAnd("WorkEffort", UtilMisc.toMap("workEffortId", workEffortId), null, false));
				if (UtilValidate.isNotEmpty(updateConfigRecords)) {
					Debug.log("workEffortId coming is update loop" + workEffortId);
					updateConfigRecords.put("primOwnerId", "");
					updateConfigRecords.put("emplTeamId", emplTeamId);
					updateConfigRecords.store();
					if (UtilValidate.isNotEmpty(callRecordMaster)) {
						callRecordMaster.put("ownerId", "");
						callRecordMaster.store();
					}
					String data = null;
					result.put("_EVENT_MESSAGE_", "Data updated succesfully with user");
					data= "Data updated succesfully with team";
					results.add(data);
					result.put("results",results);
				}
			}
		} catch (Exception e) {
			String errMsg = "" + e.toString();
			Debug.logError(e, errMsg, MODULE);
			String data = null;;
			data= errMsg;
			results.add(data);
			result.put("results",results);
			return result;

		}
		return result;
	}
	public static Map<String, Object> loadTemplate(DispatchContext dctx, Map context) {
		Delegator delegator = (Delegator) dctx.getDelegator();
		Map<String, Object> result = new HashMap<>();
		List<Map<String, Object>> results = new ArrayList<>();
		String templateId = (String) context.get("templateId");
		Map<String, Object> data = new HashMap<>();
		data.put("templateId", templateId);
		try{
			GenericValue templateMaster = delegator.findOne("TemplateMaster", UtilMisc.toMap("templateId", templateId), false);

			String templateFormContent = templateMaster.getString("templateFormContent");
			String textContent = templateMaster.getString("textContent");
			if (UtilValidate.isNotEmpty(templateFormContent)) {
				if (Base64.isBase64(templateFormContent)) {
					templateFormContent = org.ofbiz.base.util.Base64.base64Decode(templateFormContent);
				}
			}

			data.put("template", templateFormContent);
			data.put("textContent", textContent);
			results.add(data);
			result.put("results", results);
		}
		catch (Exception e) {
			Debug.logError(e, "Exception: " + e.getMessage(), MODULE);
			result.put("_ERROR_MESSAGE_", e.getMessage());
			return result;
		}
		return result;
	}
	public static Map<String, Object> getOwnerTeam(DispatchContext dctx, Map context){
		Delegator delegator = (Delegator) dctx.getDelegator();
		Map<String, Object> result = new HashMap<>();
		List<Map<String, Object>> results = new ArrayList<>();
		String emplTeamId = (String) context.get("emplTeamId");
		String businessUnitId = (String) context.get("businessUnitId");
		try {
			if (UtilValidate.isNotEmpty(emplTeamId)) {
				List<GenericValue> userLoginPersonList = EntityQuery.use(delegator).select("userLoginId","partyId","firstName").from("UserLoginPerson").where("emplTeamId", emplTeamId).queryList();
				if (UtilValidate.isNotEmpty(userLoginPersonList)) {
					for (GenericValue userLoginPerson : userLoginPersonList) {
						Map<String, Object> data = new HashMap<>();
						data.put("userLoginId", UtilValidate.isNotEmpty(userLoginPerson.getString("userLoginId"))? userLoginPerson.getString("userLoginId") : "");
						data.put("partyName", UtilValidate.isNotEmpty(userLoginPerson.getString("firstName"))? userLoginPerson.getString("firstName") : "");
						data.put("partyId", UtilValidate.isNotEmpty(userLoginPerson.getString("partyId"))? userLoginPerson.getString("partyId") : "");
						results.add(data);
					}
				}
			}

			if (UtilValidate.isNotEmpty(businessUnitId)) {
				List<GenericValue> emplTeamList = EntityQuery.use(delegator).from("EmplTeam").where("businessUnit", businessUnitId).queryList();
				if (UtilValidate.isNotEmpty(emplTeamList)) {
					for (GenericValue emplTeam : emplTeamList) {
						Map<String, Object> data = new HashMap<>();
						data.put("emplTeamId", UtilValidate.isNotEmpty(emplTeam.getString("emplTeamId"))? emplTeam.getString("emplTeamId") : "");
						data.put("teamName", UtilValidate.isNotEmpty(emplTeam.getString("teamName"))? emplTeam.getString("teamName") : "");
						results.add(data);
					}
				}
			}
			result.put("results", results);

		} catch (Exception e) {
			Debug.logError(e, "Exception: " + e.getMessage(), MODULE);
			result.put("_ERROR_MESSAGE_", e.getMessage());
			return result;
		}
		return result;
	}
	public static Map<String, Object> getRecentlyViewedDetails(DispatchContext dctx, Map context) {
		Delegator delegator = (Delegator) dctx.getDelegator();
		String entity = (String) context.get("entity");
		GenericValue userLogin=(GenericValue) context.get("userLogin");
		Map<String, Object> result = new HashMap<>();
		Map<String, Object> results = new HashMap<>();
		if (UtilValidate.isEmpty(userLogin)) {
			Map<String, Object> data = new HashMap<>();
			data.put("_ERROR_MESSAGE_", "No user login details found!");
			result.put("results", data);
			return  result;
		}
		try {
			if( UtilValidate.isNotEmpty(entity)) {    				
				List<EntityCondition> conditionList = FastList.newInstance();
				if (UtilValidate.isNotEmpty(userLogin)) {
					conditionList.add(EntityCondition.makeCondition("userLoginId", EntityOperator.EQUALS, userLogin.getString("userLoginId")));
				}
				if (UtilValidate.isNotEmpty(entity)) {
					conditionList.add( EntityCondition.makeCondition("entity", EntityOperator.EQUALS, entity) );
				}
				EntityCondition condition=EntityCondition.makeCondition(conditionList,EntityOperator.AND);
				List<GenericValue> UserLoginHistoryList = EntityQuery.use(delegator).from("UserLoginHistory").select("seqId").
						where(condition).orderBy("-fromDate").maxRows(5).queryList();
				if(UtilValidate.isNotEmpty(UserLoginHistoryList)) {
					results.put("data", UserLoginHistoryList);
					result.put("results", results);
				}
			}
		} catch (Exception e) {
			String errMsg = "Problem While  fetching recently viewed sr Details" + e.toString();
			Debug.logError(e, errMsg, MODULE);
			result.put("_ERROR_MESSAGE_", errMsg);
			return result;
		}
		return  result;
	}   
	@SuppressWarnings("unchecked")
	public static Map<String, Object> viewServiceActivityDetails(DispatchContext dctx, Map context) {
		Delegator delegator = (Delegator) dctx.getDelegator();
		String entity = (String) context.get("entity");
		GenericValue userLogin=(GenericValue) context.get("userLogin");
		Map<String, Object> result = new HashMap<>();
		List < Map<String, Object>> results = new ArrayList<>();
		String workEffortId = (String) context.get("workEffortId");
		Timestamp date = null;
		List<EntityCondition> conditions = new ArrayList<EntityCondition>();
		List <GenericValue> enumListData = null;
		Map<String, Object> enumIdMapDesc = new HashMap<>();
		List<EntityCondition> conditionlist1 = FastList.newInstance();
		if (UtilValidate.isEmpty(userLogin)) {
			Map<String, Object> data = new HashMap<>();
			data.put("_ERROR_MESSAGE_", "No user login details found!");
			result.put("results", data);
			return  result;
		}

		try {
			conditionlist1.add(EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, "IA_TYPE"));
			EntityCondition condition1 = EntityCondition.makeCondition(conditionlist1, EntityOperator.AND);
			enumListData = delegator.findList("Enumeration", condition1,null, null, null, false);
			for (GenericValue Enum: enumListData) {
				String enumId=Enum.getString("enumId");
				String description=Enum.getString("description");
				enumIdMapDesc.put(enumId,description);
			}
			List<EntityCondition> conditionsList = FastList.newInstance();
			if (UtilValidate.isNotEmpty(workEffortId)) {
				EntityCondition tempTypeCondition = EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS,
						workEffortId);
				conditionsList.add(tempTypeCondition);
			}
			EntityCondition condition = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);
			EntityFindOptions efo = new EntityFindOptions();
			efo.setDistinct(true);
			efo.setOffset(0);
			efo.setLimit(1000);
			efo.setMaxRows(1000);
			List<GenericValue> workEffortAssocTripletList = EntityQuery.use(delegator).select("value","code").from("WorkEffortAssocTriplet").where("type", "Type").queryList();
			List<GenericValue> workEffortAssocTripletSubList = EntityQuery.use(delegator).select("value","code").from("WorkEffortAssocTriplet").where("type", "SubType").queryList();
			List<GenericValue> enumerationList = EntityQuery.use(delegator).select("enumId","description").from("Enumeration").where("enumTypeId", "priority_level").queryList();
			Map<String, Object> workEffortTypeMap = new HashMap<>();
			Map<String, Object> workEffortSubTypeMap = new HashMap<>();
			Map<String, Object> enumerationMap = new HashMap<>();
			if(UtilValidate.isNotEmpty(workEffortAssocTripletList)){
				for(GenericValue each : workEffortAssocTripletList){
					workEffortTypeMap.put(each.getString("code"), each.getString("value"));
				}
			}
			if(UtilValidate.isNotEmpty(workEffortAssocTripletSubList)){
				for(GenericValue each : workEffortAssocTripletSubList){
					workEffortSubTypeMap.put(each.getString("code"), each.getString("value"));
				}
			}
			if(UtilValidate.isNotEmpty(enumerationList)){
				for(GenericValue each : enumerationList){
					enumerationMap.put(each.getString("enumId"), each.getString("description"));
				}
			}
			List<GenericValue> getActivityDetail = delegator.findList("WorkEffort", condition, null,UtilMisc.toList("workEffortId DESC"), efo, false);
			List<String> workEffortIds = EntityUtil.getFieldListFromEntityList(getActivityDetail, "workEffortId", true);
			List<String> primOwnerIds = EntityUtil.getFieldListFromEntityList(getActivityDetail, "primOwnerId", true);
			List<EntityCondition> primOwnerConditionList = FastList.newInstance();
			primOwnerConditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, primOwnerIds));
			EntityCondition primOwnerCondition = EntityCondition.makeCondition(primOwnerConditionList, EntityOperator.AND);
			List<GenericValue> personList = EntityQuery.use(delegator).select("partyId","firstName").from("Person").where(primOwnerCondition).queryList();
			Map<String, String> WorkEffortOwnerMap = new HashMap<String, String>();
			if (UtilValidate.isNotEmpty(personList)) {
				for (GenericValue eachEntry: personList) {
					WorkEffortOwnerMap.put(eachEntry.getString("partyId"),eachEntry.getString("firstName"));
				}
			}
			Map<String,Object> callRecordMasterDataMap= new HashMap<String,Object>();
			Map<String,Object> callRecordDetailsDataMap= new HashMap<String,Object>();
			Map<String,Object> personsDataMap= new HashMap<String,Object>();
			if(UtilValidate.isNotEmpty(workEffortIds)){
				List<EntityExpr> exprs = UtilMisc.toList(EntityCondition.makeCondition("workEffortId", EntityOperator.IN,workEffortIds));
				EntityCondition callCondition = EntityCondition.makeCondition(exprs, EntityOperator.AND);
				List<GenericValue> callRecordMasterData = EntityQuery.use(delegator).select("workEffortId","partyId","externalReferenceTypeId","regardingId").from("CallRecordMaster").where(callCondition).queryList();
				List<String> partyIds = EntityUtil.getFieldListFromEntityList(callRecordMasterData, "partyId", true);
				if(UtilValidate.isNotEmpty(callRecordMasterData)){
					for (GenericValue eachDataRow : callRecordMasterData) {
						Map<String,Object> eachDataMap= new HashMap<String,Object>();
						eachDataMap.put("partyId", eachDataRow.getString("partyId"));
						eachDataMap.put("externalReferenceTypeId",eachDataRow.getString("externalReferenceTypeId"));
						eachDataMap.put("regardingId", eachDataRow.getString("regardingId"));
						callRecordMasterDataMap.put(eachDataRow.getString("workEffortId"), eachDataMap);
					}
				}
				if(UtilValidate.isNotEmpty(partyIds)){
					exprs = UtilMisc.toList(EntityCondition.makeCondition("partyId", EntityOperator.IN,partyIds));
					callCondition = EntityCondition.makeCondition(exprs, EntityOperator.AND);
					List<GenericValue> callRecordDetailsData = EntityQuery.use(delegator).select("csrPartyId","partyId","callStartTime","callEndTime","callDuration").from("CallRecordDetails").where(callCondition).queryList();
					for (GenericValue eachDataRow : callRecordDetailsData) {
						Map<String,Object> eachDataMap= new HashMap<String,Object>();
						eachDataMap.put("partyId", eachDataRow.getString("partyId"));
						eachDataMap.put("externalReferenceTypeId",eachDataRow.getString("externalReferenceTypeId"));
						eachDataMap.put("regardingId", eachDataRow.getString("regardingId"));
						callRecordDetailsDataMap.put(eachDataRow.getString("partyId"), eachDataMap);
					}
					List<GenericValue> personDetailsData = EntityQuery.use(delegator).select("partyId","nationalId" ).from("Person").where(callCondition).queryList();
					for (GenericValue eachDataRow : personDetailsData) {
						Map<String,Object> eachDataMap= new HashMap<String,Object>();
						eachDataMap.put("partyId", eachDataRow.getString("partyId"));
						eachDataMap.put("nationalId",eachDataRow.getString("nationalId"));
						personsDataMap.put(eachDataRow.getString("partyId"), eachDataMap);
					}
				}
			}
			for (GenericValue Entry : getActivityDetail) {
				Map<String, Object> data = new HashMap<>();
				data.put("workEffortId", UtilValidate.isNotEmpty(Entry) ? Entry.getString("workEffortId") : "");
				data.put("workEffortServiceType",
						UtilValidate.isNotEmpty(Entry) ? Entry.getString("workEffortServiceType") : "");
				data.put("workEffortSubServiceType",
						UtilValidate.isNotEmpty(Entry) ? Entry.getString("workEffortSubServiceType") : "");
				String workEffortServiceType = Entry.getString("workEffortServiceType");
				data.put("workEffortServiceTypeDescription",
						UtilValidate.isNotEmpty(workEffortTypeMap) ? workEffortTypeMap.get(workEffortServiceType) : "");
				String workEffortSubServiceType = Entry.getString("workEffortSubServiceType");
				data.put("workEffortSubServiceTypeDescription",
						UtilValidate.isNotEmpty(workEffortSubTypeMap) ?  workEffortSubTypeMap.get(workEffortSubServiceType) : "");
				data.put("workEffortName", UtilValidate.isNotEmpty(Entry) ? Entry.getString("workEffortName") : "");
				data.put("direction", UtilValidate.isNotEmpty(Entry) ? Entry.getString("direction") : "");
				data.put("phoneNumber", UtilValidate.isNotEmpty(Entry) ? Entry.getString("phoneNumber") : "");
				data.put("description", UtilValidate.isNotEmpty(Entry) ? Entry.getString("description") : "");
				data.put("accountNumber", UtilValidate.isNotEmpty(Entry) ? Entry.getString("accountNumber") : "");
				data.put("wfOnceDone", UtilValidate.isNotEmpty(Entry) ? Entry.getString("wfOnceDone") : "");
				data.put("currentStatusId", UtilValidate.isNotEmpty(Entry) ? Entry.getString("currentStatusId") : "");
				data.put("businessUnitName", UtilValidate.isNotEmpty(Entry) ? Entry.getString("businessUnitName") : "");
				data.put("primOwnerId", UtilValidate.isNotEmpty(Entry) ? Entry.getString("primOwnerId") : "");
				data.put("estimatedStartDate", UtilValidate.isNotEmpty(Entry) ? Entry.getString("estimatedStartDate") : "");
				data.put("createdStamp", UtilValidate.isNotEmpty(Entry) ? Entry.getString("createdStamp") : "");
				data.put("createdByUserLogin", UtilValidate.isNotEmpty(Entry) ? Entry.getString("createdByUserLogin") : "");
				data.put("lastModifiedByUserLogin", UtilValidate.isNotEmpty(Entry) ? Entry.getString("lastModifiedByUserLogin") : "");
				data.put("lastModifiedDate", UtilValidate.isNotEmpty(Entry) ? Entry.getString("lastModifiedDate") : "");
				data.put("lastUpdatedStamp", UtilValidate.isNotEmpty(Entry) ? Entry.getString("lastUpdatedStamp") : "");
				data.put("lastUpdatedTxStamp", UtilValidate.isNotEmpty(Entry) ? Entry.getString("lastUpdatedTxStamp") : "");
				data.put("createdByUserLogin", UtilValidate.isNotEmpty(Entry) ? Entry.getString("createdByUserLogin") : "");
				data.put("closedByUserLogin", UtilValidate.isNotEmpty(Entry) ? Entry.getString("closedByUserLogin") : "");
				data.put("duration", UtilValidate.isNotEmpty(Entry) ? Entry.getString("duration") : "");
				data.put("emplTeamId", UtilValidate.isNotEmpty(Entry) ? Entry.getString("emplTeamId") : "");
				data.put("businessUnitName", UtilValidate.isNotEmpty(Entry) ? Entry.getString("businessUnitName") : "");
				data.put("businessUnitId", UtilValidate.isNotEmpty(Entry) ? Entry.getString("businessUnitId") : "");
				if (UtilValidate.isNotEmpty(Entry)) {
					if (UtilValidate.isNotEmpty(Entry.getString("priority"))) {
						data.put("priority", enumerationMap.get(Entry.getString("priority")));
					}
				}
				if (UtilValidate.isNotEmpty(Entry.getString("primOwnerId"))) {
					if (UtilValidate.isNotEmpty(WorkEffortOwnerMap.get(Entry.getString("primOwnerId")))) {
						data.put("primOwnerName", WorkEffortOwnerMap.get(Entry.getString("primOwnerId")));
					}
				}
				//results.add(data);
				String primaryworkEffortId = Entry.getString("workEffortId");
				if (UtilValidate.isNotEmpty(callRecordMasterDataMap)) {
					Map<String,Object> eachDataMap= new HashMap<String,Object>();
					eachDataMap=(Map<String,Object>)callRecordMasterDataMap.get(primaryworkEffortId);
					data.put("partyId",UtilValidate.isNotEmpty(eachDataMap) ? eachDataMap.get("partyId") : "");
					data.put("externalReferenceTypeId",UtilValidate.isNotEmpty(eachDataMap)?  eachDataMap.get("externalReferenceTypeId"): "");
					data.put("regardingId",UtilValidate.isNotEmpty(eachDataMap) ?  eachDataMap.get("regardingId"): "");
					//results.add(data);
					String callRecordMasterPartyId =(String) eachDataMap.get("partyId");
					eachDataMap=(Map<String,Object>)callRecordDetailsDataMap.get(callRecordMasterPartyId);
					if (UtilValidate.isNotEmpty(eachDataMap)) {
						data.put("csrPartyId",UtilValidate.isNotEmpty(eachDataMap) ? eachDataMap.get("csrPartyId") : "");
						data.put("callStartTime",UtilValidate.isNotEmpty(eachDataMap) ? eachDataMap.get("callStartTime"): "");
						data.put("callEndTime",	UtilValidate.isNotEmpty(eachDataMap) ? eachDataMap.get("callEndTime") : "");
						data.put("callDuration",UtilValidate.isNotEmpty(eachDataMap) ? eachDataMap.get("callDuration"): "");
						//results.add(data);
					}
					eachDataMap=(Map<String,Object>)personsDataMap.get(callRecordMasterPartyId);
					if (UtilValidate.isNotEmpty(eachDataMap)) {
						data.put("nationalId",UtilValidate.isNotEmpty(eachDataMap) ? eachDataMap.get("nationalId") : "");
						//results.add(data);
					}
				}
				results.add(data);
			}
			result.put("results",results);
		} catch (Exception e) {
			Debug.logError(e, "Exception: " + e.getMessage(), MODULE);
			Map<String, Object> data = new HashMap<>();
			data.put("_ERROR_MESSAGE_", e.getMessage());
			results.add(data);
			result.put("results", results);
			return result;
		}
		return result;
	}
	private static String getStringValue (GenericValue genericValue, String name) {
		if (UtilValidate.isNotEmpty(genericValue)) {
			return UtilValidate.isNotEmpty(genericValue.getString(name)) ? genericValue.getString(name) : "";
		}
		return "";
	}
	
	public static Map<String, Object> createSrHistory(DispatchContext dctx, Map context) {
		Delegator delegator = (Delegator) dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> result = new HashMap<>();
		String custRequestId = (String) context.get("custRequestId");
		@SuppressWarnings("unchecked")
		Map<String, Object> contextMap = (Map<String, Object>) context.get("contextMap");
		try {
			String orderId = UtilValidate.isNotEmpty(contextMap) && UtilValidate.isNotEmpty(contextMap.get("orderId")) ? (String) contextMap.get("orderId") : "";
			String comment = UtilValidate.isNotEmpty(contextMap) && UtilValidate.isNotEmpty(contextMap.get("comment")) ? (String) contextMap.get("comment") : "";
			if(UtilValidate.isNotEmpty(custRequestId)) {    				

				GenericValue custRequest = delegator.findOne("CustRequest", UtilMisc.toMap("custRequestId",custRequestId),false);

				GenericValue custRequestHistory = delegator.makeValue("CustRequestHistory");
				String custRequestHistoryId = delegator.getNextSeqId("CustRequestHistory");

				custRequestHistory.put("custRequestId", custRequestId);
				custRequestHistory.put("custRequestHistoryId", custRequestHistoryId);

				if (UtilValidate.isNotEmpty(custRequest)){

					if(UtilValidate.isNotEmpty(custRequest.getString("custRequestName"))){
						String custRequestName = custRequest.getString("custRequestName");
						custRequestHistory.put("custRequestName", custRequestName);
					}

					if (UtilValidate.isNotEmpty(custRequest.getString("custRequestTypeId"))) {
						String custRequestTypeId = custRequest.getString("custRequestTypeId");
						custRequestHistory.put("custRequestTypeId", custRequestTypeId);
					}

					if (UtilValidate.isNotEmpty(custRequest.getString("custRequestCategoryId"))) {
						String custRequestCategoryId = custRequest.getString("custRequestCategoryId");
						custRequestHistory.put("custRequestCategoryId", custRequestCategoryId);
					}

					if (UtilValidate.isNotEmpty(custRequest.getString("custRequestSubCategoryId"))) {
						String custRequestSubCategoryId = custRequest.getString("custRequestSubCategoryId");
						custRequestHistory.put("custRequestSubCategoryId", custRequestSubCategoryId);
					}

					if (UtilValidate.isNotEmpty(custRequest.getString("priority"))) {
						String priority = custRequest.getString("priority");
						custRequestHistory.put("priority", priority);
					}

					if (UtilValidate.isNotEmpty(custRequest.getString("statusId"))) {
						String statusId = custRequest.getString("statusId");
						custRequestHistory.put("statusId", statusId);
					}

					if(UtilValidate.isNotEmpty(custRequest.getString("custReqSrSource"))){
						String custReqSrSource = custRequest.getString("custReqSrSource");
						custRequestHistory.put("custReqSrSource", custReqSrSource);
					}

					if(UtilValidate.isNotEmpty(custRequest.getString("custOrderId"))){
						String custOrderId = custRequest.getString("custOrderId");
						custRequestHistory.put("custOrderId", custOrderId);
					}

					if(UtilValidate.isNotEmpty(custRequest.get("description"))){
						custRequestHistory.put("description", custRequest.get("description"));
					}

					if(UtilValidate.isNotEmpty(custRequest.get("description"))){
						custRequestHistory.put("resolution", custRequest.get("resolution"));
					}

					custRequestHistory.put("custReqPrimaryContact", DataHelper.getSrPrimaryContact(delegator, custRequestId));

					if(UtilValidate.isNotEmpty(custRequest.getString("responsiblePerson"))){
						String responsiblePerson = custRequest.getString("responsiblePerson");
						custRequestHistory.put("ownerId", responsiblePerson);
					}

					if (UtilValidate.isNotEmpty(custRequest.get("createdDate"))) {
						custRequestHistory.put("createdDate", custRequest.get("createdDate"));
					} else {
						Timestamp createdDate = UtilDateTime.nowTimestamp();
						custRequestHistory.put("createdDate", createdDate);
					}

					if (UtilValidate.isNotEmpty(custRequest.getString("createdByUserLogin"))) {
						custRequestHistory.put("createdByUserLogin", custRequest.getString("createdByUserLogin"));
					} 

					if (UtilValidate.isNotEmpty(custRequest.get("lastModifiedDate"))) {
						custRequestHistory.put("lastModifiedDate", custRequest.get("lastModifiedDate"));
					} else {
						Timestamp lastModifiedDate = UtilDateTime.nowTimestamp();
						custRequestHistory.put("lastModifiedDate", lastModifiedDate);
					}

					if (UtilValidate.isNotEmpty(custRequest.get("lastModifiedByUserLogin"))) {
						custRequestHistory.put("lastModifiedByUserLogin", custRequest.get("lastModifiedByUserLogin"));
					} else {
						custRequestHistory.put("lastModifiedByUserLogin", userLogin.getString("userLoginId"));
					} 

					if(UtilValidate.isNotEmpty(custRequest.getString("statusId")) && ("SR_CLOSED".equals(custRequest.getString("statusId")) || "SR_CANCELLED".equals(custRequest.getString("statusId")))) {
						Timestamp closedByDate = (Timestamp) custRequest.get("closedByDate");
						if (UtilValidate.isEmpty(closedByDate)) {
							closedByDate = UtilDateTime.nowTimestamp();
						}
						custRequestHistory.put("closedByUserLogin",userLogin.getString("userLoginId"));
						custRequestHistory.put("closedByDate",closedByDate);
					}
					
					if (UtilValidate.isNotEmpty(orderId)) {
						custRequestHistory.put("custOrderId", orderId);
					}
					
					if (UtilValidate.isNotEmpty(comment)) {
						custRequestHistory.put("comment", comment);
					} 
					custRequestHistory.create();
					result.put("results", "Successfully Saved SR History Details");
				}
			}
		} catch (Exception e) {
			String errMsg = "Problem While Saving SR History Details " + e.toString();
			Debug.logError(e, errMsg, MODULE);
			result.put("_ERROR_MESSAGE_", errMsg);
			return result;
		}
		return result;
	}
	
	public static Map<String, Object> validateSrContactInformation(DispatchContext dctx, Map context) {
		
		Delegator delegator = (Delegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> result = new HashMap<>();
		
		Map<String, Object> contactInformationContext = (Map<String, Object>) context.get("contactInformationContext");
		
		try {
			String customerId = (String) contactInformationContext.get("customerId");
			
			if(UtilValidate.isNotEmpty(customerId)) {
				
				String homePhoneNumberParam = ParamUtil.getString(contactInformationContext, "homePhoneNumberParam");
				String offPhoneNumberParam = ParamUtil.getString(contactInformationContext, "offPhoneNumberParam");
				String mobilePhoneNumberParam = ParamUtil.getString(contactInformationContext, "mobilePhoneNumberParam");
				String primaryEmailParam = ParamUtil.getString(contactInformationContext, "primaryEmailParam");
				
                EntityCondition conditionPCM = EntityCondition.makeCondition(UtilMisc.toList(
                        EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, customerId),
                        EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null)), EntityOperator.AND);

                List <GenericValue> PartyContactMechList = EntityQuery.use(delegator).select("contactMechId","partyId").from("PartyContactMech").where(conditionPCM).queryList();
                List<String> partyContactMechIds = EntityUtil.getFieldListFromEntityList(PartyContactMechList, "contactMechId", true);
                
				List<EntityCondition> conditionsListTele = new ArrayList<EntityCondition>();
				conditionsListTele.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, customerId));
				conditionsListTele.add(EntityCondition.makeCondition("contactMechId", EntityOperator.IN, partyContactMechIds));
				conditionsListTele.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.IN, UtilMisc.toList("PHONE_HOME","PHONE_MOBILE","PHONE_WORK", "PRIMARY_PHONE","PRIMARY_EMAIL")));

				EntityCondition mainConditonsTele = EntityCondition.makeCondition(conditionsListTele, EntityOperator.AND);
				List<GenericValue> PartyContactMechPurposeList = delegator.findList("PartyContactMechPurpose", mainConditonsTele, null, null, null, false);
				
				List<String> telecomContactMechIds = null;
				if(UtilValidate.isNotEmpty(PartyContactMechPurposeList)) {
					List<GenericValue> telecomPartyContactMechPurposeList = EntityUtil.filterByCondition(PartyContactMechPurposeList, EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.IN, UtilMisc.toList("PRIMARY_PHONE","PHONE_HOME","PHONE_MOBILE","PHONE_WORK")));
					if(UtilValidate.isNotEmpty(telecomPartyContactMechPurposeList)) {
						telecomContactMechIds = EntityUtil.getFieldListFromEntityList(telecomPartyContactMechPurposeList, "contactMechId", true);
					}
				}
				List<String> emailContactMechIds = null;
				if(UtilValidate.isNotEmpty(PartyContactMechPurposeList)) {
					List<GenericValue> emailPartyContactMechPurposeList = EntityUtil.filterByCondition(PartyContactMechPurposeList, EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "PRIMARY_EMAIL"));
					if(UtilValidate.isNotEmpty(emailPartyContactMechPurposeList)) {
						emailContactMechIds = EntityUtil.getFieldListFromEntityList(emailPartyContactMechPurposeList, "contactMechId", true);
					}
				}
				
				Map<String, Object> contactMechIdWithPurposeTypeMap = new HashMap<String, Object>();
				if (UtilValidate.isNotEmpty(PartyContactMechPurposeList)) {
					for (GenericValue eachEntry: PartyContactMechPurposeList) {
						String contactMechId = eachEntry.getString("contactMechId");
						String contactMechPurposeTypeId = eachEntry.getString("contactMechPurposeTypeId");
						contactMechIdWithPurposeTypeMap.put(contactMechId,contactMechPurposeTypeId);
					}
				}
				
				List<GenericValue> telecomNumberList = null;
				List<GenericValue> emailContactMechList = null;
				
				if (UtilValidate.isNotEmpty(telecomContactMechIds)) {
					telecomNumberList = EntityQuery.use(delegator).select("contactMechId","contactNumber").from("TelecomNumber").where(EntityCondition.makeCondition("contactMechId", EntityOperator.IN, telecomContactMechIds)).queryList();
				}
				if (UtilValidate.isNotEmpty(emailContactMechIds)) {
					emailContactMechList = EntityQuery.use(delegator).select("contactMechId","infoString").from("ContactMech").where(EntityCondition.makeCondition("contactMechId", EntityOperator.IN, emailContactMechIds)).queryList();
				}
				Map<String, Object> contactMechValuesMap = new HashMap<String, Object>();
				if (UtilValidate.isNotEmpty(telecomNumberList)) {
					for (GenericValue eachEntry: telecomNumberList) {
						String contactNumber = eachEntry.getString("contactNumber");
						String contactMechId = eachEntry.getString("contactMechId");
						contactMechValuesMap.put(contactMechId, contactNumber);
					}
				}
				
				if (UtilValidate.isNotEmpty(emailContactMechList)) {
					for (GenericValue eachEntry: emailContactMechList) {
						String contactNumber = eachEntry.getString("infoString");
						String contactMechId = eachEntry.getString("contactMechId");
						contactMechValuesMap.put(contactMechId, contactNumber);
					}
				}
				
				String homePhoneNumber = "";
				String offPhoneNumber = "";
				String mobilePhoneNumber = "";
				String primaryEmail = "";
				String homePhoneNumberContactMechId = "";
				String offPhoneNumberContactMechId = "";
				String mobilePhoneNumberContactMechId = "";
				String emailContactMechId = "";
				
				for (Map.Entry<String,Object> entry : contactMechValuesMap.entrySet()){
					String contactMechId = entry.getKey();
					String contactMechValue = (String) entry.getValue();
					String contactMechPurposeTypeId = (String) contactMechIdWithPurposeTypeMap.get(contactMechId);
					
					if("PRIMARY_PHONE".equals(contactMechPurposeTypeId)){
						homePhoneNumber = contactMechValue;
						homePhoneNumberContactMechId = contactMechId;
					}else if("PHONE_MOBILE".equals(contactMechPurposeTypeId)){
						mobilePhoneNumber = contactMechValue;
						mobilePhoneNumberContactMechId = contactMechId;
					}else if("PHONE_WORK".equals(contactMechPurposeTypeId)){
						offPhoneNumber = contactMechValue;
						offPhoneNumberContactMechId = contactMechId;
					}else if("PRIMARY_EMAIL".equals(contactMechPurposeTypeId)){
						primaryEmail = contactMechValue;
						emailContactMechId = contactMechId;
					}
				}
				
				String isSrContactInfoOverrideEnable = "Y";
				GenericValue srContactInfoGlobal = EntityQuery.use(delegator).from("PretailLoyaltyGlobalParameters").where("parameterId","SR_CONTACT_INFO_OVERRIDE").queryFirst();
				if(UtilValidate.isNotEmpty(srContactInfoGlobal)) {
					isSrContactInfoOverrideEnable = srContactInfoGlobal.getString("value");
				}
				
				if (UtilValidate.isNotEmpty(homePhoneNumberParam)){
					if (UtilValidate.isNotEmpty(homePhoneNumber)){
						Boolean homeTelecomNumberChanged = true;
						if (homePhoneNumber.equals(homePhoneNumberParam.trim())){
							homeTelecomNumberChanged = false;
						}
						if(homeTelecomNumberChanged){
				            if(!"Y".equals(isSrContactInfoOverrideEnable)) { // Expiring Old Contact
				            	if(UtilValidate.isNotEmpty(customerId) && UtilValidate.isNotEmpty(homePhoneNumberContactMechId)){
				            		Map<String,Object> deletePartyContactMech = dispatcher.runSync("deletePartyContactMech", UtilMisc.toMap("partyId",customerId,"contactMechId",homePhoneNumberContactMechId,"userLogin",userLogin));
				            		if(ServiceUtil.isSuccess(deletePartyContactMech)){
				            			Debug.logFatal("Party Contact mech exipired successfully...!", MODULE);
				            		}	
				            	}
				            	
				            	Map<String, Object> telecomNumber = UtilMisc.toMap("partyId", customerId, "userLogin", (Object) userLogin, "contactMechTypeId", "TELECOM_NUMBER", "contactMechPurposeTypeId", "PRIMARY_PHONE");
				            	telecomNumber.put("contactNumber", homePhoneNumberParam);
				            	
				            	Map<String, Object> serviceResultss = dispatcher.runSync("createPartyTelecomNumber", telecomNumber);
				            	
				            	if (ServiceUtil.isError(serviceResultss)) {
				            		result.put("_ERROR_MESSAGE_", "Problem While creating Phone Data");
				            		return result;
				            	}
				            }else{
				            	// Override Existing Contact
				            	Map < String, Object > serviceResults = null;
				            	Map<String, Object> telecomUpdateMap = new HashMap<String, Object>();
				            	telecomUpdateMap.put("partyId", customerId);
				            	telecomUpdateMap.put("contactMechId", homePhoneNumberContactMechId);
				            	telecomUpdateMap.put("contactNumber", homePhoneNumberParam.trim());
				            	telecomUpdateMap.put("userLogin", userLogin);
				            	
				            	serviceResults = dispatcher.runSync("updatePartyTelecomNumber", telecomUpdateMap);	
				            	
				            	if (ServiceUtil.isError(serviceResults)) {
				            		result.put("_ERROR_MESSAGE_", "Problem While updating Phone Data");
				            		return result;
				            	}
				            }
						}
					}else{
						Map<String, Object> telecomNumber = UtilMisc.toMap("partyId", customerId, "userLogin", (Object) userLogin, "contactMechTypeId", "TELECOM_NUMBER", "contactMechPurposeTypeId", "PRIMARY_PHONE");
						telecomNumber.put("contactNumber", homePhoneNumberParam);
						
						Map<String, Object> serviceResults = dispatcher.runSync("createPartyTelecomNumber", telecomNumber);
						
						if (ServiceUtil.isError(serviceResults)) {
							result.put("_ERROR_MESSAGE_", "Problem While creating Phone Data");
							return result;
						}
					}
				}else{
					if(UtilValidate.isNotEmpty(customerId) && UtilValidate.isNotEmpty(homePhoneNumberContactMechId)){
	            		Map<String,Object> deletePartyContactMech = dispatcher.runSync("deletePartyContactMech", UtilMisc.toMap("partyId",customerId,"contactMechId",homePhoneNumberContactMechId,"userLogin",userLogin));
	            		if(ServiceUtil.isSuccess(deletePartyContactMech)){
	            			Debug.logFatal("Party Contact mech exipired successfully...!", MODULE);
	            		}	
	            	}
				}
				
				if (UtilValidate.isNotEmpty(offPhoneNumberParam)){
					if (UtilValidate.isNotEmpty(offPhoneNumber)){
						Boolean offTelecomNumberChanged = true;
						if (homePhoneNumber.equals(offPhoneNumberParam.trim())){
							offTelecomNumberChanged = false;
						}
						if(offTelecomNumberChanged){
				            if(!"Y".equals(isSrContactInfoOverrideEnable)) {
				            	if(UtilValidate.isNotEmpty(customerId) && UtilValidate.isNotEmpty(offPhoneNumberContactMechId)){
				            		Map<String,Object> deletePartyContactMech = dispatcher.runSync("deletePartyContactMech", UtilMisc.toMap("partyId",customerId,"contactMechId",offPhoneNumberContactMechId,"userLogin",userLogin));
				            		if(ServiceUtil.isSuccess(deletePartyContactMech)){
				            			Debug.logFatal("Party Contact mech exipired successfully...!", MODULE);
				            		}	
				            	}
				            	Map<String, Object> telecomNumber = UtilMisc.toMap("partyId", customerId, "userLogin", (Object) userLogin, "contactMechTypeId", "TELECOM_NUMBER", "contactMechPurposeTypeId", "PHONE_WORK");
								telecomNumber.put("contactNumber", offPhoneNumberParam);
								Map<String, Object> serviceResults = dispatcher.runSync("createPartyTelecomNumber", telecomNumber);
								if (ServiceUtil.isError(serviceResults)) {
									result.put("_ERROR_MESSAGE_", "Problem While creating Phone Data");
									return result;
								}
				            	
				            }else{

				            	Map < String, Object > serviceResults = null;
				            	Map<String, Object> telecomUpdateMap = new HashMap<String, Object>();
				            	telecomUpdateMap.put("partyId", customerId);
				            	telecomUpdateMap.put("contactMechId", offPhoneNumberContactMechId);
				            	telecomUpdateMap.put("contactNumber", offPhoneNumberParam.trim());
				            	telecomUpdateMap.put("userLogin", userLogin);
				            	serviceResults = dispatcher.runSync("updatePartyTelecomNumber", telecomUpdateMap);	
				            	if (ServiceUtil.isError(serviceResults)) {
				            		result.put("_ERROR_MESSAGE_", "Problem While updating Phone Data");
				            		return result;
				            	}
				            }
						}
					}else{
						Map<String, Object> telecomNumber = UtilMisc.toMap("partyId", customerId, "userLogin", (Object) userLogin, "contactMechTypeId", "TELECOM_NUMBER", "contactMechPurposeTypeId", "PHONE_WORK");
						telecomNumber.put("contactNumber", offPhoneNumberParam);
						Map<String, Object> serviceResults = dispatcher.runSync("createPartyTelecomNumber", telecomNumber);
						if (ServiceUtil.isError(serviceResults)) {
							result.put("_ERROR_MESSAGE_", "Problem While creating Phone Data");
							return result;
						}
					}
				}else{
					if(UtilValidate.isNotEmpty(customerId) && UtilValidate.isNotEmpty(offPhoneNumberContactMechId)){
	            		Map<String,Object> deletePartyContactMech = dispatcher.runSync("deletePartyContactMech", UtilMisc.toMap("partyId",customerId,"contactMechId",offPhoneNumberContactMechId,"userLogin",userLogin));
	            		if(ServiceUtil.isSuccess(deletePartyContactMech)){
	            			Debug.logFatal("Party Contact mech exipired successfully...!", MODULE);
	            		}	
	            	}
				}
				
				if (UtilValidate.isNotEmpty(mobilePhoneNumberParam)){
					if (UtilValidate.isNotEmpty(mobilePhoneNumber)){
						Boolean mobileTelecomNumberChanged = true;
						if (mobilePhoneNumber.equals(mobilePhoneNumberParam.trim())){
							mobileTelecomNumberChanged = false;
						}
						if(mobileTelecomNumberChanged){
				            if(!"Y".equals(isSrContactInfoOverrideEnable)) {
				            	if(UtilValidate.isNotEmpty(customerId) && UtilValidate.isNotEmpty(mobilePhoneNumberContactMechId)){
				            		Map<String,Object> deletePartyContactMech = dispatcher.runSync("deletePartyContactMech", UtilMisc.toMap("partyId",customerId,"contactMechId",mobilePhoneNumberContactMechId,"userLogin",userLogin));
				            		if(ServiceUtil.isSuccess(deletePartyContactMech)){
				            			Debug.logFatal("Party Contact mech exipired successfully...!", MODULE);
				            		}	
				            	}
				            	Map<String, Object> telecomNumber = UtilMisc.toMap("partyId", customerId, "userLogin", (Object) userLogin, "contactMechTypeId", "TELECOM_NUMBER", "contactMechPurposeTypeId", "PHONE_MOBILE");
								telecomNumber.put("contactNumber", mobilePhoneNumberParam);
								Map<String, Object> serviceResults = dispatcher.runSync("createPartyTelecomNumber", telecomNumber);
								if (ServiceUtil.isError(serviceResults)) {
									result.put("_ERROR_MESSAGE_", "Problem While creating Phone Data");
									return result;
								}
				            }else{
				            	Map < String, Object > serviceResults = null;
				            	Map<String, Object> telecomUpdateMap = new HashMap<String, Object>();
				            	telecomUpdateMap.put("partyId", customerId);
				            	telecomUpdateMap.put("contactMechId", mobilePhoneNumberContactMechId);
				            	telecomUpdateMap.put("contactNumber", mobilePhoneNumberParam.trim());
				            	telecomUpdateMap.put("userLogin", userLogin);
				            	serviceResults = dispatcher.runSync("updatePartyTelecomNumber", telecomUpdateMap);	
				            	if (ServiceUtil.isError(serviceResults)) {
				            		result.put("_ERROR_MESSAGE_", "Problem While updating Phone Data");
				            		return result;
				            	}
				            }
						}
					}else{
						Map<String, Object> telecomNumber = UtilMisc.toMap("partyId", customerId, "userLogin", (Object) userLogin, "contactMechTypeId", "TELECOM_NUMBER", "contactMechPurposeTypeId", "PHONE_MOBILE");
						telecomNumber.put("contactNumber", mobilePhoneNumberParam);
						Map<String, Object> serviceResults = dispatcher.runSync("createPartyTelecomNumber", telecomNumber);
						if (ServiceUtil.isError(serviceResults)) {
							result.put("_ERROR_MESSAGE_", "Problem While creating Phone Data");
							return result;
						}
					}
				}else{
					if(UtilValidate.isNotEmpty(customerId) && UtilValidate.isNotEmpty(mobilePhoneNumberContactMechId)){
	            		Map<String,Object> deletePartyContactMech = dispatcher.runSync("deletePartyContactMech", UtilMisc.toMap("partyId",customerId,"contactMechId",mobilePhoneNumberContactMechId,"userLogin",userLogin));
	            		if(ServiceUtil.isSuccess(deletePartyContactMech)){
	            			Debug.logFatal("Party Contact mech exipired successfully...!", MODULE);
	            		}	
	            	}
				}
				
				if (UtilValidate.isNotEmpty(primaryEmailParam)){
					if (UtilValidate.isNotEmpty(primaryEmail)){
						Boolean primaryEmailChanged = true;
						if (primaryEmail.equals(primaryEmailParam.trim())){
							primaryEmailChanged = false;
						}
						if(primaryEmailChanged){
				            if(!"Y".equals(isSrContactInfoOverrideEnable)) {
				            	if(UtilValidate.isNotEmpty(customerId) && UtilValidate.isNotEmpty(emailContactMechId)){
				            		Map<String,Object> deletePartyContactMech = dispatcher.runSync("deletePartyContactMech", UtilMisc.toMap("partyId",customerId,"contactMechId",emailContactMechId,"userLogin",userLogin));
				            		if(ServiceUtil.isSuccess(deletePartyContactMech)){
				            			Debug.logFatal("Party Contact mech exipired successfully...!", MODULE);
				            		}	
				            	}
				            	Map < String, Object > inputEmail = UtilMisc.toMap("userLogin", userLogin, "emailAddress", primaryEmailParam, "partyId", customerId, "contactMechTypeId", "EMAIL_ADDRESS", "contactMechPurposeTypeId", "PRIMARY_EMAIL", "allowSolicitation", "Y");
		                        Map < String, Object > serviceResults = dispatcher.runSync("createPartyEmailAddress", inputEmail);
								
								if (ServiceUtil.isError(serviceResults)) {
									result.put("_ERROR_MESSAGE_", "Problem While creating Email Data");
									return result;
								}
								
				            }else{
				            	
				            	Map < String, Object > serviceResults = null;
				            	Map<String, Object> emailUpdateMap = new HashMap<String, Object>();
				            	emailUpdateMap.put("partyId", customerId);
				            	emailUpdateMap.put("contactMechId", emailContactMechId);
				            	emailUpdateMap.put("emailAddress", primaryEmailParam);
				            	emailUpdateMap.put("allowSolicitation", "Y");
				            	emailUpdateMap.put("userLogin", userLogin);
				            	serviceResults = dispatcher.runSync("updatePartyEmailAddress", emailUpdateMap);	
				            	
				            	if (ServiceUtil.isError(serviceResults)) {
				            		result.put("_ERROR_MESSAGE_", "Problem While updating Email Data");
				            		return result;
				            	}
				            }
						}
					}else{
						Map < String, Object > inputEmail = UtilMisc.toMap("userLogin", userLogin, "emailAddress", primaryEmailParam, "partyId", customerId, "contactMechTypeId", "EMAIL_ADDRESS", "contactMechPurposeTypeId", "PRIMARY_EMAIL", "allowSolicitation", "Y");
                        Map < String, Object > serviceResults = dispatcher.runSync("createPartyEmailAddress", inputEmail);
						
						if (ServiceUtil.isError(serviceResults)) {
							result.put("_ERROR_MESSAGE_", "Problem While creating Email Data");
							return result;
						}
					}
				}else{
					if(UtilValidate.isNotEmpty(customerId) && UtilValidate.isNotEmpty(emailContactMechId)){
	            		Map<String,Object> deletePartyContactMech = dispatcher.runSync("deletePartyContactMech", UtilMisc.toMap("partyId",customerId,"contactMechId",emailContactMechId,"userLogin",userLogin));
	            		if(ServiceUtil.isSuccess(deletePartyContactMech)){
	            			Debug.logFatal("Party Contact mech exipired successfully...!", MODULE);
	            		}	
	            	}
				}
				
			}
		} catch (Exception e) {
			String errMsg = "Problem While Saving SR Contact Details " + e.toString();
			Debug.logError(e, errMsg, MODULE);
			result.put("_ERROR_MESSAGE_", errMsg);
			return result;
		}
		return result;
	}
	
}
