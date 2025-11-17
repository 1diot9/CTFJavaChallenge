package org.apache.catalina.valves;

import ch.qos.logback.core.CoreConstants;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.util.IOTools;
import org.apache.coyote.ActionCode;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.descriptor.web.ErrorPage;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/ProxyErrorReportValve.class */
public class ProxyErrorReportValve extends ErrorReportValve {
    private static final Log log = LogFactory.getLog((Class<?>) ProxyErrorReportValve.class);
    protected boolean useRedirect = true;
    protected boolean usePropertiesFile = false;

    public boolean getUseRedirect() {
        return this.useRedirect;
    }

    public void setUseRedirect(boolean useRedirect) {
        this.useRedirect = useRedirect;
    }

    public boolean getUsePropertiesFile() {
        return this.usePropertiesFile;
    }

    public void setUsePropertiesFile(boolean usePropertiesFile) {
        this.usePropertiesFile = usePropertiesFile;
    }

    private String getRedirectUrl(Response response) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle(getClass().getSimpleName(), response.getLocale());
        String redirectUrl = null;
        try {
            redirectUrl = resourceBundle.getString(Integer.toString(response.getStatus()));
        } catch (MissingResourceException e) {
        }
        if (redirectUrl == null) {
            try {
                redirectUrl = resourceBundle.getString(Integer.toString(0));
            } catch (MissingResourceException e2) {
            }
        }
        return redirectUrl;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.valves.ErrorReportValve
    public void report(Request request, Response response, Throwable throwable) {
        int statusCode = response.getStatus();
        if (statusCode < 400 || response.getContentWritten() > 0) {
            return;
        }
        AtomicBoolean result = new AtomicBoolean(false);
        response.getCoyoteResponse().action(ActionCode.IS_IO_ALLOWED, result);
        if (!result.get()) {
            return;
        }
        String urlString = null;
        if (this.usePropertiesFile) {
            urlString = getRedirectUrl(response);
        } else {
            ErrorPage errorPage = findErrorPage(statusCode, throwable);
            if (errorPage != null) {
                urlString = errorPage.getLocation();
            }
        }
        if (urlString == null) {
            super.report(request, response, throwable);
            return;
        }
        if (!response.setErrorReported()) {
            return;
        }
        StringBuilder stringBuilder = new StringBuilder(urlString);
        if (urlString.indexOf(CoreConstants.NA) > -1) {
            stringBuilder.append('&');
        } else {
            stringBuilder.append('?');
        }
        stringBuilder.append("requestUri=");
        stringBuilder.append(URLEncoder.encode(request.getDecodedRequestURI(), request.getConnector().getURICharset()));
        stringBuilder.append("&statusCode=");
        stringBuilder.append(URLEncoder.encode(String.valueOf(statusCode), StandardCharsets.UTF_8));
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
            reason = smClient.getString("errorReportValve.unknownReason");
            description = smClient.getString("errorReportValve.noDescription");
        }
        stringBuilder.append("&statusDescription=");
        stringBuilder.append(URLEncoder.encode(description, StandardCharsets.UTF_8));
        stringBuilder.append("&statusReason=");
        stringBuilder.append(URLEncoder.encode(reason, StandardCharsets.UTF_8));
        String message = response.getMessage();
        if (message != null) {
            stringBuilder.append("&message=");
            stringBuilder.append(URLEncoder.encode(message, StandardCharsets.UTF_8));
        }
        if (throwable != null) {
            stringBuilder.append("&throwable=");
            stringBuilder.append(URLEncoder.encode(throwable.toString(), StandardCharsets.UTF_8));
        }
        String urlString2 = stringBuilder.toString();
        if (this.useRedirect) {
            if (log.isTraceEnabled()) {
                log.trace("Redirecting error reporting to " + urlString2);
            }
            try {
                response.sendRedirect(urlString2);
                return;
            } catch (IOException e) {
                return;
            }
        }
        if (log.isTraceEnabled()) {
            log.trace("Proxying error reporting to " + urlString2);
        }
        HttpURLConnection httpURLConnection = null;
        try {
            try {
                URL url = new URI(urlString2).toURL();
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.connect();
                response.setContentType(httpURLConnection.getContentType());
                response.setContentLength(httpURLConnection.getContentLength());
                OutputStream outputStream = response.getOutputStream();
                InputStream inputStream = url.openStream();
                IOTools.flow(inputStream, outputStream);
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            } catch (IOException | IllegalArgumentException | URISyntaxException e2) {
                if (log.isDebugEnabled()) {
                    log.debug("Proxy error to " + urlString2, e2);
                }
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }
        } catch (Throwable th) {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            throw th;
        }
    }
}
