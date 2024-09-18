<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://homeapps/webapp/homeapps/user-audit/audit_page_header.ftl"/>
<div class="row">
    <div id="main" role="main">
        <#assign extra='<a href="viewAppBar?appBarId=${inputContext?if_exists.appBarId!}&appBarTypeId=${inputContext?if_exists.appBarTypeId!}" class="btn btn-xs btn-primary"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>'/>
        <#assign extraLeft=''/>
        

        <form name="updateAppBarForm" id="updateAppBarForm" action="updateAppBarService" method="post" data-toggle="validator" onsubmit="javascript:return validation();">
            <#include "component://homeapps/webapp/homeapps/user-audit/audit_from_header.ftl"/>
            <div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
	        <@sectionFrameHeaderTab title="${uiLabelMap.UpdateAppBar!}" tabId="updateAppBar" extra=extra?if_exists/> 
				<@dynaScreen 
					instanceId="APP_BAR_STP"
					modeOfAction="UPDATE"
					/>
                <div class="clearfix"></div>
                <div class="form-group offset-2">
                    <div class="text-left ml-1 pad-10">
                    	<@submit
                            label="${uiLabelMap.Update!}"
                            />
                        <@cancel
                            label="${uiLabelMap.Cancel!}"
                            onclick="viewAppBar?appBarId=${inputContext?if_exists.appBarId!}&appBarTypeId=${inputContext?if_exists.appBarTypeId!}"
                            />
                    </div>
                </div>
            </div>
        </form>
    </div>
    <#-- main end -->
</div>
<#-- row end-->
<#include "component://appbar-portal/webapp/appbar-portal/configuration/uiLablePicker.ftl" />