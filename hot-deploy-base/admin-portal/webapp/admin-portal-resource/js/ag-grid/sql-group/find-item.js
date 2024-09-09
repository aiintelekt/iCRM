$(function() {
	const sqlGrpItemListInstanceId= 'SQLGRP_ITEM_LIST';
	const externalLoginKey = $("input[name='externalLoginKey']").val();
	const formDataObject = {};
	const userId = $("input[name='userId']").val();
	let gridInstance  = "";
	let sqlGrpItemListUrl = "";

	formDataObject.gridInstanceId = sqlGrpItemListInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;
	formDataObject.dataFetchCall = getSqlGrouptItemListRowData;

	gridInstance = prepareGridInstance(formDataObject);

	if(sqlGrpItemListUrl == ""){
		sqlGrpItemListUrl = getGridDataFetchUrl("SQLGRP_ITEM_LIST");
	}

	if(sqlGrpItemListUrl == "" || sqlGrpItemListUrl == null){
		sqlGrpItemListUrl = "/admin-portal/control/searchSqlGroupItems"
	}
	
	if(gridInstance){
		getSqlGrouptItemListRowData();
	}

	function getSqlGrouptItemListRowData() {
		const callCtx = {};
		callCtx.ajaxUrl = sqlGrpItemListUrl;
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#item-search-form";
		callCtx.ajaxResponseKey = "list";

		setGridData(gridInstance, callCtx);
	}

	$('#sql-group-item-list-save-pref').click(function(){
		saveGridPreference(gridInstance, sqlGrpItemListInstanceId, userId);
	});

	$('#sql-group-item-list-clear-pref').click(function(){
		clearGridPreference(gridInstance, sqlGrpItemListInstanceId, userId);
		if (gridInstance) {
			gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getSqlGrouptItemListRowData();
		}
	});
	$("#sql-group-item-list-export-btn").click(function() {
		gridInstance.exportDataAsCsv();
	});
	$('#sql-group-item-list-clear-sub-ftr').click(function(){
		gridInstance.setFilterModel(null);
	});

	$("#item-refresh-btn").click(function () {
		getSqlGrouptItemListRowData();
	});

	$("#item-search-form").on("keypress", function (event) {
		let keyPressed = event.keyCode || event.which;
		if(keyPressed === 13) {
			event.preventDefault();
			getSqlGrouptItemListRowData();
			return false;
		}
	});
	
	$("#items-remove-btn").click(function () {
    	var flag = true;
    	if(flag){
    		var selectedData = gridInstance.getSelectedRows();
    		if (selectedData.length > 0) {
    			
    			console.log(selectedData);
    			
    		    var selectedItemIds = "";
    		    for (i = 0; i < selectedData.length; i++) {
    		    	var data = selectedData[i];
    		    	selectedItemIds += data.itemId+",";
    		    }
    		    selectedItemIds = selectedItemIds.substring(0, selectedItemIds.length - 1);
    		    
    		    var inputData = {"selectedItemIds": selectedItemIds};
    		    
    		    $.ajax({
    				type : "POST",
    				url : "/admin-portal/control/removeSqlGrpItem",
    				async : true,
    				data : inputData,
    				success : function(result) {
    					if (result.code == 200) {
    						showAlert ("success", "Successfully removed sql group item# "+selectedItemIds);
    						getSqlGrouptItemListRowData();
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
    			
    		} else {
    			showAlert("error", "Please select atleast one row to be removed!");
    		}
    	}
    });
    $('#add-item-form').on('submit', function (e) {
    	if (!$('#path').val() || !$('#sequenceNum').val()) {
  			console.log('required fields missing...');
  			showAlert ("error", 'required fields missing');
  		}
    	if (!e.isDefaultPrevented()) {
      		e.preventDefault();
      		
      		if (!$('#path').val() || !$('#sequenceNum').val()) {
      			console.log('required fields missing...');
      			showAlert ("error", 'required fields missing');
      		} else {
      			var action = "createSqlGrpItem";
          		if ($('#add-item-form input[name=itemId]').val()) {
          			action = "updateSqlGrpItem";
          		}
          		
          		$.post("/admin-portal/control/"+action, $('#add-item-form').serialize(), function(data) {
        			if (data.code == 200) {
        				showAlert ("success", data.message);
        				$("#create-item-modal").modal('hide');
        				getSqlGrouptItemListRowData();
        			} else {
        				showAlert ("error", data.message);
        			}
        				
        		});
      		}
      	}
    });
    
    $("#execute-item-btn").click(function () {
    	var flag = true;
    	if(flag){
    		var selectedData = gridInstance.getSelectedRows();
    		if (selectedData.length > 0) {
    			
    			console.log(selectedData);
    			
    		    var selectedItemIds = "";
    		    for (i = 0; i < selectedData.length; i++) {
    		    	var data = selectedData[i];
    		    	selectedItemIds += data.itemId+",";
    		    }
    		    selectedItemIds = selectedItemIds.substring(0, selectedItemIds.length - 1);
    		    
    		    var inputData = {"selectedItemIds": selectedItemIds};
    		    
    		    $.ajax({
    				type : "POST",
    				url : "/admin-portal/control/executeSqlGrpItemSelected",
    				async : true,
    				data : inputData,
    				success : function(result) {
    					if (result.code == 200) {
    						showAlert ("success", "Successfully executed sql group item# "+selectedItemIds);
    						getSqlGrouptItemListRowData();
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
    			
    		} else {
    			showAlert("error", "Please select atleast one row to be executed!");
    		}
    	}
    });
    
});

function editSqlGrpItemAction(params) {
	if (params.data) {
		return '<span class="fa fa-edit btn btn-xs btn-primary edit-item" title="Edit" onclick="editSqlGrpItem(\'' + params.data.sqlGroupId + '\', \'' + params.data.itemId + '\')"></span>';
	}
}

function executeSqlGrpItemAction(params) {
	if (params.data) {
		return '<span class="fa fa-flash btn btn-xs btn-primary edit-item" title="Execute" onclick="executeSqlGrpItem(\'' + params.data.sqlGroupId + '\', \'' + params.data.itemId + '\')"></span>';
	}
}
