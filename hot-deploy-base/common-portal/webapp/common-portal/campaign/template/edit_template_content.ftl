<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/campaign/template/modal_window.ftl"/>
	
<div class="row">
<div id="main" role="main">
<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">

<div class="row">
	<div class="col-lg-8 col-md-8 col-sm-12">
		<h2 class="float-left sub-txt">Edit Contract Content</h2>
		
	</div>
	
	<div class="col-lg-4 col-md-4 col-sm-4 text-right mb-2">
		<#assign extra='<a href="${backLink!}" class="btn btn-xs btn-primary"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>
			<span id="templateContent-update-btn" class="btn btn-xs btn-primary"><i class="fa fa-edit" aria-hidden="true"></i> Update</span>' />
		<@sectionFrameHeaderTab title="" tabId="" extra=extra/> 
	</div>
</div>

<#if inputContext.isContentEditable?has_content && inputContext.isContentEditable=="N">
<div class="row">
	<div class="col-lg-6 col-md-6 col-sm-12">
		<div class="alert alert-warning">
			<strong>Warning!</strong> Contract content cant be edit
		</div>
	</div>
</div>
<#else>

<#assign previewUrl = "#">
<#if domainEntityType?has_content && domainEntityType=="REBATE">
<#if inputContext.is2g?has_content && inputContext.is2g=="N">
<#assign previewUrl = "/rebate-portal/control/agreementnon.pdf?agreementId=${domainEntityId!}">
<#else>
<#assign previewUrl = "/rebate-portal/control/agreement.pdf?agreementId=${domainEntityId!}">
</#if>
</#if>

<div class="row">
	<div class="col-lg-7 col-md-7 col-sm-12">
		<h3 class="float-left mr-2 mb-0 header-title view-title">${inputContext.domainEntityName!} (${domainEntityId!})</h3>
	</div>
	
	<div class="col-lg-5 col-md-5 col-sm-5 text-right mb-2">
		<div class="row">
		<#if domainEntityType?has_content && domainEntityType=="REBATE">
		<div class="col-lg-2 col-md-2 col-sm-2">
		<a href="${previewUrl!}" target="_blank" class="btn btn-xs btn-primary m5"><i class="fa fa-eye" aria-hidden="true"></i> Preview PDF</a>
		</div>
		</#if>
		<div class="col-lg-2 col-md-2 col-sm-2">
		<button id="templateContent-add-btn" type="button" class="btn btn-xs btn-primary m5"><i class="fa fa-plus" aria-hidden="true"></i> Create</button>
		</div>
		<div class="col-lg-2 col-md-2 col-sm-2">
		<button id="templateContent-remove-btn" type="button" class="btn btn-xs btn-primary m5" data-toggle="confirmation" title="Are you sure?"><i class="fa fa-minus" aria-hidden="true"></i> Remove</button>
		</div>
		<div class="col-lg-6 col-md-6 col-sm-6">
		<form id="templateContent-search-form" method="get">	
	    	<input type="hidden" name="domainEntityType" value="${domainEntityType!}">
	        <input type="hidden" name="domainEntityId" value="${domainEntityId!}">
	        <input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
	        
			<@dropdownCell 
		        id="contentTemplateId"
		        name="templateId"
		        label="Template"
		        options=templateList?if_exists
		        value="${templateId!}"
		        required=false
		        placeholder = "Template"
		        />
		</form>
		</div>
		</div>
	</div>
</div>
 
<form id="mainFrom" method="post" action="<@ofbizUrl>updateTemplateContentAction</@ofbizUrl>" data-toggle="validator">
<input type="hidden" name="domainEntityType" value="${domainEntityType!}">
<input type="hidden" name="domainEntityId" value="${domainEntityId!}">
<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
<div class="row padding-r">
   <div class="col-md-12 col-sm-12 tpl_cont_sortable">
    
    <#if contentList?has_content>  
    <#assign contentCount = 1> 
    <#list contentList as content> 
    <div class="row move-pointer">
    <input type="hidden" name="templateContentId" value="${content.templateContentId!}">
    <div class="col-md-9 col-sm-9">
   	
	<@inputArea
        id="contentText_${contentCount}"
        name="contentText"
        label="Content Text"
        rows="4"
        placeholder="Content Text"
        value="${content.contentText!}"
        labelColSize="col-sm-2 move-pointer" 
		inputColSize="col-sm-9"
		required=true
        />  	
	
      <#assign contentCount = contentCount + 1> 
      </div>
      <div class="col-md-3 col-sm-3">
      <@checkbox
        id="remove_${contentCount}"
        name="contentRemove"
        value="${content.templateContentId!}"
        class="form-check-input contentRemove"
        checked=false
        />
      </div>
      <hr>
      </div>
      
      </#list>
      </#if>
      
   </div>
</div>

<div class="form-group offset-2">
	<div class="text-left ml-3">
      
      <@formButton
	     btn1type="submit"
	     btn1label="${uiLabelMap.Update}"
	     btn1onclick="return formSubmission();"
	     btn2=false
	     btn2type="reset"
	     btn2label="${uiLabelMap.Clear}"
	   />
		 	
	</div>
</div>	

</form>
		  
</div>
</div>

</div>

<@createTemplateContentModal
instanceId="add-templateContent-modal"
/>

<script>
jQuery(document).ready(function() {	

$("#templateContent-remove-btn").click(function () {

	var selectedData = $('.contentRemove:checkbox:checked').map(function() {
    	return this.value;
	}).get();
	console.log(selectedData.join(","));
	console.log(selectedData.length);
	
	if (selectedData.length > 0) {
		console.log(selectedData);
	    var selectedItemIds = selectedData.join(",");
	    
	    if (selectedItemIds && selectedData.length > 0){
	    	
	    var inputData = {"selectedItemIds": selectedItemIds, "externalLoginKey": "${requestAttributes.externalLoginKey!}"};
	    
	    $.ajax({
			type : "POST",
			url : "/common-portal/control/removeTemplateContent",
			async : true,
			data : inputData,
			success : function(result) {
				if (result.code == 200) {
					showAlert ("success", "Successfully removed template content# "+selectedItemIds);
					location.reload();
				} else {
					showAlert ("error", data.message);
				}
			},
			error : function() {
				console.log('Error occured');
				showAlert("error", "Error occured!");
			},
			complete : function() {
			}
		});
	}
		
	} else {
		showAlert("error", "Please select atleast one content to be removed!");
	}
	
});

$('#templateContent-add-btn').on('click', function() {
	$('#add-templateContent-modal').modal("show");
});
$('#templateContent-add-form').on('submit', function (e) {
	if (e.isDefaultPrevented()) {
    	// handle the invalid form...
  	} else {
  		e.preventDefault();
  		var action = "createTemplateContent";
  		if ($('#templateContentId').val()) {
  			action = "updateTemplateContent";
  		}
  		$.post("/common-portal/control/"+action, $('#templateContent-add-form').serialize(), function(data) {
			if (data.code == 200) {
				showAlert ("success", data.message);
				$("#add-templateContent-modal").modal('hide');
				location.reload();
			} else {
				showAlert ("error", data.message);
			}
		});
  	}
});

$('#templateContent-update-btn').on('click', function() {
	$('#mainFrom').submit();
});

$('#contentTemplateId').on('change', function() {
	if ($(this).val()) {
		$('#templateContent-search-form').submit();
	}
});

$(".tpl_cont_sortable").sortable({
  	cursor: "move",
  	opacity: 0.5
  	//cursorAt: { left: 5 }
});

});	

</script>
</#if>
   