<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<div class="clearfix"> </div>

<form name="uploadEconomicMetricsService" id="uploadEconomicMetricsService" action="uploadEconomicMetricsService" enctype="multipart/form-data" method="post">
<input type="hidden" id="groupId" name="groupId" value="${customField.groupId!}" />
<input type="hidden" name="activeTab" value="uploadEconomicMetric"/>
<#--  
<div class="page-header">
   <h2 class="float-left">Upload Customer</h2>
</div>
-->
<@pageSectionHeader title="Create Economic Metric " />
<div class="row padding-r">
   <div class="col-md-6 col-sm-6 form-horizontal">    
      <div class="form-group row has-error">
         <label class="col-sm-4 col-form-label">Import File</label>
         <div class="col-sm-7">
            <input id="uploadedFile" name="uploadedFile" type="file" size="30" maxlength="" class="form-control" required>
            <label class="col-form-label fw" id="errorFileId"></label>
         </div>
      </div>
      <div class="form-group row">
         <label class="col-sm-4 col-form-label">File Format</label>
         <div class="col-sm-7">
            <select class="form-control input-sm" disabled="">
               <option value="CSV">CSV</option>
            </select>
         </div>
      </div>
      <div class="form-group row">
         <label class="col-sm-4 col-form-label">CSV Format Template</label>
         <div class="col-sm-7">
            <a href="downloadFile?resourceName=cf-resource&componentName=custom-field&fileName=Economic_Code_Import_File_Format.csv" target="_blank" id="downloadFile" class="btn btn-xs btn-primary ml-0">Download</a>
            <#--<a id="file-download-btn" href="/cf-resource/template/Economic_Code_Import_File_Format.csv" class="btn btn-xs btn-primary ml-0" title="" download="">
               ${uiLabelMap.download!}
            </a>-->
         </div>
      </div>
   </div>
</div>
<div class="clearfix"></div>
<div class="col-md-12 col-sm-12">
   <div class="form-group row">
      <div class="offset-sm-2 col-sm-9">
         <input type="submit" class="btn btn-xs btn-primary ml-0" value="Upload"/>
      </div>
   </div>
</div>
<form>

<div class="clearfix"> </div>
<div class="page-header">
   <h2 class="float-left">Results</h2>
</div>
<div class="table-responsive">
   <table id="economicMetricImportDetails" class="table table-striped">
      <thead>
         <tr>
            <th>Total Count</th>
            <th class="pl-5">Processed Count</th>
            <th class="pl-5">Status</th>
            <th>File Name</th>
            <th>Message</th>
            <th>Uploaded By</th>
            <th></th>
         </tr>
      </thead>
      <tbody>
         <#assign customFieldFileUpload = delegator.findByAnd("CustomFieldFileUpload", {"customFieldGroupId" : "${customField.groupId!}", "customFieldType", "ECONOMIC_METRIC"}, Static["org.ofbiz.base.util.UtilMisc"].toList("createdStamp DESC"), false)>
         <#if customFieldFileUpload?exists && customFieldFileUpload?has_content>
         <#list customFieldFileUpload as customFieldFileUploadGV>
         <tr>
            <td class="text-right"><div class="pr-4">${customFieldFileUploadGV.noOfRecordsUploaded!}</div></td>
            <td class="text-right"><div class="pr-4">${customFieldFileUploadGV.noOfRecordsProcessed!}</div></td>
            <td>${customFieldFileUploadGV.status!}</td>
            <td>${customFieldFileUploadGV.originalFileName!}</td>
            <td>${customFieldFileUploadGV.message!}</td>
            <td>${customFieldFileUploadGV.uploadedBy!}</td>
            <td>
            <a onclick="viewEconomicErrorLogs('${customFieldFileUploadGV.requestId}')">
              <span class="btn btn-xs btn-primary ml-4" data-toggle="modal" href="#economicMetricErrorLogsModal">${uiLabelMap.view}</span>
            </a>
            </td>
         </tr>
         </#list>
         </#if>
      </tbody>
   </table>
</div>

<div id="economicMetricErrorLogsModal" class="modal fade" role="dialog">
   <div class="modal-dialog modal-lg">
      <!-- Modal content-->
      <div class="modal-content">
         <div class="modal-header">
            <button type="reset" class="close" data-dismiss="modal">&times;</button>
         </div>
         <div class="modal-body">
            <div class="clearfix"> </div>
            <div class="page-header">
               <h2 class="float-left">Economic Metric Error Log</h2>
            </div>
            <div class="table-responsive">
               <table id="ajaxEconomicMetricErrorLogsdatatable" class="table table-striped">
                  <thead>
                     <tr>
                        <th>${uiLabelMap.partyId!}</th>
                        <th>${uiLabelMap.economicMetricId!}</th>
                        <th>${uiLabelMap.metricId!}</th>
                        <th>${uiLabelMap.metricValue!}</th>
                        <th>${uiLabelMap.message}</th>
                     </tr>
                  </thead>
               </table>
            </div>
         </div>
         <div class="modal-footer">
            <a href="#" class="btn btn-sm btn-primary" id="downloadMetricLogs">${uiLabelMap.download!}</a>
            <button type="submit" class="btn btn-sm btn-primary" data-dismiss="modal">${uiLabelMap.close!}</button>
         </div>
      </div>
   </div>
</div>

<script>
$("#uploadEconomicMetricsService").submit(function(e){
   var fileName=$("#uploadedFile").val();
   var match = fileName.match(/(.+)\.(.+)/);
   var fileExt = match[2];
    $("#errorFileId").empty();
    if(fileExt != null && fileExt != "" && fileExt != "csv") {
        $("#errorFileId").append("Please choose CSV file");
        e.preventDefault();
    }
});
 
 $('#economicMetricImportDetails').DataTable({
    "order":[]
 });
 
     function viewEconomicErrorLogs(requestId) {
        $("#downloadMetricLogs").attr('href','<@ofbizUrl>downloadEconomicErrorLogs</@ofbizUrl>?requestId='+ requestId);
        $('#ajaxEconomicMetricErrorLogsdatatable').DataTable({
            "processing": true,
            "serverSide": true,
            "destroy": true,
            "searching": false,
            "ordering": false,
            "ajax": {
                "url": "economicMetricErrorLogs",
                "type": "POST",
                data: {"requestId" : requestId},
            },
            "Paginate": true,
            "language": {
                "emptyTable": "No data available in table",
                "info": "Showing _START_ to _END_ of _TOTAL_ entries",
                "infoEmpty": "No entries found",
                "infoFiltered": "(filtered1 from _MAX_ total entries)",
                "lengthMenu": "Show _MENU_ entries",
                "zeroRecords": "No matching records found",
                "oPaginate": {
                    "sNext": "Next",
                    "sPrevious": "Previous"
                }
            },
            "pageLength": 10,
            "bAutoWidth": false,
            "stateSave": false,
            "columns": [{
                    "data": "partyId"
                },
                {
                   "data": "customFieldGroupId"
                },
                {
                   "data": "segmentValueId"
                },
                {
                   "data": "metricValue"
                },
                {
                   "data": "message"
                }
            ]
        });
    }
    
</script>