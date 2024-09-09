<#include "component://homeapps/webapp/homeapps/lib/ofbizFormMacros.ftl"/>
<div class="page-header">
   <h2 class="float-left">Find Orders</h2>
</div>
<form name="loyaltySearchOrdersN" id="loyaltySearchOrdersN" method="post" action="/crmsfa/control/loyaltySearchOrderService" novalidate="novalidate">
   <div class="row padding-r">
      <div class="col-md-4 col-sm-4">
         <@generalInput 
         id="orderId"
         name="orderId"
         label="Order ID"
         placeholder="Order ID"
         value=""
         required=false
         />
         <@generalInput 
         id="orderId"
         name="orderId"
         label="Order Name"
         placeholder="Order Name"
         value=""
         required=false
         />
         <@generalInput 
         id="orderId"
         name="orderId"
         label="Status"
         placeholder="Status"
         value=""
         required=false
         />
         <@inputDate 
         id="startDate"
         label="From Date"
         value=""
         placeholder="DD-MM-YYYY"
         required = false
         default=true
         />
      </div>
      <div class="col-md-4 col-sm-4">
         <@generalInput 
         id="orderId"
         name="orderId"
         label="External ID"
         placeholder="External ID"
         value=""
         required=false
         />
         <@generalInput 
         id="orderId"
         name="orderId"
         label="Product Store"
         placeholder="Product Store"
         value=""
         required=false
         />
         <@generalInput 
         id="orderId"
         name="orderId"
         label="Product"
         placeholder="Product"
         value=""
         required=false
         />
         <@inputDate 
         id="thruDate"
         label="Thru Date"
         value=""
         placeholder="DD-MM-YYYY"
         required = false
         default=true
         />
      </div>
      </div>
      <div class="col-md-12 col-sm-12">
      <div class="form-group row">
         <div class="offset-sm-2 col-sm-8">
            <button type="reset" class="btn btn-sm btn-primary mt">Find</button>
         </div>
      </div>
   </div>
</form>
<div class="page-header">
   <h2 class="float-left">Orders List</h2>
</div>
<div class="table-responsive">
   <table class="table table-striped" id="dtable">
      <thead>
         <tr>
            <th>Order ID</th>
            <th>Transaction Date</th>
            <th>Customer ID</th>
            <th>Customer Name</th>
            <th>Store</th>
            <th>Order Total</th>
         </tr>
      </thead>
      <tbody>
      </tbody>
   </table>
</div>