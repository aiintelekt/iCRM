<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#assign readOnlyPermission = Static["org.fio.homeapps.util.DataUtil"].hasPermissionWoFullPerm(request, "READ_ONLY_PERM")?if_exists />
<#assign headerextra = '' />
<#assign removeBtn = true />
<#if readOnlyPermission!>
	<#assign removeBtn = false />
<#else>
	<#assign headerextra = '<a title="Create" target="_blank" href="createTechnicianLocation?externalLoginKey=${requestAttributes.externalLoginKey!}" class="btn btn-primary btn-xs ml-2"> <i class="fa fa-plus" aria-hidden="true"></i> Create </a>' />	
</#if>
<div class="row" style="width:100%">
	
<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">

<#-- <@AgGrid
	gridheadertitle="List Of Locations"
	gridheaderid="technician-grid-action-container"
	savePrefBtn=true
	clearFilterBtn=true
	exportBtn=true
	insertBtn=false
	updateBtn=false
	removeBtn=removeBtn!
	headerextra=headerextra!
    userid="${userLogin.userLoginId}" 
    shownotifications="true" 
    instanceid="LIST_PRODUCT_STORES" 
    autosizeallcol="true"
    debug="false"
    />    
   
   <script type="text/javascript" src="/admin-portal-resource/js/ag-grid/org-Hierarchy/store-technician-location.js"></script> -->
  	<input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
  	<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

		<@fioGrid 
			id="technician-grid"
			instanceId="LIST_PRODUCT_STORES"
			jsLoc="/admin-portal-resource/js/ag-grid/org-Hierarchy/store-technician-location.js"
			headerLabel="List Of Locations"
			headerId="technician-grid-action-container"
			headerBarClass="grid-header-no-bar"
			savePrefBtn=false
			clearFilterBtn=false
			exportBtn=true
			subFltrClearBtn=false
			savePrefBtnId ="save-pref-btn"
			exportBtnId="technician-list-export-btn"
			clearFilterBtnId ="clear-filter-btn"
			helpBtn=helpBtn
			helpUrl=helpUrl!
			headerExtra=headerextra!
			/>
</div>
  	
</div>
	
<script>     
$(document).ready(function() {

});

</script>