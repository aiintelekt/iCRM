<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<#assign requestURI = ""/>

<#if request.getRequestURI().contains("editCustomField")>

<#assign requestURI = "editCustomField"/>
</#if>
<#assign groupId = groupId!/>
<#assign fieldId = request.getParameter("customFieldId")!/>

<#assign customFieldGroup  = delegator.findByAnd("CustomFieldSummary",Static["org.ofbiz.base.util.UtilMisc"].toMap("groupId", groupId,"customFieldId",fieldId), null, false)/>

 <#if customFieldGroup?has_content>
        <#list customFieldGroup as customFieldGroupVal>
          <#assign customFieldGroupType = customFieldGroupVal.customFieldType/>
           </#list>
    </#if>

<div class="row">
	 <#if groupId?has_content>
    <div id="main" role="main" class="pd-btm-title-bar pt-0">
    <div class="col-md-12 col-lg-12 col-sm-12">
    <form method="post" class="form-horizontal" data-toggle="validator" id="findCustomFieldsForm" name="findCustomFieldsForm">
    <@inputHidden id="groupId" value="${groupId!}"/>
    <@inputHidden id="isUpdate" value="Y"/>
    </form>
    <#else>
    <div id="main" role="main">
    
	<div class="col-md-12 col-lg-12 col-sm-12 dash-panel">
 <@sectionFrameHeader title="${uiLabelMap.Find} ${uiLabelMap.CustomField}" />
		<div id="accordion">
			<div class="row">
                    <@arrowDownToggle />
            </div>
                <div>
                	<div>
						   <form method="post" class="form-horizontal" data-toggle="validator" id="findCustomFieldsForm" name="findCustomFieldsForm">
						      <div class="row p-3f">
						      	
						      	<div class="col-md-6 col-lg-3 col-sm-12 search-right">
						         	<@dropdownCell 
										id="roleTypeId"
										options=roleTypeList
										required=false
										value=customField.roleTypeId
										allowEmpty=true
										tooltip = uiLabelMap.roleTypeId
										dataLiveSearch=true
										placeholder=uiLabelMap.roleTypeId
										/>
									<@dropdownCell 
										id="customFieldFormat"
										options=fieldFormatList
										required=false
										value=customField.customFieldFormat
										allowEmpty=true
										tooltip = uiLabelMap.customFieldFormat
										placeholder=uiLabelMap.customFieldFormat
										/>
								
						         </div>
						         <div class="col-md-6 col-lg-3 col-sm-12 search-right">
						         	<@dropdownCell 
										id="groupId"
										options=groupList
										required=false
										value=customField.groupId
										allowEmpty=true
										tooltip = uiLabelMap.customGroup
										dataLiveSearch=true
										placeholder=uiLabelMap.customGroup 
										/>
								<@dropdownCell 
										id="customFieldLength"
										options=fieldLengthList
										required=false
										value=customField.customFieldLength
										allowEmpty=true
										tooltip = uiLabelMap.fieldLength
										placeholder=uiLabelMap.fieldLength
										/>
								
						         </div>
						         <div class="col-md-6 col-lg-3 col-sm-12 search-right">
						         	<@inputCell 
										id="customFieldName"
										placeholder=uiLabelMap.customFieldName
										value=customField.customFieldName
										tooltip = uiLabelMap.customFieldName
										required=false
										maxlength=255
										/>
								<@dropdownCell 
										id="hide"
										options=yesNoOptions
										required=false
										value=customField.hide
										allowEmpty=true
										tooltip = uiLabelMap.hide
										placeholder=uiLabelMap.hide	
										/>
								
						         </div>
						         <div class="col-md-6 col-lg-3 col-sm-12 search-right">
							<@dropdownCell 
										id="customFieldType"
										options=fieldTypeList
										required=false
										value=customField.customFieldType
										allowEmpty=true
										tooltip = uiLabelMap.customFieldType
										placeholder=uiLabelMap.customFieldType
										/>
						         
							<div class="search-btn">
						         	<div class="float-right">
						         		<@submit label=uiLabelMap.find/>
						         	</div>	
						         </div>
						               
						        
						         
						         </div>
						        	
						      </div>
						   </form>
   						<div class="clearfix"> </div>
   					</div>
				</div>
			</div>
		</div>
</div>
</#if>
<div class="clearfix"> </div>
<#if (customFieldGroupType?has_content && customFieldGroupType == "MULTIPLE") && (requestURI?has_content && requestURI == "editCustomField")>
<div class="" style="width:100%">
<#elseif customFieldGroupType?has_content && customFieldGroupType == "MULTIPLE">

<div class="" style="width:100%" id="listof-lead">
<#else>
<div class="row" style="width:100%">
</#if>

<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
	<h2 class="float-left">${uiLabelMap.List} ${uiLabelMap.CustomField}</h2>

<div class="clearfix"> </div>	
<div id="findCustomFieldGrid" style=" width: 100%;" class="ag-theme-balham">
       <script type="text/javascript" src="/cf-resource/js/findCustomFields.js"></script>
</div>
</div>
</div>