package org.jooq.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.jooq.Configuration;
import org.jooq.Constraint;
import org.jooq.Context;
import org.jooq.CreateDomainAsStep;
import org.jooq.CreateDomainConstraintStep;
import org.jooq.CreateDomainDefaultStep;
import org.jooq.CreateDomainFinalStep;
import org.jooq.DataType;
import org.jooq.Domain;
import org.jooq.Field;
import org.jooq.Function5;
import org.jooq.SQLDialect;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/CreateDomainImpl.class */
public final class CreateDomainImpl<T> extends AbstractDDLQuery implements QOM.CreateDomain<T>, CreateDomainAsStep, CreateDomainDefaultStep<T>, CreateDomainConstraintStep, CreateDomainFinalStep {
    final Domain<?> domain;
    final boolean ifNotExists;
    DataType<T> dataType;
    Field<T> default_;
    QueryPartListView<? extends Constraint> constraints;
    private static final Set<SQLDialect> NO_SUPPORT_IF_NOT_EXISTS = SQLDialect.supportedBy(SQLDialect.FIREBIRD, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB);

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.jooq.impl.CreateDomainImpl$1, reason: invalid class name */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/CreateDomainImpl$1.class */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$jooq$SQLDialect = new int[SQLDialect.values().length];
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.CreateDomainDefaultStep
    public /* bridge */ /* synthetic */ CreateDomainConstraintStep default_(Object obj) {
        return default_((CreateDomainImpl<T>) obj);
    }

    @Override // org.jooq.CreateDomainConstraintStep
    public /* bridge */ /* synthetic */ CreateDomainConstraintStep constraints(Collection collection) {
        return constraints((Collection<? extends Constraint>) collection);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CreateDomainImpl(Configuration configuration, Domain<?> domain, boolean ifNotExists) {
        this(configuration, domain, ifNotExists, null, null, null);
    }

    CreateDomainImpl(Configuration configuration, Domain<?> domain, boolean ifNotExists, DataType<T> dataType, Field<T> default_, Collection<? extends Constraint> constraints) {
        super(configuration);
        this.domain = domain;
        this.ifNotExists = ifNotExists;
        this.dataType = dataType;
        this.default_ = default_;
        this.constraints = new QueryPartList(constraints);
    }

    @Override // org.jooq.CreateDomainAsStep
    public final <T> CreateDomainImpl<T> as(Class<T> dataType) {
        return as((DataType) DefaultDataType.getDataType((SQLDialect) null, dataType));
    }

    @Override // org.jooq.CreateDomainAsStep
    public final <T> CreateDomainImpl<T> as(DataType<T> dataType) {
        this.dataType = dataType;
        return this;
    }

    @Override // org.jooq.CreateDomainDefaultStep
    public final CreateDomainImpl<T> default_(T default_) {
        return default_((Field) Tools.field(default_));
    }

    @Override // org.jooq.CreateDomainDefaultStep
    public final CreateDomainImpl<T> default_(Field<T> default_) {
        this.default_ = default_;
        return this;
    }

    @Override // org.jooq.CreateDomainConstraintStep
    public final CreateDomainImpl<T> constraints(Constraint... constraints) {
        return constraints((Collection<? extends Constraint>) Arrays.asList(constraints));
    }

    @Override // org.jooq.CreateDomainConstraintStep
    public final CreateDomainImpl<T> constraints(Collection<? extends Constraint> constraints) {
        this.constraints = new QueryPartList(constraints);
        return this;
    }

    private final boolean supportsIfNotExists(Context<?> ctx) {
        return !NO_SUPPORT_IF_NOT_EXISTS.contains(ctx.dialect());
    }

    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        if (this.ifNotExists && !supportsIfNotExists(ctx)) {
            Tools.tryCatch(ctx, DDLStatementType.CREATE_DOMAIN, c -> {
                accept0(c);
            });
        } else {
            accept0(ctx);
        }
    }

    /* JADX WARN: Type inference failed for: r0v3, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v41, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v44, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v47, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v51, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v54, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v61, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v9, types: [org.jooq.Context] */
    private final void accept0(Context<?> ctx) {
        switch (AnonymousClass1.$SwitchMap$org$jooq$SQLDialect[ctx.family().ordinal()]) {
            default:
                ctx.visit(Keywords.K_CREATE).sql(' ').visit(Keywords.K_DOMAIN);
                if (this.ifNotExists && supportsIfNotExists(ctx)) {
                    ctx.sql(' ').visit(Keywords.K_IF_NOT_EXISTS);
                }
                ctx.sql(' ').visit(this.domain).sql(' ').visit(Keywords.K_AS).sql(' ');
                Tools.toSQLDDLTypeDeclaration(ctx, this.dataType);
                if (this.default_ != null) {
                    ctx.sql(' ').visit(Keywords.K_DEFAULT).sql(' ').visit((Field<?>) this.default_);
                }
                if (!Tools.isEmpty((Collection<?>) this.constraints)) {
                    if (ctx.family() == SQLDialect.FIREBIRD) {
                        ctx.formatSeparator().visit(DSL.check(DSL.and(Tools.map(this.constraints, c -> {
                            return ((ConstraintImpl) c).$check();
                        }))));
                        return;
                    }
                    boolean indent = this.constraints.size() > 1;
                    if (indent) {
                        ctx.formatSeparator().formatIndentStart();
                    }
                    Iterator<? extends Constraint> it = this.constraints.iterator();
                    while (it.hasNext()) {
                        Constraint constraint = it.next();
                        if (indent) {
                            ctx.formatSeparator().visit(constraint);
                        } else {
                            ctx.sql(' ').visit(constraint);
                        }
                    }
                    if (indent) {
                        ctx.formatIndentEnd();
                        return;
                    }
                    return;
                }
                return;
        }
    }

    @Override // org.jooq.impl.QOM.CreateDomain
    public final Domain<?> $domain() {
        return this.domain;
    }

    @Override // org.jooq.impl.QOM.CreateDomain
    public final boolean $ifNotExists() {
        return this.ifNotExists;
    }

    @Override // org.jooq.impl.QOM.CreateDomain
    public final DataType<T> $dataType() {
        return this.dataType;
    }

    @Override // org.jooq.impl.QOM.CreateDomain
    public final Field<T> $default_() {
        return this.default_;
    }

    @Override // org.jooq.impl.QOM.CreateDomain
    public final QOM.UnmodifiableList<? extends Constraint> $constraints() {
        return QOM.unmodifiable((List) this.constraints);
    }

    @Override // org.jooq.impl.QOM.CreateDomain
    public final QOM.CreateDomain<T> $domain(Domain<?> newValue) {
        return $constructor().apply(newValue, Boolean.valueOf($ifNotExists()), $dataType(), $default_(), $constraints());
    }

    @Override // org.jooq.impl.QOM.CreateDomain
    public final QOM.CreateDomain<T> $ifNotExists(boolean newValue) {
        return $constructor().apply($domain(), Boolean.valueOf(newValue), $dataType(), $default_(), $constraints());
    }

    @Override // org.jooq.impl.QOM.CreateDomain
    public final QOM.CreateDomain<T> $dataType(DataType<T> newValue) {
        return $constructor().apply($domain(), Boolean.valueOf($ifNotExists()), newValue, $default_(), $constraints());
    }

    @Override // org.jooq.impl.QOM.CreateDomain
    public final QOM.CreateDomain<T> $default_(Field<T> newValue) {
        return $constructor().apply($domain(), Boolean.valueOf($ifNotExists()), $dataType(), newValue, $constraints());
    }

    @Override // org.jooq.impl.QOM.CreateDomain
    public final QOM.CreateDomain<T> $constraints(Collection<? extends Constraint> newValue) {
        return $constructor().apply($domain(), Boolean.valueOf($ifNotExists()), $dataType(), $default_(), newValue);
    }

    public final Function5<? super Domain<?>, ? super Boolean, ? super DataType<T>, ? super Field<T>, ? super Collection<? extends Constraint>, ? extends QOM.CreateDomain<T>> $constructor() {
        return (a1, a2, a3, a4, a5) -> {
            return new CreateDomainImpl(configuration(), a1, a2.booleanValue(), a3, a4, a5);
        };
    }
}
