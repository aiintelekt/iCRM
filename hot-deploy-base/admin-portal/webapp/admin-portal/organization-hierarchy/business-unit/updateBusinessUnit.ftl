<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/lib/picker_macro.ftl"/>

    <div class="row">
        <div id="main" role="main">
         <div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
            <#assign extra='<a href="viewBusinessUnits?productStoreGroupId=${productStoreGroupId!}" class="btn btn-xs btn-primary"> <i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>' />
            <@sectionFrameHeaderTab title="${uiLabelMap.UpdateBusinessUnit!}" tabId="UpdateBusinessUnit" extra=extra/> 
			<#assign activeTab = request.getParameter("activeTab")?if_exists>
            <div class="clearfix"></div>
           
                <#-- <@viewSectionHeader
                    title = "Business Unit : Information"
                    title1="${businessUnitName!}"
                    />  -->
                <div class="">
	                <div class="row">
	                    <div class="col-lg-6 col-md-12 col-sm-12">
	                        <h6>Business Unit : Information</h6>
	                        <h3>${businessUnitName!}</h3>
	                    </div>
	                </div>
            	</div>
             <#--    <ul class="nav nav-tabs mt-3">
                    <li class="nav-item"><a data-toggle="tab" class="nav-link active" href="#tab1">Details</a></li>
                    <li class="nav-item"><a data-toggle="tab" class="nav-link" href="#tab2"> Contact</a></li>
                </ul>  -->
                <@navTab
                    instanceId = "UPDATE_BUSINESS_UNIT"
                    activeTabId = "details"
                    />
           <#--          <div id="tab1" class="tab-pane fade active show">
			<div class="col-lg-12 col-md-12 col-sm-12 pd-lr-0">
				<form name="updateBusinessUnit" id="updateBusinessUnit" method="post" action="<@ofbizUrl>businessUnitUpdation</@ofbizUrl>" data-toggle="validator" class="form-horizontal" >
					<@dynaScreen
							instanceId="UPDATE_BUSINESS_UNITS"
							modeOfAction="CREATE"
							/>
					<div class="offset-md-2 col-sm-10">
						<input type="submit" class="btn btn-sm btn-primary" onclick="return formSubmission();" value="Update">
						<a href="viewBusinessUnits?productStoreGroupId=${productStoreGroupId!}"class="btn btn-sm btn-secondary"> Cancel</a>
					</div>
				</form>
				<@parentBUPicker 
					instanceId="parentBUPicker"
					/> 
			</div>
                    </div>  -->
         <#--            <div id="tab2" class="tab-pane fade">
                                               
                    </div>  -->
            </div>
        </div>
    </div>
    <div id="parentbu" class="modal fade mt-2" role="dialog">
        <div class="modal-dialog modal-lg" style="width: 640px;">
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
                            instanceid="BU10"
                            styledimensions='{"width":"100%","height":"80vh"}'
                            autosave="false"
                            autosizeallcol="true"
                            debug="true"
                            requestbody='{"productStoreGroupId":"${productStoreGroupId!}"}'  
                            /> --> 
                    </div>
                     <script type="text/javascript" src="/admin-portal-resource/js/ag-grid/org-Hierarchy/updateBu-parent.js"></script>
                </div>
                <div class="modal-footer">
                <!-- <button type="submit" class="btn btn-sm btn-primary" data-dismiss="modal" id="addParentBu" name="addParentBu" >Add</button>-->
                </div>
            </div>
        </div>
    </div>
    <div id="updatephone" class="modal fade mt-2" role="dialog">
        <div class="modal-dialog modal-md">
        <!-- Modal content-->
            <div class="modal-content">
                <form method="post" action="<@ofbizUrl>updatePhoneNumber</@ofbizUrl>" class="form-horizontal" name="updatePhoneNumber" id="updatePhoneNumber" >
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
                <form method="post" action="<@ofbizUrl>updateMobileNumber</@ofbizUrl>" class="form-horizontal" name="updateMobileNumber" id="updateMobileNumber" >
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
                <form method="post" action="<@ofbizUrl>updateEmail</@ofbizUrl>" class="form-horizontal" name="updateEmail" id="updateEmail" >
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
                <form method="post" action="<@ofbizUrl>updateWeb</@ofbizUrl>" class="form-horizontal" name="updateWeb" id="updateWeb" >
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
    <div id="updateaddress" class="modal fade mt-2" role="dialog">
        <div class="modal-dialog modal-md">
        <!-- Modal content-->
            <div class="modal-content">
                <form method="post" action="<@ofbizUrl>updateAddress</@ofbizUrl>" class="form-horizontal" name="updateAddress" id="updateAddress" >
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
                            label="${uiLabelMap.ZipOrPostalCode!}"
                            id="zipOrPostalCode"
                            name="zipOrPostalCode"
                            value="${postalCode!}"
                            placeholder="${uiLabelMap.ZipOrPostalCode!}"
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
                            btn1onclick="return url2FormSubmission();"
                            />
                    </div>
                </form>
            </div>
        </div>
    </div>
<script>
function testFunction(parentBuDesc,parentBuId){
        $('#parentBuDesc').val(parentBuDesc);
        $('#parentBuId').val(parentBuId);
        $('#parentbu').modal('hide');
}
function formCancel(){
var url = "viewBusinessUnits?productStoreGroupId=${productStoreGroupId!}";
window.location(url);
}
$("#buName").keyup(function(){  
    if($("#buName").val() != null){
    $("#buName_error").html("");  
    }
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
function formSubmission(){
    var isValid = "Y";
    var status =  $("#status").val();
    var buType =  $("#buType").val();
    console.log("status"+status);
    console.log("buType"+buType);
    if(status !='' && buType!=''){
        return true;
    }else{
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
       if(isValid == "N"){
          return false;
        }  
    }
}
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
           $("#zipOrPostalCode_error").empty();
           $("#zipOrPostalCode_error").append('<ul class="list-unstyled text-danger"><li>Please enter valid ZIP/Postal Code</li></ul>');
      }
});
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
function removeUrl1Alert(){
     var url1Rem =  $("#url1Rem").val();
     if(url1Rem ==''){
        showAlert("error","Url1 field is empty");
        return false;
     }
}
function removeUrl2Alert(){
     var url2Rem =  $("#url2Rem").val();
     if(url2Rem ==''){
        showAlert("error","Url2 field is empty");
        return false;
     }
}
function removeUrl3Alert(){
     var url2Rem =  $("#url2Rem").val();
     if(url2Rem ==''){
        showAlert("error","Url2 field is empty");
        return false;
     }
}

function removeUrl4Alert(){
     var url4Rem =  $("#url4Rem").val();
     if(url4Rem ==''){
        showAlert("error","Url4 field is empty");
        return false;
     }
}

function removeUrl5Alert(){
     var url5Rem =  $("#url5Rem").val();
     if(url5Rem ==''){
        showAlert("error","Url5 field is empty");
        return false;
     }
}

$(document).ready(function(){
  <#if activeTab?has_content && activeTab == "contact">
    	$('.nav-tabs a[href="#contact"]').tab('show');
    <#else>   
    	$('.nav-tabs a[href="#details"]').tab('show');
    </#if> 
    
var productStore='${productStoreGroupId!}';
$("#searchForm #productStoreGroupId").val(productStore);
$('#updateBusinessUnit #parentBuId_desc').val('${parentBu!}');
$('#updateBusinessUnit #buId').prop('readonly', true);
});

</script>
