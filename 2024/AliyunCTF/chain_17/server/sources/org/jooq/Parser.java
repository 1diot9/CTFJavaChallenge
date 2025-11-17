package org.jooq;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.impl.ParserException;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Parser.class */
public interface Parser {
    @PlainSQL
    @Support
    @NotNull
    Queries parse(String str) throws ParserException;

    @PlainSQL
    @Support
    @NotNull
    Queries parse(String str, Object... objArr) throws ParserException;

    @PlainSQL
    @Support
    @Nullable
    Query parseQuery(String str) throws ParserException;

    @PlainSQL
    @Support
    @Nullable
    Query parseQuery(String str, Object... objArr) throws ParserException;

    @PlainSQL
    @Support
    @Nullable
    Statement parseStatement(String str) throws ParserException;

    @PlainSQL
    @Support
    @Nullable
    Statement parseStatement(String str, Object... objArr) throws ParserException;

    @PlainSQL
    @Support
    @Nullable
    ResultQuery<?> parseResultQuery(String str) throws ParserException;

    @PlainSQL
    @Support
    @Nullable
    ResultQuery<?> parseResultQuery(String str, Object... objArr) throws ParserException;

    @PlainSQL
    @Support
    @Nullable
    Select<?> parseSelect(String str) throws ParserException;

    @PlainSQL
    @Support
    @Nullable
    Select<?> parseSelect(String str, Object... objArr) throws ParserException;

    @PlainSQL
    @Support
    @NotNull
    Table<?> parseTable(String str) throws ParserException;

    @PlainSQL
    @Support
    @NotNull
    Table<?> parseTable(String str, Object... objArr) throws ParserException;

    @PlainSQL
    @Support
    @NotNull
    Field<?> parseField(String str) throws ParserException;

    @PlainSQL
    @Support
    @NotNull
    Field<?> parseField(String str, Object... objArr) throws ParserException;

    @PlainSQL
    @Support
    @NotNull
    Row parseRow(String str) throws ParserException;

    @PlainSQL
    @Support
    @NotNull
    Row parseRow(String str, Object... objArr) throws ParserException;

    @PlainSQL
    @Support
    @NotNull
    Condition parseCondition(String str) throws ParserException;

    @PlainSQL
    @Support
    @NotNull
    Condition parseCondition(String str, Object... objArr) throws ParserException;

    @PlainSQL
    @Support
    @NotNull
    Name parseName(String str) throws ParserException;

    @PlainSQL
    @Support
    @NotNull
    Name parseName(String str, Object... objArr) throws ParserException;
}
