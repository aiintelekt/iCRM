/**
 * 
 */
package org.groupfio.common.portal.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

/**
 * @author Sharif
 *
 */
public class UtilContactMech {

	private static final String MODULE = UtilContactMech.class.getName();
	
	public static GenericValue getPartyPostal(Delegator delegator, String partyId, String contactMechPurposeTypeId) {
		return getPartyPostal(delegator, partyId, contactMechPurposeTypeId, false);
	}
	
	public static GenericValue getPartyPostal(Delegator delegator, String partyId, String contactMechPurposeTypeId, boolean isCache) {
		try {
			if (UtilValidate.isNotEmpty(partyId)) {
				if (UtilValidate.isEmpty(contactMechPurposeTypeId)) {
					contactMechPurposeTypeId = "PRIMARY_LOCATION";
				}
				List<EntityCondition> conditionsList = new ArrayList<EntityCondition>();
				
				conditionsList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
				conditionsList.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, contactMechPurposeTypeId));
				conditionsList.add(EntityUtil.getFilterByDateExpr());

				EntityCondition mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);
				GenericValue contactMechPurpose = EntityQuery.use(delegator).select("contactMechId").from("PartyContactMechSummary").where(mainConditons).cache(isCache).queryFirst();
				if (UtilValidate.isNotEmpty(contactMechPurpose)) {
					GenericValue postalAddress = delegator.findOne("PostalAddress", UtilMisc.toMap("contactMechId", contactMechPurpose.getString("contactMechId")), isCache);
					return postalAddress;
				}
			}
		} catch (Exception e) {
		}
		return null;
	}
	
	public static String getPartyEmail(Delegator delegator, String partyId, String contactMechPurposeTypeId) {
		return getPartyEmail(delegator, partyId, contactMechPurposeTypeId, false);
	}
	
	public static String getPartyEmail(Delegator delegator, String partyId, String contactMechPurposeTypeId, boolean isCache) {
		try {
			if (UtilValidate.isNotEmpty(partyId)) {
				if (UtilValidate.isEmpty(contactMechPurposeTypeId)) {
					contactMechPurposeTypeId = "PRIMARY_EMAIL";
				}
				List<EntityCondition> conditionsList = new ArrayList<EntityCondition>();
				
				conditionsList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
				conditionsList.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, contactMechPurposeTypeId));
				conditionsList.add(EntityUtil.getFilterByDateExpr());

				EntityCondition mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);
				GenericValue contactMechPurpose = EntityQuery.use(delegator).select("contactMechId").from("PartyContactMechSummary").where(mainConditons).cache(isCache).queryFirst();
				if (UtilValidate.isNotEmpty(contactMechPurpose)) {
					GenericValue contactMech = delegator.findOne("ContactMech", UtilMisc.toMap("contactMechId", contactMechPurpose.getString("contactMechId")), isCache);
					return contactMech.getString("infoString");
				}
			}
		} catch (Exception e) {
		}
		return null;
	}
	
	public static Map<String, Object> getPartyEmailDetail(Delegator delegator, String partyId, String contactMechPurposeTypeId) {
		Map<String, Object> result = new LinkedHashMap<>();
		try {
			if (UtilValidate.isNotEmpty(partyId)) {
				if (UtilValidate.isEmpty(contactMechPurposeTypeId)) {
					contactMechPurposeTypeId = "PRIMARY_EMAIL";
				}
				List<EntityCondition> conditionsList = new ArrayList<EntityCondition>();
				
				conditionsList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
				conditionsList.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, contactMechPurposeTypeId));
				conditionsList.add(EntityUtil.getFilterByDateExpr());

				EntityCondition mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);
				GenericValue contactMechSummary = EntityQuery.use(delegator).select("contactMechId").from("PartyContactMechSummary").where(mainConditons).queryFirst();
				if (UtilValidate.isNotEmpty(contactMechSummary)) {
					result.put("email", contactMechSummary.getString("infoString"));
					result.put("contactMechId", contactMechSummary.getString("contactMechId"));
				}
			}
		} catch (Exception e) {
		}
		return result;
	}
	
	public static Map<String, Object> getPartyWebDetail(Delegator delegator, String partyId, String contactMechPurposeTypeId) {
		Map<String, Object> result = new LinkedHashMap<>();
		try {
			if (UtilValidate.isNotEmpty(partyId)) {
				if (UtilValidate.isEmpty(contactMechPurposeTypeId)) {
					contactMechPurposeTypeId = "PRIMARY_WEB_URL";
				}
				List<EntityCondition> conditionsList = new ArrayList<EntityCondition>();
				
				conditionsList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
				conditionsList.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, contactMechPurposeTypeId));
				conditionsList.add(EntityUtil.getFilterByDateExpr());

				EntityCondition mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);
				GenericValue contactMechSummary = EntityQuery.use(delegator).select("contactMechId", "infoString").from("PartyContactMechSummary").where(mainConditons).queryFirst();
				if (UtilValidate.isNotEmpty(contactMechSummary)) {
					result.put("webAddress", contactMechSummary.getString("infoString"));
					result.put("contactMechId", contactMechSummary.getString("contactMechId"));
				}
			}
		} catch (Exception e) {
		}
		return result;
	}
	
	public static String getPartyPhone(Delegator delegator, String partyId, String contactMechPurposeTypeId) {
		return getPartyPhone(delegator, partyId, contactMechPurposeTypeId, false);
	}
	
	public static String getPartyPhone(Delegator delegator, String partyId, String contactMechPurposeTypeId, boolean isCache) {
		try {
			if (UtilValidate.isNotEmpty(partyId)) {
				if (UtilValidate.isEmpty(contactMechPurposeTypeId)) {
					contactMechPurposeTypeId = "PRIMARY_PHONE";
				}
				List<EntityCondition> conditionsList = new ArrayList<EntityCondition>();
				
				conditionsList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
				conditionsList.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, contactMechPurposeTypeId));
				conditionsList.add(EntityUtil.getFilterByDateExpr());

				EntityCondition mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);
				GenericValue contactMechPurpose = EntityQuery.use(delegator).select("contactMechId").from("PartyContactMechSummary").where(mainConditons).cache(isCache).queryFirst();
				if (UtilValidate.isNotEmpty(contactMechPurpose)) {
					GenericValue primaryPhoneNumber = delegator.findOne("TelecomNumber", UtilMisc.toMap("contactMechId", contactMechPurpose.getString("contactMechId")), isCache);
					if (UtilValidate.isNotEmpty(primaryPhoneNumber)) {
						String phoneNumber = primaryPhoneNumber.getString("contactNumber");
						return phoneNumber;
					}
				}
			}
		} catch (Exception e) {
		}
		return null;
	}
	
	public static Map<String, Object> getPartyPhoneDetail(Delegator delegator, String partyId, String contactMechPurposeTypeId) {
		Map<String, Object> result = new LinkedHashMap<>();
		try {
			if (UtilValidate.isNotEmpty(partyId)) {
				if (UtilValidate.isEmpty(contactMechPurposeTypeId)) {
					contactMechPurposeTypeId = "PRIMARY_PHONE";
				}
				List<EntityCondition> conditionsList = new ArrayList<EntityCondition>();
				
				conditionsList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
				conditionsList.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, contactMechPurposeTypeId));
				conditionsList.add(EntityUtil.getFilterByDateExpr());

				EntityCondition mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);
				GenericValue contactMechSummary = EntityQuery.use(delegator).select("contactMechId").from("PartyContactMechSummary").where(mainConditons).queryFirst();
				if (UtilValidate.isNotEmpty(contactMechSummary)) {
					GenericValue phoneNumber = delegator.findOne("TelecomNumber", UtilMisc.toMap("contactMechId", contactMechSummary.getString("contactMechId")), false);
					if (UtilValidate.isNotEmpty(phoneNumber)) {
						result.put("phoneNumber", phoneNumber.getString("contactNumber"));
						result.put("contactMechId", phoneNumber.getString("contactMechId"));
					}
				}
			}
		} catch (Exception e) {
		}
		return result;
	}
	
	public static List<Map<String, Object>> getPartyEmailList(Delegator delegator, String partyId, String contactMechPurposeTypeId) {
		List<Map<String, Object>> entryList = new ArrayList<>();
		try {
			if (UtilValidate.isNotEmpty(partyId)) {
				
				/*if (UtilValidate.isEmpty(contactMechPurposeTypeId)) {
					contactMechPurposeTypeId = "PRIMARY_EMAIL";
				}*/
				
				List<EntityCondition> conditionsList = new ArrayList<EntityCondition>();
				
				conditionsList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
				conditionsList.add(EntityCondition.makeCondition("contactMechTypeId", EntityOperator.EQUALS, "EMAIL_ADDRESS"));
				
				if (UtilValidate.isNotEmpty(contactMechPurposeTypeId)) {
					conditionsList.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, contactMechPurposeTypeId));
				}
				
				conditionsList.add(EntityUtil.getFilterByDateExpr());

				EntityCondition mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);
				
				List<GenericValue> pcmList = delegator.findList("PartyContactMechSummary", mainConditons, null, null, null, false);
				if (UtilValidate.isNotEmpty(pcmList)) {
					for (GenericValue pcm : pcmList) {
						Map<String, Object> entry = new LinkedHashMap<>();
						entry.put("partyId", pcm.getString("partyId"));
						entry.put("contactMechId", pcm.getString("contactMechId"));
						entry.put("contactMechPurposeTypeId", pcm.getString("contactMechPurposeTypeId"));
						entry.put("purposeTypeDesc", DataUtil.getContactMechPurposeTypeDescription(delegator, pcm.getString("contactMechPurposeTypeId")));
						entry.put("infoString", pcm.getString("infoString"));
						
						entryList.add(entry);
					}
				}
			}
		} catch (Exception e) {
		}
		return entryList;
	}
	
	public static String evalutePartyPostal(Map<String, Object> context) {
		try {
			if (UtilValidate.isNotEmpty(context) && UtilValidate.isNotEmpty(context.get("partyId"))) {
				
				Map<String, Object> callCtxt = FastMap.newInstance();
				Map<String, Object> callResult = FastMap.newInstance();
				
				Delegator delegator = (Delegator) context.get("delegator");
				LocalDispatcher dispatcher = (LocalDispatcher) context.get("dispatcher");
				GenericValue userLogin = (GenericValue) context.get("userLogin");
				
				String partyId = (String) context.get("partyId");
				String isFullAddressCheck = (String) context.get("isFullAddressCheck"); 
			
				String addressContactMechId = (String) context.get("addressContactMechId");
				if(UtilValidate.isNotEmpty(addressContactMechId) && "N".equals(isFullAddressCheck)) {
					context.put("contactMechId", addressContactMechId);
					callResult = dispatcher.runSync("updatePartyPostalAddress", context);
					if (!ServiceUtil.isError(callResult)) {
	                    return (String) callResult.get("contactMechId");
	                }
				}
				
				if (!isPostalAddressFound(context)) {
					
					String address1 = (String) context.get("address1");
					String address2 = (String) context.get("address2");
					String countryGeoId = (String) context.get("countryGeoId");
					String stateGeoId = (String) context.get("stateGeoId");
					String city = (String) context.get("city");
					
					String county = (String) context.get("county");
					String isBusiness = (String) context.get("isBusiness");
					String isVacant = (String) context.get("isVacant");
					String isUspsAddrVerified = (String) context.get("isUspsAddrVerified");
					String latitude = (String) context.get("latitude");
					String longitude = (String) context.get("longitude");
					
					String zip5 = (String) context.get("zip5");
					String zip4 = (String) context.get("zip4");
					
					callCtxt = FastMap.newInstance();
					
					callCtxt.put("partyId", partyId);
					callCtxt.put("contactMechPurposeTypeId", "PRIMARY_LOCATION");
					
					callCtxt.put("address1", address1);
					callCtxt.put("address2", address2);
					callCtxt.put("countryGeoId", countryGeoId);
					callCtxt.put("stateProvinceGeoId", stateGeoId);
					callCtxt.put("city", city);
					callCtxt.put("county", county);
					callCtxt.put("postalCode", zip5);
					callCtxt.put("postalCodeExt", zip4);
					
					callCtxt.put("isBusiness", isBusiness);
					callCtxt.put("isVacant", isVacant);
					callCtxt.put("isUspsAddrVerified", isUspsAddrVerified);
					callCtxt.put("latitude", latitude);
					callCtxt.put("longitude", longitude);
					
					callCtxt.put("userLogin", userLogin);
					
					callResult = dispatcher.runSync("createPartyPostalAddress", callCtxt);
	                if (!ServiceUtil.isError(callResult)) {
	                    return (String) callResult.get("contactMechId");
	                }
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return null;
	}
	
	public static boolean isPostalAddressFound(Map<String, Object> context) {
		boolean isPostalFound = false;
		try {
			if (UtilValidate.isNotEmpty(context)) {
				
				Delegator delegator = (Delegator) context.get("delegator");
				LocalDispatcher dispatcher = (LocalDispatcher) context.get("dispatcher");
				GenericValue userLogin = (GenericValue) context.get("userLogin");
				
				String partyId = (String) context.get("partyId");
				
				String address1 = (String) context.get("address1");
				String address2 = (String) context.get("address2");
				String countryGeoId = (String) context.get("countryGeoId");
				String stateGeoId = (String) context.get("stateGeoId");
				String city = (String) context.get("city");
				
				String county = (String) context.get("county");
				
				String zip5 = (String) context.get("zip5");
				String zip4 = (String) context.get("zip4");
				
				Map<String, Object> inputMap = new LinkedHashMap<>();
				inputMap.put("address1", address1);
				inputMap.put("address2", address2);
				inputMap.put("countryGeoId", countryGeoId);
				inputMap.put("stateGeoId", stateGeoId);
				inputMap.put("city", city);
				if (UtilValidate.isNotEmpty(county)) {
					inputMap.put("county", county);
				}
				inputMap.put("zip5", zip5);
				if (UtilValidate.isNotEmpty(zip4)) {
					inputMap.put("zip4", zip4);
				}
				
				List conditions = FastList.newInstance();
				
				conditions.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
				conditions.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "PRIMARY_LOCATION"));
				
				conditions.add(EntityUtil.getFilterByDateExpr());
				
                EntityCondition mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);
                List<GenericValue> mechPurposeList = delegator.findList("PartyContactMechPurpose", mainConditons, null, null, null, false);
    			if (UtilValidate.isNotEmpty(mechPurposeList)) {
    				for (GenericValue mechPurpose : mechPurposeList) {
    					
    					String contactMechId = mechPurpose.getString("contactMechId");
    					
    					conditions = FastList.newInstance();
    					conditions.add(EntityCondition.makeCondition("contactMechId", EntityOperator.EQUALS, contactMechId));
    					
    					mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);
    	                GenericValue postalAddress = EntityQuery.use(delegator).from("PostalAddress").where(mainConditons).queryFirst();
    	    			if (UtilValidate.isNotEmpty(postalAddress)) {
    	    				Map<String, Object> outputMap = new LinkedHashMap<>();
    	    				outputMap.put("address1", UtilValidate.isNotEmpty(postalAddress.getString("address1")) ? postalAddress.getString("address1") : "");
    	    				outputMap.put("address2", UtilValidate.isNotEmpty(postalAddress.getString("address2")) ? postalAddress.getString("address2") : "");
    	    				outputMap.put("countryGeoId", UtilValidate.isNotEmpty(postalAddress.getString("countryGeoId")) ? postalAddress.getString("countryGeoId") : "");
    	    				outputMap.put("stateGeoId", UtilValidate.isNotEmpty(postalAddress.getString("stateProvinceGeoId")) ? postalAddress.getString("stateProvinceGeoId") : "");
    	    				outputMap.put("city", UtilValidate.isNotEmpty(postalAddress.getString("city")) ? postalAddress.getString("city") : "");
    	    				if (UtilValidate.isNotEmpty(county)) {
    	    					outputMap.put("county", UtilValidate.isNotEmpty(postalAddress.getString("county")) ? postalAddress.getString("county") : "");
    	    				}
    	    				outputMap.put("zip5", UtilValidate.isNotEmpty(postalAddress.getString("postalCode")) ? postalAddress.getString("postalCode") : "");
    	    				if (UtilValidate.isNotEmpty(zip4)) {
    	    					outputMap.put("zip4", UtilValidate.isNotEmpty(postalAddress.getString("postalCodeExt")) ? postalAddress.getString("postalCodeExt") : "");
    	    				}
    	    				
    	    				if (inputMap.equals(outputMap)) {
    	    					isPostalFound = true;
    	    					break;
    	    				}
    	    			}
    				};
    			}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return isPostalFound;
	}
	
	public static List<Map<String, Object>> getPartyListFromEmails(Delegator delegator, List<String> emailList) {
		List<Map<String, Object>> entryList = new ArrayList<>();
		try {
			if (UtilValidate.isNotEmpty(emailList)) {
				for (String emailAddress : emailList) {
					Map<String, Object> party = new LinkedHashMap<>();
					String partyId = org.fio.homeapps.util.DataUtil.getPartyIdByEmail(delegator, emailAddress);
					
					party.put("partyId", partyId);
					party.put("emailAddress", emailAddress);
					party.put("partyName", org.fio.homeapps.util.PartyHelper.getPartyName(delegator, partyId, false));
					
					entryList.add(party);
				}
			}
		} catch (Exception e) {
		}
		return entryList;
	}
	
}
