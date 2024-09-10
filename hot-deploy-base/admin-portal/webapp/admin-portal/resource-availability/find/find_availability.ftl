<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>

<div class="row">
	<div id="main" role="main" class="pd-btm-title-bar">
		
	<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
		<@sectionFrameHeaderTab title="${uiLabelMap.FindResAvail}" />
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
				<form method="post" id="availablity-search-form" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
				
				<div class="panel-body">
				
					<div class="row">
					<div class="col-lg-4 col-md-6 col-sm-12">
						 
						<@dropdownCell
			            id="partyId"
			            allowEmpty=true
			            options=partyList!
			            placeholder="Select Resource"
			            isMultiple="N"
			            />
			            <@dropdownCell
			            id="reasonId"
			            allowEmpty=true
			            options=reasonList!
			            placeholder="Select Reason"
			            isMultiple="N"
			            />  					              
			          
			            </div>
			       <div class="col-lg-4 col-md-6 col-sm-12">     
			            <@inputDate 
						id="fromDate"
			            placeholder="${uiLabelMap.fromDate}"
			            dateFormat="YYYY-MM-DD"
						/>
												
					</div>
					   <div class="col-lg-4 col-md-6 col-sm-12">   
						
						<@inputDate 
						id="thruDate"
			            placeholder="${uiLabelMap.thruDate}"
			            dateFormat="YYYY-MM-DD"
						/>
			              
					  <div class="search-btn">
							<@button
					        id="main-search-btn"
					        label="${uiLabelMap.Find}"
					        />	
					     	<@reset
							label="${uiLabelMap.Reset}"/>
						</div>
					</div>
					 
					</div>
							      		
				</div>	
				</form>
			</div>	
		</div>	
	</div>	
		
	</div>
	</div>
</div>

<script>     
$(document).ready(function() {

$("#fromDate_picker").on("dp.change", function (e) {
 	$('#thruDate_picker').data("DateTimePicker").minDate(e.date);
});      
$("#thruDate_picker").on("dp.change", function (e) {
   $('#fromDate_picker').data("DateTimePicker").maxDate(e.date);
});

getUsers("${loggedUserId!}", "${loggedUserPartyName!}");

});

function getUsers(loggedInUserId,userName) {
    var userOptionList = '<option value=""></option>';//'<option value="'+loggedInUserId+'">'+userName+'</option>';
    $.ajax({
        type: "GET",
        //url:'/common-portal/control/getUsersList?roleTypeId=ACT_OWNER&externalLoginKey=${requestAttributes.externalLoginKey!}',
        url:'/common-portal/control/getUsersList?roleTypeId=ACT_OWNER&externalLoginKey=${requestAttributes.externalLoginKey!}',
        async: false,
        success: function(data) {
        	for (var i = 0; i < data.length; i++) {
                var type = data[i];
                userOptionList += '<option value="'+type.partyId+'">'+type.userName+' ('+ type.roleDesc +') </option>';
            }
        }
    });
    
   	$("#partyId").html(userOptionList);
}

</script>