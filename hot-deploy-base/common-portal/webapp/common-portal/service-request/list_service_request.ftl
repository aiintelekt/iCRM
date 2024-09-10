<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#assign helpUrl = Static["org.groupfio.common.portal.util.DataHelper"].getHelpUrl(delegator, request.getRequestURI(), "serviceRequest") />  

<#assign hasPermission = Static["org.fio.homeapps.util.DataUtil"].hasPermission(request, "CREATE_OPERATION")?if_exists />
<#assign partyId= request.getParameter("partyId")! />
<#assign servicePortalName = Static["org.groupfio.common.portal.util.SrUtil"].getServicePortalName(delegator)?if_exists>

<#if partyStatusId?if_exists != "PARTY_DISABLED"> 
	<#if hasPermission>
		<#assign rightContent='<a id="create-sr" href="/${servicePortalName!}/control/addservicerequest?partyId=${request.getParameter("partyId")!}&externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank" class="btn btn-primary btn-xs ml-2"> <i class="fa fa-plus" aria-hidden="true"></i> Create </a>' />
	</#if> 
</#if>

<#assign extraLeft = '<div class="form-check-inline ml-30">
				<label class="form-check-label"> 
				<input type="checkbox" class="form-check-input sr-status" id="srOpen" name="srOpen" value="SR_OPEN">Open
				</label>
			</div>
			<div class="form-check-inline">
				<label class="form-check-label"> 
				<input type="checkbox" class="form-check-input sr-status" id="srSlaAtRisk" name="srSlaAtRisk" value="Y">SLA at Risk
				</label>
			</div>
			<div class="form-check-inline">
				<label class="form-check-label"> 
				<input type="checkbox" class="form-check-input sr-status" id="srSlaExpired" name="srSlaExpired" value="Y">Overdue
				</label>
			</div>
			<div class="form-check-inline">
				<label class="form-check-label"> 
				<input type="checkbox" class="form-check-input sr-status" id="srClosed" name="srClosed" value="SR_CLOSED">Completed
				</label>
			</div>'/>

<div class="row">
	
  	<div class="col-lg-12 col-md-12 col-sm-12">
		  	
	  	<div id="service-request-grid" style="width: 100%;" class="ag-theme-balham"></div>
	  	
	  	<#assign extraContent='
		<button id="refresh-sr-btn" type="button" class="btn btn-xs btn-primary m5"><i class="fa fa-refresh" aria-hidden="true"></i></button>
		' />
		
		<#if rightContent?has_content>
			<#assign extraContent =  extraContent + rightContent />
		</#if>		
	  	
	  	<@fioGrid 
			id="service-request-grid"
			instanceId="SR_LIST"
			jsLoc="/common-portal-resource/js/ag-grid/service-request/find-service-request.js"
			headerLabel="Service Requests"
			headerId="service_request_tle"
			headerExtra=extraContent!
			headerBarClass="grid-header-no-bar"
			headerExtraLeft = extraLeft!
			exportBtn=true
			exportBtnId="service-request-export-btn"
			savePrefBtnId="service-request-save-pref"
			clearFilterBtnId="service-request-clear-pref"
			subFltrClearId="service-request-clear-sub-ftr"
			savePrefBtn=false
			clearFilterBtn=false
			subFltrClearBtn=false
			/>             
	</div>
	<span id="service-req-btn"></span>
</div>