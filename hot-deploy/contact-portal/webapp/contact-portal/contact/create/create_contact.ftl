<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/lib/picker_macro.ftl"/>

<div class="row">
    <div id="main" role="main">

        <#assign extra='<a href="/contact-portal/control/findContact" class="btn btn-xs btn-primary back-btn">
        <i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>' />
        <#--<@sectionFrameHeader title="${uiLabelMap.CreateContact!}" extra=extra />-->
        <div class="clearfix"></div>
        <form id="mainFrom" method="post" action="<@ofbizUrl>createContactAction</@ofbizUrl>" data-toggle="validator">
        	<@inputHidden id="accountPartyIdd" value="${parameters.accountPartyId?if_exists}"/>    
            <div class="col-lg-12 col-md-12 col-sm-12">
            	
            	<@sectionFrameHeader title="${uiLabelMap.CreateContact!}" extra=extra />
            	
            	<@dynaScreen 
					instanceId="CREATE_CONT_BASE"
					modeOfAction="CREATE"
					/>
            	
                <h2 class="font-h2">Contact Information </h2>
                
                <@dynaScreen 
					instanceId="CREATE_CONT_CNTC"
					modeOfAction="CREATE"
					/>
                
                <h2 class="font-h2">Address </h2>
                
                <@dynaScreen 
					instanceId="CREATE_CONT_ADDR"
					modeOfAction="CREATE"
					/>
                
            </div>
            <div id="submitModal" class="modal fade">
		    <div class="modal-dialog">
		      <div class="modal-content">
            	<div class="modal-header">
            	<span id="message"></span>
	               <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
	            	</div>
		            <div>
		            </div>
            		<div class="modal-footer">
		              <input type="submit" class="btn btn-sm btn-primary navbar-dark"  value="Create" onclick="createWithDuplicateAddress();">
		              <input type="button" class="btn btn-sm btn-primary navbar-dark" data-dismiss="modal" value="Cancel" onclick="return false;">
			        </div>
			      </div>
			   </div>
			</div> 
            <div class="clearfix"></div>
            <div class="offset-md-2 col-sm-10">
         
            <@formButton
                     btn1type="submit"
                     btn1label="${uiLabelMap.Save}"
                     btn1onclick=""
                     btn2=true
                     btn2onclick = "resetForm()"
                     btn2type="reset"
                     btn2label="${uiLabelMap.Clear}"
                   />
            </div>
        </form>
    </div>
</div>

<@partyPicker 
	instanceId="partyPicker"
	/>
	
<script>

var regex = '';
var isUspsVarified = false;

$(document).ready(function() {

var countryGeoId = $('#generalCountryGeoId').val();console.log('countryGeoId', countryGeoId);
regexJson = getServiceResult("getZipCodeRegex",'countryGeoId', countryGeoId);
regex = regexJson.regex;
console.log('regex', regex);
if($('#generalCountryGeoId').val()) {
	 getDependentDropdownValues('getAssociatedStateList', 'countryGeoId', 'generalCountryGeoId', 'generalStateProvinceGeoId', 'stateList', 'geoId', 'geoName', '${stateValue!}');
}
	
$('#generalCountryGeoId').change(function(e, data) {
	$('#generalPostalCode').val('');
	$('#generalPostalCodeExt').val('');
	getDependentDropdownValues('getAssociatedStateList', 'countryGeoId', 'generalCountryGeoId', 'generalStateProvinceGeoId', 'stateList', 'geoId', 'geoName','${stateValue!}');
	var countryGeoId = $('#generalCountryGeoId').val();
	if(countryGeoId != ''){
		regexJson = getServiceResult("getZipCodeRegex",'countryGeoId', countryGeoId);
		regex = regexJson.regex;
	} else {
		$('#generalStateProvinceGeoId').html('<option value="">Please Select</option>');
	}
});	

$('#generalPostalCode').keyup(function(e){
	validatePostalCode();
});

$('#generalPostalCodeExt').keyup(function(e){
	validatePostalCodeExt();
});

$('#mainFrom').validator().on('submit', function (e) {
	if (e.isDefaultPrevented()) {
    	// handle the invalid form...
  	} else {
  	
  		if (isUspsVarified) {
  			return true;
  		}
  		isUspsVarified = false;
  		var valid = true;
  		
		if(validatePostalCode() || validatePostalCodeExt()){
	    	valid = false;
		}
		
		var firstName = $('#firstName').val();
		var lastName = $('#lastName').val();
		var primaryPhoneNumber = $('#primaryPhoneNumber').val();
		var primaryEmail = $('#primaryEmail').val();
		var generalPostalCode = $('#generalPostalCode').val();
		
		$.ajax({
        	type: "POST",
        	url : "/common-portal/control/getDuplicatePartyList",
        	async: false,
         	data: { "firstName": firstName,"lastName": lastName,"primaryPhoneNumber": primaryPhoneNumber,
         		"name": name,"primaryEmail": primaryEmail,
         		"generalPostalCode": generalPostalCode,"accType" : "CONTACT", "externalLoginKey": "${requestAttributes.externalLoginKey!}"},
        	success: function(data) {
        		var message = data.Error_Message;
        		if(!(message === "NO_RECORDS")){
        			valid = false;
			     	$('#submitModal').modal('show');
			        $("#message").html(message);
			  	}
        	}
      	});
      	
      	<#if isActUspsAddrVal ? has_content && isActUspsAddrVal == "Y" >
		if (valid && $('#generalCountryGeoId').val() == "USA") {
			var data = {
				"Address1": "generalAddress1",
				"Address2": "generalAddress2",
				"Zip5": "generalPostalCode",
				"Zip4": "generalPostalCodeExt",
				"City": "generalCity",
				"State": "generalStateProvinceGeoId",
				"Business": "isBusiness",
				"Vacant": "isVacant"
			};
			valid = USPSUTIL.performUspsAddrValidation(data, '${requestAttributes.externalLoginKey!}');
		} 
		</#if>
		
  		if (!valid) {
  			e.preventDefault();
  		}
  	}
});

loadZipCodeAssoc();
$("#generalStateProvinceGeoId").change(function() {	
	loadZipCodeAssoc();
});	
$("#generalPostalCode").change(function() {	
	loadZipCodeAssoc();
});

loadPostalInfo();
$("#accountPartyId_desc").on("change", function() {
	console.log('accountPartyId_desc change');
	loadPostalInfo();
});

function validatePostalCode(){
	var isInvalid = false;
	if(regex != ''){
		var re = new RegExp(regex);
		if (re.test($('#generalPostalCode').val())) {console.log(regex, $('#generalPostalCode').val(), 'regex1');
      		$('#generalPostalCode_error').html('');
      	}else{console.log(regex, $('#generalPostalCode').val(), 'regex2');
      		$('#generalPostalCode_error').html('Please enter the valid zip code');
      		isInvalid = true;
      	}
  	}
  	return isInvalid;
}

function validatePostalCodeExt(){
	var isInvalid = false;
	if($('#generalPostalCodeExt').val() != "" && $('#generalCountryGeoId').val()=="USA"){
		var re = new RegExp("^([0-9]{4})$");console.log(regex, $('#generalPostalCodeExt').val(), 'regex1');
		if (re.test($('#generalPostalCodeExt').val())) {
	  		$('#generalPostalCodeExt_error').html('');
	  	}else{
	  		$('#generalPostalCodeExt_error').html('Please enter the valid zip code extension');
	  		isInvalid = true;
	  	}
  	}
  	else
  		$('#generalPostalCodeExt_error').html('');
  	return isInvalid;
}
});

var loadPostalInfo = () => {
	console.log('calling loadPostalInfo');
	var cityOptions = '<option value="" selected="">Select City</option>';
	var countyOptions = '<option value="" selected="">Select County</option>';
	$("#generalCity").html( cityOptions );
	$("#countyGeoId").html( countyOptions );
	$("#generalStateProvinceGeoId").dropdown('clear');
	$("#generalCity").dropdown('clear');
    $("#countyGeoId").dropdown('clear');
    
    if ($("#accountPartyId_val").val()) {
    	$.ajax({
			type: "POST",
	     	url: "/common-portal/control/getPartyPostal",
	        data: {"partyId": $("#accountPartyId_val").val(), "externalLoginKey": "${requestAttributes.externalLoginKey!}"},
	        async: false,
	        success: function (result) {   
	            if (result.code == 200) {
	            	var postal = result.data;
	            	
	        		$('#attnName').val(postal.attnName);
	            	$('#generalAddress1').val(postal.address1);
					$('#generalAddress2').val(postal.address2);
					$('#generalCountryGeoId').val(postal.countryGeoId);
					$('#generalStateProvinceGeoId').val(postal.stateProvinceGeoId).change();	
					$('#generalPostalCode').val(postal.postalCode).change();
					$('#generalPostalCodeExt').val(postal.postalCodeExt).change();
					$('#generalCity').val(postal.city).change();
					$('#countyGeoId').val(postal.county);
	            }
	        }
		}); 
    } else {
    	console.log('empty postal info');
    	$('#attnName').val('');
    	$('#generalAddress1').val('');
		$('#generalAddress2').val('');
		//$('#generalCountryGeoId').val(postal.countryGeoId);
		$('#generalStateProvinceGeoId').val('').change();	
		$('#generalPostalCode').val('').change();
		$('#generalPostalCodeExt').val('').change();
		$('#generalCity').val('').change();
		$('#countyGeoId').val('');
    }
}

function createWithDuplicateAddress() {
	var valid = true;
	$('#submitModal').modal('hide');
	<#if isActUspsAddrVal ? has_content && isActUspsAddrVal == "Y" >
	if ($('#generalCountryGeoId').val() == "USA") {
		var data = {
			"Address1": "generalAddress1",
			"Address2": "generalAddress2",
			"Zip5": "generalPostalCode",
			"Zip4": "generalPostalCodeExt",
			"City": "generalCity",
			"State": "generalStateProvinceGeoId",
			"Business": "isBusiness",
			"Vacant": "isVacant"
		};
		valid = USPSUTIL.performUspsAddrValidation(data, '${requestAttributes.externalLoginKey!}');
	} 
	</#if>
	
	isUspsVarified = true;
	if (valid) {
		$('#mainFrom').submit();
	}
}

function loadZipCodeAssoc() {
	
	if (!$("#generalStateProvinceGeoId").val()) {
		return;
	}
	var cityOptions = '<option value="" selected="">Select City</option>';
	var countyOptions = '<option value="" selected="">Select County</option>';
	var zipOptions = '<option value="" selected="">Select Zip Code</option>';	
	
	let cityList = new Map();
	let countyList = new Map();
	let zipList = new Map();	
								
	$.ajax({
		type: "POST",
     	url: "/uiadv-portal/control/searchZipCodeAssocs",
        data: {"state": $("#generalStateProvinceGeoId").val(), "zip": $("#generalPostalCode").val(), "externalLoginKey": "${requestAttributes.externalLoginKey!}"},
        async: false,
        success: function (result) {   
            if (result.code == 200) {
            	for (var i = 0; i < result.data.length; i++) {
					var data = result.data[i];
					cityList.set(data.city, data.city);
					countyList.set(data.county, data.county);
					//zipList.set(data.zip, data.zip); 
				}
            }
        }
	});   
	
	for (let key of cityList.keys()) {
		if(cityList.size===1){
			cityOptions += '<option value="'+key+'" selected>'+cityList.get(key)+'</option>';
		} else {
			cityOptions += '<option value="'+key+'">'+cityList.get(key)+'</option>';
		}
	}
	for (let key of countyList.keys()) {
		if(countyList.size===1){			
			countyOptions += '<option value="'+key+'" selected>'+countyList.get(key)+'</option>';
		} else {
  			countyOptions += '<option value="'+key+'">'+countyList.get(key)+'</option>';
  		}	
	}
	
	$("#generalCity").html( cityOptions );
	$("#generalCity").dropdown('refresh');
	
	$("#countyGeoId").html( countyOptions );
	$("#countyGeoId").dropdown('refresh');
}
</script>	