import java.awt.im.InputContext

appBarId= request.getParameter("appBarId");
appBarTypeId= request.getParameter("appBarTypeId");
inputContext = new LinkedHashMap<String, Object>();
inputContext.put("appBarId", appBarId);
inputContext.put("appBarTypeId", appBarTypeId);

context.put("inputContext", inputContext);