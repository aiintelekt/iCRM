package org.fio.admin.portal.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.fio.admin.portal.constant.AdminPortalConstant.AccessLevel;
import org.fio.admin.portal.constant.AdminPortalConstant.GlobalParameter;
import org.fio.admin.portal.constant.AdminPortalConstant.SecurityType;
import org.fio.admin.portal.util.DataUtil;
import org.fio.admin.portal.util.EnumUtil;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.service.GenericServiceException;

public class ConfigurableScreenServices {
    private ConfigurableScreenServices() {}
    private static final String MODULE = ConfigurableScreenServices.class.getName();
    public static final String RESOURCE = "AdminPortalUiLabels";

    public static Map < String, Object > editScreenConfigService(DispatchContext dctx, Map < String, Object > context) {
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Security security = dctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
        String clsId = (String) context.get("clsId");
        String mountPoint = (String) context.get("mountPoint");
        String layout = (String) context.get("layout");
        String screen = (String) context.get("screen");
        String screenService = (String) context.get("screenService");
        String requestUri = (String) context.get("requestUri");
        List<Map> dataList = (List<Map>) context.get("dataList");
        String responseMessage = UtilProperties.getMessage(RESOURCE, "EditScreenConfigSuccessful", locale);
        Map < String, Object > results = ServiceUtil.returnSuccess(responseMessage);

        try{
            GenericValue ComponentLayoutScreen = delegator.findOne("ComponentLayoutScreen", UtilMisc.toMap("clsId",clsId), false);
            if(UtilValidate.isEmpty(ComponentLayoutScreen)){
                results = ServiceUtil.returnError("Error : " + "ComponentLayoutScreen data not found.");
                results.put("clsId", clsId);
                return results;
            }
            ComponentLayoutScreen.set("mountPoint", mountPoint);
            ComponentLayoutScreen.set("layout", layout);
            ComponentLayoutScreen.set("screen", screen);
            ComponentLayoutScreen.set("screenService", screenService);
            ComponentLayoutScreen.set("requestUri", requestUri);
            ComponentLayoutScreen.set("lastModifiedByUserLogin", userLogin.getString("userLoginId"));
            ComponentLayoutScreen.store();
            results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "SecurityGroupUpdatedSuccessfully", locale));

            List<EntityCondition> conditions = new ArrayList<EntityCondition>();
            conditions.add(EntityCondition.makeCondition("clsId",EntityOperator.EQUALS,clsId));
            List<GenericValue> screenSpecificationList = EntityQuery.use(delegator).from("ScreenSpecification").where(EntityCondition.makeCondition(conditions,EntityOperator.AND)).queryList();
            if(UtilValidate.isNotEmpty(screenSpecificationList)) {
                delegator.removeAll(screenSpecificationList);
            }
            Map inMap = FastMap.newInstance();
            Map outMap = FastMap.newInstance();

            inMap.put("clsId",clsId);
            for(Map tempMap: dataList){
                inMap.put("fieldName",tempMap.get("fieldName"));
                inMap.put("sequenceNum",tempMap.get("sequenceNum"));
                inMap.put("fieldService",tempMap.get("fieldService"));
                inMap.put("dataType",tempMap.get("dataType"));
                inMap.put("isMandatory",tempMap.get("isMandatory"));
                inMap.put("isCreate",tempMap.get("isCreate"));
                inMap.put("isView",tempMap.get("isView"));
                inMap.put("isEdit",tempMap.get("isEdit"));
                inMap.put("isDisabled",tempMap.get("isDisabled"));
                inMap.put("userLogin",userLogin);
                outMap = dispatcher.runSync("createScreenSpecification", inMap);
                if(ServiceUtil.isError(outMap) || ServiceUtil.isFailure(outMap)){
                    responseMessage = UtilProperties.getMessage(RESOURCE, "EditScreenSpecificationsFailed", locale);
                    results.put("clsId", clsId);
                    return results;
                }
            }

        } catch (GenericEntityException e){
            e.printStackTrace();
            Debug.logError(e.getMessage(), MODULE);
            results = ServiceUtil.returnError("Error : " + e.getMessage());
            results.put("clsId", clsId);
            return results;
        }
        catch (GenericServiceException e){
            e.printStackTrace();
            Debug.logError(e.getMessage(), MODULE);
            results = ServiceUtil.returnError("Error : " + e.getMessage());
            results.put("clsId", clsId);
            return results;
        }
        results.put("clsId", clsId);
        return results;
    }


}