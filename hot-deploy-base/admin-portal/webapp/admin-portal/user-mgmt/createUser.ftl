<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl" />
<div class="row">
	<div id="main" role="main">
		<#assign extra='<a href="findUser" class="btn btn-xs btn-primary"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>' />
		<#assign extraLeft='' />
		<form name="createUser" action="createNewUser" method="post" data-toggle="validator">
			<@inputHidden id="roleTypeId" value="EMPLOYEE" />
			<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
				<@sectionFrameHeader title="${uiLabelMap.CreateUser}" extra=extra?if_exists />
				<@dynaScreen
					instanceId="CREATE_USER"
					modeOfAction="CREATE"
					/>
				<#-- <@inputRow id="businessUnit" label="${uiLabelMap.BU}" placeholder="${uiLabelMap.BU}" required=true /> -->
				<#--<@dropdownCell id="nationalityId" label="${uiLabelMap.Nationality}" placeholder="${uiLabelMap.Nationality}" options=nationalityList! allowEmpty=true />
				<@inputRow id="relationshipBranch" label="${uiLabelMap.RelationshipBranch}" placeholder="${uiLabelMap.RelationshipBranch}" />-->
				<#-- <@dropdownCell id="securityGroupId" label="${uiLabelMap.securityGroup}" placeholder="${uiLabelMap.securityGroup}" options=securityGroupList! allowEmpty=true /> -->
				<h2 class="font-h2">Login Information </h2>
				<@dynaScreen
					instanceId="LOGIN_INFO"
					modeOfAction="CREATE"
					/>
				<#-- <@checkbox id="isLdapUser" label="${uiLabelMap.isLDAP}" checked=true value="Y" /> -->
				<h2 class="font-h2">Contact Information </h2>
				<@dynaScreen
					instanceId="CONTACT_INFORMATION"
					modeOfAction="CREATE"
					/>
				<#-- <@inputRow id="state" label="${uiLabelMap.StateOrProvince}" placeholder="${uiLabelMap.StateOrProvince}" /> -->
				<#-- <@inputRow id="country" label="${uiLabelMap.CountryOrRegion}" placeholder="${uiLabelMap.CountryOrRegion}" /> -->
				<div class="clearfix"></div>
				<div class="row">
					<div class="form-group offset-2">
						<div class="text-left ml-3">
							<@formButton btn1type="submit" btn1label="${uiLabelMap.Save}" btn2=true btn2type="reset" btn2label="${uiLabelMap.Clear}" />
						</div>
					</div>
				</div>
			</div>
		</form>
	</div> <#-- main end -->
</div> <#-- row end-->

<script type="text/javascript">
    $(document).ready(function(){
        $('input[name="isLdapUser"]').click(function(){
            if($(this).is(":checked")){
                $("#password_row").hide();
                $("#isLdapUser").val('Y');
            }
            else if($(this).is(":not(:checked)")){
                $("#password_row").show();
                $("#password").prop('required',true);
                $("#isLdapUser").val('N');
            }
        });
    });
    /*
    $('input[type="checkbox"]').each(function(){
		var $t=$(this);
		var isset = $t.prop('checked') ? 'Y' : 'N';
   		$('input[name="'+$t.attr('name')+'"]').val(isset);
  	}); */
$(document).ready(function(){
	if($('#countryGeoId').val()) {
		getDependentDropdownValues('getAssociatedStateList', 'countryGeoId', 'countryGeoId', 'stateProvinceGeoId', 'stateList', 'geoId', 'geoName','${stateValue!}');
	}
	$('#countryGeoId').change(function(e, data) {
		$("#stateProvinceGeoId").dropdown('clear');
		getDependentDropdownValues('getAssociatedStateList', 'countryGeoId', 'countryGeoId', 'stateProvinceGeoId', 'stateList', 'geoId', 'geoName','${stateValue!}');
		var countryGeoId = $('#countryGeoId').val();
		if(countryGeoId != ''){
			regexJson = getServiceResult("getZipCodeRegex",'countryGeoId', countryGeoId);
			regex = regexJson.regex;
		} else {
			$('#stateProvinceGeoId').html('<option value="">Please Select</option>');
		}
	});	
});
 
  	$("#postalCode").keyup(function(){
		var postalValue = document.getElementById("postalCode").value;
		var valid = /^\d+$/.test(postalValue);
		if(postalValue != '' && valid == false){
			$('#postalCode_error').html($('#postalCode').attr('data-error'));
	 		$('#postalCode_error').show();
	 	}else{
	 		$('#postalCode_error').html($('#postalCode').attr('data-error'));
	 		$('#postalCode_error').hide();
	 	}
  	});
  	
  	$("#contactNumber").keyup(function(){
		var contactValue = document.getElementById("contactNumber").value;
		var valid = /^\d+$/.test(contactValue);
		if(contactValue != '' && valid == false){
			$('#contactNumber_error').html($('#contactNumber').attr('data-error'));
	 		$('#contactNumber_error').show();
	 	}else{
	 		$('#contactNumber_error').html($('#contactNumber').attr('data-error'));
	 		$('#contactNumber_error').hide();
	 	}
  	});
  	$("#extension").keyup(function(){
		var contactValue = document.getElementById("extension").value;
		var valid = /^\d+$/.test(contactValue);
		if(contactValue != '' && valid == false){
			$('#extension_error').html($('#extension').attr('data-error'));
	 		$('#extension_error').show();
	 	}else{
	 		$('#extension_error').html($('#extension').attr('data-error'));
	 		$('#extension_error').hide();
	 	}
  	});
  	
  	$("#city").keyup(function(){
		var cityValue = document.getElementById("city").value;
		var valid = /[`!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?~]/.test(cityValue);
		if(cityValue != '' && valid != false){
			$('#city_error').html($('#city').attr('data-error'));
	 		$('#city_error').show();
	 	}else{
	 		$('#city_error').html($('#city').attr('data-error'));
	 		$('#city_error').hide();
	 	}
  	});
</script>
