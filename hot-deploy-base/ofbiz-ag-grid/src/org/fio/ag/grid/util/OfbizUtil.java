package org.fio.ag.grid.util;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.ofbiz.base.lang.JSON;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelParam;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;



public class OfbizUtil {
	
	private static final String module = OfbizUtil.class.getName();
	
	public static ModelService getServiceModel(HttpServletRequest request, String serviceName)
			throws GenericServiceException {
		ModelService modelService = getDispatcher(request).getDispatchContext().getModelService(serviceName);
		return modelService;
	}
	
	public static LocalDispatcher getDispatcher(HttpServletRequest request) {
		return (LocalDispatcher) request.getAttribute("dispatcher");
	}
	
	public static Map<String, Object> runService(HttpServletRequest request, Map<String, Object> inputContext, String serviceName) {
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		if(userLogin != null) {
			inputContext.put("userLogin", userLogin);
		}
		Map<String, Object> result = null;
		try {
			ModelService modelService = getServiceModel(request, serviceName);
			parseJsonInToJavaType(inputContext, modelService);
			inputContext = modelService.makeValid(inputContext, "IN");
			Debug.logInfo("valid inputContext: " + inputContext.toString(), module);
			Debug.logInfo("Running service: " + serviceName, module);
			result = getDispatcher(request).runSync(serviceName, inputContext);
			result.put("defaultEntityName", modelService.defaultEntityName);
			Debug.logInfo("service call result: " + result.toString(), module);
		} catch (Exception e) {
			Debug.logError(e, "Problems with service invocation.", module);
			// {errorMessage=The following required parameter is missing: [updatePinCodeExample.id], responseMessage=fail}
			return ServiceUtil.returnFailure(e.getMessage());
		}

		//service call result: {responseMessage=success}
		return result;
	}
	
	public static void parseJsonInToJavaType(Map<String, Object> context, ModelService modelService)
			throws IOException {
		for (ModelParam in : (List<ModelParam>) getInModelParamList(modelService)) {
			if (in.type.toLowerCase().endsWith("map")) {
				if (context.get(in.name) != null) {
					Map<String, Object> nestedMap = JSON.from((String) context.get(in.name)).toObject(Map.class);
					context.put(in.name, nestedMap);
				}

			} else if (in.type.toLowerCase().endsWith("timestamp") || in.type.toLowerCase().endsWith("date")) {
				if (context.get(in.name) != null) {
					String time = (String) context.get(in.name);
					// "2017-07-07T22:00:00.000Z" or 1981-06-17
					time = time.replaceAll("\"", "");
					Date parsedDate = null;
					try {
						// 2017-07-07T22:00:00.000Z
						DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
						parsedDate = df.parse(time);
					} catch (Exception e) {
						// 1981-06-17
						DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
						try {
							parsedDate = df2.parse(time);
						} catch (java.text.ParseException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					context.put(in.name, UtilDateTime.getTimestamp(parsedDate.getTime()));
				}

			} else if (in.type.toLowerCase().endsWith("list")) {
				if (context.get(in.name) != null) {
					String listJson = (String) context.get(in.name);
					List<Map<String, Object>> list = JSON.from(listJson).toObject(List.class);
					context.put(in.name, list);
				}

			} else if (in.type.toLowerCase().endsWith("double")) {
				if (context.get(in.name) != null) {
					String doubleStr = (String) context.get(in.name);
					context.put(in.name, new Double(doubleStr.trim()));
				}

			} else if (in.type.toLowerCase().endsWith("long")) {
				if (context.get(in.name) != null) {
					String longStr = (String) context.get(in.name);
					context.put(in.name, new Long(longStr.trim()));
				}

			}
		}
	}
	
	private static Object getInModelParamList(ModelService modelService) {
		List<ModelParam> inList = new LinkedList<ModelParam>();
		for (ModelParam modelParam : modelService.getInModelParamList()) {
			if (modelParam.internal)
				continue;
			inList.add(modelParam);
		}
		return inList;
	}

}
