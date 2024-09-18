<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/lib/picker_macro.ftl"/>
<#include "component://common-portal/webapp/common-portal/export/modal_window.ftl"/>

<div class="row">
	<div id="main" role="main" class="pd-btm-title-bar">

	<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
	
	<@sectionFrameHeader title="Find Accounts" />
		
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
				<form method="post" id="searchForm" name="searchForm" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
				<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}">
				<@inputHidden id="expFileTemplateId" value="ACCT_EXP_TPL"/>
				<@inputHidden id="exportDataType" value="ACCOUNT_LIST"/>
				<div class="panel-body">
					<@dynaScreen 
							instanceId="FIND_ACCOUNT"
							modeOfAction="CREATE"
							/>
		      		<div class="row find-srbottom">
		            	<div class="col-lg-12 col-md-12 col-sm-12">
		                	<div class="text-right pd-cbx-lbls" style="padding-top: 0px;">
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

<@exportFileModal
instanceId="exp-file-modal" exportType="ACCOUNT_LIST"
/>

<@responsiblePickerAccount 
	instanceId="partyResponsible" isExecutePartyAssoc="N"
	/>

<script>     
$(document).ready(function() {

var countryGeoId = $('#countryGeoId').val();
if($('#countryGeoId').val()) {
	getDependentDropdownValues('getAssociatedStateList', 'countryGeoId', 'countryGeoId', 'stateProvinceGeoId', 'stateList', 'geoId', 'geoName', '${stateValue!}', null, true);
}
$('#countryGeoId').change(function(e, data) {
	$("#stateProvinceGeoId").dropdown('clear');
	getDependentDropdownValues('getAssociatedStateList', 'countryGeoId', 'countryGeoId', 'stateProvinceGeoId', 'stateList', 'geoId', 'geoName','${stateValue!}', null, true);
});

$("#stateProvinceGeoId").change(function() {
	$("#city").dropdown("clear");
		
	var cityOptions = '<option value="" selected="">Select City</option>';
		
	let cityList = new Map();
								
	$.ajax({
		type: "POST",
     	url: "/uiadv-portal/control/searchZipCodeAssocs",
        data: {"state": $(this).val(), "externalLoginKey": "${requestAttributes.externalLoginKey!}"},
        async: false,
        success: function (result) {   
            if (result.code == 200) {
            	for (var i = 0; i < result.data.length; i++) {
					var data = result.data[i];
					cityList.set(data.city, data.city);
				}
            }
        }
	});   
	
	for (let key of cityList.keys()) {
  		cityOptions += '<option value="'+key+'">'+cityList.get(key)+'</option>';
	}
	
	$("#city").html( DOMPurify.sanitize(cityOptions) );
	$("#city").dropdown('refresh');
});
	
});
</script>