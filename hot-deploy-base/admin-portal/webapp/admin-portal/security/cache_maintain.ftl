<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/lib/picker_macro.ftl"/>

<div class="row">
    <div id="main" role="main">
        <div class="clearfix"></div>
        <form id="mainFrom" method="post" action="<@ofbizUrl>cacheClearAction</@ofbizUrl>" data-toggle="validator">    
            <div class="col-lg-12 col-md-12 col-sm-12">
            	<div class="row padding-r"> 
            	
            	<div class="col-md-6 col-sm-6">		
            	<@dropdownCell 
					id="isClearOfbizCache"
					label=uiLabelMap.isClearOfbizCache
					options=yesNoOptions
					required=false
					allowEmpty=true
					/>
				</div>	
					
				</div>	
            </div>
						
            <div class="clearfix"></div>
            <div class="offset-md-2 col-sm-10">
            <@formButton
                     btn1type="submit"
                     btn1label="${uiLabelMap.ClearCache}"
                     btn1onclick=""
                     btn2=false
                     btn2onclick = "resetForm()"
                     btn2type="reset"
                     btn2label="${uiLabelMap.Clear}"
                   />
            </div>
        </form>
    </div>
</div>
