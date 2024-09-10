<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<div class="row">
    <div id="main" role="main" class="pd-btm-title-bar">
		<div class="col-md-12 col-lg-12 col-sm-12 dash-panel" >
		<@sectionFrameHeader title="${uiLabelMap.Find} ${uiLabelMap.SegmentCode}"  />
		<div class="panel-group" id="accordionMenu" role="tablist" aria-multiselectable="true">
		<div class="panel panel-default">
			<div class="panel-heading" role="tab" id="headingTwo">
				<h4 class="panel-title">
					<a role="button" data-toggle="collapse" data-parent="#accordionMenu"
						href="#accordionDynaBase" aria-expanded="true"
						aria-controls="collapseOne"> ${uiLabelMap.MainFilter} </a>
				</h4>
			</div>
				<div>
   					<div>
   						<div id="accordionDynaBase" class="panel-collapse collapse show" role="tabpanel" aria-labelledby="headingOne">
   						<form method="post" class="form-horizontal" data-toggle="validator" id="findAttributeGroupForm" name="findAttributeGroupForm">
      					<div class="margin-adj-accordian">   
	      					<div class="row" >      	
	      						<div class="col-md-4 col-lg-4 col-sm-12">
						         	<@dropdownCell 
										id="groupingCode"
										options=groupingCodeList
										required=false
										value=customFieldGroup.groupingCode
										allowEmpty=true				
										dataLiveSearch=true
										placeholder=uiLabelMap.groupingCode
										/>
						         </div>
						         <div class="col-md-4 col-lg-4 col-sm-12">
						         	<@dropdownCell 
										id="groupId"
										options=groupList
										required=false
										value=customFieldGroup.groupId
										allowEmpty=true
										placeholder=uiLabelMap.segmentCode
										/>	
						         	<#-- <@simpleInput 
										id="groupId"
										placeholder=uiLabelMap.segmentCodeId
										value=customFieldGroup.groupId
										required=false
										maxlength=20
										/> -->	
						         </div>
						         <div class="col-md-4 col-lg-4 col-sm-12">
						         	<@inputCell 
										id="groupName"
										placeholder=uiLabelMap.segmentCodeName
										value=customFieldGroup.groupName
										required=false
										maxlength=255
										/>
						         </div>
						         
						         <div class="col-md-4 col-lg-4 col-sm-12">
						         	<@dropdownCell 
										id="valueCapture"
										options=valueCaptureList
										required=false
										value=customFieldGroup.valueCapture
										allowEmpty=true
										placeholder=uiLabelMap.valueCapture
										
										/>		
						         </div>
						         
						         <div class="col-md-4 col-lg-4 col-sm-12">
						         	<@dropdownCell 
										id="isCampaignUse"
										options=yesNoOptions
										required=false
										value=customFieldGroup.isCampaignUse
										allowEmpty=true
										placeholder=uiLabelMap.usedForCampaign
										
										/>				
						         </div>
						         
						         <div class="col-md-4 col-lg-4 col-sm-12">
						         	<@dropdownCell 
										id="type"
										options=typeList
										required=false
										value=customFieldGroup.type
										allowEmpty=true
										placeholder=uiLabelMap.type
										/>		
						         </div> 
						         <div class="col-12">
						         	<div class="float-right p-2">       
						        	<@submit id="findSegmentCode" label="Find"/>
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
			</div>
			<div class="clearfix"></div>
			
		</div>
		<div class="list-seg-aggrid" style="width:100%" id="list-seg-code">
  <div  class="col-lg-12 col-md-12 col-sm-12 dash-panel">
				<@pageSectionHeader title="${uiLabelMap.List} ${uiLabelMap.SegmentCode}" />
		    	<div id="findAttributeGroupgrid" style=" width: 100%;" class="ag-theme-balham">
		        	<script type="text/javascript" src="/cf-resource/js/findSegmentCode.js"></script>
		    	</div>
			 </div>
			 </div>
	</div>
</div>
 
<script>
jQuery(document).ready(function() {

	//loadSegmentCodeList();
	$("#groupingCode").change(function() {
		loadSegmentCodeList()
	});

});

function loadSegmentCodeList() {
	var nonSelectContent = "<span class='nonselect'>Select ${uiLabelMap.segmentCode!}</span>";
	var groupNameOptions = '<option value="" data-content="'+nonSelectContent+'" selected="">Select ${uiLabelMap.segmentCode!}</option>';		
		
	//if ( $("#groupingCode").val() ) {
		
		$.ajax({
			      
			type: "POST",
	     	url: "getCustomFieldGroups",
	        data:  {"groupingCode": $("#groupingCode").val(), "groupType": "SEGMENTATION"},
	        async: false,
	        success: function (data) {   
	            
	            if (data.code == 200) {
	            
	            	for (var i = 0; i < data.groups.length; i++) {
	            		var group = data.groups[i];
	            		groupNameOptions += '<option value="'+group.groupId+'">'+group.groupName+'</option>';
	            	}
	            	
	            }
				    	
	        }
	        
		});    
		
		$("#groupId").html( groupNameOptions );
		
		<#if customFieldGroup.groupId?has_content>
		$("#groupId").val( "${customFieldGroup.groupId}" );
		</#if>
	
		$('#groupId').dropdown('refresh');
	//}
		
}

</script>
