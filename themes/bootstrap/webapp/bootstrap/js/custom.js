var Base64={_keyStr:"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=",encode:function(e){var t="";var n,r,i,s,o,u,a;var f=0;e=Base64._utf8_encode(e);while(f<e.length){n=e.charCodeAt(f++);r=e.charCodeAt(f++);i=e.charCodeAt(f++);s=n>>2;o=(n&3)<<4|r>>4;u=(r&15)<<2|i>>6;a=i&63;if(isNaN(r)){u=a=64}else if(isNaN(i)){a=64}t=t+this._keyStr.charAt(s)+this._keyStr.charAt(o)+this._keyStr.charAt(u)+this._keyStr.charAt(a)}return t},decode:function(e){var t="";var n,r,i;var s,o,u,a;var f=0;e=e.replace(/[^A-Za-z0-9\+\/\=]/g,"");while(f<e.length){s=this._keyStr.indexOf(e.charAt(f++));o=this._keyStr.indexOf(e.charAt(f++));u=this._keyStr.indexOf(e.charAt(f++));a=this._keyStr.indexOf(e.charAt(f++));n=s<<2|o>>4;r=(o&15)<<4|u>>2;i=(u&3)<<6|a;t=t+String.fromCharCode(n);if(u!=64){t=t+String.fromCharCode(r)}if(a!=64){t=t+String.fromCharCode(i)}}t=Base64._utf8_decode(t);return t},_utf8_encode:function(e){e=e.replace(/\r\n/g,"\n");var t="";for(var n=0;n<e.length;n++){var r=e.charCodeAt(n);if(r<128){t+=String.fromCharCode(r)}else if(r>127&&r<2048){t+=String.fromCharCode(r>>6|192);t+=String.fromCharCode(r&63|128)}else{t+=String.fromCharCode(r>>12|224);t+=String.fromCharCode(r>>6&63|128);t+=String.fromCharCode(r&63|128)}}return t},_utf8_decode:function(e){var t="";var n=0;var r=c1=c2=0;while(n<e.length){r=e.charCodeAt(n);if(r<128){t+=String.fromCharCode(r);n++}else if(r>191&&r<224){c2=e.charCodeAt(n+1);t+=String.fromCharCode((r&31)<<6|c2&63);n+=2}else{c2=e.charCodeAt(n+1);c3=e.charCodeAt(n+2);t+=String.fromCharCode((r&15)<<12|(c2&63)<<6|c3&63);n+=3}}return t}}

$(document).ready(function(){
	     
	 $(window).scroll(function() {
		if ($(this).scrollTop() > 50) {
			$('#back-to-top').fadeIn();
		} else {
			$('#back-to-top').fadeOut();
		}
	});
	// scroll body to 0px on click
	$('#back-to-top').click(function() {
		$('#back-to-top').tooltip('hide');
		$('body,html').animate({
			scrollTop : 0
		}, 800);
		return false;
	});

	$(".slidingDiv").hide();
    $(".show_hide").show();

    $('.show_hide').click(function () {
        $(".slidingDiv").toggle("slide");
    });
    
    $('.navbar .dropdown-item').on('click', function (e) {
        var $el = $(this).children('.dropdown-toggle');
        var $parent = $el.offsetParent(".dropdown-menu");
        $(this).parent("li").toggleClass('open');

        if (!$parent.parent().hasClass('navbar-nav')) {
            if ($parent.hasClass('show')) {
                $parent.removeClass('show');
                $el.next().removeClass('show');
                $el.next().css({"top": -999, "left": -999});
            } else {
                $parent.parent().find('.show').removeClass('show');
                $parent.addClass('show');
                $el.next().addClass('show');
                if ($el[0]) {
                	$el.next().css({"top": $el[0].offsetTop, "left": $parent.outerWidth() - 4});
                }
            }
           // e.preventDefault();
            e.stopPropagation();
        }
    });

    $('.navbar .dropdown').on('hidden.bs.dropdown', function () {
        $(this).find('li.dropdown').removeClass('show open');
        $(this).find('ul.dropdown-menu').removeClass('show open');
    });
	
    $('[data-toggle="tooltip"]').tooltip(); 
    
	$(function() {	
		if ($('#mainFrom').length) {
			var requiredDropdownList = [];
			var index = 0;
			$('#mainFrom .ui.dropdown').each(function( count ) {
		  		var ddId = $(this).find(">:first-child").attr("id");
		  		var isRequired = $(this).find(">:first-child").attr("required");
		  		if (isRequired) {
		  			requiredDropdownList[index++] = ddId;
		  		}
			});
			
			if (requiredDropdownList.length) {
				$('#mainFrom').validator().on('submit', function (e) {
					if (e.isDefaultPrevented()) {
				    	var errorMessage = '<ul class="list-unstyled"><li>Please fill out this field.</li></ul>';
				    	for (var i = 0; i < requiredDropdownList.length; i++) {
							$('#'+requiredDropdownList[i]+'_error').html("");
							if((jQuery.type($('#'+requiredDropdownList[i]).val()) == "array") && ($('#'+requiredDropdownList[i]).val().length == 0)) {
								$('#'+requiredDropdownList[i]+'_error').html(errorMessage);
							}
						}
				    	var firstInvalidInput;
						for (var i = 0; i < requiredDropdownList.length; i++) {
							if ($('#'+requiredDropdownList[i]) && !$('#'+requiredDropdownList[i]).val()) {
								if (!firstInvalidInput) {
									firstInvalidInput = requiredDropdownList[i];
								}
								$('#'+requiredDropdownList[i]+'_error').html(errorMessage);
							}
						}
						if (firstInvalidInput) {
							$('html, body').animate({ scrollTop: $('.'+firstInvalidInput).offset().top-100 }, 'slow');
						}
				  	}
				});
			}
		}
	});
	
	/*
	let url = document.URL;
	let hash = url.substring(url.indexOf('#'));
	$(".nav-tabs").find("li a").each(function(key, val) {
	    if (hash == $(val).attr('href')) {
	        $(val).click();
	    }
	    
	    $(val).click(function(ky, vl) {
	        location.hash = $(this).attr('href');
	    });
	});
	*/
	resetDefaultEvents();
	
});

(function () {
      	jQuery(document).ready(function () {
      		jQuery('#navbox-trigger').click(function () {
      			return jQuery('#navigation-bar').toggleClass('navbox-open');
      		});
      		return jQuery(document).on('click', function (e) {
      			var target;
      			target = jQuery(e.target);
      			if (!target.closest('.navbox').length && !target.closest('#navbox-trigger').length) {
      				return jQuery('#navigation-bar').removeClass('navbox-open');
      			}
      		});
      	});
      }.call(this));

 function openNav() {
		$('.openbtn').hide();
		$('.closebtni').show();
		$('.rightsidenav').show();
        document.getElementById("mySidenav").style.width = "250px";
        document.getElementById("main").style.marginLeft = "250px";
      }
      
 function closeNav() {
	    $('.closebtni').hide();
		$('.openbtn').show();
	    $('.rightsidenav').hide();		
        document.getElementById("mySidenav").style.width = "0px";
        document.getElementById("main").style.marginLeft= "15px";
      }

$(function () {
	$('#datetimepicker8').datetimepicker({format: 'YYYY/MM/DD'});
	$('#datetimepicker9').datetimepicker({format: 'YYYY/MM/DD'});         
	$('#datetimepicker10').datetimepicker({format: 'YYYY/MM/DD'});
	$('#datetimepicker11').datetimepicker({format: 'YYYY/MM/DD'});
	$('#datetimepicker6').datetimepicker({format: 'YYYY/MM/DD'});
	$('#datetimepicker7').datetimepicker({format: 'YYYY/MM/DD'});          
}); 

      
$(function () {
	$('[data-toggle="tooltip"]').tooltip()
})
	  
function initiateFromValidation(fromId) {
	if ($('#'+fromId).length) {
		var requiredDropdownList = [];
		var index = 0;
		$('#'+fromId+' .ui.dropdown').each(function( count ) {
	  		var ddId = $(this).find(">:first-child").attr("id");
	  		var isRequired = $(this).find(">:first-child").attr("required");
	  		if (isRequired) {
	  			requiredDropdownList[index++] = ddId;
	  		}
		});
		
		if (requiredDropdownList.length) {
			$('#'+fromId).validator().on('submit', function (e) {
				if (e.isDefaultPrevented()) {
			    	var errorMessage = '<ul class="list-unstyled"><li>Please fill out this field.</li></ul>';
			    	for (var i = 0; i < requiredDropdownList.length; i++) {
						$('#'+requiredDropdownList[i]+'_error').html("");
					}
			    	var firstInvalidInput;
					for (var i = 0; i < requiredDropdownList.length; i++) {
						if ($('#'+requiredDropdownList[i]).val().length === 0) {
							if (!firstInvalidInput) {
								firstInvalidInput = requiredDropdownList[i];
							}
							//console.log(`error message for ${requiredDropdownList[i]}`);
							$('#'+requiredDropdownList[i]+'_error').html(errorMessage);
						}
					}
					if (firstInvalidInput) {
						$('html, body').animate({ scrollTop: $('.'+firstInvalidInput).offset().top-100 }, 'slow');
					}
			  	}
			});
		}
	}
}

$('[data-toggle="slide-collapse"]').on('click', function() {
  $navMenuCont = $($(this).data('target'));
  $navMenuCont.animate({
    'width': 'toggle'
  }, 400);
  $(".menu-overlay").fadeIn(500);

});

$(".menu-overlay").click(function(event) {
  $(".navbar-toggle").trigger("click");
  $(".menu-overlay").fadeOut(500);
});





$(function() {
        var moveLeft = 20;
        var moveDown = 10;
        
        $('div#trigger').hover(function(e) {
          $('div#pop-up').show();
          //.css('top', e.pageY + moveDown)
          //.css('left', e.pageX + moveLeft)
          //.appendTo('body');
        }, function() {
          $('div#pop-up').hide();
        });
        
        $('div#trigger').mousemove(function(e) {
          $("div#pop-up").css('top', e.pageY + moveDown).css('left', e.pageX + moveLeft);
        });
        
      });

$(function() {
        var moveLeft = 20;
        var moveDown = 10;
        
        $('div#trigger2').hover(function(e) {
          $('div#pop-up2').show();
          //.css('top', e.pageY + moveDown)
          //.css('left', e.pageX + moveLeft)
          //.appendTo('body');
        }, function() {
          $('div#pop-up2').hide();
        });
        
        $('div#trigger2').mousemove(function(e) {
          $("div#pop-up2").css('top', e.pageY + moveDown).css('left', e.pageX + moveLeft);
        });
        
      });

	
	  
$('.ui.dropdown.search').dropdown({
		clearable: true
	});	 



/*............costome profile..........*/


        $(function() {
            $("#accordion").accordion({
                collapsible: true
            });
        });

// datepicker

/*
   $('#sandbox-container .input-group.date').datepicker({
});

*/
$('.date').datetimepicker({
  useCurrent: false,
  format: 'DD/MM/YYYY'
});

$('.datetime').datetimepicker({
	  useCurrent: false,
	  format: 'DD/MM/YYYY HH:mm:ss'
});
 $(function () {
   var bindDatePicker = function() {
		$(".date11").datetimepicker({
        format:'DD-MM-YYYY',
			icons: {
				time: "fa fa-clock-o",
				date: "fa fa-calendar",
				up: "fa fa-arrow-up",
				down: "fa fa-arrow-down"
			}
		}).find('input:first').on("blur",function () {
			// check if the date is correct. We can accept dd-mm-yyyy and yyyy-mm-dd.
			// update the format if it's yyyy-mm-dd
			var date = parseDate($(this).val());

			if (! isValidDate(date)) {
				//create date based on momentjs (we have that)
				date = moment().format('YYYY-MM-DD');
			}

			$(this).val(date);
		});
	}
   
   var isValidDate = function(value, format) {
		format = format || false;
		// lets parse the date to the best of our knowledge
		if (format) {
			value = parseDate(value);
		}

		var timestamp = Date.parse(value);

		return isNaN(timestamp) == false;
   }
   
   var parseDate = function(value) {
		var m = value.match(/^(\d{1,2})(\/|-)?(\d{1,2})(\/|-)?(\d{4})$/);
		if (m)
			value = m[5] + '-' + ("00" + m[3]).slice(-2) + '-' + ("00" + m[1]).slice(-2);

		return value;
   }
   
   bindDatePicker();
 });
/*............costome profile..........*/

	  

$(".navbar-nav a").filter(function(){
    return this.href == location.href.replace(/#.*/, "");
}).parent().parent().addClass("active");
 	  
$(".navbar-nav a").filter(function(){
    return this.href == location.href.replace(/#.*/, "");
}).parent().parent().parent().addClass("active");

$(".navbar-nav a").filter(function(){
    return this.href == location.href.replace(/#.*/, "");
}).parent().parent().parent().parent().addClass("active");

$(".navbar-nav a").filter(function(){
    return this.href == location.href.replace(/#.*/, "");
}).parent().parent().parent().parent().parent().addClass("active");

$(".navbar-nav a").filter(function(){
    return this.href == location.href.replace(/#.*/, "");
}).parent().parent().parent().parent().parent().parent().addClass("active");


/*---------set value----------*/
function set_value(formName,value1,value2){
	
}
function add_value(formName,value1,field1){
	if(formName !=null && formName !="" && formName !="undefined"){
		console.log("Form Name : "+formName);
		$("#"+formName+" input[name="+field1+"]").val(value1);
		var modalId = $("#"+formName+" input[name=modalId]").val();
		if(modalId !=null && modalId !="" && modalId !="undefined"){
			$("#"+modalId).hide();
		}
		$("#"+formName).submit();
		console.log("form submitted..")
	} else{
		console.log("Form Name empty..")
	}
	
}

function getDependentDropdownValues(request, paramKey, paramField, targetField, responseName, keyName, descName, selected, callback, allowEmpty, hide, hideTitle, inputField){
// To dynamically populate a dependent drop-down on change on its parent drop-down, doesn't require any fixed naming convention 
// request      = request calling the service which retrieve the info from the DB, ex: getAssociatedStateList
// paramKey     = parameter value used in the called service 
// paramField   = parent drop-down field Id (mainId)
// targetField  = dependent drop-down field Id (dependentId)
// responseName = result returned by the service (using a standard json response, ie chaining json request)
// keyName      = keyName of the dependent drop-down  
// descName     = name of the dependent drop-down description
// selected     = optional name of a selected option
// callback     = optional javascript function called at end
// allowEmpty   = optional boolean argument, allow selection of an empty value for the dependentId
// hide         = optional boolean argument, if true the dependent drop-down field (targetField) will be hidden when no options are available else only disabled. False by default.
// hideTitle    = optional boolean argument (hide must be set to true), if true the title of the dependent drop-down field (targetField) will be hidden when no options are available else only disabled. False by default.
// inputField   = optional name of an input field    
//             this is to handle a specific case where an input field is needed instead of a drop-down when no values are returned by the request
//             this will be maybe extended later to use an auto-completed drop-down or a lookup, instead of straight drop-down currently, when there are too much values to populate
//             this is e.g. currently used in the Product Price Rules screen
    target = '#' + targetField;
    input = '#' + inputField;
    targetTitle = target + '_title'
    optionList = '';
    var paramValue = jQuery('#' + paramField).val();
    if(paramValue != null && paramValue !="" && paramValue !="undefined") {
	    jQuery.ajax({
	        url: request,
	        data: [{
	            name: paramKey,
	            value: jQuery('#' + paramField).val()
	        }], // get requested value from parent drop-down field
	        async: false,
	        type: 'POST',
	        success: function(result){
	            list = result[responseName];
	            // Create and show dependent select options            
	            if (list) {
	                if(allowEmpty) {
	                    // Allow null selection in dependent and set it as default if no selection exists.
	                    if (selected == undefined || selected == "_none_") {
	                      optionList += "<option selected='selected' value=''></option>";
	                    } else {
	                      optionList += "<option value=''></option>";
	                    }
	                }
	                jQuery.each(list, function(key, value){
	                    if (typeof value == 'string') {
	                        values = value.split(': ');
	                        if (values[1].indexOf(selected) >= 0 && selected.length > 0) {
	                            optionList += "<option selected='selected' value = " + values[1] + " >" + values[0] + "</option>";
	                        } else {
	                            optionList += "<option value = " + values[1] + " >" + values[0] + "</option>";
	                        }
	                    } else {
	                        if (value[keyName] == selected) {
	                            optionList += "<option selected='selected' value = " + value[keyName] + " >" + value[descName] + "</option>";
	                        } else {
	                            optionList += "<option value = " + value[keyName] + " >" + value[descName] + "</option>";
	                        }
	                    }
	                })
	            };
	            // Hide/show the dependent drop-down if hide=true else simply disable/enable
	            if ((!list) || (list.length < 1) || ((list.length == 1) && jQuery.inArray("_NA_", list) != -1)) {
	                jQuery(target).attr('disabled', 'disabled');
	                if (hide) {
	                    if (jQuery(target).is(':visible')) {
	                        jQuery(target).fadeOut(2500);
	                        if (hideTitle) jQuery(targetTitle).fadeOut(2500);
	                    } else {
	                        jQuery(target).fadeIn();
	                        if (hideTitle) jQuery(targetTitle).fadeIn();
	                        jQuery(target).fadeOut(2500);
	                        if (hideTitle) jQuery(targetTitle).fadeOut(2500);
	                    }
	                }
	            } else {
	                jQuery(target).removeAttr('disabled');
	                if (hide) {
	                    if (!jQuery(target).is(':visible')) {
	                        jQuery(target).fadeIn();
	                        if (hideTitle) jQuery(targetTitle).fadeIn();
	                    }
	                }
	            }
	        },
	        complete: function(){
	            // this is to handle a specific case where an input field is needed instead of a drop-down when no values are returned by the request (else if allow-empty="true" is used autoComplete handle the case)
	            // this could be extended later to use an auto-completed drop-down or a lookup, instead of drop-down currently, when there are too much values to populate
	            // Another option is to use an input field with Id instead of a drop-down, see setPriceRulesCondEventJs.ftl and top of getAssociatedPriceRulesConds service
	            if (!list && inputField) {
	                jQuery(target).hide();
	                jQuery(input).show();
	            } else if (inputField) {
	                jQuery(input).hide();
	                jQuery(target).show();
	            }
	            jQuery(target).html(optionList).click().change(); // .change() needed when using also asmselect on same field, .click() specifically for IE8
	            if (callback != null) eval(callback);
	        }
	    });
    } 
}

//*** calls any service already mounted as an event
// arguments must be either a request only (1st argument) or a request followed by {name;value} pair/s parameters 
function getServiceResult(){
    var request = arguments[0];
    var params =  new Array();
    var data;
    jQuery.ajax({
        type: 'POST',
        url: request,
        data: prepareAjaxData(arguments),
        async: false,
        cache: false,
        success: function(result){
            data = result;
        }
    });
    return data;
}

function prepareAjaxData(params) {
  var data = new Array();
  if (params.length > 1) {
    for (var i = 1; i < params.length; i++) {
      data.push({
        name: params[i],
        value: params[i + 1]
    });
      i++;
    }
  }
    return data;
}

//*** checkUomConversion returns true if an UomConversion exists
function checkUomConversion(request, params){
    data = getServiceResult(request, params);
    return data['exist'];
}

/* initTimeZone is used to intialise the path to timezones files
  
The timezone region that loads on initialization is North America (the Olson 'northamerica' file). 
To change that to another reqion, set timezoneJS.timezone.defaultZoneFile to your desired region, like so:
  timezoneJS.timezone.zoneFileBasePath = '/tz';
  timezoneJS.timezone.defaultZoneFile = 'asia';
  timezoneJS.timezone.init();

If you want to preload multiple regions, set it to an array, like this:

  timezoneJS.timezone.zoneFileBasePath = '/tz';
  timezoneJS.timezone.defaultZoneFile = ['asia', 'backward', 'northamerica', 'southamerica'];
  timezoneJS.timezone.init();

By default the timezoneJS.Date timezone code lazy-loads the timezone data files, pulling them down and parsing them only as needed. 

For example, if you go with the out-of-the-box setup, you'll have all the North American timezones pre-loaded -- 
but if you were to add a date with a timezone of 'Asia/Seoul,' it would grab the 'asia' Olson file and parse it 
before calculating the timezone offset for that date.

You can change this behavior by changing the value of timezoneJS.timezone.loadingScheme. The three possible values are:

  timezoneJS.timezone.loadingSchemes.PRELOAD_ALL -- this will preload all the timezone data files for all reqions up front. This setting would only make sense if you know your users will be using timezones from all around the world, and you prefer taking the up-front load time to the small on-the-fly lag from lazy loading.
  timezoneJS.timezone.loadingSchemes.LAZY_LOAD -- the default. Loads some amount of data up front, then lazy-loads any other needed timezone data as needed.
  timezoneJS.timezone.loadingSchemes.MANUAL_LOAD -- Preloads no data, and does no lazy loading. Use this setting if you're loading pre-parsed JSON timezone data.
  
  More at https://github.com/mde/timezone-js
  
*/  
function initTimeZone() {
  timezoneJS.timezone.zoneFileBasePath = '/images/date/timezones/min';
  timezoneJS.timezone.loadingSchemes.PRELOAD_ALL;
  timezoneJS.timezone.init();
}

function initDateRange(fromDatePanelId, thruDatePanelId, fromDateValue, thruDateValue) {
	if (fromDateValue) {
		$('#'+thruDatePanelId).data("DateTimePicker").minDate(moment(fromDateValue).format('YYYY-MM-DD'));
	}
	if (thruDateValue) {
		$('#'+fromDatePanelId).data("DateTimePicker").maxDate(moment(thruDateValue).format('YYYY-MM-DD'));
	}
	
	$("#"+fromDatePanelId).on("dp.change", function (e) {
     	$('#'+thruDatePanelId).data("DateTimePicker").minDate(e.date);
   	});      
   	$("#"+thruDatePanelId).on("dp.change", function (e) {
       $('#'+fromDatePanelId).data("DateTimePicker").maxDate(e.date);
   	});
}

function validateDateRange(fromDate, fromTime, thruDate, thruTime) {
	var valid = true;
	if (fromDate && thruDate) {
		if (fromTime) {
			fromDate = fromDate + " " + fromTime;
		}
		if (thruTime) {
			thruDate = thruDate + " " + thruTime;
		} 
				
		fromDate = moment(fromDate).format('YYYY-MM-DD HH:mm');
		thruDate = moment(thruDate).format('YYYY-MM-DD HH:mm');
		
		//alert("fromDate> "+fromDate+", thruDate> "+thruDate)
		if ((fromDate - thruDate) == 0) {
			valid = true;
		} else if (fromDate > thruDate) {
			valid = false;
		}
	}
	return valid;
}

function showAlert (type, message) {
	showAlert(type, message, 20000);
}

function showAlert (type, message, delay) {
   showAlert (type, message, delay, null); 
}

function showAlert (type, message, delay, refreshClosed) {
    var notifyType = "info";
    if(type == "error") {
        notifyType = "danger";
    } else if(type == "warning") {
        notifyType = "warning";
    } else if(type == "success") {
        notifyType = "success";
    } else if(type == "info") {
        notifyType = "info";
    }
    $.notify({
        // options
        message: message
    },{
        // settings
        type: notifyType,
        delay: delay,
        timer:1000,
        mouse_over: 'pause',
		onClosed: refreshClosed
    });	
}

resetDefaultEvents();
function resetDefaultEvents () {
	
	$(".tooltip").tooltip("hide");
	$('.tooltips').tooltip();
	
	$('.confirm-message').unbind( "click" );
	$('.confirm-message').bind( "click", function( event ) {
		event.preventDefault(); 
		
		var href = $(this).attr('href');
		var message = $(this).data('message');
		if (!$.trim(message)) {
			message = "Are you sure?";
		}
		
		bootbox.confirm(message, function(result) {
			if (result) {
				window.location.href = href;
			}
	    });
	});
	
	$('[data-toggle="confirmation"]').confirmation({
			singleton: false,
		    popout: false
	});
	//$('.selectpicker').selectpicker('refresh');
	
}

function validateUniqueId(request, entityName, fieldName, fieldValue){
    var response;
    jQuery.ajax({
        url: request,
        data: { "entityName": entityName , "fieldName":fieldName,"fieldValue": jQuery('#' + fieldValue).val()},
        async: false,
        type: 'POST',
        success: function(result){
        	response = result;
        }
    });
    return response;
}

function addAttachmentRepeateContent (actionButton) {
	var cloneHtml = $(actionButton).closest( ".file-content" ).clone();
	if ( $(actionButton).parent().children().find('[class=\"fa fa-minus-circle\"]').length == 0 ) {
        cloneHtml.children().find('[class=\"fa fa-plus-circle\"]').parent().before('<a class="plus-icon01 rd ml-1" onclick="removeAttachmentRepeateContent(this)"><i class="fa fa-minus-circle" aria-hidden="true"></i></a>');
    }
    cloneHtml.children().find('.form-control').val("");
    $(actionButton).closest( ".file-content" ).after(cloneHtml);
}
function removeAttachmentRepeateContent (actionButton) {
	$(actionButton).closest( ".file-content" ).remove();
}

$('.picker-window').keypress(function (e) {
	var key = e.which;
	if(key == 13)  // the enter key code
	{
		$('.picker-window').click();
	    return false;  
	}
}); 
$('.picker-window-erase').keypress(function (e) {
	var key = e.which;
	if(key == 13)  // the enter key code
	{
		$('.picker-window-erase').click();
	    return false;  
	}
}); 

var parentPickerInputId;
var parentPickerWindowId;
var currentPickerInputId;
var currentPickerWindowId;
$('.picker-window').click(function () {
	var pickerWindowId = $(this).attr("data-pickerWindow");
	var pickerInputId = $(this).attr("data-pickerInputId");
	if (pickerWindowId) {
		$('#'+pickerWindowId).modal('show');
	}
	currentPickerInputId = pickerInputId;
	currentPickerWindowId = pickerWindowId;
	if (parentPickerInputId === "" && parentPickerWindowId === "" || parentPickerInputId === null && parentPickerWindowId === null
		|| parentPickerInputId === undefined && parentPickerWindowId === undefined ) {
		parentPickerInputId = pickerInputId;
		parentPickerWindowId = pickerWindowId;
	}
});
$('.picker-window-erase').click(function () {
	var pickerWindowId = $(this).attr("data-pickerWindow");
	var pickerInputId = $(this).attr("data-pickerInputId");
	currentPickerInputId = "";
	currentPickerWindowId = "";
	console.log('pickerInputId> '+pickerInputId);
	$('#'+pickerInputId+'_val').val("");
	$('#'+pickerInputId+'_alter').val("");
	$('#'+pickerInputId+'_desc').val("");
	$('#'+pickerInputId+'_desc').change();
});
function setPickerWindowValue(desc, val) {
	$('#'+currentPickerInputId+'_alter').val("");
	//console.log('parentPickerInputId: '+parentPickerInputId+', parentPickerWindowId: '+parentPickerWindowId);
	currentPickerInputId = currentPickerInputId ? currentPickerInputId : parentPickerInputId;
	currentPickerWindowId = currentPickerWindowId ? currentPickerWindowId : parentPickerWindowId;
	$('#'+currentPickerWindowId).modal('hide');
	console.log('currentPickerInputId: '+currentPickerInputId);
	$('#'+currentPickerInputId+'_desc').val(desc);
	$('#'+currentPickerInputId+'_val').val(val);
	$('#'+currentPickerInputId+'_alter').val(desc);
	$('#'+currentPickerInputId+'_desc').trigger('change');
	if (currentPickerInputId === parentPickerInputId && currentPickerWindowId === parentPickerWindowId) {
		parentPickerInputId = "";
		parentPickerWindowId = "";
	}
}
function setParentPickerWindowValue(desc, val) {
	$('#' + parentPickerInputId + '_alter').val("");
	parentPickerInputId = parentPickerInputId ? parentPickerInputId : currentPickerInputId;
	parentPickerWindowId = parentPickerWindowId ? parentPickerWindowId : currentPickerWindowId;
	$('#' + parentPickerWindowId).modal('hide');
	$('#' + parentPickerInputId + '_desc').val(desc);
	$('#' + parentPickerInputId + '_val').val(val);
	$('#' + parentPickerInputId + '_alter').val(desc);
	$('#' + parentPickerInputId + '_desc').trigger('change');
	if (parentPickerInputId === currentPickerInputId && parentPickerWindowId === currentPickerWindowId) {
		parentPickerInputId = "";
		parentPickerWindowId = "";
	}
}
var getUrlParameter = function getUrlParameter(sParam) {
    var sPageURL = window.location.search.substring(1),
        sURLVariables = sPageURL.split('&'),
        sParameterName,
        i;

    for (i = 0; i < sURLVariables.length; i++) {
        sParameterName = sURLVariables[i].split('=');

        if (sParameterName[0] === sParam) {
            return sParameterName[1] === undefined ? true : decodeURIComponent(sParameterName[1]);
        }
    }
};



$.fn.insertAtCaret = function(myValue) {
	return this.each(function() {
		// IE support
		if (document.selection) {
			this.focus();
			sel = document.selection.createRange();
			sel.text = myValue;
			this.focus();
		}
		// MOZILLA / NETSCAPE support
		else if (this.selectionStart || this.selectionStart == '0') {
			var startPos = this.selectionStart;
			var endPos = this.selectionEnd;
			var scrollTop = this.scrollTop;
			this.value = this.value.substring(0, startPos) + myValue
					+ this.value.substring(endPos, this.value.length);
			this.focus();
			this.selectionStart = startPos + myValue.length;
			this.selectionEnd = startPos + myValue.length;
			this.scrollTop = scrollTop;
		} else {
			this.value += myValue;
			this.focus();
		}
	});
}

$(':reset').on('click', function(evt) {
    evt.preventDefault();
    $form = $(evt.target).closest('form');
    if ($form) {
    	//$form[0].reset();
        var formId = $form.attr('id');
        if (formId) {
        	$("form#"+formId+" input[type=text]").not("[readonly=readonly],[readonly=true],[disabled=true],[disabled=disabled]").each(function(){
        		 $(this).val('');  
        		 if($('#'+$(this)[0].id+'_error')!=undefined){
       			 $('#'+$(this)[0].id+'_error').html(''); 
        		 }
        	});        	
        }
        $form.find('textarea').val('');
        $form.find(':radio').removeAttr('checked');
        $form.find(':checkbox').removeAttr('checked');

        $form.find("input[type='checkbox']").each(function() {
            this.checked = false;
        });
        $form.find("input[type='radio']").each(function() {
            this.checked = false;
        });
        $form.find('.ui.dropdown').dropdown('clear');
        $form.find('.dropdown.icon.clear').removeClass('clear');
        $form.find('.with-errors').each(function(){
        	$(this).html('');
    	});
    }
});

function resetForm() {
	
}

String.prototype.replaceAll = function (find, replace) {
    var str = this;
    return str.replace(new RegExp(find.replace(/[-\/\\^$*+?.()|[\]{}]/g, '\\$&'), 'g'), replace);
};

String.prototype.wrapText = function (maxLength) {
    var str = this;
    if (str && str.length<=maxLength) {
    	return str;
    } else {
    	return str.substring(0, maxLength)+"...";
    }
};

function isEmptyCKEd(instanceId){
	var valid = false;
	var isImageIn = false;
	if(instanceId.document.getBody().getHtml().match(/<img/)) {
		$('img').each(function(){
		    if (this.src.length > 0) {
		    	isImageIn = true;
		    }
		}); 
	} 
	if(!isImageIn && validateEmptySpaces((instanceId.getData().replace(/<[^>]*>|\s/g, '')).trim())){
        return true;
	}
 	return valid;
}

function validateEmptySpaces(field) {
	var vArray = new Array();
	vArray = field.split("&nbsp;");
	var vFlag = 0;
	for(var i=0;i<vArray.length;i++){
		if(vArray[i] == '' || vArray[i] == "") {
			continue;
		}else {
			vFlag = 1;
			break;
		}
	}
	if(vFlag == 0) {
		return true;
	}else {
		return false;
	}
}

function validatePostalCode(fieldId, regex){
	var isInvalid = false;
	if(regex != ''){
		var re = new RegExp(regex);
		if (re.test($('#'+fieldId).val())) {
      		$('#'+fieldId+'_error').html('');
      	} else{console.log(regex, $('#'+fieldId).val(), 'regex2');
      		$('#'+fieldId+'_error').html('Please enter the valid zip code');
      		isInvalid = true;
      	}
  	}
  	return isInvalid;
}

function validatePostalCodeExt(fieldId){
	var isInvalid = false;
	if($('#'+fieldId).val() != "" && $('#'+fieldId).val()=="USA"){
		var re = new RegExp("^([0-9]{4})$");
		if (re.test($('#'+fieldId).val())) {
	  		$('#'+fieldId+'_error').html('');
	  	}else{
	  		$('#'+fieldId+'_error').html('Please enter the valid zip code extension');
	  		isInvalid = true;
	  	}
  	}
  	else
  		$('#'+fieldId+'_error').html('');
  	return isInvalid;
}

function map_to_object(map) {
    const out = Object.create(null)
    map.forEach((value, key) => {
      if (value instanceof Map) {
        out[key] = map_to_object(value)
      }
      else {
        out[key] = value
      }
    })
    return out
}

$( ".box-animate" ).on( "click", function() {
	var elementId = $(this).attr('id');
	var effectType = $(this).attr('effettype');
	if(effectType !=null && effectType !="" && effectType !="undefined"){
		$(this).effect( effectType,{
			 distance: 5,
			 times: 2}, 500,dynamic_call_back(elementId));
	}
	$(".box-animate").not(this).removeClass("selected-element-b"); //Added this statement
	$(this).addClass( "selected-element-b");
	
	//return false;
});

function dynamic_call_back(elementId){
	setTimeout(function() {
		if ($.isFunction(window.load_dynamic_data) ) {
			load_dynamic_data(elementId);
		 }
	 }, 500 );
}

const highlightText = (newElem, oldElem, highlightClass) => { 
  var oldText = oldElem.text(),     
      text = '';
  newElem.text().split('').forEach(function(val, i){
    if (val != oldText.charAt(i))
      text += "<span class='"+highlightClass+"'>"+val+"</span>";  
    else
      text += val;            
  });
  newElem.html(text); 
}

$("#email-refresh-btn").on("click", function() {
	console.log("mail user login :"+$('#refreshMailUserLogin').val());
	$("#email-refresh-btn").html("<img src='/bootstrap/images/ajax-loader.gif' />");
	jQuery.ajax({
        url: "/admin-portal/control/refreshEmailDownload",
        data: { "userLogin": $('#refreshMailUserLogin').val(),"externalLoginKey":$("#externalLoginKey").val()},
        async: true,
        type: 'POST',
        success: function(result){
        	var responseCode = result.responseMessage;
        	if("success" === responseCode){
        		showAlert("success",result.successMessage);
        		location.reload();
        	} else{
        		showAlert("error",result.errorMessage);
        	}
        }
    });
	$("#email-refresh-btn").html("<i class='fa fa-refresh' aria-hidden='true'></i>");
});

function autoScrollToTabContent(tabContentId){
	var target = $('#'+tabContentId);
	if (target.length) {
	    $('html,body').animate({
	        scrollTop: target.offset().top
	    }, 1000);
	    return false;
	}
}

function togglePassword(inputId, iconId) {
	var passwordInput = document.getElementById(inputId);
	var passwordIcon = document.getElementById(iconId);
	if (passwordInput.type === 'password') {
		passwordInput.type = 'text';
		passwordIcon.classList.remove("fa-eye-slash");
		passwordIcon.classList.add("fa-eye");
	} else {
		passwordInput.type = 'password';
		passwordIcon.classList.remove("fa-eye");
		passwordIcon.classList.add("fa-eye-slash");
	}
}

let notifyClosedFn = () => {
	location.reload(true);
}

function deserialize(serializedJavascript) {
    return eval("(" + serializedJavascript + ")");
}
function isObjectEmpty(obj) {
  return Object.getOwnPropertyNames(obj).length === 0;
}

function loadJS(FILE_URL, async = false) {
  let scriptEle = document.createElement("script");

  scriptEle.setAttribute("src", FILE_URL+'?' + new Date().getTime());
  scriptEle.setAttribute("type", "text/javascript");
  scriptEle.setAttribute("async", async);

  document.body.appendChild(scriptEle);

  // success event 
  scriptEle.addEventListener("load", () => {
    console.log("File loaded")
  });
   // error event
  scriptEle.addEventListener("error", (ev) => {
    console.log("Error on loading file", ev);
  });
}

function sanitize(val) {
	if (!val) {
		if (val=='0' || val==0) {
			return val;
		} else {
			return DOMPurify.sanitize(val);
		}
	}
	return val;
}
