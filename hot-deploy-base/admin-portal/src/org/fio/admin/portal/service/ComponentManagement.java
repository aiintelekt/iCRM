package org.fio.admin.portal.service;

import java.util.Map;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;
/**
 * 
 * @author Nirmal Kumar.P
 * @since 29-10-2022
 * 
 */
public class ComponentManagement{
	 
	public static Map<String, Object> updateComponent(DispatchContext dctx, Map<String, Object> context) throws GenericEntityException {
		Delegator delegator= dctx.getDelegator();
 		String componentId = (String) context.get("componentId");
		String uiLabels =(String) context.get("uiLabels");
		String isHide =(String) context.get("isHide");
		String requestUri =(String) context.get("requestUri"); 

		Map<String, Object> map = ServiceUtil.returnSuccess("Component Updated Successfully");
		
		GenericValue components =delegator.findOne("OfbizComponentAccess",
					UtilMisc.toMap("componentId", componentId), false);
		
		components.put("componentId", componentId);
		components.put("uiLabels", uiLabels);
		components.put("isHide", isHide);
		components.put("requestUri", requestUri);
 		delegator.store(components);
		return map;
	}
	public static Map<String, Object> updateTabShortcut(DispatchContext dctx, Map<String, Object> context) throws GenericEntityException {
		Delegator delegator= dctx.getDelegator();

		String tabId = (String) context.get("tabId");
		String pageId =(String) context.get("pageId");
		String permissionId =(String) context.get("permissionId");
		String componentId =(String) context.get("componentId");
		String tabName = (String) context.get("tabName");
		String requestUri = (String) context.get("requestUri");
		String isDisabled = (String) context.get("isDisabled");

		Map<String, Object> map = ServiceUtil.returnSuccess("Tab Shortcut Updated Successfully");
		
		GenericValue tabs =delegator.findOne("OfbizPageSecurity",
					UtilMisc.toMap("tabId", tabId,"pageId",pageId,"permissionId",permissionId,"componentId",componentId), false);
		
		tabs.put("tabId", tabId);
		tabs.put("pageId", pageId);
		tabs.put("permissionId", permissionId);
		tabs.put("componentId", componentId);
		tabs.put("uiLabels", tabName);
		tabs.put("requestUri", requestUri);
		tabs.put("isDisabled", isDisabled);
		delegator.store(tabs);
		return map;
	}
	public static Map<String, Object> updateSubMenu(DispatchContext dctx, Map<String, Object> context) throws GenericEntityException {
		Delegator delegator= dctx.getDelegator();
 		String tabId = (String) context.get("tabId");
		String pageId =(String) context.get("pageId");
		String permissionId =(String) context.get("permissionId");
		String componentId =(String) context.get("componentId");
		String tabName = (String) context.get("tabName");
		String requestUri = (String) context.get("requestUri");
		String isDisabled = (String) context.get("isDisabled");
		String shortcutId = (String) context.get("shortcutId");

		Map<String, Object> map = ServiceUtil.returnSuccess("Sub Menu Updated Successfully");
		
		GenericValue tabs =delegator.findOne("OfbizTabSecurityShortcut",
					UtilMisc.toMap("tabId", tabId,"pageId",pageId,"permissionId",permissionId,"componentId",componentId,"shortcutId",shortcutId), false);
		
		tabs.put("tabId", tabId);
		tabs.put("pageId", pageId);
		tabs.put("permissionId", permissionId);
		tabs.put("componentId", componentId);
		tabs.put("uiLabels", tabName);
		tabs.put("requestUri", requestUri);
		tabs.put("isDisabled", isDisabled);
		tabs.put("shortcutId", shortcutId);
 		delegator.store(tabs);
		return map;
	}
}
