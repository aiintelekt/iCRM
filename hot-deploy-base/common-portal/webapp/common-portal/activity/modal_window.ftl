<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<#assign requestURI = "viewContact"/>
<#if request.getRequestURI().contains("viewLead")>
<#assign requestURI = "viewLead"/>
<#elseif request.getRequestURI().contains("viewAccount")>
<#assign requestURI = "viewAccount"/>
<#elseif request.getRequestURI().contains("viewCustomer")>
<#assign requestURI = "viewCustomer"/>
</#if>

<#macro createActivityModal instanceId fromAction="">

<div id="${instanceId!}" class="modal fade" >
  <div class="modal-dialog modal-xl">
    <!-- Modal content-->
    <div class="modal-content">
      <div class="modal-header">
        <h2 class="modal-title">Create Activity</h2>
        
        <button type="button" class="close" data-dismiss="modal">&times;</button>
		        
      </div>
      <div class="modal-body">
        
		<form method="post" action="<@ofbizUrl>createServiceRequest</@ofbizUrl>" data-toggle="validator">
		
		<input type="hidden" name="activeTab" value="serviceRequests" />
        <input type="hidden" name="donePage" value="${requestURI?if_exists}"/>
        <input type="hidden" name="partyId" value="${partyId?if_exists}" />
		
		<@dynaScreen 
			instanceId="CREATE_SR"
			modeOfAction="CREATE"
			isConfigScreen="N"
			/>
			
		<div class="form-group offset-2">
			<div class="text-left ml-3">
		      
		      <@formButton
			     btn1type="submit"
			     btn1label="${uiLabelMap.Save}"
			     btn2=true
			     btn2type="reset"
			     btn2label="${uiLabelMap.Clear}"
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

</#macro>

<#macro actScheduleModal instanceId fromAction="">
<div id="${instanceId!}" class="modal fade" >
    <div class="modal-dialog modal-lg">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">Schedule</h4>
                <button type="button" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body">
                <form id="act-schedule-form" method="post" data-toggle="validator">
                    <input type="hidden" name="workEffortId" value="${requestParameters.workEffortId!requestAttributes.workEffortId!}"/>
                    <input type="hidden" name="entryId">
                    
                    <@dynaScreen 
                    instanceId="ACT_RES_AVAIL_BASE"
                    modeOfAction="CREATE"
                    />
                    
                    <div class="form-group offset-2">
                        <div class="text-left ml-3">
                            <@formButton
                            btn1type="submit"
                            btn1label="${uiLabelMap.Save}"
                            btn2=true
                            btn2id="note-reset-btn"
                            btn2type="reset"
                            btn2label="${uiLabelMap.Clear}"
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
    
});
    
function editActSchedule(entryId) {
	$('#${instanceId!}').modal('show');
	
	$.ajax({
		type: "POST",
     	url: "/admin-portal/control/getResAvailData",
        data: {"entryId": entryId, "externalLoginKey": "${requestAttributes.externalLoginKey!}"},
        async: false,
        success: function (result) {
            if (result.code == 200) {
            	$('#act-schedule-form input[name=entryId]').val(result.data.entryId);
            	$('#resourceName').html(result.data.resourceName);
            	if (result.data.fromDate) {
            		var fromDate = result.data.fromDate.split(" ");
            		$('#fromDate_date').val(fromDate[0]);
            		$('#fromDate_time').timepicker('setTime', fromDate[1]);
            	}
            	
            	if (result.data.thruDate) {
            		var thruDate = result.data.thruDate.split(" ");
            		$('#thruDate_date').val(thruDate[0]);
            		$('#thruDate_time').timepicker('setTime', thruDate[1]);
            	}
            }
        }
	}); 
}
</script> 
</#macro>