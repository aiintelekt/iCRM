<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/custom-field/modal_window.ftl"/>

<#if isApprovalEnabled?has_content && isApprovalEnabled =="Y">
<#include "component://approval-portal/webapp/approval-portal/approval/modal_window.ftl"/>
</#if>

<#assign readOnlyPermission = Static["org.fio.homeapps.util.DataUtil"].hasPermissionWoFullPerm(request, "READ_ONLY_PERM")?if_exists />


<#-- <#assign requestURI = "viewContact"/>
<#if request.getRequestURI().contains("viewLead")>
	<#assign requestURI = "viewLead"/>
<#elseif request.getRequestURI().contains("viewAccount")>
	<#assign requestURI = "viewAccount"/>
<#elseif request.getRequestURI().contains("viewOpportunity")>
	<#assign requestURI = "viewOpportunity"/>
<#elseif request.getRequestURI().contains("viewCustomer")>
	<#assign requestURI = "viewCustomer"/>	
<#elseif request.getRequestURI().contains("viewServiceRequest")>
	<#assign requestURI = "viewServiceRequest"/>	
<#elseif request.getRequestURI().contains("viewRebate")>
	<#assign requestURI = "viewRebate"/>	
<#elseif request.getRequestURI().contains("viewActivity")>
	<#assign requestURI = "viewActivity"/>		
</#if>-->
<#assign partyId= request.getParameter("partyId")! />
<#if partyId == "" && inputContext?has_content>
<#assign partyId = inputContext.partyId!>
</#if>
<#assign requestURI="${requestURI!}">
<#if !requestURI?has_content>
<#assign requestURI = request.getRequestURI()/>
<#if requestURI.contains("screenRender")>
<#assign requestURI=request.getParameter("requestUri")!>
</#if>
</#if>
<#if requestURI?has_content>
<#assign found = false>
<#list ["viewActivity", "viewRebate", "viewServiceRequest", "viewCustomer", "viewOpportunity", "viewAccount", "viewLead"] as requestUriName>
<#if requestURI?contains(requestUriName)>
<#assign requestURI = requestUriName!>
<#assign found = true>
</#if>
</#list>
<#if !found>
<#assign requestURI = "viewContact">
</#if>
<#else>
<#assign requestURI = "viewContact">
</#if>
<#if (isWorkflowActivity?has_content && isWorkflowActivity =="Y") || requestURI == "viewActivity">
	<#assign domainEntityType = "ACTIVITY"/>	
	<#assign domainEntityId = workflowActivityId!/>	
	<#if !domainEntityId?has_content>
		<#assign domainEntityId = workEffortId!/>	
	</#if>
</#if>
<#if requestURI == "viewLead">
<style>
.marginTopStyle{
	margin-top: -17px;
}
</style>
</#if>
<form></form>
   
<form name="CustomFieldform" method="post" action="<@ofbizUrl>createUpdateCustom#${tabIdForCurrentTab!}</@ofbizUrl>">   

<input type="hidden" name="domainEntityType" value="${domainEntityType!}">
<input type="hidden" name="domainEntityId" value="${domainEntityId!}">

<input type="hidden" name="activeTab" value="customFields" />  
<input type="hidden" name="donePage" value='${requestURI?if_exists}'/>
<input type="hidden" name="partyId" value="${partyId?if_exists}"/>     

<input type="hidden" name="workEffortId" value="${request.getParameter("workEffortId")!}"/> 
<input type="hidden" name="agreementId" value="${request.getParameter("agreementId")!}"/> 
<input type="hidden" name="srNumber" value="${request.getParameter("srNumber")!}"/> 

<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

<#assign extra = ""/>
<#if isWorkflowActivity?has_content && isWorkflowActivity =="Y">
	<#assign workFlowStatus = Static["org.groupfio.approval.portal.util.DataUtil"].getWorkflowStatus(delegator, Static["org.ofbiz.base.util.UtilMisc"].toMap("parentWorkEffortId", workflowActivityId!, "approvalCategoryId", approvalCategoryId!, "domainEntityType", inputContext.domainEntityType!, "domainEntityId", inputContext.domainEntityId!))?if_exists />
	<#assign partyApprovalItem = Static["org.groupfio.approval.portal.util.DataUtil"].getPartyApprovalItem(delegator, Static["org.ofbiz.base.util.UtilMisc"].toMap("parentWorkEffortId", workflowActivityId!, "partyId", loggedUserPartyId!, "approvalCategoryId", approvalCategoryId!, "domainEntityType", inputContext.domainEntityType!, "domainEntityId", inputContext.domainEntityId!))?if_exists />
	<#assign isActivateWorkFlow = Static["org.groupfio.approval.portal.util.DataUtil"].isActivateWorkFlow(delegator, Static["org.ofbiz.base.util.UtilMisc"].toMap("parentWorkEffortId", workflowActivityId!,"partyId", loggedUserPartyId!, "approvalCategoryId", approvalCategoryId!, "domainEntityType", inputContext.domainEntityType!, "domainEntityId", inputContext.domainEntityId!))?if_exists />
	
	<div class="row">
	<div class="col-lg-8 col-md-8 col-sm-8">
	</div>
	<div class="col-lg-4 col-md-4 col-sm-4">
	<#if isActivateWorkFlow>
	<@dropdownCell 
    id="decisionStatusIdRbtDetail"
    name="decisionStatusIdRbtDetail"
    label="Approval Status"
    options=decisionStatusList?if_exists
    value=partyApprovalItem.decisionStatusId!
    required=false
    placeholder = "Approval Status"
    />
    <#elseif workFlowStatus.status?has_content>
    	<div class="form-group row" style="text-align: right">
	    	<label class="col-sm-8">Approval Status: </label>
            <div class="col-sm-4">
              	<strong>${workFlowStatus.status!}</strong>
            </div>
	   	</div>
    </#if>
    </div>
    </div>
    
    <#assign analysisUrl = '#'>
	<#if agreementYear?has_content && rebatePartyIdTo?has_content>
	<#assign analysisUrl = '${agreementReportUrl!}&invoice_year=${agreementYear!}&agreement_id=${inputContext.domainEntityId!}'>
	</#if>
	
	<#if workflowCategoryId=='APVL_CAT_PAYOUT'>
	<#assign extra = extra + '<a href="${analysisUrl!}" class="btn btn-xs btn-primary m5 marginTopStyle" target="_blank"><i class="fa fa-eye" aria-hidden="true"></i> Rebate Analysis</a>'/>
	<#assign extra = extra + '<a role="button" id="payout-metadata-btn" title="Payout Metadata" style="margin-top: 0px;" class="btn btn-xs btn-primary m5 marginTopStyle"><i class="fa fa-eye" aria-hidden="true"></i> Payout Metadata</a>'/>
	</#if>
</#if>

<#if readOnlyPermission!>
	<#assign extra = "" />
<#else>
	<#assign hasPermission = Static["org.fio.homeapps.util.DataUtil"].hasPermission(request, "UPDATE_OPERATION")?if_exists />
	<#if hasPermission>
		<#assign isDisable = true />
		<#assign disableCloseOpTypes = Static["org.fio.homeapps.util.DataUtil"].getGlobalValue(delegator, "DISABLE_CLOSE_OP_TYPES", "") />
		<#if disableCloseOpTypes?has_content && srTypeId?has_content && disableCloseOpTypes.contains(srTypeId) >
			<#assign isDisable = false />
		</#if>
		<#assign extra = extra + '<input type="submit" value="Save" class="btn btn-xs btn-primary m5 marginTopStyle"></input>' />
		<#if srStatusId?has_content && (srStatusId == "SR_CLOSED" || srStatusId == "SR_CANCELLED") && isDisable>
			<#assign extra = ""/>
		</#if>
	</#if> 
</#if>

<#assign extra = extra + '&nbsp;<span id="attr-collapseAll-btn" title="" class="btn btn-xs btn-primary m5 marginTopStyle"><i class="fa fa-arrow-circle-down" aria-hidden="true"></i> <span>Expand All</span> </span>'/>

<#if isEnableProgramAct?has_content && isEnableProgramAct=="Y" && requestURI == "viewActivity">
<#if request.getParameter("groupingCodeId")?has_content>
	<#assign actAttrGcode = request.getParameter("groupingCodeId")>
</#if>
<div class="row">
<div class="col-lg-8 col-md-8 col-sm-8">
</div>
<div class="col-lg-3 col-md-3 col-sm-3">
<@dropdownCell 
id="customFieldGroupingCodeId"
name="customFieldGroupingCodeId"
label="Grouping Code"
value=actAttrGcode!
options=groupingCodeLst?if_exists
required=false
placeholder = "Grouping Code"
/>
</div>
<div class="col-lg-1 col-md-1 col-sm-1">
	<span id="assign-gcode-btn" class="btn btn-xs btn-primary" data-toggle="confirmation" title="Assign this grouping code ?"><i class="fa fa-edit" aria-hidden="true"></i> Assign </span>
</div>
</div> 
</#if>  

<div class="border-b pt-2" style="margin-top: 6px;">
	<#assign headerTitle = uiLabelMap.Attributes!>
	<#if isWorkflowActivity?has_content && isWorkflowActivity =="Y" && approvalDescription?has_content>
		<#assign headerTitle = headerTitle + ' ('+approvalDescription+')'>
	</#if>
	<#assign isShowHelpUrl="Y">
	<#if Static["org.fio.homeapps.util.DataUtil"].isPhoneCampaignEnabled(delegator)?if_exists=="Y" >
	<#assign isShowHelpUrl="N">
	</#if>
	<@sectionFrameHeaderTab title="${headerTitle!}" tabId="Attributes" extra=extra! isShowHelpUrl=isShowHelpUrl!/>
</div>
<div id="custom-field-accordion">
	
	<#if groupList?has_content && groupList?size!=0>
	
	<#assign count = 0>
    <#assign i = 0>
	
	<#list groupList as group>
	<#assign groupName = group.groupName!>
	<#assign groupId = group.groupId!>
		
	<#assign isGroupEnabled = "Y">
	<#if isProgAct?has_content && isProgAct=="Y">
		<#if !groupConfig.get(groupId)?has_content>
			<#assign isGroupEnabled = "N">
		</#if>
	</#if>
	
	<#if isGroupEnabled=="Y">
	<div class="card attr-detail">
		
		<div class="card-header pt-1 pb-1">
		<a role="button" class="card-link <#if count != 0>collapsed</#if>" data-toggle="collapse" href="#acc1_o_${count}" aria-expanded="true">
			 ${groupName?if_exists}
		</a>	 
		</div>
				
		<div id="acc1_o_${count}" class="card-collapse collapse attr-collapse <#if count == 0> show </#if>"
			data-parent="#custom-field-accordion" style="">
			<div class="card-body">
				
				<#-- <h4 class="bg-light pl-1 mt-2">Customer Level Information</h4> -->
				
				<#assign customFieldLi = delegator.findByAnd("CustomField", {"groupId":"${groupId?if_exists}"}  ,Static["org.ofbiz.base.util.UtilMisc"].toList("sequenceNumber ASC"), false)?if_exists />
                
                <#if customFieldLi?has_content && customFieldLi?size!=0>
				
				<#list customFieldLi as customField>
				
				<#assign isFieldEnabled = "Y">
				<#if isProgAct?has_content && isProgAct=="Y">
					<#if !fieldConfig.get(customField.customFieldId)?has_content>
						<#assign isFieldEnabled = "N">
					</#if>
				</#if>
				
               	<#if customField.hide?if_exists != "Y" && isFieldEnabled=="Y">
				<#-- 
				<input type="hidden" id="domainEntityType_o_${i}" name="domainEntityType_o_${i}" value="${domainEntityType!}">
				<input type="hidden" id="domainEntityId_o_${i}" name="domainEntityId_o_${i}" value="${domainEntityId!}">
				 -->
				<input type="hidden" name="partyId_o_${i}" id="partyId_o_${i}" value="${partyId?if_exists}">
              	<input type="hidden" name="customFieldId_o_${i}" id="customFieldId_o_${i}" value="${customField.customFieldId?if_exists}">
              	<input type="hidden" name="groupId_o_${i}" id="groupId_o_${i}" value="${customField.groupId?if_exists}">
                
                <#assign fieldValue = Static["org.groupfio.custom.field.util.DataUtil"].getAttrFieldValue(delegator, Static["org.ofbiz.base.util.UtilMisc"].toMap("customFieldId", customField.customFieldId, "partyId", partyId!, "domainEntityType", domainEntityType!, "domainEntityId", domainEntityId!, "attrEntityAssoc", attrEntityAssoc!))!>
                <#assign fieldActualValue = "">
                
                <#if !fieldValue?has_content>
                	<input name="action_o_${i}" type="hidden" value="CREATE"/>
               	<#else>
               		<input name="action_o_${i}" type="hidden" value="UPDATE"/>
               		<#if attrEntityAssoc?has_content>
	                	<#assign fieldActualValue = fieldValue?if_exists.get(attrEntityAssoc.fieldValueColumn)?if_exists>
	               	<#else>
	               		<#assign fieldActualValue = fieldValue?if_exists.fieldValue?if_exists>
	               	</#if>
               	</#if>
								
				<div class="row">

					<div class="col-lg-6 col-md-12 col-sm-12">
					
						<#if customField.customFieldFormat?has_content && customField.customFieldFormat?if_exists="TEXT">
                         	<@inputRow    
                                id = "customFieldValue_o_${i}"
                         		label = customField.customFieldName?if_exists
                                value= fieldActualValue?if_exists
                                required=false
                                maxlength=customField.customFieldLength!
                                inputColSize="col-sm-8"
						        labelColSize="col-sm-4"
                               />
                         <#elseif customField.customFieldFormat?has_content && customField.customFieldFormat?if_exists="DATE">
                             <@inputDate 
								id="customFieldValue_o_${i}"
                                label=customField.customFieldName?if_exists
                                placeholder="${globalDateFormat!}"
								value= fieldActualValue?if_exists
								dateFormat="${globalDateFormat!}"?upper_case
								required=false
								inputColSize="col-sm-8"
						        labelColSize="col-sm-4"
								/>
                         <#elseif customField.customFieldFormat?has_content && customField.customFieldFormat?if_exists="TEXT_AREA">
                             <@inputArea
					        	id="customFieldValue_o_${i}"
                             	label=customField.customFieldName?if_exists
                             	value= fieldActualValue?if_exists
						        inputColSize="col-sm-8"
						        labelColSize="col-sm-4"
						        rows="3"
						        required=false
						        maxlength=customField.customFieldLength!
					        />  
                         <#elseif customField.customFieldFormat?has_content && customField.customFieldFormat?if_exists="CHECK_BOX">
                             <#assign checkBoxValue = delegator.findByAnd("CustomFieldMultiValue",{"customFieldId":customField.customFieldId?if_exists},[],true)?if_exists />
                             <#if checkBoxValue?has_content>
                             	<div class="form-group row customFieldValue_o_${i}" id="customFieldValue_o_${i}_row" style="">
									<label class="col-sm-2 field-text">${customField.customFieldName}</label>
									<div class=" col-sm-10 left">
										<#assign checkedValue = fieldActualValue?if_exists />
	                                 	<#list checkBoxValue as checkBoxValue> 
	                                 	<#if checkBoxValue.hide?if_exists != "Y"> 
	                                 	<div class="form-check-inline">
	                                    	<label class="form-check-label"> 
	                                    	<input type="checkbox" class="form-check-input" name="customFieldValue_o_${i}" id="customFieldValue_o_${i}" 
	                                    	value="${checkBoxValue.fieldValue}" <#if checkedValue?contains(checkBoxValue.fieldValue)>checked</#if> >
	                                    	${checkBoxValue.description}
	                                    </label>
	                                 	</div>
	                                 	</#if>
	                                 	</#list>
									</div>
								</div>
                             </#if>
                         <#elseif customField.customFieldFormat?has_content && customField.customFieldFormat?if_exists="RADIO">
                             <#assign radioButtonValue = delegator.findByAnd("CustomFieldMultiValue",{"customFieldId":customField.customFieldId?if_exists},[],true)?if_exists />
                             <#if radioButtonValue?has_content>
                             	<div class="form-group row customFieldValue_o_${i}" id="customFieldValue_o_${i}_row" style="">
									<label class="col-sm-2 field-text">${customField.customFieldName}</label>
									<div class=" col-sm-10 left">
										<#assign radioValue = fieldActualValue?if_exists />
		                                <#list radioButtonValue as radioButtonVal>
		                                   	<#if radioButtonVal.hide?if_exists != "Y"> 
		                                    <div class="form-check-inline">
		                                       <label class="form-check-label"> 
		                                       <input type="radio" class="form-check-input"name="customFieldValue_o_${i}" id="customFieldValue_o_${i}" 
		                                       value="${checkBoxValue?if_exists.fieldValue?if_exists}" <#if radioValue == "${radioButtonVal?if_exists.fieldValue?if_exists}">checked</#if> >
		                                       ${radioButtonVal.description}
		                                       </label>
		                                    </div>
		                              	</#if>
		                                </#list>
									</div>
								</div>
                             </#if>
                         <#elseif customField.customFieldFormat?has_content && customField.customFieldFormat?if_exists="DROP_DOWN">
                             <#assign dropDownValue = delegator.findByAnd("CustomFieldMultiValue",{"customFieldId":customField.customFieldId?if_exists},[],true)?if_exists />
                             <div class="form-group row">
                                <label  class="col-sm-2 col-form-label">${customField.customFieldName?if_exists}</label>
                                <div class="col-sm-10">
                                   <select name="customFieldValue_o_${i}" id="customFieldValue_o_${i}" class="ui dropdown search form-control input-sm" >
                                      <option value="">
                                         <div class="text-muted">Please Select</div>
                                      </option>
                                      <#if dropDownValue?has_content>
                                          <#list dropDownValue as classification>
                                              <#if classification.hide?if_exists != "Y">
                                                 <option value="${classification.fieldValue}" <#if fieldActualValue?if_exists = classification.fieldValue>selected<#elseif classification?if_exists = classification.fieldValue>selected</#if>>${classification.description}</option>
                                              </#if>   
                                          </#list>
                                      </#if>
                                   </select>
                                </div>
                             </div>
                         <#elseif customField.customFieldFormat?has_content && customField.customFieldFormat?if_exists="NUMERIC">
                             <@inputRow    
                            	id="customFieldValue_o_${i}"
                                label=customField.customFieldName?if_exists
                                value=fieldActualValue
                                type="number"
                                required=false
                                maxlength=customField.customFieldLength!
                                pattern="^[1-9]\\d*(\\.\\d+)?$"
                                step="0.01"
                                inputColSize="col-sm-8"
						        labelColSize="col-sm-4"
                               />
                         <#elseif customField.customFieldFormat?has_content && customField.customFieldFormat?if_exists="INTEGER">
                             <@inputRow    
                            	id="customFieldValue_o_${i}"
                                label=customField.customFieldName?if_exists
                                value=fieldActualValue
                                type="number"
                                required=false
                                maxlength=customField.customFieldLength!
                                pattern="(\\d*)"
                                inputColSize="col-sm-8"
						        labelColSize="col-sm-4"
                               />
                         <#elseif customField.customFieldFormat?has_content && customField.customFieldFormat?if_exists="LABEL_TEXT">
                             <div class="form-group row">
                                <label class="col-sm-2 col-form-label">${customField.customFieldName?if_exists}</label>
                                <div class="col-sm-10">
                                   <label class="col-form-label input-sm">${fieldActualValue?if_exists}</label>
                                </div>
                             </div>
                         </#if>
							                         
                         <#if customField.paramData?has_content>
                         	<#assign paramDataList = Static["org.fio.homeapps.util.ParamUtil"].jsonToList(customField.paramData!)!>
                         	<#assign paramDataVal = "">
                         	<#list paramDataList as paramData> 
                         	<#assign paramValueTypeDesc = Static["org.fio.homeapps.util.EnumUtil"].getEnumName(delegator, paramData.paramValueType!, "CF_PARM_VAL_TYPE", true)!>
                         	<#assign paramDataVal = paramDataVal + "${paramData.paramName!} (${paramData.paramValue!}${paramValueTypeDesc!}),">
                         	</#list>
                         	<#-- <#assign paramDataValue = Static["org.fio.homeapps.util.ParamUtil"].jsonToMap(fieldActualValue!)!> -->
							<div class="form-group row customFieldParamValue_o_${i}" id="customFieldParamValue_o_${i}_row" style="">
								<label class="col-sm-2 field-text"></label>
								<div class=" col-sm-10 left pb-4">
									<#if !customField.paramDisplayType?has_content || customField.paramDisplayType=='INLINE'>
									<label class="col-form-label input-sm">${paramDataVal!}</label>
	                                <#-- 
	                                <div class="form-check-inline">
	                                	<label class="form-check-label"> 
	                                    <input type="checkbox" class="form-check-input" name="customFieldParamValue_o_${i}" id="customFieldParamValue_o_${i}" 
	                                    value='"${paramData.paramName!}":"${paramData.paramValue!}"' <#if paramDataValue?has_content && paramDataValue.get(paramData.paramName)?has_content>checked</#if> >
	                                    ${paramData.paramName!} (${paramData.paramValue!})
	                                    </label>
	                                </div>
	                                 -->
	                                
	                                <#elseif customField.paramDisplayType?has_content && customField.paramDisplayType=='LINK'>
	                                <button type="button" class="btn btn-xs btn-primary m5 cf-param-list" data-customFieldId="${customField.customFieldId!}" data-customFieldName="${customField.customFieldName!}" data-paramData="${paramDataVal!}"><i class="fa fa-eye" aria-hidden="true"></i> View Params</button>
	                                </#if>
								</div>
							</div>
                         </#if>
                         
					</div>
					
				</div>
				
				</#if>
				<#assign i=i+1>
				</#list>
				
				</#if>
								
			</div>
		</div>
	</div>
	<#assign count = count+i> 
	</#if>
    </#list>
         
	</#if>

</div>

</form>

<#if isWorkflowActivity?has_content && isWorkflowActivity =="Y">
<form id="approval-decision-form" name="approval-initiate-form" method="post">	
	<input type="hidden" name="approvalCategoryId" value="${approvalCategoryId!}">
	<input type="hidden" name="partyId" value="${loggedUserPartyId!}">
	<input type="hidden" name="decisionStatusId">
	<input type="hidden" name="approvalComments">
	<input type="hidden" name="prvDecisionStatusId" value="${partyApproval.decisionStatusId!}">
	<input type="hidden" name="parentWorkEffortId" value="${workflowActivityId!}">
	
	<input type="hidden" name="domainEntityType" value="${inputContext.domainEntityType!}">
    <input type="hidden" name="domainEntityId" value="${inputContext.domainEntityId!}">
    <input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
</form>

<@approvalCommentModal
instanceId="rbtdetail-approval-comment-modal" decisionStatusFieldId="decisionStatusIdRbtDetail"
/>

<@payoutMetadataModal
instanceId="payout-metadata-detail-modal"
/>

<script>
$(document).ready(function() {

$("#decisionStatusIdRbtDetail").change(function () {
	console.log('change decisionStatusId: '+$(this).val());
	let decisionStatusId = $(this).val();
	let prvDecisionStatusId = "${partyApprovalItem.decisionStatusId!}";
	if (decisionStatusId) {
		$("#approval-decision-form input[name=decisionStatusId]").val(decisionStatusId);
		//$('#rbtdetail-approval-comment-modal').modal('show');
		if (prvDecisionStatusId && prvDecisionStatusId=='DECISION_REVIEW') {
			$('#rbtdetail-approval-comment-modal').modal('show');
			return true;
		}
		
		if (decisionStatusId == 'DECISION_APPROVED') {
			changeApprovalStatus();
		} else {
			$('#rbtdetail-approval-comment-modal').modal('show');
		}
	}
});

});

function changeApprovalStatus() {
	$.ajax({
		async : false,
		url : '/approval-portal/control/changeApprovalStatus',
		type : "POST",
		data : JSON.parse(JSON.stringify($("#approval-decision-form").serialize())),
		success : function(data) {
			if (data.code == 200) {
				showAlert ("success", "Successfully changed approval status..");
				location.reload();
			} else {
				location.reload();
				showAlert ("error", data.message);
			}
		}
	});
}
 
</script>
</#if>

<@displayCustomFieldsParams
instanceId="cf-param-modal"
/>

<form id="find-attr-form" method="get">
    <input type="hidden" name="groupingCodeId" value="">
    <input type="hidden" name="workEffortId" value="${domainEntityId!}">
    <input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
</form>

<script>
$(document).ready(function() {

$("#customFieldGroupingCodeId").change(function () {
	console.log('change groupingCodeId: '+$(this).val());
	let groupingCodeId = $(this).val();
	$("#find-attr-form input[name=groupingCodeId]").val(groupingCodeId);
	$("#find-attr-form").submit();
});

$("#assign-gcode-btn").click(function () {
    console.log('click assign gcode btn');
    $.ajax({
		type: "POST",
     	url: "/common-portal/control/assignAttrGroupCode",
        data : {
			"domainEntityType" : "${domainEntityType!}", "domainEntityId" : "${domainEntityId!}", "groupingCodeId" : $("#customFieldGroupingCodeId").val() 
			,"externalLoginKey": "${requestAttributes.externalLoginKey!}"
		},
        async: false,
        success: function (data) {   
            if (data.code == 200) {
            	showAlert ("success", data.message);
            } else {
            	showAlert ("error", data.message);
            }
        }
	});
}); 

});

(function () {
  	var isCollapsedAll = false;  
  	$("#attr-collapseAll-btn").click(function(event) {
  		if (!isCollapsedAll) {
  			$('.attr-collapse').removeClass('hide').addClass('show');
  			isCollapsedAll = true;
  			$('#attr-collapseAll-btn i').removeClass('fa-arrow-circle-down').addClass('fa-arrow-circle-up');
  			$('#attr-collapseAll-btn span').text('Collapse All');
  		} else {
  			$('.attr-collapse').removeClass('show').addClass('hide');
  			isCollapsedAll = false;
  			$('#attr-collapseAll-btn i').removeClass('fa-arrow-circle-down').addClass('fa-arrow-circle-down');
  			$('#attr-collapseAll-btn span').text('Expand All');
  		}
	});
}());

</script>
                     