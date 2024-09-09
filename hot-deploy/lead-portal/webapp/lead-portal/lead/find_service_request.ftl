<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/service-request/modal_window.ftl"/>

<#assign partyId= request.getParameter("partyId")! />

<div class="page-header border-b pt-2 align-lists">

<form method="post" id="sr-search-form" class="form-horizontal" novalidate="true" data-toggle="validator">	

<input type="hidden" name="partyId" value="${partyId?if_exists}"/>
<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

<div class="col-lg-12 col-md-12 col-sm-12 check-list">
	<h2 class="right-icones">Service Requests</h2>
	<#-- 
	<a id="create-sr-btn" class="mr-2 text-dark right-icones">
		<i class="fa fa-plus fa-1" aria-hidden="true"></i> 
	</a>
	 -->
	<a id="create-sr" href="/lead-portal/control/createServiceRequest?partyId=${partyId?if_exists}&externalLoginKey=${requestAttributes.externalLoginKey!}" class="text-dark" title="Add SR" aria-expanded="false" target="_blank"> 
		<i class="fa fa-plus fa-1" aria-hidden="true"></i>   
	</a>
	<#if domainEntityType == "LEAD" || domainEntityType == "ACCOUNT" || domainEntityType == "CONTACT">
     
    <#--<span id="refresh-sr-btn" class="text-dark btn" title="Refresh SR"> 
		<i class="fa fa-refresh fa-1" aria-hidden="true"></i>
	</span>-->
	<#else>
		<span id="refresh-sr-btn" class="text-dark btn" title="Refresh SR"> 
		<i class="fa fa-refresh fa-1" aria-hidden="true"></i>
	</span>
	</#if>
	
	<div class="text-left space-check" style="margin-top: -32px; padding-left: 250px;">
		<div class="form-check-inline ml-30">
			<label class="form-check-label"> 
			<input type="checkbox" class="form-check-input sr-status" name="open" value="SR_OPEN">Open
			</label>
		</div>
		<div class="form-check-inline">
			<label class="form-check-label"> 
			<input type="checkbox" class="form-check-input sr-status" name="slaAtRisk" value="SLR">SLA at Risk
			</label>
		</div>
		<div class="form-check-inline">
			<label class="form-check-label"> 
			<input type="checkbox" class="form-check-input sr-status" name="slaExpired" value="SLE">Overdue
			</label>
		</div>
		<div class="form-check-inline">
			<label class="form-check-label"> 
			<input type="checkbox" class="form-check-input sr-status" name="closed" value="SR_CLOSED">Completed
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
		
	</div>

</div>

</form>

</div>

<@createServiceRequestModal 
	instanceId="create-sr-modal"
	/>

<script>

jQuery(document).ready(function() {

$('.sr-status').change(function(){
	//alert(this.checked);
	getServiceRequestRowData();
});

$('#refresh-sr-btn').on('click', function() {
	getServiceRequestRowData();
});

$('#create-sr-btn').on('click', function() {
	//$('#create-sr-modal').modal("show");
});

});

</script>