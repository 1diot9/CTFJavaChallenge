package com.alibaba.com.caucho.hessian.io;

import java.io.IOException;
import java.util.Calendar;

/* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/CalendarSerializer.class */
public class CalendarSerializer extends AbstractSerializer {
    private static CalendarSerializer SERIALIZER = new CalendarSerializer();

    public static CalendarSerializer create() {
        return SERIALIZER;
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractSerializer, com.alibaba.com.caucho.hessian.io.Serializer
    public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
        if (obj == null) {
            out.writeNull();
        } else {
            Calendar cal = (Calendar) obj;
            out.writeObject(new CalendarHandle(cal.getClass(), cal.getTimeInMillis()));
        }
    }
}
