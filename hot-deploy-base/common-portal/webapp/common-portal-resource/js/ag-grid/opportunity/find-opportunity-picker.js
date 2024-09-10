//fagReady("OPPORTUNITY_PICKER", function(el, api, colApi, gridApi){
//    $("#oppo-refresh-pref-btn").click(function () {
//    	gridApi.refreshUserPreferences();
//		
//		loadOpportunityGrid(gridApi, api, colApi);
//    });
//    $("#oppo-save-pref-btn").click(function () {
//    	gridApi.saveUserPreferences();
//    });
//    $("#oppo-clear-filter-btn").click(function () {
//    	try{
//    		gridApi.clearAllColumnFilters();
//    	}catch(e){
//    	}
//    	gridApi.refreshUserPreferences();
//    });
//	$("#sub-filter-clear-btn").click(function () {
//    	try{
//    		gridApi.clearAllColumnFilters();
//    	}catch(e){
//    	}
//    });
//    $("#oppo-export-btn").click(function () {
//    	gridApi.csvExport();
//    });
//    $("#oppo-refresh-btn").click(function () {
//    	loadOpportunityGrid(gridApi, api, colApi);
//    });
//    $("#find-oppo-search-btn").click(function () {
//    	loadOpportunityGrid(gridApi, api, colApi);
//    });
//    
//    $("#oppo-assoc-assoc-btn").click(function(){
//    	var selectedData = api.getSelectedRows();
//    	salesOpportunityTypeId = $("#opportunityPicker_parentOpportunityTypeId").val();
//    	if (salesOpportunityTypeId && salesOpportunityTypeId !="BASE"){
//    		if (selectedData.length > 1) {
//    			showAlert("error", "Cannot associate multiple base opportunity!");
//        		return false;
//    		}
//    	}
//    	if (selectedData.length >= 1) {
//    		var opportunityId = "";
//		    for (i = 0; i < selectedData.length; i++) {
//		    	var data = selectedData[i];
//		    	opportunityId += data.salesOpportunityId+",";
//		    }
//		    opportunityId = opportunityId.substring(0, opportunityId.length - 1);
//		    let pickerWindow = $("#opportunityPicker_pickerWindow").val();
//		    if (pickerWindow && pickerWindow == "Y"){
//		    	 setPickerWindowValue(opportunityId, opportunityId);
//		    	 return false;
//		    }
//		   
//		    salesOpportunityId = $("#notIncludeOpportunityId").val();
//		    let input= {};
//		    input ={"salesOpportunityId":salesOpportunityId,"salesOpportunityIdTo":opportunityId,"salesOpportunityTypeId":salesOpportunityTypeId};
//		    $.ajax({
//		  	  async: false,
//		  	  url:'/opportunity-portal/control/createOpportunityAssocAjax',
//		  	  type:"POST",
//		  	  data: input,
//		  	  success: function(data){
//		  		  if (data && data["responseMessage"]){
//		  			  let msg = data["responseMessage"];
//		  			  let errMsg = data["errorMessage"];
//		  			  
//		  			  if (msg =="success"){
//		  				 showAlert("success","Association created successfully");
//		  			//	 $("#oppo-assoc-refresh-btn").trigger("click");
//		  				 $(".close[data-dismiss=modal]").click();
//		  				 location.reload();
//		  			  }else{
//		  				if (errMsg){
//		  					showAlert("error",errMsg);
//			  			 }else{
//			  				showAlert("error","unbale to  add association");
//			  			 }
//		  				 $("#oppo-assoc-refresh-btn").trigger("click");
//		  				 $(".close[data-dismiss=modal]").click();
//		  			  }
//		  			 
//		  		  }
//		  	  }
//		  	});
//		    
//    	}else{
//    		showAlert("error", "Please select opportunity  to be associate!");
//    		return false;
//    	}
//    });
// //  loadOpportunityGrid(gridApi, api, colApi);
//});
//
//function loadOpportunityGrid(gridApi, api, colApi) {
//	var rowData =[];
//	gridApi.setRowData(rowData);
//	oppoType = $("#opportunityPicker_parentOpportunityTypeId").val();
//	oppoPartyId = $("#opportunityPicker_partyId").val();
//	if (!oppoType){
//		showAlert("error","Opportunity Type Missing");
//		return false;
//	}
//	if (!oppoPartyId){
//		showAlert("error","Opportunity Party Id Missing");
//		return false;
//	}
//	if (oppoType && oppoPartyId) {
//		api.showLoadingOverlay();
//		$.ajax({
//		  async: false,
//		  url:'/opportunity-portal/control/searchOpportunitys',
//		  type:"POST",
//		  data: JSON.parse(JSON.stringify($("#opportunityPicker_Form").serialize())),
//		  success: function(data){
//			  gridApi.setRowData(data.list);
//			  setTimeout(() => colApi.autoSizeAllColumns(), 500);
//		  }
//		});
//	}
//}



$(function() {
	let oppPickerInstanceId= "OPPORTUNITY_PICKER";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();
	
	const formDataObject = {};
	formDataObject.gridInstanceId = oppPickerInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;	
	
	gridInstance = prepareGridInstance(formDataObject);
	
	$('#oppo-list-save-pref-btn').click(function(){
		saveGridPreference(gridInstance, oppPickerInstanceId, userId);
	});
	$('#oppo-list-clear-filter-btn').click(function(){
		clearGridPreference(gridInstance, oppPickerInstanceId, userId);
		if (gridInstance) {
		    gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getOppoGridData();
		}
	});
	$('#oppo-assoc-clear-sub-ftr').click(function(){
		gridInstance.setFilterModel(null);
	});
    $("#oppo-refresh-btn").click(function () {
    	getOppoGridData();
    });
    $("#find-oppo-search-btn").click(function () {
    	oppoType = $("#opportunityPicker_parentOpportunityTypeId").val();
    	oppoPartyId = $("#opportunityPicker_partyId").val();
    	oppoCheck(oppoType,oppoPartyId);
    	getOppoGridData();
    });
    
    $("#oppo-assoc-assoc-btn").click(function(){
    	var selectedData = gridInstance.getSelectedRows();
    	salesOpportunityTypeId = $("#opportunityPicker_parentOpportunityTypeId").val();
    	if (salesOpportunityTypeId && salesOpportunityTypeId !="BASE"){
    		if (selectedData.length > 1) {
    			showAlert("error", "Cannot associate multiple base opportunity!");
        		return false;
    		}
    	}
    	if (selectedData.length >= 1) {
    		var opportunityId = "";
		    for (i = 0; i < selectedData.length; i++) {
		    	var data = selectedData[i];
		    	opportunityId += data.salesOpportunityId+",";
		    }
		    opportunityId = opportunityId.substring(0, opportunityId.length - 1);
		    let pickerWindow = $("#opportunityPicker_pickerWindow").val();
		    if (pickerWindow && pickerWindow == "Y"){
		    	 setPickerWindowValue(opportunityId, opportunityId);
		    	 return false;
		    }
		   
		    salesOpportunityId = $("#notIncludeOpportunityId").val();
		    let input= {};
		    input ={"salesOpportunityId":salesOpportunityId,"salesOpportunityIdTo":opportunityId,"salesOpportunityTypeId":salesOpportunityTypeId};
		    $.ajax({
		  	  async: false,
		  	  url:'/opportunity-portal/control/createOpportunityAssocAjax',
		  	  type:"POST",
		  	  data: input,
		  	  success: function(data){
		  		  if (data && data["responseMessage"]){
		  			  let msg = data["responseMessage"];
		  			  let errMsg = data["errorMessage"];
		  			  
		  			  if (msg =="success"){
		  				 showAlert("success","Association created successfully");
		  			//	 $("#oppo-assoc-refresh-btn").trigger("click");
		  				 $(".close[data-dismiss=modal]").click();
		  				 location.reload();
		  			  }else{
		  				if (errMsg){
		  					showAlert("error",errMsg);
			  			 }else{
			  				showAlert("error","unbale to  add association");
			  			 }
		  				 $("#oppo-assoc-refresh-btn").trigger("click");
		  				 $(".close[data-dismiss=modal]").click();
		  			  }
		  			 
		  		  }
		  	  }
		  	});
		    
    	}else{
    		showAlert("error", "Please select opportunity  to be associate!");
    		return false;
    	}
    });
    
	function getOppoGridData(){
		
		oppoType = $("#opportunityPicker_parentOpportunityTypeId").val();
		oppoPartyId = $("#opportunityPicker_partyId").val();
		var salesOpportunityTypeId = $("#salesOpportunityTypeId").val();
		if(salesOpportunityTypeId !="" && salesOpportunityTypeId != undefined){
			oppoCheck(oppoType,oppoPartyId);
		}
		if (oppoType && oppoPartyId) {

		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = "/opportunity-portal/control/searchOpportunitys";
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#opportunityPicker_Form";
		callCtx.ajaxResponseKey = "list";
		
		setGridData(gridInstance, callCtx);
		}
	}
	if(gridInstance){
		getOppoGridData();
	}
	function oppoCheck(oppoType,oppoPartyId){
		if (!oppoType ){
    		showAlert("error","Opportunity Type Missing");
    		return false;
    	}
    	if (!oppoPartyId ){
    		showAlert("error","Opportunity Party Id Missing");
    		return false;
    	}
	}
});
