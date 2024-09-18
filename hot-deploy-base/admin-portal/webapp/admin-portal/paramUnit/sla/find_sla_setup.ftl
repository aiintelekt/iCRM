<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
<div class="row">
    <div id="main" role="main" class="pd-btm-title-bar">

        <div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
        	<@sectionFrameHeader title="${uiLabelMap.FindSlaSetup!}" />
            <form method="post" id="searchForm" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
                <div class="">
                    <div class="row p-2">
                        <div class="col-lg-3 col-md-6 col-sm-12">
                            <@dropdownCell 
	                            id="srType"
	                            name="srTypeId"
	                            placeholder="Select SR Type"
	                            options=srTypeList!
	                            value="${requestParameters.srType?if_exists}"
	                            allowEmpty=true
	                            />
                            
                        </div>
<div class="col-lg-3 col-md-6 col-sm-12">
                  <@dropdownCell 
                            	id="srSubCategoryId"
                            	name="srSubCategoryId"
	                            placeholder="Select SR Sub Category"	                            
	                            value="${requestParameters.srSubCategory?if_exists}"
	                            allowEmpty=true
	                            />             
                        </div>
<div class="col-lg-3 col-md-6 col-sm-12">
        <@dropdownCell 
	                            id="srCategoryId"
	                            name="srCategoryId"
	                            placeholder="Select SR Category"
	                            options=srCategoryList!
	                            value="${requestParameters.srCategory?if_exists}"
	                            allowEmpty=true
	                            />                   
                        </div>

                        <div class="col-lg-3 col-md-6 col-sm-12">
                          
                            <@dropdownCell 
	                            id="status"
	                            name="status"
	                            placeholder="Select Status"
	                            options=statusOptions!
	                            allowEmpty=true
	                            />  
 <div class="search-btn">
                            <@button
	                            id="main-search-btn"
	                            label="${uiLabelMap.Find}"
	                            />
                            <@reset
	                            label="${uiLabelMap.Reset}"
	                            />
                        </div> 
                        </div>
                       
                    </div>
                </div>
            </form>
            </div>   
			<div class="clearfix"></div>
			
			<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
            <#assign rightContent='<a title="Create" href="/admin-portal/control/createSlaSetup" class="btn btn-primary btn-xs ml-2" target="_blank"> <i class="fa fa-plus" aria-hidden="true"></i> Create </a>' />   
            <#-- <@AgGrid
	            gridheadertitle=uiLabelMap.ListOfSlaSetup
	            gridheaderid="sla-setup-grid-action-container"
	            savePrefBtn=true
	            clearFilterBtn=true
	            exportBtn=true
	            insertBtn=false
	            updateBtn=false
	            removeBtn=false
	            headerextra=rightContent
	            userid="${userLogin.userLoginId}" 
	            shownotifications="true" 
	            instanceid="PARAM_SLA_STP" 
	            autosizeallcol="true"
	            debug="false"
	            />  
            <script type="text/javascript" src="/admin-portal-resource/js/ag-grid/param-unit/sla-setup.js"></script>-->
    <input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
	<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

		<@fioGrid 
			id="ListOfSlaSetup-Grid"
			instanceId="PARAM_SLA_STP"
			jsLoc="/admin-portal-resource/js/ag-grid/param-unit/sla-setup.js"
			headerLabel=uiLabelMap.ListOfSlaSetup!
			headerId="sla-setup-grid-action-container"
			subFltrClearId="sla-setup-sub-filter-clear-btn"
			savePrefBtnId="sla-setup-save-pref-btn"
			clearFilterBtnId="sla-setup-clear-filter-btn"
			headerBarClass="grid-header-no-bar"
			savePrefBtn=false
			clearFilterBtn=false
			exportBtn=true
			subFltrClearBtn=false
			exportBtnId="sla-setup-list-export-btn"			
			headerExtra=rightContent!
			/>
            </div>
        </div>
    </div>
</div>
<script>

jQuery(document).ready(function() {
	 $("input[type=reset]").click(function(){		
		 $("#srSubCategoryId").html('');	
		 $("#srSubCategoryId").dropdown('refresh');
	 });
	
	$("#srCategoryId").change(function() {

		$('.srSubCategoryId .clear').click();	
		
		if($(this).val()!=""){		
			var nonSelectContent = "<span class='nonselect'>Select SR Sub Category</span>";
			var nameOptions = '<option value="" data-content="'+nonSelectContent+'" selected="">Select SR Sub Category</option>';		
					
			$.ajax({
				type: "POST",
		     	url: "/admin-portal/control/getSrSubCategories",
		        data: {"srCategoryId": $(this).val()},
		        async: false,
		        success: function (data) {   
		        	for (var i = 0; i < data.length; i++) {
		        		var entry = data[i];
		        		nameOptions += '<option value="'+entry.srSubCategoryId+'">'+entry.srSubCategoryDesc+'</option>';
		        	}
		        }
		        
			});    
			
			$("#srSubCategoryId").html(nameOptions);	
			$("#srSubCategoryId").dropdown('refresh');
		}else{
			$("#srSubCategoryId").html('');	
			$("#srSubCategoryId").dropdown('refresh');
		}
	});

});

</script>