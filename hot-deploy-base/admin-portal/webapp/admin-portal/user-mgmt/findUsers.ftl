<#include "component://ab-ag-grid-support/webapp/ab-ag-grid-support/lib/ag_grid_header.ftl"/>
<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<div class="row">
	<div id="main" role="main">
		<#-- <@sectionFrameHeader title="${uiLabelMap.FindUsersAndRoles!}"/>  -->
		<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
			<div>
				<#--
				<div class="row">
					<div class="iconek">
						<div class="arrow-down" style="margin-top: 5px;" onclick="this.classList.toggle('active')"></div>
					</div>
				</div>
				-->
				<div class="">
					<form action="#" method="post" id="searchForm" name="searchForm">
						<div><@sectionFrameHeader title="${uiLabelMap.FindUsers!}"/></div>
						<div class="row">
							<div class="col-lg-4 col-md-6 col-sm-12">
								<#assign ulDetails = delegator.findByAnd("UserLoginPerson",null,null, false)?if_exists/>
								<#assign ulList = Static["org.fio.admin.portal.util.DataHelper"].getDropDownOptionsFromMultiDesField(ulDetails, "userLoginId", Static["org.ofbiz.base.util.UtilMisc"].toList("firstName","lastName"))?if_exists/>
								<@inputAutoComplete
									id="partyId"
									label=""
									inputColSize="form-group"
									isAutoCompleteEnable="Y"
									onkeydown=true
									autoCompleteMinLength=1
									placeholder="Party Name"
									autoCompleteLabelFieldId="partyName"
									autoCompleteValFieldId="partyId"
									autoCompleteFormId="searchForm"
									autoCompleteUrl="/admin-portal/control/getUsers"
									/>
							</div>
							<div class="col-lg-4 col-md-6 col-sm-12">
								<#assign buDetails = delegator.findByAnd("ProductStoreGroup",{"status":"ACTIVE"},null, false)?if_exists/>
								<#assign buIdList = Static["org.fio.admin.portal.util.DataHelper"].getDropDownOptions(buDetails, "productStoreGroupId", "productStoreGroupName")?if_exists/>
								<@dropdownCell
									id="businessUnit"
									name="businessUnit"
									placeholder="${uiLabelMap.SelectBusinessUnit}"
									value="${requestParameters.businessUnit?if_exists}"
									options=buIdList!
									allowEmpty=true
									/>
							</div>
							<div class="col-lg-4 col-md-6 col-sm-12">
								<#assign userStatusEnums = Static["org.fio.admin.portal.util.EnumUtil"].getEnums(delegator, "STATUS_ID")?if_exists/>
								<#assign userStatusList = Static["org.fio.admin.portal.util.DataHelper"].getDropDownOptions(userStatusEnums, "enumCode","description")?if_exists/>
								<@dropdownCell
									id="status"
									name="status"
									placeholder="${uiLabelMap.SelectUserStatus}"
									value="${requestParameters.status?if_exists}"
									options=userStatusList!
									allowEmpty=true
									/>
								<div class="text-right pad-10">
									<@button 
										label="${uiLabelMap.Search}"
										id="main-search-btn"
										/>
									<@reset label="${uiLabelMap.Clear}"
										id="reset-party"
										onclick="clearParty()"
										/>
								</div>
							</div>
						</div>
					</form>
				</div>
			</div>
		</div>
		<div class="clearfix"></div>
		<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
			<#-- <@AgGrid
				gridheadertitle=uiLabelMap.ListOfUsers
				gridheaderid="user-role-grid-action-container"
				savePrefBtn=true
				clearFilterBtn=true
				exportBtn=true
				insertBtn=false
				updateBtn=false
				removeBtn=false
				userid="${userLogin.userLoginId}"
				shownotifications="true"
				instanceid="USER_ROLE"
				autosizeallcol="true"
				debug="false"
				/>
			<script type="text/javascript" src="/admin-portal-resource/js/ag-grid/user-mgmt/user-roles.js"></script>-->
	<input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
	<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

		<@fioGrid 
			id="ListOfUsers-Grid"
			instanceId="USER_ROLE"
			jsLoc="/admin-portal-resource/js/ag-grid/user-mgmt/user-roles.js"
			headerLabel=uiLabelMap.ListOfUsers
			headerId="user-role-grid-action-container"
			subFltrClearId="user-role-sub-filter-clear-btn"
			savePrefBtnId="user-role-save-pref-btn"
			clearFilterBtnId="user-role-clear-filter-btn"
			headerBarClass="grid-header-no-bar"
			savePrefBtn=true
			clearFilterBtn=true
			exportBtn=true
			subFltrClearBtn=true
			exportBtnId="user-role-list-export-btn"
			/>	

		</div>
	</div>
</div>
</div>
<script>
function clearParty(){
	$('#partyId_val').val("");
	$('#partyId_alter').val("");
}
$(document).ready(function(){
	$("#partyId_desc").keyup(function(){
		if ($("#partyId_desc").val() != ""){
			$('#partyId_val').val("");
		} else {
			$('#partyId_val').val("");
		}
	});
});
</script>