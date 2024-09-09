	<#--added by m.vijayakumr date time picker js-->
	<script src="/omstheme/js/bootstrap-datetimepicker.js"></script>
	<script src="/omstheme/js/bootstrap-datetimepicker.min.js"></script>
	<#--end @viijayakuamr-->

<style type="text/css">
.dataTables_filter {
     display: none;
}
.dataTables_length{
 display: none;
}
</style> 
<#assign exportType =""/>

<script type="text/javascript">
   var oTable =null;
 $(document).ready(function(){
   		loadProductTable();
   });
   
 	function loadProductTable() {
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
	         "ajax": "<@ofbizUrl>getOrderErrorLogs?channelId=${requestParameters.channelId?if_exists}&statusId=${requestParameters.statusId?if_exists}&orderId=${requestParameters.orderId?if_exists}&errorCode=${requestParameters.code?if_exists}&buyerEmail=${requestParameters.buyerEmail?if_exists}</@ofbizUrl>",

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
	                 "sName": "orderId",
	                 "bSortable": false,
	                 "mRender": function(data, type, full) {
	                     return full[0].orderId;
	                 }
	             },

	             {
	                 "sName": "orderId",
	                 "bSortable": false,
	                 "mRender": function(data, type, full) {
	                     return full[0].orderId;
	                 }
	             },
	             {
	                 "sName": "orderItemId",
	                 "bSortable": false,
	                 "mRender": function(data, type, full) {
	                     return full[0].orderItemId;
	                 }
	             },
	             {
	                 "sName": "orderDate",
	                 "bSortable": false,
	                 "mRender": function(data, type, full) {
	                     return full[0].orderDate;
	                 }
	             },

	             {
	                 "sName": "buyerName",
	                 "bSortable": false,
	                 "mRender": function(data, type, full) {
	                     return full[0].buyerName;
	                 }
	             },

	            {
	                 "sName": "buyerEmail",
	                 "bSortable": false,
	                 "mRender": function(data, type, full) {
	                     return full[0].buyerEmail;
	                 }
	             },
	             
	             {
	                 "sName": "sku",
	                 "bSortable": false,
	                 "mRender": function(data, type, full) {
	                     return full[0].sku;
	                 }
	             },
	           {
	                 "sName": "itemPrice",
	                 "bSortable": false,
	                 "mRender": function(data, type, full) {
	                     return full[0].itemPrice;
	                 }
	             },
	 	           {
	                 "sName": "errorCode",
	                 "bSortable": false,
	                 "mRender": function(data, type, full) {
	                     return full[0].errorCode;
	                 }
	             },
	    		 { 
                  	"bSortable": false,
                  	"mRender": function(data, type, full) {
                  		var contentDat =full[0].statusId;
                  		var contentData ="";
                  		if(contentDat=="READY"){
                  		 contentData = '';
                  		}else if(contentDat=="ERROR"){
                  		 contentData = 'ERROR';
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

	         /*

	         // set the initial value

	         "pageLength": 10,

	          "tableTools": {

	           "sSwfPath": "/metronic/swf/copy_csv_xls_pdf.swf",

	           "aButtons": [{

	             "sExtends": "pdf",

	             "sButtonText": "PDF"

	           }, {

	             "sExtends": "csv",

	             "sButtonText": "CSV"

	           }, {

	             "sExtends": "xls",

	             "sButtonText": "Excel"

	           }, {

	             "sExtends": "print",

	             "sButtonText": "Print",

	             "sInfo": 'Please press "CTRL+P" to print or "ESC" to quit',

	             "sMessage": "Generated by DataTables"

	           }, {

	             "sExtends": "copy",

	             "sButtonText": "Copy"

	           }]

	         }*/

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



	 }
	 
	$(document).ready(function(){
	  $('.form_datetime').datetimepicker({
        //language:  'fr',
        weekStart: 1,
        todayBtn:  1,
		autoclose: 1,
		todayHighlight: 1,
		startView: 2,
		forceParse: 0,
        //showMeridian: 1
    }); 
   	  $('.thru_datetime').datetimepicker({
        //language:  'fr',
        weekStart: 1,
        todayBtn:  1,
		autoclose: 1,
		todayHighlight: 1,
		startView: 2,
		forceParse: 0,
        //showMeridian: 1
    }); 
  });
	
</script>



<div class="portlet light">
<div class="portlet-title band">
    <div class="caption font-red-sunglo">
        <i class="icon-share font-red-sunglo"></i>
        <span class="caption-subject bold uppercase">Error Logs</span>
        <span class="caption-helper"></span>
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
<div class="slimScrollDiv">
	<form method="post" name="orderErrorLogs" action="orderErrorLogs" method="get" class="form-horizontal" role="form" > 
	  <div class="form-group">
		<label class="control-label col-sm-2" for="productStoreCatalog">Channel</label>
		<div class="col-sm-6">
		<select name="channelId" id="channelId" class="form-control" onchange="">
		<option value="">Please Select</option>
			<#if omChannels?has_content>
				<#list omChannels as channel>
					<option value="${channel.channelId?if_exists}"
					<#if requestParameters.channelId?if_exists==channel.channelId?if_exists>selected</#if>>${channel.channelName?if_exists}</option>
				</#list>
			</#if>
		</select>
		</div>
	  </div>
	   <div class="form-group">
		<label class="control-label col-sm-2" for="LogStatus">Log Status</label>
		<div class="col-sm-6">
		 <select name="statusId" id="statusId" class="form-control" onchange="">
		 <option value="">Please Select</option>
			<#if omLogStatuses?has_content>
				<#list omLogStatuses as log>
					<option value="${log.statusId?if_exists}"
					<#if requestParameters.statusId?if_exists==log.statusId?if_exists>selected</#if>>${log.statusName?if_exists}</option>
				</#list>
			</#if>
		</select>
		</div>
	  </div>
	   <div class="form-group">
		<label class="control-label col-sm-2" for="reportType">Error Code</label>
		<div class="col-sm-6">
		  <select name="errorCode" id="errorCode" class="form-control">
		  <option value=""></option>
			    	<#list errorCodes as errorCode>
			          <option value="${errorCode.code?if_exists}" <#if (parameters.code?has_content) && (parameters.code?if_exists==errorCode.code?if_exists)>SELECTED</#if>>${errorCode.codeDescription?if_exists} [${errorCode.code?if_exists}]</option>
			        </#list>
		</select>
		
		</div>
	  </div>  
	<div class="form-group">
		<label class="control-label col-sm-2" for="LogStatus">Order ID</label>
		<div class="col-sm-6">
	   <input type="text" class="form-control" name="orderId" value="${requestParameters.orderId?if_exists}">
		</div>
	  </div>
	   <div class="form-group">
		<label class="control-label col-sm-2" for="LogStatus">Customer Email</label>
		<div class="col-sm-6">
	   <input type="text" class="form-control" name="buyerEmail">
		</div>
	  </div>
	<div class="form-group">
		<label class="control-label col-sm-2" for="LogStatus">From Date</label>
		<div class="col-sm-6">
		<div class="controls input-append date form_datetime"  data-date-format="yyyy-mm-dd hh:ii:ss" data-link-field="fromDate">
		<input size="16" type="text" value="" readonly  class="btn btn-default btn-xs dropdown-toggle" placeholder="YYYY-MM-DD HH:MM:SS">
        <span class="add-on"><i class="icon-remove"></i></span>
		<span class="add-on"><i class="icon-th"></i></span>
		</div>
	   <input type="hidden" class="form-control" name="fromDate" value="${requestParameters.fromDate?if_exists}">
		</div>
	  </div>
	 <div class="form-group">
		<label class="control-label col-sm-2" for="LogStatus">Thru Date</label>
		<div class="col-sm-6">
		<div class="controls input-append date thru_datetime"  data-date-format="yyyy-mm-dd hh:ii:ss" data-link-field="thruDate">
		<input size="16" type="text" value="" readonly  class="btn btn-default btn-xs dropdown-toggle" placeholder="YYYY-MM-DD HH:MM:SS">
        <span class="add-on"><i class="icon-remove"></i></span>
		<span class="add-on"><i class="icon-th"></i></span>
		</div>
	   <input type="hidden" class="form-control" name="thruDate" value="${requestParameters.thruDate?if_exists}">
		</div>
	  </div>
		 <div class="form-group">
		<label class="control-label col-sm-2" for="email"></label>
		<div class="col-sm-2">
		  <input type="submit" class="btn btn-sm btn-primary" value="Find">
		</div>
	  </div>
	  </form>
</div></div>
<div class="portlet-body">
<div class="">
   <div class="">
      <div class="">
         <div class="">
            <table class="table table-striped table-header-fixed" id="sample_2">
               <thead>
                  <tr>
                     <th>Order Id</th>
                     <th>Order Item Id</th>
                     <th>Order Item Id</th>
                     <th>Order Date</th>
                     <th>Recipient Name</th>
                     <th>Customer EMail</th>
                     <th>SKU</th>
                     <th>Price</th>
                     <th>Error Code</th>
                     <th>Status</th>
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