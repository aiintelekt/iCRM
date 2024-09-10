<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="Muruganantham">
    <link rel="shortcut icon" href="../favicon.ico">
    <title>Merge Leads | CRM </title>
    <!-- Bootstrap core CSS -->
    <link href="../themes/bootstrap/css/blue.css" rel="stylesheet">
    <link href="../themes/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <link href="../themes/bootstrap/css/fio-custom.css" rel="stylesheet">
    <link href="../themes/bootstrap/css/font-awesome.min.css" rel="stylesheet">
    <link href="../themes/bootstrap/css/bootstrap-select.min.css" rel="stylesheet" >
    <link href="../themes/bootstrap/css/dataTables.bootstrap.min.css" rel="stylesheet">
    <link href="../themes/bootstrap/css/bootstrap-datetimepicker.min.css" rel="stylesheet">
    <script type="text/javascript" src="../themes/bootstrap/js/include.js"> </script>
    <script type="text/javascript" src="../themes/bootstrap/js/bootstrap-select.min.js" defer></script>
	<script type="text/javascript"  src="../themes/bootstrap/js/popper.min.js"></script>
    <script type="text/javascript" src="../themes/bootstrap/js/jquery.min.js"> </script>
    <script type="text/javascript" src="../themes/bootstrap/js/jquery.dataTables.min.js"></script>
    <script type="text/javascript" src="../themes/bootstrap/js/dataTables.bootstrap.min.js"></script>  
    <script src="../themes/bootstrap/js/bootstrap.min.js"></script>    
    <link href="../themes/bootstrap/css/summernote.css" rel="stylesheet">    
    <script src="../themes/bootstrap/js/summernote.js"></script>
    <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
    <script src="https://oss.maxcdn.com/html5shiv/3.7.3/html5shiv.min.js"></script>
    <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->
    <script>
      $('#summernote').summernote({
       height: 300,                 // set editor height
       minHeight: null,             // set minimum height of editor
       maxHeight: null,             // set maximum height of editor
       focus: true                  // set focus to editable area after initializing summernote
      });
    </script>
  </head>
  <body>
   <#-- <div w3-include-html="nav.html"></div>
    <script>includeHTML();</script>-->
    <div class="jumbotron">
      <div class="container-fluid">
        <div class="page-header">
          <h1>Merge Leads</h1>
        </div>
        <form method="post" action="createLead" id="createLeadForm" class="form-horizontal" name="createLeadForm" novalidate="novalidate" data-toggle="validator">
          <input type="hidden" name="duplicatingPartyId">
          <input type="hidden" name=" ">
          <input type="hidden" name="">
          <div class="col-md-6 col-sm-6">
            <div class="form-group row has-error">
              <label for="inputEmail3" class="col-sm-4 control-label">From Lead	</label>
              <div class="col-sm-7">
                <div class="input-group">
                  <input type="text" class="form-control input-sm" placeholder="From Lead">
                  <span class="input-group-addon">
                  <span class="glyphicon glyphicon-list-alt
                    " data-toggle="modal" data-target="#myModal">
                  </span>
                  </span>
                </div>
              </div>
            </div>            
			<div class="form-group row has-error">
              <label for="inputEmail3" class="col-sm-4 control-label">To Lead	</label>
              <div class="col-sm-7">
                <div class="input-group">
                  <input type="text" class="form-control input-sm" placeholder="To Lead">
                  <span class="input-group-addon">
                  <span class="glyphicon glyphicon-list-alt
                    " data-toggle="modal" data-target="#myModal">
                  </span>
                  </span>
                </div>
              </div>
            </div>        
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
                    <td><a href="viewAccount.html">10000</a></td>
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
      </div>
    </div>
     <footer class="footer">
      <div class="container-fluid">
      <div class="row">	  
        <div class="col-md-6 col-sm-6">
          <p class="text-muted">		 Time Zone : Eastern Daylight Time</p>
        </div>
        <div class="col-md-6 col-sm-6">
          <p class="text-muted float-right">		2018 © <img alt="Group FiO" src="../themes/bootstrap/images/logo.png" height="20" width="44" border="0" > 
            Professional Edition of FiO RMS Build 2018.2. All Rights Reserved
          </p>
        </div>
        </div>
      </div>
    </footer>
    <!-- Bootstrap core JavaScript
      ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->
    <script>window.jQuery || document.write('<script src="../themes/bootstrap/js/jquery.min.js"><\/script>')</script>
    <script type="text/javascript" src="../themes/bootstrap/js/moment.js"></script>
	<script type="text/javascript" src="../themes/bootstrap/js/custom.js"></script>
    <script type="text/javascript" src="../themes/bootstrap/js/validator.min.js"></script>
    <script type="text/javascript" src="../themes/bootstrap/js/bootstrap-datetimepicker.min.js"></script>
  </body>
</html>