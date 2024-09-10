<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<script type="text/javascript" src="/sr-portal-resource/js/services/findcustsatisfaction.js"></script>
<div class="top-band bg-white mb-0">
	<div class="col-lg-12 col-md-12 col-sm-12">
		<div class="row">
			<marquee behavior="scroll" direction="left" class="text-danger">
				"System maintenance scheduled for 03-09-2019 from 8 AM SGT to 10 AM SGT. During this time, users may experience unavailability of services"
			</marquee>
		</div>
	</div>
</div>
<div class="row">
   <div id="main" role="main">
      <@sectionFrameHeader title="${uiLabelMap.findCustomerSatisfaction!}" />
        <div class="border rounded bg-light pad-top">
            <#assign formAction = "">
            <form action="${formAction}" method="post" id="findcustsatisfaction" name="">
          
                <div class="row p-2">
                    <div class="col-lg-4 col-md-6 col-sm-12">
                     <@inputCell
		                       name="csatid"
		                       id="csatid"
		                       placeholder="CSAT ID"
		                       maxlength=60
                       />

                      <@dropdownCell
	                          name="csatstatus"
	                          id="csatstatus"
	                          placeholder="Select CSAT Status"
	                          value="${requestParameters.csatstatus?if_exists}"
       
                        />
                         <@inputDate
	                         id="createdOn"
		                     type="date"
		                     value="${requestParameters.createdOn?if_exists}"
	                         inputColSize="col-lg-8 col-md-6 col-sm-12"
                         />
                            
                       </div>
                       <div class="col-lg-4 col-md-6 col-sm-12">
                     
                         <@inputCell
	                           id="externalId"
	                            placeholder="SR Number"
	                            value="${requestParameters.externalId?if_exists}"
                                 maxlength=60 
                           
                           />
                      <#assign srTypes = delegator.findByAnd("CustRequestAssoc", {"type" : "SRTYPE", "active","Y"}, null, false)>
                      <#assign srTypeList = Static["org.fio.admin.portal.util.DataHelper"].getDropDownOptions(srTypes, "code","value")?if_exists />
                         <@dropdownCell
	                            id="srTypeId"  
		                        placeholder="select SR Type"      
		                        allowEmpty=true        
		                        options=srTypeList!
		                        value="${requestParameters.srTypeId?if_exists}"  
		                        required=true  
                            />
                          <@inputDate
	                           id="actualEndDate"
	                           placeholder="End date"
	                           type="date"
	                           required=true  
	                           inputColSize="col-lg-8 col-md-6 col-sm-12"
                            />
                    </div>
                    <div class="col-lg-4 col-md-6 col-sm-12">
                 
                            <@inputCell
	                            id="ownerUserLoginId"
		                        placeholder="Owner"
		                        value="${requestParameters.ownerUserLoginId?if_exists}"
                            />
                        <#assign srcateTypes = delegator.findByAnd("CustRequestAssoc", {"type" : "SRCategory", "active","Y"}, null, false)>
                       <#assign srcateTypeList = Static["org.fio.admin.portal.util.DataHelper"].getDropDownOptions(srcateTypes, "code","value")?if_exists />
                            <@dropdownCell
	                             id="srCategoryId"  
		                        placeholder="select Category Type"  
		                        options=srcateTypeList!    
		                        allowEmpty=true        
		                        value="${requestParameters.srCategoryId?if_exists}"    
                            /> 
                            
                                        
                       <#-- <@submit class="btn btn-sm btn-primary navbar-dark mt" label="${uiLabelMap.Search}"/> -->
                            
                           <@button
	                            id="doSearch"
	                            label="${uiLabelMap.Search}"
	                            onclick="getRowData()"
                            />
                            <@reset
	                             label="${uiLabelMap.Reset}"
	                             id="reset"
                             />
                    </div>
                    
                </div> <#-- End row p-2-->
    <div class="col-lg-12 col-md-12 col-sm-12 profil-sec-padding pt-0">
    <div class="clearfix"></div>
        <div class="page-header border-b pt-2">
             <h2 class="float-left">Search Results </h2>
                   <a id="csr_icon" title="Export to CSV" href="" class="text-dark "> </a> <a id="export_to_excel_icon" title="Export to Excel" href="#" class="btn btn-primary btn-xs" onclick="onBtExport()"><i class="fa fa-file-excel-o" aria-hidden="true"></i> Export</a> <a id="export_to_excel_icon" title="Save Preference" href="#" class="btn btn-primary btn-xs"><i class="fa fa-save " aria-hidden="true"></i> Save Preference</a> <a id="export_to_excel_icon" title="Reassign" href="#" class="btn btn-primary btn-xs" onclick="getSelectedRows()"><i class="fa fa-retweet " aria-hidden="true"></i> Reassign</a>
                       <div class="clearfix"></div>
                  </div>
                <div class="row p-4">
                 <div id="grid" style="width: 100%;" class="ag-theme-balham"></div>
                </div> <#-- End row p-4-->
                
            </form>
        </div> <#-- End pad-top-->
   </div> <#-- End main-->
</div> <#-- End row-->
