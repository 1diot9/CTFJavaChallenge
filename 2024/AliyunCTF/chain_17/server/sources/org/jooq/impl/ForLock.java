package org.jooq.impl;

import java.util.Collection;
import java.util.Set;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Keyword;
import org.jooq.SQLDialect;
import org.jooq.impl.QOM;
import org.jooq.impl.Tools;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ForLock.class */
public final class ForLock extends AbstractQueryPart implements QOM.UNotYetImplemented {
    private static final Set<SQLDialect> NO_SUPPORT_FOR_UPDATE_QUALIFIED = SQLDialect.supportedBy(SQLDialect.DERBY, SQLDialect.FIREBIRD, SQLDialect.H2, SQLDialect.HSQLDB);
    private static final Set<SQLDialect> NO_SUPPORT_STANDARD_FOR_SHARE = SQLDialect.supportedUntil(SQLDialect.MARIADB);
    private static final Set<SQLDialect> EMULATE_FOR_UPDATE_WAIT_MY = SQLDialect.supportedUntil(SQLDialect.MYSQL);
    private static final Set<SQLDialect> EMULATE_FOR_UPDATE_WAIT_PG = SQLDialect.supportedBy(SQLDialect.POSTGRES, SQLDialect.YUGABYTEDB);
    QueryPartList<Field<?>> forLockOf;
    TableList forLockOfTables;
    ForLockMode forLockMode;
    ForLockWaitMode forLockWaitMode;
    int forLockWait;

    /* JADX WARN: Type inference failed for: r0v15, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v33, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v38, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v4, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v45, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v54, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v57, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v64, types: [org.jooq.Context] */
    /* JADX WARN: Type inference failed for: r0v69, types: [org.jooq.Context] */
    @Override // org.jooq.QueryPartInternal
    public final void accept(Context<?> ctx) {
        switch (this.forLockMode) {
            case UPDATE:
            case NO_KEY_UPDATE:
            case KEY_SHARE:
            default:
                ctx.formatSeparator().visit(Keywords.K_FOR).sql(' ').visit(this.forLockMode.toKeyword());
                break;
            case SHARE:
                if (NO_SUPPORT_STANDARD_FOR_SHARE.contains(ctx.dialect())) {
                    ctx.formatSeparator().visit(Keywords.K_LOCK_IN_SHARE_MODE);
                    break;
                } else {
                    ctx.formatSeparator().visit(Keywords.K_FOR).sql(' ').visit(this.forLockMode.toKeyword());
                    break;
                }
        }
        if (Tools.isNotEmpty((Collection<?>) this.forLockOf)) {
            ctx.qualify(!NO_SUPPORT_FOR_UPDATE_QUALIFIED.contains(ctx.dialect()) && ctx.qualify(), c -> {
                c.sql(' ').visit(Keywords.K_OF).sql(' ').visit(this.forLockOf);
            });
        } else if (Tools.isNotEmpty((Collection<?>) this.forLockOfTables)) {
            ctx.sql(' ').visit(Keywords.K_OF).sql(' ');
            switch (ctx.family()) {
                case DERBY:
                    this.forLockOfTables.toSQLFields(ctx);
                    break;
                default:
                    ctx.visit(QueryPartCollectionView.wrap(this.forLockOfTables).qualify(false));
                    break;
            }
        }
        if (ctx.family() == SQLDialect.FIREBIRD) {
            ctx.sql(' ').visit(Keywords.K_WITH_LOCK);
        }
        if (this.forLockWaitMode != null) {
            if (this.forLockWaitMode == ForLockWaitMode.WAIT && EMULATE_FOR_UPDATE_WAIT_PG.contains(ctx.dialect())) {
                Tools.prependSQL(ctx.skipUpdateCount(), ctx.dsl().setLocal(Names.N_LOCK_TIMEOUT, DSL.inline(this.forLockWait * 1000)));
                return;
            }
            if (this.forLockWaitMode == ForLockWaitMode.WAIT && EMULATE_FOR_UPDATE_WAIT_MY.contains(ctx.dialect())) {
                if (ctx.data(Tools.BooleanDataKey.DATA_LOCK_WAIT_TIMEOUT_SET) == null) {
                    ctx.skipUpdateCounts(2).data(Tools.BooleanDataKey.DATA_LOCK_WAIT_TIMEOUT_SET, true);
                    Tools.prependSQL(ctx, ctx.dsl().query("{set} @t = @@innodb_lock_wait_timeout"), ctx.dsl().query("{set} @@innodb_lock_wait_timeout = {0}", DSL.inline(this.forLockWait)));
                    Tools.appendSQL(ctx, ctx.dsl().query("{set} @@innodb_lock_wait_timeout = @t"));
                    return;
                }
                return;
            }
            ctx.sql(' ').visit(this.forLockWaitMode.toKeyword());
            if (this.forLockWaitMode == ForLockWaitMode.WAIT) {
                ctx.sql(' ').sql(this.forLockWait);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ForLock$ForLockMode.class */
    public enum ForLockMode {
        UPDATE("update"),
        NO_KEY_UPDATE("no key update"),
        SHARE("share"),
        KEY_SHARE("key share");

        private final Keyword keyword;

        ForLockMode(String sql) {
            this.keyword = DSL.keyword(sql);
        }

        public final Keyword toKeyword() {
            return this.keyword;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/ForLock$ForLockWaitMode.class */
    public enum ForLockWaitMode {
        WAIT("wait"),
        NOWAIT("nowait"),
        SKIP_LOCKED("skip locked");

        private final Keyword keyword;

        ForLockWaitMode(String sql) {
            this.keyword = DSL.keyword(sql);
        }

        public final Keyword toKeyword() {
            return this.keyword;
        }
    }
}
