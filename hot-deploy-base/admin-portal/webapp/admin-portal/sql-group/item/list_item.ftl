<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>

<div class="row">
<#assign helpUrl = Static["org.groupfio.common.portal.util.DataHelper"].getHelpUrl(delegator, request.getRequestURI(), "apv-tpl-items") />
<div class="col-lg-12 col-md-12 col-sm-12">
		
	<#assign rightContent='
		<button id="item-refresh-btn" type="button" class="btn btn-xs btn-primary m5"><i class="fa fa-refresh" aria-hidden="true"></i></button>
		' />
	<#assign rightContent = rightContent+ '<span id="create-item-btn" title="Create" class="btn btn-primary btn-xs ml-2 " ><i class="fa fa-plus" aria-hidden="true"></i> Create </span>' />
	<#assign rightContent = rightContent+ '<span id="execute-item-btn" data-toggle="confirmation" data-original-title="Are you sure to EXECUTE ?" class="btn btn-primary btn-xs ml-2 " ><i class="fa fa-cog" aria-hidden="true"></i> Execute </span>' />
	<#assign rightContent = rightContent+ '<span id="items-remove-btn" data-toggle="confirmation" data-original-title="Are you sure to REMOVE ?" class="btn btn-primary btn-xs ml-2 " ><i class="fa fa-remove" aria-hidden="true"></i> Remove </span>' />
    
    <#assign hasPermission = Static["org.fio.homeapps.util.DataUtil"].hasPermission(request, "CREATE_OPERATION")?if_exists />
		
  	<@fioGrid
		id="sql-group-item-list"
		instanceId="SQLGRP_ITEM_LIST"
		jsLoc="/admin-portal-resource/js/ag-grid/sql-group/find-item.js"
		headerLabel="Items"
		headerExtra=rightContent!
		headerBarClass="grid-header-no-bar"
		headerId="sql-group-item-list-tle"
		savePrefBtnId="sql-group-item-list-save-pref"
		clearFilterBtnId="sql-group-item-list-clear-pref"
		subFltrClearId="sql-group-item-list-clear-sub-ftr"
		serversidepaginate=false
		statusBar=false
		exportBtn=true
		exportBtnId="sql-group-item-list-export-btn"
		savePrefBtn=false
		clearFilterBtn=false
		subFltrClearBtn=false
		/>
</div>
  	
</div>

<script>

</script>