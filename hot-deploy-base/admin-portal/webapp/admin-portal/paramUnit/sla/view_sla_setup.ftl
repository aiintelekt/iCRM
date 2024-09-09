<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<div class="row">
   <div id="main" role="main">
   		<#assign extra='<a href="updateSlaSetup?slaConfigId=${slaSetupConfig?if_exists.slaConfigId!}" class="btn btn-xs btn-primary"><i class="fa fa-edit" aria-hidden="true"></i> Update</a><a href="findSlaSetup" class="btn btn-xs btn-primary"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>'/>
   
      <#assign extraLeft=''/>

      	<div class="col-md-12 col-lg-12 col-sm-12 dash-panel">
            
            <@sectionFrameHeaderTab title="${uiLabelMap.ViewSlaSetup!}" tabId="ViewSlaSetup" extra=extra?if_exists extraLeft=extraLeft/> 
      
      		<@dynaScreen 
				instanceId="PARAM_SLA_STP"
				modeOfAction="VIEW"
				/>
				
        </div>
        
        <div class="col-lg-12 col-md-12 col-sm-12 dash-panel" id="sla-variation">
            	
        	<@dynaScreen 
				instanceId="PARAM_SLA_STP_VRT"
				modeOfAction="VIEW"
				/>
        	
        </div>
        
   </div>
</div>

<script>

$(document).ready(function() {

<#if inputContext.isSlaRequired?has_content && inputContext.isSlaRequired=="N">
	$("#sla-variation").hide();
</#if>

});

</script>
