package org.jooq;

import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/ConstraintForeignKeyReferencesStep7.class */
public interface ConstraintForeignKeyReferencesStep7<T1, T2, T3, T4, T5, T6, T7> {
    @Support
    @NotNull
    ConstraintForeignKeyOnStep references(String str);

    @Support
    @NotNull
    ConstraintForeignKeyOnStep references(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8);

    @Support
    @NotNull
    ConstraintForeignKeyOnStep references(Name name);

    @Support
    @NotNull
    ConstraintForeignKeyOnStep references(Name name, Name name2, Name name3, Name name4, Name name5, Name name6, Name name7, Name name8);

    @Support
    @NotNull
    ConstraintForeignKeyOnStep references(Table<?> table);

    @Support
    @NotNull
    ConstraintForeignKeyOnStep references(Table<?> table, Field<T1> field, Field<T2> field2, Field<T3> field3, Field<T4> field4, Field<T5> field5, Field<T6> field6, Field<T7> field7);
}
