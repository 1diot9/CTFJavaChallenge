package org.jooq.impl;

import org.jooq.DataType;
import org.jooq.Name;
import org.jooq.Param;
import org.jooq.tools.JooqLogger;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AbstractParamX.class */
public abstract class AbstractParamX<T> extends AbstractField<T> implements Param<T> {
    private static final JooqLogger log = JooqLogger.getLogger((Class<?>) AbstractParam.class);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void setConverted0(Object obj);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void setInline0(boolean z);

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractParamX(Name name, DataType<T> type) {
        super(name, type);
    }

    @Override // org.jooq.Param
    @Deprecated
    public final void setValue(T value) {
        log.warn("Deprecation", "org.jooq.Param will soon be made immutable. It is recommended to no longer use its deprecated, mutating methods.", new UnsupportedOperationException("Param.setValue"));
        setConverted0(value);
    }

    final void setValue0(T value) {
        setConverted0(value);
    }

    @Override // org.jooq.Param
    @Deprecated
    public final void setConverted(Object value) {
        log.warn("Deprecation", "org.jooq.Param will soon be made immutable. It is recommended to no longer use its deprecated, mutating methods.", new UnsupportedOperationException("Param.setConverted"));
        setConverted0(value);
    }

    @Override // org.jooq.Param
    @Deprecated
    public final void setInline(boolean inline) {
        log.warn("Deprecation", "org.jooq.Param will soon be made immutable. It is recommended to no longer use its deprecated, mutating methods.", new UnsupportedOperationException("Param.setInline"));
        setInline0(inline);
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final boolean generatesCast() {
        return true;
    }
}
