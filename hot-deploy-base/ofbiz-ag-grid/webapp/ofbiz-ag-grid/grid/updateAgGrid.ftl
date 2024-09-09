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
    <div id="main" role="main" class="pd-btm-title-bar">
        <#assign extra ='<a href="viewAgGrid?instanceId=${inputContext?if_exists.instanceId!}&userId=${inputContext?if_exists.userId!}&role=${inputContext?if_exists.role!}" class="btn btn-xs btn-primary"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>' />

        <div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
        	<@sectionFrameHeaderTab title="${uiLabelMap.UpdateAgGrid!}" tabId="UpdateAgGrid" extra=extra/> 
            <form method="post" action="updateGridUserConfig" id="updateAgGridForm" name="updateAgGridForm" >
                <#include "component://homeapps/webapp/homeapps/user-audit/audit_from_header.ftl"/>
                <@dynaScreen 
	                instanceId="AG_GRID_INSTANCE"
	                modeOfAction="UPDATE"
	                />
                <div class="form-group offset-2">
                    <div class="text-left ml-3 pad-10">
                        <input type="submit" class="btn btn-sm btn-primary" value="Update">
                        <a href="viewAgGrid?instanceId=${inputContext?if_exists.instanceId!}&userId=${inputContext?if_exists.userId!}&role=${inputContext?if_exists.role!}" class="btn btn-sm btn-secondary"> Cancel</a>
                    </div>
                </div>
            </form>
            <div class="clearfix"></div>
        </div>
    </div>
</div>
<#include "component://homeapps/webapp/homeapps/user-audit/audit_footer.ftl"/>