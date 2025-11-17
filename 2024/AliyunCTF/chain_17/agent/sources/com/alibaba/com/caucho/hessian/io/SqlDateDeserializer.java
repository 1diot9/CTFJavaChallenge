package com.alibaba.com.caucho.hessian.io;

import java.io.IOException;
import java.lang.reflect.Constructor;

/* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/SqlDateDeserializer.class */
public class SqlDateDeserializer extends AbstractDeserializer {
    private Class _cl;
    private Constructor _constructor;

    public SqlDateDeserializer(Class cl) throws NoSuchMethodException {
        this._cl = cl;
        this._constructor = cl.getConstructor(Long.TYPE);
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractDeserializer, com.alibaba.com.caucho.hessian.io.Deserializer
    public Class getType() {
        return this._cl;
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractDeserializer, com.alibaba.com.caucho.hessian.io.Deserializer
    public Object readMap(AbstractHessianInput in) throws IOException {
        int ref = in.addRef(null);
        long initValue = Long.MIN_VALUE;
        while (!in.isEnd()) {
            String key = in.readString();
            if (key.equals("value")) {
                initValue = in.readUTCDate();
            } else {
                in.readString();
            }
        }
        in.readMapEnd();
        Object value = create(initValue);
        in.setRef(ref, value);
        return value;
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractDeserializer, com.alibaba.com.caucho.hessian.io.Deserializer
    public Object readObject(AbstractHessianInput in, String[] fieldNames) throws IOException {
        int ref = in.addRef(null);
        long initValue = Long.MIN_VALUE;
        for (String key : fieldNames) {
            if (key.equals("value")) {
                initValue = in.readUTCDate();
            } else {
                in.readObject();
            }
        }
        Object value = create(initValue);
        in.setRef(ref, value);
        return value;
    }

    private Object create(long initValue) throws IOException {
        if (initValue == Long.MIN_VALUE) {
            throw new IOException(this._cl.getName() + " expects name.");
        }
        try {
            return this._constructor.newInstance(new Long(initValue));
        } catch (Exception e) {
            throw new IOExceptionWrapper(e);
        }
    }
}
