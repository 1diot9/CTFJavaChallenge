package org.apache.catalina.manager;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.UnavailableException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.naming.Binding;
import javax.naming.NamingEnumeration;
import org.apache.catalina.Container;
import org.apache.catalina.ContainerServlet;
import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.Host;
import org.apache.catalina.Manager;
import org.apache.catalina.Server;
import org.apache.catalina.Service;
import org.apache.catalina.Session;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.startup.ExpandWar;
import org.apache.catalina.util.ContextName;
import org.apache.catalina.util.IOTools;
import org.apache.catalina.util.ServerInfo;
import org.apache.coyote.ProtocolHandler;
import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.apache.tomcat.util.Diagnostics;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.IntrospectionUtils;
import org.apache.tomcat.util.buf.StringUtils;
import org.apache.tomcat.util.modeler.Registry;
import org.apache.tomcat.util.net.SSLContext;
import org.apache.tomcat.util.net.SSLHostConfig;
import org.apache.tomcat.util.net.SSLHostConfigCertificate;
import org.apache.tomcat.util.net.SSLUtilBase;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.util.security.Escape;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.util.ResourceUtils;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/manager/ManagerServlet.class */
public class ManagerServlet extends HttpServlet implements ContainerServlet {
    private static final long serialVersionUID = 1;
    protected static final StringManager sm = StringManager.getManager(Constants.Package);
    protected File configBase = null;
    protected transient Context context = null;
    protected int debug = 1;
    protected File versioned = null;
    protected transient Host host = null;
    protected transient MBeanServer mBeanServer = null;
    protected ObjectName oname = null;
    protected transient javax.naming.Context global = null;
    protected transient Wrapper wrapper = null;

    @Override // org.apache.catalina.ContainerServlet
    public Wrapper getWrapper() {
        return this.wrapper;
    }

    @Override // org.apache.catalina.ContainerServlet
    public void setWrapper(Wrapper wrapper) {
        this.wrapper = wrapper;
        if (wrapper == null) {
            this.context = null;
            this.host = null;
            this.oname = null;
        } else {
            this.context = (Context) wrapper.getParent();
            this.host = (Host) this.context.getParent();
            Engine engine = (Engine) this.host.getParent();
            String name = engine.getName() + ":type=Deployer,host=" + this.host.getName();
            try {
                this.oname = new ObjectName(name);
            } catch (Exception e) {
                log(sm.getString("managerServlet.objectNameFail", name), e);
            }
        }
        this.mBeanServer = Registry.getRegistry(null, null).getMBeanServer();
    }

    @Override // jakarta.servlet.GenericServlet, jakarta.servlet.Servlet
    public void destroy() {
    }

    @Override // jakarta.servlet.http.HttpServlet
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        StringManager smClient = StringManager.getManager(Constants.Package, request.getLocales());
        String command = request.getPathInfo();
        if (command == null) {
            command = request.getServletPath();
        }
        String path = request.getParameter("path");
        String war = request.getParameter(ResourceUtils.URL_PROTOCOL_WAR);
        String config = request.getParameter("config");
        ContextName cn = null;
        if (path != null) {
            cn = new ContextName(path, request.getParameter("version"));
        } else if (config != null) {
            cn = ContextName.extractFromPath(config);
        } else if (war != null) {
            cn = ContextName.extractFromPath(war);
        }
        String type = request.getParameter("type");
        String tag = request.getParameter("tag");
        boolean update = false;
        if (request.getParameter("update") != null && request.getParameter("update").equals("true")) {
            update = true;
        }
        String tlsHostName = request.getParameter("tlsHostName");
        boolean statusLine = false;
        if ("true".equals(request.getParameter("statusLine"))) {
            statusLine = true;
        }
        response.setContentType("text/plain; charset=utf-8");
        response.setHeader("X-Content-Type-Options", "nosniff");
        PrintWriter writer = response.getWriter();
        if (command == null) {
            writer.println(smClient.getString("managerServlet.noCommand"));
        } else if (command.equals("/deploy")) {
            if (war != null || config != null) {
                deploy(writer, config, cn, war, update, smClient);
            } else if (tag != null) {
                deploy(writer, cn, tag, smClient);
            } else {
                writer.println(smClient.getString("managerServlet.invalidCommand", command));
            }
        } else if (command.equals("/list")) {
            list(writer, smClient);
        } else if (command.equals("/reload")) {
            reload(writer, cn, smClient);
        } else if (command.equals("/resources")) {
            resources(writer, type, smClient);
        } else if (command.equals("/save")) {
            save(writer, path, smClient);
        } else if (command.equals("/serverinfo")) {
            serverinfo(writer, smClient);
        } else if (command.equals("/sessions")) {
            expireSessions(writer, cn, request, smClient);
        } else if (command.equals("/expire")) {
            expireSessions(writer, cn, request, smClient);
        } else if (command.equals("/start")) {
            start(writer, cn, smClient);
        } else if (command.equals("/stop")) {
            stop(writer, cn, smClient);
        } else if (command.equals("/undeploy")) {
            undeploy(writer, cn, smClient);
        } else if (command.equals("/findleaks")) {
            findleaks(statusLine, writer, smClient);
        } else if (command.equals("/vminfo")) {
            vmInfo(writer, smClient, request.getLocales());
        } else if (command.equals("/threaddump")) {
            threadDump(writer, smClient, request.getLocales());
        } else if (command.equals("/sslConnectorCiphers")) {
            sslConnectorCiphers(writer, smClient);
        } else if (command.equals("/sslConnectorCerts")) {
            sslConnectorCerts(writer, smClient);
        } else if (command.equals("/sslConnectorTrustedCerts")) {
            sslConnectorTrustedCerts(writer, smClient);
        } else if (command.equals("/sslReload")) {
            sslReload(writer, tlsHostName, smClient);
        } else {
            writer.println(smClient.getString("managerServlet.unknownCommand", command));
        }
        writer.flush();
        writer.close();
    }

    @Override // jakarta.servlet.http.HttpServlet
    public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        StringManager smClient = StringManager.getManager(Constants.Package, request.getLocales());
        String command = request.getPathInfo();
        if (command == null) {
            command = request.getServletPath();
        }
        String path = request.getParameter("path");
        ContextName cn = null;
        if (path != null) {
            cn = new ContextName(path, request.getParameter("version"));
        }
        String config = request.getParameter("config");
        String tag = request.getParameter("tag");
        boolean update = false;
        if (request.getParameter("update") != null && request.getParameter("update").equals("true")) {
            update = true;
        }
        response.setContentType("text/plain;charset=utf-8");
        response.setHeader("X-Content-Type-Options", "nosniff");
        PrintWriter writer = response.getWriter();
        if (command == null) {
            writer.println(smClient.getString("managerServlet.noCommand"));
        } else if (command.equals("/deploy")) {
            deploy(writer, config, cn, tag, update, request, smClient);
        } else {
            writer.println(smClient.getString("managerServlet.unknownCommand", command));
        }
        writer.flush();
        writer.close();
    }

    @Override // jakarta.servlet.GenericServlet
    public void init() throws ServletException {
        if (this.wrapper == null || this.context == null) {
            throw new UnavailableException(sm.getString("managerServlet.noWrapper"));
        }
        try {
            String value = getServletConfig().getInitParameter("debug");
            this.debug = Integer.parseInt(value);
        } catch (Throwable t) {
            ExceptionUtils.handleThrowable(t);
        }
        Server server = ((Engine) this.host.getParent()).getService().getServer();
        if (server != null) {
            this.global = server.getGlobalNamingContext();
        }
        this.versioned = (File) getServletContext().getAttribute("jakarta.servlet.context.tempdir");
        this.configBase = new File(this.context.getCatalinaBase(), "conf");
        Container host = null;
        Container engine = null;
        for (Container container = this.context; container != null; container = container.getParent()) {
            if (container instanceof Host) {
                host = container;
            }
            if (container instanceof Engine) {
                engine = container;
            }
        }
        if (engine != null) {
            this.configBase = new File(this.configBase, engine.getName());
        }
        if (host != null) {
            this.configBase = new File(this.configBase, host.getName());
        }
        if (this.debug >= 1) {
            log("init: Associated with Deployer '" + this.oname + "'");
            if (this.global != null) {
                log("init: Global resources are available");
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void findleaks(boolean statusLine, PrintWriter writer, StringManager smClient) {
        if (!(this.host instanceof StandardHost)) {
            writer.println(smClient.getString("managerServlet.findleaksFail"));
            return;
        }
        String[] results = ((StandardHost) this.host).findReloadedContextMemoryLeaks();
        if (results.length > 0) {
            if (statusLine) {
                writer.println(smClient.getString("managerServlet.findleaksList"));
            }
            for (String result : results) {
                if (result.isEmpty()) {
                    result = "/";
                }
                writer.println(result);
            }
            return;
        }
        if (statusLine) {
            writer.println(smClient.getString("managerServlet.findleaksNone"));
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void sslReload(PrintWriter writer, String tlsHostName, StringManager smClient) {
        Connector[] connectors = getConnectors();
        boolean found = false;
        for (Connector connector : connectors) {
            if (Boolean.TRUE.equals(connector.getProperty("SSLEnabled"))) {
                ProtocolHandler protocol = connector.getProtocolHandler();
                if (protocol instanceof AbstractHttp11Protocol) {
                    AbstractHttp11Protocol<?> http11Protoocol = (AbstractHttp11Protocol) protocol;
                    if (tlsHostName == null || tlsHostName.length() == 0) {
                        found = true;
                        http11Protoocol.reloadSslHostConfigs();
                    } else {
                        SSLHostConfig[] sslHostConfigs = http11Protoocol.findSslHostConfigs();
                        for (SSLHostConfig sslHostConfig : sslHostConfigs) {
                            if (sslHostConfig.getHostName().equalsIgnoreCase(tlsHostName)) {
                                found = true;
                                http11Protoocol.reloadSslHostConfig(tlsHostName);
                            }
                        }
                    }
                }
            }
        }
        if (found) {
            if (tlsHostName == null || tlsHostName.length() == 0) {
                writer.println(smClient.getString("managerServlet.sslReloadAll"));
                return;
            } else {
                writer.println(smClient.getString("managerServlet.sslReload", tlsHostName));
                return;
            }
        }
        writer.println(smClient.getString("managerServlet.sslReloadFail"));
    }

    protected void vmInfo(PrintWriter writer, StringManager smClient, Enumeration<Locale> requestedLocales) {
        writer.println(smClient.getString("managerServlet.vminfo"));
        writer.print(Diagnostics.getVMInfo(requestedLocales));
    }

    protected void threadDump(PrintWriter writer, StringManager smClient, Enumeration<Locale> requestedLocales) {
        writer.println(smClient.getString("managerServlet.threaddump"));
        writer.print(Diagnostics.getThreadDump(requestedLocales));
    }

    protected void sslConnectorCiphers(PrintWriter writer, StringManager smClient) {
        writer.println(smClient.getString("managerServlet.sslConnectorCiphers"));
        Map<String, List<String>> connectorCiphers = getConnectorCiphers(smClient);
        for (Map.Entry<String, List<String>> entry : connectorCiphers.entrySet()) {
            writer.println(entry.getKey());
            for (String cipher : entry.getValue()) {
                writer.print("  ");
                writer.println(cipher);
            }
        }
    }

    private void sslConnectorCerts(PrintWriter writer, StringManager smClient) {
        writer.println(smClient.getString("managerServlet.sslConnectorCerts"));
        Map<String, List<String>> connectorCerts = getConnectorCerts(smClient);
        for (Map.Entry<String, List<String>> entry : connectorCerts.entrySet()) {
            writer.println(entry.getKey());
            for (String cert : entry.getValue()) {
                writer.println(cert);
            }
        }
    }

    private void sslConnectorTrustedCerts(PrintWriter writer, StringManager smClient) {
        writer.println(smClient.getString("managerServlet.sslConnectorTrustedCerts"));
        Map<String, List<String>> connectorTrustedCerts = getConnectorTrustedCerts(smClient);
        for (Map.Entry<String, List<String>> entry : connectorTrustedCerts.entrySet()) {
            writer.println(entry.getKey());
            for (String cert : entry.getValue()) {
                writer.println(cert);
            }
        }
    }

    protected synchronized void save(PrintWriter writer, String path, StringManager smClient) {
        try {
            ObjectName storeConfigOname = new ObjectName("Catalina:type=StoreConfig");
            if (!this.mBeanServer.isRegistered(storeConfigOname)) {
                writer.println(smClient.getString("managerServlet.storeConfig.noMBean", storeConfigOname));
                return;
            }
            if (path == null || path.length() == 0 || !path.startsWith("/")) {
                try {
                    this.mBeanServer.invoke(storeConfigOname, "storeConfig", (Object[]) null, (String[]) null);
                    writer.println(smClient.getString("managerServlet.saved"));
                    return;
                } catch (Exception e) {
                    log(sm.getString("managerServlet.error.storeConfig"), e);
                    writer.println(smClient.getString("managerServlet.exception", e.toString()));
                    return;
                }
            }
            String contextPath = path;
            if (path.equals("/")) {
                contextPath = "";
            }
            Context context = (Context) this.host.findChild(contextPath);
            if (context == null) {
                writer.println(smClient.getString("managerServlet.noContext", path));
                return;
            }
            try {
                Boolean result = (Boolean) this.mBeanServer.invoke(storeConfigOname, "store", new Object[]{context}, new String[]{"org.apache.catalina.Context"});
                if (result.booleanValue()) {
                    writer.println(smClient.getString("managerServlet.savedContext", path));
                } else {
                    writer.println(smClient.getString("managerServlet.savedContextFail", path));
                }
            } catch (Exception e2) {
                log(sm.getString("managerServlet.error.storeContextConfig", path), e2);
                writer.println(smClient.getString("managerServlet.exception", e2.toString()));
            }
        } catch (MalformedObjectNameException e3) {
            log(sm.getString("managerServlet.exception"), e3);
            writer.println(smClient.getString("managerServlet.exception", e3.toString()));
        }
    }

    /* JADX WARN: Finally extract failed */
    protected void deploy(PrintWriter writer, String config, ContextName cn, String tag, boolean update, HttpServletRequest request, StringManager smClient) {
        File uploadedWar;
        if (config != null && config.length() == 0) {
            config = null;
        }
        if (this.debug >= 1) {
            if (config == null) {
                log("deploy: Deploying web application '" + cn + "'");
            } else {
                log("deploy: Deploying web application '" + cn + "' with context configuration at '" + config + "'");
            }
        }
        if (validateContextName(cn, writer, smClient)) {
            String name = cn.getName();
            String baseName = cn.getBaseName();
            String displayPath = cn.getDisplayName();
            Context context = (Context) this.host.findChild(name);
            if (context != null && !update) {
                writer.println(smClient.getString("managerServlet.alreadyContext", displayPath));
                return;
            }
            if (config != null && config.startsWith(ResourceUtils.FILE_URL_PREFIX)) {
                config = config.substring(ResourceUtils.FILE_URL_PREFIX.length());
            }
            File deployedWar = new File(this.host.getAppBaseFile(), baseName + ".war");
            if (tag != null) {
                File uploadPath = new File(this.versioned, tag);
                if (!uploadPath.mkdirs() && !uploadPath.isDirectory()) {
                    writer.println(smClient.getString("managerServlet.mkdirFail", uploadPath));
                    return;
                }
                uploadedWar = new File(uploadPath, baseName + ".war");
            } else if (update) {
                uploadedWar = new File(deployedWar.getAbsolutePath() + ".tmp");
                if (uploadedWar.exists() && !uploadedWar.delete()) {
                    writer.println(smClient.getString("managerServlet.deleteFail", uploadedWar));
                }
            } else {
                uploadedWar = deployedWar;
            }
            if (this.debug >= 2) {
                log("Uploading WAR file to " + uploadedWar);
            }
            try {
                if (tryAddServiced(name)) {
                    if (config != null) {
                        try {
                            if (!this.configBase.mkdirs() && !this.configBase.isDirectory()) {
                                writer.println(smClient.getString("managerServlet.mkdirFail", this.configBase));
                                removeServiced(name);
                                return;
                            } else if (!ExpandWar.copy(new File(config), new File(this.configBase, baseName + ".xml"))) {
                                throw new Exception(sm.getString("managerServlet.copyError", config));
                            }
                        } catch (Throwable th) {
                            removeServiced(name);
                            throw th;
                        }
                    }
                    uploadWar(writer, request, uploadedWar, smClient);
                    if (update && tag == null) {
                        if (deployedWar.exists() && !deployedWar.delete()) {
                            writer.println(smClient.getString("managerServlet.deleteFail", deployedWar));
                            removeServiced(name);
                            return;
                        } else if (!uploadedWar.renameTo(deployedWar)) {
                            writer.println(smClient.getString("managerServlet.renameFail", uploadedWar, deployedWar));
                            removeServiced(name);
                            return;
                        }
                    }
                    if (tag != null) {
                        ExpandWar.copy(uploadedWar, deployedWar);
                    }
                    removeServiced(name);
                    check(name);
                } else {
                    writer.println(smClient.getString("managerServlet.inService", displayPath));
                }
                writeDeployResult(writer, smClient, name, displayPath);
            } catch (Exception e) {
                log(sm.getString("managerServlet.error.deploy", displayPath), e);
                writer.println(smClient.getString("managerServlet.exception", e.toString()));
            }
        }
    }

    protected void deploy(PrintWriter writer, ContextName cn, String tag, StringManager smClient) {
        if (!validateContextName(cn, writer, smClient)) {
            return;
        }
        String baseName = cn.getBaseName();
        String name = cn.getName();
        String displayPath = cn.getDisplayName();
        File localWar = new File(new File(this.versioned, tag), baseName + ".war");
        File deployedWar = new File(this.host.getAppBaseFile(), baseName + ".war");
        try {
            if (tryAddServiced(name)) {
                try {
                    if (!deployedWar.delete()) {
                        writer.println(smClient.getString("managerServlet.deleteFail", deployedWar));
                        removeServiced(name);
                        return;
                    } else {
                        ExpandWar.copy(localWar, deployedWar);
                        removeServiced(name);
                        check(name);
                    }
                } catch (Throwable th) {
                    removeServiced(name);
                    throw th;
                }
            } else {
                writer.println(smClient.getString("managerServlet.inService", displayPath));
            }
            writeDeployResult(writer, smClient, name, displayPath);
        } catch (Exception e) {
            log(sm.getString("managerServlet.error.deploy", displayPath), e);
            writer.println(smClient.getString("managerServlet.exception", e.toString()));
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Finally extract failed */
    public void deploy(PrintWriter writer, String config, ContextName cn, String war, boolean update, StringManager smClient) {
        if (config != null && config.length() == 0) {
            config = null;
        }
        if (war != null && war.length() == 0) {
            war = null;
        }
        if (this.debug >= 1) {
            if (config != null) {
                if (war != null) {
                    log("install: Installing context configuration at '" + config + "' from '" + war + "'");
                } else {
                    log("install: Installing context configuration at '" + config + "'");
                }
            } else if (cn != null) {
                log("install: Installing web application '" + cn + "' from '" + war + "'");
            } else {
                log("install: Installing web application from '" + war + "'");
            }
        }
        if (validateContextName(cn, writer, smClient)) {
            String name = cn.getName();
            String baseName = cn.getBaseName();
            String displayPath = cn.getDisplayName();
            Context context = (Context) this.host.findChild(name);
            if (context != null && !update) {
                writer.println(smClient.getString("managerServlet.alreadyContext", displayPath));
                return;
            }
            if (config != null && config.startsWith(ResourceUtils.FILE_URL_PREFIX)) {
                config = config.substring(ResourceUtils.FILE_URL_PREFIX.length());
            }
            if (war != null && war.startsWith(ResourceUtils.FILE_URL_PREFIX)) {
                war = war.substring(ResourceUtils.FILE_URL_PREFIX.length());
            }
            try {
                if (tryAddServiced(name)) {
                    if (config != null) {
                        try {
                            if (!this.configBase.mkdirs() && !this.configBase.isDirectory()) {
                                writer.println(smClient.getString("managerServlet.mkdirFail", this.configBase));
                                removeServiced(name);
                                return;
                            }
                            File localConfigFile = new File(this.configBase, baseName + ".xml");
                            File configFile = new File(config);
                            if (!configFile.getCanonicalPath().equals(localConfigFile.getCanonicalPath())) {
                                if (localConfigFile.isFile() && !localConfigFile.delete()) {
                                    writer.println(smClient.getString("managerServlet.deleteFail", localConfigFile));
                                    removeServiced(name);
                                    return;
                                }
                                ExpandWar.copy(configFile, localConfigFile);
                            }
                        } catch (Throwable th) {
                            removeServiced(name);
                            throw th;
                        }
                    }
                    if (war != null) {
                        File localWarFile = war.endsWith(".war") ? new File(this.host.getAppBaseFile(), baseName + ".war") : new File(this.host.getAppBaseFile(), baseName);
                        File warFile = new File(war);
                        if (!warFile.isAbsolute()) {
                            warFile = new File(this.host.getAppBaseFile(), war);
                        }
                        if (!warFile.getCanonicalPath().equals(localWarFile.getCanonicalPath())) {
                            if (localWarFile.exists() && !ExpandWar.delete(localWarFile)) {
                                writer.println(smClient.getString("managerServlet.deleteFail", localWarFile));
                                removeServiced(name);
                                return;
                            }
                            ExpandWar.copy(warFile, localWarFile);
                        }
                    }
                    removeServiced(name);
                    check(name);
                } else {
                    writer.println(smClient.getString("managerServlet.inService", displayPath));
                }
                writeDeployResult(writer, smClient, name, displayPath);
            } catch (Throwable t) {
                ExceptionUtils.handleThrowable(t);
                log(sm.getString("managerServlet.error.deploy", displayPath), t);
                writer.println(smClient.getString("managerServlet.exception", t.toString()));
            }
        }
    }

    private void writeDeployResult(PrintWriter writer, StringManager smClient, String name, String displayPath) {
        Context deployed = (Context) this.host.findChild(name);
        if (deployed != null && deployed.getConfigured() && deployed.getState().isAvailable()) {
            writer.println(smClient.getString("managerServlet.deployed", displayPath));
        } else if (deployed != null && !deployed.getState().isAvailable()) {
            writer.println(smClient.getString("managerServlet.deployedButNotStarted", displayPath));
        } else {
            writer.println(smClient.getString("managerServlet.deployFailed", displayPath));
        }
    }

    protected void list(PrintWriter writer, StringManager smClient) {
        List<String> parts;
        if (this.debug >= 1) {
            log("list: Listing contexts for virtual host '" + this.host.getName() + "'");
        }
        writer.println(smClient.getString("managerServlet.listed", this.host.getName()));
        Container[] contexts = this.host.findChildren();
        for (Container container : contexts) {
            Context context = (Context) container;
            if (context != null) {
                String displayPath = context.getPath();
                if (displayPath.equals("")) {
                    displayPath = "/";
                }
                if (context.getState().isAvailable()) {
                    parts = Arrays.asList(displayPath, "running", context.getManager().findSessions().length, context.getDocBase());
                } else {
                    parts = Arrays.asList(displayPath, "stopped", CustomBooleanEditor.VALUE_0, context.getDocBase());
                }
                writer.println(StringUtils.join(parts, ':'));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void reload(PrintWriter writer, ContextName cn, StringManager smClient) {
        if (this.debug >= 1) {
            log("restart: Reloading web application '" + cn + "'");
        }
        if (!validateContextName(cn, writer, smClient)) {
            return;
        }
        try {
            Context context = (Context) this.host.findChild(cn.getName());
            if (context == null) {
                writer.println(smClient.getString("managerServlet.noContext", Escape.htmlElementContent(cn.getDisplayName())));
            } else if (context.getName().equals(this.context.getName())) {
                writer.println(smClient.getString("managerServlet.noSelf"));
            } else {
                context.reload();
                writer.println(smClient.getString("managerServlet.reloaded", cn.getDisplayName()));
            }
        } catch (Throwable t) {
            ExceptionUtils.handleThrowable(t);
            log(sm.getString("managerServlet.error.reload", cn.getDisplayName()), t);
            writer.println(smClient.getString("managerServlet.exception", t.toString()));
        }
    }

    protected void resources(PrintWriter writer, String type, StringManager smClient) {
        if (this.debug >= 1) {
            if (type != null) {
                log("resources:  Listing resources of type " + type);
            } else {
                log("resources:  Listing resources of all types");
            }
        }
        if (this.global == null) {
            writer.println(smClient.getString("managerServlet.noGlobal"));
            return;
        }
        if (type != null) {
            writer.println(smClient.getString("managerServlet.resourcesType", type));
        } else {
            writer.println(smClient.getString("managerServlet.resourcesAll"));
        }
        printResources(writer, "", this.global, type, smClient);
    }

    protected void printResources(PrintWriter writer, String prefix, javax.naming.Context namingContext, String type, StringManager smClient) {
        try {
            NamingEnumeration<Binding> items = namingContext.listBindings("");
            while (items.hasMore()) {
                Binding item = (Binding) items.next();
                Object obj = item.getObject();
                if (obj instanceof javax.naming.Context) {
                    printResources(writer, prefix + item.getName() + "/", (javax.naming.Context) obj, type, smClient);
                } else if (type == null || (obj != null && IntrospectionUtils.isInstance(obj.getClass(), type))) {
                    writer.print(prefix + item.getName());
                    writer.print(':');
                    writer.print(item.getClassName());
                    writer.println();
                }
            }
        } catch (Throwable t) {
            ExceptionUtils.handleThrowable(t);
            log(sm.getString("managerServlet.error.resources", type), t);
            writer.println(smClient.getString("managerServlet.exception", t.toString()));
        }
    }

    protected void serverinfo(PrintWriter writer, StringManager smClient) {
        if (this.debug >= 1) {
            log("serverinfo");
        }
        try {
            writer.println(smClient.getString("managerServlet.serverInfo", ServerInfo.getServerInfo(), System.getProperty("os.name"), System.getProperty("os.version"), System.getProperty("os.arch"), System.getProperty("java.runtime.version"), System.getProperty("java.vm.vendor")));
        } catch (Throwable t) {
            ExceptionUtils.handleThrowable(t);
            log(sm.getString("managerServlet.error.serverInfo"), t);
            writer.println(smClient.getString("managerServlet.exception", t.toString()));
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void sessions(PrintWriter writer, ContextName cn, int idle, StringManager smClient) {
        if (this.debug >= 1) {
            log("sessions: Session information for web application '" + cn + "'");
            if (idle >= 0) {
                log("sessions: Session expiration for " + idle + " minutes '" + cn + "'");
            }
        }
        if (!validateContextName(cn, writer, smClient)) {
            return;
        }
        String displayPath = cn.getDisplayName();
        try {
            Context context = (Context) this.host.findChild(cn.getName());
            if (context == null) {
                writer.println(smClient.getString("managerServlet.noContext", Escape.htmlElementContent(displayPath)));
                return;
            }
            Manager manager = context.getManager();
            if (manager == null) {
                writer.println(smClient.getString("managerServlet.noManager", Escape.htmlElementContent(displayPath)));
                return;
            }
            int maxCount = 60;
            int histoInterval = 1;
            int maxInactiveInterval = context.getSessionTimeout();
            if (maxInactiveInterval > 0) {
                histoInterval = maxInactiveInterval / 60;
                if (histoInterval * 60 < maxInactiveInterval) {
                    histoInterval++;
                }
                if (0 == histoInterval) {
                    histoInterval = 1;
                }
                maxCount = maxInactiveInterval / histoInterval;
                if (histoInterval * maxCount < maxInactiveInterval) {
                    maxCount++;
                }
            }
            writer.println(smClient.getString("managerServlet.sessions", displayPath));
            writer.println(smClient.getString("managerServlet.sessiondefaultmax", maxInactiveInterval));
            Session[] sessions = manager.findSessions();
            int[] timeout = new int[maxCount + 1];
            int notimeout = 0;
            int expired = 0;
            for (Session session : sessions) {
                int time = (int) (session.getIdleTimeInternal() / 1000);
                if (idle >= 0 && time >= idle * 60) {
                    session.expire();
                    expired++;
                }
                int time2 = (time / 60) / histoInterval;
                if (time2 < 0) {
                    notimeout++;
                } else if (time2 >= maxCount) {
                    int i = maxCount;
                    timeout[i] = timeout[i] + 1;
                } else {
                    timeout[time2] = timeout[time2] + 1;
                }
            }
            if (timeout[0] > 0) {
                writer.println(smClient.getString("managerServlet.sessiontimeout", "<" + histoInterval, timeout[0]));
            }
            for (int i2 = 1; i2 < maxCount; i2++) {
                if (timeout[i2] > 0) {
                    writer.println(smClient.getString("managerServlet.sessiontimeout", (i2 * histoInterval) + " - <" + ((i2 + 1) * histoInterval), timeout[i2]));
                }
            }
            if (timeout[maxCount] > 0) {
                writer.println(smClient.getString("managerServlet.sessiontimeout", ">=" + (maxCount * histoInterval), timeout[maxCount]));
            }
            if (notimeout > 0) {
                writer.println(smClient.getString("managerServlet.sessiontimeout.unlimited", notimeout));
            }
            if (idle >= 0) {
                writer.println(smClient.getString("managerServlet.sessiontimeout.expired", ">" + idle, expired));
            }
        } catch (Throwable t) {
            ExceptionUtils.handleThrowable(t);
            log(sm.getString("managerServlet.error.sessions", displayPath), t);
            writer.println(smClient.getString("managerServlet.exception", t.toString()));
        }
    }

    protected void expireSessions(PrintWriter writer, ContextName cn, HttpServletRequest req, StringManager smClient) {
        int idle = -1;
        String idleParam = req.getParameter("idle");
        if (idleParam != null) {
            try {
                idle = Integer.parseInt(idleParam);
            } catch (NumberFormatException e) {
                log(sm.getString("managerServlet.error.idleParam", idleParam));
            }
        }
        sessions(writer, cn, idle, smClient);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void start(PrintWriter writer, ContextName cn, StringManager smClient) {
        if (this.debug >= 1) {
            log("start: Starting web application '" + cn + "'");
        }
        if (!validateContextName(cn, writer, smClient)) {
            return;
        }
        String displayPath = cn.getDisplayName();
        try {
            Context context = (Context) this.host.findChild(cn.getName());
            if (context == null) {
                writer.println(smClient.getString("managerServlet.noContext", Escape.htmlElementContent(displayPath)));
                return;
            }
            context.start();
            if (context.getState().isAvailable()) {
                writer.println(smClient.getString("managerServlet.started", displayPath));
            } else {
                writer.println(smClient.getString("managerServlet.startFailed", displayPath));
            }
        } catch (Throwable t) {
            ExceptionUtils.handleThrowable(t);
            log(sm.getString("managerServlet.error.start", displayPath), t);
            writer.println(smClient.getString("managerServlet.startFailed", displayPath));
            writer.println(smClient.getString("managerServlet.exception", t.toString()));
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void stop(PrintWriter writer, ContextName cn, StringManager smClient) {
        if (this.debug >= 1) {
            log("stop: Stopping web application '" + cn + "'");
        }
        if (!validateContextName(cn, writer, smClient)) {
            return;
        }
        String displayPath = cn.getDisplayName();
        try {
            Context context = (Context) this.host.findChild(cn.getName());
            if (context == null) {
                writer.println(smClient.getString("managerServlet.noContext", Escape.htmlElementContent(displayPath)));
            } else if (context.getName().equals(this.context.getName())) {
                writer.println(smClient.getString("managerServlet.noSelf"));
            } else {
                context.stop();
                writer.println(smClient.getString("managerServlet.stopped", displayPath));
            }
        } catch (Throwable t) {
            ExceptionUtils.handleThrowable(t);
            log(sm.getString("managerServlet.error.stop", displayPath), t);
            writer.println(smClient.getString("managerServlet.exception", t.toString()));
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void undeploy(PrintWriter writer, ContextName cn, StringManager smClient) {
        if (this.debug >= 1) {
            log("undeploy: Undeploying web application at '" + cn + "'");
        }
        if (validateContextName(cn, writer, smClient)) {
            String name = cn.getName();
            String baseName = cn.getBaseName();
            String displayPath = cn.getDisplayName();
            try {
                Context context = (Context) this.host.findChild(name);
                if (context == null) {
                    writer.println(smClient.getString("managerServlet.noContext", Escape.htmlElementContent(displayPath)));
                    return;
                }
                if (!isDeployed(name)) {
                    writer.println(smClient.getString("managerServlet.notDeployed", Escape.htmlElementContent(displayPath)));
                    return;
                }
                if (tryAddServiced(name)) {
                    try {
                        context.stop();
                    } catch (Throwable t) {
                        ExceptionUtils.handleThrowable(t);
                    }
                    try {
                        File war = new File(this.host.getAppBaseFile(), baseName + ".war");
                        File dir = new File(this.host.getAppBaseFile(), baseName);
                        File xml = new File(this.configBase, baseName + ".xml");
                        if (war.exists() && !war.delete()) {
                            writer.println(smClient.getString("managerServlet.deleteFail", war));
                            removeServiced(name);
                            return;
                        }
                        if (dir.exists() && !ExpandWar.delete(dir, false)) {
                            writer.println(smClient.getString("managerServlet.deleteFail", dir));
                            removeServiced(name);
                            return;
                        } else if (xml.exists() && !xml.delete()) {
                            writer.println(smClient.getString("managerServlet.deleteFail", xml));
                            removeServiced(name);
                            return;
                        } else {
                            removeServiced(name);
                            check(name);
                        }
                    } catch (Throwable th) {
                        removeServiced(name);
                        throw th;
                    }
                } else {
                    writer.println(smClient.getString("managerServlet.inService", displayPath));
                }
                writer.println(smClient.getString("managerServlet.undeployed", displayPath));
            } catch (Throwable t2) {
                ExceptionUtils.handleThrowable(t2);
                log(sm.getString("managerServlet.error.undeploy", displayPath), t2);
                writer.println(smClient.getString("managerServlet.exception", t2.toString()));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isDeployed(String name) throws Exception {
        String[] params = {name};
        String[] signature = {"java.lang.String"};
        Boolean result = (Boolean) this.mBeanServer.invoke(this.oname, "isDeployed", params, signature);
        return result.booleanValue();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void check(String name) throws Exception {
        String[] params = {name};
        String[] signature = {"java.lang.String"};
        this.mBeanServer.invoke(this.oname, "check", params, signature);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean tryAddServiced(String name) throws Exception {
        String[] params = {name};
        String[] signature = {"java.lang.String"};
        Boolean result = (Boolean) this.mBeanServer.invoke(this.oname, "tryAddServiced", params, signature);
        return result.booleanValue();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void removeServiced(String name) throws Exception {
        String[] params = {name};
        String[] signature = {"java.lang.String"};
        this.mBeanServer.invoke(this.oname, "removeServiced", params, signature);
    }

    protected void uploadWar(PrintWriter writer, HttpServletRequest request, File war, StringManager smClient) throws IOException {
        if (war.exists() && !war.delete()) {
            String msg = smClient.getString("managerServlet.deleteFail", war);
            throw new IOException(msg);
        }
        try {
            ServletInputStream istream = request.getInputStream();
            try {
                OutputStream ostream = new FileOutputStream(war);
                try {
                    IOTools.flow(istream, ostream);
                    ostream.close();
                    if (istream != null) {
                        istream.close();
                    }
                } catch (Throwable th) {
                    try {
                        ostream.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                    throw th;
                }
            } finally {
            }
        } catch (IOException e) {
            if (war.exists() && !war.delete()) {
                writer.println(smClient.getString("managerServlet.deleteFail", war));
            }
            throw e;
        }
    }

    protected static boolean validateContextName(ContextName cn, PrintWriter writer, StringManager smClient) {
        if (cn != null && (cn.getPath().startsWith("/") || cn.getPath().equals(""))) {
            return true;
        }
        String path = null;
        if (cn != null) {
            path = Escape.htmlElementContent(cn.getPath());
        }
        writer.println(smClient.getString("managerServlet.invalidPath", path));
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Map<String, List<String>> getConnectorCiphers(StringManager smClient) {
        Map<String, List<String>> result = new HashMap<>();
        Connector[] connectors = getConnectors();
        for (Connector connector : connectors) {
            if (Boolean.TRUE.equals(connector.getProperty("SSLEnabled"))) {
                SSLHostConfig[] sslHostConfigs = connector.getProtocolHandler().findSslHostConfigs();
                for (SSLHostConfig sslHostConfig : sslHostConfigs) {
                    String name = connector.toString() + "-" + sslHostConfig.getHostName();
                    result.put(name, new ArrayList<>(new LinkedHashSet(Arrays.asList(sslHostConfig.getEnabledCiphers()))));
                }
            } else {
                ArrayList<String> cipherList = new ArrayList<>(1);
                cipherList.add(smClient.getString("managerServlet.notSslConnector"));
                result.put(connector.toString(), cipherList);
            }
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Map<String, List<String>> getConnectorCerts(StringManager smClient) {
        Map<String, List<String>> result = new HashMap<>();
        Connector[] connectors = getConnectors();
        for (Connector connector : connectors) {
            if (Boolean.TRUE.equals(connector.getProperty("SSLEnabled"))) {
                SSLHostConfig[] sslHostConfigs = connector.getProtocolHandler().findSslHostConfigs();
                for (SSLHostConfig sslHostConfig : sslHostConfigs) {
                    if (sslHostConfig.getOpenSslContext().longValue() == 0) {
                        Set<SSLHostConfigCertificate> sslHostConfigCerts = sslHostConfig.getCertificates();
                        for (SSLHostConfigCertificate sslHostConfigCert : sslHostConfigCerts) {
                            String name = connector.toString() + "-" + sslHostConfig.getHostName() + "-" + sslHostConfigCert.getType();
                            List<String> certList = new ArrayList<>();
                            SSLContext sslContext = sslHostConfigCert.getSslContext();
                            String alias = sslHostConfigCert.getCertificateKeyAlias();
                            if (alias == null) {
                                alias = SSLUtilBase.DEFAULT_KEY_ALIAS;
                            }
                            Certificate[] certs = sslContext.getCertificateChain(alias);
                            if (certs == null) {
                                certList.add(smClient.getString("managerServlet.certsNotAvailable"));
                            } else {
                                for (Certificate cert : certs) {
                                    certList.add(cert.toString());
                                }
                            }
                            result.put(name, certList);
                        }
                    } else {
                        List<String> certList2 = new ArrayList<>();
                        certList2.add(smClient.getString("managerServlet.certsNotAvailable"));
                        String name2 = connector.toString() + "-" + sslHostConfig.getHostName();
                        result.put(name2, certList2);
                    }
                }
            } else {
                List<String> certList3 = new ArrayList<>(1);
                certList3.add(smClient.getString("managerServlet.notSslConnector"));
                result.put(connector.toString(), certList3);
            }
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Map<String, List<String>> getConnectorTrustedCerts(StringManager smClient) {
        Map<String, List<String>> result = new HashMap<>();
        Connector[] connectors = getConnectors();
        for (Connector connector : connectors) {
            if (Boolean.TRUE.equals(connector.getProperty("SSLEnabled"))) {
                SSLHostConfig[] sslHostConfigs = connector.getProtocolHandler().findSslHostConfigs();
                for (SSLHostConfig sslHostConfig : sslHostConfigs) {
                    String name = connector.toString() + "-" + sslHostConfig.getHostName();
                    List<String> certList = new ArrayList<>();
                    if (sslHostConfig.getOpenSslContext().longValue() == 0) {
                        SSLContext sslContext = sslHostConfig.getCertificates().iterator().next().getSslContext();
                        Certificate[] certs = sslContext.getAcceptedIssuers();
                        if (certs == null) {
                            certList.add(smClient.getString("managerServlet.certsNotAvailable"));
                        } else if (certs.length == 0) {
                            certList.add(smClient.getString("managerServlet.trustedCertsNotConfigured"));
                        } else {
                            for (Certificate cert : certs) {
                                certList.add(cert.toString());
                            }
                        }
                    } else {
                        certList.add(smClient.getString("managerServlet.certsNotAvailable"));
                    }
                    result.put(name, certList);
                }
            } else {
                List<String> certList2 = new ArrayList<>(1);
                certList2.add(smClient.getString("managerServlet.notSslConnector"));
                result.put(connector.toString(), certList2);
            }
        }
        return result;
    }

    private Connector[] getConnectors() {
        Engine e = (Engine) this.host.getParent();
        Service s = e.getService();
        return s.findConnectors();
    }
}
