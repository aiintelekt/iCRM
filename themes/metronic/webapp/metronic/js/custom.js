String.prototype.replaceAll = function(search, replace) {
    if (replace === undefined) {
        return this.toString();
    }
    return this.split(search).join(replace);
};

var notific8Settings = {
        theme: "ruby",
        //sticky: "N",
        horizontalEdge: "top",
        verticalEdge: "right",
        life: 5000
    };

$.notific8('zindex', 11500);

function showAlert (type, message) {
	
	if(type == "error") {
		notific8Settings.heading = "Error!";	
		notific8Settings.theme = "ruby";
	} else if(type == "warning") {
		notific8Settings.heading = "Warning!";
		notific8Settings.theme = "tangerine";
	} else if(type == "success") {
		notific8Settings.heading = "Success!";
		notific8Settings.theme = "teal";
	} else if(type == "info") {
		notific8Settings.heading = "Note!";
		notific8Settings.theme = "smoke";
	}
	
	$.notific8(message, notific8Settings);
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

AmCharts.loadJSON = function(url) {
	// create the request
	if (window.XMLHttpRequest) {
		// IE7+, Firefox, Chrome, Opera, Safari
		var request = new XMLHttpRequest();
	} else {
		// code for IE6, IE5
		var request = new ActiveXObject('Microsoft.XMLHTTP');
	}

	// load it
	// the last "false" parameter ensures that our code will wait before the
	// data is loaded
	request.open('GET', url, false);
	request.send();

	//alert(request.responseText);

	// parse and return the output
	//return eval("(" + request.responseText + ')');
	let responseText = "(" + request.responseText + ')';
	let cleanedResponse = xhr.responseText.replace(/\\x([0-9a-f]{2})/g, '\\u00$1');
	return JSON.parse(cleanedResponse);
};

$('.confirm-message').click(function(event){
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