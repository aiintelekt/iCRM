var eventLst = [];
var dataSetParameter = "loggedInUserOpenActivities";

$(document).ready(function() {
	getUserOpenActivity();
	$('#calendar').fullCalendar({
		defaultView : 'month',
		navLinks : true,
		eventLimit : true,
		events : eventLst,
		header : {
			left : 'prev next',
			right : 'month agendaWeek agendaDay',
			center : 'title',
		}
	});
	
	$("#myCalendar").click(function(event) {
		event.preventDefault();
		dataSetParameter = "loggedInUserOpenActivities";
		getUserOpenActivity();
	});
	
	$("#myTeamCalendar").click(function(event) {
		event.preventDefault();
		dataSetParameter = "loggedInUserTeamActivities";
		getUserOpenActivity();
	});
	
});

function getUserOpenActivity() {
	var loggedInUser = document.getElementById("userLoggedIn").value;
	$('#calendar').fullCalendar('removeEventSource', eventLst);
	$.ajax({
		type : "POST",
		url : "getactivityHome",
		async : false,
		data : {
			"ownerUserLoginId" : loggedInUser,
			"systemfilter" : dataSetParameter,
			"isRequestFromViewCalendar" : "true"
		},
		success : function(data) {
			eventLst = [];
			for (var i = 0; i < data.length; i++) {
				eventLst.push({
					id : i,
					title : data[i].workEffortId,
					start : data[i].actualStartDateCal,
					allDay : true,
					end : data[i].actualCompletionDateCal,
					url : "viewActivity?workEffortId=" + data[i].workEffortId
				});
			}
		},
		error : function(data) {
			alert("error" + data);
			result = data;
			console.log('Error occured');
			showAlert("error", "Error occured while fetching activity Data!");
		}
	});
    $('#calendar').fullCalendar('addEventSource', eventLst);
    $('#calendar').fullCalendar('refetchEvents');
}