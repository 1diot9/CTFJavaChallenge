package com.alibaba.com.caucho.hessian.io;

import ch.qos.logback.core.CoreConstants;
import java.io.IOException;
import java.lang.reflect.Method;

/* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/EnumDeserializer.class */
public class EnumDeserializer extends AbstractDeserializer {
    private Class _enumType;
    private Method _valueOf;

    public EnumDeserializer(Class cl) {
        if (cl.isEnum()) {
            this._enumType = cl;
        } else if (cl.getSuperclass().isEnum()) {
            this._enumType = cl.getSuperclass();
        } else {
            throw new RuntimeException("Class " + cl.getName() + " is not an enum");
        }
        try {
            this._valueOf = this._enumType.getMethod(CoreConstants.VALUE_OF, Class.class, String.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractDeserializer, com.alibaba.com.caucho.hessian.io.Deserializer
    public Class getType() {
        return this._enumType;
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractDeserializer, com.alibaba.com.caucho.hessian.io.Deserializer
    public Object readMap(AbstractHessianInput in) throws IOException {
        String name = null;
        while (!in.isEnd()) {
            String key = in.readString();
            if (key.equals("name")) {
                name = in.readString();
            } else {
                in.readObject();
            }
        }
        in.readMapEnd();
        Object obj = create(name);
        in.addRef(obj);
        return obj;
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractDeserializer, com.alibaba.com.caucho.hessian.io.Deserializer
    public Object readObject(AbstractHessianInput in, String[] fieldNames) throws IOException {
        String name = null;
        for (String str : fieldNames) {
            if ("name".equals(str)) {
                name = in.readString();
            } else {
                in.readObject();
            }
        }
        Object obj = create(name);
        in.addRef(obj);
        return obj;
    }

    private Object create(String name) throws IOException {
        if (name == null) {
            throw new IOException(this._enumType.getName() + " expects name.");
        }
        try {
            return this._valueOf.invoke(null, this._enumType, name);
        } catch (Exception e) {
            throw new IOExceptionWrapper(e);
        }
    }
}
