import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.util.EntityQuery

Map<String, Object> booleanList = new LinkedHashMap<String, Object>();
booleanList.put("true", "true");
booleanList.put("false", "false");
context.put("booleanList", booleanList);

Map<String, Object> columnFilterList = new LinkedHashMap<String, Object>();
columnFilterList.put("agTextColumnFilter", "Text");
columnFilterList.put("agNumberColumnFilter", "Number");
columnFilterList.put("agDateColumnFilter", "Date");
context.put("gridColumnFilterList", columnFilterList);

Map<String, Object> domLayoutList = new LinkedHashMap<String, Object>();
domLayoutList.put("normal", "normal");
domLayoutList.put("autoHeight", "autoHeight");
domLayoutList.put("print", "print");
context.put("domLayoutList", domLayoutList);

Map<String, Object> pageSizeList = new LinkedHashMap<String, Object>();
for(int i=1; i<=20; i++) {
	pageSizeList.put(i*10+"", i*10+"");
}
context.put("pageSizeList", pageSizeList);

Map<String, Object> rowSelectionList = new LinkedHashMap<String, Object>();
rowSelectionList.put("single", "single");
rowSelectionList.put("multiple", "multiple");
context.put("rowSelectionList", rowSelectionList);

String gridInstanceId = request.getParameter("gridInstanceId");
String gridUserId = request.getParameter("gridUserId");

GenericValue gridPref = EntityQuery.use(delegator).from("GridUserPreferences").where("instanceId",gridInstanceId,"userId",gridUserId,"role","ADMIN").queryFirst();
println ("gridPref--------->"+gridPref);
if(UtilValidate.isNotEmpty(gridPref))
	context.put("gridPreference",gridPref);


