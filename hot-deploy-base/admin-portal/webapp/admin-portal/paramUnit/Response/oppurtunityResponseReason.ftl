<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
      <div class="row">
        <div id="main" role="main">
          <@sectionFrameHeader title="${uiLabelMap.FindResponseReason!}" />
           <div class="col-lg-12 col-md-12 col-sm-12">
            <div id="accordion">
              <div class="row">
                <div class="iconek">
                  <div class="arrow-down" style="margin-bottom: 10px;" onclick="this.classList.toggle('active')"></div>
                </div>
              </div>
              <div class="border rounded bg-light margin-adj-accordian pad-top">
                <form action="#" method="post" id="searchForm" name="searchForm">
                      <@inputHidden 
                        id="searchCriteria"
                      /> 
                      <@inputHidden 
                        id="enumTypeId"
                        name="enumTypeId"
                        value="OPP_RESPONSE_REASON"
                      /> 
                      <div class="row p-2">
                        <div class="col-md-4 col-lg-4 col-sm-12">
                         <#assign statuses = delegator.findByAnd("Enumeration", {"enumTypeId" : "OPP_CALL_OUTCOME"}, null, false)>
                      <#assign statusList = Static["org.fio.admin.portal.util.DataHelper"].getDropDownOptions(statuses, "enumId","description")?if_exists />
                        
                          <@dropdownCell 
                              id="oppoCallOutcome"
                              name="oppoCallOutcome"
                              placeholder="Select Opportunity Call Outcome"
                              options=statusList!
                               value="${requestParameters.oppoCallOutcome?if_exists}"
                              allowEmpty=true
                              />
                           </div>  
                        <div class="col-md-4 col-lg-4 col-sm-12">
                            <@dropdownCell
	                        id="responseTypeId" 
	                        name="responseTypeId" 
	                        placeholder="Select Opportunity Response Type"      
	                        allowEmpty=true   
	                        value="${requestParameters.responseTypeId?if_exists}"
                        /> 
                        </div> 
                         <div class="col-md-4 col-lg-4 col-sm-12">
                              <@dropdownCell
	                        id="oppoResponseReasonId"
	                        name="oppoResponseReasonId"  
	                        placeholder="select Opportunity Response Reason"      
	                        allowEmpty=true        
	                        value="${requestParameters.oppoResponseReasonId?if_exists}"     
                        /> 
                        </div>
                         <div class="text-right"> 
                               <@button 
                              label="${uiLabelMap.Search}"
                              id="main-search-btn"
                              />
                          </div>
                  </form>
                  </div>
            </div>
            <div class="clearfix"></div>
            </div>
            
            <#assign rightContent='<a title="Create" href="/admin-portal/control/createOppResponseReason" class="btn btn-primary btn-xs ml-2" target="_blank"> <i class="fa fa-plus" aria-hidden="true"></i> Create </a>' />   
						 		
			<@AgGrid
			gridheadertitle=uiLabelMap.ListResponseReason
			gridheaderid="response-reason-grid-action-container"
			savePrefBtn=true
			clearFilterBtn=true
			exportBtn=true
			insertBtn=false
			updateBtn=false
			removeBtn=false
			headerextra=rightContent
			
		    userid="${userLogin.userLoginId}" 
		    shownotifications="true" 
		    instanceid="OPPTN_RES_REASON" 
		    autosizeallcol="true"
		    debug="false"
		    gridoptions='{"pagination": true, "paginationPageSize": 10 }'
		    />  
		    
		    <script type="text/javascript" src="/admin-portal-resource/js/ag-grid/param-unit/oppResponseReason.js"></script>				
                                   
          </div>
        </div>
      </div>
      
<script>
 
$("#oppoCallOutcome").change(function() {
	var oppoCallOutcome  = $("#oppoCallOutcome").val();
	if (oppoCallOutcome != "") {
	    loadResponseType(oppoCallOutcome);
	}else{
		$("#responseTypeId").html('');
		$("#oppoResponseReasonId").html('');
	}
});
$("#responseTypeId").change(function() {
   var responseTypeId  = $("#responseTypeId").val();
   var oppoCallOutcome  = $("#oppoCallOutcome").val();
   if (responseTypeId != ""&& oppoCallOutcome != "") {
       loadoppoResponseReasonId(oppoCallOutcome , responseTypeId);
   }else{
   	  $("#oppoResponseReasonId").html('');
   }
});
    function loadResponseType(oppoCallOutcome) {
     
        var nonSelectContent = "<span class='nonselect'>Please Select ResponseType</span>";
        var responseTypeOptions = '<option value="" data-content="'+nonSelectContent+'" selected="">Please Select</option>';
     
        $.ajax({
            type: "POST",
            url: "getOppoResponseReasonType",
            data: { "oppoCallOutcome": oppoCallOutcome },
            async: false,
            success: function(data) {
                   var sourceDesc = data.results;
                   for (var i = 0; i < data.length; i++) {
                        var responseType = data[i];
                        responseTypeOptions += '<option value="'+responseType.enumId+'">'+responseType.description+'</option>';
                    }
            }
        });
       
        $("#responseTypeId").html(responseTypeOptions);
}
function loadoppoResponseReasonId(oppoCallOutcome,responseTypeId) {
        var nonSelectContent = "<span class='nonselect'>Please Select</span>";
        var oppoResponseReasonOptions = '<option value="" data-content="'+nonSelectContent+'" selected="">Please Select</option>';
        $.ajax({
            type: "POST",
            url: "getOpportunityResponseReason",
            data: { "oppoCallOutcome": oppoCallOutcome , "responseTypeId": responseTypeId },
            async: false,
            success: function(data) {
                   var sourceDesc = data.results;
                   for (var i = 0; i < data.length; i++) {
                        var category = data[i];
                      oppoResponseReasonOptions += '<option value="'+category.enumId+'">'+category.description+'</option>';
                    }
            }
        });
       
        $("#oppoResponseReasonId").html(oppoResponseReasonOptions);
}
</script>