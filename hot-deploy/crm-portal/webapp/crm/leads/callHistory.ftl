<script>
   $(function(){
       $(document).ready(function(){
           $("#callHistoryStatus").DataTable({
               "lengthMenu" : false,
               "filter" : false,
               "lengthChange" : false,
               "pageLength" : 5,
               "order": [[ 0, "desc" ]]
           });
       });
   });
</script>

<div class="table-responsive">
   <table class="table table-striped" id="callHistoryStatus">
         <thead>
            <tr>
               <th>Date</th>
               <th>Call Status</th>
               <th>Call Sub Status</th>
               <th>CSR Name</th>
            </tr>
         </thead>
         <tfoot>
         </tfoot>
         <tbody>
            <#if callStatusHistory?has_content>
            <#list callStatusHistory as callStatusHistoryGV>
            <tr>
               <td>${callStatusHistoryGV.createdStamp?if_exists}</td>
               <td>${callStatusHistoryGV.description?if_exists}</td>
               <td>${callStatusHistoryGV.subStatusDescription?if_exists}</td>
               <td>${callStatusHistoryGV.csrName?if_exists}</td>
            </tr>
            </#list>
            </#if>
         </tbody>
      </table>
   </div>
<br/>