import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;

GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

if (UtilValidate.isNotEmpty(userLogin)) {
	String userLoginId =  userLogin.getString("userLoginId");
	String partyId = userLogin.getString("partyId");
	context.put("userLoginId", userLoginId);
	context.put("userLoginPartyId", partyId);
}