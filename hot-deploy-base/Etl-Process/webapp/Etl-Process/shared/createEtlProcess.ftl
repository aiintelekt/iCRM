<head>
									<#--for file upload js-->
									<script src="/metronic/js/bootstrap-fileinput.js" type="text/javascript"></script>
									<#--end of file upload js-->
									
									
									
									<#--file upload plugin-->
									<link rel="stylesheet" type="text/css" href="/metronic/css/bootstrap-fileinput.css"/>
									
									<#--end @fileupload plugin-->
									
									
									
									<#--for scroll bar check-->
									<style>
									
										::-webkit-scrollbar {
											    width: 12px;
											}
											 
											::-webkit-scrollbar-track {
											    -webkit-box-shadow: inset 0 0 6px rgba(0,0,0,0.3); 
											    border-radius: 10px;
											}
											 
											::-webkit-scrollbar-thumb {
											    border-radius: 10px;
											    -webkit-box-shadow: inset 0 0 6px rgba(0,0,0,0.5); 
											}
									</style>
									
									<#--end of scroll bar check-->
									
									
									<#--only for mannual script-->
									<script>
									$(document).ready(function(){
										
										//for file Uploading
										$("#fileProgress").click(function(){
												
											var fileName = $("#csv_fileName").val();
											var listName = $("#csvListName").val();
											if(listName=="")
											{
												alert("please enter list Name");
												return false;
											}
											if(fileName!="" )
											{
												var filenameWithext = fileName.split(/(\\|\/)/g).pop();
												$("#csvFile").val(filenameWithext);
												
												$("#listName").val(listName);
												
												$("#csv-upload").submit();
											}else
											{
												alert("Please select valid file");
											}
										
										});
									});
									
									
									//for filter search box process
									function filter(element) {
										   var value = $(element).val();
										   $("#csvSortable li").each(function() {
										     if ($(this).text().search(new RegExp(value, "i")) > -1) {
										       $(this).show();
										     } else {
										       $(this).hide();
										     }
										   });
										 }
										 
										 
										 
									</script>
									<#--end of mannual script-->	
										
							</head>

<body>
<#--portlet 1-->
								<div class="col-lg-4 col-md-4 col-sm-4 col-xs-12">
												<div class="column">
													<div class="portlet light" style="min-height:auto !important;border: 1px solid #B6BFC1 !important">
						                                <div class="portlet-title">
						                                    <div class="caption font-red-sunglo">
						                                        
						                                    </div>
						                                    <div class="actions">
						                                        <input type="text" placeholder="search..." class="form-control input-circle" id="csvtxtList" onkeyup="filter(this)">
						                                    </div>
						                                </div>
						                                <div class="portlet-body">
						                                    
							                                    
							                                    
								                                    <div class="scroller" style="max-height: 400px; overflow: scroll; width: auto;" data-always-visible="1" data-rail-visible="1" data-rail-color="red" data-handle-color="green" data-initialized="1" id="csvSortable">
							                                        	
							                                        <#--for display all the data from EtlMappingElements table-->
							                                        <#if etlMappingElements?has_content>
							                                        <ul class="list-group">
								                                        <#list etlMappingElements as etlElement>
								                                        
		                                        							<li class="list-group-item" value="${etlElement.fieldName?if_exists}" id="columnSortable"> 
				                                        							<label class="radio-inline"><input type="radio" name="optradio" value="${etlElement.fieldName?if_exists}">&nbsp;${etlElement.fieldName?if_exists}</label>
				  																	
																					
		                                        							 </li>
		                                        
		                                    							
								                                        
								                                        </#list>
								                                     </ul>
							                                        </#if>
							                                        <#--end @EtlMappingElements-->
							                                        
							                                    	</div>
								                                    
							                                
						                                </div>
						                            </div>
							                          
							                          <#--file uploading process-->
							                          <form name="csv-upload" id="csv-upload" action="<@ofbizUrl>uploadCSVFile</@ofbizUrl>" method="POST">
								                           <input type="hidden" name="csvFile" id="csvFile">
								                           <input type="hidden" name="listName" id="listName">
							                          </form>
							                          <#--end of file uploading process-->
							                          
							                          
							                          
						                                     <div class="fileinput fileinput-new pull-left" data-provides="fileinput" >
						                                     <input type="text" class="form-control" placeholder="Please select List Name...!" id="csvListName">
						                                     <div>&nbsp;&nbsp;&nbsp;</div>
							                                                        <div class="input-group input-large">
							                                                            <div class="form-control uneditable-input input-fixed input-medium" data-trigger="fileinput">
							                                                                <i class="fa fa-file fileinput-exists"></i>&nbsp;
							                                                                <span class="fileinput-filename" name="csvFile"> </span>
							                                                            </div>
							                                                            <span class="input-group-addon btn default btn-file">
							                                                                <span class="fileinput-new"> Select file </span>
							                                                                <span class="fileinput-exists"> Change </span>
							                                                                <input type="hidden"><input type="file" name="csv_fileName" id="csv_fileName"> </span>
							                                                            <a href="javascript:;" class="input-group-addon btn red fileinput-exists" data-dismiss="fileinput"> Remove </a>
							                                                        </div>
							                                                         <div>&nbsp;&nbsp;&nbsp;</div>
													                          	    	<input type="submit" class="btn btn-info" value="ADD" id="fileProgress">
													                                 </div>
							                                  </div>
							                               
							                                
						                              
						                            
								                    </div><!--end of col-md-4 etc-->
								                     
								                    
								<#--end of portlet body-->
								
								
								
								
								<#--portlet 2-->
								
								<div class="col-lg-4 col-md-4 col-sm-4 col-xs-12">
								                       <div class="portlet light bordered" style="border: 1px solid #B6BFC1 !important">
							                                <div class="portlet-title">
							                                    <div class="caption pull-right">
							                                        
							                                        
							                                        
							                                        <select class="form-control">
							                                        			<#if eltTableNameList?has_content>
							                                        				<#list eltTableNameList as eltTableName>
						                                                        		<option value="${eltTableName.tableName?if_exists}">${eltTableName.tableName?if_exists}</option>
						                                                        	</#list>
						                                                        </#if>
						                                            </select>
							                                        
							                                        
							                                       
							                                    </div>
							                                    <div class="actions">
							                                        <div class="btn-group btn-group-devided" data-toggle="buttons">
							                                            
							                                        </div>
							                                    </div>
							                                </div>
							                                <div class="portlet-body">
							                                      <div class="scroller" style="max-height: 400px; overflow: scroll; width: auto;" data-always-visible="1" data-rail-visible="1" data-rail-color="red" data-handle-color="green" data-initialized="1">
								                                    
							                                    </div>
							                                </div>
							                            </div>
								                    </div>
								<#--end of portlet 2-->
								
								
								
								<#--portlet 3-->
								
								<div class="col-lg-4 col-md-4 col-sm-4 col-xs-12">
								        <div style="min-height: 518px;">
								                       <div class="portlet light bordered" style="border: 1px solid #B6BFC1 !important">
							                                <div class="portlet-title">
							                                    <div class="caption">
							                                      
							                                        <span class="caption-subject font-green bold uppercase"></span>
							                                        <span class="caption-helper"></span>
							                                    </div>
							                                    <div class="actions">
							                                        
							                                    </div>
							                                    
							                                </div>
							                                <div class="portlet-body ">
							                                  
							                                   <div class="scroller" style="max-height: 400px; overflow: scroll; width: auto;" data-always-visible="1" data-rail-visible="1" data-rail-color="red" data-handle-color="green" data-initialized="1">
								                                    
							                                    </div>
							                                  
							                                   
							                                </div>
							                            </div>
							                           </div>
								                    
								<#--end of portlet 2-->
								
</body>								
								
