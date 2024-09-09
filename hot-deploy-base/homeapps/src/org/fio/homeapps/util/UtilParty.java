/**
 * 
 */
package org.fio.homeapps.util;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.fio.homeapps.constants.GlobalConstants.QueryType;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.jdbc.SQLProcessor;
import org.ofbiz.entity.util.EntityQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Sharif
 *
 */
public class UtilParty {

	private static final Logger log = LoggerFactory.getLogger(UtilParty.class);
	
	public static String getPartyIdByIdentification(Delegator delegator, String idValue, String identificationTypeId) {
		try {
			if(UtilValidate.isEmpty(idValue) || UtilValidate.isEmpty(identificationTypeId)){
				return null;
			}
			GenericValue gi = EntityQuery.use(delegator).from("PartyIdentification").where("partyIdentificationTypeId", identificationTypeId, "idValue", idValue).queryFirst();
			if (UtilValidate.isNotEmpty(gi)) {
				return gi.getString("partyId");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static GenericValue getDataImportCustomer(Delegator delegator, String batchId, String customerId) {
		try {
			if(UtilValidate.isEmpty(batchId) || UtilValidate.isEmpty(customerId)){
				return null;
			}
			GenericValue entity = EntityQuery.use(delegator).from("DataImportCustomer").where("batchId", batchId, "customerId", customerId, "importStatusId", "READY").queryFirst();
			return entity;
		} catch (Exception e) {	
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getPartyIdentificationTypeId(Delegator delegator, String channelAccessId) {
		String partyIdentificationTypeId = "EXT_PARTY_ID";
		try {
			if(UtilValidate.isEmpty(channelAccessId)){
				return partyIdentificationTypeId;
			}
			
			if (channelAccessId.equals("INDICA_POS")) {
				partyIdentificationTypeId = "PATIENT_ID";
			} else if (channelAccessId.equals("MEADOW_API")) {
				partyIdentificationTypeId = "MWD_ID";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return partyIdentificationTypeId;
	}
	
	public static String getPartyIdByDedupLogic(Delegator delegator, String idValue, String identificationTypeId, String emailAddress, String contactNumber, String partyName, boolean isPartyGroup) {
		String partyId = null;
		try {
			if(UtilValidate.isEmpty(idValue) || UtilValidate.isEmpty(identificationTypeId) || UtilValidate.isEmpty(partyName)) {
				return null;
			}
			
			partyId = getPartyIdByIdentification(delegator, idValue, identificationTypeId);
			String partyNameEntity = "person";
			String partyNameField = "first_name";
			if (isPartyGroup) {
				partyNameEntity = "party_group";
				partyNameField = "group_name";
			}
			
			SQLProcessor sqlProcessor = new SQLProcessor(delegator, delegator.getGroupHelperInfo("org.ofbiz"));
            ResultSet rs = null;
            
			if (UtilValidate.isEmpty(partyId) && UtilValidate.isNotEmpty(emailAddress)) {
				String contactMechPurposeTypeId = "PRIMARY_EMAIL";
				
				String queryStr = "SELECT pcm.party_id as partyId FROM party_contact_mech pcm"
						+ " INNER JOIN party_contact_mech_purpose pcmp ON pcmp.CONTACT_MECH_ID=pcm.CONTACT_MECH_ID"
						+ " INNER JOIN contact_mech cm ON cm.CONTACT_MECH_ID=pcmp.CONTACT_MECH_ID"
						+ " INNER JOIN "+partyNameEntity+" per ON per.party_id=pcm.party_id";
				queryStr += " WHERE cm.info_string='"+emailAddress+"' "
						+ " AND pcmp.CONTACT_MECH_PURPOSE_TYPE_ID='"+contactMechPurposeTypeId+"'"
						+ " AND cm.CONTACT_MECH_TYPE_ID='EMAIL_ADDRESS'";
				queryStr += " AND" + QueryUtil.getFilterByDateExpr(QueryType.NATIVE, "from_date", "thru_date", "pcm");
				queryStr += " AND per."+partyNameField+" like '"+partyName+"%'";
				
				rs = sqlProcessor.executeQuery(queryStr);
				if (rs != null) {
            		if (rs.next()) {
            			if (UtilValidate.isNotEmpty(rs.getString("partyId"))) {
            				partyId = rs.getString("partyId");
            			}
            		}
            	}
			}
			
			if (UtilValidate.isEmpty(partyId) && UtilValidate.isNotEmpty(contactNumber)) {
				String contactMechPurposeTypeId = "PRIMARY_PHONE";
				
				String queryStr = "SELECT pcm.party_id as partyId FROM party_contact_mech pcm"
						+ " INNER JOIN party_contact_mech_purpose pcmp ON pcmp.CONTACT_MECH_ID=pcm.CONTACT_MECH_ID"
						+ " INNER JOIN telecom_number tn ON tn.CONTACT_MECH_ID=pcmp.CONTACT_MECH_ID"
						+ " INNER JOIN "+partyNameEntity+" per ON per.party_id=pcm.party_id";
				queryStr += " WHERE tn.contact_number='"+contactNumber+"' "
						+ " AND pcmp.CONTACT_MECH_PURPOSE_TYPE_ID='"+contactMechPurposeTypeId+"'";
				queryStr += " AND" + QueryUtil.getFilterByDateExpr(QueryType.NATIVE, "from_date", "thru_date", "pcm");
				queryStr += " AND per."+partyNameField+" like '"+partyName+"%'";
				
				rs = sqlProcessor.executeQuery(queryStr);
				if (rs != null) {
            		if (rs.next()) {
            			if (UtilValidate.isNotEmpty(rs.getString("partyId"))) {
            				partyId = rs.getString("partyId");
            			}
            		}
            	}
			}
			
			if (UtilValidate.isEmpty(partyId)) {
				List<EntityCondition> conditions = new ArrayList<EntityCondition>();
				conditions.add(EntityCondition.makeCondition(EntityOperator.OR,
						EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, idValue),
						EntityCondition.makeCondition("externalId", EntityOperator.EQUALS, idValue)
						));
		    	EntityCondition mainConditon = EntityCondition.makeCondition(conditions, EntityOperator.AND);
		    	GenericValue party = EntityQuery.use(delegator).from("Party").where(mainConditon).queryFirst();
				if (UtilValidate.isNotEmpty(party)) {
					partyId = party.getString("partyId");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return partyId;
	}
	
}
