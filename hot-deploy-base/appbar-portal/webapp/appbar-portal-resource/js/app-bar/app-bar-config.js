function validateForm(){
	var flag = true;
	$('#appBarTypeId_error').empty();
	var appBarTypeId = $("#appBarTypeId").val();
	if(appBarTypeId == null || appBarTypeId == "" || appBarTypeId == "undefined"){
		$('#appBarTypeId_error').html("Please Select AppBar Type");
		flag = false;
	}
	return flag;
}

$("#appBarTypeId").change(function() {
    var appBarTypeId =  $("#appBarTypeId").val();
          $("#appBarTypeId_error").empty();
      if(appBarTypeId == null || appBarTypeId == "" || appBarTypeId == "undefined"){
    	  $('#appBarTypeId_error').html("Please Select AppBar Type");
      }
});