<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/lib/picker_macro.ftl"/>

<div class="row">
    <div id="main" role="main">
      <div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
        <#assign extra='<a href="/admin-portal/control/viewLov?lovId=${inputContext.lovId!}&lovTypeId=${inputContext.lovTypeId!}" class="btn btn-xs btn-primary">
        <i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>' />
        <@sectionFrameHeaderTab title="Template Category" tabId="UpdateTemplateCategory" extra=extra/> 
        <div class="clearfix"></div>
        <form id="mainFrom" method="post" action="<@ofbizUrl>updateTemplateCategoryAction</@ofbizUrl>" data-toggle="validator"> 
       <@inputHidden id="templateCategoryId" value="${inputContext.templateCategoryId!}"/>	
            	<@dynaScreen 
					instanceId="TEMPLATE_CATEGORY_BASE"
					modeOfAction="UPDATE"
					/>
            <div class="form-group offset-2">
            <div class="text-left ml-3 pad-10">
         
            <@submit label="${uiLabelMap.Save}"/>
            <@cancel label="Cancel" onclick="/admin-portal/control/viewTemplateCategory?parentTemplateCategoryId=${inputContext.parentTemplateCategoryId!}&templateCategoryId=${inputContext.templateCategoryId!}"/>
            
            </div>
            </div>
        </form>
    </div>
</div>
</div>
<@partyPicker 
	instanceId="partyPicker"
	/> 

<script>

$(document).ready(function() {

});

</script>