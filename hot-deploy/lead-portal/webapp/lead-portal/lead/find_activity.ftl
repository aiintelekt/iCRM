<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/activity/modal_window.ftl"/>
 
 <#assign helpUrl = Static["org.groupfio.common.portal.util.DataHelper"].getTabHelpUrl(delegator, request.getRequestURI(), "activities") />  

<#if mainAssocPartyId?has_content>
<#assign partyId= mainAssocPartyId />
<#else>
<#assign partyId= request.getParameter("partyId")! />
</#if>
<div class="pt-2 align-lists">

<form method="post" id="activity-search-form" class="form-horizontal" novalidate="true" data-toggle="validator">	

<input type="hidden" name="domainEntityType" value="${domainEntityType!}">
<input type="hidden" name="domainEntityId" value="${domainEntityId!}">

<input type="hidden" name="partyId" value="${partyId?if_exists}"/>
<#assign partyId= request.getParameter("partyId")! />
<#assign partySummaryDetailsView = (delegator.findOne("PartySummaryDetailsView", {"partyId" : partyId}, false))?if_exists/>
<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/> 

<div class="col-lg-12 col-md-12 col-sm-12 check-list">
	<h2 class="right-icones">Activities</h2>
	
	<span id="create-activity" class="text-dark btn" data-toggle="dropdown" title="Add Activity" aria-expanded="false"> 
		<#if partySummaryDetailsView.statusId?if_exists != "PARTY_DISABLED">
		<i class="fa fa-plus fa-1" aria-hidden="true"></i>   
		</#if>
	</span>
	<div class="dropdown-menu" aria-labelledby="create-activity">
		<h4>Add Activities</h4>
	    <a class="dropdown-item" href="/lead-portal/control/createTaskActivity?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank"><i class="fa fa-clipboard" aria-hidden="true"></i> Task</a> 
	    <a class="dropdown-item" href="/lead-portal/control/createPhoneCallActivity?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank"><i class="fa fa-phone" aria-hidden="true"></i> Phone Call</a>
	    <a class="dropdown-item" href="/lead-portal/control/addEmail?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank"><i class="fa fa-envelope" aria-hidden="true"></i> Email</a>
	    <a class="dropdown-item" href="/lead-portal/control/createAppointmentActivity?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank"><i class="fa fa-calendar" aria-hidden="true"></i> Appointment</a>
	    <#-- <a class="dropdown-item" href="/sales-portal/control/addOthers?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank"><i class="fa fa-plus-square" aria-hidden="true"></i> Others</a>  -->
    </div>
    
 <#--<span id="refresh-activity-btn" class="text-dark btn" title="Refresh Activity"> 
		<i class="fa fa-refresh fa-1" aria-hidden="true"></i>
	</span>  -->
	
	<div class="text-left space-check" style="margin-top: -32px; padding-left: 200px">
		<div class="form-check-inline ml-30">
			<label class="form-check-label"> 
			<input type="checkbox" class="form-check-input activity-status" name="open" value="IA_OPEN">Open
			</label>
		</div>
		<div class="form-check-inline">
			<label class="form-check-label"> 
			<input type="checkbox" class="form-check-input activity-status" name="closed" value="IA_MCOMPLETED">Completed
			</label>
	  </div>
		
		<#-- 
		<div class="arange-flot">
			<ul class="daigram-data mt-0">
				<li><span>25</span> Total</li>
				<li><span>10</span> Open</li>
				<li><a href="#" class="mr-2 text-success"><i
						class="fa fa-smile-o h2 mr-1" aria-hidden="true"></i></a><span>70%</span>
					SLA Met</li>
				<li><a href="#" class="mr-2 text-danger"><i
						class="fa fa-frown-o h3 mr-1" aria-hidden="true"></i></a><span>30%</span>
					SLA Missed</li>
				<li><a href="#" class="mr-2 text-dark"><i
						class="fa fa-refresh fa-1" aria-hidden="true"></i></a>2019/03/26
					11:10:14</li>
			</ul>
		</div>
		-->
		 <span class="float-right">${helpUrl?if_exists}</span>
	</div>
	
</div>

</form>

</div>

<script>

jQuery(document).ready(function() {

$('.activity-status').change(function(){
	getActivityRowData();
});

$('#refresh-activity-btn').on('click', function() {
	getActivityRowData();
});

/*
$('#create-activity-btn').on('click', function() {
	$('#create-activity-modal').modal("show");
});
*/

});

</script>