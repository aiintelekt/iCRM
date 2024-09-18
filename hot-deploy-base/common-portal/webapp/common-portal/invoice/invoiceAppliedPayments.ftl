<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/lib/picker_macro.ftl"/>
<#include "component://accounting-portal/webapp/accounting-portal/invoice/create-payment-model.ftl"/>
   
  <div class="pt-2">
     
                 
</div>         
<#-- <#if inputContext.canDoPayment?has_content && inputContext.canDoPayment == "Y">
<#assign headerextra = ' <input type="button" id="create-payment-btn" class="btn btn-primary btn-xs ml-2"  onclick="" value="Create Payment"/> 
<input type="button" id="remove-payment-btn" class="btn btn-primary btn-xs ml-2"  onclick="" value="Remove"/>' />
</#if> -->
		  		   <@AgGrid
					userid="${userLogin.userLoginId}" 
					instanceid="INVOICE_APPLIED_PAY_SR" 
					autosizeallcol="false"
					shownotifications="true"
		            autosizeallcol="true"
		            debug="false"
		            gridheadertitle="Invoice Applied Payments"
			    	gridheaderid="listofappliedpayments"
			    	statusBar=true
			    	serversidepaginate=false
			    	headerextra=headerextra!
			    	insertBtn=false
			    	updateBtn=false
			    	removeBtn=false
					/>
                <script type="text/javascript" src="/common-portal-resource/js/ag-grid/invoice/invoice-ap-list.js"></script>
  <@createPaymentModal 
	instanceId="create-payment-modal"
	path=false
	/>	
	<@updatePaymentModal 
	instanceId="update-payment-modal"
	path=false
	/>	
<script>

jQuery(document).ready(function() {

$('#create-payment-btn').on('click', function() {
	$('#create-payment-modal').modal("hide");
	$('#create-payment-modal').modal("show");
});



});

</script>
