<#include "component://bootstrap/lib/ofbizFormMacros.ftl"/>
      <div class="row" id="sr-sub-cate">
        <div id="main" role="main">
           <#assign helpUrl = Static["org.groupfio.common.portal.util.DataHelper"].getHelpUrl(delegator, request.getRequestURI()) />  

          <#-- <@sectionFrameHeader title="${uiLabelMap.FindServiceRequestType!}" extra=helpUrl/>  -->
            <div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
            <div id="">
              <#-- <div class="row">
                <div class="iconek">
                  <div class="arrow-down" style="margin-bottom: 10px;" onclick="this.classList.toggle('active')"></div>
                </div>
              </div>  -->
              <div class="">
              <@sectionFrameHeader title="${uiLabelMap.FindServiceRequestType!}" />
              	<form action="#" method="post" id="searchForm" name="searchForm">
                      <@inputHidden 
                        id="searchCriteria"
                      />  
                      <div class="row">
                        <div class="col-md-4 col-lg-4 col-sm-12">
                          <#assign srTypes = delegator.findByAnd("CustRequestAssoc", {"type" : "SRTYPE"},Static["org.ofbiz.base.util.UtilMisc"].toList("value ASC"), false)?if_exists />
                          <#assign srTypeList = Static["org.fio.admin.portal.util.DataHelper"].getDropDownOptions(srTypes, "code","value")?if_exists />
                           <@dropdownCell 
                              id="srTypeId"
                              name="srTypeId"
                              placeholder="Select SR Type"
                              options=srTypeList!
                              value="${requestParameters.srTypeId?if_exists}"
                              allowEmpty=true
                              />
                           
                        </div>
                        <div class="col-md-2 col-sm-2">
	            	     	<@button
	            	        id="main-search-btn"
	            	        label="${uiLabelMap.Find}"
	            	        />
	            	     	<@reset
	            			label="${uiLabelMap.Reset}"/>
	            	    </div>                         
                      </div>                  
                 </form>
               </div>             
            </div>
            <div class="clearfix"></div>
            </div>   
			<div class="clearfix"></div>
            
            <div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
            
			<#assign rightContent='<a title="Create" href="/admin-portal/control/serviceRequestType" class="btn btn-primary btn-xs ml-2" target="_blank"> <i class="fa fa-plus" aria-hidden="true"></i> Create </a>' />   
						 		
			<#-- <@AgGrid
			gridheadertitle=uiLabelMap.ListOfServiceRequestType
			gridheaderid="sr-type-grid-action-container"
			savePrefBtn=true
			clearFilterBtn=true
			exportBtn=true
			insertBtn=false
			updateBtn=false
			removeBtn=false
			headerextra=rightContent
			
		    userid="${userLogin.userLoginId}" 
		    shownotifications="true" 
		    instanceid="PARAM_SR_TYPE" 
		    autosizeallcol="true"
		    debug="false"
		    />  
		    
		    <script type="text/javascript" src="/admin-portal-resource/js/ag-grid/param-unit/sr-type.js"></script>-->
	<input type="hidden" id="userId" value="${userLogin.userLoginId!}"/>
	<input type="hidden" id="externalLoginKey" value="${requestAttributes.externalLoginKey!}"/>

		<@fioGrid 
			id="ListOfServiceRequestType-Grid"
			instanceId="PARAM_SR_TYPE"
			jsLoc="/admin-portal-resource/js/ag-grid/param-unit/sr-type.js"
			headerLabel=uiLabelMap.ListOfServiceRequestType!
			headerId="sr-type-grid-action-container"
			subFltrClearId="sr-type-sub-filter-clear-btn"
			savePrefBtnId="sr-type-save-pref-btn"
			clearFilterBtnId="sr-type-clear-filter-btn"
			headerBarClass="grid-header-no-bar"
			savePrefBtn=false
			clearFilterBtn=false
			exportBtn=true
			subFltrClearBtn=false
			headerExtra=rightContent!
			exportBtnId="sr-type-list-export-btn"
			/>										
			</div>	               
          </div>
        </div>
      </div>
      
<script>  

</script>