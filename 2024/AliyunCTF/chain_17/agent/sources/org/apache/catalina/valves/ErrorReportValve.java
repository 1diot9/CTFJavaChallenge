package org.apache.catalina.valves;

import cn.hutool.core.util.CharsetUtil;
import jakarta.servlet.ServletException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.util.ErrorPageSupport;
import org.apache.catalina.util.IOTools;
import org.apache.catalina.util.ServerInfo;
import org.apache.catalina.util.TomcatCSS;
import org.apache.coyote.ActionCode;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.descriptor.web.ErrorPage;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.util.security.Escape;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/ErrorReportValve.class */
public class ErrorReportValve extends ValveBase {
    private boolean showReport;
    private boolean showServerInfo;
    private final ErrorPageSupport errorPageSupport;

    public ErrorReportValve() {
        super(true);
        this.showReport = true;
        this.showServerInfo = true;
        this.errorPageSupport = new ErrorPageSupport();
    }

    @Override // org.apache.catalina.Valve
    public void invoke(Request request, Response response) throws IOException, ServletException {
        getNext().invoke(request, response);
        if (response.isCommitted()) {
            if (response.setErrorReported()) {
                AtomicBoolean ioAllowed = new AtomicBoolean(true);
                response.getCoyoteResponse().action(ActionCode.IS_IO_ALLOWED, ioAllowed);
                if (ioAllowed.get()) {
                    try {
                        response.flushBuffer();
                    } catch (Throwable t) {
                        ExceptionUtils.handleThrowable(t);
                    }
                    response.getCoyoteResponse().action(ActionCode.CLOSE_NOW, request.getAttribute("jakarta.servlet.error.exception"));
                    return;
                }
                return;
            }
            return;
        }
        Throwable throwable = (Throwable) request.getAttribute("jakarta.servlet.error.exception");
        if (request.isAsync() && !request.isAsyncCompleting()) {
            return;
        }
        if (throwable != null && !response.isError()) {
            response.reset();
            response.sendError(500);
        }
        response.setSuspended(false);
        try {
            report(request, response, throwable);
        } catch (Throwable tt) {
            ExceptionUtils.handleThrowable(tt);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ErrorPage findErrorPage(int statusCode, Throwable throwable) {
        ErrorPage errorPage = null;
        if (throwable != null) {
            errorPage = this.errorPageSupport.find(throwable);
        }
        if (errorPage == null) {
            errorPage = this.errorPageSupport.find(statusCode);
        }
        if (errorPage == null) {
            errorPage = this.errorPageSupport.find(0);
        }
        return errorPage;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void report(Request request, Response response, Throwable throwable) {
        String exceptionMessage;
        int statusCode = response.getStatus();
        if (statusCode < 400 || response.getContentWritten() > 0 || !response.setErrorReported()) {
            return;
        }
        AtomicBoolean result = new AtomicBoolean(false);
        response.getCoyoteResponse().action(ActionCode.IS_IO_ALLOWED, result);
        if (!result.get()) {
            return;
        }
        ErrorPage errorPage = findErrorPage(statusCode, throwable);
        if (errorPage != null && sendErrorPage(errorPage.getLocation(), response)) {
            return;
        }
        String message = Escape.htmlElementContent(response.getMessage());
        if (message == null) {
            if (throwable != null && (exceptionMessage = throwable.getMessage()) != null && exceptionMessage.length() > 0) {
                Scanner scanner = new Scanner(exceptionMessage);
                try {
                    message = Escape.htmlElementContent(scanner.nextLine());
                    scanner.close();
                } catch (Throwable th) {
                    try {
                        scanner.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                    throw th;
                }
            }
            if (message == null) {
                message = "";
            }
        }
        String reason = null;
        String description = null;
        StringManager smClient = StringManager.getManager(Constants.Package, request.getLocales());
        response.setLocale(smClient.getLocale());
        try {
            reason = smClient.getString("http." + statusCode + ".reason");
            description = smClient.getString("http." + statusCode + ".desc");
        } catch (Throwable t) {
            ExceptionUtils.handleThrowable(t);
        }
        if (reason == null || description == null) {
            if (message.isEmpty()) {
                return;
            }
            reason = smClient.getString("errorReportValve.unknownReason");
            description = smClient.getString("errorReportValve.noDescription");
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<!doctype html><html lang=\"");
        sb.append(smClient.getLocale().getLanguage()).append("\">");
        sb.append("<head>");
        sb.append("<title>");
        sb.append(smClient.getString("errorReportValve.statusHeader", String.valueOf(statusCode), reason));
        sb.append("</title>");
        sb.append("<style type=\"text/css\">");
        sb.append(TomcatCSS.TOMCAT_CSS);
        sb.append("</style>");
        sb.append("</head><body>");
        sb.append("<h1>");
        sb.append(smClient.getString("errorReportValve.statusHeader", String.valueOf(statusCode), reason)).append("</h1>");
        if (isShowReport()) {
            sb.append("<hr class=\"line\" />");
            sb.append("<p><b>");
            sb.append(smClient.getString("errorReportValve.type"));
            sb.append("</b> ");
            if (throwable != null) {
                sb.append(smClient.getString("errorReportValve.exceptionReport"));
            } else {
                sb.append(smClient.getString("errorReportValve.statusReport"));
            }
            sb.append("</p>");
            if (!message.isEmpty()) {
                sb.append("<p><b>");
                sb.append(smClient.getString("errorReportValve.message"));
                sb.append("</b> ");
                sb.append(message).append("</p>");
            }
            sb.append("<p><b>");
            sb.append(smClient.getString("errorReportValve.description"));
            sb.append("</b> ");
            sb.append(description);
            sb.append("</p>");
            if (throwable != null) {
                String stackTrace = getPartialServletStackTrace(throwable);
                sb.append("<p><b>");
                sb.append(smClient.getString("errorReportValve.exception"));
                sb.append("</b></p><pre>");
                sb.append(Escape.htmlElementContent(stackTrace));
                sb.append("</pre>");
                Throwable rootCause = throwable.getCause();
                for (int loops = 0; rootCause != null && loops < 10; loops++) {
                    String stackTrace2 = getPartialServletStackTrace(rootCause);
                    sb.append("<p><b>");
                    sb.append(smClient.getString("errorReportValve.rootCause"));
                    sb.append("</b></p><pre>");
                    sb.append(Escape.htmlElementContent(stackTrace2));
                    sb.append("</pre>");
                    rootCause = rootCause.getCause();
                }
                sb.append("<p><b>");
                sb.append(smClient.getString("errorReportValve.note"));
                sb.append("</b> ");
                sb.append(smClient.getString("errorReportValve.rootCauseInLogs"));
                sb.append("</p>");
            }
            sb.append("<hr class=\"line\" />");
        }
        if (isShowServerInfo()) {
            sb.append("<h3>").append(ServerInfo.getServerInfo()).append("</h3>");
        }
        sb.append("</body></html>");
        try {
            try {
                response.setContentType("text/html");
                response.setCharacterEncoding("utf-8");
            } catch (IOException | IllegalStateException e) {
                return;
            }
        } catch (Throwable t2) {
            ExceptionUtils.handleThrowable(t2);
            if (this.container.getLogger().isDebugEnabled()) {
                this.container.getLogger().debug("Failure to set the content-type of response", t2);
            }
        }
        Writer writer = response.getReporter();
        if (writer != null) {
            writer.write(sb.toString());
            response.finishResponse();
        }
    }

    protected String getPartialServletStackTrace(Throwable t) {
        StringBuilder trace = new StringBuilder();
        trace.append(t.toString()).append(System.lineSeparator());
        StackTraceElement[] elements = t.getStackTrace();
        int pos = elements.length;
        int i = elements.length - 1;
        while (true) {
            if (i < 0) {
                break;
            }
            if (elements[i].getClassName().startsWith("org.apache.catalina.core.ApplicationFilterChain") && elements[i].getMethodName().equals("internalDoFilter")) {
                pos = i;
                break;
            }
            i--;
        }
        for (int i2 = 0; i2 < pos; i2++) {
            if (!elements[i2].getClassName().startsWith("org.apache.catalina.core.")) {
                trace.append('\t').append(elements[i2].toString()).append(System.lineSeparator());
            }
        }
        return trace.toString();
    }

    private boolean sendErrorPage(String location, Response response) {
        File file = new File(location);
        if (!file.isAbsolute()) {
            file = new File(getContainer().getCatalinaBase(), location);
        }
        if (!file.isFile() || !file.canRead()) {
            getContainer().getLogger().warn(sm.getString("errorReportValve.errorPageNotFound", location));
            return false;
        }
        response.setContentType("text/html");
        response.setCharacterEncoding(CharsetUtil.UTF_8);
        try {
            OutputStream os = response.getOutputStream();
            try {
                InputStream is = new FileInputStream(file);
                try {
                    IOTools.flow(is, os);
                    is.close();
                    if (os != null) {
                        os.close();
                    }
                    return true;
                } catch (Throwable th) {
                    try {
                        is.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                    throw th;
                }
            } finally {
            }
        } catch (IOException e) {
            getContainer().getLogger().warn(sm.getString("errorReportValve.errorPageIOException", location), e);
            return false;
        }
    }

    public void setShowReport(boolean showReport) {
        this.showReport = showReport;
    }

    public boolean isShowReport() {
        return this.showReport;
    }

    public void setShowServerInfo(boolean showServerInfo) {
        this.showServerInfo = showServerInfo;
    }

    public boolean isShowServerInfo() {
        return this.showServerInfo;
    }

    public boolean setProperty(String name, String value) {
        if (name.startsWith("errorCode.")) {
            int code = Integer.parseInt(name.substring(10));
            ErrorPage ep = new ErrorPage();
            ep.setErrorCode(code);
            ep.setLocation(value);
            this.errorPageSupport.add(ep);
            return true;
        }
        if (name.startsWith("exceptionType.")) {
            String className = name.substring(14);
            ErrorPage ep2 = new ErrorPage();
            ep2.setExceptionType(className);
            ep2.setLocation(value);
            this.errorPageSupport.add(ep2);
            return true;
        }
        return false;
    }

    public String getProperty(String name) {
        String result;
        if (name.startsWith("errorCode.")) {
            int code = Integer.parseInt(name.substring(10));
            ErrorPage ep = this.errorPageSupport.find(code);
            if (ep == null) {
                result = null;
            } else {
                result = ep.getLocation();
            }
        } else if (name.startsWith("exceptionType.")) {
            String className = name.substring(14);
            ErrorPage ep2 = this.errorPageSupport.find(className);
            if (ep2 == null) {
                result = null;
            } else {
                result = ep2.getLocation();
            }
        } else {
            result = null;
        }
        return result;
    }
}
