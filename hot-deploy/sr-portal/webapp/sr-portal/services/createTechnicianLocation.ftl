<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<div class="row">
   <div id="main" role="main">
	      
	  <form id="mainFrom" name="createTechnicianLocation" action="createTechnicianLocationAction" method="post" data-toggle="validator">
      
      <div class="col-lg-12 col-md-12 col-sm-12">
      	
	  <@sectionFrameHeaderTab title="Create Technician Location Config" />
       	  	
        <@dynaScreen 
            instanceId="CREATE_TECHNICIAN_LOCATION"
            modeOfAction="CREATE"
         />
		             
		<div class="offset-md-2 col-sm-10 p-2">
       		<@formButton
                 btn1type="submit"
                 btn1label="${uiLabelMap.Save}"
                 btn2=true
                 btn2onclick = "resetFormToReload()"
                 btn2type="reset"
                 btn2label="${uiLabelMap.Clear}"
            />
        </div>
	            
     </form>
      </div>
   </div> <#-- main end -->
</div> <#-- row end-->

<script>

$(document).ready(function () {
	
	var countryGeoId = $('#generalCountryGeoId').val();
	
	regexJson = getServiceResult("getZipCodeRegex", 'countryGeoId', countryGeoId);
	regex = regexJson.regex;
	if ($('#generalCountryGeoId').val()) {
		getDependentDropdownValues('getAssociatedStateList', 'countryGeoId', 'generalCountryGeoId', 'generalStateProvinceGeoId', 'stateList', 'geoId', 'geoName', '${stateValue!}');
	}

	$('#generalCountryGeoId').change(function (e, data) {

		$("#generalStateProvinceGeoId").dropdown('clear');
		$('#generalPostalCode').val('');
		$('#generalPostalCodeExt').val('');
		getDependentDropdownValues('getAssociatedStateList', 'countryGeoId', 'generalCountryGeoId', 'generalStateProvinceGeoId', 'stateList', 'geoId', 'geoName', '${stateValue!}');
		var countryGeoId = $('#generalCountryGeoId').val();
		if (countryGeoId != '') {
			regexJson = getServiceResult("getZipCodeRegex", 'countryGeoId', countryGeoId);
			regex = regexJson.regex;
		} else {
			$('#generalStateProvinceGeoId').html('<option value="">Please Select</option>');
		}
	});
	
	getTechListForTech1();
	getProductStores();
	loadZipCodeAssoc();
	getCoordinatorList();
	
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
	
	$("#generalStateProvinceGeoId").change(function() {	
		loadZipCodeAssoc();
	});
	
});

function getProductStores(){
	
	var storeOptionList = "";
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
	    url:'/common-portal/control/getUsersList?roleTypeId=TECHNICIAN&isIncludeInactiveUser=N&externalLoginKey=${requestAttributes.externalLoginKey!}',
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
	    url:'/common-portal/control/getUsersList?roleTypeId=TECHNICIAN&isIncludeInactiveUser=N&externalLoginKey=${requestAttributes.externalLoginKey!}',
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
	    url:'/common-portal/control/getUsersList?roleTypeId=TECHNICIAN&isIncludeInactiveUser=N&externalLoginKey=${requestAttributes.externalLoginKey!}',
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
	    url:'/common-portal/control/getUsersList?roleTypeId=TECHNICIAN&isIncludeInactiveUser=N&externalLoginKey=${requestAttributes.externalLoginKey!}',
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

function getCoordinatorList(){
	var coordinatorOptionList = '<option value=""></option>';
	$("#coordinator").html(coordinatorOptionList);
	$.ajax({
	    type: "GET",
	    url:'/common-portal/control/getUsersList?roleTypeId=CUST_SERVICE_REP&isIncludeInactiveUser=N&externalLoginKey=${requestAttributes.externalLoginKey!}',
	    async: false,
	    success: function(data) {
	    	for (var i = 0; i < data.length; i++) {
	            var type = data[i];
	            coordinatorOptionList += '<option value="'+type.partyId+'">'+type.userName+' ('+ type.roleDesc +') </option>';
	        }
	    }
	});
	
	$("#coordinator").html(coordinatorOptionList);
    $("#coordinator").dropdown('refresh');
}

function loadZipCodeAssoc() {
	
	var countyOptions = '<option value="" selected="">Select County</option>';
	let countyList = new Map();
	
	$.ajax({
		type: "POST",
     	url: "/uiadv-portal/control/searchZipCodeAssocs",
        data: {"state": $("#generalStateProvinceGeoId").val(), "externalLoginKey": "${requestAttributes.externalLoginKey!}"},
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