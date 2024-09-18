
function Init() {
    var http = new XMLHttpRequest();
    http.open("GET", getDashboardsUrl, true);
    http.responseType = 'json';
    http.setRequestHeader("Content-type", "application/json");
    http.onreadystatechange = function () {
        if (http.readyState == 4 && http.status == 200) {
            ListDashboards.call(this, typeof http.response == "object" ? http.response : JSON.parse(http.response));
        }
        else if (http.readyState == 4 && http.status == 404) {
            console.log("Server not found");
        }
        else if (http.readyState == 4) {
            console.log(http.statusText);
        }
    };

    http.send();
};

function ListDashboards(data) {
    if (typeof (data) != "undefined" && data != null) {
        renderDashboard(data[0].Id);
        data.forEach(function (element) {
            var divTag = document.createElement("div");
            divTag.innerHTML = element.Name;
            divTag.className = "dashboard-item";
            divTag.setAttribute("onclick", "renderDashboard('" + element.Id + "')");
            divTag.setAttribute("name", element.Name);
            divTag.setAttribute("itemid", element.Id);
            divTag.setAttribute("version", element.Version);
            divTag.setAttribute("ispublic", element.IsPublic);
            document.getElementById("panel").appendChild(divTag);
        });
    }
}

function renderDashboard(dashboardId) {
	console.log("dashboardId---"+dashboardId);
	﻿var tokenKey = $("#tokenKey").val();

	this.dashboard = BoldBI.create({
		serverUrl: rootUrl + "/" + siteIdentifier,
		dashboardId: dashboardId,
		embedContainerId: "dashboard",
		mode: BoldBI.Mode.View,
		embedType: embedType,
		environment: environment,
		width: "100%",
		height: "100%",
		expirationTime: 10000,
		token : tokenKey
	});
    this.dashboard.loadDashboard();
	//this.dashboard.loadDashboardWidget("ALL DATA");
};

function renderDashboardLinking() {
	var param = "Demo Account00004";
	﻿var tokenKey = $("#tokenKey").val();
	var groupName = $("#groupName").val();
	if(groupName !=null && groupName !="" && groupName!=undefined){
		param = groupName;
	}
	console.log("groupName====="+groupName);
	console.log("param====="+param);
    var dashboardLinking = BoldBI.create({
        serverUrl: rootUrl + "/" + siteIdentifier,
        dashboardId: dashboardId,
        embedContainerId: "dashboard",
        mode: BoldBI.Mode.View,
        embedType: embedType,
        environment: environment,
        width: "100%",
        height: "100%",
        dashboardSettings: {showHeader: false},
        filterParameters : "COMPANY_NAME=" + param + "&BILL_TO_PARTY_NAME=" + param + "&GROUP_NAME=" + param,
        expirationTime: 100000,
        token : tokenKey
    });
    dashboardLinking.loadDashboard();
};
function renderFilterParameter() {
    var param = $("#filterParameters").val();
    console.log("param----"+param);
    var instance = BoldBI.getInstance("dashboard");
    instance.embedOptions.filterParameters = "COMPANY_NAME=" + param + "&&BILL_TO_PARTY_NAME=" + param + "&GROUP_NAME=" + param;
    instance.loadDashboard();
}

function renderDbrd() {
    var param = $("#dashboardParameters").val();
    var instance = BoldBI.getInstance("dashboard");
    console.log("param" + param);
    console.log("instance" + instance);
    instance.embedOptions.filterParameters = "STORE_NAME=" + param;
    instance.loadDashboardWidget("ALL DATA");
}

function renderDbrdDialog(dashboardId) {
	var tokenKey = $("#tokenKey").val();
	console.log("dashboardId==================="+dashboardId);
    var dialog = new ejdashboard.popups.Dialog({
        header: 'Dashboard Preview',
        width: '1200px',
        height: '700px',
        isModal: true,
        showCloseIcon: true,
        target: document.body,
        content: '<div id="preview-dashboard"></div>'
    });
    $("#dashboard-dialog").html("");
    $("#preview-dashboard").html("");

    dialog.appendTo('#dashboard-dialog');
    var previewDashboard = BoldBI.create({
        serverUrl: rootUrl + "/" + siteIdentifier,
        dashboardId: dashboardId,
        embedContainerId: "preview-dashboard",
        mode: BoldBI.Mode.View,
        embedType: embedType,
        environment: environment,
        width: "100%",
        height: "100%",
        expirationTime: 10000,
	    	token : tokenKey
	});
    previewDashboard.loadDashboard();
}
function embedConfigErrorDialog() {
    var targetContainer = $('<div id="custom_dialog"></div>');
    var dlgDiv = $('<div id="sample_dialog" ></div>');
    targetContainer.append(dlgDiv);
    $('body').append(targetContainer);
    var dialog = new window.ejs.popups.Dialog({
        header: 'Error Message',
        width: '500px',
        isModal: true,
        showCloseIcon: true,
        target: document.getElementById('custom_dialog'),
        content: '<div>To compile and run the project, an embed config file needs to be required. Please use the <a href="https://help.boldbi.com/site-administration/embed-settings/" target="_blank">URL</a> to obtain the JSON file from the Bold BI server.</div>'
    });
    dialog.appendTo('#sample_dialog');
    var dialogFooter = $('<div id="sample_dialog_footer"><button id="custom_ok_button"onclick="Cancel()">OK</button></div>')
    $('#sample_dialog').append(dialogFooter);
    $('.e-dlg-overlay').css('position', 'fixed');
};

function Cancel() {
    $("#custom_dialog").html('');
}