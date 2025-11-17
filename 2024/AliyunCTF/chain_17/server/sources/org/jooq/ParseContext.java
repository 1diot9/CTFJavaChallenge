package org.jooq;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.impl.ParserException;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/ParseContext.class */
public interface ParseContext extends Scope {
    @NotNull
    SQLDialect parseDialect();

    @NotNull
    SQLDialect parseFamily();

    @NotNull
    SQLDialectCategory parseCategory();

    @NotNull
    LanguageContext languageContext();

    char[] characters();

    @NotNull
    ParseContext characters(char[] cArr);

    char character();

    char character(int i);

    int position();

    boolean position(int i);

    boolean peek(char c);

    boolean peek(String str);

    boolean peekKeyword(String str);

    boolean peekKeyword(String... strArr);

    boolean parse(char c) throws ParserException;

    boolean parseIf(char c);

    boolean parse(String str) throws ParserException;

    boolean parseIf(String str);

    boolean parseKeyword(String str) throws ParserException;

    boolean parseKeywordIf(String str);

    boolean parseKeywordIf(String... strArr);

    boolean parseKeyword(String... strArr) throws ParserException;

    @NotNull
    Name parseIdentifier() throws ParserException;

    @Nullable
    Name parseIdentifierIf();

    @NotNull
    Name parseName() throws ParserException;

    @Nullable
    Name parseNameIf();

    boolean parseFunctionNameIf(String str);

    boolean parseFunctionNameIf(String... strArr);

    @NotNull
    String parseStringLiteral() throws ParserException;

    @Nullable
    String parseStringLiteralIf();

    @NotNull
    Long parseUnsignedIntegerLiteral() throws ParserException;

    @Nullable
    Long parseUnsignedIntegerLiteralIf();

    @NotNull
    Long parseSignedIntegerLiteral() throws ParserException;

    @Nullable
    Long parseSignedIntegerLiteralIf();

    @NotNull
    DataType<?> parseDataType() throws ParserException;

    @NotNull
    Field<?> parseField() throws ParserException;

    @NotNull
    SortField<?> parseSortField() throws ParserException;

    @NotNull
    Condition parseCondition() throws ParserException;

    @NotNull
    Table<?> parseTable() throws ParserException;

    @NotNull
    <T> List<T> parseList(String str, Function<? super ParseContext, ? extends T> function);

    @NotNull
    <T> List<T> parseList(Predicate<? super ParseContext> predicate, Function<? super ParseContext, ? extends T> function);

    <T> T parseParenthesised(Function<? super ParseContext, ? extends T> function);

    <T> T parseParenthesised(char c, Function<? super ParseContext, ? extends T> function, char c2);

    <T> T parseParenthesised(String str, Function<? super ParseContext, ? extends T> function, String str2);

    @NotNull
    ParserException exception(String str);
}
