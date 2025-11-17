package org.jooq.impl;

import org.jooq.Context;
import org.jooq.Name;
import org.jooq.QueryPartInternal;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/LazyName.class */
final class LazyName extends AbstractName {
    final LazySupplier<Name> supplier;
    transient Name name;

    /* JADX INFO: Access modifiers changed from: package-private */
    public LazyName(LazySupplier<Name> supplier) {
        this.supplier = supplier;
    }

    private final Name name() {
        if (this.name == null) {
            this.name = this.supplier.get();
        }
        return this.name;
    }

    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        ((QueryPartInternal) name()).accept(ctx);
    }

    @Override // org.jooq.Name
    public final String first() {
        return name().first();
    }

    @Override // org.jooq.Name
    public final String last() {
        return name().last();
    }

    @Override // org.jooq.Name
    public final boolean empty() {
        return name().empty();
    }

    @Override // org.jooq.Name
    public final boolean qualified() {
        return name().qualified();
    }

    @Override // org.jooq.Name
    public final boolean qualifierQualified() {
        return name().qualifierQualified();
    }

    @Override // org.jooq.Name
    public final Name qualifier() {
        return name().qualifier();
    }

    @Override // org.jooq.Name
    public final Name unqualifiedName() {
        return name().unqualifiedName();
    }

    @Override // org.jooq.Name
    public final Name.Quoted quoted() {
        return name().quoted();
    }

    @Override // org.jooq.Name
    public final Name quotedName() {
        return name().quotedName();
    }

    @Override // org.jooq.Name
    public final Name unquotedName() {
        return name().unquotedName();
    }

    @Override // org.jooq.Name
    public final Name[] parts() {
        return name().parts();
    }

    @Override // org.jooq.Name
    public final String[] getName() {
        return name().getName();
    }

    @Override // org.jooq.impl.AbstractName, org.jooq.impl.AbstractQueryPart, org.jooq.QueryPart
    public int hashCode() {
        return name().hashCode();
    }
}
