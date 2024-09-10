<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/campaign/template/modal_window.ftl"/>

<div class="row">
	<div class="col-lg-8 col-md-8 col-sm-12">
		<h2 class="float-left sub-txt">Template Content List</h2>
		
	</div>
	
	<div class="col-lg-4 col-md-4 col-sm-4 text-right mb-2">
		<form id="templateContent-search-form" name="templateContent-search-form" method="post">	
	    	<input type="hidden" name="domainEntityType" value="${domainEntityType!}">
	        <input type="hidden" name="domainEntityId" value="${domainEntityId!}">
	        <input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
	        
			<@dropdownCell 
		        id="contentTemplateId"
		        name="templateId"
		        label="Template"
		        options=templateList?if_exists
		        required=false
		        placeholder = "Template"
		        />
		</form>        
	</div>
</div>

<@createTemplateContentModal
instanceId="add-templateContent-modal"
/>
<@viewTemplateContentModal
instanceId="view-templateContent-modal"
/>
 
<script>
jQuery(document).ready(function() {
	
$('#templateContent-add-btn').on('click', function() {
	$('#add-templateContent-modal').modal("show");
});

});
</script>
