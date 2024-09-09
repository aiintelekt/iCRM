/*
 * Copyright (c) Open Source Strategies, Inc.
 *
 * Opentaps is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Opentaps is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Opentaps.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.groupfio.custom.field.event;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.fio.homeapps.constants.GlobalConstants;
import org.fio.homeapps.util.ParamUtil;
import org.fio.homeapps.util.UtilDateTime;
import org.groupfio.common.portal.CommonPortalConstants;
import org.groupfio.common.portal.util.UtilAttribute;
import org.groupfio.custom.field.ResponseCodes;
import org.groupfio.custom.field.constants.CustomFieldConstants;
import org.groupfio.custom.field.constants.CustomFieldConstants.GroupType;
import org.groupfio.custom.field.util.DataUtil;
import org.ofbiz.base.component.ComponentConfig;
import org.ofbiz.base.component.ComponentException;
import org.ofbiz.base.util.Base64;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.collections.ResourceBundleMapWrapper;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityFieldValue;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Utility class for making Ajax JSON responses.
 * 
 * @author Sharif Ul Islam
 */
public final class AjaxEvents {

	private AjaxEvents() {
	}

	private static final String MODULE = AjaxEvents.class.getName();

	public static String doJSONResponse(HttpServletResponse response, JSONObject jsonObject) {
		return doJSONResponse(response, jsonObject.toString());
	}

	public static String doJSONResponse(HttpServletResponse response, Collection<?> collection) {
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
			Debug.logWarning(
					"Could not get the UTF-8 json string due to UnsupportedEncodingException: " + e.getMessage(),
					MODULE);
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

	public static GenericValue getUserLogin(HttpServletRequest request) {
		HttpSession session = request.getSession();
		return (GenericValue) session.getAttribute("userLogin");
	}

	/*************************************************************************/
	/**                                                                     **/
	/** Common JSON Requests **/
	/**                                                                     **/
	/*************************************************************************/

	@SuppressWarnings("unchecked")
	public static String getRoleConfigs(HttpServletRequest request, HttpServletResponse response)
			throws GenericEntityException {

		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");

		Locale locale = UtilHttp.getLocale(request);
		HttpSession session = request.getSession(true);

		String groupId = request.getParameter("groupId");
		String customFieldId = request.getParameter("customFieldId");

		Map<String, Object> operatorList = new HashMap<String, Object>();
		Map<String, Object> resp = new HashMap<String, Object>();

		JSONArray datas = new JSONArray();

		try {

			if (UtilValidate.isNotEmpty(groupId) || UtilValidate.isNotEmpty(customFieldId)) {

				Map<String, Object> params = new HashMap<String, Object>();
				if (UtilValidate.isNotEmpty(groupId)) {
					params.put("groupId", groupId);
				}
				if (UtilValidate.isNotEmpty(customFieldId)) {
					params.put("customFieldId", customFieldId);
				}

				List<GenericValue> roleConfigs = delegator.findByAnd("CustomFieldRoleConfig", params,
						UtilMisc.toList("sequenceNumber"), false);

				if (UtilValidate.isNotEmpty(roleConfigs)) {
					for (GenericValue roleConfig : roleConfigs) {

						JSONObject data = new JSONObject();

						data.put("roleConfigId", roleConfig.getString("customFieldRoleConfigId"));
						data.put("roleType", roleConfig.getRelatedOne("RoleType").get("description", locale));
						data.put("groupId", groupId);
						data.put("roleTypeId", roleConfig.getString("roleTypeId"));
						data.put("sequenceNumber", roleConfig.get("sequenceNumber"));

						datas.add(data);

					}
				}

			}

		} catch (Exception e) {
			Debug.logError(e.getMessage(), MODULE);
		}

		resp.put("data", datas);

		return doJSONResponse(response, resp);
	}

	public static String getCustomFieldsGroup(HttpServletRequest request, HttpServletResponse response)
			throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String roleTypeId = (String) context.get("roleTypeId");
		String groupId = (String) context.get("groupId");
		String customFieldName = (String) context.get("customFieldName");
		String customFieldType = (String) context.get("customFieldType");
		String customFieldFormat = (String) context.get("customFieldFormat");
		String customFieldLength = (String) context.get("customFieldLength");
		String hide = (String) context.get("hide");

		try {
			ResourceBundleMapWrapper uiLabelMap = UtilProperties.getResourceBundleMap("CustomFieldUiLabels", Locale.getDefault());
			
			List<EntityCondition> conditions = FastList.newInstance();
			EntityCondition condition = null;

			conditions.add(EntityCondition.makeCondition("groupType", EntityOperator.EQUALS, GroupType.CUSTOM_FIELD));

			if (UtilValidate.isNotEmpty(roleTypeId)) {
				conditions.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, roleTypeId));
			}
			if (UtilValidate.isNotEmpty(groupId)) {
				conditions.add(EntityCondition.makeCondition("groupId", EntityOperator.EQUALS, groupId));
			}
			if (UtilValidate.isNotEmpty(customFieldType)) {
				conditions
						.add(EntityCondition.makeCondition("customFieldType", EntityOperator.EQUALS, customFieldType));
			}
			if (UtilValidate.isNotEmpty(customFieldFormat)) {
				conditions.add(
						EntityCondition.makeCondition("customFieldFormat", EntityOperator.EQUALS, customFieldFormat));
			}
			if (UtilValidate.isNotEmpty(customFieldLength)) {
				conditions.add(EntityCondition.makeCondition("customFieldLength", EntityOperator.EQUALS,
						Long.parseLong(customFieldLength)));

			}
			if (UtilValidate.isNotEmpty(hide)) {
				conditions.add(EntityCondition.makeCondition("hide", EntityOperator.EQUALS, hide));
			}
			if (UtilValidate.isNotEmpty(customFieldName)) {
				conditions.add(
						EntityCondition.makeCondition("customFieldName", EntityOperator.LIKE, "%" + customFieldName + "%"));
			}

			condition = EntityCondition.makeCondition(conditions, EntityOperator.AND);
			List<GenericValue> customFieldSummaryList = EntityQuery.use(delegator).from("CustomFieldSummary")
					.where(conditions).orderBy("sequenceNumber").queryList();
			if (UtilValidate.isNotEmpty(customFieldSummaryList)) {
				for (GenericValue entry : customFieldSummaryList) {
					Map<String, Object> data = new HashMap<String, Object>();
					data.put("customFieldName", entry.getString("customFieldName"));
					data.put("groupId", entry.getString("groupId"));
					data.put("groupName", entry.getString("groupName"));
					
					customFieldType = entry.getString("customFieldType");
					if (UtilValidate.isNotEmpty(customFieldType) && customFieldType.equals("SINGLE")) {
						customFieldType = (String) uiLabelMap.get("single");
					} else if (UtilValidate.isNotEmpty(customFieldType) && customFieldType.equals("MULTIPLE")) {
						customFieldType = (String) uiLabelMap.get("multiple");
					}
					
					customFieldFormat = entry.getString("customFieldFormat");
					if (UtilValidate.isNotEmpty(customFieldFormat) && customFieldFormat.equals("TEXT")) {
						customFieldFormat = (String) uiLabelMap.get("text");
					} else if (UtilValidate.isNotEmpty(customFieldFormat) && customFieldFormat.equals("DATE")) {
						customFieldFormat = (String) uiLabelMap.get("date");
					} else if (UtilValidate.isNotEmpty(customFieldFormat) && customFieldFormat.equals("NUMERIC")) {
						customFieldFormat = (String) uiLabelMap.get("numeric");
					} else if (UtilValidate.isNotEmpty(customFieldFormat) && customFieldFormat.equals("BOOLEAN")) {
						customFieldFormat = (String) uiLabelMap.get("boolean");
					} else if (UtilValidate.isNotEmpty(customFieldFormat) && customFieldFormat.equals("TEXT_AREA")) {
						customFieldFormat = (String) uiLabelMap.get("textArea");
					} else if (UtilValidate.isNotEmpty(customFieldFormat) && customFieldFormat.equals("CHECK_BOX")) {
						customFieldFormat = (String) uiLabelMap.get("checkBox");
					} else if (UtilValidate.isNotEmpty(customFieldFormat) && customFieldFormat.equals("DROP_DOWN")) {
						customFieldFormat = (String) uiLabelMap.get("dropDown");
					} else if (UtilValidate.isNotEmpty(customFieldFormat) && customFieldFormat.equals("RADIO")) {
						customFieldFormat = (String) uiLabelMap.get("radio");
					} else if (UtilValidate.isNotEmpty(customFieldFormat) && customFieldFormat.equals("LABEL_TEXT")) {
						customFieldFormat = (String) uiLabelMap.get("labelText");
					}
					
					data.put("customFieldType", customFieldType);
					data.put("customFieldFormat", customFieldFormat);
					
					data.put("sequenceNumber", entry.getString("sequenceNumber"));
					data.put("customFieldLength", entry.getString("customFieldLength"));
					data.put("hide", entry.getString("hide"));
					data.put("customFieldId", entry.getString("customFieldId"));
					data.put("productPromoCodeGroupId", UtilValidate.isNotEmpty(entry.getString("productPromoCodeGroupId")) ? entry.getString("productPromoCodeGroupId") : "");
					
					results.add(data);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);
	}

	public static String getSegmentCodeGroup(HttpServletRequest request, HttpServletResponse response)
			throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		String groupingCode = (String) context.get("groupingCode");
		String groupId = (String) context.get("groupId");
		String groupName = (String) context.get("groupName");
		String valueCapture = (String) context.get("valueCapture");
		String isCampaignUse = (String) context.get("isCampaignUse");
		String type = (String) context.get("type");

		try {
			List<EntityCondition> conditions = FastList.newInstance();
			EntityCondition condition = null;
			conditions.add(EntityCondition.makeCondition("groupType", EntityOperator.EQUALS, "SEGMENTATION"));
			if (UtilValidate.isNotEmpty(groupingCode)) {
				conditions.add(EntityCondition.makeCondition("groupingCode", EntityOperator.EQUALS, groupingCode));
			}
			if (UtilValidate.isNotEmpty(groupId)) {
				conditions.add(EntityCondition.makeCondition("groupId", EntityOperator.EQUALS, groupId));
			}
			if (UtilValidate.isNotEmpty(groupName)) {
				conditions.add(EntityCondition.makeCondition("groupName", EntityOperator.LIKE, "%" + groupName + "%"));
			}
			if (UtilValidate.isNotEmpty(valueCapture)) {
				conditions.add(EntityCondition.makeCondition("valueCapture", EntityOperator.EQUALS, valueCapture));
			}
			if (UtilValidate.isNotEmpty(isCampaignUse)) {
				conditions.add(EntityCondition.makeCondition("isCampaignUse", EntityOperator.EQUALS, isCampaignUse));
			}
			if (UtilValidate.isNotEmpty(type)) {
				conditions.add(EntityCondition.makeCondition("type", EntityOperator.EQUALS, type));
			}
			condition = EntityCondition.makeCondition(conditions, EntityOperator.AND);
			List<GenericValue> CustomFieldGroupList = delegator.findList("CustomFieldGroup", condition, null, null,	null, false);
			if (UtilValidate.isNotEmpty(CustomFieldGroupList)) {
				for (GenericValue Entry : CustomFieldGroupList) {
					Map<String, Object> data = new HashMap<String, Object>();
					GenericValue segGroupingName=delegator.findOne("CustomFieldGroupingCode", true, UtilMisc.toMap("customFieldGroupingCodeId", Entry.getString("groupingCode")));
					if(UtilValidate.isNotEmpty(segGroupingName)) {
						data.put("groupingCode", segGroupingName.getString("groupingCode"));
					}				
					data.put("segmentCodeId", Entry.getString("groupId"));
					data.put("segmentCodeName", Entry.getString("groupName"));
					data.put("active", Entry.getString("isActive"));
					data.put("sequenceNo", Entry.getString("sequence"));						
					results.add(data);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);
	}

	@SuppressWarnings("unchecked")
	public static String createRoleConfig(HttpServletRequest request, HttpServletResponse response)
			throws GenericEntityException {

		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");

		Locale locale = UtilHttp.getLocale(request);
		HttpSession session = request.getSession(true);

		String isCompleteReset = request.getParameter("isCompleteReset");

		String groupId = request.getParameter("groupId");
		String customFieldId = request.getParameter("customFieldId");
		String roleTypeId = request.getParameter("roleTypeId");
		String sequenceNumber = request.getParameter("sequenceNumber");

		Map<String, Object> resp = new HashMap<String, Object>();

		try {

			if (UtilValidate.isNotEmpty(groupId) || UtilValidate.isNotEmpty(customFieldId)) {

				Map<String, Object> params = new HashMap<String, Object>();
				if (UtilValidate.isNotEmpty(groupId)) {
					params.put("groupId", groupId);
				}
				if (UtilValidate.isNotEmpty(customFieldId)) {
					params.put("customFieldId", customFieldId);
				}

				if (UtilValidate.isNotEmpty(isCompleteReset) && isCompleteReset.equals("Y")) {
					delegator.removeByAnd("CustomFieldRoleConfig", params);
				}

				params.put("roleTypeId", roleTypeId);

				List<GenericValue> roleConfigs = delegator.findByAnd("CustomFieldRoleConfig", params, null, false);

				if (UtilValidate.isNotEmpty(roleConfigs)) {
					resp.put(CustomFieldConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
					resp.put(CustomFieldConstants.RESPONSE_MESSAGE, "Role assigned already");
				} else {
					GenericValue roleConfig = delegator.makeValue("CustomFieldRoleConfig");

					String configId = delegator.getNextSeqId("CustomFieldRoleConfig");

					roleConfig.put("customFieldRoleConfigId", configId);

					roleConfig.put("groupId", groupId);
					roleConfig.put("customFieldId", customFieldId);

					roleConfig.put("roleTypeId", roleTypeId);

					roleConfig.put("sequenceNumber",
							UtilValidate.isNotEmpty(sequenceNumber) ? Long.parseLong(sequenceNumber) : new Long(1));

					roleConfig.create();

					resp.put(CustomFieldConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
					resp.put(CustomFieldConstants.RESPONSE_MESSAGE, "Successfully assigned role..");
				}

			}

		} catch (Exception e) {
			Debug.logError(e.getMessage(), MODULE);
		}

		return doJSONResponse(response, resp);
	}

	@SuppressWarnings("unchecked")
	public static String removeRoleConfig(HttpServletRequest request, HttpServletResponse response)
			throws GenericEntityException {

		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");

		Locale locale = UtilHttp.getLocale(request);
		HttpSession session = request.getSession(true);

		String roleConfigId = request.getParameter("roleConfigId");

		Map<String, Object> resp = new HashMap<String, Object>();

		try {

			if (UtilValidate.isNotEmpty(roleConfigId)) {

				GenericValue roleConfig = EntityUtil.getFirst(delegator.findByAnd("CustomFieldRoleConfig",
						UtilMisc.toMap("customFieldRoleConfigId", roleConfigId), null, false));

				if (UtilValidate.isNotEmpty(roleConfig)) {

					roleConfig.remove();

					resp.put(CustomFieldConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
					resp.put(CustomFieldConstants.RESPONSE_MESSAGE, "Successfully removed role config..");

				} else {
					resp.put(CustomFieldConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
					resp.put(CustomFieldConstants.RESPONSE_MESSAGE, "Not found assigned role..");
				}

			}

		} catch (Exception e) {

			resp.put(CustomFieldConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			resp.put(CustomFieldConstants.RESPONSE_MESSAGE, "Error: " + e.getMessage());

			Debug.logError(e.getMessage(), MODULE);
		}

		return doJSONResponse(response, resp);
	}
	
	public static String removeSegmentation(HttpServletRequest request, HttpServletResponse response) {
    	
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
		String groupIds = request.getParameter("groupIds");
		String customFieldIds = request.getParameter("customFieldIds");
		String partyIds = request.getParameter("partyIds");
		
		String domainEntityType = request.getParameter("domainEntityType");
		String domainEntityId = request.getParameter("domainEntityId");
		
		Map<String, Object> result = FastMap.newInstance();
		
		try {
			
			if (UtilValidate.isNotEmpty(groupIds)) {
				
				String[] groupIdList = groupIds.split(",");
				String[] customFieldIdList = customFieldIds.split(",");
				String[] partyIdList = partyIds.split(",");
				
				for (int i = 0; i < groupIdList.length; i++) {
					
					String groupId = groupIdList[i];
					String customFieldId = customFieldIdList[i];
					String partyId = partyIdList[i];
					
					if (UtilValidate.isNotEmpty(groupId) && UtilValidate.isNotEmpty(customFieldId)) {
						if (UtilValidate.isNotEmpty(domainEntityType) && !CommonPortalConstants.PARTY_DOMAIN_ENTITY_TYPE.containsKey(domainEntityType)) {
							delegator.removeByAnd("SegmentationValueAssoc", UtilMisc.toMap("groupId", groupId, "customFieldId", customFieldId, "domainEntityId", domainEntityId, "domainEntityType", domainEntityType));
						} else {
							delegator.removeByAnd("CustomFieldPartyClassification", UtilMisc.toMap("groupId", groupId, "customFieldId", customFieldId, "partyId", partyId));
						}
					}
				}
				
				result.put(CustomFieldConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, MODULE);
			
			result.put(CustomFieldConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			result.put(CustomFieldConstants.RESPONSE_MESSAGE, e.getMessage());
            
			return AjaxEvents.doJSONResponse(response, result);
		}
		
		return AjaxEvents.doJSONResponse(response, result);
		
    }
	
	public static String removeEconomicMetric(HttpServletRequest request, HttpServletResponse response) {
    	
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
		String groupIds = request.getParameter("groupIds");
		String customFieldIds = request.getParameter("customFieldIds");
		String partyIds = request.getParameter("partyIds");
		
		String domainEntityType = request.getParameter("domainEntityType");
		String domainEntityId = request.getParameter("domainEntityId");
		
		Map<String, Object> result = FastMap.newInstance();
		
		try {
			if (UtilValidate.isNotEmpty(groupIds)) {
				
				String[] groupIdList = groupIds.split(",");
				String[] customFieldIdList = customFieldIds.split(",");
				String[] partyIdList = partyIds.split(",");
				
				for (int i = 0; i < groupIdList.length; i++) {
					
					String groupId = groupIdList[i];
					String customFieldId = customFieldIdList[i];
					String partyId = partyIdList[i];
					
					if (UtilValidate.isNotEmpty(groupId) && UtilValidate.isNotEmpty(customFieldId)) {
						if (UtilValidate.isNotEmpty(domainEntityType) && !CommonPortalConstants.PARTY_DOMAIN_ENTITY_TYPE.containsKey(domainEntityType)) {
							delegator.removeByAnd("EconomicMetricValueAssoc", UtilMisc.toMap("groupId", groupId, "customFieldId", customFieldId, "domainEntityId", domainEntityId, "domainEntityType", domainEntityType));
						} else {
							delegator.removeByAnd("PartyMetricIndicator", UtilMisc.toMap("groupId", groupId, "customFieldId", customFieldId, "partyId", partyId));
						}
					}
				}
				result.put(CustomFieldConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, MODULE);
			
			result.put(CustomFieldConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			result.put(CustomFieldConstants.RESPONSE_MESSAGE, e.getMessage());
            
			return AjaxEvents.doJSONResponse(response, result);
		}
		return AjaxEvents.doJSONResponse(response, result);
    }
	
	public static String addSegmentation(HttpServletRequest request, HttpServletResponse response) {
    	
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
		String groupId = request.getParameter("groupId");
		String customFieldId = request.getParameter("customFieldId");
		String partyId = request.getParameter("partyId");
		String actualValue = request.getParameter("actualValue");
		
		String domainEntityType = request.getParameter("domainEntityType");
        String domainEntityId = request.getParameter("domainEntityId");
		
		Map<String, Object> result = FastMap.newInstance();
		
		try {
			
			if (UtilValidate.isNotEmpty(groupId) && UtilValidate.isNotEmpty(customFieldId)) {
				
				Map<String, Object> partyTypeParam = UtilMisc.toMap("groupId", groupId, "customFieldId", customFieldId, "partyId", partyId);
				Map<String, Object> nonPartyTypeParam = UtilMisc.toMap("groupId", groupId, "customFieldId", customFieldId, "domainEntityId", domainEntityId, "domainEntityType", domainEntityType);
				
				GenericValue fieldValue = null;
	        	if (UtilValidate.isNotEmpty(domainEntityType) && !CommonPortalConstants.PARTY_DOMAIN_ENTITY_TYPE.containsKey(domainEntityType)) {
	        		fieldValue = EntityUtil.getFirst(delegator.findByAnd("SegmentationValueAssoc",
	        				nonPartyTypeParam, null, false));
	        	} else {
	        		fieldValue = EntityUtil.getFirst(delegator.findByAnd("CustomFieldPartyClassification",
	        				partyTypeParam, null, false));
	        	}
	        	
				if (UtilValidate.isNotEmpty(fieldValue)) {
					result.put(CustomFieldConstants.RESPONSE_CODE, ResponseCodes.CONFLICT_CODE);
					result.put(CustomFieldConstants.RESPONSE_MESSAGE, "Already added!");
				} else {
					
					if (UtilValidate.isNotEmpty(domainEntityType) && !CommonPortalConstants.PARTY_DOMAIN_ENTITY_TYPE.containsKey(domainEntityType)) {
		        		fieldValue = delegator.makeValue("SegmentationValueAssoc",
		        				nonPartyTypeParam);
		        	} else {
		        		fieldValue = delegator.makeValue("CustomFieldPartyClassification",
		        				partyTypeParam);
		        	}
					
					fieldValue.put("groupActualValue", actualValue);
					fieldValue.put("inceptionDate", UtilDateTime.nowTimestamp());
					
					fieldValue.create();
					
					result.put(CustomFieldConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
				}
			} else {
				result.put(CustomFieldConstants.RESPONSE_CODE, ResponseCodes.BAD_REQUEST);
				result.put(CustomFieldConstants.RESPONSE_MESSAGE, "Required field missing!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, MODULE);
			
			result.put(CustomFieldConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			result.put(CustomFieldConstants.RESPONSE_MESSAGE, e.getMessage());
            
			return AjaxEvents.doJSONResponse(response, result);
		}
		return AjaxEvents.doJSONResponse(response, result);
    }
	
	public static String addEconomicMetric(HttpServletRequest request, HttpServletResponse response) {
    	
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
		String groupingCodeId = request.getParameter("groupingCode");
		String groupId = request.getParameter("groupId");
		String customFieldId = request.getParameter("customFieldId");
		String partyId = request.getParameter("partyId");
		
		String propertyName = request.getParameter("propertyName");
		String propertyValue = request.getParameter("propertyValue");
		String sequenceNumber = request.getParameter("sequenceNumber");
		
		String domainEntityType = request.getParameter("domainEntityType");
        String domainEntityId = request.getParameter("domainEntityId");
		
		Map<String, Object> result = FastMap.newInstance();
		
		try {
			
			if (UtilValidate.isNotEmpty(groupId) && UtilValidate.isNotEmpty(customFieldId)) {
				
				Map<String, Object> partyTypeParam = UtilMisc.toMap("groupId", groupId, "customFieldId", customFieldId, "partyId", partyId);
				Map<String, Object> nonPartyTypeParam = UtilMisc.toMap("groupId", groupId, "customFieldId", customFieldId, "domainEntityId", domainEntityId, "domainEntityType", domainEntityType);
				
				GenericValue fieldValue = null;
	        	if (UtilValidate.isNotEmpty(domainEntityType) && !CommonPortalConstants.PARTY_DOMAIN_ENTITY_TYPE.containsKey(domainEntityType)) {
	        		fieldValue = EntityUtil.getFirst(delegator.findByAnd("EconomicMetricValueAssoc",
	        				nonPartyTypeParam, null, false));
	        	} else {
	        		fieldValue = EntityUtil.getFirst(delegator.findByAnd("PartyMetricIndicator",
	        				partyTypeParam, null, false));
	        	}
				
				if (UtilValidate.isNotEmpty(fieldValue)) {
					result.put(CustomFieldConstants.RESPONSE_CODE, ResponseCodes.CONFLICT_CODE);
					result.put(CustomFieldConstants.RESPONSE_MESSAGE, "Already added!");
				} else {
					
					if (UtilValidate.isNotEmpty(domainEntityType) && !CommonPortalConstants.PARTY_DOMAIN_ENTITY_TYPE.containsKey(domainEntityType)) {
		        		fieldValue = delegator.makeValue("EconomicMetricValueAssoc",
		        				nonPartyTypeParam);
		        	} else {
		        		fieldValue = delegator.makeValue("PartyMetricIndicator",
		        				partyTypeParam);
		        	}
					
					GenericValue group = EntityUtil.getFirst(delegator.findByAnd("CustomFieldGroup",
							UtilMisc.toMap("groupId", groupId), null, false));
					
					if (UtilValidate.isNotEmpty(group.getString("groupingCode"))) {
						GenericValue groupingCode = EntityUtil.getFirst(delegator.findByAnd("CustomFieldGroupingCode",
								UtilMisc.toMap("customFieldGroupingCodeId", group.getString("groupingCode")), null, false));
						if (UtilValidate.isNotEmpty(groupingCode)) {
							fieldValue.put("groupingCode", groupingCode.getString("groupingCode"));
						}
					}
					
					fieldValue.put("propertyName", propertyName);
					fieldValue.put("propertyValue", propertyValue);
					fieldValue.put("sequenceNumber", UtilValidate.isNotEmpty(sequenceNumber) ? new Long(sequenceNumber) : null);
					
					fieldValue.create();
					
					result.put(CustomFieldConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
					
				}
				
			} else {
				result.put(CustomFieldConstants.RESPONSE_CODE, ResponseCodes.BAD_REQUEST);
				result.put(CustomFieldConstants.RESPONSE_MESSAGE, "Required field missing!");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, MODULE);
			
			result.put(CustomFieldConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			result.put(CustomFieldConstants.RESPONSE_MESSAGE, e.getMessage());
            
			return AjaxEvents.doJSONResponse(response, result);
		}
		
		return AjaxEvents.doJSONResponse(response, result);
		
    }

	//////////////////////////////////////////////////////////////////////////////////////////////

	@SuppressWarnings("unchecked")
	public static String getCustomFieldMultiValues(HttpServletRequest request, HttpServletResponse response)
			throws GenericEntityException {

		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");

		Locale locale = UtilHttp.getLocale(request);
		HttpSession session = request.getSession(true);

		String customFieldId = request.getParameter("customFieldId");

		Map<String, Object> resp = new HashMap<String, Object>();

		JSONArray datas = new JSONArray();

		try {

			if (UtilValidate.isNotEmpty(customFieldId)) {

				List<GenericValue> multiValues = delegator.findByAnd("CustomFieldMultiValue",
						UtilMisc.toMap("customFieldId", customFieldId), UtilMisc.toList("sequenceNumber"), false);

				if (UtilValidate.isNotEmpty(multiValues)) {
					for (GenericValue multiValue : multiValues) {

						JSONObject data = new JSONObject();

						data.put("customFieldId", customFieldId);
						data.put("multiValueId", multiValue.getString("multiValueId"));
						data.put("fieldValue", multiValue.getString("fieldValue"));
						data.put("description", multiValue.getString("description"));
						data.put("hide", multiValue.get("hide"));
						data.put("sequenceNumber", UtilValidate.isEmpty(multiValue.get("sequenceNumber")) ? 1
								: multiValue.get("sequenceNumber"));

						datas.add(data);

					}
				}

			}

		} catch (Exception e) {
			Debug.logError(e.getMessage(), MODULE);
		}

		resp.put("data", datas);

		return doJSONResponse(response, resp);
	}

	@SuppressWarnings("unchecked")
	public static String createCustomFieldMultiValue(HttpServletRequest request, HttpServletResponse response)
			throws GenericEntityException {

		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");

		Locale locale = UtilHttp.getLocale(request);
		HttpSession session = request.getSession(true);

		String customFieldId = request.getParameter("mvCustomFieldId");
		String fieldValue = request.getParameter("fieldValue");
		String description = request.getParameter("description");
		String hide = request.getParameter("hide");
		String sequenceNumber = request.getParameter("mvSequenceNumber");

		Map<String, Object> resp = new HashMap<String, Object>();

		try {

			if (UtilValidate.isNotEmpty(customFieldId)) {

				GenericValue multiValue = EntityUtil.getFirst(delegator.findByAnd("CustomFieldMultiValue",
						UtilMisc.toMap("customFieldId", customFieldId, "fieldValue", fieldValue), null, false));

				if (UtilValidate.isNotEmpty(multiValue)) {
					resp.put(CustomFieldConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
					resp.put(CustomFieldConstants.RESPONSE_MESSAGE, "Already created multi value..");
				} else {
					multiValue = delegator.makeValue("CustomFieldMultiValue");

					String multiValueId = delegator.getNextSeqId("CustomFieldMultiValue");

					multiValue.put("customFieldId", customFieldId);
					multiValue.put("multiValueId", multiValueId);

					multiValue.put("fieldValue", fieldValue);
					multiValue.put("description", description);
					multiValue.put("hide", hide);

					multiValue.put("sequenceNumber",
							UtilValidate.isNotEmpty(sequenceNumber) ? Long.parseLong(sequenceNumber) : new Long(1));

					multiValue.create();

					resp.put(CustomFieldConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
					resp.put(CustomFieldConstants.RESPONSE_MESSAGE, "Successfully created multi value..");
				}

			}

		} catch (Exception e) {
			Debug.logError(e.getMessage(), MODULE);
			resp.put(CustomFieldConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			resp.put(CustomFieldConstants.RESPONSE_MESSAGE, e.getMessage());
		}

		return doJSONResponse(response, resp);
	}

	@SuppressWarnings("unchecked")
	public static String removeCustomFieldMultiValue(HttpServletRequest request, HttpServletResponse response)
			throws GenericEntityException {

		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");

		Locale locale = UtilHttp.getLocale(request);
		HttpSession session = request.getSession(true);

		String customFieldId = request.getParameter("customFieldId");
		String multiValueId = request.getParameter("multiValueId");

		Map<String, Object> resp = new HashMap<String, Object>();

		try {

			if (UtilValidate.isNotEmpty(customFieldId) && UtilValidate.isNotEmpty(multiValueId)) {

				GenericValue multiValue = EntityUtil.getFirst(delegator.findByAnd("CustomFieldMultiValue",
						UtilMisc.toMap("customFieldId", customFieldId, "multiValueId", multiValueId), null, false));

				if (UtilValidate.isNotEmpty(multiValue)) {

					multiValue.remove();

					resp.put(CustomFieldConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
					resp.put(CustomFieldConstants.RESPONSE_MESSAGE, "Successfully removed multi value..");

				} else {
					resp.put(CustomFieldConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
					resp.put(CustomFieldConstants.RESPONSE_MESSAGE, "Not found role config..");
				}

			}

		} catch (Exception e) {

			resp.put(CustomFieldConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			resp.put(CustomFieldConstants.RESPONSE_MESSAGE, "Error: " + e.getMessage());

			Debug.logError(e.getMessage(), MODULE);
		}

		return doJSONResponse(response, resp);
	}

	@SuppressWarnings("unchecked")
	public static String removeSelectedMultiValues(HttpServletRequest request, HttpServletResponse response)
			throws GenericEntityException {

		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");

		Locale locale = UtilHttp.getLocale(request);
		HttpSession session = request.getSession(true);

		String customFieldId = request.getParameter("customFieldId");
		String rowsSelected[] = request.getParameterValues("rowsSelected[]");

		Map<String, Object> resp = new HashMap<String, Object>();

		int successCount = 0;

		try {

			if (UtilValidate.isNotEmpty(customFieldId)) {

				GenericValue customField = EntityUtil.getFirst(delegator.findByAnd("CustomField",
						UtilMisc.toMap("customFieldId", customFieldId), null, false));

				if (UtilValidate.isEmpty(customField)) {
					resp.put(CustomFieldConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
					resp.put(CustomFieldConstants.RESPONSE_MESSAGE, "Invalid custom field..");
				} else {

					if (UtilValidate.isNotEmpty(rowsSelected)) {

						for (int i = 0; i < rowsSelected.length; i++) {
							String multiValueId = rowsSelected[i];
							GenericValue associatedEntity = EntityUtil.getFirst(delegator.findByAnd(
									"CustomFieldMultiValue",
									UtilMisc.toMap("customFieldId", customFieldId, "multiValueId", multiValueId), null,
									false));

							if (UtilValidate.isNotEmpty(associatedEntity)) {

								associatedEntity.remove();

								successCount++;
							}
						}

					}

				}

			}

			resp.put(CustomFieldConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);

			resp.put("successCount", successCount);

		} catch (Exception e) {

			resp.put(CustomFieldConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			resp.put(CustomFieldConstants.RESPONSE_MESSAGE, "Error: " + e.getMessage());

			Debug.logError(e.getMessage(), MODULE);
		}

		return doJSONResponse(response, resp);
	}

	@SuppressWarnings("unchecked")
	public static String getCustomFieldGroupingCodes(HttpServletRequest request, HttpServletResponse response)
			throws GenericEntityException {

		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");

		Locale locale = UtilHttp.getLocale(request);
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		HttpSession session = request.getSession(true);

		String groupType = request.getParameter("groupType");

		Map<String, Object> resp = new HashMap<String, Object>();
		try {

			Map<String, Object> requestParams = UtilMisc.toMap();

			List<Map<String, Object>> groupingCodeList = new ArrayList<Map<String, Object>>();

			if (UtilValidate.isNotEmpty(groupType)) {

				List<GenericValue> groupingCodes = delegator.findByAnd("CustomFieldGroupingCode",
						UtilMisc.toMap("groupType", groupType), UtilMisc.toList("sequenceNumber"), false);

				for (GenericValue groupingCode : groupingCodes) {
					Map<String, Object> ser = new HashMap<String, Object>();
					ser.put("customFieldGroupingCodeId", groupingCode.getString("customFieldGroupingCodeId"));
					ser.put("groupingCode", groupingCode.getString("groupingCode"));
					ser.put("description", groupingCode.getString("description"));
					ser.put("sequenceNumber", groupingCode.getString("sequenceNumber"));
					ser.put("groupType", groupingCode.getString("groupType"));

					groupingCodeList.add(ser);
				}

			}

			resp.put("groupingCodes", groupingCodeList);

			resp.put(CustomFieldConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);

		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);

			resp.put(CustomFieldConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			resp.put(CustomFieldConstants.RESPONSE_MESSAGE, e.getMessage());

			return doJSONResponse(response, resp);
		}

		return doJSONResponse(response, resp);
	}

	@SuppressWarnings("unchecked")
	public static String getCustomFieldGroups(HttpServletRequest request, HttpServletResponse response)
			throws GenericEntityException {

		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");

		Locale locale = UtilHttp.getLocale(request);
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		HttpSession session = request.getSession(true);

		String groupingCode = request.getParameter("groupingCode");
		String customFieldGroupingCodeId = request.getParameter("customFieldGroupingCodeId");

		String roleTypeId = request.getParameter("roleTypeId");
		String isActive = request.getParameter("isActive");
		String groupType = request.getParameter("groupType");
		String groupId = request.getParameter("groupId");

		Map<String, Object> resp = new HashMap<String, Object>();
		try {

			Map<String, Object> requestParams = UtilMisc.toMap();

			List<Map<String, Object>> groups = new ArrayList<Map<String, Object>>();
			if (UtilValidate.isNotEmpty(groupingCode)) {
				requestParams.put("groupingCode", groupingCode);
				/*GenericValue cfgc = EntityUtil.getFirst(delegator.findByAnd("CustomFieldGroupingCode",
						UtilMisc.toMap("groupingCode", groupingCode), null, false));

				if (UtilValidate.isNotEmpty(cfgc)) {
					requestParams.put("groupingCode", cfgc.getString("customFieldGroupingCodeId"));
				}*/
			}

			if (UtilValidate.isNotEmpty(customFieldGroupingCodeId)) {
				requestParams.put("groupingCode", customFieldGroupingCodeId);
			}
			if (UtilValidate.isNotEmpty(groupId)) {
				requestParams.put("groupId", groupId);
			}

			if (UtilValidate.isNotEmpty(roleTypeId)) {
				requestParams.put("roleTypeId", roleTypeId);
			}
			if (UtilValidate.isNotEmpty(isActive)) {
				requestParams.put("isActive", isActive);
			}
			if (UtilValidate.isNotEmpty(groupType)) {
				requestParams.put("groupType", groupType);
			}
			//requestParams.put("groupType","ECONOMIC_METRIC");

			List<GenericValue> groupList = delegator.findByAnd("CustomFieldGroupSummary", requestParams,
					UtilMisc.toList("sequence"), false);
			for (GenericValue group : groupList) {
				Map<String, Object> ser = new HashMap<String, Object>();
				ser.put("groupId", group.getString("groupId"));
				ser.put("groupName", group.getString("groupName"));
				ser.put("sequence", group.getLong("sequence"));
				ser.put("hide", group.getString("hide"));
				ser.put("valueCapture", group.getString("valueCapture"));
				ser.put("customFieldGroupingCodeId", group.getString("groupingCode"));

				groups.add(ser);
			}

			resp.put("groups", groups);

			resp.put(CustomFieldConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);

		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);

			resp.put(CustomFieldConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			resp.put(CustomFieldConstants.RESPONSE_MESSAGE, e.getMessage());

			return doJSONResponse(response, resp);
		}

		return doJSONResponse(response, resp);
	}

	@SuppressWarnings("unchecked")
	public static String getCustomFields(HttpServletRequest request, HttpServletResponse response)
			throws GenericEntityException {

		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");

		Locale locale = UtilHttp.getLocale(request);
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		HttpSession session = request.getSession(true);

		String groupId = request.getParameter("groupId");
		String isEnabled = request.getParameter("isEnabled");

		String hide = request.getParameter("hide");

		Map<String, Object> resp = new HashMap<String, Object>();
		try {

			Map<String, Object> requestParams = UtilMisc.toMap();

			List<Map<String, Object>> fields = new ArrayList<Map<String, Object>>();

			if (UtilValidate.isNotEmpty(groupId)) {
				requestParams.put("groupId", groupId);
			}

			if (UtilValidate.isNotEmpty(isEnabled)) {
				requestParams.put("isEnabled", isEnabled);
			}
			if (UtilValidate.isNotEmpty(hide)) {
				requestParams.put("hide", hide);
			}

			List<GenericValue> fieldList = delegator.findByAnd("CustomFieldSummary", requestParams,
					UtilMisc.toList("sequenceNumber"), false);
			for (GenericValue field : fieldList) {
				Map<String, Object> ser = new HashMap<String, Object>();
				ser.put("customFieldId", field.getString("customFieldId"));
				ser.put("groupId", field.getString("groupId"));
				ser.put("groupType", field.getString("groupType"));
				ser.put("groupName", field.getString("groupName"));
				ser.put("sequenceNumber", field.getLong("sequenceNumber"));
				ser.put("hide", field.getString("hide"));
				ser.put("isEnabled", field.getString("isEnabled"));
				ser.put("customFieldName", field.getString("customFieldName"));

				fields.add(ser);
			}

			resp.put("fields", fields);

			resp.put(CustomFieldConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);

		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);

			resp.put(CustomFieldConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			resp.put(CustomFieldConstants.RESPONSE_MESSAGE, e.getMessage());

			return doJSONResponse(response, resp);
		}

		return doJSONResponse(response, resp);
	}

	@SuppressWarnings("unused")
	public static String downloadFile(HttpServletRequest request, HttpServletResponse response)
			throws ComponentException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		Locale locale = UtilHttp.getLocale(request);
		FileInputStream fis = null;
		Map<String, Object> resp = new HashMap<String, Object>();
		String resourceName = request.getParameter("resourceName");
		String componentName = request.getParameter("componentName");
		String fileName = request.getParameter("fileName");
		String filePath = ComponentConfig.getRootLocation(componentName) + "webapp/" + resourceName + "/template/";
		String rootPath = filePath;
		OutputStream os = null;
		try {
			File file = new File(filePath + fileName);
			if (file.getCanonicalPath().startsWith(new File(rootPath).getCanonicalPath())) {
				fis = new FileInputStream(file);
				byte b[];
				int x = fis.available();
				b = new byte[x];
				Debug.log(" b size" + b.length);
				fis.read(b);

				response.setContentType("text/csv");
				response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

				os = response.getOutputStream();
				os.write(b);
				os.flush();
			}
			resp.put("fields", "Success");
		} catch (Exception e) {
			Debug.logError(e.getMessage(), MODULE);
			resp.put("fields", "Failed");
		} finally {
			try {
				if (os != null) {
					os.close();
				}
				if (fis != null) {
					fis.close();
				}
			} catch (Exception e) {
				Debug.logError(e.getMessage(), MODULE);
			}
		}
		return doJSONResponse(response, resp);
	}

	public static String multiValueUpdate(HttpServletRequest request, HttpServletResponse response)
			throws GenericEntityException {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		String customFieldId = (String) request.getParameter("mvUpdateCustomFieldId");
		String currentFieldId = (String) request.getParameter("currentFieldId");
		if (UtilValidate.isNotEmpty(customFieldId) && UtilValidate.isNotEmpty(currentFieldId)) {
			String splitList[] = currentFieldId.split(",");
			for (String multiValueId : splitList) {
				if (UtilValidate.isNotEmpty(multiValueId)) {
					String description = request.getParameter("description_" + multiValueId);
					String hide = request.getParameter("hide_" + multiValueId);
					String mvSequenceNumber = request.getParameter("mvSequenceNumber_" + multiValueId);
					//if (UtilValidate.isNotEmpty(description)) {
						GenericValue customFieldMultiValueGV = delegator.findOne("CustomFieldMultiValue",
								UtilMisc.toMap("customFieldId", customFieldId, "multiValueId", multiValueId), false);
						if (customFieldMultiValueGV != null && customFieldMultiValueGV.size() > 0) {
							customFieldMultiValueGV.set("description", description);
							customFieldMultiValueGV.set("hide", hide);
							customFieldMultiValueGV.put("sequenceNumber",
									UtilValidate.isNotEmpty(mvSequenceNumber) ? Long.parseLong(mvSequenceNumber)
											: new Long(1));
							customFieldMultiValueGV.store();
						}
					//}
				}
			}

		}
		return doJSONResponse(response, UtilMisc.toMap("data", null));
	}

	public static String economicMetricErrorLogs(HttpServletRequest request, HttpServletResponse response)
			throws GenericEntityException {
		List<EntityCondition> conditionList = new ArrayList<EntityCondition>();
		String requestId = (String) request.getParameter("requestId");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		String draw = request.getParameter("draw");
		String start = request.getParameter("start");
		String length = request.getParameter("length");
		Long totalCount = Long.valueOf("0");
		Map<String, Object> returnMap = FastMap.newInstance();
		List<Object> economicImportErrorLogs = FastList.newInstance();
		try {
			if (UtilValidate.isNotEmpty(requestId)) {
				conditionList.add(EntityCondition.makeCondition(UtilMisc.toList(
						EntityCondition.makeCondition("requestId", EntityOperator.EQUALS, requestId),
						EntityCondition.makeCondition("customFieldType", EntityOperator.EQUALS, "ECONOMIC_METRIC")),
						EntityOperator.AND));
				EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				totalCount = EntityQuery.use(delegator).from("CustomFieldSegmentImportTemp").where(mainConditons)
						.queryCount();
				if (totalCount != null && totalCount > 0) {
					EntityFindOptions efo = new EntityFindOptions();
					efo.setDistinct(true);
					int startInx = UtilValidate.isNotEmpty(start) ? Integer.parseInt(start) : 0;
					int endInx = UtilValidate.isNotEmpty(length) ? Integer.parseInt(length) : 0;
					efo.setOffset(startInx);
					efo.setLimit(endInx);
					List<GenericValue> economicImportList = delegator.findList("CustomFieldSegmentImportTemp",
							mainConditons, null, null, efo, false);
					if (economicImportList != null && economicImportList.size() > 0) {
						for (GenericValue economicImportGV : economicImportList) {
							Map<String, Object> economicImportMap = FastMap.newInstance();
							economicImportMap.put("partyId", economicImportGV.getString("partyId"));
							economicImportMap.put("customFieldGroupId",
									economicImportGV.getString("customFieldGroupId"));
							economicImportMap.put("segmentValueId", economicImportGV.getString("segmentValueId"));
							economicImportMap.put("metricValue", economicImportGV.getString("metricValue"));
							economicImportMap.put("message", economicImportGV.getString("message"));
							economicImportMap.put("requestId", economicImportGV.getString("requestId"));
							economicImportErrorLogs.add(economicImportMap);
						}
					}
				}
			}
			returnMap.put("data", economicImportErrorLogs);
			returnMap.put("draw", draw);
			returnMap.put("recordsTotal", totalCount);
			returnMap.put("recordsFiltered", totalCount);
		} catch (Exception e) {
			returnMap.put("data", economicImportErrorLogs);
			returnMap.put("draw", draw);
			returnMap.put("recordsTotal", 0);
			returnMap.put("recordsFiltered", 0);
			Debug.logError("Exception in Economic Metric Import Error Logs" + e.getMessage(), MODULE);
			return AjaxEvents.doJSONResponse(response, returnMap);
		}
		return AjaxEvents.doJSONResponse(response, returnMap);
	}

	@SuppressWarnings("unused")
	public static String downloadEconomicErrorLogs(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		Locale locale = UtilHttp.getLocale(request);
		Map<String, Object> resp = new HashMap<String, Object>();
		String requestId = request.getParameter("requestId");

		if (UtilValidate.isNotEmpty(requestId)) {
			try (DataOutputStream outputStream = new DataOutputStream(response.getOutputStream())) {
				response.setContentType("text/csv");
				response.setHeader("Content-Disposition", "attachment; filename=\"Economic Metric Error Logs.csv\"");

				List<EntityCondition> conditionList = new ArrayList<EntityCondition>();
				conditionList.add(EntityCondition.makeCondition(UtilMisc.toList(
						EntityCondition.makeCondition("requestId", EntityOperator.EQUALS, requestId),
						EntityCondition.makeCondition("customFieldType", EntityOperator.EQUALS, "ECONOMIC_METRIC")),
						EntityOperator.AND));
				EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				List<GenericValue> customFieldSegmentImportTempList = delegator.findList("CustomFieldSegmentImportTemp",
						mainConditons, null, null, null, false);

				String outputResult = "Party Id, Economic Code Id, Metric Id, Metric Value, Message\n";
				outputStream.write(outputResult.getBytes());
				if (customFieldSegmentImportTempList != null && customFieldSegmentImportTempList.size() > 0) {
					for (GenericValue customFieldSegmentImportTemp : customFieldSegmentImportTempList) {
						String partyId = "";
						String codeId = "";
						String metricId = "";
						String metricValue = "";
						String message = "";
						partyId = customFieldSegmentImportTemp.getString("partyId");
						codeId = customFieldSegmentImportTemp.getString("customFieldGroupId");
						metricId = customFieldSegmentImportTemp.getString("segmentValueId");
						metricValue = customFieldSegmentImportTemp.getString("metricValue");
						message = customFieldSegmentImportTemp.getString("message");
						outputResult = partyId + "," + codeId + "," + metricId + "," + metricValue + "," + message
								+ "\n";
						outputStream.write(outputResult.getBytes());

					}
				} else {
					outputResult = "No data available in table\n";
					outputStream.write(outputResult.getBytes());
				}
				resp.put("fields", "Success");
				outputStream.flush();
			} catch (Exception e) {
				Debug.logError(e.getMessage(), MODULE);
				resp.put("fields", "Failed");
			}
		}
		return doJSONResponse(response, resp);
	}

	public static String checkCampaignTemplate(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		Locale locale = UtilHttp.getLocale(request);
		Map<String, Object> resp = new HashMap<String, Object>();
		String campaignId = request.getParameter("campaignId");
		String flag = "N";
		if (UtilValidate.isNotEmpty(campaignId)) {
			try {
				GenericValue marketingCampaign = delegator.findOne("MarketingCampaign",
						UtilMisc.toMap("marketingCampaignId", campaignId), false);
				if (marketingCampaign != null && marketingCampaign.size() > 0) {
					String templateId = marketingCampaign.getString("campaignTemplateId");
					if (UtilValidate.isNotEmpty(templateId)) {
						flag = "Y";
					}
				}
			} catch (Exception e) {
				Debug.logError(e.getMessage(), MODULE);
			}
		}
		resp.put("flag", flag);
		return doJSONResponse(response, resp);
	}

	public static String getContactFieldGroup(HttpServletRequest request, HttpServletResponse response)
			throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map<String, Object> result = new HashMap<String, Object>();
		List<GenericValue> fieldGroup = null;
		try {
			List<EntityCondition> conditionsList = FastList.newInstance();
			String groupId = request.getParameter("groupId");
			String groupName = request.getParameter("groupName");
			String hide = request.getParameter("hide");
			if (UtilValidate.isNotEmpty(groupId)) {
				conditionsList.add(EntityCondition.makeCondition("groupId", EntityOperator.LIKE, "%" + groupId + "%"));
			}
			if (UtilValidate.isNotEmpty(groupName)) {
				conditionsList
						.add(EntityCondition.makeCondition("groupName", EntityOperator.LIKE, "%" + groupName + "%"));
			}
			if (UtilValidate.isNotEmpty(hide)) {
				conditionsList.add(EntityCondition.makeCondition("hide", EntityOperator.EQUALS, hide));
			}

			conditionsList.add(EntityCondition.makeCondition("groupType", EntityOperator.EQUALS, "CONTACT_FIELD_LIST"));
			EntityFindOptions efo = new EntityFindOptions();
			efo.setDistinct(true);

			List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
			fieldGroup = delegator.findList("CustomFieldGroup",
					EntityCondition.makeCondition(conditionsList, EntityOperator.AND), null,
					UtilMisc.toList("groupId DESC"), efo, false);

			if (UtilValidate.isNotEmpty(fieldGroup)) {
				fieldGroup.forEach(obj -> {
					try {
						Map<String, Object> dataMap = new HashMap<String, Object>();
						dataMap.put("groupId", obj.getString("groupId"));
						dataMap.put("groupName", obj.getString("groupName"));
						dataMap.put("hide", obj.getString("hide"));
						dataMap.put("sequence", obj.getString("sequence"));
						dataList.add(dataMap);
					} catch (Exception ex) {
						Debug.logInfo("Error-" + ex.getMessage(), MODULE);
						ex.printStackTrace();
						result.put("errorMessage", ex.getMessage());
						result.put("responseMessage", "error");
						result.put("data", new ArrayList<Map<String, Object>>());
					}
				});
			}
			result.put("data", dataList);
			result.put("responseMessage", "success");
			result.put("successMessage", "Data Loaded successfully.");
		} catch (Exception e) {
			e.printStackTrace();
			result.put("errorMessage", e.getMessage());
			result.put("responseMessage", "error");
			result.put("data", new ArrayList<Map<String, Object>>());
		}
		return doJSONResponse(response, result);
	}

	public static String getContactField(HttpServletRequest request, HttpServletResponse response)
			throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map<String, Object> result = new HashMap<String, Object>();
		List<GenericValue> fieldGroup = null;
		try {
			List<EntityCondition> conditionsList = FastList.newInstance();
			String groupId = request.getParameter("groupId");
			String customFieldName = request.getParameter("customFieldName");
			String customFieldType = request.getParameter("customFieldType");
			String customFieldFormat = request.getParameter("customFieldFormat");
			String customFieldLength = request.getParameter("customFieldLength");
			String hide = request.getParameter("hide");
			if (UtilValidate.isNotEmpty(groupId)) {
				conditionsList.add(EntityCondition.makeCondition("groupId", EntityOperator.EQUALS, groupId));
			}
			if (UtilValidate.isNotEmpty(customFieldName)) {
				conditionsList.add(EntityCondition.makeCondition("customFieldName", EntityOperator.LIKE,
						"%" + customFieldName + "%"));
			}
			if (UtilValidate.isNotEmpty(customFieldType)) {
				conditionsList
						.add(EntityCondition.makeCondition("customFieldType", EntityOperator.EQUALS, customFieldType));
			}
			if (UtilValidate.isNotEmpty(customFieldFormat)) {
				conditionsList.add(
						EntityCondition.makeCondition("customFieldFormat", EntityOperator.EQUALS, customFieldFormat));
			}
			if (UtilValidate.isNotEmpty(customFieldLength)) {
				conditionsList.add(
						EntityCondition.makeCondition("customFieldLength", EntityOperator.EQUALS, customFieldLength));
			}
			if (UtilValidate.isNotEmpty(hide)) {
				conditionsList.add(EntityCondition.makeCondition("hide", EntityOperator.EQUALS, hide));
			}

			conditionsList.add(EntityCondition.makeCondition("groupType", EntityOperator.EQUALS, "CONTACT_FIELD_LIST"));
			EntityFindOptions efo = new EntityFindOptions();
			efo.setDistinct(true);

			List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
			fieldGroup = delegator.findList("CustomField",
					EntityCondition.makeCondition(conditionsList, EntityOperator.AND), null,
					UtilMisc.toList("groupId DESC"), efo, false);

			if (UtilValidate.isNotEmpty(fieldGroup)) {
				fieldGroup.forEach(obj -> {
					try {
						Map<String, Object> dataMap = new HashMap<String, Object>();
						dataMap.put("customFieldId", obj.getString("customFieldId"));
						dataMap.put("customFieldName", obj.getString("customFieldName"));
						dataMap.put("groupId", obj.getString("groupId"));
						dataMap.put("groupName", obj.getString("groupName"));
						dataMap.put("customFieldType", obj.getString("customFieldType"));
						dataMap.put("customFieldFormat", obj.getString("customFieldFormat"));
						dataMap.put("sequenceNumber", obj.getString("sequenceNumber"));
						dataMap.put("customFieldLength", obj.getString("customFieldLength"));
						dataMap.put("hide", obj.getString("hide"));
						dataList.add(dataMap);
					} catch (Exception ex) {
						Debug.logInfo("Error-" + ex.getMessage(), MODULE);
						ex.printStackTrace();
						result.put("errorMessage", ex.getMessage());
						result.put("responseMessage", "error");
						result.put("data", new ArrayList<Map<String, Object>>());
					}
				});
			}
			result.put("data", dataList);
			result.put("responseMessage", "success");
			result.put("successMessage", "Data Loaded successfully.");
		} catch (Exception e) {
			e.printStackTrace();
			result.put("errorMessage", e.getMessage());
			result.put("responseMessage", "error");
			result.put("data", new ArrayList<Map<String, Object>>());
		}
		return doJSONResponse(response, result);
	}

	public static String findsegment(HttpServletRequest request, HttpServletResponse response)
			throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		String groupingCode = request.getParameter("groupingCode");
		String groupId = request.getParameter("groupId");
		String customFieldId = request.getParameter("customFieldId");
		String customFieldName = request.getParameter("customFieldName");
		String isEnabled = request.getParameter("isEnabled");
		try {

			List<EntityCondition> conditionsList = FastList.newInstance();
			EntityCondition condition = null;
			conditionsList.add(EntityCondition.makeCondition("groupType", EntityOperator.EQUALS, "SEGMENTATION"));
			if (UtilValidate.isNotEmpty(groupId)) {
				conditionsList.add(EntityCondition.makeCondition("groupId", EntityOperator.EQUALS, groupId));
			}
			if (UtilValidate.isNotEmpty(customFieldId)) {
				conditionsList
						.add(EntityCondition.makeCondition("customFieldId", EntityOperator.LIKE, "%" + customFieldId + "%"));
			}
			if (UtilValidate.isNotEmpty(customFieldName)) {
				conditionsList.add(EntityCondition.makeCondition("customFieldName", EntityOperator.LIKE,
						"%" + customFieldName.toUpperCase() + "%"));
			}
			if (UtilValidate.isNotEmpty(isEnabled)) {
				conditionsList.add(EntityCondition.makeCondition("isEnabled", EntityOperator.EQUALS, isEnabled));
			}
			condition = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);
			List<GenericValue> fieldGroup = EntityQuery.use(delegator).from("CustomFieldSummary").where(condition)
					.orderBy("sequenceNumber").queryList();
			for (GenericValue field : fieldGroup) {
				Map<String, Object> Data = new HashMap<String, Object>();
				String groupingCodee = field.getString("groupingCode");
				Data.putAll(field);

				if (UtilValidate.isNotEmpty(groupingCodee)) {
					GenericValue code = EntityUtil.getFirst(delegator.findByAnd("CustomFieldGroupingCode",
							UtilMisc.toMap("customFieldGroupingCodeId", groupingCodee), null, false));
					if (UtilValidate.isNotEmpty(groupingCode)) {
						if (UtilValidate.isNotEmpty(code)) {
							if (!code.getString("groupingCode").equals(groupingCode)) {
								continue;
							}
							Data.put("groupingCodeName", code.getString("groupingCode"));
						} else {
							continue;
						}
					} else {
						if (UtilValidate.isNotEmpty(code)) {
							Data.put("groupingCodeName", code.getString("groupingCode"));
						}
					}
				} else {
					if (UtilValidate.isNotEmpty(groupingCode)) {
						continue;
					}

				}
				results.add(Data);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, results);
	}

	public static String findAttributeGroup(HttpServletRequest request, HttpServletResponse response)
			throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		String groupName = (String) context.get("groupName");
		String groupId = (String) context.get("groupId");
		String hide = (String) context.get("hide");
		
		Object groupingCodeId = (Object) context.get("groupingCodeId");
		String groupType = (String) context.get("groupType");
		
		try {
			List<EntityCondition> conditionsList = FastList.newInstance();
			
			if (UtilValidate.isEmpty(groupType)) {
				return doJSONResponse(response, dataList);
			}
			
			conditionsList.add(EntityCondition.makeCondition("groupType", EntityOperator.EQUALS, groupType));
			
			if (UtilValidate.isNotEmpty(groupName)) {
				conditionsList.add(
						EntityCondition.makeCondition("groupName", EntityOperator.LIKE, "%" + groupName.toUpperCase() + "%"));
			}
			if (UtilValidate.isNotEmpty(groupId)) {
				conditionsList.add(
						EntityCondition.makeCondition("groupId", EntityOperator.LIKE, "%" + groupId.toUpperCase() + "%"));
			}
			if (UtilValidate.isNotEmpty(groupingCodeId)) {
				if ((groupingCodeId instanceof List)) {
					List<String> codeIdList = (List<String>) groupingCodeId;
					List<EntityCondition> codeCondList = FastList.newInstance();
					for (String codeId : codeIdList) {
						codeCondList.add(EntityCondition.makeCondition("groupingCode", EntityOperator.LIKE, "%"+codeId+"%"));
					}
					EntityCondition codeCond = EntityCondition.makeCondition(codeCondList, EntityOperator.OR);
					conditionsList.add(codeCond);
				} else {
					conditionsList.add(EntityCondition.makeCondition("groupingCode", EntityOperator.LIKE, "%"+groupingCodeId+"%"));
				}
			}
			if (UtilValidate.isNotEmpty(hide)) {
				conditionsList.add(EntityCondition.makeCondition("hide", EntityOperator.EQUALS, hide));
			}
			
			EntityCondition condition = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);
			List<GenericValue> cfgList = EntityQuery.use(delegator).from("CustomFieldGroup")
					.where(condition).orderBy("sequence").queryList();
			if (UtilValidate.isNotEmpty(cfgList)) {
				for (GenericValue cfg : cfgList) {
					Map<String, Object> data = new LinkedHashMap<>();
					data.putAll(cfg.getAllFields());
					
					String groupingCodeName = UtilAttribute.getGroupingCodeDescByIds(delegator, cfg.getString("groupingCode"));
					/*GenericValue groupingCode = EntityQuery.use(delegator).select("groupingCode", "description").from("CustomFieldGroupingCode").where("customFieldGroupingCodeId", cfg.getString("groupingCode")).cache(true).queryOne();
					if (UtilValidate.isNotEmpty(groupingCode)) {
						groupingCodeName = groupingCode.getString("description")+" ( "+groupingCode.getString("groupingCode")+" )";
					}*/
					data.put("groupingCodeName", groupingCodeName);
					
					dataList.add(data);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}
		return doJSONResponse(response, dataList);
	}

	public static String getGroupSegmentCode(HttpServletRequest request, HttpServletResponse response)
			throws GenericEntityException {

		Delegator delegator = (Delegator) request.getAttribute("delegator");

		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");

		if (UtilValidate.isEmpty(userLogin)) {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("error", "No user login details found!");
			return doJSONResponse(response, data);
		}
		try {

			String groupType = request.getParameter("groupType");

			EntityCondition cond = null;

			if (UtilValidate.isNotEmpty(groupType)) {

				cond = EntityCondition.makeCondition("groupType", EntityOperator.EQUALS, groupType);

			}

			List<GenericValue> getGroupSegmentCode = delegator.findList("CustomFieldGroupingCode", cond, null, null,
					null, false);

			for (GenericValue Entry : getGroupSegmentCode) {
				Map<String, Object> data = new HashMap<String, Object>();

				data.put("groupingCode", Entry.get("groupingCode"));
				data.put("description", Entry.getString("description"));
				data.put("sequenceNumber", Entry.getString("sequenceNumber"));
				data.put("customFieldGroupingCodeId", Entry.getString("customFieldGroupingCodeId"));

				results.add(data);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}

		return doJSONResponse(response, results);
	}

	public static String getEconometricCode(HttpServletRequest request, HttpServletResponse response)
			throws GenericEntityException {

		Delegator delegator = (Delegator) request.getAttribute("delegator");

		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");

		String economicCode = request.getParameter("groupId");
		String economicName = request.getParameter("groupName");
		String groupingCode = request.getParameter("groupingCode");
		String valueCapture = request.getParameter("valueCapture");
		String isCampaignUse = request.getParameter("isCampaignUse");
		String type = request.getParameter("type");
		

		if (UtilValidate.isEmpty(userLogin)) {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("error", "No user login details found!");
			return doJSONResponse(response, data);
		}
		try {

			Map<String, Object> condition = UtilMisc.toMap("groupType", GroupType.ECONOMIC_METRIC);

			if (UtilValidate.isNotEmpty(economicCode)) {
				condition.put("groupId", economicCode);
			}
			if (UtilValidate.isNotEmpty(groupingCode)) {
				condition.put("groupingCode", groupingCode);
				/*GenericValue cfgc = EntityUtil.getFirst(delegator.findByAnd("CustomFieldGroupingCode",
						UtilMisc.toMap("groupingCode", groupingCode), null, false));
				if(UtilValidate.isNotEmpty(cfgc)) {
				condition.put("groupingCode", cfgc.getString("customFieldGroupingCodeId"));
				}*/
			}
			
			if (UtilValidate.isNotEmpty(valueCapture)) {
				condition.put("valueCapture", valueCapture);
			}
			if (UtilValidate.isNotEmpty(isCampaignUse)) {
				condition.put("isCampaignUse", isCampaignUse);
			}
			if (UtilValidate.isNotEmpty(type)) {
				condition.put("type", type);
			}

			EntityCondition cond = EntityCondition.makeCondition(condition);

			if (UtilValidate.isNotEmpty(economicName)) {
				EntityCondition nameCondition = EntityCondition.makeCondition(
						EntityFunction.UPPER(EntityFieldValue.makeFieldValue("groupName")), EntityOperator.LIKE,
						"%" + economicName.toUpperCase() + "%");
				cond = EntityCondition.makeCondition(UtilMisc.toList(cond, nameCondition), EntityOperator.AND);
			}

			List<GenericValue> getEconometricCode = delegator.findList("CustomFieldGroup", cond, null, null, null,
					false);

			for (GenericValue Entry : getEconometricCode) {
				Map<String, Object> data = new HashMap<String, Object>();
				GenericValue getGroupingCode  =delegator.findOne("CustomFieldGroupingCode", true, UtilMisc.toMap("customFieldGroupingCodeId", Entry.get("groupingCode")));
                 				
                if(getGroupingCode !=null ) {
           		data.put("groupingCode", getGroupingCode.get("groupingCode") );
                }
	
				data.put("groupId", Entry.get("groupId"));
				data.put("groupName", Entry.get("groupName"));
				data.put("isActive", Entry.get("isActive"));
				data.put("sequence", Entry.getString("sequence"));
				results.add(data);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}

		return doJSONResponse(response, results);
	}

	public static String getEconomicMetric(HttpServletRequest request, HttpServletResponse response)
			throws GenericEntityException {
		
		Delegator delegator = (Delegator) request.getAttribute("delegator");

		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");

		String economicMetricName = request.getParameter("customFieldName");
		String isEnabled = request.getParameter("isEnabled");
		String groupingCode = request.getParameter("groupingCode");
		String economicMetricId = request.getParameter("customFieldId");
		String economicCode=request.getParameter("groupId");
		
				

		
		if (UtilValidate.isEmpty(userLogin)) {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("error", "No user login details found!");
			return doJSONResponse(response, data);
		}
		try {
			
			EntityCondition cond =null;
			

         Map<String, Object> condition = UtilMisc.toMap("groupType", "ECONOMIC_METRIC"); 	


        if (UtilValidate.isNotEmpty(isEnabled)) {
	       condition.put("isEnabled", isEnabled);
       }
        if (UtilValidate.isNotEmpty(groupingCode)) {
        	GenericValue   cfgc = EntityUtil.getFirst( delegator.findByAnd("CustomFieldGroupingCode", UtilMisc.toMap("groupingCode", groupingCode), null, false) );
	       condition.put("groupingCode", cfgc.getString("customFieldGroupingCodeId"));
        }
        if (UtilValidate.isNotEmpty(economicCode)) {
        	condition.put("groupId", economicCode);
        }

        cond = EntityCondition.makeCondition(condition);

      if (UtilValidate.isNotEmpty(economicMetricName)) {
      	EntityCondition nameCondition = EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("customFieldName")), EntityOperator.LIKE, "%"+economicMetricName.toUpperCase()+"%");
//	    cond = EntityCondition.makeCondition([cond,
//		nameCondition
//	], EntityOperator.AND);
	    
	    cond=EntityCondition.makeCondition(UtilMisc.toList(cond,nameCondition),EntityOperator.AND);
       }
      if (UtilValidate.isNotEmpty(economicMetricId)) {
        	EntityCondition IdCondition = EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("customFieldId")), EntityOperator.LIKE, "%"+economicMetricId.toUpperCase()+"%");
  	    cond=EntityCondition.makeCondition(UtilMisc.toList(cond,IdCondition),EntityOperator.AND);
         }

			List<GenericValue>  customFieldList = delegator.findList("CustomFieldSummary", cond, null, null, null, false);						
			
			for (GenericValue Entry : customFieldList) {
				Map<String, Object> data = new HashMap<String, Object>();
				
				if (UtilValidate.isNotEmpty(Entry.getString("groupingCode"))) {
					GenericValue	groupCode = EntityUtil.getFirst( delegator.findByAnd("CustomFieldGroupingCode",UtilMisc.toMap("customFieldGroupingCodeId", Entry.getString("groupingCode")), null, false) );
					if (UtilValidate.isNotEmpty(groupCode)) {
						data.put("groupingCodeName",groupCode.get("groupingCode") );
					} 
				}
							
				data.put("groupName",Entry.get("groupName") );
				data.put("groupId",Entry.get("groupId") );
				data.put("customFieldId", Entry.get("customFieldId") );
				data.put("customFieldName",  Entry.getString("customFieldName"));
				data.put("isEnabled", Entry.get("isEnabled") );
				data.put("sequenceNumber",  Entry.getString("sequenceNumber"));
				data.put("productPromoCodeGroupId", UtilValidate.isNotEmpty(Entry.getString("productPromoCodeGroupId")) ? Entry.getString("productPromoCodeGroupId") : "");
				
				
				results.add(data);
			}


		} catch (Exception e) {
			e.printStackTrace();
			return doJSONResponse(response, e.getMessage());
		}

		return doJSONResponse(response, results);
	}
	
	public static String getSearchCustomersList(HttpServletRequest request, HttpServletResponse response)
			throws GenericEntityException {
		
		Debug.log("start searach event  ::: ");
		
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		Map<String, Object> results = new HashMap<String, Object>();
		
	
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");

		if (UtilValidate.isEmpty(userLogin)) {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("error", "No user login details found!");
			return doJSONResponse(response, data);
		}

		String groupId = request.getParameter("groupId");
		String customFieldId = request.getParameter("customFieldId");

		String searchPartyId = request.getParameter("searchPartyId");
		String searchFirstName = request.getParameter("searchFirstName");
		String searchEmailId = request.getParameter("searchEmailId");
		String searchPhoneNum = request.getParameter("searchPhoneNum");
		String searchRoleTypeId = request.getParameter("searchRoleTypeId");

		String draw = request.getParameter("draw");
		String start = request.getParameter("start");
		String length = request.getParameter("length");

	
		Debug.log("after request parsms   ::: ");
		
		List<EntityCondition> roleConditionList = FastList.newInstance();
		List<EntityCondition> conditionsList = FastList.newInstance();
		List<GenericValue> roleConfigs = delegator.findByAnd("CustomFieldRoleConfig",
				UtilMisc.toMap("groupId", groupId), UtilMisc.toList("sequenceNumber"), false);

		roleConfigs.forEach(roleConfig -> conditionsList.add(
				EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, roleConfig.get("roleTypeId"))));

		if (UtilValidate.isNotEmpty(roleConditionList)) {
			EntityCondition roleCondition = EntityCondition.makeCondition(roleConditionList, EntityOperator.OR);
			conditionsList.add(roleCondition);
		}

		if (UtilValidate.isNotEmpty(searchRoleTypeId)) {
			conditionsList
					.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, searchRoleTypeId));
		}

		EntityCondition partyStatusCondition = EntityCondition.makeCondition(
				UtilMisc.toList(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PARTY_DISABLED"),
						EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, null)),
				EntityOperator.OR

		);

		conditionsList.add(partyStatusCondition);
		conditionsList.add(EntityUtil.getFilterByDateExpr());

		if (UtilValidate.isNotEmpty(searchPartyId)) {

			EntityCondition partyCondition = EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,
					searchPartyId);
			conditionsList.add(partyCondition);
		}

		if (UtilValidate.isNotEmpty(searchFirstName)) {
			EntityCondition nameCondition = EntityCondition.makeCondition(UtilMisc.toList(
					EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("firstName")),
							EntityOperator.LIKE, "%" + searchFirstName.toUpperCase() + "%"),
					EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("groupName")),
							EntityOperator.LIKE, "%" + searchFirstName.toUpperCase() + "%")),
					EntityOperator.OR);
			conditionsList.add(nameCondition);
		}

		List<EntityCondition> eventExprs = new ArrayList<>();

		if (UtilValidate.isNotEmpty(searchEmailId) || UtilValidate.isNotEmpty(searchPhoneNum)) {

			if (UtilValidate.isNotEmpty(searchEmailId)) {
				EntityCondition emailCondition = EntityCondition.makeCondition(UtilMisc.toList(
						EntityCondition.makeCondition("infoString", EntityOperator.LIKE, searchEmailId + "%"),
						EntityCondition.makeCondition("contactMechTypeId", EntityOperator.EQUALS, "EMAIL_ADDRESS")),
						EntityOperator.AND);
		
				eventExprs.add(emailCondition);
			
			}

			if (UtilValidate.isNotEmpty(searchPhoneNum)) {
				EntityCondition phoneCondition = EntityCondition.makeCondition(UtilMisc.toList(
						EntityCondition.makeCondition("contactNumber", EntityOperator.EQUALS, searchPhoneNum),
						EntityCondition.makeCondition("contactMechTypeId", EntityOperator.EQUALS, "TELECOM_NUMBER")),
						EntityOperator.AND);
	
				eventExprs.add(phoneCondition);
				
			}

			conditionsList.add(EntityCondition.makeCondition(eventExprs, EntityOperator.OR));
		}

		EntityCondition mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);

		
		
		EntityFindOptions efo = new EntityFindOptions();
		efo.setDistinct(true);
		long count = 0;
		EntityFindOptions efoNum = new EntityFindOptions();
		efoNum.setDistinct(true);
		efoNum.getDistinct();
		efoNum.setFetchSize(1000);

		count = delegator.findCountByCondition("PartyFromByRelnAndContactInfoAndPartyClassification", mainConditons,
				null, UtilMisc.toSet("partyId"), efoNum);

		int startInx = UtilValidate.isNotEmpty(start) ? Integer.parseInt(start) : 0;
		int endInx = UtilValidate.isNotEmpty(length) ? Integer.parseInt(length) : 0;
		efo.setOffset(startInx);
		efo.setLimit(endInx);

		List<GenericValue> partyList = delegator.findList("PartyFromByRelnAndContactInfoAndPartyClassification",
				mainConditons, UtilMisc.toSet("partyId"), UtilMisc.toList("createdDate"), efo, false);
		String segmentationValueAssociatedEntityName = DataUtil.getSegmentationValueAssociatedEntityName(delegator,
				groupId);
		List<Object> dataMap = new ArrayList<>();

		long recordsFiltered = count;
		long recordsTotal = count;

		if (partyList != null && partyList.size() > 0) {

			int id = 1;
			for (GenericValue party : partyList) {

				Map<String, Object> data = new HashMap<String, Object>();
				String partyId = party.getString("partyId");
				GenericValue valueAssociation = EntityUtil
						.getFirst(delegator.findByAnd(segmentationValueAssociatedEntityName,
								UtilMisc.toMap("groupId", groupId, "customFieldId", customFieldId, "partyId", partyId),
								null, false));
				if (UtilValidate.isNotEmpty(valueAssociation)) {
					continue;
				}

				GenericValue partySummary = delegator.findOne("PartySummaryDetailsView",
						UtilMisc.toMap("partyId", partyId), false);
				if (partySummary != null && partySummary.size() > 0) {
					JSONObject list = new JSONObject();
					String groupName = partySummary.getString("groupName");
					String statusId = partySummary.getString("statusId");
					String statusItemDesc = "";
					String name = partySummary.getString("firstName") + " " + partySummary.getString("lastName");
					if (UtilValidate.isNotEmpty(statusId)) {
						GenericValue statusItem = delegator.findOne("StatusItem", UtilMisc.toMap("statusId", statusId),
								false);
						if (statusItem != null && statusItem.size() > 0) {
							statusItemDesc = statusItem.getString("description");
						}
					}
					String dataSourceDesc = "";
					 GenericValue partyDataSource = EntityUtil.getFirst(delegator.findByAnd("PartyDataSource", UtilMisc.toMap("partyId", partyId ),null, false));
					 if (partyDataSource != null && partyDataSource.size() > 0) {
						 String dataSourceId = partyDataSource.getString("dataSourceId");
						 if (UtilValidate.isNotEmpty(dataSourceId)) {
						    GenericValue dataSource = delegator.findOne("DataSource",UtilMisc.toMap("dataSourceId", dataSourceId ) , false);
							   if (dataSource != null && dataSource.size() > 0) {
							    dataSourceDesc = dataSource.getString("description");
							   }
						   }
					 }

					String city = "";
					String state = "";
					String phoneNumber = "";
					String infoString = "";
//					Map<String, String> partyContactInfo = PartyPrimaryContactMechWorker
//							.getPartyPrimaryContactMechValueMaps(delegator, partyId);
//					phoneNumber = UtilValidate.isNotEmpty(partyContactInfo) ? partyContactInfo.get("PrimaryPhone") : "";
//					infoString = UtilValidate.isNotEmpty(partyContactInfo) ? partyContactInfo.get("EmailAddress") : "";
//
//					GenericValue postalAddress = PartyPrimaryContactMechWorker.getPartyPrimaryPostal(delegator,
//							partyId);
//					if (UtilValidate.isNotEmpty(postalAddress)) {
//						city = postalAddress.getString("city");
//
//						String stateProvinceGeoId = postalAddress.getString("stateProvinceGeoId");
//						if (UtilValidate.isNotEmpty(stateProvinceGeoId)) {
//							GenericValue geo = delegator.findOne("Geo", UtilMisc.toMap("geoId", stateProvinceGeoId),
//									false);
//							if (UtilValidate.isNotEmpty(geo)) {
//								state = geo.getString("geoName");
//							}
//						}
//					}

					name = PartyHelper.getPartyName(delegator, partyId, false);

					id = id + 1;
					data.put("id", id + "");
					data.put("partyId", partyId);
					data.put("name", name);
					data.put("statusId", statusItemDesc);
					data.put("phoneNumber", phoneNumber);
					data.put("infoString", infoString);
					data.put("city", city);
					data.put("state", state);

					result.add(data);
				}
			}

			results.put("data", result);
			results.put("draw", draw);
			results.put("recordsTotal", recordsTotal);
			results.put("recordsFiltered", recordsFiltered);
			Debug.log("if result    ::: ");
			
			return doJSONResponse(response, results);

		}else {
			results.put("data", null);
			results.put("draw", draw);
			results.put("recordsTotal", recordsTotal);
			results.put("recordsFiltered", recordsFiltered);

		return doJSONResponse(response, results);
	       }
	}
	
    public static String updateFieldParamConfig(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
        GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
        
        Locale locale = UtilHttp.getLocale(request);
        TimeZone timeZone = UtilHttp.getTimeZone(request);
        HttpSession session = request.getSession(true);
        
        String groupId = request.getParameter("groupId");
        String customFieldId = request.getParameter("customFieldId");

        Map<String, Object> resp = new HashMap<String, Object>();
        
        try {
        	String fieldParamTypes[] = request.getParameterValues("fieldParamType");
        	String fieldParamNames[] = request.getParameterValues("fieldParamName");
        	String fieldParamValues[] = request.getParameterValues("fieldParamValue");
        	String fieldParamValueTypes[] = request.getParameterValues("fieldParamValueType");

        	if (UtilValidate.isNotEmpty(fieldParamTypes)) {
        		GenericValue customField = EntityUtil.getFirst( delegator.findByAnd("CustomField", UtilMisc.toMap("groupId", groupId, "customFieldId", customFieldId), null, false) );
        		
        		List<Map> dataList = new ArrayList<>();
        		
        		Long sequenceNumber = new Long(1000);
        		for (int i = 0; i < fieldParamTypes.length; i++) {

        			String fieldParamType = fieldParamTypes[i];
        			String fieldParamName = fieldParamNames[i];
        			String fieldParamValue = fieldParamValues[i];
        			String fieldParamValueType = fieldParamValueTypes[i];
        			
        			if (UtilValidate.isEmpty(fieldParamType) && UtilValidate.isEmpty(fieldParamName) && UtilValidate.isEmpty(fieldParamValue) && UtilValidate.isEmpty(fieldParamValueType)) {
        				continue;
        			}
        			
        			Map<String, Object> data = new LinkedHashMap<String, Object>();
        			data.put("paramType", fieldParamType);
        			data.put("paramName", fieldParamName);
        			data.put("paramValue", fieldParamValue);
        			data.put("paramValueType", fieldParamValueType);
        			data.put("sequenceNumber", ""+sequenceNumber);
        			
        			dataList.add(data);
        			
        			sequenceNumber++;
        		}
        		
        		if (sequenceNumber > 1000) {
        			String paramData = ParamUtil.toJson(dataList);
            		System.out.println("paramData>> "+paramData);
            		
            		customField.put("paramData", paramData);
            		customField.store();
        		} else {
        			resp.put(CustomFieldConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
                    resp.put(CustomFieldConstants.RESPONSE_MESSAGE, "No valid data to be updated!!");
                    return doJSONResponse(response, resp);
        		}
        		
        	}
        	
        	resp.put(CustomFieldConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
        	resp.put(CustomFieldConstants.RESPONSE_MESSAGE, "Successfully updated param configuration!!");
        } catch (Exception e) {
        	e.printStackTrace();
            Debug.logError(e.getMessage(), MODULE);
            
            resp.put(CustomFieldConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
            resp.put(CustomFieldConstants.RESPONSE_MESSAGE, e.getMessage());
            return doJSONResponse(response, resp);
        }
        return doJSONResponse(response, resp);
    }
    
    public static String assignAttrGroupCode(HttpServletRequest request, HttpServletResponse response) {
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
		String domainEntityType = request.getParameter("domainEntityType");
		String domainEntityId = request.getParameter("domainEntityId");
		String groupingCodeId = request.getParameter("groupingCodeId");
		
		Map<String, Object> result = FastMap.newInstance();
		
		try {
			if (UtilValidate.isNotEmpty(domainEntityType) && UtilValidate.isNotEmpty(domainEntityId) && UtilValidate.isNotEmpty(groupingCodeId)) {
				GenericValue attrEntityAssoc = EntityUtil.getFirst( delegator.findByAnd("AttrEntityAssoc", UtilMisc.toMap("domainEntityType", domainEntityType), null, true) );
				UtilAttribute.storeAttribute(delegator, UtilMisc.toMap("attrEntityAssoc", attrEntityAssoc, "domainEntityType", domainEntityType, "domainEntityId", domainEntityId, "customFieldId", "ASSIGN_ATTR_GCODE", "value", groupingCodeId));
			}
			
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
			result.put(GlobalConstants.RESPONSE_MESSAGE, "Successfully assigned grouping code");
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, MODULE);
			
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());
			return AjaxEvents.doJSONResponse(response, result);
		}
		return AjaxEvents.doJSONResponse(response, result);
    }
    
    public static String findCampaignListsAjax(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String campaignNameToFind = request.getParameter("campaignNameToFind");
		String campaignStatusId = request.getParameter("campaignStatusId");
		
		String filterOutCampaign = request.getParameter("filterOutCampaign"); // remove this from list
		String isProcessed = request.getParameter("isProcessed");
		String start = request.getParameter("start");
		String length = request.getParameter("length");
		
		Map<String, Object> result = FastMap.newInstance();
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		try {
			List<EntityCondition> conditionList = new ArrayList<EntityCondition>();
			if (UtilValidate.isNotEmpty(campaignStatusId)) {
				conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, campaignStatusId));
			}
			if (UtilValidate.isNotEmpty(campaignNameToFind)) {
				EntityCondition listIdCond = EntityCondition.makeCondition(UtilMisc.toList(
						EntityCondition.makeCondition("marketingCampaignId", EntityOperator.EQUALS, campaignNameToFind),
						EntityCondition.makeCondition("campaignName", EntityOperator.LIKE,
								"%" + campaignNameToFind + "%")),
						EntityOperator.OR);
				conditionList.add(listIdCond);
			}
			if (UtilValidate.isNotEmpty(filterOutCampaign)) {
				conditionList.add(EntityCondition.makeCondition("marketingCampaignId", EntityOperator.NOT_IN, StringUtil.split(filterOutCampaign, ",")));
			}
			if (UtilValidate.isNotEmpty(isProcessed)) {
				conditionList.add(EntityCondition.makeCondition("isProcessed", EntityOperator.EQUALS, null));
			}
			
			EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			Debug.logInfo("findCampaignListsAjax mainConditons: "+mainConditons, MODULE);
			
			DynamicViewEntity dynamicView = new DynamicViewEntity();
			
			dynamicView.addMemberEntity("MC", "MarketingCampaign");
			dynamicView.addAlias("MC", "marketingCampaignId", "marketingCampaignId", null, Boolean.FALSE, Boolean.TRUE, null);
			dynamicView.addAlias("MC", "campaignName");
			dynamicView.addAlias("MC", "startDate");
			dynamicView.addAlias("MC", "endDate");
			dynamicView.addAlias("MC", "createdStamp");
			
			dynamicView.addMemberEntity("CFCCA", "CustomFieldCampaignConfigAssoc");
			dynamicView.addAlias("CFCCA", "isProcessed");
			dynamicView.addViewLink("MC", "CFCCA", Boolean.TRUE, ModelKeyMap.makeKeyMapList("marketingCampaignId"));
			
			EntityFindOptions efo = new EntityFindOptions();
			efo.setDistinct(true);
			efo.getDistinct();
			int startInx = UtilValidate.isNotEmpty(start) ? Integer.parseInt(start) : 0;
			int endInx = UtilValidate.isNotEmpty(length) ? Integer.parseInt(length) : 0;
			efo.setOffset(startInx);
			efo.setLimit(endInx);
			//efoNum.setFetchSize(1000);
			
			List<Map<String, Object>> dataList = new ArrayList<Map<String,Object>>();
			//List<GenericValue> marketingCampaignList = delegator.findList("MarketingCampaign", mainConditons, null, UtilMisc.toList("createdStamp DESC"), efo, false);
			List<GenericValue> marketingCampaignList = delegator.findListIteratorByCondition(dynamicView, mainConditons, null, null, UtilMisc.toList("createdStamp DESC"), efo).getCompleteList();
			
			if (marketingCampaignList != null && marketingCampaignList.size() > 0) {
				for (GenericValue marketingCampaign : marketingCampaignList) {
						Map<String, Object> dataMap = new HashMap<String, Object>();
						String mktCampaignId = marketingCampaign.getString("marketingCampaignId");
						String campaignNameVal = marketingCampaign.getString("campaignName");
						dataMap.put("campaignId", mktCampaignId);
						dataMap.put("campaignName", campaignNameVal);
						dataMap.put("startDate", UtilValidate.isNotEmpty(marketingCampaign.getTimestamp("startDate")) ? df.format(marketingCampaign.getTimestamp("startDate")) : "");
						dataMap.put("endDate", UtilValidate.isNotEmpty(marketingCampaign.getTimestamp("endDate")) ? df.format(marketingCampaign.getTimestamp("endDate")) : "");
						dataList.add(dataMap);

				}
				result.put("data", dataList);
				result.put("responseMessage", "success");
    			result.put("successMessage", "Data Loaded successfully.");
			} else {
				result.put("errorMessage", "Records not found");
				result.put("responseMessage", "error");
				result.put("data", new ArrayList<Map<String, Object>>());
			}
		} catch (Exception e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);

			result.put("errorMessage", e.getMessage());
			result.put("responseMessage", "error");
			result.put("data", new ArrayList<Map<String, Object>>());
		}
		return AjaxEvents.doJSONResponse(response, result);
	}
    
    public static String storeCustomFieldCheckedValue(HttpServletRequest request, HttpServletResponse response) {
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
		String domainEntityType = request.getParameter("domainEntityType");
		String domainEntityId = request.getParameter("domainEntityId");
		String partyId = request.getParameter("partyId");
		String attrChecklist = request.getParameter("attrChecklist");
		
		Map<String, Object> result = FastMap.newInstance();
		Map<String, Object> callCtxt = FastMap.newInstance();
		Map<String, Object> callResult = FastMap.newInstance();
		
		try {
			if (UtilValidate.isNotEmpty(domainEntityType) && UtilValidate.isNotEmpty(domainEntityId) && UtilValidate.isNotEmpty(partyId) && UtilValidate.isNotEmpty(attrChecklist)) {
				attrChecklist = Base64.base64Decode(attrChecklist);
				List<Map> groupDataList = ParamUtil.jsonToList(attrChecklist);
				GenericValue attrEntityAssoc = EntityUtil.getFirst( delegator.findByAnd("AttrEntityAssoc", UtilMisc.toMap("domainEntityType", domainEntityType), null, false) );
				
				for (Map groupData : groupDataList) {
					String groupId = (String) groupData.get("groupId");
					GenericValue group = EntityQuery.use(delegator).from("CustomFieldGroup").where("groupId", groupId).queryFirst();
					if (UtilValidate.isNotEmpty(group)) {
						List<Map<String, Object>> fields = (List<Map<String, Object>>) groupData.get("fields");
						if (UtilValidate.isNotEmpty(fields)) {
							for (Map field : fields) {
								String customFieldId = (String) field.get("customFieldId");
								String isChecked = (String) field.get("isChecked");
								if (UtilValidate.isNotEmpty(customFieldId)) {
									GenericValue customField = EntityQuery.use(delegator).from("CustomField").where("customFieldId", customFieldId).queryFirst();
									if (UtilValidate.isNotEmpty(customField)) {
										String serviceName = "customfield.createFieldValue";
										GenericValue fieldValue = org.groupfio.custom.field.util.DataUtil.getAttrFieldValue(delegator, UtilMisc.toMap("customFieldId", customFieldId, "partyId", partyId, "domainEntityType", domainEntityType, "domainEntityId", domainEntityId, "attrEntityAssoc", attrEntityAssoc));
										if (UtilValidate.isNotEmpty(fieldValue)) {
											serviceName = "customfield.updateFieldValue";
										}
										
										callCtxt = FastMap.newInstance();
										callCtxt.put("customFieldId", customFieldId);
										callCtxt.put("customFieldValue", isChecked);
										callCtxt.put("partyId", partyId);
										callCtxt.put("groupId", groupId);
										callCtxt.put("domainEntityType", domainEntityType);
										callCtxt.put("domainEntityId", domainEntityId);
										callCtxt.put("userLogin", userLogin);
										
										callResult = dispatcher.runSync(serviceName, callCtxt);
										if (ServiceUtil.isError(callResult)) {
											Debug.log(ServiceUtil.getErrorMessage(callResult), MODULE);
										}
									}
								}
							}
						}
					}
				}
				
			}
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, MODULE);
			
			result.put(GlobalConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			result.put(GlobalConstants.RESPONSE_MESSAGE, e.getMessage());
			return AjaxEvents.doJSONResponse(response, result);
		}
		return AjaxEvents.doJSONResponse(response, result);
	}

	public static String segmentMappingList(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> dataList = FastList.newInstance();
		List<GenericValue> mappedSegments = null;
		try {
			mappedSegments = EntityQuery.use(delegator).from("SegmentMapping").queryList();
			for (GenericValue mappedSegment : mappedSegments) {
				Map<String, Object> data = new HashMap<String, Object>();
				data.put("dwFieldId", mappedSegment.getString("dwFieldId"));
				data.put("dwFieldTable", mappedSegment.getString("dwFieldTable"));
				data.put("segmentId", mappedSegment.getString("segmentId"));
				data.put("dwTypeId", mappedSegment.getString("dwTypeId"));
				dataList.add(data);
			}
			result.put("data", dataList);
		} catch (Exception e) {
			Debug.logError(e.getMessage(), MODULE);
			result.put("data", new ArrayList<Map<String, Object>>());
		}
		return doJSONResponse(response, result);
	}
	/**
	 * 
	 * @author Nirmal Kumar P
	 * @since 08-05-2023
	 * @param request
	 * @param response
	 * @return 
	 */
	public static String getSegmentFieldDropdownValues(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		List<String> fieldList = new ArrayList<>();
		Map<String, Object> results = new HashMap<>();
		String masterTable = (String) request.getParameter("masterTable");
		
		try {
			String segmentFieldConfig = null;
			//This code follows the similar format in global parameter to add more SegmentFields --> Example: abc=1-10,xyz=1-20 ;
			segmentFieldConfig = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator,masterTable+"_FLD_RNG");
			if (UtilValidate.isNotEmpty(segmentFieldConfig)) {
				if (segmentFieldConfig.contains("-") && segmentFieldConfig.contains("=")) {
					String[] segmentFieldList = segmentFieldConfig.split(",");
					for(String segmentFieldPair : segmentFieldList) {
						String[] segmentField = segmentFieldPair.split("=");
						if (segmentField.length == 2) {
							String segmentFieldName = segmentField[0];
							String segmentFieldValueRange = segmentField[1];
							if (segmentFieldValueRange.contains("-")) {
								String[] segmentFieldConfigValues = segmentFieldValueRange.split("-");
								if (segmentFieldConfigValues.length == 2) {
									String initValue = segmentFieldConfigValues[0];
									String lastValue = segmentFieldConfigValues[1];
									if (UtilValidate.isNotEmpty(initValue))
										initValue = initValue.trim();
									if (UtilValidate.isNotEmpty(lastValue))
										lastValue = lastValue.trim();
									if (UtilValidate.isNotEmpty(initValue) && UtilValidate.isNotEmpty(lastValue)) {
										Long initValueLong = Long.valueOf(initValue);
										Long lastValueLong = Long.valueOf(lastValue);
										if (lastValueLong > initValueLong) {
											for (; initValueLong <= lastValueLong; initValueLong++) {
												fieldList.add(segmentFieldName + initValueLong.toString());
											}
										}
									}
								}
							}
						}
					}
				}
			}
			fieldList.removeAll(EntityUtil.getFieldListFromEntityList(EntityQuery.use(delegator).from("SegmentMapping").select("dwFieldId")
					.where("dwFieldTable", masterTable).queryList(), "dwFieldId", true));
			results.put("responseMessage", "success");
			results.put("list", fieldList);
		} catch (Exception e) {
			Debug.logError(e.getMessage(), MODULE);
			results.put("errorMessage", e.getMessage());
			results.put("responseMessage", "error");
			results.put("list", new ArrayList<Map<String, Object>>());
		}
		return doJSONResponse(response, results);
	}
}
