package org.fio.admin.portal.event;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;
import org.fio.admin.portal.constant.AdminPortalConstant.DateTimeTypeConstant;
import org.fio.admin.portal.util.DataUtil;
import org.ofbiz.base.component.ComponentConfig;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.HttpRequestFileUpload;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.jdbc.SQLProcessor;
import org.ofbiz.entity.model.ModelField.EncryptMethod;
import org.ofbiz.entity.model.ModelUtil;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityCrypto;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
/**
 * 
 * @author Mahendran Thanasekaran
 * @since 16-08-2019
 *
 */
public class CommonEvents {
	private CommonEvents() {}
	private static final String MODULE = CommonEvents.class.getName();
	private static final String RESOURCE = "AdminPortalUiLabels";
	public static String doJSONResponse(HttpServletResponse response, JSONObject jsonObject) {
		return doJSONResponse(response, jsonObject.toString());
	}

	public static String doJSONResponse(HttpServletResponse response, Collection < ? > collection) {
		return doJSONResponse(response, JSONArray.fromObject(collection).toString());
	}

	public static String doJSONResponse(HttpServletResponse response, Map map) {
		return doJSONResponse(response, JSONObject.fromObject(map));
	}

	public static String doJSONResponse(HttpServletResponse response, String jsonString) {
		String result = "success";

		response.setContentType("application/x-json");
		try {
			response.setContentLength(jsonString.getBytes("UTF-8").length);
		} catch (UnsupportedEncodingException e) {
			Debug.logWarning("Could not get the UTF-8 json string due to UnsupportedEncodingException: " + e.getMessage(), MODULE);
			response.setContentLength(jsonString.length());
		}

		Writer out;
		try {
			out = response.getWriter();
			out.write(jsonString);
			out.flush();
		} catch (IOException e) {
			Debug.logError(e, "Failed to get response writer", MODULE);
			result = "error";
		}
		return result;
	}
	public static String returnError(HttpServletRequest request, String errorMessage) {
		try {
			request.setAttribute("_ERROR_MESSAGE_", "ERROR :" + errorMessage);
			Debug.logError("Error : " + errorMessage, MODULE);
		} catch (Exception e) {
			Debug.logError("Error : " + e.getMessage(), MODULE);
		}
		return "error";
	}
	public static String returnSuccess(HttpServletRequest request, String successMessage) {
		try {
			request.setAttribute("_EVENT_MESSAGE_", successMessage);
			Debug.logError("Success : " + successMessage, MODULE);
		} catch (Exception e) {
			Debug.logError("Error : " + e.getMessage(), MODULE);
		}
		return "success";
	}
	public static GenericValue getUserLogin(HttpServletRequest request) {
		HttpSession session = request.getSession();
		return (GenericValue) session.getAttribute("userLogin");
	}
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws GenericEntityException
	 * Add the security permission to the security group
	 */
	public static String addPermissionToSecurityGroup(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		Map<String, Object> result = new HashMap<String, Object>();
		Locale locale = UtilHttp.getLocale(request);
		Map < String, Object > requestParameters = UtilHttp.getCombinedMap(request);
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		String groupId = (String) requestParameters.get("groupId");
		String selectedRows = (String) requestParameters.get("selectedRows");
		String responseMessage = UtilProperties.getMessage(RESOURCE, "SecurityPermissionAssocSuccessfully", locale);
		try {
			if(UtilValidate.isNotEmpty(selectedRows)) {
				List<Map<String, Object>> requestMapList = new ArrayList<Map<String, Object>>();
				if(UtilValidate.isNotEmpty(selectedRows))
					requestMapList = DataUtil.convertToListMap(selectedRows);
				if(UtilValidate.isNotEmpty(requestMapList)) {
					TransactionUtil.begin(400000);
					for(Map<String, Object> requestMap : requestMapList) {
						String permissionId = (String) requestMap.get("permissionId");

						GenericValue groupList = EntityQuery.use(delegator).from("SecurityGroupPermission").where("groupId",groupId,"permissionId",permissionId).queryFirst();
						if(UtilValidate.isEmpty(groupList)) {
							Map < String, Object > context = new HashMap < String, Object > ();
							context.put("groupId", groupId);
							context.put("permissionId", permissionId);
							context.put("userLogin", userLogin);
							result = dispatcher.runSync("addSecurityPermissionToSecurityGroup", context);
						}
					}
					TransactionUtil.commit();
					if(!ServiceUtil.isSuccess(result)) {
						responseMessage = UtilProperties.getMessage(RESOURCE,ServiceUtil.getErrorMessage(result), locale);
					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			TransactionUtil.rollback();
			return returnError(request, e.getMessage());
		}
		request.setAttribute("groupId", groupId);
		return returnSuccess(request, responseMessage);
	}
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws GenericEntityException
	 * Remove the security group and permission association
	 */
	public static String removeSercurityPermission(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		Map<String, Object> result = new HashMap<String, Object>();
		Locale locale = UtilHttp.getLocale(request);
		Map < String, Object > requestParameters = UtilHttp.getCombinedMap(request);
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		String groupId = (String) requestParameters.get("groupId");
		String requestData = DataUtil.getJsonStrBody(request);
		String responseMessage = UtilProperties.getMessage(RESOURCE, "SecurityPermissionAssocSuccessfullyRemoved", locale);
		try {

			List<Map<String, Object>> dataList = DataUtil.convertToListMap(requestData);
			List<GenericValue> toBeRemove = new LinkedList<GenericValue>();
			if(UtilValidate.isNotEmpty(dataList)) {
				TransactionUtil.begin(400000);
				for(Map<String, Object> data : dataList) {
					String securityPermissionId = (String) data.get("permissionId");
					groupId = (String) data.get("groupId");
					Map < String, Object > context = new HashMap < String, Object > ();
					context.put("groupId", groupId);
					context.put("permissionId", securityPermissionId);
					context.put("userLogin", userLogin);
					result = dispatcher.runSync("removeSecurityPermissionFromSecurityGroup", context);
				}
				TransactionUtil.commit();
				if(!ServiceUtil.isSuccess(result)) {
					responseMessage = UtilProperties.getMessage(RESOURCE,ServiceUtil.getErrorMessage(result), locale);
					result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
					result.put(ModelService.ERROR_MESSAGE, responseMessage);
					return CommonEvents.doJSONResponse(response, result);
				}
			} else {
				responseMessage = UtilProperties.getMessage(RESOURCE,"RequiredParameterMissing", locale);
				result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
				result.put(ModelService.ERROR_MESSAGE, responseMessage);
				return CommonEvents.doJSONResponse(response, result);
			}
		} catch (Exception e) {
			e.printStackTrace();
			TransactionUtil.rollback();
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
			return CommonEvents.doJSONResponse(response, result);
		}
		request.setAttribute("groupId", groupId);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		result.put(ModelService.SUCCESS_MESSAGE, responseMessage);
		return CommonEvents.doJSONResponse(response, result);
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws GenericEntityException
	 * 
	 * Generate the entity operations and corresponding security permission
	 */
	public static String generateEntityOperations(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Locale locale = UtilHttp.getLocale(request);
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		Map < String, Object > requestParameters = UtilHttp.getCombinedMap(request);
		String entityName = (String) requestParameters.get("entityName");
		String entityType = (String) requestParameters.get("entityType");
		String roleTypeId = (String) requestParameters.get("roleTypeId");
		String entityAliasName = (String) requestParameters.get("entityAliasName");
		String entityOperations = (String) requestParameters.get("entityOperations");
		String isEnabled = requestParameters.get("isEnabled") != null ? (String) requestParameters.get("isEnabled") : "Y";
		String responseMessage = UtilProperties.getMessage(RESOURCE, "EntityOpsConfigurationCreatedSuccessfully", locale);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			if (UtilValidate.isNotEmpty(entityOperations) && UtilValidate.isNotEmpty(entityName)) {
				List < String > operations = Stream.of(entityOperations.split(",")).map(Object::toString).collect(Collectors.toCollection(LinkedList::new));

				//Check entity configuration exists or not
				List<GenericValue> entityConfigExists = null;
				if("PARTY_ENTITY".equals(entityType)) {
					entityConfigExists = EntityQuery.use(delegator).from("EntityOperationConfig").where("entityName",entityName,"roleTypeId",roleTypeId).queryList();
				} else {
					entityConfigExists = EntityQuery.use(delegator).from("EntityOperationConfig").where("entityName",entityName).queryList();
				}
				//List<GenericValue> entityConfigExists = EntityQuery.use(delegator).from("EntityOperationConfig").where("entityName",entityName).queryList();
				if(entityConfigExists != null && entityConfigExists.size() > 0) {
					delegator.removeAll(entityConfigExists);
					responseMessage = UtilProperties.getMessage(RESOURCE, "EntityOpsConfigurationUpdatedSuccessfully", locale);
				}
				EntityCondition condition = EntityCondition.makeCondition(EntityOperator.OR,
						EntityCondition.makeCondition("permissionId",EntityOperator.LIKE,entityName+"\\_%"),
						EntityCondition.makeCondition("permissionId",EntityOperator.LIKE,entityAliasName+"\\_%")
						);
				List<GenericValue> entitySecurityPermissionExists = EntityQuery.use(delegator).from("SecurityPermission").where(condition).queryList();
				if(entitySecurityPermissionExists != null && entitySecurityPermissionExists.size() > 0) {
					delegator.removeAll(entitySecurityPermissionExists);
				}

				int seqId = 1;
				for (String operation: operations) {
					//create entity configuration
					condition = EntityCondition.makeCondition(
							EntityOperator.AND, EntityCondition.makeCondition("entityName", EntityOperator.EQUALS, entityName), EntityCondition.makeCondition("entityAliasName", EntityOperator.EQUALS, entityAliasName), EntityCondition.makeCondition("operationName", EntityOperator.EQUALS, operation));
					GenericValue entityOps = EntityQuery.use(delegator).from("EntityOperationConfig").where(condition).queryFirst();
					if (UtilValidate.isEmpty(entityOps)) {
						entityOps = delegator.makeValue("EntityOperationConfig");
						entityOps.set("entityName", entityName);
						entityOps.set("entityAliasName", entityAliasName);
						entityOps.set("entityType", entityType);
						entityOps.set("roleTypeId", roleTypeId);
						entityOps.set("operationName", operation);
						entityOps.set("seqId", StringUtils.leftPad(seqId + "", 2, "0"));
						entityOps.set("isEnabled", isEnabled);
						entityOps.set("createdOn", DataUtil.convertDateTimestamp(LocalTime.now().toString(), df, DateTimeTypeConstant.TIMESTAMP, DateTimeTypeConstant.TIMESTAMP));
						entityOps.set("createdBy", userLogin.getString("userLoginId"));
						entityOps.create();
					}
					seqId = seqId + 1;
					//security permission
					String operationId = ("PARTY_ENTITY".equals(entityType) ? entityAliasName : entityName) + "_" + (operation.replace(" ", "_")).toUpperCase();
					GenericValue securityPermission = EntityQuery.use(delegator).from("SecurityPermission").where("permissionId", operationId).queryFirst();
					if (UtilValidate.isEmpty(securityPermission)) {
						securityPermission = delegator.makeValue("SecurityPermission");
						securityPermission.set("permissionId", operationId);
						securityPermission.set("description",  ("PARTY_ENTITY".equals(entityType) ? entityAliasName : entityName) + " " + operation);
						securityPermission.create();
					}
				}
			}
		} catch (Exception e) {
			//e.printStackTrace();
			return returnError(request, e.getMessage());
		}
		request.setAttribute("entityName", entityName);
		request.setAttribute("roleTypeId", roleTypeId);
		return returnSuccess(request, responseMessage);
	}

	//throws GenericEntityException
	public static String createScreenConfigEvent(HttpServletRequest request, HttpServletResponse response)  {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		Map<String, Object> result = new HashMap<String, Object>();
		Locale locale = UtilHttp.getLocale(request);
		Map < String, Object > requestParameters = UtilHttp.getCombinedMap(request);
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		String responseMessage = UtilProperties.getMessage(RESOURCE, "CreateScreenConfigsSuccessful", locale);
		String clsId = (String)  requestParameters.get("clsId");
		String module = (String) requestParameters.get("module");
		String layout = (String) requestParameters.get("layout");
		String screen = (String) requestParameters.get("screen");
		String screenService = (String) requestParameters.get("screenService");
		String requestUri = (String) requestParameters.get("requestUri");

		try {
			Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
			int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
			if(rowCount<1){
				responseMessage = "No Rows to process, rowCount= "+rowCount;
				Debug.logError(responseMessage, MODULE);
				return returnError(request, responseMessage);
			}

			String fieldName = "";
			String sequenceNum = "";
			String fieldService = "";
			String dataType = "";
			String isMandatory = "";
			String isCreate = "";
			String isView = "";
			String isEdit = "";
			String isDisabled = "";
			List<Map> dataList = FastList.newInstance();
			Map inMap = FastMap.newInstance();
			Map outMap = FastMap.newInstance();

			for(int i=0; i<rowCount; i++){
				String suffix = UtilHttp.MULTI_ROW_DELIMITER + i;
				Map tempMap = FastMap.newInstance();

				if(paramMap.containsKey("fieldName"+suffix)){
					fieldName = (String) paramMap.get("fieldName"+suffix);
					tempMap.put("fieldName",fieldName);
				}
				if(UtilValidate.isNotEmpty(fieldName)) {
					if(paramMap.containsKey("sequenceNum"+suffix)){
						sequenceNum = (String) paramMap.get("sequenceNum"+suffix);
						tempMap.put("sequenceNum",sequenceNum);
					}
					if(paramMap.containsKey("fieldService"+suffix)){
						fieldService = (String) paramMap.get("fieldService"+suffix);
						tempMap.put("fieldService",fieldService);
					}
					if(paramMap.containsKey("dataType"+suffix)){
						dataType = (String) paramMap.get("dataType"+suffix);
						tempMap.put("dataType",dataType);
					}
					if(paramMap.containsKey("isMandatory"+suffix)){
						isMandatory = (String) paramMap.get("isMandatory"+suffix);
						tempMap.put("isMandatory",isMandatory);
					}
					if(paramMap.containsKey("isCreate"+suffix)){
						isCreate = (String) paramMap.get("isCreate"+suffix);
						tempMap.put("isCreate",isCreate);
					}
					if(paramMap.containsKey("isView"+suffix)){
						isView = (String) paramMap.get("isView"+suffix);
						tempMap.put("isView",isView);
					}
					if(paramMap.containsKey("isEdit"+suffix)){
						isEdit = (String) paramMap.get("isEdit"+suffix);
						tempMap.put("isEdit",isEdit);
					}
					if(paramMap.containsKey("isDisabled"+suffix)){
						isDisabled = (String) paramMap.get("isDisabled"+suffix);
						tempMap.put("isDisabled",isDisabled);
					}
					dataList.add(tempMap);
				}

			}
			inMap.put("userLogin",userLogin);
			inMap.put("mountPoint",module);
			inMap.put("layout",layout);
			inMap.put("screen",screen);
			inMap.put("screenService",screenService);
			inMap.put("requestUri",requestUri);
			inMap.put("dataList",dataList);

			if(UtilValidate.isNotEmpty(clsId)){
				inMap.put("clsId",clsId);
				outMap = dispatcher.runSync("editScreenConfigService", inMap);
			}
			else
				outMap = dispatcher.runSync("createScreenConfigService", inMap);

			if(ServiceUtil.isError(outMap) || ServiceUtil.isFailure(outMap)){
				responseMessage = "createScreenConfigService encountered errors.";
				return returnError(request, responseMessage);
			}



		} catch (Exception e) {
			e.printStackTrace();
			return returnError(request, e.getMessage());
		}
		//request.setAttribute("groupId", groupId);
		return returnSuccess(request, responseMessage);
	}

	public static String getScreenConfigurationDetails(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		String clsId = request.getParameter("clsId");
		String mountPoint = request.getParameter("mountPoint");
		String layout = request.getParameter("layout");
		String requestUri = request.getParameter("requestUri");
		String screenService = request.getParameter("screenService");

		Map<String, Object> returnMap = FastMap.newInstance();
		List<Object> findConfigsData = FastList.newInstance();

		try {

			List<EntityCondition> conditions = new ArrayList<EntityCondition>();

			if (UtilValidate.isNotEmpty(clsId))
				conditions.add(EntityCondition.makeCondition("clsId",EntityOperator.EQUALS,clsId));
			if (UtilValidate.isNotEmpty(mountPoint))
				conditions.add(EntityCondition.makeCondition("mountPoint",EntityOperator.EQUALS,mountPoint));
			if (UtilValidate.isNotEmpty(layout))
				conditions.add(EntityCondition.makeCondition("layout",EntityOperator.EQUALS,layout));

			List<GenericValue> compLayoutList = EntityQuery.use(delegator).from("ComponentLayoutScreen").
					where(EntityCondition.makeCondition(conditions,EntityOperator.AND)).queryList();

			if (compLayoutList != null && compLayoutList.size() > 0) {

				for (GenericValue eachComponent : compLayoutList) {
					Map<String, Object> compDetails = new HashMap<String, Object>();
					String clsIdValue = eachComponent.getString("clsId");
					String mountPointValue = eachComponent.getString("mountPoint");
					String layoutValue = eachComponent.getString("layout");
					String requestUriValue = eachComponent.getString("requestUri");
					String screenServiceValue = eachComponent.getString("screenService");
					compDetails.put("clsId", clsIdValue);
					compDetails.put("mountPoint", mountPointValue);
					compDetails.put("layout", layoutValue);
					compDetails.put("requestUri", requestUriValue);
					compDetails.put("screenService", screenServiceValue);
					compDetails.put("screen", eachComponent.getString("screen"));

					findConfigsData.add(compDetails);
				}
				returnMap.put("data", findConfigsData);

			}else {
				returnMap.put("data", findConfigsData);
				return CommonEvents.doJSONResponse(response, returnMap);
			}

		}catch (Exception e) {
			returnMap.put("data", findConfigsData);
			return CommonEvents.doJSONResponse(response, returnMap);
		}
		return CommonEvents.doJSONResponse(response, returnMap);

	}

	public static String getClsSpecificationDetails(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		String clsId = request.getParameter("clsId");
		String fieldId = request.getParameter("fieldId");
		String start = request.getParameter("start");
		String length = request.getParameter("length");

		Map<String, Object> returnMap = FastMap.newInstance();
		List<Object> findConfigsData = FastList.newInstance();

		try {
			List<EntityCondition> conditions = new ArrayList<EntityCondition>();
			conditions.add(EntityCondition.makeCondition("clsId",EntityOperator.EQUALS,clsId));

			List<GenericValue> screenSpecificationList = EntityQuery.use(delegator).from("ScreenSpecification").
					where(EntityCondition.makeCondition(conditions,EntityOperator.AND)).queryList();

			if (screenSpecificationList != null && screenSpecificationList.size() > 0) {

				for (GenericValue eachComponent : screenSpecificationList) {
					Map<String, Object> compDetails = new HashMap<String, Object>();
					String clsIdValue = eachComponent.getString("clsId");
					String fieldIdValue = eachComponent.getString("fieldId");
					String sequenceNumValue = eachComponent.getString("sequenceNum");
					String isDisabledValue = eachComponent.getString("isDisabled");
					String isMandatoryValue = eachComponent.getString("isMandatory");
					String fieldNameValue = eachComponent.getString("fieldName");
					String dataTypeValue = eachComponent.getString("dataType");
					String isCreateValue = eachComponent.getString("isCreate");
					String isViewValue = eachComponent.getString("isView");
					String isEditValue = eachComponent.getString("isEdit");
					String fieldServiceValue = eachComponent.getString("fieldService");


					compDetails.put("clsId", clsIdValue);
					compDetails.put("fieldId", fieldIdValue);
					compDetails.put("sequenceNum", sequenceNumValue);
					compDetails.put("isDisabled", isDisabledValue);
					compDetails.put("isMandatory", isMandatoryValue);
					compDetails.put("fieldName", fieldNameValue);
					compDetails.put("dataType", dataTypeValue);
					compDetails.put("isCreate", isCreateValue);
					compDetails.put("isView", isViewValue);
					compDetails.put("isEdit", isEditValue);
					compDetails.put("fieldService", fieldServiceValue);
					findConfigsData.add(compDetails);
				}
				returnMap.put("data", findConfigsData);

			}else {
				returnMap.put("data", findConfigsData);
				return CommonEvents.doJSONResponse(response, returnMap);
			}

		}catch (Exception e) {

			returnMap.put("data", findConfigsData);
			return CommonEvents.doJSONResponse(response, returnMap);

		}
		return CommonEvents.doJSONResponse(response, returnMap);

	}

	@SuppressWarnings("unchecked")
	public static String createRoleSecurityAssoc(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		Map<String, Object> result = new HashMap<String, Object>();
		Locale locale = UtilHttp.getLocale(request);
		Map < String, Object > requestParameters = UtilHttp.getCombinedMap(request);
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		String responseMessage = UtilProperties.getMessage(RESOURCE, "SecuritySuccessfullyAssocWithRole", locale);
		String roleTypeId = (String) requestParameters.get("roleTypeId");
		//String groupId = (String) requestParameters.get("groupId");

		List<String> groupList = new ArrayList<>();
		if(UtilValidate.isNotEmpty(requestParameters.get("groupId")) && requestParameters.get("groupId") instanceof String) {
			String reasonCode = (String) requestParameters.get("groupId");
			if(UtilValidate.isNotEmpty(reasonCode)) groupList.add(reasonCode);
		} else if(UtilValidate.isNotEmpty(requestParameters.get("groupId")) && requestParameters.get("groupId") instanceof List<?>) {
			groupList = (List<String>) requestParameters.get("groupId");
		}
		try {
			if(UtilValidate.isNotEmpty(roleTypeId) && UtilValidate.isNotEmpty(groupList)) {
				for(String groupId : groupList) {
					GenericValue roleSecurityAssoc = EntityQuery.use(delegator).from("SecurityGroupRoleTypeAssoc").where("groupId", groupId, "roleTypeId", roleTypeId).queryFirst();
					if(UtilValidate.isEmpty(roleSecurityAssoc)) {
						roleSecurityAssoc = delegator.makeValue("SecurityGroupRoleTypeAssoc");
						roleSecurityAssoc.set("groupId", groupId);
						roleSecurityAssoc.set("roleTypeId", roleTypeId);
						roleSecurityAssoc.set("isDisabled", "N");
						roleSecurityAssoc.set("lastModifiedUserLoginId",userLogin.getString("userLoginId"));
						roleSecurityAssoc.create();
					} else {
						roleSecurityAssoc.set("isDisabled", (String) requestParameters.get("isDisabled"));
						roleSecurityAssoc.set("lastModifiedUserLoginId",userLogin.getString("userLoginId"));
						roleSecurityAssoc.store();
					}
				}
			} else {
				responseMessage = UtilProperties.getMessage(RESOURCE,"RequiredParameterMissing", locale);
				return returnSuccess(request, responseMessage);
			}

		} catch (Exception e) {
			//e.printStackTrace();
			return returnSuccess(request, e.getMessage());
		}
		request.setAttribute("roleTypeId", roleTypeId);
		return returnSuccess(request, responseMessage);
	}

	public static String removeRoleSercurityGroupAssoc(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map<String, Object> result = new HashMap<String, Object>();
		Locale locale = UtilHttp.getLocale(request);
		Map < String, Object > requestParameters = UtilHttp.getCombinedMap(request);
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		String groupId = (String) requestParameters.get("groupId");
		String requestData = DataUtil.getJsonStrBody(request);
		String responseMessage = UtilProperties.getMessage(RESOURCE, "AssocicationSuccessfullyRemoved", locale);
		try {

			List<Map<String, Object>> dataList = DataUtil.convertToListMap(requestData);
			List<GenericValue> toBeRemove = new LinkedList<GenericValue>();
			if(UtilValidate.isNotEmpty(dataList)) {
				TransactionUtil.begin(400000);
				for(Map<String, Object> data : dataList) {
					String roleTypeId = (String) data.get("roleTypeId");
					groupId = (String) data.get("groupId");
					Map < String, Object > context = new HashMap < String, Object > ();
					context.put("groupId", groupId);
					context.put("roleTypeId", roleTypeId);
					GenericValue association = EntityQuery.use(delegator).from("SecurityGroupRoleTypeAssoc").where(context).queryFirst();
					if(UtilValidate.isNotEmpty(association)) {
						association.remove();
					}
				}
				TransactionUtil.commit();
				if(!ServiceUtil.isSuccess(result)) {
					responseMessage = UtilProperties.getMessage(RESOURCE,ServiceUtil.getErrorMessage(result), locale);
					result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
					result.put(ModelService.ERROR_MESSAGE, responseMessage);
					return CommonEvents.doJSONResponse(response, result);
				}
			} else {
				responseMessage = UtilProperties.getMessage(RESOURCE,"RequiredParameterMissing", locale);
				result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
				result.put(ModelService.ERROR_MESSAGE, responseMessage);
				return CommonEvents.doJSONResponse(response, result);
			}
		} catch (Exception e) {
			e.printStackTrace();
			TransactionUtil.rollback();
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
			return CommonEvents.doJSONResponse(response, result);
		}
		request.setAttribute("groupId", groupId);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		result.put(ModelService.SUCCESS_MESSAGE, responseMessage);
		return CommonEvents.doJSONResponse(response, result);
	}


	public static String uploadPartyImage(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map<String, Object> result = new HashMap<String, Object>();
		Locale locale = UtilHttp.getLocale(request);
		Map < String, Object > requestParameters = UtilHttp.getCombinedMap(request);
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		String partyId = (String) requestParameters.get("partyId");
		String userLoginId = (String) requestParameters.get("userLoginId");


		String responseMessage = UtilProperties.getMessage(RESOURCE, "PartyImageSuccessfullyUploaded", locale);
		try {
			String _default_path = ComponentConfig.getRootLocation("admin-portal")+"/webapp/admin-portal-resource/images/profile-image/";
			String _party_image_loc = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "PARTY_IMAGE_LOC", _default_path);

			if (UtilValidate.isNotEmpty(_party_image_loc)) {
				File dir = new File(_party_image_loc);
				if ( dir.isDirectory() && !dir.exists()) {
					dir.mkdirs();
				}
			}

			List<FileItem> multiparts = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);

			for (FileItem item : multiparts) {
				if (item.isFormField()) {
					String fName = item.getFieldName();
					String fValue = item.getString();
					if (fName.equals("partyId")) {
						partyId = fValue;
					} else if (fName.equals("userLoginId")) {
						userLoginId = fValue;
					} 

				}
			}
			String dataResourceId = delegator.getNextSeqId("DataResource");

			File store = new File(_party_image_loc);

			DiskFileItemFactory fileItemFactory = new DiskFileItemFactory();
			fileItemFactory.setRepository(store);
			String name = "";
			String fileExtension = "";
			for (FileItem item : multiparts) {
				if (!item.isFormField()) {
					name = new File(item.getName()).getName();
					fileExtension = org.fio.admin.portal.util.DataUtil.getFileExtension(name);
					if(!(UtilMisc.toList("png","jpg","jpeg").contains(fileExtension))) {
						request.setAttribute("userLoginId", userLoginId);
						request.setAttribute("partyId", partyId);
						return returnError(request, "Wrong file extension!");
					}
					item.write(new File(_party_image_loc + File.separator + partyId + "." + fileExtension));
				}
			}
			String mimeTypeId = new MimetypesFileTypeMap().getContentType(name);

			GenericValue dataResource = delegator.makeValue("DataResource");

			dataResource.set("dataResourceId", dataResourceId);
			dataResource.set("dataResourceName", name);
			dataResource.set("dataResourceTypeId", "LOCAL_FILE");
			dataResource.set("statusId", "CTNT_PUBLISHED");
			dataResource.set("mimeTypeId", mimeTypeId);
			// dataResource.set("objectInfo", filePath+"/"+name + "_"
			// +partyId);
			dataResource.set("objectInfo", _party_image_loc + File.separator + partyId + "." + fileExtension);
			dataResource.create();
			GenericValue content = delegator.makeValue("Content");
			String contentId = delegator.getNextSeqId("Content");
			// added prefix for attachment Id
			contentId = "PI-" + contentId;
			// ended
			content.set("contentId", contentId);
			content.set("dataResourceId", dataResourceId);
			content.set("contentName", name);
			content.set("contentTypeId", "PROFILE_IMG");
			content.set("createdDate", UtilDateTime.nowTimestamp());
			content.set("mimeTypeId", mimeTypeId);
			content.set("createdByUserLogin", userLogin.getString("userLoginId"));
			content.create();

			GenericValue partyContentGv = EntityQuery.use(delegator).from("PartyContent").where("partyId", partyId, "partyContentTypeId", "USER_PROFILE_IMAGE").filterByDate().queryFirst();
			if(UtilValidate.isNotEmpty(partyContentGv)) {
				partyContentGv.set("thruDate", UtilDateTime.nowTimestamp());
				partyContentGv.store();
			}
			if(UtilValidate.isNotEmpty(partyId)){
				GenericValue partyContent = delegator.makeValue("PartyContent");
				partyContent.set("contentId", contentId);
				partyContent.set("partyId", partyId);
				partyContent.set("partyContentTypeId", "USER_PROFILE_IMAGE");
				partyContent.set("fromDate", UtilDateTime.nowTimestamp());
				partyContent.create();
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("userLoginId", userLoginId);
			request.setAttribute("partyId", partyId);
			return returnError(request, e.getMessage());
		}
		request.setAttribute("userLoginId", userLoginId);
		request.setAttribute("partyId", partyId);
		return returnSuccess(request, "Image successfully uploaded");
	}

	public static String createO365Config(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map<String, Object> result = new HashMap<String, Object>();
		Locale locale = UtilHttp.getLocale(request);
		Map < String, Object > requestParameters = UtilHttp.getCombinedMap(request);
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");

		//String requestData = DataUtil.getJsonStrBody(request);
		String responseMessage = UtilProperties.getMessage(RESOURCE, "O365 account has been successfully configured", locale);
		try {
			String _secret_key_ms_graph = EntityUtilProperties.getPropertyValue("msgraph", "ms.graph.key", delegator);
			if(UtilValidate.isNotEmpty(_secret_key_ms_graph)) {
				EntityCrypto entityCrypto = new EntityCrypto(delegator,null); 

				String channelId = (String) requestParameters.get("channelId");
				String clientId = (String) requestParameters.get("clientId");
				if(UtilValidate.isNotEmpty(clientId))
					requestParameters.put("clientId", entityCrypto.encrypt(_secret_key_ms_graph, EncryptMethod.TRUE, clientId));
				String securityId = (String) requestParameters.get("securityId");
				if(UtilValidate.isNotEmpty(securityId))
					requestParameters.put("securityId", entityCrypto.encrypt(_secret_key_ms_graph, EncryptMethod.TRUE, securityId));
				String tenantId = (String) requestParameters.get("tenantId");
				if(UtilValidate.isNotEmpty(tenantId))
					requestParameters.put("tenantId", entityCrypto.encrypt(_secret_key_ms_graph, EncryptMethod.TRUE, tenantId));
				//MS_GRAPH
				GenericValue msGraph = EntityQuery.use(delegator).from("MsGraph").where("channelId", channelId).queryFirst();
				if(UtilValidate.isNotEmpty(msGraph)) {
					msGraph.setNonPKFields(requestParameters);
					msGraph.store();
				} else {
					msGraph = delegator.makeValue("MsGraph");
					msGraph.setPKFields(requestParameters);
					msGraph.setNonPKFields(requestParameters);
					msGraph.create();
				}
			} else {
				return returnError(request, "Please configure ms graph key for encrypt/decrypt.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return returnError(request, e.getMessage());
		}
		return returnSuccess(request, responseMessage);
	}

	public static String createOrStoreNavTabs(HttpServletRequest request, HttpServletResponse response) {
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String componentId = request.getParameter("componentId");
		String tabConfigId = request.getParameter("tabConfigId");
		String tabId = request.getParameter("tabId");
		String sequenceNo = request.getParameter("sequenceNo");
		request.setAttribute("userLogin", userLogin);
		Map < String, Object > data = new HashMap<String, Object>();;
		Map < String, Object > requestParameters = UtilHttp.getCombinedMap(request);
		componentId = UtilValidate.isNotEmpty(componentId)?componentId.replace("-", "_").toUpperCase():"";
		requestParameters.put("componentId", componentId);

		try {
			if(UtilValidate.isNotEmpty(componentId) && UtilValidate.isNotEmpty(tabConfigId) && UtilValidate.isNotEmpty(tabId)) {
				GenericValue createNewNavTab = EntityQuery.use(delegator).from("NavTabsConfig").where("componentId", componentId,"tabConfigId",tabConfigId,"tabId",tabId).queryFirst();
				if(UtilValidate.isNotEmpty(sequenceNo)) {
					int sequenceNum = Integer.parseInt(sequenceNo);
					requestParameters.put("sequenceNo", sequenceNum);
				}
				if(UtilValidate.isNotEmpty(createNewNavTab)) {
					createNewNavTab.setNonPKFields(requestParameters);
					createNewNavTab.store();
				}else {
					createNewNavTab = delegator.makeValue("NavTabsConfig");
					createNewNavTab.setPKFields(requestParameters);
					createNewNavTab.setNonPKFields(requestParameters);
					createNewNavTab.create();
				}
			}
			data.put("success", "success");
		}catch (GenericEntityException e) {
			data.put("error", "error");
			return doJSONResponse(response,data);
		}
		return doJSONResponse(response,data);
	}

	public static String removeNavTabs(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String requestData = DataUtil.getJsonStrBody(request);
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		List<Map<String, Object>> dataList = DataUtil.convertToListMap(requestData);
		List<GenericValue> toBeRemoved = new LinkedList<GenericValue>();
		Map<String, Object> result = new HashMap<String, Object>();
		request.setAttribute("userLogin", userLogin);
		String responseMessage = "Navigation Tab removed successfully";
		try {
			for(Map<String, Object> dataMap : dataList) {
				String componentId = (String) dataMap.get("componentId");
				String tabConfigId = (String) dataMap.get("tabConfigId");
				String tabId = (String) dataMap.get("tabId");

				List < EntityCondition > conditions = new ArrayList<EntityCondition>();
				conditions.add(EntityCondition.makeCondition("componentId", EntityOperator.EQUALS, componentId));
				conditions.add(EntityCondition.makeCondition("tabConfigId", EntityOperator.EQUALS, tabConfigId));
				conditions.add(EntityCondition.makeCondition("tabId", EntityOperator.EQUALS, tabId));

				EntityCondition mainCondition = EntityCondition.makeCondition(conditions, EntityOperator.AND);
				GenericValue navTabToBeRemoved = EntityQuery.use(delegator).from("NavTabsConfig").where(mainCondition).queryFirst();
				if(UtilValidate.isNotEmpty(navTabToBeRemoved)) {
					toBeRemoved.add(navTabToBeRemoved);
				}
			}if(toBeRemoved.size() > 0){
				delegator.removeAll(toBeRemoved);
			}
		}catch (GenericEntityException e) {
			e.printStackTrace();
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
			return CommonEvents.doJSONResponse(response, result);
		}
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		result.put(ModelService.SUCCESS_MESSAGE, responseMessage);
		return CommonEvents.doJSONResponse(response, result);
	}

	public static String uploadLogoImage(HttpServletRequest request, HttpServletResponse response)
			throws IOException, FileUploadException {
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		DispatchContext dctx = dispatcher.getDispatchContext();
		Delegator delegator = dctx.getDelegator();
		Locale locale = UtilHttp.getLocale(request);
		String imageType = (String) context.get("imageType");
		String imageFilenameFormat = EntityUtilProperties.getPropertyValue("catalog", "image.filename.format",
				delegator);
		String ofbizHome = System.getProperty("ofbiz.home");
		String ImagePath = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "CLIENT_" + imageType);
		String imageServerPath = ofbizHome + ImagePath;
		FlexibleStringExpander filenameExpander = FlexibleStringExpander.getInstance(imageFilenameFormat);
		if (imageType != null) {
			String fileLocation = filenameExpander.expandString(UtilMisc.toMap("type", imageType, "id", imageType));
			String filePathPrefix = "";
			String filenameToUse = fileLocation;
			if (fileLocation.lastIndexOf("/") != -1) {
				filePathPrefix = fileLocation.substring(0, fileLocation.lastIndexOf("/") + 1);
				filenameToUse = fileLocation.substring(fileLocation.lastIndexOf("/") + 1);
			}
			String defaultFileName = filenameToUse + "_temp";
			HttpRequestFileUpload uploadObject = new HttpRequestFileUpload();
			uploadObject.setOverrideFilename(defaultFileName);
			uploadObject.setSavePath(imageServerPath + "/");
			uploadObject.doUpload(request);
			if (UtilValidate.isEmpty(uploadObject.getFilename())) {
				request.setAttribute("_ERROR_MESSAGE_", "Please choose a image");
				return "error";
			}
			String clientFileName = uploadObject.getFilename();
			if (clientFileName != null && clientFileName.length() > 0) {
				if (clientFileName.lastIndexOf(".") > 0 && clientFileName.lastIndexOf(".") < clientFileName.length()) {
					filenameToUse += clientFileName.substring(clientFileName.lastIndexOf("."));
				}
				try {
					File file = new File(imageServerPath + "/", defaultFileName);
					File file1 = new File(imageServerPath + "/", filenameToUse);
					try {
						File targetDir = new File(imageServerPath + "/");
						if (!filenameToUse.startsWith("" + ".")) {
							File[] files = targetDir.listFiles();
							for (File file2 : files) {
								if (file2.isFile()
										&& file2.getName()
										.contains(filenameToUse.substring(0, filenameToUse.indexOf(".") + 1))
										&& !imageType.equals("original")) {
									file2.delete();
								} else if (file2.isFile() && imageType.equals("original")
										&& !file2.getName().equals(defaultFileName)) {
									file2.delete();
								}
							}
						} else {
							File[] files = targetDir.listFiles();
							for (File file3 : files) {
								if (file3.isFile() && !file3.getName().equals(defaultFileName)
										&& file3.getName().startsWith("" + "."))
									file3.delete();
							}
						}
					} catch (Exception e) {
						Debug.logError("error deleting existing file (not neccessarily a problem)", MODULE);
					}
					file.renameTo(file1);
				} catch (Exception e) {
					Debug.logError(e.getMessage(), MODULE);
				}
			}
		}
		request.setAttribute("_EVENT_MESSAGE_", "Image uploaded Successfully");
		return "success";
	}

	public static String getCoordinatorBackupList(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		Locale locale = UtilHttp.getLocale(request);
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String type = (String) context.get("type");
		String roleTypeId = (String) context.get("roleTypeId");
		roleTypeId = UtilValidate.isNotEmpty(roleTypeId) ? roleTypeId : "CUST_SERVICE_REP";
		String selectedPartyId = (String) context.get("selectedPartyId");
		try {
			String _where_condition_ = "";
			String _sql_query_ = "SELECT PTY.PARTY_ID, CONCAT(PER.FIRST_NAME, IFNULL(CONCAT(' ',PER.LAST_NAME),'')) AS 'NAME' FROM party PTY"
					+ " INNER JOIN person PER ON PTY.party_id=PER.party_id "
					+ " INNER JOIN party_role PR ON PTY.party_id=PR.party_id"
					+ " INNER JOIN user_login ul ON PTY.party_id=ul.party_id";

			_where_condition_ = _where_condition_ + (UtilValidate.isNotEmpty(_where_condition_) ? " AND " : "") + " (UL.ENABLED='Y' OR UL.ENABLED IS NULL)";
			_where_condition_ = _where_condition_ + (UtilValidate.isNotEmpty(_where_condition_) ? " AND " : "") + " (PTY.status_id='PARTY_ENABLED' OR PTY.status_id IS NULL)";

			if (UtilValidate.isNotEmpty(roleTypeId)) {
				_where_condition_ = _where_condition_ + (UtilValidate.isNotEmpty(_where_condition_) ? " AND " : "") + " PR.ROLE_TYPE_ID='"+roleTypeId+"'";
			}
			_where_condition_ = _where_condition_ + (UtilValidate.isNotEmpty(_where_condition_) ? " AND " : "") + " NOT EXISTS (SELECT 1 FROM party_attribute pa1 WHERE pa1.party_id=PTY.party_id AND pa1.attr_name = 'BACKUP_COORDINATOR')";
			_where_condition_ = _where_condition_ + (UtilValidate.isNotEmpty(_where_condition_) ? " AND " : "") + " NOT EXISTS (SELECT 1 FROM party_attribute pa1 WHERE pa1.attr_value=PTY.party_id AND pa1.attr_name = 'BACKUP_COORDINATOR') ";

			if("COORDINATOR".equals(type)) {
			}
			if("BACKUP_COORDINATOR".equals(type)) {
				if(UtilValidate.isNotEmpty(selectedPartyId))
					_where_condition_ = _where_condition_ + (UtilValidate.isNotEmpty(_where_condition_) ? " AND " : "") + " PTY.PARTY_ID <> '"+selectedPartyId+"'";

			}

			SQLProcessor sqlProcessor = new SQLProcessor(delegator, delegator.getGroupHelperInfo("org.ofbiz"));

			ResultSet rs = null;
			String _final_sql_script = _sql_query_+ (UtilValidate.isNotEmpty(_where_condition_) ? " WHERE "+_where_condition_ : "" );


			Debug.log("_final_sql_script ---->"+_final_sql_script, MODULE);
			rs = sqlProcessor.executeQuery(_final_sql_script);
			if (rs != null) {
				ResultSetMetaData rsMetaData = rs.getMetaData();
				List<String> columnList = new ArrayList<String>();
				int count = rsMetaData.getColumnCount();
				for(int i = 1; i<=count; i++) {
					columnList.add(rsMetaData.getColumnName(i));
				}

				while (rs.next()) {
					Map<String, Object> data = new HashMap<String, Object>();
					for(String columName : columnList) {
						String fieldName = ModelUtil.dbNameToVarName(columName);
						String fieldValue = rs.getString(columName);
						data.put(fieldName, fieldValue);
					}
					results.add(data);
				}
			}
			if(!rs.isClosed())
				rs.close();

			if(sqlProcessor != null)
				sqlProcessor.close();

			//Debug.log("Results : " + results, MODULE);
		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);
	}

	public static String createBackupConfiguration(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		Map<String, Object> result = new HashMap<String, Object>();
		Locale locale = UtilHttp.getLocale(request);
		Map < String, Object > requestParameters = UtilHttp.getCombinedMap(request);
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		String coordinator = (String) requestParameters.get("coordinator");
		String backupCoordinator = (String) requestParameters.get("backupCoordinator");
		String responseMessage = UtilProperties.getMessage(RESOURCE, "BackConfigurationAssocSuccessfully", locale);
		try {
			if(UtilValidate.isNotEmpty(coordinator) && UtilValidate.isNotEmpty(backupCoordinator)) {
				TransactionUtil.begin(400000);
				EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("partyId", EntityOperator.IN, UtilMisc.toList(coordinator, backupCoordinator)),
						EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, "BACKUP_COORDINATOR")
						);
				GenericValue checkExists = EntityQuery.use(delegator).from("PartyAttribute").where(condition).queryFirst();
				if(UtilValidate.isEmpty(checkExists)) {
					checkExists = delegator.makeValue("PartyAttribute");
					checkExists.set("partyId", coordinator);
					checkExists.set("attrName", "BACKUP_COORDINATOR");
					checkExists.set("attrValue", backupCoordinator);
					checkExists.create();
				}
				TransactionUtil.commit();
			}
		} catch (Exception e) {
			TransactionUtil.rollback();
			e.printStackTrace();
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
			return CommonEvents.doJSONResponse(response, result);
		}
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		result.put(ModelService.SUCCESS_MESSAGE, responseMessage);
		return CommonEvents.doJSONResponse(response, result);
	}
	public static String removeBackupConfiguration(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		Locale locale = UtilHttp.getLocale(request);
		Map < String, Object > requestParameters = UtilHttp.getCombinedMap(request);
		String requestData = DataUtil.getJsonStrBody(request);
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		String responseMessage = UtilProperties.getMessage(RESOURCE, "BackConfigurationRemovedSuccessfully", locale);
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			List<Map<String, Object>> dataList = DataUtil.convertToListMap(requestData);
			List<GenericValue> toBeRemove = new LinkedList<GenericValue>();
			for(Map<String, Object> data : dataList) {
				String coordinator = (String) data.get("partyId");
				String backupCoordinator = (String) data.get("attrValue");

				GenericValue backupConfigList = EntityQuery.use(delegator).from("PartyAttribute").where("partyId",coordinator,"attrName", "BACKUP_COORDINATOR","attrValue",backupCoordinator).queryFirst();

				if(UtilValidate.isNotEmpty(backupConfigList)) {
					toBeRemove.add(backupConfigList);
				}
			}
			if(toBeRemove.size() > 0){
				delegator.removeAll(toBeRemove);

				result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
				result.put(ModelService.SUCCESS_MESSAGE, responseMessage);
				return CommonEvents.doJSONResponse(response, result);
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
			return CommonEvents.doJSONResponse(response, result);
		}
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		result.put(ModelService.SUCCESS_MESSAGE, responseMessage);
		return CommonEvents.doJSONResponse(response, result);
	}
	
	public static String storeHelpContent(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		Map<String, Object> result = new HashMap<String, Object>();
		Locale locale = UtilHttp.getLocale(request);
		Map < String, Object > requestParameters = UtilHttp.getCombinedMap(request);
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		String parameterId = (String) requestParameters.get("parameterId");
		String value = UtilValidate.isNotEmpty(requestParameters.get("value")) ? (String) requestParameters.get("value") : "";
		String sectionId = (String) requestParameters.get("sectionId");
		String responseMessage = UtilProperties.getMessage(RESOURCE, "HelpContentCreatedSuccessfully", locale);
		try {
			if(UtilValidate.isNotEmpty(parameterId)) {
				TransactionUtil.begin(400000);
				GenericValue helpContentGv = EntityQuery.use(delegator).from("PretailLoyaltyGlobalParameters").where("parameterId", parameterId).queryFirst();
				if(UtilValidate.isEmpty(helpContentGv)) {
					helpContentGv = delegator.makeValue("PretailLoyaltyGlobalParameters");
					helpContentGv.set("parameterId", parameterId);
					helpContentGv.set("value", value);
					helpContentGv.set("storeId", sectionId);
					helpContentGv.create();
				} else {
					helpContentGv.set("value", value);
					helpContentGv.set("storeId", sectionId);
					helpContentGv.store();
				}
				TransactionUtil.commit();
			}
		} catch (Exception e) {
			TransactionUtil.rollback();
			e.printStackTrace();
			return returnError(request, e.getMessage());
		}
		return returnSuccess(request, responseMessage);
	}
	
	public static String importGlobalParameterSeeds(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		String componentNamesInput[] = request.getParameterValues("globalParamSection");
		String isAll = request.getParameter("isAll");
		try {
			if(UtilValidate.isNotEmpty(componentNamesInput) || (UtilValidate.isNotEmpty(isAll) && "Y".equals(isAll))) {
				List<EntityCondition> conditions = new LinkedList<>();
				if(UtilValidate.isNotEmpty(componentNamesInput)) {
					List<String> sections = Arrays.asList(componentNamesInput);
					if(UtilValidate.isNotEmpty(sections)) {
						conditions.add(EntityCondition.makeCondition("groupId",EntityOperator.IN,sections));
					}
				}
				List<GenericValue> components = EntityQuery.use(delegator).from("Component").where(EntityCondition.makeCondition(conditions,EntityOperator.AND)).select(UtilMisc.toSet("componentName","rootLocation")).queryList();
				for(GenericValue component : components) {
					String componentLocation = "";
					try {
						componentLocation = ComponentConfig.getRootLocation(component.getString("componentName"));
					}catch(Exception e) {
						e.printStackTrace();
						componentLocation = component.getString("rootLocation");
					}
					componentLocation = componentLocation + "data/PretailLoyaltyDefaultSeeds.xml";
					Map<String, Object> entityImportContext = new HashMap<String, Object>();
					entityImportContext.put("filename", componentLocation);
					entityImportContext.put("userLogin", userLogin);
					dispatcher.runSync("entityImport", entityImportContext);
				}
			}else {
				return returnError(request, "No component selected to import seed");
			}
		}catch (Exception e) {
			e.printStackTrace();
			return returnError(request, "Error occurred "+e.getMessage());
		}
		return returnSuccess(request, "Selected component seeds imported successfully");
	}
}