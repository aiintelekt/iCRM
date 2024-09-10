<#include "component://homeapps/webapp/homeapps/lib/ofbizFormMacros.ftl"/>
<link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/1.12.1/css/jquery.dataTables.css">
<script type="text/javascript" language="javascript" src="/bootstrap/js/jquery.dataTables.min.js"></script>
<#assign campaignInputId = 1000>
<#assign campaignDaySincInputId = 5000>
<#assign campaignTriggerInputId = 10000>
<#-- 
<div class="page-header border-b">
   <h1 class="float-left">${uiLabelMap.Update} ${uiLabelMap.SegmentValue} <#if campaignFieldDet.groupName?has_content>for [ ${campaignFieldDet.groupName} : <#if marketingCampaignDet?has_content>${marketingCampaignDet.campaignName?if_exists}</#if> <i class="fa fa-arrow-right" aria-hidden="true"></i> ${campaignFieldDet.customFieldName} ]</#if> </h1>
   <div class="float-right">
      <a href="/campaign/control/viewMarketingCampaign?marketingCampaignId=${marketingCampaignId!}&activeTab=drip" class="btn btn-xs btn-primary m5"> Back</a>
   </div>
</div>
-->
<#assign requestURI = ""/>
<#assign requestURI = request.getParameter("marketingCampaignId")! />
<div class="clearfix"> </div>
<#if requestURI?has_content>
<div class = "" style="width:100%">
<#else>
<div class = "row" style="width:100%">
   </#if>
   <div id="multiple-value-config" class="col-md-12 col-sm-12 dash-panel">
      <div class="">
         <div class="panel panel-default">
            <div class="panel-heading" role="tab" id="campaignConfig-heading">
               <h4 class="camp-config">
                  <a role="button" data-toggle="collapse" data-parent="#accordionMenu"
                     href="#accordion-campaignConfig" aria-expanded="false"
                     aria-controls="collapseOne"> ${uiLabelMap.CampaignConfig} </a>
               </h4>
            </div>
            <div id="accordion-campaignConfig" class="panel-collapse collapse <#if isOpenCampaignConfig?has_content>show</#if>"
               role="tabpanel" aria-labelledby="campaignConfig-heading">
               <div class="panel-body">
                  <div class="portlet-body form">
                     <div class="row">
                        <div class="col-md-6 col-sm-6">
                           <div class="form-group row">
                              <label class="col-sm-4 col-form-label">Message Type</label>
                              <div class="col-sm-7">
                                 <div class="form-check-inline">
                                    <label class="form-check-label"> <input
                                    class="form-check-input" name="configType" value="TRIGGER" type="radio" <#if campaignConfig.configType?has_content && campaignConfig.configType == "TRIGGER">checked</#if> >Trigger
                                    </label>
                                 </div>
                                 <div class="form-check-inline">
                                    <label class="form-check-label"> <input
                                    class="form-check-input" name="configType" value="BATCH" type="radio" <#if campaignConfig.configType?has_content && campaignConfig.configType == "BATCH">checked</#if> >Batch
                                    </label>
                                 </div>
                              </div>
                           </div>
                        </div>
                        <div class="col-md-6 col-sm-6">
                           <div class="form-group row">
                              <label class="col-sm-4 col-form-label">Include Coupon</label>
                              <div class="col-sm-7">
                                 <div class="form-check-inline">
                                    <label class="form-check-label"> <input
                                       class="form-check-input" name="isCouponSegment" value="Y" type="radio" <#if campaignConfig.isCouponSegment?has_content && campaignConfig.isCouponSegment == "Y">checked</#if>>Yes
                                    </label>
                                 </div>
                                 <div class="form-check-inline">
                                    <label class="form-check-label"> <input
                                       class="form-check-input" name="isCouponSegment" value="N" type="radio" <#if campaignConfig.isCouponSegment?has_content && campaignConfig.isCouponSegment == "N">checked</#if>>No
                                    </label>
                                 </div>
                              </div>
                           </div>
                        </div>
                     </div>
                     <div id="batch-configuration" style="display: <#if campaignConfig.configType?has_content && campaignConfig.configType == "BATCH">
                        block<#else>none</#if>">
                        <div class="row">
                           <div class="col-md-6 col-sm-6">
                              <div class="form-group row">
                                 <div class="offset-sm-4 col-sm-7">
                                    <div class="form-check-inline">
                                       <label class="form-check-label"> <input
                                       class="form-check-input" name="configBatchType" value="SPEC_DATE" type="radio" <#if campaignConfig.configBatchType?has_content && campaignConfig.configBatchType == "SPEC_DATE">checked</#if> >
                                       Specific Calendar Date
                                       </label>
                                    </div>
                                    <div class="form-check-inline">
                                       <label class="form-check-label"> <input
                                       class="form-check-input" name="configBatchType" value="DAY_SINCE" type="radio" <#if campaignConfig.configBatchType?has_content && campaignConfig.configBatchType == "DAY_SINCE">checked</#if> > 
                                       Inception Days Required
                                       </label>
                                    </div>
                                 </div>
                              </div>
                           </div>
                        </div>
                        <div class="row">
                           <div class="col-md-12 col-sm-12">
                              <form id="updateBatchDateCampaignForm">
                                 <input type="hidden" name="groupId" value="${groupId!}">
                                 <input type="hidden" name="customFieldId" value="${customFieldId!}">
                                 <input type="hidden" name="marketingCampaignId" value="${marketingCampaignId!}">
                                 <input type="hidden" name="isCouponSegmentDateValue" value="${campaignConfig.isCouponSegment!}">
                                 <div id="date-campaign-section" style="display: <#if campaignConfig.configBatchType?has_content && campaignConfig.configBatchType == "SPEC_DATE">
                                    block<#else>none</#if>">
                                    <#if campaignConfigAssocList?has_content && campaignConfig.configBatchType?has_content && campaignConfig.configBatchType == "SPEC_DATE" >
                                    <#assign counter = 0>
                                    <#assign globalDateFormat=globalDateFormat/>
                                    <#list campaignConfigAssocList as ca>
                                    <#assign counter = counter + 1>
                                    <#assign marketingCampaign = ca.getRelatedOne("MarketingCampaign", false)! />   
                                    <#assign isProcessed = ca.get("isProcessed")?if_exists /> 
                                    <#assign specificDate = Static["org.ofbiz.base.util.UtilDateTime"].timeStampToString(ca.specificDate, globalDateFormat, timeZone, locale)/>
									<div id="templatePreviewScreen_${campaignInputId!}" class="modal fade panel-height" role="dialog" style="margin-left: -237px;">
										<div class="modal-dialog modal-md marignStyle">
											<div class="modal-content" style="width: 1200px; height:590px;">
												<div class="modal-header">
													<h3>Template Preview</h3>
													<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
												</div>
												<div id="">
													<div class="modal-body">
													<form></form>
														<form id="campaign_tplContent_form_${campaignInputId!}">
															<input type="hidden" id="externalLoginKey" name="externalLoginKey" value="${requestAttributes.externalLoginKey?if_exists}"/>
															<input type="hidden" id="marketingCampaignId" name="marketingCampaignId" value=""/>
														</form>
														<div id="campaign_tplContent_${campaignInputId!}" class="row padding-r tplPicker" style="height: 350px"></div><br>
													</div><br>
												</div>
											</div>
										</div>
									</div>
                                    <div class="form-group row campaign-content <#if isProcessed?has_content && isProcessed=="Y">border border-warning</#if>" >
                                       <label class="col-sm-2 col-form-label">Date/Campaign</label>
                                       <div class="col-sm-4">
                                       		<@simpleDateInput 
	                                          name="specDateCampaign"
	                                          dateStartFrom=parentCampaign.startDate
	                                          dateEndTo=parentCampaign.endDate	
	                                          value=specificDate                   
	                                          dateFormat=globalDateFormat?upper_case
	                                          />
                                       </div>
                                       <div class="col-sm-4">
                                          <div class="input-group">
                                             <input id="spec-date-campaign-display-${campaignInputId}" class="form-control input-sm specDateCampaignSelected" placeholder="Select Campaign" type="text" value="${marketingCampaign.campaignName!} (${marketingCampaign.marketingCampaignId!})" readonly>
                                             <input id="spec-date-campaign-${campaignInputId}" type="hidden" name="specDateCampaignSelected" value="${marketingCampaign.marketingCampaignId!}"> 
                                             <span class="input-group-addon"> 
                                             <span class="fa fa-list campaign-picker" data-campaignInputId="${campaignInputId}" onclick="campaignPicker(this, '${isProcessed!}')" > 
                                             </span>
                                             <#if (counter > 1) && (!isProcessed?has_content || isProcessed=="N")>
                                             <a class="plus-icon01 rd ml-1" onclick="removeCampaignRepeateContent(this)"><i class="fa fa-minus-circle" aria-hidden="true"></i></a>
                                             </#if> 
                                             <a onclick="addCampaignRepeateContent(this)" class="gd ml-1"><i class="fa fa-plus-circle"></i></a>
                                             </span>
                                          </div>
                                       </div>
										<div class="col-sm-2">
											<span><a onclick="showTemplates('${campaignInputId}','${marketingCampaign.marketingCampaignId!}')">
											<span style="cursor: pointer; color: #02829d !important;">View Templates</span></a></span>
										</div>
                                    </div>
                                    <#assign campaignInputId = campaignInputId + 1>
                                    </#list>
                                    <#else>
                                    <div class="form-group row campaign-content" >
                                       <label class="col-sm-2 col-form-label">Date/Campaign </label>
                                       <div class="col-sm-4">
                                        <@simpleDateInput 
                                          name="specDateCampaign"
                                          dateStartFrom=parentCampaign.startDate
                                          dateEndTo=parentCampaign.endDate	
                                          value=""                                         
                                          dateFormat=globalDateFormat?upper_case                                          
                                          /> 
                                       </div>
                                       <div class="col-sm-4">
                                          <div class="input-group">
                                             <input id="spec-date-campaign-display-${campaignInputId}" class="form-control input-sm specDateCampaignSelected" placeholder="Select Campaign" type="text" value="" readonly>
                                             <input id="spec-date-campaign-${campaignInputId}" type="hidden" name="specDateCampaignSelected" value=""> 
                                             <span class="input-group-addon"> 
                                             <span class="fa fa-list campaign-picker" data-campaignInputId="${campaignInputId}" onclick="campaignPicker(this, '')"> 
                                             </span>
                                             <a onclick="addCampaignRepeateContent(this)" class="gd ml-1"><i class="fa fa-plus-circle"></i></a>
                                             </span>
                                          </div>
                                       </div>
											<div class="col-sm-2">
												<div id="campaignTemplatePreviewScreen_${campaignInputId!}" class="modal fade panel-height campaign-template-popup" role="dialog" style="margin-left: -237px;">
													<div class="modal-dialog modal-md marignStyle">
														<div class="modal-content" style="width: 1200px; height:590px;">
															<div class="modal-header">
																<h3>Template Preview</h3>
																<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
															</div>
															<div id="">
																<div class="modal-body">
																	<form></form>
																	<form id="campaign_tplContent_form_${campaignInputId!}" class="campaign_tplContent_form">
																			<input type="hidden" id="externalLoginKey" name="externalLoginKey" value="${requestAttributes.externalLoginKey?if_exists}"/>
																			<input type="hidden" id="marketingCampaignId" name="marketingCampaignId_${campaignInputId!}" value=""/>
																	</form>
																	<div id="campaign_tplContent_${campaignInputId!}" class="row padding-r tplPicker campaign_tplContent" style="height: 350px"></div><br>
																</div><br>
															</div>
														</div>
													</div>
												</div>
												<span><a class="showCampaignTemplates" onclick="showCampaignTemplates('${campaignInputId}')">
												<span style="cursor: pointer; color: #02829d !important;">View Templates</span></a></span>
											</div>
                                    </div>
                                    </#if>
                                 </div>
                              </form>
                              <form id="updateBatchDaysCampaignForm">
                                 <input type="hidden" name="groupId" value="${groupId!}">
                                 <input type="hidden" name="customFieldId" value="${customFieldId!}">
                                 <input type="hidden" name="marketingCampaignId" value="${marketingCampaignId!}">
                                 <input type="hidden" name="isCouponSegmentDaysValue" value="${campaignConfig.isCouponSegment!}">
                                 <div id="days-campaign-section" style="display: <#if campaignConfig.configBatchType?has_content && campaignConfig.configBatchType == "DAY_SINCE">
                                    block<#else>none</#if>">
                                    <#if campaignConfigAssocList?has_content && campaignConfig.configBatchType?has_content && campaignConfig.configBatchType == "DAY_SINCE" >
                                    <#assign counter = 0>
                                    <#list campaignConfigAssocList as ca>
                                    <#assign counter = counter + 1>
                                    <#assign marketingCampaign = ca.getRelatedOne("MarketingCampaign", false)! />
                                    <#assign isProcessed = ca.get("isProcessed")?if_exists />
									<div id="templatePreviewScreen_${campaignDaySincInputId!}" class="modal fade panel-height" role="dialog" style="margin-left: -237px;">
										<div class="modal-dialog modal-md">
											<div class="modal-content" style="width: 1200px; height:590px;">
												<div class="modal-header">
													<h3>Template Preview</h3>
													<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
												</div>
												<div id="">
												<div class="modal-body">
														<form></form>
														<form id="campaign_tplContent_form_${campaignDaySincInputId!}">
															<input type="hidden" id="externalLoginKey" name="externalLoginKey" value="${requestAttributes.externalLoginKey?if_exists}"/>
															<input type="hidden" id="marketingCampaignId" name="marketingCampaignId" value=""/>
														</form>
														<div id="campaign_tplContent_${campaignDaySincInputId!}" class="row padding-r tplPicker" style="height: 350px"></div><br>
													</div><br>
												</div>
											</div>
										</div>
									</div>
                                    <div class="form-group row campaign-content <#if isProcessed?has_content && isProcessed=="Y">border border-warning</#if>">
                                       <label class="col-sm-2 col-form-label">Days/Campaign </label>
                                       <div class="col-sm-4">
                                          <input name="daySinceCampaign" value="${ca.daySince!}" class="form-control input-sm" placeholder="Number of days" type="number" min="0" <#if isProcessed?has_content && isProcessed=="Y">readonly</#if> >
                                       </div>
                                       <div class="col-sm-4">
                                          <div class="input-group">
                                             <input id="spec-date-campaign-display-${campaignDaySincInputId}" class="form-control input-sm specDateCampaignSelected" placeholder="Select Campaign" type="text" value="${marketingCampaign.campaignName!} (${marketingCampaign.marketingCampaignId!})" readonly>
                                             <input id="spec-date-campaign-${campaignDaySincInputId}" type="hidden" name="specDateCampaignSelected" value="${marketingCampaign.marketingCampaignId!}"> 
                                             <span class="input-group-addon"> 
                                             <span class="fa fa-list campaign-picker" data-campaignInputId="${campaignDaySincInputId}" onclick="campaignPicker(this, '${isProcessed!}')" > 
                                             </span>
                                             <#if (counter > 1) && (!isProcessed?has_content || isProcessed=="N")>
                                             <a class="plus-icon01 rd ml-1" onclick="removeCampaignRepeateContent(this)"><i class="fa fa-minus-circle" aria-hidden="true"></i></a>
                                             </#if> 
                                             <a onclick="addCampaignRepeateContent(this)" class="gd ml-1"><i class="fa fa-plus-circle"></i></a>
                                             </span>
                                          </div>
                                       </div>
										<div class="col-sm-2">
											<span><a onclick="showTemplates('${campaignDaySincInputId}','${marketingCampaign.marketingCampaignId!}')">
												<span style="cursor: pointer; color: #02829d !important;">View Templates</span></a>
											</span>
										</div>
                                    </div>
                                    <#assign campaignDaySincInputId = campaignDaySincInputId + 1>
                                    </#list>
                                    <#else>
                                    <div class="form-group row campaign-content" >
                                       <label class="col-sm-2 col-form-label">Days/Campaign </label>
                                       <div class="col-sm-4">
                                          <input name="daySinceCampaign" class="form-control input-sm daySinceCampaign" placeholder="Number of days" type="number" min="0" oninput="validity.valid||(value='');">
                                       </div>
                                       <div class="col-sm-4">
                                          <div class="input-group">
                                             <input id="spec-date-campaign-display-${campaignDaySincInputId}" class="form-control input-sm specDateCampaignSelected" placeholder="Select Campaign" type="text" value="" readonly>
                                             <input id="spec-date-campaign-${campaignDaySincInputId}" type="hidden" name="specDateCampaignSelected" value=""> 
                                             <span class="input-group-addon"> 
                                             <span class="fa fa-list campaign-picker" data-campaignInputId="${campaignDaySincInputId}" onclick="campaignPicker(this, '')"> 
                                             </span>
                                             <a onclick="addCampaignRepeateContent(this)" class="gd ml-1"><i class="fa fa-plus-circle"></i></a>
                                             </span>
                                          </div>
                                       </div>
												<div class="col-sm-2">
													<div id="campaignTemplatePreviewScreen_${campaignDaySincInputId!}" class="modal fade panel-height campaign-template-popup" role="dialog" style="margin-left: -237px;">
														<div class="modal-dialog modal-md marignStyle">
															<div class="modal-content" style="width: 1200px; height:590px;">
																<div class="modal-header">
																	<h3>Template Preview</h3>
																	<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
																</div>
																<div id="">
																	<div class="modal-body">
																		<form></form>
																		<form id="campaign_tplContent_form_${campaignDaySincInputId!}" class="campaign_tplContent_form">
																			<input type="hidden" id="externalLoginKey" name="externalLoginKey" value="${requestAttributes.externalLoginKey?if_exists}"/>
																			<input type="hidden" id="marketingCampaignId" name="marketingCampaignId_${campaignDaySincInputId!}" value=""/>
																		</form>
																		<div id="campaign_tplContent_${campaignDaySincInputId!}" class="row padding-r tplPicker campaign_tplContent" style="height: 350px"></div><br>
																	</div><br>
																</div>
															</div>
														</div>
													</div>
														<span><a class="showCampaignTemplates" onclick="showCampaignTemplates('${campaignDaySincInputId}')">
														<span style="cursor: pointer;color: #02829d !important;">View Templates</span></a></span>
												</div>
                                    </div>
                                    </#if>
                                 </div>
                              </form>
                           </div>
                        </div>
                     </div>
                     <div id="trigger-configuration" style="display: <#if campaignConfig.configType?has_content && campaignConfig.configType == "TRIGGER">
                        block<#else>none</#if>">
                        <div class="row">
                           <div class="col-md-12 col-sm-12">
                              <form id="updateTriggerCampaignForm">
                                 <input type="hidden" name="groupId" value="${groupId!}">
                                 <input type="hidden" name="customFieldId" value="${customFieldId!}">
                                 <#if campaignConfigAssocList?has_content && campaignConfig.configType?has_content && campaignConfig.configType == "TRIGGER" >
                                 <#assign counter = 0>
                                 <#list campaignConfigAssocList as ca>
                                 <#assign counter = counter + 1>
                                 <#assign isProcessed = ca.get("isProcessed")?if_exists />
                                 <#assign marketingCampaign = ca.getRelatedOne("MarketingCampaign", false)! />
                                 <div class="form-group row campaign-content <#if isProcessed?has_content && isProcessed=="Y">border border-warning</#if>" >
                                    <label class="col-sm-2 col-form-label">URL/Campaign </label>
                                    <div class="col-sm-4">
                                       <input name="triggerUrl" value="${ca.triggerUrl!}" class="form-control input-sm" placeholder="URL" type="url" <#if isProcessed?has_content && isProcessed=="Y">readonly</#if> >
                                    </div>
                                    <div class="col-sm-6">
                                       <div class="input-group">
                                          <input id="spec-date-campaign-display-${campaignTriggerInputId}" class="form-control input-sm specDateCampaignSelected" placeholder="Select Campaign" type="text" value="${marketingCampaign.campaignName!} (${marketingCampaign.marketingCampaignId!})" readonly>
                                          <input id="spec-date-campaign-${campaignTriggerInputId}" type="hidden" name="specDateCampaignSelected" value="${marketingCampaign.marketingCampaignId!}"> 
                                          <span class="input-group-addon"> 
                                          <span class="fa fa-list campaign-picker" data-campaignInputId="${campaignTriggerInputId}" onclick="campaignPicker(this, '${isProcessed!}')"> 
                                          </span>
                                          </span>
                                       </div>
                                    </div>
                                 </div>
                                 <#assign campaignTriggerInputId = campaignTriggerInputId + 1>
                                 </#list>
                                 <#else>
                                 <div class="form-group row campaign-content" >
                                    <label class="col-sm-2 col-form-label">URL/Campaign </label>
                                    <div class="col-sm-4">
                                       <input name="triggerUrl" class="form-control input-sm" placeholder="URL" type="url">
                                    </div>
                                    <div class="col-sm-6">
                                       <div class="input-group">
                                          <input id="spec-date-campaign-display-${campaignTriggerInputId}" class="form-control input-sm specDateCampaignSelected" placeholder="Select Campaign" type="text" value="" readonly>
                                          <input id="spec-date-campaign-${campaignTriggerInputId}" type="hidden" name="specDateCampaignSelected" value=""> 
                                          <span class="input-group-addon"> 
                                          <span class="fa fa-list campaign-picker" data-campaignInputId="${campaignTriggerInputId}" onclick="campaignPicker(this, '')"> 
                                          </span>
                                          </span>
                                       </div>
                                    </div>
                                 </div>
                                 </#if>
                              </form>
                           </div>
                        </div>
                     </div>
                     <div class="row">
                        <div class="col-md-12 col-sm-12">
                           <div class="form-group row campaign-content">
                              <label class="col-sm-2"></label>
                              <div class="col-sm-4">
                                 <button id="campaignConfigBtn" type="button" class="btn btn-sm btn-primary mt">Save</button>
                              </div>
                           </div>
                        </div>
                     </div>
                  </div>
               </div>
            </div>
         </div>
      </div>
   </div>
</div>
<div id="select-campaign" class="modal fade" role="dialog">
   <div class="modal-dialog modal-lg">
      <!-- Modal content-->
      <div class="modal-content">
         <div class="modal-header">
            <h4 class="modal-title">${uiLabelMap.campaignListView}</h4>
            <button type="reset" class="close" data-dismiss="modal">&times;</button>
         </div>
         <div class="modal-body">
            <div class="row padding-r">
               <div class="col-md-12 col-sm-12 ">
                  <div id="myBtnContainer" class="row mb-2">
                     <div class="col-md-3 col-sm-3">
                        <div class="form-group autocomplete">
                           <div class="input-group search-bg autocomplete m5">
                              <input type="text" class="form-control" placeholder="Search Campaigns" id="campaignNameToFind" name="campaignNameToFind" >
                              <span class="input-group-addon"> <a class="btn btn-sm" title="Search Campaigns" href="javascript:findCampaignLists();"><i class="fa fa-search" style="color: white;" aria-hidden="true"></i></a></span>
                           </div>
                        </div>
                     </div>
                  </div>
               </div>
            </div>
            <div class="table-responsive">
               <table id="list-picker-campaign" class="table table-striped">
                  <thead>
                     <tr>
                        <th>Campaign Name</th>
                        <th>Start Date</th>
                        <th>End Date</th>
                     </tr>
                  </thead>
               </table>
            </div>
         </div>
      </div>
   </div>
</div>
<script>
   $(function(){
     $('#campaignNameToFind').keypress(function(e){
       if(e.which == 13) {
       	findCampaignLists();
       }
     });
   });
</script>
<script>
jQuery(document).ready(function() {
   	$('.form_datetime').datetimepicker();

   	$('#list-picker-campaign').DataTable({
   		"order": [],
   		"fnDrawCallback": function(oSettings) {
   			resetDefaultEvents();
   		}
   	});

   	$('input[name="configType"]').on('click', function() {

   		if ($(this).val() == "BATCH") {
   			$("#batch-configuration").show();
   			$("#trigger-configuration").hide();
   		} else if ($(this).val() == "TRIGGER") {
   			$("#batch-configuration").hide();
   			$("#trigger-configuration").show();
   		}

   	});
  $(document).on("click", "[name=isCouponSegment]", function() {
    let couponSegmentValue = $(this).val();
    $('input[name="isCouponSegmentDateValue"]').val(couponSegmentValue);
    $('input[name="isCouponSegmentDaysValue"]').val(couponSegmentValue);
  });
   	$('input[name="configBatchType"]').on('click', function() {
   		//alert($(this).val());

   		if ($(this).val() == "SPEC_DATE") {
   			$("#date-campaign-section").show();
   			$("#days-campaign-section").hide();
   		} else if ($(this).val() == "DAY_SINCE") {
   			$("#days-campaign-section").show();
   			$("#date-campaign-section").hide();
   		}

   	});

   	$('#select-campaign').on('click', '.campaign-selected', function() {
   		var value = $(this).children("span").attr("value");
   		//alert(value);
   		var campaignId = $(this).children('input[name="campaignId"]').val();
   		$.post("checkCampaignTemplate", {
   			"campaignId": campaignId
   		}, function(data) {
   			if (data.flag == "N") {
   				showAlert("error", "Please select the campaign which contains template");
   			} else {
   				$("#spec-date-campaign-display-" + specDateCampaignSelected).val(value);
   				$("#spec-date-campaign-" + specDateCampaignSelected).val(campaignId);
				$('#campaignTemplatePreviewScreen_' + specDateCampaignSelected + ' #campaign_tplContent_form_' + specDateCampaignSelected + ' [name="marketingCampaignId_' + specDateCampaignSelected + '"]').val(campaignId);
   				$('#select-campaign').modal('hide');
   			}
   		});
   	});

   	$('#campaignConfigBtn').on('click', function() {
   		
   		var configType = $('input[name="configType"]:checked').val();
   		var configBatchType = $('input[name="configBatchType"]:checked').val();
   		//alert( $('input[name="configType"]:checked').val() );

   		if (configType == "BATCH") {
   			if (configBatchType == "SPEC_DATE") {
   				$.post('updateBatchDateCampaign', $('#updateBatchDateCampaignForm').serialize(), function(returnedData) {

   					if (returnedData.code == 200) {
   						showAlert("success", returnedData.message);
   					}

   				});
   			} else if (configBatchType == "DAY_SINCE") {
   				$.post('updateBatchDaysCampaign', $('#updateBatchDaysCampaignForm').serialize(), function(returnedData) {

   					if (returnedData.code == 200) {
   						showAlert("success", returnedData.message);
   					}

   				});
   			}
   		} else if (configType == "TRIGGER") {
   			$.post('updateTriggerCampaign', $('#updateTriggerCampaignForm').serialize(), function(returnedData) {
   				if (returnedData.code == 200) {
   					showAlert("success", returnedData.message);
   				}
   			});
   		}
   	});
   	
<#if isDripProcessed?has_content && isDripProcessed=="Y">   	
	$('input[type=radio]').prop("disabled", true);
	//$('input[name=daySinceCampaign]').prop("readonly", true);
	//$('input[name=specDateCampaign]').prop("readonly", true);
	showAlert('warning', 'Processed Campaigns# ${processedCampaignIds!}');
</#if>
	
});
   
var specDateCampaignSelected;
function campaignPicker(actionButton, isProcessed) {
	if (!isProcessed || isProcessed=='N') {
		$('#select-campaign').modal("show");
		specDateCampaignSelected = $(actionButton).attr("data-campaignInputId");
	}
	findCampaignLists();
}
   
var campaignInputId;
function addCampaignRepeateContent(actionButton) {
	
	var cloneHtml = $(actionButton).closest(".campaign-content").clone();

	if ($(actionButton).parent().children().find('[class=\"fa fa-minus-circle\"]').length == 0) {
		cloneHtml.children().find('[class=\"fa fa-plus-circle\"]').parent().before('<a class="plus-icon01 rd ml-1" onclick="removeCampaignRepeateContent(this)"><i class="fa fa-minus-circle" aria-hidden="true"></i></a>');
	}

	if (!campaignInputId) {
		campaignInputId = Number($(actionButton).closest(".campaign-content").children().find('.campaign-picker').attr("data-campaignInputId"));
	}
	campaignInputId = campaignInputId + 1;
	
	let campaignPickerBtn = cloneHtml.children().find('.campaign-picker');

	cloneHtml.children().find('.campaign-picker').attr("data-campaignInputId", campaignInputId);

	var popupElement = cloneHtml.children().find('.campaign-template-popup');
	if (popupElement.length > 0) {
		popupElement.attr("id", "campaignTemplatePreviewScreen_" + campaignInputId);
	}
	cloneHtml.children().find('.campaign-template-popup').attr("id", "campaignTemplatePreviewScreen_" + campaignInputId);
	cloneHtml.find('#campaignTemplatePreviewScreen_' + campaignInputId).find('#marketingCampaignId').attr("name", "marketingCampaignId_" + campaignInputId);
	cloneHtml.find('#campaignTemplatePreviewScreen_' + campaignInputId).find('[name="marketingCampaignId_' + campaignInputId + '"]').val("");
	console.log(cloneHtml.find('#campaignTemplatePreviewScreen_' + campaignInputId).find('[name="marketingCampaignId_' + campaignInputId + '"]').val(""));
	var showCampaignTemplates = cloneHtml.children().find('.showCampaignTemplates');
	if (showCampaignTemplates.length > 0) {
		showCampaignTemplates.attr("onclick", "showCampaignTemplates('" + campaignInputId + "')");
	}
	cloneHtml.children().find('.campaign_tplContent').attr("id", "campaign_tplContent_"+campaignInputId);
	cloneHtml.children().find('.campaign_tplContent_form').attr("id", "campaign_tplContent_form_"+campaignInputId);

	cloneHtml.children().find('.specDateCampaignSelected').attr("id", "spec-date-campaign-display-" + campaignInputId);
	cloneHtml.children().find('[name="specDateCampaignSelected"]').attr("id", "spec-date-campaign-" + campaignInputId);

	cloneHtml.children().find('[name="specDateCampaignSelected"]').val("");
	cloneHtml.children().find('.form-control').val("");
	
	cloneHtml.children().find('.campaign-picker').attr("onclick", "campaignPicker(this, 'N')");
	cloneHtml.children().find('[name="daySinceCampaign"]').prop("readonly", false);
	cloneHtml.children().find('[name="specDateCampaign"]').prop("readonly", false);
	cloneHtml.attr("class", 'form-group row campaign-content');

	$(actionButton).closest(".campaign-content").after(cloneHtml);

	$('.form_datetime').datetimepicker();

}

function removeCampaignRepeateContent(actionButton) {
	$(actionButton).closest(".campaign-content").remove();
}
   
   
   
$(document).ready(function(){
   $('.form_datetime').datetimepicker();
   	localStorage.clear();
   	findCampaignLists();
   });
   
   function findCampaignLists(){
   
   	var campaignNameToFind = $("#campaignNameToFind").val();
   	var marketingCampaignId = "${requestURI!}";
   	var notIncludeCampaignId = "";
   	if (marketingCampaignId){
   		notIncludeCampaignId=marketingCampaignId;
   	}
   	$('input[name="specDateCampaignSelected"]').each(function() {
   		var idValue =$(this).val();
   		if (idValue){
   			notIncludeCampaignId+=","+idValue;
   		}
       });
   	let campaignStatusId =""; // MKTG_CAMP_PUBLISHED
   	let isProcessed ="Y";
   	$("#loader").show();
   	var url = "findCampaignListsAjax?campaignNameToFind="+campaignNameToFind+"&campaignStatusId="+campaignStatusId+"&filterOutCampaign="+notIncludeCampaignId+"&isProcessed="+isProcessed;
   	$('#list-picker-campaign').DataTable( {
   		    "processing": true,
   		    "serverSide": true,
   		    "destroy": true,
   		    "filter" : false,
   		    "ajax": {
   	            "url": url,
   	            "type": "POST"
   	        },
   	        "Paginate": true,
   			"language": {
   				"emptyTable": "No data available in table",
   				"info": "Showing _START_ to _END_ of _TOTAL_ entries",
   				"infoEmpty": "No entries found",
   				"infoFiltered": "(filtered1 from _MAX_ total entries)",
   				"lengthMenu": "Show _MENU_ entries",
   				"zeroRecords": "No matching records found",
   				"oPaginate": {
   					"sNext": "Next",
   					"sPrevious": "Previous"
   				}
   			},
   	         "pageLength": 10,
   	         "bAutoWidth":false,
   	         "stateSave": true,
   	         "columns": [
   	           
   	            { "data":  null,
   					"render": function(data, type, row, meta){
   						if(type === 'display'){
                           data = '<a href="#" class="campaign-selected"><span value ="'+row.campaignName+' ('+row.campaignId+')"></span>'+row.campaignName+' ('+row.campaignId+')<input type="hidden" name="campaignId" value="'+row.campaignId+'"></a>';						}
   						return data;
   					 }
   				 },
   	            { "data":  "startDate"},
   	            { "data":  "endDate"},
   	            
   				 
   	          ]
   		});
   	$("#loader").hide();
   }
function showTemplates(campaignInputId, marketingCampaignId) {
	setValueAndShowModal(campaignInputId, marketingCampaignId, 'templatePreviewScreen');
	getCampaignIdForTemplate(campaignInputId, 'templatePreviewScreen');
}
function showCampaignTemplates(campaignInputId) {
	setValueAndShowModal(campaignInputId, null, 'campaignTemplatePreviewScreen');
	getCampaignIdForTemplate(campaignInputId, 'campaignTemplatePreviewScreen');
}
function setValueAndShowModal(campaignInputId, marketingCampaignId, modalPrefix) {
	var modalSelector = '#' + modalPrefix + '_' + campaignInputId;
	var formSelector = modalSelector + ' #campaign_tplContent_form_' + campaignInputId;
	var idSelector = formSelector + ' #marketingCampaignId';
	if (marketingCampaignId) {
		$(idSelector).val(marketingCampaignId);
	}
	$(modalSelector).modal("show");
}
function getCampaignIdForTemplate(campaignInputId, modalPrefix) {
	let tplContent = '';
	var modalSelector = '#' + modalPrefix + '_' + campaignInputId;
	var formSelector = modalSelector + ' #campaign_tplContent_form_' + campaignInputId;
	var idSelector = formSelector + ' #marketingCampaignId';
	let campaignIdForTemplate = $(idSelector).val();
	if (campaignIdForTemplate!="") {
		$.ajax({
			async: false,
			url: "/common-portal/control/findTemplatesAjax",
			type: "POST",
			data: { "marketingCampaignId": campaignIdForTemplate ,"externalLoginKey" : "${requestAttributes.externalLoginKey?if_exists}" },
			success: function(data) {
				if(data.data){
					for (let i = 0; i < data.data.length; i++) {
						let tpl = data.data[i] || {};
						if(tpl){
							let templateName = tpl.templateName || 'Default Template Name';
							let templateId = tpl.templateId || '';
							let previewImg = tpl.previewImg || '';
							let previewTemplateImg = tpl.previewTemplateImg;
							if(previewTemplateImg){
								previewImg = previewTemplateImg;
							}
							tplContent += '<div class="col-md-12 col-sm-12">' + '<div class="thumb-template-shadow" style="text-align:center;">' + ' <label class="image-checkbox" style="margin-bottom: 0px;">' + ' <input type="radio" name="' + campaignInputId + '-selectedTemplate" attr-tplName="' + templateName + '" value="' + templateId + '">' + ' <img src="' + previewImg + '" alt="Checked" class="template-img">' + ' </label>' + ' <div class="thumb-template-info">' + ' <div class="thumb-template-title">' + '  <div class="thumb-template-name">' + '  <h2 class="template-title"> ' + templateName + ' </h2>' + '  </div>' + ' </div>' + ' </div>' + '<div class="template-actions" style="display: flex; justify-content: center;">' + '<div class="button-more">' + '<a href="/campaign/control/getTemplate?templateId=' + templateId + '" target="_blank">View</a>' + '</div>' + '</div>' + '</div>' + '</div>';
						}
					}
				}
			}
		});
	}else{
		 tplContent = '<div class="col-md-12 col-sm-12"><div class="thumb-template-shadow" style="text-align:center;">No images available for preview.</div></div>';
	}
	$("#campaign_tplContent_" + campaignInputId).html(tplContent);
}
</script>
