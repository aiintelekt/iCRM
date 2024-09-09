<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<#macro datePickerModal instanceId fromAction="">
<div id="${instanceId!}" class="modal fade" >
    <div class="modal-dialog modal-sm" style="max-width: 1000px;">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">Date</h4>
                <button type="button" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body">
             <#assign agreementDet = delegator.findOne("Agreement", {"agreementId" : parameters.agreementId?if_exists}, false)>
             <#assign fromDate="">
             <#assign thruDate="">
             <#if agreementDet?has_content>
             <#assign fromDate=agreementDet.get("fromDate")?string("MM/dd/yyyy")?if_exists>
             <#assign thruDate=agreementDet.get("thruDate")?string("MM/dd/yyyy")?if_exists>
             </#if>
            	<form method="post" action="#" id="${instanceId!}-datePicker-form" class="form-horizontal" name="" novalidate="novalidate" data-toggle="validator">
                        <div class="row">
                        <input type="hidden" name="modalAction" id="${instanceId!}-modalAction" value=""/>
                        <div class="col-md-5 col-sm-3 ">
                        <@inputDate 
						id="${instanceId!}-fromDate"
						value="${fromDate!}"
						label="From Date"
						dateFormat="MM/DD/YYYY"
						minDate="${fromDate!}"
						maxDate="${thruDate!}"
						/>
                        </div>
                         <div class="col-md-5 col-sm-3 ">
						 <@inputDate 
						id="${instanceId!}-thruDate"
						value="${thruDate!}"
						label="Thru Date"
						dateFormat="MM/DD/YYYY"
						minDate="${fromDate!}"
						maxDate="${thruDate!}"
						/>
                        </div>
                        </div>
                </form>
                <p></p>
            	<div class="modal-footer">
                    <@button id="${instanceId!}-submit-form" class="btn btn-sm btn-primary navbar-dark" label="${uiLabelMap.Save}" />
                    <@reset id="${instanceId!}-reset-form" label="${uiLabelMap.Reset}" class="btn btn-sm btn-primary navbar-dark"/>
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
	$("#${instanceId!}-reset-form").click(function () {
		$("#${instanceId!}-datePicker-form")[0].reset();
	});
});
</script> 
</#macro>
