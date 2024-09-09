package org.groupfio.common.portal.content;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.Locale;
import java.util.Map;
import java.io.InputStream;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.fio.homeapps.util.UtilMessage;
import org.ofbiz.base.util.Base64;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.content.data.DataResourceWorker;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.security.Security;

/**
 * Content servlet methods.
 *
 * @author Leon Torres (leon@opensourcestrategies.com)
 */
public final class ContentEvents {

    private ContentEvents() {}

    private static final String MODULE = ContentEvents.class.getName();
    /**
     * Download content for a party.
     * @param request a <code>HttpServletRequest</code> value
     * @param response a <code>HttpServletResponse</code> value
     * @return the <code>String</code> content value.
     */
    public static String downloadPartyContent(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        Security security = (Security) request.getAttribute("security");
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        Locale locale = UtilHttp.getLocale(request);
        String contentId = request.getParameter("contentId");
        String partyId = request.getParameter("partyId");

        try {
            GenericValue dataResource = getDataResource(request);
            if (dataResource == null) {
                return UtilMessage.createAndLogEventError(request, "CrmErrorContentNotFound", UtilMisc.toMap("contentId", contentId), locale, MODULE);
            }

            // get the module corresponding to the internal party
            /*   String module = Security.getSecurityModuleOfInternalParty(partyId, delegator);
            if (module == null) {
                return UtilMessage.createAndLogEventError(request, "CrmErrorPermissionDenied", locale, MODULE);
            }
*/
            // ensure association exists between our party and content (ignore role because we're using module to check for security)
            /*  List<EntityCondition> conditions = UtilMisc.<EntityCondition>toList(
                      EntityCondition.makeCondition("contentId", contentId),
                      EntityCondition.makeCondition("partyId", partyId),
                      EntityUtil.getFilterByDateExpr());*/
            //GenericValue association = EntityUtil.getFirst(delegator.findByAnd("ContentRole", conditions,null,false));
            GenericValue association = EntityUtil.getFirst(delegator.findByAnd("ContentRole", UtilMisc.toMap("contentId", contentId, "partyId", partyId), UtilMisc.toList("-createdStamp"), false));
            if (association == null) {
                return UtilMessage.createAndLogEventError(request, "CrmErrorPermissionDenied", locale, MODULE);
            }

            // check if userLogin has permission to view
            /* if (!Security.hasPartyRelationSecurity(security, module, "_VIEW", userLogin, partyId)) {
                 return UtilMessage.createAndLogEventError(request, "CrmErrorPermissionDenied", locale, MODULE);
             }*/

            return serveDataResource(request, response, dataResource);
        } catch (GenericEntityException e) {
            return UtilMessage.createAndLogEventError(request, e, locale, MODULE);
        }
    }


    /** Get the contentId and verify that we have something to download */
    private static GenericValue getDataResource(HttpServletRequest request) throws GenericEntityException {
        Delegator delegator = (Delegator) request.getAttribute("delegator");

        //GenericValue content = delegator.findByPrimaryKey("Content", UtilMisc.toMap("contentId", request.getParameter("contentId")));
        GenericValue content = delegator.findOne("Content", UtilMisc.toMap("contentId", request.getParameter("contentId")), false);

        if (content == null) {
            return null;
        }

        GenericValue dataResource = content.getRelatedOne("DataResource");
        if (dataResource == null || (!"LOCAL_FILE".equals(dataResource.get("dataResourceTypeId")) && !"ELECTRONIC_TEXT".equals(dataResource.get("dataResourceTypeId")))) {
            return null;
        }
        return dataResource;
    }

    /** Find the file and write it to the client stream. */
    private static String serveDataResource(HttpServletRequest request, HttpServletResponse response, GenericValue dataResource) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        HttpSession session = request.getSession();
        Locale locale = UtilHttp.getLocale(request);
        ServletContext application = session.getServletContext();
        Map < String, Object > input = UtilMisc. < String, Object > toMap("contentId", request.getParameter("contentId"));
        try {
            String fileLocation = dataResource.getString("objectInfo");
            String fileName = dataResource.getString("dataResourceName");

            // the file name needs to be UTF8 urlencoded for the content disposition HTTP header
            fileName = "=?UTF-8?B?" + new String(Base64.base64Encode(fileName.getBytes("UTF-8")), "UTF-8") + "?=";

            if (UtilValidate.isEmpty(fileLocation)) {
                return UtilMessage.createAndLogEventError(request, "CrmErrorContentNotFound", input, locale, MODULE);
            }

            // test if the file exists here, due to strange bugs with DataResourceWorker.streamDataResource
            File file = new File(fileLocation);
            if (!file.exists()) {
                return UtilMessage.createAndLogEventError(request, "CrmErrorContentNotFound", input, locale, MODULE);
            }

            // Set the headers so that the browser treats content as a download (this could be changed to use the mimeTypeId of the content for in-browser display)
            response.setContentType("application/x-download");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

            // write the file to the client browser
            OutputStream os = response.getOutputStream();
            streamDataResource(os, delegator, dataResource.getString("dataResourceId"), "", application.getInitParameter("webSiteId"), UtilHttp.getLocale(request), application.getRealPath("/"));
            os.flush();
            return "success";
        } catch (GeneralException e) {
            return UtilMessage.createAndLogEventError(request, e, locale, MODULE);
        } catch (IOException e) {
            return UtilMessage.createAndLogEventError(request, e, locale, MODULE);
        }
    }

    // TODO: remove this method in favor of getDataResourceStream
    public static void streamDataResource(OutputStream os, Delegator delegator, String dataResourceId, String https, String webSiteId, Locale locale, String rootDir) throws IOException, GeneralException {
        try {
            //GenericValue dataResource = delegator.findByPrimaryKeyCache("DataResource", UtilMisc.toMap("dataResourceId", dataResourceId));
            GenericValue dataResource = delegator.findOne("DataResource", UtilMisc.toMap("dataResourceId", dataResourceId), false);

            if (dataResource == null) {
                throw new GeneralException("Error in streamDataResource: DataResource with ID [" + dataResourceId + "] was not found.");
            }
            String dataResourceTypeId = dataResource.getString("dataResourceTypeId");
            if (UtilValidate.isEmpty(dataResourceTypeId)) {
                dataResourceTypeId = "SHORT_TEXT";
            }
            String mimeTypeId = dataResource.getString("mimeTypeId");
            if (UtilValidate.isEmpty(mimeTypeId)) {
                mimeTypeId = "text/html";
            }

            if (dataResourceTypeId.equals("SHORT_TEXT")) {
                String text = dataResource.getString("objectInfo");
                os.write(text.getBytes());
            } else if (dataResourceTypeId.equals("ELECTRONIC_TEXT")) {
                //GenericValue electronicText = delegator.findByPrimaryKeyCache("ElectronicText", UtilMisc.toMap("dataResourceId", dataResourceId));
                GenericValue electronicText = delegator.findOne("ElectronicText", UtilMisc.toMap("dataResourceId", dataResourceId), false);

                if (electronicText != null) {
                    String text = electronicText.getString("textData");
                    if (text != null) os.write(text.getBytes());
                }
            } else if (dataResourceTypeId.equals("IMAGE_OBJECT")) {
                byte[] imageBytes = DataResourceWorker.acquireImage(delegator, dataResource);
                if (imageBytes != null) os.write(imageBytes);
            } else if (dataResourceTypeId.equals("LINK")) {
                String text = dataResource.getString("objectInfo");
                os.write(text.getBytes());
            } else if (dataResourceTypeId.equals("URL_RESOURCE")) {
                URL url = new URL(dataResource.getString("objectInfo"));
                if (url.getHost() == null) { // is relative
                    String prefix = DataResourceWorker.buildRequestPrefix(delegator, locale, webSiteId, https);
                    String sep = "";
                    //String s = "";
                    if (url.toString().indexOf("/") != 0 && prefix.lastIndexOf("/") != (prefix.length() - 1)) {
                        sep = "/";
                    }
                    String s2 = prefix + sep + url.toString();
                    url = new URL(s2);
                }
                InputStream in = url.openStream();
                int c;
                while ((c = in .read()) != -1) {
                    os.write(c);
                }
            } else if (dataResourceTypeId.indexOf("_FILE") >= 0) {
                String objectInfo = dataResource.getString("objectInfo");
                File inputFile = DataResourceWorker.getContentFile(dataResourceTypeId, objectInfo, rootDir);
                //long fileSize = inputFile.length();
                FileInputStream fis = new FileInputStream(inputFile);
                int c;
                while ((c = fis.read()) != -1) {
                    os.write(c);
                }
            } else {
                throw new GeneralException("The dataResourceTypeId [" + dataResourceTypeId + "] is not supported in streamDataResource");
            }
        } catch (GenericEntityException e) {
            throw new GeneralException("Error in streamDataResource", e);
        }
    }

}