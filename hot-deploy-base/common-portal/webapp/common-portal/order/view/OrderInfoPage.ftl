<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
<#if OrderByIdViewList?exists>
	<div class="row">
		<div id="main" role="main" class="pd-btm-title-bar">
		<div class="col-md-12 col-lg-12 col-sm-12">	    
		    <div class="card-head margin-adj">
		        <h2 class="view-orderid">
		             Order :  ${externalId!}
		        </h2>
		     </div>   
	     </div>
	    
	     <div class='dash-panel'>
			<table width="100%" border="0" cellpadding="1" cellspacing="0">
				<tbody>
					<tr>
					    <td align="left" valign="top" width="15%">
					     <div class="tabletext" >&nbsp;<b>Order Date</b></div>
					   	</td>
					    <td width="5" >&nbsp;</td>
					    <td align="left" valign="top" width="80%">
					          <div class="tabletext" >
					              <span>${OrderByIdViewList.orderDate?if_exists}</span>             
					          </div>
					    </td>
					</tr>
					<tr>
					  	<td align="left" valign="top" width="15%">
				      		<div class="tabletext" >&nbsp;</div>
				      	</td>
				    </tr>
				    <tr>
					    <td align="left" valign="top" width="15%">
					      <div class="tabletext" >&nbsp;<b>Order Status</b></div>
					    </td>
					    <td width="5" >&nbsp;</td>
					    <td align="left" valign="top" width="80%">
					    	<div class="tabletext">${OrderByIdViewList.statusDesc?if_exists}</div>
					    </td>
					</tr>
					<tr>
					  	<td align="left" valign="top" width="15%">
				      		<div class="tabletext" >&nbsp;</div>
				      	</td>
				    </tr>
				    <tr>
					  	<td align="left" valign="top" width="15%">
				      		<div class="tabletext" >&nbsp;</div>
				      	</td>
				    </tr>
				    <tr>
					    <td align="left" valign="top" width="15%">
					      <div class="tabletext" >&nbsp;<b>Ship Date</b></div>
					    </td>
					    <td width="5" >&nbsp;</td>
					    <td align="left" valign="top" width="80%">
					    	<div class="tabletext">${OrderByIdViewList.deliveryDate?if_exists}</div>
					    </td>
					</tr>
					<tr>
					  	<td align="left" valign="top" width="15%">
				      		<div class="tabletext" >&nbsp;</div>
				      	</td>
				    </tr>
				    <tr>
					    <td align="left" valign="top" width="15%">
					      <div class="tabletext" >&nbsp;<b>Sales Channel</b></div>
					    </td>
					    <td width="5" >&nbsp;</td>
					    <td align="left" valign="top" width="80%">
					    	<div class="tabletext">${OrderByIdViewList.salesChannel?if_exists}</div>
					    </td>
					</tr>
				</tbody>
			</table>
		</div>	
	</div>
</div>

<#--<form name="orderPdfActionf122" target="_blank" method="get"/>
<#frameSection title="order #${orderId} ${externalOrder?if_exists} ">
    <table width="100%" border="0" cellpadding="1" cellspacing="0">
      <@infoRow title="Order Date" content=OrderByIdViewList.orderDate?if_exists />
      <@infoSepBar/>
      <@infoRow title="Status" content=OrderByIdViewList.statusDesc?if_exists />
         <@infoSepBar/>
         <@infoRow title="Sales Channel" content=OrderByIdViewList.salesChannel?default("N/A")/>
         <@infoSepBar/>
         <@infoRow title="Trans Num#" content=OrderByIdViewList.transactionNumber?default("N/A")/>
	     <@infoRow title="Reg Num#" content=OrderByIdViewList.registerNumber?if_exists/>
      	 <@infoRow title="Trans Type#" content=OrderByIdViewList.transactionType?if_exists/>
       	 <@infoRow title="Cashier Num#" content=OrderByIdViewList.cashierNumber?if_exists/>
	  	 <@infoSepBar/>
     </form>
    </table>

</@frameSection>-->

</#if> 
