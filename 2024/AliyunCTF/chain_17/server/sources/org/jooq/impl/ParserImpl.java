package org.jooq.impl;

import org.jooq.Condition;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Meta;
import org.jooq.Name;
import org.jooq.Parser;
import org.jooq.Queries;
import org.jooq.Query;
import org.jooq.ResultQuery;
import org.jooq.Row;
import org.jooq.Select;
import org.jooq.Statement;
import org.jooq.Table;
import org.jooq.conf.ParseWithMetaLookups;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ParserImpl.class */
final class ParserImpl implements Parser {
    private final DSLContext dsl;
    private final ParseWithMetaLookups metaLookups;
    private final Meta meta;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ParserImpl(Configuration configuration) {
        this.dsl = DSL.using(configuration);
        this.metaLookups = configuration.settings().getParseWithMetaLookups();
        this.meta = (this.metaLookups == ParseWithMetaLookups.IGNORE_ON_FAILURE || this.metaLookups == ParseWithMetaLookups.THROW_ON_FAILURE) ? this.dsl.meta() : null;
    }

    private final DefaultParseContext ctx(String sql, Object... bindings) {
        return new DefaultParseContext(this.dsl, this.meta, this.metaLookups, sql, bindings);
    }

    @Override // org.jooq.Parser
    public final Queries parse(String sql) {
        return parse(sql, Tools.EMPTY_OBJECT);
    }

    @Override // org.jooq.Parser
    public final Queries parse(String sql, Object... bindings) {
        return ctx(sql, bindings).parse();
    }

    @Override // org.jooq.Parser
    public final Query parseQuery(String sql) {
        return parseQuery(sql, Tools.EMPTY_OBJECT);
    }

    @Override // org.jooq.Parser
    public final Query parseQuery(String sql, Object... bindings) {
        return ctx(sql, bindings).parseQuery0();
    }

    @Override // org.jooq.Parser
    public final Statement parseStatement(String sql) {
        return parseStatement(sql, Tools.EMPTY_OBJECT);
    }

    @Override // org.jooq.Parser
    public final Statement parseStatement(String sql, Object... bindings) {
        return ctx(sql, bindings).parseStatementAndSemicolonIf();
    }

    @Override // org.jooq.Parser
    public final ResultQuery<?> parseResultQuery(String sql) {
        return parseResultQuery(sql, Tools.EMPTY_OBJECT);
    }

    @Override // org.jooq.Parser
    public final ResultQuery<?> parseResultQuery(String sql, Object... bindings) {
        return ctx(sql, bindings).parseResultQuery0();
    }

    @Override // org.jooq.Parser
    public final Select<?> parseSelect(String sql) {
        return parseSelect(sql, Tools.EMPTY_OBJECT);
    }

    @Override // org.jooq.Parser
    public final Select<?> parseSelect(String sql, Object... bindings) {
        return ctx(sql, bindings).parseSelect0();
    }

    @Override // org.jooq.Parser
    public final Table<?> parseTable(String sql) {
        return parseTable(sql, Tools.EMPTY_OBJECT);
    }

    @Override // org.jooq.Parser
    public final Table<?> parseTable(String sql, Object... bindings) {
        return ctx(sql, bindings).parseTable0();
    }

    @Override // org.jooq.Parser
    public final Field<?> parseField(String sql) {
        return parseField(sql, Tools.EMPTY_OBJECT);
    }

    @Override // org.jooq.Parser
    public final Field<?> parseField(String sql, Object... bindings) {
        return ctx(sql, bindings).parseField0();
    }

    @Override // org.jooq.Parser
    public final Row parseRow(String sql) {
        return parseRow(sql, Tools.EMPTY_OBJECT);
    }

    @Override // org.jooq.Parser
    public final Row parseRow(String sql, Object... bindings) {
        return ctx(sql, bindings).parseRow0();
    }

    @Override // org.jooq.Parser
    public final Condition parseCondition(String sql) {
        return parseCondition(sql, Tools.EMPTY_OBJECT);
    }

    @Override // org.jooq.Parser
    public final Condition parseCondition(String sql, Object... bindings) {
        return ctx(sql, bindings).parseCondition0();
    }

    @Override // org.jooq.Parser
    public final Name parseName(String sql) {
        return parseName(sql, Tools.EMPTY_OBJECT);
    }

    @Override // org.jooq.Parser
    public final Name parseName(String sql, Object... bindings) {
        return ctx(sql, bindings).parseName0();
    }
}
