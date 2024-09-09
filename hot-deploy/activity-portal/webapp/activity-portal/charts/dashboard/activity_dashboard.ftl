<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>

<div class="row">
    <div id="main" role="main">
</div>
</div>
<div class="col-lg-12 col-md-12 col-sm-12 mt-3 camp-dash">
<@sectionFrameHeader title="Activity Dashboard" />
	<div class="row">
			<div class="col-xl-4 col-lg-4 col-md-12 col-sm-12">
				${screens.render("component://activity-portal/widget/dashboard/DashboardScreens.xml#OutstandingTask")}
			</div>
			<div class="col-xl-4 col-lg-4 col-md-12 col-sm-12">
				${screens.render("component://activity-portal/widget/dashboard/DashboardScreens.xml#TaskByDay")}
			</div> 
			<div class="col-xl-4 col-lg-4 col-md-12 col-sm-12">
				${screens.render("component://activity-portal/widget/dashboard/DashboardScreens.xml#TaskByDayEow")}
			</div>
			<div class="col-xl-4 col-lg-4 col-md-12 col-sm-12">
				${screens.render("component://activity-portal/widget/dashboard/DashboardScreens.xml#TaskByWeek")}
			</div>
			<div class="col-xl-4 col-lg-4 col-md-12 col-sm-12">
				${screens.render("component://activity-portal/widget/dashboard/DashboardScreens.xml#OutstandingActivity")}
			</div>
			<div class="col-xl-4 col-lg-4 col-md-12 col-sm-12">
				${screens.render("component://activity-portal/widget/dashboard/DashboardScreens.xml#DurationActivity")}
			</div>
		</div>
</div>

<script type="text/javascript">

$(document).ready(function() {


});

</script>

