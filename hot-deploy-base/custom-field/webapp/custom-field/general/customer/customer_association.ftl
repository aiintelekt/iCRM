<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>

<div class="row">
    <div id="main" role="main">
<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
    <#assign extra='<a href="editSegmentValue?customFieldId=${customField.customFieldId}&groupId=${customField.groupId!}" class="btn btn-xs btn-primary m5 tooltips" title="Back to Update Segment Value" >Back</a>'/>
     <div><@sectionFrameHeader extra=extra title="${uiLabelMap.ManageParty!} for [ ${customFieldGroup.groupName!} <i class='fa fa-arrow-right' aria-hidden='true'></i> ${customField.customFieldName!} ]" /></div>
	<div class="float-right">	
		<#if customField.groupType?has_content && customField.groupType == "SEGMENTATION">
			<#assign backActionUrl = "editSegmentValue"/>
		<#elseif customField.groupType?has_content && customField.groupType == "ECONOMIC_METRIC">
			<#assign backActionUrl = "editEconomicValue"/>
		</#if>
		</div>

<ul class="nav nav-tabs">
	<li class="nav-item" id="add-customer-tab"><a data-toggle="tab" href="#tab2"
		class="nav-link active">${uiLabelMap.FindParty!}</a></li>
	<#if customField.groupType?has_content && customField.groupType == "SEGMENTATION">
	<li class="nav-item"><a data-toggle="tab" href="#tab3"
		class="nav-link">Upload Parties</a></li>
	</#if>
</ul>

<div class="tab-content">
	
	<div id="tab2" class="tab-pane fade show active">
		<#-- ${screens.render("component://custom-field/webapp/widget/custom-field/screens/common/GeneralScreens.xml#AddCustomer")} -->
		${screens.render("component://custom-field/webapp/widget/custom-field/screens/common/GeneralScreens.xml#AssignedCustomer")}
	</div>
	<#if customField.groupType?has_content && customField.groupType == "SEGMENTATION">
	<div id="tab3" class="tab-pane fade in">
		${screens.render("component://custom-field/webapp/widget/custom-field/screens/common/GeneralScreens.xml#uploadSegment")}
		
	</div>
    </#if>
</div>
</div>
</div>
</div>
<script>

jQuery(document).ready(function() {	

/*
$('#assigned-customer-tab').on('click', function(){
	findSelectedCustomers();
});

$('#add-customer-tab').on('click', function(){
	findCustomers();
});
*/
    <#if !activeTab?has_content>
        <#assign activeTab = requestParameters.activeTab!>
    </#if>
    
    <#if activeTab?has_content && activeTab == "addCustomer">
        $('.nav-tabs a[href="#tab2"]').tab('show');
    <#elseif activeTab?has_content && activeTab == "uploadCustomers">
        $('.nav-tabs a[href="#tab3"]').tab('show');	
    <#else>
        $('.nav-tabs a[href="#tab2"]').tab('show');	
    </#if>
});

</script>