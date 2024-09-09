<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>

<div class="row">
    <div id="main" role="main" class="pd-btm-title-bar">
        <#-- <@sectionFrameHeader title="Find Accounts"/> -->
        
        <#--<div class="col-lg-12 col-md-12 col-sm-12">
            
             Basic information 
             <div class="card-header mt-3" id="cp">
                <@dynaScreen 
	                instanceId="ACTIVITY_BASIC"
	                modeOfAction="VIEW"
	                />
            </div> -->
  <div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
    <div>
        <div class="row">
            <div class="col-lg-12 col-md-12 col-sm-12">
                <div class="text-left float-left">
                    <h3 class="float-left"> View Activity : ${inputContext.workEffortId!}</h3>
                </div>
            </div>
            <div class="text-right position-absolute" style="right:20px;">
            <input type='hidden' id="workEffortId" name="workEffortId" value="${requestParameters.workEffortId?if_exists}" />
            <input type='hidden' id="currentStatusId" name="workEffortId" value="${requestParameters.currentStatusId?if_exists}" />
                <#--<button type="button" class="btn btn-primary btn-xs mt-1" id="assign"><i class="fa fa-save" aria-hidden="true"></i>Assign</button>-->
                <#--<button type="button" class="btn btn-primary btn-xs mt-1" id="doSave"><i class="fa fa-save" aria-hidden="true"></i>&nbsp;&nbsp;Save</button>-->
                <#if inputContext.currentStatusId == "IA_OPEN" || inputContext.currentStatusId == "IA_MIN_PROGRESS" >
                <button type="button" class="btn btn-xs btn-primary" id="doCancel"><i class="fa fa-save" aria-hidden="true"></i>&nbsp;&nbsp;Mark Complete</button>
                </#if>
                <a href="/activity-portal/control/findActivity" class="btn btn-xs btn-primary back-btn">
        <i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>
            </div>
        </div>
    </div>
</div>    
<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
    <div class="row">
        <div class="col-md-4">
            <h6>Activity : Information</h6>
            <@headerH3 id="test3" title="" />
        </div>
        <div class="col-md-8 right-details">
           
            <div class="bd-callout">
                <small>Activity Type</small>
                <span class="text-danger"></span>
                <h5 id="workEffortServiceTypeDescription2">${inputContext.typeDesc!}</h5>
            </div>
            <div class="bd-callout">
                <small>Priority</small>
                <span class="text-danger"></span>
                <h5 id="priority">${inputContext.priorityDesc!}</h5>
            </div>
        </div>
    </div>
</div>            
         <div class="col-lg-12 col-md-12 col-sm-12">
               <#include "component://activity-portal/webapp/activity-portal/activity/tab_menu.ftl"/>
            <div class="tab-content">
                <div id="details" class="tab-pane fade active show">
                    ${screens.render("component://activity-portal/widget/activity/ActivityScreens.xml#ViewActivityDetail")}
                </div>
            </div>
        </div>
    </div>
</div>
</div>
<script>
$(document).ready(function(){
	$("#ts-priority").html($("#priority").text());
	
});
</script>
<script>
    
    $("#doCancel").click(function(event) {
    	event.preventDefault();	   
    	canceldetails();
    });
    
    
    function canceldetails(){
    	var workEffortId = $('#workEffortId').val();
        var currentStatusId = $('#currentStatusId').val();
        $.ajax({
    		url:'closedServiceActivityDetails',
    		data:{"workEffortId":workEffortId,"currentStatusId":currentStatusId},
    		type:"post",
    		success:function(data){	
    	 		showAlert("success", " Activity Closed Successfully");
    	 		loadActivity();
    	 		location.reload();
         		return data;
    		},
    		error:function(data){
    			console.log("dataerror====",data);
    			return data;
    		}
    	}); 
    }
   function loadActivity(){
    	var workEffortId =$("#workEffortId").val();
        dataSet = {"workEffortId":workEffortId};
        $.ajax({
    	    url:'getActivityDetails',
    	    data:dataSet,
    	    type:"post",
    	    success:function(data){
    		   
    		    var workEffortId1=data[0].workEffortId;
    		   
    		    var workEffortServiceTypeDescription=data[0].workEffortServiceTypeDescription;
    		    var workEffortSubServiceTypeDescription=data[0].workEffortSubServiceTypeDescription;
    		    var priority=data[0].priority;
    		    var currentStatusId=data[0].currentStatusId;
    		   
    		    if(workEffortId1!=null){
    		  	 	document.getElementById("test3").innerHTML=workEffortId1;
    		   	}else{
    		   		document.getElementById("test3").innerHTML="--";
    		   	}
    		    
    		    
    		   	if(priority!=null){
    		  	 	document.getElementById("priority").innerHTML=priority;
    		   	}else{
    		   		document.getElementById("priority").innerHTML="--";
    		   	}
    		   	
    		   	
    		   	if(workEffortServiceTypeDescription!=null){
    		  	 	document.getElementById("workEffortServiceTypeDescription2").innerHTML=workEffortServiceTypeDescription;
    		   	}else{		   		
    		   		document.getElementById("workEffortServiceTypeDescription2").innerHTML="--";		   		
    		   	}
    		   	if(workEffortSubServiceTypeDescription!=null){
    		  	 	document.getElementById("workEffortSubServiceTypeDescription2").innerHTML=workEffortSubServiceTypeDescription;
    		   	}else{
    		   		document.getElementById("workEffortSubServiceTypeDescription2").innerHTML="--";
    		   	}
    		   	if(currentStatusId!=null){
    		  	 	$(".currentStatusId [data-value='" + currentStatusId +"']").click();
    		   	}
    		  
    		   	if(currentStatusId=="IA_MCOMPLETED"){
    		   	$("#currentStatusId").empty();
    		   	document.getElementById('assign').setAttribute('disabled', true);
    	   	    document.getElementById('doSave').setAttribute('disabled', true);
    	   	    document.getElementById('doCancel').setAttribute('disabled', true);
    		   	}
    		   		    
    		}
      	});
    } 
   
</script>