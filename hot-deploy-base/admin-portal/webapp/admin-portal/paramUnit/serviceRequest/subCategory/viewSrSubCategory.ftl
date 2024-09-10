<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
<#assign custRequestCategoryIds= request.getParameter("custRequestCategoryId")!>
<div class="row">
   <div id="main" role="main">
    <div class="col-md-12 col-lg-12 col-sm-12 dash-panel">
   	<#assign extra='<a href="updateSrSubCategory?custRequestCategoryId=${inputContext.custRequestCategoryId!}" class="btn btn-xs btn-primary"><i class="fa fa-edit" aria-hidden="true"></i> Update</a><a href="activeSrSubCategory" class="btn btn-xs btn-primary"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>'/>
   
   	<#-- 
   <#if security.hasPermission("DBS_ADMPR_PUS_SRSC_EDIT", userLogin)>
      <#assign extra='<a href="updateSrSubCategory?custRequestCategoryId=${custRequestCategoryId!}" class="btn btn-xs btn-primary"><i class="fa fa-edit" aria-hidden="true"></i> Update</a><a href="activeSrSubCategory" class="btn btn-xs btn-primary"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>'/>
    <#else>
        <#assign extra='<a href="activeSrSubCategory" class="btn btn-xs btn-primary"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>'/>
    </#if>
     --> 
     
      <#assign extraLeft=''/>
      
            <@sectionFrameHeaderTab title="${uiLabelMap.ViewSrSubCategory!}" tabId="ViewSrSubCategory"  extra=extra?if_exists  extraLeft=extraLeft /> 

     
      
      		<@dynaScreen 
				instanceId="PARAM_SR_SUB_AREA"
				modeOfAction="VIEW"
				/>
        
         <div class="clearfix"></div>
        <form action="#" method="post" id="searchForm" name="searchForm">
            <@inputHidden 
              id="custRequestCategoryId"
              name="custRequestCategoryId"
              value="${custRequestCategoryIds!}"
            />
        </form>
        
        
   </div>
</div>

