package com.alibaba.com.caucho.hessian.io;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/JavaDeserializer.class */
public class JavaDeserializer extends AbstractMapDeserializer {
    private Class _type;
    private HashMap _fieldMap;
    private Method _readResolve;
    private Constructor _constructor;
    private Object[] _constructorArgs;
    private static final Logger log = Logger.getLogger(JavaDeserializer.class.getName());
    static final Map<String, Boolean> PRIMITIVE_TYPE = new HashMap<String, Boolean>() { // from class: com.alibaba.com.caucho.hessian.io.JavaDeserializer.1
        {
            put(Boolean.class.getName(), true);
            put(Character.class.getName(), true);
            put(Byte.class.getName(), true);
            put(Short.class.getName(), true);
            put(Integer.class.getName(), true);
            put(Long.class.getName(), true);
            put(Float.class.getName(), true);
            put(Double.class.getName(), true);
            put(Void.class.getName(), true);
        }
    };

    public JavaDeserializer(Class cl) {
        long j;
        long j2;
        this._type = cl;
        this._fieldMap = getFieldMap(cl);
        this._readResolve = getReadResolve(cl);
        if (this._readResolve != null) {
            this._readResolve.setAccessible(true);
        }
        Constructor[] constructors = cl.getDeclaredConstructors();
        long bestCost = Long.MAX_VALUE;
        for (int i = 0; i < constructors.length; i++) {
            Class[] param = constructors[i].getParameterTypes();
            long cost = 0;
            for (int j3 = 0; j3 < param.length; j3++) {
                long cost2 = 4 * cost;
                if (Object.class.equals(param[j3])) {
                    j = cost2;
                    j2 = 1;
                } else if (String.class.equals(param[j3])) {
                    j = cost2;
                    j2 = 2;
                } else if (Integer.TYPE.equals(param[j3])) {
                    j = cost2;
                    j2 = 3;
                } else if (Long.TYPE.equals(param[j3])) {
                    j = cost2;
                    j2 = 4;
                } else if (param[j3].isPrimitive()) {
                    j = cost2;
                    j2 = 5;
                } else {
                    j = cost2;
                    j2 = 6;
                }
                cost = j + j2;
            }
            long cost3 = ((cost < 0 || cost > 65536) ? 65536L : cost) + (param.length << 48);
            if (cost3 < bestCost) {
                this._constructor = constructors[i];
                bestCost = cost3;
            }
        }
        if (this._constructor != null) {
            this._constructor.setAccessible(true);
            Class[] params = this._constructor.getParameterTypes();
            this._constructorArgs = new Object[params.length];
            for (int i2 = 0; i2 < params.length; i2++) {
                this._constructorArgs[i2] = getParamArg(params[i2]);
            }
        }
    }

    protected static Object getParamArg(Class cl) {
        if (!cl.isPrimitive()) {
            return null;
        }
        if (Boolean.TYPE.equals(cl)) {
            return Boolean.FALSE;
        }
        if (Byte.TYPE.equals(cl)) {
            return new Byte((byte) 0);
        }
        if (Short.TYPE.equals(cl)) {
            return new Short((short) 0);
        }
        if (Character.TYPE.equals(cl)) {
            return new Character((char) 0);
        }
        if (Integer.TYPE.equals(cl)) {
            return 0;
        }
        if (Long.TYPE.equals(cl)) {
            return 0L;
        }
        if (Float.TYPE.equals(cl)) {
            return Float.valueOf(0.0f);
        }
        if (Double.TYPE.equals(cl)) {
            return Double.valueOf(0.0d);
        }
        throw new UnsupportedOperationException();
    }

    static void logDeserializeError(Field field, Object obj, Object value, Throwable e) throws IOException {
        String fieldName = field.getDeclaringClass().getName() + "." + field.getName();
        if (e instanceof HessianFieldException) {
            throw ((HessianFieldException) e);
        }
        if (e instanceof IOException) {
            throw new HessianFieldException(fieldName + ": " + e.getMessage(), e);
        }
        if (value != null) {
            throw new HessianFieldException(fieldName + ": " + value.getClass().getName() + " cannot be assigned to '" + field.getType().getName() + "'", e);
        }
        throw new HessianFieldException(fieldName + ": " + field.getType().getName() + " cannot be assigned from null", e);
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractMapDeserializer, com.alibaba.com.caucho.hessian.io.AbstractDeserializer, com.alibaba.com.caucho.hessian.io.Deserializer
    public Class getType() {
        return this._type;
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractDeserializer, com.alibaba.com.caucho.hessian.io.Deserializer
    public Object readMap(AbstractHessianInput in) throws IOException {
        try {
            Object obj = instantiate();
            return readMap(in, obj);
        } catch (IOException e) {
            throw e;
        } catch (RuntimeException e2) {
            throw e2;
        } catch (Exception e3) {
            throw new IOExceptionWrapper(this._type.getName() + ":" + e3.getMessage(), e3);
        }
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractDeserializer, com.alibaba.com.caucho.hessian.io.Deserializer
    public Object readObject(AbstractHessianInput in, String[] fieldNames) throws IOException {
        try {
            Object obj = instantiate();
            return readObject(in, obj, fieldNames);
        } catch (IOException e) {
            throw e;
        } catch (RuntimeException e2) {
            throw e2;
        } catch (Exception e3) {
            throw new IOExceptionWrapper(this._type.getName() + ":" + e3.getMessage(), e3);
        }
    }

    protected Method getReadResolve(Class cl) {
        while (cl != null) {
            Method[] methods = cl.getDeclaredMethods();
            for (Method method : methods) {
                if (method.getName().equals("readResolve") && method.getParameterTypes().length == 0) {
                    return method;
                }
            }
            cl = cl.getSuperclass();
        }
        return null;
    }

    public Object readMap(AbstractHessianInput in, Object obj) throws IOException {
        try {
            int ref = in.addRef(obj);
            while (!in.isEnd()) {
                Object key = in.readObject();
                FieldDeserializer deser = (FieldDeserializer) this._fieldMap.get(key);
                if (deser != null) {
                    deser.deserialize(in, obj);
                } else {
                    in.readObject();
                }
            }
            in.readMapEnd();
            Object resolve = resolve(obj);
            if (obj != resolve) {
                in.setRef(ref, resolve);
            }
            return resolve;
        } catch (IOException e) {
            throw e;
        } catch (Exception e2) {
            throw new IOExceptionWrapper(e2);
        }
    }

    public Object readObject(AbstractHessianInput in, Object obj, String[] fieldNames) throws IOException {
        try {
            int ref = in.addRef(obj);
            for (String name : fieldNames) {
                FieldDeserializer deser = (FieldDeserializer) this._fieldMap.get(name);
                if (deser != null) {
                    deser.deserialize(in, obj);
                } else {
                    in.readObject();
                }
            }
            Object resolve = resolve(obj);
            if (obj != resolve) {
                in.setRef(ref, resolve);
            }
            return resolve;
        } catch (IOException e) {
            throw e;
        } catch (Exception e2) {
            throw new IOExceptionWrapper(obj.getClass().getName() + ":" + e2, e2);
        }
    }

    private Object resolve(Object obj) throws Exception {
        try {
            if (this._readResolve != null) {
                return this._readResolve.invoke(obj, new Object[0]);
            }
        } catch (InvocationTargetException e) {
            if (e.getTargetException() != null) {
                throw e;
            }
        }
        return obj;
    }

    protected Object instantiate() throws Exception {
        try {
            if (this._constructor != null) {
                return this._constructor.newInstance(this._constructorArgs);
            }
            return this._type.newInstance();
        } catch (Exception e) {
            throw new HessianProtocolException("'" + this._type.getName() + "' could not be instantiated", e);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v53, types: [com.alibaba.com.caucho.hessian.io.JavaDeserializer$ObjectSetFieldDeserializer] */
    /* JADX WARN: Type inference failed for: r0v56, types: [com.alibaba.com.caucho.hessian.io.JavaDeserializer$ObjectListFieldDeserializer] */
    /* JADX WARN: Type inference failed for: r0v59, types: [com.alibaba.com.caucho.hessian.io.JavaDeserializer$ObjectMapFieldDeserializer] */
    /* JADX WARN: Type inference failed for: r0v60, types: [com.alibaba.com.caucho.hessian.io.JavaDeserializer$SqlTimeFieldDeserializer] */
    /* JADX WARN: Type inference failed for: r0v61, types: [com.alibaba.com.caucho.hessian.io.JavaDeserializer$SqlTimestampFieldDeserializer] */
    /* JADX WARN: Type inference failed for: r0v62, types: [com.alibaba.com.caucho.hessian.io.JavaDeserializer$SqlDateFieldDeserializer] */
    /* JADX WARN: Type inference failed for: r0v63, types: [com.alibaba.com.caucho.hessian.io.JavaDeserializer$BooleanFieldDeserializer] */
    /* JADX WARN: Type inference failed for: r0v64, types: [com.alibaba.com.caucho.hessian.io.JavaDeserializer$DoubleFieldDeserializer] */
    /* JADX WARN: Type inference failed for: r0v65, types: [com.alibaba.com.caucho.hessian.io.JavaDeserializer$FloatFieldDeserializer] */
    /* JADX WARN: Type inference failed for: r0v66, types: [com.alibaba.com.caucho.hessian.io.JavaDeserializer$LongFieldDeserializer] */
    /* JADX WARN: Type inference failed for: r0v67, types: [com.alibaba.com.caucho.hessian.io.JavaDeserializer$IntFieldDeserializer] */
    /* JADX WARN: Type inference failed for: r0v68, types: [com.alibaba.com.caucho.hessian.io.JavaDeserializer$ShortFieldDeserializer] */
    /* JADX WARN: Type inference failed for: r0v69, types: [com.alibaba.com.caucho.hessian.io.JavaDeserializer$ByteFieldDeserializer] */
    /* JADX WARN: Type inference failed for: r0v72, types: [com.alibaba.com.caucho.hessian.io.JavaDeserializer$StringFieldDeserializer] */
    protected HashMap getFieldMap(Class cl) {
        ObjectFieldDeserializer objectFieldDeserializer;
        HashMap fieldMap = new HashMap();
        while (cl != null) {
            Field[] fields = cl.getDeclaredFields();
            for (Field field : fields) {
                if (!Modifier.isTransient(field.getModifiers()) && !Modifier.isStatic(field.getModifiers()) && fieldMap.get(field.getName()) == null) {
                    try {
                        field.setAccessible(true);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    Class type = field.getType();
                    if (String.class.equals(type)) {
                        objectFieldDeserializer = new StringFieldDeserializer(field);
                    } else if (Byte.TYPE.equals(type)) {
                        objectFieldDeserializer = new ByteFieldDeserializer(field);
                    } else if (Short.TYPE.equals(type)) {
                        objectFieldDeserializer = new ShortFieldDeserializer(field);
                    } else if (Integer.TYPE.equals(type)) {
                        objectFieldDeserializer = new IntFieldDeserializer(field);
                    } else if (Long.TYPE.equals(type)) {
                        objectFieldDeserializer = new LongFieldDeserializer(field);
                    } else if (Float.TYPE.equals(type)) {
                        objectFieldDeserializer = new FloatFieldDeserializer(field);
                    } else if (Double.TYPE.equals(type)) {
                        objectFieldDeserializer = new DoubleFieldDeserializer(field);
                    } else if (Boolean.TYPE.equals(type)) {
                        objectFieldDeserializer = new BooleanFieldDeserializer(field);
                    } else if (Date.class.equals(type)) {
                        objectFieldDeserializer = new SqlDateFieldDeserializer(field);
                    } else if (Timestamp.class.equals(type)) {
                        objectFieldDeserializer = new SqlTimestampFieldDeserializer(field);
                    } else if (Time.class.equals(type)) {
                        objectFieldDeserializer = new SqlTimeFieldDeserializer(field);
                    } else if (Map.class.equals(type) && field.getGenericType() != field.getType()) {
                        objectFieldDeserializer = new ObjectMapFieldDeserializer(field);
                    } else if (List.class.equals(type) && field.getGenericType() != field.getType()) {
                        objectFieldDeserializer = new ObjectListFieldDeserializer(field);
                    } else if (Set.class.equals(type) && field.getGenericType() != field.getType()) {
                        objectFieldDeserializer = new ObjectSetFieldDeserializer(field);
                    } else {
                        objectFieldDeserializer = new ObjectFieldDeserializer(field);
                    }
                    fieldMap.put(field.getName(), objectFieldDeserializer);
                }
            }
            cl = cl.getSuperclass();
        }
        return fieldMap;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/JavaDeserializer$FieldDeserializer.class */
    public static abstract class FieldDeserializer {
        abstract void deserialize(AbstractHessianInput abstractHessianInput, Object obj) throws IOException;

        FieldDeserializer() {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/JavaDeserializer$ObjectFieldDeserializer.class */
    public static class ObjectFieldDeserializer extends FieldDeserializer {
        private final Field _field;

        ObjectFieldDeserializer(Field field) {
            this._field = field;
        }

        @Override // com.alibaba.com.caucho.hessian.io.JavaDeserializer.FieldDeserializer
        void deserialize(AbstractHessianInput in, Object obj) throws IOException {
            Object value = null;
            try {
                value = in.readObject(this._field.getType());
                this._field.set(obj, value);
            } catch (Exception e) {
                JavaDeserializer.logDeserializeError(this._field, obj, value, e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/JavaDeserializer$BooleanFieldDeserializer.class */
    public static class BooleanFieldDeserializer extends FieldDeserializer {
        private final Field _field;

        BooleanFieldDeserializer(Field field) {
            this._field = field;
        }

        @Override // com.alibaba.com.caucho.hessian.io.JavaDeserializer.FieldDeserializer
        void deserialize(AbstractHessianInput in, Object obj) throws IOException {
            boolean value = false;
            try {
                value = in.readBoolean();
                this._field.setBoolean(obj, value);
            } catch (Exception e) {
                JavaDeserializer.logDeserializeError(this._field, obj, Boolean.valueOf(value), e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/JavaDeserializer$ByteFieldDeserializer.class */
    public static class ByteFieldDeserializer extends FieldDeserializer {
        private final Field _field;

        ByteFieldDeserializer(Field field) {
            this._field = field;
        }

        @Override // com.alibaba.com.caucho.hessian.io.JavaDeserializer.FieldDeserializer
        void deserialize(AbstractHessianInput in, Object obj) throws IOException {
            int value = 0;
            try {
                value = in.readInt();
                this._field.setByte(obj, (byte) value);
            } catch (Exception e) {
                JavaDeserializer.logDeserializeError(this._field, obj, Integer.valueOf(value), e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/JavaDeserializer$ShortFieldDeserializer.class */
    public static class ShortFieldDeserializer extends FieldDeserializer {
        private final Field _field;

        ShortFieldDeserializer(Field field) {
            this._field = field;
        }

        @Override // com.alibaba.com.caucho.hessian.io.JavaDeserializer.FieldDeserializer
        void deserialize(AbstractHessianInput in, Object obj) throws IOException {
            int value = 0;
            try {
                value = in.readInt();
                this._field.setShort(obj, (short) value);
            } catch (Exception e) {
                JavaDeserializer.logDeserializeError(this._field, obj, Integer.valueOf(value), e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/JavaDeserializer$ObjectMapFieldDeserializer.class */
    public static class ObjectMapFieldDeserializer extends FieldDeserializer {
        private final Field _field;

        ObjectMapFieldDeserializer(Field field) {
            this._field = field;
        }

        @Override // com.alibaba.com.caucho.hessian.io.JavaDeserializer.FieldDeserializer
        void deserialize(AbstractHessianInput in, Object obj) throws IOException {
            Object value = null;
            try {
                Type[] types = ((ParameterizedType) this._field.getGenericType()).getActualTypeArguments();
                Class<?> type = this._field.getType();
                Class<?>[] clsArr = new Class[2];
                clsArr[0] = JavaDeserializer.isPrimitive(types[0]) ? (Class) types[0] : null;
                clsArr[1] = JavaDeserializer.isPrimitive(types[1]) ? (Class) types[1] : null;
                value = in.readObject(type, clsArr);
                this._field.set(obj, value);
            } catch (Exception e) {
                JavaDeserializer.logDeserializeError(this._field, obj, value, e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/JavaDeserializer$ObjectListFieldDeserializer.class */
    public static class ObjectListFieldDeserializer extends FieldDeserializer {
        private final Field _field;

        ObjectListFieldDeserializer(Field field) {
            this._field = field;
        }

        @Override // com.alibaba.com.caucho.hessian.io.JavaDeserializer.FieldDeserializer
        void deserialize(AbstractHessianInput in, Object obj) throws IOException {
            Object value = null;
            try {
                Type[] types = ((ParameterizedType) this._field.getGenericType()).getActualTypeArguments();
                Class<?> type = this._field.getType();
                Class<?>[] clsArr = new Class[1];
                clsArr[0] = JavaDeserializer.isPrimitive(types[0]) ? (Class) types[0] : null;
                value = in.readObject(type, clsArr);
                this._field.set(obj, value);
            } catch (Exception e) {
                JavaDeserializer.logDeserializeError(this._field, obj, value, e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/JavaDeserializer$ObjectSetFieldDeserializer.class */
    public static class ObjectSetFieldDeserializer extends FieldDeserializer {
        private final Field _field;

        ObjectSetFieldDeserializer(Field field) {
            this._field = field;
        }

        @Override // com.alibaba.com.caucho.hessian.io.JavaDeserializer.FieldDeserializer
        void deserialize(AbstractHessianInput in, Object obj) throws IOException {
            Object value = null;
            try {
                Type[] types = ((ParameterizedType) this._field.getGenericType()).getActualTypeArguments();
                Class<?> type = this._field.getType();
                Class<?>[] clsArr = new Class[1];
                clsArr[0] = JavaDeserializer.isPrimitive(types[0]) ? (Class) types[0] : null;
                value = in.readObject(type, clsArr);
                this._field.set(obj, value);
            } catch (Exception e) {
                JavaDeserializer.logDeserializeError(this._field, obj, value, e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/JavaDeserializer$IntFieldDeserializer.class */
    public static class IntFieldDeserializer extends FieldDeserializer {
        private final Field _field;

        IntFieldDeserializer(Field field) {
            this._field = field;
        }

        @Override // com.alibaba.com.caucho.hessian.io.JavaDeserializer.FieldDeserializer
        void deserialize(AbstractHessianInput in, Object obj) throws IOException {
            int value = 0;
            try {
                value = in.readInt();
                this._field.setInt(obj, value);
            } catch (Exception e) {
                JavaDeserializer.logDeserializeError(this._field, obj, Integer.valueOf(value), e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/JavaDeserializer$LongFieldDeserializer.class */
    public static class LongFieldDeserializer extends FieldDeserializer {
        private final Field _field;

        LongFieldDeserializer(Field field) {
            this._field = field;
        }

        @Override // com.alibaba.com.caucho.hessian.io.JavaDeserializer.FieldDeserializer
        void deserialize(AbstractHessianInput in, Object obj) throws IOException {
            long value = 0;
            try {
                value = in.readLong();
                this._field.setLong(obj, value);
            } catch (Exception e) {
                JavaDeserializer.logDeserializeError(this._field, obj, Long.valueOf(value), e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/JavaDeserializer$FloatFieldDeserializer.class */
    public static class FloatFieldDeserializer extends FieldDeserializer {
        private final Field _field;

        FloatFieldDeserializer(Field field) {
            this._field = field;
        }

        @Override // com.alibaba.com.caucho.hessian.io.JavaDeserializer.FieldDeserializer
        void deserialize(AbstractHessianInput in, Object obj) throws IOException {
            double value = 0.0d;
            try {
                value = in.readDouble();
                this._field.setFloat(obj, (float) value);
            } catch (Exception e) {
                JavaDeserializer.logDeserializeError(this._field, obj, Double.valueOf(value), e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/JavaDeserializer$DoubleFieldDeserializer.class */
    public static class DoubleFieldDeserializer extends FieldDeserializer {
        private final Field _field;

        DoubleFieldDeserializer(Field field) {
            this._field = field;
        }

        @Override // com.alibaba.com.caucho.hessian.io.JavaDeserializer.FieldDeserializer
        void deserialize(AbstractHessianInput in, Object obj) throws IOException {
            double value = 0.0d;
            try {
                value = in.readDouble();
                this._field.setDouble(obj, value);
            } catch (Exception e) {
                JavaDeserializer.logDeserializeError(this._field, obj, Double.valueOf(value), e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/JavaDeserializer$StringFieldDeserializer.class */
    public static class StringFieldDeserializer extends FieldDeserializer {
        private final Field _field;

        StringFieldDeserializer(Field field) {
            this._field = field;
        }

        @Override // com.alibaba.com.caucho.hessian.io.JavaDeserializer.FieldDeserializer
        void deserialize(AbstractHessianInput in, Object obj) throws IOException {
            String value = null;
            try {
                value = in.readString();
                this._field.set(obj, value);
            } catch (Exception e) {
                JavaDeserializer.logDeserializeError(this._field, obj, value, e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/JavaDeserializer$SqlDateFieldDeserializer.class */
    public static class SqlDateFieldDeserializer extends FieldDeserializer {
        private final Field _field;

        SqlDateFieldDeserializer(Field field) {
            this._field = field;
        }

        @Override // com.alibaba.com.caucho.hessian.io.JavaDeserializer.FieldDeserializer
        void deserialize(AbstractHessianInput in, Object obj) throws IOException {
            Date value = null;
            try {
                java.util.Date date = (java.util.Date) in.readObject();
                if (date != null) {
                    value = new Date(date.getTime());
                }
                this._field.set(obj, value);
            } catch (Exception e) {
                JavaDeserializer.logDeserializeError(this._field, obj, value, e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/JavaDeserializer$SqlTimestampFieldDeserializer.class */
    public static class SqlTimestampFieldDeserializer extends FieldDeserializer {
        private final Field _field;

        SqlTimestampFieldDeserializer(Field field) {
            this._field = field;
        }

        @Override // com.alibaba.com.caucho.hessian.io.JavaDeserializer.FieldDeserializer
        void deserialize(AbstractHessianInput in, Object obj) throws IOException {
            Timestamp value = null;
            try {
                java.util.Date date = (java.util.Date) in.readObject();
                if (date != null) {
                    value = new Timestamp(date.getTime());
                }
                this._field.set(obj, value);
            } catch (Exception e) {
                JavaDeserializer.logDeserializeError(this._field, obj, value, e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/JavaDeserializer$SqlTimeFieldDeserializer.class */
    public static class SqlTimeFieldDeserializer extends FieldDeserializer {
        private final Field _field;

        SqlTimeFieldDeserializer(Field field) {
            this._field = field;
        }

        @Override // com.alibaba.com.caucho.hessian.io.JavaDeserializer.FieldDeserializer
        void deserialize(AbstractHessianInput in, Object obj) throws IOException {
            Time value = null;
            try {
                java.util.Date date = (java.util.Date) in.readObject();
                if (date != null) {
                    value = new Time(date.getTime());
                }
                this._field.set(obj, value);
            } catch (Exception e) {
                JavaDeserializer.logDeserializeError(this._field, obj, value, e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean isPrimitive(Type type) {
        if (type != null) {
            try {
                if (type instanceof Class) {
                    Class<?> clazz = (Class) type;
                    if (!clazz.isPrimitive()) {
                        if (!PRIMITIVE_TYPE.containsKey(clazz.getName())) {
                            return false;
                        }
                    }
                    return true;
                }
                return false;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }
}
