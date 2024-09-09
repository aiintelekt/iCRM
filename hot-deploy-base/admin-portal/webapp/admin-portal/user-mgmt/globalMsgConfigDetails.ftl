<#include "component://ab-ag-grid-support/webapp/ab-ag-grid-support/lib/ag_grid_header.ftl"/>
<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

      <div class="row">
        <div id="main" role="main">
          <@sectionFrameHeader title="${uiLabelMap.FindScrollingMessage!}" />
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
                     
                      <div class="row p-2">
                        <div class="col-md-2 col-sm-4 col-sm-6">
                        
                       <@dropdownCell
				         id="componentId"
				         name="componentId"
				         placeholder="Select Component"
				         required=true
				         options=componentData!    
						  allowEmpty=true        
						  value="${requestParameters.componentId?if_exists}"
				         />
                           </div>  
                         <#assign userDetails = Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("userLoginId","firstName").from("UserLoginPerson").where("enabled","Y","statusId","PARTY_ENABLED").queryList()?if_exists />    
                            <#assign usersOptionList = Static["org.fio.admin.portal.util.DataHelper"].getDropDownOptions(userDetails, "userLoginId", "firstName")?if_exists />
                        <div class="col-md-2 col-sm-4 col-sm-6">
                    
                            <@dropdownCell
	                        id="partyId" 
	                        name="partyId" 
	                        placeholder="Select Message To"      
	                        allowEmpty=true   
	                        options=usersOptionList!
	                        value="${requestParameters.partyId?if_exists}"
                        /> 
                        
                        </div> 
                        
                         <div class="text-right"> 
                               <@button 
                              label="${uiLabelMap.Search}"
                              id="main-search-btn"
                              />
                              <@reset
                            label="${uiLabelMap.Reset}"
                            />
                          </div>
                          </div>
                  </form>
            </div>
            </div>
     		<div class="page-header border-b pt-2">
		        <@headerH2 title="List of Scrolling message" class="float-left"/>
		        <div class="clearfix"></div>
		    </div>
		    
		    <@AgGrid
					userid="${userLogin.userLoginId}" 
					instanceid="GLOBAL_MESS" 
					autosizeallcol="true" 
					gridoptions='{"pagination": true, "paginationPageSize": 10 }'
					/>
          
             <#--      
             <div class="table-responsive">
                <div class="loader text-center" id="loader" sytle="display:none;">
                  <span></span>
                  <span></span>
                  <span></span>
                </div>
               <div id="rspReasonGrid" style="width: 100%;" class="ag-theme-balham"></div>
               <script type="text/javascript" src="/admin-portal-resource/js/ag-grid/param-unit/globalMsgConfig.js"></script>
              </div> 
              -->
      </div>
      
<script>  

function loadMainGrid(fag) {
		
	$.ajax({
	  url:'/admin-portal/control/getGlobalMessageConfigData',
	  type:"POST",
	  data: JSON.parse(JSON.stringify($("#searchForm").serialize())),
	  success: function(data){
			// console.log("data: ", data)
		  fag.setRowData(data)
	  }
	})
	
}

/*  
function doSearch(){
    loadRsReasonAgGrid();
}
$("#oppoCallOutcome").change(function() {
     loadRsReasonAgGrid();
});
*/
    
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
            url: "getOpportunityResponseType",
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