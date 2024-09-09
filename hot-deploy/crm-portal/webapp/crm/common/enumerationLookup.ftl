
<div id="enumerationLookup" class="modal fade" role="dialog" style="display: none;" aria-hidden="true">
   <div class="modal-dialog modal-lg">
      <!-- Modal content-->
      <div class="modal-content">
         <div class="modal-header">
            <h4 class="modal-title"></h4>
            <button type="reset" class="close" data-dismiss="modal">×</button>
         </div>
	     <div class="clearfix"> </div>
         
         <div class="modal-body">
	     	<div class="inputs">
		    </div>
	       
	      <div class="clearfix"> </div>
	      <div class="page-header">
	         <h2 class="float-left grid-title"></h2>
	      </div>
	      <div class="table-responsive grid-data"></div>
         </div>
         <div class="modal-footer">
            <button type="sbmit" class="btn btn-sm btn-primary" data-dismiss="modal">Close</button>
         </div>
      </div>
   </div>
</div>
<script>
	$("#enumerationLookup").on("show.bs.modal", function(e) {
       var listType = $(e.relatedTarget).data('type-id');
       var name = $(e.relatedTarget).data('name');
       var modal = $(this)
   	   modal.find('.modal-title').text("Create "+name);
   	   /* var inputFields = "<div class='row'>";
   			inputFields = inputFields+"<div class='col-md-2 col-sm-2'><div class='form-group row mr'><input type='text' class='form-control input-sm' id='enumId' name='enumId' value='' placeholder='"+name+" Id'></div></div>";
  			inputFields = inputFields+"<div class='col-md-2 col-sm-2'><div class='form-group row mr'><input type='text' class='form-control input-sm' id='enumCode' name='enumCode' value='' placeholder='"+name+" Code'></div></div>";
  			inputFields = inputFields+"<div class='col-md-2 col-sm-2'><div class='form-group row mr'><input type='text' class='form-control input-sm' id='sequenceId' name='sequenceId' value='' placeholder='Sequence Id'></div></div>";
  			inputFields = inputFields+"<div class='col-md-2 col-sm-2'><div class='form-group row mr'><input type='text' class='form-control input-sm' id='description' name='description' value='' placeholder='Description'></div></div>";
  			inputFields = inputFields+"<div class='col-md-1 col-sm-1'><button type='button' class='btn btn-sm btn-primary navbar-dark' onclick='javascript:createEnumData();'>Create</button></div>";
			inputFields = inputFields+"</div>"; */
			
	   var inputFields = "<div class='row padding-r'><div class='col-md-6 col-sm-6'>";
	   		inputFields = inputFields+"<div class='form-group row'><label  class='col-sm-5 col-form-label text-danger'>"+name+" Id</label>";
			inputFields = inputFields+"<div class='col-sm-7'><input type='text' class='form-control input-sm' name='enumId' id='enumId' data-error='Please enter "+name+" Id' placeholder='"+name+" Id'>";
			inputFields = inputFields+"<input type='hidden' name='listType' id='listType'>";
			inputFields = inputFields+"<input type='hidden' name='name' id='name'>";
			inputFields = inputFields+"<div class='help-block with-errors list-unstyled' id='enumIdError'></div></div></div>";
			
			inputFields = inputFields+"<div class='form-group row'><label  class='col-sm-5 col-form-label text-danger'>"+name+" Code</label>";
			inputFields = inputFields+"<div class='col-sm-7'><input type='text' class='form-control input-sm' name='enumCode' id='enumCode' data-error='Please enter "+name+" Id' placeholder='"+name+" Code'>";
			inputFields = inputFields+"<div class='help-block with-errors list-unstyled' id='enumCodeError'></div></div></div>";
			
			inputFields = inputFields+"<div class='form-group row'><label  class='col-sm-5 col-form-label text-danger'>Sequence Id</label>";
			inputFields = inputFields+"<div class='col-sm-7'><input type='text' class='form-control input-sm' name='sequenceId' id='sequenceId' data-error='Please enter Sequence Id' placeholder='Sequence Id'>";
			inputFields = inputFields+"<div class='help-block with-errors list-unstyled' id='sequenceIdError'></div></div></div>";
			
			inputFields = inputFields+"<div class='form-group row'><label  class='col-sm-5 col-form-label text-danger'>Description</label>";
			inputFields = inputFields+"<div class='col-sm-7'><input type='text' class='form-control input-sm' name='description' id='description' data-error='Please enter description' placeholder='Description'>";
			inputFields = inputFields+"<div class='help-block with-errors list-unstyled' id='descriptionError'></div></div></div>";
			
			inputFields = inputFields+"<div class='form-group row'><label  class='col-sm-5 col-form-label'></label>";
			inputFields = inputFields+"<div class='col-sm-7'><button type='button' class='btn btn-sm btn-primary mt-2' onclick='javascript:return createEnumeration(this);'>Create</button>";
			inputFields = inputFields+"</div></div></div></div>";
		
	   modal.find('.inputs').html(inputFields);
   	   modal.find('.grid-title').text(name+" List");
   	   modal.find('.grid-data').html("<table id='ajaxEnumerationData' class='table table-striped'><thead><tr><th>"+name+" Id</th><th>"+name+" Code</th><th>Sequence Id</th><th>"+name+" Description</th><th>Disable</th><th>Action</th></tr></thead></table>");
   	   $('#listType').val(listType);
   	   $('#name').val(name);
   	   getEnumerations(listType);
   	    
    });


 function getEnumerations(listType){
	var userLoginGeoId = "${userLogin.countryGeoId!}";
 	$("#ajaxEnumerationData").DataTable({
           "processing": true,
           "destroy": true,
           "ajax": {
               "url": "getEnumerations",
               "type": "POST",
               "async": false,
               data: {
                   "listType": listType,
                   "userLoginGeoId": userLoginGeoId
               }
           },
           "pageLength": 10,
           "bAutoWidth": false,
           "paging": true,
           "ordering": false,
           "info": false,
           "searching": false,
           'columnDefs': [
           {
             'targets': 2,
             'createdCell':  function (td, cellData, rowData, row, col) {
                $(td).attr('id', 'seqId');
                $(td).attr('width', '10%');
              }
           },
           {
             'targets': 3,
             'createdCell':  function (td, cellData, rowData, row, col) {
                $(td).attr('id', 'desc'); 
                $(td).attr('width', '30%'); 
              }
           },
           {
             'targets': 4,
             'createdCell':  function (td, cellData, rowData, row, col) {
                $(td).attr('id', 'disable'); 
              }
           }
           ],
           "columns": [{
                   "data": "enumId"
               },/*
               {
                   "data": "enumTypeId"
               }, */
               {
                   "data": "enumCode"
               },
               {
                   "data": "sequenceId"
               },
               {
                   "data": "description"
               },
               {
                   "data": "disabled",
                    "render": function(data, type, row, meta){
	   					if(type === 'display'){
	   						if("N"==data){
	   							data = "No";
	   						} else if("Y"==data){
	   							data = "Yes";
	   						}
	   					}
	   					return data;
	   				 }
               },
               {   "data":  null,
	   				 "render": function(data, type, row, meta){
	   					if(type === 'display'){
	   						data = '<i class="fa fa-edit btn btn-xs btn-primary edit" id="'+row.enumId+'" aria-hidden="true" onclick=editEnum("'+row.enumId+'");></i> <i class="fa btn btn-xs btn-danger delete" id="'+row.enumId+'" aria-hidden="true" onclick=removeEnumeration("'+row.enumId+'");><span class="fa fa-trash"></span></i>';
	   					}
	   					return data;
	   				}
	   		   }
           ],
           "fnDrawCallback": function(oSettings) {
               resetDefaultEvents();
           }
       });
       
 }
 
	function editEnum(enumId){
   	  var row = $("#"+enumId).closest('tbody tr');
      var seqId = row.find("#seqId").text();
      var desc = row.find("#desc").text();
      var disabled = row.find("#disable").text();
      
      if((seqId != null && seqId != "") || (desc != null && desc != "")) {
         $("#"+enumId).removeClass().addClass("fa fa-check-square-o btn btn-xs btn-success");
         $("#"+enumId).attr("onclick","updateEnum('"+enumId+"')");
   
      }
      /*if(seqId != null && seqId != "") {*/
      	row.find("#seqId").html("").append("<input type='text' id='sequenceId' name='sequenceId' value='"+seqId+"' class='form-control input-sm'>");
      /*} */
      /*if(desc != null && desc != "") { */
        row.find("#desc").html("").append("<input type='text' id='description' name='description' value='"+desc+"' class='form-control input-sm'>");
      /*}*/
     
      var selectBox = '<select class="ui dropdown form-control input-sm" data-original-title="Hide" id="disabled" name="disabled">';
      if(disabled != null && disabled != "" && disabled == "No") {
         selectBox = selectBox + '<option value="N" selected>No</option>';
      } else {
         selectBox = selectBox + '<option value="N">No</option>';
      }
      if(disabled != null && disabled != "" && disabled == "Yes") {
         selectBox = selectBox + '<option value="Y" selected>Yes</option>';
      } else {
         selectBox = selectBox + '<option value="Y">Yes</option>';
      }
      selectBox = selectBox + '</select>';
  	  
  	  row.find("#disable").html("").append(selectBox);
  
      document.addEventListener("click", function(event) {
		if (event.target.closest('tbody tr')){}
		else{
			row.find("#seqId").text(seqId);
			row.find("#desc").text(desc);
			row.find("#disable").text(disabled);
			$("#"+enumId).removeClass().addClass("fa fa-edit btn btn-xs btn-primary edit");
			$("#"+enumId).attr("onclick","editEnum('"+enumId+"')");
		}
		    
      });
   }
   
   function createEnumeration(){
	 var enumId = $('#enumId').val();
	 var enumCode = $('#enumCode').val();
	 var sequenceId = $('#sequenceId').val();
	 var description = $('#description').val();
	 var listType = $('#listType').val();
	 var disabled =  $('#disabled').val();
	 var name =  $('#name').val();
	 $('#enumIdError').html('');
	 $('#enumCodeError').html('');
	 $('#sequenceIdError').html('');
	 $('#descriptionError').html('');
	 if(enumId == null || enumId == ""){
		$('#enumIdError').html('Please enter the '+name+' Id' );
		return false;	 
	 } else if( enumCode == "" || enumCode == null){
	 	$('#enumCodeError').html('Please enter the '+name+' Code' );
	 	return false;
	 } else if( sequenceId == null || sequenceId == "" ){
	 	$('#sequenceIdError').html('Please enter sequence id');
	 	return false;
	 }else if( description == null || description =="") {
	 	$('#descriptionError').html('Please enter description');
          return false;
     }
      if(enumId != null && enumId != "") {
         jQuery.ajax({
           url: "createEnumeration",
           type: 'POST',
           async: false,
           data: {
               "enumId": enumId,
               "enumCode":enumCode,
               "sequenceId": sequenceId,
               "description": description,
               "disabled": disabled,
               "listType":listType
           },
           error: function(msg) {
               showAlert("error", msg);
           },
           success: function(msg) {
               showAlert("success", "Record successfully created");
               getEnumerations(listType);
               $('#enumId').val('');
			   $('#enumCode').val('');
			   $('#sequenceId').val('');
			   $('#description').val('');
           }
         });
      }
   } 
   function updateEnum(enumId){
   	 var row = $("#"+enumId).closest('tbody tr');
   	 var seqId = row.find("#sequenceId").val();
     var desc = row.find("#description").val();
     var disabled = row.find("#disabled").val();
     var listType = $('#listType').val();
     
     if((desc == null || desc == "")) {
          $.notify({
          	message : '<p>Please enter description</p>',
          },{
				type: 'danger'
		  });
          return false;
      }
     /* 
     if( seqId == null || seqId == "" ){
	 	 $.notify({
          	message : '<p>Please enter sequence</p>',
          },{
				type: 'danger'
		  });
          return false;
	 } */
	 
     if(enumId != null && enumId != "") {
         jQuery.ajax({
           url: "updateEnumeration",
           type: 'POST',
           async: false,
           data: {
               "enumId": enumId,
               "sequenceId": seqId,
               "description": desc,
               "disabled": disabled
           },
           error: function(msg) {
               showAlert("error", msg);
           },
           success: function(msg) {
               showAlert("success", "Record successfully updated");
               getEnumerations(listType)
           }
         });
      }
   }
   
    function removeEnumeration(enumId){
      if(enumId != null && enumId != "") {
         jQuery.ajax({
           url: "removeEnumeration",
           type: 'POST',
           async: false,
           data: {
               "enumId": enumId  
           },
           error: function(msg) {
               showAlert("error", msg);
           },
           success: function(msg) {
               showAlert("success", "Record successfully deleted");
               var listType = $('#listType').val();
               getEnumerations(listType)
           }
         });
      }
   } 
  
</script>