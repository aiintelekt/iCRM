<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<#macro showReceipt instanceId>
<div id="${instanceId!}" class="modal fade" role="dialog">
   <div class="modal-dialog modal-lg" style="width:fit-content">
      <!-- Modal content-->
      <div class="modal-content modal-content-sig">
         <div class="modal-header modal-header-sig">
            <h3 class="modal-title modal-title-sig"><#-- e-signature--></h3>
            <button type="button" class="close" data-dismiss="modal">&times;</button>
         </div>
         <div class="modal-body" id="receiptHtmlContent">
         </div>
         <div class="modal-footer">
         </div>
      </div>
   </div>
</div>

<script>
$(document).ready(function() {



});  

function enablePopup(orderId){
	if(orderId != null && orderId != "" && orderId !="undefined"){
		$.ajax({
			async: false,
			url:'/common-portal/control/getReceiptHtmlData',
			type:"POST",
			data: {'orderId':orderId},
			success: function(data){
				if(data.data)
					$("#receiptHtmlContent").html(data.data);
				else
					$("#receiptHtmlContent").html("No content found");
				$('#${instanceId!}').modal("show");
			}
		});
	}
}
</script>
</#macro>
