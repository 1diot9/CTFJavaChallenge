package org.springframework.boot.autoconfigure.transaction;

import org.springframework.transaction.PlatformTransactionManager;

@FunctionalInterface
@Deprecated(since = "3.2.0", forRemoval = true)
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/transaction/PlatformTransactionManagerCustomizer.class */
public interface PlatformTransactionManagerCustomizer<T extends PlatformTransactionManager> extends TransactionManagerCustomizer<T> {
}
