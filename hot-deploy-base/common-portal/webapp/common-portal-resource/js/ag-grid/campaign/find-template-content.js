fagReady("TPL_CONT_LIST", function(el, api, colApi, gridApi){
    $("#templateContent-refresh-pref-btn").click(function () {
    	gridApi.refreshUserPreferences();
    });
    $("#templateContent-save-pref-btn").click(function () {
    	gridApi.saveUserPreferences();
    });
    $("#templateContent-clear-filter-btn").click(function () {
    	try{
    		gridApi.clearAllColumnFilters();
    	}catch(e){
    	}
    	gridApi.refreshUserPreferences();
    });
	$("#sub-filter-clear-btn").click(function () {
    	try{
    		gridApi.clearAllColumnFilters();
    	}catch(e){
    	}
    });
    $("#templateContent-export-btn").click(function () {
    	gridApi.csvExport();
    });
    
    $("#templateContent-search-btn").click(function () {
    	loadTemplateContentGrid(gridApi);
    });
    
    $("#templateContent-refresh-btn").click(function () {
    	loadTemplateContentGrid(gridApi);
    });
    
    $("#templateContent-detail-btn").click(function () {
		var selectedData = api.getSelectedRows();
		if (selectedData.length > 0) {
			
			console.log(selectedData);
			approval = selectedData[0];
			
			$("#apvdtail-search-form input[name=parentApprovalId]").val(approval.approvalId);
			$("#apvdtail-refresh-btn").trigger('click');
			$("#detail-approval-modal").modal('show');
			
		} else {
			showAlert("error", "Please select approval!");
		}
    });
    
    $("#contentTemplateId").change(function () {
    	console.log('change templateId: '+$(this).val());
    	loadTemplateContentGrid(gridApi);
    });
    
    $('#templateContent-add-form').on('submit', function (e) {
    	if (e.isDefaultPrevented()) {
        	// handle the invalid form...
      	} else {
      		e.preventDefault();
      		var action = "createTemplateContent";
      		if ($('#templateContentId').val()) {
      			action = "updateTemplateContent";
      		}
      		$.post("/common-portal/control/"+action, $('#templateContent-add-form').serialize(), function(data) {
    			if (data.code == 200) {
    				showAlert ("success", data.message);
    				$("#add-templateContent-modal").modal('hide');
    				loadTemplateContentGrid(gridApi);
    			} else {
    				showAlert ("error", data.message);
    			}
    		});
      	}
    });
    
    $("#templateContent-remove-btn").click(function () {
		var selectedData = api.getSelectedRows();
		if (selectedData.length > 0) {
			console.log(selectedData);
		    var selectedItemIds = "";
		    for (i = 0; i < selectedData.length; i++) {
		    	var data = selectedData[i];
		    	selectedItemIds += data.templateContentId+",";
		    }
		    
		    if (selectedItemIds && selectedData.length > 0){
		    	
		    selectedItemIds = selectedItemIds.substring(0, selectedItemIds.length - 1);
		    //alert(selectedDynaConfigIds);
		    
		    var inputData = {"selectedItemIds": selectedItemIds};
		    
		    $.ajax({
				type : "POST",
				url : "/common-portal/control/removeTemplateContent",
				async : true,
				data : inputData,
				success : function(result) {
					if (result.code == 200) {
						showAlert ("success", "Successfully removed template content# "+selectedItemIds);
						loadTemplateContentGrid(gridApi);
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
		}
			
		} else {
			showAlert("error", "Please select atleast one row to be removed!");
		}
    });
    
    $("#templateContent-preview-btn").click(function () {
    	
    });
    
    postLoadGridData(null, gridApi, "templateContent", loadTemplateContentGrid);
});

function loadTemplateContentGrid(gridApi) {
	var rowData =[];
	gridApi.setRowData(rowData);
	$.ajax({
	  async: false,
	  url:'/common-portal/control/searchTemplateContents',
	  type:"POST",
	  data: JSON.parse(JSON.stringify($("#templateContent-search-form").serialize())),
	  success: function(data){
		  gridApi.setRowData(data.list);
	  }
	});
}