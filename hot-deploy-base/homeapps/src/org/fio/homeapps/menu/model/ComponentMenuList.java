package org.fio.homeapps.menu.model;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.ParserConfigurationException;

import org.fio.homeapps.util.DataHelper;
import org.fio.homeapps.util.DataUtil;
import org.fio.homeapps.util.UtilUserAudit;
import org.ofbiz.base.location.FlexibleLocation;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilXml;
import org.ofbiz.base.util.cache.UtilCache;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityQuery;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.widget.model.MenuFactory;
import org.ofbiz.widget.model.ModelMenu;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * @author Mahendran T
 * @since 26-05-2018
 * */
public class ComponentMenuList {
	public static final String module = MenuFactory.class.getName();
	public static final UtilCache<String, Map<String, ModelMenu>> menuWebappCache = UtilCache.createUtilCache("widget.menu.webappResource", 0, 0, false);
    public static final UtilCache<String, Map<String, ModelMenu>> menuLocationCache = UtilCache.createUtilCache("widget.menu.locationResource", 0, 0, false);
    
    public static Map<String, ModelMenu> readMenuDocument(Document menuFileDoc, String menuLocation) {
        Map<String, ModelMenu> modelMenuMap = new HashMap<String, ModelMenu>();
        if (menuFileDoc != null) {
            // read document and construct ModelMenu for each menu element
            Element rootElement = menuFileDoc.getDocumentElement();
            Map<Integer, String> sortMap = new HashMap<Integer,String>();
            for (Element menuElement: UtilXml.childElementList(rootElement, "menu")){
                ModelMenu modelMenu = new ModelMenu(menuElement, menuLocation);
                String id = UtilValidate.isNotEmpty(modelMenu.getId())?modelMenu.getId() : "";
                
                if(UtilValidate.isNotEmpty(id)) {
                	int index = Integer.parseInt(id);
                    sortMap.put(index, modelMenu.getName());
                }

                modelMenuMap.put(modelMenu.getName(), modelMenu);
            }
            
            Map<String, ModelMenu> finalMenuMap = new LinkedHashMap<String, ModelMenu>();
            if(!sortMap.isEmpty()) {
            	sortMap.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEachOrdered(e->{
                		finalMenuMap.put(e.getValue(), modelMenuMap.get(e.getValue()));
                	
                });
            }
            
            if(UtilValidate.isNotEmpty(finalMenuMap)) {
            	return finalMenuMap;
            } 
         }
        return modelMenuMap;
    }
	 public static List<ModelMenu> getComponentMenuList(String resourceName) throws IOException, SAXException, ParserConfigurationException {
	        Map<String, ModelMenu> modelMenuMap = menuLocationCache.get(resourceName);
	        if (modelMenuMap == null) {
	            URL menuFileUrl = FlexibleLocation.resolveLocation(resourceName);
	            Document menuFileDoc = UtilXml.readXmlDocument(menuFileUrl, true, true);
	            modelMenuMap = readMenuDocument(menuFileDoc, resourceName);
	            menuLocationCache.putIfAbsent(resourceName, modelMenuMap);
	            modelMenuMap = menuLocationCache.get(resourceName);
	            //Debug.logInfo("Menus---->"+modelMenuMap, module);
	        }
	        List<ModelMenu> menuList = new ArrayList<ModelMenu>();
	        if (UtilValidate.isNotEmpty(modelMenuMap)) {
	        	/*modelMenuMap.entrySet().stream().forEach(e -> {
	        		menuList.add(e.getValue());
	        	});*/
	        	modelMenuMap.forEach((k,v)->{menuList.add(v);});
	        }
	        if (menuList.isEmpty()) {
	            throw new IllegalArgumentException("Could not find menu in location [" + resourceName + "]");
	        }
	        /*ModelMenu modelMenu = modelMenuMap.get(menuName);
	        if (modelMenu == null) {
	            throw new IllegalArgumentException("Could not find menu with name [" + menuName + "] in location [" + resourceName + "]");
	        }*/
	        //Debug.logInfo("Menu List from"+resourceName+"--->"+menuList, module);
	        return menuList;
	    }

	 public static Map < String, Object> getComponentMenuList(DispatchContext dctx, Map < String, Object> context) {
		 Map < String, Object> result = new HashMap < String, Object> ();
		 Delegator delegator = (Delegator) dctx.getDelegator();
		 
		 GenericValue userLogin = (GenericValue) context.get("userLogin");
		 
		 String activeApp = (String) context.get("activeApp");
		 String sectionName = (String) context.get("sectionName");
		 
		 List<Map < String, Object>> menuList = new LinkedList<Map < String, Object>> ();
		 if (UtilValidate.isNotEmpty(activeApp) && userLogin != null && userLogin.size()> 0) {
			 String userLoginId = (String) userLogin.getString("userLoginId");
			 String partyId = (String) userLogin.getString("partyId");
			 if (UtilValidate.isNotEmpty(userLoginId) && UtilValidate.isNotEmpty(partyId)) {
				 try {
					 boolean validUser = false;
					 boolean fullAdmin = false;
					 Set<String> permissionIdSet = new HashSet<String> ();
					 
					 if(UtilUserAudit.isFullAccessUser(delegator, userLogin, activeApp)) {
						 validUser = true;
						 fullAdmin = true;
					 } else {
						 EntityQuery securityGroup = EntityQuery.use(delegator).from("SecurityGroup").where("customSecurityGroupType", "Y");
						 List<GenericValue> userLoginSecurityGroup = EntityQuery.use(delegator).from("UserLoginSecurityGroup")
								 .where(EntityCondition.makeCondition("userLoginId", EntityOperator.EQUALS, userLoginId),
										 EntityCondition.makeCondition("groupId", EntityOperator.IN, EntityUtil.getFieldListFromEntityList(securityGroup.queryList(), "groupId", true))
										 )
								 .filterByDate().queryList();
						 if (userLoginSecurityGroup != null && userLoginSecurityGroup.size()> 0) {
							 List<GenericValue> securityGroupPermission = EntityQuery.use(delegator).from("SecurityGroupPermission")
									 .where(EntityCondition.makeCondition("groupId", EntityOperator.IN, EntityUtil.getFieldListFromEntityList(userLoginSecurityGroup, "groupId", true)))
									 .queryList();

							 if (UtilValidate.isNotEmpty(securityGroupPermission)) {
								 List<String> permissionIdList = EntityUtil.getFieldListFromEntityList(securityGroupPermission, "permissionId", true);

								 if (permissionIdList != null && permissionIdList.size()> 0) {
									 validUser = true;
									 permissionIdSet.addAll(permissionIdList);
									 List<GenericValue> securityPermissionList = EntityQuery.use(delegator).from("SecurityPermission")
											 .select("parentPermissionId").distinct()
											 .where(EntityCondition.makeCondition("permissionId", EntityOperator.IN, permissionIdList),
													 EntityCondition.makeCondition("actionType", EntityOperator.EQUALS, "EDIT")).queryList();
									 if(securityPermissionList != null && securityPermissionList.size() > 0) {
										 for(GenericValue permission : securityPermissionList) {
											 GenericValue securityPermission = EntityQuery.use(delegator).from("SecurityPermission")
													 .where("parentPermissionId", permission.getString("parentPermissionId"), "actionType", "VIEW")
													 .queryFirst();
											 if(UtilValidate.isNotEmpty(securityPermission)) {
												 permissionIdSet.add(securityPermission.getString("permissionId"));
											 }
										 }

									 }
								 }
							 }
						 }
					 }
					 if(validUser) {
						 List<EntityCondition> ofbizComponentAccessCond = new LinkedList<EntityCondition>();
						 EntityCondition permissionIdCond = EntityCondition.makeCondition("permissionId", EntityOperator.IN, permissionIdSet);
						 if(!fullAdmin) {
							 ofbizComponentAccessCond.add(permissionIdCond);
						 }
						 ofbizComponentAccessCond.add(EntityCondition.makeCondition("componentName", EntityOperator.EQUALS, activeApp));

						 GenericValue ofbizComponentAccessQuery = EntityQuery.use(delegator).select("componentId").from("OfbizComponentAccess")
								 .where(ofbizComponentAccessCond)
								 .orderBy("seqId").queryFirst();
						 if (UtilValidate.isNotEmpty(ofbizComponentAccessQuery)) {
							 String componentId = ofbizComponentAccessQuery.getString("componentId");
							 if (UtilValidate.isNotEmpty(componentId)) {

								 List<EntityCondition> ofbizPageSecurityCond = UtilMisc.<EntityCondition>toList(
										 EntityCondition.makeCondition("pageType", EntityOperator.EQUALS, "TAB"),
										 EntityCondition.makeCondition("componentId", EntityOperator.EQUALS, componentId));

								 if(!fullAdmin) {
									 ofbizPageSecurityCond.add(permissionIdCond);
								 }

								 EntityQuery ofbizPageSecurityQuery = EntityQuery.use(delegator).select("tabId").from("OfbizPageSecurity")
										 .where(ofbizPageSecurityCond).orderBy("seqId");

								 List<String> tabIdList = EntityUtil.getFieldListFromEntityList(ofbizPageSecurityQuery.queryList(), "tabId", true);
								 if (UtilValidate.isNotEmpty(tabIdList)) {
									 for (String tabId: tabIdList) {
										 Map < String, Object> menuMap = new HashMap < String, Object> ();

										 List<EntityCondition> ofbizPageSecurityCond1 = UtilMisc.<EntityCondition>toList(
												 EntityCondition.makeCondition("pageType", EntityOperator.EQUALS, "TAB"),
												 EntityCondition.makeCondition("tabId", EntityOperator.EQUALS, tabId),
												 EntityCondition.makeCondition("componentId", EntityOperator.EQUALS, componentId));

										 if(!fullAdmin) {
											 ofbizPageSecurityCond1.add(permissionIdCond);
										 }

										 GenericValue ofbizPageSecurityGV = EntityQuery.use(delegator).select("tabId", "permissionId", "uiLabels", "pageId")
												 .from("OfbizPageSecurity")
												 .where(ofbizPageSecurityCond1)
												 .queryFirst();

										 if (UtilValidate.isNotEmpty(ofbizPageSecurityGV)) {

											 List<EntityCondition> ofbizTabSecurityShortcutCond = UtilMisc.<EntityCondition>toList(
													 EntityCondition.makeCondition("pageType", EntityOperator.EQUALS, "SHORTCUT"),
													 EntityCondition.makeCondition("tabId", EntityOperator.EQUALS, tabId),
													 EntityCondition.makeCondition("componentId", EntityOperator.EQUALS, componentId),
													 EntityCondition.makeCondition("isDisabled", EntityOperator.EQUALS, "N"));

											 if(!fullAdmin) {
												 ofbizTabSecurityShortcutCond.add(permissionIdCond);
											 }

											 List<GenericValue> ofbizTabSecurityShortcutGV = EntityQuery.use(delegator)
													 .select("shortcutId", "permissionId", "uiLabels", "requestUri", "pageId", "parentShortcutId")
													 .from("OfbizTabSecurityShortcut")
													 .where(ofbizTabSecurityShortcutCond)
													 .orderBy("seqId").queryList();
											 if (UtilValidate.isNotEmpty(ofbizTabSecurityShortcutGV)) {

												 menuMap.put("tab", ofbizPageSecurityGV);
												 menuMap.put("shortcut", ofbizTabSecurityShortcutGV);

												 for (GenericValue shortcut : ofbizTabSecurityShortcutGV) {

													 List<EntityCondition> ofbizTabSecurityShortcutCond1 = UtilMisc.<EntityCondition>toList(
															 EntityCondition.makeCondition("pageType", EntityOperator.EQUALS, "SHORTCUT"),
															 EntityCondition.makeCondition("tabId", EntityOperator.EQUALS, tabId),
															 EntityCondition.makeCondition("componentId", EntityOperator.EQUALS, componentId),
															 EntityCondition.makeCondition("parentShortcutId", EntityOperator.EQUALS, shortcut.getString("shortcutId")),
															 EntityCondition.makeCondition("isDisabled", EntityOperator.EQUALS, "N")
															 );

													 if(!fullAdmin) {
														 ofbizTabSecurityShortcutCond1.add(permissionIdCond);
													 }

													 List<GenericValue> shortCutList = EntityQuery.use(delegator)
															 .select("shortcutId", "permissionId", "uiLabels", "requestUri", "pageId", "parentShortcutId")
															 .from("OfbizTabSecurityShortcut")
															 .where(ofbizTabSecurityShortcutCond1)
															 .orderBy("seqId").queryList();
													 if (UtilValidate.isNotEmpty(shortCutList)) {
														 menuMap.put("shortcut#"+shortcut.getString("shortcutId"), shortCutList);
													 }
												 }

												 menuList.add(menuMap);
											 }
										 }
									 }
								 }

								 //get tab and shortcut
								 if(UtilValidate.isNotEmpty(sectionName)) {
									 GenericValue ofbizTabSecurityShortcut = EntityQuery.use(delegator).select("tabId", "parentShortcutId").from("OfbizTabSecurityShortcut")
											 .where("pageId", sectionName, "componentId", componentId, "pageType", "SHORTCUT")
											 .queryFirst();
									 if(UtilValidate.isNotEmpty(ofbizTabSecurityShortcut)) {
										 result.put("tabId", ofbizTabSecurityShortcut.getString("tabId"));
										 if(UtilValidate.isNotEmpty(ofbizTabSecurityShortcut.getString("parentShortcutId"))) {
											 result.put("shortcutId", ofbizTabSecurityShortcut.getString("parentShortcutId"));
										 }
									 }
								 }
							 }
						 }
					 }

				 } catch (Exception e) {
					 Debug.logError("Exception in get menus " + e.getMessage(), module);
				 }
			 }
		 }
		 result.put("menuList", menuList);
		 return result;
	 }
	 
	 @SuppressWarnings("unchecked")
	public static Map<String, Object> getComponents(DispatchContext dctx, Map < String, Object> context) {
		 Map<String, Object> result = new HashMap<String, Object>();
		 Delegator delegator = (Delegator) dctx.getDelegator();
		 GenericValue userLogin = (GenericValue) context.get("userLogin");
		 String activeApp = (String) context.get("activeApp");
		 String sectionName = (String) context.get("sectionName");
		 HttpServletRequest request = (HttpServletRequest) context.get("request");
		 HttpSession session = (HttpSession) context.get("session");
		 try {
			 String userLoginId = userLogin.getString("userLoginId");
			 String partyId = userLogin.getString("partyId");
			 EntityCondition condition = EntityCondition.makeCondition(EntityOperator.OR,
											 EntityCondition.makeCondition("isHide",EntityOperator.EQUALS,"N"),
											 EntityCondition.makeCondition("isHide",EntityOperator.EQUALS,null));
			 
			 List<GenericValue> components = EntityQuery.use(delegator).from("OfbizComponentAccess")
					 .where(condition)
					 .orderBy("seqId").queryList();
			 List<String> groupIds1 = (List<String>) session.getAttribute("userLoginSecurityGroups");
			 List<String> groupIds = UtilValidate.isNotEmpty(session.getAttribute("userLoginSecurityGroups")) ? (List<String>) session.getAttribute("userLoginSecurityGroups") : UtilValidate.isNotEmpty(request.getAttribute("userLoginSecurityGroups")) ? (List<String>) request.getAttribute("userLoginSecurityGroups") : new LinkedList<>();
			 if(UtilValidate.isEmpty(groupIds)) {
				 String userLoginPartyId = userLogin.getString("partyId");
				 Map<String, Object> userData =  DataHelper.getUserRoleGroup(delegator, userLoginPartyId);
				 groupIds = UtilValidate.isNotEmpty(userData) ? (List<String>) userData.get("userLoginSecurityGroups") : new LinkedList<>();
				 List<String> userLoginRoles = UtilValidate.isNotEmpty(userData) ? (List<String>) userData.get("userLoginRoles") : new LinkedList<>();
				 session.setAttribute("userLoginSecurityGroups", groupIds);
				 request.setAttribute("userLoginSecurityGroups", groupIds);
				 session.setAttribute("userLoginRoles", userLoginRoles);
				 request.setAttribute("userLoginRoles", userLoginRoles);
			 }
			 System.out.println("groupIds--->"+groupIds);
			 List<Map<String, Object>> webApps = new LinkedList<Map<String,Object>>();
			 boolean hasFullAccess = DataUtil.hasFullPermission(delegator, userLoginId);
			 if(UtilValidate.isNotEmpty(components)) {
				 for(GenericValue component : components) {
					 Map<String, Object> menuData = new HashMap<String, Object>();
					 Map<String, Object> webApp = new HashMap<String, Object>();
					 String permissionId = component.getString("permissionId");
					 String componentId = component.getString("componentId");
					 boolean hasPermission = true;
					 if(!hasFullAccess)
						 hasPermission = DataUtil.validateSecurityPermission(delegator, groupIds, permissionId);
					 if(hasPermission) {
						 webApp.put("componentName", component.get("componentName"));
						 webApp.put("uiLabels", component.get("uiLabels"));
						 webApp.put("requestURI", component.get("requestUri"));
						 webApp.put("imageURL", component.get("imageUrl"));
						 menuData.put("webApp", webApp);
						 EntityCondition tabCondition = EntityCondition.makeCondition(EntityOperator.AND,
								 EntityCondition.makeCondition("componentId",EntityOperator.EQUALS,componentId),
								 EntityCondition.makeCondition(EntityOperator.OR,
										 EntityCondition.makeCondition("isDisabled",EntityOperator.EQUALS,"N"),
										 EntityCondition.makeCondition("isDisabled",EntityOperator.EQUALS,null)
										 ));
						 
						 List<GenericValue> tabs = EntityQuery.use(delegator).from("OfbizPageSecurity").where(tabCondition).orderBy("seqId").queryList();
						 if(UtilValidate.isNotEmpty(tabs)) {
							 List<Map<String, Object>> tabShortcutList = new LinkedList<Map<String,Object>>();
							 for(GenericValue tab : tabs) {
								 Map<String, Object> webAppTab = new HashMap<String, Object>();
								 Map<String, Object> webAppTabMenu = new HashMap<String, Object>();
								 permissionId = tab.getString("permissionId");
								 String tabId = tab.getString("tabId");
								 if(!hasFullAccess)
									 hasPermission = DataUtil.validateSecurityPermission(delegator, groupIds, permissionId);
								 if(hasPermission) {
									 webAppTab.put("requestURI", tab.get("requestUri"));
									 webAppTab.put("uiLabels", tab.get("uiLabels"));
									 webAppTab.put("favIcon", tab.get("favIcon"));
									 webAppTabMenu.put("webAppTab", webAppTab);
									 EntityCondition shortcutCondition = EntityCondition.makeCondition(EntityOperator.AND,
											 EntityCondition.makeCondition("tabId",EntityOperator.EQUALS,tabId),
											 EntityCondition.makeCondition("pageType",EntityOperator.EQUALS,"SHORTCUT"),
											 EntityCondition.makeCondition("componentId",EntityOperator.EQUALS,componentId),
											 EntityCondition.makeCondition(EntityOperator.OR,
													 EntityCondition.makeCondition("isDisabled",EntityOperator.EQUALS,"N"),
													 EntityCondition.makeCondition("isDisabled",EntityOperator.EQUALS,null)
													 ));
									 
									 List<GenericValue> shortcuts = EntityQuery.use(delegator).from("OfbizTabSecurityShortcut").where(shortcutCondition).orderBy("seqId").queryList();
									 
									 if(UtilValidate.isNotEmpty(shortcuts)) {
										 List<Map<String, Object>> shortcutList = new LinkedList<Map<String,Object>>();
										 for(GenericValue shortcut : shortcuts) {
											 Map<String, Object> webAppShortcut = new HashMap<String, Object>();
											
											 permissionId = shortcut.getString("permissionId");
											 String pageType = shortcut.getString("pageType");
											 String shortcutId=shortcut.getString("shortcutId");
											 if(!hasFullAccess)
												 hasPermission = DataUtil.validateSecurityPermission(delegator, groupIds, permissionId);
											 if(hasPermission) {
												 webAppShortcut.put("requestURI", shortcut.get("requestUri"));
												 webAppShortcut.put("uiLabels", shortcut.get("uiLabels"));
												 webAppShortcut.put("pageId", shortcut.get("pageId"));
												 webAppShortcut.put("favIcon", shortcut.get("favIcon"));
												 webAppShortcut.put("pageType", shortcut.get("pageType"));
												 EntityCondition SubshortcutCondition = EntityCondition.makeCondition(EntityOperator.AND,
														 EntityCondition.makeCondition("tabId",EntityOperator.EQUALS,tabId),
														 EntityCondition.makeCondition("pageType",EntityOperator.EQUALS,"SUB-SHORTCUT"),
														 EntityCondition.makeCondition("parentShortcutId",EntityOperator.EQUALS,shortcutId),
														 EntityCondition.makeCondition("componentId",EntityOperator.EQUALS,componentId),
														 EntityCondition.makeCondition(EntityOperator.OR,
																 EntityCondition.makeCondition("isDisabled",EntityOperator.EQUALS,"N"),
																 EntityCondition.makeCondition("isDisabled",EntityOperator.EQUALS,null)
																 ));
												 
												 List<GenericValue> Subshortcuts = EntityQuery.use(delegator).from("OfbizTabSecurityShortcut").where(SubshortcutCondition).orderBy("seqId").queryList();
												 List<Map<String, Object>> subshortcutList = new LinkedList<Map<String,Object>>();
												 if (UtilValidate.isNotEmpty(Subshortcuts)) {
													 for(GenericValue subshortcut : Subshortcuts) {
														 Map<String , Object> webAppShortcutMenu = new HashMap<String, Object>();
														 permissionId = subshortcut.getString("permissionId");
														  pageType = subshortcut.getString("pageType");
														  shortcutId=subshortcut.getString("shortcutId");
														 if(!hasFullAccess)
															 hasPermission = DataUtil.validateSecurityPermission(delegator, groupIds, permissionId);
														 if (hasPermission) {
															 webAppShortcutMenu.put("requestURI", subshortcut.get("requestUri"));
															 webAppShortcutMenu.put("uiLabels", subshortcut.get("uiLabels"));
															 webAppShortcutMenu.put("pageId", subshortcut.get("pageId"));
															 webAppShortcutMenu.put("favIcon", subshortcut.get("favIcon"));
															 webAppShortcutMenu.put("pageType", subshortcut.get("pageType"));
														 }
														 subshortcutList.add(webAppShortcutMenu);
													 }
													 webAppShortcut.put("webAppSubShortCutList", subshortcutList);
											 }
												
												 
												 shortcutList.add(webAppShortcut);
										 }		
										 
										 webAppTabMenu.put("webAppShortCutList", shortcutList);
										
									 }
								 }
									 tabShortcutList.add(webAppTabMenu);
							 }
							 menuData.put("tabs", tabShortcutList);
						 }
						 webApps.add(menuData);
					 }
				 }
			 }
			 }
			 result.put("webApps", webApps);
			 session.setAttribute("webAppMenus", result);
			 request.setAttribute("webAppMenus", result);
			 System.out.println("webApps --->result--->"+result);
		 } catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	 }
	 
	 public static boolean validateFullAccessUser(Delegator delegator, GenericValue userLogin) {
	    	boolean hasFullAccess = false;
	    	try {
	    		List<GenericValue> systemProperty = EntityQuery.use(delegator).from("SystemProperty").where("systemResourceId", "component-access", "systemPropertyValue", "FULL_ACCESS").queryList();
	    		if(systemProperty != null && systemProperty.size() > 0) {
	    			List<GenericValue> userLoginSecurityGroup = EntityQuery.use(delegator).from("UserLoginSecurityGroup")
	    					.where(EntityCondition.makeCondition("userLoginId", EntityOperator.EQUALS, userLogin.getString("userLoginId")),
	    							EntityCondition.makeCondition("groupId", EntityOperator.IN, EntityUtil.getFieldListFromEntityList(systemProperty, "systemPropertyId", true))
	    							)
	    					.filterByDate().queryList();
	    			if(userLoginSecurityGroup != null && userLoginSecurityGroup.size() > 0) {
	    				hasFullAccess = true;
	    			}
	    		}
	    	} catch(Exception e) {
	    		e.printStackTrace();
	    	}

	    	return hasFullAccess;
	    }
     
}
