<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<div class="row">
    <div id="main" role="main">
        <#assign extra='<a href="cloneAgGridInstance?instanceId=${gridUserPreferences?if_exists.instanceId!}&userId=${gridUserPreferences?if_exists.userId!}&role=${gridUserPreferences?if_exists.role!}" class="btn btn-xs btn-primary"><i class="fa fa-clone" aria-hidden="true"></i> Clone</a><a href="editAgGrid?instanceId=${gridUserPreferences?if_exists.instanceId!}&userId=${gridUserPreferences?if_exists.userId!}&role=${gridUserPreferences?if_exists.role!}" class="btn btn-xs btn-primary"><i class="fa fa-edit" aria-hidden="true"></i> Update</a><a href="findAgGridConfig" class="btn btn-xs btn-primary"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>'/>
        <#assign extraLeft=''/>

        <div class="col-md-12 col-lg-12 col-sm-12 dash-panel">
	        <@sectionFrameHeaderTab title="${uiLabelMap.ViewAgGrid!}" tabId="ViewAgGrid" extra=extra?if_exists  extraLeft=extraLeft?if_exists/> 
            <@dynaScreen 
            instanceId="AG_GRID_INSTANCE"
            modeOfAction="VIEW"
            />
            <div class="clearfix"></div>
        </div>
    </div>
</div>