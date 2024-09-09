<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#assign requestURI = ""/>
<#if request.getRequestURI().contains("main")>
<#assign helpUrl = Static["org.groupfio.common.portal.util.DataHelper"].getTabHelpUrl(delegator, request.getRequestURI(), "main") />
</#if>
<div class="row">
	<div id="main" role="main" class="pd-btm-title-bar">

	<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
	
	<@sectionFrameHeader title="Find Technician Location" extra=helpUrl! />
		
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
				<form method="post" id="searchForm" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
					<div class="panel-body">
						<@dynaScreen 
						instanceId="FIND_TECHNICIAN_LOCATION"
						modeOfAction="CREATE"
						/>
												
						<div class="text-right pd-cbx-lbls pad-10" style="padding-top: 0px;">
					     	<@button
					        id="main-search-btn"
					        label="${uiLabelMap.Find}"
					        />	
					     	<@reset
							label="${uiLabelMap.Reset}"/>
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

$(document).ready(function () {
	
	var countryGeoId = $('#generalCountryGeoId').val();
	
	regexJson = getServiceResult("getZipCodeRegex", 'countryGeoId', countryGeoId);
	regex = regexJson.regex;
	if ($('#generalCountryGeoId').val()) {
		getDependentDropdownValues('getAssociatedStateList', 'countryGeoId', 'generalCountryGeoId', 'stateGeoId', 'stateList', 'geoId', 'geoName', '${stateValue!}','',true);
	}

	$('#generalCountryGeoId').change(function (e, data) {

		$("#stateGeoId").dropdown('clear');
		$('#generalPostalCode').val('');
		$('#generalPostalCodeExt').val('');
		getDependentDropdownValues('getAssociatedStateList', 'countryGeoId', 'generalCountryGeoId', 'stateGeoId', 'stateList', 'geoId', 'geoName', '${stateValue!}','',true);
		var countryGeoId = $('#generalCountryGeoId').val();
		if (countryGeoId != '') {
			regexJson = getServiceResult("getZipCodeRegex", 'countryGeoId', countryGeoId);
			regex = regexJson.regex;
		} else {
			$('#stateGeoId').html('<option value="">Please Select</option>');
		}
	});
	
	getTechListForTech1();
	getProductStores();
	loadZipCodeAssoc();
	
	$("#technician1").change(function() {
		var selTechnician  = $(this).val();
		$("#technician2").html('');
		$('.technician2 .clear').click();
		getTechListForTech2(selTechnician);
	});
	
	$("#technician2").change(function() {
		var selTechnician  = $(this).val();
		$("#technician3").html('');
		$('.technician3 .clear').click();
		getTechListForTech3(selTechnician);
	});
	
	$("#technician3").change(function() {
		var selTechnician  = $(this).val();
		$("#technician4").html('');
		$('.technician4 .clear').click();
		getTechListForTech4(selTechnician);
	});
	
	$("#stateGeoId").change(function() {	
		loadZipCodeAssoc();
	});
	
});

function getProductStores(){
	
	var storeOptionList = "<option value=''></option>";
	$.ajax({
	    type: "GET",
	    url:'/admin-portal/control/getProductStores?externalLoginKey=${requestAttributes.externalLoginKey!}',
	    async: false,
	    success: function(data) {
	    	for (var i = 0; i < data.length; i++) {
	            var type = data[i];
	            storeOptionList += '<option value="'+type.storeId+'">'+type.storeName+'</option>';
	        }
	    }
	});
	
	$("#productStoreId").html(storeOptionList);
	$("#productStoreId").dropdown('refresh');
}

function getTechListForTech1(){
	
	var technicianOptionList = '<option value=""></option>';
	
	$.ajax({
	    type: "GET",
	    url:'/common-portal/control/getUsersList?roleTypeId=TECHNICIAN&externalLoginKey=${requestAttributes.externalLoginKey!}',
	    async: false,
	    success: function(data) {
	    	for (var i = 0; i < data.length; i++) {
	            var type = data[i];
	            technicianOptionList += '<option value="'+type.partyId+'">'+type.userName+' ('+ type.roleDesc +') </option>';
	        }
	    }
	});
	
	$("#technician1").html(technicianOptionList);
	$("#technician1").dropdown('refresh');
}

function getTechListForTech2(selTechnician){
	
	var technicianOptionList = '<option value=""></option>';
	
	$.ajax({
	    type: "GET",
	    url:'/common-portal/control/getUsersList?roleTypeId=TECHNICIAN&externalLoginKey=${requestAttributes.externalLoginKey!}',
	    async: false,
	    success: function(data) {
	    	for (var i = 0; i < data.length; i++) {
	            var type = data[i];
	            if(selTechnician && selTechnician != type.partyId){
	            	technicianOptionList += '<option value="'+type.partyId+'">'+type.userName+' ('+ type.roleDesc +') </option>';
	            }
	        }
	    }
	});
	
	$("#technician2").html(technicianOptionList);
    $("#technician2").dropdown('refresh');
}

function getTechListForTech3(selTechnician){
	
	var selTechnician1 = $("#technician1").val();
	var technicianOptionList = '<option value=""></option>';
	
	$.ajax({
	    type: "GET",
	    url:'/common-portal/control/getUsersList?roleTypeId=TECHNICIAN&externalLoginKey=${requestAttributes.externalLoginKey!}',
	    async: false,
	    success: function(data) {
	    	for (var i = 0; i < data.length; i++) {
	            var type = data[i];
	            if(selTechnician && selTechnician != type.partyId && selTechnician1 != type.partyId){
	            	technicianOptionList += '<option value="'+type.partyId+'">'+type.userName+' ('+ type.roleDesc +') </option>';
	            }
	        }
	    }
	});
	
	$("#technician3").html(technicianOptionList);
    $("#technician3").dropdown('refresh');
}

function getTechListForTech4(selTechnician){
	
	var selTechnician1 = $("#technician1").val();
	var selTechnician2 = $("#technician2").val();
	var technicianOptionList = '<option value=""></option>';
	
	$.ajax({
	    type: "GET",
	    url:'/common-portal/control/getUsersList?roleTypeId=TECHNICIAN&externalLoginKey=${requestAttributes.externalLoginKey!}',
	    async: false,
	    success: function(data) {
	    	for (var i = 0; i < data.length; i++) {
	            var type = data[i];
	            if(selTechnician && selTechnician != type.partyId && selTechnician1 != type.partyId && selTechnician2 != type.partyId){
	            	technicianOptionList += '<option value="'+type.partyId+'">'+type.userName+' ('+ type.roleDesc +') </option>';
	            }
	        }
	    }
	});
	
	$("#technician4").html(technicianOptionList);
    $("#technician4").dropdown('refresh');
}

function loadZipCodeAssoc() {
	
	var countyOptions = '<option value="" selected="">Select County</option>';
	let countyList = new Map();
	
	$.ajax({
		type: "POST",
     	url: "/uiadv-portal/control/searchZipCodeAssocs",
        data: {"state": $("#stateGeoId").val(), "externalLoginKey": "${requestAttributes.externalLoginKey!}"},
        async: false,
        success: function (result) {   
            if (result.code == 200) {
            	for (var i = 0; i < result.data.length; i++) {
					var data = result.data[i];
					countyList.set(data.county, data.county);
				}
            }
        }
	}); 
	
	for (let key of countyList.keys()) {
		countyOptions += '<option value="'+key+'">'+countyList.get(key)+'</option>';
	}
	
	$("#countyGeoId").html( countyOptions );
	$("#countyGeoId").dropdown('refresh');
	
}
</script>