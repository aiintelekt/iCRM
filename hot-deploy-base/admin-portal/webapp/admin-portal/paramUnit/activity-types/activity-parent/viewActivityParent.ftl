<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
<div class="row">
   <div id="main" role="main">
   
   <#assign extra='<a href="updateActivityParent?enumId=${inputContext.activityParentId!}" class="btn btn-xs btn-primary"><i class="fa fa-edit" aria-hidden="true"></i> Update</a><a href="activeActivityParent" class="btn btn-xs btn-primary"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>'/>
   
       <#assign extraLeft=''/>
      <@sectionFrameHeader 
            title="${uiLabelMap.ViewActivityParent!}"
            extra=extra?if_exists
            extraLeft=extraLeft
            />
      <div class="col-md-12 col-lg-12 col-sm-12 ">
      
      		<@dynaScreen 
				instanceId="PARAM_ACT_PRNT"
				modeOfAction="VIEW"
				/>    	
            
        <div class="clearfix"></div>
        </div>
   </div>
</div>

