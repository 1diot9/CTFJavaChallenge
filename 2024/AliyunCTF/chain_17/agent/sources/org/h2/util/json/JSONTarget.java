package org.h2.util.json;

import java.math.BigDecimal;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/util/json/JSONTarget.class */
public abstract class JSONTarget<R> {
    public abstract void startObject();

    public abstract void endObject();

    public abstract void startArray();

    public abstract void endArray();

    public abstract void member(String str);

    public abstract void valueNull();

    public abstract void valueFalse();

    public abstract void valueTrue();

    public abstract void valueNumber(BigDecimal bigDecimal);

    public abstract void valueString(String str);

    public abstract boolean isPropertyExpected();

    public abstract boolean isValueSeparatorExpected();

    public abstract R getResult();
}
