<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/lib/picker_macro.ftl"/>

<div class="row">
    <div id="main" role="main" class="pd-btm-title-bar">
        <div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
			<@sectionFrameHeader title="Dashboard"  isShowHelpUrl="Y" leftCol="col-lg-7 col-md-12 col-sm-12" rightCol="col-lg-5 col-md-12 col-sm-12" />
			
			<@AppBar 
				appBarId="EMAIL_DASHBOARD"
				appBarTypeId="DASHBOARD"
				id="appbar1"
				isEnableUserPreference=true
				animateEffect="bounce"
				/>
		</div>
    </div>
</div>
<#assign helpUrl = Static["org.groupfio.common.portal.util.DataHelper"].getHelpUrl(delegator, request.getRequestURI(), "emailLists") />
<input type="hidden" name="commEventId" id="commEventId">
<span id="mark-read"></span>
<div class="row" style="width:100%">
	<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
		<span id="dashboard-filter"></span>
		<form id="unassignedForm" name="unassignedForm" method="post" class="form-horizontal" novalidate="true" data-toggle="validator">
			<#-- <@inputHidden id="searchType" value="QUEUE"/>
			<@inputHidden id="displayType" value="UN_ASSIGNED"/> -->
			<@inputHidden id="externalLoginKey" value="${requestAttributes.externalLoginKey!}" />
			<@inputHidden id="filterBy" value="${requestParameters.filterBy!}"/>
			<@inputHidden id="filterType" value="${requestParameters.filterType!}"/>
		</form>
		<div class="row">
			<div class="col-lg-12 col-md-12 col-sm-12">
				<#-- <@AgGrid
					gridheadertitle="List"
					gridheaderid="unassigned-grid-action-container"
					savePrefBtn=true
					clearFilterBtn=true
					exportBtn=true
					insertBtn=false
					updateBtn=false
					removeBtn=false
					helpBtn=true
					helpUrl=helpUrl!
					headerextra=rightContent!
					refreshPrefBtnId="unassigned-refresh-pref-btn"
					savePrefBtnId="unassigned-save-pref-btn"
					clearFilterBtnId="unassigned-clear-filter-btn"
					exportBtnId="unassigned-export-btn"
					removeBtnId="remove-unassigned-btn"
				    userid="${userLogin.userLoginId}" 
				    shownotifications="true" 
				    instanceid="UNASSIGNED_COMM_HISTORY" 
				    autosizeallcol="true"
				    debug="false"
				    serversidepaginate=true
				    statusBar=true
				    />  
			  	<script type="text/javascript" src="/common-portal-resource/js/ag-grid/common/find-unassigned-comm-history.js"></script>-->
			  	<input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
				<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
				<div id="unassigned-comm-history-grid" style="display:none;">
						<@fioGrid 
							id="commHistoryGrid"
							instanceId="UNASSIGNED_COMM_HISTORY"
							jsLoc="/common-portal-resource/js/ag-grid/common/find-unassigned-comm-history.js"
							headerLabel="List"
							headerId="unassigned-grid-action-container"
							savePrefBtnId="unassigned-save-pref-btn"
							clearFilterBtnId="unassigned-clear-filter-btn"
							headerBarClass="grid-header-no-bar"
							subFltrClearId="sub1-filter-clear-btn"
							savePrefBtn=false
							clearFilterBtn=false
							subFltrClearBtn=false
							exportBtnId="commHistory-list-export-btn"
							exportBtn=true
							helpBtn=true
							helpUrl=helpUrl!
							headerExtra=rightContent!
							serversidepaginate=true
							statusBar=true
							/>
				</div>
				<div id="unassigned-sms-grid">
						<@fioGrid 
							id="unassignedsmscommHistoryGrid"
							instanceId="UNASSIGNED_SMS_LIST"
							jsLoc="/common-portal-resource/js/ag-grid/common/find-unassigned-sms-comm-history.js"
							headerLabel="List"
							headerId="unassigned-sms-grid-action-container"
							savePrefBtnId="unassigned-sms-save-pref-btn"
							clearFilterBtnId="unassigned-sms-clear-filter-btn"
							headerBarClass="grid-header-no-bar"
							subFltrClearId="sms-sub-filter-clear-btn"
							savePrefBtn=false
							clearFilterBtn=false
							subFltrClearBtn=false
							exportBtnId="sms-commHistory-list-export-btn"
							exportBtn=true
							helpBtn=true
							helpUrl=helpUrl!
							headerExtra=rightContent!
							serversidepaginate=true
							statusBar=true
							/>
				</div>
			</div>
		</div>
	</div>
</div>
<#-- 
<div class="row" style="width:100%">
	
	<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">		
		<form id="assignedForm" name="assignedForm" method="post" class="form-horizontal" novalidate="true" data-toggle="validator">
			<@inputHidden id="searchType" value="QUEUE"/>
			<@inputHidden id="partyId" value="${userLoginPartyId!}"/>
			<@inputHidden id="applyToAll" value="N" />
			<@inputHidden id="externalLoginKey" value="${requestAttributes.externalLoginKey!}" />
		</form>
		<div class="row">
			<div class="col-lg-12 col-md-12 col-sm-12">
				<@AgGrid
					gridheadertitle="Assigned List"
					gridheaderid="assigned-grid-action-container"
					savePrefBtn=true
					clearFilterBtn=true
					exportBtn=true
					insertBtn=false
					updateBtn=false
					removeBtn=false
					helpBtn=true
					helpUrl=helpUrl!
					headerextra=rightContent!
					refreshPrefBtnId="assigned-refresh-pref-btn"
					savePrefBtnId="assigned-save-pref-btn"
					clearFilterBtnId="assigned-clear-filter-btn"
					exportBtnId="assigned-export-btn"
					removeBtnId="remove-assigned-btn"
				    userid="${userLogin.userLoginId}" 
				    shownotifications="true" 
				    instanceid="ASSIGNED_COMM_HISTORY" 
				    autosizeallcol="true"
				    debug="false"
				    />  
			  	<script type="text/javascript" src="/common-portal-resource/js/ag-grid/common/find-assigned-comm-history.js"></script>
			</div>
		</div>
	</div>
</div>
 -->

<@workEffortAssignmentPicker 
    instanceId="partyAssignment"
    />	
<#-- 
<@domainEntityPicker 
    instanceId="domainEntityPicker"
    style="z-index: 1100000;"
    />	
-->   
<div class="modal fade" id="domainSelection" tabindex="-1" role="dialog" aria-labelledby="domainSelection" aria-hidden="true">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="">Domain</h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <form id="domainAssignForm" name="domainAssignForm" method="post" action="<@ofbizUrl>domainAssignment</@ofbizUrl>">
            <div class="modal-body">
                <#assign domainEnum = Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("enumId","description").from("Enumeration").where("enumTypeId","ENTITY_DOMAIN","isEnabled","Y").orderBy("sequenceId").queryList()?if_exists />    
        		<#assign domainList = Static["org.fio.admin.portal.util.DataUtil"].getMapFromGeneric(domainEnum,"enumId","description",false)?if_exists />
                <#-- 
                <div class="form-group  domain">
        			<div class="col-lg-12 col-md-12 col-sm-12 ">
	                <#if domainList?has_content>
				      	<#assign index=0>
				         <#list domainList.entrySet() as entry>  
				         <div class="form-check-inline" style="margin-top: 6px;">
				            <label for="${entry.key}">
				            <input type="radio" id="${entry.key}" name="domain" value="${entry.key!}" class="form-check-input" >
				            <span></span>
				            <span class="check"></span>
				            <span class="box"></span>
				            ${entry.value!}</label>
				         </div>
				         <#assign index=index+1>
				         </#list>
				    </#if>
				    </div>
				</div> -->
                <@inputHidden 
                	id="workEffortId"
                	/>
                <@radioInputCell
			        id="domainEntityType"
			        name="domainEntityType"
			        options=domainList!
			        inputColSize="col-lg-12 col-md-12 col-sm-12 "
			        value="${requestParameters.domainEntityType!'SERVICE_REQUEST'}"
			        />
			        <#-- 
			     <@inputRowPicker 
			        inputColSize="col-lg-12 col-md-12 col-sm-12"
			        glyphiconClass= "fa fa-id-card"
			        pickerWindow="domainEntityPicker"
			        label=""
			        isMakerChange=isMakerChange!
			        required=isRequired!
			        id="domainEntityId"  
			        name="domainEntityId" 
			        placeholder=""
			        labelColSize="${labelColSize!}" 
					inputColSize="${inputColSize!}"
					modalData="data-domain-type-id=''"
					isAutoCompleteEnable="Y"
					isTriggerChangeEvent="Y"
					autoCompleteMinLength="5"
					autoCompleteUrl="/common-portal/control/searchEntityDomainList"
					autoCompleteLabelFieldId="description,domainEntityId"
					autoCompleteValFieldId="domainEntityId"
					autoCompleteFormId="domainAssignForm"
			        />
			     -->
			     <@inputAutoComplete
			     	id="domainEntityId"
			     	isAutoCompleteEnable="Y"
			     	onkeydown=true
			     	inputColSize="col-lg-12 col-md-12 col-sm-12 "
			     	autoCompleteMinLength=3
			     	placeholder="Auto Complete"
			     	autoCompleteLabelFieldId="description,domainEntityId"
			     	autoCompleteValFieldId="domainEntityId"
			     	autoCompleteFormId="domainAssignForm"
			     	autoCompleteUrl="/common-portal/control/searchEntityDomainList"
			     	/>
            </div>
            <div class="modal-footer">
                <a href="#" target="_blank" id="create-btn" name="create-btn" class="btn btn-primary" style="display:none;">Create</a>
                <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancel</button>
                <a href="#" id="domain-assign" name="domain-assign" class="btn btn-primary">Assign</a>
            </div>
            </form>
        </div>
    </div>
</div>

<script>
$(function() {
	let domainEntType = $('input[type=radio][name="domainEntityType"]').val();
	if(domainEntType)
		$("#create-btn").show();
	else
		$("#create-btn").hide();
	getEmailDashboardDataCountList();
	
	//$("#unassigned-emails").addClass( "selected-element-b");
	//load_dynamic_data('unassigned-emails');
	
	$('#domainAssignForm #domainEntityId_val').val("");
	$('#domainAssignForm #domainEntityId_desc').val("");
	$('#domainSelection').on('shown.bs.modal', function (e) {
		$('#domainAssignForm #domainEntityId_val').val("");
		$('#domainAssignForm #domainEntityId_desc').val("");
		var primaryId = $(e.relatedTarget).data('primary-id');
		$('#domainAssignForm #workEffortId').val(primaryId);
	});

	$('input[type=radio][name="domainEntityType"]').change(function() {
		
        if($(this).val() === "OPPORTUNITY" || $(this).val() === "SERVICE_REQUEST")
        	$("#create-btn").show();
        else
        	$("#create-btn").hide();
        
        //alert($(this).val()); // or, use `this.value`
		//$("#domainEntityId").data('domain-type-id').val($(this).val());
    });
    	
});

$("#domain-assign").click(function(){
	$("#domainSelection").modal('hide');
	$("#domainAssignForm").submit();
});

$("#create-btn").click(function(){
	let workEffortId = $('#domainAssignForm #workEffortId').val();
	let domainType = $('input[type=radio][name="domainEntityType"]:checked').val();
	let url = "#";
	if(domainType === "SERVICE_REQUEST")
		url = "/sr-portal/control/addservicerequest?workEffortId="+workEffortId+"&externalLoginKey=${requestAttributes.externalLoginKey!}";
	else if(domainType === "OPPORTUNITY")
		url = "/opportunity-portal/control/createOpportunity?workEffortId="+workEffortId+"&externalLoginKey=${requestAttributes.externalLoginKey!}";
	
	$(this).attr("href",url)
	$("#domainSelection").modal('hide');
});


</script>
