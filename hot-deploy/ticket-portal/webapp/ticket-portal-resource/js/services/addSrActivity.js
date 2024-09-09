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
        var subTypes = '<option value="" >Please Select</option>';
        console.log(typeId);
        $.ajax({
            type: "POST",
            url: "getIASubTypes",
            data: { "iaTypeId": typeId },
            async: false,
            success: function(data) {
                   var sourceDesc = data.results;
                   console.log("--result-----"+data);
                   for (var i = 0; i < data.length; i++) {
                        var type = data[i];
                        if(type.subTypeId==='${selectedSubTypeId!}'){
                        	subTypes += '<option value="'+type.subTypeId+'" selected>'+type.subTypeDesc+'</option>';
                        }else{
                        	subTypes += '<option value="'+type.subTypeId+'">'+type.subTypeDesc+'</option>';
                        }
                    }
            }
        });
        $("#srSubTypeId").html(DOMPurify.sanitize(subTypes));
}