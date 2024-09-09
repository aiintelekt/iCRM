package org.fio.admin.portal.event;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.fio.admin.portal.constant.AdminPortalConstant.DateTimeTypeConstant;
import org.fio.admin.portal.util.DataUtil;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;

import javolution.util.FastList;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

 /**
  * 
  * @author Arshiya
  * @since 25-10-2019
  */

public class AuditLogEvents {
	private AuditLogEvents() {}
    private static final String MODULE = AuditLogEvents.class.getName();
    private static final String RESOURCE = "AdminPortalUiLabels";
    public static String doJSONResponse(HttpServletResponse response, JSONObject jsonObject) {
        return doJSONResponse(response, jsonObject.toString());
    }

    public static String doJSONResponse(HttpServletResponse response, Collection < ? > collection) {
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
    public static String returnError(HttpServletRequest request, String errorMessage) {
        try {
            request.setAttribute("_ERROR_MESSAGE_", "ERROR :" + errorMessage);
            Debug.logError("Error : " + errorMessage, MODULE);
        } catch (Exception e) {
            Debug.logError("Error : " + e.getMessage(), MODULE);
        }
        return "error";
    }
    public static String returnSuccess(HttpServletRequest request, String successMessage) {
        try {
            request.setAttribute("_EVENT_MESSAGE_", successMessage);
            Debug.logError("Success : " + successMessage, MODULE);
        } catch (Exception e) {
            Debug.logError("Error : " + e.getMessage(), MODULE);
        }
        return "success";
    }
    public static GenericValue getUserLogin(HttpServletRequest request) {
        HttpSession session = request.getSession();
        return (GenericValue) session.getAttribute("userLogin");
    }
    
    public static String getSrTypeAuditHistory(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
        List < Map<String, Object>> results = new ArrayList<Map<String, Object>>();
        Map<String, Object> context = UtilHttp.getCombinedMap(request);
        String custRequestTypeId = (String) context.get("custRequestTypeId");
        List < EntityCondition > conditionlist = FastList.newInstance();
        if (UtilValidate.isNotEmpty(custRequestTypeId)) {
            conditionlist.add(EntityCondition.makeCondition("pkCombinedValueText", EntityOperator.EQUALS, custRequestTypeId));
            conditionlist.add(EntityCondition.makeCondition("changedEntityName", EntityOperator.EQUALS, "CustRequestType"));
        }
        EntityCondition condition = EntityCondition.makeCondition(conditionlist, EntityOperator.AND);
        try {
            Set < String > fieldsToSelect = new TreeSet < String > ();
            fieldsToSelect.add("auditHistorySeqId");
            fieldsToSelect.add("changedFieldName");
            fieldsToSelect.add("oldValueText");
            fieldsToSelect.add("newValueText");
            fieldsToSelect.add("changedDate");
            fieldsToSelect.add("changedByInfo");
            //fieldsToSelect.add("status");
            List < GenericValue > srTypeAuditHistories = EntityQuery.use(delegator).select(fieldsToSelect).from("EntityAuditLog").where(condition).maxRows(100).orderBy("-auditHistorySeqId").queryList();
            if (srTypeAuditHistories != null && srTypeAuditHistories.size() > 0) {
                for (GenericValue srTypeAuditHistory: srTypeAuditHistories) {
                    Map<String, Object> data = new HashMap<String, Object>();
                    data.put("auditHistorySeqId", srTypeAuditHistory.getString("auditHistorySeqId"));
                    data.put("changedFieldName", srTypeAuditHistory.getString("changedFieldName"));
                    data.put("oldValueText", srTypeAuditHistory.getString("oldValueText"));
                    data.put("newValueText", srTypeAuditHistory.getString("newValueText"));
                    data.put("changedByInfo", srTypeAuditHistory.getString("changedByInfo"));
                    String changedDate = null;
                    if(UtilValidate.isNotEmpty(srTypeAuditHistory.getString("changedDate"))) {
                 	   changedDate = DataUtil.convertDateTimestamp(srTypeAuditHistory.getString("changedDate"), new SimpleDateFormat("dd/MM/yyyy hh:mm"), DateTimeTypeConstant.TIMESTAMP, DateTimeTypeConstant.STRING);
                    }
                    data.put("changedDate", changedDate);
                    data.put("checkedBy", "");
                    data.put("checkedDate", "");
                    data.put("status", "");
                    results.add(data);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return doJSONResponse(response, e.getMessage());
        }
        return doJSONResponse(response, results);
    }
    public static String getSrCategortSubCategoryAuditHistory(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
        List < Map<String, Object>> results = new ArrayList<Map<String, Object>>();
        Map<String, Object> context = UtilHttp.getCombinedMap(request);
        String custRequestCategoryId = (String) context.get("custRequestCategoryId");
        List < EntityCondition > conditionlist = FastList.newInstance();
        if (UtilValidate.isNotEmpty(custRequestCategoryId)) {
            conditionlist.add(EntityCondition.makeCondition("pkCombinedValueText", EntityOperator.EQUALS, custRequestCategoryId));
            conditionlist.add(EntityCondition.makeCondition("changedEntityName", EntityOperator.EQUALS, "CustRequestCategory"));
        }
        EntityCondition condition = EntityCondition.makeCondition(conditionlist, EntityOperator.AND);
        try {
            Set < String > fieldsToSelect = new TreeSet < String > ();
            fieldsToSelect.add("changedFieldName");
            fieldsToSelect.add("oldValueText");
            fieldsToSelect.add("newValueText");
            fieldsToSelect.add("changedDate");
            fieldsToSelect.add("changedByInfo");
            //fieldsToSelect.add("status");
            List < GenericValue > srTypeAuditHistories = EntityQuery.use(delegator).select(fieldsToSelect).from("EntityAuditLog").where(condition).maxRows(100).orderBy("-auditHistorySeqId").queryList();
            if (srTypeAuditHistories != null && srTypeAuditHistories.size() > 0) {
                for (GenericValue srTypeAuditHistory: srTypeAuditHistories) {
                    Map<String, Object> data = new HashMap<String, Object>();
                    data.put("changedFieldName", srTypeAuditHistory.getString("changedFieldName"));
                    data.put("oldValueText", srTypeAuditHistory.getString("oldValueText"));
                    data.put("newValueText", srTypeAuditHistory.getString("newValueText"));
                    data.put("changedByInfo", srTypeAuditHistory.getString("changedByInfo"));
                    String changedDate = null;
                    if(UtilValidate.isNotEmpty(srTypeAuditHistory.getString("changedDate"))) {
                 	   changedDate = DataUtil.convertDateTimestamp(srTypeAuditHistory.getString("changedDate"), new SimpleDateFormat("dd/MM/yyyy hh:mm"), DateTimeTypeConstant.TIMESTAMP, DateTimeTypeConstant.STRING);
                    }
                    data.put("changedDate", changedDate);
                    data.put("checkedBy", "");
                    data.put("checkedDate", "");
                    data.put("status", "");
                    results.add(data);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return doJSONResponse(response, e.getMessage());
        }
        return doJSONResponse(response, results);
    }
}
