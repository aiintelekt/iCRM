<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>

<div class="orderview-detail">
			
<#if InvoiceByIdViewList?exists>
<div>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr class="ordersec-responsive">
    <td width="50%" valign="top">
      <#include "component://common-portal/webapp/common-portal/invoice/view/InvoiceInfoPage.ftl"/>	
  		
    </td>
   <td width="10" nowrap="nowrap" class="td-res-hide">&nbsp;<div>&nbsp;&nbsp;</div></td>
    <td width="50%" valign="top">
     <#include "component://common-portal/webapp/common-portal/invoice/view/InvoiceContactInfoView.ftl"/>
    </td>
  </tr>
 
</table>
</div>

<div >
	<#include "component://common-portal/webapp/common-portal/invoice/view/InvoiceItemsViewPage.ftl"/>
</div>

<#else/>
  <p class="tableheadtext">${uiLabelMap.CrmOrderNotFound!}</p>
</#if>
	
</div>
<script>
$(document).ready(function() {	
	$(".footer").css("display","none");
});
</script>