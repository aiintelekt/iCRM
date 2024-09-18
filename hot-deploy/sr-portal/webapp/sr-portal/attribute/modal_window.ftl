<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
<style>
/*
.modal {
    height: 800px;
    width: 1700px;
    margin-left: 0px;
}
*/
</style>
<#macro createAttrModal instanceId fromAction="">

<div id="${instanceId!}" class="modal fade" >
  <div class="modal-dialog modal-lg" style="max-width: 700px;">
    <!-- Modal content-->
    <div class="modal-content">
      <div class="modal-header">
        <h4 class="modal-title">Add Attribute</h4>
        <button type="button" class="close" data-dismiss="modal">&times;</button>
      </div>
      <div class="modal-body">
        
		<form id="add-attr-form" method="post" data-toggle="validator">

		<input type="hidden" name="activeTab" value="attributes" />
        <input type="hidden" name="srNumber" value="${srNumber?if_exists}">
        <input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
		
		<@dynaScreen 
			instanceId="SR_ATTR"
			modeOfAction="CREATE"
			/>
		
		<div class="form-group offset-2">
		<div class="text-left ml-3">
	      	
	      	<@formButton
		     btn1type="submit"
		     btn1label="${uiLabelMap.Save}"
		     btn2=true
		     btn2id="srAttr-reset-btn"
		     btn2type="reset"
		     btn2label="${uiLabelMap.Clear}"
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

$('#add-attr-form').validator().on('submit', function (e) {
	if (e.isDefaultPrevented()) {
    	// handle the invalid form...
  	} else {
  		e.preventDefault();
  		
  		var action = "createAttribute";
  		/*if ($('#noteId').val()) {
  			action = "updateNoteData";
  		}
  		*/
  		$.post("/sr-portal/control/"+action, $('#add-attr-form').serialize(), function(data) {
			if (data.code == 200) {
				showAlert ("success", data.message);
				$('#${instanceId!}').modal('hide');
				//window.location = "/sr-portal/control/viewServiceRequest?srNumber=${srNumber?if_exists}#attributes";
				location.reload(); 
			} else {
				showAlert ("error", data.message);
			}
		});
  	}
});

$('#${instanceId!}').on('hidden.bs.modal', function (e) {
  	$( "#srAttr-reset-btn" ).trigger( "click" );
}); 

});

</script> 
 
</#macro>
<#macro fsrPartsOnlyModal instanceId fromAction="">

<div id="${instanceId!}" class="modal fade" >
  <div class="modal-dialog modal-lg" style="max-width: 900px;">
    <!-- Modal content-->
    <div class="modal-content">
      <div class="modal-header">
        <h4 class="modal-title">Parts Changes</h4>
        <button type="button" class="close" data-dismiss="modal">&times;</button>
      </div>
      <div class="modal-body">
        
		<div class="mt-1">
				
				<#if partsOnlyAttr?has_content && partsOnlyAttr.attrValue?has_content>
				<#assign lastUpdated = partsOnlyAttr.lastUpdatedStamp!>
				<#assign attrValue = partsOnlyAttr.attrValue!>   
				<div class="col-md-12 col-lg-12 col-sm-12" style="background-color: #02829d;">
					<div style="color: white;"> <span class=""><b> <#if lastUpdated?has_content>${lastUpdated?string("MM/dd/yyyy")?if_exists}</#if></b>  </div>
				</div>
				<div> &nbsp; </div>
				<div class="col-md-12 col-lg-12 col-sm-12">
					<@displayCell
						label=""
						value="${StringUtil.wrapString(attrValue)}"
						id="partsChangeInfo"
						labelColSize=""
						inputColSize="col-sm-12"
						/>
				</div>
				<div> &nbsp; </div>
				<#else>
				<div class="col-md-12 col-lg-12 col-sm-12" style="text-align: center;padding-bottom: 10px;">
					<h1>No Date to Display</h1>
				</div>
				</#if>
			</div>
      </div>
      <div class="modal-footer">
     	<button type="button" class="btn btn-sm btn-primary navbar-dark" data-dismiss="modal">Close</button>
      </div>
    </div>
  </div>
</div>

</#macro>
