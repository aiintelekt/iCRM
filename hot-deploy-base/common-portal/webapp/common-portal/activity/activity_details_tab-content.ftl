<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#assign readOnlyPermission = Static["org.fio.homeapps.util.DataUtil"].hasPermissionWoFullPerm(request, "READ_ONLY_PERM")?if_exists />
<#if readOnlyPermission!>
<script>
	$(document).ready(function(){
        $('a').each(function(){ 
        	var elementId = $(this).attr('id')
        	if(elementId === "link_contactId"){
        		$(this).addClass("view-link");
        		$(this).attr("href", "#"); // Set herf value
            	$(this).attr("target","");	
        	}
        });
        
        $("a.view-link").click(function () {
            $("#accessDenied").modal("show");
            return false;
        });
    });
</script>
</#if>
<#assign updateUrl = "updateActivity">
<#if inputContext?has_content & inputContext.workEffortTypeId?has_content && inputContext.workEffortTypeId=="APPOINTMENT">
<#assign updateUrl = "updateApnt">
<#-- <#elseif inputContext.workEffortTypeId=="TASK">
<#assign updateUrl = "updateTask"> -->
</#if>

<#assign dynaSuffix = ''>
<#assign isSRActivity = 'Y' />
<#if domainEntityType?has_content && domainEntityType == 'REBATE'>
	<#assign dynaSuffix = '_RBT'>
	<#assign isSRActivity = 'N' />
<#elseif domainEntityType?has_content && domainEntityType == 'ACCOUNT'>
	<#assign dynaSuffix = '_ACCT'>
	<#assign isSRActivity = 'N' />
<#elseif domainEntityType?has_content && domainEntityType == 'OPPORTUNITY'>
	<#assign dynaSuffix = '_OPPO'>
	<#assign isSRActivity = 'N' />
<#elseif domainEntityType?has_content && domainEntityType == 'CUSTOMER'>
	<#assign dynaSuffix = '_CUST'>
	<#assign isSRActivity = 'N' />	
<#elseif isProgAct?has_content && isProgAct=="Y">
	<#assign dynaSuffix = '_PROG'>
	<#assign isSRActivity = 'N' />	
<#elseif domainEntityType?has_content && domainEntityType == 'CONTACT'>
	<#assign dynaSuffix = '_CONT'>
	<#assign isSRActivity = 'N' />		
<#elseif domainEntityType?has_content && domainEntityType == 'LEAD'>
	<#assign dynaSuffix = '_LEAD'>
	<#assign isSRActivity = 'N' />		
</#if>
<#if readOnlyPermission!>
        	<#else>
	        	<ul class="flot-icone">
	                <#if isWorkflowActivity?has_content && isWorkflowActivity =="Y" && workflowCategoryId=='APVL_CAT_PAYOUT'>
	                <#assign analysisUrl = '#'>
					<#if agreementYear?has_content && rebatePartyIdTo?has_content>
					<#assign analysisUrl = '${agreementReportUrl!}&invoice_year=${agreementYear!}&agreement_id=${agreementId!}'>
					</#if>
	                <li class="mt-0">
	                	<a href="${analysisUrl!}" class="btn btn-xs btn-primary m5" target="_blank"><i class="fa fa-eye" aria-hidden="true"></i> Rebate Analysis</a>
	                </li>
	                </#if>
	                
	                <#if (inputContext.workEffortTypeId?has_content && (inputContext.workEffortTypeId !="EMAIL" && inputContext.workEffortTypeId!="PHONE"))&&(inputContext.currentStatusId!="IA_MCOMPLETED" && inputContext.currentStatusId !="IA_CLOSED") && (isWorkflowActivity?has_content && isWorkflowActivity=="N") && (!isProgAct?has_content || isProgAct=="N")>
	                <#if (inputContext.isSchedulingRequired?has_content && inputContext.isSchedulingRequired=="N") && (inputContext.statusId=="IA_OPEN" || inputContext.statusId=="IA_MSCHEDULED" || inputContext.statusId=="IA_MIN_PROGRESS") && (isScheduleTask?has_content && "N" == isScheduleTask) >
	                <li class="mt-0">
	                	<#if completeConfirm?has_content && completeConfirm == "Y">
	                		<button type="button" class="btn btn-primary btn-xs mt-0" id="doCancel" data-toggle="confirmation" title="Are you sure you want to close this Activity?"><i class="fa fa-save" aria-hidden="true"></i>&nbsp;&nbsp;Mark Complete</button>
	                	<#else>
	                		<#-- <button type="button" class="btn btn-primary btn-xs mt-0" id="doCancel"><i class="fa fa-save" aria-hidden="true"></i>&nbsp;&nbsp;Mark Complete</button>-->
	                	</#if>
	                </li>
	                </#if>
	                <li class="mt-0">
	                	<#if domainEntityFieldId?exists && domainEntityFieldId?has_content && domainEntityFieldId != "partyId">
	                    	<a href="${updateUrl!}?workEffortId=${inputContext.workEffortId!}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&${domainEntityFieldId!}=${domainEntityId!}&partyId=${partyId!}&externalLoginKey=${requestAttributes.externalLoginKey!}" class="btn btn-xs btn-primary"><i class="fa fa-edit" aria-hidden="true"></i> Update</a>
	                    <#else>
	                    	<a href="${updateUrl!}?workEffortId=${inputContext.workEffortId!}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&partyId=${partyId!}&externalLoginKey=${requestAttributes.externalLoginKey!}" class="btn btn-xs btn-primary"><i class="fa fa-edit" aria-hidden="true"></i> Update</a>
	                    </#if>
	                </li>
	            	</#if>
	            	<#if communicationEventTypeId?exists && communicationEventTypeId?has_content && communicationEventTypeId == "SMS_COMMUNICATION">
	            		<button type="button" class="btn btn-primary btn-xs mt-0" id="srAssign" data-toggle="srAssign" onclick="srDetails()"><i class="fa fa-plus" aria-hidden="true"></i>&nbsp;&nbsp;Assign</button>
	            	</#if>
	            </ul>
        		
        	</#if>
        	
        	<#if communicationEventTypeId?exists && communicationEventTypeId?has_content && communicationEventTypeId=="SMS_COMMUNICATION">
            <@dynaScreen 
	            instanceId="VIEW_SMS_ACTIVITY"
	            modeOfAction="VIEW"
	         />
        	<#else>
            <#if inputContext.workEffortTypeId?has_content>
            <#if inputContext.workEffortTypeId =="TASK">
            <@dynaScreen 
            instanceId="CREATE_TASK_ACTIVITY${dynaSuffix}"
            modeOfAction="VIEW"
            />
            <#elseif inputContext.workEffortTypeId =="EMAIL">
            <@dynaScreen 
            instanceId="CREATE_EMAIL_ACTIVITY${dynaSuffix}"
            modeOfAction="VIEW"
            />
            <#elseif inputContext.workEffortTypeId =="PHONE">
            <@dynaScreen 
            instanceId="CREATE_PHONE_ACTIVITY${dynaSuffix}"
            modeOfAction="VIEW"
            />
            <#elseif inputContext.workEffortTypeId =="APPOINTMENT">
            <@dynaScreen 
            instanceId="CREATE_APPOINTMENT_ACTIVITY${dynaSuffix}"
            modeOfAction="VIEW"
            />
            </#if>
            <#else>
            <@dynaScreen 
            instanceId="CREATE_TASK_ACTIVITY${dynaSuffix}"
            modeOfAction="VIEW"
            />
            </#if>
            </#if>
        	<#if communicationEventTypeId?exists && communicationEventTypeId?has_content && communicationEventTypeId=="SMS_COMMUNICATION">
                <@inputHidden 
                id="workEffortIds"
            	/>
                <#assign count = 1>
                <#list communicationEventList as communicationEvent>
                <div id="row_${count}" class="row padding-r_${count}">
                <@inputHidden name="communicationEventId_${count}" id="communicationEventId_${count}" value = "${communicationEvent.communicationEventId!}"/>
                <div class="col-md-4 col-lg-6 col-sm-12 form-group row" style="padding-left: 15px;">
                    <label class="col-lg-2 col-form-label field-text" id="AttributesValue">SMS Content</label>
                    <div id="smsContent" class="col-lg-8 value-text" style= "padding-left: 70px;">
                        ${communicationEvent.content?if_exists}
                    </div>
                </div>
                <div class="col-md-4 col-lg-3 col-sm-12 form-group row" style="padding-left: 15px;">
                     ${communicationEvent.smsSentDate?if_exists}
                </div>
	            <div class="col-md-4 col-lg-3 col-sm-12 form-group row" style="padding-left: 15px;">
	                 <@checkboxField
                        id="smsContentCheck_${count}"
                        name=""
                        class="form-check-input checkMe isEnabled"
                        value=communicationEvent.communicationWorkEffId!
                        checked = false
                    />
	            </div>
	            <#assign count = count+1>
	            </div>
	            </#list>
            </#if>
            <#if communicationEventTypeId?exists && communicationEventTypeId?has_content && communicationEventTypeId!="SMS_COMMUNICATION">
            <#if inputContext?has_content && inputContext.workEffortTypeId?has_content && inputContext.workEffortTypeId =="EMAIL">
            <div class="row padding-r">
                <div class="col-md-6 col-sm-6">
                    <@displayRowFileContent 
                    id="attachment"
                    label="Attachments"
                    activityId="${requestParameters.workEffortId!}"
                    />
                </div>
            </div>
            </#if>
            <#if inputContext?has_content && inputContext.workEffortTypeId?has_content && inputContext.workEffortTypeId =="EMAIL">
            <div class="col-md-12 col-lg-12 col-sm-12" id="emlContent">
                <@textareaLarge
                id="emailContent"
                groupId = "htmlDisplay"
                label=uiLabelMap.html
                rows="3"
                value = template
                required = false
                txareaClass = "ckeditor"
                />
            </div>
            <#else>
            <@inputArea
            inputColSize="col-sm-12"
            id="messages"
            label="Description"
            maxlength=100
            rows="10"
            disabled=true
            placeholder = "Description"
            value = inputContext.messages?if_exists
            />
            </#if>
            </#if>
  <div id="srAssignModal" class="modal fade" role="dialog">
   <div class="modal-dialog modal-md modal-lg">
       <div class="modal-content">
         <div class="modal-header">
         <h4 class="modal-title">SR Assignment</h4>
         <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
         </div>
         <div class="modal-body">
            <form id="sr-search-form" name="sr-search-form" method="post">
	            <@inputHidden name="fromPhoneNumber" id="fromPhoneNumber" value = "${fromPhoneNumber!}"/>
	            <@inputHidden name="communicationWorkEff" id="communicationWorkEff" value = "${communicationWorkEff!}"/>
	            <@inputHidden name="externalLoginKey " id="externalLoginKey" value = "${requestAttributes.externalLoginKey!}"/>
            </form>
		<@AgGrid
			gridheadertitle="SR Assignment"
			gridheaderid="sr-assign-grid-action-container"
			savePrefBtn=true
			clearFilterBtn=true
			exportBtn=true
			insertBtn=false
			updateBtn=false
			removeBtn=false
			headerextra=extraContent!
			refreshPrefBtnId="sr-assign-refresh-pref-btn"
			savePrefBtnId="sr-assign-save-pref-btn"
			clearFilterBtnId="sr-assign-clear-filter-btn"
			exportBtnId="sr-export-export-btn"
		    userid="${userLogin.userLoginId}" 
		    shownotifications="true" 
		    instanceid="SR_ASSIGN" 
		    autosizeallcol="true"
		    debug="false"
		    serversidepaginate=false
		    statusBar=false
		    />
		<script type="text/javascript" src="/common-portal-resource/js/ag-grid/service-request/find-sr-assign.js"></script>
         </div>
       </div>
   </div>
 </div>

<script>
    function srDetails() {
    	$('#srAssignModal').modal('show');
    }
	$('.isEnabled').change(function() {
       if($(this).is(":checked")){
       		console.log("checked");
       		addWorkEffortIds();
       }
    });
    function addWorkEffortIds() {
        var workEffortIds = $("input[name^='smsContentCheck_']:checked").map(function() {return this.value;}).get().join(',');
        $("#workEffortIds").val(workEffortIds);
    }
    function domainAssignment(custRequestId) {
	var domainEntityId = custRequestId;
	var workEffortIdList = $("input[name^='smsContentCheck_']:checked").map(function() {return this.value;}).get().join(',');
    $("#workEffortIds").val(workEffortIdList);
	var workEffortId = workEffortIdList;
	var domainEntityType = "SERVICE_REQUEST";
	var externalLoginKey = $('#externalLoginKey').val();
	if(workEffortId != "") {
		$.ajax({
			  async: true,
			  url:'/common-portal/control/srDomainAssign',
			  type:"POST",
			  data: {
				  "domainEntityId": domainEntityId,"workEffortId": workEffortId,"domainEntityType": domainEntityType,"externalLoginKey": externalLoginKey
				},
			success: function (data) {
				var message = data.responseMessage;
				console.log("message===="+message);
				if (message == "success") {
						showAlert("success", "Domain Entity Type has been assigned successfully");
		        } else {
		            showAlert("error", "Error while Domain Assign Entity Type");
		        }
		        $('#srAssignModal').modal('hide');
		        $('.isEnabled').prop('checked', false);
				}
			});
		setTimeout(function() {
	    	location.reload();
		}, 1500);
	} else {
		$('#srAssignModal').modal('hide');
		showAlert("error", "Please select any SMS content to assign SR.");
	}
}
</script>