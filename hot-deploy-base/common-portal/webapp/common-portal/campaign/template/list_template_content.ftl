<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
<div class="row">
<#assign helpUrl = Static["org.groupfio.common.portal.util.DataHelper"].getHelpUrl(delegator, request.getRequestURI(), "template-content") />
<div class="col-lg-12 col-md-12 col-sm-12">
	<#assign rightContent=''/>
	<#if domainEntityType?has_content && domainEntityType=='REBATE'>
	<#assign rightContent= rightContent+'<a href="#" id="templateContent-preview-btn" target="_blank" class="btn btn-xs btn-primary m5"><i class="fa fa-eye" aria-hidden="true"></i> Preview</a>'/>
	</#if>
	
	<#assign rightContent=rightContent+'
		<button id="templateContent-refresh-btn" type="button" class="btn btn-xs btn-primary m5"><i class="fa fa-refresh" aria-hidden="true"></i></button>
		<button id="templateContent-add-btn" type="button" class="btn btn-xs btn-primary m5"><i class="fa fa-plus" aria-hidden="true"></i> Create</button>
		' />
	
	<#assign rightContent= rightContent + '<button id="templateContent-remove-btn" type="button" class="btn btn-xs btn-primary m5" data-toggle="confirmation" title="Are you sure?"><i class="fa fa-minus" aria-hidden="true"></i> Remove</button>'/>
				
	<@AgGrid
	gridheadertitle=""
	gridheaderid="templateContent-grid-action-container"
	savePrefBtn=true
	clearFilterBtn=true
	exportBtn=true
	insertBtn=false
	updateBtn=false
	removeBtn=false
	helpBtn=true
	helpUrl=helpUrl!
	headerextra=rightContent!
	refreshPrefBtnId="templateContent-refresh-pref-btn"
	savePrefBtnId="templateContent-save-pref-btn"
	clearFilterBtnId="templateContent-clear-filter-btn"
	exportBtnId="templateContent-export-btn"
	removeBtnId="templateContent-remove-btn"
    userid="${userLogin.userLoginId}" 
    shownotifications="true" 
    instanceid="TPL_CONT_LIST" 
    autosizeallcol="true"
    debug="false"
    />
  	<script type="text/javascript" src="/common-portal-resource/js/ag-grid/campaign/find-template-content.js"></script>
</div>
</div>

<script>
jQuery(document).ready(function() {

let previewUrl = '#';		
<#if domainEntityType?has_content && domainEntityType=='REBATE'>
<#if inputContext.is2g?has_content && inputContext.is2g=="N">
previewUrl = "/rebate-portal/control/agreementnon.pdf?agreementId=${domainEntityId!}"
<#else>
previewUrl = "/rebate-portal/control/agreement.pdf?agreementId=${domainEntityId!}"
</#if>
</#if>	
	
$('#templateContent-preview-btn').attr('href', previewUrl);

});
</script>