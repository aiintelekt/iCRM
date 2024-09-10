<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<div class="row">
	<div id="main" role="main">
		<form id="mainFrom" name="mainFrom" action="<@ofbizUrl>createSegmentMap</@ofbizUrl>" method="POST" >
		<div class="col-lg-12 col-md-12 col-sm-12">
			<@sectionFrameHeader title="${uiLabelMap.segmentMap!}" />
			<@dynaScreen
				instanceId="SEGMENT_MAPPING" 
				modeOfAction="CREATE" />
			<div class="offset-md-2 col-sm-10 pb-2">
			<@submit label="${uiLabelMap.submit!}" />
			<@reset id="reset" label="${uiLabelMap.Reset!}"/>
			</div>
		</div>
		</form>
	</div>
</div>
<script>

$(document).ready(function() {
	$("#masterTable").change(function() {
	if($("#masterTable").val()==""){
	$("#reset").click();
	$("#masterField").empty();
	}
	var selectedValue = $(this).val(); // Get the selected value from the first dropdown
	var columnNameOptionList = "";
	$.ajax({
		type:"GET",
		url:'/custom-field/control/getRelatedSegmentField',
		async:false,
		data: {
			"masterTable": selectedValue
		},
		success:function(data){
			if(data.list){
				$("#masterField").empty();
				for (var i = 0; i < data.list.length; i++){
				var fieldValue=data.list[i];
				columnNameOptionList+='<option value="'+fieldValue+'">'+fieldValue+'</option>';
				}
			}
		}
	});
	$("#masterField").html(columnNameOptionList);
	$("#masterField").dropdown('refresh');
	});
});
</script>
