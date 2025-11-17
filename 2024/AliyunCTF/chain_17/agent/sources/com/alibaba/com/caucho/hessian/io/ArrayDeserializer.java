package com.alibaba.com.caucho.hessian.io;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

/* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/ArrayDeserializer.class */
public class ArrayDeserializer extends AbstractListDeserializer {
    private Class _componentType;
    private Class _type;

    public ArrayDeserializer(Class componentType) {
        this._componentType = componentType;
        if (this._componentType != null) {
            try {
                this._type = Array.newInstance((Class<?>) this._componentType, 0).getClass();
            } catch (Exception e) {
            }
        }
        if (this._type == null) {
            this._type = Object[].class;
        }
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractDeserializer, com.alibaba.com.caucho.hessian.io.Deserializer
    public Class getType() {
        return this._type;
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractDeserializer, com.alibaba.com.caucho.hessian.io.Deserializer
    public Object readList(AbstractHessianInput in, int length) throws IOException {
        if (length >= 0) {
            Object[] data = createArray(length);
            in.addRef(data);
            if (this._componentType != null) {
                for (int i = 0; i < data.length; i++) {
                    data[i] = in.readObject(this._componentType);
                }
            } else {
                for (int i2 = 0; i2 < data.length; i2++) {
                    data[i2] = in.readObject();
                }
            }
            in.readListEnd();
            return data;
        }
        ArrayList list = new ArrayList();
        in.addRef(list);
        if (this._componentType != null) {
            while (!in.isEnd()) {
                list.add(in.readObject(this._componentType));
            }
        } else {
            while (!in.isEnd()) {
                list.add(in.readObject());
            }
        }
        in.readListEnd();
        Object[] data2 = createArray(list.size());
        for (int i3 = 0; i3 < data2.length; i3++) {
            data2[i3] = list.get(i3);
        }
        return data2;
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractDeserializer, com.alibaba.com.caucho.hessian.io.Deserializer
    public Object readLengthList(AbstractHessianInput in, int length) throws IOException {
        Object[] data = createArray(length);
        in.addRef(data);
        if (this._componentType != null) {
            for (int i = 0; i < data.length; i++) {
                data[i] = in.readObject(this._componentType);
            }
        } else {
            for (int i2 = 0; i2 < data.length; i2++) {
                data[i2] = in.readObject();
            }
        }
        return data;
    }

    protected Object[] createArray(int length) {
        if (this._componentType != null) {
            return (Object[]) Array.newInstance((Class<?>) this._componentType, length);
        }
        return new Object[length];
    }

    public String toString() {
        return "ArrayDeserializer[" + this._componentType + "]";
    }
}
