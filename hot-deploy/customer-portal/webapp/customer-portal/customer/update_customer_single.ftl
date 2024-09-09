<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/lib/picker_macro.ftl"/>

<#if isLoyaltyEnable?has_content && isLoyaltyEnable =="Y">
<#include "component://loyalty-portal/webapp/loyalty-portal/picker/picker/picker.ftl"/>
</#if>

<div class="row">
	<div id="main" role="main">
		<#assign extra='<a href="/customer-portal/control/viewCustomer?partyId=${inputContext.partyId!}" class="btn btn-xs btn-primary back-btn">
		<i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>' />
		<div class="clearfix"></div>
		<form id="mainFrom" method="post" action="<@ofbizUrl>editCustomerAction</@ofbizUrl>" data-toggle="validator"> 
			<#assign partyId = "${parameters.partyId?if_exists}"/>
	        <@inputHidden id="partyId" value="${parameters.partyId?if_exists}"/>
	        <@inputHidden id="contactMechId" value="${inputContext.postalContactMechId?if_exists}"/>
	        <@inputHidden id="emailContactMechId" value="${inputContext.emailContactMechId?if_exists}"/>
	        <@inputHidden id="contactNumberContactMechId" value="${inputContext.contactNumberContactMechId?if_exists}"/>
	       
	        <@inputHidden id="stateGeoIdV" value="${inputContext.stateGeoId?if_exists}"/>
	        <@inputHidden id="state" value="${inputContext.state?if_exists}"/>
	        <#--  <@inputHidden id="postalCode" value="${partySummary.primaryPostalCode?if_exists}"/>-->
			
			<div class="col-lg-12 col-md-12 col-sm-12">
				<@sectionFrameHeader title="${uiLabelMap.UpdateCustomer!} - Single Page" extra=extra />
				
				<h4 class="bg-light pl-1 mt-2">Personal Information </h4> 
				<@dynaScreen
					instanceId="UPDATE_CUST_BASE"
					modeOfAction="UPDATE"
					/>
					
				<h4 class="bg-light pl-1 mt-2">Contact Information </h4>
				<@dynaScreen 
					instanceId="CREATE_CUST_CNTC"
					modeOfAction="UPDATE"
					/>	
					
				<h4 class="bg-light pl-1 mt-2">Address </h4>
				<@dynaScreen 
					instanceId="SINGLE_PAGE_ADDR"
					modeOfAction="UPDATE"
					/>	
			</div>
			
			<div class="clearfix"></div>
			<div class="offset-md-2 col-sm-10">
				<@submit label="${uiLabelMap.Update}" onclick="return formSubmissionEdit();"/>
				<@cancel label="${uiLabelMap.Cancel}" onclick="/customer-portal/control/viewCustomer?partyId=${inputContext.partyId!}"/>
			</div>
		</form>
	</div>
</div>

<div id="submitAddressModal" class="modal fade" role="dialog">
	<div class="modal-dialog modal-md">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<span id="AD_message"></span>
					<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
				</div>
				<div>
				</div>
				<div class="modal-footer">
					<input type="submit" class="btn btn-sm btn-primary navbar-dark"  value="Update" onclick="mainFrom.submit();">
					<input type="button" class="btn btn-sm btn-primary navbar-dark" data-dismiss="modal" value="Cancel" onclick="return false;">
				</div>
			</div>
		</div>
	</div>
</div>

<#if isLoyaltyEnable?has_content && isLoyaltyEnable =="Y">
<@localStorePreferencePicker
	instanceId="localStorePreferencePicker"
	/>
 <@loyaltyStorePicker
	instanceId="loyaltyStorePicker"
	/>
</#if>

<script>

var regex = '';

$(document).ready(function() {
	var countryGeoId = $('#editCountryGeoId').val();
	$(function() {
		var stateGeoId = $('#stateGeoIdV').val();
		var state = $('#state').val();
		var list = "";
		if (countryGeoId != null && countryGeoId != "") {
			var urlString = "/common-portal/control/getStateDataJSON?countryGeoId=" + countryGeoId + "&externalLoginKey=${requestAttributes.externalLoginKey!}";
			$.ajax({
				type: 'POST',
				async: true,
				url: urlString,
				success: function(states) {
					$('[id="mainFrom"] #editStateProvinceGeoId').empty();
					list = $('[id="mainFrom"] #editStateProvinceGeoId');
					list.append("<option value=''>Select State</option>");
					if (states.length == 0) {
						list.append("<option value = ''>N/A</option>");
					} else {
						for (var i = 0; i < states.length; i++) {
							if (stateGeoId != null && stateGeoId != "" && states[i].geoId == stateGeoId) {
								list.append("<option  value =" + states[i].geoId + " selected>" + states[i].geoName + " </option>");
							} else {
								list.append("<option  value =" + states[i].geoId + ">" + states[i].geoName + "</option>");
							}
						}
					}
				}
			});
			$('[id="mainFrom"] #editStateProvinceGeoId').append(list);
			$('[id="mainFrom"] #editStateProvinceGeoId').dropdown('refresh');
		}
	});

	console.log('countryGeoId in edit====', countryGeoId);
	if (countryGeoId != '') {
		regexJson = getServiceResult("getZipCodeRegex", 'countryGeoId', countryGeoId);
		regex = regexJson.regex;
		console.log('regex', regex);
	}
	
	$(".editCountryGeoId-input").one( "click",function(){
		getDependentDropdownValues('getAssociatedStateList', 'countryGeoId', 'editCountryGeoId', 'editStateProvinceGeoId', 'stateList', 'geoId', 'geoName', '${stateValue!}');
	});

	$('#editCountryGeoId').change(function(e, data) {
		$('[id="mainFrom"] #editStateProvinceGeoId').dropdown("clear");
		$('#generalPostalCode').val('');
		$('#generalPostalCodeExt').val('');
		getDependentDropdownValues('getAssociatedStateList', 'countryGeoId', 'editCountryGeoId', 'editStateProvinceGeoId', 'stateList', 'geoId', 'geoName', '${stateValue!}');
		var countryGeoId = $('#editStateProvinceGeoId').val();
		if (countryGeoId != '') {
			regexJson = getServiceResult("getZipCodeRegex", 'countryGeoId', countryGeoId);
			regex = regexJson.regex;
		} else {
			$('[id="mainFrom"] #editStateProvinceGeoId').html('<option value="">Please Select</option>');
		}
	});

	$('#generalPostalCode').keyup(function(e) {
		validatePostalCodeEdit();
	});

	$('#generalPostalCodeExt').keyup(function(e) {
		validatePostalCodeExtEdit();
	});
	
	$("#localStorePreference_desc").keyup(function() {
		$("#localStorePreference_val").val("");
	});
	$("#loyaltyStoreId_desc").keyup(function() {
		$("#loyaltyStoreId_val").val("");
	});
	
});

function formSubmissionEdit() {
	var firstName = $('#firstName').val();
	var lastName = $('#lastName').val();
	var primaryPhoneNumber = $('#primaryPhoneNumber').val();
	var primaryEmail = $('#primaryEmail').val();
	var generalPostalCode = $('#generalPostalCode').val();
	var partyId = $('#partyId').val();

	$.ajax({
		type: "POST",
		url: "/common-portal/control/getDuplicateEmailList",
		async: false,
		data: {
			"firstName": firstName,
			"lastName": lastName,
			"primaryEmail": primaryEmail,
			"generalPostalCode": generalPostalCode,
			"screenType": "UPDATE",
			"partyId": partyId,
			"accType": "CUSTOMER",
			"externalLoginKey": "${requestAttributes.externalLoginKey!}"
		},
		success: function(data) {
			var message = data.Error_Message;
			loadActivityEdit(message);
		}
	});
	return false;
}

function loadActivityEdit(message) {
	if (message === "NO_RECORDS") {
		this.mainFrom.submit();
	} else {
		$('#submitEditModal').modal('show');
		$("#Edit_message").html(message);
	}
}

function validatePostalCodeEdit() {
	var isInvalid = false;
	//alert(regex);
	if (regex != '') {
		var re = new RegExp(regex);
		if (re.test($('#generalPostalCode').val())) {
			console.log(regex, $('#generalPostalCode').val(), 'regex1');
			$('#generalPostalCode_error').html('');
		} else {
			console.log(regex, $('#generalPostalCode').val(), 'regex2');
			$('#generalPostalCode_error').html('Please enter the valid zip code');
			isInvalid = true;
		}
	}
	return isInvalid;
}

function validatePostalCodeExtEdit() {
	var isInvalid = false;
	if ($('#generalPostalCodeExt').val() != "" && $('#generalCountryGeoId').val() == "USA") {
		var re = new RegExp("^([0-9]{4})$");
		console.log(regex, $('#generalPostalCodeExt').val(), 'regex1');
		if (re.test($('#generalPostalCodeExt').val())) {
			$('#generalPostalCodeExt_error').html('');
		} else {
			$('#generalPostalCodeExt_error').html('Please enter the valid zip code extension');
			isInvalid = true;
		}
	} else
		$('#generalPostalCodeExt_error').html('');
	return isInvalid;
}

function getTeamMembersPRF(loggedInUserId, loggedInUserRole, loggedInUserName) {
	var userOptionList;
	userOptionList = '<option value="' + loggedInUserId + '">' + loggedInUserName + '</option>';
	$("#personResponsible").html(userOptionList);
}

let assignedStore = "${assignedStore!}";
let localStorePreference = "${localStorePreference!}";
let loyaltyStoreName = "${loyaltyStoreName!}";
let assignedStoreValue = assignedStore.replaceAll("&#x29;", ")").replaceAll("&#x28;", "(");
let localStorePreferenceValue = localStorePreference.replaceAll("&#x29;", ")").replaceAll("&#x28;", "(");
let loyaltyStoreNameValue = loyaltyStoreName.replaceAll("&#x29;", ")").replaceAll("&#x28;", "(");
$('#assignedStore_desc').val(assignedStoreValue);
$('#assignedStore_val').val('${assignedStoreId!}');
$('#localStorePreference_desc').val(localStorePreferenceValue);
$('#localStorePreference_val').val('${localStorePreferenceId!}');
$('#loyaltyStoreId_desc').val(loyaltyStoreNameValue);
$('#loyaltyStoreId_val').val('${loyaltyStoreId!}');

</script>	