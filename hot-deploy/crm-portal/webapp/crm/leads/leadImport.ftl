<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<link rel="stylesheet" href="/metronic/css/bootstrap-fileinput.css" type="text/css"/>
<script src="/metronic/js/bootstrap-fileinput.js" type="text/javascript"></script>
<div class="row">
	<div id="main" role="main">
		<@sectionFrameHeader title="${uiLabelMap.CreateLeadBatch!}" />	
		<div class="col-md-12 col-lg-12 col-sm-12 ">	
		<div class="card-header mt-2 mb-3">
	      <div class="row">      	
	      	<div class="col-md-6">
	      	<table class="table table-striped table-bordered table-advance table-hover">
			    <thead>
			        <tr>
			        	<th>
	                    	<i class="fa fa-bolt"></i> Importing </th>
			            <th>
			                <i class="fa fa-briefcase"></i> Imported Leads </th>
			            <#-- <th class="hidden-xs">
			                <i class="fa fa-briefcase"></i> Approved Leads </th>
			            <th>
			                <i class="fa fa-eye"></i> Not Approved Leads </th> -->    
			            <th>
			                <i class="fa fa-bug"></i> Error Records </th>
			        </tr>
			    </thead>
			    <tbody>
			        <tr>
			            <td class="highlight">
			                <div class="success"></div>
			                <a href="<@ofbizUrl>findLeads</@ofbizUrl>">Leads</a>
			            </td>
			            <td> ${importedLeads} </td>
			            <#-- <td> ${approvedLeads} </td>
			            <td> ${notApprovedLeads} </td> -->
			            <td> ${errorLeads} </td>
			        </tr>
			    </tbody>
			</table>	
			</div>
         
	      </div>
	   <div class="clearfix"> </div>
	</div>

	<div class="row padding-r">
		<div class="col-md-6 col-sm-6">
			<form id="uploadFileForm"  role="form" class="form-horizontal" method="post" enctype="multipart/form-data" data-toggle="validator" onsubmit="return fileUpload();">
				
			<@inputHidden id="modelName" value="${defaultModelName!}"/>
			<@inputHidden id="processId" value="${defaultModelName!}_Process"/>
			
			<@inputHidden id="customSuccessMessage" value="File Uploaded, Kindly review the error logs section for the Errors records and Find lead screen for the successfully imported records"/>	
				
			<div class="form-body">
			
			<@inputHidden id="modelType" value="DataImportLead"/>
			<@inputHidden id="modelId" value="${defaultModelId!}"/>
			
			<#-- 
			<@dropdownCell
				id="modelType"
				label=uiLabelMap.EtlModelType
				options=modelTypes
				value=uploadFilter.modelType
				allowEmpty=true
				required=true
				tooltip = uiLabelMap.EtlModelType
				/>
		
			<@dropdownCell 
				id="modelId"
				label=uiLabelMap.EtlModelName
				options=modelList
				value=uploadFilter.modelId
				allowEmpty=true
				required=true
				tooltip = uiLabelMap.EtlModelName
				/>		
			 -->
			 
			<div class="form-group row">
			   <@labels label=uiLabelMap.importFile required=true labelColSize="col-sm-4 col-form-label"/>
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
			      
			   		<a id="lead-file-download-btn" href="/crm-resource/template/o_uc65_lead.csv" class="btn btn-xs btn-primary m5 tooltips" title="" data-original-title="Download Import File Template" download>
			   			<i class="fa fa-download"></i>
			   			${uiLabelMap.download!}
			   		</a>   
			      
			   </div>
			</div> 
			
			<@dropdownCell 
				id="virtualTeamId"
				label=uiLabelMap.virtualTeam
				options=virtualTeamList
				required=false
				allowEmpty=true
				dataLiveSearch=true
				/>	
			 
			<div class="form-group row">
			   <@labels label=uiLabelMap.fileToImport required=true labelColSize="col-sm-4 col-form-label"/>
			   <div class="col-sm-7" style="margin-left: 13px">
			      
			   		<div class="row">
					
	                  <div class="fileinput fileinput-new pull-left" data-provides="fileinput" >
	                    <div class="input-group input-group-sm">
	                      <div class="form-control uneditable-input input-fixed input-group-sm" data-trigger="fileinput">
	                        <i class="fa fa-file fileinput-exists"></i>&nbsp;
	                        <span class="fileinput-filename" name="csvFile"> </span>
	                      </div>
	                      <span class="input-group-addon btn default btn-file">
	                      <span class="fileinput-new"> ${uiLabelMap.selectFile} </span>
	                      <span class="fileinput-exists"> ${uiLabelMap.change} </span>
	                      	<input type="hidden">
	                      
	                      	<input type="file" name="csv_fileName" id="csv_fileName_lst"  accept=".csv, .txt, .xls, .xlsx, .dat"> 
	                      
	                      </span>
	                      
	                      <a href="javascript:;" class="input-group-addon btn btn-xs btn-danger tooltips fileinput-exists" data-dismiss="fileinput">  ${uiLabelMap.removeButton} </a>
	                    </div>
	                    <span class="error" id="file_name_error_csv"></span> 
	                    <div class="help-block with-errors" id="csv_fileName_lst_error"></div>
	                  </div>
						
					</div>
					
					<div class="row">
						<div class="control-label" style="text-align: left;">
							<label class="red">		
		                 		${uiLabelMap.pickRightFile}
		                  	</label>
						</div>
					</div>	   
			      
			   </div>
			</div>	
															
			</div>
			<div class="form-group row">
				<div class="col-md-4 col-sm-4"></div>
				<div class="col-md-4 col-sm-4">
					<@submit label=uiLabelMap.upload />
					<#-- <@fromCommonAction iconClass="fa fa-upload" showCancelBtn=false showClearBtn=false submitLabel=uiLabelMap.upload style="padding-left:10px"/> -->
				</div>
			</div>
		</form>
						
	</div>
	
</div>
</div>
</div>
</div>

<script type="text/javascript">

function fileUpload() {
   var fileSelected = $("#csv_fileName_lst").val();
   if(fileSelected ==""){
       $("#csv_fileName_lst").prop('required',true);
       return false;
    }else{
      $("#uploadFileForm").attr("action", "<@ofbizUrl>uploadLeadFile</@ofbizUrl>?"+"modelName="+$("#modelName").val()+"&virtualTeamId="+$("#virtualTeamId").val());
      return true;
    }
}

jQuery(document).ready(function() {	

	$( "#modelType" ).change(function() {
	
		var nonSelectContent = "<span class='nonselect'>Select Model</span>";
		var options = '<option value="" data-content="'+nonSelectContent+'" selected="">Select Model</option>';	
  		
  		$.ajax({
			      
			type: "POST",
	     	url: "getEtlModels",
	        data:  {"modelType": $("#modelType").val()},
	        async: false,
	        success: function (returnedData) {   
	            
	            $('#modelId').html("");
	            
	            if (returnedData.code == 200 && returnedData.modelList.length !== 0) {
	            	
	            	for (var modelId in returnedData.modelList) {
	            		
	            		options += "<option value='"+modelId+"'>"+returnedData.modelList[modelId]+"</option>"
	            		
	            	}
	            	
	            }
								    	
	        }
	        
		});
		
		$('#modelId').html( options );
		
		$('#modelId').dropdown('refresh');
  		
	});
	
	$("#modelId").change(function() {
  		
  		$("#modelName").val( $("#modelId option:selected").text() );
  		$("#processId").val( $("#modelId option:selected").text() + "_Process" );
  		
  		$("#uploadFileForm").attr("action", "<@ofbizUrl>uploadLeadFile</@ofbizUrl>?"+"modelName="+$("#modelName").val());
  		
	});
	
	$("#importFileOptionId").change(function() {
  		if ($(this).val() == "CSV") {
  			$("#lead-file-download-btn").attr("href", "/crm-resource/template/o_uc65_lead.csv");
  		} else if ($(this).val() == "EXCEL") {
  			$("#lead-file-download-btn").attr("href", "/crm-resource/template/o_uc65_lead.xls");
  		} else if ($(this).val() == "TEXT") {
  			$("#lead-file-download-btn").attr("href", "/crm-resource/template/o_uc65_lead.txt");
  		} else if ($(this).val() == "XML") {
  			$("#lead-file-download-btn").attr("href", "/crm-resource/template/o_uc65_lead.xml");
  		} else if ($(this).val() == "JSON") {
  			$("#lead-file-download-btn").attr("href", "/crm-resource/template/o_uc65_lead.json");
  		} 
	});

});


</script>

