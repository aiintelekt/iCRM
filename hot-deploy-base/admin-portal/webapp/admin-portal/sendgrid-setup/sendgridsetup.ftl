<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
<div class="row">
	<div id="main" role="main">
		<#assign helpUrl=Static["org.groupfio.common.portal.util.DataHelper"].getHelpUrl(delegator, request.getRequestURI())/>
		<#assign extras=''/>
		<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
			<div><@sectionFrameHeader title="${uiLabelMap.SendgridSetup!}"/></div>
			<form id="sendGridSetup" method="post" action="<@ofbizUrl>createSendGridConfig</@ofbizUrl>" data-toggle="validator" novalidate="novalidate">
				<@inputHidden id="configId" value=""/>
				<@dynaScreen
					instanceId="CREATE_SENDGRID_SETUP"
					modeOfAction="CREATE"
					/>
				<div class="offset-md-2 col-sm-10">
					<input type="submit" class="btn btn-sm btn-primary disabled" value="Create" id="createSendGrid"/>
					<@reset
						id="reset-sendgrid"
						label="${uiLabelMap.Reset}"
						/>
				</div>
				<br>
			</form>
		</div>
	</div>
</div>
<form id="sendGridListForm" method="post">
	<@inputHidden id="configIdValue" value=""/>
</form>
<#include "component://admin-portal/webapp/admin-portal/sendgrid-setup/modal-window.ftl"/>
<div class="row" style="width:100%">
	<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
		<#-- <@AgGrid
			gridheadertitle=uiLabelMap.SendgridSetupList
			gridheaderid="${instanceId!}-grid-action-container"
			savePrefBtn=true
			clearFilterBtn=true
			exportBtn=true
			insertBtn=false
			updateBtn=false
			removeBtn=false
			headerextra=""
			userid="${userLogin.userLoginId}"
			removeBtnId=""
			refreshPrefBtnId="sendgrid-refresh-pref-btn"
			savePrefBtnId="sendgrid-save-pref-btn"
			clearFilterBtnId="sendgrid-clear-filter-btn"
			subFltrClearId="sendgrid-sub-filter-clear-btn"
			exportBtnId="sendgrid-export-btn"
			shownotifications="true"
			instanceid="SENDGRIDSETUP_LIST"
			autosizeallcol="true"
			debug="false"
			/>
		<script type="text/javascript" src="/admin-portal-resource/js/ag-grid/sendgrid-setup/sendgrid-setup.js"></script>-->
	<input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
  	<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

		<@fioGrid 
			id="${instanceId!}-grid"
			instanceId="SENDGRIDSETUP_LIST"
			jsLoc="/admin-portal-resource/js/ag-grid/sendgrid-setup/sendgrid-setup.js"
			headerLabel=uiLabelMap.SendgridSetupList!
			headerId="${instanceId!}-grid-action-container"
			headerBarClass="grid-header-no-bar"
			savePrefBtn=false
			clearFilterBtn=false
			exportBtn=true
			subFltrClearBtn = false
			savePrefBtnId ="sendgrid-save-pref-btn"
			clearFilterBtnId ="sendgrid-clear-filter-btn"
			subFltrClearId="sendgrid-sub-filter-clear-btn"
			exportBtnId="sendgrid-list-export-btn"
			/>
	</div>
</div>