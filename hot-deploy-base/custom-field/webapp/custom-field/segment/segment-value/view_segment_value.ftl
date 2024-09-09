<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<div class="row">
    <div id="main" role="main" class="pb-0">
    <div  class="col-lg-12 col-md-12 col-sm-12 dash-panel"> 	
		<#if customFieldGroup.groupId?has_content>		
		<#assign extra='<a href="segmentValueForGroup?groupId=${customFieldGroup.groupId}" class="btn btn-xs btn-primary m5 tooltips" title="Create Segment Value for ${customFieldGroup.groupName!}" >Create Segment Value</a>'/>
		<#assign extra=extra+'<a href="findSegmentCode" class="btn btn-xs btn-primary m5 tooltips" title="Back to Segment Code" >Back</a>'/>
		</#if>
<@sectionFrameHeader extra=extra title="${uiLabelMap.View} ${uiLabelMap.SegmentValue}"><#if customFieldGroup.groupName?has_content>for [ ${customFieldGroup.groupName} ]</#if></@sectionFrameHeader>
	
	

