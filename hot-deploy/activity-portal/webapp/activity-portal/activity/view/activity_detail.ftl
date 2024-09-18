<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/lib/picker_macro.ftl"/>

<#assign updateUrl = "main">
<#if inputContext.workEffortTypeId=="APPOINTMENT">
<#assign updateUrl = "updateApnt">
<#elseif inputContext.workEffortTypeId=="TASK">
<#assign updateUrl = "updateTask">
</#if>
<#assign helpUrl = Static["org.groupfio.common.portal.util.DataHelper"].getTabHelpUrl(delegator, request.getRequestURI(), "") />  
<div class="page-header border-b pt-2">
  <h2 class="d-inline-block">General Details</h2>
   <ul class="flot-icone">
     <li class="mt-0">
     <#if activitySummary.lastUpdatedStamp?has_content> 
        <small>${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(activitySummary.lastUpdatedStamp!, "yyyy-MM-dd")}</small>
     </#if>
     </li>
     
     <#if (inputContext.currentStatusId!="IA_MCOMPLETED" && inputContext.currentStatusId !="IA_CLOSED")>
     <li class="mt-0">
        <a href="/activity-portal/control/${updateUrl!}?workEffortId=${inputContext.workEffortId!}" class="btn btn-xs btn-primary"><i class="fa fa-edit" aria-hidden="true"></i> Update</a>
     </li>
     </#if>
      <span calss="float-right" id="act-details">${helpUrl?if_exists}</span>
  </ul>
</div>

<div class="col-md-12 col-lg-12 col-sm-12">
			
	<@dynaScreen 
	instanceId="${instanceId!}"
	modeOfAction="VIEW"
	/>

</div>

<div class="col-md-12 col-lg-12 col-sm-12">
	<@inputArea
	  inputColSize="col-sm-12"
	  id="messages"
	  label=uiLabelMap.Description
	  maxlength=100
	  rows="10"
	  disabled=true
	  placeholder = uiLabelMap.Description
	  value = inputContext.description?if_exists
	/>
</div>

<script>     
$(document).ready(function() {



});
</script>