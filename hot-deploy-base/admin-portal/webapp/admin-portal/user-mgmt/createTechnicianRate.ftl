<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<div class="row">
   <div id="main" role="main">
		
      <div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
      	
	  <@sectionFrameHeaderTab title="Create Technician Rate Config" />
	      
	  <form name="createTechnicianRate" action="createTechnicianRateAction" method="post" data-toggle="validator" onsubmit="return submitTechnicalRateForm();">
        
        <@inputHidden 
       	  	id="defaultRate"
       	  	value="N"
       	  	/>
       	  	
        <@dynaScreen 
            instanceId="CREATE_TECHNICIAN_RATE"
            modeOfAction="CREATE"
         />
		             
		<div class="offset-md-2 col-sm-10 p-2">
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
   </div> <#-- main end -->
</div> <#-- row end-->

<script type="text/javascript">
   
$(document).ready(function(){
    getTechnicianUsers();	
    $("#partyId").change(function() {
		var partyIdVal  = $(this).val();
		if(partyIdVal && partyIdVal === "company"){
			$("#defaultRate").val("Y");
		}
	});
});

function getTechnicianUsers() {
    var userOptionList = '<option value=""></option>';
    $.ajax({
        type: "GET",
        url:'/common-portal/control/getUsersList?roleTypeId=TECHNICIAN&externalLoginKey=${requestAttributes.externalLoginKey!}',
        async: false,
        success: function(data) {
        	userOptionList += '<option value="company">Standard Rates</option>';
        	for (var i = 0; i < data.length; i++) {
                var type = data[i];
                userOptionList += '<option value="'+type.partyId+'">'+type.userName+' ('+ type.roleDesc +') </option>';
            }
        }
    });
   $("#partyId").html(userOptionList);
}
 
function submitTechnicalRateForm(){

	var valid = false;
	var dateValidationChk = true;
	
	var fromDate = $("#fromDate").val();
	var thruDate = $("#thruDate").val();
	
	var lastUpdatedThruDate;
	
	var partyId = $('#partyId').val();
    var rateTypeId = $("#rateTypeId").val();
    var currencyUomId = $("#currencyUomId").val();
    var defaultRate = $("#defaultRate").val();
        
	$.ajax({
		type: "POST",
		url:'/admin-portal/control/getTechnicianLastUpdatedDate?externalLoginKey=${requestAttributes.externalLoginKey!}',
		async: false,
		data: { "partyId": partyId,
		"rateTypeId": rateTypeId,
		"currencyUomId": currencyUomId,
		"defaultRate": defaultRate },
		success: function(data) {
			if(data.length > 0){
				var item = data[0];
				lastUpdatedThruDate = item.thruDate;
			}else{
				dateValidationChk = false;
				valid = true;
			}
		},error: function(data) {
		
		}
	});
	
	startDate = new Date(fromDate);
	endDate = new Date(thruDate);
	
	if(fromDate != "" && thruDate != ""){
		if(dateValidationChk){
			lastUpdatedDate = new Date(lastUpdatedThruDate);
			
			if ((startDate - lastUpdatedDate) == 0) {
				valid = false;
			} else if (startDate > lastUpdatedDate) {
				valid = true;
			} else {
				valid = false;
			}
		}
		
		if(!valid){
			showAlert("error", "Rate is Already Configured for the Rate Type, within this Date Range");
		}else{
			if ((startDate - endDate) == 0) {
				valid = false;
			} else if (startDate < endDate) {
				valid = true;
			} else {
				valid = false;
			}
			if(!valid){
				showAlert("error", "Please select thru date greater than from date");
			}
		}
	}
	
	return valid;
}   

</script>
