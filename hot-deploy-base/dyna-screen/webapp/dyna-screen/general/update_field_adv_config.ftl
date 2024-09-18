<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>

<div class="row">
    <div id="main" role="main">
    <div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
        <#assign extra='<a href="/dyna-screen/control/updateDynaScreen?dynaConfigId=${dynaConfigId!}" class="btn btn-xs btn-primary">
        <i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>' />
        <@sectionFrameHeader title="Field Advance Configuration# ${dynaFieldId!}" extra=extra />
        <div class="clearfix"></div>
        <form id="mainForm" method="post" data-toggle="validator">    
        
            
            	
            	<@dynaScreen 
					instanceId="${dynaInstanceId!}"
					modeOfAction="UPDATE"
					/>
            	
            
            
            <div class="clearfix"></div>
            <div class="offset-md-2 col-sm-10">
         	
            	<@formButton
                     btn1type="submit"
                     btn1label="${uiLabelMap.Save}"
                     btn2=false
                     btn2onclick = "resetForm()"
                     btn2type="reset"
                     btn2label="${uiLabelMap.Clear}"
                   />
            </div>
        </form>
    </div>
</div>
</div>
<script>

$(document).ready(function() {

$('#mainForm').validator().on('submit', function (e) {
	if (e.isDefaultPrevented()) {
    	// handle the invalid form...
		
  	} else {
  		e.preventDefault();
  		
  		$.post("/dyna-screen/control/fieldAdvConfigUpdateAction", $('#mainForm').serialize(), function(data) {
			
			if (data.code == 200) {
				showAlert ("success", "Successfully updated");
			} else {
				showAlert ("error", data.message);
			}
				
		});
  	}
});

});

</script>