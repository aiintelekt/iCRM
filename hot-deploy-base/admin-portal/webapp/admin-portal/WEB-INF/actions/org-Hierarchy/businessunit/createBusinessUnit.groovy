import org.fio.admin.portal.constant.AdminPortalConstant;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilMisc;
import java.util.HashMap;

import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.condition.EntityFieldValue;
import org.ofbiz.entity.condition.EntityFunction;
import java.util.LinkedHashMap;
import org.fio.admin.portal.util.DataHelper;

userLogin = request.getAttribute("userLogin");
delegator = request.getAttribute("delegator");

inputContext = new LinkedHashMap<String, Object>();
context.put("inputContext", inputContext);
List<GenericValue> buStatusDetails = delegator.findByAnd("Enumeration",  UtilMisc.toMap("enumTypeId", AdminPortalConstant.BusinessUnitConstant.STATUS_ID), null, false);
context.put("statusId", DataHelper.getDropDownOptions(buStatusDetails, "enumId", "description"));

ArrayList<String> typeIdList =  ['PHYSICAL', 'LOGICAL'];
List < GenericValue > buTypeDetails = EntityQuery.use(delegator).from("ProductStoreGroupType").where(EntityCondition.makeCondition("isEnabled", EntityOperator.EQUALS, "Y")).orderBy("-lastUpdatedTxStamp")queryList();
context.put("buTypeId", DataHelper.getDropDownOptions(buTypeDetails, "productStoreGroupTypeId", "description"));

