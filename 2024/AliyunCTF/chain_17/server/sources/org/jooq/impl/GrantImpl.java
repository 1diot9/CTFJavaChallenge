package org.jooq.impl;

import java.util.Collection;
import java.util.List;
import org.jooq.Clause;
import org.jooq.Configuration;
import org.jooq.Context;
import org.jooq.Function5;
import org.jooq.GrantFinalStep;
import org.jooq.GrantOnStep;
import org.jooq.GrantToStep;
import org.jooq.GrantWithGrantOptionStep;
import org.jooq.Name;
import org.jooq.Privilege;
import org.jooq.Role;
import org.jooq.Table;
import org.jooq.User;
import org.jooq.impl.QOM;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/GrantImpl.class */
final class GrantImpl extends AbstractDDLQuery implements QOM.Grant, GrantOnStep, GrantToStep, GrantWithGrantOptionStep, GrantFinalStep {
    final QueryPartListView<? extends Privilege> privileges;
    Table<?> on;
    Role to;
    boolean toPublic;
    boolean withGrantOption;
    private static final Clause[] CLAUSE = {Clause.GRANT};

    @Override // org.jooq.GrantOnStep
    public /* bridge */ /* synthetic */ GrantToStep on(Table table) {
        return on((Table<?>) table);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public GrantImpl(Configuration configuration, Collection<? extends Privilege> privileges) {
        this(configuration, privileges, null, null, false, false);
    }

    GrantImpl(Configuration configuration, Collection<? extends Privilege> privileges, Table<?> on, Role to, boolean toPublic, boolean withGrantOption) {
        super(configuration);
        this.privileges = new QueryPartList(privileges);
        this.on = on;
        this.to = to;
        this.toPublic = toPublic;
        this.withGrantOption = withGrantOption;
    }

    @Override // org.jooq.GrantOnStep
    public final GrantImpl on(String on) {
        return on((Table<?>) DSL.table(DSL.name(on)));
    }

    @Override // org.jooq.GrantOnStep
    public final GrantImpl on(Name on) {
        return on((Table<?>) DSL.table(on));
    }

    @Override // org.jooq.GrantOnStep
    public final GrantImpl on(Table<?> on) {
        this.on = on;
        return this;
    }

    @Override // org.jooq.GrantToStep
    public final GrantImpl to(User to) {
        return to(DSL.role(to.getQualifiedName()));
    }

    @Override // org.jooq.GrantToStep
    public final GrantImpl to(Role to) {
        this.to = to;
        return this;
    }

    @Override // org.jooq.GrantToStep
    public final GrantImpl toPublic() {
        this.toPublic = true;
        return this;
    }

    @Override // org.jooq.GrantWithGrantOptionStep
    public final GrantImpl withGrantOption() {
        this.withGrantOption = true;
        return this;
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v27, types: [org.jooq.Context] */
    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        ctx.start(Clause.GRANT_PRIVILEGE).visit(Keywords.K_GRANT).sql(' ').visit(QueryPartCollectionView.wrap(this.privileges)).end(Clause.GRANT_PRIVILEGE).sql(' ').start(Clause.GRANT_ON).visit(Keywords.K_ON).sql(' ').visit(this.on).end(Clause.GRANT_ON).sql(' ').start(Clause.GRANT_TO).visit(Keywords.K_TO).sql(' ');
        if (this.to != null) {
            ctx.visit(this.to);
        } else if (this.toPublic) {
            ctx.visit(Keywords.K_PUBLIC);
        }
        if (this.withGrantOption) {
            ctx.sql(' ').visit(Keywords.K_WITH_GRANT_OPTION);
        }
        ctx.end(Clause.GRANT_TO);
    }

    @Override // org.jooq.impl.AbstractQueryPart, org.jooq.QueryPartInternal
    public final Clause[] clauses(Context<?> ctx) {
        return CLAUSE;
    }

    @Override // org.jooq.impl.QOM.Grant
    public final QOM.UnmodifiableList<? extends Privilege> $privileges() {
        return QOM.unmodifiable((List) this.privileges);
    }

    @Override // org.jooq.impl.QOM.Grant
    public final Table<?> $on() {
        return this.on;
    }

    @Override // org.jooq.impl.QOM.Grant
    public final Role $to() {
        return this.to;
    }

    @Override // org.jooq.impl.QOM.Grant
    public final boolean $toPublic() {
        return this.toPublic;
    }

    @Override // org.jooq.impl.QOM.Grant
    public final boolean $withGrantOption() {
        return this.withGrantOption;
    }

    @Override // org.jooq.impl.QOM.Grant
    public final QOM.Grant $privileges(Collection<? extends Privilege> newValue) {
        return $constructor().apply(newValue, $on(), $to(), Boolean.valueOf($toPublic()), Boolean.valueOf($withGrantOption()));
    }

    @Override // org.jooq.impl.QOM.Grant
    public final QOM.Grant $on(Table<?> newValue) {
        return $constructor().apply($privileges(), newValue, $to(), Boolean.valueOf($toPublic()), Boolean.valueOf($withGrantOption()));
    }

    @Override // org.jooq.impl.QOM.Grant
    public final QOM.Grant $to(Role newValue) {
        return $constructor().apply($privileges(), $on(), newValue, Boolean.valueOf($toPublic()), Boolean.valueOf($withGrantOption()));
    }

    @Override // org.jooq.impl.QOM.Grant
    public final QOM.Grant $toPublic(boolean newValue) {
        return $constructor().apply($privileges(), $on(), $to(), Boolean.valueOf(newValue), Boolean.valueOf($withGrantOption()));
    }

    @Override // org.jooq.impl.QOM.Grant
    public final QOM.Grant $withGrantOption(boolean newValue) {
        return $constructor().apply($privileges(), $on(), $to(), Boolean.valueOf($toPublic()), Boolean.valueOf(newValue));
    }

    public final Function5<? super Collection<? extends Privilege>, ? super Table<?>, ? super Role, ? super Boolean, ? super Boolean, ? extends QOM.Grant> $constructor() {
        return (a1, a2, a3, a4, a5) -> {
            return new GrantImpl(configuration(), a1, a2, a3, a4.booleanValue(), a5.booleanValue());
        };
    }
}
