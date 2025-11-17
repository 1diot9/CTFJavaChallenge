package org.jooq.impl;

import java.io.Closeable;
import java.io.Reader;
import java.util.Scanner;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.Meta;
import org.jooq.MetaProvider;
import org.jooq.Query;
import org.jooq.Source;
import org.jooq.tools.JooqLogger;
import org.jooq.tools.jdbc.JDBCUtils;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/InterpreterMetaProvider.class */
final class InterpreterMetaProvider implements MetaProvider {
    private static final JooqLogger log = JooqLogger.getLogger((Class<?>) InterpreterMetaProvider.class);
    private final Configuration configuration;
    private final Source[] sources;
    private final Query[] queries;

    public InterpreterMetaProvider(Configuration configuration, Source... sources) {
        this.configuration = Tools.configuration(configuration);
        this.sources = sources;
        this.queries = null;
    }

    public InterpreterMetaProvider(Configuration configuration, Query... queries) {
        this.configuration = Tools.configuration(configuration);
        this.sources = null;
        this.queries = queries;
    }

    @Override // org.jooq.MetaProvider
    public Meta provide() {
        Interpreter interpreter = new Interpreter(this.configuration);
        Configuration localConfiguration = this.configuration.derive();
        DSLContext ctx = DSL.using(localConfiguration);
        if (this.sources != null) {
            for (Source source : this.sources) {
                loadSource(ctx, source, interpreter);
            }
        } else {
            for (Query query : this.queries) {
                interpreter.accept(query);
            }
        }
        return interpreter.meta();
    }

    private final void loadSource(DSLContext ctx, Source source, Interpreter interpreter) {
        Reader reader = null;
        try {
            try {
                Reader reader2 = source.reader();
                reader = reader2;
                Scanner s = new Scanner(reader2).useDelimiter("\\A");
                for (Query query : ctx.parser().parse(s.hasNext() ? s.next() : "")) {
                    interpreter.accept(query);
                }
                JDBCUtils.safeClose((Closeable) reader);
            } catch (ParserException e) {
                log.error((Object) ("An exception occurred while parsing a DDL script: " + e.getMessage() + ". Please report this error to https://jooq.org/bug"), (Throwable) e);
                throw e;
            }
        } catch (Throwable th) {
            JDBCUtils.safeClose((Closeable) reader);
            throw th;
        }
    }
}
