package org.jooq;

import org.jetbrains.annotations.NotNull;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Condition.class */
public interface Condition extends Field<Boolean> {
    @Support
    @NotNull
    Condition and(Field<Boolean> field);

    @PlainSQL
    @Support
    @NotNull
    Condition and(SQL sql);

    @PlainSQL
    @Support
    @NotNull
    Condition and(String str);

    @PlainSQL
    @Support
    @NotNull
    Condition and(String str, Object... objArr);

    @PlainSQL
    @Support
    @NotNull
    Condition and(String str, QueryPart... queryPartArr);

    @Support
    @NotNull
    Condition andNot(Condition condition);

    @Support
    @NotNull
    Condition andNot(Field<Boolean> field);

    @Support
    @NotNull
    Condition andExists(Select<?> select);

    @Support
    @NotNull
    Condition andNotExists(Select<?> select);

    @Support
    @NotNull
    Condition or(Field<Boolean> field);

    @PlainSQL
    @Support
    @NotNull
    Condition or(SQL sql);

    @PlainSQL
    @Support
    @NotNull
    Condition or(String str);

    @PlainSQL
    @Support
    @NotNull
    Condition or(String str, Object... objArr);

    @PlainSQL
    @Support
    @NotNull
    Condition or(String str, QueryPart... queryPartArr);

    @Support
    @NotNull
    Condition orNot(Condition condition);

    @Support
    @NotNull
    Condition orNot(Field<Boolean> field);

    @Support
    @NotNull
    Condition orExists(Select<?> select);

    @Support
    @NotNull
    Condition orNotExists(Select<?> select);

    @Support
    @NotNull
    Condition xor(Field<Boolean> field);

    @PlainSQL
    @Support
    @NotNull
    Condition xor(SQL sql);

    @PlainSQL
    @Support
    @NotNull
    Condition xor(String str);

    @PlainSQL
    @Support
    @NotNull
    Condition xor(String str, Object... objArr);

    @PlainSQL
    @Support
    @NotNull
    Condition xor(String str, QueryPart... queryPartArr);

    @Support
    @NotNull
    Condition xorNot(Condition condition);

    @Support
    @NotNull
    Condition xorNot(Field<Boolean> field);

    @Support
    @NotNull
    Condition xorExists(Select<?> select);

    @Support
    @NotNull
    Condition xorNotExists(Select<?> select);

    @Support
    @NotNull
    Condition and(Condition condition);

    @Support
    @NotNull
    Condition not();

    @Support
    @NotNull
    Condition or(Condition condition);

    @Support
    @NotNull
    Condition xor(Condition condition);
}
