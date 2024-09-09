<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<#include "component://common-portal/webapp/common-portal/lib/picker_macro.ftl"/>

<div class="row">
	<div id="main" role="main" class="pd-btm-title-bar">
        
	<#--<#assign extraLeft='
			<a id="findcustomerSr" title="Find Customer" href="#" class="btn btn-primary btn-xs" data-toggle="modal" data-target="#findcustomer" ><i class="fa fa-search"></i> Find Customer</a>
    		<a id="createProspect" title="Create Prospect" href="#" class="btn btn-primary btn-xs" onclick="#"><i class="fa fa-user-plus"></i> Create Prospect</a>
    		<a id="createNonCrm" title="Create Non CRM" href="#" class="btn btn-primary btn-xs" onclick="#"><i class="fa fa-user-times"></i>  Create Non CRM</a>
   	   '/> 
            
        <@sectionFrameHeader  title="Add Phone Calls "  extraLeft=extraLeft  />  -->
        
        <#assign extraLeft='
	        <a id=task1" title="Task1" href="#" class="btn btn-primary btn-xs" onclick="#"> Task 1</a>
	        <a id="task2" title="Task2" href="#" class="btn btn-primary btn-xs" onclick="#">Task 2</a>
	        <a id="task3" title="Task3" href="#" class="btn btn-primary btn-xs" onclick="#">Task 3</a>
	        <a id="task4" title="Task4" href="#" class="btn btn-primary btn-xs" onclick="#">Task 4</a>
	        <a id="task5" title="Task5" href="#" class="btn btn-primary btn-xs" onclick="#">Task 5</a>
	        ' />
      <#--  <@sectionFrameHeader  title="Recently Viewed:"  extraLeft=extraLeft  />-->
        
        <#assign addActivities = '
        	<div class="dropdown-menu" aria-labelledby="E10007">
                <h4>Add Activities</h4>
                <a class="dropdown-item" href="/contact-portal/control/createTaskActivity?partyId=${partyId!}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank"><i class="fa fa-clipboard" aria-hidden="true"></i> Task</a> 
			    <a class="dropdown-item" href="/contact-portal/control/createPhoneCallActivity?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank"><i class="fa fa-phone" aria-hidden="true"></i> Phone Call</a>
			    <a class="dropdown-item" href="/contact-portal/control/addEmail?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank"><i class="fa fa-envelope" aria-hidden="true"></i> Email</a>
			    <a class="dropdown-item" href="/contact-portal/control/createAppointmentActivity?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank"><i class="fa fa-calendar" aria-hidden="true"></i> Appointment</a>
            </div>
        ' />
	   <#-- <a class="dropdown-item" href="/sales-portal/control/addOthers?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank"><i class="fa fa-plus-square" aria-hidden="true"></i> Others</a>  -->
        
        <#assign toggleDropDownData = {"E10007":addActivities!} />
        
        <div class="col-lg-12 col-md-12 col-sm-12">
        	<div class="card-head margin-adj mt-2" style = "display: none">
                <@AppBar  
	                appBarId="ACTION_APP_BAR"
	                appBarTypeId="ACTION"
	                id="appbar1"
	                extra=extra!
	                toggleDropDownData=toggleDropDownData!
	                isEnableUserPreference=true
	                />
            </div>
           
           <#-- Basic information -->
           <#assign partyId= request.getParameter("partyId")! />
           <#if partyId?has_content>
	           <div class="card-header mt-3" id="cp" style = "display: none">
	           		<@dynaScreen 
		                instanceId="CONT_BASIC_INFO"
		                modeOfAction="VIEW"
		            />
	           </div>
           </#if>
       	
           <#--  <div class="col-lg-12 col-md-12 col-sm-12">
        		<@pageSectionHeader title="Activity Details"/>
        	</div>-->
             
            <form id="mainFrom" method="post" action="<@ofbizUrl>createPhoneCallActivityAction</@ofbizUrl>" data-toggle="validator">
            	<#assign partyId = '${requestParameters.partyId!}' >
        		<@inputHidden name="partyId" id="partyId" value = "${partyId!}"/> 
        		<input type="hidden" name="domainEntityType" value="${(parameters.domainEntityType)!}"/>
        		<input type="hidden" name="domainEntityId" value="${(parameters.domainEntityId)!}"/>
	        	<div class="col-lg-12 col-md-12 col-sm-12">
	        	<@pageSectionHeader title="Activity Details"/>
                	<#assign cifNo = '${requestParameters.partyId!}' >
                    <@inputHidden name="cNo" id="cNo" value = "${cifNo!}"/>
                    <@inputHidden name="contactId" id="contactId" value = "${contactId!}"/>
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
		                instanceId="CREATE_PHONE_ACTIVITY_CONT"
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
	                     btn2=true
	                     btn2onclick = "resetFormToReload()"
	                     btn2type="reset"
	                     btn2label="${uiLabelMap.Clear}"
	                />
	            </div>
        	</form>
        </div>
    </div>
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
   		var cNo=$("#cNo").val();
		
		$("#partyId_desc").val('${appBarContext.name!}');	
		$("#partyId_Val").val($("#partyId").val());
   	 	$("#owner").change(function() {
			var owner  = $("#owner").val(); 
			if(owner != undefined && owner != null)
			   	getBusinessUnit(owner);	    
		});
		
		$('#type').val($('#workEffortTypeId').val());
		$('#type').attr('readonly','readonly');
		$('#ownerBuDesc').attr('readonly','readonly');
		
		var typeId  = $("#srTypeId").val();
	    if (typeId != "") {
	    	loadSubTypes(typeId);
	    }
	    var loggedInUserId  = $("#loggedInUserId").val();
	    if(loggedInUserId != undefined && loggedInUserId != null)
			   	getBusinessUnit(loggedInUserId);
	    getUsers(loggedInUserId,userName);
	    
	    var direction = $("#direction").val();
    	if (direction) {
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
       			$("#phoneNumber").html('');
       			populatePhoneNumber(direction,norganizer);
       		
       		}
		});
		
		$("#nrecepient").on("change", function() {
			var direction = $("#direction").val();
			var norganizer = $("#norganizer").val();
       		if(direction == "62439"){
       			var nrecepient = $("#nrecepient").val();
       			$("#phoneNumber").html('');
       			populatePhoneNumber(direction,nrecepient);
       		}
		});
		
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
	function resetFormToReload(){
    	window.location.href=window.location.href;
    }
    function onLoadDefaultElementsBehaviour(){
        
        var nonSelectContent = "<span class='nonselect'>Please Select</span>";
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
    	/*
    	if($('#partyId_val').val() == ""){
	 		showAlert('error','Please select Customer');
	 		valid = false;
	 	}else{
	 		$('#cNo').val($('#partyId_val').val());
	 	}*/
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
       
       if("62438" == direction){
    	   var orgOpt="";
  			var orgOpt = '<option value="'+'${partyId!}'+'" selected>'+'${appBarContext.name!}'+'</option>';
       		$("#norganizer").html(orgOpt);
       		$("#norganizer").dropdown('refresh');
       		var contactPartyId = $("#norganizer").val();
       		$("#phoneNumber").html('');
       		populatePhoneNumber(direction,contactPartyId);
       }
       if("62439" == direction){
    	   var receiptOpt="";
   			var receiptOpt = '<option value="'+'${partyId!}'+'" selected>'+'${appBarContext?if_exists.name!}'+'</option>';
       		$("#nrecepient").html(receiptOpt);
       		$("#nrecepient").dropdown('refresh');
       		var contactPartyId = $("#nrecepient").val();
       		$("#phoneNumber").html('');
       		populatePhoneNumber(direction,contactPartyId);
       	}
   
	}
	
	function populatePhoneNumber(direction,contactPartyId) {			
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
                    var areaCode ="";
                    var contactNumber ="";
                    if (type && type.areaCode){
                   		areaCode = type.areaCode;
                    }
                    if (type && type.contactNumber){
                   		contactNumber = type.contactNumber;
                    }
                    if (areaCode && contactNumber){
                   		contactNumber = areaCode+""+contactNumber
                    }
                    if("Y" === isprimary){
                    	telecomOptionsList += '<option value="'+contactNumber+'" selected="selected">'+contactNumber+'</option>';
                    }else{
                    	if(i==0){
                    	 telecomOptionsList =  '<option value="" data-content="'+nonSelectContent+'" selected="selected">Please Select</option>';
                    	}
                    	telecomOptionsList += '<option value="'+contactNumber+'">'+contactNumber+'</option>';
                    }
                }
	        }
		});
		$("#phoneNumber").html(telecomOptionsList);
       	$("#phoneNumber").dropdown('refresh');  
	}
	         
</script>