<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/lib/picker_macro.ftl"/>
        <div class="col-lg-12 col-md-12 col-sm-12 dash-panel style="width:100%">
            <h2 class="float-left">Set Default Currency</h2>
            <div class="clearfix"></div>
        
    <form id="mainForm" method="post" action="<@ofbizUrl>createAndUpdateDefaultValues</@ofbizUrl>" data-toggle="validator">       			
	    		 
	    		   <@inputHidden id="parameterName" value="Default Currency UOM" />
		           <@inputHidden id="parameterId" value="DEFAULT_CURRENCY_UOM" />
		           <@inputHidden id="sections" value="${inputContext.enumId?if_exists}" />
	    		    <@dynaScreen 
					instanceId="DEFAULT_CURRENCY_UOM_VALUE"
					modeOfAction="CREATE"
					/>
				
			<div class="clearfix"></div>
                   <div class="offset-md-2 col-sm-10 p-2">
                 <@submit class="btn btn-sm btn-primary navbar-dark" id="saveModal" label="${uiLabelMap.Save}" />
            </div>
     </form>
    </div>  