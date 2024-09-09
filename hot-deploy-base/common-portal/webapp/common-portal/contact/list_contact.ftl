<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<div class="row">
	
<div class="col-lg-12 col-md-12 col-sm-12">

<#assign rightContent='<a title="Create" href="/contact-portal/control/createContact?externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank" class="btn btn-primary btn-xs ml-2"> <i class="fa fa-plus" aria-hidden="true"></i> Create </a>' />   
    
<@AgGrid
	gridheadertitle=""
	gridheaderid="contact-grid-action-container"
	savePrefBtn=true
	clearFilterBtn=true
	exportBtn=true
	insertBtn=false
	updateBtn=false
	removeBtn=false
	headerextra=rightContent
	refreshPrefBtnId="contact-refresh-pref-btn"
	savePrefBtnId="contact-save-pref-btn"
	clearFilterBtnId="contact-clear-filter-btn"
	exportBtnId="contact-export-btn"
	
    userid="${userLogin.userLoginId}" 
    shownotifications="true" 
    instanceid="CONTACTS" 
    autosizeallcol="true"
    debug="false"
    />    
   
<script type="text/javascript" src="/common-portal-resource/js/ag-grid/contact/find-contact.js"></script>

</div>
  	
</div>
	
<script>     
$(document).ready(function() {

});

</script>