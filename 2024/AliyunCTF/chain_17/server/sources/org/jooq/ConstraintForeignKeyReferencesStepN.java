package org.jooq;

import java.util.Collection;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/ConstraintForeignKeyReferencesStepN.class */
public interface ConstraintForeignKeyReferencesStepN {
    ConstraintForeignKeyOnStep references(String str);

    ConstraintForeignKeyOnStep references(String str, String... strArr);

    ConstraintForeignKeyOnStep references(String str, Collection<? extends String> collection);

    ConstraintForeignKeyOnStep references(Name name);

    ConstraintForeignKeyOnStep references(Name name, Name... nameArr);

    ConstraintForeignKeyOnStep references(Name name, Collection<? extends Name> collection);

    ConstraintForeignKeyOnStep references(Table<?> table);

    ConstraintForeignKeyOnStep references(Table<?> table, Field<?>... fieldArr);

    ConstraintForeignKeyOnStep references(Table<?> table, Collection<? extends Field<?>> collection);
}
