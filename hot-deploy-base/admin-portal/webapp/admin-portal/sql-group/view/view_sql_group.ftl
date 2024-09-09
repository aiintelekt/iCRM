<#include "component://bootstrap/lib/ofbizFormMacros.ftl" />
<div class="row">
	<div id="main" role="main" class="pd-btm-title-bar">
		<#-- <@sectionFrameHeader title="Find Accounts" /> -->
		<div class="col-lg-12 col-md-12 col-sm-12">

			<div class="card-head margin-adj mt-2" id="view-detail">
				<div class="col-lg-12 col-md-12 dot-line">
					<div class="row">
						<div class="col-lg-6 col-md-6">
							<h3 class="float-left mr-2 mb-0 header-title view-title">SqlGroup Details</h3>
						</div>
						<div class="col-lg-6 col-md-6">
							<a href="/admin-portal/control/findTemplate" class="btn btn-xs btn-primary float-right text-right">Back</a>
						</div>
					</div>
				</div>
				<#-- <@AppBar appBarId="ACTION_APP_BAR" appBarTypeId="ACTION" id="appbar1" extra=extra! toggleDropDownData=toggleDropDownData! isEnableUserPreference=true /> -->
			</div>
		
			<#-- Basic information -->
			<#-- <div class="basic-info mt-3" id="cp">
				<div class="row	">
					<div class="col-lg-12 col-md-12">
						<h3 class="float-left mr-2 mb-0 header-title view-title">Basic Information</h3>
					</div>
				</div>
				<@dynaScreen
					instanceId="SQLGROUP_BASIC_INFO"
					modeOfAction="VIEW"
					/>
			</div> -->

			<#-- <#include "component://admin-portal/webapp/admin-portal/template/tab_menu.ftl" /> -->
			<@navTab
				instanceId="VIEW_SQLGROUP"
				activeTabId="a-details"
				/>
</div>
</div>
</div>
${screens.render("component://common-portal/widget/common/CommonScreens.xml#AddCommonFeatures")}

<script>
$(document).ready(function() {

});
</script>