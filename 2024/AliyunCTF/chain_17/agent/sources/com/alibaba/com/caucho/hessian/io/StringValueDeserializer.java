package com.alibaba.com.caucho.hessian.io;

import java.io.IOException;
import java.lang.reflect.Constructor;

/* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/StringValueDeserializer.class */
public class StringValueDeserializer extends AbstractDeserializer {
    private Class _cl;
    private Constructor _constructor;

    public StringValueDeserializer(Class cl) {
        try {
            this._cl = cl;
            this._constructor = cl.getConstructor(String.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractDeserializer, com.alibaba.com.caucho.hessian.io.Deserializer
    public Class getType() {
        return this._cl;
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractDeserializer, com.alibaba.com.caucho.hessian.io.Deserializer
    public Object readMap(AbstractHessianInput in) throws IOException {
        String value = null;
        while (!in.isEnd()) {
            String key = in.readString();
            if (key.equals("value")) {
                value = in.readString();
            } else {
                in.readObject();
            }
        }
        in.readMapEnd();
        Object object = create(value);
        in.addRef(object);
        return object;
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractDeserializer, com.alibaba.com.caucho.hessian.io.Deserializer
    public Object readObject(AbstractHessianInput in, String[] fieldNames) throws IOException {
        String value = null;
        for (String str : fieldNames) {
            if ("value".equals(str)) {
                value = in.readString();
            } else {
                in.readObject();
            }
        }
        Object object = create(value);
        in.addRef(object);
        return object;
    }

    private Object create(String value) throws IOException {
        if (value == null) {
            throw new IOException(this._cl.getName() + " expects name.");
        }
        try {
            return this._constructor.newInstance(value);
        } catch (Exception e) {
            throw new IOExceptionWrapper(e);
        }
    }
}
