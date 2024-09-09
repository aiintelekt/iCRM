<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/attachment/model_attachment_window.ftl"/>
<#assign partyId= request.getParameter("partyId")! />
<#assign salesOppId= request.getParameter("salesOpportunityId")! />
<#if salesOppId?has_content>
<#assign salesOppRole=EntityQuery.use(delegator).from("SalesOpportunityRole").where("salesOpportunityId", salesOppId).queryFirst()! />
	<#if salesOppRole?has_content>
	<#assign partyId=salesOppRole.get("partyId")/>
	</#if>
</#if>
<#assign helpBtn=true>
<#assign exportBtn=true>
<#if Static["org.fio.homeapps.util.DataUtil"].isPhoneCampaignEnabled(delegator)?if_exists=="Y">
<#assign exportBtn=false>
<#assign helpBtn=false>
</#if>	
<#assign helpUrl = Static["org.groupfio.common.portal.util.DataHelper"].getHelpUrl(delegator, request.getRequestURI(), "bookmarksAndFiles") />

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
<#if readOnlyPermission!>
	<#assign rightContent =""/>
	<#assign rightContent = rightContent+ '<button title="Refresh" id="refresh-notes-btn" class="btn btn-primary btn-xs ml-2"> <i class="fa fa-refresh" aria-hidden="true"></i>  </button>' />
	<#assign removeBtn = false/>

<#else>
	<#assign srStatusId= context.get("currentSrStatusId")?if_exists />
	<#assign clientPortal= context.get("clientPortal")?if_exists />
	<#assign removeBtn = true/>
	<#assign isEnableNote="Y"/>
	<#if isDisableActivity?has_content>
		<#assign isEnableNote="N"/>
	</#if>
	<#assign rightContent =""/>
	<#assign rightContent = rightContent+ '<button title="Refresh" id="refresh-attachment-btn" class="btn btn-primary btn-xs ml-2"> <i class="fa fa-refresh" aria-hidden="true"></i>  </button>' />
	<#assign rightContent = rightContent+ '<span class="btn btn-xs btn-primary" id="remove-attachment-btn" data-toggle="confirmation" title="" data-original-title="Are you sure to REMOVE ?"><i class="fa fa-remove" aria-hidden="true"></i> Remove</span>'/>
	<#assign hasPermission = Static["org.fio.homeapps.util.DataUtil"].hasPermission(request, "CREATE_OPERATION")?if_exists />
	<#if hasPermission || (clientPortal?has_content && "clientPortal" == clientPortal)>
		<#if partyStatusId?if_exists != "PARTY_DISABLED">
			<#if request.getRequestURI()?has_content && request.getRequestURI().contains("/sr-portal")>
			<#assign rightContent = rightContent + '<button id="view-all-pics-btn" title="View All Pics" class="btn btn-primary btn-xs ml-2">View All Pics</button>' />
			</#if>
			<#assign rightContent = rightContent + '<span id="create-attachment-btn" title="attachment" class="btn btn-primary btn-xs ml-2 " ><i class="fa fa-edit" aria-hidden="true"></i> Add Attachment </span>' />
			<#if clientPortal?has_content && "clientPortal" == clientPortal>
			<#else>
				<#assign rightContent = rightContent + '<span id="create-bookmark-btn" title="BookMarkURLs" class="btn btn-primary btn-xs ml-2 " ><i class="fa fa-edit" aria-hidden="true"></i> Bookmark URL</span>' />
			</#if>
			<#if clientPortal?has_content && "clientPortal" == clientPortal>
			<#else>
				
			</#if>
		</#if> 
	</#if>
</#if>

<div class="row">
<div class="col-lg-12 col-md-12 col-sm-12">
	
 <#--<@AgGrid
	gridheadertitle="Bookmarks and Files"
	gridheaderid="attachment-grid-action-container"
	savePrefBtn=true
	clearFilterBtn=true
	exportBtn=exportBtn
	insertBtn=false
	updateBtn=false
	removeBtn=removeBtn!
	helpBtn=helpBtn
	helpUrl=helpUrl!
	headerextra=rightContent!
	refreshPrefBtnId="attachment-refresh-pref-btn"
	savePrefBtnId="attachment-save-pref-btn"
	clearFilterBtnId="attachment-clear-filter-btn"
	subFltrClearId="attachment-sub-filter-clear-btn"
	exportBtnId="attachment-export-btn"
	removeBtnId="remove-attachment-btn"
    userid="${userLogin.userLoginId}" 
    shownotifications="true" 
    instanceid="ATTACHMENTS" 
    autosizeallcol="true"
    debug="false"
    />  
  	<script type="text/javascript" src="/common-portal-resource/js/ag-grid/attachment/find-attachment.js"></script>-->

  	<input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
  	<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
	<form method="get" action="/common-portal/control/getPicsList" id="ViewPicsForm" name="ViewPicsForm" target="_blank" novalidate="true" data-toggle="validator">
		<input type="hidden" name="domainEntityType" value="${domainEntityType!}">
		<input type="hidden" name="domainEntityId" value="${domainEntityId!}">
		<input type="hidden" name="externalLoginKey" value="${externalLoginKey!}"/>
		<input type="hidden" id="contentIdsList" name="contentIdsList" value=""/>
		<input type="hidden" name="requestURI" value="${request.getRequestURI()!}"/>
	</form>

		<@fioGrid 
			id="attachment-grid"
			instanceId="ATTACHMENTS"
			jsLoc="/common-portal-resource/js/ag-grid/attachment/find-attachment.js"
			headerLabel="Bookmarks and Files"
			headerId="attachment-grid-action-container"
			headerBarClass="grid-header-no-bar"
			savePrefBtn=true
			clearFilterBtn=true
			exportBtn=exportBtn
			subFltrClearBtn = true
			exportBtnId="attachment-export-btn"
			savePrefBtnId ="attachment-save-pref-btn"
			clearFilterBtnId ="attachment-clear-filter-btn"
			subFltrClearId="sub-filter-clear-btn"
			helpBtn=helpBtn
			helpUrl=helpUrl!
			headerExtra=rightContent!
			/>
	
</div>
</div>

<@imgPreviewModal 
instanceId="preview-image"
/>