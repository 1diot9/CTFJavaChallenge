package org.jooq.impl;

import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.Param;
import org.jooq.ParamMode;
import org.jooq.conf.ParamType;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
@Deprecated
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ConvertedVal.class */
public final class ConvertedVal<T> extends AbstractParamX<T> implements QOM.UNotYetImplemented {
    final AbstractParamX<?> delegate;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ConvertedVal(AbstractParamX<?> delegate, DataType<T> type) {
        super(delegate.getUnqualifiedName(), type);
        AbstractParamX<?> abstractParamX;
        if (delegate instanceof ConvertedVal) {
            ConvertedVal<?> c = (ConvertedVal) delegate;
            abstractParamX = c.delegate;
        } else {
            abstractParamX = delegate;
        }
        this.delegate = abstractParamX;
    }

    @Override // org.jooq.impl.AbstractField, org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        ctx.visit((Field<?>) this.delegate);
    }

    @Override // org.jooq.Param
    public final String getParamName() {
        return this.delegate.getParamName();
    }

    @Override // org.jooq.Param
    public final T getValue() {
        return getDataType().convert(this.delegate.getValue());
    }

    @Override // org.jooq.impl.AbstractParamX
    public final void setConverted0(Object value) {
        this.delegate.setConverted0(value);
    }

    @Override // org.jooq.impl.AbstractParamX
    public final void setInline0(boolean inline) {
        this.delegate.setInline0(inline);
    }

    @Override // org.jooq.Param
    public final boolean isInline() {
        return this.delegate.isInline();
    }

    @Override // org.jooq.Param
    public final ParamType getParamType() {
        return this.delegate.getParamType();
    }

    @Override // org.jooq.Param
    public final ParamMode getParamMode() {
        return this.delegate.getParamMode();
    }

    @Override // org.jooq.Param
    public final T $value() {
        return getValue();
    }

    @Override // org.jooq.Param
    public final Param<T> $value(T t) {
        return (Param<T>) this.delegate.$value(this.delegate.getDataType().convert(t));
    }

    @Override // org.jooq.Param
    public final boolean $inline() {
        return this.delegate.$inline();
    }

    @Override // org.jooq.Param
    public final Param<T> $inline(boolean inline) {
        throw new QOM.NotYetImplementedException();
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public String toString() {
        return this.delegate.toString();
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public int hashCode() {
        return this.delegate.hashCode();
    }

    @Override // org.jooq.impl.AbstractNamed, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public boolean equals(Object that) {
        return this.delegate.equals(that);
    }
}
