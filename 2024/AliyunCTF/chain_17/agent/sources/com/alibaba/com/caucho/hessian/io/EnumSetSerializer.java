package com.alibaba.com.caucho.hessian.io;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.EnumSet;

/* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/EnumSetSerializer.class */
public class EnumSetSerializer extends AbstractSerializer {
    private static EnumSetSerializer SERIALIZER = new EnumSetSerializer();

    public static EnumSetSerializer getInstance() {
        return SERIALIZER;
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractSerializer, com.alibaba.com.caucho.hessian.io.Serializer
    public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
        if (obj == null) {
            out.writeNull();
            return;
        }
        try {
            Field field = EnumSet.class.getDeclaredField("elementType");
            field.setAccessible(true);
            Class type = (Class) field.get(obj);
            EnumSet enumSet = (EnumSet) obj;
            Object[] objects = enumSet.toArray();
            out.writeObject(new EnumSetHandler(type, objects));
        } catch (Throwable t) {
            throw new IOException(t);
        }
    }
}
