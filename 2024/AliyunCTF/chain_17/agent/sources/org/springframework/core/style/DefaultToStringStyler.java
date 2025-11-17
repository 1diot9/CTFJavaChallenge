package org.springframework.core.style;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/style/DefaultToStringStyler.class */
public class DefaultToStringStyler implements ToStringStyler {
    private final ValueStyler valueStyler;

    public DefaultToStringStyler(ValueStyler valueStyler) {
        Assert.notNull(valueStyler, "ValueStyler must not be null");
        this.valueStyler = valueStyler;
    }

    protected final ValueStyler getValueStyler() {
        return this.valueStyler;
    }

    @Override // org.springframework.core.style.ToStringStyler
    public void styleStart(StringBuilder buffer, Object obj) {
        if (!obj.getClass().isArray()) {
            buffer.append('[').append(ClassUtils.getShortName(obj.getClass()));
            styleIdentityHashCode(buffer, obj);
        } else {
            buffer.append('[');
            styleIdentityHashCode(buffer, obj);
            buffer.append(' ');
            styleValue(buffer, obj);
        }
    }

    private void styleIdentityHashCode(StringBuilder buffer, Object obj) {
        buffer.append('@');
        buffer.append(ObjectUtils.getIdentityHexString(obj));
    }

    @Override // org.springframework.core.style.ToStringStyler
    public void styleEnd(StringBuilder buffer, Object o) {
        buffer.append(']');
    }

    @Override // org.springframework.core.style.ToStringStyler
    public void styleField(StringBuilder buffer, String fieldName, @Nullable Object value) {
        styleFieldStart(buffer, fieldName);
        styleValue(buffer, value);
        styleFieldEnd(buffer, fieldName);
    }

    protected void styleFieldStart(StringBuilder buffer, String fieldName) {
        buffer.append(' ').append(fieldName).append(" = ");
    }

    protected void styleFieldEnd(StringBuilder buffer, String fieldName) {
    }

    @Override // org.springframework.core.style.ToStringStyler
    public void styleValue(StringBuilder buffer, @Nullable Object value) {
        buffer.append(this.valueStyler.style(value));
    }

    @Override // org.springframework.core.style.ToStringStyler
    public void styleFieldSeparator(StringBuilder buffer) {
        buffer.append(',');
    }
}
