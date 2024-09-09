<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<div class="row">
   <div id="main" role="main">
		
      <div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
      	
	  <@sectionFrameHeaderTab title="Create Activity Work Type" />
	  <div class="clearfix"></div>
	  <form name="createActivityWorkType" id="createActivityWorkType" action="createActivityWorkTypeAction" method="post" data-toggle="validator" >
        
              	  	
        <@dynaScreen 
            instanceId="CREATE_ACTIVITY_WORK_TYPE"
            modeOfAction="CREATE"
         />
		             
		<div class="offset-md-2 col-sm-10 p-2">
       		<@formButton
			btn1type="submit"
            btn1label="${uiLabelMap.Save}"
            btn2=true
            btn2onclick = "resetForm()"
            btn2type="reset"
            btn2label="${uiLabelMap.Clear}"
         />
           
        </div>
	            
     </form>
      </div>
   </div> <#-- main end -->
</div> <#-- row end-->

<script type="text/javascript">
   

$(document).ready(function() {

});

 

</script>
