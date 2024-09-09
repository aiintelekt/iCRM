
<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<div class="col-lg-12 col-md-12 col-sm-12">
	<div class="row pt-2">
    	<div class="col-md-12 col-lg-6 col-sm-12 ">
 			<form name="" id="" action="" method="post">             
			  <@headerH4
			   title="Card Details"
			  />
			  
			  <@dropdownCell
			    id="sertype"
			    label="Service Type"
			    required=false
			  />
			  <@inputRow
			     id="login-username"
			     type="text"
			     name="username"
			     label="Embossing Name"
			     value=""
			     placeholder="Embossing Name"
			   />
			   <@inputRow
			     id="login-username"
			     type="text"
			     name="username"
			     label="CMS Logo 1"
			     value=""
			     placeholder="CMS Logo 1"
			   />
			   <@inputRow
			     id="login-username"
			     type="text"
			     name="username"
			     label="Product Code 1"
			     value=""
			     placeholder="Product Code 1"
			   />
			   <@inputRow
			     id="login-username"
			     type="text"
			     name="username"
			     label="CMS Logo 2"
			     value=""
			     placeholder="CMS Logo 2"
			   />
			   <@inputRow
			     id="login-username"
			     type="text"
			     name="username"
			     label="Product Code 2"
			     value=""
			     placeholder="Product Code 2"
			   />
			   <@inputRow
			     id="login-username"
			     type="text"
			     name="username"
			     label="CMS Logo 3"
			     value=""
			     placeholder="CMS Logo 3"
			   />
			   <@inputRow
			     id="login-username"
			     type="text"
			     name="username"
			     label="Product Code 3"
			     value=""
			     placeholder="Product Code 3"
			   />
			   <@inputRow
			     id="login-username"
			     type="text"
			     name="username"
			     label="CMS Logo 4"
			     value=""
			     placeholder="CMS Logo 4"
			   />
			   <@inputRow
			     id="login-username"
			     type="text"
			     name="username"
			     label="Product Code 4"
			     value=""
			     placeholder="Product Code 4"
			   />
 		</div>
 
 		<div class="col-md-12 col-lg-6 col-sm-12">
 
		  <@headerH4
		   title="Agent Details"
		  />
		  <@inputRow
		     id="login-username"
		     type="text"
		     name="username"
		     label="Agent code"
		     value=""
		     placeholder="Agent code"
		   />
		   <@inputRow
		     id="login-username"
		     type="text"
		     name="username"
		     label="Agent Name"
		     value=""
		     placeholder="Agent Name"
		   />
		   <@headerH4
		   title="Other Information"
		   />
		   <@inputRow
		     id="login-username"
		     type="text"
		     name="username"
		     label="Campaign code"
		     value=""
		     placeholder="Campaign code"
		   />
		   <@inputRow
		     id="login-username"
		     type="text"
		     name="username"
		     label="Source Name"
		     value=""
		     placeholder="Source Name"
		   />
   		<div class="clearfix"></div>
   		<div class="offset-sm-8 col-sm-6">
    		<@submit label="Save"/> <@reset label="Clear"/>
   		</div>
  </div>
 </form>
</div>
</div>
 
