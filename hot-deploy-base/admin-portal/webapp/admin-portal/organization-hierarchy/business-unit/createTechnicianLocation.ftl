<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<div class="row">
   <div id="main" role="main">
		
      <div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
      	
	  <@sectionFrameHeaderTab title="Create Technician Location Config" />
	      
	  <form name="createTechnicianLocation" action="createTechnicianLocationAction" method="post" data-toggle="validator">
       	  	
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
	
	$("#technician1").change(function() {
		var selTechnician  = $(this).val();
		getTechListForTech2(selTechnician);
	});
	
	$("#technician2").change(function() {
		var selTechnician  = $(this).val();
		getTechListForTech3(selTechnician);
	});
	
	$("#technician3").change(function() {
		var selTechnician  = $(this).val();
		getTechListForTech4(selTechnician);
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
	    url:'/admin-portal/control/getContractorTechnicians?externalLoginKey=${requestAttributes.externalLoginKey!}',
	    async: false,
	    success: function(data) {
	    	for (var i = 0; i < data.length; i++) {
	            var type = data[i];
	            technicianOptionList += '<option value="'+type.technicianId+'">'+type.name+'</option>';
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
	    url:'/admin-portal/control/getContractorTechnicians?externalLoginKey=${requestAttributes.externalLoginKey!}',
	    async: false,
	    success: function(data) {
	    	for (var i = 0; i < data.length; i++) {
	            var type = data[i];
	            if(selTechnician && selTechnician != type.technicianId){
	            	technicianOptionList += '<option value="'+type.technicianId+'">'+type.name+'</option>';
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
	    url:'/admin-portal/control/getContractorTechnicians?externalLoginKey=${requestAttributes.externalLoginKey!}',
	    async: false,
	    success: function(data) {
	    	for (var i = 0; i < data.length; i++) {
	            var type = data[i];
	            if(selTechnician && selTechnician != type.technicianId && selTechnician1 != type.technicianId){
	            	technicianOptionList += '<option value="'+type.technicianId+'">'+type.name+'</option>';
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
	    url:'/admin-portal/control/getContractorTechnicians?externalLoginKey=${requestAttributes.externalLoginKey!}',
	    async: false,
	    success: function(data) {
	    	for (var i = 0; i < data.length; i++) {
	            var type = data[i];
	            if(selTechnician && selTechnician != type.technicianId && selTechnician1 != type.technicianId && selTechnician2 != type.technicianId){
	            	technicianOptionList += '<option value="'+type.technicianId+'">'+type.name+'</option>';
	            }
	        }
	    }
	});
	
	$("#technician4").html(technicianOptionList);
    $("#technician4").dropdown('refresh');
}

</script>