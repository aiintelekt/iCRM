<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<input type="hidden" name="activeTab" value="details" />	
<div class="page-header border-b pt-2">
	<@headerH2
    	title="Campaign Description"
    />
</div>
<div class="lead">
	<p id="description"></p>
</div>  
<div class="row">
	<div class="col-md-12 col-lg-4 col-sm-12">
    	<div class="page-header border-b pt-2">
        	<@headerH2
            	title="Personalization Fields"
           	/>	
        </div>
    </div>
    <div class="col-md-12 col-lg-8 col-sm-12">
    	<div class="page-header border-b pt-2">
        	<@headerH2
           		title="Opportunity Details"
          	/>	
        </div>
    </div>  
</div>  
<div class="row">
	<div class="col-md-12 col-lg-4 col-sm-12">
		<#assign salOpportunityId = requestParameters.salesOpportunityId!>
	  <@headerH4
           		title="No Fields Available"
           	/>
	  <#--  	<#assign personalized = Static["org.fio.sales.portal.event.AjaxEvents"].getPersonalizedFields(delegator, dispatcher, salOpportunityId, userLogin) />
       	<#if personalized?has_content>
       	<#assign perFileEntry = personalized.entrySet()/>
        <#if perFileEntry?has_content>    
        	<#list perFileEntry as personal>
        		<#if personal.getValue()?has_content>
       				${personal.getKey()!} : ${personal.getValue()!}<br>
       			</#if>	
           </#list>
        <#else> 
        	<@headerH4
           		title="No Fields Available"
           	/>
        </#if>
        <#else> 
        	<@headerH4
           		title="No Fields Available"
           	/>
        </#if>   -->
      	<@submit label="Audit"/>
    </div>
    <input type='hidden' id="salesOpportunityId" name="salesOpportunityId" value="${requestParameters.salesOpportunityId?if_exists}"/>
    <div class="col-md-12 col-lg-4 col-sm-12">
          <@displayCellForDiffLabelSize
          	label="Channel"
            value="${typeEnumId!}"
            id="test1"
          />
         <@displayCellForDiffLabelSize
         	label="Product Name"
            value="${opportunityName!}"
            id="test2"
         />
        <@displayCellForDiffLabelSize
        	label="${uiLabelMap.totalSalesAmount}"
          	value="${estimatedAmount!}"
          	id="test3"
        />
        <@displayCellForDiffLabelSize
            label="Remarks"
           	value="${remarks!}"
           	id="test4"
        />
        <@displayCellForDiffLabelSize
        	label="Owner"
          	value="${userLoginId!}"
          	id="test5"
        />
    </div>
   	<div class="col-md-12 col-lg-4 col-sm-12">
    	<@displayCellForDiffLabelSize
       		label="Referral Source"
       		value="${dataSourceId!}"
       		id="test6"
     	/>
     	<@displayCellForDiffLabelSize
       		label="${uiLabelMap.referralDescription}"
       		value="${dataSourceIdDesc!}"
       		id="test9"
      	/>
     	<@displayCellForDiffLabelSize
       		label="Originating Alert"
       		value="${dataSourceDataId!}"
       		id="test10"
      	/>
    	<@displayCellForDiffLabelSize
      		label="Originating SR"
      		value="${dataSourceDataId!}"
      		id="test7"
     	/>
    	<@displayCellForDiffLabelSize
        	label="Owner BU"
         	value="${businessUnitName!}"
         	id="test8"
     	/>
   	</div>
  </div>
<script>
	$(document).ready(function() {
	   	loadajax();
	   
	    function loadajax(){
	    	var salesOpportunityId =$("#salesOpportunityId").val();
	       	dataSet = {"salesOpportunityId":salesOpportunityId};
	 		$.ajax({
	   			url:'getDetails',
	   			data:dataSet,
	   			type:"post",
			  	success:function(data){
				    var typeEnumId1=data[0].typeEnumId;
				    var productDesc=data[0].productDesc;
				    var estimatedAmount1=data[0].estimatedAmount;
				    var remarks1=data[0].remarks;
				    var userLoginId1=data[0].createdByUserLogin;
				    var dataSourceId1=data[0].dataSourceId;
				    var referralDesc = data[0].referralDesc;
				    var dataSourceDataId1=data[0].dataSourceDataId;
				    var businessUnitName1=data[0].businessUnitName;
				    var description=data[0].description;
				    var channelDescription = data[0].typeEnumDesc;
				    var campaignDescription = data[0].campaignDescription;
				    var originatingSR = data[0].originatingSR;
				    var originatingAlert = data[0].originatingAlert;
				    var salesOppownerName = data[0].salesOppownerName;
		    
				   	document.getElementById("test1").innerHTML=channelDescription;
				   	document.getElementById("test2").innerHTML=productDesc;
				   	document.getElementById("test3").innerHTML=estimatedAmount1;
				   	document.getElementById("test4").innerHTML=remarks1;
				   	document.getElementById("test5").innerHTML=salesOppownerName;
				   	document.getElementById("test6").innerHTML=dataSourceId1;
				   	document.getElementById("test7").innerHTML=originatingSR;
				   	document.getElementById("test8").innerHTML=businessUnitName1;   
				   	document.getElementById("test9").innerHTML=referralDesc;   
				   	document.getElementById("test10").innerHTML=originatingAlert;   
				   	document.getElementById("description").innerHTML=campaignDescription;
	  			}
	  		});
	  	}
   	});
</script>