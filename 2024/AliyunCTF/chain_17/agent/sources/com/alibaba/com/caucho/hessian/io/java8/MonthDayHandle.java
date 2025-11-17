package com.alibaba.com.caucho.hessian.io.java8;

import com.alibaba.com.caucho.hessian.io.HessianHandle;
import java.io.Serializable;
import java.lang.reflect.Method;

/* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/java8/MonthDayHandle.class */
public class MonthDayHandle implements HessianHandle, Serializable {
    private static final long serialVersionUID = 5288238558666577745L;
    private int month;
    private int day;

    public MonthDayHandle() {
    }

    public MonthDayHandle(Object o) {
        try {
            Class c = Class.forName("java.time.MonthDay");
            Method m = c.getDeclaredMethod("getMonthValue", new Class[0]);
            this.month = ((Integer) m.invoke(o, new Object[0])).intValue();
            Method m2 = c.getDeclaredMethod("getDayOfMonth", new Class[0]);
            this.day = ((Integer) m2.invoke(o, new Object[0])).intValue();
        } catch (Throwable th) {
        }
    }

    private Object readResolve() {
        try {
            Class c = Class.forName("java.time.MonthDay");
            Method m = c.getDeclaredMethod("of", Integer.TYPE, Integer.TYPE);
            return m.invoke(null, Integer.valueOf(this.month), Integer.valueOf(this.day));
        } catch (Throwable th) {
            return null;
        }
    }
}
