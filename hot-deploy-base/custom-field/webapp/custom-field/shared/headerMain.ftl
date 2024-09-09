<!-- BEGIN HEADER -->

<link href="/metronic/css/layout.css" rel="stylesheet" type="text/css"/>

<div class="page-header md-shadow-z-1-i navbar navbar-fixed-top">
	<!-- BEGIN HEADER INNER -->
	<div class="page-header-inner">
		<!-- BEGIN LOGO -->
		<div class="page-logo">
			<a href="#">
			<img src="/metronic/img/logo.png" alt="logo" class="logo-default"/>
			</a>
			
			<!-- DOC: Remove the above "hide" to enable the sidebar toggler button on header -->
			<#--<div class="menu-toggler sidebar-toggler <#if isDashboard?exists && isDashboard>hide</#if>">
			</div>-->
			
			<div class="menu-toggler dropdown-toggle" data-toggle="dropdown">
			</div>
			
			<#--
			<a href="javascript:;" class="dropdown-toggle" data-toggle="dropdown">
				<img class="" alt="announcement" src="/metronic/images/announcement.png">
			</a>
			-->
			
			<ul class="dropdown-menu" role="menu">
			<li>
			<div class="portlet box blue">
											
				<div class="portlet-body">
										
					<a class="icon-btn" href="#">
					<i class="fa fa-bar-chart-o"></i>
					<div>
						 Dashboard
					</div>
					</a>
										
					<a class="icon-btn" href="#">
					<i class="icon-settings"></i>
					<div>
						 Widgets
					</div>
					</a>
					
					<a class="icon-btn" href="#">
					<i class="fa fa-calendar"></i>
					<div>
						 Time Sheet
					</div>
					</a>
					<a class="icon-btn" href="javascript:;">
					<i class="fa fa-envelope"></i>
					<div>
						 Inbox
					</div>
					<span class="badge badge-info">
					4 </span>
					</a>
					<a class="icon-btn" href="javascript:;">
					<i class="fa fa-bullhorn"></i>
					<div>
						 Notification
					</div>
					<span class="badge badge-danger">
					3 </span>
					</a>
					
					<#--
					<a class="icon-btn" href="javascript:;">
					<i class="fa fa-group"></i>
					<div>
						 Users
					</div>
					<span class="badge badge-danger">
					2 </span>
					</a>
					<a class="icon-btn" href="javascript:;">
					<i class="fa fa-barcode"></i>
					<div>
						 Products
					</div>
					<span class="badge badge-success">
					4 </span>
					</a>
					<a class="icon-btn" href="javascript:;">
					<i class="fa fa-sitemap"></i>
					<div>
						 Categories
					</div>
					</a>
					<a class="icon-btn" href="javascript:;">
					<i class="fa fa-map-marker"></i>
					<div>
						 Locations
					</div>
					</a>
					<a class="icon-btn" href="javascript:;">
					<i class="fa fa-money"><i></i></i>
					<div>
						 Finance
					</div>
					</a>
					<a class="icon-btn" href="javascript:;">
					<i class="fa fa-plane"></i>
					<div>
						 Projects
					</div>
					<span class="badge badge-info">
					21 </span>
					</a>
					<a class="icon-btn" href="javascript:;">
					<i class="fa fa-thumbs-up"></i>
					<div>
						 Feedback
					</div>
					<span class="badge badge-info">
					2 </span>
					</a>
					<a class="icon-btn" href="javascript:;">
					<i class="fa fa-cloud"></i>
					<div>
						 Servers
					</div>
					<span class="badge badge-danger">
					2 </span>
					</a>
					<a class="icon-btn" href="javascript:;">
					<i class="fa fa-globe"></i>
					<div>
						 Regions
					</div>
					</a>
					<a class="icon-btn" href="javascript:;">
					<i class="fa fa-heart-o"></i>
					<div>
						 Popularity
					</div>
					<span class="badge badge-info">
					221 </span>
					</a>
					-->
					
				</div>
			</div>
			</li>
			</ul>
			
		</div>
		<!-- END LOGO -->
		<!-- BEGIN RESPONSIVE MENU TOGGLER -->
		<a href="javascript:;" class="menu-toggler responsive-toggler hidden-sm hidden-xs" data-toggle="collapse" data-target=".navbar-collapse">
		</a>
		<!-- END RESPONSIVE MENU TOGGLER -->
		<!-- BEGIN PAGE ACTIONS -->
		<!-- DOC: Remove "hide" class to enable the page header actions -->
		<div class="page-actions">
			<div class="btn-group">
				
				<#--<button type="button" class="btn red-haze btn-sm dropdown-toggle" data-toggle="dropdown" data-hover="dropdown" data-close-others="true">
				<span class="hidden-sm hidden-xs">Project kap2a&nbsp;</span><i class="fa fa-angle-down"></i>
				</button>-->
				
				<select class="bs-select form-control" data-style="red-haze" data-show-subtext="true" id="dashboardId">
					<option value="">Select Dashboard</option>
					
				</select>
				
				<#--
				<button type="button" class="btn red-haze btn-sm dropdown-toggle" data-toggle="dropdown">
				<span class="hidden-sm hidden-xs">Project kap2a&nbsp;</span><i class="fa fa-angle-down"></i>
				</button>
				
				<ul class="dropdown-menu" role="menu">
					<li>
						<a href="javascript:;">
						<i class="icon-docs"></i> New Post </a>
					</li>
					<li>
						<a href="javascript:;">
						<i class="icon-tag"></i> New Comment </a>
					</li>
					<li>
						<a href="javascript:;">
						<i class="icon-share"></i> Share </a>
					</li>
					<li class="divider">
					</li>
					<li>
						<a href="javascript:;">
						<i class="icon-flag"></i> Comments <span class="badge badge-success">4</span>
						</a>
					</li>
					<li>
						<a href="javascript:;">
						<i class="icon-users"></i> Feedbacks <span class="badge badge-danger">2</span>
						</a>
					</li>
				</ul>
				-->
				
			</div>
			
			<div class="btn-group">
				
				<a href="javascript:;" class="dropdown-toggle" data-toggle="dropdown">
					<img class="" alt="announcement" src="/metronic/images/announcement.png">
				</a>
				
				<ul class="dropdown-menu hold-on-click">
					<form action="#" class="form-horizontal" role="form">
					<li>
						<div style="padding:10px;width: 300px;">
							<div class="input-group">
								<select class="form-control input-sm hold-on-click">
									<option>General Announcement</option>
									<option>Weather Announcement</option>
									<option>Site Emergency</option>
									<option>Company Announcement</option>
								</select>
								<#--<select class="bs-select form-control input-sm" data-show-subtext="true">
									<option data-icon="fa-glass icon-success">General Announcement</option>
									<option data-icon="fa-heart icon-danger">Weather Announcement</option>
									<option data-icon="fa-film icon-warning">Site Emergency</option>
									<option data-icon="fa-home icon-info">Company Announcement</option>
								</select>-->
							</div>
						</div>
					</li>
					<li>
						<div style="padding:10px;width: 300px;">
							<div class="input-group">
								<div class="input-icon">
									<input type="text" placeholder="Broadcast Team" name="password" class="form-control" id="newpassword">
								</div>
								<span class="input-group-btn">
								<button type="button" class="btn btn-success" id="genpassword"><i class="fa fa-send-o"></i> Send</button>
								</span>
							</div>
						</div>
					</li>
					</form>
				</ul>
				
			</div>
			
		</div>
		<!-- END PAGE ACTIONS -->
		<!-- BEGIN PAGE TOP -->
		<div class="page-top">
		
			<!-- BEGIN HEADER SEARCH BOX -->
			<!-- DOC: Apply "search-form-expanded" right after the "search-form" class to have half expanded search box -->
			<!--<form class="search-form" action="extra_search.html" method="GET">
				<div class="input-group">
					<input type="text" class="form-control input-sm" placeholder="Search..." name="query">
					<span class="input-group-btn">
					<a href="javascript:;" class="btn submit"><i class="icon-magnifier"></i></a>
					</span>
				</div>
			</form>-->
			<!-- END HEADER SEARCH BOX -->
			
			<div class="search-form" style="width: 130px;">
				<div class="input-group date date-picker" data-date-format="dd/mm/yyyy" data-date-viewmode="years">
					<input type="text" id="main-date-picker" class="form-control input-sm" placeholder="Date..." readonly value="${parameters.query!}" style="text-indent: 0; padding-left: 12px">
					<span class="input-group-btn">
					<a href="javascript:;" class="btn submit"><i class="fa fa-calendar"></i></a>
					</span>
				</div>
			</div>
			
			<#--
			<form class="search-form" action="#" method="GET">
				<div style="margin-top: 20px; max-width: 128px;" class="input-group input-medium date date-picker" data-date="12-02-2012" data-date-format="dd-mm-yyyy" data-date-viewmode="years">
					<input type="text" class="form-control" readonly style="font-size: 10px;">
					<span class="input-group-btn">
					<button class="btn default" type="button"><i class="fa fa-calendar"></i></button>
					</span>
				</div>
			</form>
			-->		
				
			<!-- BEGIN TOP NAVIGATION MENU -->
			<div class="top-menu">
				<ul class="nav navbar-nav pull-right">
					<li class="separator hide">
					</li>
					<#--
					<li>
						<div style="margin-top: 20px; max-width: 128px;" class="input-group input-medium date date-picker" data-date="12-02-2012" data-date-format="dd-mm-yyyy" data-date-viewmode="years">
							<input type="text" class="form-control" readonly style="font-size: 10px;">
							<span class="input-group-btn">
							<button class="btn default" type="button"><i class="fa fa-calendar"></i></button>
							</span>
						</div>
						
						<div class="input-group">
							<span class="input-group-btn">
							<a href="javascript:;" class="btn submit"><i class="icon-magnifier"></i></a>
							</span>
						</div>
					</li>
					-->
					<!-- BEGIN NOTIFICATION DROPDOWN -->
					<!-- DOC: Apply "dropdown-dark" class after below "dropdown-extended" to change the dropdown styte -->
					<li class="dropdown dropdown-extended dropdown-notification" id="header_notification_bar">
						<a href="javascript:;" class="dropdown-toggle" data-toggle="dropdown">
						<i class="icon-bell"></i>
						<span class="badge badge-success">
						7 </span>
						</a>
						<ul class="dropdown-menu">
							<li class="external">
								<h3>You have<span class="bold"> 7 New</span> Alerts</h3>
								<a href="#">view all</a>
							</li>
							<li>
								<ul class="dropdown-menu-list scroller" style="height: 250px;" data-handle-color="#637283">
									
									<li>
										<a href="javascript:;">
										<span class="time">1 hr</span>
										<span class="details">
										<span class="label label-sm label-icon label-danger">
										<i class="fa fa-bolt"></i>
										</span>
										Barsha - Site 6 - ESC for Timesheet </span>
										</a>
									</li>
									<li>
										<a href="javascript:;">
										<span class="time">8 hrs</span>
										<span class="details">
										<span class="label label-sm label-icon label-warning">
										<i class="fa fa-bell-o"></i>
										</span>
										Barsha - Site 6 - Security Alert </span>
										</a>
									</li>
									<li>
										<a href="javascript:;">
										<span class="time">12 hrs</span>
										<span class="details">
										<span class="label label-sm label-icon label-danger">
										<i class="fa fa-bolt"></i>
										</span>
										KFH - Site 1 - ESC for OT </span>
										</a>
									</li>
									<li>
										<a href="javascript:;">
										<span class="time">12 hrs</span>
										<span class="details">
										<span class="label label-sm label-icon label-success">
										<i class="fa fa-plus"></i>
										</span>
										KFH - Site 1 - New Badges 10 </span>
										</a>
									</li>
									<li>
										<a href="javascript:;">
										<span class="time">2 hrs</span>
										<span class="details">
										<span class="label label-sm label-icon label-info">
										<i class="fa fa-bullhorn"></i>
										</span>
										DHA - Site 2 -  Environment Alert </span>
										</a>
									</li>
									
								</ul>
							</li>
						</ul>
					</li>
					<!-- END NOTIFICATION DROPDOWN -->
					
					<li class="separator hide">
					</li>
					
					<!-- BEGIN INBOX DROPDOWN -->
					<!-- DOC: Apply "dropdown-dark" class after below "dropdown-extended" to change the dropdown styte -->
					<#--<li class="dropdown dropdown-extended dropdown-inbox" id="header_inbox_bar">
						<a href="javascript:;" class="dropdown-toggle" data-toggle="dropdown" data-hover="dropdown" data-close-others="true">
						<i class="icon-envelope-open"></i>
						<span class="badge badge-danger">
						4 </span>
						</a>
						<ul class="dropdown-menu">
							<li class="external">
								<h3>You have <span class="bold">7 New</span> Messages</h3>
								<a href="inbox.html">view all</a>
							</li>
							<li>
								<ul class="dropdown-menu-list scroller" style="height: 275px;" data-handle-color="#637283">
									<li>
										<a href="inbox.html?a=view">
										<span class="photo">
										<img src="/metronic/img/avatar/avatar2.jpg" class="img-circle" alt="">
										</span>
										<span class="subject">
										<span class="from">
										Lisa Wong </span>
										<span class="time">Just Now </span>
										</span>
										<span class="message">
										Vivamus sed auctor nibh congue nibh. auctor nibh auctor nibh... </span>
										</a>
									</li>
									<li>
										<a href="inbox.html?a=view">
										<span class="photo">
										<img src="/metronic/img/avatar/avatar3.jpg" class="img-circle" alt="">
										</span>
										<span class="subject">
										<span class="from">
										Richard Doe </span>
										<span class="time">16 mins </span>
										</span>
										<span class="message">
										Vivamus sed congue nibh auctor nibh congue nibh. auctor nibh auctor nibh... </span>
										</a>
									</li>
									<li>
										<a href="inbox.html?a=view">
										<span class="photo">
										<img src="/metronic/img/avatar/avatar1.jpg" class="img-circle" alt="">
										</span>
										<span class="subject">
										<span class="from">
										Bob Nilson </span>
										<span class="time">2 hrs </span>
										</span>
										<span class="message">
										Vivamus sed nibh auctor nibh congue nibh. auctor nibh auctor nibh... </span>
										</a>
									</li>
									<li>
										<a href="inbox.html?a=view">
										<span class="photo">
										<img src="/metronic/img/avatar/avatar2.jpg" class="img-circle" alt="">
										</span>
										<span class="subject">
										<span class="from">
										Lisa Wong </span>
										<span class="time">40 mins </span>
										</span>
										<span class="message">
										Vivamus sed auctor 40% nibh congue nibh... </span>
										</a>
									</li>
									<li>
										<a href="inbox.html?a=view">
										<span class="photo">
										<img src="/metronic/img/avatar/avatar3.jpg" class="img-circle" alt="">
										</span>
										<span class="subject">
										<span class="from">
										Richard Doe </span>
										<span class="time">46 mins </span>
										</span>
										<span class="message">
										Vivamus sed congue nibh auctor nibh congue nibh. auctor nibh auctor nibh... </span>
										</a>
									</li>
								</ul>
							</li>
						</ul>
					</li>-->
					
					<li class="dropdown dropdown-extended dropdown-inbox" id="header_inbox_bar">
						<a href="javascript:;" class="dropdown-toggle" data-toggle="dropdown">
						<i class="icon-envelope-open"></i>
						<span class="badge badge-danger">
						4 </span>
						</a>
						<ul class="dropdown-menu">
							<li class="external">
								<h3>You have <span class="bold">7 New</span> Messages</h3>
								<a href="inbox.html">view all</a>
							</li>
							<li>
								<ul class="dropdown-menu-list scroller" style="height: 275px;" data-handle-color="#637283">
									<li>
										<a href="inbox.html?a=view">
										<span class="photo">
										<img src="/metronic/img/avatar/avatar2.jpg" class="img-circle" alt="">
										</span>
										<span class="subject">
										<span class="from">
										Lisa Wong </span>
										<span class="time">Just Now </span>
										</span>
										<span class="message">
										Vivamus sed auctor nibh congue nibh. auctor nibh auctor nibh... </span>
										</a>
									</li>
									<li>
										<a href="inbox.html?a=view">
										<span class="photo">
										<img src="/metronic/img/avatar/avatar3.jpg" class="img-circle" alt="">
										</span>
										<span class="subject">
										<span class="from">
										Richard Doe </span>
										<span class="time">16 mins </span>
										</span>
										<span class="message">
										Vivamus sed congue nibh auctor nibh congue nibh. auctor nibh auctor nibh... </span>
										</a>
									</li>
									<li>
										<a href="inbox.html?a=view">
										<span class="photo">
										<img src="/metronic/img/avatar/avatar1.jpg" class="img-circle" alt="">
										</span>
										<span class="subject">
										<span class="from">
										Bob Nilson </span>
										<span class="time">2 hrs </span>
										</span>
										<span class="message">
										Vivamus sed nibh auctor nibh congue nibh. auctor nibh auctor nibh... </span>
										</a>
									</li>
									<li>
										<a href="inbox.html?a=view">
										<span class="photo">
										<img src="/metronic/img/avatar/avatar2.jpg" class="img-circle" alt="">
										</span>
										<span class="subject">
										<span class="from">
										Lisa Wong </span>
										<span class="time">40 mins </span>
										</span>
										<span class="message">
										Vivamus sed auctor 40% nibh congue nibh... </span>
										</a>
									</li>
									<li>
										<a href="inbox.html?a=view">
										<span class="photo">
										<img src="/metronic/img/avatar/avatar3.jpg" class="img-circle" alt="">
										</span>
										<span class="subject">
										<span class="from">
										Richard Doe </span>
										<span class="time">46 mins </span>
										</span>
										<span class="message">
										Vivamus sed congue nibh auctor nibh congue nibh. auctor nibh auctor nibh... </span>
										</a>
									</li>
								</ul>
							</li>
						</ul>
					</li>
					
					
					<!-- END INBOX DROPDOWN -->
					
					<li class="separator hide">
					</li>
					
					<!-- BEGIN TODO DROPDOWN -->
					<!-- DOC: Apply "dropdown-dark" class after below "dropdown-extended" to change the dropdown styte -->
					<#--<li class="dropdown dropdown-extended dropdown-tasks" id="header_task_bar">
						<a href="javascript:;" class="dropdown-toggle" data-toggle="dropdown">
						<i class="icon-calendar"></i>
						<span class="badge badge-primary">
						3 </span>
						</a>
						<ul class="dropdown-menu extended tasks">
							<li class="external">
								<h3>You have <span class="bold">12 pending</span> tasks</h3>
								<a href="page_todo.html">view all</a>
							</li>
							<li>
								<ul class="dropdown-menu-list scroller" style="height: 275px;" data-handle-color="#637283">
									<li>
										<a href="javascript:;">
										<span class="task">
										<span class="desc">Crew Setup </span>
										<span class="percent">30%</span>
										</span>
										<span class="progress">
										<span style="width: 40%;" class="progress-bar progress-bar-success" aria-valuenow="40" aria-valuemin="0" aria-valuemax="100"><span class="sr-only">40% Complete</span></span>
										</span>
										</a>
									</li>
									<li>
										<a href="javascript:;">
										<span class="task">
										<span class="desc">Crew Skills</span>
										<span class="percent">65%</span>
										</span>
										<span class="progress">
										<span style="width: 65%;" class="progress-bar progress-bar-danger" aria-valuenow="65" aria-valuemin="0" aria-valuemax="100"><span class="sr-only">65% Complete</span></span>
										</span>
										</a>
									</li>
									<li>
										<a href="javascript:;">
										<span class="task">
										<span class="desc">Crew Timesheet</span>
										<span class="percent">98%</span>
										</span>
										<span class="progress">
										<span style="width: 98%;" class="progress-bar progress-bar-success" aria-valuenow="98" aria-valuemin="0" aria-valuemax="100"><span class="sr-only">98% Complete</span></span>
										</span>
										</a>
									</li>
									<li>
										<a href="javascript:;">
										<span class="task">
										<span class="desc">Project Shifts</span>
										<span class="percent">10%</span>
										</span>
										<span class="progress">
										<span style="width: 10%;" class="progress-bar progress-bar-warning" aria-valuenow="10" aria-valuemin="0" aria-valuemax="100"><span class="sr-only">10% Complete</span></span>
										</span>
										</a>
									</li>
									<li>
										<a href="javascript:;">
										<span class="task">
										<span class="desc">Approvals,Activity Code setup</span>
										<span class="percent">58%</span>
										</span>
										<span class="progress">
										<span style="width: 58%;" class="progress-bar progress-bar-info" aria-valuenow="58" aria-valuemin="0" aria-valuemax="100"><span class="sr-only">58% Complete</span></span>
										</span>
										</a>
									</li>
									<li>
										<a href="javascript:;">
										<span class="task">
										<span class="desc">Crew Request</span>
										<span class="percent">85%</span>
										</span>
										<span class="progress">
										<span style="width: 85%;" class="progress-bar progress-bar-success" aria-valuenow="85" aria-valuemin="0" aria-valuemax="100"><span class="sr-only">85% Complete</span></span>
										</span>
										</a>
									</li>
									<li>
										<a href="javascript:;">
										<span class="task">
										<span class="desc">Project Approval</span>
										<span class="percent">38%</span>
										</span>
										<span class="progress progress-striped">
										<span style="width: 38%;" class="progress-bar progress-bar-important" aria-valuenow="18" aria-valuemin="0" aria-valuemax="100"><span class="sr-only">38% Complete</span></span>
										</span>
										</a>
									</li>
								</ul>
							</li>
						</ul>
					</li>-->
					<!-- END TODO DROPDOWN -->
					
					<!-- BEGIN USER LOGIN DROPDOWN -->
					<!-- DOC: Apply "dropdown-dark" class after below "dropdown-extended" to change the dropdown styte -->
					<li class="dropdown dropdown-user">
						<a href="javascript:;" class="dropdown-toggle" data-toggle="dropdown">
						<span class="username username-hide-on-mobile">${Request.loggedPartyName!} </span>
						<!-- DOC: Do not remove below empty space(&nbsp;) as its purposely used -->
						<img alt="" class="img-circle" src="/metronic/img/avatar/avatar9.jpg"/>
						</a>
						<ul class="dropdown-menu dropdown-menu-default">
							<li>
								<a href="#">
								<i class="icon-user"></i> My Profile </a>
							</li>
							<li>
								<a href="page_calendar.html">
								<i class="icon-calendar"></i> My Calendar </a>
							</li>
							<#--<li>
								<a href="inbox.html">
								<i class="icon-envelope-open"></i> My Inbox <span class="badge badge-danger">
								3 </span>
								</a>
							</li>
							<li>
								<a href="page_todo.html">
								<i class="icon-rocket"></i> My Tasks <span class="badge badge-success">
								7 </span>
								</a>
							</li>-->
							<li class="divider">
							</li>
							<li>
								<a href="<@ofbizUrl>lock</@ofbizUrl>">
								<i class="icon-lock"></i> Lock Screen </a>
							</li>
							<li>
								<a href="<@ofbizUrl>logout</@ofbizUrl>">
								<i class="icon-key"></i> Log Out </a>
							</li>
						</ul>
					</li>
					<!-- END USER LOGIN DROPDOWN -->
					
					<!-- BEGIN USER LOGIN DROPDOWN -->
					<li class="dropdown dropdown-extended quick-sidebar-toggler">
	                    <span class="sr-only">Toggle Quick Sidebar</span>
	                    <i class="icon-logout"></i>
	                </li>
					<!-- END USER LOGIN DROPDOWN -->
					
				</ul>
				
			</div>
			<!-- END TOP NAVIGATION MENU -->
		</div>
		<!-- END PAGE TOP -->
	</div>
	<!-- END HEADER INNER -->
</div>
<!-- END HEADER -->

<div class="clearfix">
</div>

<script type="text/javascript">

  	var ComponentsDropdowns = function () {
		
		var handleBootstrapSelect = function() {
	        $('.bs-select').selectpicker({
	            iconBase: 'fa',
	            tickIcon: 'fa-check'
	        });
	 	}
	 	       
        return {
	        //main function to initiate the module
	        init: function () {            
	            handleBootstrapSelect();
	        }
	    };
	
	}();
	
	ComponentsDropdowns.init();

</script>