package ch.qos.logback.core.joran.spi;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.spi.ContextAwareImpl;
import org.xml.sax.Locator;

/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: SaxEventInterpreter.java */
/* loaded from: server.jar:BOOT-INF/lib/logback-core-1.4.14.jar:ch/qos/logback/core/joran/spi/CAI_WithLocatorSupport.class */
public class CAI_WithLocatorSupport extends ContextAwareImpl {
    /* JADX INFO: Access modifiers changed from: package-private */
    public CAI_WithLocatorSupport(Context context, SaxEventInterpreter interpreter) {
        super(context, interpreter);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // ch.qos.logback.core.spi.ContextAwareImpl
    public Object getOrigin() {
        SaxEventInterpreter i = (SaxEventInterpreter) super.getOrigin();
        Locator locator = i.locator;
        if (locator != null) {
            return SaxEventInterpreter.class.getName() + "@" + locator.getLineNumber() + ":" + locator.getColumnNumber();
        }
        return SaxEventInterpreter.class.getName() + "@NA:NA";
    }
}
