<div id="main_body_section" class="allSubSectionBlocks">
<div class="button-bar tab-bar">
  <ul>
    <li>
      <ul>
        <li class="selected"><a href="/main"> CUSTOMER</a>
</li>
        <li ><a href="/catalog/control/EditFeatureCategories">SUPPLIER</a>
</li>
        <li><a href="/catalog/control/EditFeatureGroups">ORDER</a>
</li>
        <li><a href="scheduleInvoiceData">INVOICE</a>
</li>
      </ul>
    </li>
  </ul>
  <br class="clear">
  </div>
<div class="screenlet-title-bar"><ul><li class="h3">Data Import</li>
<li class="expanded">
<a onclick="javascript:toggleScreenlet(this, 'datImport_col', 'true', 'Expand', 'Collapse');" title="Collapse">&nbsp;</a>
</li>

</ul><br class="clear"></div>
<table class="headedTable" id="ext-gen32">
  <tbody><tr class="header">
  <td class="" style=""><span class="tabletext" style="">Importing</span></td>
  <td class="" style=""><span class="tabletext" style=""># Processed</span></td>
  <td class="" style=""><span class="tabletext" style=""># Not Processed</span></td>
    
  </tr>
  <tr>
    <form name="importCustomersForm" method="post" action="importCustomerData">
	  <input type="hidden" name="SERVICE_NAME" value="importCustomers">
	  <input type="hidden" name="importedType" value="CUSTOMER">
	  <input type="hidden" name="POOL_NAME" value="pool">
	  <input type="hidden" name="sectionHeaderUiLabel" value="DataImportImportCustomers">
	  <td class="" style=""><span class="tabletext" style="">Customers:</span></td>
	  <td class="" style=""><span class="tabletext" style="">0</span></td>
	  <td class="" style=""><span class="tabletext" style="">0</span></td>
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