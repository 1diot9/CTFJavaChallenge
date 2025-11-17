package com.alibaba.com.caucho.hessian.io;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/CalendarHandle.class */
public class CalendarHandle implements Serializable, HessianHandle {
    private Class type;
    private Date date;

    public CalendarHandle() {
    }

    public CalendarHandle(Class type, long time) {
        if (!GregorianCalendar.class.equals(type)) {
            this.type = type;
        }
        this.date = new Date(time);
    }

    private Object readResolve() {
        Calendar cal;
        try {
            if (this.type != null) {
                cal = (Calendar) this.type.newInstance();
            } else {
                cal = new GregorianCalendar();
            }
            cal.setTimeInMillis(this.date.getTime());
            return cal;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e2) {
            throw new RuntimeException(e2);
        }
    }
}
