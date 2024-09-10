

$(function() {
		
	  emailGrid(); 
	  loadSegmentCodeList();

	 
	  
});

$("#groupingCode").change(function() {
	loadSegmentCodeList()
	// emailGrid(); 
  });

$("#doSearch").click(function(event) {


	
	event.preventDefault(); 
	emailGrid();
	

});




var columnDefsCus = [	
	
	  {
		 "headerName" : "Grouping Code",
		 "field" : "groupingCode"
      },
	     {
			 "headerName" : "Economic Code Id",
			 "field" : "groupId"
	     },
		  {
	        "headerName":"Economic Code Name",
	        "field":"groupName"
	     },
		  {
		        "headerName":"Sequence No",
		        "field":"sequence"
		   },
		   {
		        "headerName":"Active",
		        "field":"isActive"
		  },
	 	{ 
	 	   "headerName":"Action",
	 	   "sortable":false,
	 	   "filter":false,
	 	   "field":"groupId",
	 	  // width:90,
	 	   suppressAutoSize:true,
     
	 	   cellRenderer: function (params) {  return  `<a href="viewEconomicValueForGroup?groupId=${params.value}" class="btn btn-xs btn-primary tooltips" data-original-title="View Economic Metrics"><i class="fa fa-eye info"></i></a>
		 		<a class="btn btn-xs btn-danger tooltips confirm-message" href="economicValueForGroup?groupId=${params.value}" data-original-title="Add Economic Metrics"><i class="fa fa-plus info"></i></a>
		 				 	<a href="editEconomicMetric?groupId=${params.value}" class="btn btn-xs btn-primary tooltips" data-original-title="Edit"><i class="fa fa-pencil info"></i></a>`
		 	   } 	   
	 	   
 	   }	

   
 ];





var gridOptionsCus = null;

function emailGrid(){
$("#findAttributeGroupgrid").empty();
    gridOptionsCus = {
        defaultColDef: {
        width:244,
            filter: true,
            sortable: true,
            resizable: true
        },
        columnDefs: columnDefsCus,
        // rowData: getRowDataCus(),
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
   
    // lookup the container we want the Grid to use
    var eGridDiv = document.querySelector("#findAttributeGroupgrid");
    // create the grid passing in the div to use together with the columns &
// data we want to use
    new agGrid.Grid(eGridDiv, gridOptionsCus);
   
}


function getAjaxResponses(callback) {
    var data1;
    var result = [];
    var resultRes = null;
    var errorMessage = null;
    var resultData = null;
    var paramStr = $("#findAttributeGroupForm").serialize();
    var fromData = JSON.stringify(paramStr);

    
        $.ajax({
            type: "POST",
            url: "getEconometricCode",
            async: false,
            data: JSON.parse(fromData),
           /* data : {
            	"groupingCode": $("#groupingCode").val(),
            	"groupId":$("#groupId").val(),
            	"valueCapture":$("#valueCapture").val(),
            	"isCampaignUse" :$("#isCampaignUse").val(),
            	"groupName" :$("#groupName").val(),
            	"type":$("#type").val()
            	
                    }, */
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



function loadSegmentCodeList() {
	var nonSelectContent = "<span class='nonselect'>Select ${uiLabelMap.economicMetric!}</span>";
	var groupNameOptions = '<option value="" data-content="'+nonSelectContent+'" selected="">Select ${uiLabelMap.economicMetric!}</option>';		
		
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
	
	
		
//		<#if customFieldGroup.groupId?has_content>
//		$("#groupId").val( "${customFieldGroup.groupId}" );
//		</#if>
	
		$('#groupId').dropdown('refresh');
	
		
}








