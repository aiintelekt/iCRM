<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://homeapps/webapp/homeapps/user-audit/audit_page_header.ftl"/>
<script>
$(document).ready(function(){
	$("#gridOptionsJsString").attr('rows', '10');
    $('#gridOptionsJsString')
    .focus(function(){$(this).attr('rows', '50');})
    .blur(function(){$(this).attr('rows', '10');});
});
</script>

<div class="row">
    <div id="main" role="main" >
     <div class="col-lg-12 col-md-12 col-sm-12 dash-panel" >
        <#assign extra ='<a href="viewAgGrid?instanceId=${inputContext?if_exists.instanceId!}&userId=${inputContext?if_exists.userId!}&role=${inputContext?if_exists.role!}" class="btn btn-xs btn-primary"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>' />
        <@sectionFrameHeader title="${uiLabelMap.CloneAgGridInstance!}" extra=extra />
       
            <form method="post" action="createGridUserConfig" id="cloneAgGridForm" name="cloneAgGridForm" data-toggle="validator">
                <#include "component://homeapps/webapp/homeapps/user-audit/audit_from_header.ftl"/>
                <@dynaScreen 
	                instanceId="AG_GRID_INSTANCE"
	                modeOfAction="CREATE"
	                />
	            <div class="clearfix"></div>
                <div class="form-group offset-2">
                    <div class="text-left ml-3 p-2">
                    	<@submit
                            label="${uiLabelMap.Clone}"
                            />
                        <@reset
                            label="${uiLabelMap.Clear}"
                            />
                        <#-- <input type="submit" class="btn btn-sm btn-primary" value="Clone">
                        <a href="viewAgGrid?instanceId=${inputContext?if_exists.instanceId!}&userId=${inputContext?if_exists.userId!}&role=${inputContext?if_exists.role!}" class="btn btn-sm btn-secondary"> Cancel</a> -->
                    </div>
                </div>
            </form>
        </div>
    </div>
</div>
<#include "component://homeapps/webapp/homeapps/user-audit/audit_footer.ftl"/>