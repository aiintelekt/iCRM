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

List<GenericValue> mailEngineDetails = delegator.findByAnd("SystemProperty", UtilMisc.toMap("systemResourceId", "general","systemPropertyId", "mailEngine"), null, false);
mailEngine = EntityUtil.getFirst(mailEngineDetails);
String mailEngine = mailEngine.getString("systemPropertyValue");

mailNotificationsEnabled = delegator.findOne("SystemProperty", [systemResourceId : mailEngine,systemPropertyId : "mail.notifications.enabled"], false);
if(mailNotificationsEnabled){
	inputContext.put("mail.notifications.enabled", mailNotificationsEnabled.getString("systemPropertyValue"));
}

smtpRelayHost = delegator.findOne("SystemProperty", [systemResourceId : mailEngine,systemPropertyId : "mail.smtp.relay.host"], false);
if(smtpRelayHost){
	inputContext.put("mail.smtp.relay.host", smtpRelayHost.getString("systemPropertyValue"));
}

authUser = delegator.findOne("SystemProperty", [systemResourceId : mailEngine,systemPropertyId : "mail.smtp.auth.user"], false);
if(authUser){
	inputContext.put("mail.smtp.auth.user", authUser.getString("systemPropertyValue"));
}

authPassword = delegator.findOne("SystemProperty", [systemResourceId : mailEngine,systemPropertyId : "mail.smtp.auth.password"], false);
if(authPassword){
	inputContext.put("mail.smtp.auth.password", authPassword.getString("systemPropertyValue"));
}

smtpPort = delegator.findOne("SystemProperty", [systemResourceId : mailEngine,systemPropertyId : "mail.smtp.port"], false);
if(smtpPort){
	inputContext.put("mail.smtp.port", smtpPort.getString("systemPropertyValue"));
}
startTlsEnable = delegator.findOne("SystemProperty", [systemResourceId : mailEngine,systemPropertyId : "mail.smtp.starttls.enable"], false);
if(startTlsEnable){
	inputContext.put("mail.smtp.starttls.enable", startTlsEnable.getString("systemPropertyValue"));
}

socketFactoryPort = delegator.findOne("SystemProperty", [systemResourceId : mailEngine,systemPropertyId : "mail.smtp.socketFactory.port"], false);
if(socketFactoryPort){
	inputContext.put("mail.smtp.socketFactory.port", socketFactoryPort.getString("systemPropertyValue"));
}

authRequire = delegator.findOne("SystemProperty", [systemResourceId : mailEngine,systemPropertyId : "mail.smtp.auth.require"], false);
if(authRequire){
	inputContext.put("mail.smtp.auth.require", authRequire.getString("systemPropertyValue"));
}
inputContext.put("mailEngine", mailEngine);

context.put("inputContext", inputContext);
