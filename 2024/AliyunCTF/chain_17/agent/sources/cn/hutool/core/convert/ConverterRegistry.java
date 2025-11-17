package cn.hutool.core.convert;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.impl.ArrayConverter;
import cn.hutool.core.convert.impl.AtomicBooleanConverter;
import cn.hutool.core.convert.impl.AtomicIntegerArrayConverter;
import cn.hutool.core.convert.impl.AtomicLongArrayConverter;
import cn.hutool.core.convert.impl.AtomicReferenceConverter;
import cn.hutool.core.convert.impl.BeanConverter;
import cn.hutool.core.convert.impl.BooleanConverter;
import cn.hutool.core.convert.impl.CalendarConverter;
import cn.hutool.core.convert.impl.CharacterConverter;
import cn.hutool.core.convert.impl.CharsetConverter;
import cn.hutool.core.convert.impl.ClassConverter;
import cn.hutool.core.convert.impl.CollectionConverter;
import cn.hutool.core.convert.impl.CurrencyConverter;
import cn.hutool.core.convert.impl.DateConverter;
import cn.hutool.core.convert.impl.DurationConverter;
import cn.hutool.core.convert.impl.EnumConverter;
import cn.hutool.core.convert.impl.LocaleConverter;
import cn.hutool.core.convert.impl.MapConverter;
import cn.hutool.core.convert.impl.NumberConverter;
import cn.hutool.core.convert.impl.OptConverter;
import cn.hutool.core.convert.impl.OptionalConverter;
import cn.hutool.core.convert.impl.PathConverter;
import cn.hutool.core.convert.impl.PeriodConverter;
import cn.hutool.core.convert.impl.PrimitiveConverter;
import cn.hutool.core.convert.impl.ReferenceConverter;
import cn.hutool.core.convert.impl.StackTraceElementConverter;
import cn.hutool.core.convert.impl.StringConverter;
import cn.hutool.core.convert.impl.TemporalAccessorConverter;
import cn.hutool.core.convert.impl.TimeZoneConverter;
import cn.hutool.core.convert.impl.URIConverter;
import cn.hutool.core.convert.impl.URLConverter;
import cn.hutool.core.convert.impl.UUIDConverter;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.lang.Opt;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.map.SafeConcurrentHashMap;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.ServiceLoaderUtil;
import cn.hutool.core.util.TypeUtil;
import java.io.Serializable;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Period;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Collection;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongArray;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.concurrent.atomic.LongAdder;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/convert/ConverterRegistry.class */
public class ConverterRegistry implements Serializable {
    private static final long serialVersionUID = 1;
    private Map<Type, Converter<?>> defaultConverterMap;
    private volatile Map<Type, Converter<?>> customConverterMap;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/convert/ConverterRegistry$SingletonHolder.class */
    public static class SingletonHolder {
        private static final ConverterRegistry INSTANCE = new ConverterRegistry();

        private SingletonHolder() {
        }
    }

    public static ConverterRegistry getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public ConverterRegistry() {
        defaultConverter();
        putCustomBySpi();
    }

    private void putCustomBySpi() {
        ServiceLoaderUtil.load(Converter.class).forEach(converter -> {
            try {
                Type type = TypeUtil.getTypeArgument(ClassUtil.getClass(converter));
                if (null != type) {
                    putCustom(type, (Converter<?>) converter);
                }
            } catch (Exception e) {
            }
        });
    }

    public ConverterRegistry putCustom(Type type, Class<? extends Converter<?>> converterClass) {
        return putCustom(type, (Converter<?>) ReflectUtil.newInstance(converterClass, new Object[0]));
    }

    public ConverterRegistry putCustom(Type type, Converter<?> converter) {
        if (null == this.customConverterMap) {
            synchronized (this) {
                if (null == this.customConverterMap) {
                    this.customConverterMap = new SafeConcurrentHashMap();
                }
            }
        }
        this.customConverterMap.put(type, converter);
        return this;
    }

    public <T> Converter<T> getConverter(Type type, boolean isCustomFirst) {
        Converter<T> converter;
        if (isCustomFirst) {
            converter = getCustomConverter(type);
            if (null == converter) {
                converter = getDefaultConverter(type);
            }
        } else {
            converter = getDefaultConverter(type);
            if (null == converter) {
                converter = getCustomConverter(type);
            }
        }
        return converter;
    }

    public <T> Converter<T> getDefaultConverter(Type type) {
        if (null == this.defaultConverterMap) {
            return null;
        }
        return (Converter) this.defaultConverterMap.get(type);
    }

    public <T> Converter<T> getCustomConverter(Type type) {
        if (null == this.customConverterMap) {
            return null;
        }
        return (Converter) this.customConverterMap.get(type);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r10v0, types: [T, java.lang.Object] */
    public <T> T convert(Type type, Object obj, T t, boolean z) throws ConvertException {
        if (TypeUtil.isUnknown(type) && null == t) {
            return obj;
        }
        if (ObjectUtil.isNull(obj)) {
            return t;
        }
        if (TypeUtil.isUnknown(type)) {
            type = t.getClass();
        }
        if (type instanceof TypeReference) {
            type = ((TypeReference) type).getType();
        }
        if (obj instanceof TypeConverter) {
            return (T) ObjUtil.defaultIfNull(((TypeConverter) obj).convert(type, obj), t);
        }
        Converter<T> converter = getConverter(type, z);
        if (null != converter) {
            return converter.convert(obj, t);
        }
        Class cls = TypeUtil.getClass(type);
        if (null == cls) {
            if (null != t) {
                cls = t.getClass();
            } else {
                return obj;
            }
        }
        T t2 = (T) convertSpecial(type, cls, obj, t);
        if (null != t2) {
            return t2;
        }
        if (BeanUtil.isBean(cls)) {
            return new BeanConverter(type).convert(obj, t);
        }
        throw new ConvertException("Can not Converter from [{}] to [{}]", obj.getClass().getName(), type.getTypeName());
    }

    public <T> T convert(Type type, Object obj, T t) throws ConvertException {
        return (T) convert(type, obj, t, true);
    }

    public <T> T convert(Type type, Object obj) throws ConvertException {
        return (T) convert(type, obj, null);
    }

    /* JADX WARN: Multi-variable type inference failed */
    private <T> T convertSpecial(Type type, Class<T> cls, Object obj, T t) {
        if (null == cls) {
            return null;
        }
        if (Collection.class.isAssignableFrom(cls)) {
            return (T) new CollectionConverter(type).convert(obj, (Collection<?>) t);
        }
        if (Map.class.isAssignableFrom(cls)) {
            return (T) new MapConverter(type).convert(obj, (Map) t);
        }
        if (cls.isInstance(obj)) {
            return obj;
        }
        if (cls.isEnum()) {
            return (T) new EnumConverter(cls).convert(obj, t);
        }
        if (cls.isArray()) {
            return (T) new ArrayConverter(cls).convert(obj, t);
        }
        return null;
    }

    private ConverterRegistry defaultConverter() {
        this.defaultConverterMap = new SafeConcurrentHashMap();
        this.defaultConverterMap.put(Integer.TYPE, new PrimitiveConverter(Integer.TYPE));
        this.defaultConverterMap.put(Long.TYPE, new PrimitiveConverter(Long.TYPE));
        this.defaultConverterMap.put(Byte.TYPE, new PrimitiveConverter(Byte.TYPE));
        this.defaultConverterMap.put(Short.TYPE, new PrimitiveConverter(Short.TYPE));
        this.defaultConverterMap.put(Float.TYPE, new PrimitiveConverter(Float.TYPE));
        this.defaultConverterMap.put(Double.TYPE, new PrimitiveConverter(Double.TYPE));
        this.defaultConverterMap.put(Character.TYPE, new PrimitiveConverter(Character.TYPE));
        this.defaultConverterMap.put(Boolean.TYPE, new PrimitiveConverter(Boolean.TYPE));
        this.defaultConverterMap.put(Number.class, new NumberConverter());
        this.defaultConverterMap.put(Integer.class, new NumberConverter(Integer.class));
        this.defaultConverterMap.put(AtomicInteger.class, new NumberConverter(AtomicInteger.class));
        this.defaultConverterMap.put(Long.class, new NumberConverter(Long.class));
        this.defaultConverterMap.put(LongAdder.class, new NumberConverter(LongAdder.class));
        this.defaultConverterMap.put(AtomicLong.class, new NumberConverter(AtomicLong.class));
        this.defaultConverterMap.put(Byte.class, new NumberConverter(Byte.class));
        this.defaultConverterMap.put(Short.class, new NumberConverter(Short.class));
        this.defaultConverterMap.put(Float.class, new NumberConverter(Float.class));
        this.defaultConverterMap.put(Double.class, new NumberConverter(Double.class));
        this.defaultConverterMap.put(DoubleAdder.class, new NumberConverter(DoubleAdder.class));
        this.defaultConverterMap.put(Character.class, new CharacterConverter());
        this.defaultConverterMap.put(Boolean.class, new BooleanConverter());
        this.defaultConverterMap.put(AtomicBoolean.class, new AtomicBooleanConverter());
        this.defaultConverterMap.put(BigDecimal.class, new NumberConverter(BigDecimal.class));
        this.defaultConverterMap.put(BigInteger.class, new NumberConverter(BigInteger.class));
        this.defaultConverterMap.put(CharSequence.class, new StringConverter());
        this.defaultConverterMap.put(String.class, new StringConverter());
        this.defaultConverterMap.put(URI.class, new URIConverter());
        this.defaultConverterMap.put(URL.class, new URLConverter());
        this.defaultConverterMap.put(Calendar.class, new CalendarConverter());
        this.defaultConverterMap.put(Date.class, new DateConverter(Date.class));
        this.defaultConverterMap.put(DateTime.class, new DateConverter(DateTime.class));
        this.defaultConverterMap.put(java.sql.Date.class, new DateConverter(java.sql.Date.class));
        this.defaultConverterMap.put(Time.class, new DateConverter(Time.class));
        this.defaultConverterMap.put(Timestamp.class, new DateConverter(Timestamp.class));
        this.defaultConverterMap.put(TemporalAccessor.class, new TemporalAccessorConverter(Instant.class));
        this.defaultConverterMap.put(Instant.class, new TemporalAccessorConverter(Instant.class));
        this.defaultConverterMap.put(LocalDateTime.class, new TemporalAccessorConverter(LocalDateTime.class));
        this.defaultConverterMap.put(LocalDate.class, new TemporalAccessorConverter(LocalDate.class));
        this.defaultConverterMap.put(LocalTime.class, new TemporalAccessorConverter(LocalTime.class));
        this.defaultConverterMap.put(ZonedDateTime.class, new TemporalAccessorConverter(ZonedDateTime.class));
        this.defaultConverterMap.put(OffsetDateTime.class, new TemporalAccessorConverter(OffsetDateTime.class));
        this.defaultConverterMap.put(OffsetTime.class, new TemporalAccessorConverter(OffsetTime.class));
        this.defaultConverterMap.put(DayOfWeek.class, new TemporalAccessorConverter(DayOfWeek.class));
        this.defaultConverterMap.put(Month.class, new TemporalAccessorConverter(Month.class));
        this.defaultConverterMap.put(MonthDay.class, new TemporalAccessorConverter(MonthDay.class));
        this.defaultConverterMap.put(Period.class, new PeriodConverter());
        this.defaultConverterMap.put(Duration.class, new DurationConverter());
        this.defaultConverterMap.put(WeakReference.class, new ReferenceConverter(WeakReference.class));
        this.defaultConverterMap.put(SoftReference.class, new ReferenceConverter(SoftReference.class));
        this.defaultConverterMap.put(AtomicReference.class, new AtomicReferenceConverter());
        this.defaultConverterMap.put(AtomicIntegerArray.class, new AtomicIntegerArrayConverter());
        this.defaultConverterMap.put(AtomicLongArray.class, new AtomicLongArrayConverter());
        this.defaultConverterMap.put(Class.class, new ClassConverter());
        this.defaultConverterMap.put(TimeZone.class, new TimeZoneConverter());
        this.defaultConverterMap.put(Locale.class, new LocaleConverter());
        this.defaultConverterMap.put(Charset.class, new CharsetConverter());
        this.defaultConverterMap.put(Path.class, new PathConverter());
        this.defaultConverterMap.put(Currency.class, new CurrencyConverter());
        this.defaultConverterMap.put(UUID.class, new UUIDConverter());
        this.defaultConverterMap.put(StackTraceElement.class, new StackTraceElementConverter());
        this.defaultConverterMap.put(Optional.class, new OptionalConverter());
        this.defaultConverterMap.put(Opt.class, new OptConverter());
        return this;
    }
}
