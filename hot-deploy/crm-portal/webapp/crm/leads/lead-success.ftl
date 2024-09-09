<div id="leadSuccess" class="modal hide mt-5" role="dialog" style="display:none;">
    <div class="modal-dialog modal-sm">
      <#-- Modal content -->
      <div class="modal-content rounded-0 border-0">
        <div class=" border-bottom-0 pt-2 pb-2">
         <h5 class="text-center">Lead Created</h5>
          <div style="position: relative; padding-left: 90%; top:-30px"><a href="viewLead?partyId=${partyId}" id="cancelButton">X</a></div>
        </div>
        <div class="modal-body pb-0">
          <div class="text-center pt-0 pb-5"> Your lead is successfully created,<br> The lead ID is <span class="text-danger font-weight-bold">${partyId}</span></div>
          <#-- <div class="row">
            <div class="col-6 p-1 border-right border-light"><a href="#" class="btn btn-sm bg-light1 rounded-0 btn-block text-dark">Create Task</a></div>
            <div class="col-6 p-1"><a href="updateLeadForm?partyId=${partyId}" class="btn btn-sm bg-light1 rounded-0 btn-block text-dark">Edit Task</a></div>
          </div>
          <div class="clearfix"> </div>  -->
        </div>
      </div>
    </div>
</div>
  
<div id="shadowBox"></div>
  