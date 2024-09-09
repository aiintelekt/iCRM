<div id="myModal" class="modal fade mt-5" role="dialog">
  <div class="modal-dialog">
    <!-- Modal content-->
    <div class="modal-content rounded-0 border-0">
      <div class=" border-bottom-0 pt-2 pb-2 bg-light1">
        <h4 class="text-center">Attempt call</h4>
        <!--button type="reset" class="close" data-dismiss="modal">&times;</button!-->
      </div>
      <form method="POST" action="<@ofbizUrl>createCall</@ofbizUrl>" name="createCallLog" id="createCallLog" >
      	<input type="hidden" value="${leadId}" name="leadId" id="attemptLeadId"/>
      	<input type="hidden"  name="contactId" id="attemptContactId"/>
      	<input type="hidden"  name="contactMechId" id="attemptContactMechId"/>
      	<input type="hidden"  name="isMainLine" id="isMainLine"/>
      </form>
      <div class="modal-body pb-0">
        <div class="text-center pt-0 pb-5" id="callerInfo"> Moothedath Panjan Ramachandran <br/> +919999999999</div>
        <div class="row border-top">
          <div class="col-6 p-0 border-right border-light"><a href="" data-dismiss="modal" class="btn btn-sm bg-light1 rounded-0 btn-block text-dark">Cancel</a></div>
          <div class="col-6 p-0"><a id="callNumber" href="javascript:void(0)" class="btn btn-sm bg-light1 rounded-0 btn-block text-dark attemptCall">Call</a></div>
        </div>
        <div class="clearfix"> </div>
      </div>
    </div>
  </div>
</div> 