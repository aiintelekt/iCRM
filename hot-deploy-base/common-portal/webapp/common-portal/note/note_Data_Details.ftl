<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#assign domainEntityType= request.getParameter("domainEntityType")! />
<#assign domainEntityId= request.getParameter("domainEntityId")! />
<#assign partyId= request.getParameter("partyId")! />

<style>
a {	
    cursor: pointer;
}
</style>
<#assign isPhoneCampaignEnabled= Static["org.fio.homeapps.util.DataUtil"].isPhoneCampaignEnabled(delegator)!>

<div id="main" role="main">
   <div class="row" id="note-info"  style="width:100%">
      <div class="col-md-12 col-lg-12 col-sm-12 dash-panel">
         <#if componentUri?has_content && domainEntityType == "SERVICE_REQUEST">
         <#assign extra='<a href="${componentUri!}/viewServiceRequest?srNumber=${domainEntityId!}#sr-notes" class="btn btn-xs btn-primary">
         <i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>' />
         <@sectionFrameHeader title="Notes : Information" extra=extra />
         <#elseif componentUri?has_content && domainEntityType == "ACTIVITY">
         <#assign extra='<a href="${componentUri!}/viewActivity?workEffortId=${partyId!}#sr-notes" class="btn btn-xs btn-primary">
         <i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>' />
         <@sectionFrameHeader title="Notes : Information" extra=extra />
         <#elseif domainEntityType == "LEAD">
         <#assign extra='<a href="/lead-portal/control/viewLead?partyId=${partyId!}#lead-notes" class="btn btn-xs btn-primary">
         <i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>' />
         <@sectionFrameHeader title="Notes : Information" extra=extra />
         <#elseif domainEntityType == "ACCOUNT">
         <#assign extra='<a href="/account-portal/control/viewAccount?partyId=${partyId!}#a-notes" class="btn btn-xs btn-primary">
         <i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>' />
         <@sectionFrameHeader title="Notes : Information" extra=extra />
         <#elseif domainEntityType == "CONTACT">
         <#assign extra='<a href="/contact-portal/control/viewContact?partyId=${partyId!}#contact-notes" class="btn btn-xs btn-primary">
         <i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>' />
         <@sectionFrameHeader title="Notes : Information" extra=extra />
         <#elseif domainEntityType == "OPPORTUNITY">
         <#assign extra='<a href="/opportunity-portal/control/viewOpportunity?salesOpportunityId=${partyId!}#opportunity-notes" class="btn btn-xs btn-primary">
         <i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>' />
         <@sectionFrameHeader title="Notes : Information" extra=extra />
         <#elseif "${domainEntityType}" == "SERVICE_REQUEST">
         <#assign extra='<a href="/sr-portal/control/viewServiceRequest?srNumber=${partyId!}#sr-notes" class="btn btn-xs btn-primary">
         <i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>' />
         <@sectionFrameHeader title="Notes : Information" extra=extra />
         <#elseif domainEntityType == "CUSTOMER">
         <#assign extra='<a href="/customer-portal/control/viewCustomer?partyId=${partyId!}#c-notes" class="btn btn-xs btn-primary">
         <i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>' />
         <#assign isShowHelpUrl="Y" />
         <#if isPhoneCampaignEnabled?has_content && isPhoneCampaignEnabled?if_exists=="Y">
             <#assign extra='' />
             <#assign isShowHelpUrl="N" />
         </#if>
         <@sectionFrameHeader title="Notes : Information" extra=extra isShowHelpUrl=isShowHelpUrl!/>
         </#if>
         <#if componentUri?has_content>
         <#assign linkedFrom = "${componentUri}/viewServiceRequest?srNumber=${domainEntityId!}#sr-notes" />
         <#elseif domainEntityType?has_content && domainEntityType!="null">
         <#assign sourceComponent = Static["org.groupfio.common.portal.util.DataHelper"].convertToLabel(domainEntityType) />
         <#assign linkedFrom = Static["org.groupfio.common.portal.util.DataHelper"].prepareLinkedFrom(partyId, domainEntityType, requestAttributes.externalLoginKey!)?if_exists />
         </#if>              
         <@headerH2
         title="${requestParameters.noteId?if_exists} [<span id='note-title'></span>]"
         />
         <div class="pt-2">
            <@headerH1
            title="Notes"
            />
         </div>
         <div class="row">
            <div class="col-md-12 col-lg-6 col-sm-12">
               <@displayCell
               label="Note ID"
               value="${noteId!}"
               id="noteId"
               />
               <#if isPhoneCampaignEnabled?has_content && isPhoneCampaignEnabled?if_exists !="Y">
               <@displayCell
               label="Note Title"
               value="${noteName!}"
               id="noteName"
               />
               </#if>
               <@displayCell
               label="Note Description"
               value="${noteInfo!}"
               id="noteInfo"
               />
               <@displayCell
               label="Created By"
               value="${createdBy!}"
               id="createdBy"
               />
               <#assign date = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(createdStamp, "dd/MM/yyyy")/>
               <@displayCell
               label="Created On"
               value="${date!}"
               id="createdOn"
               />
               <#--<@displayCell
               label="File Source"
               value="${moreInfoItemId!}"
               id="test4"
               />-->
               <div class="offset-sm-0 col-sm-4">
               </div>
            </div>
            <div class="col-md-12 col-lg-6 col-sm-12">
               <#--<@displayCell
               label="File Name"
               value="${moreInfoItemName!}"
               id="test5"
               />-->
               <@displayCell 
               id="sourceId"
               label="Source ID"
               />	  
               <@displayCell
               label="Source Component"
               id="sourceComponent"
               /> 
               <#if domainEntityType?has_content && domainEntityType!="null" && "ACTIVITY" == domainEntityType>
               <@displayCell
               label="Linked From"
               value="${linkedFrom!}"
               isLink="Y"
               desValue="${partyId!}"
               linkValue="${linkedFrom!}"
               /> 
               </#if>
            </div>
         </div>
      </div>
   </div>
</div>

<script >						
var noteId="";
$(function() {
const url=window.location.search;
			const urlParam=new URLSearchParams(url);
			const noteId=urlParam.get("noteId");
			getNoteData(noteId);
});
function getNoteData(noteId) {
    var result = null;
    
    $.ajax({
        type: "POST",
        url: "/common-portal/control/getNoteData",
        async: true,
         data:  {"noteId": noteId, "domainEntityType": "${domainEntityType!}", "domainEntityId": "${domainEntityId!}", "externalLoginKey":"${requestAttributes.externalLoginKey!}"},
        success: function(result) {
            data=result.data;
             var noteId=data.noteId;
    		var noteName=data.noteName;
    		var noteInfo=data.noteInfo;
    		var moreInfoItemId=data.moreInfoItemId;
   			var moreInfoItemName=data.moreInfoItemName;
   			var createdStamp=data.createdStamp;
    		var noteParty=data.noteParty;
    		var createdBy = data.createdBy;
    		var notePartyName=data.notePartyName;
    		var createdByName = data.createdByName;
    		var moreInfoUrl = data.moreInfoUrl;
    		var sourceIdLink = data.domainEntityLink;
    		var domainEntityId = data.domainEntityId;
    		
            $('#noteId').html(noteId);
            $('#noteName').html(noteName);
            $('#noteInfo').html(noteInfo);
            $('#createdBy').html(createdByName);
            $('#createdOn').html(createdStamp);
			            
            $("#sourceId").html("<a href='"+sourceIdLink+"' target='_blank'>"+domainEntityId+"</a>");
            $("#sourceComponent").html(data.domainEntityTypeDesc);
            $("#note-title").html(noteName);
        },error: function(data) {
        	result=data;
			console.log('Error occured');
			showAlert("error", "Error occured while fetching Tiles Data!");
		}
    });
}
</script>