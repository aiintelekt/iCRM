<#include "component://homeapps/webapp/homeapps/lib/ofbizFormMacros.ftl"/>
<!-- BEGIN SIDEBAR -->
<div class="page-sidebar-wrapper">
	<!-- DOC: Set data-auto-scroll="false" to disable the sidebar from auto scrolling/focusing -->
	<!-- DOC: Change data-auto-speed="200" to adjust the sub menu slide up/down speed -->
	<div class="page-sidebar md-shadow-z-2-i  navbar-collapse collapse">
		<!-- BEGIN SIDEBAR MENU -->
		<!-- DOC: Apply "page-sidebar-menu-light" class right after "page-sidebar-menu" to enable light sidebar menu style(without borders) -->
		<!-- DOC: Apply "page-sidebar-menu-hover-submenu" class right after "page-sidebar-menu" to enable hoverable(hover vs accordion) sub menu mode -->
		<!-- DOC: Apply "page-sidebar-menu-closed" class right after "page-sidebar-menu" to collapse("page-sidebar-closed" class must be applied to the body element) the sidebar sub menu mode -->
		<!-- DOC: Set data-auto-scroll="false" to disable the sidebar from auto scrolling/focusing -->
		<!-- DOC: Set data-keep-expand="true" to keep the submenues expanded -->
		<!-- DOC: Set data-auto-speed="200" to adjust the sub menu slide up/down speed -->
		<ul class="page-sidebar-menu " data-keep-expanded="false" data-auto-scroll="true" data-slide-speed="200">
			
			<li class="start">
				<a href="<@ofbizUrl>main</@ofbizUrl>">
				<i class="icon-home"></i>
				<span class="title">Dashboard</span>
				</a>
			</li>
			
			<li>
				<a href="javascript:;">
				<i class="icon-settings"></i>
				<span class="title">Widget</span>
				<span class="arrow"></span>
				</a>
				<ul class="sub-menu">
					<@accordionItemLink url="newScreenLayout" text="Create Screen" iconClass="icon-home"/>
					<@accordionItemLink url="listScreenLayout" text="List Screen" iconClass="icon-home"/>
				</ul>
			</li>
			
			<li>
				<a href="javascript:;">
				<i class="icon-settings"></i>
				<span class="title">General</span>
				<span class="arrow"></span>
				</a>
				<ul class="sub-menu">
					<@accordionItemLink url="generalComponents" text="General Components" iconClass="icon-home"/>
					<@accordionItemLink url="tabs" text="Tabs" iconClass="icon-home"/>
					<@accordionItemLink url="accordions" text="Accordions" iconClass="icon-home"/>
					<@accordionItemLink url="notes" text="Notes" iconClass="icon-home"/>
					<@accordionItemLink url="modals" text="Modals" iconClass="icon-home"/>
				</ul>
			</li>
			
			<li>
				<a href="javascript:;">
				<i class="icon-settings"></i>
				<span class="title">Error Page</span>
				<span class="arrow"></span>
				</a>
				<ul class="sub-menu">
					<@accordionItemLink url="404Option1" text="404 Option 1" iconClass="icon-home"/>
					<@accordionItemLink url="500Option1" text="500 Option 1" iconClass="icon-home"/>
				</ul>
			</li>
			
			<li>
				<a href="javascript:;">
				<i class="icon-settings"></i>
				<span class="title">Data Tables</span>
				<span class="arrow"></span>
				</a>
				<ul class="sub-menu">
					<@accordionItemLink url="ajaxDataTables" text="Ajax Data Tables" iconClass="icon-home"/>
				</ul>
			</li>
			
			<li>
				<a href="javascript:;">
				<i class="icon-settings"></i>
				<span class="title">Portlets</span>
				<span class="arrow"></span>
				</a>
				<ul class="sub-menu">
					<@accordionItemLink url="draggablePortlets" text="Draggable Portlets 3 Col" iconClass="icon-home"/>
					<@accordionItemLink url="draggablePortlets2Col" text="Draggable Portlets 2 Col" iconClass="icon-home"/>
					<@accordionItemLink url="draggablePortlets1Col" text="Draggable Portlets 1 Col" iconClass="icon-home"/>
				</ul>
			</li>
						
		</ul>
		<!-- END SIDEBAR MENU -->
	</div>
</div>
<!-- END SIDEBAR -->

<script type="text/javascript">

$('.page-sidebar-menu').find('li.active').each(function () {
	var activeLink = $(this);
	$(this).parents('li').each(function () {
		$(this).addClass( "active" );
		$(this).children('a').children('span.arrow').addClass( "open" );
	});
	
});

</script>