package org.fio.admin.portal.organizationHierarchy.teams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.ofbiz.entity.Delegator;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.entity.GenericValue;

import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.base.component.ComponentConfig;
import org.ofbiz.base.component.ComponentException;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;


import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.security.Security;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ModelService;
import org.fio.admin.portal.constant.AdminPortalConstant;
import org.fio.admin.portal.util.DataHelper;
import org.fio.admin.portal.util.DataUtil;
import net.sf.json.JSONArray;

public class TeamService {
	public static final String MODULE = TeamService.class.getName();
	public static final String RESOURCE = "AdminPortalUiLabels";
	/* for creating team */
	public static Map<String, Object> createTeam(DispatchContext dctx, Map<String, Object> context) {
		Debug.logInfo("------inside createTeam------ " + context, MODULE);
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> results = ServiceUtil.returnSuccess();
		Security security = dctx.getSecurity();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Locale locale = (Locale) context.get("locale");
		String teamName = (String) context.get("teamId");
		String isActive = (String) context.get("status");
		String buId = (String) context.get("businessUnit");
		String userLoginId = null;
		try {
			GenericValue team = EntityQuery.use(delegator).from("EmplTeam").where("teamName", teamName).queryOne();
			if(UtilValidate.isEmpty(team))
			{
				GenericValue teamToParty = delegator.makeValue("Party");
				String partyId = delegator.getNextSeqId("Party");
				teamToParty.put("partyId", partyId);
				teamToParty.put("roleTypeId", "TEAM_ROLE");
				teamToParty.put("partyTypeId", "PARTY_GROUP");
				teamToParty.create();
				
				
				team = delegator.makeValue("EmplTeam");
				String emplTeamId = delegator.getNextSeqId("EmplTeam");
				team.put("emplTeamId", emplTeamId);
				team.put("teamName", teamName);
				team.put("isActive", isActive);
				team.put("businessUnit", buId);
				team.put("partyId", partyId);
				userLoginId = userLogin.getString("userLoginId");
				team.put("createdBy", userLoginId);
				team.put("createdOn", UtilDateTime.nowTimestamp());
				team.create();
				
				/*GenericValue teamToParty = delegator.makeValue("Party");
				String partyId = delegator.getNextSeqId("Party");
				teamToParty.put("partyId", partyId);
				teamToParty.put("roleTypeId", "TEAM_ROLE");
				teamToParty.put("partyTypeId", "PARTY_GROUP");
				teamToParty.create();*/
				
				GenericValue teamToUserLogin = delegator.makeValue("UserLogin");
				teamToUserLogin.put("userLoginId", emplTeamId);
				teamToUserLogin.create();
				results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "TeamCreatedSuccessfully", locale));
				results.put("emplTeamId", emplTeamId);
				
				boolean isTeamUpdatePartyRelationship = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "IS_TEAM_UPD_PTY_RLTNSP","N").equals("Y");
				if(isTeamUpdatePartyRelationship) {
					DataHelper.addTeamPartyRelationship(team, userLogin, dispatcher);
				}
				
			}else{
				return ServiceUtil.returnError("Team already exists");
			}
		} catch (GeneralException e) {
			 Debug.log("==error in createTeams===" + e.getMessage());
		}
		return results;
	}
	/* for updating team */
	public static Map<String, Object> updateTeam(DispatchContext dctx, Map<String, Object> context) {
		Debug.logInfo("------inside updateTeam------ " + context, MODULE);
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> results = ServiceUtil.returnSuccess();
		Security security = dctx.getSecurity();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Locale locale = (Locale) context.get("locale");
		String emplTeamId = (String) context.get("emplTeamId");
		String teamName = (String) context.get("teamName");
		String isActive = (String) context.get("status");
		/*String buId = (String) context.get("buName");*/
		String userLoginId = null;
		try {
			
			boolean isTeamUpdatePartyRelationship = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "IS_TEAM_UPD_PTY_RLTNSP","N").equals("Y");
			boolean statusChanged = false;
			GenericValue teamDetails = EntityQuery.use(delegator).from("EmplTeam").where("emplTeamId", emplTeamId).queryOne();
			if(UtilValidate.isNotEmpty(teamDetails))
			{
				String name=teamDetails.getString("teamName");
				if(!teamDetails.getString("isActive").equals(isActive))
					statusChanged = true;
				if(UtilValidate.isNotEmpty(name)&&UtilValidate.isNotEmpty(teamName)) {
					if(teamName.equalsIgnoreCase(name)){
						teamDetails.put("isActive", isActive);
						/*teamDetails.put("businessUnit", buId);*/
			            userLoginId = userLogin.getString("userLoginId");
			            teamDetails.put("modifiedBy", userLoginId);
			            teamDetails.put("modifiedOn", UtilDateTime.nowTimestamp());
						teamDetails.store();
						results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "TeamUpdatedSuccessfully", locale));
					}else{	
						GenericValue teamDesc = EntityUtil.getFirst(delegator.findByAnd("EmplTeam", UtilMisc.toMap("teamName",teamName), null, false));
						if(UtilValidate.isNotEmpty(teamDesc))
							return ServiceUtil.returnError("Team already exists");
						else{
							teamDetails.put("teamName", teamName);
							teamDetails.put("isActive", isActive);
							/*teamDetails.put("businessUnit", buId);*/
						 	userLoginId = userLogin.getString("userLoginId");
				            teamDetails.put("modifiedBy", userLoginId);
				            teamDetails.put("modifiedOn", UtilDateTime.nowTimestamp());
							teamDetails.store();
							results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "TeamUpdatedSuccessfully", locale));
						}
					}
					if(statusChanged && isTeamUpdatePartyRelationship){
						DataHelper.removeOrAddTeamMembersPartyRelationship(emplTeamId, isActive.equals("N"), delegator, userLogin, dispatcher);
					}
				}
			}
			results.put("emplTeamId", emplTeamId);
		} catch (GeneralException e) {
			Debug.log("==error in updateTeam===" + e.getMessage());
		}
		return results;
	}
	/* for removing members */
	public static Map <String, Object> removeMember(DispatchContext dctx, Map <String, Object> context) {
        Delegator delegator = dctx.getDelegator();
        Security security = dctx.getSecurity();
        LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
        Map<String, Object> results = new HashMap<String, Object>();
        String emplTeamId = (String) context.get("emplTeamId");
        String newPartyId = (String) context.get("newPartyId");
        String newPartyIds = (String) context.get("newPartyIds");
       try {
           boolean isTeamUpdatePartyRelationship = org.fio.homeapps.util.DataUtil.getGlobalValue(delegator, "IS_TEAM_UPD_PTY_RLTNSP","N").equals("Y");
           List < String > memberList = new ArrayList < String > ();
           if (UtilValidate.isNotEmpty(newPartyIds)) {
               JSONArray jsonArray = JSONArray.fromObject(newPartyIds);
               if (jsonArray != null && jsonArray.size() > 0) {
                   for (int i = 0; i < jsonArray.size(); i++) {
                	   memberList.add(jsonArray.getJSONObject(i).getString("newPartyId"));
                   }
               }
           }
           if (UtilValidate.isNotEmpty(newPartyId)) {
        	   memberList.add(newPartyId);
           }
           if (UtilValidate.isNotEmpty(memberList) && UtilValidate.isNotEmpty(emplTeamId)) {
               for (int j = 0; j < memberList.size(); j++) {
                   String teamPartyId = memberList.get(j);
                   GenericValue emplPositionFulFillment = EntityQuery.use(delegator).from("EmplPositionFulfillment").where("emplTeamId",emplTeamId,"partyId",teamPartyId).queryFirst();
                   if(UtilValidate.isNotEmpty(emplPositionFulFillment)) {
                	   emplPositionFulFillment.put("thruDate", UtilDateTime.nowTimestamp());
                	   emplPositionFulFillment.store();
                	   
                	   if(isTeamUpdatePartyRelationship) {
       					DataHelper.expireTeamMemberPartyRelationship(emplPositionFulFillment.getString("partyId"), teamPartyId ,delegator, userLogin, dispatcher);
       				}
                   }
               }
               results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "TeamAndEmployeeAssociationDeletedSuccessfully", locale));
           }
           
       } catch (Exception e) {
           e.printStackTrace();
           results = ServiceUtil.returnSuccess("Error : "+e.getMessage());
       }
       results.put("emplTeamId", emplTeamId);
       return results;
    }
	/* for adding members */
	 /* public static Map <String, Object> addMembers(DispatchContext dctx, Map <String, Object> context) {
    	 Delegator delegator = dctx.getDelegator();
         Locale locale = (Locale) context.get("locale");
         Map<String, Object> results = new HashMap<String, Object>();
         String emplTeamId = (String) context.get("emplTeamId");
         String newPartyId = (String) context.get("newPartyId");
         try {
        	 List < String > newPartyIdList = new ArrayList < String > ();
			 if (UtilValidate.isNotEmpty(newPartyId)) { 
				 JSONArray jsonArray = JSONArray.fromObject(newPartyId);
				 if (jsonArray != null && jsonArray.size()> 0){
					 for (int i = 0; i < jsonArray.size(); i++) {
						  newPartyIdList.add(jsonArray.getJSONObject(i).getString("partyId")); 
					  } 
				  }
			  }
            if (UtilValidate.isNotEmpty(newPartyIdList) && UtilValidate.isNotEmpty(emplTeamId)) {
            	for (int j = 0; j < newPartyIdList.size(); j++) {
	                String teamPartyId = newPartyIdList.get(j);
	                GenericValue teamEmpPosAssoc = EntityQuery.use(delegator).from("EmplPosition").where("partyId",teamPartyId).queryFirst();
	                if(UtilValidate.isNotEmpty(teamEmpPosAssoc)) {
	                	String posId=teamEmpPosAssoc.getString("emplPositionId");
	                    if(UtilValidate.isNotEmpty(posId)){	 
	                    	GenericValue teamEmp = delegator.makeValue("EmplPositionFulfillment");
	                    	teamEmp.put("emplPositionId", posId);
	                    	teamEmp.put("partyId", teamPartyId);
	                    	teamEmp.put("fromDate", UtilDateTime.nowTimestamp());
	                    	teamEmp.put("emplTeamId", emplTeamId);
	                    	teamEmp.put("isTeamLead", "N");
	                    	teamEmp.create();
	                    }
	                }
                }
                results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "TeamUserAssociationCreatedSuccessfully", locale));
            }
        } catch (Exception e) {
            e.printStackTrace();
            results = ServiceUtil.returnSuccess("Error : "+e.getMessage());
        }
        results.put("emplTeamId", emplTeamId);
        return results;
    }*/
	 /* for adding members */
	 /* public static Map <String, Object> addLeaders(DispatchContext dctx, Map <String, Object> context) {
    	 Delegator delegator = dctx.getDelegator();
         Locale locale = (Locale) context.get("locale");
         Map<String, Object> results = new HashMap<String, Object>();
         String emplTeamId = (String) context.get("emplTeamId");
         String newPartyId = (String) context.get("newPartyIdLeader");
         try {
        	 List < String > newPartyIdList = new ArrayList < String > ();
			 if (UtilValidate.isNotEmpty(newPartyId)) { 
				 JSONArray jsonArray = JSONArray.fromObject(newPartyId);
				 if (jsonArray != null && jsonArray.size()> 0){
					 for (int i = 0; i < jsonArray.size(); i++) {
						  newPartyIdList.add(jsonArray.getJSONObject(i).getString("partyId")); 
					  } 
				  }
			  }
            if (UtilValidate.isNotEmpty(newPartyIdList) && UtilValidate.isNotEmpty(emplTeamId)) {
            	for (int j = 0; j < newPartyIdList.size(); j++) {
	                String teamPartyId = newPartyIdList.get(j);
	                GenericValue teamEmpPosAssoc = EntityQuery.use(delegator).from("EmplPosition").where("partyId",teamPartyId).queryFirst();
	                if(UtilValidate.isNotEmpty(teamEmpPosAssoc)) {
	                	String posId=teamEmpPosAssoc.getString("emplPositionId");
	                    if(UtilValidate.isNotEmpty(posId)){	 
	                    	GenericValue teamEmp = delegator.makeValue("EmplPositionFulfillment");
	                    	teamEmp.put("emplPositionId", posId);
	                    	teamEmp.put("partyId", teamPartyId);
	                    	teamEmp.put("fromDate", UtilDateTime.nowTimestamp());
	                    	teamEmp.put("emplTeamId", emplTeamId);
	                    	teamEmp.put("isTeamLead", "Y");
	                    	teamEmp.create();
	                    }
	                }
                }
                results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "TeamLeaderAssociationCreatedSuccessfully", locale));
            }
        } catch (Exception e) {
            e.printStackTrace();
            results = ServiceUtil.returnSuccess("Error : "+e.getMessage());
        }
        results.put("emplTeamId", emplTeamId);
        return results;
    }*/
	 
		/* for updating  team role */
	 public static Map <String, Object> updateTeamRole(DispatchContext dctx, Map <String, Object> context) {
    	 Delegator delegator = dctx.getDelegator();
         Locale locale = (Locale) context.get("locale");
         Map<String, Object> results = new HashMap<String, Object>();
         String emplTeamId = (String) context.get("emplTeamId");
         String newPartyId = (String) context.get("partyUser");
         String role = (String) context.get("roleofteam");
         try {
        	 GenericValue team =  EntityUtil.getFirst(delegator.findByAnd("EmplPositionFulfillment", UtilMisc.toMap("partyId", newPartyId,"emplTeamId",emplTeamId,"thruDate",null), null, false));
        	 if(UtilValidate.isNotEmpty(team)){
 				//String roleType=team.getString("isTeamLead");
 				if(UtilValidate.isNotEmpty(role)&&role.equalsIgnoreCase("N")) 
 				{
 					team.put("isTeamLead", "N");
 					team.store();
 					results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "TeamRoleUpdatedSuccessfully", locale));
 				}
 				else if(UtilValidate.isNotEmpty(role)&&role.equalsIgnoreCase("Y"))  
 				{
 					team.put("isTeamLead", "Y");
 					team.store();
 					results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "TeamRoleUpdatedSuccessfully", locale));
 				}
 			}
        }
         catch (Exception e) {
            e.printStackTrace();
            results = ServiceUtil.returnSuccess("Error : "+e.getMessage());
        }
        results.put("emplTeamId", emplTeamId);
        return results;
    }
	 /*For adding members*/
	 public static Map <String, Object> addMembers(DispatchContext dctx, Map <String, Object> context) {
    	 Delegator delegator = dctx.getDelegator();
         Locale locale = (Locale) context.get("locale");
         Map<String, Object> results = new HashMap<String, Object>();
         String emplTeamId = (String) context.get("emplTeamId");
         String teamPartyId = (String) context.get("newPartyId");
         String throughDate=null;
         String position=null;
         String userToTeam="expired";
		 String partyId=null;
		 //String teamPartyId =null;
		 //String userAdded=null;
		 //Debug.log("newPartyId=="+newPartyId);
         try {
        	 /*List < String > newPartyIdList = new ArrayList < String > ();
			 if (UtilValidate.isNotEmpty(newPartyId)) { 
				 JSONArray jsonArray = JSONArray.fromObject(newPartyId);
				 if (jsonArray != null && jsonArray.size()> 0){
					 for (int i = 0; i < jsonArray.size(); i++) {
						  newPartyIdList.add(jsonArray.getJSONObject(i).getString("partyId")); 
					  } 
				  }
			  }*/
		 if (UtilValidate.isNotEmpty(teamPartyId) && UtilValidate.isNotEmpty(emplTeamId)) {
            	//Debug.log("inside loop");
            	//for (int j = 0; j < newPartyIdList.size(); j++) {
	              //  teamPartyId = newPartyIdList.get(j);
	            	// GenericValue teamInEmpl =  EntityUtil.getFirst(delegator.findByAnd("EmplPositionFulfillment", UtilMisc.toMap("partyId", teamPartyId,"emplTeamId",emplTeamId), null, false));
	            	 List<GenericValue> teamInEmpl = EntityQuery.use(delegator).from("EmplPositionFulfillment").where("emplTeamId", emplTeamId,"partyId", teamPartyId).queryList();
	            	 if(UtilValidate.isNotEmpty(teamInEmpl))
	 				{
	            		 for(GenericValue userTeams : teamInEmpl)
	            		 {
	            			 	throughDate=userTeams.getString("thruDate");
	            			 	position=userTeams.getString("emplPositionId");
								if(UtilValidate.isEmpty(throughDate)) {
	 			                		userToTeam="true";
	 			                		//Debug.log("user is active,already associated");
	 			                		break;
	 			                	}
	            		 }
	            		 if(userToTeam=="expired")
	 			         {
		                		if(UtilValidate.isNotEmpty(position))
		                		{
		                		 	GenericValue teamEmpAdd = delegator.makeValue("EmplPositionFulfillment");
		                		 	teamEmpAdd.put("emplPositionId", position);
		                		 	teamEmpAdd.put("partyId", teamPartyId);
		                		 	teamEmpAdd.put("fromDate", UtilDateTime.nowTimestamp());
		                		 	teamEmpAdd.put("emplTeamId", emplTeamId);
		                		 	teamEmpAdd.put("isTeamLead", "N");
		                		 	teamEmpAdd.create();
			                		userToTeam="false";
			                		Debug.log("creation success with expired position Id");
		                		}
	 			        }
	            		 
					}/*Record is empty in position fulfillment table*/
					else {
	 						
	 						
			                	String posId = delegator.getNextSeqId("EmplPosition");
			                	Debug.log("posId=="+posId);
			                	GenericValue teamPos = delegator.makeValue("EmplPosition");
			                	teamPos.put("emplPositionId", posId);
			                	teamPos.create();
			                	//Debug.log("creation success for fulfillment");
			                	GenericValue teamEmp = delegator.makeValue("EmplPositionFulfillment");
			                	teamEmp.put("emplPositionId", posId);
		                		teamEmp.put("partyId", teamPartyId);
		                		teamEmp.put("fromDate", UtilDateTime.nowTimestamp());
		                		teamEmp.put("emplTeamId", emplTeamId);
		                		teamEmp.put("isTeamLead", "N");
		                		teamEmp.create();
		                		userToTeam="false";
		                		Debug.log("creation success with new posID");
	 						
	 					}
            //}
	}
	if(userToTeam=="true") {
		results = ServiceUtil.returnFailure( "User is already associated with this Team");
    }
     else if(userToTeam=="false")
    {
    	 results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "TeamUserAssociationCreatedSuccessfully", locale));
    }
 }
         catch (Exception e) {
            e.printStackTrace();
            results = ServiceUtil.returnSuccess("Error : "+e.getMessage());
        }
        results.put("emplTeamId", emplTeamId);
        return results;
}
	 
	 
	 /*For adding leaders*/
	 public static Map <String, Object> addLeaders(DispatchContext dctx, Map <String, Object> context) {
    	 Delegator delegator = dctx.getDelegator();
         Locale locale = (Locale) context.get("locale");
         Map<String, Object> results = new HashMap<String, Object>();
         String emplTeamId = (String) context.get("emplTeamId");
         String teamPartyId = (String) context.get("newPartyIdLeader");
         String throughDate=null;
         String position=null;
         String userToTeam="expired";
		 String partyId=null;
		// String teamPartyId =null;
		 //String userAdded=null;
         try {
        	/* List < String > newPartyIdList = new ArrayList < String > ();
			 if (UtilValidate.isNotEmpty(newPartyId)) { 
				 JSONArray jsonArray = JSONArray.fromObject(newPartyId);
				 if (jsonArray != null && jsonArray.size()> 0){
					 for (int i = 0; i < jsonArray.size(); i++) {
						  newPartyIdList.add(jsonArray.getJSONObject(i).getString("partyId")); 
					  } 
				  }
			  }*/
		 if (UtilValidate.isNotEmpty(teamPartyId) && UtilValidate.isNotEmpty(emplTeamId)) {
            	//Debug.log("inside loop");
            	//for (int j = 0; j < newPartyIdList.size(); j++) {
	                //teamPartyId = newPartyIdList.get(j);
	            	// GenericValue teamInEmpl =  EntityUtil.getFirst(delegator.findByAnd("EmplPositionFulfillment", UtilMisc.toMap("partyId", teamPartyId,"emplTeamId",emplTeamId), null, false));
	            	 List<GenericValue> teamInEmpl = EntityQuery.use(delegator).from("EmplPositionFulfillment").where("emplTeamId", emplTeamId,"partyId", teamPartyId).queryList();
	            	 if(UtilValidate.isNotEmpty(teamInEmpl))
	 				{
	            		 for(GenericValue userTeams : teamInEmpl)
	            		 {
	            			 	throughDate=userTeams.getString("thruDate");
	            			 	position=userTeams.getString("thruDate");
								if(UtilValidate.isEmpty(throughDate)) {
	 			                		userToTeam="true";
	 			                		//Debug.log("user is active,already associated");
	 			                		break;
	 			                	}
	            		 }
	            		 if(userToTeam=="expired") 
	 			         {
		                		if(UtilValidate.isNotEmpty(position))
		                		{
		                		 	GenericValue teamEmpAdd = delegator.makeValue("EmplPositionFulfillment");
		                		 	teamEmpAdd.put("emplPositionId", position);
		                		 	teamEmpAdd.put("partyId", teamPartyId);
		                		 	teamEmpAdd.put("fromDate", UtilDateTime.nowTimestamp());
		                		 	teamEmpAdd.put("emplTeamId", emplTeamId);
		                		 	teamEmpAdd.put("isTeamLead", "Y");
		                		 	teamEmpAdd.create();
			                		userToTeam="false";
			                		//Debug.log("creation success with expired position Id");
		                		}
	 			        }
	            		 
					}/*Record is empty in position fulfillment table*/
					else {
	 						
	 						GenericValue teamPos = delegator.makeValue("EmplPosition");
			                	String posId = delegator.getNextSeqId("EmplPosition");
			                	teamPos.put("emplPositionId", posId);
			                	teamPos.create();
			                	//Debug.log("creation success for fulfillment");
			                	GenericValue teamEmp = delegator.makeValue("EmplPositionFulfillment");
			                	teamEmp.put("emplPositionId", posId);
		                		teamEmp.put("partyId", teamPartyId);
		                		teamEmp.put("fromDate", UtilDateTime.nowTimestamp());
		                		teamEmp.put("emplTeamId", emplTeamId);
		                		teamEmp.put("isTeamLead", "Y");
		                		teamEmp.create();
		                		userToTeam="false";
		                		Debug.log("creation success with new posID");
	 						
	 					}
            }
	//}
	if(userToTeam=="true") {
		results = ServiceUtil.returnFailure( "User is already associated with this Team");
    }
     else if(userToTeam=="false")
    {
    	 results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "TeamUserAssociationCreatedSuccessfully", locale));
    }
 }
         catch (Exception e) {
            e.printStackTrace();
            results = ServiceUtil.returnSuccess("Error : "+e.getMessage());
        }
        results.put("emplTeamId", emplTeamId);
        return results;
}
}