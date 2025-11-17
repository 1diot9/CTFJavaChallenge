package org.apache.catalina.manager.host;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;
import org.apache.catalina.Container;
import org.apache.catalina.Host;
import org.apache.catalina.util.ServerInfo;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.util.security.Escape;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/manager/host/HTMLHostManagerServlet.class */
public final class HTMLHostManagerServlet extends HostManagerServlet {
    private static final long serialVersionUID = 1;
    private static final String HOSTS_HEADER_SECTION = "<table border=\"1\" cellspacing=\"0\" cellpadding=\"3\">\n<tr>\n <td colspan=\"5\" class=\"title\">{0}</td>\n</tr>\n<tr>\n <td class=\"header-left\"><small>{0}</small></td>\n <td class=\"header-center\"><small>{1}</small></td>\n <td class=\"header-center\"><small>{2}</small></td>\n</tr>\n";
    private static final String HOSTS_ROW_DETAILS_SECTION = "<tr>\n <td class=\"row-left\"><small><a href=\"http://{0}\" rel=\"noopener noreferrer\">{0}</a></small></td>\n <td class=\"row-center\"><small>{1}</small></td>\n";
    private static final String MANAGER_HOST_ROW_BUTTON_SECTION = " <td class=\"row-left\">\n  <small>{4}</small>\n </td>\n</tr>\n";
    private static final String HOSTS_ROW_BUTTON_SECTION = " <td class=\"row-left\" NOWRAP>\n  <form class=\"inline\" method=\"POST\" action=\"{0}\">   <small><input type=\"submit\" value=\"{1}\"></small>  </form>\n  <form class=\"inline\" method=\"POST\" action=\"{2}\">   <small><input type=\"submit\" value=\"{3}\"></small>  </form>\n </td>\n</tr>\n";
    private static final String ADD_SECTION_START = "</table>\n<br>\n<table border=\"1\" cellspacing=\"0\" cellpadding=\"3\">\n<tr>\n <td colspan=\"2\" class=\"title\">{0}</td>\n</tr>\n<tr>\n <td colspan=\"2\" class=\"header-left\"><small>{1}</small></td>\n</tr>\n<tr>\n <td colspan=\"2\">\n<form method=\"post\" action=\"{2}\">\n<table cellspacing=\"0\" cellpadding=\"3\">\n<tr>\n <td class=\"row-right\">\n  <small>{3}</small>\n </td>\n <td class=\"row-left\">\n  <input type=\"text\" name=\"name\" size=\"20\">\n </td>\n</tr>\n<tr>\n <td class=\"row-right\">\n  <small>{4}</small>\n </td>\n <td class=\"row-left\">\n  <input type=\"text\" name=\"aliases\" size=\"64\">\n </td>\n</tr>\n<tr>\n <td class=\"row-right\">\n  <small>{5}</small>\n </td>\n <td class=\"row-left\">\n  <input type=\"text\" name=\"appBase\" size=\"64\">\n </td>\n</tr>\n";
    private static final String ADD_SECTION_BOOLEAN = "<tr>\n <td class=\"row-right\">\n  <small>{0}</small>\n </td>\n <td class=\"row-left\">\n  <input type=\"checkbox\" name=\"{1}\" {2}>\n </td>\n</tr>\n";
    private static final String ADD_SECTION_END = "<tr>\n <td class=\"row-right\">\n  &nbsp;\n </td>\n <td class=\"row-left\">\n  <input type=\"submit\" value=\"{0}\">\n </td>\n</tr>\n</table>\n</form>\n</td>\n</tr>\n</table>\n<br>\n\n";
    private static final String PERSIST_SECTION = "<table border=\"1\" cellspacing=\"0\" cellpadding=\"3\">\n<tr>\n <td class=\"title\">{0}</td>\n</tr>\n<tr>\n <td class=\"row-left\">\n  <form class=\"inline\" method=\"POST\" action=\"{1}\">   <small><input type=\"submit\" value=\"{2}\"></small>  </form> {3}\n </td>\n</tr>\n</table>\n<br>\n\n";

    @Override // org.apache.catalina.manager.host.HostManagerServlet, jakarta.servlet.http.HttpServlet
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        StringManager smClient = StringManager.getManager(Constants.Package, request.getLocales());
        String command = request.getPathInfo();
        response.setContentType("text/html; charset=utf-8");
        String message = "";
        if (command != null && !command.equals("/list")) {
            message = (command.equals("/add") || command.equals("/remove") || command.equals("/start") || command.equals("/stop") || command.equals("/persist")) ? smClient.getString("hostManagerServlet.postCommand", command) : smClient.getString("hostManagerServlet.unknownCommand", command);
        }
        list(request, response, message, smClient);
    }

    @Override // jakarta.servlet.http.HttpServlet
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        StringManager smClient = StringManager.getManager(Constants.Package, request.getLocales());
        String command = request.getPathInfo();
        String name = request.getParameter("name");
        response.setContentType("text/html; charset=utf-8");
        String message = "";
        if (command != null) {
            if (command.equals("/add")) {
                message = add(request, name, smClient);
            } else if (command.equals("/remove")) {
                message = remove(name, smClient);
            } else if (command.equals("/start")) {
                message = start(name, smClient);
            } else if (command.equals("/stop")) {
                message = stop(name, smClient);
            } else if (command.equals("/persist")) {
                message = persist(smClient);
            } else {
                doGet(request, response);
            }
        }
        list(request, response, message, smClient);
    }

    protected String add(HttpServletRequest request, String name, StringManager smClient) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        super.add(request, printWriter, name, true, smClient);
        return stringWriter.toString();
    }

    protected String remove(String name, StringManager smClient) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        super.remove(printWriter, name, smClient);
        return stringWriter.toString();
    }

    protected String start(String name, StringManager smClient) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        super.start(printWriter, name, smClient);
        return stringWriter.toString();
    }

    protected String stop(String name, StringManager smClient) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        super.stop(printWriter, name, smClient);
        return stringWriter.toString();
    }

    protected String persist(StringManager smClient) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        super.persist(printWriter, smClient);
        return stringWriter.toString();
    }

    public void list(HttpServletRequest request, HttpServletResponse response, String message, StringManager smClient) throws IOException {
        if (this.debug >= 1) {
            log(sm.getString("hostManagerServlet.list", this.engine.getName()));
        }
        PrintWriter writer = response.getWriter();
        Object[] args = {getServletContext().getContextPath(), smClient.getString("htmlHostManagerServlet.title")};
        writer.print(MessageFormat.format(org.apache.catalina.manager.Constants.HTML_HEADER_SECTION, args));
        writer.print(MessageFormat.format(org.apache.catalina.manager.Constants.BODY_HEADER_SECTION, args));
        Object[] args2 = new Object[3];
        args2[0] = smClient.getString("htmlHostManagerServlet.messageLabel");
        if (message == null || message.length() == 0) {
            args2[1] = "OK";
        } else {
            args2[1] = Escape.htmlElementContent(message);
        }
        writer.print(MessageFormat.format(Constants.MESSAGE_SECTION, args2));
        writer.print(MessageFormat.format(Constants.MANAGER_SECTION, smClient.getString("htmlHostManagerServlet.manager"), response.encodeURL(getServletContext().getContextPath() + "/html/list"), smClient.getString("htmlHostManagerServlet.list"), getServletContext().getContextPath() + "/" + smClient.getString("htmlHostManagerServlet.helpHtmlManagerFile"), smClient.getString("htmlHostManagerServlet.helpHtmlManager"), getServletContext().getContextPath() + "/" + smClient.getString("htmlHostManagerServlet.helpManagerFile"), smClient.getString("htmlHostManagerServlet.helpManager"), response.encodeURL("/manager/status"), smClient.getString("statusServlet.title")));
        writer.print(MessageFormat.format(HOSTS_HEADER_SECTION, smClient.getString("htmlHostManagerServlet.hostName"), smClient.getString("htmlHostManagerServlet.hostAliases"), smClient.getString("htmlHostManagerServlet.hostTasks")));
        Container[] children = this.engine.findChildren();
        String[] hostNames = new String[children.length];
        for (int i = 0; i < children.length; i++) {
            hostNames[i] = children[i].getName();
        }
        SortedSet<String> sortedHostNames = new TreeSet<>();
        sortedHostNames.addAll(Arrays.asList(hostNames));
        String hostsStart = smClient.getString("htmlHostManagerServlet.hostsStart");
        String hostsStop = smClient.getString("htmlHostManagerServlet.hostsStop");
        String hostsRemove = smClient.getString("htmlHostManagerServlet.hostsRemove");
        String hostThis = smClient.getString("htmlHostManagerServlet.hostThis");
        for (String hostName : sortedHostNames) {
            Host host = (Host) this.engine.findChild(hostName);
            if (host != null) {
                Object[] args3 = new Object[2];
                args3[0] = Escape.htmlElementContent(hostName);
                String[] aliases = host.findAliases();
                StringBuilder buf = new StringBuilder();
                if (aliases.length > 0) {
                    buf.append(aliases[0]);
                    for (int j = 1; j < aliases.length; j++) {
                        buf.append(", ").append(aliases[j]);
                    }
                }
                if (buf.length() == 0) {
                    buf.append("&nbsp;");
                    args3[1] = buf.toString();
                } else {
                    args3[1] = Escape.htmlElementContent(buf.toString());
                }
                writer.print(MessageFormat.format(HOSTS_ROW_DETAILS_SECTION, args3));
                Object[] args4 = new Object[5];
                if (host.getState().isAvailable()) {
                    args4[0] = response.encodeURL(getServletContext().getContextPath() + "/html/stop?name=" + URLEncoder.encode(hostName, "UTF-8"));
                    args4[1] = hostsStop;
                } else {
                    args4[0] = response.encodeURL(getServletContext().getContextPath() + "/html/start?name=" + URLEncoder.encode(hostName, "UTF-8"));
                    args4[1] = hostsStart;
                }
                args4[2] = response.encodeURL(getServletContext().getContextPath() + "/html/remove?name=" + URLEncoder.encode(hostName, "UTF-8"));
                args4[3] = hostsRemove;
                args4[4] = hostThis;
                if (host == this.installedHost) {
                    writer.print(MessageFormat.format(MANAGER_HOST_ROW_BUTTON_SECTION, args4));
                } else {
                    writer.print(MessageFormat.format(HOSTS_ROW_BUTTON_SECTION, args4));
                }
            }
        }
        writer.print(MessageFormat.format(ADD_SECTION_START, smClient.getString("htmlHostManagerServlet.addTitle"), smClient.getString("htmlHostManagerServlet.addHost"), response.encodeURL(getServletContext().getContextPath() + "/html/add"), smClient.getString("htmlHostManagerServlet.addName"), smClient.getString("htmlHostManagerServlet.addAliases"), smClient.getString("htmlHostManagerServlet.addAppBase")));
        Object[] args5 = {smClient.getString("htmlHostManagerServlet.addAutoDeploy"), "autoDeploy", "checked"};
        writer.print(MessageFormat.format(ADD_SECTION_BOOLEAN, args5));
        args5[0] = smClient.getString("htmlHostManagerServlet.addDeployOnStartup");
        args5[1] = "deployOnStartup";
        args5[2] = "checked";
        writer.print(MessageFormat.format(ADD_SECTION_BOOLEAN, args5));
        args5[0] = smClient.getString("htmlHostManagerServlet.addDeployXML");
        args5[1] = "deployXML";
        args5[2] = "checked";
        writer.print(MessageFormat.format(ADD_SECTION_BOOLEAN, args5));
        args5[0] = smClient.getString("htmlHostManagerServlet.addUnpackWARs");
        args5[1] = "unpackWARs";
        args5[2] = "checked";
        writer.print(MessageFormat.format(ADD_SECTION_BOOLEAN, args5));
        args5[0] = smClient.getString("htmlHostManagerServlet.addManager");
        args5[1] = "manager";
        args5[2] = "checked";
        writer.print(MessageFormat.format(ADD_SECTION_BOOLEAN, args5));
        args5[0] = smClient.getString("htmlHostManagerServlet.addCopyXML");
        args5[1] = "copyXML";
        args5[2] = "";
        writer.print(MessageFormat.format(ADD_SECTION_BOOLEAN, args5));
        writer.print(MessageFormat.format(ADD_SECTION_END, smClient.getString("htmlHostManagerServlet.addButton")));
        writer.print(MessageFormat.format(PERSIST_SECTION, smClient.getString("htmlHostManagerServlet.persistTitle"), response.encodeURL(getServletContext().getContextPath() + "/html/persist"), smClient.getString("htmlHostManagerServlet.persistAllButton"), smClient.getString("htmlHostManagerServlet.persistAll")));
        writer.print(MessageFormat.format(Constants.SERVER_HEADER_SECTION, smClient.getString("htmlHostManagerServlet.serverTitle"), smClient.getString("htmlHostManagerServlet.serverVersion"), smClient.getString("htmlHostManagerServlet.serverJVMVersion"), smClient.getString("htmlHostManagerServlet.serverJVMVendor"), smClient.getString("htmlHostManagerServlet.serverOSName"), smClient.getString("htmlHostManagerServlet.serverOSVersion"), smClient.getString("htmlHostManagerServlet.serverOSArch")));
        writer.print(MessageFormat.format(Constants.SERVER_ROW_SECTION, ServerInfo.getServerInfo(), System.getProperty("java.runtime.version"), System.getProperty("java.vm.vendor"), System.getProperty("os.name"), System.getProperty("os.version"), System.getProperty("os.arch")));
        writer.print(Constants.HTML_TAIL_SECTION);
        writer.flush();
        writer.close();
    }
}
