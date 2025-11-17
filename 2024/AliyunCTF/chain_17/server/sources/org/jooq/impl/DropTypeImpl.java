package org.jooq.impl;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.jooq.Configuration;
import org.jooq.Context;
import org.jooq.DropTypeFinalStep;
import org.jooq.DropTypeStep;
import org.jooq.Function3;
import org.jooq.SQLDialect;
import org.jooq.Type;
import org.jooq.impl.QOM;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DropTypeImpl.class */
final class DropTypeImpl extends AbstractDDLQuery implements QOM.DropType, DropTypeStep, DropTypeFinalStep {
    final QueryPartListView<? extends Type<?>> types;
    final boolean ifExists;
    QOM.Cascade cascade;
    private static final Set<SQLDialect> NO_SUPPORT_IF_EXISTS = SQLDialect.supportedUntil(new SQLDialect[0]);

    /* JADX INFO: Access modifiers changed from: package-private */
    public DropTypeImpl(Configuration configuration, Collection<? extends Type<?>> types, boolean ifExists) {
        this(configuration, types, ifExists, null);
    }

    DropTypeImpl(Configuration configuration, Collection<? extends Type<?>> types, boolean ifExists, QOM.Cascade cascade) {
        super(configuration);
        this.types = new QueryPartList(types);
        this.ifExists = ifExists;
        this.cascade = cascade;
    }

    @Override // org.jooq.DropTypeStep
    public final DropTypeImpl cascade() {
        this.cascade = QOM.Cascade.CASCADE;
        return this;
    }

    @Override // org.jooq.DropTypeStep
    public final DropTypeImpl restrict() {
        this.cascade = QOM.Cascade.RESTRICT;
        return this;
    }

    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        accept0(ctx);
    }

    private final boolean supportsIfExists(Context<?> ctx) {
        return !NO_SUPPORT_IF_EXISTS.contains(ctx.dialect());
    }

    private final void accept0(Context<?> ctx) {
        if (this.ifExists && !supportsIfExists(ctx)) {
            Tools.tryCatch(ctx, DDLStatementType.DROP_TYPE, c -> {
                accept1(c);
            });
        } else {
            accept1(ctx);
        }
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v15, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v18, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v23, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v8, types: [org.jooq.Context] */
    private final void accept1(Context<?> ctx) {
        ctx.visit(Keywords.K_DROP).sql(' ');
        ctx.visit(Keywords.K_TYPE);
        if (this.ifExists && supportsIfExists(ctx)) {
            ctx.sql(' ').visit(Keywords.K_IF_EXISTS);
        }
        ctx.sql(' ').visit(this.types);
        if (this.cascade == QOM.Cascade.CASCADE) {
            ctx.sql(' ').visit(Keywords.K_CASCADE);
        } else if (this.cascade == QOM.Cascade.RESTRICT) {
            ctx.sql(' ').visit(Keywords.K_RESTRICT);
        }
    }

    @Override // org.jooq.impl.QOM.DropType
    public final QOM.UnmodifiableList<? extends Type<?>> $types() {
        return QOM.unmodifiable((List) this.types);
    }

    @Override // org.jooq.impl.QOM.DropType
    public final boolean $ifExists() {
        return this.ifExists;
    }

    @Override // org.jooq.impl.QOM.DropType
    public final QOM.Cascade $cascade() {
        return this.cascade;
    }

    @Override // org.jooq.impl.QOM.DropType
    public final QOM.DropType $types(Collection<? extends Type<?>> newValue) {
        return $constructor().apply(newValue, Boolean.valueOf($ifExists()), $cascade());
    }

    @Override // org.jooq.impl.QOM.DropType
    public final QOM.DropType $ifExists(boolean newValue) {
        return $constructor().apply($types(), Boolean.valueOf(newValue), $cascade());
    }

    @Override // org.jooq.impl.QOM.DropType
    public final QOM.DropType $cascade(QOM.Cascade newValue) {
        return $constructor().apply($types(), Boolean.valueOf($ifExists()), newValue);
    }

    public final Function3<? super Collection<? extends Type<?>>, ? super Boolean, ? super QOM.Cascade, ? extends QOM.DropType> $constructor() {
        return (a1, a2, a3) -> {
            return new DropTypeImpl(configuration(), a1, a2.booleanValue(), a3);
        };
    }
}
