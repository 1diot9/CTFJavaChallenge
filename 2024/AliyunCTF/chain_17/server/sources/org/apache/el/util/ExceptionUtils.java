package org.apache.el.util;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-el-10.1.18.jar:org/apache/el/util/ExceptionUtils.class */
public class ExceptionUtils {
    public static void handleThrowable(Throwable t) {
        if (t instanceof ThreadDeath) {
            throw ((ThreadDeath) t);
        }
        if (!(t instanceof StackOverflowError) && (t instanceof VirtualMachineError)) {
            throw ((VirtualMachineError) t);
        }
    }

    public static void preload() {
    }
}
