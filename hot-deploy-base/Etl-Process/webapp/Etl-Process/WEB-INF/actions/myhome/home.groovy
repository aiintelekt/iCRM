
import java.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.*;
import org.ofbiz.securityext.login.*;
import org.ofbiz.common.*;




	etlMappingElements = delegator.findAll("EtlMappingElements");
	
	if(UtilValidate.isNotEmpty(etlMappingElements))
	{
		context.put("etlMappingElements",etlMappingElements);
	}
	Debug.logInfo("testing   "+etlMappingElements,"");