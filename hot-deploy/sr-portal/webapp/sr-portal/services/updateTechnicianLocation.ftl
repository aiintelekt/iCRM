<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<div class="row">
   <div id="main" role="main">
		
      <div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
      	
	  <@sectionFrameHeaderTab title="Update Technician Location" />
	      
	  <form name="updateTechnicianLocation" action="updateTechnicianLocationAction" method="post" data-toggle="validator">
       	<@inputHidden id="selectedCountryGeoId" value="${countryGeoId?if_exists}"/>
        <@dynaScreen 
            instanceId="CREATE_TECHNICIAN_LOCATION"
            modeOfAction="UPDATE"
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
var technician1= '${inputContext?if_exists.technician1!}';
var technician2= '${inputContext?if_exists.technician2!}';
var technician3= '${inputContext?if_exists.technician3!}';
var technician4= '${inputContext?if_exists.technician4!}';
var locationId = '${inputContext?if_exists.productStoreId!}';
$(document).ready(function () {
	
	//$("#generalStateProvinceGeoId").prop('disabled', true);
	//$('.ui.dropdown.generalStateProvinceGeoId').addClass("disabled");
	
	var countryGeoId = $('#selectedCountryGeoId').val();
	
	regexJson = getServiceResult("getZipCodeRegex", 'countryGeoId', countryGeoId);
	regex = regexJson.regex;
	if (countryGeoId) {
		getDependentDropdownValues('getAssociatedStateList', 'countryGeoId', 'selectedCountryGeoId', 'generalStateProvinceGeoId', 'stateList', 'geoId', 'geoName', '${generalStateProvinceGeoId!}');
		$("div.ui.dropdown.search.form-control.fluid.show-tick.generalStateProvinceGeoId.selection > i").addClass("clear");
		
		$('#generalCountryGeoId').val(countryGeoId);
		//$("#generalCountryGeoId").dropdown('refresh');
		$('#generalCountryGeoId').trigger('change');
		console.log($('#generalCountryGeoId').val());
	}

	loadZipCodeAssoc();
	getCoordinatorList();
	getProductStores();
	getTechListForTech1();
	getTechListForTech2();
	getTechListForTech3();
	getTechListForTech4();
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
	            if(locationId && locationId === type.storeId) {
	            	storeOptionList += '<option value="'+type.storeId+'" selected>'+type.storeName+'</option>';
	            	$("div.ui.dropdown.search.form-control.fluid.show-tick.productStoreId.selection > i").addClass("clear");
	            }
	            else {
	            	storeOptionList += '<option value="'+type.storeId+'">'+type.storeName+'</option>';
	            }
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
		    	if(technician2 && technician2 === type.partyId) continue; 
		    	else if(technician3 && technician3 === type.partyId) continue;
		    	else if(technician4 && technician4 === type.partyId) continue;
		    		
	    		if(technician1 && technician1 === type.partyId) {
	            	technicianOptionList += '<option value="'+type.partyId+'" selected>'+type.userName+'</option>';
	            	$("div.ui.dropdown.search.form-control.fluid.show-tick.technician1.selection > i").addClass("clear");
	            }	
	            else {
	            	technicianOptionList += '<option value="'+type.partyId+'">'+type.userName+'</option>';	
	    		}
	        }
	    }
	});
	
	$("#technician1").html(technicianOptionList);
	$("#technician1").dropdown('refresh');
	
}

function getTechListForTech2(){
	
	var technicianOptionList = '<option value=""></option>';
	$.ajax({
	    type: "GET",
	    url:'/common-portal/control/getUsersList?roleTypeId=TECHNICIAN&externalLoginKey=${requestAttributes.externalLoginKey!}',
	    async: false,
	    success: function(data) {
	    	for (var i = 0; i < data.length; i++) {
		    	var type = data[i];
		    	if(technician1 && technician1 === type.partyId) continue; 
		    	else if(technician3 && technician3 === type.partyId) continue;
		    	else if(technician4 && technician4 === type.partyId) continue;
		    		
	    		if(technician2 && technician2 === type.partyId) {
	            	technicianOptionList += '<option value="'+type.partyId+'" selected>'+type.userName+'</option>';
	            	$("div.ui.dropdown.search.form-control.fluid.show-tick.technician2.selection > i").addClass("clear");
	            }	
	            else {
	            	technicianOptionList += '<option value="'+type.partyId+'">'+type.userName+'</option>';	
	    		}
	        }
	    }
	});
	$("#technician2").html(technicianOptionList);
	$("#technician2").dropdown('refresh');
	
}

function getTechListForTech3(){
	
	var technicianOptionList = '<option value=""></option>';
	$.ajax({
	    type: "GET",
	    url:'/common-portal/control/getUsersList?roleTypeId=TECHNICIAN&externalLoginKey=${requestAttributes.externalLoginKey!}',
	    async: false,
	    success: function(data) {
	    	for (var i = 0; i < data.length; i++) {
		    	var type = data[i];
		    	if(technician1 && technician1 === type.partyId) continue; 
		    	else if(technician2 && technician2 === type.partyId) continue;
		    	else if(technician4 && technician4 === type.partyId) continue;
		    		
	    		if(technician3 && technician3 === type.partyId) {
	            	technicianOptionList += '<option value="'+type.partyId+'" selected>'+type.userName+'</option>';
	            	$("div.ui.dropdown.search.form-control.fluid.show-tick.technician3.selection > i").addClass("clear");
	            }
	            else {
	            	technicianOptionList += '<option value="'+type.partyId+'">'+type.userName+'</option>';	
	    		}
	        }
	    }
	});
	
	$("#technician3").html(technicianOptionList);
	$("#technician3").dropdown('refresh');
	
}

function getTechListForTech4(){
	
	var technicianOptionList = '<option value=""></option>';
	$.ajax({
	    type: "GET",
	    url:'/common-portal/control/getUsersList?roleTypeId=TECHNICIAN&externalLoginKey=${requestAttributes.externalLoginKey!}',
	    async: false,
	    success: function(data) {
	    	for (var i = 0; i < data.length; i++) {
		    	var type = data[i];
		    	if(technician1 && technician1 === type.partyId) continue; 
		    	else if(technician2 && technician2 === type.partyId) continue;
		    	else if(technician3 && technician3 === type.partyId) continue;
		    		
	    		if(technician4 && technician4 === type.partyId) {
	            	technicianOptionList += '<option value="'+type.partyId+'" selected>'+type.userName+'</option>';
	            	$("div.ui.dropdown.search.form-control.fluid.show-tick.technician4.selection > i").addClass("clear");
	            }
	            else {
	            	technicianOptionList += '<option value="'+type.partyId+'">'+type.userName+'</option>';	
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
	$('.ui.dropdown.countyGeoId').addClass("disabled");
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
	var selectedCounty = "${generalCountyGeoId!}";
	for (let key of countyList.keys()) {
		if(selectedCounty  && selectedCounty === key){
			countyOptions += '<option value="'+key+'" selected>'+countyList.get(key)+'</option>';
			$("div.ui.dropdown.search.form-control.fluid.show-tick.countyGeoId.selection > i").addClass("clear");
		}
		else
			countyOptions += '<option value="'+key+'">'+countyList.get(key)+'</option>';
	}
	
	$("#countyGeoId").html( countyOptions );
	$("#countyGeoId").dropdown('refresh');
}

</script>