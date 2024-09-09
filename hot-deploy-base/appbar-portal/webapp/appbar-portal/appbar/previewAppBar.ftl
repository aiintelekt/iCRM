<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<div class="row">
    <div id="main" role="main" class="">
     <div class="col-lg-12 col-md-12 col-sm-12">
        <@sectionFrameHeader  title="Preview App Bar: ${requestParameters.appBarId!}" />
       
    		<div class="<#if "DASHBOARD" == requestParameters.appBarTypeId!>dashboard-adj<#else>card-head margin-adj mt-2</#if>"> 
                <@AppBar
	                appBarId="${requestParameters.appBarId!}"
	                appBarTypeId="${requestParameters.appBarTypeId!}"
	                isEnableUserPreference=true
	                />
            </div>
        </div>
	</div>
</div>