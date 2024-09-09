<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<@sectionHeader title="${uiLabelMap.globalParamSettings!}" />
<div class="row">
    <div id="main" role="main">
		<div class="row padding-r">
		   <div class="col-md-6 col-sm-6">
		      <div class="portlet-body form">
		         <form method="post" action="<@ofbizUrl>updatePhoneGlobalParameter</@ofbizUrl>" id="updatePhoneGlobalParameter" class="form-horizontal" name="updatePhoneGlobalParameter" novalidate="novalidate" data-toggle="validator">
		            <div class="form-body">
		               <@radioInputCell 
		               name="outBoundCall"
		               label="Enable Out Bound Call"
		               options=optionMap
		               value="Y"
		               />
		               <#if outBoundCall?has_content && outBoundCall == "Y">
		               <@inputRow 
		               id="maxCallNum"
		               name="maxCallNum"
		               label="Max Call To Customer"
		               placeholder="Max Call To Customer"
		               value="${maxCallNum?if_exists}"
		               type="number"
		               required=false
		               />
		               <@inputRow 
		               id="hhMaxDays"
		               name="hhMaxDays"
		               label="Max Call To House Hold Id"
		               placeholder="Max Call To House Hold Id"
		               value="${hhMaxnum?if_exists}"
		               type="number"
		               required=false
		               />
		               <@inputRow 
		               id="callDurationDays"
		               name="callDurationDays"
		               label="Next Call Interval"
		               placeholder="Next Call Interval"
		               value="${callDurationDays?if_exists}"
		               type="number"
		               required=false
		               />
						<@inputRow 
						id="emailDurationDays"
						label=uiLabelMap.nextEmailInterval
						placeholder=uiLabelMap.nextEmailInterval
						value="${emailDuration?if_exists}"
						type="number"
						required=false
						min=1
						/>
		               <@inputRow 
						id="smsDurationDays"
						label=uiLabelMap.nextSmsInterval
						placeholder=uiLabelMap.nextSmsInterval
						value="${smsDuration?if_exists}"
						type="number"
						required=false
						min=1
						/>
		               </#if>
		            </div>
		            <div class="clearfix"> </div>
				   	<div class="form-group row">
				    	<div class="offset-sm-4 col-sm-7" >	  	 
					     <@reset label="Clear" />
				    	</div>
				   	</div>
					<div class="clearfix"> </div>
		         </form>
		      </div>
		   </div>
		</div>
	</div><#-- End main-->
</div><#-- End row-->