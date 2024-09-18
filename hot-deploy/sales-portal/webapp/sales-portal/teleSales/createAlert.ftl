<div id="createAlert" class="modal fade" role="dialog">
	<div class="modal-dialog modal-lg">
    	<!-- Modal content-->
    	<div class="modal-content">
        	<div class="modal-header">
            	<h4 class="modal-title">${uiLabelMap.createCustomerAlert!}</h4>
                	<button type="reset" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body">
            	<form method="post" action="createCustomerAlert" id="createAlert" class="form-horizontal" name="" novalidate="novalidate" data-toggle="validator">
                	<div class="row p-2">
                  		<div class="col-md-12 col-lg-6 col-sm-12 ">
                    		<#assign salesOpportunityId = '${requestParameters.salesOpportunityId?if_exists}'>
                    		<#assign customerId = "">
                    		<#assign customerName = "">
                    		<#assign firstName = "">
                    		<#assign srNumber = '${requestParameters.srNumber?if_exists}'>
                    		
                    		<@inputHidden name="srNumber" id="srNumber" value="${requestParameters.srNumber?if_exists}"/>
                    		<@inputHidden name="salesOpportunityId" id="salesOpportunityId" value="${requestParameters.salesOpportunityId?if_exists}"/>
                    		
                    		
                    		<#if salesOpportunityId?has_content>
                    			<#assign customerList = (Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("firstName","middleName","lastName","customerId").from("SalesOpportunitySummary").where("salesOpportunityId",salesOpportunityId).queryOne())?if_exists />
								<#if customerList?has_content>
                					<#assign firstName = "${customerList.firstName?if_exists}"> 
                					<#assign middleName = "${customerList.middleName?if_exists}"> 
                					<#assign lastName = "${customerList.lastName?if_exists}">
                					<#assign customerId = "${customerList.customerId?if_exists}">
                				</#if>  
                    			<@inputHidden name="alertEntityName" id="alertEntityName" value="SALES_OPPORTUNITY"/>
                    			<@inputHidden name="alertEntityReferenceId" id="alertEntityReferenceId" value="${salesOpportunityId}"/>
                    		<#elseif srNumber?has_content>
                    		   <#assign custRequestSrSummaryDetails = (Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("custRequestId","cinNumber","customerName").from("CustRequestSrSummary").where("custRequestId",srNumber).queryOne())?if_exists />
                    			<#if custRequestSrSummaryDetails?has_content>
                    				<#assign customerId = "${custRequestSrSummaryDetails.cinNumber?if_exists}"> 
                    				<#assign customerName = "${custRequestSrSummaryDetails.customerName?if_exists}"> 
                    			</#if>
                    		
                    			<@inputHidden name="alertEntityName" id="alertEntityName" value="SERVICE_REQUEST"/>
                    			<@inputHidden name="alertEntityReferenceId" id="alertEntityReferenceId" value="${srNumber}"/>
                    		 <#elseif workEffortId?has_content>
                    		   <#assign workEffortCallSummaryDetails = (Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("custRequestId","cinNumber","customerName").from("WorkEffortCallSummary").where("workEffortId",workEffortId).queryOne())?if_exists />
                    			<#if workEffortCallSummaryDetails?has_content>
                    				<#assign customerId = "${workEffortCallSummaryDetails.cinNumber?if_exists}"> 
                    				<#assign customerName = "${workEffortCallSummaryDetails.customerName?if_exists}"> 
                    			</#if>
                    		
                    			<@inputHidden name="alertEntityName" id="alertEntityName" value="WORK_EFFORT"/>
                    			<@inputHidden name="alertEntityReferenceId" id="alertEntityReferenceId" value="${workEffortId}"/>
                    		</#if>
                    		
                    		
                    		<#assign priority = Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("enumId","description").from("Enumeration").where("enumTypeId","ALERT_PRIORITY").orderBy("sequenceId").queryList()?if_exists />    
	                    	<#assign priorityList = Static["org.fio.admin.portal.util.DataUtil"].getMapFromGeneric(priority,"enumId","description",false)?if_exists />
	                    		<#if firstName?has_content>
	                    			<@inputRow id="customer" value="${firstName?if_exists} ${middleName?if_exists} ${lastName?if_exists}" label=uiLabelMap.customer placeholder="Customer"  required=true readonly=true/>
	                    		<#elseif customerName?has_content>
	                    			<@inputRow id="customer" value="${customerName?if_exists}" label=uiLabelMap.customer placeholder="Customer"  required=true readonly=true/>
	                    		<#else>
	                    			<@inputRow id="customer" value="${customerId?if_exists}" label=uiLabelMap.customer placeholder="Customer"  required=true readonly=true/>
	                    		</#if>
	                    		
                    			<#assign alertType = Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("alertTypeId","alertTypeDescription").from("AlertType").queryList()?if_exists />    
	                    		<#assign alertTypeList = Static["org.fio.admin.portal.util.DataUtil"].getMapFromGeneric(alertType,"alertTypeId","alertTypeDescription",false)?if_exists /> 
	    						<@dropdownCell
	                    			id="alertTypeId"
	                  				label=uiLabelMap.type
	                    			required=true
	                    			options=alertTypeList
	                    			placeholder="Please Select"
                    				allowEmpty=true
                    				dataError="Please fill out this field."
	                    		/> 
	                    		<@dropdownCell
	                    			id="alertCategoryId"
	                  				label=uiLabelMap.alertName
	                    			required=true
	                    			placeholder="Please Select"
                    				allowEmpty=true
                    				dataError="Please fill out this field."
	                    		/> 
	                    		<@dropdownCell
                    				id="alertPriority"
                  					label=uiLabelMap.priority
                    				required=true
                    				placeholder="Please Select"
                    				allowEmpty=true
                    				dataError="Please fill out this field."
                    			/>
                    			<@inputArea
                    				id="alertInfo"
                    				label="${uiLabelMap.message!}"
                    				placeholder="${uiLabelMap.message!}"
                    				required=true
                    				maxlength=100
                    			/>
                		</div>
                 		<div class="col-md-12 col-lg-6 col-sm-12 ">
                        	<@inputDate id="alertStartDate" label=uiLabelMap.startDate placeholder="Alert Start Date" required=true/>
                     		<@inputRow id="alertAutoClosureDuration" label=uiLabelMap.duration placeholder="Duration" required=false/>
                     		<#assign unitTypes = Static["org.fio.admin.portal.util.DataUtil"].toLinkedMap("DAYS","Days","WEEKS","Weeks","MONTHS","Months") />
 							<@dropdownCell
                    			id="unitTypeId"
                  				label=uiLabelMap.unitType
                    			required=true
                    			options=unitTypes!
                    			placeholder="Please Select"
                    			allowEmpty=true
                    			dataError="Please fill out this field."
                    		/> 
                    		<@inputRow id="alertEndDate" label=uiLabelMap.expiration placeholder="Alert End Date" required=false readonly = true/>
                    		<#assign alertStatusList = Static["org.fio.admin.portal.util.DataUtil"].toLinkedMap("Y","Active","N","In Active") />
 							<@dropdownCell
                    			id="alertStatusId"
                  				label=uiLabelMap.alertstatus
                    			required=true
                    			placeholder="Please Select"
                    			allowEmpty=true
                    			dataError="Please fill out this field."
                    		/> 
                		</div>
         	    	</div>
         	    	<div class="modal-footer">
		         	    <@formButton
		                	btn1type="submit"
		                    btn1label="${uiLabelMap.Save}"
		                    btn1onclick="return formSubmission();"
		                    btn2=false
		                />
               			<@cancel  label="${uiLabelMap.close}"/>
            		</div>
      			</form>
   			</div>
   			
   		</div>
    </div>
</div>
<script>

	function formSubmission(){
		var valid = true;
		if($('#unitTypeId').val() == ""){
 			$('#unitTypeId_error').html($('#unitTypeId').attr('data-error'));
 			$('#unitTypeId_error').show();
 				valid = false;
 		}else{
 				$('#unitTypeId_error').hide();	
 		}
 		if($('#alertTypeId').val() == ""){
 			$('#alertTypeId_error').html($('#alertTypeId').attr('data-error'));
 			$('#alertTypeId_error').show();
 				valid = false;
 		}else{
 				$('#alertTypeId_error').hide();	
 		}
 		if($('#alertCategoryId').val() == ""){
 			$('#alertCategoryId_error').html($('#alertCategoryId').attr('data-error'));
 			$('#alertCategoryId_error').show();
 				valid = false;
 		}else{
 				$('#alertCategoryId_error').hide();	
 		}
 		
 		if($('#alertStartDate').val() == ""){
 			$("#alertStartDate_error").empty();
 			$('#alertStartDate_error').html($('#alertStartDate').attr('data-error'));
 			$("#alertStartDate_error").append('<ul class="list-unstyled"><li>Please fill out this field.</li></ul>');
 			$('#alertStartDate_error').show();
 				valid = false;
 		}else{
 				$('#alertStartDate_error').hide();	
 		}
 		if($('#alertInfo').val() == ""){
 			$("#alertInfo_error").empty();
 			$('#alertInfo_error').html($('#alertInfo').attr('data-error'));
 			$("#alertInfo_error").append('<ul class="list-unstyled"><li>Please fill out this field.</li></ul>');
 			$('#alertInfo_error').show();
 				valid = false;
 		}else{
 				$('#alertInfo_error').hide();	
 		}
 		
		return valid;
	}
	
	$("#alertInfo").focus(function() {}).blur(function() {
   		var alertInfo  = $("#alertInfo").val();
	    if (alertInfo != "") {
	 		$('#alertInfo_error').hide();
	    }else{
	    	$('#alertInfo_error').html($("#alertInfo").attr('data-error'));
	 		$('#alertInfo_error').show();
	    }
	});
	
	$("#alertStartDate").focus(function() {}).blur(function() {
   		var alertStartDate  = $("#alertStartDate").val();
	    if (alertStartDate != "") {
	 		$('#alertStartDate_error').hide();
	    }else{
	    	$('#alertStartDate_error').html($("#alertStartDate").attr('data-error'));
	 		$('#alertStartDate_error').show();
	    }
	});
	
	$("#alertTypeId").change(function() {
   		var alertTypeId  = $("#alertTypeId").val();
  		$(".alertCategoryId .clear").click();
  		$(".alertPriority .clear").click();
  		$(".alertStatusId .clear").click();
   		if (alertTypeId != "") {
   			$('#alertTypeId_error').hide();
      		loadAlertCategory(alertTypeId);
   		}else{
   			$('#alertTypeId_error').html($("#alertTypeId").attr('data-error'));
	 		$('#alertTypeId_error').show();
	 		$("#alertCategoryId").empty();
	 		$("#alertPriority").empty();
	 		$("#alertStatusId").empty();
   		}
	});
	
	function loadAlertCategory(alertTypeId) {
        var nonSelectContent = "<span class='nonselect'>Please Select</span>";
        var categoryOptions = '<option value="" data-content="'+nonSelectContent+'" selected="">Please Select</option>';
        $.ajax({
            type: "POST",
            url: "getAlertCategoryData",
            data: { "alertTypeId": alertTypeId },
            async: false,
            success: function(data) {
                   var sourceDesc = data.results;
                   if(data != "" && data != undefined && data != null){
                   		for (var i = 0; i < data.length; i++) {
                        	var category = data[i];
                        	categoryOptions += '<option value="'+category.alertCategoryId+'">'+category.alertCategoryName+'</option>';
                    	}
                   }
                   
            }
        });
       
        $("#alertCategoryId").html(categoryOptions);
	}
	
	$("#alertCategoryId").change(function() {
   		var alertCategoryId  = $("#alertCategoryId").val();
   		$(".alertPriority .clear").click();
   		$(".alertStatusId .clear").click();
   		if (alertCategoryId == "") {
   			$('#alertCategoryId_error').html($("#alertCategoryId").attr('data-error'));
	 		$('#alertCategoryId_error').show();
	 		$("#alertPriority").empty();
	 		$("#alertStatusId").empty();
   		}else{
   			$('#alertCategoryId_error').hide();
      		loadEachCategory(alertCategoryId);
   		}
	});
	
	function loadEachCategory(alertCategoryId) {
        $.ajax({
            type: "POST",
            url: "getAlertCategoryData",
            data: { "alertCategoryId": alertCategoryId },
            async: false,
            success: function(data) {
            		 if(data != "" && data != undefined && data != null){
            		 	var sourceDesc = data.results;
                   		for (var i = 0; i < data.length; i++) {
                        	var category = data[i];
                        	var isActive =  category.isActive;
                        	var alertPriority = category.alertPriority;
                        	if(isActive != "" && isActive!= undefined && isActive != null){
                       			$("#alertStatusId").html('<option value="'+category.isActive+'">'+category.isActiveDesc+'</option>');
                       			$(".alertStatusId .icon").addClass('clear');
                        	}
                        	if(alertPriority != "" && alertPriority!= undefined && alertPriority != null){
                        		$("#alertPriority").html('<option value="'+category.alertPriority+'">'+category.alertPriorityDesc+'</option>');
                        		$(".alertPriority .icon").addClass('clear');
                        	}
                    	}
            		 }
            }
        });
       
	}
	
	$("#alertStartDate").blur(function() {
		var alertStartDate  = $("#alertStartDate").val();
		var alertAutoClosureDuration  = $("#alertAutoClosureDuration").val(); 
		var unitTypeId  = $("#unitTypeId").val();
		if (alertStartDate != ""&& alertStartDate != null && unitTypeId != ""&& unitTypeId != null && alertAutoClosureDuration != ""&& alertAutoClosureDuration != null) {
      		loadAlertExpiryDetails();
   		}else{
   	  		$("#alertEndDate").html('');
   		}
	});
	
	$("#unitTypeId").change(function() {
   		var unitTypeId  = $("#unitTypeId").val();
   		
   		if (unitTypeId != "") {
	 		$('#unitTypeId_error').hide();
	    }else{
	    	$('#unitTypeId_error').html($("#unitTypeId").attr('data-error'));
	 		$('#unitTypeId_error').show();
	    }
	    
   		var alertAutoClosureDuration  = $("#alertAutoClosureDuration").val();
   		if (unitTypeId != ""&& unitTypeId != null && alertAutoClosureDuration != ""&& alertAutoClosureDuration != null) {
      		loadAlertExpiryDetails();
   		}else{
   	  		$("#alertEndDate").html('');
   		}
	});
	$("#alertAutoClosureDuration").blur(function() {
   		var alertAutoClosureDuration  = $("#alertAutoClosureDuration").val();
   		var unitTypeId  = $("#unitTypeId").val();
   		if (alertAutoClosureDuration != ""&& alertAutoClosureDuration != null && unitTypeId != ""&& unitTypeId != null) {
      		loadAlertExpiryDetails();
   		}else{
   	  		$("#alertEndDate").html('');
   		}
	});
	
	function loadAlertExpiryDetails() {
		var alertStartDate  = $("#alertStartDate").val();
		var alertAutoClosureDuration  = $("#alertAutoClosureDuration").val(); 
		var unitTypeId  = $("#unitTypeId").val(); 
		
        $.ajax({
            type: "POST",
            url: "getAlertExpiryData",
            data: { "unitTypeId": unitTypeId, "alertStartDate": alertStartDate, "alertAutoClosureDuration": alertAutoClosureDuration},
            async: false,
            success: function(data) {
                  for (var i = 0; i < data.length; i++) {
                   var endData = data[i];
                   $("#alertEndDate").val(endData.endDate);
                  }
            }
        });
       
	}

</script>




