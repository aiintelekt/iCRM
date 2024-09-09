package org.groupfio.common.portal.reporting.jasper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

//import org.codehaus.groovy.runtime.InvokerHelper;
import org.groupfio.common.portal.reporting.UtilReports;
import org.groupfio.common.portal.reporting.UtilReports.ContentType;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.datasource.GenericHelperInfo;
import org.ofbiz.entity.transaction.TransactionFactoryLoader;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.webapp.control.ContextFilter;
import org.ofbiz.webapp.view.AbstractViewHandler;
import org.ofbiz.webapp.view.ViewHandlerException;

import javolution.util.FastMap;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JExcelApiExporter;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRPdfExporterParameter;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.export.JRTextExporter;
import net.sf.jasperreports.engine.export.JRTextExporterParameter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXmlExporter;
import net.sf.jasperreports.engine.export.oasis.JROdtExporter;

/**
 * Class renders Jasper Reports of any supported content type.
 */
public class JasperReportsViewHandler extends AbstractViewHandler {

    protected ServletContext context;
    public static final String module = JasperReportsViewHandler.class.getName();

    private static final String HEADER_CONTENT_DISPOSITION = "Content-Disposition";

    /* (non-Javadoc)
     * @see org.ofbiz.webapp.view.ViewHandler#init(javax.servlet.ServletContext)
     */
    public void init(ServletContext context) {
        this.context = context;
    }
    
    @SuppressWarnings("unchecked")
    public void render(String name, String page, String info, String contentType, String encoding, HttpServletRequest request, HttpServletResponse response) throws ViewHandlerException {
        Connection conn = null;
        HttpSession session = request.getSession(true);
        Debug.logInfo("name: "+name+"; page: "+ page+"; info: "+ info+"; contentType: "+ contentType+"; encoding: "+  encoding+";  request: "+ UtilMisc.printMap(request.getParameterMap())+"; response: "+ response.toString(), module);
        
        // some containers call filters on EVERY request, even forwarded ones,
        // so let it know that it came from the control servlet
        if (request == null) {
            throw new ViewHandlerException("The HttpServletRequest object was null, how did that happen?");
        }
        if (UtilValidate.isEmpty(page)) {
            throw new ViewHandlerException("View page was null or empty, but must be specified");
        }
        if (UtilValidate.isEmpty(info) && Debug.infoOn()) {
            Debug.logInfo("View info string was null or empty, (optionally used to specify an Entity that is mapped to the Entity Engine datasource that the report will use).", module);
        }

        // tell the ContextFilter we are forwarding
        request.setAttribute(ContextFilter.FORWARDED_FROM_SERVLET, Boolean.TRUE);
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        if (delegator == null) {
            throw new ViewHandlerException("The delegator object was null, how did that happen?");
        }

        try {
            // Collects parameters/properties
            Map<String, Object> parameters = (Map<String, Object>) request.getAttribute("jrParameters");
            if (UtilValidate.isEmpty(parameters)) {
                parameters = UtilHttp.getParameterMap(request);
            }

            if (!parameters.containsKey("SUBREPORT_DIR")) {
                parameters.put("SUBREPORT_DIR", context.getRealPath("/"));
            }
            Locale locale = UtilHttp.getLocale(request);
            parameters.put("REPORT_LOCALE", locale);

            String location = null;
            String reportId = (String) parameters.get("reportId");
            if (UtilValidate.isNotEmpty(reportId)) {
                GenericValue reportRegistry = EntityUtil.getFirst(delegator.findByAnd("ReportRegistry", UtilMisc.toMap("reportId", reportId), null, true));
                if (UtilValidate.isNotEmpty(reportRegistry)) {
                    location = reportRegistry.getString("reportLocation");
                }
            }

            if (UtilValidate.isEmpty(location)) {
                location = page;
            }

            // Trying to get report object from the given location.
            JasperReport report = UtilReports.getReportObject(location);
            if (report == null) {
                throw new ViewHandlerException("Fatal error. Report object can not be created for some unknown reason.");
            }

            // Provide access to uiLabelMap if resource bundle isn't defined. 
            if (UtilValidate.isEmpty(report.getResourceBundle())) {
                JRResourceBundle resources = new JRResourceBundle(locale);
                if (resources.size() > 0) {
                    parameters.put("REPORT_RESOURCE_BUNDLE", resources);
                }
            }

            // Identify what output user want to get. It's depend on requested MIME type that
            // can be passed in parameter 'reportType' or attribute content-type of the view-map tag in
            // controller file. If absent both, HTML by default.
            String myContentType = request.getParameter("reportType");
            if (UtilReports.getContentType(myContentType) == null) {
                myContentType = contentType;
                if (UtilValidate.isEmpty(myContentType)) {
                    myContentType = ContentType.HTML.toString();
                }
            }
            response.setContentType(myContentType);

            // If report is exporting to XLS or CSV format then disable pagination.
            // Also supply parameter isPlainFormat that can be used report designers in the case to do special things.
            if (ContentType.XLS.toString().equals(myContentType) || ContentType.CSV.toString().equals(myContentType)) {
                parameters.put("IS_IGNORE_PAGINATION", Boolean.TRUE);
                parameters.put("isPlainFormat", Boolean.TRUE);
            } else {
                parameters.put("isPlainFormat", Boolean.FALSE);
            }

            // Try to find data source for report
            JRDataSource jrDataSource = (JRDataSource) request.getAttribute("jrDataSource");
            JasperPrint jp = null;
            if (jrDataSource == null) {
                String datasourceName = delegator.getEntityHelperName(info);
                String jndiDataSourceName = (String) parameters.get("jndiDS");

                if (UtilValidate.isNotEmpty(datasourceName)) {
                    Debug.logInfo("Filling report with connection from datasource: " + datasourceName, module);
                    jp = JasperFillManager.fillReport(report, parameters, TransactionFactoryLoader.getInstance().getConnection(new GenericHelperInfo(null, datasourceName)));
                } else {
                    Debug.logInfo("Filling report with an empty JR datasource", module);
                    jp = JasperFillManager.fillReport(report, parameters, new JREmptyDataSource());
                }
                
            } else {
                // custom data source object
                jp = JasperFillManager.fillReport(report, parameters, jrDataSource);
            }

            if (jp.getPages().size() < 1) {
                Debug.logError("Report is empty.", module);
            } else {
                Debug.logInfo("Got report, there are " + jp.getPages().size() + " pages.", module);
            }

            // Generates and exports report
            ContentType content = UtilReports.getContentType(myContentType);
            generate(request.getParameter("reportName"), jp, content, request, response);

        } catch (java.sql.SQLException e) {
            Debug.logError(e.getMessage(), module);
            throw new ViewHandlerException("SQL exception is occurred <" + e.getMessage() + ">", e);
        } catch (JRException e) {
            Debug.logError("Can't generate Jasper report. Error: " + e.getMessage(), module);
            throw new ViewHandlerException("Unexpected JasperReports exception <" + e.getMessage() + ">", e);
        } catch (GenericEntityException e) {
            Debug.logError(e.getMessage(), module);
            throw new ViewHandlerException(e);
        } catch (MalformedURLException e) {
            Debug.logError(e.getMessage(), module);
            throw new ViewHandlerException(e);
        } catch (FileNotFoundException e) {
            Debug.logError(e.getMessage(), module);
            throw new ViewHandlerException(e);
        } catch (IOException e) {
            Debug.logError(e.getMessage(), module);
            throw new ViewHandlerException(e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            conn = null;
        }
    }

    /**
     * Method will stream out the requested report in various report types (pdf, word, excel, text, xml).
     * 
     * @param name report friendly name
     * @param jasperPrint compiled report object
     * @param contentType MIME type, a <code>ContentType</code> value
     * @param request HttpServletResponce object
     * @param response HttpServletResponse object
     * @throws IOException
     * @throws JRException
     */
    private void generate(String name, JasperPrint jasperPrint, ContentType contentType, HttpServletRequest request, HttpServletResponse response) throws IOException, JRException {
        ServletOutputStream os = response.getOutputStream();
        Map<JRExporterParameter, Object> exporterParameters = FastMap.newInstance();
        JRExporter exporter = null;
        String reportName = UtilValidate.isEmpty(name) ? jasperPrint.getName() : name;
        if (UtilValidate.isEmpty(reportName)) {
            reportName = "myReport";
        }

        if (contentType.equals(ContentType.PDF)) {
            response.setHeader(HEADER_CONTENT_DISPOSITION, String.format("attachment; filename=\"%1$s.pdf\"", reportName));
            exporter = new JRPdfExporter();
            exporterParameters.put(JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporterParameters.put(JRExporterParameter.OUTPUT_STREAM, os);
            // add logged user as author
            HttpSession session = request.getSession();
            GenericValue person = (GenericValue) session.getAttribute("person");
            if (UtilValidate.isNotEmpty(person)) {
                exporterParameters.put(JRPdfExporterParameter.METADATA_AUTHOR, PartyHelper.getPartyName((Delegator) request.getAttribute("delegator"), person.getString("partyId"), false));
            }
            // add product name as creator
            String opentaps = UtilProperties.getPropertyValue("OpentapsUiLabels.properties", "OpentapsProductName");
            if (UtilValidate.isNotEmpty(opentaps)) exporterParameters.put(JRPdfExporterParameter.METADATA_CREATOR, opentaps);

        } /*else if (contentType.equals(ContentType.HTML)) {
            response.setHeader(HEADER_CONTENT_DISPOSITION, String.format("attachment; filename=\"%1$s.html\"", reportName));
            exporter = new JRHtmlExporter();
            exporterParameters.put(JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporterParameters.put(JRExporterParameter.OUTPUT_STREAM, os);
            exporterParameters.put(JRHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN, Boolean.FALSE);
            exporterParameters.put(JRHtmlExporterParameter.IS_OUTPUT_IMAGES_TO_DIR, Boolean.FALSE);
        }*/ else if (contentType.equals(ContentType.XLS)) {
            response.setHeader(HEADER_CONTENT_DISPOSITION, String.format("attachment; filename=\"%1$s.xls\"", reportName));
            exporter = new JExcelApiExporter();
            //exporter = new JRXlsExporter();
            exporterParameters.put(JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporterParameters.put(JRExporterParameter.OUTPUT_STREAM, os);
            
            //exporter.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.TRUE);
            //exporter.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
            //exporter.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
            //exporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
            //exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, destFile.toString()); 
            //exporter.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.FALSE); 
            
        } else if (contentType.equals(ContentType.XML)) {
            response.setHeader(HEADER_CONTENT_DISPOSITION, String.format("attachment; filename=\"%1$s.xls\"", reportName));
            exporter = new JRXmlExporter();
            exporterParameters.put(JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporterParameters.put(JRExporterParameter.OUTPUT_STREAM, os);
            exporter.setParameters(exporterParameters);
        } else if (contentType.equals(ContentType.CSV)) {
            response.setHeader(HEADER_CONTENT_DISPOSITION, String.format("attachment; filename=\"%1$s.csv\"", reportName));
            exporter = new JRCsvExporter();
            exporterParameters.put(JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporterParameters.put(JRExporterParameter.OUTPUT_STREAM, os);
        } else if (contentType.equals(ContentType.RTF)){
            response.setHeader(HEADER_CONTENT_DISPOSITION, String.format("attachment; filename=\"%1$s.rtf\"", reportName));
            exporter = new JRRtfExporter();
            exporterParameters.put(JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporterParameters.put(JRExporterParameter.OUTPUT_STREAM, os);
        } else if (contentType.equals(ContentType.TXT)){
            response.setHeader(HEADER_CONTENT_DISPOSITION, String.format("attachment; filename=\"%1$s.txt\"", reportName));
            exporter = new JRTextExporter();
            exporterParameters.put(JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporterParameters.put(JRExporterParameter.OUTPUT_STREAM, os);
            exporterParameters.put(JRTextExporterParameter.CHARACTER_WIDTH, new Integer(80));
            exporterParameters.put(JRTextExporterParameter.CHARACTER_HEIGHT, new Integer(25));
        } else if (contentType.equals(ContentType.ODT)) {
            response.setHeader(HEADER_CONTENT_DISPOSITION, String.format("attachment; filename=\"%1$s.odf\"", reportName));
            exporter = new JROdtExporter();
            exporterParameters.put(JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporterParameters.put(JRExporterParameter.OUTPUT_STREAM, os);
        }

        exporter.setParameters(exporterParameters);
        exporter.exportReport();

    }

}
