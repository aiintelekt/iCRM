<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>


      <div class="row">
        <div id="main" role="main">
          <#assign extra='<a href="viewProductSubCategory?productCategoryId=${inputContext.productCategoryId!}&prodCatalogId=${inputContext.prodCatalogId!}&subCategoryId=${inputContext.subCategoryId!}" class="btn btn-xs btn-primary"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>' />

<form id="mainForm" action ="<@ofbizUrl>productSubCategoryUpdation</@ofbizUrl>" data-toggle="validator" method="post">




<input type="hidden" id="subCategoryId" name="subCategoryId" value="${inputContext.subCategoryId!}"/>
          <div class="col-md-12 col-lg-12 col-sm-12 dash-panel">
         	
         	<@sectionFrameHeaderTab title="${uiLabelMap.UpdateProductSubCategory!}" tabId="updateProductSubCategory" extra=extra/> 
			                      
          	<@dynaScreen 
			instanceId="PC_PRO_SUB_CATEGORY"
			modeOfAction="UPDATE"
			/>
			
			
			
          	
         
          <div class="form-group offset-2">
            <div class="text-left ml-3 p-2">
              <input type="submit" class="btn btn-sm btn-primary" value="Update">
                             <a href="viewProductSubCategory?prodCatalogId=${inputContext.prodCatalogId!}&productCategoryId=${inputContext.productCategoryId!}&subCategoryId=${inputContext.subCategoryId!}"class="btn btn-sm btn-secondary"> Cancel</a>
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
<script>
 	var productCategoryId = "${inputContext.productCategoryId!}";
 	var prodCatalogId = "${inputContext.prodCatalogId!}";
    $('#prodCatalogId').change(function(){
    	var selectedItem = $(this).val();
    	$.post("getProductCategoryList",{"prodCatalogId": selectedItem},function(data){
            var len = data.length;
            var options = '<option value="" data-content="<span>Please Select</span>" selected>Please Select</option>';
            if(data !=null && data !=""){
               for(var i=0;i<data.length;i++){
               		var selected = '';
               		if(data[i].productCategoryId == productCategoryId)
               			selected = 'selected';
                     options += '<option value="'+data[i].productCategoryId+'" + ' + selected + '>'+data[i].categoryName+'</option>';
               }
            }
            $("#productCategoryId").empty();
            $("#productCategoryId").append(options);
            $("#productCategoryId").dropdown("refresh");
            productCategoryId = '';
  		});
    });
 	$('#prodCatalogId').val(prodCatalogId).trigger("change");
 	
</script>
<#include "component://homeapps/webapp/homeapps/user-audit/audit_footer.ftl"/>