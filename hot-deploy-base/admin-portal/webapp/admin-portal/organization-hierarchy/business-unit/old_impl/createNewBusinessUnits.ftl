<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<div class="row">
    <div id="main" role="main">
        <#assign extra='<a href="/admin-portal/control/businessUnits_old" class="btn btn-xs btn-primary">
        <i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>' />
        <@sectionFrameHeader title="${uiLabelMap.CreateBusinessUnit!}" extra=extra />
        <div class="clearfix"></div>
        <form method="post" action="<@ofbizUrl>businessUnitCreation_old</@ofbizUrl>" class="form-horizontal" name="createBusinessUnits" id="createBusinessUnits" >
            <div class="col-lg-12 col-md-12 col-sm-12">
                <div class="row pt-2">
                    <div class="col-md-12 col-lg-6 col-sm-12">
                        <@inputRow    
                            label="${uiLabelMap.BUName!}"
                            id="buName"
                            name="buName"
                            placeholder="${uiLabelMap.BUName!}"
                            required=true
                        />
                        <@inputRow    
                            label="${uiLabelMap.BuId!}"
                            id="buId"
                            name="buId"
                            placeholder="${uiLabelMap.BuId!}"
                        />
                        <div class="form-group row">
                            <label for="inputEmail3" class="col-sm-4 col-form-label">Parent BU 
                               
                            </label>
                            <div class="col-sm-7">
                                <div class="input-group">
                                    <input type="text" class="form-control" id="parentBuDesc" name="parentBuDesc" placeholder="" aria-describedby="inputGroupPrepend3" readOnly>
                                    <div class="input-group-append">
                                        <span class="input-group-text rounded-0 cursor-pointer" id="inputGroupPrepend3" data-toggle="modal" data-target="#parentbu">
                                            <i class="fa fa-search" aria-hidden="true"></i>
                                        </span>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <input type="hidden"  id="parentBuId" name="parentBuId">
                    </div>
                    <div class="col-md-12 col-lg-6 col-sm-12 ">
                        <@dropdownCell
                            name="buType"
                            id="buType"
                            required = true
                            label="${uiLabelMap.BuType!}"
                            allowEmpty=true
                            options=buTypeId
                            placeholder = "${uiLabelMap.BuType!}"
                            value=inputContext.status!'PHYSICAL'
                        />
                     <@dropdownCell
                          name="status"
                          id="status"
                          allowEmpty = true
                          options=statusId
                          placeholder = "${uiLabelMap.selectStatus!}"
                          label="${uiLabelMap.BuStatus!}"
                          value=inputContext.status!'ACTIVE'
                          required=true
                          />
                       <#--  <@dropdownCell
                            name="status"
                            id="status"
                            required = true
                            label="${uiLabelMap.BuStatus!}"
                            allowEmpty =  false
                            options=statusId
                            placeholder = "${uiLabelMap.BuStatus!}"
                        /> -->
                    </div>
                </div>
                <h4 class="bg-light pl-1 mt-2">Contact Information </h4>
                <div class="row ">
                    <div class="col-md-12 col-lg-6 col-sm-12 ">
                        <@inputRow    
                            label="${uiLabelMap.Phone!}"
                            id="phone"
                            name="phone"
                            placeholder="${uiLabelMap.Phone!}"
                        />
                        <@inputRow    
                            label="${uiLabelMap.Mobile!}"
                            id="mobile"
                            name="mobile"
                            placeholder="${uiLabelMap.Mobile!}"
                        />
                    </div>
                    <div class="col-md-12 col-lg-6 col-sm-12 ">
                        <@inputRow    
                            label="${uiLabelMap.email!}"
                            id="email"
                            name="email"
                            placeholder="${uiLabelMap.email!}"
                        />
                        <@inputRow    
                            label="${uiLabelMap.Website!}"
                            id="website"
                            name="website"
                            placeholder="${uiLabelMap.Website!}"
                        />
                    </div>
                </div>
                <h4 class="bg-light pl-1 mt-2">Address </h4>
                <div class="row">
                    <div class="col-md-12 col-lg-6 col-sm-12">
                        <@inputRow    
                            label="${uiLabelMap.Address1!}"
                            id="address1"
                            name="address1"
                            placeholder="${uiLabelMap.Address1!}"
                        />
                        <@inputRow    
                            label="${uiLabelMap.Address2!}"
                            id="address2"
                            name="address2"
                            placeholder="${uiLabelMap.Address2!}"
                        />
                        <@inputRow    
                            label="${uiLabelMap.Address3!}"
                            id="address3"
                            name="address3"
                            placeholder="${uiLabelMap.Address3!}"
                        />
                        <@inputRow    
                            label="${uiLabelMap.City!}"
                            id="city"
                            name="city"
                            placeholder="${uiLabelMap.City!}"
                        />
                    </div>
                    <div class="col-md-12 col-lg-6 col-sm-12 ">
                        <@inputState    
                            name="stateOrProvince"
                            label="${uiLabelMap.StateOrProvince!}"
                            dataLiveSearch=true
                        />
                        <@inputRow    
                            label="${uiLabelMap.ZipOrPostalCode!}"
                            id="zipOrPostalCode"
                            name="zipOrPostalCode"
                            placeholder="${uiLabelMap.ZipOrPostalCode!}"
                        />
                        <@inputCountry    
                            name="countryOrRegion"
                            label="${uiLabelMap.CountryOrRegion!}"
                            dataLiveSearch=true
                        />
                    </div>
                </div>
            </div>
            <div class="clearfix"></div>
            <div class="offset-md-2 col-sm-10">
         
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
        </form>
    </div>
</div>
<div id="parentbu" class="modal fade mt-2 save-modal" role="dialog">
    <div class="modal-dialog modal-lg">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <h3 class="modal-title">Find Parent BU</h3>
                <button type="reset" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body">
                <div class="table-responsive">
                <div class="loader text-center" id="loader" sytle="display:none;">
                  <span></span>
                  <span></span>
                  <span></span>
                </div>
                <div id="grid1" style="width: 100%;" class="ag-theme-balham"></div>
                  <#--  <@AgGrid 
                        userid="${userLogin.userLoginId}" 
                        instanceid="BU05" 
                        styledimensions='{"width":"100%","height":"80vh"}'
                        autosave="false"
                        autosizeallcol="true" 
                        debug="true"
                    /> --> 
                </div>
                  <script type="text/javascript" src="/admin-portal-resource/js/ag-grid/org-Hierarchy/bu-parent.js"></script> 
            </div>
            <div class="modal-footer">
                <!-- <button type="submit" class="btn btn-sm btn-primary" data-dismiss="modal" id="addParentBu" name="addParentBu" >Add</button>-->
            </div>
        </div>
    </div>
</div>
<script>
    function testFunction(parentBuDesc,parentBuId){
        $('#parentBuDesc').val(parentBuDesc);
        $('#parentBuId').val(parentBuId);
        $('#parentbu').modal('hide');
    } 
</script>
<script>

function formSubmission(){
    var isValid = "Y";
    var moberr="";
    var phoneerr="";
    var weberr="";
    var emailerr="";
    var buerr="";
    var ziperr="";
    var buName =  $("#buName").val();
    var status =  $("#status").val();
    var buType =  $("#buType").val();
    var phone =  $("#phone").val();
    var buId =  $("#buId").val();
    var email =  $("#email").val();
    var website =  $("#website").val();
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
              var exp = new RegExp("^[0-9]{0,25}$");
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
         if(buId != "") {
              var exp = new RegExp("^[0-9]{0,25}$");
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
$("#status").change(function() {
   var status = $("#status").val();
   $("#status_error").empty();
});  
$("#buType").change(function() {
   var buType = $("#status").val();
   $("#buType_error").empty();
}); 
$("#phone").keyup(function() {
   var re = new RegExp("^[0-9]{0,22}$");
   var phoneDigit = $("#phone").val();
    if (re.test(phoneDigit)) {
           $("#phone_error").empty();
      } else {
           $("#phone_error").empty();
           $("#phone_error").append('<ul class="list-unstyled text-danger"><li>Please enter valid Phone Number</li></ul>');
      }
});  
$("#mobile").keyup(function() {
   var exp = new RegExp("^[0-9]{0,22}$");
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
$("#buId").keyup(function() {
   var exp = new RegExp("^[0-9]{0,25}$");
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

</script>