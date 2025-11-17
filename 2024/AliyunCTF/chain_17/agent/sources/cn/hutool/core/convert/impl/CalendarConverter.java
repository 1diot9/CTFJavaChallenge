package cn.hutool.core.convert.impl;

import cn.hutool.core.convert.AbstractConverter;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import java.util.Calendar;
import java.util.Date;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/convert/impl/CalendarConverter.class */
public class CalendarConverter extends AbstractConverter<Calendar> {
    private static final long serialVersionUID = 1;
    private String format;

    public String getFormat() {
        return this.format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // cn.hutool.core.convert.AbstractConverter
    /* renamed from: convertInternal */
    public Calendar convertInternal2(Object value) {
        if (value instanceof Date) {
            return DateUtil.calendar((Date) value);
        }
        if (value instanceof Long) {
            return DateUtil.calendar(((Long) value).longValue());
        }
        String valueStr = convertToStr(value);
        return DateUtil.calendar(StrUtil.isBlank(this.format) ? DateUtil.parse(valueStr) : DateUtil.parse(valueStr, this.format));
    }
}
