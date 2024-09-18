<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
<#assign custRequestCategoryIds= request.getParameter("custRequestCategoryId")!>
<div class="row">
   <div id="main" role="main">
   	<#assign extra='<a href="updateServiceRequestArea?custRequestCategoryId=${inputContext.custRequestCategoryId!}" class="btn btn-xs btn-primary"><i class="fa fa-edit" aria-hidden="true"></i> Update</a><a href="activeSrArea" class="btn btn-xs btn-primary"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>'/>

      <#assign extraLeft=''/>

      <div class="col-md-12 col-lg-12 col-sm-12 dash-panel">
        <@sectionFrameHeaderTab title="${uiLabelMap.ViewSrCategory!}" tabId="ViewSrCategory"  extra=extra?if_exists extraLeft=extraLeft /> 
        <div class="row">
           <div class="col-md-12 col-lg-6 col-sm-12 ">
           
           		<@dynaScreen 
				instanceId="PARAM_SR_AREA"
				modeOfAction="VIEW"
				/>
            
           </div>
           </div>
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
</div>

