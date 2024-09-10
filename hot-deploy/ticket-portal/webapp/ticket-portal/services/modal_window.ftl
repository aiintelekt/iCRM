<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<#macro generateProgramActivity instanceId>

<div id="${instanceId!}" class="modal fade" >
  <div class="modal-dialog modal-md">
    <!-- Modal content-->
    <div class="modal-content">
      <div class="modal-header">
        <h2 class="modal-title">Generate Program Activity</h2>
        
        <button type="button" class="close" data-dismiss="modal">&times;</button>
		        
      </div>
      <div class="modal-body">
        
		<form id="${instanceId!}-form" method="post" data-toggle="validator">
		
		<input type="hidden" name="srNumber" value="${srNumber!}" />
		<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}" />
				
		<@dynaScreen 
			instanceId="GEN_PROG_ACT"
			modeOfAction="CREATE"
			isConfigScreen="N"
			/>
			
		<div class="form-group offset-2">
			<div class="text-left ml-3">
		      
		      <@formButton
			     btn1type="button"
			     btn1label="${uiLabelMap.Generate}"
			     btn1id="${instanceId!}-gen-btn"
			     btn2=true
			     btn2type="reset"
			     btn2label="${uiLabelMap.Clear}"
			     btn2id="${instanceId!}-reset-btn"
			   />
				 	
			</div>
		</div>	
				
		</form>
        
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-sm btn-primary navbar-dark" data-dismiss="modal">Close</button>
      </div>
    </div>
  </div>
</div>

<script>
$(document).ready(function() {

$("#${instanceId!}-btn").click(function () {
    $("#${instanceId!}").modal("show");
});  

$("#${instanceId!}-gen-btn").click(function () {
    console.log('click gen btn');
    $('#${instanceId!}-form').submit();
});  


$('#${instanceId!}-form').on('submit', function(e) {
	console.log('submit form');
    if (e.isDefaultPrevented()) {
    	// handle the invalid form...
  	} else {
  		e.preventDefault();
  		$.post("/activity-portal/control/generateProgAct", $('#${instanceId!}-form').serialize(), function(data) {
			if (data.code == 200) {
				showAlert ("success", "Successfully generated program activities");
				$("#${instanceId!}").modal("hide");
				$("#${instanceId!}-reset-btn").click();
			} else {
				showAlert ("error", data.message);
			}
		});
  	}
});  

            
});
</script>
</#macro>
