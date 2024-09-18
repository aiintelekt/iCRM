<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl" />
<#assign requestURI="" />
<#if request.getRequestURI().contains("main")>
<#assign helpUrl=Static["org.groupfio.common.portal.util.DataHelper"].getTabHelpUrl(delegator, request.getRequestURI(), "main" ) />
</#if>
<div class="row" style="width:100%" id="listof-posOrder">
	<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
		<form method="post" id="detailsByDayForm" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
		<input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
	  	<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
	 				<@fioGrid 
						id="pos-order-Grid"
						instanceId="FIND_POS_ORDER_LIST"
						jsLoc="/contact-portal-resource/js/ag-grid/pos-order-list.js"
						headerLabel="POS Order List"
						headerId="pos-order-grid-action-container"
						savePrefBtn=false
						clearFilterBtn=false
						subFltrClearBtn=false
						exportBtn=true
						headerBarClass="grid-header-no-bar"
						clearFilterBtnId="pos-order-clear-pref-btn"
						subFltrClearId="pos-order-sub-filter-clear-btn"
						savePrefBtnId="pos-order-save-filter-btn"
						exportBtnId="pos-order-export-btn"
						serversidepaginate=true
						statusBar=true
						/>
		</form>
	</div>
</div>