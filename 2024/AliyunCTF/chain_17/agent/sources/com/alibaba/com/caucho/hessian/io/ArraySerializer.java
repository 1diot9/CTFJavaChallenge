package com.alibaba.com.caucho.hessian.io;

import java.io.IOException;

/* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/ArraySerializer.class */
public class ArraySerializer extends AbstractSerializer {
    @Override // com.alibaba.com.caucho.hessian.io.AbstractSerializer, com.alibaba.com.caucho.hessian.io.Serializer
    public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
        if (out.addRef(obj)) {
            return;
        }
        Object[] array = (Object[]) obj;
        boolean hasEnd = out.writeListBegin(array.length, getArrayType(obj.getClass()));
        for (Object obj2 : array) {
            out.writeObject(obj2);
        }
        if (hasEnd) {
            out.writeListEnd();
        }
    }

    private String getArrayType(Class cl) {
        if (cl.isArray()) {
            return '[' + getArrayType(cl.getComponentType());
        }
        String name = cl.getName();
        if (name.equals("java.lang.String")) {
            return "string";
        }
        if (name.equals("java.lang.Object")) {
            return "object";
        }
        if (name.equals("java.util.Date")) {
            return "date";
        }
        return name;
    }
}
