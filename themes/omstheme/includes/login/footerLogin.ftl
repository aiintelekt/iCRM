
<!-- END CONTAINER -->
		</div>
	</div>
</div>

<div class="page-footer">
	<!--<div class="page-footer-inner" style="color: #333;">
		 2016 &copy; MyFio OMS
	</div>-->
	 <div style="text-align:center;font-size: 10px;text-decoration: none;">
   			2016 © <img alt="myFiOERP" src="/omstheme/img/myfiooms.jpg" height="20" width="70" border="0">
		 Professional Edition of myFiO OMS Build 2016.2. All Rights Reserved<span id="quarterYear" style="display:none"></span>
    </div>
    <div style="text-align:center;font-size: 10px;text-decoration: none;">
    	     Powered by <a href="http://www.groupfio.com/"><strong>Group FiO OSI.</strong></a>
    </div>
	<div class="scroll-to-top" style="">
		<i class="icon-arrow-up"></i>
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

<#if eventMessageList?has_content>
  	<#list eventMessageList as eventMessageD>
  		<#assign sucessMsgData = sucessMsgData+eventMessageD/>
  	</#list>
</#if>

<#if errorMessageList?has_content>
  	<#list errorMessageList as errorMessageD>
  		<#assign errorMsgData = errorMsgData+errorMessageD/>
  	</#list>
</#if>

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
  	
  	<#if errorMessage?exists && errorMessage?has_content>
		 showTosterNotification("error","${errorMessage}");
    <#elseif errorMsgData?exists && errorMsgData?has_content>
    	 showTosterNotification("error","${errorMsgData}");
	</#if>
	
	<#if eventMessage?exists && eventMessage?has_content>
		 showTosterNotification("success","${eventMessage}");
	<#elseif sucessMsgData?exists && sucessMsgData?has_content>
		 showTosterNotification("success","${sucessMsgData}");
	</#if>
});

function showTosterNotification(type,msg){
    var title="";
	toastr.options = {
		  "closeButton": true,
		  "debug": false,
		  "positionClass": "toast-top-center",
		  "onclick": null,
		  "showDuration": "5000",
		  "hideDuration": "5000",
		  "timeOut": "5000",
		  "extendedTimeOut": "5000",
		  "showEasing": "swing",
		  "hideEasing": "linear",
		  "showMethod": "fadeIn",
		  "hideMethod": "fadeOut"
		};
		
	$("#toastrOptions").text("Command: toastr[" + type + "](\"" + msg + (title ? "\", \"" + title : '') + "\")\n\ntoastr.options = " + JSON.stringify(toastr.options, null, 2));
	var $toast = toastr[type](msg, title); // Wire up an event handler to a button in the toast, if it exists
    $toastlast = $toast;
}
</script>
<!-- END JAVASCRIPTS -->

</body>
</html>