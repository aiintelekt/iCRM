<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://homeapps/webapp/homeapps/user-audit/audit_page_header.ftl"/>

      <div class="row">
        <div id="main" role="main">
          <#assign extra='<a href="viewActivitySubTypes?enumId=${inputContext.activitySubTypeId!!}" class="btn btn-xs btn-primary"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>' />
         <@sectionFrameHeader title="${uiLabelMap.UpdateActivitySubType!}" extra=extra />

<form id="mainFrom" action ="<@ofbizUrl>updateActivitySubType</@ofbizUrl>" method="post">

<#include "component://homeapps/webapp/homeapps/user-audit/audit_from_header.ftl"/>

<input type="hidden" id="enumId" name="enumId" value="${inputContext.activitySubTypeId!}"/>

          <div class="col-md-12 col-lg-12 col-sm-12 ">
          
            <input type=hidden name="pe" id="pe" value="${inputContext.activityType!}"/>
            <@inputHidden    
                          id="activitySubTypeId"
                          name="activitySubTypeId"
                           value=inputContext.activitySubTypeId!
                        />
                        
              <@dynaScreen 
				instanceId="PARAM_ACT_SUB_TYP"
				modeOfAction="UPDATE"
				/>            
               
          </div>
          <div class="form-group offset-2">
            <div class="text-left ml-3">
                  <input type="submit" class="btn btn-sm btn-primary" onclick="return formSubmission();" value="Update">
               <a href="viewActivitySubTypes?enumId=${inputContext.activitySubTypeId!}"class="btn btn-sm btn-secondary"> Cancel</a>   
            </div>
          </div>
          </form>
        </div>
      </div>
      
<script>

function formSubmission(){
    var isValid = "Y";
    var activityType =  $("#activityType").val();
    var activitySubType =  $("#activitySubType").val();
    var status =  $("#status").val();
    var sequenceNumber =  $("#sequenceNumber").val();
    var seqerr="";
    if(activityType!=''&& status!=''&& activitySubType!=''){
         if(sequenceNumber != "")
       {
            var exp = new RegExp("^[0-9]{0,10}$");
            var seqDigit = $("#sequenceNumber").val();
                if (exp.test(seqDigit)) {
                    $("#sequenceNumber_error").empty();
                 }else {
                    $("#sequenceNumber_error").empty();
                    $("#sequenceNumber_error").append('<ul class="list-unstyled text-danger"><li>Please enter only numeric</li></ul>');
                    seqerr = "Y";
                    }
        }
       if( seqerr!="")
            return false;
        else
            return true;
    }
    else{
       if(status == "") {
          $("#status_error").html('');
          $("#status_error").append('<ul class="list-unstyled text-danger"><li id="status_err">Please select Status </li></ul>');
           isValid = "N";
        }
        if(activityType == "") {
          $("#activityType_error").html('');
          $("#activityType_error").append('<ul class="list-unstyled text-danger"><li id="status_err">Please select Activity Type</li></ul>');
           isValid = "N";
        }
        if(activitySubType == "") {
          $("#activitySubType_error").html('');
          $("#activitySubType_error").append('<ul class="list-unstyled text-danger"><li id="status_err">Please enter Activity Sub Type</li></ul>');
           isValid = "N";
        }
       if(sequenceNumber != "")
       {
            var exp = new RegExp("^[0-9]{0,10}$");
            var seqDigit = $("#sequenceNumber").val();
                if (exp.test(seqDigit)) {
                    $("#sequenceNumber_error").empty();
                 }else {
                    $("#sequenceNumber_error").empty();
                    $("#sequenceNumber_error").append('<ul class="list-unstyled text-danger"><li>Please enter only numeric</li></ul>');
                    seqerr = "Y";
                    }
        }
        if(isValid == "N"){
          return false;
        } 
        else if(isValid == "Y")
        {
             if( seqerr!="")
                return false;
            else
                return true;
        }
    }
}

$("#sequenceNumber").keyup(function() {
   var exp = new RegExp("^[0-9]{0,10}$");
   var seqDigit = $("#sequenceNumber").val();
    if (exp.test(seqDigit)) {
           $("#sequenceNumber_error").empty();
      } else if(seqDigit==''){
           $("#sequenceNumber_error").empty();
      }else {
           $("#sequenceNumber_error").empty();
           $("#sequenceNumber_error").append('<ul class="list-unstyled text-danger"><li>Please enter only numeric.</li></ul>');
      }
});    
$("#activityType").change(function(){  
    if($("#activityType").val() != null){
    $("#activityType_error").html("");  
    }
}); 
$("#activitySubType").keyup(function(){  
    if($("#activitySubType").val() != null){
    $("#activitySubType_error").html("");  
    }
});
$("#status").change(function(){  
    if($("#status").val() != null){
    $("#status_error").html("");  
}
});

$('#activityParent').change(function(){
    var selectedItem = $(this).val();
    $('#activityType').val('');
    var dropDown = document.getElementById("activityType");  
        dropDown.selectedIndex = 0;
    
    $.post("getTypeList",{"activityParent":selectedItem},function(data){
    
            var len = data.length;
            var options = '<option value="" data-content="<span>Please Select</span>" selected>Please Select</option>';
            if(data !=null && data !=""){
               for(var i=0;i<data.length;i++){
                     options += '<option value="'+data[i].code+'">'+data[i].description+'</option>';
               }
            }
                $("#activityType").empty();
                $("#activityType").append(options);
               $("#activityType").dropdown("refresh");
               $('#activityType').trigger("chosen:updated");
                
          });
})
    $( document ).ready(function() {
     getCategoryFunction();
     });
   function getCategoryFunction() 
    {
      var first = document.getElementById("pe").value;
      var selectedItem = $(activityParent).val();
    $.post("getSubTypeList",{"activityParent":selectedItem},function(data){
            var len = data.length;
            var options = '<option value="" data-content="<span>Please Select</span>" selected>Please Select</option>';
            if(data !=null && data !=""){
               for(var i=0;i<data.length;i++){
               if(first==data[i].code)
                   {
                    options += '<option value="'+data[i].code+'" selected>'+data[i].description+'</option>';
                   } else{
                  options += '<option value="'+data[i].code+'">'+data[i].description+'</option>';
                   }
               }
            }
                $("#activityType").append(options);
                $("#activityType").dropdown("refresh");
          });
}
   </script> 
 </script>
<#include "component://homeapps/webapp/homeapps/user-audit/audit_footer.ftl"/>
      