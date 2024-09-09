<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>


<script>
function getNoteInfo(noteId){
    $.ajax({
        type: "POST",
        url: "/common-portal/control/getNoteData",
        async: true,
        data:  {"noteId": noteId, "domainEntityType": "${domainEntityType!}", "domainEntityId": "${domainEntityId!}", "externalLoginKey": "${requestAttributes.externalLoginKey!}"},
        success: function(result) {
        	if (result.code == 200) {
        		data = result.data;
        		
        		var noteId=data.noteId;
    			var noteInfo=data.noteInfo;
            	document.getElementById("noteInfo").innerHTML=noteInfo;
        	}
             
	 	}
    });
}
</script>

<div id="modalContactView" class="modal fade" >
  <div class="modal-dialog modal-lg">
    <!-- Modal content-->
    <div class="modal-content">
      <div class="modal-header">
        <h2 class="modal-title">  Note Information View</h2>
        <button type="reset" class="close" data-dismiss="modal">&times;</button>
      </div>
      <div class="modal-body">
        <div class="clearfix"></div>
		
        <@displayCell
		     label="Note Info"
		     value=""
		     id="noteInfo"
		     labelColSize="col-sm-2"
		     />
      </div>
      <div class="modal-footer">
        <button type="sbmit" class="btn btn-sm btn-primary navbar-dark" data-dismiss="modal">Close</button>
      </div>
    </div>
  </div>
</div>