<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/lib/picker_macro.ftl"/>

<div class="row">
    <div id="main" role="main">

        <#assign extra='<a href="/admin-portal/control/findTemplate" class="btn btn-xs btn-primary back-btn">
        <i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>' />
        <div class="clearfix"></div>
        <form id="mainFrom" method="post" action="<@ofbizUrl>createSqlGroupAction</@ofbizUrl>" data-toggle="validator"> 
        	   
            <div class="col-lg-12 col-md-12 col-sm-12">
            	<@sectionFrameHeader title="${uiLabelMap.CreateSqlGroup!}" extra=extra />
            	<@dynaScreen 
					instanceId="SQLGRP_BASE"
					modeOfAction="CREATE"
					/>
            </div>
            
            <div class="clearfix"></div>
            <div class="offset-md-2 col-sm-10">
         
            <@formButton
                     btn1type="submit"
                     btn1label="${uiLabelMap.Save}"
                     btn2=true
                     btn2onclick = "resetForm()"
                     btn2type="reset"
                     btn2label="${uiLabelMap.Clear}"
                   />
            </div>
        </form>
    </div>
</div>

<script>

$(document).ready(function() {

});

</script>