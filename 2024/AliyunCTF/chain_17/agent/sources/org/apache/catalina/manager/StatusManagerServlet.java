package org.apache.catalina.manager;

import com.fasterxml.jackson.core.JsonFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.management.MBeanServer;
import javax.management.MBeanServerNotification;
import javax.management.Notification;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.QueryExp;
import org.apache.catalina.util.ServerInfo;
import org.apache.tomcat.util.modeler.Registry;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/manager/StatusManagerServlet.class */
public class StatusManagerServlet extends HttpServlet implements NotificationListener {
    private static final long serialVersionUID = 1;
    protected MBeanServer mBeanServer = null;
    protected final List<ObjectName> threadPools = Collections.synchronizedList(new ArrayList());
    protected final List<ObjectName> requestProcessors = Collections.synchronizedList(new ArrayList());
    protected final List<ObjectName> globalRequestProcessors = Collections.synchronizedList(new ArrayList());
    protected static final StringManager sm = StringManager.getManager(Constants.Package);

    @Override // jakarta.servlet.GenericServlet
    public void init() throws ServletException {
        this.mBeanServer = Registry.getRegistry(null, null).getMBeanServer();
        try {
            ObjectName objectName = new ObjectName("*:type=ThreadPool,*");
            Set<ObjectInstance> set = this.mBeanServer.queryMBeans(objectName, (QueryExp) null);
            for (ObjectInstance oi : set) {
                this.threadPools.add(oi.getObjectName());
            }
            ObjectName objectName2 = new ObjectName("*:type=GlobalRequestProcessor,*");
            Set<ObjectInstance> set2 = this.mBeanServer.queryMBeans(objectName2, (QueryExp) null);
            for (ObjectInstance oi2 : set2) {
                this.globalRequestProcessors.add(oi2.getObjectName());
            }
            ObjectName objectName3 = new ObjectName("*:type=RequestProcessor,*");
            Set<ObjectInstance> set3 = this.mBeanServer.queryMBeans(objectName3, (QueryExp) null);
            for (ObjectInstance oi3 : set3) {
                this.requestProcessors.add(oi3.getObjectName());
            }
            ObjectName objectName4 = new ObjectName("JMImplementation:type=MBeanServerDelegate");
            this.mBeanServer.addNotificationListener(objectName4, this, (NotificationFilter) null, (Object) null);
        } catch (Exception e) {
            log(sm.getString("managerServlet.error.jmx"), e);
        }
    }

    @Override // jakarta.servlet.GenericServlet, jakarta.servlet.Servlet
    public void destroy() {
        try {
            ObjectName objectName = new ObjectName("JMImplementation:type=MBeanServerDelegate");
            this.mBeanServer.removeNotificationListener(objectName, this, (NotificationFilter) null, (Object) null);
        } catch (Exception e) {
            log(sm.getString("managerServlet.error.jmx"), e);
        }
    }

    @Override // jakarta.servlet.http.HttpServlet
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        StringManager smClient = StringManager.getManager(Constants.Package, request.getLocales());
        int mode = 0;
        if (request.getParameter("XML") != null && request.getParameter("XML").equals("true")) {
            mode = 1;
        }
        if (request.getParameter(JsonFactory.FORMAT_NAME_JSON) != null && request.getParameter(JsonFactory.FORMAT_NAME_JSON).equals("true")) {
            mode = 2;
        }
        StatusTransformer.setContentType(response, mode);
        PrintWriter writer = response.getWriter();
        boolean completeStatus = false;
        if (request.getPathInfo() != null && request.getPathInfo().equals("/all")) {
            completeStatus = true;
        }
        StatusTransformer.writeHeader(writer, new Object[]{getServletContext().getContextPath()}, mode);
        Object[] args = new Object[2];
        args[0] = getServletContext().getContextPath();
        if (completeStatus) {
            args[1] = smClient.getString("statusServlet.complete");
        } else {
            args[1] = smClient.getString("statusServlet.title");
        }
        StatusTransformer.writeBody(writer, args, mode);
        Object[] args2 = new Object[9];
        args2[0] = smClient.getString("htmlManagerServlet.manager");
        args2[1] = response.encodeURL(getServletContext().getContextPath() + "/html/list");
        args2[2] = smClient.getString("htmlManagerServlet.list");
        args2[3] = getServletContext().getContextPath() + "/" + smClient.getString("htmlManagerServlet.helpHtmlManagerFile");
        args2[4] = smClient.getString("htmlManagerServlet.helpHtmlManager");
        args2[5] = getServletContext().getContextPath() + "/" + smClient.getString("htmlManagerServlet.helpManagerFile");
        args2[6] = smClient.getString("htmlManagerServlet.helpManager");
        if (completeStatus) {
            args2[7] = response.encodeURL(getServletContext().getContextPath() + "/status");
            args2[8] = smClient.getString("statusServlet.title");
        } else {
            args2[7] = response.encodeURL(getServletContext().getContextPath() + "/status/all");
            args2[8] = smClient.getString("statusServlet.complete");
        }
        StatusTransformer.writeManager(writer, args2, mode);
        StatusTransformer.writePageHeading(writer, new Object[]{smClient.getString("htmlManagerServlet.serverTitle"), smClient.getString("htmlManagerServlet.serverVersion"), smClient.getString("htmlManagerServlet.serverJVMVersion"), smClient.getString("htmlManagerServlet.serverJVMVendor"), smClient.getString("htmlManagerServlet.serverOSName"), smClient.getString("htmlManagerServlet.serverOSVersion"), smClient.getString("htmlManagerServlet.serverOSArch"), smClient.getString("htmlManagerServlet.serverHostname"), smClient.getString("htmlManagerServlet.serverIPAddress")}, mode);
        Object[] args3 = new Object[8];
        args3[0] = ServerInfo.getServerInfo();
        args3[1] = System.getProperty("java.runtime.version");
        args3[2] = System.getProperty("java.vm.vendor");
        args3[3] = System.getProperty("os.name");
        args3[4] = System.getProperty("os.version");
        args3[5] = System.getProperty("os.arch");
        try {
            InetAddress address = InetAddress.getLocalHost();
            args3[6] = address.getHostName();
            args3[7] = address.getHostAddress();
        } catch (UnknownHostException e) {
            args3[6] = "-";
            args3[7] = "-";
        }
        StatusTransformer.writeServerInfo(writer, args3, mode);
        try {
            StatusTransformer.writeVMState(writer, mode, new Object[]{smClient.getString("htmlManagerServlet.jvmFreeMemory"), smClient.getString("htmlManagerServlet.jvmTotalMemory"), smClient.getString("htmlManagerServlet.jvmMaxMemory"), smClient.getString("htmlManagerServlet.jvmTableTitleMemoryPool"), smClient.getString("htmlManagerServlet.jvmTableTitleType"), smClient.getString("htmlManagerServlet.jvmTableTitleInitial"), smClient.getString("htmlManagerServlet.jvmTableTitleTotal"), smClient.getString("htmlManagerServlet.jvmTableTitleMaximum"), smClient.getString("htmlManagerServlet.jvmTableTitleUsed")});
            StatusTransformer.writeConnectorsState(writer, this.mBeanServer, this.threadPools, this.globalRequestProcessors, this.requestProcessors, mode, new Object[]{smClient.getString("htmlManagerServlet.connectorStateMaxThreads"), smClient.getString("htmlManagerServlet.connectorStateThreadCount"), smClient.getString("htmlManagerServlet.connectorStateThreadBusy"), smClient.getString("htmlManagerServlet.connectorStateAliveSocketCount"), smClient.getString("htmlManagerServlet.connectorStateMaxProcessingTime"), smClient.getString("htmlManagerServlet.connectorStateProcessingTime"), smClient.getString("htmlManagerServlet.connectorStateRequestCount"), smClient.getString("htmlManagerServlet.connectorStateErrorCount"), smClient.getString("htmlManagerServlet.connectorStateBytesReceived"), smClient.getString("htmlManagerServlet.connectorStateBytesSent"), smClient.getString("htmlManagerServlet.connectorStateTableTitleStage"), smClient.getString("htmlManagerServlet.connectorStateTableTitleTime"), smClient.getString("htmlManagerServlet.connectorStateTableTitleBSent"), smClient.getString("htmlManagerServlet.connectorStateTableTitleBRecv"), smClient.getString("htmlManagerServlet.connectorStateTableTitleClientForw"), smClient.getString("htmlManagerServlet.connectorStateTableTitleClientAct"), smClient.getString("htmlManagerServlet.connectorStateTableTitleVHost"), smClient.getString("htmlManagerServlet.connectorStateTableTitleRequest"), smClient.getString("htmlManagerServlet.connectorStateHint")});
            if (request.getPathInfo() != null && request.getPathInfo().equals("/all")) {
                StatusTransformer.writeDetailedState(writer, this.mBeanServer, mode);
            }
            StatusTransformer.writeFooter(writer, mode);
        } catch (Exception e2) {
            throw new ServletException(e2);
        }
    }

    public void handleNotification(Notification notification, Object handback) {
        String type;
        if (notification instanceof MBeanServerNotification) {
            ObjectName objectName = ((MBeanServerNotification) notification).getMBeanName();
            if (notification.getType().equals("JMX.mbean.registered")) {
                String type2 = objectName.getKeyProperty("type");
                if (type2 != null) {
                    if (type2.equals("ThreadPool")) {
                        this.threadPools.add(objectName);
                        return;
                    } else if (type2.equals("GlobalRequestProcessor")) {
                        this.globalRequestProcessors.add(objectName);
                        return;
                    } else {
                        if (type2.equals("RequestProcessor")) {
                            this.requestProcessors.add(objectName);
                            return;
                        }
                        return;
                    }
                }
                return;
            }
            if (notification.getType().equals("JMX.mbean.unregistered") && (type = objectName.getKeyProperty("type")) != null) {
                if (type.equals("ThreadPool")) {
                    this.threadPools.remove(objectName);
                } else if (type.equals("GlobalRequestProcessor")) {
                    this.globalRequestProcessors.remove(objectName);
                } else if (type.equals("RequestProcessor")) {
                    this.requestProcessors.remove(objectName);
                }
            }
        }
    }
}
