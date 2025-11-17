package org.springframework.boot.autoconfigure.transaction;

import org.springframework.transaction.TransactionManager;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/transaction/TransactionManagerCustomizer.class */
public interface TransactionManagerCustomizer<T extends TransactionManager> {
    void customize(T transactionManager);
}
