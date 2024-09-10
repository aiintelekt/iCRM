<#include "component://ab-ag-grid-support/webapp/ab-ag-grid-support/lib/ag_grid_header.ftl" />
<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl" />
<div class="row">
	<div id="main" role="main">
		<#assign extra='<a href="findComponents" class="btn btn-xs btn-primary"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>' />
<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
	<div>
		<div>
			<form action="#" method="post" id="searchForm" name="searchForm">
				<@sectionFrameHeader title="Find Shortcut Tabs" extra=extra?if_exists />
				<div class="row">
					<input type="hidden" name="componentId" value="${requestParameters.componentId?if_exists}" />
					<div class="col-lg-4 col-md-6 col-sm-12">
						<#assign tabs=delegator.findByAnd("OfbizPageSecurity",null,null,false)?if_exists />
						<#assign ulList=Static["org.fio.admin.portal.util.DataHelper"].getDropDownOptionsFromMultiDesField(tabs, "tabId" , Static["org.ofbiz.base.util.UtilMisc"].toList("uiLabels"))?if_exists />
						<@dropdownCell
							id="tabId"
							name="tabId"
							placeholder="${uiLabelMap.SelectName}"
							value="${requestParameters.tabId?if_exists}"
							options=ulList! />
					</div>

					<div class="col-lg-4 col-md-6 col-sm-12">
						<#assign disabled=Static["org.fio.admin.portal.util.DataHelper"].getDropDownOptionsFromMultiDesField(tabs, "isDisabled" , Static["org.ofbiz.base.util.UtilMisc"].toList("isDisabled"))?if_exists />
						<@dropdownCell
						id="isDisabled"
						name="isDisabled"
						placeholder="${uiLabelMap.isDisabled}"
						value="${requestParameters.isDisabled?if_exists}"
						options=disabled! />
					</div>
					<div class="text-right pad-10">
						<@button label="${uiLabelMap.Search}" id="main-search-btn" />
					</div>

				</div>
			</form>
		</div>
	</div>
</div>
<div class="clearfix"></div>
<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
	<@AgGrid
		gridheadertitle="List of Shortcut Tabs"
		gridheaderid=""
		savePrefBtn=true
		clearFilterBtn=true
		exportBtn=true
		insertBtn=false
		updateBtn=true
		removeBtn=false
		userid=""
		shownotifications="true"
		instanceid="TAB_SHORTCUTS"
		autosizeallcol="true"
		debug="false" />
	<script type="text/javascript" src="/admin-portal-resource/js/ag-grid/component-management/shortcut-tab.js"></script>
</div>
<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
	<@AgGrid
		gridheadertitle="List of Shortcut Sub Menus"
		gridheaderid=""
		savePrefBtn=true
		clearFilterBtn=true
		exportBtn=true
		insertBtn=false
		updateBtn=true
		removeBtn=false
		userid=""
		shownotifications="true"
		instanceid="SUB_MENUS"
		autosizeallcol="true"
		debug="false" />
	<script type="text/javascript" src="/admin-portal-resource/js/ag-grid/component-management/sub-menu.js"></script>
	<form method="post" action="" id="menuForm" name="menuForm" novalidate="true" data-toggle="validator">
		<@inputHidden id="userLoginId" value="${requestParameters.tabId!}" />
		<@inputHidden id="selecteddRows" value="" />
	</form>
</div>