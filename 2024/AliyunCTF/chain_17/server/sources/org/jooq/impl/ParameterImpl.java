package org.jooq.impl;

import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.Name;
import org.jooq.ParamMode;
import org.jooq.Parameter;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ParameterImpl.class */
public final class ParameterImpl<T> extends AbstractField<T> implements Parameter<T>, QOM.UEmpty, TypedReference<T> {
    private final ParamMode paramMode;
    private final boolean isDefaulted;
    private final boolean isUnnamed;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ParameterImpl(ParamMode paramMode, Name name, DataType<T> type) {
        this(paramMode, name, type, type.defaulted(), name == null || name.empty());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Deprecated
    public ParameterImpl(ParamMode paramMode, Name name, DataType<T> type, boolean isDefaulted, boolean isUnnamed) {
        super(name, type);
        this.paramMode = paramMode;
        this.isDefaulted = isDefaulted;
        this.isUnnamed = isUnnamed;
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        ctx.visit(getUnqualifiedName());
    }

    @Override // org.jooq.Parameter
    public final ParamMode getParamMode() {
        return this.paramMode;
    }

    @Override // org.jooq.Parameter
    public final boolean isDefaulted() {
        return this.isDefaulted;
    }

    @Override // org.jooq.Parameter
    public final boolean isUnnamed() {
        return this.isUnnamed;
    }
}
