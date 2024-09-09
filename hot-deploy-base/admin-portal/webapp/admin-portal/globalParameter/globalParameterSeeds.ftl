<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<script type="text/javascript" src="https://ajax.aspnetcdn.com/ajax/jquery.validate/1.10.0/jquery.validate.min.js"></script>
<style>
.text-size-control
{
text-overflow: ellipsis;
white-space: nowrap;
overflow: hidden;
width:300px;
}
.table td, .table th {
    border-top: 2px solid #dee2e6 !important;
}
.top-band{
z-index:0;
}
.modal-body{
padding: 1.5rem !important;
}
.input-sm
{
height:44px !important;
}
</style>
<#assign extra='<a href="#"  class="btn btn-xs btn-primary back-btn" "> Import</a>' />
<div class="row">
    <div id="main" role="main">
       <div class="col-md-12 col-lg-12 col-sm-12 dash-panel" >
        <@sectionFrameHeader title="${uiLabelMap.GlobalConfiguration} Default Seed Import" />
        <div class="panel-group" id="accordionMenu" role="tablist" aria-multiselectable="true">
            <div class="panel panel-default">
                <div class="panel-heading" role="tab" id="headingTwo">
                    <h4 class="panel-title">
                        <a role="button" data-toggle="collapse" data-parent="#accordionMenu"
                            href="#accordionDynaBase" aria-expanded="true"
                            aria-controls="collapseOne"> ${uiLabelMap.MainFilter} </a>
                    </h4>
                </div>
                <div>
                    <div>
                        <div id="accordionDynaBase" class="panel-collapse collapse show" role="tabpanel" aria-labelledby="headingOne">
                            <form method="post" class="form-horizontal table-responsive" action="<@ofbizUrl>importGlobalParameterSeeds</@ofbizUrl>" name="globalParameterSeeds">
                                <div class="margin-adj-accordian">
                                    <div class="row p-3f" >
                                    <div class="col-lg-12 col-md-12 col-sm-12 search-left">
                                     <@dynaScreen 
									 instanceId="LOAD_GLOBAL_SEEDS"
									 modeOfAction="CREATE"
									/>
								    </div>
                                        <div class="col-11 p-1">
                                            <div class="float-right p-1">       
                                                <input onclick="javascript:document.globalParameterSeeds.submit();"  class="btn btn-sm btn-primary" type="submit" value="Import">
                                                <@reset label="Reset" onclick="javascript:clearFields();" />
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<script>
<#--(function () {
   var globalParamSectionOptions = $("#globalParamSection").empty();
     $.ajax({
         type: "POST",
         url: "getComponents",
         async: false,
         success: function(data) {
             if (data && data.data) {
                 $("#globalParamSection").empty();
                 globalParamSectionOptions.append("<option value=''></option>");
                 for (var i = 0; i < data.data.length; i++) {
                     var componentName = data.data[i];
                     globalParamSectionOptions.append("<option  value =" + componentName + ">" + componentName + " </option>");
                 }
             }
         }
     });
     $('#globalParamSection').append(globalParamSectionOptions);
}());-->
$(document).ready(function(){
	$('input[type="checkbox"]').change(function(){
		var $t=$(this);
		var name=this.name;
		if(name=="isAll"){
			var isset=$t.prop('checked') ? 'Y' : 'N';
			$('input[name="'+$t.attr('name')+'"]').val(isset);
		}
	});
});
</script>