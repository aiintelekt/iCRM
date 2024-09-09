<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<link rel="stylesheet" href="/metronic/css/bootstrap-fileinput.css" type="text/css"/>
<script src="/metronic/js/bootstrap-fileinput.js" type="text/javascript"></script>

<div class="row">
	<div id="main" role="main">
		<@sectionFrameHeader title="${uiLabelMap.dndImport!}" />
			<div class="col-md-12 col-lg-12 col-sm-12 ">
				<div class="border rounded bg-light margin-adj-accordian pad-top">
					<div class="row p-2">
						<div class="col-md-6 col-sm-6">
							<form id="uploadFileDnd"  role="form" class="form-horizontal" method="post" enctype="multipart/form-data" data-toggle="validator" onsubmit="return fileUpload();">
								<@inputHidden id="modelName" value="${defaultModelName!}"/>
								<@inputHidden id="extension" value="csv"/>
								<@inputHidden id="customSuccessMessage" value="File Uploaded, Kindly review the error logs section for the Errors records and Find lead screen for the successfully imported records"/>	
								<div class="form-group row">			   
								   <@labels label=uiLabelMap.importFile required=true labelColSize="col-sm-4 col-form-label" />
								   <div class="col-sm-3">
								   		<@dropdownCell 
										id="importFileOptionId"
										options=importFileOptions
										required=false
										allowEmpty=false
										dataLiveSearch=true
										/>	   
								      
								   </div>
								   <div class="col-sm-4">
								      
								   		<a id="dnd-file-download-btn" href="/crm-resource/template/dnd_import.csv" class="btn btn-xs btn-primary m5 tooltips ml-3" title="" data-original-title="Download Import File Template" download>
								   			<i class="fa fa-download"></i>
								   			${uiLabelMap.download!}
								   		</a>   
								      
								   </div>
								</div> 
			 					<div class="form-group row">
							 		<@labels label=uiLabelMap.fileToImport required=true labelColSize="col-sm-4 col-form-label" />
				                  	<div class="col-sm-5">
	                     				<input name="csv_fileName" accept=".csv" id="csv_fileName_lst" data-error="Please select Text file to Upload" type="file" size="30" maxlength="" class="form-control" required>
	                  				</div>
               					</div>
				              	<div class="form-group row">
					              	<div class="col-sm-4"></div>
					              	<div class="col-sm-4">
										<@submit label=uiLabelMap.upload/>
									</div>
								</div>
							</form>
						</div>
					</div>
				</div>
			<div class="clearfix"> </div>
			<div class="page-header border-b pt-2">
				<@headerH2 title="Results" class=""/>
				<div class="clearfix"> </div>
			</div>
  			<div class="table-responsive">  	
				<div id="myGrid" style="width: 100%;" class="ag-theme-balham"></div>     
			</div>
			<script type="text/javascript" src="/crm-resource/js/ag-grid/lead/loadDnd.js"></script>
		</div>
	</div>
</div>
<div id="dndErrorLogsModal" class="modal fade" role="dialog">
   <div class="modal-dialog modal-lg">
      <!-- Modal content-->
      <div class="modal-content">
         <div class="modal-header">
            <button type="reset" class="close" data-dismiss="modal">&times;</button>
         </div>
         <div class="modal-body">
            <div class="clearfix"> </div>
            <div class="page-header border-b pt-2">
               <@headerH2 title="DND Error Log List" class=""/>
            </div>
			<div class="clearfix"> </div>
  			<div class="table-responsive">  	
				<div id="dndLogGrid" style="width: 100%;" class="ag-theme-balham"></div>     
			</div>
			<script type="text/javascript" src="/crm-resource/js/ag-grid/lead/loadDndLogList.js"></script>
         </div>
         <div class="modal-footer">
            <@inputHidden id="importId" value=""/>
            <a href="#" class="btn btn-sm btn-primary navbar-dark dndErrorLogExport" id="dndErrorLogExport">${uiLabelMap.download!}</a>
            <button type="submit" class="btn btn-sm btn-primary" data-dismiss="modal">${uiLabelMap.close!}</button>
         </div>
      </div>
   </div>
</div>

<script type="text/javascript">

$( "#dndErrorLogExport" ).click(function() {
  $("#dndErrorLogExport").attr("href", "<@ofbizUrl>dndErrorLogExport</@ofbizUrl>?"+"importId="+$("#importId").val());
});

function fileUpload() {
   var fileSelected = $("#csv_fileName_lst").val();
   if(fileSelected ==""){
       $("#csv_fileName_lst").prop('required',true);
       return false;
    }else{
      $("#uploadFileDnd").attr("action", "<@ofbizUrl>uploadDndFile</@ofbizUrl>");
      return true;
    }
}

jQuery(document).ready(function() {
	$("#importFileOptionId").change(function() {
  		if ($(this).val() == "CSV") {
  			$("#dnd-file-download-btn").attr("href", "/crm-resource/template/dnd_import.csv");
  		}
	});

});

function viewImportError(importId){
	$('#importId').val(importId);
 	loadDndLogAgGrid(importId);
}  
</script>


