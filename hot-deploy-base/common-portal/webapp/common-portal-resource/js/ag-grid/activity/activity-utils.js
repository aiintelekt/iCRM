ACTUTIL = window.ACTUTIL || {};

(function(actutil){
	
	actutil.loadSrAssocParties = (custRequestId, fieldId, selectedValue, externalLoginKey) => {
		var nonSelectContent = "<span class='nonselect'>Please Select</span>";
		var nameOptions = '<option value="" data-content="'+nonSelectContent+'" selected="">Please Select</option>';		
				
		$.ajax({
			type: "POST",
	     	url: "/common-portal/control/getSrAssocParties",
	        data: {"srNumber": `${custRequestId}`, "externalLoginKey": `${externalLoginKey}`},
	        async: false,
	        success: function (result) {   
	            if (result.code == 200) {
	            	for (var i = 0; i < result.dataList.length; i++) {
	            		var data = result.dataList[i];
	            		var selected = selectedValue && selectedValue === data.partyId ? 'selected' : '';
	            		nameOptions += '<option value="'+data.partyId+'" '+selected+'>'+data.name+' ('+data.roleTypeDesc+')'+'</option>';
	            	}
	            }
	        }
		});    
		
		$("#"+fieldId).html( DOMPurify.sanitize(nameOptions) );
		$("#"+fieldId).dropdown('refresh');
		
	}
	
	actutil.loadSrAssocPartyEmails = (custRequestId, fieldId, selectedValue, externalLoginKey) => {
		var nonSelectContent = "<span class='nonselect'>Please Select</span>";
		var nameOptions = '<option value="" data-content="'+nonSelectContent+'" selected="">Please Select</option>';		
				
		$.ajax({
			type: "POST",
	     	url: "/common-portal/control/getSrAssocParties",
	        data: {"srNumber": `${custRequestId}`, "externalLoginKey": `${externalLoginKey}`},
	        async: false,
	        success: function (result) {   
	            if (result.code == 200) {
	            	for (var i = 0; i < result.dataList.length; i++) {
	            		var data = result.dataList[i];
	            		if (data.infoString) {
	            			var selected = selectedValue && selectedValue === data.partyId ? 'selected' : '';
		            		nameOptions += '<option value="'+data.infoString+'" '+selected+'>'+data.infoString+' - '+data.name+' ('+data.roleTypeDesc+')'+'</option>';
	            		}
	            	}
	            }
	        }
		});    
		
		$("#"+fieldId).html( DOMPurify.sanitize(nameOptions) );
		$("#"+fieldId).dropdown('refresh');
		
	}
	
	actutil.loadOwners = (activityType, selectedOwnerId, context, externalLoginKey) => {
		
	    var inputData = {};
	    var url = '/common-portal/control/getUsersList';
	    
	    var ownerArr = [];
	    if (selectedOwnerId) {
	    	ownerArr = selectedOwnerId.split(',');
		    for(var i = 0; i < ownerArr.length; i++) {
				ownerArr[i] = ownerArr[i].replace(/^\s*/, "").replace(/\s*$/, "");
			}
	    }
	    
	    if (activityType == 'TASK') {
	    	inputData = $("#tech-cal-search-from").serialize();
	    	url = '/common-portal/control/getTechnicianList';
	    }
	    
	    var roleTypeIds = 'ACT_OWNER';
	    if (context && context.get('roleTypeIds')) {
	    	roleTypeIds = context.get('roleTypeIds');
	    }
	    var techType = "ALL";
	    if (context && context.get('techType')) {
	    	techType = context.get('techType').split(',');
	    	//techType = context.get('techType');
	    }
	    
	    var userOptionList = '<option value=""></option>'; 
	    //'<option value="'+loggedInUserId+'">'+userName+'</option>';
	    $.ajax({
	        type: "GET",
	        url: url+'?roleTypeId='+roleTypeIds+'&isIncludeLoggedInUser=Y&externalLoginKey='+externalLoginKey,
	        async: false,
	        data: JSON.parse(JSON.stringify(inputData)),
	        success: function(data) {
	        	var ownerList = activityType == 'TASK' ? data.techList : data;
	            for (var i = 0; i < ownerList.length; i++) {
	                var tech = ownerList[i];
	                var additionalInfo = tech.techPriorityDesc ? tech.techPriorityDesc : tech.roleDesc;
	                var selected = ownerArr.includes(tech.userLoginId) ? 'selected' : '';
	                var techTypeId = tech.techType ? tech.techType : '';
	                if("ALL"== techType)
	                	userOptionList += '<option value="' + tech.userLoginId + '" '+selected+'>' + tech.userName + ' (' + additionalInfo + ') </option>';
	                else if(techType.includes(techTypeId))
	                	userOptionList += '<option value="' + tech.userLoginId + '" '+selected+'>' + tech.userName + ' (' + additionalInfo + ') </option>';
	            }
	        }
	    });
	    const sanitizeduserOptionList = DOMPurify.sanitize(userOptionList);
	    $("#owner").html(DOMPurify.sanitize(sanitizeduserOptionList));
	}
	
	actutil.loadContacts = (partyId, selectedContactId, fieldId, context, externalLoginKey) => {
		
	    var inputData = {};
	    var url = '/common-portal/control/getPrimaryContacts';
	    inputData.externalLoginKey = externalLoginKey;
	    
	    var optionList = '<option value=""></option>'; 
	    
	    if (partyId) {
	    	inputData.partyId = partyId;
	    }
	    if (context && context.get('isIncludeMainParty')) {
	    	inputData.isIncludeMainParty = context.get('isIncludeMainParty');
	    }
	    
	    if (inputData.partyId) {
	    	 $.ajax({
	 	        type: "GET",
	 	        url: url,
	 	        async: false,
	 	        data: JSON.parse(JSON.stringify(inputData)),
	 	        success: function(data) {
	 	           if (data) {
	 	                if (data.responseMessage == "success") {
	 	                    for (var i = 0; i < data.partyRelContacts.length; i++) {
	 	                        var entry = data.partyRelContacts[i];
	 	                        if (entry.selected != null) {
	 	                        	optionList += '<option value="' + entry.contactId + '" selected>' + entry.name + '</option>';
	 	                        } else {
	 	                        	optionList += '<option value="' + entry.contactId + '">' + entry.name + '</option>';
	 	                        }
	 	                    }
	 	                }
	 	            }
	 	        }
	 	    });
	    }
	    
	    $(`#${fieldId}`).html(DOMPurify.sanitize(optionList));
	}
	
	actutil.loadContactEmails = (partyId, selectedContactId, fieldId, context, externalLoginKey) => {
		
	    var inputData = {};
	    var url = '/common-portal/control/getPrimaryContacts';
	    inputData.externalLoginKey = externalLoginKey;
	    
	    var optionList = '<option value=""></option>'; 
	    
	    if (partyId) {
	    	inputData.partyId = partyId;
	    }
	    if (context && context.get('isLoadEmail')) {
	    	inputData.isLoadEmail = context.get('isLoadEmail');
	    }
	    
	    if (inputData.partyId) {
	    	 $.ajax({
	 	        type: "GET",
	 	        url: url,
	 	        async: false,
	 	        data: JSON.parse(JSON.stringify(inputData)),
	 	        success: function(data) {
	 	           if (data) {
	 	                if (data.responseMessage == "success") {
	 	                    for (var i = 0; i < data.partyRelContacts.length; i++) {
	 	                        var entry = data.partyRelContacts[i];
	 	                        if (entry.selected != null) {
	 	                        	optionList += '<option value="' + entry.email + '" selected>' + entry.email + ' ('+entry.name+')' + '</option>';
	 	                        } else {
	 	                        	optionList += '<option value="' + entry.email + '">' + entry.email + ' ('+entry.name+')' + '</option>';
	 	                        }
	 	                    }
	 	                }
	 	            }
	 	        }
	 	    });
	    }
	    
	    $(`#${fieldId}`).html(DOMPurify.sanitize(optionList));
	}
	
	actutil.loadBusinessUnit = (owner, fieldId, fieldName, context, externalLoginKey) => {
		
	    var inputData = {};
	    var url = 'getBusinessUnitName';
	    inputData.externalLoginKey = externalLoginKey;
	    
	    if (owner) {
	    	inputData.owner = owner
	    }
	    
	    if (owner) {
	    	 $.ajax({
	 	        type: "GET",
	 	        url: url,
	 	        async: false,
	 	        data: JSON.parse(JSON.stringify(inputData)),
	 	        success: function(data) {
	 	        	result = data;
	 	            if (result && result[0] != undefined && result[0].businessId != undefined) {
	 	                $("#"+fieldId).val(result[0].businessId);
	 	                $("#"+fieldName).val(result[0].businessunitName);
	 	            } else {
	 	                $("#"+fieldId).val("");
	 	                $("#"+fieldName).val("");
	 	            }
	 	        }
	 	    });
	    }
	    
	}
	
	actutil.loadPartyTimeZones = (partyId, targetFieldId, context, externalLoginKey) => {
		
	    var inputData = {};
	    var url = '/common-portal/control/getPartyTimeZonesList';
	    inputData.externalLoginKey = externalLoginKey;
	    
	    if (partyId) {
	    	inputData.partyId = partyId;
	    }
	    
	    if (partyId) {
	    	$(`#${targetFieldId}`).dropdown('clear');
	        var nonSelectContent = "<span class='nonselect'>Please Select</span>";
	        var timeZonesOptionList = '<option value="" data-content="' + nonSelectContent + '" selected="">Please Select</option>';
	        var selTimeZoneId = '';

	        $.ajax({
	            type: "GET",
	            url: url,
	            data: JSON.parse(JSON.stringify(inputData)),
	            async: false,
	            success: function (data) {
	                for (var i = 0; i < data.length; i++) {
	                    var entry = data[i];
	                    if (entry.selected) {
	                        selTimeZoneId = entry.timeZoneId;
	                        timeZonesOptionList += '<option value="' + entry.timeZoneId + '" selected="selected" >' + entry.description + '</option>';
	                    } else {
	                        if (selTimeZoneId != undefined && selTimeZoneId != null && selTimeZoneId != "" && i == 0) {

	                        } else {
	                            timeZonesOptionList += '<option value="' + entry.timeZoneId + '">' + entry.description + '</option>';
	                        }
	                    }
	                }
	            }
	        });
	        $(`#${targetFieldId}`).html(DOMPurify.sanitize(timeZonesOptionList));
	        $(`#${targetFieldId}`).dropdown('refresh');
	    }
	    
	}
	
})(ACTUTIL);