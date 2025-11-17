package ch.qos.logback.classic.pattern;

import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.ThrowableProxyUtil;
import ch.qos.logback.core.CoreConstants;

/* loaded from: server.jar:BOOT-INF/lib/logback-classic-1.4.14.jar:ch/qos/logback/classic/pattern/RootCauseFirstThrowableProxyConverter.class */
public class RootCauseFirstThrowableProxyConverter extends ExtendedThrowableProxyConverter {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // ch.qos.logback.classic.pattern.ThrowableProxyConverter
    public String throwableProxyToString(IThrowableProxy tp) {
        StringBuilder buf = new StringBuilder(2048);
        recursiveAppendRootCauseFirst(buf, null, 1, tp);
        return buf.toString();
    }

    protected void recursiveAppendRootCauseFirst(StringBuilder sb, String prefix, int indent, IThrowableProxy tp) {
        if (tp.getCause() != null) {
            recursiveAppendRootCauseFirst(sb, prefix, indent, tp.getCause());
            prefix = null;
        }
        ThrowableProxyUtil.indent(sb, indent - 1);
        if (prefix != null) {
            sb.append(prefix);
        }
        ThrowableProxyUtil.subjoinFirstLineRootCauseFirst(sb, tp);
        sb.append(CoreConstants.LINE_SEPARATOR);
        subjoinSTEPArray(sb, indent, tp);
        IThrowableProxy[] suppressed = tp.getSuppressed();
        if (suppressed != null) {
            for (IThrowableProxy current : suppressed) {
                recursiveAppendRootCauseFirst(sb, CoreConstants.SUPPRESSED, indent + 1, current);
            }
        }
    }
}
