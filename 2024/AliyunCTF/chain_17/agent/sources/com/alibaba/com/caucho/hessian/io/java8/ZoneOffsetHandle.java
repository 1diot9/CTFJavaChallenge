package com.alibaba.com.caucho.hessian.io.java8;

import com.alibaba.com.caucho.hessian.io.HessianHandle;
import java.io.Serializable;
import java.lang.reflect.Method;

/* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/java8/ZoneOffsetHandle.class */
public class ZoneOffsetHandle implements HessianHandle, Serializable {
    private static final long serialVersionUID = 8841589723587858789L;
    private int seconds;

    public ZoneOffsetHandle() {
    }

    public ZoneOffsetHandle(Object o) {
        try {
            Class c = Class.forName("java.time.ZoneOffset");
            Method m = c.getDeclaredMethod("getTotalSeconds", new Class[0]);
            this.seconds = ((Integer) m.invoke(o, new Object[0])).intValue();
        } catch (Throwable th) {
        }
    }

    private Object readResolve() {
        try {
            Class c = Class.forName("java.time.ZoneOffset");
            Method m = c.getDeclaredMethod("ofTotalSeconds", Integer.TYPE);
            return m.invoke(null, Integer.valueOf(this.seconds));
        } catch (Throwable th) {
            return null;
        }
    }
}
