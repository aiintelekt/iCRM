<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<div class="row">
    <div id="main" role="main">
<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
	<#--  
	  <#if customFieldGroup.groupId?has_content>		
		<a href="economicValueForGroup?groupId=${customFieldGroup.groupId}" class="btn btn-xs btn-primary m5 tooltips" title="Create Economic Metric for ${customFieldGroup.groupName!}" >Create Economic Metric</a>
		<a href="findEconomicMetric" class="btn btn-xs btn-primary m5 tooltips" title="Back to Economic Code" >Back</a>	
	  </#if>
	 -->   
	  
	 
	<#assign extra=''>
		<#if customFieldGroup.groupId?has_content>		
		<#assign extra='<a href="economicValueForGroup?groupId=${customFieldGroup.groupId}" class="btn btn-xs btn-primary m5 tooltips" title="Create Economic Metric for ${customFieldGroup.groupName!}" >Create Economic Metric</a>'>
		<#assign extra=extra+'<a href="findEconomicMetric" class="btn btn-xs btn-primary m5 tooltips" title="Back to Economic Code" >Back</a>' >
		</#if>
	
	<@sectionFrameHeaderTab extra=extra title="${uiLabelMap.View} ${uiLabelMap.EconomicValue}" tabId="ViewEconomicValue"> <#if customFieldGroup.groupName?has_content>for [ ${customFieldGroup.groupName} ]</#if></@sectionFrameHeaderTab> 

	
<#if customFieldGroup.groupId?has_content>
	<#assign actionUrl = "createEconomicValue"/>
<#else>
	<#assign actionUrl = "createEconomicValueIndividual"/>
</#if>

<div class="row padding-r">
	<div class="col-md-6 col-sm-6">
		
		<div class="portlet-body form">
			
			
			
		</form>			
							
		</div>
			
	</div>
	</div>
</div>
	
<script>

jQuery(document).ready(function() { 

});

</script>
