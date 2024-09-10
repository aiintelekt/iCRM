<#include "component://homeapps/webapp/homeapps/lib/ofbizFormMacros.ftl"/>
    <div class="page-header border-b">
      <h1>Merge Accounts</h1>
    </div>
    <form method="post" action="createLead" id="createLeadForm" class="form-horizontal" name="createLeadForm" novalidate="novalidate" data-toggle="validator">
      <input type="hidden" name="duplicatingPartyId">
      <input type="hidden" name=" ">
      <input type="hidden" name="">
      <div class="col-md-6 col-sm-6">
        <div class="form-group row has-error">
          <label for="inputEmail3" class="col-sm-4 control-label">From Account	</label>
          <div class="col-sm-7">
            <div class="input-group">
              <input type="text" class="form-control input-sm" placeholder="From Account">
              <span class="input-group-addon">
              <span class="glyphicon glyphicon-list-alt
                " data-toggle="modal" data-target="#myModal">
              </span>
              </span>
            </div>
          </div>
        </div>            
		<div class="form-group row has-error">
          <label for="inputEmail3" class="col-sm-4 control-label">To Account	</label>
          <div class="col-sm-7">
            <div class="input-group">
              <input type="text" class="form-control input-sm" placeholder="To Account">
              <span class="input-group-addon">
              <span class="glyphicon glyphicon-list-alt
                " data-toggle="modal" data-target="#myModal">
              </span>
              </span>
            </div>
          </div>
        </div>        
      </div>
      <div class="col-md-6 col-sm-6">
	   
	  </div>
      <div class="col-md-12 col-sm-12">
        <div class="form-group row">
          <div class="offset-sm-2 col-sm-9">
            <button type="reset" class="btn btn-sm btn-primary navbar-dark mt">Merge</button>

          </div>
        </div>
      </div>
    </form>
  </div>
</div>
<!-- /.container -->
<div id="myModal" class="modal fade" role="dialog">
  <div class="modal-dialog">
    <!-- Modal content-->
    <div class="modal-content">
      <div class="modal-header">            
        <h4 class="modal-title">Find Accounts</h4>
		<button type="reset" class="close" data-dismiss="modal">&times;</button>
      </div>
      <div class="modal-body">
        <div class="card-header">
        <form method="post" action="createLead" id="createLeadForm" class="form-horizontal" name="createLeadForm" novalidate="novalidate" data-toggle="validator">
      <input type="hidden" name="duplicatingPartyId">
      <input type="hidden" name=" ">
      <input type="hidden" name="">
	  
      <div class="row"> 
      <div class="col-md-2 col-sm-2"> 
	  <div class="form-group row mr">
		 <input type="text" class="form-control input-sm" id="" placeholder="Account ID">
      </div>
      </div>
	  <div class="col-md-2 col-sm-2">
	  <div class="form-group row mr">
		 <input type="text" class="form-control input-sm" id="" placeholder="Name">
      </div>
      </div>
	  <div class="col-md-2 col-sm-2">
	  <div class="form-group row mr">
		 <input type="text" class="form-control input-sm" id="" placeholder="Company Name">
      </div>
      </div>
	  <div class="col-md-2 col-sm-2">
	  <div class="form-group row mr">
		 <input type="text" class="form-control input-sm" id="" placeholder="Email Address">
      </div>
      </div>
	  <div class="col-md-2 col-sm-2"> 
		  <div class="form-group row mr">
		 <input type="text" class="form-control input-sm" id="" placeholder="Phone Number">
      </div>
      </div>
	  <div class="col-md-1 col-sm-1">
    <a href="#" class="btn btn-sm btn-primary">Find Accounts </a>
      </div>			 
      </div>			 
  </form>
        <div class="clearfix"> </div>
      </div>
	   <div class="clearfix"> </div>
        <div class="page-header">
          <h2 class="float-left">Accounts List </h2>
        </div>
        <div class="table-responsive">
          <table id="dtable" class="table table-striped">
            <thead>
              <tr>
                <th>Account ID</th>
                <th>Account Name</th>
                <th>Status</th>					
                <th>E-Mail Address</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td><a href="<@ofbizUrl>viewAccount</@ofbizUrl>">10000</a></td>
			    <td>Issam Jamai</td>
                <td>Enabled</td>
                <td>issamjamai@hotmail.com</td>                  
              </tr>
			  <tr>
			  <td><a href="viewAccount.html">10001</a></td>
			  <td>Sanjeev Bhola</td>
              <td>Enabled</td>                    
              <td>sgffor@gmail.com</td>                  
              </tr>
			  <tr>
			  <td><a href="viewAccount.html">10002</a></td>
			  <td>Sanjeev Bhola</td>
              <td>Enabled</td>                  
              <td>sgffor@gmail.com</td>               
              </tr>				 
            </tbody>
          </table>
        </div>
	  </div>
      <div class="modal-footer">
        <button type="sbmit" class="btn btn-sm btn-primary" data-dismiss="modal">Close</button>
      </div>
    </div>