		<#--PREPROCESS LOG IS STATIC DATA BECAUSE HAS NO SHORTCUT GROUP, ID ETC -->
		<#assign shortcutGroupReq = request.getParameter("sessionTab")?if_exists/>	
		<#if shortcutGroupReq?has_content>
			${session.setAttribute("sessionTab", "${shortcutGroupReq?if_exists}")}
		<#else>
			
			<#assign shortcutGroupReq = session.getAttribute("sessionTab")?if_exists />
		</#if>
			
			
			<link href="/metronic/css/layout.css" rel="stylesheet" type="text/css"/>
				<#--tab is not consider because of etl has no tab now implementation will be later
			<@import location="component://fio-dashboards/webapp/fio-dashboards/lib/portalContentMacros.ftl" />-->
				<body>
				<#assign group_Id=groupId?if_exists>
				
				<div class="panel-group" id="accordions">
					 <#if shortcutGroupReq?if_exists=="Logs">
					 <fieldset>
					 <legend>${uiLabelMap.preProcessorLogs}</legend>
							 <div class="panel panel-default" style='    margin-bottom: 1px;box-shadow: none;'>
						      <div class="panel-heading" style="padding: 7px 15px;">
						        <h4 class="panel-title">
						          <a data-toggle="collapse" data-parent="#accordions" href="#pre_defined_logs">${uiLabelMap.preProcessorLogs}</a>
						        </h4>
						      </div><!--end of panel-heading-->
						      <div id="pre_defined_logs" class="panel-collapse collapse">
		       							 <div class="panel-body" style="padding:0px;border-top-color: transparent;">
			                            <ul class='list-group shortcut_group'>
									      <li class="list-group-item" style="" id="pre_def_id">
						                                    <a href="etlLogs" class="nav-link">
						                                        <i class="icon-bar-chart"></i>
						                                        <span class="title">${uiLabelMap.preProcessorErrorLogs}</span>
						                                        <span class="selected"></span>
						                                    </a>
						                                   
						                                </li>
						                                 </ul>
						                           </div><!--end of panel-body-->
											    </div><!--end of collapse class-->
											  </div> <!--end of panel panel-default-->
					</fieldset>
					<div>&nbsp;&nbsp;</div>
					<#--<fieldset>
					 <legend>DYNAMIC VIEW LOG</legend>		
						</#if>
				<#assign count= 0/>
					<#assign opentapsShortcutGroup = delegator.findByAnd("OpentapsShortcutGroup", {"applicationId" : "data-migration", "tabId" : "${shortcutGroupReq?if_exists}"})/>
			        <#if opentapsShortcutGroup?has_content>
			           <#list  opentapsShortcutGroup as group>
			           <#assign count= count+1/>
							<div class="panel panel-default" style='    margin-bottom: 1px;box-shadow: none;'>
						      <div class="panel-heading" style="padding: 7px 15px;">
						        <h4 class="panel-title">
						          <a data-toggle="collapse" data-parent="#accordions" href="#collapse_${count}" ref-dms="collapse_${count}" >${uiLabelMap.get(group.uiLabel?if_exists)}</a>
						        </h4>
						      </div>--><!--end of panel-heading-->
			                 <#--<#assign OpentapsShortcut = delegator.findByAnd("OpentapsShortcut", {"groupId" : group.groupId?if_exists})/>
			                          <div id="collapse_${count}" class="panel-collapse collapse">
		       							 <div class="panel-body" style="padding:0px;border-top-color: transparent;">
			                            <ul class='list-group shortcut_group'>
			                            <#list OpentapsShortcut as Shortcut>-->
			                            
			                                <#--<li class="nav-item start active open">-->
			                               <#-- <li class="list-group-item" style="">
			                                    <a href="${Shortcut.linkUrl?if_exists}" class="nav-link " data-ref="collapse_${count}">
			                                        <i class="icon-bar-chart"></i>
			                                        <span class="title">${uiLabelMap.get(Shortcut.uiLabel?if_exists)}</span>
			                                        <span class="selected"></span>
			                                    </a>
			                                   
			                                </li>
			                              </#list>  
			                            </ul>
			                           </div>--><!--end of panel-body-->
								    <#--</div>--><!--end of collapse class-->
								  <#--</div>--> <!--end of panel panel-default-->					
							<#--</fieldset>
							 </#list>
							 
					 <fieldset>-->
					 <#--<legend>ORDER LOGS</legend>-->
							<#-- <div class="panel panel-default" style='    margin-bottom: 1px;box-shadow: none;'>
						      <div class="panel-heading" style="padding: 7px 15px;">
						        <h4 class="panel-title">
						          <a data-toggle="collapse" data-parent="#accordions" href="#order_defined_logs">ORDER LOGS</a>
						        </h4>
						      </div>--><!--end of panel-heading-->
						      <#--<div id="order_defined_logs" class="panel-collapse collapse">
		       							 <div class="panel-body" style="padding:0px;border-top-color: transparent;">
			                            <ul class='list-group shortcut_group'>
									      <li class="list-group-item" style="" id="order_def_id">
						                                    <a href="orderErrorLogs" class="nav-link">
						                                        <i class="icon-bar-chart"></i>
						                                        <span class="title">Order Error Logs</span>
						                                        <span class="selected"></span>
						                                    </a>
						                                   
						                                </li>
							                                 </ul>
							                           </div>--><!--end of panel-body-->
												    <#--</div>--><!--end of collapse class-->
												  <#--</div>--> <!--end of panel panel-default-->
						<#--</fieldset>
						<div>&nbsp;&nbsp;</div>-->
						</#if>
							<!-- END SIDEBAR MENU -->
						</div>
				<script type="text/javascript">
				
				
				
				
				function splitRequest()
				{
						var	requestUri = "${request.getRequestURI()}";
						var current_request =requestUri.split("&#47;");
						return 	current_request[3];	
				}
				
				$(document).ready(function(){
					var url_request = splitRequest();
					
					$(".shortcut_group li a").each(function(){
							var shortcutUrl = $(this).attr("href");
							
							if(shortcutUrl!="" && shortcutUrl.indexOf("?")!=-1)
							{		var tst_data = shortcutUrl.split("?");
									shortcutUrl = tst_data[0];
							}
								
								if(url_request=="etlLogs")
								{
									$("#pre_defined_logs").attr("class","panel-collapse collapse in");
									$("#pre_def_id").attr("class","list-group-item active");
								}
								if(url_request=="orderErrorLogs")
								{
									$("#order_defined_logs").attr("class","panel-collapse collapse in");
									$("#order_def_id").attr("class","list-group-item active");
								}
								if(shortcutUrl==url_request)
								{
									var expander_attribute = $(this).attr("data-ref")
									$("#"+expander_attribute).attr("class","panel-collapse collapse in");
									$("#"+expander_attribute).attr("aria-expanded","true");
									$(this).parent().attr("class","list-group-item active");
								}
					});
					
					
					
				});
				
				</script>
				
				</body>
				
				
				