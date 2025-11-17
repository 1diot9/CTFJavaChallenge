package com.alibaba.com.caucho.hessian.io.java8;

import com.alibaba.com.caucho.hessian.io.HessianHandle;
import java.io.Serializable;
import java.lang.reflect.Method;

/* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/java8/InstantHandle.class */
public class InstantHandle implements HessianHandle, Serializable {
    private static final long serialVersionUID = -4367309317780077156L;
    private long seconds;
    private int nanos;

    public InstantHandle() {
    }

    public InstantHandle(Object o) {
        try {
            Class c = Class.forName("java.time.Instant");
            Method m = c.getDeclaredMethod("getEpochSecond", new Class[0]);
            this.seconds = ((Long) m.invoke(o, new Object[0])).longValue();
            Method m2 = c.getDeclaredMethod("getNano", new Class[0]);
            this.nanos = ((Integer) m2.invoke(o, new Object[0])).intValue();
        } catch (Throwable th) {
        }
    }

    private Object readResolve() {
        try {
            Class c = Class.forName("java.time.Instant");
            Method m = c.getDeclaredMethod("ofEpochSecond", Long.TYPE, Long.TYPE);
            return m.invoke(null, Long.valueOf(this.seconds), Integer.valueOf(this.nanos));
        } catch (Throwable th) {
            return null;
        }
    }
}
