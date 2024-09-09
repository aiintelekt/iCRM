<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/lib/picker_macro.ftl"/>
  <div class="row">
	<div id="main" role="main">
		<div class="col-sm-12 col-md-12 col-lg-12 dash-panel">
			<div class="col-sm-12 col-md-12 col-lg-12>
				<@sectionFrameHeader title="Default Value Configuration"/>
				<#include "component://admin-portal/webapp/admin-portal/paramUnit/Response/setDefaultRMUser.ftl"/>
				<#include "component://admin-portal/webapp/admin-portal/paramUnit/Response/setDefaultCountry.ftl"/>
				<#include "component://admin-portal/webapp/admin-portal/paramUnit/Response/setDefaultCurrencyUomValue.ftl"/>
				<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
					<h2 class="float-left">Set Default SLA</h2>
					<div class="clearfix"></div>
					<form id="mainForm" method="post" action="<@ofbizUrl>createAndUpdateDefaultValues</@ofbizUrl>" data-toggle="validator">
						<@inputHidden id="parameterName" value=" " />
						<@inputHidden id="parameterId" value="DEFAULT_SR_SLA"/>
						<@inputHidden id="sections" value="${inputContext.enumId?if_exists}"/>
						<@dynaScreen 
							instanceId="DEFAULT_SR_SLA"
							modeOfAction="CREATE"
							/>
						<div class="clearfix"></div>
						<div class="offset-md-2 col-sm-10 p-2">
							<@submit
								class="btn btn-sm btn-primary navbar-dark"
								id="saveModal"
								label="${uiLabelMap.Save}"
								/>
						</div>
					</form>
				</div>
			</div>
		</div>
	</div>
</div>
  <script>
  
   $(document).ready(function(){
       $("#defaultSrSla").attr('oninput', "validity.valid||(value='.');");
                 
       $("#defaultSrSla").keypress(function (e) {
         if(e.keyCode == 46){
         return false;
         
         }
      });
       
    });
  
  </script>