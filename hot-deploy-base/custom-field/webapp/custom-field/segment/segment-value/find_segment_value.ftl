<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<div class="row">
<div id="main" role="main" class="pd-btm-title-bar">
    	<div class="col-md-12 col-lg-12 col-sm-12 dash-panel">
    <#assign helpUrl = Static["org.groupfio.common.portal.util.DataHelper"].getHelpUrl(delegator, request.getRequestURI()) />  
    <#if groupId?has_content>
    <div class="row">
    	<form method="post" class="form-horizontal" data-toggle="validator" id="findsegmentRequestForm" name="findsegmentRequestForm">
    		<@inputHidden id="groupId" value="${groupId!}"/>
    	</form>
    <#else>
   
    	<@sectionFrameHeader title="${uiLabelMap.Find} ${uiLabelMap.SegmentValue}" />
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
				   <form method="post" class="form-horizontal" data-toggle="validator" id="findsegmentRequestForm" name="findsegmentRequestForm">
				     	<div class="margin-adj-accordian">
				       <div class="row">
				      	<div class="col-md-4 col-lg-4 col-sm-12">
				      		<@inputHidden id="isUpdate" value="N"/>
				         	<@dropdownCell 
								id="groupingCode"
								options=groupingCodeList
								required=false
								value=customField.groupingCode
								allowEmpty=true
								tooltip = uiLabelMap.groupingCode				
								dataLiveSearch=true
								placeholder=uiLabelMap.groupingCode
								/>
				         </div>
				      	
				         <div class="col-md-4 col-lg-4 col-sm-12">
				         	<@dropdownCell 
								id="groupId"
								options=groupList
								required=false
								value=customField.groupId
								allowEmpty=true	
								placeholder=uiLabelMap.segmentCode		
								/>	
				         </div>
				         <div class="col-md-4 col-lg-4 col-sm-12">
				         	<@inputCell 
								id="customFieldId"
								placeholder=uiLabelMap.segmentValueId
								value=customField.customFieldId
								tooltip = uiLabelMap.segmentValueId
								required=false
								maxlength=255
								/>		
				         </div>
				         <div class="col-md-4 col-lg-4 col-sm-12">
				         	<@inputCell 
								id="customFieldName"
								placeholder=uiLabelMap.segmentValueName
								value=customField.customFieldName
								tooltip = uiLabelMap.segmentValueName
								required=false
								maxlength=255
								/>		
				         </div>
				         <div class="col-md-4 col-lg-4 col-sm-12">
				         	<@dropdownCell 
								id="isEnabled"
								options=yesNoOptions
								required=false
								value=customField.isEnabled
								allowEmpty=true		
								placeholder=uiLabelMap.enabled	
								/>	
				         </div>
				         <div class="col-12"> 
				         	<div class="float-right p-2">     
				        		<@submit id="btnFind" label="Find" />
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
   		</#if> 
   		</div>
		</div> 
   		<div class="clearfix"></div>
   		
 		
 		<div class="" style="width:100%">
		<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
	   	<@pageSectionHeader title="${uiLabelMap.List} ${uiLabelMap.SegmentValue}" />
    	<div id="findsegmentgrid" style=" width: 100%;" class="ag-theme-balham">
	        <script type="text/javascript" src="/cf-resource/js/ag-grid/contactfield/findsegment.js"></script>
     		</div>
     		</div>
     		</div>
	</div>

<script type="text/javascript">

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
	        
		});    groupId
		
		$("#groupId").html( groupNameOptions );
		
		<#if customField.groupId?has_content>
		$("#groupId").val( "${customField.groupId}" );
		</#if>
	
		$('#groupId').dropdown('refresh');
	//}
		
}
		
</script>