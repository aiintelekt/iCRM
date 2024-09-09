<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<div class="row">
	<div id="main" role="main" class="pd-btm-title-bar">
	<#assign helpUrl = Static["org.groupfio.common.portal.util.DataHelper"].getHelpUrl(delegator, request.getRequestURI()) />  

	
	<div class="col-md-12 col-lg-12 col-sm-12 dash-panel">
	<@sectionFrameHeader title="${uiLabelMap.Find} ${uiLabelMap.ContactFieldGroup}" />
		<div class="panel-group" id="accordionMenu" role="tablist" aria-multiselectable="true">
		<div class="panel panel-default">
			<div class="panel-heading" role="tab" id="headingTwo">
				<h4 class="panel-title">
					<a role="button" data-toggle="collapse" data-parent="#accordionMenu"
						href="#accordionDynaBase" aria-expanded="false"
						aria-controls="collapseOne"> ${uiLabelMap.MainFilter} </a>
				</h4>
			</div>
                <div>
                	<div>	
                		<div id="accordionDynaBase" class="panel-collapse collapse" role="tabpanel" aria-labelledby="headingOne">			
						<form action="findContactFieldGroup" method="post" id="searchForm" name="searchForm" data-toggle="validator">
							<div class="margin-adj-accordian">   
			      				<div class="row">
							         <div class="col-lg-3 col-md-3 col-sm-12">
							         	<@inputCell  
							                id="groupId"
							                placeholder =uiLabelMap.groupId 
							                value="${customFieldGroup.groupId?if_exists}"
							                />
							         </div>
							         <div class="col-lg-3 col-md-3 col-sm-4">            
							         	<@inputCell    
							                id="groupName"
							                placeholder =uiLabelMap.groupName
							                value="${customFieldGroup.groupName?if_exists}"
							                /> 
							         </div>
							         <div class="col-lg-3 col-md-3 col-sm-12">            
							         	<@dropdownCell
											id="hide"
											options=yesNoOptions
											value="${customFieldGroup.hide?if_exists}"
											allowEmpty=true
											placeholder = uiLabelMap.hide
											/>
							         </div>
			     					 <div class="col-lg-3 col-md-3 col-sm-12">
			     					 	<div class="text-right">
		         							<@button id="doSearch" label="Search"/>
	         							</div>
			         				 </div>
		         				 </div>
		      				</div>  
	      				</form>	
      				</div>
      				</div>
      				</div>
   				</div>
   			</div>
			<div class="clearfix"> </div>
			</div>
			<div class="list-seg-aggrid" style="width:100%" id="list-seg-code">
 			<div  class="col-lg-12 col-md-12 col-sm-12 dash-panel">
			<@pageSectionHeader title="${uiLabelMap.List} ${uiLabelMap.ContactFieldGroup}" />
			<div class="clearfix"></div>
			<div class="">				
				<div id="myGrid" style="width: 100%;" class="ag-theme-balham"></div>
				<script type="text/javascript" src="/cf-resource/js/ag-grid/contactfield/contactFieldGroup.js"></script>   			
			</div>
		</div>
		</div>
		<div class="clearfix"></div>
	</div><#-- End main-->
</div><#-- End row-->
<script>
$("#doSearch").click(function(event) {
	event.preventDefault(); 
	loadAgGrid();
});
</script>
