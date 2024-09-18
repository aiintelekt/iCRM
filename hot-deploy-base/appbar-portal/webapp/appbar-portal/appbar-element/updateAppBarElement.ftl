<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://homeapps/webapp/homeapps/user-audit/audit_page_header.ftl"/>
<div class="row">
    <div id="main" role="main">
        <#assign extra='<a href="viewAppBar?appBarId=${inputContext?if_exists.appBarId!}&appBarTypeId=${inputContext?if_exists.appBarTypeId!}&appBarElementId=${inputContext?if_exists.appBarElementId!}" class="btn btn-xs btn-primary"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>'/>
        <#assign extraLeft=''/>
        <#assign title="${uiLabelMap.UpdateAppBarElement!}" />
        <#if appBarName?exists>
        	<#assign title= title + "- ${appBarName?if_exists!}" />
        </#if>
        <form name="updateAppBarElementForm" id="updateAppBarElementForm" action="updateAppBarElementService" method="post" data-toggle="validator">
            <#include "component://homeapps/webapp/homeapps/user-audit/audit_from_header.ftl"/>
            <@inputHidden 
	        	id="appBarId"
	        	value="${inputContext?if_exists.appBarId!}"
	        	/>
	        <@inputHidden 
	        	id="appBarTypeId"
	        	value="${inputContext?if_exists.appBarTypeId!}"
	        	/>
            <div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
		        <@sectionFrameHeader 
			        title=title
			        extra=extra?if_exists
			        />
				<@dynaScreen 
					instanceId="APP_BAR_ELEMENT_STP"
					modeOfAction="UPDATE"
					/>
                <div class="clearfix"></div>
                <div class="form-group offset-2">
                    <div class="text-left ml-1 p-2">
                    	<@submit
                            label="${uiLabelMap.Update!}"
                            />
                        <@cancel
                            label="${uiLabelMap.Cancel!}"
                            onclick="viewAppBar?appBarId=${inputContext?if_exists.appBarId!}&appBarTypeId=${inputContext?if_exists.appBarTypeId!}&appBarElementId=${inputContext?if_exists.appBarElementId!}"
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