import java.sql.BatchUpdateException;

import javolution.util.FastList

import org.ofbiz.entity.condition.*
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.fio.crm.util.DataHelper;
import org.ofbiz.base.util.UtilProperties;
import java.util.LinkedHashMap;





importFileOptions = new LinkedHashMap<String, Object>();

importFileOptions.put("CSV", "csv");
//importFileOptions.put("EXCEL", "excel");
//importFileOptions.put("TEXT", "text");
//importFileOptions.put("XML", "xml");
//importFileOptions.put("JSON", "json");

context.put("importFileOptions", importFileOptions);