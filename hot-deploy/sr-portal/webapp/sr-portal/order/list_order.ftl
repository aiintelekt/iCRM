<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://sr-portal/webapp/sr-portal/attribute/modal_window.ftl"/>

<#assign readOnlyPermission = Static["org.fio.homeapps.util.DataUtil"].hasPermissionWoFullPerm(request, "READ_ONLY_PERM")?if_exists />

<#assign helpUrl = Static["org.groupfio.common.portal.util.DataHelper"].getTabHelpUrl(delegator, request.getRequestURI(), "listOrders") />
<#if readOnlyPermission!>
	<#assign extra =""/>
	<#assign removeAccess = false />
<#else>
	<#assign extraright = '<span id="help-url">${helpUrl?if_exists}</span> ' />
	<#-- <span id="approve-im-btn" title="Approve for IM" class="btn btn-primary btn-xs ml-2"> <i class="fa fa-check" aria-hidden="true"></i> Approve for IM </span> --> 	
	<#assign extra=''/>
	<#if enableFsrpartsOnlyModal?has_content && enableFsrpartsOnlyModal=="Y">
	<#assign partsOnlyBtn="btn-success">
	<#if partsOnlyAttr?has_content && partsOnlyAttr.attrValue?has_content>
		<#assign partsOnlyBtn="btn-danger">
	</#if>
	<#assign extra = '<span id="partsOnly-btn" title="Parts Changes" class="btn btn-xs ml-2 ${partsOnlyBtn!}" data-toggle="modal" data-target="#fsrPartsOnlyModal"> <i class="fa fa-eye" aria-hidden="true"></i> Parts Changes </span>'>
	</#if>
	<#assign extra = extra+'
		<span id="proof-btn" title="Proof" class="btn btn-primary btn-xs ml-2"> <i class="fa fa-search" aria-hidden="true"></i> Proof </span>
		<a title="Associate" target="_blank" href="/sr-portal/control/createSrOrderAssoc?srNumber=${srNumber!}" class="btn btn-primary btn-xs ml-2"> <i class="fa fa-plus" aria-hidden="true"></i> Add Order </a>
		<span class="btn btn-xs btn-primary" id="order-remove-btn" data-toggle="confirmation" title="" data-original-title="Are you sure to REMOVE ?"><i class="fa fa-remove" aria-hidden="true"></i> Remove</span>
		<span class="btn btn-xs btn-primary" id="update-btn" title="Save"><i class="fa fa-edit" aria-hidden="true"></i> Save</span>'/>
	<#if isAllowCreate?has_content && isAllowCreate=="N">
		<#assign extra =""/>
	</#if>
	<#assign isDisable = true />
	<#assign disableCloseOpTypes = Static["org.fio.homeapps.util.DataUtil"].getGlobalValue(delegator, "DISABLE_CLOSE_OP_TYPES", "") />
	<#if disableCloseOpTypes?has_content && srTypeId?has_content && disableCloseOpTypes.contains(srTypeId) >
		<#assign isDisable = false />
	</#if>
	
	<#if srStatusId?has_content && (srStatusId == "SR_CLOSED" || srStatusId == "SR_CANCELLED") && isDisable>
		<#assign extra = ""/>
	</#if>
	<#assign hasPermission = Static["org.fio.homeapps.util.DataUtil"].hasPermission(request, "DELETE_OPERATION")?if_exists />
	<#assign removeAccess = true />
	<#if !hasPermission || (srStatusId?has_content && (srStatusId == "SR_CLOSED" || srStatusId == "SR_CANCELLED"))>
		<#assign removeAccess = false />
	</#if>	
</#if>
<#if request.getContextPath().contains("client-portal")>
	<#assign removeAccess = false />
</#if>
<@inputHidden 
	id="inspectStatusList"
	value="${inspectStatusList?if_exists}"
	/>
<div class="row">
	
<div class="col-lg-12 col-md-12 col-sm-12">

	<div id="note-grid" style="width: 100%;" class="ag-theme-balham"></div>
	<#assign instanceId = "LIST_SR_ORDERS" />
	<#assign requestURI = request.getRequestURI()/>
	<#if requestURI.contains("/client-portal/control/viewServiceRequest") >
		<#assign instanceId = "CP_LIST_SR_ORDERS" />
	</#if>
	<@inputHidden id="gridInstanceId" value="${instanceId!}" />
	<#-- <@AgGrid
	gridheadertitle="Orders"
	gridheaderid="order-grid-action-container"
	savePrefBtn=true
	clearFilterBtn=true
	exportBtn=true
	insertBtn=false
	updateBtn=true
	removeBtn=removeAccess!
	headerextra=extra
	headerextraright=extraright!
	updateBtnLabel="Save"
	refreshPrefBtnId="order-refresh-pref-btn"
	savePrefBtnId="order-save-pref-btn"
	clearFilterBtnId="order-clear-filter-btn"
	subFltrClearId="order-sub-filter-clear-btn"
	exportBtnId="order-export-btn"
	removeBtnId="order-remove-btn"
    userid="${userLogin.userLoginId}" 
    shownotifications="true" 
    instanceid="${instanceId!}" 
    autosizeallcol="true"
    debug="false"
    />    
	
  	<script type="text/javascript" src="/sr-portal-resource/js/ag-grid/order/find-order.js"></script>-->
	<input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
  	<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

		<@fioGrid 
			id="order-grid"
			instanceId="${instanceId!}"
			jsLoc="/sr-portal-resource/js/ag-grid/order/find-order.js"
			headerLabel="Orders"
			headerId="order-grid-action-container"
			headerBarClass="grid-header-no-bar"
			savePrefBtn=false
			clearFilterBtn=false
			exportBtn=true
			subFltrClearBtn = false
			savePrefBtnId ="order-save-pref-btn"
			clearFilterBtnId ="order-clear-filter-btn"
			subFltrClearId="order-sub-filter-clear-btn"
			headerExtraRight=extraright!
			headerExtra=extra!
			exportBtnId="order-list-export-btn"
			/>
</div>
  	
</div>	


<@fsrPartsOnlyModal 
	instanceId="fsrPartsOnlyModal"
/>
<style>
#proof-modal .modal-content {
    height: auto !important;
    overflow-x: hidden !important;
    overflow-y: auto !important;
}
</style>

<div id="proof-modal" class="modal fade mt-2 save-modal" role="dialog">
    <div class="modal-dialog">
        <!-- Modal content-->
        <div class="modal-content">
        	<div class="modal-header">
		        <h3 class="modal-title" style="font-weight: bold;" id="proof-title"></h3>
		        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
		          <span aria-hidden="true">&times;</span>
		        </button>
		    </div>
		    <form name="proof-form" id="proof-form" method="POST">
            <div class="modal-body" style="padding-bottom:10px !important;">
               	<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>	 	
        		<@inputHidden name="justificationOld" id="justificationOld" value="${justificationOldProd!}"/>
        		<@inputHidden name="proofOrderIds"/>
            	<div class="form-group justificationOldProd" style="">
				    <label for="part-changes" class="col-form-label" style="font-size: 24px; font-weight: bold;">Changes </label>
		           	<div>
		               <label id="part-changes" class="col-form-label"></label>
		            </div>
		            <div class="row">
		            	<div class="col-lg-6 col-md-6 col-sm-6">
		            		<h3 id="proof-data-title-1" style="font-weight: bold;"></h3>
		            	</div>
		            	<div class="col-lg-6 col-md-6 col-sm-6">
		            		<h3 id="proof-data-title-2" style="font-weight: bold;"></h3>
		            	</div>
		            </div>
		            <div class="row proof-section">
		            	<div class="col-lg-6 col-md-6 col-sm-6">
		            		<div id="proof-data-desc-1" class="proof-data-desc1-bg"></div>
		            	</div>
		            	<div class="col-lg-6 col-md-6 col-sm-6">
		            		<div id="proof-data-desc-2" class="proof-data-desc2-bg"></div>
		            	</div>
		            </div>
		        </div>
            </div>
            <div class="modal-footer">
		        <input type="button" id="proof-action-btn" value="Proof" class="btn btn-primary m5">
		    </div>
		    </form>
        </div>
    </div>
</div>

