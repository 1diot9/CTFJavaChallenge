package com.alibaba.com.caucho.hessian.io;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/CollectionDeserializer.class */
public class CollectionDeserializer extends AbstractListDeserializer {
    private Class _type;

    public CollectionDeserializer(Class type) {
        this._type = type;
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractDeserializer, com.alibaba.com.caucho.hessian.io.Deserializer
    public Class getType() {
        return this._type;
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractDeserializer, com.alibaba.com.caucho.hessian.io.Deserializer
    public Object readList(AbstractHessianInput in, int length) throws IOException {
        return readList(in, length, this._type);
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractDeserializer, com.alibaba.com.caucho.hessian.io.Deserializer
    public Object readList(AbstractHessianInput in, int length, Class<?> expectType) throws IOException {
        Collection list = createList();
        in.addRef(list);
        Deserializer deserializer = null;
        SerializerFactory factory = findSerializerFactory(in);
        if (expectType != null) {
            deserializer = factory.getDeserializer(expectType.getName());
        }
        while (!in.isEnd()) {
            list.add(deserializer != null ? deserializer.readObject(in) : in.readObject());
        }
        in.readEnd();
        return list;
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractDeserializer, com.alibaba.com.caucho.hessian.io.Deserializer
    public Object readLengthList(AbstractHessianInput in, int length) throws IOException {
        return readList(in, length, null);
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractDeserializer, com.alibaba.com.caucho.hessian.io.Deserializer
    public Object readLengthList(AbstractHessianInput in, int length, Class<?> expectType) throws IOException {
        Collection list = createList();
        in.addRef(list);
        Deserializer deserializer = null;
        SerializerFactory factory = findSerializerFactory(in);
        if (expectType != null) {
            deserializer = factory.getDeserializer(expectType.getName());
        }
        while (length > 0) {
            list.add(deserializer != null ? deserializer.readObject(in) : in.readObject());
            length--;
        }
        return list;
    }

    private Collection createList() throws IOException {
        Collection list = null;
        if (this._type == null) {
            list = new ArrayList();
        } else if (!this._type.isInterface()) {
            try {
                list = (Collection) this._type.newInstance();
            } catch (Exception e) {
            }
        }
        if (list == null) {
            if (SortedSet.class.isAssignableFrom(this._type)) {
                list = new TreeSet();
            } else if (Set.class.isAssignableFrom(this._type)) {
                list = new HashSet();
            } else if (List.class.isAssignableFrom(this._type)) {
                list = new ArrayList();
            } else if (Collection.class.isAssignableFrom(this._type)) {
                list = new ArrayList();
            } else {
                try {
                    list = (Collection) this._type.newInstance();
                } catch (Exception e2) {
                    throw new IOExceptionWrapper(e2);
                }
            }
        }
        return list;
    }

    public String toString() {
        return getClass().getSimpleName() + "[" + this._type + "]";
    }
}
