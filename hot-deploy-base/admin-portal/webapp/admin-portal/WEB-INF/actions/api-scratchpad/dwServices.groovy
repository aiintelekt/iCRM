import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.Debug;

webServiceTest = orders = delegator.findList("WebServiceTest",  EntityCondition.makeCondition([serviceType : "dwService"]), ['serviceName', 'description'] as Set, ['sequence'] as LinkedList, null, false);
context.webServiceTest = webServiceTest;