<#include "component://homeapps/webapp/homeapps/lib/ofbizFormMacros.ftl"/>

<div class="row">
	<div class="col-md-12">
		<div class="portlet light">
			<div class="portlet-title">
				<div class="caption font-green-haze">
					<i class="fa fa-building font-green-haze"></i>
					<span class="caption-subject bold uppercase"> ${uiLabelMap.FindErrorlogs!}</span>
				</div>
				<div class="tools">
					<a href="javascript:;" class="collapse">
					</a>
					<#--<a href="#portlet-config" data-toggle="modal" class="config">
					</a>
					<a href="javascript:;" class="reload">
					</a>-->
					<a href="javascript:;" class="fullscreen">
					</a>
					<#--<a href="javascript:;" class="remove">
					</a>-->
				</div>
			</div>
			<div class="portlet-body form">
				
				<form role="form" class="form-horizontal" action="<@ofbizUrl>findErrorLogs</@ofbizUrl>" method="post">
					
					<div class="form-body">
						
						<@dropdownInput 
							id="modelType"
							label=uiLabelMap.EtlModelType
							options=modelTypes
							value=errorLogFilter.modelType
							allowEmpty=true
							tooltip = uiLabelMap.EtlModelType
							/>
					
						<@dropdownInput 
							id="modelId"
							label=uiLabelMap.EtlModelName
							options=modelList
							value=errorLogFilter.modelId
							allowEmpty=true
							tooltip = uiLabelMap.EtlModelName
							/>
						 
					</div>
					
					<@fromCommonAction iconClass="fa fa-check" showCancelBtn=false submitLabel=uiLabelMap.CommonFind/>
					
				</form>
				
			</div>
		</div>
	</div>
	
</div>

<script type="text/javascript">
	
 	$( "#modelType" ).change(function() {
  		
  		$.ajax({
			      
			type: "POST",
	     	url: "getEtlModels",
	        data:  {"modelType": $("#modelType").val()},
	        success: function (returnedData) {   
	            
	            $('#modelId').html("");
	            
	            if (returnedData.code == 200 && returnedData.modelList.length !== 0) {
	            	
	            	var row = "<option value=''>Please Select</option>";
	            	
	            	for (var modelId in returnedData.modelList) {
	            		
	            		row += "<option value='"+modelId+"'>"+returnedData.modelList[modelId]+"</option>"
	            		
	            	}
	            	
	            }
	            
	            $('#modelId').html(row);
				    	
	        }
	        
		});
  		
	});
	
</script>