<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
<script type="text/javascript" >
	$(document).ready(function() {
    	$('#detailsid').click(function(){
    		var loc=window.location;
    		var newLoc=loc.toString().substring(0,loc.toString().indexOf("#"));
    		window.location=newLoc+"#details";
    		window.scrollTo(0,0);
  		});
   });
</script>
 <div class="top-band bg-white mb-0">
                  <div class="col-lg-12 col-md-12 col-sm-12">
                     <div class="row">
                        <marquee behavior="scroll" direction="left" class="text-danger">"System maintenance scheduled for 03-09-2019 from 8 AM SGT to 10 AM SGT. During this time, users may experience unavailability of services"</marquee>
                      </div>
                    </div>
                  </div>
                  
                  <div class="container-fluid">
        <div class="row">
          <div id="main" role="main">
                <div class="top-band bg-light">
                    <div class="col-lg-12 col-md-12 col-sm-12">
                        <div class="row">
                            <div class="col-lg-12 col-md-12 col-sm-12">
                              <div class="text-left float-left"><h3 class="float-left">Recently Viewed: </h3>
                              <a href="#" class="btn btn-xs btn-primary">SG-OP-20191023-OFZ-198563</a>
                              <a href="#" class="btn btn-xs btn-primary">SG_OP-20191023-OFZ-198564</a>
                              <a href="#" class="btn btn-xs btn-primary">SG_OP-20191023-OFZ-198565</a>
                              <a href="#" class="btn btn-xs btn-primary">SG_OP-20191023-OFZ-198566</a>
                              <a href="#" class="btn btn-xs btn-primary">SG_OP-20191023-OFZ-198567</a>
                            </div>
                        </div>
                    </div>
                </div>
               </div>
           <div class="clearfix"></div>    
          <div class="col-lg-12 col-md-12 col-sm-12 mid">
          <div class="card-head margin-adj mt-0">
          <h2> <a href="">Zhang Ziyi Sgcrm</a></h2>
          <a herf="" class="text-dark left-icones" title="View Customer Alerts"><i class="fa fa-bell-o custicons" aria-hidden="true"></i></a>
          <a herf="" class="text-dark left-icones" data-toggle="modal" data-target="#customer-alert" title="Add Customer Alerts"><img src="" width="21" height="22" class="cust-icon"></a>
          <a herf="" title="Add Service Request" class="text-dark left-icones"><i class="fa fa-plus-square custicons" aria-hidden="true"></i></a>
          <a herf="" title="Add Opportunity" class="text-dark left-icones"><img src="" width="21" height="22" class="cust-icon"></a>
          <a href="#" title="Add Activity" id="dropdown09" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false" class="text-drak left-icones"><img src="" width="20" height="20" class="cust-icon" class="cust-icon"></a>
          <div class="dropdown-menu" aria-labelledby="cust-icon"></a>
           <@headerH4
             title="Add Activities"
             />
           <a class="dropdown-item" href="#"><i class="fa fa-clipboard" aria-hidden="true"></i> Task</a>
           <a class="dropdown-item" href="#"><i class="fa fa-phone" aria-hidden="true"></i> phone Call</a>
           <a class="dropdown-item" href="#"><i class="fa fa-envelope" aria-hidden="true"></i> Email</a>
           <a class="dropdown-item" href="#"><i class="fa fa-calendar" aria-hidden="true"></i> Appointment</a>
           <a class="dropdown-item" href="#"><i class="fa fa-square" aria-hidden="true"></i> Others</a>
          </div>
          
          <ul class="text-right">
          <li><label class="bglight">Phone <i class="fa fa-times fa-1 text-danger" aria-hidden="true"></i></label></li>
          <li><label class="bglight">Email <i class="fa fa-check fa-1 text-success" aria-hidden="true"></i></label></li>
          <li><label class="bglight">Postal <i class="fa fa-check fa-1 text-success" aria-hidden="true"></i></label></li>
          <li><label class="bglight">SMS <i class="fa fa-check fa-1 text-success" aria-hidden="true"></i></label></li>
          
          <li> <a href="" title="View Service Requests" class="text-dark mr-3"><span class="rounded-circle badge badge-secondary position-absolute">15</span><i class="fa fa-plus-square custicons fa-1" aria-hidden="true"></i></a></li>
          <li> <a href="" title="View Opportunities" class="text-dark mr-2"><span class="rounded-circle badge badge-secondary position-absolute">10</span><img src="" width="21" height="22" class="cust-icon"></a></li>
          <li> <a href="" title="View Household Opportunities"class="text-dark mr-3"><span class="rounded-circle badge badge-secondary position-absolute">15</span><i class="fa fa-home" aria-hidden="true"></i></a></li>
          <li> <a href="" class="ml-5 mr-2 text-dark" data-toggle="modal" data-target="#phonepopup"><i class="fa fa-envelope fa-1" aria-hidden="true"></i>capuser3@uat1bank.dbs.com</a></li>
          <li><a href="" class="mr-2 text-dark" aria-hidden="true" data-toggle="modal" data-target="#myModal2"><i class="fa fa-phone fa-1" aria-hidden="true"></i> (+65) 9998 1728 </a></li>
          </ul>
          </div>
          
          <div class="clearfix"></div>
          <div class="card-head margin-adj mt-0">
             <div class="row">
             <div class="col-md-5">
             <div class="bd-callout float-left">
          <h6>Opportunity : Information</h6>
          <@headerH3
           title="SG-OP-20191023-OFZ-123456"
          />
          </div>
          
          
          <div class="bd-callout float-left">
          <small>Campaign Code</small>
          <@headerH5
           title="C0001"
          />
          </div>
          
          <div class="bd-callout float-left">
          <small>Campaign Name</small>
          <h5>April 2019</h5><h5> Campaign</h5>
          </div>
          
          <div class="bd-callout float-left">
          <small>Campaign End</small>
          <h5>30/04/2019</h5><h5> 13:00</h5>
          </div>
          </div>
          <div class="col-md-7 right-details">
           <div class="bd-callout" style="width:150px;">
            <small data-toggle="modal" data-target="#Notinterested">Opportunity Status</small>
            <@dropdownCell
              id=""
              label=""
              placeholder=""
              />
          </div>              
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              
            <div class="bd-callout" style="width:150px;">
            <small data-toggle="modal" data-target="#Notinterested">Response Reason </small>
            <@dropdownCell
              id=""
              label=""
              placeholder=""
              />
          </div>                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       
                                                                                                    
            <div class="bd-callout" style="width:150px;">
            <small data-toggle="modal" data-target="#Notinterested">Response Type</small>
            <@dropdownCell
              id=""
              label=""
              placeholder=""
              />
          </div>                                                                                                             
          
          <div class="bd-callout" style="width:150px;">
            <small data-toggle="modal" data-target="#Notinterested">Call Outcome</small>
            <@dropdownCell
              id=""
              label=""
              placeholder=""
              />
              </div>
              <div class="bd-callout">
               <small># Days since last call</small>
               <@headerH5
                title="5 Days"
                />
               </div>
              
              <div class="bd-callout">
               <small>Call Back Date</small>
               <@headerH5
               title="10/04/2019"
               />
               </div>
              
              </div>
              </div>
              </div>
              
              
              
         <div class="card-head margin-adj mt-2" id="cp">
         <div class="row">
          <div class="col-md-6 col-lg-3 col-sm-12">
         <@displayCell
          label="Customer"
          value="Zhang Ziyi Sgcrm"
          />
         
           <@displayCell
              label="CIN/CIF ID"
              value="2027949"
            />
              
            </div>
            
          <div class="col-md-6 col-lg-3 col-sm-12">
          <@displayCell
              label="Prospect"
              value=""
            />
            <@displayCell
              label="Prospect ID"
              value=""
            />
              
            </div>
          
          <div class="col-md-6 col-lg-3 col-sm-12">
          <@displayCell
              label="Non CRM "
              value=""
            />
            <@displayCell
              label="V+ ID "
              value=""
            />  
            </div>
          
          <div class="col-md-6 col-lg-3 col-sm-12">
          <@displayCell
              label="National ID"
              value=""
            />  
           
          </div>
         </div>
        </div>  
          
          
      <ul class="nav nav-tabs mt-2">
      <li class="nav-item"><a data-toggle="tab" class="nav-link active" id="detailsid" href="#details">Details </a></li>
      <li class="nav-item"><a data-toggle="tab" class="nav-link active" href="">Customer Response </a></li>
      <li class="nav-item"><a data-toggle="tab" class="nav-link active" href="">Straight Through Processing </a></li>
      <li class="nav-item"><a data-toggle="tab" class="nav-link active" href="">Activities </a></li> 
      <li class="nav-item"><a data-toggle="tab" class="nav-link active" href="">Offers </a></li>
      <li class="nav-item"><a data-toggle="tab" class="nav-link active" href="">Notes and Attachment </a></li> 
      <li class="nav-item"><a data-toggle="tab" class="nav-link active" href="">Related Opportunities </a></li>
      <li class="nav-item"><a data-toggle="tab" class="nav-link active" href="">Administration </a></li>
       <div class="text-right position-absolute" style="right:20px;">
       <a title="Save" href="#" class="btn btn-primary btn-xs mt-1"><i class="fa fa-save" aria-hidden="true"></i>Save</a><a title="CLose" href="#" class="btn btn-primary btn-xs mt-1"><i class="fa fa-window-close-o" aria-hidden="true"></i>Close</a>
       </div>
       </ul>
       
     <div class="tab-content">
       <div id="details" class="tab-pane fade">
       <div> <h1> fdsjaflkjf</h1></div>
        ${screens.render("component://campaign/webapp/widget/campaign/screens/list/paramUnitScreen.xml#details")}
        </div>
        </div>