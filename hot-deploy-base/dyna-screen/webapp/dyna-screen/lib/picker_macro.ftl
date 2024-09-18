<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>

<#macro labelPicker instanceId isOnScreen=true fromAction="">

<div id="${instanceId!}" class="modal fade mt-2 save-modal" role="dialog">
    <div class="modal-dialog modal-lg">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <h3 class="modal-title">Find UI Labels</h3>
                <button type="reset" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body">
            	<form method="post" id="findUiLabels" class="form-horizontal" data-toggle="validator">
            	<div class="row p-2">
            		<div class="col-md-4 col-sm-4">
			         	<@dropdownCell 
						id="labelComponentName"
						options=componentList
						placeholder=uiLabelMap.module
						required=false
						allowEmpty=true
						/>	
			         </div>
			         
			         <div class="col-md-2 col-sm-2">
			         	<@button
			            id="find-ui-label"
			            label="${uiLabelMap.Find}"
			            />	
			         </div>
			    </div>
            	</form>
            	
                <div class="table-responsive">
	                <div class="loader text-center" id="loader" sytle="display:none;">
	                  <span></span>
	                  <span></span>
	                  <span></span>
	                </div>
	                <div id="ui_label_grid" style="width: 100%;" class="ag-theme-balham"></div>
                  	<script type="text/javascript" src="/dyna-screen-resource/js/ag-grid/create-widget/ui_labels.js"></script> 
           		</div>
                  
            </div>
            <div class="modal-footer">
                <!-- <button type="submit" class="btn btn-sm btn-primary" data-dismiss="modal" id="addParentBu" name="addParentBu" >Add</button>-->
            </div>
        </div>
    </div>
</div>

<script>

$(document).ready(function() {

	$("#find-ui-label").click(function(event) {
	    event.preventDefault(); 
	    getUiLabelRowData();
	    
	});

	$('#uiLabelPicker').on('show.bs.modal', function (e) {
	  	sizeToFitUiLabelGrid();
	});

});

<#if isOnScreen>
function setUiLabelPickerWindowValue(selectedVal) {
	$('#'+currentPickerInputId+'_desc').val(selectedVal);
	$('#'+currentPickerInputId+'_val').val(selectedVal);
	$('#'+currentPickerWindowId).modal('hide');
	$('#'+currentPickerInputId+'_desc').trigger('change');
}
</#if>

</script>

</#macro>
