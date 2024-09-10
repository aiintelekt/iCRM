<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl" />

<div class="row" style="width:100%">
		<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
           <#-- <@AgGrid
                gridheadertitle="${uiLabelMap.listContactRatesReport!}"
                gridheaderid="contact-rates-grid-action-container"
                savePrefBtn=false
                clearFilterBtn=false
                exportBtn=true
                insertBtn=false
                updateBtn=false
                removeBtn=false
                refreshPrefBtn=false
                savePrefBtn=false
                subFltrClearBtn = false
                exportBtnId="contact-rates-report-export-btn"
                userid="${userLogin.userLoginId}" 
                shownotifications="true" 
                instanceid="CONTACT_RATES_REPORT" 
                autosizeallcol="true"
                debug="false"
                statusBar=true
            /> 
            <script type="text/javascript" src="/contact-portal-resource/js/ag-grid/contact-rates-report.js"></script>-->
    <input type="hidden" id="userId" value="${userLogin.userLoginId}"/>
  	<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

	 				<@fioGrid 
						id="contact-rates-Grid"
						instanceId="CONTACT_RATES_REPORT"
						jsLoc="/contact-portal-resource/js/ag-grid/contact-rates-report.js"
						headerLabel="${uiLabelMap.listContactRatesReport!}"
						headerId="contact-rates-grid-action-container"
						savePrefBtn=false
						clearFilterBtn=false
						subFltrClearBtn=false
						exportBtn=true
						headerBarClass="grid-header-no-bar"
						clearFilterBtnId="contact-rates-clear-pref-btn"
						subFltrClearId="contact-rates-sub-filter-clear-btn"
						savePrefBtnId="contact-rates-save-filter-btn"
						exportBtnId="contact-rates-report-export-btn"
						/>
        </div>
</div>