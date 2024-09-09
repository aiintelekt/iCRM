<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<div class="row" style="width:100%">
	
<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">

<@AgGrid
	gridheadertitle="List Of Activity Work Type "
	gridheaderid="activityWork-grid-action-container"
	savePrefBtn=true
	clearFilterBtn=true
	exportBtn=true
	insertBtn=false
	updateBtn=false
	removeBtn=true
	
    userid="${userLogin.userLoginId}" 
    shownotifications="true" 
    instanceid="LIST_ACTIVITY_WORK_TYPE" 
    autosizeallcol="true"
    debug="false"
    />    
   
<script type="text/javascript" src="/admin-portal-resource/js/ag-grid/lov/find-Activity-work-type.js"></script>  

</div>
  	
</div>
	
<script>     
$(document).ready(function() {

});

</script>