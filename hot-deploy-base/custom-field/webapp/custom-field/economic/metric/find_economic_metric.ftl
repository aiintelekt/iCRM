<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<div class="row">
 <#assign helpUrl = Static["org.groupfio.common.portal.util.DataHelper"].getHelpUrl(delegator, request.getRequestURI()) />  
    <div id="main" role="main">
<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
    <@sectionFrameHeader title="${uiLabelMap.Find} ${uiLabelMap.EconomicMetric}"  />
  <div class="">
   <form id="findAttributeGroupForm" method="post" class="form-horizontal" data-toggle="validator">
      <div class="row">
		      	
		      	<div class="col-md-6 col-lg-3 col-sm-12">
		         	<@dropdownCell 
						id="groupingCode"
						options=groupingCodeList
						required=false
						value=customFieldGroup.groupingCode
						allowEmpty=true
						placeholder = uiLabelMap.groupingCode
						dataLiveSearch=true
						/>
<@dropdownCell 
						id="isCampaignUse"
						options=yesNoOptions
						required=false
						value=customFieldGroup.isCampaignUse
						allowEmpty=true
						placeholder = uiLabelMap.isCampaignUse
						/>				
		        
		
		         </div>	
		         <div class="col-md-6 col-lg-3 col-sm-12">
		         	<@dropdownCell 
						id="groupId"
						options=customFieldGroupList
						required=false
						value=customFieldGroup.groupId
						allowEmpty=true
						placeholder = uiLabelMap.economicMetric
						dataLiveSearch=true
						/>	
						
					<#-- 	
		         	<@simpleInput 
						id="groupId"
						placeholder=uiLabelMap.economicMetricId
						value=customFieldGroup.groupId
						required=false
						maxlength=250
						/>	 -->
<@dropdownCell 
						id="type"
						options=typeList
						required=false
						value=customFieldGroup.type
						allowEmpty=true
						placeholder = uiLabelMap.type
						/>
		         </div>
		         <div class="col-md-6 col-lg-3 col-sm-12">
		         	<@inputCell 
						id="groupName"
						placeholder=uiLabelMap.economicMetricName
						value=customFieldGroup.groupName
						required=false
						maxlength=255
						/>
		         </div>
		         
		         <div class="col-md-6 col-lg-3 col-sm-12">
		         	<@dropdownCell 
						id="valueCapture"
						options=valueCaptureList
						required=false
						value=customFieldGroup.valueCapture
						allowEmpty=true
						placeholder = uiLabelMap.valueCapture
						/>	
 <div class="search-btn">
         <div class="float-right">
         	<@submit  id="doSearch" label="Find"/>
         	</div>
       	</div>	
		         </div>
		         
		        
        
      </div>
   </form>
   <div class="clearfix"> </div>
</div>
</div>
</div>


<#-- 
<script>

jQuery(document).ready(function() {

	loadSegmentCodeList();
	$("#groupingCode").change(function() {
	loadSegmentCodeList()
	});

});

function loadSegmentCodeList() {
	var nonSelectContent = "<span class='nonselect'>Select ${uiLabelMap.economicMetric!}</span>";
	var groupNameOptions = '<option value="" data-content="'+nonSelectContent+'" selected="">Select ${uiLabelMap.economicMetric!}</option>';		
		
	//if ( $("#groupingCode").val() ) {
		
		$.ajax({
			      
			type: "POST",
	     	url: "getCustomFieldGroups",
	        data:  {"groupingCode": $("#groupingCode").val()},
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

-->
