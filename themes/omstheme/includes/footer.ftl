			<div class="modal fade in" id="rate-modal" tabindex="-1" role="basic" aria-hidden="true" style="padding-right: 17px;">
				<div class="modal-dialog">
				    <div class="modal-content">
		                <div class="modal-header">
		                    <button type="button" class="close" data-dismiss="modal">&times;</button>
		                    <h4 class="modal-title">Rate This Page</h4>
		                </div>
		                <div class="modal-body">
		                    <div class="row">
		                      <div class="col-md-12">
		                        <h4><strong>Rate this page</strong></h4>
		                          <div class="col-md-12">
		                            <form name="rating" action="">
		                                  <div class="row lead">
		                                    <h4>How easy was it to find what you needed?</h4>
		                                    <div id="stars" class="starrr"></div><h5><span id="count">0</span> star(s)</h5>
		                                  </div>
		                                  <div class="row lead">
		                                    <h4>How likely are you to recommend this site to a friend?</h4>
		                                    <div id="stars-2" class="starrr"></div><h5><span id="count-2">0</span> star(s)</h5>
		                                  </div>
		                                  <div class="row lead">
		                                    <h4>Is there anything else you would like us to know?</h4>
		                                    <textarea class="form-control" rows="6" style="resize:none"></textarea>
		                                  </div>
		                                  <div class="row lead toolbars-submit modal-btn-bar"> <a href="" class="btn btn-primary margin-bottom margin-right">Rate</a> <a href="" class="btn btn-default margin-bottom margin-right" data-dismiss="modal">Cancel</a> </div>
		                            </form>
		                          </div>
		                      </div>
		                      </div>
		                    </div>
		                </div>
		            </div>
				    <!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
			</div>



<!-- END CONTAINER -->
		</div>
	</div>
</div>

<#if requestAttributes.errorMessageList?has_content><#assign errorMessageList=requestAttributes.errorMessageList></#if>
<#if requestAttributes.eventMessageList?has_content><#assign eventMessageList=requestAttributes.eventMessageList></#if>
<#if requestAttributes.serviceValidationException?exists><#assign serviceValidationException = requestAttributes.serviceValidationException></#if>
<#if requestAttributes.uiLabelMap?has_content><#assign uiLabelMap = requestAttributes.uiLabelMap></#if>

<#if !errorMessage?has_content>
  <#assign errorMessage = requestAttributes._ERROR_MESSAGE_?if_exists>
</#if>
<#if !errorMessageList?has_content>
  <#assign errorMessageList = requestAttributes._ERROR_MESSAGE_LIST_?if_exists>
</#if>
<#if !eventMessage?has_content>
  <#assign eventMessage = requestAttributes._EVENT_MESSAGE_?if_exists>
</#if>
<#if !eventMessageList?has_content>
  <#assign eventMessageList = requestAttributes._EVENT_MESSAGE_LIST_?if_exists>
</#if>

<#assign errorMsgData = ""/>
<#assign sucessMsgData = ""/>

<#if eventMessageList?exists && eventMessageList?has_content>
  	<#list eventMessageList as eventMessageD>
  		<#assign sucessMsgData = sucessMsgData+eventMessageD/>
  	</#list>
</#if>

<#if errorMessageList?exists && errorMessageList?has_content>
  	<#list errorMessageList as errorMessageD>
  		<#assign errorMsgData = errorMsgData+errorMessageD/>
  	</#list>
</#if>

<#assign isValidToken = requestAttributes.isValidToken?if_exists>
<#assign showAlert = requestAttributes.showAlert?if_exists>
<#assign showRenewAlert = requestAttributes.showRenewAlert?if_exists>
<#assign expireDate = requestAttributes.expireDate?if_exists>
<#assign stopService = requestAttributes.stopService?if_exists>

<div class="page-footer">
	<div class="page-footer-inner">
		 2016 &copy; MyFio OMS
	</div>
	<div class="scroll-to-top" style="">
		<i class="icon-arrow-up"></i>
	</div>
</div>

<script type="text/javascript">
jQuery(document).ready(function() {     
  Metronic.init(); // init metronic core components
  Layout.init(); // init current layout
  //Login.init();
  Demo.init();
       // init background slide images
       /*$.backstretch([
        "/metronic/images/bg/1.jpg",
        "/metronic/images/bg/2.jpg",
        "/metronic/images/bg/3.jpg",
        "/metronic/images/bg/4.jpg"
        ],{
          fade: 1000,
          duration: 8000
    }
    );*/
    
    $('#stars').on('starrr:change', function(e, value){
    		$('#count').html(value);
  	});
  
  	$('#stars-2').on('starrr:change', function(e, value){
   		 $('#count-2').html(value);
  	});

  	<#if serviceValidationException?exists && serviceValidationException?has_content>
  		showTosterNotification("info","Error:Please contact administrator");
  	</#if>
  	
  	<#if errorMessage?exists && errorMessage?has_content>
		 showTosterNotification("error","${StringUtil.wrapString(errorMessage)}");
    <#elseif errorMsgData?exists && errorMsgData?has_content>
    	 showTosterNotification("error","${StringUtil.wrapString(errorMsgData)}");
	</#if>
	
	<#if eventMessage?exists && eventMessage?has_content>
		 showTosterNotification("success","${StringUtil.wrapString(eventMessage)}");
	<#elseif sucessMsgData?exists && sucessMsgData?has_content>
		 showTosterNotification("success","${StringUtil.wrapString(sucessMsgData)}");
	</#if>
	
	<#if showAlert?exists && showAlert?has_content && showAlert>
		showSubscriptionAlert('${expireDate?if_exists}');
	<#elseif stopService?exists && stopService?has_content && stopService>
		expireAlert();
	</#if>
	
	<#if showRenewAlert?exists && showRenewAlert?has_content && showRenewAlert>
		showSubscriptionRenewAlert('${expireDate?if_exists}');
	</#if>
});

function showTosterNotification(type,msg){
    var title="";
	toastr.options = {
		  "closeButton": true,
		  "debug": false,
		  "positionClass": "toast-top-center",
		  "onclick": null,
		  "showDuration": "3000",
		  "hideDuration": "2000",
		  "timeOut": "2000",
		  "extendedTimeOut": "2000",
		  "showEasing": "swing",
		  "hideEasing": "linear",
		  "showMethod": "fadeIn",
		  "hideMethod": "fadeOut"
		};
		
	$("#toastrOptions").text("Command: toastr[" + type + "](\"" + msg + (title ? "\", \"" + title : '') + "\")\n\ntoastr.options = " + JSON.stringify(toastr.options, null, 2));
	var $toast = toastr[type](msg, title); // Wire up an event handler to a button in the toast, if it exists
    $toastlast = $toast;
}
function showSubscriptionAlert(dateVal){
    var msg = '<div class="subsriptionError">Your subscription is going to expire on '+dateVal+'.Please Upgrade your plan.</div>';
	bootbox.dialog({
        message: msg,
        title: "Subscription Remainder Alert",
        buttons: {
            danger: {
                label: "Upgrade!",
                className: "btn btn-primary",
                callback: function() {
                      window.location="<@ofbizUrl>expireSubscriptionPage</@ofbizUrl>";
                }
            }
            
        }
    });
 }
 function showSubscriptionRenewAlert(dateVal){
    var msg = '<div class="subsriptionError">Your subscription is going to expire on '+dateVal+'.Please Renew your plan.</div>';
	bootbox.dialog({
        message: msg,
        title: "Subscription Remainder Alert",
        buttons: {
            danger: {
                label: "Renew!",
                className: "btn btn-primary",
                callback: function() {
                      window.location="<@ofbizUrl>renewalSubscriptionPage</@ofbizUrl>";
                }
            }
            
        }
    });
 }
 
 function expireAlert(){
    var msg = '<div class="subsriptionError">Your subscription plan has expired. Please renew or upgrade your plan.</div>';
	bootbox.dialog({
        message: msg,
        title: "Subscription Remainder Alert",
        buttons: {
            danger: {
                label: "Upgrade!",
                className: "btn btn-primary",
                callback: function() {
                      window.location="<@ofbizUrl>expireSubscriptionPage</@ofbizUrl>";
                }
            }
            
        },
        onEscape: function() {
        	 window.location="<@ofbizUrl>expireSubscriptionPage</@ofbizUrl>";
    	}
    });
 }
</script>
<!-- END JAVASCRIPTS -->

</body>
</html>