<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>

      <div class="row">
        <div id="main" role="main">
        <div class="col-md-12 col-lg-12 col-sm-12 dash-panel ">	
          <#assign extra='<a href="viewSrSubCategory?custRequestCategoryId=${inputContext.custRequestCategoryId!}" class="btn btn-xs btn-primary"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>' />
         <@sectionFrameHeaderTab title="${uiLabelMap.UpdateSrSubCategory!}" tabId="UpdateSrSubCategory" extra=extra/> 

			<form id="mainForm" action ="<@ofbizUrl>updateSrSubCategorys</@ofbizUrl>" method="post">
			
					<input type="hidden" id="custRequestCategoryId" name="custRequestCategoryId" value="${inputContext.custRequestCategoryId!}"/>
			
						          	
			          	<@dynaScreen 
							instanceId="PARAM_SR_SUB_AREA"
							modeOfAction="UPDATE"
							/>		          	
		          	
		          	<div class="form-group offset-2">
			            <div class="text-left ml-3 p-2">
			             <input type="submit" class="btn btn-sm btn-primary"  value="Update"/>
			             <a href="viewSrSubCategory?custRequestCategoryId=${custRequestCategoryId!}"class="btn btn-sm btn-secondary"> Cancel</a>
			            </div>
			        </div>
			  </form>
        </div>
      </div>
 </div>
 <script>


 jQuery(document).ready(function() {	 
	 $("input[type=submit]").click(function(){		
		 	if($("#statusId").val() == "") {		 		
		 		$("#statusId_error").html('');
	          $("#statusId_error").append('<ul class="list-unstyled text-danger">Please Select Status</ul>');
	          //return false;
	        }else{
	        	$("#statusId_error").html('');
	        }
	        if($("#srSubArea").val() == "") {
	          $("#srSubArea_error").html('');
	          $("#srSubArea_error").append('<ul class="list-unstyled text-danger">Please Select SR Sub Category</ul>');
	         // return false;
	        }else{
	        	$("#srSubArea_error").html('');
	        }
	        if($("#srCategoryId").val() == "") {
	          $("#srCategoryId_error").html('');
	          $("#srCategoryId_error").append('<ul class="list-unstyled text-danger">Please Select SR Category</ul>');
	          // return false;
	        } else{
	        	$("#srCategoryId_error").html('');
	        }
		 
		 
	 });
 });
 
 
 


</script> 
   
