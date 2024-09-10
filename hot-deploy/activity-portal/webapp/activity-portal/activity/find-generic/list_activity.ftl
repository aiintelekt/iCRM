<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<div class="row" style="width:100%" id="listof-lead">
	
<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">

<#assign rightContent='<span id="create-activity" title="Create" class="btn btn-primary btn-xs ml-2" data-toggle="dropdown" aria-expanded="false"> <i class="fa fa-plus" aria-hidden="true"></i> Create </span>
<div class="dropdown-menu" aria-labelledby="create-activity">
<a class="dropdown-item" href="/activity-portal/control/createTask?externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank"><i class="fa fa-clipboard" aria-hidden="true"></i> Task</a>
<a class="dropdown-item" href="/activity-portal/control/createApnt?externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank"><i class="fa fa-calendar" aria-hidden="true"></i> Appointment</a>
</div>' />   
    
<#-- <@AgGrid
	gridheadertitle=uiLabelMap.ListOfActivitys
	gridheaderid="activity-grid-action-container"
	savePrefBtn=true
	clearFilterBtn=true
	exportBtn=true
	insertBtn=false
	updateBtn=false
	removeBtn=false
	
    userid="${userLogin.userLoginId}" 
    shownotifications="true" 
    instanceid="ACTIVITY_GEN_LIST" 
    autosizeallcol="true"
    debug="false"
    
    serversidepaginate=true
    statusBar=true
    />    
         
<script type="text/javascript" src="/activity-portal-resource/js/ag-grid/find-activity-gen.js"></script>-->
<input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

		<@fioGrid 
			id="activityGrid"
			instanceId="ACTIVITY_GEN_LIST"
			jsLoc="/activity-portal-resource/js/ag-grid/find-activity-gen.js"
			headerLabel=uiLabelMap.ListOfActivitys
			headerId="activity-grid-action-container"
			savePrefBtnId="save-pref-btn"
			clearFilterBtnId="clear-filter-btn"
			headerBarClass="grid-header-no-bar"
			subFltrClearBtn = true
			subFltrClearId="activity-sub-filter-clear-btn"
			exportBtnId="activity-gen-export-btn"
			savePrefBtn=true
			clearFilterBtn=true
			exportBtn=true
			serversidepaginate=true
			statusBar=true
			/>
</div>
  	
</div>

<script>

$(document).ready(function() {
function deserialize(serializedJavascript) {
    return eval("(" + serializedJavascript + ")");
}
});

</script>