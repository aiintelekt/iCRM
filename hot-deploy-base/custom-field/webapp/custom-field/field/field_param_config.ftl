<#include "component://homeapps/webapp/homeapps/lib/ofbizFormMacros.ftl"/>

<#assign paramConfigInputId = 1000>

<div id="multiple-value-config" class="col-md-12 col-sm-12 dash-panel">
	<div class="panel panel-default">
		<div class="panel-heading" role="tab" id="campaignConfig-heading">
			<h4 class="camp-config">
				<a role="button" data-toggle="collapse" data-parent="#accordionMenu"
					href="#accordion-campaignConfig" aria-expanded="false"
					aria-controls="collapseOne"> ${uiLabelMap.ParamConfig} </a>
			</h4> </div>
		<div id="accordion-campaignConfig" class="panel-collapse collapse show" role="tabpanel" aria-labelledby="campaignConfig-heading">
			<div class="panel-body">
				<div class="portlet-body form">
					<div id="batch-configuration" style="display: block">
						<div class="row">
							<div class="col-md-12 col-sm-12">
								<form id="update_param_config_form">
								
									<input type="hidden" name="groupId" value="${groupId!}">
									<input type="hidden" name="customFieldId" value="${customFieldId!}">
									<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}">
									
									<div id="days-campaign-section" style="display: block">
										<#if paramDataList?has_content>
											<#assign counter = 0>
											<#list paramDataList as paramData>
											<#assign counter = counter + 1>
											<div class="form-group row param-config-content">
												<div class="col-md-2 col-sm-2 fieldParamType-input">
													<select name="fieldParamType" autocomplete="off" class="ui dropdown search form-control fluid show-tick fieldParamType" required>
						                            	<option value="">${uiLabelMap.fieldParamType!}</option>
						                                <#if fieldParamTypes?has_content>
						                                <#list fieldParamTypes as paramType>
						                                <option value="${paramType.enumId!}" <#if paramType.enumId?has_content && paramType.enumId == "${paramData.paramType!}">selected</#if> >${paramType.description!}</option>
						                                </#list>
						                                </#if>
						                          	</select>
					                          	</div>
					                          	<div class="col-md-2 col-sm-2">
													<input type="text" class="form-control input-sm" name="fieldParamName" placeholder="${uiLabelMap.fieldParamName!}" value="${paramData.paramName!}" required/>
					                          	</div>
					                          	<div class="col-md-2 col-sm-2">
													<input type="text" class="form-control input-sm" name="fieldParamValue" placeholder="${uiLabelMap.fieldParamValue!}" value="${paramData.paramValue!}"/>
					                          	</div>
					                          	<div class="col-md-2 col-sm-2 fieldParamValueType-input">
													<select name="fieldParamValueType" autocomplete="off" class="ui dropdown search form-control fluid show-tick " data-paramConfigInputId="${paramConfigInputId}">
						                            	<option value="">${uiLabelMap.fieldParamValueType!}</option>
						                                <#if fieldParamValueTypes?has_content>
						                                <#list fieldParamValueTypes as paramValueType>
						                                <option value="${paramValueType.enumId!}" <#if paramValueType.enumId?has_content && paramValueType.enumId == "${paramData.paramValueType!}">selected</#if> >${paramValueType.description!}</option>
						                                </#list>
						                                </#if>
						                          	</select>
					                          	</div>
					                          	<div class="col-md-2 col-sm-2">
													<div class="input-group">
														<span class="input-group-addon"> 
															<a onclick="addParamConfigRepeateContent(this)" class="gd ml-1"><i class="fa fa-plus-circle"></i></a> 
															<#if (counter > 1) >
																<a class="plus-icon01 rd ml-1" onclick="removeParamConfigRepeateContent(this)"><i class="fa fa-minus-circle" aria-hidden="true"></i></a>
															</#if> 
														</span>
													</div>
					                          	</div>
											</div>
											</#list>
										<#else>
										
											<div class="form-group row param-config-content">
												<div class="col-md-2 col-sm-2 fieldParamType-input">
													<select name="fieldParamType" autocomplete="off" class="ui dropdown search form-control fluid show-tick ">
						                            	<option value="">${uiLabelMap.fieldParamType!}</option>
						                                <#if fieldParamTypes?has_content>
						                                <#list fieldParamTypes as paramType>
						                                <option value="${paramType.enumId!}" >${paramType.description!}</option>
						                                </#list>
						                                </#if>
						                          	</select>
					                          	</div>
					                          	<div class="col-md-2 col-sm-2">
													<input type="text" class="form-control input-sm" value="" name="fieldParamName" placeholder="${uiLabelMap.fieldParamName!}" />
					                          	</div>
					                          	<div class="col-md-2 col-sm-2">
													<input type="text" class="form-control input-sm" value="" name="fieldParamValue" placeholder="${uiLabelMap.fieldParamValue!}" />
					                          	</div>
					                          	<div class="col-md-2 col-sm-2 fieldParamValueType-input">
													<select name="fieldParamValueType" autocomplete="off" class="ui dropdown search form-control fluid show-tick " data-paramConfigInputId="${paramConfigInputId}">
						                            	<option value="">${uiLabelMap.fieldParamValueType!}</option>
						                                <#if fieldParamValueTypes?has_content>
						                                <#list fieldParamValueTypes as paramValueType>
						                                <option value="${paramValueType.enumId!}" >${paramValueType.description!}</option>
						                                </#list>
						                                </#if>
						                          	</select>
					                          	</div>
					                          	<div class="col-md-2 col-sm-2">
													<div class="input-group">
														<span class="input-group-addon"> 
															<a onclick="addParamConfigRepeateContent(this)" class="gd ml-1"><i class="fa fa-plus-circle"></i></a> 
														</span>
													</div>
					                          	</div>
											</div>
										
										</#if>
										
									</div>
								</form>
							</div>
						</div>
					</div>
					
					<div class="row">
						<div class="col-md-12 col-sm-12">
							<div class="form-group row param-config-content">
								<label class="col-sm-2"></label>
								<div class="col-sm-4">
									<button id="param_config_btn" type="button" class="btn btn-sm btn-primary mt">Save</button>
								</div>
							</div>
						</div>
					</div>
					
				</div>
			</div>
		</div>
	</div>
</div>

<script>

jQuery(document).ready(function() {
	$('.ui.dropdown.search').dropdown({clearable: true});
	
	$('#param_config_btn').on('click', function(){
						
		$.post('updateFieldParamConfig', $('#update_param_config_form').serialize(), function(returnedData) {
			if (returnedData.code == 200) {
				showAlert ("success", returnedData.message);
			}
		});
		
	});
	
});

var fieldParamType = '<option value="">${uiLabelMap.fieldParamType!}</option>';
                    <#if fieldParamTypes?has_content>
                    <#list fieldParamTypes as paramType>
                    fieldParamType += '<option value="${paramType.enumId!}" >${paramType.description!}</option>';
                    </#list>
                    </#if>

var fieldParamValueType = '<option value="">${uiLabelMap.fieldParamValueType!}</option>';
                    <#if fieldParamValueTypes?has_content>
                    <#list fieldParamValueTypes as paramValueType>
                    fieldParamValueType += '<option value="${paramValueType.enumId!}" >${paramValueType.description!}</option>';
                    </#list>
                    </#if>

var paramConfigInputId;
function addParamConfigRepeateContent (actionButton) {

	var cloneHtml = $(actionButton).closest( ".param-config-content" ).clone();

	if ( $(actionButton).parent().children().find('[class=\"fa fa-minus-circle\"]').length == 0 ) {
        cloneHtml.children().find('[class=\"fa fa-plus-circle\"]').parent().after('<a class="plus-icon01 rd ml-1" onclick="removeParamConfigRepeateContent(this)"><i class="fa fa-minus-circle" aria-hidden="true"></i></a>');
    }
    
    /*
    if (!paramConfigInputId) {
    	paramConfigInputId = Number( $(actionButton).closest( ".param-config-content" ).children().find('.campaign-picker').attr("data-paramConfigInputId") );
    }
    paramConfigInputId = paramConfigInputId + 1;
    */
    
    $(cloneHtml).find('.fieldParamType-input').empty();
	$(cloneHtml).find('.fieldParamType-input').html('<select class="ui dropdown search form-control fluid show-tick fieldParamType" name="fieldParamType" data-live-search="true" autocomplete="off" ><option value="">'+fieldParamType+'</select>');		
    $(cloneHtml).find('.fieldParamValueType-input').empty();
	$(cloneHtml).find('.fieldParamValueType-input').html('<select class="ui dropdown search form-control fluid show-tick fieldParamValueType" name="fieldParamValueType" data-live-search="true" autocomplete="off" >'+fieldParamValueType+'</select>');		
	    
    cloneHtml.children().find('.form-control').val("");
    
    $(actionButton).closest( ".param-config-content" ).after(cloneHtml);
    
    $('.ui.dropdown.search').dropdown({clearable: true});
}

function removeParamConfigRepeateContent (actionButton) {
	$(actionButton).closest( ".param-config-content" ).remove();
}

</script>