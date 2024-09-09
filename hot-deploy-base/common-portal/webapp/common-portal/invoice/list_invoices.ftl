<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<#assign helpUrl = Static["org.groupfio.common.portal.util.DataHelper"].getHelpUrl(delegator, request.getRequestURI(), "listCampaigns") />

<div class="row">
<div class="col-lg-12 col-md-12 col-sm-12">
<@AgGrid
	gridheadertitle="Invoices"
	gridheaderid="invoice-grid-action-container"
	savePrefBtn=true
	clearFilterBtn=true
	exportBtn=true
	insertBtn=false
	updateBtn=false
	removeBtn=false
	helpBtn=true
	helpUrl=helpUrl!
	refreshPrefBtnId="invoice-refresh-pref-btn"
	savePrefBtnId="invoice-save-pref-btn"
	clearFilterBtnId="invoice-clear-filter-btn"
	exportBtnId="invoice-export-btn"
    userid="${userLogin.userLoginId}" 
    shownotifications="true" 
    instanceid="CSP_INVOICE_GRID" 
    autosizeallcol="true"
    debug="false"
    />    
	<script type="text/javascript" src="/common-portal-resource/js/ag-grid/invoice/findInvoice.js"></script>
</div>
</div>