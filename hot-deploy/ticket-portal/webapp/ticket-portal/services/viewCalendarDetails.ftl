<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<link rel='stylesheet' type='text/css'
	href='/ticket-portal-resource/js/calendar/fullcalendar.min.css' />
<script type='text/javascript'
	src='/ticket-portal-resource/js/calendar/moment.min.js'></script>
<#-- <script type='text/javascript'
	src='/ticket-portal-resource/js/calendar/jquery.min.js'></script>
<script type='text/javascript'
	src="/ticket-portal-resource/js/calendar/jquery-ui.custom.min.js"></script>  -->
<script type='text/javascript'
	src='/ticket-portal-resource/js/calendar/fullcalendar.min.js'></script>
<script type='text/javascript'
	src='/ticket-portal-resource/js/calendar/viewCalendar.js'></script>

<div class="row">
	<div id="main" role="main">
		<div class="col-lg-12 col-md-12 col-sm-12">
			<form action="myActivities" method="post" id="myActivities"
				name="myActivities"><@inputHidden id="userLoggedIn"
				value="${userLogin.userLoginId?if_exists}"/></form>
			<div class="top-band bg-light">
				<div class="col-lg-12 col-md-12 col-sm-12">
					<div class="row">
						<div class="col-lg-12 col-md-12 col-sm-12">
							<h1 class="float-left mr-2 mb-0">Calendar</h1>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="col-lg-12 col-md-12 col-sm-12">
			<span class=" row page-header mb-4">
				<h2 class="float-left">
					My Calendar <a class="text-dark" href="" id="dropdown05"
						data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"><i
						class="fa fa-angle-down" aria-hidden="true"></i></a>
					<div class="dropdown-menu" aria-labelledby="dropdown05">
						<h4>System Views</h4>
						<a class="dropdown-item" id="myCalendar" href=""> My Calendar
						</a> <a class="dropdown-item" id="myTeamCalendar" href=""> My Team
							Calendar </a>
					</div>
				</h2>
			</span>
		</div>
		<div class="clearfix"></div>
		<div class="col-lg-12 col-md-12 col-sm-12" id="calendar"></div>
	</div>
</div>
