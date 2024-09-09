<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
<#include "component://dyna-screen/webapp/dyna-screen/lib/picker_macro.ftl"/>
<#include "component://dyna-screen/webapp/dyna-screen/general/modal_window.ftl"/>

<div class="row">
    <div id="main" role="main">
     <div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
        <#assign extra='
        <span id="add-dd-value-btn" title="Drop down value" class="btn btn-primary btn-xs ml-2 " ><i class="fa fa-plus" aria-hidden="true"></i> DD Value </span>
        <span id="remove-field-btn" data-toggle="confirmation" title="Are you sure to REMOVE ?" class="btn btn-primary btn-xs ml-2 " ><i class="fa fa-times" aria-hidden="true"></i> Remove </span>
        <a href="/dyna-screen/control/updateDynaScreen?dynaConfigId=${dynaConfigId!}" class="btn btn-xs btn-primary">
        <i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>
        ' />
        <@sectionFrameHeader title="Field Detail Configuration# ${dynaFieldId!}" extra=extra />
        <div class="clearfix"></div>
        <form id="mainForm" method="post" data-toggle="validator">    
        
           
            	
            	<@dynaScreen 
					instanceId="DFG_DETAIL"
					modeOfAction="UPDATE"
					/>
            	
            </div>
            
            <div class="clearfix"></div>
            <div class="offset-md-2 col-sm-10 p-2">
         	
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
<@labelPicker 
	instanceId="uiLabelPicker"
	/>
	
<@addStaticData 
	instanceId="data-modal-view"
	/> 	

<script>

$(document).ready(function() {

$('#mainForm').validator().on('submit', function (e) {
	if (e.isDefaultPrevented()) {
    	// handle the invalid form...
		
  	} else {
  		e.preventDefault();
  		
  		var errorMessage = validateFieldDetailForm();
  		if (errorMessage) {
  			showAlert ("error", errorMessage);
  		} else {
	  		$.post("/dyna-screen/control/fieldDetailConfigUpdateAction", $('#mainFrom').serialize(), function(data) {
				
				if (data.code == 200) {
					showAlert ("success", "Successfully updated");
				} else {
					showAlert ("error", data.message);
				}
					
			});
		}
  	}
});

$('#add-dd-value-btn').on('click', function() {

	var fieldName = $('#fieldName_val').val();
	var dynaFieldId = $('#dynaFieldId').val();

	var fieldType = $('#fieldType').val();
	var lookupTypeId = $('#lookupTypeId').val();

	if (fieldType != "DROPDOWN" && fieldType != "RADIO") {
		showAlert ("error", "Field type should be Drop Down or Radio");
	} else if (lookupTypeId != "STATIC_DATA") {
		showAlert ("error", "Lookup type should be STATIC DATA");
	} else {
		$('#data-modal-view').modal("show");
		    	
    	$('#field-value-title').html( fieldName );
    	$('#selectedDynaFieldId').val( dynaFieldId );
    	
    	loadFieldDataGrid();
	}
    
});

$('#remove-field-btn').on('click', function(e) {
	e.preventDefault();
	
	var inputData = {"dynaConfigId": "${dynaConfigId!}", "dynaFieldId": "${dynaFieldId!}"};
		    
    $.ajax({
		type : "POST",
		url : "/dyna-screen/control/fieldRemoveAction",
		async : true,
		data : inputData,
		success : function(result) {
			if (result.code == 200) {
				showAlert ("success", "Successfully removed field# ${dynaFieldId!}");
				window.location="/dyna-screen/control/updateDynaScreen?dynaConfigId=${dynaConfigId!}";
			} else {
				showAlert ("error", data.message);
			}
		},
		error : function() {
			console.log('Error occured');
			showAlert("error", "Error occured!");
		},
		complete : function() {
		}
	});
		
});

});

function validateFieldDetailForm() {

	var fieldName = $('#fieldName_val').val();
	var dynaFieldId = $('#dynaFieldId').val();

	var fieldType = $('#fieldType').val();
	var lookupTypeId = $('#lookupTypeId').val();
	var lookupFieldService = $('#lookupFieldService').val();
	var lookupFieldFilter = $('#lookupFieldFilter').val();
	var pickerWindowId = $('#pickerWindowId').val();

	var isValid = true;
	var errorMessage = "";
	
	if ((fieldType=='DROPDOWN' || fieldType=='RADIO') && lookupTypeId == '') {
		isValid = false;
		errorMessage += 'Lookup type empty# '+fieldName+"</br>";
	}
	if (fieldType=='DROPDOWN' && lookupTypeId == 'DYNAMIC_DATA' && (lookupFieldService=='' || lookupFieldFilter=='')) {
		isValid = false;
		errorMessage += 'Dropdown as dynamic data, Lookup field service & lookup field filter cant empty# '+fieldName;
	}
	if (fieldType=='PICKER' && pickerWindowId == '') {
		isValid = false;
		errorMessage += 'Picker window ID empty# '+fieldName;
	}
	
	return errorMessage;
}

</script>