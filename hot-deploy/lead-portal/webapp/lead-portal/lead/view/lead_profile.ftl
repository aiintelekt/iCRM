<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/lib/picker_macro.ftl"/>
<#include "component://lead-portal/webapp/lead-portal/lead/modal_window.ftl"/>
<#assign partyId= request.getParameter("partyId")! />
<#assign partySummaryDetailsView = (delegator.findOne("PartySummaryDetailsView", {"partyId" : partyId}, false))?if_exists/>
<#assign helpUrl = Static["org.groupfio.common.portal.util.DataHelper"].getTabHelpUrl(delegator, request.getRequestURI(), "profileDetails") />  

<style>
#dropDowm_row .statusDropDown {
	padding-top: 7px;
	width: 130px;
}
</style>
<div class="pt-2">
	<h2 class="d-inline-block">General Details</h2>
	<ul class="flot-icone">
		<#-- <li class="mt-0"><a href="#" class=" text-dark"><i class="fa fa-refresh fa-1" aria-hidden="true"></i> </a> </li>-->
		<li class="mt-0" style="padding-top: 6px;">
			<span id="" class="labelStatus"> Lead Status</span>
		</li>
		<li class="mt-0">
			<div class="form-group row " id="dropDowm_row">
				<div class="col-sm-12" style="line-height: normal;">
					<#assign leadStatus = Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("enumId","description").from("Enumeration").where("enumTypeId","PARTY_LEAD_STATUS").orderBy("sequenceId").queryList()?if_exists />
					<#if partySummary?if_exists.statusId?if_exists == "LEAD_QUALIFIED" && isLeadStatusEnable != "Y">
					<#assign statusItem = delegator.findOne("StatusItem", Static["org.ofbiz.base.util.UtilMisc"].toMap("statusId", partySummary.statusId?if_exists), true)!/>
					<span>:&nbsp;&nbsp;${statusItem.description!}</span><#else>
					<select id="statusId" name="statusId"  class="ui dropdown search form-control statusDropDown" >
						<#list leadStatus as eachleadStatus>
						<option <#if partySummary?if_exists.statusId?if_exists == "${eachleadStatus.enumId?if_exists}">selected</#if> value="${eachleadStatus.enumId?if_exists}">${eachleadStatus.description?if_exists}</option>
						</#list>
						</#if>
					</select>
				</div>
			</div>
		</li>
		<#--<li class="mt-0">
			<#assign leadStatus = Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("enumId","description").from("Enumeration").where("enumTypeId","PARTY_LEAD_STATUS").orderBy("sequenceId").queryList()?if_exists />
			<span>Lead Status</span>
				<#if partySummaryDetailsView?if_exists.statusId?if_exists == "LEAD_QUALIFIED" && isLeadStatusEnable != "Y">
				<#assign statusItem = delegator.findOne("StatusItem", Static["org.ofbiz.base.util.UtilMisc"].toMap("statusId", partySummaryDetailsView.statusId?if_exists), true)!/>
				<span>:&nbsp;&nbsp;${statusItem.description!}</span>
		 		<#else>
		 			&nbsp;&nbsp;&nbsp;<select id="statusId" name="statusId" class="inputBox">
	    				<#list leadStatus as eachleadStatus>
	        				<option <#if partySummaryDetailsView?if_exists.statusId?if_exists == "${eachleadStatus.enumId?if_exists}">selected</#if> value="${eachleadStatus.enumId?if_exists}">${eachleadStatus.description?if_exists}</option>
	        			 </#list>  
					</select>
		 		</#if>
		</li>-->
		<#-- <li class="mt-0">
		<#if partySummary.lastModifiedDate?has_content> 
            <small>${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(partySummary.lastModifiedDate!, "yyyy-MM-dd")}</small>
        </#if>
         </li> -->
         <#if hasReassignPermission?default(false)>
         <li class="mt-0">
	      	<span data-toggle="modal" data-target="#partyResponsible" title="Reassign" class="btn btn-xs btn-primary"><i class="fa fa-edit" aria-hidden="true"></i> Reassign</span>
         </li>
          <li class="mt-0">
            <a href="<@ofbizUrl>updateLead?partyId=</@ofbizUrl>${inputContext.partyId!}" class="btn btn-xs btn-primary"><i class="fa fa-edit" aria-hidden="true"></i> Update</a>
         </li>
         </#if>
        
         <#if partySummary?if_exists.statusId?if_exists == "LEAD_QUALIFIED">
	         
	         <li class="mt-0">
		      	<span id="convert-lead-btn" class="btn btn-xs btn-primary"><i class="fa fa-cog" aria-hidden="true"></i> Convert</span>
	         </li>
		</#if>
         <#if hasDeactivatePermission?default(false)>
         
         <li class="mt-0">
		      	<span id="deativate-lead-btn" data-toggle="confirmation" title="Are you sure?	Do you want to deactivate" class="btn btn-xs btn-primary"><i class="fa fa-cog" aria-hidden="true"></i> Deactivate</span>
	         </li>
         </#if>
         <li class="mt-0">
		      <span class="mt-0" id="lead_help_btn">${helpUrl?if_exists}</span>
	     </li>
	</ul>
</div>

<form name="deactivateLeadForm" id="deactivateLeadForm" action="deactivateLead" method="post">
	<input type="hidden" name="partyId" value="${parameters.partyId!}">
</form>

<div class="col-md-12 col-lg-12 col-sm-12">
			
	<@dynaScreen 
	instanceId="LEAD_BASE_INFO"
	modeOfAction="VIEW"
	/>
	
</div>

<@reassignPicker 
    instanceId="partyResponsible"
    />
	
<@leadConvertModal 
	instanceId="leadConvert"
	/>	

<script>     
$(document).ready(function() {

$('#convert-lead-btn').on('click', function() {
	$('#leadConvert').modal('show');
});

$('#deativate-lead-btn').on('click', function() {
	//alert("deactivate");
	$('#deactivateLeadForm').submit();
});

$('#statusId').on('change', function() {
	var partyId = $("#partyId").val();
	var statusId = $("#statusId").val();
   	$.ajax({
	        type: "POST",
			url: "leadStatusUpdate",
	        async: true,
	        data: { "partyId": partyId ,"statusId": statusId},
	        success: function(data) {
				if(data.responseMessage == "success"){
					$.notify({
						message : '<p>Lead Status changed Successfully</p>'
					});
					setTimeout(location.reload.bind(location),1000);
				}else{
					if(data && data.responseMessage == "error" && statusId != null ){
						event.preventDefault();
						showAlert("error","Error occurred while updating lead status");
					}
				}
			}
	});

});	
	
});
</script>