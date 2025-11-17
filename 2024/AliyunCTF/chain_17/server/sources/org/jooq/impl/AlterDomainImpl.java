package org.jooq.impl;

import java.util.Set;
import org.jooq.AlterDomainDropConstraintCascadeStep;
import org.jooq.AlterDomainFinalStep;
import org.jooq.AlterDomainRenameConstraintStep;
import org.jooq.AlterDomainStep;
import org.jooq.Configuration;
import org.jooq.Constraint;
import org.jooq.Context;
import org.jooq.Domain;
import org.jooq.Field;
import org.jooq.Function14;
import org.jooq.Name;
import org.jooq.SQLDialect;
import org.jooq.impl.QOM;
import org.jooq.impl.Tools;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AlterDomainImpl.class */
public final class AlterDomainImpl<T> extends AbstractDDLQuery implements QOM.AlterDomain<T>, AlterDomainStep<T>, AlterDomainDropConstraintCascadeStep, AlterDomainRenameConstraintStep, AlterDomainFinalStep {
    final Domain<T> domain;
    final boolean ifExists;
    Constraint addConstraint;
    Constraint dropConstraint;
    boolean dropConstraintIfExists;
    Domain<?> renameTo;
    Constraint renameConstraint;
    boolean renameConstraintIfExists;
    Field<T> setDefault;
    boolean dropDefault;
    boolean setNotNull;
    boolean dropNotNull;
    QOM.Cascade cascade;
    Constraint renameConstraintTo;
    private static final Set<SQLDialect> NO_SUPPORT_RENAME_CONSTRAINT_IF_EXISTS = SQLDialect.supportedBy(SQLDialect.FIREBIRD, SQLDialect.POSTGRES);
    private static final Set<SQLDialect> NO_SUPPORT_DROP_CONSTRAINT_IF_EXISTS = SQLDialect.supportedBy(SQLDialect.FIREBIRD);

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.AlterDomainStep
    public /* bridge */ /* synthetic */ AlterDomainFinalStep setDefault(Object obj) {
        return setDefault((AlterDomainImpl<T>) obj);
    }

    @Override // org.jooq.AlterDomainStep
    public /* bridge */ /* synthetic */ AlterDomainFinalStep renameTo(Domain domain) {
        return renameTo((Domain<?>) domain);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AlterDomainImpl(Configuration configuration, Domain<T> domain, boolean ifExists) {
        this(configuration, domain, ifExists, null, null, false, null, null, false, null, false, false, false, null, null);
    }

    AlterDomainImpl(Configuration configuration, Domain<T> domain, boolean ifExists, Constraint addConstraint, Constraint dropConstraint, boolean dropConstraintIfExists, Domain<?> renameTo, Constraint renameConstraint, boolean renameConstraintIfExists, Field<T> setDefault, boolean dropDefault, boolean setNotNull, boolean dropNotNull, QOM.Cascade cascade, Constraint renameConstraintTo) {
        super(configuration);
        this.domain = domain;
        this.ifExists = ifExists;
        this.addConstraint = addConstraint;
        this.dropConstraint = dropConstraint;
        this.dropConstraintIfExists = dropConstraintIfExists;
        this.renameTo = renameTo;
        this.renameConstraint = renameConstraint;
        this.renameConstraintIfExists = renameConstraintIfExists;
        this.setDefault = setDefault;
        this.dropDefault = dropDefault;
        this.setNotNull = setNotNull;
        this.dropNotNull = dropNotNull;
        this.cascade = cascade;
        this.renameConstraintTo = renameConstraintTo;
    }

    @Override // org.jooq.AlterDomainStep
    public final AlterDomainImpl<T> add(Constraint addConstraint) {
        this.addConstraint = addConstraint;
        return this;
    }

    @Override // org.jooq.AlterDomainStep
    public final AlterDomainImpl<T> dropConstraint(String dropConstraint) {
        return dropConstraint((Constraint) DSL.constraint(DSL.name(dropConstraint)));
    }

    @Override // org.jooq.AlterDomainStep
    public final AlterDomainImpl<T> dropConstraint(Name dropConstraint) {
        return dropConstraint((Constraint) DSL.constraint(dropConstraint));
    }

    @Override // org.jooq.AlterDomainStep
    public final AlterDomainImpl<T> dropConstraint(Constraint dropConstraint) {
        this.dropConstraint = dropConstraint;
        this.dropConstraintIfExists = false;
        return this;
    }

    @Override // org.jooq.AlterDomainStep
    public final AlterDomainImpl<T> dropConstraintIfExists(String dropConstraint) {
        return dropConstraintIfExists((Constraint) DSL.constraint(DSL.name(dropConstraint)));
    }

    @Override // org.jooq.AlterDomainStep
    public final AlterDomainImpl<T> dropConstraintIfExists(Name dropConstraint) {
        return dropConstraintIfExists((Constraint) DSL.constraint(dropConstraint));
    }

    @Override // org.jooq.AlterDomainStep
    public final AlterDomainImpl<T> dropConstraintIfExists(Constraint dropConstraint) {
        this.dropConstraint = dropConstraint;
        this.dropConstraintIfExists = true;
        return this;
    }

    @Override // org.jooq.AlterDomainStep
    public final AlterDomainImpl<T> renameTo(String renameTo) {
        return renameTo(DSL.domain(DSL.name(renameTo)));
    }

    @Override // org.jooq.AlterDomainStep
    public final AlterDomainImpl<T> renameTo(Name renameTo) {
        return renameTo(DSL.domain(renameTo));
    }

    @Override // org.jooq.AlterDomainStep
    public final AlterDomainImpl<T> renameTo(Domain<?> renameTo) {
        this.renameTo = renameTo;
        return this;
    }

    @Override // org.jooq.AlterDomainStep
    public final AlterDomainImpl<T> renameConstraint(String renameConstraint) {
        return renameConstraint((Constraint) DSL.constraint(DSL.name(renameConstraint)));
    }

    @Override // org.jooq.AlterDomainStep
    public final AlterDomainImpl<T> renameConstraint(Name renameConstraint) {
        return renameConstraint((Constraint) DSL.constraint(renameConstraint));
    }

    @Override // org.jooq.AlterDomainStep
    public final AlterDomainImpl<T> renameConstraint(Constraint renameConstraint) {
        this.renameConstraint = renameConstraint;
        this.renameConstraintIfExists = false;
        return this;
    }

    @Override // org.jooq.AlterDomainStep
    public final AlterDomainImpl<T> renameConstraintIfExists(String renameConstraint) {
        return renameConstraintIfExists((Constraint) DSL.constraint(DSL.name(renameConstraint)));
    }

    @Override // org.jooq.AlterDomainStep
    public final AlterDomainImpl<T> renameConstraintIfExists(Name renameConstraint) {
        return renameConstraintIfExists((Constraint) DSL.constraint(renameConstraint));
    }

    @Override // org.jooq.AlterDomainStep
    public final AlterDomainImpl<T> renameConstraintIfExists(Constraint renameConstraint) {
        this.renameConstraint = renameConstraint;
        this.renameConstraintIfExists = true;
        return this;
    }

    @Override // org.jooq.AlterDomainStep
    public final AlterDomainImpl<T> setDefault(T setDefault) {
        return setDefault((Field) Tools.field(setDefault));
    }

    @Override // org.jooq.AlterDomainStep
    public final AlterDomainImpl<T> setDefault(Field<T> setDefault) {
        this.setDefault = setDefault;
        return this;
    }

    @Override // org.jooq.AlterDomainStep
    public final AlterDomainImpl<T> dropDefault() {
        this.dropDefault = true;
        return this;
    }

    @Override // org.jooq.AlterDomainStep
    public final AlterDomainImpl<T> setNotNull() {
        this.setNotNull = true;
        return this;
    }

    @Override // org.jooq.AlterDomainStep
    public final AlterDomainImpl<T> dropNotNull() {
        this.dropNotNull = true;
        return this;
    }

    @Override // org.jooq.AlterDomainDropConstraintCascadeStep
    public final AlterDomainImpl<T> cascade() {
        this.cascade = QOM.Cascade.CASCADE;
        return this;
    }

    @Override // org.jooq.AlterDomainDropConstraintCascadeStep
    public final AlterDomainImpl<T> restrict() {
        this.cascade = QOM.Cascade.RESTRICT;
        return this;
    }

    @Override // org.jooq.AlterDomainRenameConstraintStep
    public final AlterDomainImpl<T> to(String renameConstraintTo) {
        return to((Constraint) DSL.constraint(DSL.name(renameConstraintTo)));
    }

    @Override // org.jooq.AlterDomainRenameConstraintStep
    public final AlterDomainImpl<T> to(Name renameConstraintTo) {
        return to((Constraint) DSL.constraint(renameConstraintTo));
    }

    @Override // org.jooq.AlterDomainRenameConstraintStep
    public final AlterDomainImpl<T> to(Constraint renameConstraintTo) {
        this.renameConstraintTo = renameConstraintTo;
        return this;
    }

    private final boolean supportsRenameConstraintIfExists(Context<?> ctx) {
        return !NO_SUPPORT_RENAME_CONSTRAINT_IF_EXISTS.contains(ctx.dialect());
    }

    private final boolean supportsDropConstraintIfExists(Context<?> ctx) {
        return !NO_SUPPORT_DROP_CONSTRAINT_IF_EXISTS.contains(ctx.dialect());
    }

    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        if ((this.renameConstraintIfExists && !supportsRenameConstraintIfExists(ctx)) || (this.dropConstraintIfExists && !supportsDropConstraintIfExists(ctx))) {
            Tools.tryCatch(ctx, DDLStatementType.ALTER_DOMAIN, c -> {
                accept0(c);
            });
        } else {
            accept0(ctx);
        }
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v33, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v37, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v41, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v51, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v57, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v62, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v66, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v70, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v8, types: [org.jooq.Context] */
    private final void accept0(Context<?> ctx) {
        ctx.visit(Keywords.K_ALTER).sql(' ').visit(Keywords.K_DOMAIN).sql(' ');
        if (this.ifExists) {
            ctx.visit(Keywords.K_IF_EXISTS).sql(' ');
        }
        ctx.visit(this.domain).sql(' ');
        if (this.addConstraint != null) {
            if (ctx.family() == SQLDialect.FIREBIRD) {
                ctx.visit(Keywords.K_ADD).sql(' ').visit(DSL.check(((ConstraintImpl) this.addConstraint).$check()));
                return;
            } else {
                ctx.visit(Keywords.K_ADD).sql(' ').visit(this.addConstraint);
                return;
            }
        }
        if (this.dropConstraint != null) {
            ctx.visit(Keywords.K_DROP_CONSTRAINT);
            if (this.dropConstraintIfExists && supportsDropConstraintIfExists(ctx)) {
                ctx.sql(' ').visit(Keywords.K_IF_EXISTS);
            }
            if (ctx.family() != SQLDialect.FIREBIRD) {
                ctx.sql(' ').data(Tools.BooleanDataKey.DATA_CONSTRAINT_REFERENCE, true, c -> {
                    c.visit(this.dropConstraint);
                });
                acceptCascade(ctx, this.cascade);
                return;
            }
            return;
        }
        if (this.renameTo != null) {
            ctx.visit(ctx.family() == SQLDialect.FIREBIRD ? Keywords.K_TO : Keywords.K_RENAME_TO).sql(' ').data(Tools.BooleanDataKey.DATA_CONSTRAINT_REFERENCE, true, c2 -> {
                c2.visit(this.renameTo);
            });
            return;
        }
        if (this.renameConstraint != null) {
            ctx.visit(Keywords.K_RENAME_CONSTRAINT).sql(' ').data(Tools.BooleanDataKey.DATA_CONSTRAINT_REFERENCE, true, c3 -> {
                if (this.renameConstraintIfExists && supportsRenameConstraintIfExists(c3)) {
                    c3.visit(Keywords.K_IF_EXISTS).sql(' ');
                }
                c3.visit(this.renameConstraint).sql(' ').visit(Keywords.K_TO).sql(' ').visit(this.renameConstraintTo);
            });
            return;
        }
        if (this.setDefault != null) {
            ctx.visit(Keywords.K_SET_DEFAULT).sql(' ').visit((Field<?>) this.setDefault);
            return;
        }
        if (this.dropDefault) {
            ctx.visit(Keywords.K_DROP_DEFAULT);
        } else if (this.setNotNull) {
            ctx.visit(Keywords.K_SET_NOT_NULL);
        } else if (this.dropNotNull) {
            ctx.visit(Keywords.K_DROP_NOT_NULL);
        }
    }

    @Override // org.jooq.impl.QOM.AlterDomain
    public final Domain<T> $domain() {
        return this.domain;
    }

    @Override // org.jooq.impl.QOM.AlterDomain
    public final boolean $ifExists() {
        return this.ifExists;
    }

    @Override // org.jooq.impl.QOM.AlterDomain
    public final Constraint $addConstraint() {
        return this.addConstraint;
    }

    @Override // org.jooq.impl.QOM.AlterDomain
    public final Constraint $dropConstraint() {
        return this.dropConstraint;
    }

    @Override // org.jooq.impl.QOM.AlterDomain
    public final boolean $dropConstraintIfExists() {
        return this.dropConstraintIfExists;
    }

    @Override // org.jooq.impl.QOM.AlterDomain
    public final Domain<?> $renameTo() {
        return this.renameTo;
    }

    @Override // org.jooq.impl.QOM.AlterDomain
    public final Constraint $renameConstraint() {
        return this.renameConstraint;
    }

    @Override // org.jooq.impl.QOM.AlterDomain
    public final boolean $renameConstraintIfExists() {
        return this.renameConstraintIfExists;
    }

    @Override // org.jooq.impl.QOM.AlterDomain
    public final Field<T> $setDefault() {
        return this.setDefault;
    }

    @Override // org.jooq.impl.QOM.AlterDomain
    public final boolean $dropDefault() {
        return this.dropDefault;
    }

    @Override // org.jooq.impl.QOM.AlterDomain
    public final boolean $setNotNull() {
        return this.setNotNull;
    }

    @Override // org.jooq.impl.QOM.AlterDomain
    public final boolean $dropNotNull() {
        return this.dropNotNull;
    }

    @Override // org.jooq.impl.QOM.AlterDomain
    public final QOM.Cascade $cascade() {
        return this.cascade;
    }

    @Override // org.jooq.impl.QOM.AlterDomain
    public final Constraint $renameConstraintTo() {
        return this.renameConstraintTo;
    }

    @Override // org.jooq.impl.QOM.AlterDomain
    public final QOM.AlterDomain<T> $domain(Domain<T> newValue) {
        return $constructor().apply(newValue, Boolean.valueOf($ifExists()), $addConstraint(), $dropConstraint(), Boolean.valueOf($dropConstraintIfExists()), $renameTo(), $renameConstraint(), Boolean.valueOf($renameConstraintIfExists()), $setDefault(), Boolean.valueOf($dropDefault()), Boolean.valueOf($setNotNull()), Boolean.valueOf($dropNotNull()), $cascade(), $renameConstraintTo());
    }

    @Override // org.jooq.impl.QOM.AlterDomain
    public final QOM.AlterDomain<T> $ifExists(boolean newValue) {
        return $constructor().apply($domain(), Boolean.valueOf(newValue), $addConstraint(), $dropConstraint(), Boolean.valueOf($dropConstraintIfExists()), $renameTo(), $renameConstraint(), Boolean.valueOf($renameConstraintIfExists()), $setDefault(), Boolean.valueOf($dropDefault()), Boolean.valueOf($setNotNull()), Boolean.valueOf($dropNotNull()), $cascade(), $renameConstraintTo());
    }

    @Override // org.jooq.impl.QOM.AlterDomain
    public final QOM.AlterDomain<T> $addConstraint(Constraint newValue) {
        return $constructor().apply($domain(), Boolean.valueOf($ifExists()), newValue, $dropConstraint(), Boolean.valueOf($dropConstraintIfExists()), $renameTo(), $renameConstraint(), Boolean.valueOf($renameConstraintIfExists()), $setDefault(), Boolean.valueOf($dropDefault()), Boolean.valueOf($setNotNull()), Boolean.valueOf($dropNotNull()), $cascade(), $renameConstraintTo());
    }

    @Override // org.jooq.impl.QOM.AlterDomain
    public final QOM.AlterDomain<T> $dropConstraint(Constraint newValue) {
        return $constructor().apply($domain(), Boolean.valueOf($ifExists()), $addConstraint(), newValue, Boolean.valueOf($dropConstraintIfExists()), $renameTo(), $renameConstraint(), Boolean.valueOf($renameConstraintIfExists()), $setDefault(), Boolean.valueOf($dropDefault()), Boolean.valueOf($setNotNull()), Boolean.valueOf($dropNotNull()), $cascade(), $renameConstraintTo());
    }

    @Override // org.jooq.impl.QOM.AlterDomain
    public final QOM.AlterDomain<T> $dropConstraintIfExists(boolean newValue) {
        return $constructor().apply($domain(), Boolean.valueOf($ifExists()), $addConstraint(), $dropConstraint(), Boolean.valueOf(newValue), $renameTo(), $renameConstraint(), Boolean.valueOf($renameConstraintIfExists()), $setDefault(), Boolean.valueOf($dropDefault()), Boolean.valueOf($setNotNull()), Boolean.valueOf($dropNotNull()), $cascade(), $renameConstraintTo());
    }

    @Override // org.jooq.impl.QOM.AlterDomain
    public final QOM.AlterDomain<T> $renameTo(Domain<?> newValue) {
        return $constructor().apply($domain(), Boolean.valueOf($ifExists()), $addConstraint(), $dropConstraint(), Boolean.valueOf($dropConstraintIfExists()), newValue, $renameConstraint(), Boolean.valueOf($renameConstraintIfExists()), $setDefault(), Boolean.valueOf($dropDefault()), Boolean.valueOf($setNotNull()), Boolean.valueOf($dropNotNull()), $cascade(), $renameConstraintTo());
    }

    @Override // org.jooq.impl.QOM.AlterDomain
    public final QOM.AlterDomain<T> $renameConstraint(Constraint newValue) {
        return $constructor().apply($domain(), Boolean.valueOf($ifExists()), $addConstraint(), $dropConstraint(), Boolean.valueOf($dropConstraintIfExists()), $renameTo(), newValue, Boolean.valueOf($renameConstraintIfExists()), $setDefault(), Boolean.valueOf($dropDefault()), Boolean.valueOf($setNotNull()), Boolean.valueOf($dropNotNull()), $cascade(), $renameConstraintTo());
    }

    @Override // org.jooq.impl.QOM.AlterDomain
    public final QOM.AlterDomain<T> $renameConstraintIfExists(boolean newValue) {
        return $constructor().apply($domain(), Boolean.valueOf($ifExists()), $addConstraint(), $dropConstraint(), Boolean.valueOf($dropConstraintIfExists()), $renameTo(), $renameConstraint(), Boolean.valueOf(newValue), $setDefault(), Boolean.valueOf($dropDefault()), Boolean.valueOf($setNotNull()), Boolean.valueOf($dropNotNull()), $cascade(), $renameConstraintTo());
    }

    @Override // org.jooq.impl.QOM.AlterDomain
    public final QOM.AlterDomain<T> $setDefault(Field<T> newValue) {
        return $constructor().apply($domain(), Boolean.valueOf($ifExists()), $addConstraint(), $dropConstraint(), Boolean.valueOf($dropConstraintIfExists()), $renameTo(), $renameConstraint(), Boolean.valueOf($renameConstraintIfExists()), newValue, Boolean.valueOf($dropDefault()), Boolean.valueOf($setNotNull()), Boolean.valueOf($dropNotNull()), $cascade(), $renameConstraintTo());
    }

    @Override // org.jooq.impl.QOM.AlterDomain
    public final QOM.AlterDomain<T> $dropDefault(boolean newValue) {
        return $constructor().apply($domain(), Boolean.valueOf($ifExists()), $addConstraint(), $dropConstraint(), Boolean.valueOf($dropConstraintIfExists()), $renameTo(), $renameConstraint(), Boolean.valueOf($renameConstraintIfExists()), $setDefault(), Boolean.valueOf(newValue), Boolean.valueOf($setNotNull()), Boolean.valueOf($dropNotNull()), $cascade(), $renameConstraintTo());
    }

    @Override // org.jooq.impl.QOM.AlterDomain
    public final QOM.AlterDomain<T> $setNotNull(boolean newValue) {
        return $constructor().apply($domain(), Boolean.valueOf($ifExists()), $addConstraint(), $dropConstraint(), Boolean.valueOf($dropConstraintIfExists()), $renameTo(), $renameConstraint(), Boolean.valueOf($renameConstraintIfExists()), $setDefault(), Boolean.valueOf($dropDefault()), Boolean.valueOf(newValue), Boolean.valueOf($dropNotNull()), $cascade(), $renameConstraintTo());
    }

    @Override // org.jooq.impl.QOM.AlterDomain
    public final QOM.AlterDomain<T> $dropNotNull(boolean newValue) {
        return $constructor().apply($domain(), Boolean.valueOf($ifExists()), $addConstraint(), $dropConstraint(), Boolean.valueOf($dropConstraintIfExists()), $renameTo(), $renameConstraint(), Boolean.valueOf($renameConstraintIfExists()), $setDefault(), Boolean.valueOf($dropDefault()), Boolean.valueOf($setNotNull()), Boolean.valueOf(newValue), $cascade(), $renameConstraintTo());
    }

    @Override // org.jooq.impl.QOM.AlterDomain
    public final QOM.AlterDomain<T> $cascade(QOM.Cascade newValue) {
        return $constructor().apply($domain(), Boolean.valueOf($ifExists()), $addConstraint(), $dropConstraint(), Boolean.valueOf($dropConstraintIfExists()), $renameTo(), $renameConstraint(), Boolean.valueOf($renameConstraintIfExists()), $setDefault(), Boolean.valueOf($dropDefault()), Boolean.valueOf($setNotNull()), Boolean.valueOf($dropNotNull()), newValue, $renameConstraintTo());
    }

    @Override // org.jooq.impl.QOM.AlterDomain
    public final QOM.AlterDomain<T> $renameConstraintTo(Constraint newValue) {
        return $constructor().apply($domain(), Boolean.valueOf($ifExists()), $addConstraint(), $dropConstraint(), Boolean.valueOf($dropConstraintIfExists()), $renameTo(), $renameConstraint(), Boolean.valueOf($renameConstraintIfExists()), $setDefault(), Boolean.valueOf($dropDefault()), Boolean.valueOf($setNotNull()), Boolean.valueOf($dropNotNull()), $cascade(), newValue);
    }

    public final Function14<? super Domain<T>, ? super Boolean, ? super Constraint, ? super Constraint, ? super Boolean, ? super Domain<?>, ? super Constraint, ? super Boolean, ? super Field<T>, ? super Boolean, ? super Boolean, ? super Boolean, ? super QOM.Cascade, ? super Constraint, ? extends QOM.AlterDomain<T>> $constructor() {
        return (a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14) -> {
            return new AlterDomainImpl(configuration(), a1, a2.booleanValue(), a3, a4, a5.booleanValue(), a6, a7, a8.booleanValue(), a9, a10.booleanValue(), a11.booleanValue(), a12.booleanValue(), a13, a14);
        };
    }
}
