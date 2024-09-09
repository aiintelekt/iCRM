<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/lib/picker_macro.ftl"/>
<script type="text/javascript" src="/account-portal-resource/js/emailActivity.js"></script>
<script src="/bootstrap/js/ckeditor/ck-custom-functions.js" type="text/javascript"></script>
<#assign comHistoryTypes = delegator.findByAnd("CommunicationEventType",Static["org.ofbiz.base.util.UtilMisc"].toMap("parentTypeId", "COMMUNICATION_HISTORY")?if_exists,null,false)?if_exists />
<#assign readOnlyPermission = Static["org.fio.homeapps.util.DataUtil"].hasPermissionWoFullPerm(request, "READ_ONLY_PERM")?if_exists />
<style>
.dynamic-iframe {
	border: rgb(255, 255, 255);
}
</style>
<#assign extra =''>
<#list comHistoryTypes as comHistoryType>
	<#assign extra = extra + '<div class="form-check-inline composeRadios">
		<label for="'+comHistoryType.communicationEventTypeId+'_radio">
			<input type="radio" '+(comHistoryType_index == 0)?string("checked='checked'","")+' id="'+comHistoryType.communicationEventTypeId+'_radio" name="searchTypeUI" value="'+comHistoryType.communicationEventTypeId+'" class="form-check-input">
			<span></span>
			<span class="check"></span>
			<span class="box"></span> '
			+comHistoryType.description+'
		</label>
	</div>'>
</#list>
<#assign extra = extra + '<div class="form-check-inline composeChecks">
	<label for="All">
		<input type="checkbox" checked="checked" id="All" name="displayChecks" value="ALL" class="form-check-input">
		<span></span>
		<span class="check"></span>
		<span class="box"></span>
		ALL
	</label>
</div>'>
<#list comHistoryTypes as comHistoryType>
	<#assign extra = extra + '<div class="form-check-inline composeChecks">
		<label for="'+comHistoryType.communicationEventTypeId+'_check">
			<input type="checkbox" checked="checked" id="'+comHistoryType.communicationEventTypeId+'_check" name="displayChecks" value="'+comHistoryType.communicationEventTypeId+'" class="form-check-input">
			<span></span>
			<span class="check"></span>
			<span class="box"></span> '
			+comHistoryType.description+'
		</label>
	</div>'>
</#list>
<#assign viewAll = '<a type="button" target="_BLANK" class="btn btn-xs btn-primary mr-1 ml-1" href="/common-portal/control/viewAllCommunicationEvents?custRequestId=${custRequestId!}&externalLoginKey=${requestAttributes.externalLoginKey!}&domainEntityId=${custRequestId!}&domainEntityType=SERVICE_REQUEST">View All</a>'>

<script>
	<#if readOnlyPermission!>
		$(document).ready(function(){
	        $('a.view-link').each(function(){ 
	            $(this).attr("href", "#"); // Set herf value
	            $(this).attr("target","");
	        });
	        
	        $("a.view-link").click(function () {
	            $("#accessDenied").modal("show");
	            return false;
	        });
	    });
    </#if>
</script>
<#if readOnlyPermission!>
<#else>
	<#if currentSrStatusId?has_content && currentSrStatusId != "SR_CLOSED" && currentSrStatusId != "SR_CANCELLED">
	<#assign hasPermission = Static["org.fio.homeapps.util.DataUtil"].hasPermission(request, "CREATE_OPERATION")?if_exists />
	<#if hasPermission>
		<#assign composeBtn>
			<@button 
				label="${uiLabelMap.Compose}"
				id="compose_btn"
				class="btn btn-xs btn-primary mr-1 ml-1"
			/>
		</#assign>
		<#assign extra = extra + composeBtn + viewAll>
	</#if>
	</#if>
</#if>

<@sectionFrameHeaderTab title="Communication History" tabId="communicationHistory" extra=extra! helpBtnStyle="margin-top: 4px;"/> 

<div class="col-md-12 col-lg-12 col-sm-12" id="emailActivityForm">
	<form method="post" action="<@ofbizUrl>createCommunicationHistoryEvent#sr-communication-history</@ofbizUrl>" id="SrEmail" class="form-horizontal" name="SrCommMainForm" novalidate="novalidate" data-toggle="validator" enctype="multipart/form-data">
        <input type="hidden" name="custRequestId" id="custRequestId" value="${custRequestId!}"/>
		<input type="hidden" name="partyId" id="partyId" value="${fromPartyId!}"/>
        <input type="hidden" name="cNo" id="cNo" value="${fromPartyId!}"/>
		<input type="hidden" name="domainEntityType" value="${domainEntityType!}"/>
		<input type="hidden" name="domainEntityId" value="${domainEntityId!}"/>
        <input type="hidden" name="direction" id="direction" value="OUT"/>
        <input type="hidden" name="ccEmailIds" id="ccEmailIds" value = "${fsrOwnerEmail!}"/>
        <input type="hidden" name="subject" id="subject" value="Updated SR ID - [${custRequestId!}] - ${srName!}">
        <input type="hidden" name="path" id="path" value="sr-portal"/>
        <input type="hidden" name="activeTab" id="activeTab" value="sr-communication-history"/>
        <input type="hidden" name="srCommHistoryFlag" id="srCommHistoryFlag" value="Y"/>
        <input type="hidden" name="srStatusId" id="srStatusId" value="${currentSrStatusId!}"/>
        <input type="hidden" name="srName" id="srName" value="${srName!}"/>
        <input type="hidden" name="recipientPartyEmails" value=""/>
        <input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
        
        <@inputHidden name="ownerBu" id="ownerBu" />
        <#assign srType = EntityQuery.use(delegator).from("WorkEffortAssocTriplet").where("entityName", "Activity", "type", "Type", "value", "E-mail", "active", "Y").queryFirst()! />
       
        <@inputHidden id="srTypeId" value="${(srType.code)!}"/>
        <@inputHidden id="workEffortTypeId" value="${(srType.value)!}"/>
        <@inputHidden id="owner" value="${userLogin.userLoginId?if_exists}" />
        
        <#assign fromPartyEmailId=""/>
		<#assign fromPartyEmailInfo=Static["org.groupfio.common.portal.util.PartyPrimaryContactMechWorker"].getPartyPrimaryContactMechValueMaps(delegator, userLogin.partyId, Static["org.ofbiz.base.util.UtilMisc"].toMap("isRetriveEmail", true),true)!>
		<#if fromPartyEmailInfo?has_content> 
			<#assign fromPartyEmailId=fromPartyEmailInfo.get("EmailAddress")!/>
			<@inputHidden name="nsender" id="nsender" value="${fromPartyEmailId!}" />
		</#if>
		
		<#assign defaultEmailInfo = EntityQuery.use(delegator).from("SystemProperty").where("systemResourceId", "NOTIFICATION", "systemPropertyId", "from").queryFirst()! />
		<#if defaultEmailInfo?has_content>
			<@inputHidden name="defaultFromEmailId" id="defaultFromEmailId" value="${(defaultEmailInfo.systemPropertyValue)!}"/>
		</#if>
		
		<#if srCommPrimaryContactId?has_content>
			<#assign toPartyEmailId= srCommPrimaryContactId />
			<@inputHidden name="contactId" id="contactId" value="${srCommPrimaryContactId}" />
		</#if>
		
		<#assign toPartyEmailInfo=Static["org.groupfio.common.portal.util.PartyPrimaryContactMechWorker"].getPartyPrimaryContactMechValueMaps(delegator, toPartyEmailId, Static["org.ofbiz.base.util.UtilMisc"].toMap("isRetriveEmail", true),true)!>
		<#if toPartyEmailInfo?has_content> 
			<#assign toPartyEmailId=toPartyEmailInfo.get("EmailAddress")!/>
			<@inputHidden name="nto" id="nto" value="${toPartyEmailId!}" />
		</#if>

    	<div class="col-lg-12 col-md-12 col-sm-12">
            <div class="col-md-12 col-md-12 col-sm-12 ">
		     <@dynaScreen 
                instanceId="SR_COMMN_HISTORY"
                modeOfAction="CREATE"
             />
		</div>

        <div class="row p-2">
            <div class="col-md-12 col-lg-12 col-sm-12 sr-ck-editor" id="emlContent">
            <@textareaLarge
               id="emailContent"
               groupId = "htmlDisplay"
               label="HTML"
               rows="3"
               value = template
               required = true
               txareaClass = "ckeditor"
               />
            
            <script>
		        CKEDITOR.replace( 'emailContent', {	
		        	//extraPlugins: 'autogrow',
		        	customConfig : '/bootstrap/js/ckeditor/ck-custom-config.js',
		        	autoGrow_minHeight : 800
					//removePlugins: 'resize',
		        });
		        //CKEDITOR.on('instanceLoaded', function(e) {e.editor.resize("100%", 800)} );
		    </script>
            </div>
   		</div>
        
        <div class="row">
        	<div class="col-md-6 col-sm-6">
        	<@inputRowFilePicker 
			id="attachment"
			label="Attachments"
			placeholder="Select Attachment"
			/>
        	</div>
        </div>
        
        <div class="row">
            <div class="form-group 1">
               <div class="text-left ml-3">
                  <@formButton
                     btn1type="submit"
                     btn1label="Send"
                     btn1onclick="return formSubmission();"
                     btn2=true
                     btn2onclick = "clearCommHistoryForm()"
                     btn2type="reset"
                     btn2label="${uiLabelMap.Clear}"
                   />
               </div>
            </div>
        </div>
    </div>        
    </form>
	
</div>
<div class="col-md-12 col-lg-12 col-sm-12" id="smsActivityForm">
	<form method="post" action="<@ofbizUrl>createCommunicationHistorySmsEvent#sr-communication-history</@ofbizUrl>" id="SrEmail" class="form-horizontal" name="SrCommMainForm" novalidate="novalidate" data-toggle="validator">
        <input type="hidden" name="custRequestId" id="custRequestId" value="${custRequestId!}"/>
		<input type="hidden" name="partyId" id="partyId" value="${fromPartyId!}"/>
        <input type="hidden" name="cNo" id="cNo" value="${fromPartyId!}"/>
		<input type="hidden" name="domainEntityType" value="${domainEntityType!}"/>
		<input type="hidden" name="domainEntityId" value="${domainEntityId!}"/>
        <input type="hidden" name="direction" id="direction" value="OUT"/>
        <input type="hidden" name="ccEmailIds" id="ccEmailIds" value = "${fsrOwnerEmail!}"/>
        <input type="hidden" name="subject" id="subject" value="Updated SR ID - [${custRequestId!}] - ${srName!}">
        <input type="hidden" name="path" id="path" value="sr-portal"/>
        <input type="hidden" name="activeTab" id="activeTab" value="sr-communication-history"/>
        <input type="hidden" name="srCommHistoryFlag" id="srCommHistoryFlag" value="Y"/>
        <input type="hidden" name="srStatusId" id="srStatusId" value="${currentSrStatusId!}"/>
        <input type="hidden" name="srName" id="srName" value="${srName!}"/>
        <input type="hidden" name="recipientPartyEmails" value=""/>
        <input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
        
        <@inputHidden name="ownerBu" id="ownerBu" />
        <#assign srType = EntityQuery.use(delegator).from("WorkEffortAssocTriplet").where("entityName", "Activity", "type", "Type", "value", "E-mail", "active", "Y").queryFirst()! />
       
        <@inputHidden id="srTypeId" value="${(srType.code)!}"/>
        <@inputHidden id="workEffortTypeId" value="${(srType.value)!}"/>
        <@inputHidden id="owner" value="${userLogin.userLoginId?if_exists}" />
        
        <#assign fromPartyEmailId=""/>
		<#assign fromPartyEmailInfo=Static["org.groupfio.common.portal.util.PartyPrimaryContactMechWorker"].getPartyPrimaryContactMechValueMaps(delegator, userLogin.partyId, Static["org.ofbiz.base.util.UtilMisc"].toMap("isRetriveEmail", true),true)!>
		<#if fromPartyEmailInfo?has_content> 
			<#assign fromPartyEmailId=fromPartyEmailInfo.get("EmailAddress")!/>
			<@inputHidden name="nsender" id="nsender" value="${fromPartyEmailId!}" />
		</#if>
		
		<#assign defaultEmailInfo = EntityQuery.use(delegator).from("SystemProperty").where("systemResourceId", "NOTIFICATION", "systemPropertyId", "from").queryFirst()! />
		<#if defaultEmailInfo?has_content>
			<@inputHidden name="defaultFromEmailId" id="defaultFromEmailId" value="${(defaultEmailInfo.systemPropertyValue)!}"/>
		</#if>
		
		<#if srCommPrimaryContactId?has_content>
			<#assign toPartyEmailId= srCommPrimaryContactId />
			<@inputHidden name="contactId" id="contactId" value="${srCommPrimaryContactId}" />
		</#if>
		
		<#assign toPartyEmailInfo=Static["org.groupfio.common.portal.util.PartyPrimaryContactMechWorker"].getPartyPrimaryContactMechValueMaps(delegator, toPartyEmailId, Static["org.ofbiz.base.util.UtilMisc"].toMap("isRetriveEmail", true),true)!>
		<#if toPartyEmailInfo?has_content> 
			<#assign toPartyEmailId=toPartyEmailInfo.get("EmailAddress")!/>
			<@inputHidden name="nto" id="nto" value="${toPartyEmailId!}" />
		</#if>

    	<div class="col-lg-12 col-md-12 col-sm-12">
            <div class="col-md-12 col-md-12 col-sm-12 ">
		     <@dynaScreen 
                instanceId="SR_COMMN_HISTORY_SMS"
                modeOfAction="CREATE"
             />
		</div>

        <div class="row p-2">
            <div class="col-md-12 col-lg-12 col-sm-12 sr-ck-editor" id="emlContent">
            <@textareaLarge
               id="messageContent"
               groupId = "htmlDisplay"
               label="Text"
               rows="3"
               value = template!
               required = true
               txareaClass = ""
               tooltip = "Max limit ${smsCharLimit!} chars"
               />
            </div>
   		</div>
   		<div class="row p-2"><span id="enterred">0</span><span>/${smsCharLimit!} <i>(* Please be aware that messages exceeding 160 characters will be split into subsequent parts).</i></span></div>
        <div class="row p-2">
            <div class="col-md-12 col-lg-12 col-sm-12 sr-ck-editor" id="">
				<div class="form-group row " id="mediaUrls">
				   <label  class="col-sm-4 col-form-label" for="mediaUrl">
				     <h2 class="float-left col-form-label has-error">Media URL</h2>
				   </label>
				   <div class="col-sm-11">
				      <textarea class="form-control" rows="1" id="mediaUrl_0" name="mediaUrl"  autocomplete="off" ></textarea>
				      <div class="form-control-focus">
				      </div>
				      <div class="help-block with-errors" id="mediaUrl_0_error"></div>
				   </div>
				   <span id="rem_mediaUrl_0" onclick="removeMediaUrl('0')" class="btn btn-primary"><li class="fa fa-times-circle custicons"></li></span>
				</div>
            </div>
   		</div>
        <div class="row">
            <div class="form-group 1">
               <div class="text-left ml-3">
                  <span onclick="addMediaUrl()" class="btn btn-primary">Add Another Media URL</span>
               </div>
            </div>
        </div>
        <div class="row">
            <div class="form-group 1">
               <div class="text-left ml-3">
                  <@formButton
                     btn1type="submit"
                     btn1label="Send"
                     btn1onclick="return formSubmissionSms();"
                     btn2=true
                     btn2onclick = "clearCommHistoryForm()"
                     btn2type="reset"
                     btn2label="${uiLabelMap.Clear}"
                   />
               </div>
            </div>
        </div>
    </div>
    </form>
	
</div>
<div class="col-md-12 col-lg-12 col-sm-12" id="com-history">
	
	<#if communicationHistoryList?has_content>
		<#list communicationHistoryList as communicationHistory>
			
			<#if communicationHistory?has_content>
			<#assign fromPartyName = communicationHistory.fromPartyName!>
			<#assign toPartyName = communicationHistory.toPartyName!>
	        <#assign entryDate = communicationHistory.entryDate!>
	        <#assign description = communicationHistory.message!>   
	        <#assign eventId = communicationHistory.eventId!>
	        <#assign isAttachment = communicationHistory.isAttachment!>
	        <#assign direction = communicationHistory.direction!>
	        <#if direction?has_content && direction?if_exists=="OUT">
	        <#assign directionDesc = "Outbound">
	        <#elseif direction?has_content && direction?if_exists=="IN">
	        <#assign directionDesc = "Inbound">
	        </#if>
	        
	        <#-- <#if direction?has_content && direction == "OUT">
				<div class="col-md-12 col-lg-12 col-sm-12" style="background-color: Navy;"> 
				    <div style="color: white;"> <b> ${fromPartyName} said... </b> <span class="float-right"> <b> ${entryDate} </b> </span> </div>
				</div>
			</#if> -->
			
			<div class="col-md-12 col-lg-12 col-sm-12 pd-lr-5" style="background-color: #02829d;"> 
			    <div style="color: white;"> <b> ${fromPartyName} said. </b> <span style="padding-left:15%"> <b>${directionDesc!}</b></span> <span style="padding-left:15%"><b>${toPartyName!}</b></span><span style="padding-left:15%"><b>${communicationHistory.communicationEventType!}</b></span> <span class="float-right"> <b> ${entryDate} </b> </span> </div>
			</div>
			
			<div> &nbsp; </div>
			
			<div class="row">
			<div class="col-md-10 col-lg-10 col-sm-10" >
				 ${StringUtil.wrapString(description)}
			</div>
			<div class="col-md-2 col-lg-2 col-sm-2" >
				 ${toPartyName!}
			</div>
			</div>
			
			<#if isAttachment?if_exists == "Y">
				<div class="row">
	            	<div class="col-md-6 col-sm-6">
		            	<@displayRowFileContent 
							id="attachment"
							label="Attachments"
							commEventId="${eventId!}"
							labelColSize="col-sm-2"
							inputColSize="col-sm-9"
						/>
	            	</div>
	            </div>
            </#if>
            
			<div> &nbsp; </div>
			</#if>
		</#list>
	</#if>
</div>


<@templatePicker 
	instanceId="templatePicker"
/>	


<script>
$('#emailActivityForm').hide();
$('#smsActivityForm').hide();
$('.composeRadios').hide();
$(document).ready(function() {
	function updateTextCount(){
		$('#enterred').text($('#messageContent').val().length);
	}
	$('#messageContent').on('input', function() {
		updateTextCount();
	});
	/*$('#emailActivityForm').hide();
	$('#smsActivityForm').hide();
	$('.composeRadios').hide();*/

	var clicks = 0;

	$('#compose_btn').on('click', function() {
		clicks++;
		if(clicks % 2 == 0){
			$('#emailActivityForm').hide();
			$('#smsActivityForm').hide();
		}else{
			displayComposeForms();
		}
		$('.composeRadios').toggle();
		$('.composeChecks').toggle();
	});

	function displayComposeForms(){
		$("#messageContent").val("");
		var radio = $("input[name='searchTypeUI']:checked").val();
		if(radio == "SMS_COMMUNICATION"){
			$("input[name='templateCategories']").val("SMS");
			$('#emailActivityForm').hide();
			$('#smsActivityForm').show();
			$("#messageContent").val('$'+"{SR_NUMBER}");
			updateTextCount();
		}else if(radio == "EMAIL_COMMUNICATION"){
			$("input[name='templateCategories']").val('');
			$('#emailActivityForm').show();
			$('#smsActivityForm').hide();
		}
	}

	$('.composeRadios').on('click', function() {
		displayComposeForms();
	});
	
	$('.composeChecks').on('click', function() {
		var elementAll = $(this).find('input[name="displayChecks"]:first');
		if($(elementAll).val()=="ALL" && $(elementAll).prop("id")=="All"){
			if($(elementAll).prop("checked")){
				$('input[name="displayChecks"]').prop("checked",true);
			}else{
				$('input[name="displayChecks"]').prop("checked",false);
			}
		}else{
			if($('input[name="displayChecks"]:not("#All"):checked').length === $('input[name="displayChecks"]:not("#All")').length){
				$('#All').prop("checked",true);
			}else{
				$('#All').prop("checked",false);
			}
		}
		loadHistory();
	});
	$("#messageContent").attr("maxlength","${smsCharLimit!}");
	loadAssociatedParties();
	loadHistory();
	updateTextCount();
});
function loadHistory(){
	var selectedOptions = [];
	$('input[name="displayChecks"]:checked').each(function() {
		selectedOptions.push($(this).val());
	});
	$("#com-history").html("");
	var history = '';
	$.ajax({
		type: "POST",
		url: "getAllCommunicationHistory",
		async: true,
		data: { "custRequestId": '${custRequestId!}', "filters" : selectedOptions.join(','), 'limit':"${historyLimit!}" },
		success: function(data) {
			if(data && data.records && data.records.length > 0){
				data.records.forEach((item, index) => {
					var directionDesc = "";
					if(item.direction=="OUT"){
						directionDesc = '<i class="fa fa-share" aria-hidden="true" style="font-size:16px"></i>';
					}else if(item.direction=="IN"){
						directionDesc = '<i class="fa fa-reply" aria-hidden="true" style="font-size:16px"></i>';
					}
					item.communicationEventTypeDescIcon = item.communicationEventTypeDesc;
					if(item.communicationEventTypeDesc){
						if(item.communicationEventTypeDesc == 'SMS'){
							item.communicationEventTypeDescIcon = '<i class="fa fa-comments" aria-hidden="true" style="font-size:16px"></i>';
						}else if(item.communicationEventTypeDesc == 'EMAIL'){
							item.communicationEventTypeDescIcon = '<i class="fa fa-envelope" aria-hidden="true" style="font-size:16px"></i>';
						}
					}
					var fromPartyName = item.fromPartyName || '';
					var toPartyName = item.toPartyName || '';
					var communicationEventTypeDescIcon = item.communicationEventTypeDescIcon || '';
					var entryDate = item.entryDate || '';
					history += '<div class="col-md-12 col-lg-12 col-sm-12 pd-lr-5" style="background-color: #02829d;">';
					history += '<div style="color: white;"> <b> '+fromPartyName+' said.. </b> <span style="padding-left:30%"><b>'+toPartyName+'</b></span><span class="float-right"><b>'+communicationEventTypeDescIcon+'</b> &nbsp; <b> '+entryDate+' </b> &nbsp; <b>'+directionDesc+'</b> </span> </div>';
					history += '</div>';
					history += '<div> &nbsp; </div>';
					history += '<div class="row">';
						if(item.communicationEventTypeDesc == 'SMS'){
							history += '<div class="col-md-9 col-lg-9 col-sm-9" >';
						}else{
							history += '<div class="col-md-10 col-lg-10 col-sm-10" >';
						}
					history += '<iframe class="dynamic-iframe" width="110%" height="300px" srcdoc="' + item.message.replace(/"/g, '&quot;') + '"></iframe>';
					history += '</div>';
					if(item.communicationEventTypeDesc == 'SMS'){
						history += '<div class="col-md-3 col-lg-3 col-sm-3" >';
						if(item.toPartyNamePhone){
								let toPartyNamePhone = item.toPartyNamePhone.split(',');
								toPartyNamePhone.forEach(function(partyNamePhone) {
									history += partyNamePhone + "<br>";
								});
							}
						history += '</div>';
					}
					history += '</div>';
					if(item.isAttachment && item.isAttachment == "Y"){
						history += '<div class="row">';
						history += '<div class="col-md-6 col-sm-6">';
						history += '<div class="col-md-6 col-sm-6">';
						history += '<div class="form-group row attachment file-content" id="attachment_row">';
						history += '<label class="col-sm-2 field-text" id="'+item.eventId+'_label">Attachments </label>';
						history += '<div class=" col-sm-9 left">';
						item.fileContents.forEach((fileContent) => {
							history += '<a href="/partymgr/control/ViewSimpleContent?contentId='+fileContent.contentId+'&externalLoginKey=${requestAttributes.externalLoginKey!}" class="buttontext"><u>'+fileContent.contentName+'</u></a>&nbsp;';
						});
						history += '</div>';
						history += '</div>';
						history += '</div>';
						history += '</div>';
					}
					history += '<div> &nbsp; </div>';
					if(item.imgData){
						history += '<div class="row">';
						history += '<div class="col-md-6 col-sm-6">';
						history += '<div class="col-md-6 col-sm-6">';
						history += '<div class="form-group row attachment file-content" id="attachment_row">';
						history += '<label class="col-sm-2 field-text" id="'+item.eventId+'_label">Images</label>';
						history += '<div class=" col-sm-9 left">';
						let imgLength = item.imgData.split(",").length;
						item.imgData.split(",").forEach((img,index) => {
							history += '<a target="_Blank" href="'+img+'" class="buttontext"><u>'+img.split('/').pop().split('.').slice(0, -1).join('.')+'</u></a>';
							if(index !== (imgLength - 1)){
								history += ',&nbsp;'
							};
						});
						history += '</div>';
						history += '</div>';
						history += '</div>';
						history += '</div>';
					}
					history += '<div> &nbsp; </div>';
				});
			}
			$("#com-history").html(history);
			setTimeout(function() {
				$('.dynamic-iframe').each(function() {
					var iframe = this;
					var doc = iframe.contentWindow.document;
					var iframeHeight = calculateTotalContentHeight(doc);
					$(iframe).height(iframeHeight);
				});
			}, 1000);
		},
		error: function(data) {
			result=data;
			showAlert("error", "Error occured while fetching Communication History");
		}
	});
}
function calculateTotalContentHeight(doc) {
	let totalHeight = 0;
	$('body', doc).children().each(function() {
		totalHeight += $(this).outerHeight(true);
	});
	if (totalHeight === 0 || totalHeight < 40) {
		totalHeight = 40;
	}
	return totalHeight;
}
function clearCommHistoryForm(){
	CKEDITOR.instances.emailContent.setData("");
}

function getBusinessUnit() {
	var owner = $('#owner').val();
    $.ajax({
        type: "POST",
        url: "getBusinessUnitName",
        async: false,
         data: { "owner": owner },
        success: function(data) {
            result=data;
            if(result && result[0] != undefined && result[0].businessId != undefined){
            	$("#ownerBu").val(result[0].businessId);
            }else{
            	$("#ownerBu").val("");
            }
        },error: function(data) {
        	result=data;
			showAlert("error", "Error occured while fetching Business Unit");
		}
	});
}
	
function formSubmission(){
	var valid = true;
	var emailInstance = CKEDITOR.instances.emailContent;
	//if(validateCKEDITORforBlank((CKEDITOR.instances.emailContent.getData().replace(/<[^>]*>|\s/g, '')).trim())){
	if(isEmptyCKEd(emailInstance)) {
		showAlert("error", "Please Enter Comment");
		valid = false;
	}else{
		var htmlContent = CKEDITOR.instances["emailContent"].getData();
		$('#emailContent').val(htmlContent);
		
		$('#SrEmail input[name=recipientPartyEmails]').val($('#commHisPartyId').val());
		
		valid = true;
	}
		
 	return valid;
}
function formSubmissionSms(){	
	return true;
}

function loadAssociatedParties() {
	console.log('loadAssociatedParties ...... ');
	
	$('#commHisPartyId').empty();
	let userOptionList = '<option value="" selected="">Select Recipients</option>';	
	let smsUserOptionList = '<option value="" selected="">Select Recipients</option>';	
	
	$.ajax({
		async : false,
		url : '/sr-portal/control/getSrAssocParties',
		type : "POST",
		data: {"srNumber": '${custRequestId!}', "externalLoginKey": "${requestAttributes.externalLoginKey!}"},
		success : function(result) {
			if (result.list) {
				for (var i = 0; i < result.list.length; i++) {
					var data = result.list[i];
					if (data.infoString) {
						userOptionList += '<option value="'+data.infoString+'">'+data.name+' - '+data.roleTypeDesc+' ('+data.infoString+')'+'</option>';
					}
					if(data.phoneNumber && data.phoneSolicitation == "Y"){
						smsUserOptionList += '<option selected=selected value="'+data.partyId+'">'+data.name+' - '+data.roleTypeDesc+' '+data.phoneNumber+'</option>';
					}
				}
			}
		}
	});
		
	$('#commHisPartyId').html(userOptionList);
	$('#commHisPartyId').dropdown('refresh');
	$('#commHisSmsPartyId').html(smsUserOptionList);
	$('#commHisSmsPartyId').dropdown('refresh');
}
$("#smsTemplateId_desc").change(function() {
	var smsTemplateId = $("#smsTemplateId_val").val();
	loadSmsTemplate(smsTemplateId);
});
function loadSmsTemplate(smsTemplateId){
	var smsTemplate = "";
	$.ajax({
		type: "POST",
		url: "loadTemplate",
		data: { "templateId": smsTemplateId },
		async: false,
		success: function(result) {
			if(result && result[0] && result[0].textContent){
				$("#messageContent").val("$"+"{SR_NUMBER}\n"+result[0].textContent);
			}else if (result && result[0]){
				$("#messageContent").val("$"+"{SR_NUMBER}\n");
			}
			$('#enterred').text($('#messageContent').val().length);
		}
	});
}
</script>
<style>
	html{
		overflow-y: auto !important;
	}
	.pd-lr-5 {
		padding-left: 5px !important;
		padding-right: 5px !important;
	}
</style>
<script>
let imageUrls = 1;
function addMediaUrl() {
	let mediaUrlHtml = `<div class="col-sm-11" style="padding-top:5px">
							<textarea class="form-control" rows="1" id="mediaUrl_`+imageUrls+`" name="mediaUrl"  autocomplete="off" ></textarea>
							<div class="form-control-focus"></div>
							<div class="help-block with-errors" id="mediaUrl_`+imageUrls+`_error"></div>
						</div>
						<span id="rem_mediaUrl_`+imageUrls+`" onclick="removeMediaUrl('`+imageUrls+`')" class="btn btn-primary" style="padding-top:5px"><li class="fa fa-times-circle custicons"></li></span>`;
						imageUrls++;
						$("#mediaUrls").append(mediaUrlHtml);
}

function removeMediaUrl(index) {
	$("#mediaUrl_"+index).parent().remove();
	$("#rem_mediaUrl_"+index).remove();
}
</script>