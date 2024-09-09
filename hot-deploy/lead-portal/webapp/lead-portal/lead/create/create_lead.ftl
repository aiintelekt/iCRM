<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/lib/picker_macro.ftl"/>

<div class="row">
    <div id="main" role="main">

        <#assign extra='<a href="#"  class="btn btn-xs btn-primary back-btn" onclick="window.history.back();">
        <i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>' />
        <#-- <@sectionFrameHeader title="${uiLabelMap.CreateLead!}" extra=extra />  -->
        <div class="clearfix"></div>
        <form id="mainFrom" method="post" action="<@ofbizUrl>createLeadAction</@ofbizUrl>" data-toggle="validator">    
            <div class="col-lg-12 col-md-12 col-sm-12">
            <@sectionFrameHeader title="${uiLabelMap.CreateLead!}" extra=extra />
            <@inputHidden id="statusId" value="LEAD_UNIVERSE"/>
            <#assign roleTypeId = "">
            <#assign userName = "">
			<@inputHidden id="loggedInUserId" value="${userLogin.partyId?if_exists}" />
            <#assign findMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", userLogin.partyId)>
            <#assign person = delegator.findOne("Person", findMap, true)!>
            <#if person?has_content>
            	<#assign userName = (person.firstName!) + " " + (person.middleName!) + " " + person.lastName!>
            	<@inputHidden id="userName" value="${userName!}"/>
            </#if>
			<#assign roleList = (Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("roleTypeId").from("PartyRole").where("partyId",userLogin.partyId).queryFirst())?if_exists />
			<#if roleList?has_content>
				<#assign roleTypeId = "${roleList.roleTypeId?if_exists}">
				<@inputHidden id="userLoginRole" value="${roleTypeId}"/>
			</#if>
			
            	<@dynaScreen 
					instanceId="CREATE_LEAD_BASE"
					modeOfAction="CREATE"
					/>
            	
                <h2 class="font-h2">Contact Information </h2>
                
                <@dynaScreen 
					instanceId="CREATE_CNTC"
					modeOfAction="CREATE"
					/>
                
                <h2 class="font-h2">Address </h2>
                
                <@dynaScreen 
					instanceId="CREATE_LEAD_ADDR"
					modeOfAction="CREATE"
					/>
                
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

<@accountPicker 
	instanceId="parentAccount"
	/>

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
          <input type="submit" class="btn btn-sm btn-primary navbar-dark"  value="Create" onclick="mainFrom.submit();">
          <input type="button" class="btn btn-sm btn-primary navbar-dark" data-dismiss="modal" value="Cancel" onclick="return false;">
        </div>
      </div>
   </div>
</div> 

<script>

var regex = '';

$(document).ready(function() {

var loggedInUserRole  = $("#userLoginRole").val();
var loggedInUserId  = $("#loggedInUserId").val();
var loggedInUserName  = $("#userName").val();
getTeamMembersPRF(loggedInUserId,loggedInUserRole,loggedInUserName);
var countryGeoId = $('#generalCountryGeoId').val();console.log('countryGeoId', countryGeoId);
regexJson = getServiceResult("getZipCodeRegex",'countryGeoId', countryGeoId);
regex = regexJson.regex;
console.log('regex', regex);
if($('#generalCountryGeoId').val()) {
	 getDependentDropdownValues('getAssociatedStateList', 'countryGeoId', 'generalCountryGeoId', 'generalStateProvinceGeoId', 'stateList', 'geoId', 'geoName', '${stateValue!}');
}
	
$('#generalCountryGeoId').change(function(e, data) {
	$("#generalStateProvinceGeoId").dropdown('clear');
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
  		var valid = true;
  		
		if(validatePostalCode() || validatePostalCodeExt()){
	    	valid = false;
		}
		
		var name = $('#leadName').val();
		var primaryPhoneNumber = $('#primaryPhoneNumber').val();
		var primaryEmail = $('#primaryEmail').val();
		var generalPostalCode = $('#generalPostalCode').val();
		
		$.ajax({
        	type: "POST",
        	url : "/common-portal/control/getDuplicatePartyList",
        	async: false,
         	data: { "primaryPhoneNumber": primaryPhoneNumber,
         		"name": name, "primaryEmail": primaryEmail,
         		"generalPostalCode": generalPostalCode, "accType" : "LEAD", "externalLoginKey" : "${requestAttributes.externalLoginKey!}"
         		},
        	success: function(data) {
        		var message = data.Error_Message;
        		if(!(message === "NO_RECORDS")){
        			valid = false;
			     	$('#submitModal').modal('show');
			        $("#message").html(message);
			  	}
        	}
      	});
		
  		if (!valid) {
  			e.preventDefault();
  		}
  	}
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
var re = new RegExp(regex);
if (re.test($('#generalPostalCode').val())) {console.log(regex, $('#generalPostalCode').val(), 'regex1');
	$('#generalPostalCode_error').html('');
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

function getTeamMembersPRF(loggedInUserId,loggedInUserRole,loggedInUserName) {
	var userOptionList;
	userOptionList = '<option value="'+loggedInUserId+'">'+loggedInUserName+'</option>';
   	$("#personResponsible").html(userOptionList);
}

</script>