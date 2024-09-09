
<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
 <div class="page-header border-b pt-2">
   <@headerH2
    title="Administration"
    />
 </div>
  <div class="row">
  <div class="col-md-12 col-lg-6 col-sm-12">
   <@displayCell
     id="createdOn"
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
	});
	function loaddata(){
		var salesOppId = $('#salesOppId').val();
		$.ajax({
			url:'getviewopp',
			data:{"salesOppId":salesOppId},
			type:"post",
			success:function(data){
	 			if(data!=undefined&&data!=""){
					var createdStamp = data[0].createdStamp;
					document.getElementById("createdOn").innerHTML = createdStamp; 
					var createdByUserLogin = data[0].createdByUserLogin;
					document.getElementById("createdBy").innerHTML = createdByUserLogin;
					var modifiedUserLogin = data[0].lastModifiedByUserLogin;
					document.getElementById("modifiedBy").innerHTML = modifiedUserLogin; 
					var lastUpdatedStamp = data[0].lastModifiedDate;
					document.getElementById("modifiedOn").innerHTML = lastUpdatedStamp; 
					var closedOn = data[0].closedOn;
					document.getElementById("closedOn").innerHTML = closedOn; 
					var closedBy = data[0].closedBy;
					document.getElementById("closedBy").innerHTML = closedBy; 
				}
			}
		}); 
	}	
</script>
