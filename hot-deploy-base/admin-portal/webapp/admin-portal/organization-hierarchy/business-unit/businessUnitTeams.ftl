<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl" />

<div class=" pt-2">
	<div class="clearfix"></div>
</div>
<div class="">
	<div class="loader text-center" id="loader" sytle="display:none;">
		<span></span>
		<span></span>
		<span></span>
	</div>
	<div id="grid1" style="width: 100%;" class="ag-theme-balham"></div>
	<#--<@AgGrid
			userid="${userLogin.userLoginId}"
			instanceid="BU03"
			styledimensions='{"width":"100%","height":"80vh"}'
			autosave="false"
			autosizeallcol="true"
			debug="true"
			requestbody='{"productStoreGroupId":"${productStoreGroupId!}"}'
			/> -->
	<script type="text/javascript" src="/admin-portal-resource/js/ag-grid/org-Hierarchy/bu-teams.js"></script>
</div>
<div class="clearfix"></div>
