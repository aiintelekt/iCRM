<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<div class="row" style="width:100%" id="listof-lead">
	
<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">

<#assign rightContent='<a title="Create" href="/contact-portal/control/createContact" class="btn btn-primary btn-xs ml-2"> <i class="fa fa-plus" aria-hidden="true"></i> Create </a>' />   
    
<#-- <@AgGrid
	gridheadertitle=uiLabelMap.ListOfContacts
	gridheaderid="contact-grid-action-container"
	savePrefBtn=true
	clearFilterBtn=true
	exportBtn=true
	insertBtn=false
	updateBtn=false
	removeBtn=false
	headerextra=rightContent!
	
    userid="${userLogin.userLoginId}" 
    shownotifications="true" 
    instanceid="CONTACTS" 
    autosizeallcol="true"
    debug="false"
    
    serversidepaginate=true
	statusBar=true
    />    
   
<script type="text/javascript" src="/contact-portal-resource/js/ag-grid/find-contact.js"></script>    -->
 <input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
 <input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

		<@fioGrid 
			id="contact-grid-header-title"
			instanceId="CONTACTS"
			jsLoc="/contact-portal-resource/js/ag-grid/find-contact.js"
			headerLabel=uiLabelMap.ListOfContacts
			headerId="contact-grid-action-container"
			savePrefBtnId="contact-save-pref-btn"
			clearFilterBtnId="contact-clear-filter-btn"
			headerBarClass="grid-header-no-bar"
			exportBtnId="contact-list-export-btn"
			headerExtra=rightContent!
			savePrefBtn=true
			clearFilterBtn=true
			exportBtn=true
			/>
</div>
  	
</div>
	
<script>     
$(document).ready(function() {

});

</script>