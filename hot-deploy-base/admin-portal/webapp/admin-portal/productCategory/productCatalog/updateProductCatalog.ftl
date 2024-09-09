<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>


      <div class="row">
        <div id="main" role="main">
          <div class="col-md-12 col-lg-12 col-sm-12 dash-panel ">
          <#assign extra='<a href="viewProductCatalog?prodCatalogId=${inputContext.prodCatalogId!}" class="btn btn-xs btn-primary"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>' />

<form id="mainForm" action ="<@ofbizUrl>productCatalogUpdation</@ofbizUrl>" data-toggle="validator" method="post">

         <@sectionFrameHeaderTab title="${uiLabelMap.UpdateProductCatalog!}" extra=extra tabId="updateProductCatalog"/> 


<input type="hidden" id="prodCatalogId" name="prodCatalogId" value="${inputContext.prodCatalogId!}"/>
        
			    
			          
			                      
          	<@dynaScreen 
			instanceId="PC_PRO_CATALOG"
			modeOfAction="UPDATE"
			/>
			
			
			
          	
         
          <div class="form-group offset-2">
            <div class="text-left  p-2">
              <input type="submit" class="btn btn-sm btn-primary" value="Update">
                             <a href="viewProductCatalog?prodCatalogId=${prodCatalogId!}"class="btn btn-sm btn-secondary"> Cancel</a>
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
		  </form>
        </div>
      </div>
  </div>
   
<#include "component://homeapps/webapp/homeapps/user-audit/audit_footer.ftl"/>