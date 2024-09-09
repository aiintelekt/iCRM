<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<div class="row">
    <div id="main" role="main">
   	 <#assign helpUrl = Static["org.groupfio.common.portal.util.DataHelper"].getHelpUrl(delegator, request.getRequestURI()) />  
<div class="col-lg-12 col-md-12 col-sm-12 dash-panel ">
	<@sectionFrameHeader title="${uiLabelMap.Create!} ${uiLabelMap.CustomFieldGroup!}"  />
	<div class="col-md-6 col-sm-6" style="padding:0px">
				
		<div class="portlet-body form">
			<form id="mainFrom" role="form" class="form-horizontal" action="<@ofbizUrl>createCustomFieldGroup</@ofbizUrl>" encType="multipart/form-data" method="post" data-toggle="validator">
				
			<div class="form-body">
			
			<@inputRow 
				id="groupId"
				label=uiLabelMap.groupId
				placeholder=uiLabelMap.groupId
				value=customFieldGroup.groupId
				required=false
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
				
			<@dropdownCell 
				id="groupingCode"
				label=uiLabelMap.groupingCode
				options=groupingCodeList
				required=false
				value=customFieldGroup.groupingCode
				allowEmpty=true				
				dataLiveSearch=true
				required=true
				placeholder=uiLabelMap.pleaseSelect
				isMultiple="Y"
				/>		
			
			<@inputRow 
				id="sequence"
				label=uiLabelMap.sequence
				placeholder=uiLabelMap.sequence
				value=customFieldGroup.sequence
				maxlength=20
				dataError="Please Select Digits Only"
				type="number"
				/>	
				
			<@dropdownCell 
				id="hide"
				label=uiLabelMap.hide
				options=yesNoOptions
				required=false
				value=customFieldGroup.hide
				allowEmpty=true
				placeholder=uiLabelMap.pleaseSelect
				/>								
															
			</div>
			
			<div class="form-group row">
                    <div class="offset-sm-4 col-sm-7 p-2">
                       	<@submit label=uiLabelMap.submit/>
                    	<@reset label=uiLabelMap.Clear/>                    		
                    </div>
                </div>
			
		</form>			
							
		</div>
		</div>																																													
	</div>
</div>
</div>

<script>

</script>
