<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>

<div class="row">
    <div id="main" role="main">
    <div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
        <#assign extra='<a href="/admin-portal/control/viewOppoStage?opportunityStageId=${inputContext.opportunityStageId!}" class="btn btn-xs btn-primary">
        <i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>' />
        <@sectionFrameHeaderTab title="${uiLabelMap.UpdateOppoStage!}" tabId="UpdateOppoStage" extra=extra/> 

        <div class="clearfix"></div>
        <form id="mainFrom" method="post" action="<@ofbizUrl>updateOppoStageAction</@ofbizUrl>" data-toggle="validator"> 
        
            
            	
            	<@dynaScreen 
					instanceId="OPPO_STAGE_BASE"
					modeOfAction="UPDATE"
					/>
            	
             <div class="form-group offset-2">
            <div class="text-left ml-3 pad-10">
            <@submit label="${uiLabelMap.Save}"/>
            <@cancel label="Cancel" onclick="/admin-portal/control/viewOppoStage?opportunityStageId=${inputContext.opportunityStageId!}"/>
            
            </div>
            </div>
        </form>
    </div>
</div>
</div>
<script>

$(document).ready(function() {



});

</script>