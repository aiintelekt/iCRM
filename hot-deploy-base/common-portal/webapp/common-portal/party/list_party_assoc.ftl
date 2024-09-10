<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
<div class="row">
<#assign helpUrl = Static["org.groupfio.common.portal.util.DataHelper"].getHelpUrl(delegator, request.getRequestURI(), "party-assoc") />
<div class="col-lg-12 col-md-12 col-sm-12">
		
	<#assign hasPermission = Static["org.fio.homeapps.util.DataUtil"].hasPermission(request, "CREATE_OPERATION")?if_exists />
	<#if hasPermission>
		<#assign rightContent='
			<button id="refresh-partyassoc-btn" type="button" class="btn btn-xs btn-primary m5"><i class="fa fa-refresh" aria-hidden="true"></i></button>
			<button type="button" class="btn btn-xs btn-primary m5" data-toggle="modal" data-target="#create-partyassoc-modal"><i class="fa fa-plus fa-1" aria-hidden="true"></i> ${uiLabelMap.Add!}</button>
			' />
	</#if>
			
	<@AgGrid
	gridheadertitle="Associated Parties"
	gridheaderid="partyassoc-grid-action-container"
	savePrefBtn=true
	clearFilterBtn=true
	exportBtn=true
	insertBtn=false
	updateBtn=false
	removeBtn=true
	helpBtn=true
	helpUrl=helpUrl!
	headerextra=rightContent!
	refreshPrefBtnId="partyassoc-refresh-pref-btn"
	savePrefBtnId="partyassoc-save-pref-btn"
	clearFilterBtnId="partyassoc-clear-filter-btn"
	exportBtnId="partyassoc-export-btn"
	removeBtnId="partyassoc-remove-btn"
    userid="${userLogin.userLoginId}" 
    shownotifications="true" 
    instanceid="ASSOC_PARTY_LIST" 
    autosizeallcol="true"
    debug="false"
    />    
  	<script type="text/javascript" src="/common-portal-resource/js/ag-grid/party/find-party-assoc.js"></script>
  	
</div>
  	
</div>

<script>

</script>