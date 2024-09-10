<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<link rel="stylesheet" href="/admin-portal-resource/css/custom.css" type="text/css"/>

<div id="main" role="main" class="pd-btm-title-bar">
  	<@sectionFrameHeader title="${uiLabelMap.Find} ${uiLabelMap.batchDataStatus!}" />
  	<div class="col-lg-12 col-md-12 col-sm-12">
  	
  	<form method="post" id="findJobExecutionForm" class="form-horizontal" name="findJobExecutionForm" novalidate="novalidate" data-toggle="validator">
    <div class="row p-2">
    
    	<div class="col-md-2 col-sm-2">
         	<@inputDate 
				id="fromDate"
				placeholder="From date"
				/>
         </div>
         <div class="col-md-2 col-sm-2">
         	<@inputDate 
				id="thruDate"
				placeholder="Thru date"
				/>
         </div>
         
         <div class="col-md-2 col-sm-2">
         	<@button
            id="find-dyna-screen"
            label="${uiLabelMap.Find}"
            />	
         </div>
		         
	</div>	
	</form>

</div>

<script>

jQuery(document).ready(function() {

$('#find-jobExecution-button').on('click', function(){
	findBatchJobs();
});

$(".form_datetime").datetimepicker({
    //autoclose: true,
    //isRTL: BootStrapInit.isRTL(),
    //format: "dd MM yyyy - hh:ii",
    //pickerPosition: (BootStrapInit.isRTL() ? "bottom-right" : "bottom-left")
});

});

</script>
