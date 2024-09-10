<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>

<div class="row" style="width:100%">
	
<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">

<#assign rightContent='
		<button id="custgrpup-refresh-btn" type="button" class="btn btn-xs btn-primary m5"><i class="fa fa-refresh" aria-hidden="true"></i></button>
		<button id="custgrpup-repost-btn" type="button" class="btn btn-xs btn-primary m5" data-toggle="confirmation" title="Are you sure ?"><i class="fa fa-save fa-1" aria-hidden="true"></i> ${uiLabelMap.Repost!}</button>
		' /> 
    
<@AgGrid
	gridheadertitle=""
	gridheaderid="custgrpup-grid-action-container"
	savePrefBtn=true
	clearFilterBtn=true
	exportBtn=true
	insertBtn=false
	updateBtn=false
	removeBtn=false
	headerextra=rightContent
	refreshPrefBtnId="custgrpup-refresh-pref-btn"
	savePrefBtnId="custgrpup-save-pref-btn"
	clearFilterBtnId="custgrpup-clear-filter-btn"
	exportBtnId="custgrpup-export-btn"
    userid="${userLogin.userLoginId}" 
    shownotifications="true" 
    instanceid="CUST_GRP_UPDATE_LIST" 
    autosizeallcol="true"
    debug="false"
    
    serversidepaginate=true
    statusBar=true
    />    
         
<script type="text/javascript" src="/admin-portal-resource/js/ag-grid/indica/find-cust-grp-update.js"></script>

</div>
  	
</div>

<script>

$(document).ready(function() {

});

</script>