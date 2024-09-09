<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
<div class="row">
   <div id="main" role="main">
   <#assign extra='<a href="updateNonWorkingDay?holidayConfigId=${holidayConfig?if_exists.holidayConfigId!}" class="btn btn-xs btn-primary"><i class="fa fa-edit" aria-hidden="true"></i> Update</a><a href="findHolidaysList" class="btn btn-xs btn-primary"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>'/>

   	<#-- 
      <#if security.hasPermission("DBS_ADMPR_PUS_CAL_EDIT", userLogin)>
        <#assign extra='<a href="updateNonWorkingDay?holidayConfigId=${holidayConfig?if_exists.holidayConfigId!}" class="btn btn-xs btn-primary"><i class="fa fa-edit" aria-hidden="true"></i> Update</a><a href="findHolidaysList" class="btn btn-xs btn-primary"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>'/>
      <#else>
        <#assign extra='<a href="findHolidaysList" class="btn btn-xs btn-primary"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>'/>
      </#if>  
       -->
       
      <#assign extraLeft=''/>

      <div class="col-md-12 col-lg-12 col-sm-12 dash-panel">

        <@sectionFrameHeaderTab title="${uiLabelMap.ViewCalenderNonWeekendDay!}" tabId="ViewCalenderNonWeekendDay" extra=extra?if_exists/> 
      
      		<@dynaScreen 
				instanceId="PARAM_CAL_NW_DAY"
				modeOfAction="VIEW"
				/>
      	
        <div class="clearfix"></div>
        </div>
   </div>
</div>

