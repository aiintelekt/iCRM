<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
<#include "component://homeapps/webapp/homeapps/user-audit/audit_page_header.ftl"/>

<div class="row">
    <div id="main" role="main">
        <#assign extra='<a href="/admin-portal/control/viewSlaSetup?slaConfigId=${inputContext.slaConfigId!}" class="btn btn-xs btn-primary">
        <i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>' />

        <div class="clearfix"></div>
        
         <div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
        
        <@sectionFrameHeaderTab title="${uiLabelMap.UpdateSlaSetup!}" tabId="UpdateSlaSetup" extra=extra/> 
         
        <form method="post" action="<@ofbizUrl>updateSlaSetupAction</@ofbizUrl>" data-toggle="validator"> 
        
        
        	<input type="hidden" name="slaConfigId" value="${inputContext.slaConfigId!}">
        	<input type="hidden" id="isSlaReq" name="isSlaReq" value="${isSlaReq!}">
        	<@inputHidden  id="srExisting" value="${srSubCategoryId?if_exists}" />  
            <div class="col-lg-12 col-md-12 col-sm-12 ">
            	
            	<@dynaScreen 
					instanceId="PARAM_SLA_STP"
					modeOfAction="UPDATE"
					/>
            	
            </div>
            
            <div class="col-lg-12 col-md-12 col-sm-12 " id="sla-variation">
            	
            	<@dynaScreen 
					instanceId="PARAM_SLA_STP_VRT"
					modeOfAction="UPDATE"
					/>
            	
            </div>
            
            <div class="clearfix"></div>
            <div class="offset-md-2 col-sm-10 pad-10">
            
            <@submit label="${uiLabelMap.Save}"/>
            <@cancel label="Cancel" onclick="/admin-portal/control/viewSlaSetup?slaConfigId=${inputContext.slaConfigId!}"/>
         	
            </div>
        </form>
        </div>
    </div>
</div>


<script>

$(document).ready(function() {
	
	$("#srResolutionUnit_label").html('');
	$("#srResolutionUnit_label").html('SLA for SR Resolution'+'<span class="list-unstyled text-danger">*</span>');
	$("#slaSrResolution_label").html('');
	$("#slaSrResolution_label").html('SR Resolution Unit'+'<span class="list-unstyled text-danger">*</span>');
	
	$("#srResolutionUnit").attr("required","required");
	if($("input[name^='slaSrResolution']" ).length){
		$("input[name^='slaSrResolution']" ).each(function(){
			$(this).attr("required","required");
		});	
	}		
	//$("#slaSrResolution").attr("required","required");
	if($("#isSlaReq").val()=="N"){
		$("#sla-variation").css("display","none");
    	$("#srResolutionUnit").removeAttr("required");
    	$("#slaSrResolution").removeAttr("required");
	}
	$('input[type=radio][name=isSlaRequired]').change(function() {
	    if ($(this).val() == "Y") {
	    	$("#sla-variation").show();	    	
	    	$("#srResolutionUnit").attr("required","required");
	    	$("#slaSrResolution").attr("required","required");
	    	
	    } else {
	    	//$("#sla-variation").hide();
	    	$("#sla-variation").css("display","none");
	    	$("#srResolutionUnit").removeAttr("required");
	    	$("#slaSrResolution").removeAttr("required");
	    	
	    }
	});




/*
$("#srTypeId").change(function() {
	
	var nonSelectContent = "<span class='nonselect'>Select SR Category</span>";
	var nameOptions = '<option value="" data-content="'+nonSelectContent+'" selected="">Select SR Category</option>';		
			
	$.ajax({
		type: "POST",
     	url: "/admin-portal/control/getSrCategories",
        data: {"srTypeId": $(this).val(),"externalLoginKey": "${requestAttributes.externalLoginKey!}"},
        async: false,
        success: function (data) {   
        	for (var i = 0; i < data.length; i++) {
        		var entry = data[i];
        		nameOptions += '<option value="'+entry.srCategoryId+'">'+entry.srCategoryDesc+'</option>';
        	}
        }
        
	});    
	
	$("#srCategoryId").html( nameOptions );
	$("#srCategoryId").val("${inputContext.srCategoryId!}");
	$("#srCategoryId").dropdown('refresh');
	$("#srCategoryId").trigger('change');
});
*/
	loadSubCategory($("#srCategoryId").val());
	$("#srCategoryId").change(function() {
		   var srCategoryId  = $(this).val();
		   $("#srSubCategoryId").dropdown('clear');
		  
		   if (srCategoryId != "") {
		   		
		       loadSubCategory(srCategoryId);
		   }else{			   
			   $("#srSubCategoryId").html('');	
			   $("#srSubCategoryId").dropdown('clear');
		   }
		});
	function loadSubCategory(srCategoryId) {
		 $("#srSubCategoryId").dropdown('clear');
		var nonSelectContent = "<span class='nonselect'>Please Select</span>";
		var subCategoryOptions = '<option value="" data-content="'+nonSelectContent+'" selected="">Please Select</option>';
		var srSubCategoryId = $("#srExisting").val();
		
		$.ajax({
			type: "POST",
	     	url: "/admin-portal/control/getSrSubCategories",
	        data: {"srCategoryId": srCategoryId},
	        async: false,
	        success: function (data) {   
	        	for (var i = 0; i < data.length; i++) {
	        		var entry = data[i];	        		
	        		if(srSubCategoryId && srSubCategoryId == entry.srSubCategoryId){
	        			subCategoryOptions += '<option value="'+entry.srSubCategoryId+'" selected="selected" >'+entry.srSubCategoryDesc+'</option>';
	         		}else{
	         			subCategoryOptions += '<option value="'+entry.srSubCategoryId+'">'+entry.srSubCategoryDesc+'</option>';
	         		}
	        	}
	        }
	        
		});
		$("#srSubCategoryId").html(subCategoryOptions);
		 //$("#srSubCategoryId").dropdown('clear');
	}
	
	
	/*$("#srCategoryId").change(function() {	
		
		if($(this).val()!=""){		
			var nonSelectContent = "<span class='nonselect'>Select SR Sub Category</span>";
			var nameOptions = '<option value="" data-content="'+nonSelectContent+'" selected="">Select SR Sub Category</option>';		
					
			$.ajax({
				type: "POST",
		     	url: "/admin-portal/control/getSrSubCategories",
		        data: {"srCategoryId": $(this).val()},
		        async: false,
		        success: function (data) {   
		        	for (var i = 0; i < data.length; i++) {
		        		var entry = data[i];
		        		if(srSubCategoryId && srSubCategoryId === entry.srSubCategoryId){
		        			nameOptions += '<option value="'+entry.srSubCategoryId+'" selected="selected" >'+entry.srSubCategoryDesc+'</option>';
		         		}else{
		         			nameOptions += '<option value="'+entry.srSubCategoryId+'">'+entry.srSubCategoryDesc+'</option>';
		         		}
		        	}
		        }
		        
			});    
			
			$("#srSubCategoryId").html(nameOptions);	
			$("#srSubCategoryId").dropdown('refresh');
		}else{
			$("#srSubCategoryId").html('');	
			$("#srSubCategoryId").dropdown('refresh');
		}
	});*/

//$("#srTypeId").trigger('change');

});

</script>