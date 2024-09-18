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
    EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "CONTACT_REL_INV")
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
