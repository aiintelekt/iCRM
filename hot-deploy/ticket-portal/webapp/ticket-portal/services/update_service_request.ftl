<script src="/bootstrap/js/ckeditor/ckeditor.js" type="text/javascript"></script>
<script src="/bootstrap/js/ckeditor/ck-custom-functions.js" type="text/javascript"></script>

<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/lib/picker_macro.ftl"/>
<script type="text/javascript" src="/common-portal-resource/js/ag-grid/service-request/service_request.js"></script>
<#assign copyFlag = requestParameters.copy!>
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
		<a href="/ticket-portal/control/viewServiceRequest?srNumber=${context.custRequestId!}" class="btn btn-xs btn-primary">
		<i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>'/>
		<div class="clearfix"></div>
		<form id="mainFrom" method="post" <#if copyFlag?if_exists == "Y"> action="<@ofbizUrl>addServiceRequestEvent</@ofbizUrl>" <#else> action="<@ofbizUrl>updateServiceRequestAction</@ofbizUrl>" </#if>  data-toggle="validator">
		<div class="col-lg-12 col-md-12 col-sm-12">
			<#if copyFlag?if_exists == "Y">
			<@sectionFrameHeader title="${uiLabelMap.AddServiceRequest!}" extra=extra/>
			<#else>
			<@sectionFrameHeader title="${uiLabelMap.UpdateServiceRequest!}" extra=extra/>
			</#if>
			<@inputHidden id="copyFlag" value="${copyFlag?if_exists}"/>
			<@inputHidden id="srNumber" value="${parameters.srNumber?if_exists}"/>
			<@inputHidden id="selectedContactId" value="${selectedContactId?if_exists}"/>
			<@inputHidden  id="selectedOwnerId" value="${ownerUserLoginId?if_exists}"/>
			<@inputHidden  id="srTypeIdd" value="${srTypeId?if_exists}"/>
			<@inputHidden  id="selectedSrCategoryId" value="${srCategoryId?if_exists}"/>
			<@inputHidden  id="selectedSrSubCategoryId" value="${srSubCategoryId?if_exists}"/>
			<@inputHidden id="loggedInUserId" value="${userLogin.userLoginId?if_exists}"/>
			<@inputHidden id="primContactName" value="${primContactName?if_exists}"/>
			<@inputHidden id="primContactId" value="${primContactId?if_exists}"/>
			<input type="hidden" name="externalLoginKey" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
			<input type="hidden" name="fromEmailId" id="toEmailId" value="${fromEmailId!}"/>
			<input type="hidden" name="appUrl" id="appUrl" value="${appUrl!}"/>
			<#assign findMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", userLogin.partyId)>
			<#assign person = delegator.findOne("Person", findMap, true)!>
			<#if person?has_content>
			<#assign userName = (person.firstName!) + " " + (person.middleName!) + " " + person.lastName!>
			<@inputHidden id="userName" value="${userName!}"/>
			</#if>
			<#if copyFlag?if_exists == "Y">
			<div class="col-lg-12 col-md-12 col-sm-12">
				<@dynaScreen
					instanceId="SR_BASE_GEN"
					modeOfAction="CREATE"
					/>
			</div>
			<#else>
			<div class="col-lg-12 col-md-12 col-sm-12">
				<@dynaScreen
					instanceId="SR_BASE_GEN"
					modeOfAction="UPDATE"
					/>
			</div>
			</#if>
			<div class="col-md-12 col-lg-12 col-sm-12">
				<#--
				<@inputArea
					inputColSize="col-sm-12"
					id="description"
					label=uiLabelMap.Description
					rows="10"
					placeholder = uiLabelMap.Description
					value="${description!}"
					/>
				-->
				<@textareaLarge
					id="description"
					label=uiLabelMap.Description
					rows="5"
					required = false
					txareaClass ="ckeditor"
					value=description!
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
					value="${resolution!}"
					/>
				-->
				<@textareaLarge
					id="resolution"
					label=uiLabelMap.Resolution
					rows="5"
					required = false
					txareaClass = "ckeditor"
					value=resolution!
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
				<#if copyFlag?if_exists == "Y">
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
				<#else>
				<@submit label="${uiLabelMap.SaveAndNew}" onclick="updateSR();"/>
				<@submit label="${uiLabelMap.Save}" onclick="return formSubmission();"/>
				<@cancel label="${uiLabelMap.Cancel}" onclick="/ticket-portal/control/viewServiceRequest?srNumber=${context.custRequestId!}"/>
				</#if>
			</div>
		</div>
		</form>
	</div>
</div>
<@partyPicker 
	instanceId="partyPicker"
	/> 

<script>

	function updateSR(){
		formSubmission();
		var statusSR = document.getElementById('srStatusId').value;
		if (statusSR === "SR_CLOSED"){
			var url = "/ticket-portal/control/addservicerequest?externalLoginKey=${requestAttributes.externalLoginKey!}";
			window.open(url);
		}
	}
$(document).ready(function(){

	var srTypeId = $('#srTypeId').val();
	if ("REEB_REC_INS_OLY" === srTypeId){
		//$("#onceAndDone").prop("checked", true);
		$("input[name=onceAndDone][value='N']").attr('checked', false);
		$("input[name=onceAndDone][value='Y']").attr('checked', true);
		getAllSrStatuses("Y");
	}else{
		$("input[name=onceAndDone][value='Y']").attr('checked', false);
		$("input[name=onceAndDone][value='N']").attr('checked', true);
		var sr_status = $("#srStatusId").val();
		if (sr_status === "SR_CLOSED")
			getAllSrStatuses("N");
	}

	var copyFlag = $("#copyFlag").val();
	if (copyFlag && "Y" == copyFlag){
		$("span.picker-window-erase").css("display", "display");
		$("span.picker-window").css("display", "display");
		$("#orderId").val("");

	}else{
		$("span.picker-window-erase").css("display", "none");
		$("span.picker-window").css("display", "none");
	}
	$('#ownerBu').attr('readonly', 'readonly');

	var partyId = $("#cNo_val").val();

	if (partyId != "") {
		getPrimaryContacts(partyId);
		getPartyRoleTypeId(partyId);
	}

	var selectedOwnerId = $("#selectedOwnerId").val();
	if (selectedOwnerId != "" || selectedOwnerId == ""){
		getUsers();
	}


	var srTypeId = $("#srTypeId").val();
	
	<#if enableCustomCategory?exists && enableCustomCategory?if_exists == "Y">
		if (srTypeId != "" && srTypeId != undefined){
			loadCategory();
		}else{
			$(" #srCategoryId ").html('');
		}
	</#if>
	var srCategoryId = $('#selectedSrCategoryId').val();
	if (srCategoryId != "" && srCategoryId != undefined){
		loadSubCategory(srCategoryId);
	}

	<#if enableCustomCategory?exists && enableCustomCategory?if_exists == "Y">
		$("#srTypeId").change(function(){
			$("#srCategoryId").dropdown('clear');
			$("#srCategoryId").dropdown('refresh');
			if ($(this).val()) {
				loadCategory();
			}
		});
	</#if>

	$(".srSubCategoryId-input").one("click", function(){
		var srCategoryId = $("#srCategoryId").val();
		loadSubCategory(srCategoryId);
	});


	$("div.ui.dropdown.search.form-control.fluid.show-tick.srCategoryId.selection > i").addClass("clear");
	$("div.ui.dropdown.search.form-control.fluid.show-tick.srSubCategoryId.selection > i").addClass("clear");

});

function formSubmission(){
	var srStatusId = document.getElementById('srStatusId').value;
	//var resolution = $('#resolution').val();
	//if(resolution != null) resolution = resolution.trim();
	var resolutionInstance = CKEDITOR.instances.resolution;

	var requiredResolution = $("#requiredResolution").val();
	if (!requiredResolution || (requiredResolution && requiredResolution == "Y")){
		if (srStatusId === "SR_CLOSED" && isEmptyCKEd(resolutionInstance)){
			/*
			var message = "Resolution field is mandatory to resolve the SR !";
			$('#submitModal').modal('show');
			$("#message").html(message);
			*/
			showAlert("error", "Resolution field is mandatory to resolve the SR!");
			return false;
		}
	}
}
<#-- function loadData(){
	
	var partyId = $("#cNo_val").val();
	var srTypeId = $("#srTypeIdd").val();
	var srCategoryId = $("#srCategoryIdd").val();
	var srSubCategoryId = $("#srSubCategoryIdd").val();

	var nonSelectContent = "<span class='nonselect'>Please Select</span>";
	var categoryOptions = '<option value="" data-content="' + nonSelectContent + '" >Please Select</option>';

	$.ajax({
		type: "POST",
		url: "getSrCategory",
		data: {
			"srTypeId": srTypeId
		},
		async: false,
		success: function(data) {
			var sourceDesc = data.results;
			for (var i = 0; i < data.length; i++) {
				var category = data[i];
				if (srCategoryId && srCategoryId === category.srCategoryId) {
					categoryOptions += '<option value="' + category.srCategoryId + '" selected="selected" >' + category.srCategoryDesc + '</option>';
				} else {
					categoryOptions += '<option value="' + category.srCategoryId + '">' + category.srCategoryDesc + '</option>';
				}
			}
		}
	});

	$("#srCategoryId").html(categoryOptions);
	$("#srCategoryId").dropdown('refresh');

	categoryOptions = '';

	$.ajax({
		type: "POST",
		url: "getSrSubCategory",
		data: {
			"srTypeId": srTypeId,
			"srCategoryId": srCategoryId
		},
		async: false,
		success: function(data) {
			var sourceDesc = data.results;
			for (var i = 0; i < data.length; i++) {
				var category = data[i];

				if (srSubCategoryId && srSubCategoryId === category.srSubCategoryId) {
					categoryOptions += '<option value="' + category.srSubCategoryId + '" selected="selected" >' + category.srSubCategoryDesc + '</option>';
				} else {
					categoryOptions += '<option value="' + category.srSubCategoryId + '">' + category.srSubCategoryDesc + '</option>';
				}
			}
		}
	});

	$("#srSubCategoryId").html(categoryOptions);
	$("#srSubCategoryId").dropdown('refresh');

}


$("#srTypeId").change(function() {
	var srTypeId = $("#srTypeId").val();
	if (srTypeId != "") {
		$('#srTypeId_error').hide();
		loadCategory(srTypeId);
	} else {
		$("#srCategoryId").html('');
		//$("#srSubCategoryId").html('');
		$('.srCategoryId .clear').click();
		$('.srSubCategoryId .clear').click();
		$('.srSubCategoryId .icon').removeClass('clear');
		$('#srTypeId_error').html($("#srTypeId").attr('data-error'));
		$('#srTypeId_error').show();
	}
});

$("#srCategoryId").change(function() {
	var srCategoryId = $(this).val();
	var srTypeId = $("#srTypeId").val();
	if (srCategoryId != "") {
		$('#srCategoryId_error').hide();
		loadSubCategory(srTypeId, srCategoryId);
	} else {
		$('.srSubCategoryId .clear').click();
		if (srTypeId != "") {
			$('#srCategoryId_error').html($("#srCategoryId").attr('data-error'));
			$('#srCategoryId_error').show();
		} else {
			$('#srCategoryId_error').hide();
		}
	}
});

$("#owner").change(function() {
	var owner = $("#owner").val();
	if (owner != undefined && owner != null)
		getBusinessUnit(owner);
});

function loadCategory(srTypeId) {
	var nonSelectContent = "<span class='nonselect'>Please Select</span>";
	var categoryOptions = '<option value="" data-content="' + nonSelectContent + '" selected="">Please Select</option>';
	$.ajax({
		type: "POST",
		url: "getSrCategory",
		data: {
			"srTypeId": srTypeId
		},
		async: false,
		success: function(data) {
			var sourceDesc = data.results;
			for (var i = 0; i < data.length; i++) {
				var category = data[i];
				categoryOptions += '<option value="' + category.srCategoryId + '">' + category.srCategoryDesc + '</option>';
			}
		}
	});

	$("#srCategoryId").html(categoryOptions);
}

function loadSubCategory(srTypeId, srCategoryId) {
	var nonSelectContent = "<span class='nonselect'>Please Select</span>";
	var subCategoryOptions = '<option value="" data-content="' + nonSelectContent + '" selected="">Please Select</option>';
	$.ajax({
		type: "POST",
		url: "getSrSubCategory",
		data: {
			"srTypeId": srTypeId,
			"srCategoryId": srCategoryId
		},
		async: false,
		success: function(data) {
			var sourceDesc = data.results;
			for (var i = 0; i < data.length; i++) {
				var category = data[i];
				subCategoryOptions += '<option value="' + category.srSubCategoryId + '">' + category.srSubCategoryDesc + '</option>';
			}
		}
	});

	$("#srSubCategoryId").html(subCategoryOptions);
}

function getPrimaryContacts(partyId) {
	var nonSelectContent = "<span class='nonselect'>Please Select</span>";
	var dataSourceOptions = '';

	$.ajax({
		type: "POST",
		url: "/common-portal/control/getPrimaryContacts",
		data: {
			"partyId": partyId,
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
				} else {
					for (var i = 0; i < data.length; i++) {
						var entry = data[i];
						dataSourceOptions += '<option value="' + entry.contactId + '">' + entry.name + '</option>';
					}
				}
			}
		}
	});
	$("#ContactID").html(dataSourceOptions);
	$("#ContactID").dropdown('refresh');
}

function getBusinessUnit(owner) {
	var owner = owner;
	$.ajax({
		type: "POST",
		url: "getBusinessUnitName",
		async: false,
		data: {
			"owner": owner
		},
		success: function(data) {
			result = data;
			if (result && result[0] != undefined && result[0].businessunitName != undefined)
				$("#ownerBu").val(result[0].businessunitName);
			else
				$("#ownerBu").val("");
		},
		error: function(data) {
			result = data;
			showAlert("error", "Error occured while fetching Business Unit");
		}
	});
}

function getUsers(ownerUserLoginId) {
	var userOptionList = '';
	$.ajax({
		type: "GET",
		url: '/common-portal/control/getUsersList',
		async: false,
		success: function(data) {
			for (var i = 0; i < data.length; i++) {
				var entry = data[i];
				if (ownerUserLoginId && ownerUserLoginId === entry.userLoginId) {
					userOptionList += '<option value="' + entry.userLoginId + '" selected>' + entry.userName + '</option>';
				} else {
					userOptionList += '<option value="' + entry.userLoginId + '">' + entry.userName + '</option>';
				}
			}
		}
	});
	$("#owner").html(userOptionList);
} -->

</script>







