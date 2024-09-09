package org.fio.admin.portal.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fio.admin.portal.constant.AdminPortalConstant.CustRequestAssocConstants;
import org.fio.admin.portal.event.AjaxEvents;
import org.fio.homeapps.util.EnumUtil;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;

import javolution.util.FastList;
import org.fio.admin.portal.constant.AdminPortalConstant.ParamUnitConstant;
/**
 * 
 * @author Arshiya
 *
 */
public class TripletUtil {
	private static String MODULE = TripletUtil.class.getName();
    public static final String RESOURCE = "AdminPortalUiLabels";
    
    //Author : Arshiya S, Description : Getting SR Categories
    public static String getSrCategories(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        List < Map < String, Object >> results = new ArrayList < Map < String, Object >> ();
        Map < String, Object > context = UtilHttp.getCombinedMap(request);
        String srTypeId = (String) context.get("srTypeId");
        try {
            List < EntityCondition > conditionlist = FastList.newInstance();
            if (UtilValidate.isNotEmpty(srTypeId)) {
                conditionlist.add(EntityCondition.makeCondition("parentCode", EntityOperator.EQUALS, srTypeId));
            }
            conditionlist.add(EntityCondition.makeCondition("type", EntityOperator.EQUALS, CustRequestAssocConstants.SR_Category));
            conditionlist.add(EntityCondition.makeCondition(
                    EntityCondition.makeCondition("active", EntityOperator.EQUALS, "Y"), EntityOperator.OR,
                    EntityCondition.makeCondition("active", EntityOperator.EQUALS, null)));
            EntityCondition condition = EntityCondition.makeCondition(conditionlist, EntityOperator.AND);
            Set < String > fieldsToSelect = new TreeSet < String > ();
            fieldsToSelect.add("code");
            fieldsToSelect.add("value");
            List < GenericValue > srCategorires = EntityQuery.use(delegator).select(fieldsToSelect).from("CustRequestAssoc").where(condition).orderBy("-sequenceNumber").queryList();
            if (UtilValidate.isNotEmpty(srCategorires)) {
                for (GenericValue srCategoriry: srCategorires) {
                    Map < String, Object > data = new HashMap < String, Object > ();
                    data.put("srCategoryId",srCategoriry.getString("code"));
                    data.put("srCategoryDesc",srCategoriry.getString("value"));
                    
                    results.add(data);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxEvents.doJSONResponse(response, e.getMessage());
        }
        return AjaxEvents.doJSONResponse(response, results);
    }
    
    //Author : Arshiya S, Description : Getting SR Categories
    public static String getSrSubCategories(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        List < Map < String, Object >> results = new ArrayList < Map < String, Object >> ();
        Map < String, Object > context = UtilHttp.getCombinedMap(request);
        String srTypeId = (String) context.get("srTypeId");
        String srCategoryId = (String) context.get("srCategoryId");
        try {
            List < EntityCondition > conditionlist = FastList.newInstance();
            if (UtilValidate.isNotEmpty(srTypeId)) {
                conditionlist.add(EntityCondition.makeCondition("grandparentCode", EntityOperator.EQUALS, srTypeId));
            }
            if (UtilValidate.isNotEmpty(srCategoryId)) {
                conditionlist.add(EntityCondition.makeCondition("parentCode", EntityOperator.EQUALS, srCategoryId));
            }
            conditionlist.add(EntityCondition.makeCondition("type", EntityOperator.EQUALS, CustRequestAssocConstants.SR_SubCategory));
            conditionlist.add(EntityCondition.makeCondition(
                    EntityCondition.makeCondition("active", EntityOperator.EQUALS, "Y"), EntityOperator.OR,
                    EntityCondition.makeCondition("active", EntityOperator.EQUALS, null)));
            EntityCondition condition = EntityCondition.makeCondition(conditionlist, EntityOperator.AND);
            Set < String > fieldsToSelect = new TreeSet < String > ();
            fieldsToSelect.add("code");
            fieldsToSelect.add("value");
            List < GenericValue > srSubCategorires = EntityQuery.use(delegator).select(fieldsToSelect).from("CustRequestAssoc").where(condition).orderBy("-sequenceNumber").queryList();
            if (UtilValidate.isNotEmpty(srSubCategorires)) {
                for (GenericValue srSubCategoriry: srSubCategorires) {
                    Map < String, Object > data = new HashMap < String, Object > ();
                    data.put("srSubCategoryId",srSubCategoriry.getString("code"));
                    data.put("srSubCategoryDesc",srSubCategoriry.getString("value"));
                    
                    results.add(data);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxEvents.doJSONResponse(response, e.getMessage());
        }
        return AjaxEvents.doJSONResponse(response, results);
    }
  //Author : Arshiya S, Description : Getting IA Sub Type
    public static String getIASubTypes(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        List < Map < String, Object >> results = new ArrayList < Map < String, Object >> ();
        Map < String, Object > context = UtilHttp.getCombinedMap(request);
        String iaTypeId = (String) context.get("iaTypeId");
        try {
            List < EntityCondition > conditionlist = FastList.newInstance();
            if (UtilValidate.isNotEmpty(iaTypeId)) {
                conditionlist.add(EntityCondition.makeCondition("parentCode", EntityOperator.EQUALS, iaTypeId));
            }
            conditionlist.add(EntityCondition.makeCondition("type", EntityOperator.EQUALS, ParamUnitConstant.SUB_TYPE));
            conditionlist.add(EntityCondition.makeCondition(
                    EntityCondition.makeCondition("active", EntityOperator.EQUALS, "Y"), EntityOperator.OR,
                    EntityCondition.makeCondition("active", EntityOperator.EQUALS, null)));
            EntityCondition condition = EntityCondition.makeCondition(conditionlist, EntityOperator.AND);
            Set < String > fieldsToSelect = new TreeSet < String > ();
            fieldsToSelect.add("code");
            fieldsToSelect.add("value");
            List < GenericValue > srCategorires = EntityQuery.use(delegator).select(fieldsToSelect).from("WorkEffortAssocTriplet").where(condition).orderBy("-lastUpdatedTxStamp").queryList();
            if (UtilValidate.isNotEmpty(srCategorires)) {
                for (GenericValue srCategoriry: srCategorires) {
                    Map < String, Object > data = new HashMap < String, Object > ();
                    data.put("subTypeId",srCategoriry.getString("code"));
                    data.put("subTypeDesc",srCategoriry.getString("value"));
                    
                    results.add(data);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxEvents.doJSONResponse(response, e.getMessage());
        }
        return AjaxEvents.doJSONResponse(response, results);
    }
}
