/**
 * 
 */
package org.groupfio.common.portal.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.fio.homeapps.util.PartyHelper;
import org.groupfio.common.portal.CommonPortalConstants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.cache.UtilCache;
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
 * @author Sharif
 *
 */
public class DataUtil {

    private static String MODULE = DataUtil.class.getName();

    public static List<GenericValue> getLatestVersionHeaderConfigs (Delegator delegator, String hdrFileType) {

	List<GenericValue> headerConfigs = new ArrayList<GenericValue>();

	try {

	    List conditionsList = FastList.newInstance();

	    EntityFindOptions efo = new EntityFindOptions();
	    efo.setDistinct(true);

	    if (UtilValidate.isNotEmpty(hdrFileType)) {
		conditionsList.add(EntityCondition.makeCondition("hdrFileType", EntityOperator.EQUALS, hdrFileType));
	    }

	    EntityCondition mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);

	    List<GenericValue> hdrIdList = delegator.findList("HadoopHdrMaster", mainConditons, UtilMisc.toSet("hdrId"), UtilMisc.toList("hdrRmSeqNum"), efo, false);

	    if (UtilValidate.isNotEmpty(hdrIdList)) {
		String hdrId = hdrIdList.get(0).getString("hdrId");

		headerConfigs = delegator.findByAnd("HadoopHdrMaster", UtilMisc.toMap("hdrId", hdrId, "hdrFileType", hdrFileType), null, false);

	    }

	} catch (Exception e) {
	    Debug.logError(e, MODULE);
	}

	return headerConfigs;

    }

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

    public static <T> List<T> getFieldListFromMapList(List<Map<String, Object>> genericValueList, String fieldName, boolean distinct) {
	if (genericValueList == null || fieldName == null) {
	    return null;
	}
	List<T> fieldList = new LinkedList<T>();
	Set<T> distinctSet = null;
	if (distinct) {
	    distinctSet = new HashSet<T>();
	}

	for (Map<String, Object> value: genericValueList) {
	    T fieldValue = UtilGenerics.<T>cast(value.get(fieldName));
	    if (fieldValue != null) {
		if (distinct) {
		    if (!distinctSet.contains(fieldValue)) {
			fieldList.add(fieldValue);
			distinctSet.add(fieldValue);
		    }
		} else {
		    fieldList.add(fieldValue);
		}
	    }
	}

	return fieldList;
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
    public static boolean isContactPhoneChange(Delegator delegator, String contactId, String partyRelAssocId, String phoneNumber, String contactMechPurposeTypeId) {

	try {
	    if (UtilValidate.isNotEmpty(phoneNumber)) {
		GenericValue mobilePurpose = EntityQuery.use(delegator).select("contactMechId").from("PartyContactMechPurpose").where("partyId", contactId, "contactMechPurposeTypeId", contactMechPurposeTypeId, "partyRelAssocId", partyRelAssocId).queryFirst();
		if(UtilValidate.isNotEmpty(mobilePurpose)) {
		    String contactMechId = mobilePurpose.getString("contactMechId");

		    GenericValue phoneContactMech = EntityQuery.use(delegator).select("contactNumber").from("TelecomNumber").where("contactMechId", contactMechId).queryFirst();
		    if (UtilValidate.isNotEmpty(phoneContactMech.getString("contactNumber")) && !phoneNumber.equals(phoneContactMech.getString("contactNumber"))) {
			return true;
		    }
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}

	return false;
    }
    /**
     * Gets the login role. for DBS_TL or DBS_RM
     *
     * @param delegator the delegator
     * @param loginId the login id
     * @return the login role
     */
    public static List<String> getLoginRole(Delegator delegator, String loginPartyId, String teamId) {
	List<String> roles = new ArrayList<String>();
	Map<String,Object> teamMember = VirtualTeamUtil.getFirstVirtualTeamMember(delegator, teamId, loginPartyId);
	String securityGroup = (String)teamMember.get("securityGroupId");
	roles.add(securityGroup);
	return roles;
    }

    // Arshiya code [start]

    public static String getBusinessUnitName(Delegator delegator, String productStoreGroupId) {

	String buName = null;
	try {
	    GenericValue producStoreGroup = EntityQuery.use(delegator).from("ProductStoreGroup").where("productStoreGroupId",productStoreGroupId).cache(true).queryOne();
	    if(UtilValidate.isNotEmpty(producStoreGroup)){
		buName = producStoreGroup.getString("productStoreGroupName");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    Debug.logError(e.getMessage(), MODULE);
	}

	return buName;
    }

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
			if (returnType.equalsIgnoreCase(CommonPortalConstants.DateTimeTypeConstant.SQL_DATE)) {
			    return (T) new java.sql.Date(date.getTime());
			} else if (returnType.equalsIgnoreCase(CommonPortalConstants.DateTimeTypeConstant.TIMESTAMP)) {
			    String stamp = sdf.format(date.getTime());
			    return (T) Timestamp.valueOf(stamp);
			} else if (returnType.equalsIgnoreCase(CommonPortalConstants.DateTimeTypeConstant.UTIL_DATE)) {
			    return (T) new Date(date.getTime());
			} else if (returnType.equalsIgnoreCase(CommonPortalConstants.DateTimeTypeConstant.STRING)) {
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

    public static int getOnceDoneCustReq (Delegator delegator, String ownerBu) {
	int onceDoneCnt = 0;
	try {
	    List conditionsList = FastList.newInstance();
	    EntityFindOptions efo = new EntityFindOptions();
	    efo.setDistinct(true);

	    if (UtilValidate.isNotEmpty(ownerBu)) {
		conditionsList.add(EntityCondition.makeCondition("ownerBu", EntityOperator.EQUALS, ownerBu));
	    }
	    conditionsList.add(EntityCondition.makeCondition("custRequestTypeId", EntityOperator.NOT_EQUAL,CommonPortalConstants.SR_COMPLAINT));
	    conditionsList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS,"SR_CLOSED"));
	    EntityCondition mainConditons = EntityCondition.makeCondition(conditionsList, EntityOperator.AND);

	    List<GenericValue> onceDoneList = EntityQuery.use(delegator).select("custReqOnceDone").from("CustRequest").where(mainConditons).queryList();

	    if (UtilValidate.isNotEmpty(onceDoneList)) {
		for(GenericValue onceDoneGv : onceDoneList) {
		    String onceDone = onceDoneGv.getString("custReqOnceDone");
		    if(UtilValidate.isNotEmpty(onceDone)) {
			onceDoneCnt = onceDoneCnt + Integer.parseInt(onceDone);
		    }
		}
	    }

	} catch (Exception e) {
	    Debug.logError(e, MODULE);
	}

	return onceDoneCnt;

    }

    public static List < SimpleDateFormat > getDateFormats(String formatType) {
	List < SimpleDateFormat > dateFormats = null;
	try {
	    if (CommonPortalConstants.DateTimeTypeConstant.DATE.equalsIgnoreCase(formatType)) {
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
	    } else if (CommonPortalConstants.DateTimeTypeConstant.TIMESTAMP.equalsIgnoreCase(formatType)) {
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

    public static long getSrCountbyStatus(Delegator delegator, String statusId, List<GenericValue> custRequests , String type) {
	long srCount = 0;
	Timestamp systemTime = UtilDateTime.nowTimestamp();
	try {
	    if(UtilValidate.isNotEmpty(statusId) && UtilValidate.isNotEmpty(custRequests)) {

		if(UtilValidate.isNotEmpty(custRequests)) {
		    for(GenericValue custRequest : custRequests) {
			String custRequestId = custRequest.getString("custRequestId");
			Timestamp dueDate = null;
			Timestamp srClosedDate = null;
			GenericValue custRequestSuppl = EntityQuery.use(delegator).from("CustRequestSupplementory").select("commitDate").where("custRequestId", custRequestId).queryOne();
			if(UtilValidate.isNotEmpty(custRequestSuppl)) {
			    dueDate = custRequestSuppl.getTimestamp("commitDate");
			}
			srClosedDate = custRequest.getTimestamp("closedByDate");
			if("SR_OPEN".equals(statusId)) {
			    if(UtilValidate.isNotEmpty(dueDate) && (systemTime.before(dueDate) || systemTime.equals(dueDate))) 
				srCount = srCount + 1;
			}else if(("SR_CLOSED".equals(statusId) || "SR_CANCELLED".equals(statusId) ) && "BEYOND_SLA".equals(type)) {
			    if(UtilValidate.isNotEmpty(srClosedDate) && UtilValidate.isNotEmpty(dueDate) ) {
				if(dueDate.before(srClosedDate))
				    srCount = srCount + 1;
			    }
			}else if(("SR_CLOSED".equals(statusId) || "SR_CANCELLED".equals(statusId) ) && "WITH_IN_SLA".equals(type)) {
			    if(UtilValidate.isNotEmpty(srClosedDate) && UtilValidate.isNotEmpty(dueDate) ) {
				if(srClosedDate.before(dueDate))
				    srCount = srCount + 1;
			    }
			}
		    }
		}
	    }
	}catch(Exception e) {
	    e.printStackTrace();
	}
	return srCount;
    }

    public static String getCifNumber(Delegator delegator, String partyId , String custRequestId) {
	// TODO Auto-generated method stub
	if ("99999".equals(partyId)) {
	    partyId = getPartyIdentificationValue(delegator, "", "CIF_REFERENCE", custRequestId);
	    return partyId;
	} else if (UtilValidate.isNotEmpty(partyId)) {
	    GenericValue partyIdentification;
	    try {
		partyIdentification = delegator.findOne("PartyIdentification", UtilMisc.toMap("partyId", partyId, "partyIdentificationTypeId", "CIF"), true);
		if (UtilValidate.isNotEmpty(partyIdentification)) {
		    return  partyIdentification.getString("idValue");
		}
	    } catch (GenericEntityException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }

	}
	return null;
    }

    public static String getPartyIdentificationValue(Delegator delegator, String partyId, String partyIdentificationTypeId,
	    String custRequestId) {
	String idValue = null;
	try {
	    EntityCondition mainCondition = EntityCondition.makeCondition(EntityOperator.AND,
		    EntityCondition.makeCondition("attrName", EntityOperator.EQUALS, partyIdentificationTypeId),
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

    public static GenericValue findPartyLatestContactMech(String partyId, String contactMechTypeId, Delegator delegator) {
	try {
	    return EntityQuery.use(delegator).from("PartyAndContactMech")
		    .where("partyId", partyId, "contactMechTypeId", contactMechTypeId)
		    .orderBy("-fromDate")
		    .filterByDate()
		    .queryFirst();
	} catch (GenericEntityException e) {
	    Debug.logError(e, "Error while finding latest ContactMech for party with ID [" + partyId + "] TYPE [" + contactMechTypeId + "]: " + e.toString(), MODULE);
	    return null;
	}
    }

    // Arshiya code [end]

    public static String getEmplTeamId(Delegator delegator, String partyId) {
	try {
	    GenericValue emplTeam = EntityQuery.use(delegator).select(UtilMisc.toSet("emplTeamId","partyId","businessUnit")).from("EmplTeam").where("partyId", partyId).queryFirst();
	    if (UtilValidate.isNotEmpty(emplTeam)) {    
		return emplTeam.getString("emplTeamId");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    Debug.logError(e.getMessage(), MODULE);
	}

	return null;
    }

	public static Map<String, Object> getPrimaryContact(Delegator delegator, String partyId, String partyRoleTypeId) {
		Map<String, Object> data = new LinkedHashMap<String, Object>();

		try {
			if (UtilValidate.isNotEmpty(partyId) && UtilValidate.isNotEmpty(partyRoleTypeId)) {
				List conditions = FastList.newInstance();

				conditions.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, partyId));
				EntityCondition roleTypeCondition = EntityCondition.makeCondition(UtilMisc.toList(
						EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "CONTACT"),
						EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS,
								"CONTACT_REL_INV"),
						EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, partyRoleTypeId)));
				conditions.add(roleTypeCondition);
				conditions.add(EntityUtil.getFilterByDateExpr());

				EntityCondition mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);
				
				List<GenericValue> assocContactList = EntityQuery.use(delegator).select("partyIdFrom", "roleTypeIdFrom", "statusId").from("PartyRelationship").where(mainConditons).queryList();
				if (UtilValidate.isNotEmpty(assocContactList)) {
					GenericValue primaryContact = null;
					for (GenericValue assocContact : assocContactList) {
						String contactId = assocContact.getString("partyIdFrom");
						String relationshipStatusId = assocContact.getString("statusId");
						if (UtilValidate.isNotEmpty(relationshipStatusId)
								&& relationshipStatusId.equals("PARTY_DEFAULT")) {
							primaryContact = assocContact;
							break;
						}
					}
					if (UtilValidate.isEmpty(primaryContact)) {
						primaryContact = assocContactList.get(0);
					}

					if (UtilValidate.isNotEmpty(primaryContact)) {
						GenericValue party = EntityQuery.use(delegator).from("Party").where("partyId", primaryContact.getString("partyIdFrom")).queryFirst();
						
						data.put("contactId", primaryContact.getString("partyIdFrom"));
						data.put("contactName",
								PartyHelper.getPersonName(delegator, primaryContact.getString("partyIdFrom"), false));
						data.put("partyIdFrom", primaryContact.getString("partyIdFrom"));
						data.put("roleTypeIdFrom", primaryContact.getString("roleTypeIdFrom"));
						
						data.put("timeZoneDesc", party.getString("timeZoneDesc"));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e.getMessage(), MODULE);
		}

		return data;
	}

    public static void relatedPartyContactAssociation(Delegator delegator, String partyId, String contactPartyId, String contactType, String contactMechPurposeTypeId, String partyRelAssocId) {

	try {
	    if (UtilValidate.isNotEmpty(partyId)) {

		EntityCondition condition1 = EntityCondition.makeCondition("partyRelAssocId", EntityOperator.EQUALS, partyRelAssocId);
		EntityCondition condition2 = EntityCondition.makeCondition(UtilMisc.toList(
			EntityCondition.makeCondition("assocId", EntityOperator.NOT_EQUAL, null),
			EntityCondition.makeCondition("assocId", EntityOperator.NOT_EQUAL, "")), EntityOperator.OR);

		EntityCondition conditon = EntityCondition.makeCondition(UtilMisc.toList(condition1, condition2, EntityCondition.makeCondition("assocTypeId", EntityOperator.EQUALS, contactType)));
		List<GenericValue> relAssocList = EntityQuery.use(delegator).from("PartyRelationshipAssoc").where(conditon).queryList();

		if (UtilValidate.isEmpty(relAssocList)) {
		    EntityCondition mainCondition = EntityCondition.makeCondition(UtilMisc.toList(
			    EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, contactPartyId),
			    EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, contactMechPurposeTypeId),
			    EntityUtil.getFilterByDateExpr()
			    ), 
			    EntityOperator.AND);

		    GenericValue pcmp = EntityQuery.use(delegator).select("contactMechId").from("PartyContactMechPurpose").where(mainCondition).queryFirst();
		    if (UtilValidate.isNotEmpty(pcmp)) {
			String contactMechId = pcmp.getString("contactMechId");

			String assocSeqId = "0";
			List<GenericValue> partyRelationshipAssocList = delegator.findList("PartyRelationshipAssoc", EntityCondition.makeCondition("partyRelAssocId",EntityOperator.EQUALS,partyRelAssocId), UtilMisc.toSet("assocSeqId"), UtilMisc.toList("assocSeqId DESC"), null, false);
			if(partyRelationshipAssocList != null && partyRelationshipAssocList.size() >0) {
			    GenericValue partyRelationshipAssoc = partyRelationshipAssocList.get(0);
			    assocSeqId = partyRelationshipAssoc.getString("assocSeqId");
			}

			int seqId = Integer.parseInt(assocSeqId)+1;
			String assSeqId = StringUtils.leftPad(""+seqId, 4, "0");
			GenericValue partyRelAssoc = delegator.makeValue("PartyRelationshipAssoc");
			partyRelAssoc.put("partyRelAssocId", partyRelAssocId);
			partyRelAssoc.put("assocSeqId", Long.valueOf(assSeqId));
			partyRelAssoc.put("assocTypeId", contactType);
			partyRelAssoc.put("assocId", contactMechId);
			partyRelAssoc.put("solicitationStatus", "Y");
			partyRelAssoc.create();

		    }
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    Debug.logError(e.getMessage(), MODULE);
	}

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

    public static String getSrNumber(Delegator delegator, String custRequestId) {
	try {
	    if (UtilValidate.isNotEmpty(custRequestId)) {
		List conditions = FastList.newInstance();

		conditions.add(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId));

		EntityCondition mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);
		GenericValue entry = EntityQuery.use(delegator).select("externalId").from("CustRequest").where(mainConditons).queryFirst();
		if (UtilValidate.isNotEmpty(entry)) {
		    return UtilValidate.isNotEmpty(entry.getString("externalId")) ? entry.getString("externalId") : custRequestId;
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    Debug.logError(e.getMessage(), MODULE);
	}
	return null;
    }

    public static String getCustomFieldMultiValueDes(Delegator delegator, String customFieldId, String multiValueId) {
	String value = null;
	try {
	    if (UtilValidate.isNotEmpty(customFieldId) && UtilValidate.isNotEmpty(multiValueId)) {
		EntityCondition mainCondition = EntityCondition.makeCondition(EntityOperator.AND,
			EntityCondition.makeCondition("customFieldId", EntityOperator.EQUALS, customFieldId),
			EntityCondition.makeCondition("multiValueId", EntityOperator.EQUALS, multiValueId)
			);

		GenericValue entry = EntityQuery.use(delegator).select("description").from("CustomFieldMultiValue").where(mainCondition).cache().queryFirst();
		if(UtilValidate.isNotEmpty(entry)){
		    value = entry.getString("description");
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    Debug.logError(e.getMessage(), MODULE);
	}

	return value;
    }
    public static String getCustomFieldMultiValueFieldValue(Delegator delegator, String customFieldId, String multiValueId) {
	String value = null;
	try {
	    if (UtilValidate.isNotEmpty(customFieldId) && UtilValidate.isNotEmpty(multiValueId)) {
		EntityCondition mainCondition = EntityCondition.makeCondition(EntityOperator.AND,
			EntityCondition.makeCondition("customFieldId", EntityOperator.EQUALS, customFieldId),
			EntityCondition.makeCondition("multiValueId", EntityOperator.EQUALS, multiValueId)
			);

		GenericValue entry = EntityQuery.use(delegator).select("fieldValue").from("CustomFieldMultiValue").where(mainCondition).queryFirst();
		if(UtilValidate.isNotEmpty(entry)){
		    value = entry.getString("fieldValue");
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    Debug.logError(e.getMessage(), MODULE);
	}

	return value;
    }
    public static String getCustomFieldMultiValueId(Delegator delegator, String customFieldId, String fieldValue) {
	String value = null;
	try {
	    if (UtilValidate.isNotEmpty(customFieldId) && UtilValidate.isNotEmpty(fieldValue)) {
		EntityCondition mainCondition = EntityCondition.makeCondition(EntityOperator.AND,
			EntityCondition.makeCondition("customFieldId", EntityOperator.EQUALS, customFieldId),
			EntityCondition.makeCondition("fieldValue", EntityOperator.EQUALS, fieldValue)
			);

		GenericValue entry = EntityQuery.use(delegator).select("multiValueId").from("CustomFieldMultiValue").where(mainCondition).queryFirst();
		if(UtilValidate.isNotEmpty(entry)){
		    value = entry.getString("multiValueId");
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    Debug.logError(e.getMessage(), MODULE);
	}

	return value;
    }
    public static String getCustomFieldMultiValueDescription(Delegator delegator, String customFieldId, String fieldValue) {
	String value = null;
	try {
	    if (UtilValidate.isNotEmpty(customFieldId) && UtilValidate.isNotEmpty(fieldValue)) {
		EntityCondition mainCondition = EntityCondition.makeCondition(EntityOperator.AND,
			EntityCondition.makeCondition("customFieldId", EntityOperator.EQUALS, customFieldId),
			EntityCondition.makeCondition("fieldValue", EntityOperator.EQUALS, fieldValue)
			);

		GenericValue entry = EntityQuery.use(delegator).select("description").from("CustomFieldMultiValue").where(mainCondition).queryFirst();
		if(UtilValidate.isNotEmpty(entry)){
		    value = entry.getString("description");
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    Debug.logError(e.getMessage(), MODULE);
	}

	return value;
    }

    public static String getProductStoreName(Delegator delegator, String productStoreId) {
	String storeName = null;
	try {
	    if (UtilValidate.isNotEmpty(productStoreId)) {
		EntityCondition mainCondition = EntityCondition.makeCondition(EntityOperator.AND,
			EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, productStoreId)
			);

		GenericValue store = EntityQuery.use(delegator).select("storeName").from("ProductStore").where(mainCondition).cache().queryFirst();
		if(UtilValidate.isNotEmpty(store)){
		    storeName = store.getString("storeName");
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    Debug.logError(e.getMessage(), MODULE);
	}

	return storeName;
    }

    public static String getProductStorePrefix(Delegator delegator, String productStoreId) {
	String storePrefix = null;
	try {
	    if (UtilValidate.isNotEmpty(productStoreId)) {
		EntityCondition mainCondition = EntityCondition.makeCondition(EntityOperator.AND,
			EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, productStoreId)
			);

		GenericValue store = EntityQuery.use(delegator).select("storePrefix").from("ProductStore").where(mainCondition).queryFirst();
		if(UtilValidate.isNotEmpty(store)){
		    storePrefix = store.getString("storePrefix");
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    Debug.logError(e.getMessage(), MODULE);
	}

	return storePrefix;
    }

    public static String getContentAttribute(Delegator delegator, String contentId, String attrName) {
	String attrValue = "";
	try {

	    if(UtilValidate.isNotEmpty(contentId) && UtilValidate.isNotEmpty(attrName)) {
		GenericValue contentAttribute = EntityQuery.use(delegator).from("ContentAttribute").where("contentId", contentId, "attrName", attrName).queryFirst();
		attrValue = UtilValidate.isNotEmpty(contentAttribute) ? contentAttribute.getString("attrValue") : "";
	    }
	    
	} catch (Exception e) {
	    e.printStackTrace();
	    Debug.logError(e.getMessage(), MODULE);
	}

	return attrValue;
    }


    public static Map<String, Object> getPartyRate(Delegator delegator, Map<String, Object> context){
	Map<String, Object>  result = new HashMap<String, Object>();
	try {
	    String partyId = (String) context.get("partyId");
	    String rateTypeId = (String) context.get("rateTypeId");
	    Timestamp timeEntryDate = UtilValidate.isNotEmpty(context.get("timeEntryDate")) ? (Timestamp) context.get("timeEntryDate") : UtilDateTime.nowTimestamp();
	    LocalDateTime monthStart = timeEntryDate.toLocalDateTime().with(TemporalAdjusters.firstDayOfMonth());
	    LocalDateTime monthEnd = timeEntryDate.toLocalDateTime().with(TemporalAdjusters.lastDayOfMonth());

	    int monthInt = monthStart.getMonthValue(); 
	    String month = ""; 
	    if (monthInt < 10) month = "0"+String.valueOf(monthInt); 
	    else month = String.valueOf(monthInt);

	    String timeEntryDateStr = ""+monthStart.getYear()+"-"+month+"-";

	    List<EntityCondition> conditions = new ArrayList<>();
	    conditions.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
	    conditions.add(EntityCondition.makeCondition("rateTypeId", EntityOperator.EQUALS, rateTypeId));
	    conditions.add(EntityCondition.makeCondition("fromDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(Timestamp.valueOf(monthStart))));
	    //conditions.add(EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(Timestamp.valueOf(monthEnd))));
	    EntityCondition condtion = EntityCondition.makeCondition(conditions, EntityOperator.AND);

	    List<GenericValue> partyRateList = EntityQuery.use(delegator).from("PartyRate").where(condtion).queryList();
	    if(UtilValidate.isNotEmpty(partyRateList)) {
		for(GenericValue partyRate : partyRateList) {
		    Timestamp fromDate = partyRate.getTimestamp("fromDate");
		    Timestamp thruDate = partyRate.getTimestamp("thruDate");

		    if(UtilValidate.isNotEmpty(fromDate) && (timeEntryDate.equals(fromDate) || timeEntryDate.after(fromDate)) && (UtilValidate.isNotEmpty(thruDate) && (timeEntryDate.before(thruDate) || timeEntryDate.equals(thruDate)))) {
			result.put("rate", partyRate.getDouble("rate"));
			result.put("fromDate", fromDate);
			result.put("thruDate", thruDate);
			break;
		    } else {
			result.put("rate", partyRate.getDouble("rate"));
			result.put("fromDate", fromDate);
			result.put("thruDate", thruDate);
			break;
		    }
		}
	    } else {
		condtion = EntityCondition.makeCondition(EntityOperator.AND,
			EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, "Company"),
			EntityCondition.makeCondition("rateTypeId", EntityOperator.EQUALS, rateTypeId),
			EntityCondition.makeCondition("defaultRate", EntityOperator.EQUALS,"Y")
			);
		GenericValue defaultRate = EntityQuery.use(delegator).from("PartyRate").where(condtion).filterByDate().queryFirst();
		if(UtilValidate.isNotEmpty(defaultRate)) {
		    result.put("rate", defaultRate.getDouble("rate"));
		    result.put("fromDate", defaultRate.getTimestamp("fromDate"));
		    result.put("thruDate", defaultRate.getTimestamp("thruDate"));
		}

	    }
	    //List<GenericValue> partyRate 
	} catch (Exception e) {
	    Debug.logError(e, e.getMessage(), MODULE);
	}

	return result;
    }

    public static String getRoleTypeDescription(Delegator delegator, String roleTypeId) {
	try {
	    if (UtilValidate.isNotEmpty(roleTypeId)) {
		List conditions = FastList.newInstance();

		conditions.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, roleTypeId));

		EntityCondition mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);
		GenericValue roleType = EntityQuery.use(delegator).select("description").from("RoleType").where(mainConditons).cache().queryFirst();
		if (UtilValidate.isNotEmpty(roleType)) {
		    return roleType.getString("description");
		}
	    }
	} catch (Exception e) {}

	return "";
    }

    public static String getContactMechPurposeTypeDescription(Delegator delegator, String contactMechPurposeTypeId) {
	try {
	    if (UtilValidate.isNotEmpty(contactMechPurposeTypeId)) {
		List conditions = FastList.newInstance();

		conditions.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, contactMechPurposeTypeId));

		EntityCondition mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);
		GenericValue cmpt = EntityQuery.use(delegator).select("description").from("ContactMechPurposeType").where(mainConditons).queryFirst();
		if (UtilValidate.isNotEmpty(cmpt)) {
		    return cmpt.getString("description");
		}
	    }
	} catch (Exception e) {}

	return "";
    }

    public static String getCustRequestAttribute(Delegator delegator, String custRequestId, String attrName) {
		String attrValue = "";
		try {
			GenericValue primaryPersonGv = EntityQuery.use(delegator).select("attrValue").from("CustRequestAttribute").where("custRequestId", custRequestId,"attrName", attrName).cache(true).queryFirst();
			attrValue = UtilValidate.isNotEmpty(primaryPersonGv) && UtilValidate.isNotEmpty(primaryPersonGv.getString("attrValue")) ? primaryPersonGv.getString("attrValue") : "";
		} catch (Exception e) {
			e.printStackTrace();
			Debug.logError(e, e.getMessage(), MODULE);
		}
		return attrValue;
	}

    public static List<Map<String, Object>> getProductCategoryList(Delegator delegator, String prodCatalogId) {
	List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
	try {
	    if (UtilValidate.isNotEmpty(prodCatalogId)) {

		List<EntityCondition> conditionlist = FastList.newInstance();

		conditionlist.add(EntityCondition.makeCondition("prodCatalogId", EntityOperator.EQUALS, prodCatalogId));
		conditionlist.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
		EntityCondition condition = EntityCondition.makeCondition(conditionlist, EntityOperator.AND);
		List<GenericValue> productCatalogList = EntityQuery.use(delegator)
			.select("prodCatalogId", "productCategoryId").from("ProdCatalogCategory").where(condition)
			.orderBy("sequenceNum").queryList();

		if (UtilValidate.isNotEmpty(productCatalogList)) {
		    List<String> productCategoryIds = EntityUtil.getFieldListFromEntityList(productCatalogList,
			    "productCategoryId", true);

		    for (String productCategoryId : productCategoryIds) {
			Map<String, Object> data = new HashMap<String, Object>();

			GenericValue category = EntityQuery.use(delegator).select("categoryName")
				.from("ProductCategory").where("productCategoryId", productCategoryId).queryOne();
			if (UtilValidate.isNotEmpty(category)) {
			    data.put("productCategoryId", productCategoryId);
			    data.put("categoryName", category.getString("categoryName"));

			    dataList.add(data);
			}
		    }

		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    Debug.logError(e, e.getMessage(), MODULE);
	}
	return dataList;
    }

    public static List<Map<String, Object>> getProductSubCategoryList(Delegator delegator, String prodCatalogId, String productCategoryId) {
	List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
	try {
	    if (UtilValidate.isNotEmpty(prodCatalogId) || UtilValidate.isNotEmpty(productCategoryId)) {

		List<EntityCondition> conditionlist = FastList.newInstance();

		if (UtilValidate.isNotEmpty(productCategoryId)) {
		    conditionlist.add(EntityCondition.makeCondition("primaryParentCategoryId", EntityOperator.IN, Arrays.asList(productCategoryId.split(","))));

		    EntityCondition condition = EntityCondition.makeCondition(conditionlist, EntityOperator.AND);
		    List<GenericValue> subCategoryList = EntityQuery.use(delegator)
			    .select("productCategoryId", "categoryName").from("ProductCategory").where(condition).queryList();
		    for (GenericValue each : subCategoryList) {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("productCategoryId", each.getString("productCategoryId"));
			data.put("categoryName", each.getString("categoryName"));

			dataList.add(data);
		    }

		} else {
		    conditionlist.add(EntityCondition.makeCondition("prodCatalogId", EntityOperator.EQUALS, prodCatalogId));
		    conditionlist.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
		    EntityCondition condition = EntityCondition.makeCondition(conditionlist, EntityOperator.AND);
		    List<GenericValue> productCatalogList = EntityQuery.use(delegator)
			    .select("prodCatalogId", "productCategoryId").from("ProdCatalogCategory").where(condition)
			    .orderBy("sequenceNum").queryList();

		    if (UtilValidate.isNotEmpty(productCatalogList)) {
			List<String> productCategoryIds = EntityUtil.getFieldListFromEntityList(productCatalogList,
				"productCategoryId", true);

			for (String productCategId : productCategoryIds) {
			    List<GenericValue> subCategoryList = EntityQuery.use(delegator)
				    .select("productCategoryId", "categoryName").from("ProductCategory")
				    .where("primaryParentCategoryId", productCategId).queryList();
			    if (UtilValidate.isNotEmpty(subCategoryList)) {
				for (GenericValue each : subCategoryList) {
				    Map<String, Object> data = new HashMap<String, Object>();
				    data.put("productCategoryId", each.getString("productCategoryId"));
				    data.put("categoryName", each.getString("categoryName"));

				    dataList.add(data);
				}
			    }
			}
		    }
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    Debug.logError(e, e.getMessage(), MODULE);
	}
	return dataList;
    }

    public static String getPartyRelIdTo(Delegator delegator, Map<String, Object> context) {

	String partyIdFrom = (String) context.get("partyIdFrom");
	//String partyIdTo = (String) context.get("partyIdTo");
	String roleTypeIdFrom = (String) context.get("roleTypeIdFrom");
	String roleTypeIdTo = (String) context.get("roleTypeIdTo");
	String partyRelationshipTypeId = (String) context.get("partyRelationshipTypeId");

	try {
	    if (UtilValidate.isNotEmpty(partyIdFrom) && UtilValidate.isNotEmpty(roleTypeIdFrom) && UtilValidate.isNotEmpty(roleTypeIdTo) && UtilValidate.isNotEmpty(partyRelationshipTypeId)) {
		EntityCondition searchConditions = EntityCondition.makeCondition(EntityOperator.AND,
			EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, partyIdFrom),
			//EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, partyIdTo),
			EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, roleTypeIdFrom),
			EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, roleTypeIdTo),
			EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, partyRelationshipTypeId),
			EntityUtil.getFilterByDateExpr());

		GenericValue existingRelationship = EntityQuery.use(delegator).select("partyIdTo").from("PartyRelationship").where(searchConditions).queryFirst();
		if (UtilValidate.isNotEmpty(existingRelationship)) {
		    return existingRelationship.getString("partyIdTo");
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}

	return null;
    }

    public static List<String> getPartyRelIdToList(Delegator delegator, Map<String, Object> context) {
	String partyIdFrom = (String) context.get("partyIdFrom");
	//String partyIdTo = (String) context.get("partyIdTo");
	String roleTypeIdFrom = (String) context.get("roleTypeIdFrom");
	String roleTypeIdTo = (String) context.get("roleTypeIdTo");
	String partyRelationshipTypeId = (String) context.get("partyRelationshipTypeId");

	try {
	    if (UtilValidate.isNotEmpty(partyIdFrom) && UtilValidate.isNotEmpty(roleTypeIdFrom) && UtilValidate.isNotEmpty(roleTypeIdTo) && UtilValidate.isNotEmpty(partyRelationshipTypeId)) {
		EntityCondition searchConditions = EntityCondition.makeCondition(EntityOperator.AND,
			EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, partyIdFrom),
			//EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, partyIdTo),
			EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, roleTypeIdFrom),
			EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, roleTypeIdTo),
			EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, partyRelationshipTypeId),
			EntityUtil.getFilterByDateExpr());

		List<GenericValue> existingRelationships = delegator.findList("PartyRelationship", searchConditions,null, null, null, false);
		if (UtilValidate.isNotEmpty(existingRelationships)) {
		    return existingRelationships.stream().map(x-> {
			return x.getString("partyIdTo");
		    }).collect(Collectors.toList());
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return null;
    }

    public static List<GenericValue> getPartyRelList(Delegator delegator, Map<String, Object> context) {
	String partyIdFrom = (String) context.get("partyIdFrom");
	//String partyIdTo = (String) context.get("partyIdTo");
	String roleTypeIdFrom = (String) context.get("roleTypeIdFrom");
	String roleTypeIdTo = (String) context.get("roleTypeIdTo");
	String partyRelationshipTypeId = (String) context.get("partyRelationshipTypeId");

	try {
	    if (UtilValidate.isNotEmpty(partyIdFrom) && UtilValidate.isNotEmpty(roleTypeIdFrom) && UtilValidate.isNotEmpty(roleTypeIdTo) && UtilValidate.isNotEmpty(partyRelationshipTypeId)) {
		EntityCondition searchConditions = EntityCondition.makeCondition(EntityOperator.AND,
			EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, partyIdFrom),
			//EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, partyIdTo),
			EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, roleTypeIdFrom),
			EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, roleTypeIdTo),
			EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, partyRelationshipTypeId),
			EntityUtil.getFilterByDateExpr());

		List<GenericValue> existingRelationships = delegator.findList("PartyRelationship", searchConditions,null, null, null, false);
		return existingRelationships;
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return null;
    }

    public static String toList(List<String> list,String prefix){
	String str ="";

	if(list.size() > 0){
	    for(int i=0;i<list.size();i++){
		if(i == 0){
		    if(UtilValidate.isNotEmpty(prefix)){
			str= str+"'"+prefix+list.get(i)+"'";
		    }else{
			str= str+"'"+list.get(i)+"'";
		    }
		}
		else{
		    if(UtilValidate.isNotEmpty(prefix)){
			str = str+",'"+prefix+list.get(i)+"'";
		    }else{
			str = str+",'"+list.get(i)+"'";
		    }
		}
	    }
	}

	return str;
    }

    public static List<String> getContactPartyList(Delegator delegator, Map<String, Object> context) {

	String partyIdFrom = (String) context.get("partyIdFrom");
	String partyIdTo = (String) context.get("partyIdTo");
	String roleTypeIdFrom = (String) context.get("roleTypeIdFrom");
	String roleTypeIdTo = (String) context.get("roleTypeIdTo");
	String partyRelationshipTypeId = (String) context.get("partyRelationshipTypeId");
	List<String> contactIdList = new ArrayList<String>();
	try {
	    if (UtilValidate.isNotEmpty(partyIdFrom) && UtilValidate.isNotEmpty(partyIdTo) && UtilValidate.isNotEmpty(roleTypeIdFrom) && UtilValidate.isNotEmpty(roleTypeIdTo) && UtilValidate.isNotEmpty(partyRelationshipTypeId)) {
		EntityCondition searchConditions = EntityCondition.makeCondition(EntityOperator.AND,
			EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, partyIdTo),
			EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, roleTypeIdFrom),
			EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, roleTypeIdTo),
			EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, partyRelationshipTypeId),
			EntityUtil.getFilterByDateExpr());

		List<GenericValue> relationshipList = EntityQuery.use(delegator).from("PartyRelationship").where(searchConditions).queryList();

		if(UtilValidate.isNotEmpty(relationshipList)) {
		    contactIdList = EntityUtil.getFieldListFromEntityList(relationshipList, "roleTypeIdFrom", true);
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}

	return contactIdList;
    }

    public static void loadParentAttachments(Delegator delegator, String custRequestId, String custRequestIdNew){
	List<String> contentIds = new LinkedList<String>();
	try {
	    List<EntityCondition> conditionlist = new ArrayList<EntityCondition>();
	    List<GenericValue> toBeStore = new ArrayList<GenericValue>();

	    conditionlist.add(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId));
	    conditionlist.add(EntityCondition.makeCondition("contentTypeId", EntityOperator.IN,
		    UtilMisc.toList("SR_ATTACHMENT_DATA", "HYPERLINK", "EMAIL_ATTACHMENT_DATA")));
	    conditionlist.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));

	    EntityCondition custReqCondition = EntityCondition.makeCondition(conditionlist, EntityOperator.AND);
	    List<GenericValue> srContentDataList = delegator.findList("CustRequestContent", custReqCondition, null,null, null, false);

	    if (UtilValidate.isNotEmpty(srContentDataList)) {
		for(GenericValue srContentData: srContentDataList) {
		    GenericValue srContentDataNew = (GenericValue) srContentData.clone();
		    srContentDataNew.put("custRequestId", custRequestIdNew);
		    toBeStore.add(srContentDataNew);
		}
		//contentIds = EntityUtil.getFieldListFromEntityList(srContentDataList, "contentId", true);
	    }


	    // get activity attachment

	    List<GenericValue> workEfforts = EntityQuery.use(delegator).from("WorkEffort").where("domainEntityType", "SERVICE_REQUEST", "domainEntityId", custRequestId).queryList();
	    List<String> workEffortIds = UtilValidate.isNotEmpty(workEfforts) ? EntityUtil.getFieldListFromEntityList(workEfforts, "workEffortId", true) : new ArrayList<String>();
	    if (UtilValidate.isNotEmpty(workEffortIds)) {
		conditionlist.clear();
		conditionlist.add(EntityCondition.makeCondition("workEffortId", EntityOperator.IN, workEffortIds));
		conditionlist.add(EntityCondition.makeCondition("workEffortContentTypeId", EntityOperator.IN, UtilMisc.toList("ACTIVITY_ATTACHMENT_DATA", "ACTIVITY_HYPERLINK", "EMAIL_ATTACHMENT_DATA")));
		conditionlist.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));

		EntityCondition workEffortCondition = EntityCondition.makeCondition(conditionlist, EntityOperator.AND);
		List<GenericValue> activityContentDataList = delegator.findList("WorkEffortContent", workEffortCondition, null, null, null, false);

		if (UtilValidate.isNotEmpty(activityContentDataList)) {
		    for(GenericValue activityContentData : activityContentDataList) {
			GenericValue custRequestContentNew = delegator.makeValue("CustRequestContent");
			custRequestContentNew.put("custRequestId", custRequestIdNew);
			String contentTypeId =  activityContentData.getString("workEffortContentTypeId");
			String contentId = activityContentData.getString("contentId");
			Timestamp fromDate = activityContentData.getTimestamp("fromDate");
			if("ACTIVITY_ATTACHMENT_DATA".equals(contentTypeId))  contentTypeId = "SR_ATTACHMENT_DATA";
			if("ACTIVITY_HYPERLINK".equals(contentTypeId))  contentTypeId = "HYPERLINK";
			custRequestContentNew.set("contentId", contentId);
			custRequestContentNew.set("contentTypeId", contentTypeId);
			custRequestContentNew.set("fromDate", fromDate);
			toBeStore.add(custRequestContentNew);
		    }
		}
	    }

	    if(UtilValidate.isNotEmpty(toBeStore)) {
		delegator.storeAll(toBeStore);
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public static void loadParentNotes(Delegator delegator, String custRequestId, String custRequestIdNew){

	try {
	    String partyId = "";
	    GenericValue custRequestData = EntityQuery.use(delegator).from("CustRequest")
		    .where("custRequestId", custRequestId).queryOne();
	    if (UtilValidate.isNotEmpty(custRequestData)
		    && UtilValidate.isNotEmpty(custRequestData.getString("fromPartyId"))) {
		partyId = custRequestData.getString("fromPartyId");
	    }
	    List<GenericValue> toBeStore = new ArrayList<GenericValue>();

	    List<EntityCondition> conditionList = new ArrayList<EntityCondition>();

	    conditionList.add(EntityCondition.makeCondition("custRequestId", EntityOperator.EQUALS, custRequestId));

	    if(UtilValidate.isNotEmpty(conditionList)) {
		EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);

		List<GenericValue> noteList = delegator.findList("CustRequestNoteView", mainConditons, null,
			UtilMisc.toList("noteDateTime DESC"), null, false);
		if (UtilValidate.isNotEmpty(noteList)) {
		    List<String> noteIds = EntityUtil.getFieldListFromEntityList(noteList, "noteId", true);
		    EntityCondition condition = EntityCondition.makeCondition("noteId", EntityOperator.IN, noteIds);
		    List<GenericValue> noteDataList = EntityQuery.use(delegator).from("NoteData").where(condition).queryList();

		    if(UtilValidate.isNotEmpty(noteDataList)) {
			for(GenericValue noteData : noteDataList) {
			    GenericValue noteDataNew = (GenericValue) noteData.clone();
			    noteDataNew.put("noteParty", partyId);
			    toBeStore.add(noteDataNew);
			}
		    }

		    List<GenericValue> custRequestNoteList = EntityQuery.use(delegator).from("CustRequestNote").where(condition).queryList();
		    if(UtilValidate.isNotEmpty(custRequestNoteList)) {
			for(GenericValue custRequestNote : custRequestNoteList) {
			    GenericValue custRequestNoteNew = (GenericValue) custRequestNote.clone();
			    custRequestNoteNew.put("custRequestId", custRequestIdNew);
			    toBeStore.add(custRequestNoteNew);
			}
		    }		
		}
	    }
	    if(UtilValidate.isNotEmpty(toBeStore)) {
		delegator.storeAll(toBeStore);
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
	public static boolean isJSONValid(String test) {
		try {
			new JSONObject(test);
		} catch (JSONException ex) {
			try {
				new JSONArray(test);
			} catch (JSONException ex1) {
				return false;
			}
		}
		return true;
	}

    public static String isEnabledEconomicMetricForComponent(Delegator delegator, String parameterId, String defValue,String component) {
	String isEnabled =defValue;
	List<String> componentIds = new LinkedList<String>();
	try {
	    String paramValues = "";
	    if (UtilValidate.isNotEmpty(parameterId)) {
		paramValues = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, parameterId);
	    }
	    if (UtilValidate.isNotEmpty(paramValues)) {
		componentIds = org.fio.homeapps.util.DataUtil.stringToList(paramValues, ",");
		if (UtilValidate.isNotEmpty(component) && UtilValidate.isNotEmpty(componentIds) && !componentIds.contains(component)) {
		    return "N";
		}
	    }
	} catch (Exception e) {
	}

	return defValue;
    }
    public static int getDefaultAutoCompleteMaxRows(Delegator delegator) {
	try {
	    String globalParameter =org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "DEFAULT_AUTO_COMPLETE_MAX_ROWS","20");
	    if (UtilValidate.isNotEmpty(globalParameter)) {
		return Integer.parseInt(globalParameter);
	    }
	} catch (Exception e) {
	}
	return 20;
    }
    public static int getDefaultMaxRowsCount(Delegator delegator) {
	try {
	    String globalParameter =org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "DEFAULT_MAX_ROWS_COUNT","100");
	    if (UtilValidate.isNotEmpty(globalParameter)) {
		return Integer.parseInt(globalParameter);
	    }
	} catch (Exception e) {
	}
	return 100;
    }
    public static int defaultFioGridfetchLimit(Delegator delegator) {
	try {
	    GenericValue systemProperty = EntityQuery.use(delegator).from("SystemProperty").where("systemResourceId", "general", "systemPropertyId", "fio.grid.fetch.limit").queryFirst();
	    if (UtilValidate.isNotEmpty(systemProperty)) {
		return Integer.parseInt((String) systemProperty.getString("systemPropertyValue"));
	    }
	} catch (Exception e) {
	}
	return 1000;
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
	public static List<Map<String, String>> getDaysLastCallCountDropdown(Delegator delegator) {
		List<Map<String, String>> lengthList = FastList.newInstance();

		String daysSinceLastCall = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "DAYS_SINCE_LAST_CALL_COUNT");
		if (UtilValidate.isNotEmpty(daysSinceLastCall)) {
			if (UtilValidate.isNotEmpty(daysSinceLastCall.contains("-"))) {
				String[] daysSinceLastCallCountValues = daysSinceLastCall.split("-");
				if (daysSinceLastCallCountValues.length == 2) {
					String initValue = daysSinceLastCallCountValues[0];
					String lastValue = daysSinceLastCallCountValues[1];
					if (UtilValidate.isNotEmpty(initValue))
						initValue = initValue.trim();
					if (UtilValidate.isNotEmpty(lastValue))
						lastValue = lastValue.trim();
					if (UtilValidate.isNotEmpty(initValue) && UtilValidate.isNotEmpty(lastValue)) {
						Long initValueLong = Long.valueOf(initValue);
						Long lastValueLong = Long.valueOf(lastValue);
						if (lastValueLong > initValueLong) {
							for (; initValueLong <= lastValueLong; initValueLong++) {
								lengthList
										.add(UtilMisc.toMap("value",initValueLong.toString(),"description",initValueLong.toString()));
							}
						}
					}
				}
			} else {
				lengthList.add(UtilMisc.toMap("value",daysSinceLastCall.trim(),"description",daysSinceLastCall.trim()));
			}
		}

		return lengthList;
	}
	
	public static String numberFormatter(Locale locale, String valStr) {
		String value = valStr;
		try {
			if(locale == null) locale = new Locale("en", "US");
			if (UtilValidate.isNotEmpty(valStr)) {
			    NumberFormat usFormatter = NumberFormat.getInstance(locale);
			    double num = Double.parseDouble(valStr);
			    value = usFormatter.format(num);
			}
		} catch (Exception e) {
			value = valStr;
		}
		return value;
	}
	public static List<String> getPartyRoles(String partyId, List<String> possibleRoleTypeIds, Delegator delegator) throws GenericEntityException {

		List<EntityCondition> conditions = new ArrayList<>();
		EntityCondition mainConditons = null;
		try {
			if(UtilValidate.isNotEmpty(possibleRoleTypeIds) && UtilValidate.isNotEmpty(partyId)) {
				conditions.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
				conditions.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, possibleRoleTypeIds));
				mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);
			}
			return EntityUtil.getFieldListFromEntityList(delegator.findList("PartyRole", mainConditons, null, null, null, false),"roleTypeId",true);
		} catch (GenericEntityException e) {
			Debug.logError("Error while retrieving party roles: " + e.getMessage(), MODULE);
		}
		return new ArrayList<>();
	}
	public static void updateTabs(EntityCondition commonCondition, Set<String> tabsToBeSelected, String isEnabled, Delegator delegator) {
		List<EntityCondition> conditions = new ArrayList<>();
		conditions.add(commonCondition);
		conditions.add(EntityCondition.makeCondition("tabId", EntityOperator.IN, tabsToBeSelected));
		EntityCondition mainCondition = EntityCondition.makeCondition(conditions, EntityOperator.AND);
		List<GenericValue> enablePhoneCallRelatedTabs;
		try {
			enablePhoneCallRelatedTabs = EntityQuery.use(delegator)
					.select("isEnabled", "componentId", "tabConfigId", "tabId")
					.from("NavTabsConfig")
					.where(mainCondition)
					.cache(false)
					.queryList();
			for (GenericValue tab : enablePhoneCallRelatedTabs) {
				tab.set("isEnabled", isEnabled);
				tab.store();
			}
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String getLogoFilePath(Delegator delegator) {
		return DataUtil.getFilePathByType(delegator, "LOGO");
	}

	public static String getIconFilePath(Delegator delegator) {
		return DataUtil.getFilePathByType(delegator, "ICON");
	}

	public static String getFilePathByType(Delegator delegator, String type) {
		String imagePath = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "CLIENT_"+type );
		String defaultImagePath = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator,"DEFAULT_"+type);

		if(UtilValidate.isEmpty(defaultImagePath)) {
			if(type.equals("LOGO")) {
				defaultImagePath = "/bootstrap/images/fio-logo.png";
			}else if(type.equals("ICON")) {
				defaultImagePath = "/bootstrap/images/favicon.ico";
			}
		}
		if(UtilValidate.isNotEmpty(imagePath)) {
			UtilCache.clearAllCaches();

			String ofbizHome = System.getProperty("ofbiz.home");
			String boostrapPath = imagePath.substring(imagePath.indexOf("/bootstrap/images/"));
			String imageServerPath = ofbizHome + imagePath+"/";
			File directory = new File(imageServerPath);
			File[] files = directory.listFiles();

			if (files != null) {
				for (File file : files) {
					if (FilenameUtils.getExtension((file.getName())) != null) {
						return boostrapPath+"/"+file.getName();
					}
				}
			}
		}
		return defaultImagePath;
	}
	public static boolean isValidUrl(String urlString) {
		Set<String> allowedProtocols = new HashSet<>();
        allowedProtocols.add("http");
        allowedProtocols.add("https");
        try {
			URL url = new URL(urlString);
			String protocol = url.getProtocol();
			String host = url.getHost();
			return allowedProtocols.contains(protocol);
		} catch (MalformedURLException e) {
			return false;
		}
	}
}
