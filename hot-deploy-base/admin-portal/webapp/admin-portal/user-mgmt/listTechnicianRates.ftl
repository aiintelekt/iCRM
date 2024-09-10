<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<#assign headerextra = '<a title="Create" target="_blank" href="createTechnicianRate?externalLoginKey=${requestAttributes.externalLoginKey!}" class="btn btn-primary btn-xs ml-2"> <i class="fa fa-plus" aria-hidden="true"></i> Create </a>' />
<#assign headerextra = headerextra + '<span class="btn btn-xs btn-primary" id="remove-btn" data-toggle="confirmation" title="" data-original-title="Are you sure to REMOVE ?"><i class="fa fa-remove" aria-hidden="true"></i> Remove</span>'/>
<div class="row" style="width:100%">
	
<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">

<#-- <@AgGrid
	gridheadertitle="List Of Technician Rates"
	gridheaderid="technician-grid-action-container"
	savePrefBtn=true
	clearFilterBtn=true
	exportBtn=true
	insertBtn=false
	updateBtn=false
	removeBtn=true
	headerextra=headerextra!	
    userid="${userLogin.userLoginId}" 
    shownotifications="true" 
    instanceid="LIST_TECHNICIAN_RATES" 
    autosizeallcol="true"
    debug="false"
    />    
   
<script type="text/javascript" src="/admin-portal-resource/js/ag-grid/user-mgmt/find-technician-rates.js"></script>-->
    <input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
	<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

		<@fioGrid 
			id="technician-Grid"
			instanceId="LIST_TECHNICIAN_RATES"
			jsLoc="/admin-portal-resource/js/ag-grid/user-mgmt/find-technician-rates.js"
			headerLabel="List Of Technician Rates"
			headerId="technician-grid-action-container"
			subFltrClearId="technician-sub-filter-clear-btn"
			savePrefBtnId="technician-save-pref-btn"
			clearFilterBtnId="technician-clear-filter-btn"
			headerBarClass="grid-header-no-bar"
			savePrefBtn=false
			clearFilterBtn=false
			exportBtn=true
			subFltrClearBtn=false
			headerExtra=headerextra!
			exportBtnId="technician-list-export-btn"
			/>	

</div>
  	
</div>
	
<script>     
$(document).ready(function() {

});

</script>