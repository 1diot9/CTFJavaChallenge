package cn.hutool.core.convert.impl;

import cn.hutool.core.convert.AbstractConverter;
import java.time.Duration;
import java.time.temporal.TemporalAmount;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/convert/impl/DurationConverter.class */
public class DurationConverter extends AbstractConverter<Duration> {
    private static final long serialVersionUID = 1;

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // cn.hutool.core.convert.AbstractConverter
    public Duration convertInternal(Object value) {
        if (value instanceof TemporalAmount) {
            return Duration.from((TemporalAmount) value);
        }
        if (value instanceof Long) {
            return Duration.ofMillis(((Long) value).longValue());
        }
        return Duration.parse(convertToStr(value));
    }
}
