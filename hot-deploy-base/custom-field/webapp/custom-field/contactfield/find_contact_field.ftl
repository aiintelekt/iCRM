<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<div class="row">
	<div id="main" role="main" class="pd-btm-title-bar">
	<#assign helpUrl = Static["org.groupfio.common.portal.util.DataHelper"].getHelpUrl(delegator, request.getRequestURI()) />  

	
	<div class="col-md-12 col-lg-12 col-sm-12 dash-panel ">
	<@sectionFrameHeader title="${uiLabelMap.Find} ${uiLabelMap.ContactField}" />
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
   						<form action="findContactField" method="post" id="searchForm" name="searchForm" data-toggle="validator">
							<div class="margin-adj-accordian">   
			      				<div class="row">
							      	<div class="col-lg-4 col-md-4 col-sm-12">
							         	<@dropdownCell 
											id="groupId"
											options=groupList
											value=customField.groupId!
											allowEmpty=true
											placeholder = uiLabelMap.customGroup
											/>
							         </div>
							         <div class="col-lg-4 col-md-4 col-sm-12">
							         	<@inputCell 
											id="customFieldName"
											placeholder=uiLabelMap.customFieldName
											value=customField.customFieldName!
											/>
							         </div>
							         <div class="col-lg-4 col-md-4 col-sm-12">
							         	<@dropdownCell 
											id="customFieldType"
											options=fieldTypeList
											value=customField.customFieldType!
											allowEmpty=true
											placeholder = uiLabelMap.customFieldType
											/>
							         </div>
							         <div class="col-lg-4 col-md-4 col-sm-12">
							         	<@dropdownCell 
											id="customFieldFormat"
											options=fieldFormatList
											value=customField.customFieldFormat!
											allowEmpty=true
											placeholder = uiLabelMap.customFieldFormat
											/>	
							         </div>
							         <div class="col-lg-4 col-md-4 col-sm-12">
							         	<@dropdownCell 
											id="customFieldLength"
											options=fieldLengthList
											value=customField.customFieldLength!
											allowEmpty=true
											placeholder = uiLabelMap.fieldLength
											/>
							         </div>
							         <div class="col-lg-4 col-md-4 col-sm-12">
							         	<@dropdownCell 
											id="hide"
											options=yesNoOptions
											value=customField.hide!
											allowEmpty=true
											placeholder = uiLabelMap.hide
											/>	
							         </div>
			     					 <div class="col-lg-12 col-md-12 col-sm-12">
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
			<@pageSectionHeader title="${uiLabelMap.List} ${uiLabelMap.ContactField}" />
			<div class="clearfix"></div>
			<div class="table-responsive">				
				<div id="myGrid" style="width: 100%;" class="ag-theme-balham"></div>
				<script type="text/javascript" src="/cf-resource/js/ag-grid/contactfield/contactField.js"></script>   			
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