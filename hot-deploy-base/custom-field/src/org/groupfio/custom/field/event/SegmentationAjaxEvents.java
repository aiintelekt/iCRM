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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONNull;
import net.sf.json.JSONObject;

import org.groupfio.custom.field.ResponseCodes;
import org.groupfio.custom.field.constants.CustomFieldConstants;
import org.groupfio.custom.field.constants.CustomFieldConstants.GroupType;
import org.groupfio.custom.field.util.DataHelper;
import org.groupfio.custom.field.util.DataUtil;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.LocalDispatcher;

import javolution.util.FastList;

/**
 * Utility class for making Ajax JSON responses.
 * @author Sharif Ul Islam
 */
public final class SegmentationAjaxEvents {

    private SegmentationAjaxEvents() { }

    private static final String MODULE = SegmentationAjaxEvents.class.getName();
  
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
    
    public static GenericValue getUserLogin(HttpServletRequest request) {
        HttpSession session = request.getSession();
        return (GenericValue) session.getAttribute("userLogin");
    }

    /*************************************************************************/
    /**                                                                     **/
    /**                      Common JSON Requests                           **/
    /**                                                                     **/
    /*************************************************************************/
    
    @SuppressWarnings("unchecked")
	public static String addSelectedSegmentCustomer(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {

		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");

		Locale locale = UtilHttp.getLocale(request);
		HttpSession session = request.getSession(true);

		String groupId = request.getParameter("groupId");
		String customFieldId = request.getParameter("customFieldId");
		String rowsSelected[] = request.getParameterValues("rowsSelected[]");

		Map<String, Object> resp = new HashMap<String, Object>();

		int successCount = 0;
		int alreadyExistsCount = 0;
		
		try {

			if (UtilValidate.isNotEmpty(groupId)) {
				
				GenericValue customFieldGroup = EntityUtil.getFirst( delegator.findByAnd("CustomFieldGroup", UtilMisc.toMap("groupId", groupId), null, false) );
				GenericValue customField = EntityUtil.getFirst( delegator.findByAnd("CustomField", UtilMisc.toMap("customFieldId", customFieldId), null, false) );
				
				if (UtilValidate.isEmpty(customFieldGroup) || UtilValidate.isEmpty(customField)) {
					resp.put(CustomFieldConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
		            resp.put(CustomFieldConstants.RESPONSE_MESSAGE, "Invalid segment code / value..");
				} else {
					
					if (UtilValidate.isNotEmpty(rowsSelected)) {
						
						for (int i = 0; i < rowsSelected.length; i++) {
							String partyId = rowsSelected[i];
							String segmentationValueAssociatedEntityName = DataUtil.getSegmentationValueAssociatedEntityName(delegator, groupId);
							GenericValue associatedEntity = EntityUtil.getFirst( delegator.findByAnd(segmentationValueAssociatedEntityName, UtilMisc.toMap("groupId", groupId, "customFieldId", customFieldId, "partyId", partyId), null, false) );
							
							if (UtilValidate.isEmpty(associatedEntity)) {
								
								associatedEntity = delegator.makeValue(segmentationValueAssociatedEntityName);
								
								associatedEntity.put("groupId", groupId);
								associatedEntity.put("customFieldId", customFieldId);
								associatedEntity.put("partyId", partyId);
								
								if (customFieldGroup.getString("groupType").equals(GroupType.ECONOMIC_METRIC)) {
									associatedEntity.put("propertyName", groupId+"."+customFieldId);
									if (UtilValidate.isNotEmpty(customFieldGroup.getString("groupingCode"))) {
										GenericValue groupingCode = customFieldGroup.getRelatedOne("CustomFieldGroupingCode", false);
										if (UtilValidate.isNotEmpty(groupingCode)) {
											associatedEntity.put("groupingCode", groupingCode.getString("groupingCode"));
										}
									}
								}
								
								if (customFieldGroup.getString("groupType").equals(GroupType.SEGMENTATION)) {
									associatedEntity.put("inceptionDate", UtilDateTime.nowTimestamp());
								}
								
								associatedEntity.create();
								
								successCount++;
							} else {
								alreadyExistsCount++;
							}
							
						}
						
					}
					
				}
				
			}
			
			resp.put(CustomFieldConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
			
			resp.put("successCount", successCount);
			resp.put("alreadyExistsCount", alreadyExistsCount);
			
		} catch (Exception e) {
			
			resp.put(CustomFieldConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
            resp.put(CustomFieldConstants.RESPONSE_MESSAGE, "Error: "+e.getMessage());
			
			Debug.logError(e.getMessage(), MODULE);
		}
		
		return doJSONResponse(response, resp);
	}
    
    @SuppressWarnings("unchecked")
	public static String removeSelectedSegmentCustomer(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {

		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");

		Locale locale = UtilHttp.getLocale(request);
		HttpSession session = request.getSession(true);

		String groupId = request.getParameter("groupId");
		String customFieldId = request.getParameter("customFieldId");
		String rowsSelected[] = request.getParameterValues("rowsSelected[]");

		Map<String, Object> resp = new HashMap<String, Object>();

		int successCount = 0;
		
		try {

			if (UtilValidate.isNotEmpty(groupId)) {
				
				GenericValue customFieldGroup = EntityUtil.getFirst( delegator.findByAnd("CustomFieldGroup", UtilMisc.toMap("groupId", groupId), null, false) );
				GenericValue customField = EntityUtil.getFirst( delegator.findByAnd("CustomField", UtilMisc.toMap("customFieldId", customFieldId), null, false) );
				
				if (UtilValidate.isEmpty(customFieldGroup) || UtilValidate.isEmpty(customField)) {
					resp.put(CustomFieldConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
		            resp.put(CustomFieldConstants.RESPONSE_MESSAGE, "Invalid segment code / value..");
				} else {
					
					if (UtilValidate.isNotEmpty(rowsSelected)) {
						
						for (int i = 0; i < rowsSelected.length; i++) {
							String partyId = rowsSelected[i];
							String segmentationValueAssociatedEntityName = DataUtil.getSegmentationValueAssociatedEntityName(delegator, groupId);
							GenericValue associatedEntity = EntityUtil.getFirst( delegator.findByAnd(segmentationValueAssociatedEntityName, UtilMisc.toMap("groupId", groupId, "customFieldId", customFieldId, "partyId", partyId), null, false) );
							
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
            resp.put(CustomFieldConstants.RESPONSE_MESSAGE, "Error: "+e.getMessage());
			
			Debug.logError(e.getMessage(), MODULE);
		}
		
		return doJSONResponse(response, resp);
	}
    
    @SuppressWarnings("unchecked")
	public static String activateSegmemntCode(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {

		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");

		Locale locale = UtilHttp.getLocale(request);
		HttpSession session = request.getSession(true);

		String groupId = request.getParameter("groupId");

		Map<String, Object> resp = new HashMap<String, Object>();

		JSONArray datas = new JSONArray();
		
		try {

			if (UtilValidate.isNotEmpty(groupId)) {
				GenericValue customFieldGroup = EntityUtil.getFirst( delegator.findByAnd("CustomFieldGroup", UtilMisc.toMap("groupId", groupId), null, false) );
				if (UtilValidate.isNotEmpty(customFieldGroup)) {
					customFieldGroup.put("isActive", "Y");
					
					customFieldGroup.store();
					
					resp.put(CustomFieldConstants.RESPONSE_MESSAGE, "Successfully Activated Segment Code!");
					resp.put(CustomFieldConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
				}
			}
			
		} catch (Exception e) {
			resp.put(CustomFieldConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
            resp.put(CustomFieldConstants.RESPONSE_MESSAGE, "Error: "+e.getMessage());
			
			Debug.logError(e.getMessage(), MODULE);
		}
		
		resp.put("data", datas);

		return doJSONResponse(response, resp);
	}
    
    @SuppressWarnings("unchecked")
	public static String inActivateSegmemntCode(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {

		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");

		Locale locale = UtilHttp.getLocale(request);
		HttpSession session = request.getSession(true);

		String groupId = request.getParameter("groupId");

		Map<String, Object> resp = new HashMap<String, Object>();

		JSONArray datas = new JSONArray();
		
		try {

			if (UtilValidate.isNotEmpty(groupId)) {
				GenericValue customFieldGroup = EntityUtil.getFirst( delegator.findByAnd("CustomFieldGroup", UtilMisc.toMap("groupId", groupId), null, false) );
				if (UtilValidate.isNotEmpty(customFieldGroup)) {
					customFieldGroup.put("isActive", "N");
					
					customFieldGroup.store();
					
					resp.put(CustomFieldConstants.RESPONSE_MESSAGE, "Successfully InActivated Segment Code!");
					resp.put(CustomFieldConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
				}
			}
			
		} catch (Exception e) {
			resp.put(CustomFieldConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
            resp.put(CustomFieldConstants.RESPONSE_MESSAGE, "Error: "+e.getMessage());
			
			Debug.logError(e.getMessage(), MODULE);
		}
		
		resp.put("data", datas);

		return doJSONResponse(response, resp);
	}
    
    @SuppressWarnings("unchecked")
	public static String loadSegmentCode(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {

		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");

		Locale locale = UtilHttp.getLocale(request);
		HttpSession session = request.getSession(true);

		String groupId = request.getParameter("groupId");

		Map<String, Object> resp = new HashMap<String, Object>();

		JSONArray datas = new JSONArray();
		
		try {

			if (UtilValidate.isNotEmpty(groupId)) {
				GenericValue customFieldGroup = EntityUtil.getFirst( delegator.findByAnd("CustomFieldGroup", UtilMisc.toMap("groupId", groupId), null, false) );
				if (UtilValidate.isNotEmpty(customFieldGroup)) {
					
					resp.put("segmentCode", customFieldGroup);
					
					resp.put(CustomFieldConstants.RESPONSE_MESSAGE, "Successfully Activated Segment Code!");
					resp.put(CustomFieldConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
				}
			}
			
		} catch (Exception e) {
			resp.put(CustomFieldConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
            resp.put(CustomFieldConstants.RESPONSE_MESSAGE, "Error: "+e.getMessage());
			
			Debug.logError(e.getMessage(), MODULE);
		}
		
		resp.put("data", datas);

		return doJSONResponse(response, resp);
	}
    
    @SuppressWarnings("unchecked")
	public static String enabledSegmemntCodeValue(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {

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
				GenericValue customField = EntityUtil.getFirst( delegator.findByAnd("CustomField", UtilMisc.toMap("customFieldId", customFieldId), null, false) );
				if (UtilValidate.isNotEmpty(customField)) {
					customField.put("isEnabled", "Y");
					
					customField.store();
					
					resp.put(CustomFieldConstants.RESPONSE_MESSAGE, "Successfully Enabled Segment Value!");
					resp.put(CustomFieldConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
				}
			}
			
		} catch (Exception e) {
			resp.put(CustomFieldConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
            resp.put(CustomFieldConstants.RESPONSE_MESSAGE, "Error: "+e.getMessage());
			
			Debug.logError(e.getMessage(), MODULE);
		}
		
		resp.put("data", datas);

		return doJSONResponse(response, resp);
	}
    
    @SuppressWarnings("unchecked")
	public static String disabledSegmemntCodeValue(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {

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
				GenericValue customField = EntityUtil.getFirst( delegator.findByAnd("CustomField", UtilMisc.toMap("customFieldId", customFieldId), null, false) );
				if (UtilValidate.isNotEmpty(customField)) {
					customField.put("isEnabled", "N");
					
					customField.store();
					
					resp.put(CustomFieldConstants.RESPONSE_MESSAGE, "Successfully Disabled Segment Value!");
					resp.put(CustomFieldConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
				}
			}
			
		} catch (Exception e) {
			resp.put(CustomFieldConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
            resp.put(CustomFieldConstants.RESPONSE_MESSAGE, "Error: "+e.getMessage());
			
			Debug.logError(e.getMessage(), MODULE);
		}
		
		resp.put("data", datas);

		return doJSONResponse(response, resp);
	}
    
    //////////////////////////////////////////////////////////////////////////////////////////////
    
    @SuppressWarnings("unchecked")
	public static String getSegmentValueMultiValues(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {

		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");

		Locale locale = UtilHttp.getLocale(request);
		HttpSession session = request.getSession(true);

		String groupId = request.getParameter("groupId");
		String customFieldId = request.getParameter("customFieldId");

		Map<String, Object> resp = new HashMap<String, Object>();

		JSONArray datas = new JSONArray();
		
		try {

			if (UtilValidate.isNotEmpty(customFieldId)) {
				
				List<GenericValue> multiValues = delegator.findByAnd("CustomFieldValueConfig", UtilMisc.toMap("groupId", groupId, "customFieldId", customFieldId, "valueCapture", "MULTIPLE"), UtilMisc.toList("valueSeqNum"), false);
				
				if (UtilValidate.isNotEmpty(multiValues)) {
					for (GenericValue multiValue : multiValues) {
						
						JSONObject data = new JSONObject();
						
						data.put("customFieldId", customFieldId);
						data.put("groupId", multiValue.getString("groupId"));
						data.put("valueCapture", multiValue.getString("valueCapture"));
						data.put("fieldValue", UtilValidate.isNotEmpty(multiValue.getString("valueData")) ? multiValue.getString("valueData") : JSONNull.getInstance());
						data.put("description", UtilValidate.isNotEmpty(multiValue.getString("description")) ? multiValue.getString("description") : JSONNull.getInstance());
						data.put("hide", UtilValidate.isNotEmpty(multiValue.getString("hide")) ? multiValue.getString("hide") : JSONNull.getInstance());
						data.put("sequenceNumber", UtilValidate.isEmpty(multiValue.get("valueSeqNum")) ? 1 : multiValue.get("valueSeqNum"));
						
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
	public static String createSegmentValueMultiValue(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {

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
				
				EntityCondition conditions = EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("customFieldId", EntityOperator.EQUALS, customFieldId),
						EntityCondition.makeCondition(EntityOperator.OR,
								EntityCondition.makeCondition("valueData", EntityOperator.EQUALS, fieldValue),
								EntityCondition.makeCondition("valueSeqNum", EntityOperator.EQUALS, Long.parseLong(sequenceNumber))
								)
						);
				
				GenericValue multiValue = EntityUtil.getFirst( delegator.findList("CustomFieldValueConfig", conditions, null, null, null, false) );
				
				if (UtilValidate.isNotEmpty(multiValue)) {
					resp.put(CustomFieldConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
		            resp.put(CustomFieldConstants.RESPONSE_MESSAGE, "Already created multi value..");
				} else {
					
					GenericValue segmentValue = EntityUtil.getFirst( delegator.findByAnd("CustomField", UtilMisc.toMap("customFieldId", customFieldId), null, false) );
					String groupId = segmentValue.getString("groupId");
					GenericValue segmentCode = EntityUtil.getFirst( delegator.findByAnd("CustomFieldGroup", UtilMisc.toMap("groupId", groupId), null, false) );
					String valueCapture = segmentCode.getString("valueCapture");
					
					multiValue = delegator.makeValue("CustomFieldValueConfig");
					
					multiValue.put("groupId", groupId);
					multiValue.put("customFieldId", customFieldId);
					multiValue.put("valueCapture", valueCapture);
					multiValue.put("valueSeqNum", UtilValidate.isNotEmpty(sequenceNumber) ? Long.parseLong(sequenceNumber) : new Long(1));
					multiValue.put("valueData", fieldValue);
					multiValue.put("description", description);
					multiValue.put("hide", UtilValidate.isNotEmpty(hide) ? hide : "N");
					
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
	public static String removeSegmentValueMultiValue(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {

		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");

		Locale locale = UtilHttp.getLocale(request);
		HttpSession session = request.getSession(true);

		String customFieldId = request.getParameter("customFieldId");
		//String groupId = request.getParameter("groupId");
		//String valueCapture = request.getParameter("valueCapture");
		String valueSeqNum = request.getParameter("valueSeqNum");

		Map<String, Object> resp = new HashMap<String, Object>();

		try {

			if (UtilValidate.isNotEmpty(customFieldId) && UtilValidate.isNotEmpty(valueSeqNum)) {
				
				GenericValue segmentValue = EntityUtil.getFirst( delegator.findByAnd("CustomField", UtilMisc.toMap("customFieldId", customFieldId), null, false) );
				String groupId = segmentValue.getString("groupId");
				GenericValue segmentCode = EntityUtil.getFirst( delegator.findByAnd("CustomFieldGroup", UtilMisc.toMap("groupId", groupId), null, false) );
				String valueCapture = segmentCode.getString("valueCapture");
				
				GenericValue multiValue = EntityUtil.getFirst( delegator.findByAnd("CustomFieldValueConfig", UtilMisc.toMap("customFieldId", customFieldId, "groupId", groupId, "valueSeqNum", Long.parseLong(valueSeqNum)), null, false) );
				
				if (UtilValidate.isNotEmpty(multiValue)) {
					
					multiValue.remove();
					
					resp.put(CustomFieldConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
		            resp.put(CustomFieldConstants.RESPONSE_MESSAGE, "Successfully removed multi value..");
					
				} else {
					resp.put(CustomFieldConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
		            resp.put(CustomFieldConstants.RESPONSE_MESSAGE, "Not found multi value..");
				}
				
			}
			
		} catch (Exception e) {
			
			resp.put(CustomFieldConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
            resp.put(CustomFieldConstants.RESPONSE_MESSAGE, "Error: "+e.getMessage());
			
			Debug.logError(e.getMessage(), MODULE);
		}
		
		return doJSONResponse(response, resp);
	}
    
    @SuppressWarnings("unchecked")
	public static String removeSelectedSegmentValueMultiValues(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {

		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");

		Locale locale = UtilHttp.getLocale(request);
		HttpSession session = request.getSession(true);

		String customFieldId = request.getParameter("customFieldId");
		String groupId = request.getParameter("groupId");
		String rowsSelected[] = request.getParameterValues("rowsSelected[]");
		
		Map<String, Object> resp = new HashMap<String, Object>();

		int successCount = 0;
		
		try {

			if (UtilValidate.isNotEmpty(customFieldId)) {
				
				GenericValue customField = EntityUtil.getFirst( delegator.findByAnd("CustomField", UtilMisc.toMap("customFieldId", customFieldId), null, false) );
				GenericValue segmentCode = EntityUtil.getFirst( delegator.findByAnd("CustomFieldGroup", UtilMisc.toMap("groupId", groupId), null, false) );
				String valueCapture = segmentCode.getString("valueCapture");
				
				if (UtilValidate.isEmpty(customField)) {
					resp.put(CustomFieldConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
		            resp.put(CustomFieldConstants.RESPONSE_MESSAGE, "Invalid segment value..");
				} else {
					
					if (UtilValidate.isNotEmpty(rowsSelected)) {
						
						for (int i = 0; i < rowsSelected.length; i++) {
							String sequenceNumber = rowsSelected[i];
							
							GenericValue associatedEntity = EntityUtil.getFirst( delegator.findByAnd("CustomFieldValueConfig", UtilMisc.toMap("customFieldId", customFieldId, "groupId", groupId, "valueSeqNum", Long.parseLong(sequenceNumber)), null, false) );
							
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
            resp.put(CustomFieldConstants.RESPONSE_MESSAGE, "Error: "+e.getMessage());
			
			Debug.logError(e.getMessage(), MODULE);
		}
		
		return doJSONResponse(response, resp);
	}
    
    @SuppressWarnings("unchecked")
    public static String updateBatchDateCampaign(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
        GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
        
        Locale locale = UtilHttp.getLocale(request);
        TimeZone timeZone = UtilHttp.getTimeZone(request);
        HttpSession session = request.getSession(true);
        
        String globalDateFormat = org.fio.homeapps.util.DataHelper.getGlobalDateFormat(delegator);
		String globalDateTimeFormat = org.fio.homeapps.util.DataHelper.getGlobalDateTimeFormat(delegator);
		SimpleDateFormat df1 = new SimpleDateFormat(globalDateTimeFormat);
		SimpleDateFormat df2 = new SimpleDateFormat(globalDateFormat);
		
        String groupId = request.getParameter("groupId");
        String customFieldId = request.getParameter("customFieldId");
        String marketingCampaignId = null;
        marketingCampaignId = request.getParameter("marketingCampaignId");
        String isCouponSegment = request.getParameter("isCouponSegmentDateValue");

        Map<String, Object> resp = new HashMap<String, Object>();
        try {
        	
        	GenericValue campaign = null;
        	try {
					campaign = EntityUtil.getFirst( delegator.findByAnd("MarketingCampaign", UtilMisc.toMap("marketingCampaignId", marketingCampaignId), null, false) );

					if (UtilValidate.isNotEmpty(campaign)) {
						
						List<GenericValue> campaignConfigAssocList = delegator.findByAnd("CustomFieldCampaignConfigAssoc", UtilMisc.toMap("customFieldId", customFieldId), UtilMisc.toList("sequenceNumber"), false);
						for (GenericValue campaignConfigAssoc : campaignConfigAssocList) {
							GenericValue marketingCampaign = campaignConfigAssoc.getRelatedOne("MarketingCampaign", false);
							if (UtilValidate.isNotEmpty(marketingCampaign)) {
								marketingCampaign.put("parentCampaignId", null);
								marketingCampaign.store();

							}
						}
						
					}
				
			} catch (Exception e) {
				Debug.log("not found as campaign: "+ e.getMessage());
			}
        	
        	Enumeration params = request.getParameterNames();
		
        	String specDateCampaign[] = request.getParameterValues("specDateCampaign");
        	String specDateCampaignSelected[] = request.getParameterValues("specDateCampaignSelected");
        	if (UtilValidate.isNotEmpty(specDateCampaign)) {    			
    			   			
        		GenericValue campaignConfig = EntityUtil.getFirst( delegator.findByAnd("CustomFieldCampaignConfig", UtilMisc.toMap("groupId", groupId, "customFieldId", customFieldId), null, false) );

        		if (UtilValidate.isEmpty(campaignConfig)) {
        			campaignConfig = delegator.makeValue("CustomFieldCampaignConfig", UtilMisc.toMap("groupId", groupId, "customFieldId", customFieldId));
        			
        			campaignConfig.put("configType", "BATCH");
        			campaignConfig.put("configBatchType", "SPEC_DATE");
        			campaignConfig.put("isCouponSegment", isCouponSegment);
        			
        			campaignConfig.create();

        		}  else {
        			campaignConfig.put("configType", "BATCH");
        			campaignConfig.put("configBatchType", "SPEC_DATE");
        			campaignConfig.put("isCouponSegment", isCouponSegment);
        			
        			campaignConfig.store();

        		}
        		
        		//delegator.removeByAnd("CustomFieldCampaignConfigAssoc", UtilMisc.toMap("customFieldId", customFieldId));
        		/** If processed do not remove 
        		 * And do not update if processed
        		 * **/
        		List conditionList = FastList.newInstance();
    			conditionList.add(EntityCondition.makeCondition("customFieldId", EntityOperator.EQUALS, customFieldId));
    			//conditionList.add(EntityCondition.makeCondition("isProcessed", EntityOperator.NOT_EQUAL, "Y"));
    			conditionList.add(EntityCondition.makeCondition(EntityOperator.OR,
						EntityCondition.makeCondition("isProcessed", EntityOperator.EQUALS, null),
						EntityCondition.makeCondition("isProcessed", EntityOperator.NOT_EQUAL, "Y")));
    			delegator.removeByCondition("CustomFieldCampaignConfigAssoc", EntityCondition.makeCondition(conditionList, EntityOperator.AND));
    			
        		Long sequenceNumber = new Long(1);
        		for (int i = 0; i < specDateCampaign.length; i++) {
        		
        			String selectedDate = df1.format(df2.parse(specDateCampaign[i]));      			
        			String selectedCampaign = specDateCampaignSelected[i];
        			
        			if (UtilValidate.isNotEmpty(selectedDate) && UtilValidate.isNotEmpty(selectedCampaign)) {
        				
        				List<GenericValue> assocCampainConfigList = delegator.findByAnd("CustomFieldCampaignConfigAssoc", UtilMisc.toMap("customFieldId", customFieldId,
        						"marketingCampaignId",selectedCampaign,"isProcessed","Y"), null, false);
						
        				if (UtilValidate.isEmpty(assocCampainConfigList)) {
	        				
        			        GenericValue campaignConfigAssoc = delegator.makeValue("CustomFieldCampaignConfigAssoc", UtilMisc.toMap("customFieldId", customFieldId, "sequenceNumber", sequenceNumber));

        			           campaignConfigAssoc.put("specificDate", UtilDateTime.stringToTimeStamp(selectedDate,globalDateTimeFormat,timeZone, locale));
        				        				campaignConfigAssoc.put("marketingCampaignId", selectedCampaign);
        				        				
        				       campaignConfigAssoc.create();
        				
        									}

        				sequenceNumber++;
        				if (UtilValidate.isNotEmpty(campaign)) {
        					
        					GenericValue childCampaign = EntityUtil.getFirst( delegator.findByAnd("MarketingCampaign", UtilMisc.toMap("marketingCampaignId", selectedCampaign), null, false) );
        					if (UtilValidate.isNotEmpty(childCampaign)) {
        						childCampaign.put("parentCampaignId", marketingCampaignId);
        						childCampaign.store();
        					}
        					
        				}
        				
        			}
        			
        		}
        		
        	}
        	
        	resp.put(CustomFieldConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
        	resp.put(CustomFieldConstants.RESPONSE_MESSAGE, "Successfully updated batch specific calendar date campaigns!!");
        	
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
    public static String updateBatchDaysCampaign(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {

    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
        GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
        
        Locale locale = UtilHttp.getLocale(request);
        TimeZone timeZone = UtilHttp.getTimeZone(request);
        HttpSession session = request.getSession(true);
        
        String groupId = request.getParameter("groupId");
        String customFieldId = request.getParameter("customFieldId");
        String isCouponSegment = request.getParameter("isCouponSegmentDaysValue");
        String marketingCampaignId = null;
         marketingCampaignId = request.getParameter("marketingCampaignId");
        Map<String, Object> resp = new HashMap<String, Object>();
        try {
        	
        	GenericValue campaign = null;
        	try {
				
					campaign = EntityUtil.getFirst( delegator.findByAnd("MarketingCampaign", UtilMisc.toMap("marketingCampaignId", marketingCampaignId), null, false) );

					if (UtilValidate.isNotEmpty(campaign)) {
						
						List<GenericValue> campaignConfigAssocList = delegator.findByAnd("CustomFieldCampaignConfigAssoc", UtilMisc.toMap("customFieldId", customFieldId), UtilMisc.toList("sequenceNumber"), false);
						for (GenericValue campaignConfigAssoc : campaignConfigAssocList) {
							GenericValue marketingCampaign = campaignConfigAssoc.getRelatedOne("MarketingCampaign", false);
							if (UtilValidate.isNotEmpty(marketingCampaign)) {
								marketingCampaign.put("parentCampaignId", null);
								marketingCampaign.store();

							}
						}
					}
				
			} catch (Exception e) {
				Debug.log("not found as campaign: "+ e.getMessage());
			}
        	
        	Enumeration params = request.getParameterNames();
        	
        	String daySinceCampaign[] = request.getParameterValues("daySinceCampaign");
        	String specDateCampaignSelected[] = request.getParameterValues("specDateCampaignSelected");

        	if (UtilValidate.isNotEmpty(daySinceCampaign)) {
        		
        		GenericValue campaignConfig = EntityUtil.getFirst( delegator.findByAnd("CustomFieldCampaignConfig", UtilMisc.toMap("groupId", groupId, "customFieldId", customFieldId), null, false) );
        		if (UtilValidate.isEmpty(campaignConfig)) {
        			campaignConfig = delegator.makeValue("CustomFieldCampaignConfig", UtilMisc.toMap("groupId", groupId, "customFieldId", customFieldId));
        			
        			campaignConfig.put("configType", "BATCH");
        			campaignConfig.put("configBatchType", "DAY_SINCE");
        			campaignConfig.put("isCouponSegment", isCouponSegment);

        			campaignConfig.create();

        		} else {
        			campaignConfig.put("configType", "BATCH");
        			campaignConfig.put("configBatchType", "DAY_SINCE");
        			campaignConfig.put("isCouponSegment", isCouponSegment);

        			campaignConfig.store();

        		}
        		
        	//	delegator.removeByAnd("CustomFieldCampaignConfigAssoc", UtilMisc.toMap("customFieldId", customFieldId));
        		/** If processed do not remove 
        		 * And do not update if processed
        		 * **/
        		List conditionList = FastList.newInstance();
    			conditionList.add(EntityCondition.makeCondition("customFieldId", EntityOperator.EQUALS, customFieldId));
    			//conditionList.add(EntityCondition.makeCondition("isProcessed", EntityOperator.NOT_EQUAL, "Y"));
    			conditionList.add(EntityCondition.makeCondition(EntityOperator.OR,
						EntityCondition.makeCondition("isProcessed", EntityOperator.EQUALS, null),
						EntityCondition.makeCondition("isProcessed", EntityOperator.NOT_EQUAL, "Y")));
    			delegator.removeByCondition("CustomFieldCampaignConfigAssoc", EntityCondition.makeCondition(conditionList, EntityOperator.AND));
    			
        		Long sequenceNumber = new Long(1);
        		for (int i = 0; i < daySinceCampaign.length; i++) {

        			String selectedDaySince = daySinceCampaign[i];
        			String selectedCampaign = specDateCampaignSelected[i];

        			if (UtilValidate.isNotEmpty(selectedDaySince) && UtilValidate.isNotEmpty(selectedCampaign)) {
        				List<GenericValue> assocCampainConfigList = delegator.findByAnd("CustomFieldCampaignConfigAssoc", UtilMisc.toMap("customFieldId", customFieldId,
        						"marketingCampaignId",selectedCampaign,"isProcessed","Y"), null, false);
        				if (UtilValidate.isEmpty(assocCampainConfigList)) {
	        				GenericValue campaignConfigAssoc = delegator.makeValue("CustomFieldCampaignConfigAssoc", UtilMisc.toMap("customFieldId", customFieldId, "sequenceNumber", sequenceNumber));
	
	        				campaignConfigAssoc.put("daySince", new Long(selectedDaySince));
	        				campaignConfigAssoc.put("marketingCampaignId", selectedCampaign);
	        				
	        				campaignConfigAssoc.create();
        				}
        				sequenceNumber++;
        				
        				if (UtilValidate.isNotEmpty(campaign)) {
        					
        					GenericValue childCampaign = EntityUtil.getFirst( delegator.findByAnd("MarketingCampaign", UtilMisc.toMap("marketingCampaignId", selectedCampaign), null, false) );
        					if (UtilValidate.isNotEmpty(childCampaign)) {
        						childCampaign.put("parentCampaignId", marketingCampaignId);
        						childCampaign.store();

        					}
        					
        				}
        				
        			}
        			
        		}
        		
        	}
        	
        	resp.put(CustomFieldConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
        	resp.put(CustomFieldConstants.RESPONSE_MESSAGE, "Successfully updated batch specific days since start campaigns!!");
        	
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
    public static String updateTriggerCampaign(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
        GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
        
        Locale locale = UtilHttp.getLocale(request);
        TimeZone timeZone = UtilHttp.getTimeZone(request);
        HttpSession session = request.getSession(true);
        
        String groupId = request.getParameter("groupId");
        String customFieldId = request.getParameter("customFieldId");
        String isCouponSegment = request.getParameter("isCouponSegmentDateValue");

        String marketingCampaignId = null;
         marketingCampaignId = request.getParameter("marketingCampaignId");


        Map<String, Object> resp = new HashMap<String, Object>();
        try {
        	
        	GenericValue campaign = null;
        	try {
				
					campaign = EntityUtil.getFirst( delegator.findByAnd("MarketingCampaign", UtilMisc.toMap("marketingCampaignId", marketingCampaignId), null, false) );

					if (UtilValidate.isNotEmpty(campaign)) {
						
						List<GenericValue> campaignConfigAssocList = delegator.findByAnd("CustomFieldCampaignConfigAssoc", UtilMisc.toMap("customFieldId", customFieldId), UtilMisc.toList("sequenceNumber"), false);
						for (GenericValue campaignConfigAssoc : campaignConfigAssocList) {
							GenericValue marketingCampaign = campaignConfigAssoc.getRelatedOne("MarketingCampaign", false);
							if (UtilValidate.isNotEmpty(marketingCampaign)) {
								marketingCampaign.put("parentCampaignId", null);
								marketingCampaign.store();

							}
						}
					}
			
			} catch (Exception e) {
				Debug.log("not found as campaign: "+ e.getMessage());
			}
        	
        	Enumeration params = request.getParameterNames();
        	
        	String triggerUrl = request.getParameter("triggerUrl");
        	String selectedCampaign = request.getParameter("specDateCampaignSelected");

        	if (UtilValidate.isNotEmpty(triggerUrl)) {

        		GenericValue campaignConfig = EntityUtil.getFirst( delegator.findByAnd("CustomFieldCampaignConfig", UtilMisc.toMap("groupId", groupId, "customFieldId", customFieldId), null, false) );

        		if (UtilValidate.isEmpty(campaignConfig)) {
        			campaignConfig = delegator.makeValue("CustomFieldCampaignConfig", UtilMisc.toMap("groupId", groupId, "customFieldId", customFieldId));
        			
        			campaignConfig.put("configType", "TRIGGER");
        			campaignConfig.put("isCouponSegment", isCouponSegment);

        			campaignConfig.create();

        		} else {
        			campaignConfig.put("configType", "TRIGGER");
        			campaignConfig.put("configBatchType", null);
        			campaignConfig.put("isCouponSegment", isCouponSegment);

        			campaignConfig.store();

        		}
        		
        		delegator.removeByAnd("CustomFieldCampaignConfigAssoc", UtilMisc.toMap("customFieldId", customFieldId));
        		
        		Long sequenceNumber = new Long(1);
        		
        		GenericValue campaignConfigAssoc = delegator.makeValue("CustomFieldCampaignConfigAssoc", UtilMisc.toMap("customFieldId", customFieldId, "sequenceNumber", sequenceNumber));

				campaignConfigAssoc.put("triggerUrl", triggerUrl);
				campaignConfigAssoc.put("marketingCampaignId", selectedCampaign);
				
				campaignConfigAssoc.create();

				if (UtilValidate.isNotEmpty(campaign)) {
					
					GenericValue childCampaign = EntityUtil.getFirst( delegator.findByAnd("MarketingCampaign", UtilMisc.toMap("marketingCampaignId", selectedCampaign), null, false) );
					if (UtilValidate.isNotEmpty(childCampaign)) {
						childCampaign.put("parentCampaignId", marketingCampaignId);
						childCampaign.store();

					}
					
				}
        		
        	}
        	
        	resp.put(CustomFieldConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
        	resp.put(CustomFieldConstants.RESPONSE_MESSAGE, "Successfully updated trigger campaign configuration!!");
        	
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
    public static String getCustomFieldGroupServices(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
        
    	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
    	Delegator delegator = (Delegator) request.getAttribute("delegator");
        GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
        
        Locale locale = UtilHttp.getLocale(request);
        TimeZone timeZone = UtilHttp.getTimeZone(request);
        HttpSession session = request.getSession(true);
        
        String serviceTypeId = request.getParameter("serviceTypeId");
        
        Map<String, Object> resp = new HashMap<String, Object>();
        try {
        	
        	List<Map<String, Object>> services = new ArrayList<Map<String, Object>>();
        	if (UtilValidate.isNotEmpty(serviceTypeId)) {
        		
        		if (serviceTypeId.equals("INTERNAL")) {
        			
        			List<GenericValue> serviceList = delegator.findByAnd("CustomFieldGroupService", UtilMisc.toMap("isEnabled", "Y"), UtilMisc.toList("sequenceNumber"), false);
        			for (GenericValue service : serviceList) {
        				Map<String, Object> ser = new HashMap<String, Object>();
        				ser.put("serviceConfigId", service.getString("customFieldGroupServiceId"));
        				ser.put("serviceName", service.getString("serviceName"));
        				ser.put("description", service.getString("description"));
        				ser.put("sequenceNumber", service.getLong("sequenceNumber"));
        				
        				services.add(ser);
        			}
        			
        		} else if (serviceTypeId.equals("WEBHOOK_PUSH")) {
        			
        			List<GenericValue> serviceList = delegator.findByAnd("CustomFieldWebhookConfig", UtilMisc.toMap("isEnabled", "Y"), UtilMisc.toList("webhookSeqNum"), false);
        			for (GenericValue service : serviceList) {
        				Map<String, Object> ser = new HashMap<String, Object>();
        				ser.put("serviceConfigId", service.getString("customFieldWebhookConfigId"));
        				ser.put("serviceName", service.getString("serviceName"));
        				ser.put("description", service.getString("serviceName"));
        				ser.put("sequenceNumber", service.getLong("webhookSeqNum"));
        				
        				services.add(ser);
        			}
        			
        		}
        		
        	}
        	
        	resp.put("services", services);
        	
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
  	public static String enableEconomicMetric(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {

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
  				GenericValue customField = EntityUtil.getFirst( delegator.findByAnd("CustomField", UtilMisc.toMap("customFieldId", customFieldId,"groupType","ECONOMIC_METRIC"), null, false) );
  				if (UtilValidate.isNotEmpty(customField)) {
  					customField.put("isEnabled", "Y");
  					
  					customField.store();
  					
  					resp.put(CustomFieldConstants.RESPONSE_MESSAGE, "Successfully Enabled Economic Metric!");
  					resp.put(CustomFieldConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
  				}
  			}
  			
  		} catch (Exception e) {
  			resp.put(CustomFieldConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
              resp.put(CustomFieldConstants.RESPONSE_MESSAGE, "Error: "+e.getMessage());
  			
  			Debug.logError(e.getMessage(), MODULE);
  		}
  		
  		resp.put("data", datas);

  		return doJSONResponse(response, resp);
  	}
      
      @SuppressWarnings("unchecked")
  	public static String disabledEconomicMetric(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {

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
  				GenericValue customField = EntityUtil.getFirst( delegator.findByAnd("CustomField", UtilMisc.toMap("customFieldId", customFieldId,"groupType","ECONOMIC_METRIC"), null, false) );
  				if (UtilValidate.isNotEmpty(customField)) {
  					customField.put("isEnabled", "N");
  					
  					customField.store();
  					
  					resp.put(CustomFieldConstants.RESPONSE_MESSAGE, "Successfully Disabled Economic Metric!");
  					resp.put(CustomFieldConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
  				}
  			}
  			
  		} catch (Exception e) {
  			resp.put(CustomFieldConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
              resp.put(CustomFieldConstants.RESPONSE_MESSAGE, "Error: "+e.getMessage());
  			
  			Debug.logError(e.getMessage(), MODULE);
  		}
  		
  		resp.put("data", datas);

  		return doJSONResponse(response, resp);
  	}
      
    @SuppressWarnings("unchecked")
  	public static String removeSegmentation(HttpServletRequest request, HttpServletResponse response)
  			throws GenericEntityException {

  		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
  		Delegator delegator = (Delegator) request.getAttribute("delegator");
  		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");

  		Locale locale = UtilHttp.getLocale(request);
  		HttpSession session = request.getSession(true);

  		String groupId = request.getParameter("groupId");
  		String customFieldId = request.getParameter("customFieldId");
  		String partyId = request.getParameter("partyId");

  		Map<String, Object> resp = new HashMap<String, Object>();

  		try {

  			if (UtilValidate.isNotEmpty(groupId) && UtilValidate.isNotEmpty(customFieldId) && UtilValidate.isNotEmpty(partyId)) {

  				GenericValue segmentation = EntityUtil.getFirst(delegator.findByAnd("CustomFieldPartyClassification",
  						UtilMisc.toMap("groupId", groupId, "customFieldId", customFieldId, "partyId", partyId), null, false));

  				if (UtilValidate.isNotEmpty(segmentation)) {

  					segmentation.remove();

  					resp.put(CustomFieldConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
  					resp.put(CustomFieldConstants.RESPONSE_MESSAGE, "Successfully removed segmentation..");

  				} else {
  					resp.put(CustomFieldConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
  					resp.put(CustomFieldConstants.RESPONSE_MESSAGE, "Not found segmentation..");
  				}

  			}

  		} catch (Exception e) {

  			resp.put(CustomFieldConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
  			resp.put(CustomFieldConstants.RESPONSE_MESSAGE, "Error: " + e.getMessage());

  			Debug.logError(e.getMessage(), MODULE);
  		}

  		return doJSONResponse(response, resp);
  	}
    
    
}
