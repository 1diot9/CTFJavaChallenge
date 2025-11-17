package org.jooq;

import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/ConstraintForeignKeyReferencesStep1.class */
public interface ConstraintForeignKeyReferencesStep1<T1> {
    @Support
    @NotNull
    ConstraintForeignKeyOnStep references(String str);

    @Support
    @NotNull
    ConstraintForeignKeyOnStep references(String str, String str2);

    @Support
    @NotNull
    ConstraintForeignKeyOnStep references(Name name);

    @Support
    @NotNull
    ConstraintForeignKeyOnStep references(Name name, Name name2);

    @Support
    @NotNull
    ConstraintForeignKeyOnStep references(Table<?> table);

    @Support
    @NotNull
    ConstraintForeignKeyOnStep references(Table<?> table, Field<T1> field);
}
