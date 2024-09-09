<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
<#assign extra='<a href="/custom-field/control/copyGroupingCode?groupingCodeId=${groupingCodeId!}" class="btn btn-xs btn-primary m5"><i class="fa fa-edit" aria-hidden="true"></i> Copy</a>
<a href="#"  class="btn btn-xs btn-primary back-btn" onclick="window.history.back();"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>'/>
<div class="row" id="update-code">
	<div id="main" role="main">
		<form id="mainFrom" method="post" action="<@ofbizUrl>updateGroupingCode</@ofbizUrl>" data-toggle="validator">
			<input type="hidden" name="groupingCodeId" value="${groupingCodeId!}"/>
			<input type="hidden" name="groupType" value="${groupingCode.groupType!}"/>
			<div class="col-lg-12 col-md-12 col-sm-12">
				<@sectionFrameHeader title="${uiLabelMap.Update} ${uiLabelMap.GroupingCode} [ <small>${groupingCode.groupType!}</small> ]" extra=extra/>
			</div>
			<br>
			<div class="col-lg-6 col-md-6 col-sm-6">
				<@inputRow
					id="groupingCode"
					label=uiLabelMap.groupingCode
					value=groupingCode.groupingCode
					readonly=true
					/>
				<@inputRow
					id="sequenceNumber"
					label=uiLabelMap.sequence
					placeholder=uiLabelMap.sequence
					value=groupingCode.sequenceNumber
					type="number"
					required=false
					min=1
					/>
				<@inputArea
					id="description"
					label=uiLabelMap.description
					placeholder=uiLabelMap.description
					rows="3"
					value=groupingCode.description
					required=true
					dataError="Please Enter Description"
					/>
			</div>
			<br>
			<div class="offset-md-2 col-sm-12">
				<@submit
					label="Submit"
					onclick="javascript:return onSubmitValidate(this);"
					/>
				<@cancel
					label="Cancel"
					onclick="groupingCode?groupType=${groupType!}"
					/>
			</div>
		</form>
	</div>
</div>


<form method="post" id="findAttributeGroupForm">
	<input type="hidden" name="groupingCodeId" value="${groupingCodeId!}" />
	<input type="hidden" name="groupingCode" value="${groupingCodeId!}" />
	<input type="hidden" name="groupType" value="${groupingCode.groupType!}" />
	<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
</form>

<script>
	$('#groupingCode').keyup(function(){	
		if($(this).val()!=""){
		
		$('#groupingCode_error').hide();
	}
});
	$('#description').keyup(function(){	
		if($(this).val()!=""){
		
		$('#description_error').hide();
	}
});

	function onSubmitValidate() {	
	var valid=true;	
	if($('#sequenceNumber').val().length>19){
		$('#sequenceNumber_error').html("Please Enter below 19 digits");
		valid=false;
	}
	if($('#description').val()==""){
	$('#description_error').html("Please Enter Description");
	$('#description_error').show();
		valid=false;
	}	
	return valid;
	 
   }
</script>