<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<div class="row">
    <div id="main" role="main"><#assign extra='
        <a href="/admin-portal/control/businessUnits" class="btn btn-xs btn-primary">
            <i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back
        </a>
        <a href="/admin-portal/control/updateBusinessUnits?productStoreGroupId=${productStoreGroupId!}" class="btn btn-xs btn-primary text-right">
            <i class="fa fa-edit" aria-hidden="true"></i> Update
        </a>' />
        

        <div class="clearfix"></div>
        <div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
         <@sectionFrameHeaderTab title="${uiLabelMap.ViewBusinessUnit!}" tabId="ViewBusinessUnit" extra=extra />
            <div class="">
                <div class="row">
                    <div class="col-lg-6 col-md-12 col-sm-12">
                        <h6>Business Unit : Information</h6>
                        <h3>${businessUnitName!}</h3>
                    </div>
                </div>
            </div>
            <@navTab
	            instanceId="VIEW_BUSINESS_UNIT"
	            activeTabId="tab1"
            />
            <div class="clearfix"></div>
        </div>
        </div>
    <div id="updatephone" class="modal fade mt-2" role="dialog">
    <div class="modal-dialog modal-md">
    <!-- Modal content-->
        <div class="modal-content">
            <form method="post" action="<@ofbizUrl>updatePhoneNumberView</@ofbizUrl>" class="form-horizontal" name="updatePhoneNumber" id="updatePhoneNumber" >
                <div class="modal-header">
                    <h3 class="modal-title">Update Phone Number</h3>
                    <button type="reset" class="close" data-dismiss="modal">&times;</button>
                </div>
                <div class="modal-body">        
                    <!--<div class="form-group row">
                    <label class="col-sm-4 col-form-label">Type </label>
                    <div class="col-sm-7">
                        <select class="ui dropdown search form-control fluid show-tick" data-live-search="true">
                            <option>Home</option>
                            <option>Mobile</option>
                            <option>Office</option>
                         </select>
                     </div>
                     </div>-->
                     <@inputHidden    
                        id="productStoreGroupId"
                        name="productStoreGroupId"
                        value="${productStoreGroupId!}"
                      />
                      <@inputHidden    
                        id="phoneMechId"
                        name="phoneMechId"
                        value="${phoneMechId!}"
                      />
                     <@inputRow    
                        label="${uiLabelMap.Phone!}"
                        id="phone"
                        name="phone"
                        value="${phone!}"
                        placeholder="${uiLabelMap.Phone!}"
                      />
                </div>
                <div class="modal-footer">
                   <@formButton
                        btn1type="submit"
                        btn1label="${uiLabelMap.Update}"
                        btn1onclick="return phoneFormSubmission();"
                        />
                </div>
            </form>
        </div>
    </div>
</div>
<div id="updatemobile" class="modal fade mt-2" role="dialog">
    <div class="modal-dialog modal-md">
    <!-- Modal content-->
        <div class="modal-content">
            <form method="post" action="<@ofbizUrl>updateMobileNumberView</@ofbizUrl>" class="form-horizontal" name="updateMobileNumber" id="updateMobileNumber" >
                <div class="modal-header">
                    <h3 class="modal-title">Update Mobile Number</h3>
                    <button type="reset" class="close" data-dismiss="modal">&times;</button>
                </div>
                <div class="modal-body">        
                    <!--<div class="form-group row">
                        <label class="col-sm-4 col-form-label">Type </label>
                        <div class="col-sm-7">
                        <select class="ui dropdown search form-control fluid show-tick" data-live-search="true">
                            <option>Home</option>
                            <option>Mobile</option>
                            <option>Office</option>
                        </select>
                        </div>
                    </div>-->
                    <@inputHidden    
                        id="productStoreGroupId"
                        name="productStoreGroupId"
                        value="${productStoreGroupId!}"
                      />
                      <@inputHidden    
                        id="mobileMechId"
                        name="mobileMechId"
                        value="${mobileId!}"
                      />                     
                    <@inputRow    
                        label="${uiLabelMap.Mobile!}"
                        id="mobile"
                        name="mobile"
                        value="${mobile!}"
                        placeholder="${uiLabelMap.Mobile!}"
                    />
                </div>
                <div class="modal-footer">
                    <@formButton
                        btn1type="submit"
                        btn1label="${uiLabelMap.Update}"
                        btn1onclick="return mobileFormSubmission();"
                        />
                </div>
            </form>
        </div>
    </div>
</div>
<div id="updatemail" class="modal fade mt-2" role="dialog">
    <div class="modal-dialog modal-md">
    <!-- Modal content-->
        <div class="modal-content">
            <form method="post" action="<@ofbizUrl>updateEmailView</@ofbizUrl>" class="form-horizontal" name="updateEmail" id="updateEmail" >
                <div class="modal-header">
                    <h3 class="modal-title">Update Email Address</h3>
                    <button type="reset" class="close" data-dismiss="modal">&times;</button>
                </div>
                <div class="modal-body">        
                    <@inputHidden    
                        id="productStoreGroupId"
                        name="productStoreGroupId"
                        value="${productStoreGroupId!}"
                      />
                      <@inputHidden    
                        id="emailId"
                        name="emailId"
                        value="${emailId!}"
                      />                            
                    <@inputRow    
                        label="${uiLabelMap.email!}"
                        id="email"
                        name="email"
                        value="${email!}"
                        placeholder="${uiLabelMap.email!}"
                    />    
                </div>
                <div class="modal-footer">
                    <@formButton
                        btn1type="submit"
                        btn1label="${uiLabelMap.Update}"
                        btn1onclick="return emailFormSubmission();"
                        />
                </div>
            </form>
        </div>
    </div>
</div>
<div id="updateweb" class="modal fade mt-2" role="dialog">
    <div class="modal-dialog modal-md">
        <!-- Modal content-->
        <div class="modal-content">
            <form method="post" action="<@ofbizUrl>updateWebView</@ofbizUrl>" class="form-horizontal" name="updateWeb" id="updateWeb" >
                <div class="modal-header">
                    <h3 class="modal-title">Update Web Address</h3>
                    <button type="reset" class="close" data-dismiss="modal">&times;</button>
                </div>
                <div class="modal-body">        
                    <@inputHidden    
                        id="productStoreGroupId"
                        name="productStoreGroupId"
                        value="${productStoreGroupId!}"
                      />
                      <@inputHidden    
                        id="webId"
                        name="webId"
                        value="${websiteId!}"
                      />                                 
                    <@inputRow    
                        label="${uiLabelMap.Website!}"
                        id="web"
                        name="web"
                        value="${web!}"
                        placeholder="${uiLabelMap.Website!}"
                    />
                </div>
                <div class="modal-footer">
                    <@formButton
                        btn1type="submit"
                        btn1label="${uiLabelMap.Update}"
                        btn1onclick="return webFormSubmission();"
                        />
                </div>
            </form>
        </div>
    </div>
</div>
<div id="updateaddress" class="modal fade" role="dialog">
    <div class="modal-dialog modal-md">
    <!-- Modal content-->
        <div class="modal-content">
            <form method="post" action="<@ofbizUrl>updateAddressView</@ofbizUrl>" class="form-horizontal" name="updateAddress" id="updateAddress" >
                <div class="modal-header">
                    <h3 class="modal-title">Update Address</h3>
                    <button type="reset" class="close" data-dismiss="modal">&times;</button>
                </div>
                <div class="modal-body">        
                    <@inputHidden    
                        id="productStoreGroupId"
                        name="productStoreGroupId"
                        value="${productStoreGroupId!}"
                      />
                      <@inputHidden    
                        id="postalId"
                        name="postalId"
                        value="${postalId!}"
                      />
                    <@inputRow    
                        label="${uiLabelMap.Address1!}"
                        id="address1"
                        name="address1"
                        value="${address1!}"
                        placeholder="${uiLabelMap.Address1!}"
                    />
                    <@inputRow    
                        label="${uiLabelMap.Address2!}"
                        id="address2"
                        name="address2"
                        value="${address2!}"
                        placeholder="${uiLabelMap.Address2!}"
                    />
                    <@inputRow    
                        label="${uiLabelMap.Address3!}"
                        id="address3"
                        name="address3"
                        value="${address3!}"
                        placeholder="${uiLabelMap.Address3!}"
                    />
                    <@inputRow    
                        label="${uiLabelMap.City!}"
                        id="city"
                        name="city"
                        value="${city!}"
                        placeholder="${uiLabelMap.City!}"
                    />
                    <@inputState    
                        name="stateOrProvince"
                        label="${uiLabelMap.StateOrProvince!}"
                        dataLiveSearch=true
                    />
                    <@inputRow    
                        label="ZIP/PostalCode"
                        id="zipOrPostalCode"
                        name="zipOrPostalCode"
                        value="${postalCode!}"
                        placeholder="ZIP/PostalCode"
                    />
                    <@inputCountry    
                        name="countryOrRegion"
                        countryValue="${country!}" 
                        stateValue="${state!}" 
                        label="${uiLabelMap.CountryOrRegion!}"
                        dataLiveSearch=true
                    />
                </div>
                <div class="modal-footer">
                   <@formButton
                        btn1type="submit"
                        btn1label="${uiLabelMap.Update}"
                        btn1onclick="return addressFormSubmission();"
                        />
                </div>
            </form>
        </div>
    </div>
</div>

</div>
 <div id="updateurl1" class="modal fade mt-2" role="dialog">
        <div class="modal-dialog modal-md">
        <!-- Modal content-->
            <div class="modal-content">
                <form method="post" action="<@ofbizUrl>updateUrl</@ofbizUrl>" class="form-horizontal" name="updateUrl1" id="updateUrl1" >
                    <div class="modal-header">
                        <h3 class="modal-title">Update URL (1)</h3>
                        <button type="reset" class="close" data-dismiss="modal">&times;</button>
                    </div>
                    <div class="modal-body">        
                        <@inputHidden    
                            id="productStoreGroupId"
                            name="productStoreGroupId"
                            value="${productStoreGroupId!}"
                            />
                                                        
                        <@inputRow    
                            label="Url 1"
                            id="url1"
                            name="url1"
                            value="${url1!}"
                            placeholder="URL 1"
                            />
                    </div>
                    <div class="modal-footer">
                        <@formButton
                            btn1type="submit"
                            btn1label="${uiLabelMap.Update}"
                            btn1onclick="return url1FormSubmission();"
                            />
                    </div>
                </form>
            </div>
        </div>
    </div>
    <div id="updateurl2" class="modal fade mt-2" role="dialog">
        <div class="modal-dialog modal-md">
        <!-- Modal content-->
            <div class="modal-content">
                <form method="post" action="<@ofbizUrl>updateUrl</@ofbizUrl>" class="form-horizontal" name="updateUrl2" id="updateUrl2" >
                    <div class="modal-header">
                        <h3 class="modal-title">Update URL (2)</h3>
                        <button type="reset" class="close" data-dismiss="modal">&times;</button>
                    </div>
                    <div class="modal-body">        
                        <@inputHidden    
                            id="productStoreGroupId"
                            name="productStoreGroupId"
                            value="${productStoreGroupId!}"
                            />
                                                        
                        <@inputRow    
                            label="Url 2"
                            id="url2"
                            name="url2"
                            value="${url2!}"
                            placeholder="URL 2"
                            />
                    </div>
                    <div class="modal-footer">
                        <@formButton
                            btn1type="submit"
                            btn1label="${uiLabelMap.Update}"
                            btn1onclick="return url1FormSubmission();"
                            />
                    </div>
                </form>
            </div>
        </div>
    </div>
     <div id="updateurl3" class="modal fade mt-2" role="dialog">
        <div class="modal-dialog modal-md">
        <!-- Modal content-->
            <div class="modal-content">
                <form method="post" action="<@ofbizUrl>updateUrl</@ofbizUrl>" class="form-horizontal" name="updateUrl3" id="updateUrl3" >
                    <div class="modal-header">
                        <h3 class="modal-title">Update URL (3)</h3>
                        <button type="reset" class="close" data-dismiss="modal">&times;</button>
                    </div>
                    <div class="modal-body">        
                        <@inputHidden    
                            id="productStoreGroupId"
                            name="productStoreGroupId"
                            value="${productStoreGroupId!}"
                            />
                                                        
                        <@inputRow    
                            label="Url 3"
                            id="url3"
                            name="url3"
                            value="${url3!}"
                            placeholder="URL 3"
                            />
                    </div>
                    <div class="modal-footer">
                        <@formButton
                            btn1type="submit"
                            btn1label="${uiLabelMap.Update}"
                            btn1onclick="return url3FormSubmission();"
                            />
                    </div>
                </form>
            </div>
        </div>
    </div>
     <div id="updateurl4" class="modal fade mt-2" role="dialog">
        <div class="modal-dialog modal-md">
        <!-- Modal content-->
            <div class="modal-content">
                <form method="post" action="<@ofbizUrl>updateUrl</@ofbizUrl>" class="form-horizontal" name="updateUrl4" id="updateUrl4" >
                    <div class="modal-header">
                        <h3 class="modal-title">Update URL (4)</h3>
                        <button type="reset" class="close" data-dismiss="modal">&times;</button>
                    </div>
                    <div class="modal-body">        
                        <@inputHidden    
                            id="productStoreGroupId"
                            name="productStoreGroupId"
                            value="${productStoreGroupId!}"
                            />
                                                        
                        <@inputRow    
                            label="Url 4"
                            id="url4"
                            name="url4"
                            value="${url4!}"
                            placeholder="URL 4"
                            />
                    </div>
                    <div class="modal-footer">
                        <@formButton
                            btn1type="submit"
                            btn1label="${uiLabelMap.Update}"
                            btn1onclick="return url4FormSubmission();"
                            />
                    </div>
                </form>
            </div>
        </div>
    </div>
     <div id="updateurl5" class="modal fade mt-2" role="dialog">
        <div class="modal-dialog modal-md">
        <!-- Modal content-->
            <div class="modal-content">
                <form method="post" action="<@ofbizUrl>updateUrl</@ofbizUrl>" class="form-horizontal" name="updateUrl5" id="updateUrl5" >
                    <div class="modal-header">
                        <h3 class="modal-title">Update URL (5)</h3>
                        <button type="reset" class="close" data-dismiss="modal">&times;</button>
                    </div>
                    <div class="modal-body">        
                        <@inputHidden    
                            id="productStoreGroupId"
                            name="productStoreGroupId"
                            value="${productStoreGroupId!}"
                            />
                                                        
                        <@inputRow    
                            label="Url 5"
                            id="url5"
                            name="url5"
                            value="${url5!}"
                            placeholder="URL 5"
                            />
                    </div>
                    <div class="modal-footer">
                        <@formButton
                            btn1type="submit"
                            btn1label="${uiLabelMap.Update}"
                            btn1onclick="return url5FormSubmission();"
                            />
                    </div>
                </form>
            </div>
        </div>
    </div>
<script>
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
$("#web").keyup(function() {
   var em = /^(?:(?:https?|ftp):\/\/)?(?:(?!(?:10|127)(?:\.\d{1,3}){3})(?!(?:169\.254|192\.168)(?:\.\d{1,3}){2})(?!172\.(?:1[6-9]|2\d|3[0-1])(?:\.\d{1,3}){2})(?:[1-9]\d?|1\d\d|2[01]\d|22[0-3])(?:\.(?:1?\d{1,2}|2[0-4]\d|25[0-5])){2}(?:\.(?:[1-9]\d?|1\d\d|2[0-4]\d|25[0-4]))|(?:(?:[a-z\u00a1-\uffff0-9]-*)*[a-z\u00a1-\uffff0-9]+)(?:\.(?:[a-z\u00a1-\uffff0-9]-*)*[a-z\u00a1-\uffff0-9]+)*(?:\.(?:[a-z\u00a1-\uffff]{2,})))(?::\d{2,5})?(?:\/\S*)?$/;
   var website = $("#web").val();
    if (em.test(website)) {
           $("#web_error").empty();
      } else if(website==''){
           $("#web_error").empty();
      }else {
           $("#web_error").empty();
           $("#web_error").append('<ul class="list-unstyled text-danger"><li>Please enter valid Website</li></ul>');
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
           $("#url1_error").append('<ul class="list-unstyled text-danger"><li>Please enter valid Url</li></ul>');
      }
}); 
$("#url2").keyup(function() {
   var em = /^(?:(?:https?|ftp):\/\/)?(?:(?!(?:10|127)(?:\.\d{1,3}){3})(?!(?:169\.254|192\.168)(?:\.\d{1,3}){2})(?!172\.(?:1[6-9]|2\d|3[0-1])(?:\.\d{1,3}){2})(?:[1-9]\d?|1\d\d|2[01]\d|22[0-3])(?:\.(?:1?\d{1,2}|2[0-4]\d|25[0-5])){2}(?:\.(?:[1-9]\d?|1\d\d|2[0-4]\d|25[0-4]))|(?:(?:[a-z\u00a1-\uffff0-9]-*)*[a-z\u00a1-\uffff0-9]+)(?:\.(?:[a-z\u00a1-\uffff0-9]-*)*[a-z\u00a1-\uffff0-9]+)*(?:\.(?:[a-z\u00a1-\uffff]{2,})))(?::\d{2,5})?(?:\/\S*)?$/;
   var url2 = $("#url2").val();
    if (em.test(url2)) {
           $("#url2_error").empty();
      } else if(url2==''){
           $("#url2_error").empty();
      }else {
           $("#url2_error").empty();
           $("#url2_error").append('<ul class="list-unstyled text-danger"><li>Please enter valid Website</li></ul>');
      }
});
function phoneFormSubmission(){
    var phone =  $("#phone").val();
    if(phone ==''){
        $("#phone_error").html('');
        $("#phone_error").append('<ul class="list-unstyled text-danger"><li id="phone_err">Please enter Phone Number</li></ul>');
        return false;
    }
    var re = new RegExp("^[0-9]{0,22}$");
   var phoneDigit = $("#phone").val();
    if (re.test(phoneDigit)) {
           $("#phone_error").empty();
      } else {
           $("#phone_error").empty();
           $("#phone_error").append('<ul class="list-unstyled text-danger"><li>Please enter valid Phone Number</li></ul>');
           return false;
      }
}
function mobileFormSubmission(){
    var mobile =  $("#mobile").val();
    if(mobile ==''){
        $("#mobile_error").html('');
        $("#mobile_error").append('<ul class="list-unstyled text-danger"><li id="mobile_err">Please enter Mobile Number</li></ul>');
        return false;
    }
     var exp = new RegExp("^[0-9]{0,22}$");
   var mobDigit = $("#mobile").val();
    if (exp.test(mobDigit)) {
           $("#mobile_error").empty();
      } else {
           $("#mobile_error").empty();
           $("#mobile_error").append('<ul class="list-unstyled text-danger"><li>Please enter valid Mobile Number</li></ul>');
           return false;
      }
}
function webFormSubmission(){
    var web =  $("#web").val();
    if(web ==''){
        $("#web_error").html('');
        $("#web_error").append('<ul class="list-unstyled text-danger"><li id="web_err">Please enter Website</li></ul>');
        return false;
    }
     var em = /^(?:(?:https?|ftp):\/\/)?(?:(?!(?:10|127)(?:\.\d{1,3}){3})(?!(?:169\.254|192\.168)(?:\.\d{1,3}){2})(?!172\.(?:1[6-9]|2\d|3[0-1])(?:\.\d{1,3}){2})(?:[1-9]\d?|1\d\d|2[01]\d|22[0-3])(?:\.(?:1?\d{1,2}|2[0-4]\d|25[0-5])){2}(?:\.(?:[1-9]\d?|1\d\d|2[0-4]\d|25[0-4]))|(?:(?:[a-z\u00a1-\uffff0-9]-*)*[a-z\u00a1-\uffff0-9]+)(?:\.(?:[a-z\u00a1-\uffff0-9]-*)*[a-z\u00a1-\uffff0-9]+)*(?:\.(?:[a-z\u00a1-\uffff]{2,})))(?::\d{2,5})?(?:\/\S*)?$/;
   var website = $("#web").val();
    if (em.test(website)) {
           $("#web_error").empty();
      } else {
           $("#web_error").empty();
           $("#web_error").append('<ul class="list-unstyled text-danger"><li>Please enter valid Website</li></ul>');
               return false;
      }
}
function emailFormSubmission(){
    var email =  $("#email").val();
    if(email ==''){
        $("#email_error").html('');
        $("#email_error").append('<ul class="list-unstyled text-danger"><li id="email_err">Please enter Email</li></ul>');
        return false;
    }
    var up = /^[A-Z0-9._%+-]+@([A-Z0-9-]+\.)+[A-Z]{2,4}$/i;
   var mail = $("#email").val();
    if (up.test(mail)) {
           $("#email_error").empty();
      } else{
           $("#email_error").empty();
           $("#email_error").append('<ul class="list-unstyled text-danger"><li>Please enter valid Email</li></ul>');
           return false;
      } 
}
$("#address1").keyup(function() {
   var ad = $("#address1").val();
    if(ad!=''){
           $("#address1_error").empty();
     }
});
$("#city").keyup(function() {
   var city = $("#city").val();
    if(city!=''){
           $("#city_error").empty();
     }
});
$("#zipOrPostalCode").keyup(function() {
    var exp = new RegExp("^[0-9]{0,10}$");
    var zip = $("#zipOrPostalCode").val();
    if (exp.test(zip)) {
           $("#zipOrPostalCode_error").empty();
      } else {
           $("#zipOrPostalCode_error").html('');
           $("#zipOrPostalCode_error").append('<ul class="list-unstyled text-danger"><li>Please enter valid ZIP/Postal Code</li></ul>');
      }
});
function addressFormSubmission(){
    var isValid = "Y";
    var address1 =  $("#address1").val();
    var city =  $("#city").val();
    var zipOrPostalCode =  $("#zipOrPostalCode").val();
    var country = $('select[name="countryOrRegion"]').val();
    var state =   $('select[name="stateOrProvince"]').val();
    if(address1 ==''){
        $("#address1_error").html('');
        $("#address1_error").append('<ul class="list-unstyled text-danger"><li id="address1_err">Please enter Address1</li></ul>');
        isValid = "N";
    }
    if(city ==''){
        $("#city_error").html('');
        $("#city_error").append('<ul class="list-unstyled text-danger"><li id="city_err">Please enter City</li></ul>');
        isValid = "N";
    }
    if(zipOrPostalCode ==''){
        $("#zipOrPostalCode_error").html('');
        $("#zipOrPostalCode_error").append('<ul class="list-unstyled text-danger"><li id="zipOrPostalCode_err">Please enter ZIP/Postal Code</li></ul>');
        isValid = "N";
    }else{
             var exp = new RegExp("^[0-9]{0,10}$");
            var zip = $("#zipOrPostalCode").val();
            if (exp.test(zip)) {
                $("#zipOrPostalCode_error").empty();
             } else {
                 $("#zipOrPostalCode_error").empty();
                $("#zipOrPostalCode_error").append('<ul class="list-unstyled text-danger"><li>Please enter valid ZIP/Postal Code</li></ul>');
                isValid = "N";
            }
    
    }
    
    if(isValid == "N"){
          return false;
    } 
    else if(isValid == "Y") {
         return true;
    }
}
function url1FormSubmission(){
    var url1 =  $("#url1").val();
    if(url1 ==''){
        $("#url1_error").html('');
        $("#url1_error").append('<ul class="list-unstyled text-danger"><li id="web_err">Please enter Url</li></ul>');
        return false;
    }
   var em = /^(?:(?:https?|ftp):\/\/)?(?:(?!(?:10|127)(?:\.\d{1,3}){3})(?!(?:169\.254|192\.168)(?:\.\d{1,3}){2})(?!172\.(?:1[6-9]|2\d|3[0-1])(?:\.\d{1,3}){2})(?:[1-9]\d?|1\d\d|2[01]\d|22[0-3])(?:\.(?:1?\d{1,2}|2[0-4]\d|25[0-5])){2}(?:\.(?:[1-9]\d?|1\d\d|2[0-4]\d|25[0-4]))|(?:(?:[a-z\u00a1-\uffff0-9]-*)*[a-z\u00a1-\uffff0-9]+)(?:\.(?:[a-z\u00a1-\uffff0-9]-*)*[a-z\u00a1-\uffff0-9]+)*(?:\.(?:[a-z\u00a1-\uffff]{2,})))(?::\d{2,5})?(?:\/\S*)?$/;
   var urlAd1 = $("#url1").val();
    if (em.test(urlAd1)) {
           $("#url1_error").empty();
      } else {
           $("#url1_error").empty();
           $("#url1_error").append('<ul class="list-unstyled text-danger"><li>Please enter valid Url</li></ul>');
               return false;
      }
}

function url2FormSubmission(){
    var url2 =  $("#url2").val();
    if(url2 ==''){
        $("#url2_error").html('');
        $("#url2_error").append('<ul class="list-unstyled text-danger"><li id="web_err">Please enter Url</li></ul>');
        return false;
    }
   var em = /^(?:(?:https?|ftp):\/\/)?(?:(?!(?:10|127)(?:\.\d{1,3}){3})(?!(?:169\.254|192\.168)(?:\.\d{1,3}){2})(?!172\.(?:1[6-9]|2\d|3[0-1])(?:\.\d{1,3}){2})(?:[1-9]\d?|1\d\d|2[01]\d|22[0-3])(?:\.(?:1?\d{1,2}|2[0-4]\d|25[0-5])){2}(?:\.(?:[1-9]\d?|1\d\d|2[0-4]\d|25[0-4]))|(?:(?:[a-z\u00a1-\uffff0-9]-*)*[a-z\u00a1-\uffff0-9]+)(?:\.(?:[a-z\u00a1-\uffff0-9]-*)*[a-z\u00a1-\uffff0-9]+)*(?:\.(?:[a-z\u00a1-\uffff]{2,})))(?::\d{2,5})?(?:\/\S*)?$/;
   var urlAd2 = $("#url2").val();
    if (em.test(urlAd2)) {
           $("#url2_error").empty();
      } else {
           $("#url2_error").empty();
           $("#url2_error").append('<ul class="list-unstyled text-danger"><li>Please enter valid Url</li></ul>');
               return false;
      }
}
function url3FormSubmission(){
    var url3 =  $("#url3").val();
    if(url3 ==''){
        $("#url3_error").html('');
        $("#url3_error").append('<ul class="list-unstyled text-danger"><li id="web_err">Please enter Url</li></ul>');
        return false;
    }
   var em = /^(?:(?:https?|ftp):\/\/)?(?:(?!(?:10|127)(?:\.\d{1,3}){3})(?!(?:169\.254|192\.168)(?:\.\d{1,3}){2})(?!172\.(?:1[6-9]|2\d|3[0-1])(?:\.\d{1,3}){2})(?:[1-9]\d?|1\d\d|2[01]\d|22[0-3])(?:\.(?:1?\d{1,2}|2[0-4]\d|25[0-5])){2}(?:\.(?:[1-9]\d?|1\d\d|2[0-4]\d|25[0-4]))|(?:(?:[a-z\u00a1-\uffff0-9]-*)*[a-z\u00a1-\uffff0-9]+)(?:\.(?:[a-z\u00a1-\uffff0-9]-*)*[a-z\u00a1-\uffff0-9]+)*(?:\.(?:[a-z\u00a1-\uffff]{2,})))(?::\d{2,5})?(?:\/\S*)?$/;
   var urlAd3 = $("#url3").val();
    if (em.test(urlAd3)) {
           $("#url3_error").empty();
      } else {
           $("#url3_error").empty();
           $("#url3_error").append('<ul class="list-unstyled text-danger"><li>Please enter valid Url</li></ul>');
               return false;
      }
}
function url4FormSubmission(){
    var url4 =  $("#url4").val();
    if(url4 ==''){
        $("#url4_error").html('');
        $("#url4_error").append('<ul class="list-unstyled text-danger"><li id="web_err">Please enter Url</li></ul>');
        return false;
    }
   var em = /^(?:(?:https?|ftp):\/\/)?(?:(?!(?:10|127)(?:\.\d{1,3}){3})(?!(?:169\.254|192\.168)(?:\.\d{1,3}){2})(?!172\.(?:1[6-9]|2\d|3[0-1])(?:\.\d{1,3}){2})(?:[1-9]\d?|1\d\d|2[01]\d|22[0-3])(?:\.(?:1?\d{1,2}|2[0-4]\d|25[0-5])){2}(?:\.(?:[1-9]\d?|1\d\d|2[0-4]\d|25[0-4]))|(?:(?:[a-z\u00a1-\uffff0-9]-*)*[a-z\u00a1-\uffff0-9]+)(?:\.(?:[a-z\u00a1-\uffff0-9]-*)*[a-z\u00a1-\uffff0-9]+)*(?:\.(?:[a-z\u00a1-\uffff]{2,})))(?::\d{2,5})?(?:\/\S*)?$/;
   var urlAd4 = $("#url4").val();
    if (em.test(urlAd4)) {
           $("#url4_error").empty();
      } else {
           $("#url4_error").empty();
           $("#url4_error").append('<ul class="list-unstyled text-danger"><li>Please enter valid Url</li></ul>');
               return false;
      }
}
function url5FormSubmission(){
    var url5 =  $("#url5").val();
    if(url5 ==''){
        $("#url5_error").html('');
        $("#url5_error").append('<ul class="list-unstyled text-danger"><li id="web_err">Please enter Url</li></ul>');
        return false;
    }
   var em = /^(?:(?:https?|ftp):\/\/)?(?:(?!(?:10|127)(?:\.\d{1,3}){3})(?!(?:169\.254|192\.168)(?:\.\d{1,3}){2})(?!172\.(?:1[6-9]|2\d|3[0-1])(?:\.\d{1,3}){2})(?:[1-9]\d?|1\d\d|2[01]\d|22[0-3])(?:\.(?:1?\d{1,2}|2[0-4]\d|25[0-5])){2}(?:\.(?:[1-9]\d?|1\d\d|2[0-4]\d|25[0-4]))|(?:(?:[a-z\u00a1-\uffff0-9]-*)*[a-z\u00a1-\uffff0-9]+)(?:\.(?:[a-z\u00a1-\uffff0-9]-*)*[a-z\u00a1-\uffff0-9]+)*(?:\.(?:[a-z\u00a1-\uffff]{2,})))(?::\d{2,5})?(?:\/\S*)?$/;
   var urlAd5 = $("#url5").val();
    if (em.test(urlAd5)) {
           $("#url5_error").empty();
      } else {
           $("#url5_error").empty();
           $("#url5_error").append('<ul class="list-unstyled text-danger"><li>Please enter valid Url</li></ul>');
               return false;
      }
}

function removePhoneAlert(){
     var phoneMech =  $("#phoneMech").val();
     if(phoneMech ==''){
        showAlert("error","Phone Number field is empty");
        return false;
     }
}
function removeMobileAlert(){
     var mob =  $("#mobileNoId").val();
     if(mob ==''){
        showAlert("error","Mobile Number field is empty");
        return false;
     }
}
function removeWebAlert(){
     var web =  $("#websiteRem").val();
     if(web ==''){
        showAlert("error","Website field is empty");
        return false;
     }
}
function removeEmailAlert(){
     var emailRem =  $("#emailRem").val();
     if(emailRem ==''){
        showAlert("error","Email field is empty");
        return false;
     }
}
function removeAddressAlert(){
     var postalRem =  $("#postalRem").val();
     if(postalRem ==''){
        showAlert("error","Address field is empty");
        return false;
     }
}

 $(document).ready(function() {
 
 var tabPhone= document.getElementById("phoneUpdateId").value;
 if(tabPhone!='')
        $("tab1").attr('class', "tab-pane fade");
    });
</script>