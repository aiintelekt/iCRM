<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<script type="text/javascript" src="https://ajax.aspnetcdn.com/ajax/jquery.validate/1.10.0/jquery.validate.min.js"></script>
<style>
.text-size-control
{
text-overflow: ellipsis;
white-space: nowrap;
overflow: hidden;
width:300px;
}
.table td, .table th {
    border-top: 2px solid #dee2e6 !important;
}
.top-band{
z-index:0;
}
.modal-body{
padding: 1.5rem !important;
}
.input-sm
{
height:44px !important;
}
</style>
<div class="row">
    <div id="main" role="main">
       <div class="col-md-12 col-lg-12 col-sm-12 dash-panel" >
        <@sectionFrameHeader title="${uiLabelMap.Find} ${uiLabelMap.GlobalConfiguration}"  />
        <div class="panel-group" id="accordionMenu" role="tablist" aria-multiselectable="true">
            <div class="panel panel-default">
                <div class="panel-heading" role="tab" id="headingTwo">
                    <h4 class="panel-title">
                        <a role="button" data-toggle="collapse" data-parent="#accordionMenu"
                            href="#accordionDynaBase" aria-expanded="true"
                            aria-controls="collapseOne"> ${uiLabelMap.MainFilter} </a>
                    </h4>
                </div>
                <div>
                    <div>
                        <div id="accordionDynaBase" class="panel-collapse collapse show" role="tabpanel" aria-labelledby="headingOne">
                            <form method="post" class="form-horizontal table-responsive" data-toggle="validator" id="search" name="findSystemPropertyForm">
                                <div class="margin-adj-accordian">
                                    <div class="row p-3f" >
                                    <div class="col-lg-12 col-md-12 col-sm-12 search-left">
                                     <@dynaScreen 
									 instanceId="FIND_GLOBAL_CONFIGURATIONS"
									 modeOfAction="CREATE"
									/>
								    </div>
                                        <div class="col-11 p-1">
                                            <div class="float-right p-1">       
                                                <button class="btn btn-sm btn-primary" type="submit" id="">Find</button>
                                                <@reset label="Reset" onclick="javascript:clearFields();" />
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
        <div class="col-sm-12 col-md-12 col-lg-12 dash-panel">
            <#assign extra = '<span id="attr-collapseAll-btn" title="" class="btn btn-xs btn-primary"><i class="fa fa-arrow-circle-down" aria-hidden="true"></i> <span>Expand All</span> </span>'/>
            <#assign extra= extra + '<a href="" data-toggle="modal" data-target="#addparameter" class="btn btn-xs btn-primary"><i class="fa fa-plus" aria-hidden="true"></i> Add</a>' />
            <@sectionFrameHeader title="${uiLabelMap.GlobalConfiguration} List " extra=extra />
            <div id="custom-field-accordion">
                <#assign count = 0>
                <#list sectionDetails as sectionList>
                <div class="card attr-detail">
                    <div class="card-header pt-1 pb-1">
                        <a role="button" class="card-link <#if count != 0>collapsed</#if>" data-toggle="collapse" href="#acc1_o_${count}" aria-expanded="true">
                        ${sectionList.description?if_exists}
                        </a>	 
                    </div>
                    <div id="acc1_o_${count}" class="card-collapse collapse attr-collapse <#if count == 0> show </#if>" data-parent="#custom-field-accordion" style="">
                        <div class="card-body">
                            <div class="row">
                                <div class="col-md-12 col-lg-12 col-sm-12 ">
                                      <div class="table-responsive">
                                        <table class="table table-hover table-striped">
                                            <thead>
                                                <tr>
                                                    <th width="20%">Parameter ID</th>
                                                    <th width="30%">Parameter Name</th>
                                                    <th width="35%">Parameter Value</th>
                                                    <th width="10%"></th>
                                                    <th width="5%"></th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <#assign parameterList = delegator.findByAnd("PretailLoyaltyGlobalParameters", {"storeId" : "${sectionList.enumId!}", "isActive": "Y"}, ["-lastUpdatedTxStamp"], false)>
                                                <#list parameterList as paramList>
                                                <tr>
                                                    <td>${paramList.parameterId!}</td>
                                                    <td>${paramList.description!}</td>
                                                    <td title="${paramList.value!}"><label class="edit-text text-size-control">${paramList.value!}</label> <input type="text" name = "${sectionList.enumId!}_o_${paramList_index}" class="workingHr" id = "${sectionList.enumId!}_o_${paramList_index}" class="form-control" style="display:none;"></td>
                                                    <td><a class="btn btn-xs btn-primary" onclick="edit(this);" title="Edit"><i class="fa fa-edit" aria-hidden="true"></i> <a class="btn btn-xs btn-success" onclick="save('${paramList.parameterId?if_exists}',${paramList_index?if_exists},'${sectionList.enumId!}');" title="Submit"><i class="fa fa-check" aria-hidden="true"></i> </a> </td>
                                                    <td><a onclick="addOrViewComment('${paramList.parameterId?if_exists}');" style="background-color: #02829d !important; border-color: #02829d47 !important; color: #ffffff !important;" class="btn btn-xs btn-primary" title="Comments" ><i class="fa fa-comment" aria-hidden="true"></i> Comments</a></td>
                                                </tr>
                                                </#list> 
                                            </tbody>
                                        </table>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <#assign count = count+1>
                </#list>
            </div>
        </div>
            <#-- <div class="col-sm-12 col-md-12 col-lg-12 dash-panel" id="afterFilter">
            <#assign extra = '<span id="attr-collapseAll-btn" title="" class="btn btn-xs btn-primary"><i class="fa fa-arrow-circle-down" aria-hidden="true"></i> <span>Expand All</span> </span>'/>
            <#assign extra= extra + '<a href="" data-toggle="modal" data-target="#addparameter" class="btn btn-xs btn-primary"><i class="fa fa-plus" aria-hidden="true"></i> Add</a>' />
            <@sectionFrameHeader title="${uiLabelMap.GlobalConfiguration!}" extra=extra />
            <div id="custom-field-accordion">
                <#assign count = 0>
                <#assign count = count+1>
            </div>
        </div>-->
    </div>
    </div>
</div>
<form action ="<@ofbizUrl>updateParameter</@ofbizUrl>" name="updateParameterForm" method="post">
   <input type ="hidden" name ="paramId" id="paramId"/>
   <input type ="hidden" name ="value" id="value"/> 
</form>
<div id="addparameter" class="modal fade mt-2 save-modal" role="dialog">
<div class="modal-dialog modal-md">
   <!-- Modal content-->
   <div class="modal-content">
      <div class="modal-header">
         <h3 class="modal-title">Add Parameter Setup </h3>
         <button type="reset" class="close" data-dismiss="modal">&times;</button>
      </div>
    
   <div class="modal-body">
   <form action ="<@ofbizUrl>createSection</@ofbizUrl>" method="post">
      <@checkbox
      name="addSection"
      id="addSection"
      label="${uiLabelMap.AddSection!}"
      checked=false
      value="Y"
      />
      <@inputRow
      name="newSection"
      id="newSection"
      placeholder = "${uiLabelMap.NewSection!}"
      label="${uiLabelMap.NewSection!}"
      style="display:none;"
      />
      
      <div class="modal-footer"  id="addSectionId" style="display:none;">
         <div class="text-left ml-1">
            <input type="submit" id="addDropdown" name="addDropdown" class="btn btn-sm btn-primary" style="display:none;" value="Add Section" onclick="return formSubmission();" />
         </div>
      </div>
      </form>

 <form action ="<@ofbizUrl>createParameter</@ofbizUrl>" method="post">
        
     <div class="form-group row " id="dropDowm_row">
                  <label class="col-sm-4 field-text " for="sections">Section</label>
                  <div class="col-sm-8">
                  
                    <select name="sections" id="sections"  class="ui dropdown search form-control input-sm" >
                    <option value="">${uiLabelMap.SelectSection!}</option>
                      <#if sectionDetails?exists && sectionDetails?has_content>
                        <#list sectionDetails as li>
                           <option value="${li.enumId!}">${li.description!}</option>
                        </#list>
                      </#if>
                    </select>
                       <div class="help-block with-errors" id="sections_error"></div>
                  </div>
               </div>
        
        
             
      <@inputRow    
      label="${uiLabelMap.ParameterName!}"
      id="parameterName"
      name="parameterName"
      placeholder="${uiLabelMap.ParameterName!}"
      />
      <@inputRow    
      label="${uiLabelMap.ParameterId!}"
      id="parameterId"
      name="parameterId"
      placeholder="${uiLabelMap.ParameterId!}"
      />
      <@inputRow    
      label="${uiLabelMap.ParameterValue!}"
      id="parameterValue"
      name="parameterValue"
      placeholder="${uiLabelMap.ParameterValue!}"
      />
   </div>
   
   <div class="modal-footer"  id="addFooter"  >
         <div class="text-left ml-1">
            <input type="submit" id="add" name="add" class="btn btn-sm btn-primary" value="Add"  onclick="return formParamSubmission();"/>
         </div>
      </div>
   
  <#-- <div class="modal-footer">
   <@formButton
   btn1type="submit"
   btn1label="${uiLabelMap.Add}"
   />  -->
</form>
      </div>
   </div>
</div>
 <#--To display comment popup -->
 <div id="submitCommentModal" class="modal fade" role="dialog">
   <div class="modal-dialog modal-md modal-lg">
       <div class="modal-content">
         <div class="modal-header">
         <h4 class="modal-title">Comments</h4>
         <form id="editCommentForm" method="post">
        <input type="hidden" id="commentToUpdate" name="commentToUpdate" value="">
        <input type="hidden" id="commentParamId" name="commentParamId" value="">
        <#assign editPermission = Static["org.fio.homeapps.util.DataUtil"].hasPermission(request, "ADMNPR_GLOBAL_PARAMS_CMNT_EDIT")?if_exists />
         <#if editPermission>
         <a class='btn btn-xs btn-primary' onclick="editComment();" title="Edit"><i class="fa fa-edit" aria-hidden='true'></i></a>
           </#if>
           <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
         </div>
         <div class="modal-body p-3">
         <span id="comment_message"></span>
         </div>
         <div class="modal-footer"></div>
         <form>
       </div>
   </div>
 </div>
 
 <div id="addCommentModal" class="modal fade" role="dialog">
   <div class="modal-dialog modal-md modal-lg">
       <div class="modal-content">
         <div class="modal-header">
         <h4 class="modal-title">Add Comments</h4>
         <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
         </div>
         <form id="addCommentForm" method="post"  data-toggle="validator">
         <div class="modal-body">
         <@inputArea id="formComment" 
         name="comment" 
         label="Comments" 
         labelColSize="col-md-1 col-lg-1"
         inputColSize="col-md-11 col-lg-11"
         rows="20" 
         required=true
         placeholder="${uiLabelMap.Comments}" />
         <@inputHidden id="formParameterId" name="parameterId" value=""/>
         </div>
         <div class="modal-footer">
         <input type="submit" class="btn btn-sm btn-primary navbar-dark" id="saveButton" value="Save" onclick="javascript:return onSubmitValidate(this);">
		 <input type="button" class="btn btn-sm btn-primary navbar-dark" data-dismiss="modal" value="Cancel" onclick="clear();">	
         </div>
         </form>
       </div>
   </div>
 </div>
 
  <div id="updateNewCommentModal" class="modal fade" role="dialog">
   <div class="modal-dialog modal-md modal-lg">
       <div class="modal-content">
         <div class="modal-header">
         <h4 class="modal-title">Edit Comments</h4>
         <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
         </div>
         <form id="updateCommentForm" method="post" data-toggle="validator">
         <div class="modal-body">
         <@inputArea id="formCommentToEdit" 
         name="comment" 
         label="Comments" 
         labelColSize="col-md-1 col-lg-1"
         inputColSize="col-md-11 col-lg-11"
         rows="20"
         value=""
         required= true
         dataError="Please Enter the Comments"
         placeholder="${uiLabelMap.Comments}" />
         <@inputHidden id="formParameterIdforEdit" name="parameterId" value=""/>
         </div>
         <div class="modal-footer">
         <input type="submit" class="btn btn-sm btn-primary navbar-dark" id="saveButton" value="Update" onclick="javascript:return onSubmitValidateUpdate(this);">
		 <input type="button" class="btn btn-sm btn-primary navbar-dark" data-dismiss="modal" value="Cancel" onclick="clearUpdate();">	
         </div>
         </form>
       </div>
   </div>
 </div>
 
<script>
   function onSubmitValidateUpdate() {
   	var valid = true;
   	if ($('#formCommentToEdit').val() == "") {
   		$('#formCommentToEdit_error').html("Please Enter the Comments");
   		$('#formCommentToEdit_error').show();
   		valid = false;
   	} else {
   		updateComments();
   	}
   	return valid;
   }
   $('#formCommentToEdit').keyup(function(){	
   		if($(this).val()!=""){
   		
   		$('#formCommentToEdit_error').hide();
   	}
   });
   $('#formComment').keyup(function(){	
   		if($(this).val()!=""){
   		
   		$('#formComment_error').hide();
   	}
   });
   	function onSubmitValidate() {
   		var valid = true;
   		if ($('#formComment').val() == "") {
   			$('#formComment_error').html("Please Enter the Comments");
   			$('#formComment_error').show();
   			valid = false;
   		} else {
   			addComments();
   		}
   		return valid;
   	}   	

	function clear() {
		$('#addCommentModal #formComment #formParameterId').val('');
	}

function clearUpdate() {
		$('#updateNewCommentModal #formCommentToEdit #formParameterIdforEdit').val('');
	}
function editComment(){
	var commentForEdit = $('#commentToUpdate').val();
	var paramIdToEdit = $('#commentParamId').val();
	$('#submitCommentModal').modal('hide');
	$('#updateNewCommentModal #formCommentToEdit').val('');
	$('#updateNewCommentModal #formParameterIdforEdit').val('');
	$('#updateNewCommentModal').modal('show');
	        $("#formCommentToEdit").val(commentForEdit);
	        $("#formParameterIdforEdit").val(paramIdToEdit);
	}

function updateComments() {
	event.preventDefault();
	var parameterId = $('#formParameterIdforEdit').val();
	var comment = $('#formCommentToEdit').val();
	if (comment != null) {
		$.ajax({
			type: "POST",
			url: "addComment",
			async: true,
			data: {
				"parameterId": parameterId,
				"comment": comment
			},
			success: function(data) {
				if (data.success == "success") {
					showAlert("success", data.successMessage);
				} else {
					showAlert("error", "Error in updating comments");
				}
			}
		});
		$('#updateNewCommentModal').modal('hide');
		return false;
	}
}
   function edit(element) {
       var parent=$(element).parent().parent();
       var value=$(parent).find('.edit-text').text();
       //hide label
       $(parent).find('label').hide();
       //show input, set placeholder
       var input=$(parent).find('input[type="text"]');
       $(input).show();
       $(input).attr('value', value);
   }
   
   function save(parameterId,index,block){
       var myParVal = "";
       myParVal=block+"_o_"+index;
       var myVal=document.getElementById(myParVal).value;
       if(parameterId !=null )
       {
           document.getElementById("paramId").value = parameterId;
           document.getElementById("value").value = myVal;
           document.updateParameterForm.submit();
       }
   }
   function findTrigger()
   {
         //$("#custom-field-accordion").html("");
         $.ajax({
         type : "POST",
			url : "searchGlobalParameters",
			async: false,
			data :  $("form").serializeArray(),
			success : function(data) {
				$("#custom-field-accordion").html("");
			    var jsondata = "";
			    var count = 0;
	 $.each(data, function(key, value) {
	 var param = key;
	jsondata = jsondata +"<div class='card attr-detail'>";
    jsondata = jsondata + "<div class='card-header pt-1 pb-1'>";
    if(count != 0){
    jsondata = jsondata + "<a role='button' class='card-link collapsed' data-toggle='collapse' href='#acc1_o_"+count+"' aria-expanded='true'>"+param+"</a>";
    }
    else{
    jsondata = jsondata + "<a role='button' class='card-link' data-toggle='collapse' href='#acc1_o_"+count+"' aria-expanded='false'>"+param+"</a>";
    }	 
    jsondata = jsondata + "</div>";
	            var jsonSectionData =loadValue(value,count);
	                jsondata = jsondata + jsonSectionData;
	                jsondata = jsondata + "</div>";
                    jsondata = jsondata + "</div>";
                  
                    $("#custom-field-accordion").html(jsondata);
	               count = count+1;
                   });
             }
        });
	               
   }
   
    $('button').on('click', function() {
       event.preventDefault();
       findTrigger();
    });
    

function loadValue(value, count) {
	var jsondata1 = "";
	if (count == 0) {
		jsondata1 = jsondata1 + "<div id='acc1_o_" + count + "' class='card-collapse collapse attr-collapse show' data-parent='#custom-field-accordion' style''>";
	}
    else {
	jsondata1 = jsondata1 + "<div id='acc1_o_" + count + "' class='card-collapse collapse attr-collapse' data-parent='#custom-field-accordion' style''>";
    }
    jsondata1 = jsondata1 + "<div class='card-body'>";
    jsondata1 = jsondata1 + "<div class='row'>";
    jsondata1 = jsondata1 + "<div class='col-md-12 col-lg-12 col-sm-12 '>";
    jsondata1 = jsondata1 + "<div class='table-responsive'>";
    jsondata1 = jsondata1 + "<table class='table table-hover table-striped'>";
    jsondata1 = jsondata1 + "<thead>";
    jsondata1 = jsondata1 + "<tr>";
    jsondata1 = jsondata1 + "<th width='20%'>Parameter ID</th>";
    jsondata1 = jsondata1 + "<th width='30%'>Parameter Name</th>";
    jsondata1 = jsondata1 + "<th width='35%'>Parameter Value</th>";
    jsondata1 = jsondata1 + "<th width='10%'></th>";
    jsondata1 = jsondata1 + "<th width='5%'></th>";
    jsondata1 = jsondata1 + "</tr>";
    jsondata1 = jsondata1 + "</thead>";
    jsondata1 = jsondata1 + "<tbody>";
    $.each(value,function(i,v) {
    var jsonDataParameter = "<tr>";
    jsonDataParameter = jsonDataParameter + "<td>"+v.parameterId+"</td>";
    jsonDataParameter = jsonDataParameter + "<td>"+v.description+"</td>";
    jsonDataParameter = jsonDataParameter + "<td title='\""+v.value+"\"'><label class='edit-text text-size-control'>"+v.value+"</label> <input type='text' name = '"+v.storeId+"_o_"+i+"' class='workingHr' id = '"+v.storeId+"_o_"+i+"' class='form-control' style='display:none;'></td>"
     jsonDataParameter = jsonDataParameter +"<td><a class='btn btn-xs btn-primary' onclick='edit(this);' title='Edit'><i class='fa fa-edit' aria-hidden='true'></i> <a class='btn btn-xs btn-success' onclick='save(\""+v.parameterId+"\","+i+",\""+v.storeId+"\");' title='Submit'><i class='fa fa-check' aria-hidden='true'></i> </a> </td>";
   jsonDataParameter = jsonDataParameter + "<td><a onclick='addOrViewComment(\""+v.parameterId+"\");' style='background-color: #02829d !important; border-color: #02829d47 !important; color: #ffffff !important;' class='btn btn-xs btn-primary' title='Comments'><i class='fa fa-comment' aria-hidden='true'></i> Comments</a></td>";
    jsonDataParameter = jsonDataParameter + "</tr>";
    	jsondata1 = jsondata1 + jsonDataParameter;
    });
    jsondata1 = jsondata1 + "</tbody>";
    jsondata1 = jsondata1 + "</table>";
    jsondata1 = jsondata1 + "</div>";
    jsondata1 = jsondata1 + "</div>";
    jsondata1 = jsondata1 + "</div>";
    jsondata1 = jsondata1 + "</div>";
    jsondata1 = jsondata1 + "</div>";
    return jsondata1;
    }
<#-- script to display or add comment -->
function addOrViewComment(paramIdForComment) {
	var url = "addOrViewComment";
	$.ajax({
				type: "POST",
				url: url,
				async: true,
				data: {
					"parameterId": paramIdForComment
				},
				success: function(data) {
						var commentData = data.comment;
						if (commentData != null) {
							$('#submitCommentModal').modal('show');
							$("#comment_message").html(commentData);
							$("#commentToUpdate").val(commentData);
							$("#commentParamId").val(paramIdForComment);
						}
						else{
							$('#addCommentModal #formComment').val('');
							$('#addCommentModal').modal('show');
						 	$("input[name=parameterId]").val(paramIdForComment);
						}
				}
			});
		}
function addComments() {
	event.preventDefault();
	var parameterId = $('#formParameterId').val();
	var comment = $('#formComment').val();
		$.ajax({
			type: "POST",
			url: "addComment",
			async: true,
			data: {
				"parameterId": parameterId,
				"comment": comment
			},
			success: function(data) {
			if (data.success == "success") {
				showAlert("success", data.successMessage);
			} else {
				showAlert("error", "Error in adding comments");
			}
		}
	});
	$('#addCommentModal #formComment').val('');
	$('#addCommentModal').modal('hide');
	return false;
}
 function clearFields() {
 	document.getElementById("search").reset();
 	findTrigger();
 }
  $(document).ready(function(){
  $('input[name="addSection"]').click(function(){
   if($(this).is(":checked")){
    $("#newSection_row").show();
    $("#addDropdown").show();
       $("#addSectionId").show();
     $("#dropDowm_row").hide();
      $("#parameterName_row").hide();
       $("#parameterId_row").hide();
        $("#parameterValue_row").hide();
        $("#add").hide();
           $("#addFooter").hide();
    
    $("#addSection").val('Y');
    }
   else if($(this).is(":not(:checked)")){
    $("#newSection_row").hide();
    $("#addDropdown").hide();
    
    $("#addSectionId").hide();
    
      $("#dropDowm_row").show();
      $("#parameterName_row").show();
       $("#parameterId_row").show();
        $("#parameterValue_row").show();
        $("#add").show();
           $("#addFooter").show();
  
    
    
    $("#newSection").prop('required',true);
    $("#addDropdown").prop('required',true);
    $("#addSection").val('N');
    }
   });
   });
 $("#newSection").keyup(function(){  
    if($("#newSection").val() != null){
    $("#newSection_error").html("");  
    }
}); 
    
function formSubmission(){
    var isValid = "Y";
    var newSection =  $("#newSection").val();
    if(newSection!='')
        return true;
    else{
        $("#newSection_error").html('');
        $("#newSection_error").append('<ul class="list-unstyled text-danger"><li id="newSection_err">Please enter New Section </li></ul>');
        return false;
    }
}
 
 
 $("#sections").change(function(){  
    if($("#sections").val() != null){
    $("#sections_error").html("");  
}
}); 
$("#parameterName").keyup(function(){  
    if($("#parameterName").val() != null){
    $("#parameterName_error").html("");  
    }
}); 
$("#parameterId").keyup(function(){  
    if($("#parameterId").val() != null){
    $("#parameterId_error").html("");  
    }
}); 
  
  function formParamSubmission(){
    var isValid = "Y";
    var sections =  $("#sections").val();
    var parameterName =  $("#parameterName").val();
    var parameterId =  $("#parameterId").val();
    if(sections!=''&& parameterName!=''&& parameterId!=''){
            return true;
    }
    else{
       if(sections == "") {
          $("#sections_error").html('');
          $("#sections_error").append('<ul class="list-unstyled text-danger"><li id="sections_err">Please select Sections </li></ul>');
           isValid = "N";
        }
        if(parameterName == "") {
          $("#parameterName_error").html('');
          $("#parameterName_error").append('<ul class="list-unstyled text-danger"><li id="parameterName_err">Please enter Parameter Name</li></ul>');
           isValid = "N";
        }
         if(parameterId == "") {
           $("#parameterId_error").html('');
           $("#parameterId_error").append('<ul class="list-unstyled text-danger"><li id="parameterId_err">Please enter Parameter Id</li></ul>');
            isValid = "N";
        }
        
        if(isValid == "N")
          return false;
        else if(isValid == "Y")
          return true;
        }
}
(function () {
  	var isCollapsedAll = false;  
  	$("#attr-collapseAll-btn").click(function(event) {
  		if (!isCollapsedAll) {
  			$('.attr-collapse').removeClass('hide').addClass('show');
  			isCollapsedAll = true;
  			$('#attr-collapseAll-btn i').removeClass('fa-arrow-circle-down').addClass('fa-arrow-circle-up');
  			$('#attr-collapseAll-btn span').text('Collapse All');
  		} else {
  			$('.attr-collapse').removeClass('show').addClass('hide');
  			isCollapsedAll = false;
  			$('#attr-collapseAll-btn i').removeClass('fa-arrow-circle-down').addClass('fa-arrow-circle-down');
  			$('#attr-collapseAll-btn span').text('Expand All');
  		}
	});
}()); 
 
</script>
<!--div class="col-9 offset-2">
   <div class="alert alert-success alert-dismissible fade in " id="success-alert">
   <button type="button" class="close" data-dismiss="alert">x</button>
   <strong>Success! </strong>
   Product have added to your wishlist.
   </div>
   </div!-->