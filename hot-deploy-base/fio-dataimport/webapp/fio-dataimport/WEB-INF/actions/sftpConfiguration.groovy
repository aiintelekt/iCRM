import org.ofbiz.base.util.*;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityQuery;

	sftpConfigurate = delegator.findAll("SftpConfiguration",false);
	if(UtilValidate.isNotEmpty(sftpConfigurate)){
		context.put("sftpConfigurations",sftpConfigurate);
	}

String seqId= request.getParameter("seqId");

GenericValue sftpConfig = EntityQuery.use(delegator).from("SftpConfiguration").where("seqId", seqId).cache().queryFirst();

if(UtilValidate.isNotEmpty(sftpConfig))
{
context.put("sftpConfig",sftpConfig);
}

