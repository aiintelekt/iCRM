<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<div class="row">
    <div id="main" role="main">
        <#assign extraLeft='
	        <a id=task1" title="Task1" href="#" class="btn btn-primary btn-xs" onclick="#"> Task 1</a>
	        <a id="task2" title="Task2" href="#" class="btn btn-primary btn-xs" onclick="#">Task 2</a>
	        <a id="task3" title="Task3" href="#" class="btn btn-primary btn-xs" onclick="#">Task 3</a>
	        <a id="task4" title="Task4" href="#" class="btn btn-primary btn-xs" onclick="#">Task 4</a>
	        <a id="task5" title="Task5" href="#" class="btn btn-primary btn-xs" onclick="#">Task 5</a>
	        ' />
        <@sectionFrameHeader  title="Recently Viewed:"  extraLeft=extraLeft  />
        <div class="col-lg-12 col-md-12 col-sm-12">
            <div class="card-header margin-adj-accordian pad-top">
                <form method="post" id="searchForm" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
                    <div class="row p-2">
                        <div class="col-lg-4 col-md-6 col-sm-12">
                            <#--<@inputRow 
                            id="cif"
                            placeholder=uiLabelMap.cinOrCif
                            inputColSize="col-sm-12"
                            iconClass="fa fa-user"
                            required=false
                            /> -->
                            <@inputRow 
	                            id="partyId"
	                            placeholder="Lead Id"
	                            inputColSize="col-sm-12"
	                            iconClass="fa fa-user"
	                            required=false
	                            /> 
                            <@inputRow 
	                            id="name"
	                            placeholder="Lead Name"
	                            inputColSize="col-sm-12"
	                            iconClass="fa fa-user-circle-o"
	                            required=false
	                            /> 
                            <@inputRow 
	                            id="localName"
	                            placeholder=uiLabelMap.localName
	                            inputColSize="col-sm-12"
	                            iconClass="fa fa-user-circle-o"
	                            required=false
	                            /> 
                        </div>
                        <div class="col-lg-4 col-md-6 col-sm-12">
                            <@inputRow 
	                            id="email"
	                            placeholder=uiLabelMap.email
	                            inputColSize="col-sm-12"
	                            iconClass="fa fa-envelope"
	                            required=false
	                            /> 
                            <@inputRow 
	                            id="phone"
	                            placeholder="Phone"
	                            inputColSize="col-sm-12"
	                            iconClass="fa fa-phone"
	                            required=false
	                            />
                        </div>
                        <div class="col-md-2 col-sm-2">
                            <@button
	                            id="main-search-btn"
	                            label="${uiLabelMap.Find}"
	                            />	
                            <@reset
                            	label="${uiLabelMap.Reset}"
                            	/>
                        </div>
                    </div>
                </form>
            </div>
            <#assign rightContent='<a title="Create" href="/lead-portal/control/createLead" class="btn btn-primary btn-xs ml-2"> <i class="fa fa-plus" aria-hidden="true"></i> Create </a>' />   
            <@AgGrid
	            gridheadertitle=uiLabelMap.ListOfLeads
	            gridheaderid="lead-grid-action-container"
	            savePrefBtn=true
	            clearFilterBtn=true
	            exportBtn=true
	            insertBtn=false
	            updateBtn=false
	            removeBtn=false
	            headerextra=rightContent
	            userid="${userLogin.userLoginId}" 
	            shownotifications="true" 
	            instanceid="LEADS_LIST" 
	            autosizeallcol="true"
	            debug="false"
	            serversidepaginate=true
	            statusBar=true
	            />
            <script type="text/javascript" src="/lead-portal-resource/js/ag-grid/find-lead-new.js"></script> 
        </div>
    </div>
</div>
