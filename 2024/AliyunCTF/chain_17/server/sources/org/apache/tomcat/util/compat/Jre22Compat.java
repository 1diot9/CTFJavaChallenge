package org.apache.tomcat.util.compat;

import java.lang.reflect.Method;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/compat/Jre22Compat.class */
public class Jre22Compat extends Jre21Compat {
    private static final Log log = LogFactory.getLog((Class<?>) Jre22Compat.class);
    private static final StringManager sm = StringManager.getManager((Class<?>) Jre22Compat.class);
    private static final boolean hasPanama;

    static {
        Method m1 = null;
        try {
            Class<?> c1 = Class.forName("java.lang.foreign.MemorySegment");
            Class<?> c2 = Class.forName("java.io.Console");
            m1 = c1.getMethod("getString", Long.TYPE);
            c2.getMethod("isTerminal", new Class[0]);
        } catch (ClassNotFoundException e) {
            log.debug(sm.getString("jre22Compat.javaPre22"), e);
        } catch (ReflectiveOperationException e2) {
            log.debug(sm.getString("jre22Compat.unexpected"), e2);
        }
        hasPanama = m1 != null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isSupported() {
        return hasPanama;
    }
}
