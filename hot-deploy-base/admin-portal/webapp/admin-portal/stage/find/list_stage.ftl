<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>

<div class="row" style="width:100%">
	
<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">

<#assign rightContent='<a title="Create" href="/admin-portal/control/createOppoStage" class="btn btn-primary btn-xs ml-2" target="_blank"> <i class="fa fa-plus" aria-hidden="true"></i> Create </a>' />   
    
<#-- <@AgGrid
	gridheadertitle=uiLabelMap.ListOfOppoStages
	gridheaderid="oppo-stage-grid-action-container"
	savePrefBtn=true
	clearFilterBtn=true
	exportBtn=true
	insertBtn=false
	updateBtn=false
	removeBtn=false
	headerextra=rightContent
	
    userid="${userLogin.userLoginId}" 
    shownotifications="true" 
    instanceid="OPPO_STAGE_LIST" 
    autosizeallcol="false"
    debug="false"
    />    
         
<script type="text/javascript" src="/admin-portal-resource/js/ag-grid/stage/find-opportunity-stage.js"></script>-->
	<input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
  	<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

		<@fioGrid 
			id="ListOfOppoStages-grid"
			instanceId="OPPO_STAGE_LIST"
			jsLoc="/admin-portal-resource/js/ag-grid/stage/find-opportunity-stage.js"
			headerLabel=uiLabelMap.ListOfOppoStages
			headerId="ListOfOppoStages-list-grid-container"
			headerBarClass="grid-header-no-bar"
			savePrefBtn=true
			clearFilterBtn=true
			exportBtn=true
			subFltrClearBtn = true
			savePrefBtnId ="oppo-stage-save-pref-btn"
			clearFilterBtnId ="oppo-stage-clear-filter-btn"
			subFltrClearId="oppo-stage-sub-filter-clear-btn"
			headerExtra=rightContent!
			exportBtnId="oppo-stage-list-export-btn"
			/>
</div>
  	
</div>
	
<script>     
$(document).ready(function() {

});

</script>