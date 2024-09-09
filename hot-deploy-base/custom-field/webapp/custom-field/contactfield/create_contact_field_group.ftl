<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<div class="row">
	<div id="main" role="main" class="pd-btm-title-bar">
	    <#assign helpUrl = Static["org.groupfio.common.portal.util.DataHelper"].getHelpUrl(delegator, request.getRequestURI()) />  
	<div class="col-md-12 col-lg-12 col-sm-12 dash-panel">
    	<@sectionFrameHeader title="${uiLabelMap.CreateContactFieldGroup}"/>		
		<div class="row p-2">
			<div class="col-md-6 col-sm-6" style="padding:0px">									
				<form role="form" class="form-horizontal" 
					action="<@ofbizUrl>createContactFieldGroup</@ofbizUrl>" 
					encType="multipart/form-data" method="post" data-toggle="validator">						
					<div class="form-body">					
						<@inputRow 
							id="groupId"
							label=uiLabelMap.groupId
							placeholder=uiLabelMap.groupId
							value=customFieldGroup.groupId
							required=true
							maxlength=250
							/>					
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