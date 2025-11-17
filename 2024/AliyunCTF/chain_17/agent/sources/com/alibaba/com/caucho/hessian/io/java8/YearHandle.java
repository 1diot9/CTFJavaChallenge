package com.alibaba.com.caucho.hessian.io.java8;

import com.alibaba.com.caucho.hessian.io.HessianHandle;
import java.io.Serializable;
import java.lang.reflect.Method;

/* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/java8/YearHandle.class */
public class YearHandle implements HessianHandle, Serializable {
    private static final long serialVersionUID = -6299552890287487926L;
    private int year;

    public YearHandle() {
    }

    public YearHandle(Object o) {
        try {
            Class c = Class.forName("java.time.Year");
            Method m = c.getDeclaredMethod("getValue", new Class[0]);
            this.year = ((Integer) m.invoke(o, new Object[0])).intValue();
        } catch (Throwable th) {
        }
    }

    private Object readResolve() {
        try {
            Class c = Class.forName("java.time.Year");
            Method m = c.getDeclaredMethod("of", Integer.TYPE);
            return m.invoke(null, Integer.valueOf(this.year));
        } catch (Throwable th) {
            return null;
        }
    }
}
