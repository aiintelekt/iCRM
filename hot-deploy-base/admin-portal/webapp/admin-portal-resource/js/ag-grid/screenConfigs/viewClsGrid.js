
$(function() {
	loadClsGrid();
});

var columnClsDefs = [{ 
	"headerName":"Cls Id",
	"field":"clsId",
	"sortable":true,
	"filter":true,
	cellRenderer: function(params) {
		return '<a href="viewClsSpecifications?clsId=' +params.data.clsId +' ">' + params.data.clsId + '</a>';
	}
},
{ 
	"headerName":"Module",
	"field":"mountPoint",
	"sortable":true,
	"filter":true,
},
{ 
	"headerName":"Layout Name",
	"field":"layout",
	"sortable":true,
	"filter":true,
},
{ 
	"headerName":"Request Uri",
	"field":"requestUri",
	"sortable":true,
	"filter":true,
},
{ 
	"headerName":"Screen Service",
	"field":"screenService",
	"sortable":true,
	"filter":true,
},
{ 
	 "headerName":"Edit",
     "field":"isEdit",
     cellRenderer: function(params) {
    	 
    	 var clsId = params.data.clsId;
    	 var module = params.data.mountPoint;
    	 var layout = params.data.layout;
    	 var screen = params.data.screen;
    	 var screenService = params.data.screenService;
    	 var requestUri = params.data.requestUri;
    	return '<button class="fa fa-edit btn btn-xs btn-primary" onclick=setupEdit("'+clsId+'","'+module+'","'+layout+'","'+screen+'","'+screenService+'","'+requestUri+'")>';
     	//return '<a href="createScreenConfigs?clsId=' +params.data.clsId +'&module=' +params.data.mountPoint +'&screen='+params.data.layout+'&requestUri='+params.data.requestUri+'&layout='+params.data.layout+'&screenService='+params.data.screenService+'     " class="fa fa-edit btn btn-xs btn-primary" aria-hidden="true"></i>';    	
     	}

}



];

var gridOptionsCls = null;
function loadClsGrid(){
	$("#viewClsGrid").empty();
	gridOptionsCls = {
			defaultColDef: {
				filter: true,
				sortable: true,
				resizable: true
			},
			columnDefs: columnClsDefs,
			floatingFilter: true,
			rowSelection: "multiple",
			editType: "fullRow",
			paginationPageSize: 10,
			domLayout:"autoHeight",
			pagination: true,
			onGridReady: function() {
				sizeToFitCls();
				getClsRowData();
			}
	}

	//lookup the container we want the Grid to use
	var eGridDiv = document.querySelector("#viewClsGrid");
	// create the grid passing in the div to use together with the columns & data we want to use
	new agGrid.Grid(eGridDiv, gridOptionsCls);

}


function getClsAjaxResponse(callback) {
	var result = [];
	var resultRes = null;
	var params = {}
	var inputData = {};
	var mountPoint = $("#module").val();
	var layout = $("#layout").val();
	inputData = {"mountPoint" : mountPoint, "layout" : layout };
	var errorMessage = null;
	var resultData = null;
	$.ajax({
		type: "POST",
		url: "getScreenConfigurationDetails",
		async: false,
		data: inputData,
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
		}
	});

}

function sizeToFitCls() {
	gridOptionsCls.api.sizeColumnsToFit();
}

function getClsRowData() {
	var result;
	result = getClsAjaxResponse(function(agdata) {
		gridOptionsCls.api.setRowData(agdata);
	});
}

function setupEdit(clsId,module,layout,screen,screenService,requestUri){
		
		var form = document.createElement("form");
		form.setAttribute("method","post");
		form.setAttribute("action","createScreenConfigs");
		
		var cls = document.createElement("input");
		cls.setAttribute("type","hidden");
		cls.setAttribute("name","clsId");
		cls.setAttribute("value",clsId);
		form.appendChild(cls);
		
		var mod = document.createElement("input");
		mod.setAttribute("type","hidden");
		mod.setAttribute("name","module");
		mod.setAttribute("value",module);
		form.appendChild(mod);
		
		var lo = document.createElement("input");
		lo.setAttribute("type","hidden");
		lo.setAttribute("name","layout");
		lo.setAttribute("value",layout);
		form.appendChild(lo);
		
		var sc = document.createElement("input");
		sc.setAttribute("type","hidden");
		sc.setAttribute("name","screen");
		sc.setAttribute("value",screen);
		form.appendChild(sc);
		
		var ss = document.createElement("input");
		ss.setAttribute("type","hidden");
		ss.setAttribute("name","screenService");
		ss.setAttribute("value",screenService);
		form.appendChild(ss);
		
		var ru = document.createElement("input");
		ru.setAttribute("type","hidden");
		ru.setAttribute("name","requestUri");
		ru.setAttribute("value",requestUri);
		form.appendChild(ru);
		
		document.body.appendChild(form);
		form.submit();
}





