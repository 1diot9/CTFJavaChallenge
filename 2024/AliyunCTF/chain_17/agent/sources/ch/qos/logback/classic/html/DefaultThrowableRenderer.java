package ch.qos.logback.classic.html;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.helpers.Transform;
import ch.qos.logback.core.html.IThrowableRenderer;

/* loaded from: agent.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/html/DefaultThrowableRenderer.class */
public class DefaultThrowableRenderer implements IThrowableRenderer<ILoggingEvent> {
    static final String TRACE_PREFIX = "<br />&nbsp;&nbsp;&nbsp;&nbsp;";

    @Override // ch.qos.logback.core.html.IThrowableRenderer
    public void render(StringBuilder sbuf, ILoggingEvent event) {
        sbuf.append("<tr><td class=\"Exception\" colspan=\"6\">");
        for (IThrowableProxy tp = event.getThrowableProxy(); tp != null; tp = tp.getCause()) {
            render(sbuf, tp);
        }
        sbuf.append("</td></tr>");
    }

    void render(StringBuilder sbuf, IThrowableProxy tp) {
        printFirstLine(sbuf, tp);
        int commonFrames = tp.getCommonFrames();
        StackTraceElementProxy[] stepArray = tp.getStackTraceElementProxyArray();
        for (int i = 0; i < stepArray.length - commonFrames; i++) {
            StackTraceElementProxy step = stepArray[i];
            sbuf.append(TRACE_PREFIX);
            sbuf.append(Transform.escapeTags(step.toString()));
            sbuf.append(CoreConstants.LINE_SEPARATOR);
        }
        if (commonFrames > 0) {
            sbuf.append(TRACE_PREFIX);
            sbuf.append("\t... ").append(commonFrames).append(" common frames omitted").append(CoreConstants.LINE_SEPARATOR);
        }
    }

    public void printFirstLine(StringBuilder sb, IThrowableProxy tp) {
        int commonFrames = tp.getCommonFrames();
        if (commonFrames > 0) {
            sb.append("<br />").append(CoreConstants.CAUSED_BY);
        }
        sb.append(tp.getClassName()).append(": ").append(Transform.escapeTags(tp.getMessage()));
        sb.append(CoreConstants.LINE_SEPARATOR);
    }
}
