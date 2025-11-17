package com.alibaba.com.caucho.hessian.io;

import java.io.IOException;

/* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/ObjectDeserializer.class */
public class ObjectDeserializer extends AbstractDeserializer {
    private Class _cl;

    public ObjectDeserializer(Class cl) {
        this._cl = cl;
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractDeserializer, com.alibaba.com.caucho.hessian.io.Deserializer
    public Class getType() {
        return this._cl;
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractDeserializer, com.alibaba.com.caucho.hessian.io.Deserializer
    public Object readObject(AbstractHessianInput in) throws IOException {
        return in.readObject();
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractDeserializer, com.alibaba.com.caucho.hessian.io.Deserializer
    public Object readObject(AbstractHessianInput in, String[] fieldNames) throws IOException {
        throw new UnsupportedOperationException(String.valueOf(this));
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractDeserializer, com.alibaba.com.caucho.hessian.io.Deserializer
    public Object readList(AbstractHessianInput in, int length) throws IOException {
        throw new UnsupportedOperationException(String.valueOf(this));
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractDeserializer, com.alibaba.com.caucho.hessian.io.Deserializer
    public Object readLengthList(AbstractHessianInput in, int length) throws IOException {
        throw new UnsupportedOperationException(String.valueOf(this));
    }

    public String toString() {
        return getClass().getSimpleName() + "[" + this._cl + "]";
    }
}
