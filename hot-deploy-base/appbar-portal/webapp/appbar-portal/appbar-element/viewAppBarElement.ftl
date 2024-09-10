<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://homeapps/webapp/homeapps/user-audit/audit_page_header.ftl"/>
<div class="row">
    <div id="main" role="main">
		<#assign extra='<a href="updateAppBarElement?appBarId=${inputContext?if_exists.appBarId!}&appBarTypeId=${inputContext?if_exists.appBarTypeId!}&appBarElementId=${inputContext?if_exists.appBarElementId!}" class="btn btn-xs btn-primary"><i class="fa fa-edit" aria-hidden="true"></i> Update</a><a href="viewAppBar?appBarId=${inputContext?if_exists.appBarId!}&appBarTypeId=${inputContext?if_exists.appBarTypeId!}" class="btn btn-xs btn-primary"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>'/>
	    <#assign extraLeft=''/>
	    <@sectionFrameHeader 
	    	title="${uiLabelMap.ViewAppBarElement!}"
	        extra=extra?if_exists
	        extraLeft=extraLeft
	        />
		<div class="col-lg-12 col-md-12 col-sm-12">
		    <@dynaScreen 
		        instanceId="APP_BAR_ELEMENT_STP"
		        modeOfAction="VIEW"
		        />
	    </div>
    </div>
</div>

<script></script>
<script type="text/javascript" src="/appbar-portal-resource/js/ag-grid/app-bar-elements.js"></script>
