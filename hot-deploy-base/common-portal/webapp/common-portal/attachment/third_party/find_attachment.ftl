<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/attachment/third_party/model_attachment_window.ftl"/>
<script type="text/javascript" src="/bootstrap/js/jquery.validate-1.15.1.min.js"></script>

<#assign partyId= request.getParameter("partyId")! />
<#assign srStatusId= context.get("currentSrStatusId")?if_exists />

<div class="pt-2 align-lists">

<form method="post" id="attachment-thirdpty-search-form" class="form-horizontal" novalidate="true" data-toggle="validator">	

<input type="hidden" name="partyId" value="${partyId?if_exists}"/>
<input type="hidden" name="externalLoginKey" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
<input type="hidden" name="domainEntityType" value="${domainEntityType!}">
<input type="hidden" name="domainEntityId" value="${domainEntityId!}">
<input type="hidden" name="custRequestId" value="${custRequestId!}">
<input type="hidden" name="classificationEnumTypes" value="THIRDPTY_CONTENT_CLASS">

</form>

</div>
	
<@createAttachmentModal 
instanceId="create-attachment-thirdpty-modal"
/>

<@imgPreviewModal 
instanceId="img-preview-modal"
/>
	
<script>

jQuery(document).ready(function() {

$('#create-attachment-thirdpty-btn').on('click', function() {
	$('#create-attachment-thirdpty-modal').modal("show");
});

});

</script>
