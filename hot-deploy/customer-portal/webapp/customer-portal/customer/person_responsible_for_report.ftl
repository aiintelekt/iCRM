<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl" />
<#assign requestURI="" />
<#if request.getRequestURI().contains("main")>
    <#assign helpUrl=Static["org.groupfio.common.portal.util.DataHelper"].getTabHelpUrl(delegator, request.getRequestURI(), "main" ) />
</#if>
<div class="row">
    <div id="main" role="main" class="pd-btm-title-bar">
        <div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
            <@sectionFrameHeader title="PERSON RESPONSIBLE FOR REPORT" extra=helpUrl! />
            <div class="panel-group" id="accordionMenu" role="tablist" aria-multiselectable="true">
                <div class="panel panel-default">
                    <div id="accordionDynaBase" class="panel-collapse collapse show" role="tabpanel" aria-labelledby="headingOne">
                        <form method="post" id="personResponsibleForReportForm" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
                            <#assign personResponsibleFor='${requestParameters.personResponsibleFor!}'>
                                <@inputHidden id="personResponsibleFor" value="${personResponsibleFor!}" />
                                <div class="panel-body">
                                   <#--  <@AgGrid
										gridheadertitle="Person Responsible For Report"
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
										exportBtnId="person-resposible-for-report-export-btn"
									    userid="${userLogin.userLoginId}" 
									    shownotifications="true" 
									    instanceid="PERSON_RESPOSIBLE_FOR_REPORT" 
									    autosizeallcol="true"
									    debug="false"
										statusBar=true
									/>
                                    <script type="text/javascript" src="/contact-portal-resource/js/ag-grid/person-responsible-for-report.js"></script>-->
    <input type="hidden" id="userId" value="${userLogin.userLoginId}"/>
  	<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

	 				<@fioGrid 
						id="personResponsibleGrid"
						instanceId="PERSON_RESPOSIBLE_FOR_REPORT"
						jsLoc="/contact-portal-resource/js/ag-grid/person-responsible-for-report.js"
						headerLabel="Person Responsible For Report"
						headerId="personResponsible-grid-action-container"
						savePrefBtn=false
						clearFilterBtn=false
						subFltrClearBtn=false
						exportBtn=true
						headerBarClass="grid-header-no-bar"
						clearFilterBtnId="personResponsible-clear-pref-btn"
						subFltrClearId="personResponsible-sub-filter-clear-btn"
						savePrefBtnId="personResponsible-save-filter-btn"
						exportBtnId="person-resposible-for-report-export-btn"
						/>
                                </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>