fagReady("AWS_INSTANCES", function(el, api, colApi, gridApi){
	$("#campaign-refresh-pref-btn").click(function () {
		gridApi.refreshUserPreferences();
	});
	$("#aws-refresh-btn").click(function () {
		loadMainGrid(api, gridApi, colApi);
	});
	$("#save-pref-btn").click(function () {
		gridApi.saveUserPreferences();
	});
	$("#clear-filter-btn").click(function () {
		try{
			gridApi.clearAllColumnFilters();
		}catch(e){
		}
		gridApi.refreshUserPreferences();
	});
	$("#sub-filter-clear-btn").click(function () {
		try{
			gridApi.clearAllColumnFilters();
		}catch(e){
		}
	});
	$("#export-btn").click(function () {
		gridApi.csvExport();
	});
	$("#fetch-previous").click(function () {
		fetchPrevious();
		loadMainGrid(api, gridApi, colApi);
	});
	$("#fetch-next").click(function () {
		fetchNext();
		loadMainGrid(api, gridApi, colApi);
	});
	$("#fetch-first").click(function () {
		fetchFirst();
		loadMainGrid(api, gridApi, colApi);
	});
	$("#fetch-last").click(function () {
		fetchLast();
		loadMainGrid(api, gridApi, colApi);
	});
	$('#goto-page').keypress(function(event){
		var keycode = (event.keyCode ? event.keyCode : event.which);
		if(keycode == '13'){
			if(goto()) loadMainGrid(api, gridApi, colApi);
		}
	});
	$("#dashboard-filter").click(function(event) {
		event.preventDefault(); 
		console.log("dashboard-filter trigger");
		loadMainGrid(api, gridApi, colApi);
	});
	$('.change-state-drpdwns').on('click',function(){
		let state = $(this).attr('state');
		if(state){
			var rowdata = api.getSelectedRows();
			var instances = [];
			if(rowdata!=null && rowdata!=""){
				rowdata.forEach((element,index) => {
					instances[index] = element.instanceId;
				});
				$.ajax({
					async: false,
					url:'/common-portal/control/updateAWSInstancesState',
					type:"POST",
					data: {"instanceId":instances.join(", "),"state":state},
					success: function(result){
							if(result){
								showAlert('success', 'The request for '+state+' posted to AWS');
								window.location.reload();
							}
					}
				});
			} else {
				showAlert('error', 'Please select one record in the list');
			}
			console.log(instances)
		}
	});
	loadMainGrid(api, gridApi, colApi);
});

function loadMainGrid(api, gridApi, colApi) {	
	var rowData =[];
	gridApi.setRowData(rowData);
	api.showLoadingOverlay();
	var formInput = $('#searchForm, #limitForm').serialize();
	$.ajax({
		async: true,
		url:'/common-portal/control/getAllAWSInstances',
		type:"POST",
		data: JSON.parse(JSON.stringify(formInput)),
		success: function(result){
			if(result){
				gridApi.setRowData(result.list);
				setTimeout(() => colApi.autoSizeAllColumns(), 1000);
				result.list=[];
				paginateHandler(result);
			}
		}
	});
}

function onRowSelected(event) {
	var statesMap = new Map();
	statesMap.set('start',['start','starting','pending','running']);
	statesMap.set('stop',['stop','stopping','stopped']);
	statesMap.set('reboot',['stop','stopping','stopped']);
	var skipStates = [];
	var selectedRows = event.api.getSelectedRows();
	for(let ele of statesMap.keys()){
		selectedRows.forEach((element,index) => {
			let values = statesMap.get(ele);
			if(values.includes(element.instanceState)){
				if(!skipStates.includes(ele)){
					skipStates.push(ele);
				}
			}
		});
	}
	var dropdowns = $('.change-state-drpdwns');
	dropdowns.each((index, element) => {
		let state = $(element).attr('state');
		if(state && skipStates.includes(state)){
			$(element).removeAttr('state');
			$(element).removeAttr('href');
		}
	});
	dropdowns.each((index, element) => {
		let state = $(element).attr('state');
		let textContent = $(element).text();
		if(!state && !skipStates.includes(state)){
			for(let sEach of statesMap.keys()){
				if(textContent.toUpperCase().indexOf(sEach.toUpperCase())>=0 && !skipStates.includes(sEach)){
					$(element).attr('state', sEach);
					$(element).attr('href','#');
				}
			}
		}
	});
}