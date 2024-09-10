package org.groupfio.common.portal.util;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;

import org.fio.homeapps.constants.GlobalConstants.DateTimeTypeConstant;
import org.groupfio.common.portal.CommonPortalConstants;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;

import javolution.util.FastList;

/**
 * 
 * @author Arshiya
 * @since 06-09-2019
 * 
 */
public class ReportUtil {

    private static String MODULE = ReportUtil.class.getName();
    public static final String RESOURCE = "ReportPortalUiLabels";

    public static String getBusinessUnitName(Delegator delegator, String productStoreGroupId) {
		
    	String buName = null;
		try {
			GenericValue producStoreGroup = EntityQuery.use(delegator).from("ProductStoreGroup").where("productStoreGroupId",productStoreGroupId).queryOne();
			if(UtilValidate.isNotEmpty(producStoreGroup)){
				buName = producStoreGroup.getString("productStoreGroupName");
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		
		return buName;
	}
	
	//Desc to get once and done against owner bu
	public static int getOnceDoneCustReq (Delegator delegator, String ownerBu) {
		int onceDoneCnt = 0;
		try {
			List conditionsList = FastList.newInstance();
			EntityFindOptions efo = new EntityFindOptions();
			efo.setDistinct(true);
			
			if (UtilValidate.isNotEmpty(ownerBu)) {
				conditionsList.add(EntityCondition.makeCondition("ownerBu", EntityOperator.EQUALS, ownerBu));
			}
			conditionsList.add(EntityCondition.makeCondition("custRequestTypeId", EntityOperator.NOT_EQUAL,CommonPortalConstants.SR_COMPLAINT));
			conditionsList.add(EntityCondition.makeCondition("statusId", EntityOperator.IN,UtilMisc.toList("SR_CLOSED", "SR_CANCELLED")));
			EntityCondition mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);
			
			List<GenericValue> onceDoneList = EntityQuery.use(delegator).select("custReqOnceDone").from("CustRequest").where(mainConditons).queryList();
			
			if (UtilValidate.isNotEmpty(onceDoneList)) {
				for(GenericValue onceDoneGv : onceDoneList) {
					String onceDone = onceDoneGv.getString("custReqOnceDone");
					if(UtilValidate.isNotEmpty(onceDone)) {
						onceDoneCnt = onceDoneCnt + Integer.parseInt(onceDone);
					}
				}
			}
			
		} catch (Exception e) {
			Debug.logError(e, MODULE);
		}
		
		return onceDoneCnt;
		
	}

	//Desc get calculated data for open SR details
	public static List < Map < String, Object >> getSlaCalculatedData(Delegator delegator, List<GenericValue> custRequests, String businessUnit, HttpServletRequest request) {

        List < Map < String, Object >> slaResults = new ArrayList < Map < String, Object >> ();
        try {
            Timestamp systemTime = UtilDateTime.nowTimestamp();
            
            /*SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd");
    		Calendar cal = Calendar.getInstance();
            cal.setTime(java.sql.Date.valueOf(systemTime.toString()));
            cal.add(Calendar.DATE, -1);
            String sinceCallDate = formatter1.format(cal.getTime()) +" 00:00:00";*/
            /*Timestamp srEndDate = null;*/
            //Based on enum configuration
            List<String> ownerBuId  = new ArrayList<String>();
            List<GenericValue> srEnumList = EntityQuery.use(delegator).from("Enumeration").where("enumTypeId","SR_REPORT").orderBy("sequenceId").queryList();
            int onceDone = DataUtil.getOnceDoneCustReq(delegator, businessUnit);
            BigDecimal  srWithinCloseSla = getClosedSrSla(delegator,"SR_CLOSED" ,"WITH_IN_SLA");
            BigDecimal  srBeyondCloseSla = getClosedSrSla(delegator,"SR_CLOSED","BEYOND_SLA");
            BigDecimal  srOpenSla = getOpenSrBeyondSla(delegator,"SR_OPEN");
            Double resEffeciency = 0.0;
            if(UtilValidate.isNotEmpty(srWithinCloseSla) && !srWithinCloseSla.equals(BigDecimal.ZERO)) {
                BigDecimal res = srWithinCloseSla.add(srBeyondCloseSla).add(srOpenSla);
                resEffeciency = srWithinCloseSla.doubleValue() / res.doubleValue();
            }
            if(UtilValidate.isNotEmpty(custRequests)) {
                for(GenericValue srEnum : srEnumList) {
                    if(UtilValidate.isNotEmpty(srEnumList)) {
                    	long sPlus3 = 0, sPlus10 = 0, sPlus11 = 0, sPlus16 = 0, sPlus21 = 0, sPlus31 = 0, sPlus50 = 0, total = 0;
                    	Map < String, Object> slaCalculatedMap = new LinkedHashMap < > ();
                        for(GenericValue custRequest  : custRequests) {
                            if(UtilValidate.isNotEmpty(custRequest)) {
                                
                                if(UtilValidate.isNotEmpty(custRequest.getString("ownerBu")) /*&& !ownerBuId.contains(custRequest.getString("ownerBu"))*/) {
                                
                                String custRequestId = custRequest.getString("custRequestId");
                                GenericValue custRequestSuppl = EntityQuery.use(delegator).from("CustRequestSupplementory").select("commitDate").where("custRequestId", custRequestId).queryOne();
                                if(UtilValidate.isNotEmpty(custRequestSuppl)) {
                                Timestamp dueDate = null;
                                Timestamp srCreatedDate = null;
                                Timestamp srClosedDate = null;
                                srCreatedDate = custRequest.getTimestamp("createdDate");
                                if(UtilValidate.isNotEmpty(custRequestSuppl)) {
                                    dueDate = custRequestSuppl.getTimestamp("commitDate");
                                }
                                srClosedDate = custRequest.getTimestamp("closedByDate");
                                slaCalculatedMap.put("slaReportType", srEnum.getString("description"));
                                slaCalculatedMap.put("ownerBuId", custRequest.getString("ownerBu"));
                                slaCalculatedMap.put("ownerBu", DataUtil.getBusinessUnitName(delegator, custRequest.getString("ownerBu")));
                                long dateDiff = 0;
                                long dateDiffP3 = 0, dateDiffP10 = 0, dateDiffP11 = 0, dateDiffP16 = 0, dateDiffP21 = 0, dateDiffP31 = 0, dateDiffP50 = 0;
                                
                                long countDiff = 0;
                                
                                if(UtilValidate.isNotEmpty(srEnum) && "CLOSED_WITH_IN_SLA".equals(srEnum.getString("enumId")) ) {
                                    /*if("SR_CLOSED".equals(custRequest.getString("statusId")) || "SR_CANCELLED".equals(custRequest.getString("statusId"))) {
                                        if(UtilValidate.isNotEmpty(dueDate) && UtilValidate.isNotEmpty(srClosedDate) && srClosedDate.before(dueDate)) {
                                           // onceDone = DataUtil.getOnceDoneCustReq(delegator, custRequest.getString("ownerBu"), srEnum.getString("enumId"));
                                        }
                                        //dateDiff = getWorkingDayCountBetweenDates(delegator,custRequest.getString("createdDate"),"CLOSED_WITH_IN_SLA");
                                    }
                                    total = delegator.findCountByCondition("CustRequest", EntityCondition.makeCondition("statusId", EntityOperator.IN, UtilMisc.toList("SR_CLOSED", "SR_CANCELLED")), null, null);*/
                                } else if(UtilValidate.isNotEmpty(srEnum) && "CLOSED_BEYOND_SLA".equals(srEnum.getString("enumId")) ) {
                                    /*if( ("SR_CLOSED".equals(custRequest.getString("statusId")) || "SR_CANCELLED".equals(custRequest.getString("statusId")))) {
                                    if(UtilValidate.isNotEmpty(dueDate) && UtilValidate.isNotEmpty(srClosedDate) && srClosedDate.after(dueDate)) {
                                            //onceDone = DataUtil.getOnceDoneCustReq(delegator, custRequest.getString("ownerBu"), srEnum.getString("enumId"));
                                           // total = delegator.findCountByCondition("CustRequest", EntityCondition.makeCondition("statusId", EntityOperator.IN, UtilMisc.toList("SR_OPEN", "SR_PENDING")), null, null);
                                        }
                                    }*/
                                    resEffeciency = 0.0;
                                } else if(UtilValidate.isNotEmpty(srEnum) && "OPEN_BEYOND_SLA".equals(srEnum.getString("enumId"))  ) {
                                    /*if(!"SR_CLOSED".equals(custRequest.getString("statusId")) && !"SR_CANCELLED".equals(custRequest.getString("statusId"))) {
                                    	if(UtilValidate.isNotEmpty(dueDate) && systemTime.after(dueDate)) {
                                            //onceDone = DataUtil.getOnceDoneCustReq(delegator, custRequest.getString("ownerBu"), srEnum.getString("enumId"));
                                    	}
                                       // total = delegator.findCountByCondition("CustRequest", EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN, UtilMisc.toList("SR_CLOSED", "SR_CANCELLED")), null, null);
                                    }*/
                                    resEffeciency = 0.0;
                                   //dateDiff = getWorkingDayCountBetweenDates(delegator,custRequest.getTimestamp("createdDate"),"OPEN_BEYOND_SLA");
                                } else if(UtilValidate.isNotEmpty(srEnum) && "OPEN_WITH_IN_SLA".equals(srEnum.getString("enumId")) ) {
                                    /*if(!"SR_CLOSED".equals(custRequest.getString("statusId")) && !"SR_CANCELLED".equals(custRequest.getString("statusId"))) {
                                        if(UtilValidate.isNotEmpty(dueDate) && systemTime.before(dueDate)) {
                                            //onceDone = DataUtil.getOnceDoneCustReq(delegator, custRequest.getString("ownerBu"), srEnum.getString("enumId"));
                                        }
                                        //total = delegator.findCountByCondition("CustRequest", EntityCondition.makeCondition("statusId", EntityOperator.NOT_IN, UtilMisc.toList("SR_CLOSED", "SR_CANCELLED")), null, null);   
                                    }*/
                                    resEffeciency = 0.0;
                                }
                                slaCalculatedMap.put("onceDone", onceDone);
                                slaCalculatedMap.put("resolutioinEfficiency", resEffeciency);
                                Timestamp subtractedDate3 =  getPrevBusinessDateBeforeSystem(3);
                                Timestamp subtractedDate10 = getPrevBusinessDateBeforeSystem(10);
                                Timestamp subtractedDate11 = getPrevBusinessDateBeforeSystem(15);
                                Timestamp subtractedDate16 = getPrevBusinessDateBeforeSystem(20);
                                Timestamp subtractedDate21 = getPrevBusinessDateBeforeSystem(30);
                                Timestamp subtractedDate31 = getPrevBusinessDateBeforeSystem(50);
                                Timestamp subtractedDate50 = getPrevBusinessDateBeforeSystem(100);
                                
                                dateDiffP3 =  getWorkingDayCountBetweenDates(delegator,subtractedDate3,custRequest.getString("ownerBu"),custRequest.getString("custRequestId"),srEnum.getString("enumId"), dueDate,srClosedDate,3);
                                dateDiffP10 = getWorkingDayCountBetweenDates(delegator,subtractedDate10,custRequest.getString("ownerBu"),custRequest.getString("custRequestId"),srEnum.getString("enumId"),dueDate,srClosedDate,10);
                                dateDiffP11 = getWorkingDayCountBetweenDates(delegator,subtractedDate11,custRequest.getString("ownerBu"),custRequest.getString("custRequestId"),srEnum.getString("enumId"),dueDate,srClosedDate,15);
                                dateDiffP16 = getWorkingDayCountBetweenDates(delegator,subtractedDate16,custRequest.getString("ownerBu"),custRequest.getString("custRequestId"),srEnum.getString("enumId"),dueDate,srClosedDate,20);
                                dateDiffP21 = getWorkingDayCountBetweenDates(delegator,subtractedDate21,custRequest.getString("ownerBu"),custRequest.getString("custRequestId"),srEnum.getString("enumId"),dueDate,srClosedDate,30);
                                dateDiffP31 = getWorkingDayCountBetweenDates(delegator,subtractedDate31,custRequest.getString("ownerBu"),custRequest.getString("custRequestId"),srEnum.getString("enumId"),dueDate,srClosedDate,50);
                                dateDiffP50 = getWorkingDayCountBetweenDates(delegator,subtractedDate50,custRequest.getString("ownerBu"),custRequest.getString("custRequestId"),srEnum.getString("enumId"),dueDate,srClosedDate,60);
                                
                                sPlus3 =  sPlus3 + dateDiffP3;
                                sPlus10 = sPlus10 + dateDiffP10;
                                sPlus11 = sPlus11 + dateDiffP11;
                                sPlus16 = sPlus16 + dateDiffP16;
                                sPlus21 = sPlus21 + dateDiffP21;
                                sPlus31 = sPlus31 + dateDiffP31;
                                sPlus50 = sPlus50 + dateDiffP50;
                                
                                total = sPlus3 + sPlus10 + sPlus11 + sPlus16 + sPlus21 + sPlus31 + sPlus50 ;
                                slaCalculatedMap.put("s+3Days", sPlus3);
                                slaCalculatedMap.put("s+10Days", sPlus10);
                                slaCalculatedMap.put("s+11to15Days", sPlus11);
                                slaCalculatedMap.put("s+16to20Days", sPlus16);
                                slaCalculatedMap.put("s+21to30Days", sPlus21);
                                slaCalculatedMap.put("s+31to50Days", sPlus31);
                                slaCalculatedMap.put("s+>50Days", sPlus50);
                                slaCalculatedMap.put("total", total);
                            }
                            }ownerBuId.add(custRequest.getString("ownerBu"));
                        }
                      }
                      slaResults.add(slaCalculatedMap);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Debug.log("===error messages==="+e.getMessage());
        }
        return slaResults;
    }
    private static BigDecimal getOpenSrBeyondSla(Delegator delegator, String statusId) {
        long srCount = 0;
        List < EntityCondition > conditionlist = FastList.newInstance();
        try {
            Set < String > fieldsToSelect = new TreeSet < String > ();
            if (UtilValidate.isNotEmpty(statusId)) {
            	conditionlist.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, statusId));
            }
            //conditionlist.add(EntityCondition.makeCondition("custRequestTypeId" , EntityOperator.NOT_EQUAL, CommonPortalConstants.SR_COMPLAINT));
            //conditionlist.add(EntityCondition.makeCondition("closedDateTime" , EntityOperator.EQUALS, null));
            EntityCondition condition = EntityCondition.makeCondition(conditionlist, EntityOperator.AND);
            fieldsToSelect.add("closedByDate");
            fieldsToSelect.add("custRequestId");
            //fieldsToSelect.add("statusId");
            List < GenericValue > custRequests = EntityQuery.use(delegator).select(fieldsToSelect).from("CustRequest").where(condition).distinct(true).orderBy("-lastUpdatedTxStamp").queryList();
            if (UtilValidate.isNotEmpty(custRequests)) {
                srCount = DataUtil.getSrCountbyStatus(delegator,statusId,custRequests,null);
            }
        }catch(Exception e) {
        	e.printStackTrace();
        }
        return BigDecimal.valueOf(srCount);
    }

    private static BigDecimal getClosedSrSla(Delegator delegator, String statusId, String type) {
        long srCount = 0;
        List < EntityCondition > conditionlist = FastList.newInstance();
        try {
            Set < String > fieldsToSelect = new TreeSet < String > ();
            if (UtilValidate.isNotEmpty(statusId)) {
                conditionlist.add(EntityCondition.makeCondition("statusId", EntityOperator.IN, UtilMisc.toList("SR_CLOSED", "SR_CANCELLED")));
            }
            //conditionlist.add(EntityCondition.makeCondition("custRequestTypeId" , EntityOperator.NOT_EQUAL, CommonPortalConstants.SR_COMPLAINT));
            EntityCondition condition = EntityCondition.makeCondition(conditionlist, EntityOperator.AND);
            fieldsToSelect.add("closedByDate");
            fieldsToSelect.add("custRequestId");
            //fieldsToSelect.add("statusId");
            List < GenericValue > custRequests = EntityQuery.use(delegator).select(fieldsToSelect).from("CustRequest").where(condition).distinct(true).orderBy("-lastUpdatedTxStamp").queryList();
            if (UtilValidate.isNotEmpty(custRequests)) {
                srCount = DataUtil.getSrCountbyStatus(delegator,statusId,custRequests,type);
            }
        }catch(Exception e) {
        	e.printStackTrace();
        }
        return BigDecimal.valueOf(srCount);
    }

    public static int getWorkingDaysBetweenDates(Delegator delegator, Timestamp tStartDate, Timestamp tEndDate) {
        
        int numberOfDays = 0;
	    if(UtilValidate.isNotEmpty(tStartDate) && UtilValidate.isNotEmpty(tEndDate) && !tStartDate.equals(tEndDate)) {
	    	String startDate = "";
            if (UtilValidate.isNotEmpty(tStartDate)) {
            	startDate= DataUtil.convertDateTimestamp(tStartDate.toString(), new SimpleDateFormat("dd/MM/yyyy"), DateTimeTypeConstant.TIMESTAMP, DateTimeTypeConstant.STRING);
            }
            String endDate = "";
            if (UtilValidate.isNotEmpty(tEndDate)) {
            	endDate= DataUtil.convertDateTimestamp(tEndDate.toString(), new SimpleDateFormat("dd/MM/yyyy"), DateTimeTypeConstant.TIMESTAMP, DateTimeTypeConstant.STRING);
            }
            
            if(startDate.equals(endDate)) {
                return numberOfDays;
            }
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            Date date1;
            Date date2;
            try {
                date1 = df.parse(startDate);
                date2 = df.parse(endDate);
                Calendar cal1 = Calendar.getInstance();
                Calendar cal2 = Calendar.getInstance();
                cal1.setTime(date1);
                cal2.setTime(date2);
                while (cal1.before(cal2)) {
                    if ((Calendar.SATURDAY != cal1.get(Calendar.DAY_OF_WEEK))&&(Calendar.SUNDAY != cal1.get(Calendar.DAY_OF_WEEK))) {
                        numberOfDays++;
                        cal1.add(Calendar.DATE,1);
                    }else if(cal1.equals(cal2)){
                        cal1.add(Calendar.DATE,1);
                    }else {
                        cal1.add(Calendar.DATE,1);
                    }
                }
               
                List conditionList = FastList.newInstance();
                java.sql.Date tStartDate1 =  DataUtil.convertDateTimestamp(tStartDate.toString(), new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"), DateTimeTypeConstant.TIMESTAMP, CommonPortalConstants.DateTimeTypeConstant.SQL_DATE);
                java.sql.Date tEndDate1 =  DataUtil.convertDateTimestamp(tEndDate.toString(), new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"), DateTimeTypeConstant.TIMESTAMP, CommonPortalConstants.DateTimeTypeConstant.SQL_DATE);
                
                conditionList.add(EntityCondition.makeCondition("status", EntityOperator.IN, UtilMisc.toList("ACTIVE","Y")));
				conditionList.add(EntityCondition.makeCondition("holidayDate", EntityOperator.BETWEEN, UtilMisc.toList(tStartDate1,tEndDate1)));
        		
				EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
        		List<GenericValue> holidayConfigList = delegator.findList("TechDataHolidayConfig", mainConditons, null, null, null, true);
                if(UtilValidate.isNotEmpty(holidayConfigList)) {
                    for(GenericValue holidayConfig : holidayConfigList) {
                        String holidayDate = holidayConfig.getString("holidayDate");
                         if(UtilValidate.isNotEmpty(holidayDate)) {
                             holidayDate = DataUtil.convertDateTimestamp(holidayDate, new SimpleDateFormat("dd/MM/yyyy"), DateTimeTypeConstant.DATE, DateTimeTypeConstant.STRING);
                         }
                         date1 = df.parse(holidayDate);
                         cal1 = Calendar.getInstance();
                         cal1.setTime(date1);
                         cal2.setTime(date2);
                         if ((Calendar.SATURDAY != cal1.get(Calendar.DAY_OF_WEEK))&&(Calendar.SUNDAY != cal1.get(Calendar.DAY_OF_WEEK))) {
                             numberOfDays--;
                         }
                     }
                }
    			
            } catch (ParseException | GenericEntityException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
	    }
	    return numberOfDays;
	}
    //Desc get calculated data for open SR details
    public static List < Map < String, Object >> getCustReqSupplemtoryData(Delegator delegator, List<GenericValue> custRequests, HttpServletRequest request) {

          List <Map <String, Object>> slaResults = new ArrayList <Map <String, Object>> ();
          SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
          Timestamp systemTime = UtilDateTime.nowTimestamp();
          
          try {
              if(UtilValidate.isNotEmpty(custRequests)) {
                  for(GenericValue custRequest  : custRequests) {
                      Map < String, Object > data = new HashMap < String, Object > ();
                      if(UtilValidate.isNotEmpty(custRequest)) {
                    	  
                    	  data.put("refId", "");
                    	  data.put("product", "");
                    	  data.put("productLvl1", "");
                    	  data.put("productLvl2", "");
                    	  data.put("accountNumber", "");
                    	  data.put("accountName", "");
                    	  data.put("transactionDate", "");
                    	  data.put("followNeeded", "");
                    	  
                          String custRequestId = custRequest.getString("custRequestId");
                          data.put("srNumber", custRequestId);
                          GenericValue custReqChannelPweb = EntityQuery.use(delegator).from("CustRequestChannelPweb").where("custRequestId",custRequestId).queryOne();
                          if(UtilValidate.isNotEmpty(custReqChannelPweb)) {
                              data.put("refId",custReqChannelPweb.getString("custPwebRefNo"));
                          }
                          /*data.put("customerOwner", custRequest.getString("responsiblePerson"));
                          data.put("segment", custRequest.getString("custReqSegment"));*/
                          data.put("customerOwnerInv", "NA");
                          data.put("customerOwnerCmp", "NA");
                          data.put("segmentInv", "NA");
                          /*data.put("srTypeId", custRequest.getString("custRequestTypeId"));
                          data.put("srCategoryId", custRequest.getString("custRequestCategoryId"));
                          data.put("srSubCategoryId", custRequest.getString("custRequestSubCategoryId"));
                          */
                          data.put("srStatus", "Open");
                          data.put("srSource", custRequest.getString("custReqSrSource"));
                          //custRequest.getString("subStatusId")
                          data.put("srSubStatus", Objects.toString(org.fio.homeapps.util.DataUtil.getStatusDescription(delegator, custRequest.getString("subStatusId")),""));
                          data.put("ownerBu", DataUtil.getBusinessUnitName(delegator, custRequest.getString("ownerBu"))); // bu name
                          data.put("employeeId", /*custRequest.getString("custReqEmpName")*/"");
                          data.put("reason", custRequest.getString("reason"));
                          data.put("comments", custRequest.getString("internalComment"));
                          String plannedDueDate = "";
                          if (UtilValidate.isNotEmpty(custRequest.getString("actualEndDate"))) {
                              plannedDueDate = DataUtil.convertDateTimestamp(custRequest.getString("actualEndDate"), new SimpleDateFormat("dd/MM/yyyy hh:mm"), DateTimeTypeConstant.TIMESTAMP, DateTimeTypeConstant.STRING);
                          }
                          data.put("plannedDueDate", plannedDueDate); // may change                           
                          data.put("approvalStatus", "NA");//custRequest.getString("statusId")
                          data.put("description", custRequest.getString("description"));
                          String closedByDate = "";
                          if (UtilValidate.isNotEmpty(custRequest.getString("closedByDate"))) {
                              closedByDate = DataUtil.convertDateTimestamp(custRequest.getString("closedByDate"), new SimpleDateFormat("dd/MM/yyyy hh:mm"), DateTimeTypeConstant.TIMESTAMP, DateTimeTypeConstant.STRING);
                          }
                          data.put("dateClosed", closedByDate);
                          String createdOn = "";
                          if (UtilValidate.isNotEmpty(custRequest.getString("createdDate"))) {
                              createdOn = DataUtil.convertDateTimestamp(custRequest.getString("createdDate"), new SimpleDateFormat("dd/MM/yyyy hh:mm"),DateTimeTypeConstant.TIMESTAMP, DateTimeTypeConstant.STRING);
                          }
                          data.put("createdOn", createdOn);
                          data.put("createdBy", custRequest.getString("createdByUserLogin"));
                          data.put("modifiedBy", custRequest.getString("lastModifiedByUserLogin"));
                          data.put("modifiedByBu", "NA");
                          data.put("createdByBu", "NA");
                          data.put("srTypeId", Objects.toString(org.fio.homeapps.util.DataUtil.getTripletDescription(delegator, custRequest.getString("custRequestTypeId"),"SRTYPE"),""));
                          data.put("srCategoryId", Objects.toString(org.fio.homeapps.util.DataUtil.getTripletDescription(delegator, custRequest.getString("custRequestCategoryId"),"SRCategory"),""));
                          data.put("srSubCategoryId", Objects.toString(org.fio.homeapps.util.DataUtil.getTripletDescription(delegator, custRequest.getString("custRequestSubCategoryId"),"SRSubCategory"),""));
                          
                          data.put("priority", Objects.toString(org.fio.homeapps.util.EnumUtil.getEnumDescription(delegator, custRequest.getString("priority")),"PRIORITY_LEVEL"));
                          GenericValue custReqSupp = EntityQuery.use(delegator).from("CustRequestSupplementory").where("custRequestId",custRequestId).queryOne();
                          String overDue = "N";
                          if(UtilValidate.isNotEmpty(custReqSupp)) {
                              data.put("product", custReqSupp.getString("accountType")); //Product Type lookup table
                              /*data.put("productLvl1", custReqSupp.getString("productHoldingSipSn"));
                              data.put("productLvl2", custReqSupp.getString("productHoldingUt"));*/
                              data.put("productLvl1", "NA");
                              data.put("productLvl2", "NA");
                              data.put("accountNumber", custReqSupp.getString("accountNumber")); // LAA_ACCT_NO or ODA_ACCT_NO
                              data.put("accountName", custReqSupp.getString("accountType"));
                              String commitDate = custReqSupp.getString("commitDate");
                              if(UtilValidate.isNotEmpty(commitDate)) {
                                  commitDate = DataUtil.convertDateTimestamp(commitDate, df, CommonPortalConstants.DateTimeTypeConstant.TIMESTAMP, CommonPortalConstants.DateTimeTypeConstant.STRING);
                              }
                              if(UtilValidate.isNotEmpty(custReqSupp.getTimestamp("commitDate")) && systemTime.after(custReqSupp.getTimestamp("commitDate"))) {
                                  overDue = "Y";
                              }
                              data.put("transactionDate", commitDate);
                              
                              data.put("followNeeded", custReqSupp.getString("custActionPlanReq"));
                          }
                          data.put("disputeAmount", "NA");
                          data.put("supervisorReview", "NA");
                          int diffInDays = 0;
                          if(UtilValidate.isNotEmpty(custRequest.getTimestamp("createdDate"))) {
                              Date dt1 = new Date(custRequest.getTimestamp("createdDate").getTime()); 
                              Date dt2= new Date(systemTime.getTime());
                              diffInDays = (int) ((dt2.getTime() - dt1.getTime()) / (1000 * 60 * 60 * 24));
                          }
                          
                          data.put("duration", diffInDays); // sytemdate.substract(srcreateddate)
                          data.put("overdue", overDue);
                          data.put("currency", custRequest.getString("currencyUomId")); //doubt
                          data.put("awardStatus", "NA");
                          data.put("documentReceived", "NA");
                          data.put("documentNeeded", "NA");
                          
                          String cif = DataUtil.getCifNumber(delegator,custRequest.getString("fromPartyId"),custRequestId);
                          String contactFullName = "";
                          if (UtilValidate.isNotEmpty(custRequest.getString("fromPartyId"))) {
                              GenericValue person = delegator.findOne("Person", UtilMisc.toMap("partyId", custRequest.getString("fromPartyId")), true);
                              if (UtilValidate.isNotEmpty(person)) {
                                  if ("01".equals((String)custRequest.get("customerRelatedType")) || "1".equals((String)custRequest.get("customerRelatedType"))) {
                                      contactFullName = contactFullName + person.getString("firstName");
                                      contactFullName = contactFullName + person.getString("lastName");
                                  } 
                              }
                          }
                          data.put("cifInv", cif); 
                          data.put("cifCompany", "NA"); // crm / non crm customer
                          data.put("contactFullName", contactFullName);
                          data.put("lastGroupAssignedDate", "NA");
                          data.put("lastUserAssignedDate", "NA");
                          data.put("awardNominee", "NA");
                    }
                    slaResults.add(data);
                }
            }
        } catch (Exception e) {
              e.printStackTrace();
              //Debug.log("===error messages==="+e.getMessage());
        }
        return slaResults;
    }
    @SuppressWarnings("unchecked")
    public static long getWorkingDayCountBetweenDates(Delegator delegator, Timestamp  tStartDate, String ownerBu, String custRequestId, String type,Timestamp dueDate, Timestamp srClosedDate,int intrv) {
        
        long workDays = 0;
        Timestamp tEndDate = UtilDateTime.nowTimestamp();
        List conditionList = FastList.newInstance();
        Date date = new Date();
        String modifiedDate= new SimpleDateFormat("yyyy-MM-dd").format(date);
        modifiedDate = modifiedDate +" 23:59:59";
        Timestamp modifiedDate1 = null;
        try {
            //Debug.log("intrv>>>"+intrv+"custRequestId"+custRequestId);
            if(intrv == 10) {
                modifiedDate1 =  getPrevBusinessDateBeforeSystem(4);
            }else if(intrv == 15) {
                modifiedDate1 = getPrevBusinessDateBeforeSystem(11);
            }else if(intrv == 20) {
                modifiedDate1 = getPrevBusinessDateBeforeSystem(16);
            }else if(intrv == 30) {
                modifiedDate1 = getPrevBusinessDateBeforeSystem(21);
            }else if(intrv == 50) {
                modifiedDate1 = getPrevBusinessDateBeforeSystem(31);
            }else if(intrv == 60) {
                modifiedDate1 = getPrevBusinessDateBeforeSystem(51);
            }
            
            if(UtilValidate.isNotEmpty(tStartDate)) {
                try {
                	GenericValue custRequestRes = EntityQuery.use(delegator).from("CustRequest").where("custRequestId",custRequestId).queryOne();
                	if(UtilValidate.isNotEmpty(custRequestRes)) {
                		srClosedDate = custRequestRes.getTimestamp("closedByDate");
                	}
                	GenericValue custRequestSuppl = EntityQuery.use(delegator).from("CustRequestSupplementory").select("commitDate").where("custRequestId", custRequestId).queryOne();
                	if(UtilValidate.isNotEmpty(custRequestSuppl)) {
                		dueDate = custRequestSuppl.getTimestamp("commitDate");
                	}
                	//Debug.log("custRequestId>>>>"+custRequestId+">>>>>srClosedDate>>>>"+srClosedDate+">>>>>>>dueDate>>>"+dueDate);
                    if("CLOSED_WITH_IN_SLA".equals(type)) {
                        if(UtilValidate.isNotEmpty(dueDate) && UtilValidate.isNotEmpty(srClosedDate) && srClosedDate.before(dueDate)) {
                           if(intrv == 3 ) {
                                conditionList.add(EntityCondition.makeCondition("createdDate", EntityOperator.BETWEEN, UtilMisc.toList(tStartDate,Timestamp.valueOf(modifiedDate))));
                            }else {
                            	conditionList.add(EntityCondition.makeCondition("createdDate", EntityOperator.BETWEEN, UtilMisc.toList(tStartDate,modifiedDate1)));
                            }
                            conditionList.add(EntityCondition.makeCondition("custRequestTypeId" , EntityOperator.NOT_EQUAL, CommonPortalConstants.SR_COMPLAINT));
                            conditionList.add(EntityCondition.makeCondition("ownerBu" , EntityOperator.EQUALS, ownerBu));
                            conditionList.add(EntityCondition.makeCondition("custRequestId" , EntityOperator.EQUALS, custRequestId));
                            conditionList.add(EntityCondition.makeCondition("statusId" , EntityOperator.IN, UtilMisc.toList("SR_CLOSED", "SR_CANCELLED")));
                         //   Debug.log("CLOSED_WITH_IN_SLA>>>>"+conditionList);
                        }
                    }else if ("CLOSED_BEYOND_SLA".equals(type)) {
                        if(UtilValidate.isNotEmpty(dueDate) && UtilValidate.isNotEmpty(srClosedDate) && srClosedDate.after(dueDate)) {
                        	if(intrv == 3 ) {
                                conditionList.add(EntityCondition.makeCondition("createdDate", EntityOperator.BETWEEN, UtilMisc.toList(tStartDate,Timestamp.valueOf(modifiedDate))));
                            }else {
                            	conditionList.add(EntityCondition.makeCondition("createdDate", EntityOperator.BETWEEN, UtilMisc.toList(tStartDate,modifiedDate1)));
                            }
                            conditionList.add(EntityCondition.makeCondition("custRequestTypeId" , EntityOperator.NOT_EQUAL, CommonPortalConstants.SR_COMPLAINT));
                            conditionList.add(EntityCondition.makeCondition("ownerBu" , EntityOperator.EQUALS, ownerBu));
                            conditionList.add(EntityCondition.makeCondition("custRequestId" , EntityOperator.EQUALS, custRequestId));
                            conditionList.add(EntityCondition.makeCondition("statusId" , EntityOperator.IN, UtilMisc.toList("SR_CLOSED", "SR_CANCELLED")));
                        }
                    }else if ("OPEN_BEYOND_SLA".equals(type)) {
                        if(UtilValidate.isNotEmpty(dueDate) && UtilValidate.isNotEmpty(tEndDate) && tEndDate.after(dueDate)) {
                            if(intrv == 3 ) {
                                conditionList.add(EntityCondition.makeCondition("createdDate", EntityOperator.BETWEEN, UtilMisc.toList(tStartDate,Timestamp.valueOf(modifiedDate))));
                            }else {
                            	conditionList.add(EntityCondition.makeCondition("createdDate", EntityOperator.BETWEEN, UtilMisc.toList(tStartDate,modifiedDate1)));
                            }
                            conditionList.add(EntityCondition.makeCondition("custRequestTypeId" , EntityOperator.NOT_EQUAL, CommonPortalConstants.SR_COMPLAINT));
                            conditionList.add(EntityCondition.makeCondition("ownerBu" , EntityOperator.EQUALS, ownerBu));
                            conditionList.add(EntityCondition.makeCondition("custRequestId" , EntityOperator.EQUALS, custRequestId));
                            conditionList.add(EntityCondition.makeCondition("statusId" , EntityOperator.NOT_IN, UtilMisc.toList("SR_CLOSED", "SR_CANCELLED")));
                        }
                    }else if ("OPEN_WITH_IN_SLA".equals(type)) {
                        if(UtilValidate.isNotEmpty(dueDate) && UtilValidate.isNotEmpty(tEndDate) && tEndDate.before(dueDate)) {
                            if(intrv == 3 ) {
                                conditionList.add(EntityCondition.makeCondition("createdDate", EntityOperator.BETWEEN, UtilMisc.toList(tStartDate,Timestamp.valueOf(modifiedDate))));
                            }else {
                            	conditionList.add(EntityCondition.makeCondition("createdDate", EntityOperator.BETWEEN, UtilMisc.toList(tStartDate,modifiedDate1)));
                            }
                            conditionList.add(EntityCondition.makeCondition("custRequestTypeId" , EntityOperator.NOT_EQUAL, CommonPortalConstants.SR_COMPLAINT));
                            conditionList.add(EntityCondition.makeCondition("ownerBu" , EntityOperator.EQUALS, ownerBu));
                            conditionList.add(EntityCondition.makeCondition("custRequestId" , EntityOperator.EQUALS, custRequestId));
                            conditionList.add(EntityCondition.makeCondition("statusId" , EntityOperator.NOT_IN, UtilMisc.toList("SR_CLOSED", "SR_CANCELLED")));
                        }
                    }
                    EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
                    //Debug.log("==mainConditons====?type??"+type+"---"+mainConditons);
                    if(UtilValidate.isNotEmpty(mainConditons)) {
                        workDays = delegator.findCountByCondition("CustRequest", mainConditons, null, null, null);
                    }
                    //Debug.log("workDays>>>>>>"+workDays);
//                    Debug.log("workDays>>>>>>"+workDays);
                } catch (/*ParseException | */GenericEntityException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }catch(Exception e) {
        	
        }
        return workDays;
    }
    
    public static Timestamp getPrevBusinessDateBeforeSystem(int slaCnt) {
        Timestamp subDate = null;
         try {
            LocalDate datee = LocalDate.now();
            for(int i =1;i<=slaCnt;i++) {
                LocalDate day = datee.minusDays(i);
                if(day.getDayOfWeek() == DayOfWeek.SATURDAY || day.getDayOfWeek() == DayOfWeek.SUNDAY) {
                    slaCnt = slaCnt+1;
                }
            }
            if(slaCnt == 15 || slaCnt == 20 || slaCnt == 30 || slaCnt == 50 || slaCnt == 60) {
                subDate = Timestamp.valueOf(datee.minusDays(slaCnt)+" 23:59:59.0");
            }else {
                subDate = Timestamp.valueOf(datee.minusDays(slaCnt)+" 00:00:00");
            }
            
         }catch(Exception e) {
            e.printStackTrace();
         }
        return subDate;
    }
    
    public static List < Map < String, Object >> getSrStatusReport(Delegator delegator, List<GenericValue> custRequests, Map<String, Object> context) {

        List <Map <String, Object>> results = new ArrayList <Map <String, Object>> ();
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        
        Timestamp systemTime = UtilDateTime.nowTimestamp();
        
        String startDate = (String) context.get("startDate");
        String endDate = (String) context.get("endDate");
        
        try {
        	
            if(UtilValidate.isNotEmpty(custRequests)) {
            	
            	Map<String, List <GenericValue>> categoryMap = new HashMap<>();
	            List < String > custRequestIds = EntityUtil.getFieldListFromEntityList(custRequests, "custRequestId", true);
	            
	            Map<String, String> categoryDescMap = new HashMap<>();
	            List < Map < String, Object >> result = new ArrayList<>();
	            for (GenericValue custRequest : custRequests) {
	            	if (UtilValidate.isEmpty(custRequest))
	            		continue;
	            	String custRequestCategoryId = (String) custRequest.get("custRequestCategoryId");
	            	List <GenericValue> custRequestList = categoryMap.get(custRequestCategoryId);
	            	if (custRequestList==null) {
	            		custRequestList = new ArrayList<>();
	            		custRequestList.add(custRequest);
	            		categoryMap.put(custRequestCategoryId, custRequestList);
	            		categoryDescMap.put(custRequestCategoryId, (String) custRequest.get("description"));
	            	} else {
	            		custRequestList.add(custRequest);
	            		categoryMap.put(custRequestCategoryId, custRequestList);
	            	}
	            	
	            }
	            
	            final String fStartDate = startDate;
	            final String fEndDate = endDate;
	            
	            categoryMap.forEach((categoryId,cRequests)->{
	            	Map < String, Object > resultMap = new HashMap<>();
	            	
	            	resultMap.put("categoryId", categoryDescMap.get(categoryId));
	        		//long closeRecords = ((Stream<GenericValue>) cRequests).filter(custRequest -> "SR_CLOSED".equalsIgnoreCase((String) custRequest.get("closedDateTime")) && "1".equalsIgnoreCase((String) custRequest.get("escalationLevel"))).count();
	        		int countWithinSLAA1 = 0;
	        		int countCreatedDuringPeriod = 0;
	        		int openAsStartOfPeriod = 0;
	        		int countExceedingSLAA2 = 0;
	        		int countWithinSlaB1 = 0;
	        		int countWithinSlaB2 = 0;
	        		
	        		for (GenericValue custRequest :cRequests) {
	                	if (UtilValidate.isEmpty(custRequest))
	                		continue;
	        			Timestamp createdDate = (Timestamp) custRequest.get("createdDate");
	        			Timestamp lastModifiedDate = (Timestamp) custRequest.get("lastModifiedDate");
	        			
	        			Timestamp crStartDate = null;
						Timestamp crEndDate = null;
						try {
							Date parsedDateFrom = dateFormat.parse(fStartDate+" "+"00:00:00.0");
							crStartDate = new java.sql.Timestamp(parsedDateFrom.getTime());	
							
							parsedDateFrom = dateFormat.parse(fEndDate+" "+"00:00:00.0");
							crEndDate = new java.sql.Timestamp(parsedDateFrom.getTime());
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}	
	        			
	        			//Timestamp crEndDate = UtilDateTime.getDayEnd(Timestamp.valueOf(fEndDate));
	        			//Timestamp crStartDate = UtilDateTime.getDayStart(Timestamp.valueOf(fStartDate));
	        			
	        			
	        			
	        			if (createdDate!=null && createdDate.compareTo(crStartDate)>=0 && createdDate.compareTo(crEndDate)<=0)
	        				countCreatedDuringPeriod++;
	        			if (lastModifiedDate!=null && createdDate.compareTo(crStartDate)<0 && lastModifiedDate.compareTo(crStartDate)>0)
	        				openAsStartOfPeriod++;
	        			String custRequestId = (String) custRequest.get("custRequestId");  
	        			Timestamp closedDateTime = (Timestamp) custRequest.get("closedByDate");
	        			Timestamp commitDate = (Timestamp) custRequest.get("commitDate");
						if ("SR_CLOSED".equalsIgnoreCase((String) custRequest.get("statusId")) && commitDate!=null && closedDateTime!=null) {
							if (closedDateTime.compareTo(crStartDate) >= 0
									&& closedDateTime.compareTo(crEndDate) <= 0) {
								if (closedDateTime.compareTo(commitDate) <= 0) {
									countWithinSLAA1++;
								} else {
									countExceedingSLAA2++;
								}
							}
							if (createdDate!=null && closedDateTime.compareTo(crEndDate)>0 && createdDate.compareTo(crStartDate)>=0 && createdDate.compareTo(crEndDate)<=0) {
								if (closedDateTime.compareTo(commitDate)<=0)
									countWithinSlaB1++;
								else 
									countWithinSlaB2++;
							}
						} else if (createdDate!=null && createdDate.compareTo(crStartDate)>=0 && createdDate.compareTo(crEndDate)<=0 && "SR_OPEN".equalsIgnoreCase((String) custRequest.get("statusId")) && commitDate!=null) {
							if (crEndDate.compareTo(commitDate)<0){
								countWithinSlaB1++;
							} else {
								countWithinSlaB2++;
							}
						}
						
	        		}
	        		
	        		resultMap.put("createdDuringPeriod", countCreatedDuringPeriod);
	        		resultMap.put("openAsStartOfPeriod", openAsStartOfPeriod);
	        		resultMap.put("totalAB", (countCreatedDuringPeriod-openAsStartOfPeriod));
	        		resultMap.put("withinSLAA1", countWithinSLAA1);
	        		resultMap.put("exceedingSLAA2", countExceedingSLAA2);
	        		resultMap.put("totalA1A2", (countExceedingSLAA2+countWithinSLAA1));
	        		resultMap.put("withinSLAB1", countWithinSlaB1);
	        		resultMap.put("exceedingSLAB2", countWithinSlaB2);
	        		resultMap.put("totalB1B2", (countWithinSlaB1+countWithinSlaB2));
	        		
	        		System.out.println("============resultMap============"+resultMap);
	        		
	        		results.add(resultMap);
	        		
	        	});
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return results;
  }
  public static Timestamp isDeclaredHoliday(Delegator delegator, Timestamp tStartDate) {
	  
	   if(UtilValidate.isNotEmpty(tStartDate)) {
		    //check Between Holiday configuratrion 
        	//String sDate= tStartDate.getYear()+tStartDate.getMonth() + tStartDate.getDate()+"";
		    try {
		    	List conditionList = FastList.newInstance();
                java.sql.Date tStartDate1 =  DataUtil.convertDateTimestamp(tStartDate.toString(), new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"), DateTimeTypeConstant.TIMESTAMP, CommonPortalConstants.DateTimeTypeConstant.SQL_DATE);
                        		
				conditionList.add(EntityCondition.makeCondition("holidayDate", EntityOperator.EQUALS, tStartDate1));
        		conditionList.add(EntityCondition.makeCondition("status", EntityOperator.IN, UtilMisc.toList("ACTIVE","Y")));
        		EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
        		List<GenericValue> serviceRequestList = delegator.findList("TechDataHolidayConfig", mainConditons, null, UtilMisc.toList("createdDate DESC"), null, false);
        		if(UtilValidate.isNotEmpty(serviceRequestList)) {
        			int holidateCnt = serviceRequestList.size();
        			tStartDate =  ReportUtil.getPrevBusinessDateBeforeSystem(holidateCnt);
        		}
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			    
      }
	  return tStartDate;
	}
}