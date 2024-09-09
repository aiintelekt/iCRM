package org.fio.crm.util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityFieldValue;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.ModelService;

/**
 * @author Sharif
 *
 */
public class DataHelper {
	
	private static String MODULE = DataHelper.class.getName();

	public static Map getDropDownOptions(List<GenericValue> entityList, String keyField, String desField){
		Map<String, Object> options = new LinkedHashMap<String, Object>();
		for (GenericValue entity : entityList) {
			options.put(entity.getString(keyField), entity.getString(desField));
		}
		return options;
	}
	
	public static Map getDropDownOptionsFromMultiDesField(List<GenericValue> entityList, String keyField, LinkedList<String> desField){
		Map<String, Object> options = new LinkedHashMap<String, Object>();
		for (GenericValue entity : entityList) {
			String descField = "";
			for(String desFieldVal : desField) {
				if(UtilValidate.isNotEmpty(descField)) {
				   if(UtilValidate.isNotEmpty(entity.getString(desFieldVal))) {
					   descField = descField + " " + entity.getString(desFieldVal);
				   }
				} else {
					descField = UtilValidate.isNotEmpty(entity.getString(desFieldVal)) ? entity.getString(desFieldVal) : "";
				}
			}
			options.put(entity.getString(keyField), descField);
		}
		return options;
	}
	
	public static Map getDropDownOptionsFromMap(List<Map<String, Object>> entityList, String keyField, String desField){
		Map<String, Object> options = new LinkedHashMap<String, Object>();
		for (Map<String, Object> entity : entityList) {
			options.put(entity.get(keyField).toString(), entity.get(desField));
		}
		return options;
	}
	
	public static String sqlPropToJavaProp(String prop) {
		if (UtilValidate.isNotEmpty(prop)) {
			//String prop = "hp_due_wthin_1_yr_amt";
			prop = prop.toLowerCase();
			prop = prop.replace("_1_", "1");
			prop = prop.replace("_2_", "2");
			String convertedString = "";
			for (int i = 0; i < prop.length(); i++) {
				if (prop.charAt(i) == '_') {
					convertedString += (""+prop.charAt(++i)).toUpperCase();
				} else {
					convertedString += prop.charAt(i);
				}
			}
			return convertedString;
		}
		return prop;
	}
	
	public static Map getLeadModelList(Delegator delegator, String modelType){
		
		Map<String, Object> modelList = new HashMap<String, Object>();
		try {
			
			EntityCondition conditions = null;
			
			if (UtilValidate.isNotEmpty(modelType)) {
				conditions = EntityCondition.makeCondition("tableName", EntityOperator.EQUALS, modelType);
			} else {
				conditions = EntityCondition.makeCondition(UtilMisc.toList(
						EntityCondition.makeCondition("tableName", EntityOperator.EQUALS, "DataImportLead")
				), EntityOperator.OR);
			}
			
			List<GenericValue> etlSourceTable = delegator.findList("EtlSourceTable", conditions, null, null, null, false);
			if(UtilValidate.isNotEmpty(etlSourceTable)){
				List<String> listNameList = EntityUtil.getFieldListFromEntityList(etlSourceTable, "listName", true);
				
				if (UtilValidate.isNotEmpty(listNameList)) {
					for (String modelName : listNameList) {
						GenericValue etlModel = EntityUtil.getFirst( delegator.findByAnd("EtlModel", UtilMisc.toMap("modelName", modelName), null, false) );
						if (UtilValidate.isNotEmpty(etlModel) && UtilValidate.isEmpty(etlModel.getString("isExport"))) {
							modelList.put(etlModel.getString("modelId"), modelName);
						}
					}
				}
				
			}
		} catch (GenericEntityException e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);

		}
		
		return modelList;
		
	}
	
	public static String getLeadId(String prefix, String sequenceNumber){
		
		String formattedPostCode = "";
		if (UtilValidate.isNotEmpty(prefix)) {
			formattedPostCode = prefix;
		}
		
		if(UtilValidate.isNotEmpty(sequenceNumber)){
			int length = sequenceNumber.length();
			if (length==1) {
				formattedPostCode += "0000" + (sequenceNumber);
	        }
			else if (length==2) {
				formattedPostCode += "000" + (sequenceNumber);
	        }
			else if (length==3) {
				formattedPostCode += "00" + (sequenceNumber);
	        }
			else if (length==4) {
				formattedPostCode += "0" + (sequenceNumber);
			}
	        else{
	        	formattedPostCode += (sequenceNumber);
	        }
		}
		
		return formattedPostCode;
	}
	
	public static boolean isResponsibleForParty(Delegator delegator, String partyId) {
		
		try {
			List<EntityCondition> conditions = new ArrayList <EntityCondition>();
			//conditions.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "ACCOUNT_MANAGER"));
			conditions.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
			
			conditions.add(EntityCondition.makeCondition(EntityOperator.AND,
			        EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "ACCOUNT_MANAGER")
					));
			
			conditions.add(EntityCondition.makeCondition(EntityOperator.OR,
			        EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PARTY_DISABLED"),
			        EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, null)));
			EntityCondition mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);
			GenericValue responsibleForParty = EntityUtil.getFirst( delegator.findList("PartyToSummaryByRole", mainConditons, null, null, null, false) );
			if(UtilValidate.isNotEmpty(responsibleForParty)) {
				return true;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);

		}
		
		return false;
	}
	
	public static boolean isResponsibleForParty(Delegator delegator, String employeePartyId, String jobFamily, String countryGeoId, String city) {
		
		try {
			
			List<EntityCondition> conditions = new ArrayList <EntityCondition>();
			
			String emplPositionTypeId = null;
			if (UtilValidate.isNotEmpty(jobFamily) && jobFamily.equals("JOBFAMILY_0001")) {
				emplPositionTypeId = "DBS_TC";
			} else if (UtilValidate.isNotEmpty(jobFamily) && jobFamily.equals("JOBFAMILY_0002")) {
				emplPositionTypeId = "DBS_RM";
			}
			
			if (UtilValidate.isNotEmpty(emplPositionTypeId) && UtilValidate.isNotEmpty(countryGeoId) && UtilValidate.isNotEmpty(city)) {
				conditions.add( EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("emplPositionTypeId", EntityOperator.EQUALS, emplPositionTypeId),
						EntityCondition.makeCondition("employeePartyId", EntityOperator.EQUALS, employeePartyId),
						EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "EMPL_POS_OCCUPIED"),
						EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, "Company"),
						EntityCondition.makeCondition("countryGeoId", EntityOperator.EQUALS, countryGeoId),
						EntityCondition.makeCondition("city", EntityOperator.EQUALS, city),
						EntityUtil.getFilterByDateExpr("actualFromDate", "actualThruDate")
						)
						);
				
				conditions.add( EntityCondition.makeCondition(EntityOperator.AND,
						EntityUtil.getFilterByDateExpr()
						)
						);
				
				EntityCondition mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);
				GenericValue responsibleForParty = EntityUtil.getFirst( delegator.findList("EmplPositionAndFulfillment", mainConditons, null, null, null, false) );
				if (UtilValidate.isNotEmpty(responsibleForParty)) {
			        return true;
				}
			}
			
		} catch (Exception e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);

		}
		
		return false;
	}
	
	public static String getResponsibleForParty(Delegator delegator, String jobFamily, String countryGeoId, String city) {
		
		try {
			
			List<EntityCondition> conditions = new ArrayList <EntityCondition>();
			
			String emplPositionTypeId = null;
			if (UtilValidate.isNotEmpty(jobFamily) && jobFamily.equals("JOBFAMILY_0001")) {
				emplPositionTypeId = "DBS_TC";
			} else if (UtilValidate.isNotEmpty(jobFamily) && jobFamily.equals("JOBFAMILY_0002")) {
				emplPositionTypeId = "DBS_RM";
			}
			
			if (UtilValidate.isNotEmpty(emplPositionTypeId) && UtilValidate.isNotEmpty(countryGeoId) && UtilValidate.isNotEmpty(city)) {
				conditions.add( EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("emplPositionTypeId", EntityOperator.EQUALS, emplPositionTypeId),
						EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "EMPL_POS_OCCUPIED"),
						EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, "Company"),
						EntityCondition.makeCondition("countryGeoId", EntityOperator.EQUALS, countryGeoId),
						EntityCondition.makeCondition("city", EntityOperator.EQUALS, city),
						EntityUtil.getFilterByDateExpr("actualFromDate", "actualThruDate")
						)
						);
				
				conditions.add( EntityCondition.makeCondition(EntityOperator.AND,
						EntityUtil.getFilterByDateExpr()
						)
						);
				
				EntityCondition mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);
				List<GenericValue> responsibleForPartyList = delegator.findList("EmplPositionAndFulfillment", mainConditons, null, null, null, false);
				if (UtilValidate.isNotEmpty(responsibleForPartyList)) {
					
					Map<String, Long> employeeList = new LinkedHashMap<String, Long>();
					for (GenericValue responsibleForParty : responsibleForPartyList) {
						String employeePartyId = responsibleForParty.getString("employeePartyId");
						
						EntityCondition responsibleForCountCond = EntityCondition.makeCondition(EntityOperator.AND,
								EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, employeePartyId),
								EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "ACCOUNT_MANAGER"),
								EntityCondition.makeCondition("securityGroupId", EntityOperator.EQUALS, "ACCOUNT_OWNER"),
								EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "RESPONSIBLE_FOR"),
								EntityUtil.getFilterByDateExpr()
								);
						
						long responsibleForCount = delegator.findCountByCondition("PartyRelationship", responsibleForCountCond, null, null);
						
						employeeList.put(employeePartyId, responsibleForCount);
					}
					
			        LinkedHashMap<String, Long> sortedEmplList = new LinkedHashMap<>();
			        
			        employeeList.entrySet()
			        .stream()
			        .sorted(Map.Entry.comparingByValue())
			        .forEachOrdered(x -> sortedEmplList.put(x.getKey(), x.getValue()));
			        
			        Map.Entry<String, Long> firstResult = sortedEmplList.entrySet().iterator().next();
			        String employeePartyId= firstResult.getKey();
			        Long responsibleForCount = firstResult.getValue();
			        
			        Debug.logInfo("employeePartyId: " + employeePartyId + ", responsibleForCount: "+responsibleForCount, MODULE);
					
			        return employeePartyId;
				}
			}
			
		} catch (Exception e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);

		}
		
		return null;
	}
	
	public static String getResponsibleForParty(Delegator delegator, String partyId) {
		
		try {
			
			List<EntityCondition> conditions = new ArrayList <EntityCondition>();
			
			conditions.add( EntityCondition.makeCondition(EntityOperator.AND,
					EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, partyId),
					EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "ACCOUNT_MANAGER"),
			        EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.IN, UtilMisc.toList("ACCOUNT", "LEAD", "CONTACT")),
			        EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "RESPONSIBLE_FOR"),
					EntityUtil.getFilterByDateExpr()
					)
					);
			
			EntityCondition mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);
			GenericValue responsibleForParty = EntityUtil.getFirst( delegator.findList("PartyRelationship", mainConditons, null, null, null, false) );
			if (UtilValidate.isNotEmpty(responsibleForParty)) {
				return responsibleForParty.getString("partyIdTo");
			}
			
		} catch (Exception e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);

		}
		
		return null;
	}
	
	public static String getEmployeePositionType(Delegator delegator, String employeePartyId, String countryGeoId) {
		
		try {
			
			List<EntityCondition> conditions = new ArrayList <EntityCondition>();
			
			if (UtilValidate.isNotEmpty(employeePartyId) && UtilValidate.isNotEmpty(countryGeoId)) {
				conditions.add( EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("employeePartyId", EntityOperator.EQUALS, employeePartyId),
						EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "EMPL_POS_OCCUPIED"),
						EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, "Company"),
						EntityCondition.makeCondition("countryGeoId", EntityOperator.EQUALS, countryGeoId),
						EntityUtil.getFilterByDateExpr("actualFromDate", "actualThruDate")
						)
						);
				
				conditions.add( EntityCondition.makeCondition(EntityOperator.AND,
						EntityUtil.getFilterByDateExpr()
						)
						);
				
				EntityCondition mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);
				GenericValue responsibleForParty = EntityUtil.getFirst( delegator.findList("EmplPositionAndFulfillment", mainConditons, null, null, null, false) );
				if (UtilValidate.isNotEmpty(responsibleForParty)) {
			        return responsibleForParty.getString("emplPositionTypeId");
				}
			}
			
		} catch (Exception e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);

		}
		
		return null;
	}
	
	public static Map<String, Object> prepareImportLeadResult(String actionType, String responseType, String backUrl) {
		
		Map<String, Object> result = new LinkedHashMap<String, Object>();
		
		if (UtilValidate.isNotEmpty(actionType) && UtilValidate.isNotEmpty(responseType)) {
			
			switch (actionType) {
			case "UPDATE":
				if (responseType.equals("SUCCESS")) {
					result.put(ModelService.RESPONSE_MESSAGE, "success-update");
				} else {
					result.put(ModelService.RESPONSE_MESSAGE, "error-update");
				}
				break;
			case "CREATE":
				if (responseType.equals("SUCCESS")) {
					result.put(ModelService.RESPONSE_MESSAGE, "success-create");
				} else {
					result.put(ModelService.RESPONSE_MESSAGE, "error-create");
				}			
				break;
			case "STAGING":
				if (responseType.equals("SUCCESS")) {
					String responseMessage = "success-staging";
					if (UtilValidate.isNotEmpty(backUrl)) {
						responseMessage += "-"+backUrl;
					}
					result.put(ModelService.RESPONSE_MESSAGE, responseMessage);
				} else {
					result.put(ModelService.RESPONSE_MESSAGE, "error-staging");
				}
				break;
			default:
				break;
			}
			
		}
		
		return result;
	}
	
	public static boolean getFirstValidRoleTypeId(String roleTypeId, List<String> possibleRoleTypeIds) throws GenericEntityException {

        for (String possibleRoleTypeId : possibleRoleTypeIds) {
        	if (possibleRoleTypeId.equals(roleTypeId))  {
                return true;
            }
        }
        return false;
    }
	
	public static String getGeoName(Delegator delegator, String value, String geoTypeId) {
		
		try {
			if (UtilValidate.isNotEmpty(value)) {
				EntityCondition condition = EntityCondition.makeCondition(EntityOperator.OR,
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("geoId")), EntityOperator.EQUALS, value.toString().toUpperCase()),
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("geoCode")), EntityOperator.EQUALS, value.toString().toUpperCase()),
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("geoName")), EntityOperator.EQUALS, value.toString().toUpperCase())
               			);   
				
				condition = EntityCondition.makeCondition(EntityOperator.AND,
               			EntityCondition.makeCondition("geoTypeId", EntityOperator.EQUALS, geoTypeId),
               			condition
               			);                     	
				
				GenericValue entity = EntityUtil.getFirst( delegator.findList("Geo", condition, null, UtilMisc.toList("-createdStamp"), null, false) );
				if (UtilValidate.isNotEmpty(entity)) {
					return entity.getString("geoName");
				}
			}
		} catch (Exception e) {
		}
		
		return "";
	}
	
	public static String getEnumDescription(Delegator delegator, String value, String enumTypeId) {
		
		try {
			if (UtilValidate.isNotEmpty(value)) {
				EntityCondition condition = EntityCondition.makeCondition(EntityOperator.OR,
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("enumId")), EntityOperator.EQUALS, value.toString().toUpperCase()),
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("enumCode")), EntityOperator.EQUALS, value.toString().toUpperCase()),
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("description")), EntityOperator.EQUALS, value.toString().toUpperCase())
               			);    
				
				if (UtilValidate.isNotEmpty(enumTypeId)) {
					condition = EntityCondition.makeCondition(EntityOperator.AND,
							EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, enumTypeId),
	               			condition
	               			);       
				}
				
				GenericValue enumEntity = EntityUtil.getFirst( delegator.findList("Enumeration", condition, null, UtilMisc.toList("-createdStamp"), null, false) );
				if (UtilValidate.isNotEmpty(enumEntity)) {
					return enumEntity.getString("description");
				}
			}
		} catch (Exception e) {
		}
		
		return "";
	}
	
	public static String getPartyIdentificationDescription(Delegator delegator, String value) {
		
		try {
			if (UtilValidate.isNotEmpty(value)) {
				
				EntityCondition condition = EntityCondition.makeCondition(EntityOperator.OR,
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("partyIdentificationTypeId")), EntityOperator.EQUALS, value.toUpperCase()),
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("description")), EntityOperator.EQUALS, value.toUpperCase())
               			);
				
				GenericValue partyIdentificationType = EntityUtil.getFirst( delegator.findList("PartyIdentificationType", condition, null, UtilMisc.toList("-createdStamp"), null, false) );
				
				if (UtilValidate.isNotEmpty(partyIdentificationType)) {
					return partyIdentificationType.getString("description");
				}
			}
		} catch (Exception e) {
		}
		
		return "";
	}
	
	public static String numberFormat(double value) {
	    if(value < 1000) {
	        return format("###.##", value);
	    } else {
	        double hundreds = value % 1000;
	        int other = (int) (value / 1000);
	        return format(",##", other) + ',' + format("000.##", hundreds);
	    }
	}

	private static String format(String pattern, Object value) {
	    return new DecimalFormat(pattern).format(value);
	}
	
}
