<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/lib/picker_macro.ftl"/>


   
  <div class="pt-2">
     
                 
</div>         
		    	<#--<#assign headerextra = '<a title="Create Invoice Item" target="_blank" href="createInvoiceItem?invoiceId=${requestParameters.invoiceId!}&externalLoginKey=${requestAttributes.externalLoginKey!}" class="btn btn-primary btn-xs ml-2"> <i class="fa fa-plus" aria-hidden="true"></i> Create Invoice Item</a>' />-->
		    	<@AgGrid
					userid="${userLogin.userLoginId}" 
					instanceid="INVOICE_ITEM_LIST_SR" 
					autosizeallcol="false"
					shownotifications="true"
		            autosizeallcol="true"
		            debug="true"
		            gridheadertitle="Invoice Items"
			    	gridheaderid="listofItems"
			    	statusBar=true
			    	serversidepaginate=false
			    	headerextra=headerextra!
			    	insertBtn=false
			    	updateBtn=false
			    	removeBtn=false
					/>
                <script type="text/javascript" src="/common-portal-resource/js/ag-grid/invoice/invoice-item-list.js"></script>
  

