package org.jooq.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.jooq.Binding;
import org.jooq.Catalog;
import org.jooq.Check;
import org.jooq.Configuration;
import org.jooq.Context;
import org.jooq.ContextConverter;
import org.jooq.DataType;
import org.jooq.Domain;
import org.jooq.Name;
import org.jooq.Schema;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DomainImpl.class */
public class DomainImpl<T> extends AbstractNamed implements Domain<T>, QOM.UNotYetImplemented {
    private final Schema schema;
    private final Check<?>[] checks;
    private final DataType<T> type;

    /* JADX INFO: Access modifiers changed from: package-private */
    public DomainImpl(Schema schema, Name name, DataType<T> type, Check<?>... checks) {
        super(qualify(schema, name), null);
        this.schema = schema;
        this.checks = checks;
        this.type = new DomainDataType(this, type);
    }

    @Override // org.jooq.Qualified
    public final Catalog getCatalog() {
        if (getSchema() == null) {
            return null;
        }
        return getSchema().getCatalog();
    }

    @Override // org.jooq.Qualified
    public final Schema getSchema() {
        return this.schema;
    }

    @Override // org.jooq.Domain
    public final List<Check<?>> getChecks() {
        return Collections.unmodifiableList(Arrays.asList(this.checks));
    }

    @Override // org.jooq.Typed
    public final ContextConverter<?, T> getConverter() {
        return this.type.getConverter();
    }

    @Override // org.jooq.Typed
    public final Binding<?, T> getBinding() {
        return this.type.getBinding();
    }

    @Override // org.jooq.Typed
    public final Class<T> getType() {
        return this.type.getType();
    }

    @Override // org.jooq.Typed
    public final DataType<T> getDataType() {
        return this.type;
    }

    @Override // org.jooq.Typed
    public final DataType<T> getDataType(Configuration configuration) {
        return this.type.getDataType(configuration);
    }

    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        ctx.visit(getQualifiedName());
    }

    @Override // org.jooq.Typed
    public final DataType<T> $dataType() {
        return this.type;
    }

    @Override // org.jooq.Qualified
    public final Schema $schema() {
        return getSchema();
    }
}
