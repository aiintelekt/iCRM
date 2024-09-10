<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
<div class="row">
   <div id="main" role="main">
   	<#assign extra='<a href="updateSr?custRequestTypeId=${inputContext.custRequestTypeId!}" class="btn btn-xs btn-primary"><i class="fa fa-edit" aria-hidden="true"></i> Update</a><a href="srType" class="btn btn-xs btn-primary"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>'/>
   	<#-- 
     <#if security.hasPermission("DBS_ADMPR_PUS_SRT_EDIT", userLogin)>
        <#assign extra='<a href="updateSr?custRequestTypeId=${custRequestTypeId!}" class="btn btn-xs btn-primary"><i class="fa fa-edit" aria-hidden="true"></i> Update</a><a href="srType" class="btn btn-xs btn-primary"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>'/>
     <#else>
        <#assign extra='<a href="srType" class="btn btn-xs btn-primary"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>'/>
     </#if>  
       -->
       
      <#assign extraLeft=''/>
     
      <div class="col-md-12 col-lg-12 col-sm-12 dash-panel">
      	
       <@sectionFrameHeaderTab title="${uiLabelMap.ViewSrType!}" tabId="ViewSrType"  extra=extra?if_exists extraLeft=extraLeft/> 
				
			 <@dynaScreen 
				instanceId="PARAM_SR_TYPE"
				modeOfAction="VIEW"
				/>    
        
        	 <div class="clearfix"></div>
		        <form action="#" method="post" id="searchForm" name="searchForm">
		            <@inputHidden 
		              id="custRequestTypeId"
		              name="custRequestTypeId"
		              value="${inputContext.get('custRequestTypeId')!}"
		            />
		        </form>
		        
		        
		                
        </div>
       
   </div>
</div>

