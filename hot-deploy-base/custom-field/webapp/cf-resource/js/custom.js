/*String.prototype.replaceAll = function(search, replace) {
    if (replace === undefined) {
        return this.toString();
    }
    return this.split(search).join(replace);
};

function showAlert (type, message) {
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
		delay: 1
	});
	
}

$('.pulsate-regular').pulsate({
    color: "#bf1c56"
});

$('.pulsate-once').pulsate({
    color: "#399bc3",
    repeat: false
});

$('.pulsate-crazy').pulsate({
    color: "#fdbe41",
    reach: 50,
    repeat: 10,
    speed: 100,
    glow: true
});
	
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
	
	$('.selectpicker').selectpicker('refresh');
}*/

