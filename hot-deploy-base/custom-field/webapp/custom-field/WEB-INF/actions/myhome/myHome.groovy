/**
 * @author Sharif Ul Islam
 * @since June 16, 2015
 *
 */
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilMisc;
import java.util.Date;
import java.text.SimpleDateFormat;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.entity.util.EntityUtil;
import org.groupfio.custom.field.util.DataHelper;
import org.ofbiz.entity.datasource.GenericHelperInfo;
import org.ofbiz.entity.jdbc.SQLProcessor;
import org.groupfio.custom.field.util.QueryUtil;

delegator = request.getAttribute("delegator");
uiLabelMap = UtilProperties.getResourceBundleMap("CustomFieldUiLabels", locale);
