<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/lib/picker_macro.ftl"/>
	
  <div class="row">
        <div id="main" role="main">
         
         <div class="col-sm-12 col-md-12 col-lg-12 dash-panel">
         <@sectionFrameHeader title="Working Hours Value Configuration"  />
         
  			 <form id="mainForm" method="post" action="<@ofbizUrl>storeWorkingHrs</@ofbizUrl>" data-toggle="validator">
  			    
                    
                          <@dynaScreen 
							instanceId="WORKING_HRS_DYNA"
							modeOfAction="CREATE"
							/> 
         	  
         	   <div class="offset-md-2 col-sm-10 pad-10">
	           		<@formButton
	                     btn1type="submit"
	                     btn1label="${uiLabelMap.Save}"
	                    
	                />
	            </div>		
         	   
  			</form>
  			
 		</div>
    </div>
 </div>
   
<#include "component://homeapps/webapp/homeapps/user-audit/audit_footer.ftl"/>


<script>
$(document).ready(function(){
$('#workStartTime').attr('readonly', true);

$('#workEndTime').attr('readonly', true);
 });
</script>
 

	
	



