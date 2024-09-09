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

package org.fio.dataimport;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.fio.dataimport.util.DataUtil;
import org.fio.dataimport.util.PartyHelper;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.datasource.GenericHelperInfo;
import org.ofbiz.entity.jdbc.SQLProcessor;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;

/**
 * @author Sharif
 *
 */
public class EmplPositionImportServices {

    public static String module = EmplPositionImportServices.class.getName();

    public static Map<String, Object> importEmplPositions(DispatchContext dctx, Map<String, ?> context) {

        String organizationPartyId = (String) context.get("organizationPartyId");
        int imported = 0;
        OpentapsImporter importer = null;
        try {
            // then import the tax rates for the counties
        	importer = new OpentapsImporter("DataImportEmplPosition", dctx, new EmplPositionDecoder(organizationPartyId));
            imported += importer.runImport(context);
        } catch (GenericEntityException e) {
            return UtilMessage.createAndLogServiceError(e, module);
        }
        
        Map<String, Object> result = ServiceUtil.returnSuccess();
        result.put("importedRecords", imported);
        result.put("importedDataList", importer.importedDataList);
        
        return result;
    }
}

// maps DataImportLead into a set of opentaps entities that describes the Lead
class EmplPositionDecoder implements ImportDecoder {
    public static final String module = EmplPositionDecoder.class.getName();
    protected String organizationPartyId;

    public EmplPositionDecoder(String organizationPartyId) {
        this.organizationPartyId = organizationPartyId;
    }

    public List<GenericValue> decode(GenericValue entry, Timestamp importTimestamp, Delegator delegator, LocalDispatcher dispatcher, Object... args) throws Exception {
        
    	//String isExternalIdAsPartyId = UtilProperties.getPropertyValue("config.properties", "is.lead.externalId.asPartyId");
    	GenericValue userLogin = delegator.findOne("UserLogin", false, UtilMisc.toMap("userLoginId","admin"));
    	
    	List<GenericValue> toBeStored = FastList.newInstance();
    	
    	if (entry.getString("isAccess").equals("N")) {
    		
    		String reportingEmplPositionId = null;
    		String managedEmplPositionId = null;
    		
    		// expiring reporting information [start]
    		
    		//GenericValue reportingMember = DataUtil.findPartyByEmail(delegator, entry.getString("reportingEmail"));
    		GenericValue reportingMember = DataUtil.findPartyByLogin(delegator, entry.getString("reporting1bankid"));
    		if (UtilValidate.isNotEmpty(reportingMember)) {
    			String rmPartyId = reportingMember.getString("partyId");
	    		/*GenericValue reportingUserLogin = EntityUtil.getFirst(delegator.findByAnd("UserLogin", UtilMisc.toMap("userLoginId", entry.getString("reporting1bankid")), null, false));
	            if (UtilValidate.isNotEmpty(reportingUserLogin)) {
	            	reportingUserLogin.put("enabled", "N");
	            	toBeStored.add(reportingUserLogin);
	            }*/
	            
	            GenericValue reportingEmplPosition = UtilImport.getPartyEmplPosition(delegator, rmPartyId, entry.getString("reportingPositionType"));
	            if (UtilValidate.isNotEmpty(reportingEmplPosition)) {
	            	reportingEmplPositionId = reportingEmplPosition.getString("emplPositionId");
	            	/*GenericValue emplPosition = EntityUtil.getFirst(delegator.findByAnd("EmplPosition", UtilMisc.toMap("emplPositionId", reportingEmplPositionId), null, false));
		        	if (UtilValidate.isNotEmpty(emplPosition)) {
		        		reportingEmplPosition.put("thruDate", UtilDateTime.nowTimestamp());
		        		toBeStored.add(reportingEmplPosition);
		        		
		        		emplPosition.put("statusId", "EMPL_POS_ACTIVE");
		        		toBeStored.add(emplPosition);
		        		
		        	}*/
	            }
	        	
	            /*GenericValue dbsRole = EntityUtil.getFirst(delegator.findByAnd("PartyRole", UtilMisc.toMap("partyId", rmPartyId, "roleTypeId", entry.getString("reportingPositionType")), null, false));
				if (UtilValidate.isNotEmpty(dbsRole)) {
					dbsRole.remove();
				}*/
				
				// remove from any team [start]
	            
	            /*EntityCondition conditions = EntityCondition.makeCondition(EntityOperator.AND,
	                    EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "ACCOUNT_TEAM"),
	                    EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "ACCOUNT_MANAGER"),
	                    //EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, accountTeamPartyId),
	                    EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, rmPartyId),
	                    EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "ASSIGNED_TO"),
	                    EntityUtil.getFilterByDateExpr());
	            
	            List<GenericValue> relationships = delegator.findList("PartyRelationship",  conditions, null, null, null, false);
	            for (GenericValue relationship : relationships) {
	            	relationship.put("thruDate", UtilDateTime.nowTimestamp());
	            	toBeStored.add(relationship);
	            }*/
	            
	            // remove from any team [end]
    		}
    		
            // expiring reporting information [end]
            
            // expiring managed information [start]
    		
    		//GenericValue managedMember = DataUtil.findPartyByEmail(delegator, entry.getString("mangedEmail"));
    		GenericValue managedMember = DataUtil.findPartyByLogin(delegator, entry.getString("managed1bankid"));
            if (UtilValidate.isNotEmpty(managedMember)) {
            	String rmPartyId = managedMember.getString("partyId");
	            /*GenericValue managedUserLogin = EntityUtil.getFirst(delegator.findByAnd("UserLogin", UtilMisc.toMap("userLoginId", entry.getString("managed1bankid")), null, false));
	            if (UtilValidate.isNotEmpty(managedUserLogin)) {
	            	managedUserLogin.put("enabled", "N");
	            	toBeStored.add(managedUserLogin);
	            }*/
	            
	            GenericValue reportingEmplPosition = UtilImport.getPartyEmplPosition(delegator, rmPartyId, entry.getString("managedByPositionType"));
	            if (UtilValidate.isNotEmpty(reportingEmplPosition)) {
	            	managedEmplPositionId = reportingEmplPosition.getString("emplPositionId");
		        	/*GenericValue emplPosition = EntityUtil.getFirst(delegator.findByAnd("EmplPosition", UtilMisc.toMap("emplPositionId", managedEmplPositionId), null, false));
		        	if (UtilValidate.isNotEmpty(emplPosition)) {
		        		reportingEmplPosition.put("thruDate", UtilDateTime.nowTimestamp());
		        		toBeStored.add(reportingEmplPosition);
		        		
		        		emplPosition.put("statusId", "EMPL_POS_ACTIVE");
		        		toBeStored.add(emplPosition);
		        		
		        	}*/
	            }
	            
	            /*GenericValue dbsRole = EntityUtil.getFirst(delegator.findByAnd("PartyRole", UtilMisc.toMap("partyId", rmPartyId, "roleTypeId", entry.getString("managedByPositionType")), null, false));
				if (UtilValidate.isNotEmpty(dbsRole)) {
					dbsRole.remove();
				}*/
				
				// remove from any team [start]
	            
	            /*EntityCondition conditions = EntityCondition.makeCondition(EntityOperator.AND,
	                    EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "ACCOUNT_TEAM"),
	                    EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "ACCOUNT_MANAGER"),
	                    //EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, accountTeamPartyId),
	                    EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, rmPartyId),
	                    EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "ASSIGNED_TO"),
	                    EntityUtil.getFilterByDateExpr());
	            
	            List<GenericValue> relationships = delegator.findList("PartyRelationship",  conditions, null, null, null, false);
	            for (GenericValue relationship : relationships) {
	            	relationship.put("thruDate", UtilDateTime.nowTimestamp());
	            	toBeStored.add(relationship);
	            }*/
	            
	            // remove from any team [end]
	            
            }
            
            // expiring managed information [end]
            
            if (UtilValidate.isNotEmpty(reportingEmplPositionId) && UtilValidate.isNotEmpty(managedEmplPositionId)) {
            	EntityCondition searchConditions = EntityCondition.makeCondition(EntityOperator.AND,
        				EntityCondition.makeCondition("emplPositionIdReportingTo", EntityOperator.EQUALS, reportingEmplPositionId),
        				EntityCondition.makeCondition("emplPositionIdManagedBy", EntityOperator.EQUALS, managedEmplPositionId),
        				EntityUtil.getFilterByDateExpr()
        				);
        		
        		GenericValue reportingStruct = EntityUtil.getFirst( delegator.findList("EmplPositionReportingStruct", searchConditions, null, null, null, false) );
        		if (UtilValidate.isNotEmpty(reportingStruct)) {
        			reportingStruct.put("thruDate", UtilDateTime.nowTimestamp());
	        		toBeStored.add(reportingStruct);
        		}
            }
            
            return toBeStored;
    	}

    	// reporting member [start]
    	
    	//GenericValue reportingMember = DataUtil.findPartyByEmail(delegator, entry.getString("reportingEmail"));
        GenericValue reportingMember = DataUtil.findPartyByLogin(delegator, entry.getString("reporting1bankid"));
        String rmPartyId = null;
        if (UtilValidate.isEmpty(reportingMember)) {
        	
        	rmPartyId = delegator.getNextSeqId("Party");
			String firstName = "";
			String lastName = "";
			String rmFullName = entry.getString("reportingMemberName");
			
			if (UtilValidate.isNotEmpty(rmFullName)) {
	        	String[] tokens = rmFullName.split(" ", 2);
	        	if (tokens.length > 1) {
	        		firstName = tokens[0];
	        		lastName = tokens[1];
	        	} else {
	        		lastName = tokens[0];
	        	}
	        }
			
			toBeStored.addAll(UtilImport.makePartyWithRolesExt(rmPartyId, "PERSON", rmPartyId,  UtilMisc.toList("CUSTOMER", "BILL_TO_CUSTOMER"), delegator));
            GenericValue person = delegator.makeValue("Person", UtilMisc.toMap("partyId", rmPartyId, "firstName", firstName, "lastName", lastName));
            toBeStored.add(person);
            
            toBeStored.addAll(UtilImport.makePartyWithRolesExt(rmPartyId, "PERSON", rmPartyId,  UtilMisc.toList("ACCOUNT_MANAGER", "EMPLOYEE", entry.getString("reportingPositionType")), delegator));
            
            GenericValue emailContactMech = delegator.makeValue("ContactMech", UtilMisc.toMap("contactMechId", delegator.getNextSeqId("ContactMech"), "contactMechTypeId", "EMAIL_ADDRESS", "infoString", entry.getString("reportingEmail")));
            toBeStored.add(emailContactMech);
            if (rmPartyId != null) {
                toBeStored.add(delegator.makeValue("PartyContactMech", UtilMisc.toMap("contactMechId", emailContactMech.get("contactMechId"), "partyId", rmPartyId, "fromDate", importTimestamp,"allowSolicitation","Y")));
                toBeStored.add(UtilImport.makeContactMechPurpose("PRIMARY_EMAIL", emailContactMech, rmPartyId, importTimestamp, delegator));
                //toBeStored.add(UtilImport.makePartySupplementalData(partySupplementalData, personPartyId, "primaryEmailId", emailContactMech, delegator));
            }
            
        } else {
        	rmPartyId = reportingMember.getString("partyId");
        	
        	String firstName = "";
			String lastName = "";
			String rmFullName = entry.getString("reportingMemberName");
			
			if (UtilValidate.isNotEmpty(rmFullName)) {
	        	String[] tokens = rmFullName.split(" ", 2);
	        	if (tokens.length > 1) {
	        		firstName = tokens[0];
	        		lastName = tokens[1];
	        	} else {
	        		lastName = tokens[0];
	        	}
	        }
        	
			GenericValue person = EntityUtil.getFirst(delegator.findByAnd("Person", UtilMisc.toMap("partyId", rmPartyId), null, false));
			if (UtilValidate.isNotEmpty(person)) {
				person.put("firstName", firstName);
				person.put("lastName", lastName);
				toBeStored.add(person);
			}
			
			GenericValue dbsRole = DataUtil.getFirstDbsRole(delegator, rmPartyId);
			if (UtilValidate.isNotEmpty(dbsRole)) {
				if (!dbsRole.getString("roleTypeId").equals(entry.getString("reportingPositionType"))) {
					dbsRole.remove();
				} 
			}
			
			dbsRole = delegator.makeValue("PartyRole", UtilMisc.toMap("partyId", rmPartyId, "roleTypeId", entry.getString("reportingPositionType")));
			toBeStored.add(dbsRole);
			
			GenericValue emailPurpose = DataUtil.getActivePartyContactMechPurpose(delegator, rmPartyId, "PRIMARY_EMAIL", null);
         	if(UtilValidate.isNotEmpty(emailPurpose)){
         		GenericValue emailContactMech = delegator.findOne("ContactMech", false, UtilMisc.toMap("contactMechId", emailPurpose.getString("contactMechId")));
         		if (UtilValidate.isNotEmpty(emailContactMech.getString("infoString")) && !entry.getString("reportingEmail").equals(emailContactMech.getString("infoString"))) {
         			emailContactMech.put("infoString", entry.getString("reportingEmail"));
         			toBeStored.add(emailContactMech);
		 		}
         	}
			
        }
        
        GenericValue rmUserLogin = EntityUtil.getFirst(delegator.findByAnd("UserLogin", UtilMisc.toMap("userLoginId", entry.getString("reporting1bankid")), null, false));
        if (UtilValidate.isEmpty(rmUserLogin)) {
        	rmUserLogin = delegator.makeValue("UserLogin");
        	rmUserLogin.put("userLoginId", entry.getString("reporting1bankid"));
        	rmUserLogin.put("partyId", rmPartyId);
        	rmUserLogin.put("currentPassword", "{SHA}47b56994cbc2b6d10aa1be30f70165adb305a41a");
        	rmUserLogin.put("enabled", "Y");
        	rmUserLogin.put("countryGeoId", entry.getString("countryGeo"));
        	toBeStored.add(rmUserLogin);
        } else {
        	rmUserLogin.put("enabled", "Y");
        	toBeStored.add(rmUserLogin);
        }
        
        GenericValue makerChecker = EntityUtil.getFirst(delegator.findByAnd("SystemProperty", UtilMisc.toMap("systemResourceId", "security", "systemPropertyId", entry.getString("reporting1bankid")), null, false));
        if (UtilValidate.isEmpty(makerChecker)) {
        	makerChecker = delegator.makeValue("SystemProperty");
        	makerChecker.put("systemResourceId", "security");
        	makerChecker.put("systemPropertyId", entry.getString("reporting1bankid"));
        	makerChecker.put("systemPropertyValue", "SG-I3BE-MAKERCHECKER");
        	makerChecker.put("description", "MAKERCHECKER");
        	toBeStored.add(makerChecker);
        }
        
        String reportingEmplPositionId = UtilImport.getPartyEmplPositionId(delegator, rmPartyId, entry.getString("reportingPositionType"));
        if (UtilValidate.isEmpty(reportingEmplPositionId)) {
        	
        	reportingEmplPositionId = assignEmplPosition(delegator, toBeStored, rmPartyId, entry.getString("reportingPositionType"), entry);
        	
        } else {
        	GenericValue emplPosition = EntityUtil.getFirst(delegator.findByAnd("EmplPosition", UtilMisc.toMap("emplPositionId", reportingEmplPositionId), null, false));
        	if (UtilValidate.isNotEmpty(emplPosition)) {
        		
        		emplPosition.put("statusId", "EMPL_POS_OCCUPIED");
        		toBeStored.add(emplPosition);
        		
        	}
        }
        
        // reporting member [end]
        
        // managed member [start]
        
      //GenericValue managedMember = DataUtil.findPartyByEmail(delegator, entry.getString("mangedEmail"));
        GenericValue managedMember = DataUtil.findPartyByLogin(delegator, entry.getString("managed1bankid"));
        if (UtilValidate.isEmpty(managedMember)) {
        	
        	rmPartyId = delegator.getNextSeqId("Party");
			String firstName = "";
			String lastName = "";
			String rmFullName = entry.getString("mangedMemberName");
			
			if (UtilValidate.isNotEmpty(rmFullName)) {
	        	String[] tokens = rmFullName.split(" ", 2);
	        	if (tokens.length > 1) {
	        		firstName = tokens[0];
	        		lastName = tokens[1];
	        	} else {
	        		lastName = tokens[0];
	        	}
	        }
			
			toBeStored.addAll(UtilImport.makePartyWithRolesExt(rmPartyId, "PERSON", rmPartyId,  UtilMisc.toList("CUSTOMER", "BILL_TO_CUSTOMER"), delegator));
            GenericValue person = delegator.makeValue("Person", UtilMisc.toMap("partyId", rmPartyId, "firstName", firstName, "lastName", lastName));
            toBeStored.add(person);
            
            toBeStored.addAll(UtilImport.makePartyWithRolesExt(rmPartyId, "PERSON", rmPartyId,  UtilMisc.toList("ACCOUNT_MANAGER", "EMPLOYEE", entry.getString("managedByPositionType")), delegator));
            
            GenericValue emailContactMech = delegator.makeValue("ContactMech", UtilMisc.toMap("contactMechId", delegator.getNextSeqId("ContactMech"), "contactMechTypeId", "EMAIL_ADDRESS", "infoString", entry.getString("mangedEmail")));
            toBeStored.add(emailContactMech);
            if (rmPartyId != null) {
                toBeStored.add(delegator.makeValue("PartyContactMech", UtilMisc.toMap("contactMechId", emailContactMech.get("contactMechId"), "partyId", rmPartyId, "fromDate", importTimestamp,"allowSolicitation","Y")));
                toBeStored.add(UtilImport.makeContactMechPurpose("PRIMARY_EMAIL", emailContactMech, rmPartyId, importTimestamp, delegator));
                //toBeStored.add(UtilImport.makePartySupplementalData(partySupplementalData, personPartyId, "primaryEmailId", emailContactMech, delegator));
            }
            
        } else {
        	rmPartyId = managedMember.getString("partyId");
        	
        	String firstName = "";
			String lastName = "";
			String rmFullName = entry.getString("mangedMemberName");
			
			if (UtilValidate.isNotEmpty(rmFullName)) {
	        	String[] tokens = rmFullName.split(" ", 2);
	        	if (tokens.length > 1) {
	        		firstName = tokens[0];
	        		lastName = tokens[1];
	        	} else {
	        		lastName = tokens[0];
	        	}
	        }
        	
			GenericValue person = EntityUtil.getFirst(delegator.findByAnd("Person", UtilMisc.toMap("partyId", rmPartyId), null, false));
			if (UtilValidate.isNotEmpty(person)) {
				person.put("firstName", firstName);
				person.put("lastName", lastName);
				toBeStored.add(person);
			}
			
			GenericValue dbsRole = DataUtil.getFirstDbsRole(delegator, rmPartyId);
			if (UtilValidate.isNotEmpty(dbsRole)) {
				if (!dbsRole.getString("roleTypeId").equals(entry.getString("managedByPositionType"))) {
					dbsRole.remove();
				} 
			}
			
			dbsRole = delegator.makeValue("PartyRole", UtilMisc.toMap("partyId", rmPartyId, "roleTypeId", entry.getString("managedByPositionType")));
			toBeStored.add(dbsRole);
			
			GenericValue emailPurpose = DataUtil.getActivePartyContactMechPurpose(delegator, rmPartyId, "PRIMARY_EMAIL", null);
         	if(UtilValidate.isNotEmpty(emailPurpose)){
         		GenericValue emailContactMech = delegator.findOne("ContactMech", false, UtilMisc.toMap("contactMechId", emailPurpose.getString("contactMechId")));
         		if (UtilValidate.isNotEmpty(emailContactMech.getString("infoString")) && !entry.getString("mangedEmail").equals(emailContactMech.getString("infoString"))) {
         			emailContactMech.put("infoString", entry.getString("mangedEmail"));
         			toBeStored.add(emailContactMech);
		 		}
         	}
        }
        
        rmUserLogin = EntityUtil.getFirst(delegator.findByAnd("UserLogin", UtilMisc.toMap("userLoginId", entry.getString("managed1bankid")), null, false));
        if (UtilValidate.isEmpty(rmUserLogin)) {
        	rmUserLogin = delegator.makeValue("UserLogin");
        	rmUserLogin.put("userLoginId", entry.getString("managed1bankid"));
        	rmUserLogin.put("partyId", rmPartyId);
        	rmUserLogin.put("currentPassword", "{SHA}47b56994cbc2b6d10aa1be30f70165adb305a41a");
        	rmUserLogin.put("enabled", "Y");
        	rmUserLogin.put("countryGeoId", entry.getString("countryGeo"));
        	toBeStored.add(rmUserLogin);
        } else {
        	rmUserLogin.put("enabled", "Y");
        	toBeStored.add(rmUserLogin);
        }
        
        makerChecker = EntityUtil.getFirst(delegator.findByAnd("SystemProperty", UtilMisc.toMap("systemResourceId", "security", "systemPropertyId", entry.getString("managed1bankid")), null, false));
        if (UtilValidate.isEmpty(makerChecker)) {
        	makerChecker = delegator.makeValue("SystemProperty");
        	makerChecker.put("systemResourceId", "security");
        	makerChecker.put("systemPropertyId", entry.getString("managed1bankid"));
        	makerChecker.put("systemPropertyValue", "SG-I3BE-MAKERCHECKER");
        	makerChecker.put("description", "MAKERCHECKER");
        	toBeStored.add(makerChecker);
        }
        
        String managedEmplPositionId = UtilImport.getPartyEmplPositionId(delegator, rmPartyId, entry.getString("managedByPositionType"));
        if (UtilValidate.isEmpty(managedEmplPositionId)) {
        	
        	managedEmplPositionId = assignEmplPosition(delegator, toBeStored, rmPartyId, entry.getString("managedByPositionType"), entry);
        		
        } else {
        	GenericValue emplPosition = EntityUtil.getFirst(delegator.findByAnd("EmplPosition", UtilMisc.toMap("emplPositionId", managedEmplPositionId), null, false));
        	if (UtilValidate.isNotEmpty(emplPosition)) {
        		
        		emplPosition.put("statusId", "EMPL_POS_OCCUPIED");
        		toBeStored.add(emplPosition);
        		
        	}
        }
        
        // managed member [end]
        
        // construct reporting structure [start]
        
        EntityCondition searchConditions = EntityCondition.makeCondition(EntityOperator.AND,
				EntityCondition.makeCondition("emplPositionIdReportingTo", EntityOperator.EQUALS, reportingEmplPositionId),
				EntityCondition.makeCondition("emplPositionIdManagedBy", EntityOperator.EQUALS, managedEmplPositionId),
				EntityUtil.getFilterByDateExpr()
				);
		
		GenericValue reportingStruct = EntityUtil.getFirst( delegator.findList("EmplPositionReportingStruct", searchConditions, null, null, null, false) );
		if (UtilValidate.isEmpty(reportingStruct) 
				|| ( UtilValidate.isNotEmpty(reportingStruct.getString("emplTeamId")) && !reportingStruct.getString("emplTeamId").equals(entry.getString("teamId")))) {
			
			if (UtilValidate.isNotEmpty(reportingStruct)) {
				reportingStruct.put("thruDate", UtilDateTime.nowTimestamp());
				toBeStored.add(reportingStruct);
			}
			
			GenericValue rs = delegator.makeValue("EmplPositionReportingStruct");
			rs.put("emplPositionIdReportingTo", reportingEmplPositionId);
			rs.put("emplPositionIdManagedBy", managedEmplPositionId);
			rs.put("emplTeamId", entry.getString("teamId"));
			rs.put("fromDate", UtilDateTime.nowTimestamp());
			rs.put("comments", "Created from import process");
        	toBeStored.add(rs);
			
		}
        
		// construct reporting structure [end]
		
        return toBeStored;
    }
    
    private static String assignEmplPosition(Delegator delegator, List<GenericValue> toBeStored, String partyId, String positionType, GenericValue entry) {
    	String emplPositionId = null;
    	GenericValue vacantPosition = UtilImport.getVacantEmplPosition(delegator, positionType, entry.getString("countryGeo"), entry.getString("city"));
    	if (UtilValidate.isEmpty(vacantPosition)) {
    		vacantPosition = delegator.makeValue("EmplPosition");
    		
    		emplPositionId = delegator.getNextSeqId("EmplPosition");
    		
    		vacantPosition.put("emplPositionId", emplPositionId);
    		vacantPosition.put("statusId", "EMPL_POS_OCCUPIED");
    		vacantPosition.put("partyId", "Company");
    		vacantPosition.put("emplPositionTypeId", positionType);
    		vacantPosition.put("actualFromDate", UtilDateTime.nowTimestamp());
    		vacantPosition.put("countryGeoId", entry.getString("countryGeo"));
    		vacantPosition.put("city", entry.getString("city"));
    	} else {
    		emplPositionId = vacantPosition.getString("emplPositionId");
    		vacantPosition.put("statusId", "EMPL_POS_OCCUPIED");
    	}
    	toBeStored.add(vacantPosition);
    	
    	GenericValue fulfillment = delegator.makeValue("EmplPositionFulfillment");
    	fulfillment.put("emplPositionId", emplPositionId);
    	fulfillment.put("partyId", partyId);
    	fulfillment.put("fromDate", UtilDateTime.nowTimestamp());
    	fulfillment.put("comments", "Created from import process");
    	toBeStored.add(fulfillment);
    	
    	return emplPositionId;
    }
    
	public static List<String> runSqlQuery(String query, Delegator delegator) {

		ResultSet rs = null;
		ArrayList<String> resultList = new ArrayList<String>();
		String selGroup = "org.ofbiz";

		String sqlCommandSeq = query;

		if (sqlCommandSeq != null && sqlCommandSeq.length() > 0 && selGroup != null && selGroup.length() > 0) {

			String helperName = delegator.getGroupHelperName(selGroup);
			GenericHelperInfo ghi = delegator.getGroupHelperInfo("org.ofbiz");
			SQLProcessor dumpSeq = new SQLProcessor(delegator,ghi);

			try {
				if (sqlCommandSeq.toUpperCase().startsWith("SELECT")) {

					rs = dumpSeq.executeQuery(sqlCommandSeq);

					while (rs.next()) {
						resultList.add(rs.getString(1));
					}
				}
			} catch (Exception e) {
			}finally {
				try { 
					if (rs != null) {
						rs.close();
					}
				} catch (Exception e) {
				}
		}
			
		}
		return resultList;
	}
	
}
