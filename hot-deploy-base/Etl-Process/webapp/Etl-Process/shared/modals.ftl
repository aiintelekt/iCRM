<#assign listNameRequest ="" >
<#if listNameDb?has_content>
	<#assign listNameRequest = listNameDb.listName?if_exists>
</#if>
<div class="modal fade bs-modal-lg" id="modelConfiguration" tabindex="-1"
	role="dialog" aria-hidden="true">
	<div class="modal-dialog modal-lg">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"
					aria-hidden="true"></button>
				<h4 class="modal-title">Model Configuration: ${listNameRequest?if_exists}</h4>
			</div>
			<div class="modal-body">
			
				<div class="tabbable-line">
                    <ul class="nav nav-tabs ">
                        <li class="active">
                            <a href="#tab_model_config" data-toggle="tab"> Configuration </a>
                        </li>
                        <li>
                            <a href="#tab_model_filter" data-toggle="tab"> Filter </a>
                        </li>
                        <li>
                            <a href="#tab_model_notification" data-toggle="tab"> Notification </a>
                        </li>
                    </ul>
                    <div class="tab-content" style="padding: 0">
                        <div class="tab-pane active" id="tab_model_config">
			
							<form id="updateModelDefaultForm" data-toggle="validator">
								
								<input type="hidden" name="modelName" value="${listNameRequest?if_exists}" class="form-control"/>
								
								<div class="form-body" id="modelConfigurationFromBody" >
									
									<div class="form-group row">
			                            <label class="col-md-3 control-label">Delimiter</label>
			                            <div class="col-md-9">
				                            <select name="etl_param_delimiter" class="form-control input-sm"
												placeholder="Select Delimiter">
												<option value="">Please Select</option>
												<option value="TAB" <#if textDelimiter?if_exists=='TAB'>selected</#if>>TAB</option>
												<option value="COMMA" <#if textDelimiter?if_exists=='COMMA'>selected</#if>>COMMA</option>
												<option value="SEMICOLON" <#if textDelimiter?if_exists=='SEMICOLON'>selected</#if>>SEMICOLON</option>
												<option value="PIPELINE" <#if textDelimiter?if_exists=='PIPELINE'>selected</#if>>PIPELINE</option>
											</select>
										</div>
			                        </div>
			                        
			                        <div class="form-group row">
			                            <label class="col-md-3 control-label">Record Count</label>
			                            <div class="col-md-9">
			                            	<input name="etl_param_recordCount" value="<#if modelDefault.recordCount?has_content>${modelDefault.recordCount}</#if>" class="form-control input-sm" placeholder="Total Record Count" type="number" step="1">
										</div>
			                        </div>
			                        
			                        <div class="form-group row">
			                            <label class="col-md-3 control-label">Is Data file No Header</label>
			                            <div class="col-md-9">
			                            	<input type="checkbox" name="etl_param_isDatafileNoHeader" value="Y" <#if modelDefault.isDatafileNoHeader?if_exists=='Y'>checked</#if>>
										</div>
			                        </div>
			                        
									<div class="portlet light">
			                            <div class="portlet-title">
			                                <div class="caption">
			                                    <i class="icon-puzzle font-grey-gallery"></i>
			                                    <span class="caption-subject theme-font-color bold font-grey-gallery"> Row Number </span>
			                                </div>
			                                <div class="tools">
			                                    <a href="" class="collapse" data-original-title="" title=""> </a>
			                                </div>
			                            </div>
			                            <div class="portlet-body" style="display: block;">
			                                
			                                <#if modelDefaultRangeList?has_content>
				                        
					                        <#assign counter = 0>
					                        <#list modelDefaultRangeList as range>
					                        <#assign counter = counter + 1>
					                        <div class="form-group row">
					                            <label class="col-md-2 control-label">Range</label>
					                            <div class="col-md-4">
						                            <input name="startRange" class="form-control input-sm" placeholder="Start" type="number" step="1" value="${range.startRange!}">
												</div>
												<div class="col-md-4">
						                            <input name="endRange" class="form-control input-sm" placeholder="End" type="number" step="1" value="${range.endRange!}">
												</div>
												<div class="col-md-2">
													<#if (counter > 1) >
														<a class="plus-icon01 rd" onclick="removeRangeRepeateContent(this)"><i class="fa fa-minus-circle" aria-hidden="true"></i></a>
													</#if> 
													<a onclick="addRangeRepeateContent(this)" class="gd"><i class="fa fa-plus-circle"></i></a>
												</div>
					                        </div>
					                        
					                        </#list>
					                        
					                        <#else>
					                        
					                        <div class="form-group row">
					                            <label class="col-md-3 control-label">Range</label>
					                            <div class="col-md-3">
						                            <input name="startRange" class="form-control input-sm" placeholder="Start" type="number" step="1">
												</div>
												<div class="col-md-3">
						                            <input name="endRange" class="form-control input-sm" placeholder="End" type="number" step="1">
												</div>
												<div class="col-md-3"><a onclick="addRangeRepeateContent(this)" class=""><i class="fa fa-plus-circle"></i></a></div>
					                        </div>
					                        
					                        </#if>
			                                
			                            </div>
			                        </div>
			                        
			                        <div class="portlet light">
			                            <div class="portlet-title">
			                                <div class="caption">
			                                    <i class="icon-puzzle font-grey-gallery"></i>
			                                    <span class="caption-subject theme-font-color bold font-grey-gallery"> SFTP Configuration</span>
			                                </div>
			                                <div class="tools">
			                                    <a href="" class="expand" data-original-title="" title=""> </a>
			                                </div>
			                            </div>
			                            <div class="portlet-body" style="display: none;">
			                                
			                                <div class="form-group row">
					                            <label class="col-md-3 control-label">Enable SFTP</label>
					                            <div class="col-md-9">
					                            	<input type="checkbox" name="etl_param_isSftpEnable" value="Y" <#if modelDefault.isSftpEnable?if_exists=='Y'>checked</#if>>
												</div>
					                        </div>
			                                
			                                <div class="form-group row">
					                            <label class="col-md-3 control-label">Host</label>
					                            <div class="col-md-9">
					                            	<input name="sftp_host" value="<#if sftpConfig?has_content>${sftpConfig.host}</#if>" class="form-control input-sm" placeholder="FTP host" type="text">
												</div>
					                        </div>
													                        
					                        <div class="form-group row">
					                            <label class="col-md-3 control-label">Username</label>
					                            <div class="col-md-9">
					                            	<input name="sftp_userName" value="<#if sftpConfig?has_content>${sftpConfig.userName}</#if>" class="form-control input-sm" placeholder="FTP username" type="text">
												</div>
					                        </div>
					                        
					                        <div class="form-group row">
					                            <label class="col-md-3 control-label">Password</label>
					                            <div class="col-md-9">
					                            	<input name="sftp_password" value="<#if sftpConfig?has_content>${sftpConfig.password}</#if>" class="form-control input-sm" placeholder="FTP password" type="password">
												</div>
					                        </div>
					                        
					                        <div class="form-group row">
					                            <label class="col-md-3 control-label">Port</label>
					                            <div class="col-md-9">
					                            	<input name="sftp_port" value="<#if sftpConfig?has_content>${sftpConfig.port}</#if>" class="form-control input-sm" placeholder="FTP port" type="number" step="1">
												</div>
					                        </div>
					                        
					                        <div class="form-group row">
					                            <label class="col-md-3 control-label">Location</label>
					                            <div class="col-md-9">
					                            	<input name="sftp_location" value="<#if sftpConfig?has_content>${sftpConfig.location}</#if>" class="form-control input-sm" placeholder="FTP location" type="text">
					                            	<span class="help-block small"><#if sftpImportLocation?has_content>Default: ${sftpImportLocation}</#if></span>
												</div>
					                        </div>
			                                
			                            </div>
			                        </div>
																													
								</div>
								
							</form>
							
						</div>
						
                        <div class="tab-pane" id="tab_model_filter">
                            
                            <form id="updateModelFilterForm">
					
								<input type="hidden" name="modelName" value="${listNameRequest?if_exists}" class="form-control"/>
								
								<div class="form-body" id="modelFilterFromBody" >
									
									<div class="form-group row">
			                            <label class="col-md-3 control-label">Field Name</label>
			                            <label class="col-md-2 control-label">Condition</label>
			                            <label class="col-md-3 control-label">Value</label>
			                            <label class="col-md-2 control-label">Operator</label>
			                            <label class="col-md-2 control-label"></label>
			                        </div>
									
									<div id="modelFilterRows">
									<div class="form-group row">
			                            <div class="col-md-3">
				                            <select name="fieldName" class="form-control input-sm">
				                            	<#if etlMappingElements?has_content>
				                            	<#list etlMappingElements as elem>
				                            	<option value="${elem.etlFieldName!}">${elem.etlFieldName!}</option>
				                            	</#list>
				                            	</#if>
											</select>
										</div>
										<div class="col-md-2">
				                            <select name="condition" class="form-control input-sm">
												<option value="EQUAL"> = </option>
												<option value="NOT_EQUAL"> <> </option>
												<option value="GATHER_THAN"> > </option>
												<option value="LESS_THAN"> < </option>
												<option value="LIKE"> LIKE </option>
												<option value="NOT_LIKE"> NOT LIKE </option>
											</select>
										</div>
										<div class="col-md-3">
				                            <input name="value" class="form-control input-sm" placeholder="Value" type="text">
										</div>
										<div class="col-md-2">
				                            <select name="operator" class="form-control input-sm">
												<option value="AND"> AND </option>
												<option value="OR"> OR </option>
											</select>
										</div>
										<div class="col-md-2"><a onclick="addModelFilterRepeateContent(this)" class="gn"><i class="fa fa-plus-circle"></i></a></div>
			                        </div>
			                        </div>
			                        		
			                        						
								</div>
								
							</form>
                            
                        </div>
                        
                        <div class="tab-pane" id="tab_model_notification">
                            
                            <form id="updateModelNotificationForm">
					
								<input type="hidden" name="modelName" value="${listNameRequest?if_exists}" class="form-control"/>
								
								<div class="form-body" id="modelNotificationFromBody" >
									
									<div id="modelNotificationRows">
									<div class="row" style="padding-bottom: 30px">
									
										<div class="col-md-11">
											<div class="form-group row">
												<label class="col-md-2 control-label">Reason</label>
												<div class="col-md-4">
						                            <select name="reason" class="form-control input-sm">
						                            	<option value="COMPLETE">Complete</option>
						                            	<option value="FAILURE">Failure</option>
													</select>
												</div>
												
												<label class="col-md-2 control-label">Subject</label>
												<div class="col-md-4">
						                            <input name="subject" class="form-control input-sm" placeholder="Email Subject" type="text">
												</div>
											</div>
											
											<div class="form-group row">
												<label class="col-md-2 control-label">Content</label>
												<div class="col-md-10">
						                            <textarea name="content" placeholder="Email content" class="form-control input-sm" rows="3"></textarea>
												</div>
											</div>
											
											<div class="form-group row">
												<label class="col-md-2 control-label">From</label>
												<div class="col-md-4">
						                            <input name="fromString" class="form-control input-sm" placeholder="from emails with comma seperate" type="text">
												</div>
												
												<label class="col-md-2 control-label">To</label>
												<div class="col-md-4">
						                            <input name="toString" class="form-control input-sm" placeholder="to emails with comma seperate" type="text">
												</div>
											</div>
											
											<div class="form-group row">
												<label class="col-md-2 control-label">CC</label>
												<div class="col-md-4">
						                            <input name="ccString" class="form-control input-sm" placeholder="cc emails with comma seperate" type="text">
												</div>
												
												<label class="col-md-2 control-label">BCC</label>
												<div class="col-md-4">
						                            <input name="bccString" class="form-control input-sm" placeholder="bcc emails with comma seperate" type="text">
												</div>
											</div>
										</div>
										
										<div class="col-md-1"><a onclick="addModelNotificationRepeateContent(this)" class="gn"><i class="fa fa-plus-circle"></i></a></div>
										
			                        </div>
			                        </div>
			                        		
			                        						
								</div>
								
							</form>
                            
                        </div>
                        
                    </div>
                </div>	
					
			</div>
			<div class="modal-footer">
				<input type="button" id="updateModelDefaultBtn"
					class="btn btn-xs btn-info default" value="Update">
			</div>
			
		</div>
		
	</div>
	
</div>
	
<div class="modal fade" id="modelElementConfiguration" tabindex="-1"
	role="basic" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"
					aria-hidden="true"></button>
				<h4 class="modal-title">
					Model: <span class="modelName">${listNameRequest?if_exists}</span>
					, &nbsp;&nbsp;
					Element: <span class="modelElementCustomName"></span>
				</h4>
			</div>
			<div class="modal-body">
			
				<div class="tabbable-line">
                    <ul class="nav nav-tabs ">
                        <li class="active">
                            <a href="#tab_elem_config" data-toggle="tab"> Configuration </a>
                        </li>
                        <li>
                            <a href="#tab_elem_filter" data-toggle="tab"> Filter </a>
                        </li>
                    </ul>
                    <div class="tab-content" style="padding: 0">
                        <div class="tab-pane active" id="tab_elem_config">
                           
                            <form id="updateModelElementDefaultForm">
					
								<input type="hidden" name="modelName" value="${listNameRequest?if_exists}" class="form-control"/>
								<input type="hidden" name="modelElementName" class="modelElementName" class="form-control"/>
								
								<div class="form-body" id="modelElementConfigurationFromBody" >
									
									<div class="form-group row"> 
			                            <label class="col-md-3 control-label">Trim</label>
			                            <div class="col-md-9">
				                            <select name="etl_param_trim" class="form-control input-sm">
												<option value="NO">No</option>
												<option value="YES">Yes</option>
											</select>
										</div>
			                        </div>
			                        
			                        <div class="form-group row">
			                            <label class="col-md-3 control-label">Concat</label>
			                            <div class="col-md-4">
				                            <input name="etl_param_concat_prefix" class="form-control input-sm" placeholder="Prefix" type="text">
										</div>
										<div class="col-md-5">
				                            <input name="etl_param_concat_suffix" class="form-control input-sm" placeholder="Suffix" type="text">
										</div>
			                        </div>
			                        
			                        <div class="form-group row">
			                            <label class="col-md-3 control-label">Substring</label>
			                            <div class="col-md-4">
				                            <input name="etl_param_substring_start" class="form-control input-sm" placeholder="Start" type="number" step="1">
										</div>
										<div class="col-md-5">
				                            <input name="etl_param_substring_end" class="form-control input-sm" placeholder="End" type="number" step="1">
										</div>
			                        </div>
			                        
			                        <div class="form-group row">
			                            <label class="col-md-3 control-label">Replace</label>
			                            <div class="col-md-9">
				                            <textarea name="etl_param_string_replace" placeholder="Replace string by JSON data, Exp: {\"oldString\": \"newString\"}" class="form-control input-sm" rows="3"></textarea>
										</div>
			                        </div>
			                        
			                        <div class="form-group row">
			                            <label class="col-md-3 control-label">Add</label>
			                            <div class="col-md-9">
				                            <input name="etl_param_numeric_add" class="form-control input-sm" placeholder="Add Numeric" type="number" step="any">
										</div>
			                        </div>
			                        
			                        <div class="form-group row">
			                            <label class="col-md-3 control-label">Subtract</label>
			                            <div class="col-md-9">
				                            <input name="etl_param_numeric_subtract" class="form-control input-sm" placeholder="Subtract Numeric" type="number" step="any">
										</div>
			                        </div>
			                        
			                        <div class="form-group row">
			                            <label class="col-md-3 control-label">Multiply</label>
			                            <div class="col-md-9">
				                            <input name="etl_param_numeric_multiply" class="form-control input-sm" placeholder="Multiply Numeric" type="number" step="any">
										</div>
			                        </div>
			                        
			                        <div class="form-group row">
			                            <label class="col-md-3 control-label">Divide</label>
			                            <div class="col-md-9">
				                            <input name="etl_param_numeric_divide" class="form-control input-sm" placeholder="Divide Numeric" type="number" step="any">
										</div>
			                        </div>
			                        
			                        <div class="form-group row">
			                            <label class="col-md-3 control-label">Custom Function</label>
			                            <div class="col-md-9">
				                            <select name="etl_param_custom_function" class="form-control input-sm">
												<option value="">Please select..</option>
												<option value="testCustomService">testCustomFunction</option>
											</select>
										</div>
			                        </div>
			                        
			                        <div class="form-group row">
			                            <label class="col-md-3 control-label">Max Length</label>
			                            <div class="col-md-9">
				                            <input name="etl_param_max_length" class="form-control input-sm" placeholder="Max Length of data" type="number" step="1">
										</div>
			                        </div>
			                        
			                        <div class="form-group row">
			                            <label class="col-md-3 control-label">Default Value</label>
			                            <div class="col-md-9">
				                            <input name="etl_param_default_value" class="form-control input-sm" placeholder="Default Value of data" type="text">
										</div>
			                        </div>
			                        		
								</div>
								
							</form>
                            
                        </div>
                        <div class="tab-pane" id="tab_elem_filter">
                            
                            <form id="updateModelElementFilterForm">
					
								<input type="hidden" name="modelName" value="${listNameRequest?if_exists}" class="form-control"/>
								<input type="hidden" name="modelElementName" class="modelElementName" class="form-control"/>
								
								<div class="form-body" id="modelElementFilterFromBody" >
									
									<div class="form-group row">
			                            <label class="col-md-3 control-label">Filter Function</label>
			                            <div class="col-md-9">
				                            <select name="etl_param_custom_filterFunction" class="form-control input-sm">
												<option value="">Please select..</option>
												<option value="testFilterService">testFilterService</option>
											</select>
										</div>
			                        </div>
									
									<div class="form-group row">
			                            <label class="col-md-3 control-label">Field Name</label>
			                            <label class="col-md-2 control-label">Condition</label>
			                            <label class="col-md-3 control-label">Value</label>
			                            <label class="col-md-2 control-label">Operator</label>
			                            <label class="col-md-2 control-label"></label>
			                        </div>
									
									<div id="modelElementFilterRows">
									<div class="form-group row">
			                            <div class="col-md-3">
				                            <select name="fieldName" class="form-control input-sm">
				                            	<#if etlMappingElements?has_content>
				                            	<#list etlMappingElements as elem>
				                            	<option value="${elem.etlFieldName!}">${elem.etlFieldName!}</option>
				                            	</#list>
				                            	</#if>
											</select>
										</div>
										<div class="col-md-2">
				                            <select name="condition" class="form-control input-sm">
												<option value="EQUAL"> = </option>
												<option value="NOT_EQUAL"> <> </option>
												<option value="GATHER_THAN"> > </option>
												<option value="LESS_THAN"> < </option>
												<option value="LIKE"> LIKE </option>
												<option value="NOT_LIKE"> NOT LIKE </option>
											</select>
										</div>
										<div class="col-md-3">
				                            <input name="value" class="form-control input-sm" placeholder="Value" type="text">
										</div>
										<div class="col-md-2">
				                            <select name="operator" class="form-control input-sm">
												<option value="AND"> AND </option>
												<option value="OR"> OR </option>
											</select>
										</div>
										<div class="col-md-2"><a onclick="addElementFilterRepeateContent(this)" class="gn"><i class="fa fa-plus-circle"></i></a></div>
			                        </div>
			                        </div>
			                        		
			                        						
								</div>
								
							</form>
                            
                        </div>
                    </div>
                </div>
					
			</div>
			<div class="modal-footer">
				<input type="button" id="updateModelElementDefaultBtn"
					class="btn btn-xs btn-info default" value="Update">
			</div>
			
		</div>
		
	</div>
	
</div>

<script>

var elementFieldOptions = '<#if etlMappingElements?has_content><#list etlMappingElements as elem><option value="${elem.etlFieldName!}">${elem.etlFieldName!}</option></#list></#if>';

// Script for modelConfiguration [start]

$('#updateModelDefaultBtn').click(function () {

	$.post('updateEtlModelDefaults', $('#updateModelDefaultForm').serialize(), function(returnedData) {
	
		if (returnedData.code == 200) {
			//$('#modelConfiguration').modal('toggle');
			//notificationProgress("Successfully updated model configuration..", "success");
			
			$.post('updateEtlModelFilters', $('#updateModelFilterForm').serialize(), function(returnedData) {
				
				if (returnedData.code == 200) {
					$('#modelConfiguration').modal('toggle');
					notificationProgress("Successfully updated model configuration..", "success");
				}
					
			});
			
			$.post('updateEtlModelNotifications', $('#updateModelNotificationForm').serialize(), function(returnedData) {
				
				if (returnedData.code == 200) {
					
				}
					
			});
			
		}
			
	});

});

function addRangeRepeateContent (actionButton) {

	var cloneHtml = $(actionButton).parent().parent().clone();

	if ( $(actionButton).parent().children().find('[class=\"fa fa-minus-circle\"]').length == 0 ) {
        cloneHtml.children().find('[class=\"fa fa-plus-circle\"]').parent().before('<a class="plus-icon01 rd" onclick="removeRangeRepeateContent(this)"><i class="fa fa-minus-circle" aria-hidden="true"></i></a>');
    }
    
    $(actionButton).parent().parent().after(cloneHtml);

}

function removeRangeRepeateContent (actionButton) {
	$(actionButton).parent().parent().remove();
}

$('#modelConfiguration').on('shown.bs.modal', function () {
  	
  	$.ajax({
			      
		type: "POST",
     	url: "getEtlModelFilters",
        data:  {"modelName": "${listNameRequest?if_exists}"},
        success: function (data) {   
            
            $('#modelFilterRows').html("");
            
            if (data.length === 0) {
            	
            	// no filter found
            	var row = prepareModelFilterRow("", "", "", "", 0);
	                
	            $('#modelFilterRows').append(row);   
            	
            } else {
						            	
            	var counter = 0;
            	for(var i in data) {
	     			var fieldName = data[i].fieldName;
	     			var filterCondition = data[i].filterCondition;
	     			var filterValue = data[i].filterValue;
	     			var operator = data[i].operator;
	     			counter++;
	     			
	     			var row = prepareModelFilterRow(fieldName, filterCondition, filterValue, operator, counter);
	                
	            	$('#modelFilterRows').append(row);     
	            	$('#model_filter_fieldName_'+counter).val(fieldName);     
	     			
				}
            	
            }
			    	
        }
        
	});    
	
	$.ajax({
			      
		type: "POST",
     	url: "getEtlModelNotifications",
        data:  {"modelName": "${listNameRequest?if_exists}"},
        success: function (data) {   
            
            $('#modelNotificationRows').html("");
            
            if (data.length === 0) {
            	
            	// no filter found
            	var row = prepareModelNotificationRow("", "", "", "", "", "", "", 0);
	                
	            $('#modelNotificationRows').append(row);   
            	
            } else {
						            	
            	var counter = 0;
            	for(var i in data) {
            	
	     			var reason = data[i].reason;
	     			var subject = data[i].subject;
	     			var content = data[i].content;
	     			var fromString = data[i].fromString;
	     			var toString = data[i].toString;
	     			var ccString = data[i].ccString;
	     			var bccString = data[i].bccString;
	     			
	     			counter++;
	     			
	     			var row = prepareModelNotificationRow(reason, subject, content, fromString, toString, ccString, bccString, counter);
	                
	            	$('#modelNotificationRows').append(row);     
	            	$('#model_notification_reason_'+counter).val(reason);     
	     			
				}
            	
            }
			    	
        }
        
	});    
  	
})

function prepareModelNotificationRow(reason, subject, content, fromString, toString, ccString, bccString, counter) {

	var row = '<div class="row" style="padding-bottom: 30px">' +
									
										'<div class="col-md-11">' +
											'<div class="form-group row">' +
												'<label class="col-md-2 control-label">Reason</label>' +
												'<div class="col-md-4">' +
						                            '<select name="reason" id="model_notification_reason_'+counter+'" class="form-control input-sm">' +
						                            	'<option value="COMPLETE">Complete</option>' +
						                            	'<option value="FAILURE">Failure</option>' +
													'</select>' +
												'</div>' +
												
												'<label class="col-md-2 control-label">Subject</label>' +
												'<div class="col-md-4">' +
						                            '<input name="subject" value="'+subject+'" class="form-control input-sm" placeholder="Email Subject" type="text">' +
												'</div>' +
											'</div>' +
											
											'<div class="form-group row">' +
												'<label class="col-md-2 control-label">Content</label>' +
												'<div class="col-md-10">' +
						                            '<textarea name="content" placeholder="Email content" class="form-control input-sm" rows="3">'+content+'</textarea>' +
												'</div>' +
											'</div>' +
											
											'<div class="form-group row">' +
												'<label class="col-md-2 control-label">From</label>' +
												'<div class="col-md-4">' +
						                            '<input name="fromString" value="'+fromString+'" class="form-control input-sm" placeholder="from emails with comma seperate" type="text">' +
												'</div>' +
												
												'<label class="col-md-2 control-label">To</label>' +
												'<div class="col-md-4">' +
						                            '<input name="toString" value="'+toString+'" class="form-control input-sm" placeholder="to emails with comma seperate" type="text">' +
												'</div>' +
											'</div>' +
											
											'<div class="form-group row">' +
												'<label class="col-md-2 control-label">CC</label>' +
												'<div class="col-md-4">' +
						                            '<input name="ccString" value="'+ccString+'" class="form-control input-sm" placeholder="cc emails with comma seperate" type="text">' +
												'</div>' +
												
												'<label class="col-md-2 control-label">BCC</label>' +
												'<div class="col-md-4">' +
						                            '<input name="bccString" value="'+bccString+'" class="form-control input-sm" placeholder="bcc emails with comma seperate" type="text">' +
												'</div>' +
											'</div>' +
										'</div>' +
										
										'<div class="col-md-1"><a onclick="addModelNotificationRepeateContent(this)" class="gn"><i class="fa fa-plus-circle"></i></a></div>' +
										
			                        '</div>'
			                        ;
			                        

	return row;

}

function prepareModelFilterRow(fieldName, filterCondition, filterValue, operator, counter) {

	var row = '<div class="form-group row">' +
                    '<div class="col-md-3">' +
                        '<select name="fieldName" id="model_filter_fieldName_'+counter+'" class="form-control input-sm">' +
                        	elementFieldOptions +
						'</select>' +
					'</div>' +
					'<div class="col-md-2">' +
                        '<select name="condition" class="form-control input-sm">' +
							'<option value="EQUAL"' + (filterCondition == "EQUAL" ? " selected='selected'" : "") + '> = </option>' +
							'<option value="NOT_EQUAL"' + (filterCondition == "NOT_EQUAL" ? " selected='selected'" : "") + '> <> </option>' +
							'<option value="GATHER_THAN"' + (filterCondition == "GATHER_THAN" ? " selected='selected'" : "") + '> > </option>' +
							'<option value="LESS_THAN"' + (filterCondition == "LESS_THAN" ? " selected='selected'" : "") + '> < </option>' +
							'<option value="LIKE"' + (filterCondition == "LIKE" ? " selected='selected'" : "") + '> LIKE </option>' +
							'<option value="NOT_LIKE"' + (filterCondition == "NOT_LIKE" ? " selected='selected'" : "") + '> NOT LIKE </option>' +
						'</select>' +
					'</div>' +
					'<div class="col-md-3">' +
                        '<input name="value" value="'+filterValue+'" class="form-control input-sm" placeholder="Value" type="text">' +
					'</div>' +
					'<div class="col-md-2">' +
                        '<select name="operator" class="form-control input-sm">' +
							'<option value="AND"' + (operator == "AND" ? " selected='selected'" : "") + '> AND </option>' +
							'<option value="OR"' + (operator == "OR" ? " selected='selected'" : "") + '> OR </option>' +
						'</select>' +
					'</div>' +
					
					'<div class="col-md-2">' +
					(counter > 1 ? " <a class='plus-icon01 rd' onclick='removeModelFilterRepeateContent(this)'><i class='fa fa-minus-circle' aria-hidden='true'></i></a>" : "") + 
					'<a onclick="addModelFilterRepeateContent(this)" class=""><i class="fa fa-plus-circle"></i></a>' +
					'</div>' +
					
                '</div>';

	return row;

}

// Script for modelConfiguration [end]

// Script for modelElementConfiguration [start]

$('#updateModelElementDefaultBtn').click(function () {

	$.post('updateEtlModelElementDefaults', $('#updateModelElementDefaultForm').serialize(), function(returnedData) {
	
		if (returnedData.code == 200) {
			//$('#modelElementConfiguration').modal('toggle');
			//notificationProgress("Successfully updated model element configuration..", "success");
			
			$.post('updateEtlModelElementFilters', $('#updateModelElementFilterForm').serialize(), function(returnedData) {
				
				if (returnedData.code == 200) {
					$('#modelElementConfiguration').modal('toggle');
					notificationProgress("Successfully updated model element configuration..", "success");
				}
					
			});
			
		}
			
	});
	
});

function showModelElementConfig(element) {
	
	var elementId = $(element).attr("data-etlElementId");
	var elementName = $(element).attr("data-etlFieldName");
	var elementCustomName = $("#etlFieldName_"+elementId).val();
	
	//alert("elementId>"+elementId+", elementName>"+elementName+", elementCustomName>"+elementCustomName);
	
	$('#modelElementConfiguration .modelElementCustomName').html(elementCustomName);
	$('#modelElementConfiguration .modelElementName').val(elementName);
	
	$.ajax({
			      
		type: "POST",
     	url: "getEtlModelElementDefaults",
        data:  {"modelName": "${listNameRequest?if_exists}", "modelElementName": elementName},
        success: function (data) {   
            
            if (data.length === 0) {
            	
            	$('#updateModelElementDefaultForm')[0].reset();
            	
            } else {
            	
            	for(var i in data) {
	     			var propertyName = data[i].propertyName;
	     			var propertyValue = data[i].propertyValue;
	     			//alert("propertyName>"+propertyName+", propertyValue>"+propertyValue);
	     			
	     			$('[name="etl_param_'+propertyName+'"]').val(propertyValue);
				}
            	
            }
			                  	
            $.ajax({
			      
				type: "POST",
		     	url: "getEtlModelElementFilters",
		        data:  {"modelName": "${listNameRequest?if_exists}", "modelElementName": elementName},
		        success: function (data) {   
		            
		            $('#modelElementFilterRows').html("");
		            
		            if (data.length === 0) {
		            	
		            	// no filter found
		            	var row = prepareElementFilterRow("", "", "", "", 0);
			                
			            $('#modelElementFilterRows').append(row);   
		            	
		            } else {
								            	
		            	var counter = 0;
		            	for(var i in data) {
			     			var fieldName = data[i].fieldName;
			     			var filterCondition = data[i].filterCondition;
			     			var filterValue = data[i].filterValue;
			     			var operator = data[i].operator;
			     			counter++;
			     			
			     			var row = prepareElementFilterRow(fieldName, filterCondition, filterValue, operator, counter);
			                
			            	$('#modelElementFilterRows').append(row);     
			            	$('#elem_filter_fieldName_'+counter).val(fieldName);     
			     			
						}
		            	
		            }
					    	
		        	$('#modelElementConfiguration').modal('toggle');	 
		        	
		        }
		        
			});      	
                  	
        }
        
	});
	
}

function prepareElementFilterRow(fieldName, filterCondition, filterValue, operator, counter) {

	var row = '<div class="form-group row">' +
                    '<div class="col-md-3">' +
                        '<select name="fieldName" id="elem_filter_fieldName_'+counter+'" class="form-control input-sm">' +
                        	elementFieldOptions +
						'</select>' +
					'</div>' +
					'<div class="col-md-2">' +
                        '<select name="condition" class="form-control input-sm">' +
							'<option value="EQUAL"' + (filterCondition == "EQUAL" ? " selected='selected'" : "") + '> = </option>' +
							'<option value="NOT_EQUAL"' + (filterCondition == "NOT_EQUAL" ? " selected='selected'" : "") + '> <> </option>' +
							'<option value="GATHER_THAN"' + (filterCondition == "GATHER_THAN" ? " selected='selected'" : "") + '> > </option>' +
							'<option value="LESS_THAN"' + (filterCondition == "LESS_THAN" ? " selected='selected'" : "") + '> < </option>' +
							'<option value="LIKE"' + (filterCondition == "LIKE" ? " selected='selected'" : "") + '> LIKE </option>' +
							'<option value="NOT_LIKE"' + (filterCondition == "NOT_LIKE" ? " selected='selected'" : "") + '> NOT LIKE </option>' +
						'</select>' +
					'</div>' +
					'<div class="col-md-3">' +
                        '<input name="value" value="'+filterValue+'" class="form-control input-sm" placeholder="Value" type="text">' +
					'</div>' +
					'<div class="col-md-2">' +
                        '<select name="operator" class="form-control input-sm">' +
							'<option value="AND"' + (operator == "AND" ? " selected='selected'" : "") + '> AND </option>' +
							'<option value="OR"' + (operator == "OR" ? " selected='selected'" : "") + '> OR </option>' +
						'</select>' +
					'</div>' +
					
					'<div class="col-md-2">' +
					(counter > 1 ? " <a class='plus-icon01 rd' onclick='removeElementFilterRepeateContent(this)'><i class='fa fa-minus-circle' aria-hidden='true'></i></a>" : "") + 
					'<a onclick="addElementFilterRepeateContent(this)" class=""><i class="fa fa-plus-circle"></i></a>' +
					'</div>' +
					
                '</div>';

	return row;

}
	
// Script for modelElementConfiguration [end]

// Script for model element filter [start]

function addElementFilterRepeateContent (actionButton) {

	var cloneHtml = $(actionButton).parent().parent().clone();

	if ( $(actionButton).parent().children().find('[class=\"fa fa-minus-circle\"]').length == 0 ) {
        cloneHtml.children().find('[class=\"fa fa-plus-circle\"]').parent().before('<a class="plus-icon01 rd" onclick="removeElementFilterRepeateContent(this)"><i class="fa fa-minus-circle" aria-hidden="true"></i></a>');
    }
    
    $(actionButton).parent().parent().after(cloneHtml);

}

function removeElementFilterRepeateContent (actionButton) {
	$(actionButton).parent().parent().remove();
}

// Script for model element filter [end]

// Script for model filter [start]

function addModelFilterRepeateContent (actionButton) {

	var cloneHtml = $(actionButton).parent().parent().clone();

	if ( $(actionButton).parent().children().find('[class=\"fa fa-minus-circle\"]').length == 0 ) {
        cloneHtml.children().find('[class=\"fa fa-plus-circle\"]').parent().before('<a class="plus-icon01 rd" onclick="removeModelFilterRepeateContent(this)"><i class="fa fa-minus-circle" aria-hidden="true"></i></a>');
    }
    
    $(actionButton).parent().parent().after(cloneHtml);

}

function removeModelFilterRepeateContent (actionButton) {
	$(actionButton).parent().parent().remove();
}

// Script for model filter [end]

// Script for model notification [start]

function addModelNotificationRepeateContent (actionButton) {

	var cloneHtml = $(actionButton).parent().parent().clone();

	if ( $(actionButton).parent().children().find('[class=\"fa fa-minus-circle\"]').length == 0 ) {
        cloneHtml.children().find('[class=\"fa fa-plus-circle\"]').parent().before('<a class="plus-icon01 rd" onclick="removeModelNotificationRepeateContent(this)"><i class="fa fa-minus-circle" aria-hidden="true"></i></a>');
    }
    
    $(actionButton).parent().parent().after(cloneHtml);

}

function removeModelNotificationRepeateContent (actionButton) {
	$(actionButton).parent().parent().remove();
}

// Script for model notification [end]

</script>		

<style>

a.plus-icon01 {
    right: 25px;
}

.form-group a {
	color: #03ab37;    padding: 5px;
}

</style>