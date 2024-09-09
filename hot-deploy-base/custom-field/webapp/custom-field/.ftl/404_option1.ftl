<#include "component://lockbox-manager/webapp/lockbox-manager/lib/portalContentMacros.ftl"/>
<link href="/fd_css/style.min.css" rel="stylesheet" type="text/css"/>

<div class="page-head">
	<!-- BEGIN PAGE TITLE -->
	<div class="page-title">
		<h1>404 Page Option 1 <small>404 page option 1</small></h1>
	</div>
	<!-- END PAGE TITLE -->
	<!-- BEGIN PAGE TOOLBAR -->
	<div class="page-toolbar">
		<!-- BEGIN THEME PANEL -->
		<div class="btn-group btn-theme-panel">
			<a href="javascript:;" class="btn dropdown-toggle" data-toggle="dropdown">
			<i class="icon-settings"></i>
			</a>
			<div class="dropdown-menu theme-panel pull-right dropdown-custom hold-on-click">
				<div class="row">
					<div class="col-md-4 col-sm-4 col-xs-12">
						<h3>THEME</h3>
						<ul class="theme-colors">
							<li class="theme-color theme-color-default active" data-theme="default">
								<span class="theme-color-view"></span>
								<span class="theme-color-name">Dark Header</span>
							</li>
							<li class="theme-color theme-color-light" data-theme="light">
								<span class="theme-color-view"></span>
								<span class="theme-color-name">Light Header</span>
							</li>
						</ul>
					</div>
					<div class="col-md-8 col-sm-8 col-xs-12 seperator">
						<h3>LAYOUT</h3>
						<ul class="theme-settings">
							<li>
								 Theme Style
								<select class="layout-style-option form-control input-small input-sm">
									<option value="square" selected="selected">Square corners</option>
									<option value="rounded">Rounded corners</option>
								</select>
							</li>
							<li>
								 Layout
								<select class="layout-option form-control input-small input-sm">
									<option value="fluid" selected="selected">Fluid</option>
									<option value="boxed">Boxed</option>
								</select>
							</li>
							<li>
								 Header
								<select class="page-header-option form-control input-small input-sm">
									<option value="fixed" selected="selected">Fixed</option>
									<option value="default">Default</option>
								</select>
							</li>
							<li>
								 Top Dropdowns
								<select class="page-header-top-dropdown-style-option form-control input-small input-sm">
									<option value="light">Light</option>
									<option value="dark" selected="selected">Dark</option>
								</select>
							</li>
							<li>
								 Sidebar Mode
								<select class="sidebar-option form-control input-small input-sm">
									<option value="fixed">Fixed</option>
									<option value="default" selected="selected">Default</option>
								</select>
							</li>
							<li>
								 Sidebar Menu
								<select class="sidebar-menu-option form-control input-small input-sm">
									<option value="accordion" selected="selected">Accordion</option>
									<option value="hover">Hover</option>
								</select>
							</li>
							<li>
								 Sidebar Position
								<select class="sidebar-pos-option form-control input-small input-sm">
									<option value="left" selected="selected">Left</option>
									<option value="right">Right</option>
								</select>
							</li>
							<li>
								 Footer
								<select class="page-footer-option form-control input-small input-sm">
									<option value="fixed">Fixed</option>
									<option value="default" selected="selected">Default</option>
								</select>
							</li>
						</ul>
					</div>
				</div>
			</div>
		</div>
		<!-- END THEME PANEL -->
	</div>
	<!-- END PAGE TOOLBAR -->
</div>

<!-- BEGIN PAGE BREADCRUMB -->
<ul class="page-breadcrumb breadcrumb">
	<li>
		<a href="#">Error Page</a>
		<i class="fa fa-circle"></i>
	</li>
	<li>
		<a href="<@ofbizUrl>404Option1</@ofbizUrl>">404 Page Option 1</a>
	</li>
</ul>
<!-- END PAGE BREADCRUMB -->

<!-- BEGIN PAGE CONTENT-->
<div class="row">
	<div class="col-md-12 page-404">
		<div class="number">
			 404
		</div>
		<div class="details">
			<h3>Oops! You're lost.</h3>
			<p>
				 We can not find the page you're looking for.<br/>
				<a href="index.html">
				Return home </a>
				or try the search bar below.
			</p>
			<form action="#">
				<div class="input-group input-medium">
					<input type="text" class="form-control" placeholder="keyword...">
					<span class="input-group-btn">
					<button type="submit" class="btn blue"><i class="fa fa-search"></i></button>
					</span>
				</div>
				<!-- /input-group -->
			</form>
		</div>
	</div>
</div>
<!-- END PAGE CONTENT-->