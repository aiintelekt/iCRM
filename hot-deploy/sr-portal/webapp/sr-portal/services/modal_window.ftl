<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<#macro generateProgramActivity instanceId>

<div id="${instanceId!}" class="modal fade" >
  <div class="modal-dialog modal-md">
    <!-- Modal content-->
    <div class="modal-content">
      <div class="modal-header">
        <h2 class="modal-title">Generate Program Activity</h2>
        
        <button type="button" class="close" data-dismiss="modal">&times;</button>
		        
      </div>
      <div class="modal-body">
        
		<form id="${instanceId!}-form" method="post" data-toggle="validator">
		
		<input type="hidden" name="srNumber" value="${srNumber!}" />
		<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}" />
				
		<@dynaScreen 
			instanceId="GEN_PROG_ACT"
			modeOfAction="CREATE"
			isConfigScreen="N"
			/>
			
		<div class="form-group offset-2">
			<div class="text-left ml-3">
		      
		      <@formButton
			     btn1type="button"
			     btn1label="${uiLabelMap.Generate}"
			     btn1id="${instanceId!}-gen-btn"
			     btn2=true
			     btn2type="reset"
			     btn2label="${uiLabelMap.Clear}"
			     btn2id="${instanceId!}-reset-btn"
			   />
				 	
			</div>
		</div>	
				
		</form>
        
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-sm btn-primary navbar-dark" data-dismiss="modal">Close</button>
      </div>
    </div>
  </div>
</div>

<script>
$(document).ready(function() {

$("#${instanceId!}-btn").click(function () {
    $("#${instanceId!}").modal("show");
});  

$("#${instanceId!}-gen-btn").click(function () {
    console.log('click gen btn');
    $('#${instanceId!}-form').submit();
});  


$('#${instanceId!}-form').on('submit', function(e) {
	console.log('submit form');
    if (e.isDefaultPrevented()) {
    	// handle the invalid form...
  	} else {
  		e.preventDefault();
  		$.post("/activity-portal/control/generateProgAct", $('#${instanceId!}-form').serialize(), function(data) {
			if (data.code == 200) {
				showAlert ("success", "Successfully generated program activities");
				$("#${instanceId!}").modal("hide");
				$("#${instanceId!}-reset-btn").click();
			} else {
				showAlert ("error", data.message);
			}
		});
  	}
});  

            
});
</script>
</#macro>
<#macro datePickerModal instanceId fromAction="">
<div id="${instanceId!}" class="modal fade" >
    <div class="modal-dialog modal-sm" style="max-width: 1000px;">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">Closed Date</h4>
                <button type="button" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body">
             <#assign closedDate="">
             <#if srClosedDate?has_content>
             <#assign closedDate=srClosedDate?if_exists>
             </#if>
            	<form method="post" action="#" id="${instanceId!}-datePicker-form" class="form-horizontal" name="" novalidate="novalidate" data-toggle="validator">
                      <input type="hidden" id="${instanceId!}-srClosedDate" value="${srClosedDate!}">
                        <div class="row">
                        <div class="col-md-5 col-sm-3">
                        <@inputDate 
							id="${instanceId!}-closedDate"
							name="closedDate"
							value="${closedDate!}"
							label="Closed Date"
							dateFormat="${globalDateFormat!}"
						/>
                        </div>
                        </div>
                </form>
                <p></p>
            	<div class="modal-footer">
                    <@button id="${instanceId!}-submit-form" class="btn btn-sm btn-primary navbar-dark" label="${uiLabelMap.Save}" />
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-sm btn-primary navbar-dark" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>
<script>
$(document).ready(function() {
$("#${instanceId!}-submit-form").click(function(){
	$("#${instanceId!}-submit-form").attr('disabled',true);
	let srClosedDate = $("#${instanceId!}-srClosedDate").val();
	let closedDate = $("#${instanceId!}-closedDate").val();
	let custRequestId = "${inputContext.custRequestId!}";
	console.log("-closedDate--"+closedDate+"-custRequestId-----"+custRequestId);
	if (closedDate && srClosedDate) {
		let srClosedDate_dt = new Date(srClosedDate);
		let closedDate_dt = new Date(closedDate);
		if (srClosedDate_dt.getTime()==closedDate_dt.getTime()){
			showAlert("error","Please change the date to update the closed date");
			$("#${instanceId!}-submit-form").removeAttr('disabled');
			return false;
		}
	}
	if (closedDate && custRequestId) {
		var input = {};
		input={"srClosedDate":closedDate,"custRequestId":custRequestId};
		$.ajax({
	    	type: "POST",
	        url: "<@ofbizUrl>updateSrClosedDate</@ofbizUrl>",
			async: false,
			data: input,
			success: function (data) {
		    var mesg = data;
		    if (mesg) {
		    	if (mesg == "error") {
		    		$("#${instanceId!}-submit-form").removeAttr('disabled');
					showAlert("error",mesg["message"]);
					return false;
			    }else{
			    	showAlert("success",mesg["message"]);
			    	location.reload();
			    }
	    	}
	        }
	    });
	}else if (!closedDate){
		$("#${instanceId!}-submit-form").removeAttr('disabled');
		showAlert("error","Date required");
	}else{
		$("#${instanceId!}-submit-form").removeAttr('disabled');
		showAlert("error","Custrequest id missing");
	}
});
});
</script> 
</#macro>
<#macro thirdPartyInvDescModal instanceId fromAction="">
<div id="${instanceId!}" class="modal fade">
    <div class="modal-dialog modal-lg" style="max-width: 700px;">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">Attributes Details</h4>
                <button type="button" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body">
            	<@displayCell id="thirdPtyInvPrice-desc" label="Price" value="${inputContext?if_exists.thirdPartyInvoicePrice!}"/>
            	</br>
            	<@displayCell id="thirdPtyInvNumber-desc" label="Third Party Invoice Numbers" value="${inputContext?if_exists.thirdPartyInvoiceNumber!}"/>
            	
               
                <div class="form-group offset-2">
                    <div class="text-left ml-3">
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-sm btn-primary navbar-dark" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>

</#macro>