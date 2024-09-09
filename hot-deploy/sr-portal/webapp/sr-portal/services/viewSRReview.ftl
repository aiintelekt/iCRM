<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
   <#--<script type="text/javascript">
    $(document).ready(function() {
         document.getElementById('save').style.cssText="display:none";
	      $("#clear").hide();
	   getSrReviewDetails();
	   var accessLevel = document.getElementById('accessLevel').value;
	   //alert("accessLevel000000000000_______"+accessLevel);
	   if(accessLevel==undefined ||accessLevel==null||accessLevel==""||!(accessLevel=="A"||accessLevel=="Y")){
	       $("#save").hide();
	       document.getElementById('save').style.cssText="display:none";
	       $("#clear").hide();
	   }else{
	  	 document.getElementById('save').style.cssText="display:block";
	      $("#clear").show();
	   }
	});
	function getSrReviewDetails() {
	    var result = null;
		var srNumberUrlParam = document.getElementById('srNumberUrlParam').value;
		if(srNumberUrlParam !=null && srNumberUrlParam != "" && srNumberUrlParam != 'undefined'){
		    $.ajax({
		        type: "POST",
		        url: "getSrReviewDetails",
		        async: false,
		        data: {"srNumber": srNumberUrlParam},
		        success: function(data) {
		            result=data[0];
		            $.each(result, function(name, val) {
			            var $el = $('[name="' + name + '"]'),
			                type = $el.attr('type');
			                 //console.log("name----------"+name+type);
			             switch (type) {
				            case 'select':
				            	if("srTypeId" == name){
				                	$el.filter('[value="' + val + '"]').attr('disabled', true);
				                }else if("srCategoryId" == name){
				                	$el.filter('[value="' + val + '"]').attr('disabled', true);
				                }else if("srSubCategoryId" == name){
				                	$el.filter('[value="' + val + '"]').attr('disabled', true);
				                }else{
				                	$el.filter('[value="' + val + '"]');
				                }
				                break;
				            case 'checkbox':
				                $el.attr('checked', 'checked');
				                break;
				            case 'radio':
				                $el.filter('[value="' + val + '"]').attr('checked', 'checked');
				                break;
				            default:
				                $el.val(val);
			            }
			        });
		        },error: function(data) {
		        	result=data;
					console.log('Error occured');
					showAlert("error", "Error occured while fetching Review Data!");
				}
		    });
	    } 
	}
    
   </script>-->
  
<div class="page-header border-b pt-2">
	<@headerH2
		title="SR Review"
    />
</div>
<div class="clearfix"></div>
<form action="saveSrReview" method="post" id="ServiceReview" name="ServiceReview">
	<#assign srNumberUrlParam = requestParameters.srNumber!>
 	<input type="hidden" name="srNumber" id="srNumber" value="${srNumberUrlParam!}" />
	<input type="hidden" name="srNumberUrlParam" id="srNumberUrlParam" value="${srNumberUrlParam!}" /> 
	<input type="hidden" name="accessLevel" id="accessLevel" value="${accessLevel!}" />
	<#assign custRequestSrSummary = EntityQuery.use(delegator).from("CustRequestSrSummary").where("custRequestId", parameters.srNumber).queryOne()/>
	<div class="row">
		<div class="col-md-12 col-lg-6 col-sm-12 ">
			<#assign srTypes = delegator.findByAnd("CustRequestAssoc", {"type" : "SRTYPE", "active","Y"}, null, false)>
   			<#assign srTypeList = Static["org.fio.admin.portal.util.DataHelper"].getDropDownOptions(srTypes, "code","value")?if_exists />
  			<@dropdownCell
     			id="srTypeId1"
		        name = "srTypeId"
		       	label=uiLabelMap.SRType
		        placeholder="Please Select"
		        options=srTypeList!
				required=true
     		/>
     		<#assign srCategories = delegator.findByAnd("CustRequestAssoc", {"type" : "SRCategory"}, null, false)>
	 		<#assign srCategoryList = Static["org.fio.admin.portal.util.DataHelper"].getDropDownOptions(srCategories, "code","value")?if_exists />
     		<@dropdownCell
     			id="srCategory"
     			name="srCategoryId"
     			label=uiLabelMap.SRCategory
     			placeholder="Please Select"
     			options=srCategoryList!
	  			allowEmpty=true        
	  			required=true
     		/>
     		<#assign srSubCategories = delegator.findByAnd("CustRequestAssoc", {"type" : "SRSubCategory"}, null, false)>
	 		<#assign srSubCategoryList = Static["org.fio.admin.portal.util.DataHelper"].getDropDownOptions(srSubCategories, "code","value")?if_exists />
       		<@dropdownCell 
       			id="srSubCategory"
        		required=true
        		name="SRSubCategory"
        		label=uiLabelMap.SRSubCategory
       			allowEmpty=true        
       			placeholder = "Please Select "
       			options=srSubCategoryList!
       		/>	
      		<@inputRow 
     			id="otherSrSubCategory"
     			label=uiLabelMap.OtherSubCategory
     			placeholder=uiLabelMap.OtherSubCategory
     		/>
     		<#assign srPriorities = delegator.findByAnd("Enumeration", {"enumTypeId" : "PRIORITY_LEVEL","enumService","ServiceRequest","enumEntity","ServiceRequest"}, null, false)>
     		<#assign srPrioritiesList = Static["org.fio.admin.portal.util.DataHelper"].getDropDownOptions(srPriorities, "enumId","description")?if_exists />
       		<@dropdownCell 
       			id="srPriority"
        		name="priority"
       			label=uiLabelMap.Priority
       			options=srPrioritiesList!
       			allowEmpty=true
       			placeholder = "Please Select"
       			required=true
       		/>
      		<div class="form-group row priority" style="">
				<label class="col-sm-4 field-text">${uiLabelMap.tatCalculation}</label>
        		<div class="col-sm-7">
           			<div class="form-check form-check-inline">
          				<input class="form-check-input" name="inlineRadioOptions" id="inlineRadio1" value="option1" type="radio">
          				<label class="form-check-label" for="inlineRadio1">On</label>
        			</div>
        			<div class="form-check form-check-inline">
          				<input class="form-check-input" name="inlineRadioOptions" id="inlineRadio2" value="option2" type="radio">
          				<label class="form-check-label" for="inlineRadio2">Off</label>
        			</div>
    			</div>
    		</div> 	
        	<@dropdownCell
            	id="reviewedBy" 
            	label=uiLabelMap.reviewedBy 
            	placeholder="Reviewed By"      
            	allowEmpty=true        
            	value="${requestParameters.reviewedBy?if_exists}"     
        	/>         
		</div>
        <div class="col-md-12 col-lg-6 col-sm-12 ">
			<#assign srTypes = delegator.findByAnd("CustRequestAssoc", {"type" : "SRTYPE", "active","Y"}, null, false)>
   			<#assign srTypeList = Static["org.fio.admin.portal.util.DataHelper"].getDropDownOptions(srTypes, "code","value")?if_exists />
  			<@dropdownCell
     			id="origsrTypeId"
     			label=uiLabelMap.OriginalSRType
     			placeholder="Please Select"
     			options=srTypeList!
	 			value="${custRequestSrSummary.srTypeId?if_exists}"
	 			disabled=true
     		/>
     		<#assign srCategories = delegator.findByAnd("CustRequestAssoc", {"type" : "SRCategory"}, null, false)>
	 		<#assign srCategoryList = Static["org.fio.admin.portal.util.DataHelper"].getDropDownOptions(srCategories, "code","value")?if_exists />
     		<@dropdownCell
     			id="origsrCategoryId"
     			label=uiLabelMap.OriginalSRCategory
     			placeholder="Please Select"
	  			allowEmpty=true   
	   			options=srCategoryList!     
	 			value="${custRequestSrSummary.srCategoryId?if_exists}"
	  			disabled=true  
     		/>
       		<@dropdownCell 
       			id="origsrSubCategoryId"
        		label=uiLabelMap.OriginalSubCategory
       			allowEmpty=true    
       			options=srSubCategoryList!    
       			value="${custRequestSrSummary.srSubCategoryId?if_exists}"
       			placeholder = "Please Select "
       			disabled=true
      		 />	
      		<@inputRow 
     			id="origotherSrSubCategory"
     			label=uiLabelMap.OriginalOtherSubCategory
     			placeholder=uiLabelMap.OtherSubCategory
     			value="${custRequestSrSummary.otherSrSubCategory?if_exists}"
     			disabled=true
     		/>
       		<@dropdownCell 
       			id="origpriority"
       			label=uiLabelMap.OriginalPriority
       			options=srPrioritiesList!
       			allowEmpty=true
       			value="${(custRequestSrSummary.priority)!}"
      			placeholder = "Please Select"
      			disabled=true
       		/>
        	<#assign custRequest = EntityQuery.use(delegator).select("tatDays").from("CustRequest").where("custRequestId", parameters.srNumber).queryOne()! />
       		<@inputRow 
     			id="tatDays"
     			label=uiLabelMap.ManualTATDAYS
     			value="${custRequest.tatDays?if_exists}"
     			disabled=true
     		/>	
			<@inputDate
        		id="reviewDate"
        		type="date"
        		placeholder="Reviewed Date"
        		minDate=0!
        		label=uiLabelMap.ReviewedOn
            />
        </div>
       	<div class="form-group row col-sm-7 offset-md-2">
    		<input type="submit" id="save" name="" class="btn btn-sm btn-primary" value="Save">&nbsp;
 			<input type="reset" id="clear" class="btn btn-sm btn-secondary" value="Clear">
		</div>  
	</div>
</form>

<script>
    function validate() {
        var srTypeId = jQuery("#srTypeId1").find(":selected").val();
        var srCategory = jQuery("#srCategory").find(":selected").val();
        var subCategory = jQuery("#srSubCategory").find(":selected").val();
        var priority = jQuery("#srPriority").find(":selected").val();

        if (!srTypeId || !srCategory || !subCategory || !priority) {
            return false;
        }
    }
</script>
                    