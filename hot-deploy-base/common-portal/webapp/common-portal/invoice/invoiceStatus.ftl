<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/lib/picker_macro.ftl"/>

 <#--  <#if inputContext.statusId?has_content && (inputContext.statusId != "INVOICE_CANCELLED")&& ( inputContext.statusId != "INVOICE_PAID") && ( inputContext.statusId != "INVOICE_VOID")>
  <ul class="flot-icone">
  <#if inputContext.canSetToPaid?has_content && inputContext.canSetToPaid == 'N'>
    <input type="button" id="status-to-sent" class="btn btn-primary btn-xs ml-2"  onclick="" value="Status to 'APPROVED'"/>
   </#if>
     <input type="button" id="status-to-paid" class="btn btn-primary btn-xs ml-2"  onclick="" value="Status to 'PAID'"/>
 	<input type="button" id="status-to-void" class="btn btn-primary btn-xs ml-2"  onclick="" value="Status to 'VOID'"/>
 	<input type="button" id="status-to-cancelled" class="btn btn-primary btn-xs ml-2"  onclick="" value="Status to 'CANCELLED'"/>
</ul>
</#if>-->
<@pageSectionHeader title="Invoice Status" extra=extra/>
<div class="pt-2"></div>       
 <div class="contact-table">
<table class="table table-striped">
   <thead>
      <tr>
       
         <th>Status date</th>
         <th>Status</th>
         <th>Change By User Login Id</th>
      </tr>
   </thead>
   <tbody>
   <#if inputContext.InvoiceStatuses?has_content>
      <#list  inputContext.InvoiceStatuses as invoiceStatus>
      <tr>
         <td>${invoiceStatus.statusDate!}</td>
         <td><#if invoiceStatus.statusId?has_content>
                     <#assign findMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("statusId", invoiceStatus.statusId!,"statusTypeId","INVOICE_STATUS")>
                    <#assign invoiceStatusDes = delegator.findByAnd("StatusItem", findMap,null, true)!>
          			<#assign invoiceStatusDes =  invoiceStatusDes.get(0)/>
           </#if>
         ${invoiceStatusDes.description!}</td>
         <td>${inputContext.userLoginId!}</td>
      </tr>
      </#list>
      <#else>
      <tr>
         <td></td>
         <td>
            
         </td>
         <td>
            
         </td>
         <td>
            
         </td>
         <td>
        
         </td>
         
         <td>
         
         </td>
         <td>
            
            
         </td>
      </tr>
      </#if>
   </tbody>
</table>
 </div>
            </div>
           		
        
    </div>
</div>

<script>
  $("#status-to-sent").click(function () {
		var invoiceStatus = "INVOICE_SENT";
		
		   
		    $.ajax({
				type : "POST",
				url : "/accounting-portal/control/changeInvoiceStatus",
				data: {"invoiceStatus": "INVOICE_SENT", "invoiceId": "${requestParameters.invoiceId!}"},
				async : true,
				success : function(result) {
						showAlert ("success", "Successfully Changed the status ");
						location.reload();
				},
				error : function() {
					console.log('Error occured');
					showAlert("error", "Error occured!");
				},
				complete : function() {
				}
			});
			
	
});
$("#status-to-paid").click(function () {
		    $.ajax({
				type : "POST",
				url : "/accounting-portal/control/changeInvoiceStatus",
				data: {"invoiceStatus": "INVOICE_PAID", "invoiceId": "${requestParameters.invoiceId!}"},
				async : true,
				success : function(data) {
				 var message = data.Error_Message;
				 $.notify({
	                	message : '<p>'+message+'</p>'
	              	});
    				setTimeout(location.reload.bind(location),1000);
				},
				error : function() {
					console.log('Error occured');
					showAlert("error", "Error occured!");
				},
				complete : function() {
				}
			});
			
	
});
$("#status-to-cancelled").click(function () {
		    $.ajax({
				type : "POST",
				url : "/accounting-portal/control/changeInvoiceStatus",
				data: {"invoiceStatus": "INVOICE_CANCELLED", "invoiceId": "${requestParameters.invoiceId!}"},
				async : true,
				success : function(result) {
						showAlert ("success", "Successfully Changed the status ");
						location.reload();
				},
				error : function() {
					console.log('Error occured');
					showAlert("error", "Error occured!");
				},
				complete : function() {
				}
			});
});
$("#status-to-void").click(function () {
		    $.ajax({
				type : "POST",
				url : "/accounting-portal/control/changeInvoiceStatus",
				data: {"invoiceStatus": "INVOICE_VOID", "invoiceId": "${requestParameters.invoiceId!}"},
				async : true,
				success : function(result) {
						showAlert ("success", "Successfully Changed the status ");
						location.reload();
				},
				error : function() {
					console.log('Error occured');
					showAlert("error", "Error occured!");
				},
				complete : function() {
				}
			});
});
</script>

