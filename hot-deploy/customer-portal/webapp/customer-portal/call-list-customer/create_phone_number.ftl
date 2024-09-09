<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<style>
	.scroll-1{
	height:430px;
	overflow-y:scroll;
	overflow-x:hidden;
	}
</style>
<!-- Telecom Number-->
<#assign numberCnt = 1/>
<#if contactMeches?has_content>
<#list contactMeches as contactMechMap>
<#assign contactMech = contactMechMap.contactMech>
<#assign partycontactMech = contactMechMap.partyContactMech>
<#if contactMech.contactMechId ==  partycontactMech.contactMechId && contactMech.contactMechTypeId == "TELECOM_NUMBER">
<div id="TELECOMcontactInfo_${contactMech.contactMechId}" class="modal fade TELECOMcontactInfotest-extend-height" role="dialog">
	<div class="modal-dialog modal-lg" style="max-width: 1300px;">
		<div class="modal-content">
			<div class="modal-header">
				<h4 class="modal-title">${uiLabelMap.updatePhoneNumber}</h4>
				<button type="reset" class="close" data-dismiss="modal">&times;</button>
			</div>
			<div class="modal-body poplabel-left">
				<div class="row padding">
					<div class="col-md-4 col-sm-4 ">
						<div class="form-group row text-danger">
							<label  class="col-sm-12 field-text">${uiLabelMap.contactPurpose}*</label>
						</div>
					</div>
					<div class="col-md-8 col-sm-8">
						<#assign i = 0/>
						<#assign existingTelecomCmptSize = contactMechMap.partyContactMechPurposes?size />
						<input type="hidden" name="existingTelecomCmptSize" id="existingTelecomCmptSize" value="${existingTelecomCmptSize}" />
						<#list contactMechMap.partyContactMechPurposes as partyContactMechPurpose>
						<form method="post" action="<@ofbizUrl>deletePartyContactMechPurpose</@ofbizUrl>" class="form-horizontal" name="deletePersonContactMechPurpose_EMAIL_${partyContactMechPurpose.contactMechId}_${i}" novalidate="novalidate" data-toggle="validator">
							<input type="hidden" name="activeTab" value="contactInfo" />
							<input type="hidden" name="donePage" value='viewCallListCustomer'/>
							<input type="hidden" name="contactMechId" value="${partyContactMechPurpose.contactMechId}"/>
							<input type="hidden" name="partyId" value="${partyId?if_exists}"/>
							<input type="hidden" name="campaignListId" value="${campaignListId?if_exists}"/>
							<input type="hidden" name="marketingCampaignId" value="${marketingCampaignId?if_exists}"/>
							<input type="hidden" name="contactListId" value="${contactListId?if_exists}"/>
							<input type="hidden" name="contactMechPurposeTypeId" value="${partyContactMechPurpose.contactMechPurposeTypeId}"/>
							<input type="hidden" name="fromDate" value="${partyContactMechPurpose.fromDate?if_exists}"/>
							<div class="form-group row">
								<div class="col-sm-8">
									<label class="field-text input-sm fw">
									<#assign contactMechPurposeType = partyContactMechPurpose.getRelatedOne("ContactMechPurposeType",true)>
									<#if contactMechPurposeType?has_content>
									${contactMechPurposeType.get("description",locale)}<br>
									<#else>
									${uiLabelMap.PartyMechPurposeTypeNotFound}: "${partyContactMechPurpose.contactMechPurposeTypeId}"
									</#if>
									<#if partyContactMechPurpose.thruDate?has_content>
									(${uiLabelMap.CommonExpire}: ${getLocalizedDate(partyContactMechPurpose.thruDate)})
									</#if>
									</label>
								</div>
								<div class="">
									<#if !partyContactMechPurpose.contactMechPurposeTypeId.contains("IDEAL")>
									<input type="checkbox" class="checkbox" name="contactMechPurposeTypeIds" id="contactMechPurposeTypeId_${i}" value ="${partyContactMechPurpose.contactMechPurposeTypeId}"/>
									<label  for="contactMechPurposeTypeId_${i}">Remove
									<input type="hidden" name="contactMechPurposeTypeId" id="contactMechPurposeTypeId"/>
									</label>
									<!-- <a class="btn btn-xs btn-secondary btn-danger tooltips confirm-message" href="javascript:document.deletePersonContactMechPurpose_EMAIL_${partyContactMechPurpose.contactMechId}_${i}.submit()" data-original-title="${uiLabelMap.delete}"><i class="fa fa-times red"></i></a>-->
									</#if>
								</div>
							</div>
						</form>
						<#assign i = i+1/>
						</#list>
						<form method="post" action="<@ofbizUrl>createPartyContactMechPurpose</@ofbizUrl>" class="form-horizontal" name="createPartyContactMechPurpose_TELECOM" id="createPartyContactMechPurpose_TELECOM_${contactMech.contactMechId}" novalidate="novalidate" data-toggle="validator">
							<input type="hidden" name="activeTab" value="contactInfo" />
							<input type="hidden" name="donePage" value='viewCallListCustomer'/>
							<input type="hidden" name="contactMechId" value="${contactMech.contactMechId}"/>
							<input type="hidden" name="partyId" value="${partyId?if_exists}"/>
							<input type="hidden" name="campaignListId" value="${campaignListId?if_exists}"/>
							<input type="hidden" name="marketingCampaignId" value="${marketingCampaignId?if_exists}"/>
							<input type="hidden" name="contactListId" value="${contactListId?if_exists}"/>
							<div class="form-group row">
								<#assign contactMechTPTelecom = delegator.findByAnd("ContactMechTypePurpose", {"contactMechTypeId" : "TELECOM_NUMBER"}, [], false)>
								<div class="col-sm-11">
									<select name="contactMechPurposeTypeId" id="contactMechPurposeTypeId" class="ui dropdown search form-control input-sm" >
										<option value="" data-content="<span class='nonselect'>Please Select</span>" selected>Please Select</option>
										<#if contactMechTPTelecom?exists && contactMechTPTelecom?has_content>
										<#list contactMechTPTelecom as contactMechTPTelecomGV>
										<#assign contactMechPTTelecom = delegator.findOne("ContactMechPurposeType", {"contactMechPurposeTypeId" : contactMechTPTelecomGV.contactMechPurposeTypeId}, false)>
										<#if contactMechPTTelecom?exists && contactMechPTTelecom?has_content>
										<option value="${contactMechPTTelecom.contactMechPurposeTypeId}" <#if ("${contactMechPTTelecom.contactMechPurposeTypeId}"="IDEAL_MOBILE_PHONE" || "${contactMechPTTelecom.contactMechPurposeTypeId}"="AOS_MOBILE_PHONE") || ("${contactMechPTTelecom.contactMechPurposeTypeId}" = "IDEAL_PRIMARY_PHONE" ) > disabled data-content="<span class='nonselect'>${contactMechPTTelecom.description}</span>" </#if> >
										${contactMechPTTelecom.description} 
										</option>
										</#if>
										</#list>
										</#if>
									</select>
									<div class="help-block with-errors" id="contactMechPurposeTypeId_error"></div>
								</div>
								<div class="col-sm-2">
									<!-- <a class="btn btn-xs btn-primary tooltips" href="javascript:document.createPartyContactMechPurpose_EMAIL_${contactMech.contactMechId}.submit()" data-original-title="${uiLabelMap.addPurpose}"><i class="fa fa-plus info"></i></a>-->
								</div>
							</div>
						</form>
					</div>
				</div>
				<hr/>
				<form method="post" action="<@ofbizUrl>updateTelecomNumberWithPurpose</@ofbizUrl>" id="updateTelecomNumber_${contactMech.contactMechId}" class="form-horizontal updateTelecomNumber" name="updateTelecomNumber" novalidate="novalidate" data-toggle="validator">
					<input type="hidden" name="activeTab" value="contactInfo" />
					<input type="hidden" name="donePage" value='viewCallListCustomer'/>
					<input type="hidden" name="partyId" value="${partyId?if_exists}"/>
					<input type="hidden" name="contactMechId" value="${contactMech.contactMechId}"/>
					<input type="hidden" name="campaignListId" value="${campaignListId?if_exists}"/>
					<input type="hidden" name="marketingCampaignId" value="${marketingCampaignId?if_exists}"/>
					<input type="hidden" name="contactListId" value="${contactListId?if_exists}"/>
					<input type="hidden" id="accType" name="accType" value="${accType?if_exists}"/>
					<#assign telecomNumber = delegator.findOne("TelecomNumber", {"contactMechId", "${contactMech.contactMechId}"}, false)?if_exists />
					<#assign cmId = contactMech.contactMechId/>
					<#assign entities = delegator.findByAnd("PartyContactMechPurpose", Static["org.ofbiz.base.util.UtilMisc"].toMap("contactMechId",cmId), null, false)>
					<#assign teleCode = Static["org.fio.homeapps.util.DataUtil"].getCountryTeleCode(delegator, "") />
					<#assign countryCode="">
					<#if telecomNumber?has_content && telecomNumber.countryCode?has_content>
					<#assign countryCode=telecomNumber.countryCode?if_exists>
					<#elseif teleCode?has_content>
					<#assign countryCode=teleCode?if_exists>
					</#if>
					<@inputRow 
						id="contactNumbers${numberCnt!}"
						name="contactNumber"
						label=uiLabelMap.phoneNumber
						placeholder=uiLabelMap.phoneNumber
						value="${telecomNumber?if_exists.contactNumber?if_exists}"
						required=true
						maxlength=10
						/>
					<@inputRow 
						id="extension${numberCnt!}"
						name="extension"
						label=uiLabelMap.extension
						placeholder=uiLabelMap.extension
						value="${partycontactMech?if_exists.extension?if_exists}"
						pattern="^[0-9]+$"
						maxlength=5
						/>
					<#assign phoneAllowSolicitation = "Y"/>
					<#if partySummary?exists && partySummary?has_content>
					<#assign partySolicitation = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(delegator.findByAnd("PartyContactMech", {"partyId" : partySummary.partyId,"contactMechId":contactMech.contactMechId}, [], false))?if_exists/>
					</#if>
					<#if partySolicitation?exists && partySolicitation?has_content>
					<#if partySolicitation.allowSolicitation?has_content && partySolicitation.allowSolicitation=="N">
					<#assign phoneAllowSolicitation = "N"/>
					</#if>
					</#if>
					<@dropdownCell
						id="allowSolicitation"
						name="allowSolicitation"
						label=uiLabelMap.allowSolicitation
						options=yesNoOptions
						value="${phoneAllowSolicitation?if_exists}"
						required=false
						allowEmpty=true
						dataLiveSearch=true
						/>
					<div class="col-md-12 col-sm-12">
						<div class="form-group row">
							<div class="offset-sm-4 col-sm-9">
								<@formButton
									btn1type="button"
									btn1label="${uiLabelMap.Update}"
									btn1onclick="formSubmissionPhone('${contactMech.contactMechId}','${entities.get(0).contactMechPurposeTypeId}');"
									/>
							</div>
						</div>
					</div>
				</form>
			</div>
		</div>
	</div>
</div>
</#if>
<#assign numberCnt = numberCnt + 1/>
</#list>
</#if>
<script>
var targetElement = document.querySelector('.modal.TELECOMcontactInfotest-extend-height');
var observer = new MutationObserver(function(mutationsList) {
    for (var mutation of mutationsList) {
        if (mutation.type === 'attributes' && mutation.attributeName === 'class') {
            var currentClass = targetElement.className;
            if (!currentClass.includes('show')) {
                $('#createTELECOMcontactInfo').modal('show');
            }
        }
    }
});
observer.observe(targetElement, { attributes: true });

let inputValue = "";
let inputValues = "";
$(document).ready(function() {
	$('.updateTelecomNumber [name="contactNumber"]').keyup(function(e) {
		inputValues = $(this);
		var obj = $(this);
		validatePhoneNumberUpdate(obj);
	});
	$('.fa.fa-pencil-square-o.btn.btn-xs.btn-primary.tooltips').click(function() {
        $('#createTELECOMcontactInfo').modal('hide');
    });
});

function formSubmissionPhone(contactMechId, cmpTypeId) {
	if (inputValues == "" || inputValues == "undefined" || inputValues == null) {
		inputValues = $('.updateTelecomNumber [name="contactNumber"]');
		var inputValue1 = inputValues.val();
	}
	if (validatePhoneNumberUpdate(inputValues)) {
		return false;
	}
	var primaryPhoneNumber = document.getElementsByName("contactNumber")[0].value;
	var accType = document.getElementById("accType").value;
	var partyId = document.getElementById("partyId").value;
	var cmtype = document.getElementById("contactMechPurposeTypeId").value;
	var formName = "updateTelecomNumber";
	var contactMechId = contactMechId;
	var cmpTypeId = cmpTypeId;
	var ctId = $('#contactMechPurposeTypeId_TELECOM').val();
	var allVals = [];
	$('input[name="contactMechPurposeTypeIds"]:checked').each(function() {
		allVals.push($(this).val());
	});
	var fName = "createPartyContactMechPurpose_TELECOM_" + contactMechId;
	var cmpType = document.getElementById(fName).elements.namedItem("contactMechPurposeTypeId").value;
	var errorId = "contactMechPurposeTypeId_error_" + contactMechId;
	var delName = "TELECOMcontactInfo_" + contactMechId;
	var existingTelecomCmptSize = $('[id="' + delName + '"] #existingTelecomCmptSize').val();
	if ((existingTelecomCmptSize === "0")) {
		if (cmpType == "") {
			$('[id="' + fName + '"] #contactMechPurposeTypeId_error').html('Please fill out the field');
			return false;
		} else {
			$('[id="' + fName + '"] #contactMechPurposeTypeId_error').html('');
		}
	}

	if ((allVals.length == existingTelecomCmptSize)) {
		if (cmpType == "") {
			$('[id="' + fName + '"] #contactMechPurposeTypeId_error').html('Please Select Contact Purpose To Remove Existing One.');
			return false;
		} else {
			$('[id="' + fName + '"] #contactMechPurposeTypeId_error').html('');
		}
	}
	if (cmpType === "PHONE_MOBILE") {
		$.ajax({
			type: "POST",
			url: "/common-portal/control/getDuplicatePhoneNumber",
			async: true,
			data: {
				"primaryPhoneNumber": primaryPhoneNumber,
				"partyId": partyId,
				"accType": accType,
				"screenType": "UPDATE",
				"externalLoginKey": "${requestAttributes.externalLoginKey!}"
			},
			success: function(data) {
				var message = data.Error_Message;
				loadActivity(message, formName, contactMechId, allVals, cmpType);
			}
		});
	} else {
		var message = "NO_RECORDS";
		loadActivity(message, formName, contactMechId, allVals, cmpType);
	}
}

function loadActivity(message, formName, contactMechId, allVals, cmpType) {
	var formName = formName;
	var contactMechId = contactMechId;
	var allVals = allVals;
	var cmpType = cmpType;
	if (message === "NO_RECORDS") {
		if (formName === "updateTelecomNumber") {
			var fName = "#updateTelecomNumber_" + contactMechId;
			$(fName).append("<input type='hidden' id='contactMechPurposeTypeId_APA' name='contactMechPurposeTypeId_APA' value=\"" + cmpType + "\">");
			$(fName).append("<input type='hidden' id='deleteCMPurposeTypeIds_DPA' name='deleteCMPurposeTypeIds_DPA' value=\"" + allVals + "\">");
			$(fName).submit();
		}
	} else {
		if (formName === "updateTelecomNumber") {
			$('#submitPhoneModal').modal('show');
			$("#TN_message").html(message);
			$("#TN_cmId").html(contactMechId);
			$("#TN_cmAId").html(cmpType);
			$("#TN_cmDId").html(allVals);
		}
	}
}

function validatePhoneNumberUpdate(obj) {
    var isInvalid = false;
    if (obj && obj.length > 0) {
        var contactNumberValue = obj.val();
        var re = new RegExp("^[0-9]{1,11}$");
        if (contactNumberValue != "") {
            var numberCount = contactNumberValue.replace(/[^0-9]/g, '').length;
            if (numberCount < 10 || numberCount > 10) {
                $('[id="' + obj.attr('id') + '_error"]').html('Please enter a minimum of 10 numbers only');
                isInvalid = true;
            } else {
                if (!re.test(contactNumberValue)) {
                    $('[id="' + obj.attr('id') + '_error"]').html('Please enter a minimum of 10 numbers only');
                    isInvalid = true;
                } else {
                    $('[id="' + obj.attr('id') + '_error"]').html('');
                }
            }
        }
    } else {
        console.error("obj is not a valid jQuery object or is not defined.");
    }
    return isInvalid;
}

</script>
<!-- Telecom Number start-->
<div id="createTELECOMcontactInfo" class="modal fade" role="dialog">
	<div class="modal-dialog modal-lg" style="max-width: 1300px;">
		<form method="post" action="<@ofbizUrl>createCallListTelecomNumber</@ofbizUrl>" id="createTelecomNumber" class="form-horizontal" name="createTelecomNumber" novalidate="novalidate" data-toggle="validator">
			<input type="hidden" name="contactMechTypeId" value="TELECOM_NUMBER"><input type="hidden" name="donePage" value="viewCallListCustomer" /><input type="hidden" name="partyId" value="${partyId?if_exists}"><input type="hidden" name="campaignListId" value="${campaignListId?if_exists}"><input type="hidden" name="externalLoginKey" value="${externalLoginKey?if_exists}">
			<input type="hidden" name="marketingCampaignId" value="${marketingCampaignId?if_exists}"/>
			<input type="hidden" name="contactListId" value="${contactListId?if_exists}"/>
			<div class="modal-content">
				<div class="modal-header">
					<h4 class="modal-title">Phone numbers of ${inputContext.partyName!}
					</h4>
					<button type="reset" class="close" data-dismiss="modal">&times;
					</button>
				</div>
				<div class="scroll-1">
					<div class="modal-body poplabel-left">
						<br><br>
						<#if contactMeches?has_content>
						<div class="contact-table">
							<table class="table table-striped" style="font-size: 13px !important;">
								<thead>
									<tr>
										<th>${uiLabelMap.contactType!}</th>
										<th>${uiLabelMap.contactInformation!}</th>
										<th>${uiLabelMap.CommonPurpose!}</th>
										<th>${uiLabelMap.solicitation!}</th>
										<th></th>
									</tr>
								</thead>
								<tbody>
									<#list contactMeches as contactMechMap>
									<#assign contactMech = contactMechMap.contactMech>
									<#assign partyContactMech = contactMechMap.partyContactMech>
									<#list contactMechMap.partyContactMechPurposes as partyContactMechPurpose>
									<#assign contactMechPurposeType = partyContactMechPurpose.getRelatedOne("ContactMechPurposeType",true)>
									<#if contactMechPurposeType?has_content && !contactMechPurposeType.get("contactMechPurposeTypeId").contains("PRIMARY_PHONE")>
									<tr>
										<#if contactMechMap.contactMechType.get("description",locale).contains("Phone")>
										<td>${contactMechMap.contactMechType.get("description",locale)!}</td>
										<td>
											<#if "TELECOM_NUMBER" == contactMech.contactMechTypeId && contactMechMap.telecomNumber?exists>
											<#assign telecomNumber = contactMechMap.telecomNumber?if_exists>
											<#assign contactNumber = telecomNumber.contactNumber?if_exists>
											<#assign areaCode = telecomNumber.areaCode?if_exists>
											<#assign phoneNumber = areaCode+contactNumber>
											<#assign contactNumber=Static["org.groupfio.common.portal.util.DataHelper"].preparePhoneNumber(delegator, phoneNumber?default("0000000000"))/>
											<#if telecomNumber.askForName?has_content><b>${uiLabelMap.toName}:</b> ${telecomNumber.askForName}<br/></#if>
											<#if telecomNumber?has_content && telecomNumber.countryCode?has_content>
											+${telecomNumber.countryCode?replace("&#x2b;","")?if_exists}-
											</#if>
											${contactNumber!}
											<#if partyContactMech.extension?has_content>${uiLabelMap.PartyContactExt}&nbsp;${partyContactMech.extension}</#if>
											<#else>
											${contactMech.infoString?if_exists}
											</#if>
											<#if partyContactMech.thruDate?has_content><b>${uiLabelMap.PartyContactEffectiveThru}:&nbsp;${getLocalizedDate(partyContactMech.thruDate)}</b></#if>
										</td>
										<td>
											
											
											${contactMechPurposeType.get("description",locale)}<br>
											<#if partyContactMechPurpose.thruDate?has_content>
											(${uiLabelMap.CommonExpire}: ${getLocalizedDate(partyContactMechPurpose.thruDate)})
											</#if>
										</td>
										<td>
											<#if partySummary?exists && partySummary?has_content>
											<#assign partySolicitation = delegator.findByAnd("PartyContactMech", {"partyId" : partySummary.partyId?if_exists,"contactMechId":contactMech.contactMechId}, [], false)?if_exists/>
											</#if>
											<#if partySolicitation?has_content>
											<#list partySolicitation as partySolicitation>
											<#if requestURI?exists && requestURI?has_content && requestURI == "viewLead" && "TELECOM_NUMBER" == contactMech.contactMechTypeId && contactMechMap.telecomNumber?exists>
											<#if partySolicitation.allowSolicitation?has_content && partySolicitation.allowSolicitation="N">
											<font color="red"><b>(N)</b></font>
											<#else>
											<b>(Y)</b>
											</#if>
											<#else>
											<#if partySolicitation.allowSolicitation?has_content && partySolicitation.allowSolicitation="N">
											<font color="red"><b>(N)</b></font>
											<#else>
											<b>(Y)</b>
											</#if>
											</#if>
											</#list>
											</#if>
										</td>
										<td>
											<#assign idealType = "N"/>
											<#list contactMechMap.partyContactMechPurposes as partyContactMechPurpose>
											<#if partyContactMechPurpose.contactMechPurposeTypeId.contains("IDEAL")>
											<#assign idealType = "Y"/>
											</#if>
											</#list>
											<#if partyStatusId?if_exists != "PARTY_DISABLED">
											<#if idealType=="N">
											<#if contactMechMap.contactMechType.get("description",locale).contains("Phone")>
											<a data-toggle="modal" href="#TELECOMcontactInfo_${partyContactMech.contactMechId}"><i class="fa fa-pencil-square-o btn btn-xs btn-primary tooltips"  data-original-title="${uiLabelMap.edit}"></i></a>
											</#if>
											<a class="btn btn-xs btn-danger" data-toggle="confirmation"  href="javascript:deleteContactMech('${partyContactMech.partyId?if_exists}','${partyContactMech?if_exists.contactMechId?if_exists}');" data-original-title="Are you sure to delete"><i class="fa fa-times tooltips" title="Delete"></i></a>
											</#if>
											</#if>
										</td>
									</tr>
									</#if>
									</#if>
									</#list>
									</#list>
								</tbody>
							</table>
						</div>
						</#if>
						<#assign contactMechTPTelecom=delegator.findByAnd("ContactMechTypePurpose", { "contactMechTypeId" : "TELECOM_NUMBER" } , [], false)>
						<div class="form-group row">
							<label class="col-sm-4 field-text">${uiLabelMap.CommonPurpose!}<span class="text-danger">&#42;</span></label>
							<div class="col-sm-8">
								<select name="contactMechPurposeTypeId" id="contactMechPurposeTypeId" class="ui dropdown form-control input-sm">
									<option value="" data-content="<span class='nonselect'>Please Select</span>" selected>Please Select</option>
									<#if contactMechTPTelecom?exists && contactMechTPTelecom?has_content>
									<#list contactMechTPTelecom as contactMechTPTelecomGV>
									<#assign contactMechPTTelecom=delegator.findOne("ContactMechPurposeType", { "contactMechPurposeTypeId" : contactMechTPTelecomGV.contactMechPurposeTypeId } , false)>
									<#if contactMechPTTelecom?exists && contactMechPTTelecom?has_content && "${contactMechPTTelecom.contactMechPurposeTypeId}"!="PRIMARY_PHONE">
									<option value="${contactMechPTTelecom.contactMechPurposeTypeId}" <#if ("${contactMechPTTelecom.contactMechPurposeTypeId}"="IDEAL_MOBILE_PHONE" || "${contactMechPTTelecom.contactMechPurposeTypeId}"="PRIMARY_PHONE" || "${contactMechPTTelecom.contactMechPurposeTypeId}"="AOS_MOBILE_PHONE" ) || ("${contactMechPTTelecom.contactMechPurposeTypeId}"="IDEAL_PRIMARY_PHONE" )>disabled data-content="<span class='nonselect'>${contactMechPTTelecom.description}</span>"
									</#if>>${contactMechPTTelecom.description}
									</option>
									</#if>
									</#list>
									</#if>
								</select>
								<div class="help-block with-errors" id="contactMechPurposeTypeId_error"></div>
							</div>
						</div>
						<@inputRow 
							id="contactNumberss"
							name="contactNumber"
							label=uiLabelMap.phoneNumber
							placeholder=uiLabelMap.phoneNumber
							value=""
							required=true
							/>
						<@inputRow 
							id="extension"
							name="extension"
							label=uiLabelMap.extension
							placeholder=uiLabelMap.extension
							value=""
							pattern="^[0-9]+$"
							maxlength=5
							dataError="Please Enter Numbers Only."
							/>
						<@dropdownCell 
							id="allowSolicitation"
							name="allowSolicitation"
							label=uiLabelMap.allowSolicitation
							options=yesNoOptions
							required=false
							allowEmpty=true
							dataLiveSearch=true
							/>
						<div class="col-md-12 col-sm-12">
							<div class="form-group row">
								<div class="offset-sm-4 col-sm-9">
									<input type="button" class="btn btn-sm btn-primary navbar-dark mt" value="${uiLabelMap.Save}" onclick="formTelecomNumberSubmission(createTelecomNumber.contactMechPurposeTypeId,createTelecomNumber.contactNumberss);" />
									<@reset id="reset" label="${uiLabelMap.Reset}" />
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</form>
	</div>
</div>
<!-- Telecom Number end-->
<script>
	$(document).ready(function() {
	 	$('#createTelecomNumber #contactMechPurposeTypeId').change(function() {
	 	if ($(this).val() !== '') {
                    $('#createTelecomNumber #contactMechPurposeTypeId_error').html("");
                } else {
                    $('#createTelecomNumber #contactMechPurposeTypeId_error').html("Please fill out the field");
                }
	       });
	       $('#contactNumberss').keyup(function(){
	          validatePhoneNumber();
	       });
	    });
		function formTelecomNumberSubmission(cmpTypeId, cnId) {
	    var isInValid = false;
	    var contactMechPurposeTypeId = $('[id="createTelecomNumber"] #contactMechPurposeTypeId').val();
	    if (contactMechPurposeTypeId == "") {
	        $('[id="createTelecomNumber"] #contactMechPurposeTypeId_error').html('Please fill out the field');
	        isInValid = true;
	    }else{
	            $('[id="createTelecomNumber"] #contactMechPurposeTypeId_error').html('');
	    }
	
	    if ($('[id="createTelecomNumber"] #contactNumberss').val() == "") {
	        $('[id="createTelecomNumber"] #contactNumberss_error').html('Please fill out the field');
	        isInValid = true;
	    }else{
	            $('[id="createTelecomNumber"] #contactNumberss_error').html('');
	    }
	    if (isInValid || validatePhoneNumber()) {
	        return false;
	    }
	
	    var primaryPhoneNumber = cnId.value;
	    var partyId = document.getElementById("partyId").value;
	    var cmtype = cmpTypeId.value;
	    var formName = "createTelecomNumber";
	
	    if ((cmtype === "PHONE_MOBILE")) {
	        $.ajax({
	            type: "POST",
	            url: "/common-portal/control/getDuplicatePhoneNumber",
	            async: true,
	            data: {
	                "primaryPhoneNumber": primaryPhoneNumber,
	                "partyId": partyId,
	                "screenType": "CREATE"
	            },
	            success: function(data) {
	                var message = data.Error_Message;
	                loadActivityCreate(message, formName);
	            }
	        });
	    } else {
	        var message = "NO_RECORDS";
	        loadActivityCreate(message, formName);
	    }
	}
	
function validatePhoneNumber() {
    var isInvalid = false;
    var phoneNumber = $('[id="createTelecomNumber"] #contactNumberss').val();
     var re = new RegExp("^[0-9]{1,11}$");
    if (phoneNumber != "") {
        var numberCount = phoneNumber.replace(/[^0-9]/g, '').length;
        if (numberCount < 10 || numberCount > 10) {
            event.preventDefault();
            $('[id="createTelecomNumber"] #contactNumberss_error').html('Please enter a minimum of 10 numbers only');
            isInvalid = true;
        } else {
            if (!re.test(phoneNumber)) {
                event.preventDefault();
                $('[id="createTelecomNumber"] #contactNumberss_error').html('Please enter a minimum of 10 numbers only');
                isInvalid = true;
            } else {
                $('[id="createTelecomNumber"] #contactNumberss_error').html('');
            }
        }
    } else {
        if ($('[id="createTelecomNumber"] #contactNumberss_error') != undefined)
            $('[id="createTelecomNumber"] #contactNumberss_error').html('');
    }
    
    return isInvalid;
}

	function loadActivityCreate(message, formName) {
	    var formName = formName;
	    if (message === "NO_RECORDS") {
	        if (formName === "createTelecomNumber") $('form#createTelecomNumber').submit();
	    } else {
	        if (formName === "createTelecomNumber") {
	            $('#createTelecomNumberModal').modal('show');
	            $("#TNC_message").html(message);
	        }
	    }
	}
	function deleteContactMech(partyId,contactMechId){
		if (partyId && contactMechId){
			var urlString = "/common-portal/control/deleteContactMechAjax?contactMechId=" + contactMechId + "&partyId=" +partyId+ "&externalLoginKey=${requestAttributes.externalLoginKey!}";
		    $.ajax({
		      type: 'POST',
		      async: true,
		      url: urlString,
		      success: function(result) {
		        if (result){
		        	var status = result["status"];
		        	if (status && status =="success"){
		        	    $('#createTELECOMcontactInfo').modal('hide');
		        		showAlert("success", "Successfully deleted");
		        		location.reload();
		        	}else{
		        		$('#createTelecomNumberModal').modal('hide');
		        		showAlert("error", "Error deleting record");
		        	}
		        }
		      }
		    });
		}
	}
</script>