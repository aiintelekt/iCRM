<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
<#include "component://homeapps/webapp/homeapps/user-audit/audit_page_header.ftl"/>

<div class="row">
    <div id="main" role="main">
 <div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
        <#assign extra='<a href="/admin-portal/control/findSlaSetup" class="btn btn-xs btn-primary">
        <i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>' />
        <@sectionFrameHeader title="${uiLabelMap.CreateSlaSetup!}" extra=extra />
       
        <form id="mainForm" method="post" action="<@ofbizUrl>createSlaSetupAction</@ofbizUrl>" data-toggle="validator">    
        
        <#include "component://homeapps/webapp/homeapps/user-audit/audit_from_header.ftl"/>
        
           
            	
            	<@dynaScreen 
					instanceId="PARAM_SLA_STP"
					modeOfAction="CREATE"
					/>
            	
            
            	
            	<@dynaScreen 
					instanceId="PARAM_SLA_STP_VRT"
					modeOfAction="CREATE"
					/>
<div class="clearfix"></div>
            <div class="offset-md-2 col-sm-8" id="create-sla">
         
	            <@formButton 
	                     btn1type="submit"
	                     btn1id="slaSubmit"
	                     btn1label="${uiLabelMap.Save}"
	                     btn2=true
	                     btn2onclick = "resetForm()"
	                     btn2type="reset"
	                     btn2label="${uiLabelMap.Clear}"
	                   />         
           	
	         </div>
            	
            </div>
            
            
          </div>
        </form>
    </div>
</div>

<script>

$(document).ready(function() {
    $("#srResolutionUnit_label").html('');
    $("#srResolutionUnit_label").html('SLA for SR Resolution' + '<span class="list-unstyled text-danger">*</span>');
    $("#slaSrResolution_label").html('');
    $("#slaSrResolution_label").html('SR Resolution Unit' + '<span class="list-unstyled text-danger">*</span>');

    $("#srResolutionUnit").attr("required", "required");
    //$("#slaSrResolution").attr("required", "required");
	if($("input[name^='slaSrResolution']" ).length){
		$("input[name^='slaSrResolution']" ).each(function(){
			$(this).attr("required","required");
		});	
	}	
    $('input[type=radio][name=isSlaRequired]').change(function() {
        if ($(this).val() == "Y") {
            $("#sla-variation").show();
            $("#srResolutionUnit").attr("required", "required");
            $("#slaSrResolution_0").attr("required", "required");

        } else {
            //$("#sla-variation").hide();
            $("#sla-variation").css("display", "none");
            $("#srResolutionUnit").removeAttr("required");
            $("#slaSrResolution").removeAttr("required");

        }
    });
    
    getCategory();
    
    $("#slaSubmit").click(function() {
        var srResolutionUnit = $("#srResolutionUnit").val();
        srResolutionUnit = srResolutionUnit.replace(/^0+/, '');
        $("#srResolutionUnit").val(srResolutionUnit);

    });
    
    $("#srCategoryId").change(function() {

	    $('.srSubCategoryId .clear').click();
	    $('#srSubCategoryId').empty();
	    $('#srSubCategoryId').dropdown('clear');
	    //$("div.ui.dropdown.search.form-control.fluid.show-tick.srSubCategoryId.selection > i").removeClass("clear");
	    if ($(this).val() != "") {
	        var nonSelectContent = "<span class='nonselect'>Select SR Sub Category</span>";
	        //var nameOptions = '<option value="" data-content="'+nonSelectContent+'" selected="">Select SR Sub Category</option>';		
	        var nameOptions = '<option value="NA" selected="">NA</option>';
	        $.ajax({
	            type: "POST",
	            url: "/admin-portal/control/getSrSubCategories",
	            data: {
	                "srCategoryId": $(this).val()
	            },
	            async: false,
	            success: function(data) {
	                for (var i = 0; i < data.length; i++) {
	                    var entry = data[i];
	                    nameOptions += '<option value="' + entry.srSubCategoryId + '">' + entry.srSubCategoryDesc + '</option>';
	                }
	            }
	        });
	        $("div.ui.dropdown.search.form-control.fluid.show-tick.srSubCategoryId.selection > i").addClass("clear");
	        $("#srSubCategoryId").html(nameOptions);
	        $("#srSubCategoryId").dropdown('refresh');
	    } else {
	        $("#srSubCategoryId").html('');
	        $("#srSubCategoryId").dropdown('refresh');
	    }
	});

});


/*
$("#srTypeId").change(function() {
	
	var nonSelectContent = "<span class='nonselect'>Select SR Category</span>";
	var nameOptions = '<option value="" data-content="'+nonSelectContent+'" selected="">Select SR Category</option>';		

	$.ajax({
		type: "POST",
     	url: "/admin-portal/control/getSrCategories",
        data: {"srTypeId": $(this).val(),"externalLoginKey": "${requestAttributes.externalLoginKey!}"},
        async: false,
        success: function (data) {   
        	for (var i = 0; i < data.length; i++) {
        		var entry = data[i];
        		nameOptions += '<option value="'+entry.srCategoryId+'">'+entry.srCategoryDesc+'</option>';
        	}
        }
        
	});    
	
	$("#srCategoryId").html( nameOptions );
	$("#srCategoryId").dropdown('refresh');
	
});
*/

function getCategory() {
    $('#srCategoryId').empty();
    $('#srCategoryId').dropdown('clear');
    //$("div.ui.dropdown.search.form-control.fluid.show-tick.srCategoryId.selection > i").removeClass("clear");
    var nonSelectContent = "<span class='nonselect'>Select SR Category</span>";
    var nameOptions = '<option value="NA" selected>NA</option>';
    $("#srSubCategoryId").html(nameOptions);
    $.ajax({
        type: "POST",
        url: "/admin-portal/control/getSrCategories",
        //data: {"externalLoginKey": "${requestAttributes.externalLoginKey!}"},
        async: false,
        success: function(data) {
            for (var i = 0; i < data.length; i++) {
                var entry = data[i];
                nameOptions += '<option value="' + entry.srCategoryId + '">' + entry.srCategoryDesc + '</option>';
            }
            $("div.ui.dropdown.search.form-control.fluid.show-tick.srCategoryId.selection > i").addClass("clear");
        }

    });
    $("div.ui.dropdown.search.form-control.fluid.show-tick.srSubCategoryId.selection > i").addClass("clear");
    $("#srCategoryId").html(nameOptions);
    $("#srCategoryId").dropdown('refresh');
    $("#srCategoryId").trigger("change");
};

</script>

<#include "component://homeapps/webapp/homeapps/user-audit/audit_footer.ftl"/>
