<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
<#include "component://homeapps/webapp/homeapps/user-audit/audit_page_header.ftl"/>

<div class="row">
   <div id="main" role="main">
   <div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
      <#assign extra='<a href="findHolidaysList" class="btn btn-xs btn-primary"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>'/>
      <#assign extraLeft=''/>
      <@sectionFrameHeader 
          title="${uiLabelMap.NewNonWorkingDays}"
          extra=extra?if_exists
          />

<form name="mainForm" id="mainFrom" action="createHolidayConfiguration" method="post" data-toggle="validator"  >

<#include "component://homeapps/webapp/homeapps/user-audit/audit_from_header.ftl"/>
			
			<@dynaScreen 
				instanceId="PARAM_CAL_NW_DAY"
				modeOfAction="CREATE"
				/>
			
         <div class="clearfix"></div>
         <div class="row">
            <div class="form-group offset-2">
               <div class="text-left ml-2">
                  <@formButton
                     btn1type="submit"
                     btn1label="${uiLabelMap.Save}"
                     btn1onclick="return formSubmission();"
                     btn2=true
                     btn2onclick = "resetForm()"
                     btn2type="reset"
                     btn2label="${uiLabelMap.Clear}"
                   />
               </div>
            </div>
            
         </div>
      </div>
     </form>
   </div> <#-- main end -->
</div> <#-- row end-->
</div>
<script type="text/javascript">

$(document).ready(function(){

});

</script>

<#include "component://homeapps/webapp/homeapps/user-audit/audit_footer.ftl"/>
