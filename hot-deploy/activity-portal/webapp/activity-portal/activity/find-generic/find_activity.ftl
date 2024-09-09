<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/lib/picker_macro.ftl"/>
<div class="row">
	<div id="main" role="main" class="pd-btm-title-bar">

	<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
	
	<@sectionFrameHeader title="Find Activity" />
		
	<div class="panel-group" id="accordionMenu" role="tablist" aria-multiselectable="true">
		<div class="panel panel-default">
			<div class="panel-heading" role="tab" id="headingTwo">
				<h4 class="panel-title">
					<a role="button" data-toggle="collapse" data-parent="#accordionMenu"
						href="#accordionDynaBase" aria-expanded="true"
						aria-controls="collapseOne"> ${uiLabelMap.MainFilter} </a>
				</h4>
			</div>	
			
			<div id="accordionDynaBase" class="panel-collapse collapse show" role="tabpanel" aria-labelledby="headingOne">
				<form method="post" id="activity-search-form" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
				
				<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
				<input type="hidden" name="owner" value="">
				<input type="hidden" name="isSrActivityOnly" value="N">
				<input type="hidden" name="requiredSrInfo" value="N">
				
				<#if activityTypeList?has_content>
				<#list activityTypeList.entrySet() as entry>  
				    <input type="hidden" name="defaultActivityTypes" value="${entry.key!}">
				</#list>
				</#if>
				<#if isEnableProgramAct?has_content && isEnableProgramAct="Y">	
				<input type="hidden" name="isChecklistActivity" value="N">	
				</#if>
				<div class="panel-body">
				
					<div class="row">
					<div class="col-lg-12 col-md-12 col-sm-12">
						<@dynaScreen 
							instanceId="FIND_ACT_GEN"
							modeOfAction="CREATE"
							/>
					</div>
					
					</div>
											
					<div class="text-right pd-cbx-lbls pad-10" style="padding-top: 0px;">
				     	<@button
				        id="main-search-btn"
				        label="${uiLabelMap.Find}"
				        />	
				     	<@reset
						label="${uiLabelMap.Reset}"/>
		            </div>
		      		
				</div>	
				</form>
			</div>	
		</div>	
	</div>	
		
	</div>
	</div>
</div>
<@partyPicker 
	instanceId="partyPicker"
	/>
<script>     
$(document).ready(function() {

	$('.picker-window-erase').click(function () {
		$("#srPrimaryContactId").empty();
		$('#srPrimaryContactId').dropdown('clear');
	});
	
	$(':reset').on('click', function(evt) {
	    evt.preventDefault();
	    $('.picker-window-erase').click();
	    //$("#srPrimaryContactId").empty();
		//$('#srPrimaryContactId').dropdown('clear');
	});
	
  $("#srPartyId_desc").on("change", function() {
		var partyId = $("#srPartyId_val").val();
		if (partyId != "") {
       		getPrimaryContacts(partyId);
       }
	});
$("#startDate_picker").on("dp.change", function (e) {
 	$('#endDate_picker').data("DateTimePicker").minDate(e.date);
});      
$("#endDate_picker").on("dp.change", function (e) {
   $('#startDate_picker').data("DateTimePicker").maxDate(e.date);
});

$(".technician-input").one( "click",function(){
	getTechnicians();
});

});

function getTechnicians() {
    var userOptionList = '<option value=""></option>';
    $.ajax({
        type: "GET",
        url:'/common-portal/control/getUsersList?roleTypeId=ACT_OWNER&isIncludeInactiveUser=Y&externalLoginKey=${requestAttributes.externalLoginKey!}',
        async: false,
        success: function(data) {
        	for (var i = 0; i < data.length; i++) {
                var type = data[i];
                if (type.userName) {
                	userOptionList += '<option value="'+type.userLoginId+'">'+type.userName+'</option>';
                }
            }
        }
    });
   $("#technician").html(DOMPurify.sanitize(userOptionList));
   $("#technician").dropdown('refresh');
}
function getPrimaryContacts(partyId){
	$("#srPrimaryContactId").empty();
	var nonSelectContent = "<span class='nonselect'>Please Select</span>";
	var dataSourceOptions = '';
	var selectedContactId  = $("#selectedContactId").val();
	$('#srPrimaryContactId').dropdown('clear');
	$("div.ui.dropdown.search.form-control.fluid.show-tick.srPrimaryContactId.selection > i").removeClass("clear");
	$.ajax({
		type: "POST",
		url: "/common-portal/control/getPrimaryContacts",
		data: {"partyId": partyId, "externalLoginKey": "${requestAttributes.externalLoginKey!}"},
		async: false,
		success: function (data) {
			if((data != null && data != "" && data !="undefined") && data.responseMessage=="success" && data.partyRelContacts.length > 0){
					for (var i = 0; i < data.partyRelContacts.length; i++) {
						var entry = data.partyRelContacts[i];
						if(entry.selected!=null){
		            		dataSourceOptions += '<option value="'+entry.contactId+'" selected>'+entry.name+'</option>';
						}else{
		            		dataSourceOptions += '<option value="'+entry.contactId+'">'+entry.name+'</option>';
						}
					}
				$("div.ui.dropdown.search.form-control.fluid.show-tick.srPrimaryContactId.selection > i").addClass("clear");
			}
		}
	});
	$("#srPrimaryContactId").append(dataSourceOptions);
	$("#srPrimaryContactId").dropdown('refresh');
}
</script>