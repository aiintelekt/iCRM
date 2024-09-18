import java.util.function.Function
import java.util.stream.Collectors

import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.util.EntityQuery

visitId = request.getParameter("visitId");
searchCriteria = request.getParameter("searchCriteria");
if(UtilValidate.isNotEmpty(visitId) && UtilValidate.isEmpty(searchCriteria)) {
	GenericValue visitGv = EntityQuery.use(delegator).from("Visit").where("visitId",visitId).queryOne();
	if(UtilValidate.isNotEmpty(visitGv)) {
		context.put("visit",visitGv);
	}
	
	Set<String> fieldsToSelect = new TreeSet<String>();
	fieldsToSelect.add("visitId");
	fieldsToSelect.add("contentId");
	fieldsToSelect.add("hitStartDateTime");
	fieldsToSelect.add("hitTypeId");
	fieldsToSelect.add("numOfBytes");
	fieldsToSelect.add("requestUrl");
	fieldsToSelect.add("lastUpdatedTxStamp");
	fieldsToSelect.add("createdTxStamp");
	List<Map<String, Object>> results = new ArrayList<Map<String,Object>>();
	List<GenericValue> serverHitList = EntityQuery.use(delegator).select(fieldsToSelect).from("ServerHit").where("visitId",visitId).orderBy("-lastUpdatedTxStamp").queryList();
	if(serverHitList != null && serverHitList.size() > 0)
	{
		for(GenericValue serverHit : serverHitList) {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("visitId", serverHit.getString("visitId"));
			data.put("contentId", serverHit.getString("contentId"));
			data.put("startDate", serverHit.getString("hitStartDateTime"));
			data.put("hitType", serverHit.getString("hitTypeId"));
			data.put("usedSize", serverHit.getString("numOfBytes"));
			data.put("url", serverHit.getString("requestUrl"));
			results.add(data);
		}
		context.put("serverHits", results);
	} else {
		
		request.setAttribute("_ERROR_MESSAGE_", "Invaild visit id");
	}
}	