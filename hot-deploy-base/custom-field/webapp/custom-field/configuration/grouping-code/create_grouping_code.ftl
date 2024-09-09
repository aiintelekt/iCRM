<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<div class="row" id="create-code">
    <div id="main" role="main">
    <#assign reqGroupType = "${groupingCode.groupType?if_exists}" />
    <#assign helpUrl = "" />
    <#-- 
    <#if reqGroupType == "SEGMENTATION">
 	<#assign helpUrl = Static["org.groupfio.common.portal.util.DataHelper"].getHelpUrl(delegator, "/custom-field/control/groupingCode?groupType=SEGMENTATION") />		
  	 <#else>
     <#assign helpUrl = Static["org.groupfio.common.portal.util.DataHelper"].getHelpUrl(delegator, "/custom-field/control/groupingCode?groupType=ECONOMIC_METRIC") />		
	 </#if>
	 -->
<div  class="col-lg-12 col-md-12 col-sm-12 dash-panel"> 
     <@sectionFrameHeader title="${uiLabelMap.Create} ${uiLabelMap.GroupingCode} [ <small>${groupingCode.groupType!?if_exists}</small> ]" extra=helpUrl?if_exists />

  		<div class="col-md-6 col-md-6 col-sm-6" style="padding:0px">		
		<div class="portlet-body form">
			<form role="form" class="form-horizontal" action="<@ofbizUrl>createGroupingCode</@ofbizUrl>" encType="multipart/form-data" method="post" id="groupCode" data-toggle="validator">
				
			<input type="hidden" name="groupType" value="${groupingCode.groupType!}" />		
				
			<div class="form-body">
			
			<@inputRow 
				id="groupingCode"
				label=uiLabelMap.groupingCode
				placeholder=uiLabelMap.groupingCode
				value=groupingCode.groupingCode
				required=true
				dataError="Please Enter Grouping Code"
				/>	
				
			<@inputRow 
				id="sequenceNumber"
				label=uiLabelMap.sequence
				placeholder=uiLabelMap.sequence
				value=groupingCode.sequenceNumber		
				maxlength=20			
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
			
			<div class="form-group row">
                    <div class="offset-sm-4 col-sm-7 p-2">
                       <@submit label="${uiLabelMap.submit!}" onclick="javascript:return onSubmitValidate(this);"/>
                    	<@reset label="${uiLabelMap.Clear!}" onclick="javascript:clearGrouping();"/>
                    </div>
                </div>
			
		</form>			
							
		</div>
		</div>	
	</div>	
</div>
</div>
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
$('#sequenceNumber').keyup(function(){	
		if($(this).val()!=""){
		
		$('#sequenceNumber_error').hide();
	}
});


	function onSubmitValidate() {	
	var valid=true;
	if($('#groupingCode').val()==""){
	$('#groupingCode_error').html("Please Enter Grouping code");
	$('#groupingCode_error').show();
		valid=false;
	}
	else if($('#groupingCode').val().length>250){
	$('#groupingCode_error').html("Please Enter less than 250 Characters");
	$('#groupingCode_error').show();
		valid=false;
	}	
	if($('#sequenceNumber').val() != ""){
		if(!new RegExp(/^[0-9]{0,20}$/).test($('#sequenceNumber').val())){console.log('fsdfsd');
			$('#sequenceNumber_error').html("Please Enter integers only");
			$('#sequenceNumber_error').show();
			valid=false;
		}
	}
	if($('#description').val()==""){
	$('#description_error').html("Please Enter Description");
	$('#description_error').show();
		valid=false;
	}	
	return valid;
	 
   }
</script>