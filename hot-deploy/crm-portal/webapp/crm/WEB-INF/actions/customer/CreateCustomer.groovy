import org.ofbiz.base.util.Debug
import org.ofbiz.base.util.UtilHttp

Map paramMap = UtilHttp.getParameterMap(request);
context.put("paramMap", paramMap);

println ("paramMap------------>"+paramMap);
List leaveList = [];
List nonCoreList = [];
Map leaveMap = new HashMap();
int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
if (rowCount > 1) {
	for (int i = 0; i < rowCount; i++) {
		String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;
		String leaveType = "";
		String nonCoreType = "";
		String leaveDuration = "";
		String nonCoreDuration = "";
		   Map<String, Object> leaveData = new HashMap<String, Object>();
		   Map<String, Object> nonCoreData = new HashMap<String, Object>();
		if(paramMap.get("leaveType" +thisSuffix)){
				leaveType = paramMap.get("leaveType" +thisSuffix)
		}
		if(paramMap.get("leaveDuration" +thisSuffix)){
				leaveDuration = paramMap.get("leaveDuration" +thisSuffix)
		}
		if(leaveType){
			leaveData.put("leaveType", leaveType);
			  leaveData.put("leaveDuration", leaveDuration);
			  leaveList.add(leaveData);
			  
		}
		if(paramMap.get("nonCoreType" +thisSuffix)){
				nonCoreType = paramMap.get("nonCoreType" +thisSuffix)
		}
		if(paramMap.get("nonCoreDuration" +thisSuffix)){
				nonCoreDuration = paramMap.get("nonCoreDuration" +thisSuffix)
		}
		nonCoreData.put("nonCoreType", nonCoreType);
		  nonCoreData.put("nonCoreDuration", nonCoreDuration);
		  nonCoreList.add(nonCoreData);
	}
}
context.leaveList = leaveList;
Debug.log("=========leaveList========11==="+leaveList);
context.nonCoreList = nonCoreList;