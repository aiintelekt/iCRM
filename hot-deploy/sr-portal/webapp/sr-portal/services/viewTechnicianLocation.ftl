<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#assign readOnlyPermission = Static["org.fio.homeapps.util.DataUtil"].hasPermissionWoFullPerm(request, "READ_ONLY_PERM")?if_exists />

<div class="row">
   <div id="main" role="main">
		
      <div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
		<#assign extra='' />
		<#if readOnlyPermission!>
		<#else>
			<#assign extra='
		        <a href="/sr-portal/control/createTechnicianLocation?externalLoginKey=${requestAttributes.externalLoginKey!}" class="btn btn-xs btn-primary text-right">
		            <i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back
		        </a>
		        <a href="/sr-portal/control/updateTechnicianLocation?countyId=${context.generalCountyGeoId!}&stateId=${context.generalStateProvinceGeoId!}" class="btn btn-xs btn-primary text-right">
		            <i class="fa fa-edit" aria-hidden="true"></i> Update
		        </a>' />
		</#if>
	  <@sectionFrameHeaderTab title="View Technician Location" extra=extra/>
	      
       <@dynaScreen 
            instanceId="CREATE_TECHNICIAN_LOCATION"
            modeOfAction="VIEW"
         />
	            
      </div>
   </div> <#-- main end -->
</div> <#-- row end-->

<script type="text/javascript">
   
$(document).ready(function(){
	
	<#-- getProductStores();
	getTechListForTech1();
	getTechListForTech2();
	getTechListForTech3();
	getTechListForTech4();  -->
});

<#-- function getProductStores(){
	
	var storeOptionList = "";
	$.ajax({
	    type: "GET",
	    url:'/admin-portal/control/getProductStores?externalLoginKey=${requestAttributes.externalLoginKey!}',
	    async: false,
	    success: function(data) {
	    	for (var i = 0; i < data.length; i++) {
	            var type = data[i];
	            storeOptionList += '<option value="'+type.storeId+'">'+type.storeName+'</option>';
	        }
	    }
	});
	
	$("#productStoreId").html(storeOptionList);
	$("#productStoreId").dropdown('refresh');
}

function getTechListForTech1(){
	
	var technicianOptionList = '<option value=""></option>';
	
	$.ajax({
	    type: "GET",
	    url:'/admin-portal/control/getContractorTechnicians?externalLoginKey=${requestAttributes.externalLoginKey!}',
	    async: false,
	    success: function(data) {
	    	var technician1= '${inputContext.technician1!}';
	    	for (var i = 0; i < data.length; i++) {
	            var type = data[i];
	            if(technician1 && technician1 === type.technicianId)
	            	technicianOptionList += '<option value="'+type.technicianId+'" selected>'+type.name+'</option>';
	            else
	            	technicianOptionList += '<option value="'+type.technicianId+'">'+type.name+'</option>';
	        }
	    }
	});
	
	$("#technician1").html(technicianOptionList);
	$("#technician1").dropdown('refresh');
}

function getTechListForTech2(){
	
	var technicianOptionList = '<option value=""></option>';
	
	$.ajax({
	    type: "GET",
	    url:'/admin-portal/control/getContractorTechnicians?externalLoginKey=${requestAttributes.externalLoginKey!}',
	    async: false,
	    success: function(data) {
	    	var technician2= '${inputContext.technician2!}';
	    	for (var i = 0; i < data.length; i++) {
	            var type = data[i];
	            if(technician2 && technician2 === type.technicianId)
	            	technicianOptionList += '<option value="'+type.technicianId+'" selected>'+type.name+'</option>';
	            else
	            	technicianOptionList += '<option value="'+type.technicianId+'">'+type.name+'</option>';
	        }
	    }
	});
	
	$("#technician2").html(technicianOptionList);
	$("#technician2").dropdown('refresh');
}

function getTechListForTech3(){
	
	var technicianOptionList = '<option value=""></option>';
	
	$.ajax({
	    type: "GET",
	    url:'/admin-portal/control/getContractorTechnicians?externalLoginKey=${requestAttributes.externalLoginKey!}',
	    async: false,
	    success: function(data) {
	    	var technician3= '${inputContext.technician2!}';
	    	for (var i = 0; i < data.length; i++) {
	            var type = data[i];
	            if(technician3 && technician3 === type.technicianId)
	            	technicianOptionList += '<option value="'+type.technicianId+'" selected>'+type.name+'</option>';
	            else
	            	technicianOptionList += '<option value="'+type.technicianId+'">'+type.name+'</option>';
	        }
	    }
	});
	
	$("#technician3").html(technicianOptionList);
	$("#technician3").dropdown('refresh');
}

function getTechListForTech4(){
	
	var technicianOptionList = '<option value=""></option>';
	
	$.ajax({
	    type: "GET",
	    url:'/admin-portal/control/getContractorTechnicians?externalLoginKey=${requestAttributes.externalLoginKey!}',
	    async: false,
	    success: function(data) {
	    	var technician4 = '${inputContext.technician2!}';
	    	for (var i = 0; i < data.length; i++) {
	            var type = data[i];
	            if(technician4 && technician4 === type.technicianId)
	            	technicianOptionList += '<option value="'+type.technicianId+'" selected>'+type.name+'</option>';
	            else
	            	technicianOptionList += '<option value="'+type.technicianId+'">'+type.name+'</option>';
	        }
	    }
	});
	
	$("#technician4").html(technicianOptionList);
	$("#technician4").dropdown('refresh');
}  -->

</script>
