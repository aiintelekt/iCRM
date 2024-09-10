<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<div class="row">
    <div id="main" role="main" class="pd-btm-title-bar">
        <@sectionFrameHeader title=uiLabelMap.findAccounts />
        <div class="col-md-12 col-lg-12 col-sm-12 ">
		  	<div id="accordion">
			  	<div class="row">
                    <@arrowDownToggle />
                </div>
                <div>
                	<div>
			     		<form action="findAccounts" method="post" id="searchForm" name="searchForm" data-toggle="validator">
					  		<div class="border rounded bg-light margin-adj-accordian pad-top">
			     				<div class="row p-2">
					       			<div class="col-lg-6 col-md-6 col-sm-6">
					           			<@inputCell    
						                    id="accountSearchPartyId"
						                    placeholder =uiLabelMap.companyId
						                    value="${accountSearchPartyId?if_exists}"
						                    />
					               </div>
					               <div class="col-lg-6 col-md-6 col-sm-6">
					               	  <@inputCell    
					                    id="searchGroupName"
					                    placeholder =uiLabelMap.companyName
					                    value="${searchGroupName?if_exists}"
					                    />
					               </div>
					               <div class="col-lg-6 col-md-6 col-sm-6">
					               	  <@inputCell    
					                    id="searchEmailId"
					                    placeholder =uiLabelMap.email
					                    value="${searchEmailId?if_exists}"
					                    />
					               </div>
					               <div class="col-lg-6 col-md-6 col-sm-6">
					               	  <@inputCell    
					                    id="searchPhoneNum"
					                    placeholder =uiLabelMap.phoneNumber
					                    value="${searchPhoneNum?if_exists}"
					                    />
					               </div>
					               <div class="col-lg-12 col-md-12 col-sm-12">
					                    <div class="text-right">
					           	  			<@button id="doSearch" label="Find"/>
					           	  		</div>
					           	  	</div>
			        			</div>
		        			</div>
				     	</form>
			     	</div>
		  		</div>
	  		</div>
			<@pageSectionHeader title=uiLabelMap.accountsList />
	  		<div class="clearfix"> </div>
  			<div class="table-responsive">  	
				<div id="myGrid" style="width: 100%;" class="ag-theme-balham"></div>     
			</div>
			<script type="text/javascript" src="/crm-resource/js/ag-grid/account/account.js"></script>
		</div>
		<div class="clearfix"></div>
	</div><#-- End main-->
</div><#-- End row-->
<script>
$("#doSearch").click(function(event) {
	event.preventDefault(); 
	loadAgGrid();
});       
</script>
