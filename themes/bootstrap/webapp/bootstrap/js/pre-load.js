var postLoadGridData = (api, gridApi, tabId, callBack) => {
	console.log("initiate tab: "+tabId)
	var tabShown = false;
	
	var href = window.location.href;
	if (href.includes(tabId)) {
		if (api && gridApi) {
			callBack(api, gridApi);
		} else if (gridApi) {
			callBack(gridApi);
		} else {
			callBack();
		}
		tabShown = true;
	}
	
	$("a[href='#"+tabId+"']").on('show.bs.tab', function (event) {
    	if (!tabShown) {
    		if (api && gridApi) {
    			callBack(api, gridApi);
    		} else {
    			callBack(gridApi);
    		}
    		tabShown = true;
    		console.log("load data for tab: "+tabId);
    	}
	});
}

var postLoadGrid = (api, gridApi, colApi, tabId, callBack) => {
	console.log("initiate tab: "+tabId)
	var tabShown = false;
	
	var href = window.location.href;
	if (href.includes(tabId)) {
		if (api && gridApi && colApi) {
			callBack(gridApi, api, colApi);
		}else if (api && gridApi) {
			callBack(gridApi, api);
		} else if (gridApi) {
			callBack(gridApi);
		} else {
			callBack();
		}
		tabShown = true;
	}
	
	$("a[href='#"+tabId+"']").on('show.bs.tab', function (event) {
    	if (!tabShown) {
    		if (api && gridApi && colApi) {
    			callBack(gridApi, api, colApi);
    		}else if (api && gridApi) {
    			callBack(gridApi, api);
    		} else {
    			callBack(gridApi);
    		}
    		tabShown = true;
    		console.log("load data for tab: "+tabId);
    	}
	});
}
var gridDataPostLoad = (tabId, callBack) => {
	console.log("initiate tab: "+tabId)
	var tabShown = false;
	
	var href = window.location.href;
	if (href.includes(tabId)) {
		callBack();
		tabShown = true;
	}
	
	$("a[href='#"+tabId+"']").on('show.bs.tab', function (event) {
    	if (!tabShown) {
    		callBack();
    		tabShown = true;
    		console.log("load data for tab: "+tabId);
    	}
	});
}
var initiateDefaultEvents = () => {
	$('[data-toggle="confirmation"]').confirmation({
		singleton: false,
	    popout: false
	});
}

function loadTabContent(instanceId, tabId, callback){
	//var location = window.location.href;
	//var hashVal = window.location.hash.substr(1);
	/*
	$("span[id^='tab_content_']" ).each(function(){
		var id = this.id;
		$("#"+id).html("");
	});	
	*/
	
	$("#"+instanceId+"-tab-form #tabId").val(tabId);
	var externalLoginKey = $("#"+instanceId+"-tab-form #externalLoginKey").val();
    //$("#tab_content_"+tabId).html("Loading.........");
	$("#tab_content_"+tabId).html("");
	$("#loading-img_"+tabId).show();
    $.ajax({
        type: "POST",
        url:'screenRender?externalLoginKey='+externalLoginKey,
        async: true,
        //data:{"partyId": "${parameters.partyId!}","componentId": "${currentComponent!}","tabConfigId":instanceId,"tabId":tabId},
		data: JSON.parse(JSON.stringify($("#"+instanceId+"-tab-form").serialize())),
        success: function(data) {
        	if(data){
				$("#tab_content_"+tabId).html("");
				$("#loading-img_"+tabId).hide();
        		//$("#tab_content_"+tabId).html(DOMPurify.sanitize(data));
				$("#tab_content_"+tabId).html(data);
				if(tabId!="temp-tagConfig" && tabId != "list-data-tag" && tabId!="template-tag"){
					$('select:not(.custom-selectbox)').dropdown();
					$("select").dropdown();
				}
				if (typeof callback === 'function') {
					callback();
				}
        	}
        }
    });
}

var exportList = (formId) => {
	$.ajax({
		async: true,
		url:'/common-portal/control/exportData',
		type:"POST",
		data: JSON.parse(JSON.stringify($("#"+formId).serialize())),
		success: function(data){
			showAlert("success", "Export in-progress, please check download screen.");
		}
	});
}

var exportList = () => {
	$.ajax({
		async: true,
		url:'/common-portal/control/exportData',
		type:"POST",
		data: JSON.parse(JSON.stringify($("#searchForm").serialize())),
		success: function(data){
			showAlert("success", "Export in-progress, please check download screen.");
		}
	});
}