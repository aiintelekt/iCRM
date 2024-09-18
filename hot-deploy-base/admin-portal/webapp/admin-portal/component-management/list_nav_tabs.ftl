<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<style>
.new-config{
height:450px !important;
}
.modal-dialog-for-update {
	width: 44%;
}
</style>
<#assign headerextra1 = '<button class="btn btn-xs btn-primary" id="nav-tab-refresh-button" hidden="hidden"><i class="fa fa-refresh" aria-hidden="true"></i></button>'/>
<#assign headerextra2 ='<button class="btn btn-xs btn-primary" onclick="enableCreateNavPopup()" id="nav-tab-create-modal-button"><i class="fa fa-plus" aria-hidden="true"></i> Create Nav Tab</button>'/>
<#assign headerextra = headerextra1+headerextra2/>
<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
	<#--<@AgGrid
		gridheadertitle="${uiLabelMap.ListOfNavigationTab!}"
		gridheaderid="navigation-tab-grid-action-container"
		savePrefBtn=true
		clearFilterBtn=true
		exportBtn=true
		insertBtn=false
		updateBtn=false
		removeBtn=true
		headerextra=headerextra!
		userid="${userLogin.userLoginId}"
		removeBtnId="nav-tab-remove-btn"
		refreshPrefBtnId="nav-tab-refresh-pref-btn"
		savePrefBtnId="nav-tab-save-pref-btn"
		clearFilterBtnId="nav-tab-clear-filter-btn"
		subFltrClearId="nav-tab-sub-filter-clear-btn"
		exportBtnId="nav-tab-export-btn"
		shownotifications="true"
		instanceid="LIST_NAV_TABS"
		autosizeallcol="true"
		debug="false"
		/>

<script type="text/javascript" src="/admin-portal-resource/js/ag-grid/component-management/nav-tab.js"></script>-->
	<@fioGrid
		id="list-nav-tabs"
		instanceId="LIST_NAV_TABS"
		jsLoc="/admin-portal-resource/js/ag-grid/component-management/nav-tab.js"
		headerLabel=uiLabelMap.ListOfNavigationTab!
		headerExtra=extraRight!
		headerBarClass="grid-header-no-bar"
		headerId="list-nav-tabs-tle"
		savePrefBtnId="list-nav-tabs-save-pref"
		clearFilterBtnId="list-nav-tabs-clear-pref"
		subFltrClearId="list-nav-tabs-clear-sub-ftr"
		serversidepaginate=false
		statusBar=false
		savePrefBtn=false
		clearFilterBtn=false
		exportBtn=true
		subFltrClearBtn=false
		exportBtnId="list-nav-tabs-export-btn"
		/>
	</div>
	</div>
	</div>
</div>

<#-- Popup for create nav tab window -->
<div id="create-nav-tab-modal" class="modal fade">
	<div class="modal-dialog modal-lg">
	<!-- Modal content-->
		<form id="mainFrom" method="post" data-toggle="validator" onsubmit="return submitCreateNavTab();">
			<div class="modal-content">
				<div class="modal-header">
					<h2 id="create-nav-tab-modal_des_title" class="modal-title">${uiLabelMap.CreateNavTab!}</h2>
					<button type="reset" class="close" data-dismiss="modal">&times;</button>
				</div>
				<div class="modal-body" id="create-nav-tab-modal_des_value">
					<@dynaScreen
						instanceId="CREATE_NAV_TAB"
						modeOfAction="CREATE"
						/>
					<div class="row pt-2">
						<div class="offset-md-4 col-sm-10">
							<@formButton
								btn1type="submit"
								btn1label="${uiLabelMap.Save}"
								btn1id="create-nav-tab-button"
								btn2=true
								btn2onclick = "resetForm()"
								btn2type="reset"
								btn2id="reset-nav-tab-button"
								btn2label="${uiLabelMap.Clear}"
								/>
						</div>
					</div>
				</form>
			</div>
		</div>
	</div>
</div>

<#-- Popup for update nav tab window -->
<div id="update-nav-tab-modal" class="modal fade">
  <div class="modal-dialog modal-dialog-for-update modal-lg">
    <!-- Modal content-->
    <div class="modal-content">
    <form id="updateNavTab" method="post" data-toggle="validator" >
      <div class="modal-header">
        <h2 id="update-nav-tab-modal_des_title" class="modal-title"></h2>
        <button type="reset" class="close" data-dismiss="modal">&times;</button>
      </div>
      <div class="modal-body new-config" id="update-nav-tab-modal_des_value" >
			<@dynaScreen
			instanceId="UPDATE_NAV_TAB"
			modeOfAction="UPDATE"
			/>
       <div class="row pt-2">
       <div class="offset-md-4 col-sm-10">
       <button id="update-nav-tab-btn" type="submit" class="btn btn-sm btn-primary disabled">
         <a href=""><span style="color: white;">Update</span></a>
        </button>
       <button type="submit" class="btn btn-sm btn-primary navbar-dark" data-dismiss="modal">Cancel</button>
      </div>
      </div>
      </div>
      </form>
    </div>
  </div>
</div>
<script>
	$(document).ready(function() {
	
	});
	function resetForm(){
		$('[id*="_error"]').empty();
	}
</script>