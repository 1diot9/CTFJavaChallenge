package org.jooq;

import java.sql.PreparedStatement;
import java.text.DecimalFormat;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.Context;
import org.jooq.RenderContext;
import org.jooq.conf.ParamType;
import org.jooq.exception.DataAccessException;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/Context.class */
public interface Context<C extends Context<C>> extends ExecuteScope {
    @NotNull
    C visit(QueryPart queryPart) throws DataAccessException;

    @NotNull
    C visit(Condition condition) throws DataAccessException;

    @NotNull
    C visit(Field<?> field) throws DataAccessException;

    @NotNull
    C visitSubquery(QueryPart queryPart) throws DataAccessException;

    @NotNull
    C start(Clause clause);

    @NotNull
    C end(Clause clause);

    @NotNull
    C data(Object obj, Object obj2, Consumer<? super C> consumer);

    boolean declareFields();

    @NotNull
    C declareFields(boolean z);

    @NotNull
    C declareFields(boolean z, Consumer<? super C> consumer);

    boolean declareTables();

    @NotNull
    C declareTables(boolean z);

    @NotNull
    C declareTables(boolean z, Consumer<? super C> consumer);

    boolean declareAliases();

    @NotNull
    C declareAliases(boolean z);

    @NotNull
    C declareAliases(boolean z, Consumer<? super C> consumer);

    boolean declareWindows();

    @NotNull
    C declareWindows(boolean z);

    @NotNull
    C declareWindows(boolean z, Consumer<? super C> consumer);

    boolean declareCTE();

    @NotNull
    C declareCTE(boolean z);

    @NotNull
    C declareCTE(boolean z, Consumer<? super C> consumer);

    @Nullable
    QueryPart topLevel();

    @NotNull
    C topLevel(QueryPart queryPart);

    @Nullable
    QueryPart topLevelForLanguageContext();

    @NotNull
    C topLevelForLanguageContext(QueryPart queryPart);

    boolean subquery();

    @NotNull
    C subquery(boolean z);

    @NotNull
    C subquery(boolean z, QueryPart queryPart);

    boolean derivedTableSubquery();

    @NotNull
    C derivedTableSubquery(boolean z);

    boolean setOperationSubquery();

    @NotNull
    C setOperationSubquery(boolean z);

    boolean predicandSubquery();

    @NotNull
    C predicandSubquery(boolean z);

    int subqueryLevel();

    int scopeLevel();

    @NotNull
    C scopeStart();

    @NotNull
    C scopeStart(QueryPart queryPart);

    @Nullable
    QueryPart scopePart();

    @NotNull
    C scopeMarkStart(QueryPart queryPart);

    @NotNull
    C scopeRegister(QueryPart queryPart);

    @NotNull
    C scopeRegisterAndMark(QueryPart queryPart, boolean z);

    @NotNull
    C scopeRegister(QueryPart queryPart, boolean z);

    @NotNull
    C scopeRegister(QueryPart queryPart, boolean z, QueryPart queryPart2);

    boolean inScope(QueryPart queryPart);

    boolean inCurrentScope(QueryPart queryPart);

    @NotNull
    QueryPart scopeMapping(QueryPart queryPart);

    @NotNull
    C scopeMarkEnd(QueryPart queryPart);

    @NotNull
    C scopeEnd();

    boolean stringLiteral();

    @NotNull
    C stringLiteral(boolean z);

    int nextIndex();

    int peekIndex();

    @NotNull
    C skipUpdateCount();

    @NotNull
    C skipUpdateCounts(int i);

    int skipUpdateCounts();

    @Nullable
    PreparedStatement statement();

    @NotNull
    BindContext bindValue(Object obj, Field<?> field) throws DataAccessException;

    @NotNull
    String peekAlias();

    @NotNull
    String nextAlias();

    @NotNull
    String render();

    @NotNull
    String render(QueryPart queryPart);

    @Deprecated(forRemoval = true, since = "3.10")
    @NotNull
    C keyword(String str);

    @NotNull
    C sql(String str);

    @NotNull
    C sql(String str, boolean z);

    @NotNull
    C sqlIndentStart(String str);

    @NotNull
    C sqlIndentEnd(String str);

    @NotNull
    C sqlIndentStart();

    @NotNull
    C sqlIndentEnd();

    @NotNull
    C sql(char c);

    @NotNull
    C sqlIndentStart(char c);

    @NotNull
    C sqlIndentEnd(char c);

    @NotNull
    C sql(int i);

    @NotNull
    C sql(long j);

    DecimalFormat floatFormat();

    @NotNull
    C sql(float f);

    DecimalFormat doubleFormat();

    @NotNull
    C sql(double d);

    @NotNull
    C format(boolean z);

    boolean format();

    @NotNull
    C formatNewLine();

    @NotNull
    C formatNewLineAfterPrintMargin();

    @NotNull
    C formatSeparator();

    @NotNull
    C separatorRequired(boolean z);

    boolean separatorRequired();

    @NotNull
    C formatIndentStart();

    @NotNull
    C formatIndentStart(int i);

    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    C formatIndentLockStart();

    @NotNull
    C formatIndentEnd();

    @NotNull
    C formatIndentEnd(int i);

    @Deprecated(forRemoval = true, since = "3.14")
    @NotNull
    C formatIndentLockEnd();

    @NotNull
    C formatPrintMargin(int i);

    @Deprecated(forRemoval = true, since = "3.10")
    @NotNull
    C literal(String str);

    boolean quote();

    @NotNull
    C quote(boolean z);

    @NotNull
    C quote(boolean z, Consumer<? super C> consumer);

    boolean qualify();

    @NotNull
    C qualify(boolean z);

    @NotNull
    C qualify(boolean z, Consumer<? super C> consumer);

    boolean qualifySchema();

    @NotNull
    C qualifySchema(boolean z);

    @NotNull
    C qualifySchema(boolean z, Consumer<? super C> consumer);

    boolean qualifyCatalog();

    @NotNull
    C qualifyCatalog(boolean z);

    @NotNull
    C qualifyCatalog(boolean z, Consumer<? super C> consumer);

    @NotNull
    ParamType paramType();

    @NotNull
    C paramType(ParamType paramType);

    @NotNull
    C visit(QueryPart queryPart, ParamType paramType);

    @NotNull
    C paramTypeIf(ParamType paramType, boolean z);

    @NotNull
    C paramType(ParamType paramType, Consumer<? super C> consumer);

    @NotNull
    C paramTypeIf(ParamType paramType, boolean z, Consumer<? super C> consumer);

    @NotNull
    LanguageContext languageContext();

    @NotNull
    C languageContext(LanguageContext languageContext);

    @NotNull
    C languageContext(LanguageContext languageContext, Consumer<? super C> consumer);

    @NotNull
    C languageContext(LanguageContext languageContext, QueryPart queryPart, Consumer<? super C> consumer);

    @NotNull
    C languageContextIf(LanguageContext languageContext, boolean z);

    @NotNull
    RenderContext.CastMode castMode();

    @NotNull
    C castMode(RenderContext.CastMode castMode);

    @NotNull
    C castMode(RenderContext.CastMode castMode, Consumer<? super C> consumer);

    @NotNull
    C castModeIf(RenderContext.CastMode castMode, boolean z);
}
