var srNumber="";
var noteId="";

var columnDefsNotes = [

	 {
	    "headerName":"Note ID",
	    "field":"noteId",
        	cellRenderer: function(params) {
			return '<a href="noteDataDetails?noteId=' + params.data.noteId + '">' + params.data.noteId + '</a>'
		}
	 },
	 {
	    "headerName":"Note Title",
	    "field":"noteName"
	 },
	  {
	"headerName" : "Note Description",
	"field" : "noteInfo"
	 },
	 {
	"headerName" : "File Source",
	"field" : "noteType"
	 },
	 {
	"headerName" : "File Link",
	"field" : "moreInfoItemName",
	 cellRenderer: function(params) {
		var moreInfoItemName = params.data.moreInfoItemName;
		var docId = params.data.moreInfoItemId;
		if(moreInfoItemName != null && moreInfoItemName != "" && moreInfoItemName != undefined){
		//return '<a href="#" onclick="documentDowloadRequest('+ docId +','+ moreInfoItemName +')" >'+moreInfoItemName+'</a>'
		return '<a href=javascript:documentDowloadRequest("' + docId + '")>'+moreInfoItemName+'</a>';
		
		}
	 }
	 },
	 {
	"headerName" : "Created By",
	"field" : "createdBy"
	 },
	 {
	"headerName" : "Created On",
	"field" : "createdStamp"
	 }
  ]
var gridOptionsNotes = null;
function noteAttachGrid(){
	const url=window.location.search;
	const urlParam=new URLSearchParams(url);
	srNumber=urlParam.get("srNumber");
$("#NotesGrid").empty();
gridOptionsNotes = {
        defaultColDef: {
        width:350,
            filter: true,
            sortable: true,
            resizable: true
        },
        columnDefs: columnDefsNotes,
        // rowData: getRowData(),
        floatingFilter: true,
        rowSelection: "multiple",
        editType: "fullRow",
        paginationPageSize: 10,
        domLayout:"autoHeight",
        pagination: true,
        onGridReady: function() {
        getRowDataNotes();
        sizeToFitNotes();
        }
    }

    // lookup the container we want the Grid to use
    var eGridDiv = document.querySelector("#NotesGrid");
    // create the grid passing in the div to use together with the columns &
// data we want to use
    new agGrid.Grid(eGridDiv, gridOptionsNotes);
   
}


function getAjaxResponse(callback) {
    var data1;
    var result = [];
    var resultRes = null;
    // var params = {}
    // var paramStr = $("#findTemplateForm").serialize();
   

    // validate the serialize form data
   
   
    /*
* if(parameters == null || parameters == '' || parameters == 'undefined'){
* callback(result); } else{
*/
    // console.log("formData--->"+JSON.stringify(paramStr));
        // var fromData = JSON.stringify(paramStr);
        var errorMessage = null;
        var resultData = null;
        $.ajax({
            type: "POST",
            url: "getNotesAttachments",
            async: false,
            data:  {"srNumber": srNumber},
            success: function(data) {
                var result1 = data[0];
                if(result1)
                	noteId=result1.noteId;
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
            //$('#loader').hide();
            }
        });
    //}
   
}

function getRowDataNotes() {
var result;
result = getAjaxResponse(function(agdata) {
	gridOptionsNotes.api.setRowData(agdata);
    });
}

function sizeToFitNotes() {
	gridOptionsNotes.api.sizeColumnsToFit();
}

function documentDowloadRequest(documentId) {
	$.ajax({
        type: "POST",
        url: "downloadNotesAttachments",
        async: false,
        data:  {"documentId": documentId},
        success: function(data) {
            var resultData = data.result; 
            var resultStatus;          
            if(resultData != null || resultData != undefined){
            	resultStatus = resultData;
                if (resultStatus == "success") {
                	var fileNetUrl = data.fileNetUrl;
                	if (fileNetUrl != "") {
                		window.open(fileNetUrl, "_blank");
                	} else {
                		console.log('Error occured fileNetUrl: '+fileNetUrl);
                		showAlert("error", "Error occured!");
                	}
                } else {
                	var errorMsg = data.errorMessage;
                	console.log('Error occured');
                    showAlert("error", "Error occured!"+ errorMsg);
                }
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