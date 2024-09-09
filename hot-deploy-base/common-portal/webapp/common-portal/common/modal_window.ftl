<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<#-- <#assign requestURI = "viewContact"/>
<#if request.getRequestURI().contains("viewLead")>
<#assign requestURI = "viewLead"/>
<#elseif request.getRequestURI().contains("viewAccount")>
<#assign requestURI = "viewAccount"/>
<#elseif request.getRequestURI().contains("viewCustomer")>
<#assign requestURI = "viewCustomer"/>
</#if>-->

<#assign requestURI="${requestURI!}">
<#if !requestURI?has_content>
<#assign requestURI = request.getRequestURI()/>
<#if requestURI.contains("screenRender")>
<#assign requestURI=request.getParameter("requestUri")!>
</#if>
</#if>
<#if requestURI?has_content>
<#if requestURI?contains("viewLead")>
<#assign requestURI = "viewLead"/>
<#elseif requestURI?contains("viewAccount")>
<#assign requestURI = "viewAccount"/>
<#elseif requestURI?contains("viewCustomer")>
<#assign requestURI = "viewCustomer"/>
<#else>
<#assign requestURI = "viewContact"/>
</#if>
</#if>