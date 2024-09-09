var screen = "";
var gridOptions = null;

var orderTypeList = {
		"" : "Please Select",
		"ORIGINAL" : "Original"
	};
let inputQtyData = new Map();

initiate();
function initiate() {
	console.log('externalLoginKey: '+$('#externalLoginKey').val());
	$.ajax({
		type : "POST",
		url : "/dyna-screen/control/getDynamicData",
		async : false,
		data : {
			"filterData" : { lookupFieldFilter: "{ 	\"entity_name\": \"EntityOrderType\", 	\"name_field\": \"orderTypeDesc\", 	\"value_field\": \"orderTypeId\" }" },
			"externalLoginKey" : $('#externalLoginKey').val()
		},
		success : function(data) {
			if (data.code == 200) {
				for (var key in data.fieldDataList) {
					if (key) {
						orderTypeList[key] = data.fieldDataList[key];
					}
				}
				console.log('orderTypeList: '+orderTypeList);
			} else {
				console.log (data.message);
			}
		},
		error : function() {
			console.log('Error occured');
		},
		complete : function() {
		}
	});
}

function getOrderLineRowData() {
	var result;
	result = getAjaxResponse(function(agdata) {
		gridOptions.api.setRowData(agdata);
	});
}
function sizeToFit() {
	gridOptions.api.sizeColumnsToFit();
}

function getAjaxResponse(callback) {
	
	var orderId = $("#mainFrom input[name=orderId]").val();
	
	var externalId = $("#mainFrom input[id=orderId_alter]").val();
	if(!externalId){
		externalId = $("#mainFrom input[id=orderId_desc]").val();
	}
	
	var srNumber = $("#mainFrom input[name='srNumber']").val();
	var orderDate = $("#mainFrom input[name=orderDate]").val();
	var externalLoginKey = $('#externalLoginKey').val();
	var location = $("#locationId").val();
	inputQtyData = new Map();
	
	if ( (orderId || externalId) ) {
		$.ajax({
			type : "POST",
			url : "/sr-portal/control/getOrderLines",
			async : false,
			data : {
				"orderId" : orderId,
				"externalId" : externalId,
				"srNumber" : srNumber,
				"orderDate" : orderDate,
				"location" : location,
				"externalLoginKey": externalLoginKey
			},
			success : function(data) {
				if (data.code == 200) {
					orderLines = data.orderLines;
					for (var index in orderLines) {
						var orderLine = orderLines[index];
						inputQtyData.set(orderId+'_'+orderLine.sequenceNo, orderLine.appliedQty);
					}
                	callback(orderLines);
				} else {
					console.log(data.message);
				}
			},
			error : function() {
				console.log('Error occured');
			},
			complete : function() {
			}
		});
	} else {
		callback([]);
	}
}


var columnDefs = [ 
{
	"headerName" : "Order ID",
	"field" : "externalId","filter": true,"floatingFilter": true, 
	"cellEditor" : "agTextCellEditor",
}, {
	"headerName" : "Item #",
	"field" : "productId","filter": true,"floatingFilter": true, 
	"cellEditor" : "agTextCellEditor",
}, {
	"headerName" : "Lot Detail",
	"field" : "productName","filter": true,"floatingFilter": true, 
	"cellEditor" : "agTextCellEditor",
	"cellRenderer": function(params) {
		let skuDescription = params.data.productName;
		let value = skuDescription;
		if (skuDescription && skuDescription.length > 20) {
			value = skuDescription.substring(0, 20) + '<span onclick="viewProdDesc(\'' + base64.encode(skuDescription) + '\')" class="btn btn-xs btn-primary m5 tooltips">...</span>';
		}
		return value;
	}
}, {
	"headerName" : "Actual Qty",
	"field" : "actualQty","filter": true,"floatingFilter": true, 
	"type": "numericColumn",
	"cellEditor" : "agTextCellEditor"
}, {
	"headerName" : "Applied Qty",
	"field" : "appliedQty","filter": true,"floatingFilter": true, 
	"type": "numericColumn",
	"cellEditor" : "agTextCellEditor",
	"editable" : true
}, {
	"headerName" : "Order Type",
	"field" : "orderTypeId","filter": true,"floatingFilter": true, 
	"cellEditor" : "agSelectCellEditor",
	"editable" : true,
	"cellEditorParams" : {
		values : extractValues(orderTypeList)
	},
	"valueFormatter" : function(params) {
		return lookupValue(orderTypeList, params.value);
	},
	"valueParser" : function(params) {
		return lookupKey(orderTypeList, params.newValue);
	}
}, {
	"headerName" : "Order Date",
	"field" : "orderDate","filter": true,"floatingFilter": true, 
	"cellEditor" : "agTextCellEditor",
}, {
	"headerName": "Location",
	"field": "storeName","filter": true,"floatingFilter": true, 
	"sortable": true,
	"minWidth": 100,
	"filter": "agTextColumnFilter"
}, {
	"headerName" : "Sequence #",
	"field" : "subSequenceNumber","filter": true,"floatingFilter": true, 
	"cellEditor" : "agTextCellEditor",
}, {
	"headerName" : "Line Status",
	"field" : "lineStatus","filter": true,"floatingFilter": true, 
	"cellEditor" : "agTextCellEditor",
}, {
	"headerName" : "Ship Date",
	"field" : "shippedDate","filter": true,"floatingFilter": true, 
	"cellEditor" : "agTextCellEditor",
}, {
	"headerName" : "Total Applied Qty",
	"field" : "totalAppliedQty","filter": true,"floatingFilter": true, 
	"type": "numericColumn",
	"cellEditor" : "agTextCellEditor",
	"editable" : true,
}, {
	"headerName" : "Total Avail Qty",
	"field" : "totalAvailQty","filter": true,"floatingFilter": true, 
	"type": "numericColumn",
	"cellEditor" : "agTextCellEditor",
	"editable" : true,
}, {
	"headerName": "Detail",
	"sortable": false,
	"minWidth": 200,
	cellRenderer: params => `<a target="_blank" href="/common-portal/control/viewOrder?orderId=${params.data.orderId}&externalLoginKey=${params.data.externalLoginKey}" >View</a>`
}
];

$(document).ready(function() {
	
	var actionMethod;
	$('#create-order-assoc').on('click', function() {
		var errorMessage = prepareScreenFields();
  		if (errorMessage) {
  			showAlert ("error", errorMessage);
  		} else {
  			let orderId = $("#mainFrom input[name=orderId]").val();
  			let externalId = $("#mainFrom input[id=orderId_alter]").val();
  			if(!externalId){
  				externalId = $("#mainFrom input[id=orderId_desc]").val();
  			}
  			let location = $("#locationId").val();
  			let orderDate = $("#mainFrom input[name=orderDate]").val();
  			
  			$.post("validateOrderDetails", {"orderId" : orderId, "externalId" : externalId, "location" : location, "orderDate" : orderDate}, function(data) {
  				if (data.requiredJustification == "Y") {
  					$('#justificationModel').modal('show');
  					actionMethod = "createSrOrderAssocAction";
  				} else {
  					actionMethod = "createSrOrderAssocAction";
  					$('#mainFrom').submit();
  				}
  			});
  			
  		}
	});
	
	$("#justificationModel").on("show.bs.modal", function(e) {
		var justificationOld = $("#justificationOld").val();
        if(justificationOld !=null && justificationOld != "" && justificationOld != 'undefined'){  
        } else{
			$("#justificationOldProd").dropdown('clear');
        }
    });

	$("#justificationOldProd").change(function(){
		$("#justificationOldProd_error").html("");
	});
	$('#justification-btn').on('click', function() {
		$("#justificationOldProd_error").html("");
		var justificationOldProd = $("#justificationOldProd").val();
		if(justificationOldProd == "" || justificationOldProd == null || justificationOldProd == "undefined"){
			$("#justificationOldProd_error").html("Please select justification");
		} else{
			$.post("updateSrJustifiation", $('#justificationForm').serialize(), function(data) {
				if (data.code == 200) {
					$('#justificationModel').modal('hide');
					$('#mainFrom').submit();
				} else {
				}
			});	
		}
	});
	
	$('#update-order-assoc').on('click', function() {
		var errorMessage = prepareScreenFields();
  		if (errorMessage) {
  			showAlert ("error", errorMessage);
  		} else {
  			let orderId = $("#mainFrom input[name=orderId]").val();
  			let externalId = $("#mainFrom input[id=orderId_alter]").val();
  			if(!externalId){
  				externalId = $("#mainFrom input[id=orderId_desc]").val();
  			}
  			let location = $("#locationId").val();
  			let orderDate = $("#mainFrom input[name=orderDate]").val();
  			$.post("validateOrderDetails", {"orderId" : orderId, "externalId" : externalId, "location" : location, "orderDate" : orderDate}, function(data) {
  				if (data.requiredJustification == "Y") {
  					$('#justificationModel').modal('show');
  					actionMethod = "srOrderAssocUpdateAction";
  				} else {
  					actionMethod = "srOrderAssocUpdateAction";
  					$('#mainFrom').submit();
  				}
  			});
  		}
	});
	
	$('#mainFrom').validator().on('submit', function (e) {
		if (e.isDefaultPrevented()) {
	    	// handle the invalid form...
	  	} else {
	  		e.preventDefault();
	  		
	  		var errorMessage = prepareScreenFields();
	  		if (errorMessage) {
	  			showAlert ("error", errorMessage);
	  		} else {
	  			let orderId = $("#mainFrom input[name=orderId]").val();
	  			let externalId = $("#mainFrom input[id=orderId_alter]").val();
	  			if(!externalId){
	  				externalId = $("#mainFrom input[id=orderId_desc]").val();
	  			}
	  			if (!orderId) {
	  				$("#mainFrom input[name=orderId]").val(externalId);
	  			}
	  			$.post(actionMethod, $('#mainFrom').serialize(), function(data) {
					if (data.code == 200) {
						showAlert ("success", data.message);
						window.location = "updateSrOrderAssoc?orderId="+data.orderId+"&srNumber="+data.srNumber;
					} else {
						showAlert ("error", data.message);
					}
				});
	  		}
	  	}
	});
	
});

loadOrderLineGrid();
function loadOrderLineGrid() {
	
	$("#order-line-grid").empty();
	gridOptions = {
		defaultColDef : {
			filter : true,
			sortable : true,
			resizable : true,
			//editable : true,
			//width : 124,
		},
		columnDefs : columnDefs,
		// rowData: data,
		floatingFilter : true,
		rowSelection: "single",
		//editType : "fullRow",
		paginationPageSize : 15,
		domLayout : "autoHeight",
		pagination : true,
		onGridReady : function() {
			//sizeToFit();
			getOrderLineRowData();
		},
		stopEditingWhenGridLosesFocus: true,
		onFirstDataRendered: onFirstDataRendered,
	}

	// lookup the container we want the Grid to use
	var eGridDiv = document.querySelector("#order-line-grid");
	// create the grid passing in the div to use together with the columns &
	// data we want to use
	new agGrid.Grid(eGridDiv, gridOptions);
}

function onFirstDataRendered(params) {
	params.columnApi.autoSizeAllColumns();
}

function prepareScreenFields() {
	$('#order-assoc-fields').empty();
	var orderId = $("#mainFrom input[name=orderId]").val();
	
	var isValid = true;
	var errorMessage = "";
	var aqNotChange = 0;
	
	gridOptions.api.forEachNode( function(rowNode, index) {
	    console.log('node ' + rowNode.data.sequenceNo + ' is in the grid');
	    
	    let productId = prepareData(rowNode.data.productId);
	    let orderTypeId = prepareData(rowNode.data.orderTypeId);
	    
	    let appliedQty = prepareData(rowNode.data.appliedQty);
	    let sequenceNo = prepareData(rowNode.data.sequenceNo);
		
	    let totalAppliedQty = prepareData(rowNode.data.totalAppliedQty);
	    let totalAvailQty = prepareData(rowNode.data.totalAvailQty);
		
		appliedQty = appliedQty ? appliedQty : 0;
		totalAvailQty = totalAvailQty ? totalAvailQty : 0;
		
		let isQtyChanged = inputQtyData.get(orderId+'_'+sequenceNo) != appliedQty;
		
		// field validation [start]
		if (isQtyChanged && appliedQty > totalAvailQty) {
			isValid = false;
			errorMessage += 'Applied qty cant be > total avail qty, productId# '+productId+"</br>";
		}
		
		if (inputQtyData.get(orderId+'_'+sequenceNo) == appliedQty) {
			aqNotChange++;
		}
		
		// field validation [end]
		
		if (appliedQty && isValid) {
			orderTypeId = "<input type='hidden' name='orderTypeId' value='"+orderTypeId+"'>";
			appliedQty = "<input type='hidden' name='appliedQty' value='"+appliedQty+"'>";
			sequenceNo = "<input type='hidden' name='sequenceNo' value='"+sequenceNo+"'>";
			
			$('#order-assoc-fields').append(orderTypeId);
			$('#order-assoc-fields').append(appliedQty);
			$('#order-assoc-fields').append(sequenceNo);
		}
	});
	
	console.log('aqNotChange: '+aqNotChange);
	if (inputQtyData.size == aqNotChange) {
		errorMessage = "Please double-click on the Applied Qty in the Order Lines grid, to update the value";
	}
	
	return errorMessage;
}

function printResult(res) {
    console.log('---------------------------------------')
    if (res.add) {
        res.add.forEach(function(rowNode) {
            console.log('Added Row Node', rowNode);
        });
    }
    if (res.remove) {
        res.remove.forEach(function(rowNode) {
            console.log('Removed Row Node', rowNode);
        });
    }
}

function prepareData(data) {
	if (data) {
		return data;
	}
	return "";
}
function viewProdDesc(description){
	$('#show-des-modal_des_title').html("Sku Info");
	$('#show-des-modal_des_value').html(DOMPurify.sanitize(base64.decode(description)));
	$('#show-des-modal').modal("show");
}

function extractValues(mappings) {
	return Object.keys(mappings);
}
function lookupValue(mappings, key) {
	return mappings[key];
}
function lookupKey(mappings, name) {
	for ( var key in mappings) {
		if (mappings.hasOwnProperty(key)) {
			if (name === mappings[key]) {
				return key;
			}
		}
	}
}

function isOrderAssocExists(orderId, externalId, srNumber, externalLoginKey) {
	let isExists = false;
	$.ajax({
		type: "POST",
     	url: "/sr-portal/control/getOrderDetail",
        data: {"orderId": orderId, "externalId": externalId, "srNumber": srNumber, "externalLoginKey": externalLoginKey},
        async: false,
        success: function (data) {   
            if (data.code == 409) {
            	showAlert("error", data.message);
            	isExists = true;
            	gridOptions.api.setRowData([]);
            	$("#orderDate").val(null);
            	$('#locationId').dropdown('clear');
            }
        }
	});
	return isExists;
}

function getOrderDetail(orderId, externalId, srNumber, externalLoginKey) {
	let result = null;
	$.ajax({
		type: "POST",
     	url: "/sr-portal/control/getOrderDetail",
        data: {"orderId": orderId, "externalId": externalId, "srNumber": srNumber, "externalLoginKey": externalLoginKey},
        async: false,
        success: function (data) {  
        	result = data;
            if (data.code == 409) {
            	showAlert("error", data.message);
            	gridOptions.api.setRowData([]);
            	$("#orderDate").val(null);
            	$('#locationId').dropdown('clear');
            }
        }
	});
	return result;
}