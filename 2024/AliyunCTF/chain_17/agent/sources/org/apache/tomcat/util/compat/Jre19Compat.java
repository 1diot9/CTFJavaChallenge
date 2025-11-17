package org.apache.tomcat.util.compat;

import java.lang.reflect.Field;
import java.net.SocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/compat/Jre19Compat.class */
public class Jre19Compat extends Jre16Compat {
    private static final Log log = LogFactory.getLog((Class<?>) Jre19Compat.class);
    private static final StringManager sm = StringManager.getManager((Class<?>) Jre19Compat.class);
    private static final boolean supported;

    @Override // org.apache.tomcat.util.compat.Jre16Compat, org.apache.tomcat.util.compat.JreCompat
    public /* bridge */ /* synthetic */ SocketChannel openUnixDomainSocketChannel() {
        return super.openUnixDomainSocketChannel();
    }

    @Override // org.apache.tomcat.util.compat.Jre16Compat, org.apache.tomcat.util.compat.JreCompat
    public /* bridge */ /* synthetic */ ServerSocketChannel openUnixDomainServerSocketChannel() {
        return super.openUnixDomainServerSocketChannel();
    }

    @Override // org.apache.tomcat.util.compat.Jre16Compat, org.apache.tomcat.util.compat.JreCompat
    public /* bridge */ /* synthetic */ SocketAddress getUnixDomainSocketAddress(String str) {
        return super.getUnixDomainSocketAddress(str);
    }

    static {
        Class<?> c1 = null;
        try {
            c1 = Class.forName("java.lang.WrongThreadException");
        } catch (ClassNotFoundException cnfe) {
            log.debug(sm.getString("jre19Compat.javaPre19"), cnfe);
        }
        supported = c1 != null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isSupported() {
        return supported;
    }

    @Override // org.apache.tomcat.util.compat.JreCompat
    public Object getExecutor(Thread thread) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        Object result = super.getExecutor(thread);
        if (result == null) {
            try {
                Field holderField = thread.getClass().getDeclaredField("holder");
                holderField.setAccessible(true);
                Object holder = holderField.get(thread);
                Field taskField = holder.getClass().getDeclaredField("task");
                taskField.setAccessible(true);
                Object task = taskField.get(holder);
                if (task != null && task.getClass().getCanonicalName() != null && (task.getClass().getCanonicalName().equals("org.apache.tomcat.util.threads.ThreadPoolExecutor.Worker") || task.getClass().getCanonicalName().equals("java.util.concurrent.ThreadPoolExecutor.Worker"))) {
                    Field executorField = task.getClass().getDeclaredField("this$0");
                    executorField.setAccessible(true);
                    result = executorField.get(task);
                }
            } catch (NoSuchFieldException e) {
                return null;
            }
        }
        return result;
    }
}
