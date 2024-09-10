<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
<#include "component://homeapps/webapp/homeapps/user-audit/audit_page_header.ftl"/>
<div class="row">
   <div id="main" role="main">
      <#assign extra='<a href="viewNonWorkingDay?holidayConfigId=${inputContext?if_exists.holidayConfigId!}" class="btn btn-xs btn-primary"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>'/>
      <#assign extraLeft=''/>

<form name="mainForm" id="mainForm" action="updateHolidayConfig" method="post" data-toggle="validator"  >

<#include "component://homeapps/webapp/homeapps/user-audit/audit_from_header.ftl"/>

	
          <div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
      			
      			<@sectionFrameHeaderTab title="${uiLabelMap.UpdateNonWorkingDays}" tabId="UpdateNonWorkingDays" extra=extra?if_exists/> 
      			
                <@inputHidden 
                        id="holidayConfigId"
                        value=inputContext.holidayConfigId!
                      />
                      <@inputHidden 
                        id="nonWorkingDate"
                        value=inputContext.nonWorkingDate!
                      />
				               
               	<@dynaScreen 
					instanceId="PARAM_CAL_NW_DAY"
					modeOfAction="UPDATE"
					/>       
                      
         <div class="clearfix"></div>
         <div class="row">
            <div class="form-group offset-2">
               <div class="text-left ml-2">
               
               	<@submit label="${uiLabelMap.Save}"/>
            	<@cancel label="Cancel" onclick="/admin-portal/control/viewNonWorkingDay?holidayConfigId=${inputContext?if_exists.holidayConfigId!}"/>
               
               </div>
            </div>
            
         </div>
      </div>
     </form>
   </div> <#-- main end -->
</div> <#-- row end-->

<script type="text/javascript">
$(document).ready(function(){
	
});
</script>

<#include "component://homeapps/webapp/homeapps/user-audit/audit_footer.ftl"/>
