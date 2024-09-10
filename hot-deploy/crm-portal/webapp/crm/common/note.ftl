<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<@pageSectionHeader title="Notes" 
	extra='<div class="">
      <span class="btn btn-xs btn-primary m5" data-toggle="modal" 
      	data-target="#noteCreateUpdate">${uiLabelMap.createNew!}</span>
   </div>' />
<div class="clearfix"></div>
<@inputHidden id="noteListData" value=partyNotesListStr />
<div class="table-responsive">				
	<div id="noteGrid" style="width: 100%;" class="ag-theme-balham"></div>   			
</div>
<script type="text/javascript" src="/crm-resource/js/ag-grid/opportunity/note.js"></script>

<#assign requestURI = "viewContact"/>
<#if request.getRequestURI().contains("viewLead")>
<#assign requestURI = "viewLead"/>
<#elseif request.getRequestURI().contains("viewAccount")>
<#assign requestURI = "viewAccount"/>
<#elseif request.getRequestURI().contains("viewCustomer")>
<#assign requestURI = "viewCustomer"/>
</#if>
<#if partyNotesList?has_content>
<#assign count = 0>
<#list partyNotesList as notes>
<#assign count = count+1>
<form name="deleteNotes${notes.noteId}" method="post" action="deleteNote" class="row">
   <input type="hidden" name="activeTab" value="opportunites" />
   <input type="hidden" name="donePage" value='${requestURI?if_exists}'/>
   <input type="hidden" name="targetPartyId" value="${notes.targetPartyId?if_exists}"/>
   <input type="hidden" name="partyId" value="${notePartyId?if_exists}">
   <input type="hidden" name="noteId" value="${notes.noteId?if_exists}"/>
</form>

<div id="editNotes_${notes.noteId}" class="modal fade" role="dialog">
   <div class="modal-dialog modal-md">
      <!-- Modal content-->
      <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">${uiLabelMap.editNote!}</h4>
                <button type="reset" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body">
                <form method="post" action="<@ofbizUrl>updateCustomerNote</@ofbizUrl>" id="AddPartyNoteWebm_${count!}" class="form-horizontal" name="AddPartyNoteWebm" novalidate="novalidate" data-toggle="validator">
                <input type="hidden" id="activeTab_${count!}"  name="activeTab" value="opportunites" />
                <input type="hidden" id="partyId_${count!}" name="partyId" value="${notePartyId?if_exists}">
                <input type="hidden" id="noteName_${count!}" name="noteName">
                <input type="hidden" id="donePage_${count!}" name="donePage" value='${requestURI?if_exists}'/>
                <input type="hidden" id="noteId_${count!}" name="noteId" value="${notes.noteId?if_exists}">
                <#assign noteDatas = delegator.findOne("NoteData", {"noteId" : notes.noteId}, true)>
                 <#assign enumerationProductsList = delegator.findByAnd("Enumeration", {"enumTypeId" : "MAIN_PRODUCT"}, Static["org.ofbiz.base.util.UtilMisc"].toList("sequenceId"), false)>
                   <#assign enumerationProductsList1 = Static["org.fio.crm.util.DataHelper"].getDropDownOptions(enumerationProductsList, "enumId", "description") />
                       <@dropdownCell 
				         id="noteType_${count!}"
				         label="Product"
				         options=enumerationProductsList1!
				         required=true
				         value=noteDatas.noteType?if_exists
				         allowEmpty=true
				         onchange="getSubProducts('${noteDatas.noteType!}','','${count!}');"
			         />
			          <@dropdownCell
				         id="subProduct_${count!}"
				         label="Sub Product"
				         required=true
				         value=noteDatas.subProduct?if_exists
				         allowEmpty=true
				         />
				         
			         
                     <div class="form-group row has-error">
                         <label  class="col-sm-4 col-form-label text-danger">Note</label>
                         <div class="col-sm-7">
                            <textarea class="form-control" name="note" rows="3" id="AddPartyNoteWebm_note_${count!}" placeholder="" required>${notes.noteInfo?if_exists}</textarea>
                            <div class="help-block with-errors"></div>
                        </div>
                    </div>
                    
                     <div class="form-group row">
			            <label  class="col-sm-4 col-form-label has-error">Call back date</label>
			            <div class="col-sm-7">
			               <div class="input-group date" id="datetimepicker_${count!}">
			                  <input type='text' class="form-control input-sm" name="callBackDate" id="callBackDate_${count!}" data-date-format="DD-MM-YYYY" value="<#if noteDatas.callBackDate?has_content>${noteDatas.callBackDate?string["dd-MM-yyyy"]}</#if>" />
			                  <span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span>
			                  </span>
			               </div>
			               <div class="help-block with-errors" id="fromDate_error"></div>
			            </div>
			         </div>
			         
			         
                    <#if notes.isImportant?has_content && notes.isImportant=="Y">
                    <div class="form-group row">
                    <label class="col-sm-3 col-form-label">${uiLabelMap.isImportant!}</label>
                        <div class="col-sm-7">
                              <input type="checkbox" id="isImportant" name="isImportant"  id="isImportant" value="Y" checked disabled>
                        </div>
                    </div>
                    </#if>
            </div>
            <div class="modal-footer">
               <Input type="button" class="btn btn-sm btn-primary navbar-dark" value="${uiLabelMap.save}" onclick="submitForm('${count!}');"/>
            </div>
         </div>
      </form>
   </div>
</div>
</#list>
</#if>
<!-- /.container -->
<#include "component://crm/webapp/crm/common/modalNoteCreate.ftl">

<script type="text/javascript" >
		function submitForm(count)
         {
         	var actvityTab = $("#activeTab_"+count).val();
         	var partyId = $("#partyId_"+count).val();
         	var noteName = $("#noteName_"+count).val();
         	var donePage = $("#donePage_"+count).val();
         	var noteId = $("#noteId_"+count).val();
         	
         	var noteType = $("#noteType_"+count).val();
         	var subProduct = $("#subProduct_"+count).val();
         	var AddPartyNoteWebm_note = $("#AddPartyNoteWebm_note_"+count).val();
         	var callBackDate = $("#callBackDate_"+count).val();
         	var isImportant = $("#isImportant_"+count).val();
         	
         	
         	$("#TmpactiveTab").val(actvityTab);
         	$("#TmppartyId").val(partyId);
         	$("#TmpnoteName").val(noteName);
         	$("#TmpdonePage").val(donePage);
         	$("#TmpnoteId").val(noteId);
         	
         	$("#TmpnoteType").val(noteType);
         	$("#TmpsubProduct").val(subProduct);
         	$("#TmpAddPartyNoteWebm_note").val(AddPartyNoteWebm_note);
         	$("#TmpcallBackDates").val(callBackDate);
         	$("#TmpisImportant").val(isImportant);
         	
         	//alert(noteType+"----"+callBackDate);
         	$("#tempForm").submit();
         }  
         function getSubProducts(product, subProduct,cnt) {
         //alert("Data going to filter");
           var productId = product;
           if(productId=="")
           {
           		productId = $("#noteType_"+cnt).val();
           }
           
           $("#subProduct_"+cnt).empty();
           var list = $("#subProduct_"+cnt);
           list.append("<option value='' class='nonselect'>Please Select</option>");
           if (productId != null && productId != "") {
               $("#subProduct_"+cnt).attr("required",true);
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
              $("#subProduct_"+cnt).attr("required",false);
           }
           $('#subProduct_'+cnt).append(list);
           $('#subProduct_'+cnt).dropdown('refresh');
       }
  $(function(){
     $(document).ready(function() {
    	 //alert("${count!}");
	     var i =0;
	     var today = new Date();
		<#list partyNotesList as notes>
		<#assign noteDatas = delegator.findOne("NoteData", {"noteId" : notes.noteId}, true)>
			    	 i = i+1;
			     	$("#datetimepicker_"+i).datetimepicker({
	    				minDate: today
	    			});
			     	var notType = $("#noteType_"+i).val();
			     	//lets trigger the sub product filters too
			     	getSubProducts(notType,'${noteDatas.subProduct!}',i);
			     	
		</#list>	     
         /*$("#noteTable").DataTable({
         	destroy: true,
            "order": [[ 0, "desc" ]]
         });*/
         
            
       
     } );
 });    
  </script>
  
  <form name="tempForm" id="tempForm" action="<@ofbizUrl>updateCustomerNote</@ofbizUrl>">
  	 			<input type="hidden"  id="TmpactiveTab" name="activeTab"/>
                <input type="hidden"  id="TmppartyId" name="partyId" />
                <input type="hidden"  id="TmpnoteName" name="noteName" />
                <input type="hidden"  id="TmpdonePage" name="donePage"/>
                <input type="hidden"  id="TmpnoteId" name="noteId" />
                
                <input type="hidden"  id="TmpnoteType" name="noteType"/>
                <input type="hidden"  id="TmpsubProduct" name="subProduct" />
                <input type="hidden"  id="TmpAddPartyNoteWebm_note" name="note" />
                <input type="hidden"  id="TmpcallBackDates" name="callBackDate"/>
                <input type="hidden"  id="TmpisImportant" name="isImportant" />
                
  </form>
