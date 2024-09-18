<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.9.0/jquery.min.js"> </script>
<script type="text/javascript">
    var reReservationWidgetParameters = new Object();
</script>
<#if InvoiceByIdViewList?exists>
<#macro sectionSepBar>
<tr>
  <#if requestURI?contains("responsive-portal")>
  <td colspan="8"></td>
  <#else><td colspan="6"></td></#if>
    <td colspan="7">
        <hr class="sepbar"/>
    </td>
</tr>
</#macro>
<#if !requestURI?contains("responsive-portal")><div class="dash-panel" style="margin: 15px 0px 0px 0px !important;">
    <h4 class="orderitem-title">Invoice Items</h4>
</div></#if>
<div class='dash-panel' style="margin: 15px 0px 0px 0px !important;">
    <form name="createReturnAction1" url="createReturnFromOrder" orderId="${invoiceId!}" />
    <input type="hidden" value="${invoiceId!}" id="orderId" />
    <table width="100%" border="0" cellpadding="0" cellspacing="0">
        <tbody>
            <tr align="left" valign="bottom">
                <#assign requestURI = request.getRequestURI()/>
               <#if requestURI?contains("responsive-portal")><td width="8%" align="left"> <#else><td width="5%" align="left"> </#if>
                    <div class="tableheadtext">
                        <h4>Invoice ID</h4>
                    </div>
                </td>
                
                <td width="5%" align="left">
                    <div class="tableheadtext">
                        <h4>Sequence</h4>
                    </div>
                </td>
                <td width="5%" align="left">
                    <div class="tableheadtext">
                        <h4>Type</h4>
                    </div>
                </td>
                 <#if requestURI?contains("responsive-portal")><td width="12%" align="left"> <#else><td width="10%" align="left"> </#if>
                    <div class="tableheadtext">
                        <h4>Customer</h4>
                    </div>
                </td>
                <td width="15%" align="left">
                    <div class="tableheadtext">
                        <h4>SKU</h4>
                    </div>
                </td>
                <#if requestURI?contains("responsive-portal")><td width="5%" align="right"> <#else><td width="7%" align="right"> </#if>
                    <div class="tableheadtext">
                        <h4>QTY Sold</h4>
                    </div>
                </td>
                <td width="7%" align="right">
                    <div class="tableheadtext">
                        <h4>Unit Retail</h4>
                    </div>
                </td>
                
                <#if !requestURI?contains("responsive-portal")>
                <td width="7%" align="right">
                    <div class="tableheadtext">
                        <h4>Unit Cost</h4>
                    </div>
                </td>
                </#if>
                 <#if requestURI?contains("responsive-portal")><td width="6%" align="right"> <#else><td width="10%" align="right"> </#if>
                    <div class="tableheadtext">
                        <h4>Discount</h4>
                    </div>
                </td>
                <#if requestURI?contains("responsive-portal")><td width="7%" align="right"> <#else> <td width="7%" align="right"></#if>
                    <div class="tableheadtext">
                        <h4>EXT Discount</h4>
                    </div>
                </td>
                <#if requestURI?contains("responsive-portal")><td width="5%" align="right"> <#else><td width="10%" align="right"> </#if>
                    <div class="tableheadtext">
                        <h4>Total</h4>
                    </div>
                </td>
                <#if requestURI?contains("responsive-portal")><td width="5%" align="right"> <#else><td width="7%" align="right"> </#if>
                    <div class="tableheadtext">
                        <h4>Margin</h4>
                    </div>
                </td>
                <td width="5%">&nbsp;</td>
            </tr>
            <#list InvoiceByIdViewList as item>
            <#if item.currencyUom?has_content>
                <#assign isoCode=item.currencyUom?if_exists>
            <#else>
                <#assign isoCode=currencyUomId?if_exists>
            </#if>
            <tr>
                <td colspan="15">
                    <hr class="sepbar">
                </td>
            </tr>
            <tr>
                <td align="left" colspan="1" valign="top">${item.invoiceId!}</td>
                
                <td align="left" colspan="1" valign="top">
                    <div class="tabletext" >${item.sequenceNumber?if_exists}</div>
                </td>
                <td align="left" colspan="1" valign="top">
                    <div class="tabletext" >${item.invoiceType?if_exists}</div>
                </td>
                <td align="left" colspan="1" valign="top">
                    <div class="tabletext" >${item.billToPartyName?if_exists}</div>
                </td>
                <td align="left" colspan="1" valign="top">
                    <div class="tabletext" >${item.skuDescription?if_exists}</div>
                </td>
                
                <td align="right" colspan="1" valign="top">
                    <div class="tabletext" >${item.quantitySold?string("0.00")?default("0.00")?if_exists}</div>
                </td>
                <td align="right" colspan="1" valign="top">
                    <div class="tabletext" >${item.unitRetail?string("0.00")?default("0.00")?if_exists}</div>
                </td>
                 <#if !requestURI?contains("responsive-portal")>
                <td align="right" colspan="1" valign="top">
                    <div class="tabletext" >${item.unitCost?string("0.00")?default("0.00")?if_exists}</div>
                </td>
                </#if>
                <td align="right" valign="top">
                   <@ofbizCurrency amount=item.get("discountAmount")?default(0.00) isoCode=currencyUomId/>
                </td>
                <td align="right" colspan="1" valign="top">
                    <div class="tabletext" >${item.extendedDiscount?string("0.00")?default("0.00")?if_exists}</div>
                </td>
                <td align="right" valign="top" >
                    <@ofbizCurrency amount=item.get("itemNetSales")?default(0.00) isoCode=currencyUomId/>
                </td>
                 <td align="right" colspan="1" valign="top">
                    <div class="tabletext" >${item.marginCost?string("0.00")?default("0.00")?if_exists}</div>
                </td>
                <td align="right" valign="top">
                    &nbsp;
                </td>
            </tr>
            <tr>
                <td style="padding-top : 10px; padding-bottom : 10px;" colspan="7">
                </td>
            </tr>
            </#list>
            <#if InvoiceByIdViewList?exists>
            <tr>
                <td colspan="15" >
                    <hr class="sepbar">
                </td>
            </tr>
            <tr>
                <td align="right" colspan="10">
                    <div class="tabletext"><b>Total Other Invoice Adjustments</b></div>
                </td>
                <td align="right" nowrap="nowrap">
                    <div class="tabletext"><#if totalAdjustments?has_content><@ofbizCurrency amount="${totalAdjustments?if_exists}" isoCode=isoCode/><#else>$0.00</#if></div>
                </td>
                 <td align="right">
                    <div class="tabletext"><b></b></div>
                </td>
            </tr>
            <tr>
                <td align="right" colspan="10">
                    <div class="tabletext"><b>Items Sub Total</b></div>
                </td>
                <td align="right" nowrap="nowrap">
                    <div class="tabletext"><#if totalItemAmount?has_content><@ofbizCurrency amount="${totalItemAmount?if_exists}" isoCode=isoCode/><#else>$0.00</#if></div>
                </td>
                <td align="right">
                    <div class="tabletext"><b></b></div>
                </td>
            </tr>
            <tr>
                <td align="right" colspan="10">
                    <div class="tabletext"><b> Total Sales Tax</b></div>
                </td>
                <td align="right" nowrap="nowrap">
                    <div class="tabletext"><#if totalTaxAmount?has_content><@ofbizCurrency amount="${totalTaxAmount?if_exists}" isoCode=isoCode/><#else>$0.00</#if></div>
                </td>
                <td align="right">
                    <div class="tabletext"><b></b></div>
                </td>
            </tr>
            <@sectionSepBar/>
            <#-- grand total -->
            <tr>
                <td align="right" colspan="10">
                    <div class="tabletext"><b>Invoice Total</b></div>
                </td>
                <td align="right" nowrap="nowrap">
                    <div class="tabletext"><#if invoiceTotal?has_content><@ofbizCurrency amount="${invoiceTotal?if_exists}" isoCode=isoCode/></#if></div>
                </td>
                 <td align="right">
                    <div class="tabletext"><b></b></div>
                </td>
            </tr>
            </#if>
    </table>
    </#if>
</div>