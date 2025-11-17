package ch.qos.logback.core.layout;

import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.LayoutBase;

/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/layout/EchoLayout.class */
public class EchoLayout<E> extends LayoutBase<E> {
    @Override // ch.qos.logback.core.Layout
    public String doLayout(E event) {
        return String.valueOf(event) + CoreConstants.LINE_SEPARATOR;
    }
}
