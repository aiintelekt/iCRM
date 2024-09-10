<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
<#include "component://dyna-screen/webapp/dyna-screen/lib/picker_macro.ftl"/>

<link href="/dyna-screen-resource/css/form_widget.css" rel="stylesheet">
<script type="text/javascript" src="/dyna-screen-resource/js/jquery.steps.min.js"></script>


<style>

</style>

<div class="row">
<div id="main" role="main" class="pd-btm-title-bar">
	
  	<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
	
	<@sectionFrameHeader title="Create Dyna Screen" />
  	
  	<div class="wrapper">
		
    	<div id="wizard">
    	
    		<!-- SECTION 1 -->
            <h4></h4>
            <section>	
            	
            	<div id="select-screen-template" style="width: 100%;" class="ag-theme-balham"></div>
  				<script type="text/javascript" src="/dyna-screen-resource/js/ag-grid/create-widget/select_screen_template_grid.js"></script>
                
            </section>
            
			<!-- SECTION 2 -->
            <h4></h4>
            <section>
            
            <form id="mainFrom" action ="<@ofbizUrl>dynaScreenStep2CreateAction</@ofbizUrl>" method="post">
	
				<input type="hidden" name="dynaConfigId" id="dynaConfigId" value=""/>		 
				
				<div id="dyna-screen-fields"></div>
						 		 		 
				<div class="col-md-12 col-lg-12 col-sm-12 ">
			        
			         <@dynaScreen 
						instanceId="CREATE_DYNA_SCREEN"
						modeOfAction="CREATE"
						/>
			       <#-- <div class="row padding-r">
			        <div class="col-md-6 col-sm-6 form-horizontal">
			        
			        <@inputRow 
						id="instanceId"
						label=uiLabelMap.instanceId
						placeholder=uiLabelMap.instanceId
						required=false
						/>  
			              
			        <@dropdownCell 
						id="componentMountPoint"
						label=uiLabelMap.module
						options=componentList
						required=false
						value=inputContext.componentMountPoint
						allowEmpty=true
						/>	
						
					<@inputRow 
						id="screenDisplayName"
						label=uiLabelMap.displayName
						placeholder=uiLabelMap.displayName
						value=inputContext.screenDisplayName!
						required=true
						/>	
						
					<@dropdownCell 
						id="layoutType"
						label=uiLabelMap.layoutType
						options=layoutTypeList
						required=true
						value=inputContext.layoutType
						allowEmpty=true
						/>
						
					<@dropdownCell 
						id="securityGroupId"
						label=uiLabelMap.securityGroup
						options=securityGroupList
						required=false
						value=inputContext.securityGroupId
						allowEmpty=true
						/>	
						
					<@inputRow 
						id="defaultMessage"
						label=uiLabelMap.defaultMessage
						placeholder=uiLabelMap.defaultMessage
						required=false
						value=inputContext.defaultMessage
						/> 	
						
					<@dropdownCell 
						id="labelColSize"
						label=uiLabelMap.labelColSize
						options=colSizeList
						required=false
						allowEmpty=true
						value=inputContext.labelColSize
						/>
						
					<@dropdownCell 
						id="inputColSize"
						label=uiLabelMap.inputColSize
						options=colSizeList
						required=false
						allowEmpty=true
						value=inputContext.inputColSize
						/>				
						
					</div>
					
					<div class="col-md-6 col-sm-6 form-horizontal">
					
					<@inputDate
				        id="fromDate"
				        label=uiLabelMap.fromDate
				        type="date"
				        value=inputContext.fromDate
				        placeholder=uiLabelMap.fromDate
				        />
				        
				  	<@inputDate
				        id="thruDate"
				        label=uiLabelMap.thruDate
				        type="date"
				        value=inputContext.thruDate
				        placeholder=uiLabelMap.thruDate
				        />   
				        
				 	<@dropdownCell 
						id="isPrimary"
						label=uiLabelMap.isPrimary
						options=yesNoOptions
						required=false
						allowEmpty=true
						/>
						
					<@dropdownCell 
						id="isDisabledDyna"
						label=uiLabelMap.isDisabled
						options=yesNoOptions
						required=false
						allowEmpty=true
						value=inputContext.isDisabled
						/>	
						
					<@dropdownCell 
						id="isFullscreen"
						label=uiLabelMap.isFullscreen
						options=yesNoOptions
						required=false
						allowEmpty=true
						value=inputContext.isFullscreen
						/>
					
					</div>
					</div>	-->	
					             
				</div>
				
			    <#--        
			   	<div class="form-group offset-2">
			    	<div class="text-left ml-3">
			        	
					<@formButton
					btn1type="submit"
					btn1label="${uiLabelMap.Save}"
					btn1onclick="return formSubmission();"
					btn2=true
					btn2type="reset"
					btn2label="${uiLabelMap.Clear}"
					/>
			         	
			    	</div>
			  	</div> -->
			          
			</form>
			
			<div class="row">
			  	<div class="col-lg-12 col-md-12 col-sm-12">
				
			  	<div class="page-header border-b pt-2">
			  		
			  		<div class="float-left">
			        <@headerH2 title="${uiLabelMap.listOfFields!}" class=""/>
			        <@dropdownCell 
					id="serviceName"
					options=serviceNameList
					placeholder=uiLabelMap.serviceName
					required=false
					allowEmpty=true
					style="width: 500px"
					/>
										
			        </div>
			        
			        <div class="float-right">
			        
			        <span id="add-screen-field-btn" title="Create" class="btn btn-primary btn-xs ml-2 " ><i class="fa fa-plus" aria-hidden="true"></i> Add </span>
			        <span id="remove-screen-field-btn" title="Create" class="btn btn-primary btn-xs ml-2 " ><i class="fa fa-times" aria-hidden="true"></i> Remove </span>
					
					<#if security.hasPermission("DYNA_SCN_LBL_MANG", userLogin)>
					<a target="_blank" href="/webtools/control/SearchLabels?externalLoginKey=${requestAttributes.externalLoginKey}" class="btn btn-primary btn-xs ml-2"><i class="fa fa-cogs" aria-hidden="true"></i> Label Manager</a>
					</#if>
					
			        </div>
			        <div class="clearfix"></div>
			    </div>  
			    	
			  	<div id="dyna-field-grid" style="width: 100%;" class="ag-theme-balham"></div>
			  	<script type="text/javascript" src="/dyna-screen-resource/js/ag-grid/create-widget/dyna_screen_step2.js"></script>
			           
			  	</div>
			</div>
            	
            </section>

    	</div>
        
	</div>
  	
           
  	</div>
  	
</div>
</div>

<@labelPicker 
	instanceId="uiLabelPicker"
	isOnScreen=false
	/>

<script>

jQuery(document).ready(function() {

$(function(){

	$("#wizard").steps({
        headerTag: "h4",
        bodyTag: "section",
        transitionEffect: "fade",
        enableAllSteps: true,
        transitionEffectSpeed: 500,
        onStepChanging: function (event, currentIndex, newIndex) { 
        	
            if ( newIndex === 1 ) {
                
                var selectedData = gridOptionsSelectScreenTemplate.api.getSelectedRows();
                if (selectedData.length == 0) {
			    	showAlert ("error", "Select Template");
			    	return false;
			    } else {
			    	$('.steps ul').addClass('step-2');
			    	selectedData = selectedData[0];
			    	$("#dynaConfigId").val(selectedData.dynaConfigId);
                	loadScreenFieldGrid();
                	
			    }
				                
            } else {
                $('.steps ul').removeClass('step-2');
            }
            return true; 
        },
        labels: {
            finish: "Create",
            next: "Next",
            previous: "Previous"
        }
    });
		    
    // Custom Steps Jquery Steps
    $('.wizard > .steps li a').click(function(){
    	$(this).parent().addClass('checked');
		$(this).parent().prevAll().addClass('checked');
		$(this).parent().nextAll().removeClass('checked');
    });
	    
    // Custom Button Jquery Steps
    $('.forward').click(function(){
    	$("#wizard").steps('next');
    });
    
    $('.backward').click(function(){
        $("#wizard").steps('previous');
    });
    
})

});

</script>
