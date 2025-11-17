package org.jooq;

import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/AlterDomainStep.class */
public interface AlterDomainStep<T> {
    @Support({SQLDialect.FIREBIRD, SQLDialect.HSQLDB, SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    AlterDomainFinalStep add(Constraint constraint);

    @Support({SQLDialect.FIREBIRD, SQLDialect.HSQLDB, SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    AlterDomainDropConstraintCascadeStep dropConstraint(String str);

    @Support({SQLDialect.FIREBIRD, SQLDialect.HSQLDB, SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    AlterDomainDropConstraintCascadeStep dropConstraint(Name name);

    @Support({SQLDialect.FIREBIRD, SQLDialect.HSQLDB, SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    AlterDomainDropConstraintCascadeStep dropConstraint(Constraint constraint);

    @Support({SQLDialect.FIREBIRD, SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    AlterDomainDropConstraintCascadeStep dropConstraintIfExists(String str);

    @Support({SQLDialect.FIREBIRD, SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    AlterDomainDropConstraintCascadeStep dropConstraintIfExists(Name name);

    @Support({SQLDialect.FIREBIRD, SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    AlterDomainDropConstraintCascadeStep dropConstraintIfExists(Constraint constraint);

    @Support({SQLDialect.FIREBIRD, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterDomainFinalStep renameTo(String str);

    @Support({SQLDialect.FIREBIRD, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterDomainFinalStep renameTo(Name name);

    @Support({SQLDialect.FIREBIRD, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterDomainFinalStep renameTo(Domain<?> domain);

    @Support({SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    AlterDomainRenameConstraintStep renameConstraint(String str);

    @Support({SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    AlterDomainRenameConstraintStep renameConstraint(Name name);

    @Support({SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    AlterDomainRenameConstraintStep renameConstraint(Constraint constraint);

    @Support({SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    AlterDomainRenameConstraintStep renameConstraintIfExists(String str);

    @Support({SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    AlterDomainRenameConstraintStep renameConstraintIfExists(Name name);

    @Support({SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    AlterDomainRenameConstraintStep renameConstraintIfExists(Constraint constraint);

    @Support({SQLDialect.FIREBIRD, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterDomainFinalStep setDefault(T t);

    @Support({SQLDialect.FIREBIRD, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterDomainFinalStep setDefault(Field<T> field);

    @Support({SQLDialect.FIREBIRD, SQLDialect.HSQLDB, SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB})
    @CheckReturnValue
    @NotNull
    AlterDomainFinalStep dropDefault();

    @Support({SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    AlterDomainFinalStep setNotNull();

    @Support({SQLDialect.POSTGRES})
    @CheckReturnValue
    @NotNull
    AlterDomainFinalStep dropNotNull();
}
