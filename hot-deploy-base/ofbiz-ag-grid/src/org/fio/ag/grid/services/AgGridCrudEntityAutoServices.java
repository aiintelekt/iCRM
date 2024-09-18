package org.fio.ag.grid.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fio.ag.grid.util.HttpUtil;
import org.fio.ag.grid.util.OfbizUtil;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.service.GeneralServiceException;


public class AgGridCrudEntityAutoServices {

	private static final String module = AgGridCrudEntityAutoServices.class.getName();

	/**
	from https://cwiki.apache.org/confluence/display/OFBIZ/Service+Engine+Guide
	
	The entity-auto engine can implement the following Create operations:
	1. a single OUT primary key for primary auto-sequencing
	2. a single INOUT primary key for primary auto-sequencing with optional override
	3. a multi-part primary key with all parts part IN except the last which is OUT only (the missing primary key is a sub-sequence mainly for entity association
	all primary key fields IN for a manually specified primary key
	
	For any more complex situation, write code for your own service instead of relying on an automatically defined one.	
	 * @throws GeneralServiceException 
	**/
	public static String dataCreate(HttpServletRequest request, HttpServletResponse response) throws GeneralServiceException {
		Map<String, Object> totalResults = multiCallEntityAutoService(request, true);
		try {
			HttpUtil.writeResultMapJSONResponse(totalResults, response);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "success";

	}

	public static String dataUpdate(HttpServletRequest request, HttpServletResponse response) throws GeneralServiceException {
		Map<String, Object> totalResults = multiCallEntityAutoService(request, false);
		try {
			HttpUtil.writeResultMapJSONResponse(totalResults, response);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "success";
	}

	public static String dataRemove(HttpServletRequest request, HttpServletResponse response) throws GeneralServiceException {
		Map<String, Object> totalResults = multiCallEntityAutoService(request, false);
		try {
			HttpUtil.writeResultMapJSONResponse(totalResults, response);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "success";

	}
	
	private static Map<String, Object> multiCallEntityAutoService(HttpServletRequest request, boolean create) throws GeneralServiceException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		List<Map<String, Object>> errors = new ArrayList<>();
		List<Map<String, Object>> successes = new ArrayList<>();
		List<Map<String, Object>> failedEntities = new ArrayList<>();
		String serviceName = request.getParameter("entity-auto");
		String jsonBodyStr = HttpUtil.getJsonStrBody(request);
		Debug.logInfo("[dataUpdate] service name: " + serviceName + ", request body: " + jsonBodyStr, module);
		List<Map<String, Object>> list = HttpUtil.jsonArrayStrToMapList(jsonBodyStr);
		if (list != null) {
			Debug.logInfo("payload as java object: " + list.toString(), module);
			for (Map<String, Object> entity : list) {
				Debug.logInfo("service call input: " + entity.toString(), module);
				Map<String, Object> result = OfbizUtil.runService(request, entity, serviceName);
				if(create && result.get("defaultEntityName") != null) {
					ModelEntity modelEntity = delegator.getModelEntity((String) result.get("defaultEntityName"));
					if(modelEntity.getPkFieldNames().size() > 1) {
						throw new GeneralServiceException("This service cannot deal with an entity with more than one pk field");
					}
					String firstPkFieldName = modelEntity.getFirstPkFieldName();
					Debug.logInfo("firstPkFieldName: " + firstPkFieldName, module);
					if(result.containsKey(firstPkFieldName) && entity.containsKey(firstPkFieldName) && entity.get(firstPkFieldName) == null) {
						Debug.logInfo("updating firstPkFieldName in entity from result", module);
						entity.put(firstPkFieldName, result.get(firstPkFieldName));
					}else {
						Debug.logInfo("Not updating firstPkFieldName in entity from result", module);
					}
					
				}
				Debug.logInfo("service call result: " + result.toString(), module);
				if (result.containsKey("errorMessage")) {
					errors.add(result);
					failedEntities.add(entity);
				}else {
					successes.add(entity);
				}
			}
		} else {
			Debug.logInfo("unmarshalled paylod is null ", module);
		}

		//have to return entities which are thoes where the operation was successful to can apply partial changes to the grid in the case of say remove
		//where some where successfully removed while others where not (this is not implemented for update only for remove at the moment, besides error notifications show for 
		//failed updates, reverting the cells wouldn't necessaryily be useful)
		if (errors.size() > 0) {
			return UtilMisc.toMap("successes", list.size() - errors.size(), "errors", errors, "entities", successes, "failedEntities", failedEntities);
		} else {
			return UtilMisc.toMap("successes", list.size(), "entities", successes);
		}

	}

}
