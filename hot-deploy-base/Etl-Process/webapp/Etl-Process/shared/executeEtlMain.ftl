<head>

												<#--for file upload js-->

												<script src="/metronic/js/bootstrap-fileinput.js" type="text/javascript"></script>

												<#--end of file upload js-->

												

												

												

												

												

												

												<#--file upload plugin-->

												<link rel="stylesheet" type="text/css" href="/metronic/css/bootstrap-fileinput.css"/>

												

												<#--end @fileupload plugin-->

						

												<#--for draggle modal-->

												 <script src="/metronic/js/ui-modals.min.js" type="text/javascript"></script>

												<#--end of draggle modal-->

												

												

											<#--for form validation process-->

											

											<#--end of formvalidation-->

												

											

												

												<#--for scroll bar check-->

												

												<#--for alert popup-->

												<script src="/metronic/js/bootstrap-confirmation.min.js" type="text/javascript"></script>

												<script src="/metronic/js/ui-confirmations.min.js" type="text/javascript"></script>

												<#--end of alert popup-->

												

												

												

												<#--notification of js-->

												<script src="/metronic/js/notify.min.js" type="text/javascript"></script>

												<script src="/metronic/js/notify.js" type="text/javascript"></script>

												<#--end of notification-->

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

														button.btn.dropdown-toggle.selectpicker.btn-default {

														    background-color: white !important;

														}

														

														

														.md-shadow-z-3, .modal {

		 													   box-shadow: 0 0px 0px rgba(0, 0, 0, 0.0), 0 0px 0px rgba(0, 0, 0, 0.0);

															}

															

															@media only screen and (max-width: 500px) {

															    body {

															        background-color: red;

															    }

															}

															

															

															.search-form .form-group {

																  float: right !important;

																  transition: all 0.35s, border-radius 0s;

																  width: 32px;

																  height: 32px;

																  background-color: #fff;

																  box-shadow: 0 1px 1px rgba(0, 0, 0, 0.075) inset;

																  border-radius: 25px;

																  border: 1px solid #ccc;

																}

																.search-form .form-group input.form-control {

																  padding-right: 20px;

																  border: 0 none;

																  background: transparent;

																  box-shadow: none;

																  display:block;

																}

																.search-form .form-group input.form-control::-webkit-input-placeholder {

																  display: none;

																}

																.search-form .form-group input.form-control:-moz-placeholder {

																  /* Firefox 18- */

																  display: none;

																}

																.search-form .form-group input.form-control::-moz-placeholder {

																  /* Firefox 19+ */

																  display: none;

																}

																.search-form .form-group input.form-control:-ms-input-placeholder {

																  display: none;

																}

																.search-form .form-group:hover,

																.search-form .form-group.hover {

																  width: 100%;

																  border-radius:25px 25px 25px 25px;

																}

																.search-form .form-group span.form-control-feedback {

																  position: absolute;

																  top: -1px;

																  right: -2px;

																  z-index: 2;

																  display: block;

																  width: 34px;

																  height: 34px;

																  line-height: 34px;

																  text-align: center;

																  color: #3596e0;

																  left: initial;

																  font-size: 14px;

																}

																													

																.error

																{

																	font-size:12;

																	color:red;

																}												

										

												</style>
												
												</head>
												<body>
												
												
												<div class="container-fluid">
												<div>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</div>
												<!--creation of form -->
												
												<div class="portlet light form-fit bordered">
													<div class="portlet-title">
					                                    <div class="caption">
					                                    
					                                    <form class="form-horizontal" role="form">
					                                    <div class="form-group">
					                                    	
														   <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">          
																<div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
																<h2> <div class="label label-default">Importing</div></h2>
																</div>
																<div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
																<h3> <div class="label label-default">#Proceeded</div></h3>
																</div>
																<div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
																<h3> <div class="label label-default">#Not Proceeded</div></h3>
																</div>
												     	 </div>
												     	 </div>
												     	 <div class="form-group">
												     	 <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">          
																<div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
																 <label class="">Product Lines</label>
																</div>
																<div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
																 <label class="">0</label>
																</div>
																<div class="col-lg-4 col-md-4 col-sm-4 col-xs-4">
																 <label class="">17</label>
																</div>
												     	 </div>
												      </div>
					                                    
					                                    </form>
					                                   <div>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</div>
					                                   
					                                        <i class="icon-social-dribbble font-green"></i>
					                                        <span class="caption-subject font-green bold uppercase">Upload a File</span>
					                                    </div>
					                                    
                               						 </div>
                               						 
                               						 
                               						 <div class="portlet-body container">
                               						 
                               						 <#--portlet body -->
                               						 	<form class="form-horizontal" role="form">
														    <div class="form-group">
														      <label class="control-label col-sm-2" for="email">File to Import</label>
														      <div class="col-sm-8">
														       
														       
														       
														        <!--for file input-->
		
							                                                  <div class="fileinput fileinput-new pull-left" data-provides="fileinput" >
							
										                                                
							
																		                
							
																							                                   <div class="input-group input-group-sm">
							
																			                                                            <div class="form-control uneditable-input input-fixed input-group-sm" data-trigger="fileinput">
							
																			                                                                <i class="fa fa-file fileinput-exists"></i>&nbsp;
							
																			                                                                <span class="fileinput-filename" name="csvFile"> </span>
							
																			                                                            </div><!--end of form-control uneditable-input input-fixed input-group-sm-->
							
																			                                                            <span class="input-group-addon btn default btn-file">
							
																			                                                                <span class="fileinput-new"> Select file </span>
							
																			                                                                <span class="fileinput-exists"> Change </span>
							
																			                                                                <input type="hidden"><input type="file" name="csv_fileName" id="csv_fileName"> </span>
							
																			                                                            <a href="javascript:;" class="input-group-addon btn red fileinput-exists" data-dismiss="fileinput"> Remove </a>
							
																			                                                            
							
																			                                                   </div><!--end of input-group input-group-sm-->
							
																			                                                      
							
																	                     <span class="error" id="file_name_error"></span> 
							
																									                               
							
																			           </div><!--end of fileinput process-->
														       
														       
														       
														       
														      </div>
														    </div>
														    <div class="form-group">
														
														      <div class="col-sm-offset-2 col-sm-8">          
																		<label style="color:red">												       
																			Pick the right excel (2003 edition) and upload
																			/ upload text file with tab delimiter
																		</label>
														      </div>
														    </div>
														    <div class="form-group">
															    <label class="control-label col-sm-2" for="email">File Format:</label>
															    <div class="col-sm-8">
																										     
															<select class="bs-select form-control btn-xs" data-live-search="true" data-size="8" id="getSelected">
													
																										                                                     								
													                                              		<option value="excel">Excel</option>
													                                              		<option value="text">Text</option>
															</select>	
		
		
															    </div>
															    
															    
															  </div>
															  
															  
															   <div class="form-group">
																    <label class="control-label col-sm-2" for="email">Excel Format Template</label>
																    <div class="col-sm-8">
																											     
																		<a href="#">download</a>
		
															    </div>
															    </div>
															    
															     <div class="form-group">
																    <label class="control-label col-sm-2" for="email">Text Format Template</label>
																    <div class="col-sm-8">
																											     
																		<a href="#">download</a>
																	</div>
															    </div>
															    
															    <div class="form-group">
																   <div class="col-sm-offset-2 col-sm-8">          
																		<button type="button" class="btn btn-default btn-sm" style="background-color:white">Upload</button>
														     	 </div>
														      </div>
														 </form><!--end of form-->
                               						 
                               						 <#--end of portlet body-->
                               						 </div>
												</div>
												  
												  </div>
												  
												</body>