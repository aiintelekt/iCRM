<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
<div class="row">
	<div id="main" role="main" class="pd-btm-title-bar">
		<#-- <@sectionFrameHeader title="Find Accounts" /> -->

		<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
			<#assign extra='<a href="/admin-portal/control/findResAvail" class="btn btn-xs btn-primary">
	        <i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>' />
			<@sectionFrameHeader title="${uiLabelMap.ViewResAvail!}" extra=extra />
			<#-- Basic information -->
				<#-- <div class="card-header mt-3" id="cp">
					<@dynaScreen instanceId="ACTIVITY_BASIC" modeOfAction="VIEW" />
		</div> -->
		<#-- <div class="card-head mt-3">
			<div class="row">
				<div class="col-md-4">
					<h6>Activity : Information</h6>
					<@headerH3 title="${inputContext.workEffortId!}" />
				</div>
				<div class="col-md-8 right-details">
					<div class="bd-callout">
						<small>Activity Type</small>
						<span class="text-danger">*</span>
						<@headerH5 title="${inputContext.typeDesc!}" />
					</div>
					<div class="bd-callout">
						<small>Priority</small>
						<span class="text-danger">*</span>
						<@headerH5 title="" id="ts-priority" />
					</div>
				</div>
			</div>
			-->
			<#include "component://admin-portal/webapp/admin-portal/resource-availability/tab_menu.ftl" />
			<@navTab
				instanceId="VIEW_RES_AVAIL"
				activeTabId="details"
				/>
</div>
</div>
</div>

<script>
	$(document).ready(function() {

	});
</script>