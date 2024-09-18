<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<div class="row">
        <div id="main" role="main">
       <#--  <div class="col-lg-12 col-md-12 col-sm-12">
             <div class="row">
				<marquee behavior="scroll" direction="left" class="text-danger">"System maintenance scheduled for 15-01-2020 from 8 AM SGT to 10 AM SGT. During this time, users may experience unavailability of services"</marquee>
			 </div>
	    </div>-->
	    <br>
	    <br>
        <@sectionFrameHeader title="${uiLabelMap.OverdueSRSummaryReport!}" />
          <div class="card-header">
                    <form method="post" action="<@ofbizUrl>findSROverDueSummary</@ofbizUrl>" id="searchForm" class="form-horizontal" name="searchForm" novalidate="true" data-toggle="validator">
                       <@inputHidden 
                        id="searchCriteria"
                      />
                      <div class="row">
                      <div class="form-group col-md-12 col-lg-6">
                        <#assign bUs = delegator.findByAnd("ProductStoreGroup",{"status":"ACTIVE"}, null, false)>
                        <#assign buList = Static["org.fio.admin.portal.util.DataHelper"].getDropDownOptions(bUs, "productStoreGroupName","productStoreGroupName")?if_exists />       
                          <@dropdownCell 
                              id="ownerBu"
                               label=uiLabelMap.BusinessUnit!
                              options=buList!
                              required=false
                              value="${requestParameters.ownerBu?if_exists}"
                              allowEmpty=true
                              />
                     	 </div>
                      <div class="form-group col-md-12 col-lg-6">
                              <@button 
                                label="${uiLabelMap.Search}"
                                onclick="javascript: return doSearch();"
                                />
                                <@reset
                            label="${uiLabelMap.Reset}"
                            />
                        </div>
                       </div>
                  </form>
                </div>
              </div>
            </div>
		            <h2>Summary of Overdue SRs</h2>
		            <hr>
                  <#-- <div class="clearfix"></div>
                   <div class="table-responsive">
                   <div class="loader text-center" id="loader" sytle="display:none;">
                </div> -->
               <div id="grid1" style="width: 100%;" class="ag-theme-balham"></div>
                 <#-- <@AgGrid
                userid="${userLogin.userLoginId}" 
                instanceid="REPORT03" 
                styledimensions='{"width":"100%","height":"80vh"}'
                autosave="false"
                autosizeallcol="true" 
                debug="true"
                requestbody='${searchCriteria!}'
                endpoint="/report-portal/control/getSrStatusReport"
                aggridthemeclass="ag-theme-balham"
                /> -->
              </div>
             <#-- <script type="text/javascript" src="/bootstrap/js/fio-ag-grid.js"></script> -->
             <script type="text/javascript" src="/ticket-portal-resource/js/ag-grid/services/summaryOverdueSr.js"></script>
          </div>
        </div>
      </div>
    </div>
    
<script>
 $(document).ready(function() {
	   $('input[type=reset]').click(function(){
	   		$("#ownerBu_error").empty();
	   });   	
   });
   	
 <#-- $("#ownerBu").change(function() {
        if($(this).val() == null || $(this).val() == "") {
           $("#ownerBu_error").css('display','block');
           $("#ownerBu_error").html('<ul class="list-unstyled"><li id="error1">Please select an item in the list.</li></ul>');
        } else
        	$("#ownerBu_error").css('display','none');
      }); -->   
        
function doSearch(){
	$("#loader").show();
    var businessUnit = $("#ownerBu").val();
    var isValid = "Y";
    
    $("#ownerBu_error").empty();
       	<#-- if(businessUnit == '' || businessUnit == null){
            $("#ownerBu_error").css('display','block');
            $("#ownerBu_error").html('<ul class="list-unstyled"><li>Please select an item in the list.</li></ul>');
            valid = false;
        }
        else{
        	$("#ownerBu_error").css('display','none');
        	$("#searchForm").submit();
        	}-->
        	$("#searchForm").submit();
}

</script>