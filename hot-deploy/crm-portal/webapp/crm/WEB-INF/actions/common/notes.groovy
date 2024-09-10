import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.GenericValue;
import javolution.util.FastList;
import org.ofbiz.base.util.UtilMisc;
import java.util.*;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityFindOptions;
import java.text.SimpleDateFormat;
import org.ofbiz.base.util.UtilValidate;
import org.fio.crm.constants.CrmConstants;
import org.ofbiz.base.conversion.JSONConverters.ListToJSON;
import org.ofbiz.base.lang.JSON;

conditionsList = FastList.newInstance();
GenericValue noteDatas = null;
delegator = request.getAttribute("delegator");
partyId = parameters.get("partyId");

noteConditions = [];
partyNoteCond = [];
List partyNotesList = new ArrayList();
if(UtilValidate.isNotEmpty(partyId)) {
context.put("notePartyId", partyId);
accountPartyNote = "N";
partyRoleNote = delegator.findOne("PartyRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", "ACCOUNT"), false);
if( partyRoleNote != null && partyRoleNote.size() > 0) {
    accountPartyNote = "Y";
} 
context.put("accountPartyNote", accountPartyNote);
EntityCondition roleCondition = EntityCondition.makeCondition([EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "CONTACT"),
    EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "ACCOUNT"),
    EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, CrmConstants.PartyRelationshipTypeConstants.CONTACT_REL_INV)
], EntityOperator.AND);

noteConditions.add(roleCondition);

EntityCondition statusCondition = EntityCondition.makeCondition([EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PARTY_DISABLED"),
    EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, null)
], EntityOperator.OR);

noteConditions.add(statusCondition);

noteConditions.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, partyId));


EntityFindOptions efo1 = new EntityFindOptions();
efo1.setDistinct(true);
noteConditions.add(EntityUtil.getFilterByDateExpr());
partyFromRelnListNote = delegator.findList("PartyFromByRelnAndContactInfoAndPartyClassification", EntityCondition.makeCondition(noteConditions, EntityOperator.AND), null, UtilMisc.toList("createdDate"), efo1, false);
if (partyFromRelnListNote != null && partyFromRelnListNote.size() > 0) {
    partyFromRelnNote = EntityUtil.getFieldListFromEntityList(partyFromRelnListNote, "partyIdFrom", true);
    if(partyFromRelnNote != null && partyFromRelnNote.size() > 0) {
        context.put("partyFromRelnNote", partyFromRelnNote);
        partyNoteCond.add(EntityCondition.makeCondition("targetPartyId", EntityOperator.IN, partyFromRelnNote));
    }
}
partyNoteCond.add(EntityCondition.makeCondition("targetPartyId", EntityOperator.EQUALS, partyId));
//results = delegator.findByAnd("PartyNoteView", UtilMisc.toMap("targetPartyId", partyId), UtilMisc.toList("noteDateTime DESC"),true);
List noteList = new ArrayList();
partyNoteViewList = delegator.findList("PartyNoteView", EntityCondition.makeCondition(partyNoteCond, EntityOperator.OR), null, UtilMisc.toList("noteDateTime DESC"), null, false);
for(GenericValue partyNoteView : partyNoteViewList){ 
	Map noteMap = new HashMap();
 noteId = partyNoteView.getString("noteId");
 noteDateTime = partyNoteView.getString("noteDateTime");
 noteInfo = partyNoteView.getString("noteInfo");
 noteParty = partyNoteView.getString("noteParty");
 targetPartyId = partyNoteView.getString("targetPartyId");
 isImportant = partyNoteView.getString("isImportant");
 noteDateTime = partyNoteView.getString("noteDateTime");
 //noteParty = partyNoteView.getString("partyId");
 noteMap.put("noteId", noteId);
 noteMap.put("noteDateTime", noteDateTime);
 noteMap.put("noteInfo", noteInfo);
 noteMap.put("noteParty", noteParty);
 noteMap.put("targetPartyId", targetPartyId);
 noteMap.put("isImportant", isImportant);
 noteMap.put("noteDateTime", noteDateTime);
 /*campaignIdNote = delegator.findByAnd("PartyNote",UtilMisc.toMap("noteId", noteId),null,true);
	 if(campaignIdNote != null){
	 for(GenericValue campaignIdNoteList : campaignIdNote){
		 campId = campaignIdNoteList.get("campaignId");
		noteMap.put("campId", campId);  
		 campaignNoteName = from("MarketingCampaign").where("marketingCampaignId",campId).queryOne();
			 if(campaignNoteName != null){
			 campname = campaignNoteName.get("campaignName");
			 noteMap.put("campname", campname);
			 }
		 }
	}*/
	partyNoteData = from("NoteData").where("noteId",noteId).queryOne();
	callBackDate="";
	mainProdDescription="";
	subProdDescription="";
	if(UtilValidate.isNotEmpty(partyNoteData)){
	  mainProdDesc = from("Enumeration").where("enumId",partyNoteData.get("noteType")).queryOne();
	  subProdDesc = from("Enumeration").where("enumId",partyNoteData.get("subProduct")).queryOne();
	  callBackDate = partyNoteData.getString("callBackDate");
	  if(UtilValidate.isNotEmpty(mainProdDesc)){
	    mainProdDescription = mainProdDesc.get("description");
	  }
	  if(UtilValidate.isNotEmpty(subProdDesc)){
	    subProdDescription = subProdDesc.get("description");
	  }
	}
	 noteMap.put("product", mainProdDescription);
	 noteMap.put("subProduct", subProdDescription);
	 noteMap.put("callBackDate", callBackDate);
	 /*if(accountPartyNote == "Y") {
	  person = from("Person").where("partyId",targetPartyId).queryOne();
	  if(UtilValidate.isNotEmpty(person)){
		  
	  }
	 }*/
	noteList.add(noteMap);
}

context.put("partyNotesList", noteList);
ListToJSON listToJSON = new ListToJSON();
JSON json = listToJSON.convert(noteList);
context.put("partyNotesListStr", json.toString());
}

/*//Campaign Details
List campList = new ArrayList();
getpartyDetails = delegator.findByAnd("ContactListParty",UtilMisc.toMap("partyId",partyId),null,true);
if( getpartyDetails != null && getpartyDetails.size()>0 ){
    for(GenericValue contactLists : getpartyDetails){
        contactListId = contactLists.getString("contactListId");
        if(UtilValidate.isNotEmpty(contactListId)){
            getCampaigns = delegator.findByAnd("MarketingCampaignContactList",UtilMisc.toMap("contactListId",contactListId),null,true);
			 if( getCampaigns !=null && getCampaigns.size()>0 ){
                for(GenericValue gv :getCampaigns){
                    campaignId = gv.getString("marketingCampaignId");
					campaignContactListId = campaignId+"_PROD_LIST";
					conditions = FastList.newInstance();
                    conditions.add (new EntityExpr("marketingCampaignId", EntityOperator.EQUALS,campaignId));
                    orderBy = UtilMisc.toList("startDate");
                    campaignDetails = delegator.findByAnd("MarketingCampaign",UtilMisc.toMap("marketingCampaignId",campaignId),orderBy,true);
					if(UtilValidate.isNotEmpty(campaignDetails)){
                        for(GenericValue campaign : campaignDetails){
                            Map campMap = new HashMap();
                            campaignName = campaign.getString("campaignName");
                            campaignStartDate = campaign.getString("startDate");
                            if(campaignStartDate !=null){
                            Date campaignDate = new SimpleDateFormat("yyyy-MM-dd").parse(campaignStartDate);
                            SimpleDateFormat formatter1 = new SimpleDateFormat("MM/dd/yyyy");
                            campaignStartDate = formatter1.format(campaignDate);
                            }
                            campaignTypeId = campaign.getString("campaignTypeId");
                            capmaignType = from("CampaignType").where("campaignTypeId", campaignTypeId).queryOne();
                            //GenericValue capmaignType = delegator.findByPrimaryKey("CampaignType",UtilMisc.toMap("campaignTypeId",campaignTypeId));
                            if(UtilValidate.isNotEmpty(capmaignType)){
                                campaignTypeDesc = capmaignType.getString("description");
                            }
                            campMap.put("campaignTypeId",campaignTypeDesc);
                            campMap.put("campaignId",campaignId);
                            campMap.put("campaignName",campaignName);
                            campMap.put("startDate",campaignStartDate);
							campaignContactListDetails = delegator.findByAnd("CampaignContactListParty",UtilMisc.toMap("contactListId",campaignContactListId,"partyId",partyId),null,false);
							if(campaignContactListDetails != null){
								for(GenericValue campaignContactListDet : campaignContactListDetails){
								opened = campaignContactListDet.getString("opened");
								notOpen = campaignContactListDet.getString("notOpen");
								bounced = campaignContactListDet.getString("bounced");
								unSubscribe = campaignContactListDet.getString("unsubscribed");
								subscribe = campaignContactListDet.getString("subscribed");
								campMap.put("opened",opened);
								campMap.put("notOpen",notOpen);
								campMap.put("bounced",bounced);
								campMap.put("unSubscribe",unSubscribe);
								campMap.put("subscribe",subscribe);
								campaignClickList = from("MarketingCampaignClickedDetails").where("campaignId",campaignId,"partyId",partyId,"linkTypeId","LINK").queryOne();
									if(campaignClickList != null){
									clickCount = campaignClickList.getAt("count");
									campMap.put("clickCount",clickCount);
									}
								}
							}
                            campList.add(campMap);
                        }
                    }    
                }
            
            }
        }
    }

}
context.put("campList",campList);*/


