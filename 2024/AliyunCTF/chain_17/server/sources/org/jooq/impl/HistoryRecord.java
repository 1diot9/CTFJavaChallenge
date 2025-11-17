package org.jooq.impl;

import java.sql.Timestamp;
import org.jooq.Record1;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/HistoryRecord.class */
class HistoryRecord extends UpdatableRecordImpl<HistoryRecord> {
    private static final long serialVersionUID = 1;

    HistoryRecord setId(Integer value) {
        set(0, value);
        return this;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Integer getId() {
        return (Integer) get(0);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public HistoryRecord setMigratedAt(Timestamp value) {
        set(1, value);
        return this;
    }

    Timestamp getMigratedAt() {
        return (Timestamp) get(1);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public HistoryRecord setMigratedFrom(String value) {
        set(2, value);
        return this;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getMigratedFrom() {
        return (String) get(2);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public HistoryRecord setMigratedTo(String value) {
        set(3, value);
        return this;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getMigratedTo() {
        return (String) get(3);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public HistoryRecord setMigratedToTags(String value) {
        set(4, value);
        return this;
    }

    String getMigratedToTags() {
        return (String) get(4);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public HistoryRecord setMigrationTime(Long value) {
        set(5, value);
        return this;
    }

    Long getMigrationTime() {
        return (Long) get(5);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public HistoryRecord setJooqVersion(String value) {
        set(6, value);
        return this;
    }

    String getJooqVersion() {
        return (String) get(6);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public HistoryRecord setSql(String value) {
        set(7, value);
        return this;
    }

    String getSql() {
        return (String) get(7);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public HistoryRecord setSqlCount(Integer value) {
        set(8, value);
        return this;
    }

    Integer getSqlCount() {
        return (Integer) get(8);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public HistoryRecord setStatus(HistoryStatus value) {
        set(9, value);
        return this;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public HistoryStatus getStatus() {
        return (HistoryStatus) get(9);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public HistoryRecord setStatusMessage(String value) {
        set(10, value);
        return this;
    }

    String getStatusMessage() {
        return (String) get(10);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public HistoryRecord setResolution(HistoryResolution value) {
        set(11, value);
        return this;
    }

    HistoryResolution getResolution() {
        return (HistoryResolution) get(11);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public HistoryRecord setResolutionMessage(String value) {
        set(12, value);
        return this;
    }

    String getResolutionMessage() {
        return (String) get(12);
    }

    @Override // org.jooq.impl.UpdatableRecordImpl, org.jooq.UpdatableRecord
    public Record1<Integer> key() {
        return (Record1) super.key();
    }

    HistoryRecord() {
        super(History.HISTORY);
    }

    HistoryRecord(Integer id, Timestamp migratedAt, String migratedFrom, String migratedTo, String migratedToTags, Long migrationTime, String jooqVersion, String sql, Integer sqlCount, HistoryStatus status, String statusMessage, HistoryResolution resolution, String resolutionMessage) {
        super(History.HISTORY);
        setId(id);
        setMigratedAt(migratedAt);
        setMigratedFrom(migratedFrom);
        setMigratedTo(migratedTo);
        setMigratedToTags(migratedToTags);
        setMigrationTime(migrationTime);
        setJooqVersion(jooqVersion);
        setSql(sql);
        setSqlCount(sqlCount);
        setStatus(status);
        setStatusMessage(statusMessage);
        setResolution(resolution);
        setResolutionMessage(resolutionMessage);
        resetChangedOnNotNull();
    }
}
