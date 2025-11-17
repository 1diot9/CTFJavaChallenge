package org.jooq.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.jooq.Commits;
import org.jooq.Configuration;
import org.jooq.ContextTransactionalRunnable;
import org.jooq.Files;
import org.jooq.Meta;
import org.jooq.Migration;
import org.jooq.MigrationContext;
import org.jooq.MigrationListener;
import org.jooq.Queries;
import org.jooq.Query;
import org.jooq.Schema;
import org.jooq.exception.DataMigrationException;
import org.jooq.exception.DataMigrationVerificationException;
import org.jooq.tools.JooqLogger;
import org.jooq.tools.StopWatch;
import org.jooq.tools.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/MigrationImpl.class */
final class MigrationImpl extends AbstractScope implements Migration {
    static final JooqLogger log = JooqLogger.getLogger((Class<?>) Migration.class);
    final HistoryImpl history;
    final org.jooq.Commit to;
    org.jooq.Commit from;
    Queries queries;
    Commits commits;

    /* JADX INFO: Access modifiers changed from: package-private */
    public MigrationImpl(Configuration configuration, org.jooq.Commit to) {
        super(HistoryImpl.initCtx(configuration.derive(new ThreadLocalTransactionProvider(configuration.systemConnectionProvider())), configuration.settings().getMigrationDefaultSchema()));
        this.to = to;
        this.history = new HistoryImpl(configuration());
    }

    @Override // org.jooq.Migration
    public final org.jooq.Commit from() {
        if (this.from == null) {
            this.from = currentCommit();
        }
        return this.from;
    }

    @Override // org.jooq.Migration
    public final org.jooq.Commit to() {
        return this.to;
    }

    @Override // org.jooq.Migration
    public final Queries queries() {
        if (this.queries == null) {
            Files files = from().migrateTo(to());
            this.queries = files.from().migrateTo(files.to());
        }
        return this.queries;
    }

    private final Commits commits() {
        if (this.commits == null) {
            this.commits = configuration().commitProvider().provide();
        }
        return this.commits;
    }

    @Override // org.jooq.Migration
    public final void verify() {
        verify0(migrationContext());
    }

    private final void verify0(DefaultMigrationContext ctx) {
        HistoryRecord currentRecord = this.history.currentHistoryRecord(false);
        if (currentRecord != null) {
            switch (currentRecord.getStatus()) {
                case FAILURE:
                    throw new DataMigrationVerificationException("Previous migration attempt from " + currentRecord.getMigratedFrom() + " to " + currentRecord.getMigratedTo() + " has failed. Please resolve before migrating.");
                case STARTING:
                case REVERTING:
                case MIGRATING:
                    throw new DataMigrationVerificationException("Ongoing migration from " + currentRecord.getMigratedFrom() + " to " + currentRecord.getMigratedTo() + ". Please wait until it has finished.");
                default:
                    org.jooq.Commit currentCommit = commits().get(currentRecord.getMigratedTo());
                    if (currentCommit == null) {
                        throw new DataMigrationVerificationException("Version currently installed is not available from CommitProvider: " + currentRecord.getMigratedTo());
                    }
                    break;
            }
        }
        validateCommitProvider(ctx, from());
        validateCommitProvider(ctx, to());
        revertUntracked(ctx, null, currentRecord);
    }

    private final void validateCommitProvider(DefaultMigrationContext ctx, org.jooq.Commit commit) {
        if (commits().get(commit.id()) == null) {
            throw new DataMigrationVerificationException("Commit is not available from CommitProvider: " + commit.id());
        }
        for (Schema schema : this.history.lookup(commit.meta().getSchemas())) {
            if (!ctx.migratedSchemas().contains(schema)) {
                throw new DataMigrationVerificationException("Schema is referenced from commit, but not configured for migration: " + String.valueOf(schema));
            }
        }
    }

    private final Queries revertUntrackedQueries(Set<Schema> includedSchemas) {
        org.jooq.Commit currentCommit = currentCommit();
        Meta currentMeta = currentCommit.meta();
        Meta meta = dsl().meta();
        Objects.requireNonNull(includedSchemas);
        Meta existingMeta = meta.filterSchemas((v1) -> {
            return r1.contains(v1);
        });
        Set<Schema> expectedSchemas = new HashSet<>();
        expectedSchemas.addAll(this.history.lookup(from().meta().getSchemas()));
        expectedSchemas.addAll(this.history.lookup(to().meta().getSchemas()));
        expectedSchemas.retainAll(includedSchemas);
        for (Schema schema : existingMeta.getSchemas()) {
            if (includedSchemas.contains(schema)) {
                existingMeta = existingMeta.apply(DSL.dropTableIfExists(schema.getQualifiedName().append(History.HISTORY.getUnqualifiedName())).cascade());
                if (!expectedSchemas.contains(schema)) {
                    existingMeta = existingMeta.apply(DSL.dropSchemaIfExists(schema).cascade());
                } else {
                    currentMeta = currentMeta.apply(DSL.createSchemaIfNotExists(schema));
                }
            }
        }
        return existingMeta.migrateTo(currentMeta);
    }

    private final void revertUntracked(DefaultMigrationContext ctx, MigrationListener listener, HistoryRecord currentRecord) {
        if (ctx.revertUntrackedQueries.queries().length > 0) {
            if (!Boolean.TRUE.equals(dsl().settings().isMigrationRevertUntracked())) {
                throw new DataMigrationVerificationException("Non-empty difference between actual schema and migration from schema: " + String.valueOf(ctx.revertUntrackedQueries) + (currentRecord == null ? "\n\nUse Settings.migrationAutoBaseline to automatically set a baseline" : ""));
            }
            if (listener != null) {
                execute(ctx, listener, ctx.revertUntrackedQueries);
            }
        }
    }

    final DefaultMigrationContext migrationContext() {
        Set<Schema> schemas = this.history.schemas();
        return new DefaultMigrationContext(configuration(), schemas, from(), to(), queries(), revertUntrackedQueries(schemas));
    }

    @Override // org.jooq.Migration
    public final void execute() {
        run(() -> {
            DefaultMigrationContext ctx = migrationContext();
            MigrationListener listener = new MigrationListeners(this.configuration);
            if (!Boolean.FALSE.equals(dsl().settings().isMigrationAutoVerification())) {
                verify0(ctx);
            }
            try {
                listener.migrationStart(ctx);
                if (from().equals(to())) {
                    log.info("jOOQ Migrations", "Version " + to().id() + " is already installed as the current version.");
                    listener.migrationEnd(ctx);
                    return;
                }
                log.info("jOOQ Migrations", "Version " + from().id() + " is being migrated to " + to().id());
                StopWatch watch = new StopWatch();
                if (log.isDebugEnabled()) {
                    for (Query query : queries()) {
                        log.debug("jOOQ Migrations", dsl().renderInlined(query));
                    }
                }
                HistoryRecord record = createRecord(HistoryStatus.STARTING);
                try {
                    log(watch, record, HistoryStatus.REVERTING);
                    revertUntracked(ctx, listener, record);
                    log(watch, record, HistoryStatus.MIGRATING);
                    execute(ctx, listener, queries());
                    log(watch, record, HistoryStatus.SUCCESS);
                } catch (Exception e) {
                    StringWriter s = new StringWriter();
                    e.printStackTrace(new PrintWriter(s));
                    log.error("jOOQ Migrations", "Version " + from().id() + " migration to " + to().id() + " failed: " + e.getMessage());
                    log(watch, record, HistoryStatus.FAILURE, HistoryResolution.OPEN, s.toString());
                    throw new DataMigrationRedoLogException(record, e);
                }
            } finally {
                listener.migrationEnd(ctx);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/MigrationImpl$DataMigrationRedoLogException.class */
    public static final class DataMigrationRedoLogException extends DataMigrationException {
        final HistoryRecord record;

        public DataMigrationRedoLogException(HistoryRecord record, Exception cause) {
            super("Redo log", cause);
            this.record = record;
        }
    }

    private final HistoryRecord createRecord(HistoryStatus status) {
        HistoryRecord record = (HistoryRecord) this.history.historyCtx.newRecord(History.HISTORY);
        record.setJooqVersion("3.19.3").setMigratedAt(new Timestamp(dsl().configuration().clock().instant().toEpochMilli())).setMigratedFrom(from().id()).setMigratedTo(to().id()).setMigratedToTags(new org.jooq.tools.json.JSONArray(Tools.map(to().tags(), (v0) -> {
            return v0.id();
        })).toString()).setMigrationTime(0L).setSql(queries().toString()).setSqlCount(Integer.valueOf(queries().queries().length)).setStatus(status).insert();
        return record;
    }

    private final void log(StopWatch watch, HistoryRecord record, HistoryStatus status) {
        log(watch, record, status, null, null);
    }

    private final void log(StopWatch watch, HistoryRecord record, HistoryStatus status, HistoryResolution resolution, String message) {
        record.setMigrationTime(Long.valueOf(watch.split() / 1000000)).setStatus(status).setStatusMessage(message).setResolution(resolution).update();
    }

    private final void execute(DefaultMigrationContext ctx, MigrationListener listener, Queries q) {
        listener.queriesStart(ctx);
        for (Query query : q.queries()) {
            ctx.query(query);
            listener.queryStart(ctx);
            query.execute();
            listener.queryEnd(ctx);
            ctx.query(null);
        }
        listener.queriesEnd(ctx);
    }

    final void init() {
        this.history.init();
        MigrationContext ctx = migrationContext();
        if (Boolean.TRUE.equals(ctx.settings().isMigrationSchemataCreateSchemaIfNotExists())) {
            for (Schema schema : ctx.migratedSchemas()) {
                dsl().createSchemaIfNotExists(schema).execute();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final org.jooq.Commit currentCommit() {
        HistoryRecord currentRecord = this.history.currentHistoryRecord(true);
        if (currentRecord == null) {
            org.jooq.Commit result = Boolean.TRUE.equals(settings().isMigrationAutoBaseline()) ? to() : to().root();
            if (result == null) {
                throw new DataMigrationVerificationException("CommitProvider did not provide a root version for " + to().id());
            }
            return result;
        }
        org.jooq.Commit result2 = commits().get(currentRecord.getMigratedTo());
        if (result2 == null) {
            throw new DataMigrationVerificationException("CommitProvider did not provide a version for " + currentRecord.getMigratedTo());
        }
        return result2;
    }

    private final void run(ContextTransactionalRunnable runnable) {
        try {
            init();
            dsl().transaction(runnable);
        } catch (DataMigrationRedoLogException e) {
            HistoryRecord record = this.history.currentHistoryRecord(false);
            if (record == null || !StringUtils.equals(e.record.getId(), record.getId())) {
                e.record.changed(true);
                e.record.insert();
            }
            Throwable cause = e.getCause();
            if (cause instanceof DataMigrationException) {
                DataMigrationException r = (DataMigrationException) cause;
                throw r;
            }
            throw new DataMigrationException("Exception during migration", e);
        } catch (DataMigrationException e2) {
            throw e2;
        } catch (Exception e3) {
            throw new DataMigrationException("Exception during migration", e3);
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("-- Migration\n--   From: ").append(from().id()).append("\n").append("--   To  : ").append(to().id()).append("\n").append(queries());
        return sb.toString();
    }
}
