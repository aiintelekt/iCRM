<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
      <div class="row">
        <div id="main" role="main">
          <@sectionFrameHeader title="${uiLabelMap.OpportunityResponseType!}" />
            <div class="col-lg-12 col-md-12 col-sm-12">
              <div id="accordion">
                <div class="row">
                 <div class="iconek">
                   <div class="arrow-down" style="margin-bottom: 10px;" onclick="this.classList.toggle('active')"></div>
                 </div>
               </div>
              <div class="border rounded bg-light margin-adj-accordian pad-top">
          <form action="#" method="post" id="responseTypeSearchForm" name="searchForm">
                      <@inputHidden 
                        id="searchCriteria"
                      />  
                      <@inputHidden 
                        id="enumTypeId"
                        value="OPP_RESPONSE_TYPE"
                      />
                      <div class="row p-2">
                           <div class="col-md-4 col-lg-4 col-sm-12">
                      <#assign status = Static["org.fio.admin.portal.util.EnumUtil"].getEnums(delegator, "OPP_CALL_OUTCOME")?if_exists />
                   <#assign statusList1 = Static["org.fio.admin.portal.util.DataHelper"].getDropDownOptions(status, "enumId","description")?if_exists /> 
                        <@dropdownCell 
                              id="parentEnumId"
                              name="parentEnumId"
                              placeholder="Select Call OutCome"
                              options=statusList1!
                               value="${requestParameters.parentEnumId?if_exists}"
                              allowEmpty=true
                              />
                        </div>
                        <div class="col-md-4 col-lg-4 col-sm-12">
                       
                           <@dropdownCell 
                              id="enumId"
                              name="enumId"
                              placeholder="Select Opportunity Response Type"
                              allowEmpty=true
                              />
                            
                          
                        </div> 
                       <div class="col-lg-4 col-md-4col-sm-12">
                          <div class="text-left">
                            <@button 
                              label="${uiLabelMap.Search}"
                              id="main-search-btn"
                              />
                           </div>
                         </div>
                      </div> 
                  </form>
                  </div>
            </div>
            
             <#assign rightContent='<a title="Create" href="/admin-portal/control/createOpportunityResponseType" class="btn btn-primary btn-xs ml-2" target="_blank"> <i class="fa fa-plus" aria-hidden="true"></i> Create </a>' />   
						 		
			<@AgGrid
			gridheadertitle=uiLabelMap.ListOfOpportunityResponseType
			gridheaderid="response-type-grid-action-container"
			savePrefBtn=true
			clearFilterBtn=true
			exportBtn=true
			insertBtn=false
			updateBtn=false
			removeBtn=false
			headerextra=rightContent
			
		    userid="${userLogin.userLoginId}" 
		    shownotifications="true" 
		    instanceid="OPPTN_RES_TYPE" 
		    autosizeallcol="true"
		    debug="false"
		    gridoptions='{"pagination": true, "paginationPageSize": 10 }'
		    />  		
             
            <script type="text/javascript" src="/admin-portal-resource/js/ag-grid/param-unit/opportunity-Response-Type.js"></script>     
                   
          </div>
        </div>
      </div>
      
<script>  

$("#parentEnumId").change(function() {
   var enumId  = $("#parentEnumId").val();
  
    if (enumId != "") {
        loadResponseType(enumId);
    }
});
	
function loadResponseType(parentEnumId) {
  
    var nonSelectContent = "<span class='nonselect'>Please Select ResponseType</span>";
    var responseTypeOptions = '<option value="" data-content="'+nonSelectContent+'" selected="">Please Select</option>';
  
    $.ajax({
        type: "POST",
        url: "getOpportunityResponseType",
        data: { "enumId": parentEnumId },
        async: false,
        success: function(data) {
               var sourceDesc = data.results;
               for (var i = 0; i < data.length; i++) {
                    var responseType = data[i];
                    responseTypeOptions += '<option value="'+responseType.enumId+'">'+responseType.description+'</option>';
                }
        }
    });
   
    $("#enumId").html(responseTypeOptions);
}
    
</script>