package cn.hutool.core.convert.impl;

import cn.hutool.core.convert.AbstractConverter;
import java.time.Period;
import java.time.temporal.TemporalAmount;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/convert/impl/PeriodConverter.class */
public class PeriodConverter extends AbstractConverter<Period> {
    private static final long serialVersionUID = 1;

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // cn.hutool.core.convert.AbstractConverter
    public Period convertInternal(Object value) {
        if (value instanceof TemporalAmount) {
            return Period.from((TemporalAmount) value);
        }
        if (value instanceof Integer) {
            return Period.ofDays(((Integer) value).intValue());
        }
        return Period.parse(convertToStr(value));
    }
}
