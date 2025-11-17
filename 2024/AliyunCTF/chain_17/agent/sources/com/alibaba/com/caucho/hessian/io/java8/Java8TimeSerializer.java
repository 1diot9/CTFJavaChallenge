package com.alibaba.com.caucho.hessian.io.java8;

import com.alibaba.com.caucho.hessian.io.AbstractHessianOutput;
import com.alibaba.com.caucho.hessian.io.AbstractSerializer;
import java.io.IOException;
import java.lang.reflect.Constructor;

/* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/java8/Java8TimeSerializer.class */
public class Java8TimeSerializer<T> extends AbstractSerializer {
    private Class<T> handleType;

    private Java8TimeSerializer(Class<T> handleType) {
        this.handleType = handleType;
    }

    public static <T> Java8TimeSerializer<T> create(Class<T> handleType) {
        return new Java8TimeSerializer<>(handleType);
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractSerializer, com.alibaba.com.caucho.hessian.io.Serializer
    public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
        if (obj == null) {
            out.writeNull();
            return;
        }
        try {
            Constructor<T> constructor = this.handleType.getConstructor(Object.class);
            T handle = constructor.newInstance(obj);
            out.writeObject(handle);
        } catch (Exception e) {
            throw new RuntimeException("the class :" + this.handleType.getName() + " construct failed:" + e.getMessage(), e);
        }
    }
}
