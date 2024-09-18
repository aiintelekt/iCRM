fagReady("ATTACHMENTS_THIRDPTY", function(el, api, colApi, gridApi) {
    $("#attachment-thirdpty-refresh-pref-btn").click(function() {
        gridApi.refreshUserPreferences();
        loadThirdPtyAttachmentGrid(gridApi, api, colApi);
    });
    $("#attachment-thirdpty-save-pref-btn").click(function() {
        gridApi.saveUserPreferences();
    });
    $("#attachment-thirdpty-clear-filter-btn").click(function() {
    	try{
    		gridApi.clearAllColumnFilters();
    	}catch(e){
    	}
    	gridApi.refreshUserPreferences();
    });
    $("#attachment-thirdpty-export-btn").click(function() {
        gridApi.csvExport();
    });

    $("#refresh-attachment-thirdpty-btn").click(function() {
        loadThirdPtyAttachmentGrid(gridApi, api, colApi);
    });
    
    //remove attachment
    $('#remove-attachment-thirdpty-btn').on('click', function(e) {
        var selectedRows = api.getSelectedRows();
        if (selectedRows != undefined && selectedRows != null && selectedRows.length > 0) {
            gridApi.removeSelected();
        	
            /*let externalLoginKey = $('#externalLoginKey').val();
        	var inputData = {};
            var url = '/common-portal/control/removeAttachmentData';
            inputData.externalLoginKey = externalLoginKey;
            inputData.externalLoginKey = externalLoginKey;
        	
        	$.ajax({
     	        type: "POST",
     	        url: url,
     	        async: false,
     	        data: JSON.parse(JSON.stringify(inputData)),
     	        success: function(data) {
     	        	if (data.code == 200) {
     	        		console.log('Content association done successfully');
     	        	} else {
     	        		console.log('Failed! Content association: '+data.message);
     	        	}
     	        }
     	    });
        	*/
        	
        	setTimeout(() => {
                loadThirdPtyAttachmentGrid(gridApi, api, colApi);
            }, 1000);
        } else {
            showAlert("error", "Please select atleast one record in the list");
        }
    });

    document.getElementById('uploadFile_thirdpty_error').innerHTML = "";
    $("#add-attachment-thirdpty-form1").validate({
        rules: {
            uploadFile_thirdpty: {
                required: true,
                uploadFile_thirdpty: true,
            }
        }
    });
    $("#uploadFile_thirdpty").change(function() {
        $("#uploadFile_thirdpty").blur().focus();
        document.getElementById('uploadFile_thirdpty_error').innerHTML = "";
    });

    function validateUploadFile() {
        if ($('#uploadFile_thirdpty')[0].files.length === 0) {
            alert("Attachment is Required");
            document.getElementById('uploadFile_thirdpty_error').innerHTML = " Attachment is Required*";
            $('#uploadFile_thirdpty').focus();

            return false;
        } else {
            const fi = document.getElementById('uploadFile_thirdpty');
            var uploadedFileSize = fi.files.item(0).size;
            var globalFileSizeVal = $("#globalFileSize").val();
            if (globalFileSizeVal && globalFileSizeVal != "") {
                if (uploadedFileSize && uploadedFileSize / 1024 / 1024 > globalFileSizeVal) {
                    document.getElementById('uploadFile_thirdpty_error').innerHTML = " File Size Should Not Exceed " + globalFileSizeVal + " MB";
                    return false;
                }
            }
            document.getElementById('uploadFile_thirdpty_error').innerHTML = "";
            return true;
        }
        
        let classificationEnumId = $('#classificationEnumId').val();
        if (!classificationEnumId) {
        	console.log('Classification is missing> '+classificationEnumId);
        	showAlert("error", "Classification is required!");
        	return false;
        }
        
        let invoiceAmount = $('#invoiceAmount').val();
        if (!invoiceAmount) {
        	console.log('invoiceAmount is missing> '+invoiceAmount);
        	showAlert("error", "Invoice Amount is required!");
        	return false;
        }
    }

    $('#add-attachment-thirdpty-form-submit').on('click', function(e) {
        var isvalid = validateUploadFile();
        if (isvalid) {
            var fd = new FormData();
            var files = $('#uploadFile_thirdpty')[0].files[0];
            fd.append('file', files);
            fd.append('classificationEnumId', $("#add-attachment-thirdpty-form1 #classificationEnumId").val());
            fd.append('attachmentDescription', $("#add-attachment-thirdpty-form1 #attachmentDescription").val());
            fd.append('partyId', $("#add-attachment-thirdpty-form1 #partyId").val());
            fd.append('custRequestId', $("#add-attachment-thirdpty-form1 #custRequestId1").val());
            fd.append('publicOrPrivate', $("#add-attachment-thirdpty-form1 #attachmentType").val());
            fd.append('domainEntityType', $("#add-attachment-thirdpty-form1 #domainEntityType1").val());
            fd.append('domainEntityId', $("#add-attachment-thirdpty-form1 #domainEntityId").val());
            
            console.log('classificationEnumId> '+$("#add-attachment-thirdpty-form1 #classificationEnumId").val());
            console.log('attachmentDescription> '+$("#add-attachment-thirdpty-form1 #attachmentDescription").val());
            console.log('partyId> '+$("#add-attachment-thirdpty-form1 #partyId").val());
            console.log('custRequestId1> '+$("#add-attachment-thirdpty-form1 #custRequestId1").val());
            console.log('attachmentType> '+$("#add-attachment-thirdpty-form1 #attachmentType").val());
            console.log('domainEntityType1> '+$("#add-attachment-thirdpty-form1 #domainEntityType1").val());
            console.log('domainEntityId> '+$("#add-attachment-thirdpty-form1 #domainEntityId").val());
            
            $.ajax({
                type: "POST",
                processData: false,
                contentType: false,
                url: "/common-portal/control/createattachmentData",
                data: fd,
                async: false,
                success: function(data) {
                	if (data.code == 200) {
                        
                        // content association [start]
                        let contentId = data.contentId;
                        let externalLoginKey = $('#externalLoginKey').val();
                        let classificationEnumId = $('#add-attachment-thirdpty-form1 #classificationEnumId').val();
                        let invoiceAmount = $('#add-attachment-thirdpty-form1 #invoiceAmount').val();
                        
                        let callContext = new Map();
                        callContext.set('contentId', contentId);
                        callContext.set('classificationEnumId', classificationEnumId);
                        callContext.set('invoiceAmount', invoiceAmount);
                        callContext.set('isThirdPartyAttachment', 'Y');
                        
                        createContentAssociation(callContext, externalLoginKey);
                        // content association [end]
                        
                        $("#create-attachment-thirdpty-modal").modal('hide');
                		showAlert("success", "Successfully created attachment");
                        var rowData = [];
                        api.showLoadingOverlay();
                        gridApi.setRowData(rowData);
                        loadThirdPtyAttachmentGrid(gridApi, api, colApi);
                        
                	} else {
                		showAlert("error", "Attachment upload failed!");
                	}
                }
            });
            
            
        }
        e.preventDefault();
    });
    
    $("#approval-initiate-btn").click(function () {
    	console.log('initiat approval for 3rd party attachment');
    	var selectedData = api.getSelectedRows();
		if (selectedData.length > 0) {
			console.log(selectedData);
			entry = selectedData[0];
			let contentId = entry.contentId;
			
			let externalLoginKey = $('#externalLoginKey').val();
			let domainEntityType = $("#add-attachment-thirdpty-form1 #domainEntityType1").val();
            let domainEntityId = $("#add-attachment-thirdpty-form1 #domainEntityId").val();
			
            let callContext = new Map();
            callContext.set('contentId', contentId);
            callContext.set('domainEntityType', domainEntityType);
            callContext.set('domainEntityId', domainEntityId);
            
			updateCurrentContentApproval(callContext, externalLoginKey);
			
			$.ajax({
				async : false,
				url : '/approval-portal/control/initiateApprovalProcess',
				type : "POST",
				data : JSON.parse(JSON.stringify($("#approval-initiate-form").serialize())),
				success : function(data) {
					if (data.code == 200) {
	    				showAlert ("success", "Successfully initiate approval process..");
	    				//loadApprovalGrid(gridApi);
	    				
	    				location.reload();
	    			} else {
	    				showAlert ("error", data.message);
	    			}
				}
			});
			
		} else {
			showAlert("error", "Please select attachment!");
		}
		
    });
    
    postLoadGrid(api, gridApi, colApi, "thirdpty-attachment", loadThirdPtyAttachmentGrid);
    
});

const createContentAssociation = (context, externalLoginKey) => {
	console.log('call createContentAssociation');
	var inputData = {};
    var url = '/common-portal/control/createContentAssociation';
    inputData.externalLoginKey = externalLoginKey;
    
    if (context) {
    	inputData.contentId = context.get('contentId');
    	
    	inputData.isThirdPartyAttachment = context.get('isThirdPartyAttachment');
    	inputData.classificationEnumId = context.get('classificationEnumId');
    	inputData.invoiceAmount = context.get('invoiceAmount');
    	
    	$.ajax({
 	        type: "POST",
 	        url: url,
 	        async: false,
 	        data: JSON.parse(JSON.stringify(inputData)),
 	        success: function(data) {
 	        	if (data.code == 200) {
 	        		console.log('Content association done successfully');
 	        	} else {
 	        		console.log('Failed! Content association: '+data.message);
 	        	}
 	        }
 	    });
    }
}

const updateCurrentContentApproval = (context, externalLoginKey) => {
	console.log('call createContentAssociation');
	var inputData = {};
    var url = '/approval-portal/control/updateCurrentContentApproval';
    inputData.externalLoginKey = externalLoginKey;
    
    if (context) {
    	inputData.contentId = context.get('contentId');
    	
    	inputData.domainEntityType = context.get('domainEntityType');
    	inputData.domainEntityId = context.get('domainEntityId');
    	
    	$.ajax({
 	        type: "POST",
 	        url: url,
 	        async: false,
 	        data: JSON.parse(JSON.stringify(inputData)),
 	        success: function(data) {
 	        	if (data.code == 200) {
 	        		console.log('Update current content approval successfully');
 	        	} else {
 	        		console.log('Failed! Update current content approval: '+data.message);
 	        	}
 	        }
 	    });
    }
}

function loadThirdPtyAttachmentGrid(gridApi, api, colApi) {
    var rowData = [];
    api.showLoadingOverlay();
    gridApi.setRowData(rowData);
    $.ajax({
        async: false,
        url: '/common-portal/control/searchattachments?externalLoginKey=' + $("#attachment-thirdpty-search-form #externalLoginKey").val(),
        type: "POST",
        data: JSON.parse(JSON.stringify($("#attachment-thirdpty-search-form").serialize())),
        success: function(data) {
            gridApi.setRowData(data.data);
        }
    });
}

function downloadAttachment(contentId, partyId) {
    window.location = '/common-portal/control/downloadPartyContent?contentId=' + contentId + '&partyId=' + partyId;
}

function previewImage(imageEncoded){
	$("#preview-image").attr('src', imageEncoded);
	$('#img-preview').modal("show");
}