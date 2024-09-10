<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<div class="row">
	<div id="main" role="main" class="pd-btm-title-bar">
	<div class="col-md-12 col-lg-12 col-sm-12 dash-panel">
	<@sectionFrameHeaderTab title="${uiLabelMap.Update} ${uiLabelMap.ContactFieldGroup}" />
		<div class="row p-2">
			<div class="col-md-6 col-sm-6" style="padding:0px">
			<form role="form" class="form-horizontal" 
				action="<@ofbizUrl>updateContactFieldGroup</@ofbizUrl>" 
				encType="multipart/form-data" method="post" data-toggle="validator">																				
				<div class="form-body">
					<@inputHidden id="groupId" name="groupId" value=customFieldGroup.groupId! />
					<@displayCell label="${uiLabelMap.groupId!}"
      					value="${customFieldGroup.groupId!}" />
					<@inputRow 
						id="groupName"
						label=uiLabelMap.groupName
						placeholder=uiLabelMap.groupName
						value=customFieldGroup.groupName
						required=true
						maxlength=255
						/>
					
					<@inputRow 
						id="sequence"
						label=uiLabelMap.sequence
						placeholder=uiLabelMap.sequence
						value=customFieldGroup.sequence
						type="number"
						required=false
						min=1
						/>	
						
					<@dropdownCell 
						id="hide"
						label=uiLabelMap.hide
						options=yesNoOptions
						required=false
						value=customFieldGroup.hide
						allowEmpty=true
						/>																																																																																																																																																																																																																																														
				</div>
                <div class="clearfix"></div>
                <div class="col-md-12 col-sm-12">
                    <div class="form-group row">
                        <div class="offset-sm-4 col-sm-7">
                            <@submit label="Submit"/>  
                            <@cancel label="Cancel" onclick="/custom-field/control/findContactFieldGroup"/>
                        </div>
                    </div>
                </div>
			</form>		
			</div>
		</div>
	</div>
</div>
</div>