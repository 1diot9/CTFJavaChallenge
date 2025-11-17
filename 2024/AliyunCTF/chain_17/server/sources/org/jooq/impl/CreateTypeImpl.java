package org.jooq.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import org.jooq.Configuration;
import org.jooq.Context;
import org.jooq.CreateTypeFinalStep;
import org.jooq.CreateTypeStep;
import org.jooq.Field;
import org.jooq.Function4;
import org.jooq.SQLDialect;
import org.jooq.Type;
import org.jooq.conf.ParamType;
import org.jooq.impl.QOM;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/CreateTypeImpl.class */
final class CreateTypeImpl extends AbstractDDLQuery implements QOM.CreateType, CreateTypeStep, CreateTypeFinalStep {
    final Type<?> type;
    final boolean ifNotExists;
    QueryPartListView<? extends Field<String>> values;
    QueryPartListView<? extends Field<?>> attributes;
    private static final Set<SQLDialect> NO_SUPPORT_IF_NOT_EXISTS = SQLDialect.supportedUntil(SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB);

    @Override // org.jooq.CreateTypeStep
    public /* bridge */ /* synthetic */ CreateTypeFinalStep as(Collection collection) {
        return as((Collection<? extends Field<?>>) collection);
    }

    @Override // org.jooq.CreateTypeStep
    public /* bridge */ /* synthetic */ CreateTypeFinalStep as(Field[] fieldArr) {
        return as((Field<?>[]) fieldArr);
    }

    @Override // org.jooq.CreateTypeStep
    public /* bridge */ /* synthetic */ CreateTypeFinalStep asEnum(Collection collection) {
        return asEnum((Collection<? extends Field<String>>) collection);
    }

    @Override // org.jooq.CreateTypeStep
    public /* bridge */ /* synthetic */ CreateTypeFinalStep asEnum(Field[] fieldArr) {
        return asEnum((Field<String>[]) fieldArr);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CreateTypeImpl(Configuration configuration, Type<?> type, boolean ifNotExists) {
        this(configuration, type, ifNotExists, null, null);
    }

    CreateTypeImpl(Configuration configuration, Type<?> type, boolean ifNotExists, Collection<? extends Field<String>> values, Collection<? extends Field<?>> attributes) {
        super(configuration);
        this.type = type;
        this.ifNotExists = ifNotExists;
        this.values = new QueryPartList(values);
        this.attributes = new QueryPartList(attributes);
    }

    @Override // org.jooq.CreateTypeStep
    public final CreateTypeImpl asEnum(String... values) {
        return asEnum((Collection<? extends Field<String>>) Tools.fields(values));
    }

    @Override // org.jooq.CreateTypeStep
    public final CreateTypeImpl asEnum(Field<String>... values) {
        return asEnum((Collection<? extends Field<String>>) Arrays.asList(values));
    }

    @Override // org.jooq.CreateTypeStep
    public final CreateTypeImpl asEnum(Collection<? extends Field<String>> values) {
        this.values = new QueryPartList(values);
        return this;
    }

    @Override // org.jooq.CreateTypeStep
    public final CreateTypeImpl asEnum() {
        return this;
    }

    @Override // org.jooq.CreateTypeStep
    public final CreateTypeImpl as(Field<?>... attributes) {
        return as((Collection<? extends Field<?>>) Arrays.asList(attributes));
    }

    @Override // org.jooq.CreateTypeStep
    public final CreateTypeImpl as(Collection<? extends Field<?>> attributes) {
        this.attributes = new QueryPartList(attributes);
        return this;
    }

    private final boolean supportsIfNotExists(Context<?> ctx) {
        return !NO_SUPPORT_IF_NOT_EXISTS.contains(ctx.dialect());
    }

    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        if (this.ifNotExists && !supportsIfNotExists(ctx)) {
            Tools.tryCatch(ctx, DDLStatementType.CREATE_TYPE, c -> {
                accept0(c);
            });
        } else {
            accept0(ctx);
        }
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v12, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v20, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v24, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v27, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v34, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v4, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v9, types: [org.jooq.Context] */
    private final void accept0(Context<?> ctx) {
        ctx.visit(Keywords.K_CREATE).sql(' ');
        ctx.visit(Keywords.K_TYPE).sql(' ');
        if (this.ifNotExists && supportsIfNotExists(ctx)) {
            ctx.visit(Keywords.K_IF_NOT_EXISTS).sql(' ');
        }
        ctx.visit(this.type).sql(' ');
        ctx.visit(Keywords.K_AS).sql(' ');
        if (!this.values.isEmpty()) {
            ctx.visit(Keywords.K_ENUM).sql(" (").visit(this.values, ParamType.INLINED).sql(')');
            return;
        }
        switch (ctx.family()) {
            case DUCKDB:
                ctx.visit(Keywords.K_STRUCT).sql(' ');
                break;
        }
        ctx.sql('(').visit(new QueryPartList(this.attributes).map(f -> {
            return declare(f);
        }), ParamType.INLINED).sql(')');
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final <T> Field<T> declare(Field<T> f) {
        return CustomField.of(f.getUnqualifiedName(), f.getDataType(), (Consumer<? super Context<?>>) c -> {
            c.visit(f.getUnqualifiedName());
            c.sql(' ');
            Tools.toSQLDDLTypeDeclarationForAddition(c, f.getDataType());
        });
    }

    @Override // org.jooq.impl.QOM.CreateType
    public final Type<?> $type() {
        return this.type;
    }

    @Override // org.jooq.impl.QOM.CreateType
    public final boolean $ifNotExists() {
        return this.ifNotExists;
    }

    @Override // org.jooq.impl.QOM.CreateType
    public final QOM.UnmodifiableList<? extends Field<String>> $values() {
        return QOM.unmodifiable((List) this.values);
    }

    @Override // org.jooq.impl.QOM.CreateType
    public final QOM.UnmodifiableList<? extends Field<?>> $attributes() {
        return QOM.unmodifiable((List) this.attributes);
    }

    @Override // org.jooq.impl.QOM.CreateType
    public final QOM.CreateType $type(Type<?> newValue) {
        return $constructor().apply(newValue, Boolean.valueOf($ifNotExists()), $values(), $attributes());
    }

    @Override // org.jooq.impl.QOM.CreateType
    public final QOM.CreateType $ifNotExists(boolean newValue) {
        return $constructor().apply($type(), Boolean.valueOf(newValue), $values(), $attributes());
    }

    @Override // org.jooq.impl.QOM.CreateType
    public final QOM.CreateType $values(Collection<? extends Field<String>> newValue) {
        return $constructor().apply($type(), Boolean.valueOf($ifNotExists()), newValue, $attributes());
    }

    @Override // org.jooq.impl.QOM.CreateType
    public final QOM.CreateType $attributes(Collection<? extends Field<?>> newValue) {
        return $constructor().apply($type(), Boolean.valueOf($ifNotExists()), $values(), newValue);
    }

    public final Function4<? super Type<?>, ? super Boolean, ? super Collection<? extends Field<String>>, ? super Collection<? extends Field<?>>, ? extends QOM.CreateType> $constructor() {
        return (a1, a2, a3, a4) -> {
            return new CreateTypeImpl(configuration(), a1, a2.booleanValue(), a3, a4);
        };
    }
}
