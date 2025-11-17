package com.alibaba.com.caucho.hessian.io;

import java.io.IOException;
import java.util.Locale;

/* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/LocaleSerializer.class */
public class LocaleSerializer extends AbstractSerializer {
    private static LocaleSerializer SERIALIZER = new LocaleSerializer();

    public static LocaleSerializer create() {
        return SERIALIZER;
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractSerializer, com.alibaba.com.caucho.hessian.io.Serializer
    public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
        if (obj == null) {
            out.writeNull();
        } else {
            Locale locale = (Locale) obj;
            out.writeObject(new LocaleHandle(locale.toString()));
        }
    }
}
