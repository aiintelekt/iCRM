<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl" />
<#include "component://common-portal/webapp/common-portal/lib/picker_macro.ftl" />
<form id="mainForm" method="post" action="<@ofbizUrl>editUserAction</@ofbizUrl>" data-toggle="validator">
	<@inputHidden id="userName" name="userName" value="${userData.userLoginId!}" />
	<@inputHidden id="userPartyId" value="${userData.partyId?if_exists}" />
	<@inputHidden id="contactMechId" value="${inputContext.postalContactId?if_exists}" />
	<@inputHidden id="emailContactMechId" value="${inputContext.emailContactId?if_exists}" />
	<@inputHidden id="contactNumberContactMechId" value="${inputContext.phoneContactId?if_exists}" />
	<@inputHidden id="stateProvinceGeoId" value="${inputContext.generalStateProvinceGeoId?if_exists}" />
	<@inputHidden id="state" value="${inputContext.state?if_exists}" />
	<#assign partySummaryDetailsView=(delegator.findOne("PartySummaryDetailsView", {"partyId" : partyId}, false))?if_exists />
	<@inputHidden id="postalCode" value="${partySummaryDetailsView.primaryPostalCode?if_exists}" />
	<#assign helpUrl=Static["org.groupfio.common.portal.util.DataHelper"].getTabHelpUrl(delegator, request.getRequestURI(), "contactInfo" ) />
	<@pageSectionHeader title="Edit User Information" extra='
                        <div class="float-right">
                            <p> <span>${helpUrl?if_exists}</span> </p>
                        </div>
                        ' />
	<h4 class="bg-light pl-1 mt-2">Personal Information </h4>
	<@dynaScreen
		instanceId="UPDATE_USER_BASE"
		modeOfAction="UPDATE"
		/>
	<h4 class="bg-light pl-1 mt-2">Contact Information </h4>
	<@dynaScreen
		instanceId="CREATE_USER_CNTC"
		modeOfAction="UPDATE"
		/>
	<h4 class="bg-light pl-1 mt-2">Address </h4>
	<@dynaScreen
		instanceId="CREATE_USER_ADDR"
		modeOfAction="UPDATE"
		/>
	<div class="clearfix"></div>
	<div class="offset-md-2 col-sm-10">
		<@formButton
			btn1type="submit"
			btn1label="${uiLabelMap.Update}"
			btn2=true
			btn2id="contact-reset-btn"
			btn2type="reset"
			btn2label="${uiLabelMap.Clear}"
			/>
	</div>
</form>
<script>
	var regex = '';
	$(document).ready(function() {
		//$(function() {
			var statusId = $('#statusIdVal').val();
			var userStatus = $('#userStatus').val();
			var userOptionList = '<option value="'+statusId+'">'+userStatus+'</option>';
		//});
		//$(function() {
			var stateGeoId = $('#stateGeoId').val();
			var state = $('#state').val();
			$("#stateGeoId").html('<option value="'+stateGeoId+'">'+state+'</option>');
		//});
		var countryGeoId = $('#generalCountryGeoId').val();
		console.log('countryGeoId', countryGeoId);
		console.log('state', '${inputContext.stateGeoId!}');
		if($('#generalCountryGeoId').val()) {
			getDependentDropdownValues('getAssociatedStateList', 'countryGeoId', 'generalCountryGeoId', 'stateGeoId', 'stateList', 'geoId', 'geoName', '${inputContext.stateGeoId!}');
			regexJson = getServiceResult("getZipCodeRegex",'countryGeoId', $('#generalCountryGeoId').val());
			regex = regexJson.regex;
		}
		$('#generalCountryGeoId').change(function(e, data) {
			$('#generalPostalCode').val('');
			$('#generalPostalCodeExt').val('');
			getDependentDropdownValues('getAssociatedStateList', 'countryGeoId', 'generalCountryGeoId', 'stateGeoId', 'stateList', 'geoId', 'geoName','${inputContext.stateGeoId!}');
			var countryGeoId = $('#generalCountryGeoId').val();
			if(countryGeoId != ''){
				regexJson = getServiceResult("getZipCodeRegex",'countryGeoId', countryGeoId);
				regex = regexJson.regex;
			} else {
				$('#stateGeoId').html('<option value="">Please Select</option>');
			}
		});
		$('#generalPostalCode').keyup(function(e) {
			validatePostalCodeEdit();
		});
		$('#generalPostalCodeExt').keyup(function(e) {
			validatePostalCodeExtEdit();
		});
	});
	
	function validatePostalCodeEdit(){
		var isInvalid = false;
		//alert(regex);
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
	
	function validatePostalCodeExtEdit(){
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
</script>