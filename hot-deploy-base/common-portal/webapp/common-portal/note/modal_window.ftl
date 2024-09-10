<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#-- 
<style>
	.modal {
	height: 800px;
	width: 1700px;
	margin-left: 0px;
	}
</style>
-->
<#assign isPhoneCampaignEnabled = Static["org.fio.homeapps.util.DataUtil"].isPhoneCampaignEnabled(delegator)!>
<#macro createNoteModal instanceId fromAction="">
<div id="${instanceId!}" class="modal fade" >
	<div class="modal-dialog modal-lg" style="max-width: 1700px;">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<h4 class="modal-title">Notes</h4>
				<button type="button" class="close" data-dismiss="modal">&times;</button>
			</div>
			<div class="modal-body">
				<form id="add-note-form" method="post" data-toggle="validator">
					<#assign callListNoteType = Static["org.fio.homeapps.util.DataUtil"].getGlobalValue(delegator, "ENABLE_CALL_LIST_NOTE_TYPE", "N") /> 
					<input type="hidden" name="activeTab" value="notes" />
					<input type="hidden" name="partyId" value="${partyId?if_exists}">
					<input type="hidden" name="salesOpportunityId" value="${salesOpportunityId?if_exists}">
					<input type="hidden" name="custRequestId" value="${custRequestId?if_exists}">
					<input type="hidden" name="donePage" value='${requestURI?if_exists}'/>
					<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
					<input type="hidden" name="noteId" id="noteId" />
					<input type="hidden" name="domainEntityType" value="${domainEntityType!}">
					<input type="hidden" name="domainEntityId" value="${domainEntityId!}">
					<input type="hidden" name="workEffortId" value="${requestParameters.workEffortId!requestAttributes.workEffortId!}"/>
					<#if callListNoteType?exists && callListNoteType?has_content && callListNoteType=="Y">
						<input type="hidden" id="callListNoteType" name="callListNoteType" value="_NA_"/>
					</#if>
					<#if isPhoneCampaignEnabled?has_content && isPhoneCampaignEnabled?if_exists=="Y">
						<input type="hidden" id="hideNoteDescription" name="hideNoteDescription" value="Y"/>
					</#if>
					<@dynaScreen 
						instanceId="NOTE_DATA"
						modeOfAction="CREATE"
						/>
					<div class="row padding-r" id="expiredDateField" style="display:none;">
						<div class="col-md-12 col-sm-12">
							<@inputDate
								id="expiredDate"
								label="Expired Date"
								labelColSize="col-sm-2 field-text"
								inputColSize="input-group col-sm-4 date"
								dateFormat="MM/dd/YYYY"
								/>
						</div>
					</div>
					<div class="form-group offset-2">
						<div class="text-left ml-3">
							<#--  <@formButton
							btn1type="submit"
							btn1label="${uiLabelMap.Save}"
							btn2=true
							btn2id="note-reset-btn"
							btn2type="reset"
							btn2label="${uiLabelMap.Clear}"
							/>-->
							<@submit label="${uiLabelMap.Save}" id="submit"/>
							<@reset label="${uiLabelMap.Clear}" id="reset-note" onclick="clearSummerNote()"/>
						</div>
					</div>
				</form>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-sm btn-primary navbar-dark" data-dismiss="modal">Close</button>
			</div>
		</div>
	</div>
</div>
<script>
	function clearSummerNote() {
		$('#note').summernote('code', '');
	}
$(document).ready(function() {

	$('#${instanceId!}').on('hidden.bs.modal', function(e) {
		$('#noteId').val("");
		$("#note-reset-btn").trigger("click");
		$('#note').summernote('code', '');
	});
	$("#expiredDateField").hide();
});

function editNote(noteId) {

	$('#${instanceId!}').modal('show');

	$("#isImportant_0").prop("checked", false);
	$("#isImportant_1").prop("checked", false);
	$.ajax({
		type: "POST",
		url: "/common-portal/control/getNoteData",
		data: {
			"noteId": noteId,
			"domainEntityType": "${domainEntityType!}",
			"domainEntityId": "${domainEntityId!}",
			"externalLoginKey": "${requestAttributes.externalLoginKey!}"
		},
		async: false,
		success: function(result) {
			if (result.code == 200) {
				for (var fieldName in result.data) {
					//console.log("name: "+fieldName+", value: "+result.data[fieldName]);
					if (result.data[fieldName]) {
						$('#add-note-form #noteId').val(result.data.noteId);
						$('#add-note-form #noteName').val(result.data.noteName);
						$('#add-note-form #noteType').val(result.data.noteType);
						$('#add-note-form #note').val(result.data.note);
						$('#add-note-form #isImportant').val(result.data.isImportant);
						$('#add-note-form #expiredDate').val(result.data.expiredDate);
					}
					if (fieldName == "isImportant") {
						var val = result.data[fieldName];
						if ("Y" === val) {
							$("#isImportant_0").prop("checked", true);
							$("#expiredDateField").show();
						} else {
							$("#isImportant_1").prop("checked", true);
							$("#expireDate").val("");
							$("#expiredDateField").hide();
						}
					}
				}
				$('.ui.dropdown.search').dropdown({
					clearable: true
				});
				$('#noteType_error').html("");
				$('#note').summernote('code', result.data['note']);
			}
		}
	});
}

$('input[type=radio][name=isImportant]').change(function() {
	var value = this.value;
	if ("Y" === value) {
		$("#expiredDateField").show();
	} else {
		$("#expireDate").val("");
		$("#expiredDateField").hide();
	}
});

</script>
</#macro>