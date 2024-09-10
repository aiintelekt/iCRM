<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://homeapps/webapp/homeapps/user-audit/audit_page_header.ftl"/>
<div class="row">
    <div id="main" role="main">
		<#assign extra='<a target="_blank" href="previewAppBar?appBarId=${inputContext?if_exists.appBarId!}&appBarTypeId=${inputContext?if_exists.appBarTypeId!}" title="Preview" class="btn btn-primary btn-xs ml-2 "><i class="fa fa-eye" aria-hidden="true"></i> Preview </a>
		<a href="updateAppBar?appBarId=${inputContext?if_exists.appBarId!}&appBarTypeId=${inputContext?if_exists.appBarTypeId!}" class="btn btn-xs btn-primary"><i class="fa fa-edit" aria-hidden="true"></i> Update</a><a href="findAppBar" class="btn btn-xs btn-primary"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>'/>
	    <#assign extraLeft=''/>

		<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
	        
	        <@sectionFrameHeaderTab title="${uiLabelMap.ViewAppBar!}" tabId="ViewAppBar"  extra=extra?if_exists 
	        extraLeft=extraLeft/> 
	        
		    <@dynaScreen 
		        instanceId="APP_BAR_STP"
		        modeOfAction="VIEW"
		        />
		    <div class="clearfix"></div>
		    </div>
		    
		    <div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
		    <#assign extraRight='<a title="Add Element" href="/appbar-portal/control/createAppBarElement?appBarId=${inputContext?if_exists.appBarId!}&appBarTypeId=${inputContext?if_exists.appBarTypeId!}" class="btn btn-primary btn-xs ml-2"> <i class="fa fa-plus" aria-hidden="true"></i> Add Element </a>' />
		    <#--<@AgGrid
	            userid="${userLogin.userLoginId}" 
	            shownotifications="true" 
	            instanceid="APP_BAR_ELEMENTS" 
	            autosizeallcol="true"
	            debug="false"
	            insertBtn=false
	            headerextra=extraRight!
	            gridheadertitle=uiLabelMap.ListOfElements!
	            />-->
	        <@fioGrid
				id="app-bar-elements"
				instanceId="APP_BAR_ELEMENTS"
				jsLoc="/appbar-portal-resource/js/ag-grid/app-bar-elements.js"
				headerLabel=uiLabelMap.ListOfElements!
				headerExtra=extraRight!
				headerBarClass="grid-header-no-bar"
				headerId="app-bar-elements-tle"
				savePrefBtnId="app-bar-elements-save-pref"
				clearFilterBtnId="app-bar-elements-clear-pref"
				subFltrClearId="app-bar-elements-clear-sub-ftr"
				serversidepaginate=false
				statusBar=false
				exportBtn=true
				exportBtnId="app-bar-elements-list-export-btn"
				savePrefBtn=false
				clearFilterBtn=false
				subFltrClearBtn=false
				/>
	        </div>
	    </div>
    </div>
</div>
<form name="appBarElementForm" id="appBarElementForm">
	<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
	<input type="hidden" name="userId" value="${userLogin.userLoginId?if_exists}" />
	<@inputHidden 
		id="appBarId"
		value="${requestParameters.appBarId?if_exists}"
		/>
	<@inputHidden 
		id="appBarTypeId"
		value="${requestParameters.appBarTypeId?if_exists}"
		/>
</form>

<script></script>
<#--<script type="text/javascript" src="/appbar-portal-resource/js/ag-grid/app-bar-elements.js"></script>-->