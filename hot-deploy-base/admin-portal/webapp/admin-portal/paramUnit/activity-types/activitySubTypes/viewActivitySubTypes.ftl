<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<div class="row">
   <div id="main" role="main">
   	
   	<#assign extra='<a href="updateActivitySubTypes?enumId=${inputContext.activitySubTypeId!}" class="btn btn-xs btn-primary"><i class="fa fa-edit" aria-hidden="true"></i> Update</a><a href="activeActivitySubTypes" class="btn btn-xs btn-primary"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>'/>
   	
   		<#-- 
      <#if security.hasPermission("DBS_ADMPR_PUS_AST_EDIT", userLogin)>
      <#assign extra='<a href="updateActivitySubTypes?enumId=${inputContext.activitySubTypeId!}" class="btn btn-xs btn-primary"><i class="fa fa-edit" aria-hidden="true"></i> Update</a><a href="activeActivitySubTypes" class="btn btn-xs btn-primary"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>'/>
      <#else>
      <#assign extra='<a href="activeActivitySubTypes" class="btn btn-xs btn-primary"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>'/>
      </#if> 
       -->
        
       <#assign extraLeft=''/>
      <@sectionFrameHeader 
            title="${uiLabelMap.ViewActivitySubType!}"
            extra=extra?if_exists
            extraLeft=extraLeft
            />
      <div class="col-md-12 col-lg-12 col-sm-12 ">
           
           <@dynaScreen 
				instanceId="PARAM_ACT_SUB_TYP"
				modeOfAction="VIEW"
				/>  
			                      
           
        <div class="clearfix"></div>
      </div>
   </div>
</div>

