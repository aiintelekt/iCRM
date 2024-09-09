<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>

<div class="row" style="width:100%">
	
<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">

<#assign rightContent='<a title="Create" href="/admin-portal/control/createOtherLov" class="btn btn-primary btn-xs ml-2" target="_blank"> <i class="fa fa-plus" aria-hidden="true"></i> Create </a>' />   
    
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
    instanceid="OTHER_LOV_LIST" 
    autosizeallcol="true"
    debug="false"
    />    
         
<script type="text/javascript" src="/admin-portal-resource/js/ag-grid/lov/find-other-lov.js"></script>
-->
<@fioGrid
	id="other-lov-list"
	instanceId="OTHER_LOV_LIST"
	jsLoc="/admin-portal-resource/js/ag-grid/lov/find-other-lov.js"
	headerLabel=uiLabelMap.ListOfLovs!
	headerExtra=rightContent!
	headerBarClass="grid-header-no-bar"
	headerId="other-lov-list-tle"
	savePrefBtnId="other-lov-list-save-pref"
	clearFilterBtnId="other-lov-list-clear-pref"
	subFltrClearId="other-lov-list-clear-sub-ftr"
	serversidepaginate=false
	statusBar=false
	savePrefBtn=true
	clearFilterBtn=true
	exportBtn=true
	subFltrClearBtn=true
	exportBtnId="other-lov-list-export-btn"
	/>

</div>
  	
</div>
	
<script>     
$(document).ready(function() {

});

</script>