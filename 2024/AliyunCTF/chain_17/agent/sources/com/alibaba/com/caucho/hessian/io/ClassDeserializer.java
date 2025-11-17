package com.alibaba.com.caucho.hessian.io;

import java.io.IOException;
import java.util.HashMap;

/* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/ClassDeserializer.class */
public class ClassDeserializer extends AbstractMapDeserializer {
    private static final HashMap<String, Class> _primClasses = new HashMap<>();
    private ClassLoader _loader;

    static {
        _primClasses.put("void", Void.TYPE);
        _primClasses.put("boolean", Boolean.TYPE);
        _primClasses.put("java.lang.Boolean", Boolean.class);
        _primClasses.put("byte", Byte.TYPE);
        _primClasses.put("java.lang.Byte", Byte.class);
        _primClasses.put("char", Character.TYPE);
        _primClasses.put("java.lang.Character", Character.class);
        _primClasses.put("short", Short.TYPE);
        _primClasses.put("java.lang.Short", Short.class);
        _primClasses.put("int", Integer.TYPE);
        _primClasses.put("java.lang.Integer", Integer.class);
        _primClasses.put("long", Long.TYPE);
        _primClasses.put("java.lang.Long", Long.class);
        _primClasses.put("float", Float.TYPE);
        _primClasses.put("java.lang.Float", Float.class);
        _primClasses.put("double", Double.TYPE);
        _primClasses.put("java.lang.Double", Double.class);
        _primClasses.put("java.lang.String", String.class);
    }

    public ClassDeserializer(ClassLoader loader) {
        this._loader = loader;
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractMapDeserializer, com.alibaba.com.caucho.hessian.io.AbstractDeserializer, com.alibaba.com.caucho.hessian.io.Deserializer
    public Class getType() {
        return Class.class;
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractDeserializer, com.alibaba.com.caucho.hessian.io.Deserializer
    public Object readMap(AbstractHessianInput in) throws IOException {
        int ref = in.addRef(null);
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
        Object value = create(name);
        in.setRef(ref, value);
        return value;
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractDeserializer, com.alibaba.com.caucho.hessian.io.Deserializer
    public Object readObject(AbstractHessianInput in, String[] fieldNames) throws IOException {
        int ref = in.addRef(null);
        String name = null;
        for (String str : fieldNames) {
            if ("name".equals(str)) {
                name = in.readString();
            } else {
                in.readObject();
            }
        }
        Object value = create(name);
        in.setRef(ref, value);
        return value;
    }

    Object create(String name) throws IOException {
        if (name == null) {
            throw new IOException("Serialized Class expects name.");
        }
        Class cl = _primClasses.get(name);
        if (cl != null) {
            return cl;
        }
        try {
            if (this._loader != null) {
                return Class.forName(name, false, this._loader);
            }
            return Class.forName(name);
        } catch (Exception e) {
            throw new IOExceptionWrapper(e);
        }
    }
}
