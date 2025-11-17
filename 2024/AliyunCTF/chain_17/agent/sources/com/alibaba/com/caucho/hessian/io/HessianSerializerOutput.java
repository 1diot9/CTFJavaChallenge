package com.alibaba.com.caucho.hessian.io;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/HessianSerializerOutput.class */
public class HessianSerializerOutput extends HessianOutput {
    public HessianSerializerOutput(OutputStream os) {
        super(os);
    }

    public HessianSerializerOutput() {
    }

    public void writeObjectImpl(Object obj) throws IOException {
        Class cl = obj.getClass();
        try {
            Method method = cl.getMethod("writeReplace", new Class[0]);
            Object repl = method.invoke(obj, new Object[0]);
            writeObject(repl);
        } catch (Exception e) {
            try {
                writeMapBegin(cl.getName());
                while (cl != null) {
                    Field[] fields = cl.getDeclaredFields();
                    for (Field field : fields) {
                        if (!Modifier.isTransient(field.getModifiers()) && !Modifier.isStatic(field.getModifiers())) {
                            field.setAccessible(true);
                            writeString(field.getName());
                            writeObject(field.get(obj));
                        }
                    }
                    cl = cl.getSuperclass();
                }
                writeMapEnd();
            } catch (IllegalAccessException e2) {
                throw new IOExceptionWrapper(e2);
            }
        }
    }
}
