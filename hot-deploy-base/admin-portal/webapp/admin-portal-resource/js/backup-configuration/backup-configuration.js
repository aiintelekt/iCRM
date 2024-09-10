$("#coordinator").on("change", function() {
	if (this.value) {
        $("#coordinator_error").html('');
    }
	loadBackupCoordinator();
	
});
	
function loadCoordinator() {
	var nonSelectContent = "<span class='nonselect'>Please Select</span>";
	var coordinatorOptions = '<option value="" data-content="'+nonSelectContent+'" selected="">Please Select</option>';
	
	//var coordinatorOptions = '';
	//$("#coordinator").html(coordinatorOptions).change();
	$.ajax({
		type: "GET",
		url: "getCoordinatorBackupList",
		data: { "type": "COORDINATOR"},
		async: false,
		success: function(data) {
			for (var i = 0; i < data.length; i++) {
				var coord = data[i];
				if(coord){
					coordinatorOptions += '<option value="'+coord.partyId+'">'+coord.name+'</option>';
        		}
			}
			//$("div.ui.dropdown.search.form-control.fluid.show-tick.coordinator.selection > i").addClass("clear");

		}
	});
	
	$("#coordinator").dropdown('clear');
	$("#coordinator").html(coordinatorOptions);
	$("#coordinator").dropdown('refresh');
}

function loadBackupCoordinator() {
	var nonSelectContent = "<span class='nonselect'>Please Select</span>";
	var backupOptions = '<option value="" data-content="'+nonSelectContent+'" selected="">Please Select</option>';
	
	var coordinator = $("#coordinator").val();
	
	//var backupOptions = '';
	//$("#backupCoordinator").html(backupOptions).change();
	$.ajax({
		type: "GET",
		url: "getCoordinatorBackupList",
		data: { "type": "BACKUP_COORDINATOR", "selectedPartyId":coordinator },
		async: false,
		success: function(data) {
			for (var i = 0; i < data.length; i++) {
				var backupCoord = data[i];
				if(backupCoord){
					backupOptions += '<option value="'+backupCoord.partyId+'">'+backupCoord.name+'</option>';
        		}
			}
			//$("div.ui.dropdown.search.form-control.fluid.show-tick.backupCoordinator.selection > i").addClass("clear");
		}
	});
	$("#backupCoordinator").dropdown('clear');
	$("#backupCoordinator").html(backupOptions);
	$("#backupCoordinator").dropdown('refresh');
}

$("#backupCoordinator").change(function() {
    if (this.value) {
        $("#backupCoordinator_error").html('');
    }
});
$("#back_config_btn").on("click", function() {
	$("#coordinator_error").html('');
	$("#backupCoordinator_error").html('');
	let coordinator = $("#coordinator").val();
	let backupCoordinator = $("#backupCoordinator").val();
	let valid = true;
	
	if(!coordinator){
		valid = false;
		$("#coordinator_error").html('<ul class="list-unstyled"><li>Please select an item in the list.</li></ul>');
	}
	if(!backupCoordinator){
		valid = false;
		$("#backupCoordinator_error").html('<ul class="list-unstyled"><li>Please select an item in the list.</li></ul>');
	}
	
	if(valid){
		$.ajax({
		type: "POST",
		url: "createBackupConfiguration",
		data: JSON.parse(JSON.stringify($("#mainFrom").serialize())),
		async: false,
		success: function(data) {
			if (data) {
	            if(data.responseMessage=="success"){
					showAlert("success", data.successMessage);
					loadCoordinator();
					$("#coordinator").dropdown('clear');
					$("#coordinator").dropdown('set selected', '');
					$("#coordinator").dropdown('refresh');
					
					$("#backupCoordinator").dropdown('clear');
					$("#backupCoordinator").dropdown('set selected', '');
					$("#backupCoordinator").dropdown('refresh');
					$('#create-click').click();
				} else {
					showAlert("error", data.errorMessage);
				}
			}
		}
	});
	}
	
	
	
});

