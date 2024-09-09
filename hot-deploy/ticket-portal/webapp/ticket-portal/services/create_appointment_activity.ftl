<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>


<#include "component://common-portal/webapp/common-portal/lib/picker_macro.ftl"/>
<script type="text/javascript" src="/common-portal-resource/js/ag-grid/activity/validation-activity.js"></script>

<div id="main" role="main" class="pd-btm-title-bar">
	  	
<#-- <@sectionFrameHeader title="Find Accounts"/> -->
<#assign extraLeft='
	<a id=task1" title="Task1" href="#" class="btn btn-primary btn-xs" onclick="#"> Task 1</a>
   	<a id="task2" title="Task2" href="#" class="btn btn-primary btn-xs" onclick="#">Task 2</a>
    <a id="task3" title="Task3" href="#" class="btn btn-primary btn-xs" onclick="#">Task 3</a>
    <a id="task4" title="Task4" href="#" class="btn btn-primary btn-xs" onclick="#">Task 4</a>
    <a id="task5" title="Task5" href="#" class="btn btn-primary btn-xs" onclick="#">Task 5</a>
' />
 <#--<@sectionFrameHeader  title="Recently Viewed:"  extraLeft=extraLeft  />-->
<#assign addActivities = '
        	<div class="dropdown-menu" aria-labelledby="E10007">
                <h4>Add Activities</h4>
                <a class="dropdown-item" href="/opportunity-portal/control/createTaskActivity?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&externalLoginKey=${requestAttributes.externalLoginKey!}&salesOpportunityId=${salesOpportunityId?if_exists}" target="_blank"><i class="fa fa-clipboard" aria-hidden="true"></i> Task</a> 
			    <a class="dropdown-item" href="/opportunity-portal/control/createPhoneCallActivity?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&externalLoginKey=${requestAttributes.externalLoginKey!}&salesOpportunityId=${salesOpportunityId?if_exists}" target="_blank"><i class="fa fa-phone" aria-hidden="true"></i> Phone Call</a>
			    <a class="dropdown-item" href="/opportunity-portal/control/addEmail?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank"><i class="fa fa-envelope" aria-hidden="true"></i> Email</a>
			    <a class="dropdown-item" href="/opportunity-portal/control/createAppointmentActivity?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&externalLoginKey=${requestAttributes.externalLoginKey!}&salesOpportunityId=${salesOpportunityId?if_exists}" target="_blank"><i class="fa fa-calendar" aria-hidden="true"></i> Appointment</a>
            </div>
        ' />
        
        <#assign toggleDropDownData = {"E10007":addActivities!} />
  	
<div class="col-lg-12 col-md-12 col-sm-12">
			
			<#-- Action Bar  -->
			<div class="card-head margin-adj mt-2" style="display:none">
                
            </div>
            
            <#-- KPI Bar -->
           	<div class="card-head margin-adj mt-2" style="display:none">
    		
			</div>
			
           <#-- Basic information -->
			<div class="card-header mt-3" id="cp" style="display:none">
		
			</div>
       	
            <div class="col-lg-12 col-md-12 col-sm-12">
        		<@pageSectionHeader title="Activity Details"/>
        	</div>

			<form id="mainFrom" method="post" action="<@ofbizUrl>createAppointmentActivityAction</@ofbizUrl>" data-toggle="validator" onsubmit="return submitActivityForm();">
				<input type="hidden" name="domainEntityType" value="${(parameters.domainEntityType)!}"/>
        		<input type="hidden" name="domainEntityId" value="${(parameters.domainEntityId)!}"/>
	        	<div class="col-lg-12 col-md-12 col-sm-12">
                	<#assign cifNo = '${requestParameters.partyId!}' >
                	<#assign custRequestId = '${requestParameters.custRequestId!}' >
                    <@inputHidden name="cNo" id="cNo" value = "${cifNo!}"/>
                    <@inputHidden name="custRequestId" id="custRequestId" value = "${custRequestId!}"/>
                    <@inputHidden name="ownerBu" id="ownerBu" />
                    <#assign srType = EntityQuery.use(delegator).from("WorkEffortAssocTriplet").where("entityName", "Activity", "type", "Type", "value", "Appointment", "active", "Y").queryFirst()! />
                    <@inputHidden id="srTypeId" value="${(srType.code)!}"/>
                    <@inputHidden id="workEffortTypeId" value="${(srType.value)!}"/>
                    <@inputHidden id="loggedInUserId" value="${userLogin.userLoginId?if_exists}" />
                    <#assign userName = userLogin.userLoginId>
                    <#assign findMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", userLogin.partyId)>
                    <#assign person = delegator.findOne("Person", findMap, true)!>
                    <#if person?has_content>
                    	<#assign userName = (person.firstName!) + " " + (person.middleName!) + " " + person.lastName!>
                    	<@inputHidden id="userName" value="${userName!}"/>
                    </#if>
	                <@dynaScreen 
		                instanceId="APNT_ACT_GEN"
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

	<@partyPicker 
	instanceId="partyPicker"
	/>
	
<script>
    
    $(document).ready(function() {
    	//onLoadDefaultElementsBehaviour();
    	$("#owner").change(function() {
			var owner  = $("#owner").val(); 
			if(owner != undefined && owner != null)
			   	getBusinessUnit(owner);	    
		});
		getAttendees();
		$( "#norganizer" ).val(userName);
   		$('#norganizer').attr('readonly','readonly');
		$('#onceDone').val("N");
		$('#onceDone').checked=true;
		$('#type').val($('#workEffortTypeId').val());
		$('#type').attr('readonly','readonly');
		$('#ownerBuDesc').attr('readonly','readonly');
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
		
		var typeId  = $("#srTypeId").val();
	    if (typeId != "") {
	    	loadSubTypes(typeId);
	    }
	    
	    var userName  = $("#userName").val();
	    var loggedInUserId  = $("#loggedInUserId").val();
	    if(loggedInUserId != undefined && loggedInUserId != null){
			getBusinessUnit(loggedInUserId);
	    	getUsers(loggedInUserId,userName);
	    }
	    	
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
		var m= today.getMinutes();
		today = mm + '/' + dd + '/' + yyyy +" "+hh+":"+m;
		$('#actualStartDate').val(today.toLocaleString([],{hour12: false,dateStyle:"short",timeStyle:"short"}).replace(",",""));
		$('#estimatedStartDate').val(today.toLocaleString([],{hour12: false,dateStyle:"short",timeStyle:"short"}).replace(",",""));
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
	function getAttendees() {
        var userOptionList = '<option value="">Please Select</option>';
        $.ajax({
            type: "GET",
            url:'/common-portal/control/getAttendeeList',
              data: {"partyId": $("#partyId").val(), "externalLoginKey": "${requestAttributes.externalLoginKey!}"},
            async: false,
            success: function(data) {
            	if (data) {
	            if(data.responseMessage=="success"){
	            	for (var i = 0; i < data.attendeesList.length; i++) {
	            		var entry = data.attendeesList[i];
	            		if(entry!=null){
	            		userOptionList += '<option value="'+entry.partyId+'">'+entry.userName+'</option>';
	            		}
	            	}
	            }
	            }
	            }
        });
       $("#requiredAttendees").html(userOptionList);
       $("#optionalAttendees").html(userOptionList);
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
	
	            
</script>