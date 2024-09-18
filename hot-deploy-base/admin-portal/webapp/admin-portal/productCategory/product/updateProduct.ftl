<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>


      <div class="row">
        <div id="main" role="main">
           <div class="col-md-12 col-lg-12 col-sm-12 dash-panel ">
          <#assign extra='<a href="viewProduct?productCategoryId=${inputContext.productCategoryId!}&prodCatalogId=${inputContext.prodCatalogId!}&subCategoryId=${inputContext.subCategoryId!}&productId=${inputContext.productId!}" class="btn btn-xs btn-primary"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>' />

<form id="mainForm" action ="<@ofbizUrl>productUpdation</@ofbizUrl>" data-toggle="validator" method="post">

    <@sectionFrameHeaderTab title="${uiLabelMap.UpdateProduct!}" tabId="updateProduct" extra=extra/> 

<input type="hidden" id="productId" name="productId" value="${inputContext.productId!}"/>
       
			    
			          
			                      
          	<@dynaScreen 
			instanceId="PC_PROD"
			modeOfAction="UPDATE"
			/>
			
			
			
          	
         
          <div class="form-group offset-2">
            <div class="text-left pad-10">
              <input type="submit" class="btn btn-sm btn-primary" value="Update">
                             <a href="viewProduct?prodCatalogId=${inputContext.prodCatalogId!}&productCategoryId=${inputContext.productCategoryId!}&subCategoryId=${inputContext.subCategoryId!}&productId=${inputContext.productId!}"class="btn btn-sm btn-secondary"> Cancel</a>
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
 	var subCategoryId = "${inputContext.subCategoryId!}";
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
                     options += '<option value="'+data[i].productCategoryId+'" ' + selected + '>'+data[i].categoryName+'</option>';
               }
            }
            $("#productCategoryId").empty();
            $("#productCategoryId").append(options);
            $("#productCategoryId").dropdown("refresh");
            if(productCategoryId!='')
            	$('#productCategoryId').val(productCategoryId).trigger("change");
            productCategoryId = '';
  		});
    });
 	
 	
    $('#productCategoryId').change(function(){
    	var selectedItem = $(this).val();
    	$.post("getSubCategoryList",{"productCategoryId": selectedItem,"associated": "Y", "excluded": "N"},function(data){
            var len = data.length;
            var options = '<option value="" data-content="<span>Please Select</span>" selected>Please Select</option>';
            if(data !=null && data !=""){
               for(var i=0;i<data.length;i++){
               		 var selected = '';
               		if(data[i].subCategoryId == subCategoryId)
               			selected = 'selected';
                     options += '<option value="'+data[i].subCategoryId+'" ' + selected + '>'+data[i].subCategoryName+'</option>';
               }
            }
                $("#subCategoryId").empty();
                $("#subCategoryId").append(options);
                $("#subCategoryId").dropdown("refresh");
                subCategoryId = '';
          });
    });
    $('#prodCatalogId').val(prodCatalogId).trigger("change");
    
</script>
<#include "component://homeapps/webapp/homeapps/user-audit/audit_footer.ftl"/>