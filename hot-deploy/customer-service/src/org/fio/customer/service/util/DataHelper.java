/**
 * 
 */
package org.fio.customer.service.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.fio.homeapps.util.DataUtil;
import org.ofbiz.base.crypto.HashCrypt;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.model.ModelUtil;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceContainer;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;
/***
 * 
 * @author Mahendran T
 *
 */
public class DataHelper {
	
	private static String MODULE = DataHelper.class.getName();
	
	@SuppressWarnings("unchecked")
	public static String getPrimaryContactId(Delegator delegator, Map<String, Object> context) {
		LocalDispatcher dispatcher = ServiceContainer.getLocalDispatcher(delegator.getDelegatorName(), delegator);
		String contactId = "";
		try {
			Map<String, Object> serResult = dispatcher.runSync("common.getContactAndPartyAssoc", context);
			
			if (UtilValidate.isNotEmpty(serResult) && ServiceUtil.isSuccess(serResult)) {
				List<Object> primaryContactList = FastList.newInstance();
				
				primaryContactList = (List<Object>) serResult.get("partyContactAssoc");
				
				for (int i = 0; i < primaryContactList.size(); i++) {
					Map<String, Object> partyContactMap = new HashMap<String, Object>();
					partyContactMap = (Map<String, Object>) primaryContactList.get(i);
					
					String primaryContactStatusId = (String) partyContactMap.get("statusId");
					
					if ("PARTY_DEFAULT".equals(primaryContactStatusId)) {
						contactId = (String) partyContactMap.get("contactId");
					}
				}
				
				if(UtilValidate.isEmpty(contactId)) {
					Map<String, Object> partyContactMap = (Map<String, Object>) primaryContactList.get(0);
					contactId = (String) partyContactMap.get("contactId");
				}		
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return contactId;
		
	}
	
	public static String createSalesOpportunityRole(Delegator delegator, Map<String, Object> context) {
		
		try {
			String salesOpportunityId = (String) context.get("salesOpportunityId");
			String partyId = (String) context.get("partyId");
			String roleTypeId = (String) context.get("roleTypeId");
			GenericValue salesOppoRole = EntityQuery.use(delegator).from("SalesOpportunityRole").where("salesOpportunityId",salesOpportunityId, "partyId", partyId, "roleTypeId", roleTypeId).queryFirst();
			if(UtilValidate.isNotEmpty(salesOppoRole)) {
				salesOppoRole.setNonPKFields(context);
				salesOppoRole.store();
			} else {
				salesOppoRole = delegator.makeValue("SalesOpportunityRole");
				salesOppoRole.setPKFields(context);
				salesOppoRole.setNonPKFields(context);
				salesOppoRole.create();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}
		return "success";
	}
	
	public static Map<String, Object> createPartyIdentification(Delegator delegator, Map<String, Object> context) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			String partyId = (String) context.get("partyId");
			String idValue = (String) context.get("idValue");
			String partyIdentifyTypeId= (String) context.get("partyIdentificationTypeId");
			
			if(UtilValidate.isNotEmpty(partyId) && UtilValidate.isNotEmpty(partyIdentifyTypeId) && UtilValidate.isNotEmpty(idValue)) {
				GenericValue partyIdentificationGv = EntityQuery.use(delegator).from("PartyIdentification").where("partyId", partyId, "partyIdentificationTypeId", partyIdentifyTypeId).queryFirst();
				if(UtilValidate.isNotEmpty(partyIdentificationGv)) {
					partyIdentificationGv.set("idValue", idValue);
					partyIdentificationGv.store();
				} else {
					partyIdentificationGv = delegator.makeValue("PartyIdentification", UtilMisc.toMap("partyId", partyId, "partyIdentificationTypeId", partyIdentifyTypeId, "idValue", idValue));
					partyIdentificationGv.create();
				}
			}
			result = ServiceUtil.returnSuccess();
			
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}
	
	public static Map<String, Object> createPartyMetricIndicator(Delegator delegator, Map<String, Object> context){
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			String groupId = (String) context.get("groupId");
			String customFieldId = (String) context.get("customFieldId");
			String partyId= (String) context.get("partyId");
			String groupingCode= (String) context.get("groupingCode");
			String propertyName= (String) context.get("propertyName");
			String propertyValue= (String) context.get("propertyValue");
			//long sequenceNumber= UtilValidate.isNotEmpty(context.get("sequenceNumber")) ? (long) context.get("sequenceNumber") : 1;	
			
			GenericValue partyMetricIndicator = EntityQuery.use(delegator).from("PartyMetricIndicator").where("groupId", groupId, "customFieldId", customFieldId, "partyId", partyId).queryFirst();
			if(UtilValidate.isNotEmpty(partyMetricIndicator)) {
				partyMetricIndicator.set("propertyValue", propertyValue);
				partyMetricIndicator.store();
			} else {
				partyMetricIndicator = delegator.makeValue("PartyMetricIndicator");
				partyMetricIndicator.setPKFields(context);
				partyMetricIndicator.setNonPKFields(context);
				partyMetricIndicator.create();
			}
			
			result = ServiceUtil.returnSuccess();
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}
	public static Map<String, Object> createPartyAttribute(Delegator delegator, Map<String, Object> context){
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			String attrName = (String) context.get("attrName");
			String attrValue = (String) context.get("attrValue");
			String partyId= (String) context.get("partyId");;
			
			GenericValue partyAttribute = EntityQuery.use(delegator).from("PartyAttribute").where("partyId", partyId, "attrName", attrName).queryFirst();
			if(UtilValidate.isNotEmpty(partyAttribute)) {
				partyAttribute.set("attrValue", attrValue);
				partyAttribute.store();
			} else {
				partyAttribute = delegator.makeValue("PartyAttribute");
				partyAttribute.setPKFields(context);
				partyAttribute.setNonPKFields(context);
				partyAttribute.create();
			}
			
			result = ServiceUtil.returnSuccess();
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}
	
	public static String getParentCustName(Delegator delegator, String partyIdFrom) {
		
		String name = "";
		try {
			EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
					EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, partyIdFrom),
					EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "ACCOUNT"),
					EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "REL_PARENT_ACCOUNT")
					);
			
			GenericValue partyRelationship = EntityQuery.use(delegator).from("PartyRelationship")
					.where(condition).queryFirst();
			if(UtilValidate.isNotEmpty(partyRelationship)) {
				String partyIdTo = partyRelationship.getString("partyIdTo");
				name = DataUtil.getPartyName(delegator, partyIdTo);
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return name;
	}
	
	public static Map<String, Object> getCustParentNameList(Delegator delegator, List<GenericValue> dataList, String fieldId){
		Map<String, Object> results = new HashMap<>();
		try {
			if (UtilValidate.isNotEmpty(dataList)) {
				List<String> typeIds = EntityUtil.getFieldListFromEntityList(dataList, fieldId, true).stream()
						.map(x->x.toString()).collect(Collectors.toList());
				results = typeIds.stream().filter(x->UtilValidate.isNotEmpty(x)).collect(Collectors.toMap(x -> x.toString(),
						x -> {
							String val = getParentCustName(delegator, x);
							if (UtilValidate.isEmpty(val)) {
								val = "";
							}
							return UtilValidate.isNotEmpty(x) ? val : "";
						},
						(attr1, attr2) -> {
							return attr2;
						}));
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return results;
	}
	
	
	public static String getCustPrimPhone(Delegator delegator, String partyId) {
		String phoneNo = "";
		try {
			DynamicViewEntity dynamicView = new DynamicViewEntity();
			dynamicView.addMemberEntity("PCM", "PartyContactMech");
			dynamicView.addAlias("PCM", "partyId");
			dynamicView.addAlias("PCM", "contactMechId");
			dynamicView.addAlias("PCM", "fromDate");
			dynamicView.addAlias("PCM", "thruDate");
			dynamicView.addMemberEntity("PCMP", "PartyContactMechPurpose");
			dynamicView.addAlias("PCMP", "contactMechPurposeTypeId");
			dynamicView.addViewLink("PCM", "PCMP", Boolean.FALSE, ModelKeyMap.makeKeyMapList("contactMechId"));
			dynamicView.addMemberEntity("TN", "TelecomNumber");
			dynamicView.addAlias("TN", "contactNumber");
			dynamicView.addViewLink("PCM", "TN", Boolean.FALSE, ModelKeyMap.makeKeyMapList("contactMechId"));
			
			EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
					EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId),
					EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "PRIMARY_PHONE")
					);
			
			GenericValue telecomNumber = EntityQuery.use(delegator).from(dynamicView).where(condition).filterByDate().queryFirst();
			if(UtilValidate.isNotEmpty(telecomNumber))
				phoneNo = telecomNumber.getString("contactNumber");
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return phoneNo;
	}
	
	public static String getPartyIdByEmail(Delegator delegator, String emailId) {
		String partyId = "";
		try {
			DynamicViewEntity dynamicView = new DynamicViewEntity();
			dynamicView.addMemberEntity("PCM", "PartyContactMech");
			dynamicView.addAlias("PCM", "partyId");
			dynamicView.addAlias("PCM", "contactMechId");
			dynamicView.addAlias("PCM", "fromDate");
			dynamicView.addAlias("PCM", "thruDate");
			dynamicView.addMemberEntity("PCMP", "PartyContactMechPurpose");
			dynamicView.addAlias("PCMP", "contactMechPurposeTypeId");
			dynamicView.addViewLink("PCM", "PCMP", Boolean.FALSE, ModelKeyMap.makeKeyMapList("contactMechId"));
			dynamicView.addMemberEntity("CM", "ContactMech");
			dynamicView.addAlias("CM", "infoString");
			dynamicView.addViewLink("PCM", "CM", Boolean.FALSE, ModelKeyMap.makeKeyMapList("contactMechId"));
			
			EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
					EntityCondition.makeCondition("infoString", EntityOperator.EQUALS, emailId),
					EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "PRIMARY_EMAIL")
					);
			
			GenericValue emailAddress = EntityQuery.use(delegator).from(dynamicView).where(condition).filterByDate().queryFirst();
			if(UtilValidate.isNotEmpty(emailAddress))
				partyId = emailAddress.getString("partyId");
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return partyId;
	}
	
	public static String getCustPrimEmail(Delegator delegator, String partyId) {
		String emailId = "";
		try {
			DynamicViewEntity dynamicView = new DynamicViewEntity();
			dynamicView.addMemberEntity("PCM", "PartyContactMech");
			dynamicView.addAlias("PCM", "partyId");
			dynamicView.addAlias("PCM", "contactMechId");
			dynamicView.addAlias("PCM", "fromDate");
			dynamicView.addAlias("PCM", "thruDate");
			dynamicView.addMemberEntity("PCMP", "PartyContactMechPurpose");
			dynamicView.addAlias("PCMP", "contactMechPurposeTypeId");
			dynamicView.addViewLink("PCM", "PCMP", Boolean.FALSE, ModelKeyMap.makeKeyMapList("contactMechId"));
			dynamicView.addMemberEntity("CM", "ContactMech");
			dynamicView.addAlias("CM", "infoString");
			dynamicView.addViewLink("PCM", "CM", Boolean.FALSE, ModelKeyMap.makeKeyMapList("contactMechId"));
			
			EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
					EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId),
					EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "PRIMARY_EMAIL")
					);
			
			GenericValue emailAddress = EntityQuery.use(delegator).from(dynamicView).where(condition).filterByDate().queryFirst();
			if(UtilValidate.isNotEmpty(emailAddress))
				emailId = emailAddress.getString("infoString");
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return emailId;
	}
	
	public static Map<String, Object> getCustPrimPhoneList(Delegator delegator, List<GenericValue> dataList, String fieldId){
		Map<String, Object> results = new HashMap<>();
		try {
			if (UtilValidate.isNotEmpty(dataList)) {
				List<String> typeIds = EntityUtil.getFieldListFromEntityList(dataList, fieldId, true).stream()
						.map(x->x.toString()).collect(Collectors.toList());
				results = typeIds.stream().filter(x->UtilValidate.isNotEmpty(x)).collect(Collectors.toMap(x -> x.toString(),
						x -> {
							String val = getCustPrimPhone(delegator, x);
							if (UtilValidate.isEmpty(val)) {
								val = "";
							}
							return UtilValidate.isNotEmpty(x) ? val : "";
						},
						(attr1, attr2) -> {
							return attr2;
						}));
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return results;
	}
	
	
	public static String getPartyAttribute(Delegator delegator, String partyId, String attrName) {
		String attrValue = "";
		try {
			GenericValue partyAttribute = EntityQuery.use(delegator).from("PartyAttribute").where("partyId", partyId, "attrName", attrName).queryFirst();
			if(UtilValidate.isNotEmpty(partyAttribute))
				attrValue = partyAttribute.getString("attrValue");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return attrValue;
	}
	public static Map<String, Object> getPartyAttributeList(Delegator delegator, List<GenericValue> dataList, String fieldId, String attrName){
		Map<String, Object> results = new HashMap<>();
		try {
			if (UtilValidate.isNotEmpty(dataList)) {
				List<String> typeIds = EntityUtil.getFieldListFromEntityList(dataList, fieldId, true).stream()
						.map(x->x.toString()).collect(Collectors.toList());
				results = typeIds.stream().filter(x->UtilValidate.isNotEmpty(x)).collect(Collectors.toMap(x -> x.toString(),
						x -> {
							String val = getPartyAttribute(delegator, x, attrName);
							if (UtilValidate.isEmpty(val)) {
								val = "";
							}
							return UtilValidate.isNotEmpty(x) ? val : "";
						},
						(attr1, attr2) -> {
							return attr2;
						}));
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return results;
	}
	
	public static String getPartyRelAssocId(Delegator delegator, String accountPartyId, String contactPartyId) {
		String relAssocId = "";
		try {
			EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
					EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, contactPartyId),
					EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, accountPartyId),
					EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "CONTACT"),
					EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "ACCOUNT"),
					EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "CONTACT_REL_INV")
					);
			GenericValue partyReletionship = EntityQuery.use(delegator).from("PartyRelationship").where(condition).queryFirst();
			if(UtilValidate.isNotEmpty(partyReletionship)) {
				relAssocId = partyReletionship.getString("partyRelAssocId");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
				
		return relAssocId;
	}
	
	@SuppressWarnings("serial")
	public static List<Map<String, Object>> getCustomerRefList(Delegator delegator, String partyId, String skipIdentificationTypeId){
		List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
		try {
			List<EntityCondition> conditionList = new ArrayList<EntityCondition>();
			if(UtilValidate.isNotEmpty(partyId)) {
				conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
				List<String> ignoreTypeIds = new ArrayList<String>();
				if(UtilValidate.isNotEmpty(skipIdentificationTypeId)) {
					ignoreTypeIds = skipIdentificationTypeId.contains(",") ? org.fio.admin.portal.util.DataUtil.stringToList(skipIdentificationTypeId, "") : new ArrayList<String>() {{add(skipIdentificationTypeId);}};
					conditionList.add(EntityCondition.makeCondition("partyIdentificationTypeId", EntityOperator.NOT_IN, ignoreTypeIds));
				}
				EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				List<GenericValue> partyIdentificationTypeList = EntityQuery.use(delegator).from("PartyIdentification").where(condition).queryList();
				if(UtilValidate.isNotEmpty(partyIdentificationTypeList)) {
					for(GenericValue partyIdentificationType : partyIdentificationTypeList) {
						String typeId = partyIdentificationType.getString("partyIdentificationTypeId");
						String idValue = partyIdentificationType.getString("idValue");
						result.add(UtilMisc.toMap("alt_ref_name", typeId, "alt_ref_id", idValue));
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static List<Map<String, Object>> getAddressInfo(Delegator delegator, String partyId, String purposeTypeId){
		List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
		try {
			if(UtilValidate.isNotEmpty(partyId)) {
				DynamicViewEntity dynamicView = new DynamicViewEntity();
				dynamicView.addMemberEntity("PCM", "PartyContactMech");
				dynamicView.addAlias("PCM", "partyId");
				dynamicView.addAlias("PCM", "contactMechId");
				dynamicView.addAlias("PCM", "pcmFromDate", "fromDate","",false, false, null);
				dynamicView.addAlias("PCM", "pcmThruDate", "thruDate","",false, false, null);
				
				dynamicView.addMemberEntity("PCMP", "PartyContactMechPurpose");
				dynamicView.addAlias("PCMP", "contactMechPurposeTypeId");
				dynamicView.addAlias("PCMP", "pcmpFromDate", "fromDate","",false, false, null);
				dynamicView.addAlias("PCMP", "pcmpThruDate", "thruDate","",false, false, null);
				dynamicView.addViewLink("PCM", "PCMP", Boolean.FALSE, ModelKeyMap.makeKeyMapList("contactMechId"));
				
				dynamicView.addMemberEntity("PA", "PostalAddress");
				dynamicView.addAlias("PA", "toName");
				dynamicView.addAlias("PA", "attnName");
				dynamicView.addAlias("PA", "address1");
				dynamicView.addAlias("PA", "address2");
				dynamicView.addAlias("PA", "city");
				dynamicView.addAlias("PA", "postalCode");
				dynamicView.addAlias("PA", "countryGeoId");
				dynamicView.addAlias("PA", "stateProvinceGeoId");
				dynamicView.addAlias("PA", "phoneContactMechId");
				dynamicView.addAlias("PA", "addressValidInd");
				dynamicView.addViewLink("PCM", "PA", Boolean.FALSE, ModelKeyMap.makeKeyMapList("contactMechId"));
				
				List<EntityCondition> conditionList = new ArrayList<EntityCondition>();
				conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
				if(UtilValidate.isNotEmpty(purposeTypeId)) {
					conditionList.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, purposeTypeId));	
				}
				EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				List<GenericValue> postalAddressList = EntityQuery.use(delegator).from(dynamicView).where(condition).filterByDate("pcmFromDate","pcmThruDate").queryList();
				//GenericValue postalAddress = EntityQuery.use(delegator).from(dynamicView).where(condition).filterByDate("pcmFromDate","pcmThruDate").queryFirst();
				if(UtilValidate.isNotEmpty(postalAddressList)) {
					for(GenericValue postalAddress : postalAddressList) {
						Map<String, Object> data = new LinkedHashMap<String, Object>();
						data.put("cust_addr1", postalAddress.getString("address1"));
						data.put("cust_addr2", postalAddress.getString("address2"));
						data.put("cust_city", postalAddress.getString("city"));
						data.put("cust_state", postalAddress.getString("stateProvinceGeoId"));
						data.put("cust_zip", postalAddress.getString("postalCode"));
						data.put("cust_addr1", postalAddress.getString("address1"));
						
						//data.putAll(org.fio.admin.portal.util.DataUtil.convertGenericToMap(postalAddress));
						
						String phoneContactMechId = postalAddress.getString("phoneContactMechId");
						if(UtilValidate.isNotEmpty(phoneContactMechId)) {
							GenericValue telecom = EntityQuery.use(delegator).from("TelecomNumber").where("contactMechId", phoneContactMechId).queryFirst();
							if(UtilValidate.isNotEmpty(telecom)) {
								data.put("cust_phone", UtilValidate.isNotEmpty(telecom.getString("contactNumber")) ? org.fio.admin.portal.util.DataUtil.formatPhoneNumber(telecom.getString("contactNumber"), "$1-$2-$3") : "");
							}
						}
						if("BILLING_LOCATION".equals(purposeTypeId)) {
							data.put("cust_addr_valid", postalAddress.getString("addressValidInd"));
						}
						result.add(data);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static List<Map<String, Object>> getCustContactInfo(Delegator delegator, String partyId, String purposeTypeId){
		List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
		try {
			if(UtilValidate.isNotEmpty(partyId)) {
				String roleTypeId = org.fio.admin.portal.util.DataUtil.getPartySecurityRole(delegator, partyId);
				EntityCondition relationshipCon = EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, partyId),
						EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, roleTypeId),
						EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "CONTACT"),
						EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "CONTACT_REL_INV"),
						EntityUtil.getFilterByDateExpr("fromDate", "thruDate")
						);
				List<GenericValue> accountContactList = EntityQuery.use(delegator).from("PartyRelationship").where(relationshipCon).queryList();
				
				List<String> contactPartyIds = UtilValidate.isNotEmpty(accountContactList) ? EntityUtil.getFieldListFromEntityList(accountContactList, "partyIdFrom", true) : new ArrayList<String>();
				if(UtilValidate.isNotEmpty(contactPartyIds)) {
					DynamicViewEntity dynamicView = new DynamicViewEntity();
					dynamicView.addMemberEntity("P", "Party");
					dynamicView.addAlias("P", "partyId", "partyId", null, false, true, null);
					dynamicView.addAlias("P", "statusId");
					dynamicView.addAlias("P", "roleTypeId");
					
					dynamicView.addMemberEntity("PER", "Person");
					dynamicView.addAlias("PER", "firstName");
					dynamicView.addAlias("PER", "middleName");
					dynamicView.addAlias("PER", "lastName");
					dynamicView.addAlias("PER", "designation");
					dynamicView.addViewLink("P", "PER", Boolean.FALSE, ModelKeyMap.makeKeyMapList("partyId"));
					
					
					List<EntityCondition> conditionList = new ArrayList<EntityCondition>();
					conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, contactPartyIds));
					
					conditionList.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PARTY_DISABLED"));
					
					EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
					
					List<GenericValue> contactList = EntityQuery.use(delegator).from(dynamicView).where(condition).queryList();
					for(GenericValue contact : contactList) {
						String contactPartyId = contact.getString("partyId");
						Map<String, Object> data = new LinkedHashMap<String, Object>();
						String cntName = contact.getString("firstName") + (UtilValidate.isNotEmpty(contact.getString("lastName")) ? contact.getString("lastName") : "");
						data.put("cnt_name", cntName);
						data.put("cnt_email", getCustPrimEmail(delegator, contactPartyId));
						data.put("cnt_designation", contact.getString("designation"));
						String contactPhn = getCustPrimPhone(delegator, contactPartyId);
						data.put("cnt_phone", UtilValidate.isNotEmpty(contactPhn) ? org.fio.admin.portal.util.DataUtil.formatPhoneNumber(contactPhn, "$1-$2-$3") : "");
						data.put("cnt_role", contact.getString("roleTypeId"));
						//data.putAll(org.fio.admin.portal.util.DataUtil.convertGenericToMap(contact));
						result.add(data);
					}
				}
				
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static List<Map<String, Object>> getCustCustomData(Delegator delegator, String partyId, String groupId){
		List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
		try {
			if(UtilValidate.isNotEmpty(partyId)) {
				String roleTypeId = org.fio.admin.portal.util.DataUtil.getPartySecurityRole(delegator, partyId);
				DynamicViewEntity dynamicView = new DynamicViewEntity();
				dynamicView.addMemberEntity("CF", "CustomField");
				dynamicView.addAlias("CF", "customFieldId");
				dynamicView.addAlias("CF", "groupId");
				dynamicView.addAlias("CF", "groupType");
				dynamicView.addAlias("CF", "isEnabled");
				dynamicView.addAlias("CF", "sequenceNumber");
				
				dynamicView.addMemberEntity("CFV", "CustomFieldValue");
				dynamicView.addAlias("CFV", "partyId");
				dynamicView.addAlias("CFV", "fieldValue");
				dynamicView.addAlias("CFV", "fromDate");
				dynamicView.addAlias("CFV", "thruDate");
				dynamicView.addViewLink("CF", "CFV", Boolean.FALSE, ModelKeyMap.makeKeyMapList("customFieldId"));
				
				
				List<EntityCondition> conditionList = new ArrayList<EntityCondition>();
				conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
				if(UtilValidate.isNotEmpty(groupId))
					conditionList.add(EntityCondition.makeCondition("groupId", EntityOperator.EQUALS, groupId));
				conditionList.add(EntityCondition.makeCondition("groupType", EntityOperator.EQUALS, "CUSTOM_FIELD"));
				conditionList.add(EntityCondition.makeCondition(EntityOperator.OR,
						EntityCondition.makeCondition("isEnabled", EntityOperator.EQUALS, "Y"),
						EntityCondition.makeCondition("isEnabled", EntityOperator.EQUALS, null)));
				
				
				EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				
				List<GenericValue> customerCustomDataList = EntityQuery.use(delegator).from(dynamicView).where(condition).filterByDate("fromDate","thruDate").orderBy("sequenceNumber ASC").queryList();
				for(GenericValue customerCustomData : customerCustomDataList) {
					Map<String, Object> data = new LinkedHashMap<String, Object>();
					//data.putAll(org.fio.admin.portal.util.DataUtil.convertGenericToMap(customerCustomData));
					data.put("param_name", customerCustomData.getString("customFieldId"));
					data.put("param_value", customerCustomData.getString("fieldValue"));
					result.add(data);
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static Map<String, Object> getCustEconomicData(Delegator delegator, String partyId, String groupId){
		Map<String, Object> result = new LinkedHashMap<String, Object>();
		try {
			if(UtilValidate.isNotEmpty(partyId)) {
				DynamicViewEntity dynamicView = new DynamicViewEntity();
				dynamicView.addMemberEntity("CF", "CustomField");
				dynamicView.addAlias("CF", "customFieldId");
				dynamicView.addAlias("CF", "groupId");
				dynamicView.addAlias("CF", "groupType");
				dynamicView.addAlias("CF", "isEnabled");
				dynamicView.addAlias("CF", "sequenceNumber");
				
				dynamicView.addMemberEntity("PMI", "PartyMetricIndicator");
				dynamicView.addAlias("PMI", "partyId");
				dynamicView.addAlias("PMI", "groupingCode");
				dynamicView.addAlias("PMI", "propertyName");
				dynamicView.addAlias("PMI", "propertyValue");
				dynamicView.addViewLink("CF", "PMI", Boolean.FALSE, ModelKeyMap.makeKeyMapList("customFieldId","customFieldId","groupId","groupId"));
				
				
				List<EntityCondition> conditionList = new ArrayList<EntityCondition>();
				conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
				conditionList.add(EntityCondition.makeCondition("groupId", EntityOperator.EQUALS, groupId));
				conditionList.add(EntityCondition.makeCondition("groupType", EntityOperator.EQUALS, "ECONOMIC_METRIC"));
				//conditionList.add(EntityCondition.makeCondition("groupingCode", EntityOperator.EQUALS, "CUSTOMER_BAL_METRICS"));
				conditionList.add(EntityCondition.makeCondition(EntityOperator.OR,
						EntityCondition.makeCondition("isEnabled", EntityOperator.EQUALS, "Y"),
						EntityCondition.makeCondition("isEnabled", EntityOperator.EQUALS, null)));
				
				
				EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				
				List<GenericValue> customerMetricDataList = EntityQuery.use(delegator).from(dynamicView).where(condition).orderBy("sequenceNumber ASC").queryList();
				if(UtilValidate.isNotEmpty(customerMetricDataList)){
					result.putAll(org.fio.admin.portal.util.DataUtil.getMapFromGeneric(customerMetricDataList, "propertyName", "propertyValue", false));
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static List<Map<String, Object>> getSocialRefList(Delegator delegator, String partyId, String contactMechTypeId){
		List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
		try {
			if(UtilValidate.isNotEmpty(partyId)) {
				if(UtilValidate.isNotEmpty(partyId)) {
					DynamicViewEntity dynamicView = new DynamicViewEntity();
					dynamicView.addMemberEntity("PCM", "PartyContactMech");
					dynamicView.addAlias("PCM", "partyId");
					dynamicView.addAlias("PCM", "contactMechId");
					dynamicView.addAlias("PCM", "pcmFromDate", "fromDate","",false, false, null);
					dynamicView.addAlias("PCM", "pcmThruDate", "thruDate","",false, false, null);
					
					dynamicView.addMemberEntity("PCMP", "PartyContactMechPurpose");
					dynamicView.addAlias("PCMP", "contactMechPurposeTypeId");
					dynamicView.addAlias("PCMP", "pcmpFromDate", "fromDate","",false, false, null);
					dynamicView.addAlias("PCMP", "pcmpThruDate", "thruDate","",false, false, null);
					dynamicView.addViewLink("PCM", "PCMP", Boolean.FALSE, ModelKeyMap.makeKeyMapList("contactMechId"));
					
					dynamicView.addMemberEntity("CM", "ContactMech");
					dynamicView.addAlias("CM", "infoString");
					dynamicView.addAlias("CM", "contactMechTypeId");
					dynamicView.addViewLink("PCM", "CM", Boolean.FALSE, ModelKeyMap.makeKeyMapList("contactMechId"));
					
					List<EntityCondition> conditionList = new ArrayList<EntityCondition>();
					conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
					if(UtilValidate.isNotEmpty(contactMechTypeId)) {
						conditionList.add(EntityCondition.makeCondition("contactMechTypeId", EntityOperator.EQUALS, contactMechTypeId));	
					}
					EntityCondition condition = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
					List<GenericValue> socialAddressList = EntityQuery.use(delegator).from(dynamicView).where(condition).filterByDate("pcmFromDate","pcmThruDate").queryList();
					//GenericValue postalAddress = EntityQuery.use(delegator).from(dynamicView).where(condition).filterByDate("pcmFromDate","pcmThruDate").queryFirst();
					if(UtilValidate.isNotEmpty(socialAddressList)) {
						for(GenericValue socialRef : socialAddressList) {
							Map<String, Object> data = new LinkedHashMap<String, Object>();
							String contactMechPurposeTypeId = socialRef.getString("contactMechPurposeTypeId");
							if(UtilValidate.isNotEmpty(contactMechPurposeTypeId)) {
								GenericValue contactMechPurposeType = EntityQuery.use(delegator).from("ContactMechPurposeType").where("contactMechPurposeTypeId", contactMechPurposeTypeId).queryFirst();
								
								if(UtilValidate.isNotEmpty(contactMechPurposeType)) {
									String refName = contactMechPurposeType.getString("description");
									data.put("ref_name", UtilValidate.isNotEmpty(refName) ? refName.toLowerCase() : "");
									data.put("ref_val", socialRef.getString("infoString"));
									result.add(data);
								}
							}
							
							
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static boolean validateCustAddressData(List<String> requiredFields, Map<String, Object> requestData) {
		boolean isValid = true;
		String errorList = "";
		try {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("custAddr1", UtilValidate.isNotEmpty(requestData.get("cust_addr1")) ? (String) requestData.get("cust_addr1") : "");
			data.put("custAddr2", UtilValidate.isNotEmpty(requestData.get("cust_addr2")) ? (String) requestData.get("cust_addr2") : "");
			data.put("custCity", UtilValidate.isNotEmpty(requestData.get("cust_city")) ? (String) requestData.get("cust_city") : "");
			data.put("custState", UtilValidate.isNotEmpty(requestData.get("cust_state")) ? (String) requestData.get("cust_state") : "");
			data.put("custZip", UtilValidate.isNotEmpty(requestData.get("cust_zip")) ? (String) requestData.get("cust_zip") : "");
			data.put("custPhone", UtilValidate.isNotEmpty(requestData.get("cust_phone")) ? (String) requestData.get("cust_phone") : "");
			data.put("custAddrValid", UtilValidate.isNotEmpty(requestData.get("cust_addr_valid")) ? (String) requestData.get("cust_addr_valid") : "");

			if(UtilValidate.isNotEmpty(requiredFields)) {
				errorList = "";
				for(String requiredField : requiredFields) {
					String javaFieldName = ModelUtil.dbNameToVarName(requiredField);
					if(UtilValidate.isNotEmpty(data) && UtilValidate.isEmpty(data.get(javaFieldName))) {
						errorList = errorList+ (UtilValidate.isEmpty(errorList) ? "cust_ship_to -> "+requiredField : ", "+requiredField);
					}

				}
			}

			if(UtilValidate.isNotEmpty(errorList)) {
				isValid = false;
			}
		} catch (Exception e) {
			isValid = false;
		}
		return isValid;
	}
	public static boolean checkPassword(String oldPassword, boolean useEncryption, String currentPassword) {
		boolean passwordMatches = false;
		if (oldPassword != null) {
			if (useEncryption) {
				passwordMatches = HashCrypt.comparePassword(oldPassword,
						org.ofbiz.common.login.LoginServices.getHashType(), currentPassword);
			} else {
				passwordMatches = oldPassword.equals(currentPassword);
			}
		}
		if (!passwordMatches
				&& "true".equals(UtilProperties.getPropertyValue("security", "password.accept.encrypted.and.plain"))) {
			passwordMatches = currentPassword.equals(oldPassword);
		}
		return passwordMatches;
	}
	
	public static List<GenericValue> getPrimaryContactInfoByType(Delegator delegator, String partyId, String contactMechPurposeTypeId) {
		List<GenericValue> contactInfoList = new ArrayList<GenericValue>();
		try {
			DynamicViewEntity dynamicView = new DynamicViewEntity();
			dynamicView.addMemberEntity("PCM", "PartyContactMech");
			dynamicView.addAlias("PCM", "partyId");
			dynamicView.addAlias("PCM", "contactMechId");
			dynamicView.addAlias("PCM", "fromDate");
			dynamicView.addAlias("PCM", "thruDate");
			dynamicView.addMemberEntity("PCMP", "PartyContactMechPurpose");
			dynamicView.addAlias("PCMP", "contactMechPurposeTypeId");
			dynamicView.addViewLink("PCM", "PCMP", Boolean.FALSE, ModelKeyMap.makeKeyMapList("contactMechId"));
			
			EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
					EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId),
					EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, contactMechPurposeTypeId)
					);
			TransactionUtil.begin();
			contactInfoList = EntityQuery.use(delegator).from(dynamicView).where(condition).filterByDate().queryList();
			TransactionUtil.commit();
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return contactInfoList;
	}
	
	public static String getPartyIdentification(Delegator delegator, String partyId, String partyIdentificationTypeId) {
		String idValue = "";
		try {
			GenericValue partyAttribute = EntityQuery.use(delegator).from("PartyIdentification").where("partyId", partyId, "partyIdentificationTypeId", partyIdentificationTypeId).queryFirst();
			if(UtilValidate.isNotEmpty(partyAttribute))
				idValue = partyAttribute.getString("idValue");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return idValue;
	}
	public static Map<String, Object> getPartyIdentificationValueList(Delegator delegator, List<GenericValue> dataList, String fieldId, String partyIdentificationTypeId){
		Map<String, Object> results = new HashMap<>();
		try {
			if (UtilValidate.isNotEmpty(dataList)) {
				List<String> typeIds = EntityUtil.getFieldListFromEntityList(dataList, fieldId, true).stream()
						.map(x->x.toString()).collect(Collectors.toList());
				results = typeIds.stream().filter(x->UtilValidate.isNotEmpty(x)).collect(Collectors.toMap(x -> x.toString(),
						x -> {
							String val = getPartyIdentification(delegator, x, partyIdentificationTypeId);
							if (UtilValidate.isEmpty(val)) {
								val = "";
							}
							return UtilValidate.isNotEmpty(x) ? val : "";
						},
						(attr1, attr2) -> {
							return attr2;
						}));
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return results;
	}
	
}

