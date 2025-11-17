package cn.hutool.core.convert.impl;

import cn.hutool.core.convert.AbstractConverter;
import cn.hutool.core.convert.ConvertException;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.format.GlobalCustomFormat;
import cn.hutool.core.util.StrUtil;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/convert/impl/DateConverter.class */
public class DateConverter extends AbstractConverter<Date> {
    private static final long serialVersionUID = 1;
    private final Class<? extends Date> targetType;
    private String format;

    public DateConverter(Class<? extends Date> targetType) {
        this.targetType = targetType;
    }

    public DateConverter(Class<? extends Date> targetType, String format) {
        this.targetType = targetType;
        this.format = format;
    }

    public String getFormat() {
        return this.format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // cn.hutool.core.convert.AbstractConverter
    public Date convertInternal(Object value) {
        DateTime parse;
        if (value == null) {
            return null;
        }
        if ((value instanceof CharSequence) && StrUtil.isBlank(value.toString())) {
            return null;
        }
        if (value instanceof TemporalAccessor) {
            return wrap(DateUtil.date((TemporalAccessor) value));
        }
        if (value instanceof Calendar) {
            return wrap(DateUtil.date((Calendar) value));
        }
        if (value instanceof Number) {
            return wrap(((Number) value).longValue());
        }
        String valueStr = convertToStr(value);
        if (StrUtil.isBlank(this.format)) {
            parse = DateUtil.parse(valueStr);
        } else {
            parse = DateUtil.parse(valueStr, this.format);
        }
        DateTime dateTime = parse;
        if (null != dateTime) {
            return wrap(dateTime);
        }
        throw new ConvertException("Can not convert {}:[{}] to {}", value.getClass().getName(), value, this.targetType.getName());
    }

    private Date wrap(DateTime date) {
        if (Date.class == this.targetType) {
            return date.toJdkDate();
        }
        if (DateTime.class == this.targetType) {
            return date;
        }
        if (java.sql.Date.class == this.targetType) {
            return date.toSqlDate();
        }
        if (Time.class == this.targetType) {
            return new Time(date.getTime());
        }
        if (Timestamp.class == this.targetType) {
            return date.toTimestamp();
        }
        throw new UnsupportedOperationException(StrUtil.format("Unsupported target Date type: {}", this.targetType.getName()));
    }

    private Date wrap(long mills) {
        if (GlobalCustomFormat.FORMAT_SECONDS.equals(this.format)) {
            return DateUtil.date(mills * 1000);
        }
        if (Date.class == this.targetType) {
            return new Date(mills);
        }
        if (DateTime.class == this.targetType) {
            return DateUtil.date(mills);
        }
        if (java.sql.Date.class == this.targetType) {
            return new java.sql.Date(mills);
        }
        if (Time.class == this.targetType) {
            return new Time(mills);
        }
        if (Timestamp.class == this.targetType) {
            return new Timestamp(mills);
        }
        throw new UnsupportedOperationException(StrUtil.format("Unsupported target Date type: {}", this.targetType.getName()));
    }

    @Override // cn.hutool.core.convert.AbstractConverter
    public Class<Date> getTargetType() {
        return this.targetType;
    }
}
