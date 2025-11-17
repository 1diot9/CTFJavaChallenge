package org.jooq.impl;

import java.util.function.Supplier;
import org.jooq.Configuration;
import org.jooq.DiagnosticsListener;
import org.jooq.DiagnosticsListenerProvider;
import org.jooq.ExecuteListener;
import org.jooq.ExecuteListenerProvider;
import org.jooq.MigrationListener;
import org.jooq.MigrationListenerProvider;
import org.jooq.RecordListener;
import org.jooq.RecordListenerProvider;
import org.jooq.TransactionListener;
import org.jooq.TransactionListenerProvider;
import org.jooq.VisitListener;
import org.jooq.VisitListenerProvider;
import org.jooq.exception.DataAccessException;
import org.jooq.tools.JooqLogger;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AbstractConfiguration.class */
public abstract class AbstractConfiguration implements Configuration {
    private static final JooqLogger log = JooqLogger.getLogger((Class<?>) AbstractConfiguration.class);

    @Override // org.jooq.Configuration
    public final Configuration set(RecordListener... newRecordListeners) {
        return set(DefaultRecordListenerProvider.providers(newRecordListeners));
    }

    @Override // org.jooq.Configuration
    public final Configuration setAppending(RecordListener... newRecordListeners) {
        return setAppending(DefaultRecordListenerProvider.providers(newRecordListeners));
    }

    @Override // org.jooq.Configuration
    public final Configuration setAppending(RecordListenerProvider... newRecordListenerProviders) {
        return set((RecordListenerProvider[]) Tools.combine((Object[]) recordListenerProviders(), (Object[]) newRecordListenerProviders));
    }

    @Override // org.jooq.Configuration
    public final Configuration set(ExecuteListener... newExecuteListeners) {
        return set(DefaultExecuteListenerProvider.providers(newExecuteListeners));
    }

    @Override // org.jooq.Configuration
    public final Configuration setAppending(ExecuteListener... newExecuteListeners) {
        return setAppending(DefaultExecuteListenerProvider.providers(newExecuteListeners));
    }

    @Override // org.jooq.Configuration
    public final Configuration setAppending(ExecuteListenerProvider... newExecuteListenerProviders) {
        return set((ExecuteListenerProvider[]) Tools.combine((Object[]) executeListenerProviders(), (Object[]) newExecuteListenerProviders));
    }

    @Override // org.jooq.Configuration
    public final Configuration set(MigrationListener... newMigrationListeners) {
        return set(DefaultMigrationListenerProvider.providers(newMigrationListeners));
    }

    @Override // org.jooq.Configuration
    public final Configuration setAppending(MigrationListener... newMigrationListeners) {
        return setAppending(DefaultMigrationListenerProvider.providers(newMigrationListeners));
    }

    @Override // org.jooq.Configuration
    public final Configuration setAppending(MigrationListenerProvider... newMigrationListenerProviders) {
        return set((MigrationListenerProvider[]) Tools.combine((Object[]) migrationListenerProviders(), (Object[]) newMigrationListenerProviders));
    }

    @Override // org.jooq.Configuration
    public final Configuration set(VisitListener... newVisitListeners) {
        return set(DefaultVisitListenerProvider.providers(newVisitListeners));
    }

    @Override // org.jooq.Configuration
    public final Configuration setAppending(VisitListener... newVisitListeners) {
        return setAppending(DefaultVisitListenerProvider.providers(newVisitListeners));
    }

    @Override // org.jooq.Configuration
    public final Configuration setAppending(VisitListenerProvider... newVisitListenerProviders) {
        return set((VisitListenerProvider[]) Tools.combine((Object[]) visitListenerProviders(), (Object[]) newVisitListenerProviders));
    }

    @Override // org.jooq.Configuration
    public final Configuration set(TransactionListener... newTransactionListeners) {
        return set(DefaultTransactionListenerProvider.providers(newTransactionListeners));
    }

    @Override // org.jooq.Configuration
    public final Configuration setAppending(TransactionListener... newTransactionListeners) {
        return setAppending(DefaultTransactionListenerProvider.providers(newTransactionListeners));
    }

    @Override // org.jooq.Configuration
    public final Configuration setAppending(TransactionListenerProvider... newTransactionListenerProviders) {
        return set((TransactionListenerProvider[]) Tools.combine((Object[]) transactionListenerProviders(), (Object[]) newTransactionListenerProviders));
    }

    @Override // org.jooq.Configuration
    public final Configuration set(DiagnosticsListener... newDiagnosticsListeners) {
        return set(DefaultDiagnosticsListenerProvider.providers(newDiagnosticsListeners));
    }

    @Override // org.jooq.Configuration
    public final Configuration setAppending(DiagnosticsListener... newDiagnosticsListeners) {
        return setAppending(DefaultDiagnosticsListenerProvider.providers(newDiagnosticsListeners));
    }

    @Override // org.jooq.Configuration
    public final Configuration setAppending(DiagnosticsListenerProvider... newDiagnosticsListenerProviders) {
        return set((DiagnosticsListenerProvider[]) Tools.combine((Object[]) diagnosticsListenerProviders(), (Object[]) newDiagnosticsListenerProviders));
    }

    @Override // org.jooq.Configuration
    public final Configuration derive(RecordListener... newRecordListeners) {
        return derive(DefaultRecordListenerProvider.providers(newRecordListeners));
    }

    @Override // org.jooq.Configuration
    public final Configuration deriveAppending(RecordListener... newRecordListeners) {
        return deriveAppending(DefaultRecordListenerProvider.providers(newRecordListeners));
    }

    @Override // org.jooq.Configuration
    public final Configuration deriveAppending(RecordListenerProvider... newRecordListenerProviders) {
        return derive((RecordListenerProvider[]) Tools.combine((Object[]) recordListenerProviders(), (Object[]) newRecordListenerProviders));
    }

    @Override // org.jooq.Configuration
    public final Configuration derive(ExecuteListener... newExecuteListeners) {
        return derive(DefaultExecuteListenerProvider.providers(newExecuteListeners));
    }

    @Override // org.jooq.Configuration
    public final Configuration deriveAppending(ExecuteListener... newExecuteListeners) {
        return deriveAppending(DefaultExecuteListenerProvider.providers(newExecuteListeners));
    }

    @Override // org.jooq.Configuration
    public final Configuration deriveAppending(ExecuteListenerProvider... newExecuteListenerProviders) {
        return derive((ExecuteListenerProvider[]) Tools.combine((Object[]) executeListenerProviders(), (Object[]) newExecuteListenerProviders));
    }

    @Override // org.jooq.Configuration
    public final Configuration derive(MigrationListener... newMigrationListeners) {
        return derive(DefaultMigrationListenerProvider.providers(newMigrationListeners));
    }

    @Override // org.jooq.Configuration
    public final Configuration deriveAppending(MigrationListener... newMigrationListeners) {
        return deriveAppending(DefaultMigrationListenerProvider.providers(newMigrationListeners));
    }

    @Override // org.jooq.Configuration
    public final Configuration deriveAppending(MigrationListenerProvider... newMigrationListenerProviders) {
        return derive((MigrationListenerProvider[]) Tools.combine((Object[]) migrationListenerProviders(), (Object[]) newMigrationListenerProviders));
    }

    @Override // org.jooq.Configuration
    public final Configuration derive(VisitListener... newVisitListeners) {
        return derive(DefaultVisitListenerProvider.providers(newVisitListeners));
    }

    @Override // org.jooq.Configuration
    public final Configuration deriveAppending(VisitListener... newVisitListeners) {
        return deriveAppending(DefaultVisitListenerProvider.providers(newVisitListeners));
    }

    @Override // org.jooq.Configuration
    public final Configuration deriveAppending(VisitListenerProvider... newVisitListenerProviders) {
        return derive((VisitListenerProvider[]) Tools.combine((Object[]) visitListenerProviders(), (Object[]) newVisitListenerProviders));
    }

    @Override // org.jooq.Configuration
    public final Configuration derive(TransactionListener... newTransactionListeners) {
        return derive(DefaultTransactionListenerProvider.providers(newTransactionListeners));
    }

    @Override // org.jooq.Configuration
    public final Configuration deriveAppending(TransactionListener... newTransactionListeners) {
        return deriveAppending(DefaultTransactionListenerProvider.providers(newTransactionListeners));
    }

    @Override // org.jooq.Configuration
    public final Configuration deriveAppending(TransactionListenerProvider... newTransactionListenerProviders) {
        return derive((TransactionListenerProvider[]) Tools.combine((Object[]) transactionListenerProviders(), (Object[]) newTransactionListenerProviders));
    }

    @Override // org.jooq.Configuration
    public final Configuration derive(DiagnosticsListener... newDiagnosticsListeners) {
        return derive(DefaultDiagnosticsListenerProvider.providers(newDiagnosticsListeners));
    }

    @Override // org.jooq.Configuration
    public final Configuration deriveAppending(DiagnosticsListener... newDiagnosticsListeners) {
        return deriveAppending(DefaultDiagnosticsListenerProvider.providers(newDiagnosticsListeners));
    }

    @Override // org.jooq.Configuration
    public final Configuration deriveAppending(DiagnosticsListenerProvider... newDiagnosticsListenerProviders) {
        return derive((DiagnosticsListenerProvider[]) Tools.combine((Object[]) diagnosticsListenerProviders(), (Object[]) newDiagnosticsListenerProviders));
    }

    @Override // org.jooq.Configuration
    public boolean commercial(Supplier<String> logMessage) {
        if (commercial()) {
            return true;
        }
        log.warn(logMessage.get());
        return false;
    }

    @Override // org.jooq.Configuration
    public boolean requireCommercial(Supplier<String> logMessage) throws DataAccessException {
        if (commercial()) {
            return true;
        }
        throw new DataAccessException(logMessage.get());
    }
}
