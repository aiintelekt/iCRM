<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/lib/picker_macro.ftl"/>
<#include "component://common-portal/webapp/common-portal/account/modal_window.ftl"/>
 <#assign helpUrl = Static["org.groupfio.common.portal.util.DataHelper"].getTabHelpUrl(delegator, request.getRequestURI(), "profileDetails") />  

<div class="pt-2">
      <h2 class="d-inline-block">General Details</h2>
       <ul class="flot-icone">
         <#-- <li class="mt-0"><a href="#" class=" text-dark"><i class="fa fa-refresh fa-1" aria-hidden="true"></i> </a> </li> -->
          <#if inputContext?has_content && inputContext.isParentAccount?if_exists =="Y">
         <li class="mt-0">
         	<span data-toggle="modal" data-target="#childAccounts" title="Child Accounts" class="btn btn-xs btn-primary"><i class="fa fa-eye" aria-hidden="true"></i> Child Accounts</span>
         
         </li>
         </#if>
         <#if hasReassignPermission?default(false)>
         <li class="mt-0">
	      	<span data-toggle="modal" data-target="#partyResponsible" title="Reassign" class="btn btn-xs btn-primary"><i class="fa fa-edit" aria-hidden="true"></i> Reassign</span>
         </li>
          <li class="mt-0">
            <a href="<@ofbizUrl>updateAccount?partyId=</@ofbizUrl>${inputContext.partyId!}" class="btn btn-xs btn-primary"><i class="fa fa-edit" aria-hidden="true"></i> Update</a>
         </li>
         </#if>
        
			         
         <#--  <#if hasDeactivatePermission?default(false)>
         <li class="mt-0">
	      	<a class="fa fa-times btn btn-xs btn-danger m5" data-toggle="confirmation" href="javascript:document.deactivateAccountForm.submit();" alt="Deactivate Account" title="Are you sure?	Do you want to deactivate"></a>
	      	<form name="deactivateAccountForm" id="deactivateAccountForm" action="deactivateAccount" method="post">
	      		<input type="hidden" name="partyId" value="${parameters.partyId!}">
	      	</form>
         </li>
         </#if>-->
         <li>${helpUrl?if_exists}</li>

      </ul>
      
</div>

<div class="col-md-12 col-lg-12 col-sm-12">
			
	<@dynaScreen 
	instanceId="ACCT_BASE_INFO"
	modeOfAction="VIEW"
	/>
	
<@responsiblePickerAccount 
    instanceId="partyResponsible"
/>	
	    
<@accountPicker 
	instanceId="accountPicker"
/>	

<@childAccountPicker 
	instanceId="childAccounts"
/>
  
</div>