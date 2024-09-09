<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<div id="notePopup" class="modal fade" role="dialog">
   <div class="modal-dialog modal-md">
      <!-- Modal content-->
      <div class="modal-content">
         <div class="modal-header">
            <h4 class="modal-title">Modal Header</h4>
            <button type="reset" class="close" data-dismiss="modal">&times;</button>
         </div>
         <div class="modal-body">
            <p>Some text in the modal.</p>
         </div>
         <div class="modal-footer">
            <button type="reset" class="btn btn-sm btn-primary navbar-dark" data-dismiss="modal">Close</button>
         </div>
      </div>
   </div>
</div>
<#assign partyId = requestParameters.partyId?if_exists>
<#assign notesList =delegator.findByAnd("PartyNoteView",{"targetPartyId":"${partyId?if_exists}","isImportant":"Y"},["-noteDateTime"],false)?if_exists>
<!-- Note Update -->
<div id="noteCreateUpdate" class="modal fade" role="dialog">
   <div class="modal-dialog modal-md">
      <!-- Modal content-->
      <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">${uiLabelMap.createNote!}</h4>
                <button type="reset" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body">
                <form method="post" action="<@ofbizUrl>createPartyNoteWebm1</@ofbizUrl>" id="AddPartyNoteWebm" class="form-horizontal" name="AddPartyNoteWebm" novalidate="novalidate" data-toggle="validator">
                    <input type="hidden" name="activeTab" value="opportunites" />
                    <input type="hidden" name="partyId" value="${partyId?if_exists}">
                    <input type="hidden" name="noteName">
                    <input type="hidden" name="noteId">
                    <input type="hidden" name="campaignNoteId" id="campaignNoteId">
                    <#assign requestURI = "viewContact"/>
                    <#if request.getRequestURI().contains("viewLead")>
                      <#assign requestURI = "viewLead"/>
                    <#elseif request.getRequestURI().contains("viewAccount")>
                      <#assign requestURI = "viewAccount"/>
                    </#if>
                    <input type="hidden" name="donePage" value='${requestURI?if_exists}'/>
                    <#assign enumerationProductsList = delegator.findByAnd("Enumeration", {"enumTypeId" : "MAIN_PRODUCT"}, Static["org.ofbiz.base.util.UtilMisc"].toList("sequenceId"), false)>
                   <#assign enumerationProductsList1 = Static["org.fio.crm.util.DataHelper"].getDropDownOptions(enumerationProductsList, "enumId", "description") />
                       <@dropdownCell
			         id="noteType"
			         label="Product"
			         options=enumerationProductsList1!
			         required=true
			         value=product?if_exists
			         allowEmpty=true
			         />
			         
			          <@dropdownCell
				         id="subProduct"
				         label="Sub Product"
				         required=true
				         value=subProduct?if_exists
				         allowEmpty=true
				         />
                    <div class="form-group row has-error">
                        <label  class="col-sm-4 col-form-label text-danger">Note*</label>
                        <div class="col-sm-7">
                            <textarea class="form-control" name="note" rows="3" id="AddPartyNoteWebm_note" placeholder="" required></textarea>
                            <div class="help-block with-errors"></div>
                        </div>
                    </div>
                     <div class="form-group row">
			            <label  class="col-sm-4 col-form-label has-error">Call back date</label>
			            <div class="col-sm-7">
			               <div class="input-group date" id="datetimepicker_callback">
			                  <input type='text' class="form-control input-sm" name="callBackDate" id="callBackDate" data-date-format="DD-MM-YYYY" value="<#if marketingCampaign?exists>${startTime?if_exists}<#else>${curTime?if_exists}</#if>" />
			                  <span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span>
			                  </span>
			               </div>
			               <div class="help-block with-errors" id="fromDate_error"></div>
			            </div>
			         </div>
                    <div class="form-group row">
                      <#if !notesList?has_content>
                        <label class="col-sm-3 col-form-label">${uiLabelMap.isImportant!}</label>
                            <div class="col-sm-7">
                                <input type="checkbox" id="isImportant" name="isImportant">
                            </div>
                        </#if> 
                    </div>
                </div>
            <div class="modal-footer">
               <@submit class="btn btn-sm btn-primary navbar-dark" label="${uiLabelMap.save}"/>
            </div>
         </div>
      </form>
   </div>
</div>
<div id="noteUpdate" class="modal fade" role="dialog">
   <div class="modal-dialog modal-md">
      <!-- Modal content-->
      <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">${uiLabelMap.editNote!}</h4>
                <button type="reset" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body">
                <form method="post" action="<@ofbizUrl>updateCustomerNote</@ofbizUrl>" id="AddPartyNoteWebm" class="form-horizontal" name="AddPartyNoteWebm" novalidate="novalidate" data-toggle="validator">
                <input type="hidden" name="activeTab" value="details" />
                <input type="hidden" name="partyId" value="${partyId?if_exists}">
                <input type="hidden" name="noteName">
                <#assign requestURI = "viewContact"/>
                <#if request.getRequestURI().contains("viewLead")>
                  <#assign requestURI = "viewLead"/>
                <#elseif request.getRequestURI().contains("viewAccount")>
                  <#assign requestURI = "viewAccount"/>
                </#if>
                <input type="hidden" name="donePage" value='${requestURI?if_exists}'/>
                <#if notesList?has_content>
                     <div class="form-group row has-error">
                         <label  class="col-sm-3 col-form-label text-danger">Note</label>
                         <div class="col-sm-7">
                             <#if notesList?has_content>
                                 <#list notesList as note>
                                     <#assign noteInfo ="${note.noteInfo?if_exists}"/>
                                     <input type="hidden" name="noteId" value="${note.noteId?if_exists}">
                                 </#list>
                             </#if>
                             <textarea class="form-control" name="note" rows="3" id="AddPartyNoteWebm_note" placeholder="" required>${noteInfo?if_exists}</textarea>
                             <div class="help-block with-errors"></div>
                        </div>
                    </div>
                    <div class="form-group row">
                    <label class="col-sm-3 col-form-label">${uiLabelMap.isImportant!}</label>
                        <div class="col-sm-7">
                          <#if notesList?has_content>
                              <input type="checkbox" id="isImportant" name="isImportant" value="Y" checked disabled>
                          </#if> 
                        </div>
                    </div>
                </#if>
            </div>
            <div class="modal-footer">
               <@submit class="btn btn-sm btn-primary navbar-dark" label="${uiLabelMap.save!}"/>
            </div>
         </div>
      </form>
   </div>
</div>

<script>

 $(document).ready(function() {
 $("#datetimepicker_callback").datetimepicker({
	    				minDate: new Date()
	    			});
 
 
      // getSubProducts("${enumerationProductsList1?if_exists}", "${subProduct?if_exists}");
       $("#noteType").change(function() {
       //alert("Trigger called");
           getSubProducts($(this).val(), "${subProduct?if_exists}");
       });
   
       function getSubProducts(product, subProduct) {
           var productId = product;
           $("#subProduct").empty();
           var list = $("#subProduct");
           list.append("<option value='' class='nonselect'>Please Select</option>");
           if (productId != null && productId != "") {
               $("#subProduct").attr("required",true);
               $.ajax({
                   type: 'POST',
                   async: false,
                   url: "getSubProductsDataJSON",
                   data: {
                       "productId": productId
                   },
                   success: function(subProducts) {
                       if (subProducts.length == 0) {
                           list.append("<option value = ''>N/A</option>");
                       } else {
                           for (var i = 0; i < subProducts.length; i++) {
                               if (subProduct != null && subProduct != "" && subProducts[i].enumId == subProduct) {
                                   list.append("<option  value =" + subProducts[i].enumId + " selected>" + subProducts[i].description + " </option>");
                               } else {
                                   list.append("<option  value =" + subProducts[i].enumId + ">" + subProducts[i].description + "</option>");
                               }
   
                           }
                       }
                   }
               });
           } else {
              $("#subProduct").attr("required",false);
           }
           $('#subProduct').append(list);
           $('#subProduct').dropdown('refresh');
       }
   });
</script>