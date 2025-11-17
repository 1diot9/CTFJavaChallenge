package org.h2.command;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import org.h2.api.ErrorCode;
import org.h2.engine.Database;
import org.h2.engine.DbObject;
import org.h2.engine.Mode;
import org.h2.engine.Session;
import org.h2.engine.SessionLocal;
import org.h2.expression.ParameterInterface;
import org.h2.message.DbException;
import org.h2.message.Trace;
import org.h2.result.ResultInterface;
import org.h2.result.ResultWithGeneratedKeys;
import org.h2.result.ResultWithPaddedStrings;
import org.h2.util.Utils;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/command/Command.class */
public abstract class Command implements CommandInterface {
    protected final SessionLocal session;
    protected long startTimeNanos;
    private final Trace trace = getDatabase().getTrace(0);
    private volatile boolean cancel;
    private final String sql;
    private boolean canReuse;

    public abstract boolean isTransactional();

    @Override // org.h2.command.CommandInterface
    public abstract boolean isQuery();

    @Override // org.h2.command.CommandInterface
    public abstract ArrayList<? extends ParameterInterface> getParameters();

    public abstract boolean isReadOnly();

    public abstract ResultInterface queryMeta();

    public abstract ResultWithGeneratedKeys update(Object obj);

    public abstract ResultInterface query(long j);

    public abstract Set<DbObject> getDependencies();

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract boolean isRetryable();

    /* JADX INFO: Access modifiers changed from: package-private */
    public Command(SessionLocal sessionLocal, String str) {
        this.session = sessionLocal;
        this.sql = str;
    }

    @Override // org.h2.command.CommandInterface
    public final ResultInterface getMetaData() {
        return queryMeta();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void start() {
        if (this.trace.isInfoEnabled() || getDatabase().getQueryStatistics()) {
            this.startTimeNanos = Utils.currentNanoTime();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setProgress(Database database, int i) {
        database.setProgress(i, this.sql, 0L, 0L);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void checkCanceled() {
        if (this.cancel) {
            this.cancel = false;
            throw DbException.get(ErrorCode.STATEMENT_WAS_CANCELED);
        }
    }

    @Override // org.h2.command.CommandInterface
    public void stop() {
        if (this.session.isOpen()) {
            commitIfNonTransactional();
            if (isTransactional() && this.session.getAutoCommit()) {
                this.session.commit(false);
            }
        }
        if (this.trace.isInfoEnabled() && this.startTimeNanos != 0) {
            long nanoTime = (System.nanoTime() - this.startTimeNanos) / 1000000;
            if (nanoTime > 100) {
                this.trace.info("slow query: {0} ms", Long.valueOf(nanoTime));
            }
        }
    }

    @Override // org.h2.command.CommandInterface
    public ResultInterface executeQuery(long j, boolean z) {
        this.startTimeNanos = 0L;
        long j2 = 0;
        Database database = getDatabase();
        this.session.waitIfExclusiveModeEnabled();
        boolean z2 = true;
        this.session.lock();
        try {
            this.session.startStatementWithinTransaction(this);
            Session threadLocalSession = this.session.setThreadLocalSession();
            while (true) {
                try {
                    try {
                        database.checkPowerOff();
                        try {
                            ResultInterface query = query(j);
                            z2 = !query.isLazy();
                            if (database.getMode().charPadding == Mode.CharPadding.IN_RESULT_SETS) {
                                ResultInterface resultInterface = ResultWithPaddedStrings.get(query);
                                this.session.resetThreadLocalSession(threadLocalSession);
                                this.session.endStatement();
                                if (z2) {
                                    stop();
                                }
                                return resultInterface;
                            }
                            this.session.resetThreadLocalSession(threadLocalSession);
                            this.session.endStatement();
                            if (z2) {
                                stop();
                            }
                            this.session.unlock();
                            return query;
                        } catch (OutOfMemoryError e) {
                            database.shutdownImmediately();
                            throw DbException.convert(e);
                        } catch (DbException e2) {
                            if (!isRetryable()) {
                                throw e2;
                            }
                            j2 = filterConcurrentUpdate(e2, j2);
                        } catch (Throwable th) {
                            throw DbException.convert(th);
                        }
                    } catch (Throwable th2) {
                        this.session.resetThreadLocalSession(threadLocalSession);
                        this.session.endStatement();
                        if (z2) {
                            stop();
                        }
                        throw th2;
                    }
                } catch (DbException e3) {
                    DbException addSQL = e3.addSQL(this.sql);
                    SQLException sQLException = addSQL.getSQLException();
                    database.exceptionThrown(sQLException, this.sql);
                    if (sQLException.getErrorCode() == 90108) {
                        database.shutdownImmediately();
                        throw addSQL;
                    }
                    database.checkPowerOff();
                    throw addSQL;
                }
            }
        } finally {
            this.session.unlock();
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:10:0x0047, code lost:            r5.session.resetThreadLocalSession(r0);     */
    /* JADX WARN: Code restructure failed: missing block: B:12:0x004f, code lost:            r5.session.endStatement();     */
    /* JADX WARN: Code restructure failed: missing block: B:13:0x0058, code lost:            if (1 == 0) goto L10;     */
    /* JADX WARN: Code restructure failed: missing block: B:14:0x005b, code lost:            stop();     */
    /* JADX WARN: Code restructure failed: missing block: B:19:0x007c, code lost:            return r0;     */
    /* JADX WARN: Code restructure failed: missing block: B:21:0x0062, code lost:            r15 = move-exception;     */
    /* JADX WARN: Code restructure failed: missing block: B:23:0x0066, code lost:            if (0 == 0) goto L14;     */
    /* JADX WARN: Code restructure failed: missing block: B:25:0x006b, code lost:            throw r15;     */
    /* JADX WARN: Code restructure failed: missing block: B:26:0x006c, code lost:            r0.addSuppressed(r15);     */
    @Override // org.h2.command.CommandInterface
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public org.h2.result.ResultWithGeneratedKeys executeUpdate(java.lang.Object r6) {
        /*
            Method dump skipped, instructions count: 339
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.h2.command.Command.executeUpdate(java.lang.Object):org.h2.result.ResultWithGeneratedKeys");
    }

    private void commitIfNonTransactional() {
        if (!isTransactional()) {
            boolean autoCommit = this.session.getAutoCommit();
            this.session.commit(true);
            if (!autoCommit && this.session.getAutoCommit()) {
                this.session.begin();
            }
        }
    }

    private long filterConcurrentUpdate(DbException dbException, long j) {
        int errorCode = dbException.getErrorCode();
        if (errorCode != 90131 && errorCode != 90143 && errorCode != 90112) {
            throw dbException;
        }
        long currentNanoTime = Utils.currentNanoTime();
        if (j == 0 || currentNanoTime - j <= this.session.getLockTimeout() * 1000000) {
            return j == 0 ? currentNanoTime : j;
        }
        throw DbException.get(ErrorCode.LOCK_TIMEOUT_1, dbException, new String[0]);
    }

    @Override // org.h2.command.CommandInterface, java.lang.AutoCloseable
    public void close() {
        this.canReuse = true;
    }

    @Override // org.h2.command.CommandInterface
    public void cancel() {
        this.cancel = true;
    }

    public String toString() {
        return this.sql + Trace.formatParams(getParameters());
    }

    public boolean isCacheable() {
        return false;
    }

    public boolean canReuse() {
        return this.canReuse;
    }

    public void reuse() {
        this.canReuse = false;
        Iterator<? extends ParameterInterface> it = getParameters().iterator();
        while (it.hasNext()) {
            it.next().setValue(null, true);
        }
    }

    public void setCanReuse(boolean z) {
        this.canReuse = z;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final Database getDatabase() {
        return this.session.getDatabase();
    }
}
