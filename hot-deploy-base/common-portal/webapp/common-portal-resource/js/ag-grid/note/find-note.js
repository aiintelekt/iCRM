

$(function() {
	let noteListInstanceId = "NOTES";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();
	
	const formDataObject = {};
	formDataObject.gridInstanceId = noteListInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;
	//formDataObject.dataFetchCall = getRecentTransRowData;
	
	
	gridInstance = prepareGridInstance(formDataObject);
	
	function getNoteGridData() {
	    const callCtx = {};
	    callCtx.ajaxUrl = "/common-portal/control/searchNotes";
	    callCtx.externalLoginKey = externalLoginKey;
	    callCtx.formId = "#note-search-form";
	    callCtx.ajaxResponseKey = "data";
		gridInstance.showLoadingOverlay();

	    setGridData1(gridInstance, callCtx)
	        .then(responseData => {
	            if (responseData && responseData.importantNotesCount !== undefined) {
	        		const $field = $('#10242').find('span');
	        		if(responseData.importantNotesCount != null && responseData.importantNotesCount > 0){
	        			$field.attr('badge', responseData.importantNotesCount);
	        		}else{
	        			$field.attr('badge', 0);
	        		}
	            } else {
	                console.log("importantNotesCount not found");
	            }
	        })
	        .catch(error => {
	            console.error("Error:", error);
	        });
	}
	
	
	if(gridInstance){
		getNoteGridData();
	}
	
	$('#notes-save-pref').click(function(){
		saveGridPreference(gridInstance, noteListInstanceId, userId);
	});
	
	$('#notes-clear-pref').click(function(){
		clearGridPreference(gridInstance, noteListInstanceId, userId);
		if (gridInstance) {
		    gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getNoteGridData();
		}
	});
	$('#notes-clear-sub-ftr').click(function(){
		gridInstance.setFilterModel(null);
	});
	
	$("#refresh-notes-btn").click(function() {
		$('#note-search-form #isImportant').val("");
		getNoteGridData();
	});
	$("#notes-export-btn").click(function () {
	    gridInstance.exportDataAsCsv();
	});
	
	$("#note-search-btn").click(function() {
		getNoteGridData();
	});

	$(".filter-notes").click(function(event) {
		event.preventDefault();

		$("#notes-grid-header-title").html(DOMPurify.sanitize($(this).attr("data-searchTypeLabel")));
		//alert($(this).attr("data-searchTypeLabel"));
		$("#searchForm input[name=searchType]").val($(this).attr("data-searchType"));

		getNoteGridData();
	});

	$("#get-all-notes-btn").click(function(event) {
		event.preventDefault();

		var selectedData = gridInstance.getSelectedRows();
		if (selectedData.length > 0) {
			var selectedNoteIds = "";
			for (i = 0; i < selectedData.length; i++) {
				var data = selectedData[i];
				selectedNoteIds += data.noteId + ",";
			}
			selectedNoteIds = selectedNoteIds.substring(0, selectedNoteIds.length - 1);
			$("#noteIdsList").val(selectedNoteIds);
		}else{
			$("#noteIdsList").val("");
		}
		$("#ViewNotesForm").submit();
	});

	$("#notes-remove-btn").click(function() {
		var currentStatusId = $("#currentStatusId").val();
		var flag = true;
		if (currentStatusId != null && currentStatusId == "IA_MCOMPLETED") {
			flag = false;
		}
		if (flag) {
			var selectedData = gridInstance.getSelectedRows();
			if (selectedData.length > 0) {

				console.log(selectedData);

				var selectedNoteIds = "";
				for (i = 0; i < selectedData.length; i++) {
					var data = selectedData[i];
					selectedNoteIds += data.noteId + ",";
				}
				selectedNoteIds = selectedNoteIds.substring(0, selectedNoteIds.length - 1);

				var inputData = {
					"selectedNoteIds": selectedNoteIds
				};

				$.ajax({
					type: "POST",
					url: "/common-portal/control/removeNoteData",
					async: true,
					data: inputData,
					success: function(result) {
						if (result.code == 200) {
							showAlert("success", "Successfully removed note# " + selectedNoteIds);
							getNoteGridData();
						} else {
							showAlert("error", data.message);
						}
					},
					error: function() {
						console.log('Error occured');
						showAlert("error", "Error occured!");
					},
					complete: function() {}
				});

			} else {
				showAlert("error", "Please select atleast one row to be removed!");
			}
		}
	});

	$(document).ready(function() {
		$("#noteType").change(function() {
			if($("#noteType").val()!=""){
				$('#noteType_error').hide();
			}else{
				$('#noteType_error').show();
			}
		});
		$("#noteName").change(function() {
			$('#noteName_error').hide();
		});
		$(".note-editable").keyup(function(){
			if($("#note").val() !=""){
				$("#note_error").hide();
			}else{
				$('#note_error').show();
			}
		});
	});
	$('#add-note-form').on('submit', function(e) {
		var callListNoteType = document.getElementById("callListNoteType");
		var hideNoteDescription = $("#hideNoteDescription").val();
		var selectedValue = "";
		if(callListNoteType){
			selectedValue = callListNoteType;
		} else{
			var type = document.getElementById("noteType");
			var selectedText = type.options[type.selectedIndex].innerHTML;
			selectedValue = type.value;
		}
		if (selectedValue == "") {
			$("#noteType_error").html("Please select the Note Category.");
			$('#noteType_error').show();
			e.preventDefault();
		} else {
			$('#noteType_error').hide();
		}
		if ($("#noteName").val() == "") {
			e.preventDefault();
			$('#noteName_error').show();
		} else {
			$('#noteName_error').hide();
		}
		if (hideNoteDescription === "Y") {
			var noteContent = $('#note').summernote('code');
			var textContent = $('<div>').html(DOMPurify.sanitize(noteContent)).text();
			var noteNameValue = textContent.substring(0, 15);
			$("#noteName").val(noteNameValue);
		}
		if ($('#note').summernote('isEmpty')  && hideNoteDescription!="Y") {
			e.preventDefault();
			$("#note_error").html("Please fill the Description.");
			$('#note_error').show();
		} else {
			$('#note_error').hide();
			if ($("#noteName").val() != "" && selectedValue != "") {
				e.preventDefault();
				var action = "createNoteData";
				if ($('#noteId').val()) {
					action = "updateNoteData";
				}
				$.post("/common-portal/control/" + action, $('#add-note-form').serialize(), function(data) {
					if (data.code == 200) {
						showAlert("success", data.message);
						$("#create-note-modal").modal('hide');
						getNoteGridData();
					} else {
						showAlert("error", data.message);
					}
				});
			}
		}
	});

	$("#remove-note-btn").click(function() {
		getNoteGridData();
	});

});

/*
fagReady("NOTES", function(el, api, colApi, gridApi) {
	$("#notes-refresh-pref-btn").click(function() {
		gridApi.refreshUserPreferences();
		$("#note-search-form #isImportant").val("");
		loadNotesGrid(gridApi, api, colApi);
	});
	$("#notes-save-pref-btn").click(function() {
		gridApi.saveUserPreferences();
	});
	$("#notes-clear-filter-btn").click(function() {
		try {
			gridApi.clearAllColumnFilters();
		} catch (e) {}
		gridApi.refreshUserPreferences();
	});
	$("#note-sub-filter-clear-btn").click(function() {
		try {
			gridApi.clearAllColumnFilters();
		} catch (e) {}
	});
	$("#notes-export-btn").click(function() {
		gridApi.csvExport();
	});

	$("#refresh-notes-btn").click(function() {
		$('#note-search-form #isImportant').val("");
		loadNotesGrid(gridApi, api, colApi);
	});

	$("#note-search-btn").click(function() {
		loadNotesGrid(gridApi, api, colApi);
	});

	$(".filter-notes").click(function(event) {
		event.preventDefault();

		$("#notes-grid-header-title").html($(this).attr("data-searchTypeLabel"));
		//alert($(this).attr("data-searchTypeLabel"));
		$("#searchForm input[name=searchType]").val($(this).attr("data-searchType"));

		loadNotesGrid(gridApi, api, colApi);
	});

	$("#get-all-notes-btn").click(function(event) {
		event.preventDefault();

		var selectedData = api.getSelectedRows();
		if (selectedData.length > 0) {
			var selectedNoteIds = "";
			for (i = 0; i < selectedData.length; i++) {
				var data = selectedData[i];
				selectedNoteIds += data.noteId + ",";
			}
			selectedNoteIds = selectedNoteIds.substring(0, selectedNoteIds.length - 1);
			$("#noteIdsList").val(selectedNoteIds);
		}else{
			$("#noteIdsList").val("");
		}
		$("#ViewNotesForm").submit();
	});

	$("#notes-remove-btn").click(function() {
		var currentStatusId = $("#currentStatusId").val();
		var flag = true;
		if (currentStatusId != null && currentStatusId == "IA_MCOMPLETED") {
			flag = false;
		}
		if (flag) {
			var selectedData = api.getSelectedRows();
			if (selectedData.length > 0) {

				console.log(selectedData);

				var selectedNoteIds = "";
				for (i = 0; i < selectedData.length; i++) {
					var data = selectedData[i];
					selectedNoteIds += data.noteId + ",";
				}
				selectedNoteIds = selectedNoteIds.substring(0, selectedNoteIds.length - 1);

				var inputData = {
					"selectedNoteIds": selectedNoteIds
				};

				$.ajax({
					type: "POST",
					url: "/common-portal/control/removeNoteData",
					async: true,
					data: inputData,
					success: function(result) {
						if (result.code == 200) {
							showAlert("success", "Successfully removed note# " + selectedNoteIds);
							loadNotesGrid(gridApi, api, colApi);
						} else {
							showAlert("error", data.message);
						}
					},
					error: function() {
						console.log('Error occured');
						showAlert("error", "Error occured!");
					},
					complete: function() {}
				});

			} else {
				showAlert("error", "Please select atleast one row to be removed!");
			}
		}
	});

	$(document).ready(function() {
		$("#noteType").change(function() {
			if($("#noteType").val()!=""){
				$('#noteType_error').hide();
			}else{
				$('#noteType_error').show();
			}
		});
		$("#noteName").change(function() {
			$('#noteName_error').hide();
		});
		$(".note-editable").keyup(function(){
			if($("#note").val() !=""){
				$("#note_error").hide();
			}else{
				$('#note_error').show();
			}
		});
	});
	$('#add-note-form').on('submit', function(e) {
		var callListNoteType = document.getElementById("callListNoteType");
		var hideNoteDescription = $("#hideNoteDescription").val();
		var selectedValue = "";
		if(callListNoteType){
			selectedValue = callListNoteType;
		} else{
			var type = document.getElementById("noteType");
			var selectedText = type.options[type.selectedIndex].innerHTML;
			selectedValue = type.value;
		}
		if (selectedValue == "") {
			$("#noteType_error").html("Please select the Note Category.");
			$('#noteType_error').show();
			e.preventDefault();
		} else {
			$('#noteType_error').hide();
		}
		if ($("#noteName").val() == "") {
			e.preventDefault();
			$('#noteName_error').show();
		} else {
			$('#noteName_error').hide();
		}
		if (hideNoteDescription === "Y") {
			var noteContent = $('#note').summernote('code');
			var textContent = $('<div>').html(noteContent).text();
			var noteNameValue = textContent.substring(0, 15);
			$("#noteName").val(noteNameValue);
		}
		if ($('#note').summernote('isEmpty')  && hideNoteDescription!="Y") {
			e.preventDefault();
			$("#note_error").html("Please fill the Description.");
			$('#note_error').show();
		} else {
			$('#note_error').hide();
			if ($("#noteName").val() != "" && selectedValue != "") {
				e.preventDefault();
				var action = "createNoteData";
				if ($('#noteId').val()) {
					action = "updateNoteData";
				}
				$.post("/common-portal/control/" + action, $('#add-note-form').serialize(), function(data) {
					if (data.code == 200) {
						showAlert("success", data.message);
						$("#create-note-modal").modal('hide');
						loadNotesGrid(gridApi, api, colApi);
					} else {
						showAlert("error", data.message);
					}
				});
			}
		}
	});

	$("#remove-note-btn").click(function() {
		loadNotesGrid(gridApi, api, colApi);
	});

	postLoadGrid(api, gridApi, colApi, "sr-notes", loadNotesGrid);
	postLoadGrid(api, gridApi, colApi, "a-note", loadNotesGrid);
	postLoadGrid(api, gridApi, colApi, "lead-notes", loadNotesGrid);
	postLoadGrid(api, gridApi, colApi, "contact-notes", loadNotesGrid);
	postLoadGrid(api, gridApi, colApi, "c-notes", loadNotesGrid);
	postLoadGrid(api, gridApi, colApi, "act-notes", loadNotesGrid);
	postLoadGrid(api, gridApi, colApi, "opportunity-notes", loadNotesGrid);

	//loadNotesGrid(api, gridApi);
});

function loadNotesGrid(gridApi, api, colApi) {
	var rowData = [];
	gridApi.setRowData(rowData);
	api.showLoadingOverlay();
	$.ajax({
		async: false,
		url: '/common-portal/control/searchNotes',
		type: "POST",
		data: JSON.parse(JSON.stringify($("#note-search-form").serialize())),
		success: function(data) {
			if (data) {
				gridApi.setRowData(data.data);
				setTimeout(() => colApi.autoSizeAllColumns(), 1000);
				const $field = $('#10242').find('span');
				if(data.importantNotesCount != null && data.importantNotesCount > 0){
					$field.attr('badge', data.importantNotesCount);
				}else{
					$field.attr('badge', 0);
				}
			}
		}
	});
}
*/

function viewNoteInfo(noteId) {
	getNoteInfo(noteId);
	$('#modalContactView').modal("show");

}

function removeNote(noteId) {
    var message = $(this).data('message');
    if (!$.trim(message)) {
        message = "Are you sure? Do you want to delete";
    }
    var inputData = {
        "selectedNoteIds": noteId
    };
    bootbox.confirm(message, function (result) {
        if (result) {
            // Define the 'href' variable or remove this line if not notifyClosedFnneeded.
            var href = "/common-portal/control/removeNoteData";
            // AJAX request to remove the note
            $.ajax({
                type: "POST",
                url: href,
                async: true,
                data: inputData,
                success: function (result) {
                    if (result.code == 200) {
                        showAlert("success", "Successfully removed note# " + noteId);
                        $("#remove-note-btn").trigger('click');
                    } else {
                        showAlert("error", result.message);
                    }
                },
                error: function () {
                    console.log('Error occurred');
                    showAlert("error", "Error occurred!");
                },
                complete: function () {}
            });
        }
    });
}

function setGridData1(gridInstance, callCtxObj) {
    return new Promise((resolve, reject) => {
        let ajaxUrl = callCtxObj.ajaxUrl;
        let formId = callCtxObj.formId;
        let key = callCtxObj.ajaxResponseKey;
        let responseData = {};

        var parameters = "";
        var formData = $(formId + ' :input').serializeArray();
        if (formData) {
            // Filter out inputs with empty values
            formData = formData.filter(function (input) {
                return input.value.trim() !== '';
            });
            parameters = $.param(formData);
        }

        if (parameters == null || parameters == '' || parameters == 'undefined') {
            setRowData(gridInstance, []);
            resolve(responseData);
        }
        
        if (ajaxUrl) {
            var formParam = JSON.parse(JSON.stringify($(formId).serialize()));
            if (!formParam) formParam = {};
            $.ajax({
                type: "POST",
                async: true,
                url: ajaxUrl,
                data: formParam,
                success: function (data) {
                    if (data) {
                        if (key) {
                            responseData = data;
                            result = data[key];
                            removeElementByKey(responseData, key);
                        } else {
                            result = data;
                        }
                        setRowData(gridInstance, result && result.length > 0 ? result : []);
                        resolve(responseData);
                    }
                },
                error: function (error) {
                    reject(error);
                }
            });
        }
    });
}
function description(params) {
	return `<a href="#" onclick="viewNoteInfo('${params.data.noteId}')" class="btn btn-xs btn-primary m5 tooltips view-contactMember">Click Here </a>`;
}
function edit(params) { 
	if (params.data.srStatusId != null && params.data.srStatusId != "" && (params.data.srStatusId == "SR_CLOSED" || params.data.srStatusId == "SR_CANCELLED")) { 
		return '' 
	}else if(params.data.noteType == "EV Type"){ 
		return '' 
	} else if (params.data.partyIdStatus == "PARTY_DISABLED") {
		return '' 
	} else {
		if (params.data.loginUser == params.data.createdByUserLogin) {
			return '<span class="fa fa-edit btn btn-xs btn-primary edit-note" title="Edit" onclick="editNote(\'' + params.data.noteId + '\')"></span>'; 
		} else { 
			return ''; 
		} 
	} 
}
function noteIdParams(params) {
	if (params.data.campaignListId!= null ) {
		return '<a target="_blank" href="noteDataDetails?noteId=' + params.data.noteId +'&domainEntityId='+ params.data.domainEntityId + '&domainEntityType=' + params.data.domainEntityType + '&' + 'noteName=' + params.data.noteName + '&' + 'partyId=' + params.data.partyId + '&'+'campaignListId='+params.data.campaignListId+'">' + params.data.noteId + '</a>' 
	}else {
		return '<a target="_blank" href="noteDataDetails?noteId=' + params.data.noteId +'&domainEntityId='+ params.data.domainEntityId + '&' + 'domainEntityType=' + params.data.domainEntityType + '&' + 'noteName=' + params.data.noteName + '&' + 'partyId=' + params.data.partyId + '">' + params.data.noteId + '</a>'
	} 
} 
function remove(params) { 
	if (params.data.srStatusId != null && params.data.srStatusId != "" && (params.data.srStatusId == "SR_CLOSED" || params.data.srStatusId == "SR_CANCELLED")) {
		return '' 
	} else if (params.data.partyIdStatus == "PARTY_DISABLED") {
		return '' 
	} else {
		if (params.data.loginUser == params.data.createdByUserLogin) {
			return '<span class="fa fa-trash btn btn-xs btn-primary remove-note" title="Remove" onclick="removeNote(\'' + params.data.noteId + '\')"></span>'; 
		} else {
			return ''; 
		} 
	} 
}