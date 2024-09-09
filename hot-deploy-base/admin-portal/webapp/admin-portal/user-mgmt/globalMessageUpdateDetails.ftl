l <#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/> 

	
  <div class="row">
        <div id="main" role="main">
        <#--  <#assign extra='<a href="oppResponseReason" class="btn btn-xs btn-primary"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>' />-->
         <@sectionFrameHeader title="${uiLabelMap.updateGlobalMessage!}" extra=extra />
         
<form id="mainFrom" action ="<@ofbizUrl>processGlobalMessageUpdate</@ofbizUrl>" method="post">

<#include "component://homeapps/webapp/homeapps/user-audit/audit_from_header.ftl"/>
		 <div class="col-md-10 col-lg-6 col-sm-10">
                        
         <@dropdownCell
         id="componentId"
         label=uiLabelMap.Component
         placeholder="Select Component"
         required=true
         options=componentData!    
		  allowEmpty=true        
		  value="${requestParameters.componentId?if_exists}"  
         />
        </div> 
        
        
        <div class="col-md-10 col-lg-6 col-sm-10">
                        
                          <@dropdownCell
	                        id="roleTypeId" 
	                        label=uiLabelMap.messageType
	                        name="roleTypeId" 
	                        placeholder="Select Message Type"      
	                        allowEmpty=true   
	                        options=msgTypeData!
	                        required = true
	                        value="${requestParameters.roleTypeId?if_exists}"
                        /> 
                       </div>  
          <#assign userDetails = Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("userLoginId","firstName").from("UserLoginPerson").where("enabled","Y","statusId","PARTY_ENABLED").queryList()?if_exists />    
                            <#assign usersOptionList = Static["org.fio.admin.portal.util.DataHelper"].getDropDownOptions(userDetails, "userLoginId", "firstName")?if_exists />
          <div class="col-md-10 col-lg-6 col-sm-10">
                        
                          <@dropdownCell
	                        id="partyId" 
	                        label=uiLabelMap.messageTo
	                        name="partyId" 
	                        placeholder="Select Message To"      
	                        allowEmpty=true   
	                        options=usersOptionList!
	                        required = true
	                        value="${requestParameters.partyId?if_exists}"
                        /> 
                       </div>
           <div class="col-md-10 col-lg-6 col-sm-10">
         
			<@inputArea
         		id="description"
         		label=uiLabelMap.Message
         		placeholder = uiLabelMap.Message
         		name="description"
         		maxlength="500"
         		required = true
         		 value="${requestParameters.description?if_exists}"
         	/>
          </div>
          
           <div class="col-md-10 col-lg-6 col-sm-10">
                  <@inputDate
                     id="fromDate"
                     label=uiLabelMap.startDateTime
                     placeholder="From Date"
                     type="datetime"
                     required = true
                      value="${requestParameters.fromDate?if_exists}"
                     />
                  </div>
                  
            <div class="col-md-10 col-lg-6 col-sm-10">
                  <@inputDate
                     id="thruDate"
                     label=uiLabelMap.endDateTime
                     placeholder="Thru Date"
                     type="datetime"
                      value="${requestParameters.thruDate?if_exists}"
                     />
             </div>        
              <div class="col-md-10 col-lg-6 col-sm-10">
                  <@dropdownCell
			         id="isEnabled"
			         label=uiLabelMap.isEnabled
			         placeholder="Please Select"
			         required=true
			         options=enableOptionsList!    
					  allowEmpty=true        
					  value="${requestParameters.isEnabled?if_exists}"  
			         />
             </div>    
                  
          <div class="form-group offset-2">
            <div class="text-left ml-3">
              
              <@formButton
                     btn1type="submit"
                     btn1label="${uiLabelMap.Save}"
                     btn1onclick="return formSubmission();"
                     <#-- btn2=true
                     btn2type="reset"
                     btn2label="${uiLabelMap.Clear}"-->
                   />
             
             	
            </div>
          </div>
          
           
                  
		  </form>
        </div>
      </div>

<script>
$(document).ready(function() {
$("#partyId").prepend('<option value="ALL">All Users</option>');
$("#isEnabled").append('<option value="Y">Yes</option>');
$("#isEnabled").append('<option value="N">No</option>');
});
$("#description").keyup(function() {
   var description = $("#description").val();
   $("#description_error").empty();
      
});
$("#roleTypeId").change(function() {
   var description = $("#roleTypeId").val();
   $("#roleTypeId_error").empty();
      
});
$("#fromDate").keyup(function() {
   var description = $("#fromDate").val();
   $("#fromDate_error").empty();
      
});
$("#componentId").change(function() {
   var description = $("#componentId").val();
   $("#componentId_error").empty();
      
});

function formSubmission(){
     var isValid = "Y";
    var description =  $("#description").val();
    var componentId =  $("#componentId").val();
    var roleTypeId =  $("#roleTypeId").val();
    var fromDate =  $("#fromDate").val();
    if(description!=''){
    }else{
        if(description == "") {
           $("#description_error").html('');
           $("#description_error").append('<ul class="list-unstyled text-danger"><li id="description_err"> Global Description </li></ul>');
           isValid = "N";
        }
        }
        if(componentId!=''){
    }else{
        if(componentId == "") {
           $("#componentId_error").html('');
           $("#componentId_error").append('<ul class="list-unstyled text-danger"><li id="componentId_err">Select Component</li></ul>');
           isValid = "N";
        }
         }
        if(roleTypeId!=''){
    }else{
        if(roleTypeId == "") {
           $("#roleTypeId_error").html('');
           $("#roleTypeId_error").append('<ul class="list-unstyled text-danger"><li id="roleTypeId_err">Select Message Type</li></ul>');
           isValid = "N";
        }
         }
        if(fromDate!=''){
    }else{
        if(fromDate == "") {
           $("#fromDate_error").html('');
           $("#fromDate_error").append('<ul class="list-unstyled text-danger"><li id="fromDate_err">Select From Date</li></ul>');
           isValid = "N";
        }
         }
        if(isValid == "N"){
          return false;
        } 
        else if(isValid == "Y")
        {
	        return true;
        } 
    }


       
</script>
   
   
<#include "component://homeapps/webapp/homeapps/user-audit/audit_footer.ftl"/>
  