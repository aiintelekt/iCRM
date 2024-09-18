/*
 * Copyright (c) Open Source Strategies, Inc.
 *
 * Opentaps is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Opentaps is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Opentaps.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.fio.crm.content;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.io.InputStream;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.fio.crm.util.UtilMessage;
import org.ofbiz.base.util.Base64;
import org.ofbiz.base.util.Debug;
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
           /* GenericValue association = EntityUtil.getFirst(delegator.findByAnd("ContentRole", UtilMisc.toMap("contentId", contentId, "partyId", partyId), UtilMisc.toList("-createdStamp"), false));
            if (association == null) {
                return UtilMessage.createAndLogEventError(request, "CrmErrorPermissionDenied", locale, MODULE);
            }*/

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
        String contentId = request.getParameter("contentId");
        GenericValue dataResource = null;
        if(UtilValidate.isNotEmpty(contentId)) {
            if(org.fio.crm.util.DataUtil.isValidContentIdOrDataResourceId(contentId)) {
            	GenericValue content = delegator.findOne("Content", UtilMisc.toMap("contentId", contentId), false);
                String dataResourceId	= content.getString("dataResourceId");

                if (content == null) {
                    return null;
                }

               // GenericValue dataResource = content.getRelatedOne("DataResource");
                if(UtilValidate.isNotEmpty(dataResourceId) && org.fio.crm.util.DataUtil.isValidContentIdOrDataResourceId(dataResourceId)) {
                	 dataResource = delegator.findOne("DataResource", UtilMisc.toMap("dataResourceId", dataResourceId), false);
                     if (dataResource == null || (!"LOCAL_FILE".equals(dataResource.get("dataResourceTypeId")) && !"ELECTRONIC_TEXT".equals(dataResource.get("dataResourceTypeId")))) {
                         return null;
                     }
                }
            }
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
        	if(UtilValidate.isNotEmpty(dataResource) && dataResource !=null) {
        		String fileLocation = dataResource.getString("objectInfo");
                String fileName = dataResource.getString("dataResourceName");
                String mimeTypeId = dataResource.getString("mimeTypeId");
                // the file name needs to be UTF8 urlencoded for the content disposition HTTP header
                fileName = "=?UTF-8?B?" + new String(Base64.base64Encode(fileName.getBytes("UTF-8")), "UTF-8") + "?=";

                if (UtilValidate.isEmpty(fileLocation)) {
                    return UtilMessage.createAndLogEventError(request, "CrmErrorContentNotFound", input, locale, MODULE);
                }

                // test if the file exists here, due to strange bugs with DataResourceWorker.streamDataResource
                String rootPath = fileLocation;
                File file = new File(fileLocation);
                if (file.getCanonicalPath().startsWith(new File(rootPath).getCanonicalPath())) {
                	if (!file.exists()) {
                        return UtilMessage.createAndLogEventError(request, "CrmErrorContentNotFound", input, locale, MODULE);
                    }
                }
                
                // Set the headers so that the browser treats content as a download (this could be changed to use the mimeTypeId of the content for in-browser display)
                response.setContentType(UtilValidate.isNotEmpty(mimeTypeId) ? mimeTypeId : "application/x-download");
                response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

                // write the file to the client browser
                OutputStream os = response.getOutputStream();
                streamDataResource(os, delegator, dataResource.getString("dataResourceId"), "", application.getInitParameter("webSiteId"), UtilHttp.getLocale(request), application.getRealPath("/"));
                os.flush();
                return "success";
        	}else {
        		return "error";
        	}
        } catch (GeneralException e) {
            return UtilMessage.createAndLogEventError(request, e, locale, MODULE);
        } catch (IOException e) {
            return UtilMessage.createAndLogEventError(request, e, locale, MODULE);
        }
    }

    // TODO: remove this method in favor of getDataResourceStream
    public static void streamDataResource(OutputStream os, Delegator delegator, String dataResourceId, String https, String webSiteId, Locale locale, String rootDir) throws IOException, GeneralException {
    	Set<String> allowedProtocols = new HashSet<>();
    		allowedProtocols.add("http");
    		allowedProtocols.add("https");
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
            	String urlString = dataResource.getString("objectInfo");
            	urlString = urlString.replaceAll(" ", "%20");
            	
            	if(UtilValidate.isNotEmpty(urlString)) {
            	    if (org.groupfio.common.portal.util.DataUtil.isValidUrl(urlString)) {
            	        URL url = new URL(urlString);

            	        // Validate the protocol is either HTTP or HTTPS
            	        String protocol = url.getProtocol();
            	        if (url != null && ("http".equals(protocol) || "https".equals(protocol))) {
            	            // Resolve the IP address and validate it is not internal
            	            InetAddress address = InetAddress.getByName(url.getHost());
            	            if (!address.isSiteLocalAddress() && !address.isLoopbackAddress() && !address.isAnyLocalAddress()) {
            	                if (url.getHost() == null) { // is relative
            	                    String prefix = DataResourceWorker.buildRequestPrefix(delegator, locale, webSiteId, https);
            	                    String sep = "";
            	                    if (url.toString().indexOf("/") != 0 && prefix.lastIndexOf("/") != (prefix.length() - 1)) {
            	                        sep = "/";
            	                    }
            	                    String s2 = prefix + sep + url.toString();
            	                    url = new URL(s2);
            	                }
            	                
            	                // Perform the request with additional security checks
        	                	try {
									InputStream is = url.openStream();
									int c;
									while ((c = is.read()) != -1) {
									    os.write(c);
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
            	            } else {
            	                // Handle the case where the address is internal, local, or invalid
            	                throw new SecurityException("Attempted SSRF with internal or local address: " + url);
            	            }
            	        } else {
            	            // Handle invalid or unsupported protocols
            	            throw new MalformedURLException("Unsupported URL protocol: " + protocol);
            	        }
            	    }
            	}
            } else if (dataResourceTypeId.indexOf("_FILE") >= 0) {
                String objectInfo = dataResource.getString("objectInfo");
                File inputFile = DataResourceWorker.getContentFile(dataResourceTypeId, objectInfo, rootDir);
                String rootPath = inputFile.getPath();
                //long fileSize = inputFile.length();
                if (inputFile.getCanonicalPath().startsWith(new File(rootPath).getCanonicalPath())) {
                	FileInputStream fis = new FileInputStream(inputFile);
                    int c;
                    while ((c = fis.read()) != -1) {
                        os.write(c);
                    }
                }
            } else {
                throw new GeneralException("The dataResourceTypeId [" + dataResourceTypeId + "] is not supported in streamDataResource");
            }
        } catch (GenericEntityException e) {
            throw new GeneralException("Error in streamDataResource", e);
        }
    }

}