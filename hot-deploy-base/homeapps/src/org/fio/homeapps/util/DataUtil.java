/**
 * 
 */
package org.fio.homeapps.util;

import java.security.SecureRandom;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.fio.homeapps.constants.GlobalConstants;
import org.fio.homeapps.constants.GlobalConstants.AccessLevel;
import org.fio.homeapps.constants.GlobalConstants.DateTimeTypeConstant;
import org.fio.homeapps.constants.GlobalConstants.SourceInvoked;
import org.ofbiz.base.crypto.HashCrypt;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
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
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelField;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityCrypto;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONObject;

/**
 * @author Sharif
 * @author Mahendran Thanasekaran
 */
public class DataUtil {
	
	private static String MODULE = DataUtil.class.getName();
	
	public static String getPartyRelAssocId(Delegator delegator, Map<String, Object> context) {
		String partyIdFrom = (String) context.get("partyIdFrom");
		String partyIdTo = (String) context.get("partyIdTo");
		String roleTypeIdFrom = (String) context.get("roleTypeIdFrom");
		String roleTypeIdTo = (String) context.get("roleTypeIdTo");
		String partyRelationshipTypeId = (String) context.get("partyRelationshipTypeId");
		
		try {
			if (UtilValidate.isNotEmpty(partyIdFrom) && UtilValidate.isNotEmpty(partyIdTo) && UtilValidate.isNotEmpty(roleTypeIdFrom) && UtilValidate.isNotEmpty(roleTypeIdTo) && UtilValidate.isNotEmpty(partyRelationshipTypeId)) {
				EntityCondition searchConditions = EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, partyIdFrom),
						EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, partyIdTo),
						EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, roleTypeIdFrom),
						EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, roleTypeIdTo),
						EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, partyRelationshipTypeId),
						EntityUtil.getFilterByDateExpr());
				
				GenericValue existingRelationship = EntityQuery.use(delegator).select("partyRelAssocId").from("PartyRelationship").where(searchConditions).queryFirst();
				if (UtilValidate.isNotEmpty(existingRelationship)) {
					return existingRelationship.getString("partyRelAssocId");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static GenericValue getActivePartyContactMechPurpose(Delegator delegator, String partyId, String contactMechPurposeTypeId, String partyRelAssocId) {
		GenericValue partyContactMechPurpose = null;
		try {
			if (UtilValidate.isNotEmpty(partyId) && UtilValidate.isNotEmpty(contactMechPurposeTypeId)) {
				EntityCondition searchConditions = EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId),
						EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, contactMechPurposeTypeId),
						EntityUtil.getFilterByDateExpr());
				
				if (UtilValidate.isNotEmpty(partyRelAssocId)) {
					searchConditions = EntityCondition.makeCondition(EntityOperator.AND,
							EntityCondition.makeCondition("partyRelAssocId", EntityOperator.EQUALS, partyRelAssocId),
							searchConditions);
				}
				
				partyContactMechPurpose = EntityQuery.use(delegator).from("PartyContactMechPurpose").where(searchConditions).queryFirst();
				return partyContactMechPurpose;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return partyContactMechPurpose;
	}
	
	public static GenericValue findPartyByEmail(Delegator delegator, String emailAddress) {
		try {
			GenericValue contactMech = EntityQuery.use(delegator).from("ContactMech").where("contactMechTypeId", "EMAIL_ADDRESS", "infoString", emailAddress).queryFirst();
			if (UtilValidate.isNotEmpty(contactMech)) {
				
				EntityCondition mainCond = EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("contactMechId", EntityOperator.EQUALS, contactMech.getString("contactMechId")),
						EntityUtil.getFilterByDateExpr()
						);
				
				GenericValue partyContactMech = EntityQuery.use(delegator).from("PartyContactMech").where(mainCond).queryFirst();
				if (UtilValidate.isNotEmpty(partyContactMech)) {
					GenericValue party = EntityQuery.use(delegator).from("Party").where("partyId", partyContactMech.getString("partyId")).queryFirst();
					return party;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public static String getPartyIdByEmail(Delegator delegator, String emailAddress) {
		try {
			GenericValue party = findPartyByEmail(delegator, emailAddress);
			if (UtilValidate.isNotEmpty(party)) {
				return party.getString("partyId");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static GenericValue findPartyByLogin(Delegator delegator, String oneBankId) {
		try {
			
			if (UtilValidate.isNotEmpty(oneBankId)) {
				GenericValue userLogin = EntityQuery.use(delegator).select("partyId").from("UserLogin").where("userLoginId",  oneBankId).queryFirst();
				if (UtilValidate.isNotEmpty(userLogin)) {
					GenericValue party = EntityQuery.use(delegator).from("Party").where("partyId", userLogin.getString("partyId")).queryFirst();
					return party;
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String findLoginIdByPartyId(Delegator delegator, String partyId) {
		try {
			if (UtilValidate.isNotEmpty(partyId)) {
				GenericValue userLogin = EntityQuery.use(delegator).select("userLoginId").from("UserLogin").where("partyId",  partyId).queryFirst();
				if (UtilValidate.isNotEmpty(userLogin)) {
					return userLogin.getString("userLoginId");
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static GenericValue getFirstDbsRole(Delegator delegator, String partyId) {
		
		try {
			if (UtilValidate.isNotEmpty(partyId)) {
				EntityCondition condition = EntityCondition.makeCondition(UtilMisc.toList(
						EntityCondition.makeCondition("roleTypeId", EntityOperator.LIKE, "DBS_%"),
						EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId)
						), EntityOperator.AND);
				List<GenericValue> dbsRoleList = delegator.findList("PartyRole", condition, null, null, null, false);
				if (UtilValidate.isNotEmpty(dbsRoleList)) {
					return dbsRoleList.get(0);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
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

    public static Map<String, Object> getDndStatus(Delegator delegator, String telecomNumber) {
    	String dndStatus = "N";
    	String solicitationStatus = "Y";
    	Map<String, Object> rsponseMap = new HashMap<String, Object>();
    	try {
    		GenericValue dndMaster = EntityQuery.use(delegator).from("DndMaster").where("number", telecomNumber).orderBy("lastUpdatedStamp DESC").queryFirst();
    		if(dndMaster != null && dndMaster.size() > 0) {
    			String dndIndicator = dndMaster.getString("indicator");
    			String dndSeqId = dndMaster.getString("seqId");
    			if(UtilValidate.isNotEmpty(dndIndicator)) {
    				rsponseMap.put("dndIndicator", dndIndicator);
    				rsponseMap.put("dndSeqId", dndSeqId);
    				if("A".equalsIgnoreCase(dndIndicator)) {
    					solicitationStatus = "N";
    					dndStatus = "Y";
    				} else if("D".equalsIgnoreCase(dndIndicator)) {
    					solicitationStatus = "Y";
    					dndStatus = "N";
    				}
    			} 
    		}
    	} catch (GenericEntityException ex) {
    		Debug.log("Exception in getDndStatus method: " +ex.getMessage());
    	}
    	rsponseMap.put("dndStatus", dndStatus);
    	rsponseMap.put("solicitationStatus", solicitationStatus);
    	return rsponseMap;
    }

    public static boolean validateDndAuditLogDetails(Delegator delegator, String telecomNumber, String partyId, String dndIndicator) {
    	Boolean dndValidation = false;
    	try {
    		GenericValue dndAuditLogDetails = EntityQuery.use(delegator).from("DndAuditLogDetails")
    				.where("partyId", partyId, "dndNumber", telecomNumber, "dndIndicator", dndIndicator)
    				.queryFirst();
    		if(dndAuditLogDetails == null || dndAuditLogDetails.size() < 1) {
    			dndValidation = true;
    		}
    	} catch (GenericEntityException ex) {
    		Debug.log("Exception in validateDndAuditLogDetails method: " +ex.getMessage());
    	}

    	return dndValidation;
    }

    @SuppressWarnings("unchecked")
    public static GenericValue makeDndAuditLogDetails(String dndSeqId, String partyId, String changeStatus, String dndNumber, String dndIndicator, Timestamp now, Delegator delegator) {
	    Map<String, Object> dndAuditLogDetails = FastMap.newInstance();
	    dndAuditLogDetails.put("seqId", delegator.getNextSeqId("DndAuditLogDetails"));
	    dndAuditLogDetails.put("partyId", partyId);
	    dndAuditLogDetails.put("dndSeqId", dndSeqId);
	    dndAuditLogDetails.put("changeStatus", changeStatus);
	    dndAuditLogDetails.put("dndNumber", dndNumber);
	    dndAuditLogDetails.put("dndIndicator", dndIndicator);
	    dndAuditLogDetails.put("changeDate", now);
	    return delegator.makeValue("DndAuditLogDetails", dndAuditLogDetails);
    }
    
    public static String getPartyAttrValue(Delegator delegator, String partyId, String attrName) {
    	String attrValue = null;
		try {
			
			if(UtilValidate.isEmpty(partyId) || UtilValidate.isEmpty(attrName)){
				return attrValue;
			}
			
			EntityCondition mainCondition = EntityCondition.makeCondition(EntityOperator.AND,
                    EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId),
                    EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, attrName)
                    );
			
			GenericValue productAttribute = EntityQuery.use(delegator).select("attrValue").from("PartyAttribute").where(mainCondition).queryFirst();
			if(UtilValidate.isNotEmpty(productAttribute)){
				attrValue = productAttribute.getString("attrValue");
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return attrValue;
	}
    
    public static String getPartyIdentificationValue(Delegator delegator, String partyId, String partyIdentificationTypeId) {
    	return getPartyIdentificationValue(delegator, partyId, partyIdentificationTypeId, false);
    }
    public static String getPartyIdentificationValue(Delegator delegator, String partyId, String partyIdentificationTypeId, boolean useCache) {
    	String idValue = null;
		try {
			if (UtilValidate.isNotEmpty(partyId) && UtilValidate.isNotEmpty(partyIdentificationTypeId)) {
				EntityCondition mainCondition = EntityCondition.makeCondition(EntityOperator.AND,
	                    EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId),
	                    EntityCondition.makeCondition("partyIdentificationTypeId", EntityOperator.EQUALS, partyIdentificationTypeId)
	                    );
				
				GenericValue partyIdentification = EntityQuery.use(delegator).select("idValue").from("PartyIdentification").where(mainCondition).cache(useCache).queryFirst();
				if(UtilValidate.isNotEmpty(partyIdentification)){
					idValue = partyIdentification.getString("idValue");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return idValue;
	}
    
    public static boolean isValidPartyIdentificationParty(Delegator delegator, String idValue, String partyIdentificationTypeId) {
    	return UtilValidate.isNotEmpty(getPartyIdentificationPartyId(delegator, idValue, partyIdentificationTypeId));
	}
    
    public static String getPartyIdentificationPartyId(Delegator delegator, String idValue, String partyIdentificationTypeId) {
    	String partyId = null;
		try {
			if (UtilValidate.isNotEmpty(idValue) && UtilValidate.isNotEmpty(partyIdentificationTypeId)) {
				EntityCondition mainCondition = EntityCondition.makeCondition(EntityOperator.AND,
	                    EntityCondition.makeCondition("idValue", EntityOperator.EQUALS, idValue),
	                    EntityCondition.makeCondition("partyIdentificationTypeId", EntityOperator.EQUALS, partyIdentificationTypeId)
	                    );
				
				GenericValue entity = EntityQuery.use(delegator).select("partyId").from("PartyIdentification").where(mainCondition).queryFirst();
				if(UtilValidate.isNotEmpty(entity)) {
					partyId = entity.getString("partyId");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return partyId;
	}

	public static GenericValue getPartyIdentification(Delegator delegator, String partyId,
			String partyIdentificationTypeId) {
		try {
			if (UtilValidate.isNotEmpty(partyId) && UtilValidate.isNotEmpty(partyIdentificationTypeId)) {
				GenericValue partyIdentification = EntityQuery.use(delegator).from("PartyIdentification").where("partyId", partyId, "partyIdentificationTypeId", partyIdentificationTypeId).queryFirst();
				if (UtilValidate.isNotEmpty(partyIdentification)) {
					return partyIdentification;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return null;
	}

	public static String storePartyIdentification(Delegator delegator, String partyId, String loyaltyId,
			String partyIdentificationTypeId) {
		String value = "success";
		try {
			if (UtilValidate.isNotEmpty(partyId) && UtilValidate.isNotEmpty(partyIdentificationTypeId)) {
				Debug.logInfo("storePartyIdentification - partyId: "+partyId+", partyIdentificationTypeId: "+partyIdentificationTypeId, MODULE);
				GenericValue partyIdentification = delegator.makeValue("PartyIdentification",UtilMisc.toMap("partyId", partyId, "partyIdentificationTypeId", partyIdentificationTypeId));
				if (UtilValidate.isNotEmpty(loyaltyId)) {
					partyIdentification.set("idValue", loyaltyId);
					delegator.createOrStore(partyIdentification);
					return value;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
    public static String getSystemConfigDesc(Delegator delegator, String profileConfigurationId) {
		String description = "";
		try {
			if (UtilValidate.isNotEmpty(profileConfigurationId)) {
				List conditionList = FastList.newInstance();
				
				conditionList.add(EntityCondition.makeCondition(EntityOperator.OR,
						EntityCondition.makeCondition("profileConfigurationId", EntityOperator.EQUALS, profileConfigurationId),
						EntityCondition.makeCondition("profileCode", EntityOperator.EQUALS, profileConfigurationId)
	                    ));
				
				/*if (UtilValidate.isNotEmpty(profileConfigurationId)) {
					conditionList.add(EntityCondition.makeCondition("profileConfigurationId", EntityOperator.EQUALS, profileConfigurationId));
				}*/
				
				EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				GenericValue systemConfig = EntityQuery.use(delegator).select("profileDescription").from("ProfileConfiguration").where(mainConditons).queryFirst();
				if (UtilValidate.isNotEmpty(systemConfig)) {
					description = systemConfig.getString("profileDescription");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return description;
	}
    
    public static String getSystemConfigCode(Delegator delegator, String profileConfigurationId) {
		String description = "";
		try {
			if (UtilValidate.isNotEmpty(profileConfigurationId)) {
				List conditionList = FastList.newInstance();
				
				conditionList.add(EntityCondition.makeCondition(EntityOperator.OR,
						EntityCondition.makeCondition("profileConfigurationId", EntityOperator.EQUALS, profileConfigurationId),
						EntityCondition.makeCondition("profileCode", EntityOperator.EQUALS, profileConfigurationId)
	                    ));
				
				/*if (UtilValidate.isNotEmpty(profileConfigurationId)) {
					conditionList.add(EntityCondition.makeCondition("profileConfigurationId", EntityOperator.EQUALS, profileConfigurationId));
				}*/
				
				EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				GenericValue systemConfig = EntityQuery.use(delegator).select("profileCode").from("ProfileConfiguration").where(mainConditons).queryFirst();
				if (UtilValidate.isNotEmpty(systemConfig)) {
					description = systemConfig.getString("profileCode");
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return description;
	}
    
    public static List<GenericValue> getCountryList(Delegator delegator){

		List<GenericValue> countryList = new ArrayList<GenericValue>();

		try {

			List<EntityCondition> conditionsList = new ArrayList<EntityCondition>();
			conditionsList.add(EntityCondition.makeCondition("geoTypeId", EntityOperator.EQUALS, "COUNTRY"));
			
			EntityCondition mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);
			countryList = delegator.findList("Geo", mainConditons, null, UtilMisc.toList("geoId"), null, false);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return countryList;
	}
    /*
    public static boolean getValidMkrChkAction(Delegator delegator, GenericValue userLogin, String permissionId) {
    	boolean validMkrChkAction = false;
    	try {
    		if (UtilValidate.isNotEmpty(userLogin) && UtilValidate.isNotEmpty(permissionId)) {
    			if(validateFullAccessUser(delegator, userLogin)) {
    				return false;
    			}

    			EntityQuery securityGroup = EntityQuery.use(delegator).from("SecurityGroup").where("customSecurityGroupType", "Y");
    			List<GenericValue> userLoginSecurityGroup = EntityQuery.use(delegator).from("UserLoginSecurityGroup")
    					.where(EntityCondition.makeCondition("userLoginId", EntityOperator.EQUALS, userLogin.getString("userLoginId")),
    							EntityCondition.makeCondition("groupId", EntityOperator.IN, EntityUtil.getFieldListFromEntityList(securityGroup.queryList(), "groupId", true))
    							)
    					.filterByDate().queryList();
    			if (userLoginSecurityGroup != null && userLoginSecurityGroup.size()> 0) {
    				GenericValue securityGroupPermission = EntityQuery.use(delegator).from("SecurityGroupPermission")
    						.where(EntityCondition.makeCondition("permissionId", EntityOperator.EQUALS, permissionId),
    								EntityCondition.makeCondition("groupId", EntityOperator.IN, EntityUtil.getFieldListFromEntityList(userLoginSecurityGroup, "groupId", true))
    								).queryFirst();
    				if(UtilValidate.isNotEmpty(securityGroupPermission)) {
    					validMkrChkAction = true;
    				}
    			}
    		}
    	} catch (Exception e) {
    		e.printStackTrace();
    	}

    	return validMkrChkAction;
    }
    */
    public static boolean isValidWorkEffortType(Delegator delegator, String workEffortType, String parentTypeId) {
    	return UtilValidate.isNotEmpty(getWorkEffortTypeId(delegator, workEffortType, parentTypeId));
	}
    
    public static String getWorkEffortTypeId(Delegator delegator, String workEffortType, String parentTypeId) {
    	
		String workEffortTypeId = null;
		
		try {
			
			if (UtilValidate.isNotEmpty(workEffortType)) {
				List conditionList = FastList.newInstance();
				
				conditionList.add(EntityCondition.makeCondition(EntityOperator.OR,
						EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("workEffortTypeId"), EntityOperator.EQUALS, EntityFunction.UPPER(workEffortType)),
						EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("description"), EntityOperator.EQUALS, EntityFunction.UPPER(workEffortType))
	                    ));
				
				if (UtilValidate.isNotEmpty(parentTypeId)) {
					conditionList.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("parentTypeId"), EntityOperator.EQUALS, EntityFunction.UPPER(parentTypeId)));
				}
				
				EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				GenericValue type = EntityQuery.use(delegator).select("workEffortTypeId").from("WorkEffortType").where(mainConditons).queryFirst();
				if (UtilValidate.isNotEmpty(type)) {
					workEffortTypeId = type.getString("workEffortTypeId");
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return workEffortTypeId;
	}
    
    public static String getWorkEffortTypeDescription(Delegator delegator, String workEffortType) {
		String workEffortTypeId = null;
		try {
			
			if (UtilValidate.isNotEmpty(workEffortType)) {
				List conditionList = FastList.newInstance();
				
				conditionList.add(EntityCondition.makeCondition(EntityOperator.OR,
						EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("workEffortTypeId"), EntityOperator.EQUALS, EntityFunction.UPPER(workEffortType)),
						EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("description"), EntityOperator.EQUALS, EntityFunction.UPPER(workEffortType))
	                    ));
				
				EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				GenericValue type = EntityQuery.use(delegator).select("description").from("WorkEffortType").where(mainConditons).queryFirst();
				if (UtilValidate.isNotEmpty(type)) {
					workEffortTypeId = type.getString("description");
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return workEffortTypeId;
	}
    
    public static String getWorkEffortAttrValue(Delegator delegator, String workEffortId, String attrName) {
    	
		String attrValue = null;
		
		try {
			
			if (UtilValidate.isNotEmpty(workEffortId)) {
				List conditionList = FastList.newInstance();
				
				conditionList.add(EntityCondition.makeCondition(EntityOperator.OR,
						EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId)
	                    ));
				
				if (UtilValidate.isNotEmpty(attrName)) {
					conditionList.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("attrName"), EntityOperator.EQUALS, EntityFunction.UPPER(attrName)));
				}
				
				EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				GenericValue attribute = EntityQuery.use(delegator).select("attrValue").from("WorkEffortAttribute").where(mainConditons).queryFirst();
				if (UtilValidate.isNotEmpty(attribute)) {
					attrValue = attribute.getString("attrValue");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return attrValue;
	}
    
    public static String getStatusId(Delegator delegator, String value) {
		return getStatusId(delegator, value, null);
	}
    
    public static boolean isValidStatusId(Delegator delegator, String value) {
		return UtilValidate.isNotEmpty(getStatusId(delegator, value, null)); 
	}
	
	public static boolean isValidStatusId(Delegator delegator, String value, String statusTypeId) {
		return UtilValidate.isNotEmpty(getStatusId(delegator, value, statusTypeId)); 
	}
	
	public static String getStatusId(Delegator delegator, String value, String statusTypeId) {
		try {
			if (UtilValidate.isNotEmpty(value)) {
				EntityCondition condition = EntityCondition.makeCondition(EntityOperator.OR,
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("statusId")), EntityOperator.EQUALS, value.toString().toUpperCase()),
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("statusCode")), EntityOperator.EQUALS, value.toString().toUpperCase()),
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("description")), EntityOperator.EQUALS, value.toString().toUpperCase())
               			);    
				
				if (UtilValidate.isNotEmpty(statusTypeId)) {
					condition = EntityCondition.makeCondition(EntityOperator.AND,
							EntityCondition.makeCondition("statusTypeId", EntityOperator.EQUALS, statusTypeId),
	               			condition
	               			);       
				}
				
				GenericValue statusEntity = EntityQuery.use(delegator).select("statusId").from("StatusItem").where(condition).queryFirst();
				if (UtilValidate.isNotEmpty(statusEntity)) {
					return statusEntity.getString("statusId");
				}
			}
		} catch (Exception e) {
		}
		return null;
	}
	
	public static String getStatusDescription(Delegator delegator, String value) {
		return getStatusDescription(delegator, value, null);
	}
	
	public static String getStatusDescription(Delegator delegator, String value, String statusTypeId) {
		try {
			if (UtilValidate.isNotEmpty(value)) {
				EntityCondition condition = EntityCondition.makeCondition(EntityOperator.OR,
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("statusId")), EntityOperator.EQUALS, value.toString().toUpperCase()),
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("statusCode")), EntityOperator.EQUALS, value.toString().toUpperCase()),
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("description")), EntityOperator.EQUALS, value.toString().toUpperCase())
               			);    
				
				if (UtilValidate.isNotEmpty(statusTypeId)) {
					condition = EntityCondition.makeCondition(EntityOperator.AND,
							EntityCondition.makeCondition("statusTypeId", EntityOperator.EQUALS, statusTypeId),
	               			condition
	               			);       
				}
				
				GenericValue statusEntity = EntityQuery.use(delegator).select("description").from("StatusItem").where(condition).cache().queryFirst();
				if (UtilValidate.isNotEmpty(statusEntity)) {
					return statusEntity.getString("description");
				}
			}
		} catch (Exception e) {
		}
		return null;
	}
	
	public static boolean isValidMsdStatusId(Delegator delegator, String workEffortTypeId, String attributeName, String attributeValue) {
		return UtilValidate.isNotEmpty(getMsdStatusId(delegator, workEffortTypeId, attributeName, attributeValue)); 
	}
	
	public static String getMsdStatusId(Delegator delegator, String workEffortTypeId, String attributeName, String attributeValue) {
		
		try {
			if (UtilValidate.isNotEmpty(workEffortTypeId) && UtilValidate.isNotEmpty(attributeName) && UtilValidate.isNotEmpty(attributeValue)) {
				EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("workEffortTypeId", EntityOperator.EQUALS, workEffortTypeId),
						EntityCondition.makeCondition("attributeName", EntityOperator.EQUALS, attributeName),
						EntityCondition.makeCondition("attributeValue", EntityOperator.EQUALS, attributeValue)
               			);    
				
				GenericValue statusEntity = EntityQuery.use(delegator).select("ofbizCode").from("ActivityTypeStatusMap").where(condition).queryFirst();
				if (UtilValidate.isNotEmpty(statusEntity)) {
					
					String statusTypeId = null;
					if (UtilValidate.isNotEmpty(attributeName) && attributeName.equals("statuscode")) {
						statusTypeId = "IA_STATUS_ID";
					} else if (UtilValidate.isNotEmpty(attributeName) && attributeName.equals("statecode")) {
						statusTypeId = "IA_SUB_STATUS_ID";
					}
					
					String ofbizCode = statusEntity.getString("ofbizCode");
					String statusId = getStatusId(delegator, ofbizCode, statusTypeId);
					
					return statusId;
				}
			}
		} catch (Exception e) {
		}
		
		return null;
	}
	
	public static boolean validatePermission(Delegator delegator, String userLoginId, String permissionId) {
		boolean hasPermission = false;
		try {
			EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
					EntityCondition.makeCondition("userLoginId",EntityOperator.EQUALS,userLoginId),
					EntityCondition.makeCondition("permissionId",EntityOperator.EQUALS,permissionId));
			GenericValue userLoginSecurityGroupPermission = EntityQuery.use(delegator).from("UserLoginSecurityGroupPermission").where(condition).filterByDate().queryFirst();
			if(UtilValidate.isNotEmpty(userLoginSecurityGroupPermission)) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return hasPermission;
		
	}
	public static boolean hasFullPermission(Delegator delegator, String userLoginId) {
		boolean hasFullPermission = false;
		try {
			String fullAccessGroupId = EntityUtilProperties.getPropertyValue("admin-portal.properties", "full.access.security.group", delegator);
			EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
					EntityCondition.makeCondition("userLoginId",EntityOperator.EQUALS,userLoginId),
					EntityCondition.makeCondition("groupId",EntityOperator.EQUALS,fullAccessGroupId));
			GenericValue userLoginSecurityGroupPermission = EntityQuery.use(delegator).from("UserLoginSecurityGroup").where(condition).filterByDate().queryFirst();
			if(UtilValidate.isNotEmpty(userLoginSecurityGroupPermission)) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return hasFullPermission;
		
	}
	public static boolean isValidUserLogin(Delegator delegator, String userLoginId) {
		
		try {
			if (UtilValidate.isNotEmpty(userLoginId)) {
				EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("userLoginId", EntityOperator.EQUALS, userLoginId)
               			);                       	
				
				GenericValue userLogin = EntityQuery.use(delegator).from("UserLogin").where(condition).queryFirst();
               	return UtilValidate.isNotEmpty(userLogin);
			}
		} catch (Exception e) {
		}
		
		return false;
	}
	
	public static boolean isValidWorkEffortAssocTriplet(Delegator delegator, String entityName, String process, String type, String subType) {
		return UtilValidate.isNotEmpty(getWorkEffortAssocTriplet(delegator, entityName, process, type, subType)); 
	}
    
	public static GenericValue getWorkEffortAssocTriplet(Delegator delegator, String entityName, String process, String type, String subType) {
		try {
			if (UtilValidate.isNotEmpty(process) && UtilValidate.isNotEmpty(type) && UtilValidate.isNotEmpty(subType)) {
				EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("entityName", EntityOperator.EQUALS, entityName),
						
						EntityCondition.makeCondition("grandparentCode", EntityOperator.EQUALS, process),
						EntityCondition.makeCondition("parentCode", EntityOperator.EQUALS, type),
						EntityCondition.makeCondition("code", EntityOperator.EQUALS, subType)
               			);
				
				GenericValue entity = EntityQuery.use(delegator).from("WorkEffortAssocTriplet").where(condition).queryFirst();
				return entity;
			}
		} catch (Exception e) {
		}
		return null;
	}
	
	public static boolean isValidCustRequestAssocTriplet(Delegator delegator, String process, String type, String subType) {
		return UtilValidate.isNotEmpty(getCustRequestAssocTriplet(delegator, process, type, subType)); 
	}
    
	public static GenericValue getCustRequestAssocTriplet(Delegator delegator, String process, String type, String subType) {
		
		try {
			if (UtilValidate.isNotEmpty(process) && UtilValidate.isNotEmpty(type) && UtilValidate.isNotEmpty(subType)) {
				EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
						
						EntityCondition.makeCondition("grandparentCode", EntityOperator.EQUALS, process),
						EntityCondition.makeCondition("parentCode", EntityOperator.EQUALS, type),
						EntityCondition.makeCondition("code", EntityOperator.EQUALS, subType)
               			);
				
				GenericValue entity = EntityQuery.use(delegator).from("CustRequestAssoc").where(condition).queryFirst();
				return entity;
			}
		} catch (Exception e) {
		}
		
		return null;
	}
	
	public static boolean isValidMsdCustRequestAssocTriplet(Delegator delegator, String process, String type, String subType) {
		return UtilValidate.isNotEmpty(getMsdCustRequestAssocTriplet(delegator, process, type, subType)); 
	}
    
	public static GenericValue getMsdCustRequestAssocTriplet(Delegator delegator, String process, String type, String subType) {
		
		try {
			if (UtilValidate.isNotEmpty(process) && UtilValidate.isNotEmpty(type) && UtilValidate.isNotEmpty(subType)) {
				EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
						
						EntityCondition.makeCondition("msGrandparentCode", EntityOperator.EQUALS, process),
						EntityCondition.makeCondition("msParentCode", EntityOperator.EQUALS, type),
						EntityCondition.makeCondition("msCode", EntityOperator.EQUALS, subType)
               			);
				
				GenericValue entity = EntityQuery.use(delegator).from("CustRequestAssoc").where(condition).queryFirst();
				return entity;
			}
		} catch (Exception e) {
		}
		
		return null;
	}
	
	public static boolean isValidCustRequestType(Delegator delegator, String custRequestTypeId) {
    	return UtilValidate.isNotEmpty(getCustRequestTypeId(delegator, custRequestTypeId));
	}

	public static String getCustRequestTypeId(Delegator delegator, String custRequestTypeId) {
    	
		String custRequestType = null;
		
		try {
			
			if (UtilValidate.isNotEmpty(custRequestTypeId)) {
				  GenericValue getCustRequestType = delegator.findOne("CustRequestType", UtilMisc.toMap("custRequestTypeId", custRequestTypeId), true);
				  if (UtilValidate.isNotEmpty(getCustRequestType)) {
					  custRequestType = getCustRequestType.getString("custRequestTypeId");
				  }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return custRequestType;
	}
	
	public static boolean isValidCustRequestType(Delegator delegator, String custRequestType, String parentTypeId) {
    	return UtilValidate.isNotEmpty(getCustRequestTypeId(delegator, custRequestType, parentTypeId));
	}
    
    public static String getCustRequestTypeId(Delegator delegator, String custRequestType, String parentTypeId) {
		String custRequestTypeId = null;
		try {
			if (UtilValidate.isNotEmpty(custRequestType)) {
				List conditionList = FastList.newInstance();
				
				conditionList.add(EntityCondition.makeCondition(EntityOperator.OR,
						EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("custRequestTypeId"), EntityOperator.EQUALS, EntityFunction.UPPER(custRequestType)),
						EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("description"), EntityOperator.EQUALS, EntityFunction.UPPER(custRequestType))
	                    ));
				
				if (UtilValidate.isNotEmpty(parentTypeId)) {
					conditionList.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("parentTypeId"), EntityOperator.EQUALS, EntityFunction.UPPER(parentTypeId)));
				}
				
				EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				GenericValue type = EntityQuery.use(delegator).select("custRequestTypeId").from("CustRequestType").where(mainConditons).queryFirst();
				if (UtilValidate.isNotEmpty(type)) {
					custRequestTypeId = type.getString("custRequestTypeId");
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return custRequestTypeId;
	}
    
    public static String getCustRequestTypeDesc(Delegator delegator, String custRequestTypeId) {
		String description = null;
		try {
			if (UtilValidate.isNotEmpty(custRequestTypeId)) {
				List conditionList = FastList.newInstance();
				
				conditionList.add(EntityCondition.makeCondition("custRequestTypeId", EntityOperator.EQUALS, custRequestTypeId));
				
				EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				GenericValue type = EntityQuery.use(delegator).select("description").from("CustRequestType").where(mainConditons).cache().queryFirst();
				if (UtilValidate.isNotEmpty(type)) {
					description = type.getString("description");
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return description;
	}
    
    public static boolean isValidCustRequestCategory(Delegator delegator, String custRequestCategory, String parentTypeId) {
    	return UtilValidate.isNotEmpty(getCustRequestCategoryId(delegator, custRequestCategory, parentTypeId));
	}
    
    public static String getCustRequestCategoryId(Delegator delegator, String custRequestCategory, String parentTypeId) {
    	
		String workEffortTypeId = null;
		
		try {
			
			if (UtilValidate.isNotEmpty(custRequestCategory)) {
				List conditionList = FastList.newInstance();
				
				conditionList.add(EntityCondition.makeCondition(EntityOperator.OR,
						EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("custRequestCategoryId"), EntityOperator.EQUALS, EntityFunction.UPPER(custRequestCategory)),
						EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("description"), EntityOperator.EQUALS, EntityFunction.UPPER(custRequestCategory))
	                    ));
				
				if (UtilValidate.isNotEmpty(parentTypeId)) {
					conditionList.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("parentCustRequestCategoryId"), EntityOperator.EQUALS, EntityFunction.UPPER(parentTypeId)));
				}
				
				EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				GenericValue type = EntityQuery.use(delegator).select("custRequestCategoryId").from("CustRequestCategory").where(mainConditons).queryFirst();
				if (UtilValidate.isNotEmpty(type)) {
					workEffortTypeId = type.getString("custRequestCategoryId");
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return workEffortTypeId;
	}
    
    public static String getCustRequestCategoryDesc(Delegator delegator, String custRequestCategory) {
		String description = null;
		try {
			if (UtilValidate.isNotEmpty(custRequestCategory)) {
				List conditionList = FastList.newInstance();
				
				conditionList.add(EntityCondition.makeCondition("custRequestCategoryId", EntityOperator.EQUALS, custRequestCategory));
				
				EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				GenericValue type = EntityQuery.use(delegator).select("description").from("CustRequestCategory").where(mainConditons).cache().queryFirst();
				if (UtilValidate.isNotEmpty(type)) {
					description = type.getString("description");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return description;
	}
    
    public static String getRoleTypeDesc(Delegator delegator, String roleTypeId) {
		String description = null;
		try {
			if (UtilValidate.isNotEmpty(roleTypeId)) {
				List conditionList = FastList.newInstance();
				conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, roleTypeId));
				
				EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				GenericValue type = EntityQuery.use(delegator).select("description").from("RoleType").where(mainConditons).cache().queryFirst();
				if (UtilValidate.isNotEmpty(type)) {
					description = type.getString("description");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return description;
	}
	
	public static String getMsdCustRequestAssocId(Delegator delegator, String custRequestType, String parentTypeId) {
    	
		String workEffortTypeId = null;
		
		try {
			
			if (UtilValidate.isNotEmpty(custRequestType)) {
				List conditionList = FastList.newInstance();
				
				conditionList.add(EntityCondition.makeCondition(EntityOperator.OR,
						EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("custRequestTypeId"), EntityOperator.EQUALS, EntityFunction.UPPER(custRequestType)),
						EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("description"), EntityOperator.EQUALS, EntityFunction.UPPER(custRequestType))
	                    ));
				
				if (UtilValidate.isNotEmpty(parentTypeId)) {
					conditionList.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("parentTypeId"), EntityOperator.EQUALS, EntityFunction.UPPER(parentTypeId)));
				}
				
				EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				GenericValue type = EntityQuery.use(delegator).select("custRequestTypeId").from("CustRequestType").where(mainConditons).queryFirst();
				if (UtilValidate.isNotEmpty(type)) {
					workEffortTypeId = type.getString("custRequestTypeId");
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return workEffortTypeId;
	}
	
	public static boolean isValidParty(Delegator delegator, String partyIdFrom) {
    	return UtilValidate.isNotEmpty(getParty(delegator, partyIdFrom));
	}
	public static String getParty(Delegator delegator, String partyIdFrom) {
		String partyId = null;
		try {
			if (UtilValidate.isNotEmpty(partyIdFrom)) {
				GenericValue party = delegator.findOne("Party", UtilMisc.toMap("partyId", partyIdFrom), true);  
				  if (UtilValidate.isNotEmpty(party)) {
					  partyId = party.getString("partyId");
				  }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return partyId;
	}
	public static boolean isValidResponsiblePerson(Delegator delegator, String responsiblePerson) {
    	return UtilValidate.isNotEmpty(getResponsiblePerson(delegator, responsiblePerson));
	}
	public static String getResponsiblePerson(Delegator delegator, String responsiblePerson) {
		String responsiblePersonId = null;
		try {
			if (UtilValidate.isNotEmpty(responsiblePerson)) {
				 GenericValue emplTeam = delegator.findOne("EmplTeam", UtilMisc.toMap("emplTeamId", responsiblePerson), true); 
				 if (UtilValidate.isEmpty(emplTeam)) {
					GenericValue responsible = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", responsiblePerson), true); 
					if (UtilValidate.isNotEmpty(responsible)) {
					  responsiblePersonId = responsible.getString("userLoginId");
				    }
				} else {
	                    GenericValue responsible = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", responsiblePerson), true);
	                    if (UtilValidate.isNotEmpty(responsible)) {
	                      responsiblePersonId = responsible.getString("userLoginId");
	                    }
	                }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return responsiblePersonId;
	}
	
	public static boolean isValidEnumStatus(Delegator delegator, String statusId , String enumTypeId) {
    	return UtilValidate.isNotEmpty(getValidStatus(delegator, statusId , enumTypeId));
	}
	
	public static String getValidStatus(Delegator delegator, String statusId, String enumTypeId) {
		String status = null;
		try {
			if (UtilValidate.isNotEmpty(statusId)) {
				List conditionList = FastList.newInstance();
				conditionList.add(
						EntityCondition.makeCondition(
				                EntityCondition.makeCondition("enumId", EntityOperator.EQUALS, statusId),
				                EntityOperator.OR,
				                EntityCondition.makeCondition("enumCode", EntityOperator.EQUALS, statusId)
				           )
						);
				conditionList.add(EntityCondition.makeCondition("enumTypeId", EntityOperator.EQUALS, enumTypeId));
				
				EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				GenericValue statusEnum = EntityQuery.use(delegator).select("enumId").from("Enumeration").where(mainConditons).queryFirst();
				if (UtilValidate.isNotEmpty(statusEnum)) {
					status = statusEnum.getString("enumId");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}
	
	public static GenericValue getSrTriplet(Delegator delegator, String value, String type) {
		try {
			if (UtilValidate.isNotEmpty(value)) {
			    EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
               		EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("type")), EntityOperator.EQUALS, type.toString().toUpperCase()),
               		EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("code")), EntityOperator.EQUALS, value.toString().toUpperCase())
               		);
				GenericValue custRequestAssoc = EntityQuery.use(delegator).from("CustRequestAssoc").where(condition).queryFirst();
				return custRequestAssoc;
			}
		} catch (Exception e) {
		}
		return null;
	}
	
	public static String getTripletDescription(Delegator delegator, String value, String type) {
		try {
			if (UtilValidate.isNotEmpty(value)) {
			    EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
               		EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("type")), EntityOperator.EQUALS, type.toString().toUpperCase()),
               		EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("code")), EntityOperator.EQUALS, value.toString().toUpperCase())
               		);
				GenericValue custRequestAssoc = EntityQuery.use(delegator).select("value").from("CustRequestAssoc").where(condition).queryFirst();
				if (UtilValidate.isNotEmpty(custRequestAssoc)) {
					return custRequestAssoc.getString("value");
				}
			}
		} catch (Exception e) {
		}
		return null;
	}
	
	public static String getDummyCIFValue(Delegator delegator, String partyIdentificationTypeId) {
    	String idValue = null;
		try {
			EntityCondition mainCondition = EntityCondition.makeCondition(EntityOperator.AND,
                    EntityCondition.makeCondition("attrValue", EntityOperator.EQUALS, "99999"),
                    EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, partyIdentificationTypeId)
                    );
			
			GenericValue custReqAttribute = EntityQuery.use(delegator).select("attrValue").from("CustRequestAttribute").where(mainCondition).queryFirst();
			if(UtilValidate.isNotEmpty(custReqAttribute)){
				idValue = custReqAttribute.getString("attrValue");
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return idValue;
	}
	public static String getStatusCode(Delegator delegator, String value) {
		return getStatusCode(delegator, value, null);
	}
	
	public static String getStatusCode(Delegator delegator, String value, String statusTypeId) {
		try {
			if (UtilValidate.isNotEmpty(value)) {
				EntityCondition condition = EntityCondition.makeCondition(EntityOperator.OR,
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("statusId")), EntityOperator.EQUALS, value.toString().toUpperCase()),
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("statusCode")), EntityOperator.EQUALS, value.toString().toUpperCase()),
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("description")), EntityOperator.EQUALS, value.toString().toUpperCase())
               			);    
				
				if (UtilValidate.isNotEmpty(statusTypeId)) {
					condition = EntityCondition.makeCondition(EntityOperator.AND,
							EntityCondition.makeCondition("statusTypeId", EntityOperator.EQUALS, statusTypeId),
	               			condition
	               			);       
				}
				
				GenericValue statusEntity = EntityQuery.use(delegator).select("statusCode").from("StatusItem").where(condition).queryFirst();
				if (UtilValidate.isNotEmpty(statusEntity)) {
					return statusEntity.getString("statusCode");
				}
			}
		} catch (Exception e) {
		}
		return null;
	}
	
	public static String getValidCifParty(Delegator delegator, String partyId ,  String partyIdentificationTypeId) {
		return getValidCifParty(delegator, partyId, partyIdentificationTypeId, false);
	}
	
	public static String getValidCifParty(Delegator delegator, String partyId ,  String partyIdentificationTypeId, boolean useCache) {
    	String idValue = null;
		try {
			EntityCondition mainCondition = EntityCondition.makeCondition(EntityOperator.AND,
                    EntityCondition.makeCondition("attrValue", EntityOperator.EQUALS, partyId),
                    EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, partyIdentificationTypeId)
                    );
			
			GenericValue custReqAttribute = EntityQuery.use(delegator).select("custRequestId").from("CustRequestAttribute").where(mainCondition).queryFirst();
			if(UtilValidate.isNotEmpty(custReqAttribute)){
				idValue = custReqAttribute.getString("custRequestId");
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return idValue;
	}

	public static String getPartyIdentificationValue(Delegator delegator, String partyId, String partyIdentificationTypeId,
			String custRequestId) {
    	String idValue = null;
		try {
			EntityCondition mainCondition = EntityCondition.makeCondition(EntityOperator.AND,
                    EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, partyIdentificationTypeId),
                    EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId)
                    );
			
			GenericValue custReqAttribute = EntityQuery.use(delegator).from("CustRequestAttribute").where(mainCondition).queryFirst();
			if(UtilValidate.isNotEmpty(custReqAttribute)){
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return idValue;
	}

	public static String getWorkEffortAssignPartyId(Delegator delegator, String workEffortId) {
		// TODO Auto-generated method stub
		if(UtilValidate.isNotEmpty(workEffortId)) {
			List conditionList = FastList.newInstance();
			conditionList.add(EntityCondition.makeCondition(EntityOperator.AND,
					EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId),
					EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PRTYASGN_ASSIGNED"),
					
					EntityCondition.makeCondition(EntityOperator.OR,
        					EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "CUSTOMER"),
        					EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "CARD_CUST")
        	                ), EntityUtil.getFilterByDateExpr()
	                ));
			EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			GenericValue partyAssignment;
			try {
				partyAssignment = EntityQuery.use(delegator).select("partyId").from("WorkEffortPartyAssignment").where(mainConditons).queryFirst();
				if (UtilValidate.isNotEmpty(partyAssignment)) {
					return partyAssignment.getString("partyId");
				}
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	public static String getSrStatus(Delegator delegator, String srNumber) {
		// TODO Auto-generated method stub
		if(UtilValidate.isNotEmpty(srNumber)) {
			try {
				GenericValue srStatus = EntityQuery.use(delegator).from("CustRequest").where("custRequestId",srNumber).queryOne();
				if (UtilValidate.isNotEmpty(srStatus)) {
					return srStatus.getString("statusId");
				}
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public static String getWorkEffortAttrId(Delegator delegator, String fromPartyId, String attrName) {
		String workEffortId = null;
		try {
			
			if (UtilValidate.isNotEmpty(fromPartyId)) {
				List<EntityCondition> conditionList = FastList.newInstance();
				conditionList.add(EntityCondition.makeCondition("attrValue", EntityOperator.EQUALS, fromPartyId));
				if (UtilValidate.isNotEmpty(attrName)) 
					conditionList.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("attrName"), EntityOperator.EQUALS, EntityFunction.UPPER(attrName)));
				
				EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				GenericValue attribute = EntityQuery.use(delegator).select("workEffortId").from("WorkEffortAttribute").where(mainConditons).queryFirst();
				if (UtilValidate.isNotEmpty(attribute)) {
					workEffortId = attribute.getString("workEffortId");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return workEffortId;
	}
	
	@SuppressWarnings("unchecked")
    public static < T > T convertDateTimestamp(String value, SimpleDateFormat sdf, String type, String returnType) {
        Date date = null;
        try {
            List < SimpleDateFormat > dateFormatList = getDateFormats(type);
            if (dateFormatList.size() > 0) {
                for (SimpleDateFormat format: dateFormatList) {
                    try {
                        format.setLenient(false);
                        date = format.parse(value);
                    } catch (ParseException e) {}
                    if (date != null) {
                        if (returnType.equalsIgnoreCase(DateTimeTypeConstant.SQL_DATE)) {
                            return (T) new java.sql.Date(date.getTime());
                        } else if (returnType.equalsIgnoreCase(DateTimeTypeConstant.TIMESTAMP)) {
                            String stamp = sdf.format(date.getTime());
                            return (T) Timestamp.valueOf(stamp);
                        } else if (returnType.equalsIgnoreCase(DateTimeTypeConstant.UTIL_DATE)) {
                            return (T) new Date(date.getTime());
                        } else if (returnType.equalsIgnoreCase(DateTimeTypeConstant.STRING)) {
                            String dateStr = sdf.format(date.getTime());
                            return (T) dateStr;
                        }
                        break;
                    }
                }
            }
        } catch (Exception e) {
            Debug.logError("Date Time Conversion Error : " + e.getMessage(), MODULE);
        }
        return null;
    }
    public static List <SimpleDateFormat> getDateFormats(String formatType) {
        List < SimpleDateFormat > dateFormats = null;
        try {
            if (DateTimeTypeConstant.DATE.equalsIgnoreCase(formatType)) {
                dateFormats = new ArrayList < SimpleDateFormat > () {
                    /**
                     * 
                     */
                    private static final long serialVersionUID = 8458284727816509794L;

                    {
                        add(new SimpleDateFormat("MM/dd/yy"));
                        add(new SimpleDateFormat("dd/MM/yy"));
                        add(new SimpleDateFormat("MM/dd/yyyy"));
                        add(new SimpleDateFormat("dd/MM/yyyy"));
                        add(new SimpleDateFormat("M/dd/yyyy"));
                        add(new SimpleDateFormat("dd.M.yyyy"));
                        add(new SimpleDateFormat("M/dd/yyyy hh:mm:ss a"));
                        add(new SimpleDateFormat("dd.M.yyyy hh:mm:ss a"));
                        add(new SimpleDateFormat("dd.MMM.yyyy"));
                        add(new SimpleDateFormat("dd-MMM-yyyy"));
                        add(new SimpleDateFormat("dd-MM-yyyy"));
                        add(new SimpleDateFormat("dd MMM yyyy"));
                        add(new SimpleDateFormat("yyyy-MM-dd"));
                        add(new SimpleDateFormat("yyyyMMdd"));
                    }
                };
            } else if (DateTimeTypeConstant.TIMESTAMP.equalsIgnoreCase(formatType)) {
                dateFormats = new ArrayList < SimpleDateFormat > () {

                    /**
                     * 
                     */
                    private static final long serialVersionUID = -659231124932530282L;

                    {
                        add(new SimpleDateFormat("MM/dd/yy HH:mm:ss"));
                        add(new SimpleDateFormat("dd/MM/yy HH:mm:ss"));
                        add(new SimpleDateFormat("MM/dd/yyyy HH:mm:ss"));
                        add(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"));
                        add(new SimpleDateFormat("M/dd/yyyy HH:mm:ss"));
                        add(new SimpleDateFormat("dd.M.yyyy HH:mm:ss"));
                        add(new SimpleDateFormat("dd.MMM.yyyy HH:mm:ss"));
                        add(new SimpleDateFormat("dd.MM.yyyy HH:mm:ss"));
                        add(new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss"));
                        add(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss"));
                        add(new SimpleDateFormat("dd MMM yyyy HH:mm:ss"));
                        add(new SimpleDateFormat("dd MM yyyy HH:mm:ss"));
                        add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
                        add(new SimpleDateFormat("yyyyMMdd HH:mm:ss"));
                    }
                };
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateFormats;
    }
    
    public static Map < String, Object > getMapFromGeneric(List < GenericValue > list, String keyField, String valueField, boolean keyDescrtion) {
        Map < String, Object > dataMap = new LinkedHashMap < String, Object > ();
        if (UtilValidate.isNotEmpty(list)) {
            if (keyDescrtion)
                dataMap = list.stream().collect(Collectors.toMap(s -> s.getString(keyField) + "(" + s.getString(valueField) + ")", s -> UtilValidate.isNotEmpty(s.getString(valueField))? s.getString(valueField) : "", (oldValue, newValue) -> newValue, LinkedHashMap::new));
            else
                dataMap = list.stream().collect(Collectors.toMap(s -> s.getString(keyField), s -> UtilValidate.isNotEmpty(s.getString(valueField))? s.getString(valueField) : "", (oldValue, newValue) -> newValue, LinkedHashMap::new));
        }
        return dataMap;
    }
    public static String getBusinessUnitId(Delegator delegator, String partyId) {
    	String businessId = null;
		try {
			if (UtilValidate.isNotEmpty(partyId)) {
				 GenericValue person = EntityQuery.use(delegator).from("Person").where("partyId", partyId).queryOne();
				 if (UtilValidate.isNotEmpty(person)) {
					 businessId = person.getString("businessUnit");
				 }
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return businessId;
	}
    public static String getUserLoginPartyId(Delegator delegator, String userLoginId) {
    	String partyId = null;
		try {
			if (UtilValidate.isNotEmpty(userLoginId)) {
				 GenericValue userLogin = EntityQuery.use(delegator).from("UserLogin").where("userLoginId", userLoginId).queryOne();
				 if (UtilValidate.isNotEmpty(userLogin)) {
					 partyId = userLogin.getString("partyId");
				 }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return partyId;
	}
    public static String getPartyUserLoginId(Delegator delegator, String partyId) {
    	String userLoginId = null;
		try {
			if (UtilValidate.isNotEmpty(partyId)) {
				EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId),
						EntityCondition.makeCondition("enabled", EntityOperator.EQUALS, "Y"));
				GenericValue userLogin = EntityUtil
						.getFirst(delegator.findList("UserLogin", condition, null, null, null, false));
				if (UtilValidate.isNotEmpty(userLogin)) {
					userLoginId = userLogin.getString("userLoginId");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return userLoginId;
	}
    
    @SuppressWarnings("unchecked")
    public static Map<String, Object> getAccessCritriaList(Delegator delegator, List<Map<String, Object>> buInfo,String userLoginId, String opLevel){
        Map<String, Object> result = new HashMap<String, Object>();
        try { 
            if(UtilValidate.isNotEmpty(buInfo)) {
                List<String> teamList = new LinkedList<String>();
                List<String> businessLists = new LinkedList<String>();
                for(Map<String, Object> buList : buInfo) {
                    String bu = (String) buList.get("bu");	
                    businessLists.add(bu);
                    teamList.addAll((List<String>) buList.get("team_list"));
                }
                if(UtilValidate.isNotEmpty(teamList)) {
                    List<String> loginIds = new ArrayList<String>();
                    loginIds.add(userLoginId);
                    loginIds.addAll(teamList);
                    /*
                    EntityCondition condition  = null;
                    if(UtilValidate.isNotEmpty(teamList)) {
                        condition = EntityCondition.makeCondition("emplTeamId",EntityOperator.IN,teamList);
                    }
                    
                    List<GenericValue> emplTeamFulfillment = EntityQuery.use(delegator).select("partyId").from("EmplTeam").where(condition).queryList();
                    if(UtilValidate.isNotEmpty(emplTeamFulfillment)) {
                        partyIds = EntityUtil.getFieldListFromEntityList(emplTeamFulfillment, "partyId", true);
                    } */
                    result.put("loginIds", loginIds);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    
    public static Map<String, Object> getCustRequestDetail(Delegator delegator,String custRequestId){
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            String commonTeams = "";
            GenericValue securityGlobal = EntityQuery.use(delegator).from("PretailLoyaltyGlobalParameters").where("parameterId",GlobalConstants.COMMON_TEAMS).queryFirst();
            if(UtilValidate.isNotEmpty(securityGlobal)) {
                commonTeams = securityGlobal.getString("value");
            }
            List<String> commonTeamList = new ArrayList<String>();
            if(UtilValidate.isNotEmpty(commonTeams)) {
                commonTeamList = Stream.of(commonTeams.split(",")).map(e -> new String(e)).collect(Collectors.toList());
            }
            GenericValue custRequestGv = EntityQuery.use(delegator).from("CustRequest").where("custRequestId",custRequestId).queryFirst();
            if(UtilValidate.isNotEmpty(custRequestGv)) {
                String teamId = custRequestGv.getString("emplTeamId");
                if(UtilValidate.isNotEmpty(teamId)) {
                    GenericValue emplTeam = EntityQuery.use(delegator).from("EmplTeam").where("emplTeamId",teamId).queryFirst();
                    String businessUnit = "";
                    if(UtilValidate.isNotEmpty(emplTeam)) {
                        businessUnit = emplTeam.getString("businessUnit");
                        if(UtilValidate.isNotEmpty(commonTeamList) && commonTeamList.contains(teamId)) {
                            result.put("businessUnit", businessUnit);
                            result.put("teamId", teamId);
                        } else {}
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    
    public static Map<String, Object> getWorkEffortDetail(Delegator delegator,String externalId){
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            String commonTeams = "";
            GenericValue securityGlobal = EntityQuery.use(delegator).from("PretailLoyaltyGlobalParameters").where("parameterId",GlobalConstants.COMMON_TEAMS).queryFirst();
            if(UtilValidate.isNotEmpty(securityGlobal)) {
                commonTeams = securityGlobal.getString("value");
            }
            List<String> commonTeamList = new ArrayList<String>();
            if(UtilValidate.isNotEmpty(commonTeams)) {
                commonTeamList = Stream.of(commonTeams.split(",")).map(e -> new String(e)).collect(Collectors.toList());
            }
            GenericValue workEffortGv = EntityQuery.use(delegator).from("WorkEffort").where("externalId",externalId).queryFirst();
            if(UtilValidate.isNotEmpty(workEffortGv)) {
                String teamId = workEffortGv.getString("emplTeamId");
                if(UtilValidate.isNotEmpty(teamId)) {
                    GenericValue emplTeam = EntityQuery.use(delegator).from("EmplTeam").where("emplTeamId",teamId).queryFirst();
                    String businessUnit = "";
                    if(UtilValidate.isNotEmpty(emplTeam)) {
                        businessUnit = emplTeam.getString("businessUnit");
                        if(UtilValidate.isNotEmpty(commonTeamList) && commonTeamList.contains(teamId)) {
                            result.put("businessUnit", businessUnit);
                            result.put("teamId", teamId);
                        } else {}
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    
    
    public static Map<String, Object> getExistEntityDetails(Delegator delegator, String entityName, Map<String, Object> primaryKeys){
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            String commonTeams = "";
            GenericValue securityGlobal = EntityQuery.use(delegator).from("PretailLoyaltyGlobalParameters").where("parameterId",GlobalConstants.COMMON_TEAMS).queryFirst();
            if(UtilValidate.isNotEmpty(securityGlobal)) {
                commonTeams = securityGlobal.getString("value");
            }
            List<String> commonTeamList = new ArrayList<String>();
            if(UtilValidate.isNotEmpty(commonTeams)) {
                commonTeamList = Stream.of(commonTeams.split(",")).map(e -> new String(e)).collect(Collectors.toList());
            }
            GenericValue entityGv = EntityQuery.use(delegator).from(entityName).where(primaryKeys).queryFirst();
            if(UtilValidate.isNotEmpty(entityGv)) {
                String teamId = entityGv.getString("emplTeamId");
                if(UtilValidate.isNotEmpty(teamId)) {
                    GenericValue emplTeam = EntityQuery.use(delegator).from("EmplTeam").where("emplTeamId",teamId).queryFirst();
                    String businessUnit = "";
                    if(UtilValidate.isNotEmpty(emplTeam)) {
                        businessUnit = emplTeam.getString("businessUnit");
                        if(UtilValidate.isNotEmpty(commonTeamList) && commonTeamList.contains(teamId)) {
                            result.put("businessUnit", businessUnit);
                            result.put("teamId", teamId);
                        } else {}
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    
    public static String getGlobalValue(Delegator delegator, String parameterId) {
    	return getGlobalValue(delegator, parameterId, null);
    }
    
    public static String getGlobalValue(Delegator delegator, String parameterId, String defValue) {
		try {
			if (UtilValidate.isNotEmpty(parameterId)) {
				EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("parameterId", EntityOperator.EQUALS, parameterId)
               			);    
				
				GenericValue param = EntityQuery.use(delegator).select("value").from("PretailLoyaltyGlobalParameters").where(condition).queryFirst();
				if (UtilValidate.isNotEmpty(param)) {
					return param.getString("value");
				}
			}
		} catch (Exception e) {
		}
		return defValue;
	}
    
    public static GenericValue pretailLoyaltyGlobalParameters(Delegator delegator, String parameterId) {
		try {
			if (UtilValidate.isNotEmpty(parameterId)) {
				EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("parameterId", EntityOperator.EQUALS, parameterId)
               			);    
				
				GenericValue globalParameter = EntityQuery.use(delegator).from("PretailLoyaltyGlobalParameters").where(condition).queryFirst();
				if (UtilValidate.isNotEmpty(globalParameter)) {
					return globalParameter;
				}
			}
		} catch (Exception e) {
		}
		return null;
	}
    
    public static boolean isValidMsdWorkEffortType(Delegator delegator, String workEffortType, String parentTypeId, String enumService) {
    	return UtilValidate.isNotEmpty(getMsdWorkEffortTypeId(delegator, workEffortType, parentTypeId, enumService));
	}
    
    public static String getMsdWorkEffortTypeId(Delegator delegator, String workEffortType, String parentTypeId, String enumService) {
		String workEffortTypeId = null;
		try {
			if (UtilValidate.isNotEmpty(workEffortType)) {
				workEffortType = EnumUtil.getEnumDescription(delegator, workEffortType, "IA_SOURCE", "Activities");
				if (UtilValidate.isNotEmpty(workEffortType)) {
					
					List conditionList = FastList.newInstance();
					
					conditionList.add(EntityCondition.makeCondition(EntityOperator.OR,
							EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("workEffortTypeId"), EntityOperator.EQUALS, EntityFunction.UPPER(workEffortType)),
							EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("description"), EntityOperator.EQUALS, EntityFunction.UPPER(workEffortType))
		                    ));
					
					if (UtilValidate.isNotEmpty(parentTypeId)) {
						conditionList.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("parentTypeId"), EntityOperator.EQUALS, EntityFunction.UPPER(parentTypeId)));
					}
					
					EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
					GenericValue type = EntityQuery.use(delegator).select("workEffortTypeId").from("WorkEffortType").where(mainConditons).queryFirst();
					if (UtilValidate.isNotEmpty(type)) {
						workEffortTypeId = type.getString("workEffortTypeId");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return workEffortTypeId;
	}
    
    public static void prepareAppStatusData(Map<String, Object> data) {
		if (UtilValidate.isEmpty(data.get("sourceInvoked"))) {
			data.put("sourceInvoked", SourceInvoked.UNKNOWN);
		}
	}
    
    public static Map<String, Object> getUserBuTeam(Delegator delegator, String partyId) {
    	Map<String, Object> data = new HashMap<String, Object>();
		try {
			if (UtilValidate.isNotEmpty(partyId)) {
				String businessId = "";
				String emplTeamId = "";
				 GenericValue person = EntityQuery.use(delegator).select("emplTeamId","businessUnit").from("Person").where("partyId", partyId).queryOne();
				 if(UtilValidate.isEmpty(person)) {
					 person = EntityQuery.use(delegator).select("emplTeamId","ownerBu").from("Party").where("partyId", partyId).queryOne();
					 businessId= UtilValidate.isNotEmpty(person.getString("ownerBu")) ? person.getString("ownerBu") :"";
					 emplTeamId = person.getString("emplTeamId");
					 data.put("businessUnit", businessId);
					 data.put("emplTeamId", emplTeamId);
				 } else {
					 //businessId = UtilValidate.isNotEmpty(person.getString("businessUnit")) ? person.getString("businessUnit") :"" ;
					 businessId = person.getString("businessUnit");
					 emplTeamId = person.getString("emplTeamId");
					 data.put("businessUnit", businessId);
					 data.put("emplTeamId", emplTeamId);
				 }
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return data;
	}
    
    public static String getCustRequestAttrValue(Delegator delegator, String attrName,
			String custRequestId) {
    	String idValue = null;
		try {
			EntityCondition mainCondition = EntityCondition.makeCondition(EntityOperator.AND,
                    EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, attrName),
                    EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId)
                    );
			
			GenericValue custReqAttribute = EntityQuery.use(delegator).select("attrValue").from("CustRequestAttribute").where(mainCondition).cache().queryFirst();
			if(UtilValidate.isNotEmpty(custReqAttribute)){
				idValue = custReqAttribute.getString("attrValue");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return idValue;
	}
    
    public static String getWorkEffortTypeDescription(Delegator delegator, String workEffortType, String parentTypeId) {
		String workEffortTypeId = null;
		try {
			if (UtilValidate.isNotEmpty(workEffortType)) {
				List conditionList = FastList.newInstance();
				
				conditionList.add(EntityCondition.makeCondition(EntityOperator.OR,
						EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("workEffortTypeId"), EntityOperator.EQUALS, EntityFunction.UPPER(workEffortType)),
						EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("description"), EntityOperator.EQUALS, EntityFunction.UPPER(workEffortType))
	                    ));
				
				if (UtilValidate.isNotEmpty(parentTypeId)) {
					conditionList.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("parentTypeId"), EntityOperator.EQUALS, EntityFunction.UPPER(parentTypeId)));
				}
				
				EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				GenericValue type = EntityQuery.use(delegator).select("description").from("WorkEffortType").where(mainConditons).queryFirst();
				if (UtilValidate.isNotEmpty(type)) {
					workEffortTypeId = type.getString("description");
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return workEffortTypeId;
	}

	public static GenericValue getStatusItem(Delegator delegator, String value, String statusTypeId) {
		try {
			if (UtilValidate.isNotEmpty(value)) {
				EntityCondition condition = EntityCondition.makeCondition(EntityOperator.OR,
	           			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("statusId")), EntityOperator.EQUALS, value.toString().toUpperCase()),
	           			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("statusCode")), EntityOperator.EQUALS, value.toString().toUpperCase()),
	           			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("description")), EntityOperator.EQUALS, value.toString().toUpperCase())
	           			);    
				
				if (UtilValidate.isNotEmpty(statusTypeId)) {
					condition = EntityCondition.makeCondition(EntityOperator.AND,
							EntityCondition.makeCondition("statusTypeId", EntityOperator.EQUALS, statusTypeId),
	               			condition
	               			);       
				}
				
				GenericValue statusEntity = EntityQuery.use(delegator).select("statusId", "statusCode", "description", "statusTypeId").from("StatusItem").where(condition).queryFirst();
				return statusEntity;
			}
		} catch (Exception e) {
		}
		return null;
	}
    
	public static String getEmplTeamId(Delegator delegator, String partyId) {
		
		try {
			
			if (UtilValidate.isNotEmpty(partyId)) {
				
				String businessUnit = DataUtil.getBusinessUnitId(delegator, partyId);
				if (UtilValidate.isNotEmpty(businessUnit)) {
					GenericValue emplTeamFulfillment = EntityQuery.use(delegator).select("emplTeamId").from("EmplPositionFulfillment").where("partyId", partyId,"businessUnit", businessUnit).filterByDate().queryFirst();
                    if(UtilValidate.isNotEmpty(emplTeamFulfillment)) {
                        String emplTeamId = emplTeamFulfillment.getString("emplTeamId");
                        return emplTeamId;
                    }
				}
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		
		return null;
	}
	
	public static String getGeoName(Delegator delegator, String value) {
		return getGeoName(delegator, value, null);
	}
	
	public static String getGeoName(Delegator delegator, String value, String geoTypeId) {
        try {
            if (UtilValidate.isNotEmpty(value)) {
            	List<EntityCondition> conditions = new ArrayList<EntityCondition>();
				conditions.add(EntityCondition.makeCondition(EntityOperator.OR,
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("geoId")), EntityOperator.EQUALS, value.toString().toUpperCase()),
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("geoCode")), EntityOperator.EQUALS, value.toString().toUpperCase()),
               			EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("geoName")), EntityOperator.EQUALS, value.toString().toUpperCase())
               			));   
				
				conditions.add(EntityCondition.makeCondition("geoTypeId", EntityOperator.IN, StringUtil.split(geoTypeId, ",")));
				EntityCondition mainConditon = EntityCondition.makeCondition(conditions, EntityOperator.AND);
				GenericValue entity = EntityQuery.use(delegator).select("geoName").from("Geo").where(mainConditon).cache(true).queryFirst();
				if (UtilValidate.isNotEmpty(entity)) {
                    return entity.getString("geoName");
                }
            }
        } catch (Exception e) {}
        return "";
    }
	
	public static String getUomDescription(Delegator delegator, String value) {
		return getUomDescription(delegator, value, null);
	}
	
	public static String getUomDescription(Delegator delegator, String value, String uomTypeId) {
        try {
            if (UtilValidate.isNotEmpty(value)) {
                EntityCondition condition = EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("uomId")), EntityOperator.EQUALS, value.toString().toUpperCase());

                condition = EntityCondition.makeCondition(EntityOperator.AND,
                	condition,
                    EntityCondition.makeCondition("uomTypeId", EntityOperator.EQUALS, uomTypeId)
                );

                GenericValue entity = EntityQuery.use(delegator).select("description").from("Uom").where(condition).cache().queryFirst();
                if (UtilValidate.isNotEmpty(entity)) {
                    return entity.getString("description");
                }
            }
        } catch (Exception e) {}
        return "";
    }
	
	public static String getTeamName(Delegator delegator, String emplTeamId) {
        try {
            if (UtilValidate.isNotEmpty(emplTeamId)) {
            	EntityCondition condition = EntityCondition.makeCondition("emplTeamId", EntityOperator.EQUALS, emplTeamId);

                GenericValue entity = EntityQuery.use(delegator).select("teamName").from("EmplTeam").where(condition).queryFirst();
                if (UtilValidate.isNotEmpty(entity)) {
                    return entity.getString("teamName");
                }
            }
        } catch (Exception e) {}
        return "";
    }
	
	public static String getPartyDataSource(Delegator delegator, String partyId) {
        try {
        	List conditionList = FastList.newInstance();
			
			conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
			//conditionList.add(EntityCondition.makeCondition("dataSourceId", EntityOperator.EQUALS, entry.getString("source")));
			conditionList.add(EntityUtil.getFilterByDateExpr());
			
			EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
			
			GenericValue entity = EntityQuery.use(delegator).select("dataSourceId").from("PartyDataSource").where(mainConditons).queryFirst();
            if (UtilValidate.isNotEmpty(entity)) {
            	return getPartyDataSource(delegator, partyId, entity.getString("dataSourceId"));
            }
        } catch (Exception e) {
        	
        }
        return null;
    }
	public static String getPartyDataSource(Delegator delegator, String partyId, String dataSourceId) {
        try {
            if (UtilValidate.isNotEmpty(partyId) && UtilValidate.isNotEmpty(dataSourceId)) {
            	EntityCondition condition = EntityCondition.makeCondition("dataSourceId", EntityOperator.EQUALS, dataSourceId);
            	GenericValue entity = EntityQuery.use(delegator).select("description").from("DataSource").where(condition).cache().queryFirst();
            	if (UtilValidate.isNotEmpty(entity)) {
            		return entity.getString("description");
            	}
            }
        } catch (Exception e) {
        	
        }
        return "";
    }
	
	public static String getPartyDataSourceId(Delegator delegator, String partyId) {
        try {
            if (UtilValidate.isNotEmpty(partyId)) {
            	List conditionList = FastList.newInstance();
				
				conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
				//conditionList.add(EntityCondition.makeCondition("dataSourceId", EntityOperator.EQUALS, entry.getString("source")));
				conditionList.add(EntityUtil.getFilterByDateExpr());
				
				EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				
				GenericValue entity = EntityQuery.use(delegator).from("PartyDataSource").where(mainConditons).queryFirst();
                if (UtilValidate.isNotEmpty(entity)) {
                	return entity.getString("dataSourceId");
                }
            }
        } catch (Exception e) {
        	
        }
        return "";
    }
	public static Map<String, Object> getPartyDataSourceList(Delegator delegator, List<GenericValue> dataList, String fieldId) {
		return getPartyDataSourceList(delegator,dataList,fieldId,false);
	}
	public static Map<String, Object> getPartyDataSourceList(Delegator delegator, List<GenericValue> dataList, String fieldId,boolean onlyActive) {
		Map<String, Object> results = new HashMap<>();
		try {
			if (UtilValidate.isNotEmpty(dataList)) {
				List<String> typeIds = EntityUtil.getFieldListFromEntityList(dataList, fieldId, true).stream()
						.map(x->x.toString()).collect(Collectors.toList());
				results = typeIds.stream().filter(x->UtilValidate.isNotEmpty(x)).collect(Collectors.toMap(x -> x.toString(),
						x -> {
							String value ="";
							try {
								EntityCondition condition =null;
								if(onlyActive)
									condition = EntityCondition.makeCondition(EntityOperator.AND,EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, x),EntityUtil.getFilterByDateExpr());
								else
									condition = EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, x);
								GenericValue entity = EntityQuery.use(delegator).select("dataSourceId").from("PartyDataSource").where(condition).queryFirst();
								if (UtilValidate.isNotEmpty(entity)) {
									condition = EntityCondition.makeCondition("dataSourceId", EntityOperator.EQUALS, entity.getString("dataSourceId"));
									entity = EntityQuery.use(delegator).select("description").from("DataSource").where(condition).queryFirst();
									if (UtilValidate.isNotEmpty(entity)) {
										//return entity.getString("description");
										value = UtilValidate.isNotEmpty(entity.getString("description")) ? entity.getString("description") : "";
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
							return value;
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
	
	public static String getPartyClassification(Delegator delegator, String partyId) {
        try {
            if (UtilValidate.isNotEmpty(partyId)) {
            	EntityCondition condition = EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId);

                GenericValue entity = EntityQuery.use(delegator).select("description").from("PartyClassificationAndGroup").where(condition).queryFirst();
                if (UtilValidate.isNotEmpty(entity)) {
                	return entity.getString("description");
                }
            }
        } catch (Exception e) {}
        return "";
    }
	
	public static String getWorkEffortEmailId(Delegator delegator, String workEffortId) {
		
		try {
			if (UtilValidate.isNotEmpty(workEffortId)) {
				GenericValue workEffortCommExtension = EntityQuery.use(delegator).select("wftExtValue").from("WorkEffortCommExtension").where("workEffortId", workEffortId,"wftExtType", "TO_TYPE").queryFirst();
				if(UtilValidate.isNotEmpty(workEffortCommExtension) && UtilValidate.isNotEmpty(workEffortCommExtension.getString("wftExtValue"))) {
					String wftExtValue = workEffortCommExtension.getString("wftExtValue");
					return wftExtValue;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return null;
	}
	
	public static String getSrPrimaryContact(Delegator delegator, String custRequestId) {
    	String primaryContactId = null;
		try {
			if (UtilValidate.isNotEmpty(custRequestId)) {
				List<EntityCondition> conditionList = new LinkedList<EntityCondition>();
				conditionList.add(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId));
				conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "CONTACT"));
				conditionList.add(EntityCondition.makeCondition("isPrimary", EntityOperator.EQUALS, "Y"));
				EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);

				GenericValue entity = EntityQuery.use(delegator).select("partyId").from("CustRequestContact").where(mainConditons).filterByDate().queryFirst();
				
				if (UtilValidate.isNotEmpty(entity)) {
					primaryContactId = entity.getString("partyId");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return primaryContactId;
	}
	public static List<String> getSrPrimaryContactList(Delegator delegator, String custRequestId) {
    	List<String> primaryContactIds = new ArrayList<String>();
		try {
			if (UtilValidate.isNotEmpty(custRequestId)) {
				List<EntityCondition> conditionList = new LinkedList<EntityCondition>();
				conditionList.add(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId));
				conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "CONTACT"));
				conditionList.add(EntityCondition.makeCondition("isPrimary", EntityOperator.EQUALS, "Y"));
				EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);

				List<GenericValue> entity = EntityQuery.use(delegator).select("partyId").from("CustRequestContact").where(mainConditons).filterByDate().queryList();
				if (UtilValidate.isNotEmpty(entity)) {
					primaryContactIds.addAll(EntityUtil.getFieldListFromEntityList(entity, "partyId", true));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return primaryContactIds;
	}
	
	public static String getSrHomeOwner(Delegator delegator, String custRequestId) {
    	String homeOwnerId = null;
		try {
			if (UtilValidate.isNotEmpty(custRequestId)) {
				List<EntityCondition> conditionList = new LinkedList<EntityCondition>();
				conditionList.add(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId));
				conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "CUSTOMER"));
				conditionList.add(EntityUtil.getFilterByDateExpr());
				EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);

				GenericValue entity = EntityQuery.use(delegator).select("partyId").from("CustRequestParty").where(mainConditons).queryFirst();
				if (UtilValidate.isNotEmpty(entity)) {
					homeOwnerId = entity.getString("partyId");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return homeOwnerId;
	}
	
	public static String getSrActivityOwners(Delegator delegator, String custRequestId) {
    	String ownerName = "";
		try {
			if (UtilValidate.isNotEmpty(custRequestId)) {
				List<EntityCondition> conditionList = new LinkedList<EntityCondition>();
				conditionList.add(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId));
				EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);

				List<GenericValue> entityList = delegator.findList("CustRequestWorkEffort", mainConditons, UtilMisc.toSet("workEffortId"), null, null, false);
				if (UtilValidate.isNotEmpty(entityList)) {
					Set<String> primOwnerIdList = new LinkedHashSet<>();
					for (GenericValue entity : entityList) {
						String workEffortId = entity.getString("workEffortId");
						conditionList = new LinkedList<EntityCondition>();
						conditionList.add(EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId));
						mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);

						GenericValue activity = EntityQuery.use(delegator).select("primOwnerId").from("WorkEffort").where(mainConditons).queryFirst();
						if (UtilValidate.isNotEmpty(activity) && UtilValidate.isNotEmpty(activity.getString("primOwnerId"))) {
							primOwnerIdList.add(activity.getString("primOwnerId"));
						}
					}
					
					for (String primOwnerId : primOwnerIdList) {
						ownerName += PartyHelper.getPartyName(delegator, DataUtil.getPartyIdByUserLoginId(delegator, primOwnerId), false) + ",";
					}
					
					if (UtilValidate.isNotEmpty(ownerName)) {
						ownerName = ownerName.substring(0, ownerName.length()-1);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ownerName;
	}
	
	public static String getCampaignChannelType(Delegator delegator, String campaignTypeId) {
        try {
            if (UtilValidate.isNotEmpty(campaignTypeId)) {
            	EntityCondition condition = EntityCondition.makeCondition("campaignTypeId", EntityOperator.EQUALS, campaignTypeId);

                GenericValue entity = EntityQuery.use(delegator).select("description").from("CampaignType").where(condition).queryFirst();
                if (UtilValidate.isNotEmpty(entity)) {
                    return entity.getString("description");
                }
            }
        } catch (Exception e) {}
        return "";
    }
	
	public static String getPartyRoleTypeId(Delegator delegator, String partyId) {

        try {
            if (UtilValidate.isNotEmpty(partyId)) {
            	GenericValue party = delegator.findOne("Party", UtilMisc.toMap("partyId", partyId), false);
				if (UtilValidate.isNotEmpty(party)) {
					return party.getString("roleTypeId");
				}
            }
        } catch (Exception e) {}
        return null;
    }
	
	public static boolean isPartyRoleExists(Delegator delegator, String partyId, String roleTypeId) {
        try {
            if (UtilValidate.isNotEmpty(partyId) && UtilValidate.isNotEmpty(roleTypeId)) {
            	for (String roleId : roleTypeId.split(",")) {
            		GenericValue partyRole = EntityQuery.use(delegator).from("PartyRole")
                			.where("partyId", partyId, "roleTypeId", roleId).queryFirst();
                	if (UtilValidate.isNotEmpty(partyRole)) {
    					return true;
    				}
            	}
            }
        } catch (Exception e) {}
        return false;
    }
	
	public static List<String> getPartyIds(Delegator delegator, String roleTypeId) {
		List<String> partyIds = new ArrayList<>();
        try {
            if (UtilValidate.isNotEmpty(roleTypeId)) {
            	List<GenericValue> partyRoles = delegator.findByAnd("PartyRole", UtilMisc.toMap("roleTypeId", roleTypeId), null, false);
				if (UtilValidate.isNotEmpty(partyRoles)) {
					return partyRoles.stream().map(x->x.getString("partyId")).distinct().collect(Collectors.toList());
				}
            }
        } catch (Exception e) {}
        return partyIds;
    }

	public static boolean isBase64(String stringBase64){
	        String regex =
	               "([A-Za-z0-9+/]{4})*"+
	               "([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)";

	        Pattern patron = Pattern.compile(regex);

	        if (!patron.matcher(stringBase64).matches()) {
	            return false;
	        } else {
	            return true;
	        }
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getAccessList(Map<String, Object> context){
		Delegator delegator = (Delegator) context.get("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) context.get("dispatcher");
        Map<String, Object> result = new HashMap<String, Object>();
		try {
			String accessLevel = "Y";
	        String opLevel = "L1";
			List<Map<String, Object>> buInfo = new ArrayList<Map<String, Object>>();
			String userLoginId = (String) context.get("userLoginId");
             if (UtilValidate.isNotEmpty(context)) {
                 Map<String, Object> callCtx = new LinkedHashMap<String, Object>();
                 if(UtilValidate.isNotEmpty(context.get("teamId")))
                	 callCtx.put("teamId", context.get("teamId"));
                 callCtx.put("businessUnit", context.get("businessUnit"));
                 callCtx.put("modeOfOp", context.get("modeOfOp"));
                 callCtx.put("entityName", context.get("entityName"));
                 callCtx.put("userLoginId", userLoginId);
                 Map<String, Object> accessMatrixRes = dispatcher.runSync("ap.getAccessMatrixInfo", callCtx);
                 if (ServiceUtil.isSuccess(accessMatrixRes)) {
                     JSONObject accessMatrixObj = JSONObject.fromObject(accessMatrixRes.get("securityLevelInfo").toString());
                     accessLevel = (String) accessMatrixObj.get("access_level");
                     opLevel = (String) accessMatrixRes.get("opLevel");
                     buInfo = (List<Map<String, Object>>) accessMatrixObj.get("bu_info");
                 } else {
                     accessLevel = null;
                     result.put(ModelService.ERROR_MESSAGE, accessMatrixRes.get("errorMessage").toString());
                 }
             }
             
             if (UtilValidate.isNotEmpty(accessLevel) && AccessLevel.YES.equals(accessLevel) ) {
        		 Map<String, Object> accessableList = org.fio.homeapps.util.DataUtil.getAccessCritriaList(delegator, buInfo, userLoginId, opLevel);
        		 if (UtilValidate.isNotEmpty(accessableList)) {
        			 if (AccessLevel.LEVEL2.equals(opLevel))
        				 result.put("ownerId", UtilValidate.isNotEmpty(accessableList.get("loginIds")) ? (List<String> ) accessableList.get("loginIds") : new ArrayList<>());
                     else
                    	 result.put("emplTeamId", UtilValidate.isNotEmpty(accessableList.get("loginIds")) ? (List<String> ) accessableList.get("loginIds") : new ArrayList<>());
        			 
        		 }
             } else if (AccessLevel.ALL.equals(accessLevel) ) {
            	 Debug.log("access level "+accessLevel, MODULE);
            	 accessLevel = AccessLevel.YES;
             }
             result.put("accessLevel", accessLevel);
             result.put("opLevel", opLevel);
		} catch (Exception e) {
			Debug.log("Error : "+e.getMessage(), MODULE);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
		}
		return result;
	}
	
	public static boolean validateSecurityPermission(Delegator delegator, List<String> groupIds, String permissionId) {
		boolean hasPermission = false;
		try {
			if(UtilValidate.isNotEmpty(groupIds) && UtilValidate.isNotEmpty(permissionId)) {
				EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("groupId",EntityOperator.IN,groupIds),
						EntityCondition.makeCondition("permissionId",EntityOperator.EQUALS,permissionId));
				GenericValue userLoginSecurityGroupPermission = EntityQuery.use(delegator).from("SecurityGroupPermission").where(condition).queryFirst();
				if(UtilValidate.isNotEmpty(userLoginSecurityGroupPermission)) {
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return hasPermission;
		
	}
	
	public static String getPartyIdByUserLoginId(Delegator delegator, String userLoginId) {
    	String partyId = null;
		try {
			if (UtilValidate.isNotEmpty(userLoginId)) {
				List<EntityCondition> conditionList = new LinkedList<EntityCondition>();
				conditionList.add(EntityCondition.makeCondition("userLoginId", EntityOperator.EQUALS, userLoginId));
				
				EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				GenericValue entity = EntityQuery.use(delegator).select("partyId").from("UserLogin").where(mainConditons).cache().queryFirst();
				if (UtilValidate.isNotEmpty(entity)) {
					partyId = entity.getString("partyId");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return partyId;
	}
	
	public static GenericValue getActiveUserLoginByPartyId(Delegator delegator, String partyId) {
		try {
			if (UtilValidate.isNotEmpty(partyId)) {
				List<EntityCondition> conditionList = new LinkedList<EntityCondition>();
				conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
				conditionList.add(EntityCondition.makeCondition("enabled", EntityOperator.EQUALS, "Y"));
				
				EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				GenericValue entity = EntityQuery.use(delegator).from("UserLogin").where(mainConditons).cache().queryFirst();
				return entity;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getActiveUserLoginIdByPartyId(Delegator delegator, String partyId) {
		try {
			if (UtilValidate.isNotEmpty(partyId)) {
				List<EntityCondition> conditionList = new LinkedList<EntityCondition>();
				conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
				conditionList.add(EntityCondition.makeCondition("enabled", EntityOperator.EQUALS, "Y"));
				
				EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				GenericValue entity = EntityQuery.use(delegator).select("userLoginId").from("UserLogin").where(mainConditons).cache().queryFirst();
				if (UtilValidate.isNotEmpty(entity)) {
					return entity.getString("userLoginId");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static boolean isActiveUserLogin(Delegator delegator, String userLoginId) {
		try {
			if (UtilValidate.isNotEmpty(userLoginId)) {
				List<EntityCondition> conditionList = new LinkedList<EntityCondition>();
				conditionList.add(EntityCondition.makeCondition("userLoginId", EntityOperator.EQUALS, userLoginId));
				conditionList.add(EntityCondition.makeCondition("enabled", EntityOperator.EQUALS, "Y"));
				
				EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				GenericValue entity = EntityQuery.use(delegator).from("UserLogin").where(mainConditons).queryFirst();
				return UtilValidate.isNotEmpty(entity);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static String getUserLoginName(Delegator delegator, String partyId) {
		String partyName = null;
		try {
			if(UtilValidate.isNotEmpty(partyId)) {
				GenericValue person = EntityQuery.use(delegator).from("Person").where("partyId", partyId).cache(true).queryFirst();
				if(UtilValidate.isEmpty(person)) {
					GenericValue partyGroup = EntityQuery.use(delegator).from("PartyGroup").where("partyId", partyId).cache(true).queryFirst();
					partyName = UtilValidate.isNotEmpty(partyGroup) ? partyGroup.getString("groupName") : "";
				} else {
					partyName = person.getString("firstName")+ (UtilValidate.isNotEmpty(person.getString("lastName")) ? " "+person.getString("lastName") : "" );
				}
			}
		} catch (Exception e) {
		}
		return partyName;
	}
	
	@SuppressWarnings("unchecked")
	public static boolean hasPermission(HttpServletRequest request, String permissionId) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		HttpSession session = request.getSession();
		boolean hasPermission = false;
		try {
			hasPermission = hasFullPermission(delegator, userLogin.getString("userLoginId"));
			if(!hasPermission) {
				List<String> groupIds = UtilValidate.isNotEmpty(session.getAttribute("userLoginSecurityGroups")) ? (List<String>) session.getAttribute("userLoginSecurityGroups") : UtilValidate.isNotEmpty(request.getAttribute("userLoginSecurityGroups")) ? (List<String>) request.getAttribute("userLoginSecurityGroups") : new LinkedList<>();
				if(UtilValidate.isNotEmpty(groupIds) && UtilValidate.isNotEmpty(permissionId)) {
					EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
							EntityCondition.makeCondition("groupId",EntityOperator.IN,groupIds),
							EntityCondition.makeCondition("permissionId",EntityOperator.EQUALS,permissionId));
					GenericValue userLoginSecurityGroupPermission = EntityQuery.use(delegator).from("SecurityGroupPermission").where(condition).queryFirst();
					if(UtilValidate.isNotEmpty(userLoginSecurityGroupPermission)) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return hasPermission;
		
	}
	
	public static Map<String, Object> getSrStatusList(Delegator delegator, String statusTypeId){
        Map<String, Object> result = new HashMap<String, Object>();
		try {
			if (UtilValidate.isNotEmpty(statusTypeId)) {
				List<EntityCondition> conditionList = new LinkedList<EntityCondition>();
				conditionList.add(EntityCondition.makeCondition("statusTypeId", EntityOperator.EQUALS, statusTypeId));
				EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);

				List<GenericValue> statusItemList = delegator.findList("StatusItem", mainConditons, null, UtilMisc.toList("sequenceId"), null, false);
				
				for (GenericValue statusItem : statusItemList) {
					String statusId = statusItem.getString("statusId");
					String sequenceId = statusItem.getString("sequenceId");
					result.put(statusId, sequenceId);
				}
			}
		}catch (Exception e) {
			Debug.log("Error : "+e.getMessage(), MODULE);
			result.put(ModelService.ERROR_MESSAGE, e.getMessage());
		}
		return result;
	}
	
	public static String getActivityOwnersName(Delegator delegator, String workEffortId) {
		return getActivityOwnersName(delegator, workEffortId, null);
	}
	
	public static String getActivityOwnersName(Delegator delegator, String workEffortId, List<String> ownerRoles) {
		return getActivityOwnersName(delegator, workEffortId, ownerRoles, null);
	}
	
	public static String getActivityOwnersName(Delegator delegator, String workEffortId, List<String> ownerRoles, String activityOwnerRole) {
		String ownerName = "";
		try {
			if(UtilValidate.isNotEmpty(workEffortId)) {
				if (UtilValidate.isEmpty(activityOwnerRole)) {
					activityOwnerRole = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "ACT_OWNER", "TECHNICIAN");
				}
				List<String> ownersPartyIds = getActivityOwnersPartyIds(delegator, workEffortId, ownerRoles, false, activityOwnerRole);
				if(UtilValidate.isNotEmpty(ownersPartyIds)) {
					List<String> names = new ArrayList<>();
					for(String partyId : ownersPartyIds) {
						String cacheKey = "PARTY_NAME_"+partyId;
						if (CacheUtil.getInstance().notContains(cacheKey)) {
							CacheUtil.getInstance().put(cacheKey, org.fio.homeapps.util.PartyHelper.getPersonName(delegator, partyId, false));
						}
						names.add((String) CacheUtil.getInstance().get(cacheKey));
						//names.add(org.fio.homeapps.util.PartyHelper.getPersonName(delegator, partyId, false));
					}
					if(UtilValidate.isNotEmpty(names)) ownerName = listToString(names, "");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ownerName;
	}
	
	public static String getActivityOwnersName(Delegator delegator, List<String> ownersPartyIds) {
		String ownerName = "";
		try {
			if(UtilValidate.isNotEmpty(ownersPartyIds)) {
				if(UtilValidate.isNotEmpty(ownersPartyIds)) {
					List<String> names = new ArrayList<>();
					for(String partyId : ownersPartyIds) {
						String cacheKey = "PARTY_NAME_"+partyId;
						if (CacheUtil.getInstance().notContains(cacheKey)) {
							CacheUtil.getInstance().put(cacheKey, org.fio.homeapps.util.PartyHelper.getPersonName(delegator, partyId, false));
						}
						names.add((String) CacheUtil.getInstance().get(cacheKey));
						//names.add(org.fio.homeapps.util.PartyHelper.getPersonName(delegator, partyId, false));
					}
					if(UtilValidate.isNotEmpty(names)) ownerName = listToString(names, "");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ownerName;
	}
	
	public static List<String> getActivityOwnersPartyIds(Delegator delegator, String workEffortId) {
		return getActivityOwnersPartyIds(delegator, workEffortId, null, false);
	}
	
	public static List<String> getActivityOwnersPartyIds(Delegator delegator, String workEffortId, List<String> ownerRoles) {
		return getActivityOwnersPartyIds(delegator, workEffortId, ownerRoles, false);
	}
	
	public static List<String> getActivityOwnersPartyIds(Delegator delegator, String workEffortId, List<String> ownerRoles, boolean isOnlyActiveOwner) {
		return getActivityOwnersPartyIds(delegator, workEffortId, ownerRoles, isOnlyActiveOwner, null);
	}
	
	public static List<String> getActivityOwnersPartyIds(Delegator delegator, String workEffortId, List<String> ownerRoles, boolean isOnlyActiveOwner, String activityOwnerRole) {
		List<String> ownersPartyIds = new ArrayList<>();
		try {
			if(UtilValidate.isNotEmpty(workEffortId)) {
				if (UtilValidate.isEmpty(ownerRoles)) {
					ownerRoles = new ArrayList<>();
					if (UtilValidate.isEmpty(activityOwnerRole)) {
						activityOwnerRole = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "ACT_OWNER", "TECHNICIAN");
					}
					if(UtilValidate.isNotEmpty(activityOwnerRole)) {
						if(UtilValidate.isNotEmpty(activityOwnerRole) && activityOwnerRole.contains(",")) {
							ownerRoles = stringToList(activityOwnerRole, ",");
						} else
							ownerRoles.add(activityOwnerRole);
					}
				}
				
				if(UtilValidate.isEmpty(ownerRoles)) ownerRoles.add("CAL_OWNER");
				List<EntityCondition> conditionList = FastList.newInstance();
				conditionList.add(EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId),
						EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PRTYASGN_ASSIGNED"),
						EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, ownerRoles)
						//EntityUtil.getFilterByDateExpr()
		                ));
				
				if (isOnlyActiveOwner) {
					conditionList.add(EntityUtil.getFilterByDateExpr());
				}
				
				EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
				List<GenericValue> workEffortPartyAssignList = EntityQuery.use(delegator).select("partyId").from("WorkEffortPartyAssignment").where(mainConditons).filterByDate().cache(true).queryList();
				ownersPartyIds = UtilValidate.isNotEmpty(workEffortPartyAssignList) ? EntityUtil.getFieldListFromEntityList(workEffortPartyAssignList, "partyId", true) : new ArrayList<>();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ownersPartyIds;
	}
	
	public static String listToString(Collection<String> list, String separator) {
		String value = "";
		try {
			separator = UtilValidate.isNotEmpty(separator) ? separator : ", ";
			if(UtilValidate.isNotEmpty(list))
				value = list.stream().filter(e-> e!=null && !e.isEmpty()).map(String::trim).distinct().collect(Collectors.joining(separator));
		} catch (Exception e) {
			Debug.logError(e, e.getMessage(),MODULE);
		}
		return value;
	}
	
	public static List<String> stringToList(String str, String separator){
		List<String> list = new LinkedList<String>();
		try {
			separator = UtilValidate.isNotEmpty(separator) ? separator : ", ";
			list = Stream.of(str.trim().split(separator)).distinct().collect(Collectors.toList());
		} catch (Exception e) {
		}
		return list;
	}
	
	public static String getSrActivityOwnersName(Delegator delegator, String custRequestId) {
		return getSrActivityOwnersName(delegator, custRequestId, null);
	}
	
	public static String getSrActivityOwnersName(Delegator delegator, String custRequestId, String activityOwnerRole) {
    	String ownersName = "";
		try {
			if (UtilValidate.isNotEmpty(custRequestId)) {
				if (UtilValidate.isEmpty(activityOwnerRole)) {
					activityOwnerRole = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "ACT_OWNER", "TECHNICIAN");
				}
				
				List<EntityCondition> conditionList = new LinkedList<EntityCondition>();
				conditionList.add(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId));
				EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);

				//List<GenericValue> entityList = delegator.findList("CustRequestWorkEffort", mainConditons, UtilMisc.toSet("workEffortId"), null, null, false);
				List<GenericValue> entityList = EntityQuery.use(delegator).select("workEffortId").from("CustRequestWorkEffort").where(mainConditons).cache(true).queryList();
				if (UtilValidate.isNotEmpty(entityList)) {
					Set<String> ownersNames = new LinkedHashSet<>();
					String names = "";
					for (GenericValue entity : entityList) {
						String workEffortId = entity.getString("workEffortId");
						names = getActivityOwnersName(delegator, workEffortId, UtilMisc.toList("TECHNICIAN"), activityOwnerRole);
						if(UtilValidate.isNotEmpty(names)) ownersNames.addAll(stringToList(names, ","));
					}
					
					if(UtilValidate.isNotEmpty(ownersNames))
						ownersName = listToString(ownersNames, "");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ownersName;
	}
	
	public static Map<String, Object> getUserLoginInfo(Delegator delegator, String userLoginId) {
		String firstName = "";
		String lastName = "";
		String roleTypeId = "";
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			String userLoginPartyId = getPartyIdByUserLoginId(delegator, userLoginId);
			if(UtilValidate.isNotEmpty(userLoginPartyId)) {
				GenericValue person = EntityQuery.use(delegator).from("Person").where("partyId", userLoginPartyId).queryFirst();
				if(UtilValidate.isNotEmpty(person)) {
					if(UtilValidate.isNotEmpty(person.getString("firstName"))){
						firstName = person.getString("firstName");
					}
					if(UtilValidate.isNotEmpty(person.getString("firstName"))){
						lastName = person.getString("lastName");
					}
				}
				GenericValue party = EntityQuery.use(delegator).from("Party").where("partyId", userLoginPartyId).queryFirst();
				if(UtilValidate.isNotEmpty(party) && UtilValidate.isNotEmpty(party.getString("roleTypeId"))) {
					roleTypeId = party.getString("roleTypeId");
				}
				result.put("firstName", firstName);
				result.put("lastName", lastName);
				result.put("roleTypeId", roleTypeId);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return result;
	}
	
	public static String getSrPrimaryPartyId(Delegator delegator, String custRequestId) {
		String srPrimaryPartyId = "";
		try {
			if (UtilValidate.isNotEmpty(custRequestId)) {

				GenericValue custRequestAttribute = EntityQuery.use(delegator).select("attrValue").from("CustRequestAttribute").where("custRequestId", custRequestId,"attrName","PRIMARY").queryFirst();
				if (UtilValidate.isNotEmpty(custRequestAttribute)) {
					String attrValue = custRequestAttribute.getString("attrValue");
					if(UtilValidate.isNotEmpty(attrValue) && "HOME".equals(attrValue)){
						List<EntityCondition> conditionList = new LinkedList<EntityCondition>();
						conditionList.add(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId));
						conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "CUSTOMER"));
						conditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
						conditionList.add(EntityUtil.getFilterByDateExpr());
						EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
						
						GenericValue entity = EntityQuery.use(delegator).select("partyId").from("CustRequestParty").where(mainConditons).queryFirst();
						if (UtilValidate.isNotEmpty(entity)) {
							srPrimaryPartyId = entity.getString("partyId");
						}
						return srPrimaryPartyId;
					}
					if(UtilValidate.isNotEmpty(attrValue) && "CONTRACTOR".equals(attrValue)){
						List<EntityCondition> conditionList = new LinkedList<EntityCondition>();
						conditionList.add(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId));
						conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "CONTRACTOR"));
						conditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
						conditionList.add(EntityUtil.getFilterByDateExpr());
						EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
						
						GenericValue entity = EntityQuery.use(delegator).select("partyId").from("CustRequestParty").where(mainConditons).queryFirst();
						
						if (UtilValidate.isNotEmpty(entity)) {
							srPrimaryPartyId = entity.getString("partyId");
						}
						return srPrimaryPartyId;
					}
					if(UtilValidate.isNotEmpty(attrValue) && "DEALER".equals(attrValue)){
						List<EntityCondition> conditionList = new LinkedList<EntityCondition>();
						conditionList.add(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId));
						conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "ACCOUNT"));
						conditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
						conditionList.add(EntityUtil.getFilterByDateExpr());
						EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
						
						GenericValue entity = EntityQuery.use(delegator).select("partyId").from("CustRequestParty").where(mainConditons).queryFirst();
						
						if (UtilValidate.isNotEmpty(entity)) {
							srPrimaryPartyId = entity.getString("partyId");
						}
						return srPrimaryPartyId;
					}
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return srPrimaryPartyId;
	}
	
	public static List<LocalDate> getHolidays(Map<String, Object> context){
    	Delegator delegator = (Delegator) context.get("delegator"); 
    	String businessUnit = (String) context.get("businessUnit");
    	List<LocalDate> holidays = new ArrayList<>();
    	try {
    		List<EntityCondition> conditionList = FastList.newInstance();
			conditionList.add(EntityCondition.makeCondition(EntityOperator.OR,
							EntityCondition.makeCondition("status", EntityOperator.EQUALS, null),
							EntityCondition.makeCondition("status", EntityOperator.EQUALS, "ACTIVE"))
	                	);
			
			List<GenericValue> holidayConfigList = EntityQuery.use(delegator).from("TechDataHolidayConfig").where(EntityCondition.makeCondition(conditionList, EntityOperator.AND)).distinct(true).cache(false).queryList();
	    	if(UtilValidate.isNotEmpty(holidayConfigList)) {
	    		for(GenericValue holidayConfig : holidayConfigList) {
	    			java.sql.Date holidayDate = holidayConfig.getDate("holidayDate");
	    			holidays.add(holidayDate.toLocalDate());
	    		}
	    	}
    	} catch (Exception e) {
			//e.printStackTrace();
    		Debug.logError(e.getMessage(), MODULE);
		}
		return holidays;
    }
	
	public static String getSrPrimaryPerson(Delegator delegator, String custRequestId) {
		String attrValue="";
		try {
			if (UtilValidate.isNotEmpty(custRequestId)) {
				GenericValue custRequestAttribute = EntityQuery.use(delegator).select("attrValue").from("CustRequestAttribute").where(UtilMisc.toMap("custRequestId", custRequestId, "attrName", "PRIMARY")).queryFirst();
				if (UtilValidate.isNotEmpty(custRequestAttribute)) {
					attrValue = custRequestAttribute.getString("attrValue");
					return attrValue;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return attrValue;
	}
	
	public static String getPartyName(Delegator delegator, String partyId) {
		String partyName = null;
		try {
			if(UtilValidate.isNotEmpty(partyId)) {
				GenericValue person = EntityQuery.use(delegator).from("Person").where("partyId", partyId).cache(true).queryFirst();
				if(UtilValidate.isEmpty(person)) {
					GenericValue partyGroup = EntityQuery.use(delegator).from("PartyGroup").where("partyId", partyId).cache(true).queryFirst();
					partyName = UtilValidate.isNotEmpty(partyGroup) ? partyGroup.getString("groupName") : "";
				} else {
					partyName = person.getString("firstName")+ (UtilValidate.isNotEmpty(person.getString("lastName")) ? " "+person.getString("lastName") : "" );
				}
			}
		} catch (Exception e) {
		}
		return partyName;
	}
	
	public static Map<String, Map<String, Object>> getPrimaryContactDetails(Delegator delegator, List<GenericValue> resultList, String fieldId, String partyRoleTypeId) {
		Map<String, Map<String, Object>> dataList = new HashMap<String, Map<String, Object>>();
		
		if (UtilValidate.isNotEmpty(resultList) && UtilValidate.isNotEmpty(partyRoleTypeId)) {
			try {
				List<String> partyIds = EntityUtil.getFieldListFromEntityList(resultList, fieldId, true);

				partyIds.parallelStream().forEach(e ->{
					Map<String, Object> data = new LinkedHashMap<String, Object>();
					
					try {
						EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
								EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, e),
								EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "CONTACT"),
								EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, partyRoleTypeId),
								EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PARTY_DEFAULT"),
								EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "CONTACT_REL_INV"),
								EntityUtil.getFilterByDateExpr()
								);
						
						GenericValue assocContact = EntityQuery.use(delegator).select("partyIdFrom").from("PartyRelationship").where(condition).cache(true).queryFirst();
						if(UtilValidate.isEmpty(assocContact)) {
							condition = EntityCondition.makeCondition(EntityOperator.AND,
									EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, e),
									EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "CONTACT"),
									EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, partyRoleTypeId),
									EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "CONTACT_REL_INV"),
									EntityUtil.getFilterByDateExpr()
									);
							assocContact = EntityQuery.use(delegator).select("partyIdFrom").from("PartyRelationship").where(condition).cache(true).queryFirst();
						} 

						if (UtilValidate.isNotEmpty(assocContact)) {
							String primaryContactId = (String) assocContact.get("partyIdFrom");
							data.put("contactId", primaryContactId);
							data.put("contactName", PartyHelper.getPersonName(delegator, primaryContactId, false));
							data.put("partyIdFrom", primaryContactId);
							data.put("primaryContactEmail", PartyHelper.getEmailAddress(delegator, primaryContactId, "PRIMARY_EMAIL"));
							data.put("primaryContactPhone", PartyHelper.getContactNumber(delegator, primaryContactId, "PRIMARY_PHONE"));
							//data.put("roleTypeIdFrom", assocContact.getString("roleTypeIdFrom"));
							if(UtilValidate.isNotEmpty(data))
								dataList.put(e, data);
						}
					}catch (Exception e1) {
						e1.printStackTrace();
					}
					
				});
			}catch (Exception e) {
				e.printStackTrace();
				Debug.logError(e.getMessage(), MODULE);
			}
		} 

		return dataList;
	}
	
	public static String getTenantPropertiesValue(Delegator delegator, String resourceName, String propertyName) {
		try {
			if (UtilValidate.isNotEmpty(resourceName) && UtilValidate.isNotEmpty(propertyName)) {
				List conditions = FastList.newInstance();
				
				conditions.add(EntityCondition.makeCondition("resourceName", EntityOperator.EQUALS, resourceName));
				conditions.add(EntityCondition.makeCondition("propertyName", EntityOperator.EQUALS, propertyName));
				
                EntityCondition mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);
                GenericValue entity = EntityQuery.use(delegator).select("propertyValue").from("TenantProperties").where(mainConditons).cache(false).queryFirst();
    			if (UtilValidate.isNotEmpty(entity)) {
    				return entity.getString("propertyValue");
    			}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}
		return null;
	}
	
	public static Map<String, Object> getPartyDataSourceByPartyId(Delegator delegator, List<String> partyIdList, boolean onlyActive) {
		return getPartyDataSourceByPartyId(delegator, partyIdList, onlyActive, false);
	}
	public static Map<String, Object> getPartyDataSourceByPartyId(Delegator delegator, List<String> partyIdList) {
		return getPartyDataSourceByPartyId(delegator, partyIdList, false, false);
	}
	public static Map<String, Object> getPartyDataSourceByPartyId(Delegator delegator, List<String> partyIdList, boolean onlyActive, boolean useCache) {
		Map<String, Object> results = new HashMap<>();
		try {
			if (UtilValidate.isNotEmpty(partyIdList)) {

				results = partyIdList.stream().filter(x->UtilValidate.isNotEmpty(x)).collect(Collectors.toMap(x -> x.toString(),
						x -> {
							String value ="";
							try {
								EntityCondition condition =  null;
								if(onlyActive)
									condition = EntityCondition.makeCondition(EntityOperator.AND,EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, x),EntityUtil.getFilterByDateExpr());
								else
									condition = EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, x);
								GenericValue entity = EntityQuery.use(delegator).select("dataSourceId").from("PartyDataSource").where(condition).queryFirst();
								if (UtilValidate.isNotEmpty(entity)) {
									condition = EntityCondition.makeCondition("dataSourceId", EntityOperator.EQUALS, entity.getString("dataSourceId"));
									entity = EntityQuery.use(delegator).select("description").from("DataSource").where(condition).queryFirst();
									if (UtilValidate.isNotEmpty(entity)) {
										//return entity.getString("description");
										value = UtilValidate.isNotEmpty(entity.getString("description")) ? entity.getString("description") : "";
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
							return value;
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
	
	public static String getPartyIdByPrimaryEmail(Delegator delegator, String emailId) {
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
			TransactionUtil.begin();
			GenericValue emailAddress = EntityQuery.use(delegator).from(dynamicView).where(condition).filterByDate().queryFirst();
			if(UtilValidate.isNotEmpty(emailAddress))
				partyId = emailAddress.getString("partyId");
			TransactionUtil.commit();
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return partyId;
	}
	
	public static String getCountryTeleCode(Delegator delegator, String countryGeoId) {
		String teleCode = "";
		try {
			if (UtilValidate.isEmpty(countryGeoId)) {
				countryGeoId = getGlobalValue(delegator, "DEFAULT_COUNTRY");
			}
			if (UtilValidate.isNotEmpty(countryGeoId)) {
				GenericValue geo = delegator.findOne("Geo", UtilMisc.toMap("geoId", countryGeoId), false);
				if (UtilValidate.isNotEmpty(geo)) {
					String geoCode = geo.getString("geoCode");
					if (UtilValidate.isNotEmpty(geoCode)) {
						GenericValue countryTeleCode = delegator.findOne("CountryTeleCode", UtilMisc.toMap("countryCode", geoCode), false);
						if (UtilValidate.isNotEmpty(countryTeleCode)) {
							teleCode = countryTeleCode.getString("teleCode");
						}
					}
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return teleCode;
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
			TransactionUtil.begin();
			GenericValue emailAddress = EntityQuery.use(delegator).from(dynamicView).where(condition).filterByDate().queryFirst();
			if(UtilValidate.isNotEmpty(emailAddress))
				emailId = emailAddress.getString("infoString");
			TransactionUtil.commit();
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return emailId;
	}
	
	public static boolean hasPermissionWoFullPerm(HttpServletRequest request, String permissionId) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
		HttpSession session = request.getSession();
		boolean hasPermission = false;
		try {
			List<String> groupIds = UtilValidate.isNotEmpty(session.getAttribute("userLoginSecurityGroups")) ? (List<String>) session.getAttribute("userLoginSecurityGroups") : UtilValidate.isNotEmpty(request.getAttribute("userLoginSecurityGroups")) ? (List<String>) request.getAttribute("userLoginSecurityGroups") : new LinkedList<>();
			if(UtilValidate.isEmpty(groupIds)) {
				String userLoginPartyId = userLogin.getString("partyId");
	        	Map<String, Object> result = org.fio.homeapps.util.DataHelper.getUserRoleGroup(delegator, userLoginPartyId);
	        	if(UtilValidate.isNotEmpty(result)) {
	        		groupIds =  (List<String>) result.get("userLoginSecurityGroups");
	        		request.setAttribute("userLoginSecurityGroups", result.get("userLoginSecurityGroups"));
	        		request.setAttribute("userLoginRoles", result.get("roles"));
	                session.setAttribute("userLoginSecurityGroups", result.get("userLoginSecurityGroups"));
	                session.setAttribute("userLoginRoles", result.get("roles"));
	        	}
			}
			
			if(UtilValidate.isNotEmpty(groupIds) && UtilValidate.isNotEmpty(permissionId)) {
				EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
						EntityCondition.makeCondition("groupId",EntityOperator.IN,groupIds),
						EntityCondition.makeCondition("permissionId",EntityOperator.EQUALS,permissionId));
				GenericValue userLoginSecurityGroupPermission = EntityQuery.use(delegator).from("SecurityGroupPermission").where(condition).queryFirst();
				if(UtilValidate.isNotEmpty(userLoginSecurityGroupPermission)) {
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return hasPermission;
		
	}
	
	public static Map<String, Object> getPrimaryContactInfo(Delegator delegator, String partyIdTo, String roleTypeIdTo){
		
		Map<String, Object> data = new LinkedHashMap<String, Object>();
		
		try {
			EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
					EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, partyIdTo),
					EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "CONTACT"),
					EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, roleTypeIdTo),
					EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PARTY_DEFAULT"),
					EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "CONTACT_REL_INV"),
					EntityUtil.getFilterByDateExpr()
					);
			
			GenericValue assocContact = EntityQuery.use(delegator).select("partyIdFrom").from("PartyRelationship").where(condition).cache(false).queryFirst();	
			if (UtilValidate.isNotEmpty(assocContact)) {
				String primaryContactId = (String) assocContact.get("partyIdFrom");
				data.put("contactId", primaryContactId);
				data.put("contactName", PartyHelper.getPersonName(delegator, primaryContactId, false));
				data.put("partyIdFrom", primaryContactId);
				data.put("primaryContactEmail", PartyHelper.getEmailAddress(delegator, primaryContactId, "PRIMARY_EMAIL"));
				data.put("primaryContactPhone", PartyHelper.getContactNumber(delegator, primaryContactId, "PRIMARY_PHONE"));
				
			}
		}catch (Exception e1) {
			e1.printStackTrace();
		}
		return data;
	}
	
	public static String getSystemPropertyValue(Delegator delegator, String systemResourceId, String systemPropertyId) {
		return getSystemPropertyValue(delegator, systemResourceId, systemPropertyId, null);
	}

	public static String getSystemPropertyValue(Delegator delegator, String systemResourceId, String systemPropertyId, String defaultValue) {
		try {
			if (UtilValidate.isNotEmpty(systemResourceId) && UtilValidate.isNotEmpty(systemPropertyId)) {
				EntityCondition mainConditons = EntityCondition.makeCondition(UtilMisc.toList(
						EntityCondition.makeCondition("systemResourceId",systemResourceId),
						EntityCondition.makeCondition("systemPropertyId",systemPropertyId)), 
						EntityOperator.AND);
				GenericValue systemProperty = EntityQuery.use(delegator).from("SystemProperty").select("systemPropertyValue").where(mainConditons).queryFirst();
				if (UtilValidate.isNotEmpty(systemProperty) && UtilValidate.isNotEmpty(systemProperty.getString("systemPropertyValue"))) 
					return systemProperty.getString("systemPropertyValue");
			}
		} catch (Exception e) {
			Debug.logError(e.getMessage(), MODULE);
		}
		return defaultValue;
	}
	
    public static String combineValueKey(String str1, String str2) {
	String value = "";
	try {
	    value = UtilValidate.isNotEmpty(str1) ? str1 + (UtilValidate.isNotEmpty(str2) ? " ("+str2+")" :"") : UtilValidate.isNotEmpty(str2) ? str2 :"";
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return value;
    }
    
    public static String getDefaultCurrencySymbol(Delegator delegator,String defaultCurrencyUom) {
    	String symbol = "\\$";
    	Locale local = Locale.getDefault();
    	try {
    		if (UtilValidate.isEmpty(defaultCurrencyUom)) {
    			defaultCurrencyUom = getGlobalValue(delegator, "DEFAULT_CURRENCY_UOM", "USA");
    		}
    		com.ibm.icu.text.NumberFormat nf = com.ibm.icu.text.NumberFormat.getCurrencyInstance(local);
    		if (UtilValidate.isNotEmpty(nf)) {
    			nf.setCurrency(com.ibm.icu.util.Currency.getInstance(defaultCurrencyUom));
    			symbol = nf.getCurrency().getSymbol(local);
    		}
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return symbol;
    }
	public static String isPhoneCampaignEnabled(Delegator delegator) {
		return getGlobalValue(delegator,"IS_PHONE_CAMPAIGN_ENABLED","N");
	}
	public static String preparePhoneNumber(Delegator delegator, String value) {
		try {
			if (UtilValidate.isNotEmpty(value)) {
				String phoneNumberFormat = (String) CacheUtil.getInstance().get("PHONE_NUMBER_FORMAT");
				if (UtilValidate.isEmpty(phoneNumberFormat)) {
					phoneNumberFormat = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "PHONE_NUMBER_FORMAT", "(\\d{3})(\\d{3})(\\d+)|($1)-$2-$3");
					CacheUtil.getInstance().put("PHONE_NUMBER_FORMAT", phoneNumberFormat);
				}
				
				String regex = phoneNumberFormat.substring(0, phoneNumberFormat.indexOf("|"));
				String replacement = phoneNumberFormat.substring(phoneNumberFormat.indexOf("|")+1, phoneNumberFormat.length());
				
				value  = value.replaceFirst(regex, replacement);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}
	public static Map<String,Object> getDashboardDetails (Delegator delegator,String dashboardInstanceId, String checkAppBarId) {
		Map <String,Object> result = new HashMap<>();
		try {
			if(UtilValidate.isNotEmpty(checkAppBarId) && checkAppBarId.equals("Y")) {
				String isAppBarId = "N";
				String isBoldBiDashboard = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "IS_DASHBOARD_ENABLED","N");
				result.put("isBoldBiDashboard",isBoldBiDashboard);
				GenericValue biDashboard = EntityQuery.use(delegator).from("BiDashboard").where("dashboardInstanceId",dashboardInstanceId).queryOne();
				if(UtilValidate.isNotEmpty(biDashboard)) {
					isAppBarId = "Y";
				}
				result.put("isAppBarId",isAppBarId);
				return result;
			}
			String dashboardId = "";
			String dashboardTypeId = "";
			String isEnabled = "";
			String defaultMessage = "";
			int maxHeight = 500;
			int maxWidth = 100;
			String rootUrl ="";
			String siteIdentifier ="";
			String environment ="";
			String embedType ="";
			String getDashboardsUrl ="";
			String authorizationServerUrl = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "AUTHORIZATION_SERVER_URL");
			String dashboardDetails = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "DASHBOARD_DETAILS");
			if (UtilValidate.isNotEmpty(dashboardDetails)) {
					String[] detailsArray = dashboardDetails.split(",");
					if (detailsArray.length > 0) rootUrl = detailsArray[0];
					if (detailsArray.length > 1) siteIdentifier = detailsArray[1];
					if (detailsArray.length > 2) environment = detailsArray[2];
					if (detailsArray.length > 3) embedType = detailsArray[3];
					if (detailsArray.length > 4) getDashboardsUrl = detailsArray[4];
			}
			if(UtilValidate.isNotEmpty(dashboardInstanceId)) {
				GenericValue biDashboard = EntityQuery.use(delegator).from("BiDashboard").where("dashboardInstanceId",dashboardInstanceId).queryOne();
				if(UtilValidate.isNotEmpty(biDashboard)) {
					if(UtilValidate.isNotEmpty(biDashboard.getString("dashboardId"))) {
						dashboardId = biDashboard.getString("dashboardId");
					}
					dashboardId = biDashboard.getString("dashboardId");
					isEnabled = biDashboard.getString("isEnabled");
					defaultMessage = biDashboard.getString("defaultMessage");
					maxHeight = biDashboard.getInteger("maxHeight");
					maxWidth = biDashboard.getInteger("maxWidth");
					dashboardTypeId = biDashboard.getString("dashboardTypeId");
					maxWidth = UtilValidate.isNotEmpty(biDashboard.getInteger("maxWidth")) ? biDashboard.getInteger("maxWidth"): 100;  // Default to 100 if null
					maxHeight = UtilValidate.isNotEmpty(biDashboard.getInteger("maxHeight")) ? biDashboard.getInteger("maxHeight"): 500;  // Default to 800 if null
				}else {
					result.put("error","error");
					result.put("errorMessage","No Dashboard instance id");
				}
			}
			result.put("rootUrl",rootUrl);
			result.put("siteIdentifier",siteIdentifier);
			result.put("environment",environment);
			result.put("embedType",embedType);
			result.put("getDashboardsUrl",getDashboardsUrl);
			result.put("authorizationServerUrl",UtilValidate.isNotEmpty(authorizationServerUrl)?authorizationServerUrl:"");
			result.put("dashboardId",UtilValidate.isNotEmpty(dashboardId) ? dashboardId: "");
			result.put("dashboardTypeId",UtilValidate.isNotEmpty(dashboardTypeId) ? dashboardTypeId : "");
			result.put("isEnabled",UtilValidate.isNotEmpty(isEnabled) ? isEnabled: "");
			result.put("maxWidth",UtilValidate.isNotEmpty(maxWidth) ? maxWidth: 100);
			result.put("maxHeight",UtilValidate.isNotEmpty(maxHeight) ? maxHeight: 500);
			result.put("defaultMessage",UtilValidate.isNotEmpty(defaultMessage) ? defaultMessage: "Please contact your administrator");
		} catch (Exception e) {
			Debug.logError(e, MODULE);
		}
		return result;
	}
	public static  List<String> getDashboardInstanceId(Delegator delegator, String requestUri) {
		List<String> dashboardInstanceIdList = new ArrayList<>();
		try {
			String componentName="";
			String componentId="";
			if(UtilValidate.isNotEmpty(requestUri)) {
				String[] parts = requestUri.split("/");
				if (parts.length > 1) {
					componentName = parts[1];
				}
				if(UtilValidate.isNotEmpty(componentName)) {
					List<GenericValue> componentGV = EntityQuery.use(delegator).from("OfbizComponentAccess").select("componentId","componentName","uiLabels","description").where("componentName", componentName).queryList();
					if (UtilValidate.isNotEmpty(componentGV)) {
						for(GenericValue component : componentGV) {
							componentId = component.getString("componentId");
						}
					}
				}
				List<GenericValue> biDashboardGV = EntityQuery.use(delegator).from("BiDashboard").where("dashboardTypeId", "EMBEDDED","isEnabled","Y").queryList();
				if(UtilValidate.isNotEmpty(componentId)) {
					biDashboardGV = EntityQuery.use(delegator).from("BiDashboard").where("dashboardTypeId", "EMBEDDED","isEnabled","Y","componentId",componentId).queryList();
				}
				if (UtilValidate.isNotEmpty(biDashboardGV)) {
					for (GenericValue biDashboard : biDashboardGV) {
						String dashboardId = biDashboard.getString("dashboardId");
						String dashboardInstanceId = biDashboard.getString("dashboardInstanceId");
						int maxWidth = biDashboard.getInteger("maxWidth");
						int maxHeight = biDashboard.getInteger("maxHeight");
						if (UtilValidate.isNotEmpty(dashboardId)) {
							dashboardInstanceIdList.add(dashboardInstanceId +","+dashboardId + ","+ maxWidth + ","+ maxHeight);
						}
					}
				}else {
					if(UtilValidate.isNotEmpty(componentId)) {
						biDashboardGV = EntityQuery.use(delegator).from("BiDashboard").where("dashboardTypeId", "EMBEDDED","componentId",componentId).queryList();
						if(UtilValidate.isEmpty(biDashboardGV)) {
							dashboardInstanceIdList.add("error" + "," + "No Dashboard Instance Id for this component with embedded type");
						}
					}else {
						dashboardInstanceIdList.add("error" + "," + "Dashboard Instance Id not enabled");
					}
				}
			}else {
				dashboardInstanceIdList.add("error" + "," + "Request Uri is empty");
			}
		} catch (Exception e) {
			e.getMessage();
			String errorMessage = e.getMessage();
			Debug.log("errorMessage"+errorMessage);
			dashboardInstanceIdList.add("error" + "," + errorMessage);
		}
		return dashboardInstanceIdList;
	}
	public static String generateSecurePassword(int length) {
		if (length <= 0) {
			throw new IllegalArgumentException("Length must be greater than 0");
		}
		byte[] randomBytes = new byte[length];
		SecureRandom secureRandom = new SecureRandom();
		secureRandom.nextBytes(randomBytes);
		return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes).substring(0, length);
	}

}
