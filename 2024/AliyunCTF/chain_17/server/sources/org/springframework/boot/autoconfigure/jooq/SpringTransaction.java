package org.springframework.boot.autoconfigure.jooq;

import org.jooq.Transaction;
import org.springframework.transaction.TransactionStatus;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jooq/SpringTransaction.class */
class SpringTransaction implements Transaction {
    private final TransactionStatus transactionStatus;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SpringTransaction(TransactionStatus transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public TransactionStatus getTxStatus() {
        return this.transactionStatus;
    }
}
