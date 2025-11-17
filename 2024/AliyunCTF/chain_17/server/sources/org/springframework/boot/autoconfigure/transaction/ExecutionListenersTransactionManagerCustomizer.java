package org.springframework.boot.autoconfigure.transaction;

import java.util.List;
import java.util.Objects;
import org.springframework.transaction.ConfigurableTransactionManager;
import org.springframework.transaction.TransactionExecutionListener;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/transaction/ExecutionListenersTransactionManagerCustomizer.class */
class ExecutionListenersTransactionManagerCustomizer implements TransactionManagerCustomizer<ConfigurableTransactionManager> {
    private final List<TransactionExecutionListener> listeners;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ExecutionListenersTransactionManagerCustomizer(List<TransactionExecutionListener> listeners) {
        this.listeners = listeners;
    }

    @Override // org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizer
    public void customize(ConfigurableTransactionManager transactionManager) {
        List<TransactionExecutionListener> list = this.listeners;
        Objects.requireNonNull(transactionManager);
        list.forEach(transactionManager::addListener);
    }
}
