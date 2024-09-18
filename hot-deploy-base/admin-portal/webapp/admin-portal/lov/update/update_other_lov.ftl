<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/lib/picker_macro.ftl"/>

<div class="row">
    <div id="main" role="main">
    <div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
        <#assign extra='<a href="/admin-portal/control/viewLov?lovId=${inputContext.lovId!}&lovTypeId=${inputContext.lovTypeId!}" class="btn btn-xs btn-primary">
        <i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>' />
        <@sectionFrameHeaderTab title="${uiLabelMap.UpdateLov!}" extra=extra tabId="UpdateOtherLov"/> 
        <div class="clearfix"></div>
        <form id="mainForm" method="post" action="<@ofbizUrl>updateOtherLovAction</@ofbizUrl>" data-toggle="validator"> 
        
           
            	
            	<@dynaScreen 
					instanceId="OTHER_LOV_BASE"
					modeOfAction="UPDATE"
					/>
           
            
           <div class="form-group offset-2">
            <div class="text-left ml-3 pad-10">
         
            <@submit label="${uiLabelMap.Save}"/>
            <@cancel label="Cancel" onclick="/admin-portal/control/viewOtherLov?lovId=${inputContext.lovId!}&lovTypeId=${inputContext.lovTypeId!}"/>
            
            </div>
            </div>
        </form>
    </div>
</div>
</div>
<@partyPicker 
	instanceId="partyPicker"
	/> 

<script>

$(document).ready(function() {

});

</script>