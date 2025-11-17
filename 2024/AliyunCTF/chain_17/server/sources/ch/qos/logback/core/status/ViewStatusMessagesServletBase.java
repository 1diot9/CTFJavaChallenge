package ch.qos.logback.core.status;

import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.helpers.Transform;
import ch.qos.logback.core.util.CachingDateFormatter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/status/ViewStatusMessagesServletBase.class */
public abstract class ViewStatusMessagesServletBase extends HttpServlet {
    private static final long serialVersionUID = -3551928133801157219L;
    private static CachingDateFormatter SDF = new CachingDateFormatter("yyyy-MM-dd HH:mm:ss");
    static String SUBMIT = "submit";
    static String CLEAR = "Clear";
    int count;

    protected abstract StatusManager getStatusManager(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse);

    protected abstract String getPageTitle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse);

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // jakarta.servlet.http.HttpServlet
    public void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.count = 0;
        StatusManager sm = getStatusManager(req, resp);
        resp.setContentType("text/html");
        PrintWriter output = resp.getWriter();
        output.append("<html>\r\n");
        output.append("<head>\r\n");
        printCSS(req.getContextPath(), output);
        output.append("</head>\r\n");
        output.append("<body>\r\n");
        output.append(getPageTitle(req, resp));
        output.append("<form method=\"POST\">\r\n");
        output.append("<input type=\"submit\" name=\"" + SUBMIT + "\" value=\"" + CLEAR + "\">");
        output.append("</form>\r\n");
        if (CLEAR.equalsIgnoreCase(req.getParameter(SUBMIT))) {
            sm.clear();
            sm.add(new InfoStatus("Cleared all status messages", this));
        }
        output.append("<table>");
        StringBuilder buf = new StringBuilder();
        if (sm != null) {
            printList(buf, sm);
        } else {
            output.append("Could not find status manager");
        }
        output.append((CharSequence) buf);
        output.append("</table>");
        output.append("</body>\r\n");
        output.append("</html>\r\n");
        output.flush();
        output.close();
    }

    public void printCSS(String localRef, PrintWriter output) {
        output.append("  <STYLE TYPE=\"text/css\">\r\n");
        output.append("    .warn  { font-weight: bold; color: #FF6600;} \r\n");
        output.append("    .error { font-weight: bold; color: #CC0000;} \r\n");
        output.append("    table { margin-left: 2em; margin-right: 2em; border-left: 2px solid #AAA; }\r\n");
        output.append("    tr.even { background: #FFFFFF; }\r\n");
        output.append("    tr.odd  { background: #EAEAEA; }\r\n");
        output.append("    td { padding-right: 1ex; padding-left: 1ex; border-right: 2px solid #AAA; }\r\n");
        output.append("    td.date { text-align: right; font-family: courier, monospace; font-size: smaller; }");
        output.append((CharSequence) CoreConstants.LINE_SEPARATOR);
        output.append("  td.level { text-align: right; }");
        output.append((CharSequence) CoreConstants.LINE_SEPARATOR);
        output.append("    tr.header { background: #596ED5; color: #FFF; font-weight: bold; font-size: larger; }");
        output.append((CharSequence) CoreConstants.LINE_SEPARATOR);
        output.append("  td.exception { background: #A2AEE8; white-space: pre; font-family: courier, monospace;}");
        output.append((CharSequence) CoreConstants.LINE_SEPARATOR);
        output.append("  </STYLE>\r\n");
    }

    public void printList(StringBuilder buf, StatusManager sm) {
        buf.append("<table>\r\n");
        printHeader(buf);
        List<Status> statusList = sm.getCopyOfStatusList();
        for (Status s : statusList) {
            this.count++;
            printStatus(buf, s);
        }
        buf.append("</table>\r\n");
    }

    public void printHeader(StringBuilder buf) {
        buf.append("  <tr class=\"header\">\r\n");
        buf.append("    <th>Date </th>\r\n");
        buf.append("    <th>Level</th>\r\n");
        buf.append("    <th>Origin</th>\r\n");
        buf.append("    <th>Message</th>\r\n");
        buf.append("  </tr>\r\n");
    }

    String statusLevelAsString(Status s) {
        switch (s.getEffectiveLevel()) {
            case 0:
                return "INFO";
            case 1:
                return "<span class=\"warn\">WARN</span>";
            case 2:
                return "<span class=\"error\">ERROR</span>";
            default:
                return null;
        }
    }

    String abbreviatedOrigin(Status s) {
        Object o = s.getOrigin();
        if (o == null) {
            return null;
        }
        String fqClassName = o.getClass().getName();
        int lastIndex = fqClassName.lastIndexOf(46);
        if (lastIndex != -1) {
            return fqClassName.substring(lastIndex + 1, fqClassName.length());
        }
        return fqClassName;
    }

    private void printStatus(StringBuilder buf, Status s) {
        String trClass;
        if (this.count % 2 == 0) {
            trClass = "even";
        } else {
            trClass = "odd";
        }
        buf.append("  <tr class=\"").append(trClass).append("\">\r\n");
        String dateStr = SDF.format(s.getTimestamp());
        buf.append("    <td class=\"date\">").append(dateStr).append("</td>\r\n");
        buf.append("    <td class=\"level\">").append(statusLevelAsString(s)).append("</td>\r\n");
        buf.append("    <td>").append(abbreviatedOrigin(s)).append("</td>\r\n");
        buf.append("    <td>").append(s.getMessage()).append("</td>\r\n");
        buf.append("  </tr>\r\n");
        if (s.getThrowable() != null) {
            printThrowable(buf, s.getThrowable());
        }
    }

    private void printThrowable(StringBuilder buf, Throwable t) {
        buf.append("  <tr>\r\n");
        buf.append("    <td colspan=\"4\" class=\"exception\"><pre>");
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        buf.append(Transform.escapeTags(sw.getBuffer()));
        buf.append("    </pre></td>\r\n");
        buf.append("  </tr>\r\n");
    }
}
