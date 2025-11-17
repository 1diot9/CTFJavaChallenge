package org.apache.tomcat.util.compat;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.apache.catalina.Lifecycle;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/compat/Jre21Compat.class */
public class Jre21Compat extends Jre19Compat {
    private static final Log log = LogFactory.getLog((Class<?>) Jre21Compat.class);
    private static final StringManager sm = StringManager.getManager((Class<?>) Jre21Compat.class);
    private static final Method nameMethod;
    private static final Method startMethod;
    private static final Method ofVirtualMethod;

    static {
        Method m1 = null;
        Method m2 = null;
        Method m3 = null;
        try {
            Class<?> c1 = Class.forName("java.lang.Thread$Builder");
            m1 = c1.getMethod("name", String.class, Long.TYPE);
            m2 = c1.getMethod(Lifecycle.START_EVENT, Runnable.class);
            m3 = Thread.class.getMethod("ofVirtual", (Class[]) null);
        } catch (ClassNotFoundException e) {
            log.debug(sm.getString("jre21Compat.javaPre21"), e);
        } catch (ReflectiveOperationException e2) {
            log.error(sm.getString("jre21Compat.unexpected"), e2);
        }
        nameMethod = m1;
        startMethod = m2;
        ofVirtualMethod = m3;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isSupported() {
        return ofVirtualMethod != null;
    }

    @Override // org.apache.tomcat.util.compat.JreCompat
    public Object createVirtualThreadBuilder(String name) {
        try {
            Object threadBuilder = ofVirtualMethod.invoke(null, (Object[]) null);
            nameMethod.invoke(threadBuilder, name, 0L);
            return threadBuilder;
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    @Override // org.apache.tomcat.util.compat.JreCompat
    public void threadBuilderStart(Object threadBuilder, Runnable command) {
        try {
            startMethod.invoke(threadBuilder, command);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new UnsupportedOperationException(e);
        }
    }
}
