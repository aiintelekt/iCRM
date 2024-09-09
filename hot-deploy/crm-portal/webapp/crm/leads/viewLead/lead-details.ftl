<#include "component://lms-mobile/webapp/lms-mobile/lib/mobileMacros.ftl"/>

  <div class="col-form-label" id="Details-tab" role="tabpanel" aria-labelledby="nav-contact-tab">
  <div class="float-right mb-1">
	<div class="accordion-option"> 
	    <a href="javascript:void(0)" class="toggle-accordion btn-toggle btn btn-xs btn-primary" id="allText" accordion-id="#panelAccordion"></a>
  	</div>
      <a href="editLead?leadId=${partySummary?if_exists.partyId?if_exists}">
        <span class="fa fa-edit btn btn-xs btn-primary" data-toggle="modal" data-target="" alt="${uiLabelMap.edit}" title="${uiLabelMap.edit}"></span>
      </a>
      <a href="reAssignLead?leadId=${leadId!}" class="btn btn-xs btn-primary">Re-assign Lead</a>
  </div>
  <div class="clearfix"></div>
  <div class="panel-group" id="panelAccordion" role="tablist" aria-multiselectable="true"><#-- id="accordion" -->
    <div class="panel panel-default">
      <div class="panel-heading m15" role="tab" id="headingOne">
        <div class="panel-title">
        <a role="button" data-toggle="collapse" data-parent="#panelAccordion" href="#collapseOne" aria-expanded="false" aria-controls="collapseOne">
          Company details        </a>
      </div>
      </div>
      <div id="collapseOne" class="panel-collapse collapse in" role="tabpanel" aria-labelledby="headingOne">
        <div class="panel-body">
        <@contentBox  id="leadSource"  label="Lead Source" value=(createSource.description)! formclass="pb-2" labelclass=""/>
        <@contentBox  id="industry"  label="Industry" value=(industry.description)! formclass="pb-2" labelclass=""/>
        <@contentBox  id="constitution"  label="Constitution" value=(constitution.description)! formclass="pb-2" labelclass=""/>
        <@contentBox  id="dateOfIncorporation"  label="Date of incorporation" value=leadData.dateOfIncorporation formclass="pb-2" labelclass=""/>
        <@contentBox  id="noOfEmployees"  label="No. of Employees" value=leadData.noOfEmployees formclass="pb-2" labelclass=""/>
        <@contentBox  id="salesTurnover"  label="Sales turnover" value=leadData.salesTurnover formclass="pb-2" labelclass=""/>
        <@contentBox  id="tcpUser"  label="TCP user" value=(tallyUserType.description)! formclass="pb-2" labelclass=""/>
        <@contentBox  id="preferredLanguages"  label="Preferred Language" value=(preferredLanguages.description)! formclass="pb-2" labelclass=""/>
        <@contentBox  id="paidUpCapital"  label="Paid Up Capital" value=leadData.paidupCapital formclass="pb-2" labelclass=""/>
        <@contentBox  id="gstn"  label="GSTN" value=partyAttrs.gstn formclass="pb-2" labelclass=""/>
        <@contentBox  id="iecCode"  label="IEC Code" value=partyAttrs.iecCode formclass="pb-2" labelclass=""/>
        <@contentBox  id="cin"  label="CIN" value=partyAttrs.cin formclass="pb-2" labelclass=""/>
        <@contentBox  id="companyPan"  label="Company Pan" value=leadData.permanentAcccountNumber formclass="pb-2" labelclass=""/>

        </div>
      </div>
    </div>
    <div class="panel panel-default">
      <div class="panel-heading m15" role="tab" id="headingTwo">
        <div class="panel-title">
        <a class="collapsed" role="button" data-toggle="collapse" data-parent="#panelAccordion" href="#collapseTwo" aria-expanded="false" aria-controls="collapseTwo">
          Company contact details
        </a>
      </div>
      </div>
      <div id="collapseTwo" class="panel-collapse collapse in" role="tabpanel" aria-labelledby="headingTwo">
        <div class="panel-body">
          <div class="form-group row">
            <div class="col-10 text-dark pt-2 pb-2"><span class="text-secondary">Main line 1</span><br/> ${primaryPhone!}</div>
            <#if primaryPhone?has_content && primaryPhoneMechId?has_content>
            	<div class="col-2 text-dark text-right"><i class="fa fa-phone display-2 text-success" aria-hidden="true"></i></div>
            </#if>
          </div>
		  <#list aoAddress as address>
		  <div class="form-group row">
			<div class="col-12">									
				<div class="text-secondary">Address 1</div>
		     	<div><@nullChecked value=address.address1/> _ <@nullChecked value=address.address2/></div>
			 	<div class="text-secondary">City</div>
		 		<div><#if address.city?has_content>
          				${geoMap[address.city]} 
         			 </#if></div>
				<div class="text-secondary">State</div>
		 		<div><#if address.stateProvinceGeoId?has_content>
          				${geoMap[address.stateProvinceGeoId]} 
         			 </#if></div>
		 		<div class="text-secondary">PIN Code</div>
		 		<div><@nullChecked value=address.postalCode/></div>
			</div>
		  </div>
        </#list>
        </div>
      </div>
    </div>
    <div class="panel panel-default">
      <div class="panel-heading m15" role="tab" id="headingThree">
        <div class="panel-title">
        <a class="collapsed" role="button" data-toggle="collapse" data-parent="#panelAccordion" href="#collapseThree" aria-expanded="false" aria-controls="collapseThree">
          Existing bank details
        </a>
      </div>
      </div>
      <div id="collapseThree" class="panel-collapse collapse in" role="tabpanel" aria-labelledby="headingThree">
        <div class="panel-body">
    
         <#list banksLists as bank>
	    		<div class="form-group row">
	                <div class="col-1"><i class="fa fa-university" aria-hidden="true"></i></div>
	                <div class="col-9">
	                  <div class=""><span class="text-secondary">Bank name</span></div>
	                  <span class="mb-3">${bank.bankName}</span>
	                  <div class=""><span class="text-secondary">Facilities with other banks</span></div>
	                  <#list bank.banksProductsList as facility> 
	                  	<div class="mb-1">${facility.productName!} - &#8377;${facility.productValue!}</div>
	                  </#list>
	                </div>	                
	              </div>
		  </#list>
           <#if banksLists?size == 0>
            <div class="form-group row">
				<div class="col-9">
					<div class="mb-1">No Banks </div>
				</div>	
            </div>
 		</#if>
	
        </div>
      </div>
    </div>
	  <div class="panel panel-default">
      <div class="panel-heading m15" role="tab" id="headingFour">
        <div class="panel-title">
        <a class="collapsed" role="button" data-toggle="collapse" data-parent="#panelAccordion" href="#collapseFour" aria-expanded="false" aria-controls="collapseFour">
          Lead assignment
        </a>
      </div>
      </div>
      <div id="collapseFour" class="panel-collapse collapse in" role="tabpanel" aria-labelledby="headingFour">
        <div class="panel-body">
        
          <@contentBox  id="leadAssignedFrom"  label="Lead assigned from" value=assignByName formclass="pb-2" labelclass=""/>
          <@contentBox  id="leadAssignedTo"  label="Lead assigned to" value=assignToName formclass="pb-2" labelclass=""/>
        <#--   
          <div class="form-group row">
		<div class="col-12">									
		<div class="small text-secondary">Lead assigned from</div>
      Source name goes here
        </div>
        </div>
        
		<div class="form-group row">
		<div class="col-12">									
		<div class="small text-secondary">Lead assigned to</div>
      RM name goes here
        </div>
        </div>
         -->
        </div>
      </div>
    </div>
  </div>
</div>
