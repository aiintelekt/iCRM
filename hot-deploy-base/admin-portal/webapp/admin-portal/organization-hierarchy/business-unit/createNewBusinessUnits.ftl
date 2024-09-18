<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
<div class="row">
    <div id="main" role="main">
        <#assign extra='<a href="/admin-portal/control/businessUnits" class="btn btn-xs btn-primary">
        <i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>' />
        <div class="clearfix"></div>
        <form method="post" action="<@ofbizUrl>businessUnitCreation</@ofbizUrl>" class="form-horizontal" name="createBusinessUnits" id="createBusinessUnits" >
            <div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
        	
        	<@sectionFrameHeader title="${uiLabelMap.CreateBusinessUnit!}" extra=extra />
            
            	<@dynaScreen 
					instanceId="BU_BASE"
					modeOfAction="CREATE"
					/>
            	
                <h2 class="font-h2">Contact Information </h2>
                
                <@dynaScreen 
					instanceId="BU_CONTACT"
					modeOfAction="CREATE"
					/>
                
                <h2 class="font-h2">Address </h2>
                
                <@dynaScreen 
					instanceId="BU_ADDRESS"
					modeOfAction="CREATE"
					/>
                
	            <div class="offset-md-2 col-sm-10 pad-10 mr-3" id="create-buss-unit">
	         
	            <@formButton
	                     btn1type="submit"
	                     btn1label="${uiLabelMap.Save}"
	                     btn1onclick="return formSubmission();"
	                     btn2=true
	                     btn2onclick = "resetForm()"
	                     btn2type="reset"
	                     btn2label="${uiLabelMap.Clear}"
	                   />
	            </div>
	            
            </div>
            
            <div class="clearfix"></div>
            
            
        </form>
    </div>
</div>

<div id="parentBusinessUnit" class="modal fade mt-2" role="dialog">
    <div class="modal-dialog modal-lg" style="width: 640px;">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <h3 class="modal-title">Find Parent BU</h3>
                <button type="reset" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body">

	<#assign rightContent='
		<button id="refresh-bu-btn" type="button" class="btn btn-xs btn-primary m5"><i class="fa fa-refresh" aria-hidden="true"></i></button>
		' />
    <input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
  	<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

		<@fioGrid 
			id="parentBu-grid"
			instanceId="PARENT_BU"
			jsLoc="/admin-portal-resource/js/ag-grid/org-Hierarchy/bu-parent.js"
			headerLabel=""
			headerId="parentBu-grid-action-container"
			headerBarClass="grid-header-no-bar"
			savePrefBtn=false
			clearFilterBtn=false
			exportBtn=true
			subFltrClearBtn = false
			headerExtra=rightContent!
			savePrefBtnId="parentBu-save-pref-btn"
			clearFilterBtnId="parentBu-clear-filter-btn"
			subFltrClearId="parentBu-sub-filter-clear-btn"
			exportBtnId="parentBu-export-btn"
			/>
                 <#--<div class="table-responsive">
                <div class="loader text-center" id="loader" sytle="display:none;">
                  <span></span>
                  <span></span>
                  <span></span>
                </div>
                <div id="grid1" style="width: 100%;" class="ag-theme-balham"></div> -->
                  <#--  <@AgGrid 
                        userid="${userLogin.userLoginId}" 
                        instanceid="BU05" 
                        styledimensions='{"width":"100%","height":"80vh"}'
                        autosave="false"
                        autosizeallcol="true" 
                        debug="true"
                    /> --> 
                <#-- </div>
                  <script type="text/javascript" src="/admin-portal-resource/js/ag-grid/org-Hierarchy/bu-parent.js"></script> 
            </div> -->
            <#-- <div class="modal-footer">
                <!-- <button type="submit" class="btn btn-sm btn-primary" data-dismiss="modal" id="addParentBu" name="addParentBu" >Add</button>-->
            <#--</div>  -->
        </div>
    </div>
</div>

<script>

$(document).ready(function() {
	getDependentDropdownValues('getAssociatedStateList', 'countryGeoId', 'countryOrRegion', 'stateOrProvince', 'stateList', 'geoId', 'geoName','${stateValue!}');
});

function formSubmission(){
    var isValid = "Y";
    var moberr="";
    var phoneerr="";
    var weberr="";
    var emailerr="";
    var buerr="";
    var ziperr="";
    var url1err="";
    var url2err="";
    var buName =  $("#buName").val();
    var parentBuId =  $("#parentBuId_desc").val();
    var status =  $("#status").val();
    var buType =  $("#buType").val();
    var phone =  $("#phone").val();
    var buId =  $("#buId").val();
    var email =  $("#email").val();
    var website =  $("#website").val();
    var url1 =  $("#url1").val();
    var url2 =  $("#url2").val();
    var zipOrPostalCode =  $("#zipOrPostalCode").val();
    if(buName !='' && status !='' && buType!=''){
         if(mobile != "") {
              var exp = new RegExp("^[0-9]{0,22}$");
              var mobDigit = $("#mobile").val();
                if (exp.test(mobDigit)) {
                $("#mobile_error").empty();
               // isValid = "Y";
                } else {
                 $("#mobile_error").empty();
                    $("#mobile_error").append('<ul class="list-unstyled text-danger"><li>Please enter valid Mobile Number</li></ul>');
                     moberr = "Y";
                }
        }
        if(phone != "") {
             var re = new RegExp("^[0-9]{0,22}$");
            var phoneDigit = $("#phone").val();
            if (re.test(phoneDigit)) {
                    $("#phone_error").empty();
            } else {
                    $("#phone_error").empty();
                    $("#phone_error").append('<ul class="list-unstyled text-danger"><li>Please enter valid Phone Number</li></ul>');
                   phoneerr= "Y";
            }
        }
        if(buId != "") {
              var exp = new RegExp("^[a-zA-Z0-9 ]+$");
              var mobDigit = $("#buId").val();
                if (exp.test(mobDigit)) {
                $("#buId_error").empty();
                } else {
                 $("#buId_error").empty();
                    $("#buId_error").append('<ul class="list-unstyled text-danger"><li>Please enter valid BU ID</li></ul>');
                  buerr = "Y";
                }
        }
         if(email != "") {
             var up = /^[A-Z0-9._%+-]+@([A-Z0-9-]+\.)+[A-Z]{2,4}$/i;
              var mobDigit = $("#email").val();
                if (up.test(mobDigit)) {
                $("#email_error").empty();
                } else {
                 $("#email_error").empty();
                    $("#email_error").append('<ul class="list-unstyled text-danger"><li>Please enter valid Email</li></ul>');
                  emailerr= "Y";
                }
        }
        if(website != "") {
            var em = /^(?:(?:https?|ftp):\/\/)?(?:(?!(?:10|127)(?:\.\d{1,3}){3})(?!(?:169\.254|192\.168)(?:\.\d{1,3}){2})(?!172\.(?:1[6-9]|2\d|3[0-1])(?:\.\d{1,3}){2})(?:[1-9]\d?|1\d\d|2[01]\d|22[0-3])(?:\.(?:1?\d{1,2}|2[0-4]\d|25[0-5])){2}(?:\.(?:[1-9]\d?|1\d\d|2[0-4]\d|25[0-4]))|(?:(?:[a-z\u00a1-\uffff0-9]-*)*[a-z\u00a1-\uffff0-9]+)(?:\.(?:[a-z\u00a1-\uffff0-9]-*)*[a-z\u00a1-\uffff0-9]+)*(?:\.(?:[a-z\u00a1-\uffff]{2,})))(?::\d{2,5})?(?:\/\S*)?$/;
            var web = $("#website").val();
            if (em.test(web)) {
                $("#website_error").empty();
            } else {
                $("#website_error").empty();
                $("#website_error").append('<ul class="list-unstyled text-danger"><li>Please enter valid Website</li></ul>');
                weberr= "Y";
            }
        }
        if(url1 != "") {
            var em = /^(?:(?:https?|ftp):\/\/)?(?:(?!(?:10|127)(?:\.\d{1,3}){3})(?!(?:169\.254|192\.168)(?:\.\d{1,3}){2})(?!172\.(?:1[6-9]|2\d|3[0-1])(?:\.\d{1,3}){2})(?:[1-9]\d?|1\d\d|2[01]\d|22[0-3])(?:\.(?:1?\d{1,2}|2[0-4]\d|25[0-5])){2}(?:\.(?:[1-9]\d?|1\d\d|2[0-4]\d|25[0-4]))|(?:(?:[a-z\u00a1-\uffff0-9]-*)*[a-z\u00a1-\uffff0-9]+)(?:\.(?:[a-z\u00a1-\uffff0-9]-*)*[a-z\u00a1-\uffff0-9]+)*(?:\.(?:[a-z\u00a1-\uffff]{2,})))(?::\d{2,5})?(?:\/\S*)?$/;
            var url1 = $("#url1").val();
            if (em.test(url1)) {
                $("#url1_error").empty();
            } else {
                $("#url1_error").empty();
                $("#url1_error").append('<ul class="list-unstyled text-danger"><li>Please enter valid url</li></ul>');
                url1err= "Y";
            }
        }
        if(url2 != "") {
            var em = /^(?:(?:https?|ftp):\/\/)?(?:(?!(?:10|127)(?:\.\d{1,3}){3})(?!(?:169\.254|192\.168)(?:\.\d{1,3}){2})(?!172\.(?:1[6-9]|2\d|3[0-1])(?:\.\d{1,3}){2})(?:[1-9]\d?|1\d\d|2[01]\d|22[0-3])(?:\.(?:1?\d{1,2}|2[0-4]\d|25[0-5])){2}(?:\.(?:[1-9]\d?|1\d\d|2[0-4]\d|25[0-4]))|(?:(?:[a-z\u00a1-\uffff0-9]-*)*[a-z\u00a1-\uffff0-9]+)(?:\.(?:[a-z\u00a1-\uffff0-9]-*)*[a-z\u00a1-\uffff0-9]+)*(?:\.(?:[a-z\u00a1-\uffff]{2,})))(?::\d{2,5})?(?:\/\S*)?$/;
            var url2 = $("#url2").val();
            if (em.test(url2)) {
                $("#url2_error").empty();
            } else {
                $("#url2_error").empty();
                $("#url2_error").append('<ul class="list-unstyled text-danger"><li>Please enter valid url</li></ul>');
                url2err= "Y";
            }
        }
        if(zipOrPostalCode != "") {
            var exp = new RegExp("^[0-9]{0,10}$");
            var zip = $("#zipOrPostalCode").val();
            if (exp.test(zip)) {
                $("#zipOrPostalCode_error").empty();
            } else {
                $("#zipOrPostalCode_error").empty();
                $("#zipOrPostalCode_error").append('<ul class="list-unstyled text-danger"><li>Please enter valid ZIP/Postal Code</li></ul>');
                var ziperr="Y";
            }
        }
        if( emailerr!="" ||  buerr!="" ||  phoneerr!="" || moberr!="" || weberr!="" || ziperr!="" )
            return false;
        else
            return true;
    }
    else{
        if(buName == "") {
           $("#buName_error").html('');
           $("#buName_error").append('<ul class="list-unstyled text-danger"><li id="srTypeId_err">Please enter BU Name</li></ul>');
            isValid = "N";
        }
       /* if(parentBuId == "") {
           $("#parentBuId_error").html('');
           $("#parentBuId_error").append('<ul class="list-unstyled text-danger"><li id="srTypeId_err">Please select parent BU</li></ul>');
            isValid = "N";
        }*/
        if(status == "") {
          $("#status_error").html('');
          $("#status_error").append('<ul class="list-unstyled text-danger"><li id="srCategoryId_err">Please select BU Status</li></ul>');
           isValid = "N";
        }
        if(buType == "") {
            $("#buType_error").html('');
            $("#buType_error").append('<ul class="list-unstyled text-danger"><li id="srSubCategoryId_err">Please select BU Type</li></ul>');
             isValid = "N";
        }
         if(mobile != "") {
              var exp = new RegExp("^[0-9]{0,22}$");
              var mobDigit = $("#mobile").val();
                if (exp.test(mobDigit)) {
                $("#mobile_error").empty();
               // isValid = "Y";
                } else {
                 $("#mobile_error").empty();
                    $("#mobile_error").append('<ul class="list-unstyled text-danger"><li>Please enter valid Mobile Number</li></ul>');
                     moberr = "Y";
                }
        }
        if(phone != "") {
             var re = new RegExp("^[0-9]{0,22}$");
            var phoneDigit = $("#phone").val();
            if (re.test(phoneDigit)) {
                    $("#phone_error").empty();
            } else {
                    $("#phone_error").empty();
                    $("#phone_error").append('<ul class="list-unstyled text-danger"><li>Please enter valid Phone Number</li></ul>');
                    phoneerr = "Y";
            }
        }
        <#--if(buId != "") {
              var exp = new RegExp("^[0-9]{0,25}$");
              var mobDigit = $("#buId").val();
                if (exp.test(mobDigit)) {
                $("#buId_error").empty();
                } else {
                 $("#buId_error").empty();
                    $("#buId_error").append('<ul class="list-unstyled text-danger"><li>Please enter valid BU ID</li></ul>');
                     buerr = "Y";
                }
        }-->
        if(email != "") {
              var up = /^[A-Z0-9._%+-]+@([A-Z0-9-]+\.)+[A-Z]{2,4}$/i;
              var mobDigit = $("#email").val();
                if (up.test(mobDigit)) {
                $("#email_error").empty();
                } else {
                 $("#email_error").empty();
                    $("#email_error").append('<ul class="list-unstyled text-danger"><li>Please enter valid Email</li></ul>');
                     emailerr= "Y";
                }
        }
        if(website != "") {
            var em = /^(?:(?:https?|ftp):\/\/)?(?:(?!(?:10|127)(?:\.\d{1,3}){3})(?!(?:169\.254|192\.168)(?:\.\d{1,3}){2})(?!172\.(?:1[6-9]|2\d|3[0-1])(?:\.\d{1,3}){2})(?:[1-9]\d?|1\d\d|2[01]\d|22[0-3])(?:\.(?:1?\d{1,2}|2[0-4]\d|25[0-5])){2}(?:\.(?:[1-9]\d?|1\d\d|2[0-4]\d|25[0-4]))|(?:(?:[a-z\u00a1-\uffff0-9]-*)*[a-z\u00a1-\uffff0-9]+)(?:\.(?:[a-z\u00a1-\uffff0-9]-*)*[a-z\u00a1-\uffff0-9]+)*(?:\.(?:[a-z\u00a1-\uffff]{2,})))(?::\d{2,5})?(?:\/\S*)?$/;
            var web = $("#website").val();
            if (em.test(web)) {
                $("#website_error").empty();
            } else {
                $("#website_error").empty();
                $("#website_error").append('<ul class="list-unstyled text-danger"><li>Please enter valid Website</li></ul>');
                weberr= "Y";
            }
        }
         if(url1 != "") {
            var em = /^(?:(?:https?|ftp):\/\/)?(?:(?!(?:10|127)(?:\.\d{1,3}){3})(?!(?:169\.254|192\.168)(?:\.\d{1,3}){2})(?!172\.(?:1[6-9]|2\d|3[0-1])(?:\.\d{1,3}){2})(?:[1-9]\d?|1\d\d|2[01]\d|22[0-3])(?:\.(?:1?\d{1,2}|2[0-4]\d|25[0-5])){2}(?:\.(?:[1-9]\d?|1\d\d|2[0-4]\d|25[0-4]))|(?:(?:[a-z\u00a1-\uffff0-9]-*)*[a-z\u00a1-\uffff0-9]+)(?:\.(?:[a-z\u00a1-\uffff0-9]-*)*[a-z\u00a1-\uffff0-9]+)*(?:\.(?:[a-z\u00a1-\uffff]{2,})))(?::\d{2,5})?(?:\/\S*)?$/;
            var url1 = $("#url1").val();
            if (em.test(url1)) {
                $("#url1_error").empty();
            } else {
                $("#url1_error").empty();
                $("#url1_error").append('<ul class="list-unstyled text-danger"><li>Please enter valid url</li></ul>');
                url1err= "Y";
            }
        }
        if(url2 != "") {
            var em = /^(?:(?:https?|ftp):\/\/)?(?:(?!(?:10|127)(?:\.\d{1,3}){3})(?!(?:169\.254|192\.168)(?:\.\d{1,3}){2})(?!172\.(?:1[6-9]|2\d|3[0-1])(?:\.\d{1,3}){2})(?:[1-9]\d?|1\d\d|2[01]\d|22[0-3])(?:\.(?:1?\d{1,2}|2[0-4]\d|25[0-5])){2}(?:\.(?:[1-9]\d?|1\d\d|2[0-4]\d|25[0-4]))|(?:(?:[a-z\u00a1-\uffff0-9]-*)*[a-z\u00a1-\uffff0-9]+)(?:\.(?:[a-z\u00a1-\uffff0-9]-*)*[a-z\u00a1-\uffff0-9]+)*(?:\.(?:[a-z\u00a1-\uffff]{2,})))(?::\d{2,5})?(?:\/\S*)?$/;
            var url2 = $("#url2").val();
            if (em.test(url2)) {
                $("#url2_error").empty();
            } else {
                $("#url2_error").empty();
                $("#url2_error").append('<ul class="list-unstyled text-danger"><li>Please enter valid url</li></ul>');
                url2err= "Y";
            }
        }
        if(zipOrPostalCode != "") {
            var exp = new RegExp("^[0-9]{0,10}$");
            var zip = $("#zipOrPostalCode").val();
            if (exp.test(zip)) {
                $("#zipOrPostalCode_error").empty();
            } else {
                $("#zipOrPostalCode_error").empty();
                $("#zipOrPostalCode_error").append('<ul class="list-unstyled text-danger"><li>Please enter valid ZIP/Postal Code</li></ul>');
                var ziperr="Y";
            }
        }
       if(isValid == "N"){
          return false;
        }  
        else if(isValid == "Y")
        {
             if( emailerr!="" ||  buerr!="" ||  phoneerr!="" || moberr!="" || weberr!="" || ziperr!="")
                return false;
            else
                return true;
        }
    }
}
$("#buName").keyup(function(){  
    if($("#buName").val() != null){
    $("#buName_error").html("");  
    }
}); 

/*$("#parentBuId_desc").change(function(){  
    if($("#parentBuId_desc").val() != null){
    $("#parentBuId_error").html("");  
    }
}); */
$("#status").change(function() {
   var status = $("#status").val();
   $("#status_error").empty();
});  
$("#buType").change(function() {
   var buType = $("#status").val();
   $("#buType_error").empty();	
}); 
$("#phone").keyup(function() {
   var re = new RegExp("^[0-9]{0,15}$");
   var phoneDigit = $("#phone").val();
    if (re.test(phoneDigit)) {
           $("#phone_error").empty();
      } else {
           $("#phone_error").empty();
           $("#phone_error").append('<ul class="list-unstyled text-danger"><li>Please enter valid Phone Number</li></ul>');
      }
});  
$("#mobile").keyup(function() {
   var exp = new RegExp("^[0-9]{0,15}$");
   var mobDigit = $("#mobile").val();
    if (exp.test(mobDigit)) {
           $("#mobile_error").empty();
      } else {
           $("#mobile_error").empty();
           $("#mobile_error").append('<ul class="list-unstyled text-danger"><li>Please enter valid Mobile Number</li></ul>');
      }
});   
$("#email").keyup(function() {
   var up = /^[A-Z0-9._%+-]+@([A-Z0-9-]+\.)+[A-Z]{2,4}$/i;
   var mail = $("#email").val();
    if (up.test(mail)) {
           $("#email_error").empty();
      } else if(mail==''){
           $("#email_error").empty();
      }else{
           $("#email_error").empty();
           $("#email_error").append('<ul class="list-unstyled text-danger"><li>Please enter valid Email</li></ul>');
      } 
});   
$("#website").keyup(function() {
   var em = /^(?:(?:https?|ftp):\/\/)?(?:(?!(?:10|127)(?:\.\d{1,3}){3})(?!(?:169\.254|192\.168)(?:\.\d{1,3}){2})(?!172\.(?:1[6-9]|2\d|3[0-1])(?:\.\d{1,3}){2})(?:[1-9]\d?|1\d\d|2[01]\d|22[0-3])(?:\.(?:1?\d{1,2}|2[0-4]\d|25[0-5])){2}(?:\.(?:[1-9]\d?|1\d\d|2[0-4]\d|25[0-4]))|(?:(?:[a-z\u00a1-\uffff0-9]-*)*[a-z\u00a1-\uffff0-9]+)(?:\.(?:[a-z\u00a1-\uffff0-9]-*)*[a-z\u00a1-\uffff0-9]+)*(?:\.(?:[a-z\u00a1-\uffff]{2,})))(?::\d{2,5})?(?:\/\S*)?$/;
   var web = $("#website").val();
    if (em.test(web)) {
           $("#website_error").empty();
      } else if(web==''){
           $("#website_error").empty();
      }else {
           $("#website_error").empty();
           $("#website_error").append('<ul class="list-unstyled text-danger"><li>Please enter valid Website</li></ul>');
      }
}); 
$("#url1").keyup(function() {
  		 var em = /^(?:(?:https?|ftp):\/\/)?(?:(?!(?:10|127)(?:\.\d{1,3}){3})(?!(?:169\.254|192\.168)(?:\.\d{1,3}){2})(?!172\.(?:1[6-9]|2\d|3[0-1])(?:\.\d{1,3}){2})(?:[1-9]\d?|1\d\d|2[01]\d|22[0-3])(?:\.(?:1?\d{1,2}|2[0-4]\d|25[0-5])){2}(?:\.(?:[1-9]\d?|1\d\d|2[0-4]\d|25[0-4]))|(?:(?:[a-z\u00a1-\uffff0-9]-*)*[a-z\u00a1-\uffff0-9]+)(?:\.(?:[a-z\u00a1-\uffff0-9]-*)*[a-z\u00a1-\uffff0-9]+)*(?:\.(?:[a-z\u00a1-\uffff]{2,})))(?::\d{2,5})?(?:\/\S*)?$/;
         var url1 = $("#url1").val();
            if (em.test(url1)) {
                $("#url1_error").empty();
            } else if(url1==''){
          		 $("#url1_error").empty();
     		 }else {
                $("#url1_error").empty();
                $("#url1_error").append('<ul class="list-unstyled text-danger"><li>Please enter valid url</li></ul>');
            }
});
$("#url2").keyup(function() {
   var em = /^(?:(?:https?|ftp):\/\/)?(?:(?!(?:10|127)(?:\.\d{1,3}){3})(?!(?:169\.254|192\.168)(?:\.\d{1,3}){2})(?!172\.(?:1[6-9]|2\d|3[0-1])(?:\.\d{1,3}){2})(?:[1-9]\d?|1\d\d|2[01]\d|22[0-3])(?:\.(?:1?\d{1,2}|2[0-4]\d|25[0-5])){2}(?:\.(?:[1-9]\d?|1\d\d|2[0-4]\d|25[0-4]))|(?:(?:[a-z\u00a1-\uffff0-9]-*)*[a-z\u00a1-\uffff0-9]+)(?:\.(?:[a-z\u00a1-\uffff0-9]-*)*[a-z\u00a1-\uffff0-9]+)*(?:\.(?:[a-z\u00a1-\uffff]{2,})))(?::\d{2,5})?(?:\/\S*)?$/;
            var url1 = $("#url2").val();
            if (em.test(url2)) {
                $("#url2_error").empty();
            }else if(url2==''){
          		 $("#url2_error").empty();
     		 } else {
                $("#url2_error").empty();
                $("#url2_error").append('<ul class="list-unstyled text-danger"><li>Please enter valid url</li></ul>');
            }
});
$("#buId").keyup(function() {
   var exp = new RegExp("^[a-zA-Z0-9]+$");
   var bu = $("#buId").val();
    if (exp.test(bu)) {
           $("#buId_error").empty();
      }else if(bu==''){
           $("#buId_error").empty();
      } else {
           $("#buId_error").empty();
           $("#buId_error").append('<ul class="list-unstyled text-danger"><li>Please enter valid BU ID</li></ul>');
      }
});  
$("#zipOrPostalCode").keyup(function() {
    var exp = new RegExp("^[0-9]{0,10}$");
    var zip = $("#zipOrPostalCode").val();
    if (exp.test(zip)) {
           $("#zipOrPostalCode_error").empty();
      } else {
           $("#zipOrPostalCode_error").empty();
           $("#zipOrPostalCode_error").append('<ul class="list-unstyled text-danger"><li>Please enter valid ZIP/Postal Code</li></ul>');
      }
}); 

$('#countryOrRegion').change(function(e, data) {
	getDependentDropdownValues('getAssociatedStateList', 'countryGeoId', 'countryOrRegion', 'stateOrProvince', 'stateList', 'geoId', 'geoName','${stateValue!}');
});
function resetForm(){
$('[id*="_error"]').empty();
}


</script>