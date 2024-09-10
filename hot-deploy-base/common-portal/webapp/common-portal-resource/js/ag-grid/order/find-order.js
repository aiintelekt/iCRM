/*
fagReady("LIST_ORDERS", function(el, api, colApi, gridApi){
    $("#order-refresh-pref-btn").click(function () {
    	gridApi.refreshUserPreferences();
    });
    $("#order-save-pref-btn").click(function () {
    	gridApi.saveUserPreferences();
    });
    $("#order-clear-filter-btn").click(function () {
    	try{
    		gridApi.clearAllColumnFilters();
    	}catch(e){
    	}
    	gridApi.refreshUserPreferences();
    });
	$("#order-sub-filter-clear-btn").click(function () {
    	try{
    		gridApi.clearAllColumnFilters();
    	}catch(e){
    	}
    });
    $("#order-export-btn").click(function () {
    	gridApi.csvExport();
    });

    $("#refresh-order-btn").click(function () {
    	loadOrderGrid(gridApi, api, colApi);
    });
    
    $(".filter-order").click(function(event) {
        event.preventDefault(); 
        
        $("#order-grid-header-title").html($(this).attr("data-searchTypeLabel"));
        //alert($(this).attr("data-searchTypeLabel"));
        $("#searchForm input[name=searchType]").val($(this).attr("data-searchType"));
        
        loadOrderGrid(gridApi, api, colApi);
    });  
    
    //loadOrderGrid(gridApi, api, colApi);
    postLoadGrid(api, gridApi, colApi, "sr-orders", loadOrderGrid);
    postLoadGrid(api, gridApi, colApi, "a-orders", loadOrderGrid);
    postLoadGrid(api, gridApi, colApi, "c-orders", loadOrderGrid);
    postLoadGrid(api, gridApi, colApi, "orders", loadOrderGrid);
    
    //api.sizeColumnsToFit();
});

function loadOrderGrid(gridApi, api, colApi) {
	var rowData =[];
	gridApi.setRowData(rowData);
	api.showLoadingOverlay();
	$.ajax({
	  async: false,
	  url:'/common-portal/control/searchOrders',
	  type:"POST",
	  data: JSON.parse(JSON.stringify($("#order-search-form").serialize())),
	  success: function(result){
		  gridApi.setRowData(result.data);
		  setTimeout(() => colApi.autoSizeAllColumns(), 1000);
	  }
	});
}
*/

$(function() {
	let orderInstanceId= "LIST_ORDERS";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();
	
	const formDataObject = {};
	formDataObject.gridInstanceId = orderInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;
	//formDataObject.dataFetchCall = getCustOrderedRowData;
	
	
	gridInstance = prepareGridInstance(formDataObject);
	
	if(gridInstance){
		getOrderGridData();
	}
	
	function getCustOrderedRowData() {
		var result = getOrderGridData(function(agdata) {
			gridInstance.setGridOption('rowData', agdata);
	    });
	}
	$(".filter-order").click(function(event) {
        event.preventDefault(); 
        
        $("#order-grid-header-title").html(DOMPurify.sanitize($(this).attr("data-searchTypeLabel")));

        $("#searchForm input[name=searchType]").val($(this).attr("data-searchType"));
        
    	getOrderGridData();
    });  
	
	$('#order-save-pref').click(function(){
		saveGridPreference(gridInstance, orderInstanceId, userId);
	});
	
	$('#order-clear-filter-btn').click(function(){
		clearGridPreference(gridInstance, orderInstanceId, userId);
		if (gridInstance) {
		    gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
	});
	$('#order-clear-sub-ftr').click(function(){
		gridInstance.setFilterModel(null);
	});
	$("#refresh-order-btn").click(function () {
    	getOrderGridData();
    });
	
	$("#order-export-btn").click(function() {
		gridInstance.exportDataAsCsv();
	});
    $(".filter-opportunity").click(function(event) {
        event.preventDefault(); 
        
        $("#opportunity-grid-header-title").html(DOMPurify.sanitize($(this).attr("data-searchTypeLabel")));
        $("#searchForm input[name=searchType]").val($(this).attr("data-searchType"));
        
        getOrderGridData();
    });
    $(".oppo-status").change(function(event) {
    	var openStatus = $('input[name="oppoOpen"]:checked').val();
    	var closedStatus = $('input[name="oppoClosed"]:checked').val();
    	
    	if(openStatus != null && openStatus !="undefined" && openStatus != "")
    		$("#opportunity-search-form #statusOpen").val(openStatus);
    	else
    		$("#opportunity-search-form #statusOpen").val("");
    	
    	if(closedStatus != null && closedStatus !="undefined" && closedStatus != "")
    		$("#opportunity-search-form #statusClosed").val(closedStatus);
    	else
    		$("#opportunity-search-form #statusClosed").val("");
    	
    	getOrderGridData();
    });
    
    $("#estClosedDays").focusout(function() {
    	var estClosedDays = $("#estClosedDays").val();
    	if(estClosedDays != null && estClosedDays !="undefined" && estClosedDays != "")
    		$("#opportunity-search-form #estimatedClosedDays").val(estClosedDays);
    	else
    		$("#opportunity-search-form #estimatedClosedDays").val("");
    	
    	getOrderGridData();
    });
    
    $("#refresh-opportunity-btn").click(function () {
    	getOrderGridData();
    });
    
	$("#search-oppo-btn").click(function () {
    	getOrderGridData();
    });
	
	function setRowData(data){
		gridInstance.setGridOption('rowData', data);
	}
	
	function getOrderGridData() {
	    var result = [];
		gridInstance.showLoadingOverlay();

	    // validate the serialize form data
	    var parameters =  $("#order-search-form :input").filter(function(index, element) {
			        return $(element).val() != '';
			    }).serialize();

	    if(parameters == null || parameters == '' || parameters == 'undefined'){
	    	
	    	setRowData(result);
	    } else{
			

	        $.ajax({
				async: true,
				url:'/common-portal/control/searchOrders',
			    type:"POST",
			    data: JSON.parse(JSON.stringify($("#order-search-form").serialize())),
	            success: function(data) {
					setRowData(result);
					if(data){
						gridInstance.showLoadingOverlay();
						let resData = data.data;
						if(resData){
							setRowData(resData);
						}	
					}
	            },
	            error: function() {
	                showAlert("error", "Error occured!");
	               	setRowData(result);
	            },
	            complete: function() {
	            }
	        });
	    }   
	}
	
	$("#send-ereceipt-btn").click(function () {
    	var selectedData = gridInstance.getSelectedRows();
		if (selectedData.length > 0) {
			
			console.log(selectedData);
			
		    var selectedItemIds = "";
		    for (i = 0; i < selectedData.length; i++) {
		    	var data = selectedData[i];
		    	selectedItemIds += data.externalId+",";
		    }
		    selectedItemIds = selectedItemIds.substring(0, selectedItemIds.length - 1);
		    $('#send-order-ereceipt-form input[name=transactionNumber]').val(selectedItemIds);
			
			$.ajax({
				async : false,
				url : '/common-portal/control/sendEreceipt',
				type : "POST",
				data : JSON.parse(JSON.stringify($("#send-order-ereceipt-form").serialize())),
				success : function(data) {
					if (data.code == 200) {
						showAlert ("success", "Successfully send ereceipt..");
						getOrderGridData();
					} else {
						showAlert ("error", data.message);
					}
				}
			});
			
		} else {
			showAlert("error", "Please select at least one order to send Ereceipt!");
		}
    });
	
});

function enableInvoiceReceiptPopup(transactionNumber){
	if(transactionNumber != null && transactionNumber != "" && transactionNumber !="undefined"){
		$.ajax({
			async: false,
			url:'/common-portal/control/getReceiptHtmlData',
			type:"POST",
			data: {'orderId':transactionNumber},
			success: function(data){
				if(data.data)
					$("#invoiceReceiptHtmlContent").html(DOMPurify.sanitize(data.data));
				else
					$("#invoiceReceiptHtmlContent").html("No content found");
				$('#invoice-receipt-popup').modal("show");
			}
		});
	}
}

function viewSkuDescription(orderId){
	$.ajax({
		  async: false,
		  url:'/common-portal/control/getSkuDescriptionList',
		  type:"POST",
		  data: {"orderId":orderId},
		  success: function(result){
				$('#show-des-modal_des_title').html("Sku Info");
				var jsondata  = "";
				jsondata = jsondata + "<div class='table-responsive'>";
			    jsondata = jsondata + "<table class='table table-hover table-striped'>";
			    jsondata = jsondata + "<thead>";
			    jsondata = jsondata + "<tr>";
			    jsondata = jsondata + "<th width='30%'>SKU Number</th>";
			    jsondata = jsondata + "<th width='30%'>Sku Description</th>";
			    jsondata = jsondata + "<th width='35%'>Product Image</th>";
			    jsondata = jsondata + "</tr>";
			    jsondata = jsondata + "</thead>";
			    jsondata = jsondata + "<tbody>";
				if(result != null){
						$.each(result.skuDescriptionList, function( key, value ) {
						  var jsondata1 = "";
						  jsondata1 = jsondata1 + "<tr>";
						  jsondata1=jsondata1+"<td>"+value.skuNumber+"</td>";
						  jsondata1=jsondata1+"<td>"+value.skuDescription+"</td>";
						  if(value.smallImageUrl != null){
						  jsondata1=jsondata1+"<td><img src="+value.smallImageUrl+" alt="+value.skuDescription+" style-'display:block;' width='80px' height='80px'></td>";
						  }
						  else{
							  jsondata1=jsondata1+"<td></td>"; 
						  }
						  jsondata1 =jsondata1+ "</tr>";
						  jsondata = jsondata+jsondata1;
						});
				}
					jsondata = jsondata + "</tbody>";
					jsondata = jsondata + "</table>";
					jsondata = jsondata + "</div>";
				$('#show-des-modal_des_value').html(DOMPurify.sanitize(jsondata));
				$('#show-des-modal').modal("show");
		  }
		});
}
function enableReceiptPopup(orderId){
	if(orderId != null && orderId != "" && orderId !="undefined"){
		$.ajax({
			async: false,
			url:'getReceiptHtmlData',
			type:"POST",
			data: {'orderId':orderId},
			success: function(data){
				if(data.data)
					$("#receiptHtmlContent").html(DOMPurify.sanitize(data.data));
				else
					$("#receiptHtmlContent").html("No content found");
				$('#receipt-popup').modal("show");
			}
		});
	}
}

function orderIdParams(params){
	return`<a target="_blank" href="/common-portal/control/redirectOrders?orderId=${params.data.orderId}&partyId=${params.data.partyId}&externalLoginKey=${params.data.externalLoginKey}" >${params.data.externalId}</a>`;
} 
function skuDescriptionParams(params) { 
	let skuDescription = params.data.skuDescription; 
	let value = skuDescription; 
	if (skuDescription && skuDescription.length > 20) {
		value = skuDescription.substring(0, 20) + '<span onclick="viewSkuDescription(\'' + skuDescription + '\')" class="btn btn-xs btn-primary m5 tooltips">...</span>'; 
	} 
	return value;
} 



