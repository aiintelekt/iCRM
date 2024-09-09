<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<div class="row">
	<div id="main" role="main">
		<div class="col-lg-12 col-md-12 col-sm-12 profil-sec-padding pt-1 dash-panel">
		<span id="dashboard-filter"></span>
			<div class="">
				<h2 class="float-left"><span id="optHeader"></span></h2>
				<div class="clearfix"></div>
			</div><div class="clearfix"></div>
			<div class="">
				<div id="myCampaignsList" style="height:auto; width: 100%;" class="ag-theme-balham">
					<#assign rightContent='
					<span id="change-state" title="Instance state" class="btn btn-primary btn-xs ml-2" data-toggle="dropdown" aria-expanded="false"> <i class="fa fa-plus" aria-hidden="true"></i> Instance state </span>
					<div class="dropdown-menu" aria-labelledby="change-state">
						<a class="dropdown-item change-state-drpdwns" state="stop" href="#">Stop instance</a>
						<a class="dropdown-item change-state-drpdwns" state="start" href="#">Start instance</a>
						<a class="dropdown-item change-state-drpdwns" state="reboot" href="#">Reboot instance</a>
					</div>
					<button id="aws-refresh-btn" type="button" class="btn btn-xs btn-primary m5"><i class="fa fa-refresh" aria-hidden="true"></i></button>
					' />
					<#assign exportBtn=true>
					<#if isPhoneCampaignEnabled?has_content && isPhoneCampaignEnabled?if_exists=="Y">
					<#assign exportBtn=false>
					</#if>
					<@AgGrid
						gridheadertitle="AWS Instances" 
						gridheaderid="campaign-grid-action-container"
						savePrefBtn=true
						clearFilterBtn=true
						exportBtn=exportBtn
						insertBtn=false
						updateBtn=false
						removeBtn=false
						headerextra=rightContent!
						refreshPrefBtnId="aws-refresh-pref-btn"
						savePrefBtnId="save-pref-btn"
						clearFilterBtnId="clear-filter-btn"
						exportBtnId="export-btn"
						userid="${userLogin.userLoginId}"
						shownotifications="true"
						instanceid="AWS_INSTANCES"
						autosizeallcol="true"
						debug="false"
						serversidepaginate=true
						statusBar=true
					 /> 
				</div>
				<script type="text/javascript" src="/common-portal-resource/js/ag-grid/common/find-aws-instances.js"></script>
			</div>
		</div>
	 </div>
 </div>
 
<form method="post" id="searchForm" class="form-horizontal" novalidate="novalidate" data-toggle="validator">

<@inputHidden id="statusId" value="MKTG_CAMP_PUBLISHED"/>
<@inputHidden id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

</form>
<style>
.ag-theme-balham .ag-header-select-all span {
	height: auto;
}
</style>