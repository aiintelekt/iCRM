<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
<#if InvoiceList?exists>
	<div class="row">
		<div id="main" role="main" class="pd-btm-title-bar">
		<div class="col-md-12 col-lg-12 col-sm-12">	    
		    <div class="card-head margin-adj">
		        <h2 class="view-orderid">
		             Invoice :  ${invoiceId!}
		        </h2>
		     </div>   
	     </div>
	    
	     <div class='dash-panel'>
			<table width="100%" border="0" cellpadding="1" cellspacing="0">
				<tbody>
					<tr>
					    <td align="left" valign="top" width="15%">
					      <div class="tabletext" >&nbsp;<b>External ID</b></div>
					    </td>
					    <td width="5" >&nbsp;</td>
					    <td align="left" valign="top" width="80%">
					    	<div class="tabletext">${InvoiceList.externalId?if_exists}</div>
					    </td>
					</tr>
					<tr>
					  	<td align="left" valign="top" width="15%">
				      		<div class="tabletext" >&nbsp;</div>
				      	</td>
				    </tr>
					<tr>
					    <td align="left" valign="top" width="15%">
					     <div class="tabletext" >&nbsp;<b>Invoice Date</b></div>
					   	</td>
					    <td width="5" >&nbsp;</td>
					    <td align="left" valign="top" width="80%">
					          <div class="tabletext" >
					              <span>${InvoiceList.invoiceDate?if_exists}</span>             
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
					      <div class="tabletext" >&nbsp;<b>Invoice Status</b></div>
					    </td>
					    <td width="5" >&nbsp;</td>
					    <td align="left" valign="top" width="80%">
					    	<div class="tabletext">${InvoiceList.statusDesc?if_exists}</div>
					    </td>
					</tr>
					<tr>
					  	<td align="left" valign="top" width="15%">
				      		<div class="tabletext" >&nbsp;</div>
				      	</td>
				    </tr>
				    <tr>
					    <td align="left" valign="top" width="15%">
					      <div class="tabletext" >&nbsp;<b>Invoice Type</b></div>
					    </td>
					    <td width="5" >&nbsp;</td>
					    <td align="left" valign="top" width="80%">
					    	<div class="tabletext">${InvoiceList.invoiceType?if_exists}</div>
					    </td>
					</tr>
					 <tr>
					    <td align="left" valign="top" width="15%">
					      <div class="tabletext" >&nbsp;<b>Invoice Amount</b></div>
					    </td>
					    <td width="5" >&nbsp;</td>
					    <td align="left" valign="top" width="80%">
					    	<div class="tabletext">
					    	<@ofbizCurrency amount=invoiceTotal?default(0.00) isoCode=currencyUomId/>
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
				      		<div class="tabletext" >&nbsp;</div>
				      	</td>
				    </tr>


				</tbody>
			</table>
		</div>	
	</div>
</div>


</#if> 
