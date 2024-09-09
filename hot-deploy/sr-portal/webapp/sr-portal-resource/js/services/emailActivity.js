
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
    
    $("#template").change(function() {
        var templateId = $("#template").val();
        loadTemplate(templateId);
    });


});

function loadSubTypes(typeId) {
        var nonSelectContent = "<span class='nonselect'>Please Select</span>";
        var subTypes = '<option value="" >Please Select</option>';
        $.ajax({
            type: "POST",
            url: "getIASubTypes",
            data: { "iaTypeId": typeId },
            async: false,
            success: function(data) {
                   var sourceDesc = data.results;
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

function loadTemplate(templateId){
    var template = "";
    $.ajax({
        type: "POST",
        url: "loadTemplate",
        data: { "templateId": templateId },
        async: true,
        success: function(data) {
            template = data.template;
            $("#mainFrom").append(DOMPurify.sanitize("<input type='hidden' name='emailFormContent' value='"+template+"' /> "));
            $("#emailContent").html(DOMPurify.sanitize(template));
           // $("#emailContent").html(template);
           //$("#emailFormContent").html("<textarea id='emailContent' class='ckeditor'>"+$.parseHTML(template)+"</textarea>");
           //$("#emailFormContent").html("<@textareaLarge label='Messages' id='emailFormContent' rows='10' value='"+template+"'/>");
           //$("#emailFormContent").html(""<@textareaLarge label='Messages' id='emailFormContent' rows='10' value=template/>);        
        }
    });
    
}