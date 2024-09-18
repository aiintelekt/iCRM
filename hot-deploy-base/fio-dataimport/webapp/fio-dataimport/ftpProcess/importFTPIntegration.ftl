<div id="main_body_section" class="allSubSectionBlocks">
<div class="button-bar tab-bar">
  <ul>
    <li>
      <ul>
        <li class="selected"><a href="/fio-dataimport/control/main"> CUSTOMER</a>
</li>
        <li ><a href="/fio-dataimport/control/supplierImp">SUPPLIER</a>
</li>
        <li><a href="/fio-dataimport/control/orderImp">ORDER</a>
</li>
        <li><a href="/fio-dataimport/control/invoiceImp">INVOICE</a>
</li>
      </ul>
    </li>
  </ul>
  <br class="clear">
  </div>
<div class="screenlet-title-bar"><ul><li class="h3">Data Import Customer</li>
<li class="expanded">
<a onclick="javascript:toggleScreenlet(this, 'datImport_col', 'true', 'Expand', 'Collapse');" title="Collapse">&nbsp;</a>
</li>

</ul><br class="clear"></div>
<table>
	<thead>
	  <tr>
	  <td><span class="label" style="color:#000000;"><b>Importing<b/></span></td>
	  <td><span class="label" style="color:#000000;"><b># Processed<b/></span></td>
	  <td><span class="label" style="color:#000000;"><b># Not Processed<b/></span></td>
  </tr>
  </thead>
  <tbody>
  <tr>
    <form name="importCustomersForm" method="post" action="<@ofbizUrl>importCustomerData</@ofbizUrl>">
	  <input type="hidden" name="SERVICE_NAME" value="importCustomers">
	  <input type="hidden" name="importedType" value="CUSTOMER">
	  <input type="hidden" name="POOL_NAME" value="pool">
	  <input type="hidden" name="sectionHeaderUiLabel" value="DataImportImportCustomers">
	  <td class="" style=""><span class="tabletext" style="">Customers:</span></td>
	  <td class="" style=""><span class="tabletext" style="">${customersProcessed?if_exists}</span></td>
	  <td class="" style=""><span class="tabletext" style="">${customersNotProcessed?if_exists}</span></td>
	  <td class="">  <input type="submit" value="Schedule" class="smallSubmit" onclick="submitFormWithSingleClick(this)">
	</td>
	  <td class="" style="">
	</td>  
	</form>  
  </tr>
</tbody></table>

<br>

<div class="screenlet-title-bar"><ul><li class="h3">Error Logs</li>
<li class="expanded">
<a onclick="javascript:toggleScreenlet(this, 'datImport_col', 'true', 'Expand', 'Collapse');" title="Collapse">&nbsp;</a>
</li>
</div>
<table style="width:100%">
	<thead>
	      <tr>
		      <td><span class="label" style="color: #000000;font-size: 12px;">Sequence Id</span></td>
			  <td><span class="label" style="color: #000000;font-size: 12px;">Task Id</span></td>	
			  <td><span class="label" style="color: #000000;font-size: 12px;">Task Name</span></td>
			  <td><span class="label" style="color: #000000;font-size: 12px;">Time Stamp</span></td> 
			  <td><span class="label" style="color: #000000;font-size: 12px;">Process Id</span></td>
			  <td><span class="label" style="color: #000000;font-size: 12px;">List Id</span></td>
			  <td><span class="label" style="color: #000000;font-size: 12px;">Status</span></td>
			  <td><span class="label" style="color: #000000;font-size: 12px;">Table Name</span></td>
			  <td><span class="label" style="color: #000000;font-size: 12px;">Log Msg1</span></td>
			  <td><span class="label" style="color: #000000;font-size: 12px;">Log Msg2</span></td>
			  <td><span class="label" style="color: #000000;font-size: 12px;">Log Msg3</span></td>
		 </tr> 
	 </thead> 
		<tbody>
		<#list entList as list>
		<#if list.tableName=="DataImportCustomer">
			<tr>
			   <td>
					<div class="tabletext" style="color: #000000;">${list.seqId?if_exists}</div>
				</td>
				<td>
					<div class="tabletext" style="color: #000000;">${list.taskId?if_exists}</div>
				</td>
				<td>
					<div class="tabletext" style="color: #000000;">${list.taskName?if_exists}</div>
				</td>
				<td>
					<div class="tabletext" style="color: #000000;"> ${list.timeStamp?if_exists}</div>
				</td>
				<td>
					<div class="tabletext" style="color: #000000;"> ${list.processId?if_exists}</div>
				</td>
				<td>
					<div class="tabletext" style="color: #000000;"> ${list.listId?if_exists}</div>
				</td>
				<td>
					<div class="tabletext" style="color: #000000;"> ${list.status?if_exists}</div>
				</td>
				<td>
					<div class="tabletext" style="color: #000000;"> ${list.tableName?if_exists}</div>
				</td>
				<td>
					<div class="tabletext" style="color: #000000;"> ${list.logMsg1?if_exists}</div>
				</td>
				<td>
					<div class="tabletext" style="color: #000000;">${list.logMsg2?if_exists}</div>
				</td>
				<td>
					<div class="tabletext" style="color: #000000;">${list.logMsg3?if_exists}</div>
				</td>
			</tr>	
				</#if>	
     </#list>
     </tbody>
     
</table>