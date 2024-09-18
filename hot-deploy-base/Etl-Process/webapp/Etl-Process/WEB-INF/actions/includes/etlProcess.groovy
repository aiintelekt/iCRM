import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.*;

processId = request.getParameter("processId");
isUpdate  = request.getParameter("isUpdate");

if(UtilValidate.isNotEmpty(processId) && UtilValidate.isNotEmpty(isUpdate))
{
	//etlProcess = delegator.findByPrimaryKey("EtlProcess",UtilMisc.toMap("processId",processId));
	GenericValue etlProcess = EntityQuery.use(delegator).from("EtlProcess").where("processId", processId).cache().queryFirst();
	context.put("etlProcessProduct",etlProcess);
}