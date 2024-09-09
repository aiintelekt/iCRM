import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
import java.util.HashMap;
import net.sf.json.JSONObject;
import org.ofbiz.base.util.UtilDateTime;
import java.util.TimeZone;

import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.GenericValue;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.entity.condition.EntityExpr;

delegator = request.getAttribute("delegator");
uiLabelMap = UtilProperties.getResourceBundleMap("common-portalUiLabels", locale);

inputContext = context.get("inputContext");

String partyId = request.getParameter("partyId");

if (UtilValidate.isNotEmpty(partyId)) {
	
	List conditionList = FastList.newInstance();
	conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
	//conditionList.add(EntityCondition.makeCondition("dataSourceId", EntityOperator.EQUALS, entry.getString("source")));
	conditionList.add(EntityUtil.getFilterByDateExpr());
	
	EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
	List<GenericValue> sources = delegator.findList("PartyDataSource", mainConditons, null, null, null, false);
    context.put("dataSources", sources);
    dataSourcesAsString = new StringBuffer();
	dataSourceId = "";
    for (GenericValue ds : sources) {
        dataSource = ds.getRelatedOne("DataSource");
        if (dataSource != null) {
			dataSourceId = dataSource.get("dataSourceId");
            dataSourcesAsString.append(dataSource.get("description"));
            dataSourcesAsString.append(", ");
        }
    }
    
    if (UtilValidate.isNotEmpty(dataSourcesAsString)) {
    	dataSourcesAsString = dataSourcesAsString.toString().substring(0, dataSourcesAsString.length()-2);
    }
    
   	context.put("dataSourcesAsString", dataSourcesAsString);
   	if (UtilValidate.isNotEmpty(inputContext)) {
		inputContext.put("dataSourcesNames", dataSourcesAsString);
		inputContext.put("dataSourceId", dataSourceId);
	}
}

context.put("inputContext", inputContext);
