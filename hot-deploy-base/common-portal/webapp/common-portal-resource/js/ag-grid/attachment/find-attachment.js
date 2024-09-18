//fagReady("ATTACHMENTS", function(el, api, colApi, gridApi) {
//    $("#attachment-refresh-pref-btn").click(function() {
//        gridApi.refreshUserPreferences();
//        loadAttachmentGrid(gridApi, api, colApi);
//    });
//    $("#attachment-save-pref-btn").click(function() {
//        gridApi.saveUserPreferences();
//    });
//    $("#attachment-clear-filter-btn").click(function() {
//    	try{
//    		gridApi.clearAllColumnFilters();
//    	}catch(e){
//    	}
//    	gridApi.refreshUserPreferences();
//    });
//	$("#attachment-sub-filter-clear-btn").click(function () {
//    	try{
//    		gridApi.clearAllColumnFilters();
//    	}catch(e){
//    	}
//    });
//    $("#attachment-export-btn").click(function() {
//        gridApi.csvExport();
//    });
//
//    $("#refresh-attachment-btn").click(function() {
//        loadAttachmentGrid(gridApi, api, colApi);
//    });
//
//    $("#refresh-attachment-btn").on('click', function() {
//        loadAttachmentGrid(gridApi, api, colApi);
//    });
//
//    $(".filter-attachment").click(function(event) {
//        event.preventDefault();
//
//        $("#attachment-grid-header-title").html($(this).attr("data-searchTypeLabel"));
//        $("#searchForm input[name=searchType]").val($(this).attr("data-searchType"));
//
//        loadAttachmentGrid(gridApi, api, colApi);
//    });
//
//    //remove attachment
//    $('#remove-attachment-btn').on('click', function(e) {
//        var selectedRows = api.getSelectedRows();
//        if (selectedRows != undefined && selectedRows != null && selectedRows.length > 0) {
//
//            gridApi.removeSelected();
//            setTimeout(() => {
//                loadAttachmentGrid(gridApi, api, colApi);
//            }, 1000);
//        } else {
//            showAlert("error", "Please select atleast one record in the list");
//        }
//    });
//
//    document.getElementById('uploadFile_error').innerHTML = "";
//    $("#add-attachment-form1").validate({
//        rules: {
//            uploadFile: {
//                required: true,
//                uploadFile: true,
//            }
//        }
//    });
//    $("#uploadFile").change(function() {
//        $("#uploadFile").blur().focus();
//        document.getElementById('uploadFile_error').innerHTML = "";
//    });
//
//    function validateUploadFile() {
//
//        if ($('#uploadFile')[0].files.length === 0) {
//            alert("Attachment is Required");
//            document.getElementById('uploadFile_error').innerHTML = " Attachment is Required*";
//            $('#uploadFile').focus();
//
//            return false;
//        } else {
//
//            const fi = document.getElementById('uploadFile');
//            var fileLimitsVal = $("#fileLimits").val();
//            var fileLimits = parseInt(fileLimitsVal);
//			if(fi.files.length > fileLimits){
//				document.getElementById('uploadFile_error').innerHTML = " Please select maximum " + fileLimits + " files";
//                return false;
//			}
//			
//			for (var i = 0; i <= fi.files.length - 1; i++) {
//				var uploadedFileSize = fi.files.item(i).size;
//	            var globalFileSizeVal = $("#globalFileSize").val();
//	            if (globalFileSizeVal && globalFileSizeVal != "") {
//	                if (uploadedFileSize && uploadedFileSize / 1024 / 1024 > globalFileSizeVal) {
//	                    document.getElementById('uploadFile_error').innerHTML = " File Size Should Not Exceed " + globalFileSizeVal + " MB";
//	                    return false;
//	                }
//	            }
//			}
//            document.getElementById('uploadFile_error').innerHTML = "";
//            return true;
//        }
//    }
//
//    $('#add-attachment-form-submit').on('click', function(e) {
//        var isvalid = validateUploadFile();
//        if (isvalid) {
//            var fd = new FormData();
//			const fi = document.getElementById('uploadFile');
//	        for (var i = 0; i <= fi.files.length - 1; i++) {
//				var files = $('#uploadFile')[0].files[i];
//				fd.append('file['+i+']', files);
//			}	
//            fd.append('classificationEnumId', $("#classificationEnumId").val());
//            fd.append('attachmentDescription', $("#attachmentDescription").val());
//            fd.append('partyId', $("#add-attachment-form1 #partyId").val());
//            fd.append('path', $("#path").val());
//            fd.append('salesOpportunityId', $("#salesOpportunityId1").val());
//            fd.append('custRequestId', $("#custRequestId1").val());
//            fd.append('publicOrPrivate', $("#attachmentType").val());
//            fd.append('domainEntityType', $("#add-attachment-form1 #domainEntityType1").val());
//            fd.append('domainEntityId', $("#add-attachment-form1 #domainEntityId").val());
//            $("#create-attachment-modal").modal('hide');
//            $.ajax({
//                type: "POST",
//                processData: false,
//                contentType: false,
//                url: "/common-portal/control/createattachmentData",
//                data: fd,
//                async: true,
//                success: function(data) {
//                    showAlert("success", "Successfully created attachment");
//                    var rowData = [];
//                    api.showLoadingOverlay();
//                    gridApi.setRowData(rowData);
//                    loadAttachmentGrid(gridApi, api, colApi);
//                }
//            });
//        }
//        e.preventDefault();
//    });
//    
//    //add bookmark
//    function validateUrl() {
//        if ($('#url').val() == "" || $('#url').val() == "undefined") {
//            //alert("Bookmark Url is Required");
//            document.getElementById('url_error').innerHTML = " Bookmark Url is Required *";
//            $('#url').focus();
//
//            return false;
//        } else {
//            var urlVal = $('#url').val();
//            if (urlVal && !urlVal.match(/^http([s]?):\/\/.*/)) {
//                $("#url").val('http://' + urlVal);
//            }
//            document.getElementById('url_error').innerHTML = "";
//            return true;
//        }
//    }
//    $('#add-bookmark-form-submit').on('click', function(e) {
//
//        var partyId = $("#bookmark #partyId").val();
//        console.log("partyId----" + partyId);
//        var domainEntityType = $("#domainEntityType").val() ? $("#domainEntityType").val() : $("#domainEntityType1").val();
//        console.log('add bookmark, domainEntityType: '+domainEntityType);
//        var isValid = validateUrl();
//        if (isValid) {
//            $.ajax({
//                type: "POST",
//                url: "createattachmentData",
//                data: {
//                    "classificationEnumId": $("#classificationEnumId1").val(),
//                    "url": $("#url").val(),
//                    "domainEntityType": domainEntityType,
//                    "domainEntityId": $("#domainEntityId").val(),
//                    "attachmentDescription": $("#attachmentDescription1").val(),
//                    "partyId": partyId,
//                    "salesOpportunityId": $("#salesOpportunityId1").val(),
//                    "custRequestId": $("#custRequestId1").val()
//                },
//                sync: true,
//                success: function(data) {
//                    showAlert("success", "Sucessfully created Bookmark");
//                    $('#create-bookmark-modal').modal('hide');
//                    loadAttachmentGrid(gridApi, api, colApi);
//                }
//
//            });
//        }
//        e.preventDefault();
//    });
//
//    postLoadGrid(api, gridApi, colApi, "sr-attachments", loadAttachmentGrid);
//    postLoadGrid(api, gridApi, colApi, "a-attachments", loadAttachmentGrid);
//    postLoadGrid(api, gridApi, colApi, "lead-attachments", loadAttachmentGrid);
//    postLoadGrid(api, gridApi, colApi, "contact-attachments", loadAttachmentGrid);
//    postLoadGrid(api, gridApi, colApi, "c-attachments", loadAttachmentGrid);
//
//    postLoadGrid(api, gridApi, colApi, "act-attachments", loadAttachmentGrid);
//    postLoadGrid(api, gridApi, colApi, "opportunity-attachments", loadAttachmentGrid);
//});

//function loadAttachmentGrid(gridApi, api, colApi) {
//    var rowData = [];
//    api.showLoadingOverlay();
//    gridApi.setRowData(rowData);
//    $.ajax({
//        async: false,
//        url: '/common-portal/control/searchattachments?externalLoginKey=' + $("#attachment-search-form #externalLoginKey").val(),
//        type: "POST",
//        data: JSON.parse(JSON.stringify($("#attachment-search-form").serialize())),
//        success: function(data) {
//            gridApi.setRowData(data.data);
//        }
//    });
//}

function downloadAttachment(contentId, partyId) {
    window.location = '/common-portal/control/downloadPartyContent?contentId=' + contentId + '&partyId=' + partyId;
}

$(function() {
	let attachmentInstanceId= "ATTACHMENTS";
	let gridInstance  = "";
	var externalLoginKey = $('#externalLoginKey').val();
	var userId = $("#userId").val();
    var formName1 = "";

	const formDataObject = {};
	formDataObject.gridInstanceId = attachmentInstanceId;
	formDataObject.externalLoginKey = externalLoginKey;
	formDataObject.userId = userId;	
	
	gridInstance = prepareGridInstance(formDataObject);
	
	$('#attachment-save-pref-btn').click(function(){
		saveGridPreference(gridInstance, attachmentInstanceId, userId);
	});

	$('#attachment-clear-filter-btn').click(function(){
		clearGridPreference(gridInstance, attachmentInstanceId, userId);
		if (gridInstance) {
		    gridInstance.destroy();
		}
		gridInstance = prepareGridInstance(formDataObject);
		if(gridInstance){
			getAttachmentGridData();
		}
	});
	$('#sub-filter-clear-btn').click(function(){
		gridInstance.setFilterModel(null);
	});
	$("#refresh-attachment-btn").click(function() {
        getAttachmentGridData();
    });
    
    $("#attachment-export-btn").click(function () {
	    gridInstance.exportDataAsCsv();
	});
    $(".filter-attachment").click(function(event) {
        event.preventDefault();

        $("#attachment-grid-header-title").html(DOMPurify.sanitize($(this).attr("data-searchTypeLabel")));
        $("#searchForm input[name=searchType]").val($(this).attr("data-searchType"));

        getAttachmentGridData();
    });
    $("#view-all-pics-btn").click(function (event) {
		event.preventDefault();
		var selectedData = gridInstance.getSelectedRows();
		if (selectedData.length > 0) {
			var selectedContentIds = "";
			for (i = 0; i < selectedData.length; i++) {
				var data = selectedData[i];
				selectedContentIds += data.contentId+",";
			}
			selectedContentIds = selectedContentIds.substring(0, selectedContentIds.length - 1);
			$("#ViewPicsForm #contentIdsList").val(selectedContentIds);
		}else{
			$("#ViewPicsForm #contentIdsList").val("");
		}
		$("#ViewPicsForm").submit();
	});
    //remove attachment
    $('#remove-attachment-btn').on('click', function(e) {
        e.preventDefault();
        var selectedData = gridInstance.getSelectedRows();
        var selectedRowData = [];
        if (selectedData && selectedData.length > 0) {
        	 var selectedattachmentIds = "";
 		    for (i = 0; i < selectedData.length; i++) {
 		    	var data = selectedData[i];
 		    	selectedattachmentIds += data.contentId+",";
 		    }
 		    selectedattachmentIds = selectedattachmentIds.substring(0, selectedattachmentIds.length - 1);
 	        var inputData = {
 	               "contentId": selectedattachmentIds
 	           };
            $.ajax({
                type: "POST",
                url: "/common-portal/control/removeattachmentData",
                async: true,
                data: inputData,
                success: function(result) {
                    if (result.responseMessage === "success") {
                        showAlert("success", "Successfully removed attachment");
                        getAttachmentGridData();
                        e.preventDefault();
                    } else {
                        showAlert("error", result.message);
                        e.preventDefault();
                    }
                },
                error: function() {
                    console.log('Error occurred');
                    showAlert("error", "Error occurred!");
                },
                complete: function() {
                }
            });
        } else {
            showAlert("error", "Please select at least one record in the list");
        }
    });


    document.getElementById('uploadFile_error').innerHTML = "";
    $("#add-attachment-form1").validate({
        rules: {
            uploadFile: {
                required: true,
                uploadFile: true,
            }
        }
    });

    function validateUploadFile() {

        if ($('#uploadFile')[0].files.length === 0) {
            alert("Attachment is Required");
            document.getElementById('uploadFile_error').innerHTML = " Attachment is Required*";
            $('#uploadFile').focus();

            return false;
        } else {

            const fi = document.getElementById('uploadFile');
            var fileLimitsVal = $("#fileLimits").val();
            var fileLimits = parseInt(fileLimitsVal);
			if(fi.files.length > fileLimits){
				document.getElementById('uploadFile_error').innerHTML = " Please select maximum " + fileLimits + " files";
                return false;
			}
			
			for (var i = 0; i <= fi.files.length - 1; i++) {
				var uploadedFileSize = fi.files.item(i).size;
	            var globalFileSizeVal = $("#globalFileSize").val();
	            if (globalFileSizeVal && globalFileSizeVal != "") {
	                if (uploadedFileSize && uploadedFileSize / 1024 / 1024 > globalFileSizeVal) {
	                    document.getElementById('uploadFile_error').innerHTML = " File Size Should Not Exceed " + globalFileSizeVal + " MB";
	                    return false;
	                }
	            }
			}
            document.getElementById('uploadFile_error').innerHTML = "";
            return true;
        }
    }

    $('#add-attachment-form-submit').on('click', function(e) {
        var isvalid = validateUploadFile();
        if (isvalid) {
            var fd = new FormData();
			const fi = document.getElementById('uploadFile');
	        for (var i = 0; i <= fi.files.length - 1; i++) {
				var files = $('#uploadFile')[0].files[i];
				fd.append('file['+i+']', files);
			}	
            fd.append('classificationEnumId', $("#classificationEnumId").val());
            fd.append('attachmentDescription', $("#attachmentDescription").val());
            fd.append('partyId', $("#add-attachment-form1 #partyId").val());
            fd.append('path', $("#path").val());
            fd.append('salesOpportunityId', $("#salesOpportunityId1").val());
            fd.append('custRequestId', $("#custRequestId1").val());
            fd.append('publicOrPrivate', $("#attachmentType").val());
            fd.append('domainEntityType', $("#add-attachment-form1 #domainEntityType1").val());
            fd.append('domainEntityId', $("#add-attachment-form1 #domainEntityId").val());
            $("#create-attachment-modal").modal('hide');
            $.ajax({
                type: "POST",
                processData: false,
                contentType: false,
                url: "/common-portal/control/createattachmentData",
                data: fd,
                async: true,
                success: function(data) {
                    showAlert("success", "Successfully created attachment");
                    getAttachmentGridData();
                }
            });
        }
        e.preventDefault();
    });
    
    //add bookmark
    function validateUrl() {
        if ($('#url').val() == "" || $('#url').val() == "undefined") {
            //alert("Bookmark Url is Required");
            document.getElementById('url_error').innerHTML = " Bookmark Url is Required *";
            $('#url').focus();

            return false;
        } else {
            var urlVal = $('#url').val();
            if (urlVal && !urlVal.match(/^http([s]?):\/\/.*/)) {
                $("#url").val('http://' + urlVal);
            }
            document.getElementById('url_error').innerHTML = "";
            return true;
        }
    }
    $('#add-bookmark-form-submit').on('click', function(e) {

        var partyId = $("#bookmark #partyId").val();
        console.log("partyId----" + partyId);
        var domainEntityType = $("#domainEntityType").val() ? $("#domainEntityType").val() : $("#domainEntityType1").val();
        console.log('add bookmark, domainEntityType: '+domainEntityType);
        var isValid = validateUrl();
        if (isValid) {
            $.ajax({
                type: "POST",
                url: "createattachmentData",
                data: {
                    "classificationEnumId": $("#classificationEnumId1").val(),
                    "url": $("#url").val(),
                    "domainEntityType": domainEntityType,
                    "domainEntityId": $("#domainEntityId").val(),
                    "attachmentDescription": $("#attachmentDescription1").val(),
                    "partyId": partyId,
                    "salesOpportunityId": $("#salesOpportunityId1").val(),
                    "custRequestId": $("#custRequestId1").val()
                },
                sync: true,
                success: function(data) {
                    showAlert("success", "Sucessfully created Bookmark");
                    $('#create-bookmark-modal').modal('hide');
                    getAttachmentGridData();
                }

            });
        }
        e.preventDefault();
    });
	function getAttachmentGridData(){
		gridInstance.showLoadingOverlay();

		const callCtx = {};
		callCtx.ajaxUrl = "/common-portal/control/searchattachments";
		callCtx.externalLoginKey = externalLoginKey;
		callCtx.formId = "#attachment-search-form";
		callCtx.ajaxResponseKey = "data";
		
		setGridData(gridInstance, callCtx);
	}
	if(gridInstance){
		getAttachmentGridData();
	}
});

function imageUrlParams(params) { 
	if (params.data.imageUrl != null && params.data.imageUrl != "" && params.data.imageUrl != undefined) {
		var imageUrl = params.data.imageUrl; 
		return `<span id="image-pre" onclick="previewImage('${imageUrl}')"><img border="0" width="50" height="50" src="${imageUrl}"></span>`;
	} else {
		return ''; 
	} 
}
function downloadParams(params) {
	if (params.data.contentType !== null && params.data.contentType !== "" && params.data.contentType !== undefined) {
		var contentType = params.data.contentType;
		if (contentType === "HYPERLINK") {
			return `<a target="_blank" href="${params.data.contentName}">${params.data.contentName}</a>`; 
		} else {
			if (params.data.contentId !== null && params.data.contentId !== "" && params.data.contentId !== undefined) {
				if (params.data.partyId !== null && params.data.partyId !== "" && params.data.partyId !== undefined) {
					return `<span class="fa fa-download btn btn-xs btn-primary" title="Download" onclick="downloadAttachment('${params.data.contentId}', '${params.data.partyId}')"></span>`;
				} else {
					var partyId = null;
					return `<span class="fa fa-download btn btn-xs btn-primary" title="Download" onclick="downloadAttachment('${params.data.contentId}', '${partyId}')"></span>`;
				}
			} else {
				return ''; 
			}
		}
	} else {
		return ''; 
	}
}
function linkedFrom(params) {
	if (params && params.data && params.data.linkedFrom) {
		var linkedFrom = params.data.linkedFrom;
		return `<a target="_blank" href="viewActivity?workEffortId=${encodeURIComponent(linkedFrom)}">${linkedFrom}</a>`;
	} else {
		return '';
	}
}
function nameUrlParams(params) {
	if (params && params.data && params.data.contentType) {
		var contentType = params.data.contentType;
		if (contentType === "HYPERLINK") {
			return `<a target="_blank" href="${params.data.contentName}">${params.data.contentName}</a>`; 
		} else {
			if (params.data.contentName) {
				return params.data.contentName; 
			} else {
				return ''; 
			}
		}
	} else {
		return '';
	}
}