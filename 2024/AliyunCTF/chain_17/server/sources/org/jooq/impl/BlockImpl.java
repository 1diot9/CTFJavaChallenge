package org.jooq.impl;

import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;
import org.jooq.Block;
import org.jooq.Configuration;
import org.jooq.Context;
import org.jooq.Keyword;
import org.jooq.LanguageContext;
import org.jooq.SQLDialect;
import org.jooq.Statement;
import org.jooq.conf.ParamType;
import org.jooq.impl.QOM;
import org.jooq.impl.Tools;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/BlockImpl.class */
final class BlockImpl extends AbstractRowCountQuery implements Block {
    private static final Set<SQLDialect> SUPPORTS_NULL_STATEMENT = SQLDialect.supportedBy(SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB);
    final Collection<? extends Statement> statements;
    final boolean alwaysWrapInBeginEnd;
    static final String STATEMENT_VARIABLES = "org.jooq.impl.BlockImpl.statement-variables";

    /* JADX INFO: Access modifiers changed from: package-private */
    public BlockImpl(Configuration configuration, Collection<? extends Statement> statements, boolean alwaysWrapInBeginEnd) {
        super(configuration);
        this.statements = Tools.collect(Tools.filter(statements, s -> {
            return !(s instanceof NullStatement);
        }));
        this.alwaysWrapInBeginEnd = alwaysWrapInBeginEnd;
    }

    /* JADX WARN: Type inference failed for: r0v19, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v35, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v58, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v65, types: [org.jooq.Context] */
    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {
            case FIREBIRD:
                if (Tools.increment(ctx.data(), Tools.SimpleDataKey.DATA_BLOCK_NESTING)) {
                    ctx.paramType(ParamType.INLINED).visit(Keywords.K_EXECUTE_BLOCK).sql(' ').visit(Keywords.K_AS);
                    ctx.data(Tools.BooleanDataKey.DATA_FORCE_STATIC_STATEMENT, true);
                    scopeDeclarations(ctx.formatIndentStart(), c -> {
                        accept0(c.formatIndentEnd().formatSeparator());
                    });
                } else {
                    accept0(ctx);
                }
                Tools.decrement(ctx.data(), Tools.SimpleDataKey.DATA_BLOCK_NESTING);
                return;
            case POSTGRES:
            case YUGABYTEDB:
                bodyAsString(ctx, Keywords.K_DO, c2 -> {
                    accept0(c2);
                });
                return;
            case H2:
                String name = randomName();
                if (Tools.increment(ctx.data(), Tools.SimpleDataKey.DATA_BLOCK_NESTING)) {
                    ctx.paramType(ParamType.INLINED).visit(Keywords.K_CREATE).sql(' ').visit(Keywords.K_ALIAS).sql(' ').sql(name).sql(' ').visit(Keywords.K_AS).sql(" $$").formatIndentStart().formatSeparator().sql("void x(Connection c) throws SQLException ");
                    ctx.data(Tools.BooleanDataKey.DATA_FORCE_STATIC_STATEMENT, true);
                }
                accept0(ctx);
                if (Tools.decrement(ctx.data(), Tools.SimpleDataKey.DATA_BLOCK_NESTING)) {
                    ctx.formatIndentEnd().formatSeparator().sql("$$;").formatSeparator().visit(Keywords.K_CALL).sql(' ').sql(name).sql("();").formatSeparator().visit(Keywords.K_DROP).sql(' ').visit(Keywords.K_ALIAS).sql(' ').sql(name).sql(';');
                    return;
                }
                return;
            case MYSQL:
                accept0(ctx);
                return;
            case HSQLDB:
            case MARIADB:
            default:
                Tools.increment(ctx.data(), Tools.SimpleDataKey.DATA_BLOCK_NESTING);
                accept0(ctx);
                Tools.decrement(ctx.data(), Tools.SimpleDataKey.DATA_BLOCK_NESTING);
                return;
        }
    }

    static final void scopeDeclarations(Context<?> ctx, Consumer<? super Context<?>> runnable) {
        if (!ctx.configuration().commercial()) {
            runnable.accept(ctx);
        }
    }

    /* JADX WARN: Type inference failed for: r0v10, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v19, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v25, types: [org.jooq.Context] */
    static final void bodyAsString(Context<?> ctx, Keyword keyword, Consumer<? super Context<?>> runnable) {
        ParamType previous = ctx.paramType();
        if (Tools.increment(ctx.data(), Tools.SimpleDataKey.DATA_BLOCK_NESTING)) {
            ctx.paramType(ParamType.INLINED);
            if (keyword != null) {
                ctx.visit(keyword).sql(' ');
            }
            ctx.sql('$').sql(ctx.settings().getRenderDollarQuotedStringToken()).sql('$').formatSeparator().data(Tools.BooleanDataKey.DATA_FORCE_STATIC_STATEMENT, true);
        }
        runnable.accept(ctx);
        if (Tools.decrement(ctx.data(), Tools.SimpleDataKey.DATA_BLOCK_NESTING)) {
            ctx.formatSeparator().sql('$').sql(ctx.settings().getRenderDollarQuotedStringToken()).sql('$').paramType(previous);
        }
    }

    private static final String randomName() {
        long currentTimeMillis = System.currentTimeMillis();
        return "block_" + currentTimeMillis + "_" + currentTimeMillis;
    }

    private final void accept0(Context<?> ctx) {
        boolean wrapInBeginEnd = this.alwaysWrapInBeginEnd;
        if (wrapInBeginEnd) {
            boolean topLevel = ctx.scopeLevel() == -1;
            LanguageContext language = ctx.languageContext();
            if (topLevel && language == LanguageContext.QUERY) {
                ctx.languageContext(LanguageContext.BLOCK);
            }
            begin(ctx, topLevel);
            scopeDeclarations(ctx, c -> {
                accept1(c);
            });
            end(ctx, topLevel);
            if (topLevel && language == LanguageContext.QUERY) {
                ctx.languageContext(language);
                return;
            }
            return;
        }
        accept1(ctx);
    }

    /* JADX WARN: Code restructure failed: missing block: B:5:0x0089, code lost:            return;     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private final void accept1(org.jooq.Context<?> r6) {
        /*
            r5 = this;
            r0 = r5
            java.util.Collection<? extends org.jooq.Statement> r0 = r0.statements
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L27
            int[] r0 = org.jooq.impl.BlockImpl.AnonymousClass1.$SwitchMap$org$jooq$SQLDialect
            r1 = r6
            org.jooq.SQLDialect r1 = r1.family()
            int r1 = r1.ordinal()
            r0 = r0[r1]
            switch(r0) {
                default: goto L24;
            }
        L24:
            goto L89
        L27:
            r0 = r5
            java.util.Collection<? extends org.jooq.Statement> r0 = r0.statements
            java.util.Iterator r0 = r0.iterator()
            r7 = r0
        L31:
            r0 = r7
            boolean r0 = r0.hasNext()
            if (r0 == 0) goto L89
            r0 = r7
            java.lang.Object r0 = r0.next()
            org.jooq.Statement r0 = (org.jooq.Statement) r0
            r8 = r0
            r0 = r8
            boolean r0 = r0 instanceof org.jooq.impl.NullStatement
            if (r0 == 0) goto L5f
            java.util.Set<org.jooq.SQLDialect> r0 = org.jooq.impl.BlockImpl.SUPPORTS_NULL_STATEMENT
            r1 = r6
            org.jooq.SQLDialect r1 = r1.dialect()
            boolean r0 = r0.contains(r1)
            if (r0 != 0) goto L5f
            goto L31
        L5f:
            r0 = r8
            boolean r0 = r0 instanceof org.jooq.Query
            if (r0 == 0) goto L81
            r0 = r8
            boolean r0 = r0 instanceof org.jooq.Block
            if (r0 != 0) goto L81
            r0 = r6
            org.jooq.LanguageContext r1 = org.jooq.LanguageContext.QUERY
            r2 = r8
            r3 = r8
            void r3 = (v1) -> { // java.util.function.Consumer.accept(java.lang.Object):void
                lambda$accept1$4(r3, v1);
            }
            org.jooq.Context r0 = r0.languageContext(r1, r2, r3)
            goto L86
        L81:
            r0 = r6
            r1 = r8
            accept2(r0, r1)
        L86:
            goto L31
        L89:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jooq.impl.BlockImpl.accept1(org.jooq.Context):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void accept2(Context<?> ctx, Statement s) {
        int i;
        int i2;
        ctx.formatSeparator();
        if (ctx instanceof DefaultRenderContext) {
            DefaultRenderContext d = (DefaultRenderContext) ctx;
            i = d.sql.length();
        } else {
            i = 0;
        }
        int position = i;
        ctx.visit(s);
        if (ctx instanceof DefaultRenderContext) {
            DefaultRenderContext d2 = (DefaultRenderContext) ctx;
            i2 = d2.sql.length();
        } else {
            i2 = 0;
        }
        if (position < i2) {
            semicolonAfterStatement(ctx, s);
        }
    }

    private static final void semicolonAfterStatement(Context<?> ctx, Statement s) {
        if (s instanceof Block) {
            return;
        }
        ctx.sql(';');
    }

    /* JADX WARN: Type inference failed for: r0v17, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v9, types: [org.jooq.Context] */
    private static final void begin(Context<?> ctx, boolean topLevel) {
        if (ctx.family() == SQLDialect.H2) {
            ctx.sql('{');
        } else {
            ctx.visit(Keywords.K_BEGIN);
        }
        if (ctx.family() == SQLDialect.MARIADB && Tools.toplevel(ctx.data(), Tools.SimpleDataKey.DATA_BLOCK_NESTING)) {
            ctx.sql(' ').visit(Keywords.K_NOT).sql(' ').visit(Keywords.K_ATOMIC);
        } else if (ctx.family() == SQLDialect.HSQLDB) {
            ctx.sql(' ').visit(Keywords.K_ATOMIC);
        }
        ctx.formatIndentStart();
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [org.jooq.Context] */
    private static final void end(Context<?> ctx, boolean topLevel) {
        ctx.formatIndentEnd().formatSeparator();
        if (ctx.family() == SQLDialect.H2) {
            ctx.sql('}');
        } else {
            ctx.visit(Keywords.K_END);
        }
        switch (ctx.family()) {
            case FIREBIRD:
            case H2:
                return;
            default:
                ctx.sql(';');
                return;
        }
    }

    @Override // org.jooq.Block
    public final QOM.UnmodifiableList<? extends Statement> $statements() {
        return QOM.unmodifiable(this.statements);
    }
}
