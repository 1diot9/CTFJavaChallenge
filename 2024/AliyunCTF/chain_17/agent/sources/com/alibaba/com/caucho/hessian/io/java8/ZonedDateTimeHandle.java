package com.alibaba.com.caucho.hessian.io.java8;

import com.alibaba.com.caucho.hessian.io.HessianHandle;
import java.io.Serializable;
import java.lang.reflect.Method;

/* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/java8/ZonedDateTimeHandle.class */
public class ZonedDateTimeHandle implements HessianHandle, Serializable {
    private static final long serialVersionUID = -6933460123278647569L;
    private Object dateTime;
    private Object offset;
    private String zoneId;

    public ZonedDateTimeHandle() {
    }

    public ZonedDateTimeHandle(Object o) {
        try {
            Class c = Class.forName("java.time.ZonedDateTime");
            Method m = c.getDeclaredMethod("toLocalDateTime", new Class[0]);
            this.dateTime = m.invoke(o, new Object[0]);
            Method m2 = c.getDeclaredMethod("getOffset", new Class[0]);
            this.offset = m2.invoke(o, new Object[0]);
            Method m3 = c.getDeclaredMethod("getZone", new Class[0]);
            Object zone = m3.invoke(o, new Object[0]);
            if (zone != null) {
                Class zoneId = Class.forName("java.time.ZoneId");
                Method m4 = zoneId.getDeclaredMethod("getId", new Class[0]);
                this.zoneId = (String) m4.invoke(zone, new Object[0]);
            }
        } catch (Throwable th) {
        }
    }

    private Object readResolve() {
        try {
            Class zoneDateTime = Class.forName("java.time.ZonedDateTime");
            Method ofLocal = zoneDateTime.getDeclaredMethod("ofLocal", Class.forName("java.time.LocalDateTime"), Class.forName("java.time.ZoneId"), Class.forName("java.time.ZoneOffset"));
            Class c = Class.forName("java.time.ZoneId");
            Method of = c.getDeclaredMethod("of", String.class);
            return ofLocal.invoke(null, this.dateTime, of.invoke(null, this.zoneId), this.offset);
        } catch (Throwable th) {
            return null;
        }
    }
}
