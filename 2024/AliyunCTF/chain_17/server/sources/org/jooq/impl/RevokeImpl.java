package org.jooq.impl;

import java.util.Collection;
import java.util.List;
import org.jooq.Clause;
import org.jooq.Configuration;
import org.jooq.Context;
import org.jooq.Function5;
import org.jooq.Name;
import org.jooq.Privilege;
import org.jooq.RevokeFinalStep;
import org.jooq.RevokeFromStep;
import org.jooq.RevokeOnStep;
import org.jooq.Role;
import org.jooq.SQLDialect;
import org.jooq.Table;
import org.jooq.User;
import org.jooq.impl.QOM;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/RevokeImpl.class */
final class RevokeImpl extends AbstractDDLQuery implements QOM.Revoke, RevokeOnStep, RevokeFromStep, RevokeFinalStep {
    final QueryPartListView<? extends Privilege> privileges;
    final boolean grantOptionFor;
    Table<?> on;
    Role from;
    boolean fromPublic;
    private static final Clause[] CLAUSE = {Clause.REVOKE};

    @Override // org.jooq.RevokeOnStep
    public /* bridge */ /* synthetic */ RevokeFromStep on(Table table) {
        return on((Table<?>) table);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public RevokeImpl(Configuration configuration, Collection<? extends Privilege> privileges, boolean grantOptionFor) {
        this(configuration, privileges, grantOptionFor, null, null, false);
    }

    RevokeImpl(Configuration configuration, Collection<? extends Privilege> privileges, boolean grantOptionFor, Table<?> on, Role from, boolean fromPublic) {
        super(configuration);
        this.privileges = new QueryPartList(privileges);
        this.grantOptionFor = grantOptionFor;
        this.on = on;
        this.from = from;
        this.fromPublic = fromPublic;
    }

    @Override // org.jooq.RevokeOnStep
    public final RevokeImpl on(String on) {
        return on((Table<?>) DSL.table(DSL.name(on)));
    }

    @Override // org.jooq.RevokeOnStep
    public final RevokeImpl on(Name on) {
        return on((Table<?>) DSL.table(on));
    }

    @Override // org.jooq.RevokeOnStep
    public final RevokeImpl on(Table<?> on) {
        this.on = on;
        return this;
    }

    @Override // org.jooq.RevokeFromStep
    public final RevokeImpl from(User from) {
        return from(DSL.role(from.getQualifiedName()));
    }

    @Override // org.jooq.RevokeFromStep
    public final RevokeImpl from(Role from) {
        this.from = from;
        return this;
    }

    @Override // org.jooq.RevokeFromStep
    public final RevokeImpl fromPublic() {
        this.fromPublic = true;
        return this;
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v30, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v35, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v7, types: [org.jooq.Context] */
    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        ctx.start(Clause.REVOKE_PRIVILEGE).visit(Keywords.K_REVOKE).sql(' ');
        if (this.grantOptionFor) {
            ctx.visit(Keywords.K_GRANT_OPTION_FOR).sql(' ');
        }
        ctx.visit(this.privileges).end(Clause.REVOKE_PRIVILEGE).sql(' ').start(Clause.REVOKE_ON).visit(Keywords.K_ON).sql(' ').visit(this.on).end(Clause.REVOKE_ON).sql(' ').start(Clause.REVOKE_FROM).visit(Keywords.K_FROM).sql(' ');
        if (this.from != null) {
            ctx.visit(this.from);
        } else if (this.fromPublic) {
            ctx.visit(Keywords.K_PUBLIC);
        }
        if (ctx.family() == SQLDialect.HSQLDB) {
            ctx.sql(' ').visit(Keywords.K_RESTRICT);
        }
        ctx.end(Clause.REVOKE_FROM);
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final Clause[] clauses(Context<?> ctx) {
        return CLAUSE;
    }

    @Override // org.jooq.impl.QOM.Revoke
    public final QOM.UnmodifiableList<? extends Privilege> $privileges() {
        return QOM.unmodifiable((List) this.privileges);
    }

    @Override // org.jooq.impl.QOM.Revoke
    public final boolean $grantOptionFor() {
        return this.grantOptionFor;
    }

    @Override // org.jooq.impl.QOM.Revoke
    public final Table<?> $on() {
        return this.on;
    }

    @Override // org.jooq.impl.QOM.Revoke
    public final Role $from() {
        return this.from;
    }

    @Override // org.jooq.impl.QOM.Revoke
    public final boolean $fromPublic() {
        return this.fromPublic;
    }

    @Override // org.jooq.impl.QOM.Revoke
    public final QOM.Revoke $privileges(Collection<? extends Privilege> newValue) {
        return $constructor().apply(newValue, Boolean.valueOf($grantOptionFor()), $on(), $from(), Boolean.valueOf($fromPublic()));
    }

    @Override // org.jooq.impl.QOM.Revoke
    public final QOM.Revoke $grantOptionFor(boolean newValue) {
        return $constructor().apply($privileges(), Boolean.valueOf(newValue), $on(), $from(), Boolean.valueOf($fromPublic()));
    }

    @Override // org.jooq.impl.QOM.Revoke
    public final QOM.Revoke $on(Table<?> newValue) {
        return $constructor().apply($privileges(), Boolean.valueOf($grantOptionFor()), newValue, $from(), Boolean.valueOf($fromPublic()));
    }

    @Override // org.jooq.impl.QOM.Revoke
    public final QOM.Revoke $from(Role newValue) {
        return $constructor().apply($privileges(), Boolean.valueOf($grantOptionFor()), $on(), newValue, Boolean.valueOf($fromPublic()));
    }

    @Override // org.jooq.impl.QOM.Revoke
    public final QOM.Revoke $fromPublic(boolean newValue) {
        return $constructor().apply($privileges(), Boolean.valueOf($grantOptionFor()), $on(), $from(), Boolean.valueOf(newValue));
    }

    public final Function5<? super Collection<? extends Privilege>, ? super Boolean, ? super Table<?>, ? super Role, ? super Boolean, ? extends QOM.Revoke> $constructor() {
        return (a1, a2, a3, a4, a5) -> {
            return new RevokeImpl(configuration(), a1, a2.booleanValue(), a3, a4, a5.booleanValue());
        };
    }
}
