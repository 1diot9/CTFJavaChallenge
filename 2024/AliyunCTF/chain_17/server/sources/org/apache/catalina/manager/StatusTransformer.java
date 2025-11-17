package org.apache.catalina.manager;

import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.util.FileSize;
import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.management.MBeanServer;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.QueryExp;
import org.apache.tomcat.util.json.JSONFilter;
import org.apache.tomcat.util.net.SSLUtilBase;
import org.apache.tomcat.util.security.Escape;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/manager/StatusTransformer.class */
public class StatusTransformer {
    public static void setContentType(HttpServletResponse response, int mode) {
        if (mode == 0) {
            response.setContentType("text/html;charset=utf-8");
        } else if (mode == 1) {
            response.setContentType("text/xml;charset=utf-8");
        } else if (mode == 2) {
            response.setContentType("application/json;charset=utf-8");
        }
    }

    public static void writeHeader(PrintWriter writer, Object[] args, int mode) {
        if (mode == 0) {
            writer.print(MessageFormat.format(Constants.HTML_HEADER_SECTION, args));
            return;
        }
        if (mode == 1) {
            writer.write(Constants.XML_DECLARATION);
            writer.print(MessageFormat.format(Constants.XML_STYLE, args));
            writer.write("<status>");
        } else if (mode == 2) {
            writer.append('{').append('\"').append(SSLUtilBase.DEFAULT_KEY_ALIAS).append('\"').append(':').append('{').println();
        }
    }

    public static void writeBody(PrintWriter writer, Object[] args, int mode) {
        if (mode == 0) {
            writer.print(MessageFormat.format(Constants.BODY_HEADER_SECTION, args));
        }
    }

    public static void writeManager(PrintWriter writer, Object[] args, int mode) {
        if (mode == 0) {
            writer.print(MessageFormat.format(Constants.MANAGER_SECTION, args));
        }
    }

    public static void writePageHeading(PrintWriter writer, Object[] args, int mode) {
        if (mode == 0) {
            writer.print(MessageFormat.format(Constants.SERVER_HEADER_SECTION, args));
        }
    }

    public static void writeServerInfo(PrintWriter writer, Object[] args, int mode) {
        if (mode == 0) {
            writer.print(MessageFormat.format(Constants.SERVER_ROW_SECTION, args));
        }
    }

    public static void writeFooter(PrintWriter writer, int mode) {
        if (mode == 0) {
            writer.print(Constants.HTML_TAIL_SECTION);
        } else if (mode == 1) {
            writer.write("</status>");
        } else if (mode == 2) {
            writer.append('}').append('}');
        }
    }

    public static void writeVMState(PrintWriter writer, int mode, Object[] args) throws Exception {
        SortedMap<String, MemoryPoolMXBean> memoryPoolMBeans = new TreeMap<>();
        for (MemoryPoolMXBean mbean : ManagementFactory.getMemoryPoolMXBeans()) {
            String sortKey = mbean.getType() + ":" + mbean.getName();
            memoryPoolMBeans.put(sortKey, mbean);
        }
        if (mode == 0) {
            writer.print("<h1>JVM</h1>");
            writer.print("<p>");
            writer.print(args[0]);
            writer.print(' ');
            writer.print(formatSize(Long.valueOf(Runtime.getRuntime().freeMemory()), true));
            writer.print(' ');
            writer.print(args[1]);
            writer.print(' ');
            writer.print(formatSize(Long.valueOf(Runtime.getRuntime().totalMemory()), true));
            writer.print(' ');
            writer.print(args[2]);
            writer.print(' ');
            writer.print(formatSize(Long.valueOf(Runtime.getRuntime().maxMemory()), true));
            writer.print("</p>");
            writer.write("<table border=\"0\"><thead><tr><th>" + args[3] + "</th><th>" + args[4] + "</th><th>" + args[5] + "</th><th>" + args[6] + "</th><th>" + args[7] + "</th><th>" + args[8] + "</th></tr></thead><tbody>");
            for (MemoryPoolMXBean memoryPoolMBean : memoryPoolMBeans.values()) {
                MemoryUsage usage = memoryPoolMBean.getUsage();
                writer.write("<tr><td>");
                writer.print(memoryPoolMBean.getName());
                writer.write("</td><td>");
                writer.print(memoryPoolMBean.getType());
                writer.write("</td><td>");
                writer.print(formatSize(Long.valueOf(usage.getInit()), true));
                writer.write("</td><td>");
                writer.print(formatSize(Long.valueOf(usage.getCommitted()), true));
                writer.write("</td><td>");
                writer.print(formatSize(Long.valueOf(usage.getMax()), true));
                writer.write("</td><td>");
                writer.print(formatSize(Long.valueOf(usage.getUsed()), true));
                if (usage.getMax() > 0) {
                    writer.write(" (" + ((usage.getUsed() * 100) / usage.getMax()) + "%)");
                }
                writer.write("</td></tr>");
            }
            writer.write("</tbody></table>");
            return;
        }
        if (mode == 1) {
            writer.write("<jvm>");
            writer.write("<memory");
            writer.write(" free='" + Runtime.getRuntime().freeMemory() + "'");
            writer.write(" total='" + Runtime.getRuntime().totalMemory() + "'");
            writer.write(" max='" + Runtime.getRuntime().maxMemory() + "'/>");
            for (MemoryPoolMXBean memoryPoolMBean2 : memoryPoolMBeans.values()) {
                MemoryUsage usage2 = memoryPoolMBean2.getUsage();
                writer.write("<memorypool");
                writer.write(" name='" + Escape.xml("", memoryPoolMBean2.getName()) + "'");
                writer.write(" type='" + memoryPoolMBean2.getType() + "'");
                writer.write(" usageInit='" + usage2.getInit() + "'");
                writer.write(" usageCommitted='" + usage2.getCommitted() + "'");
                writer.write(" usageMax='" + usage2.getMax() + "'");
                writer.write(" usageUsed='" + usage2.getUsed() + "'/>");
            }
            writer.write("</jvm>");
            return;
        }
        if (mode == 2) {
            indent(writer, 1).append('\"').append((CharSequence) "jvm").append('\"').append(':').append('{').println();
            indent(writer, 2).append('\"').append((CharSequence) "memory").append('\"').append(':').append('{');
            appendJSonValue(writer, "free", Long.toString(Runtime.getRuntime().freeMemory())).append(',');
            appendJSonValue(writer, "total", Long.toString(Runtime.getRuntime().totalMemory())).append(',');
            appendJSonValue(writer, "max", Long.toString(Runtime.getRuntime().maxMemory()));
            writer.append('}').append(',').println();
            indent(writer, 2).append('\"').append((CharSequence) "memorypool").append('\"').append(':').append('[');
            boolean first = true;
            for (MemoryPoolMXBean memoryPoolMBean3 : memoryPoolMBeans.values()) {
                MemoryUsage usage3 = memoryPoolMBean3.getUsage();
                if (first) {
                    first = false;
                    writer.println();
                } else {
                    writer.append(',').println();
                }
                indent(writer, 3).append('{');
                appendJSonValue(writer, "name", JSONFilter.escape(memoryPoolMBean3.getName())).append(',');
                appendJSonValue(writer, "type", memoryPoolMBean3.getType().toString()).append(',');
                appendJSonValue(writer, "usageInit", Long.toString(usage3.getInit())).append(',');
                appendJSonValue(writer, "usageCommitted", Long.toString(usage3.getCommitted())).append(',');
                appendJSonValue(writer, "usageMax", Long.toString(usage3.getMax())).append(',');
                appendJSonValue(writer, "usageUsed", Long.toString(usage3.getUsed()));
                writer.append('}');
            }
            writer.append(']').println();
            indent(writer, 1).append('}');
        }
    }

    private static PrintWriter appendJSonValue(PrintWriter writer, String name, String value) {
        return writer.append('\"').append((CharSequence) name).append('\"').append(':').append('\"').append((CharSequence) value).append('\"');
    }

    private static PrintWriter indent(PrintWriter writer, int count) {
        for (int i = 0; i < count; i++) {
            writer.append(' ').append(' ');
        }
        return writer;
    }

    public static void writeConnectorsState(PrintWriter writer, MBeanServer mBeanServer, List<ObjectName> threadPools, List<ObjectName> globalRequestProcessors, List<ObjectName> requestProcessors, int mode, Object[] args) throws Exception {
        if (mode == 2) {
            writer.append(',').println();
            indent(writer, 1).append('\"').append((CharSequence) "connector").append('\"').append(':').append('[').println();
        }
        boolean first = true;
        for (ObjectName objectName : threadPools) {
            if (first) {
                first = false;
            } else if (mode == 2) {
                writer.append(',').println();
            }
            String name = objectName.getKeyProperty("name");
            writeConnectorState(writer, objectName, name, mBeanServer, globalRequestProcessors, requestProcessors, mode, args);
        }
        if (mode == 2) {
            writer.append(']');
        }
    }

    public static void writeConnectorState(PrintWriter writer, ObjectName tpName, String name, MBeanServer mBeanServer, List<ObjectName> globalRequestProcessors, List<ObjectName> requestProcessors, int mode, Object[] args) throws Exception {
        if (mode != 0) {
            if (mode != 1) {
                if (mode == 2) {
                    indent(writer, 1).append('{').println();
                    indent(writer, 2);
                    appendJSonValue(writer, "name", JSONFilter.escape(name)).append(',').println();
                    indent(writer, 2).append('\"').append((CharSequence) "threadInfo").append('\"').append(':').append('{');
                    appendJSonValue(writer, "maxThreads", mBeanServer.getAttribute(tpName, "maxThreads").toString()).append(',');
                    appendJSonValue(writer, "currentThreadCount", mBeanServer.getAttribute(tpName, "currentThreadCount").toString()).append(',');
                    appendJSonValue(writer, "currentThreadsBusy", mBeanServer.getAttribute(tpName, "currentThreadsBusy").toString());
                    writer.append('}');
                    ObjectName grpName = null;
                    for (ObjectName objectName : globalRequestProcessors) {
                        if (name.equals(objectName.getKeyProperty("name")) && objectName.getKeyProperty("Upgrade") == null) {
                            grpName = objectName;
                        }
                    }
                    if (grpName != null) {
                        writer.append(',').println();
                        indent(writer, 2).append('\"').append((CharSequence) "requestInfo").append('\"').append(':').append('{');
                        appendJSonValue(writer, "maxTime", mBeanServer.getAttribute(grpName, "maxTime").toString()).append(',');
                        appendJSonValue(writer, "processingTime", mBeanServer.getAttribute(grpName, "processingTime").toString()).append(',');
                        appendJSonValue(writer, "requestCount", mBeanServer.getAttribute(grpName, "requestCount").toString()).append(',');
                        appendJSonValue(writer, "errorCount", mBeanServer.getAttribute(grpName, "errorCount").toString()).append(',');
                        appendJSonValue(writer, "bytesReceived", mBeanServer.getAttribute(grpName, "bytesReceived").toString()).append(',');
                        appendJSonValue(writer, "bytesSent", mBeanServer.getAttribute(grpName, "bytesSent").toString());
                        writer.append('}').println();
                    }
                    indent(writer, 1).append('}');
                    return;
                }
                return;
            }
            writer.write("<connector name='" + name + "'>");
            writer.write("<threadInfo ");
            writer.write(" maxThreads=\"" + mBeanServer.getAttribute(tpName, "maxThreads") + "\"");
            writer.write(" currentThreadCount=\"" + mBeanServer.getAttribute(tpName, "currentThreadCount") + "\"");
            writer.write(" currentThreadsBusy=\"" + mBeanServer.getAttribute(tpName, "currentThreadsBusy") + "\"");
            writer.write(" />");
            ObjectName grpName2 = null;
            for (ObjectName objectName2 : globalRequestProcessors) {
                if (name.equals(objectName2.getKeyProperty("name")) && objectName2.getKeyProperty("Upgrade") == null) {
                    grpName2 = objectName2;
                }
            }
            if (grpName2 != null) {
                writer.write("<requestInfo ");
                writer.write(" maxTime=\"" + mBeanServer.getAttribute(grpName2, "maxTime") + "\"");
                writer.write(" processingTime=\"" + mBeanServer.getAttribute(grpName2, "processingTime") + "\"");
                writer.write(" requestCount=\"" + mBeanServer.getAttribute(grpName2, "requestCount") + "\"");
                writer.write(" errorCount=\"" + mBeanServer.getAttribute(grpName2, "errorCount") + "\"");
                writer.write(" bytesReceived=\"" + mBeanServer.getAttribute(grpName2, "bytesReceived") + "\"");
                writer.write(" bytesSent=\"" + mBeanServer.getAttribute(grpName2, "bytesSent") + "\"");
                writer.write(" />");
                writer.write("<workers>");
                for (ObjectName objectName3 : requestProcessors) {
                    if (name.equals(objectName3.getKeyProperty("worker"))) {
                        writeProcessorState(writer, objectName3, mBeanServer, mode);
                    }
                }
                writer.write("</workers>");
            }
            writer.write("</connector>");
            return;
        }
        writer.print("<h1>");
        writer.print(name);
        writer.print("</h1>");
        writer.print("<p>");
        writer.print(args[0]);
        writer.print(' ');
        writer.print(mBeanServer.getAttribute(tpName, "maxThreads"));
        writer.print(' ');
        writer.print(args[1]);
        writer.print(' ');
        writer.print(mBeanServer.getAttribute(tpName, "currentThreadCount"));
        writer.print(' ');
        writer.print(args[2]);
        writer.print(' ');
        writer.print(mBeanServer.getAttribute(tpName, "currentThreadsBusy"));
        writer.print(' ');
        writer.print(args[3]);
        writer.print(' ');
        writer.print(mBeanServer.getAttribute(tpName, "keepAliveCount"));
        writer.print("<br>");
        ObjectName grpName3 = null;
        for (ObjectName objectName4 : globalRequestProcessors) {
            if (name.equals(objectName4.getKeyProperty("name")) && objectName4.getKeyProperty("Upgrade") == null) {
                grpName3 = objectName4;
            }
        }
        if (grpName3 == null) {
            return;
        }
        writer.print(args[4]);
        writer.print(' ');
        writer.print(formatTime(mBeanServer.getAttribute(grpName3, "maxTime"), false));
        writer.print(' ');
        writer.print(args[5]);
        writer.print(' ');
        writer.print(formatTime(mBeanServer.getAttribute(grpName3, "processingTime"), true));
        writer.print(' ');
        writer.print(args[6]);
        writer.print(' ');
        writer.print(mBeanServer.getAttribute(grpName3, "requestCount"));
        writer.print(' ');
        writer.print(args[7]);
        writer.print(' ');
        writer.print(mBeanServer.getAttribute(grpName3, "errorCount"));
        writer.print(' ');
        writer.print(args[8]);
        writer.print(' ');
        writer.print(formatSize(mBeanServer.getAttribute(grpName3, "bytesReceived"), true));
        writer.print(' ');
        writer.print(args[9]);
        writer.print(' ');
        writer.print(formatSize(mBeanServer.getAttribute(grpName3, "bytesSent"), true));
        writer.print("</p>");
        writer.print("<table border=\"0\"><tr><th>" + args[10] + "</th><th>" + args[11] + "</th><th>" + args[12] + "</th><th>" + args[13] + "</th><th>" + args[14] + "</th><th>" + args[15] + "</th><th>" + args[16] + "</th><th>" + args[17] + "</th></tr>");
        for (ObjectName objectName5 : requestProcessors) {
            if (name.equals(objectName5.getKeyProperty("worker"))) {
                writer.print("<tr>");
                writeProcessorState(writer, objectName5, mBeanServer, mode);
                writer.print("</tr>");
            }
        }
        writer.print("</table>");
        writer.print("<p>");
        writer.print(args[18]);
        writer.print("</p>");
    }

    protected static void writeProcessorState(PrintWriter writer, ObjectName pName, MBeanServer mBeanServer, int mode) throws Exception {
        String stageStr;
        Integer stageValue = (Integer) mBeanServer.getAttribute(pName, "stage");
        int stage = stageValue.intValue();
        boolean fullStatus = true;
        boolean showRequest = true;
        switch (stage) {
            case 0:
                stageStr = "R";
                fullStatus = false;
                break;
            case 1:
                stageStr = "P";
                fullStatus = false;
                break;
            case 2:
                stageStr = "P";
                fullStatus = false;
                break;
            case 3:
                stageStr = "S";
                break;
            case 4:
                stageStr = "F";
                break;
            case 5:
                stageStr = "F";
                break;
            case 6:
                stageStr = "K";
                showRequest = false;
                break;
            case 7:
                stageStr = "R";
                fullStatus = false;
                break;
            default:
                stageStr = CoreConstants.NA;
                fullStatus = false;
                break;
        }
        if (mode == 0) {
            writer.write("<td><strong>");
            writer.write(stageStr);
            writer.write("</strong></td>");
            if (fullStatus) {
                writer.write("<td>");
                writer.print(formatTime(mBeanServer.getAttribute(pName, "requestProcessingTime"), false));
                writer.write("</td>");
                writer.write("<td>");
                if (showRequest) {
                    writer.print(formatSize(mBeanServer.getAttribute(pName, "requestBytesSent"), false));
                } else {
                    writer.write(CoreConstants.NA);
                }
                writer.write("</td>");
                writer.write("<td>");
                if (showRequest) {
                    writer.print(formatSize(mBeanServer.getAttribute(pName, "requestBytesReceived"), false));
                } else {
                    writer.write(CoreConstants.NA);
                }
                writer.write("</td>");
                writer.write("<td>");
                writer.print(Escape.htmlElementContent(mBeanServer.getAttribute(pName, "remoteAddrForwarded")));
                writer.write("</td>");
                writer.write("<td>");
                writer.print(Escape.htmlElementContent(mBeanServer.getAttribute(pName, "remoteAddr")));
                writer.write("</td>");
                writer.write("<td nowrap>");
                writer.write(Escape.htmlElementContent(mBeanServer.getAttribute(pName, "virtualHost")));
                writer.write("</td>");
                writer.write("<td nowrap class=\"row-left\">");
                if (showRequest) {
                    writer.write(Escape.htmlElementContent(mBeanServer.getAttribute(pName, "method")));
                    writer.write(32);
                    writer.write(Escape.htmlElementContent(mBeanServer.getAttribute(pName, "currentUri")));
                    String queryString = (String) mBeanServer.getAttribute(pName, "currentQueryString");
                    if (queryString != null && !queryString.equals("")) {
                        writer.write(CoreConstants.NA);
                        writer.print(Escape.htmlElementContent(queryString));
                    }
                    writer.write(32);
                    writer.write(Escape.htmlElementContent(mBeanServer.getAttribute(pName, "protocol")));
                } else {
                    writer.write(CoreConstants.NA);
                }
                writer.write("</td>");
                return;
            }
            writer.write("<td>?</td><td>?</td><td>?</td><td>?</td><td>?</td><td>?</td>");
            return;
        }
        if (mode == 1) {
            writer.write("<worker ");
            writer.write(" stage=\"" + stageStr + "\"");
            if (fullStatus) {
                writer.write(" requestProcessingTime=\"" + mBeanServer.getAttribute(pName, "requestProcessingTime") + "\"");
                writer.write(" requestBytesSent=\"");
                if (showRequest) {
                    writer.write(mBeanServer.getAttribute(pName, "requestBytesSent"));
                } else {
                    writer.write(CustomBooleanEditor.VALUE_0);
                }
                writer.write("\"");
                writer.write(" requestBytesReceived=\"");
                if (showRequest) {
                    writer.write(mBeanServer.getAttribute(pName, "requestBytesReceived"));
                } else {
                    writer.write(CustomBooleanEditor.VALUE_0);
                }
                writer.write("\"");
                writer.write(" remoteAddr=\"" + Escape.htmlElementContent(mBeanServer.getAttribute(pName, "remoteAddr")) + "\"");
                writer.write(" virtualHost=\"" + Escape.htmlElementContent(mBeanServer.getAttribute(pName, "virtualHost")) + "\"");
                if (showRequest) {
                    writer.write(" method=\"" + Escape.htmlElementContent(mBeanServer.getAttribute(pName, "method")) + "\"");
                    writer.write(" currentUri=\"" + Escape.htmlElementContent(mBeanServer.getAttribute(pName, "currentUri")) + "\"");
                    String queryString2 = (String) mBeanServer.getAttribute(pName, "currentQueryString");
                    if (queryString2 != null && !queryString2.equals("")) {
                        writer.write(" currentQueryString=\"" + Escape.htmlElementContent(queryString2) + "\"");
                    } else {
                        writer.write(" currentQueryString=\"&#63;\"");
                    }
                    writer.write(" protocol=\"" + Escape.htmlElementContent(mBeanServer.getAttribute(pName, "protocol")) + "\"");
                } else {
                    writer.write(" method=\"&#63;\"");
                    writer.write(" currentUri=\"&#63;\"");
                    writer.write(" currentQueryString=\"&#63;\"");
                    writer.write(" protocol=\"&#63;\"");
                }
            } else {
                writer.write(" requestProcessingTime=\"0\"");
                writer.write(" requestBytesSent=\"0\"");
                writer.write(" requestBytesReceived=\"0\"");
                writer.write(" remoteAddr=\"&#63;\"");
                writer.write(" virtualHost=\"&#63;\"");
                writer.write(" method=\"&#63;\"");
                writer.write(" currentUri=\"&#63;\"");
                writer.write(" currentQueryString=\"&#63;\"");
                writer.write(" protocol=\"&#63;\"");
            }
            writer.write(" />");
        }
    }

    public static void writeDetailedState(PrintWriter writer, MBeanServer mBeanServer, int mode) throws Exception {
        ObjectName queryHosts = new ObjectName("*:j2eeType=WebModule,*");
        Set<ObjectName> hostsON = mBeanServer.queryNames(queryHosts, (QueryExp) null);
        if (mode == 0) {
            writer.print("<h1>");
            writer.print("Application list");
            writer.print("</h1>");
            writer.print("<p>");
            int count = 0;
            Iterator<ObjectName> iterator = hostsON.iterator();
            while (iterator.hasNext()) {
                ObjectName contextON = iterator.next();
                String webModuleName = contextON.getKeyProperty("name");
                if (webModuleName.startsWith("//")) {
                    webModuleName = webModuleName.substring(2);
                }
                int slash = webModuleName.indexOf(47);
                if (slash == -1) {
                    count++;
                } else {
                    int i = count;
                    count++;
                    writer.print("<a href=\"#" + i + ".0\">");
                    writer.print(Escape.htmlElementContent(webModuleName));
                    writer.print("</a>");
                    if (iterator.hasNext()) {
                        writer.print("<br>");
                    }
                }
            }
            writer.print("</p>");
            int count2 = 0;
            for (ObjectName contextON2 : hostsON) {
                int i2 = count2;
                count2++;
                writer.print("<a class=\"A.name\" name=\"" + i2 + ".0\">");
                writeContext(writer, contextON2, mBeanServer, mode);
            }
            return;
        }
        if (mode != 1 && mode == 2) {
            writer.append(',').println();
            indent(writer, 1).append('\"').append((CharSequence) "context").append('\"').append(':').append('[').println();
            boolean first = true;
            for (ObjectName contextON3 : hostsON) {
                if (first) {
                    first = false;
                } else {
                    writer.append(',').println();
                }
                writeContext(writer, contextON3, mBeanServer, mode);
            }
            writer.println();
            indent(writer, 1).append(']').println();
        }
    }

    protected static void writeContext(PrintWriter writer, ObjectName objectName, MBeanServer mBeanServer, int mode) throws Exception {
        String webModuleName = objectName.getKeyProperty("name");
        String name = webModuleName;
        if (name == null) {
            return;
        }
        if (name.startsWith("//")) {
            name = name.substring(2);
        }
        int slash = name.indexOf(47);
        if (slash != -1) {
            String hostName = name.substring(0, slash);
            String contextName = name.substring(slash);
            ObjectName queryManager = new ObjectName(objectName.getDomain() + ":type=Manager,context=" + contextName + ",host=" + hostName + ",*");
            Set<ObjectName> managersON = mBeanServer.queryNames(queryManager, (QueryExp) null);
            ObjectName managerON = null;
            for (ObjectName aManagersON : managersON) {
                managerON = aManagersON;
            }
            ObjectName queryJspMonitor = new ObjectName(objectName.getDomain() + ":type=JspMonitor,WebModule=" + webModuleName + ",*");
            Set<ObjectName> jspMonitorONs = mBeanServer.queryNames(queryJspMonitor, (QueryExp) null);
            if (contextName.equals("/")) {
            }
            if (mode == 0) {
                writer.print("<h1>");
                writer.print(Escape.htmlElementContent(name));
                writer.print("</h1>");
                writer.print("</a>");
                writer.print("<p>");
                Object startTime = mBeanServer.getAttribute(objectName, "startTime");
                writer.print(" Start time: " + new Date(((Long) startTime).longValue()));
                writer.print(" Startup time: ");
                writer.print(formatTime(mBeanServer.getAttribute(objectName, "startupTime"), false));
                writer.print(" TLD scan time: ");
                writer.print(formatTime(mBeanServer.getAttribute(objectName, "tldScanTime"), false));
                if (managerON != null) {
                    writeManager(writer, managerON, mBeanServer, mode);
                }
                if (jspMonitorONs != null) {
                    writeJspMonitor(writer, jspMonitorONs, mBeanServer, mode);
                }
                writer.print("</p>");
                String onStr = objectName.getDomain() + ":j2eeType=Servlet,WebModule=" + webModuleName + ",*";
                ObjectName servletObjectName = new ObjectName(onStr);
                Set<ObjectInstance> set = mBeanServer.queryMBeans(servletObjectName, (QueryExp) null);
                for (ObjectInstance oi : set) {
                    writeWrapper(writer, oi.getObjectName(), mBeanServer, mode);
                }
                return;
            }
            if (mode != 1 && mode == 2) {
                indent(writer, 2).append('{').println();
                appendJSonValue(indent(writer, 3), "name", JSONFilter.escape(JSONFilter.escape(name))).append(',');
                appendJSonValue(writer, "startTime", new Date(((Long) mBeanServer.getAttribute(objectName, "startTime")).longValue()).toString()).append(',');
                appendJSonValue(writer, "startupTime", mBeanServer.getAttribute(objectName, "startupTime").toString()).append(',');
                appendJSonValue(writer, "tldScanTime", mBeanServer.getAttribute(objectName, "tldScanTime").toString());
                if (managerON != null) {
                    writeManager(writer, managerON, mBeanServer, mode);
                }
                if (jspMonitorONs != null) {
                    writeJspMonitor(writer, jspMonitorONs, mBeanServer, mode);
                }
                writer.append(',').println();
                indent(writer, 3).append('\"').append((CharSequence) "wrapper").append('\"').append(':').append('[').println();
                String onStr2 = objectName.getDomain() + ":j2eeType=Servlet,WebModule=" + webModuleName + ",*";
                ObjectName servletObjectName2 = new ObjectName(onStr2);
                Set<ObjectInstance> set2 = mBeanServer.queryMBeans(servletObjectName2, (QueryExp) null);
                boolean first = true;
                for (ObjectInstance oi2 : set2) {
                    if (first) {
                        first = false;
                    } else {
                        writer.append(',').println();
                    }
                    writeWrapper(writer, oi2.getObjectName(), mBeanServer, mode);
                }
                writer.println();
                indent(writer, 3).append(']').println();
                indent(writer, 2).append('}');
            }
        }
    }

    public static void writeManager(PrintWriter writer, ObjectName objectName, MBeanServer mBeanServer, int mode) throws Exception {
        if (mode == 0) {
            writer.print("<br>");
            writer.print(" Active sessions: ");
            writer.print(mBeanServer.getAttribute(objectName, "activeSessions"));
            writer.print(" Session count: ");
            writer.print(mBeanServer.getAttribute(objectName, "sessionCounter"));
            writer.print(" Max active sessions: ");
            writer.print(mBeanServer.getAttribute(objectName, "maxActive"));
            writer.print(" Rejected session creations: ");
            writer.print(mBeanServer.getAttribute(objectName, "rejectedSessions"));
            writer.print(" Expired sessions: ");
            writer.print(mBeanServer.getAttribute(objectName, "expiredSessions"));
            writer.print(" Longest session alive time: ");
            writer.print(formatSeconds(mBeanServer.getAttribute(objectName, "sessionMaxAliveTime")));
            writer.print(" Average session alive time: ");
            writer.print(formatSeconds(mBeanServer.getAttribute(objectName, "sessionAverageAliveTime")));
            writer.print(" Processing time: ");
            writer.print(formatTime(mBeanServer.getAttribute(objectName, "processingTime"), false));
            return;
        }
        if (mode != 1 && mode == 2) {
            writer.append(',').println();
            indent(writer, 3).append('\"').append((CharSequence) "manager").append('\"').append(':').append('{');
            appendJSonValue(writer, "activeSessions", mBeanServer.getAttribute(objectName, "activeSessions").toString()).append(',');
            appendJSonValue(writer, "sessionCounter", mBeanServer.getAttribute(objectName, "sessionCounter").toString()).append(',');
            appendJSonValue(writer, "maxActive", mBeanServer.getAttribute(objectName, "maxActive").toString()).append(',');
            appendJSonValue(writer, "rejectedSessions", mBeanServer.getAttribute(objectName, "rejectedSessions").toString()).append(',');
            appendJSonValue(writer, "expiredSessions", mBeanServer.getAttribute(objectName, "expiredSessions").toString()).append(',');
            appendJSonValue(writer, "sessionMaxAliveTime", mBeanServer.getAttribute(objectName, "sessionMaxAliveTime").toString()).append(',');
            appendJSonValue(writer, "sessionAverageAliveTime", mBeanServer.getAttribute(objectName, "sessionAverageAliveTime").toString()).append(',');
            appendJSonValue(writer, "processingTime", mBeanServer.getAttribute(objectName, "processingTime").toString());
            writer.append('}');
        }
    }

    public static void writeJspMonitor(PrintWriter writer, Set<ObjectName> jspMonitorONs, MBeanServer mBeanServer, int mode) throws Exception {
        int jspCount = 0;
        int jspReloadCount = 0;
        for (ObjectName jspMonitorON : jspMonitorONs) {
            Object obj = mBeanServer.getAttribute(jspMonitorON, "jspCount");
            jspCount += ((Integer) obj).intValue();
            Object obj2 = mBeanServer.getAttribute(jspMonitorON, "jspReloadCount");
            jspReloadCount += ((Integer) obj2).intValue();
        }
        if (mode == 0) {
            writer.print("<br>");
            writer.print(" JSPs loaded: ");
            writer.print(jspCount);
            writer.print(" JSPs reloaded: ");
            writer.print(jspReloadCount);
            return;
        }
        if (mode != 1 && mode == 2) {
            writer.append(',').println();
            indent(writer, 3).append('\"').append((CharSequence) "jsp").append('\"').append(':').append('{');
            appendJSonValue(writer, "jspCount", Integer.toString(jspCount)).append(',');
            appendJSonValue(writer, "jspReloadCount", Integer.toString(jspReloadCount));
            writer.append('}');
        }
    }

    public static void writeWrapper(PrintWriter writer, ObjectName objectName, MBeanServer mBeanServer, int mode) throws Exception {
        String servletName = objectName.getKeyProperty("name");
        String[] mappings = (String[]) mBeanServer.invoke(objectName, "findMappings", (Object[]) null, (String[]) null);
        if (mode == 0) {
            writer.print("<h2>");
            writer.print(Escape.htmlElementContent(servletName));
            if (mappings != null && mappings.length > 0) {
                writer.print(" [ ");
                for (int i = 0; i < mappings.length; i++) {
                    writer.print(Escape.htmlElementContent(mappings[i]));
                    if (i < mappings.length - 1) {
                        writer.print(" , ");
                    }
                }
                writer.print(" ] ");
            }
            writer.print("</h2>");
            writer.print("<p>");
            writer.print(" Processing time: ");
            writer.print(formatTime(mBeanServer.getAttribute(objectName, "processingTime"), true));
            writer.print(" Max time: ");
            writer.print(formatTime(mBeanServer.getAttribute(objectName, "maxTime"), false));
            writer.print(" Request count: ");
            writer.print(mBeanServer.getAttribute(objectName, "requestCount"));
            writer.print(" Error count: ");
            writer.print(mBeanServer.getAttribute(objectName, "errorCount"));
            writer.print(" Load time: ");
            writer.print(formatTime(mBeanServer.getAttribute(objectName, "loadTime"), false));
            writer.print(" Classloading time: ");
            writer.print(formatTime(mBeanServer.getAttribute(objectName, "classLoadTime"), false));
            writer.print("</p>");
            return;
        }
        if (mode != 1 && mode == 2) {
            indent(writer, 4).append('{');
            appendJSonValue(writer, "servletName", JSONFilter.escape(servletName)).append(',');
            appendJSonValue(writer, "processingTime", mBeanServer.getAttribute(objectName, "processingTime").toString()).append(',');
            appendJSonValue(writer, "maxTime", mBeanServer.getAttribute(objectName, "maxTime").toString()).append(',');
            appendJSonValue(writer, "requestCount", mBeanServer.getAttribute(objectName, "requestCount").toString()).append(',');
            appendJSonValue(writer, "errorCount", mBeanServer.getAttribute(objectName, "errorCount").toString()).append(',');
            appendJSonValue(writer, "loadTime", mBeanServer.getAttribute(objectName, "loadTime").toString()).append(',');
            appendJSonValue(writer, "classLoadTime", mBeanServer.getAttribute(objectName, "classLoadTime").toString());
            writer.append('}');
        }
    }

    public static String formatSize(Object obj, boolean mb) {
        long bytes = -1;
        if (obj instanceof Long) {
            bytes = ((Long) obj).longValue();
        } else if (obj instanceof Integer) {
            bytes = ((Integer) obj).intValue();
        }
        if (mb) {
            StringBuilder buff = new StringBuilder();
            if (bytes < 0) {
                buff.append('-');
                bytes = -bytes;
            }
            long mbytes = bytes / FileSize.MB_COEFFICIENT;
            long rest = ((bytes - (mbytes * FileSize.MB_COEFFICIENT)) * 100) / FileSize.MB_COEFFICIENT;
            buff.append(mbytes).append('.');
            if (rest < 10) {
                buff.append('0');
            }
            buff.append(rest).append(" MiB");
            return buff.toString();
        }
        return (bytes / FileSize.KB_COEFFICIENT) + " KiB";
    }

    public static String formatTime(Object obj, boolean seconds) {
        long time = -1;
        if (obj instanceof Long) {
            time = ((Long) obj).longValue();
        } else if (obj instanceof Integer) {
            time = ((Integer) obj).intValue();
        }
        if (seconds) {
            return (((float) time) / 1000.0f) + " s";
        }
        return time + " ms";
    }

    public static String formatSeconds(Object obj) {
        long time = -1;
        if (obj instanceof Long) {
            time = ((Long) obj).longValue();
        } else if (obj instanceof Integer) {
            time = ((Integer) obj).intValue();
        }
        return time + " s";
    }
}
