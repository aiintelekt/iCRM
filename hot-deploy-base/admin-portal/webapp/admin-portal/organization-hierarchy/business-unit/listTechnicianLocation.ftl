<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<div class="row" style="width:100%">
	
<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">

<@AgGrid
	gridheadertitle="List Of Stores"
	gridheaderid="technician-grid-action-container"
	savePrefBtn=true
	clearFilterBtn=true
	exportBtn=true
	insertBtn=false
	updateBtn=false
	removeBtn=true
	
    userid="${userLogin.userLoginId}" 
    shownotifications="true" 
    instanceid="LIST_PRODUCT_STORES" 
    autosizeallcol="true"
    debug="false"
    />    
   
   <script type="text/javascript" src="/admin-portal-resource/js/ag-grid/org-Hierarchy/store-technician-location.js"></script> 

</div>
  	
</div>
	
<script>     
$(document).ready(function() {

});

</script>