<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<div class="row">
	<div id="main" role="main" class="pd-btm-title-bar">
	<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
    	<@sectionFrameHeader title="${uiLabelMap.Create} ${uiLabelMap.ContactField}" />				
		<div class="col-md-6 col-sm-6" style="padding:0px">	
			<form role="form" class="form-horizontal" 
				action="<@ofbizUrl>createContactField</@ofbizUrl>" 
				encType="multipart/form-data" method="post" data-toggle="validator">				
				<div class="form-body">			
					<#if groupId?has_content>
						<input type="hidden" id="groupId" name="groupId" value="${customField.groupId!}" />												
					<#else>	
						<@dropdownCell 
							id="groupId"
							label=uiLabelMap.contactFieldGroup
							placeholder=uiLabelMap.contactFieldGroup
							options=groupList
							required=true
							value=customField.groupId
							allowEmpty=true
							/>
					</#if>
					
					<@inputRow 
						id="customFieldName"
						label=uiLabelMap.contactFieldName
						placeholder=uiLabelMap.contactFieldName
						value=customField.customFieldName
						required=true
						maxlength=255
						/>
									
					<@dropdownCell 
						id="customFieldType"
						label=uiLabelMap.contactFieldType
						placeholder=uiLabelMap.contactFieldType
						options=fieldTypeList
						required=true
						value=customField.customFieldType
						allowEmpty=true
						/>	
						
					<@dropdownCell 
						id="customFieldFormat"
						label=uiLabelMap.contactFieldFormat
						placeholder=uiLabelMap.contactFieldFormat
						options=fieldFormatList
						required=true
						value=customField.customFieldFormat
						allowEmpty=true
						/>	
						
					<@dropdownCell 
						id="customFieldLength"
						label=uiLabelMap.fieldLength
						placeholder=uiLabelMap.fieldLength
						options=fieldLengthList
						required=false
						value=customField.customFieldLength
						allowEmpty=true
						/>													
						
					<@inputRow 
						id="sequenceNumber"
						label=uiLabelMap.sequence
						placeholder=uiLabelMap.sequence
						value=customField.sequenceNumber
						type="number"
						required=false
						min=1
						/>	
						
					<@dropdownCell 
						id="hide"
						label=uiLabelMap.hide
						placeholder=uiLabelMap.hide
						options=yesNoOptions
						required=false
						value=customField.hide
						allowEmpty=true
						/>							
				</div>
                <div class="clearfix"></div>
                <div class="col-md-12 col-sm-12">
                    <div class="form-group row">
                        <div class="offset-sm-4 col-sm-7 p-2">
                            <@submit label="Submit"/> 
                            <@cancel label="Cancel" onclick="/custom-field/control/findContactField"/> 
                        </div>
                    </div>
                </div>
			</form>		
		</div>
	</div>
</div>
</div>