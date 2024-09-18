<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://common-portal/webapp/common-portal/lib/picker_macro.ftl"/>
<style>
div#srDateRange_to_picker2 {
    margin-left: 7px;
    margin-top: 6px;
}
</style>
<div class="row">
	<div id="main" role="main" class="pd-btm-title-bar">

	<div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
	
	<@sectionFrameHeader title="${uiLabelMap.FindServiceRequest!}" />
	
	<div class="panel-group" id="accordionMenu" role="tablist" aria-multiselectable="true">
		<div class="panel panel-default">
			<div class="panel-heading" role="tab" id="headingTwo">
				<h4 class="panel-title">
					<a role="button" data-toggle="collapse" data-parent="#accordionMenu"
						href="#accordionDynaBase" aria-expanded="false"
						aria-controls="collapseOne"> ${uiLabelMap.MainFilter} </a>
				</h4>
			</div>	
			
			<div id="accordionDynaBase" class="panel-collapse collapse" role="tabpanel" aria-labelledby="headingOne">
				<form method="post" id="searchForm" class="form-horizontal" novalidate="novalidate" data-toggle="validator">
				<div class="panel-body">
					<#assign userName = "">
					<@inputHidden id="loggedInUserId" value="${userLogin.userLoginId?if_exists}" />
		            <#assign findMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", userLogin.partyId)>
		            <#assign person = delegator.findOne("Person", findMap, true)!>
		            <#if person?has_content>
		            	<#assign userName = (person.firstName!) + " " + (person.middleName!) + " " + person.lastName!>
		            	<@inputHidden id="userName" value="${userName!}"/>
		            </#if>
						            	
					<@dynaScreen 
							instanceId="FIND_SR_DYNA"
							modeOfAction="CREATE"
					/>
														
					<div class="row find-srbottom">
		            	<div class="col-lg-12 col-md-12 col-sm-12">
		                	<div class="text-right pd-cbx-lbls" style="padding-top: 0px;">
			                	<div class="form-check-inline">
		                        	<label class="form-check-label flx-cbx-lbl">
		                          		<input id="unAssigned" name="unAssigned" class="form-check-input" value="y"  type="checkbox">Unassigned Owner
		                          	</label>
		                        </div>
		                    	<div class="form-check-inline">
		                        	<label class="form-check-label flx-cbx-lbl">
		                          		<input id="open" name="open" class="form-check-input" value="y" checked="checked" type="checkbox">Open
		                          	</label>
		                        </div>
		        				<div class="form-check-inline">
		          					<label class="form-check-label flx-cbx-lbl">
		          						<input id="closed" name="closed" class="form-check-input" value="" type="checkbox">Closed
		          					</label>
		        				</div>
		        				<div class="form-check-inline">
		          					<label class="form-check-label flx-cbx-lbl">
		          						<input id="slaAtRisk" name="slaAtRisk" class="form-check-input" value="" type="checkbox">SLA at Risk
		          					</label>
		        				</div>
		        				<div class="form-check-inline">
		          					<label class="form-check-label flx-cbx-lbl">
		          						<input id="slaExpired" name="slaExpired" class="form-check-input" value="" type="checkbox">Overdue
		          					</label>
		        				</div>
		        				
						     	<@button
						        id="sr-search-btn"
						        label="${uiLabelMap.Find}"
						        />	
						     	<@reset
								label="${uiLabelMap.Reset}"/>
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
 
<@partyPicker 
	instanceId="partyPicker"
/>

<script>
	$('.picker-window-erase').click(function () {
		$("#srPrimaryContactId").empty();
		$('#srPrimaryContactId').dropdown('clear');
	});
	
	$(':reset').on('click', function(evt) {
	    evt.preventDefault();
	    $('.picker-window-erase').click();
	    //$("#srPrimaryContactId").empty();
		//$('#srPrimaryContactId').dropdown('clear');
	});

	$(document).ready(function() {
		$('#srDateRange_from_picker1').addClass('w-100');
		$('#srDateRange_to_picker2').width('92%');

		$("div.ui.dropdown.search.form-control.fluid.show-tick.owner.selection > i").addClass("clear");
		loadCategory();
		getUsers();
	});
	
	function getUsers() {
		var loggedInUserId  = $("#loggedInUserId").val();
		var loggedInUserName  = $("#userName").val();
		var userOptionList = '<option value=""></option>';//'<option value="'+loggedInUserId+'" selected="selected">'+loggedInUserName+'</option>';
		$.ajax({
			type: "GET",
			url:'/common-portal/control/getUsersList?roleTypeId=TECHNICIAN',
			async: false,
			success: function(data) {
				for (var i = 0; i < data.length; i++) {
					var type = data[i];
					userOptionList += '<option value="'+type.userLoginId+'">'+type.userName+'</option>';
				}
			}
		});
		$("#owner").html(userOptionList);
	}
	
	$("#partyId_desc").on("change", function() {
		var partyId = $("#partyId_val").val();
		if (partyId != "") {
       		getPrimaryContacts(partyId);
       }
	});
	
	<#--  
	function getPrimaryContacts(partyId){
		var nonSelectContent = "<span class='nonselect'>Please Select</span>";
		var dataSourceOptions = '';
		
		$.ajax({
			type: "POST",
			url: "/common-portal/control/getPrimaryContacts",
			data: {"partyId": partyId, "externalLoginKey": "${requestAttributes.externalLoginKey!}"},
			async: false,
			success: function (data) {   
				if (data) {
					if(data.responseMessage=="success"){
						for (var i = 0; i < data.partyRelContacts.length; i++) {
							var entry = data.partyRelContacts[i];
							if(entry.selected!=null){
			            		dataSourceOptions += '<option value="'+entry.contactId+'" selected>'+entry.name+'</option>';
							}else{
			            		dataSourceOptions += '<option value="'+entry.contactId+'">'+entry.name+'</option>';
							}
						}
					}
				}
			}
		});    
		$("#srPrimaryContactId").html( dataSourceOptions );
		$("#srPrimaryContactId").dropdown('refresh');
	}-->
	
function getPrimaryContacts(partyId){
	$("#srPrimaryContactId").empty();
	var nonSelectContent = "<span class='nonselect'>Please Select</span>";
	var dataSourceOptions = '';
	var selectedContactId  = $("#selectedContactId").val();
	$('#srPrimaryContactId').dropdown('clear');
	$("div.ui.dropdown.search.form-control.fluid.show-tick.srPrimaryContactId.selection > i").removeClass("clear");
	$.ajax({
		type: "POST",
		url: "/common-portal/control/getPrimaryContacts",
		data: {"partyId": partyId, "externalLoginKey": "${requestAttributes.externalLoginKey!}"},
		async: false,
		success: function (data) {
			if((data != null && data != "" && data !="undefined") && data.responseMessage=="success" && data.partyRelContacts.length > 0){
					for (var i = 0; i < data.partyRelContacts.length; i++) {
						var entry = data.partyRelContacts[i];
						if(entry.selected!=null){
		            		dataSourceOptions += '<option value="'+entry.contactId+'" selected>'+entry.name+'</option>';
						}else{
		            		dataSourceOptions += '<option value="'+entry.contactId+'">'+entry.name+'</option>';
						}
					}
				$("div.ui.dropdown.search.form-control.fluid.show-tick.srPrimaryContactId.selection > i").addClass("clear");
			}
		}
	});
	$("#srPrimaryContactId").append(dataSourceOptions);
	$("#srPrimaryContactId").dropdown('refresh');
}

 <#-- function process(date) {
        var parts = date.split("/");
        return new Date(parts[2], parts[1] - 1, parts[0]);
    }
   
    $("#dueDate").focus(function() {}).blur(function() {
    	var createdOn = $("#createdOn").val();
        var dueDate = $("#dueDate").val();
        var dueDateLen = $("#dueDate").val().trim().length;
         if ((createdOn  != null || createdOn  != "") && (dueDate != null || dueDate != "")) {
            $("#dueDate_error").empty();
            if (process(dueDate) < process(createdOn)) {
                $("#dueDate_error").empty();
                $("#dueDate_error").append('<ul class="list-unstyled"><li>End date cannot be before start date</li></ul>');
            }
        }
    });   -->
    
 	var open="";
    var slaAtRisk="";
    var slaExpired="";
    var closed="";
    
	$("#open").change(function(){
	  var cBox = this.checked;
	  if (cBox) {
	     $("#open").val('y');
	      open = "Y";
	     
	   }else{
	       $("#open").val('');
	        open = "";
	   }
	});
	
	$("#closed").change(function(){
	  var cBox = this.checked;
	  if (cBox) {
	     $("#closed").val('y');
	      closed = "Y";
	     
	   }else{
	       $("#closed").val('');
	        closed = "";
	   }
	});
	
	$("#slaAtRisk").change(function(){
	  var cBox = this.checked;
	  if (cBox) {
	     $("#slaAtRisk").val('y');
	     slaAtRisk = "Y";
	     
	   }else{
	       $("#slaAtRisk").val('');
	       slaAtRisk = "";
	   }
	});
	
	$("#slaExpired").change(function(){
	  var cBox = this.checked;
	  if (cBox) {
	     $("#slaExpired").val('y');
	     slaExpired = "Y";
	     
	   }else{
	       $("#slaExpired").val('');
	       slaExpired = "";
	   }
	});
	
	$("#createdOn").change(function() {
	   var startDate = $("#createdOn").val();
	   $("#createdOn_error").empty();
	      
	});
	
	 $("#dueDate").change(function() {
	   var dueDate = $("#dueDate").val();
	   $("#dueDate_error").empty();
	      
	});
	
	$("#srArea").change(function() {
	   var srCategoryId  = $("#srArea").val();
	   var srTypeId  = $("#srTypeId").val();
	   if (srCategoryId != "") {
	   	   $('.srSubArea .clear').click();
	       loadSubCategory(srCategoryId);
	   }else{
	   	  $("#srSubArea").html('');
	   	  $('.srSubArea .clear').click();
	   }
	});
	
	function loadCategory() {
	    var nonSelectContent = "<span class='nonselect'>Please Select</span>";
	    var categoryOptions = '<option value="" data-content="'+nonSelectContent+'" selected="">Please Select</option>';
	    $.ajax({
	        type: "POST",
	        url: "getSrCategory",
	        async: false,
	        success: function(data) {
	        	var sourceDesc = data.results;
	            for (var i = 0; i < data.length; i++) {
	            	var category = data[i];
	                categoryOptions += '<option value="'+category.srCategoryId+'">'+category.srCategoryDesc+'</option>';
	          	}
	        }
	    });
	    $("#srArea").html(categoryOptions);
	}
	
	function loadSubCategory(srCategoryId) {
	    var nonSelectContent = "<span class='nonselect'>Please Select</span>";
	    var subCategoryOptions = '<option value="" data-content="'+nonSelectContent+'" selected="">Please Select</option>';
	    $.ajax({
	        type: "POST",
	        url: "getSrSubCategory",
	        data: { "srCategoryId": srCategoryId },
	        async: false,
	        success: function(data) {
	        	var sourceDesc = data.results;
	            for (var i = 0; i < data.length; i++) {
	            	var category = data[i];
	                subCategoryOptions += '<option value="'+category.srSubCategoryId+'">'+category.srSubCategoryDesc+'</option>';
	          	}
	        }
	    });
	    $("#srSubArea").html(subCategoryOptions);
	}
	
	document.getElementById('resolve').onclick = () => {
    	var rowdata = getSelectedRows();
        if(rowdata!=null && rowdata!=""){
      		rowdata.forEach(element => {
	      		var srSubStatus = "SR_RESOLVED"; 
	      		var externalId = element.externalId;
	        	var srStatus = element.srStatus;
	            if(srStatus!= "Cancelled" && srStatus!="Closed"){
	            	var dataSets =  { "srSubStatus": srSubStatus , "externalId": externalId };           
			        $.ajax({
			            type: "POST",
			            url: "updateSRResolveStatus",
			            data:dataSets,
			            async: false,
			            success: function(data) {  
			            $.notify({
			                message : '<p>Resolved sub status Changed for</p>'+externalId,
			              });
			             loadFindSrvsAgGrid();
			            }
			        });
	        	}else {
	              $.notify({
	                message : '<p>You cannot resolve the record with status Closed / Cancelled</p>',
	              });
	           	}
			});
		} else {
              $.notify({
                message : '<p>Please select one record in the list</p>',
              });
           }
	}

	var externalId;
	$("#reassign").click(function () {          
	var rowdata = getSelectedRows();
    	if(rowdata!=null && rowdata!=""){
        	rowdata.forEach(element => {
			 	var ownerUserLoginId = element.ownerUserLoginId;     
			    var emplTeamId = element.empTeamId;
			    var ownerBuId = element.ownerBuId;                     
			    var ownerBuName = element.ownerBuName;
			    externalId=element.externalId;
			    document.getElementById("externalId").value = externalId;
			    if (emplTeamId != "" && ownerBuId != "") {
			    	loaduserteam(emplTeamId , ownerBuId, externalId);
			        $("#reassignModal").modal();
			        $("input[type='reset']").hide();
			    } 
      		}); 
		} else {
      		$.notify({
        		message : '<p>Please select one record in the list</p>',
      		});
   		}
	});
   
	function loaduserteam(emplTeamId,businessUnitId,workEffortId) {
    	var nonSelectContent;
        var userOption;       
        var teamOptions;
        var dataSet = {};
        $("input[name$='emp']").click(function() {
        	var test = $(this).val();
            if(test=="user"){      
            	$("div.textboxteam").hide();
                $("div.textboxUser").show();                          
                document.getElementById("userText").innerHTML = null;
                dataSet =  { "emplTeamId": emplTeamId , "businessUnitId": "" };
            }
            if(test=="team")
            {        
            	$("div.textboxteam").show();
                $("div.textboxUser").hide();
                document.getElementById("userText").innerHTML = null;
                dataSet =  { "emplTeamId": "" , "businessUnitId": businessUnitId };
          	}
      		$.ajax({
        		type: "POST",
	            url: "getOwnerTeam",
	            data:dataSet,
	            async: false,
	            success: function(data) {
	            	nonSelectContent = "<span class='nonselect'>Please Select</span>";
	            	userOption = '<option value="" data-content="'+nonSelectContent+'" selected="">Please Select</option>';       
	            	teamOptions = '<option value="" data-content="'+nonSelectContent+'" selected="">Please Select</option>';
	            	var sourceDesc = data.results;
	            	for (var i = 0; i < data.length; i++) {
	                	var category = data[i];
	                    userOption += '<option value="'+category.userLoginId+'">'+category.partyName+'</option>';                     
	                    teamOptions += '<option value="'+category.emplTeamId+'">'+category.teamName+'</option>';
	            	}
        		}
      		});
   
      		$("#userText").html(userOption);
      		$("#teamText").html(teamOptions);
		});    
	}

	document.getElementById('saveModal').onclick = () => {
    	var ownerUserLoginId=  $('#userText').val();  
       	var emp=  $('#teamText').val();            
       	var dataSets =  { "externalId": externalId , "ownerUserLoginId": ownerUserLoginId, "emplTeamId":emp };   
       	$.ajax({
        	type: "POST",
            url: "UpdateReasignSR",
            data:dataSets,
            async: false,
            success: function(data) {
            	$("#reassignModal").modal('hide');   
              	$.notify({
                	message : '<p>Reassigned Successfully for</p>'+externalId,
              	});
              	loadFindSrvsAgGrid();
            }
        });   
    }

 </script>
 