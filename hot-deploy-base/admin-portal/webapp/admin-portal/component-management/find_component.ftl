<#include "component://ab-ag-grid-support/webapp/ab-ag-grid-support/lib/ag_grid_header.ftl" />
<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl" />
<div class="row">
	<div id="main" role="main">
		<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
			<div>
				<div>
					<form action="#" method="post" id="searchForm" name="searchForm">
						<@sectionFrameHeader title="Find Components" />
				<div class="row">
					<div class="col-lg-4 col-md-6 col-sm-12">
						<#assign components=delegator.findByAnd("OfbizComponentAccess",null,null,false)?if_exists />
						<#assign ulList=Static["org.fio.admin.portal.util.DataHelper"].getDropDownOptionsFromMultiDesField(components, "componentId" , Static["org.ofbiz.base.util.UtilMisc"].toList("uiLabels"))?if_exists />
						<@dropdownCell
							id="componentId"
							name="componentId"
							placeholder="${uiLabelMap.SelectName}"
							value="${requestParameters.componentId?if_exists}"
							options=ulList! />
					</div>
					<div class="col-lg-4 col-md-6 col-sm-12">
						<#assign hideOption=Static["org.fio.admin.portal.util.DataHelper"].getDropDownOptionsFromMultiDesField(components, "isHide" , Static["org.ofbiz.base.util.UtilMisc"].toList("isHide"))?if_exists />
						<@dropdownCell
							id="isHide"
							name="isHide"
							placeholder="${uiLabelMap.isHide}"
							value="${requestParameters.isHide?if_exists}"
							options=hideOption! />
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
		gridheadertitle="List of Components"
		gridheaderid=""
		savePrefBtn=true
		clearFilterBtn=true
		exportBtn=true
		insertBtn=false
		updateBtn=true
		removeBtn=false
		userid=""
		shownotifications="true"
		instanceid="COMPONENTS"
		autosizeallcol="true"
		debug="false" />

	<script type="text/javascript" src="/admin-portal-resource/js/ag-grid/component-management/component.js"></script>
</div>