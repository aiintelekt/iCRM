<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<div class="row">
	<div id="main" role="main" class="pd-btm-title-bar">
    	<@sectionFrameHeader title=uiLabelMap.findContacts />
		<div class="col-md-12 col-lg-12 col-sm-12">
			<div id="accordion">
				<div class="row">
                    <@arrowDownToggle />
                </div>
                <div>
                	<div>				
						<form action="findContact" method="post" id="searchForm" name="searchForm" data-toggle="validator">
							<div class="border rounded bg-light margin-adj-accordian pad-top">   
			      				<div class="row px-4">
							         <div class="col-lg-4 col-md-4 col-sm-12">
							         	<@inputCell  
							                id="partyId"
							                placeholder ="Contact ID" 
							                value="${partyId?if_exists}"
							                />
							         </div>
							         <div class="col-lg-4 col-md-4 col-sm-4">            
							         	<@inputCell    
							                id="firstName"
							                placeholder =uiLabelMap.firstName
							                value="${firstName?if_exists}"
							                /> 
							         </div>
							         <div class="col-lg-4 col-md-4 col-sm-12">            
							         	<@inputCell    
							                id="lastName"
							                placeholder =uiLabelMap.lastName
							                value="${lastName?if_exists}"
							                />
							         </div>
							         <div class="col-lg-4 col-md-4 col-sm-12">            
							         	<@inputCell    
							                id="emailAddress"
							                placeholder="Email Address"
							                value="${emailAddress?if_exists}"
							                />
							         </div>
							         <div class="col-lg-4 col-md-4 col-sm-12">            
							         	<@inputCell    
							                id="contactNumber"
							                placeholder="Phone Number"
							                value="${contactNumber?if_exists}"
							                />
							         </div>
			     					 <div class="col-lg-12 col-md-12 col-sm-12">
			     					 	<div class="text-right">
		         							<@button id="doSearch" label="Search"/>
	         							</div>
			         				 </div>
		         				 </div>
		      				</div>  
	      				</form>	
      				</div>
   				</div>
   			</div>
			<div class="clearfix"> </div>
			<@pageSectionHeader title="Contacts List" />
			<div class="clearfix"></div>
			<div class="table-responsive">				
				<div id="myGrid" style="width: 100%;" class="ag-theme-balham"></div>   			
			</div>
			<script type="text/javascript" src="/crm-resource/js/ag-grid/contact/contact.js"></script>
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