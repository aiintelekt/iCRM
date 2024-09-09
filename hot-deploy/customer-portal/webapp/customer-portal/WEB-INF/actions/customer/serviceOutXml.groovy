import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import java.util.List;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.*;

/*
XmlInput=requestParameters.get("XmlInput");


if(XmlInput!=null && !XmlInput.equals("")){

serviceInput = UtilMisc.toMap("XmlInput",XmlInput);

Map result = dispatcher.runSync("findCustomers",serviceInput);

XmlOutput=(String)result.get("XmlOutput");

context.put("XmlOutput",XmlOutput);
}

*/

String XmlOutput=request.getAttribute("XmlOutput");

context.put("XmlOutput",XmlOutput);