$(function() {
	var typeId  = $("#srTypeId").val();
    if (typeId != "") {
    	loadSubTypes(typeId);
    }
	$("#srTypeId").change(function() {
       var typeId  = $("#srTypeId").val();
        if (typeId != "") {
            loadSubTypes(typeId);
        }
    });
});

function loadSubTypes(typeId) {
        var nonSelectContent = "<span class='nonselect'>Please Select</span>";
        var subTypes = '<option value="" data-content="'+nonSelectContent+'" selected="">Please Select</option>';
        $.ajax({
            type: "POST",
            url: "getIASubTypes",
            data: { "iaTypeId": typeId },
            async: false,
            success: function(data) {
                   var sourceDesc = data.results;
                   for (var i = 0; i < data.length; i++) {
                        var type = data[i];
                        subTypes += '<option value="'+type.subTypeId+'">'+type.subTypeDesc+'</option>';
                    }
            }
        });
        $("#srSubTypeId").html(DOMPurify.sanitize(subTypes));
}