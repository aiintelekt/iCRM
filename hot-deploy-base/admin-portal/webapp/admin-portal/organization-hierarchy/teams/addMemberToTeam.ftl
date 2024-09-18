<#include "component://ab-ag-grid-support/webapp/ab-ag-grid-support/lib/ag_grid_header.ftl"/>
<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
    <div class="row">
        <div id="main" role="main">
            <#assign extra=' <a href="viewTeam?emplTeamId=${emplTeamId!}" class="btn btn-xs btn-primary">
                <i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>' />
            <@sectionFrameHeader title="${uiLabelMap.AddMembers!}" extra=extra />
            <div class="col-lg-12 col-md-12 col-sm-12">
            
            	<div class="page-header border-b pt-2">
			        <div class="float-right" id="main-grid-action-container">
			        
			        </div>
			        <div class="clearfix"></div>
			    </div>
       			
                <@AgGrid
					userid="${userLogin.userLoginId}" 
					instanceid="TEAM_MEMBER" 
					autosizeallcol="false" 
					gridoptions='{"pagination": true, "paginationPageSize": 10 }'
					requestbody='{"emplTeamId":"${emplTeamId!}"}'
					/>
					
                <#-- 
                <div class="clearfix"></div>
	            <div class="table-responsive">
		                 <div class="loader text-center" id="loader1" sytle="display:none;">
		                  <span></span>
		                  <span></span>
		                  <span></span>
		            	</div>
	            		<div id="T003" style="width: 100%;" class="ag-theme-balham"></div>
	            		<script type="text/javascript" src="/admin-portal-resource/js/ag-grid/org-Hierarchy/teams-member.js"></script> 
						                      
                </div>
                 -->
                 
                <form method="post" action="addMembers" id="addMembersForm" name="addMembersForm">
                 <@inputHidden
                    id="modalId"
                    value="addMember"/>
                    
                 <@inputHidden
                    id="emplTeamId"
                    value="${emplTeamId!}"/>
                    
                 <@inputHidden
                    id="newPartyId"
                    value=""/>
                    
                  <@inputHidden
                    id="partyId"
                    value=""/>
                 </form>
				                
            </div>
        </div>
    </div>    

<script type="text/javascript">
/*
 var gridInstance1 = document.getElementById('T003');
 gridInstance1.addEventListener("buttonBarClickEvent", function(evt){
    console.log("[gridInstance1] button bar click ----> ", evt.detail);
    switch(evt.detail.clickEventId){
        case "add-selected":
            var selectedRows = fag["T003"].getInstanceApi().getSelectedRows();
            if(selectedRows !=null && selectedRows != "" && selectedRows != 'undefined'){
                $('#modal-error').hide();
                $('#newPartyId').val(JSON.stringify(selectedRows));
                console.log("newPartyIds--->"+JSON.stringify(selectedRows));    
                $('#addMembersForm').submit();
            } else{
                $('#modal-error').show();
            }
            break;
        case "clear-selected":
            fag["T003"].getInstanceApi().deselectAll();
            break;
    }
 });
 */
 
</script>


