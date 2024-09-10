<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<div class="row">
	<div id="main" role="main">
    <#assign extra='
    	<script>
       		$(document).ready(function(){
         		$("#accordion").click(function(){
             	$(".row").show();
         		});
       		});
        </script> 
    ' />
    <@sectionFrameHeader
    	title="${uiLabelMap.findProspects}"
 		extra=extra?if_exists
    />
    <div class="col-lg-12 col-md-12 col-sm-12 profil-sec-padding pt-0">
    	<div id="accordion">
        	<div class="row">
            	<@arrowDownToggle />
            </div>
 		<div class="border rounded bg-light margin-adj-accordian pad-top">
			<form method="post" action="#" id="searchProspect" class="form-horizontal" name="searchProspect" novalidate="novalidate" data-toggle="validator">
            	<div class="row p-2">
        			<div class="col-lg-4 col-md-7 col-sm-10">
       					<div class="form-group">
        					<@inputCell 
             					id="firstName"
             					placeholder="Name"
             					required=false
             				/>
       					</div> 
       					<div class="form-group">
           					<@inputCell 
             					id="prodLineInterest"
             					placeholder="Product Line"
             					required=false
             				/>         
       					</div>
       				</div>
        			<div class="col-lg-4 col-md-7 col-sm-10">
       					<div class="form-group">
        					<@inputCell 
             					id="status"
             					placeholder="Status"
             					required=false
             				/>
       					</div>
       					<div class="form-group">
           					<@inputCell 
             					id="segment"
             					placeholder="Segment"
             					required=false
             				/>         
       					</div>
       				</div>
            		<div class="col-lg-4 col-md-7 col-sm-10">
       					<div class="form-group">
        					<@inputCell 
             					id="sourceId"
             					placeholder="Source  ID"
             					required=false
             				/>
       					</div>
       					<div class="form-group">
           					<@inputCell 
             					id="createdOn"
             					placeholder="Aging of Prospect"
             					required=false
             				/>         
       					</div>
       				</div> 
                    <div class="col-lg-12 col-md-12 col-sm-12">
       					<div class="float-right"> 
        					<@button label="Search" label="${uiLabelMap.search}" id="searchProspects" />	
       						<@reset label="${uiLabelMap.reset}" />
       					</div>
     				</div>
   				</div>
			</form>  
		</div>    
    	</div>
    </div> 
    <div class="col-lg-12 col-md-12 col-sm-12 profil-sec-padding pt-0">
	    <div class="clearfix"></div>
		    <div class="page-header border-b pt-2">
		    	<@headerH2
		        	title="${uiLabelMap.salesProspects}"
		            class="float-left"
		        />
		        <a id="export_to_excel_icon" title="Export to CSV" href="#" class="btn btn-primary btn-xs" onclick="onBtExport()"><i class="fa fa-file-excel-o" aria-hidden="true"></i> Export</a> <a id="export_to_excel_icon" title="Save Preference" href="#" class="btn btn-primary btn-xs"><i class="fa fa-save " aria-hidden="true"></i> Save Preference</a>
		    	<div class="clearfix"></div>
		        </div><div class="clearfix"></div>
		        <div class="table-responsive">
		    	<div id="ProspectAgGrid"  class="ag-theme-balham"></div>
		        	<script type="text/javascript" src="/sales-portal-resource/js/ag-grid/findProspects.js"></script>
		        </div>
		    </div>
	    </div>
    </div>
</div>
<script>  
   
$(document).ready(function() {
	$("#searchProspects").click(function(event) {
	    event.preventDefault(); 
	    loadAgGridMyCell();
	});
	$("#firstName").keyup(function(event) {
	    loadAgGridMyCell();
	});
});
</script>
   