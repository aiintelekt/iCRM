<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/note/modal_window.ftl"/>
<#assign contextPath = request.getContextPath()/>
<#assign campaignListId= request.getParameter("campaignListId")! />
<div class="pt-2 align-lists">
	<form id="note-search-form" name="note-search-form" method="post">	
		<input type="hidden" id="isDisableActivity" name="isDisableActivity" value="${isDisableActivity?if_exists}"/>
		<input type="hidden" name="partyId" value="${partyId?if_exists}"/>
		<input type="hidden" name="isImportant" id="isImportant" value="${isImportant?if_exists}"/>
		<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
		<input type="hidden" name="domainEntityType" value="${domainEntityType!}">
		<input type="hidden" name="domainEntityId" value="${domainEntityId!}">
		<input type="hidden" name="srStatusId" id="srStatusIdVal" value="${srStatusId!}">
		<input type="hidden" name="workEffortId" value="${requestParameters.workEffortId!}">
		<input type="hidden" name="contextPath" id="contextPath" value="${contextPath!}">
		<input type="hidden" name="campaignListId" id="campaignListId" value="${campaignListId!}"/>
	</form>
</div>
<@createNoteModal 
	instanceId="create-note-modal"
	/>
<script>
jQuery(document).ready(function() {
	var contextPath = $("#contextPath").val();
	//if(contextPath == "/client-portal")
	//$("#notes-remove-btn").hide();

	$('#create-note-btn').on('click', function() {
		$("#reset-note").trigger("click");
		$("#isImportant_1").prop("checked", true);
		$("#expireDate").val("");
		$("#expiredDateField").hide();
		$('#create-note-modal').modal("show");
	})

	$('#refresh-note-btn').on('click', function() {
		getNoteRowData();
	});
	
	$('#create-note-modal').on('shown.bs.modal', function (e) {
		getNoteUsers("${loggedUserId!}", "${loggedUserPartyName!}");
	});
	
	$('#create-note-modal').on('hidden.bs.modal', function (e) {
		//try to invoke the update count event
		$.ajax({
			async: false,
			url:"/common-portal/control/updateImportantNoteCount?externalLoginKey=${requestAttributes.externalLoginKey!}",
			type:"POST",
			data: JSON.parse(JSON.stringify($("#add-note-form").serialize())),
			success: function(data){
				if(data){
					var noteData = data.impNoteCount;
					if (noteData != null && noteData != "" && $(".ab-im-notes")[0]){
					    var innerHtml = '<span class="custom-badge" badge="'+noteData+'" style=""><i class="fa fa fa fa-sticky-note-o" style="font-size:20px;" aria-hidden="true"></i></span>';
					    $(".ab-im-notes").html(innerHtml);
					}
				}
			}
		});
		
	});

});

function getNoteUsers(loggedInUserId, userName) {
	var userOptionList = '<option value="">Party</option>';
	userOptionList += '<option value="' + loggedInUserId + '">' + userName + '</option>';
	$.ajax({
		type: "GET",
		url: '/common-portal/control/getUsersList?externalLoginKey=${requestAttributes.externalLoginKey!}',
		async: false,
		success: function(data) {
			for (var i = 0; i < data.length; i++) {
				var type = data[i];
				userOptionList += '<option value="' + type.userLoginId + '">' + type.userName + '</option>';
			}
		}
	});
	$("#noteUserLoginId").html(userOptionList);
}
</script>