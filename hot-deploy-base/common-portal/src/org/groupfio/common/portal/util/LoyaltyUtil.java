package org.groupfio.common.portal.util;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.fio.homeapps.util.PartyHelper;
import org.fio.homeapps.util.UtilDateTime;
import org.fio.homeapps.util.DataUtil;
import org.groupfio.common.portal.CommonPortalConstants;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

/**
 * 
 * @author Mano Godwin M
 *
 */
public class LoyaltyUtil {

	private static String MODULE = LoyaltyUtil.class.getName();

	private LoyaltyUtil() {
	};

	public static String assignLoyaltyId(String partyId, Delegator delegator) {

		GenericValue userLogin = null;
		try {
			userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "admin"), false);

			String loyaltyCode = "";

			GenericValue loyaltyNumber = null;
			try {
				loyaltyNumber = EntityUtil.getFirst(delegator.findByAnd("LoyaltyNumber",
						UtilMisc.toMap("partyId", partyId), UtilMisc.toList("createdStamp DESC"), false));
				if (UtilValidate.isNotEmpty(loyaltyNumber))
					loyaltyCode = loyaltyNumber.getString("loyaltyCode");
			} catch (GenericEntityException e1) {
				e1.printStackTrace();
			}
			if (UtilValidate.isEmpty(loyaltyCode)) {
				loyaltyCode = generateUniqueLoyaltyNumber(delegator,
						UtilMisc.toMap("customerPartyId", partyId, "userLogin", userLogin));
			}

			if (UtilValidate.isNotEmpty(loyaltyCode)) {
				String partyIdentification=org.fio.homeapps.util.DataUtil.storePartyIdentification(delegator, partyId,loyaltyCode,"LOYALTY_ID");

				GenericValue personPartyGroupGV = delegator.findOne("Person", UtilMisc.toMap("partyId", partyId),
						false);
				if (UtilValidate.isEmpty(personPartyGroupGV))
					personPartyGroupGV = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", partyId), false);
				personPartyGroupGV.put("loyaltyId", loyaltyCode);
				personPartyGroupGV.put("isLoyaltyEnabled", "Y");
				personPartyGroupGV.store();
			}

			return loyaltyCode;

		} catch (GenericEntityException ex) {
			Debug.logError(ex.getMessage(), MODULE);
		}

		return null;

	}

	public static String validateLoyaltyCustomer(Delegator delegator, String partyId) {

		String message = "success";
		List<EntityCondition> conditions = UtilMisc.<EntityCondition>toList(
				EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId),
				EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "PRIMARY_LOCATION"));
		EntityCondition findConditions = EntityCondition.makeCondition(conditions, EntityOperator.AND);
		EntityFindOptions findOpt = new EntityFindOptions();
		findOpt.setDistinct(true);
		List<String> orderBy = FastList.newInstance();
		orderBy.add("fromDate DESC");
		
		List<EntityCondition> emailConditions = UtilMisc.<EntityCondition>toList(
				EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId),
				EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "PRIMARY_EMAIL"));
		EntityCondition findEmailConditions = EntityCondition.makeCondition(emailConditions, EntityOperator.AND);

		List<String> emptyFieldList = new ArrayList<String>();
		Map<String,String> reqFields = null;
		List<GenericValue> partyCtmcPurpose;
		List<GenericValue> partyEmailCtmcPurpose= FastList.newInstance();;
		try {
			String loyaltyCustomerPermission = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "LOYALTY_CUSTOMER_VALIDATION");
			GenericValue person = EntityQuery.use(delegator).from("Person").where("partyId", partyId).queryOne();
			partyCtmcPurpose = delegator.findList("PartyContactMechPurpose", findConditions, null, null, findOpt, false);
			partyCtmcPurpose = EntityUtil.filterByDate(partyCtmcPurpose);
			if (UtilValidate.isNotEmpty(partyCtmcPurpose)) {
				GenericValue partyContactmech = EntityUtil.getFirst(partyCtmcPurpose);
				partyCtmcPurpose = delegator.findByAnd("PartyContactMech",
						UtilMisc.toMap("partyId", partyContactmech.getString("partyId"), "contactMechId",
								partyContactmech.getString("contactMechId")), null, false);
			}
			partyCtmcPurpose = EntityUtil.filterByDate(partyCtmcPurpose, true);
			GenericValue postalAddress = null;
			if (UtilValidate.isNotEmpty(partyCtmcPurpose)) {
				GenericValue partyCtmcPurposeNew = EntityUtil.getFirst(partyCtmcPurpose);
				postalAddress = delegator.findOne("PostalAddress",
						UtilMisc.toMap("contactMechId", partyCtmcPurposeNew.getString("contactMechId")), false);
			}
			//emailConditions
			partyEmailCtmcPurpose = delegator.findList("PartyContactMechPurpose", findEmailConditions, null, null, findOpt, false);
			partyEmailCtmcPurpose = EntityUtil.filterByDate(partyEmailCtmcPurpose);
			GenericValue emailContctMechData =null;
			if (UtilValidate.isNotEmpty(loyaltyCustomerPermission) && loyaltyCustomerPermission.equals("Y")) {
				if (UtilValidate.isNotEmpty(partyEmailCtmcPurpose)) {
					emailContctMechData = EntityUtil.getFirst(partyEmailCtmcPurpose);
					String contactMechId = emailContctMechData.getString("contactMechId");
					GenericValue partyContactMech = EntityQuery.use(delegator).from("PartyContactMech").where("partyId", partyId, 
							"contactMechId",contactMechId).queryOne();
					if (UtilValidate.isNotEmpty(partyContactMech)) {
						String allowSolicitation = partyContactMech.getString("allowSolicitation");
						if (UtilValidate.isEmpty(allowSolicitation))
							message = "success";
						else if(allowSolicitation.equals("N"))
							return "Customer is not eligible to enable a loyalty number";
						else {}
					}
				}else {
					return "Customer is not eligible to enable a loyalty number";
				}
				GenericValue contactMech = null;
				if (UtilValidate.isNotEmpty(emailContctMechData))
					contactMech = delegator.findOne("ContactMech", UtilMisc.toMap("contactMechId", emailContctMechData.getString("contactMechId")), false);
				String primaryEmail = (UtilValidate.isNotEmpty(contactMech)?contactMech.getString("infoString"):"");
				String firstName = (UtilValidate.isNotEmpty(person)?person.getString("firstName"):"");
				String lastName = (UtilValidate.isNotEmpty(person)?person.getString("lastName"):"");
				reqFields = UtilMisc.toMap("Email",primaryEmail, "First Name",firstName, "Last Name",lastName);
			}
			/*String generalAddress1 = (UtilValidate.isNotEmpty(postalAddress)?postalAddress.getString("address1"):"");
			String generalCity = (UtilValidate.isNotEmpty(postalAddress)?postalAddress.getString("city"):"");
			String generalStateProvinceGeoId = (UtilValidate.isNotEmpty(postalAddress)?postalAddress.getString("stateProvinceGeoId"):"");
			String generalPostalCode = (UtilValidate.isNotEmpty(postalAddress)?postalAddress.getString("postalCode"):"");
			String generalCountryGeoId = (UtilValidate.isNotEmpty(postalAddress)?postalAddress.getString("countryGeoId"):"");
			String loyaltyPostalPermission = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "LOYALTY_CUST_POSTAL_ADDR_VALIDATION");
			if (UtilValidate.isNotEmpty(loyaltyPostalPermission) && loyaltyPostalPermission.equals("Y")) {
				reqFields = UtilMisc.toMap("Zip",generalPostalCode, "Address Line 1",generalAddress1,"City" , generalCity,"Country",generalCountryGeoId,"State",generalStateProvinceGeoId);
			}*/
			if (UtilValidate.isNotEmpty(reqFields)) {
				for(String reqField : reqFields.keySet()) {
					if(UtilValidate.isEmpty(reqFields.get(reqField)))
						emptyFieldList.add(reqField);
				}
			}
			if (UtilValidate.isNotEmpty(emptyFieldList))
				message = "Customer is not eligible to enable loyalty number : Required field(s) " + emptyFieldList +" missing";
			
		} catch (GenericEntityException gex) {
			Debug.logError(gex.getMessage(), MODULE);
		}
		return message;
	}

	public static synchronized String generateUniqueLoyaltyNumber(Delegator delegator, Map<String, Object> context) {
		String loyaltyNum = "";
		try {
			GenericValue userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "admin"), false);
			String isEnableLoyaltyProgram  = DataUtil.getGlobalValue(delegator,"ENABLE_LOYALTY_PROGRAM","NO");
			String brand = StringUtils.defaultIfEmpty(DataUtil.getGlobalValue(delegator, "DEFAULT_LOYALTY_BRAND_CODE"), "10");
			String defaultOrganizationId = StringUtils.defaultIfEmpty(DataUtil.getGlobalValue(delegator, "DEFAULT_ORGANIZATION_ID"), "Company");
			
			if ("NO".equalsIgnoreCase(isEnableLoyaltyProgram))
				return loyaltyNum;

			String customerPartyId = (String) context.get("customerPartyId");
			String organizationId = (String) context.get("organizationId");
			Timestamp fromDate = (Timestamp) context.get("fromDate");
			Timestamp thruDate = (Timestamp) context.get("thruDate");
			String description = (String) context.get("description");

			organizationId = UtilValidate.isEmpty(organizationId) ? defaultOrganizationId : organizationId;

			loyaltyNum = getGeneratedFormattedLoyaltyNumber(brand,
					getNextLoyaltySeqValue(delegator, CommonPortalConstants.CMConstants.DEFAULT_LN_CONFIG_ID));

			GenericValue ln = delegator.findOne("LoyaltyNumber", UtilMisc.toMap("loyaltyCode", loyaltyNum), false);

			if (UtilValidate.isEmpty(ln)) {
				createLoyaltyNumber(delegator, userLogin, loyaltyNum, customerPartyId, organizationId, fromDate,
						thruDate, description);
				Debug.log("check the loyalty number");
			} else {
				generateUniqueLoyaltyNumber(delegator, context);
			}

			resetDefaultLoyaltyNumberConfig(delegator, CommonPortalConstants.CMConstants.DEFAULT_LN_CONFIG_ID);

		} catch (Exception e) {
			Debug.logError(e.getMessage(), MODULE);
		}
		return loyaltyNum;
	}

	private static String getNextLoyaltySeqValue(Delegator delegator, String loyaltyNumberConfigId) {
		try {
			GenericValue loyaltyNumberConfig = delegator.findOne("LoyaltyNumberConfig", false,
					UtilMisc.toMap("loyaltyNumberConfigId", loyaltyNumberConfigId));
			if (UtilValidate.isNotEmpty(loyaltyNumberConfig))
				return loyaltyNumberConfig.getString("seqValue");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static void resetDefaultLoyaltyNumberConfig(Delegator delegator, String loyaltyNumberConfigId) {
		Long seqValue = Long.parseLong("0");
		try {
			GenericValue loyaltyNumberConfig = delegator.findOne("LoyaltyNumberConfig", false,
					UtilMisc.toMap("loyaltyNumberConfigId", loyaltyNumberConfigId));
			if (UtilValidate.isEmpty(loyaltyNumberConfig)) {
				loyaltyNumberConfig = delegator.makeValue("LoyaltyNumberConfig",
						UtilMisc.toMap("loyaltyNumberConfigId", loyaltyNumberConfigId));
			} else
				try {
					seqValue = Long.parseLong(loyaltyNumberConfig.getString("seqValue"))+1;
				} catch (Exception e) {
					Debug.logError(e.getMessage(), MODULE);
				}

			loyaltyNumberConfig.set("seqValue", seqValue);
			delegator.createOrStore(loyaltyNumberConfig);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void createLoyaltyNumber(Delegator delegator, GenericValue userLogin, String loyaltyCode,
			String customerPartyId, String organizationId, Timestamp fromDate, Timestamp thruDate, String description)
			throws GenericEntityException {
		GenericValue ln = delegator.makeValue("LoyaltyNumber", UtilMisc.toMap("loyaltyCode", loyaltyCode));

		ln.put("partyId", customerPartyId);
		ln.put("organizationId", organizationId);
		ln.put("fromDate", fromDate);
		ln.put("thruDate", thruDate);

		ln.put("createdByUserLogin", userLogin.getString("partyId"));
		ln.put("loyaltyCodeStatusId", CommonPortalConstants.CMConstants.LoyaltyCodeStatus.LOYALTY_CODE_STATUS_CREATED);
		ln.put("description", description);
		ln.create();
	}

	public static String getGeneratedFormattedLoyaltyNumber(String brand, String seqValue) {

		int defaultCk = CommonPortalConstants.CMConstants.DEFAULT_CHECK_NUMBER;

		String genNum = brand + StringUtils.leftPad(seqValue, 8,"0") + defaultCk;
		Long mod10Value = DataHelper.mod10(genNum, genNum.length());

		genNum = genNum.substring(0, genNum.length() - 1) + mod10Value;

		return genNum;
	}

	public static String getConfigurationParameterValue(Delegator delegator, String parameterId) {
		try {
			GenericValue configurationParameter = delegator.findOne("ConfigurationParameters",
					UtilMisc.toMap("parameterId", parameterId), false);
			if (UtilValidate.isNotEmpty(configurationParameter))
				return configurationParameter.getString("value");
		} catch (Exception e) {
			Debug.logError(e.getMessage(), MODULE);
		}
		return null;
	}

	public static void updatePartyLoyaltyNumberHistory(Delegator delegator, String partyId, String loyaltyNo,
			String statusId, Timestamp date) {
		try {
			GenericValue pretailLoyaltyNumberHistory = delegator.makeValidValue("PretailLoyaltyNumberHistory");

			pretailLoyaltyNumberHistory.put("partyId", partyId);
			pretailLoyaltyNumberHistory.put("loyaltyId", loyaltyNo);
			pretailLoyaltyNumberHistory.put("statusId", statusId);
			pretailLoyaltyNumberHistory.put("date", date);
			pretailLoyaltyNumberHistory.create();
		} catch (Exception e) {
			Debug.logError(e.getMessage(), MODULE);
		}
	}

	public static GenericValue expireLoyaltyNumber(Delegator delegator, String partyId) {
		GenericValue loyaltyNumberGvForRemove = null;
		try {
			
				GenericValue partyIdentificationValue=org.fio.homeapps.util.DataUtil.getPartyIdentification(delegator, partyId, "LOYALTY_ID");
				if (UtilValidate.isNotEmpty(partyIdentificationValue)) {
					String loyaltyNo =partyIdentificationValue.getString("idValue");

				LoyaltyUtil.updatePartyLoyaltyNumberHistory(delegator, partyId, loyaltyNo, "EXPIRED",
						UtilDateTime.nowTimestamp());
				partyIdentificationValue.remove();

				if (UtilValidate.isNotEmpty(loyaltyNo)) {
					GenericValue loyaltyNumberCust = delegator.findOne("LoyaltyNumber",
							UtilMisc.toMap("loyaltyCode", loyaltyNo), false);
					if (UtilValidate.isNotEmpty(loyaltyNumberCust)) {
						loyaltyNumberCust.set("loyaltyCodeStatusId", "DISSABLED");
						loyaltyNumberGvForRemove = loyaltyNumberCust;
					}
				}

				List<GenericValue> loyaltyPointsList = delegator.findByAnd("FioLoyaltyPoints",
						UtilMisc.toMap("partyId", partyId, "loyaltyId", loyaltyNo), null, false);
				if (UtilValidate.isNotEmpty(loyaltyPointsList)) {

					Double balancePoints = getBalancePoints(delegator, partyId);

					if (balancePoints != 0) {

						GenericValue flp = delegator.makeValue("FioLoyaltyPoints",
								UtilMisc.toMap("loyaltyType", "REDEEMED"));

						flp.put("partyId", partyId);
						flp.put("fioLoyaltyPointsId", delegator.getNextSeqId("FioLoyaltyPoints"));
						flp.put("channelId", "WEB");
						flp.put("transactionType", "LOYALTY_CAMPAIGN");
						flp.put("transactionDate", UtilDateTime.nowTimestamp());
						flp.put("uomLoyalty", "CurrToPoints");
						flp.put("redeemedValue", balancePoints);

						flp.put("loyaltyId", loyaltyNo);
						flp.put("uomLoyaltyValue", balancePoints.longValue());

						flp.create();

					}
				}
			}
		} catch (Exception e) {
			Debug.logError(e.getMessage(), MODULE);
		}
		return loyaltyNumberGvForRemove;
	}

	public static Double getBalancePoints(Delegator delegator, String partyId) {
		Double totalEarned = 0.00;
		Double totalRedeemed = 0.00;
		Double balancePoints = 0.00;

		List<GenericValue> loyaltyPointsList = null;
		try {
			String getPartyIdentification=org.fio.homeapps.util.DataUtil.getPartyIdentificationValue(delegator, partyId, "LOYALTY_ID");
			if (UtilValidate.isNotEmpty(getPartyIdentification)) 
				loyaltyPointsList = delegator.findByAnd("FioLoyaltyPoints",
						UtilMisc.toMap("partyId", partyId, "loyaltyId",getPartyIdentification), null,
						false);

			if (UtilValidate.isNotEmpty(loyaltyPointsList)) {

				for (GenericValue fioLoyalty : loyaltyPointsList) {
					if ("EARNED".equals(fioLoyalty.getString("loyaltyType"))) {
						if (UtilValidate.isNotEmpty(fioLoyalty.getString("uomLoyaltyValue"))) {
							totalEarned = totalEarned
									+ Double.parseDouble(fioLoyalty.getString("uomLoyaltyValue").trim());
						}
					} else {
						if (UtilValidate.isNotEmpty(fioLoyalty.getString("uomLoyaltyValue"))) {
							totalRedeemed = totalRedeemed
									+ Double.parseDouble(fioLoyalty.getString("uomLoyaltyValue").trim());
						}
					}
				}

				if (totalEarned > totalRedeemed) {
					balancePoints = totalEarned - totalRedeemed;
				}

			}
		} catch (GenericEntityException e1) {
			Debug.logError(e1.getMessage(), MODULE);
		}
		return balancePoints;
	}

	public static Map<String, Object> replaceLoyalty(Delegator delegator, String partyId) {
		Map<String, Object> errorMessageMap = FastMap.newInstance();

		if (UtilValidate.isNotEmpty(partyId)) {
			String message = LoyaltyUtil.validateLoyaltyCustomer(delegator, partyId);

			if (!"success".equals(message)) {
				errorMessageMap.put("_ERROR_MESSAGE_", message);
				return errorMessageMap;
			}
			Double balancePoints = LoyaltyUtil.getBalancePoints(delegator, partyId);
			try {
				String loyaltyCode = "";
				GenericValue userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "admin"), false);
				loyaltyCode = LoyaltyUtil.generateUniqueLoyaltyNumber(delegator,
						UtilMisc.toMap("customerPartyId", partyId, "userLogin", userLogin));

				if (balancePoints > 0) {

					GenericValue flp = delegator.makeValue("FioLoyaltyPoints",
							UtilMisc.toMap("loyaltyType", "REDEEMED"));

					flp.put("partyId", partyId);
					flp.put("fioLoyaltyPointsId", delegator.getNextSeqId("FioLoyaltyPoints"));
					flp.put("loyaltyType", "EARNED");
					flp.put("channelId", "WEB");
					flp.put("transactionType", "LOYALTY_CAMPAIGN");
					flp.put("transactionDate", UtilDateTime.nowTimestamp());
					flp.put("uomLoyalty", "CurrToPoints");
					flp.put("loyaltyId", loyaltyCode);
					flp.put("uomLoyaltyValue", balancePoints.longValue());

					flp.create();

				}

				if (UtilValidate.isNotEmpty(loyaltyCode)) {
					String partyIdentification=org.fio.homeapps.util.DataUtil.storePartyIdentification(delegator, partyId, loyaltyCode,"LOYALTY_ID");

					GenericValue personGV = delegator.findOne("Person", UtilMisc.toMap("partyId", partyId), false);
					if (UtilValidate.isEmpty(personGV))
						personGV = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", partyId), false);
					if (UtilValidate.isNotEmpty(personGV)) {

						if (UtilValidate.isNotEmpty(personGV.getString("loyaltyId"))) {

							GenericValue loyaltyNumberCust = delegator.findOne("LoyaltyNumber",
									UtilMisc.toMap("loyaltyCode", personGV.getString("loyaltyId")), false);
							if (loyaltyNumberCust != null) {
								loyaltyNumberCust.set("loyaltyCodeStatusId", "REPLACED");
								loyaltyNumberCust.store();
							}
						}

						personGV.put("loyaltyId", loyaltyCode);
						personGV.put("isLoyaltyEnabled", "Y");
						personGV.store();
					}
				}
			} catch (Exception e) {
				Debug.logError(e.getMessage(), MODULE);
				errorMessageMap.put("_ERROR_MESSAGE_", e.getMessage());
				return errorMessageMap;
			}
		}
		return errorMessageMap;
	}

	public static Map<String, Object> updateloyaltyidEnabled(Delegator delegator, String partyId,
			String loyaltyIdStatus) {
		List<GenericValue> toStore = FastList.newInstance();
		GenericValue loyaltyEnableIns = null;
		Map<String, Object> errorMessageMap = FastMap.newInstance();
		try {
			GenericValue partyTypeGv = delegator.findOne("Party", UtilMisc.toMap("partyId", partyId), false);
			if (UtilValidate.isNotEmpty(partyTypeGv)) {
				String partyType = partyTypeGv.getString("partyTypeId");
				if ("PARTY_GROUP".equals(partyType))
					loyaltyEnableIns = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", partyId), false);
				else
					loyaltyEnableIns = delegator.findOne("Person", UtilMisc.toMap("partyId", partyId), false);
					loyaltyEnableIns.set("isLoyaltyEnabled", loyaltyIdStatus);

			} else {
				errorMessageMap.put("_ERROR_MESSAGE_", "No customer found for the given party id " + partyId);
				return errorMessageMap;
			}
			if ("N".equals(loyaltyIdStatus)) {
				loyaltyEnableIns.set("loyaltyId", null);

				toStore.add(LoyaltyUtil.expireLoyaltyNumber(delegator, partyId));

				loyaltyEnableIns.store();

			} else {
				String message = LoyaltyUtil.validateLoyaltyCustomer(delegator, partyId);

				if (!"success".equals(message)) {
					errorMessageMap.put("_ERROR_MESSAGE_", message);
					return errorMessageMap;
				}

				EntityCondition loyaltyCondition = EntityCondition.makeCondition(
						UtilMisc.toList(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId)),
						EntityOperator.AND);
				EntityFindOptions findOpt = new EntityFindOptions();
				findOpt.setMaxRows(1);
				List<GenericValue> loyaltyIdlist = delegator.findList("LoyaltyNumber", loyaltyCondition, null,
						UtilMisc.toList("-lastUpdatedStamp"), findOpt, false);
				GenericValue loyaltyNumber = EntityUtil.getFirst(loyaltyIdlist);

				if (UtilValidate.isNotEmpty(loyaltyNumber)) {
					String partyIdentification=org.fio.homeapps.util.DataUtil.storePartyIdentification(delegator, partyId, loyaltyNumber.getString("loyaltyCode"),"LOYALTY_ID");

					loyaltyNumber.set("loyaltyCodeStatusId", "ENABLED");
					loyaltyEnableIns.set("loyaltyId", loyaltyNumber.getString("loyaltyCode"));
					toStore.add(loyaltyEnableIns);
					toStore.add(loyaltyNumber);

				} else {

					String partyName = PartyHelper.getPartyName(delegator, partyId, false);
					if (UtilValidate.isEmpty(partyName)) {
						errorMessageMap.put("_ERROR_MESSAGE_", "Insufficient Data to update Loyalty Customer");
						return errorMessageMap;
					}
					LoyaltyUtil.assignLoyaltyId(partyId, delegator);
				}

			}

			delegator.storeAll(toStore);

		} catch (Exception e) {
			Debug.logWarning(e, MODULE);
			errorMessageMap.put("_ERROR_MESSAGE_", e.getMessage());
			return errorMessageMap;
		}
		return errorMessageMap;
	}
	
	public static Map<String,Object> getAssignedOrNewLoyaltyNumber(Delegator delegator,GenericValue userLogin, String partyId, String updateToPartyAttribute) {
		Map<String, Object> errorMessageMap = FastMap.newInstance();
		String loyaltyCode = "";
		try {
			String partyName = PartyHelper.getPartyName(delegator, partyId, false);
			if (UtilValidate.isEmpty(partyName)) {
				errorMessageMap.put("_ERROR_MESSAGE_", "Insufficient Data to update Loyalty Customer");
				return errorMessageMap;
			}
			
			String message = LoyaltyUtil.validateLoyaltyCustomer(delegator,partyId);
			
			if(!"success".equals(message)){
				errorMessageMap.put("_ERROR_MESSAGE_",message);
				return errorMessageMap;
			}
			
			loyaltyCode = generateUniqueLoyaltyNumber(delegator,
					UtilMisc.toMap("customerPartyId", partyId, "userLogin", userLogin));
			if(UtilValidate.isNotEmpty(loyaltyCode) && "Y".equalsIgnoreCase(updateToPartyAttribute)) {
				String partyIdentification=org.fio.homeapps.util.DataUtil.storePartyIdentification(delegator, partyId,loyaltyCode,"LOYALTY_ID");

				GenericValue person = delegator.findOne("Person", UtilMisc.toMap("partyId",partyId),false);
				if(UtilValidate.isEmpty(person))
					person = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId",partyId),false);
				if(UtilValidate.isNotEmpty(person)){
					person.put("loyaltyId", loyaltyCode);
					person.put("isLoyaltyEnabled", "Y");
					person.store();
				}	
			}
			
		}catch(Exception e) {
			Debug.logWarning(e, MODULE);
			errorMessageMap.put("_ERROR_MESSAGE_", e.getMessage());
			return errorMessageMap;
		}
		return errorMessageMap;
	}
	
}
