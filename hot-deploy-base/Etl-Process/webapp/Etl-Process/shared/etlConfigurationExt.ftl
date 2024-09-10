<#--<#if projectId?exists && projectId!= "" && projectId!= null >-->
<style>
.caption {
    width: 100%;
}
</style>

  <div id="file_upload_view" style="">
        <#--starting of upload form process-->
        <div class="portlet light" style="  border: 0px solid #B6BFC1 !important;"> 
          <div class="">
            <div class="caption">
             <ul class="nav nav-tabs left_margin_portlet">
			  <li class="active"><a data-toggle="tab" href="#Defaults">${uiLabelMap.defaults}</a></li>
			  <li id="groupTab"><a data-toggle="tab" href="#Grouping">${uiLabelMap.grouping}</a></li>
			  
			</ul>
			
			<#--for defaults tab-->
			<div class="tab-content">
			  <div id="Defaults" class="tab-pane fade in active">
			     ${screens.render("component://Etl-Process/widget/MyHomeScreens.xml#etlConfigurationNew")}
			</div>
			<#--end of defaults tab-->
   		    
   		    <#--for grouping tab-->
   		    <div id="Grouping" class="tab-pane fade">
				 <#--${screens.render("component://Etl-Process/webapp/widget/Etl-Process/screens/myhome/MyHomeScreens.xml#etlGrouping")}-->

				<#--grouping subtabs start-->
				<ul class="nav nav-pills">
				    <li class="active"><a data-toggle="pill" href="#Groups">${uiLabelMap.groups}</a></li>
				    <li><a data-toggle="pill" href="#ETLModel">${uiLabelMap.modelGrouping}</a></li>
				    <li><a data-toggle="pill" href="#ETLProcess">${uiLabelMap.processGrouping}</a></li>
				</ul>
				  
				<div class="tab-content">
					<div id="Groups" class="tab-pane fade in active">				
					 ${screens.render("component://Etl-Process/widget/MyHomeScreens.xml#createETLGroup")}		
					</div>
					<div id="ETLModel" class="tab-pane fade">					 
					  ${screens.render("component://Etl-Process/widget/MyHomeScreens.xml#createETLModelGroup")}      
					</div>
					<div id="ETLProcess" class="tab-pane fade">					  
					  ${screens.render("component://Etl-Process/widget/MyHomeScreens.xml#createETLProcessGroup")}    
					</div>	
				
		    	</div>
				<#--grouping subtabs end-->
			</div>
            <#--end of grouping tab-->
             

            </div>
          </div>
        </div>
        <#--end of starting upload form process-->
      </div>