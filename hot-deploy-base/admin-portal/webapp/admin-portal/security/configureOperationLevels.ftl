<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<div class="row">
    <div id="main" role="main">
       <#assign extra = '<a href="findSecurityRole" class="btn btn-xs btn-primary"><i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>' />
      

       <div class="clearfix"></div>
       <div class="col-lg-12 col-md-12 col-sm-12 dash-panel">
            <@sectionFrameHeaderTab title="${uiLabelMap.EntityOperations}" tabId="ConfigureOperationLevels" extra=extra!/> 
       <#assign roleType = (Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("description").from("RoleType").where("roleTypeId",requestParameters.roleTypeId!).queryOne())?if_exists />
       <#assign extra ='<button type="button" class="btn btn-sm btn-primary mt-2" onclick="javascript:prepareData();"> ${uiLabelMap.Save!}</button>' />
       <@viewSectionHeader
            title="${uiLabelMap.Permissions!}:${uiLabelMap.Information!}"
            title1="${roleType?if_exists.description!}"
            extra=extra!
            />
            
            <#assign entityOpsEnum = Static["org.fio.admin.portal.util.EnumUtil"].getEnums(delegator, "ENTITY_OPERATIONS")?if_exists />
            <#assign entityOpsList = Static["org.fio.admin.portal.util.DataHelper"].getDropDownOptions(entityOpsEnum, "enumId","description")?if_exists />  
          <div class="table-responsive">
            <form name="configureOpLevelForm" id="configureOpLevelForm" action="configureOperationLevelService" method="post" >
               <@inputHidden 
                   id="roleTypeId" 
                   value="${requestParameters.roleTypeId!}"
                   />
             <table class="table table-bodered table-hover table-striped">
                <thead>
                   <tr>
                      <th >Entity</th>
                      <#if entityOpsList?has_content>
                          <#list entityOpsList.entrySet() as entry>
                             <th class="text-center" width="8%">${entry.value!}</th>
                          </#list>
                      </#if>
                   </tr>
                </thead>
                <tbody>
                   
                   <#if entityConfigureMap?has_content>
                   <#assign count = 0>
                          <#list entityConfigureMap.entrySet() as entry>
                              <tr>
                                 <td>
                                 <#assign eName = entry.key! />
                                 <#assign entityAliasName = (Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).select("entityAliasName","entityType","roleTypeId").from("EntityOperationConfig").where("entityName",eName).queryFirst())?if_exists />
                                    <#-- ${entry.key!} -->
                                    ${entityAliasName.entityAliasName!eName}
                                    <@inputHidden 
                                        id="entityName_o_${count}"
                                        value="${entry.key!}" 
                                        />
                                 </td>
                                 
                                  <#if entityOpsList?has_content>
                                      <#list entityOpsList.entrySet() as allOps>
                                         <#assign entityOperation = allOps.value! />
                                            <#assign exists = false />
                                            <#assign isEnabled = "N" />
                                            <#list entry.value as operation >
                                                <#assign entityOps = operation.operationName?if_exists />
                                                <#assign isEnabled = operation.isEnabled?if_exists />
                                                <#if entityOperation == entityOps>
                                                   <#assign exists = true />
                                                </#if>
                                            </#list>
                                            <#assign imgValue =1 />
                                            <#assign oplevel ="L1" />
                                            <#if securityLevelMap?has_content>
                                                <#assign entitySecurity = securityLevelMap.get(entry.key!)?if_exists/>
                                                <#list entitySecurity! as configuredLevel>
                                                    <#if entityOperation == configuredLevel.operationName>
                                                        <#assign imgValue=configuredLevel.imgValue! />
                                                        <#assign oplevel=configuredLevel.oplevel! />
                                                    </#if>
                                                </#list>
                                            </#if>
                                            
                                             <#if exists>
                                                <td class="text-center px-3 py-1">
                                                  <#if isEnabled=="N">
                                                    <#-- <img id="${entry.key!}_${entityOperation}" value="${imgValue!'1'}" onclick="#" src="/bootstrap/images/type-1.png" width="16" height="16" class="none-events noselect" > -->  
                                                  <#else>
                                                  <@dropdownCell
                                                        id="${entityOperation?replace(' ','')?lower_case}_o_${count}"
                                                        options=levelOptions!
                                                        value="${oplevel!L1}"
                                                        allowEmpty=false
                                                        dataLiveSearch= false
                                                        />
                                                    <#-- <img id="${entry.key!}_${entityOperation?replace(' ','_')}" value="${imgValue!'1'}" onclick="permissionAction('${entry.key!}_${entityOperation?replace(' ','_')}','${entityOperation?replace(' ','')?lower_case}_o_${count}');" src="${levelImage?if_exists.get(imgValue)!'/bootstrap/images/type-1.png'}" width="16" height="16" class="cursor-pointer noselect" >
                                                    <@inputHidden 
                                                       id="${entityOperation?replace(' ','')?lower_case}_o_${count}"
                                                       value="${oplevel!L1}"
                                                    /> -->
                                                  </#if>
                                                </td>
                                             <#else>
                                                <td class="text-center p-1">
                                                  <#--  <img id="${entry.key!}_${entityOperation}" value="${imgValue!'1'}" onclick="#" src="/bootstrap/images/type-1.png" width="16" height="16" class="none-events noselect">-->
                                                </td>
                                             </#if>
                                             
                                      </#list>
                                  </#if>
                                 
                              </tr>
                              <#assign count = count+1>
                          </#list>
                       </#if>
                </tbody>
             </table>
             <div class="text-right"> <button type="button" class="btn btn-sm btn-primary mb-2 " onclick="window.location.href='javascript:prepareData()';"> Save</button>                
             </div>
             </form>
          </div>
          <div class="col-md-12 col-lg-12 bg-light pb-2">
             <div class="pt-2">
                <@headerH2 
                    title="${uiLabelMap.PermissionLevel!}" 
                    />
                <div class="clearfix"></div>
             </div>
             <div class="col-md-12 col-lg-12">
                <div class="row">
                    <#assign opsLevelEnum = Static["org.fio.admin.portal.util.EnumUtil"].getEnums(delegator, "OPERATION_LEVELS")?if_exists />
                    <#assign opsLevelList = Static["org.fio.admin.portal.util.DataHelper"].getDropDownOptions(opsLevelEnum, "enumId","description")?if_exists /> 
                
                    <#if opsLevelList?has_content>
                      <#list opsLevelList.entrySet() as level>
                        <div class="col-sm-2">
                         <div class="row">
                         <#-- <#if "L1" == level.key!>
                            <div class="col-1"><img src="/bootstrap/images/type-1-red.png" width="16" height="16" /></div>
                         <#elseif "L2" == level.key!>
                            <div class="col-1"><img src="/bootstrap/images/type-2-yellow.png" width="16" height="16" /></div>
                         <#elseif "L3" == level.key!>
                            <div class="col-1"><img src="/bootstrap/images/type-3-yellow.png" width="16" height="16" /></div>
                         <#elseif "L4" == level.key!>
                            <div class="col-1"><img src="/bootstrap/images/type-4-green.png" width="16" height="16" /></div>
                         <#elseif "L5" == level.key!>
                            <div class="col-1"><img src="/bootstrap/images/type-5-green.png" width="16" height="16" /></div>
                         <#elseif "L6" == level.key!>
                            <div class="col-1"><img src="/bootstrap/images/type-6-red.png" width="16" height="16" /></div>
                         <#else>
                         </#if> -->
                          <div class="col-10">${level.key!} - ${level.value!}</div>
                         </div>
                        </div>
                      </#list>
                    </#if>
                    <#--
                    <div class="col-sm-2">
                       <div class="row">
                          <div class="col-1"><img src="/bootstrap/images/type-1.png" width="16" height="16" /></div>
                          <div class="col-10">${uiLabelMap.Disabled!}</div>
                       </div>
                    </div> -->
                </div>
             </div>
          </div>
       </div>
    </div>
</div>
<script src="/bootstrap/js/buddy-setup.js"></script>
<script>
function prepareData(){
    $('#configureOpLevelForm').submit();
}
</script>
