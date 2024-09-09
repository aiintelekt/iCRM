<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/lib/picker_macro.ftl"/>
<script type="text/javascript" src="/account-portal-resource/js/emailActivity.js"></script>
<script src="/bootstrap/js/ckeditor/ckeditor.js" type="text/javascript"></script>
<script src="/bootstrap/js/ckeditor/ck-custom-functions.js" type="text/javascript"></script>

<#assign readOnlyPermission = Static["org.fio.homeapps.util.DataUtil"].hasPermissionWoFullPerm(request, "READ_ONLY_PERM")?if_exists />
<style>
.dynamic-iframe {
	border: rgb(255, 255, 255);
}
</style>
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
<#assign extra= "">
<#if readOnlyPermission!>
<#else>
	<#if currentSrStatusId?has_content && currentSrStatusId != "SR_CLOSED" && currentSrStatusId != "SR_CANCELLED">
	<#assign hasPermission = Static["org.fio.homeapps.util.DataUtil"].hasPermission(request, "CREATE_OPERATION")?if_exists />
	<#if hasPermission>
		<#assign extra= extra+'<button class="btn btn-sm btn-primary" style="font-size: 11px;" id="compose_btn">${uiLabelMap.Compose}</button>' />
	</#if>
	</#if>
</#if>
<@sectionFrameHeaderTab title="Communication History" tabId="communicationHistory" extra=extra?if_exists isShowHelpUrl="Y" />

<#assign supportEmailId = EntityQuery.use(delegator).from("PretailLoyaltyGlobalParameters").where("parameterId", "TO_EMAIL_ID").queryOne()! />
<#if supportEmailId?exists && supportEmailId?has_content>
	<#assign supportEmailId = supportEmailId.value!>
</#if>

<#assign ccEmailIds = supportEmailId!>
<#if fsrOwnerEmail?has_content>
	<#assign ccEmailIds = fsrOwnerEmail + "," + ccEmailIds!>
</#if>
<div class="col-md-12 col-lg-12 col-sm-12" id="activityForm">
	<form method="post" action="<@ofbizUrl>createCommunicationHistoryEvent</@ofbizUrl>" id="SrEmail" class="form-horizontal" name="SrCommMainForm" novalidate="novalidate" data-toggle="validator" enctype="multipart/form-data">
        <input type="hidden" name="custRequestId" id="custRequestId" value="${custRequestId!}"/>
		<input type="hidden" name="partyId" id="partyId" value="${fromPartyId!}"/>
        <input type="hidden" name="cNo" id="cNo" value="${fromPartyId!}"/>
		<input type="hidden" name="domainEntityType" value="${domainEntityType!}"/>
		<input type="hidden" name="domainEntityId" value="${domainEntityId!}"/>
        <input type="hidden" name="direction" id="direction" value="OUT"/>
        <input type="hidden" name="ccEmailIds" id="ccEmailIds" value = "${ccEmailIds!}"/>
        <input type="hidden" name="subject" id="subject" value="${emailSubject!}">
        <input type="hidden" name="path" id="path" value="ticket-portal"/>
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

<div class="col-md-12 col-lg-12 col-sm-12" id="com-history">
    <iframe style="max-width: 100%; width: 100%;" class="dynamic-iframe" id="iframe-1" srcdoc="
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
                    <div class='col-md-12 col-lg-12 col-sm-12' style='background-color: #02829d;'>
                        <div style='color: white;'><b>&nbsp;${fromPartyName!} said...</b> <span class='' style='float: right !important;'><b>${entryDate!}&nbsp;</b></span></div>
                    </div><div> &nbsp; </div>
                    <div class=''><span>&nbsp; ${description!}</span><span style='float:right;'>${toPartyName!}</span>&nbsp;</div>
                    <#if isAttachment?if_exists == 'Y'>
                        <div class='row'>
                            <div class='col-md-6 col-sm-6'>
                                <@displayIframeRowFileContent 
                                    id='attachment'
                                    label='Attachments'
                                    commEventId='${eventId!}'
                                    labelColSize='col-sm-2' inputColSize='col-sm-9'
                                />
                            </div>
                        </div>
                    </#if>
                    <div>&nbsp;</div>
                </#if> 
            </#list>
        </#if>
    "></iframe>
</div>


<@templatePicker 
	instanceId="templatePicker"
/>	


<script>
$('#activityForm').hide();
$(document).ready(function() {
$('#compose_btn').on('click', function() {
	$('#activityForm').toggle();
});
	setTimeout(function() {
		setIframeHeight();
	}, 5000);
	function calculateTotalContentHeight(doc) {
		let totalHeight = 0;
		$(doc.body).children().each(function() {
			let elementHeight = $(this).outerHeight(true);
			totalHeight += elementHeight;
		});
			totalHeight = totalHeight +100;
			console.log("totalHeight----"+totalHeight);
			return totalHeight;
	}
	function setIframeHeight() {
		var iframe = document.getElementById('iframe-1');
		if (iframe) {
			var doc = iframe.contentDocument || iframe.contentWindow.document;
			var totalHeight = calculateTotalContentHeight(doc);
			iframe.style.height = totalHeight + 'px';
		}
	}
loadAssociatedParties();
	
});
	
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

function loadAssociatedParties() {
	console.log('loadAssociatedParties ...... ');
	
	$('#commHisPartyId').empty();
	let userOptionList = '<option value="" selected="">Select Recipients</option>';	
	
	$.ajax({
		async : false,
		url : '/ticket-portal/control/getSrAssocParties',
		type : "POST",
		data: {"srNumber": '${custRequestId!}', "externalLoginKey": "${requestAttributes.externalLoginKey!}"},
		success : function(result) {
			if (result.list) {
				for (var i = 0; i < result.list.length; i++) {
					var data = result.list[i];		
					if (data.infoString) {
						userOptionList += '<option value="'+data.infoString+'">'+data.name+' - '+data.roleTypeDesc+' ('+data.infoString+')'+'</option>';	
					}
				}
			}
		}
	});
		
	$('#commHisPartyId').html(userOptionList);
	$('#commHisPartyId').dropdown('refresh');
}
		
</script>
<style>
#sr-communication-history input#compose_btn{
margin-right: 7px !important;
}
</style>