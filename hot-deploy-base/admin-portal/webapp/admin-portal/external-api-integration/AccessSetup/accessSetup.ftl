<#include "component://ab-ag-grid-support/webapp/ab-ag-grid-support/lib/ag_grid_header.ftl"/>
<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<div class="row">
<div id="main" role="main">
	<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
		<@sectionFrameHeader title="${uiLabelMap.AccessSetup!}"  />
		<div class="panel-heading" role="tab" id="headingTwo">
			<h4 class="panel-title">
				<a role="button" data-toggle="collapse" data-parent="#accordionMenu"
					href="#accordionDynaBase" aria-expanded="true"
					aria-controls="collapseOne"> ${uiLabelMap.MainFilter}
				</a>
			</h4>
		</div>
		<div id="accordionDynaBase" class="panel-collapse collapse show" role="tabpanel" aria-labelledby="headingOne">
			<form action="accessSetup" method="post" id="searchForm" name="searchForm">
				<@inputHidden id="searchCriteria" />
				<div class="row pb-2">
					<div class="col-md-4 col-lg-4 col-sm-12">
					<@dropdownCell
						name="systemName"
						id="systemName"
						allowEmpty =  true
						options=systemName
						value="${requestParameters.systemName?if_exists}"
						placeholder = uiLabelMap.systemName />
					</div>
					<div class="col-md-4 col-lg-4 col-sm-12">
					<@dropdownCell
						name="authMethod"
						id="authMethod"
						allowEmpty =  true
						options=authMethod
						value="${requestParameters.authMethod?if_exists}"
						placeholder = uiLabelMap.selectAuthMethod />
					</div>
					<div class="col-md-4 col-lg-4 col-sm-12">
					<#-- <#assign statuses = delegator.findByAnd("Enumeration", {"enumTypeId" : "STATUS_ID"}, null, false)>
					<#assign statusesList = Static["org.fio.admin.portal.util.DataHelper"].getDropDownOptions(statuses, "enumId","description")?if_exists />
					<@dropdownCell
						name="status"
						id="status"
						allowEmpty =  true
						options=statusesList!
						value="${requestParameters.status?if_exists}"
						placeholder = uiLabelMap.selectStatus />-->
					<div class="text-right">
					<@button 
						label="${uiLabelMap.Search}"
						id="main-search-btn" />
					</div>
					</div>
				</div>
			</form>
			</div>
	</div>
         <#-- 
         <div class="table-responsive">
          <div class="loader text-center" id="loader" sytle="display:none;">
                  <span></span>
                  <span></span>
                  <span></span>
            </div>
            <div id="grid1" style="width: 100%;" class="ag-theme-balham"></div>
            <script type="text/javascript" src="/admin-portal-resource/js/ag-grid/external-api/access-setup.js"></script> 
        </div>
       	 -->
	<div class="col-m-12 col-sm-12 col-lg-12 dash-panel">
		<#assign headerextra='<a title="Create" href="/admin-portal/control/createAccessSetup" class="btn btn-primary btn-xs ml-2" target="_blank"> <i class="fa fa-plus" aria-hidden="true"></i> Create </a>' />   
		<#-- <@AgGrid
			gridheadertitle=uiLabelMap.ListOfConfiguration
			userid="${userLogin.userLoginId}"
			instanceid="ACCESS_SETUP"
			headerextra=headerextra!
			autosizeallcol="true"
			gridoptions='{"pagination": true, "paginationPageSize": 10 }' />
			<script type="text/javascript" src="/admin-portal-resource/js/ag-grid/external-api/access-setup.js">-->
<input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

		<@fioGrid 
			id="access-setup-Grid"
			instanceId="ACCESS_SETUP"
			jsLoc="/admin-portal-resource/js/ag-grid/external-api/access-setup.js"
			headerLabel=uiLabelMap.ListOfConfiguration!
			headerId="access-setup-grid-action-container"
			subFltrClearId="access-setup-sub-filter-clear-btn"
			savePrefBtnId="access-setup-save-pref-btn"
			clearFilterBtnId="access-setup-clear-filter-btn"
			headerBarClass="grid-header-no-bar"
			savePrefBtn=false
			clearFilterBtn=false
			exportBtn=true
			subFltrClearBtn=false
			headerExtra=headerextra!
			exportBtnId="access-setup-list-export-btn"
			/>
	</div>
</div>
</div>

<script>

function loadMainGrid(fag) {
		
	$.ajax({
	  url:'/admin-portal/control/getAccessSetup',
	  type:"POST",
	  data: JSON.parse(JSON.stringify($("#searchForm").serialize())),
	  success: function(data){
			// console.log("data: ", data)
		  fag.setRowData(data)
	  }
	})
	
}

/*
function doSearch(){
        loadAgGrid();
    }
 $("#systemName").change(function() {
         loadAgGrid();
    });
$("#authMethod").change(function() {
         loadAgGrid();
    });
$("#status").change(function() {
         loadAgGrid();
    });
*/

</script>