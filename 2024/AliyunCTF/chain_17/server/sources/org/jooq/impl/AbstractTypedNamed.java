package org.jooq.impl;

import org.jooq.Binding;
import org.jooq.Comment;
import org.jooq.Configuration;
import org.jooq.ContextConverter;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.Generator;
import org.jooq.Name;
import org.jooq.Typed;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AbstractTypedNamed.class */
public abstract class AbstractTypedNamed<T> extends AbstractNamed implements Typed<T> {
    private final DataType<T> type;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractTypedNamed(Name name, Comment comment, DataType<T> type) {
        super(name, comment);
        DataType<T> dataType;
        if (type.computed() && !(this instanceof TypedReference)) {
            dataType = type.generatedAlwaysAs((Generator) null);
        } else if (type.defaulted() && !(this instanceof TypedReference)) {
            dataType = type.default_((Field) null);
        } else {
            dataType = type;
        }
        this.type = dataType;
    }

    @Override // org.jooq.Typed
    public final ContextConverter<?, T> getConverter() {
        return getDataType().getConverter();
    }

    @Override // org.jooq.Typed
    public final Binding<?, T> getBinding() {
        return getDataType().getBinding();
    }

    @Override // org.jooq.Typed
    public final Class<T> getType() {
        return getDataType().getType();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DataType<?> getExpressionDataType() {
        return getDataType();
    }

    @Override // org.jooq.Typed
    public final DataType<T> getDataType() {
        return this.type;
    }

    @Override // org.jooq.Typed
    public final DataType<T> getDataType(Configuration configuration) {
        return getDataType().getDataType(configuration);
    }

    @Override // org.jooq.Typed
    public final DataType<T> $dataType() {
        return getDataType();
    }
}
