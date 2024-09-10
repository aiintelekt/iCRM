<#include "component://common-portal/webapp/common-portal/store/modal_window.ftl"/>
<#assign contextPath = request.getContextPath()/>
<#assign marketingCampaignId= request.getParameter("marketingCampaignId")! />
<div class="pt-2 align-lists">
	
    <form method="post" action="" id="findStoreReceipt" name="findStoreReceipt" novalidate="novalidate" data-toggle="validator">
    	<input type="hidden" name="domainEntityType" value="${domainEntityType!}">
    	<input type="hidden" name="domainEntityId" value="${domainEntityId!}">
		<input type="hidden" name="isStoreReceipt" value="Y">
		<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/> 		
	</form>
            
</div>

<div>
<@addStoreReceipt 
	instanceId="add-store-receipt-modal"
	/>
</div>