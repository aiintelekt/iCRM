<#include "component://homeapps/webapp/homeapps/lib/ofbizFormMacros.ftl"/>
<div class="page-header">
   <h2 class="float-left">Upload Customers into Segment</h2>
</div>
<form name="uploadSegmentService" id="uploadSegmentService" action="uploadSegmentService" enctype="multipart/form-data" method="post">
<input type="hidden" id="groupId" name="groupId" value="${customField.groupId!}" />
<input type="hidden" id="customFieldId" name="customFieldId" value="${customField.customFieldId!}" />
<input type="hidden" name="activeTab" value="uploadCustomers"/>
<div class="row padding-r">
   <div class="col-md-6 col-sm-6">
      <div class="form-group row has-error has-danger">
         <label class="col-sm-4 col-form-label text-danger">Import File*</label>
         <div class="col-sm-7">
         <div class="input-icon ">
            <input id="uploadedFile" name="uploadedFile" type="file" size="30" maxlength="" class="form-control">
            <label class="col-form-label fw" id="errorFileId"></label>
            </div>
         </div>
      </div>
      <div class="form-group row">
         <label class="col-sm-4 col-form-label">Update Mode</label>
         <div class="col-sm-7">
            <select class="custom-select ui dropdown  form-control input-sm" name="updateMode" id="updateMode">
               <option value="APPEND">--Select--</option>
               <option value="REPLACE_ALL">Replace All</option>
               <option value="APPEND">Append</option>
               <option value="REMOVE">Remove</option>
               <option value="REPLACE_ACCROSS">Replace Accross</option>
            </select>
         </div>
      </div>
      <div class="form-group row">
         <label class="col-sm-4 col-form-label">File Format</label>
         <div class="col-sm-7">
            <select class="custom-select ui dropdown  form-control input-sm" disabled="">
               <option value="CSV">CSV</option>
            </select>
         </div>
      </div>
      <div class="form-group row">
         <label class="col-sm-4 col-form-label">CSV Format Template</label>
         <div class="col-sm-7">
             <a href="downloadFile?resourceName=cf-resource&componentName=custom-field&fileName=Segment_Upload_File_Format.csv" target="_blank" id="downloadFile" class="btn btn-xs btn-primary ml-0">Download</a>
             <#--<a id="file-download-btn" href="/cf-resource/template/Segment_Upload_File_Format.csv" class="btn btn-xs btn-primary ml-0" title="" download="">
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
         <input type="submit" class="btn btn-xs btn-primary ml-0" value="Upload" id="submitBtn" name="submitBtn"/>
      </div>
   </div>
</div>
<form>
<script>

jQuery(document).ready(function() { 

   $("#uploadSegmentService").submit(function(e){
      var fileName=$("#uploadedFile").val();
      $("#errorFileId").empty();
      if(fileName != null && fileName != '' && fileName != undefined) {
      var match = fileName.match(/(.+)\.(.+)/);
      var fileExt = match[2];
       if(fileExt != null && fileExt != "" && fileExt != "csv") {
           $("#errorFileId").append("Please choose CSV file");
           e.preventDefault();
       } else if(fileExt != null && fileExt != "" && fileExt == "csv"){
           $("#submitBtn").attr("disabled", true);
       }
       } else {
           $("#errorFileId").append("Please select a file");
           e.preventDefault();
       }
   });
   $('#customerImportDetails').DataTable({
    "order": [],
   });
   $(document).attr("title", "Custom Field: ${uiLabelMap.AddAndUploadCustomers!}");
   
});   
   
</script>
<div class="clearfix"> </div>
<div class="page-header">
   <h2 class="float-left">Results</h2>
</div>
<div class="table-responsive">
   <table id="customerImportDetails" class="table table-striped">
      <thead>
         <tr>
            <th >Total Count</th>
            <th class="pl-5">Processed Count</th>
            <th class="pl-5">Status</th>
            <th>File Name</th>
            <th>Message</th>
            <th>Uploaded By</th>
         </tr>
      </thead>
      <tbody>
         <#assign customFieldFileUpload = delegator.findByAnd("CustomFieldFileUpload", {"customFieldGroupId" : "${customField.groupId!}", "segmentValueId" : "${customField.customFieldId!}", "customFieldType", "SEGMENTATION"}, Static["org.ofbiz.base.util.UtilMisc"].toList("createdStamp DESC"), false)>
         <#if customFieldFileUpload?exists && customFieldFileUpload?has_content>
         <#list customFieldFileUpload as customFieldFileUploadGV>
         <tr>
            <td class="text-right"><div class="pr-4">${customFieldFileUploadGV.noOfRecordsUploaded!}</div></td>
            <td class="text-right"><div class="pr-4">${customFieldFileUploadGV.noOfRecordsProcessed!}</div></td>
            <td>${customFieldFileUploadGV.status!}</td>
            <td>${customFieldFileUploadGV.originalFileName!}</td>
            <td>${customFieldFileUploadGV.message!}</td>
            <td>${customFieldFileUploadGV.uploadedBy!}</td>
         </tr>
         </#list>
         </#if>
      </tbody>
   </table>
</div>