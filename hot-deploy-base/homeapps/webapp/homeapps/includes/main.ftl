<#if userLogin?has_content>
<nav class="navbar navbar-expand-lg navbar-dark fixed-top">      	  
   <div id="navigation-bar" class="navigation-bar" >
      <div class="bar">
        <button id="navbox-trigger" class="navbox-trigger">
        <span class="glyphicon glyphicon-th"></span>
        </button>
		</div>
      <div class="navbox">
        <div class="navbox-tiles">
	<#--	<a href="/crm/control/main?${StringUtil.wrapString(externalKeyParam)}" class="tile">
          <img class="" src="/bootstrap/images/crm.png" alt="CRM, Customer Service, Order Entry, Marketing" title="CRM, Customer Service, Order Entry, Marketing">
          <span class="tiletxt"> CRM </span>
          </a>              
          <#--<a href="/cms/control/main?${StringUtil.wrapString(externalKeyParam)}" class="tile">
          <img class="" src="/bootstrap/images/content-management.png" alt="Content management System" title="Content management System">
          <span class="tiletxt">Content Management </span>
          </a>
          <a href="/campaign/control/main?${StringUtil.wrapString(externalKeyParam)}" class="tile">
          <img class="" src="/bootstrap/images/campaign-manager.png" alt="Retail Marketing Solutions" title="Retail Marketing Solutions">
          <span class="tiletxt"> Campaign Manager </span>
          </a>
          <a href="/custom-field/control/main?${StringUtil.wrapString(externalKeyParam)}>" class="tile">
          <img class="" src="/bootstrap/images/customer-segmentation.png" alt="Customer Segmentation" title="Customer Segmentation">
          <span class="tiletxt"> Segmentation </span>
          </a>
          <a href="/partymgr/control/main?${StringUtil.wrapString(externalKeyParam)}" class="tile">
          <img class="" src="/bootstrap/images/user-management.png" alt="Parties and Users" title="Parties and Users">
          <span class="tiletxt"> User Management </span>
          </a>
          <a href="/data-importer/control/main?${StringUtil.wrapString(externalKeyParam)}>" class="tile">
          <img class="" src="/bootstrap/images/data-import.png" alt="Data Importer" title="Data Importer">
          <span class="tiletxt"> Data Importer </span>
          </a>
          -->
          <#if componentAccess?exists && componentAccess?has_content>
          <#list componentAccess as components>
            
            <a href="${components.requestURI!}?${StringUtil.wrapString(externalKeyParam)}>" class="tile">
              <img class="" src="${components.imageURL!}" alt="${components.uiLabels!}" title="${components.uiLabels!}">
               <span class="tiletxt"> ${components.uiLabels!} </span>
            </a>
           </#list>
         </#if>
        </div>
      </div>
    </div>			
   <a class="navbar-brand navbar-brand-tit  mr-auto" href="#">Dashboard</a>
   <h1 class="float-right">${userName?if_exists}</h1>
    <ul class="navbar-nav">
        <li class="nav-item dropdown">
        <a class="nav-link dropdown-toggle" href="" id="dropdown05" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"><span class="glyphicon glyphicon-cog">
          </span></a>     
          <ul class="dropdown-menu dropdown-menu-right" aria-labelledby="dropdown05">
          <a class="dropdown-item" href="#">Profile</a>
          <a class="dropdown-item" href="#">Help</a>
          <a class="dropdown-item" href="<@ofbizUrl>logout</@ofbizUrl>">${uiLabelMap.CommonLogout}</a>
        </ul>
        </li>
      </ul>		   
  </nav>
  <div class="">
  <div class="container-fluid">
    <div class="row mt-3">
      <#--<div class="col-sm-6 col-lg-4">
        <div class="prodetails">
          <a href="/crm/control/main?${StringUtil.wrapString(externalKeyParam)}">
            <div class="pro-img"><img src="/bootstrap/images/crm.png"></div>
            <div class="pro-heading">CRM </div>
            <div class="products">Leads, Person, Contacts, Accounts, Tickets, Activities, Opportunities, Teams, Configuration, Reports</div>
          </a>
        </div>
      </div>
      <#--<div class="col-sm-6 col-lg-4">
        <div class="prodetails">
          <a href="/cms/control/main?${StringUtil.wrapString(externalKeyParam)}">
            <div class="pro-img"><img src="/bootstrap/images/content-management.png"></div>
            <div class="pro-heading">Content Management </div>
            <div class="products">Catalog, Category, Product, Feature, Price Books, Price Rules, Store Group, Stores, Shipping, Filters, Configuration </div>
          </a>
        </div>
      </div> 
      <div class="col-sm-6 col-lg-4">
        <div class="prodetails">
          <a href="/campaign/control/main?${StringUtil.wrapString(externalKeyParam)}">
            <div class="pro-img"><img src="/bootstrap/images/campaign-manager.png"></div>
            <div class="pro-heading">Campaign Manager</div>
            <div class="products"> Retail Marketing Solutions</div>
          </a>
        </div>
      </div>
      <div class="col-sm-6 col-lg-4">
        <div class="prodetails">
          <a href="/custom-field/control/main?${StringUtil.wrapString(externalKeyParam)}">
            <div class="pro-img"><img src="/bootstrap/images/customer-segmentation.png"></div>
            <div class="pro-heading">Segmentation </div>
            <div class="products">Customer Segmentation </div>
          </a>
        </div>
      </div>
      <div class="col-sm-6 col-lg-4">
        <div class="prodetails">
          <a href="/partymgr/control/main?${StringUtil.wrapString(externalKeyParam)}">
            <div class="pro-img"><img src="/bootstrap/images/user-management.png"></div>
            <div class="pro-heading">User Management </div>
            <div class="products">Parties and Users </div>
          </a>
        </div>
      </div>
      <div class="col-sm-6 col-lg-4">
        <div class="prodetails">
          <a href="/data-importer/control/main?${StringUtil.wrapString(externalKeyParam)}">
            <div class="pro-img"><img src="/bootstrap/images/data-import.png"></div>
            <div class="pro-heading">Data Importer </div>
            <div class="products">Data Importer </div>
          </a>
        </div>
      </div> -->
      <#if componentAccess?exists && componentAccess?has_content>
        <#list componentAccess as components>
            <div class="col-sm-6 col-lg-4">
              <div class="prodetails">
                <a href="${components.requestURI!}?${StringUtil.wrapString(externalKeyParam)}">
                <div class="pro-img"><img src="${components.imageURL!}"></div>
                <div class="pro-heading">${components.uiLabels!}</div>
                <div class="products">${components.description!} </div>
              </a>
            </div>
          </div>
        </#list>
      </#if>
    </div>
  </div>
</div>
</#if>
