<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
<div class="row">
	<div id="main" role="main">
		<#assign helpUrl=Static["org.groupfio.common.portal.util.DataHelper"].getHelpUrl(delegator, request.getRequestURI())/>
		<div id="mainFrom" class="col-lg-12 col-md-12 col-sm-12 dash-panel">
		<form method="post" action="<@ofbizUrl>enableOrDisableComponents</@ofbizUrl>" data-toggle="validator">
			<div><@sectionFrameHeader title="${uiLabelMap.enableDisableComponents!}" extra=extras /></div>
				<@dynaScreen
					instanceId="COMPONENTS_HIDE"
					modeOfAction="CREATE"
					/>
				<div class="offset-md-2 col-sm-10">
					<input type="submit" class="btn btn-sm btn-primary disabled" value="Update"/>
					<@reset label="${uiLabelMap.Reset}"/>
				</div>
				<br>
			</form>
		</div>
	</div>
</div>
<script>
	$("#componentId").change(function() {
	    $("#tabId").html('');
	    $("#tabId").dropdown('clear');
	    $("#tabId").dropdown('refresh');
	    $("#shortcutId").html('');
	    $("#shortcutId").dropdown('clear');
        $("#shortcutId").dropdown('refresh');
	    if ($(this).val() !== "") {
	        loadTabId();
	    }
	});
	$("#tabId").change(function() {
	    $("#shortcutId").html('');
	    $("#shortcutId").dropdown('refresh');
	    if ($(this).val() !== "") {
	        loadShortCut();
	    }
	});

function loadTabId() {
    var componentId = $("#componentId").val();
    var componentIdOptions = '<option></option>';
    $("#tabId").html(componentIdOptions).change();
    $.ajax({
        type: "GET",
        url: "getComponentMenus",
        data: { "componentId": componentId },
        async: false,
        success: function(data) {
            var menus = data.results;
            if(menus != undefined && menus !=null){
	            for (var i = 0; i < menus.length; i++) {
	                var tabMenus = menus[i];
	                componentIdOptions += '<option value="' + tabMenus.tabId + '" selected="selected">' + tabMenus.description + '</option>';
	            }
            }
            $("#tabId").html(componentIdOptions);
            $("div.ui.dropdown.search.form-control.fluid.show-tick.tabId.selection > i").addClass("clear");
            $("#tabId").dropdown('clear');
            $("#tabId").dropdown('refresh');
        }
    });
}
function loadShortCut() {
    var tabId = $("#tabId").val();
    var componentId = $("#componentId").val();
    var shortcutIdOptions = '<option></option>';
    $("#shortcutId").html(shortcutIdOptions).change();
    $.ajax({
        type: "GET",
        url: "getComponentMenus",
        data: { "componentId": componentId, "tabId": tabId },
        async: false,
        success: function(data) {
            var results = data.results;
            if(results != undefined && results !=null){
	            for (var i = 0; i < results.length; i++) {
	                var shortcutMenus = results[i];
	                shortcutIdOptions += '<option value="' + shortcutMenus.shortcutId + '" selected="selected" >' + shortcutMenus.description + '</option>';
	            }
            }
            $("#shortcutId").html(shortcutIdOptions);
            $("div.ui.dropdown.search.form-control.fluid.show-tick.shortcutId.selection > i").addClass("clear");
            $("#shortcutId").dropdown('clear');
            $("#shortcutId").dropdown('refresh');
        }
    });
}

</script>