import javax.swing.DebugGraphics
import org.fio.admin.portal.constant.AdminPortalConstant;
import org.fio.admin.portal.util.DataHelper
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.util.EntityQuery
import org.ofbiz.base.util.*
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

inputContext = new LinkedHashMap<String, Object>();



smtpRelayHost = delegator.findOne("SystemProperty", [systemResourceId : "general",systemPropertyId : "general.sftp.host"], false);
if(UtilValidate.isNotEmpty(smtpRelayHost)){
	inputContext.put("host", smtpRelayHost.getString("systemPropertyValue"));
}

authUser = delegator.findOne("SystemProperty", [systemResourceId : "general",systemPropertyId : "general.sftp.user"], false);
if(UtilValidate.isNotEmpty(authUser)){
	inputContext.put("user", authUser.getString("systemPropertyValue"));
}

authPassword = delegator.findOne("SystemProperty", [systemResourceId : "general",systemPropertyId : "general.sftp.password"], false);
if(UtilValidate.isNotEmpty(authPassword)){
	inputContext.put("password", authPassword.getString("systemPropertyValue"));
}

sftpPort = delegator.findOne("SystemProperty", [systemResourceId : "general",systemPropertyId : "general.sftp.port"], false);
if(UtilValidate.isNotEmpty(sftpPort)){
	inputContext.put("port", sftpPort.getString("systemPropertyValue"));
}
stfpLoc = delegator.findOne("SystemProperty", [systemResourceId : "general",systemPropertyId : "general.sftp.location"], false);
if(UtilValidate.isNotEmpty(stfpLoc)){
	inputContext.put("location", stfpLoc.getString("systemPropertyValue"));
}



context.put("inputContext", inputContext);
