package com.alibaba.com.caucho.hessian.io.java8;

import com.alibaba.com.caucho.hessian.io.HessianHandle;
import java.io.Serializable;
import java.lang.reflect.Method;

/* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/java8/LocalTimeHandle.class */
public class LocalTimeHandle implements HessianHandle, Serializable {
    private static final long serialVersionUID = -5892919085390462315L;
    private int hour;
    private int minute;
    private int second;
    private int nano;

    public LocalTimeHandle() {
    }

    public LocalTimeHandle(Object o) {
        try {
            Class c = Class.forName("java.time.LocalTime");
            Method m = c.getDeclaredMethod("getHour", new Class[0]);
            this.hour = ((Integer) m.invoke(o, new Object[0])).intValue();
            Method m2 = c.getDeclaredMethod("getMinute", new Class[0]);
            this.minute = ((Integer) m2.invoke(o, new Object[0])).intValue();
            Method m3 = c.getDeclaredMethod("getSecond", new Class[0]);
            this.second = ((Integer) m3.invoke(o, new Object[0])).intValue();
            Method m4 = c.getDeclaredMethod("getNano", new Class[0]);
            this.nano = ((Integer) m4.invoke(o, new Object[0])).intValue();
        } catch (Throwable th) {
        }
    }

    private Object readResolve() {
        try {
            Class c = Class.forName("java.time.LocalTime");
            Method m = c.getDeclaredMethod("of", Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE);
            return m.invoke(null, Integer.valueOf(this.hour), Integer.valueOf(this.minute), Integer.valueOf(this.second), Integer.valueOf(this.nano));
        } catch (Throwable th) {
            return null;
        }
    }
}
