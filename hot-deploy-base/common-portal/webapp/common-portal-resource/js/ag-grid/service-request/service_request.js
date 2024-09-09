function getLocation(){
	var county=$("#countyGeoId").val();
	var state=$("#generalStateProvinceGeoId").val();
	var externalLoginKey = $("#externalLoginKey").val();
	if(!county){
		county=$('#countyVal').val();
	}
	console.log(`county: ${county}, state: ${state}`);
	if(county && state) {
		console.log('calling getLocation');
	   	$.ajax({
			type: "POST",
	     	url: "/sr-portal/control/getLocation?externalLoginKey="+externalLoginKey,
	        data: {"state": state, "county": county},
	        async: false,
	        success: function(data) {
				result=data;
				var prodStoreId = result[0].productStoreId;		
				if(prodStoreId) {
					$("#location").val(prodStoreId);
					
					 $("#location").trigger( "change" );
					 $("div.ui.dropdown.search.form-control.fluid.show-tick.location.selection > i").addClass("clear");
				} else{
					$("#location").dropdown('clear');
				}
			},error: function(data) {
				result=data;
			}
		});
	}
	
}

function loadCategory() {
	var nonSelectContent = "<span class='nonselect'>Please Select</span>";
	//var categoryOptions = '<option value="" data-content="'+nonSelectContent+'" selected="">Please Select</option>';
	var srCategoryId = $("#selectedSrCategoryId").val();
	var srTypeId = $("#srTypeId").val();
	var categoryOptions = '';
	$("#srSubCategoryId").html(DOMPurify.sanitize(categoryOptions)).change();
	$.ajax({
		type: "GET",
		url: "getSrCategory",
		data: { "srTypeId": srTypeId },
		async: false,
		success: function(data) {
			var sourceDesc = data.results;
			for (var i = 0; i < data.length; i++) {
				var category = data[i];
				if(srCategoryId && srCategoryId === category.srCategoryId){
        			categoryOptions += '<option value="'+category.srCategoryId+'" selected="selected" >'+category.srCategoryDesc+'</option>';
        		}else{
					categoryOptions += '<option value="'+category.srCategoryId+'">'+category.srCategoryDesc+'</option>';
        		}
			}
			$("div.ui.dropdown.search.form-control.fluid.show-tick.srCategoryId.selection > i").addClass("clear");
		}
	});
	$("div.ui.dropdown.search.form-control.fluid.show-tick.srSubCategoryId.selection > i").addClass("clear");
	$("#srCategoryId").dropdown('clear');
	$("#srCategoryId").html(DOMPurify.sanitize(categoryOptions));
	$("#srCategoryId").dropdown('refresh');
}

function loadSubCategory(srCategoryId) {
	var nonSelectContent = "<span class='nonselect'>Please Select</span>";
	//var subCategoryOptions = '<option value="" data-content="'+nonSelectContent+'" selected="">Please Select</option>';
	var subCategoryOptions = '';
	var srSubCategoryId = $("#selectedSrSubCategoryId").val();
	$('#srSubCategoryId').empty();
	$('#srSubCategoryId').dropdown('clear');
	if(srCategoryId=="NA"){
		srCategoryId = "";
	}
	$.ajax({
		type: "POST",
		url: "getSrSubCategory",
		data: { "srCategoryId": srCategoryId },
		async: false,
		success: function(data) {
			var sourceDesc = data.results;
			for (var i = 0; i < data.length; i++) {
				var category = data[i];
				if(srSubCategoryId && srSubCategoryId === category.srSubCategoryId){
					 subCategoryOptions += '<option value="'+category.srSubCategoryId+'" selected="selected" >'+category.srSubCategoryDesc+'</option>';
         		}else{
         			subCategoryOptions += '<option value="'+category.srSubCategoryId+'">'+category.srSubCategoryDesc+'</option>';
         		}
			}
		}
	});
	$("div.ui.dropdown.search.form-control.fluid.show-tick.srSubCategoryId.selection > i").addClass("clear");
	$("#srSubCategoryId").html(DOMPurify.sanitize(subCategoryOptions));
	$("#srSubCategoryId").dropdown('refresh');
}

function getUsers() {
	$("#owner").empty();
	var loggedInUserId  = $("#loggedInUserId").val();
	var loggedInUserName  = $("#userName").val();
	var selectedOwnerId  = $("#selectedOwnerId").val();	
	var externalLoginKey = $("#externalLoginKey").val();
	
	var userOptionList = '';
	var flag = true;
	/*if(selectedOwnerId == "" || selectedOwnerId == null){
		userOptionList += '<option value="'+loggedInUserId+'" selected="selected">'+loggedInUserName+'</option>';
	}*/
	// userOptionList = '<option value="'+loggedInUserId+'">'+loggedInUserName+'</option>';
	var externalkey="${externalKeyParam}";
	$.ajax({
		type: "GET",
		url:'/common-portal/control/getUsersList?roleTypeId=SR_OWNER&isIncludeLoggedInUser=Y&isIncludeInactiveUser=N&externalLoginKey='+externalLoginKey,
		async: false,
		success: function(data) {
			for (var i = 0; i < data.length; i++) {
				var type = data[i];
				var op = type.userLoginId;
				if(selectedOwnerId && selectedOwnerId === type.userLoginId){
					userOptionList += '<option value="'+type.userLoginId+'" selected="selected">'+type.userName+'</option>';
				}else if((selectedOwnerId == "" || selectedOwnerId == null) &&   op==loggedInUserId){
					if(flag){
						userOptionList += '<option value="'+type.userLoginId+'" selected="selected">'+type.userName+'</option>';
						flag = false;
					}else if(type.userLoginId != loggedInUserId){
					
						userOptionList += '<option value="'+type.userLoginId+'">'+type.userName+'</option>';
					}
				}else {
					userOptionList += '<option value="'+type.userLoginId+'">'+type.userName+'</option>';
				}
			}
		}
	});
	$("#owner").html(DOMPurify.sanitize(userOptionList));
	$("#owner").dropdown('refresh');
}

function getSalesPerson(){
	$("#salesPerson").empty();
	var loggedInUserId  = $("#loggedInUserId").val();
	var selectedSalesPerson  = $("#selectedSalesPerson").val();	
	var externalLoginKey = $("#externalLoginKey").val();	
	var userOptionList = '<option value="" selected="">Select Sales Person</option>';	
	var externalkey="${externalKeyParam}";
	var flag = true;
	$.ajax({
		type: "GET",
		url:'/common-portal/control/getUsersList?roleTypeId=SALES_REP&isIncludeLoggedInUser=Y&isIncludeInactiveUser=N&externalLoginKey='+externalLoginKey,
		async: false,
		success: function(data) {
			for (var i = 0; i < data.length; i++) {
				var type = data[i];		
				var op = type.userLoginId;
				if(selectedSalesPerson && selectedSalesPerson === type.partyId){
					userOptionList += '<option value="'+type.partyId+'" selected="selected">'+type.userName+'</option>';
					$("div.ui.dropdown.search.form-control.fluid.show-tick.salesPerson.selection > i").addClass("clear");
				} else if((selectedSalesPerson == "" || selectedSalesPerson == null) &&   op==loggedInUserId){
					userOptionList += '<option value="'+type.partyId+'" selected="selected">'+type.userName+'</option>';
					$("div.ui.dropdown.search.form-control.fluid.show-tick.salesPerson.selection > i").addClass("clear");
				}
				else{
					userOptionList += '<option value="'+type.partyId+'">'+type.userName+'</option>';	
				}				
			}
		}
	});
	
	$("#salesPerson").html(DOMPurify.sanitize(userOptionList));
	$("#salesPerson").dropdown('refresh');
	
}


$(function() {
	$('.cNo .picker-window-erase').click(function () {
		$("#ContactID").empty();
		$('#ContactID').dropdown('clear');
	});
	
	$('.contractorId .picker-window-erase').click(function () {
		$('#contractorOffNumber').val("");
		$('#contractorMobileNumber').val("");
		$('#contractorHomeNumber').val("");
		$('#contractorPrimaryEmail').val("");
		
	});

	$('.customerId .picker-window-erase').click(function () {
		$('#homePhoneNumber').val("").change();
		$('#offPhoneNumber').val("").change();
	    $('#mobilePhoneNumber').val("").change();
	    $('#customerPrimaryEmail').val("").change();
	});
	
	var roleVal =$("#role").val();	
	if(roleVal !=null && roleVal != '' && roleVal=="CUSTOMER"){		
		$("#ContactID_label").css("display", "none");
		$("div.ui.dropdown.search.form-control.fluid.show-tick.ContactID").css("display", "none");
	}
});
function getPrimaryContacts(partyId){
	$("#ContactID").empty();
	$('#ContactID').dropdown('clear');
	var primContactName = $("#primContactName").val();
	var primContactId = $("#primContactId").val();
	var nonSelectContent = "<span class='nonselect'>Please Select</span>";
	var dataSourceOptions = '<option value=""></option>';
	var selectedContactId  = $("#selectedContactId").val();
	var externalLoginKey = $("#externalLoginKey").val();
	$("div.ui.dropdown.search.form-control.fluid.show-tick.ContactID.selection > i").removeClass("clear");
	$.ajax({
		type: "POST",
		url: "/common-portal/control/getPrimaryContacts?externalLoginKey="+externalLoginKey,
		data: {"partyId": partyId},
		async: false,
		success: function (data) {  
			if (data) {
				if(data.responseMessage=="success" && data.partyRelContacts.length > 0){
					for (var i = 0; i < data.partyRelContacts.length; i++) {
						var entry = data.partyRelContacts[i];
						
							if(primContactId && (primContactId === entry.contactId || primContactId.includes(entry.contactId))){
		            			dataSourceOptions += '<option value="'+entry.contactId+'" selected>'+entry.name+'</option>';
		            			$("#ContactID_error").html('');
		            		} else if((primContactId == null || primContactId == "" || primContactId == undefined) && entry.statusId == "PARTY_DEFAULT" ){
		            			dataSourceOptions += '<option value="'+entry.contactId+'" selected>'+entry.name+'</option>';
		            			$("#ContactID_error").html('');
								$("#primContactIdForName").val(entry.contactId);
		            		}else{
								
		            			dataSourceOptions += '<option value="'+entry.contactId+'" >'+entry.name+'</option>';
		            		}
						
					}
					
					$("div.ui.dropdown.search.form-control.fluid.show-tick.ContactID.selection > i").addClass("clear");

				} else{
					if(data.length > 0){
						for (var i = 0; i < data.length; i++) {
							var entry = data[i];
							dataSourceOptions += '<option value="'+entry.contactId+'">'+entry.name+'</option>';
						}
						$("div.ui.dropdown.search.form-control.fluid.show-tick.ContactID.selection > i").addClass("clear");
					}
				}
				
			}
		}
			
	});
	
	$("#ContactID").append(DOMPurify.sanitize(dataSourceOptions));
	$("#ContactID").dropdown('refresh');
	$("#ContactID").trigger('change');
	
	// TODO for this call during change dealer FSR address and location being empty: Create FSR
	getOnboardedPrimaryContacts();
	
}
function getHomeOwnerAddress(partyId,contactMechId) {	
	var partyId = partyId;
	var externalLoginKey = $("#externalLoginKey").val();
	$.ajax({
		type: "POST",
		url: "/common-portal/control/getPartyAddress?externalLoginKey="+externalLoginKey,
		async: false,
		data: { "partyId": partyId },
		success: function(data) {
			result=data;
			if (result && result.postal) {
				var postalVal=result.postal["postalAddressList"];
				if (postalVal[contactMechId]) {
					$('#generalPostalCode').val(postalVal[contactMechId].postalCode).change();
					$('#countryCodeVal').val(postalVal[contactMechId].countryGeoId);
					$('#countyVal').val(postalVal[contactMechId].county).change();
					$('#countyGeoId').val(postalVal[contactMechId].county);
					$('#generalCountryGeoId').val(postalVal[contactMechId].countryGeoId);
					
					$('#stateIdVal').val(postalVal[contactMechId].stateProvinceGeoId);			
					$('#cityIdVal').val(postalVal[contactMechId].city).change();
					$('#generalAddress1').val(postalVal[contactMechId].address1).change();
					$('#generalAddress2').val(postalVal[contactMechId].address2).change();
					$('#generalPostalCodeExt').val(postalVal[contactMechId].postalCodeExt).change();
				}
			}
		},error: function(data) {
			result=data;
			//showAlert("error", "Error occured while fetching homeowner address");
		}
	});
	//getLocation();
}

function getCustomersAddress(partyId) {
	console.log('calling getCustomersAddress');
	$("#homeOwnerAddress").dropdown('clear');
	$("#homeOwnerAddress").val('');	
	$("#homeOwnerAddress").empty();
	$("div.ui.dropdown.search.form-control.fluid.show-tick.homeOwnerAddress.selection > i").removeClass("clear");
	var customerId = $("#customerId_val").val();		
	var contractor = $("#contractorId_val").val();
	var ownerAddress = $('#homeOwnAddress').val();
	var externalLoginKey = $("#externalLoginKey").val();
	var customerAddOptionList = '<option value=""></option>';	
	if(customerId != "" && contractor != ""){	
		console.log('calling getCustomersAddress 1');
		$.ajax({
			type: "GET",
			url: "/common-portal/control/getPartyAddress?externalLoginKey="+externalLoginKey,
			data: { "partyId": customerId },
			async: false,
			success: function (data) {
				if (data && data.postal) {
				var postalVal=data.postal["postalAddressList"];			
				for (const key in postalVal) {
					var type = postalVal[key];			
					var attnName=type.attnName;
					var add1=type.address1;
					var add2=type.address2;
					var city=type.city;
					var state=type.state;
					var postalCode = type.postalCode;
					var postalCodeExt = type.postalCodeExt;
					var county = type.county;
					var desc="";
					if(attnName){
						if(desc != ""){
							desc=desc+", "+attnName;
						}else{
							desc=desc+attnName;
						}
					}
					if(add1){
						if(desc != ""){
							desc=desc+", "+add1;
						}else{
							desc=desc+add1;
						}
					}
					if(add2){
						if(desc != ""){
							desc=desc+", "+add2;
						}else{
							desc=desc+add2;
						}
					}
					if(city){
						if(desc != ""){
							desc=desc+", "+city;
						}else{
							desc=desc+city;
						}
						
					} 
					if(state){
						if(desc != ""){
							desc=desc+", "+state;
						}else{
							desc=desc+state;
						}
						
					}
					if(postalCode){
						if(desc != ""){
							desc=desc+", "+postalCode;
						}else{
							desc=desc+postalCode;
						}
						if(postalCodeExt){
							if(desc != ""){
								desc=desc+"-"+postalCodeExt;
							}
						}
					}
					if(county){
						if(desc != ""){
							desc=desc+" ("+county+")";
						}/*else{
							desc=desc+" ("+county+")";
						}*/
						
					}
					if(ownerAddress !=null && ownerAddress !="" && ownerAddress !="undefined" && key == ownerAddress){
						customerAddOptionList += '<option value="' + key + '" selected>' +"[Homeowner] "+ desc + '</option>';
						$("div.ui.dropdown.search.form-control.fluid.show-tick.homeOwnerAddress.selection > i").addClass("clear");
					} else{
						customerAddOptionList += '<option value="' + key + '">' +"[Homeowner] "+ desc + '</option>';
					}
				}
				}
			}
		});
		$.ajax({
			type: "GET",
			url: "/common-portal/control/getPartyAddress?externalLoginKey="+externalLoginKey,
			data: { "partyId": contractor },
			async: false,
			success: function (data) {
				if (data && data.postal) {
				var postalVal=data.postal["postalAddressList"];			
				for (const key in postalVal) {
					var type = postalVal[key];			
					var attnName=type.attnName;
					var add1=type.address1;
					var add2=type.address2;
					var city=type.city;
					var state=type.state;
					var postalCode = type.postalCode;
					var postalCodeExt = type.postalCodeExt;
					var county = type.county;
					var desc="";
					if(attnName){
						if(desc != ""){
							desc=desc+", "+attnName;
						}else{
							desc=desc+attnName;
						}
					}
					if(add1){
						if(desc != ""){
							desc=desc+", "+add1;
						}else{
							desc=desc+add1;
						}
					}
					if(add2){
						if(desc != ""){
							desc=desc+", "+add2;
						}else{
							desc=desc+add2;
						}
					}
					if(city){
						if(desc != ""){
							desc=desc+", "+city;
						}else{
							desc=desc+city;
						}
						
					} 
					if(state){
						if(desc != ""){
							desc=desc+", "+state;
						}else{
							desc=desc+state;
						}
						
					}
					if(postalCode){
						if(desc != ""){
							desc=desc+", "+postalCode;
						}else{
							desc=desc+postalCode;
						}
						if(postalCodeExt){
							if(desc != ""){
								desc=desc+"-"+postalCodeExt;
							}
						}
					}
					if(county){
						if(desc != ""){
							desc=desc+" ("+county+")";
						}/*else{
							desc=desc+" ("+county+")";
						}*/
						
					}
					
					if(ownerAddress !=null && ownerAddress !="" && ownerAddress !="undefined" && key == ownerAddress){
						customerAddOptionList += '<option value="' + key + '" selected >' + "[Contractor] "+ desc + '</option>';
						$("div.ui.dropdown.search.form-control.fluid.show-tick.homeOwnerAddress.selection > i").addClass("clear");
					} else{
						customerAddOptionList += '<option value="' + key + '">' + "[Contractor] "+ desc + '</option>';
					}
				}
				}
			}
		});
	} else {
		console.log('calling getCustomersAddress 2, partyId: '+partyId);
		if (!partyId && contractor) {
			partyId = contractor;
		} else if (!partyId && customerId) {
			partyId = customerId;
		}
		
		if (partyId) {
			$.ajax({
				type: "GET",
				url: "/common-portal/control/getPartyAddress?externalLoginKey="+externalLoginKey,
				data: {"partyId": partyId},
				async: false,
				success: function (data) {
					if (data && data.postal) {
					var postalVal=data.postal["postalAddressList"];			
					for (const key in postalVal) {
						var type = postalVal[key];			
						var attnName=type.attnName;
						var add1=type.address1;
						var add2=type.address2;
						var city=type.city;
						var state=type.state;
						var postalCode = type.postalCode;
						var postalCodeExt = type.postalCodeExt;
						var county = type.county;
						var desc="";
						if(attnName){
							if(desc != ""){
								desc=desc+", "+attnName;
							}else{
								desc=desc+attnName;
							}
						}
						if(add1){
							if(desc != ""){
								desc=desc+", "+add1;
							}else{
								desc=desc+add1;
							}
						}
						if(add2){
							if(desc != ""){
								desc=desc+", "+add2;
							}else{
								desc=desc+add2;
							}
						}
						if(city){
							if(desc != ""){
								desc=desc+", "+city;
							}else{
								desc=desc+city;
							}
							
						} 
						if(state){
							if(desc != ""){
								desc=desc+", "+state;
							}else{
								desc=desc+state;
							}
							
						}
						if(postalCode){
							if(desc != ""){
								desc=desc+", "+postalCode;
							}else{
								desc=desc+postalCode;
							}
							if(postalCodeExt){
								if(desc != ""){
									desc=desc+"-"+postalCodeExt;
								}
							}
						}
						if(county){
							if(desc != ""){
								desc=desc+" ("+county+")";
							}/*else{
								desc=desc+" ("+county+")";
							}*/
							
						}
						
						if(ownerAddress !=null && ownerAddress !="" && ownerAddress !="undefined" && key == ownerAddress){
							customerAddOptionList += '<option value="' + key + '" selected >' + desc + '</option>';
							$("div.ui.dropdown.search.form-control.fluid.show-tick.homeOwnerAddress.selection > i").addClass("clear");
						} else{
							customerAddOptionList += '<option value="' + key + '">' + desc + '</option>';
						}
					}
					}
				}
			});
		}
	}
	
	$("#homeOwnerAddress").html(DOMPurify.sanitize(customerAddOptionList));
	//getLocation();
}
function getBusinessUnit(owner) {
	var owner = owner;
	$.ajax({
		type: "POST",
		url: "getBusinessUnitName",
		async: false,
		data: { "owner": owner },
		success: function(data) {
			result=data;
			if(result && result[0] != undefined && result[0].businessunitName != undefined)
				$("#ownerBu").val(result[0].businessunitName);
			else
				$("#ownerBu").val("");
		},error: function(data) {
			result=data;
			showAlert("error", "Error occured while fetching Business Unit");
		}
	});
}

$(function() {
	$("#owner").change(function() {
		var owner  = $("#owner").val(); 
		if(owner != undefined && owner != null)
			getBusinessUnit(owner);	    
	});

	$("#srCategoryId").change(function() {
		$("#srSubCategoryId").dropdown('clear');
		var srCategoryId  = $(this).val();
		var srTypeId  = $("#srTypeId").val();
		if (srCategoryId != "" && srCategoryId != null && srCategoryId !="undefined") {
			$('.srSubCategoryId .clear').click();
			$('#srCategoryId_error').hide();
			loadSubCategory(srCategoryId);
		}else{
			$("#srSubCategoryId").html('');
			$('.srSubCategoryId .clear').click();
			if(srTypeId != ""){
				$('#srCategoryId_error').html(DOMPurify.sanitize($("#srCategoryId").attr('data-error')));
				$('#srCategoryId_error').show();
			}else{
				$('#srCategoryId_error').hide();
			}
		}
	});

	$("#srSubCategoryId").change(function() {
		if($(this).val() == "" && $("#srCategoryId").val() != ""){
			$('#srSubCategoryId_error').html(DOMPurify.sanitize($("#srSubCategoryId").attr('data-error')));
			$('#srSubCategoryId_error').show();
		}
		else{
			$('#srSubCategoryId_error').hide();
		}
	});
	
	$("#cNo_desc").on("change", function() {
		loadDealerDetails();
	});
	
	$('input[type=radio][name=onceAndDone]').change(function() {
		if (this.value == 'Y') {
			var isReqResolution = $("#requiredResolution").val();
			
			if (!isReqResolution || (isReqResolution && isReqResolution=="Y")) {
				$('#resolution').prop('required',true);
				$('#resolution_error').show();
			}
			getAllSrStatuses(this.value);
		}
		else if (this.value == 'N') {
			$('#resolution').prop('required',false);
			$('#resolution_error').hide();
			getAllSrStatuses(this.value);
		}
	});
	
	$("#ContactID").on("change", function() {
		var primary = $("input[type='radio'][name=primary]:checked").val();
		
		if("HOME" == primary){
		} else if("DEALER" == primary){
			console.log('ContactID change');
			getOnboardedPrimaryContacts();
			var primaryContactId = $("#ContactID").val();
			if(primaryContactId){
				getAccountAddress(primaryContactId);
			}
		} else if("CONTRACTOR" == primary){
		}
	});
	
});

function loadDealerDetails(){
	var partyId = $("#cNo_val").val();
	/*
	var srNumber = $("#srNumber").val();
	if (srNumber == null || srNumber == "" || srNumber == "undefined") {
		autoPopulateSrName();
	}*/
	
	if (partyId != ""){
		getPartyRoleTypeId(partyId);
		getPrimaryContacts(partyId);
		getAccountAddress(partyId);
	}
	
}

function getPartyRoleTypeId(partyId) {
	var partyId = partyId;
	$.ajax({
		type: "POST",
		url: "getPartyRoleTypeId",
		async: false,
		data: { "partyId": partyId },
		success: function(data) {
			result=data;
			if(result && result[0] != undefined && result[0].roleTypeId != undefined)
				var roleTypeId = result[0].roleTypeId;
			    if("ACCOUNT" == roleTypeId) 
			    	$('#orderId_row').show();
			    else
			    	$('#orderId_row').hide();
			
		},error: function(data) {
			result=data;
			showAlert("error", "Error occured while fetching Party Role");
		}
	});
}

function getAllSrStatuses(onceAndDone) {
	
	var onceAndDone = onceAndDone;
	var statusOptionList = "";
	var externalLoginKey = $("#externalLoginKey").val();
	$.ajax({
		type: "GET",
		url:'/sr-portal/control/getAllSrStatuses?externalLoginKey='+externalLoginKey,
		async: false,
		success: function(data) {
			for (var i = 0; i < data.length; i++) {
				var eachItem = data[i];
				if(onceAndDone && onceAndDone === "Y" && "SR_CLOSED" === eachItem.statusId){
					statusOptionList += '<option value="'+eachItem.statusId+'" selected="selected">'+eachItem.description+'</option>';
					break;
				}else if(onceAndDone && onceAndDone === "N"){
					if("SR_OPEN" == eachItem.statusId){
						statusOptionList += '<option value="'+eachItem.statusId+'" selected="selected">'+eachItem.description+'</option>';
					}else{
						statusOptionList += '<option value="'+eachItem.statusId+'">'+eachItem.description+'</option>';
					}
				}
			}
		},error: function(data) {
			result=data;
			showAlert("error", "Error occured while fetching Sr Statuses");
		}
	});
	$("#srStatusId").html(DOMPurify.sanitize(statusOptionList));
	$("#srStatusId").dropdown('refresh');
	
	if(onceAndDone && onceAndDone === "Y"){
		$("div.ui.dropdown.search.form-control.fluid.show-tick.srStatusId.selection > i").removeClass("icon");
	}else if(onceAndDone && onceAndDone === "N"){
		$("div.ui.dropdown.search.form-control.fluid.show-tick.srStatusId.selection > i").addClass("dropdown icon");
	}
}

function autoPopulateSrName(){
	var primary = $("input[type='radio'][name=primary]:checked").val();
	var srName= "";
	if("HOME" == primary){
		srName = $("#customerId_desc").val();
	} else if("DEALER" == primary){
		var primContactIdForName = $("#primContactIdForName").val();
		if(primContactIdForName){
			$('#ContactID > option:selected').each(function(){
				var id = $(this).val();
				var text = $(this).text();
				if(primContactIdForName === id)
					srName = text;
			});	
		} 

		if(!srName){
			$('#ContactID > option:selected').each(function(){
				var id = $(this).val();
				var text = $(this).text();
				if(text){
					srName = text;
					return false;
				}
					
			});	
		}
		
		//srName = $("#cNo_desc").val();
		//if( $('#ContactID').val() ) { 
		//	srName = $( "#ContactID option:selected" ).text();
		//}
	} else if("CONTRACTOR" == primary){
		srName = $("#contractorId_desc").val();
	}
	let domainAssign = $("input[type='hidden'][name=workEffortId]").val();
	if(!domainAssign)
		$('#srName').val(srName).change();
}

function dynamicRequiredField(){
	var primary = $("input[type='radio'][name=primary]:checked").val();
	$("#ContactID_error").html('');
	$("#customerId_desc").prop('required', false);
	$("#ContactID").prop('required', false);
	$("#contractorId_desc").prop('required', false);
	if("HOME" == primary){
		$("#customerId_desc").prop('required', true);
	} else if("DEALER" == primary){
		$("#ContactID").prop('required', true);
		var value = $("#ContactID").val();
    	if(value == "" || value == "undefined" || value == "null" ){
    		$("#ContactID_error").append('<ul class="list-unstyled text-danger"><li id="ContactID_err">Please select an item in the list. </li></ul>');
    	}
	} else if("CONTRACTOR" == primary){
		$("#contractorId_desc").prop('required', true);
	}
}

function getOnboardedPrimaryContacts(){
	$('#optionalAttendees').dropdown('clear');
	var partyId = $("#cNo_val").val();
	if ($("#cNo").val()) {
		partyId = $("#cNo").val();
	}
	var srNumber  = $("#srNumber").val();
	var onboardedAttendeesList = "";
	var externalLoginKey = $("#externalLoginKey").val();
	var custmerId=$("#customerId_val").val();
	var contractorId=$("#contractorId_val").val();
	
	// TODO for this call during change dealer FSR address and location being empty: Create FSR
	/*if(custmerId == "" && contractorId == ""){		
		$("#homeOwnerAddress").val('');
		$("#homeOwnerAddress").empty();
		$("#homeOwnerAddress").dropdown('clear');	
		
		$('#cityIdVal').val('');	
		$("#generalCity").dropdown('clear');
		$("#countyGeoId").dropdown('clear');
		$("#countyVal").val('');
		$('#generalAddress1').val('');
		$('#generalAddress2').val('');			
		$('#generalPostalCode').val('');
		$('#generalPostalCodeExt').val('');
	}
	if(custmerId == "" && contractorId != ""){		
		getCustomersAddress(contractorId);		
	}
	if(custmerId != "" && contractorId == ""){		
		getCustomersAddress(custmerId);		
	}*/
	
	var primaryContactId = $("#ContactID").val();
	
	var primContactId = $("#primContactId").val();
	var externalLoginKey = $("#externalLoginKey").val();
	if(srNumber != undefined && srNumber != null){
		$.ajax({
			type: "POST",
			url: "/common-portal/control/searchInviteUsers?externalLoginKey="+externalLoginKey,
			data: {"partyId": partyId, "custRequestId": srNumber},
			async: false,
			success: function (data) {
				for (var i = 0; i < data.data.length; i++) {
					var entry = data.data[i];
					if (entry != null) {
						if ((entry.selected != null || entry.selected != "" || entry.selected != undefined) && entry.selected == "opt" && primaryContactId != entry.invitePartyId) {
							onboardedAttendeesList += '<option value="'+ entry.invitePartyId + '" selected>'+entry.name+'</option>';
						}else {
							if(primaryContactId != null || primaryContactId != "" || primaryContactId != undefined){
								if(primaryContactId != entry.invitePartyId){
									onboardedAttendeesList += '<option value="'+entry.invitePartyId+'">'+entry.name+'</option>';
								}
							}else{
								onboardedAttendeesList += '<option value="'+entry.invitePartyId+'">'+entry.name+'</option>';
							}
						}
					}
				}
			}
		});
	}else{
		$.ajax({
			type: "POST",
			url: "/common-portal/control/searchInviteUsers?externalLoginKey="+externalLoginKey,
			data: {"partyId": partyId},
			async: false,
			success: function (data) {
				for (var i = 0; i < data.data.length; i++) {
					var eachItem = data.data[i];
					if(primaryContactId != null || primaryContactId != "" || primaryContactId != undefined){
						if(primaryContactId != eachItem.invitePartyId){
							onboardedAttendeesList += '<option value="'+eachItem.invitePartyId+'">'+eachItem.name+'</option>';
						}
					}else{
						onboardedAttendeesList += '<option value="'+eachItem.invitePartyId+'">'+eachItem.name+'</option>';
					}
				}
			}
		});
	}
	
	$("#optionalAttendees").html(DOMPurify.sanitize(onboardedAttendeesList));
}

function loadCustomFieldValue(groupId, customFieldName, targetId) {
	loadCustomFieldValue(groupId, customFieldName, targetId, "");
}
function loadCustomFieldValue(groupId, customFieldName, targetId, defaultValue) {
	var options = '<option value="" selected=""></option>';
	var selectedCustomField = defaultValue;
	$("#"+targetId).html(DOMPurify.sanitize(options));
	var externalLoginKey = $("#externalLoginKey").val();
	$.ajax({
		type: "POST",
		url: "/common-portal/control/getCustomFieldMultiValueList?externalLoginKey="+externalLoginKey,
		data:{"groupId": groupId,"customFieldName":customFieldName},
		async: false,
		success: function(data) {
			var dataList = data.dataList;
			for (var i = 0; i < dataList.length; i++) {
				var customField = dataList[i];
				if(selectedCustomField && selectedCustomField === customField.fieldValue){
					options += '<option value="'+customField.fieldValue+'" selected="selected" >'+customField.description+'</option>';
        		}else{
        			options += '<option value="'+customField.fieldValue+'">'+customField.description+'</option>';
        		}
			}
			$("div.ui.dropdown.search.form-control.fluid.show-tick."+targetId+".selection > i").addClass("clear");
		}
	});
	$("#"+targetId).html(DOMPurify.sanitize(options));
	$("#"+targetId).dropdown('refresh');
}

function loadSegmentCodeData(groupingCode, targetId) {
	loadSegmentCodeData(groupingCode, targetId,"")
}
function loadSegmentCodeData(groupingCode, targetId, defaultValue) {
	var options = '<option value="" selected=""></option>';
	var externalLoginKey = $("#externalLoginKey").val();
	var selectedOption = defaultValue;
	if ($('#'+targetId+'Id').length) selectedOption = $('#'+targetId+'Id').val();
	$("#"+targetId).html(DOMPurify.sanitize(options));
	$.ajax({
		type: "POST",
		url: "/common-portal/control/getSegmentCodeList?externalLoginKey="+externalLoginKey,
		data:{"groupingCode": groupingCode},
		async: false,
		success: function(data) {
			var dataList = data.dataList;
			for (var i = 0; i < dataList.length; i++) {
				var segmentCode = dataList[i];
				if(selectedOption && selectedOption === segmentCode.groupId){
					options += '<option value="'+segmentCode.groupId+'" selected="selected" >'+segmentCode.groupName+'</option>';
					$("div.ui.dropdown.search.form-control.fluid.show-tick."+targetId+".selection > i").addClass("clear");
        		}else{
        			options += '<option value="'+segmentCode.groupId+'">'+segmentCode.groupName+'</option>';
        		}
			}
			//$("div.ui.dropdown.search.form-control.fluid.show-tick."+targetId+".selection > i").addClass("clear");
		}
	});
	$("#"+targetId).html(DOMPurify.sanitize(options));
	$("#"+targetId).dropdown('refresh');
}

function loadSegmentValueData(groupId, targetId) {
	loadSegmentValueData(groupId, targetId, "");
}
function loadSegmentValueData(groupId, targetId, defaultValue) {
	var options = '<option value="" selected=""></option>';
	var selectedCustomField = defaultValue;
	if ($('#'+targetId+'Id').length) selectedCustomField = $('#'+targetId+'Id').val();
	var externalLoginKey = $("#externalLoginKey").val();
	$("#"+targetId).html(DOMPurify.sanitize(options));
	$.ajax({
		type: "POST",
		url: "/common-portal/control/getSegmentValueList?externalLoginKey="+externalLoginKey,
		data:{"groupId": groupId},
		async: false,
		success: function(data) {
			var dataList = data.dataList;
			for (var i = 0; i < dataList.length; i++) {
				var segmentValue = dataList[i];
				if(selectedCustomField && selectedCustomField === segmentValue.customFieldId){
					options += '<option value="'+segmentValue.customFieldId+'" selected="selected" >'+segmentValue.customFieldName+'</option>';
					$("div.ui.dropdown.search.form-control.fluid.show-tick."+targetId+".selection > i").addClass("clear");
        		}else{
        			options += '<option value="'+segmentValue.customFieldId+'">'+segmentValue.customFieldName+'</option>';
        		}
			}
			//$("div.ui.dropdown.search.form-control.fluid.show-tick."+targetId+".selection > i").addClass("clear");
		}
	});
	$("#"+targetId).html(DOMPurify.sanitize(options));
	$("#"+targetId).dropdown('refresh');
}

$(function() {
	// jQuery plugin to prevent double submission of forms
	jQuery.fn.preventDoubleSubmission = function() {
	  $(this).on('submit',function(e){
	    var $form = $(this);

	    if ($form.data('submitted') === true) {
	      e.preventDefault();
	    } else {
	      $form.data('submitted', true);
	    }
	  });
	  return this;
	};
});

function getPrimaryTechnician(){
	$("#primaryTechnician").empty();
	var externalLoginKey = $("#externalLoginKey").val();	
	var existsPrimTech= $("#selectedPrimaryTechnician").val();
	var userOptionList = '<option value="" selected="">Please Select</option>';	
	var externalkey="${externalKeyParam}";
	var flag = true;
	$.ajax({
		type: "GET",
		url:'/common-portal/control/getUsersList?roleTypeId=TECHNICIAN&isIncludeInactiveUser=N&externalLoginKey='+externalLoginKey,
		async: false,
		success: function(data) {
			for (var i = 0; i < data.length; i++) {
				var type = data[i];		
				var op = type.userLoginId;
				if(existsPrimTech && existsPrimTech === type.partyId){
					userOptionList += '<option value="'+type.partyId+'" selected="selected">'+type.userName+'</option>';
					$("div.ui.dropdown.search.form-control.fluid.show-tick.primaryTechnician.selection > i").addClass("clear");
				}/* 
				else if((selectPrimTech == "" || selectPrimTech == null) &&   op==loggedInUserId){
					userOptionList += '<option value="'+type.partyId+'" selected="selected">'+type.userName+'</option>';
					$("div.ui.dropdown.search.form-control.fluid.show-tick.salesPerson.selection > i").addClass("clear");
				}*/
				else{
					userOptionList += '<option value="'+type.partyId+'">'+type.userName+'</option>';	
				}				
			}
		}
	});
	
	$("#primaryTechnician").html(DOMPurify.sanitize(userOptionList));
	$("#primaryTechnician").dropdown('refresh');
}

function loadProgramTemplate(targetId, value, externalLoginKey) {
	let options = '<option value=""></option>';
	let selectedOption ="";
	$("#"+targetId).html(DOMPurify.sanitize(options));
	$.ajax({
		type: "POST",
		url: "/common-portal/control/getProgramTemplateList?externalLoginKey="+externalLoginKey,
		//data:{"groupingCode": groupingCode},
		async: false,
		success: function(data) {
			var dataList = data.dataList;
			for (let i = 0; i < dataList.length; i++) {
				let progTpl = dataList[i];
				let dataAttr = 'data-daysRequired="'+progTpl.daysRequired+'" data-displayFormat="'+progTpl.displayFormat+'" data-fromDate="'+progTpl.fromDate+'" data-programTemplateName="'+progTpl.programTemplateName+'"';
				if (value && value === progTpl.programTemplateId) {
					options += '<option value="'+progTpl.programTemplateId+'" selected="selected" '+dataAttr+' >'+progTpl.programTemplateName+'</option>';
					$("div.ui.dropdown.search.form-control.fluid.show-tick."+targetId+".selection > i").addClass("clear");
        		} else {
        			options += '<option value="'+progTpl.programTemplateId+'" '+dataAttr+'>'+progTpl.programTemplateName+'</option>';
        		}
			}
			//$("div.ui.dropdown.search.form-control.fluid.show-tick."+targetId+".selection > i").addClass("clear");
		}
	});
	$("#"+targetId).html(DOMPurify.sanitize(options));
	$("#"+targetId).dropdown('refresh');
}

function getAccountAddress(partyId) {
	console.log('calling getAccountAddress');
	$("#homeOwnerAddress").dropdown('clear');
	$("#homeOwnerAddress").val('');	
	$("#homeOwnerAddress").empty();
	$("div.ui.dropdown.search.form-control.fluid.show-tick.homeOwnerAddress.selection > i").removeClass("clear");
	var accountPartyId = $("#cNo_val").val();		
	var primaryContactId = $("#ContactID").val();
	var ownerAddress = $('#homeOwnAddress').val();
	var externalLoginKey = $("#externalLoginKey").val();
	var customerAddOptionList = '<option value=""></option>';	
	if(accountPartyId != "" && primaryContactId != ""){	
		console.log('calling getAccountAddress 1');
		$.ajax({
			type: "GET",
			url: "/common-portal/control/getPartyAddress?externalLoginKey="+externalLoginKey,
			data: { "partyId": accountPartyId },
			async: false,
			success: function (data) {
				if (data && data.postal) {
				var postalVal=data.postal["postalAddressList"];			
				for (const key in postalVal) {
					var type = postalVal[key];			
					var attnName=type.attnName;
					var add1=type.address1;
					var add2=type.address2;
					var city=type.city;
					var state=type.state;
					var postalCode = type.postalCode;
					var postalCodeExt = type.postalCodeExt;
					var county = type.county;
					var desc="";
					if(attnName){
						if(desc != ""){
							desc=desc+", "+attnName;
						}else{
							desc=desc+attnName;
						}
					}
					if(add1){
						if(desc != ""){
							desc=desc+", "+add1;
						}else{
							desc=desc+add1;
						}
					}
					if(add2){
						if(desc != ""){
							desc=desc+", "+add2;
						}else{
							desc=desc+add2;
						}
					}
					if(city){
						if(desc != ""){
							desc=desc+", "+city;
						}else{
							desc=desc+city;
						}
						
					} 
					if(state){
						if(desc != ""){
							desc=desc+", "+state;
						}else{
							desc=desc+state;
						}
						
					}
					if(postalCode){
						if(desc != ""){
							desc=desc+", "+postalCode;
						}else{
							desc=desc+postalCode;
						}
						if(postalCodeExt){
							if(desc != ""){
								desc=desc+"-"+postalCodeExt;
							}
						}
					}
					if(county){
						if(desc != ""){
							desc=desc+" ("+county+")";
						}/*else{
							desc=desc+" ("+county+")";
						}*/
						
					}
					if(ownerAddress !=null && ownerAddress !="" && ownerAddress !="undefined" && key == ownerAddress){
						customerAddOptionList += '<option value="' + key + '" selected>' +"[Dealer] "+ desc + '</option>';
						$("div.ui.dropdown.search.form-control.fluid.show-tick.homeOwnerAddress.selection > i").addClass("clear");
					} else{
						customerAddOptionList += '<option value="' + key + '">' +"[Dealer] "+ desc + '</option>';
					}
				}
				}
			}
		});
		$.ajax({
			type: "GET",
			url: "/common-portal/control/getPartyAddress?externalLoginKey="+externalLoginKey,
			data: { "partyId": primaryContactId },
			async: false,
			success: function (data) {
				if (data && data.postal) {
				var postalVal=data.postal["postalAddressList"];			
				for (const key in postalVal) {
					var type = postalVal[key];			
					var attnName=type.attnName;
					var add1=type.address1;
					var add2=type.address2;
					var city=type.city;
					var state=type.state;
					var postalCode = type.postalCode;
					var postalCodeExt = type.postalCodeExt;
					var county = type.county;
					var desc="";
					if(attnName){
						if(desc != ""){
							desc=desc+", "+attnName;
						}else{
							desc=desc+attnName;
						}
					}
					if(add1){
						if(desc != ""){
							desc=desc+", "+add1;
						}else{
							desc=desc+add1;
						}
					}
					if(add2){
						if(desc != ""){
							desc=desc+", "+add2;
						}else{
							desc=desc+add2;
						}
					}
					if(city){
						if(desc != ""){
							desc=desc+", "+city;
						}else{
							desc=desc+city;
						}
						
					} 
					if(state){
						if(desc != ""){
							desc=desc+", "+state;
						}else{
							desc=desc+state;
						}
						
					}
					if(postalCode){
						if(desc != ""){
							desc=desc+", "+postalCode;
						}else{
							desc=desc+postalCode;
						}
						if(postalCodeExt){
							if(desc != ""){
								desc=desc+"-"+postalCodeExt;
							}
						}
					}
					if(county){
						if(desc != ""){
							desc=desc+" ("+county+")";
						}/*else{
							desc=desc+" ("+county+")";
						}*/
						
					}
					
					if(ownerAddress !=null && ownerAddress !="" && ownerAddress !="undefined" && key == ownerAddress){
						customerAddOptionList += '<option value="' + key + '" selected >' + "[Contact] "+ desc + '</option>';
						$("div.ui.dropdown.search.form-control.fluid.show-tick.homeOwnerAddress.selection > i").addClass("clear");
					} else{
						customerAddOptionList += '<option value="' + key + '">' + "[Contact] "+ desc + '</option>';
					}
				}
				}
			}
		});
	} else {
		console.log('calling getAccountAddress 2, partyId: '+partyId);
		if (!partyId && accountPartyId) {
			partyId = accountPartyId;
		} else if (!partyId && primaryContactId) {
			partyId = primaryContactId;
		}
		
		if (partyId) {
			$.ajax({
				type: "GET",
				url: "/common-portal/control/getPartyAddress?externalLoginKey="+externalLoginKey,
				data: {"partyId": partyId},
				async: false,
				success: function (data) {
					if (data && data.postal) {
					var postalVal=data.postal["postalAddressList"];			
					for (const key in postalVal) {
						var type = postalVal[key];			
						var attnName=type.attnName;
						var add1=type.address1;
						var add2=type.address2;
						var city=type.city;
						var state=type.state;
						var postalCode = type.postalCode;
						var postalCodeExt = type.postalCodeExt;
						var county = type.county;
						var desc="";
						if(attnName){
							if(desc != ""){
								desc=desc+", "+attnName;
							}else{
								desc=desc+attnName;
							}
						}
						if(add1){
							if(desc != ""){
								desc=desc+", "+add1;
							}else{
								desc=desc+add1;
							}
						}
						if(add2){
							if(desc != ""){
								desc=desc+", "+add2;
							}else{
								desc=desc+add2;
							}
						}
						if(city){
							if(desc != ""){
								desc=desc+", "+city;
							}else{
								desc=desc+city;
							}
							
						} 
						if(state){
							if(desc != ""){
								desc=desc+", "+state;
							}else{
								desc=desc+state;
							}
							
						}
						if(postalCode){
							if(desc != ""){
								desc=desc+", "+postalCode;
							}else{
								desc=desc+postalCode;
							}
							if(postalCodeExt){
								if(desc != ""){
									desc=desc+"-"+postalCodeExt;
								}
							}
						}
						if(county){
							if(desc != ""){
								desc=desc+" ("+county+")";
							}/*else{
								desc=desc+" ("+county+")";
							}*/
							
						}
						
						if(ownerAddress !=null && ownerAddress !="" && ownerAddress !="undefined" && key == ownerAddress){
							customerAddOptionList += '<option value="' + key + '" selected >' + desc + '</option>';
							$("div.ui.dropdown.search.form-control.fluid.show-tick.homeOwnerAddress.selection > i").addClass("clear");
						} else{
							customerAddOptionList += '<option value="' + key + '">' + desc + '</option>';
						}
					}
					}
				}
			});
		}
	}
	
	$("#homeOwnerAddress").html(DOMPurify.sanitize(customerAddOptionList));
	//getLocation();
}

function loadCoordinator(){
	let state = $("#generalStateProvinceGeoId").val();
	let county = $("#countyGeoId").val();
	let loggedInUserId = $("#loggedInUserId").val();
	if(state && county){
		$.ajax({
				type: "GET",
				url: "/common-portal/control/getCoordinatorList?externalLoginKey="+externalLoginKey,
				data: {"stateGeoId": state,"countyGeoId":county},
				async: false,
				success: function (data) {
					var coordinatorUserLogin = data.coordinatorLoginId;
					if(coordinatorUserLogin){
						$('#mainFrom #owner').dropdown('set selected', coordinatorUserLogin);
						$('#mainFrom #owner').dropdown('refresh');
					} else{
						$('#mainFrom #owner').dropdown('set selected', loggedInUserId);
						$('#mainFrom #owner').dropdown('refresh');
					}
				}
			});
	}
	
	
}
function loadPrimaryTechnician(){
	$('#mainFrom #primaryTechnician').dropdown('clear');
	$('#mainFrom #primaryTechnician').dropdown('refresh');
	
	let state = $("#generalStateProvinceGeoId").val();
	let county = $("#countyGeoId").val();
	let loggedInUserId = $("#loggedInUserId").val();
	if(state && county){
		$.ajax({
				type: "GET",
				url: "/common-portal/control/getPrimaryTechnicianList?externalLoginKey="+externalLoginKey,
				data: {"stateGeoId": state,"countyGeoId":county},
				async: false,
				success: function (data) {
					var technicianId = data.technicianId;
					if(technicianId){
						$('#mainFrom #primaryTechnician').dropdown('set selected', technicianId);
						$('#mainFrom #primaryTechnician').dropdown('refresh');
					}
				}
			});
	}
	
	
}
