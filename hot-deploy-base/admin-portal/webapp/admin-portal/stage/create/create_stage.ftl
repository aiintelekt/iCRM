<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>

<div class="row">
    <div id="main" role="main">
     <div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
        <#assign extra='<a href="/admin-portal/control/findOppoStage" class="btn btn-xs btn-primary">
        <i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>' />
        <@sectionFrameHeader title="${uiLabelMap.CreateOppoStage!}" extra=extra />
        <div class="clearfix"></div>
        <form id="mainForm" method="post" action="<@ofbizUrl>createOppoStageAction</@ofbizUrl>" data-toggle="validator">    
        
        	<input type="hidden" name="vendorPartyId" value="${vendorPartyId!}">
        
           
            	
            	<@dynaScreen 
					instanceId="OPPO_STAGE_BASE"
					modeOfAction="CREATE"
					/>
            	
            <div class="form-group offset-2">
            <div class="text-left ml-1 pad-10">
         
            <@formButton
                     btn1type="submit"
                     btn1label="${uiLabelMap.Save}"
                     btn2=true
                     btn2onclick = "resetForm()"
                     btn2type="reset"
                     btn2label="${uiLabelMap.Clear}"
                   />
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