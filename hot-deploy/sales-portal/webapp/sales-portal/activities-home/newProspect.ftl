<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>

<div class="row">
   <div id="main" role="main">
     <@sectionFrameHeader title="${uiLabelMap.newProspect}" />
        <form method="post" action="#" id="task" class="form-horizontal" name="task" novalidate="novalidate" data-toggle="validator">
               <div class="col-md-12 col-lg-12 col-sm-12">
                <@pageSectionHeader title="${uiLabelMap.general}" /> 
                  <@headerH3  title="${uiLabelMap.profile}"/>
                     <div class="row p-2">
                        <div class="col-md-12 col-lg-6 col-sm-12 ">
                          <@inputRow id="" label="Sub Type" label=uiLabelMap.prospectId placeholder="-----" readonly=true required=true/>
                          <@inputRow id=""  label=uiLabelMap.salutation placeholder=""/>
                          <@inputRow id=""  label=uiLabelMap.firstName placeholder="" required=true/>
                          <@inputRow id=""  label=uiLabelMap.lastName placeholder=""/>
                          <@inputRow id=""  label=uiLabelMap.typeOfPrivilege placeholder=""/>
                          <@inputRow id=""  label=uiLabelMap.asiaTreasuresMembership placeholder=""/>
                          <@inputRow id=""  label=uiLabelMap.membershipType placeholder=""/>
                          <@displayCell   label="${uiLabelMap.status}" value="--"  />
                       </div>
                    <div class="col-md-12 col-lg-6 col-sm-12 ">
                      <@inputRow id=""  label=uiLabelMap.maritalStatus placeholder=""/>
                    <@inputRow id=""  label=uiLabelMap.gender placeholder=""/>
                    <@inputRow id=""  label=uiLabelMap.dateOfBirth placeholder=""/>
                     <@inputRow id=""  label="${uiLabelMap.nationality}" placeholder="Linked From"/>
                   <@inputRow id=""  label="${uiLabelMap.prospectSegment}"placeholder="Resolution"/>
                   <@displayCell   label="${uiLabelMap.businessFranchiseForProspect}" value="--" />
                   <@inputRow id=""  label="${uiLabelMap.productLineInterest}"placeholder="Resolution"/>
                   <@inputRow id=""  label="${uiLabelMap.expiryDate}"placeholder="Resolution"/>
                  </div>
                </div>
               <@headerH2 title="${uiLabelMap.channel}" /> 
                  <div class="row pt-2">
                     <div class="col-md-12 col-lg-6 col-sm-12 ">
                                          <@inputRow id=""  label="${uiLabelMap.sourceCampaign}"placeholder=""/>
                                           <@inputRow id=""  label="${uiLabelMap.prospectChannel}"placeholder=""/>

                   </div>
                     <div class="col-md-12 col-lg-6 col-sm-12">
                        <@inputRow id=""  label="${uiLabelMap.leadImport}"placeholder="Resolution"/>
                          <@inputRow id="" label="Sub Type" label=uiLabelMap.linkedCustomer placeholder="-----" readonly=true/>

                     </div>    
                  </div> 
                <#-- Start Demographics-->
                <@headerH2 title="${uiLabelMap.demographics}" /> 
                  <div class="row pt-2">
                     <div class="col-md-12 col-lg-6 col-sm-12 ">
                         <@inputRow id=""  label="${uiLabelMap.spokenLanguages}" placeholder=""/>
                         <@inputRow id=""  label="${uiLabelMap.race}"placeholder=""/>
                         <@inputRow id=""  label="${uiLabelMap.religion}"placeholder=""/>
                          <@inputRow id=""  label="${uiLabelMap.education}"placeholder=""/>
                    </div>
                     <div class="col-md-12 col-lg-6 col-sm-12">
                        <@inputRow id=""  label="${uiLabelMap.jobTitle}"placeholder=""/>
                        <@inputRow id=""  label="${uiLabelMap.occupation}"placeholder=""/>
                        <@inputRow id=""  label="${uiLabelMap.propertyOwnership}"placeholder=""/>
                         <@inputRow id=""  label="${uiLabelMap.accommodationType}"placeholder=""/>
                     </div>    
                   </div>
                <#-- End Demographics-->
                <#-- Start Recreation and healthh-->
                <@headerH2 title="${uiLabelMap.recreationAndHealths}" /> 
                  <div class="row pt-2">
                       <div class="col-md-12 col-lg-6 col-sm-12">
                  <@inputRow id=""  label="${uiLabelMap.likesAndDislikes}"placeholder=""/>
                         <@inputRow id=""  label="${uiLabelMap.dislikes}"placeholder=""/>
                      
                      </div>
                        <div class="col-md-12 col-lg-6 col-sm-12">
                       <@inputRow id=""  label="${uiLabelMap.healthRemarks}"placeholder="Resolution"/>
                       </div>    
                 </div> 
                <#-- End Recreation and health-->
          <#-- source of wealth -->
                <@pageSectionHeader title="${uiLabelMap.sourceOfWealth}" /> 
                  <@headerH3  title="${uiLabelMap.employmentWithCurrentEmployer}"/>
                    <div class="row p-2">
                       <div class="col-md-12 col-lg-6 col-sm-12 ">
                      <@inputRow id="" label="Sub Type" label=uiLabelMap.nameOfEmployer placeholder="" />
                           <@inputRow id=""  label=uiLabelMap.natureOfBusinessEmployer placeholder=""/>
                               <@inputRow id=""  label=uiLabelMap.descriptionOfBusinessEmployer placeholder=""/>
                      </div>
                          <div class="col-md-12 col-lg-6 col-sm-12 ">
                                        <@inputRow id=""  label=uiLabelMap.positionInCompanyOrAreaOfExpertise placeholder=""/>
                                         <@inputRow id=""  label=uiLabelMap.areaOfExpertise placeholder=""/>
                                        <@inputRow id=""  label=uiLabelMap.currentAnnualIncome placeholder=""/>
                          </div>
                  </div>
                <@headerH2 title="Channel" title="${uiLabelMap.investment}" /> 
                  <div class="row pt-2">
                     <div class="col-md-12 col-lg-6 col-sm-12 ">
                              <@inputRow id=""  label="${uiLabelMap.sizeOfTotalInvestments}"placeholder=""/>

                   </div>
                   <div class="col-md-12 col-lg-6 col-sm-12">
                                    <@inputRow id=""  label="${uiLabelMap.typeOfInvestments}"placeholder="Resolution"/>
                   </div>    
                 </div> 
            <#-- communication preferences -->
             <#-- source of wealth -->
                <@pageSectionHeader title="${uiLabelMap.communicationPreferences}" /> 
                  <@headerH3  title="${uiLabelMap.preferredAddress}"/>
                    <div class="row p-2">
                       <div class="col-md-12 col-lg-6 col-sm-12 ">
                      <@inputRow id="" label="Sub Type" label=uiLabelMap.addressType placeholder=" " />
                       <@inputRow id=""  label=uiLabelMap.addressLine1 placeholder=""/>
                        <@inputRow id=""  label=uiLabelMap.addressLine2 placeholder=""/>
                          <@inputRow id=""  label=uiLabelMap.addressLine3 placeholder=""/>
                            <@inputRow id=""  label=uiLabelMap.city placeholder=""/>
                           </div>
                                  <div class="col-md-12 col-lg-6 col-sm-12 ">
         
                                         <@inputRow id=""  label=uiLabelMap.state placeholder=""/>
                                         <@inputRow id=""  label=uiLabelMap.country placeholder=""/>
                                        <@inputRow id=""  label=uiLabelMap.postalCode placeholder=""/>
                                         <@inputRow id=""  label=uiLabelMap.othersCity placeholder=""/>
                                        <@inputRow id=""  label=uiLabelMap.OthersCountry placeholder=""/>
                    
                                </div>
                     </div>
                <@headerH2  title="${uiLabelMap.prefferedPhoneOrEmail}" /> 
                  <div class="row pt-2">
                     <div class="col-md-12 col-lg-6 col-sm-12 ">
                                          <@inputRow id=""  label="${uiLabelMap.prefferedPhoneType}" placeholder=""/>
                                          <@inputRow id=""  label="${uiLabelMap.mobilePhone}" placeholder=""/>
                                          <@inputRow id=""  label="${uiLabelMap.homePhone}" placeholder=""/>
                     </div>
                     <div class="col-md-12 col-lg-6 col-sm-12">
                                          <@inputRow id=""  label="${uiLabelMap.officePhone}"placeholder="" required=true/>
                                          <@inputRow id=""  label="${uiLabelMap.fax}"placeholder=""/>
                                          <@inputRow id=""  label="${uiLabelMap.emailAddress}"placeholder="" required=true/>
                     </div>    
                   </div>
                <@headerH2   title="${uiLabelMap.marketingPreference}"/> 
                  <div class="row pt-2">
                     <div class="col-md-12 col-lg-6 col-sm-12 ">
                                          <@inputRow id=""  label="${uiLabelMap.preferredName}"placeholder=""/>
                                          <@inputRow id=""  label="${uiLabelMap.preferredModeOfCommunication}"placeholder=""/>
                                         <@inputRow id=""  label="${uiLabelMap.preferredLanguage}"placeholder=""/>
                                       <@inputRow id=""  label="${uiLabelMap.preferredFrequencyOfContact}"placeholder=""/>
                                      <@inputRow id=""  label="${uiLabelMap.bestTimeToCall}"placeholder=""/>
                                      <@inputRow id=""  label="${uiLabelMap.phone}"placeholder=""/>
                                     <@inputRow id=""  label="${uiLabelMap.email}"placeholder=""/>
                     </div>
                  <div class="col-md-12 col-lg-6 col-sm-12">
                   <@checkbox  id="" label=uiLabelMap.faxes />
                   <@checkbox  id="" label=uiLabelMap.receiveEmailUpdates />

                      <@inputRow id=""  label="${uiLabelMap.subscriptionToAdditionalServices}"placeholder=""/>
                      <@inputRow id=""  label="${uiLabelMap.createdBy}"placeholder=""/>
                    <@inputRow id=""  label="${uiLabelMap.createdOn}"placeholder=""/>
                   <@checkbox  id="" label=uiLabelMap.sms/>
                   <@inputRow id=""  label="${uiLabelMap.post}"placeholder=""/>
                    </div>   
          </div>
                 <div class="row">
                  <div class="form-group offset-2">
                     <div class="text-left ml-3">
                  
                  <@formButton
                     btn1type="submit"
                     btn1label="${uiLabelMap.Save}"
                     btn1onclick="return formSubmission();"
                     btn2=true
                     btn2onclick = "resetForm()"
                     btn2type="reset"
                     btn2label="${uiLabelMap.Clear}"
                   />
               </div>
            </div>
         </div>
      </div>   
     </form>
       <div class="col-md-12 col-lg-12 col-sm-12">
                <#assign extraLeft='
        <a href="/sales-portal/control/addTask" class="m1-2 text-dark">
        <i class="fa fa-plus fa-1" aria-hidden="true"></i>
        </a>
        ' />
        <@pageSectionHeader  title="${uiLabelMap.activities}" extraLeft=extraLeft />
      </div>   
        <div class="col-md-12 col-lg-12 col-sm-12">
        <@pageSectionHeader  title="Notes" />
          <@textareaLargeWithoutLabel  id="messages" rows="3" />
		</div> 
         <div class="col-md-12 col-lg-12 col-sm-12">
                <#assign extraLeft='
                  <a href="/sales-portal/control/addTask" class="m1-2 text-dark">
        <i class="fa fa-plus fa-1" aria-hidden="true"></i>
        </a>
        ' />
        <@pageSectionHeader   title="${uiLabelMap.serviceRequests}" extraLeft=extraLeft />
           </div>   
        <div class="col-md-12 col-lg-12 col-sm-12">
               <@pageSectionHeader  title="Administration"/>
          <div class="row pt-2">
                    <div class="col-md-12 col-lg-6 col-sm-12">
                     <@displayCell   label="${uiLabelMap.createdOn}" value="--"  />
                   <@displayCell   label="${uiLabelMap.modifiedOn}" value="--" />
                   </div>
                     <div class="col-md-12 col-lg-6 col-sm-12">
                           <@displayCell   label="${uiLabelMap.createdBy}" value="--" />
                   <@displayCell   label="${uiLabelMap.modifiedBy}" value="--" />
                   </div>
          </div>
       </div>   
     </div><#-- End main-->
   </div><#-- End row-->