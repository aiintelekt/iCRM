CMMUTIL = window.CMMUTIL || {};

(function(cmmutil){
	
	cmmutil.getPartyEmailList = (partyId, fieldId, selectedValue, externalLoginKey) => {
		if (!partyId) {
			return false;
		}
		console.log(`partyId: ${partyId}`);
		let nonSelectContent = "<span class='nonselect'>Please Select</span>";
		let nameOptions = '<option value="" data-content="'+nonSelectContent+'" selected="">Please Select</option>';		
				
		$.ajax({
			type: "POST",
	     	url: "/common-portal/control/getPartyEmailList",
	        data: {"partyId": `${partyId}`, "externalLoginKey": `${externalLoginKey}`},
	        async: false,
	        success: function (result) {   
	            if (result.code == 200) {
	            	for (let i = 0; i < result.dataList.length; i++) {
	            		let data = result.dataList[i];
	            		let selected = selectedValue && selectedValue === data.partyId ? 'selected' : '';
	            		nameOptions += '<option value="'+data.infoString+'" '+selected+'>'+data.infoString+' ('+data.purposeTypeDesc+')'+'</option>';
	            	}
	            }
	        }
		});    
		
		$("#"+fieldId).html( DOMPurify.sanitize(nameOptions) );
		$("#"+fieldId).dropdown('refresh');
		
	}
	
	cmmutil.loadCustomFieldGroup = (groupType, roleTypeId, fieldId, selectedValue, externalLoginKey) => {
		if (!groupType) {
			return false;
		}
		console.log(`groupType: ${groupType}`);
		let nonSelectContent = "<span class='nonselect'>Please Select</span>";
		let nameOptions = '<option value="" data-content="'+nonSelectContent+'" selected="">Please Select</option>';		
				
		$.ajax({
			type: "POST",
	     	url: "/dyna-screen/control/getDynamicData",
	        data : {
				"filterData" : { lookupFieldFilter: '{ 	"entity_name": "CustomFieldGroupSummary", 	"name_field": "groupName", "value_field": "groupId", "order_by": "sequence", "filter_value": {"groupType": "'+groupType+'", "roleTypeId": "'+roleTypeId+'"} }' }
				,"externalLoginKey": `${externalLoginKey}`
			},
	        async: false,
	        success: function (data) {   
	            if (data.code == 200) {
	            	for (let key in data.fieldDataList) {
						if (key) {
							nameOptions += '<option value="'+key+'">'+data.fieldDataList[key]+'</option>';
						}
					}
	            }
	        }
		});    
		
		$("#"+fieldId).html( DOMPurify.sanitize(nameOptions) );
		$("#"+fieldId).dropdown('refresh');
	}
	
	cmmutil.loadSrAssocParties = (custRequestId, fieldId, selectedValue, externalLoginKey) => {
		let nonSelectContent = "<span class='nonselect'>Please Select</span>";
		let nameOptions = '<option value="" data-content="'+nonSelectContent+'" selected="">Please Select</option>';		
				
		$.ajax({
			type: "POST",
	     	url: "/common-portal/control/getSrAssocParties",
	        data: {"srNumber": `${custRequestId}`, "externalLoginKey": `${externalLoginKey}`},
	        async: false,
	        success: function (result) {   
	            if (result.code == 200) {
	            	for (let i = 0; i < result.dataList.length; i++) {
	            		let data = result.dataList[i];
	            		let selected = selectedValue && selectedValue === data.partyId ? 'selected' : '';
	            		nameOptions += '<option value="'+data.partyId+'" '+selected+'>'+data.name+' ('+data.roleTypeDesc+')'+'</option>';
	            	}
	            }
	        }
		});    
		
		$("#"+fieldId).html( DOMPurify.sanitize(nameOptions) );
		$("#"+fieldId).dropdown('refresh');
		
	}
	
	cmmutil.loadSrAssocPartyEmails = (custRequestId, fieldId, selectedValue, externalLoginKey) => {
		let nonSelectContent = "<span class='nonselect'>Please Select</span>";
		let nameOptions = '<option value="" data-content="'+nonSelectContent+'" selected="">Please Select</option>';		
				
		$.ajax({
			type: "POST",
	     	url: "/common-portal/control/getSrAssocParties",
	        data: {"srNumber": `${custRequestId}`, "externalLoginKey": `${externalLoginKey}`},
	        async: false,
	        success: function (result) {   
	            if (result.code == 200) {
	            	for (let i = 0; i < result.dataList.length; i++) {
	            		let data = result.dataList[i];
	            		if (data.infoString) {
	            			let selected = selectedValue && selectedValue === data.partyId ? 'selected' : '';
		            		nameOptions += '<option value="'+data.infoString+'" '+selected+'>'+data.infoString+' - '+data.name+' ('+data.roleTypeDesc+')'+'</option>';
	            		}
	            	}
	            }
	        }
		});    
		
		$("#"+fieldId).html( DOMPurify.sanitize(nameOptions) );
		$("#"+fieldId).dropdown('refresh');
		
	}
	
	cmmutil.loadOwners = (roleTypeIds, selectedOwnerId, context, removeDuplicateUsers, externalLoginKey) => {
		let inputData = {};
		let url = '/common-portal/control/getUsersList';
	    
		let ownerArr = [];
	    if (selectedOwnerId) {
	    	ownerArr = selectedOwnerId.split(',');
		    for(let i = 0; i < ownerArr.length; i++) {
				ownerArr[i] = ownerArr[i].replace(/^\s*/, "").replace(/\s*$/, "");
			}
	    }
	    
	    if (context && context.get('roleTypeIds')) {
	    	roleTypeIds = context.get('roleTypeIds');
	    }
	    let userOptionList = '<option value=""></option>'; 
	    //'<option value="'+loggedInUserId+'">'+userName+'</option>';
	    $.ajax({
	        type: "GET",
	        url: `${url}?roleTypeId=${roleTypeIds}&isIncludeLoggedInUser=Y&externalLoginKey=${externalLoginKey}${removeDuplicateUsers === "Y" ? '&removeDuplicateUsers=Y' : ''}`,
	        async: false,
	        data: JSON.parse(JSON.stringify(inputData)),
	        success: function(data) {
	        	let ownerList = data;
	        	let selected = '';
	            for (let i = 0; i < ownerList.length; i++) {
	                let user = ownerList[i];
	                let additionalInfo = user.roleDesc;
	                selected = ownerArr.includes(user.userLoginId) ? 'selected' : '';
	                userOptionList += '<option value="' + user.userLoginId + '" '+selected+'>' + user.userName + ' (' + additionalInfo + ') </option>';
	            }
	            
	            if (selected) {
	            	$("div.ui.dropdown.search.form-control.fluid.show-tick.owner.selection > i").addClass("clear");
	            }
	        }
	    });
	    $("#owner").html(DOMPurify.sanitize(userOptionList));
	}
	
	cmmutil.loadContacts = (partyId, selectedContactId, fieldId, context, externalLoginKey) => {
		
	    let inputData = {};
	    let url = '/common-portal/control/getPrimaryContacts';
	    inputData.externalLoginKey = externalLoginKey;
	    
	    let optionList = '<option value=""></option>'; 
	    
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
	 	                    for (let i = 0; i < data.partyRelContacts.length; i++) {
	 	                        let entry = data.partyRelContacts[i];
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
	
	cmmutil.loadContactEmails = (partyId, selectedContactId, fieldId, context, externalLoginKey) => {
		
	    let inputData = {};
	    let url = '/common-portal/control/getPrimaryContacts';
	    inputData.externalLoginKey = externalLoginKey;
	    
	    let optionList = '<option value=""></option>'; 
	    
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
	 	                    for (let i = 0; i < data.partyRelContacts.length; i++) {
	 	                        let entry = data.partyRelContacts[i];
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
	
	cmmutil.loadBusinessUnit = (owner, fieldId, fieldName, context, externalLoginKey) => {
		
	    let inputData = {};
	    let url = 'getBusinessUnitName';
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
	
	cmmutil.loadPartyTimeZones = (partyId, targetFieldId, context, externalLoginKey) => {
		
	    let inputData = {};
	    let url = '/common-portal/control/getPartyTimeZonesList';
	    inputData.externalLoginKey = externalLoginKey;
	    
	    if (partyId) {
	    	inputData.partyId = partyId;
	    }
	    
	    if (partyId) {
	    	$(`#${targetFieldId}`).dropdown('clear');
	        let nonSelectContent = "<span class='nonselect'>Please Select</span>";
	        let timeZonesOptionList = '<option value="" data-content="' + nonSelectContent + '" selected="">Please Select</option>';
	        let selTimeZoneId = '';

	        $.ajax({
	            type: "GET",
	            url: url,
	            data: JSON.parse(JSON.stringify(inputData)),
	            async: false,
	            success: function (data) {
	                for (let i = 0; i < data.length; i++) {
	                    let entry = data[i];
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
	
})(CMMUTIL);