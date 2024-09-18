<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>



<div class="row">
	<div id="main" role="main" class="pd-btm-title-bar">
        
		<#assign extraLeft='
	        <a id=task1" title="Task1" href="#" class="btn btn-primary btn-xs" onclick="#"> Task 1</a>
	        <a id="task2" title="Task2" href="#" class="btn btn-primary btn-xs" onclick="#">Task 2</a>
	        <a id="task3" title="Task3" href="#" class="btn btn-primary btn-xs" onclick="#">Task 3</a>
	        <a id="task4" title="Task4" href="#" class="btn btn-primary btn-xs" onclick="#">Task 4</a>
	        <a id="task5" title="Task5" href="#" class="btn btn-primary btn-xs" onclick="#">Task 5</a>
        ' />
        
        <@sectionFrameHeader  title="Recently Viewed:"  extraLeft=extraLeft  />
        
        <#assign addActivities = '
        	<div class="dropdown-menu" aria-labelledby="E10007">
                <h4>Add Activities</h4>
                <a class="dropdown-item" href="/lead-portal/control/createTaskActivity?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank"><i class="fa fa-clipboard" aria-hidden="true"></i> Task</a> 
			    <a class="dropdown-item" href="/lead-portal/control/createPhoneCallActivity?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank"><i class="fa fa-phone" aria-hidden="true"></i> Phone Call</a>
			    <a class="dropdown-item" href="/sales-portal/control/addEmail?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank"><i class="fa fa-envelope" aria-hidden="true"></i> Email</a>
			    <a class="dropdown-item" href="/lead-portal/control/createAppointmentActivity?partyId=${partyId?if_exists}&domainEntityType=${domainEntityType!}&domainEntityId=${domainEntityId!}&externalLoginKey=${requestAttributes.externalLoginKey!}" target="_blank"><i class="fa fa-calendar" aria-hidden="true"></i> Appointment</a>
            </div>
        ' />
        
        <#assign toggleDropDownData = {"E10007":addActivities!} />
        
        <div class="col-lg-12 col-md-12 col-sm-12">
        	<div class="card-head margin-adj mt-2">
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
	           <div class="card-header mt-3" id="cp">
	           		<@dynaScreen 
		                instanceId="LEAD_BASIC_INFO"
		                modeOfAction="VIEW"
		            />
	           </div>
           </#if>  
       	
            <div class="col-lg-12 col-md-12 col-sm-12">
        		<@pageSectionHeader title="SR Details"/>
        	</div>
             
            <form id="mainFrom" method="post" action="<@ofbizUrl>createServiceRequestAction</@ofbizUrl>" data-toggle="validator"> 
	        	<div class="col-lg-12 col-md-12 col-sm-12">
	        		<#assign cifNo = '${requestParameters.partyId!}' >
                	<@inputHidden  id="cNo" value = "${cifNo!}"/>
					<@inputHidden id="owner" value="${userLogin.userLoginId?if_exists}" />
                    
                    <#assign userName = userLogin.userLoginId>
                    <#assign findMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", userLogin.partyId)>
                    <#assign person = delegator.findOne("Person", findMap, true)!>
                    <#if person?has_content>
                    	<#assign userName = (person.firstName!) + " " + (person.middleName!) + " " + person.lastName!>
                    	<@inputHidden id="userName" value="${userName!}"/>
                    </#if>
	               							
	                <@dynaScreen 
		                instanceId="CREATE_SERVICE_REQUEST"
		                modeOfAction="CREATE"
		             />
	            </div>
	            
	            <div class="col-md-12 col-lg-12 col-sm-12 ">
         			<@textareaLarge  label="Description" id="description" rows="4"/>
      			</div>
      			
      			<div class="col-md-12 col-lg-12 col-sm-12 ">
         			<@textareaLarge  label="Resolution" id="resolution" rows="4"/>
      			</div>
      			
      			<div class="col-md-12 col-lg-12 col-sm-12 ">
         			<@textareaLarge  label="Notes" id="notes" rows="4"/>
      			</div>
      			
	           	<div class="offset-md-2 col-sm-10">
	           		<@formButton
	                     btn1type="submit"
	                     btn1label="${uiLabelMap.Save}"
	                     btn2=true
	                     btn2onclick = "resetForm()"
	                     btn2type="reset"
	                     btn2label="${uiLabelMap.Clear}"
	                />
	            </div>
        	</form>
        </div>
    </div>
</div>

<script>

	$(document).ready(function() {
		
		$('#ownerr').val($('#userName').val());
		var owner  = $("#owner").val();
		$('#ownerr').attr('readonly','readonly');
		$('#ownerBu').attr('readonly','readonly'); 
		if(owner != undefined && owner != null)
		 	getBusinessUnit(owner);
	});

	 function formSubmission(){
	 	
	 	var valid = true;
	 	if($('#cNo').val() == ""){
	 		showAlert('error','Please select Customer');
	 		valid = false;
	 	}
	 	
	 	if($('#srTypeId').val() == ""){
	 		$('#srTypeId_error').html($('#srTypeId').attr('data-error'));
	 		$('#srTypeId_error').show();
	 		valid = false;
	 	}else{
	 		$('#srTypeId_error').hide();	
	 	}
	 	
	 	if($('#srCategoryId').val() == ""){
	 		$('#srCategoryId_error').html($('#srCategoryId').attr('data-error'));
	 		$('#srCategoryId_error').show();
	 		valid = false;
	 	}else{
	 		$('#srCategoryId_error').hide();	
	 	}
	 	if($('#srSubCategoryId').val() == ""){
	 		$('#srSubCategoryId_error').html($('#srSubCategoryId').attr('data-error'));
	 		$('#srSubCategoryId_error').show();
	 		valid = false;
	 	}else{
	 		$('#srSubCategoryId_error').hide();	
	 	}
	 	
	 	if($('#srStatusId').val() == ""){
	 		$('#srStatusId_error').html($('#srStatusId').attr('data-error'));
	 		$('#srStatusId_error').show();
	 		valid = false;
	 	}else{
	 		$('#srStatusId_error').hide();	
	 	}
	 	
	 	if(!valid)
	 		$('#back-to-top').click();
	 	return valid;
	 }
 	
	$("#srTypeId").change(function() {
   		var srTypeId  = $("#srTypeId").val();
	    if (srTypeId != "") {
	 		$('#srTypeId_error').hide();
	        loadCategory(srTypeId);
	    }else{
	    	$("#srCategoryId").html('');
	    	//$("#srSubCategoryId").html('');
	    	$('.srCategoryId .clear').click();
	    	$('.srSubCategoryId .clear').click();
	    	$('.srSubCategoryId .icon').removeClass('clear');
	    	$('#srTypeId_error').html($("#srTypeId").attr('data-error'));
	 		$('#srTypeId_error').show();
	    }
	});
	
	$("#srCategoryId").change(function() {
	   var srCategoryId  = $(this).val();
	   var srTypeId  = $("#srTypeId").val();
	   if (srCategoryId != "") {
	   		$('#srCategoryId_error').hide();
	       loadSubCategory(srTypeId , srCategoryId);
	   }else{
	   		$('.srSubCategoryId .clear').click();
	   		if(srTypeId != ""){
	   	  		$('#srCategoryId_error').html($("#srCategoryId").attr('data-error'));
	 	  		$('#srCategoryId_error').show();
	 	  	}else{
	 	  		$('#srCategoryId_error').hide();
	 	  	}
	   }
	});
	
	$("#srSubCategoryId").change(function() {
	   if($(this).val() == "" && $("#srCategoryId").val() != ""){
	  		$('#srSubCategoryId_error').html($("#srSubCategoryId").attr('data-error'));
	  		$('#srSubCategoryId_error').show();
	  	}
	   	else{
	   		$('#srSubCategoryId_error').hide();
	   	}
	});
	
	$("#srStatusId").change(function() {
	   var srStatusId  = $("#srStatusId").val();
	   if (srStatusId != "") {
	   		$('#srStatusId_error').hide();
	   }else{
	   	  $("#srStatusId_error").show('');
	   }
	});
	
	function loadCategory(srTypeId) {
        var nonSelectContent = "<span class='nonselect'>Please Select</span>";
        var categoryOptions = '<option value="" data-content="'+nonSelectContent+'" selected="">Please Select</option>';
        $.ajax({
            type: "POST",
            url: "getSrCategory",
            data: { "srTypeId": srTypeId },
            async: false,
            success: function(data) {
                   var sourceDesc = data.results;
                   for (var i = 0; i < data.length; i++) {
                        var category = data[i];
                        categoryOptions += '<option value="'+category.srCategoryId+'">'+category.srCategoryDesc+'</option>';
                    }
            }
        });
       
        $("#srCategoryId").html(categoryOptions);
	}
	
	function loadSubCategory(srTypeId,srCategoryId) {
        var nonSelectContent = "<span class='nonselect'>Please Select</span>";
        var subCategoryOptions = '<option value="" data-content="'+nonSelectContent+'" selected="">Please Select</option>';
        $.ajax({
            type: "POST",
            url: "getSrSubCategory",
            data: { "srTypeId": srTypeId , "srCategoryId": srCategoryId },
            async: false,
            success: function(data) {
                   var sourceDesc = data.results;
                   for (var i = 0; i < data.length; i++) {
                        var category = data[i];
                        subCategoryOptions += '<option value="'+category.srSubCategoryId+'">'+category.srSubCategoryDesc+'</option>';
                    }
            }
        });
       
        $("#srSubCategoryId").html(subCategoryOptions);
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
	
</script>