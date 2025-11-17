package org.jooq;

import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/ConstraintForeignKeyReferencesStep3.class */
public interface ConstraintForeignKeyReferencesStep3<T1, T2, T3> {
    @Support
    @NotNull
    ConstraintForeignKeyOnStep references(String str);

    @Support
    @NotNull
    ConstraintForeignKeyOnStep references(String str, String str2, String str3, String str4);

    @Support
    @NotNull
    ConstraintForeignKeyOnStep references(Name name);

    @Support
    @NotNull
    ConstraintForeignKeyOnStep references(Name name, Name name2, Name name3, Name name4);

    @Support
    @NotNull
    ConstraintForeignKeyOnStep references(Table<?> table);

    @Support
    @NotNull
    ConstraintForeignKeyOnStep references(Table<?> table, Field<T1> field, Field<T2> field2, Field<T3> field3);
}
