package com.alibaba.com.caucho.hessian.io.java8;

import com.alibaba.com.caucho.hessian.io.HessianHandle;
import java.io.Serializable;
import java.lang.reflect.Method;

/* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/java8/LocalDateTimeHandle.class */
public class LocalDateTimeHandle implements HessianHandle, Serializable {
    private static final long serialVersionUID = 7563825215275989361L;
    private Object date;
    private Object time;

    public LocalDateTimeHandle() {
    }

    public LocalDateTimeHandle(Object o) {
        try {
            Class c = Class.forName("java.time.LocalDateTime");
            Method m = c.getDeclaredMethod("toLocalDate", new Class[0]);
            this.date = m.invoke(o, new Object[0]);
            Method m2 = c.getDeclaredMethod("toLocalTime", new Class[0]);
            this.time = m2.invoke(o, new Object[0]);
        } catch (Throwable th) {
        }
    }

    private Object readResolve() {
        try {
            Class c = Class.forName("java.time.LocalDateTime");
            Method m = c.getDeclaredMethod("of", Class.forName("java.time.LocalDate"), Class.forName("java.time.LocalTime"));
            return m.invoke(null, this.date, this.time);
        } catch (Throwable th) {
            return null;
        }
    }
}
