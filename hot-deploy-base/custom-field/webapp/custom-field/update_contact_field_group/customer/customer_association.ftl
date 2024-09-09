<#include "component://homeapps/webapp/homeapps/lib/ofbizFormMacros.ftl"/>

<div class="page-header border-b">
	<h1 class="float-left">${uiLabelMap.ManageCustomers!} <#if customField.customFieldName?has_content>for [ ${customFieldGroup.groupName!} <i class="fa fa-arrow-right" aria-hidden="true"></i> ${customField.customFieldName} ]</#if> </h1>
	<div class="float-right">
	
		<#if customField.groupType?has_content && customField.groupType == "SEGMENTATION">
			<#assign backActionUrl = "editSegmentValue"/>
		<#elseif customField.groupType?has_content && customField.groupType == "ECONOMIC_METRIC">
			<#assign backActionUrl = "editEconomicValue"/>
		</#if>
	
		<a href="${backActionUrl!}?groupId=${customFieldGroup.groupId!}&customFieldId=${customField.customFieldId!}" class="btn btn-xs btn-primary m5 tooltips" title="Back to ${customField.customFieldName!}" >Back</a>
	</div>
</div>

<ul class="nav nav-tabs">
	<#-- <li class="nav-item" id="assigned-customer-tab"><a data-toggle="tab" href="#tab1"
		class="nav-link active">${uiLabelMap.AssignedCustomer!}</a></li> -->
	<li class="nav-item" id="add-customer-tab"><a data-toggle="tab" href="#tab2"
		class="nav-link active">${uiLabelMap.AddCustomer!}</a></li>
	<#if customField.groupType?has_content && customField.groupType == "SEGMENTATION">
	<li class="nav-item"><a data-toggle="tab" href="#tab3"
		class="nav-link">Upload Customers</a></li>
	</#if>
</ul>

<div class="tab-content">
	
	<#-- 
	<div id="tab1" class="tab-pane fade show active">
		
		${screens.render("component://custom-field/webapp/widget/custom-field/screens/segment/SegmentScreens.xml#AssignedCustomer")}
		
	</div>
	 -->
	 
	<div id="tab2" class="tab-pane fade show active">
		
		${screens.render("component://custom-field/webapp/widget/custom-field/screens/common/GeneralScreens.xml#AddCustomer")}
		${screens.render("component://custom-field/webapp/widget/custom-field/screens/common/GeneralScreens.xml#AssignedCustomer")}
		
	</div>
	<#if customField.groupType?has_content && customField.groupType == "SEGMENTATION">
	<div id="tab3" class="tab-pane fade in">
		${screens.render("component://custom-field/webapp/widget/custom-field/screens/common/GeneralScreens.xml#uploadSegment")}
		<#--<div class="row padding-r">
			<div class="col-md-6 col-sm-6 form-horizontal">
				<div class="form-group row">
					<label class="col-sm-4 col-form-label">Import File </label>
					<div class="col-sm-7">
						<input id="uploadedFile" name="uploadedFile" type="file" size="30"
							maxlength="" class="form-control" onchange="">
					</div>
				</div>
				<div class="form-group has-error row">
					<label class="col-sm-4 col-form-label"></label>
					<div class="col-sm-7">
						<label class="col-form-label fw">Pick the right TEXT File
							and upload</label>
					</div>
				</div>
				<div class="form-group row">
					<label class="col-sm-4 col-form-label">File Format</label>
					<div class="col-sm-7">
						<select class="form-control input-sm" disabled>
							<option value="1">Text</option>
						</select>
					</div>
				</div>
				<div class="form-group row">
					<label class="col-sm-4 col-form-label">Text Format Template </label>
					<div class="col-sm-7">
						<a href="#" class="btn btn-xs btn-primary mt">Download</a>
					</div>
				</div>
			</div>
		</div>
		<div class="clearfix"></div>
		<div class="col-md-12 col-sm-12">
			<div class="form-group row">
				<div class="offset-sm-2 col-sm-9">
					<button type="reset" class="btn btn-sm btn-primary mt">Upload</button>
				</div>
			</div>
		</div>
		<div class="table-responsive">
			<table class="table table-striped">
				<thead>
					<tr>
						<th colspan="2">Results</th>
					</tr>
				</thead>
				<tbody>
					<tr>
						<td>Total Count</td>
						<td>12</td>
					</tr>
					<tr>
						<td>Success Count</td>
						<td>10</td>
					</tr>
					<tr>
						<td>Error Count</td>
						<td>2</td>
					</tr>
				</tbody>
			</table>
		</div>
		<div class="clearfix"></div>-->
		
	</div>
    </#if>
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