<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>


<#macro addStaticData instanceId fromAction="">

<div id="${instanceId!}" class="modal fade" >
  <div class="modal-dialog modal-lg">
    <!-- Modal content-->
    <div class="modal-content">
      <div class="modal-header">
        <h2 class="modal-title">${uiLabelMap.listOfFieldData} for [ <span id="field-value-title"></span> ]</h2>
        <button type="button" class="close" data-dismiss="modal">&times;</button>
      </div>
      <div class="modal-body">
        
        <div class="clearfix"></div>
		
		<form id="field-data-form" method="post">
		
		<input type="hidden" name="dynaConfigId" value="${inputContext.dynaConfigId!}"/>
		<input type="hidden" name="selectedDynaFieldId" id="selectedDynaFieldId"/>		 
		
		<div id="dyna-screen-field-datas"></div>
		
		</form>
		
		<div class="page-header border-b pt-2">
	        <div class="float-right">
	        <span id="add-field-data-btn" title="Create" class="btn btn-primary btn-xs ml-2 " ><i class="fa fa-plus" aria-hidden="true"></i> Add </span>
	        <span id="remove-field-data-btn" title="Remove" class="btn btn-primary btn-xs ml-2 " ><i class="fa fa-times" aria-hidden="true"></i> Remove </span>
	        </div>
	        <div class="clearfix"></div>
	    </div>
		
		<div id="dyna-field-data-grid" style="width: 100%;" class="ag-theme-balham"></div>
  		<script type="text/javascript" src="/dyna-screen-resource/js/ag-grid/general/dyna_screen_dd_grid.js"></script>
        
      </div>
      <div class="modal-footer">
        <button id="update-field-data-btn" type="button" class="btn btn-sm btn-primary navbar-dark">Update</button>
      </div>
    </div>
  </div>
</div>
</div>

</#macro>
