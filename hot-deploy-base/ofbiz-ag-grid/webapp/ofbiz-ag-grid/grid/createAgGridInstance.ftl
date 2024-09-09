<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://homeapps/webapp/homeapps/user-audit/audit_page_header.ftl"/>
<div class="row">
    <div id="main" role="main">
        <#assign extra='<a href="findAgGridConfig" class="btn btn-xs btn-primary"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>'/>
        <#assign extraLeft=''/>
        <form name="craeteAgGridForm" id="craeteAgGridForm" action="createGridUserConfig" method="post" data-toggle="validator">
            <#include "component://homeapps/webapp/homeapps/user-audit/audit_from_header.ftl"/>
            <div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
		        <@sectionFrameHeader 
		        title="${uiLabelMap.CreateAgGridInstance}"
		        extra=extra?if_exists 
		        />
                <@dynaScreen 
	                instanceId="AG_GRID_INSTANCE"
	                modeOfAction="CREATE"
	                />
                <div class="clearfix"></div>
                <div class="form-group offset-2">
                    <div class="text-left ml-3 pad-10">
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
    <#-- main end -->
</div>
<#-- row end-->