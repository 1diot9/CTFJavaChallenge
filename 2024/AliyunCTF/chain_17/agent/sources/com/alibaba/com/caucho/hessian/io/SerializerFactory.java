package com.alibaba.com.caucho.hessian.io;

import com.alibaba.com.caucho.hessian.io.java8.DurationHandle;
import com.alibaba.com.caucho.hessian.io.java8.InstantHandle;
import com.alibaba.com.caucho.hessian.io.java8.Java8TimeSerializer;
import com.alibaba.com.caucho.hessian.io.java8.LocalDateHandle;
import com.alibaba.com.caucho.hessian.io.java8.LocalDateTimeHandle;
import com.alibaba.com.caucho.hessian.io.java8.LocalTimeHandle;
import com.alibaba.com.caucho.hessian.io.java8.MonthDayHandle;
import com.alibaba.com.caucho.hessian.io.java8.OffsetDateTimeHandle;
import com.alibaba.com.caucho.hessian.io.java8.OffsetTimeHandle;
import com.alibaba.com.caucho.hessian.io.java8.PeriodHandle;
import com.alibaba.com.caucho.hessian.io.java8.YearHandle;
import com.alibaba.com.caucho.hessian.io.java8.YearMonthHandle;
import com.alibaba.com.caucho.hessian.io.java8.ZoneIdSerializer;
import com.alibaba.com.caucho.hessian.io.java8.ZoneOffsetHandle;
import com.alibaba.com.caucho.hessian.io.java8.ZonedDateTimeHandle;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.ObjectName;
import org.springframework.util.backoff.ExponentialBackOff;

/* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/SerializerFactory.class */
public class SerializerFactory extends AbstractSerializerFactory {
    private static final Logger log = Logger.getLogger(SerializerFactory.class.getName());
    private static Deserializer OBJECT_DESERIALIZER = new BasicDeserializer(13);
    private static ConcurrentHashMap _unrecognizedTypeCache = new ConcurrentHashMap();
    private static HashMap _staticSerializerMap = new HashMap();
    private static HashMap _staticDeserializerMap = new HashMap();
    private static HashMap _staticTypeMap = new HashMap();
    protected Serializer _defaultSerializer;
    protected ArrayList _factories;
    protected CollectionSerializer _collectionSerializer;
    protected MapSerializer _mapSerializer;
    private ClassLoader _loader;
    private Deserializer _hashMapDeserializer;
    private Deserializer _arrayListDeserializer;
    private ConcurrentHashMap _cachedSerializerMap;
    private ConcurrentHashMap _cachedDeserializerMap;
    private ConcurrentHashMap _cachedTypeDeserializerMap;
    private boolean _isAllowNonSerializable;
    private Map<String, Object> _typeNotFoundDeserializerMap;
    private static final Object PRESENT;
    private ClassFactory _classFactory;

    static {
        addBasic(Void.TYPE, "void", 0);
        addBasic(Boolean.class, "boolean", 1);
        addBasic(Byte.class, "byte", 2);
        addBasic(Short.class, "short", 3);
        addBasic(Integer.class, "int", 4);
        addBasic(Long.class, "long", 5);
        addBasic(Float.class, "float", 6);
        addBasic(Double.class, "double", 7);
        addBasic(Character.class, "char", 9);
        addBasic(String.class, "string", 10);
        addBasic(Object.class, "object", 13);
        addBasic(Date.class, "date", 11);
        addBasic(Boolean.TYPE, "boolean", 1);
        addBasic(Byte.TYPE, "byte", 2);
        addBasic(Short.TYPE, "short", 3);
        addBasic(Integer.TYPE, "int", 4);
        addBasic(Long.TYPE, "long", 5);
        addBasic(Float.TYPE, "float", 6);
        addBasic(Double.TYPE, "double", 7);
        addBasic(Character.TYPE, "char", 8);
        addBasic(boolean[].class, "[boolean", 14);
        addBasic(byte[].class, "[byte", 15);
        addBasic(short[].class, "[short", 16);
        addBasic(int[].class, "[int", 17);
        addBasic(long[].class, "[long", 18);
        addBasic(float[].class, "[float", 19);
        addBasic(double[].class, "[double", 20);
        addBasic(char[].class, "[char", 21);
        addBasic(String[].class, "[string", 22);
        addBasic(Object[].class, "[object", 23);
        _staticSerializerMap.put(Class.class, new ClassSerializer());
        _staticDeserializerMap.put(Number.class, new BasicDeserializer(12));
        _staticSerializerMap.put(BigDecimal.class, new StringValueSerializer());
        try {
            _staticDeserializerMap.put(BigDecimal.class, new StringValueDeserializer(BigDecimal.class));
            _staticDeserializerMap.put(BigInteger.class, new BigIntegerDeserializer());
        } catch (Throwable th) {
        }
        _staticSerializerMap.put(UUID.class, new StringValueSerializer());
        _staticDeserializerMap.put(UUID.class, new UUIDDeserializer());
        _staticSerializerMap.put(File.class, new StringValueSerializer());
        try {
            _staticDeserializerMap.put(File.class, new StringValueDeserializer(File.class));
        } catch (Throwable th2) {
        }
        _staticSerializerMap.put(ObjectName.class, new StringValueSerializer());
        try {
            _staticDeserializerMap.put(ObjectName.class, new StringValueDeserializer(ObjectName.class));
        } catch (Throwable th3) {
        }
        _staticSerializerMap.put(java.sql.Date.class, new SqlDateSerializer());
        _staticSerializerMap.put(Time.class, new SqlDateSerializer());
        _staticSerializerMap.put(Timestamp.class, new SqlDateSerializer());
        _staticSerializerMap.put(InputStream.class, new InputStreamSerializer());
        _staticDeserializerMap.put(InputStream.class, new InputStreamDeserializer());
        try {
            _staticDeserializerMap.put(java.sql.Date.class, new SqlDateDeserializer(java.sql.Date.class));
            _staticDeserializerMap.put(Time.class, new SqlDateDeserializer(Time.class));
            _staticDeserializerMap.put(Timestamp.class, new SqlDateDeserializer(Timestamp.class));
        } catch (Throwable e) {
            e.printStackTrace();
        }
        try {
            _staticDeserializerMap.put(StackTraceElement.class, new StackTraceElementDeserializer());
        } catch (Throwable th4) {
        }
        try {
            if (isJava8()) {
                _staticSerializerMap.put(Class.forName("java.time.LocalTime"), Java8TimeSerializer.create(LocalTimeHandle.class));
                _staticSerializerMap.put(Class.forName("java.time.LocalDate"), Java8TimeSerializer.create(LocalDateHandle.class));
                _staticSerializerMap.put(Class.forName("java.time.LocalDateTime"), Java8TimeSerializer.create(LocalDateTimeHandle.class));
                _staticSerializerMap.put(Class.forName("java.time.Instant"), Java8TimeSerializer.create(InstantHandle.class));
                _staticSerializerMap.put(Class.forName("java.time.Duration"), Java8TimeSerializer.create(DurationHandle.class));
                _staticSerializerMap.put(Class.forName("java.time.Period"), Java8TimeSerializer.create(PeriodHandle.class));
                _staticSerializerMap.put(Class.forName("java.time.Year"), Java8TimeSerializer.create(YearHandle.class));
                _staticSerializerMap.put(Class.forName("java.time.YearMonth"), Java8TimeSerializer.create(YearMonthHandle.class));
                _staticSerializerMap.put(Class.forName("java.time.MonthDay"), Java8TimeSerializer.create(MonthDayHandle.class));
                _staticSerializerMap.put(Class.forName("java.time.OffsetDateTime"), Java8TimeSerializer.create(OffsetDateTimeHandle.class));
                _staticSerializerMap.put(Class.forName("java.time.ZoneOffset"), Java8TimeSerializer.create(ZoneOffsetHandle.class));
                _staticSerializerMap.put(Class.forName("java.time.OffsetTime"), Java8TimeSerializer.create(OffsetTimeHandle.class));
                _staticSerializerMap.put(Class.forName("java.time.ZonedDateTime"), Java8TimeSerializer.create(ZonedDateTimeHandle.class));
            }
        } catch (Throwable t) {
            log.warning(String.valueOf(t.getCause()));
        }
        PRESENT = new Object();
    }

    public SerializerFactory() {
        this(Thread.currentThread().getContextClassLoader());
    }

    public SerializerFactory(ClassLoader loader) {
        this._factories = new ArrayList();
        this._typeNotFoundDeserializerMap = new ConcurrentHashMap(8);
        this._loader = loader;
    }

    public Class<?> loadSerializedClass(String className) throws ClassNotFoundException {
        return getClassFactory().load(className);
    }

    public ClassFactory getClassFactory() {
        ClassFactory classFactory;
        synchronized (this) {
            if (this._classFactory == null) {
                this._classFactory = new ClassFactory(getClassLoader());
            }
            classFactory = this._classFactory;
        }
        return classFactory;
    }

    private static void addBasic(Class cl, String typeName, int type) {
        _staticSerializerMap.put(cl, new BasicSerializer(type));
        BasicDeserializer basicDeserializer = new BasicDeserializer(type);
        _staticDeserializerMap.put(cl, basicDeserializer);
        _staticTypeMap.put(typeName, basicDeserializer);
    }

    public ClassLoader getClassLoader() {
        return this._loader;
    }

    public void setSendCollectionType(boolean isSendType) {
        if (this._collectionSerializer == null) {
            this._collectionSerializer = new CollectionSerializer();
        }
        this._collectionSerializer.setSendJavaType(isSendType);
        if (this._mapSerializer == null) {
            this._mapSerializer = new MapSerializer();
        }
        this._mapSerializer.setSendJavaType(isSendType);
    }

    public void addFactory(AbstractSerializerFactory factory) {
        this._factories.add(factory);
    }

    public boolean isAllowNonSerializable() {
        return this._isAllowNonSerializable;
    }

    public void setAllowNonSerializable(boolean allow) {
        this._isAllowNonSerializable = allow;
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractSerializerFactory
    public Serializer getSerializer(Class cl) throws HessianProtocolException {
        Serializer serializer = (Serializer) _staticSerializerMap.get(cl);
        if (serializer != null) {
            return serializer;
        }
        if (this._cachedSerializerMap != null) {
            serializer = (Serializer) this._cachedSerializerMap.get(cl);
            if (serializer != null) {
                return serializer;
            }
        }
        for (int i = 0; serializer == null && this._factories != null && i < this._factories.size(); i++) {
            AbstractSerializerFactory factory = (AbstractSerializerFactory) this._factories.get(i);
            serializer = factory.getSerializer(cl);
        }
        if (serializer == null) {
            if (isZoneId(cl)) {
                serializer = ZoneIdSerializer.getInstance();
            } else if (isEnumSet(cl)) {
                serializer = EnumSetSerializer.getInstance();
            } else if (JavaSerializer.getWriteReplace(cl) != null) {
                serializer = new JavaSerializer(cl, this._loader);
            } else if (HessianRemoteObject.class.isAssignableFrom(cl)) {
                serializer = new RemoteSerializer();
            } else if (Map.class.isAssignableFrom(cl)) {
                if (this._mapSerializer == null) {
                    this._mapSerializer = new MapSerializer();
                }
                serializer = this._mapSerializer;
            } else if (Collection.class.isAssignableFrom(cl)) {
                if (this._collectionSerializer == null) {
                    this._collectionSerializer = new CollectionSerializer();
                }
                serializer = this._collectionSerializer;
            } else if (cl.isArray()) {
                serializer = new ArraySerializer();
            } else if (Throwable.class.isAssignableFrom(cl)) {
                serializer = new ThrowableSerializer(cl, getClassLoader());
            } else if (InputStream.class.isAssignableFrom(cl)) {
                serializer = new InputStreamSerializer();
            } else if (Iterator.class.isAssignableFrom(cl)) {
                serializer = IteratorSerializer.create();
            } else if (Enumeration.class.isAssignableFrom(cl)) {
                serializer = EnumerationSerializer.create();
            } else if (Calendar.class.isAssignableFrom(cl)) {
                serializer = CalendarSerializer.create();
            } else if (Locale.class.isAssignableFrom(cl)) {
                serializer = LocaleSerializer.create();
            } else if (Enum.class.isAssignableFrom(cl)) {
                serializer = new EnumSerializer(cl);
            } else if (BitSet.class.isAssignableFrom(cl)) {
                serializer = BitSetSerializer.create();
            }
        }
        if (serializer == null) {
            serializer = getDefaultSerializer(cl);
        }
        if (this._cachedSerializerMap == null) {
            this._cachedSerializerMap = new ConcurrentHashMap(8);
        }
        this._cachedSerializerMap.put(cl, serializer);
        return serializer;
    }

    protected Serializer getDefaultSerializer(Class cl) {
        if (this._defaultSerializer != null) {
            return this._defaultSerializer;
        }
        if (!Serializable.class.isAssignableFrom(cl) && !this._isAllowNonSerializable) {
            throw new IllegalStateException("Serialized class " + cl.getName() + " must implement java.io.Serializable");
        }
        return new JavaSerializer(cl, this._loader);
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractSerializerFactory
    public Deserializer getDeserializer(Class cl) throws HessianProtocolException {
        Deserializer deserializer = (Deserializer) _staticDeserializerMap.get(cl);
        if (deserializer != null) {
            return deserializer;
        }
        if (this._cachedDeserializerMap != null) {
            deserializer = (Deserializer) this._cachedDeserializerMap.get(cl);
            if (deserializer != null) {
                return deserializer;
            }
        }
        for (int i = 0; deserializer == null && this._factories != null && i < this._factories.size(); i++) {
            AbstractSerializerFactory factory = (AbstractSerializerFactory) this._factories.get(i);
            deserializer = factory.getDeserializer(cl);
        }
        if (deserializer == null) {
            if (Collection.class.isAssignableFrom(cl)) {
                deserializer = new CollectionDeserializer(cl);
            } else if (Map.class.isAssignableFrom(cl)) {
                deserializer = new MapDeserializer(cl);
            } else if (cl.isInterface()) {
                deserializer = new ObjectDeserializer(cl);
            } else if (cl.isArray()) {
                deserializer = new ArrayDeserializer(cl.getComponentType());
            } else if (Enumeration.class.isAssignableFrom(cl)) {
                deserializer = EnumerationDeserializer.create();
            } else if (Enum.class.isAssignableFrom(cl)) {
                deserializer = new EnumDeserializer(cl);
            } else if (Class.class.equals(cl)) {
                deserializer = new ClassDeserializer(this._loader);
            } else {
                deserializer = getDefaultDeserializer(cl);
            }
        }
        if (this._cachedDeserializerMap == null) {
            this._cachedDeserializerMap = new ConcurrentHashMap(8);
        }
        this._cachedDeserializerMap.put(cl, deserializer);
        return deserializer;
    }

    protected Deserializer getDefaultDeserializer(Class cl) {
        if (!Serializable.class.isAssignableFrom(cl) && !this._isAllowNonSerializable) {
            throw new IllegalStateException("Serialized class " + cl.getName() + " must implement java.io.Serializable");
        }
        return new JavaDeserializer(cl);
    }

    public Object readList(AbstractHessianInput in, int length, String type) throws HessianProtocolException, IOException {
        Deserializer deserializer = getDeserializer(type);
        if (deserializer != null) {
            return deserializer.readList(in, length);
        }
        return new CollectionDeserializer(ArrayList.class).readList(in, length);
    }

    public Object readMap(AbstractHessianInput in, String type) throws HessianProtocolException, IOException {
        return readMap(in, type, null, null);
    }

    public Object readMap(AbstractHessianInput in, String type, Class<?> expectKeyType, Class<?> expectValueType) throws HessianProtocolException, IOException {
        Deserializer deserializer = getDeserializer(type);
        if (deserializer != null) {
            return deserializer.readMap(in);
        }
        if (this._hashMapDeserializer != null) {
            return this._hashMapDeserializer.readMap(in, expectKeyType, expectValueType);
        }
        this._hashMapDeserializer = new MapDeserializer(HashMap.class);
        return this._hashMapDeserializer.readMap(in, expectKeyType, expectValueType);
    }

    public Object readObject(AbstractHessianInput in, String type, String[] fieldNames) throws HessianProtocolException, IOException {
        Deserializer deserializer = getDeserializer(type);
        if (deserializer != null) {
            return deserializer.readObject(in, fieldNames);
        }
        if (this._hashMapDeserializer != null) {
            return this._hashMapDeserializer.readObject(in, fieldNames);
        }
        this._hashMapDeserializer = new MapDeserializer(HashMap.class);
        return this._hashMapDeserializer.readObject(in, fieldNames);
    }

    public Deserializer getObjectDeserializer(String type, Class cl) throws HessianProtocolException {
        Deserializer reader = getObjectDeserializer(type);
        if (cl == null || cl.equals(reader.getType()) || cl.isAssignableFrom(reader.getType()) || HessianHandle.class.isAssignableFrom(reader.getType())) {
            return reader;
        }
        if (log.isLoggable(Level.FINE)) {
            log.fine("hessian: expected '" + cl.getName() + "' at '" + type + "' (" + reader.getType().getName() + ")");
        }
        return getDeserializer(cl);
    }

    public Deserializer getObjectDeserializer(String type) throws HessianProtocolException {
        Deserializer deserializer = getDeserializer(type);
        if (deserializer != null) {
            return deserializer;
        }
        if (this._hashMapDeserializer != null) {
            return this._hashMapDeserializer;
        }
        this._hashMapDeserializer = new MapDeserializer(HashMap.class);
        return this._hashMapDeserializer;
    }

    public Deserializer getListDeserializer(String type, Class cl) throws HessianProtocolException {
        Deserializer reader = getListDeserializer(type);
        if (cl == null || cl.equals(reader.getType()) || cl.isAssignableFrom(reader.getType())) {
            return reader;
        }
        if (log.isLoggable(Level.FINE)) {
            log.fine("hessian: expected '" + cl.getName() + "' at '" + type + "' (" + reader.getType().getName() + ")");
        }
        return getDeserializer(cl);
    }

    public Deserializer getListDeserializer(String type) throws HessianProtocolException {
        Deserializer deserializer = getDeserializer(type);
        if (deserializer != null) {
            return deserializer;
        }
        if (this._arrayListDeserializer != null) {
            return this._arrayListDeserializer;
        }
        this._arrayListDeserializer = new CollectionDeserializer(ArrayList.class);
        return this._arrayListDeserializer;
    }

    public Deserializer getDeserializer(String type) throws HessianProtocolException {
        Deserializer deserializer;
        if (type == null || type.equals("") || this._typeNotFoundDeserializerMap.containsKey(type)) {
            return null;
        }
        if (this._cachedTypeDeserializerMap != null && (deserializer = (Deserializer) this._cachedTypeDeserializerMap.get(type)) != null) {
            return deserializer;
        }
        Deserializer deserializer2 = (Deserializer) _staticTypeMap.get(type);
        if (deserializer2 != null) {
            return deserializer2;
        }
        if (type.startsWith("[")) {
            Deserializer subDeserializer = getDeserializer(type.substring(1));
            deserializer2 = subDeserializer != null ? new ArrayDeserializer(subDeserializer.getType()) : new ArrayDeserializer(Object.class);
        } else if (_unrecognizedTypeCache.get(type) == null) {
            try {
                Class cl = loadSerializedClass(type);
                deserializer2 = getDeserializer(cl);
            } catch (Exception e) {
                log.warning("Hessian/Burlap: '" + type + "' is an unknown class in " + this._loader + ":\n" + e);
                this._typeNotFoundDeserializerMap.put(type, PRESENT);
                log.log(Level.FINER, e.toString(), (Throwable) e);
                _unrecognizedTypeCache.put(type, new AtomicLong(1L));
            }
        } else {
            ((AtomicLong) _unrecognizedTypeCache.get(type)).incrementAndGet();
            if (((AtomicLong) _unrecognizedTypeCache.get(type)).get() % ExponentialBackOff.DEFAULT_INITIAL_INTERVAL == 0) {
                ((AtomicLong) _unrecognizedTypeCache.get(type)).getAndSet(1L);
            }
        }
        if (deserializer2 != null) {
            if (this._cachedTypeDeserializerMap == null) {
                this._cachedTypeDeserializerMap = new ConcurrentHashMap(8);
            }
            this._cachedTypeDeserializerMap.put(type, deserializer2);
        }
        return deserializer2;
    }

    private static boolean isZoneId(Class cl) {
        try {
            if (isJava8()) {
                if (Class.forName("java.time.ZoneId").isAssignableFrom(cl)) {
                    return true;
                }
            }
            return false;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private static boolean isEnumSet(Class cl) {
        return EnumSet.class.isAssignableFrom(cl);
    }

    private static boolean isJava8() {
        String javaVersion = System.getProperty("java.specification.version");
        return Double.valueOf(javaVersion).doubleValue() >= 1.8d;
    }
}
