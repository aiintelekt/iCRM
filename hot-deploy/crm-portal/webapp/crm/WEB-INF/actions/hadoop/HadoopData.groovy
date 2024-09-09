import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilMisc;
import java.util.HashMap;
import org.fio.crm.util.DataUtil;

delegator = request.getAttribute("delegator");
//uiLabelMapDM = UtilProperties.getResourceBundleMap("DataImporterUiLabels", locale);

//context.uiLabelMapDM = uiLabelMapDM;

context.facilitiesHeaderConfigs = DataUtil.getLatestVersionHeaderConfigs (delegator, "o_uc65_facilities");
context.caAccountHeaderConfigs = DataUtil.getLatestVersionHeaderConfigs (delegator, "o_uc65_ca_account");

//context.customerHeaderConfigs = DataUtil.getLatestVersionHeaderConfigs (delegator, "o_uc65_customer");
context.customerHeaderConfigs = DataUtil.getLatestVersionHeaderConfigs (delegator, "o_uc65_customer_sme");

context.fdAccountHeaderConfigs = DataUtil.getLatestVersionHeaderConfigs (delegator, "o_uc65_fd_account");
context.gcinPpinLinHeaderConfigs = DataUtil.getLatestVersionHeaderConfigs (delegator, "o_uc65_gcin_gpin_lin");
context.loanAccountHeaderConfigs = DataUtil.getLatestVersionHeaderConfigs (delegator, "o_uc65_loan_account");
context.tradeAccountHeaderConfigs = DataUtil.getLatestVersionHeaderConfigs (delegator, "o_uc65_trade_account");
context.contactHeaderConfigs = DataUtil.getLatestVersionHeaderConfigs (delegator, "o_uc65_contact");


