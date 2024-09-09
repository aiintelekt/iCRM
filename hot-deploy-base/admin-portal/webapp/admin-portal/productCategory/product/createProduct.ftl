<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
	
<div class="row">
<div id="main" role="main">
<#assign extra='<a href="findProduct" class="btn btn-xs btn-primary"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>' />
 <div class="col-lg-12 col-md-12 col-sm-12 dash-panel">        
<form id="mainForm" action ="<@ofbizUrl>productCreation</@ofbizUrl>" data-toggle="validator" method="post">

<@sectionFrameHeader title="${uiLabelMap.CreateProduct!}" extra=extra />
		 
<@dynaScreen 
	instanceId="PC_PROD"
	modeOfAction="CREATE"
	/> 		 		 
		 		 		 		 		 
<div class="form-group offset-2">
	<div class="text-left pad-10">
      
      <@formButton
	     btn1type="submit"
	     btn1label="${uiLabelMap.Save}"
	     btn1onclick=""
	     btn2=true
	     btn2type="reset"
	     
	      btn2onclick="resetForm();"
	     btn2label="${uiLabelMap.Clear}"
	   />
 
 	
	</div>
</div>
		  
</form>
</div>
</div>
</div>
<script>
 var isDisabled = "";
    $('#prodCatalogId').change(function(){
    	var selectedItem = $(this).val();
    	$.post("getProductCategoryList",{"prodCatalogId": selectedItem,"associated": "Y", "excluded": "N"},function(data){
            var len = data.length;
            var options = '<option value="" data-content="<span>Please Select</span>" selected>Please Select</option>';
            if(data !=null && data !=""){
               for(var i=0;i<data.length;i++){
                     options += '<option value="'+data[i].productCategoryId+'">'+data[i].categoryName+'</option>';
               }
            }
                $("#productCategoryId").empty();
                $("#productCategoryId").append(options);
                $("#productCategoryId").dropdown("refresh");
          });
    });
    var isDisabled = "";
    $('#productCategoryId').change(function(){
    	var selectedItem = $(this).val();
    	$.post("getSubCategoryList",{"productCategoryId": selectedItem,"associated": "Y", "excluded": "N"},function(data){
            var len = data.length;
            var options = '<option value="" data-content="<span>Please Select</span>" selected>Please Select</option>';
            if(data !=null && data !=""){
               for(var i=0;i<data.length;i++){
                     options += '<option value="'+data[i].subCategoryId+'">'+data[i].subCategoryName+'</option>';
               }
            }
                $("#subCategoryId").empty();
                $("#subCategoryId").append(options);
                $("#subCategoryId").dropdown("refresh");
          });
    });
</script>
   
<#include "component://homeapps/webapp/homeapps/user-audit/audit_footer.ftl"/>
   