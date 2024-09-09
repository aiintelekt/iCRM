<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/lib/picker_macro.ftl"/>
	
<div class="row">
<div id="main" role="main">
<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
<@radioInputCell
id="previewMode"
name="previewMode"
options=previewModes
inputColSize="col-sm-12"
value="CREATE"
/>
<#assign extra='<span class="btn btn-xs btn-primary"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</span><span class="btn btn-xs btn-primary"><i class="fa fa-edit" aria-hidden="true"></i> Update</span>' />
<@sectionFrameHeaderTab title="${uiLabelMap.preview!}: <h4 style='padding-top:5px'>${inputContext.screenDisplayName!} [${inputContext.dynaConfigId!}]</h4>" tabId="previewDynaConfig" extra=extra/> 

<form id="mainFrom" method="post" data-toggle="validator">

<div id="preview-create" style="visibility: visible">		 		 		 		 
<@dynaScreen 
	instanceId=inputContext.dynaConfigId!
	modeOfAction="CREATE"
	isConfigScreen="Y"
	/>
	
<div class="form-group offset-2">
	<div class="text-left ml-3">
      
      <@formButton
	     btn1type="submit"
	     btn1label="${uiLabelMap.Save}"
	     btn1onclick="return formSubmission();"
	     btn2=true
	     btn2type="reset"
	     btn2label="${uiLabelMap.Clear}"
	   />
		 	
	</div>
</div>	
</div>

<div id="preview-view" style="visibility: visible">		 		 		 		 
<@dynaScreen 
	instanceId=inputContext.dynaConfigId!
	modeOfAction="VIEW"
	isConfigScreen="Y"
	/>
</div>

<div id="preview-update" style="visibility: visible">		 		 		 		 
<@dynaScreen 
	instanceId=inputContext.dynaConfigId!
	modeOfAction="UPDATE"
	isConfigScreen="Y"
	/>
	
<div class="form-group offset-2">
	<div class="text-left ml-3">
      
      <@formButton
	     btn1type="submit"
	     btn1label="${uiLabelMap.Save}"
	     btn1onclick="return formSubmission();"
	     btn2=true
	     btn2type="reset"
	     btn2label="${uiLabelMap.Clear}"
	   />
		 	
	</div>
</div>	
</div>

</form>
		  
</div>
</div>

<@partyPicker 
	instanceId="partyPicker"
	/> 

<div id="parentbu" class="modal fade mt-2 save-modal" role="dialog">
    <div class="modal-dialog modal-lg">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <h3 class="modal-title">Find Parent BU</h3>
                <button type="reset" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body">
                <div class="table-responsive">
                <div class="loader text-center" id="loader" sytle="display:none;">
                  <span></span>
                  <span></span>
                  <span></span>
                </div>
                <div id="grid1" style="width: 100%;" class="ag-theme-balham"></div>
                  
                </div>
                  <script type="text/javascript" src="/admin-portal-resource/js/ag-grid/org-Hierarchy/bu-parent.js"></script> 
            </div>
            <div class="modal-footer">
                <!-- <button type="submit" class="btn btn-sm btn-primary" data-dismiss="modal" id="addParentBu" name="addParentBu" >Add</button>-->
            </div>
        </div>
    </div>
</div>
</div>
<script>

jQuery(document).ready(function() {	

$('#preview-create').show(); $('#preview-view').hide(); $('#preview-update').hide();

$("input[name=previewMode]").change(function() {
	var previewMode = $(this).val();

	if (previewMode=="CREATE") {
		$('#preview-create').show(); $('#preview-view').hide(); $('#preview-update').hide();
	} else if (previewMode=="VIEW") {
		$('#preview-create').hide(); $('#preview-view').show(); $('#preview-update').hide();
	} else if (previewMode=="UPDATE") {
		$('#preview-create').hide(); $('#preview-view').hide(); $('#preview-update').show();
	}
		
});

});	

</script>
   