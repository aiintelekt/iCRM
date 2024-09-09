<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>

<div class="row">
	<div id="main" role="main" class="pd-btm-title-bar">

	<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">

	<@sectionFrameHeader title="${uiLabelMap.Find} ${uiLabelMap.CustomFieldGroup}" />
		
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
				<form method="post" id="findAttributeGroupForm" name="findAttributeGroupForm" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
				<input type="hidden" name="groupType" value="CUSTOM_FIELD" />
				<div class="panel-body">
				
					<div class="row">
					<div class="col-lg-3 col-md-3 col-sm-3">
						<@inputRow 
						id="groupId"
						placeholder=uiLabelMap.groupId
						inputColSize="col-sm-12"
						required=false
						/> 
					</div>
					<div class="col-lg-3 col-md-3 col-sm-3">
						<@inputRow 
						id="groupName"
						placeholder=uiLabelMap.groupName
						inputColSize="col-sm-12"
						required=false
						/> 
					</div>
					<div class="col-lg-3 col-md-3 col-sm-3">
						<@dropdownCell 
						id="groupingCodeId"
						options=groupingCodeList
						required=false
						allowEmpty=true				
						dataLiveSearch=true
						placeholder=uiLabelMap.groupingCode
						isMultiple="Y"
						/>
					</div>
					<div class="col-lg-3 col-md-3 col-sm-3">
						<@dropdownCell
			            id="hide"
			            allowEmpty=true
			            options=yesNoOptions!
			            placeholder=uiLabelMap.hide
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

<script>     
$(document).ready(function() {

$("#main-search-btn").click(function(event) {
    event.preventDefault(); 
    getGridRowData();
});
	
});
</script>
