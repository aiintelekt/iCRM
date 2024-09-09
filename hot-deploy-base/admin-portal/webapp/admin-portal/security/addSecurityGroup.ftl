<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<div class="row">
   <div id="main" role="main">
      <#assign extra='<a href="viewSecurityRole?roleTypeId=${requestParameters.roleTypeId!}" class="btn btn-xs btn-primary">
      <i class="fa fa-chevron-circle-left" aria-hidden="true"></i> Back</a>'/>
      <@sectionFrameHeader
      title="${uiLabelMap.ViewSecurityRole!}"
      extra=extra?if_exists
      />
      <div class="alert alert-danger" role="alert" id="modal-error" style="display:none;">
         <strong>Error : </strong> Please select atleast one security group
      </div>
      <div class="table-responsive">
         <div class="loader text-center" id="loader" sytle="display:none;">
            <span></span>
            <span></span>
            <span></span>
         </div>
         <div id="grid" style="width: 100%;" class="ag-theme-balham"></div>
         <@inputHidden id="roleTypeId" name="roleTypeId" value="${requestParameters.roleTypeId!}"/>
         <#--  <@AgGrid
         userid="${userLogin.userLoginId}"
         instanceid="SR04"
         styledimensions='{"width":"100%","height":"auto"}'
         autosave="false"
         autosizeallcol="true"
         debug="true"
         buttonbarbuttons='[{"label":"Add", "clickEventId":"add-selected", "styleClass":"btn btn-primary btn-xs ml-2"}, {"label":"Clear Selected", "clickEventId":"clear-selected", "styleClass":"btn btn-primary btn-xs ml-2"}]'
         requestbody='{"roleTypeId":"${requestParameters.roleTypeId!}"}'
         /> -->
         <script type="text/javascript" src="/admin-portal-resource/js/ag-grid/security/add-security-role.js"></script>
      </div>
      <form method="post" action="createRoleSecurityAssoc" id="addSecurityGroupForm" name="addSecurityGroupForm" novalidate="true" data-toggle="validator">
         <@inputHidden
         id="modalId"
         value="addSecurityGroup"/>
         <@inputHidden
         id="roleTypeId"
         value="${requestParameters.roleTypeId!}"
         />
         <@inputHidden
         id="groupId"
         value=""
         />
         <@inputHidden
         id="groupIds"
         value=""
         />
      </form>
   </div>
</div>
</div>