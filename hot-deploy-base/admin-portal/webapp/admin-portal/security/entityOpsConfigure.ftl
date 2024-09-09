<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<div class="row">
   <div id="main" role="main" >
      <#-- <#if entityOpsConfig?has_content>
      <#assign extra = '<a href="viewEntityOpsConfigure?entityName=${requestParameters.entityName?if_exists}" class="btn btn-xs btn-primary"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>' />
        <@sectionFrameHeader 
            title="${uiLabelMap.UpdateEntityOperations}"
            extra=extra!
            />
      <#else>
        <@sectionFrameHeader 
            title="${uiLabelMap.ConfigureOperations}"
            />
      </#if>  -->
      <div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
      	<#if entityOpsConfig?has_content>
      		<#assign extra = '<a href="viewEntityOpsConfigure?entityName=${requestParameters.entityName?if_exists}" class="btn btn-xs btn-primary"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>' />
        	<@sectionFrameHeaderTab 
            	title="${uiLabelMap.UpdateEntityOperations}"
            	extra=extra!
            />
      		<#else>
        		<@sectionFrameHeaderTab 
            	title="${uiLabelMap.ConfigureOperations}"
            />
      	</#if>
         <form method="post" action="generateEntityOperations" id="entityConfigureForm" name="entityConfigureForm" novalidate  data-toggle="validator" onsubmit = "javascript:return generateOperations();" >
            <div class="row">
               <div class="col-md-12 col-lg-6 col-sm-12 ">
               <#if entityOpsConfig?has_content>
                  <@dropdownCell 
	                  id="entityName"
	                  label="${uiLabelMap.Entity}"
	                  placeholder="${uiLabelMap.Entity}"
	                  options=entities!
	                  allowEmpty=true
	                  required=true
	                  value="${entityOpsConfig?if_exists.entityName!}"
	                  disabled=true
	                  />
	           <#else>
	               <@dropdownCell 
                      id="entityName"
                      label="${uiLabelMap.Entity}"
                      placeholder="${uiLabelMap.Entity}"
                      options=entities!
                      allowEmpty=true
                      required=true
                      value="${entityOpsConfig?if_exists.entityName!}"
                      dataError="Please select an Entity"
                      />
	           </#if>
                  
	              <@dropdownCell 
                      id="entityType"
                      label="${uiLabelMap.EntityType}"
                      placeholder="${uiLabelMap.EntityType}"
                      options=entityTypeList!
                      allowEmpty=true
                      required=true
                      value="${entityOpsConfig?if_exists.entityType!'PARTY_ENTITY'}"
                      dataError="Please select an Entity Type"
                      />
                  <div id="partyEntityRoleType" style="display:none;">
	              <@dropdownCell 
                      id="roleTypeId"
                      label="${uiLabelMap.RoleType}"
                      placeholder="${uiLabelMap.RoleType}"
                      options=roleTypeList!
                      allowEmpty=true
                      value="${entityOpsConfig?if_exists.roleTypeId!}"
                      dataError="Please select an Role Type"
                      />
                  </div>
                  <@inputRow
	                  id="entityAliasName"
	                  label="${uiLabelMap.Alias}"
	                  placeholder="${uiLabelMap.Alias}"
	                  value="${entityOpsConfig?if_exists.entityAliasName!}"
	                  dataError="Please enter entity alias name"
	                  maxlength=60
	                  />
                  <@inputHidden 
                      id="entityOperations"
                      />
                  <#assign entityOpsEnum = Static["org.fio.admin.portal.util.EnumUtil"].getEnums(delegator, "ENTITY_OPERATIONS")?if_exists />
                  <#assign entityOpsList = Static["org.fio.admin.portal.util.DataHelper"].getDropDownOptions(entityOpsEnum, "enumId","description")?if_exists />  
                  
                  <@inputCheckBox
                      id="operations"
                      label="${uiLabelMap.Operations}"
                      optionList=entityOpsList!
                      optionValues=entityOpsConfig?if_exists.operations!
                      />
               </div>
            </div>
            <#-- row end -->
            <div class="form-group offset-2">
               <div class="text-left ml-1">
                  <@submit
	                  label="${uiLabelMap.Generate!}"
	                  />
               </div>
            </div>
         </form>
         <#--<#if entityOpsConfig?has_content> -->
             <div class="clearfix"></div>
             
             <div class="">
				<@AgGrid 
					userid="${userLogin.userLoginId}" 
                    instanceid="ENTITY_OPERATION"
                    shownotifications="true"
                    autosizeallcol="true"
                    debug="false"
                    insertBtn=false
                    updateBtn=false
                    removeBtn=false
                    gridheadertitle=uiLabelMap.ListofConfigureOperations!
                    gridheaderid="listEntityOpsBtns"
                    statusBar=true
                    serversidepaginate=false
                    /> 
                <script type="text/javascript" src="/admin-portal-resource/js/ag-grid/security/list-config-entity-ops.js"></script>
             	
             </div>
             
         <#--</#if> -->
         
      </div>
   </div> <#-- main end -->
</div> <#-- row end -->

<script>
function generateOperations(){
  	var operations = $("input[name='operations']:checked").map(function() {return this.value;}).get().join(',');
  	console.log("operations--->"+operations);
  	var entityName = $("#entityName").val();
    var flag = "Y";
    if(entityName == null || entityName == "") {
        $('#entityName_error').append('<ul class="list-unstyled"><li>Please select an Entity</li></ul>');
        flag = "N";
    } else{
        $("#entityName_error").empty();
        $('#entityName_error').html("");
    }
  	if(operations == null || operations == "" || operations == 'undefined') {
  		$('#operations_error').html("Please select atleast one operation");
  		 flag = "N";
  	} else{
  		$('#operations_error').html("");
  		$('#entityOperations').val(operations);
  	}
  	var entityType = $("#entityType").val();
	if("PARTY_ENTITY" == entityType){
		var roleType = $("#roleTypeId").val();
		if(roleType == null || roleType == "") {
	        $('#roleTypeId_error').append('<ul class="list-unstyled"><li>Please select an role type</li></ul>');
	        flag = "N";
	    } else{
	        $("#roleTypeId_error").empty();
	        $('#roleTypeId_error').html("");
	    }
	}
	
  	if("Y" == flag){
        return true;
    } 
    else {
        return false;
    }
}

$(document).ready(function() {
  $('.operations').change(function() {
  var operations = $("input[name='operations']:checked").map(function() {return this.value;}).get().join(',');
    if(operations == null || operations == "" || operations == 'undefined') {
        $('#operations_error').html("Please select atleast one operation");
        return false;
    } else {
        $('#operations_error').html("");
        return true;
    }
  });
  $("#entityName").change(function() {
     $("#entityName_error").empty();
     if($(this).val() == null || $(this).val() == "") {
        $("#entityName_error").css('display','block');
        $("#entityName_error").append('<ul class="list-unstyled"><li>Please select an Entity</li></ul>');
     } else {
        $("#entityName_error").css('display','none');
     }
  }); 
  
  var entityType = $("#entityType").val();
  if("PARTY_ENTITY" == entityType){
  	$("#partyEntityRoleType").css('display','block');
  	$("#roleTypeId").prop('required',true);
  	$("#roleTypeId_label").html('RoleType <span class="text-danger"> *</span>');
  }
  $("#entityType").change(function() {
 	 $("#entityAliasName").prop('readonly',false);
     $("#roleTypeId_error").empty();
     $("#partyEntityRoleType").css('display','none');
     if($(this).val() == null || $(this).val() == "") {
        $("#roleTypeId_error").css('display','block');
        $("#roleTypeId_error").append('<ul class="list-unstyled"><li>Please select an role type </li></ul>');
     } else {
        $("#entityType_error").css('display','none');
        if("PARTY_ENTITY" == $(this).val()){
		  	$("#partyEntityRoleType").css('display','block');
		  	$("#roleTypeId").prop('required',true);
  			$("#roleTypeId_label").html('RoleType <span class="text-danger"> *</span>');
		} else {
			$("#roleTypeId").prop('required',false);
		}
     }
  }); 
  
  $("#roleTypeId").change(function() {
  	$("#entityAliasName").val("");
  	var roleTypeDesc = $( "#roleTypeId option:selected" ).text();
  	if($(this).val() != null && $(this).val() != "" && $(this).val() != "undefined"){
  		$("#entityAliasName").val(roleTypeDesc);
  	}
  	$("#entityAliasName").prop('readonly',true);
  });
  
});
</script>