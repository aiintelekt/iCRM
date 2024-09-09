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