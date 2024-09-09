<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<#include "component://crm/webapp/crm/common/modalNoteCreate.ftl">
<#include "component://crm/webapp/crm/common/writeEmail.ftl" />
<#include "component://crm/webapp/crm/common/findTeamMembersModal.ftl" />
<#include "component://crm/webapp/crm/common/createLogCall.ftl" />
<div class="row">
    <div id="main" role="main">    
	   <div class="top-band bg-light">
		   <p class="float-right mr-2 pb-2">
	          <#if notesList?has_content>
	              <#list notesList as note>
	                  <#if note.isImportant?if_exists = 'Y'>
	                      <span class="fa fa-sticky-note btn btn-xs btn-danger tooltips" data-toggle="modal" data-target="#noteUpdate" data-original-title="${uiLabelMap.editNote}"></span>
	                  </#if>
	              </#list>
	           </#if>
	          <#--<span class="fa fa-sticky-note btn btn-xs btn-primary tooltips" data-toggle="modal" data-target="#noteCreateUpdate" data-original-title="${uiLabelMap.createNote}"></span>-->
		      <#--<span class="glyphicon glyphicon-comment btn btn-xs btn-primary tooltips" data-toggle="modal" data-target="#myModal"></span>-->
		      <span class="glyphicon glyphicon-earphone btn btn-xs btn-primary tooltips" data-toggle="modal" data-target="#callLogModal"  data-original-title="${uiLabelMap.logCall}"></span>
		      <span class="glyphicon glyphicon-envelope btn btn-xs btn-primary tooltips" data-toggle="modal" data-target="#writeEmailModal" data-original-title="${uiLabelMap.writeEmail}"></span>
		   </p>
	       <div class="ml-2">
	          <h1 class="float-left">View Account -  ${partySummary?if_exists.groupName?if_exists} </h1>&nbsp;
	          <#if primaryContactInformation?exists && primaryContactInformation?has_content>
	              <#if primaryContactInformation.PrimaryPhone?exists && primaryContactInformation.PrimaryPhone?has_content>
	                  <a href="#" class="btn btn-xs btn-success "><span class="glyphicon glyphicon-earphone">&nbsp;${primaryContactInformation.PrimaryPhone?if_exists}</span></a>
	              </#if>
	          </#if>
	          <#if primaryContactInformation?exists && primaryContactInformation?has_content>
	              <#if primaryContactInformation.EmailAddress?exists && primaryContactInformation.EmailAddress?has_content>
	                  <a href="#" class="btn btn-xs btn-success "><span class="glyphicon glyphicon-envelope">&nbsp;${primaryContactInformation.EmailAddress?if_exists}</span></a>
	              </#if>
	          </#if>  
	       </div>
		</div>
	   <#-- Display the tabs-->
	   <div class="nav-tabs mx-2"> 
	   <#include "component://crm/webapp/crm/common/tabMenu.ftl"/>
	   </div>   
	   <div class="tab-content mx-2">
	      <!--Details tab start-->
	      <div id="details" class="tab-pane fade">
	         ${screens.render("component://crm/webapp/widget/crm/screens/account/AccountScreens.xml#viewAccountDetails")}
	      </div>
	      <!--Details tab end-->
	      <div id="contactInfo" class="tab-pane fade">
	         ${screens.render("component://crm/webapp/widget/crm/screens/account/AccountScreens.xml#contactInfo")}
	      </div> 
	      <!-- contact info tab end-->
	      
	   	  <div id="hadoop" class="tab-pane fade">
	         ${screens.render("component://crm/webapp/widget/crm/screens/hadoop/HadoopScreens.xml#HadoopData")}
	      </div> 
	      
	      <!--Order Details tab Start-->
	      <div id="orderDetails" class="tab-pane fade">
	         ${screens.render("component://crm/webapp/widget/crm/screens/account/AccountScreens.xml#orderDetails")}
	      </div> 
	      
	     <div id="loanDetails"  class="tab-pane fade">		
			${screens.render("component://crm/webapp/widget/crm/screens/account/AccountScreens.xml#loanDetails")}
		 </div>
		<div id="accountDetails"  class="tab-pane fade">		
			${screens.render("component://crm/webapp/widget/crm/screens/account/AccountScreens.xml#accountDetails")}
		</div>
	      <!-- Order Details tab end-->
	      <div id="customFields" class="tab-pane fade">
	         ${screens.render("component://crm/webapp/widget/crm/screens/account/AccountScreens.xml#customFields")}
	      </div>
	      <!--custom Field tab end-->
	      
	      <div id="contact" class="tab-pane fade">
	         ${screens.render("component://crm/webapp/widget/crm/screens/account/AccountScreens.xml#accountContact")}
	      </div>
	      <!-- Contact tab end-->
	      
	      <div id="opportunites" class="tab-pane fade">
	         ${screens.render("component://crm/webapp/widget/crm/screens/account/AccountScreens.xml#opportunites")}
	      </div>
	      <!--Opportunities tab end-->
	      
	      <div id="notes" class="tab-pane fade">
	         ${screens.render("component://crm/webapp/widget/crm/screens/account/AccountScreens.xml#note")}
	      </div>
	      <!-- Note tab end-->
	      <div id="logCall" class="tab-pane fade">
	         ${screens.render("component://crm/webapp/widget/crm/screens/common/CommonScreens.xml#callDetails")}
	      </div>
	      <#-- <div id="search" class="tab-pane fade">
	         ${screens.render("component://crm/webapp/widget/crm/screens/account/AccountScreens.xml#search")}
	      </div> -->
	      <!-- Search tab end -->
	      
	      <div id="campaignDetails" class="tab-pane fade">
	         ${screens.render("component://crm/webapp/widget/crm/screens/account/AccountScreens.xml#campaignDetails")}
	      </div>
	      <!--Campaign Details tab end-->
	      <div id="formValue" class="tab-pane fade">
	         ${screens.render("component://crm/webapp/widget/crm/screens/account/AccountScreens.xml#formValue")}
	      </div>
	      <!--Form Values tab end-->
	      <div id="segmentation" class="tab-pane fade">
	         ${screens.render("component://crm/webapp/widget/crm/screens/account/AccountScreens.xml#segmentationDetails")}
	      </div>
	      <!-- Segmentation tab end-->
	      
	      <div id="economicsMetrics" class="tab-pane fade">
	         ${screens.render("component://crm/webapp/widget/crm/screens/account/AccountScreens.xml#EconomicsMetrics")}
	      </div>
      </div>
      </div><#-- End main-->
</div><#-- End row-->
<!-- /.container -->
${screens.render("component://crm/webapp/widget/crm/screens/account/AccountScreens.xml#modalPop")}	
    
<#--
<div id="myModal" class="modal fade" role="dialog">
   <div class="modal-dialog">
      <div class="modal-content">
         <div class="modal-header">
            <h4 class="modal-title">Modal Header</h4>
            <button type="reset" class="close" data-dismiss="modal">&times;</button>
         </div>
         <div class="modal-body">
            <p>Some text in the modal.</p>
         </div>
         <div class="modal-footer">
            <button type="reset" class="btn btn-sm btn-primary navbar-dark" data-dismiss="modal">Close</button>
         </div>
      </div>
   </div>
</div>
-->