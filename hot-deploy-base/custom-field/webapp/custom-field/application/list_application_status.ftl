<#include "component://homeapps/webapp/homeapps/lib/ofbizFormMacros.ftl"/>

<div class="row">
	<div class="col-md-12">
		<div class="portlet light">
			<div class="portlet-title">
				<div class="caption font-green-haze">
					<i class="fa fa-building font-green-haze"></i>
					<span class="caption-subject bold uppercase"> ${uiLabelMap.List!} ${uiLabelMap.AppStatus!}</span>
				</div>
				<div class="tools">
					<a href="javascript:;" class="collapse">
					</a>
					<#--<a href="#portlet-config" data-toggle="modal" class="config">
					</a>
					<a href="javascript:;" class="reload">
					</a>-->
					<a href="javascript:;" class="fullscreen">
					</a>
					<#--<a href="javascript:;" class="remove">
					</a>-->
				</div>
			</div>
			<div class="portlet-body form">
				
				<div class="table-toolbar">
					<form id="applicationStatusForm" method="post" action="<@ofbizUrl>createAppStatus</@ofbizUrl>" data-toggle="validator">
					
					<input type="hidden" name="clientRegistryId" value="${clientRegistryId!}" />
					<input type="hidden" name="sourceInvoked" value="PORTAL" />
					
					<div class="row">
						<div class="col-md-3">
							<@dateInput 
							id="fromDate"
							label=uiLabelMap.CommonFromDate
							value=appStatus.fromDate
							required=true
							disablePastDate="Y"
							/>
						</div>
						<div class="col-md-3">
							<@dateInput 
							id="thruDate"
							label=uiLabelMap.CommonThruDate
							value=appStatus.thruDate
							required=true
							disablePastDate="Y"
							/>
						</div>
						<div class="col-md-6 ">
							<div class="form-group form-md-line-input pull-left">
								<button type="submit" class="btn btn-sm default">
									<i class="fa fa-check"></i> ${uiLabelMap.Generate!}
								</button>
							</div>
						</div>
					</div>
					</form>
				</div>
				
				<div class="table-scrollable">
					<table class="table table-hover" id="ca">
					<thead>
					<tr>
						<th>${uiLabelMap.CommonFromDate!}</th>
						<th>${uiLabelMap.CommonThruDate!}</th>
						<th>${uiLabelMap.generatedByUserLogin!}</th>
						<th>${uiLabelMap.sourceInvoked!}</th>
						<th>${uiLabelMap.secretCode!}</th>
						<th class="text-center">Action</th>
					</tr>
					</thead>
					<tbody>
					
					<#if appStatusList?has_content>
						
					<#list appStatusList as ec>
					<tr>
						<td>${ec.fromDate!}</td>
						<td>${ec.thruDate!}</td>
						<td>${ec.generatedByUserLogin!}</td>
						<td>${ec.sourceInvoked!}</td>
						<td>${ec.secretCode!}</td>
						<td class="text-center">
							<div class="btn-group btn-group-solid">
								<a class="btn btn-xs default tooltips confirm-message" href="deleteAppStatus?clientRegistryId=${ec.clientRegistryId}&clientStatusId=${ec.clientStatusId}" data-original-title="Remove"><i class="fa fa-times red"></i></a>
								<a class="btn btn-xs default tooltips confirm-message" href="expireAppStatus?clientRegistryId=${ec.clientRegistryId}&clientStatusId=${ec.clientStatusId}" data-original-title="Expire"><i class="fa fa-minus purple"></i></a>
							</div>
						</td>	
					</tr>
					
					</#list>
						
					</#if>
					
					</tbody>
					</table>
				</div>
				
			</div>
		</div>
	</div>
	
</div>

<script type="text/javascript">
	
	$("#applicationStatustBtn").click(function() {
  		
  		$("#applicationStatusForm").submit();
  		
	});
	
</script>

<script type="text/javascript">
	
 	var TableAdvanced_ca = function () {
		
		var initTable2 = function () {
	        var table = $('#ca');
	
			/* Formatting function for row details */
	        function fnFormatDetails(oTable, nTr) {
	            var aData = oTable.fnGetData(nTr);
	            var sOut = '<table>';
	            sOut += '<tr><td>${uiLabelMap.CommonFromDate}:</td><td>' + aData[1] + '</td></tr>';
	            sOut += '<tr><td>${uiLabelMap.CommonThruDate}:</td><td>' + aData[2] + '</td></tr>';
	            sOut += '<tr><td>${uiLabelMap.generatedByUserLogin}:</td><td>' + aData[3] + '</td></tr>';
	            sOut += '<tr><td>${uiLabelMap.sourceInvoked}:</td><td>' + aData[4] + '</td></tr>';
	            sOut += '<tr><td>${uiLabelMap.secretCode}:</td><td>' + aData[5] + '</td></tr>';
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
		            {
		                "orderable": false,
		                "visible": false,
		                "targets": [5]
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
	                }, /*{
	                    "sExtends": "xls",
	                    "sButtonText": "Excel"
	                },*/ {
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