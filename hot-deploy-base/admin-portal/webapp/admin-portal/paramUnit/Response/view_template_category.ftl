<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>

<div class="row">
	<div id="main" role="main" class="pd-btm-title-bar">
		<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
			<@sectionFrameHeaderTab title="View Template Category" tabId="ViewTemplateCategory"/>
			<#include "component://admin-portal/webapp/admin-portal/paramUnit/Response/tab_menu.ftl"/>
			<@navTab
				instanceId="VIEW_TEMP_CATEGORY"
				activeTabId="details"
				/>
			</div>
		</div>
	</div>
</div>
