<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/lib/picker_macro.ftl"/>

<#if isLoyaltyEnable?has_content && isLoyaltyEnable =="Y">
<#include "component://loyalty-portal/webapp/loyalty-portal/picker/picker/picker.ftl"/>
</#if>
<#assign isShowHelpUrl="Y">
	<#if isPhoneCampaignEnabled?has_content && isPhoneCampaignEnabled?if_exists=="Y">
	<#assign isShowHelpUrl="N">
	</#if>
<div class="row">
	<div id="main" role="main">
		<#assign extra='<a href="/customer-portal/control/viewCustomer?partyId=${inputContext.partyId!}" class="btn btn-xs btn-primary back-btn">
		<i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>' />
		<div class="clearfix"></div>
		<form id="mainFrom" method="post" action="<@ofbizUrl>updateCustomerAction</@ofbizUrl>" data-toggle="validator" >
			<#assign partyId = "${parameters.partyId?if_exists}"/>
			<@inputHidden id="partyId" value="${parameters.partyId?if_exists}"/>
			<#assign partySummaryDetailsView = (delegator.findOne("PartySummaryDetailsView", {"partyId" : partyId}, false))?if_exists/>
			<@inputHidden id="postalCode" value="${partySummaryDetailsView.primaryPostalCode?if_exists}"/>
			<div class="col-lg-12 col-md-12 col-sm-12">
				<@sectionFrameHeader title="${uiLabelMap.UpdateCustomer!}" extra=extra  isShowHelpUrl=isShowHelpUrl!/>
				<@dynaScreen
					instanceId="UPDATE_CUST_BASE"
					modeOfAction="UPDATE"
					/>
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
								<input type="submit" class="btn btn-sm btn-primary navbar-dark"  value="Update" onclick="mainForm.submit();">
								<input type="button" class="btn btn-sm btn-primary navbar-dark" data-dismiss="modal" value="Cancel" onclick="return false;">
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="clearfix"></div>
			<div class="offset-md-2 col-sm-10">
				<input type="submit" class="btn btn-sm btn-primary navbar-dark"  value="Update" onclick="return formSubmission();">
				<@cancel label="${uiLabelMap.Cancel}" onclick="/customer-portal/control/viewCustomer?partyId=${inputContext.partyId!}"/>
				<#--  <@formButton
				btn1type="submit"
				btn1label="${uiLabelMap.Save}"
				btn2=false
				btn2onclick = "resetForm()"
				btn2type="reset"
				btn2label="${uiLabelMap.Clear}"
				/>-->
			</div>
		</form>
	</div>
</div>

<#-- 
<@accountPicker 
	instanceId="parentAccountModal"
	/>
 -->
<#if isLoyaltyEnable?has_content && isLoyaltyEnable =="Y">
<@localStorePreferencePicker
	instanceId="localStorePreferencePicker"
	/>
 <@loyaltyStorePicker
	instanceId="loyaltyStorePicker"
	/>
</#if>
<script>
	//added for duplicate records check paddress

	function formSubmission(){
		var firstName = document.getElementById("firstName").value;
		var lastName = document.getElementById("lastName").value;
		if(firstName == "" || lastName == "")
			return false;
		var postalCode = document.getElementById("postalCode").value;
		var partyId = document.getElementById("partyId").value;
		var accType = "CUSTOMER";
		var screenType = "UPDATE";
		$.ajax({
			type: "POST",
			url: "/common-portal/control/getDuplicateAddress",
			data: {
				"firstName": firstName,
				"lastName": lastName,
				"accType": accType,
				"partyId": partyId,
				"postalCode": postalCode,
				"screenType": screenType
			},
			async: false,
			success: function(data) {
				var message = data.Error_Message;
			}
		});
		if(message === "NO_RECORDS"){
			mainFrom.submit();
		}else{
			$('#submitAddressModal').modal('show');
			$("#AD_message").html(message);
		}
		return false;
	}

	let assignedStore = "${assignedStore!}";
	let localStorePreference = "${localStorePreference!}";
	let loyaltyStoreName = "${loyaltyStoreName!}";
	let assignedStoreValue = assignedStore.replaceAll("&#x29;", ")").replaceAll("&#x28;", "(");
	let localStorePreferenceValue = localStorePreference.replaceAll("&#x29;",")").replaceAll("&#x28;","(");
	let loyaltyStoreNameValue = loyaltyStoreName.replaceAll("&#x29;",")").replaceAll("&#x28;","(");
		$('#assignedStore_desc').val(assignedStoreValue);
		$('#assignedStore_val').val('${assignedStoreId!}');
		$('#localStorePreference_desc').val(localStorePreferenceValue);
		$('#localStorePreference_val').val('${localStorePreferenceId!}');
		$('#loyaltyStoreId_desc').val(loyaltyStoreNameValue);
		$('#loyaltyStoreId_val').val('${loyaltyStoreId!}');
		
	$(document).ready(function(){
		$("#localStorePreference_desc").keyup(function() {
			$("#localStorePreference_val").val("");
		});
		$("#loyaltyStoreId_desc").keyup(function() {
			$("#loyaltyStoreId_val").val("");
		});
	});
</script>