package org.fio.homeapps.util;

import java.io.*;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import freemarker.template.*;

import org.ofbiz.base.util.template.FreeMarkerWorker;

public abstract class FreemarkerUtil {

    private static final String module = FreemarkerUtil.class.getName();

    // Utility class should not be instantiated.
    private FreemarkerUtil() { }

    /**
     * Renders a template fragment. If leaveTags is true, it will leave tags in place and highlighted in red if their data is missing.
     * @param templateIdString a <code>String</code> value
     * @param template the template code text (a <code>String</code> containing the Freemarker code)
     * @param context the context <code>Map</code> to use for rendering the template
     * @param outWriter the output <code>Writer</code> where the template gets rendered
     * @param leaveTags if set to <code>true</code>, undefined variables will be displayed as the variable name; else if set to <code>false</code>, undefined variables will be displayed as empty strings
     * @param highlightTags if set to <code>true</code> and leaveTags is set to <code>true</code>, undefined variables will be displayed as the variable name in red
     * @exception TemplateException if an error occurs
     * @exception IOException if an error occurs
     */
    @SuppressWarnings("unchecked")
    public static void renderTemplateWithTags(String templateIdString, String template, Map context, Writer outWriter, boolean leaveTags, boolean highlightTags) throws TemplateException, IOException {

        // For each ${beginList:something}...${endList:something} pair, replace the terminators with proper <#list somethings as something> and </#list>
        String listName = null;
        Matcher matcher = Pattern.compile("(?s)\\$\\{beginList:([\\p{L}\\p{Lu}]+)\\}(.*)\\$\\{endList:\\1\\}").matcher(template);
        StringBuffer sb = new StringBuffer();
        int start = 0;
        while (matcher.find()) {
            if (matcher.group(1) != null) {
                listName = matcher.group(1);
                sb.append(template.substring(start, matcher.start()));
                sb.append("<#list ");
                sb.append(listName);
                sb.append("s?default([])");
                sb.append(" as ");
                sb.append(listName);
                sb.append(">");
                if (matcher.group(2) != null) {
                    sb.append(matcher.group(2));
                }
                sb.append("</#list>");
                start = matcher.end();
            }
        }
        sb.append(template.substring(start));
        template = sb.toString();

        // matches the FTL place holders eg: ${someVariable}
        final String ftlPlaceHolderRegex = "\\$\\{(.*?)\\}";
        if (leaveTags) {
            if (highlightTags) {
                // replaces ${someVariable} by ${someVariable?default('<span style="color:ff0000">someVariable</span>')} to avoid template errors: if someVariable is not defined the string "someVariable" will be displayed in red
                template = template.replaceAll(ftlPlaceHolderRegex, "\\${$1?default(\"<span style=\\\\\"color:#ff0000\\\\\">\" + r\"\\${$1}</span>\")}");
            } else {
                // replaces ${someVariable} by ${someVariable?default("someVariable")} to avoid template errors: if someVariable is not defined the string "someVariable" will be displayed
                template = template.replaceAll(ftlPlaceHolderRegex, "\\${$1?default(r\"\\${$1}\")}");
            }
        } else {
            // replaces ${someVariable} by ${someVariable?if_exists} to avoid template errors: if someVariable is not defined nothing will be displayed
            template = template.replaceAll(ftlPlaceHolderRegex, "\\${$1?if_exists}");
        }
        FreeMarkerWorker.renderTemplate(templateIdString, template, context, outWriter, false);
    }
}
