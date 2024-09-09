<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<div class="row">
   <div id="main" role="main">
   
   	<#assign extra='<a href="updateActivityTypes?enumId=${inputContext.activityTypeId!}" class="btn btn-xs btn-primary"><i class="fa fa-edit" aria-hidden="true"></i> Update</a><a href="activeActivityType" class="btn btn-xs btn-primary"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>'/>
   
   	<#-- 
    <#if security.hasPermission("DBS_ADMPR_PUS_AT_EDIT", userLogin)>
      <#assign extra='<a href="updateActivityTypes?enumId=${activityTypeId!}" class="btn btn-xs btn-primary"><i class="fa fa-edit" aria-hidden="true"></i> Update</a><a href="activeActivityType" class="btn btn-xs btn-primary"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>'/>
     <#else>
        <#assign extra='<a href="activeActivityType" class="btn btn-xs btn-primary"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>'/>
     </#if> 
      -->
      <#assign extraLeft=''/>
      <@sectionFrameHeader 
            title="${uiLabelMap.ViewActivityType!}"
            extra=extra?if_exists
            extraLeft=extraLeft
            />
      <div class="col-md-12 col-lg-12 col-sm-12 ">
        
        	<@dynaScreen 
				instanceId="PARAM_ACT_TYP"
				modeOfAction="VIEW"
				/>
        
        <div class="clearfix"></div>
        </div>
   </div>
</div>

