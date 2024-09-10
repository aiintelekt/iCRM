 
 <#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
 <script>
 $(function() {
   		//$('.srSubStatusId [data-value="${responseObj.srSubStatus?if_exists!}"]').click();
         //$('#srSubStatusId').val('${responseObj.srSubStatus?if_exists!}');
         //document.querySelector("select#srSubStatusId").setAttribute('selected','selected');
         //$('.srSubStatusId option[value='${responseObj.srSubStatus?if_exists!}']').attr('selected','selected');
		//$('#srSubStatusId').trigger('change');
});

 
 </script>
 <div class="row">
                <div class="col-lg-3 col-md-12 col-sm-12">
                  <h6>Service request : Information</h6>
                  <h3>${requestParameters.srNumber?if_exists}</h3>
                </div>
                <div class="col-lg-9 col-md-12 col-sm-12">
                <form method="post" action="saveServiceRequest" id="commnInfo"  name="commnInfo" >
	                <#assign srNumberUrlParam = requestParameters.srNumber!>
					<input type="hidden" name="externalId" id="externalId" value="${srNumberUrlParam!}" />
                  <div class="bd-callout" style="width: 165px;">
	                   <#assign srSubStatus = delegator.findByAnd("StatusItem", {"statusTypeId" : "SR_SUB_STATUS_ID"}, null, false)>
	                   <#assign srSubStatusList = Static["org.fio.admin.portal.util.DataHelper"].getDropDownOptions(srSubStatus, "statusId","description")?if_exists />
		               <@dropdownCell 
		               id="srSubStatusId1"
		               options=srSubStatusList
		                label=uiLabelMap.SRSubStatus
		               allowEmpty=true
		               placeholder = "Please Select "
		               value="${responseObj.srSubStatus?if_exists!}"
		               />
                   </div>
                   <div class="bd-callout" style="width: 165px;">
	                   <#assign srStatus = delegator.findByAnd("StatusItem", {"statusTypeId" : "SR_STATUS_ID"}, null, false)>
	                   <#assign srStatusList = Static["org.fio.admin.portal.util.DataHelper"].getDropDownOptions(srStatus, "statusId","description")?if_exists />
		               <@dropdownCell 
		               id="srStatusId1"
		                label=uiLabelMap.SRStatus
		               options=srStatusList
		               value="${responseObj.srStatus?if_exists!}" 
		               allowEmpty=true
		               placeholder = "Please Select"
		               />
                   </div>
                    </form>
                  <div class="bd-callout" style="width: 85px;overflow-x:hidden;">
                    <small>SR Sub Category</small>
                    <h5>${responseObj.srSubCategoryName?if_exists}</h5>
                  </div>
                  <div class="bd-callout" style="width: 85px;overflow-x:hidden;">
                    <small>SR Category</small>
                    <h5>${responseObj.srCategoryName?if_exists}</h5>
                  </div>
                  <div class="bd-callout" style="width: 65px;overflow-x:hidden;">
                    <small>SR Type</small>
                    <h5>${responseObj.srTypeName?if_exists}</h5>
                  </div>
				   <div class="bd-callout" style="width: 45px;overflow-x:auto;">
                    <small>Priority</small>
                    <h5>${responseObj.priority?if_exists}</h5>
                  </div>
                  <!--div class="bd-callout" style="width: 45px;overflow-x:auto;">
                    <small>Urgency</small>
                    <h5>${responseObj.urgencyState?if_exists}</h5>
                  </div!-->
                  <div class="bd-callout" style="width: 45px;overflow-x:auto;">
                    <small>Duration</small>
                    <h5>${responseObj.durationDays?if_exists}</h5>
                  </div>
				  <div class="bd-callout" style="width: 65px;overflow-x:auto;">
                    <small>Closed Date</small>
                    <h5>${responseObj.closedOn?if_exists}</h5>
                  </div>
                  <div class="bd-callout" style="width: 65px;overflow-x:auto;">
                    <small>Due Date</small>
                    <h5>${responseObj.dueDate?if_exists}</h5>
                  </div>
                  <div class="bd-callout" style="width: 65px;overflow-x:auto;">
                    <small>Open Date</small>
                    <h5>${responseObj.openDate?if_exists}</h5>
                  </div>
                </div>
              </div>