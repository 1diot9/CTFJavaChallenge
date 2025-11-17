package org.jooq.impl;

import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Param;
import org.jooq.ParamMode;
import org.jooq.conf.ParamType;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/LazyVal.class */
public final class LazyVal<T> extends AbstractParamX<T> {
    private final Field<T> field;
    private transient AbstractParamX<T> delegate;

    /* JADX INFO: Access modifiers changed from: package-private */
    public LazyVal(Field<T> field) {
        super(Names.N_VALUE, field.getDataType());
        this.field = field;
    }

    private final void init() {
        if (this.delegate == null) {
            this.delegate = (AbstractParamX) DSL.val((Object) null, this.field);
        }
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        init();
        ctx.visit((Field<?>) this.delegate);
    }

    @Override // org.jooq.Param
    public final String getParamName() {
        if (this.delegate == null) {
            return null;
        }
        init();
        return this.delegate.getParamName();
    }

    @Override // org.jooq.Param
    public final T getValue() {
        if (this.delegate == null) {
            return null;
        }
        init();
        return this.delegate.getValue();
    }

    @Override // org.jooq.impl.AbstractParamX
    public final void setConverted0(Object value) {
        init();
        this.delegate.setConverted0(value);
    }

    @Override // org.jooq.impl.AbstractParamX
    public final void setInline0(boolean inline) {
        init();
        this.delegate.setInline0(inline);
    }

    @Override // org.jooq.Param
    public final boolean isInline() {
        if (this.delegate == null) {
            return false;
        }
        init();
        return this.delegate.isInline();
    }

    @Override // org.jooq.Param
    public final ParamType getParamType() {
        if (this.delegate == null) {
            return ParamType.INDEXED;
        }
        init();
        return this.delegate.getParamType();
    }

    @Override // org.jooq.Param
    public final ParamMode getParamMode() {
        if (this.delegate == null) {
            return ParamMode.IN;
        }
        init();
        return this.delegate.getParamMode();
    }

    @Override // org.jooq.Param
    public final T $value() {
        return this.delegate.$value();
    }

    @Override // org.jooq.Param
    public final Param<T> $value(T value) {
        return this.delegate.$value(value);
    }

    @Override // org.jooq.Param
    public final boolean $inline() {
        return this.delegate.$inline();
    }

    @Override // org.jooq.Param
    public final Param<T> $inline(boolean inline) {
        return this.delegate.$inline(inline);
    }
}
