<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>


      <div class="row">
        <div id="main" role="main">
          <#assign extra='<a href="viewProductCategory?productCategoryId=${inputContext.productCategoryId!}&prodCatalogId=${inputContext.prodCatalogId!}" class="btn btn-xs btn-primary"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>' />

<form  action ="<@ofbizUrl>productCategoryUpdation</@ofbizUrl>" data-toggle="validator" method="post">



<input type="hidden" id="productCategoryId" name="productCategoryId" value="${inputContext.productCategoryId!}"/>
<input type="hidden" id="prodCatalog" name="prodCatalog" value="${inputContext.prodCatalogId!}"/>
          <div class="col-md-12 col-lg-12 col-sm-12 dash-panel">
			          
         <@sectionFrameHeaderTab title="${uiLabelMap.UpdateProductCategory!}" tabId="updateProductCategory" extra=extra/> 
			                      
          	<@dynaScreen 
			instanceId="PC_PRO_CATEGORY"
			modeOfAction="UPDATE"
			/>
          	
          <div class="form-group offset-2">
            <div class="text-left pad-10">
              <input type="submit" class="btn btn-sm btn-primary" value="Update">
                             <a href="viewProductCategory?prodCatalogId=${inputContext.prodCatalogId!}&productCategoryId=${inputContext.productCategoryId!}"class="btn btn-sm btn-secondary"> Cancel</a>
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
 
   
<#include "component://homeapps/webapp/homeapps/user-audit/audit_footer.ftl"/>