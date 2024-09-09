<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>

<div class="row" style="width:100%">
	
<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">

<#assign rightContent='<a title="Create" href="/admin-portal/control/createTemplateCategory" class="btn btn-primary btn-xs ml-2" target="_blank"> <i class="fa fa-plus" aria-hidden="true"></i> Create </a>' />   
<#--
<@AgGrid
	gridheadertitle="Template Category List"
	gridheaderid="lov-grid-action-container"
	savePrefBtn=true
	clearFilterBtn=true
	exportBtn=true
	insertBtn=false
	updateBtn=false
	removeBtn=false
	headerextra=rightContent
	
    userid="${userLogin.userLoginId}" 
    shownotifications="true" 
    instanceid="TEMPLATE_CATEGORY_LIST" 
    autosizeallcol="true"
    debug="false"
    />    
         
<script type="text/javascript" src="/admin-portal-resource/js/ag-grid/lov/find-template-category.js"></script>
-->
<@fioGrid
	id="template-category"
	instanceId="TEMPLATE_CATEGORY_LIST"
	jsLoc="/admin-portal-resource/js/ag-grid/lov/find-template-category.js"
	headerLabel="Template Category List"
	headerExtra=rightContent!
	headerBarClass="grid-header-no-bar"
	headerId="template-category-tle"
	savePrefBtnId="template-category-save-pref"
	clearFilterBtnId="template-category-clear-pref"
	subFltrClearId="template-category-clear-sub-ftr"
	serversidepaginate=false
	statusBar=false
	exportBtn=true
	savePrefBtn=true
	clearFilterBtn=true
	subFltrClearBtn=true
	exportBtnId="template-category-list-export-btn"
	/>

</div>
  	
</div>
	
<script>     
$(document).ready(function() {

});

</script>