


import java.sql.Timestamp;

import org.fio.crm.party.PartyHelper;
import org.fio.homeapps.util.DataUtil;
import org.fio.homeapps.util.EnumUtil
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.Debug;

String state = request.getParameter("stateId");
String county = request.getParameter("countyId");

inputContext = new LinkedHashMap<String, Object>();

if (UtilValidate.isNotEmpty(state) && UtilValidate.isNotEmpty(county)) {
	
	ProductStoreTechAssoc = from("ProductStoreTechAssoc").where("state", state, "county", county).queryOne();
	
	if (UtilValidate.isNotEmpty(ProductStoreTechAssoc)) {
		 state = ProductStoreTechAssoc.getString("state");
		 county = ProductStoreTechAssoc.getString("county");
		 
		String stateDesc = org.fio.admin.portal.util.DataUtil.getGeoName(delegator, state, "PROVINCE/STATE");
		
		String isTechInspection = ProductStoreTechAssoc.getString("isTechInspection");
		String productStoreId = ProductStoreTechAssoc.getString("productStoreId");
		String productStoreName = ProductStoreTechAssoc.getString("productStoreName");
		
		String technician1 = ProductStoreTechAssoc.getString("technicianId01");
		String technician2 = ProductStoreTechAssoc.getString("technicianId02");
		String technician3 = ProductStoreTechAssoc.getString("technicianId03");
		String technician4 = ProductStoreTechAssoc.getString("technicianId04");
		String coordinator = ProductStoreTechAssoc.getString("coordinator");
		
		String technicianName1 = ProductStoreTechAssoc.getString("technicianName01");
		String technicianName2 = ProductStoreTechAssoc.getString("technicianName02");
		String technicianName3 = ProductStoreTechAssoc.getString("technicianName03");
		String technicianName4 = ProductStoreTechAssoc.getString("technicianName04");
		String coordinatorName = ProductStoreTechAssoc.getString("coordinatorName");
		
		context.put("generalCountyGeoId", county);
		context.put("generalStateProvinceGeoId", state);
		
		inputContext.put("state", state);
		inputContext.put("generalStateProvinceGeoId", stateDesc);
		
		inputContext.put("countyGeoId", county);
		inputContext.put("isTechInspection", isTechInspection);
		//inputContext.put("productStoreId", productStoreId);
		inputContext.put("productStoreId", productStoreName);
		inputContext.put("productStoreDesc", productStoreName);
		/*
		inputContext.put("technician1", UtilValidate.isNotEmpty(technician1) ? technician1 : "");
		inputContext.put("technician2", UtilValidate.isNotEmpty(technician2) ? technician2 : "");
		inputContext.put("technician3", UtilValidate.isNotEmpty(technician3) ? technician3 : "");
		inputContext.put("technician4", UtilValidate.isNotEmpty(technician4) ? technician4 : "");
		*/
		inputContext.put("technician1", UtilValidate.isNotEmpty(technicianName1) ? technicianName1 : "");
		inputContext.put("technician2", UtilValidate.isNotEmpty(technicianName2) ? technicianName2 : "");
		inputContext.put("technician3", UtilValidate.isNotEmpty(technicianName3) ? technicianName3 : "");
		inputContext.put("technician4", UtilValidate.isNotEmpty(technicianName4) ? technicianName4 : "");
		inputContext.put("coordinator", UtilValidate.isNotEmpty(coordinatorName) ? coordinatorName : "");
		
		
		inputContext.put("technician1Desc", UtilValidate.isNotEmpty(technicianName1) ? technicianName1 : "");
		inputContext.put("technician2Desc", UtilValidate.isNotEmpty(technicianName2) ? technicianName2 : "");
		inputContext.put("technician3Desc", UtilValidate.isNotEmpty(technicianName3) ? technicianName3 : "");
		inputContext.put("technician4Desc", UtilValidate.isNotEmpty(technicianName4) ? technicianName4 : "");
		inputContext.put("coordinatorDesc", UtilValidate.isNotEmpty(coordinatorName) ? coordinatorName : "");
		
		
		GenericValue geoAssoc = EntityQuery.use(delegator).from("GeoAssoc").where("geoIdTo","MA","geoAssocTypeId","REGIONS").queryFirst();
		inputContext.put("generalCountryGeoId", UtilValidate.isNotEmpty(geoAssoc) ? geoAssoc.getString("geoId") : "");
	}
	
	context.put("inputContext", inputContext);
}


