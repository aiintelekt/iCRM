<#include "component://homeapps/webapp/homeapps/lib/ofbizFormMacros.ftl"/>

<ul class="nav nav-tabs">
	<#-- <li class="nav-item" id="assigned-customer-tab"><a data-toggle="tab" href="#tab1"
		class="nav-link active">${uiLabelMap.AssignedCustomer!}</a></li> -->
	<li class="nav-item"><a data-toggle="tab" href="#hadoop-tab1"
		class="nav-link active">CaAccount Data</a></li>
	<li class="nav-item"><a data-toggle="tab" href="#hadoop-tab2"
		class="nav-link">Customer Data</a></li>
	<li class="nav-item"><a data-toggle="tab" href="#hadoop-tab3"
		class="nav-link">Facilities Data</a></li>	
	<li class="nav-item"><a data-toggle="tab" href="#hadoop-tab4"
		class="nav-link">FdAccount Data</a></li>	
	<li class="nav-item"><a data-toggle="tab" href="#hadoop-tab5"
		class="nav-link">GcinGpinLinkage Data</a></li>	
	<li class="nav-item"><a data-toggle="tab" href="#hadoop-tab6"
		class="nav-link">LoanAccount Data</a></li>	
	<li class="nav-item"><a data-toggle="tab" href="#hadoop-tab7"
		class="nav-link">TradeAccount Data</a></li>	
	<li class="nav-item"><a data-toggle="tab" href="#hadoop-tab8"
		class="nav-link">Contact Data</a></li>						
</ul>

<div class="tab-content">
	
	<div id="hadoop-tab1" class="tab-pane fade show active">
		
		${screens.render("component://crm/webapp/widget/crm/screens/hadoop/HadoopScreens.xml#CaAccountData")}
		
	</div>
	
	<div id="hadoop-tab2" class="tab-pane fade in">
				
		${screens.render("component://crm/webapp/widget/crm/screens/hadoop/HadoopScreens.xml#CustomerData")}
		
	</div>
	
	<div id="hadoop-tab3" class="tab-pane fade in">
				
		${screens.render("component://crm/webapp/widget/crm/screens/hadoop/HadoopScreens.xml#FacilitiesData")}
		
	</div>
	
	<div id="hadoop-tab4" class="tab-pane fade in">
				
		${screens.render("component://crm/webapp/widget/crm/screens/hadoop/HadoopScreens.xml#FdAccountData")}
		
	</div>
	
	<div id="hadoop-tab5" class="tab-pane fade in">
				
		${screens.render("component://crm/webapp/widget/crm/screens/hadoop/HadoopScreens.xml#GcinGpinLinkageData")}
		
	</div>
	
	<div id="hadoop-tab6" class="tab-pane fade in">
				
		${screens.render("component://crm/webapp/widget/crm/screens/hadoop/HadoopScreens.xml#LoanAccountData")}
		
	</div>
	
	<div id="hadoop-tab7" class="tab-pane fade in">
				
		${screens.render("component://crm/webapp/widget/crm/screens/hadoop/HadoopScreens.xml#TradeAccountData")}
		
	</div>
	
	<div id="hadoop-tab8" class="tab-pane fade in">
				
		${screens.render("component://crm/webapp/widget/crm/screens/hadoop/HadoopScreens.xml#ContactData")}
		
	</div>
	
</div>

<script>

jQuery(document).ready(function() {	

$("a[href='#hadoop']").on('shown.bs.tab', function(e) {
	findCaAccounts();
});

$("a[href='#hadoop-tab1']").on('shown.bs.tab', function(e) {
	findCaAccounts();
});
$("a[href='#hadoop-tab2']").on('shown.bs.tab', function(e) {
	findHdaCustomers();
});
$("a[href='#hadoop-tab3']").on('shown.bs.tab', function(e) {
	findHdaFacilities();
});
$("a[href='#hadoop-tab4']").on('shown.bs.tab', function(e) {
	findHdaFdAccounts();
});
$("a[href='#hadoop-tab5']").on('shown.bs.tab', function(e) {
	findHdaGcinGpinLinkages();
});
$("a[href='#hadoop-tab6']").on('shown.bs.tab', function(e) {
	findHdaLoanAccounts();
});
$("a[href='#hadoop-tab7']").on('shown.bs.tab', function(e) {
	findHdaTradeAccounts();
});
$("a[href='#hadoop-tab8']").on('shown.bs.tab', function(e) {
	findHdaContacts();
});

$(".form_datetime").datetimepicker({
    //autoclose: true,
    //isRTL: BootStrapInit.isRTL(),
    //format: "dd MM yyyy - hh:ii",
    //pickerPosition: (BootStrapInit.isRTL() ? "bottom-right" : "bottom-left")
});

});

</script>