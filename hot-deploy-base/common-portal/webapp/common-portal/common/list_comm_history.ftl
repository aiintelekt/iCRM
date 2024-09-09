<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/lib/picker_macro.ftl"/>
<script type="text/javascript" src="/account-portal-resource/js/emailActivity.js"></script>
<script src="/bootstrap/js/ckeditor/ckeditor.js" type="text/javascript"></script>
<script src="/bootstrap/js/ckeditor/ck-custom-functions.js" type="text/javascript"></script>

<#if mainAssocPartyId?has_content>
<#assign partyId= mainAssocPartyId />
<#else>
<#assign partyId= request.getParameter("partyId")! />
<#assign salesOpportunityId = '${requestParameters.salesOpportunityId!}' >
</#if>
<#if !(partyId?has_content) && userLogin?exists>
	<#assign partyId =userLogin.get('partyId')!''/>
</#if>
<#if partyId?has_content>
	<#assign cNo = partyId! />
</#if>
<#assign requestURI = request.getRequestURI()/>
<#if requestURI.contains("screenRender")>
<#assign requestURI=request.getParameter("requestUri")!>
</#if>
<#assign searchType = requestParameters.searchType! />
<#assign nonPartyDomain = false />
<#assign composeEnable = false />
<#if requestURI?has_content>
<#if requestURI.contains("viewOpportunity")>
	<#assign nonPartyDomain = true />
	<#assign composeEnable = true />
	<#assign searchType = "QUEUE" />
	<#assign subSearchType = "OPPORTUNITY" />
	<#assign partyId = ""/>
	<#assign domainType = "OPPORTUNITY" />
	<#assign domainId = requestParameters.salesOpportunityId! />
	<#assign subject = 'Updated OP - [${requestParameters.salesOpportunityId!!}] - ${inputContext?if_exists.opportunityName!}'>
<#elseif requestURI.contains("viewAccount") || requestURI.contains("viewCustomer") >
	<#assign composeEnable = true />
<#else>
</#if>
</#if>
<div class="pt-2 align-lists">
	<form method="post" id="comm-history-search-form" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
		<@inputHidden id="partyId" value="${partyId!}"/>
		<@inputHidden id="searchType" value="${searchType!}"/>
		<@inputHidden id="subSearchType" value="${subSearchType!}"/>
		<@inputHidden id="domainEntityType" value="${domainType!}"/>
		<@inputHidden id="domainEntityId" value="${domainId!}"/>
		
		<@inputHidden id="requestURI" value="${requestURI!}"/>
		<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
	</form>
</div>

<#assign helpUrl = Static["org.groupfio.common.portal.util.DataHelper"].getHelpUrl(delegator, requestURI, "bookmarksAndFiles") />

<#assign extraLeft = '<div class="form-check-inline">
				    <label for="searchTypeAll">
				    <input type="radio" id="searchTypeAll" name="searchTypeUI" value="ALL" class="form-check-input" checked>
				    <span></span>
				    <span class="check"></span>
				    <span class="box"></span>
				 	All</label>
				</div>
				<div class="form-check-inline">
				    <label for="searchTypeCustRequest">
				    <input type="radio" id="searchTypeCustRequest" name="searchTypeUI" value="SERVICE_REQUEST" class="form-check-input">
				    <span></span>
				    <span class="check"></span>
				    <span class="box"></span>
				    Service Request</label>
				</div>
				<div class="form-check-inline">
				    <label for="searchTypeOppo">
				    <input type="radio" id="searchTypeOppo" name="searchTypeUI" value="OPPORTUNITY" class="form-check-input">
				    <span></span>
				    <span class="check"></span>
				    <span class="box"></span>
				 	Opportunity</label>
				</div>
				<div class="form-check-inline">
				    <label for="searchTypeOrder">
				    <input type="radio" id="searchTypeOrder" name="searchTypeUI" value="ORDER" class="form-check-input">
				    <span></span>
				    <span class="check"></span>
				    <span class="box"></span>
				    Order</label>
				</div>
				'/>
<#if nonPartyDomain>
	<#assign extraLeft = '' />
</#if>				
<#if composeEnable!>
	<#assign hasPermission = Static["org.fio.homeapps.util.DataUtil"].hasPermission(request, "CREATE_OPERATION")?if_exists />
	<#if hasPermission>
		<#assign extraLeft = extraLeft + '<input type="button" id="compose_btn" name="compose_btn" class="btn btn-xs btn-primary" value="Compose">' />
	</#if>
</#if>

<@sectionFrameHeaderTab title="Communication History" extra=extraLeft! tabId="commHistory" helpBtnStyle="margin-top: 4px;"/> 

<div class="col-md-12 col-lg-12 col-sm-12" id="activityForm">
	<form method="post" action="<@ofbizUrl>createCommunicationHistoryEvent</@ofbizUrl>" id="SrEmail" class="form-horizontal" name="SrCommMainForm" novalidate="novalidate" data-toggle="validator" enctype="multipart/form-data">
        <input type="hidden" name="partyId" id="partyId" value="${cNo!}"/>
        <input type="hidden" name="cNo" id="cNo" value="${cNo!}"/>
		<input type="hidden" name="domainEntityType" value="${domainEntityType!}"/>
		<input type="hidden" name="domainEntityId" value="${domainEntityId!}"/>
        <input type="hidden" name="direction" id="direction" value="OUT"/>
        <input type="hidden" name="ccEmailIds" id="ccEmailIds" value = "${fsrOwnerEmail!}"/>
        <input type="hidden" name="subject" id="subject" value="${subject!}">
        <@inputHidden id="workEffortTypeId" value="E-Mail"/>
        <input type="hidden" name="recipientPartyEmails" value=""/>
        <input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
        
        <@inputHidden name="ownerBu" id="ownerBu" />
      
        <@inputHidden id="owner" value="${userLogin.userLoginId?if_exists}" />
        
        <#assign fromPartyEmailId=""/>		                     				
		<#assign fromPartyEmailInfo=Static["org.groupfio.common.portal.util.PartyPrimaryContactMechWorker"].getPartyPrimaryContactMechValueMaps(delegator, userLogin.partyId, Static["org.ofbiz.base.util.UtilMisc"].toMap("isRetrivePhone", true, "isRetriveEmail", true),true)!>
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
		
		<#assign toPartyEmailInfo=Static["org.groupfio.common.portal.util.PartyPrimaryContactMechWorker"].getPartyPrimaryContactMechValueMaps(delegator, toPartyEmailId, Static["org.ofbiz.base.util.UtilMisc"].toMap("isRetrivePhone", true, "isRetriveEmail", true),true)!>
		<#if toPartyEmailInfo?has_content> 
			<#assign toPartyEmailId=toPartyEmailInfo.get("EmailAddress")!/>
			<@inputHidden name="nto" id="nto" value="${toPartyEmailId!}" />
		</#if>

    	<div class="col-lg-12 col-md-12 col-sm-12">
            <div class="col-md-12 col-md-12 col-sm-12 ">
		     <@dynaScreen 
                instanceId="COMM_HISTORY"
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
					//removePlugins: 'resize',
					customConfig : '/bootstrap/js/ckeditor/ck-custom-config.js',
		        	autoGrow_minHeight : 800
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

<@templatePicker 
	instanceId="templatePicker"
/>
  
<div class="col-md-12 col-lg-12 col-sm-12" id="com-history">
	
	<#if communicationHistoryList?has_content>
		<#list communicationHistoryList as communicationHistory>
			
			<#assign fromPartyName = communicationHistory.fromPartyName! />
	        <#assign entryDate = communicationHistory.entryDate! />
	        <#assign description = communicationHistory.message! /> 
	        <#assign bodyPreview = communicationHistory.bodyPreview! />   
	        <#assign eventId = communicationHistory.eventId! />
	        <#assign isAttachment = communicationHistory.isAttachment! />
	        <#assign direction = communicationHistory.direction! />
	        
			
			<div class="col-md-12 col-lg-12 col-sm-12" style="background-color: #02829d;"> 
			    <div style="color: white;"> <b> ${fromPartyName!} said... </b> <span class="float-right"> <b> ${entryDate!} </b> </span> </div>
			</div>
			
			<div> &nbsp; </div>
			
			<div class="row">
				<div class="col-md-10 col-lg-10 col-sm-10" >
					 ${StringUtil.wrapString(description!'')}
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
							labelColSize="col-sm-2" inputColSize="col-sm-9"
						/>
	            	</div>
	            </div>
            </#if>
            
			<div> &nbsp; </div>
			 
		</#list>
	</#if>
	
</div>

<script>
$('#activityForm').hide();
$(document).ready(function() {

postLoadGridData(null, null, "a-communicationInfo", loadCommunicationHistory);
postLoadGridData(null, null, "c-communicationHistory", loadCommunicationHistory);
postLoadGridData(null, null, "oppo-communication-history", loadCommunicationHistory);
postLoadGridData(null, null, "a-communication-history", loadCommunicationHistory);
postLoadGridData(null, null, "sr-communication-history", loadCommunicationHistory);

$(".commHisPartyId-input").one( "click",function(){
	loadAssocParties();
});

});

$(function() {
	$('#compose_btn').on('click', function() {
		$('#activityForm').toggle();
	});
	
	if($('input[type=radio][name="searchTypeUI"]').val()) {
		$("#comm-history-search-form input[type=hidden][name='searchType']").val($('input[type=radio][name="searchTypeUI"]').val());
	}
});

$('input[type=radio][name="searchTypeUI"]').on('change', function() {
    $("#comm-history-search-form input[type=hidden][name='searchType']").val(this.value);
	loadCommunicationHistory();
});
function loadCommunicationHistory() {
	$.ajax({
		async: true,
		url: '/common-portal/control/getEmailCommunicationHistory',
		type: "POST",
		data: $("#comm-history-search-form").serialize(),
		success: function(data) {
			console.log("Received data:", data);
			if (data.list && Array.isArray(data.list) && data.list.length > 0) {
				let resultList = data.list;
				console.log("resultList:", resultList);
				let fragment = document.createDocumentFragment(); // Create a document fragment
				for (let i = 0; i < resultList.length; i++) {
					let commData = resultList[i];
					let fromPartyName = commData.fromPartyName || "";
					let entryDate = commData.entryDate || "";
					let toEmailId = commData.toEmailId || "";
					let fromEmailId = commData.fromEmailId || "";
					let message = commData.message || "";
					let subject = commData.subject || "";
					let isAttachment = commData.isAttachment || "";
					let fileContents = commData.fileContents || [];
					let partyNameDiv = document.createElement('div');
					partyNameDiv.className = 'col-md-12 col-lg-12 col-sm-12';
					partyNameDiv.style.backgroundColor = '#02829d';
					partyNameDiv.style.padding = '4px';
					partyNameDiv.innerHTML = '<div style="color: white; font-family: Frutiger Next Pro, Frutiger, Arial, sans-serif;font-size: 1rem;"> &nbsp;&nbsp;<b> ' + fromPartyName + ' said... </b> <span style="padding-left: 5%;"><strong>Sub: </strong>' + subject + '</span><span class="float-right"> <b style="float:right;"> ' + entryDate + ' </b> &nbsp;</span> </div>';
					fragment.appendChild(partyNameDiv);
					let toEmaildiv = document.createElement('div');
					toEmaildiv.className = 'col-md-12 col-lg-12 col-sm-12';
					toEmaildiv.style.paddingBottom = '10px';
					toEmaildiv.style.paddingTop = '5px';
					toEmaildiv.innerHTML = '<div class="row"><br><div class="col-md-4 col-lg-4 col-sm-4" ><span style="float:right;  margin-top: -5px; font-family: Frutiger Next Pro, Frutiger, Arial, sans-serif; color:#212529;"><b>To: </b><span style="float:right;">' + toEmailId + '</span>&nbsp;</span></div><br><div class="col-md-8 col-lg-8 col-sm-8" style="color:#212529; margin-left: 10px;">' + message + '</div></div>';
					fragment.appendChild(toEmaildiv);
					if (isAttachment === "Y" && fileContents.length) {
						let attachmentDiv = document.createElement('div');
						let labelTag = document.createElement('label');
						labelTag.innerHTML = ` &nbsp;Attachments : `;
						attachmentDiv.appendChild(labelTag); // Append label to attachmentDiv first
						for (let j = 0; j < fileContents.length; j++) {
							let contentId = fileContents[j].contentId || "";
							let contentName = fileContents[j].contentName || "";
							if (contentId && contentName) {
								let a = document.createElement('a');
								a.href = '/partymgr/control/ViewSimpleContent?contentId=' + contentId + '&externalLoginKey=' + '${requestAttributes.externalLoginKey!}';
								a.className = 'buttontext';
								let u = document.createElement('u');
								u.innerHTML = contentName;
								a.appendChild(u); // Append <u> to <a>
								attachmentDiv.appendChild(a); // Append <a> to attachmentDiv
								attachmentDiv.appendChild(document.createTextNode(' '));
							}
						}
						fragment.appendChild(attachmentDiv); // Append the entire attachmentDiv to the fragment
						let spaceDiv = document.createElement('div');
						spaceDiv.innerHTML = '&nbsp;';
						fragment.appendChild(spaceDiv);
					}
				}
				setTimeout(function() {
					let iframe = $('<iframe>', {
						src: 'about:blank',
						frameborder: '0',
						width: '100%',
						height: '500px'
					}).appendTo('#com-history');
					iframe.each(function() {
						let iframeElement = this;
						let doc = iframeElement.contentWindow.document;
						doc.open();
						doc.write('<!DOCTYPE html><html><head><title>Communication History</title></head><body></body></html>');
						doc.close();
						let body = $(doc.body);
						body.append(fragment); // Append the fragment to the iframe's body
						let iframeHeight = calculateTotalContentHeight(doc);
						$(iframeElement).height(iframeHeight);
					});
				}, 1000);
			} else {
				$("#com-history").html('<div class="row"><div class="col-md-12 col-lg-12 col-sm-12" style="text-align: center;"><span><b>No record found.</b></span></div></div>');
				console.error("data.list is not an array or is undefined");
			}
		},
		error: function(jqXHR, textStatus, errorThrown) {
			console.error("AJAX request failed:", textStatus, errorThrown);
		}
	});
}
function calculateTotalContentHeight(doc) {
	let totalHeight = 0;
	$(doc.body).children().each(function() {
		let elementHeight = $(this).outerHeight(true);
		totalHeight += elementHeight;
	});
		totalHeight = totalHeight +100;
		return totalHeight;
}
function clearCommHistoryForm(){
	CKEDITOR.instances.emailContent.setData("");
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

function loadAssocParties() {
	console.log('loadAssocParties ...... ');
	
	$('#commHisPartyId').empty();
	let userOptionList = '<option value="" selected="">Select Recipients</option>';	
	
	$.ajax({
		async : false,
		url : '/common-portal/control/loadAssocParties',
		type : "POST",
		data: {"domainType": "${domainType!}", "domainId": "${domainId!}", "externalLoginKey": "${requestAttributes.externalLoginKey!}"},
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