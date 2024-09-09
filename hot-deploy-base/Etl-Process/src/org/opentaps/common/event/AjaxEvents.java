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

package org.opentaps.common.event;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Utility class for making Ajax JSON responses.
 * @author Chris Liberty (cliberty@opensourcestrategies.com)
 * @version $Rev$
 */
public final class AjaxEvents {

    private AjaxEvents() { }

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
            Debug.logWarning("Could not get the UTF-8 json string due to UnsupportedEncodingException: " + e.getMessage(), MODULE);
            response.setContentLength(jsonString.length());
        }

        Writer out;
        try {
           /* out = response.getWriter();
            out.write(jsonString);*/
            out = response.getWriter();
			out.write(validate(jsonString));
            out.flush();
        } catch (IOException e) {
            Debug.logError(e, "Failed to get response writer", MODULE);
            result = "error";
        }
        return result;
    }


    /*************************************************************************/
    /**                                                                     **/
    /**                      Common JSON Requests                           **/
    /**                                                                     **/
    /*************************************************************************/


    /** Gets a list of states (provinces) that are associated with a given countryGeoId. */
   /* public static String getStateDataJSON(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        String countryGeoId = request.getParameter("countryGeoId");

        try {
            Collection<GenericValue> states = UtilCommon.getStates(delegator, countryGeoId);
            return doJSONResponse(response, states);
        } catch (GenericEntityException e) {
            return doJSONResponse(response, FastList.newInstance());
        }
    }*/

    // get the sub problem list added By Gnanakumar
    
    /** Gets a list of Sub Problem that are associated with a given Problem code. */
   /* public static String getSubProblemDataJSON(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        String pcodeId = request.getParameter("pcodeId");

        try {
            Collection<GenericValue> subProblem = UtilCommon.getSubProblem(delegator, pcodeId);
            return doJSONResponse(response, subProblem);
        } catch (GenericEntityException e) {
            return doJSONResponse(response, FastList.newInstance());
        }
    }*/
    
    
    // end get the sub problem list  added By Gnanakumar   
    
    
 // get categoryId list based on hierarchLvel added By Gnanakumar
    
    /** Gets a list of Sub Problem that are associated with a given Problem code. */
  /*  public static String getCategoryDataJSON(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        String hierarchyLevel = request.getParameter("hierarchyLevel");
        String catalogId = request.getParameter("catalogId");

        try {
            Collection<GenericValue> categoryIds = UtilCommon.getCategoryId(delegator,hierarchyLevel,catalogId);
            return doJSONResponse(response, categoryIds);
        } catch (GenericEntityException e) {
            return doJSONResponse(response, FastList.newInstance());
        }
    }*/
    
    
    // end categoryId list based on hierarchLvel added By Gnanakumar       
    
    
    /** Return agreement term list specific for given term type.
     * @throws GenericEntityException */
   /* public static String getAgreementTermValidFieldsJSON(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        Locale locale = UtilHttp.getLocale(request.getSession());
        String termType = UtilCommon.getParameter(request, "termType");
        String itemId = UtilCommon.getParameter(request, "item");

        Map<String, Object> resp = new HashMap<String, Object>();
        resp.put("item", itemId);

        List<String> validFields = UtilAgreement.getValidFields(termType, delegator);
        if (UtilValidate.isNotEmpty(validFields)) {

            resp.put("fields", validFields);

            // checks fields that require special handling
            for (String field : validFields) {
                if ("valueEnumId".equals(field)) {
                    // Enumeration. We should send enumeration name and list of values in response.
                    String enumTitle = (String) resp.get("enumTitle");
                    if (enumTitle != null) {
                        // Error. Only enumeration may be for given term type. Ignore.
                        Debug.logWarning("More than one enumeration value for term type " + termType + ". Ignore.", MODULE);
                        continue;
                    }

                    // Gets enumeration mapped to the term type
                    List<GenericValue> termTypeToEnumMaps = delegator.findByAnd("TermTypeToEnumTypeMap", UtilMisc.toMap("termTypeId", termType));
                    if (UtilValidate.isEmpty(termTypeToEnumMaps)) {
                        // Error. No mapping between term type and enum type.
                        Debug.logError("No mapping between term type " + termType + " and enumeration type.", MODULE);
                        continue;
                    }

                    // EnumerationType.description as field title
                    GenericValue termTypeToEnumMap = EntityUtil.getFirst(termTypeToEnumMaps);
                    enumTitle = (String) (termTypeToEnumMap.getRelatedOne("EnumerationType").get("description", "OpentapsEntityLabels", locale));
                    resp.put("enumTitle", UtilValidate.isNotEmpty(enumTitle) ? enumTitle : "Enumeration Value");

                    // Enumeration values
                    List<GenericValue> values = delegator.findByCondition("Enumeration", EntityCondition.makeCondition("enumTypeId", termTypeToEnumMap.getString("enumTypeId")), Arrays.asList("enumId", "description"), Arrays.asList("sequenceId"));
                    List<Map<String, String>> enumValues = new ArrayList<Map<String, String>>();
                    for (GenericValue value : values) {
                        Map<String, String> enumValue = new HashMap<String, String>();
                        enumValue.put("enumId", value.getString("enumId"));
                        enumValue.put("description", (String) value.get("description", "OpentapsEntityLabels", locale));
                        enumValues.add(enumValue);
                    }
                    resp.put("enumValues", enumValues);

                } else if ("currencyUomId".equals(field)) {

                    // Currency drop-down. Returns list of currencies.
                    List<GenericValue> currencies = UtilCommon.getCurrencies(delegator);
                    List<Map<String, String>> currencyValues = new ArrayList<Map<String, String>>();
                    for (GenericValue currency : currencies) {
                        Map<String, String> currencyValue = new HashMap<String, String>();
                        currencyValue.put("uomId", currency.getString("uomId"));
                        currencyValue.put("abbreviation", currency.getString("abbreviation"));
                        currencyValues.add(currencyValue);
                    }
                    resp.put("currencies", currencyValues);
                    resp.put("defaultCurrencyId", UtilConfig.getPropertyValue("opentaps", "defaultCurrencyUomId"));
                }
            }
        }

        return doJSONResponse(response, resp);
    }*/

   /* public static String getPartyCarrierAccountsJSON(HttpServletRequest request, HttpServletResponse response) {
        String partyId = request.getParameter("partyId");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        return org.opentaps.common.event.AjaxEvents.doJSONResponse(response, PartyHelper.getPartyCarrierAccounts(partyId, delegator));
    }
*/
    /*public static String getNewInternalMessagesJSON(HttpServletRequest request, HttpServletResponse response) throws GenericServiceException {

        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Locale locale = UtilHttp.getLocale(request);

        String partyIdTo = UtilCommon.getParameter(request, "partyIdTo");
        String returnNumberOnly = UtilCommon.getParameter(request, "returnNumberOnly");

        Map<String, Object> callCtxt = UtilMisc.<String, Object>toMap("partyIdTo", partyIdTo, "isRead", Boolean.FALSE, "locale", locale);
        Map<String, Object> callResult = dispatcher.runSync("opentaps.receiveInternalMessage", callCtxt);

        List<?> newMessages = FastList.newInstance();

        if (ServiceUtil.isError(callResult) || ServiceUtil.isFailure(callResult)) {
            Debug.logError("Unexpected error. Service opentaps.receiveInternalMessage returned error.", MODULE);
            return org.opentaps.common.event.AjaxEvents.doJSONResponse(response, UtilMisc.toMap("newMessages", newMessages, "numberOfNewMessages", 0));
        }

        List<?> messages = (List<?>) callResult.get("messages");

        if ("Y".equals(returnNumberOnly)) {
            return org.opentaps.common.event.AjaxEvents.doJSONResponse(response, UtilMisc.toMap("newMessages", null, "numberOfNewMessages", UtilValidate.isNotEmpty(messages) ? messages.size() : 0));
        }

        return org.opentaps.common.event.AjaxEvents.doJSONResponse(response, UtilMisc.toMap("newMessages", messages));
    }

    public static String checkExistOrderContentJSON(HttpServletRequest request, HttpServletResponse response) throws GenericServiceException {

        String orderId = UtilCommon.getParameter(request, "orderId");
        String fileName = UtilCommon.getParameter(request, "fileName");
        String initialPath = UtilProperties.getPropertyValue("content.properties", "content.upload.path.prefix");
        String ofbizHome = System.getProperty("ofbiz.home");
        if (!initialPath.startsWith("/")) {
            initialPath = "/" + initialPath;
        }
        String filePath = ofbizHome + initialPath + "/"  + org.opentaps.common.content.ContentServices.ORDERCONTENT_PREV + orderId + "/" + fileName;
        File file = new File(filePath);
        Debug.logInfo(filePath + " exist " + file.exists(), MODULE);
        return org.opentaps.common.event.AjaxEvents.doJSONResponse(response, UtilMisc.toMap("existSameFile", file.exists()));
    }

    *//**
     * Finds and returns list of agreements to given supplier.
     *//*
    public static String findSupplierAgreementsJSON(HttpServletRequest request, HttpServletResponse response) {

        Locale locale = UtilHttp.getLocale(request);
        Delegator delegator = (Delegator) request.getAttribute("delegator");

        String organizationPartyId = (String) request.getSession().getAttribute("organizationPartyId");
        if (UtilValidate.isEmpty(organizationPartyId)) {
            organizationPartyId = UtilCommon.getParameter(request, "organizationPartyId");
            if (UtilValidate.isEmpty(organizationPartyId)) {
                organizationPartyId = UtilConfig.getPropertyValue("opentaps", "organizationPartyId");
                if (UtilValidate.isEmpty(organizationPartyId)) {
                    UtilMessage.createAndLogEventError(request, "OpentapsError_OrganizationNotSet", locale, MODULE);
                }
            }
        }
        String partyId = UtilCommon.getParameter(request, "partyId");

        List<GenericValue> agreements = null;
        try {

            agreements = delegator.findByAnd("Agreement", UtilMisc.toList(
                    EntityCondition.makeCondition("agreementTypeId", "PURCHASE_AGREEMENT"),
                    EntityCondition.makeCondition("statusId", "AGR_ACTIVE"),
                    EntityCondition.makeCondition("partyIdFrom", organizationPartyId),
                    EntityCondition.makeCondition("partyIdTo", partyId),
                    EntityUtil.getFilterByDateExpr())
            );

        } catch (GenericEntityException e) {
            Debug.logError(e.getMessage(), MODULE);
            return doJSONResponse(response, FastList.newInstance());
        }

        return doJSONResponse(response, UtilValidate.isNotEmpty(agreements) ? agreements : FastList.newInstance());
    }

    *//**
     * Checks if a <code>SupplierProduct</code> exists.
     * @param request a <code>HttpServletRequest</code> value
     * @param response a <code>HttpServletResponse</code> value
     * @throws FoundationException if an error raise
     * @return a <code>String</code> value
     *//*
    public static String checkExistSupplierProductJSON(HttpServletRequest request, HttpServletResponse response) throws FoundationException {
        String productId = UtilCommon.getParameter(request, "productId");
        String partyId = UtilCommon.getParameter(request, "partyId");
        String currencyUomId = UtilCommon.getParameter(request, "currencyUomId");
        String quantity = UtilCommon.getParameter(request, "quantity");

        DomainsLoader domainLoader = new DomainsLoader(request);
        PurchasingRepositoryInterface purchasingRepository = domainLoader.loadDomainsDirectory().getPurchasingDomain().getPurchasingRepository();

        Map<String, Object> results = FastMap.<String, Object>newInstance();
        SupplierProduct supplierProduct = purchasingRepository.getSupplierProduct(partyId, productId, new BigDecimal(quantity), currencyUomId);
        results.put("existSupplierProduct", supplierProduct != null);
        results.put("isVirtual", UtilValidate.isNotEmpty(purchasingRepository.findList(Product.class, purchasingRepository.map(Product.Fields.productId, productId, Product.Fields.isVirtual, "Y"))));

        return doJSONResponse(response, results);
    }*/

	/*
	 * added by m.vijayakumar for retrieving the data from the tablename
	 * date:11/03/2016
	 */
	public static String getFeildNameList(HttpServletRequest request, HttpServletResponse response)  {
		String tableName = request.getParameter("tableName");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		List<GenericValue> columnList = new ArrayList<GenericValue>();
		List commonList = new ArrayList();
		try {
			if(UtilValidate.isNotEmpty(tableName))
			{
				columnList = delegator.findByAnd("EtlDestination", UtilMisc.toMap("tableName",tableName), null	,false);		
				for(GenericValue g:columnList)
				{
					Map tempMap = new HashMap();
					List<GenericValue> eltDefaultConfig = delegator.findByAnd("EtlDefaultsConfig", UtilMisc.toMap("etlFieldName",g.getString("etlFieldName"),"etlTableName",tableName),null,false);
					List etlDefList = new ArrayList();
					if(UtilValidate.isNotEmpty(eltDefaultConfig))
					{
						
						for(GenericValue gg:eltDefaultConfig)
						{
							Map tempMap1 = new HashMap();
							tempMap1.put("seqIdConfig",gg.getString("seqId"));
							tempMap1.put("etlFieldNameConfig",gg.getString("etlFieldName"));
							tempMap1.put("defaultValue",gg.getString("defaultValue"));
							tempMap1.put("etlTableName",gg.getString("etlTableName"));
							if(UtilValidate.isNotEmpty(tempMap1))
							{
								etlDefList.add(tempMap1);
							}
							
						}
					}
					tempMap.put("etlDefList",etlDefList);
					tempMap.put("etlDefListSize",etlDefList.size());
					tempMap.put("seqId",g.getString("seqId"));
					tempMap.put("tableName",g.getString("tableName"));
					tempMap.put("etlFieldName",g.getString("etlFieldName"));
					tempMap.put("tableTitle",g.getString("tableTitle"));
					tempMap.put("isPrime",g.getString("isPrime"));
					commonList.add(tempMap);
				}
			}


		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			Debug.log("Exception=="+e.getMessage()); 
			//e.printStackTrace();
			return "error";

		}
		return doJSONResponse(response, commonList);
	}



	public static String getMapElement(HttpServletRequest request, HttpServletResponse response) throws Exception 
	{
		String tableName = request.getParameter("tableName");
		String etlMappingElement =request.getParameter("etlMappingElement");
		String destinationTable =  request.getParameter("destinationTable");
		String listName = request.getParameter("modalName");
		String customElementName = request.getParameter("customElementName");
		
		Delegator delegator = (Delegator) request.getAttribute("delegator");

		List<GenericValue> etlMappingElemens;
		try {
			
			etlMappingElemens = delegator.findByAnd("EtlMappingElements", UtilMisc.toMap("listName", listName,"etlFieldName", etlMappingElement), null, false);
			if(UtilValidate.isNotEmpty(etlMappingElemens))
			{
				GenericValue etlMapUpdate = EntityUtil.getFirst(etlMappingElemens);
				etlMapUpdate.set("tableName", tableName);
				etlMapUpdate.set("tableColumnName", destinationTable);
				
				if (UtilValidate.isNotEmpty(customElementName)) {
					etlMapUpdate.set("etlCustomFieldName", customElementName);
				}
				
				etlMapUpdate.store();
				return doJSONResponse(response, etlMapUpdate);
			}else
			{
				Debug.logInfo("Unavailable of the  EtlMappingElements---"+listName+"and "+etlMappingElement, "");
				return "error";
			}
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			Debug.logInfo("The error message is "+e.getMessage(), "");
			//e.printStackTrace();
		}

		return "error";

	}
	public static String removeMappedList(HttpServletRequest request, HttpServletResponse response) {
		String id = request.getParameter("Id");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map trueOrFalse =  new HashMap();
		String deleted = "N";
		try {
			if(UtilValidate.isNotEmpty(id))
			{

				GenericValue etlMappingElements = delegator.findOne("EtlMappingElements",UtilMisc.toMap("Id",id),false);
				if(UtilValidate.isNotEmpty(etlMappingElements)){
					etlMappingElements.set("tableName", "");
					etlMappingElements.set("tableColumnName", "");
					delegator.store(etlMappingElements);
					deleted = "Y";
				}
			}
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			Debug.log("Exception=="+e.getMessage());

		}
		trueOrFalse.put("deleted", deleted);
		return doJSONResponse(response, trueOrFalse);
	}

	public static String migrateFromStagingToEtlFinal(HttpServletRequest request, HttpServletResponse response) {
		String listName = request.getParameter("listName");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map trueOrFalse =  new HashMap();
		String deleted = "N",updated="N";
		String modelId ="";
		String groupId = "";
		String groupName = "";
		String serviceName = "";
		
		Map constraints = new LinkedHashMap();
		try {
			if(UtilValidate.isNotEmpty(listName))
			{
				GenericValue getModlel = EntityUtil.getFirst(delegator.findByAnd("EtlModel",UtilMisc.toMap("modelName",listName),null,false));
				if(UtilValidate.isNotEmpty(getModlel)){
					serviceName = getModlel.getString("serviceName");
					modelId = getModlel.getString("modelId");
					groupId = getModlel.getString("groupId");
				}

				List conditions = UtilMisc.toList(new EntityExpr("listName", EntityOperator.EQUALS, listName));
				conditions.add(new EntityExpr("tableName", EntityOperator.NOT_EQUAL, null));
				conditions.add(new EntityExpr("tableColumnName", EntityOperator.NOT_EQUAL, null));

				EntityConditionList conditionList = new EntityConditionList( conditions,EntityOperator.AND);
				List<GenericValue> etlMappingElements;

				etlMappingElements = delegator.findAll("EtlMappingElements",false);
				etlMappingElements = EntityUtil.filterByCondition(etlMappingElements, conditionList);
				if(UtilValidate.isNotEmpty(etlMappingElements))
				{
					String mappedTableName = "";
					
					//restrict the primary key filed must map procedure
					if(UtilValidate.isNotEmpty(etlMappingElements))
					{
						for(GenericValue g:etlMappingElements)
						{
							if(UtilValidate.isNotEmpty(g.getString("tableName")))
							{
								mappedTableName = g.getString("tableName");
								break;
							}
							
							
						}
						
					}
					if(UtilValidate.isNotEmpty(mappedTableName))
					{
						List primCondition = UtilMisc.toList(new EntityExpr("tableName", EntityOperator.EQUALS, mappedTableName));
						primCondition.add(new EntityExpr("isPrime", EntityOperator.EQUALS, "Y"));
						
						EntityConditionList conditionforPrime = new EntityConditionList<EntityCondition>(primCondition, EntityOperator.AND);

						List<GenericValue> etlPrimeFiled = delegator.findAll("EtlDestination",false);//, conditionforPrime, UtilMisc.toSet("etlFieldName"), null);
						etlPrimeFiled = EntityUtil.filterByCondition(etlPrimeFiled, conditionforPrime);
						if(UtilValidate.isNotEmpty(etlPrimeFiled))
						{
							List<String> etlfildName = EntityUtil.getFieldListFromEntityList(etlPrimeFiled, "etlFieldName", true);
							
							//List<GenericValue> etlMappedOrNot = delegator.findByCondition("EtlMappingElements", new EntityExpr("tableColumnName", EntityOperator.IN	, etlfildName), null	, null);
							List<String> etlMappingElemPrim = EntityUtil.getFieldListFromEntityList(etlMappingElements, "tableColumnName", true);
							
							boolean condition = etlMappingElemPrim.containsAll(etlfildName);
							if(!condition)
							{

								deleted = "N";
								trueOrFalse.put("deleted", deleted);
								trueOrFalse.put("errorMsg", "Please map the required fields before creating the model");
								return doJSONResponse(response, trueOrFalse);
							}
							
							
						}else
						{
							deleted = "N";
							trueOrFalse.put("deleted", deleted);
							trueOrFalse.put("errorMsg", "Primary key is missing in destination table");
							return doJSONResponse(response, trueOrFalse);
						}
					}else
					{
						deleted = "N";
						trueOrFalse.put("deleted", deleted);
						trueOrFalse.put("errorMsg", "No Table is found with mapped");
						return doJSONResponse(response, trueOrFalse);
					}

					//end restrict primarykey


					//delete the table when same list name is comming inside on it
					List<GenericValue> etlSourceTableEntry = delegator.findByAnd("EtlSourceTable", UtilMisc.toMap("listName",listName), null, false);
					if(UtilValidate.isNotEmpty(etlSourceTableEntry))
					{
						delegator.removeAll(etlSourceTableEntry);
						updated="Y";
					}

					//migration process of one table to another table
					for(GenericValue g:etlMappingElements)
					{

						GenericValue etlSourceTable = delegator.makeValue("EtlSourceTable");
						etlSourceTable.put("Id", delegator.getNextSeqId("EtlSourceTable"));
						etlSourceTable.put("listName", listName);
						etlSourceTable.put("etlFieldName", g.getString("etlFieldName"));
						etlSourceTable.put("tableName", g.getString("tableName"));
						etlSourceTable.put("tableColumnName", g.getString("tableColumnName"));
						if("".equals(g.getString("tableName")) || "".equals(g.getString("tableColumnName")))
						{
							continue;
						}
						etlSourceTable.create();
					}
					//create Process while creating model
					String processId = listName+"_Process";
					String processName = listName+" Process";
					String description = listName+" Process";
					
					//int removeProcess = delegator.removeByAnd("EtlProcess",UtilMisc.toMap("modalName",listName));
					GenericValue checkProcess = EntityUtil.getFirst(delegator.findByAnd("EtlProcess",UtilMisc.toMap("modalName",listName),null,false));
					if(UtilValidate.isEmpty(checkProcess)){
						GenericValue group = delegator.findOne("EtlGrouping",UtilMisc.toMap("groupId",groupId),false);
						groupName = group.getString("groupName");
						GenericValue makeProcess = delegator.makeValue("EtlProcess");
						makeProcess.put("processId", processId);
						makeProcess.put("processName", processName);
						makeProcess.put("modalName", listName);
						makeProcess.put("serviceName", serviceName);
						makeProcess.put("description", description);
						makeProcess.put("tableName", mappedTableName);
						makeProcess.create();	
						//EtlProcess Grouping
						GenericValue processGroup = delegator.makeValue("EtlProcessGrouping");
						processGroup.put("processId", processId);
						processGroup.put("groupId", groupId);
						processGroup.put("processName", processName);
						processGroup.put("groupName", groupName);
						processGroup.put("tableName", mappedTableName);
						processGroup.create();
						//EtlModel Grouping
						GenericValue modelGroup = delegator.makeValue("EtlModelGrouping");
						modelGroup.put("modelId", modelId);
						modelGroup.put("groupId", groupId);
						modelGroup.put("modelName", listName);
						modelGroup.put("groupName", groupName);
						modelGroup.put("tableName", mappedTableName);
						modelGroup.create();
					}else{
						checkProcess.put("description", description);
						checkProcess.put("tableName", mappedTableName);
						checkProcess.put("serviceName", serviceName);
						checkProcess.store();
					}
					
					deleted = "Y";
				}
				constraints.put("deleted", deleted);
				constraints.put("updated", updated);
			}
			trueOrFalse.put("deleted", deleted);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			Debug.log("Exception=="+e.getMessage());
			//e.printStackTrace();
		}
		return doJSONResponse(response, trueOrFalse);
	}



	public static String getEtlSource(HttpServletRequest request, HttpServletResponse response) {
		String listName = request.getParameter("listName");
		String tableName  = request.getParameter("tableName");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Map trueOrFalse =  new HashMap();
		String deleted = "N";
		List<GenericValue> etlSourceTable = null;
		List etlFinalList = new ArrayList();
		try {
			etlSourceTable = delegator.findByAnd("EtlSourceTable", UtilMisc.toMap("listName",listName,"tableName",tableName),null,false);

			//added by m.vijayakumar date:18/03/2016 for getting the selected process name
			String processId = "";
			String processDescription="";
			List<GenericValue> etlProcess = delegator.findByAnd("EtlProcess", UtilMisc.toMap("modalName", listName), null, false);
			
			if(UtilValidate.isNotEmpty(etlProcess))
			{
				GenericValue etlprocessGeneric = EntityUtil.getFirst(etlProcess);
				if(UtilValidate.isNotEmpty(etlprocessGeneric))
				{
					processId = etlprocessGeneric.getString("processId");
					processDescription = etlprocessGeneric.getString("description");
				}
			}
			if(UtilValidate.isNotEmpty(etlSourceTable))
			{
				List<GenericValue> primeEtlDestination = delegator.findByAnd("EtlDestination", UtilMisc.toMap("isPrime", "Y"), null, false);
				List<String> etlFieldName = EntityUtil.getFieldListFromEntityList(primeEtlDestination, "etlFieldName", true);

				for(GenericValue g:etlSourceTable)
				{

					//check whether the primary key field exists or not date:18/03/2016

					boolean isPrime = etlFieldName.contains(g.getString("tableColumnName"));

					//end of primary key check

					Map tmp = new HashMap();
					tmp.put("etlFieldName", g.getString("etlFieldName"));
					tmp.put("tableColumnName", g.getString("tableColumnName"));
					tmp.put("isPrime", isPrime?"Y":"N");
					tmp.put("processId", processId);
					tmp.put("processDescription", processDescription);

					//added by m.vijayakumar date:20/05/2016
					tmp.put("tableName", g.getString("tableName"));
					
					GenericValue mappedElement = EntityUtil.getFirst( delegator.findByAnd("EtlMappingElements", UtilMisc.toMap("listName", g.getString("listName"), "tableName", g.getString("tableName"), "etlFieldName", g.getString("etlFieldName")), null, false) );
					
					if (UtilValidate.isNotEmpty(mappedElement)) {
						tmp.put("etlCustomFieldName", mappedElement.getString("etlCustomFieldName") );
					}
					
					etlFinalList.add(tmp);
				}	
			}



			//end @vijayakumar
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			Debug.log("Exception=="+e.getMessage());

		}

		return doJSONResponse(response, etlFinalList);
	}



	public static String getModalProcessAssociation(HttpServletRequest request, HttpServletResponse response) {
		String processId = request.getParameter("processId");
		String modalName = request.getParameter("modalName");
		String tableName = request.getParameter("tableName");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String dynamicDescription="";
		Map trueOrFalse =  new HashMap();
		String set = "N";
		List<GenericValue> etlSourceTable = null;
		try {
			GenericValue etlProcess = delegator.findOne("EtlProcess",UtilMisc.toMap("processId",processId),false);



			if(UtilValidate.isNotEmpty(etlProcess))
			{
				//added by m.vijayakumar date:18/03/2016 to remove previous associaton with one to many mapping avoidance
				List<GenericValue> prevAssocEtlProcess = delegator.findByAnd("EtlProcess", UtilMisc.toMap("modalName",modalName),null,false);
				for(GenericValue g:prevAssocEtlProcess)
				{
					g.set("modalName", "");
				}
				delegator.storeAll(prevAssocEtlProcess);
				//end @vijayakumar

				etlProcess.set("modalName", modalName);
				etlProcess.set("tableName", tableName);
				etlProcess.store();
				set="Y";
			}
			
			GenericValue etlProcessDesc = delegator.findOne("EtlProcess",UtilMisc.toMap("processId",processId),false);
			if(UtilValidate.isNotEmpty(etlProcessDesc))
			{
				dynamicDescription = etlProcessDesc.getString("description");
			}
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			Debug.log("Exception=="+e.getMessage());

		}
		
		trueOrFalse.put("etlDescription", dynamicDescription);
		trueOrFalse.put("set", set);
		return doJSONResponse(response, trueOrFalse);
	}

	public static String removeAssocModelToProcess(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String processId = request.getParameter("processId");
		GenericValue etlProcess;
		Map trueOrFalse =  new HashMap();
		String set="N";
		try {
			etlProcess = delegator.findOne("EtlProcess",UtilMisc.toMap("processId",processId),false);
			if(UtilValidate.isNotEmpty(etlProcess))
			{
				if(UtilValidate.isNotEmpty(etlProcess.getString("modalName")))
				{

					etlProcess.set("modalName", "");
					delegator.store(etlProcess);
					set="Y";	
				}
			}
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			Debug.log("Exception=="+e.getMessage()); 
			return "errror";
		}
		trueOrFalse.put("set", set);
		return doJSONResponse(response, trueOrFalse);

	}

	
	public static String removeAllMapping(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String listName = request.getParameter("listName");
		Map trueOrFalse =  new HashMap();
		String set="N";
		if(UtilValidate.isNotEmpty(listName))
		{
			List<GenericValue> etlMappingElements;
			try {
				etlMappingElements = delegator.findByAnd("EtlMappingElements", UtilMisc.toMap("listName", listName), null, false);
				if(UtilValidate.isNotEmpty(etlMappingElements))
				{
					for(GenericValue g:etlMappingElements)
					{
						g.set("tableName", "");
						g.set("tableColumnName", "");
					}
					delegator.storeAll(etlMappingElements);
					set="Y";
				}
				
				//finally migratted table need to be empty
				List<GenericValue> etlSourceTable = delegator.findByAnd("EtlSourceTable", UtilMisc.toMap("listName",  listName), null, false);
				if(UtilValidate.isNotEmpty(etlSourceTable))
				{
					delegator.removeAll(etlSourceTable);
				}
				//end of empty to be migrated table
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				Debug.log("Exception=="+e.getMessage()); 
			    //e.printStackTrace();
			}
			
			
		}
		trueOrFalse.put("set", set);
		return doJSONResponse(response, trueOrFalse);
	}

	public static String getCompleteAssociatedFromEtlMapping(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String listName = request.getParameter("listName");
		String tableName = request.getParameter("tableName");
		
		List<Map<String, String>> etlAdditionalMapList = new ArrayList<Map<String,String>>();
		try
		{
			
			if(UtilValidate.isNotEmpty(listName))
			{
				
				String processId = "";
				String processDescription="";
				
				List<GenericValue> etlProcess = delegator.findByAnd("EtlProcess", UtilMisc.toMap("modalName", listName), null, false);
				
				if(UtilValidate.isNotEmpty(etlProcess))
				{
					GenericValue etlprocessGeneric = EntityUtil.getFirst(etlProcess);
					if(UtilValidate.isNotEmpty(etlprocessGeneric))
					{
						processId = etlprocessGeneric.getString("processId");
						processDescription = etlprocessGeneric.getString("description");
					}
				}
				
				List conditions = UtilMisc.toList(new EntityExpr("listName", EntityOperator.EQUALS, listName));
				conditions.add(new EntityExpr("tableName", EntityOperator.EQUALS, tableName));
				conditions.add(new EntityExpr("tableColumnName", EntityOperator.NOT_EQUAL, ""));

				EntityConditionList conditionList = new EntityConditionList( conditions,EntityOperator.AND);
				
				List<GenericValue> etlMappingElements = delegator.findAll("EtlMappingElements",false);//, conditionList, null, null);
				
				etlMappingElements = EntityUtil.filterByCondition(etlMappingElements, conditionList);
				//for additional map tab need some amount of new list 
				
				for(GenericValue g:etlMappingElements)
				{
					Map<String,String> etlMap = new HashMap<String, String>();
					etlMap.put("Id", g.getString("Id"));
					etlMap.put("etlFieldName",g.getString("etlFieldName") );
					etlMap.put("etlCustomFieldName",g.getString("etlCustomFieldName") );
					etlMap.put("tableColumnName", g.getString("tableColumnName"));
					
					etlMap.put("processId", processId);
					etlMap.put("processDescription", processDescription);
					
					//now we got table name so lets take the corressponding table column name sequence id
					if(UtilValidate.isNotEmpty(tableName))
					{
						List entDestCondition = new ArrayList();
						entDestCondition.add(new EntityExpr("tableName", EntityOperator.EQUALS	, tableName));
						entDestCondition.add(new EntityExpr("etlFieldName", EntityOperator.EQUALS	, g.getString("tableColumnName")));
						
						EntityConditionList entDestConditionList = new EntityConditionList( entDestCondition,EntityOperator.AND);
						List<GenericValue> etlDestinationList = delegator.findAll("EtlDestination",false);//, entDestConditionList, null, null);
						etlDestinationList =   EntityUtil.filterByCondition(etlDestinationList, entDestConditionList);
						if(UtilValidate.isNotEmpty(etlDestinationList))
						{
							GenericValue etlDest = EntityUtil.getFirst(etlDestinationList);
							etlMap.put("mappTap", etlDest.getString("seqId"));
							etlMap.put("isPrime", etlDest.getString("isPrime"));
						}
					}
					etlAdditionalMapList.add(etlMap);

				}
				//end of new list
			}
		}catch(Exception e)
		{
			/*e.printStackTrace();*/
			Debug.logError(e.getMessage(),MODULE);
			return "error";
		}
		return doJSONResponse(response, etlAdditionalMapList);
	}
	public static String getHelpContent(HttpServletRequest request, HttpServletResponse response) {
		
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		
		//getting the neccassary input
		String messageModuleId = request.getParameter("messageModuleId");  //component name
		String reference = request.getParameter("references");  //table name
		String MessageTypeId = request.getParameter("MessageTypeId");  //field name of the table name
		String messageDescription = "";
		
		//response map attribute values
		Map<String, String> responseCnt = new HashMap<String, String>();
		
		try
		{
			if(UtilValidate.isNotEmpty("messageModuleId"))
			{
				//checking whether the message module is exists the message module id
				GenericValue messageModule = delegator.findOne("MessageModule",UtilMisc.toMap("MessageModuleId",messageModuleId),false);
				if(UtilValidate.isNotEmpty(messageModule))
				{
					List<GenericValue> message = delegator.findByAnd("Message", UtilMisc.toMap("MessageModuleId",messageModuleId,"Reference",reference,"MessageTypeId",MessageTypeId),null,false);
					if(UtilValidate.isNotEmpty(message))
					{
						//getting the short or long message for the particular field
						GenericValue messageValid = EntityUtil.getFirst(message);
						if(UtilValidate.isNotEmpty(messageValid.getString("LongMessage")))
						{
							messageDescription = messageValid.getString("LongMessage");
						}else 
						{
							messageDescription = messageValid.getString("LongMessage");
						}
						
						
					}
				}
			
			}
		}catch(Exception e)
		{
			/*e.printStackTrace();*/
			Debug.logError(e.getMessage(),MODULE);
			return "error";
		}
		responseCnt.put("messageDescription", messageDescription);
		return doJSONResponse(response, responseCnt);
		
	}
	
	//end @vijayakumar for ETL process
	
	public static String getEtlImportTypeData(HttpServletRequest request, HttpServletResponse response) {

		GenericDelegator delegator = (GenericDelegator)request.getAttribute("delegator");
		String processId = request.getParameter("processId");

		Map<String, Object> resp = new HashMap<String, Object>();

		try {
			GenericValue process = delegator.findOne("EtlProcess", UtilMisc.toMap("processId", processId),false);
			if (UtilValidate.isNotEmpty(process)) 
			{
				String table = process.getString("tableName");
				GenericValue checkUploadRequest = EntityUtil.getFirst(delegator.findByAnd("EtlUploadRequest",UtilMisc.toMap("status","RUNNING"),null,false));
				if(UtilValidate.isNotEmpty(checkUploadRequest)){
					resp.put("result","lock");
				}
				
			}
			else
			{
				resp.put("result","unLock");
			}

		} catch (GenericEntityException e) {
			return org.opentaps.common.event.AjaxEvents.doJSONResponse(response, UtilMisc.toMap("name", ""));
		}

		return org.opentaps.common.event.AjaxEvents.doJSONResponse(response, resp);
	}
	public static void setStoreLocale(HttpServletRequest request, HttpServletResponse response)throws IOException {
		HttpSession session = ((HttpServletRequest) request).getSession();
		PrintWriter out = response.getWriter();                
		String locale = request.getParameter("locale");		
		String result="try";
		try{
			if(UtilValidate.isNotEmpty(locale)){	
				UtilHttp.setLocale(request, locale);
			}
			result="success";
		}catch(Exception e2){
			result="error";	
			/*e2.printStackTrace();*/
			Debug.logError(e2.getMessage(),MODULE);
		}		
		out.print(result);
	}
	
	public static String createCrossReference(HttpServletRequest request, HttpServletResponse response)throws IOException {
		GenericDelegator delegator = (GenericDelegator)request.getAttribute("delegator");       
		String crossReferenceId = request.getParameter("crossReferenceId");		
		String description = request.getParameter("description");		
		String isEnabled = request.getParameter("isEnabled");
		if(UtilValidate.isEmpty(isEnabled))
			isEnabled = "No";
		
		if(UtilValidate.isNotEmpty(crossReferenceId)){
		try {
			GenericValue partyIdentificationType = EntityQuery.use(delegator).from("PartyIdentificationType").where("partyIdentificationTypeId", crossReferenceId).queryOne();
			if(UtilValidate.isEmpty(partyIdentificationType)){
				GenericValue partyIdentificationType1 = delegator.makeValue("PartyIdentificationType",UtilMisc.toMap("partyIdentificationTypeId", crossReferenceId));
				partyIdentificationType1.put("description", description);
				partyIdentificationType1.put("isEnabled", isEnabled);
				partyIdentificationType1.create();
			}
			else{
				partyIdentificationType.put("description", description);
				partyIdentificationType.put("isEnabled", isEnabled);
				partyIdentificationType.store();
			}
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			Debug.log("Exception=="+e.getMessage());
		}	
	}
		return "success";
	}
public static String validate(String str)
	{
		return str;
	}
}