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
 
 function checkThis(cb) {
 	var id = cb.id;
	if(cb.checked==true)
		$("#nullValue_"+id).val("Y");
	if(cb.checked==false)
		$("#nullValue_"+id).val("N");
}
 function checkThis1(cb) {
 	var id = cb.id;
	if(cb.checked==true)
		$("#overrideValue_"+id).val("Y");
	if(cb.checked==false)
		$("#overrideValue_"+id).val("N");
}
</script>

<form name="EltBasedList" id="EltBasedList" method="get">
	<input  type="hidden" name="etlDestTableName"  id="etlDestTableName">
</form>	

<div class="row-fluid">
	<div class="col-lg-12">								
	<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 portlet light bordered">
		<div class="portlet-title band">
			<div class="caption font-red-sunglo">
				<i class="icon-settings font-red-sunglo"></i>
				<span class="caption-subject bold uppercase">${uiLabelMap.preProcessorLogs}</span>
			</div>
			<div class="actions">
			
		<#if etlPr_ocess?has_content>	
			<select class="form-control" onchange="getModelBasedList(this);">
			<option value="">${uiLabelMap.modelType}</option>
			
			<option value="DataImportAccount" <#if requestParameters.etlDestTableName?has_content><#if requestParameters.etlDestTableName=="DataImportAccount">Selected</#if></#if>>Account Model</option>
			<option value="DataImportLead" <#if requestParameters.etlDestTableName?has_content><#if requestParameters.etlDestTableName=="DataImportLead">Selected</#if></#if>>Lead Model</option>
			<option value="DataImportCustomer" <#if requestParameters.etlDestTableName?has_content><#if requestParameters.etlDestTableName=="DataImportCustomer">Selected</#if></#if>>Customer Model</option>
			<option value="DataImportContact" <#if requestParameters.etlDestTableName?has_content><#if requestParameters.etlDestTableName=="DataImportContact">Selected</#if></#if>>Contact Model</option>
			<option value="DataImportItm" <#if requestParameters.etlDestTableName?has_content><#if requestParameters.etlDestTableName=="DataImportItm">Selected</#if></#if>>ITM Model</option>
			<option value="DataImportActivity" <#if requestParameters.etlDestTableName?has_content><#if requestParameters.etlDestTableName=="DataImportActivity">Selected</#if></#if>>Activity Model</option>
									
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
		<div class="actions" style="padding-right: 6px;" >
				
			<select class="form-control" onchange="getModelFields(this);" >
			
				<option value="">${uiLabelMap.model}</option>
				<#list etlSet as model>
					<option value="${model.listName?if_exists}" <#if model.listName?has_content && model.listName?if_exists==requestParameters.model?if_exists>selected</#if>>${model.listName?if_exists} </option>	
				</#list>
				</select>
			
		</div>
		</div>
							<div class="portlet-body">
								<table class="table table-striped table-bordered table-hover" id="ca">
									<thead>
										<tr>
											<th>${uiLabelMap.task}</th>
											<th>${uiLabelMap.status}</th>
											<th>${uiLabelMap.model}</th>
											<th>${uiLabelMap.reason}</th>
											<th>${uiLabelMap.fileName}</th>
											<th>${uiLabelMap.date}</th>
										</tr>
									</thead>
									<tbody>
									<#if errorLogs?has_content>
										<#list errorLogs as err>
											<tr>
												<td>${err.taskName?if_exists}</td>
												<td>${err.status?if_exists}</td>
												<td>${err.listId?if_exists}</td>
												<td><span class="red">${err.logMsg1?if_exists}</span></td>
												<td>${err.logMsg2?if_exists}</td>
												<td>${err.timeStamp?if_exists}</td>												
											</tr>
										</#list>
									</#if>
									</tbody>
								</table>
							</div>
						</div>
</div>
</div>


<form name="modelList" id="modelList" method="POST" action="etlLogs">
	<input  type="hidden" name="model"  id="model">
	<input  type="hidden" name="etlDestTableName"  id="etlDestTableName1" value="${requestParameters.etlDestTableName?if_exists}">
</form>	

<script type="text/javascript">

	var TableAdvanced_ca = function () {
		
		var initTable2 = function () {
	        var table = $('#ca');
	
			/* Formatting function for row details */
	        function fnFormatDetails(oTable, nTr) {
	            var aData = oTable.fnGetData(nTr);
	            var sOut = '<table>';
	            sOut += '<tr><td>${uiLabelMap.task}:</td><td>' + aData[1] + '</td></tr>';
	            sOut += '<tr><td>${uiLabelMap.status}:</td><td>' + aData[2] + '</td></tr>';
	            sOut += '<tr><td>${uiLabelMap.model}:</td><td>' + aData[3] + '</td></tr>';
	            sOut += '<tr><td>${uiLabelMap.reason}:</td><td>' + aData[4] + '</td></tr>';
	            sOut += '<tr><td>${uiLabelMap.fileName}:</td><td>' + aData[5] + '</td></tr>';
	            sOut += '<tr><td>${uiLabelMap.date}:</td><td>' + aData[6] + '</td></tr>';
	            //sOut += '<tr><td>Others:</td><td>Could provide a link here</td></tr>';
	            sOut += '</table>';
				
	            return sOut;
	        }
	
	        /*
	         * Insert a 'details' column to the table
	         */
	        var nCloneTh = document.createElement('th');
	        nCloneTh.className = "table-checkbox";
	
	        var nCloneTd = document.createElement('td');
	        nCloneTd.innerHTML = '<span class="row-details row-details-close"></span>';
	
	        table.find('thead tr').each(function () {
	            this.insertBefore(nCloneTh, this.childNodes[0]);
	        });
	
	        table.find('tbody tr').each(function () {
	            this.insertBefore(nCloneTd.cloneNode(true), this.childNodes[0]);
	        });
	
	        /* Set tabletools buttons and button container */
	        $.extend(true, $.fn.DataTable.TableTools.classes, {
	            "container": "btn-group tabletools-btn-group pull-right",
	            "buttons": {
	                "normal": "btn btn-sm default",
	                "disabled": "btn btn-sm default disabled"
	            }
	        });
	
	        var oTable = table.dataTable({
	
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
	                "search": "Search:",
	                "zeroRecords": "No matching records found"
	            },
	
				"columnDefs": [
					{
		                "orderable": false,
		                "targets": [0]
		            },
		            
	            ],
	            "order": [
	                [0, 'asc']
	            ],
	            "lengthMenu": [
	                [5, 15, 20, -1],
	                [5, 15, 20, "All"] // change per page values here
	            ],
	
	            // set the initial value
	            "pageLength": 10,
	            "dom": "<'row' <'col-md-12'T>><'row'<'col-md-6 col-sm-12'l><'col-md-6 col-sm-12'f>r><'table-scrollable't><'row'<'col-md-5 col-sm-12'i><'col-md-7 col-sm-12'p>>", // horizobtal scrollable datatable
	
	            // Uncomment below line("dom" parameter) to fix the dropdown overflow issue in the datatable cells. The default datatable layout
	            // setup uses scrollable div(table-scrollable) with overflow:auto to enable vertical scroll(see: assets/global/plugins/datatables/plugins/bootstrap/dataTables.bootstrap.js). 
	            // So when dropdowns used the scrollable div should be removed. 
	            //"dom": "<'row' <'col-md-12'T>><'row'<'col-md-6 col-sm-12'l><'col-md-6 col-sm-12'f>r>t<'row'<'col-md-5 col-sm-12'i><'col-md-7 col-sm-12'p>>",
	
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
	            }
	        });
	
	        var tableWrapper = $('#ca_wrapper'); // datatable creates the table wrapper by adding with id {your_table_jd}_wrapper
	        var tableColumnToggler = $('#ca_column_toggler');
	        
	        tableWrapper.find('.dataTables_length select').select2(); // initialize select2 dropdown
	        
	        /* Add event listener for opening and closing details
	         * Note that the indicator for showing which row is open is not controlled by DataTables,
	         * rather it is done here
	         */
	        table.on('click', ' tbody td .row-details', function () {
	            var nTr = $(this).parents('tr')[0];
	            if (oTable.fnIsOpen(nTr)) {
	                /* This row is already open - close it */
	                $(this).addClass("row-details-close").removeClass("row-details-open");
	                oTable.fnClose(nTr);
	            } else {
	                /* Open this row */
	                $(this).addClass("row-details-open").removeClass("row-details-close");
	                oTable.fnOpen(nTr, fnFormatDetails(oTable, nTr), 'details');
	            }
	        });
	        
	        /* handle show/hide columns*/
	        $('input[type="checkbox"]', tableColumnToggler).change(function () {
	            /* Get the DataTables object again - this is not a recreation, just a get of the object */
	            var iCol = parseInt($(this).attr("data-column"));
	            iCol = iCol +1;
	            var bVis = oTable.fnSettings().aoColumns[iCol].bVisible;
	            oTable.fnSetColumnVis(iCol, (bVis ? false : true));
	        });
	        
	    }
	    
		return {

	        //main function to initiate the module
	        init: function () {
	
	            if (!jQuery().dataTable) {
	                return;
	            }
	
	            //console.log('me 1');
	
	            initTable2();
	            
	            //console.log('me 2');
	        }
	
	    };
	
	}();
	
	TableAdvanced_ca.init();
	
</script>

