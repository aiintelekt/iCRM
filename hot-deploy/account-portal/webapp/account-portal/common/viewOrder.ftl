<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<div class="col-md-12 col-lg-12 col-sm-12">
			
<#if OrderByIdViewList?exists>
<div>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td width="50%" valign="top">
      <#include "component://account-portal/webapp/account-portal/common/OrderInfoPage.ftl"/>	
  		
    </td>
   <td width="10" nowrap="nowrap">&nbsp;<div>&nbsp;&nbsp;</div></td>
    <td width="50%" valign="top"><div>&nbsp;&nbsp;</div><div>&nbsp;</div>
     <#include "component://account-portal/webapp/account-portal/common/OrderContactInfoView.ftl"/>
    </td>
  </tr>
 
</table>
</div>

<div >
	<#include "component://account-portal/webapp/account-portal/common/OrderItemsViewPage.ftl"/>
</div>

<#else/>
  <p class="tableheadtext">${uiLabelMap.CrmOrderNotFound}</p>
</#if>
	
</div>
<script>
$(document).ready(function() {	
	$(".footer").css("display","none");
});
</script>