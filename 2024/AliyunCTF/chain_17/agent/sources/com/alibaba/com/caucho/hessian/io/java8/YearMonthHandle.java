package com.alibaba.com.caucho.hessian.io.java8;

import com.alibaba.com.caucho.hessian.io.HessianHandle;
import java.io.Serializable;
import java.lang.reflect.Method;

/* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/java8/YearMonthHandle.class */
public class YearMonthHandle implements HessianHandle, Serializable {
    private static final long serialVersionUID = -4150786187896925314L;
    private int year;
    private int month;

    public YearMonthHandle() {
    }

    public YearMonthHandle(Object o) {
        try {
            Class c = Class.forName("java.time.YearMonth");
            Method m = c.getDeclaredMethod("getYear", new Class[0]);
            this.year = ((Integer) m.invoke(o, new Object[0])).intValue();
            Method m2 = c.getDeclaredMethod("getMonthValue", new Class[0]);
            this.month = ((Integer) m2.invoke(o, new Object[0])).intValue();
        } catch (Throwable th) {
        }
    }

    private Object readResolve() {
        try {
            Class c = Class.forName("java.time.YearMonth");
            Method m = c.getDeclaredMethod("of", Integer.TYPE, Integer.TYPE);
            return m.invoke(null, Integer.valueOf(this.year), Integer.valueOf(this.month));
        } catch (Throwable th) {
            return null;
        }
    }
}
