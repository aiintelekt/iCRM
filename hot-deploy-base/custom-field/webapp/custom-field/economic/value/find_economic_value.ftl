<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<div class="row">
    <div id="main" role="main">
    <#assign helpUrl = Static["org.groupfio.common.portal.util.DataHelper"].getHelpUrl(delegator, request.getRequestURI()) />  

	
		<div class="col-md-12 col-lg-12 col-sm-12 dash-panel">
<@sectionFrameHeader title="${uiLabelMap.Find} ${uiLabelMap.EconomicValue}" />

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
				   	<form id="searchForm" method="post" class="form-horizontal" data-toggle="validator">
				      	<div class="margin-adj-accordian">   
	      					<div class="row">    
				      			<div class="col-lg-3 col-md-6 col-sm-12">
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
								<@dropdownCell 
										id="isEnabled"
										options=yesNoOptions
										required=false
										value=customField.isEnabled
										allowEmpty=true
										placeholder = uiLabelMap.isEnabled				
										/>
						         </div>
				      	
				         		<div class="col-lg-3 col-md-6 col-sm-12">
						         	<@dropdownCell 
										id="groupId"
										options=groupList
										required=false
										value=customField.groupId
										allowEmpty=true
										tooltip = uiLabelMap.economicCode	
										placeholder = uiLabelMap.economicCode
										dataLiveSearch=true
										/>	
										
						         </div>
						         <div class="col-lg-3 col-md-6 col-sm-12">
						         	<@inputCell 
										id="customFieldId"
										placeholder=uiLabelMap.economicValueId
										value=customField.customFieldId
										tooltip = uiLabelMap.economicValueId
										required=false
										maxlength=255
										/>		
						         </div>
						         <div class="col-lg-3 col-md-6 col-sm-12">
						         	<@inputCell 
										id="customFieldName"
										placeholder=uiLabelMap.economicValueName
										value=customField.customFieldName
										required=false
										maxlength=255
										/>
									<div class="search-btn">
						         	<div class="float-right">
						         		<@submit id="doSearch" label=uiLabelMap.find/>
						         	</div>
						         </div>	
						         </div>		
						         </div>
						        
					</div>
				   	</form>
				   	</div>
					<div class="clearfix"> </div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>

<#--  
<script type="text/javascript">

jQuery(document).ready(function() {	

	loadEconomicCodeList();
	$("#groupingCode").change(function() {
		loadEconomicCodeList()
	});

});

function loadEconomicCodeList() {
	var nonSelectContent = "<span class='nonselect'>Select ${uiLabelMap.economicCode!}</span>";
	var groupNameOptions = '<option value="" data-content="'+nonSelectContent+'" selected="">Select ${uiLabelMap.economicCode!}</option>';		
		
	if ( $("#groupingCode").val() ) {
		
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
		
		<#if customField.groupId?has_content>
		$("#groupId").val( "${customField.groupId}" );
		</#if>
	
		$('#groupId').dropdown('refresh');
	}
		
}
		
</script>

-->
