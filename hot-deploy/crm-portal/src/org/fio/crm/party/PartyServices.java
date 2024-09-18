package org.fio.crm.party;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fio.crm.constants.CrmConstants.ValidationAuditType;
import org.fio.crm.util.UtilCommon;
import org.fio.crm.util.UtilMessage;
import org.fio.crm.writer.WriterUtil;
import org.fio.homeapps.util.DataUtil;
import org.groupfio.common.portal.util.UtilCampaign;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityTypeUtil;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

public class PartyServices {
	public static final String module = PartyServices.class.getName();
    public static final String resource = "PartyErrorUiLabels";
    public static final String crmResource = "crmUiLabels";
	public static Map<String, Object> createPartyGroup(DispatchContext ctx, Map<String, ? extends Object> context) {
        Map<String, Object> result = FastMap.newInstance();
        Delegator delegator = ctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Timestamp now = UtilDateTime.nowTimestamp();

        String partyId = (String) context.get("partyId");
        Locale locale = (Locale) context.get("locale");
        String errMsg = null;

        // partyId might be empty, so check it and get next seq party id if empty
        if (UtilValidate.isEmpty(partyId)) {
            try {
                partyId = delegator.getNextSeqId("Party");
            } catch (IllegalArgumentException e) {
                errMsg = UtilProperties.getMessage(resource,"partyservices.could_not_create_party_group_generation_failure", locale);
                return ServiceUtil.returnError(errMsg);
            }
        } 
        
        //Commented by Prabhu
        /*else {
            // if specified partyId starts with a number, return an error
            if (partyId.matches("\\d+")) {
                errMsg = UtilProperties.getMessage(resource,"partyservices.could_not_create_party_ID_digit", locale);
                return ServiceUtil.returnError(errMsg);
            }
        }*/
        //End
        try {
            // check to see if party object exists, if so make sure it is PARTY_GROUP type party
            GenericValue party = delegator.findOne("Party", UtilMisc.toMap("partyId", partyId),false);
            GenericValue partyGroupPartyType = delegator.findOne("PartyType", UtilMisc.toMap("partyTypeId", "PARTY_GROUP"),true);

            if (partyGroupPartyType == null) {
                errMsg = UtilProperties.getMessage(resource,"partyservices.party_type_not_found_in_database_cannot_create_party_group", locale);
                return ServiceUtil.returnError(errMsg);
            }

            if (party != null) {
                GenericValue partyType = party.getRelatedOne("PartyType",true);

                if (!EntityTypeUtil.isType(partyType, partyGroupPartyType)) {
                    errMsg = UtilProperties.getMessage(resource,"partyservices.cannot_create_party_group_already_exists_not_PARTY_GROUP_type", locale);
                    return ServiceUtil.returnError(errMsg);
                }
            } else {
                // create a party if one doesn't already exist
                String partyTypeId = "PARTY_GROUP";

                if (UtilValidate.isNotEmpty(context.get("partyTypeId"))) {
                    GenericValue desiredPartyType = delegator.findOne("PartyType", UtilMisc.toMap("partyTypeId", context.get("partyTypeId")),true);
                    if (desiredPartyType != null && EntityTypeUtil.isType(desiredPartyType, partyGroupPartyType)) {
                        partyTypeId = desiredPartyType.getString("partyTypeId");
                    } else {
                        return ServiceUtil.returnError("The specified partyTypeId [" + context.get("partyTypeId") + "] could not be found or is not a sub-type of PARTY_GROUP");
                    }
                }

                Map<String, Object> newPartyMap = UtilMisc.toMap("partyId", partyId, "partyTypeId", partyTypeId, "createdDate", now, "lastModifiedDate", now);
                if (userLogin != null) {
                    newPartyMap.put("createdByUserLogin", userLogin.get("userLoginId"));
                    newPartyMap.put("lastModifiedByUserLogin", userLogin.get("userLoginId"));
                }

                String statusId = (String) context.get("statusId");
                party = delegator.makeValue("Party", newPartyMap);
                party.setNonPKFields(context);

                if (statusId == null) {
                    statusId = "PARTY_ENABLED";
                }
                party.set("statusId", statusId);
                party.create();

                // create the status history
                GenericValue partyStat = delegator.makeValue("PartyStatus",
                        UtilMisc.toMap("partyId", partyId, "statusId", statusId, "statusDate", now));
                partyStat.create();
            }

            GenericValue partyGroup = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", partyId),false);
            if (partyGroup != null) {
                errMsg = UtilProperties.getMessage(resource,"partyservices.cannot_create_party_group_already_exists", locale);
                return ServiceUtil.returnError(errMsg);
            }

            partyGroup = delegator.makeValue("PartyGroup", UtilMisc.toMap("partyId", partyId));
            partyGroup.setNonPKFields(context);
            partyGroup.create();

        } catch (GenericEntityException e) {
            Debug.logWarning(e, module);
            Map<String, String> messageMap = UtilMisc.toMap("errMessage", e.getMessage());
            errMsg = UtilProperties.getMessage(resource,"partyservices.data_source_error_adding_party_group", messageMap, locale);
            return ServiceUtil.returnError(errMsg);
        }

        result.put("partyId", partyId);
        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return result;
    }
    //Create Custom CrossReference
    public static Map<String, Object> createCustomCrossReference(DispatchContext dctx, Map<String, Object> context) {
        Delegator delegator = dctx.getDelegator();
        String crossReferenceId = (String) context.get("crossReferenceId");
        String referenceValue = (String) context.get("referenceValue");
        String partyId = (String) context.get("partyId");
        try{
            if(UtilValidate.isNotEmpty(crossReferenceId) && UtilValidate.isNotEmpty(partyId)){
              GenericValue partyIdentification = EntityQuery.use(delegator).from("PartyIdentification").where("partyIdentificationTypeId", crossReferenceId).queryOne();
           if(UtilValidate.isNotEmpty(partyIdentification)) {
        	   partyIdentification.set("idValue", referenceValue);
        	   partyIdentification.store();
           }else {
               String partyIdentificationVal=org.fio.homeapps.util.DataUtil.storePartyIdentification(delegator, partyId, referenceValue,crossReferenceId);
           }
            }
        } catch (Exception e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError("ErrorwithService: " + e.getMessage());
        }
        return ServiceUtil.returnSuccess();
    }
    /**
     * Add a PartyNote.
     * @param dctx The DispatchContext that this service is operating in.
     * @param context Map containing the input parameters.
     * @return Map with the result of the service, the output parameters.
     */
    public static Map<String, Object> createPartyNote(DispatchContext dctx, Map<String, ? extends Object> context) {
        Map<String, Object> result = new HashMap<String, Object>();
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String noteString = (String) context.get("note");
        String partyId = (String) context.get("partyId");
        String noteId = (String) context.get("noteId");
        String noteName = (String) context.get("noteName");
        String isImportant = (String) context.get("isImportant");
        String campaignNoteId = (String) context.get("campaignNoteId");
        Locale locale = (Locale) context.get("locale");
        
        String noteType = (String) context.get("noteType"); 
        String callBackDate = (String) context.get("callBackDate");
        String subProduct = (String) context.get("subProduct");
        String noteTypeId = (String) context.get("noteTypeId");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String lastContactDate = sdf.format(new Date());
        
        //Map noteCtx = UtilMisc.toMap("note", noteString, "userLogin", userLogin);

        //Make sure the note Id actually exists if one is passed to avoid a foreign key error below
        if (noteId != null) {
            try {
                GenericValue value = EntityQuery.use(delegator).from("NoteData").where("noteId", noteId).queryOne();
                if (value == null) {
                    Debug.logError("ERROR: Note id does not exist for : " + noteId + ", autogenerating." , module);
                    noteId = null;
                }
            } catch (GenericEntityException e) {
                Debug.logError(e, "ERROR: Note id does not exist for : " + noteId + ", autogenerating." , module);
                noteId = null;
            }
        }

        // if no noteId is specified, then create and associate the note with the userLogin
        if (noteId == null) {
            Map<String, Object> noteRes = null;
            try {
                noteRes = dispatcher.runSync("createNote", UtilMisc.toMap("partyId", userLogin.getString("partyId"),
                         "note", noteString, "userLogin", userLogin, "locale", locale, "noteName", noteName));
            } catch (GenericServiceException e) {
                Debug.logError(e, e.getMessage(), module);
                return ServiceUtil.returnError(UtilProperties.getMessage(resource, 
                        "PartyNoteCreationError", UtilMisc.toMap("errorString", e.getMessage()), locale));
            }

            if (noteRes.get(ModelService.RESPONSE_MESSAGE).equals(ModelService.RESPOND_ERROR))
                return noteRes;

            noteId = (String) noteRes.get("noteId");

            if (UtilValidate.isEmpty(noteId)) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource,
                        "partyservices.problem_creating_note_no_noteId_returned", locale));
            }else
            {
            	GenericValue noteData = null;
            	try {
            		noteData = delegator.findOne("NoteData", false, UtilMisc.toMap("noteId",noteId));
            		if(UtilValidate.isNotEmpty(noteType))
            		{
            			noteData.put("noteType", noteType);
            		}
            		if(UtilValidate.isNotEmpty(callBackDate))
            		{
            			/*String pattern = "dd-MM-yyyy";
            			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

            			Date callBkDate = simpleDateFormat.parse(callBackDate);
            			noteData.put("callBackDate", new Timestamp(callBkDate.getTime()));*/
            			//noteData.put("callBackDate", callBackDate);
            			try {
                            Date callBackDate1 = new SimpleDateFormat("dd-MM-yyyy").parse(callBackDate);
                            callBackDate = sdf.format(callBackDate1);
                            noteData.put("callBackDate", java.sql.Date.valueOf(callBackDate));
                			}catch(ParseException pe) {
                				Debug.log("====ParseException==="+pe.getMessage());
                			}
            		}
            		if(UtilValidate.isNotEmpty(subProduct))
            		{
            			noteData.put("subProduct", subProduct);
            		}
            		noteData.store();
            		GenericValue partySupplData = delegator.findOne("PartySupplementalData", UtilMisc.toMap("partyId", partyId),false);
                	if (UtilValidate.isNotEmpty(partySupplData)) {
                		if(UtilValidate.isNotEmpty(callBackDate))
                		{
                              partySupplData.set("lastCallBackDate", java.sql.Date.valueOf(callBackDate));
                              partySupplData.put("lastContactDate", java.sql.Date.valueOf(lastContactDate));
                			  partySupplData.store();
                			
                		}
            			
                	}
                	

            	} catch (Exception e1) {
            		// TODO Auto-generated catch block
            		//e1.printStackTrace();
		    		Debug.logError(e1.getMessage(), module);

            	}

            }
        }
        result.put("noteId", noteId);
        if("on".equals(isImportant)){
            isImportant = "Y";
        }

        // Set the party info
        try {
            Map<String, String> fields = UtilMisc.toMap("partyId", partyId, "noteId", noteId,"isImportant",isImportant,"campaignId",campaignNoteId);
            GenericValue v = delegator.makeValue("PartyNote", fields);

            delegator.create(v);
        } catch (GenericEntityException ee) {
            Debug.logError("Exception in create party note"+ee.getMessage(), module);
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, UtilProperties.getMessage(resource,
                    "partyservices.problem_associating_note_with_party", 
                    UtilMisc.toMap("errMessage", ee.getMessage()), locale));
        }
        result = ServiceUtil.returnSuccess(UtilProperties.getMessage(crmResource,
                "noteCreateSuccess", locale));
        return result;
    }
    //update Note
    public static String updateCustomerNote(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        String noteId = request.getParameter("noteId");
        String text = request.getParameter("note");
        // a note may be for a Party or a Case
        String partyId = request.getParameter("partyId"); 
        String campaignListId = request.getParameter("campaignListId");
        Locale locale = UtilHttp.getLocale(request);
        String returnMsg = null;
        String subProduct = (String) request.getParameter("subProduct");
        String noteType = request.getParameter("noteType"); 
        String callBackDate = request.getParameter("callBackDate");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String lastContactDate = sdf.format(new Date());
        try{
            if(UtilValidate.isNotEmpty(noteId)) {
                GenericValue note = EntityQuery.use(delegator).from("NoteData").where("noteId", noteId).queryOne();
                // update the note
                if(note != null && note.size() > 0) {
                    note.setString("noteInfo", text);
                    if(UtilValidate.isNotEmpty(noteType))
            		{
                    	note.put("noteType", noteType);
            		}
            		if(UtilValidate.isNotEmpty(callBackDate))
            		{
            			/*String pattern = "dd-MM-yyyy";
            			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

            			Date callBkDate = null;
						try {
							callBkDate = simpleDateFormat.parse(callBackDate);
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
            			note.put("callBackDate", new Timestamp(callBkDate.getTime()));*/
            			try {
            			Date callBackDate1 = new SimpleDateFormat("dd-MM-yyyy").parse(callBackDate);
                        callBackDate = sdf.format(callBackDate1);
                        note.put("callBackDate", java.sql.Date.valueOf(callBackDate));
            			}catch(ParseException pe) {
            				Debug.log("====ParseException==="+pe.getMessage());
            			}
            		}

            		if(UtilValidate.isNotEmpty(subProduct))
            		{
            			note.put("subProduct", subProduct);
            		}
                    note.store();
                    GenericValue partySupplData = delegator.findOne("PartySupplementalData", UtilMisc.toMap("partyId", partyId),false);
                	if (UtilValidate.isNotEmpty(partySupplData)) {
                		if(UtilValidate.isNotEmpty(callBackDate))
                		{
                			partySupplData.set("lastCallBackDate", java.sql.Date.valueOf(callBackDate));
                			partySupplData.put("lastContactDate", java.sql.Date.valueOf(lastContactDate));
              			    partySupplData.store();
                			
                		}
            			
                	}
                }
            }
        }
        catch (GenericEntityException e) {
            Debug.logError("Exception in update customer note"+e.getMessage(), module);
        }
        returnMsg = UtilProperties.getMessage(crmResource, "noteUpdatedSuccess", locale);
        request.setAttribute("_EVENT_MESSAGE_", returnMsg);
        return "success";
        
    }
    
    public static Map<String, Object> updatePartySupplementalData(DispatchContext dctx, Map<String, Object> context) {
    	Delegator delegator = dctx.getDelegator();
    	Locale locale = (Locale) context.get("locale");
    	String partyId = (String) context.get("partyId");
    	String contactMechTypeId = (String) context.get("contactMechTypeId");
    	String contactMechId = (String) context.get("contactMechId");
    	String newContactMechId = (String) context.get("newContactMechId");
    	Map<String, Object> results = ServiceUtil.returnSuccess();
    	String purpose = null;
    	String fieldToUpdate = null;
    	String mechId = null;
    	try {
    		if ("POSTAL_ADDRESS".equals(contactMechTypeId)) {
        		GenericValue locationGv = EntityQuery.use(delegator).from("SystemProperty").where("systemResourceId", "system","systemPropertyId","primary.location").queryOne();
        		String primaryLocation = "";
        		if(UtilValidate.isNotEmpty(locationGv)) {
        			primaryLocation = locationGv.getString("systemPropertyValue");
        		}
        		purpose = UtilValidate.isNotEmpty(primaryLocation) ? primaryLocation : "PRIMARY_LOCATION";
        		fieldToUpdate = "primaryPostalAddressId";
        	} else if ("TELECOM_NUMBER".equals(contactMechTypeId)) {
        		GenericValue phoneGv = EntityQuery.use(delegator).from("SystemProperty").where("systemResourceId", "system","systemPropertyId","primary.phone").queryOne();
        		String primaryPhone = "";
        		if(UtilValidate.isNotEmpty(phoneGv)) {
        			primaryPhone = phoneGv.getString("systemPropertyValue");
        		}
        		purpose = UtilValidate.isNotEmpty(primaryPhone) ? primaryPhone : "PRIMARY_PHONE";
        		fieldToUpdate = "primaryTelecomNumberId";
        	} else if ("EMAIL_ADDRESS".equals(contactMechTypeId)) {
        		GenericValue emailGv = EntityQuery.use(delegator).from("SystemProperty").where("systemResourceId", "system","systemPropertyId","primary.email").queryOne();
        		String primaryPhone = "";
        		if(UtilValidate.isNotEmpty(emailGv)) {
        			primaryPhone = emailGv.getString("systemPropertyValue");
        		}
        		purpose = UtilValidate.isNotEmpty(primaryPhone) ? primaryPhone : "PRIMARY_EMAIL";
        		fieldToUpdate = "primaryEmailId";
        	} else {
        		return results;
        	}
    		
    		mechId = contactMechId;
    		if(UtilValidate.isNotEmpty(newContactMechId)){
    			mechId = newContactMechId;
    		}
    		
    		List<EntityCondition> condition = FastList.newInstance();
    		EntityCondition mainCond = null;
    		//EntityCondition condition = null;
    		
    		Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();
            //nowTimeStamp.setMinutes(nowTimeStamp.getMinutes()+2);
            
	        condition.add(EntityCondition.makeCondition(
						EntityOperator.AND,
						EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,partyId),
						EntityCondition.makeCondition("contactMechId",EntityOperator.EQUALS,mechId),
						EntityCondition.makeCondition("contactMechTypeId",EntityOperator.EQUALS,contactMechTypeId)
					));
			
			condition.add(EntityCondition.makeCondition(EntityOperator.OR,
											EntityCondition.makeCondition("contactThruDate",EntityOperator.EQUALS,null),
											EntityCondition.makeCondition("contactThruDate",EntityOperator.GREATER_THAN_EQUAL_TO,nowTimeStamp)
											));
					
			condition.add(EntityCondition.makeCondition(EntityOperator.OR,
											EntityCondition.makeCondition("purposeThruDate",EntityOperator.EQUALS,null),
											EntityCondition.makeCondition("purposeThruDate",EntityOperator.GREATER_THAN_EQUAL_TO,nowTimeStamp)
	    									));
			if (condition.size() > 0) mainCond = EntityCondition.makeCondition(condition, EntityOperator.AND);
			
    		Debug.logInfo("Update Contact Mech Purpose " +purpose, "");
    		Debug.logInfo("Entity Condition-->"+mainCond, module);
    		
    		List<GenericValue> contactMechAndPurpose = delegator.findList("PartyContactWithPurpose", mainCond, UtilMisc.toSet("contactMechPurposeTypeId"), UtilMisc.toList("purposeFromDate DESC"), null, false);
    		if (UtilValidate.isNotEmpty(contactMechAndPurpose)) {
    			for (GenericValue contactMechPurpose : contactMechAndPurpose) {
    				if (purpose.equals(contactMechPurpose.getString("contactMechPurposeTypeId")) || "GENERAL_LOCATION".equals(contactMechPurpose.getString("contactMechPurposeTypeId"))) {
    					GenericValue partySupplData = delegator.findOne("PartySupplementalData", UtilMisc.toMap("partyId", partyId),false);
    					if (partySupplData != null && !mechId.equals(partySupplData.getString(fieldToUpdate))) {
    						partySupplData.set(fieldToUpdate, mechId);
    						partySupplData.store();
    					}
    				}
    			}
    		}
    	} catch (Exception e) {
    		return UtilMessage.createAndLogServiceError(e, module);
		}
    	
    	
    	return results;
    	
    }
    
    public static Map<String, Object> clearPartySupplementalData(DispatchContext dctx, Map<String, Object> context) {
        Delegator delegator = dctx.getDelegator();
        Locale locale = (Locale) context.get("locale");
        Map<String, Object> results = ServiceUtil.returnSuccess();

        String partyId = (String) context.get("partyId");
        String contactMechId = (String) context.get("contactMechId");

        try {
            EntityConditionList<EntityExpr> conditionList = EntityCondition.makeCondition(
                    UtilMisc.toList(
                            EntityCondition.makeCondition("partyId", partyId),
                            EntityCondition.makeCondition("contactMechId", contactMechId)
                    ), EntityOperator.AND
            );
            
                       
            GenericValue partyContactMech = EntityUtil.getFirst(delegator.findList("PartyContactMech", conditionList, null, UtilMisc.toList("fromDate DESC"), null, false));
            if (UtilValidate.isNotEmpty(partyContactMech)) {
                List<GenericValue> partyContactMechPurps = partyContactMech.getRelated("PartyContactMechPurpose", UtilMisc.toList("fromDate DESC"));
                if (UtilValidate.isNotEmpty(partyContactMechPurps)) {
                    for (GenericValue contactMechPurpose : partyContactMechPurps) {
                        String purposeTypeId = contactMechPurpose.getString("contactMechPurposeTypeId");
                        String fieldToUpdate = null;
                        List<EntityCondition> conditionsList = FastList.newInstance();
                        /*EntityFindOptions findOptions = new EntityFindOptions();
                        findOptions.setDistinct(true);*/
                        if ("GENERAL_LOCATION".equals(purposeTypeId) || "PRIMARY_LOCATION".equals(purposeTypeId)) {
                            fieldToUpdate = "primaryPostalAddressId";
                            //conditionsList.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.IN, UtilMisc.toList("GENERAL_LOCATION","PRIMARY_LOCATION")));
                            EntityCondition general = EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, UtilMisc.toList("GENERAL_LOCATION"));
                            EntityCondition primary = EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, UtilMisc.toList("PRIMARY_LOCATION"));
                            conditionsList.add(EntityCondition.makeCondition(general,EntityOperator.OR,primary));
                        } else if ("PRIMARY_PHONE".equals(purposeTypeId)) {
                            fieldToUpdate = "primaryTelecomNumberId";
                            conditionsList.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "PRIMARY_PHONE"));
                            
                        } else if ("PRIMARY_EMAIL".equals(purposeTypeId)) {
                            fieldToUpdate = "primaryEmailId";
                            conditionsList.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "PRIMARY_EMAIL"));
                        } else {
                            return results;
                        }
                        conditionsList.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId));
                        GenericValue partySupplData = delegator.findOne("PartySupplementalData", UtilMisc.toMap("partyId", partyId),false);
                        if (partySupplData != null && UtilValidate.isNotEmpty(partySupplData.getString(fieldToUpdate))) {
                            partySupplData.set(fieldToUpdate, null);
                            partySupplData.store();
                            
                            
                            List<GenericValue>   PartyContactMechPurposeList = delegator.findList("PartyContactMechPurpose", EntityCondition.makeCondition(conditionsList,EntityOperator.AND), UtilMisc.toSet("contactMechId"), null, null, false);
                            conditionsList = FastList.newInstance();
                            Set<String> cmidList = new TreeSet<String>();
                            for(int i=0;i<PartyContactMechPurposeList.size();i++){
                            	GenericValue PartyContactMechPurposeGv = PartyContactMechPurposeList.get(i);
                            	String cmid = PartyContactMechPurposeGv.getString("contactMechId");
                            	cmidList.add(cmid);
                            }
                            if(!cmidList.isEmpty()) {
                            	conditionsList.add(EntityCondition.makeCondition("contactMechId", EntityOperator.IN, UtilMisc.toList(cmidList)));
                            }
                            Timestamp nowTimeStamp = UtilDateTime.nowTimestamp();
                            //nowTimeStamp.setMinutes(nowTimeStamp.getMinutes()+2);
                            
                            
                            
                            EntityCondition contactMechIdCondition = EntityCondition.makeCondition(conditionsList, EntityOperator.OR);
                            EntityCondition partyIdCondition = EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId);
                            EntityCondition mainCondition = EntityCondition.makeCondition(partyIdCondition,EntityOperator.AND,contactMechIdCondition);
                            EntityCondition filterDateCondition = EntityCondition.makeCondition(
                            		EntityCondition.makeCondition("thruDate",EntityOperator.EQUALS,null),EntityOperator.OR,
                            		EntityCondition.makeCondition("thruDate",EntityOperator.GREATER_THAN,nowTimeStamp));
                            
                            EntityCondition mainCondition1 = EntityCondition.makeCondition(mainCondition,EntityOperator.AND,filterDateCondition);
                            
                            PartyContactMechPurposeList = delegator.findList("PartyContactMech", mainCondition1, null, UtilMisc.toList("lastUpdatedStamp DESC"),null,false);
                            
                            
                            PartyContactMechPurposeList = EntityUtil.filterByDate(PartyContactMechPurposeList);
                            
                            if(PartyContactMechPurposeList.size()>0){
                            	partySupplData.set(fieldToUpdate, PartyContactMechPurposeList.get(0).getString("contactMechId"));
                            	partySupplData.store();
                            }
                            
                        }
                    }
                }
            }
        } catch (GenericEntityException e) {
            return UtilMessage.createAndLogServiceError(e, module);
        }

        return results;
    }
    public static String deleteNote(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        Locale locale = UtilHttp.getLocale(request);
        String returnMsg = null;
        String noteId = request.getParameter("noteId");
        // a note may be for a Party or a Case
        //String partyId = request.getParameter("partyId");
        String targetPartyId = request.getParameter("targetPartyId");

        try {
            if (UtilValidate.isNotEmpty(noteId) && UtilValidate.isNotEmpty(targetPartyId)) {
                // get the note entity
                GenericValue note = delegator.findOne("NoteData", UtilMisc.toMap("noteId", noteId), false);
                // delete the PartyNote entity
                GenericValue partyNote = delegator.findOne("PartyNote", UtilMisc.toMap("noteId", noteId, "partyId", targetPartyId), false);
                if (partyNote != null && partyNote.size() > 0) {
                    partyNote.remove();
                }

                // delete the note
                if (note != null && note.size() > 0) {
                    note.remove();
                }
                returnMsg = UtilProperties.getMessage(crmResource, "noteDeleteSuccess", locale);
            } else {
                returnMsg = UtilProperties.getMessage(crmResource, "noteDeleteFailed", locale);
                request.setAttribute("_ERROR_MESSAGE_", returnMsg);
                return "error";
            }

        } catch (Exception e) {
            Debug.logError("Exception in delete note" + e.getMessage(), module);
        }
        request.setAttribute("_EVENT_MESSAGE_", returnMsg);
        return "success";
    }
    
    public static Map < String, Object > updatePersonResponsible(DispatchContext dctx, Map < String, Object > context) {
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String partyId = (String) context.get("partyId");
        String accountPartyId = (String) context.get("accountPartyId");
        String roleTypeIdFrom = (String) context.get("roleTypeIdFrom");
        java.sql.Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
        Map < String, Object > result = ServiceUtil.returnSuccess();
        Locale locale = (Locale) context.get("locale");
        String errMsg = null;
        try {
            String roleTypeIdTo = "";
            String securityGroupId = "";
            // To assaign Role Type to new party
            if (UtilValidate.isNotEmpty(accountPartyId)) {
            	
            	GenericValue partyRoleCondition = delegator.findOne("PartyRole", UtilMisc.toMap("partyId", accountPartyId, "roleTypeId", "ACCOUNT_MANAGER"), false);
            	
                /*EntityConditionList < EntityCondition > roleCondition = EntityCondition.makeCondition(UtilMisc.toList(
                        EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, accountPartyId),
                        EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "ACCOUNT_MANAGER")),
                    EntityOperator.AND);
                List < GenericValue > partyRoleCondition = delegator.findList("PartyRole", roleCondition, null, null, null, false);*/

                if (UtilValidate.isNotEmpty(partyRoleCondition)) {
                    roleTypeIdTo = "ACCOUNT_MANAGER";
                    securityGroupId = "ACCOUNT_OWNER";
                } else {
                    result = ServiceUtil.returnError(UtilProperties.getMessage(crmResource, "partyNotFound", locale));
                    result.put("partyId", partyId);
                    return result;
                }

                if (UtilValidate.isNotEmpty(partyId) && UtilValidate.isNotEmpty(roleTypeIdFrom)) {
                    EntityCondition conditionPR = EntityCondition.makeCondition(UtilMisc.toList(
                    		EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, partyId),
                        EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "ACCOUNT_MANAGER"),
                        EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, roleTypeIdFrom),
                        EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "RESPONSIBLE_FOR"),
                        EntityUtil.getFilterByDateExpr()), EntityOperator.AND);

                    List < GenericValue > responsibleFor = EntityQuery.use(delegator).from("PartyRelationship").where(conditionPR).orderBy("fromDate DESC").queryList();

                    if (responsibleFor != null && responsibleFor.size() > 0) {
                        for (GenericValue partyRelationship: responsibleFor) {
                            partyRelationship.set("thruDate", nowTimestamp);
                            partyRelationship.store();
                        }
                    }
                    GenericValue partyRelationshipcreate = delegator.makeValue("PartyRelationship");
                    partyRelationshipcreate.set("partyIdFrom", partyId);
                    partyRelationshipcreate.set("partyIdTo", accountPartyId);
                    partyRelationshipcreate.set("roleTypeIdFrom", roleTypeIdFrom);
                    partyRelationshipcreate.set("roleTypeIdTo", roleTypeIdTo);
                    partyRelationshipcreate.set("securityGroupId", securityGroupId);
                    partyRelationshipcreate.set("fromDate", nowTimestamp);
                    partyRelationshipcreate.set("partyRelationshipTypeId", "RESPONSIBLE_FOR");
                    partyRelationshipcreate.set("createdByUserLoginId", userLogin.get("userLoginId"));
                    partyRelationshipcreate.create();
                    
                    List < EntityCondition > campaignContactListParty = new ArrayList < EntityCondition > ();
                    campaignContactListParty.add(EntityCondition.makeCondition(EntityOperator.AND,
                        EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId)));
                    campaignContactListParty.add(EntityCondition.makeCondition(EntityOperator.OR,
                        EntityCondition.makeCondition("isApproved", EntityOperator.EQUALS, null),
                        EntityCondition.makeCondition("isApproved", EntityOperator.EQUALS, ""),
                        EntityCondition.makeCondition("isApproved", EntityOperator.EQUALS, "N")));

                    EntityCondition CampListPartycondition = EntityCondition.makeCondition(campaignContactListParty, EntityOperator.AND);
                    List < GenericValue > campaignContactListPartyList = delegator.findList("CampaignContactListParty", CampListPartycondition, null, null, null, false);
                    if (campaignContactListPartyList != null && campaignContactListPartyList.size() > 0) {
                        List < String > contactListId = EntityUtil.getFieldListFromEntityList(campaignContactListPartyList, "contactListId", true);
                        if (contactListId != null && contactListId.size() > 0) {
                            List < GenericValue > mktCampCL = EntityQuery.use(delegator).from("MarketingCampaignContactList")
                                .where(EntityCondition.makeCondition("contactListId", EntityOperator.IN, contactListId)).queryList();
                            if (mktCampCL != null && mktCampCL.size() > 0) {
                                List < GenericValue > mktCamp = EntityQuery.use(delegator).from("MarketingCampaign")
                                    .where(EntityCondition.makeCondition("marketingCampaignId", EntityOperator.IN, EntityUtil.getFieldListFromEntityList(mktCampCL, "marketingCampaignId", true)),
                                        EntityCondition.makeConditionDate("startDate", "endDate")
                                    ).queryList();
                                if (mktCampCL != null && mktCampCL.size() > 0) {
                                    List < GenericValue > mktCampCLNew = EntityQuery.use(delegator).from("MarketingCampaignContactList")
                                        .where(EntityCondition.makeCondition("contactListId", EntityOperator.IN, contactListId),
                                            EntityCondition.makeCondition("marketingCampaignId", EntityOperator.IN, EntityUtil.getFieldListFromEntityList(mktCamp, "marketingCampaignId", true)))
                                        .queryList();
                                    if (mktCampCLNew != null && mktCampCLNew.size() > 0) {
                                        campaignContactListParty.add(EntityCondition.makeCondition(EntityOperator.AND,
                                            EntityCondition.makeCondition("contactListId", EntityOperator.IN, EntityUtil.getFieldListFromEntityList(mktCampCLNew, "contactListId", true))));
                                        EntityCondition CampListPartycondition1 = EntityCondition.makeCondition(campaignContactListParty, EntityOperator.AND);
                                        List < GenericValue > campaignContactListPartyList1 = delegator.findList("CampaignContactListParty", CampListPartycondition1, null, null, null, false);
                                        if (campaignContactListPartyList1 != null && campaignContactListPartyList1.size() > 0) {
                                            for (GenericValue campaignContactListPartyGV: campaignContactListPartyList1) {
                                                campaignContactListPartyGV.set("csrPartyId", accountPartyId);
                                                campaignContactListPartyGV.store();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    
                } else {
                    result = ServiceUtil.returnError(UtilProperties.getMessage(crmResource, "partyNotFound", locale));
                    result.put("partyId", partyId);
                    return result;
                }
            } else {
                result = ServiceUtil.returnError(UtilProperties.getMessage(crmResource, "reassignPartyNotFound", locale));
                result.put("partyId", partyId);
                return result;
            }
        } catch (GenericEntityException e) {
            Debug.logError("Exception in Update Person Responsible For" + e.getMessage(), module);
            result = ServiceUtil.returnError(UtilProperties.getMessage(crmResource, "reassignProcessFailed", locale));
            result.put("partyId", partyId);
            return result;
        }
        result = ServiceUtil.returnSuccess(UtilProperties.getMessage(crmResource, "partySuccessFullyReassign", locale));
        result.put("partyId", partyId);
        return result;
    }
    
    public static Map < String, Object > updatePersonResponsibleFor(DispatchContext dctx, Map < String, Object > context) {
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String partyId = (String) context.get("partyId");
        String accountPartyId = (String) context.get("accountPartyId");
        String roleTypeIdFrom = (String) context.get("roleTypeIdFrom");
        java.sql.Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
        Map < String, Object > result = ServiceUtil.returnSuccess();
        Locale locale = (Locale) context.get("locale");
        String errMsg = null;
        try {
            String roleTypeIdTo = "";
            String securityGroupId = "";
            // To assaign Role Type to new party
            String userLoginId = userLogin.getString("userLoginId");
            if (UtilValidate.isNotEmpty(accountPartyId)) {
            	GenericValue partyRoleCondition = delegator.findOne("PartyRole", UtilMisc.toMap("partyId", accountPartyId, "roleTypeId", "ACCOUNT_MANAGER"), false);
            	if("CUSTOMER".equals(roleTypeIdFrom) || "ACCOUNT".equals(roleTypeIdFrom)){
            		 partyRoleCondition = delegator.findOne("PartyRole", UtilMisc.toMap("partyId", accountPartyId, "roleTypeId", "EMPLOYEE"), false);
            	 }
                /*EntityConditionList < EntityCondition > roleCondition = EntityCondition.makeCondition(UtilMisc.toList(
                        EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, accountPartyId),
                        EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "ACCOUNT_MANAGER")),
                    EntityOperator.AND);
                List < GenericValue > partyRoleCondition = delegator.findList("PartyRole", roleCondition, null, null, null, false);*/

                if (UtilValidate.isNotEmpty(partyRoleCondition)) {
                    roleTypeIdTo = "ACCOUNT_MANAGER";
                    securityGroupId = "ACCOUNT_OWNER";
                    if("CUSTOMER".equals(roleTypeIdFrom)){
                    	roleTypeIdTo="EMPLOYEE";
                    	securityGroupId="CONTACT_OWNER";
                    }
                    else if("ACCOUNT".equals(roleTypeIdFrom)){
                    	roleTypeIdTo="EMPLOYEE";
                    	securityGroupId = "ACCOUNT_OWNER";
                    }
                    
                } else {
                    result = ServiceUtil.returnError(UtilProperties.getMessage(crmResource, "partyNotFound", locale));
                    result.put("partyId", partyId);
                    return result;
                }

                if (UtilValidate.isNotEmpty(partyId) && UtilValidate.isNotEmpty(roleTypeIdFrom)) {
                	// expire old responsible for [start]
                	EntityCondition conditionPR = EntityCondition.makeCondition(UtilMisc.toList(
                    		EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, partyId),
                    		//EntityCondition.makeCondition("partyIdTo", EntityOperator.NOT_EQUAL, accountPartyId),
                    		//EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "ACCOUNT_MANAGER"),
                    		EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, roleTypeIdFrom),
                    		EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "RESPONSIBLE_FOR"),
                    		EntityUtil.getFilterByDateExpr()), EntityOperator.AND);
                	if("CUSTOMER".equals(roleTypeIdFrom) || "ACCOUNT".equals(roleTypeIdFrom) ){
                		conditionPR = EntityCondition.makeCondition(UtilMisc.toList(
                        		EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, partyId),
                        		//EntityCondition.makeCondition("partyIdTo", EntityOperator.NOT_EQUAL, accountPartyId),
                        		//EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "EMPLOYEE"),
                        		EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, roleTypeIdFrom),
                        		EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "RESPONSIBLE_FOR"),
                        		EntityUtil.getFilterByDateExpr()), EntityOperator.AND);
                	}

                    List<GenericValue> oldResponsibleForList = EntityQuery.use(delegator).from("PartyRelationship").where(conditionPR).orderBy("fromDate DESC").queryList();
                    if (UtilValidate.isNotEmpty(oldResponsibleForList)) {
                        for (GenericValue partyRelationship: oldResponsibleForList) {
                            partyRelationship.set("thruDate", nowTimestamp);
                            partyRelationship.store();
                        }
                    }
                    // expire old responsible for [end]
                	
                    conditionPR = EntityCondition.makeCondition(UtilMisc.toList(
                    		EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, partyId),
                    		EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, accountPartyId),
                    		EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, roleTypeIdFrom),
                    		EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "ACCOUNT_MANAGER"),
                    		EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "RESPONSIBLE_FOR"),
                    		EntityUtil.getFilterByDateExpr()
                		), 
                		EntityOperator.AND);
                    if("CUSTOMER".equals(roleTypeIdFrom) ){
                		conditionPR = EntityCondition.makeCondition(UtilMisc.toList(
                        		EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, partyId),
                        		EntityCondition.makeCondition("partyIdTo", EntityOperator.NOT_EQUAL, accountPartyId),
                        		EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "EMPLOYEE"),
                        		EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, roleTypeIdFrom),
                        		EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "RESPONSIBLE_FOR"),
                        		EntityUtil.getFilterByDateExpr()), EntityOperator.AND);
                		roleTypeIdTo="EMPLOYEE";
                	}

                    GenericValue responsibleFor = EntityUtil.getFirst( delegator.findList("PartyRelationship", conditionPR, null, null, null, false) );
                    if (UtilValidate.isEmpty(responsibleFor)) {
                    	GenericValue partyRelationshipcreate = delegator.makeValue("PartyRelationship");
                        partyRelationshipcreate.set("partyIdFrom", partyId);
                        partyRelationshipcreate.set("partyIdTo", accountPartyId);
                        partyRelationshipcreate.set("roleTypeIdFrom", roleTypeIdFrom);
                        partyRelationshipcreate.set("roleTypeIdTo", roleTypeIdTo);
                        partyRelationshipcreate.set("securityGroupId", securityGroupId);
                        partyRelationshipcreate.set("fromDate", nowTimestamp);
                        partyRelationshipcreate.set("partyRelationshipTypeId", "RESPONSIBLE_FOR");
                        partyRelationshipcreate.set("createdByUserLoginId", userLoginId);
                        partyRelationshipcreate.create();
                        
                        // We are adding additional config as a CSR to the customer if phone campaign is enabled.
                        if("Y".equals(org.fio.homeapps.util.DataUtil.isPhoneCampaignEnabled(delegator)) && !roleTypeIdTo.equals("CUST_SERVICE_REP")) {
                        	roleTypeIdTo="CUST_SERVICE_REP";
                        	conditionPR = EntityCondition.makeCondition(UtilMisc.toList(
                            		EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, partyId),
                            		EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, accountPartyId),
                            		EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, roleTypeIdTo),
                            		EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, roleTypeIdFrom),
                            		EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "RESPONSIBLE_FOR"),
                            		EntityUtil.getFilterByDateExpr()), EntityOperator.AND);
                            responsibleFor = EntityUtil.getFirst( delegator.findList("PartyRelationship", conditionPR, null, null, null, false) );
                        	
                            if(UtilValidate.isEmpty(responsibleFor)) {
                            	partyRelationshipcreate = delegator.makeValue("PartyRelationship");
                            	partyRelationshipcreate.set("partyIdFrom", partyId);
                            	partyRelationshipcreate.set("partyIdTo", accountPartyId);
                            	partyRelationshipcreate.set("roleTypeIdFrom", roleTypeIdFrom);
                            	partyRelationshipcreate.set("roleTypeIdTo", roleTypeIdTo);
                            	partyRelationshipcreate.set("securityGroupId", securityGroupId);
                            	partyRelationshipcreate.set("fromDate", nowTimestamp);
                            	partyRelationshipcreate.set("partyRelationshipTypeId", "RESPONSIBLE_FOR");
                            	partyRelationshipcreate.set("createdByUserLoginId", userLoginId);
                            	partyRelationshipcreate.create();
                            }
                    	}
                    }
                    String ownerId = DataUtil.getPartyUserLoginId(delegator, accountPartyId);
                  //update owner roletype/ownerbu/empteam in  party table
                    if(UtilValidate.isNotEmpty(ownerId)) {
                    	try {
							dispatcher.runSync("ap.updateSecurityPartyInfo", UtilMisc.toMap("userLoginId", userLoginId, "ownerId",ownerId, "roleTypeId", "", "partyId", accountPartyId));
						} catch (GenericServiceException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                    }
                } else {
                    result = ServiceUtil.returnError(UtilProperties.getMessage(crmResource, "partyNotFound", locale));
                    result.put("partyId", partyId);
                    return result;
                }
            } else {
                result = ServiceUtil.returnError(UtilProperties.getMessage(crmResource, "reassignPartyNotFound", locale));
                result.put("partyId", partyId);
                return result;
            }
        } catch (GenericEntityException e) {
            Debug.logError("Exception in Update Person Responsible For" + e.getMessage(), module);
            result = ServiceUtil.returnError(UtilProperties.getMessage(crmResource, "reassignProcessFailed", locale));
            result.put("partyId", partyId);
            return result;
        }
        result = ServiceUtil.returnSuccess(UtilProperties.getMessage(crmResource, "partySuccessFullyReassign", locale));
        result.put("partyId", partyId);
        return result;
    }
    
    public static Map < String, Object > updateDefaultContact(DispatchContext dctx, Map < String, Object > context) {
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        String partyId = (String) context.get("partyId");
        String partyIdFrom = (String) context.get("partyIdFrom");
        String roleTypeIdFrom = (String) context.get("roleTypeIdFrom");
        String roleTypeIdTo = (String) context.get("roleTypeIdTo");
        String statusId = (String) context.get("statusId");
        String isMarketable = (String) context.get("isMarketable");
        Map < String, Object > result = ServiceUtil.returnSuccess();
        Locale locale = (Locale) context.get("locale");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String msg = null;
        try {

                if (UtilValidate.isNotEmpty(partyId) && UtilValidate.isNotEmpty(roleTypeIdFrom) && UtilValidate.isNotEmpty(partyIdFrom) && UtilValidate.isNotEmpty(roleTypeIdTo)) {
                    EntityCondition conditionPR = EntityCondition.makeCondition(UtilMisc.toList(
                        EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, partyIdFrom),
                        EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, partyId),
                        EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, roleTypeIdFrom),
                        EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, roleTypeIdTo),
                        EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "CONTACT_REL_INV"),
                        EntityUtil.getFilterByDateExpr()), EntityOperator.AND);

                    List < GenericValue > updateDefaultContactList = EntityQuery.use(delegator).from("PartyRelationship").where(conditionPR).orderBy("fromDate DESC").queryList();

                    if (updateDefaultContactList != null && updateDefaultContactList.size() > 0) {
                        for (GenericValue updateDefaultContactGV : updateDefaultContactList) {
                            updateDefaultContactGV.set("statusId", statusId);
                            
                            if (UtilValidate.isNotEmpty(isMarketable)) {
                            	updateDefaultContactGV.set("isMarketable", isMarketable);
                            }
                            
                            updateDefaultContactGV.store();
                        }
                    }
                    
                    if (UtilValidate.isEmpty(statusId)) {
                    	Map<String, Object> primaryContact = org.groupfio.common.portal.util.DataUtil.getPrimaryContact(delegator, partyId, roleTypeIdTo);
                    	if (UtilValidate.isNotEmpty(primaryContact)) {
                    		partyIdFrom = (String) primaryContact.get("partyIdFrom");
                    		roleTypeIdFrom = (String) primaryContact.get("roleTypeIdFrom");
                    		Map<String, Object> callContext = new LinkedHashMap<String, Object>();
                    		callContext.put("partyId", partyId);
                    		callContext.put("roleTypeIdTo", roleTypeIdTo);
                    		callContext.put("partyIdFrom", partyIdFrom);
                    		callContext.put("roleTypeIdFrom", roleTypeIdFrom);
                    		callContext.put("statusId", "PARTY_DEFAULT");
                    		callContext.put("isMarketable", isMarketable);
                    		callContext.put("userLogin", userLogin);
                    		Map<String, Object> callResult = dispatcher.runSync("crmsfa.updateDefaultContact", callContext);
                    	}
                    }
                    
                } else {
                    //result = ServiceUtil.returnError(UtilProperties.getMessage(crmResource, "defaultContactUpdateFailed", locale));
                	result = ServiceUtil.returnError("Failed to update contact status");
                    result.put("partyId", partyId);
                    return result;
                }
        } catch (Exception e) {
            Debug.logError("Exception in Update Person Contact Status" + e.getMessage(), module);
            result = ServiceUtil.returnError(UtilProperties.getMessage(crmResource, "defaultContactUpdateFailed", locale));
            result.put("partyId", partyId);
            return result;
        }
        result = ServiceUtil.returnSuccess("Contact status successfully updated");
        result.put("partyId", partyId);
        return result;
    }
    public static Map < String, Object > removeContact(DispatchContext dctx, Map < String, Object > context) {
    	Delegator delegator = dctx.getDelegator();
        String partyId = (String) context.get("partyId");
        String partyIdFrom = (String) context.get("partyIdFrom");
        String roleTypeIdFrom = (String) context.get("roleTypeIdFrom");
        String roleTypeIdTo = (String) context.get("roleTypeIdTo");
        String statusId = (String) context.get("statusId");

        Map < String, Object > result = ServiceUtil.returnSuccess();
        Locale locale = (Locale) context.get("locale");
        String msg = null;
        
        
        try {
        	
        	if (UtilValidate.isNotEmpty(partyId) && UtilValidate.isNotEmpty(roleTypeIdFrom) && UtilValidate.isNotEmpty(partyIdFrom) && UtilValidate.isNotEmpty(roleTypeIdTo)) {
                	EntityCondition conditionSO = EntityCondition.makeCondition(UtilMisc.toList(
                    EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId),
                    EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, roleTypeIdTo)), EntityOperator.AND);

                	List < GenericValue > salesOpportunityRoleList = EntityQuery.use(delegator).from("SalesOpportunityAndRole").where(conditionSO).queryList();
                	if(UtilValidate.isNotEmpty(salesOpportunityRoleList)){
                		List < String > salesOpportunityIds = EntityUtil.getFieldListFromEntityList(salesOpportunityRoleList, "salesOpportunityId", true);
                			if(UtilValidate.isNotEmpty(salesOpportunityIds)){
                				 EntityCondition conditionSO1 = EntityCondition.makeCondition(UtilMisc.toList(
                                 EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyIdFrom),
                                 EntityCondition.makeCondition("salesOpportunityId", EntityOperator.IN, salesOpportunityIds),
                                 EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, roleTypeIdFrom),
                                 EntityCondition.makeCondition("opportunityStatusId", EntityOperator.NOT_EQUAL, "OPPO_CLOSED")), EntityOperator.AND);
                            	
                				 List < GenericValue > salesOpportunityContact = EntityQuery.use(delegator).from("SalesOpportunityAndRole").where(conditionSO1).queryList();

                				 if(UtilValidate.isNotEmpty(salesOpportunityContact)){
                            	 result = ServiceUtil.returnError("Can not remove contact is having open opportunity");
                                 result.put("partyId", partyId);
                                 return result;
                				 }
                		   }
                	 }
                	 EntityCondition conditionSO2 = EntityCondition.makeCondition(UtilMisc.toList(
                     EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId),
                     EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, roleTypeIdTo),
                     EntityUtil.getFilterByDateExpr()), EntityOperator.AND);

                     List < GenericValue > WorkEffortPartyAssignmentList = EntityQuery.use(delegator).from("WorkEffortPartyAssignment").where(conditionSO2).queryList();
                     if(UtilValidate.isNotEmpty(WorkEffortPartyAssignmentList)){
                         	 List < String > workEffortIds = EntityUtil.getFieldListFromEntityList(WorkEffortPartyAssignmentList, "workEffortId", true);
                         	 if(UtilValidate.isNotEmpty(workEffortIds)){
                 		 		  EntityCondition conditionSO3 = EntityCondition.makeCondition(UtilMisc.toList(
                                  EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyIdFrom),
                                  EntityCondition.makeCondition("workEffortId", EntityOperator.IN, workEffortIds),
                                  EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, roleTypeIdFrom)), EntityOperator.AND);
                                  List < GenericValue > WorkEffortContactList= EntityQuery.use(delegator).from("WorkEffortContact").where(conditionSO3).queryList();

                              	 List < String > conatctWorkEffortIds = EntityUtil.getFieldListFromEntityList(WorkEffortContactList, "workEffortId", true);
                                      if(UtilValidate.isNotEmpty(conatctWorkEffortIds)){
                                    	  
                                    	  EntityCondition conditionWf = EntityCondition.makeCondition(UtilMisc.toList(
                                          EntityCondition.makeCondition("workEffortId", EntityOperator.IN, conatctWorkEffortIds),
                                          EntityCondition.makeCondition("currentStatusId", EntityOperator.NOT_EQUAL, "IA_MCOMPLETED")), EntityOperator.AND);

                                          List < GenericValue > workEffortList= EntityQuery.use(delegator).from("WorkEffort").where(conditionWf).queryList();

                                          if(UtilValidate.isNotEmpty(workEffortList)){
                                        	  result = ServiceUtil.returnError("Can not remove contact is having open activity");                                              
                                        	  result.put("partyId", partyId);
                                              return result;
                                          }
                                    	  
                                      }
                         	   }
                         }
                        
                            EntityCondition conditionPR = EntityCondition.makeCondition(UtilMisc.toList(
                            EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, partyId),
                            EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, roleTypeIdFrom),
                            EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, roleTypeIdTo),
                            EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "CONTACT_REL_INV"),
                            EntityUtil.getFilterByDateExpr()), EntityOperator.AND);

                            List < GenericValue > taotalContactAssocList = EntityQuery.use(delegator).from("PartyRelationship").where(conditionPR).orderBy("fromDate DESC").queryList();

                            if (taotalContactAssocList != null && taotalContactAssocList.size() > 1) {
                            	
                            	 EntityCondition conditionPRNew = EntityCondition.makeCondition(UtilMisc.toList(
                                 EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, partyIdFrom),
                                 EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, partyId),
                                 EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, roleTypeIdFrom),
                                 EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, roleTypeIdTo),
                                 EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "CONTACT_REL_INV"),
                                 EntityUtil.getFilterByDateExpr()), EntityOperator.AND);
                            	 GenericValue removeContactList  = EntityUtil.getFirst(EntityUtil.filterByCondition(taotalContactAssocList, conditionPRNew));

                            	 	removeContactList.set("thruDate", UtilDateTime.nowTimestamp());
                                    removeContactList.store();
                                    
                                    //disable If userlogin existing to this party
                                    GenericValue disableUserLogin = null;
                            			if (UtilValidate.isNotEmpty(partyIdFrom) ) {
                            				disableUserLogin = 	EntityQuery.use(delegator).from("UserLogin")
                            				.where("partyId", partyIdFrom).queryFirst();
                            				if(UtilValidate.isNotEmpty(disableUserLogin)) {
                            					Timestamp disabledDateTime = UtilDateTime.nowTimestamp();
                            					disableUserLogin.put("enabled", "N");
                            					disableUserLogin.put("disabledDateTime",disabledDateTime );
                            					disableUserLogin.store();
                            				}

                            			}
                                    
                            }else{
                              	  result = ServiceUtil.returnError("Cannot remove; only one contact exists");                                             
                              	  result.put("partyId", partyId);
                                  return result;
                            	  }
                            //set next contact as primary contact as per contact creation date
                            List < GenericValue > setParimaryContactList = null;
                            GenericValue setParimaryContact = null;
                            EntityCondition conditionPR1 = EntityCondition.makeCondition(UtilMisc.toList(
                                    EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, partyId),
                                    EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, roleTypeIdFrom),
                                    EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, roleTypeIdTo),
                                    EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "CONTACT_REL_INV"),
                                    EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null)), EntityOperator.AND);

                                      setParimaryContactList = EntityQuery.use(delegator).from("PartyRelationship").where(conditionPR1).orderBy("fromDate ASC").queryList();
                           
                                  if(UtilValidate.isNotEmpty(setParimaryContactList) && setParimaryContactList.size() >= 1){
                                	  
                                	  if (setParimaryContactList.size() == 1 || statusId != null ){
                                		setParimaryContact = EntityUtil.getFirst(setParimaryContactList);
                                      	setParimaryContact.set("statusId", "PARTY_DEFAULT");
                                      	setParimaryContact.store();                                	  
                                      	}
                                	  	
                                  }
                
                }else {
                	result = ServiceUtil.returnError("Failed to remove contact");
                    result.put("partyId", partyId);
                    return result;
                }
        } catch (GenericEntityException e) {
            Debug.logError("Exception in Update Person Contact Status" + e.getMessage(), module);
            result = ServiceUtil.returnError(UtilProperties.getMessage(crmResource, "Conatc remove failed", locale));
            result.put("partyId", partyId);
            return result;
        }
        result = ServiceUtil.returnSuccess("Contact removed successfully");
        result.put("partyId", partyId);
        return result;
    }
    public static Map < String, Object > rmReassignFromLead(DispatchContext dctx, Map < String, Object > context) {
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        java.sql.Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
        Map < String, Object > result = ServiceUtil.returnSuccess();
        Locale locale = (Locale) context.get("locale");
        try {
            List<String> partyIdList = UtilCommon.getArrayToList((String) context.get("partyList"));
            String reAssignPartyId = (String) context.get("reAssignPartyId");
            if(UtilValidate.isNotEmpty(reAssignPartyId) && partyIdList != null && partyIdList.size() > 0) {
                for(String leadId: partyIdList) { 
                    Boolean validate = true;
                    EntityCondition conditionPR = EntityCondition.makeCondition(UtilMisc.toList(
                        EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, leadId),
                        EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "LEAD"),
                        EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "RESPONSIBLE_FOR"),
                        EntityUtil.getFilterByDateExpr()), EntityOperator.AND);

                    List<GenericValue> oldResponsibleForList = EntityQuery.use(delegator).from("PartyRelationship").where(conditionPR).queryList();
                    if (UtilValidate.isNotEmpty(oldResponsibleForList)) {
                        for (GenericValue partyRelationship: oldResponsibleForList) {
                            if(!reAssignPartyId.equals(partyRelationship.getString("partyIdTo"))) {
                                partyRelationship.set("thruDate", nowTimestamp);
                                partyRelationship.store();
                            
                                List<Map<String, Object>> validationAuditLogList = new ArrayList<Map<String, Object>>();
                                validationAuditLogList.add(WriterUtil.prepareValidationAudit(null, "rmPartyId", partyRelationship.getString("partyIdTo"), reAssignPartyId, userLogin.getString("userLoginId"), ValidationAuditType.VAT_RM_REASSIGN, "Reassign "));
                                String pkCombinedValueText = leadId + "::" + leadId;
                                WriterUtil.writeValidationAudit(delegator, pkCombinedValueText, validationAuditLogList);

                            } else {
                                validate = false;
                            }
                        }
                    }
                    
                    if(validate) {
                        conditionPR = EntityCondition.makeCondition(UtilMisc.toList(
                            EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, leadId),
                            EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, reAssignPartyId),
                            EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "LEAD"),
                            EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "RESPONSIBLE_FOR"),
                            EntityUtil.getFilterByDateExpr()
                        ),EntityOperator.AND);

                        GenericValue responsibleFor = EntityUtil.getFirst( delegator.findList("PartyRelationship", conditionPR, null, null, null, true) );
                        if (responsibleFor == null || responsibleFor.size() < 1) {
                            GenericValue partyRelationshipcreate = delegator.makeValue("PartyRelationship");
                            partyRelationshipcreate.set("partyIdFrom", leadId);
                            partyRelationshipcreate.set("partyIdTo", reAssignPartyId);
                            partyRelationshipcreate.set("roleTypeIdFrom", "LEAD");
                            partyRelationshipcreate.set("roleTypeIdTo", "ACCOUNT_MANAGER");
                            partyRelationshipcreate.set("securityGroupId", "ACCOUNT_OWNER");
                            partyRelationshipcreate.set("fromDate", nowTimestamp);
                            partyRelationshipcreate.set("partyRelationshipTypeId", "RESPONSIBLE_FOR");
                            partyRelationshipcreate.set("createdByUserLoginId", userLogin.get("userLoginId"));
                            partyRelationshipcreate.create();
                        
                        }
                    }
                }
            }
        } catch (GenericEntityException e) {
            Debug.logError("Exception in Update Person Responsible For" + e.getMessage(), module);
            result = ServiceUtil.returnError(UtilProperties.getMessage(crmResource, "reassignProcessFailed", locale));
            return result;
        }
        result = ServiceUtil.returnSuccess(UtilProperties.getMessage(crmResource, "rmSuccessfullyReassign", locale));
        return result;
    }
}
