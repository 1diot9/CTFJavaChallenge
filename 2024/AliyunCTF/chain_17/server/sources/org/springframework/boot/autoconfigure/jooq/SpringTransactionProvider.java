package org.springframework.boot.autoconfigure.jooq;

import org.jooq.TransactionContext;
import org.jooq.TransactionProvider;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/jooq/SpringTransactionProvider.class */
public class SpringTransactionProvider implements TransactionProvider {
    private final PlatformTransactionManager transactionManager;

    public SpringTransactionProvider(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Override // org.jooq.TransactionProvider
    public void begin(TransactionContext context) {
        TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionDefinition(6));
        context.transaction(new SpringTransaction(status));
    }

    @Override // org.jooq.TransactionProvider
    public void commit(TransactionContext ctx) {
        this.transactionManager.commit(getTransactionStatus(ctx));
    }

    @Override // org.jooq.TransactionProvider
    public void rollback(TransactionContext ctx) {
        this.transactionManager.rollback(getTransactionStatus(ctx));
    }

    private TransactionStatus getTransactionStatus(TransactionContext ctx) {
        SpringTransaction transaction = (SpringTransaction) ctx.transaction();
        return transaction.getTxStatus();
    }
}
