//fagReady("ACTIVITY_ATTACHMENTS", function(el, api, colApi, gridApi){
//	
//    $("#attachment-refresh-pref-btn").click(function () {
//    	gridApi.refreshUserPreferences();
//    });
//    $("#attachment-save-pref-btn").click(function () {
//    	gridApi.saveUserPreferences();
//    });
//    $("#attachment-clear-filter-btn").click(function () {
//    	try{
//    		gridApi.clearAllColumnFilters();
//    	}catch(e){
//    	}
//    	gridApi.refreshUserPreferences();
//    });
//	$("#sub-filter-clear-btn").click(function () {
//    	try{
//    		gridApi.clearAllColumnFilters();
//    	}catch(e){
//    	}
//    });
//    $("#attachment-export-btn").click(function () {
//    	gridApi.csvExport();
//    });
//
//    $("#refresh-attachment-btn").click(function () {
//    	loadAttachmentGrid(gridApi,api);
//    });
//    
//    $("#refresh-attachment-btn").on('click', function() {
//    	loadAttachmentGrid(gridApi,api);
//    });
//    
//    $(".filter-attachment").click(function(event) {
//        event.preventDefault(); 
//        
//        $("#attachment-grid-header-title").html($(this).attr("data-searchTypeLabel"));
//        alert($(this).attr("data-searchTypeLabel"));
//        $("#searchForm input[name=searchType]").val($(this).attr("data-searchType"));
//        
//        loadAttachmentGrid(gridApi,api);
//    }); 
//    
//    //remove attachment
//    $('#remove-attachment-btn').on('click', function(e) {
//    	var currentStatusId = $("#currentStatusId").val();
//    	var flag = true;
//    	if(currentStatusId != null && currentStatusId =="IA_MCOMPLETED"){
//    		flag = false;
//    	}
//    	if(flag){
//    		var selectedRows = api.getSelectedRows();
//        	if(selectedRows!= undefined && selectedRows != null && selectedRows.length>0){
//        		
//        		gridApi.removeSelected();
//        		//showAlert ("success", "");
//    	    } else {
//    	    	showAlert("error","Please select atleast one record in the list")
//    	    }
//        	setTimeout(() => { loadAttachmentGrid(gridApi, api); }, 1000);
//    	}
//    });
//   
//   
//    
//    
//    document.getElementById('uploadFile_error').innerHTML = "";
//    $("#add-attachment-form1").validate({
//           rules:{
//               uploadFile:{
//                   required:true ,
//                   uploadFile:true,  
//               }
//           }
//       });
//       $("#uploadFile").change(function(){
//           $("#uploadFile").blur().focus();
//        	 document.getElementById('uploadFile_error').innerHTML = "";
//       });
//   function validateUploadFile(){
//
//       if($('#uploadFile')[0].files.length === 0){
//          alert("Attachment is Required");
//           document.getElementById('uploadFile_error').innerHTML = " Attachment is Required*";
//           $('#uploadFile').focus();
//
//           return false;
//       }else{
//       
//       		const fi = document.getElementById('uploadFile');
//            var fileLimitsVal = $("#fileLimits").val();
//            var fileLimits = parseInt(fileLimitsVal);
//			//var fi = document.getElementById('uploadFile');
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
//   	    
//        document.getElementById('uploadFile_error').innerHTML = "";
//         return true;
//       }
//   }
//
//   $('#add-attachment-form-submit').on('click', function (e) {
//   		//var partyId= $("#add-attachment-form1 input[name=partyId]").val();
//   		//console.log("partyId-------------"+partyId);
//   		var isvalid=validateUploadFile();
//   		if(isvalid){
//			var formInput = new FormData();
//			var files = $('#uploadFile')[0].files[0];
//			const fi = document.getElementById('uploadFile');
//			for (var i = 0; i <= fi.files.length - 1; i++) {
//				var files = $('#uploadFile')[0].files[i];
//				formInput.append('file['+i+']', files);
//			}
//           /*var files = $('#uploadFile')[0].files[0];
//           formInput.append('file',files);*/
//			formInput.append('classificationEnumId',$("#classificationEnumId").val());
//			formInput.append('attachmentDescription',$("#add-attachment-form1 #attachmentDescription").val());
//			formInput.append('partyId',$("#add-attachment-form1 #partyId").val());
//			formInput.append('path',$("#add-attachment-form1 #path").val());
//			formInput.append('domainEntityType',$("#add-attachment-form1 #domainEntityType").val());
//			formInput.append('domainEntityId',$("#add-attachment-form1 #domainEntityId").val());
//			formInput.append('workEffortId',$("#add-attachment-form1 #workEffortId").val());
//			formInput.append('globalPathName',$("#add-attachment-form1 #globalPathName").val());
//			formInput.append('linkedFrom',$("#add-attachment-form1 #linkedFrom").val());
//			formInput.append('publicOrPrivate',$("#attachmentType").val());
//   			$.ajax({
//	           type: "POST",
//	           processData: false, 
//	           contentType: false,
//	           url: "/common-portal/control/createattachmentData",
//	           data: formInput,
//	           sync: true,
//	           success: function(data) {
//					$("#create-act-attachment").modal('hide');
//					$('.clear').click();
//					document.getElementById('publicOrPrivateAtt').checked = true;
//					$("#add-attachment-form1 #uploadFile").val("");
//					$("#add-attachment-form1 #attachmentDescription").val("");	
//					loadAttachmentGrid(gridApi,api); 
//					showAlert ("success", "Successfully created attachment");
//           		}
//   			});
//        }
//   		e.preventDefault();
//   	});
//    //add bookmark
//   function validateUrl(){
//
//	    if($('#url').val()==""||$('#url').val()=="undefined"){
//	        //alert("Bookmark Url is Required");
//	        document.getElementById('url_error').innerHTML = " Bookmark Url is Required *";
//	        $('#url').focus();
//
//	        return false;
//	    }else{
//	    	var urlVal = $('#url').val();
//		    if (urlVal && !urlVal.match(/^http([s]?):\/\/.*/)) {
//		    	$("#url").val('http://' + urlVal);
//		  	}
//	      document.getElementById('url_error').innerHTML = "";
//	      return true;
//	    }
//	}
//   $('#add-bookmark-form-submit').on('click', function (e) {
//		var partyId= $("#bookmark #partyId").val();
//		var domainEntityType= $("#bookmark #domainEntityType").val();
//		var domainEntityId= $("#bookmark #domainEntityId").val();
//		
//		console.log("partyId----"+partyId);
//			var isValid=validateUrl();
//			if(isValid){
//		$.ajax({
//       type: "POST",
//       url: "createattachmentData",
//       data: { "classificationEnumId":$("#classificationEnumId1").val(),"url":$("#url").val(),"domainEntityType":domainEntityType,"domainEntityId":domainEntityId,"attachmentDescription":$("#attachmentDescription1").val(), "partyId": partyId,"workEffortId":$("#workEffortId").val(),"linkedFrom":$("#linkedFrom").val()},
//       //data: { "classificationEnumId":$("#classificationEnumId1").val(),"url":$("#url").val(),"domainEntityType":domainEntityType,"domainEntityId":domainEntityId,"attachmentDescription":$("#attachmentDescription1").val(), "partyId": partyId,"workEffortId":$("#workEffortId").val(),"linkedFrom":$("#linkedFrom").val(),"helpfulLink":$("input[name='helpfulLink']:checked").val()},
//       sync: true,
//       success: function(data) {
//			showAlert ("success", "Sucessfully created Bookmark");
//			$('#create-bookmark-modal').modal('hide');
//			  loadAttachmentGrid(gridApi,api); 
//			  
//			  $("#classificationEnumId1").val("");
//			  $("#classificationEnumId1").dropdown('clear');
//			  $("div.ui.dropdown.search.form-control.fluid.show-tick.classificationEnumId1.selection > i").removeClass("clear");
//			  $("#classificationEnumId1").dropdown('refresh');
//			  $("#url").val("");
//			  $("#attachmentDescription1").val("");
//       }
//
//   });
//   }
//	e.preventDefault();
//});
//    //loadAttachmentGrid(gridApi,api);
//   postLoadGrid(api, gridApi, null, "act-attachments", loadAttachmentGrid);
//});

//function loadAttachmentGrid(gridApi,api) {
//	var rowData =[];
//	
//	gridApi.setRowData(rowData);
//	$.ajax({
//	  async: false,
//	  url:'/common-portal/control/searchattachments',
//	  type:"POST",
//	  data: JSON.parse(JSON.stringify($("#attachment-search-form").serialize())),
//	  success: function(data){
//		  gridApi.setRowData(data.data);
//	  }
//	});
//}
function downloadActAttachment(contentId) {
	window.location='/common-portal/control/downloadPartyContent?contentId=' + contentId + '&externalLoginKey=' + $("#attachment-search-form #externalLoginKey").val();
}
function previewImage(imageEncoded){
	$("#preview-image").attr('src', imageEncoded);
	$('#img-preview').modal("show");
}

$(function() {
	let attachmentInstanceId= "ACTIVITY_ATTACHMENTS";
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

    $("#refresh-attachment-btn").on('click', function() {
        getAttachmentGridData();
    });
	$("#attachment-list-export-btn").click(function() {
		gridInstance.exportDataAsCsv();
	});
    $(".filter-attachment").click(function(event) {
        event.preventDefault(); 
        
        $("#attachment-grid-header-title").html(DOMPurify.sanitize($(this).attr("data-searchTypeLabel")));
        alert($(this).attr("data-searchTypeLabel"));
        $("#searchForm input[name=searchType]").val($(this).attr("data-searchType"));
        
		getAttachmentGridData();
    }); 
    //remove attachment
    $('#remove-attachment-btn').on('click', function(e) {
        e.preventDefault();
        var currentStatusId = $("#currentStatusId").val();
    	var flag = true;
    	if(currentStatusId != null && currentStatusId =="IA_MCOMPLETED"){
    		flag = false;
    	}
    	if(flag){
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
    	}
    });
    
    document.getElementById('uploadFile_error').innerHTML = "";
    $("#add-attachment-form1").validate({
           rules:{
               uploadFile:{
                   required:true ,
                   uploadFile:true,  
               }
           }
       });
       $("#uploadFile").change(function(){
           $("#uploadFile").blur().focus();
        	 document.getElementById('uploadFile_error').innerHTML = "";
       });
   function validateUploadFile(){

       if($('#uploadFile')[0].files.length === 0){
          alert("Attachment is Required");
           document.getElementById('uploadFile_error').innerHTML = " Attachment is Required*";
           $('#uploadFile').focus();

           return false;
       }else{
       
       		const fi = document.getElementById('uploadFile');
            var fileLimitsVal = $("#fileLimits").val();
            var fileLimits = parseInt(fileLimitsVal);
			//var fi = document.getElementById('uploadFile');
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

   $('#add-attachment-form-submit').on('click', function (e) {
   		//var partyId= $("#add-attachment-form1 input[name=partyId]").val();
   		//console.log("partyId-------------"+partyId);
   		var isvalid=validateUploadFile();
   		if(isvalid){
			var formInput = new FormData();
			var files = $('#uploadFile')[0].files[0];
			const fi = document.getElementById('uploadFile');
			for (var i = 0; i <= fi.files.length - 1; i++) {
				var files = $('#uploadFile')[0].files[i];
				formInput.append('file['+i+']', files);
			}
           /*var files = $('#uploadFile')[0].files[0];
           formInput.append('file',files);*/
			formInput.append('classificationEnumId',$("#classificationEnumId").val());
			formInput.append('attachmentDescription',$("#add-attachment-form1 #attachmentDescription").val());
			formInput.append('partyId',$("#add-attachment-form1 #partyId").val());
			formInput.append('path',$("#add-attachment-form1 #path").val());
			formInput.append('domainEntityType',$("#add-attachment-form1 #domainEntityType").val());
			formInput.append('domainEntityId',$("#add-attachment-form1 #domainEntityId").val());
			formInput.append('workEffortId',$("#add-attachment-form1 #workEffortId").val());
			formInput.append('globalPathName',$("#add-attachment-form1 #globalPathName").val());
			formInput.append('linkedFrom',$("#add-attachment-form1 #linkedFrom").val());
			formInput.append('publicOrPrivate',$("#attachmentType").val());
   			$.ajax({
	           type: "POST",
	           processData: false, 
	           contentType: false,
	           url: "/common-portal/control/createattachmentData",
	           data: formInput,
	           sync: true,
	           success: function(data) {
					$("#create-act-attachment").modal('hide');
					$('.clear').click();
					document.getElementById('publicOrPrivateAtt').checked = true;
					$("#add-attachment-form1 #uploadFile").val("");
					$("#add-attachment-form1 #attachmentDescription").val("");	
					getAttachmentGridData();
					showAlert ("success", "Successfully created attachment");
           		}
   			});
        }
   		e.preventDefault();
   	});
    //add bookmark
   function validateUrl(){

	    if($('#url').val()==""||$('#url').val()=="undefined"){
	        //alert("Bookmark Url is Required");
	        document.getElementById('url_error').innerHTML = " Bookmark Url is Required *";
	        $('#url').focus();

	        return false;
	    }else{
	    	var urlVal = $('#url').val();
		    if (urlVal && !urlVal.match(/^http([s]?):\/\/.*/)) {
		    	$("#url").val('http://' + urlVal);
		  	}
	      document.getElementById('url_error').innerHTML = "";
	      return true;
	    }
	}
   $('#add-bookmark-form-submit').on('click', function (e) {
		var partyId= $("#bookmark #partyId").val();
		var domainEntityType= $("#bookmark #domainEntityType").val();
		var domainEntityId= $("#bookmark #domainEntityId").val();
		
		console.log("partyId----"+partyId);
			var isValid=validateUrl();
			if(isValid){
		$.ajax({
       type: "POST",
       url: "createattachmentData",
       data: { "classificationEnumId":$("#classificationEnumId1").val(),"url":$("#url").val(),"domainEntityType":domainEntityType,"domainEntityId":domainEntityId,"attachmentDescription":$("#attachmentDescription1").val(), "partyId": partyId,"workEffortId":$("#workEffortId").val(),"linkedFrom":$("#linkedFrom").val()},
       //data: { "classificationEnumId":$("#classificationEnumId1").val(),"url":$("#url").val(),"domainEntityType":domainEntityType,"domainEntityId":domainEntityId,"attachmentDescription":$("#attachmentDescription1").val(), "partyId": partyId,"workEffortId":$("#workEffortId").val(),"linkedFrom":$("#linkedFrom").val(),"helpfulLink":$("input[name='helpfulLink']:checked").val()},
       sync: true,
       success: function(data) {
			showAlert ("success", "Sucessfully created Bookmark");
			$('#create-bookmark-modal').modal('hide');
			getAttachmentGridData();
			  
			  $("#classificationEnumId1").val("");
			  $("#classificationEnumId1").dropdown('clear');
			  $("div.ui.dropdown.search.form-control.fluid.show-tick.classificationEnumId1.selection > i").removeClass("clear");
			  $("#classificationEnumId1").dropdown('refresh');
			  $("#url").val("");
			  $("#attachmentDescription1").val("");
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

/* $('#remove-attachment-btn').on('click', function(e) {
    	var gridOptionsattachment = null;
    		$("#attachment-grid").empty();
    		gridOptionsattachment = {
    			defaultColDef : {
    				filter : true,
    				sortable : true,
    				resizable : true
    			},
    			columnDefs : columnattachmentDefs,
    			floatingFilter : true,
    			rowSelection : "multiple",
    			editType : "fullRow",
    			paginationPageSize : 10,
    			domLayout : "autoHeight",
    			pagination : true,
    			onGridReady : function() {
    				//sizeToFitattachment();
    				loadAttachmentGrid(gridApi);
    			}
    		}

    		// lookup the container we want the Grid to use
    		var eGridDiv = document.querySelector("#attachment-grid");
    		// create the grid passing in the div to use together with the columns &
    		// data we want to use
    		new agGrid.Grid(eGridDiv, gridOptionsattachment);
			e.preventDefault();
			//alert("remove click");
			var selectedData = gridOptionsattachment.api.getSelectedRows();
			if (selectedData.length > 0) {
				console.log(selectedData);
		    var selectedattachmentIds = "";
		    for (i = 0; i < selectedData.length; i++) {
		    	var data = selectedData[i];
		    	selectedattachmentIds += data.contentId+",";
		    }
		    selectedattachmentIds = selectedattachmentIds.substring(0, selectedattachmentIds.length - 1);
		    var inputData = {"selectedattachmentIds": selectedattachmentIds};
		    $.ajax({
				type : "POST",
				url : "/common-portal/control/removeattachmentData",
				async : true,
				data : inputData,
				success : function(result) {
					if (result.code == 200) {
						showAlert ("success", "Successfully removed attachment# "+selectedattachmentIds);
						loadAttachmentGrid(gridApi);
					} else {
						showAlert ("error", data.message);
					}
				},
				error : function() {
					console.log('Error occured');
					showAlert("error", "Error occured!");
				},
				complete : function() {
				}
			});
			
		} else {
			showAlert("error", "Please select atleast one row to be removed!");
		}
		
	});*/

