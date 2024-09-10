<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>

<div class="row">
	<div id="main" role="main" class="pd-btm-title-bar">

	<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
	
	<@sectionFrameHeader title="${uiLabelMap.Find} ${uiLabelMap.CustomField}" />
		
	<div class="panel-group" id="accordionMenu" role="tablist" aria-multiselectable="true">
		<div class="panel panel-default">
			<div class="panel-heading" role="tab" id="headingTwo">
				<h4 class="panel-title">
					<a role="button" data-toggle="collapse" data-parent="#accordionMenu"
						href="#accordionDynaBase" aria-expanded="true"
						aria-controls="collapseOne"> ${uiLabelMap.MainFilter} </a>
				</h4>
			</div>	
			
			<div id="accordionDynaBase" class="panel-collapse collapse show" role="tabpanel" aria-labelledby="headingOne">
				<form method="post" id="findCustomFieldsForm" name="findCustomFieldsForm" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
				<div class="panel-body">
				
					<div class="row">
					<div class="col-lg-4 col-md-6 col-sm-12">
					
						<@dropdownCell
			            id="roleTypeId"
			            allowEmpty=true
			            options=roleTypeList!
			            placeholder=uiLabelMap.roleTypeId
			            />
			            
			            <@dropdownCell
			            id="customFieldFormat"
			            allowEmpty=true
			            options=fieldFormatList!
			            placeholder=uiLabelMap.customFieldFormat
			            />
			            
			            <@dropdownCell
			            id="groupId"
			            allowEmpty=true
			            options=groupList!
			            placeholder=uiLabelMap.customGroup
			            />
						 						
					</div>
					<div class="col-lg-4 col-md-6 col-sm-12">
									            
			            <@dropdownCell
			            id="customFieldLength"
			            allowEmpty=true
			            options=fieldLengthList!
			            placeholder=uiLabelMap.fieldLength
			            />
			            
			            <@inputRow 
						id="customFieldName"
						placeholder=uiLabelMap.customFieldName
						inputColSize="col-sm-12"
						required=false
						/> 
			            
						<@dropdownCell
			            id="hide"
			            allowEmpty=true
			            options=yesNoOptions!
			            placeholder=uiLabelMap.hide
			            />
						
					</div>
					<div class="col-lg-4 col-md-6 col-sm-12">
						
						<@dropdownCell
			            id="customFieldType"
			            allowEmpty=true
			            options=fieldTypeList!
			            placeholder=uiLabelMap.customFieldType
			            />   
												
						<div class="search-btn pb-1">
							<@button
					        id="main-search-btn"
					        label="${uiLabelMap.Find}"
					        />	
					     	<@reset
							label="${uiLabelMap.Reset}"/>
						</div>
					
					</div>
					</div>
					
				</div>	
				</form>
			</div>	
		</div>	
	</div>	
		
	</div>
	</div>
</div>

<div class="row" style="width:100%">
<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">

<@pageSectionHeader title="${uiLabelMap.List} ${uiLabelMap.CustomField}" />
<div id="findCustomFieldGrid" style=" width: 100%;" class="ag-theme-balham">
</div>
     
<script type="text/javascript" src="/cf-resource/js/findCustomFields.js"></script>

</div>
</div>

<script>     
$(document).ready(function() {

$("#main-search-btn").click(function(event) {
    event.preventDefault(); 
    getGridRowData();
});
	
});
</script>
