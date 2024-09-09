<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://customer-portal/webapp/customer-portal/call-list-customer/create_phone_number.ftl"/>
<#include "component://customer-portal/webapp/customer-portal/call-list-customer/modal_window.ftl"/>
<script src="/bootstrap/js/ckeditor/ckeditor.js" type="text/javascript"></script>
<script src="/bootstrap/js/ckeditor/ck-custom-functions.js" type="text/javascript"></script>
<#include "component://common-portal/webapp/common-portal/lib/picker_macro.ftl"/>
<script type="text/javascript" src="/account-portal-resource/js/emailActivity.js"></script>
<div class="row">
	<div id="main" role="main" class="pd-btm-title-bar">
		<div class="col-lg-12 col-md-12 col-sm-12">
			<div class="card-head margin-adj mt-2" id="view-detail">
				<div class="col-lg-12 col-md-12 dot-line">
					<div class="row">
						<div class="col-lg-6 col-md-6">
							<h3 class="float-left mr-2 mb-0 header-title view-title">Customer Purchase Summary</h3>
						</div>
						<div class="col-lg-6 col-md-6">
							<#-- <a href="findCustomer" class="btn btn-xs btn-primary float-right text-right"><i class="fa fa-chevron-circle-left"></i> Back</a>-->
						</div>
					</div>
				</div>
				<@AppBar  
					appBarId="CUST_CALL_ACTION"
					appBarTypeId="ACTION"
					id="appbar1"
					extra=extra!
					toggleDropDownData=toggleDropDownData!
					isEnableUserPreference=true
					/>
			</div>
			<#assign partyId = '${requestParameters.partyId!}' >
			<@inputHidden name="partyId" id="partyId" value = "${partyId!}"/>
			<div class="card-head margin-adj mt-2">
				<div class="row	">
					<div class="col-lg-12 col-md-12">
					<h3 class="float-left mr-2 mb-0 header-title view-title">KPI Metrics
						<#if isEnableDashboardButton?has_content && isEnableDashboardButton =="Y"> <a title="view" href="/dashboard-portal/control/viewPartyDashboard?partyId=${partyId!}&requestUri=${request.getRequestURI()!}&externalLoginkey=${requestAttributes.externalLoginKey!}" target="_blank" class="btn btn-primary btn-xs ml-2 float-right"> View Dashboard </a></#if>
					</h3>
					</div>
				</div>
				<@AppBar
					appBarId="CUST_CALL_KPI_BAR"
					appBarTypeId="KPI"
					id="kpi-metrics"
					isEnableUserPreference=true
					/>
			</div>
			<@navTab
				instanceId="VIEW_CALL_LIST_CUSTOMER"
				activeTabId="c-profile"
				/>
		</div>
	</div>
</div>
<div class="modal fade mt-5 save-modal" id="writeEmail" tabindex="-1" role="dialog" aria-labelledby="writeEmail" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <h3 class="modal-title">Write Email</h3>
                <button type="reset" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body">
            	<form id="activityEmail" name="activityEmail" method="post">
            	<div class="col-md-12 col-lg-12 col-sm-12" id="emlContent">
                <#assign userLoginEmail=""/>		                     				
        		<#assign userloginContact=Static["org.groupfio.common.portal.util.PartyPrimaryContactMechWorker"].getPartyPrimaryContactMechValueMaps(delegator, userLogin.partyId, Static["org.ofbiz.base.util.UtilMisc"].toMap("isRetriveEmail", true),true)!>
        		<#if userloginContact?has_content> 
					<#assign userLoginEmail=userloginContact.get("EmailAddress")!/>
					<@inputHidden name="loginEmail" id="loginEmail" value="${userLoginEmail!}" />
				</#if>
                <#assign cifNo = '${requestParameters.partyId!}' >
            	<#assign partyId = '${requestParameters.partyId!}' >
            	<#assign campaignListId = '${requestParameters.campaignListId!}' >
            	<#assign externalLoginKey = '${requestParameters.externalLoginKey!}' >
                <@inputHidden name="partyId" id="partyId" value = "${partyId!}"/>
                <@inputHidden name="cNo" id="cNo" value = "${cifNo!}"/>
                <@inputHidden id="workEffortTypeId" value="E-mail"/>
                <@inputHidden id="domainEntityType" value="CUSTOMER"/>
                <@inputHidden id="domainEntityId" value="${partyId!}"/>
                <@inputHidden id="campaignListId" value="${campaignListId!}"/>
                <@inputHidden id="marketingCampaignId" value="${marketingCampaignId!}"/>
                <@inputHidden id="contactListId" value="${contactListId!}"/>
                <@inputHidden id="externalLoginKey" value="${externalLoginKey!}"/>
                
            	<@dynaScreen
					instanceId="CREATE_CUST_EMIL_ACTVT_CUST" 
					modeOfAction="CREATE" 
					/>
                    
                    <@textareaLarge
		               id="emailContent"
		               groupId = "htmlDisplay"
		               label=uiLabelMap.HTML
		               rows="2"
		               value = template
		               required = false
		               txareaClass = "ckeditor"
		               />
				<script>          
				    CKEDITOR.replace( 'emailContent',{
				    	customConfig : '/bootstrap/js/ckeditor/ck-custom-config.js'
			        });
				</script>
				
		 	
				<div class="row padding-r">
                    	<div class="col-md-6 col-sm-6">
                    	<@inputRowFilePicker 
						id="attachment"
						label="Attachments"
						placeholder="Select Attachment"
						/>
                    	</div>
                    </div>
				</div>
            </div>
            <div class="modal-footer">
                <button type="submit" class="btn btn-sm btn-primary">Send</button>
                <button class="btn btn-sm btn-secondary" data-dismiss="modal" type="button">Cancel</button>
            </div>
            </form>
        </div>
    </div>
</div>
<@templatePicker 
	instanceId="templatePicker"
	/>
<div>
	<@reassignPicker 
		instanceId="partyResponsible"
		/>
	<@editBirthDayDate 
		instanceId="birthdayDatePopup"
		/>
	<@updatePhoneNumber 
		instanceId="update-phone-number"
		/>
</div>	
<#if activeMessType?has_content>
	<#include "component://messenger-portal/webapp/messenger-portal/messenger/place_call.ftl"/>
</#if>	
	
<script>

$(document).ready(function() {
	CKEDITOR.config.height = 150;
	$('#phone').on('click', function(e) {
		$('#createTELECOMcontactInfo').modal('show');
	});

	
	$("#10244").click(function() {
		$('#writeEmail').modal("show");
	});
	var cNo = $("#cNo").val();
	if (cNo != null && cNo != undefined && cNo != "") {
		loadContacts();
		//loadCcContacts();
	}
	var defaultFrom = $('#loginEmail').val();
	if (defaultFrom != null && defaultFrom != "" & defaultFrom != undefined) {
		var defaultLoggedInUserEmail = '<option value="' + defaultFrom + '" selected>' + defaultFrom + '</option>';
		$("#nsender").html(defaultLoggedInUserEmail);
		$("#nsender").dropdown('refresh');
	}
	
$('.ab-im-notes').click(function() {
	location.hash = "#c-notes";
	$('.nav-tabs a[href="#c-notes"]').tab('show');
	loadTabContent("VIEW_CALL_LIST_CUSTOMER", "c-notes", function() {
		$("#note-search-form #isImportant").val("Y");
		$("#note-search-btn").click();
		var target = $('#nav-tab-focus');
		if (target.length) {
			$('html,body').animate({
				scrollTop: target.offset().top
			}, 1000);
			return false;
		}
	});
});

$('.addNotes').click(function() {
	location.hash = "#c-notes";
	$('.nav-tabs a[href="#c-notes"]').tab('show');
	loadTabContent("VIEW_CALL_LIST_CUSTOMER", "c-notes", function() {
		$("#create-note-btn").click();
		var target = $('#nav-tab-focus');
		if (target.length) {
			$('html,body').animate({
				scrollTop: target.offset().top
			}, 1000);
			return false;
		}
	});
});

    $("#activityEmail").submit(function () {
    	event.preventDefault();
    	$("#writeEmail").modal('hide');
    	$.ajax({
			type : "POST",
			url : "addEmailEvent",
			data: $('#activityEmail').serialize(),
			async : true,
			success : function(result) {
				showAlert ("success", "Email sent successfully");
			},
			error : function() {
				showAlert("error", "Error occured!");
			},
			complete : function() {
			}
		});
    });
});
function loadContacts() {
	var dataSourceOptions = "";
	var ntoOptions = "";
	var partyId = $("#partyId_val").val();
	$.ajax({
		type: "POST",
		url: "/common-portal/control/getPrimaryContacts",
		data: {
			"partyId": partyId,
			"toEmailDD": "Y",
			"externalLoginKey": "${requestAttributes.externalLoginKey!}"
		},
		async: false,
		success: function(data) {
			if (data) {
				if (data.responseMessage == "success") {
					for (var i = 0; i < data.partyRelContacts.length; i++) {
						var entry = data.partyRelContacts[i];
						if (entry.selected != null) {
							dataSourceOptions += '<option value="' + entry.contactId + '" selected>' + entry.name + '</option>';
						} else {
							dataSourceOptions += '<option value="' + entry.contactId + '">' + entry.name + '</option>';
						}
					}
					for (var i = 0; i < data.toMapList.length; i++) {
						var entry = data.toMapList[i];
						if (entry != null) {
							if (entry.selected != null) {
								ntoOptions += '<option value="' + entry.EmailAddress + '" selected>' + entry.EmailAddress + '</option>';
								$('#contactId').val(entry.primaryContactId);
							} else {
								ntoOptions += '<option value="' + entry.EmailAddress + '">' + entry.EmailAddress + '</option>';
								if (i == 0) {
									$('#contactId').val(entry.primaryContactId);
								}
							}
						}
					}
				} else {
					for (var i = 0; i < data.length; i++) {
						var entry = data[i];
						dataSourceOptions += '<option value="' + entry.contactId + '">' + entry.name + '</option>';
					}

				}
			}
		}

	});

	var custNtoOptions = "";
	var custNtoOptions = '<option value="' + '${actionBarContext.primaryEmail!}' + '" selected>' + '${actionBarContext.primaryEmail!}' + '</option>';
	$("#nto").html(custNtoOptions);

	$("#nto").dropdown('refresh');
}
function submitActivityForm(){
	
}
</script>
<style>
#writeEmail .modal-content {
    height: 33rem;
}
</style>