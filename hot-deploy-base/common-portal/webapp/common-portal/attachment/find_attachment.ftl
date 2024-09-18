<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/attachment/model_attachment_window.ftl"/>
<#include "component://common-portal/webapp/common-portal/attachment/model_bookmark_window.ftl"/>
<script type="text/javascript" src="/bootstrap/js/jquery.validate-1.15.1.min.js"></script>
<#assign partyId= request.getParameter("partyId")! />
<#assign salesOppId= request.getParameter("salesOpportunityId")! />
<#if salesOppId?has_content>
<#assign salesOppRole=EntityQuery.use(delegator).from("SalesOpportunityRole").where("salesOpportunityId", salesOppId).queryFirst()! />
<#if salesOppRole?has_content>
<#assign partyId=salesOppRole.get("partyId")/>
</#if>
</#if>
<#assign srStatusId= context.get("currentSrStatusId")?if_exists />
<#assign clientPortal= context.get("clientPortal")?if_exists />
<div class="pt-2 align-lists">

<form method="post" id="attachment-search-form" class="form-horizontal" novalidate="true" data-toggle="validator">	

<input type="hidden" name="partyId" value="${partyId?if_exists}"/>
<input type="hidden" name="externalLoginKey" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
<input type="hidden" name="domainEntityType" value="${domainEntityType!}">
<input type="hidden" name="domainEntityId" value="${domainEntityId!}">
<input type="hidden" name="salesOpportunityId" value="${salesOppId!}">
<input type="hidden" name="custRequestId" value="${custRequestId!}">

</form>

</div>
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

	
	<@createAttachmentModal 
	instanceId="create-attachment-modal"
	path="${partal}"
	isMultiple=true
	/>
	
	<@createBookmarkModal 
	instanceId="create-bookmark-modal"
	path="${partal}"
	/> 
<script>
jQuery(document).ready(function() {

$('#create-attachment-btn').on('click', function() {
	$('#create-bookmark-modal').modal("hide");
	$('#create-attachment-modal').modal("show");
});

$('#create-bookmark-btn').on('click', function() {
$('#create-attachment-modal').modal("hide");
	$('#create-bookmark-modal').modal("show");
});

});
</script>
