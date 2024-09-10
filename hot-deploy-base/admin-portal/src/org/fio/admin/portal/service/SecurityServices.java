package org.fio.admin.portal.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpSession;

import org.fio.admin.portal.constant.AdminPortalConstant.AccessLevel;
import org.fio.admin.portal.constant.AdminPortalConstant.GlobalParameter;
import org.fio.admin.portal.constant.AdminPortalConstant.SecurityType;
import org.fio.admin.portal.util.DataUtil;
import org.fio.admin.portal.util.EnumUtil;
import org.fio.homeapps.constants.GlobalConstants;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import net.sf.json.JSONArray;

/**
 * 
 * @author Mahendran
 * @since 04-08-2019
 * 
 */
public class SecurityServices {
    private SecurityServices() {}
    private static final String MODULE = SecurityServices.class.getName();
    public static final String RESOURCE = "AdminPortalUiLabels";

    public static Map < String, Object > createSecurityGroup(DispatchContext dctx, Map < String, Object > context) {
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Security security = dctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
        Map < String, Object > results = ServiceUtil.returnSuccess();
        String groupId = (String) context.get("groupId");
        String securityType = (String) context.get("securityTypeId");
        String description = (String) context.get("description");
        try {
            GenericValue securityGroup = EntityQuery.use(delegator).from("SecurityGroup").where("groupId", groupId).queryOne();
            if (UtilValidate.isNotEmpty(securityGroup)) {
                results = ServiceUtil.returnError(UtilProperties.getMessage(RESOURCE, "SecurityGroupAlreadyExists", locale));
            } else {
                securityGroup = delegator.makeValue("SecurityGroup");
                securityGroup.set("groupId", groupId);
                securityGroup.set("securityTypeId", securityType);
                securityGroup.set("description", description);
                securityGroup.create();
                results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "SecurityGroupCreatedSuccessfully", locale));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Debug.logError(e.getMessage(), MODULE);
            results = ServiceUtil.returnError("Error : " + e.getMessage());
            results.put("groupId", groupId);
            return results;
        }
        results.put("groupId", groupId);
        return results;
    }
    public static Map < String, Object > updateSecurityGroup(DispatchContext dctx, Map < String, Object > context) {
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Security security = dctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
        Map < String, Object > results = ServiceUtil.returnSuccess();
        String groupId = (String) context.get("groupId");
        String securityType = (String) context.get("securityTypeId");
        String description = (String) context.get("description");
        String action = "";
        try {
            GenericValue securityGroup = EntityQuery.use(delegator).from("SecurityGroup").where("groupId", groupId).queryOne();
            if (UtilValidate.isEmpty(securityGroup)) {
                results = ServiceUtil.returnError(UtilProperties.getMessage(RESOURCE, "SecurityGroupNotExists", locale));
            } else {
                securityGroup.set("description", description);
                securityGroup.set("securityTypeId", securityType);
                securityGroup.store();
                results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "SecurityGroupUpdatedSuccessfully", locale));
            } 
        } catch (Exception e) {
            e.printStackTrace();
            Debug.logError(e.getMessage(), MODULE);
            results = ServiceUtil.returnError("Error : " + e.getMessage());
            results.put("groupId", groupId);
            return results;
        }
        results.put("groupId", groupId);
        return results;
    }
    public static Map < String, Object > createSecurityPermission(DispatchContext dctx, Map < String, Object > context) {
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Security security = dctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
        Map < String, Object > results = ServiceUtil.returnSuccess();
        String permissionId = (String) context.get("permissionId");
        String description = (String) context.get("description");
        try {
            GenericValue securityPermission = EntityQuery.use(delegator).from("SecurityPermission").where("permissionId", permissionId).queryOne();
            if (UtilValidate.isNotEmpty(securityPermission)) {
                results = ServiceUtil.returnError(UtilProperties.getMessage(RESOURCE, "SecurityPermissionAlreadytExists", locale));
            } else {
                securityPermission = delegator.makeValue("SecurityPermission");
                securityPermission.set("permissionId", permissionId);
                securityPermission.set("description", description);
                securityPermission.create();
                results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "SecurityPermissionCreatedSuccessfully", locale));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Debug.logError(e.getMessage(), MODULE);
            results = ServiceUtil.returnError("Error : " + e.getMessage());
            results.put("permissionId", permissionId);
            return results;
        }
        results.put("permissionId", permissionId);
        return results;
    }
    public static Map < String, Object > updateSecurityPermission(DispatchContext dctx, Map < String, Object > context) {
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Security security = dctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
        Map < String, Object > results = ServiceUtil.returnSuccess();
        String permissionId = (String) context.get("permissionId");
        String description = (String) context.get("description");
        try {
            GenericValue securityPermission = EntityQuery.use(delegator).from("SecurityPermission").where("permissionId", permissionId).queryOne();
            if (UtilValidate.isEmpty(securityPermission)) {
                results = ServiceUtil.returnError(UtilProperties.getMessage(RESOURCE, "SecurityPermissionNotExists", locale));
            } else {
                securityPermission.set("description", description);
                securityPermission.store();
                results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "SecurityPermissionUpdatedSuccessfully", locale));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Debug.logError(e.getMessage(), MODULE);
            results = ServiceUtil.returnError("Error : " + e.getMessage());
            results.put("permissionId", permissionId);
            return results;
        }
        results.put("permissionId", permissionId);
        return results;
    }
    public static Map <String, Object> configureOperationLevelService(DispatchContext dctx, Map <String, Object> context) {
    	Delegator delegator = dctx.getDelegator();
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Security security = dctx.getSecurity();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	Locale locale = (Locale) context.get("locale");
    	Map < String, Object > results = ServiceUtil.returnSuccess();
    	String roleTypeId = (String) context.get("roleTypeId");
    	String entityName = (String) context.get("entityName");
    	String userLoginId = userLogin.getString("userLoginId");
    	String responseMessage = UtilProperties.getMessage(RESOURCE, "EntityOperationSecuritySuccessfullyConfigured", locale);
    	try {
    		if(UtilValidate.isNotEmpty(roleTypeId) && UtilValidate.isNotEmpty(entityName)) {
    			String securityPrefix = EntityUtilProperties.getPropertyValue("admin-portal.properties", "ops.security.group.prefix", delegator);
    			String securitySuffix = EntityUtilProperties.getPropertyValue("admin-portal.properties", "ops.security.group.suffix", delegator);
    			String securityGroupId = UtilValidate.isNotEmpty(securityPrefix) ? securityPrefix+"_"+ roleTypeId : roleTypeId;
    			securityGroupId = securityGroupId + (UtilValidate.isNotEmpty(securitySuffix) ? "_" +securitySuffix :"");
    			String securityGroupDesc = "Security Group for " + roleTypeId +" role";
    			List<GenericValue> toBeStore = new LinkedList<GenericValue>();
    			String permissionId = "";
    			List<EntityCondition> conditions = new ArrayList<EntityCondition>();
    			conditions.add(EntityCondition.makeCondition("roleTypeId",EntityOperator.EQUALS,roleTypeId));
    			conditions.add(EntityCondition.makeCondition("entityName",EntityOperator.EQUALS,entityName));
    			List<GenericValue> SecurityGroupOpLevelList = EntityQuery.use(delegator).from("SecurityGroupOpLevel").where(EntityCondition.makeCondition(conditions,EntityOperator.AND)).queryList();
    			if(UtilValidate.isNotEmpty(SecurityGroupOpLevelList)) {
    				delegator.removeAll(SecurityGroupOpLevelList);
    				// update message
    				responseMessage = UtilProperties.getMessage(RESOURCE, "EntityOperationSecuritySuccessfullyUpdated", locale);
    			} 
    			//Create Dynamic security group for the entity operation configuration
    			GenericValue securityGroup = EntityQuery.use(delegator).from("SecurityGroup").where("groupId",securityGroupId).queryFirst();
    			if(UtilValidate.isEmpty(securityGroup)) {
    				securityGroup = delegator.makeValue("SecurityGroup");
    				securityGroup.set("groupId", securityGroupId);
    				securityGroup.set("securityTypeId", SecurityType.OPS_SECURITY);
    				securityGroup.set("description", securityGroupDesc);
    				securityGroup.create();
    			}

    			List<String> permissionList = new ArrayList<String>();
    			Timestamp now = UtilDateTime.nowTimestamp();
    			List<GenericValue> operationList = EnumUtil.getEnums(delegator, "ENTITY_OPERATIONS");
    			if(UtilValidate.isNotEmpty(operationList)) {
    				for(GenericValue operationGv : operationList) {
    					String operationEnumId = operationGv.getString("enumId");
    					String operationDesc = operationGv.getString("description");
    					String fieldId = operationDesc.replace(" ", "").toLowerCase();
    					String requestParam = (String) context.get(fieldId);
    					if (UtilValidate.isNotEmpty(requestParam)) {
    						permissionId = entityName+"_"+operationEnumId;
    						GenericValue permission = EntityQuery.use(delegator).from("SecurityPermission").where("permissionId",permissionId).queryFirst();
    						if(UtilValidate.isNotEmpty(permission)) {
    							GenericValue securityGroupOpLevel = delegator.makeValue("SecurityGroupOpLevel", UtilMisc.toMap("securityGroupId", securityGroupId, "permissionId", permissionId , "roleTypeId", roleTypeId, 
    									"entityName", entityName, "operationName", operationDesc, "oplevel", requestParam, "createdOn", now, "createdBy", userLoginId, "modifiedOn", now, "modifiedBy", userLoginId));
    							toBeStore.add(securityGroupOpLevel);
    							permissionList.add(permissionId);
    						}
    					}
    				}
    				// store entity permission level 
    				if(UtilValidate.isNotEmpty(toBeStore))
    					delegator.storeAll(toBeStore);
    				//Associate the security permission to security group
    				if(UtilValidate.isNotEmpty(permissionList)) {
    					toBeStore = new ArrayList<GenericValue>();
    					for(String permission : permissionList) {
    						GenericValue securityGroupPermission = EntityQuery.use(delegator).from("SecurityGroupPermission").where("groupId",securityGroupId,"permissionId",permission).queryFirst();
    						if(UtilValidate.isEmpty(securityGroupPermission)) {
    							securityGroupPermission = delegator.makeValue("SecurityGroupPermission",UtilMisc.toMap("groupId",securityGroupId,"permissionId",permission));
    							toBeStore.add(securityGroupPermission);
    						}
    					}
    					if(UtilValidate.isNotEmpty(toBeStore))
    						delegator.storeAll(toBeStore);
    					
    					//Associate security group with role
    					GenericValue roleSecurityAssoc = EntityQuery.use(delegator).from("SecurityGroupRoleTypeAssoc").where("groupId",securityGroupId,"roleTypeId",roleTypeId).queryFirst();
    					if(UtilValidate.isEmpty(roleSecurityAssoc)) {
    						roleSecurityAssoc = delegator.makeValue("SecurityGroupRoleTypeAssoc",UtilMisc.toMap("groupId",securityGroupId,"roleTypeId",roleTypeId));
    						roleSecurityAssoc.create();
    					}
    				} 	
    			}
    		} 
    	} catch (Exception e) {
    		e.printStackTrace();
    		results = ServiceUtil.returnError("Error : "+e.getMessage());
    	}
    	return ServiceUtil.returnSuccess(responseMessage);
    }
    
    public static Map <String, Object> createRole(DispatchContext dctx, Map <String, Object> context) {
        Delegator delegator = dctx.getDelegator();
        Locale locale = (Locale) context.get("locale");
        String roleTypeId = (String) context.get("roleTypeId");
        String description = (String) context.get("description");
        String parentTypeId = (String) context.get("parentTypeId");
        Map<String, Object> results = new HashMap<String, Object>();
        try {
            if(UtilValidate.isEmpty(parentTypeId))
                parentTypeId = EntityUtilProperties.getPropertyValue("admin-portal.properties", "security.parent.role", "", delegator);
            GenericValue roleType = EntityQuery.use(delegator).from("RoleType").where("roleTypeId",roleTypeId).distinct().queryFirst();
            if(UtilValidate.isEmpty(roleType)) {
                roleType = delegator.makeValue("RoleType");
                roleType.set("roleTypeId", roleTypeId);
                roleType.set("description", description);
                roleType.set("parentTypeId", parentTypeId);
                roleType.set("hasTable", "N");
                roleType.create();
                results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "RoleSuccessfullyCreated", locale));
            } else {
                results = ServiceUtil.returnError(UtilProperties.getMessage(RESOURCE, "RoleAlreadyExists", locale));
                return results;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ServiceUtil.returnError("Error : "+e.getMessage());
        }
        results.put("roleTypeId", roleTypeId);
        return results;
    }
    public static Map <String, Object> updateRole(DispatchContext dctx, Map <String, Object> context) {
        Delegator delegator = dctx.getDelegator();
        Locale locale = (Locale) context.get("locale");
        String roleTypeId = (String) context.get("roleTypeId");
        String description = (String) context.get("description");
        Map<String, Object> results = new HashMap<String, Object>();
        try {
            GenericValue roleType = EntityQuery.use(delegator).from("RoleType").where("roleTypeId",roleTypeId).distinct().queryFirst();
            if(UtilValidate.isNotEmpty(roleType)) {
                roleType.set("description", description);
                roleType.store();
                results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "RoleHasBeenUpdatedSuccessfully", locale));
                results.put("roleTypeId", roleTypeId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            results = ServiceUtil.returnError("Error : "+e.getMessage());
        }
        results.put("roleTypeId", roleTypeId);
        return results;
    }
    public static Map <String, Object> removeRole(DispatchContext dctx, Map <String, Object> context) {
        Delegator delegator = dctx.getDelegator();
        Locale locale = (Locale) context.get("locale");
        String roleTypeId = (String) context.get("roleTypeId");
        String responseMessage = UtilProperties.getMessage(RESOURCE, "RoleHasBeenDeletedSuccessfully", locale);
        try {
        	List <String> roleList = new ArrayList <String>();
        	if (UtilValidate.isNotEmpty(roleTypeId)) {
                JSONArray jsonArray = JSONArray.fromObject(roleTypeId);
                if (jsonArray != null && jsonArray.size() > 0)
                    for (int i = 0; i < jsonArray.size(); i++) {
                    	roleList.add(jsonArray.getJSONObject(i).getString("roleTypeId"));
                    }
            }
            for (String role : roleList) {
                GenericValue roleType = EntityQuery.use(delegator).from("RoleType").where("roleTypeId",role).distinct().queryFirst();
                if(UtilValidate.isNotEmpty(roleType)) {
                    roleType.remove();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ServiceUtil.returnError("Error : "+e.getMessage());
        }
        return ServiceUtil.returnSuccess(responseMessage);
    }
    public static Map <String, Object> createRoleSecurityAssoc(DispatchContext dctx, Map <String, Object> context) {
    	 Delegator delegator = dctx.getDelegator();
         Locale locale = (Locale) context.get("locale");
         Map<String, Object> results = new HashMap<String, Object>();
         String roleTypeId = (String) context.get("roleTypeId");
         String groupId = (String) context.get("groupId");
         String groupIds = (String) context.get("groupIds");
        try {
        	List < String > groupList = new ArrayList < String > ();
            if (UtilValidate.isNotEmpty(groupIds)) {
                JSONArray jsonArray = JSONArray.fromObject(groupIds);
                if (jsonArray != null && jsonArray.size() > 0)
                    for (int i = 0; i < jsonArray.size(); i++) {
                    	groupList.add(jsonArray.getJSONObject(i).getString("groupId"));
                    }
            }
            if (UtilValidate.isNotEmpty(groupId)) {
            	groupList.add(groupId);
            }
            if (UtilValidate.isNotEmpty(groupList) && UtilValidate.isNotEmpty(roleTypeId)) {
                for (int j = 0; j < groupList.size(); j++) {
                    String securityGroupId = groupList.get(j);
                    GenericValue roleSecurityAssoc = EntityQuery.use(delegator).from("SecurityGroupRoleTypeAssoc").where("groupId",securityGroupId,"roleTypeId",roleTypeId).queryFirst();
                    if(UtilValidate.isEmpty(roleSecurityAssoc)) {
                        roleSecurityAssoc = delegator.makeValue("SecurityGroupRoleTypeAssoc",UtilMisc.toMap("groupId",securityGroupId,"roleTypeId",roleTypeId));
                        roleSecurityAssoc.create();
                    }
                }
                results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "SecurityAndRoleAssociationCreatedSuccessfully", locale));
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            results = ServiceUtil.returnError("Error : "+e.getMessage());
        }
        results.put("roleTypeId", roleTypeId);
        return results;
    }
    public static Map <String, Object> removeRoleSecurityAssoc(DispatchContext dctx, Map <String, Object> context) {
        Delegator delegator = dctx.getDelegator();
        Locale locale = (Locale) context.get("locale");
        Map<String, Object> results = new HashMap<String, Object>();
        String roleTypeId = (String) context.get("roleTypeId");
        String groupId = (String) context.get("groupId");
        String groupIds = (String) context.get("groupIds");
       try {
       	List < String > groupList = new ArrayList < String > ();
           if (UtilValidate.isNotEmpty(groupIds)) {
               JSONArray jsonArray = JSONArray.fromObject(groupIds);
               if (jsonArray != null && jsonArray.size() > 0)
                   for (int i = 0; i < jsonArray.size(); i++) {
                   	groupList.add(jsonArray.getJSONObject(i).getString("groupId"));
                   }
           }
           if (UtilValidate.isNotEmpty(groupId)) {
           	groupList.add(groupId);
           }
           if (UtilValidate.isNotEmpty(groupList) && UtilValidate.isNotEmpty(roleTypeId)) {
               for (int j = 0; j < groupList.size(); j++) {
                   String securityGroupId = groupList.get(j);
                   GenericValue roleSecurityAssoc = EntityQuery.use(delegator).from("SecurityGroupRoleTypeAssoc").where("groupId",securityGroupId,"roleTypeId",roleTypeId).queryFirst();
                   if(UtilValidate.isNotEmpty(roleSecurityAssoc)) {
                       roleSecurityAssoc.remove();
                   }
               }
               results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "SecurityAndRoleAssociationDeletedSuccessfully", locale));
           }
           
       } catch (Exception e) {
           e.printStackTrace();
           results = ServiceUtil.returnError("Error : "+e.getMessage());
       }
       results.put("roleTypeId", roleTypeId);
       return results;
    }
    @SuppressWarnings("unchecked")
	public static Map<String, Object> getAccessMatrixInfo(DispatchContext dctx, Map<String, Object> context) {
        Delegator delegator = dctx.getDelegator();
        Locale locale = (Locale) context.get("locale");
        Map<String, Object> results = new HashMap<String, Object>();
        String userLoginId = (String) context.get("userLoginId");
        String entityName = (String) context.get("entityName");
        String modeOfOperation = (String) context.get("modeOfOp");
        String businessUnit = (String) context.get("businessUnit");
        String teamId = (String) context.get("teamId");
        String roleTypeId = (String) context.get("roleTypeId");
        String partyId = "";
        try {
            String isSecurityEnable = "Y";
            GenericValue securityGlobal = EntityQuery.use(delegator).from("PretailLoyaltyGlobalParameters").where("parameterId",GlobalParameter.IS_SECURITY_MATRIX_ENABLE).queryFirst();
            if(UtilValidate.isNotEmpty(securityGlobal)) {
                isSecurityEnable = securityGlobal.getString("value");
            }
            if(!"Y".equals(isSecurityEnable)) {
                Map<String, Object> dataMap = new LinkedHashMap<String, Object>();
                dataMap.put("bu_info", new LinkedList<Map<String,Object>>());
                dataMap.put("owner_access_only", "");
                dataMap.put("access_level", AccessLevel.ALL);
                dataMap.put("mode", modeOfOperation);
                dataMap.put("entity_name", entityName);
                results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "UserHasSecurityLevel", locale));
                results.put("opLevel", AccessLevel.LEVEL6);
                results.put("securityLevelInfo", DataUtil.convertToJson(dataMap));
                return results;
            }
            if(UtilValidate.isEmpty(userLoginId)) {
                results = ServiceUtil.returnError(UtilProperties.getMessage(RESOURCE, "AMUserLoginIdIsEmpty", locale));
                results.put("errorCode", "E3001"); // User login id is empty
                return results;
            }
            if(UtilValidate.isEmpty(entityName)) {
                results = ServiceUtil.returnError(UtilProperties.getMessage(RESOURCE, "AMEntityNameIsEmpty", locale));
                results.put("errorCode", "E3002"); // Entity name is empty
                return results;
            }
            if(UtilValidate.isEmpty(modeOfOperation)) {
                results = ServiceUtil.returnError(UtilProperties.getMessage(RESOURCE, "AMModeOfOperationIsEmpty", locale));
                results.put("errorCode", "E3003"); //Mode of operation is empty
                return results;
            }
            //Validate the user is valid user or not
            Map<String, Object> isValid = DataUtil.isValidUser(delegator, context);
            if(!ServiceUtil.isSuccess(isValid)) {
                results = ServiceUtil.returnError(UtilProperties.getMessage(RESOURCE, "AMInvalidUser", locale));
                results.put("errorCode", "E3005"); //User not authenticated
                return results;
            } else {
                partyId = (String) isValid.get("partyId");
            }
            //Validate entity exists or not
            /*
            ModelEntity modelEntity = delegator.getModelEntity(entityName);
            if(UtilValidate.isEmpty(modelEntity)) {
                results = ServiceUtil.returnError(UtilProperties.getMessage(RESOURCE, "AMInvalidEntity", locale));
                results.put("errorCode", "E3006"); // Invalid entity name
                return results;
            }*/
            
            //Validate operation is exists or not
            GenericValue entityOperation = EntityQuery.use(delegator).from("EntityOperationConfig").where("entityName",entityName,"operationName",modeOfOperation).queryFirst();
            if(UtilValidate.isEmpty(entityOperation)) {
            	entityOperation = EntityQuery.use(delegator).from("EntityOperationConfig").where("entityAliasName",entityName,"operationName",modeOfOperation).queryFirst();
            	if(UtilValidate.isEmpty(entityOperation)) {
	                results = ServiceUtil.returnError(UtilProperties.getMessage(RESOURCE, "AMInvalidEntityOperation", locale));
	                results.put("errorCode", "E3007"); // Invalid entity operation
	                return results;
            	}
            }
            if(UtilValidate.isEmpty(businessUnit)) {
                if(UtilValidate.isNotEmpty(teamId)) {
                    GenericValue emplTeam = EntityQuery.use(delegator).select("businessUnit").from("EmplTeam").where("emplTeamId",teamId).queryFirst();
                    if(UtilValidate.isNotEmpty(emplTeam)) {
                        businessUnit = emplTeam.getString("businessUnit");
                    } 
                } else {
                    GenericValue person = EntityQuery.use(delegator).select("businessUnit").from("Person").where("partyId",partyId).queryFirst();
                    if(UtilValidate.isNotEmpty(person)) {
                        businessUnit = person.getString("businessUnit");
                    } 
                }
                if(UtilValidate.isEmpty(businessUnit)) {
                    results = ServiceUtil.returnError(UtilProperties.getMessage(RESOURCE, "AMBusinessUnitIsEmpty", locale));
                    results.put("errorCode", "E3004"); //Business unit is empty
                    return results;
                }
            }
            //Validate the business unit if it's exists
            //if(UtilValidate.isNotEmpty(businessUnit)) {
                EntityCondition condition = EntityCondition.makeCondition(EntityOperator.OR,
                    EntityCondition.makeCondition("productStoreGroupName",EntityOperator.EQUALS,businessUnit),
                    EntityCondition.makeCondition("productStoreGroupId",EntityOperator.EQUALS,businessUnit),
                    EntityCondition.makeCondition("description",EntityOperator.EQUALS,businessUnit),
                    EntityCondition.makeCondition("externalId",EntityOperator.EQUALS,businessUnit));
                GenericValue productStoreGroup = EntityQuery.use(delegator).from("ProductStoreGroup").where(condition).filterByDate().queryFirst();
                if(UtilValidate.isEmpty(productStoreGroup)) {
                    results = ServiceUtil.returnError(UtilProperties.getMessage(RESOURCE, "AMInvalidBusinessUnit", locale));
                    results.put("errorCode", "E3008"); // Invalid business unit
                    return results;
                }
           //}
            Map<String, Object> fulfillmentCondition = new HashMap<String, Object>();
            fulfillmentCondition.put("partyId", partyId);
            fulfillmentCondition.put("businessUnit", businessUnit);
            if(UtilValidate.isNotEmpty(teamId)) {
                fulfillmentCondition.put("emplTeamId", teamId);
                GenericValue emplTeamFulfillment = EntityQuery.use(delegator).select("roleTypeId").from("EmplPositionFulfillment").where(fulfillmentCondition).filterByDate().queryFirst();
                if(UtilValidate.isNotEmpty(emplTeamFulfillment)) {
                    roleTypeId = emplTeamFulfillment.getString("roleTypeId");
                }
            }
            if(UtilValidate.isEmpty(roleTypeId)) {
                GenericValue emplTeamFulfillment = EntityQuery.use(delegator).select("roleTypeId").from("EmplPositionFulfillment").where("partyId", partyId,"businessUnit", businessUnit).filterByDate().queryFirst();
                if(UtilValidate.isNotEmpty(emplTeamFulfillment)) {
                    roleTypeId = emplTeamFulfillment.getString("roleTypeId");
                }
            }
            //get the primary role type id from party attribute table if role type id is not exists
            if(UtilValidate.isEmpty(roleTypeId)) {
                //validate the party and role
                String parentRoleType = EntityUtilProperties.getPropertyValue("admin-portal.properties", "security.parent.role", "", delegator);
                List<GenericValue> partyRole = DataUtil.getPartyRoles(delegator, partyId, parentRoleType);
                if(UtilValidate.isEmpty(partyRole)) {
                    results = ServiceUtil.returnError(UtilProperties.getMessage(RESOURCE, "AMPartyRoleNotExists", locale));
                    results.put("errorCode", "E3009"); // party role not exists
                    return results;
                } else {
                    roleTypeId = partyRole.get(0).getString("roleTypeId");
                }
            }
            
            
            //get the configured security info
            EntityCondition condition1 = EntityCondition.makeCondition(EntityOperator.AND,
                EntityCondition.makeCondition("roleTypeId",EntityOperator.EQUALS,roleTypeId),
                EntityCondition.makeCondition("entityName",EntityOperator.EQUALS,entityName),
                EntityCondition.makeCondition("operationName",EntityOperator.EQUALS,modeOfOperation));
            GenericValue securityGroupOpLevel = EntityQuery.use(delegator).from("SecurityGroupOpLevel").where(condition1).queryFirst();
            if(UtilValidate.isNotEmpty(securityGroupOpLevel)) {
                Map<String, Object> dataMap = new LinkedHashMap<String, Object>();
                Map<String, Object> buResult = new HashMap<String, Object>();
                List<Map<String, Object>> buInfo = new LinkedList<Map<String,Object>>();
                String ownerAccessOnly ="";
                String accessLevel = AccessLevel.YES;
                String oplevel = securityGroupOpLevel.getString("oplevel");
                buResult = validateBuAssociate(delegator, businessUnit, teamId, partyId, oplevel);
                
                if(AccessLevel.LEVEL1.equals(oplevel)) {
                    accessLevel = AccessLevel.NO;
                } else if(AccessLevel.LEVEL2.equals(oplevel)) {
                    ownerAccessOnly = AccessLevel.YES;
                    if(UtilValidate.isNotEmpty(buResult) && ServiceUtil.isSuccess(buResult)) {
                        buInfo = (List<Map<String, Object>>) buResult.get("buInfo");
                    } else {
                        accessLevel = AccessLevel.NO;
                    }
                } else if(AccessLevel.LEVEL3.equals(oplevel)) {
                    if(UtilValidate.isNotEmpty(buResult) && ServiceUtil.isSuccess(buResult)) {
                        buInfo = (List<Map<String, Object>>) buResult.get("buInfo");
                    } else {
                        accessLevel = AccessLevel.NO;
                    }
                } else if(AccessLevel.LEVEL4.equals(oplevel)) {
                    if(UtilValidate.isNotEmpty(buResult) && ServiceUtil.isSuccess(buResult)) {
                        buInfo = (List<Map<String, Object>>) buResult.get("buInfo");
                    } else {
                        accessLevel = AccessLevel.NO;
                    }
                } else if(AccessLevel.LEVEL5.equals(oplevel)) {
                    if(UtilValidate.isNotEmpty(buResult) && ServiceUtil.isSuccess(buResult)) {
                        buInfo = (List<Map<String, Object>>) buResult.get("buInfo");
                    } else {
                        accessLevel = AccessLevel.NO;
                    }
                } else if(AccessLevel.LEVEL6.equals(oplevel)) {
                    accessLevel = AccessLevel.ALL;
                }
                
                dataMap.put("bu_info", buInfo);
                dataMap.put("owner_access_only", ownerAccessOnly);
                dataMap.put("access_level", accessLevel);
                dataMap.put("mode", modeOfOperation);
                dataMap.put("entity_name", entityName);
               
                results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "UserHasSecurityLevel", locale));
                results.put("opLevel", oplevel);
                results.put("securityLevelInfo", DataUtil.convertToJson(dataMap));
            } else {
                results = ServiceUtil.returnError(UtilProperties.getMessage(RESOURCE, "AMSecurityRoleConfigurationEmpty", locale));
                results.put("errorCode", "E3010"); // security role configuration
                return results;
            }
        } catch (Exception e) {
            e.printStackTrace();
            results = ServiceUtil.returnError("Error : "+e.getMessage());
        }
        return results;
    }
    
    public static Map<String, Object> validateBuAssociate(Delegator delegator, String businessUnit, String teamId, String partyId, String oplevel) {
        Map<String, Object> results = new HashMap<String, Object>();
        List<Map<String, Object>> buInfo = new LinkedList<Map<String,Object>>();
        try {
            /*List<String> commonTeams = new ArrayList<String>();
            List<GenericValue> commonTeamList = EntityQuery.use(delegator).select("emplTeamId").from("EmplTeam").where("isCommon","Y").queryList();
            if(UtilValidate.isNotEmpty(commonTeamList)) {
                commonTeams = EntityUtil.getFieldListFromEntityList(commonTeamList, "emplTeamId", true);
            }
            */
            List<EntityCondition> conditions = new ArrayList<EntityCondition>();
            if(UtilValidate.isNotEmpty(teamId))
                conditions.add(EntityCondition.makeCondition("emplTeamId",EntityOperator.EQUALS,teamId));
            if(UtilValidate.isNotEmpty(businessUnit))
                conditions.add(EntityCondition.makeCondition("businessUnit",EntityOperator.EQUALS,businessUnit));
            
            conditions.add(EntityCondition.makeCondition(EntityOperator.OR,EntityCondition.makeCondition("isActive",EntityOperator.EQUALS,null),EntityCondition.makeCondition("isActive",EntityOperator.EQUALS,"Y")));
            EntityCondition condition = EntityCondition.makeCondition(conditions,EntityOperator.AND);
            List<GenericValue> emplTeams = EntityQuery.use(delegator).select("emplTeamId","businessUnit").from("EmplTeam").where(condition).queryList();
            List<String> teams = new ArrayList<String>();
            
            if(UtilValidate.isNotEmpty(emplTeams)) {
                teams = EntityUtil.getFieldListFromEntityList(emplTeams, "emplTeamId", true);
                businessUnit = emplTeams.get(0).getString("businessUnit");
               
                List<EntityCondition> conditions1 = new ArrayList<EntityCondition>();
                if(UtilValidate.isNotEmpty(partyId))
                    conditions1.add(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,partyId));
                if(AccessLevel.LEVEL2.equals(oplevel)) {
                    if(UtilValidate.isNotEmpty(teams))
                        conditions1.add(EntityCondition.makeCondition("emplTeamId",EntityOperator.IN,teams));
                    else {
                        Debug.logError("Error: For Level2 team is Empty", MODULE);
                        results.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                        results.put(ModelService.ERROR_MESSAGE, "AccessDenied");
                        return results;
                    }
                    EntityCondition mainCond = EntityCondition.makeCondition(conditions1, EntityOperator.AND);
                    List<GenericValue> emplFulfillment = EntityQuery.use(delegator).select("partyId","emplTeamId").from("EmplPositionFulfillment").where(mainCond).filterByDate().queryList();
                    if(UtilValidate.isEmpty(emplFulfillment)) {
                        Debug.logError("Error: For Level2 EmplPositionFulfillment is Empty", MODULE);
                        results.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                        results.put(ModelService.ERROR_MESSAGE, "AccessDenied");
                        return results;
                    }else {
                        List<GenericValue> emplTeam = EntityQuery.use(delegator).from("EmplTeam").where("emplTeamId",emplFulfillment.get(0).getString("emplTeamId"),"businessUnit",businessUnit).queryList();
                        if(UtilValidate.isNotEmpty(emplTeam)) {
                            Map<String, Object> buMap = new LinkedHashMap<String, Object>();
                            buMap.put("bu", businessUnit);
                            String teamId1 = emplTeam.get(0).getString("emplTeamId");
                            buMap.put("team_list", UtilMisc.toList(teamId1));
                            buInfo.add(buMap);
                        }
                        
                    }
                } else if(AccessLevel.LEVEL3.equals(oplevel)){
                    if(UtilValidate.isNotEmpty(teams))
                        conditions1.add(EntityCondition.makeCondition("emplTeamId",EntityOperator.IN,teams));
                    else {
                        Debug.logError("Error: For Level3 team is Empty", MODULE);
                        results.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                        results.put(ModelService.ERROR_MESSAGE, "AccessDenied");
                        return results;
                    }
                    EntityCondition mainCond = EntityCondition.makeCondition(conditions1, EntityOperator.AND);
                    List<GenericValue> emplFulfillment = EntityQuery.use(delegator).select("partyId","emplTeamId").from("EmplPositionFulfillment").where(mainCond).filterByDate().queryList();
                    if(UtilValidate.isEmpty(emplFulfillment)) {
                        Debug.logError("Error: For Level3 EmplPositionFulfillment is Empty", MODULE);
                        results.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                        results.put(ModelService.ERROR_MESSAGE, "AccessDenied");
                        return results;
                    }else {
                    	EntityCondition cond1 = EntityCondition.makeCondition(
                    				EntityOperator.AND,
                    				EntityCondition.makeCondition("emplTeamId", EntityOperator.IN, EntityUtil.getFieldListFromEntityList(emplFulfillment, "emplTeamId", true)),
                    				EntityCondition.makeCondition("businessUnit", EntityOperator.EQUALS, businessUnit)
                    			);
                        List<GenericValue> emplTeam = EntityQuery.use(delegator).from("EmplTeam").where(cond1).queryList();
                        if(UtilValidate.isNotEmpty(emplTeam)) {
                            Map<String, Object> buMap = new LinkedHashMap<String, Object>();
                            buMap.put("bu", businessUnit);
                            List<String> teamList = EntityUtil.getFieldListFromEntityList(emplTeam, "emplTeamId", true);
                            buMap.put("team_list", teamList);
                            buInfo.add(buMap);
                        }
                        
                    }
                
                } else if(AccessLevel.LEVEL4.equals(oplevel) ) {
                    if(UtilValidate.isNotEmpty(businessUnit))
                        conditions1.add(EntityCondition.makeCondition("businessUnit",EntityOperator.EQUALS,businessUnit));
                    
                    EntityCondition mainCond = EntityCondition.makeCondition(conditions1, EntityOperator.AND);
                    List<GenericValue> emplFulfillment = EntityQuery.use(delegator).select("partyId","emplTeamId").from("EmplPositionFulfillment").where(mainCond).filterByDate().queryList();
                    if(UtilValidate.isEmpty(emplFulfillment)) {
                        Debug.logError("Error: For Level4 EmplPositionFulfillment is Empty", MODULE);
                        results.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                        results.put(ModelService.ERROR_MESSAGE, "AccessDenied");
                        return results;
                    } else {
                        List<GenericValue> emplTeam = EntityQuery.use(delegator).from("EmplTeam").where("businessUnit",businessUnit).queryList();
                        if(UtilValidate.isNotEmpty(emplTeam)) {
                             Map<String, Object> buMap = new LinkedHashMap<String, Object>();
                             buMap.put("bu", businessUnit);
                             List<String> teamList = EntityUtil.getFieldListFromEntityList(emplTeam, "emplTeamId", true);
                             buMap.put("team_list", teamList);
                             buInfo.add(buMap);
                        }
                    }
                } else if(AccessLevel.LEVEL5.equals(oplevel)) {
                    List<GenericValue> emplFulfillment = EntityQuery.use(delegator).select("partyId","emplTeamId").from("EmplPositionFulfillment").where("businessUnit",businessUnit).filterByDate().queryList();
                    if(UtilValidate.isEmpty(emplFulfillment)) {
                    	Debug.logError("Error: L5 EmplTeam is Empty for "+businessUnit+ " - " +teamId, MODULE);
                        results.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                        results.put(ModelService.ERROR_MESSAGE, "AccessDenied");
                        return results;
                    } else {
                    	List<GenericValue> emplTeam = EntityQuery.use(delegator).from("EmplTeam").where("businessUnit",businessUnit).queryList();
                        if(UtilValidate.isNotEmpty(emplTeam)) {
                        	Map<String, Object> buMap = new LinkedHashMap<String, Object>();
                            buMap.put("bu", businessUnit);
                            List<String> teamList = EntityUtil.getFieldListFromEntityList(emplTeam, "emplTeamId", true);
                            buMap.put("team_list", teamList);
                            buInfo.add(buMap);
                        }
                        
                    }
                    List<String> childBuList = new ArrayList<String>();
                    List<String> childBusinessUnits = DataUtil.getHierarchyBu(delegator, UtilMisc.toList(businessUnit), childBuList);
                    if(UtilValidate.isNotEmpty(childBusinessUnits)) {
                        EntityCondition activeCondition = EntityCondition.makeCondition(EntityOperator.OR,EntityCondition.makeCondition("isActive",EntityOperator.EQUALS,null),EntityCondition.makeCondition("isActive",EntityOperator.EQUALS,"Y"));
                        for(String childBu : childBusinessUnits) {
                            EntityCondition condition1 = EntityCondition.makeCondition(EntityOperator.AND,EntityCondition.makeCondition("businessUnit",EntityOperator.EQUALS,childBu),activeCondition);
                            List<GenericValue> emplTeams1 = EntityQuery.use(delegator).select("emplTeamId","businessUnit").from("EmplTeam").where(condition1).queryList();
                            if(UtilValidate.isNotEmpty(emplTeams1)) {
                                Map<String, Object> buMap = new LinkedHashMap<String, Object>();
                                teams = EntityUtil.getFieldListFromEntityList(emplTeams1, "emplTeamId", true);
                                String bUnit = emplTeams1.get(0).getString("businessUnit");
                                buMap.put("bu", bUnit);
                                buMap.put("team_list", teams);
                                buInfo.add(buMap);
                            }
                        }
                    }
                }
                
            } else {
                Debug.logError("Error: EmplTeam is Empty for "+businessUnit+ " - " +teamId, MODULE);
                results.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                results.put(ModelService.ERROR_MESSAGE, "AccessDenied");
                return results;
            }
             
        } catch (Exception e) {
            e.printStackTrace();
            results.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            results.put(ModelService.ERROR_MESSAGE, "Error : "+e.getMessage());
            return results;
        }
        results.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        results.put(ModelService.SUCCESS_MESSAGE, "ValidUser");
        results.put("buInfo", buInfo);
        return results;
    }
    
    public static Map < String, Object > addCustomSecurityGroup(DispatchContext dctx, Map < String, Object > context) {
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Security security = dctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
        Map < String, Object > results = ServiceUtil.returnSuccess();
        String userLoginId = (String) context.get("userLoginId");
        String selectedGroupIds = (String) context.get("selectedGroupIds");
        String activeTab = (String) context.get("activeTab");
        try {
        	
        	if(UtilValidate.isNotEmpty(userLoginId) && UtilValidate.isNotEmpty(selectedGroupIds)) {
    			List<Map<String, Object>> requestMapList = new ArrayList<Map<String, Object>>();
    			if(UtilValidate.isNotEmpty(selectedGroupIds))
    				requestMapList = DataUtil.convertToListMap(selectedGroupIds);
    			if(UtilValidate.isNotEmpty(requestMapList)) {
    				for(Map<String, Object> requestMap : requestMapList) {
    					String groupId = (String) requestMap.get("groupId");
    					GenericValue userLoginSecurityGroup = EntityQuery.use(delegator).from("UserLoginSecurityGroup").where("userLoginId", userLoginId,"groupId", groupId).filterByDate().queryFirst();
    					if(UtilValidate.isNotEmpty(userLoginSecurityGroup)) {
    						userLoginSecurityGroup.store();
    					} else {
    						userLoginSecurityGroup = delegator.makeValue("UserLoginSecurityGroup");
    						userLoginSecurityGroup.set("userLoginId", userLoginId);
    						userLoginSecurityGroup.set("groupId", groupId);
    						userLoginSecurityGroup.set("fromDate", UtilDateTime.nowTimestamp());
    						userLoginSecurityGroup.create();
    					}
    					results.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
    				}
    			}
    			
    			if(ServiceUtil.isSuccess(results))
    				results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "CustomSecurityAddedSuccessfully", locale));
    			else
    				results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "ErrorOccurred", locale));
    		}
        } catch (Exception e) {
            e.printStackTrace();
            Debug.logError(e.getMessage(), MODULE);
            results = ServiceUtil.returnError("Error : " + e.getMessage());
            results.put("activeTab", activeTab);
            results.put("userLoginId", userLoginId);
            return results;
        }
        results.put("userLoginId", userLoginId);
        results.put("activeTab", activeTab);
        return results;
    }
    @SuppressWarnings("unchecked")
	public static Map < String, Object > addCustomSecurityGroupForInviteUsers(DispatchContext dctx, Map < String, Object > context) {
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Security security = dctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
        Map < String, Object > results = ServiceUtil.returnSuccess();
        String userLoginId = (String) context.get("userLoginId");
        List selectedGroupIds = (List) context.get("selectedGroupIds");
        try {
        	
        	if(UtilValidate.isNotEmpty(userLoginId) && UtilValidate.isNotEmpty(selectedGroupIds)) {
    			if(UtilValidate.isNotEmpty(selectedGroupIds)) {
    				for( int i=0;i<selectedGroupIds.size();i++) {
    					String groupId = (String) selectedGroupIds.get(i);
    					GenericValue userLoginSecurityGroup = EntityQuery.use(delegator).from("UserLoginSecurityGroup").where("userLoginId", userLoginId,"groupId", groupId).filterByDate().queryFirst();
    					if(UtilValidate.isNotEmpty(userLoginSecurityGroup)) {
    						userLoginSecurityGroup.store();
    					} else {
    						userLoginSecurityGroup = delegator.makeValue("UserLoginSecurityGroup");
    						userLoginSecurityGroup.set("userLoginId", userLoginId);
    						userLoginSecurityGroup.set("groupId", groupId);
    						userLoginSecurityGroup.set("fromDate", UtilDateTime.nowTimestamp());
    						userLoginSecurityGroup.create();
    					}
    					results.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
    				}
    			}
    			
    			if(ServiceUtil.isSuccess(results))
    				results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "CustomSecurityAddedSuccessfully", locale));
    			else
    				results = ServiceUtil.returnSuccess(UtilProperties.getMessage(RESOURCE, "ErrorOccurred", locale));
    		}
        } catch (Exception e) {
            e.printStackTrace();
            Debug.logError(e.getMessage(), MODULE);
            results.put("userLoginId", userLoginId);
            results = ServiceUtil.returnError("Error : " + e.getMessage());
            return results;
        }
        results.put("userLoginId", userLoginId);
        return results;
    }
    
    public static Map < String, Object > updateSecurityPartyInfo(DispatchContext dctx, Map < String, Object > context) {
        Delegator delegator = dctx.getDelegator();
        Map < String, Object > results = ServiceUtil.returnSuccess();
        String userLoginId = (String) context.get("userLoginId");
        String ownerId = (String) context.get("ownerId");
        String roleTypeId = (String) context.get("roleTypeId");
        String ownerBu = (String) context.get("ownerBu");
        String ownerTeam = (String) context.get("ownerTeam");
        String partyId = (String) context.get("partyId");
        try {
        	String currentPartyId = org.fio.homeapps.util.DataUtil.getUserLoginPartyId(delegator, ownerId);
        	Map<String, Object> buTeamData = org.fio.homeapps.util.DataUtil.getUserBuTeam(delegator, currentPartyId);
			if(UtilValidate.isEmpty(ownerBu))
				ownerBu = (String) buTeamData.get("businessUnit");
			if(UtilValidate.isEmpty(ownerTeam))
				ownerTeam = (String) buTeamData.get("emplTeamId");
			
			GenericValue party = EntityQuery.use(delegator).from("Party").where("partyId", partyId).queryOne();
        	if(UtilValidate.isNotEmpty(party)) {
        		if(UtilValidate.isNotEmpty(roleTypeId))
        			party.set("roleTypeId", roleTypeId);
        		if(UtilValidate.isNotEmpty(ownerId))
        			party.set("ownerId", ownerId);
        		if(UtilValidate.isNotEmpty(ownerBu))
        			party.set("ownerBu", ownerBu);
        		if(UtilValidate.isNotEmpty(ownerTeam))
        			party.set("emplTeamId", ownerTeam);
        		party.store();
        	}
        } catch (Exception e) {
        	Debug.logError(e, e.getMessage(), MODULE);
        	results = ServiceUtil.returnError("Error : " + e.getMessage());
            return results;
		}
        return results;
    }
}