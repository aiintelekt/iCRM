<#include "component://homeapps/webapp/homeapps/lib/ofbizFormMacros.ftl"/>

<div class="page-header border-b">
	<h1>${uiLabelMap.Find} ${uiLabelMap.CustomField}</h1>
</div>

<div class="card-header mt-2 mb-3">
   <form method="post" class="form-horizontal" data-toggle="validator">
      <div class="row">
      	
      	<div class="col-md-2 col-sm-2">
         	<@simpleDropdownInput 
				id="roleTypeId"
				options=roleTypeList
				required=false
				value=customField.roleTypeId
				allowEmpty=true
				tooltip = uiLabelMap.roleTypeId
				dataLiveSearch=true
				emptyText = uiLabelMap.roleTypeId
				/>
         </div>
         <div class="col-md-2 col-sm-2">
         	<@simpleDropdownInput 
				id="groupId"
				options=groupList
				required=false
				value=customField.groupId
				allowEmpty=true
				tooltip = uiLabelMap.customGroup
				dataLiveSearch=true
				emptyText = uiLabelMap.customGroup
				/>
         </div>
         <div class="col-md-2 col-sm-2">
         	<@simpleInput 
				id="customFieldName"
				placeholder=uiLabelMap.customFieldName
				value=customField.customFieldName
				tooltip = uiLabelMap.customFieldName
				required=false
				maxlength=255
				/>
         </div>
         <div class="col-md-2 col-sm-2">
         	<@simpleDropdownInput 
				id="customFieldType"
				options=fieldTypeList
				required=false
				value=customField.customFieldType
				allowEmpty=true
				tooltip = uiLabelMap.customFieldType
				emptyText = uiLabelMap.customFieldType
				/>
         </div>
         <div class="col-md-2 col-sm-2">
         	<@simpleDropdownInput 
				id="customFieldFormat"
				options=fieldFormatList
				required=false
				value=customField.customFieldFormat
				allowEmpty=true
				tooltip = uiLabelMap.customFieldFormat
				emptyText = uiLabelMap.customFieldFormat
				/>	
         </div>
         <div class="col-md-2 col-sm-2">
         	<@simpleDropdownInput 
				id="customFieldLength"
				options=fieldLengthList
				required=false
				value=customField.customFieldLength
				allowEmpty=true
				tooltip = uiLabelMap.fieldLength
				emptyText = uiLabelMap.fieldLength
				/>
         </div>
         <div class="col-md-2 col-sm-2">
         	<@simpleDropdownInput 
				id="hide"
				options=yesNoOptions
				required=false
				value=customField.hide
				allowEmpty=true
				tooltip = uiLabelMap.hide
				emptyText = uiLabelMap.hide
				/>	
         </div>
         
         <@fromSimpleAction id="" showCancelBtn=false isSubmitAction=true submitLabel="Find"/>
        	
      </div>
   </form>
   <div class="clearfix"> </div>
</div>
