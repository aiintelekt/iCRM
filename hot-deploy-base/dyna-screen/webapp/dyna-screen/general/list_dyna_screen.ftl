<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<div class="row" style="width:100%">
	
  	<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
	
	<div class="">
        <@headerH2 title="${uiLabelMap.listOfDynaScreens!}" class="float-left"/>
        <div class="float-right">
        
        <button id="remove-screen-btn" data-toggle="confirmation" title="Are you sure to REMOVE ?" class="btn btn-primary btn-xs ml-2 " >
        	<i class="fa fa-times" aria-hidden="true"></i> Remove 
        </button>
        <span id="export-screen-btn" title="Export" class="btn btn-primary btn-xs ml-2 " ><i class="fa fa-edit" aria-hidden="true"></i> Export </span>
        
        </div>
        <div class="clearfix"></div>
    </div>    	
	  	  	  	
  	<div id="dyna-screen-grid" style="width: 100%;" class="ag-theme-balham"></div>
  	<script type="text/javascript" src="/dyna-screen-resource/js/ag-grid/general/dyna_screen_grid.js"></script>
           
  	</div>
</div>
	
<script>     
$(document).ready(function() {

$("#export-screen-btn").click(function(event) {
    
    var selectedData = gridOptionsDynaScreen.api.getSelectedRows();
	if (selectedData.length > 0) {
		console.log(selectedData);
	    var selectedDynaConfigIds = "";
	    for (i = 0; i < selectedData.length; i++) {
	    	var data = selectedData[i];
	    	selectedDynaConfigIds += data.dynaConfigId+",";
	    }
	    selectedDynaConfigIds = selectedDynaConfigIds.substring(0, selectedDynaConfigIds.length - 1);
	    
	    $("#find-dyna-screen-form input[name='dynaConfigIds']").val(selectedDynaConfigIds);
	}
    
    $("#find-dyna-screen-form").attr("action", "exportDynaConfiguration");
    
    $("#find-dyna-screen-form").submit();
    
});

});
</script>