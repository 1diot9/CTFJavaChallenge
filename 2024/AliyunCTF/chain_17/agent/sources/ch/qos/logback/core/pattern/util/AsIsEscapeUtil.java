package ch.qos.logback.core.pattern.util;

import cn.hutool.core.text.StrPool;

/* loaded from: agent.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/pattern/util/AsIsEscapeUtil.class */
public class AsIsEscapeUtil implements IEscapeUtil {
    @Override // ch.qos.logback.core.pattern.util.IEscapeUtil
    public void escape(String escapeChars, StringBuffer buf, char next, int pointer) {
        buf.append(StrPool.BACKSLASH);
        buf.append(next);
    }
}
