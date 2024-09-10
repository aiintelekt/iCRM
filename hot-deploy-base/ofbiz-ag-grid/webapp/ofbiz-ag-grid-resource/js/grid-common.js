function resetGridStatusBar(){
	$("#totalRecordCount").html(0);
	$("#timeTaken").html(0);
	$("#chunkSize").html(0);
	$("#chunkCount").html(0);
}

function paginateHandler(data){
	var viewSize = $("#limitForm input[name=VIEW_SIZE]").val();
	var viewIndex = $("#limitForm input[name=VIEW_INDEX]").val();
      
	if(viewIndex == 0){
		$("#fetch-previous").css("pointer-events", "none");
		$("#fetch-first").css("pointer-events", "none");
	}
	else{
		$("#fetch-previous").css("pointer-events", "auto");
		$("#fetch-first").css("pointer-events", "auto");
	}
  
	$("#totalRecordCount").html(DOMPurify.sanitize(data.totalRecords));
	$("#timeTaken").html(DOMPurify.sanitize(data.timeTaken));
	$("#chunkSize").html(DOMPurify.sanitize(data.chunkSize));
	$("#chunkCount").html(DOMPurify.sanitize((parseInt(data.viewIndex)+1)+"/"+data.chunks));
	$("#goto-page").val(parseInt(data.viewIndex)+1);
	$("#TOTAL_CHUNK").val(data.chunks);
	if(parseInt(data.recordCount) < parseInt(viewSize)){
		$("#fetch-previous").css("pointer-events", "none");
		$("#fetch-next").css("pointer-events", "none");
		$("#fetch-first").css("pointer-events", "none");
		$("#fetch-last").css("pointer-events", "none");
	} 
	if((parseInt(data.chunks)-1) <= parseInt(viewIndex)){
		$("#fetch-next").css("pointer-events", "none");
		$("#fetch-last").css("pointer-events", "none");
	} else{
		$("#fetch-next").css("pointer-events", "auto");
		$("#fetch-last").css("pointer-events", "auto");
	}
}

function fetchPrevious() {
	$("#timeTaken").html(0);
	var viewIndex = $("#limitForm input[name=VIEW_INDEX]").val();
	if(viewIndex != null && viewIndex != "" && viewIndex != "undefined" ) {
		if(viewIndex>0){
			viewIndex = parseInt(viewIndex) - 1;
		} else{
			viewIndex = 0;
		}
		$("#limitForm input[name=VIEW_INDEX]").val(viewIndex);
	}
}

function fetchNext() {
	$("#timeTaken").html(0);
	var viewIndex = $("#limitForm input[name=VIEW_INDEX]").val();
	
	if(viewIndex != null && viewIndex != "" && viewIndex != "undefined" ) {
		viewIndex = parseInt(viewIndex) + 1;
		$("#limitForm input[name=VIEW_INDEX]").val(viewIndex);
	}
}

function fetchLast() {
	$("#timeTaken").html(0);
	var totalChunk = $("#TOTAL_CHUNK").val();
	$("#limitForm input[name=VIEW_INDEX]").val(parseInt(totalChunk)-1);
}

function fetchFirst() {
	$("#timeTaken").html(0);
	$("#limitForm input[name=VIEW_INDEX]").val(0);
}

function goto() {
  var pageNum = $("#goto-page").val();
  var totalChunk = $("#TOTAL_CHUNK").val();
  if("0" == pageNum) {
	  $("#goto-page").val(1);
	  pageNum = 1;
  }
  if(isNaN(pageNum) || "" == pageNum || parseInt(pageNum) > parseInt(totalChunk)){
	  return false;
  }else{
	  $("#limitForm input[name=VIEW_INDEX]").val(parseInt(pageNum)-1);
	  return true;
  }
}
function getGridDataFetchUrl(instanceid){
	var url = "";
	var limitFormInput = $('#limitForm').serialize();
	var url = '/ofbiz-ag-grid/control/getGridInstanceInfo?gridInstanceId='+instanceid; 
	$.ajax({
		async: false,
		url:url,
		type:"POST",
		data: JSON.parse(JSON.stringify(limitFormInput)),
		success: function(data){
			url=data.appliedRequestUrl;
		}
	});
	return url;
}
function prepareGridInstance(formDataObject){
	let gridApi = "";
	let gridInstanceId = formDataObject.gridInstanceId;
	let externalLoginKey = formDataObject.externalLoginKey;
	let userId = formDataObject.userId;
	
	
	if(gridInstanceId && userId){
		$("#"+gridInstanceId).html("");
		var gridPrefMap = getGridUserPreference(gridInstanceId, userId);
		var gridPreference = gridPrefMap.gridPreference;
		var filterModel = gridPrefMap.filterModel;
		if(!isObjectEmpty(gridPreference)){
			gridPreference.onGridReady= function() {
				//formDataObject.dataFetchCall();
				if(filterModel !== undefined && !isObjectEmpty(filterModel))
					gridApi.setFilterModel(filterModel);
			}
			//$('#grid_loading_'+gridInstanceId).hide();
			let gridOptions = gridPreference;
			//console.log("gridOptions---------->"+JSON.stringify(gridOptions));
			const eGridDiv = document.querySelector("#"+gridInstanceId);
			gridApi = agGrid.createGrid(eGridDiv, gridOptions);
		}
	}
	return gridApi;
}
function getGridUserPreference(gridInstanceId, userId){
	let gridPrefMap = new Map();
	if(gridInstanceId && userId){
		$.ajax({
	        type: "POST",
	      	async: false,
			url:'/ofbiz-ag-grid/control/getGridUserConfig',
			type:"POST",
			data: {"userid":userId,"instanceid":gridInstanceId,"lastUpdateTime":"-1"},
	        success: function(data) {
				let gridPreference = data.admin !==undefined ? deserialize(data.admin) : new Map();
				let userPreference = data.user !== undefined ? deserialize(data.user) : new Map();
				if(!isObjectEmpty(userPreference)){
					let userColumnDef = userPreference.columnDefs;
					if(!isObjectEmpty(userColumnDef)){
						gridPreference.columnDefs = userColumnDef;
					}
					let custom = userPreference.custom;
					if(!isObjectEmpty(custom)){
						let filterModel = custom.filterModel;
						if(!isObjectEmpty(filterModel)){
							gridPrefMap.filterModel = filterModel;
						}
					}
				}
				gridPrefMap.gridPreference = gridPreference;
				
	        },
	        error: function() {
	            console.log('Error occured');
	            showAlert("error", "Error occured!");
	        },
	        complete: function() {
	        	//$('#loader').hide();
	        }
	    });	
	}
	return gridPrefMap;
}
function saveGridPreference(gridInstance, prefInstanceId, userId){
	
	if(gridInstance && prefInstanceId && userId){
		let userPreferenceInstance = new Map();
		var columnState = gridInstance.getColumnState();
		var columnDefs = gridInstance.columnModel.columnDefs;
		var savedFilterModel = gridInstance.getFilterModel();
		console.log("savedFilterModel : "+ JSON.stringify(savedFilterModel));
		// Map columnState to get rearranged column definitions with header names
	    var rearrangedColumnDefs = columnState.map(function(column) {
	        var correspondingDef = columnDefs.find(function(def) {
	            return def.field === column.colId;
	        });
			if(correspondingDef){
				if (column && column.width !== undefined) {
	            	// If width information exists in the columnState, append it to the column definition
		            correspondingDef.width = column.width;
		        }
				if (column && column.hide !== undefined) {
		            // If width information exists in the columnState, append it to the column definition
		            correspondingDef.hide = column.hide;
		        }
			}
	        return correspondingDef;
	    });
		userPreferenceInstance.columnDefs = rearrangedColumnDefs;
		
		//prepare the filter model
		let filterModel = new Map();
		if(!isObjectEmpty(savedFilterModel)){
			filterModel.filterModel = savedFilterModel;	
		}
		userPreferenceInstance.custom = filterModel;
		
		let userGridOptions = JSON.stringify(userPreferenceInstance);
		
		var url = "/ofbiz-ag-grid/control/saveUserPrefGridConfig"; 
		$.ajax({
			async: false,
			url:url,
			type:"POST",
			data: {"instanceid": prefInstanceId, "userid": userId, "userGridOptions":userGridOptions},
			success: function(data){
				var responseMessage = data.responseMessage;
				if("success" == responseMessage){
					showAlert("success", data.successMessage);
				} else{
					showAlert("error", data.errorMessage);
				}
			}, 
			 error: function() {
	            console.log('Error occured');
	            showAlert("error", "Error occured!");
	        },
	        complete: function() {
	        	//$('#loader').hide();
	        }
		});
	}
}
function clearGridPreference(gridInstance, prefInstanceId, userId){
	if(gridInstance && prefInstanceId && userId){
		$.ajax({
		  async: false,
		  url:'/ofbiz-ag-grid/control/adminRemoveGrid',
		  type:"POST",
		  data: {
		  	instanceId: prefInstanceId,
		  	userId: userId,
		  	role: "USER"
		  },
		  success: function(data){
			console.log("User preference cleared!");
			showAlert("success", "User preference cleared!");
		  }
		});
	}
}
function setRowData(gridInstance,data){
	gridInstance.setGridOption('rowData', data);
}
function setGridData(gridInstance, callCtxObj) {
    var result = [];
	let ajaxUrl = callCtxObj.ajaxUrl;
	let formId = callCtxObj.formId;
	let key = callCtxObj.ajaxResponseKey;
	let responseData = {};
	
    var parameters = "";
	var formData = $(formId+' :input').serializeArray();
	if(formData){
		// Filter out inputs with empty values
        formData = formData.filter(function(input) {
            return input.value.trim() !== '';
        });
        parameters = $.param(formData);
	}

    if(parameters == null || parameters == '' || parameters == 'undefined'){
    	setRowData(gridInstance,result);
    } 
	if(ajaxUrl){
		var formParam = JSON.parse(JSON.stringify($(formId).serialize()));
		if(!formParam) formParam = {};
        $.ajax({
			type: "POST",
			async: true,
			url:ajaxUrl,
			//type:"POST",
			data: formParam,
            success: function(data) {
				if(data){
					if(key){
						responseData = data;
						result = data[key];
						removeElementByKey(responseData, key);
					}
					else{
						result = data;
					}
					paginateHandler(data);
					setRowData(gridInstance,result && result.length > 0 ? result : []);
					gridInstance.autoSizeAllColumns(true);
					return responseData;
				}
            },
            error: function() {
            },
            complete: function() {
            }
        });
    }
	
}

function removeElementByKey(obj, keyToRemove) {
    if (obj.hasOwnProperty(keyToRemove)) {
        delete obj[keyToRemove];
        //console.log(`Element with key '${keyToRemove}' removed.`);
    } else {
        //console.log(`Element with key '${keyToRemove}' not found.`);
    }
}
