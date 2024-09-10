<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://ticket-portal/webapp/ticket-portal/services/findCustomerModal.ftl"/>
<#include "component://sales-portal/webapp/sales-portal/activities-home/modalForActivity.ftl">
<#include "component://common-portal/webapp/common-portal/lib/picker_macro.ftl"/>
<script type="text/javascript" src="/common-portal-resource/js/ag-grid/service-request/service_request.js"></script>
<script src="/bootstrap/js/ckeditor/ckeditor.js" type="text/javascript"></script>
<script src="/bootstrap/js/ckeditor/ck-custom-functions.js" type="text/javascript"></script>

<#assign cifNo = '${requestParameters.partyId!}' >
<#assign fromEmailId = Static["org.fio.homeapps.util.DataUtil"].getGlobalValue(delegator, "FROM_EMAIL_ID")?if_exists!>
<#assign appUrl = Static["org.fio.homeapps.util.DataUtil"].getGlobalValue(delegator, "APP_URL")?if_exists!>
<#assign requiredResolution = Static["org.fio.homeapps.util.DataUtil"].getGlobalValue(delegator, "SR_REQUIRED_RESOLUTION","Y")?if_exists>
<div class="row">
	<div id="main" role="main">
		<@inputHidden id="requiredResolution" value="${requiredResolution!}"/>
		<#assign extra='<button type="submit" class="btn btn-xs btn-primary" onClick="updateSR();">
		<i class="fa fa-save" aria-hidden="true"></i> ${uiLabelMap.SaveAndNew}</button>
		<button type="submit" class="btn btn-xs btn-primary" onClick="return formSubmission();">
		<i class="fa fa-save" aria-hidden="true"></i> Save</button>
		<button type="reset" class="btn btn-xs btn-primary" onClick="resetForm();">
		Clear</button>
		<a href="/ticket-portal/control/findServiceRequests" class="btn btn-xs btn-primary">
		<i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>'/>
		<#-- <@sectionFrameHeader title="${uiLabelMap.AddServiceRequest!}" extra=extra /> -->
		<div class="clearfix"></div>
		<form id="mainFrom" method="post" action="<@ofbizUrl>addServiceRequestEvent</@ofbizUrl>" data-toggle="validator">
			<div class="col-lg-12 col-md-12 col-sm-12">
				<@inputHidden id="statusId" value="LEAD_ASSIGNED"/>
				<@inputHidden id="leadOrAccountPartyId" value="${parameters.partyId?if_exists}"/>
				<#assign partySummaryDetailsView = (delegator.findOne("PartyGroup", {"partyId" : "${parameters.partyId?if_exists}"}, false))?if_exists/>
				<#if partySummaryDetailsView?has_content && partySummaryDetailsView.groupName?has_content>
				<input type="hidden" id="partyName" value="${partySummaryDetailsView.groupName?if_exists}">
				<#else>
				<#assign findMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", "${parameters.partyId?if_exists}")>
				<#assign personName = delegator.findOne("Person", findMap, true)!>
				<#assign partyDetail = delegator.findOne("Party", findMap, true)!>
				<#assign nameVal = (personName.firstName!) + " " + (personName.middleName!) + " " + personName.lastName!>
				<input type="hidden" id="partyName" value="${nameVal?if_exists}">
				<input type="hidden" id="role" value="${partyDetail.roleTypeId?if_exists}">
				</#if>
				<#assign roleTypeId = "">
				<#assign userName = "">
				<#-- <@inputHidden id="owner" value="${userLogin.userLoginId?if_exists}" /> -->
				<@inputHidden id="loggedInUserId" value="${userLogin.userLoginId?if_exists}"/>
				<#assign findMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", userLogin.partyId)>
				<#assign person = delegator.findOne("Person", findMap, true)!>
				<#if person?has_content>
				<#assign userName = (person.firstName!) + " " + (person.middleName!) + " " + person.lastName!>
				<@inputHidden id="userName" value="${userName!}"/>
				</#if>
				<input type="hidden" name="externalLoginKey" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
				<input type="hidden" name="fromEmailId" id="fromEmailId" value="${fromEmailId!}"/>
				<input type="hidden" name="appUrl" id="appUrl" value="${appUrl!}"/>
				<@sectionFrameHeader title="${uiLabelMap.AddServiceRequest!}" extra=extra/>
				<@dynaScreen
					instanceId="SR_BASE_GEN"
					modeOfAction="CREATE"
					/>
			</div>
			<div class="col-md-12 col-lg-12 col-sm-12 ">
				<#-- 
				<@inputArea
					inputColSize="col-sm-12"
					id="description"
					label=uiLabelMap.Description
					rows="10"
					placeholder = uiLabelMap.Description
					/> -->
				<@textareaLarge
					id="description"
					label=uiLabelMap.Description
					rows="5"
					required = false
					txareaClass = "ckeditor"
					/>
				<script>
					CKEDITOR.replace( 'description',{
						customConfig : '/bootstrap/js/ckeditor/ck-custom-config.js'
						});
				</script>
			</div>
			<div class="col-md-12 col-lg-12 col-sm-12 ">
				<#--
				<@inputArea
					inputColSize="col-sm-12"
					id="resolution"
					label=uiLabelMap.Resolution
					rows="10"
					placeholder = uiLabelMap.Resolution
					value=""
					/> -->
				<@textareaLarge
					id="resolution"
					label=uiLabelMap.Resolution
					rows="5"
					required = false
					txareaClass = "ckeditor"
					/>
				<script>
					CKEDITOR.replace( 'resolution',{
						customConfig : '/bootstrap/js/ckeditor/ck-custom-config.js'
					});
					// resize the editor after it has been fully initialized
					//CKEDITOR.on('instanceLoaded', function(e) {e.editor.resize("100%", 400)} );
				</script>
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
							<input type="button" class="btn btn-sm btn-primary navbar-dark" data-dismiss="modal" value="OK" onclick="return false;">
						</div>
					</div>
				</div>
			</div>
			<div class="clearfix"></div>
			<div class="offset-md-2 col-sm-10">
				<@formButton
					btn1type="submit"
					btn1label="${uiLabelMap.SaveAndNew}"
					btn1onclick="return updateSR();"
					btn2=true
					btn2type=""
					btn2label="${uiLabelMap.Save}"
					btn2onclick="return formSubmission();"
					btn3=true
					btn3onclick = "resetForm()"
					btn3type="reset"
					btn3label="${uiLabelMap.Clear}"
					/>
			</div>
		</form>
	</div>
</div>
<@partyPicker 
	instanceId="partyPicker"
	/>

<script>
	function updateSR() {
		formSubmission();
		var statusSR = document.getElementById('srStatusId').value;
		if (statusSR === "SR_CLOSED") {
			var url = "/ticket-portal/control/addservicerequest?externalLoginKey=${requestAttributes.externalLoginKey!}";
			window.open(url);
		}
	}
$(document).ready(function() {
	$(".sourceComponent").hide();
	$(".sourceDocumentId").hide();

	var loggedInUserId = $("#loggedInUserId").val();
	var leadOrAccountPartyId = $("#leadOrAccountPartyId").val();
	if (leadOrAccountPartyId != null && leadOrAccountPartyId != '') {
		$("#cNo_row").find("span.picker-window-erase").css("display", "none");
		$("#cNo_row").find("span.picker-window").css("display", "none");
		$("#cNo").val(leadOrAccountPartyId);
		$("#cNo").attr('readonly', 'readonly');
		$("#cNo_val").val(leadOrAccountPartyId);
		$("#cNo_desc").val($("#partyName").val());
		getPartyRoleTypeId(leadOrAccountPartyId);
		getPrimaryContacts(leadOrAccountPartyId);
	}

	$('#ownerBu').attr('readonly', 'readonly');
	if (loggedInUserId != undefined && loggedInUserId != null) {
		getBusinessUnit(loggedInUserId);
		getUsers();
	}

	$("#srName").on("keydown", function(event) {

		var keyCode = event.keyCode || event.which;

		if (!event.shiftKey && (keyCode == 188 || keyCode == 190)) {
			return false;
		}

		return true;

	});

	<#--$(".srCategoryId-input").one("click", function() {
		loadCategory();
	});-->

	<#if enableCustomCategory?exists && enableCustomCategory?if_exists == "Y">
		$("#srCategoryId").dropdown('clear');
		$("#srCategoryId").html("<span class='nonselect'>Please Select</span>");
		$("#srCategoryId").dropdown('refresh');
		$("#srTypeId").change(function() {
			$("#srCategoryId").dropdown('clear');
			$("#srCategoryId").dropdown('refresh');
			if ($(this).val()) {
				loadCategory();
			}
		});
	</#if>

	$(".srSubCategoryId-input").one("click", function() {
		var srCategoryId = $("#srCategoryId").val();
		loadSubCategory(srCategoryId);
	});
	$("#srCategoryId").change(function() {
		$("#srSubCategoryId").dropdown('clear');
		var srCategoryId = $("#srCategoryId").val();
		if (srCategoryId == "" || srCategoryId == null) {
			$("#srCategoryId_error").show();
		} else {
			$("#srCategoryId_error").hide();
		}

	});
	$("#srSubCategoryId").change(function() {
		var srCategoryId = $("#srCategoryId").val();
		var srSubCategoryId = $("#srSubCategoryId").val();
		if (srSubCategoryId == "" || srSubCategoryId == null) {
			$("#srSubCategoryId_error").show();
		} else {
			$("#srSubCategoryId_error").hide();
		}

	});


});

function formSubmission() {

	var valid = true;
	var srStatusId = document.getElementById('srStatusId').value;
	var resolutionInstance = CKEDITOR.instances.resolution;
	var isReqResolution = $("#requiredResolution").val();
	if (!isReqResolution || (isReqResolution && isReqResolution == "Y")) {
		if (srStatusId === "SR_CLOSED" && isEmptyCKEd(resolutionInstance)) {
			showAlert("error", "Resolution field is mandatory to resolve the SR!");
			return false;
		}
	}

	var srCategoryId = $("#srCategoryId").val();
	var srSubCategoryId = $("#srSubCategoryId").val();
	if (srCategoryId == "" || srCategoryId == null) {
		$("#srCategoryId_error").show();
	} else {
		$("#srCategoryId_error").hide();
	}
	if (srSubCategoryId == "" || srSubCategoryId == null) {
		$("#srSubCategoryId_error").show();
	} else {
		$("#srSubCategoryId_error").hide();
	}

	return valid;
}

</script>
<style>
.ui-menu, .ui-widget, .ui-autocomplete{
	height: 200px;
	overflow-y: scroll;
}
</style>
