

$(function() {
	emailGrid();
	loadEconomicCodeList();

});

$("#groupingCode").change(function() {
	loadEconomicCodeList()
	//emailGrid();
});

$("#doSearch").click(function(event) {
	event.preventDefault(); 	
	emailGrid();
		
});

var columnDefsCus = [

	    {
		 "headerName" : "Grouping Code",
		 "field" : "groupingCodeName"

        },
	     {
			 "headerName" : "Economic Code",
			 "field" : "groupName"
	     },
		  {
	        "headerName":"Economic Metric Id",
	        "field":"customFieldId"
	     },
	     {
		        "headerName":"Economic Metric Name",
		        "field":"customFieldName"
		  },
		  {
		        "headerName":"Enabled",
		        "field":"isEnabled"
		   },
		  {
		        "headerName":"Sequence No",
		        "field":"sequenceNumber"
		   },{
		        "headerName":"Coupon Campaign",
		        "field":"productPromoCodeGroupId"
		   },
	     
	 	  { 
	 	   "headerName":"Action",
	 	   "sortable":false,
	 	   "filter":false,
	 	   "field":"",
	 	   suppressAutoSize:true,
     
	 	  cellRenderer: function (params) {  return  `<a href="economicValueCustomer?customFieldId=${params.data.customFieldId}&groupId=${params.data.groupId}" class="btn btn-xs btn-primary tooltips" data-original-title="Edit"><i class="fa fa-plus info"></i></a>
		 		<a class="btn btn-xs btn-danger tooltips confirm-message" href="editEconomicValue?customFieldId=${params.data.customFieldId}&groupId=${params.data.groupId}" data-original-title="Remove"><i class="fa fa-pencil info"></i></a>`
		 	   } 	   
 	   }
	
   
 ];

		

var gridOptionsCus = null;

function emailGrid(){
$("#economicMetricAgGrid").empty();
    gridOptionsCus = {
        defaultColDef: {
        width:244,
            filter: true,
            sortable: true,
            resizable: true
        },
        columnDefs: columnDefsCus,
        floatingFilter: true,
        rowSelection: "multiple",
        editType: "fullRow",
        paginationPageSize: 10,
        domLayout:"autoHeight",
        pagination: true,
        onGridReady: function() {
        getRowDataCus();
        sizeToFitCus();
        }
    }

    var eGridDiv = document.querySelector("#economicMetricAgGrid");
    new agGrid.Grid(eGridDiv, gridOptionsCus);
   
}

function getAjaxResponses(callback) {
    var data1;
    var result = [];
    var resultRes = null;
    var paramStr = "";
    
    if(getUrlParameter("groupId") != "" && getUrlParameter("groupId")!="undefined" && getUrlParameter("groupId")!=undefined)
    	paramStr = {groupId: getUrlParameter("groupId")};
    
    else
    	paramStr = $("#searchForm").serialize();
      
    var fromData = JSON.stringify(paramStr);  
    
        var errorMessage = null;
        var resultData = null;
        $.ajax({
            type: "POST",
            url: "getEconomicMetric",
            async: false,
            data:  JSON.parse(fromData),
            /*data: {
            	   "customFieldName": $("#customFieldName").val(),
            	   "isEnabled"      : $("#isEnabled").val(),
            	   "groupingCode"   : $("#groupingCode").val(),
            	   "customFieldId"  : $("#customFieldId").val(),
            	   "groupId"        :  $("#groupId").val()
                  },*/

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
                    callback(data);
                }
               
            },
            error: function() {
                console.log('Error occured');
                showAlert("error", "Error occured!");
                callback(result);
            },
            complete: function() {

            }
        });

   
}

function getRowDataCus() {
var result;
result = getAjaxResponses(function(agdata) {
	
gridOptionsCus.api.setRowData(agdata);
    });
}

function sizeToFitCus() {
    gridOptionsCus.api.sizeColumnsToFit();
}



function loadEconomicCodeList() {
	var nonSelectContent = "<span class='nonselect'>Select ${uiLabelMap.economicCode!}</span>";
	var groupNameOptions = '<option value="" data-content="'+nonSelectContent+'" selected="">Select ${uiLabelMap.economicCode!}</option>';		
		
	if ( $("#groupingCode").val() ) {
		
		$.ajax({
			      
			type: "POST",
	     	url: "getCustomFieldGroups",
	        data:  {"groupingCode": $("#groupingCode").val()},
	        async: false,
	        success: function (data) {   
	            
	            if (data.code == 200) {
	            
	            	for (var i = 0; i < data.groups.length; i++) {
	            		var group = data.groups[i];
	            		groupNameOptions += '<option value="'+group.groupId+'">'+group.groupName+'</option>';
	            	}
	            	
	            }
				    	
	        }
	        
		});    
		$(".groupId").find('.clear').click();
		$("#groupId").html( DOMPurify.sanitize(groupNameOptions) );
		
//		<#if customField.groupId?has_content>
//		$("#groupId").val( "${customField.groupId}" );
//		</#if>
	
		$('#groupId').dropdown('refresh');
	}
		
}






