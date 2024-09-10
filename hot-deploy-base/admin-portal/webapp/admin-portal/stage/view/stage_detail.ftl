<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>

<div class="">
  <h2 class="d-inline-block">General Details</h2>
   <ul class="flot-icone">
     <li class="mt-0">
     <#if oppoStageSummary.lastUpdatedStamp?has_content> 
        <small>${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(oppoStageSummary.lastUpdatedStamp!, "yyyy-MM-dd")}</small>
     </#if>
     </li>
     
     <a href="<@ofbizUrl>updateOppoStage?opportunityStageId=</@ofbizUrl>${inputContext.opportunityStageId!}" class="btn btn-xs btn-primary"><i class="fa fa-edit" aria-hidden="true"></i> Update</a>
  </ul>
</div>

<div class="col-md-12 col-lg-12 col-sm-12">
			
	<@dynaScreen 
	instanceId="OPPO_STAGE_BASE"
	modeOfAction="VIEW"
	/>

</div>

<script>     
$(document).ready(function() {

});
</script>