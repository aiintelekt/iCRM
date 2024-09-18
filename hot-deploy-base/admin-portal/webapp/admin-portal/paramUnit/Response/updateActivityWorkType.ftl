<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<div class="row">
   <div id="main" role="main">
		
      <div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
      	
	  <@sectionFrameHeaderTab title="Update Activity Work Type" />
	      
	  <form name="updateActivityWorkType" action="updateActivityWorkTypeAction" method="post" data-toggle="validator">
        
        <@inputHidden 
       	  	id="workEffortPurposeTypeId"
       	  	value="${parameters.workEffortPurposeTypeId?if_exists}"
       	  	/>
       	  	
        
       	  	
        <@dynaScreen 
            instanceId="CREATE_ACTIVITY_WORK_TYPE"
            modeOfAction="UPDATE"
         />
		             
		<div class="offset-md-2 col-sm-10 p-2">
       		<@formButton
                 btn1type="submit"
                 btn1label="${uiLabelMap.Save}"
                 btn2=true
                 btn2onclick = "resetFormToReload()"
                 btn2type="reset"
                 btn2label="${uiLabelMap.Clear}"
            />
        </div>
	            
     </form>
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
