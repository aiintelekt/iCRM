<link type="text/css" rel="stylesheet" href="<@ofbizContentUrl>/om_css/tooltipstyle.css</@ofbizContentUrl>"/>
<script type="text/javascript" src="<@ofbizContentUrl>/om_js/tooltipscript.js</@ofbizContentUrl>"></script>

<style type="text/css">
.dataTables_filter {
     display: none;
}
.dataTables_length{
 display: none;
}
.alignmentchange{
text-align : right;
width : 10% !important;
}
.alignmentchange3{
text-align : right;
width : 15% !important;
}
.alignmentchange1{
width : 15% !important;
}
.alignmentchange2{
text-align : center;
width : 10% !important;
}
</style> 
<#assign exportType =""/>
<script type="text/javascript">
   var oTable =null;
 $(document).ready(function(){
   		loadextractModel();
   });
   
 	function loadextractModel() {
	     var table = $('#sample_2');

	     /* Formatting function for row details */

	     function fnFormatDetails(oTable, nTr) {

	         var aData = oTable.fnGetData(nTr);

	    
	         return sOut;

	     }



	     /*

	      * Insert a 'details' column to the table

	      */

	     oTable = table.dataTable({

	         "processing": true,
	         "serverSide": true,
	         "ajax": "<@ofbizUrl>getEtlBatchesModels?etlDestTableName=${requestParameters.etlDestTableName?if_exists}&model=${requestParameters.model?if_exists}&batchId=${requestParameters.batchId?if_exists}</@ofbizUrl>",

	         // Internationalisation. For more info refer to http://datatables.net/manual/i18n

	         "language": {

	             "aria": {

	                 "sortAscending": ": activate to sort column ascending",

	                 "sortDescending": ": activate to sort column descending"

	             },

	             "emptyTable": "No data available in table",

	             "info": "Showing _START_ to _END_ of _TOTAL_ entries",

	             "infoEmpty": "No entries found",

	             "infoFiltered": "(filtered1 from _MAX_ total entries)",

	             "lengthMenu": "Show _MENU_ entries",

	            // "search": "Search:",

	             "zeroRecords": "No matching records found",
	             
	            // "searchPlaceholder":"Order Id",

	             "oPaginate": {

	                 "sNext": "Next",

	                 "sPrevious": "Previous"

	             }

	         },

	         "columnDefs": [

	             {

	                 "orderable": false,

	                 "targets": [0]

	             },

	             {

	                 "targets": [3],

	                 "visible": false

	                // "searchable": false

	             },

	             {

	                 "targets": [6],

	                 "visible": false

	                // "searchable": false

	             },

	         ],

	         "order": [

	            

	         ],

	         "lengthMenu": [

	             [10, 15, 20, 50],

	             [10, 15, 20, 50] // change per page values here

	         ],
	         
	         "bAutoWidth":false,

	         "aoColumns": [
	         
	               {
	                 "sName": "batchId",
	                 "sClass": "alignmentchange1",
	                 "bSortable": false,
	                 "mRender": function(data, type, full) {
	                     return full[0].batchId;
	                 }
	             },
	             {
	                 "sName": "modelName",
	                 "sClass": "alignmentchange1",
	                 "bSortable": false,
	                 "mRender": function(data, type, full) {
	                     return full[0].modelName;
	                 }
	             }, {
	                 "sName": "accessType",
	                  "sClass": "alignmentchange1",
	                 "bSortable": false,
	                 "mRender": function(data, type, full) {
	                     return full[0].accessType;
	                 }
	             },
	             {
	                 "sName": "statusId",
	                  "sClass": "alignmentchange1",
	                 "bSortable": false,
	                 "mRender": function(data, type, full) {
	                     return full[0].statusId;
	                 }
	             },
	             {
	                 "sName": "processedCount",
	                 "sClass": "alignmentchange",
	                 "bSortable": false,
	                 "mRender": function(data, type, full) {
	                     return full[0].processedCount;
	                 }
	             },
	             {
	                 "sName": "notProcessedCount",
	                 "sClass": "alignmentchange3",
	                 "bSortable": false,
	                 "mRender": function(data, type, full) {
	                     return full[0].notProcessedCount;
	                 }
	             },
	             {
	                 "sName": "createdBy",
	                  "sClass": "alignmentchange2",
	                 "bSortable": false,
	                 "mRender": function(data, type, full) {
	                     return full[0].createdBy;
	                 }
	             }
	          
	         ],

	         "aoColumnDefs": [

	             {
	                 "aTargets": [1]
	             }

	         ],

	       

	     });



	     var tableWrapper = $('#allOrders'); // datatable creates the table wrapper by adding with id {your_table_jd}_wrapper

	     $('#allOrders').addClass("dataTables_wrapper form-inline dt-bootstrap no-footer");

	     var tableColumnToggler = $('#allOrders_column_toggler');



	     tableWrapper.find('.dataTables_length select').select2(); // initialize select2 dropdown



	     /* Add event listener for opening and closing details

	      * Note that the indicator for showing which row is open is not controlled by DataTables,

	      * rather it is done here

	      */

	     table.on('click', ' tbody td .row-details', function() {

	         var nTr = $(this).parents('tr')[0];

	         if (oTable.fnIsOpen(nTr)) {
	             /* This row is already open - close it */
	             $(this).addClass("row-details-close").removeClass("row-details-open");
	             oTable.fnClose(nTr);

	         } else {
	             /* Open this row */
	             $(this).addClass("row-details-open").removeClass("row-details-close");
	             oTable.fnOpen(nTr, fnFormatDetails(oTable, nTr), 'details hidden-md hidden-lg');
	         }

	     });



	     /* handle show/hide columns*/

	     $('input[type="checkbox"]', tableColumnToggler).change(function() {

	         /* Get the DataTables object again - this is not a recreation, just a get of the object */

	         var iCol = parseInt($(this).attr("data-column"));

	         iCol = iCol + 1;

	         var bVis = oTable.fnSettings().aoColumns[iCol].bVisible;

	         oTable.fnSetColumnVis(iCol, (bVis ? false : true));

	     });



	     table.find('.group-checkable').click(function() {

	         var set = jQuery(this).attr("data-set");

	         var checked = jQuery(this).is(":checked");

	         jQuery(set).each(function() {

	              if (checked) {
                    $(this).attr("checked", true);
                    $(this).parents('span').addClass("checked");
                    $(this).parents('tr').toggleClass("active");
                    
                    if(orderManagementDownloadLogIds !=""){
	            		orderManagementDownloadLogIds = orderManagementDownloadLogIds+$(this).val()+"|";
	            	}else{
	            		orderManagementDownloadLogIds = $(this).val()+"|";
	            	}
                    
                } else {
                    $(this).attr("checked", false);
                    $(this).parents('span').removeClass("checked");
                    $(this).parents('tr').removeClass("active");
                    if(orderManagementDownloadLogIds.indexOf($(this).val())>-1){
			       	 	orderManagementDownloadLogIds=orderManagementDownloadLogIds.replace($(this).val()+"|","");     
			    	}
                }

	         });

	     });



	     table.on('change', 'tbody tr .checkboxes', function() {

	         	$(this).parents('tr').toggleClass("active");
            	if($(this).is(":checked")){
		            	if(orderManagementDownloadLogIds !=""){
		            		orderManagementDownloadLogIds = orderManagementDownloadLogIds+$(this).val()+"|";
		            	}else{
		            		orderManagementDownloadLogIds = $(this).val()+"|";
		            	}
                
            	}else{
	            	if(orderManagementDownloadLogIds.indexOf($(this).val())>-1){
				        orderManagementDownloadLogIds=orderManagementDownloadLogIds.replace($(this).val()+"|","");     
				    }
            	}

	     });

	 }
</script>
<script>
  function  getModelBasedList(element)
  {
 
  	var selectedModel = $(element).val();
  	 
  	if(selectedModel!="")
  	{
  		$("#etlDestTableName").val(selectedModel);
  		$("#EltBasedList").submit();
  	}
  }
   function  getModelFields(element)
  {
  	var selectedModel = $(element).val();
  	if(selectedModel!="")
  	{
  		$("#model").val(selectedModel);
  		$("#modelList").submit();
  	}
  }
  </script>
<form name="EltBasedList" id="EltBasedList" method="get" action="etlBatches">
  <input  type="hidden" name="etlDestTableName"  id="etlDestTableName">
</form>
<form name="modelList" id="modelList" method="" action="etlBatches">
  <input  type="hidden" name="model"  id="model">
  <input  type="hidden" name="etlDestTableName"  id="etlDestTableName1" value="${requestParameters.etlDestTableName?if_exists}">
</form>
<div class="portlet light">
<div class="portlet-title band">
    <div class="caption font-red-sunglo">
        <i class="icon-share font-red-sunglo"></i>
        <span class="caption-subject bold uppercase">${uiLabelMap.batches}</span>
        <span class="caption-helper"></span>
    </div>
     <div class="actions">
        <#if etlPr_ocess?has_content>	
        <select class="form-control" onchange="getModelBasedList(this);">
          <option value="">${uiLabelMap.selectType}</option>
          
          	<option value="DataImportAccount" <#if requestParameters.etlDestTableName?has_content><#if requestParameters.etlDestTableName=="DataImportAccount">Selected</#if></#if>>Account Model</option>
          	<option value="DataImportLead" <#if requestParameters.etlDestTableName?has_content><#if requestParameters.etlDestTableName=="DataImportLead">Selected</#if></#if>>Lead Model</option>
          	<option value="DataImportCustomer" <#if requestParameters.etlDestTableName?has_content><#if requestParameters.etlDestTableName=="DataImportCustomer">Selected</#if></#if>>Customer Model</option>
			
          	<#-- 
            <option value="DataImportSupplier" <#if requestParameters.etlDestTableName?has_content><#if requestParameters.etlDestTableName=="DataImportSupplier">Selected</#if></#if>>Supplier Model</option>
			<option value="DataImportCategory" <#if requestParameters.etlDestTableName?has_content><#if requestParameters.etlDestTableName=="DataImportCategory">Selected</#if></#if>>Category Model</option>
			<option value="DataImportProduct" <#if requestParameters.etlDestTableName?has_content><#if requestParameters.etlDestTableName=="DataImportProduct">Selected</#if></#if>>Product Model</option>
			<option value="EtlImportOrderFields" <#if requestParameters.etlDestTableName?has_content><#if requestParameters.etlDestTableName=="EtlImportOrderFields">Selected</#if></#if>>Purchase Order Model</option>
			<option value="DataImportInvoiceHeader" <#if requestParameters.etlDestTableName?has_content><#if requestParameters.etlDestTableName=="DataImportInvoiceHeader">Selected</#if></#if>>Invoice Model</option>
			<option value="DataImportInvoiceItem" <#if requestParameters.etlDestTableName?has_content><#if requestParameters.etlDestTableName=="DataImportInvoiceItem">Selected</#if></#if>>Invoice Item Model</option>
			<option value="FioLockboxBatchStaging" <#if requestParameters.etlDestTableName?has_content><#if requestParameters.etlDestTableName=="FioLockboxBatchStaging">Selected</#if></#if>>Lockbox Model</option>
			<option value="FioLockboxBatchItemStaging" <#if requestParameters.etlDestTableName?has_content><#if requestParameters.etlDestTableName=="FioLockboxBatchItemStaging">Selected</#if></#if>>Lockbox Item Model</option>
			<option value="DataImportWallet" <#if requestParameters.etlDestTableName?has_content><#if requestParameters.etlDestTableName=="DataImportWallet">Selected</#if></#if>>Wallet Model</option>
			 -->
			
          <#--<#list etlPr_ocess as listEtl>
          <option value="${listEtl.tableName?if_exists}" <#if etlDestTableName?has_content && etlDestTableName?if_exists==listEtl.tableName?if_exists>selected</#if>>${listEtl.tableTitle?if_exists} </option>	
          </#list>-->
        </select>
        </#if>
        
        
        		
      </div>
      
      <div class="actions" style="padding-right: 6px;">
        <#if requestParameters.model?has_content || requestParameters.etlDestTableName?has_content>	
        <select class="form-control" onchange="getModelFields(this);" >
          <option value="">${uiLabelMap.selectModel}</option>
          <#list etlModelsList as model>
          <option value="${model.listName?if_exists}" <#if model.listName?has_content && model.listName?if_exists==requestParameters.model?if_exists>selected</#if>>${model.listName?if_exists} </option>	
          </#list>
        </select>
        </#if>
      </div>
    
    <#if requestParameters.batchId?has_content && requestParameters.groupId?has_content && requestParameters.processId?has_content>	    
    <div class="actions">
    	<form method="POST" action="importFileByBatch">
		  	<input type="hidden" name="batchId" value="${requestParameters.batchId?if_exists}">
		  	<input type="hidden" name="groupId" value="${requestParameters.groupId?if_exists}">
		  	<input type="hidden" name="processId" value="${requestParameters.processId?if_exists}">
		  	<button type="submit" class="btn btn-default">Reprocess</button>
		</form>
    </div>
    </#if>
    
</div>
<div class="portlet-body">
<div class="">
   <div class="">
      <div class="">
         <div class="">
            <table class="table table-striped table-header-fixed" id="sample_2">
               <thead>
                  <tr>
                     <th>${uiLabelMap.batchId}</th>
                     <th>${uiLabelMap.modelName}</th>
                     <th>${uiLabelMap.accessType}</th>
                     <th>${uiLabelMap.statusId}</th>
                     <th>${uiLabelMap.processedCount}</th>
                     <th>${uiLabelMap.notProcessedCount}</th>
                     <th>${uiLabelMap.createdBy}</th>
                  </tr>
               </thead>
               <tbody>
               </tbody>
            </table>
         </div>
      </div>
   </div>
</div>
</div></div></div>