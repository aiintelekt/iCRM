<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#assign partyId= request.getParameter("partyId")! />

<#assign helpUrl = Static["org.groupfio.common.portal.util.DataHelper"].getHelpUrl(delegator, request.getRequestURI(), "bookmarksAndFiles") />
<#assign workFlowStatus = Static["org.groupfio.approval.portal.util.DataUtil"].getWorkflowStatus(delegator, Static["org.ofbiz.base.util.UtilMisc"].toMap("approvalCategoryId", approvalCategoryId!, "domainEntityType", domainEntityType!, "domainEntityId", domainEntityId!))?if_exists />
<#assign isEnableInitiateWorkFlow = Static["org.groupfio.approval.portal.util.DataUtil"].isEnableInitiateWorkFlow(delegator, Static["org.ofbiz.base.util.UtilMisc"].toMap("loggedUserId", loggedUserId!, "createdByUserLogin", createdByUserLogin!, "approvalTemplateId", inputContext.approvalTemplateId!, "approvalCategoryId", approvalCategoryId!, "domainEntityType", domainEntityType!, "domainEntityId", domainEntityId!))?if_exists />
	
<#assign srStatusId= context.get("currentSrStatusId")?if_exists />
<#assign clientPortal= context.get("clientPortal")?if_exists />

<#if workFlowStatus.status?has_content>
	<div class="form-group row" style="text-align: right">
    	<label class="col-sm-11">Approval Status: </label>
        <div class="col-sm-1">
          	<strong>${workFlowStatus.status!}</strong>
        </div>
   	</div>
</#if>

<#assign rightContent=''/>
<#if isEnableInitiateWorkFlow>
<#assign rightContent='
		<button id="approval-initiate-btn" type="button" class="btn btn-xs btn-primary m5" data-toggle="confirmation" title="${uiLabelMap.ApprovalInitiateConfirmation!}"><i class="fa fa-cogs" aria-hidden="true"></i> ${uiLabelMap.InitiateApproval!}</button>
		' />
</#if>
<#assign rightContent= rightContent+'
		<button id="refresh-attachment-thirdpty-btn" type="button" class="btn btn-xs btn-primary m5"><i class="fa fa-refresh" aria-hidden="true"></i></button>
		' />
<#assign hasPermission = Static["org.fio.homeapps.util.DataUtil"].hasPermission(request, "CREATE_OPERATION")?if_exists />
<#if hasPermission || (clientPortal?has_content && "clientPortal" == clientPortal)>
	<#if partyStatusId?if_exists != "PARTY_DISABLED">
		<#assign rightContent = rightContent + '<span id="create-attachment-thirdpty-btn" title="attachment" class="btn btn-primary btn-xs ml-2 " ><i class="fa fa-edit" aria-hidden="true"></i> Add Attachment </span>' />
	</#if> 
</#if>

<div class="row">

<div class="col-lg-12 col-md-12 col-sm-12">
	
	<@AgGrid
	gridheadertitle="3rd Party Invoice"
	gridheaderid="attachment-thirdpty-grid-action-container"
	savePrefBtn=true
	clearFilterBtn=true
	exportBtn=true
	insertBtn=false
	updateBtn=false
	removeBtn=true
	helpBtn=true
	helpUrl=helpUrl!
	headerextra=rightContent!
	refreshPrefBtnId="attachment-thirdpty-refresh-pref-btn"
	savePrefBtnId="attachment-thirdpty-save-pref-btn"
	clearFilterBtnId="attachment-thirdpty-clear-filter-btn"
	exportBtnId="attachment-thirdpty-export-btn"
	removeBtnId="remove-attachment-thirdpty-btn"
    userid="${userLogin.userLoginId}" 
    shownotifications="true" 
    instanceid="ATTACHMENTS_THIRDPTY" 
    autosizeallcol="true"
    debug="false"
    />  
    
  	<script type="text/javascript" src="/common-portal-resource/js/ag-grid/attachment/third_party/find-attachment.js"></script>
	
</div>
  	
</div>
