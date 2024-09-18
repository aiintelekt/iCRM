/**
 * 
 */
package org.groupfio.etl.process.wrapper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.fio.homeapps.util.ParamUtil;
import org.groupfio.etl.process.util.DataHelper;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;

/**
 * @author Sharif
 *
 */
public class ImportWrapper {

	private static String MODULE = ImportWrapper.class.getName();
	
	public static void wrapLeadData(Delegator delegator, String countryGeoId, Map<String, Object> rowValue) {
		
		try {
			if (UtilValidate.isNotEmpty(rowValue) && UtilValidate.isNotEmpty(rowValue.get("leadId"))) {
				List<GenericValue> exportFieldList = delegator.findByAnd("ExportField", UtilMisc.toMap("countryCode", countryGeoId, "exportFieldType", "LEAD_EXPORT"), UtilMisc.toList("sequenceNumber"), false);
				if (UtilValidate.isNotEmpty(exportFieldList)) {
					
					List<String> exportFieldNameList = EntityUtil.getFieldListFromEntityList(exportFieldList, "exportFieldName", true); 
					if (rowValue.size() != exportFieldNameList.size()) {
						
						//Map<String, Object> columns = UtilMisc.toMap(exportFieldNameList);
						
						String leadId = ParamUtil.getString(rowValue, "leadId");
						
						Map<String, Object> exportRow = new LinkedHashMap<String,Object>();
						
						GenericValue person = delegator.findOne("Person", UtilMisc.toMap("partyId",leadId), false);
						String first_name = "";
						String last_name = "";
						if(person != null && person.size() > 0) {
							first_name = person.getString("firstName");
							last_name = person.getString("lastName");
						}
						GenericValue partySupplementalData = EntityUtil.getFirst( delegator.findByAnd("PartySupplementalData", UtilMisc.toMap("partyId", leadId), null, false) );
						if (UtilValidate.isNotEmpty(partySupplementalData)) {
							
						    exportRow.put("lead_id",leadId);
						    
							ExportWrapper.getData(exportFieldNameList, "first_name", first_name != "" ? first_name : partySupplementalData.getString("partyFirstName"), exportRow);
							ExportWrapper.getData(exportFieldNameList, "last_name", last_name != "" ? last_name : partySupplementalData.getString("partyLastName"), exportRow);
							ExportWrapper.getData(exportFieldNameList, "company_name", partySupplementalData.getString("companyName") != null ? partySupplementalData.getString("companyName") : "", exportRow);
							ExportWrapper.getData(exportFieldNameList, "parent_co_details", partySupplementalData.getString("parentCoDetails") != null ? partySupplementalData.getString("parentCoDetails") : "", exportRow);
							ExportWrapper.getData(exportFieldNameList, "Salutation", DataHelper.getEnumDescription(delegator, partySupplementalData.getString("title"), "SALUTATION"), exportRow);
							ExportWrapper.getData(exportFieldNameList, "sales_turnover", partySupplementalData.getString("salesTurnover") != null ? partySupplementalData.getString("salesTurnover") : "", exportRow);
							ExportWrapper.getData(exportFieldNameList, "date_of_incorporation", partySupplementalData.getString("dateOfIncorporation") != null ? partySupplementalData.getString("dateOfIncorporation") : "", exportRow);
							ExportWrapper.getData(exportFieldNameList, "constitution", DataHelper.getEnumDescription(delegator, partySupplementalData.getString("constitution"), "DBS_CONSTITUTION"), exportRow);
							ExportWrapper.getData(exportFieldNameList, "industry_cat", DataHelper.getEnumDescription(delegator, partySupplementalData.getString("industryCat"), "DBS_INDUSTRY_CAT"), exportRow);
							ExportWrapper.getData(exportFieldNameList, "industry", DataHelper.getEnumDescription(delegator, partySupplementalData.getString("industry"), "DBS_INDUSTRY"), exportRow);
							ExportWrapper.getData(exportFieldNameList, "Import / Export Customer", DataHelper.getEnumDescription(delegator, partySupplementalData.getString("customerTradingType"), "DBS_CUST_TRD_TYPE"), exportRow);
							ExportWrapper.getData(exportFieldNameList, "tally_user_type", DataHelper.getEnumDescription(delegator, partySupplementalData.getString("tallyUserType"), "DBS_TALLY_USR_TYPE"), exportRow);
							ExportWrapper.getData(exportFieldNameList, "source", DataHelper.getPartyIdentificationDescription(delegator, partySupplementalData.getString("source")), exportRow);
							ExportWrapper.getData(exportFieldNameList, "tcp_name", DataHelper.getEnumDescription(delegator, partySupplementalData.getString("tcpName"), "DBS_TCP_NAME"), exportRow);
							ExportWrapper.getData(exportFieldNameList, "key_contact_person1", partySupplementalData.getString("keyContactPerson1") != null ? partySupplementalData.getString("keyContactPerson1") : "", exportRow);
							ExportWrapper.getData(exportFieldNameList, "key_contact_person2", partySupplementalData.getString("keyContactPerson2") != null ? partySupplementalData.getString("keyContactPerson2") : "", exportRow);
							
							String primary_phone_country_code = "";
							String phone_number1 = "";
							String phone_number2_country_code = "";
							String phone_number2 = "";
							String address1 = "";
							String address2 = "";
							String cityId = "";
							String stateProvinceGeoId = "";
							String postalCode = "";
							String email_address = "";
							
							String phoneNumberOneDndStatus = "";
							String phoneNumberTwoDndStatus = "";
							
							List < GenericValue > partyContactMechs = delegator.findByAnd("PartyContactMech", UtilMisc.toMap("partyId", leadId), null, false);
							if (partyContactMechs != null && partyContactMechs.size() > 0) {
								partyContactMechs = EntityUtil.filterByDate(partyContactMechs);
								if (partyContactMechs != null && partyContactMechs.size() > 0) {
									partyContactMechs = EntityUtil.getFieldListFromEntityList(partyContactMechs, "contactMechId", true);
								}
								if (partyContactMechs != null && partyContactMechs.size() > 0) {
									Set < String > findOptions = UtilMisc.toSet("contactMechId");
									List < String > orderBy = UtilMisc.toList("createdStamp DESC");

									EntityCondition condition1 = EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, leadId);
									EntityCondition condition2 = EntityCondition.makeCondition("contactMechId", EntityOperator.IN, partyContactMechs);

									EntityCondition primaryPhoneConditions = EntityCondition.makeCondition(UtilMisc.toList(condition1, condition2, EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "PRIMARY_PHONE")));
									List < GenericValue > primaryPhones = delegator.findList("PartyContactMechPurpose", primaryPhoneConditions, findOptions, orderBy, null, false);
									if (primaryPhones != null && primaryPhones.size() > 0) {
										GenericValue primaryPhone = EntityUtil.getFirst(EntityUtil.filterByDate(primaryPhones));
										if (UtilValidate.isNotEmpty(primaryPhone)) {
											GenericValue primaryPhoneNumber = delegator.findOne("TelecomNumber", UtilMisc.toMap("contactMechId", primaryPhone.getString("contactMechId")), false);
											if (UtilValidate.isNotEmpty(primaryPhoneNumber)) {
												phone_number1 = primaryPhoneNumber.getString("contactNumber");
												primary_phone_country_code = primaryPhoneNumber.getString("countryCode"); 
												phoneNumberOneDndStatus = primaryPhoneNumber.getString("dndStatus"); 
											}
										}
									}

									EntityCondition secondaryPhoneConditions = EntityCondition.makeCondition(UtilMisc.toList(condition1, condition2, EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "PHONE_WORK_SEC")));
									List <GenericValue> secondaryPhones = delegator.findList("PartyContactMechPurpose", secondaryPhoneConditions, findOptions, orderBy, null, false);
									if (secondaryPhones != null && secondaryPhones.size() > 0) {
										GenericValue secondaryPhone = EntityUtil.getFirst(EntityUtil.filterByDate(secondaryPhones));
										if (UtilValidate.isNotEmpty(secondaryPhone)) {
											GenericValue secondaryPhoneNumber = delegator.findOne("TelecomNumber", UtilMisc.toMap("contactMechId", secondaryPhone.getString("contactMechId")), false);
											if (UtilValidate.isNotEmpty(secondaryPhoneNumber)) {
												phone_number2 = secondaryPhoneNumber.getString("contactNumber");
												phone_number2_country_code = secondaryPhoneNumber.getString("countryCode"); 
												phoneNumberTwoDndStatus = secondaryPhoneNumber.getString("dndStatus"); 
											}
										}
									}

									EntityCondition primaryEmailConditions = EntityCondition.makeCondition(UtilMisc.toList(condition1, condition2, EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "PRIMARY_EMAIL")));
									List < GenericValue > primaryEmails = delegator.findList("PartyContactMechPurpose", primaryEmailConditions, findOptions, orderBy, null, false);
									if (primaryEmails != null && primaryEmails.size() > 0) {
										GenericValue primaryEmail = EntityUtil.getFirst(EntityUtil.filterByDate(primaryEmails));
										if (UtilValidate.isNotEmpty(primaryEmail)) {
											GenericValue primaryInfoString = delegator.findOne("ContactMech", UtilMisc.toMap("contactMechId", primaryEmail.getString("contactMechId")), false);
											if (UtilValidate.isNotEmpty(primaryInfoString)) {
												email_address = primaryInfoString.getString("infoString");
											}
										}
									}

									EntityCondition postalAddressConditions = EntityCondition.makeCondition(UtilMisc.toList(condition1, condition2, EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "PRIMARY_LOCATION")));
									List < GenericValue > primaryAddressList = delegator.findList("PartyContactMechPurpose", postalAddressConditions, findOptions, orderBy, null, false);
									if (primaryAddressList != null && primaryAddressList.size() > 0) {
										GenericValue primaryAddress = EntityUtil.getFirst(EntityUtil.filterByDate(primaryAddressList));
										if (UtilValidate.isNotEmpty(primaryAddress)) {
											GenericValue postalAddress = delegator.findOne("PostalAddress", UtilMisc.toMap("contactMechId", primaryAddress.getString("contactMechId")), false);
											if (UtilValidate.isNotEmpty(postalAddress)) {
												cityId = postalAddress.getString("city");
												stateProvinceGeoId = postalAddress.getString("stateProvinceGeoId");
												address1 = postalAddress.getString("address1");
												address2 = postalAddress.getString("address2");
												postalCode = postalAddress.getString("postalCode");
											}
										}
									}
								}
							}
							
							String personResponsible = "";
							String personResponsibleAssignBy = "";

							EntityCondition conditionPR = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, leadId),
						            EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "ACCOUNT_MANAGER"),
						        EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.IN, UtilMisc.toList("ACCOUNT", "LEAD", "CONTACT")),
						        EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "RESPONSIBLE_FOR"),
						        EntityUtil.getFilterByDateExpr()), EntityOperator.AND);

							GenericValue responsibleFor = EntityQuery.use(delegator).from("PartyRelationship").where(conditionPR).orderBy("fromDate DESC").queryFirst();
							
							if (UtilValidate.isNotEmpty(responsibleFor)) {
								personResponsible = responsibleFor.getString("partyIdTo");
							    
							    if (UtilValidate.isNotEmpty(responsibleFor.getString("createdByUserLoginId"))) {
							    	GenericValue createdByUserLogin =  EntityQuery.use(delegator).from("UserLogin").where("userLoginId", responsibleFor.getString("createdByUserLoginId")).queryFirst();
							    	if (UtilValidate.isNotEmpty(createdByUserLogin)) {
							    		personResponsibleAssignBy = createdByUserLogin.getString("partyId");
							    	}
							    }
							    
							}
							
							ExportWrapper.getData(exportFieldNameList, "phone_number1_country_code", primary_phone_country_code != null ? primary_phone_country_code : "", exportRow);
							ExportWrapper.getData(exportFieldNameList, "phone_number1", phone_number1 != null ? phone_number1 : "", exportRow);
							ExportWrapper.getData(exportFieldNameList, "phone_number2_country_code", phone_number2_country_code != null ? phone_number2_country_code : "", exportRow);
							ExportWrapper.getData(exportFieldNameList, "phone_number2", phone_number2 != null ? phone_number2 : "", exportRow);
							ExportWrapper.getData(exportFieldNameList, "Company Address", address1 != null ? address1 : "", exportRow);
							ExportWrapper.getData(exportFieldNameList, "city", DataHelper.getGeoName(delegator, partySupplementalData.getString("city"), "CITY"), exportRow);
							ExportWrapper.getData(exportFieldNameList, "state", DataHelper.getGeoName(delegator, partySupplementalData.getString("stateProvinceGeoId"), "STATE,PROVINCE"), exportRow);
							ExportWrapper.getData(exportFieldNameList, "email_address",email_address != null ? email_address : "", exportRow);
							ExportWrapper.getData(exportFieldNameList, "permanent_acccount_number", partySupplementalData.getString("permanentAcccountNumber") != null ? partySupplementalData.getString("permanentAcccountNumber") : "", exportRow);
							ExportWrapper.getData(exportFieldNameList, "business_reg_no", partySupplementalData.getString("businessRegNo") != null ? partySupplementalData.getString("businessRegNo") : "", exportRow);
							ExportWrapper.getData(exportFieldNameList, "Other Banks", DataHelper.getEnumDescription(delegator, partySupplementalData.getString("otherBankName"), "DBS_EXISTING_BN"), exportRow);
							ExportWrapper.getData(exportFieldNameList, "Total Cash Balances Maintained With Other Banks", partySupplementalData.getString("otherBankBalance") != null ? partySupplementalData.getString("otherBankBalance") : "", exportRow);
							ExportWrapper.getData(exportFieldNameList, "Current Product(s) Held With Other Bank", DataHelper.getEnumDescription(delegator, partySupplementalData.getString("productsHeldInOthBank"), "DBS_PROD_HIOB"), exportRow);
							ExportWrapper.getData(exportFieldNameList, "Product Value with other banks", partySupplementalData.getString("productsValueInOthBank") != null ? partySupplementalData.getString("productsValueInOthBank") : "", exportRow);
							ExportWrapper.getData(exportFieldNameList, "paidup_capital", partySupplementalData.getString("paidupCapital") != null ? partySupplementalData.getString("paidupCapital") : "", exportRow);
							ExportWrapper.getData(exportFieldNameList, "authorised_cap", partySupplementalData.getString("authorisedCap") != null ? partySupplementalData.getString("authorisedCap") : "", exportRow);
							ExportWrapper.getData(exportFieldNameList, "Lead Assigned To", personResponsible, exportRow);
							ExportWrapper.getData(exportFieldNameList, "Lead Assigned By", personResponsibleAssignBy, exportRow);
							ExportWrapper.getData(exportFieldNameList, "segment", partySupplementalData.getString("segment") != null ? partySupplementalData.getString("segment") : "", exportRow);
							ExportWrapper.getData(exportFieldNameList, "Asset / Liability Lead", DataHelper.getEnumDescription(delegator, partySupplementalData.getString("liabOrAsset"), "DBS_LA_TYPE"), exportRow);
							ExportWrapper.getData(exportFieldNameList, "Telecaller Status", DataHelper.getEnumDescription(delegator, partySupplementalData.getString("teleCallingStatus"), "DBS_TELE_CALL_STATUS"), exportRow);
							ExportWrapper.getData(exportFieldNameList, "Telecaller sub status", DataHelper.getEnumDescription(delegator, partySupplementalData.getString("teleCallingSubStatus"), "DBS_TELE_SUB_STATUS"), exportRow);
							ExportWrapper.getData(exportFieldNameList, "Telecaller Remarks", partySupplementalData.getString("teleCallingRemarks") != null ? partySupplementalData.getString("teleCallingRemarks") : "", exportRow);
							ExportWrapper.getData(exportFieldNameList, "RM Call Status", DataHelper.getEnumDescription(delegator, partySupplementalData.getString("rmCallingStatus"), "DBS_RM_CALL_STATUS"), exportRow);
							ExportWrapper.getData(exportFieldNameList, "RM Call Sub Status", DataHelper.getEnumDescription(delegator, partySupplementalData.getString("rmCallingSubStatus"), "DBS_RM_SUB_STATUS"), exportRow);
							ExportWrapper.getData(exportFieldNameList, "RM Remarks", partySupplementalData.getString("rmCallingRemarks") != null ? partySupplementalData.getString("rmCallingRemarks") : "", exportRow);
							ExportWrapper.getData(exportFieldNameList, "no_of_attempt", partySupplementalData.getString("noOfAttempt") != null ? partySupplementalData.getString("noOfAttempt") : "", exportRow);
							ExportWrapper.getData(exportFieldNameList, "finacle_id", partySupplementalData.getString("finacleId") != null ? partySupplementalData.getString("finacleId") : "", exportRow);
							ExportWrapper.getData(exportFieldNameList, "place_of_incorporation", DataHelper.getEnumDescription(delegator, partySupplementalData.getString("placeOfIncorporation"), "DBS_PLACE_INCORP"), exportRow);
							ExportWrapper.getData(exportFieldNameList, "no_of_employees", partySupplementalData.getString("noOfEmployees") != null ? partySupplementalData.getString("noOfEmployees") : "", exportRow);
							ExportWrapper.getData(exportFieldNameList, "job_family", DataHelper.getEnumDescription(delegator, partySupplementalData.getString("jobFamily"), "DBS_JOB_FAMILY"), exportRow);
							ExportWrapper.getData(exportFieldNameList, "designation", DataHelper.getEnumDescription(delegator, partySupplementalData.getString("designation"), "DBS_LD_DESIGNATION"), exportRow);
							ExportWrapper.getData(exportFieldNameList, "PIN Code", postalCode, exportRow);
							ExportWrapper.getData(exportFieldNameList, "lead_status", DataHelper.getEnumDescription(delegator, partySupplementalData.getString("leadStatus"), "LEAD_STATUS_HISTORY"), exportRow);

							ExportWrapper.getData(exportFieldNameList, "phone_number1_dnd_status", phoneNumberOneDndStatus, exportRow);
							ExportWrapper.getData(exportFieldNameList, "phone_number2_dnd_status", phoneNumberTwoDndStatus, exportRow);
							ExportWrapper.getData(exportFieldNameList, "lead_score", DataHelper.getEnumDescription(delegator, partySupplementalData.getString("leadScore"), "LEAD_SCORE"), exportRow);
							
						}
						
						if (UtilValidate.isNotEmpty(exportRow)) {
							
							for (String key : exportRow.keySet()) {
								
								String tableColumnName = "";
								GenericValue checkValue = EntityUtil.getFirst(delegator.findByAnd("EtlMappingElements",
										UtilMisc.toMap("listName", "Lead ModelA", "etlFieldName", key), null, false));
								if (UtilValidate.isNotEmpty(checkValue)) {
									tableColumnName = checkValue.getString("tableColumnName");
								}
								
								if (UtilValidate.isNotEmpty(tableColumnName) && !rowValue.containsKey(tableColumnName)) {
									rowValue.put(tableColumnName, exportRow.get(key));
								}
							}
							
						}
						
					}
					
				}
			}
		} catch (Exception e) {
			Debug.logError("wrapLeadData ERROR: "+e.getMessage(), MODULE);
		}
		
	}
	
}
