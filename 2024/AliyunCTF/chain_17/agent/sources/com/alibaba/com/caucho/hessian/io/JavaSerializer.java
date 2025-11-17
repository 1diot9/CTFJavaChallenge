package com.alibaba.com.caucho.hessian.io;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/JavaSerializer.class */
public class JavaSerializer extends AbstractSerializer {
    private static final Logger log = Logger.getLogger(JavaSerializer.class.getName());
    private static Object[] NULL_ARGS = new Object[0];
    private Field[] _fields;
    private FieldSerializer[] _fieldSerializers;
    private Object _writeReplaceFactory;
    private Method _writeReplace;

    public JavaSerializer(Class cl, ClassLoader loader) {
        introspectWriteReplace(cl, loader);
        if (this._writeReplace != null) {
            this._writeReplace.setAccessible(true);
        }
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        while (cl != null) {
            for (Field field : cl.getDeclaredFields()) {
                if (!Modifier.isTransient(field.getModifiers()) && !Modifier.isStatic(field.getModifiers())) {
                    field.setAccessible(true);
                    if (field.getType().isPrimitive() || (field.getType().getName().startsWith("java.lang.") && !field.getType().equals(Object.class))) {
                        arrayList.add(field);
                    } else {
                        arrayList2.add(field);
                    }
                }
            }
            cl = cl.getSuperclass();
        }
        List fields = new ArrayList();
        fields.addAll(arrayList);
        fields.addAll(arrayList2);
        Collections.reverse(fields);
        this._fields = new Field[fields.size()];
        fields.toArray(this._fields);
        this._fieldSerializers = new FieldSerializer[this._fields.length];
        for (int i = 0; i < this._fields.length; i++) {
            this._fieldSerializers[i] = getFieldSerializer(this._fields[i].getType());
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static Method getWriteReplace(Class cl) {
        while (cl != null) {
            Method[] methods = cl.getDeclaredMethods();
            for (Method method : methods) {
                if (method.getName().equals("writeReplace") && method.getParameterTypes().length == 0) {
                    return method;
                }
            }
            cl = cl.getSuperclass();
        }
        return null;
    }

    private static FieldSerializer getFieldSerializer(Class type) {
        if (Integer.TYPE.equals(type) || Byte.TYPE.equals(type) || Short.TYPE.equals(type) || Integer.TYPE.equals(type)) {
            return IntFieldSerializer.SER;
        }
        if (Long.TYPE.equals(type)) {
            return LongFieldSerializer.SER;
        }
        if (Double.TYPE.equals(type) || Float.TYPE.equals(type)) {
            return DoubleFieldSerializer.SER;
        }
        if (Boolean.TYPE.equals(type)) {
            return BooleanFieldSerializer.SER;
        }
        if (String.class.equals(type)) {
            return StringFieldSerializer.SER;
        }
        if (Date.class.equals(type) || java.sql.Date.class.equals(type) || Timestamp.class.equals(type) || Time.class.equals(type)) {
            return DateFieldSerializer.SER;
        }
        return FieldSerializer.SER;
    }

    private void introspectWriteReplace(Class cl, ClassLoader loader) {
        try {
            String className = cl.getName() + "HessianSerializer";
            Class serializerClass = Class.forName(className, false, loader);
            Object serializerObject = serializerClass.newInstance();
            Method writeReplace = getWriteReplace(serializerClass, cl);
            if (writeReplace != null) {
                this._writeReplaceFactory = serializerObject;
                this._writeReplace = writeReplace;
                return;
            }
        } catch (ClassNotFoundException e) {
        } catch (Exception e2) {
            log.log(Level.FINER, e2.toString(), (Throwable) e2);
        }
        this._writeReplace = getWriteReplace(cl);
    }

    protected Method getWriteReplace(Class cl, Class param) {
        while (cl != null) {
            for (Method method : cl.getDeclaredMethods()) {
                if (method.getName().equals("writeReplace") && method.getParameterTypes().length == 1 && param.equals(method.getParameterTypes()[0])) {
                    return method;
                }
            }
            cl = cl.getSuperclass();
        }
        return null;
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractSerializer, com.alibaba.com.caucho.hessian.io.Serializer
    public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
        Object repl;
        if (out.addRef(obj)) {
            return;
        }
        Class cl = obj.getClass();
        try {
            if (this._writeReplace != null) {
                if (this._writeReplaceFactory != null) {
                    repl = this._writeReplace.invoke(this._writeReplaceFactory, obj);
                } else {
                    repl = this._writeReplace.invoke(obj, new Object[0]);
                }
                if (repl != obj) {
                    out.removeRef(obj);
                    out.writeObject(repl);
                    out.replaceRef(repl, obj);
                    return;
                }
            }
            int ref = out.writeObjectBegin(cl.getName());
            if (ref < -1) {
                writeObject10(obj, out);
                return;
            }
            if (ref == -1) {
                writeDefinition20(out);
                out.writeObjectBegin(cl.getName());
            }
            writeInstance(obj, out);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e2) {
            throw new RuntimeException(e2);
        }
    }

    private void writeObject10(Object obj, AbstractHessianOutput out) throws IOException {
        for (int i = 0; i < this._fields.length; i++) {
            Field field = this._fields[i];
            out.writeString(field.getName());
            this._fieldSerializers[i].serialize(out, obj, field);
        }
        out.writeMapEnd();
    }

    private void writeDefinition20(AbstractHessianOutput out) throws IOException {
        out.writeClassFieldLength(this._fields.length);
        for (int i = 0; i < this._fields.length; i++) {
            Field field = this._fields[i];
            out.writeString(field.getName());
        }
    }

    public void writeInstance(Object obj, AbstractHessianOutput out) throws IOException {
        for (int i = 0; i < this._fields.length; i++) {
            Field field = this._fields[i];
            this._fieldSerializers[i].serialize(out, obj, field);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/JavaSerializer$FieldSerializer.class */
    public static class FieldSerializer {
        static final FieldSerializer SER = new FieldSerializer();

        FieldSerializer() {
        }

        void serialize(AbstractHessianOutput out, Object obj, Field field) throws IOException {
            Object value = null;
            try {
                value = field.get(obj);
            } catch (IllegalAccessException e) {
                JavaSerializer.log.log(Level.FINE, e.toString(), (Throwable) e);
            }
            try {
                out.writeObject(value);
            } catch (IOException e2) {
                throw new IOExceptionWrapper(e2.getMessage() + "\n Java field: " + field, e2);
            } catch (RuntimeException e3) {
                throw new RuntimeException(e3.getMessage() + "\n Java field: " + field, e3);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/JavaSerializer$BooleanFieldSerializer.class */
    public static class BooleanFieldSerializer extends FieldSerializer {
        static final FieldSerializer SER = new BooleanFieldSerializer();

        BooleanFieldSerializer() {
        }

        @Override // com.alibaba.com.caucho.hessian.io.JavaSerializer.FieldSerializer
        void serialize(AbstractHessianOutput out, Object obj, Field field) throws IOException {
            boolean value = false;
            try {
                value = field.getBoolean(obj);
            } catch (IllegalAccessException e) {
                JavaSerializer.log.log(Level.FINE, e.toString(), (Throwable) e);
            }
            out.writeBoolean(value);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/JavaSerializer$IntFieldSerializer.class */
    public static class IntFieldSerializer extends FieldSerializer {
        static final FieldSerializer SER = new IntFieldSerializer();

        IntFieldSerializer() {
        }

        @Override // com.alibaba.com.caucho.hessian.io.JavaSerializer.FieldSerializer
        void serialize(AbstractHessianOutput out, Object obj, Field field) throws IOException {
            int value = 0;
            try {
                value = field.getInt(obj);
            } catch (IllegalAccessException e) {
                JavaSerializer.log.log(Level.FINE, e.toString(), (Throwable) e);
            }
            out.writeInt(value);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/JavaSerializer$LongFieldSerializer.class */
    public static class LongFieldSerializer extends FieldSerializer {
        static final FieldSerializer SER = new LongFieldSerializer();

        LongFieldSerializer() {
        }

        @Override // com.alibaba.com.caucho.hessian.io.JavaSerializer.FieldSerializer
        void serialize(AbstractHessianOutput out, Object obj, Field field) throws IOException {
            long value = 0;
            try {
                value = field.getLong(obj);
            } catch (IllegalAccessException e) {
                JavaSerializer.log.log(Level.FINE, e.toString(), (Throwable) e);
            }
            out.writeLong(value);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/JavaSerializer$DoubleFieldSerializer.class */
    public static class DoubleFieldSerializer extends FieldSerializer {
        static final FieldSerializer SER = new DoubleFieldSerializer();

        DoubleFieldSerializer() {
        }

        @Override // com.alibaba.com.caucho.hessian.io.JavaSerializer.FieldSerializer
        void serialize(AbstractHessianOutput out, Object obj, Field field) throws IOException {
            double value = 0.0d;
            try {
                value = field.getDouble(obj);
            } catch (IllegalAccessException e) {
                JavaSerializer.log.log(Level.FINE, e.toString(), (Throwable) e);
            }
            out.writeDouble(value);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/JavaSerializer$StringFieldSerializer.class */
    public static class StringFieldSerializer extends FieldSerializer {
        static final FieldSerializer SER = new StringFieldSerializer();

        StringFieldSerializer() {
        }

        @Override // com.alibaba.com.caucho.hessian.io.JavaSerializer.FieldSerializer
        void serialize(AbstractHessianOutput out, Object obj, Field field) throws IOException {
            String value = null;
            try {
                value = (String) field.get(obj);
            } catch (IllegalAccessException e) {
                JavaSerializer.log.log(Level.FINE, e.toString(), (Throwable) e);
            }
            out.writeString(value);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/JavaSerializer$DateFieldSerializer.class */
    public static class DateFieldSerializer extends FieldSerializer {
        static final FieldSerializer SER = new DateFieldSerializer();

        DateFieldSerializer() {
        }

        @Override // com.alibaba.com.caucho.hessian.io.JavaSerializer.FieldSerializer
        void serialize(AbstractHessianOutput out, Object obj, Field field) throws IOException {
            Date value = null;
            try {
                value = (Date) field.get(obj);
            } catch (IllegalAccessException e) {
                JavaSerializer.log.log(Level.FINE, e.toString(), (Throwable) e);
            }
            if (value == null) {
                out.writeNull();
            } else {
                out.writeUTCDate(value.getTime());
            }
        }
    }
}
