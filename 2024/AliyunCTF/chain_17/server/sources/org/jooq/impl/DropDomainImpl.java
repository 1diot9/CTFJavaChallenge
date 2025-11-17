package org.jooq.impl;

import java.util.Set;
import org.jooq.Configuration;
import org.jooq.Context;
import org.jooq.Domain;
import org.jooq.DropDomainCascadeStep;
import org.jooq.DropDomainFinalStep;
import org.jooq.Function3;
import org.jooq.SQLDialect;
import org.jooq.impl.QOM;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DropDomainImpl.class */
public final class DropDomainImpl extends AbstractDDLQuery implements QOM.DropDomain, DropDomainCascadeStep, DropDomainFinalStep {
    final Domain<?> domain;
    final boolean ifExists;
    QOM.Cascade cascade;
    private static final Set<SQLDialect> NO_SUPPORT_IF_EXISTS = SQLDialect.supportedBy(SQLDialect.FIREBIRD);

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.jooq.impl.DropDomainImpl$1, reason: invalid class name */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DropDomainImpl$1.class */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$jooq$SQLDialect = new int[SQLDialect.values().length];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DropDomainImpl(Configuration configuration, Domain<?> domain, boolean ifExists) {
        this(configuration, domain, ifExists, null);
    }

    DropDomainImpl(Configuration configuration, Domain<?> domain, boolean ifExists, QOM.Cascade cascade) {
        super(configuration);
        this.domain = domain;
        this.ifExists = ifExists;
        this.cascade = cascade;
    }

    @Override // org.jooq.DropDomainCascadeStep
    public final DropDomainImpl cascade() {
        this.cascade = QOM.Cascade.CASCADE;
        return this;
    }

    @Override // org.jooq.DropDomainCascadeStep
    public final DropDomainImpl restrict() {
        this.cascade = QOM.Cascade.RESTRICT;
        return this;
    }

    private final boolean supportsIfExists(Context<?> ctx) {
        return !NO_SUPPORT_IF_EXISTS.contains(ctx.dialect());
    }

    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        if (this.ifExists && !supportsIfExists(ctx)) {
            Tools.tryCatch(ctx, DDLStatementType.DROP_DOMAIN, c -> {
                accept0(c);
            });
        } else {
            accept0(ctx);
        }
    }

    /* JADX WARN: Type inference failed for: r0v15, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v3, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v9, types: [org.jooq.Context] */
    private final void accept0(Context<?> ctx) {
        switch (AnonymousClass1.$SwitchMap$org$jooq$SQLDialect[ctx.family().ordinal()]) {
            default:
                ctx.visit(Keywords.K_DROP).sql(' ').visit(Keywords.K_DOMAIN);
                if (this.ifExists && supportsIfExists(ctx)) {
                    ctx.sql(' ').visit(Keywords.K_IF_EXISTS);
                }
                ctx.sql(' ').visit(this.domain);
                acceptCascade(ctx, this.cascade);
                return;
        }
    }

    @Override // org.jooq.impl.QOM.DropDomain
    public final Domain<?> $domain() {
        return this.domain;
    }

    @Override // org.jooq.impl.QOM.DropDomain
    public final boolean $ifExists() {
        return this.ifExists;
    }

    @Override // org.jooq.impl.QOM.DropDomain
    public final QOM.Cascade $cascade() {
        return this.cascade;
    }

    @Override // org.jooq.impl.QOM.DropDomain
    public final QOM.DropDomain $domain(Domain<?> newValue) {
        return $constructor().apply(newValue, Boolean.valueOf($ifExists()), $cascade());
    }

    @Override // org.jooq.impl.QOM.DropDomain
    public final QOM.DropDomain $ifExists(boolean newValue) {
        return $constructor().apply($domain(), Boolean.valueOf(newValue), $cascade());
    }

    @Override // org.jooq.impl.QOM.DropDomain
    public final QOM.DropDomain $cascade(QOM.Cascade newValue) {
        return $constructor().apply($domain(), Boolean.valueOf($ifExists()), newValue);
    }

    public final Function3<? super Domain<?>, ? super Boolean, ? super QOM.Cascade, ? extends QOM.DropDomain> $constructor() {
        return (a1, a2, a3) -> {
            return new DropDomainImpl(configuration(), a1, a2.booleanValue(), a3);
        };
    }
}
