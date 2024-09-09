<div id="reassignModal" class="modal fade" role="dialog">
   <div class="modal-dialog modal-sm">
      <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">Assign To</h4>
                <button type="reset" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body">
                <form method="post" action="#" id="reassignModal" class="form-horizontal" name="" novalidate="novalidate" data-toggle="validator">
                    <div class="row p-1">
                   <div class="col-md-12 col-lg-12 col-sm-12 ">
                    <@inputHidden id="csrloginId" />
                   
                     User<input type="radio" id="user" name="emp" value="user">
                     Team<input type="radio" id="team" name="emp" value="team">
                    
                    <div id="textboxUser">
                     <#assign componentList = (Static["org.ofbiz.entity.util.EntityQuery"].use(delegator).from("Enumeration").where("enumTypeId", "CALL_OUT_COME").queryList())?if_exists>
                           <#assign components = (Static["org.fio.admin.portal.util.DataHelper"].getDropDownOptions(componentList, "enumId","description"))?if_exists />
                              <@dropdownCell
                              	label="User/Team"
                              	required=true
                                id="userText"
                                placeholder="Select User"
                                options=components!
                                value="${requestParameters.callOutCome?if_exists}"
                              />
                    </div>
                                        
                    
                     </div>
                     </div>
                    </div>
                             <div class="modal-footer">
                                  <@submit class="btn btn-sm btn-primary navbar-dark" label="${uiLabelMap.create}"/>
                                </form>
                              </div>
                           </div>
                        </div>
                     </div>
                     
  <script>
  var csrloginId=document.getElementById("csrloginId");
  console.log("csrloginId"+csrloginId);
alert("hhell"+csrloginId);
$(function(){
  
  
  });
  </script>