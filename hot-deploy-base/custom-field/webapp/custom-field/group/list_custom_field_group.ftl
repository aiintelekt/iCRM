<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>

<div class="row" style="width:100%">
<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">

<#assign listTitle = "${uiLabelMap.List} ${uiLabelMap.CustomFieldGroup}" />
<#if groupingCode?has_content && groupingCode.groupType?has_content>
	<#if groupingCode.groupType=="SEGMENTATION">
		<#assign listTitle = "${uiLabelMap.List} ${uiLabelMap.SegmentCode}" />
	<#elseif groupingCode.groupType=="ECONOMIC_METRIC">
		<#assign listTitle = "${uiLabelMap.List} ${uiLabelMap.EconomicMetric}" />	
	</#if>
</#if>

<@pageSectionHeader title=listTitle />
<div id="findAttributeGroupgrid" style=" width: 100%;" class="ag-theme-balham"></div>

<#if groupingCode?has_content && groupingCode.groupType?has_content>
	<#if groupingCode.groupType=="SEGMENTATION">
		<script type="text/javascript" src="/cf-resource/js/findSegmentCode.js"></script>
	<#elseif groupingCode.groupType=="ECONOMIC_METRIC">
		<script type="text/javascript" src="/cf-resource/js/ag-grid/econometric-code.js"></script>
	<#elseif groupingCode.groupType=="CUSTOM_FIELD">
		<script type="text/javascript" src="/cf-resource/js/ag-grid/contactfield/findAttributeGroup.js"></script>	
	</#if>
<#else>
	<script type="text/javascript" src="/cf-resource/js/ag-grid/contactfield/findAttributeGroup.js"></script>	
</#if>     

</div>
</div>

<script type="text/javascript">

jQuery(document).ready(function() {		

});	
	
</script>