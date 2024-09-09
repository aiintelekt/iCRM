<#--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->

<html>
<head>
	<script type="text/javascript" language="javascript" src="/bootstrap/js/jquery.min.js" crossorigin="anonymous"> </script> 
    <script type="text/javascript" language="javascript" src="/bootstrap/js/jquery-ui.js"></script>
</head>
<body>
  <div>${uiLabelMap.SecurityExtThisEmailIsInResponseToYourRequestToHave} <#if useEncryption>${uiLabelMap.SecurityExtANew}<#else>${uiLabelMap.SecurityExtYour}</#if> ${uiLabelMap.SecurityExtPasswordSentToYou}.</div>
  <br />
  <#-- <div> Temporary password: ${password!} -->
  <div>
  	<#-- 
  	<#assign url = request.getRequestURL()! />
    <#assign requestUrl = url?split('/')?chunk(3)[0]?join("/") /> -->
      <form method="post" action="#" name="passwordResetForm" id="passwordResetForm">
      <#--form method="post" action="${baseEcommerceSecureUrl}/partymgr/control/passwordChange" name="loginform" id="loginform" target="_blank">
        <input type="hidden" name="USERNAME" value="${userLogin.userLoginId!}" />
        <input type="hidden"  name="password" value="${password!}" />
        <input type="hidden"  name="tenantId" value="${tenantId!}" />
        <input type="hidden" name="forgotPwdFlag" value="true" /--><#-- see OFBIZ-4983 -->
      	<a href="${redirectUrl!}/admin-portal/control/passwordChange?USERNAME=${userLogin.userLoginId!}&forgotPwdFlag=true&password=${password!}">Click Here </a> to reset your password
      </form>
  </div>
</body>
</html>
<script>
	function submitForm(){
		$("#passwordResetForm").submit();
	}
</script>
