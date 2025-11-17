package org.jooq.impl;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import org.jooq.Check;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/History.class */
class History extends TableImpl<HistoryRecord> {
    private static final long serialVersionUID = 1;
    static final History HISTORY = new History();
    final TableField<HistoryRecord, Integer> ID;
    final TableField<HistoryRecord, Timestamp> MIGRATED_AT;
    final TableField<HistoryRecord, String> MIGRATED_FROM;
    final TableField<HistoryRecord, String> MIGRATED_TO;
    final TableField<HistoryRecord, String> MIGRATED_TO_TAGS;
    final TableField<HistoryRecord, Long> MIGRATION_TIME;
    final TableField<HistoryRecord, String> JOOQ_VERSION;
    final TableField<HistoryRecord, String> SQL;
    final TableField<HistoryRecord, Integer> SQL_COUNT;
    final TableField<HistoryRecord, HistoryStatus> STATUS;
    final TableField<HistoryRecord, String> STATUS_MESSAGE;
    final TableField<HistoryRecord, HistoryResolution> RESOLUTION;
    final TableField<HistoryRecord, String> RESOLUTION_MESSAGE;

    @Override // org.jooq.impl.TableImpl, org.jooq.impl.AbstractTable, org.jooq.Table
    public /* bridge */ /* synthetic */ Table as(Table table) {
        return as((Table<?>) table);
    }

    @Override // org.jooq.impl.TableImpl, org.jooq.RecordQualifier
    public Class<HistoryRecord> getRecordType() {
        return HistoryRecord.class;
    }

    private History(Name alias, Table<HistoryRecord> aliased) {
        this(alias, aliased, (Field[]) null, null);
    }

    private History(Name alias, Table<HistoryRecord> aliased, Field<?>[] parameters, Condition where) {
        super(alias, (Schema) null, aliased, parameters, DSL.comment("The migration history of jOOQ Migrations."), TableOptions.table(), where);
        this.ID = createField(DSL.name("ID"), SQLDataType.INTEGER.nullable(false).identity(true), this, "The database version ID.");
        this.MIGRATED_AT = createField(DSL.name("MIGRATED_AT"), SQLDataType.TIMESTAMP(6).nullable(false), this, "The date/time when the database version was migrated to.");
        this.MIGRATED_FROM = createField(DSL.name("MIGRATED_FROM"), SQLDataType.VARCHAR(255).nullable(false), this, "The previous database version ID.");
        this.MIGRATED_TO = createField(DSL.name("MIGRATED_TO"), SQLDataType.VARCHAR(255).nullable(false), this, "The current database version ID.");
        this.MIGRATED_TO_TAGS = createField(DSL.name("MIGRATED_TO_TAGS"), SQLDataType.CLOB.nullable(false), this, "The current database version tags, if any, in JSON array format.");
        this.MIGRATION_TIME = createField(DSL.name("MIGRATION_TIME"), SQLDataType.BIGINT, this, "The time in milliseconds it took to migrate to this database version.");
        this.JOOQ_VERSION = createField(DSL.name("JOOQ_VERSION"), SQLDataType.VARCHAR(50).nullable(false), this, "The jOOQ version used to migrate to this database version.");
        this.SQL = createField(DSL.name("SQL"), SQLDataType.CLOB, this, "The SQL statements that were run to install this database version.");
        this.SQL_COUNT = createField(DSL.name("SQL_COUNT"), SQLDataType.INTEGER.nullable(false), this, "The number of SQL statements that were run to install this database version.");
        this.STATUS = createField(DSL.name("STATUS"), SQLDataType.VARCHAR(10).nullable(false).asEnumDataType(HistoryStatus.class), this, "The database version installation status.");
        this.STATUS_MESSAGE = createField(DSL.name("STATUS_MESSAGE"), SQLDataType.CLOB, this, "Any info or error message explaining the status.");
        this.RESOLUTION = createField(DSL.name("RESOLUTION"), SQLDataType.VARCHAR(10).asEnumDataType(HistoryResolution.class), this, "The error resolution, if any.");
        this.RESOLUTION_MESSAGE = createField(DSL.name("RESOLUTION_MESSAGE"), SQLDataType.CLOB, this, "Any info or error message explaining the resolution.");
    }

    History(String alias) {
        this(DSL.name(alias), HISTORY);
    }

    History(Name alias) {
        this(alias, HISTORY);
    }

    History() {
        this(DSL.name("JOOQ_MIGRATION_HISTORY"), null);
    }

    @Override // org.jooq.impl.TableImpl, org.jooq.impl.AbstractTable, org.jooq.Table
    public Identity<HistoryRecord, Integer> getIdentity() {
        return super.getIdentity();
    }

    @Override // org.jooq.impl.TableImpl, org.jooq.impl.AbstractTable, org.jooq.Table
    public UniqueKey<HistoryRecord> getPrimaryKey() {
        return Internal.createUniqueKey((Table) HISTORY, DSL.name("JOOQ_MIGR_HIST_PK"), new TableField[]{HISTORY.ID}, true);
    }

    @Override // org.jooq.impl.TableImpl, org.jooq.impl.AbstractTable, org.jooq.Table
    public List<Check<HistoryRecord>> getChecks() {
        return Arrays.asList(Internal.createCheck(this, DSL.name("JOOQ_MIGR_HIST_CHK1"), "\"STATUS\" IN('STARTING', 'REVERTING', 'MIGRATING', 'SUCCESS', 'FAILURE')", true), Internal.createCheck(this, DSL.name("JOOQ_MIGR_HIST_CHK2"), "\"RESOLUTION\" IN('OPEN', 'RESOLVED', 'IGNORED')", true));
    }

    @Override // org.jooq.impl.AbstractTable, org.jooq.Table, org.jooq.SelectField
    public History as(String alias) {
        return new History(DSL.name(alias), this);
    }

    @Override // org.jooq.impl.TableImpl, org.jooq.impl.AbstractTable, org.jooq.Table, org.jooq.SelectField
    public History as(Name alias) {
        return new History(alias, this);
    }

    @Override // org.jooq.impl.TableImpl, org.jooq.impl.AbstractTable, org.jooq.Table
    public History as(Table<?> alias) {
        return new History(alias.getQualifiedName(), this);
    }
}
