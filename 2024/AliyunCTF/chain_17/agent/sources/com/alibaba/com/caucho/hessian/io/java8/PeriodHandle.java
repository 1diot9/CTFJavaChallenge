package com.alibaba.com.caucho.hessian.io.java8;

import com.alibaba.com.caucho.hessian.io.HessianHandle;
import java.io.Serializable;
import java.lang.reflect.Method;

/* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/java8/PeriodHandle.class */
public class PeriodHandle implements HessianHandle, Serializable {
    private static final long serialVersionUID = 4399720381283781186L;
    private int years;
    private int months;
    private int days;

    public PeriodHandle() {
    }

    public PeriodHandle(Object o) {
        try {
            Class c = Class.forName("java.time.Period");
            Method m = c.getDeclaredMethod("getYears", new Class[0]);
            this.years = ((Integer) m.invoke(o, new Object[0])).intValue();
            Method m2 = c.getDeclaredMethod("getMonths", new Class[0]);
            this.months = ((Integer) m2.invoke(o, new Object[0])).intValue();
            Method m3 = c.getDeclaredMethod("getDays", new Class[0]);
            this.days = ((Integer) m3.invoke(o, new Object[0])).intValue();
        } catch (Throwable th) {
        }
    }

    private Object readResolve() {
        try {
            Class c = Class.forName("java.time.Period");
            Method m = c.getDeclaredMethod("of", Integer.TYPE, Integer.TYPE, Integer.TYPE);
            return m.invoke(null, Integer.valueOf(this.years), Integer.valueOf(this.months), Integer.valueOf(this.days));
        } catch (Throwable th) {
            return null;
        }
    }
}
