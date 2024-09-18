/**
 * 
 */
package org.groupfio.etl.process.validator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.fio.homeapps.util.ParamUtil;
import org.groupfio.etl.process.EtlConstants;
import org.groupfio.etl.process.EtlConstants.ValidationAuditType;
import org.groupfio.etl.process.ResponseCodes;
import org.groupfio.etl.process.util.ValidatorUtil;
import org.groupfio.etl.process.writer.WriterUtil;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityFieldValue;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

/**
 * @author Sharif
 *
 */
public class LeadDataValidatorBck implements Validator {

	private static String MODULE = LeadDataValidatorBck.class.getName();

	private boolean validate;

	/* (non-Javadoc)
	 * @see org.groupfio.etl.process.validator.Validator#validate(java.util.Map)
	 */
	@Override
	public Map<String, Object> validate(Map<String, Object> context) {

		Map<String, Object> response = new HashMap<String, Object>();
		Map<String, Object> data = (Map<String, Object>) context.get("data");
		Map<String, Object> validationMessage = new HashMap<String, Object>();

		List<String> errorCodes = new ArrayList<String>();
		
		List<Map<String, Object>> validationAuditLogList = new ArrayList<Map<String, Object>>();

		try {

			setValidate(true);

			Delegator delegator = (Delegator) context.get("delegator");
			String modelName = ParamUtil.getString(context, "modelName");

			Integer rowNumber = ParamUtil.getInteger(context, "rowNumber");
			String taskName = ParamUtil.getString(context, "taskName");
			String tableName = ParamUtil.getString(context, "tableName");

			Locale locale = (Locale) context.get("locale");
			GenericValue userLogin = (GenericValue) context.get("userLogin");

			String message = null;
			String leadShortForm = ParamUtil.getString(context, "leadShortForm");

			//boolean isNotDuplicate = ParamUtil.getBoolean(context, "isNotDuplicate");

			String isNotDuplicate = ParamUtil.getString(context, "isNotDuplicate");
			Debug.log(isNotDuplicate+"isNoDuplicateisNoDuplicate");
			Debug.log("===leadShortForm===="+leadShortForm);

			if (UtilValidate.isNotEmpty(data.get("stateProvinceGeoId"))) {
				GenericValue validGeo = ValidatorUtil.getValidGeo(delegator, data.get("stateProvinceGeoId").toString(), "STATE");
				if (UtilValidate.isNotEmpty(validGeo)) {
					data.put("stateProvinceGeoId", validGeo.getString("geoId"));
				}
			}
			
			/*if (UtilValidate.isNotEmpty(data.get("city"))) {
				GenericValue validGeo = ValidatorUtil.getValidGeo(delegator, data.get("city").toString(), "CITY");
				if (UtilValidate.isNotEmpty(validGeo)) {
					data.put("city", validGeo.getString("geoId"));
				}
			}*/
			
			if (UtilValidate.isNotEmpty(data.get("postalCode")) && !ValidatorUtil.validatePINCode(data.get("postalCode").toString())) {
				setValidate(false);
				message = "Invalid PIN Code" + " [Row No:" + rowNumber + "]";
				validationMessage.put("postalCode", message);
				errorCodes.add("E1062");
			} else if (UtilValidate.isNotEmpty(data.get("postalCode"))) {
					
				EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("geoName", EntityOperator.EQUALS, data.get("postalCode")),
						EntityCondition.makeCondition("geoAssocTypeId", EntityOperator.EQUALS, "POSTAL_CODE"),
						EntityCondition.makeCondition("geoTypeId", EntityOperator.EQUALS, "POSTAL_CODE")
               			);                       	
				
				GenericValue geoAssoc = EntityUtil.getFirst( delegator.findList("GeoAssocSummary", condition, null, null, null, false) );
				if (UtilValidate.isNotEmpty(geoAssoc)) {
					
					if (UtilValidate.isEmpty(data.get("city")) || !data.get("city").equals(geoAssoc.get("geoId"))) {
						GenericValue validGeo = ValidatorUtil.getValidGeo(delegator, geoAssoc.getString("geoId"), "CITY");
						validationAuditLogList.add(WriterUtil.prepareValidationAudit(null, "city", ValidatorUtil.getValidGeoName(delegator, (String)data.get("city"), "CITY"), validGeo.getString("geoName"), userLogin.getString("userLoginId"), ValidationAuditType.VAT_LEAD_IMPORT, "City Auto corrected by PIN Code: "+data.get("postalCode")));
					}
					data.put("city", geoAssoc.get("geoId"));
					
					GenericValue geoStateAssoc = EntityUtil.getFirst( delegator.findByAnd("GeoAssocSummary", UtilMisc.toMap("geoIdTo", geoAssoc.get("geoId"), "geoAssocTypeId", "COUNTY_CITY"), null, false) );
					if (UtilValidate.isNotEmpty(geoStateAssoc)) {
						if (UtilValidate.isEmpty(data.get("stateProvinceGeoId")) || !data.get("stateProvinceGeoId").equals(geoStateAssoc.get("geoId"))) {
							GenericValue validGeo = ValidatorUtil.getValidGeo(delegator, geoStateAssoc.getString("geoId"), "STATE");
							validationAuditLogList.add(WriterUtil.prepareValidationAudit(null, "state", ValidatorUtil.getValidGeoName(delegator, (String)data.get("stateProvinceGeoId"), "STATE"), validGeo.getString("geoName"), userLogin.getString("userLoginId"), ValidationAuditType.VAT_LEAD_IMPORT, "State Auto corrected by PIN Code: "+data.get("postalCode")));
						}
						data.put("stateProvinceGeoId", geoStateAssoc.get("geoId"));
					}
					
				} else {
					setValidate(false);
					message = "Invalid PIN Code" + " [Row No:" + rowNumber + "]";
					validationMessage.put("postalCode", message);
					errorCodes.add("E1082");
				}	
				
			} 

			/*if (UtilValidate.isNotEmpty(data.get("city")) && !ValidatorUtil.isValidGeo(delegator, data.get("city").toString(), "CITY")) {
				setValidate(false);
				message = "Invalid city" + " [Row No:" + rowNumber + "]";
				validationMessage.put("city", message);
				errorCodes.add("E1042");
			} else if (UtilValidate.isNotEmpty(data.get("city"))) {
				GenericValue validGeo = ValidatorUtil.getValidGeo(delegator, data.get("city").toString(), "CITY");
				if (UtilValidate.isNotEmpty(validGeo)) {
					data.put("city", validGeo.getString("geoId"));

					// Auto correction state
					GenericValue geoStateAssoc = EntityUtil.getFirst( delegator.findByAnd("GeoAssocSummary", UtilMisc.toMap("geoIdTo", validGeo.get("geoId"), "geoAssocTypeId", "COUNTY_CITY"), null, false) );
					if (UtilValidate.isNotEmpty(geoStateAssoc)) {
						if (UtilValidate.isEmpty(data.get("stateProvinceGeoId")) || !data.get("stateProvinceGeoId").equals(geoStateAssoc.get("geoId"))) {
							GenericValue stateValidGeo = ValidatorUtil.getValidGeo(delegator, geoStateAssoc.getString("geoId"), "STATE");
							validationAuditLogList.add(WriterUtil.prepareValidationAudit(null, "state", ValidatorUtil.getValidGeoName(delegator, (String)data.get("stateProvinceGeoId"), "STATE"), stateValidGeo.getString("geoName"), userLogin.getString("userLoginId"), ValidationAuditType.VAT_LEAD_IMPORT, "State Auto corrected by City: "+validGeo.getString("geoName")));
						}
						data.put("stateProvinceGeoId", geoStateAssoc.get("geoId"));
					}

				}
			}*/

			// check duplicate [start]

			GenericValue partyIdentification = null;
			String leadId = UtilValidate.isNotEmpty(data.get("leadId")) ? data.get("leadId").toString() : null;
			String primaryPartyId = leadId;
			Debug.log("===leadId===="+leadId);
			if (UtilValidate.isNotEmpty(leadId)) {

				EntityCondition condition = EntityCondition.makeCondition(EntityOperator.OR,
						EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, leadId),
						EntityCondition.makeCondition("idValue", EntityOperator.EQUALS, leadId)
						);      

				partyIdentification = EntityUtil.getFirst( delegator.findList("PartyIdentification", condition, null, UtilMisc.toList("-createdStamp"), null, false) );
				if (UtilValidate.isNotEmpty(partyIdentification)) {
					leadId = partyIdentification.getString("idValue");
					primaryPartyId = partyIdentification.getString("partyId");
				}

			}

			boolean isDisalbed = false;
			if (UtilValidate.isNotEmpty(partyIdentification)) {
				GenericValue party = EntityUtil.getFirst( delegator.findByAnd("Party", UtilMisc.toMap("partyId", partyIdentification.getString("partyId")), null, false) );
				if (UtilValidate.isNotEmpty(party) && UtilValidate.isNotEmpty(party.getString("statusId")) && party.getString("statusId").equals("PARTY_DISABLED")) {
					isDisalbed = true;
				}
			}

			if (isDisalbed) {

				setValidate(false);
				message = "Disabled lead data" + " [Row No:" + rowNumber + "]";
				validationMessage.put("leadId", message);
				errorCodes.add("E1080");

			} else {

				if (!"on".equals(isNotDuplicate)) {
					if (UtilValidate.isNotEmpty(data.get("companyName"))) {
						Map<String,String> phoneMap=new HashMap<>();
						String ccdPhoneNum1 = ParamUtil.getString(data, "primaryPhoneCountryCode");
						String phoneNum1 = ParamUtil.getString(data, "primaryPhoneNumber");
						String ccdPhoneNum2 = ParamUtil.getString(data, "secondaryPhoneCountryCode");
						String phoneNum2 = ParamUtil.getString(data, "secondaryPhoneNumber");
						String companyName = ParamUtil.getString(data, "companyName");
						String emailAddress = ParamUtil.getString(data, "emailAddress");

						String address1 = ParamUtil.getString(data, "address1");
						String city = ParamUtil.getString(data, "city");
						String state = ParamUtil.getString(data, "stateProvinceGeoId");
						String postalCode = ParamUtil.getString(data, "postalCode");

						Map<String,String> addressMap=new HashMap<>();

						phoneMap.put("ccdPhoneNum1", ccdPhoneNum1);
						phoneMap.put("phoneNum1", phoneNum1);
						phoneMap.put("ccdPhoneNum2", ccdPhoneNum2);
						phoneMap.put("phoneNum2", phoneNum2);

						if (UtilValidate.isNotEmpty(state)) {
							GenericValue validGeo = ValidatorUtil.getValidGeo(delegator, state, "STATE");
							if(UtilValidate.isNotEmpty(validGeo)){
								state = validGeo.getString("geoId");
							}
						}
						if (UtilValidate.isNotEmpty(city)) {
							GenericValue validGeo = ValidatorUtil.getValidGeo(delegator, city, "CITY");
							if(UtilValidate.isNotEmpty(validGeo)){
								city = validGeo.getString("geoId");
							}
						}
						addressMap.put("address1",address1);
						addressMap.put("city",city);
						addressMap.put("stateProvinceGeoId",state);
						addressMap.put("postalCode",postalCode);

						HashMap<String,String> dedupStatus = getLeadDedupStatus(primaryPartyId, companyName,emailAddress,phoneMap,addressMap,delegator);
						Debug.log(dedupStatus+"dedupStatusReturned");
						//data.put("leadId", leadId);
						String tempLeadId = "";
						primaryPartyId = dedupStatus.get("leadId");
						String oldLeadId = dedupStatus.get("originalLeadId");
						String dedupLeadNewId = dedupStatus.get("foundLeadId");
						if (UtilValidate.isNotEmpty(primaryPartyId)) {

							EntityCondition condition = EntityCondition.makeCondition(EntityOperator.OR,
									EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, primaryPartyId),
									EntityCondition.makeCondition("idValue", EntityOperator.EQUALS, primaryPartyId)
									);      

							partyIdentification = EntityUtil.getFirst( delegator.findList("PartyIdentification", condition, null, UtilMisc.toList("-createdStamp"), null, false) );
							if (UtilValidate.isNotEmpty(partyIdentification)) {
								tempLeadId = partyIdentification.getString("idValue");
							}

						}

						String reasonCode = dedupStatus.get("responseCode");
						String errorMessage = dedupStatus.get("errorMessage");

						/*E1083- duplicate -- E1084 = Company Name and Lead Id not match  -- E1080 = disabled -- 
	                S101  = update -- S103 = create lead -- S104 = create lead with data enrich segment	*/	
						Debug.log(leadId+"leadIdleadIdleadId");
						if (UtilValidate.isNotEmpty(dedupStatus)) {

							if ("E1083".equals(reasonCode) || "E1084".equals(reasonCode) || "E1080".equals(reasonCode)) {
								setValidate(false);
								errorCodes.add(reasonCode);
								validationMessage.put("leadId", errorMessage);
								//data.put("importError", errorMessage);
								data.put("primaryPartyId", primaryPartyId);
								
								validationAuditLogList.add(WriterUtil.prepareValidationAudit(null, "leadId", oldLeadId, dedupLeadNewId, userLogin.getString("userLoginId"), ValidationAuditType.VAT_LEAD_DEDUP, errorMessage));
								
							} else if ("S101".equals(reasonCode)) {
								data.put("leadId", tempLeadId);
							} else if ("S103".equals(reasonCode)) {
								if (UtilValidate.isEmpty(leadId)){
									data.put("leadId", null);
								}else{
									data.put("leadId", leadId);
								}
							} else if ("S104".equals(reasonCode)) {
								data.put("leadId", null);
								data.put("isDedupAutoSegmentation", "Y");
							}

						}


					}
				}

			}
			Debug.log(data+"datadata");

			// check duplicate [end]

			if (UtilValidate.isEmpty(data.get("leadId"))) {
				data.put("leadId", "LD-" + delegator.getNextSeqId("DataImportLead"));
				
				leadId = (String) data.get("leadId");
				primaryPartyId = (String) data.get("leadId");
			}
			
			if (UtilValidate.isEmpty(data.get("firstName")) && UtilValidate.isEmpty(data.get("keyContactPerson1"))) {
				setValidate(false);
				message = UtilProperties.getPropertyValue("Etl-Process.properties", "EtlFirstConditionEmpty") + " [Row No:" + rowNumber + "]";
				validationMessage.put("firstName", message);
				errorCodes.add("E1017");
			}

			if (UtilValidate.isEmpty(data.get("primaryPhoneNumber")) && UtilValidate.isEmpty(data.get("address1")) && UtilValidate.isEmpty(data.get("emailAddress"))  && !"Y".equals(leadShortForm) ) {
				setValidate(false);
				message = UtilProperties.getPropertyValue("Etl-Process.properties", "EtlSecondConditionEmpty") + " [Row No:" + rowNumber + "]";
				validationMessage.put("primaryPhoneNumber", message);
				errorCodes.add("E1018");
			}

			if (UtilValidate.isEmpty(data.get("companyName"))) {
				setValidate(false);
				message = UtilProperties.getPropertyValue("Etl-Process.properties", "EtlCompanyNameEmpty") + " [Row No:" + rowNumber + "]";
				validationMessage.put("companyName", message);
				errorCodes.add("E1007");
			}

			if (UtilValidate.isEmpty(data.get("city")) && !"Y".equals(leadShortForm)) {
				setValidate(false);
				message = UtilProperties.getPropertyValue("Etl-Process.properties", "EtlCityEmpty") + " [Row No:" + rowNumber + "]";
				validationMessage.put("city", message);
				errorCodes.add("E1019");
			}
			if (!"Y".equals(leadShortForm)) {
				if (UtilValidate.isEmpty(data.get("stateProvinceGeoId"))) {
					setValidate(false);
					message = UtilProperties.getPropertyValue("Etl-Process.properties", "EtlStateIdEmpty") + " [Row No:" + rowNumber + "]";
					validationMessage.put("stateProvinceGeoId", message);
					errorCodes.add("E1003");
				} else if (!ValidatorUtil.isValidGeo(delegator, data.get("stateProvinceGeoId").toString(), "STATE")) {
					setValidate(false);
					message = UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceStateIdError") + " [Row No:" + rowNumber + "]";
					validationMessage.put("stateProvinceGeoId", message);
					errorCodes.add("E1002");
				} else if (UtilValidate.isNotEmpty(data.get("stateProvinceGeoId")) && !"Y".equals(leadShortForm)) {
					GenericValue validGeo = ValidatorUtil.getValidGeo(delegator, data.get("stateProvinceGeoId").toString(), "STATE");
					if (UtilValidate.isNotEmpty(validGeo)) {
						data.put("stateProvinceGeoId", validGeo.getString("geoId"));

						if ( (UtilValidate.isNotEmpty(data.get("city")) && ValidatorUtil.isValidGeo(delegator, data.get("city").toString(), "CITY")) 
								&& !ValidatorUtil.isValidGeo(delegator, validGeo.getString("geoId"), data.get("city").toString(), "COUNTY_CITY")
								) {
							setValidate(false);
							message = "Invalid state and city association" + " [Row No:" + rowNumber + "]";
							validationMessage.put("city", message);
							errorCodes.add("E1073");
						}

					}
				} 
			}
			if (UtilValidate.isEmpty(data.get("segment")) && !"Y".equals(leadShortForm) ) {
				setValidate(false);
				message = UtilProperties.getPropertyValue("Etl-Process.properties", "EtlSegmentEmpty") + " [Row No:" + rowNumber + "]";
				validationMessage.put("segment", message);
				errorCodes.add("E1015");
			}

			/*if (UtilValidate.isEmpty(data.get("liabOrAsset")) &&  !"Y".equals(leadShortForm) ) {
				setValidate(false);
				message = UtilProperties.getPropertyValue("Etl-Process.properties", "EtlLiabOrAssetEmpty") + " [Row No:" + rowNumber + "]";
				validationMessage.put("liabOrAsset", message);
				errorCodes.add("E1016");
			}*/

			if (UtilValidate.isNotEmpty(data.get("countryGeoId"))) {

				String contgeoid = (String) data.get("countryGeoId");
				if (UtilValidate.isNotEmpty((String) data.get("stateProvinceGeoId")) || contgeoid.equals("SGP")) {
					if (contgeoid.equals("SGP")) {
						data.put("stateProvinceGeoId", "_NA_");
					} else {
						EntityCondition condition = EntityCondition.makeCondition(EntityOperator.OR,
								EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("geoId")), EntityOperator.EQUALS, data.get("stateProvinceGeoId").toString().toUpperCase()),
								EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("geoName")), EntityOperator.EQUALS, data.get("stateProvinceGeoId").toString().toUpperCase()),
								EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("geoCode")), EntityOperator.EQUALS, data.get("stateProvinceGeoId").toString().toUpperCase()),
								EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("abbreviation")), EntityOperator.EQUALS, data.get("stateProvinceGeoId").toString().toUpperCase())
								);                       	
						condition = EntityCondition.makeCondition(EntityOperator.AND,
								EntityCondition.makeCondition("geoTypeId", EntityOperator.EQUALS, "STATE"),
								condition
								);

						GenericValue geo = EntityUtil.getFirst( delegator.findList("Geo", condition, null, null, null, false) );

						if (UtilValidate.isEmpty(geo)) {
							setValidate(false);
							message = UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceStateIdError") + " [Row No:" + rowNumber + "]";
							validationMessage.put("stateProvinceGeoId", message);
							errorCodes.add("E1002");
						} else {
							data.put("stateProvinceGeoId", geo.getString("geoId"));
						}

					}
				} else {
					setValidate(false);
					message = UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceStateIdEmptyError");
					validationMessage.put("stateProvinceGeoId", message);
					errorCodes.add("E1003");
				}
			}

			if (UtilValidate.isEmpty(data.get("source"))) {
				setValidate(false);
				message = UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServiceinvalidPartyIdentificationTypeIdError") + " [Row No:" + rowNumber + "]";
				validationMessage.put("source", message);
				errorCodes.add("E1004");
			} else {

				EntityCondition condition = EntityCondition.makeCondition(EntityOperator.OR,
						EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("partyIdentificationTypeId")), EntityOperator.EQUALS, data.get("source").toString().toUpperCase()),
						EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("description")), EntityOperator.EQUALS, data.get("source").toString().toUpperCase())
						);

				GenericValue partyIdentificationType = EntityUtil.getFirst( delegator.findList("PartyIdentificationType", condition, null, UtilMisc.toList("-createdStamp"), null, false) );

				if (UtilValidate.isEmpty(partyIdentificationType)) {
					setValidate(false);
					message = UtilProperties.getPropertyValue("Etl-Process.properties", "EtlImportServicepartyIdentificationTypeIdEmptyError") + " [Row No:" + rowNumber + "]";
					validationMessage.put("source", message);
					errorCodes.add("E1005");
				} else {
					data.put("source", partyIdentificationType.getString("partyIdentificationTypeId"));
				}

				/*if (UtilValidate.isNotEmpty(partyIdentificationType) && partyIdentificationType.getString("partyIdentificationTypeId").equals("TCP")
						&& UtilValidate.isEmpty(data.get("tcpName"))  && !"Y".equals(leadShortForm) 
						) {
					setValidate(false);
					message = UtilProperties.getPropertyValue("Etl-Process.properties", "EtlTcpNameEmpty") + " [Row No:" + rowNumber + "]";
					validationMessage.put("tcpName", message);
					errorCodes.add("E1008");
				}*/

			}

			if (UtilValidate.isNotEmpty(data.get("emailAddress")) && !ValidatorUtil.validateEmail(data.get("emailAddress").toString())  && !"Y".equals(leadShortForm) ) {
				setValidate(false);
				message = "Invalid email address" + " [Row No:" + rowNumber + "]";
				validationMessage.put("emailAddress", message);
				errorCodes.add("E1020");
			}

			/*if (UtilValidate.isNotEmpty(data.get("industryCat")) && !ValidatorUtil.isValidEnum(delegator, data.get("industryCat").toString(), "DBS_INDUSTRY_CAT")) {
				setValidate(false);
				message = "Invalid industry category" + " [Row No:" + rowNumber + "]";
				validationMessage.put("industryCat", message);
				errorCodes.add("E1021");
			} else if (UtilValidate.isNotEmpty(data.get("industryCat"))) {
				GenericValue validEnum = ValidatorUtil.getValidEnum(delegator, data.get("industryCat").toString(), "DBS_INDUSTRY_CAT");
				if (UtilValidate.isNotEmpty(validEnum)) {
					data.put("industryCat", validEnum.getString("enumId"));
				}
			}*/

			if (UtilValidate.isNotEmpty(data.get("industry")) && !ValidatorUtil.isValidEnum(delegator, data.get("industry").toString(), "PARTY_INDUSTRY")) {
				setValidate(false);
				message = "Invalid industry" + " [Row No:" + rowNumber + "]";
				validationMessage.put("industry", message);
				errorCodes.add("E1022");
			} else if (UtilValidate.isNotEmpty(data.get("industry"))) {
				GenericValue validEnum = ValidatorUtil.getValidEnum(delegator, data.get("industry").toString(), "DBS_INDUSTRY");
				if (UtilValidate.isNotEmpty(validEnum)) {
					data.put("industry", validEnum.getString("enumId"));
				}
			}

			/*if (UtilValidate.isNotEmpty(data.get("customerTradingType")) && !ValidatorUtil.isValidEnum(delegator, data.get("customerTradingType").toString(), "DBS_CUST_TRD_TYPE")) {
				setValidate(false);
				message = "Invalid customer trading type" + " [Row No:" + rowNumber + "]";
				validationMessage.put("customerTradingType", message);
				errorCodes.add("E1023");
			} else if (UtilValidate.isNotEmpty(data.get("customerTradingType"))) {
				GenericValue validEnum = ValidatorUtil.getValidEnum(delegator, data.get("customerTradingType").toString(), "DBS_CUST_TRD_TYPE");
				if (UtilValidate.isNotEmpty(validEnum)) {
					data.put("customerTradingType", validEnum.getString("enumId"));
				}
			}*/

			/*if (UtilValidate.isNotEmpty(data.get("tallyUserType")) && !ValidatorUtil.isValidEnum(delegator, data.get("tallyUserType").toString(), "DBS_TALLY_USR_TYPE")) {
				setValidate(false);
				message = "Invalid tally user type" + " [Row No:" + rowNumber + "]";
				validationMessage.put("tallyUserType", message);
				errorCodes.add("E1024");
			} else if (UtilValidate.isNotEmpty(data.get("tallyUserType"))) {
				GenericValue validEnum = ValidatorUtil.getValidEnum(delegator, data.get("tallyUserType").toString(), "DBS_TALLY_USR_TYPE");
				if (UtilValidate.isNotEmpty(validEnum)) {
					data.put("tallyUserType", validEnum.getString("enumId"));
				}
			}*/

			/*if (UtilValidate.isNotEmpty(data.get("liabOrAsset")) && !ValidatorUtil.isValidEnum(delegator, data.get("liabOrAsset").toString(), "DBS_LA_TYPE")) {
				setValidate(false);
				message = "Invalid liabOrAsset" + " [Row No:" + rowNumber + "]";
				validationMessage.put("liabOrAsset", message);
				errorCodes.add("E1025");
			} else if (UtilValidate.isNotEmpty(data.get("liabOrAsset"))) {
				GenericValue validEnum = ValidatorUtil.getValidEnum(delegator, data.get("liabOrAsset").toString(), "DBS_LA_TYPE");
				if (UtilValidate.isNotEmpty(validEnum)) {
					data.put("liabOrAsset", validEnum.getString("enumId"));

					if ( (UtilValidate.isNotEmpty(data.get("rmCallingStatus")) && ValidatorUtil.isValidEnum(delegator, data.get("rmCallingStatus").toString(), "DBS_RM_CALL_STATUS"))
							&& !ValidatorUtil.isValidEnum(delegator, data.get("rmCallingStatus").toString(), "DBS_RM_CALL_STATUS", validEnum.getString("enumId"))
							) {
						setValidate(false);
						message = "Invalid rm calling status with wrong parent liabOrAsset combination" + " [Row No:" + rowNumber + "]";
						validationMessage.put("rmCallingStatus", message);
						errorCodes.add("E1071");
					}
				}
			}*/

			/*if (UtilValidate.isNotEmpty(data.get("constitution")) && !ValidatorUtil.isValidEnum(delegator, data.get("constitution").toString(), "DBS_CONSTITUTION")) {
				setValidate(false);
				message = "Invalid constitution" + " [Row No:" + rowNumber + "]";
				validationMessage.put("constitution", message);
				errorCodes.add("E1026");
			} else if (UtilValidate.isNotEmpty(data.get("constitution"))) {
				GenericValue validEnum = ValidatorUtil.getValidEnum(delegator, data.get("constitution").toString(), "DBS_CONSTITUTION");
				if (UtilValidate.isNotEmpty(validEnum)) {
					data.put("constitution", validEnum.getString("enumId"));
				}
			}

			if (UtilValidate.isNotEmpty(data.get("teleCallingStatus")) && !ValidatorUtil.isValidEnum(delegator, data.get("teleCallingStatus").toString(), "DBS_TELE_CALL_STATUS")) {
				setValidate(false);
				message = "Invalid tele calling status" + " [Row No:" + rowNumber + "]";
				validationMessage.put("teleCallingStatus", message);
				errorCodes.add("E1027");
			} else if (UtilValidate.isNotEmpty(data.get("teleCallingStatus"))) {
				GenericValue validEnum = ValidatorUtil.getValidEnum(delegator, data.get("teleCallingStatus").toString(), "DBS_TELE_CALL_STATUS");
				if (UtilValidate.isNotEmpty(validEnum)) {
					data.put("teleCallingStatus", validEnum.getString("enumId"));

					if ( (UtilValidate.isNotEmpty(data.get("teleCallingSubStatus")) && ValidatorUtil.isValidEnum(delegator, data.get("teleCallingSubStatus").toString(), "DBS_TELE_SUB_STATUS"))
							&& !ValidatorUtil.isValidEnum(delegator, data.get("teleCallingSubStatus").toString(), "DBS_TELE_SUB_STATUS", validEnum.getString("enumId"))
							) {
						setValidate(false);
						message = "Invalid tele calling sub status with wrong parent tele calling status combination" + " [Row No:" + rowNumber + "]";
						validationMessage.put("teleCallingSubStatus", message);
						errorCodes.add("E1069");
					}
				}
			}

			if (UtilValidate.isNotEmpty(data.get("rmCallingStatus")) && !ValidatorUtil.isValidEnum(delegator, data.get("rmCallingStatus").toString(), "DBS_RM_CALL_STATUS")) {
				setValidate(false);
				message = "Invalid rm calling status" + " [Row No:" + rowNumber + "]";
				validationMessage.put("rmCallingStatus", message);
				errorCodes.add("E1028");
			} else if (UtilValidate.isNotEmpty(data.get("rmCallingStatus"))) {
				GenericValue validEnum = ValidatorUtil.getValidEnum(delegator, data.get("rmCallingStatus").toString(), "DBS_RM_CALL_STATUS");
				if (UtilValidate.isNotEmpty(validEnum)) {
					data.put("rmCallingStatus", validEnum.getString("enumId"));

					if ( (UtilValidate.isNotEmpty(data.get("rmCallingSubStatus")) && ValidatorUtil.isValidEnum(delegator, data.get("rmCallingSubStatus").toString(), "DBS_RM_SUB_STATUS"))
							&& !ValidatorUtil.isValidEnum(delegator, data.get("rmCallingSubStatus").toString(), "DBS_RM_SUB_STATUS", validEnum.getString("enumId"))
							) {
						setValidate(false);
						message = "Invalid rm calling sub status with wrong parent rm calling status combination" + " [Row No:" + rowNumber + "]";
						validationMessage.put("rmCallingSubStatus", message);
						errorCodes.add("E1070");
					}
				}
			}*/

			/*if (UtilValidate.isNotEmpty(data.get("segment")) && (!data.get("segment").equals("IBG3") && !data.get("segment").equals("IBG4"))) {
				setValidate(false);
				message = "Invalid segment" + " [Row No:" + rowNumber + "]";
				validationMessage.put("segment", message);
				errorCodes.add("E1029");
			}

			if (UtilValidate.isNotEmpty(data.get("dateOfIncorporation")) && !ValidatorUtil.isValidDateFormat(data.get("dateOfIncorporation").toString())) {
				setValidate(false);
				message = "Invalid date of incorporation date format, should be dd-MM-yyyy" + " [Row No:" + rowNumber + "]";
				validationMessage.put("rmCallingStatus", message);
				errorCodes.add("E1030");
			}

			if (UtilValidate.isNotEmpty(data.get("tcpName")) && !ValidatorUtil.isValidEnum(delegator, data.get("tcpName").toString(), "DBS_TCP_NAME")) {
				setValidate(false);
				message = "Invalid TCP name" + " [Row No:" + rowNumber + "]";
				validationMessage.put("tcpName", message);
				errorCodes.add("E1033");
			} else if (UtilValidate.isNotEmpty(data.get("tcpName"))) {
				GenericValue validEnum = ValidatorUtil.getValidEnum(delegator, data.get("tcpName").toString(), "DBS_TCP_NAME");
				if (UtilValidate.isNotEmpty(validEnum)) {
					data.put("tcpName", validEnum.getString("enumId"));
				}
			}

			if (UtilValidate.isNotEmpty(data.get("placeOfIncorporation")) && !ValidatorUtil.isValidEnum(delegator, data.get("placeOfIncorporation").toString(), "DBS_PLACE_INCORP")) {
				setValidate(false);
				message = "Invalid place of incorporation" + " [Row No:" + rowNumber + "]";
				validationMessage.put("placeOfIncorporation", message);
				errorCodes.add("E1034");
			} else if (UtilValidate.isNotEmpty(data.get("placeOfIncorporation"))) {
				GenericValue validEnum = ValidatorUtil.getValidEnum(delegator, data.get("placeOfIncorporation").toString(), "DBS_PLACE_INCORP");
				if (UtilValidate.isNotEmpty(validEnum)) {
					data.put("placeOfIncorporation", validEnum.getString("enumId"));
				}
			}

			if (UtilValidate.isNotEmpty(data.get("otherBankName")) && !ValidatorUtil.isValidEnum(delegator, data.get("otherBankName").toString(), "DBS_EXISTING_BN")) {
				setValidate(false);
				message = "Invalid other bank name" + " [Row No:" + rowNumber + "]";
				validationMessage.put("otherBankName", message);
				errorCodes.add("E1035");
			} else if (UtilValidate.isNotEmpty(data.get("otherBankName"))) {
				GenericValue validEnum = ValidatorUtil.getValidEnum(delegator, data.get("otherBankName").toString(), "DBS_EXISTING_BN");
				if (UtilValidate.isNotEmpty(validEnum)) {
					data.put("otherBankName", validEnum.getString("enumId"));
				}
			}

			if (UtilValidate.isNotEmpty(data.get("productsHeldInOthBank")) && !ValidatorUtil.isValidEnum(delegator, data.get("productsHeldInOthBank").toString(), "DBS_PROD_HIOB")) {
				setValidate(false);
				message = "Invalid products held in other bank" + " [Row No:" + rowNumber + "]";
				validationMessage.put("productsHeldInOthBank", message);
				errorCodes.add("E1036");
			} else if (UtilValidate.isNotEmpty(data.get("productsHeldInOthBank"))) {
				GenericValue validEnum = ValidatorUtil.getValidEnum(delegator, data.get("productsHeldInOthBank").toString(), "DBS_PROD_HIOB");
				if (UtilValidate.isNotEmpty(validEnum)) {
					data.put("productsHeldInOthBank", validEnum.getString("enumId"));
				}
			}

			if (UtilValidate.isNotEmpty(data.get("jobFamily")) && !ValidatorUtil.isValidEnum(delegator, data.get("jobFamily").toString(), "DBS_JOB_FAMILY")) {
				setValidate(false);
				message = "Invalid Job Family" + " [Row No:" + rowNumber + "]";
				validationMessage.put("jobFamily", message);
				errorCodes.add("E1038");
			} else if (UtilValidate.isNotEmpty(data.get("jobFamily"))) {
				GenericValue validEnum = ValidatorUtil.getValidEnum(delegator, data.get("jobFamily").toString(), "DBS_JOB_FAMILY");
				if (UtilValidate.isNotEmpty(validEnum)) {
					data.put("jobFamily", validEnum.getString("enumId"));
				}
			}
			
			if (UtilValidate.isEmpty(data.get("jobFamily")) && !"Y".equals(leadShortForm) ) {
				setValidate(false);
				message = "Job Family is empty" + " [Row No:" + rowNumber + "]";
				validationMessage.put("jobFamily", message);
				errorCodes.add("E1041");
			}

			if (UtilValidate.isNotEmpty(data.get("teleCallingSubStatus")) && !ValidatorUtil.isValidEnum(delegator, data.get("teleCallingSubStatus").toString(), "DBS_TELE_SUB_STATUS")) {
				setValidate(false);
				message = "Invalid tele calling sub status" + " [Row No:" + rowNumber + "]";
				validationMessage.put("teleCallingSubStatus", message);
				errorCodes.add("E1043");
			} else if (UtilValidate.isNotEmpty(data.get("teleCallingSubStatus"))) {
				GenericValue validEnum = ValidatorUtil.getValidEnum(delegator, data.get("teleCallingSubStatus").toString(), "DBS_TELE_SUB_STATUS");
				if (UtilValidate.isNotEmpty(validEnum)) {
					data.put("teleCallingSubStatus", validEnum.getString("enumId"));
				}
			}

			if (UtilValidate.isNotEmpty(data.get("rmCallingSubStatus")) && !ValidatorUtil.isValidEnum(delegator, data.get("rmCallingSubStatus").toString(), "DBS_RM_SUB_STATUS")) {
				setValidate(false);
				message = "Invalid Job Family" + " [Row No:" + rowNumber + "]";
				validationMessage.put("rmCallingSubStatus", message);
				errorCodes.add("E1044");
			} else if (UtilValidate.isNotEmpty(data.get("rmCallingSubStatus"))) {
				GenericValue validEnum = ValidatorUtil.getValidEnum(delegator, data.get("rmCallingSubStatus").toString(), "DBS_RM_SUB_STATUS");
				if (UtilValidate.isNotEmpty(validEnum)) {
					data.put("rmCallingSubStatus", validEnum.getString("enumId"));
				}
			}

			if (UtilValidate.isEmpty(data.get("teleCallingStatus")) && UtilValidate.isNotEmpty(data.get("teleCallingSubStatus"))) {
				setValidate(false);
				message = "Without telecaller status, telecaller sub status is invalid" + " [Row No:" + rowNumber + "]";
				validationMessage.put("rmCallingSubStatus", message);
				errorCodes.add("E1045");
			}

			if (UtilValidate.isEmpty(data.get("rmCallingStatus")) && UtilValidate.isNotEmpty(data.get("rmCallingSubStatus"))) {
				setValidate(false);
				message = "Without RM call status, RM sub status is invalid" + " [Row No:" + rowNumber + "]";
				validationMessage.put("rmCallingSubStatus", message);
				errorCodes.add("E1046");
			}*/

			/*if (UtilValidate.isNotEmpty(data.get("designation")) && !ValidatorUtil.isValidEnum(delegator, data.get("designation").toString(), "DBS_LD_DESIGNATION")) {
				setValidate(false);
				message = "Invalid designation" + " [Row No:" + rowNumber + "]";
				validationMessage.put("designation", message);
				errorCodes.add("E1047");
			} else if (UtilValidate.isNotEmpty(data.get("designation"))) {
				GenericValue validEnum = ValidatorUtil.getValidEnum(delegator, data.get("designation").toString(), "DBS_LD_DESIGNATION");
				if (UtilValidate.isNotEmpty(validEnum)) {
					data.put("designation", validEnum.getString("enumId"));
				}
			}

			if (UtilValidate.isNotEmpty(data.get("finacleId")) && !ValidatorUtil.validateNumeric(data.get("finacleId").toString())) {
				setValidate(false);
				message = "Finacle Id should contain numbers only" + " [Row No:" + rowNumber + "]";
				validationMessage.put("finacleId", message);
				errorCodes.add("E1048");
			}*/

			if (UtilValidate.isNotEmpty(data.get("noOfEmployees")) && !ValidatorUtil.validateNumeric(data.get("noOfEmployees").toString())) {
				setValidate(false);
				message = "No Of Employees should contain numbers only" + " [Row No:" + rowNumber + "]";
				validationMessage.put("noOfEmployees", message);
				errorCodes.add("E1049");
			}
			if (UtilValidate.isNotEmpty(data.get("primaryPhoneNumber")) && !ValidatorUtil.validateNumeric(data.get("primaryPhoneNumber").toString())) {
				setValidate(false);
				message = "Primary Phone Number should contain numbers only" + " [Row No:" + rowNumber + "]";
				validationMessage.put("noOfEmployees", message);
				errorCodes.add("E1050");
			}
			if (UtilValidate.isNotEmpty(data.get("secondaryPhoneNumber")) && !ValidatorUtil.validateNumeric(data.get("secondaryPhoneNumber").toString())) {
				setValidate(false);
				message = "Secondary Phone Number should contain numbers only" + " [Row No:" + rowNumber + "]";
				validationMessage.put("secondaryPhoneNumber", message);
				errorCodes.add("E1066");
			}
			if (UtilValidate.isNotEmpty(data.get("primaryPhoneCountryCode")) && !ValidatorUtil.validateCountyCode(data.get("primaryPhoneCountryCode").toString())) {
				setValidate(false);
				message = "Country Code 1 should contain numbers and '+' only" + " [Row No:" + rowNumber + "]";
				validationMessage.put("primaryPhoneCountryCode", message);
				errorCodes.add("E1051");
			}
			if (UtilValidate.isNotEmpty(data.get("secondaryPhoneCountryCode")) && !ValidatorUtil.validateCountyCode(data.get("secondaryPhoneCountryCode").toString())) {
				setValidate(false);
				message = "Country Code 2 should contain numbers and '+' only" + " [Row No:" + rowNumber + "]";
				validationMessage.put("secondaryPhoneCountryCode", message);
				errorCodes.add("E1052");
			}
			if (UtilValidate.isNotEmpty(data.get("permanentAcccountNumber")) && !ValidatorUtil.validatePAN(data.get("permanentAcccountNumber").toString())) {
				setValidate(false);
				message = "PAN Number should accept 10 characters and Alphanumeric only"+ " [Row No:" + rowNumber + "]";
				validationMessage.put("permanentAcccountNumber", message);
				errorCodes.add("E1053");
			}
			if (UtilValidate.isNotEmpty(data.get("firstName")) && !ValidatorUtil.validateAlphabets(data.get("firstName").toString())) {
				setValidate(false);
				message = "First Name should not contain numbers " + " [Row No:" + rowNumber + "]";
				validationMessage.put("firstName", message);
				errorCodes.add("E1054");
			}
			if (UtilValidate.isNotEmpty(data.get("lastName")) && !ValidatorUtil.validateAlphabets(data.get("lastName").toString())) {
				setValidate(false);
				message = "Last Name should not contain numbers" + " [Row No:" + rowNumber + "]";
				validationMessage.put("lastName", message);
				errorCodes.add("E1055");
			}
			if (UtilValidate.isNotEmpty(data.get("keyContactPerson1")) && !ValidatorUtil.validateAlphabets(data.get("keyContactPerson1").toString())) {
				setValidate(false);
				message = "Key Contact Person 1 should not contain numbers " + " [Row No:" + rowNumber + "]";
				validationMessage.put("keyContactPerson1", message);
				errorCodes.add("E1056");
			}
			if (UtilValidate.isNotEmpty(data.get("keyContactPerson2")) && !ValidatorUtil.validateAlphabets(data.get("keyContactPerson2").toString())) {
				setValidate(false);
				message = "Key Contact Person 2 should not contain numbers" + " [Row No:" + rowNumber + "]";
				validationMessage.put("keyContactPerson2", message);
				errorCodes.add("E1057");
			}
			//if (UtilValidate.isNotEmpty(data.get("companyName")) && !ValidatorUtil.validateAlphabetsWithSplCharacters(data.get("companyName").toString())) {
			if (UtilValidate.isNotEmpty(data.get("companyName")) && !data.get("companyName").toString().matches("^[ A-Za-z0-9'@.!&:*()+-]{0,255}$")) {
				setValidate(false);
				message = "Invalid Company Name Format" + " [Row No:" + rowNumber + "]";
				validationMessage.put("companyName", message);
				errorCodes.add("E1058");
			}
			/*if (UtilValidate.isNotEmpty(data.get("dateOfIncorporation")) && ValidatorUtil.isFutureDate(data.get("dateOfIncorporation").toString())) {
				setValidate(false);
				message = "Date of Incorporation should not be future date" + " [Row No:" + rowNumber + "]";
				validationMessage.put("dateOfIncorporation", message);
				errorCodes.add("E1059");
			}
			if (UtilValidate.isNotEmpty(data.get("title")) && !ValidatorUtil.isValidEnum(delegator, data.get("title").toString(), "SALUTATION")) {
				setValidate(false);
				message = "Invalid Salutation " + " [Row No:" + rowNumber + "]";
				validationMessage.put("title", message);
				errorCodes.add("E1060");
			}

			if (UtilValidate.isNotEmpty(data.get("paidupCapital")) && !ValidatorUtil.validateNumericDecimal(data.get("paidupCapital").toString())) {
				setValidate(false);
				message = "Paid-Up Capital should be numbers and above lakhs" + " [Row No:" + rowNumber + "]";
				validationMessage.put("paidupCapital", message);
				errorCodes.add("E1063");
			}
			if (UtilValidate.isNotEmpty(data.get("productsValueInOthBank")) && !ValidatorUtil.validateNumericDecimal(data.get("productsValueInOthBank").toString())) {
				setValidate(false);
				message = "Product Value With Other Banks should be numbers and above lakhs" + " [Row No:" + rowNumber + "]";
				validationMessage.put("productsValueInOthBank", message);
				errorCodes.add("E1064");
			}
			if (UtilValidate.isNotEmpty(data.get("authorisedCap")) && !ValidatorUtil.validateNumericDecimal(data.get("authorisedCap").toString())) {
				setValidate(false);
				message = "Authorized Capital should be numbers and above lakhs" + " [Row No:" + rowNumber + "]";
				validationMessage.put("authorisedCap", message);
				errorCodes.add("E1065");
			}
			if (UtilValidate.isNotEmpty(data.get("otherBankBalance")) && !ValidatorUtil.validateNumericDecimal(data.get("otherBankBalance").toString())) {
				setValidate(false);
				message = "Total Cash Balances Maintained With Other Banks should be numbers and above lakhs" + " [Row No:" + rowNumber + "]";
				validationMessage.put("otherBankBalance", message);
				errorCodes.add("E1061");
			}
			if (UtilValidate.isNotEmpty(data.get("noOfAttempt")) && !ValidatorUtil.validateNumeric(data.get("noOfAttempt").toString())) {
				setValidate(false);
				message = "No of attempt should contain numbers only" + " [Row No:" + rowNumber + "]";
				validationMessage.put("noOfAttempt", message);
				errorCodes.add("E1067");
			}

			if (UtilValidate.isNotEmpty(data.get("salesTurnover")) && !ValidatorUtil.validateDefaultNumericDecimal(data.get("salesTurnover").toString())) {
				setValidate(false);
				message = "Sales Turn Over should contain only numbers or decimals" + " [Row No:" + rowNumber + "]";
				validationMessage.put("salesTurnover", message);
				errorCodes.add("E1068");
			}*/
			
			/*if (UtilValidate.isNotEmpty(data.get("leadScore")) && !ValidatorUtil.isValidEnum(delegator, data.get("leadScore").toString(), "LEAD_SCORE")) {
				setValidate(false);
				message = "Invalid lead score " + " [Row No:" + rowNumber + "]";
				validationMessage.put("title", message);
				errorCodes.add("E1085");
			} else if (UtilValidate.isNotEmpty(data.get("leadScore"))) {
				GenericValue validEnum = ValidatorUtil.getValidEnum(delegator, data.get("leadScore").toString(), "LEAD_SCORE");
				if (UtilValidate.isNotEmpty(validEnum)) {
					data.put("leadScore", validEnum.getString("enumId"));
				}
			}*/

			/*if ( (UtilValidate.isNotEmpty(data.get("city")) && ValidatorUtil.isValidGeo(delegator, data.get("city").toString(), "CITY"))
					&& (UtilValidate.isNotEmpty(data.get("jobFamily")) && ValidatorUtil.isValidEnum(delegator, data.get("jobFamily").toString(), "DBS_JOB_FAMILY"))
					&& UtilValidate.isNotEmpty(data.get("leadAssignTo"))
					) {
				
				String leadScore = UtilValidate.isNotEmpty(data.get("leadScore")) ? data.get("leadScore").toString() : null;
				
				if (UtilValidate.isEmpty(userLogin) || !userLogin.getString("partyId").equals(data.get("leadAssignTo"))) {
					if (!DataHelper.isResponsibleForParty(delegator, data.get("leadAssignTo").toString(), data.get("jobFamily").toString(), "IND", data.get("city").toString(), leadScore)

							) {
						setValidate(false);
						message = "Invalid Lead Assign To" + " [Row No:" + rowNumber + "]";
						validationMessage.put("leadAssignTo", message);
						errorCodes.add("E1072");
					}
				}

			}*/
			if (UtilValidate.isNotEmpty(data.get("primaryPhoneCountryCode")) &&  UtilValidate.isEmpty(data.get("primaryPhoneNumber")) ){
				setValidate(false);
				message = "Missing Phone Number 1" + " [Row No:" + rowNumber + "]";
				validationMessage.put("primaryPhoneNumber", message);
				errorCodes.add("E1074");
			}
			if (UtilValidate.isNotEmpty(data.get("secondaryPhoneCountryCode")) &&  UtilValidate.isEmpty(data.get("secondaryPhoneNumber")) ){
				setValidate(false);
				message = "Missing Phone Number 2" + " [Row No:" + rowNumber + "]";
				validationMessage.put("secondaryPhoneNumber", message);
				errorCodes.add("E1075");
			}
			
			if(UtilValidate.isNotEmpty(data.get("primaryPhoneNumber")) && !data.get("primaryPhoneNumber").toString().matches("^[0-9]{0,}$")) {
				setValidate(false);
				message = "Phone Number 1 should contain 10 digits" + " [Row No:" + rowNumber + "]";
				validationMessage.put("primaryPhoneNumber", message);
				errorCodes.add("E1076");
			}
			if(UtilValidate.isNotEmpty(data.get("secondaryPhoneNumber")) && !data.get("secondaryPhoneNumber").toString().matches("^[0-9]{0,}$")) {
				setValidate(false);
				message = "Phone Number 2 should contain 10 digits" + " [Row No:" + rowNumber + "]";
				validationMessage.put("secondaryPhoneNumber", message);
				errorCodes.add("E1077");
			}
			if(UtilValidate.isNotEmpty(data.get("primaryPhoneCountryCode")) && !data.get("primaryPhoneCountryCode").toString().matches("^[+]?[0-9]{1,2}$")) {
				setValidate(false);
				message = "Country Code 1 should contain 3 digits" + " [Row No:" + rowNumber + "]";
				validationMessage.put("primaryPhoneCountryCode", message);
				errorCodes.add("E1078");
			}
			if(UtilValidate.isNotEmpty(data.get("secondaryPhoneCountryCode")) && !data.get("secondaryPhoneCountryCode").toString().matches("^[+]?[0-9]{1,2}$")) {
				setValidate(false);
				message = "Country Code 2 should contain 3 digits" + " [Row No:" + rowNumber + "]";
				validationMessage.put("secondaryPhoneCountryCode", message);
				errorCodes.add("E1079");
			}

			if (UtilValidate.isNotEmpty(data.get("leadStatus")) && !ValidatorUtil.isValidEnum(delegator, data.get("leadStatus").toString(), "LEAD_STATUS_HISTORY")) {
				setValidate(false);
				message = "Invalid Lead Status" + " [Row No:" + rowNumber + "]";
				validationMessage.put("leadStatus", message);
				errorCodes.add("E1081");
			} else if (UtilValidate.isNotEmpty(data.get("leadStatus"))) {
				GenericValue validEnum = ValidatorUtil.getValidEnum(delegator, data.get("leadStatus").toString(), "LEAD_STATUS_HISTORY");
				if (UtilValidate.isNotEmpty(validEnum)) {
					data.put("leadStatus", validEnum.getString("enumId"));
				}
			}
			
			if (UtilValidate.isNotEmpty(data.get("isDedupAutoSegmentation")) && data.get("isDedupAutoSegmentation").equals("Y")) {
				
				if (errorCodes.contains("E1019")) {
					errorCodes.remove("E1019");
				} 
				if (errorCodes.contains("E1018")) {
					errorCodes.remove("E1018");
				} 
				if (errorCodes.contains("E1003")) {
					errorCodes.remove("E1003");
				} 
				if (errorCodes.size() == 0) {
					setValidate(true);
				}

			}

			String pkCombinedValueText = leadId + "::" + primaryPartyId;
			WriterUtil.writeValidationAudit(delegator, pkCombinedValueText, validationAuditLogList);
			
			if (!isValidate()) {

				response.put(EtlConstants.RESPONSE_CODE, ResponseCodes.BAD_REQUEST);
				response.put(EtlConstants.RESPONSE_MESSAGE, "Lead Data Validation Failed...!");

			} else {
				response.put(EtlConstants.RESPONSE_CODE, ResponseCodes.SUCCESS_CODE);
			}

			data.put("errorCodes", StringUtil.join(errorCodes, ","));

		} catch (Exception e) {
			Debug.log(e.getMessage(), MODULE);

			response.put(EtlConstants.RESPONSE_CODE, ResponseCodes.INTERNAL_SERVER_ERROR_CODE);
			response.put(EtlConstants.RESPONSE_MESSAGE, "Lead Data Validation Failed...!");

			return response;
		}

		response.put("data", data);
		response.put("validationMessage", validationMessage);
		
		Debug.log("response: "+response);
		Debug.log("dataAtEnd: "+data);

		return response;
	}

	public HashMap<String,String> getLeadDedupStatus(String leadId,String companyName,String email,Map phone,Map address,Delegator delegator) throws GenericEntityException{
		HashMap<String,String> results=new HashMap<String,String>();
		if (UtilValidate.isNotEmpty(leadId)) {
			//IF Lead Id NOT EMPTY
			/*Debug.log(leadId+""+companyName+" "+email+" "+phone+" "+address+"8888888888888888888888888");
			EntityCondition condition = EntityCondition.makeCondition(EntityOperator.OR,
					EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, leadId),
					EntityCondition.makeCondition("idValue", EntityOperator.EQUALS, leadId)
					);      

			GenericValue partyIdentification = EntityUtil.getFirst( delegator.findList("PartyIdentification", condition, null, UtilMisc.toList("-createdStamp"), null, false) );
			if (UtilValidate.isNotEmpty(partyIdentification)) {
				//leadId = partyIdentification.getString("idValue");
			}
			 */
			GenericValue party=null;
			boolean isDisalbed = false;
			party = EntityUtil.getFirst( delegator.findByAnd("Party", UtilMisc.toMap("partyId", leadId), null, false) );
			if (UtilValidate.isNotEmpty(party) && UtilValidate.isNotEmpty(party.getString("statusId")) && party.getString("statusId").equals("PARTY_DISABLED")) {
				isDisalbed = true;
			}

			//IF Lead Id MATCH FOUND AND IF NOT DISABLED
			if (UtilValidate.isNotEmpty(party)){
				String companyLeadId="";
				if(!isDisalbed){
					if(UtilValidate.isNotEmpty(companyName)){
						GenericValue partySupplementalData = EntityUtil.getFirst( delegator.findByAnd("PartySupplementalData", UtilMisc.toMap("companyName", companyName,"partyId",leadId), null, false) );
						if (UtilValidate.isNotEmpty(partySupplementalData)) {
							companyLeadId = partySupplementalData.getString("partyId");

							//update lead 
							results.put("leadId", companyLeadId);
							results.put("responseCode", "S101");
							return results;
						}else{
							//insert to error log
							results.put("leadId", leadId);
							results.put("originalLeadId", leadId);
							results.put("foundLeadId", "");
							results.put("errorMessage", "Lead Id "+leadId +"does not match with Company Name "+companyName);
							results.put("responseCode", "E1084");
							return results;

						}
					}

				}else{
					//insert to error log
					results.put("leadId", leadId);
					results.put("originalLeadId", leadId);
					results.put("foundLeadId", "");
					results.put("errorMessage", "Lead Id "+leadId +"is disabled");
					results.put("responseCode", "E1080");
					return results;


				}
			}else{
				//IF Lead Id MATCH NOT FOUND
				String companyLeadId="";
				String dedupedLeadId="";
				String reasonCode="";
				if(UtilValidate.isNotEmpty(companyName)){
					GenericValue partySupplementalData = EntityUtil.getFirst( delegator.findByAnd("PartySupplementalData", UtilMisc.toMap("companyName", companyName), null, false) );
					if (UtilValidate.isNotEmpty(partySupplementalData)) {
						//IF Lead Id MATCH NOT FOUND BUT COMPANY NAME EXACT MATCH 
						companyLeadId = partySupplementalData.getString("partyId");
						HashMap<String,String> dedupedLeadIdMap = validateContactInfo(email,phone,address,companyLeadId,delegator);
						Debug.log(dedupedLeadIdMap+"dedupedLeadIdMap001");
						if(UtilValidate.isNotEmpty(dedupedLeadIdMap)){

							dedupedLeadId = dedupedLeadIdMap.get("dedupedLeadId");
							reasonCode = dedupedLeadIdMap.get("reasonCode");
							if("E1083".equals(reasonCode)){
								results.put("leadId", companyLeadId);
								results.put("responseCode", "E1083");
								results.put("originalLeadId", leadId );
								results.put("foundLeadId", dedupedLeadIdMap.get("dedupedLeadId"));
								results.put("errorMessage", "Given Lead Id "+leadId+ "does not exist and Lead Id "+companyLeadId +" found for  Company Name does not match with Contact Information Lead Id "+dedupedLeadId+" found for the field "+dedupedLeadIdMap.get("dedupedField"));
								return results;
							}else if("S2222".equals(reasonCode)){
								results.put("leadId", companyLeadId);
								results.put("responseCode", "E1083");
								results.put("originalLeadId", leadId );
								results.put("foundLeadId", dedupedLeadId);
								results.put("errorMessage", "Given Lead Id "+leadId+" does not match with deduped Lead Id "+dedupedLeadId+" found for Company Name and Contact Information");

								return results;
							}else if("S2000".equals(reasonCode)){
								results.put("leadId", companyLeadId);
								results.put("responseCode", "E1083");
								results.put("originalLeadId", leadId );
								results.put("foundLeadId", dedupedLeadId);
								results.put("errorMessage", "Lead Id  "+leadId +" does not exist and given Contact Information does not match with Lead Id "+companyLeadId+ " found for Company Name");
								return results;
							}

						}
					}else{
						//IF Lead Id MATCH NOT FOUND and Company Name NOT EXACT MATCH
						//write logic to check the contact dup
						HashMap<String,String> dedupedLeadIdMap=validateContactInfo(email,phone,address,null,delegator);
						Debug.log(dedupedLeadIdMap+"dedupedLeadIdMap002");
						dedupedLeadId = dedupedLeadIdMap.get("dedupedLeadId");
						reasonCode = dedupedLeadIdMap.get("reasonCode");
						if("S2000".equals(reasonCode)){
							results.put("leadId", "");
							results.put("responseCode", "S103"); //create lead
							results.put("originalLeadId", "");
							return results;

						}else {
							results.put("leadId", dedupedLeadId);
							results.put("responseCode", "E1083"); //duplicate
							results.put("originalLeadId", leadId );
							results.put("foundLeadId", dedupedLeadId);
							results.put("errorMessage", "Lead Id "+leadId +" does not exist and duplicate Lead Id "+dedupedLeadId+" found for given Contact Information");
							return results;

						}
					}
				}
			}

		}else{
			//IF Lead Id IS EMPTY
			String companyLeadId="";
			GenericValue partySupplementalData = EntityUtil.getFirst( delegator.findByAnd("PartySupplementalData", UtilMisc.toMap("companyName", companyName), null, false) );
			if (UtilValidate.isNotEmpty(partySupplementalData)) {
				//IF Lead Id IS EMPTY AND Company Name EXACT MATCH
				companyLeadId = partySupplementalData.getString("partyId");
				if (UtilValidate.isEmpty(email) && UtilValidate.isEmpty(phone) && UtilValidate.isEmpty(address) ) {
					//Create Lead and assign to the RM/Centrail team to enter contact, again if any of the contact match move to error log as duplicate
					results.put("leadId", "");
					results.put("responseCode", "S104"); //create lead
					results.put("originalLeadId", "");
					return results;
				}
				HashMap<String,String> dedupedLeadIdMap = validateContactInfo(email,phone,address,companyLeadId,delegator);
				Debug.log(dedupedLeadIdMap+"dedupedLeadIdMap003");
				String	dedupedLeadId = dedupedLeadIdMap.get("dedupedLeadId");
				String	reasonCode = dedupedLeadIdMap.get("reasonCode");
				if(UtilValidate.isNotEmpty(reasonCode)){
					if("S2222".equals(reasonCode)){
						results.put("leadId", companyLeadId);
						results.put("responseCode", "S101"); //update
						return results;
					}else if("E1083".equals(reasonCode)){
						results.put("leadId", companyLeadId);
						results.put("responseCode", "E1083");
						results.put("originalLeadId", companyLeadId );
						results.put("foundLeadId", dedupedLeadId);
						results.put("errorMessage", "Lead Id "+companyLeadId +" found for Company Name does not match with deduped Lead Id "+dedupedLeadId+" found for Contact Information for the field "+dedupedLeadIdMap.get("dedupedField"));
						return results;
					}else if("S2000".equals(reasonCode)){
						results.put("leadId", companyLeadId);  //clarify
						results.put("originalLeadId", companyLeadId );
						results.put("foundLeadId", "");
						results.put("errorMessage", "Lead Id "+companyLeadId +" found for Company Name does not match with the given Contact Information");
						results.put("responseCode", "E1083"); 
						return results;
					}

				}else{
					//action 
				}
			}else{


				String phoneNum1 = ParamUtil.getString(phone, "phoneNum1");
				String phoneNum2 = ParamUtil.getString(phone, "phoneNum2");
				String ccdPhoneNum1 = ParamUtil.getString(phone, "ccdPhoneNum1");
				String ccdPhoneNum2 = ParamUtil.getString(phone, "ccdPhoneNum2");

				String address1 = ParamUtil.getString(phone, "address1");
				String city = ParamUtil.getString(phone, "city");
				String state = ParamUtil.getString(phone, "stateProvinceGeoId");
				String pincode = ParamUtil.getString(phone, "postalCode");
				//IF Lead Id IS EMPTY AND Company Name NOT EXACT MATCH
				if (UtilValidate.isEmpty(email) && UtilValidate.isEmpty(phoneNum1) && UtilValidate.isEmpty(phoneNum2)  && UtilValidate.isEmpty(ccdPhoneNum1) && UtilValidate.isEmpty(ccdPhoneNum2)    && UtilValidate.isEmpty(address1)  && UtilValidate.isEmpty(city) && UtilValidate.isEmpty(state) && UtilValidate.isEmpty(pincode)) {
					//IF Lead Id IS EMPTY AND Company Name NOT A EXACT MATCH AND CONTACT INFO EMPTY
					// Set the segment code to "Data enrich" and add the team responsible as Central Team. 
					results.put("leadId", "");
					results.put("responseCode", "S104"); 
					return results;

				}else{
					HashMap<String,String> dedupedLeadIdMap=validateContactInfo(email,phone,address,null,delegator);
					String	dedupedLeadId = dedupedLeadIdMap.get("dedupedLeadId");
					String	reasonCode = dedupedLeadIdMap.get("reasonCode");

					if("S2222".equals(reasonCode)){
						results.put("leadId", dedupedLeadId);
						results.put("responseCode", "S101"); //update
						return results;
					}else if("E1083".equals(reasonCode)){
						results.put("leadId", dedupedLeadIdMap.get("dedupedLeadId"));
						results.put("responseCode", "E1083");
						results.put("originalLeadId", dedupedLeadIdMap.get("partyId") );
						results.put("foundLeadId", dedupedLeadId);
						results.put("errorMessage", "No Lead found for the given Company Name but duplicate Lead Id "+dedupedLeadId+" found for the field "+dedupedLeadIdMap.get("dedupedField"));
						return results;
					}else if("S2000".equals(reasonCode)){
						results.put("leadId", "");
						results.put("responseCode", "S103"); //create a lead
						return results;
					}

				}
			}


		}
		return results;

	}
	public HashMap<String,String> validateContactInfo(String email,Map<String,String> phone,Map<String,String> address,String partyId,Delegator delegator) throws GenericEntityException{
		String leadId="";
		String emailLeadId="";
		String phoneLeadId="";
		String addressLeadId="";
		boolean emailMatch=false;
		boolean phoneMatch=false;
		boolean addressMatch=false;
		boolean fullDedup=false;
		HashMap<String,String> validContact=new HashMap<String,String>();
		Debug.log(email+" "+phone+" "+address+" "+partyId);
		HashMap <String,String> result=new HashMap<String,String>();
		if(UtilValidate.isNotEmpty(partyId)){
			if(UtilValidate.isNotEmpty(email)){
				GenericValue contactMech = EntityUtil.getFirst( delegator.findByAnd("ContactMech", UtilMisc.toMap("contactMechTypeId", "EMAIL_ADDRESS","infoString",email), UtilMisc.toList("-createdStamp"), false) );
				if(UtilValidate.isNotEmpty(contactMech)){
					String contactMechId=contactMech.getString("contactMechId");
					GenericValue partyContactMech = EntityUtil.getFirst( delegator.findByAnd("PartyContactMech", UtilMisc.toMap("contactMechId", contactMechId), UtilMisc.toList("-createdStamp"), false) );
					if(UtilValidate.isNotEmpty(partyContactMech)){
						emailLeadId=partyContactMech.getString("partyId");
						emailMatch=true;
						if(UtilValidate.isNotEmpty(partyId)){
							if(!emailLeadId.equals(partyId)){
								//write error log
								validContact.put("partyId", partyId);
								validContact.put("dedupedLeadId", emailLeadId);
								validContact.put("reasonCode", "E1083");
								validContact.put("dedupedField", "EMAIL");

								return validContact;
							}
						}

					}
				}

			}
			if(UtilValidate.isNotEmpty(phone)){
				//String ccdPhoneNum1=phone.get("phoneNum1");
				String phoneNum1=phone.get("phoneNum1");
				//String ccdPhoneNum2=phone.get("phoneNum2");
				String phoneNum2=phone.get("phoneNum2");
				
				if(UtilValidate.isNotEmpty(phoneNum1)){
					GenericValue contactMech = EntityUtil.getFirst( delegator.findByAnd("TelecomNumber", UtilMisc.toMap(/*"countryCode",ccdPhoneNum1 ,*/"contactNumber",phoneNum1), UtilMisc.toList("-createdStamp"), false) );
					Debug.log(contactMech+"contactMechcontactMechpp");

					if(UtilValidate.isNotEmpty(contactMech)){
						String contactMechId=contactMech.getString("contactMechId");
						GenericValue partyContactMech = EntityUtil.getFirst( delegator.findByAnd("PartyContactMech", UtilMisc.toMap("contactMechId", contactMechId), UtilMisc.toList("-createdStamp"), false) );
						if(UtilValidate.isNotEmpty(partyContactMech)){
							phoneLeadId=partyContactMech.getString("partyId");
							phoneMatch=true;
							Debug.log(phoneLeadId+"phoneLeadIdphoneLeadId");
							if(UtilValidate.isNotEmpty(partyId)){
								if(!phoneLeadId.equals(partyId)){
									//write error log
									validContact.put("partyId", partyId);
									validContact.put("dedupedLeadId", phoneLeadId);
									validContact.put("reasonCode", "E1083");
									validContact.put("dedupedField", "PHONE_NUM_1");

									return validContact;
								}
							}

						}
					}
				}
				if(UtilValidate.isNotEmpty(phoneNum2)){
					GenericValue contactMech2 = EntityUtil.getFirst( delegator.findByAnd("TelecomNumber", UtilMisc.toMap(/*"countryCode",ccdPhoneNum2 ,*/"contactNumber",phoneNum2), UtilMisc.toList("-createdStamp"), false) );
					if(UtilValidate.isNotEmpty(contactMech2)){
						Debug.log(phoneLeadId+"phoneLeadIdphoneLeadId002");
						String contactMechId=contactMech2.getString("contactMechId");
						GenericValue partyContactMech = EntityUtil.getFirst( delegator.findByAnd("PartyContactMech", UtilMisc.toMap("contactMechId", contactMechId), UtilMisc.toList("-createdStamp"), false) );
						if(UtilValidate.isNotEmpty(partyContactMech)){
							phoneLeadId=partyContactMech.getString("partyId");

							Debug.log(phoneLeadId+"phoneLeadIdphoneLeadId003");

							phoneMatch=true;
							if(UtilValidate.isNotEmpty(partyId)){
								if(!phoneLeadId.equals(partyId)){
									//write error log
									validContact.put("partyId", partyId);
									validContact.put("dedupedLeadId", phoneLeadId);
									validContact.put("reasonCode", "E1083");
									validContact.put("dedupedField", "PHONE_NUM_2");

									return validContact;
								}
							}

						}
					}
				}


			}
			if(UtilValidate.isNotEmpty(address)){
				String address1=address.get("address1");
				/*String address2=address.get("address2");
				String city=address.get("city");
				String state=address.get("stateProvinceGeoId");
				String pincode=address.get("postalCode");*/
				Debug.log(address1);	
				
				if(UtilValidate.isNotEmpty(address1)){
				GenericValue contactMech = EntityUtil.getFirst( delegator.findByAnd("PostalAddress", UtilMisc.toMap("address1",address1), UtilMisc.toList("-createdStamp"), false) );
				Debug.log(contactMech+"contactMechcontactMechcontactMechAddress");
					if(UtilValidate.isNotEmpty(contactMech)){
						String contactMechId=contactMech.getString("contactMechId");
						GenericValue partyContactMech = EntityUtil.getFirst( delegator.findByAnd("PartyContactMech", UtilMisc.toMap("contactMechId", contactMechId), UtilMisc.toList("-createdStamp"), false) );
						if(UtilValidate.isNotEmpty(partyContactMech)){
							addressLeadId=partyContactMech.getString("partyId");
							Debug.log(addressLeadId+"addressLeadIdaddressLeadId");
							addressMatch=true;
							if(UtilValidate.isNotEmpty(partyId)){
								if(!addressLeadId.equals(partyId)){
									//write error log
									validContact.put("partyId", partyId);
									validContact.put("dedupedLeadId", addressLeadId);
									validContact.put("reasonCode", "E1083");
									validContact.put("dedupedField", "ADDRESS");

									return validContact;
								}
							}

						}
					}
				}

			}
			if(emailMatch && phoneMatch && addressMatch){

				//write error log
				validContact.put("partyId", partyId);
				validContact.put("dedupedLeadId", partyId);
				validContact.put("reasonCode", "S2222");
				return validContact;
			}
			validContact.put("partyId", partyId);
			validContact.put("reasonCode", "S2000");
			return validContact;
		}else{

			if(UtilValidate.isNotEmpty(email)){
				GenericValue contactMech = EntityUtil.getFirst( delegator.findByAnd("ContactMech", UtilMisc.toMap("contactMechTypeId", "EMAIL_ADDRESS","infoString",email), UtilMisc.toList("-createdStamp"), false) );
				if(UtilValidate.isNotEmpty(contactMech)){
					String contactMechId=contactMech.getString("contactMechId");
					GenericValue partyContactMech = EntityUtil.getFirst( delegator.findByAnd("PartyContactMech", UtilMisc.toMap("contactMechId", contactMechId), UtilMisc.toList("-createdStamp"), false) );
					if(UtilValidate.isNotEmpty(partyContactMech)){
						//write error log
						validContact.put("partyId", partyId);
						emailLeadId=partyContactMech.getString("partyId");
						Debug.log(emailLeadId+"phoneLeadIdphoneLeadId222");
						validContact.put("dedupedLeadId", emailLeadId);
						validContact.put("reasonCode", "E1083");
						emailMatch=true;
						//return validContact;
					}
				}
			}
			if(UtilValidate.isNotEmpty(phone)){
				//String ccdPhoneNum1=phone.get("phoneNum1");
				String phoneNum1=phone.get("phoneNum1");
				//String ccdPhoneNum2=phone.get("phoneNum2");
				String phoneNum2=phone.get("phoneNum2");
				if(UtilValidate.isNotEmpty(phoneNum1)){
					GenericValue contactMech = EntityUtil.getFirst( delegator.findByAnd("TelecomNumber", UtilMisc.toMap(/*"countryCode",ccdPhoneNum1 ,*/"contactNumber",phoneNum1), UtilMisc.toList("-createdStamp"), false) );
					if(UtilValidate.isNotEmpty(contactMech)){
						String contactMechId=contactMech.getString("contactMechId");
						GenericValue partyContactMech = EntityUtil.getFirst( delegator.findByAnd("PartyContactMech", UtilMisc.toMap("contactMechId", contactMechId), UtilMisc.toList("-createdStamp"), false) );
						if(UtilValidate.isNotEmpty(partyContactMech)){
							phoneLeadId = partyContactMech.getString("partyId");
							Debug.log(phoneLeadId+"phoneLeadIdphoneLeadId111");
							phoneMatch = true;
							if(UtilValidate.isNotEmpty(emailLeadId)){
								if(!phoneLeadId.equals(emailLeadId)){
									//write error log

									validContact.put("partyId", partyId);
									phoneLeadId=partyContactMech.getString("partyId");
									validContact.put("dedupedLeadIdMismatch", emailLeadId);
									validContact.put("dedupedLeadId", phoneLeadId);
									validContact.put("reasonCode", "E1083");
									validContact.put("dedupedField", "PHONE_NUM_1");

									return validContact;

								}
							}else{

							}

						}
					}
				}
				if(UtilValidate.isNotEmpty(phoneNum2)){
				GenericValue contactMech2 = EntityUtil.getFirst( delegator.findByAnd("TelecomNumber", UtilMisc.toMap(/*"countryCode",ccdPhoneNum2 ,*/"contactNumber",phoneNum2), UtilMisc.toList("-createdStamp"), false) );
				if(UtilValidate.isNotEmpty(contactMech2)){
					String contactMechId=contactMech2.getString("contactMechId");
					GenericValue partyContactMech = EntityUtil.getFirst( delegator.findByAnd("PartyContactMech", UtilMisc.toMap("contactMechId", contactMechId), UtilMisc.toList("-createdStamp"), false) );
					if(UtilValidate.isNotEmpty(partyContactMech)){
						phoneLeadId=partyContactMech.getString("partyId");
						phoneMatch=true;
						Debug.log(phoneLeadId+"phoneLeadIdphoneLeadId222");
						if(UtilValidate.isNotEmpty(emailLeadId)){
							if(!phoneLeadId.equals(emailLeadId)){
								//write error log

								//write error log

								validContact.put("partyId", partyId);
								phoneLeadId=partyContactMech.getString("partyId");
								validContact.put("dedupedLeadIdMismatch", emailLeadId);
								validContact.put("dedupedLeadId", phoneLeadId);
								validContact.put("reasonCode", "E1083");
								validContact.put("dedupedField", "PHONE_NUM_2");

								return validContact;

							}
						}else{

						}

					}
				}
			}

			}
			if(UtilValidate.isNotEmpty(address)){
				String address1=address.get("address1");
				/*String address2=address.get("address2");
				String city=address.get("city");
				String state=address.get("stateProvinceGeoId");
				String pincode=address.get("pincode");*/
				if(UtilValidate.isNotEmpty(address1)){
					GenericValue contactMech = EntityUtil.getFirst( delegator.findByAnd("PostalAddress", UtilMisc.toMap("address1",address1), UtilMisc.toList("-createdStamp"), false) );
					if(UtilValidate.isNotEmpty(contactMech)){
						String contactMechId=contactMech.getString("contactMechId");
						GenericValue partyContactMech = EntityUtil.getFirst( delegator.findByAnd("PartyContactMech", UtilMisc.toMap("contactMechId", contactMechId), UtilMisc.toList("-createdStamp"), false) );
						if(UtilValidate.isNotEmpty(partyContactMech)){
							addressLeadId=partyContactMech.getString("partyId");
							addressMatch=true;
							if(UtilValidate.isNotEmpty(emailLeadId)){
								if(!addressLeadId.equals(emailLeadId)){
									//write error log

									validContact.put("partyId", partyId);
									phoneLeadId=partyContactMech.getString("partyId");
									validContact.put("dedupedLeadIdMismatch", emailLeadId);
									validContact.put("dedupedLeadId", addressLeadId);
									validContact.put("reasonCode", "E1083");
									validContact.put("dedupedField", "ADDRESS");

									return validContact;

								}
							}else{

							}

						}
					}
				}

			}
			if(emailMatch && phoneMatch && addressMatch){
				fullDedup=true;
				//write error log
				validContact.put("partyId", partyId);
				validContact.put("dedupedLeadId", partyId);
				validContact.put("reasonCode", "S2222");

				return validContact;

			}
			if(emailMatch || phoneMatch || addressMatch){
				//throw in error log as duplicate with the original 
				//write error log
				if(emailMatch)
				{
					validContact.put("partyId", partyId);
					validContact.put("dedupedLeadId", emailLeadId);
					validContact.put("reasonCode", "E1083");
					validContact.put("dedupedField", "EMAIL");

				}else if(phoneMatch){
					validContact.put("partyId", partyId);
					validContact.put("dedupedLeadId", phoneLeadId);
					validContact.put("reasonCode", "E1083");
					validContact.put("dedupedField", "PHONE_NUM_1");

				}else if(addressMatch){
					validContact.put("partyId", partyId);
					validContact.put("dedupedLeadId", addressLeadId);
					validContact.put("reasonCode", "E1083");
					validContact.put("dedupedField", "ADDRESS");
				}

				return validContact;

			}

			validContact.put("reasonCode", "S2000");
			return validContact;

		}

	}



	public boolean isValidate() {
		return validate;
	}

	public void setValidate(boolean validate) {
		this.validate = validate;
	}

}
