<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#assign readOnlyPermission = Static["org.fio.homeapps.util.DataUtil"].hasPermissionWoFullPerm(request, "READ_ONLY_PERM")?if_exists />
<#assign contextPath = request.getContextPath()/>
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
<div class="row">
	<#if mainAssocPartyId?has_content>
	<#assign partyId= mainAssocPartyId />
	<#else>
	<#assign partyId= request.getParameter("partyId")! />
	<#assign salesOpportunityId = '${requestParameters.salesOpportunityId!}' >
	</#if>
	<#assign srStatusId= context.get("currentSrStatusId")?if_exists />
	<#assign extraLeft = '' />
	<#assign helpUrl = Static["org.groupfio.common.portal.util.DataHelper"].getHelpUrl(delegator, request.getRequestURI(), "activities") /> 
	<#assign hasPermission = Static["org.fio.homeapps.util.DataUtil"].hasPermission(request, "CREATE_OPERATION")?if_exists />
	
	<#assign isEnableClosedOppoEmailActivity = Static["org.fio.homeapps.util.DataUtil"].getGlobalValue(delegator, "ENABLE_CLOSED_OPPO_EMAIL_ACT")?if_exists>
	<#assign isEnableActivity=""/>
	<#if !readOnlyPermission!>
		<#assign isEnableActivity="Y"/>
		<#if isDisableActivity?has_content && isDisableActivity == "Y">
			<#assign isEnableActivity="N"/>
		</#if>	
		
		<#if isOppoClosed?has_content && (isEnableClosedOppoEmailActivity?has_content && isEnableClosedOppoEmailActivity == "N")>
			<#assign isEnableActivity="N"/>
		</#if>
		
		<#if !hasPermission>
			<#assign isEnableActivity="N"/>
		</#if>
		
		<#if partyStatusId?if_exists != "PARTY_DISABLED">
		<#if isEnableActivity == "Y">
			<#assign extraLeft = extraLeft +'
			<span id="create-activity" class="text-dark btn" style="padding: 1px 5px !important;" data-toggle="dropdown" title="Add Activity" aria-expanded="false"> '/> 
			<#if (srStatusId?has_content && ("SR_CLOSED" != srStatusId || "SR_CANCELLED" != srStatusId)) || !srStatusId?has_content >
				<#assign extraLeft = extraLeft +' <i class="fa fa-plus fa-1" aria-hidden="true"></i>' />
			</#if>
			<#assign extraLeft = extraLeft +'</span>' />
		</#if>	
		</#if>
		
		<#if isEnableActivity=="Y">
			<#if (srStatusId?has_content && ("SR_CLOSED" != srStatusId || "SR_CANCELLED" != srStatusId)) || !srStatusId?has_content  >
			<#assign extraLeft = extraLeft +'<div class="dropdown-menu" aria-labelledby="create-activity">
				<h4>Add Activities</h4>' />
				
				<#if domainEntityType! == "OPPORTUNITY">
					<#if !isOppoClosed?has_content || isOppoClosed=='N'>
						<#assign extraLeft = extraLeft +'<a class="dropdown-item" href="/opportunity-portal/control/createTaskActivity?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&externalLoginKey=${requestAttributes.externalLoginKey!}&salesOpportunityId=${salesOpportunityId?if_exists}" target="_blank"><i class="fa fa-clipboard" aria-hidden="true"></i> Task/Schedule</a>' />
					</#if>
				<#elseif domainEntityType! == "SERVICE_REQUEST" && contextPath.contains('ticket-portal')>
					<#assign extraLeft = extraLeft +'<a class="dropdown-item" href="/ticket-portal/control/createTaskActivity?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&externalLoginKey=${requestAttributes.externalLoginKey!}&srNumber=${custRequestId?if_exists}&custRequestId=${custRequestId?if_exists}" target="_blank"><i class="fa fa-clipboard" aria-hidden="true"></i> Task/Schedule</a>' />
				<#elseif domainEntityType! == "SERVICE_REQUEST">
					<#assign extraLeft = extraLeft +'<a class="dropdown-item" href="/sr-portal/control/createTaskActivity?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&externalLoginKey=${requestAttributes.externalLoginKey!}&srNumber=${custRequestId?if_exists}&custRequestId=${custRequestId?if_exists}" target="_blank"><i class="fa fa-clipboard" aria-hidden="true"></i> Task/Schedule</a>' />
				<#elseif domainEntityType! == "CUSTOMER">
					<#assign extraLeft = extraLeft +'<a class="dropdown-item" href="/customer-portal/control/createTaskActivity?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank"><i class="fa fa-clipboard" aria-hidden="true"></i> Task/Schedule</a>' />
				<#elseif domainEntityType! == "REBATE">
			    	<#assign extraLeft = extraLeft +'<a class="dropdown-item" href="/rebate-portal/control/createTaskActivity?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank"><i class="fa fa-clipboard" aria-hidden="true"></i> Task/Schedule</a>' />
				<#elseif domainEntityType! == "LEAD">
					<#assign extraLeft = extraLeft +'<a class="dropdown-item" href="/lead-portal/control/createTaskActivity?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank"><i class="fa fa-clipboard" aria-hidden="true"></i> Task/Schedule</a>' />
			     <#elseif domainEntityType! == "CONTACT">
			    	<#assign extraLeft = extraLeft +'<a class="dropdown-item" href="/contact-portal/control/createTaskActivity?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank"><i class="fa fa-envelope" aria-hidden="true"></i> Task/Schedule</a>' />
			    <#else>
			    	<#assign extraLeft = extraLeft +'<a class="dropdown-item" href="/account-portal/control/createTaskActivity?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank"><i class="fa fa-clipboard" aria-hidden="true"></i> Task/Schedule</a>' />
			    </#if>
			    
			    <#if domainEntityType! == "OPPORTUNITY"> 
			    	<#if !isOppoClosed?has_content || isOppoClosed=='N'>
			   		 	<#assign extraLeft = extraLeft +'<a class="dropdown-item" href="/opportunity-portal/control/createPhoneCallActivity?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&externalLoginKey=${requestAttributes.externalLoginKey!}&salesOpportunityId=${salesOpportunityId?if_exists}" target="_blank"><i class="fa fa-phone" aria-hidden="true"></i> Phone Call</a>' />
			   		</#if>
			    <#elseif domainEntityType! == "SERVICE_REQUEST" && contextPath.contains('ticket-portal')>
			    	<#assign extraLeft = extraLeft +'<a class="dropdown-item" href="/ticket-portal/control/createPhoneCallActivity?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&externalLoginKey=${requestAttributes.externalLoginKey!}&srNumber=${custRequestId?if_exists}&custRequestId=${custRequestId?if_exists}" target="_blank"><i class="fa fa-phone" aria-hidden="true"></i> Phone Call</a>' />
			    <#elseif domainEntityType! == "SERVICE_REQUEST">
			    	<#assign extraLeft = extraLeft +'<a class="dropdown-item" href="/sr-portal/control/createPhoneCallActivity?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&externalLoginKey=${requestAttributes.externalLoginKey!}&srNumber=${custRequestId?if_exists}&custRequestId=${custRequestId?if_exists}" target="_blank"><i class="fa fa-phone" aria-hidden="true"></i> Phone Call</a>' />
			    <#elseif domainEntityType! == "CUSTOMER">
			    	<#assign extraLeft = extraLeft +'<a class="dropdown-item" href="/customer-portal/control/createPhoneCallActivity?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank"><i class="fa fa-phone" aria-hidden="true"></i> Phone Call</a>' />
				<#elseif domainEntityType! == "REBATE">
			    	<#assign extraLeft = extraLeft +'<a class="dropdown-item" href="/rebate-portal/control/createPhoneCallActivity?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank"><i class="fa fa-phone" aria-hidden="true"></i> Phone Call</a>' />
				<#elseif domainEntityType! == "LEAD">
					<#assign extraLeft = extraLeft +'<a class="dropdown-item" href="/lead-portal/control/createPhoneCallActivity?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank"><i class="fa fa-phone" aria-hidden="true"></i> Phone Call</a>' />
			     <#elseif domainEntityType! == "CONTACT">
			    	<#assign extraLeft = extraLeft +'<a class="dropdown-item" href="/contact-portal/control/createPhoneCallActivity?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank"><i class="fa fa-envelope" aria-hidden="true"></i> Phone Call</a>' />
			    <#else>
			    	<#assign extraLeft = extraLeft +'<a class="dropdown-item" href="/account-portal/control/createPhoneCallActivity?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank"><i class="fa fa-phone" aria-hidden="true"></i> Phone Call</a>' />
			    </#if>
			    
			    <#if domainEntityType! == "OPPORTUNITY">
			   		<#if !isOppoClosed?has_content || (!isEnableClosedOppoEmailActivity?has_content || isEnableClosedOppoEmailActivity == "Y")>
			    		<#assign extraLeft = extraLeft +'<a class="dropdown-item" href="/opportunity-portal/control/addEmail?partyId=${partyId?if_exists}&salesOpportunityId=${salesOpportunityId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank"><i class="fa fa-envelope" aria-hidden="true"></i> Email</a>' />
			    	</#if>
			    <#elseif domainEntityType! == "ACCOUNT">
			    	<#assign extraLeft = extraLeft +'<a class="dropdown-item" href="/account-portal/control/addEmail?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank"><i class="fa fa-envelope" aria-hidden="true"></i> Email</a>' />
			    <#elseif domainEntityType! == "LEAD">
			    	<#assign extraLeft = extraLeft +'<a class="dropdown-item" href="/lead-portal/control/addEmail?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank"><i class="fa fa-envelope" aria-hidden="true"></i> Email</a>' />
			    <#elseif domainEntityType! == "CONTACT">
			    	<#assign extraLeft = extraLeft +'<a class="dropdown-item" href="/contact-portal/control/addEmail?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank"><i class="fa fa-envelope" aria-hidden="true"></i> Email</a>' />
			    <#elseif domainEntityType! == "SERVICE_REQUEST" && contextPath.contains('ticket-portal')>
			    	<#assign extraLeft = extraLeft +'<a class="dropdown-item" href="/ticket-portal/control/createEmailActivity?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&externalLoginKey=${requestAttributes.externalLoginKey!}&srNumber=${custRequestId?if_exists}&custRequestId=${custRequestId?if_exists}" target="_blank"><i class="fa fa-envelope" aria-hidden="true"></i> Email</a> '/>
			    <#elseif domainEntityType! == "SERVICE_REQUEST">
			    	<#assign extraLeft = extraLeft +'<a class="dropdown-item" href="/sr-portal/control/createEmailActivity?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&externalLoginKey=${requestAttributes.externalLoginKey!}&srNumber=${custRequestId?if_exists}&custRequestId=${custRequestId?if_exists}" target="_blank"><i class="fa fa-envelope" aria-hidden="true"></i> Email</a> '/>
			    <#elseif domainEntityType! == "CUSTOMER">
			    	<#assign extraLeft = extraLeft +'<a class="dropdown-item" href="/customer-portal/control/addEmail?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank"><i class="fa fa-envelope" aria-hidden="true"></i> Email</a>' />
			    <#elseif domainEntityType! == "REBATE">
			    	<#assign extraLeft = extraLeft +'<a class="dropdown-item" href="/rebate-portal/control/addEmail?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank"><i class="fa fa-envelope" aria-hidden="true"></i> Email</a>' />
			    </#if>
			    
		   	<#assign extraLeft = extraLeft +' </div>'/>
		    </#if>
		</#if>
	</#if>
	
    <#assign extraLeft = extraLeft +''/>
    <#if isEnableActivity=="Y">
		<#assign extraLeft = extraLeft +'<span class="text-left" style="margin-top: -32px;">' />
	<#else>
		<#assign extraLeft = extraLeft +'<span class="text-left"> '/>
	</#if>
	<#assign extraLeft = extraLeft +'
		<div class="form-check-inline ml-30">
			<label class="form-check-label"> 
			<input type="checkbox" class="form-check-input activity-status" name="openchk" id="openchk" value="IA_OPEN" checked>Open
			</label>
		</div>
		<div class="form-check-inline">
			<label class="form-check-label"> 
			<input type="checkbox" class="form-check-input activity-status" name="closedchk" id="closedchk" value="IA_MCOMPLETED">Completed
			</label>
		</div>
		<div class="form-check-inline">
			<label class="form-check-label"> 
			<input type="checkbox" class="form-check-input activity-status" name="schedulechk" id="schedulechk" value="IA_MSCHEDULED">Scheduled
			</label>
		</div>
	' />
	<#assign enableChecklistDomainType = Static["org.fio.homeapps.util.DataUtil"].getGlobalValue(delegator, "ENABLE_CHECKLIST_DOMAIN_TYPE","CUSTOMER")?if_exists>
	<#if isEnableProgramAct?has_content && isEnableProgramAct="Y" && enableChecklistDomainType?has_content && domainEntityType == enableChecklistDomainType>
	<#assign extraLeft = extraLeft +'
		<div class="form-check-inline">
			<label class="form-check-label pr-2"> 
			<input type="radio" class="form-check-input" name="activity-cat" value="PROG"> Checklist 
			</label>
			<label class="form-check-label"> 
			<input type="radio" class="form-check-input" name="activity-cat" value="OTHER"> Not-Checklist
			</label>
		</div>
		'/>
	</#if>
	<#assign extraLeft = extraLeft +'</span>'/>
		
	<#assign rightContent='
		<button id="refresh-activity-btn" type="button" class="btn btn-xs btn-primary m5"><i class="fa fa-refresh" aria-hidden="true"></i></button>
		' />	
		
  	<div class="col-lg-12 col-md-12 col-sm-12">
	<#--   	  	
  	<@AgGrid
		gridheadertitle="Activities"
		gridheaderid="activity-grid-action-container"
		savePrefBtn=true
		clearFilterBtn=true
		exportBtn=true
		insertBtn=false
		updateBtn=false
		removeBtn=false
		helpBtn=true
		helpUrl=helpUrl!
		headerextra=rightContent!
		headerextraleft =extraLeft!
		refreshPrefBtnId="activity-refresh-pref-btn"
		savePrefBtnId="activity-save-pref-btn"
		clearFilterBtnId="activity-clear-filter-btn"
		subFltrClearId="activity-sub-filter-clear-btn"
		exportBtnId="activity-export-btn"
	    userid="${userLogin.userLoginId}" 
	    shownotifications="true" 
	    instanceid="ACTIVITES" 
	    autosizeallcol="true"
	    debug="false"
	    /> 
  	<script type="text/javascript" src="/common-portal-resource/js/ag-grid/activity/find-activity.js"></script>
  	
  	-->
  	<@fioGrid
		instanceId="ACTIVITES"
		jsLoc="/common-portal-resource/js/ag-grid/activity/find-activity.js"
		headerLabel="Activities"
		headerId="activities_tle"
		headerExtra=rightContent!
		headerBarClass="grid-header-no-bar"
		headerExtraLeft = extraLeft!
		savePrefBtnId="activities-save-pref"
		clearFilterBtnId="activities-clear-pref"
		subFltrClearId="activities-clear-sub-ftr"
		serversidepaginate=false
		statusBar=false
		helpBtn=true
		helpUrl=helpUrl!
		exportBtn=true
		exportBtnId="activity-export-btn"
		savePrefBtn=false
		clearFilterBtn=false
		subFltrClearBtn=false
		/>
           
  	</div>
  	<span id="act-search-btn"></span>
</div>
<script>
$(document).ready(function() {
	
});
</script>