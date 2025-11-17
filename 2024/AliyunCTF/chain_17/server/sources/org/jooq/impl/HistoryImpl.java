package org.jooq.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.jetbrains.annotations.Nullable;
import org.jooq.Commits;
import org.jooq.Condition;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.Schema;
import org.jooq.SelectWhereStep;
import org.jooq.TableField;
import org.jooq.Version;
import org.jooq.conf.InterpreterSearchSchema;
import org.jooq.conf.MappedCatalog;
import org.jooq.conf.MappedSchema;
import org.jooq.conf.MigrationSchema;
import org.jooq.conf.RenderMapping;
import org.jooq.exception.DataAccessException;
import org.jooq.exception.DataMigrationVerificationException;
import org.jooq.tools.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/HistoryImpl.class */
public class HistoryImpl extends AbstractScope implements org.jooq.History {
    final DSLContext ctx;
    final DSLContext historyCtx;
    final Commits commits;
    final List<Version> versions;

    /* JADX INFO: Access modifiers changed from: package-private */
    public HistoryImpl(Configuration configuration) {
        super(configuration);
        this.ctx = configuration.dsl();
        this.historyCtx = initCtx(configuration, configuration.settings().getMigrationHistorySchema()).dsl();
        this.commits = configuration.commitProvider().provide();
        this.versions = initVersions();
    }

    @Override // java.lang.Iterable
    public final Iterator<Version> iterator() {
        return Collections.unmodifiableList(this.versions).iterator();
    }

    @Override // org.jooq.History
    public final Version root() {
        if (!Tools.isEmpty((Collection<?>) this.versions)) {
            return this.versions.get(0);
        }
        throw new DataMigrationVerificationException("No versions are available");
    }

    @Override // org.jooq.History
    public final Version current() {
        if (!Tools.isEmpty((Collection<?>) this.versions)) {
            return this.versions.get(this.versions.size() - 1);
        }
        throw new DataMigrationVerificationException("No versions are available");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Set<Schema> schemas() {
        Set<Schema> set = new LinkedHashSet<>();
        for (MigrationSchema schema : this.configuration.settings().getMigrationSchemata()) {
            addSchema(set, schema);
        }
        if (this.configuration.settings().getMigrationDefaultSchema() != null) {
            addSchema(set, this.configuration.settings().getMigrationDefaultSchema());
            set.add(DSL.schema(""));
        }
        return set;
    }

    private final void addSchema(Set<Schema> set, MigrationSchema schema) {
        if (schema != null) {
            set.addAll(lookup(Arrays.asList(DSL.schema(DSL.name(schema.getCatalog(), schema.getSchema())))));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Collection<Schema> lookup(List<Schema> schemas) {
        Collection<Schema> result = schemas;
        List<InterpreterSearchSchema> searchPath = configuration().settings().getInterpreterSearchPath();
        if (!searchPath.isEmpty()) {
            result = new HashSet<>();
            Schema defaultSchema = DSL.schema(DSL.name(searchPath.get(0).getCatalog(), searchPath.get(0).getSchema()));
            for (Schema schema : schemas) {
                if (schema.getQualifiedName().empty()) {
                    result.add(defaultSchema);
                } else {
                    result.add(schema);
                }
            }
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final Configuration initCtx(Configuration configuration, MigrationSchema defaultSchema) {
        if (defaultSchema != null) {
            Configuration result = configuration.derive();
            if (!StringUtils.isBlank(defaultSchema.getCatalog())) {
                result.settings().withRenderMapping(new RenderMapping().withCatalogs(new MappedCatalog().withInput("").withOutput(defaultSchema.getCatalog()).withSchemata(new MappedSchema().withInput("").withOutput(defaultSchema.getSchema()))));
            } else if (!StringUtils.isBlank(defaultSchema.getSchema())) {
                result.settings().withRenderMapping(new RenderMapping().withSchemata(new MappedSchema().withInput("").withOutput(defaultSchema.getSchema())));
            }
            return result;
        }
        return configuration;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Multi-variable type inference failed */
    @Nullable
    public final HistoryRecord currentHistoryRecord(boolean successOnly) {
        Condition or;
        if (existsHistory()) {
            SelectWhereStep selectFrom = this.historyCtx.selectFrom(History.HISTORY);
            if (successOnly) {
                or = History.HISTORY.STATUS.eq(DSL.inline(HistoryStatus.SUCCESS));
            } else {
                or = History.HISTORY.STATUS.eq(DSL.inline(HistoryStatus.SUCCESS)).or(History.HISTORY.RESOLUTION.eq((TableField<HistoryRecord, HistoryResolution>) HistoryResolution.OPEN));
            }
            return (HistoryRecord) selectFrom.where(or).orderBy(History.HISTORY.MIGRATED_AT.desc(), History.HISTORY.ID.desc()).limit((Number) 1).fetchOne();
        }
        return null;
    }

    final boolean existsHistory() {
        try {
            Configuration c = this.historyCtx.configuration().derive();
            c.data("org.jooq.tools.LoggerListener.exception.mute", true);
            c.dsl().fetchExists(History.HISTORY);
            return true;
        } catch (DataAccessException e) {
            return false;
        }
    }

    private final List<Version> initVersions() {
        List<Version> result = new ArrayList<>();
        if (existsHistory()) {
            result.add(this.commits.root().version());
            for (HistoryRecord r : this.historyCtx.selectFrom(History.HISTORY).where(History.HISTORY.STATUS.eq(DSL.inline(HistoryStatus.SUCCESS))).orderBy(History.HISTORY.ID.asc())) {
                org.jooq.Commit commit = this.commits.get(r.getMigratedTo());
                if (commit != null) {
                    result.add(commit.version());
                } else {
                    throw new DataMigrationVerificationException("CommitProvider didn't provide version for ID: " + r.getMigratedTo());
                }
            }
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void init() {
        if (!existsHistory()) {
            if ((Boolean.TRUE.equals(this.historyCtx.settings().isMigrationHistorySchemaCreateSchemaIfNotExists()) && this.historyCtx.settings().getMigrationHistorySchema() != null) || (Boolean.TRUE.equals(this.historyCtx.settings().isMigrationSchemataCreateSchemaIfNotExists()) && this.historyCtx.settings().getMigrationDefaultSchema() != null)) {
                this.historyCtx.createSchemaIfNotExists("").execute();
            }
            this.historyCtx.meta(History.HISTORY).ddl().executeBatch();
        }
    }

    @Override // org.jooq.History
    public final void resolve(String message) {
        HistoryRecord h = currentHistoryRecord(false);
        if (h != null) {
            h.setResolution(HistoryResolution.RESOLVED).setResolutionMessage(message).update();
            return;
        }
        throw new DataMigrationVerificationException("No current history record found to resolve");
    }

    public int hashCode() {
        return this.versions.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj instanceof HistoryImpl) {
            HistoryImpl h = (HistoryImpl) obj;
            return this.versions.equals(h.versions);
        }
        return false;
    }

    public String toString() {
        return "History [" + String.valueOf(current()) + "]";
    }
}
