<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/lib/picker_macro.ftl"/>

<div class="row">
	<div id="main" role="main" class="pd-btm-title-bar">

	<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
	
	<@sectionFrameHeader title="Find Sql Group" />
		
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
				<form method="post" id="sql-group-search-form" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
				
				<input type="hidden" name="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>
				<input type="hidden" name="userId" value="${userLogin.userLoginId?if_exists}" />
				<div class="panel-body">
				
					<div class="row">
					<div class="col-lg-12 col-md-12 col-sm-12">
						 
						<@dynaScreen 
							instanceId="FIND_SQLGRP"
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

<script>     
$(document).ready(function() {
/*
$("#startDate_picker").on("dp.change", function (e) {
 	$('#endDate_picker').data("DateTimePicker").minDate(e.date);
});      
$("#endDate_picker").on("dp.change", function (e) {
   $('#startDate_picker').data("DateTimePicker").maxDate(e.date);
});

$(".technician-input").one( "click",function(){
	getTechnicians();
});
*/
});

</script>