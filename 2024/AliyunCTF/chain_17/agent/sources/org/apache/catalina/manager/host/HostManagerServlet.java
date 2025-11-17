package org.apache.catalina.manager.host;

import jakarta.servlet.ServletException;
import jakarta.servlet.UnavailableException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.StringTokenizer;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import org.apache.catalina.Container;
import org.apache.catalina.ContainerServlet;
import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.Host;
import org.apache.catalina.Wrapper;
import org.apache.catalina.core.ContainerBase;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.startup.HostConfig;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.buf.StringUtils;
import org.apache.tomcat.util.res.StringManager;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/manager/host/HostManagerServlet.class */
public class HostManagerServlet extends HttpServlet implements ContainerServlet {
    private static final long serialVersionUID = 1;
    protected static final StringManager sm = StringManager.getManager(Constants.Package);
    protected transient Context context = null;
    protected int debug = 1;
    protected transient Host installedHost = null;
    protected transient Engine engine = null;
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
            this.installedHost = null;
            this.engine = null;
        } else {
            this.context = (Context) wrapper.getParent();
            this.installedHost = (Host) this.context.getParent();
            this.engine = (Engine) this.installedHost.getParent();
        }
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
        String name = request.getParameter("name");
        response.setContentType("text/plain; charset=utf-8");
        response.setHeader("X-Content-Type-Options", "nosniff");
        PrintWriter writer = response.getWriter();
        if (command == null) {
            writer.println(smClient.getString("hostManagerServlet.noCommand"));
        } else if (command.equals("/add")) {
            add(request, writer, name, false, smClient);
        } else if (command.equals("/remove")) {
            remove(writer, name, smClient);
        } else if (command.equals("/list")) {
            list(writer, smClient);
        } else if (command.equals("/start")) {
            start(writer, name, smClient);
        } else if (command.equals("/stop")) {
            stop(writer, name, smClient);
        } else if (command.equals("/persist")) {
            persist(writer, smClient);
        } else {
            writer.println(smClient.getString("hostManagerServlet.unknownCommand", command));
        }
        writer.flush();
        writer.close();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void add(HttpServletRequest request, PrintWriter writer, String name, boolean htmlMode, StringManager smClient) {
        String aliases = request.getParameter("aliases");
        String appBase = request.getParameter("appBase");
        boolean manager = booleanParameter(request, "manager", false, htmlMode);
        boolean autoDeploy = booleanParameter(request, "autoDeploy", true, htmlMode);
        boolean deployOnStartup = booleanParameter(request, "deployOnStartup", true, htmlMode);
        boolean deployXML = booleanParameter(request, "deployXML", true, htmlMode);
        boolean unpackWARs = booleanParameter(request, "unpackWARs", true, htmlMode);
        boolean copyXML = booleanParameter(request, "copyXML", false, htmlMode);
        add(writer, name, aliases, appBase, manager, autoDeploy, deployOnStartup, deployXML, unpackWARs, copyXML, smClient);
    }

    protected boolean booleanParameter(HttpServletRequest request, String parameter, boolean theDefault, boolean htmlMode) {
        String value = request.getParameter(parameter);
        boolean booleanValue = theDefault;
        if (value != null) {
            if (htmlMode) {
                if (value.equals(CustomBooleanEditor.VALUE_ON)) {
                    booleanValue = true;
                }
            } else if (theDefault) {
                if (value.equals("false")) {
                    booleanValue = false;
                }
            } else if (value.equals("true")) {
                booleanValue = true;
            }
        } else if (htmlMode) {
            booleanValue = false;
        }
        return booleanValue;
    }

    @Override // jakarta.servlet.GenericServlet
    public void init() throws ServletException {
        if (this.wrapper == null || this.context == null) {
            throw new UnavailableException(sm.getString("hostManagerServlet.noWrapper"));
        }
        try {
            String value = getServletConfig().getInitParameter("debug");
            this.debug = Integer.parseInt(value);
        } catch (Throwable t) {
            ExceptionUtils.handleThrowable(t);
        }
    }

    protected synchronized void add(PrintWriter writer, String name, String aliases, String appBase, boolean manager, boolean autoDeploy, boolean deployOnStartup, boolean deployXML, boolean unpackWARs, boolean copyXML, StringManager smClient) {
        File appBaseFile;
        if (this.debug >= 1) {
            log(sm.getString("hostManagerServlet.add", name));
        }
        if (name == null || name.length() == 0) {
            writer.println(smClient.getString("hostManagerServlet.invalidHostName", name));
            return;
        }
        if (this.engine.findChild(name) != null) {
            writer.println(smClient.getString("hostManagerServlet.alreadyHost", name));
            return;
        }
        String applicationBase = appBase;
        if (applicationBase == null || applicationBase.length() == 0) {
            applicationBase = name;
        }
        File file = new File(applicationBase);
        if (!file.isAbsolute()) {
            file = new File(this.engine.getCatalinaBase(), file.getPath());
        }
        try {
            appBaseFile = file.getCanonicalFile();
        } catch (IOException e) {
            appBaseFile = file;
        }
        if (!appBaseFile.mkdirs() && !appBaseFile.isDirectory()) {
            writer.println(smClient.getString("hostManagerServlet.appBaseCreateFail", appBaseFile.toString(), name));
            return;
        }
        File configBaseFile = getConfigBase(name);
        if (manager) {
            if (configBaseFile == null) {
                writer.println(smClient.getString("hostManagerServlet.configBaseCreateFail", name));
                return;
            }
            try {
                InputStream is = getServletContext().getResourceAsStream("/WEB-INF/manager.xml");
                try {
                    if (is == null) {
                        writer.println(smClient.getString("hostManagerServlet.managerXml"));
                        if (is != null) {
                            is.close();
                            return;
                        }
                        return;
                    }
                    Path dest = new File(configBaseFile, "manager.xml").toPath();
                    Files.copy(is, dest, new CopyOption[0]);
                    if (is != null) {
                        is.close();
                    }
                } finally {
                }
            } catch (IOException e2) {
                writer.println(smClient.getString("hostManagerServlet.managerXml"));
                return;
            }
        }
        StandardHost host = new StandardHost();
        host.setAppBase(applicationBase);
        host.setName(name);
        host.addLifecycleListener(new HostConfig());
        if (aliases != null && !aliases.isEmpty()) {
            StringTokenizer tok = new StringTokenizer(aliases, ", ");
            while (tok.hasMoreTokens()) {
                host.addAlias(tok.nextToken());
            }
        }
        host.setAutoDeploy(autoDeploy);
        host.setDeployOnStartup(deployOnStartup);
        host.setDeployXML(deployXML);
        host.setUnpackWARs(unpackWARs);
        host.setCopyXML(copyXML);
        try {
            this.engine.addChild(host);
            if (((StandardHost) this.engine.findChild(name)) != null) {
                writer.println(smClient.getString("hostManagerServlet.addSuccess", name));
            } else {
                writer.println(smClient.getString("hostManagerServlet.addFailed", name));
            }
        } catch (Exception e3) {
            writer.println(smClient.getString("hostManagerServlet.exception", e3.toString()));
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public synchronized void remove(PrintWriter writer, String name, StringManager smClient) {
        if (this.debug >= 1) {
            log(sm.getString("hostManagerServlet.remove", name));
        }
        if (name == null || name.length() == 0) {
            writer.println(smClient.getString("hostManagerServlet.invalidHostName", name));
            return;
        }
        if (this.engine.findChild(name) == null) {
            writer.println(smClient.getString("hostManagerServlet.noHost", name));
            return;
        }
        if (this.engine.findChild(name) == this.installedHost) {
            writer.println(smClient.getString("hostManagerServlet.cannotRemoveOwnHost", name));
            return;
        }
        try {
            Container child = this.engine.findChild(name);
            this.engine.removeChild(child);
            if (child instanceof ContainerBase) {
                child.destroy();
            }
            Host host = (StandardHost) this.engine.findChild(name);
            if (host == null) {
                writer.println(smClient.getString("hostManagerServlet.removeSuccess", name));
            } else {
                writer.println(smClient.getString("hostManagerServlet.removeFailed", name));
            }
        } catch (Exception e) {
            writer.println(smClient.getString("hostManagerServlet.exception", e.toString()));
        }
    }

    protected void list(PrintWriter writer, StringManager smClient) {
        if (this.debug >= 1) {
            log(sm.getString("hostManagerServlet.list", this.engine.getName()));
        }
        writer.println(smClient.getString("hostManagerServlet.listed", this.engine.getName()));
        Container[] hosts = this.engine.findChildren();
        for (Container container : hosts) {
            Host host = (Host) container;
            String name = host.getName();
            String[] aliases = host.findAliases();
            writer.println(String.format("[%s]:[%s]", name, StringUtils.join(aliases)));
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void start(PrintWriter writer, String name, StringManager smClient) {
        if (this.debug >= 1) {
            log(sm.getString("hostManagerServlet.start", name));
        }
        if (name == null || name.length() == 0) {
            writer.println(smClient.getString("hostManagerServlet.invalidHostName", name));
            return;
        }
        Container host = this.engine.findChild(name);
        if (host == null) {
            writer.println(smClient.getString("hostManagerServlet.noHost", name));
            return;
        }
        if (host == this.installedHost) {
            writer.println(smClient.getString("hostManagerServlet.cannotStartOwnHost", name));
            return;
        }
        if (host.getState().isAvailable()) {
            writer.println(smClient.getString("hostManagerServlet.alreadyStarted", name));
            return;
        }
        try {
            host.start();
            writer.println(smClient.getString("hostManagerServlet.started", name));
        } catch (Exception e) {
            getServletContext().log(sm.getString("hostManagerServlet.startFailed", name), e);
            writer.println(smClient.getString("hostManagerServlet.startFailed", name));
            writer.println(smClient.getString("hostManagerServlet.exception", e.toString()));
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void stop(PrintWriter writer, String name, StringManager smClient) {
        if (this.debug >= 1) {
            log(sm.getString("hostManagerServlet.stop", name));
        }
        if (name == null || name.length() == 0) {
            writer.println(smClient.getString("hostManagerServlet.invalidHostName", name));
            return;
        }
        Container host = this.engine.findChild(name);
        if (host == null) {
            writer.println(smClient.getString("hostManagerServlet.noHost", name));
            return;
        }
        if (host == this.installedHost) {
            writer.println(smClient.getString("hostManagerServlet.cannotStopOwnHost", name));
            return;
        }
        if (!host.getState().isAvailable()) {
            writer.println(smClient.getString("hostManagerServlet.alreadyStopped", name));
            return;
        }
        try {
            host.stop();
            writer.println(smClient.getString("hostManagerServlet.stopped", name));
        } catch (Exception e) {
            getServletContext().log(sm.getString("hostManagerServlet.stopFailed", name), e);
            writer.println(smClient.getString("hostManagerServlet.stopFailed", name));
            writer.println(smClient.getString("hostManagerServlet.exception", e.toString()));
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void persist(PrintWriter writer, StringManager smClient) {
        if (this.debug >= 1) {
            log(sm.getString("hostManagerServlet.persist"));
        }
        try {
            MBeanServer platformMBeanServer = ManagementFactory.getPlatformMBeanServer();
            ObjectName oname = new ObjectName(this.engine.getDomain() + ":type=StoreConfig");
            platformMBeanServer.invoke(oname, "storeConfig", (Object[]) null, (String[]) null);
            writer.println(smClient.getString("hostManagerServlet.persisted"));
        } catch (Exception e) {
            getServletContext().log(sm.getString("hostManagerServlet.persistFailed"), e);
            writer.println(smClient.getString("hostManagerServlet.persistFailed"));
            if (e instanceof InstanceNotFoundException) {
                writer.println(smClient.getString("hostManagerServlet.noStoreConfig"));
            } else {
                writer.println(smClient.getString("hostManagerServlet.exception", e.toString()));
            }
        }
    }

    protected File getConfigBase(String hostName) {
        File configBase = new File(this.context.getCatalinaBase(), "conf");
        if (!configBase.exists()) {
            return null;
        }
        if (this.engine != null) {
            configBase = new File(configBase, this.engine.getName());
        }
        if (this.installedHost != null) {
            configBase = new File(configBase, hostName);
        }
        if (!configBase.mkdirs() && !configBase.isDirectory()) {
            return null;
        }
        return configBase;
    }
}
