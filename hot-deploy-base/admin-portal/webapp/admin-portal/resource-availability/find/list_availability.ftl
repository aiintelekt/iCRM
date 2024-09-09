<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>

<div class="row" style="width:100%">
	
<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">

<#assign rightContent='<a title="Create" target="_blank" href="/admin-portal/control/createResAvail?externalLoginKey=${requestAttributes.externalLoginKey!}" class="btn btn-primary btn-xs ml-2"> <i class="fa fa-plus" aria-hidden="true"></i> Create </a>' />   
<#assign rightContent=rightContent + '<span class="btn btn-xs btn-primary" id="resavail-remove-btn" data-toggle="confirmation" title="" data-original-title="Are you sure to REMOVE ?"><i class="fa fa-remove" aria-hidden="true"></i> Remove</span>' />   
    
<#-- <@AgGrid
	gridheadertitle=uiLabelMap.ListOfResAvails
	gridheaderid="res-avail-grid-action-container"
	savePrefBtn=true
	clearFilterBtn=true
	exportBtn=true
	insertBtn=false
	updateBtn=false
	removeBtn=true
	headerextra=rightContent
	
	removeBtnId="resavail-remove-btn"
	
    userid="${userLogin.userLoginId}" 
    shownotifications="true" 
    instanceid="RES_AVAIL_LIST" 
    autosizeallcol="true"
    debug="false"
    />    
         
<script type="text/javascript" src="/admin-portal-resource/js/ag-grid/resource-availability/find-res-avail.js"></script>-->
    <input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
	<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

		<@fioGrid 
			id="resavail-Grid"
			instanceId="RES_AVAIL_LIST"
			jsLoc="/admin-portal-resource/js/ag-grid/resource-availability/find-res-avail.js"
			headerLabel=uiLabelMap.ListOfResAvails!
			headerId="resavail-grid-action-container"
			subFltrClearId="resavail-sub-filter-clear-btn"
			savePrefBtnId="resavail-save-pref-btn"
			clearFilterBtnId="resavail-clear-filter-btn"
			headerBarClass="grid-header-no-bar"
			savePrefBtn=false
			clearFilterBtn=false
			exportBtn=true
			subFltrClearBtn=false
			headerExtra=rightContent!
			exportBtnId="resavail-list-export-btn"
			/>	

</div>
  	
</div>

<script>

$(document).ready(function() {

});

</script>