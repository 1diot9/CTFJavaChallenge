package com.alibaba.com.caucho.hessian.io.java8;

import com.alibaba.com.caucho.hessian.io.HessianHandle;
import java.io.Serializable;
import java.lang.reflect.Method;

/* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/java8/OffsetTimeHandle.class */
public class OffsetTimeHandle implements HessianHandle, Serializable {
    private static final long serialVersionUID = -3269846941421652860L;
    private Object localTime;
    private Object zoneOffset;

    public OffsetTimeHandle() {
    }

    public OffsetTimeHandle(Object o) {
        try {
            Class c = Class.forName("java.time.OffsetTime");
            Method m = c.getDeclaredMethod("getOffset", new Class[0]);
            this.zoneOffset = m.invoke(o, new Object[0]);
            Method m2 = c.getDeclaredMethod("toLocalTime", new Class[0]);
            this.localTime = m2.invoke(o, new Object[0]);
        } catch (Throwable th) {
        }
    }

    private Object readResolve() {
        try {
            Class c = Class.forName("java.time.OffsetTime");
            Method m = c.getDeclaredMethod("of", Class.forName("java.time.LocalTime"), Class.forName("java.time.ZoneOffset"));
            return m.invoke(null, this.localTime, this.zoneOffset);
        } catch (Throwable th) {
            return null;
        }
    }
}
