/*fagReady("ECONOMIC_METRICS", function(el, api, colApi, gridApi){
    $("#economic-metric-refresh-pref-btn").click(function () {
    	gridApi.refreshUserPreferences();
    });
    $("#economic-metric-save-pref-btn").click(function () {
    	gridApi.saveUserPreferences();
    });
    $("#economic-metric-clear-filter-btn").click(function () {
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
    $("#economic-metric-export-btn").click(function () {
    	gridApi.csvExport();
    });

    $("#refresh-economic-metric-btn").click(function () {
    	loadEconomicMetricGrid(gridApi);
    });
    
    $("#find-economic-metric").click(function(event) {
    	loadEconomicMetricGrid(gridApi);
    });
    
    $("#economic-metric-remove-btn").click(function () {
    	
		var selectedData = api.getSelectedRows();
		console.log(selectedData);
		if (selectedData.length > 0) {
			console.log(selectedData);
		    var groupIds = "";
		    var customFieldIds = "";
		    var partyIds = "";
		    
		    var domainEntityId = "";
		    var domainEntityType = "";
		    
		    for (i = 0; i < selectedData.length; i++) {
		    	var data = selectedData[i];
		    	groupIds += data.groupId+",";
		    	customFieldIds += data.customFieldId+",";
		    	partyIds += data.partyId+",";
		    	
		    	domainEntityId = data.domainEntityId;
		    	domainEntityType = data.domainEntityType;
		    }
		    groupIds = groupIds.substring(0, groupIds.length - 1);
		    customFieldIds = customFieldIds.substring(0, customFieldIds.length - 1);
		    partyIds = partyIds.substring(0, partyIds.length - 1);
		    
		    var inputData = {"groupIds": groupIds, "customFieldIds": customFieldIds, "partyIds": partyIds, "domainEntityId": domainEntityId, "domainEntityType": domainEntityType};
		    
		    $.ajax({
				type : "POST",
				url : "/common-portal/control/removeEconomicMetric",
				async : true,
				data : inputData,
				success : function(result) {
					if (result.code == 200) {
						showAlert ("success", "Successfully removed selected EconomicMetric");
						loadEconomicMetricGrid(gridApi);
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
		
    });
    
    $("#add-economicmetric").click(function(event) {
    	var inputData = {};
    	var paramStr = $("#economic-metric-add-form").serialize();
    	inputData = JSON.parse(JSON.stringify(paramStr));

    	$.ajax({
    		type: "POST",
         	url: "/common-portal/control/addEconomicMetric",
            data:  inputData,
            async: false,
            success: function (data) {   
                if (data.code == 200) {
                	showAlert("success", "Successfully added");
                	loadEconomicMetricGrid(gridApi);
                } else {
                	showAlert("error", data.message);
                }
                
            }
            
    	}); 
    });
    
    postLoadGridData(null, gridApi, "economicMetric", loadEconomicMetricGrid);
    
    //loadEconomicMetricGrid(gridApi);
});

function loadEconomicMetricGrid(gridApi) {
	var rowData =[];
	gridApi.setRowData(rowData);
	$.ajax({
	  async: false,
	  url:'/common-portal/control/searchEconomicMetrics',
	  type:"POST",
	  data: JSON.parse(JSON.stringify($("#economic-metric-search-form").serialize())),
	  success: function(result){
		  gridApi.setRowData(result.data);
	  }
	});
}*/



$(function() {
	let economicsMetricInstanceId= "ECONOMIC_METRICS";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();

	const formDataObject = {};
	formDataObject.gridInstanceId = economicsMetricInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;	

	gridInstance = prepareGridInstance(formDataObject);


	$("#refresh-economic-metric-btn").click(function () {
		getEMGridData();
	});

	$("#find-economic-metric").click(function(event) {
		getEMGridData();
	});

	$('#economic-metric-save-pref-btn').click(function(){
		saveGridPreference(gridInstance, economicsMetricInstanceId, userId);
	});
	$('#economic-metric-clear-filter-btn').click(function(){
		clearGridPreference(gridInstance, economicsMetricInstanceId, userId);
		if (gridInstance) {
			gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getEMGridData();
		}
	});
	$('#economic-metric-sub-filter-btn').click(function(){
		gridInstance.setFilterModel(null);
	});
	$("#economic-metric-export-btn").click(function () {
		gridInstance.exportDataAsCsv();
	});
	$("#economic-metric-remove-btn").click(function () {

		var selectedData = gridInstance.getSelectedRows();
		console.log(selectedData);
		if (selectedData.length > 0) {
			console.log(selectedData);
			var groupIds = "";
			var customFieldIds = "";
			var partyIds = "";

			var domainEntityId = "";
			var domainEntityType = "";

			for (i = 0; i < selectedData.length; i++) {
				var data = selectedData[i];
				groupIds += data.groupId+",";
				customFieldIds += data.customFieldId+",";
				partyIds += data.partyId+",";

				domainEntityId = data.domainEntityId;
				domainEntityType = data.domainEntityType;
			}
			groupIds = groupIds.substring(0, groupIds.length - 1);
			customFieldIds = customFieldIds.substring(0, customFieldIds.length - 1);
			partyIds = partyIds.substring(0, partyIds.length - 1);

			var inputData = {"groupIds": groupIds, "customFieldIds": customFieldIds, "partyIds": partyIds, "domainEntityId": domainEntityId, "domainEntityType": domainEntityType};

			$.ajax({
				type : "POST",
				url : "/common-portal/control/removeEconomicMetric",
				async : true,
				data : inputData,
				success : function(result) {
					if (result.code == 200) {
						showAlert ("success", "Successfully removed selected EconomicMetric");
						getEMGridData();
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

	});
	$("#add-economicmetric").click(function(event) {
		var inputData = {};
		var paramStr = $("#economic-metric-add-form").serialize();
		inputData = JSON.parse(JSON.stringify(paramStr));

		$.ajax({
			type: "POST",
			url: "/common-portal/control/addEconomicMetric",
			data:  inputData,
			async: false,
			success: function (data) {   
				if (data.code == 200) {
					showAlert("success", "Successfully added");
					getEMGridData();
				} else {
					showAlert("error", data.message);
				}

			}

		}); 
	});

	function getEMGridData(){
		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = "/common-portal/control/searchEconomicMetrics";
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#economic-metric-search-form";
		callCtx.ajaxResponseKey = "data";

		setGridData(gridInstance, callCtx);
	}
	if(gridInstance){
		getEMGridData();
	}
});
