<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<div class="row">
   <div id="main" role="main">
		
      <div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
      	<#assign extra='
      	<a href="/admin-portal/control/updateTechnicianRate?partyId=${partyId!}&rateTypeId=${inputContext.rateTypeId!}&uomId=${inputContext.currencyUomId!}&fromDate=${inputContext.fromDate!}" class="btn btn-xs btn-primary text-right">
            <i class="fa fa-edit" aria-hidden="true"></i> Update
        </a>
        <a href="/admin-portal/control/createTechnicianRate" class="btn btn-xs btn-primary text-right">
             Back
        </a>' />
	  <@sectionFrameHeaderTab title="View Technician Rate" extra=extra/>
	      
        <@dynaScreen 
            instanceId="CREATE_TECHNICIAN_RATE"
            modeOfAction="VIEW"
         />
	            
      </div>
   </div> <#-- main end -->
</div> <#-- row end-->

<script type="text/javascript">
   
$(document).ready(function(){
	getTechnicianUsers();
});

function getTechnicianUsers() {
    var userOptionList = '<option value=""></option>';
    $.ajax({
        type: "GET",
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
