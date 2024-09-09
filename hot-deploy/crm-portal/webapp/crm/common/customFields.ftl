<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<form name="CustomFieldform" method="post" action="<@ofbizUrl>createUpdateCustom</@ofbizUrl>" onsubmit="">
   <input type="hidden" name="activeTab" value="customFields" />    
   <div class="border-b mt-2">
      <h2 class="d-inline-block">Attributes</h2>
      <#if groupList?has_content && groupList?size!=0>
      <div class="float-right">
         <input type="submit" value="Update" class="btn btn-xs btn-primary mt-2" / >
      </div>
      </#if>
   </div>
   <#assign requestURI = "viewContact"/>
   <#if request.getRequestURI().contains("viewLead")>
   <#assign requestURI = "viewLead"/>
   <#elseif request.getRequestURI().contains("viewAccount")>
   <#assign requestURI = "viewAccount"/>
   </#if>
   <input type="hidden" name="donePage" value='${requestURI?if_exists}'/>
   <input type="hidden" name="partyId" value="${partyId?if_exists}"/>
   <#if groupList?has_content && groupList?size!=0>
   <div class="panel-group" id="subaccordionMenu1" role="tablist" aria-multiselectable="true">
      <div class="row">
         <#assign count = 0>
         <#assign i = 0>
         <#list groupList as groupList>
         <div class="col-md-4 col-sm-4 mt-2">
            <div class="panel panel-default">
               <div class="panel-heading" role="tab" id="subaccordion_o_${count}">
                  <h4 class="panel-title">
                     <a class="collapsed" role="button" data-toggle="collapse" data-parent="#subaccordionMenu1" href="#acc1_o_${count}" aria-expanded="false" aria-controls="collapseTwo">
                        ${groupList?if_exists}
                     </a>
                  </h4>
               </div>
               <div id="acc1_o_${count}" class="panel-collapse collapse" role="tabpanel" aria-labelledby="head1" data-parent="#subaccordionMenu1">
                  <#-- <#if templatePartyAttributes?has_content && templatePartyAttributes?size!=0>
                  <#list templatePartyAttributes as configValue> -->
                  <#assign customFieldLi = delegator.findByAnd("CustomField", {"groupName":"${groupList?if_exists}"}  ,Static["org.ofbiz.base.util.UtilMisc"].toList("sequenceNumber ASC"), false)?if_exists />
                  <#if customFieldLi?has_content && customFieldLi?size!=0>
                      <#list customFieldLi as customField>
                          <#assign customFieldRoleConfigLi = delegator.findByAnd("CustomFieldRoleConfig", {"customFieldId":"${customField.customFieldId?if_exists}", "roleTypeId", "${roleType?if_exists}"}  , [], false)?if_exists />
                          <#-- <#assign i =customField.customFieldId!> -->
                          <#if customField.hide?if_exists != "Y" && customFieldRoleConfigLi?exists && customFieldRoleConfigLi?has_content && customFieldRoleConfigLi?size != 0>
                              <div class="panel-body">
                              <input type="hidden" name="partyId_o_${i}" id="partyId_o_${i}" value="${partyId?if_exists}">
                              <input type="hidden" name="customFieldId_o_${i}" id="customFieldId_o_${i}" value="${customField.customFieldId?if_exists}">
                                 <#assign fieldValue = delegator.findOne("CustomFieldValue",{"customFieldId":customField.customFieldId?if_exists,"partyId":partyId?if_exists},false)?if_exists />
                                 <#if !fieldValue?has_content>
                                    <input name="action_o_${i}" type="hidden" value="CREATE"/>
                                 <#else>
                                    <input name="action_o_${i}" type="hidden" value="UPDATE"/>
                                 </#if>
                                 <#if customField.customFieldFormat?has_content && customField.customFieldFormat?if_exists="TEXT">
                                 	<@inputRow    
                                        id = "customFieldValue_o_${i}"
                                 		label = customField.customFieldName?if_exists
                                        value= fieldValue?if_exists.fieldValue?if_exists
                                        required=false
                                       />
                                 <#elseif customField.customFieldFormat?has_content && customField.customFieldFormat?if_exists="DATE">
                                     <@inputDate 
										id="customFieldValue_o_${i}"
	                                    label=customField.customFieldName?if_exists
	                                    placeholder="YYYY-MM-DD"
										value= fieldValue?if_exists.fieldValue?if_exists
										required=false
										/>
                                 <#elseif customField.customFieldFormat?has_content && customField.customFieldFormat?if_exists="TEXT_AREA">
                                     <@inputArea
							        	id="customFieldValue_o_${i}"
                                     	label=customField.customFieldName?if_exists
                                     	value= fieldValue?if_exists.fieldValue?if_exists
								        inputColSize="col-sm-7"
								        labelColSize="col-sm-4"
								        rows="3"
								        required=false
							        />  
                                 <#elseif customField.customFieldFormat?has_content && customField.customFieldFormat?if_exists="CHECK_BOX">
                                     <#assign checkBoxValue = delegator.findByAnd("CustomFieldMultiValue",{"customFieldId":customField.customFieldId?if_exists},[],true)?if_exists />
                                     <#if checkBoxValue?has_content>
                                         <label class="form-check-label">${customField.customFieldName}</label>
                                         <#assign checkedValue = fieldValue?if_exists.fieldValue?if_exists />
                                         <#list checkBoxValue as checkBoxValue> 
                                         <#if checkBoxValue.hide?if_exists != "Y"> 
                                         <div class="form-check-inline">
                                            <label class="form-check-label"> 
                                            <input type="checkbox" class="form-check-input"name="customFieldValue_o_${i}" id="customFieldValue_o_${i}" 
                                            value="${checkBoxValue.fieldValue}" <#if checkedValue == "${checkBoxValue.fieldValue}">checked</#if> >
                                            ${checkBoxValue.description}
                                            </label>
                                         </div>
                                         </#if>
                                         </#list>
                                     </#if>
                                 <#elseif customField.customFieldFormat?has_content && customField.customFieldFormat?if_exists="RADIO">
                                     <#assign radioButtonValue = delegator.findByAnd("CustomFieldMultiValue",{"customFieldId":customField.customFieldId?if_exists},[],true)?if_exists />
                                     <#if radioButtonValue?has_content>
                                         <label class="form-check-label">${customField.customFieldName}</label>
                                         <#assign radioValue = fieldValue?if_exists.fieldValue?if_exists />
                                         <#list radioButtonValue as radioButtonVal>
                                             <#if radioButtonVal.hide?if_exists != "Y"> 
                                             <div class="form-check-inline">
                                                <label class="form-check-label"> 
                                                <input type="radio" class="form-check-input"name="customFieldValue_o_${i}" id="customFieldValue_o_${i}" 
                                                value="${checkBoxValue?if_exists.fieldValue?if_exists}" <#if radioValue == "${radioButtonVal?if_exists.fieldValue?if_exists}">checked</#if> >
                                                ${radioButtonVal.description}
                                                </label>
                                             </div>
                                         </#if>
                                         </#list>
                                     </#if>
                                 <#elseif customField.customFieldFormat?has_content && customField.customFieldFormat?if_exists="DROP_DOWN">
                                     <#assign dropDownValue = delegator.findByAnd("CustomFieldMultiValue",{"customFieldId":customField.customFieldId?if_exists},[],true)?if_exists />
                                     <div class="form-group row row">
                                        <label  class="col-sm-4 col-form-label">${customField.customFieldName?if_exists}</label>
                                        <div class="col-sm-7">
                                           <select name="customFieldValue_o_${i}" id="customFieldValue_o_${i}" class="ui dropdown search form-control input-sm" >
                                              <option value="">
                                                 <div class="text-muted">Please Select</div>
                                              </option>
                                              <#if dropDownValue?has_content>
                                                  <#list dropDownValue as classification>
                                                      <#if classification.hide?if_exists != "Y">
                                                         <option value="${classification.multiValueId}" <#if fieldValue.fieldValue?if_exists = classification.multiValueId>selected<#elseif classification?if_exists = classification.multiValueId>selected</#if>>${classification.description}</option>
                                                      </#if>   
                                                  </#list>
                                              </#if>
                                           </select>
                                        </div>
                                     </div>
                                 <#elseif customField.customFieldFormat?has_content && customField.customFieldFormat?if_exists="NUMERIC">
                                     <@inputRow    
                                    	id="customFieldValue_o_${i}"
	                                    label=customField.customFieldName?if_exists
	                                    value=fieldValue.fieldValue
	                                    inputType="number"
	                                    required=false
	                                    min=1
                                       />
                                 <#-- <#elseif customField.customFieldFormat?has_content && customField.customFieldFormat="BOOLEAN"> -->
                                 <#elseif customField.customFieldFormat?has_content && customField.customFieldFormat?if_exists="LABEL_TEXT">
                                     <div class="form-group row">
                                        <label class="col-sm-4 col-form-label">${customField.customFieldName?if_exists}</label>
                                        <div class="col-sm-7">
                                           <label class="col-form-label input-sm">${fieldValue.fieldValue?if_exists}</label>
                                        </div>
                                     </div>
                                 </#if>
                              </div>
                          </#if>
                          <#assign i=i+1>
                      </#list>
                  </#if>
                  <#--</#list>
                  </#if>-->
               </div>
            </div>
         </div>
         <#assign count = count+i> 
         </#list>
      </div>
   </div>
   </#if>
</form>
<script type="text/javascript">
/*
$(document).ready(function(){
    $('a[data-toggle="tab"]').on('show.bs.tab', function(e) {
        localStorage.setItem('activeTab', $(e.target).attr('href'));
    });
    var activeTab = localStorage.getItem('activeTab');
    if(activeTab){
        $('.nav-tabs a[href="' + activeTab + '"]').tab('show');
    } else{
    	$('.nav-tabs a[href="#Details"]').tab('show');
    }
});
*/
</script>