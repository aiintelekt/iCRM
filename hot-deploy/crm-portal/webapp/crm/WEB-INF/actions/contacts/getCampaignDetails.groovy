import java.util.Arrays;
import java.util.List;

import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.util.EntityUtil;
import javolution.util.FastList;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.condition.EntityExpr;
import java.text.SimpleDateFormat;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityQuery;

//import org.agnitas.opentapws.OpenemmClientRMS;

partyId = parameters.get("partyId");
println("``````````````````````````````````````"+partyId);
if(UtilValidate.isEmpty(partyId)){
	partyId = request.getParameter("partyId");
}
context.partyId = partyId;
campaigns = delegator.findAll("MarketingCampaign",true);
campaigns = EntityUtil.filterByDate(campaigns);
context.campaigns = campaigns;

//get Party Attribute Values
Object [] arr = null;
List campList = new ArrayList();
int campId, openEmmCampId,compId=0;
String contactListId = ""; String campaignId = ""; String campaignName=""; String openEmmCampaignId="";
String campaignStartDate="";
String campaignTypeId ="";
String campaignTypeDesc = "";
//OpenemmClientRMS opc = new OpenemmClientRMS();
   

List<GenericValue> getpartyDetails = delegator.findByAnd("ContactListParty",UtilMisc.toMap("partyId","DemoCustomer"),null,false);
if(getpartyDetails.size()>0){
    for(GenericValue contactLists : getpartyDetails){
        contactListId = contactLists.getString("contactListId");		
        if(UtilValidate.isNotEmpty(contactListId)){
            getCampaigns = delegator.findByAnd("MarketingCampaignContactList",UtilMisc.toMap("contactListId",contactListId,"contactPurposeType","LIVE"),null,false);
            if(getCampaigns.size()>0){
                for(GenericValue gv :getCampaigns){
                    campaignId = gv.getString("marketingCampaignId");
                   /* String ClientID =null;
                    opentapsClient = delegator.findByAnd("PartyAttribute", UtilMisc.toMap("partyId", "Company","attrName","alternateOpenEmmClientId"));
                    if(UtilValidate.isNotEmpty(opentapsClient)){
                        ClientID = opentapsClient.getString("attrValue");
                    }
                    context.ClientID = ClientID;
                    if(UtilValidate.isNotEmpty(ClientID)){
                        compId = Integer.parseInt(ClientID);
                    }*/
                    campId = Integer.parseInt(campaignId);
                    conditions = FastList.newInstance();
                    //conditions.add (new EntityExpr("marketingCampaignId", EntityOperator.EQUALS,campaignId));
                    //orderBy = UtilMisc.toList("startDate");
					campaignDetails = delegator.findByAnd("MarketingCampaign", UtilMisc.toMap("marketingCampaignId", campaignId),UtilMisc.toList("startDate"),false	);
                    if(UtilValidate.isNotEmpty(campaignDetails)){
                        for(GenericValue campaign : campaignDetails){
                            openEmmCampaignId = campaign.getString("openEmmCampaignId");
                            /*if(UtilValidate.isEmpty(openEmmCampaignId)){
                                continue;
                            }*/
                            Map campMap = new HashMap();
                            campaignName = campaign.getString("campaignName");
                            campaignStartDate = campaign.getString("startDate");
							if(UtilValidate.isNotEmpty(campaignStartDate)){
                            Date campaignDate = new SimpleDateFormat("yyyy-MM-dd").parse(campaignStartDate);
                            SimpleDateFormat formatter1 = new SimpleDateFormat("MM/dd/yyyy");
                            campaignStartDate = formatter1.format(campaignDate);
							}
                            campaignTypeId = campaign.getString("campaignTypeId");
                            capmaignType = delegator.findByAnd("CampaignType",UtilMisc.toMap("campaignTypeId",campaignTypeId),null,false);
                            if(UtilValidate.isNotEmpty(capmaignType)){
                                campaignTypeDesc = capmaignType.getAt("description");
                            }
                            campMap.put("campaignTypeId",campaignTypeDesc);
                            campMap.put("campaignId",campaignId);
                            campMap.put("campaignName",campaignName);
                            campMap.put("startDate",campaignStartDate);
                            /*if(UtilValidate.isNotEmpty(openEmmCampaignId)){
                                openEmmCampId=Integer.parseInt(openEmmCampaignId);
                                arr1 = opc.getemmopencount(compId, openEmmCampId);
                                if(UtilValidate.isNotEmpty(arr1)){
                                    Object[] objarr1=(Object[]) arr1[0];
                                    String[] stringArray2 = Arrays.copyOf(objarr1, objarr1.length, String[].class);
                                    String opened=stringArray2[0];
                                    String clicked=stringArray2[1];String bounced=stringArray2[2];
                                    String unsubscribed=stringArray2[3];
                                    if(UtilValidate.isNotEmpty(campaignTypeId)){
                                        if(campaignTypeId !="PHONE_CALL"){
                                           if(UtilValidate.isNotEmpty(opened)){
                                                int ope = Integer.parseInt(opened);
                                                campMap.put("opened",ope>0?"Y":"N");
                                            }
                                            if(UtilValidate.isNotEmpty(clicked)){
                                                int click = Integer.parseInt(clicked);
                                                campMap.put("clicked",click>0?"Y":"N");
                                            }
                                            if(UtilValidate.isNotEmpty(bounced)){
                                                int bounce = Integer.parseInt(bounced);
                                                campMap.put("bounced",bounce>0?"Y":"N");
                                            }
                                            if(UtilValidate.isNotEmpty(unsubscribed)){
                                                int unsubscribe = Integer.parseInt(unsubscribed);
                                                campMap.put("unsubscribed",unsubscribe>0?"Y":"N");
                                            }
                                        }
                                    }
                                }
                            }*/
                            campList.add(campMap);
                        }
                    }
                }
            }
        }
    }
}
context.put("campList",campList);
println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"+campList);