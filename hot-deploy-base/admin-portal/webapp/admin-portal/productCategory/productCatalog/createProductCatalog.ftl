<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
	
<div class="row">
<div id="main" role="main">
<#assign extra='<a href="findProductCatalog" class="btn btn-xs btn-primary"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>' />

<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">         

<form action ="<@ofbizUrl>productCatalogCreation</@ofbizUrl>" data-toggle="validator" method="post">

<@sectionFrameHeader title="${uiLabelMap.CreateProductCatalog!}" extra=extra />
		 
		 
<@dynaScreen 
	instanceId="PC_PRO_CATALOG"
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

$("#productCatalog").keyup(function() {
	var productCatalog = $("#productCatalog").val();
	$("#productCatalog_error").empty();

});

function resetForm(){
$('[id*="_error"]').empty();
}
function formSubmission() {
	var isValid = "Y";
	var productCatalog = $("#productCatalog").val();
	
		if (productCatalog == "") {
			$("#productCatalog_error").html('');
			$("#productCatalog_error")
					.append(
							'<ul class="list-unstyled text-danger"><li id="productCatalog_err">Please Enter Product Catalog</li></ul>');
			isValid = "N";
		}
		
		if (isValid == "N") {
			return false;
		} else if (isValid == "Y") {
			if (seqerr != "")
				return false;
			else
				return true;
		}
	}


</script>
   
<#include "component://homeapps/webapp/homeapps/user-audit/audit_footer.ftl"/>
   