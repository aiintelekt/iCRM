<#assign requestURI = request.getRequestURI()/>
<#if requestURI.contains("screenRender")>
<#assign requestURI=request.getParameter("requestUri")!>
</#if>
<#if Static["org.fio.homeapps.util.DataUtil"].isPhoneCampaignEnabled(delegator)?if_exists=="Y">
	<#assign headerGridTitle = "List of CSRs">
	<#assign modalTitle = "Find CSRs">
<#else>
	<#assign headerGridTitle = "List of Users">
	<#assign modalTitle = "Find User">
</#if>
<#macro reassignPicker instanceId fromAction="updatePersonResponsibleParty">
<div  id="${instanceId!}" class="modal fade bd-example-modal-lg" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel" aria-hidden="true">
	<div class="modal-dialog modal-lg" style="max-width: 1200px;">
		<div class="modal-content">
			<div class="modal-header">
				<h2 class="modal-title">${modalTitle!}</h2>
				<button type="button" class="close" data-dismiss="modal" id="close">&times;</button>
			</div>
			<div class="modal-body" style="padding-bottom: 8px;">
				<div class="card-header popup-bot">
					<form  method="post" id="searchReassignForm" name="searchReassignForm">
						<@inputHidden 
							id="externalLoginKey"
							value="${requestAttributes.externalLoginKey!}"
							/>
							<input type="hidden" name="marketingCampaignId" value="${marketingCampaignId?if_exists}"/>
							<input type="hidden" name="contactListId" value="${contactListId?if_exists}"/>
							<#if Static["org.fio.homeapps.util.DataUtil"].isPhoneCampaignEnabled(delegator)?if_exists=="Y">
								<input type="hidden" name="activeTeamMember" value="Y"/>
							</#if>
						<div class="row">
							<div class="col-md-6 col-sm-6">
								<@dropdownCell 
									id="roleTypeId"
									placeholder="${uiLabelMap.Role}"
									options=reassignOwnerList!
									value="CUST_SERVICE_REP"
									/>
							</div>
							<div class="col-md-2 col-sm-2">
								<@button
									id="sr-reassign-search-btn"
									label="${uiLabelMap.Find}"
									/>
							</div>
						</div>
					</form>
				</div>
			<#-- <@AgGrid
					gridheadertitle=headerGridTitle!
					gridheaderid="${instanceId!}_user-list-container"
					savePrefBtn=true
					clearFilterBtn=true
					exportBtn=false
					insertBtn=false
					updateBtn=false
					removeBtn=false
					refreshPrefBtnId="reassignPicker-refresh-pref-btn"
					savePrefBtnId="reassignPicker-save-pref-btn"
					clearFilterBtnId="reassignPicker-clear-filter-btn"
					userid="${userLogin.userLoginId}" 
					shownotifications="true" 
					instanceid="PICKER_USERS_LIST" 
					autosizeallcol="true"
					debug="false"
					/>
				<script type="text/javascript" src="/common-portal-resource/js/ag-grid/picker/find-users.js"></script>-->
	<input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
  	<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
				  <@fioGrid
						instanceId="PICKER_USERS_LIST"
						jsLoc="/common-portal-resource/js/ag-grid/picker/find-users.js"
						headerLabel=""
						headerId=headerGridTitle!
						savePrefBtnId="reassignPicker-save-pref"
						clearFilterBtnId="reassignPicker-clear-pref"
						subFltrClearId="reassignPicker-clear-sub-ftr"
						headerBarClass="grid-header-no-bar"
						serversidepaginate=false
						statusBar=false
						savePrefBtn=false
						clearFilterBtn=false
						exportBtn=false
						subFltrClearBtn=false
						/>
			</div>
		</div>
	</div>
</div>
<form name="personResponsibleParty" id="personResponsibleParty" method="POST" action="<@ofbizUrl>${fromAction}</@ofbizUrl>" style="display:none;">
	<input type="hidden" name="partyId" value="${partyId?if_exists}"/>
	<input type="hidden" name="campaignListId" value="${campaignListId?if_exists}"/>
	<input type="hidden" name="marketingCampaignId" value="${marketingCampaignId?if_exists}"/>
	<input type="hidden" name="contactListId" value="${contactListId?if_exists}"/>
	<input type="hidden" name="accountPartyId" value=""/>
	<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
	<#if requestURI.contains("/customer-portal/control/outBoundCallList") >
		<input type="hidden" name="toRM" id="toRM" />
		<input type="hidden" name="partyLists" id="partyLists" />
		<input type="hidden" name="campaignId"/>
		<input type="hidden" name="csrPartyId"/>
		<input type="hidden" name="callStatus"/>
		<input type="hidden" name="noOfDaysSinceLastCall"/>
		<input type="hidden" name="callBackDate"/>
		<input type="hidden" name="defaultTimeZoneId"/>
	</#if>
</form>
<script>
	$(document).ready(function() {
	
	$('#${instanceId!}').on('shown.bs.modal', function (e) {
		$("#sr-reassign-search-btn").trigger('click');
	});
	});
	function reassignParty(value) {
		if (value != null && value != "") {
			$("#${instanceId!}").modal('hide');
			$("#personResponsibleParty input[name=accountPartyId]").val(value);
			<#if requestURI.contains("/customer-portal/control/outBoundCallList") >
			$("#personResponsibleParty input[name=toRM]").val(value);
			$('#personResponsibleParty input[name="campaignId"]').val($("#campaignId").val());
			$('#personResponsibleParty input[name="csrPartyId"]').val($("#csrPartyId").val());
			$('#personResponsibleParty input[name="callStatus"]').val($("#callStatus").val());
			$('#personResponsibleParty input[name="noOfDaysSinceLastCall"]').val($("#noOfDaysSinceLastCall").val());
			$('#personResponsibleParty input[name="callBackDate"]').val($("#callBackDate").val());
			$('#personResponsibleParty input[name="defaultTimeZoneId"]').val($("#defaultTimeZoneId").val());
			</#if>
			document.personResponsibleParty.submit();
		}
	}
</script>
</#macro>
<style>
#birthdayDatePopup.expanded {
    width: 400px; 
    height: 400px;
}
.birthDayDate-extend-height {
    z-index: 2;
    position: sticky;
}
#updateRMOrAM.modal-content {
    height: 300px;
    overflow-x: hidden;
    overflow-y: scroll;
}
</style>
<#macro editBirthDayDate instanceId>
<div  id="${instanceId!}" class="modal fade bd-example-modal-lg" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel" aria-hidden="true">
	<div class="modal-dialog modal-lg" style="max-width: 450px;">
		<div class="modal-content">
			<div class="modal-header">
				<h4 class="modal-title">Update Birth Date</h4>
				<button type="button" class="close" data-dismiss="modal" id="close">&times;</button>
			</div>
			<div class="modal-body" style="padding-bottom: 8px;">
			<#if requestURI?contains("viewCustomer")>
				<form method="post" id="updateBirthdayDate" action="<@ofbizUrl>updateBirthDate</@ofbizUrl>" name="updateBirthdayDate">
			<#else>
				<form method="post" id="updateBirthdayDate" action="<@ofbizUrl>updateBirthdayDate</@ofbizUrl>" name="updateBirthdayDate">
			</#if>
					<@inputHidden id="externalLoginKey" value="${requestAttributes.externalLoginKey!}" />
					<@inputHidden id="partyId" value="${inputContext.partyId!}" />
					<@inputHidden id="campaignListId" value="${campaignListId?if_exists}" />
					<input type="hidden" name="marketingCampaignId" value="${marketingCampaignId?if_exists}"/>
					<input type="hidden" name="contactListId" value="${contactListId?if_exists}"/>
					<br>
					<#-- 
					<@inputDate 
						id="birthDayDate" 
						label="Birth Date" 
						dateFormat="yyyy-MM-dd" 
						value="${inputContext.birthDate!}"
						labelColSize="col-sm-4" 
						inputColSize="col-sm-8"
						/> -->
					<@customDatePicker 
						id="birthDayDate" 
						label="Birth Date" 
						value="${inputContext.birthDate!}"
						labelColSize="col-sm-4" 
						inputColSize="col-sm-8"
						/>
					<br>
					<div class="modal-footer" id="addFooter">
						<div class="text-left ml-1">
							<input type="submit" class="btn btn-sm btn-primary disabled" value="Update" id="update-birth-date"/>
							<button type="submit" class="btn btn-sm btn-primary navbar-dark" data-dismiss="modal">Cancel</button>
						</div>
					</div>
				</form>
			</div>
		</div>
	</div>
</div>
<script>
	$(document).ready(function() {
		$("#birthDayDate_picker").click(function() {
			$('#birthDayDate_error').html('');
		});
		$("#update-birth-date").click(function() {
			let birthdayDate = $("#updateBirthdayDate #birthDayDate_custom").val();
			if(birthdayDate !="" && birthdayDate !=null){
				$('#birthDayDate_error').html('');
				$("#updateBirthdayDate").submit();
			}else{
				event.preventDefault();
				$('#birthDayDate_error').html('Please select birth date');
			}
		});
		$("#birthDayDate_picker").click(function() {
			const fieldId = "birthDayDate";
			const $field = $('#' + fieldId).next();
			$field.addClass('birthDayDate-extend-height');
		});
	});
</script>
</#macro>

<#macro updatePhoneNumber instanceId>
<div  id="${instanceId!}" class="modal fade bd-example-modal-lg" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel" aria-hidden="true">
	<div class="modal-dialog modal-lg" style="max-width: 600px;">
		<div class="modal-content">
			<div class="modal-header">
				<h4 class="modal-title">Update Phone Number</h4>
				<button type="button" class="close" data-dismiss="modal" id="close">&times;</button>
			</div>
			<div class="modal-body" style="padding-bottom: 8px;">
			<form method="post" action="<@ofbizUrl>updateTelecomNumber</@ofbizUrl>" class="form-horizontal" id="updatePhoneNumberForm" novalidate="novalidate" data-toggle="validator">

					<@inputHidden id="externalLoginKey" value="${requestAttributes.externalLoginKey!}" />
					<@inputHidden id="campaignListId" value="${campaignListId?if_exists}" />
					<input type="hidden" name="marketingCampaignId" value="${marketingCampaignId?if_exists}"/>
					<input type="hidden" name="contactListId" value="${contactListId?if_exists}"/>
					<#list contactMeches as contactMechMap>
					<#assign contactMech = contactMechMap.contactMech>
					<#assign partycontactMech = contactMechMap.partyContactMech>
					<#assign test=contactMech.contactMechId>
					<#if contactMech.contactMechId ==  partycontactMech.contactMechId && contactMech.contactMechTypeId == "TELECOM_NUMBER">
					</#if></#list>
                     <input type="hidden" name="donePage" value='${requestURI?if_exists}'/>
                     <input type="hidden" id="contactMechId" name="contactMechId" value="${inputContext.contactNumberContactMechId!}"/>
                     <input type="hidden" name="partyId" value="${partyId?if_exists}"/>
			         <input type="hidden" id="accType" name="accType" value="CUSTOMER">
                     
					<@inputRow 
						id="contactNumber"
						name="contactNumber"
						label=uiLabelMap.phoneNumber
						placeholder=uiLabelMap.phoneNumber
						value="${inputContext.primaryTelecomNumber?if_exists}"
						required=true
						/>
                     </form>
					<div class="modal-footer" id="addFooter">
						<div class="text-left ml-1">
							<input type="submit" class="btn btn-sm btn-primary disabled" value="Update" id="update-phone"/>
							<button type="submit" class="btn btn-sm btn-primary navbar-dark" data-dismiss="modal">Close</button>
						</div>
					</div>
				</form>
			</div>
		</div>
	</div>
</div>
<script>
$(document).ready(function() {
    $("#updatePhoneNumberForm #contactNumber").keyup(function(e) {
        let inputValues = $(this);
        validateUpdatePhoneNumber(inputValues);
    });

    $("#update-phone").click(function(event) {
        let phoneNumber = $("#updatePhoneNumberForm #contactNumber").val();

        if (phoneNumber != "" && phoneNumber != null) {
            if (validateUpdatePhoneNumber()) {
                event.preventDefault();
            } else {
                $("#updatePhoneNumberForm").submit();
            }
        } else {
            event.preventDefault();
            $('[id="updatePhoneNumberForm"] #contactNumber_error').html('Please Enter Phone Number');
        }
    });
});

function validateUpdatePhoneNumber() {
    var isInvalid = false;
    var contactNumberValue = $('[id="updatePhoneNumberForm"] #contactNumber').val();
    var re = new RegExp("^[0-9]{1,11}$");
    if (contactNumberValue != "") {
        var numberCount = contactNumberValue.replace(/[^0-9]/g, '').length;
        if (numberCount < 10 || numberCount > 10) {
            event.preventDefault();
            $('[id="updatePhoneNumberForm"] #contactNumber_error').html('Please Enter only 10 digits');
            isInvalid = true;
        } else {
            if (!re.test(contactNumberValue)) {
                event.preventDefault();
                $('[id="updatePhoneNumberForm"] #contactNumber_error').html('Please Enter only 10 digits');
                isInvalid = true;
            } else {
                $('[id="updatePhoneNumberForm"] #contactNumber_error').html('');
            }
        }
    } else {
        if ($('[id="updatePhoneNumberForm"] #contactNumber_error') != undefined)
            $('[id="updatePhoneNumberForm"] #contactNumber_error').html('');
    }

    return isInvalid;
}

</script>
</#macro>