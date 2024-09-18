<#escape x as x?xml>

		<fo:table >
			<fo:table-column column-width="100%"/>
			<fo:table-body>
				<fo:table-row height="50pt">
					<fo:table-cell>
						<fo:block height="50pt">
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-body>
			</fo:table>
			<fo:table >
			<fo:table-column column-width="100%"/>
			<fo:table-body>
				<fo:table-row height="20pt">
					<fo:table-cell>
						<fo:block height="20pt">
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-body>
			</fo:table>
       		<fo:table font-size="10pt">
			<fo:table-column column-width="40%"/>
			<fo:table-column column-width="20%"/>
			<fo:table-column column-width="40%"/>
			<fo:table-body>
				<fo:table-row >
					<fo:table-cell padding="3px">
						<fo:block>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row >
					<fo:table-cell padding="3px">
						<fo:block border="1px solid" background-color="#dcdcdc" font-weight="bold">Invoice To</fo:block>
						<fo:block>&#160;</fo:block>
						<#if billingAddress?has_content>
						<#assign shippingAddress=billingAddress?if_exists>
						</#if>
						<#if shippingAddress?has_content>
						<#assign geoName="">
						<#if shippingAddress.stateProvinceGeoId?has_content>
						<#assign stateGeo = (delegator.findOne("Geo", {"geoId", shippingAddress.stateProvinceGeoId!}, false))! />
						</#if>
						
						<fo:block>${shippingAddress.toName?if_exists}</fo:block>
						<fo:block>${shippingAddress.attnName?if_exists}</fo:block>
						<fo:block>${shippingAddress.address1?if_exists}</fo:block>
						<#if shippingAddress?has_content && shippingAddress.address2?has_content && shippingAddress.address2!="null"><fo:block>${shippingAddress.address2?if_exists}</fo:block></#if>
						<fo:block><#if shippingAddress.city?has_content>${shippingAddress.city?if_exists}</#if><#if stateGeo?has_content>,${stateGeo.geoName?if_exists}<#elseif shippingAddress.stateProvinceGeoId?has_content>,${shippingAddress.stateProvinceGeoId?if_exists}</#if><#if shippingAddress.postalCode?has_content>, ${shippingAddress.postalCode?if_exists}</#if><#if shippingAddress.county?has_content>(${shippingAddress.county?if_exists})</#if></fo:block>
						<fo:block>${shippingAddress.countryGeoId?if_exists}</fo:block>
						<fo:block>Tel No:&#160;${shippingAddress.phoneNumber?if_exists}</fo:block>
						</#if>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block></fo:block>
						<fo:block></fo:block>
						<fo:block></fo:block>
						<fo:block></fo:block>
					</fo:table-cell>
					<fo:table-cell padding="3px" text-align="left">
						<fo:block margin-right="2px" border="1px solid" background-color="#dcdcdc" font-weight="bold">Shipping Address: </fo:block>
						<fo:block>&#160;</fo:block>
						<#if shippingAddress?has_content>
						<#assign geoName="">
						<#if shippingAddress.stateProvinceGeoId?has_content>
						<#assign stateGeo = (delegator.findOne("Geo", {"geoId", shippingAddress.stateProvinceGeoId!}, false))! />
						</#if>
						
						<fo:block>${shippingAddress.toName?if_exists}</fo:block>
						<fo:block>${shippingAddress.attnName?if_exists}</fo:block>
						<fo:block>${shippingAddress.address1?if_exists}</fo:block>
						<#if shippingAddress?has_content && shippingAddress.address2?has_content && shippingAddress.address2!="null"><fo:block>${shippingAddress.address2?if_exists}</fo:block></#if>
						<fo:block><#if shippingAddress.city?has_content>${shippingAddress.city?if_exists}</#if><#if stateGeo?has_content>,${stateGeo.geoName?if_exists}<#elseif shippingAddress.stateProvinceGeoId?has_content>,${shippingAddress.stateProvinceGeoId?if_exists}</#if><#if shippingAddress.postalCode?has_content>, ${shippingAddress.postalCode?if_exists}</#if><#if shippingAddress.county?has_content>(${shippingAddress.county?if_exists})</#if></fo:block>
						<fo:block>${shippingAddress.countryGeoId?if_exists}</fo:block>
						<fo:block>Tel No:&#160;${shippingAddress.phoneNumber?if_exists}</fo:block>
						</#if>
					</fo:table-cell>
				</fo:table-row>
				
			</fo:table-body>
			</fo:table>	
			<#-- for shipping method -->
		<#-- <fo:table >
		 <fo:table-column column-width="100%"/>	
		 <fo:table-body>
				<fo:table-row >
					<fo:table-cell padding="3px" text-align="center" >
        <fo:table border="1px solid">
		<fo:table-column column-width="15%"/>
		<fo:table-column column-width="15%"/>
		<fo:table-column column-width="15%"/> 
		<fo:table-column column-width="25%"/>
		<fo:table-column column-width="15%"/>
		<fo:table-column column-width="15%"/>
		<fo:table-body  font-size="10pt">
			<fo:table-row>
			<fo:table-cell padding="2px" border="1px solid"> 
					<fo:block font-weight="bold">
					S.O. No.		
					</fo:block>
				</fo:table-cell>
				<fo:table-cell padding="2px" border="1px solid"> 
					<fo:block font-weight="bold">
					P.O. No.		
					</fo:block>
				</fo:table-cell>
				<fo:table-cell padding="2px" border="1px solid"> 
					<fo:block font-weight="bold">
					Terms	
					</fo:block>
				</fo:table-cell>
				<fo:table-cell padding="3px" border="1px solid"> 
					<fo:block font-weight="bold">
						Account#
					</fo:block>
				</fo:table-cell>
				<fo:table-cell padding="3px" border="1px solid"> 
					<fo:block font-weight="bold">
					 Rep
					</fo:block>
				</fo:table-cell>
				<fo:table-cell padding="3px" border="1px solid"> 
					<fo:block font-weight="bold">
					 FOB	
					</fo:block>
				</fo:table-cell>
			</fo:table-row>
			<fo:table-row height="30pt">
				<fo:table-cell padding="2px" border="1px solid"> 
					<fo:block font-weight="bold">
						
					</fo:block>
				</fo:table-cell>
				<fo:table-cell padding="2px" border="1px solid"> 
					<fo:block font-weight="bold">
						
					</fo:block>
				</fo:table-cell>
				<fo:table-cell padding="3px" border="1px solid"> 
					<fo:block font-weight="bold">
						<#if terms?has_content>${terms.termDays?if_exists} Days</#if>
					</fo:block>
				</fo:table-cell>
				<fo:table-cell padding="3px" border="1px solid"> 
				
					<fo:block font-weight="bold">
					
					</fo:block>
					
				</fo:table-cell>
				<fo:table-cell padding="3px" border="1px solid"> 
					<fo:block font-weight="bold">
					 	
					</fo:block>
				</fo:table-cell>
				<fo:table-cell padding="3px" border="1px solid"> 
					<fo:block font-weight="bold">
					 	
					</fo:block>
				</fo:table-cell>
			</fo:table-row>
			</fo:table-body>
			</fo:table>
			
			</fo:table-cell>
			</fo:table-row>
			</fo:table-body>
			</fo:table>
			-->
			<fo:table >
			<fo:table-column column-width="50%"/>
			<fo:table-column column-width="50%"/>
			<fo:table-body>
				<fo:table-row height="20pt">
					<fo:table-cell>
						<fo:block height="20pt">
						</fo:block>
					</fo:table-cell>
					<fo:table-cell text-align="center">
						<fo:block font-size="18pt"></fo:block>
						<fo:block font-size="12pt"></fo:block>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-body>
			</fo:table>
			
       <#-- For Order Items -->
       <fo:table >
		 <fo:table-column column-width="100%"/>	
		 <fo:table-body>
				<fo:table-row >
					<fo:table-cell padding="3px" text-align="left" >
        <fo:table>
		<fo:table-column column-width="10%"/>
		<fo:table-column column-width="13%"/>
		<fo:table-column column-width="35%"/>
		<fo:table-column column-width="7%"/> 
		<fo:table-column column-width="13%"/> 
		<fo:table-column column-width="12%"/> 
		<fo:table-column text-align="right" column-width="10%" />
		
		<fo:table-body  font-size="9pt" >
			<fo:table-row background-color="#dcdcdc">
				<fo:table-cell padding="2px"> 
					<fo:block font-weight="bold">
					Item		
					</fo:block>
				</fo:table-cell>
				
				<fo:table-cell padding="2px"> 
					<fo:block font-weight="bold">
					SKU ID		
					</fo:block>
				</fo:table-cell>
				
				<fo:table-cell padding="2px" > 
					<fo:block font-weight="bold">
					Description	
					</fo:block>
				</fo:table-cell>
				
				<fo:table-cell padding="1px" > 
					<fo:block font-weight="bold">
					 Hrs	
					</fo:block>
				</fo:table-cell>
				
				<fo:table-cell padding="1px" > 
					<fo:block font-weight="bold">
					 Rate	
					</fo:block>
				</fo:table-cell>
				<fo:table-cell padding="1px" > 
					<fo:block font-weight="bold">
					 Qty	
					</fo:block>
				</fo:table-cell>
				
				
				
				<fo:table-cell padding="2px"> 
					<fo:block text-align="right" font-weight="bold">
					 Total	
					</fo:block>
				</fo:table-cell>
			</fo:table-row>
			
												
		<#if invoiceItems?has_content>
		<#list invoiceItems as eachItem>
		<#assign productName="">
							<#assign quantityUomId="">
							<#assign productNameDet = delegator.findOne("Product",Static["org.ofbiz.base.util.UtilMisc"].toMap("productId",eachItem.productId?if_exists), true)?if_exists>
							 <#assign productName=eachItem.description?if_exists>
							<#if productName?has_content>
							 		<#assign invoiceItemType = delegator.findOne("InvoiceItemType",Static["org.ofbiz.base.util.UtilMisc"].toMap("invoiceItemTypeId",productName?if_exists), true)?if_exists>
					               <#if invoiceItemType?has_content>
					                 <#assign productName = invoiceItemType.get("description")?if_exists>
					               </#if>
					          <#else>
					              <#assign productName=eachItem.invoiceItemTypeId?if_exists>
					                <#assign invoiceItemType = delegator.findOne("InvoiceItemType",Static["org.ofbiz.base.util.UtilMisc"].toMap("invoiceItemTypeId",productName?if_exists), true)?if_exists>
					                <#if invoiceItemType?has_content>
					                 <#assign productName = invoiceItemType.get("description")?if_exists>
					               </#if>
							</#if>
					         <#assign invoiceItemHrsList = delegator.findByAnd("TimeEntry", {"invoiceId",eachItem.invoiceId?if_exists,"rateTypeId",productName?if_exists}, null, false)?if_exists>
							<#if productNameDet?has_content>
							<#if productNameDet.get("internalName")?has_content>
							<#assign productName=productNameDet.get("internalName")?if_exists>
							<#elseif productNameDet.get("brandName")?has_content>
							<#assign productName=productNameDet.get("brandName")?if_exists>
							<#elseif productNameDet.get("productName")?has_content>
							<#assign productName=productNameDet.get("productName")?if_exists>
							</#if>
							<#if productNameDet?has_content && productNameDet.quantityUomId?has_content>
							<#assign quantityUomId=productNameDet.quantityUomId?if_exists/>
							</#if>
							<#else>
							<#if invoiceItemHrsList?has_content>
					                <#assign productName=eachItem.invoiceItemTypeId>
					                <#assign invoiceItemType = delegator.findOne("InvoiceItemType",Static["org.ofbiz.base.util.UtilMisc"].toMap("invoiceItemTypeId",productName?if_exists), true)?if_exists>
					                <#assign productName = invoiceItemType.get("description")?if_exists>
					                <#assign invoiceItemHrs = invoiceItemHrsList.get(0)?if_exists>
							</#if> 
						</#if>
				
			<#assign prodPrice = delegator.findByAnd("ProductPrice", {"productId" : eachItem.productId?if_exists,"productPriceTypeId","DEFAULT_PRICE"}, null, false)?if_exists>
			
			<#assign prodPrice = delegator.findByAnd("OrderItemBilling", {"invoiceId" : eachItem.invoiceId?if_exists,"invoiceItemSeqId",eachItem.invoiceItemSeqId?if_exists}, null, false)?if_exists>
			
			<fo:table-row border-bottom="1px solid">
				<fo:table-cell padding="1px" > 
					<fo:block>${eachItem.invoiceItemSeqId}</fo:block>
				</fo:table-cell>
				
				<fo:table-cell padding="1px" > 
					<fo:block >${eachItem.productId?if_exists}
					</fo:block>
				</fo:table-cell>
				
				<fo:table-cell padding="2px" > 
					<fo:block >${productName?if_exists}
					</fo:block>
				</fo:table-cell>
				<fo:table-cell padding="1px" > 
					<fo:block>
					
					<#if invoiceItemHrsList?has_content && invoiceItemHrsList[0]?has_content>${invoiceItemHrsList[0].hours?string("#0.00")?if_exists}<#else>-</#if>
					
					 	
					</fo:block>
				</fo:table-cell>
				<fo:table-cell padding="1px" > 
					<fo:block><#if prodPrice?has_content && prodPrice[0]?has_content>${prodPrice[0].amount?string("#0.00")?if_exists}<#else>${eachItem.amount?string("#0.00")?if_exists}</#if>
					 	
					</fo:block>
				</fo:table-cell>
				
				<fo:table-cell padding="1px" > 
					<fo:block><#if eachItem.quantity?has_content>${eachItem.quantity?string("#0.00")?if_exists}<#else>-</#if>
					 	
					</fo:block>
				</fo:table-cell>
				
				<fo:table-cell padding="2px" text-align="right" > 
					<fo:block>${eachItem.amount?string("#0.00")?if_exists}
					 	
					</fo:block>
				</fo:table-cell>
				
			</fo:table-row>
			</#list>
			</#if>
			</fo:table-body>
			</fo:table>
			
			</fo:table-cell>
			</fo:table-row>
			</fo:table-body>
			</fo:table>
			
			<fo:table >
			<fo:table-column column-width="80%"/>
			<fo:table-column column-width="20%"/>
			<fo:table-body text-align="right" font-size="10pt" font-weight="bold">
				<fo:table-row height="20pt">
					<fo:table-cell>
						<fo:block height="20pt">Items SubTotal</fo:block>
					</fo:table-cell>
					:table-row height="20pt">
					<fo:table-cell>
						<fo:block height="20pt"><@ofbizCurrency amount=invoiceNoTaxTotal?string("0.00")?if_exists isoCode=currencyUomId/></fo:block>
					</fo:table-cell>
				</fo:table-row>
				<!--<fo:table-row height="20pt">
				<#assign invoiceItemSet = delegator.findByAnd("InvoiceItem", {"invoiceId" : parameters.invoiceId?if_exists,"invoiceItemTypeId":"ITM_SHIPPING_CHARGES"}, null, false)?if_exists>
						<#assign shipingAmt=0.00>
				<#if invoiceItemSet?has_content>	
				<#list invoiceItemSet as shipDet>
				<#assign shipingAmt=shipingAmt+shipDet.amount?if_exists>
				</#list>
				</#if>
					<fo:table-cell>
						<fo:block height="20pt">Total Shipping and Handling</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block height="20pt">${shipingAmt?if_exists}</fo:block>
					</fo:table-cell>
				</fo:table-row>-->
				<fo:table-row height="20pt">
					<fo:table-cell>
						<fo:block height="20pt">Total GST Tax</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block height="20pt"><#if invoiceTaxTotal?has_content>${invoiceTaxTotal?string("#0.00")?if_exists}<#else>0.00</#if></fo:block>
					</fo:table-cell>
				</fo:table-row>
				<#-- <fo:table-row height="20pt">
					<fo:table-cell>
						<fo:block height="20pt">Total Other Order Adjustments</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block height="20pt">0.00</fo:block>
					</fo:table-cell>
				</fo:table-row> -->
				<fo:table-row height="20pt">
					<fo:table-cell>
						<fo:block height="20pt">Total</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block height="20pt"><@ofbizCurrency amount=invoiceTotal?string("0.00")?if_exists isoCode=currencyUomId/></fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row height="20pt">
					<fo:table-cell>
						<fo:block height="20pt">Balance Due</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block height="20pt"><@ofbizCurrency amount=outstandingAmount?string("0.00")?if_exists isoCode=currencyUomId/></fo:block>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-body>
			</fo:table>
			
			
			<fo:table >
			<fo:table-column column-width="100%"/>
			<fo:table-body font-size="10pt">
				<fo:table-row>
					<fo:table-cell>
						<fo:block height="20pt">
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-body>
			</fo:table>
			
			<fo:table >
			<fo:table-column column-width="100%"/>
			<fo:table-body font-size="9pt">
				<fo:table-row>
					<fo:table-cell>
						<fo:block height="20pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;
						              Damages and shortages must be reported within 5 business days of invoice date. Inspect all packages upon receipt. Returns are
					only accepted within 30 days of invoice date and must have a Returned Goods Authorization (RGA) number prior to being returned.
					Opened products may not be returned. Returns may be subject to a restocking fee. Freight is non-refundable. All PPE product sales are
					considered final sale/non-returnable. Credits can be used against future purchases or applied to outstanding invoices. Credits on
					account are valid for one year.
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
					<fo:table-row>
					<fo:table-cell>
						<fo:block height="20pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;
					There are no refunds or credits on special order items. Opened products may not be returned. Orders for pick up will be held in our
					warehouse for two weeks. After this time the order will be cancelled unless otherwise specified
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-body>
			</fo:table>
		
		<#-- Note -->
		
		
		<#-- end of note -->
		
	

</#escape>
