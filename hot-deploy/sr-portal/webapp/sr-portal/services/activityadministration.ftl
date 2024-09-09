<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<div class="page-header border-b pt-2">
	<@headerH2
   		title="Administration"
    />
</div>

<div class="row">
	<div class="col-md-12 col-lg-6 col-sm-12">
   		<@displayCell
     		id="createdStamp"
     		label="Created On"
     		value=""
   		/>
  		<@displayCell
     		id="modifiedOn"
     		label="Modified On"
     		value=""
   		/>
 		<@displayCell
     		id="closedOn"
     		label="Closed On"
     		value=""
   		/>
	</div>
 	<div class="col-md-12 col-lg-6 col-sm-12">
 		<@displayCell
     		id="createdBy"
     		label="Created By"
     		value=""
   		/>
 		<@displayCell
     		id="modifiedBy"
     		label="Modified By"
     		value=""
   		/>
   		<@displayCell
     		id="closedBy"
     		label="Closed By"
     		value=""
   		/>
 	</div>
</div>

<script>
	$(document).ready(function() {    
		loaddata();
		
	function loaddata(){
		var workEffortId = $('#workEffortId').val();
		$.ajax({
    		url:'viewServiceActivityDetails',
    		data:{"workEffortId":workEffortId},
    		type:"post",
    		success:function(data){
    			var createdStamp = data[0].createdStamp;
     			if(createdStamp!=null){
  	 				document.getElementById("createdStamp").innerHTML=createdStamp;
   				}else{
   					document.getElementById("createdStamp").innerHTML="--";
   				}
    
    			var createdByUserLogin = data[0].createdByUserLogin;
     			if(createdByUserLogin!=null){
  	 				document.getElementById("createdBy").innerHTML=createdByUserLogin;
   				}else{
   					document.getElementById("createdBy").innerHTML="--";
   				}
   				
    			var lastModifiedByUserLogin = data[0].lastModifiedByUserLogin;
     			if(lastModifiedByUserLogin!=null){
  	 				document.getElementById("modifiedBy").innerHTML=lastModifiedByUserLogin;
			   	}else{
			   		document.getElementById("modifiedBy").innerHTML="--";
			   	}
			   	
    			var lastModifiedDate = data[0].lastModifiedDate;
    			if(lastModifiedDate!=null){
  	 				document.getElementById("modifiedOn").innerHTML=lastModifiedDate;
   				}else{
   					document.getElementById("modifiedOn").innerHTML="--";
   				}
   				
    			var lastUpdatedTxStamp = data[0].lastUpdatedTxStamp;
     			if(lastUpdatedTxStamp!=null){
  	 				document.getElementById("closedOn").innerHTML=lastUpdatedTxStamp;
   				}else{
   					document.getElementById("closedOn").innerHTML="--";
   				}
   				
    			var closedByUserLogin = data[0].closedByUserLogin;
     			if(closedByUserLogin!=null){
  	 				document.getElementById("closedBy").innerHTML=closedByUserLogin;
   				}else{
   					document.getElementById("closedBy").innerHTML="--";
   				}
    		}
    	}); 
	}    
});


</script>
 
 
