package org.springframework.boot.orm.jpa.hibernate;

import jakarta.transaction.TransactionManager;
import jakarta.transaction.UserTransaction;
import org.hibernate.engine.transaction.jta.platform.internal.AbstractJtaPlatform;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/orm/jpa/hibernate/SpringJtaPlatform.class */
public class SpringJtaPlatform extends AbstractJtaPlatform {
    private static final long serialVersionUID = 1;
    private final JtaTransactionManager transactionManager;

    public SpringJtaPlatform(JtaTransactionManager transactionManager) {
        Assert.notNull(transactionManager, "TransactionManager must not be null");
        this.transactionManager = transactionManager;
    }

    protected TransactionManager locateTransactionManager() {
        return this.transactionManager.getTransactionManager();
    }

    protected UserTransaction locateUserTransaction() {
        return this.transactionManager.getUserTransaction();
    }
}
