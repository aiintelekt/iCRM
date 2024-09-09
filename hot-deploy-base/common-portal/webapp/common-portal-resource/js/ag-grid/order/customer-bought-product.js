$(function() {
	let customerBoughtInstanceId= $('#customerBoughtInstanceId').val();
	let gridInstance  = "";
	//loadCustBoughtProduct();
	//let customerBoughtInstanceId = $("#recent-transactions_instanceId").val();
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();
	
	const formDataObject = {};
	formDataObject.gridInstanceId = customerBoughtInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;
	formDataObject.dataFetchCall = getCustBoughtRowData;
	
	
	gridInstance = prepareGridInstance(formDataObject);
	
	if(gridInstance){
		getCustBoughtRowData();
	}
	
	function getCustBoughtRowData() {
		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = "/customer-portal/control/searchOrders";
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#shipped-product-form";
		callCtx.ajaxResponseKey = "data";
		
		setGridData(gridInstance, callCtx);
	}
	
	$('#cust-bought-save-pref').click(function(){
		saveGridPreference(gridInstance, customerBoughtInstanceId, userId);
	});
	$("#shipped-export-btn").click(function() {
		gridInstance.exportDataAsCsv();
	});
	$('#cust-bought-clear-pref').click(function(){
		clearGridPreference(gridInstance, customerBoughtInstanceId, userId);
		if (gridInstance) {
		    gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getCustBoughtRowData();
		}
	});
	$('#cust-bought-clear-sub-ftr').click(function(){
		gridInstance.setFilterModel(null);
	});
	
});

/*
var columnDefs11 = "";
	//let customerBoughtInstanceId = $("#customerBoughtInstanceId").val();
	

	//columnDefs = JSON.stringify(getColumnDef(custBoughtInstanceId));
	//columnDefs11 = getColumnDef(custBoughtInstanceId);
	console.log("columnDefs--->"+columnDefs11);
	//var jsonStr = JSON.stringify(person);
	
	$("#customer-bought-product").empty();
	var custBoughtProdGridOptions = {
        columnDefs: getColumnDef(custBoughtInstanceId),
        defaultColDef: {
			flex: 1,
		    minWidth: 150,
		    filter: true,
		    sortable: true,
		    floatingFilter: true,
        },
        onGridReady: function() {
        	//sizeToFit();
        	getCustBoughtRowData();
        }
    }
    //lookup the container we want the Grid to use
    //var eGridDiv = document.querySelector("#customer-bought-product");
    
	// create the grid passing in the div to use together with the columns & data we want to use
    let gridApi11  = ""; //new agGrid.Grid(eGridDiv, custBoughtProdGridOptions);
	
	const eGridDiv11 = document.querySelector("#customer-bought-product");
	gridApi11 = agGrid.createGrid(eGridDiv11, custBoughtProdGridOptions);

	gridApi11.setGridOption('columnDefs', columnDefs11);
	
	
	function getCustBoughtRowData() {
		var result;
		result = getCustBoughtProdResponse(function(agdata) {
			gridApi11.setGridOption('rowData', agdata);
	    });
	}
*/



/*
function deserialize(serializedJavascript) {
    return eval("(" + serializedJavascript + ")");
}
*/
function getCustBoughtProdResponse(callback) {
    var result = [];
    var paramStr = $("#shipped-product-form").serialize();
    
	// validate the serialize form data
    var parameters =  $("#shipped-product-form :input")
		    .filter(function(index, element) {
		        return $(element).val() != '';
		    }).serialize();
    
    
    if(parameters == null || parameters == '' || parameters == 'undefined'){
    	callback(result);
    } else{
    	console.log("formData--->"+JSON.stringify(paramStr));
        var fromData = JSON.stringify(paramStr);
        var errorMessage = null;
        var resultData = null;
        $.ajax({
            type: "POST",
          	async: true,
			url:'/customer-portal/control/searchOrders',
			type:"POST",
			data: JSON.parse(JSON.stringify($("#shipped-product-form").serialize())),
            success: function(data) {
                var result1 = data[0];
                if(data[0] != null || data[0] != undefined){
                    errorMessage = data[0].errorMessage;
                    resultData = data[0].errorResult;
                }
                if(errorMessage != null || errorMessage != undefined) {
                    showAlert("error", errorMessage);
                    console.log("--errorMessage-----" + errorMessage);
                    callback(resultData);
                }else{
                    callback(data.data);
                }
                
            },
            error: function() {
                console.log('Error occured');
                showAlert("error", "Error occured!");
                callback(result);
            },
            complete: function() {
            	//$('#loader').hide();
            }
        });
    }
    
}


function priceParams(params){
	return `<i class="fa fa-usd"></i><span> ${params.value}</span>`; 
} 
function invoiceNoParams(params){
	return `<a target="_blank" href="/customer-portal/control/viewTransactionInvoice?invoiceId=${params.data.invoiceId}&externalLoginKey=${params.data.externalLoginKey}" >${params.data.invoiceId}</a>`; 
} 
function productImageParams(params){
	if(params && params.data){
		if (params.data.productImage != null && params.data.productName != null) {
			return `<img src="${params.data.productImage}" width="20" height="20" > ${params.data.productName}`; 
		} else if (params.data.productImage == null && params.data.productName != null) {
			return `<div style="width:23px; float:left;"> </div>${params.data.productName}`; 
		} else if (params.data.productImage != null && params.data.productName == null) {
			return `<img src="${params.data.productImage}" width="20" height="20" >`; 
		} else {
			return null; 
		} 
	}else{
		return null;
	}
}

