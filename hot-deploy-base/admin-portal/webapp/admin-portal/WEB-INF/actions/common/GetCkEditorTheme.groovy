import org.fio.admin.portal.event.AjaxEvents;
import org.fio.homeapps.util.DataUtil;

Map<String, Object> data = new HashMap<String, Object>();

String ckEditorTheme =  DataUtil.getGlobalValue(delegator, "CK_EDITOR_THEME", "white");

data.put("ckEditorTheme", ckEditorTheme);

return AjaxEvents.doJSONResponse(response, data);
