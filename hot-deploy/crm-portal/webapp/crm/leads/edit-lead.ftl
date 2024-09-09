<#include "component://lms-mobile/webapp/lms-mobile/lib/mobileMacros.ftl"/>
<style>
.accordion-option{float: right;clear: both;}
.accordion-option .title {font-size: 20px;font-weight: bold;float: left; padding: 0; margin: 0;}
.accordion-option .toggle-accordion {}
.accordion-option .toggle-accordion:before {content: "Expand All";}
.accordion-option .toggle-accordion.active:before {content: "Collapse All";}
</style>
<#if request.getRequestURI()?contains("crm")>
    <link rel="stylesheet" href="/lms-mobile-resource/css/mobiscroll.jquery.min.css">
    <script src="/lms-mobile-resource/js/mobiscroll.jquery.min.js"></script>
    <script src="/lms-mobile-resource/js/jquery.bootstrap.wizard.js"></script>
    <script src="/lms-mobile-resource/js/leads.js"></script>
</#if>
<div class="page-header border-b">
	<h1 class="float-left">
		Edit Lead 
		<#if leadName?has_content>
		- ${leadName}
		</#if>
	</h1>
</div>
<form method="POST" action="<@ofbizUrl>updateLeadExt</@ofbizUrl>" name="updateLead" id="updateLead" onsubmit="return submitLead();">
<input type="hidden" value="${leadId}" name="leadId" />
<input type="hidden" value="${primaryPhoneMechId!}" name="primaryPhoneMechId" />
<input type="hidden" value="" name="isMainLine" id="isMainLine"/>
<div class="container-fluid px-0">
   <div class="accordion-option pb-2"> 
    <a href="javascript:void(0)" class="toggle-accordion btn btn-xs btn-primary" id="allText" accordion-id="#accordion"></a>
  </div>
  <div class="clearfix"></div>
  <div class="panel-group" id="accordion" role="tablist" aria-multiselectable="true">
  <div class="row" id="displayMessageTab1" style="display:none;">
     <div class="col-12 p-0">
	    <div class="alert alert-danger alert-dismissible rounded-0" id="alertStatusTab1">
	       <button type="button" class="close" id="closeButtonTab2" data-dismiss="alert">x</button>
	       <strong id="returnMessageTab1"></strong>            
	    </div>
     </div>
 </div>
    <div class="panel panel-default">
    <#if (ajaxEvents.getBankAttribute())??>
    	${ajaxEvents.getBankAttribute()}
    	</#if>
      <div class="panel-heading m15" role="tab" id="headingOne">
        <div class="panel-title">
        <a role="button" data-toggle="collapse" data-parent="#accordion" href="#collapseOne" aria-expanded="false" aria-controls="collapseOne">
          Company overview 
        </a>
      </div>
      </div>      
      <div id="collapseOne" class="panel-collapse collapse in" role="tabpanel" aria-labelledby="headingOne">
        <div class="panel-body">
            <#assign leadSource = "${leadData.createSource!}"/>
            <#if !leadSource?has_content>
               <#assign leadSource = "${leadData.source!}"/>
            </#if>
        	<@dropdownBox 
	          id="leadSource"
	          label="Lead Source"
	          value= "${leadSource!}"
	          dataLiveSearch=true
	          allowEmptyValue="Lead Source"
	          options=lead_SourceList
	      	/>
			<@dropdownBox 
		          id="industry"
		          label="Industry"
		          value=leadData.industry
		          dataLiveSearch=true
		          allowEmptyValue="Industry"
		          options=industryList
	          />
		<@dropdownBox 
	          id="constitution"
	          label="Constitution"
	          dataLiveSearch=true
	          value=leadData.constitution
	          allowEmptyValue="Constitution"
	          options=constitutionList
          />
          
		
        <@inputBox name="dateOfIncorporation" id="demo-datetime" value=leadData.dateOfIncorporation label="Date of incorporation" type="text"/>
        
        <@inputBox name="noOfEmployees" id="noOfEmployees" value=leadData.noOfEmployees label="No. of Employees" type="text"/>
		<@decimalBox name="salesTurnover" id="salesTurnover" value=leadData.salesTurnover label="Sales turnover"  />
        
        
        <@dropdownBox 
	          id="tcpUser"
	          label="TCP User"
	          dataLiveSearch=true
	          value=leadData.tallyUserType
	          allowEmptyValue="Please Select"
	          options=tcpUserList
          />
          
        <@dropdownBox 
	          id="preferredLanguages"
	          label="Preferred Languages"
	          dataLiveSearch=true
	          value=leadData.preferredLanguages
	          allowEmptyValue="Please Select"
	          options=preferredLanguagesList
          />

        <@decimalBox name="paidupCapital" id="paidupCapital" value=leadData.paidupCapital label="Paid Up Capital" />
        
        <@inputBox name="gstn" id="gstn"  label="GSTIN" type="text" value=partyAttrs.gstn/>
		
		<@inputBox name="iecCode" id="iecCode"  label="IEC Code" type="text"  value=partyAttrs.iecCode/>
		
		<@inputBox name="cin" id="cin"  label="CIN" type="text"  value=partyAttrs.cin/>
		
		<@inputBox name="permanentAcccountNumber" id="permanentAcccountNumber" value=leadData.permanentAcccountNumber label="Company PAN" type="text" />
		
		
        </div>
      </div>
    </div>
    <div class="panel panel-default">
      <div class="panel-heading m15" role="tab" id="headingTwo">
        <div class="panel-title">
        <a class="collapsed" role="button" data-toggle="collapse" data-parent="#accordion" href="#collapseTwo" aria-expanded="false" aria-controls="collapseTwo">
          Company contact details
        </a>
      </div>
      </div>
      <div id="collapseTwo" class="panel-collapse collapse in" role="tabpanel" aria-labelledby="headingTwo">
        <div class="panel-body">
          <@inputBox name="primaryPhone" id="primaryPhone"  label="Main line 1" type="text"  value=primaryPhone!/>
          
		<#list aoAddress as address>
		<div class="form-group row">
         <div class="col-1"><i class="fa fa-map-marker" aria-hidden="true"></i></div>
         <div class="col-9 small"> <@nullChecked value=address.address1/> <@nullChecked value=address.address2/>
          <#if address.city?has_content>
          	${geoMap[address.city]} 
          </#if>
          <#if address.stateProvinceGeoId?has_content>
          	${geoMap[address.stateProvinceGeoId]} 
          </#if>
           
          
          <@nullChecked value=address.postalCode/> </div>
	  		<div class="col-2 text-right"><a onclick="return fnbankAttribute('editAddress?leadId=${leadId}&contactMechId=${address.contactMechId}');" class="text-secondary"><i class="fa fa-pencil" aria-hidden="true"></i></a></div>
      	</div>
      </#list>
	  <div class="text-center mt-4 mb-4 small "><a onclick="return fnbankAttribute('addAddress?leadId=${leadId}');">Add another address</a></div>
        </div>
      </div>
    </div>
	
    <div class="panel panel-default">
      <div class="panel-heading m15" role="tab" id="headingThree">
        <div class="panel-title">
        <a class="collapsed" role="button" data-toggle="collapse" data-parent="#accordion" href="#collapseThree" aria-expanded="false" aria-controls="collapseThree">
          Bank Details
        </a>
      </div>
      </div>
      <div id="collapseThree" class="panel-collapse collapse in" role="tabpanel" aria-labelledby="headingThree">
        <div class="panel-body">
			<div class="col-12 mb-2"><div class="row">Existing Banks (If Any)</div></div>
    		<#list banksLists as bank>
	    		<div class="form-group row">
	                <div class="col-1"><i class="fa fa-university" aria-hidden="true"></i></div>
	                <div class="col-9">
	                  <div class="small"><small class="text-secondary">Bank name</small></div>
	                  <span class="mb-3 small">${bank.bankName}</span>
	                  <div class="small"><small class="text-secondary">Facilities with other banks</small></div>
	                  <#list bank.banksProductsList as facility> 
	                  	
	                  	<div class="mb-1 small">${facility.productName!} - &#8377;${facility.productValue!}</div>
	                  </#list>
	                </div>
	                <div class="col-2 text-right"><a  onclick="return fnbankAttribute('addBank?leadId=${leadId}&companyBankSeqId=${bank.companyBankSeqId}');" class="text-secondary"><i class="fa fa-pencil" aria-hidden="true"></i></a></div>
	              </div>
		  </#list>
			    <div class="text-center mt-4 mb-4 small"><a onclick="return fnbankAttribute('addBank?leadId=${leadId}');"> Add another bank</a></div><#-- href="addBank?leadId=${leadId}" -->
        </div>
      </div>
    </div>
	  <div class="panel panel-default">
      <div class="panel-heading m15" role="tab" id="headingFour">
        <div class="panel-title">
        <a class="collapsed" role="button" data-toggle="collapse" data-parent="#accordion" href="#collapseFour" aria-expanded="false" aria-controls="collapseFour">
          Lead assignment
        </a>
      </div>
      </div>
      <div id="collapseFour" class="panel-collapse collapse in" role="tabpanel" aria-labelledby="headingFour">
        <div class="panel-body">
        <@contentBox  id="leadAssignedFrom"  label="Lead assigned from" value=assignByName/>
          <@contentBox  id="leadAssignedTo"  label="Lead assigned to" value=assignToName/>
       <#--  
            <div class="form-group row">
		<div class="col-12">									
		<div class="small text-secondary">Lead assigned from</div>
      <input class="form-control input-sm" placeholder="Source name goes here" Value="Source name goes here" />
        </div>
        </div>
        
		<div class="form-group row">
		<div class="col-12">									
		<div class="small text-secondary">Lead assigned to</div>
      <input class="form-control input-sm" placeholder="RM name goes here" Value="RM name goes here" />
        </div>
        </div>
         -->
        </div>
      </div>
    </div>
  </div>       
</div>
      
    <footer class="footer fixed-bottom border"> 
		<div class="container-fluid">
        <div class="col-12 p-1">
          <div class="row">
            <div class="col-6 border-right"> <a href="#" data-toggle="modal" data-target="#myModal" class="btn btn-sm rounded-0 btn-block text-dark">Cancel</a></div>
            <div class="col-6 p-0 submit-button">
              <input type="submit" class="btn btn-sm rounded-0 btn-block text-dark" id="submitButton" value="Save">		
            </div>
          </div>
        </div>
      </div>
    </footer>
<form>	
 	  
<div id="myModal" class="modal fade mt-5" role="dialog">
   <div class="modal-dialog">
     <!-- Modal content-->
    <div class="modal-content rounded-0 border-0">
      <div class=" border-bottom-0 pt-2 pb-2 bg-light1">
        <h4 class="text-center">Edit Lead</h4>
        <!--button type="reset" class="close" data-dismiss="modal">&times;</button!-->
          </div>
          <div class="modal-body pb-0">
            <div class="text-center pt-0 pb-5"> Are you sure you want to cancel editing Lead ?<br/>
            ${leadData.groupName}

</div>
            <div class="row border-top">
              <div class="col-6 p-0 border-right border-light"><a href="viewLead?partyId=${leadId}&msg=details" class="btn btn-sm bg-light1 rounded-0 btn-block text-dark">Yes</a></div>
              <div class="col-6 p-0"><a href=""  data-dismiss="modal" class="btn btn-sm bg-light1 rounded-0 btn-block text-dark">No</a></div>
            </div>
            <div class="clearfix"> </div>
          </div>
        </div>
      </div>
    </div> 

	<div id="DL" class="modal fade mt-5" role="dialog">
      <div class="modal-dialog">
        <!-- Modal content-->
    <div class="modal-content rounded-0 border-0">
      <div class=" border-bottom-0 pt-2 pb-2 bg-light1">
        <h4 class="text-center">Drop lead</h4>
        <!--button type="reset" class="close" data-dismiss="modal">&times;</button!-->
          </div>
          <div class="modal-body pb-0">
            <div class="text-center pt-0 pb-5"> Confirm dropping lead?

</div>
            <div class="row border-top">
              <div class="col-6 p-0 border-right border-light"><a href="allLeads" class="btn btn-sm bg-light1 rounded-0 btn-block text-dark">Yes</a></div>
              <div class="col-6 p-0"><a href="" data-dismiss="modal" class="btn btn-sm bg-light1 rounded-0 btn-block text-dark">No</a></div>
            </div>
            <div class="clearfix"> </div>
          </div>
        </div>
      </div>
    </div>

 <script>
  mobiscroll.settings = {
      lang: 'en',                           // Specify language like: lang: 'pl' or omit setting to use default
      theme: 'ios',                         // Specify theme like: theme: 'ios' or omit setting to use default
      display: 'center'                     // Specify display mode like: display: 'bottom' 'bubble" or omit setting to use default
  };
  
  $(function () {
  
      var now = new Date();
  
     
  
      // Mobiscroll Date & Time initialization
      $('#demo-datetime').mobiscroll().date({
          dateWheels: '|D M d|,|yy|',            // More info about dateWheels: https://docs.mobiscroll.com/4-5-0/datetime#localization-dateWheels
          dateFormat: 'mm/dd/yy', 
          onInit: function (event, inst) {  // More info about onInit: https://docs.mobiscroll.com/4-5-0/datetime#event-onInit
              inst.setVal(now, false);
          }
      });
     
  
  });
</script>

<script>
function enableButton()
  {
     var selectelem = document.getElementById('message');
     var btnelem = document.getElementById('seedoc');
     btnelem.disabled = !selectelem.value;
  }
  
  function fnbankAttribute(url){
  	var obj = {
	   leadSource: $('#leadSource').val(),
	   industry: $('#industry').val(),
	   constitution: $('#constitution').val(),
	   demodatetime: $('#demo-datetime').val(),
	   noOfEmployees: $('#noOfEmployees').val(),
	   salesTurnover: $('#salesTurnover').val(),
	   tcpUser: $('#tcpUser').val(),
	   preferredLanguages: $('#preferredLanguages').val(),
	   paidupCapital: $('#paidupCapital').val(),
	   gstn: $('#gstn').val(),
	   iecCode: $('#iecCode').val(),
	   cin: $('#cin').val(),
	   primaryPhone : $('#primaryPhone').val(),
	   permanentAcccountNumber: $('#permanentAcccountNumber').val()
   };
   var bankAttribute = JSON.stringify(obj);
   
	$.ajax({
	    url: 'setBankAttribute',
	    async: false,
	    type: "POST",
	    data: {
	        "bankAttribute": bankAttribute,
	    },
	    success: function (data) {
	        location.href = url; 
	    }
    });
  }
  
  $(document).ready(function(){
    initAccordion();
    //$('#submitButton').attr('disabled',true);
    $(":input").keyup(validate);
    
    var phone = $('#primaryPhone').val().trim();  
    if(phone.length == 0) {
    	$('#primaryPhone').val('+91 ');
    } else {
    	/*if(!phone.startsWith('+91 ')){
    		$('#primaryPhone').val('+91 '+phone);	
    	}*/
    }
    
    $("#primaryPhone").on("keypress, keydown", function(event) { 
           var phone = $(this).val();
           var readOnlyLength = 4;
	       if(phone != null && phone != "" && phone != undefined) {
	           if ((event.which != 37 && (event.which != 39)) && ((this.selectionStart < readOnlyLength) || ((this.selectionStart == readOnlyLength) && (event.which == 8)))) {
			        return false;
			   }
           } 
   });
   
   var flag = "";
   <#if parameters.flag! == 'edit'>
   		flag = "edit";
   	</#if>
   $.ajax({
	    url: 'getBankAttribute',
	    async: false,
	    type: "POST",
	    success: function(data){
	    	if(data != '' && flag != 'edit'){
	        	var obj = jQuery.parseJSON(data.bankAttribute);
	        	if(Object.keys(obj).length > 0){
		        	$('#leadSource').dropdown('set selected', obj.leadSource);
				    $('#industry').dropdown('set selected', obj.industry);
				    $('#constitution').dropdown('set selected', obj.constitution);
				    $('#demo-datetime').val(obj.demodatetime);
				    $('#noOfEmployees').val(obj.noOfEmployees);
				    $('#salesTurnover').val(obj.salesTurnover);
				    $('#tcpUser').dropdown('set selected', obj.tcpUser);
				    $('#preferredLanguages').dropdown('set selected', obj.preferredLanguages);
				    $('#paidupCapital').val(obj.paidupCapital);
				    $('#gstn').val(obj.gstn);
				    $('#iecCode').val(obj.iecCode);
				    $('#cin').val(obj.cin);
				    $('#primaryPhone').val(obj.primaryPhone),
				    $('#permanentAcccountNumber').val(obj.permanentAcccountNumber);
			    }
		    }
	    }
    });
   
   });
  
  function submitLead(){
  	//$('#submitButton').attr('disabled',true);
  	
	var submitForm = false;
	
	var valid = validate();
	
	if(valid){
		submitForm = true;
	}
		return valid;	
	
  }

  function validate() {
 
  	//$('#submitButton').attr('disabled',true);
  	  var valid = false;
  	  var tab=$("#currentTab").val();
  	  if(validateLeadInfo()){
	 // 		$('#submitButton').attr('disabled',false);
	  		valid = true;
	  	}
  	return valid;
  }
  
  
  function validateLeadInfo() {
  
  	  var valid = true;  
  	  var cinLcl=$("#cin").val().trim();
  	  var pan=$("#permanentAcccountNumber").val().trim();
  	  var iec=$("#iecCode").val().trim();
  	  var sto=$("#salesTurnover").val().trim();
  	  var noe=$("#noOfEmployees").val().trim();
  	  var puc=$("#paidupCapital").val().trim();
  	  var gst=$("#gstn").val().trim();
  	  var leadS=$("#leadSource").val().trim();
 	  var mob=$("#primaryPhone").val().trim();
 	  console.log(mob);
 	  if(leadS.length == 0) {
	  		valid = false;
	  		showError('tab','Please select lead source');
	  }else if(noe.length != 0 && !onlyNumbers(noe)) {
	  		valid = false;
	  		showError('tab','No. of Employees should contain only numbers');
	  }else if(sto.length != 0 && !validDecimals(sto)){
	  		valid = false;
	  		showError('tab','Sales turnover should contain only numbers');
	  }else if(puc.length != 0 && !onlyNumbers(puc)){
	  		valid = false;
	  		showError('tab','Paid Up Capital should contain only numbers');
	  }else if(gst.length != 0 && gst.length != 15) {
	  		valid = false;
	  		showError('tab','GSTN number should be of length 15');
	  }else if(gst.length != 0 && !validAlphanumeric(gst)){
	  		valid = false;
	  		showError('tab','Please enter valid GSTN number');
	  }else if(gst.length != 0 && onlyNumbers(gst)){
	  		valid = false;
	  		showError('tab','Please enter valid GSTN number');
	  }else if(gst.length != 0 && onlyAlphabets(gst)){
	  		valid = false;
	  		showError('tab','Please enter valid GSTN number');
	  }else if(iec.length != 0 && iec.length != 10) {
	  		valid = false;
	  		showError('tab','IEC Code should must be of length 10');
	  }else if(iec.length != 0 && !onlyNumbers(iec)){
	  		valid = false;
	  		showError('tab','Please enter valid IEC Code');
	  }else if(cinLcl.length != 0 && !(cinLcl.length == 8 || cinLcl.length == 21)){
	  		valid = false;
	  		showError('tab','CIN number should be of length 8 or 21');
	  }else if(cinLcl.length != 0 && !validAlphanumeric(cinLcl)){
	  		valid = false;
	  		showError('tab','Please enter valid cin number');
	  }else if(cinLcl.length != 0 && onlyAlphabets(cinLcl)){
	  		valid = false;
	  		showError('tab','Please enter valid cin number');
	  }else if(cinLcl.length != 0 && onlyNumbers(cinLcl)){
	  		valid = false;
	  		showError('tab','Please enter valid cin number');
	  }else if(pan.length != 0 && pan.length != 10) {
	  		valid = false;
	  		showError('tab','PAN number should only 10 characters');
	  }else if(pan.length != 0 && !validAlphanumeric(pan)){
	  		valid = false;
	  		showError('tab','Please enter valid company PAN');
	  }else if(pan.length != 0 && onlyAlphabets(pan)){
	  		valid = false;
	  		showError('tab','Please enter valid company PAN');
	  }else if(pan.length != 0 && onlyNumbers(pan)){
	  		valid = false;
	  		showError('tab','Please enter valid company PAN');
	  }else if(mob.length > 3 && !validPhone(mob)){
	  	valid = false;
	  }
	  if(validPhone(mob)){
         if($('#primaryPhone').val() != null){
              $("#isMainLine").val("Y");
              console.log('--isMainLine--'+$("#isMainLine").val());
          }
      }else{
        $("#isMainLine").val("N");
      }	
	  if(valid) {
  		hideError("tab");
  	  }
  	  return valid;
  }
    
  function showError(tab, errorMessage){
	
		$("#alertStatusTab1").show();
	    $("#displayMessageTab1").show();
	    $("#returnMessageTab1").html(errorMessage);
	
  		//$('#submitButton').addClass("disabled");		
  }
  
  function hideError(tab) {
  
		$("#displayMessageTab1").hide();

  		$('#submitButton').removeClass("disabled");		
  }
  
  
  

  $('.ui.dropdown.search').dropdown({
  	clearable: true
  });
    
</script>

