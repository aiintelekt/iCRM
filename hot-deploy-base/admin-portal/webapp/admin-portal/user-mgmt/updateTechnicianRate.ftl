<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<div class="row">
   <div id="main" role="main">
		
      <div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
      	
	  <@sectionFrameHeaderTab title="Update Technician Rate Config" />
	      
	  <form name="updateTechnicianRate" action="updateTechnicianRateAction" method="post" data-toggle="validator" onsubmit="return submitTechnicalRateForm();">
        
        <@inputHidden id="partyId" value="${parameters.partyId?if_exists}" />
        <@inputHidden id="rateTypeId" value="${parameters.rateTypeId?if_exists}" />
        <@inputHidden id="currencyUomId" value="${parameters.uomId?if_exists}" />
        <@inputHidden id="fromDate" value="${parameters.fromDate?if_exists}" />
       	  	
        <@inputHidden 
       	  	id="defaultRate"
       	  	value="N"
       	  	/>
       	  	
        <@dynaScreen 
            instanceId="CREATE_TECHNICIAN_RATE"
            modeOfAction="UPDATE"
         />
		             
		<div class="offset-md-2 col-sm-10 p-2">
       		<@formButton
                 btn1type="submit"
                 btn1label="${uiLabelMap.Save}"
                 btn2=true
                 btn2onclick = "resetThruDate()"
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
    
    $('#partyDesc').attr('readonly','readonly');
    $('#rateTypeDesc').attr('readonly','readonly');
    $('#currencyUomDesc').attr('readonly','readonly');
    $('#fromDateDesc').attr('readonly','readonly');
    $('#rate').attr('readonly','readonly');
    	
});

function getTechnicianUsers() {
    var userOptionList = '<option value=""></option>';
    $.ajax({
        type: "GET",
        url:'/common-portal/control/getUsersList?roleTypeId=ACT_OWNER&externalLoginKey=${requestAttributes.externalLoginKey!}',
        async: false,
        success: function(data) {
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
	
	var fromDate = $("#fromDate").val();
	var thruDate = $("#thruDate").val();
	
	startDate = new Date(fromDate);
	endDate = new Date(thruDate);
	
	if(fromDate != "" && thruDate != ""){
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
		return valid;
	}
}

function resetThruDate(){
	$("#thruDate").val("");
}
</script>
