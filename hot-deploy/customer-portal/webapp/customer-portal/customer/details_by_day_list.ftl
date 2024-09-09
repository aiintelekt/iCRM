<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl" />
<#assign requestURI="" />
<#if request.getRequestURI().contains("main")>
<#assign helpUrl=Static["org.groupfio.common.portal.util.DataHelper"].getTabHelpUrl(delegator, request.getRequestURI(), "main" ) />
</#if>
<div class="row" style="width:100%" id="listof-lead">
	<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
		<form method="post" id="detailsByDayForm" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
			<#-- <@AgGrid
				gridheadertitle="Details By Day Report List"
				gridheaderid="customer-grid-action-container"
				savePrefBtn=false
				clearFilterBtn=false
				exportBtn=true
				insertBtn=false
				updateBtn=false
				removeBtn=false
				refreshPrefBtn=false
				savePrefBtn=false
				subFltrClearBtn = false
				exportBtnId="details-by-day-export-btn"
				userid="${userLogin.userLoginId}" 
				shownotifications="true" 
				instanceid="DETAILS_BY_DAY" 
				autosizeallcol="true"
				debug="false"
				statusBar=true
				/>
			<script type="text/javascript" src="/contact-portal-resource/js/ag-grid/details-by-day-list.js"></script>-->
	<input type="hidden" id="userId" value="${userLogin.userLoginId}"/>
  	<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

	 				<@fioGrid 
						id="detail-rates-Grid"
						instanceId="DETAILS_BY_DAY"
						jsLoc="/contact-portal-resource/js/ag-grid/details-by-day-list.js"
						headerLabel="Details By Day Report List"
						headerId="detail-grid-action-container"
						savePrefBtn=false
						clearFilterBtn=false
						subFltrClearBtn=false
						exportBtn=true
						headerBarClass="grid-header-no-bar"
						clearFilterBtnId="detail-clear-pref-btn"
						subFltrClearId="detail-sub-filter-clear-btn"
						savePrefBtnId="detail-save-filter-btn"
						exportBtnId="details-by-day-export-btn"
						/>
		</form>
	</div>
</div>