<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
<#include "component://homeapps/webapp/homeapps/user-audit/audit_page_header.ftl"/>

      <div class="row">
        <div id="main" role="main">
          <#assign extra='<a href="viewServiceRequestType?custRequestTypeId=${inputContext.custRequestTypeId!}" class="btn btn-xs btn-primary"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>' />
<form id="" action ="<@ofbizUrl>srTypeUpdation</@ofbizUrl>" method="post">

<#include "component://homeapps/webapp/homeapps/user-audit/audit_from_header.ftl"/>

<input type="hidden" id="custRequestTypeId" name="custRequestTypeId" value="${inputContext.custRequestTypeId!}"/>
          <div class="col-md-12 col-lg-12 col-sm-12 dash-panel">
         	
           <@sectionFrameHeader title="${uiLabelMap.UpdateSrType!}" extra=extra />
			                      
          	<@dynaScreen 
			instanceId="PARAM_SR_TYPE"
			modeOfAction="UPDATE"
			/>
          	
          <div class="form-group offset-2">
            <div class="text-left ml-3 pad-10">
              <input type="submit" class="btn btn-sm btn-primary" onclick="return formSubmission();" value="Update">
                             <a href="viewServiceRequestType?custRequestTypeId=${custRequestTypeId!}"class="btn btn-sm btn-secondary"> Cancel</a>
            	<#-- <@fromCommonAction showCancelBtn=false showClearBtn=true/> -->
            
            <#-- 
              <@formButton
                     btn1type="submit"
                     btn1label="${uiLabelMap.Update}"
                     btn1onclick="return formSubmission();"
                     btn2=true
                     btn2onclick = "resetForm()"
                     btn2type="reset"
                     btn2label="${uiLabelMap.Clear}"
                   />
               -->		
               
            </div>
          </div>
          
          </div>
		  </form>
        </div>
      </div>
 <script>
 

   </script>
   
