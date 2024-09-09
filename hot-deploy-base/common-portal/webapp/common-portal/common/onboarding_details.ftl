<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/lib/picker_macro.ftl"/>
<#include "component://common-portal/webapp/common-portal/common/model_invite_users_window.ftl"/>
<#assign partal ="" >
  <#if "ACCOUNT" = '${domainEntityType!}'>
       <#assign partal = "account-portal">
   <#elseif "LEAD" = '${domainEntityType!}'>
       <#assign partal = "lead-portal">    
   <#elseif "CONTACT" = '${domainEntityType!}'>
       <#assign partal = "contact-portal">  
   <#elseif "OPPORTUNITY" = '${domainEntityType!}'>
       <#assign partal = "opportunity-portal"> 
   <#elseif "SERVICE_REQUEST" = '${domainEntityType!}'>
       <#assign partal = "sr-portal">  
   <#elseif "CUSTOMER" = '${domainEntityType!}'>
       <#assign partal = "customer-portal">   
   </#if> 
 	<#assign helpUrl = Static["org.groupfio.common.portal.util.DataHelper"].getTabHelpUrl(delegator, request.getRequestURI(), "OnboardingDetails") />  
<div class="page-header border-b pt-2">
      <h2 class="d-inline-block">Onboarding Contacts</h2>
    
       <ul class="flot-icone">
         <#-- <li class="mt-0"><a href="#" class=" text-dark"><i class="fa fa-refresh fa-1" aria-hidden="true"></i> </a> </li> 
         <li class="mt-0">
         <#if partySummary.lastModifiedDate?has_content> 
            <small>${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(partySummary.lastModifiedDate!, "yyyy-MM-dd")}</small>
         </#if>
         </li>-->
      <#if inputContext.accountStatus?has_content && inputContext.accountStatus = "Active" >  
        <#assign toDisable = "Y"> 
        <li class="mt-0">
          <a href="/account-portal/control/enableOrDisableLoginForAssoc?partyId=${inputContext.partyId!}&toDisable=${toDisable}" class="btn btn-primary btn-xs ml-2">
         Disable Login</a>
        </li>
       <#else>
       <#assign toDisable = "N"> 
       <li class="mt-0">
          <a href="/account-portal/control/enableOrDisableLoginForAssoc?partyId=${inputContext.partyId!}&toDisable=${toDisable}" class="btn btn-primary btn-xs ml-2">
         Enable Login</a>
       </li>
         </#if>
       <li class="mt-0">
          <input type="button" id="invite-users-btn" class="btn btn-primary btn-xs ml-2"  onclick="" value="Invite Users"/>
	   </li>	         
         <#--  <#if hasDeactivatePermission?default(false)>
         <li class="mt-0">
	      	<a class="fa fa-times btn btn-xs btn-danger m5" data-toggle="confirmation" href="javascript:document.deactivateAccountForm.submit();" alt="Deactivate Account" title="Are you sure?	Do you want to deactivate"></a>
	      	<form name="deactivateAccountForm" id="deactivateAccountForm" action="deactivateAccount" method="post">
	      		<input type="hidden" name="partyId" value="${parameters.partyId!}">
	      	</form>
         </li>
         </#if>-->
         <li class="mt-0">${helpUrl?if_exists}</li>
      </ul>
      
</div>

<div class="col-md-11 col-lg-12 col-sm-12">
	<#--<div id="invite-user-grid" style="width: 100%;" class="ag-theme-balham p-1"></div>
	<@AgGrid
	gridheadertitle=""
	gridheaderid="invite-user-grid-container"
	savePrefBtn=false
	clearFilterBtn=false
	exportBtn=false
	insertBtn=false
	updateBtn=false
	removeBtn=false
	headerextra=rightContent
	
    userid="${userLogin.userLoginId}" 
    shownotifications="true" 
    instanceid="INVITE_USERS_LIST" 
    autosizeallcol="true"
    debug="false"
    /> 
  	 	<script type="text/javascript" src="/common-portal-resource/js/ag-grid/contact/find-invite-user.js"></script>-->
	<input type="hidden" id="userId" value="${userLogin.userLoginId!}">
    <input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

	<@fioGrid 
			id="userList"
			instanceId="INVITE_USERS_LIST"
			jsLoc="/common-portal-resource/js/ag-grid/contact/find-invite-user.js"
			headerLabel=""
			headerBarClass="grid-header-no-bar"
			headerId="invite-user-grid-container"
			headerExtra=rightContent!
			subFltrClearBtn = true
			savePrefBtnId="inviteUsers-save-pref-btn"
			clearFilterBtnId="inviteUsers-clear-filter-btn"
			subFltrClearId="sub-filter-clear-btn"
			exportBtn=false
			exportBtnId="inviteUsers-list-export-btn"
			savePrefBtn=false
			clearFilterBtn=false
			subFltrClearBtn=false
			/>
<@inviteUserModal 
	instanceId="invite-user-modal"
	path=false
	/>	  
	
</div>
<script>

jQuery(document).ready(function() {

$('#invite-users-btn').on('click', function() {
	$('#invite-user-modal').modal("hide");
	$('#invite-user-modal').modal("show");
});



});

</script>