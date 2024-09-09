<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/note/note_info_modal.ftl" />
<#assign partyId= request.getParameter("partyId")! />
<#assign campaignListId= request.getParameter("campaignListId")! />
<#assign srStatusId= context.get("currentSrStatusId")?if_exists />
<#assign contextPath = request.getContextPath()/>
<div class="row">
<#assign readOnlyPermission = Static["org.fio.homeapps.util.DataUtil"].hasPermissionWoFullPerm(request, "READ_ONLY_PERM")?if_exists />

<script>
	<#if readOnlyPermission!>
		$(document).ready(function(){
	        $('a.view-link').each(function(){ 
	            $(this).attr("href", "#"); // Set herf value
	            $(this).attr("target","");
	        });
	        
	        $("a.view-link").click(function () {
	            $("#accessDenied").modal("show");
	            return false;
	        });
	    });
    </#if>
</script>

<#assign helpUrl = Static["org.groupfio.common.portal.util.DataHelper"].getHelpUrl(delegator, request.getRequestURI(), "notes") />
<div class="col-lg-12 col-md-12 col-sm-12">
		
	<#assign rightContent="" />
	<#assign rightContent = rightContent+ '<button title="Refresh" id="refresh-notes-btn" class="btn btn-primary btn-xs ml-2"> <i class="fa fa-refresh" aria-hidden="true"></i>  </button>' />	
	<#if readOnlyPermission!>
	<#else>
		<#assign rightContent = rightContent+ ' <input type="button" value="View All Notes" class="btn btn-xs btn-primary" id="get-all-notes-btn" />' /> 
	    <#assign isEnableNote="Y"/>
	    <#if isDisableActivity?has_content>
	    	<#assign isEnableNote="N"/>
	    </#if>
	    <#if inputContext.currentStatusId?has_content && inputContext.currentStatusId=="IA_MCOMPLETED">
	    	<#assign isEnableNote="N"/>
	    </#if>
	    <#assign hasPermission = Static["org.fio.homeapps.util.DataUtil"].hasPermission(request, "CREATE_OPERATION")?if_exists />
	    <#assign requestURI = request.getRequestURI() />
	    <#if hasPermission>
	        <#if partyStatusId?if_exists != "PARTY_DISABLED">
	            <#assign rightContent = rightContent+ '<span id="create-note-btn" title="Note" class="btn btn-primary btn-xs ml-2 " ><i class="fa fa-edit" aria-hidden="true"></i> Create Note </span>' />
	        </#if>
	   	<#elseif requestURI.contains("/client-portal")>
	   		<#assign rightContent = rightContent+ '<span id="create-note-btn" title="Note" class="btn btn-primary btn-xs ml-2 " ><i class="fa fa-edit" aria-hidden="true"></i> Create Note </span>' />
	    </#if>	
	</#if>
	<#assign exportBtn=true>
	<#assign helpBtn=true>
	<#assign gridheadertitle="Notes">
	<#if Static["org.fio.homeapps.util.DataUtil"].isPhoneCampaignEnabled(delegator)?if_exists=="Y">
	<#assign exportBtn=false>
	<#assign helpBtn=false>
	<#assign gridheadertitle="">
	<style>
	.clearfix {
	    margin-top: -10px;
	}
	</style>
	</#if>
	<#-- 
	<@AgGrid
	gridheadertitle=gridheadertitle!
	gridheaderid="notes-grid-action-container"
	savePrefBtn=true
	clearFilterBtn=true
	exportBtn=exportBtn
	insertBtn=false
	updateBtn=false
	removeBtn=false
	helpBtn=helpBtn
	helpUrl=helpUrl!
	headerextra=rightContent!
	refreshPrefBtnId="notes-refresh-pref-btn"
	savePrefBtnId="notes-save-pref-btn"
	subFltrClearId="note-sub-filter-clear-btn"
	clearFilterBtnId="notes-clear-filter-btn"
	exportBtnId="notes-export-btn"
	removeBtnId="notes-remove-btn"
    userid="${userLogin.userLoginId}" 
    shownotifications="true" 
    instanceid="NOTES" 
    autosizeallcol="true"
    debug="false"
    />    
  	<script type="text/javascript" src="/common-portal-resource/js/ag-grid/note/find-note.js"></script>
  	-->
  	<@fioGrid
		instanceId="NOTES"
		jsLoc="/common-portal-resource/js/ag-grid/note/find-note.js"
		headerLabel=gridheadertitle
		headerId="note_tle"
		headerExtra=rightContent!
		headerBarClass="grid-header-no-bar"
		headerExtraLeft = extraLeft!
		savePrefBtnId="notes-save-pref"
		clearFilterBtnId="notes-clear-pref"
		subFltrClearId="notes-clear-sub-ftr"
		exportBtn=exportBtn
		exportBtnId="notes-export-btn"
		serversidepaginate=false
		statusBar=false
		helpBtn=helpBtn
		helpUrl=helpUrl!
		savePrefBtn=true
		clearFilterBtn=true
		subFltrClearBtn=true
		/>
	<form method="get" action="/common-portal/control/getNotesList" id="ViewNotesForm" name="ViewNotesForm" target="_blank" novalidate="true" data-toggle="validator">
        <@inputHidden id="noteIdsList" name="noteIdsList" value="" />
        <input type="hidden" name="domainEntityType" value="${domainEntityType!}">
        <input type="hidden" name="domainEntityId" value="${domainEntityId!}">
        <input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
        <input type="hidden" name="requestURI" id="requestURI" value="${request.getRequestURI()!}"/>
        <input type="hidden" name="campaignListId" id="campaignListId" value="${campaignListId!}"/>
    </form>
</div>
  	
</div>
<span id="remove-note-btn"></span>
<span id="note-search-btn"></span>

<script>
<#if isDisableActivity?has_content && isDisableActivity == "Yes">
function editNote(noteId) {
	showAlert("error", "Cant be edit!");
}
</#if>
</script>