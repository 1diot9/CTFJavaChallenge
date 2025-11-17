package io.r2dbc.spi;

import java.time.Duration;
import org.reactivestreams.Publisher;

/* loaded from: server.jar:BOOT-INF/lib/r2dbc-spi-1.0.0.RELEASE.jar:io/r2dbc/spi/Connection.class */
public interface Connection extends Closeable {
    Publisher<Void> beginTransaction();

    Publisher<Void> beginTransaction(TransactionDefinition transactionDefinition);

    @Override // io.r2dbc.spi.Closeable
    Publisher<Void> close();

    Publisher<Void> commitTransaction();

    Batch createBatch();

    Publisher<Void> createSavepoint(String str);

    Statement createStatement(String str);

    boolean isAutoCommit();

    ConnectionMetadata getMetadata();

    IsolationLevel getTransactionIsolationLevel();

    Publisher<Void> releaseSavepoint(String str);

    Publisher<Void> rollbackTransaction();

    Publisher<Void> rollbackTransactionToSavepoint(String str);

    Publisher<Void> setAutoCommit(boolean z);

    Publisher<Void> setLockWaitTimeout(Duration duration);

    Publisher<Void> setStatementTimeout(Duration duration);

    Publisher<Void> setTransactionIsolationLevel(IsolationLevel isolationLevel);

    Publisher<Boolean> validate(ValidationDepth validationDepth);
}
