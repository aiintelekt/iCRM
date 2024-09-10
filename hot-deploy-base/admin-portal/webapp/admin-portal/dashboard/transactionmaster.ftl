<!DOCTYPE html>
<html>
	<head>
		<script type="text/javascript" src="https://cdn.boldbi.com/embedded-sdk/latest/boldbi-embed.js"></script>
		<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
		<script type="text/javascript" src="/bootstrap/js/boldbi-js/Index.js"></script>
		<script type="text/javascript">
			var rootUrl = "https://intelekt.groupfio.com/bi";
			var dashboardId = "79edfbb4-7441-400f-8a55-1e2a277d34e2";
			var siteIdentifier = "site/woodwaredemo";
			var environment = "onpremise";
			var embedType = "component";
			var authorizationServerUrl = "https://groupfio-sample.boldbidemo.com/AuthorizationServer";
			var getDashboardsUrl = "/GetDashboards";
		</script>
		<style>
			[role="listbox"]:not(.ag-list):not(.ag-select-list) {
			min-height: 0em;
			}
			#dashboard_embeddedbi {
			height: 1000px;
			}
			.dropdown-div {
			display: inline-block;
			vertical-align: top;
			margin-right: 10px;
			margin-top: 15px;
			margin-left: 10px;
			}
			.view-btn, .view-dsbrd-btn {
			display: inline-block;
			vertical-align: top;
			height: 24px;
			border-radius: 5px;
			padding: 2px;
			margin-top: 12px;
			font-size: 14px;
			margin-left: -5px;
			}
			.view-dsbrd-link {
			margin-top: 16px;
			padding-bottom: 0px;
			}
			#dropDowm_row .ui.search.dropdown .menu{
			max-height:80px  !important;
			}
			.form-group {
			margin-bottom: 5.3rem !important;
			}
		</style>
	</head>
	<br><br><br><br>
	<body onload="renderDashboardLinking()">
		<div class="row" style="width:100%; height:1080px;">
			<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
				<#assign groupName = "${groupName!}">
				<input type="hidden" value="${groupName!}" id="groupName"/>
				<input type="hidden" value="${authorizationServerUrl!}" id="authorizationServerUrl"/>
				<#--
				<div class="form-group row" id="dropDowm_row">
					<label class="col-sm-1 field-text" style="font-weight:bold;padding: 2px;margin-top: 16px;margin-left: 14px;" for="sections">Company Name </label>
					<div class="col-sm-2" style=" margin-top: 11px;">
						<select name="filterParameters" id="filterParameters" onChange="" class="ui dropdown search form-control fluid show-tick">
							<
							<option value="BUILDERS HARDWARE">BUILDERS HARDWARE</option>
							<#if partyGroupList?has_content>
							<#list partyGroupList as partyGroups>
							<option value="${partyGroups.groupName!}">${partyGroups.groupName!}</option>
							</#list>
							</#if>
							<option value="HARDWARE CONSULTANTS LLC">HARDWARE CONSULTANTS LLC</option>
						</select>
						<div class="help-block with-errors" id="sections_error"></div>
					</div>
				</div>
				<div class="view-btn">
					<input type="button" onclick="renderFilterParameter()" class="btn btn-primary btn-xs ml-2 btn-style-view" value="Filter" />
				</div>
				<div class="view-dsbrd-link view-dsbrd-btn" style="margin-left: 3px;">
					<a href="dashboardConfiguration">Home page</a>
				</div>
				<br><br><br><br>-->
				<div id="viewer-section" style="width:100%;">
					<div id="dashboard"></div>
					<div id="dashboard-dialog"></div>
				</div>
			</div>
		</div>
		<!-- Visual Studio Browser Link -->
		<script type="text/javascript" src="/_vs/browserLink" async="async" id="__browserLink_initializationData" data-requestId="07f86da08cbb4a5cbdc93de636611fa0" data-requestMappingFromServer="false" data-connectUrl="http://localhost:63260/a15f7df637e44c11885e85455852b991/browserLink"></script>
		<!-- End Browser Link -->
		<script src="/_framework/aspnetcore-browser-refresh.js"></script>
	</body>
</html>