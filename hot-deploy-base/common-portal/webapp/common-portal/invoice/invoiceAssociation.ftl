<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/lib/picker_macro.ftl"/>
   
  <div class="pt-2">
     
                 
</div>         
		    	<@AgGrid
					userid="${userLogin.userLoginId}" 
					instanceid="INVOICE_ASSOC_LIST" 
					autosizeallcol="false"
					shownotifications="true"
		            autosizeallcol="true"
		            debug="false"
		            gridheadertitle="Invoice Associations"
			    	gridheaderid="listofappliedpayments"
			    	statusBar=true
			    	serversidepaginate=false
			    	headerextra=headerextra!
			    	insertBtn=false
			    	updateBtn=false
			    	removeBtn=false
					/>
                <script type="text/javascript" src="/accounting-portal-resource/js/ag-grid/services/invoice-assoc-list.js"></script>
  

