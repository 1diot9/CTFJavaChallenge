package org.apache.catalina.valves;

import java.io.IOException;
import java.io.Writer;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.coyote.ActionCode;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.json.JSONFilter;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/catalina/valves/JsonErrorReportValve.class */
public class JsonErrorReportValve extends ErrorReportValve {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.valves.ErrorReportValve
    public void report(Request request, Response response, Throwable throwable) {
        String type;
        int statusCode = response.getStatus();
        if (statusCode < 400 || response.getContentWritten() > 0 || !response.setErrorReported()) {
            return;
        }
        AtomicBoolean result = new AtomicBoolean(false);
        response.getCoyoteResponse().action(ActionCode.IS_IO_ALLOWED, result);
        if (!result.get()) {
            return;
        }
        StringManager smClient = StringManager.getManager(Constants.Package, request.getLocales());
        response.setLocale(smClient.getLocale());
        if (throwable != null) {
            type = smClient.getString("errorReportValve.exceptionReport");
        } else {
            type = smClient.getString("errorReportValve.statusReport");
        }
        String message = response.getMessage();
        if (message == null && throwable != null) {
            message = throwable.getMessage();
        }
        String description = smClient.getString("http." + statusCode + ".desc");
        if (description == null) {
            if (message == null || message.isEmpty()) {
                return;
            } else {
                description = smClient.getString("errorReportValve.noDescription");
            }
        }
        String jsonReport = "{\n  \"type\": \"" + JSONFilter.escape(type) + "\",\n  \"message\": \"" + JSONFilter.escape(message) + "\",\n  \"description\": \"" + JSONFilter.escape(description) + "\"\n}";
        try {
            try {
                response.setContentType("application/json");
                response.setCharacterEncoding("utf-8");
            } catch (Throwable t) {
                ExceptionUtils.handleThrowable(t);
                if (this.container.getLogger().isDebugEnabled()) {
                    this.container.getLogger().debug("Failure to set the content-type of response", t);
                }
            }
            Writer writer = response.getReporter();
            if (writer != null) {
                writer.write(jsonReport);
                response.finishResponse();
            }
        } catch (IOException | IllegalStateException e) {
        }
    }
}
