package org.apache.el.lang;

import jakarta.el.FunctionMapper;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.el.util.MessageFactory;
import org.apache.el.util.ReflectionUtil;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-el-10.1.18.jar:org/apache/el/lang/FunctionMapperImpl.class */
public class FunctionMapperImpl extends FunctionMapper implements Externalizable {
    private static final long serialVersionUID = 1;
    protected ConcurrentMap<String, Function> functions = new ConcurrentHashMap();

    @Override // jakarta.el.FunctionMapper
    public Method resolveFunction(String prefix, String localName) {
        Function f = this.functions.get(prefix + ":" + localName);
        if (f == null) {
            return null;
        }
        return f.getMethod();
    }

    @Override // jakarta.el.FunctionMapper
    public void mapFunction(String prefix, String localName, Method m) {
        String key = prefix + ":" + localName;
        if (m == null) {
            this.functions.remove(key);
        } else {
            Function f = new Function(prefix, localName, m);
            this.functions.put(key, f);
        }
    }

    @Override // java.io.Externalizable
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(this.functions);
    }

    @Override // java.io.Externalizable
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.functions = (ConcurrentMap) in.readObject();
    }

    /* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-el-10.1.18.jar:org/apache/el/lang/FunctionMapperImpl$Function.class */
    public static class Function implements Externalizable {
        protected transient Method m;
        protected String owner;
        protected String name;
        protected String[] types;
        protected String prefix;
        protected String localName;

        public Function(String prefix, String localName, Method m) {
            if (localName == null) {
                throw new NullPointerException(MessageFactory.get("error.nullLocalName"));
            }
            if (m == null) {
                throw new NullPointerException(MessageFactory.get("error.nullMethod"));
            }
            this.prefix = prefix;
            this.localName = localName;
            this.m = m;
        }

        public Function() {
        }

        @Override // java.io.Externalizable
        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeUTF(this.prefix != null ? this.prefix : "");
            out.writeUTF(this.localName);
            if (this.owner != null && this.name != null && this.types != null) {
                out.writeUTF(this.owner);
                out.writeUTF(this.name);
                out.writeObject(this.types);
            } else {
                out.writeUTF(this.m.getDeclaringClass().getName());
                out.writeUTF(this.m.getName());
                out.writeObject(ReflectionUtil.toTypeNameArray(this.m.getParameterTypes()));
            }
        }

        @Override // java.io.Externalizable
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            this.prefix = in.readUTF();
            if (this.prefix.isEmpty()) {
                this.prefix = null;
            }
            this.localName = in.readUTF();
            this.owner = in.readUTF();
            this.name = in.readUTF();
            this.types = (String[]) in.readObject();
        }

        public Method getMethod() {
            if (this.m == null) {
                try {
                    Class<?> t = ReflectionUtil.forName(this.owner);
                    Class<?>[] p = ReflectionUtil.toTypeArray(this.types);
                    this.m = t.getMethod(this.name, p);
                } catch (Exception e) {
                }
            }
            return this.m;
        }

        public boolean equals(Object obj) {
            return (obj instanceof Function) && hashCode() == obj.hashCode();
        }

        public int hashCode() {
            return (this.prefix + this.localName).hashCode();
        }
    }
}
