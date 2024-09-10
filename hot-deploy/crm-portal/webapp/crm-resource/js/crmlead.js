var selected_tab = 1;
var errorTab = 1;
$(document).ready(function() {
	progressBar();	
	disableSubmit();
	$('#tab1').show();
    $('#tab2').hide();
    $('#tab3').hide();
    $('#back').val("Cancel");
    $('#next').val("Next");
    
});

/** Next button logic */
function goNext() {
	if(selected_tab > 0 && !(validateCompanyInfo() && verifyLeadExists())){
		selected_tab = 1;
	}else if(selected_tab > 1 && !validateContactInfo()){
		selected_tab = 2;
	}else if(selected_tab > 2 && !validateLeadSource()){
		selected_tab = 3;
	}else{
		selected_tab++;
	}
	if(selected_tab < 4){
	    selected_tab = selected_tab > 3 ? 3 : selected_tab;
	    progressBar();
	    //disableSubmit();
	    myFunction(selected_tab);
	    return false;
	}else{
		return true;
	}
}

/** Back button logic */
function goBack() {
	if(selected_tab == 1){
    	window.location.href = 'main';
    }else{
	    selected_tab--;
	    selected_tab = selected_tab < 1 ? 1 : selected_tab;
	    hideError("tab");
	    progressBar();
	    myFunction(selected_tab);
    }
}

/** Next and Back button display function */
function myFunction(selected_tab) {

    switch (selected_tab) {
        case 1:
            $(document).ready(function () {
                $('#tab1').show();
                $('#tab2').hide();
                $('#tab3').hide();
                $('#back').val("Cancel");
                $('#next').val("Next");
            });
            return 0;
        case 2:
            $(document).ready(function () {
                $('#tab1').hide();
                $('#tab2').show();
                $('#tab3').hide();
                $('#back').val("Back");
                $('#next').val("Next");
            });
            return 0;
        case 3:
            $(document).ready(function () {
                $('#tab1').hide();
                $('#tab2').hide();
                $('#tab3').show();
                $('#back').val("Back");
                $('#next').val("Save");
            });
            return 0;
    }
}

/** Progress bar color changing logic*/
function progressBar(){
	var percent = (selected_tab/3) * 100;
	$('.progress-bar').css({width:percent+'%'});
}

/** Company constitution auto populate */
function wordMatch(){
	var companyName = $('#companyName').val().toLowerCase();
	if(companyName == ""){
		$('#constitution').dropdown('clear');
	}else{
		var private = /(pvt|priv|pte| p.l| pl| p \/ l| plimited| pltd| pv| p\/l| p l| p. l| \(p\) l)+/;
		if(/( limited liability partnership| llp| ll p| l lp| limited lp| l.p.| l l p| l. lp| l. p.| lp| l p| l.p)+/.test(companyName)){
			$('#constitution').dropdown('set selected', 'DBS_CONST_1004');
		} else if(/(embassy|high commission|consulate)+/.test(companyName)){
			$('#constitution').dropdown('set selected', 'DBS_CONST_1001');
		} else if(/(huf)+/.test(companyName)){
			$('#constitution').dropdown('set selected', 'DBS_CONST_1002');
		} else if(/(enterprises|enterprise|trader|associates)+/.test(companyName)){
			$('#constitution').dropdown('set selected', 'DBS_CONST_1012');
		} else if(/( ltd| limit| limited| public corporation)+/.test(companyName) && !private.test(companyName)){
			$('#constitution').dropdown('set selected', 'DBS_CONST_1007');
		} else if(private.test(companyName)){
			$('#constitution').dropdown('set selected', 'DBS_CONST_1006');
		} else {
	    	$('#constitution').dropdown('set selected', 'DBS_CONST_1012');
		}
	}
}

/** Company Info validation **/
function validateCompanyInfo() {
  	var valid = true;
  	var cname=$("#companyName").val().trim();
  	var pin=$("#pincode").val().trim();
  	if(cname.length == 0){
  		valid = false;
  		showError('Please enter company name');
  	}else if(!validMaxLength(cname,300)){
  		valid = false;
  		showError('Company name cannot be more than 300 characters ');
  	}else if($("#pincode").val().trim() != '' && $("#pincode").val().length != 6) {
  		valid = false;
  		showError('Pincode should be 6 characters');
  	}else if(pin.length != 0 && !onlyNumbers(pin)) {
  		valid = false;
  		showError('Pincode should must be numbers only');
  	}
  
  	if(valid) {
  		hideError("tab");
  	}
  	
  	return valid;
  }


/** Mobile number validation */
function validPhone(phone){
	
	var valid = true;
	var readOnlyLength = 4;
	if(phone != null && phone != "" && phone != undefined) {
        if ((event.which != 37 && (event.which != 39)) && ((this.selectionStart < readOnlyLength) || ((this.selectionStart == readOnlyLength) && (event.which == 8)))) {
        	valid = false;
		 }else{
		 	var countrycode = /(\+)?([0-9]{2})$/;
		   var phoneno = /([0-9]{10})$/;
		   var arr = phone.split(' ');
		   if(!countrycode.test(arr[0].trim())) {
	    		 showError('Invalid country code');
			      valid =  false; 
		   }else if(arr[1].trim().length != 0) {
    		 if(!/^(\+)?([0-9]{2})+[ ][0-9]+$/.test(phone) ){
    			 showError('Mobile number should accepts only numerics');
    			 valid =  false; 
    		 }else if(arr[1].trim().length > 10 || arr[1].trim().length < 10) { 
    			 showError('Mobile number should be 10 digits');
    			 valid =  false; 
    		 }else{
    			 hideError("tab");
    			 valid = true;
    	    } 	    
		   }
		   else{
			   hideError("tab");
			   valid =true;
		   }
		 }
	}
	return valid;
}

/**Email validation*/
function validEmail(email){
	var re = /^([A-Za-z0-9_\-\.])+\@([A-Za-z0-9_\-\.])+\.([A-Za-z]{2,4})$/;
	return re.test(email);
}

/**Maximum length logic*/
function validMaxLength(elem, maxLen) {
	var valid = true;
	if(elem != '' && elem.length > maxLen) {
		valid = false;
	}
	return valid;
}

/**Blank Field checking */
function isBlank(elem) {
	var valid = true;
	if(elem.val().trim() == ''){
		valid = false;
	}
	return valid;
}

function showError(errorMessage){
	$("#displayMessageTab").show();
    $("#returnMessageTab").html(DOMPurify.sanitize(errorMessage));
	disableSubmit();	
}

function hideError(tab) {
	$("#displayMessageTab").hide();
	enableSubmit();  			
}

function enableSubmit(){
	$('#next').removeClass("disabled");
	$('#next').attr('disabled',false);
}
  
function disableSubmit(){
  	$('#next').addClass("disabled");
	$('#next').attr('disabled',true);
}