<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://homeapps/webapp/homeapps/user-audit/audit_page_header.ftl"/>
<div class="row">
    <div id="main" role="main">
    <#assign extra='<a href="viewAppBar?appBarId=${inputContext?if_exists.appBarId!}&appBarTypeId=${inputContext?if_exists.appBarTypeId!}" class="btn btn-xs btn-primary"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>'/>
	    <form method="post" action="createAppBarElementService" id="createAppBarElementForm" name="createAppBarElementForm" data-toggle="validator" >
	    	<#include "component://homeapps/webapp/homeapps/user-audit/audit_from_header.ftl"/>
	        <div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
	        <@sectionFrameHeader 
		        title="${uiLabelMap.CreateAppBarElement}"
		        extra=extra!
		        />
	        	<@inputHidden 
	        		id="appBarId"
	        		value="${inputContext?if_exists.appBarId!}"
	        		/>
	        	<@inputHidden 
	        		id="appBarTypeId"
	        		value="${inputContext?if_exists.appBarTypeId!}"
	        		/>
	            <@dynaScreen 
		            instanceId="APP_BAR_ELEMENT_STP"
		            modeOfAction="CREATE"
		            />
	            <div class="clearfix"></div>
	            <div class="form-group offset-2">
	                <div class="text-left ml-1 pad-10">
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
<#include "component://appbar-portal/webapp/appbar-portal/configuration/uiLablePicker.ftl" />

<script type="text/javascript" src="/appbar-portal-resource/js/app-bar/app-bar-element-config.js"></script>
