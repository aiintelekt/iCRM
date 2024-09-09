package org.groupfio.common.portal.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.fio.homeapps.util.EnumUtil;
import org.fio.homeapps.util.PartyHelper;
import org.groupfio.common.portal.event.AjaxEvents;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


public class ContactAndPartyAssoc {

    private static final String MODULE = ContactAndPartyAssoc.class.getName();

    public static Map < String, Object > getContactAndAccountAssoc(DispatchContext dctx, Map < String, Object > context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        Map < String, Object > result = ServiceUtil.returnSuccess();
        String partyIdFrom = (String) context.get("partyIdFrom");
        String partyIdTo = (String) context.get("partyIdTo");
        List < Object > accountContactList = FastList.newInstance();
        try {
            if (UtilValidate.isNotEmpty(partyIdFrom) || UtilValidate.isNotEmpty(partyIdTo)) {
                List < EntityCondition > conditions = new ArrayList < EntityCondition > ();

                // construct role conditions
                EntityCondition roleTypeCondition = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "CONTACT"),
                    EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "ACCOUNT"),
                    EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "CONTACT_REL_INV")));
                conditions.add(roleTypeCondition);
                EntityCondition partyStatusCondition = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PARTY_DISABLED"),
                    EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, null)), EntityOperator.OR);
                conditions.add(partyStatusCondition);
                if (UtilValidate.isNotEmpty(partyIdTo)) {
                    conditions.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, partyIdTo));
                }
                if (UtilValidate.isNotEmpty(partyIdFrom)) {
                    conditions.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, partyIdFrom));
                }
                conditions.add(EntityUtil.getFilterByDateExpr());
                EntityCondition mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);
                List < GenericValue > partyFromReln = delegator.findList("PartyFromRelnAndParty", mainConditons, null, null, null, false);
                if (partyFromReln != null && partyFromReln.size() > 0) {
                    List < String > partyRelnId = null;
                    if (UtilValidate.isNotEmpty(partyIdFrom)) {
                        partyRelnId = EntityUtil.getFieldListFromEntityList(partyFromReln, "partyIdTo", true);
                    } else if (UtilValidate.isNotEmpty(partyIdTo)) {
                        partyRelnId = EntityUtil.getFieldListFromEntityList(partyFromReln, "partyIdFrom", true);
                    }

                    if (partyRelnId != null && partyRelnId.size() > 0) {
                        for (String partyId: partyRelnId) {
                            String contactId = "";
                            String accountId = "";
                            String name = "";
                            String companyName = "";
                            String statusId = "";
                            String isMarketable = "";
                            GenericValue partySummaryDetailsViewGv = delegator.findOne("PartySummaryDetailsView", UtilMisc.toMap("partyId", partyId), false);
                            if (UtilValidate.isNotEmpty(partyIdFrom)) {
                                contactId = partyIdFrom;
                                accountId = partyId;
                                companyName = partySummaryDetailsViewGv.getString("groupName");
                            } else if (UtilValidate.isNotEmpty(partyIdTo)) {
                                contactId = partyId;
                                accountId = partyIdTo;
                                name = partySummaryDetailsViewGv.getString("firstName");
                                if (UtilValidate.isNotEmpty(partySummaryDetailsViewGv.getString("lastName"))) {
                                    name = name + " " + partySummaryDetailsViewGv.getString("lastName");
                                }
                            }
                            GenericValue partyRelationship = EntityQuery.use(delegator).from("PartyRelationship")
                                .where("partyIdFrom", contactId, "partyIdTo", accountId, "roleTypeIdFrom", "CONTACT",
                                    "roleTypeIdTo", "ACCOUNT", "partyRelationshipTypeId", "CONTACT_REL_INV")
                                .filterByDate().queryFirst();
                            if (partyRelationship != null && partyRelationship.size() > 0) {
                                String partyRelAssocId = partyRelationship.getString("partyRelAssocId");
                                statusId = partyRelationship.getString("statusId");
                                isMarketable = partyRelationship.getString("isMarketable");
                                Map < String, Object > accountContactMap = new HashMap < String, Object > ();
                                accountContactMap.put("contactId", contactId);
                                accountContactMap.put("accountId", accountId);
                                accountContactMap.put("partyId", accountId);
                                accountContactMap.put("statusId", statusId);
                                accountContactMap.put("name", name);
                                accountContactMap.put("companyName", companyName);
                                accountContactMap.put("partyRelAssocId", partyRelAssocId);
                                accountContactMap.put("isMarketable", isMarketable);
                                accountContactList.add(accountContactMap);
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            Debug.logInfo("==========================ERROR======================" + ex.toString(), "");
        }
        Debug.log("accountContactList=============" + accountContactList);
        result.put("accountContactAssoc", accountContactList);
        return result;
    }
    
    public static Map < String, Object > getContactAndLeadAssoc(DispatchContext dctx, Map < String, Object > context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        Map < String, Object > result = ServiceUtil.returnSuccess();
        String partyIdFrom = (String) context.get("partyIdFrom");
        String partyIdTo = (String) context.get("partyIdTo");
        List < Object > leadContactList = FastList.newInstance();
        try {
            if (UtilValidate.isNotEmpty(partyIdFrom) || UtilValidate.isNotEmpty(partyIdTo)) {
                List < EntityCondition > conditions = new ArrayList < EntityCondition > ();

                // construct role conditions
                EntityCondition roleTypeCondition = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "CONTACT"),
                    EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "LEAD"),
                    EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "CONTACT_REL_INV")));
                conditions.add(roleTypeCondition);
                EntityCondition partyStatusCondition = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PARTY_DISABLED"),
                    EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, null)), EntityOperator.OR);
                conditions.add(partyStatusCondition);
                if (UtilValidate.isNotEmpty(partyIdTo)) {
                    conditions.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, partyIdTo));
                }
                if (UtilValidate.isNotEmpty(partyIdFrom)) {
                    conditions.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, partyIdFrom));
                }
                conditions.add(EntityUtil.getFilterByDateExpr());
                EntityCondition mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);
                List < GenericValue > partyFromReln = delegator.findList("PartyFromRelnAndParty", mainConditons, null, null, null, false);
                if (partyFromReln != null && partyFromReln.size() > 0) {
                    List < String > partyRelnId = null;
                    if (UtilValidate.isNotEmpty(partyIdFrom)) {
                        partyRelnId = EntityUtil.getFieldListFromEntityList(partyFromReln, "partyIdTo", true);
                    } else if (UtilValidate.isNotEmpty(partyIdTo)) {
                        partyRelnId = EntityUtil.getFieldListFromEntityList(partyFromReln, "partyIdFrom", true);
                    }

                    if (partyRelnId != null && partyRelnId.size() > 0) {
                        for (String partyId: partyRelnId) {
                            String contactId = "";
                            String leadId = "";
                            String name = "";
                            String companyName = "";
                            String statusId = "";
                            String isMarketable = "";
                            GenericValue partySummaryDetailsViewGv = delegator.findOne("PartySummaryDetailsView", UtilMisc.toMap("partyId", partyId), false);
                            if (UtilValidate.isNotEmpty(partyIdFrom)) {
                                contactId = partyIdFrom; // to be validated
                                leadId = partyId;
                                companyName = partySummaryDetailsViewGv.getString("companyName");
                            } else if (UtilValidate.isNotEmpty(partyIdTo)) {
                                contactId = partyId;
                                leadId = partyIdTo;
                                name = partySummaryDetailsViewGv.getString("groupName");
                                if (UtilValidate.isNotEmpty(partySummaryDetailsViewGv.getString("lastName"))) {
                                    name = partySummaryDetailsViewGv.getString("firstName") + " " + partySummaryDetailsViewGv.getString("lastName");
                                }
                            }
                            GenericValue partyContactViewGv = delegator.findOne("Person", UtilMisc.toMap("partyId", contactId), false);
                            if(UtilValidate.isNotEmpty(partyContactViewGv.getString("firstName"))) {
                            	if (UtilValidate.isNotEmpty(partyContactViewGv.getString("lastName"))) {
                                    name = partyContactViewGv.getString("firstName") + " " + partyContactViewGv.getString("lastName");
                                }else {
                                	name = partyContactViewGv.getString("firstName");
                                }
                            }
                            GenericValue partyRelationship = EntityQuery.use(delegator).from("PartyRelationship")
                                .where("partyIdFrom", contactId, "partyIdTo", leadId, "roleTypeIdFrom", "CONTACT",
                                    "roleTypeIdTo", "LEAD", "partyRelationshipTypeId", "CONTACT_REL_INV")
                                .filterByDate().queryFirst();
                            if (partyRelationship != null && partyRelationship.size() > 0) {
                                String partyRelAssocId = partyRelationship.getString("partyRelAssocId");
                                statusId = partyRelationship.getString("statusId");
                                isMarketable = partyRelationship.getString("isMarketable");
                                Map < String, Object > leadContactMap = new HashMap < String, Object > ();

                                leadContactMap.put("contactId", contactId);
                                leadContactMap.put("leadId", leadId);
                                leadContactMap.put("partyId", leadId);
                                leadContactMap.put("statusId", statusId);
                                leadContactMap.put("name", name);
                                leadContactMap.put("companyName", companyName);
                                leadContactMap.put("partyRelAssocId", partyRelAssocId);
                                leadContactMap.put("isMarketable", isMarketable);
                                leadContactList.add(leadContactMap);
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            Debug.logInfo("==========================ERROR======================" + ex.toString(), "");
        }
        Debug.log("accountContactList=============" + leadContactList);
        result.put("leadContactAssoc", leadContactList);
        return result;
    }
    
    public static Map < String, Object > getContactAndPartyAssoc(DispatchContext dctx, Map < String, Object > context) {
    	LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        Map < String, Object > result = ServiceUtil.returnSuccess();
        String partyIdFrom = (String) context.get("partyIdFrom");
        String partyIdTo = (String) context.get("partyIdTo");
        String partyRoleTypeId = (String) context.get("partyRoleTypeId");
        String isMarketable = "";
        List < Object > dataList = FastList.newInstance();
        try {
            if (UtilValidate.isNotEmpty(partyIdFrom) || UtilValidate.isNotEmpty(partyIdTo)) {
                List < EntityCondition > conditions = new ArrayList < EntityCondition > ();

                if (UtilValidate.isNotEmpty(partyIdFrom)) {
                    conditions.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, partyIdFrom));
                }
                if (UtilValidate.isNotEmpty(partyIdTo)) {
                    conditions.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, partyIdTo));
                }
                
                // construct role conditions
                EntityCondition roleTypeCondition = EntityCondition.makeCondition(UtilMisc.toList(
                		EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "CONTACT"),
                		EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "CONTACT_REL_INV")
                		
                		));
                conditions.add(roleTypeCondition);
                if (UtilValidate.isNotEmpty(partyRoleTypeId)) {
                	conditions.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, partyRoleTypeId));
                }
                
                EntityCondition partyStatusCondition = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PARTY_DISABLED"),
                    EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, null)), EntityOperator.OR);
                conditions.add(partyStatusCondition);
                
                conditions.add(EntityUtil.getFilterByDateExpr());
                
                EntityCondition mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);
                List<GenericValue> partyRelList = delegator.findList("PartyFromRelnAndParty", mainConditons, null, null, null, false);
                if (UtilValidate.isNotEmpty(partyRelList)) {
                	
                	for (GenericValue partyRel : partyRelList) {

                		String assocPartyId = null;
                		String assocRoleTypeId = null;
                		String contactId = null;
                		String name = null;
                		String isPrimary= null;

                		if (UtilValidate.isNotEmpty(partyIdFrom)) {
                			assocPartyId = partyRel.getString("partyIdTo");
                			assocRoleTypeId = partyRel.getString("roleTypeIdTo");
                			contactId = partyIdFrom;
                		} else if (UtilValidate.isNotEmpty(partyIdTo)) {
                			assocPartyId = partyRel.getString("partyIdFrom");
                			assocRoleTypeId = partyRel.getString("roleTypeIdFrom");
                			contactId = assocPartyId;
                		}

                		name = PartyHelper.getPartyName(delegator, assocPartyId, false);

                		//added for derive marketing field from allow solicitation
                		EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
                				EntityCondition.makeCondition("roleTypeId",EntityOperator.EQUALS,"CONTACT"),
                				EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,contactId),
                				EntityCondition.makeCondition(EntityOperator.OR,
                						EntityCondition.makeCondition("allowSolicitation",EntityOperator.EQUALS,"Y"),
                						EntityCondition.makeCondition("allowSolicitation",EntityOperator.EQUALS,null)),
                				EntityCondition.makeCondition(EntityOperator.OR,
                						EntityCondition.makeCondition("contactMechTypeId",EntityOperator.EQUALS,"EMAIL_ADDRESS"),
                						EntityCondition.makeCondition("contactMechTypeId",EntityOperator.EQUALS,"TELECOM_NUMBER")));

                		List<GenericValue> partyContactMechList = EntityQuery.use(delegator).from("PartyAndContactMech").where(condition).queryList();
                		if (UtilValidate.isNotEmpty(partyContactMechList)) {
                			isMarketable = "Y";
                		}
                		else {
                			isMarketable = "N";
                		}
                		//ended

                		String partyRelAssocId = partyRel.getString("partyRelAssocId");
                		String statusId = partyRel.getString("relationshipStatusId");
                		//String isMarketable = partyRel.getString("isMarketable");
                		String roleTypeIdFrom = partyRel.getString("roleTypeIdFrom");
                		String roleTypeIdTo = partyRel.getString("roleTypeIdTo");
                		if(UtilValidate.isNotEmpty(statusId) && statusId.equals("PARTY_DEFAULT")) {
                			isPrimary = "Y";
                		}
                		Map<String, Object> data = new HashMap<String, Object>();

                		data.put("contactId", contactId);
                		data.put("partyId", assocPartyId);
                		data.put("statusId", statusId);
                		data.put("name", name);
                		data.put("partyRelAssocId", partyRelAssocId);
                		data.put("isMarketable", isMarketable);
                		data.put("roleTypeIdFrom", roleTypeIdFrom);
                		data.put("roleTypeIdTo", roleTypeIdTo);
                		data.put("assocRoleTypeId", assocRoleTypeId);
                		data.put("isPrimary", isPrimary);

                		dataList.add(data);

                		//update isMarketable flag in db 
                		GenericValue partyRelShipGv = EntityUtil.getFirst( delegator.findByAnd("PartyRelationship", UtilMisc.toMap("partyIdTo", partyIdTo,"partyIdFrom", contactId), null, false) );
                		if(UtilValidate.isNotEmpty(partyRelShipGv)){
                			partyRelShipGv.put("isMarketable", isMarketable);
                			partyRelShipGv.store();
                		}
                		 

                	}
                	
                	
                	
                    /*List < String > partyRelnId = null;
                    if (UtilValidate.isNotEmpty(partyIdFrom)) {
                        partyRelnId = EntityUtil.getFieldListFromEntityList(partyFromReln, "partyIdTo", true);
                    } else if (UtilValidate.isNotEmpty(partyIdTo)) {
                        partyRelnId = EntityUtil.getFieldListFromEntityList(partyFromReln, "partyIdFrom", true);
                    }

                    if (partyRelnId != null && partyRelnId.size() > 0) {
                        for (String partyId : partyRelnId) {
                            String contactId = "";
                            String assocPartyId = "";
                            String name = "";
                            String companyName = "";
                            String statusId = "";
                            String isMarketable = "";
                            String roleTypeIdFrom = "";
                            String roleTypeIdTo = "";
                            
                            GenericValue partySummaryDetailsViewGv = delegator.findOne("PartySummaryDetailsView", UtilMisc.toMap("partyId", partyId), false);
                            if (UtilValidate.isNotEmpty(partyIdFrom)) {
                                contactId = partyIdFrom; // to be validated
                                assocPartyId = partyId;
                                companyName = partySummaryDetailsViewGv.getString("companyName");
                            } else if (UtilValidate.isNotEmpty(partyIdTo)) {
                                contactId = partyId;
                                assocPartyId = partyIdTo;
                                name = partySummaryDetailsViewGv.getString("groupName");
                                if (UtilValidate.isNotEmpty(partySummaryDetailsViewGv.getString("lastName"))) {
                                    name = partySummaryDetailsViewGv.getString("firstName") + " " + partySummaryDetailsViewGv.getString("lastName");
                                }
                            }
                            
                            GenericValue partyContactViewGv = delegator.findOne("Person", UtilMisc.toMap("partyId", contactId), false);
                            if(UtilValidate.isNotEmpty(partyContactViewGv.getString("firstName"))) {
                            	if (UtilValidate.isNotEmpty(partyContactViewGv.getString("lastName"))) {
                                    name = partyContactViewGv.getString("firstName") + " " + partyContactViewGv.getString("lastName");
                                }else {
                                	name = partyContactViewGv.getString("firstName");
                                }
                            }
                            
                            GenericValue partyRelationship = EntityQuery.use(delegator).from("PartyRelationship")
                                .where("partyIdFrom", contactId, "partyIdTo", assocPartyId, "roleTypeIdFrom", "CONTACT",
                                    "roleTypeIdTo", partyRoleTypeId, "partyRelationshipTypeId", "CONTACT_REL_INV")
                                .filterByDate().queryFirst();
                            
                            if (UtilValidate.isNotEmpty(partyRelationship)) {
                                String partyRelAssocId = partyRelationship.getString("partyRelAssocId");
                                statusId = partyRelationship.getString("statusId");
                                isMarketable = partyRelationship.getString("isMarketable");
                                roleTypeIdFrom = partyRelationship.getString("roleTypeIdFrom");
                                roleTypeIdTo = partyRelationship.getString("roleTypeIdTo");
                                Map < String, Object > partyContactMap = new HashMap < String, Object > ();

                                partyContactMap.put("contactId", contactId);
                                partyContactMap.put("partyId", assocPartyId);
                                partyContactMap.put("statusId", statusId);
                                partyContactMap.put("name", name);
                                partyContactMap.put("companyName", companyName);
                                partyContactMap.put("partyRelAssocId", partyRelAssocId);
                                partyContactMap.put("isMarketable", isMarketable);
                                partyContactMap.put("roleTypeIdFrom", roleTypeIdFrom);
                                partyContactMap.put("roleTypeIdTo", roleTypeIdTo);
                                partyContactList.add(partyContactMap);
                            }
                        }
                    }*/
                }
            }
        } catch (Exception ex) {
            Debug.logInfo("==========================ERROR======================" + ex.toString(), "");
        }
        result.put("partyContactAssoc", dataList);
        return result;
    }
    
    public static Map < String, Object > getContactAndPartyAssocUL(DispatchContext dctx, Map < String, Object > context) {
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Delegator delegator = dctx.getDelegator();
    	Map < String, Object > result = ServiceUtil.returnSuccess();
    	String partyIdFrom = (String) context.get("partyIdFrom");
    	String partyIdTo = (String) context.get("partyIdTo");
    	String partyRoleTypeId = (String) context.get("partyRoleTypeId");
    	String isMarketable = "";
    	List < Object > dataList = FastList.newInstance();
    	try {
    		if (UtilValidate.isNotEmpty(partyIdFrom) || UtilValidate.isNotEmpty(partyIdTo)) {
    			List < EntityCondition > conditions = new ArrayList < EntityCondition > ();

    			if (UtilValidate.isNotEmpty(partyIdFrom)) {
    				conditions.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, partyIdFrom));
    			}
    			if (UtilValidate.isNotEmpty(partyIdTo)) {
    				conditions.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, partyIdTo));
    			}

    			// construct role conditions
    			EntityCondition roleTypeCondition = EntityCondition.makeCondition(UtilMisc.toList(
    					EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "CONTACT"),
    					EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "CONTACT_REL_INV")

    					));
    			conditions.add(roleTypeCondition);
    			if (UtilValidate.isNotEmpty(partyRoleTypeId)) {
    				conditions.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, partyRoleTypeId));
    			}

    			EntityCondition partyStatusCondition = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PARTY_DISABLED"),
    					EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, null)), EntityOperator.OR);
    			conditions.add(partyStatusCondition);

    			conditions.add(EntityUtil.getFilterByDateExpr());

    			EntityCondition mainConditons = EntityCondition.makeCondition(conditions, EntityOperator.AND);
    			List<GenericValue> partyRelList = delegator.findList("PartyFromRelnAndParty", mainConditons, null, null, null, false);
    			if (UtilValidate.isNotEmpty(partyRelList)) {

    				for (GenericValue partyRel : partyRelList) {

    					String assocPartyId = null;
    					String assocRoleTypeId = null;
    					String contactId = null;
    					String name = null;
    					String isPrimary= null;

    					if (UtilValidate.isNotEmpty(partyIdFrom)) {
    						assocPartyId = partyRel.getString("partyIdTo");
    						assocRoleTypeId = partyRel.getString("roleTypeIdTo");
    						contactId = partyIdFrom;
    					} else if (UtilValidate.isNotEmpty(partyIdTo)) {
    						assocPartyId = partyRel.getString("partyIdFrom");
    						assocRoleTypeId = partyRel.getString("roleTypeIdFrom");
    						contactId = assocPartyId;
    					}

    					name = PartyHelper.getPartyName(delegator, assocPartyId, false);
    					String partyRelAssocId = partyRel.getString("partyRelAssocId");
    					String statusId = partyRel.getString("relationshipStatusId");
    					//String isMarketable = partyRel.getString("isMarketable");
    					String roleTypeIdFrom = partyRel.getString("roleTypeIdFrom");
    					String roleTypeIdTo = partyRel.getString("roleTypeIdTo");
    					if(UtilValidate.isNotEmpty(statusId) && statusId.equals("PARTY_DEFAULT")) {
    						isPrimary = "Y";
    					}
    					GenericValue enableUserLogin = EntityQuery.use(delegator).from("UserLogin")
    							.where("partyId", contactId).queryFirst();
    					if(UtilValidate.isEmpty(enableUserLogin)) {
    						Map<String, Object> data = new HashMap<String, Object>();

    						data.put("contactId", contactId);
    						data.put("partyId", assocPartyId);
    						data.put("statusId", statusId);
    						data.put("name", name);
    						data.put("partyRelAssocId", partyRelAssocId);
    						data.put("isMarketable", isMarketable);
    						data.put("roleTypeIdFrom", roleTypeIdFrom);
    						data.put("roleTypeIdTo", roleTypeIdTo);
    						data.put("assocRoleTypeId", assocRoleTypeId);
    						data.put("isPrimary", isPrimary);

    						dataList.add(data);

    					}

    				}

    			}
    		}
    	} catch (Exception ex) {
    		Debug.logInfo("==========================ERROR======================" + ex.toString(), "");
    	}
    	result.put("partyContactAssoc", dataList);
    	return result;
    }
    
    public static String getContactAndPartyAssocDetails(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        String contactPartyId = request.getParameter("contactPartyId");
        String partyId = request.getParameter("partyId");
        String contactType = request.getParameter("contactType");
        String partyRelAssocId = request.getParameter("partyRelAssocId");
        Map < String, Object > resp = new HashMap < String, Object > ();
        JSONArray datas = new JSONArray();
        try {
            if (UtilValidate.isNotEmpty(contactPartyId) && UtilValidate.isNotEmpty(partyId) && UtilValidate.isNotEmpty(contactType) && UtilValidate.isNotEmpty(partyRelAssocId)) {
                EntityCondition condition1 = EntityCondition.makeCondition("partyRelAssocId", EntityOperator.EQUALS, partyRelAssocId);
                EntityCondition condition2 = EntityCondition.makeCondition(UtilMisc.toList(
                    EntityCondition.makeCondition("assocId", EntityOperator.NOT_EQUAL, null),
                    EntityCondition.makeCondition("assocId", EntityOperator.NOT_EQUAL, "")), EntityOperator.OR);
                int i = 1;
                if ("EMAIL".equalsIgnoreCase(contactType) || "PHONE".equalsIgnoreCase(contactType)) {
                    EntityCondition conditon = EntityCondition.makeCondition(UtilMisc.toList(condition1, condition2, EntityCondition.makeCondition("assocTypeId", EntityOperator.EQUALS, contactType)));
                    List < GenericValue > partyRelationshipAssoc = EntityQuery.use(delegator).from("PartyRelationshipAssoc")
                        .where(conditon).queryList();
                    if (partyRelationshipAssoc != null && partyRelationshipAssoc.size() > 0) {
                        JSONObject data = new JSONObject();
                        if ("EMAIL".equalsIgnoreCase(contactType)) {
                            List < String > assocIdEmail = EntityUtil.getFieldListFromEntityList(partyRelationshipAssoc, "assocId", true);
                            if (assocIdEmail != null && assocIdEmail.size() > 0) {
                                List < GenericValue > pcwpEmail = EntityQuery.use(delegator).from("PartyContactWithPurpose")
                                    .where(EntityCondition.makeCondition("contactMechId", EntityOperator.IN, assocIdEmail),
                                        EntityCondition.makeCondition("contactMechTypeId", EntityOperator.EQUALS, "EMAIL_ADDRESS"),
                                        EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,contactPartyId))
                                    .filterByDate("contactFromDate", "contactThruDate", "purposeFromDate", "purposeThruDate")
                                    .orderBy("contactMechPurposeTypeId").queryList();
                                if (pcwpEmail != null && pcwpEmail.size() > 0) {
                                    for (GenericValue pcwpEmailGV: pcwpEmail) {
                                        String infoString = pcwpEmailGV.getString("infoString");
                                        String contactMechId = pcwpEmailGV.getString("contactMechId");
                                        String purposeDescription = pcwpEmailGV.getString("purposeDescription");
                                        String contactMechPurposeTypeId = pcwpEmailGV.getString("contactMechPurposeTypeId");
                                        String allowSolicitation = "Y";
                                        String isEdit = "Y";
                                        GenericValue emailSolicitaion = EntityQuery.use(delegator).from("PartyRelationshipAssoc")
                                            .where("partyRelAssocId", partyRelAssocId, "assocTypeId", "EMAIL", "assocId", contactMechId)
                                            .queryFirst();
                                        String cmId = emailSolicitaion.getString("assocId");
                                        GenericValue contcatMechEmailSolicitation = EntityQuery.use(delegator).from("PartyAndContactMech")
                                                .where("contactMechId", cmId,"roleTypeId","CONTACT")
                                                .queryFirst();
                                        // GenericValue contcatMechEmailSolicitation = EntityQuery.use(delegator).from("PartyAndContactMech").where("contactMechId", contactMechId).queryOne();
                                        if (contcatMechEmailSolicitation != null && contcatMechEmailSolicitation.size() > 0 && "N".equalsIgnoreCase(contcatMechEmailSolicitation.getString("allowSolicitation"))) {
                                            allowSolicitation = "N";
                                        }
                                        if (UtilValidate.isNotEmpty(contactMechPurposeTypeId) && ("AOS_EMAIL_ADDRESS".equalsIgnoreCase(contactMechPurposeTypeId) || "IDEAL_EMAIL_ADDRESS".equalsIgnoreCase(contactMechPurposeTypeId))) {
                                            isEdit = "N";
                                        }
                                        data.put("infoString", infoString);
                                        data.put("contactMechId", contactMechId);
                                        data.put("purposeDescription", purposeDescription);
                                        data.put("purposeTypeId", contactMechPurposeTypeId);
                                        data.put("allowSolicitation", allowSolicitation);
                                        data.put("id", "email" + contactPartyId + partyId + contactMechId + i);
                                        data.put("isEdit", isEdit);
                                        datas.add(data);
                                        i++;
                                        
                                        
                                    }
                                }
                            }
                        } else if ("PHONE".equalsIgnoreCase(contactType)) {
                            List < String > assocIdPhone = EntityUtil.getFieldListFromEntityList(partyRelationshipAssoc, "assocId", true);
                            if (assocIdPhone != null && assocIdPhone.size() > 0) {
                                List < GenericValue > pcwpPhone = EntityQuery.use(delegator).from("PartyContactWithPurpose")
                                    .where(EntityCondition.makeCondition("contactMechId", EntityOperator.IN, assocIdPhone),
                                        EntityCondition.makeCondition("contactMechTypeId", EntityOperator.EQUALS, "TELECOM_NUMBER"),
                                        EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,contactPartyId))
                                    .filterByDate("contactFromDate", "contactThruDate", "purposeFromDate", "purposeThruDate")
                                    .orderBy("contactMechPurposeTypeId").queryList();
                                if (pcwpPhone != null && pcwpPhone.size() > 0) {
                                    for (GenericValue pcwpPhoneGV: pcwpPhone) {
                                        String contactMechId = pcwpPhoneGV.getString("contactMechId");
                                        GenericValue telecomNumber = delegator.findOne("TelecomNumber", UtilMisc.toMap("contactMechId", contactMechId), false);
                                        if (telecomNumber != null && telecomNumber.size() > 0) {
                                            String purposeDescription = pcwpPhoneGV.getString("purposeDescription");
                                            String allowSolicitation = "Y";
                                            String contactMechPurposeTypeId = pcwpPhoneGV.getString("contactMechPurposeTypeId");
                                            String isEdit = "Y";
                                            GenericValue phoneSolicitaionFromAssoc = EntityQuery.use(delegator).from("PartyRelationshipAssoc")
                                                .where("partyRelAssocId", partyRelAssocId, "assocTypeId", "PHONE", "assocId", contactMechId)
                                                .queryFirst();
                                            String cmId = phoneSolicitaionFromAssoc.getString("assocId");
                                            GenericValue phoneSolicitaion = EntityQuery.use(delegator).from("PartyAndContactMech")
                                                    .where("contactMechId", cmId,"roleTypeId","CONTACT")
                                                    .queryFirst();
                                           // GenericValue phoneSolicitaion = EntityQuery.use(delegator).from("PartyAndContactMech").where("contactMechId", contactMechId).queryOne();
                                            if (phoneSolicitaion != null && phoneSolicitaion.size() > 0 && "N".equalsIgnoreCase(phoneSolicitaion.getString("allowSolicitation"))) {
                                                allowSolicitation = "N";
                                            }
                                            if (UtilValidate.isNotEmpty(contactMechPurposeTypeId) && ("AOS_MOBILE_PHONE".equalsIgnoreCase(contactMechPurposeTypeId) || "IDEAL_MOBILE_PHONE".equalsIgnoreCase(contactMechPurposeTypeId) || "IDEAL_PRIMARY_PHONE".equalsIgnoreCase(contactMechPurposeTypeId))) {
                                                isEdit = "N";
                                            }
                                            String contactNumber = telecomNumber.getString("contactNumber");
                                            data.put("contactMechId", contactMechId);
                                            data.put("contactPartyId", contactPartyId);
                                            data.put("accountPartyId", partyId);
                                            data.put("partyRelAssocId", partyRelAssocId);
                                            data.put("purposeDescription", purposeDescription);
                                            data.put("purposeTypeId", contactMechPurposeTypeId);
                                            data.put("allowSolicitation", allowSolicitation);
                                            data.put("contactNumber", contactNumber);
                                            data.put("id", "phone" + contactPartyId + partyId + contactMechId + i);
                                            data.put("isEdit", isEdit);
                                            datas.add(data);
                                            i++;
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else if ("DESIGNATION".equalsIgnoreCase(contactType)) {
                	
                	List conditionList = FastList.newInstance();
                	
                	conditionList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
                	conditionList.add(EntityCondition.makeCondition("contactId", EntityOperator.EQUALS, contactPartyId));
                	conditionList.add(EntityCondition.makeCondition("partyRelAssocId", EntityOperator.EQUALS, partyRelAssocId));
                	
                	EntityCondition mainConditons = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
    				List<GenericValue> contactDesignationList = delegator.findList("ContactDesignationAssoc", mainConditons, UtilMisc.toSet("designationName", "contactDesignationAssocId"), null, null, false);
    				if (UtilValidate.isNotEmpty(contactDesignationList)) {
    					for (GenericValue contactDesignation: contactDesignationList) {
    						JSONObject data = new JSONObject();
                            String designationName = contactDesignation.getString("designationName");
                            String designationId = contactDesignation.getString("contactDesignationAssocId");
                            data.put("designationName", designationName);
                            data.put("designationId", designationId);
                            data.put("contactPartyId", contactPartyId);
                            data.put("accountPartyId", partyId);
                            data.put("partyRelAssocId", partyRelAssocId);
                            data.put("id", "des" + contactPartyId + partyId + designationId + i);
                            datas.add(data);
                            i++;
                        }
    				}
                }
            }

        } catch (Exception e) {
            Debug.logError(e.getMessage(), MODULE);
        }
        resp.put("data", datas);

        return AjaxEvents.doJSONResponse(response, resp);
    }

    /*
     * Get Contacts Email, Phone and Designation in popup
     */
    public static String getContactsDetails(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        String contactPartyId = request.getParameter("contactPartyId");
        String partyId = request.getParameter("partyId");
        String contactType = request.getParameter("contactType");
        String partyRelAssocId = request.getParameter("partyRelAssocId");
        String rowId = request.getParameter("rowId");
        String targetRoleTypeId = request.getParameter("targetRoleTypeId");
        Map < String, Object > resp = new HashMap < String, Object > ();
        JSONArray datas = new JSONArray();
        try {
            if (UtilValidate.isNotEmpty(contactPartyId) && UtilValidate.isNotEmpty(partyId) && UtilValidate.isNotEmpty(contactType) && UtilValidate.isNotEmpty(partyRelAssocId)) {
                EntityCondition condition1 = EntityCondition.makeCondition("partyRelAssocId", EntityOperator.EQUALS, partyRelAssocId);
                EntityCondition condition2 = EntityCondition.makeCondition(UtilMisc.toList(
                    EntityCondition.makeCondition("assocId", EntityOperator.NOT_EQUAL, null),
                    EntityCondition.makeCondition("assocId", EntityOperator.NOT_EQUAL, "")), EntityOperator.OR);
                EntityCondition assocCondition = EntityCondition.makeCondition(UtilMisc.toList(condition1, condition2, EntityCondition.makeCondition("assocTypeId", EntityOperator.EQUALS, contactType)));
                List < GenericValue > partyRelationshipAssoc = EntityQuery.use(delegator).from("PartyRelationshipAssoc")
                    .where(assocCondition).queryList();
                List < EntityCondition > conditions = new ArrayList < EntityCondition > ();
                if ("DESIGNATION".equalsIgnoreCase(contactType)) {
                	
                	conditions.add(EntityCondition.makeCondition("enumTypeId", EntityOperator.IN, UtilMisc.toList("DESIGNATION")));
                	
                	List<GenericValue> designationList = EntityQuery.use(delegator).from("Enumeration")
                            .where(conditions).queryList();
                	
                	if (UtilValidate.isNotEmpty(designationList)) {
                		for (GenericValue designation : designationList) {
                            JSONObject data = new JSONObject();
                            data.put("designationName", designation.getString("description"));
                            data.put("designationId", designation.getString("enumId"));
                            data.put("contactPartyId", contactPartyId);
                            data.put("partyId", partyId);
                            data.put("partyRelAssocId", partyRelAssocId);
                            data.put("rowId", rowId);
                            data.put("targetRoleTypeId", targetRoleTypeId);
                            datas.add(data);
                		}
                	}
                	
                } else {
                    conditions.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, contactPartyId));
                    if (partyRelationshipAssoc != null && partyRelationshipAssoc.size() > 0) {
                        conditions.add(EntityCondition.makeCondition("contactMechId", EntityOperator.NOT_IN, EntityUtil.getFieldListFromEntityList(partyRelationshipAssoc, "assocId", true)));
                    }
                    if ("EMAIL".equalsIgnoreCase(contactType)) {
                        conditions.add(EntityCondition.makeCondition("contactMechTypeId", EntityOperator.EQUALS, "EMAIL_ADDRESS"));
                    } else if ("PHONE".equalsIgnoreCase(contactType)) {
                        conditions.add(EntityCondition.makeCondition("contactMechTypeId", EntityOperator.EQUALS, "TELECOM_NUMBER"));
                    }
                    conditions.add(EntityCondition.makeCondition(UtilMisc.toList(
                        EntityCondition.makeCondition("allowSolicitation", EntityOperator.EQUALS, "Y"),
                        EntityCondition.makeCondition("allowSolicitation", EntityOperator.EQUALS, null),
                        EntityCondition.makeCondition("allowSolicitation", EntityOperator.EQUALS, "")), EntityOperator.OR));
                    List < GenericValue > pacm = EntityQuery.use(delegator).from("PartyAndContactMech")
                        .where(conditions).filterByDate().queryList();
                    if (pacm != null && pacm.size() > 0) {
                        for (GenericValue pacmGV: pacm) {
                            String purpose = "";
                            JSONObject data = new JSONObject();
                            String contactMechId = pacmGV.getString("contactMechId");
                            String infoString = pacmGV.getString("infoString");
                            String telecomNumber = pacmGV.getString("tnContactNumber");
                            String solicitation = pacmGV.getString("allowSolicitation");
                            if (UtilValidate.isEmpty(solicitation)) {
                                solicitation = "Y";
                            }
                            List < GenericValue > pcmpEmail = EntityQuery.use(delegator).from("PartyContactMechPurpose")
                                .where(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, contactPartyId),
                                    EntityCondition.makeCondition("contactMechId", EntityOperator.EQUALS, contactMechId))
                                .filterByDate().queryList();
                            if (pcmpEmail != null && pcmpEmail.size() > 0) {
                                int i = 1;
                                for (GenericValue pcmpEmailGV: pcmpEmail) {
                                    GenericValue contactMechPurposeType = delegator.findOne("ContactMechPurposeType", UtilMisc.toMap("contactMechPurposeTypeId", pcmpEmailGV.getString("contactMechPurposeTypeId")), false);
                                    if (pcmpEmail != null && pcmpEmail.size() > 0) {
                                        purpose = purpose + contactMechPurposeType.getString("description");
                                        if (i < pcmpEmail.size())
                                            purpose = purpose + ", ";
                                    }
                                    i++;
                                }
                            }
                            data.put("infoString", infoString);
                            data.put("contactNumber", telecomNumber);
                            data.put("solicitation", solicitation);
                            data.put("contactPartyId", contactPartyId);
                            data.put("partyId", partyId);
                            data.put("contactMechId", contactMechId);
                            data.put("purpose", purpose);
                            data.put("partyRelAssocId", partyRelAssocId);
                            data.put("rowId", rowId);
                            data.put("targetRoleTypeId", targetRoleTypeId);
                            datas.add(data);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Debug.logError(e.getMessage(), MODULE);
        }
        resp.put("data", datas);

        return AjaxEvents.doJSONResponse(response, resp);
    }
    public static String addContactsToAssoc(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        String partyId = request.getParameter("partyId");
        String contactPartyId = request.getParameter("contactPartyId");
        String contactMechId = request.getParameter("contactMechId");
        String partyRelAssocId = request.getParameter("partyRelAssocId");
        String contactType = request.getParameter("contactType");
        String partyRoleTypeId = request.getParameter("partyRoleTypeId");
        try {
            if (UtilValidate.isNotEmpty(partyId) && UtilValidate.isNotEmpty(contactMechId) && UtilValidate.isNotEmpty(contactPartyId) && UtilValidate.isNotEmpty(partyRelAssocId) && UtilValidate.isNotEmpty(contactType) && UtilValidate.isNotEmpty(partyRoleTypeId)) {
                EntityCondition searchConditions = EntityCondition.makeCondition(EntityOperator.AND,
                    EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, contactPartyId),
                    EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, partyId),
                    EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "CONTACT"),
                    EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, partyRoleTypeId),
                    EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "CONTACT_REL_INV"),
                    EntityCondition.makeCondition("partyRelAssocId", EntityOperator.EQUALS, partyRelAssocId),
                    EntityUtil.getFilterByDateExpr());

                GenericValue existingRelationship = EntityUtil.getFirst(delegator.findList("PartyRelationship", searchConditions, null, null, null, false));
                if (UtilValidate.isNotEmpty(existingRelationship) && ("EMAIL".equalsIgnoreCase(contactType) || "PHONE".equalsIgnoreCase(contactType) || "DESIGNATION".equalsIgnoreCase(contactType))) {
                    
                	if ("DESIGNATION".equalsIgnoreCase(contactType)) {
                		GenericValue designationAssoc = EntityUtil.getFirst(delegator.findByAnd("ContactDesignationAssoc", UtilMisc.toMap("partyId", partyId, "contactId", contactPartyId, "designationEnumId", contactMechId, "partyRelAssocId", partyRelAssocId), null, false));
                        if (UtilValidate.isEmpty(designationAssoc)) {
                        	
                        	designationAssoc = delegator.makeValue("ContactDesignationAssoc", UtilMisc.toMap("partyId", partyId, "contactId", contactPartyId, "designationEnumId", contactMechId, "partyRelAssocId", partyRelAssocId));
                        	String contactDesignationAssocId = delegator.getNextSeqId("ContactDesignationAssoc");
                        	
                        	designationAssoc.put("contactDesignationAssocId", contactDesignationAssocId);
                        	
                        	//designationAssoc.put("sequenceNumber", new Long(seqId));
                        	designationAssoc.put("designationName", EnumUtil.getEnumDescription(delegator, contactMechId, "DESIGNATION"));
                        	
                        	designationAssoc.create();
                        }

                        contactMechId = designationAssoc.getString("contactDesignationAssocId");
                	}
            		GenericValue partyRelAssoc = EntityUtil.getFirst(delegator.findByAnd("PartyRelationshipAssoc", UtilMisc.toMap("partyRelAssocId", partyRelAssocId, "assocTypeId", contactType, "assocId", contactMechId), null, false));
                    if (UtilValidate.isEmpty(partyRelAssoc)) {
                        String assocSeqId = "0";
                        List<GenericValue> partyRelationshipAssocList = delegator.findList("PartyRelationshipAssoc", EntityCondition.makeCondition("partyRelAssocId",EntityOperator.EQUALS,partyRelAssocId), UtilMisc.toSet("assocSeqId"), UtilMisc.toList("assocSeqId DESC"), null, false);
                        if(partyRelationshipAssocList != null && partyRelationshipAssocList.size() >0) {
                            GenericValue partyRelationshipAssoc = partyRelationshipAssocList.get(0);
                            assocSeqId = partyRelationshipAssoc.getString("assocSeqId");
                        }
                        
                        int seqId = Integer.parseInt(assocSeqId)+1;
                        String assSeqId = StringUtils.leftPad(""+seqId, 4, "0");
                        partyRelAssoc = delegator.makeValue("PartyRelationshipAssoc");
                        partyRelAssoc.put("partyRelAssocId", partyRelAssocId);
                        partyRelAssoc.put("assocSeqId", Long.valueOf(assSeqId));
                        partyRelAssoc.put("assocTypeId", contactType);
                        partyRelAssoc.put("assocId", contactMechId);
                        if ("EMAIL".equalsIgnoreCase(contactType) || "PHONE".equalsIgnoreCase(contactType)) {
                            partyRelAssoc.put("solicitationStatus", "Y");
                        }
                        partyRelAssoc.create();
                        
                        if ("EMAIL".equalsIgnoreCase(contactType) || "PHONE".equalsIgnoreCase(contactType)) {
                            List<GenericValue> partyContactMechPurposeList = EntityQuery.use(delegator).from("PartyContactMechPurpose")
                                .where("partyId", contactPartyId, "contactMechId", contactMechId).filterByDate().queryList();
                            if(partyContactMechPurposeList != null && partyContactMechPurposeList.size() > 0) {
                                for(GenericValue partyContactMechPurposeGV : partyContactMechPurposeList) {
                                    partyContactMechPurposeGV.set("partyRelAssocId", partyRelAssocId);
                                }
                                delegator.storeAll(partyContactMechPurposeList);
                            }
                        }
                        
                    }
                		
                }
            }
        } catch (Exception e) {
            Debug.logInfo("==========================ERROR======================" + e.toString(), "");
        }
        return AjaxEvents.doJSONResponse(response, UtilMisc.toMap("data", null));
    }
    
    public static String updatePartyContactAssoc(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        GenericValue userLogin = (GenericValue) request.getSession(true).getAttribute("userLogin");
        String partyId = request.getParameter("partyId");
        String contactPartyId = request.getParameter("contactPartyId");
        String contactMechId = request.getParameter("contactMechId");
        String partyRelAssocId = request.getParameter("partyRelAssocId");
        String contactType = request.getParameter("contactType");
        String infoString = request.getParameter("infoString");
        String solicitation = request.getParameter("solicitation");
        String contactNumber = request.getParameter("contactNumber");
        String designationName = request.getParameter("designationName");
        String partyRoleTypeId = request.getParameter("partyRoleTypeId");
        Timestamp now = UtilDateTime.nowTimestamp();
        try {
            if (UtilValidate.isNotEmpty(partyId) && UtilValidate.isNotEmpty(contactMechId) && UtilValidate.isNotEmpty(contactPartyId) && UtilValidate.isNotEmpty(partyRelAssocId) && UtilValidate.isNotEmpty(contactType) && UtilValidate.isNotEmpty(partyRoleTypeId)) {
                EntityCondition searchConditions = EntityCondition.makeCondition(EntityOperator.AND,
                    EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, contactPartyId),
                    EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, partyId),
                    EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "CONTACT"),
                    EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, partyRoleTypeId),
                    EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "CONTACT_REL_INV"),
                    EntityCondition.makeCondition("partyRelAssocId", EntityOperator.EQUALS, partyRelAssocId),
                    EntityUtil.getFilterByDateExpr());

                GenericValue existingRelationship = EntityUtil.getFirst(delegator.findList("PartyRelationship", searchConditions, null, null, null, false));
                if (UtilValidate.isEmpty(solicitation)) {
                    solicitation = "Y";
                }
                if (UtilValidate.isNotEmpty(infoString) && existingRelationship != null && existingRelationship.size() > 0 && "EMAIL".equalsIgnoreCase(contactType)) {
                    List < GenericValue > pcmpEmail = EntityQuery.use(delegator).from("PartyContactMechPurpose")
                        .where(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, contactPartyId),
                            EntityCondition.makeCondition("contactMechId", EntityOperator.EQUALS, contactMechId),
                            EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.IN, UtilMisc.toList("AOS_EMAIL_ADDRESS", "IDEAL_EMAIL_ADDRESS")))
                        .filterByDate().queryList();
                    if (pcmpEmail != null && pcmpEmail.size() > 0) {
                        List < GenericValue > pcmpEmailExpire = EntityQuery.use(delegator).from("PartyContactMechPurpose")
                            .where(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, contactPartyId),
                                EntityCondition.makeCondition("contactMechId", EntityOperator.EQUALS, contactMechId),
                                EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.NOT_IN, UtilMisc.toList("AOS_EMAIL_ADDRESS", "IDEAL_EMAIL_ADDRESS")))
                            .filterByDate().queryList();
                        if (pcmpEmailExpire != null && pcmpEmailExpire.size() > 0) {
                            for (GenericValue pcmpEmailExpireGV: pcmpEmailExpire) {
                                pcmpEmailExpireGV.put("thruDate", now);
                            }
                            delegator.storeAll(pcmpEmailExpire);
                        }
                        Map < String, Object > inputEmail = UtilMisc.toMap("userLogin", userLogin, "emailAddress", infoString, "partyId", contactPartyId, "contactMechTypeId", "EMAIL_ADDRESS", "contactMechPurposeTypeId", "PRIMARY_EMAIL", "allowSolicitation", solicitation);
                        Map < String, Object > serviceResultsEmail = dispatcher.runSync("createPartyEmailAddress", inputEmail);
                        if (ServiceUtil.isSuccess(serviceResultsEmail)) {
                            String emailContactMechId = (String) serviceResultsEmail.get("contactMechId");
                            if (UtilValidate.isNotEmpty(emailContactMechId)) {
                                List < GenericValue > partyContactMechPurposeEmail = EntityQuery.use(delegator).from("PartyContactMechPurpose")
                                    .where("partyId", contactPartyId, "contactMechId", emailContactMechId).filterByDate().queryList();
                                if (partyContactMechPurposeEmail != null && partyContactMechPurposeEmail.size() > 0) {
                                    for (GenericValue partyContactMechPurposeEmailGV: partyContactMechPurposeEmail) {
                                        partyContactMechPurposeEmailGV.put("partyRelAssocId", partyRelAssocId);
                                    }
                                    delegator.storeAll(partyContactMechPurposeEmail);

                                }

                                GenericValue partyRelAssocEmail = EntityUtil.getFirst(delegator.findByAnd("PartyRelationshipAssoc", UtilMisc.toMap("partyRelAssocId", partyRelAssocId, "assocTypeId", "EMAIL", "assocId", emailContactMechId), null, false));
                                if (partyRelAssocEmail == null || partyRelAssocEmail.size() < 1) {
                                    partyRelAssocEmail = delegator.makeValue("PartyRelationshipAssoc");
                                    partyRelAssocEmail.put("partyRelAssocId", partyRelAssocId);
                                    partyRelAssocEmail.put("assocSeqId", delegator.getNextSeqIdLong("PartyRelationshipAssoc"));
                                    partyRelAssocEmail.put("assocTypeId", "EMAIL");
                                    partyRelAssocEmail.put("assocId", emailContactMechId);
                                    partyRelAssocEmail.put("solicitationStatus", solicitation);
                                    partyRelAssocEmail.create();
                                }
                            }
                        }
                    } else {
                        GenericValue contactMech = EntityQuery.use(delegator).from("ContactMech")
                            .where("contactMechId", contactMechId).queryOne();
                        if (contactMech != null && contactMech.size() > 0) {
                            contactMech.put("infoString", infoString);
                            contactMech.store();

                            GenericValue partyContactMechEmail = EntityQuery.use(delegator).from("PartyContactMech")
                                .where("partyId", contactPartyId, "contactMechId", contactMechId).filterByDate().queryOne();
                            if (partyContactMechEmail != null && partyContactMechEmail.size() > 0) {
                                partyContactMechEmail.put("allowSolicitation", solicitation);
                                partyContactMechEmail.store();
                            }

                            GenericValue partyRelationshipAssocEmail = EntityQuery.use(delegator).from("PartyRelationshipAssoc")
                                .where("partyRelAssocId", partyRelAssocId, "assocId", contactMechId, "assocTypeId", "EMAIL").queryFirst();
                            if (partyRelationshipAssocEmail != null && partyRelationshipAssocEmail.size() > 0) {
                                partyRelationshipAssocEmail.put("solicitationStatus", solicitation);
                                partyRelationshipAssocEmail.store();
                            }
                        }
                    }
                } else if (UtilValidate.isNotEmpty(contactNumber) && existingRelationship != null && existingRelationship.size() > 0 && "PHONE".equalsIgnoreCase(contactType)) {
                    List < GenericValue > pcmpPhone = EntityQuery.use(delegator).from("PartyContactMechPurpose")
                        .where(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, contactPartyId),
                            EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.IN, UtilMisc.toList("AOS_MOBILE_PHONE", "IDEAL_MOBILE_PHONE", "IDEAL_PRIMARY_PHONE")),
                            EntityCondition.makeCondition("contactMechId", EntityOperator.EQUALS, contactMechId)
                        	)
                        .filterByDate().queryList();
                    if (pcmpPhone != null && pcmpPhone.size() > 0) {
                        List < GenericValue > pcmpPhoneExpire = EntityQuery.use(delegator).from("PartyContactMechPurpose")
                            .where(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, contactPartyId),
                                EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.NOT_IN, UtilMisc.toList("AOS_MOBILE_PHONE", "IDEAL_MOBILE_PHONE", "IDEAL_PRIMARY_PHONE")),
                                EntityCondition.makeCondition("contactMechId", EntityOperator.EQUALS, contactMechId)
                            	)
                            .filterByDate().queryList();
                        if (pcmpPhoneExpire != null && pcmpPhoneExpire.size() > 0) {
                            for (GenericValue pcmpPhoneExpireGV: pcmpPhoneExpire) {
                                pcmpPhoneExpireGV.put("thruDate", now);
                            }
                            delegator.storeAll(pcmpPhoneExpire);
                        }
                        Map < String, Object > inputPhone = UtilMisc.toMap("userLogin", userLogin, "contactNumber", contactNumber, "partyId", contactPartyId, "contactMechTypeId", "TELECOM_NUMBER", "contactMechPurposeTypeId", "PRIMARY_PHONE", "allowSolicitation", solicitation);
                        Map < String, Object > serviceResultsPhone = dispatcher.runSync("createPartyTelecomNumber", inputPhone);
                        if (ServiceUtil.isSuccess(serviceResultsPhone)) {
                            String phoneContactMechId = (String) serviceResultsPhone.get("contactMechId");
                            if (UtilValidate.isNotEmpty(phoneContactMechId)) {
                                List < GenericValue > partyContactMechPurposePhone = EntityQuery.use(delegator).from("PartyContactMechPurpose")
                                    .where("partyId", contactPartyId, "contactMechId", phoneContactMechId).filterByDate().queryList();
                                if (partyContactMechPurposePhone != null && partyContactMechPurposePhone.size() > 0) {
                                    for (GenericValue partyContactMechPurposePhoneGV: partyContactMechPurposePhone) {
                                        partyContactMechPurposePhoneGV.put("partyRelAssocId", partyRelAssocId);
                                    }
                                    delegator.storeAll(partyContactMechPurposePhone);
                                }

                                GenericValue partyRelAssocPhone = EntityUtil.getFirst(delegator.findByAnd("PartyRelationshipAssoc", UtilMisc.toMap("partyRelAssocId", partyRelAssocId, "assocTypeId", "PHONE", "assocId", phoneContactMechId), null, false));
                                if (partyRelAssocPhone == null || partyRelAssocPhone.size() < 1) {
                                    partyRelAssocPhone = delegator.makeValue("PartyRelationshipAssoc");
                                    partyRelAssocPhone.put("partyRelAssocId", partyRelAssocId);
                                    partyRelAssocPhone.put("assocSeqId", delegator.getNextSeqIdLong("PartyRelationshipAssoc"));
                                    partyRelAssocPhone.put("assocTypeId", "PHONE");
                                    partyRelAssocPhone.put("assocId", phoneContactMechId);
                                    partyRelAssocPhone.put("solicitationStatus", solicitation);
                                    partyRelAssocPhone.create();
                                }
                            }
                        }
                    } else {
                        GenericValue telecomNumber = EntityQuery.use(delegator).from("TelecomNumber")
                            .where("contactMechId", contactMechId).queryOne();
                        if (telecomNumber != null && telecomNumber.size() > 0) {
                            telecomNumber.put("contactNumber", contactNumber);
                            telecomNumber.store();

                            GenericValue partyContactMechPhone = EntityQuery.use(delegator).from("PartyContactMech")
                                .where("partyId", contactPartyId, "contactMechId", contactMechId).filterByDate().queryOne();
                            if (partyContactMechPhone != null && partyContactMechPhone.size() > 0) {
                                partyContactMechPhone.put("allowSolicitation", solicitation);
                                partyContactMechPhone.store();
                            }

                            GenericValue partyRelationshipAssocPhone = EntityQuery.use(delegator).from("PartyRelationshipAssoc")
                                .where("partyRelAssocId", partyRelAssocId, "assocId", contactMechId, "assocTypeId", "PHONE").queryFirst();
                            if (partyRelationshipAssocPhone != null && partyRelationshipAssocPhone.size() > 0) {
                                partyRelationshipAssocPhone.put("solicitationStatus", solicitation);
                                partyRelationshipAssocPhone.store();
                            }
                        }
                    }
                } else if (existingRelationship != null && existingRelationship.size() > 0 && "DESIGNATION".equalsIgnoreCase(contactType)) {
                	GenericValue designationAssoc = EntityUtil.getFirst(delegator.findByAnd("ContactDesignationAssoc", UtilMisc.toMap("contactDesignationAssocId", contactMechId), null, false));
                    if (UtilValidate.isNotEmpty(designationAssoc)) {
                    	if (UtilValidate.isNotEmpty(designationName)) {
                    		designationAssoc.put("designationName", designationName);
                    		
                    		designationAssoc.store();
                    	}
                    }
                }
              //added for derive marketing field from allow solicitation
                if ("EMAIL".equalsIgnoreCase(contactType) || "PHONE".equalsIgnoreCase(contactType)) {

                	String isMarketable = "Y";
                	EntityCondition condition = EntityCondition.makeCondition(EntityOperator.AND,
                			EntityCondition.makeCondition("roleTypeId",EntityOperator.EQUALS,"CONTACT"),
                			EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,contactPartyId),
                			EntityCondition.makeCondition("allowSolicitation",EntityOperator.EQUALS,"Y"),
                			EntityCondition.makeCondition(EntityOperator.OR,
                					EntityCondition.makeCondition("contactMechTypeId",EntityOperator.EQUALS,"EMAIL_ADDRESS"),
                					EntityCondition.makeCondition("contactMechTypeId",EntityOperator.EQUALS,"TELECOM_NUMBER")));
                	List<GenericValue> partyContactMechList = EntityQuery.use(delegator).from("PartyAndContactMech").where(condition).queryList();
                	if (UtilValidate.isNotEmpty(partyContactMechList)) {
                		isMarketable = "Y";
                	}
                	else {
                		isMarketable = "N";
                	}
                	GenericValue partyRelShipGv = EntityUtil.getFirst( delegator.findByAnd("PartyRelationship", UtilMisc.toMap("partyIdTo", partyId,"partyIdFrom", contactPartyId), null, false) );
                	if(UtilValidate.isNotEmpty(partyRelShipGv)){
                		partyRelShipGv.put("isMarketable", isMarketable);
                		partyRelShipGv.store();
                	}
                	//ended 
                }
              }
        } catch (Exception e) {
            Debug.logInfo("==========================ERROR======================" + e.toString(), "");
        }
        return AjaxEvents.doJSONResponse(response, UtilMisc.toMap("data", null));
    }
    
    //added for merge party
    @SuppressWarnings("unused")
    public static Map<String, Object> mergePartyAndAssoc(DispatchContext dctx, Map < String, Object > context) {
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Delegator delegator = dctx.getDelegator();
    	Map < String, Object > result = ServiceUtil.returnSuccess();
    	String partyIdFrom = (String) context.get("fromPartyId");
    	String partyIdTo = (String) context.get("toPartyId");
    	String partyRoleTypeId = (String) context.get("partyRoleTypeId");
    	String name = "";
    	try {
    		//check picker party conditions
    		GenericValue successorParty = delegator.findOne("Party", UtilMisc.toMap("partyId", partyIdTo),false);
    		GenericValue predecessorParty = delegator.findOne("Party", UtilMisc.toMap("partyId", partyIdFrom),false);
    		// return when  from and to partyId with diff role
    		if(!predecessorParty.getString("roleTypeId").equals(successorParty.getString("roleTypeId"))) {
    			result.put(ModelService.ERROR_MESSAGE,"Please select "+partyRoleTypeId+" party!" );
    			return ServiceUtil.returnError("Please select "+partyRoleTypeId+" party!");
    		}
    		//return when screenrole and different picker roles
    		if(!( partyRoleTypeId.equals(successorParty.getString("roleTypeId"))))
    		{
    			result.put(ModelService.ERROR_MESSAGE,"Please select "+partyRoleTypeId+" party!" );
    			return ServiceUtil.returnError("Please select "+partyRoleTypeId+" party!");

    		}
    		//ended
    		List < GenericValue > partyRelationship = EntityQuery.use(delegator).from("PartyRelationship")
    				.where("partyIdTo", partyIdFrom, "partyRelationshipTypeId", "PREDECESSOR_FOR").filterByDate().queryList();
    		if (partyRelationship.isEmpty() && partyRelationship.size() == 0){

    			if (successorParty.getString("statusId") == null || !"PARTY_DISABLED".equals(successorParty.getString("statusId"))) {
    				if (("LEAD".equals(partyRoleTypeId) || "ACCOUNT".equals(partyRoleTypeId) || "CUSTOMER".equals(partyRoleTypeId) || "CONTACT".equals(partyRoleTypeId)))
    				{
    					if (UtilValidate.isNotEmpty(partyIdFrom) || UtilValidate.isNotEmpty(partyIdTo)) {
    						GenericValue partyMergeAssoc = delegator.makeValue("PartyRelationship");
    						partyMergeAssoc.put("partyIdFrom", partyIdFrom);
    						partyMergeAssoc.put("partyIdTo", partyIdTo);
    						partyMergeAssoc.put("roleTypeIdFrom", partyRoleTypeId);
    						partyMergeAssoc.put("roleTypeIdTo", partyRoleTypeId);
    						partyMergeAssoc.put("fromDate", UtilDateTime.nowTimestamp());
    						partyMergeAssoc.put("partyRelationshipTypeId", "PREDECESSOR_FOR");
    						partyMergeAssoc.create();
    						
    						//deactivate Account
    						GenericValue accountParty = delegator.findOne("Party", UtilMisc.toMap("partyId", partyIdFrom),false);
    						accountParty.put("statusId", "PARTY_DISABLED");
    						accountParty.store();
    						delegator.create("PartyDeactivation", UtilMisc.toMap("partyId", partyIdFrom, "deactivationTimestamp", UtilDateTime.nowTimestamp()));
    						
    						//get name of customer
    						GenericValue partySummary = EntityQuery.use(delegator).from("PartySummaryDetailsView").where("partyId", partyIdFrom).cache(false).queryOne();
    						if("LEAD".equals(partyRoleTypeId) || "ACCOUNT".equals(partyRoleTypeId)){
    							name = partySummary.getString("groupName");
    						}
    						else if("CONTACT".equals(partyRoleTypeId) || "CUSTOMER".equals(partyRoleTypeId)) {
    							name = partySummary.getString("firstName");
    							if (UtilValidate.isNotEmpty(partySummary.getString("lastName"))) {
    								if (UtilValidate.isNotEmpty(name)) {
    									name = name + " " + partySummary.getString("lastName");
    								} else {
    									name = partySummary.getString("lastName");
    								}
    							}	
    						}
    					}
    				}
    				else {
    					result.put(ModelService.ERROR_MESSAGE,"Please select "+partyRoleTypeId+" party!" );
    					return ServiceUtil.returnError("Please select "+partyRoleTypeId+" party!");
    				}
    			}
    		}
    		else {
    			result.put(ModelService.ERROR_MESSAGE,"Can not merge "+ partyIdFrom +" !It is successor of another Party." );
    			return ServiceUtil.returnError("Can not merge "+ partyIdFrom +" !It is successor of another Party.");
    		}
    	}
    	catch (Exception ex ) { 
    	}
    	result.put("partyId", partyIdTo);
    	result.put(ModelService.SUCCESS_MESSAGE, name+" "+partyRoleTypeId+" is successfully Merged!");
    	return result;
    }
    
    public static Map<String, Object> performContactAndPartyAssoc(DispatchContext dctx, Map<String, Object> context) {
    	LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Map<String, Object> result = ServiceUtil.returnSuccess();
        String partyId = (String) context.get("partyId");
        String assocPartyId = (String) context.get("assocPartyId");
        String roleTypeId = (String) context.get("roleTypeId");
        
        Map<String, Object> callCtxt = new HashMap<String, Object>();
		Map<String, Object> callResult = new HashMap<String, Object>();
        try {
            if (UtilValidate.isNotEmpty(partyId) && UtilValidate.isNotEmpty(assocPartyId) && UtilValidate.isNotEmpty(roleTypeId)) {
            	if (UtilValidate.isNotEmpty(assocPartyId)) {
        			GenericValue assocParty = EntityQuery.use(delegator).from("Party").where("partyId", assocPartyId).queryFirst();
        			if (UtilValidate.isNotEmpty(assocParty)) {
        				if (UtilValidate.isNotEmpty(roleTypeId) && (roleTypeId.equals("ACCOUNT") || roleTypeId.equals("LEAD"))) {
        					callCtxt = new HashMap<String, Object>();
        					callCtxt.put("partyIdTo", assocPartyId);
        					callCtxt.put("partyIdFrom", partyId);
        					callCtxt.put("partyRoleTypeId", roleTypeId);
        					callCtxt.put("userLogin", userLogin);
        					callResult = dispatcher.runSync("common.getContactAndPartyAssoc", callCtxt);
        					if (UtilValidate.isNotEmpty(callResult) && UtilValidate.isEmpty(callResult.get("partyContactAssoc"))) {
        						String serviceName = null;
        						String partyParamName = null;
        						if (roleTypeId.equals("ACCOUNT")) {
        							serviceName = "crmsfa.assignContactToAccount";
        							partyParamName = "accountPartyId";
        						} else if (roleTypeId.equals("LEAD")) {
        							serviceName = "crmsfa.assignContactToLead";
        							partyParamName = "leadPartyId";
        						}
        						
        						callCtxt = new HashMap<String, Object>();
        						callCtxt.put("contactPartyId", partyId);
        						callCtxt.put(partyParamName, assocPartyId);
        						callCtxt.put("party", roleTypeId);
        						callCtxt.put("userLogin", userLogin);
        						callResult = dispatcher.runSync(serviceName, callCtxt);
        						if (ServiceUtil.isSuccess(callResult)) {
        							Debug.logInfo("Contact# "+partyId+" associated with assocPartyId# "+assocPartyId+", roleTypeId# "+roleTypeId, MODULE);
        						}
        					}
        				}
        			}
        		}
            }
        } catch (Exception ex) {
            Debug.logInfo("==========================ERROR======================" + ex.toString(), "");
        }
        return result;
    }
    
}