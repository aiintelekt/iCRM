<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>

<div class="row">
	<div id="main" role="main" class="pd-btm-title-bar">

	<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
	
	<#assign extra='<a href="createDynaScreen" class="btn btn-xs btn-primary"><i class="fa fa-edit" aria-hidden="true"></i> Create</a>' />
	<div><@sectionFrameHeader title="Find Screen Configurations" extra=extra?if_exists/></div>
		
	<div class="panel-group" id="accordionMenu" role="tablist" aria-multiselectable="true">
		<div class="panel panel-default">
			<div class="panel-heading" role="tab" id="headingTwo">
				<h4 class="panel-title">
					<a role="button" data-toggle="collapse" data-parent="#accordionMenu"
						href="#accordionDynaBase" aria-expanded="true"
						aria-controls="collapseOne"> ${uiLabelMap.MainFilter} </a>
				</h4>
			</div>	
			
			<div id="accordionDynaBase" class="panel-collapse collapse show" role="tabpanel" aria-labelledby="headingOne">
				<form method="post" id="find-dyna-screen-form" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
				
				<input type="hidden" name="dynaConfigIds">
				<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
				
				<div class="panel-body">
				
					<div class="row">
					<div class="col-lg-12 col-md-12 col-sm-12">
						 <@dynaScreen 
							instanceId="FIND_DYNA_SCREEN"
							modeOfAction="CREATE"
							/>
						<#-- <div class="row">
						
							<div class="col-md-2 col-sm-2">
					         	<@inputRow 
								id="dynaConfigId"
								name="dynaConfigId"
								placeholder=uiLabelMap.dynaConfigId
								inputColSize="col-sm-12"
								required=false
								/> 	
					         </div>
					         <div class="col-md-2 col-sm-2">
					         	<@inputRow 
								id="screenDisplayName"
								name="screenDisplayName"
								placeholder=uiLabelMap.screenDisplayName
								inputColSize="col-sm-12"
								required=false
								/> 	
					         </div>
    
					    	<div class="col-md-2 col-sm-2">
					         	<@dropdownCell 
								id="componentMountPoint"
								options=componentList
								placeholder=uiLabelMap.module
								required=false
								allowEmpty=true
								/>	
					         </div>
					         <div class="col-md-2 col-sm-2">
					         	<@dropdownCell 
								id="layoutType"
								options=layoutTypeList
								placeholder=uiLabelMap.layoutType
								required=false
								allowEmpty=true
								/>
					         </div>
					         <div class="col-md-2 col-sm-2">
					         	<@dropdownCell 
								id="isPrimary"
								options=yesNoOptions
								placeholder=uiLabelMap.isPrimary
								required=false
								allowEmpty=true
								/>	
					         </div>
					                
						</div>-->
												
					</div>
					</div>
											
					<div class="text-right pd-cbx-lbls pad-10" style="padding-top: 0px;">
				     	<@button
				        id="find-dyna-screen"
				        label="${uiLabelMap.Find}"
				        />	
				     	<@reset
						label="${uiLabelMap.Reset}"/>
		            </div>
		      		
				</div>	
				</form>
			</div>	
		</div>	
	</div>	
		
	</div>
	</div>
</div>

<script>     
$(document).ready(function() {

$("#find-dyna-screen").click(function(event) {
    event.preventDefault(); 
    getDynaScreenRowData();
});
	
});
</script>