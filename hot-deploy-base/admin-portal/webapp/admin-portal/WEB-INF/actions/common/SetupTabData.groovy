import javax.servlet.http.HttpServletRequestWrapper

import org.ofbiz.base.util.UtilHttp
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.cache.UtilCache
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.widget.renderer.ScreenRenderer


Delegator delegator = request.getAttribute("delegator");
String componentId = request.getParameter("componentId");
String tabConfigId = request.getParameter("tabConfigId");
String tabId = request.getParameter("tabId");
String requestUri = request.getParameter("requestUri");
String uniqueId = request.getParameter("uniqueId");

context.put("tabIdForCurrentTab", tabId);

Map<String, Object> reqMap = UtilHttp.getParameterMap(request);


//((HttpServletRequestWrapper)request).setRequestURI(requestUri);

ScreenRenderer screenRed = session.getAttribute("mainScreenRenderer-"+uniqueId);
Map<String, Object> contextMap = screenRed.getContext();

contextMap.putAll(org.fio.homeapps.util.UtilHttp.getCombinedMap(request));
//println ("contextMap----------->"+contextMap);

Map<String, Object> duplicateMap = new HashMap<String, Object>();
duplicateMap.putAll(contextMap);
duplicateMap.remove("Request");
duplicateMap.remove("response");
duplicateMap.remove("request");
duplicateMap.remove("screens");


if(UtilValidate.isNotEmpty(duplicateMap)) {
	for(String key : duplicateMap.keySet()) {
		context.put(key, duplicateMap.get(key));
	}
}
context.put("requestUri", requestUri);

String externalLoginKey = request.getAttribute("externalLoginKey");
context.put("externalLoginKey", externalLoginKey);

Map<String, Object> data = new HashMap<String, Object>();
if(UtilValidate.isNotEmpty(componentId) && UtilValidate.isNotEmpty(tabConfigId) && UtilValidate.isNotEmpty(tabId)) {
	componentId = UtilValidate.isNotEmpty(componentId)?componentId.replace("-", "_").toUpperCase():"";
	
	GenericValue navTabsConfig = EntityQuery.use(delegator).from("NavTabsConfig").where("componentId",componentId,"tabConfigId",tabConfigId,"tabId", tabId).cache(true).queryFirst();
	//println ("navTabsConfig-------------->"+navTabsConfig);
	if(UtilValidate.isNotEmpty(navTabsConfig)) {
		request.setAttribute("tabContent", navTabsConfig.getString("tabContent"));
		request.setAttribute("tabContent", navTabsConfig.getString("tabContent"));
		data.put("tabContent", navTabsConfig.getString("tabContent"));
		data.put("location", request.getParameter("location"));
		context.put("tabContent", navTabsConfig.getString("tabContent"));
	}
}


/*
String contextDataJson = parameters.contextData;

if(UtilValidate.isNotEmpty(contextDataJson)) {
	Map<String, Object> contextData = DataUtil.convertToMapIgnore(contextDataJson);
	if(UtilValidate.isNotEmpty(contextData)) {
		for(String key : contextData.keySet()) {
			context.put(key, contextData.get(key));
		}
	}
}
*/


/*
String inputDataJson = parameters.inputContext;

Map<String, Object> inputData = new HashMap();
if(UtilValidate.isNotEmpty(inputDataJson)) {
	inputData = DataUtil.convertToMapIgnore(inputDataJson);
}
context.put("inputContext", inputData);
*/
