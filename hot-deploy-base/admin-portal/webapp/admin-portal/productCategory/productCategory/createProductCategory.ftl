<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
	
<div class="row">
<div id="main" role="main">
<#assign extra='<a href="findProductCategory" class="btn btn-xs btn-primary"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>' />

<div class="col-lg-12 col-md-12 col-sm-12 dash-panel"> 

<@sectionFrameHeader title="${uiLabelMap.CreateProductCategory!}" extra=extra />
         
<form action ="<@ofbizUrl>productCategoryCreation</@ofbizUrl>" data-toggle="validator" method="post">
		 
<@dynaScreen 
	instanceId="PC_PRO_CATEGORY"
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


   
<#include "component://homeapps/webapp/homeapps/user-audit/audit_footer.ftl"/>
   