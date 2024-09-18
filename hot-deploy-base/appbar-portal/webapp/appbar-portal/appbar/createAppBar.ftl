<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://homeapps/webapp/homeapps/user-audit/audit_page_header.ftl"/>
<div class="row">
    <div id="main" role="main">
    <div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
        <@sectionFrameHeader 
	        title="${uiLabelMap.CreateAppBar}"
	        />
	    <form method="post" action="createAppBarService" id="appBarConfigForm" name="appBarConfigForm" data-toggle="validator" onsubmit="javascript:return validateForm();" >
	    	<#include "component://homeapps/webapp/homeapps/user-audit/audit_from_header.ftl"/>
	        
	            <@dynaScreen 
		            instanceId="APP_BAR_STP"
		            modeOfAction="CREATE"
		            />
	            <div class="clearfix"></div>
	            <div class="form-group offset-2">
	                <div class="text-left ml-1 p-2">
	                    <@submit
	                        label="${uiLabelMap.Save}"
	                        />
	                    <@reset
	                        label="${uiLabelMap.Clear}"
	                        />
	                </div>
	            </div>
	            
	        </div>
        </form>
    </div>
</div>
<script></script>
<#include "component://appbar-portal/webapp/appbar-portal/configuration/uiLablePicker.ftl" />
<script type="text/javascript" src="/appbar-portal-resource/js/app-bar/app-bar-config.js"></script>
