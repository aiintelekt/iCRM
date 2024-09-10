<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>

<div class="row" style="width:100%">
	
<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">

<#assign rightContent='<a title="Create" href="/admin-portal/control/createLov" class="btn btn-primary btn-xs ml-2" target="_blank"> <i class="fa fa-plus" aria-hidden="true"></i> Create </a>' />   
    
<#--
<@AgGrid
	gridheadertitle=uiLabelMap.ListOfLovs
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
    instanceid="LOV_LIST" 
    autosizeallcol="true"
    debug="false"
    />    
         
<script type="text/javascript" src="/admin-portal-resource/js/ag-grid/lov/find-lov.js"></script>
-->
<@fioGrid
	id="lov-list"
	instanceId="LOV_LIST"
	jsLoc="/admin-portal-resource/js/ag-grid/lov/find-lov.js"
	headerLabel=uiLabelMap.ListOfLovs!
	headerExtra=rightContent!
	headerBarClass="grid-header-no-bar"
	headerId="lov-list-tle"
	savePrefBtnId="lov-list-save-pref"
	clearFilterBtnId="lov-list-clear-pref"
	subFltrClearId="lov-list-clear-sub-ftr"
	serversidepaginate=false
	statusBar=false
	savePrefBtn=true
	clearFilterBtn=true
	exportBtn=true
	subFltrClearBtn=true
	exportBtnId="lov-list-export-btn"
	/>

</div>
  	
</div>
	
<script>     
$(document).ready(function() {

});

</script>