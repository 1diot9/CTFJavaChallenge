package ch.qos.logback.core.pattern.util;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/pattern/util/AlmostAsIsEscapeUtil.class */
public class AlmostAsIsEscapeUtil extends RestrictedEscapeUtil {
    @Override // ch.qos.logback.core.pattern.util.RestrictedEscapeUtil, ch.qos.logback.core.pattern.util.IEscapeUtil
    public void escape(String escapeChars, StringBuffer buf, char next, int pointer) {
        super.escape("%)", buf, next, pointer);
    }
}
