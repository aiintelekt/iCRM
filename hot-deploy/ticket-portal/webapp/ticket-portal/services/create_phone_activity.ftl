<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<#include "component://common-portal/webapp/common-portal/lib/picker_macro.ftl"/>

<div id="main" role="main" class="pd-btm-title-bar">
  	
	<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
		
		<#-- Action Bar  -->
		<div class="card-head margin-adj mt-2" style = "display: none">
                
       	</div>
        
        <#-- KPI Bar -->
      	<div class="card-head margin-adj mt-2" style = "display: none">
    		
		</div>
           
       	<#-- Basic information -->
           
		<div class="card-header mt-3" id="cp" style = "display: none">
		
		</div>
       	
        <div class="col-lg-12 col-md-12 col-sm-12">
    		<@pageSectionHeader title="Activity Details"/>
    	</div>

		<form id="mainFrom" method="post" action="<@ofbizUrl>createPhoneCallActivityAction</@ofbizUrl>" data-toggle="validator">
			<input type="hidden" name="domainEntityType" value="${(parameters.domainEntityType)!}"/>
        	<input type="hidden" name="domainEntityId" value="${(parameters.domainEntityId)!}"/>
	        	
	        	<div class="col-lg-12 col-md-12 col-sm-12">
                	<#assign cifNo = '${requestParameters.partyId!}' >
                	<#assign custRequestId = '${requestParameters.custRequestId!}' >
                    <@inputHidden name="cNo" id="cNo" value = "${cifNo!}"/>
                    <@inputHidden name="custRequestId" id="custRequestId" value = "${custRequestId!}"/>
                    <@inputHidden name="ownerBu" id="ownerBu" />
                    <#assign srType = EntityQuery.use(delegator).from("WorkEffortAssocTriplet").where("entityName", "Activity", "type", "Type", "value", "Phone Call", "active", "Y").queryFirst()! />
                    <@inputHidden id="srTypeId" value="${(srType.code)!}"/>
                    <@inputHidden id="workEffortTypeId" value="${(srType.value)!}"/>
                    <@inputHidden id="isPhoneCall" value="Y"/>
                    
                    <@inputHidden id="loggedInUserId" value="${userLogin.userLoginId?if_exists}" />
                    <#assign userName = userLogin.userLoginId>
                    <#assign findMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", userLogin.partyId)>
                    <#assign person = delegator.findOne("Person", findMap, true)!>
                    <#if person?has_content>
                    	<#assign userName = (person.firstName!) + " " + (person.middleName!) + " " + person.lastName!>
                    	<@inputHidden id="userName" value="${userName!}"/>
                    </#if>
                    
	                <@dynaScreen 
		                instanceId="PHONE_ACT_GEN"
		                modeOfAction="CREATE"
		             />
	            </div>
	            
	            <div class="col-md-12 col-lg-12 col-sm-12 ">
         			<@textareaLarge  label="Description" id="messages" rows="4"/>
      			</div>
      			
	           	<div class="offset-md-2 col-sm-10">
	           		<@formButton
	                     btn1type="submit"
	                     btn1label="${uiLabelMap.Save}"
	                     btn1onclick="return formSubmission();"
	                     btn2=true
	                     btn2onclick = "resetFormToReload()"
	                     btn2type="reset"
	                     btn2label="${uiLabelMap.Clear}"
	                />
	            </div>

	</form>
	
</div>
	<@partyPicker 
	instanceId="partyPicker"
	/>
<script>
    
    $(document).ready(function() {
    	var userName  = $("#userName").val();
   		$( "#callFrom" ).remove();
   		$( "#callFrom_error" ).html('<i class="fa fa-user fa-1" aria-hidden="true"></i> '+'<b>'+userName+'</b>').css('color','blue');
   		<#-- $( "#norganizer" ).val(userName);
   		$('#norganizer').attr('readonly','readonly');  -->
   		onLoadDefaultElementsBehaviour();
   		
    	$("#owner").change(function() {
			var owner  = $("#owner").val();
			if(owner != undefined && owner != null)
			   	getBusinessUnit(owner);	    
		});
		$('#onceDone').val("N");
		$('#onceDone').checked=true;
		$('#type').val($('#workEffortTypeId').val());
		$('#type').attr('readonly','readonly');
		$('#ownerBuDesc').attr('readonly','readonly');
		
		<#-- Focus the description in update -->
		if($("#messages").length > 0) {
		  $("#messages").focus(); 
		}
		
		var typeId  = $("#srTypeId").val();
	    if (typeId != "") {
	    	loadSubTypes(typeId);
	    }
	    
	    var loggedInUserId  = $("#loggedInUserId").val(); 
	    var direction = $("#direction").val();
		if (direction != undefined && direction != null && direction != "") {
       		loadCallToAndFrom(direction,loggedInUserId,userName);
       	}
	    $("#direction").on("change", function() {
			var direction = $("#direction").val();
			if (direction != undefined && direction != null && direction != "") {
       			loadCallToAndFrom(direction,loggedInUserId,userName);
       		}
		});
		
		$("#norganizer").on("change", function() {
			var direction = $("#direction").val();
			var norganizer = $("#norganizer").val();
       		if(direction == "62438"){
       			var norganizer = $("#norganizer").val();
       			populatePhoneNumber(direction,norganizer);
       		}
		});
		
		$("#nrecepient").on("change", function() {
			var direction = $("#direction").val();
			var norganizer = $("#norganizer").val();
       		if(direction == "62439"){
       			var nrecepient = $("#nrecepient").val();
       			populatePhoneNumber(direction,nrecepient);
       		}
		});
		
	    if(loggedInUserId != undefined && loggedInUserId != null)
			   	getBusinessUnit(loggedInUserId);
	    getUsers(loggedInUserId,userName);
	    $("span.picker-window-erase").css("display", "none");
		$("span.picker-window").css("display", "none");
	    var cNo=$("#cNo").val();
		if(cNo== null || cNo== undefined || cNo==""){
		$("#cNo").val($("#partyId_val").val());
		cNo=$("#partyId_val").val();
		}
		if(cNo!=null && cNo!= undefined && cNo!="")
		{
		 loadContacts();
		
		}
	    $("#partyId_desc").on("change", function() {

		var nonSelectContent = "<span class='nonselect'>Select Contact</span>";
		var dataSourceOptions = '';
		var partyId = $("#partyId_val").val();
		$.ajax({
			type: "POST",
	     	url: "/common-portal/control/getPrimaryContacts",
	        data: {"partyId": partyId, "externalLoginKey": "${requestAttributes.externalLoginKey!}"},
	        async: false,
	        success: function (data) {   
	            if (data) {
	            if(data.responseMessage=="success"){
	            	for (var i = 0; i < data.partyRelContacts.length; i++) {
	            		var entry = data.partyRelContacts[i];
	            		if(entry.selected!=null){
	            		dataSourceOptions += '<option value="'+entry.contactId+'" selected>'+entry.name+'</option>';
	            		}else{
	            		dataSourceOptions += '<option value="'+entry.contactId+'">'+entry.name+'</option>';
	            		}
	            	}
	            	}else{
	            	for (var i = 0; i < data.length; i++) {
	            		var entry = data[i];
	            		dataSourceOptions += '<option value="'+entry.contactId+'">'+entry.name+'</option>';
	            	}
	            	
	            	}
	            }
	        }
	        
		});    
		
		$("#contactId").html( dataSourceOptions );
				
		$("#contactId").dropdown('refresh');
	}); 	
    });
    function resetFormToReload(){
    	window.location.href=window.location.href;
    }
    function onLoadDefaultElementsBehaviour(){
        
       var today = new Date();
		var dd = String(today.getDate()).padStart(2, '0');
		var mm = String(today.getMonth() + 1).padStart(2, '0');
		var yyyy = today.getFullYear();
		var hh= today.getHours();
		var m= today.getSeconds();
		//today = mm + '/' + dd + '/' + yyyy +" "+hh+":"+m;
		today = mm + '/' + dd + '/' + yyyy;
		$('#callDateTime').val(today.toLocaleString([],{hour12: false,dateStyle:"short",timeStyle:"short"}).replace(",",""));
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
		            if(result && result[0] != undefined && result[0].businessId != undefined){
		            	$("#ownerBu").val(result[0].businessId);
		            	$("#ownerBuDesc").val(result[0].businessunitName);
		            }else{
		            	$("#ownerBu").val("");
		            	$("#ownerBuDesc").val("");
		            }
		        },error: function(data) {
		        	result=data;
					showAlert("error", "Error occured while fetching Business Unit");
				}
		});
	}
	 function loadContacts(){
	var dataSourceOptions="";
	var partyId = $("#partyId_val").val();
		$.ajax({
			type: "POST",
	     	url: "/common-portal/control/getPrimaryContacts",
	        data: {"partyId": partyId, "externalLoginKey": "${requestAttributes.externalLoginKey!}"},
	        async: false,
	        success: function (data) {   
	            if (data) {
	            if(data.responseMessage=="success"){
	            	for (var i = 0; i < data.partyRelContacts.length; i++) {
	            		var entry = data.partyRelContacts[i];
	            		if(entry.selected!=null){
	            		dataSourceOptions += '<option value="'+entry.contactId+'" selected>'+entry.name+'</option>';
	            		}else{
	            		dataSourceOptions += '<option value="'+entry.contactId+'">'+entry.name+'</option>';
	            		}
	            	}
	            	}else{
	            	for (var i = 0; i < data.length; i++) {
	            		var entry = data[i];
	            		dataSourceOptions += '<option value="'+entry.contactId+'">'+entry.name+'</option>';
	            	}
	            	
	            	}
	            }
	        }
	        
		});    
		
		$("#contactId").html( dataSourceOptions );
				
		$("#contactId").dropdown('refresh');
	}
	function loadSubTypes(typeId) {
        var nonSelectContent = "<span class='nonselect'>Please Select</span>";
        var subTypes = '<option value="" data-content="'+nonSelectContent+'" selected="">Please Select</option>';
        $.ajax({
            type: "POST",
            url: "getIASubTypes",
            data: { "iaTypeId": typeId },
            async: false,
            success: function(data) {
               var sourceDesc = data.results;
               for (var i = 0; i < data.length; i++) {
                    var type = data[i];
                    subTypes += '<option value="'+type.subTypeId+'">'+type.subTypeDesc+'</option>';
                }
            }
        });
        $("#srSubTypeId").html(subTypes);
	}
	 function getUsers(loggedInUserId,userName) {
        var userOptionList = '<option value="'+loggedInUserId+'">'+userName+'</option>';
        $.ajax({
            type: "GET",
            url:'/common-portal/control/getUsersList',
            async: false,
            success: function(data) {
            	for (var i = 0; i < data.length; i++) {
                    var type = data[i];
                    userOptionList += '<option value="'+type.userLoginId+'">'+type.userName+'</option>';
                }
            }
        });
       $("#owner").html(userOptionList);
	}
	function formSubmission(){
    	var valid = true;
    	if($('#partyId_val').val() == ""){
	 		showAlert('error','Please select Customer');
	 		valid = false;
	 	}else{
	 		$('#cNo').val($('#partyId_val').val());
	 	}
	 	return valid;
	 }
	 
	 function loadCallToAndFrom(direction,loggedInUserId,userName) {
        
        var userOptionList = '<option value="'+loggedInUserId+'">'+userName+'</option>';
        $.ajax({
            type: "GET",
            url:'/common-portal/control/getUsersList',
            async: false,
            success: function(data) {
            	for (var i = 0; i < data.length; i++) {
                    var type = data[i];
                    userOptionList += '<option value="'+type.userLoginId+'">'+type.userName+'</option>';
                }
            }
        });
        
        if("62438" == direction){
       		$("#nrecepient").html(userOptionList);
       		$("#nrecepient").dropdown('refresh');
       	}
       	if("62439" == direction){
       		$("#norganizer").html(userOptionList);
       		$("#norganizer").dropdown('refresh');
       	}
       
       var dataSourceOptions = '';
       var partyId = $("#partyId_val").val();
       var partyRoleTypeId = "${partyRoleTypeId!}";
       var isIncludeMainParty="N";
		if (partyRoleTypeId && partyRoleTypeId =="CUSTOMER"){
			isIncludeMainParty = "Y";
		}
		$.ajax({
			type: "POST",
	     	url: "/common-portal/control/getPrimaryContacts",
	        data: {"partyId": partyId, "externalLoginKey": "${requestAttributes.externalLoginKey!}","isIncludeMainParty":isIncludeMainParty},
	        async: false,
	        success: function (data) {   
	            if (data) {
		            if(data.responseMessage=="success"){
		            	for (var i = 0; i < data.partyRelContacts.length; i++) {
		            		var entry = data.partyRelContacts[i];
		            		if(entry.selected!=null){
		            			dataSourceOptions += '<option value="'+entry.contactId+'" selected>'+entry.name+'</option>';
		            		}else{
		            			dataSourceOptions += '<option value="'+entry.contactId+'">'+entry.name+'</option>';
		            		}
		            	}
		            }else{
		            	for (var i = 0; i < data.length; i++) {
		            		var entry = data[i];
		            		dataSourceOptions += '<option value="'+entry.contactId+'">'+entry.name+'</option>';
		            	}
		            }
	            }
	        }
		}); 
       
       if("62438" == direction){
       		$("#norganizer").html(dataSourceOptions);
       		$("#norganizer").dropdown('refresh');
       		var contactPartyId = $("#norganizer").val();
       		populatePhoneNumber(direction,contactPartyId);
       }
       if("62439" == direction){
       		$("#nrecepient").html(dataSourceOptions);
       		$("#nrecepient").dropdown('refresh');
       		var contactPartyId = $("#nrecepient").val();
       		populatePhoneNumber(direction,contactPartyId);
       	}
	}
	
	function populatePhoneNumber(direction,contactPartyId) {	
		var partyRoleTypeId = "${partyRoleTypeId!}";
		if (partyRoleTypeId && partyRoleTypeId =="CUSTOMER"){
			contactPartyId = $("#cNo").val();
		}
        var nonSelectContent = "<span class='nonselect'>Please Select</span>";
        var telecomOptionsList = '';
       	$.ajax({
			type: "POST",
	     	url: "/common-portal/control/getPartyTelecomNumbers",
	        data: {"partyId": contactPartyId, "externalLoginKey": "${requestAttributes.externalLoginKey!}"},
	        async: false,
	        success: function (data) {   
	            for (var i = 0; i < data.length; i++) {
                    var type = data[i];
                    var isprimary = type.isPrimary;
                    
                    if("Y" === isprimary){
                    	telecomOptionsList += '<option value="'+type.contactNumber+'" selected="selected">'+type.contactNumber+'</option>';
                    }else{
                    	if(i==0){
                    	 telecomOptionsList =  '<option value="" data-content="'+nonSelectContent+'" selected="selected">Please Select</option>';
                    	}
                    	telecomOptionsList += '<option value="'+type.contactNumber+'">'+type.contactNumber+'</option>';
                    }
                }
	        }
		});
		$("#phoneNumber").html(telecomOptionsList);
       	$("#phoneNumber").dropdown('refresh');  
	}
	       
</script>