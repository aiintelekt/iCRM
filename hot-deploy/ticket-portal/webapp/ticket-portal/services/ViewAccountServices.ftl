<#include "component://admin-portal/webapp/admin-portal/global/ofbizFormMacros.ftl"/>
   
<div class="page-header border-b pt-2">
	<@headerH2
    	title="Account Services"
    />
</div>
<div class="clearfix"></div>
<div class="row">
  	<div class="col-md-12">
   		<@headerH4
    		title="Cheque Book Request"
    	/> 
  	</div>
  	<div class="col-md-12 col-lg-6 col-sm-12 ">
    	<@displayCell
     		label="Account#"
     		value="${responseObj.accountNumber?if_exists}"
  		/>
       	<@displayCell
    		label="Amt of Cheque"
     		value=""
  		/>
  		<@displayCell
     		label="Number of Copies"
     		value=""
  		/>
  	</div>
  	<div class="col-lg-6 col-md-12 col-sm-12 ">
   		<@displayCell
     		label="Check #"
     		value=""
  		/>
       	<@displayCell
     		label="Date of Cheque"
     		value=""
  		/>
  		<@displayCell
     		label="Charge Fee"
     		value=""
  		/>
  		<@displayCell
     		label="Mail Copy"
     		value="No"
  		/>
	</div>
</div>
<div class="row">
	<div class="col-md-12">
    	<@headerH4
    		title="Cheque Book Order"
    	/> 
  	</div>
  	<div class="col-md-12 col-lg-6 col-sm-12 ">
    	<@displayCell
     		label="Account#"
     		value="${responseObj.accountNumber?if_exists}"
  		/>
       	<@displayCell
     		label="Collection Branch"
     		value=""
  		/>
  	</div>
  	<div class="col-md-12 col-lg-6 col-sm-12 ">
  		<@displayCell
     		label="Charge for Order"
     		value=""
  		/>
  		<@displayCell
     		label="Supervisors Approval"
     		value="No"
  		 />
  		<@displayCell
    		label="Collection Method"
     		value="No"
  		/>
 	</div>
</div>
<div class="row">
	<div class="col-md-12">
  		<@headerH4
    		title="Fee Waiver"
    	/> 
  	</div>
  	<div class="col-md-12 col-lg-6 col-sm-12 ">
    	<@displayCell
     		label="Account#"
     		value="${responseObj.accountNumber?if_exists}"
  		/>
       	<@displayCell
     		label="Fee Type"
     		value=""
  		/>
  		<@displayCell
     		label="Fee Amount"
     		value=""
  		/>
  		<@displayCell
     		label="GST Amount"
     		value="No"
  		/>
  	</div>
  	<div class="col-lg-6 col-md-12 col-sm-12 ">
    	<@displayCell
     		label="Reason"
     		value=""
  		/>
       	<@displayCell
     		label="GL Number"
     		value=""
  		/>
  		<@displayCell
     		label="PC Code"
     		value=""
  		/>
  		<@displayCell
     		label="Comments"
     		value="No"
  		/>
  		<@displayCell
     		label="Validity Periods (Months)"
     		value="No"
  		/>
  	</div>
</div>
<div class="row">
	<div class="col-md-12">
  		<@headerH4
    		title="Fee Reversal"
    	/> 
  	</div>
  	<div class="col-md-12 col-lg-6 col-sm-12 ">
    	<@displayCell
     		label="Fee Type"
     		value=""
  		/>
  		<@displayCell
     		label="Fee Amount"
     		value=""
  		/>
  		<@displayCell
     		label="Account Charged"
     		value=""
  		/>
  		<@displayCell
     		label="GST Amount"
     		value="No"
  		/>
  		<@displayCell
     		label="GL Number"
     		value="No"
  		/>
  	</div>
  	<div class="col-lg-6 col-md-12 col-sm-12 ">
    	<@displayCell
     		label="PC Code"
     		value=""
  		/>
       	<@displayCell
     		label="Reason"
     		value=""
  		/>
  		<@displayCell
     		label="To Account #"
     		value=""
  		/>
  		<@displayCell
     		label="Reversal Amount"
     		value="No"
  		/>
  		<@displayCell
     		label="Comments"
     		value="No"
  		/>
  	</div>
</div>
<div class="row">
	<div class="col-md-12">
   		<@headerH4
    		title="Fund Transfer"
    	/>
  	</div>
  	<div class="col-md-12 col-lg-6 col-sm-12 ">
    	<@displayCell
     		label="From Account #"
     		value=""
  		/>
  		<@displayCell
     		label="Send confirmation"
     		value=""
  		/>
  		<@displayCell
     		label="Amount"
     		value=""
  		/>
  		<@displayCell
     		label="Transfer Date"
     		value="No"
  		/>
  	</div>
  	<div class="col-md-12 col-lg-6 col-sm-12 ">
    	<@displayCell
     		label="To Account"
     		value=""
  		/>
  		<@displayCell
     		label="Charge Fee"
     		value=""
  		/>
  		<@displayCell
     		label="Transfer Agreement"
     		value=""
  		/>
  		<@displayCell
     		label="Transfer Type"
     		value="No"
  		/>
  	</div>
</div>
<div class="row">
	<div class="col-md-12">
  		<@headerH4
    		title="Statement Copy"
    	/>
  	</div>
  	<div class="col-md-12 col-lg-6 col-sm-12 ">
    	<@displayCell
     		label="Account#"
     		value="${responseObj.accountNumber?if_exists}"
  		/>
  		<@displayCell
     		label="Month"
     		value=""
  		/>
  		<@displayCell
     		label="Year"
     		value=""
  		/>
  		<@displayCell
     		label="Deducting Account #"
     		value="No"
  		 />
  		<@displayCell
     		label="Method"
     		value="No"
  		/>
  		<@displayCell
     		label="No. of Copies"
     		value="No"
  		/>
  		<@displayCell
     		label="Charge Fee"
     		value="No"
  		/>
	</div>
  	<div class="col-lg-6 col-md-12 col-sm-12 ">
   		<@displayCell
    		label="Redirect Address Code"
     		value=""
  		/>
  		<@displayCell
     		label="Redirect Address - Block #"
     		value=""
  		/>
  		<@displayCell
     		label="Redirect Address - Level #"
     		value=""
  		/>
  		<@displayCell
     		label="Redirect Address - Unit #"
     		value="No"
  		 />
  		<@displayCell
     		label="Redirect Address - Address"
     		value="No"
  		/>
  		<@displayCell
     		label="Redirect Address - Postal Code"
     		value="No"
  		/>
	</div>
</div>
<div class="row">
	<div class="col-md-12">
  		<@headerH4
    		title="Stop Cheque"
    	/>
  	</div>
  	<div class="col-md-12 col-lg-6 col-sm-12 ">
  		<@displayCell
     		label="Account#"
     		value="${responseObj.accountNumber?if_exists}"
  		/>
  		<@displayCell
     		label="Cheque#"
     		value=""
  		/>
  		<@displayCell
     		label="Payee Name"
     		value=""
  		/>
  		<@displayCell
     		label="Amt of Cheque"
     		value="No"
  		/>
  	</div>
  	<div class="col-lg-6 col-md-12 col-sm-12 ">
    	<@displayCell
     		label="Item Not Posted"
     		value=""
  		/>
  		<@displayCell
     		label="Order Sent for Signature"
     		value=""
  		/>
  		<@displayCell
     		label="Order Received"
     		value="No"
  		/>
  		<@displayCell
     		label="Charge Fee"
     		value=""
  		/>
	</div>
</div>
