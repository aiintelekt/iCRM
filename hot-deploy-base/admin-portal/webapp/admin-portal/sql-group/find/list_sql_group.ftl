<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>

<div class="row" style="width:100%">
	
<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">

<#assign rightContent='<a title="Create" href="/admin-portal/control/createSqlGroup" target="_blank" class="btn btn-primary btn-xs ml-2"> <i class="fa fa-plus" aria-hidden="true"></i> Create </a>' 
/>   

<#assign rightContent='
		<button id="item-refresh-btn" type="button" class="btn btn-xs btn-primary m5"><i class="fa fa-refresh" aria-hidden="true"></i></button>
		' />
	<#assign rightContent = rightContent+ '<a title="Create" href="/admin-portal/control/createSqlGroup" target="_blank" class="btn btn-primary btn-xs ml-2"> <i class="fa fa-plus" aria-hidden="true"></i> Create </a>' />
	<#assign rightContent = rightContent+ '<span id="execute-item-btn" data-toggle="confirmation" data-original-title="Are you sure to EXECUTE ?" class="btn btn-primary btn-xs ml-2 " ><i class="fa fa-cog" aria-hidden="true"></i> Execute </span>' />
  
<@fioGrid
	id="sql-group"
	instanceId="SQLGRP_LIST"
	jsLoc="/admin-portal-resource/js/ag-grid/sql-group/find-sql-group.js"
	headerLabel=uiLabelMap.ListOfSqlGroups!
	headerExtra=rightContent!
	headerBarClass="grid-header-no-bar"
	headerId="sql-group-tle"
	savePrefBtnId="sql-group-save-pref"
	clearFilterBtnId="sql-group-clear-pref"
	subFltrClearId="sql-group-clear-sub-ftr"
	serversidepaginate=false
	statusBar=false
	exportBtn=true
	exportBtnId="sql-group-list-export-btn"
	savePrefBtn=false
	clearFilterBtn=false
	subFltrClearBtn=false
	/>
</div>
  	
</div>

<script>

$(document).ready(function() {

});

</script>