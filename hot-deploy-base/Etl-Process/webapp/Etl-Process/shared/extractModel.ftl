<link type="text/css" rel="stylesheet" href="<@ofbizContentUrl>/om_css/tooltipstyle.css</@ofbizContentUrl>"/>
<script type="text/javascript" src="<@ofbizContentUrl>/om_js/tooltipscript.js</@ofbizContentUrl>"></script>

<style type="text/css">
.dataTables_filter {
     display: none;
}
.dataTables_length{
 display: none;
}
</style> 
<#assign exportType =""/>
<script>
function selectModel(value){
	window.location = "<@ofbizUrl>etlExtractModel</@ofbizUrl>?typeId="+value;
}
</script>
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
	         "ajax": "<@ofbizUrl>getExtractModels?typeId=${requestParameters.typeId?if_exists}</@ofbizUrl>",

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

	             [1, 'asc']

	         ],

	         "lengthMenu": [

	             [10, 15, 20, 50],

	             [10, 15, 20, 50] // change per page values here

	         ],
	         
	         "bAutoWidth":false,

	         "aoColumns": [
	         
	               {
	                 "sName": "jobId",
	                 "bSortable": false,
	                 "mRender": function(data, type, full) {
	                     return full[0].jobId;
	                 }
	             },
	             {
	                 "sName": "modelName",
	                 "bSortable": false,
	                 "mRender": function(data, type, full) {
	                     return full[0].modelName;
	                 }
	             }, {
	                 "sName": "modelName",
	                 "bSortable": false,
	                 "mRender": function(data, type, full) {
	                     return full[0].modelName;
	                 }
	             },
	             {
	                 "sName": "createdDate",
	                 "bSortable": false,
	                 "mRender": function(data, type, full) {
	                     return full[0].createdDate;
	                 }
	             },
	             
	          
	    		 { 
                  	"bSortable": false,
                  	"mRender": function(data, type, full) {
                  		var contentDat =full[0].download;
                  		var contentData ="";
                  		if(contentDat=="Y"){
                  		<#if requestParameters.typeId?has_content>
	                  		<#if requestParameters.typeId=="CUSTOMER">
	                  		 contentData = '<a class="btn btn-sm btn-primary" href="extractCustomerModelService?modelId='+full[0].jobId+'">${uiLabelMap.extract}</a>';
	                  		</#if>
                  		</#if>
                  		<#if requestParameters.typeId?has_content>
	                  		<#if requestParameters.typeId=="ACCOUNT">
	                  		 contentData = '<a class="btn btn-sm btn-primary" href="extractAccountsModelService?modelId='+full[0].jobId+'">${uiLabelMap.extract}</a>';
	                  		</#if>
                  		</#if>
                  		<#if requestParameters.typeId?has_content>
	                  		<#if requestParameters.typeId=="SUPPLIER">
	                  		 contentData = '<a class="btn btn-sm btn-primary" href="extractSuppliersModelService?modelId='+full[0].jobId+'">${uiLabelMap.extract}</a>';
	                  		</#if>
                  		</#if>
                  		<#if requestParameters.typeId?has_content>
	                  		<#if requestParameters.typeId=="CATEGORY">
	                  		 contentData = '<a class="btn btn-sm btn-primary" href="extractCategoryModelService?modelId='+full[0].jobId+'">${uiLabelMap.extract}</a>';
	                  		</#if>
                  		</#if>
                  		<#if requestParameters.typeId?has_content>
	                  		<#if requestParameters.typeId=="INVOICE">
	                  		 contentData = '<a class="btn btn-sm btn-primary" href="extractInvoiceModelService?modelId='+full[0].jobId+'">${uiLabelMap.extract}</a>';
	                  		</#if>
                  		</#if>
                  		<#if requestParameters.typeId?has_content>
	                  		<#if requestParameters.typeId=="INVOICE_ITEM">
	                  		 contentData = '<a class="btn btn-sm btn-primary" href="extractInvoiceItemModelService?modelId='+full[0].jobId+'">${uiLabelMap.extract}</a>';
	                  		</#if>
                  		</#if>
                  		<#if requestParameters.typeId?has_content>
	                  		<#if requestParameters.typeId=="ORDER">
	                  		 contentData = '<a class="btn btn-sm btn-primary" href="extractOrderModelService?modelId='+full[0].jobId+'">${uiLabelMap.extract}</a>';
	                  		</#if>
                  		</#if>
                  		<#if requestParameters.typeId?has_content>
	                  		<#if requestParameters.typeId=="PRODUCT">
	                  		 contentData = '<a class="btn btn-sm btn-primary" href="extractProductModelService?modelId='+full[0].jobId+'">${uiLabelMap.extract}</a>';
	                  		</#if>
                  		</#if>
                  		}
			            return contentData;
			        }
			      }

	         ],

	         "aoColumnDefs": [

	             {
	                 "sClass": "hidden-md hidden-lg",
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

<div class="portlet light">
<div class="portlet-title band">
    <div class="caption font-red-sunglo">
        <i class="icon-share font-red-sunglo"></i>
        <span class="caption-subject bold uppercase">${uiLabelMap.extractModel}</span>
        <span class="caption-helper"></span>
    </div>
    <div class="actions">
    	<div class="pull-right">
     <select name="modelType" id="modelType" class="form-control" onChange="selectModel(this.value);">
     	<option value="">${uiLabelMap.pleaseSelect}</option>
     	
     	<option value="ACCOUNT" <#if requestParameters.typeId?has_content><#if requestParameters.typeId=="ACCOUNT">Selected</#if></#if>>Account Model</option>
     	<option value="LEAD" <#if requestParameters.typeId?has_content><#if requestParameters.typeId=="LEAD">Selected</#if></#if>>Lead Model</option>
     	
     	<#-- 
     	<option value="CUSTOMER" <#if requestParameters.typeId?has_content><#if requestParameters.typeId=="CUSTOMER">Selected</#if></#if>>Customer Model</option>
     	<option value="SUPPLIER" <#if requestParameters.typeId?has_content><#if requestParameters.typeId=="SUPPLIER">Selected</#if></#if>>Supplier Model</option>
     	<option value="CATEGORY" <#if requestParameters.typeId?has_content><#if requestParameters.typeId=="CATEGORY">Selected</#if></#if>>Category Model</option>
     	<option value="PRODUCT" <#if requestParameters.typeId?has_content><#if requestParameters.typeId=="PRODUCT">Selected</#if></#if>>Product Model</option>
     	<option value="ORDER" <#if requestParameters.typeId?has_content><#if requestParameters.typeId=="ORDER">Selected</#if></#if>>Purchase Order Model</option>    
     	<option value="INVOICE" <#if requestParameters.typeId?has_content><#if requestParameters.typeId=="INVOICE">Selected</#if></#if>>Invoice Model</option>
     	<option value="INVOICE_ITEM" <#if requestParameters.typeId?has_content><#if requestParameters.typeId=="INVOICE_ITEM">Selected</#if></#if>>Invoice Item Model</option>
     	 -->
     	</select>
     </div>
    </div>
    <div class="actions">
        <#--<a class="btn btn-circle btn-icon-only btn-default" href="javascript:;">
            <i class="icon-cloud-upload"></i>
        </a>
        <a class="btn btn-circle btn-icon-only btn-default" href="javascript:;">
            <i class="icon-wrench"></i>
        </a>
        <a class="btn btn-circle btn-icon-only btn-default" href="javascript:;">
            <i class="icon-trash"></i>
        </a>
        <a class="btn btn-circle btn-icon-only btn-default fullscreen" href="javascript:;" data-original-title="" title=""> </a>-->
    </div>
</div>
<div class="portlet-body">
<div class="">
   <div class="">
      <div class="">
         <div class="">
            <table class="table table-striped table-header-fixed" id="sample_2">
               <thead>
                  <tr>
                     <th>${uiLabelMap.modelId}</th>
                     <th>${uiLabelMap.modelName}</th>
                     <th>${uiLabelMap.modelName}</th>
                     <th>${uiLabelMap.createdDate}</th>
                     <th>${uiLabelMap.extract}</th>
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